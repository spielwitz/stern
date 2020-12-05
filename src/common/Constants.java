/**	STERN, das Strategiespiel.
    Copyright (C) 1989-2020 Michael Schweitzer, spielwitz@icloud.com

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

public class Constants
{
	// Mindestens vorausgesetzte Build-Version beim Laden von Spielen oder bei der
	// Kommunikation mit dem STERN Server, um Inkompatibilitaeten oder Vorteile durch
	// Programmfehler zu vermeiden.
	public static final String 	MIN_BUILD = "202012050000";
	
	// Empfohlener Build. Wird auf die Webseite kopiert und vom Upgrade Checker benutzt.
	// Zeigt ein wichtiges Update an
	public static final String 	RECOMMENDED_BUILD = "202012050000";
	
	// Wenn Spiele aus aelteren Builds geladen werden, muss mirgiert werden.
	static final String 		MIGRATION_BUILD_LIMIT = "202012040000";
	
	public static final String 	STERN_URL = "https://stern.dyndns1.de";
	public static final String 	NO_BUILD_INFO = "000000000000";
	
	// Spielfeld
	public static final int FELD_MAX_X = 20;
	public static final int FELD_MAX_Y = 18;
	
	// Planeten
	static final int 		BESITZER_NEUTRAL = -1;
	public static final int ANZAHL_SPIELER_MAX = 6;
	public static final int ANZAHL_SPIELER_MIN = 2;
	public static final int ANZAHL_PLANETEN_MAX = 42;
	static final int 		MAX_ANZ_FESTUNGEN = 2;
	static final int 		KAUF_EPROD = 4;
	static final int 		FESTUNG_STAERKE = 375;
	static final int 		FESTUNG_REPARATUR_ANZ_RAUMER = 2;
	static final int 		TRANSPORTER_MAX_ENEGER = 30;
	static final int 		RAUMER_ZU_BEGINN_SPIELER = 375;
	static final int 		RAUMER_ZU_BEGINN_NEUTRAL = 11;
	static final int 		EPROD_ZU_BEGINN_SPIELER = 10;
	static final int 		EVORRAT_ZU_BEGINN_SPIELER = 30;
	static final int 		EVORRAT_ZU_BEGINN_NEUTRAL_MAX = 5;
	static final int 		EPROD_ZU_BEGINN_NEUTRAL = 10;
	static final int 		EPROD_ZU_BEGINN_NEUTRAL_EXTRA = 5;
	static final int 		EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W1 = 15;
	static final int 		EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W2 = 200;
	public static final int SPIELER_NAME_MIN_LAENGE = 3;
	public static final int SPIELER_NAME_MAX_LAENGE = 10;
	static final int 		PLANETEN_NAME_MAX_LAENGE = 5;
	public static final 	String SPIELER_REGEX_PATTERN = "[0-9a-zA-Z]*";
	static final int 		EPROD_MAX = 100;
	static final int 		ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE = 2;
	static final int 		ANZ_NAHE_PLANETEN = 4;
	static final int 		SENDER_JAHRE = 8;
	static final double		VERT_BONUS = 1.25;
	
	// Default-Werte bei neuem Spiel
	public static final int DEFAULT_ANZAHL_PLANETEN = 42;
	public static final int DEFAULT_ANZAHL_SPIELER = 6;
	public static final int DEFAULT_MAX_JAHRE = 30;
	
	// Flugobjekte
	static final int 		KEIN_PLANET = -1;
	static final int 		GESCHWINDIGKEIT_NORMAL = 2;
	static final int 		GESCHWINDIGKEIT_SCHNELL = 4;
	static final int 		GESCHWINDIGKEIT_LANGSAM = 1;
	static final double 	PATROUILLE_BEOBACHTUNGSRADIUS = 1.5;
	static final int 		PATROUILLE_KAPERT_RAUMER = 5;
	
	// Kaufpreise
	static final int 		PREIS_AUFKLAERER = 4;
	static final int 		PREIS_PATROUILLE = 8;
	static final int 		PREIS_FESTUNG = 75;
	static final int 		PREIS_FESTUNG_REPARATUR = 1;
	static final int 		PREIS_EPROD = 75;
	static final int 		PREIS_ERAUM = 0;
	static final int 		PREIS_MINE50 = 14;
	static final int 		PREIS_MINE100 = 22;
	static final int 		PREIS_MINE250 = 43;
	static final int 		PREIS_MINE500 = 65;
	static final int 		PREIS_MINENRAEUMER = 25;
	static final int 		PREIS_TRANSPORTER= 5;
	
	static final double 	PREIS_KAUF_VERKAUF_RATIO = 2./3.;
	
	// Statistik
	static final char 		STATISTIK_MODUS_PUNKTE = '1';
	static final char 		STATISTIK_MODUS_RAUMER = '2';
	static final char 		STATISTIK_MODUS_PLANETEN = '3';
	static final char 		STATISTIK_MODUS_PRODUKTION = '4';
	
	// Kuenstlige Intelligenz
	static final int 		KI_FAKTOR_DISTANZ 		= 1,
				 			KI_MIN_RAUMER_NEUTR_PL 	= 10,
				 			KI_FAKTOR_ANGRIFF_MAX_RND = 30;
	static final double 	KI_FAKTOR_ANGRIFF 			= 1.5,
							KI_BEDROHUNG_ERHOEHUNG		= 0.5;

	// Spielkontrolle
	static final int 		PAUSE_MILLISECS = 1000;
	static final int 		PAUSE_MILLISECS_ANIMATION = 200;
	
	// Sonstiges
	static int 					ANZ_TAGE_JAHR = 365;
	public static final String 	FONT_NAME = "/JetBrainsMono-Regular.ttf";
	public static final float 	DIALOG_FONT_SIZE = 11F;
	public static final String 	RELEASE_FILE_NAME = "release.txt";
	public static final String 	RELEASE_RECOMMENDED_FILE_NAME = "releaseRecommended.txt";
	static final String 		GELOESCHTER_SPIELER = "_DELETED_";
	public final static String	REG_NAME_SERVER = "Stern";
	static final double 		PRECISION = 0.000001;
	

}
