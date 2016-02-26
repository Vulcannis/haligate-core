package org.haligate.core;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

@ProviderType
public interface Traversed
{
    public Resource< ? > asResource( ) throws IOException;

    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException;

    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws IOException;

    public < T > T asObject( final Class< T > contentType ) throws IOException;

    public < T > T asObject( final TypeToken< T > contentType ) throws IOException;

    public Traversing follow( final String... rels ) throws IOException;

    public Traversing followHeader( final String header ) throws IOException;

    public Traversing followHeader( final String header, final Function< List< String >, URI > disambiguator ) throws IOException;
}
