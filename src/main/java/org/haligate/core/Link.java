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

    public static Link forUri( final URI href )
    {
        return new Link( href.toString( ) );
    }

    public Link( )
    {
    }

    public Link( final String href )
    {
        properties.put( "href", href );
    }

    public URI toUri( )
    {
        if( templated ) {
            throw new IllegalStateException(
                "Cannot get URI of a template link without providing parameters. Explicitly pass an empty parameter map to ignore optional parameters. ("
                    + getHref( ) + ")" );
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

    @Override
    public int hashCode( )
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + getHref( ).hashCode( );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if( this == obj ) {
            return true;
        }
        if( obj == null ) {
            return false;
        }
        if( getClass( ) != obj.getClass( ) ) {
            return false;
        }
        final Link other = (Link)obj;
        if( !getHref( ).equals( other.getHref( ) ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString( )
    {
        return "Link [href=" + getHref( ) + "]";
    }
}
