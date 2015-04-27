package org.haligate.core;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.*;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Predicate;
import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;

public class Traversal
{
    private static final Pattern followRelPattern = Pattern.compile( "([^\\[]+)(?:\\[(?:(\\d+)|(?:([^:]+):([^\\]]+)))\\])?" );

    private final URI currentLocation;
    private final CloseableHttpClient httpClient;
	private final HttpContext context;

    protected Traversal( final CloseableHttpClient httpClient, final HttpContext context , final URI root )
    {
        this.httpClient = httpClient;
		this.context = context;
        currentLocation = root;
    }

	public Traversal follow( final String... rels ) throws IOException
    {
        Traversal traversor = this;
        for( final String rel: rels ) {
            final Matcher matcher = followRelPattern.matcher( rel );
            if( !matcher.matches( ) ) {
                throw new IllegalArgumentException( "Cannot follow relation '" + rel + "'" );
            }
            final String relName = matcher.group( 1 ), secondaryIndex = matcher.group( 2 ), secondaryKeyAttribute = matcher.group( 3 ), secondaryKeyValue = matcher.group( 4 );
            final Resource< Void > resource = traversor.asResource( Void.class );
            final List< Link > locations = resource.getLinks( ).get( relName );
            final Link selectedLink;
            if( locations.size( ) == 0 ) {
                throw new IllegalStateException( "Cannot follow, no links of relation '" + relName + "' found in resource " + resource );
            } else if( locations.size( ) > 1 ) {
                if( secondaryIndex != null ) {
                    selectedLink = locations.get( Integer.parseInt( secondaryIndex ) );
                } else if( secondaryKeyAttribute != null && secondaryKeyValue != null ) {
                    selectedLink = getOnlyElement( filter( locations, new Predicate< Link >( ) {
                        @Override
                        public boolean apply( final Link input )
                        {
                            final Map< String, String > properties = input.getProperties( );
                            return properties.containsKey( secondaryKeyAttribute ) && properties.get( secondaryKeyAttribute ).equals( secondaryKeyValue );
                        }
                    } ) );
                } else {
                    throw new IllegalStateException( "Cannot follow, multiple links of relation '" + relName + "' found in resource " + resource + " but no secondary key given" );
                }
            } else {
                selectedLink = locations.get( 0 );
            }
            traversor = new Traversal( httpClient, context, selectedLink.toUri( ) );
        }
        return traversor;
    }

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

    @SuppressWarnings( "unchecked" )
	public < T > Resource< T > asResource( final TypeToken< T > typeToken ) throws IOException
    {
    	return (Resource< T >)asResource( typeToken.getRawType( ) );
    }

    public Link asLink( ) throws IOException
    {
        return asResource( Void.class ).getSelfLink( );
    }

    public < T > T asObject( final Class< T > objectClass ) throws IOException
    {
    	return asResource( objectClass ).getBody( );
    }

	@SuppressWarnings( "unchecked" )
	public < T > T asObject( final TypeToken< T > typeToken ) throws IOException
	{
		return (T)asResource( typeToken.getRawType( ) ).getBody( );
	}
}
