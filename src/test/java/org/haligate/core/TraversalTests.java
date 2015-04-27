package org.haligate.core;

import static org.haligate.core.OurMatchers.hasKey;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.haligate.core.data.Actor;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

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
    public void asObject( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Actor actor = client.from( rootUri.resolve( "/actors/1" ) ).asObject( Actor.class );
        assertThat( actor, notNullValue( ) );
        assertThat( actor.getName( ), equalTo( "Keanu Reeves" ) );
    }

    @Test
    public void asGenericObject( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        @SuppressWarnings( "serial" )
		final Map< String, List< String > > actors = client.from( rootUri.resolve( "/actors" ) ).asObject( new TypeToken< Map< String, List< String > > >( ) { } );
        assertThat( actors, notNullValue( ) );
		assertThat( actors, Matchers.hasKey( "names" ) );
        assertThat( actors.get( "names" ), hasItem( "Keanu Reeves" ) );
        assertThat( actors.get( "names" ), hasItem( "Laurence Fishborne" ) );
    }

    @Test
    public void asResource( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final URI actorUri = rootUri.resolve( "/actors/1" );
		final Resource< Actor > actor = client.from( actorUri ).asResource( Actor.class );
        assertThat( actor, notNullValue( ) );
        assertThat( actor.getSelfLink( ).toUri( ), equalTo( actorUri ) );
        assertThat( actor.getLinks( ), hasKey( "self" ) );
        assertThat( actor.getLinks( ), hasKey( "filmography" ) );
        assertThat( actor.getBody( ), notNullValue( ) );
        assertThat( actor.getBody( ).getName( ), equalTo( "Keanu Reeves" ) );
    }

    @Test
    public void asGenericResource( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final URI actorsUri = rootUri.resolve( "/actors" );
		@SuppressWarnings( "serial" )
		final Resource< Map< String, List< String > > > actors = client.from( actorsUri ).asResource( new TypeToken< Map< String, List< String > > >( ) { } );
        assertThat( actors, notNullValue( ) );
        assertThat( actors, notNullValue( ) );
        assertThat( actors.getSelfLink( ).toUri( ), equalTo( actorsUri ) );
        assertThat( actors.getLinks( ), hasKey( "self" ) );
        assertThat( actors.getLinks( ), hasKey( "root" ) );
        assertThat( actors.getBody( ), notNullValue( ) );
        assertThat( actors.getBody( ), Matchers.hasKey( "names" ) );
        assertThat( actors.getBody( ).get( "names" ), hasItem( "Keanu Reeves" ) );
        assertThat( actors.getBody( ).get( "names" ), hasItem( "Laurence Fishborne" ) );
    }

    @Test
    public void indexedSecondaryKey( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link movie = client.from( rootUri ).follow( "movies[0]", "movie[0]" ).asLink( );

        assertThat( movie.toUri( ), equalTo( rootUri.resolve( "/movies/1" ) ) );
    }

    @Test
    public void secondaryKey( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link movie = client.from( rootUri ).follow( "movies", "movie[name:The Matrix]" ).asLink( );

        assertThat( movie.toUri( ), equalTo( rootUri.resolve( "/movies/1" ) ) );
    }

    @Test
    public void linkPathTemplates( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link released = client.from( rootUri ).follow( "movies" ).with( "year", "1999" ).follow( "released" ).asLink( );

        assertThat( released.toUri( ), equalTo( rootUri.resolve( "/movies/released/1999" ) ) );
    }

    @Test
    public void linkParameterTemplates( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Resource< ? > results = client.from( rootUri ).follow( "actors" ).with( "name", "Keanu" ).follow( "search" ).asResource( );

        assertThat( results.getLinks( ), hasKey( "actor" ) );
        assertThat( results.getLinks( ).get( "actor" ), hasSize( 1 ) );
        assertThat( results.getLinks( ).get( "actor" ).get( 0 ).toUri( ), equalTo( rootUri.resolve( "/actors/1" ) ) );
    }

    @Test
    public void curies( ) throws IOException
    {
        final Client client = Haligate.defaultClient( );
        final Link actors = client.from( rootUri ).follow( "http://example.com/rels/actors" ).asLink( );

        assertThat( actors.toUri( ), equalTo( rootUri.resolve( "/actors" ) ) );
    }
}
