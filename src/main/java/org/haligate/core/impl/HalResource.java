package org.haligate.core.impl;

import static com.google.common.collect.Multimaps.unmodifiableListMultimap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.haligate.core.*;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;

public class HalResource< T > implements Resource< T >
{
    private final Config config;
    private final String text;
    private final T body;
    private final ListMultimap< String, Link > links = ArrayListMultimap.create( );
    private final Map< String, UriTemplate > curieTemplates = Maps.newHashMap( );
    private final Map< Link, JsonNode > embeddedResources = Maps.newHashMap( );
    private final ListMultimap< String, String > headers;

    protected HalResource( final Config config, final String text, final Class< T > type, final ListMultimap< String, String > headers  ) throws IOException
    {
        this.config = config;
        this.text = text;
        this.headers = unmodifiableListMultimap( headers );
        final ObjectMapper mapper = config.mapper.get( ).copy( );
        final JsonNode root = mapper.readTree( text );
        readCurries( curieTemplates, mapper, root );
        body = init( config, mapper, root, type, curieTemplates, links, embeddedResources );
    }

    protected HalResource( final Config config, final JsonNode root, final Class< T > type, final Map< String, UriTemplate > curieTemplates ) throws IOException
    {
        this.config = config;
        this.text = root.toString( );
        this.headers = ImmutableListMultimap.of( );
        this.curieTemplates.putAll( curieTemplates );
        final ObjectMapper mapper = config.mapper.get( ).copy( );
        body = init( config, mapper, root, type, curieTemplates, links, embeddedResources );
    }

    private static < T > T init( final Config config, final ObjectMapper mapper, final JsonNode root, final Class< T > type, final Map< String, UriTemplate > curieTemplates, final ListMultimap< String, Link > links, final Map< Link, JsonNode > embeddedResources ) throws IOException
    {
        readLinks( mapper, root, curieTemplates, links );
        readEmbeds( config, mapper, root, curieTemplates, links, embeddedResources );
        if( type == Void.class ) {
            return null;
        } else {
            mapper.addMixIn( type, IgnoreHalProperties.class );
            return mapper.treeToValue( root, type );
        }
    }

    private static void readCurries( final Map< String, UriTemplate > curieTemplates, final ObjectMapper mapper, final JsonNode root )
    {
        for( final JsonNode curie: root.path( "_links" ).path( "curies" ) ) {
            final String href = curie.get( "href" ).asText( );
            final String name = curie.get( "name" ).asText( );
            curieTemplates.put( name, UriTemplate.fromTemplate( href ) );
        }
    }

    private static void readLinks( final ObjectMapper mapper, final JsonNode root, final Map< String, UriTemplate > curieTemplates, final ListMultimap< String, Link > links ) throws IOException
    {
        for( final Iterator< Map.Entry< String, JsonNode > > it = root.path( "_links" ).fields( ); it.hasNext( ); ) {
            final Entry< String, JsonNode > entry = it.next( );
            if( entry.getKey( ).equals( "curies" ) ) {
                continue;
            }
            final String rel = parseRel( curieTemplates, entry.getKey( ) );
            final Iterable< JsonNode > nodes;
            if( entry.getValue( ).isArray( ) ) {
                nodes = entry.getValue( );
            } else {
                nodes = asList( entry.getValue( ) );
            }
            for( final JsonNode element: nodes ) {
                final Link link = mapper.treeToValue( element, Link.class );
                links.put( rel, link );
            }
        }
    }

    private static void readEmbeds( final Config config, final ObjectMapper mapper, final JsonNode root, final Map< String, UriTemplate > curieTemplates, final ListMultimap< String, Link > links, final Map< Link, JsonNode > embeddedResources ) throws IOException
    {
        for( final Iterator< Map.Entry< String, JsonNode > > it = root.path( "_embedded" ).fields( ); it.hasNext( ); ) {
            final Entry< String, JsonNode > entry = it.next( );
            final String rel = parseRel( curieTemplates, entry.getKey( ) );
            final Iterable< JsonNode > nodes;
            if( entry.getValue( ).isArray( ) ) {
                nodes = entry.getValue( );
            } else {
                nodes = asList( entry.getValue( ) );
            }
            for( final JsonNode resource: nodes ) {
                final JsonNode selfLink = resource.path( "_links" ).path( "self" );
                final Link link = mapper.treeToValue( selfLink, Link.class );
                if( config.includeEmbeddedLinks ) {
                	links.put( rel, link );
                }
                embeddedResources.put( link, resource );
            }
        }
    }

    private static String parseRel( final Map< String, UriTemplate > curieTemplates, final String rel )
    {
        final int index = rel.indexOf( ':' );
        if( index == -1 ) {
            return rel;
        } else {
            final String name = rel.substring( 0, index ), value = rel.substring( index + 1 );
            return curieTemplates.get( name ).expand( singletonMap( "rel", (Object)value ) );
        }
    }

    @Override
    public Link getSelfLink( )
    {
        return Iterables.getOnlyElement( links.get( "self" ) );
    }

    @Override
    public T getBody( )
    {
        return body;
    }

    @Override
    public ListMultimap< String, Link > getLinks( )
    {
        return Multimaps.unmodifiableListMultimap( links );
    }

    @Override
    public boolean hasEmbeddedResourceFor( final Link link )
    {
        return embeddedResources.containsKey( link );
    }

    @Override
    public Resource< ? > getEmbeddedResourceFor( final Link link ) throws IOException
    {
        return getEmbeddedResourceFor( link, Void.class );
    }

    @Override
    public < S > Resource< S > getEmbeddedResourceFor( final Link link, final Class< S > type ) throws IOException
    {
        final JsonNode resourceNode = embeddedResources.get( link );
        if( resourceNode == null ) {
            return null;
        } else {
            return new HalResource< S >( config, resourceNode, type, curieTemplates );
        }
    }

    @SuppressWarnings( "unchecked" )
	@Override
    public < S > Resource< S > getEmbeddedResourceFor( final Link link, final TypeToken< S > type ) throws IOException
    {
    	return (Resource< S >)getEmbeddedResourceFor( link, type.getRawType( ) );
    }

    @Override
    public ListMultimap< String, String > getHeaders( )
    {
        return headers;
    }

    @Override
    public String toString( )
    {
        if( body == null ) {
            return text;
        } else {
            return body.toString( );
        }
    }

    @JsonIgnoreProperties( { "_links", "_embedded" } )
    private static class IgnoreHalProperties
    {
    }
}
