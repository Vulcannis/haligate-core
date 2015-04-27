package org.haligate.core;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.*;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;

public class Traversal
{
    private static final Pattern followRelPattern = Pattern.compile( "([^\\[]+)(?:\\[(?:(\\d+)|(?:([^:]+):([^\\]]+)))\\])?" );

    private final URI currentLocation;
    private final CloseableHttpClient httpClient;
	private final HttpContext context;
	private final Map< String, Object > parameters = Maps.newHashMap( );

    protected Traversal( final CloseableHttpClient httpClient, final HttpContext context , final URI root )
    {
        this.httpClient = httpClient;
		this.context = context;
        currentLocation = root;
    }

	public Traversal with( final String param, final String value )
	{
		parameters.put( param, value );
		return this;
	}

	public Traversal with( final String param, final Collection< String > value )
	{
		parameters.put( param, value );
		return this;
	}

	public Traversal with( final Map< String, Object > values )
	{
		parameters.putAll( values );
		return this;
	}

	public Traversal follow( final String... rels ) throws IOException
    {
        Traversal traversor = this;
        for( final String rel: rels ) {
            final Matcher matcher = followRelPattern.matcher( rel );
            if( !matcher.matches( ) ) {
                throw new IllegalArgumentException( "Cannot follow relation '" + rel + "'" );
            }
            final String relName = matcher.group( 1 ), secondaryIndex = matcher.group( 2 ), secondaryKeyAttribute = matcher.group( 3 ), secondaryKeyValue = matcher.group( 4 );
            final Resource< ? > resource = traversor.asResource( );
            final List< Link > locations = resource.getLinks( ).get( relName );
            final Link selectedLink;
            if( locations.size( ) == 0 ) {
                throw new IllegalStateException( "Cannot follow, no links of relation '" + relName + "' found in resource " + resource );
            } else if( locations.size( ) > 1 ) {
                if( secondaryIndex != null ) {
                	final int index = Integer.parseInt( secondaryIndex );
                	if( index < 0 || index >= locations.size( ) ) {
                		throw new IllegalStateException( "Cannot follow, index " + secondaryIndex + " given but only " + locations.size( ) + " links available in resource " + resource );
                	}
                    selectedLink = locations.get( index );
                } else if( secondaryKeyAttribute != null && secondaryKeyValue != null ) {
					final List< Link > matchingLinks = FluentIterable.from( locations ).filter( new SecondaryKeyPredicate( secondaryKeyAttribute, secondaryKeyValue ) ).toList( );
					if( matchingLinks.isEmpty( ) ) {
            			throw new IllegalStateException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue + "] does not apply to any links in resource " + resource );
					} else if( matchingLinks.size( ) > 1 ) {
            			throw new IllegalStateException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue + "] matches multiple(" + matchingLinks.size( ) + ") links in resource " + resource );
					} else {
						selectedLink = matchingLinks.get( 0 );
					}
                } else {
                    throw new IllegalStateException( "Cannot follow, multiple links of relation '" + relName + "' found in resource " + resource + " but no secondary key given" );
                }
            } else {
            	selectedLink = locations.get( 0 );
            	if( secondaryIndex != null && Integer.parseInt( secondaryIndex ) != 0 ) {
            		throw new IllegalStateException( "Cannot follow, index " + secondaryIndex + " given but only " + locations.size( ) + " links available in resource " + resource );
            	} else if( secondaryKeyAttribute != null || secondaryKeyValue != null ) {
            		if( !new SecondaryKeyPredicate( secondaryKeyAttribute, secondaryKeyValue ).apply( selectedLink ) ) {
            			throw new IllegalStateException( "Cannot follow, secondary key [" + secondaryKeyAttribute + ":" + secondaryKeyValue + "] does not apply to any links in resource " + resource );
            		}
            	}
            }
            traversor = new Traversal( httpClient, context, selectedLink.toUri( parameters ) );
        }
        return traversor;
    }

	public Resource< ? > asResource( ) throws IOException
	{
		return asResource( Void.class );
	}

    public < T > Resource< T > asResource( final Class< T > type ) throws IOException
    {
        final HttpGet request = new HttpGet( currentLocation );
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType );
        try( final CloseableHttpResponse response = httpClient.execute( request, context ) ) {
            final String content = EntityUtils.toString( response.getEntity( ) );
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + currentLocation + ": " + response );
            }
            return new Resource< T >( content, type );
        }
    }

    @SuppressWarnings( "unchecked" )
	public < T > Resource< T > asResource( final TypeToken< T > typeToken ) throws IOException
    {
    	return (Resource< T >)asResource( typeToken.getRawType( ) );
    }

    public Link asLink( ) throws IOException
    {
        return asResource( Void.class ).getSelfLink( );
    }

    public < T > T asObject( final Class< T > objectClass ) throws IOException
    {
    	return asResource( objectClass ).getBody( );
    }

	@SuppressWarnings( "unchecked" )
	public < T > T asObject( final TypeToken< T > typeToken ) throws IOException
	{
		return (T)asResource( typeToken.getRawType( ) ).getBody( );
	}

	private static class SecondaryKeyPredicate implements Predicate< Link >
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
