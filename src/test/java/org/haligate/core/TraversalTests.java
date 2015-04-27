package org.haligate.core;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.haligate.core.data.Actor;
import org.junit.Test;

public class TraversalTests extends TestBase
{
    @Test
    public void asLink( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link root = client.from( rootUri ).asLink( );

        assertThat( root, notNullValue( ) );
        assertThat( root.toUri( ), equalTo( rootUri ) );
    }

    @Test
    public void followFromUri( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link movies = client.from( rootUri ).follow( "movies" ).asLink( );

        assertThat( movies, notNullValue( ) );
        assertThat( movies.toUri( ), equalTo( rootUri.resolve( "/movies" ) ) );
    }

    @Test
    public void followFromLink( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link movies = client.from( rootUri ).follow( "movies" ).asLink( );

        final Link root = client.from( movies ).follow( "root" ).asLink( );

        assertThat( root, notNullValue( ) );
        assertThat( root.toUri( ), equalTo( rootUri ) );
    }

    @Test
    public void basics( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Resource< Actor > cast = client.from( rootUri ).follow( "movies", "movie[0]", "cast[name:Keanu Reeves]" ).asResource( Actor.class );

        assertThat( cast.getSelfLink( ).toUri( ), equalTo( rootUri.resolve( "/actors/1" ) ) );
        final Actor body = cast.getBody( );
        assertThat( body, notNullValue( ) );
        assertThat( body.getName( ), equalTo( "Keanu Reeves" ) );
    }
}
