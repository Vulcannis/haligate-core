package org.haligate.core;

import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Map.Entry;

import org.hamcrest.*;

import com.google.common.collect.Multimap;

public class OurMatchers
{
	public static < K, V > Matcher< Multimap< ? extends K, ? extends V >> hasEntry( final Matcher< ? super K > keyMatcher, final Matcher< ? super V > valueMatcher )
	{
		return new IsMultimapContaining< K, V >( keyMatcher, valueMatcher );
	}

	public static < K, V > Matcher< Multimap< ? extends K, ? extends V >> hasEntry( final K key, final V value )
	{
		return new IsMultimapContaining< K, V >( equalTo( key ), equalTo( value ) );
	}

	public static < K > Matcher< Multimap< ? extends K, ? >> hasKey( final Matcher< ? super K > keyMatcher )
	{
		return new IsMultimapContaining< K, Object >( keyMatcher, anything( ) );
	}

	public static < K > Matcher< Multimap< ? extends K, ? >> hasKey( final K key )
	{
		return new IsMultimapContaining< K, Object >( equalTo( key ), anything( ) );
	}

	public static < V > Matcher< Multimap< ? , ? extends V >> hasValue( final Matcher< ? super V > valueMatcher )
	{
		return new IsMultimapContaining< Object, V >( anything( ), valueMatcher );
	}

	public static < V > Matcher< Multimap< ? , ? extends V >> hasValue( final V value )
	{
		return new IsMultimapContaining< Object, V >( anything( ), equalTo( value ) );
	}

	public static class IsMultimapContaining< K, V > extends TypeSafeMatcher< Multimap< ? extends K, ? extends V >>
	{
		private final Matcher< ? super K > keyMatcher;
		private final Matcher< ? super V > valueMatcher;

		public IsMultimapContaining( final Matcher< ? super K > keyMatcher, final Matcher< ? super V > valueMatcher )
		{
			this.keyMatcher = keyMatcher;
			this.valueMatcher = valueMatcher;
		}

		@Override
		public boolean matchesSafely( final Multimap< ? extends K, ? extends V > Multimap )
		{
			for( final Entry< ? extends K, ? extends V > entry: Multimap.entries( ) ) {
				if( keyMatcher.matches( entry.getKey( ) ) && valueMatcher.matches( entry.getValue( ) ) ) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void describeMismatchSafely( final Multimap< ? extends K, ? extends V > Multimap, final Description mismatchDescription )
		{
			mismatchDescription.
				appendText( "Multimap was " ).
				appendValueList( "[", ", ", "]", Multimap.entries( ) );
		}

		@Override
		public void describeTo( final Description description )
		{
			description.
				appendText( "Multimap containing [" ).
				appendDescriptionOf( keyMatcher ).
				appendText( "->" ).
				appendDescriptionOf( valueMatcher ).
				appendText( "]" );
		}
	}
}
