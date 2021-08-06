/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script;

import java.util.HashMap;
import static kp.ps.script.Script.INT_OFFSET;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public enum ScriptInternal
{
    GAME_TURN(0, false, true),
    MY_NUM_PEOPLE(1, false, true),
    BLUE_PEOPLE(2, false, true),
    RED_PEOPLE(3, false, true),
    YELLOW_PEOPLE(4, false, true),
    GREEN_PEOPLE(5, false, true),
    MY_NUM_KILLED_BY_HUMAN(6, false, true),
    RED_RED_KILLED_BY_HUMAN(7, false, true),
    YELLOW_RED_KILLED_BY_HUMAN(8, false, true),
    GREEN_RED_KILLED_BY_HUMAN(9, false, true),
    WILD_PEOPLE(10, false, true),
    BLUE_MANA(11, false, true),
    RED_MANA(12, false, true),
    YELLOW_MANA(13, false, true),
    GREEN_MANA(14, false, true),

    ATTR_EXPANSION(0, true, false),
    ATTR_PREF_SPY_TRAINS(1, true, false),
    ATTR_PREF_RELIGIOUS_TRAINS(2, true, false),
    ATTR_PREF_WARRIOR_TRAINS(3, true, false),
    ATTR_PREF_FIREWARRIOR_TRAINS(4, true, false),
    ATTR_PREF_SPY_PEOPLE(5, true, false),
    ATTR_PREF_RELIGIOUS_PEOPLE(6, true, false),
    ATTR_PREF_WARRIOR_PEOPLE(7, true, false),
    ATTR_PREF_FIREWARRIOR_PEOPLE(8, true, false),
    ATTR_MAX_BUILDINGS_ON_GO(9, true, false),
    ATTR_HOUSE_PERCENTAGE(10, true, false),
    ATTR_AWAY_BRAVE(11, true, false),
    ATTR_AWAY_WARRIOR(12, true, false),
    ATTR_AWAY_RELIGIOUS(13, true, false),
    ATTR_DEFENSE_RAD_INCR(14, true, false),
    ATTR_MAX_DEFENSIVE_ACTIONS(15, true, false),
    ATTR_AWAY_SPY(16, true, false),
    ATTR_AWAY_FIREWARRIOR(17, true, false),
    ATTR_ATTACK_PERCENTAGE(18, true, false),
    ATTR_AWAY_SHAMAN(19, true, false),
    ATTR_PEOPLE_PER_BOAT(20, true, false),
    ATTR_PEOPLE_PER_BALLON(21, true, false),
    ATTR_DONT_USE_BOATS(22, true, false),
    ATTR_MAX_SPY_ATTACKS(23, true, false),
    ATTR_ENEMY_SPY_MAX_STAND(24, true, false),
    ATTR_MAX_ATTACKS(25, true, false),
    ATTR_EMPTY_AT_WAYPOING(26, true, false),
    ATTR_SPY_CHECK_FREQUENCY(27, true, false),
    ATTR_RETREAT_VALUE(28, true, false),
    ATTR_BASE_UNDER_ATTACK_RETREAT(29, true, false),
    ATTR_RANDOM_BUILD_SIDE(30, true, false),
    ATTR_USE_PREACHER_FOR_DEFENSE(31, true, false),
    ATTR_SHAMEN_BLAST(32, true, false),
    ATTR_MAX_TRAIN_AT_ONCE(33, true, false),
    ATTR_GROUP_OPTION(34, true, false),
    ATTR_PREF_BOAT_HUTS(35, true, false),
    ATTR_PREF_BALLON_HUTS(36, true, false),
    ATTR_PREF_BOAT_DRIVERS(37, true, false),
    ATTR_PREF_BALLON_DRIVERS(38, true, false),
    ATTR_FIGHT_STOP_DISTANCE(39, true, false),
    ATTR_SPY_DISCOVER_CHANGE(40, true, false),
    ATTR_COUNT_PREACH_DAMAGE(41, true, false),
    ATTR_DONT_GROUP_AT_DT(42, true, false),
    ATTR_SPELL_DELAY(43, true, false),
    ATTR_DONT_DELETE_USELESS_BOAT_HOUSE(44, true, false),
    ATTR_BOAT_HOUSE_BROKEN(45, true, false),
    ATTR_DONT_AUTO_TRAIN_PREACHERS(46, true, false),
    ATTR_SPARE_6(47, true, false),

    MY_MANA(48),

    M_SPEll_BURN_COST(49),
    M_SPEll_BLAST_COST(50),
    M_SPEll_LIGHTNING_COST(51),
    M_SPEll_WHIRLMIND_COST(52),
    M_SPEll_INSECT_PLAGUE_COST(53),
    M_SPEll_INVISIBILITY_COST(54),
    M_SPEll_HYPNOTISM_COST(55),
    M_SPEll_FIRESTORM_COST(56),
    M_SPEll_GHOST_ARMY_COST(57),
    M_SPEll_EROSION_COST(58),
    M_SPEll_SWAMP_COST(59),
    M_SPEll_LAND_BRIDGE_COST(60),
    M_SPEll_ANGEL_OF_DEAD_COST(61),
    M_SPEll_EARTHQUAKE_COST(62),
    M_SPEll_FLATTEN_COST(63),
    M_SPEll_VOLCANO_COST(64),
    M_SPEll_WRATH_OF_GOD_COST(65),

    M_BUILDING_SMALL_HUT(66),
    M_BUILDING_MEDIUM_HUT(67),
    M_BUILDING_LARGE_HUT(68),
    M_BUILDING_DRUM_TOWER(69),
    M_BUILDING_TEMPLE(70),
    M_BUILDING_SPY_TRAIN(71),
    M_BUILDING_WARRIOR_TRAIN(72),
    M_BUILDING_FIREWARRIOR_TRAIN(73),
    M_BUILDING_RECONVERSION(74),
    M_BUILDING_WALL_PIECE(75),
    M_BUILDING_GATE(76),
    M_BUILDING_CURR_OE_SLOT(77),
    M_BUILDING_BOAT_HUT(78),
    M_BUILDING_BOAT_HUT_2(79),
    M_BUILDING_AIRSHIP_HUT(80),
    M_BUILDING_AIRSHIP_HUT_2(81),

    B_BUILDING_SMALL_HUT(82),
    B_BUILDING_MEDIUM_HUT(83),
    B_BUILDING_LARGE_HUT(84),
    B_BUILDING_DRUM_TOWER(85),
    B_BUILDING_TEMPLE(86),
    B_BUILDING_SPY_TRAIN(87),
    B_BUILDING_WARRIOR_TRAIN(88),
    B_BUILDING_FIREWARRIOR_TRAIN(89),
    B_BUILDING_RECONVERSION(90),
    B_BUILDING_WALL_PIECE(91),
    B_BUILDING_GATE(92),
    B_BUILDING_CURR_OE_SLOT(93),
    B_BUILDING_BOAT_HUT(94),
    B_BUILDING_BOAT_HUT_2(95),
    B_BUILDING_AIRSHIP_HUT(96),
    B_BUILDING_AIRSHIP_HUT_2(97),

    R_BUILDING_SMALL_HUT(98),
    R_BUILDING_MEDIUM_HUT(99),
    R_BUILDING_LARGE_HUT(100),
    R_BUILDING_DRUM_TOWER(101),
    R_BUILDING_TEMPLE(102),
    R_BUILDING_SPY_TRAIN(103),
    R_BUILDING_WARRIOR_TRAIN(104),
    R_BUILDING_FIREWARRIOR_TRAIN(105),
    R_BUILDING_RECONVERSION(106),
    R_BUILDING_WALL_PIECE(107),
    R_BUILDING_GATE(108),
    R_BUILDING_CURR_OE_SLOT(109),
    R_BUILDING_BOAT_HUT(110),
    R_BUILDING_BOAT_HUT_2(111),
    R_BUILDING_AIRSHIP_HUT(112),
    R_BUILDING_AIRSHIP_HUT_2(113),

    Y_BUILDING_SMALL_HUT(114),
    Y_BUILDING_MEDIUM_HUT(115),
    Y_BUILDING_LARGE_HUT(116),
    Y_BUILDING_DRUM_TOWER(117),
    Y_BUILDING_TEMPLE(118),
    Y_BUILDING_SPY_TRAIN(119),
    Y_BUILDING_WARRIOR_TRAIN(120),
    Y_BUILDING_FIREWARRIOR_TRAIN(121),
    Y_BUILDING_RECONVERSION(122),
    Y_BUILDING_WALL_PIECE(123),
    Y_BUILDING_GATE(124),
    Y_BUILDING_CURR_OE_SLOT(125),
    Y_BUILDING_BOAT_HUT(126),
    Y_BUILDING_BOAT_HUT_2(127),
    Y_BUILDING_AIRSHIP_HUT(128),
    Y_BUILDING_AIRSHIP_HUT_2(129),

    G_BUILDING_SMALL_HUT(130),
    G_BUILDING_MEDIUM_HUT(131),
    G_BUILDING_LARGE_HUT(132),
    G_BUILDING_DRUM_TOWER(133),
    G_BUILDING_TEMPLE(134),
    G_BUILDING_SPY_TRAIN(135),
    G_BUILDING_WARRIOR_TRAIN(136),
    G_BUILDING_FIREWARRIOR_TRAIN(137),
    G_BUILDING_RECONVERSION(138),
    G_BUILDING_WALL_PIECE(139),
    G_BUILDING_GATE(140),
    G_BUILDING_CURR_OE_SLOT(141),
    G_BUILDING_BOAT_HUT(142),
    G_BUILDING_BOAT_HUT_2(143),
    G_BUILDING_AIRSHIP_HUT(144),
    G_BUILDING_AIRSHIP_HUT_2(145),

    M_PERSON_BRAVE(146),
    M_PERSON_WARRIOR(147),
    M_PERSON_RELIGIOUS(148),
    M_PERSON_SPY(149),
    M_PERSON_FIREWARRIOR(150),
    M_PERSON_SHAMAN(151),

    B_PERSON_BRAVE(152),
    B_PERSON_WARRIOR(153),
    B_PERSON_RELIGIOUS(154),
    B_PERSON_SPY(155),
    B_PERSON_FIREWARRIOR(156),
    B_PERSON_SHAMAN(157),

    R_PERSON_BRAVE(158),
    R_PERSON_WARRIOR(159),
    R_PERSON_RELIGIOUS(160),
    R_PERSON_SPY(161),
    R_PERSON_FIREWARRIOR(162),
    R_PERSON_SHAMAN(163),

    Y_PERSON_BRAVE(164),
    Y_PERSON_WARRIOR(165),
    Y_PERSON_RELIGIOUS(166),
    Y_PERSON_SPY(167),
    Y_PERSON_FIREWARRIOR(168),
    Y_PERSON_SHAMAN(169),

    G_PERSON_BRAVE(170),
    G_PERSON_WARRIOR(171),
    G_PERSON_RELIGIOUS(172),
    G_PERSON_SPY(173),
    G_PERSON_FIREWARRIOR(174),
    G_PERSON_SHAMAN(175),

    BLUE_KILLED_BY_ME(176),
    RED_KILLED_BY_ME(177),
    YELLOW_KILLED_BY_ME(178),
    GREEN_KILLED_BY_ME(179),

    MY_NUM_KILLED_BY_BLUE(180),
    MY_NUM_KILLED_BY_RED(181),
    MY_NUM_KILLED_BY_YELLOW(182),
    MY_NUM_KILLED_BY_GREEN(183),

    BURN(184),
    BLAST(185),
    LIGHTNING_BOLT(186),
    WHIRLWIND(187),
    INSECT_PLAGUE(188),
    INVISIBILITY(189),
    HYPNOTISM(190),
    FIRESTORM(191),
    GHOST_ARMY(192),
    EROSION(193),
    SWAMP(194),
    LAND_BRIDGE(195),
    ANGEL_OF_DEAD(196),
    EARTHQUAKE(197),
    FLATTEN(198),
    VOLCANO(199),
    WRATH_OF_GOD(200),

    BRAVE(201),
    WARRIOR(202),
    RELIGIOUS(203),
    SPY(204),
    FIREWARRIOR(205),
    SHAMAN(206),

    SMALL_HUT(207),
    MEDIUM_HUT(208),
    LARGE_HUT(209),
    DRUM_TOWER(210),
    TEMPLE(211),
    SPY_TRAIN(212),
    WARRIOR_TRAIN(213),
    FIREWARRIOR_TRAIN(214),
    RECONVERSION(215),
    WALL_PIECE(216),
    GATE(217),
    BOAT_HUT(215),
    BOAT_HUT_2(219),
    AIRSHIP_HUT(220),
    AIRSHIP_HUT_2(221),

    NO_SPECIFIC_PERSON(222),
    NO_SPECIFIC_BUILDING(223),
    NO_SPECIFIC_SPELL(224),

    TARGET_SHAMAN(225),

    M_VEHICLE_BOAT_1(226),
    M_VEHICLE_AIRSHIP_1(227),

    B_VEHICLE_BOAT_1(228),
    B_VEHICLE_AIRSHIP_1(229),

    R_VEHICLE_BOAT_1(230),
    R_VEHICLE_AIRSHIP_1(231),

    Y_VEHICLE_BOAT_1(232),
    Y_VEHICLE_AIRSHIP_1(233),

    G_VEHICLE_BOAT_1(234),
    G_VEHICLE_AIRSHIP_1(235),

    CP_FREE_ENTRIES(236),
    RANDOM_100(237),

    NUM_SHAMEN_DEFENDERS(238),

    CAMERA_ANGLE(239),
    CAMERA_X(240),
    CAMERA_Z(241),

    M_SPELL_SHIELD_COST(242),
    SHIELD(243),
    CONVERT(244),
    TELEPORT(245),
    BLOODLUST(246);

    /*ATTACK_MARKER(0, false),
    ATTACK_BUILDING(1, false),
    ATTACK_PERSON(2, false),

    ATTACK_NORMAL(0, false),
    ATTACK_BY_BOAT(1, false),
    ATTACK_BY_BALLON(2, false),

    GUARD_NORMAL(0, false),
    GUARD_WITH_GHOSTS(1, false);*/

    private final UInt16 code;
    private final boolean constant;

    private ScriptInternal(int code, boolean useOffset, boolean isConstant)
    {
        this.code = UInt16.valueOf((useOffset ? INT_OFFSET : 0) + code);
        this.constant = isConstant;
    }
    private ScriptInternal(int code) { this(code, true, true); }

    public final String getInternalName() { return name(); }

    public final UInt16 getCode() { return code; }

    public final boolean equalsCode(UInt16 code) { return this.code.equals(code); }
    
    public final boolean isConstant() { return constant; }

    private static final HashMap<String, ScriptInternal> BY_NAME = new HashMap<>();
    private static final HashMap<UInt16, ScriptInternal> BY_CODE = new HashMap<>();
    static
    {
        for(ScriptInternal internal : values())
        {
            BY_NAME.put(internal.name(), internal);
            BY_CODE.put(internal.getCode(), internal);
            
        }
    }
    
    public static final ScriptInternal fromName(String name) { return BY_NAME.getOrDefault(name, null); }
    public static final ScriptInternal fromCode(UInt16 code) { return BY_CODE.getOrDefault(code, null); }
}
