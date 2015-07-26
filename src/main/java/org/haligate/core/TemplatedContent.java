package org.haligate.core;

import java.util.Map;

import com.google.common.base.Optional;

public interface TemplatedContent< S >
{
    public Optional< S > getContent( final Map< String, Object > parameters );
}
