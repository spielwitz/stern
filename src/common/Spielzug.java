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
import java.util.UUID;

@SuppressWarnings("serial") class Spielzug implements Serializable
{
	transient private Planet planetVorher;

	private int plIndex;
	private Planet planetNachher;
	private Flugobjekt obj;
	private boolean[] buendnis;
	private UUID stopLabel;
	
	Spielzug(int plIndex, Flugobjekt obj, Planet planetVorher)
	{
		// Flugobjekte starten
		super();
		this.plIndex = plIndex;
		this.obj = obj;
		this.planetVorher = planetVorher;
	}
	
	Spielzug(int plIndex, Planet planetVorher, Planet planetNachher)
	{
		// Planeteneditor
		super();
		this.plIndex = plIndex;
		this.planetVorher = planetVorher;
		this.planetNachher = (Planet)Utils.klon(planetNachher);
	}
	
	Spielzug(int plIndex, Planet planetVorher, boolean[] buendnis)
	{
		// Buendnis geaendert
		super();
		this.plIndex = plIndex;
		this.planetVorher = planetVorher;
		this.buendnis = (boolean[])Utils.klon(buendnis);
	}
	
	Spielzug(Flugobjekt obj, UUID stopLabel, int plIndex)
	{
		// Gestopptes Objekt zu neuem Ziel geschickt
		super();
		this.obj = obj;
		this.stopLabel = stopLabel;
		this.plIndex = plIndex;
	}
	
	public Planet getPlanetVorher() {
		return this.planetVorher;
	}
	
	public Planet getPlanetNachher() {
		return this.planetNachher;
	}
	
	public Flugobjekt getObj() {
		return obj;
	}

	public int getPlIndex() {
		return plIndex;
	}	
	
	public boolean[] getBuendnis()
	{
		return this.buendnis;
	}
	
	public UUID getStopLabel()
	{
		return this.stopLabel;
	}
}
