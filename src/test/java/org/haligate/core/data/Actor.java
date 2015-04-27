package org.haligate.core.data;

public class Actor
{
    private String name;

    public Actor( )
    {
    }

    public Actor( final String name )
    {
        this.name = name;
    }

    public String getName( )
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public String toString( )
    {
        return "Actor [name=" + name + "]";
    }
}
