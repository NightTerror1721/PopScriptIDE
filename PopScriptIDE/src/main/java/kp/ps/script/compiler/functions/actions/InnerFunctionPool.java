/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.functions.actions;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.functions.Parameter;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.utils.Utils;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public final class InnerFunctionPool
{
    private InnerFunctionPool() {}
    
    private static final LinkedHashMap<ScriptToken, InnerFunction> functions = new LinkedHashMap<>();
    private static final Map<String, InnerFunction> functionsByName;
    
    
    public static final List<InnerFunction> list()
    {
        return new LinkedList<>(functions.values());
    }
    
    public static final boolean exists(ScriptToken token) { return functions.containsKey(token); }
    public static final boolean exists(String name) { return functionsByName.containsKey(name); }
    
    public static final InnerFunction get(ScriptToken token)
    {
        InnerFunction function = functions.getOrDefault(token, null);
        if(function == null)
            throw new IllegalStateException();
        return function;
    }
    
    public static final InnerFunction get(String name)
    {
        InnerFunction function = functionsByName.getOrDefault(name, null);
        if(function == null)
            throw new IllegalStateException();
        return function;
    }
    
    private static void register(InnerFunction function)
    {
        functions.put(function.getAction(), function);
    }
    
    private static void register(ScriptToken action, boolean hasReturn, Parameter... pars)
    {
        register(new InnerFunction(action, hasReturn, pars));
    }
    
    private static Parameter state(String name, boolean isOn)
    {
        return Parameter.typed(name, TypeId.STATE, isOn ? TypedValue.from(ScriptToken.ON) : TypedValue.from(ScriptToken.OFF));
    }
    private static Parameter state(boolean isOn) { return state("enabled", isOn); }
    
    private static Parameter state(String name)
    {
        return Parameter.typed(name, TypeId.STATE);
    }
    private static Parameter state() { return state("enabled"); }
    
    private static Parameter integer(String name, int def)
    {
        return Parameter.integer(name, Int32.valueOf(def));
    }
    private static Parameter integer(String name)
    {
        return Parameter.integer(name);
    }
    
    private static Parameter typed(String name, TypeId type, ScriptToken def)
    {
        if(def == null)
            return Parameter.typed(name, type);
        return Parameter.typed(name, type, TypedValue.from(def));
    }
    private static Parameter typed(String name, TypeId type)
    {
        return typed(name, type, null);
    }
    
    private static Parameter internal(String name, ScriptInternal def)
    {
        if(def == null)
            return Parameter.internal(name);
        return Parameter.internal(name, def);
    }
    
    static
    {
        register(ScriptToken.CONSTRUCT_BUILDING, false, state());
        register(ScriptToken.FETCH_WOOD, false, state());
        register(ScriptToken.SHAMAN_GET_WILDS, false, state());
        register(ScriptToken.HOUSE_A_PERSON, false, state());
        register(ScriptToken.SEND_GHOSTS, false, state());
        register(ScriptToken.BRING_NEW_PEOPLE_BACK, false, state());
        register(ScriptToken.TRAIN_PEOPLE, false, state());
        register(ScriptToken.POPULATE_DRUM_TOWER, false, state());
        register(ScriptToken.DEFEND, false, state());
        register(ScriptToken.DEFEND_BASE, false, state());
        register(ScriptToken.PREACH, false, state());
        register(ScriptToken.BUILD_WALLS, false, state());
        register(ScriptToken.SABOTAGE, false, state());
        register(ScriptToken.SPELL_OFFENSIVE, false, state());
        register(ScriptToken.FIREWARRIOR_DEFEND, false, state());
        register(ScriptToken.BUILD_VEHICLE, false, state());
        register(ScriptToken.FETCH_LOST_PEOPLE, false, state());
        register(ScriptToken.FETCH_LOST_VEHICLE, false, state());
        register(ScriptToken.FETCH_FAR_VEHICLE, false, state());
        register(ScriptToken.AUTO_ATTACK, false, state());
        register(ScriptToken.FLATTEN_BASE, false, state());
        register(ScriptToken.BUILD_OUTER_DEFENCES, false, state());
        register(ScriptToken.SET_AUTO_BUILD, false, state());
        register(ScriptToken.SET_AUTO_HOUSE, false, state());
        register(ScriptToken.DONT_HOUSE_SPECIALISTS, false, state());
        register(ScriptToken.SET_REINCARNATION, false, state());
        register(ScriptToken.EXTRA_WOOD_COLLECTION, false, state());
        register(ScriptToken.SET_BUCKET_USAGE, false, state());
        register(ScriptToken.SET_SPECIAL_NO_BLDG_PANEL, false, state());
        register(ScriptToken.TURN_PUSH, false, state());
        register(ScriptToken.FLYBY_ALLOW_INTERRUPT, false, state());
        register(ScriptToken.GIVE_UP_AND_SULK, false, state());
        register(ScriptToken.AUTO_MESSAGES, false, state());
        register(ScriptToken.ATTACK, false,
                typed("team", TypeId.TRIBE),
                integer("num_ppl"),
                typed("target", TypeId.ATTACK_TARGET),
                integer("attack_model"),
                integer("damage"),
                internal("spell_1", ScriptInternal.NO_SPECIFIC_SPELL),
                internal("spell_2", ScriptInternal.NO_SPECIFIC_SPELL),
                internal("spell_3", ScriptInternal.NO_SPECIFIC_SPELL),
                typed("attack_type", TypeId.ATTACK_MODE, ScriptToken.ATTACK_NORMAL),
                integer("bring_back_vehicles", 0),
                integer("marker_1", -1),
                integer("marker_2", -1),
                integer("marker_3", -1));
        register(ScriptToken.SPELL_ATTACK, false,
                integer("spell"),
                integer("marker"),
                integer("direction"));
        register(ScriptToken.RESET_BASE_MARKER, false);
        register(ScriptToken.SET_BASE_MARKER, false, integer("marker"));
        register(ScriptToken.SET_BASE_RADIUS, false, integer("radius"));
        register(ScriptToken.COUNT_PEOPLE_IN_MARKER, true,
                typed("team", TypeId.TRIBE),
                integer("marker"),
                integer("radius"));
        register(ScriptToken.SET_DRUM_TOWER_POS, false, integer("x"), integer("z"));
        register(ScriptToken.CONVERT_AT_MARKER, false, integer("marker"));
        register(ScriptToken.PREACH_AT_MARKER, false, integer("marker"));
        register(ScriptToken.SEND_GHOST_PEOPLE, false, integer("num"));
        register(ScriptToken.GET_SPELLS_CAST, true,
                typed("team", TypeId.TRIBE),
                integer("spell"));
        register(ScriptToken.GET_NUM_ONE_OFF_SPELLS, true,
                typed("team", TypeId.TRIBE),
                integer("spell"));
        register(ScriptToken.SET_ATTACK_VARIABLE, false, Parameter.variable("variable"));
        register(ScriptToken.BUILD_DRUM_TOWER, false, integer("x"), integer("z"));
        register(ScriptToken.GUARD_AT_MARKER, false,
                integer("marker"),
                integer("num_braves", 0),
                integer("num_warriors", 0),
                integer("num_firewarriors", 0),
                integer("num_preachers", 0),
                typed("guard", TypeId.GUARD_MODE, ScriptToken.GUARD_NORMAL));
        register(ScriptToken.GUARD_BETWEEN_MARKERS, false,
                integer("marker_1"),
                integer("marker_2"),
                integer("num_braves", 0),
                integer("num_warriors", 0),
                integer("num_firewarriors", 0),
                integer("num_preachers", 0),
                typed("guard", TypeId.GUARD_MODE, ScriptToken.GUARD_NORMAL));
        register(ScriptToken.SPELL_DEFENCE, false, integer("x"), integer("z"), state());
        register(ScriptToken.GET_HEIGHT_AT_POS, true, integer("marker"));
        register(ScriptToken.SEND_ALL_PEOPLE_TO_MARKER, false, integer("marker"));
        register(ScriptToken.RESET_CONVERT_MARKER, false);
        register(ScriptToken.SET_CONVERT_MARKER, false, integer("marker"));
        register(ScriptToken.SET_MARKER_ENTRY, false,
                integer("entry_id"),
                integer("marker_1"),
                integer("marker_2", -1),
                integer("num_braves", 0),
                integer("num_warriors", 0),
                integer("num_firewarriors", 0),
                integer("num_preachers", 0));
        register(ScriptToken.MARKER_ENTRIES, false,
                integer("entry_1"),
                integer("entry_2", -1),
                integer("entry_3", -1),
                integer("entry_4", -1));
        register(ScriptToken.CLEAR_GUARDING_FROM, false,
                integer("entry_1"),
                integer("entry_2", -1),
                integer("entry_3", -1),
                integer("entry_4", -1));
        register(ScriptToken.SET_BUILDING_DIRECTION, false, integer("direction"));
        register(ScriptToken.TRAIN_PEOPLE_NOW, false, integer("num"), integer("model"));
        register(ScriptToken.PRAY_AT_HEAD, false, integer("num_ppl"), integer("marker"));
        register(ScriptToken.PUT_PERSON_IN_DT, false,
                integer("person_type"),
                integer("x"),
                integer("z"));
        register(ScriptToken.I_HAVE_ONE_SHOT, true,
                typed("type", TypeId.SHOT_TYPE),
                integer("model"));
        register(ScriptToken.BOAT_PATROL, false,
                integer("num_ppl"),
                integer("marker_1"),
                integer("marker_2"),
                integer("marker_3", -1),
                integer("marker_4", -1),
                typed("type_of_vehicle", TypeId.VEHICLE_TYPE, ScriptToken.BOAT_TYPE));
        register(ScriptToken.DEFEND_SHAMEN, false, integer("num_ppl"));
        register(ScriptToken.SEND_SHAMEN_DEFENDERS_HOME, false);
        register(ScriptToken.IS_BUILDING_NEAR, true,
                integer("building_model"),
                integer("x"),
                integer("z"),
                typed("team", TypeId.TRIBE),
                integer("radius"));
        register(ScriptToken.BUILD_AT, false,
                integer("x"),
                integer("z"),
                integer("building_model"),
                integer("settlement_number"));
        register(ScriptToken.SET_SPELL_ENTRY, false,
                integer("entry_id"),
                integer("spell"),
                integer("min_mana"),
                integer("frequency"),
                integer("min_ppl"),
                integer("base_spell"));
        register(ScriptToken.DELAY_MAIN_DRUM_TOWER, false);
        register(ScriptToken.BUILD_MAIN_DRUM_TOWER, false);
        register(ScriptToken.ZOOM_TO, false,
                integer("x"),
                integer("z"),
                integer("angle"));
        register(ScriptToken.DISABLE_USER_INPUTS, false);
        register(ScriptToken.ENABLE_USER_INPUTS, false);
        register(ScriptToken.OPEN_DIALOG, false, integer("index"));
        register(ScriptToken.GIVE_ONE_SHOT, false,
                integer("spell"),
                typed("team", TypeId.TRIBE));
        register(ScriptToken.CLEAR_STANDING_PEOPLE, false);
        register(ScriptToken.ONLY_STAND_AT_MARKERS, false);
        register(ScriptToken.NAV_CHECK, true,
                typed("team", TypeId.TRIBE),
                typed("type", TypeId.ATTACK_TARGET),
                integer("model"),
                integer("remember"));
        register(ScriptToken.TARGET_S_WARRIORS, false);
        register(ScriptToken.DONT_TARGET_S_WARRIORS, false);
        register(ScriptToken.TARGET_BLUE_SHAMAN, false);
        register(ScriptToken.DONT_TARGET_BLUE_SHAMAN, false);
        register(ScriptToken.TARGET_BLUE_DRUM_TOWERS, false);
        register(ScriptToken.DONT_TARGET_BLUE_DRUM_TOWERS, false);
        register(ScriptToken.HAS_BLUE_KILLED_A_GHOST, true);
        register(ScriptToken.COUNT_GUARD_FIRES, true,
                integer("x"),
                integer("z"),
                integer("rad"));
        register(ScriptToken.GET_HEAD_TRIGGER_COUNT, true, integer("x"), integer("z"));
        register(ScriptToken.MOVE_SHAMAN_TO_MARKER, false, integer("marker"));
        register(ScriptToken.TRACK_SHAMAN_TO_ANGLE, false, integer("angle"));
        register(ScriptToken.TRACK_SHAMAN_EXTRA_BOLLOCKS, false, integer("angle"));
        register(ScriptToken.IS_SHAMAN_AVAILABLE_FOR_ATTACK, true);
        register(ScriptToken.PARTIAL_BUILDING_COUNT, false);
        register(ScriptToken.SEND_BLUE_PEOPLE_TO_MARKER, false, integer("angle"));
        register(ScriptToken.GIVE_MANA_TO_PLAYER, false,
                typed("team", TypeId.TRIBE),
                integer("mana"));
        register(ScriptToken.IS_PLAYER_IN_WORLD_VIEW, true);
        register(ScriptToken.DESELECT_ALL_BLUE_PEOPLE, false);
        register(ScriptToken.FLASH_BUTTON, false, integer("id"), state());
        register(ScriptToken.TURN_PANEL_ON, false, integer("id"));
        register(ScriptToken.GIVE_PLAYER_SPELL, false,
                typed("team", TypeId.TRIBE),
                integer("spell"));
        register(ScriptToken.HAS_PLAYER_BEEN_IN_ENCYC, true);
        register(ScriptToken.IS_BLUE_SHAMAN_SELECTED, true);
        register(ScriptToken.CLEAR_SHAMAN_LEFT_CLICK, false);
        register(ScriptToken.CLEAR_SHAMAN_RIGHT_CLICK, false);
        register(ScriptToken.IS_SHAMAN_ICON_LEFT_CLICKED, true);
        register(ScriptToken.IS_SHAMAN_ICON_RIGHT_CLICKED, true);
        register(ScriptToken.TRIGGER_THING, false, integer("marker"));
        register(ScriptToken.TRACK_TO_MARKER, false, integer("marker"));
        register(ScriptToken.CAMERA_ROTATION, false, integer("angle"));
        register(ScriptToken.COUNT_BLUE_SHAPES, true);
        register(ScriptToken.COUNT_BLUE_IN_HOUSES, true);
        register(ScriptToken.HAS_HOUSE_INFO_BEEN_SHOWN, true);
        register(ScriptToken.CLEAR_HOUSE_INFO_FLAG, false);
        register(ScriptToken.COUNT_BLUE_WITH_BUILD_COMMAND, true);
        register(ScriptToken.TARGET_PLAYER_DT_AND_S, false, typed("team", TypeId.TRIBE));
        register(ScriptToken.REMOVE_PLAYER_THING, false,
                typed("team", TypeId.TRIBE),
                integer("spell_or_building_id"));
        register(ScriptToken.SET_WOOD_COLLECTION_RADII, false,
                integer("min"),
                integer("max"),
                integer("x"),
                integer("z"));
        register(ScriptToken.GET_NUM_PEOPLE_CONVERTED, false, typed("team", TypeId.TRIBE));
        register(ScriptToken.GET_NUM_PEOPLE_BEING_PREACHED, false, typed("team", TypeId.TRIBE));
        register(ScriptToken.TRIGGER_LEVEL_LOST, false);
        register(ScriptToken.TRIGGER_LEVEL_WON, false);
        register(ScriptToken.REMOVE_HEAD_AT_POS, false, integer("x"), integer("z"));
        register(ScriptToken.SET_BUCKET_COUNT_FOR_SPELL, false, integer("spell"), integer("mult"));
        register(ScriptToken.CREATE_MSG_NARRATIVE, false, integer("idx"));
        register(ScriptToken.CREATE_MSG_OBJECTIVE, false, integer("idx"));
        register(ScriptToken.CREATE_MSG_INFORMATION, false, integer("idx"));
        register(ScriptToken.CREATE_MSG_INFORMATION_ZOOM, false,
                integer("idx"),
                integer("x"),
                integer("z"),
                integer("angle"));
        register(ScriptToken.SET_MSG_ZOOM, false,
                integer("x"),
                integer("z"),
                integer("angle"));
        register(ScriptToken.SET_MSG_TIMEOUT, false, integer("idx"));
        register(ScriptToken.SET_MSG_DELETE_ON_OK, false);
        register(ScriptToken.SET_MSG_RETURN_ON_OK, false);
        register(ScriptToken.SET_MSG_DELETE_ON_RMB_ZOOM, false);
        register(ScriptToken.SET_MSG_OPEN_DLG_ON_RMB_ZOOM, false);
        register(ScriptToken.SET_MSG_CREATE_RETURN_MSG_ON_RMB_ZOOM, false);
        register(ScriptToken.SET_MSG_OPEN_DLG_ON_RMB_DELETE, false);
        register(ScriptToken.SET_MSG_ZOOM_ON_LMB_OPEN_DLG, false);
        register(ScriptToken.SET_MSG_AUTO_OPEN_DLG, false);
        register(ScriptToken.SET_MSG_OK_SAVE_EXIT_DLG, false);
        register(ScriptToken.FIX_WILD_IN_AREA, false,
                integer("x"),
                integer("z"),
                integer("rad"));
        register(ScriptToken.CHECK_IF_PERSON_PREACHED_TO, false,
                integer("arg_1"),
                integer("arg_2"),
                integer("arg_3"));
        register(ScriptToken.COUNT_ANGELS, true, typed("team", TypeId.TRIBE));
        register(ScriptToken.SET_NO_BLUE_REINC, false);
        register(ScriptToken.IS_SHAMAN_IN_AREA, true,
                typed("team", TypeId.TRIBE),
                integer("marker"),
                integer("radius"));
        register(ScriptToken.FORCE_TOOLTIP, false,
                integer("x"),
                integer("z"),
                integer("head_or_building_id"),
                integer("duration"));
        register(ScriptToken.SET_DEFENCE_RADIUS, false, integer("radius"));
        register(ScriptToken.MARVELLOUS_HOUSE_DEATH, false);
        register(ScriptToken.CALL_TO_ARMS, false);
        register(ScriptToken.DELETE_SMOKE_STUFF, false,
                integer("x"),
                integer("z"),
                integer("rad"));
        register(ScriptToken.SET_TIMER_GOING, false, integer("time"));
        register(ScriptToken.REMOVE_TIMER, false);
        register(ScriptToken.HAS_TIMER_REACHED_ZERO, true);
        register(ScriptToken.START_REINC_NOW, false);
        register(ScriptToken.FLYBY_CREATE_NEW, false);
        register(ScriptToken.FLYBY_START, false);
        register(ScriptToken.FLYBY_STOP, false);
        register(ScriptToken.FLYBY_SET_EVENT_POS, false,
                integer("x"),
                integer("z"),
                integer("start"),
                integer("duration"));
        register(ScriptToken.FLYBY_SET_EVENT_ANGLE, false,
                integer("direction"),
                integer("start"),
                integer("duration"));
        register(ScriptToken.FLYBY_SET_EVENT_ZOOM, false,
                integer("zoom"),
                integer("start"),
                integer("duration"));
        register(ScriptToken.FLYBY_SET_EVENT_INT_POINT, false,
                integer("x"),
                integer("z"),
                integer("start"),
                integer("duration"));
        register(ScriptToken.FLYBY_SET_EVENT_TOOLTIP, false,
                integer("x"),
                integer("z"),
                integer("idx"),
                integer("start"),
                integer("duration"));
        register(ScriptToken.FLYBY_SET_END_TARGET, false,
                integer("x"),
                integer("z"),
                integer("angle"),
                integer("zoom"));
        register(ScriptToken.FLYBY_SET_MESSAGE, false,
                integer("idx"),
                integer("start"));
        register(ScriptToken.KILL_TEAM_IN_AREA, false,
                integer("x"),
                integer("z"),
                integer("rad"));
        register(ScriptToken.CLEAR_ALL_MSG, false);
        register(ScriptToken.SET_MSG_ID, false, integer("id"));
        register(ScriptToken.GET_MSG_ID, true);
        register(ScriptToken.KILL_ALL_MSG_ID, false, integer("id"));
        register(ScriptToken.IS_PRISON_ON_LEVEL, true);
        
        functionsByName = functions.values().stream()
                .collect(Collectors.toMap(f -> f.getName(), Utils::self));
    }
}
