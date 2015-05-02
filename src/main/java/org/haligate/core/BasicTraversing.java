package org.haligate.core;

import java.io.IOException;
import java.util.*;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public abstract class BasicTraversing implements Traversing
{
    @Override
    public Traversing follow( final String... rels ) throws IOException
    {
        return get( ).follow( rels );
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return get( ).asResource( );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return get( ).asResource( contentType );
    }

    @Override
    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws IOException
    {
        return get( ).asResource( contentType );
    }

    @Override
    public < T > T asObject( final Class< T > contentType ) throws IOException
    {
        return get( ).asObject( contentType );
    }

    @Override
    public < T > T asObject( final TypeToken< T > contentType ) throws IOException
    {
        return get( ).asObject( contentType );
    }

    @Override
    public Traversing followHeader( final String header ) throws IOException
    {
        return get( ).followHeader( header );
    }

    @Override
    public Traversing followHeader( final String header, final Function< List< String >, String > disambiguator ) throws IOException
    {
        return get( ).followHeader( header, disambiguator );
    }

    @Override
    public Traversed with( final String name, final String value ) throws IOException
    {
        return get( ).with( name, value );
    }

    @Override
    public Traversed with( final Map< String, Object > parameters ) throws IOException
    {
    	return get( ).with( parameters );
    }

}
