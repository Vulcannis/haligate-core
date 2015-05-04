package org.haligate.core.impl;

import java.io.IOException;

import org.haligate.core.*;

import com.google.common.collect.ListMultimap;

public class HalTraversed extends BasicTraversed
{
    private final String content;

    HalTraversed( final Config config, final String responseContent, final ListMultimap< String, String > headers )
    {
        super( config, headers );
        this.content = responseContent;
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return new HalResource< Void >( config, content, Void.class, headers );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return new HalResource< T >( config, content, contentType, headers );
    }
}
