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
public class Archiv implements Serializable
{
	private int punkte[]; // Punkte pro Spieler
	private int raumer[]; // Raumer pro Spieler
	private int anzPl[]; // Anzahl Planeten pro Spieler
	private int eprod[]; // Energieproduktion pro Spieler
	
	Archiv(int[] punkte, int[] raumer, int[] anzPl, int[] eprod)
	{
		super();
		this.punkte = punkte;
		this.raumer = raumer;
		this.anzPl = anzPl;
		this.eprod = eprod;
	}

	public int[] getRaumer() {
		return raumer;
	}

	public int[] getAnzPl() {
		return anzPl;
	}

	public int[] getEprod() {
		return eprod;
	}
	
	public int[] getPunkte()
	{
		return punkte;
	}	
}
