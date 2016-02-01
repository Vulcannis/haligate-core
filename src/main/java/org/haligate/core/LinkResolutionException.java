package org.haligate.core;

@SuppressWarnings( "serial" )
public class LinkResolutionException extends HaligateRuntimeException
{
    public LinkResolutionException( )
    {
        super( );
    }

    public LinkResolutionException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public LinkResolutionException( final String message )
    {
        super( message );
    }

    public LinkResolutionException( final Throwable cause )
    {
        super( cause );
    }
}
