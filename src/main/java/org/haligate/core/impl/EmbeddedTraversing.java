package org.haligate.core.impl;

import java.io.IOException;
import java.util.Collections;

import org.haligate.core.*;

public class EmbeddedTraversing extends HttpTraversing
{
    private final Resource< ? > parentResource;

    EmbeddedTraversing( final Config config, final Resource< ? > parentResource, final Link link )
    {
        super( config, link, Collections.< String, Object >emptyMap( ) );
        this.parentResource = parentResource;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        return new EmbeddedTraversed( config, parentResource, link );
    }
}
