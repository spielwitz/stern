/**	STERN, das Strategiespiel.
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

import java.util.ArrayList;
import java.util.HashSet;

import common.Spiel.PlanetenInfo;

public class SpielInfo 
{
	public String name;
	public long startDatum;
	public long letztesUpdate;
	public int maxJahre;
	public int jahr; // Momentanes Jahr (startet mit 0)
	public boolean simpleStern;
	public HashSet<String> zugeingabeBeendet;
	
	public ArrayList<PlanetenInfo> planetenInfo;
	
	public Spieler[] spieler;
	
	public boolean abgeschlossen;
}
