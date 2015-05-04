package org.haligate.core;

import java.io.IOException;
import java.net.URI;

import com.google.common.collect.ImmutableListMultimap;

public class EmbeddedTraversed extends BasicTraversed
{
    private final Resource< ? > parentResource;
    private final URI embeddedLink;

    EmbeddedTraversed( final Client client, final Resource< ? > parentResource, final URI embeddedLink )
    {
        super( client, ImmutableListMultimap.< String, String >of( ) );
        this.parentResource = parentResource;
        this.embeddedLink = embeddedLink;
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return parentResource.getEmbeddedResourceFor( embeddedLink );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return parentResource.getEmbeddedResourceFor( embeddedLink, contentType );
    }
}
