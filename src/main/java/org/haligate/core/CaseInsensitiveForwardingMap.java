package org.haligate.core;

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.ForwardingMap;

@SuppressWarnings( "serial" )
public class CaseInsensitiveForwardingMap< V > extends ForwardingMap< String, V > implements Serializable
{
    public CaseInsensitiveForwardingMap( )
    {
        this( new HashMap< String, V >( ) );
    }

    public CaseInsensitiveForwardingMap( final Map< String, V > inner )
    {
        this.inner = inner;
    }

    private final Map< String, V > inner;

    @Override
    protected Map< String, V > delegate( )
    {
        return inner;
    }

    private static String upper( final Object key )
    {
        return key == null ? null : key.toString( ).toUpperCase( );
    }

    @Override
    public V get( final Object key )
    {
        return inner.get( upper( key ) );
    }

    @Override
    public void putAll( final Map< ? extends String, ? extends V > map )
    {
        if( map == null || map.isEmpty( ) ) {
            inner.putAll( map );
        } else {
            for( final Entry< ? extends String, ? extends V > entry: map.entrySet( ) ) {
                inner.put( upper( entry.getKey( ) ), entry.getValue( ) );
            }
        }
    }

    @Override
    public V remove( final Object object )
    {
        return inner.remove( upper( object ) );
    }

    @Override
    public boolean containsKey( final Object key )
    {
        return inner.containsKey( upper( key ) );
    }

    @Override
    public V put( final String key, final V value )
    {
        return inner.put( upper( key ), value );
    }
}
