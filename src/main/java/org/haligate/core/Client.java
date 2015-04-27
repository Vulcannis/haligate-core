package org.haligate.core;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;

public class Client
{
    private final CloseableHttpClient httpClient;

    public Client( final CloseableHttpClient httpClient )
    {
        this.httpClient = httpClient;
    }

    public Traversal from( final URI root )
    {
        return new Traversal( httpClient, root );
    }

    public Traversal from( final Link link )
    {
        return new Traversal( httpClient, link.toUri( ) );
    }
}
