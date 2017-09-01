package org.haligate.core;

import java.util.function.Supplier;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Suppliers;

public class Haligate
{
    public static final ContentType jsonHalContentType = ContentType.create( "application/hal+json" );
    public static final ContentType jsonContentType = ContentType.APPLICATION_JSON;

    public static Client defaultClient( )
    {
        return custom( ).createClient( );
    }

    public static ClientBuilder custom( )
    {
        return new ClientBuilder( );
    }

	public static class ClientBuilder
	{
	    private Supplier< CloseableHttpClient > httpClient = Suppliers.ofInstance( HttpClients.createDefault( ) );
		private Supplier< HttpContext > context = Suppliers.ofInstance( (HttpContext)HttpClientContext.create( ) );
		private Supplier< ObjectMapper > mapper = Suppliers.ofInstance( new ObjectMapper( ) );
		private boolean includeEmbeddedLinks = false;

		public ClientBuilder usingClient( final CloseableHttpClient httpClient )
		{
		    return usingClient( Suppliers.ofInstance( httpClient ) );
		}

		public ClientBuilder usingClient( final Supplier< CloseableHttpClient > httpClient )
		{
		    this.httpClient = httpClient;
		    return this;
		}

		public ClientBuilder usingContext( final HttpContext context )
		{
		    return usingContext( Suppliers.ofInstance( context ) );
		}

		public ClientBuilder usingContext( final Supplier< HttpContext > contextSupplier )
        {
		    context = contextSupplier;
            return this;
        }

		public ClientBuilder usingMapper( final ObjectMapper mapper )
		{
		    return usingMapper( Suppliers.ofInstance( mapper ) );
		}

        public ClientBuilder usingMapper( final Supplier< ObjectMapper > mapper )
        {
            this.mapper = mapper;
            return this;
        }

        public ClientBuilder includeEmbeddedLinks( final boolean value )
        {
        	includeEmbeddedLinks = value;
        	return this;
        }

        public Client createClient( )
		{
			return new Client( httpClient, context, mapper, includeEmbeddedLinks );
		}
	}
}
