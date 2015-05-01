package org.haligate.core;

import java.io.IOException;
import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Supplier;

public class EmbeddedTraversing extends HttpTraversing
{
    private final Resource< ? > parentResource;
    private final URI embeddedUri;

    EmbeddedTraversing( final CloseableHttpClient httpClient, final Supplier< HttpContext > context, final Resource< ? > parentResource, final URI embeddedUri )
    {
        super( httpClient, context, embeddedUri );
        this.parentResource = parentResource;
        this.embeddedUri = embeddedUri;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        return new EmbeddedTraversed( httpClient, context, parentResource, embeddedUri );
    }
}
