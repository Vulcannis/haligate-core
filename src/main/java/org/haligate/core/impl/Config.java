package org.haligate.core.impl;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;

public class Config
{
    protected final Supplier< CloseableHttpClient > httpClient;
    protected final Supplier< HttpContext > context;
    protected final Supplier< ObjectMapper > mapper;

    public Config( final Supplier< CloseableHttpClient > httpClient, final Supplier< HttpContext > context, final Supplier< ObjectMapper > mapper )
    {
        this.httpClient = httpClient;
        this.context = context;
        this.mapper = mapper;
    }
}
