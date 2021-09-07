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
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.Macro;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.types.TypeId;

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
    
    public final Namespace getChild(String name)
    {
        Namespace child = children.get(name);
        if(child == null && parent != null)
            return parent.getChild(name);
        return child;
    }
    
    public final boolean existsChild(String name)
    {
        if(children.containsKey(name))
            return true;
        return parent != null && parent.existsChild(name);
    }
    
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
    private static final HashMap<ScriptInternal, NamespaceField> INTERNAL_GLOBALS = new HashMap<>();
    private static final HashMap<ScriptToken, NamespaceField> TOKEN_GLOBALS = new HashMap<>();
    
    private static NamespaceField addGlobal(NamespaceField field)
    {
        GLOBALS.put(field.getName(), field);
        return field;
    }
    private static void addGlobalTypedValue(ScriptToken token)
    {
        addGlobalTypedValue(token.getLangName(), TypedValue.from(token));
    }
    private static void addGlobalTypedValue(String name, TypedValue value)
    {
        if(value != null)
        {
            try
            {
                NamespaceField field = addGlobal(NamespaceField.typedValue(name, value.getType()));
                field.initiateTypedValue(value);
                TOKEN_GLOBALS.put(value.getToken(), field);
            }
            catch(CompilerException ex) { throw new IllegalStateException(ex); }
        }
    }
    private static void addGlobalInternal(String name, ScriptInternal internal)
    {
        if(internal != null)
        {
            try
            {
                NamespaceField field = addGlobal(NamespaceField.internal(name));
                field.initiateInternal(internal);
                INTERNAL_GLOBALS.put(internal, field);
            }
            catch(CompilerException ex) { throw new IllegalStateException(ex); }
        }
    }
    static
    {
        // state //
        addGlobalTypedValue(ScriptToken.ON);
        addGlobalTypedValue(ScriptToken.OFF);
        
        // tribe //
        addGlobalTypedValue(ScriptToken.BLUE);
        addGlobalTypedValue(ScriptToken.RED);
        addGlobalTypedValue(ScriptToken.YELLOW);
        addGlobalTypedValue(ScriptToken.GREEN);
        
        // attack_target //
        addGlobalTypedValue(ScriptToken.ATTACK_MARKER);
        addGlobalTypedValue(ScriptToken.ATTACK_BUILDING);
        addGlobalTypedValue(ScriptToken.ATTACK_PERSON);
        
        // attack_mode //
        addGlobalTypedValue(ScriptToken.ATTACK_NORMAL);
        addGlobalTypedValue(ScriptToken.ATTACK_BY_BOAT);
        addGlobalTypedValue(ScriptToken.ATTACK_BY_BALLON);
        
        // guard_mode //
        addGlobalTypedValue(ScriptToken.GUARD_NORMAL);
        addGlobalTypedValue(ScriptToken.GUARD_WITH_GHOSTS);
        
        // count_wild_t //
        addGlobalTypedValue(ScriptToken.COUNT_WILD);
        
        // shot_type //
        addGlobalTypedValue(ScriptToken.SPELL_TYPE);
        addGlobalTypedValue(ScriptToken.BUILDING_TYPE);
        
        // vehicle_type //
        addGlobalTypedValue(ScriptToken.BOAT_TYPE);
        addGlobalTypedValue(ScriptToken.BALLON_TYPE);
        
        // action //
        for(TypedValue action : TypedValue.from(TypeId.ACTION))
            addGlobalTypedValue(action.getToken().name(), action);
        
        // int //
        for(ScriptInternal internal : ScriptInternal.values())
            addGlobalInternal(internal.name(), internal);
    }
    
    public static final NamespaceField getGlobalByInternal(ScriptInternal internal)
    {
        NamespaceField field = INTERNAL_GLOBALS.get(internal);
        if(field == null)
            throw new IllegalStateException();
        return field;
    }
    
    public static final NamespaceField getGlobalByToken(ScriptToken token)
    {
        NamespaceField field = TOKEN_GLOBALS.get(token);
        if(field == null)
            throw new IllegalStateException();
        return field;
    }
}
