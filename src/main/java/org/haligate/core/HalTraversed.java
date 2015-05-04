package org.haligate.core;

import java.io.IOException;

import com.google.common.collect.ListMultimap;

public class HalTraversed extends BasicTraversed
{
    private final String content;

    HalTraversed( final Client client, final String responseContent, final ListMultimap< String, String > headers )
    {
        super( client, headers );
        this.content = responseContent;
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return new Resource< Void >( client, content, Void.class, headers );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return new Resource< T >( client, content, contentType, headers );
    }
}
