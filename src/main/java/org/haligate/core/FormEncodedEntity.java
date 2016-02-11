package org.haligate.core;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.*;
import com.google.common.collect.*;

/**
 * A URL enocoded form entity with fields consisting of template values.
 */
public class FormEncodedEntity implements TemplatedContent< UrlEncodedFormEntity >
{
    private final Charset charset;

    public FormEncodedEntity( )
    {
        charset = null;
    }

    public FormEncodedEntity( final Charset charset )
    {
        this.charset = charset;
    }

    @Override
    public Optional< UrlEncodedFormEntity > getContent( final Map< String, Object > parameters )
    {
        final List< NameValuePair > params = FluentIterable.from( parameters.entrySet( ) ).transformAndConcat( new Function< Map.Entry< String, Object >, Iterable< NameValuePair > >( ) {
            @Override
            public Iterable< NameValuePair > apply( final Entry< String, Object > input )
            {
                // TODO try using/mirroring UriTemplate's expansion code
                final Object value = input.getValue( );
                if( value.getClass( ).isArray( ) ) {
                    final int length = Array.getLength( value );
                    final List< NameValuePair > list = Lists.newArrayListWithCapacity( length );
                    for( int loop = 0; loop < length; loop++ ) {
                        list.add( new BasicNameValuePair( input.getKey( ), Array.get( value, loop ).toString( ) ) );
                    }
                    return list;
                } else if( value instanceof Iterable ) {
                    return FluentIterable.from( (Iterable< ? >)value ).transform( new Function< Object, NameValuePair >( ) {
                        @Override
                        public NameValuePair apply( final Object item )
                        {
                            return new BasicNameValuePair( input.getKey( ), item.toString( ) );
                        }
                    } ).toList( );
                } else {
                    return Collections.< NameValuePair >singleton( new BasicNameValuePair( input.getKey( ), value.toString( ) ) );
                }
            }
        } ).toList( );
        parameters.clear( );
        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity( params, charset );
        return Optional.of( entity );
    }
}
