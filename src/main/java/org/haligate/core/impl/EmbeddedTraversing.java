package org.haligate.core.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.haligate.core.*;

public class EmbeddedTraversing extends HttpTraversing
{
    private final Resource< ? > parentResource;
    private final URI embeddedUri;

    EmbeddedTraversing( final Config config, final Resource< ? > parentResource, final URI embeddedUri )
    {
        super( config, embeddedUri, Collections.< String, String >emptyMap( ) );
        this.parentResource = parentResource;
        this.embeddedUri = embeddedUri;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        return new EmbeddedTraversed( config, parentResource, embeddedUri );
    }
}
