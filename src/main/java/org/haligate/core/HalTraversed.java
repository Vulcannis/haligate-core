package org.haligate.core;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;

public class HalTraversed extends BasicTraversed
{
    private final String content;

    HalTraversed( final CloseableHttpClient httpClient, final Supplier< HttpContext > context, final String responseContent, final ListMultimap< String, String > headers )
    {
        super( httpClient, context, headers );
        this.content = responseContent;
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return new Resource< Void >( content, Void.class, headers );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return new Resource< T >( content, contentType, headers );
    }
}
