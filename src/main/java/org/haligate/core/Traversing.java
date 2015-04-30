package org.haligate.core;

import java.io.IOException;

public interface Traversing extends Traversed
{
    public Traversed get( ) throws IOException;

    public Traversed post( final Object content ) throws IOException;

    public Link asLink( );
}
