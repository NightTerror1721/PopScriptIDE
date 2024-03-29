/*
 * Generated on 9/28/21, 12:19 PM
 */
package kp.ps.editor;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * 
 */
%%

%public
%class PopScriptHighlight
%extends AbstractJFlexCTokenMaker
%unicode
/* Case sensitive */
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public PopScriptHighlight() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * {@inheritDoc}
	 */
	public String[] getLineCommentStartAndEnd(int languageIndex) {
		return new String[] { "//", null };
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
						case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;

			/* No documentation comments */
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new TokenImpl();
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

Letter							= [A-Za-z]
LetterOrUnderscore				= ({Letter}|"_")
NonzeroDigit						= [1-9]
Digit							= ("0"|{NonzeroDigit})
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
EscapedSourceCharacter				= ("u"{HexDigit}{HexDigit}{HexDigit}{HexDigit})
Escape							= ("\\"(([btnfr\"'\\])|([0123]{OctalDigit}?{OctalDigit}?)|({OctalDigit}{OctalDigit}?)|{EscapedSourceCharacter}))
NonSeparator						= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierStart					= ({LetterOrUnderscore}|"$")
IdentifierPart						= ({IdentifierStart}|{Digit}|("\\"{EscapedSourceCharacter}))

LineTerminator				= (\n)
WhiteSpace				= ([ \t\f]+)

CharLiteral	= ([\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\'])
UnclosedCharLiteral			= ([\'][^\'\n]*)
ErrorCharLiteral			= ({UnclosedCharLiteral}[\'])
StringLiteral				= ([\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])

MLCBegin					= "/*"
MLCEnd					= "*/"

/* No documentation comments */
LineCommentBegin			= "//"

IntegerLiteral			= ({Digit}+)
/* No hex literals */
/* No float literals */
ErrorNumberFormat			= (({IntegerLiteral}){NonSeparator}+)


Separator					= ([\(\)\{\}\[\]])
Separator2				= ([\;,.])

Identifier				= ({IdentifierStart}{IdentifierPart}*)

URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)


/* No string state */
/* No char state */
%state MLC
/* No documentation comment state */
%state EOL_COMMENT

%%

<YYINITIAL> {

	/* Keywords */
	"const" |
"else" |
"every" |
"if" |
"import" |
"init" |
"internal" |
"macro" |
"main" |
"namespace" |
"off" |
"on" |
"var" |
"strict" |
"global" |
"yield"		{ addToken(Token.RESERVED_WORD); }

	/* Keywords 2 (just an optional set of keywords colored differently) */
	"AIRSHIP_HUT" |
"AIRSHIP_HUT_2" |
"ANGEL_OF_DEAD" |
"ATTACK_BUILDING" |
"ATTACK_BY_BALLON" |
"ATTACK_BY_BOAT" |
"ATTACK_MARKER" |
"ATTACK_NORMAL" |
"ATTACK_PERSON" |
"ATTR_ATTACK_PERCENTAGE" |
"ATTR_AWAY_BRAVE" |
"ATTR_AWAY_FIREWARRIOR" |
"ATTR_AWAY_RELIGIOUS" |
"ATTR_AWAY_SHAMAN" |
"ATTR_AWAY_SPY" |
"ATTR_AWAY_WARRIOR" |
"ATTR_BASE_UNDER_ATTACK_RETREAT" |
"ATTR_BOAT_HOUSE_BROKEN" |
"ATTR_COUNT_PREACH_DAMAGE" |
"ATTR_DEFENSE_RAD_INCR" |
"ATTR_DONT_AUTO_TRAIN_PREACHERS" |
"ATTR_DONT_DELETE_USELESS_BOAT_HOUSE" |
"ATTR_DONT_GROUP_AT_DT" |
"ATTR_DONT_USE_BOATS" |
"ATTR_EMPTY_AT_WAYPOING" |
"ATTR_ENEMY_SPY_MAX_STAND" |
"ATTR_EXPANSION" |
"ATTR_FIGHT_STOP_DISTANCE" |
"ATTR_GROUP_OPTION" |
"ATTR_HOUSE_PERCENTAGE" |
"ATTR_MAX_ATTACKS" |
"ATTR_MAX_BUILDINGS_ON_GO" |
"ATTR_MAX_DEFENSIVE_ACTIONS" |
"ATTR_MAX_SPY_ATTACKS" |
"ATTR_MAX_TRAIN_AT_ONCE" |
"ATTR_PEOPLE_PER_BALLON" |
"ATTR_PEOPLE_PER_BOAT" |
"ATTR_PREF_BALLON_DRIVERS" |
"ATTR_PREF_BALLON_HUTS" |
"ATTR_PREF_BOAT_DRIVERS" |
"ATTR_PREF_BOAT_HUTS" |
"ATTR_PREF_FIREWARRIOR_PEOPLE" |
"ATTR_PREF_FIREWARRIOR_TRAINS" |
"ATTR_PREF_RELIGIOUS_PEOPLE" |
"ATTR_PREF_RELIGIOUS_TRAINS" |
"ATTR_PREF_SPY_PEOPLE" |
"ATTR_PREF_SPY_TRAINS" |
"ATTR_PREF_WARRIOR_PEOPLE" |
"ATTR_PREF_WARRIOR_TRAINS" |
"ATTR_RANDOM_BUILD_SIDE" |
"ATTR_RETREAT_VALUE" |
"ATTR_SHAMEN_BLAST" |
"ATTR_SPARE_6" |
"ATTR_SPELL_DELAY" |
"ATTR_SPY_CHECK_FREQUENCY" |
"ATTR_SPY_DISCOVER_CHANGE" |
"ATTR_USE_PREACHER_FOR_DEFENSE" |
"BALLON_TYPE" |
"BLAST" |
"BLOODLUST" |
"BLUE_KILLED_BY_ME" |
"BLUE_MANA" |
"BLUE_PEOPLE" |
"BOAT_HUT" |
"BOAT_HUT_2" |
"BOAT_TYPE" |
"BRAVE" |
"BUILDING_TYPE" |
"BURN" |
"B_BUILDING_AIRSHIP_HUT" |
"B_BUILDING_AIRSHIP_HUT_2" |
"B_BUILDING_BOAT_HUT" |
"B_BUILDING_BOAT_HUT_2" |
"B_BUILDING_CURR_OE_SLOT" |
"B_BUILDING_DRUM_TOWER" |
"B_BUILDING_FIREWARRIOR_TRAIN" |
"B_BUILDING_GATE" |
"B_BUILDING_LARGE_HUT" |
"B_BUILDING_MEDIUM_HUT" |
"B_BUILDING_RECONVERSION" |
"B_BUILDING_SMALL_HUT" |
"B_BUILDING_SPY_TRAIN" |
"B_BUILDING_TEMPLE" |
"B_BUILDING_WALL_PIECE" |
"B_BUILDING_WARRIOR_TRAIN" |
"B_PERSON_BRAVE" |
"B_PERSON_FIREWARRIOR" |
"B_PERSON_RELIGIOUS" |
"B_PERSON_SHAMAN" |
"B_PERSON_SPY" |
"B_PERSON_WARRIOR" |
"B_VEHICLE_AIRSHIP_1" |
"B_VEHICLE_BOAT_1" |
"CAMERA_ANGLE" |
"CAMERA_X" |
"CAMERA_Z" |
"CONVERT" |
"COUNT_WILD" |
"CP_FREE_ENTRIES" |
"DRUM_TOWER" |
"EARTHQUAKE" |
"EROSION" |
"FIRESTORM" |
"FIREWARRIOR" |
"FIREWARRIOR_TRAIN" |
"FLATTEN" |
"GAME_TURN" |
"GATE" |
"GHOST_ARMY" |
"GREEN_KILLED_BY_HUMAN" |
"GREEN_KILLED_BY_ME" |
"GREEN_MANA" |
"GREEN_PEOPLE" |
"GUARD_NORMAL" |
"GUARD_WITH_GHOSTS" |
"G_BUILDING_AIRSHIP_HUT" |
"G_BUILDING_AIRSHIP_HUT_2" |
"G_BUILDING_BOAT_HUT" |
"G_BUILDING_BOAT_HUT_2" |
"G_BUILDING_CURR_OE_SLOT" |
"G_BUILDING_DRUM_TOWER" |
"G_BUILDING_FIREWARRIOR_TRAIN" |
"G_BUILDING_GATE" |
"G_BUILDING_LARGE_HUT" |
"G_BUILDING_MEDIUM_HUT" |
"G_BUILDING_RECONVERSION" |
"G_BUILDING_SMALL_HUT" |
"G_BUILDING_SPY_TRAIN" |
"G_BUILDING_TEMPLE" |
"G_BUILDING_WALL_PIECE" |
"G_BUILDING_WARRIOR_TRAIN" |
"G_PERSON_BRAVE" |
"G_PERSON_FIREWARRIOR" |
"G_PERSON_RELIGIOUS" |
"G_PERSON_SHAMAN" |
"G_PERSON_SPY" |
"G_PERSON_WARRIOR" |
"G_VEHICLE_AIRSHIP_1" |
"G_VEHICLE_BOAT_1" |
"HYPNOTISM" |
"INSECT_PLAGUE" |
"INVISIBILITY" |
"LAND_BRIDGE" |
"LARGE_HUT" |
"LIGHTNING_BOLT" |
"MEDIUM_HUT" |
"MY_MANA" |
"MY_NUM_KILLED_BY_BLUE" |
"MY_NUM_KILLED_BY_GREEN" |
"MY_NUM_KILLED_BY_HUMAN" |
"MY_NUM_KILLED_BY_RED" |
"MY_NUM_KILLED_BY_YELLOW" |
"MY_NUM_PEOPLE" |
"M_BUILDING_AIRSHIP_HUT" |
"M_BUILDING_AIRSHIP_HUT_2" |
"M_BUILDING_BOAT_HUT" |
"M_BUILDING_BOAT_HUT_2" |
"M_BUILDING_CURR_OE_SLOT" |
"M_BUILDING_DRUM_TOWER" |
"M_BUILDING_FIREWARRIOR_TRAIN" |
"M_BUILDING_GATE" |
"M_BUILDING_LARGE_HUT" |
"M_BUILDING_MEDIUM_HUT" |
"M_BUILDING_RECONVERSION" |
"M_BUILDING_SMALL_HUT" |
"M_BUILDING_SPY_TRAIN" |
"M_BUILDING_TEMPLE" |
"M_BUILDING_WALL_PIECE" |
"M_BUILDING_WARRIOR_TRAIN" |
"M_PERSON_BRAVE" |
"M_PERSON_FIREWARRIOR" |
"M_PERSON_RELIGIOUS" |
"M_PERSON_SHAMAN" |
"M_PERSON_SPY" |
"M_PERSON_WARRIOR" |
"M_SPELL_SHIELD_COST" |
"M_SPELL_ANGEL_OF_DEAD_COST" |
"M_SPELL_BLAST_COST" |
"M_SPELL_BURN_COST" |
"M_SPELL_EARTHQUAKE_COST" |
"M_SPELL_EROSION_COST" |
"M_SPELL_FIRESTORM_COST" |
"M_SPELL_FLATTEN_COST" |
"M_SPELL_GHOST_ARMY_COST" |
"M_SPELL_HYPNOTISM_COST" |
"M_SPELL_INSECT_PLAGUE_COST" |
"M_SPELL_INVISIBILITY_COST" |
"M_SPELL_LAND_BRIDGE_COST" |
"M_SPELL_LIGHTNING_COST" |
"M_SPELL_SWAMP_COST" |
"M_SPELL_VOLCANO_COST" |
"M_SPELL_TORNADO_COST" |
"M_SPELL_WRATH_OF_GOD_COST" |
"M_VEHICLE_AIRSHIP_1" |
"M_VEHICLE_BOAT_1" |
"NO_SPECIFIC_BUILDING" |
"NO_SPECIFIC_PERSON" |
"NO_SPECIFIC_SPELL" |
"NUM_SHAMEN_DEFENDERS" |
"RANDOM_100" |
"RECONVERSION" |
"RED_KILLED_BY_HUMAN" |
"RED_KILLED_BY_ME" |
"RED_MANA" |
"RED_PEOPLE" |
"RELIGIOUS" |
"R_BUILDING_AIRSHIP_HUT" |
"R_BUILDING_AIRSHIP_HUT_2" |
"R_BUILDING_BOAT_HUT" |
"R_BUILDING_BOAT_HUT_2" |
"R_BUILDING_CURR_OE_SLOT" |
"R_BUILDING_DRUM_TOWER" |
"R_BUILDING_FIREWARRIOR_TRAIN" |
"R_BUILDING_GATE" |
"R_BUILDING_LARGE_HUT" |
"R_BUILDING_MEDIUM_HUT" |
"R_BUILDING_RECONVERSION" |
"R_BUILDING_SMALL_HUT" |
"R_BUILDING_SPY_TRAIN" |
"R_BUILDING_TEMPLE" |
"R_BUILDING_WALL_PIECE" |
"R_BUILDING_WARRIOR_TRAIN" |
"R_PERSON_BRAVE" |
"R_PERSON_FIREWARRIOR" |
"R_PERSON_RELIGIOUS" |
"R_PERSON_SHAMAN" |
"R_PERSON_SPY" |
"R_PERSON_WARRIOR" |
"R_VEHICLE_AIRSHIP_1" |
"R_VEHICLE_BOAT_1" |
"SHAMAN" |
"SHIELD" |
"SMALL_HUT" |
"SPELL_TYPE" |
"SPY" |
"SPY_TRAIN" |
"SWAMP" |
"TARGET_SHAMAN" |
"TELEPORT" |
"TEMPLE" |
"TRIBE_BLUE" |
"TRIBE_GREEN" |
"TRIBE_RED" |
"TRIBE_YELLOW" |
"VOLCANO" |
"WALL_PIECE" |
"WARRIOR" |
"WARRIOR_TRAIN" |
"WHIRLWIND" |
"WILD_PEOPLE" |
"WRATH_OF_GOD" |
"YELLOW_KILLED_BY_HUMAN" |
"YELLOW_KILLED_BY_ME" |
"YELLOW_MANA" |
"YELLOW_PEOPLE" |
"Y_BUILDING_AIRSHIP_HUT" |
"Y_BUILDING_AIRSHIP_HUT_2" |
"Y_BUILDING_BOAT_HUT" |
"Y_BUILDING_BOAT_HUT_2" |
"Y_BUILDING_CURR_OE_SLOT" |
"Y_BUILDING_DRUM_TOWER" |
"Y_BUILDING_FIREWARRIOR_TRAIN" |
"Y_BUILDING_GATE" |
"Y_BUILDING_LARGE_HUT" |
"Y_BUILDING_MEDIUM_HUT" |
"Y_BUILDING_RECONVERSION" |
"Y_BUILDING_SMALL_HUT" |
"Y_BUILDING_SPY_TRAIN" |
"Y_BUILDING_TEMPLE" |
"Y_BUILDING_WALL_PIECE" |
"Y_BUILDING_WARRIOR_TRAIN" |
"Y_PERSON_BRAVE" |
"Y_PERSON_FIREWARRIOR" |
"Y_PERSON_RELIGIOUS" |
"Y_PERSON_SHAMAN" |
"Y_PERSON_SPY" |
"Y_PERSON_WARRIOR" |
"Y_VEHICLE_AIRSHIP_1" |
"Y_VEHICLE_BOAT_1"		{ addToken(Token.RESERVED_WORD_2); }

	/* Data types */
	"action" |
"attack_mode" |
"attack_target" |
"count_wild_t" |
"int" |
"shot_type" |
"state" |
"tribe" |
"vehicle_type"		{ addToken(Token.DATA_TYPE); }

	/* Functions */
	"Attack" |
"AttackBlue" |
"AttackGreen" |
"AttackRed" |
"AttackYellow" |
"AutoAttack" |
"AutoMessages" |
"BoatPatrol" |
"BringNewPeopleBack" |
"BuildAt" |
"BuildDrumTower" |
"BuildMainDrumTower" |
"BuildOuterDefences" |
"BuildVehicle" |
"BuildWalls" |
"CallToArms" |
"CameraRotation" |
"CheckIfPersonPreachedTo" |
"ClearAllMsg" |
"ClearGuardingFrom" |
"ClearHouseInfoFlag" |
"ClearShamanLeftClick" |
"ClearShamanRightClick" |
"ClearStandingPeople" |
"ConstructBuilding" |
"ConvertAtMarker" |
"CountAngels" |
"CountBlueInHouses" |
"CountBlueShapes" |
"CountBlueWithBuildCommand" |
"CountGuardFires" |
"CountPeopleInMarker" |
"CreateMsgInformation" |
"CreateMsgInformationZoom" |
"CreateMsgNarrative" |
"CreateMsgObjective" |
"Defend" |
"DefendBase" |
"DefendShamen" |
"DelayMainDrumTower" |
"DeleteSmokeStuff" |
"DeselectAllBluePeople" |
"DisableUserInputs" |
"DontHouseSpecialists" |
"DontTargetBlueDrumTowers" |
"DontTargetBlueShaman" |
"DontTargetFirewarriors" |
"EnableShamanGetWilds" |
"EnableUserInputs" |
"ExtraWoodCollection" |
"FetchFarVehicle" |
"FetchLostPeople" |
"FetchLostVehicle" |
"FetchWood" |
"FirewarriorDefend" |
"FixWildInArea" |
"FlashButton" |
"FlattenBase" |
"FlybyAllowInterrupt" |
"FlybyCreateNew" |
"FlybySetEndTarget" |
"FlybySetEventAngle" |
"FlybySetEventIntPoint" |
"FlybySetEventPos" |
"FlybySetEventTooltip" |
"FlybySetEventZoom" |
"FlybySetMessage" |
"FlybyStart" |
"FlybyStop" |
"ForceTooltip" |
"GetHeadTriggerCount" |
"GetHeightAtPos" |
"GetMsgId" |
"GetNumOneOffSpells" |
"GetNumPeopleBeingPreached" |
"GetNumPeopleConverted" |
"GetSpellsCast" |
"GiveManaToPlayer" |
"GiveOneShot" |
"GivePlayerSpell" |
"GiveUpAndSulk" |
"GuardAtMarker" |
"GuardBetweenMarkers" |
"HasBlueKilledAGhost" |
"HasHouseInfoBeenShown" |
"HasPlayerBeenInEncyc" |
"HasTimerReachedZero" |
"HouseAPerson" |
"IHaveOneShot" |
"IsBlueShamanSelected" |
"IsBuildingNear" |
"IsPlayerInWorldView" |
"IsPrisonOnLevel" |
"IsShamanAvailableForAttack" |
"IsShamanIconLeftClicked" |
"IsShamanIconRightClicked" |
"IsShamanInArea" |
"KillAllMsgId" |
"KillTeamInArea" |
"MarkerEntries" |
"MarvellousHouseDeath" |
"MoveShamanToMarker" |
"NavCheck" |
"OnlyStandAtMarkers" |
"OpenDialog" |
"PartialBuildingCount" |
"PopulateDrumTower" |
"PrayAtHead" |
"Preach" |
"PreachAtMarker" |
"PutPersonInDrumTower" |
"RemoveHeadAtPos" |
"RemovePlayerThing" |
"RemoveTimer" |
"ResetBaseMarker" |
"ResetConvertMarker" |
"Sabotage" |
"SendAllPeopleToMarker" |
"SendBluePeopleToMarker" |
"SendGhostPeople" |
"SendGhosts" |
"SendShamenDefendersHome" |
"SetAttackVariable" |
"SetAutoBuild" |
"SetAutoHouse" |
"SetBaseMarker" |
"SetBaseRadious" |
"SetBucketCountForSpell" |
"SetBucketUsage" |
"SetBuildingDirection" |
"SetConvertMarker" |
"SetCreateReturnMsgOnRmbZoom" |
"SetDefenseRadius" |
"SetDrumTowerPos" |
"SetMarkerEntry" |
"SetMsgAutoOpenDlg" |
"SetMsgDeleteOnOk" |
"SetMsgDeleteOnRmbZoom" |
"SetMsgId" |
"SetMsgOkSaveExitDlg" |
"SetMsgOpenDlgOnRmbDelete" |
"SetMsgOpenDlgOnRmbZoom" |
"SetMsgReturnOnOk" |
"SetMsgTimeout" |
"SetMsgZoom" |
"SetMsgZoomOnLmbOpenDlg" |
"SetNoBlueReinc" |
"SetReincarnation" |
"SetSpecialNoBldgPanel" |
"SetSpellEntry" |
"SetTimerGoing" |
"SetWoodCollectionRadii" |
"ShamanDefend" |
"Spare10" |
"Spare5" |
"Spare6" |
"Spare7" |
"Spare8" |
"Spare9" |
"SpellAttack" |
"SpellDefence" |
"SpellOffensive" |
"StartReincNow" |
"StopCameraRotation" |
"TargetBlueDrumTowers" |
"TargetBlueShaman" |
"TargetPlayerDrumTowerAndFirewarrior" |
"TargetsFirewarriors" |
"TrackShamanExtraBollocks" |
"TrackShamanToAngle" |
"TrackToMarker" |
"TrainPeople" |
"TrainPeopleNow" |
"TriggerLevelLost" |
"TriggerLevelWon" |
"TriggerThing" |
"TurnPanelOn" |
"TurnPush" |
"ZoomTo"		{ addToken(Token.FUNCTION); }

	

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{Identifier}					{ addToken(Token.IDENTIFIER); }

	{WhiteSpace}					{ addToken(Token.WHITESPACE); }

	/* String/Character literals. */
	{CharLiteral}				{ addToken(Token.LITERAL_CHAR); }
{UnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }
{ErrorCharLiteral}			{ addToken(Token.ERROR_CHAR); }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
{UnclosedStringLiteral}		{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }

	/* Comment literals. */
	{MLCBegin}	{ start = zzMarkedPos-2; yybegin(MLC); }
	/* No documentation comments */
	{LineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

	/* Separators. */
	{Separator}					{ addToken(Token.SEPARATOR); }
	{Separator2}					{ addToken(Token.SEPARATOR); }

	/* Operators. */
	"!" |
"!=" |
"&&" |
"*" |
"*=" |
"+" |
"++" |
"+=" |
"-" |
"--" |
"-=" |
"/" |
"/=" |
":" |
"<" |
"<=" |
"=" |
"==" |
">" |
">=" |
"?" |
"||"		{ addToken(Token.OPERATOR); }

	/* Numbers */
	{IntegerLiteral}				{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
	/* No hex literals */
	/* No float literals */
	{ErrorNumberFormat}			{ addToken(Token.ERROR_NUMBER_FORMAT); }

	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters. */
	.							{ addToken(Token.IDENTIFIER); }

}


/* No char state */

/* No string state */

<MLC> {

	[^hwf\n*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]					{}

	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_MULTILINE); }
	"*"						{}
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}


/* No documentation comment state */

<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}

