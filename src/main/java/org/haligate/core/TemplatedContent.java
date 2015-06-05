package org.haligate.core;

import java.util.Map;

public interface TemplatedContent< S >
{
    public Object getContent( final Map< String, Object > parameters );
}
