package org.haligate.core;

import java.io.IOException;
import java.net.URI;

import com.google.common.collect.ListMultimap;

public interface Resource< T >
{
    
    Link getSelfLink( );
    
    T getBody( );
    
    ListMultimap< String, Link > getLinks( );
    
    boolean hasEmbeddedResourceFor( URI uri );
    
    Resource< ? > getEmbeddedResourceFor( URI uri ) throws IOException;
    
    < S > Resource< S > getEmbeddedResourceFor( URI uri, Class< S > type ) throws IOException;
    
    ListMultimap< String, String > getHeaders( );
    
}
