package org.haligate.core;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Test;

public class CustomClientTests extends TestBase
{
	@Test
	public void customContext( ) throws IOException
	{
		final HttpClientContext context = HttpClientContext.create( );
        assertThat( context.getCookieStore( ), nullValue( ) );
		final Client client = Haligate.usingContext( context ).createClient( );
        client.from( rootUri ).asLink( );
        assertThat( context.getCookieStore( ), notNullValue( ) );
	}
}
