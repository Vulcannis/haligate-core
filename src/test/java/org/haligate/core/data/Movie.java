package org.haligate.core.data;

public class Movie
{
    private String title;

    public Movie( )
    {
    }

    public Movie( final String title )
    {
        this.title = title;
    }

    public String getTitle( )
    {
        return title;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }

    @Override
    public int hashCode( )
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( title == null ? 0 : title.hashCode( ) );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if( this == obj ) {
            return true;
        }
        if( obj == null ) {
            return false;
        }
        if( getClass( ) != obj.getClass( ) ) {
            return false;
        }
        final Movie other = (Movie)obj;
        if( title == null ) {
            if( other.title != null ) {
                return false;
            }
        } else if( !title.equals( other.title ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString( )
    {
        return "Movie [title=" + title + "]";
    }
}
