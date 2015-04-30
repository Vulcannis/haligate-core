package org.haligate.core;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

public interface Traversed
{
    public Resource< ? > asResource( ) throws IOException;

    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException;

    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws IOException;

    public < T > T asObject( final Class< T > contentType ) throws IOException;

    public < T > T asObject( final TypeToken< T > contentType ) throws IOException;

    public Traversing follow( final String... rels ) throws IOException;

    public Traversing followHeader( final String header ) throws IOException;

    public Traversing followHeader( final String header, final Function< List< String >, String > disambiguate ) throws IOException;

    public Traversed with( final String name, final String value ) throws IOException;
}