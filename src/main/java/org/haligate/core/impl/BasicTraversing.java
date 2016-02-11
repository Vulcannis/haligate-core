package org.haligate.core.impl;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.haligate.core.*;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public abstract class BasicTraversing implements Traversing
{
	protected final Link selectedLink;
	protected final Map< String, String > requestHeaders = Maps.newHashMap( );
    protected final Map< String, Object > parameters = Maps.newHashMap( );

	public BasicTraversing( final Link selectedLink )
	{
		this.selectedLink = selectedLink;
	}

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
    public Traversing followHeader( final String header, final Function< List< String >, URI > disambiguator ) throws IOException
    {
        return get( ).followHeader( header, disambiguator );
    }

    @Override
    public Traversing with( final String name, final Object value )
    {
        parameters.put( name, value );
        return this;
    }

    @Override
    public Traversing with( final Map< String, Object > parameters ) throws IOException
    {
    	this.parameters.putAll( parameters );
    	return this;
    }

    @Override
    public Traversing withHeader( final String name, final String value ) throws IOException
    {
    	requestHeaders.put( name, value );
    	return this;
    }
}
