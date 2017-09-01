package org.haligate.core;

import java.net.URI;
import java.util.function.Supplier;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.haligate.core.impl.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client extends Config
{
    public Client( final Supplier< CloseableHttpClient > httpClient, final Supplier< HttpContext > context, final Supplier< ObjectMapper > mapper, final boolean includeEmbeddedLinks )
    {
        super( httpClient, context, mapper, includeEmbeddedLinks );
    }

    public Traversing from( final URI root )
    {
        return new HttpTraversing( this, Link.forUri( root ) );
    }

    public Traversing from( final Link link )
    {
        return new HttpTraversing( this, link );
    }
}
