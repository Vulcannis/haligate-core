package org.haligate.core;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.google.common.net.HttpHeaders;

public class HttpTraversing extends BasicTraversing
{
    protected final Client client;
    protected final URI uri;

    HttpTraversing( final Client client, final URI uri  )
    {
        this.client = client;
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
        final String requestContent = client.mapper.get( ).writeValueAsString( content );
        request.setEntity( new StringEntity( requestContent, ContentType.APPLICATION_JSON ) );
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
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType );
        try ( final CloseableHttpResponse response = client.httpClient.get( ).execute( request, client.context.get( ) ) ) {
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + uri + ": " + response );
            }
            final ListMultimap< String, String > headers = parseHeaders( response );
            final String responseContent = EntityUtils.toString( response.getEntity( ) );
            return new HalTraversed( client, responseContent, headers );
        }
    }

    private ListMultimap< String, String > parseHeaders( final HttpResponse response )
    {
        final ListMultimap< String, String > headers = newCaseInsensitiveKeyedListMultimap( );
        for( final Header header: response.getAllHeaders( ) ) {
            headers.put( header.getName( ), header.getValue( ) );
        }
        return headers;
    }

    private static ListMultimap< String, String > newCaseInsensitiveKeyedListMultimap( )
    {
        final Map< String, Collection< String > > map = new CaseInsensitiveForwardingMap< Collection< String > >( );
        final ListMultimap< String, String > headers = Multimaps.newListMultimap( map, new Supplier< List< String > >( ) {
            @Override
            public List< String > get( )
            {
                return Lists.newArrayList( );
            }
        } );
        return headers;
    }
}
