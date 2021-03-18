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

@SuppressWarnings("serial") class Mine implements Serializable
{
	private int positionX;
	private int positionY;
	private int strength;

	Mine(int positionX, int positionY, int strength) {
		super();
		this.positionX = positionX;
		this.positionY = positionY;
		this.strength = strength;
	}

	public int getStrength() {
		return strength;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	void addToStrength(int strength)
	{
		this.strength += strength;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}
	
	
}
