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
import java.util.UUID;

@SuppressWarnings("serial") class Move implements Serializable
{
	transient private Planet planetBefore;

	private int planetIndex;
	private Planet planetAfter;
	private Ship ship;
	private int allianceChanges = -1;
	private UUID stopLabel;
	
	Move(int planetIndex, Ship ship, Planet planetBefore)
	{
		super();
		this.planetIndex = planetIndex;
		this.ship = ship;
		this.planetBefore = planetBefore;
	}
	
	Move(int planetIndex, Planet planetBefore, Planet planetAfter)
	{
		super();
		this.planetIndex = planetIndex;
		this.planetBefore = planetBefore;
		this.planetAfter = (Planet)Utils.klon(planetAfter);
	}
	
	Move(int planetIndex, int allianceChanges)
	{
		super();
		this.planetIndex = planetIndex;
		this.allianceChanges = allianceChanges;
	}
	
	Move(Ship ship, UUID stopLabel, int planetIndex)
	{
		super();
		this.ship = ship;
		this.stopLabel = stopLabel;
		this.planetIndex = planetIndex;
	}
	
	public Planet getPlanetBefore() {
		return this.planetBefore;
	}
	
	public Planet getPlanetAfter() {
		return this.planetAfter;
	}
	
	public Ship getShip() {
		return ship;
	}

	public int getPlanetIndex() {
		return planetIndex;
	}	
	
	public int getAllianceChanges()
	{
		return this.allianceChanges;
	}
	
	public UUID getStopLabel()
	{
		return this.stopLabel;
	}
}
