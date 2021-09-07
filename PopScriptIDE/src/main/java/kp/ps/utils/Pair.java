/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import java.util.Objects;

/**
 *
 * @author Marc
 * @param <L>
 * @param <R>
 */
public final class Pair<L, R>
{
    public final L left;
    public final R right;
    
    private Pair(L left, R right)
    {
        this.left = left;
        this.right = right;
    }
    
    public static final <L, R> Pair<L, R> of(L left, R right)
    {
        return new Pair<>(left, right);
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(o == this)
            return true;
        
        if(o == null)
            return false;
        
        if(o instanceof Pair<?, ?>)
        {
            Pair<?, ?> other = (Pair<?, ?>) o;
            return Objects.equals(left, other.left) && Objects.equals(right, other.right);
        }
        
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.left);
        hash = 29 * hash + Objects.hashCode(this.right);
        return hash;
    }
}
