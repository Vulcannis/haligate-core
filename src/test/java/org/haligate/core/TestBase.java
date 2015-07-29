package org.haligate.core;

import static java.util.Arrays.asList;
import static net.jadler.Jadler.*;
import static org.haligate.core.Haligate.*;

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.*;

import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.google.common.net.HttpHeaders;
import com.theoryinpractise.halbuilder.api.*;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

import net.jadler.Request;
import net.jadler.junit.rule.JadlerRule;
import net.jadler.stubbing.*;

public class TestBase
{
    @Rule
    public JadlerRule jadlerRule = new JadlerRule( );

    protected URI rootUri;

    @Before
	public void init( ) throws IllegalArgumentException, SecurityException
	{
	    final RepresentationFactory representationFactory = new StandardRepresentationFactory( );

	    rootUri = URI.create( "http://localhost:" + port( ) );
	    final Representation root = representationFactory.newRepresentation( rootUri.resolve( "" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation movies = representationFactory.newRepresentation( rootUri.resolve( "/movies" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation actors = representationFactory.newRepresentation( rootUri.resolve( "/actors" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation theMatrix = representationFactory.newRepresentation( rootUri.resolve( "/movies/1" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation johnWick = representationFactory.newRepresentation( rootUri.resolve( "/movies/2" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation keanuReeves = representationFactory.newRepresentation( rootUri.resolve( "/actors/1" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation laurenceFishborne = representationFactory.newRepresentation( rootUri.resolve( "/actors/2" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation releases1999 = representationFactory.newRepresentation( rootUri.resolve( "/movies/released/1999" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
	    final Representation releases2014 = representationFactory.newRepresentation( rootUri.resolve( "/movies/released/2014" ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );

	    link( root, "movies", movies );
	    link( root, "actors", actors );

	    link( movies, "root", root );
	    link( movies, "movie", theMatrix, "The Matrix" );
	    link( movies, "movie", johnWick, "John Wick" );
	    movies.withLink( "released", rootUri.resolve( "/movies/released" ).toASCIIString( ) + "/{year}" );
	    movies.withLink( "create", rootUri.resolve( "/movies" ) );

	    actors.withProperty( "names", asList( "Keanu Reeves", "Laurence Fishborne" ) );
	    link( actors, "root", root );
	    link( actors, "actor", keanuReeves, "Keanu Reeves" );
	    link( actors, "actor", laurenceFishborne, "Laurence Fishborne" );
	    actors.withLink( "search", rootUri.resolve( "/movies" ).toASCIIString( ) + "{?name}" );

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

        embed( releases1999, "movie", theMatrix );
        embed( releases2014, "movie", johnWick );

	    mockResources( root, movies, actors, theMatrix, keanuReeves, releases1999, releases2014 );

        onRequest( ).
            havingMethodEqualTo( "GET" ).
            havingPathEqualTo( "/movies" ).
            havingParameter( "name" ).
            havingHeaderEqualTo( HttpHeaders.ACCEPT, jsonHalContentType.getMimeType( ) ).
        respondUsing( new Responder( ) {
			@Override
			public StubResponse nextResponse( final Request request )
			{
				final String name = request.getParameters( ).getValue( "name" );
				final ImmutableMap< String, Representation > actors = ImmutableMap.of( "Keanu Reeves", keanuReeves, "Laurence Fishborne", laurenceFishborne );
				for( final Entry< String, Representation > entry: actors.entrySet( ) ) {
					if( entry.getKey( ).contains( name ) ) {
						final Representation results = representationFactory.newRepresentation( request.getURI( ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
						link( results, "actor", entry.getValue( ), entry.getKey( ) );
						return StubResponse.builder( ).body( results.toString( jsonHalContentType.getMimeType( ) ), Charsets.UTF_8 ).build( );
					}
				}
				return StubResponse.builder( ).status( 404 ).build( );
			}
        } );

        onRequest( ).
            havingMethodEqualTo( "POST" ).
            havingPathEqualTo( "/movies" ).
            havingHeaderEqualTo( HttpHeaders.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE ).
        respondUsing( new Responder( ) {
            @Override
            public StubResponse nextResponse( final Request request )
            {
                final Map< String, String > parameters = parseForm( request );
                final String name = parameters.get( "name" );
                final ImmutableMap< String, Representation > actors = ImmutableMap.of( "Keanu Reeves", keanuReeves, "Laurence Fishborne", laurenceFishborne );
                for( final Entry< String, Representation > entry: actors.entrySet( ) ) {
                    if( entry.getKey( ).contains( name ) ) {
                        final Representation results = representationFactory.newRepresentation( request.getURI( ) ).withNamespace( "ex", "http://example.com/rels/{rel}" );
                        link( results, "actor", entry.getValue( ), entry.getKey( ) );
                        return StubResponse.builder( ).body( results.toString( jsonHalContentType.getMimeType( ) ), Charsets.UTF_8 ).build( );
                    }
                }
                return StubResponse.builder( ).status( 404 ).build( );
            }
        } );

        onRequest( ).
            havingMethodEqualTo( "POST" ).
            havingPathEqualTo( "/movies" ).
            havingHeaderEqualTo( HttpHeaders.CONTENT_TYPE, jsonContentType.toString( ) ).
        respondUsing( new Responder( ) {
            private int nextId = 3;

            @Override
            public StubResponse nextResponse( final Request request )
            {
                final URI location = rootUri.resolve( "/movies/" + nextId++ );
                return StubResponse.builder( ).header( HttpHeaders.LOCATION, location.toASCIIString( ) ).status( HttpStatus.SC_CREATED ).build( );
            }
        } );
	}

    protected Map< String, String > parseForm( final Request request )
    {
        final List< NameValuePair > parameters = URLEncodedUtils.parse( request.getBodyAsString( ), request.getEncoding( ) );
        final Map< String, String > map = Maps.newHashMap( );
        for( final NameValuePair pair: parameters ) {
            map.put( pair.getName( ), pair.getValue( ) );
        }
        return map;
    }

    private void link( final Representation from, final String rel, final Representation to )
    {
        link( from, rel, to, null );
    }

    private void link( final Representation from, final String rel, final Representation to, final String name )
    {
        from.withLink( rel, to.getResourceLink( ).getHref( ), name, null, null, null );
        from.withLink( "ex:" + rel, to.getResourceLink( ).getHref( ), name, null, null, null );
    }

    private void embed( final Representation from, final String rel, final Representation to )
    {
        from.withRepresentation( rel, to );
    }

    private void mockResources( final Representation... resources )
    {
        for( final Representation resource: resources ) {
            final URI resourceUri = URI.create( resource.getResourceLink( ).getHref( ) );
            onRequest( ).
                havingMethodEqualTo( "GET" ).
                havingPathEqualTo( "/" + rootUri.relativize( resourceUri ).toASCIIString( ) ).
                havingHeaderEqualTo( HttpHeaders.ACCEPT, jsonHalContentType.getMimeType( ) ).
            respond( ).
                withHeader( "X-Root-Resource", rootUri.toASCIIString( ) ).
                withContentType( jsonHalContentType.toString( ) ).
                withBody( resource.toString( jsonHalContentType.getMimeType( ) ) );
            onRequest( ).
                havingMethodEqualTo( "PUT" ).
                havingPathEqualTo( "/" + rootUri.relativize( resourceUri ).toASCIIString( ) ).
                havingHeaderEqualTo( HttpHeaders.CONTENT_TYPE, jsonContentType.toString( ) ).
            respond( ).
                withHeader( "X-Root-Resource", rootUri.toASCIIString( ) ).
                withStatus( HttpStatus.SC_NO_CONTENT );
            onRequest( ).
                havingMethodEqualTo( "DELETE" ).
                havingPathEqualTo( "/" + rootUri.relativize( resourceUri ).toASCIIString( ) ).
            respond( ).
                withHeader( "X-Root-Resource", rootUri.toASCIIString( ) ).
                withStatus( HttpStatus.SC_NO_CONTENT );
        }
    }
}
