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

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial") 
class ScreenContentBoard implements Serializable
{
	private ArrayList<ScreenContentBoardPlanet> p;
	private ArrayList<Point> m;
	private ArrayList<ScreenContentBoardLine> l;
	private ArrayList<ScreenContentBoardPosition> o;
	private ArrayList<ScreenContentBoardMine> n;
	private ScreenContentBoardRadar r;
	
	ScreenContentBoard(
			ArrayList<ScreenContentBoardPlanet> planets,
			ArrayList<Point> positionsMarked,
			ArrayList<ScreenContentBoardLine> lines,
			ArrayList<ScreenContentBoardPosition> positions,
			ArrayList<ScreenContentBoardMine> mines,
			ScreenContentBoardRadar radarCircle) {

		this.p = planets;
		this.m = positionsMarked;
		this.l = lines;
		this.o = positions;
		this.n = mines;
		this.r = radarCircle;
	}

	public ArrayList<ScreenContentBoardPlanet> getPlanets() {
		return p;
	}

	public ArrayList<Point> getPositionsMarked() {
		return this.m;
	}
	
	void clearMarks()
	{
		this.m = null;
		this.r = null;
		this.l = null;
		
		for (ScreenContentBoardPlanet pl: this.p)
		{
			pl.clearFrames();
		}
	}

	public ArrayList<ScreenContentBoardLine> getLines() {
		return l;
	}
	
	public ArrayList<ScreenContentBoardPosition> getPositions() {
		return o;
	}

	public ArrayList<ScreenContentBoardMine> getMines() {
		return n;
	}
	
	public void setPositions(ArrayList<ScreenContentBoardPosition> positions)
	{
		this.o = positions;
	}

	public ScreenContentBoardRadar getRadarCircle() {
		return r;
	}
}
