package org.haligate.core.impl;

import java.io.UncheckedIOException;

import org.haligate.core.Resource;

import com.google.common.collect.ListMultimap;

public class HalTraversed extends BasicTraversed
{
    private final String content;

    HalTraversed( final Config config, final String responseContent, final ListMultimap< String, String > headers )
    {
        super( config, headers );
        content = responseContent;
    }

    @Override
    public Resource< ? > asResource( ) throws UncheckedIOException
    {
        return new HalResource< Void >( config, content, Void.class, headers );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws UncheckedIOException
    {
        return new HalResource< T >( config, content, contentType, headers );
    }
}
