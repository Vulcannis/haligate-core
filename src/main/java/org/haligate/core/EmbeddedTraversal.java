package org.haligate.core;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

public class EmbeddedTraversal extends Traversal
{
    private final Resource< ? > resource;
    private final Link nextResource;

    public EmbeddedTraversal( final CloseableHttpClient httpClient, final HttpContext context, final Resource< ? > resource, final Link nextResource )
    {
        super( httpClient, context, resource.getSelfLink( ).toUri( ) );
        this.resource = resource;
        this.nextResource = nextResource;
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > type ) throws IOException
    {
        return resource.getEmbeddedResourceFor( nextResource.toUri( ), type );
    }
}
