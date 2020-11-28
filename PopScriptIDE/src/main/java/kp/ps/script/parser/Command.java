/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.HashMap;

/**
 *
 * @author Marc
 */
public final class Command extends Fragment
{
    private final CommandId id;
    
    private Command(CommandId id) { this.id = id; }
    
    public final String getCommandName() { return id.getCommandName(); }
    
    public final CommandId getCommandId() { return id; }

    @Override
    public final FragmentType getFragmentType() { return FragmentType.COMMAND; }

    @Override
    public final boolean isStatement() { return false; }

    @Override
    public final String toString() { return id.toString(); }
    
    
    private static final HashMap<CommandId, Command> BY_ID = new HashMap<>();
    private static final HashMap<String, Command> BY_NAME = new HashMap<>();
    static
    {
        for(CommandId id : CommandId.values())
        {
            Command command = new Command(id);
            BY_ID.put(command.getCommandId(), command);
            BY_NAME.put(command.getCommandName(), command);
        }
    }
    
    public static final Command fromId(CommandId id) { return BY_ID.getOrDefault(id, null); }
    public static final Command fromName(String name) { return BY_ID.getOrDefault(name, null); }
    
    public static final boolean exists(CommandId id) { return fromId(id) != null; }
    public static final boolean exists(String name) { return fromName(name) != null; }
}
