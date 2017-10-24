package org.haligate.core;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.*;

import org.osgi.annotation.versioning.ProviderType;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

import reactor.core.publisher.Flux;

@ProviderType
public interface Traversed
{
    public Resource< ? > asResource( ) throws UncheckedIOException;

    public < T > Resource< T > asResource( final Class< T > contentType ) throws UncheckedIOException;

    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws UncheckedIOException;

    public < T > T asObject( final Class< T > contentType ) throws UncheckedIOException;

    public < T > T asObject( final TypeToken< T > contentType ) throws UncheckedIOException;

    @SuppressWarnings( "serial" )
	public default String getProperty( final String name ) throws UncheckedIOException
    {
    	return asObject( new TypeToken< Map< String, String > >( ) { } ).get( name );
    }

    public Traversing follow( final String... rels ) throws UncheckedIOException;

    public Traversing followHeader( final String header ) throws UncheckedIOException;

    public Traversing followHeader( final String header, final Function< List< String >, URI > disambiguator ) throws UncheckedIOException;

	public Flux< Traversing > followMany( final String rel ) throws UncheckedIOException;
}
