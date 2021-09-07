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

    CONSTRUCT_BUILDING(1, "ConstructBuilding"),
    FETCH_WOOD(2, "FetchWood"),
    SHAMAN_GET_WILDS(3, "EnableShamanGetWilds"),
    HOUSE_A_PERSON(4, "HouseAPerson"),
    SEND_GHOSTS(5, "SendGhosts"),
    BRING_NEW_PEOPLE_BACK(6, "BringNewPeopleBack"),
    TRAIN_PEOPLE(7, "TrainPeople"),
    POPULATE_DRUM_TOWER(8, "PopulateDrumTower"),
    DEFEND(9, "Defend"),
    DEFEND_BASE(10, "DefendBase"),
    SPELL_DEFENCE(11, "SpellDefence"),
    PREACH(12, "Preach"),
    BUILD_WALLS(13, "BuildWalls"),
    SABOTAGE(14, "Sabotage"),
    SPELL_OFFENSIVE(15, "SpellOffensive"),
    FIREWARRIOR_DEFEND(16, "FirewarriorDefend"),
    BUILD_VEHICLE(17, "BuildVehicle"),
    FETCH_LOST_PEOPLE(18, "FetchLostPeople"),
    FETCH_LOST_VEHICLE(19, "FetchLostVehicle"),
    FETCH_FAR_VEHICLE(20, "FetchFarVehicle"),
    AUTO_ATTACK(21, "AutoAttack"),

    SHAMAN_DEFEND(22, "ShamanDefend"),
    FLATTEN_BASE(23, "FlattenBase"),
    BUILD_OUTER_DEFENCES(24, "BuildOuterDefences"),
    SPARE5(25, "Spare5"),
    SPARE6(26, "Spare6"),
    SPARE7(27, "Spare7"),
    SPARE8(28, "Spare8"),
    SPARE9(29, "Spare9"),
    SPARE10(30, "Spare10"),
    COUNT_WILD(31, false, true, "COUNT_WILD"),

    ATTACK(32, "Attack"),
    ATTACK_BLUE(33, "AttackBlue"),
    ATTACK_RED(34, "AttackRed"),
    ATTACK_YELLOW(35, "AttackYellow"),
    ATTACK_GREEN(36, "AttackGreen"),
    SPELL_ATTACK(37, "SpellAttack"),

    RESET_BASE_MARKER(38, "ResetBaseMarker"),
    SET_BASE_MARKER(39, "SetBaseMarker"),
    SET_BASE_RADIUS(40, "SetBaseRadious"),
    COUNT_PEOPLE_IN_MARKER(41, "CountPeopleInMarker"),
    SET_DRUM_TOWER_POS(42, "SetDrumTowerPos"),

    ATTACK_MARKER(43, false, true, "ATTACK_MARKER"),
    ATTACK_BUILDING(44, false, true, "ATTACK_BUILDING"),
    ATTACK_PERSON(45, false, true, "ATTACK_PERSON"),
    CONVERT_AT_MARKER(46, "ConvertAtMarker"),
    PREACH_AT_MARKER(47, "PreachAtMarker"),
    SEND_GHOST_PEOPLE(48, "SendGhostPeople"),
    GET_SPELLS_CAST(49, "GetSpellsCast"),
    GET_NUM_ONE_OFF_SPELLS(50, "GetNumOneOffSpells"),
    ATTACK_NORMAL(51, false, true, "ATTACK_NORMAL"),
    ATTACK_BY_BOAT(52, false, true, "ATTACK_BY_BOAT"),
    ATTACK_BY_BALLON(53, false, true, "ATTACK_BY_BALLON"),
    SET_ATTACK_VARIABLE(54, "SetAttackVariable"),
    BUILD_DRUM_TOWER(55, "BuildDrumTower"),
    GUARD_AT_MARKER(56, "GuardAtMarker"),
    GUARD_BETWEEN_MARKERS(57, "GuardBetweenMarkers"),
    GET_HEIGHT_AT_POS(58, "GetHeightAtPos"),
    SEND_ALL_PEOPLE_TO_MARKER(59, "SendAllPeopleToMarker"),
    GUARD_NORMAL(60, false, true, "GUARD_NORMAL"),
    GUARD_WITH_GHOSTS(61, false, true, "GUARD_WITH_GHOSTS"),
    RESET_CONVERT_MARKER(62, "ResetConvertMarker"),
    SET_CONVERT_MARKER(63, "SetConvertMarker"),
    SET_MARKER_ENTRY(64, "SetMarkerEntry"),
    MARKER_ENTRIES(65, "MarkerEntries"),
    CLEAR_GUARDING_FROM(66, "ClearGuardingFrom"),
    SET_BUILDING_DIRECTION(67, "SetBuildingDirection"),
    TRAIN_PEOPLE_NOW(68, "TrainPeopleNow"),
    PRAY_AT_HEAD(69, "PrayAtHead"),
    PUT_PERSON_IN_DT(70, "PutPersonInDrumTower"),
    I_HAVE_ONE_SHOT(71, "IHaveOneShot"),
    SPELL_TYPE(72, false, true, "SPELL_TYPE"),
    BUILDING_TYPE(73, false, true, "BUILDING_TYPE"),
    BOAT_PATROL(74, "BoatPatrol"),
    DEFEND_SHAMEN(75, "DefendShamen"),
    SEND_SHAMEN_DEFENDERS_HOME(76, "SendShamenDefendersHome"),
    BOAT_TYPE(77, "BOAT_TYPE"),
    BALLON_TYPE(78, "BALLON_TYPE"),
    IS_BUILDING_NEAR(79, "IsBuildingNear"),
    BUILD_AT(80, "BuildAt"),
    SET_SPELL_ENTRY(81, "SetSpellEntry"),
    DELAY_MAIN_DRUM_TOWER(82, "DelayMainDrumTower"),
    BUILD_MAIN_DRUM_TOWER(83, "BuildMainDrumTower"),
    ZOOM_TO(84, "ZoomTo"),
    DISABLE_USER_INPUTS(85, "DisableUserInputs"),
    ENABLE_USER_INPUTS(86, "EnableUserInputs"),
    OPEN_DIALOG(87, "OpenDialog"),
    GIVE_ONE_SHOT(88, "GiveOneShot"),
    CLEAR_STANDING_PEOPLE(89, "ClearStandingPeople"),
    ONLY_STAND_AT_MARKERS(90, "OnlyStandAtMarkers"),
    BLUE(91, false, true, "TRIBE_BLUE"),
    RED(92, false, true, "TRIBE_RED"),
    YELLOW(93, false, true, "TRIBE_YELLOW"),
    GREEN(94, false, true, "TRIBE_GREEN"),
    NAV_CHECK(95, "NavCheck"),
    TARGET_S_WARRIORS(96, "TargetsFirewarriors"),
    DONT_TARGET_S_WARRIORS(97, "DontTargetFirewarriors"),
    TARGET_BLUE_SHAMAN(98, "TargetBlueShaman"),
    DONT_TARGET_BLUE_SHAMAN(99, "DontTargetBlueShaman"),
    TARGET_BLUE_DRUM_TOWERS(100, "TargetBlueDrumTowers"),
    DONT_TARGET_BLUE_DRUM_TOWERS(101, "DontTargetBlueDrumTowers"),
    HAS_BLUE_KILLED_A_GHOST(102, "HasBlueKilledAGhost"),
    COUNT_GUARD_FIRES(103, "CountGuardFires"),
    GET_HEAD_TRIGGER_COUNT(104, "GetHeadTriggerCount"),
    MOVE_SHAMAN_TO_MARKER(105, "MoveShamanToMarker"),
    TRACK_SHAMAN_TO_ANGLE(106, "TrackShamanToAngle"),
    TRACK_SHAMAN_EXTRA_BOLLOCKS(107, "TrackShamanExtraBollocks"),
    IS_SHAMAN_AVAILABLE_FOR_ATTACK(108, "IsShamanAvailableForAttack"),
    PARTIAL_BUILDING_COUNT(109, "PartialBuildingCount"),
    SEND_BLUE_PEOPLE_TO_MARKER(110, "SendBluePeopleToMarker"),
    GIVE_MANA_TO_PLAYER(111, "GiveManaToPlayer"),
    IS_PLAYER_IN_WORLD_VIEW(112, "IsPlayerInWorldView"),
    SET_AUTO_BUILD(113, "SetAutoBuild"),
    DESELECT_ALL_BLUE_PEOPLE(114, "DeselectAllBluePeople"),
    FLASH_BUTTON(115, "FlashButton"),
    TURN_PANEL_ON(116, "TurnPanelOn"),
    GIVE_PLAYER_SPELL(117, "GivePlayerSpell"),
    HAS_PLAYER_BEEN_IN_ENCYC(118, "HasPlayerBeenInEncyc"),
    IS_BLUE_SHAMAN_SELECTED(119, "IsBlueShamanSelected"),
    CLEAR_SHAMAN_LEFT_CLICK(120, "ClearShamanLeftClick"),
    CLEAR_SHAMAN_RIGHT_CLICK(121, "ClearShamanRightClick"),
    IS_SHAMAN_ICON_LEFT_CLICKED(122, "IsShamanIconLeftClicked"),
    IS_SHAMAN_ICON_RIGHT_CLICKED(123, "IsShamanIconRightClicked"),
    TRIGGER_THING(124, "TriggerThing"),
    TRACK_TO_MARKER(125, "TrackToMarker"),
    CAMERA_ROTATION(126, "CameraRotation"),
    STOP_CAMERA_ROTATION(127, "StopCameraRotation"),
    COUNT_BLUE_SHAPES(128, "CountBlueShapes"),
    COUNT_BLUE_IN_HOUSES(129, "CountBlueInHouses"),
    HAS_HOUSE_INFO_BEEN_SHOWN(130, "HasHouseInfoBeenShown"),
    CLEAR_HOUSE_INFO_FLAG(131, "ClearHouseInfoFlag"),
    SET_AUTO_HOUSE(132, "SetAutoHouse"),
    COUNT_BLUE_WITH_BUILD_COMMAND(133, "CountBlueWithBuildCommand"),
    DONT_HOUSE_SPECIALISTS(134, "DontHouseSpecialists"),
    TARGET_PLAYER_DT_AND_S(135, "TargetPlayerDrumTowerAndFirewarrior"),
    REMOVE_PLAYER_THING(136, "RemovePlayerThing"),
    SET_REINCARNATION(137, "SetReincarnation"),
    EXTRA_WOOD_COLLECTION(138, "ExtraWoodCollection"),
    SET_WOOD_COLLECTION_RADII(139, "SetWoodCollectionRadii"),
    GET_NUM_PEOPLE_CONVERTED(140, "GetNumPeopleConverted"),
    GET_NUM_PEOPLE_BEING_PREACHED(141, "GetNumPeopleBeingPreached"),

    TRIGGER_LEVEL_LOST(142, "TriggerLevelLost"),
    TRIGGER_LEVEL_WON(143, "TriggerLevelWon"),

    REMOVE_HEAD_AT_POS(144, "RemoveHeadAtPos"),
    SET_BUCKET_USAGE(145, "SetBucketUsage"),
    SET_BUCKET_COUNT_FOR_SPELL(146, "SetBucketCountForSpell"),
    CREATE_MSG_NARRATIVE(147, "CreateMsgNarrative"),
    CREATE_MSG_OBJECTIVE(148, "CreateMsgObjective"),
    CREATE_MSG_INFORMATION(149, "CreateMsgInformation"),
    CREATE_MSG_INFORMATION_ZOOM(150, "CreateMsgInformationZoom"),
    SET_MSG_ZOOM(151, "SetMsgZoom"),
    SET_MSG_TIMEOUT(152, "SetMsgTimeout"),
    SET_MSG_DELETE_ON_OK(153, "SetMsgDeleteOnOk"),
    SET_MSG_RETURN_ON_OK(154, "SetMsgReturnOnOk"),
    SET_MSG_DELETE_ON_RMB_ZOOM(155, "SetMsgDeleteOnRmbZoom"),
    SET_MSG_OPEN_DLG_ON_RMB_ZOOM(156, "SetMsgOpenDlgOnRmbZoom"),
    SET_MSG_CREATE_RETURN_MSG_ON_RMB_ZOOM(157, "SetCreateReturnMsgOnRmbZoom"),
    SET_MSG_OPEN_DLG_ON_RMB_DELETE(158, "SetMsgOpenDlgOnRmbDelete"),
    SET_MSG_ZOOM_ON_LMB_OPEN_DLG(159, "SetMsgZoomOnLmbOpenDlg"),
    SET_MSG_AUTO_OPEN_DLG(160, "SetMsgAutoOpenDlg"),
    SET_SPECIAL_NO_BLDG_PANEL(161, "SetSpecialNoBldgPanel"),
    SET_MSG_OK_SAVE_EXIT_DLG(162, "SetMsgOkSaveExitDlg"),
    FIX_WILD_IN_AREA(163, "FixWildInArea"),
    CHECK_IF_PERSON_PREACHED_TO(164, "CheckIfPersonPreachedTo"),
    COUNT_ANGELS(165, "CountAngels"),
    SET_NO_BLUE_REINC(166, "SetNoBlueReinc"),
    IS_SHAMAN_IN_AREA(167, "IsShamanInArea"),
    FORCE_TOOLTIP(168, "ForceTooltip"),
    SET_DEFENCE_RADIUS(169, "SetDefenseRadius"),
    MARVELLOUS_HOUSE_DEATH(170, "MarvellousHouseDeath"),
    CALL_TO_ARMS(171, "CallToArms"),
    DELETE_SMOKE_STUFF(172, "DeleteSmokeStuff"),
    SET_TIMER_GOING(173, "SetTimerGoing"),
    REMOVE_TIMER(174, "RemoveTimer"),
    HAS_TIMER_REACHED_ZERO(175, "HasTimerReachedZero"),
    START_REINC_NOW(176, "StartReincNow"),
    TURN_PUSH(177, "TurnPush"),
    FLYBY_CREATE_NEW(178, "FlybyCreateNew"),
    FLYBY_START(179, "FlybyStart"),
    FLYBY_STOP(180, "FlybyStop"),
    FLYBY_ALLOW_INTERRUPT(181, "FlybyAllowInterrupt"),
    FLYBY_SET_EVENT_POS(182, "FlybySetEventPos"),
    FLYBY_SET_EVENT_ANGLE(183, "FlybySetEventAngle"),
    FLYBY_SET_EVENT_ZOOM(184, "FlybySetEventZoom"),
    FLYBY_SET_EVENT_INT_POINT(185, "FlybySetEventIntPoint"),
    FLYBY_SET_EVENT_TOOLTIP(186, "FlybySetEventTooltip"),
    FLYBY_SET_END_TARGET(187, "FlybySetEndTarget"),
    FLYBY_SET_MESSAGE(188, "FlybySetMessage"),
    KILL_TEAM_IN_AREA(189, "KillTeamInArea"),
    CLEAR_ALL_MSG(190, "ClearAllMsg"),
    SET_MSG_ID(191, "SetMsgId"),
    GET_MSG_ID(192, "GetMsgId"),
    KILL_ALL_MSG_ID(193, "KillAllMsgId"),
    GIVE_UP_AND_SULK(194, "GiveUpAndSulk"),
    AUTO_MESSAGES(195, "AutoMessages"),
    IS_PRISON_ON_LEVEL(196, "IsPrisonOnLevel");

    private final UInt16 code;
    private final boolean command;
    private final boolean useCommandNumber;
    private final String langName;

    private ScriptToken(int code, boolean isCommand, boolean useCommandNumber, String name)
    {
        this.code = UInt16.valueOf(TOKEN_OFFSET + (!useCommandNumber ? 0 : NO_COMMANDS) + code);
        this.command = isCommand;
        this.useCommandNumber = useCommandNumber;
        this.langName = name;
    }
    private ScriptToken(int code, String name) { this(code, true, true, name); }
    
    public final String getLangName() { return langName; }

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
