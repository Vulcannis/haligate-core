package org.haligate.core;

import java.io.IOException;
import java.net.URI;

public class EmbeddedTraversing extends HttpTraversing
{
    private final Resource< ? > parentResource;
    private final URI embeddedUri;

    EmbeddedTraversing( final Client client, final Resource< ? > parentResource, final URI embeddedUri )
    {
        super( client, embeddedUri );
        this.parentResource = parentResource;
        this.embeddedUri = embeddedUri;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        return new EmbeddedTraversed( client, parentResource, embeddedUri );
    }
}
