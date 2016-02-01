package org.haligate.core;

@SuppressWarnings( "serial" )
public class HaligateRuntimeException extends RuntimeException
{
    public HaligateRuntimeException( )
    {
    }

    public HaligateRuntimeException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public HaligateRuntimeException( final String message )
    {
        super( message );
    }

    public HaligateRuntimeException( final Throwable cause )
    {
        super( cause );
    }
}
