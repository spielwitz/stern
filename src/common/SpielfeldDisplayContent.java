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

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial") class SpielfeldDisplayContent implements Serializable
{
	private ArrayList<SpielfeldPlanetDisplayContent> p; // Planeten
	private ArrayList<Point> m; // Markierte Felder
	private ArrayList<SpielfeldLineDisplayContent> l; // Linien
	private ArrayList<SpielfeldPointDisplayContent> o; // Objekte
	private ArrayList<MinenfeldDisplayContent> n; // Minen
	private SpielfeldPointRadarDisplayContent r; // Radarstrahl bei Patrouillenbeobachtung
	
	SpielfeldDisplayContent(
			ArrayList<SpielfeldPlanetDisplayContent> planets,
			ArrayList<Point> markedFields,
			ArrayList<SpielfeldLineDisplayContent> lines,
			ArrayList<SpielfeldPointDisplayContent> points,
			ArrayList<MinenfeldDisplayContent> minen,
			SpielfeldPointRadarDisplayContent radar) {

		this.p = planets;
		this.m = markedFields;
		this.l = lines;
		this.o = points;
		this.n = minen;
		this.r = radar;
	}

	public ArrayList<SpielfeldPlanetDisplayContent> getPlanets() {
		return p;
	}

	public ArrayList<Point> getMarkedFields() {
		return this.m;
	}
	
	public void clearMarkedFieldsAndPlanets()
	{
		this.m = null;
		this.r = null;
		
		for (SpielfeldPlanetDisplayContent pl: this.p)
		{
			pl.clearFrameCols();
		}
	}

	public ArrayList<SpielfeldLineDisplayContent> getLines() {
		return l;
	}
	
	public ArrayList<SpielfeldPointDisplayContent> getPoints() {
		return o;
	}

	public ArrayList<MinenfeldDisplayContent> getMinen() {
		return n;
	}
	
	public void setPoints(ArrayList<SpielfeldPointDisplayContent> points)
	{
		this.o = points;
	}

	public SpielfeldPointRadarDisplayContent getRadar() {
		return r;
	}
}
