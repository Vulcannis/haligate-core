package org.haligate.core;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.common.net.HttpHeaders;

public class RetrievingTraversal extends Traversal
{
    public RetrievingTraversal( final CloseableHttpClient httpClient, final HttpContext context, final URI nextUri )
    {
        super( httpClient, context, nextUri );
    }

    @Override
    public < T > Resource< T > asResource( final Class< T > type ) throws IOException
    {
        final HttpGet request = new HttpGet( currentLocation );
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType );
        try( final CloseableHttpResponse response = httpClient.execute( request, context ) ) {
            final String content = EntityUtils.toString( response.getEntity( ) );
            if( response.getStatusLine( ).getStatusCode( ) / 100 != 2 ) {
                throw new IOException( "Unexpected response for resource " + currentLocation + ": " + response );
            }
            return new Resource< T >( content, type );
        }
    }
}
