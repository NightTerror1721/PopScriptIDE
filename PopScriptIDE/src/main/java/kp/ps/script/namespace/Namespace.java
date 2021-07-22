/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.HashMap;
import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.Macro;
import kp.ps.script.compiler.TypeId;

/**
 *
 * @author Marc
 */
public final class Namespace
{
    private final String name;
    private final Namespace parent;
    private final HashMap<String, Namespace> children;
    private final HashMap<String, NamespaceField> fields;
    private final HashMap<String, Macro> macros;
    
    private Namespace(String name, Namespace parent)
    {
        this.parent = parent;
        this.name = name;
        this.children = new HashMap<>();
        this.fields = new HashMap<>();
        this.macros = new HashMap<>();
    }
    
    public final String getName() { return name; }
    
    public final Namespace getParent() { return parent; }
    
    public final boolean isGlobal() { return parent == null; }
    
    public final Namespace getChild(String name) { return children.get(name); }
    
    public final boolean existsChild(String name) { return children.containsKey(name); }
    
    public final NamespaceField getField(String name)
    {
        NamespaceField field = fields.get(name);
        if(field == null)
            field = parent == null ? GLOBALS.get(name) : parent.getField(name);
        return field;
    }
    
    public final boolean existsField(String name) { return getField(name) != null; }
    
    public final Macro getMacro(String name)
    {
        Macro macro = macros.get(name);
        if(macro == null && parent != null)
            macro = parent.getMacro(name);
        return macro;
    }
    
    public final boolean existsMacro(String name) { return getMacro(name) != null; }
    
    public static final Namespace createRoot()
    {
        return new Namespace(null, null);
    }
    
    public final Namespace createChild(String name)
    {
        Namespace n = new Namespace(name, this);
        children.put(n.getName(), n);
        return n;
    }
    
    public final void addField(NamespaceField field)
    {
        fields.put(Objects.requireNonNull(field).getName(), field);
    }
    
    private String toString(boolean printGlobal)
    {
        if(parent == null)
        {
            if(printGlobal)
                return "<global>";
            return "";
        }
        return toString(printGlobal) + "." + name;
    }
    
    @Override
    public final String toString()
    {
        return toString(parent != null);
    }
    
    
    
    
    
    private static final HashMap<String, NamespaceField> GLOBALS = new HashMap<>();
    private static void addGlobal(NamespaceField field)
    {
        GLOBALS.put(field.getName(), field);
    }
    static
    {
        // state //
        addGlobal(NamespaceField.token(TypeId.STATE, "on", ScriptToken.ON));
        addGlobal(NamespaceField.token(TypeId.STATE, "off", ScriptToken.OFF));
        
        // attack_target //
        addGlobal(NamespaceField.token(TypeId.ATTACK_TARGET, "ATTACK_MARKER", ScriptToken.ATTACK_MARKER));
        addGlobal(NamespaceField.token(TypeId.ATTACK_TARGET, "ATTACK_BUILDING", ScriptToken.ATTACK_BUILDING));
        addGlobal(NamespaceField.token(TypeId.ATTACK_TARGET, "ATTACK_PERSON", ScriptToken.ATTACK_PERSON));
        
        // attack_mode //
        addGlobal(NamespaceField.token(TypeId.ATTACK_MODE, "ATTACK_NORMAL", ScriptToken.ATTACK_NORMAL));
        addGlobal(NamespaceField.token(TypeId.ATTACK_MODE, "ATTACK_BY_BOAT", ScriptToken.ATTACK_BY_BOAT));
        addGlobal(NamespaceField.token(TypeId.ATTACK_MODE, "ATTACK_BY_BALLON", ScriptToken.ATTACK_BY_BALLON));
        
        // guard_mode //
        addGlobal(NamespaceField.token(TypeId.GUARD_MODE, "GUARD_NORMAL", ScriptToken.GUARD_NORMAL));
        addGlobal(NamespaceField.token(TypeId.GUARD_MODE, "GUARD_WITH_GHOSTS", ScriptToken.GUARD_WITH_GHOSTS));
        
        // count_wild_t //
        addGlobal(NamespaceField.token(TypeId.COUNT_WILD_T, "COUNT_WILD", ScriptToken.COUNT_WILD));
        
        // action //
        for(ScriptToken token : ScriptToken.values())
            if(token.isCommand())
                addGlobal(NamespaceField.token(TypeId.ACTION, token.name(), token));
        
        // int //
        for(ScriptInternal internal : ScriptInternal.values())
            addGlobal(NamespaceField.internal(internal.name(), internal));
    }
}
