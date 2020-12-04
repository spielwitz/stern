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

@SuppressWarnings("serial") 
class SpielfeldPointRadarDisplayContent implements Serializable
{
	private PointLowRes p; // Punkt
	private byte c; // Farbe
	private int f; // Winkel Flugrichtung (in ganzzahligen Grad)
	private int r; // Winkel Radarstrahl (in ganzzahligen Grad)
	
	SpielfeldPointRadarDisplayContent(
			Point pos, 
			byte col,
			int winkelFlugrichtung,
			int winkelRadarstrahl)
	{
		super();
		this.p = new PointLowRes(pos);
		this.c = col;
		this.f = winkelFlugrichtung;
		this.r = winkelRadarstrahl;
	}

	public int getWinkelFlugrichtung() {
		return f;
	}

	public int getWinkelRadarstrahl() {
		return r;
	}

	public byte getCol() {
		return c;
	}
	
	public Point getPos()
	{
		return this.p.toPoint();
	}
}
