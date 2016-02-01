package org.haligate.core.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.*;

public class CaseInsensitiveKeyedListMultimapTest
{
    @Test
    public void keySetRetainsOriginalCase( )
    {
        final ListMultimap< String, String > map = CaseInsensitiveForwardingMap.newCaseInsensitiveKeyedListMultimap( );
        map.put( "A", "1" );
        map.put( "b", "2" );
        assertThat( map.keySet( ), equalTo( (Set< String >)ImmutableSet.of( "A", "b" ) ) );
    }

    @Test
    public void keysAreCaseInsensitive( )
    {
        final ListMultimap< String, String > map = CaseInsensitiveForwardingMap.newCaseInsensitiveKeyedListMultimap( );
        map.put( "A", "1" );
        assertTrue( map.containsKey( "a" ) );
        assertThat( map.get( "a" ).get( 0 ), equalTo( "1" ) );
    }
}
