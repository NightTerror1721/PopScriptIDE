package kp.le.util;

import java.util.Objects;

/**
 *
 * @author Marc
 */
public final class Pair<F, S>
{
    public final F first;
    public final S second;
    
    private Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }
    
    public static final <F, S> Pair<F, S> of(F first, S second)
    {
        return new Pair<>(first, second);
    }
    
    public static final <F, S> Pair<F, S> empty() { return of(null, null); }
    
    @Override
    public final boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o == this)
            return true;
        
        if(o instanceof Pair other)
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.first);
        hash = 97 * hash + Objects.hashCode(this.second);
        return hash;
    }
    
    @Override
    public final String toString()
    {
        return "(" + first + ", " + second + ")";
    }
}
