package org.haligate.core;

import static net.jadler.Jadler.*;
import static org.haligate.core.Haligate.jsonHalContentType;

import java.net.URI;

import org.junit.*;

import com.theoryinpractise.halbuilder.api.*;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

public class TestBase
{
	protected URI rootUri;

	@Before
	public void init( )
	{
	    initJadler( );

	    final RepresentationFactory representationFactory = new StandardRepresentationFactory( );

	    rootUri = URI.create( "http://localhost:" + port( ) );
	    final Representation root = representationFactory.newRepresentation( rootUri.resolve( "" ) );
	    final Representation movies = representationFactory.newRepresentation( rootUri.resolve( "/movies" ) );
	    final Representation actors = representationFactory.newRepresentation( rootUri.resolve( "/actors" ) );
	    final Representation theMatrix = representationFactory.newRepresentation( rootUri.resolve( "/movies/1" ) );
	    final Representation johnWick = representationFactory.newRepresentation( rootUri.resolve( "/movies/2" ) );
	    final Representation keanuReeves = representationFactory.newRepresentation( rootUri.resolve( "/actors/1" ) );
	    final Representation laurenceFishborne = representationFactory.newRepresentation( rootUri.resolve( "/actors/2" ) );

	    link( root, "movies", movies );
	    link( root, "actors", actors );

	    link( movies, "root", root );
	    link( movies, "movie", theMatrix, "The Matrix" );
	    link( movies, "movie", johnWick, "John Wick" );

	    link( actors, "root", root );
	    link( actors, "actor", keanuReeves, "Keanu Reeves" );
	    link( actors, "actor", laurenceFishborne, "Laurence Fishborne" );

	    theMatrix.withProperty( "title", "The Matrix" );
	    link( theMatrix, "movies", movies );
	    link( theMatrix, "cast", keanuReeves, "Keanu Reeves" );
	    link( theMatrix, "cast", laurenceFishborne, "Laurence Fishborne" );

	    johnWick.withProperty( "title", "John Wick" );
	    link( johnWick, "movies", movies );
	    link( johnWick, "cast", keanuReeves, "Keanu Reeves" );

	    keanuReeves.withProperty( "name", "Keanu Reeves" );
	    link( keanuReeves, "actors", actors );
	    link( keanuReeves, "filmography", theMatrix, "The Matrix" );
	    link( keanuReeves, "filmography", johnWick, "John Wick" );

	    laurenceFishborne.withProperty( "name", "Laurence Fishborne" );
	    link( laurenceFishborne, "actors", actors );
	    link( laurenceFishborne, "filmography", theMatrix, "The Matrix" );

	    mockResources( root, movies, actors, theMatrix, keanuReeves );
	}

	private void link( final Representation from, final String rel, final Representation to )
	{
	    link( from, rel, to, null );
	}

	private void link( final Representation from, final String rel, final Representation to, final String name )
	{
	    from.withLink( rel, to.getResourceLink( ).getHref( ), name, null, null, null );
	}

	private void mockResources( final Representation... resources )
	{
	    for( final Representation resource: resources ) {
	        final URI resourceUri = URI.create( resource.getResourceLink( ).getHref( ) );
	        onRequest( ).
	            havingMethodEqualTo( "GET" ).
	            havingPathEqualTo( "/" + rootUri.relativize( resourceUri ).toASCIIString( ) ).
	            havingHeaderEqualTo( "Accept", jsonHalContentType ).
	        respond( ).
	            withContentType( jsonHalContentType ).
	            withBody( resource.toString( jsonHalContentType ) );
	    }
	}

	@After
	public void dispose( )
	{
	    closeJadler( );
	}
}
