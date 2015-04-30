package org.haligate.core;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;

public class HttpTraversing implements Traversing
{
    protected final CloseableHttpClient httpClient;
    protected final HttpContext context;
    protected final URI uri;

    HttpTraversing( final CloseableHttpClient httpClient, final HttpContext context, final URI uri )
    {
        this.httpClient = httpClient;
        this.context = context;
        this.uri = uri;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        final HttpGet request = new HttpGet( uri );
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType );
        try ( final CloseableHttpResponse response = httpClient.execute( request, context ) ) {
            final String responseContent = EntityUtils.toString( response.getEntity( ) );
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + uri + ": " + response );
            }
            final Map< String, Collection< String > > map = new CaseInsensitiveForwardingMap< Collection< String > >( );
            final ListMultimap< String, String > headers = Multimaps.newListMultimap( map, new Supplier< List< String > >( ) {
                @Override
                public List< String > get( )
                {
                    return Lists.newArrayList( );
                }
            } );
            for( final Header header: response.getAllHeaders( ) ) {
                headers.put( header.getName( ), header.getValue( ) );
            }
            return new HalTraversed( httpClient, context, responseContent, headers );
        }
    }

    @Override
    public Traversed post( final Object content ) throws IOException
    {
        final HttpPost request = new HttpPost( uri );
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType );
        final String requestContent = new ObjectMapper( ).writeValueAsString( content );
        request.setEntity( new StringEntity( requestContent, ContentType.APPLICATION_JSON ) );
        try ( final CloseableHttpResponse response = httpClient.execute( request, context ) ) {
            final String responseContent = EntityUtils.toString( response.getEntity( ) );
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + uri + ": " + response );
            }
            final Map< String, Collection< String > > map = new CaseInsensitiveForwardingMap< Collection< String > >( );
            final ListMultimap< String, String > headers = Multimaps.newListMultimap( map, new Supplier< List< String > >( ) {
                @Override
                public List< String > get( )
                {
                    return Lists.newArrayList( );
                }
            } );
            for( final Header header: response.getAllHeaders( ) ) {
                headers.put( header.getName( ), header.getValue( ) );
            }
            return new HalTraversed( httpClient, context, responseContent, headers );
        }
    }

    @Override
    public Link asLink( )
    {
        final Link link = new Link( );
        link.setProperty( "href", uri.toASCIIString( ) );
        return link;
    }

    @Override
    public Traversing follow( final String... rels ) throws IOException
    {
        return get( ).follow( rels );
    }

    @Override
    public Resource< ? > asResource( ) throws IOException
    {
        return get( ).asResource( );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > contentType ) throws IOException
    {
        return get( ).asResource( contentType );
    }

    @Override
    public < T > Resource< T > asResource( final TypeToken< T > contentType ) throws IOException
    {
        return get( ).asResource( contentType );
    }

    @Override
    public < T > T asObject( final Class< T > contentType ) throws IOException
    {
        return get( ).asObject( contentType );
    }

    @Override
    public < T > T asObject( final TypeToken< T > contentType ) throws IOException
    {
        return get( ).asObject( contentType );
    }

    @Override
    public Traversing followHeader( final String header ) throws IOException
    {
        return get( ).followHeader( header );
    }

    @Override
    public Traversing followHeader( final String header, final Function< List< String >, String > disambiguate ) throws IOException
    {
        return get( ).followHeader( header, disambiguate );
    }

    @Override
    public Traversed with( final String name, final String value ) throws IOException
    {
        return get( ).with( name, value );
    }

    @Override
    public Traversed with( final Map< String, Object > parameters ) throws IOException
    {
    	return get( ).with( parameters );
    }
}
