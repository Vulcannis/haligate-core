package org.haligate.core.support;

import static com.google.common.collect.testing.Helpers.mapEntry;
import static java.util.Arrays.asList;

import java.util.*;
import java.util.Map.Entry;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.common.collect.Lists;
import com.google.common.collect.testing.*;
import com.google.common.collect.testing.features.*;

import junit.framework.TestSuite;

@RunWith( AllTests.class )
public class CaseInsensitiveForwardingMapTest
{
    private static List< String > list( final String... values )
    {
        return Lists.newArrayList( asList( values ) );
    }

    public static TestSuite suite( )
    {
        final TestSuite suite = new TestSuite( CaseInsensitiveForwardingMapTest.class.getName( ) );
        suite.addTest( MapTestSuiteBuilder.
            using( new TestMapGenerator< String, List< String > >( ) {
                @Override
                public SampleElements< Entry< String, List< String > > > samples( )
                {
                    return new SampleElements< Entry< String, List< String > > >(
                        mapEntry( "Vary", list( "1", "2" ) ),
                        mapEntry( "CACHE-CONTROL", list( "1" ) ),
                        mapEntry( "accept", list( "1", "3", "4" ) ),
                        mapEntry( "contENT-Type", list( "1", "5" ) ),
                        mapEntry( "Content-Length", list( "4" ) )
                    );
                }

                @Override
                public Map< String, List< String > > create( final Object... elements )
                {
                    final Map< String, List< String > > map = new CaseInsensitiveForwardingMap< List< String > >( );
                    for( final Object element: elements ) {
                        @SuppressWarnings( "unchecked" )
                        final Map.Entry< String, List< String > > entry = (Entry< String, List< String > >)element;
                        map.put( entry.getKey( ), entry.getValue( ) );
                    }
                    return map;
                }

                @SuppressWarnings( "unchecked" )
                @Override
                public Entry< String, List< String > >[ ] createArray( final int length )
                {
                    return new Entry[ length ];
                }

                @Override
                public Iterable< Entry< String, List< String > > > order( final List< Entry< String, List< String > > > insertionOrder )
                {
                    return insertionOrder;
                }

                @Override
                public String[ ] createKeyArray( final int length )
                {
                    return new String[ length ];
                }

                @SuppressWarnings( "unchecked" )
                @Override
                public List< String >[ ] createValueArray( final int length )
                {
                    return new List[ length ];
                }
            } ).
            named( CaseInsensitiveForwardingMapTest.class.getName( ) ).
            withFeatures(
                MapFeature.GENERAL_PURPOSE,
                MapFeature.ALLOWS_ANY_NULL_QUERIES,
                CollectionSize.ANY,
                CollectionFeature.SUPPORTS_ITERATOR_REMOVE
            ).
            createTestSuite( )
        );
        return suite;
    }
}
