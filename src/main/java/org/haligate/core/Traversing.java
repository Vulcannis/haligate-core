package org.haligate.core;

import java.io.IOException;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface Traversing extends Traversed
{
    public Traversed get( ) throws IOException;

    public Traversed post( final Object content ) throws IOException;

    public Traversed put( final Object content ) throws IOException;

    public Traversed delete( ) throws IOException;

    public Link asLink( );

    public Traversing withHeader( String name, String value ) throws IOException;

    public Traversing with( final String name, final Object value ) throws IOException;

	public Traversing with( final Map< String, Object > parameters ) throws IOException;
}
