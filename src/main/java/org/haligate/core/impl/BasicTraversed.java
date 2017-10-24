package org.haligate.core.impl;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.*;
import java.util.regex.*;

import org.haligate.core.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;

import reactor.core.publisher.*;
import reactor.util.function.Tuples;

public abstract class BasicTraversed implements Traversed
{
    private static final Pattern followRelPattern = Pattern.compile( "([^\\[]+)(?:\\[(?:(\\d+)|(?:([^:]+):([^\\]]+)))\\])?" );

    protected final Config config;
    protected final ListMultimap< String, String > headers;

    BasicTraversed( final Config config, final ListMultimap< String, String > headers )
    {
        this.config = config;
        this.headers = headers;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws UncheckedIOException
    {
        return (Resource< T >)asResource( contentType.getRawType( ) );
    }

    @Override
    public < T > T asObject( final Class< T > contentType ) throws UncheckedIOException
    {
        return asResource( contentType ).getBody( );
    }

    @Override
    public < T > T asObject( final TypeToken< T > contentType ) throws UncheckedIOException
    {
        return asResource( contentType ).getBody( );
    }

    @Override
    public Traversing follow( final String... rels ) throws UncheckedIOException
    {
        return follow( rels, 0 );
    }

    protected Traversing follow( final String[ ] rels, final int index ) throws UncheckedIOException
    {
        final Resource< ? > resource = asResource( );
        final Link selectedLink = determineLink( rels[ index ], resource );

        final Traversing traversing = followLink( resource, selectedLink );

        if( index < rels.length - 1 ) {
            return ( (BasicTraversed)traversing.get( ) ).follow( rels, index + 1 );
        } else {
            return traversing;
        }
    }

	private Traversing followLink( final Resource< ? > resource, final Link selectedLink )
	{
		final Traversing traversing;
        if( resource.hasEmbeddedResourceFor( selectedLink ) ) {
            traversing = new EmbeddedTraversing( config, resource, selectedLink );
        } else {
            traversing = new HttpTraversing( config, selectedLink );
        }
		return traversing;
	}

    @Override
	public Flux< Traversing > followMany( final String rel ) throws UncheckedIOException
	{
		return Mono
			.just( asResource( ) )
			.flatMapMany( r -> Flux.fromIterable( r.getLinks( ).get( rel ) ).map( x -> Tuples.of( r, x ) ) )
			.map( x -> followLink( x.getT1( ), x.getT2( ) ) )
			;
	}

    @Override
    public Traversing followHeader( final String header )
    {
        return followHeader( header, input -> {
		    if( input.size( ) != 1 ) {
		        throw new LinkResolutionException( "Cannot follow header '" + header + "', need exactly one value but was: " + input );
		    }
		    return URI.create( input.get( 0 ) );
		} );
    }

    @Override
    public Traversing followHeader( final String header, final Function< List< String >, URI > disambiguator )
    {
        return new HttpTraversing( config, Link.forUri( disambiguator.apply( headers.get( header ) ) ) );
    }

    private Link determineLink( final String rel, final Resource< ? > resource )
    {
        final Matcher matcher = followRelPattern.matcher( rel );
        if( !matcher.matches( ) ) {
            throw new LinkResolutionException( "Cannot follow relation '" + rel + "'" );
        }
        final String relName = matcher.group( 1 ),
        	secondaryIndex = matcher.group( 2 ),
        	secondaryKeyAttribute = matcher.group( 3 ),
        	secondaryKeyValue = matcher.group( 4 );
        final List< Link > locations = resource.getLinks( ).get( relName );
        final Link selectedLink;
        if( locations.size( ) == 0 ) {
            throw new LinkResolutionException( "Cannot follow, no links of relation '" + relName + "' found in resource " + resource );
        } else if( locations.size( ) > 1 ) {
            if( secondaryIndex != null ) {
                final int index = Integer.parseInt( secondaryIndex );
                if( index < 0 || index >= locations.size( ) ) {
                    throw new LinkResolutionException( "Cannot follow, index " + secondaryIndex + " given but only " + locations.size( )
                        + " links available in resource " + resource );
                }
                selectedLink = locations.get( index );
            } else if( secondaryKeyAttribute != null && secondaryKeyValue != null ) {
                final List< Link > matchingLinks = FluentIterable.from( locations )
                    .filter( new SecondaryKeyPredicate( secondaryKeyAttribute, secondaryKeyValue ) ).toList( );
                if( matchingLinks.isEmpty( ) ) {
                    throw new LinkResolutionException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue
                        + "] does not apply to any links in resource " + resource );
                } else if( matchingLinks.size( ) > 1 ) {
                    throw new LinkResolutionException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue + "] matches multiple("
                        + matchingLinks.size( ) + ") links in resource " + resource );
                } else {
                    selectedLink = matchingLinks.get( 0 );
                }
            } else {
                throw new LinkResolutionException( "Cannot follow, multiple links of relation '" + relName + "' found in resource " + resource
                    + " but no secondary key given" );
            }
        } else {
            selectedLink = locations.get( 0 );
            if( secondaryIndex != null && Integer.parseInt( secondaryIndex ) != 0 ) {
                throw new LinkResolutionException( "Cannot follow, index " + secondaryIndex + " given but only " + locations.size( )
                    + " links available in resource " + resource );
            } else if( secondaryKeyAttribute != null || secondaryKeyValue != null ) {
                if( !new SecondaryKeyPredicate( secondaryKeyAttribute, secondaryKeyValue ).apply( selectedLink ) ) {
                    throw new LinkResolutionException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue
                        + "] does not apply to any links in resource " + resource );
                }
            }
        }
        return selectedLink;
    }

    protected static class SecondaryKeyPredicate implements Predicate< Link >
    {
        private final String secondaryKeyAttribute;
        private final String secondaryKeyValue;

        private SecondaryKeyPredicate( final String secondaryKeyAttribute, final String secondaryKeyValue )
        {
            this.secondaryKeyAttribute = secondaryKeyAttribute;
            this.secondaryKeyValue = secondaryKeyValue;
        }

        @Override
        public boolean apply( final Link input )
        {
            final Map< String, String > properties = input.getProperties( );
            return properties.containsKey( secondaryKeyAttribute ) && properties.get( secondaryKeyAttribute ).equals( secondaryKeyValue );
        }
    }
}
