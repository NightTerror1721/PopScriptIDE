/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;
import kp.ps.script.Script;
import kp.ps.script.ScriptField;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
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
    private final GlobalVariables globalVars = new GlobalVariables();
    
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
    
    private VariableIndex newVariable(boolean isGlobal) throws CompilerException
    {
        if(!isGlobal)
        {
            if(!unusedVars.isEmpty())
                return unusedVars.pop();
        }
        
        checkVarsMoreSpace();
        FieldLocation location = allocate();
        VariableIndex index = new VariableIndex(varCount++);
        ScriptField field = ScriptField.user(index.index);
        fields[location.location] = field;
        vars[index.index] = location;
        return index;
    }
    
    public final VariableIndex newVariable() throws CompilerException
    {
        return newVariable(false);
    }
    
    public final VariableIndex newGlobalVariable(String name, Int32 initialValue) throws CompilerException
    {
        return globalVars.register(name, initialValue);
    }
    public final VariableIndex newGlobalVariable(String name) throws CompilerException
    {
        return newGlobalVariable(name, null);
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
            Stream.of(indices)
                    .filter(Predicate.not(globalVars::isGlobal))
                    .forEach(unusedVars::push);
    }
    
    public final void insertToScript(Script script)
    {
        script.insertFields(0, fields, 0, fieldCount);
    }
    
    public final void compileGlobalInitVariables(CompilerState state, CodeManager initCode, ErrorList errors) throws CompilerException
    {
        globalVars.compile(state, initCode, errors);
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
        
        public final boolean isUnused(VariableIndex index)
        {
            return set.contains(index.index);
        }
    }
    
    private final class GlobalVariables
    {
        private final HashMap<String, GlobalVariableData> vars = new HashMap<>();
        private final HashSet<Integer> indicesSet = new HashSet<>();
        
        public final VariableIndex register(String name, Int32 initValue) throws CompilerException
        {
            GlobalVariableData var = vars.getOrDefault(name, null);
            if(var == null)
            {
                var = new GlobalVariableData(newVariable(true).index);
                vars.put(name, var);
                indicesSet.add(var.variableIndex);
            }
            
            if(initValue != null)
            {
                if(var.initValue != null)
                    throw new CompilerException("Global variable '%s' has already init value.", name);
                var.initValue = initValue;
            }
            
            return new VariableIndex(var.variableIndex);
        }
        
        public final void compile(CompilerState state, CodeManager initCode, ErrorList errors) throws CompilerException
        {
            for(GlobalVariableData var : vars.values())
            {
                if(var.initValue != null)
                {
                    if(state.isStrictModeEnabled())
                        throw new CompilerException("Cannot use 'global init' command in 'strict' mode.");
                    
                    initCode.insertTokenCode(ScriptToken.SET);
                    initCode.insertFieldCode(getVariableFieldLocation(new VariableIndex(var.variableIndex)));
                    initCode.insertFieldCode(registerConstant(var.initValue));
                }
            }
        }
        
        public final boolean isGlobal(VariableIndex index)
        {
            return indicesSet.contains(index.index);
        }
        
        private final class GlobalVariableData
        {
            private final int variableIndex;
            private Int32 initValue;
            
            private GlobalVariableData(int variableIndex)
            {
                this.variableIndex = variableIndex;
                this.initValue = null;
            }
        }
    }
}
