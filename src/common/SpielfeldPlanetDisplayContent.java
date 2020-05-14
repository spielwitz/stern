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

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class SpielfeldPlanetDisplayContent implements Serializable
{
	// Informationen ueber einen Planeten, wie er von einem Client im Spielfeld
	// dargestellt wird
	private String n;
	private Point p;
	private byte c; // Farbe
	private boolean i;
	private ArrayList<Byte> f; // Rahmenfarben, beginnend mit dem innersten
	
	public SpielfeldPlanetDisplayContent(String name, Point pos, byte col, boolean invers,
			ArrayList<Byte> frameCols) {
		super();
		this.n = name;
		this.p = pos;
		this.c = col;
		this.i = invers;
		this.f = frameCols;
	}

	public String getName() {
		return n;
	}

	public Point getPos() {
		return p;
	}

	public byte getCol() {
		return c;
	}
	
	public ArrayList<Byte> getFrameCols() {
		return f;
	}
	
	public boolean isInvers()
	{
		return this.i;
	}
}
