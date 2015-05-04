package org.haligate.core;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import com.google.common.base.Supplier;

public class CustomClientTest extends TestBase
{
	@Test
	public void customStaticContext( ) throws IOException
	{
		final HttpClientContext context = HttpClientContext.create( );
        assertThat( context.getCookieStore( ), nullValue( ) );
		final Client client = Haligate.custom( ).usingContext( context ).createClient( );
        client.from( rootUri ).get( );
        assertThat( context.getCookieStore( ), notNullValue( ) );
	}

	@Test
	public void customContextSupplier( ) throws IOException
	{
        final HttpClientContext context = HttpClientContext.create( );
        final int[ ] count = { 0 };
        final Client client = Haligate.custom( ).usingContext( new Supplier< HttpContext >( ) {
            @Override
            public HttpContext get( )
            {
                count[ 0 ]++;
                return context;
            }
        } ).createClient( );
        client.from( rootUri ).follow( "movies", "root" ).asLink( );
        assertThat( count[ 0 ], equalTo( 2 ) );
	}
}
