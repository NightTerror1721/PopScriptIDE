/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script;

import java.util.HashMap;
import static kp.ps.script.Script.NO_COMMANDS;
import static kp.ps.script.Script.TOKEN_OFFSET;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public enum ScriptToken
{
    IF(0, false, false, "if"),
    ELSE(1, false, false, "else"),
    ENDIF(2, false, false, null),
    BEGIN(3, false, false, null),
    END(4, false, false, null),
    EVERY(5, false, false, "every"),
    DO(6, false, false, null),
    SET(7, false, false, "set"),
    INCREMENT(8, false, false, "inc"),
    DECREMENT(9, false, false, "dec"),
    EXP_START(10, false, false, "exp_start"),
    EXP_END(11, false, false, "exp_end"),
    GREATER_THAN(12, false, false, ">"),
    LESS_THAN(13, false, false, "<"),
    EQUAL_TO(14, false, false, "=="),
    NOT_EQUAL_TO(15, false, false, "!="),
    GREATER_THAN_EQUAL_TO(16, false, false, ">="),
    LESS_THAN_EQUAL_TO(17, false, false, ">="),
    SCRIPT_END(19, false, false, null),
    AND(20, false, false, "&&"),
    OR(21, false, false, "||"),
    ON(22, false, false, "on"),
    OFF(23, false, false, "off"),
    COMPUTER_PLAYER(24, false, false, "computer_player"),
    MULTIPLY(25, false, false, "mul"),
    DIVIDE(26, false, false, "div"),

    CONSTRUCT_BUILDING(1),
    FETCH_WOOD(2),
    SHAMAN_GET_WILDS(3),
    HOUSE_A_PERSON(4),
    SEND_GHOSTS(5),
    BRING_NEW_PEOPLE_BACK(6),
    TRAIN_PEOPLE(7),
    POPULATE_DRUM_TOWER(8),
    DEFEND(9),
    DEFEND_BASE(10),
    SPELL_DEFENSE(11),
    PREACH(12),
    BUILD_WALLS(13),
    SABOTAGE(14),
    SPELL_OFFENSIVE(15),
    FIREWARRIOR_DEFEND(16),
    BUILD_VEHICLE(17),
    FETCH_LOST_PEOPLE(18),
    FETCH_LOST_VEHICLE(19),
    FETCH_FAR_VEHICLE(20),
    AUTO_ATTACK(21),

    SHAMAN_DEFEND(22),
    FLATTEN_BASE(23),
    BUILD_OUTER_DEFENCES(24),
    SPARE5(25),
    SPARE6(26),
    SPARE7(27),
    SPARE8(28),
    SPARE9(29),
    SPARE10(30),
    COUNT_WILD(31, false, true, null),

    ATTACK(32),
    ATTACK_BLUE(33),
    ATTACK_RED(34),
    ATTACK_YELLOW(35),
    ATTACK_GREEN(36),
    SPELL_ATTACK(37),

    RESET_BASE_MARKER(38),
    SET_BASE_MARKER(39),
    SET_BASE_RADIUS(40),
    COUNT_PEOPLE_IN_MARKER(41),
    SET_DRUM_TOWER_POS(42),

    ATTACK_MARKER(43, false, true, null),
    ATTACK_BUILDING(44, false, true, null),
    ATTACK_PERSON(45, false, true, null),
    CONVERT_AT_MARKER(46),
    PREACH_AT_MARKER(47),
    SEND_GHOST_PEOPLE(48),
    GET_SPELLS_CAST(49),
    GET_NUM_ONE_OFF_SPELLS(50),
    ATTACK_NORMAL(51, false, true, null),
    ATTACK_BY_BOAT(52, false, true, null),
    ATTACK_BY_BALLON(53, false, true, null),
    SET_ATTACK_VARIABLE(54),
    BUILD_DRUM_TOWER(55),
    GUARD_AT_MARKER(56),
    GUARD_BETWEEN_MARKERS(57),
    GET_HEIGHT_AT_POS(58),
    SEND_ALL_PEOPLE_TO_MARKER(59),
    GUARD_NORMAL(60, false, true, null),
    GUARD_WITH_GHOSTS(61, false, true, null),
    RESET_CONVERT_MARKER(62),
    SET_CONVERT_MARKER(63),
    SET_MARKER_ENTRY(64),
    MARKER_ENTRIES(65),
    CLEAR_GUARDING_FROM(66),
    SET_BUILDING_DIRECTION(67),
    TRAIN_PEOPLE_NOW(68),
    PRAY_AT_HEAD(69),
    PUT_PERSON_IN_DT(70),
    I_HAVE_ONE_SHOT(71),
    SPELL_TYPE(72),
    BUILDING_TYPE(73),
    BOAT_PATROL(74),
    DEFEND_SHAMEN(75),
    SEND_SHAMEN_DEFENDERS_HOME(76),
    BOAT_TYPE(77),
    BALLON_TYPE(78),
    IS_BUILDING_NEAR(79),
    BUILD_AT(80),
    SET_SPELL_ENTRY(81),
    DELAY_MAIN_DRUM_TOWER(82),
    BUILD_MAIN_DRUM_TOWER(83),
    ZOOM_TO(84),
    DISABLE_USER_INPUTS(85),
    ENABLE_USER_INPUTS(86),
    OPEN_DIALOG(87),
    GIVE_ONE_SHOT(88),
    CLEAR_STANDING_PEOPLE(89),
    ONLY_STAND_AT_MARKERS(90),
    BLUE(91, false, true, null),
    RED(92, false, true, null),
    YELLOW(93, false, true, null),
    GREEN(94, false, true, null),
    NAV_CHECK(95),
    TARGET_S_WARRIORS(96),
    DONT_TARGET_S_WARRIORS(97),
    TARGET_BLUE_SHAMAN(98),
    DONT_TARGET_BLUE_SHAMAN(99),
    TARGET_BLUE_DRUM_TOWERS(100),
    DONT_TARGET_BLUE_DRUM_TOWERS(101),
    HAS_BLUE_KILLED_A_GHOST(102),
    COUNT_GUARD_FIRES(103),
    GET_HEAD_TRIGGER_COUNT(104),
    MOVE_SHAMAN_TO_MARKER(105),
    TRACK_SHAMAN_TO_ANGLE(106),
    TRACK_SHAMAN_EXTRA_BOLLOCKS(107),
    IS_SHAMAN_AVAILABLE_FOR_ATTACK(108),
    PARTIAL_BUILDING_COUNT(109),
    SEND_BLUE_PEOPLE_TO_MARKER(110),
    GIVE_MANA_TO_PLAYER(111),
    IS_PLAYER_IN_WORLD_VIEW(112),
    SET_AUTO_BUILD(113),
    DESELECT_ALL_BLUE_PEOPLE(114),
    FLASH_BUTTON(115),
    TURN_PANEL_ON(116),
    GIVE_PLAYER_SPELL(117),
    HAS_PLAYER_BEEN_IN_ENCYC(118),
    IS_BLUE_SHAMAN_SELECTED(119),
    CLEAR_SHAMAN_LEFT_CLICK(120),
    CLEAR_SHAMAN_RIGHT_CLICK(121),
    IS_SHAMAN_ICON_LEFT_CLICKED(122),
    IS_SHAMAN_ICON_RIGHT_CLICKED(123),
    TRIGGER_THING(124),
    TRACK_TO_MARKER(125),
    CAMERA_ROTATION(126),
    STOP_CAMERA_ROTATION(127),
    COUNT_BLUE_SHAPES(128),
    COUNT_BLUE_IN_HOUSES(129),
    HAS_HOUSE_INFO_BEEN_SHOWN(130),
    CLEAR_HOUSE_INFO_FLAG(131),
    SET_AUTO_HOUSE(132),
    COUNT_BLUE_WITH_BUILD_COMMAND(133),
    DONT_HOUSE_SPECIALISTS(134),
    TARGET_PLAYER_DT_AND_S(135),
    REMOVE_PLAYER_THING(136),
    SET_REINCARNATION(137),
    EXTRA_WOOD_COLLECTION(138),
    SET_WOOD_COLLECTION_RADII(139),
    GET_NUM_PEOPLE_CONVERTED(140),
    GET_NUM_PEOPLE_BEING_PREACHED(141),

    TRIGGER_LEVEL_LOST(142),
    TRIGGER_LEVEL_WON(143),

    REMOVE_HEAD_AT_POS(144),
    SET_BUCKET_USAGE(145),
    SET_BUCKET_COUNT_FOR_SPELL(146),
    CREATE_MSG_NARRATIVE(147),
    CREATE_MSG_OBJECTIVE(148),
    CREATE_MSG_INFORMATION(149),
    CREATE_MSG_INFORMATION_ZOON(150),
    SET_MSG_ZOON(151),
    SET_MSG_TIMEOUT(152),
    SET_MSG_DELETE_ON_OK(153),
    SET_MSG_RETURN_ON_OK(154),
    SET_MSG_DELETE_ON_RMB_ZOOM(155),
    SET_MSG_OPEN_DLG_ON_RMB_ZOOM(156),
    SET_MSG_CREATE_RETURN_MSG_ON_RMB_ZOOM(157),
    SET_MSG_OPEN_DLG_ON_RMB_DELETE(158),
    SET_MSG_ZOOM_ON_LMB_OPEN_DLG(159),
    SET_MSG_AUTO_OPEN_DLG(160),
    SET_SPECIAL_NO_BLDG_PANEL(161),
    SET_MSG_OK_SAVE_EXIT_DLG(162),
    FIX_WILD_IN_AREA(163),
    CHECK_IF_PERSON_PREACHED_TO(164),
    COUNT_ANGELS(165),
    SET_NO_BLUE_REINC(166),
    IS_SHAMAN_IN_AREA(167),
    FORCE_TOOLTIP(168),
    SET_DEFENCE_RADIUS(169),
    MARVELLOUS_HOUSE_DEATH(170),
    CALL_TO_ARMS(171),
    DELETE_SMOKE_STUFF(172),
    SET_TIMER_GOING(173),
    REMOVE_TIMER(174),
    HAS_TIMER_REACHED_ZERO(175),
    START_REINC_NOW(176),
    TURN_PUSH(177),
    FLYBY_CREATE_NEW(178),
    FLYBY_START(179),
    FLYBY_STOP(180),
    FLYBY_ALLOW_INTERRUPT(181),
    FLYBY_SET_EVENT_POS(182),
    FLYBY_SET_EVENT_ANGLE(183),
    FLYBY_SET_EVENT_ZOOM(184),
    FLYBY_SET_EVENT_INT_POINT(185),
    FLYBY_SET_EVENT_TOOLTIP(186),
    FLYBY_SET_END_TARGET(187),
    FLYBY_SET_MESSAGE(188),
    KILL_TEAM_IN_AREA(189),
    CLEAR_ALL_MSG(190),
    SET_MSG_ID(191),
    GET_MSG_ID(192),
    KILL_ALL_MSG_ID(193),
    GIVE_UP_AND_SULK(194),
    AUTO_MESSAGES(195),
    IS_PRISION_ON_LEVEL(196);

    private final UInt16 code;
    private final boolean command;
    private final boolean useCommandNumber;

    private ScriptToken(int code, boolean isCommand, boolean useCommandNumber, String name)
    {
        this.code = UInt16.valueOf(TOKEN_OFFSET + (!useCommandNumber ? 0 : NO_COMMANDS) + code);
        this.command = isCommand;
        this.useCommandNumber = useCommandNumber;
    }
    private ScriptToken(int code) { this(code, true, true, null); }

    public final String getTokenName() { return name(); }

    public final UInt16 getCode() { return code; }

    public final boolean equalsCode(UInt16 code) { return this.code.equals(code); }

    public final boolean isCommand() { return command; }

    public final boolean useCommandNumber() { return useCommandNumber; }


    private static final HashMap<String, ScriptToken> BY_NAME = new HashMap<>();
    private static final HashMap<UInt16, ScriptToken> BY_CODE = new HashMap<>();
    static
    {
        for(ScriptToken token : values())
        {
            BY_NAME.put(token.getTokenName(), token);
            BY_CODE.put(token.getCode(), token);
        }
    }
    
    public static final ScriptToken fromName(String name) { return BY_NAME.getOrDefault(name, null); }
    public static final ScriptToken fromCode(UInt16 code) { return BY_CODE.getOrDefault(code, null); }
}
