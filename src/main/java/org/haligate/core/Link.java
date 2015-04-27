package org.haligate.core;

import static java.util.Collections.unmodifiableMap;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.Maps;

public class Link
{
    private final Map< String, String > properties = Maps.newHashMap( );
    private boolean templated;

    public URI toUri( )
    {
        return URI.create( properties.get( "href" ) );
    }

    @JsonProperty
    public void setTemplated( final boolean value )
    {
        templated = value;
    }

    public boolean isTemplated( )
    {
        return templated;
    }

    @JsonAnySetter
    public void setProperty( final String name, final String value )
    {
        properties.put( name, value );
    }

    @JsonAnyGetter
    public Map< String, String > getProperties( )
    {
        return unmodifiableMap( properties );
    }
}
