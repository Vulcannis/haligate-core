package org.haligate.core;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Supplier;

public class Client
{
    private final CloseableHttpClient httpClient;
	private final Supplier< HttpContext > context;

    public Client( final CloseableHttpClient httpClient, final Supplier< HttpContext > contextSupplier  )
    {
        this.httpClient = httpClient;
		this.context = contextSupplier;
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
