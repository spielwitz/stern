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

@SuppressWarnings("serial")
public class ScreenContent implements Serializable
{
	private ScreenContentConsole c;
	private ScreenContentPlanets p;
	private ScreenContentBoard f;
	private ScreenContentPlanetEditor e;
	private ScreenContentStatistics t;
	
	private int m;
	private boolean u;
	private boolean s;
	private int g;
	
	transient final static int MODE_BOARD = 0;
	transient final static int MODE_PLANET_EDITOR = 1;
	transient final static int MODE_STATISTICS = 2;
	transient final static int MODE_DISTANCE_MATRIX = 3;
	
	public ScreenContent()
	{
		this.m = MODE_BOARD;
	}
	
	public int getMode() {
		return m;
	}

	public void setMode(int modus)
	{
		if (modus == MODE_PLANET_EDITOR || modus == MODE_STATISTICS || modus == MODE_DISTANCE_MATRIX)
			this.m = modus;
		else
			this.m = MODE_BOARD;
	}
	
	public int getEventDay()
	{
		return this.g;
	}
	
	public void setEventDay(int tag)
	{
		this.g = tag;
	}

	public ScreenContentConsole getConsole() {
		return c;
	}

	public ScreenContentPlanets getPlanets() {
		return p;
	}

	public ScreenContentBoard getBoard() {
		return f;
	}
	
	public ScreenContentPlanetEditor getPlanetEditor() {
		return e;
	}

	public void setConsole(ScreenContentConsole screenContentConsole) {
		this.c = screenContentConsole;
	}

	public void setPlanets(ScreenContentPlanets screenContentPlanets) {
		this.p = screenContentPlanets;
	}

	public void setBoard(ScreenContentBoard screenContentBoard) {
		this.f = screenContentBoard;
	}

	public void setPlanetEditor(ScreenContentPlanetEditor screenContentPlanetEditor) {
		this.e = screenContentPlanetEditor;
	}
	
	public ScreenContentStatistics getStatistics() {
		return t;
	}

	public void setStatistik(ScreenContentStatistics screenContentStatistics) {
		this.t = screenContentStatistics;
	}
	
	public void setPause(boolean isPause)
	{
		this.u = isPause;
	}
	
	public boolean isPause()
	{
		return this.u;
	}
	
	void setSnapshot()
	{
		this.s = true;
	}
	
	public boolean isSnapshot()
	{
		return this.s;
	}
}
