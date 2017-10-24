package org.haligate.core;

import java.io.UncheckedIOException;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface Traversing extends Traversed
{
    public Traversed get( ) throws UncheckedIOException;

    public Traversed post( final Object content ) throws UncheckedIOException;

    public Traversed put( final Object content ) throws UncheckedIOException;

    public Traversed delete( ) throws UncheckedIOException;

    public Link asLink( );

    public Traversing withHeader( String name, String value ) throws UncheckedIOException;

    public Traversing with( final String name, final Object value ) throws UncheckedIOException;

	public Traversing with( final Map< String, Object > parameters ) throws UncheckedIOException;
}
