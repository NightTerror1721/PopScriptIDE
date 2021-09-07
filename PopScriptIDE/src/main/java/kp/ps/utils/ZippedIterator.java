/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils;

import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author Marc
 */
public class ZippedIterator<L, R> implements Iterator<Pair<L, R>>
{
    private final Iterator<L> left;
    private final Iterator<R> right;
    
    public ZippedIterator(Iterator<L> left, Iterator<R> right)
    {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    @Override
    public boolean hasNext()
    {
        return left.hasNext() && right.hasNext();
    }

    @Override
    public Pair<L, R> next()
    {
        return Pair.of(left.next(), right.next());
    }
}
