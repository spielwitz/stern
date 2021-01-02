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

@SuppressWarnings("serial") class SpielfeldLineDisplayContent implements Serializable
{
	// Stellt eine Linie dar, die im Spielfeld gezeichnet wird, z.B. um die
	// Wege der Patrouillen anzuzeigen
	private Point s;
	private Point e;
	private PointLowRes p;
	private byte c;
	
	SpielfeldLineDisplayContent(Point start, Point end, Point pos, byte col) {
		super();
		this.s = start;
		this.e = end;
		this.p = pos == null ? null : new PointLowRes(pos);
		this.c = col;
	}

	public Point getStart() {
		return s;
	}

	public Point getEnd() {
		return e;
	}

	public byte getCol() {
		return c;
	}
	
	public Point getPos()
	{
		return this.p== null ? null : this.p.toPoint();
	}
	
}
