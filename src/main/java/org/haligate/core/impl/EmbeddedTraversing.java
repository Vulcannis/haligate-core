package org.haligate.core.impl;

import java.io.UncheckedIOException;

import org.haligate.core.*;

public class EmbeddedTraversing extends HttpTraversing
{
    private final Resource< ? > parentResource;

    EmbeddedTraversing( final Config config, final Resource< ? > parentResource, final Link selectedLink )
    {
        super( config, selectedLink );
        this.parentResource = parentResource;
    }

    @Override
    public Traversed get( ) throws UncheckedIOException
    {
        return new EmbeddedTraversed( config, parentResource, selectedLink );
    }
}
