package org.haligate.core;

import static java.util.Collections.unmodifiableMap;

import java.net.URI;
import java.util.Map;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.Maps;

public class Link
{
    private final Map< String, String > properties = Maps.newHashMap( );
    private boolean templated;

    public URI toUri( )
    {
    	if( templated ) {
    		throw new IllegalStateException( "Cannot get URI of a template link without providing parameters. Explicitly pass an empty parameter map to ignore optional parameters. (" + getHref( ) + ")" );
    	} else {
    		return URI.create( getHref( ) );
    	}
    }

    public URI toUri( final Map< String, Object > parameters )
    {
    	final String expanded = UriTemplate.expand( getHref( ), parameters );
		return URI.create( expanded );
    }

	public String getHref( )
	{
		return properties.get( "href" );
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
