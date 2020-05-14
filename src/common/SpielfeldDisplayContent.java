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

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class SpielfeldDisplayContent implements Serializable
{
	private ArrayList<SpielfeldPlanetDisplayContent> p;
	private ArrayList<PointLowRes> m; // Markierte Felder
	private ArrayList<SpielfeldLineDisplayContent> l;
	private Point o; // Position des Schwarzen Lochs. Wenn NULL, dann kein Loch :-)
	private ArrayList<MinenfeldDisplayContent> n;
	
	public SpielfeldDisplayContent(
			ArrayList<SpielfeldPlanetDisplayContent> planets,
			ArrayList<Point2D.Double> markedFields,
			ArrayList<SpielfeldLineDisplayContent> lines,
			Point lochPos,
			ArrayList<MinenfeldDisplayContent> minen) {

		this.p = planets;
		this.m = markedFields == null ? null : PointLowRes.toArrayList(markedFields);
		this.l = lines;
		this.o = lochPos;
		this.n = minen;
	}

	public ArrayList<SpielfeldPlanetDisplayContent> getPlanets() {
		return p;
	}

	public ArrayList<Point2D.Double> getMarkedFields() {
		return this.m == null ? null : PointLowRes.fromArrayList(m);
	}

	public ArrayList<SpielfeldLineDisplayContent> getLines() {
		return l;
	}

	public Point getLochPos() {
		return o;
	}

	public ArrayList<MinenfeldDisplayContent> getMinen() {
		return n;
	}
}
