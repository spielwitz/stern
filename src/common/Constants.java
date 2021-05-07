/**	STERN - a strategy game
    Copyright (C) 1989-2021 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package common;

import java.util.Hashtable;

public class Constants
{
	// Minimum required build version when reading games or when exchaning data
	// with the STERN server to avoid incompatibilities and advantages caused
	// by program errors.
	public static final String 	BUILD_COMPATIBLE = "2018";

	// Recommended build. Was used by the update checker (suspended) to indicate
	// an important update.
	public static final String 	BUILD_IMPORTANT_UPDATE = "2018";

	// Games older than this build have to be migrated.
	static final String 		BUILD_MIGRATION = "2018";
	
	public static final String 	STERN_URL = "https://stern.dyndns1.de";
	public static final String 	BUILD_NO_INFO = "9999";
	
	// Game board dimensions 
	public static final int BOARD_MAX_X = 20;
	public static final int BOARD_MAX_Y = 18;
	
	// Planets
	static final int 		NEUTRAL = -1;
	public static final int PLAYERS_COUNT_MAX = 6;
	public static final int PLAYERS_COUNT_MIN = 2;
	public static final int PLANETS_COUNT_MAX = 42;
	static final int 		DEFENSE_SHIELDS_COUNT_MAX = 2;
	static final int 		MONEY_PRODUCTION_PURCHASE = 4;
	static final int 		DEFENSE_SHIELD_FIGHTERS = 350;
	static final int 		DEFENSE_SHIELD_REPAIR_FIGHTERS_COUNT = 2;
	static final int 		TRANSPORT_MONEY_MAX = 30;
	static final int 		FIGHTERS_COUNT_INITIAL_PLAYERS = 350;
	static final int 		FIGHTERS_COUNT_INITIAL_NEUTRAL_MAX = 10;
	static final int 		MONEY_PRODUCTION_INITIAL_PLAYERS = 10;
	static final int 		MONEY_SUPPLY_INITIAL_PLAYERS = 30;
	static final int 		MONEY_SUPPLY_INITIAL_NEUTRAL_MAX = 5;
	static final int 		MONEY_PRODUCTION_INITIAL_NEUTRAL = 10;
	static final int 		MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA = 5;
	static final int 		MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W1 = 15;
	static final int 		MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W2 = 200;
	static final int 		MONEY_PRODUCTION_MAX = 100;
	public static final int PLAYER_NAME_LENGTH_MIN = 3;
	public static final int PLAYER_NAME_LENGTH_MAX = 10;
	public static final 	String PLAYER_NAME_REGEX_PATTERN = "[0-9a-zA-Z]*";
	static final int 		PLANET_NAME_LENGTH_MAX = 2;
	public static final int GAME_NAME_LENGTH_MIN = 3;
	public static final int GAME_NAME_LENGTH_MAX = 18;
	static final int 		GAME_PLANETS_NEARBY_COUNT = 4;
	static final double		DEFENSE_BONUS = 1.25;
	
	// Default values for a new game
	public static final int PLAYERS_COUNT_DEFAULT = 6;
	public static final int YEARS_COUNT_MAX_DEFAULT = 50;
	
	// Spaceships
	static final int 		NO_PLANET = -1;
	static final int 		SPEED_NORMAL = 2;
	static final int 		SPEED_FAST = 4;
	static final int 		SPEED_SLOW = 1;
	static final double 	PATROL_RADAR_RANGE = 1.5;
	static final int 		PATROL_CAPUTURES_FIGHTERS_COUNT_MAX = 5;
	
	// Prices
	static final double 	PRICE_RATIO_BUY_SELL = 2./3.;
	
	static Hashtable<ShipType, Point> PRICES_MIN_MAX;
	
	static 
	{
		PRICES_MIN_MAX = new Hashtable<ShipType, Point>();
		
		PRICES_MIN_MAX.put(ShipType.DEFENCE_SHIELD, new Point(60, 90));
		PRICES_MIN_MAX.put(ShipType.DEFENCE_SHIELD_REPAIR, new Point(1, 1));
		PRICES_MIN_MAX.put(ShipType.MONEY_PRODUCTION, new Point(60, 90));
		PRICES_MIN_MAX.put(ShipType.FIGHTER_PRODUCTION, new Point(0, 0));
		
		PRICES_MIN_MAX.put(ShipType.SCOUT, new Point(3, 6));
		PRICES_MIN_MAX.put(ShipType.PATROL, new Point(6, 10));
		PRICES_MIN_MAX.put(ShipType.TRANSPORT, new Point(4, 7));
		PRICES_MIN_MAX.put(ShipType.MINESWEEPER, new Point(20, 28));
		
		PRICES_MIN_MAX.put(ShipType.MINE50, new Point(12, 17));
		PRICES_MIN_MAX.put(ShipType.MINE100, new Point(20, 25));
		PRICES_MIN_MAX.put(ShipType.MINE250, new Point(40, 47));
		PRICES_MIN_MAX.put(ShipType.MINE500, new Point(60, 70));		
	}
	
	// Statistics
	static final char 		STATISTICS_MODE_SCORE = '1';
	static final char 		STATISTICS_MODE_FIGHTERS = '2';
	static final char 		STATISTICS_MODE_PLANETS = '3';
	static final char 		STATISTICS_MODE_MONEY_PRODUCTION = '4';
	
	// Replay and evaluation
	static final int 		PAUSE_MILLISECS = 1000;
	static final int 		PAUSE_MILLISECS_ANIMATION = 200;
	
	// Others
	static int 					DAYS_OF_YEAR_COUNT = 365;
	public static final String 	FONT_NAME = "/JetBrainsMono-Regular.ttf";
	public static final float 	FONT_DIALOG_SIZE = 11F;
	public static final String 	FILE_NAME_RELEASE = "release.txt";
	public static final String 	FILE_NAME_RELEASE_RECOMMENDED = "releaseRecommended.txt";
	static final String 		PLAYER_DELETED_NAME = "_DELETED_";
	public final static String	RMI_REGISTRATION_NAME_SERVER = "Stern";
	static final double 		PRECISION = 0.000001;
}
