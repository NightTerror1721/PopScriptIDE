/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import kp.ps.script.instruction.Instruction;

/**
 *
 * @author mpasc
 */
public final class Scope extends Fragment implements Iterable<Instruction>
{
    public static final Scope EMPTY_SCOPE = new Scope();
    
    private final Instruction[] instructions;
    
    public Scope()
    {
        this.instructions = new Instruction[0];
    }
    public Scope(Instruction... insts)
    {
        this.instructions = Arrays.copyOf(insts, insts.length);
    }
    public Scope(Collection<Instruction> insts)
    {
        this.instructions = insts.toArray(Instruction[]::new);
    }
    
    public final Instruction[] getInstructions()
    {
        return Arrays.copyOf(instructions, instructions.length);
    }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.SCOPE; }

    @Override
    public final boolean isStatement() { return false; }

    @Override
    public final String toString()
    {
        return null;
    }

    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Scope)
            return Arrays.equals(instructions, ((Scope) o).instructions);
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + Arrays.deepHashCode(this.instructions);
        return hash;
    }

    @Override
    public Iterator<Instruction> iterator()
    {
        return new Iterator<Instruction>()
        {
            private int it = 0;
            
            @Override
            public boolean hasNext() { return it < instructions.length; }

            @Override
            public final Instruction next()
            {
                if(!hasNext())
                    throw new NoSuchElementException();
                return instructions[it++];
            }
        };
    }
    
}
