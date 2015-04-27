package org.haligate.core;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.*;

public class Resource< T >
{
    private final T body;
    private final ListMultimap< String, Link > links;
    private final String content;

    protected Resource( final String content, final Class< T > type ) throws IOException
    {
        this.content = content;
        final ObjectMapper mapper = new ObjectMapper( );
        final JsonNode root = mapper.readTree( content );
        links = readLinks( mapper, root );
        if( type == Void.class ) {
            body = null;
        } else {
            mapper.addMixIn( type, IgnoreHalProperties.class );
            body = mapper.treeToValue( root, type );
        }
    }

    private static ListMultimap< String, Link > readLinks( final ObjectMapper mapper , final JsonNode root ) throws IOException
    {
        final ListMultimap< String, Link > links = ArrayListMultimap.create( );
        for( final Iterator< Map.Entry< String, JsonNode > > it = root.get( "_links" ).fields( ); it.hasNext( ); ) {
            final Entry< String, JsonNode > entry = it.next( );
            final String rel = entry.getKey( );
            if( entry.getValue( ).isArray( ) ) {
                for( final JsonNode element: entry.getValue( ) ) {
                    final Link link = mapper.treeToValue( element, Link.class );
                    links.put( rel, link );
                }
            } else {
                final Link link = mapper.treeToValue( entry.getValue( ), Link.class );
                links.put( rel, link );
            }
        }
        return links;
    }

    public Link getSelfLink( )
    {
        return Iterables.getOnlyElement( links.get( "self" ) );
    }

    public T getBody( )
    {
        return body;
    }

    public ListMultimap< String, Link > getLinks( )
    {
        return Multimaps.unmodifiableListMultimap( links );
    }

    @Override
    public String toString( )
    {
        if( body == null ) {
            return content;
        } else {
            return body.toString( );
        }
    }

    @JsonIgnoreProperties( { "_links", "_embedded" } )
    private static class IgnoreHalProperties
    {
    }
}
