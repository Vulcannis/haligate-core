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

    public Traversing from( final URI root )
    {
        return new HttpTraversing( httpClient, context, root );
    }

    public Traversing from( final Link link )
    {
        return new HttpTraversing( httpClient, context, link.toUri( ) );
    }
}
