package org.haligate.core.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.util.EntityUtils;
import org.haligate.core.*;
import org.haligate.core.support.CaseInsensitiveForwardingMap;

import com.google.common.collect.ListMultimap;
import com.google.common.net.HttpHeaders;

public class HttpTraversing extends BasicTraversing
{
    protected final Config config;
    protected final URI uri;

    public HttpTraversing( final Config config, final URI uri  )
    {
        this.config = config;
        this.uri = uri;
    }

    @Override
    public Traversed get( ) throws IOException
    {
        final HttpGet request = new HttpGet( uri );
        return execute( request );
    }

    @Override
    public Traversed post( final Object content ) throws IOException
    {
        final HttpPost request = new HttpPost( uri );
        final String requestContent = config.mapper.get( ).writeValueAsString( content );
        request.setEntity( new StringEntity( requestContent, ContentType.APPLICATION_JSON ) );
        return execute( request );
    }

    @Override
    public Traversed put( final Object content ) throws IOException
    {
        final HttpPut request = new HttpPut( uri );
        final String requestContent = config.mapper.get( ).writeValueAsString( content );
        request.setEntity( new StringEntity( requestContent, ContentType.APPLICATION_JSON ) );
        return execute( request );
    }

    @Override
    public Traversed delete( ) throws IOException
    {
        final HttpDelete request = new HttpDelete( uri );
        return execute( request );
    }

    @Override
    public Link asLink( )
    {
        final Link link = new Link( );
        link.setProperty( "href", uri.toASCIIString( ) );
        return link;
    }

    private Traversed execute( final HttpUriRequest request ) throws IOException, ClientProtocolException
    {
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType.getMimeType( ) );
        try ( final CloseableHttpResponse response = config.httpClient.get( ).execute( request, config.context.get( ) ) ) {
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + uri + ": " + response );
            }
            final ListMultimap< String, String > headers = parseHeaders( response );
            final HttpEntity entity = response.getEntity( );
            final String responseContent = entity == null ? null : EntityUtils.toString( entity );
            return new HalTraversed( config, responseContent, headers );
        }
    }

    private ListMultimap< String, String > parseHeaders( final HttpResponse response )
    {
        final ListMultimap< String, String > headers = CaseInsensitiveForwardingMap.newCaseInsensitiveKeyedListMultimap( );
        for( final Header header: response.getAllHeaders( ) ) {
            headers.put( header.getName( ), header.getValue( ) );
        }
        return headers;
    }
}
