/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Marc
 */
public final class FragmentList implements Iterable<Fragment>
{
    private final Fragment[] array;
    
    public FragmentList() { this.array = new Fragment[0]; }
    public FragmentList(Fragment... fragments)
    {
        this.array = Stream.of(fragments)
                .map(Objects::requireNonNull)
                .toArray(Fragment[]::new);
    }
    public FragmentList(Collection<Fragment> c)
    {
        this.array = c.stream()
                .map(Objects::requireNonNull)
                .toArray(Fragment[]::new);
    }
    
    public final boolean isEmpty() { return array.length <= 0; }
    public final int size() { return array.length; }
    
    public final <T extends Fragment> T get(int index) { return (T) array[index]; }
    
    public final int indexOf(Fragment fragment)
    {
        for(int i = 0; i < array.length; ++i)
            if(array[i].equals(fragment))
                return i;
        return -1;
    }
    public final int lastIndexOf(Fragment fragment)
    {
        int index = -1;
        for(int i = 0; i < array.length; ++i)
            if(array[i].equals(fragment))
                index = i;
        return index;
    }
    
    public final boolean contains(Fragment fragment) { return indexOf(fragment) >= 0; }
    
    public final Stream<Fragment> stream() { return StreamSupport.stream(spliterator(), false); }
    public final Stream<Fragment> parallelstream() { return StreamSupport.stream(spliterator(), true); }
    
    public final FragmentList concat(FragmentList other)
    {
        if(other.isEmpty())
            return this;
        if(isEmpty())
            return other;
        
        Fragment[] newarray = Arrays.copyOf(array, array.length + other.array.length);
        System.arraycopy(other.array, 0, newarray, array.length, other.array.length);
        
        return new FragmentList(newarray);
    }
    public final FragmentList concat(Fragment... fragments)
    {
        return concat(new FragmentList(fragments));
    }
    
    public final FragmentList concat(int index, FragmentList other)
    {
        if(index == 0)
            return other.concat(this);
        if(index >= array.length)
            return concat(other);
        if(other.isEmpty())
            return this;
        
        Fragment[] newarray = new Fragment[array.length + other.array.length];
        System.arraycopy(array, 0, newarray, 0, index);
        System.arraycopy(other.array, 0, newarray, index, other.array.length);
        System.arraycopy(array, index + other.array.length, newarray, index, array.length - index);
        
        return new FragmentList(newarray);
    }
    public final FragmentList concat(int index, Fragment... fragments)
    {
        return concat(index, new FragmentList(fragments));
    }
    
    public final FragmentList subList(int index, int length)
    {
        int from = Math.max(0, index);
        int to = index + Math.max(0, length);
        to = Math.min(array.length, to);
        
        if(from >= to)
            return new FragmentList();
        
        return new FragmentList(Arrays.copyOfRange(array, from, to));
    }
    public final FragmentList subList(int index)
    {
        index = Math.max(0, index);
        return subList(index, array.length - index);
    }
    
    public final FragmentList extractScope(Fragment begin, Fragment end) throws CompilerException
    {
        int inners = 0;
        int beginIndex = 0;
        int endIndex = array.length - 1;
        
        for(int i = 0; i < array.length; ++i)
        {
            Fragment frag = array[i];
            if(frag.equals(begin))
            {
                if(inners == 0)
                    beginIndex = i + 1;
                inners++;
            }
            else if(frag.equals(end))
            {
                inners--;
                if(inners == 0)
                {
                    endIndex = i - 1;
                    break;
                }
                if(inners < 0)
                    throw new CompilerException("Unexpected " + end + ".");
            }
        }
        
        return subList(beginIndex, endIndex - beginIndex + 1);
    }
    
    public final FragmentList extractUntil(Fragment end)
    {
        for(int i = 0; i < array.length; ++i)
            if(array[i].equals(end))
            {
                if(i <= 0)
                    return new FragmentList();
                return subList(0, i - 1);
            }
        
        return this;
    }
    
    public final FragmentList extractUntil(int index, Fragment end)
    {
        FragmentList list = subList(index);
        if(list.isEmpty())
            return list;
        
        return list.extractUntil(end);
    }
    
    public final FragmentList[] split(Fragment sep)
    {
        if(isEmpty())
            return new FragmentList[0];
        
        int last = 0;
        LinkedList<FragmentList> parts = new LinkedList<>();
        for(int i = 0; i < array.length; ++i)
        {
            if(array[i].equals(sep))
            {
                if(last >= i)
                    parts.add(new FragmentList());
                else
                {
                    parts.add(subList(last, i - last));
                }
                last = i + 1;
            }
        }
        
        if(last < array.length)
            parts.add(subList(last, array.length - last));
        
        if(parts.isEmpty())
            return new FragmentList[] { this };
        return parts.toArray(FragmentList[]::new);
    }

    @Override
    public final Iterator<Fragment> iterator()
    {
        return new Iterator<Fragment>()
        {
            private int it = 0;
            
            @Override
            public final boolean hasNext() { return it < array.length; }

            @Override
            public final Fragment next()
            {
                if(it >= array.length)
                    throw new NoSuchElementException();
                return array[it++];
            }
        };
    }
    
    @Override
    public final Spliterator<Fragment> spliterator()
    {
        return Spliterators.spliterator(array, Spliterator.SIZED | Spliterator.IMMUTABLE);
    }
    
    
    public final class Pointer
    {
        private int index;
        private final int limit;
        
        private Pointer(int initialIndex)
        {
            this.index = initialIndex;
            this.limit = array.length;
        }
        private Pointer() { this(0); }
        
        public final FragmentList list() { return FragmentList.this; }
        public final int increase(int times) { return index += times; }
        public final int increase() { return increase(1); }
        public final int decrease(int times) { return index -= times; }
        public final int decrease() { return decrease(1); }
        public final int index() { return index; }
        public final int finish() { return index = limit; }
        public final boolean end() { return index >= limit; }
        
        public final Fragment value() { return array[index]; }
    }
    
    public final Pointer createPointer(int index) { return new Pointer(index); }
    public final Pointer createPointer() { return new Pointer(); }
}
