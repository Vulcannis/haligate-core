package org.haligate.core;

import java.net.URI;
import java.util.Collections;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.haligate.core.impl.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;

public class Client extends Config
{
    public Client( final Supplier< CloseableHttpClient > httpClient, final Supplier< HttpContext > context, final Supplier< ObjectMapper > mapper )
    {
        super( httpClient, context, mapper );
    }

    public Traversing from( final URI root )
    {
        return new HttpTraversing( this, Link.forUri( root ), Collections.< String, Object >emptyMap( ) );
    }

    public Traversing from( final Link link )
    {
        return new HttpTraversing( this, link, Collections.< String, Object >emptyMap( ) );
    }
}
