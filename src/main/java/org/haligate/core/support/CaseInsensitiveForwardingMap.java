package org.haligate.core.support;

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.*;

@SuppressWarnings( "serial" )
public class CaseInsensitiveForwardingMap< V > extends ForwardingMap< String, V > implements Serializable
{
    // Upper case key -> inserted key
    private final Map< String, String > originalKeys = Maps.newHashMap( );

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
        return inner.get( originalKeys.get( upper( key ) ) );
    }

    @Override
    public void putAll( final Map< ? extends String, ? extends V > map )
    {
        if( map == null ) {
            throw new NullPointerException( );
        } else if( !map.isEmpty( ) ) {
            for( final Entry< ? extends String, ? extends V > entry: map.entrySet( ) ) {
                final String upperKey = upper( entry.getKey( ) );
                final String oldKey = originalKeys.get( upperKey );
                if( oldKey != null ) {
                    inner.remove( oldKey );
                }
                originalKeys.put( upperKey, entry.getKey( ) );
                inner.put( entry.getKey( ), entry.getValue( ) );
            }
        }
    }

    @Override
    public V remove( final Object object )
    {
        final String upper = upper( object );
        if( upper != null ) {
            final String oldKey = originalKeys.remove( upper );
            if( oldKey != null ) {
                return inner.remove( oldKey );
            }
        }
        return null;
    }

    @Override
    public boolean containsKey( final Object key )
    {
        return originalKeys.containsKey( upper( key ) );
    }

    @Override
    public V put( final String key, final V value )
    {
        final V oldValue = inner.remove( originalKeys.put( upper( key ), key ) );
        inner.put( key, value );
        return oldValue;
    }

    public static < T > ListMultimap< String, T > newCaseInsensitiveKeyedListMultimap( )
    {
        final Map< String, Collection< T > > map = new CaseInsensitiveForwardingMap< Collection< T > >( );
        final ListMultimap< String, T > multimap = Multimaps.newListMultimap( map, ( ) -> Lists.newArrayList( ) );
        return multimap;
    }
}
