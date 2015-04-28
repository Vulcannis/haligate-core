package org.haligate.core;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

public class Client
{
    private final CloseableHttpClient httpClient;
	private final HttpContext context;

    public Client( final CloseableHttpClient httpClient, final HttpContext context  )
    {
        this.httpClient = httpClient;
		this.context = context;
    }

    public Traversal from( final URI root )
    {
        return new RetrievingTraversal( httpClient, context, root );
    }

    public Traversal from( final Link link )
    {
        return new RetrievingTraversal( httpClient, context, link.toUri( ) );
    }
}
