package org.haligate.core.impl;

import java.util.function.Supplier;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Config
{
    protected final Supplier< CloseableHttpClient > httpClient;
    protected final Supplier< HttpContext > context;
    protected final Supplier< ObjectMapper > mapper;
	protected final boolean includeEmbeddedLinks;

    public Config( final Supplier< CloseableHttpClient > httpClient, final Supplier< HttpContext > context, final Supplier< ObjectMapper > mapper, final boolean includeEmbeddedLinks )
    {
        this.httpClient = httpClient;
        this.context = context;
        this.mapper = mapper;
		this.includeEmbeddedLinks = includeEmbeddedLinks;
    }
}
