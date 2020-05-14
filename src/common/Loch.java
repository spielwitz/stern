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
public class Loch implements Serializable
{
	private boolean active; // Loch ist aktiv
	private Point pos; // Momentane Position
	private int nextActionJahr; // Jahr, in dem das Loch wieder verschwindet bzw. auftaucht
	
	public Loch(boolean active, Point pos, int nextActionJahr) {
		super();
		this.active = active;
		this.pos = pos;
		this.nextActionJahr = nextActionJahr;
	}

	public boolean isActive() {
		return active;
	}

	public Point getPos() {
		return pos;
	}

	public int getNextActionJahr() {
		return nextActionJahr;
	}
	
	public void setPosition(Point pos)
	{
		this.pos = pos.klon();
	}
}
