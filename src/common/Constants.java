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
	// Mindestens vorausgesetzte Build-Version beim Laden von Spielen
	public static final String MIN_BUILD = "202005140800";
	// Empfohlener Build. Wird auf die Webseite kopiert vom Upgrade Checker benutzt.
	// Zeigt ein wichtiges Update an
	public static final String RECOMMENDED_BUILD = "202005140800";
	public static final String STERN_URL = "https://stern.dyndns1.de";
	public static final String NO_BUILD_INFO = "000000000000";
	
	// Spielfeld
	public static final int FELD_MAX_X = 20;
	public static final int FELD_MAX_Y = 18;
	public static final int SPIELFELD_ERZEUGEN_MAX_DIST = 5;
	public static final int SPIELFELD_ERZEUGEN_VERSUCHE = 750;
	
	// Planeten
	public static final int BESITZER_NEUTRAL = -1;
	public static final int ANZAHL_SPIELER_MAX = 6;
	public static final int ANZAHL_SPIELER_MIN = 2;
	public static final int ANZAHL_PLANETEN_MAX = 42;
	public static final int MAX_ANZ_FESTUNGEN = 2;
	public static final int KAUF_EPROD = 4;
	public static final int FESTUNG_STAERKE_MIN = 350; // 200
	public static final int FESTUNG_STAERKE_SPANNE = 101; // 51
	public static final int FESTUNG_STAERKE_EINFACHER_KAMPF = 400; // 250
	public static final int VERT_BONUS_PROZENT_MIN = 15;
	public static final int VERT_BONUS_PROZENT_SPANNE = 20;
	public static final int TRANSPORTER_MAX_ENEGER = 30;
	public static final int PLANETEN_MINDESTABSTAND = 2;
	public static final int RAUMER_ZU_BEGINN_SPIELER = 375; // 250
	public static final int RAUMER_ZU_BEGINN_NEUTRAL = 11;
	public static final int EPROD_ZU_BEGINN_SPIELER = 10;
	public static final int EVORRAT_ZU_BEGINN_SPIELER = 30;
	public static final int EVORRAT_ZU_BEGINN_NEUTRAL_MAX = 5;
	public static final int EPROD_ZU_BEGINN_NEUTRAL = 10;
	public static final int EPROD_ZU_BEGINN_NEUTRAL_EXTRA = 5;
	public static final int EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W1 = 15;
	public static final int EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W2 = 200;
	public static final int EPROD_SUMME_MIN = 14;
	public static final int EPROD_SUMME_MAX = 17;
	public static final int PLANETEN_NAME_MAX_LAENGE = 5; // 2;
	public static final int SPIELER_NAME_MIN_LAENGE = 3;
	public static final int SPIELER_NAME_MAX_LAENGE = 10;
	public static final String SPIELER_REGEX_PATTERN = "[0-9a-zA-Z]*";
	public static final String EMAIL_REGEX_PATTERN = "@";
	public static final int EPROD_MAX = 100;
	public static final int ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE = 2;
	public static final int ANZ_NAHE_PLANETEN = 4;
	public static final int SENDER_JAHRE = 5;
	
	// Default-Werte bei neuem Spiel
	public static final int DEFAULT_ANZAHL_PLANETEN = 42;
	public static final int DEFAULT_ANZAHL_SPIELER = 6;
	public static final int DEFAULT_MAX_JAHRE = 30;
	
	// Flugobjekte
	public static final int KEIN_PLANET = -1;
	public static final int AUSSERIRDISCHE_ERSTES_JAHR = 4;
	public static final int AUSSERIRDISCHE_WAHRSCHEINLICHKEIT = 15;
	public static final int GESCHWINDIGKEIT_NORMAL = 2;
	public static final int GESCHWINDIGKEIT_SCHNELL = 4;
	public static final int GESCHWINDIGKEIT_LANGSAM = 1;
	public static final int PATROUILLE_BEOBACHTUNG_DX = 1;
	public static final int PATROUILLE_BEOBACHTUNG_DY = 1;
	public static final int PATROUILLE_KAPERT_RAUMER = 5;
	
	// Schwarzes Loch
	public static final int LOCH_MINDESTDAUER = 2;
	public static final int LOCH_DAUER_SPANNE = 7;
	public static final int LOCH_MINDESTDAUER_WEG = 1;
	public static final int LOCH_DAUER_SPANNE_WEG = 3;
	public static final int LOCH_MAX_DX = 2;
	public static final int LOCH_MAX_DY = 2;
	public static final int LOCH_ERSTES_JAHR = 7;
	
	// Kaufpreise
	public static final int PREIS_MIN_AUFKL = 3;
	public static final int PREIS_MIN_PATR = 6;
	public static final int PREIS_MIN_FESTUNG = 60;
	public static final int PREIS_MIN_FESTUNG_REPARATUR = 1;
	public static final int PREIS_MIN_EPROD = 60;
	public static final int PREIS_MIN_ERAUM = 0;
	public static final int PREIS_MIN_MINE50 = 12;
	public static final int PREIS_MIN_MINE100 = 20;
	public static final int PREIS_MIN_MINE250 = 40;
	public static final int PREIS_MIN_MINE500 = 60;
	public static final int PREIS_MIN_TRANSP= 4;
	public static final int PREIS_MIN_MINENR= 20;
	
	public static final int PREIS_SPANNE_AUFKL = 4;
	public static final int PREIS_SPANNE_PATR = 5;
	public static final int PREIS_SPANNE_FESTUNG = 31;
	public static final int PREIS_SPANNE_FESTUNG_REPARATUR = 1;
	public static final int PREIS_SPANNE_EPROD = 31;
	public static final int PREIS_SPANNE_ERAUM = 1;
	public static final int PREIS_SPANNE_MINE50 = 6;
	public static final int PREIS_SPANNE_MINE100 = 6;
	public static final int PREIS_SPANNE_MINE250 = 8;
	public static final int PREIS_SPANNE_MINE500 = 11;
	public static final int PREIS_SPANNE_TRANSP= 4;
	public static final int PREIS_SPANNE_MINENR= 9;
	
	public static final double PREIS_KAUF_VERKAUF_RATIO = 2./3.;
	
	// Statistik
	public static final char STATISTIK_MODUS_PUNKTE = '1';
	public static final char STATISTIK_MODUS_RAUMER = '2';
	public static final char STATISTIK_MODUS_PLANETEN = '3';
	public static final char STATISTIK_MODUS_PRODUKTION = '4';
	
	// Kuenstlige Intelligenz
	public static final int KI_FAKTOR_DISTANZ 		= 1,
				 			KI_MIN_RAUMER_NEUTR_PL 	= 10,
				 			KI_FAKTOR_ANGRIFF_MAX_RND = 30;
	public static final double KI_FAKTOR_ANGRIFF 			= 1.5,
							   KI_BEDROHUNG_ERHOEHUNG		= 0.5;

	// Spielkontrolle
	public static final int PAUSE_MILLISECS = 1000;
	
	// Sonstiges
	public static final String FONT_NAME = "/JetBrainsMono-Regular.ttf";
	public static final float DIALOG_FONT_SIZE = 11F;
	public static final String RELEASE_FILE_NAME = "release.txt";
	public static final String RELEASE_RECOMMENDED_FILE_NAME = "releaseRecommended.txt";
	public static final String GELOESCHTER_SPIELER = "_DELETED_";
}
