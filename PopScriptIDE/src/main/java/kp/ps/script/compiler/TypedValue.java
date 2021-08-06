/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kp.ps.script.ScriptToken;

/**
 *
 * @author Marc
 */
public final class TypedValue
{
    private final TypeId type;
    private final ScriptToken token;
    
    private TypedValue(TypeId type, ScriptToken token)
    {
        this.type = Objects.requireNonNull(type);
        this.token = Objects.requireNonNull(token);
    }
    
    public final TypeId getType() { return type; }
    public final ScriptToken getToken() { return token; }
    
    public final boolean equals(TypedValue other)
    {
        return type == other.type && token == other.token;
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof TypedValue)
        {
            TypedValue other = (TypedValue) o;
            return type == other.type && token == other.token;
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + Objects.hashCode(this.token);
        return hash;
    }
    
    
    private static final TypedValue[][] ALL = init();
    private static final Map<ScriptToken, TypedValue> BY_TOKEN = initMap();
    
    private static TypedValue[][] init()
    {
        TypeId[] types = TypeId.values();
        TypedValue[][] values = new TypedValue[types.length][];
        
        values[TypeId.INT.ordinal()] = new TypedValue[] {};
        values[TypeId.STATE.ordinal()] = create(TypeId.STATE,
                ScriptToken.ON,
                ScriptToken.OFF
        );
        values[TypeId.TRIBE.ordinal()] = create(TypeId.TRIBE,
                ScriptToken.BLUE,
                ScriptToken.RED,
                ScriptToken.YELLOW,
                ScriptToken.GREEN
        );
        values[TypeId.ATTACK_TARGET.ordinal()] = create(TypeId.ATTACK_TARGET,
                ScriptToken.ATTACK_MARKER,
                ScriptToken.ATTACK_BUILDING,
                ScriptToken.ATTACK_PERSON
        );
        values[TypeId.ATTACK_MODE.ordinal()] = create(TypeId.ATTACK_MODE,
                ScriptToken.ATTACK_NORMAL,
                ScriptToken.ATTACK_BY_BOAT,
                ScriptToken.ATTACK_BY_BALLON
        );
        values[TypeId.GUARD_MODE.ordinal()] = create(TypeId.GUARD_MODE,
                ScriptToken.GUARD_NORMAL,
                ScriptToken.GUARD_WITH_GHOSTS
        );
        values[TypeId.COUNT_WILD_T.ordinal()] = create(TypeId.COUNT_WILD_T,
                ScriptToken.COUNT_WILD
        );
        values[TypeId.ACTION.ordinal()] = createActions(TypeId.ACTION);
        
        return values;
    }
    
    private static Map<ScriptToken, TypedValue> initMap()
    {
        return Stream.of(ALL)
                .flatMap(Stream::of)
                .collect(Collectors.toMap(TypedValue::getToken, v -> v));
    }
    
    private static TypedValue[] create(TypeId type, ScriptToken... tokens)
    {
        if(tokens == null || tokens.length < 1)
            return new TypedValue[] {};
        
        return Stream.of(tokens)
                .map(t -> new TypedValue(type, t))
                .toArray(TypedValue[]::new);
    }
    
    private static TypedValue[] createActions(TypeId type)
    {
        return Stream.of(ScriptToken.values())
                .filter(ScriptToken::isCommand)
                .map(t -> new TypedValue(type, t))
                .toArray(TypedValue[]::new);
    }
    
    public static final TypedValue[] from(TypeId type)
    {
        int id = type.ordinal();
        if(id < 0 || id >= ALL.length)
            return new TypedValue[] {};
        
        return Arrays.copyOf(ALL[id], ALL[id].length);
    }
    
    public static final TypedValue from(ScriptToken token)
    {
        return BY_TOKEN.getOrDefault(token, null);
    }
}
