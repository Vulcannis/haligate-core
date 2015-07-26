package org.haligate.core;

import java.io.IOException;

import com.google.common.collect.ListMultimap;

public interface Resource< T >
{
    public Link getSelfLink( );

    public T getBody( );

    public ListMultimap< String, Link > getLinks( );

    public boolean hasEmbeddedResourceFor( final Link link );

    public Resource< ? > getEmbeddedResourceFor( final Link link ) throws IOException;

    public < S > Resource< S > getEmbeddedResourceFor( final Link link, final Class< S > type ) throws IOException;

    public ListMultimap< String, String > getHeaders( );
}
