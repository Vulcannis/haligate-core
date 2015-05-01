package org.haligate.core;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.*;

public class Haligate
{
    public static final String jsonHalContentType = "application/hal+json";

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
		private Supplier< HttpContext > contextSupplier = Suppliers.ofInstance( (HttpContext)HttpClientContext.create( ) );

		public ClientBuilder usingContext( final HttpContext context )
		{
		    return usingContext( Suppliers.ofInstance( context ) );
		}

		public ClientBuilder usingContext( final Supplier< HttpContext > contextSupplier )
        {
		    this.contextSupplier = contextSupplier;
            return this;
        }

        public Client createClient( )
		{
			final CloseableHttpClient httpClient = HttpClients.createDefault( );
			return new Client( httpClient, contextSupplier );
		}
	}
}
