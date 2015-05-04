package org.haligate.core.impl;

import java.io.IOException;
import java.net.URI;

import org.haligate.core.Resource;

import com.google.common.collect.ImmutableListMultimap;

public class EmbeddedTraversed extends BasicTraversed
{
    private final Resource< ? > parentResource;
    private final URI embeddedLink;

    EmbeddedTraversed( final Config config, final Resource< ? > parentResource, final URI embeddedLink )
    {
        super( config, ImmutableListMultimap.< String, String >of( ) );
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
