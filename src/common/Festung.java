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
public class Festung implements Serializable
{
	private int faktor; // 1=einfache oder 2=doppelte Festung
	private int intakt; // Wert in Prozent, also z.B. 100
	
	public Festung(int faktor, int intakt) {
		super();
		this.faktor = faktor;
		this.intakt = intakt;
	}

	public int getFaktor() {
		return faktor;
	}

	public int getIntakt() {
		return intakt;
	}

	public void addFestung()
	{
		this.faktor++;
		
		double i2 = (double)this.intakt;
		this.intakt = Utils.round(((i2*(double)(this.faktor-1)) + 100.) / (double)this.faktor);
	}
	
	public void subFestung()
	{
		if (this.faktor > 1)
			this.faktor--;
	}
	
	public void incIntakt()
	{
		if (this.intakt < 100)
			this.intakt++;
	}
	
	public void subIntakt()
	{
		if (this.intakt > 0)
			this.intakt--;
	}
	
	public void setIntakt(int arg)
	{
		if (arg > 0 && arg <= 100)
			this.intakt = arg;
	}
}
