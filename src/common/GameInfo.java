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

import java.util.ArrayList;
import java.util.HashSet;

import common.Game.PlanetInfo;

public class GameInfo 
{
	public String name;
	public long dateStart;
	public long dateUpdate;
	public int yearMax;
	public int year;
	public boolean simple;
	public HashSet<String> moveEnteringFinalized;
	
	public ArrayList<PlanetInfo> planetInfo;
	
	public Player[] players;
	
	public boolean finalized;
}
