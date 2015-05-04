package org.haligate.core;

import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;

public class Client
{
    protected final Supplier< CloseableHttpClient > httpClient;
    protected final Supplier< HttpContext > context;
    protected final Supplier< ObjectMapper > mapper;

    public Client( final Supplier< CloseableHttpClient > httpClient, final Supplier< HttpContext > contextSupplier, final Supplier< ObjectMapper > mapper )
    {
        this.httpClient = httpClient;
		this.context = contextSupplier;
		this.mapper = mapper;
    }

    public Traversing from( final URI root )
    {
        return new HttpTraversing( this, root );
    }

    public Traversing from( final Link link )
    {
        return new HttpTraversing( this, link.toUri( ) );
    }
}
