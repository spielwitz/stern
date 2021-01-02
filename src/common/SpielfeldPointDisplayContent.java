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

@SuppressWarnings("serial") 
class SpielfeldPointDisplayContent implements Serializable
{
	// Stellt einen Punkt dar, der im Spielfeld gezeichnet wird, z.B. um ein Objekt anzuzeigen
	private Point p;
	private byte c;
	private byte s;
	private int h;
	
	SpielfeldPointDisplayContent(
			Point pos, 
			byte col,
			byte symbol,
			int hash)
	{
		super();
		this.p = pos;
		this.c = col;
		this.s = symbol;
		this.h = hash;
	}

	public byte getCol() {
		return c;
	}
	
	public Point getPos()
	{
		return this.p;
	}
	
	public int getHash()
	{
		return this.h;
	}

	public byte getSymbol() {
		return s;
	}
}
