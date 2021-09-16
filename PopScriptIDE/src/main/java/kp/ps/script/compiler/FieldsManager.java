/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import kp.ps.script.Script;
import kp.ps.script.ScriptField;
import kp.ps.script.ScriptInternal;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class FieldsManager
{
    private int fieldCount;
    private int varCount;
    
    private final ScriptField[] fields = new ScriptField[Script.MAX_FIELDS];
    
    private final FieldLocation[] vars = new FieldLocation[Script.MAX_VARS];
    private final HashMap<ScriptInternal, FieldLocation> internals = new HashMap<>();
    private final HashMap<Int32, FieldLocation> constants = new HashMap<>();
    
    private final UnusedVariables unusedVars = new UnusedVariables();
    
    final void clear()
    {
        fieldCount = 0;
        varCount = 0;
        
        for(int i = 0; i < fields.length; ++i)
            fields[i] = null;
        
        for(int i = 0; i < vars.length; ++i)
            vars[i] = null;
        
        internals.clear();
        constants.clear();
        unusedVars.clear();
    }
    
    private void checkMoreSpace() throws CompilerException
    {
        if(fieldCount >= Script.MAX_FIELDS)
            throw new CompilerException("Cannot allocate more fields.");
    }
    
    private void checkVarsMoreSpace() throws CompilerException
    {
        if(varCount >= Script.MAX_VARS)
            throw new CompilerException("Cannot allocate more variables.");
    }
    
    private FieldLocation allocate() throws CompilerException
    {
        checkMoreSpace();
        return new FieldLocation(fieldCount++);
    }
    
    public final FieldLocation registerConstant(Int32 value) throws CompilerException
    {
        if(constants.containsKey(value))
            return constants.get(value);
        
        FieldLocation location = allocate();
        ScriptField field = ScriptField.constant(value);
        fields[location.location] = field;
        constants.put(value, location);
        return location;
    }
    
    public final FieldLocation registerInternal(ScriptInternal internal) throws CompilerException
    {
        if(internals.containsKey(internal))
            return internals.get(internal);
        
        FieldLocation location = allocate();
        ScriptField field = ScriptField.internal(internal.getCode());
        fields[location.location] = field;
        internals.put(internal, location);
        return location;
    }
    
    public final VariableIndex newVariable() throws CompilerException
    {
        if(!unusedVars.isEmpty())
            return unusedVars.pop();
        
        checkVarsMoreSpace();
        FieldLocation location = allocate();
        VariableIndex index = new VariableIndex(varCount++);
        ScriptField field = ScriptField.user(index.index);
        fields[location.location] = field;
        vars[index.index] = location;
        return index;
    }
    
    public final FieldLocation getVariableFieldLocation(VariableIndex index)
    {
        if(index.index >= varCount)
            throw new IllegalStateException("Variable index not found.");
        return vars[index.index];
    }
    
    public final ScriptField getField(FieldLocation location)
    {
        if(location.location >= fieldCount)
            throw new IllegalStateException("Field location not found.");
        return fields[location.location];
    }
    
    public final void popVariables(VariableIndex... indices)
    {
        if(indices != null && indices.length > 0)
            for(VariableIndex index : indices)
                unusedVars.push(index);
    }
    
    public final void insertToScript(Script script)
    {
        script.insertFields(0, fields, 0, fieldCount);
    }
    
    
    public static final class VariableIndex implements Comparable<VariableIndex>
    {
        private final int index;
        
        private VariableIndex(int index) { this.index = index; }
        
        @Override
        public final boolean equals(Object o)
        {
            if(this == o)
                return true;
            
            if(o == null)
                return false;
            
            if(o instanceof VariableIndex)
                return index == ((VariableIndex) o).index;
            return false;
        }

        @Override
        public final int hashCode()
        {
            int hash = 5;
            hash = 97 * hash + this.index;
            return hash;
        }

        @Override
        public final int compareTo(VariableIndex o)
        {
            return Integer.compare(index, o.index);
        }
    }
    
    public static final class FieldLocation implements Comparable<FieldLocation>
    {
        private final int location;
        
        private FieldLocation(int location) { this.location = location; }
        
        public final int getLocation() { return location; }
        
        @Override
        public final boolean equals(Object o)
        {
            if(this == o)
                return true;
            
            if(o == null)
                return false;
            
            if(o instanceof FieldLocation)
                return location == ((FieldLocation) o).location;
            return false;
        }

        @Override
        public final int hashCode()
        {
            int hash = 3;
            hash = 73 * hash + this.location;
            return hash;
        }

        @Override
        public final int compareTo(FieldLocation o)
        {
            return Integer.compare(location, o.location);
        }
    }
    
    private final class UnusedVariables
    {
        private final LinkedList<Integer> list = new LinkedList<>();
        private final HashSet<Integer> set = new HashSet<>();
        
        public final void push(VariableIndex index)
        {
            if(index.index >= varCount)
                throw new IllegalStateException("Variable index not found.");
            
            if(set.contains(index.index))
                throw new IllegalStateException();
            
            list.add(index.index);
            set.add(index.index);
        }
        
        public final VariableIndex pop()
        {
            if(list.isEmpty())
                throw new IllegalStateException();
            
            Integer idx = list.pop();
            set.remove(idx);
            return new VariableIndex(idx);
        }
        
        public final boolean isEmpty() { return list.isEmpty(); }
        
        public final void clear()
        {
            list.clear();
            set.clear();
        }
    }
}
