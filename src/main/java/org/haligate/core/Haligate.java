package org.haligate.core;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;

public class Haligate
{
    public static final String jsonHalContentType = "application/hal+json";

    public static Client defaultClient( )
    {
        final CloseableHttpClient httpClient = HttpClients.createDefault( );
        return new Client( httpClient, HttpClientContext.create( ) );
    }

	public static ClientBuilder usingContext( final HttpContext context )
	{
		return new ClientBuilder( context );
	}

	public static class ClientBuilder
	{
		private final HttpContext context;

		public ClientBuilder( final HttpContext context )
		{
			this.context = context;
		}

		public Client createClient( )
		{
			final CloseableHttpClient httpClient = HttpClients.createDefault( );
			return new Client( httpClient, context );
		}
	}
}
