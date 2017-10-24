package org.haligate.core.impl;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.util.EntityUtils;
import org.haligate.core.*;
import org.haligate.core.support.CaseInsensitiveForwardingMap;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.net.HttpHeaders;
import com.ibm.icu.text.*;

public class HttpTraversing extends BasicTraversing
{
	private static final Set< Charset > jsonCharsets = ImmutableSet.of( Charsets.UTF_8, Charsets.UTF_16, Charsets.UTF_16BE, Charsets.UTF_16LE, Charset.forName( "UTF-32" ), Charset.forName( "UTF-32BE" ), Charset.forName( "UTF-32LE" ) );

    protected final Config config;

    public HttpTraversing( final Config config, final Link selectedLink )
    {
    	super( selectedLink );
        this.config = config;
    }

    @Override
    public Traversed get( ) throws UncheckedIOException
    {
        final URI uri = selectedLink.toUri( parameters );
        final HttpGet request = new HttpGet( uri );
        return execute( request );
    }

    @Override
    public Traversed post( final Object content ) throws UncheckedIOException
    {
        final HttpPost request = new HttpPost( );
        prepareRequestContent( request, content );
        final URI uri = selectedLink.toUri( parameters );
        request.setURI( uri );
        return execute( request );
    }

    private void prepareRequestContent( final HttpEntityEnclosingRequest request, final Object content ) throws UncheckedIOException
    {
        if( content instanceof Optional ) {
            if( ( (Optional< ? >)content ).isPresent( ) ) {
                prepareRequestContent( request, ( (Optional< ? >)content ).get( ) );
            } else {
                request.setEntity( new StringEntity( "", ContentType.APPLICATION_JSON ) );
            }
        } else if( content instanceof TemplatedContent ) {
            final Object producedContent = ( (TemplatedContent< ? >)content ).getContent( parameters );
            prepareRequestContent( request, producedContent );
        } else if( content instanceof HttpEntity ) {
            request.setEntity( (HttpEntity)content );
        } else {
        	try {
        		final String requestContent = config.mapper.get( ).writeValueAsString( content );
        		request.setEntity( new StringEntity( requestContent, ContentType.APPLICATION_JSON ) );
        	}
        	catch( final IOException e ) {
        		throw new UncheckedIOException( e );
        	}
        }
    }

    @Override
    public Traversed put( final Object content ) throws UncheckedIOException
    {
        final HttpPut request = new HttpPut( );
        prepareRequestContent( request, content );
        final URI uri = selectedLink.toUri( parameters );
        request.setURI( uri );
        return execute( request );
    }

    @Override
    public Traversed delete( ) throws UncheckedIOException
    {
        final URI uri = selectedLink.toUri( parameters );
        final HttpDelete request = new HttpDelete( uri );
        return execute( request );
    }

    @Override
    public Link asLink( )
    {
    	// TODO Should support partial expansion.
        return Link.forUri( selectedLink.toUri( parameters ) );
    }

    private Traversed execute( final HttpUriRequest request ) throws UncheckedIOException
    {
        request.addHeader( HttpHeaders.ACCEPT, Haligate.jsonHalContentType.getMimeType( ) );
        for( final Entry< String, String > entry: requestHeaders.entrySet( ) ) {
            request.addHeader( entry.getKey( ), entry.getValue( ) );
        }
        try ( final CloseableHttpResponse response = config.httpClient.get( ).execute( request, config.context.get( ) ) ) {
            final HttpEntity entity = response.getEntity( );
            final StatusLine statusLine = response.getStatusLine( );
            if( statusLine.getStatusCode( ) / 100 != 2 ) {
                EntityUtils.consumeQuietly( entity );
                throw new HttpResponseException( statusLine.getStatusCode( ), statusLine.getReasonPhrase( ) );
            }
            try {
                final ListMultimap< String, String > headers = parseHeaders( response );
                final String responseContent;
                if( entity == null ) {
                    responseContent = null;
                } else {
                    responseContent = loadEntity( entity );
                }
                return new HalTraversed( config, responseContent, headers );
            }
            finally {
                EntityUtils.consume( entity );
            }
        }
        catch( final IOException e ) {
        	throw new UncheckedIOException( e );
        }
    }

	private String loadEntity( final HttpEntity entity ) throws IOException
	{
		final String responseContent;
		final byte[ ] responseContentRaw = EntityUtils.toByteArray( entity );
		Charset charset = null;
		final ContentType contentType = ContentType.get( entity );
		if( contentType != null ) {
			charset = contentType.getCharset( );
		}
		if( charset == null ) {
			final CharsetDetector detector = new CharsetDetector( );
			detector.setText( responseContentRaw );
			final CharsetMatch detected = detector.detect( );
			charset = Charset.forName( detected.getName( ) );
			if( !jsonCharsets.contains( charset ) ) {
				charset = null;
			}
		}
		if( charset == null ) {
			charset = Charsets.UTF_8;
		}
		responseContent = new String( responseContentRaw, charset );
		return responseContent;
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
