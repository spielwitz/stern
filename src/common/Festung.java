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
class Festung implements Serializable
{
	private int faktor; // 1=einfache oder 2=doppelte Festung
	private int intakt; // Wert in Prozent, also z.B. 100 -> War in frueheren STERN-Versionen
	private int raumer; // Anzahl der Festungsraumer
	
	Festung()
	{
		this.faktor = 0;
		this.raumer = 0;
		
		this.intakt = -1; // Kennzeichen, dass neue Festungsdefinition gilt
		
		this.add(); // Gleich eine Festung hinzufuegen
	}
	
	void add()
	{
		if (this.faktor < Constants.MAX_ANZ_FESTUNGEN)
		{
			this.faktor++;
			this.raumer += Constants.FESTUNG_STAERKE;
		}
	}
	
	void subtract()
	{
		if (this.faktor > 0)
		{
			this.raumer -= 
					Utils.round(Constants.FESTUNG_STAERKE * this.getZustand());
			this.faktor--;
			
			if (this.raumer <= 0)
			{
				this.faktor = 0;
				this.raumer = 0;
			}
		}
	}
	
	void repair()
	{
		int maxRepairRaumer = Math.min(
								this.faktor * Constants.FESTUNG_STAERKE - this.raumer,
								Constants.FESTUNG_REPARATUR_ANZ_RAUMER);
		
		if (maxRepairRaumer > 0)
			this.raumer += maxRepairRaumer;
				
	}
	
	boolean istVollIntakt()
	{
		return this.raumer == this.faktor * Constants.FESTUNG_STAERKE;
	}
	
	int getRaumer()
	{
		return this.raumer;
	}
	
	void setRaumer(int raumer)
	{
		this.raumer = raumer;
		
		if (this.raumer <= 0)
		{
			this.raumer = 0;
			this.faktor = 0;
		}
	}
	
	int getFaktor()
	{
		return this.faktor;
	}
	
	double getZustand()
	{
		if (this.faktor > 0)
			return (double)this.raumer / (double)(this.faktor * Constants.FESTUNG_STAERKE); 
		else
			return 0;
	}
	
	static Festung migrieren(int faktor, int intakt)
	{
		Festung festung = new Festung();
		festung.faktor = faktor;
		festung.intakt = intakt;
		
		festung.migrieren();
		
		return festung;
	}
	
	private void migrieren()
	{
		// Alte Festungsdefinition (mit intakt) auf neue Definition (mit Raumern) umstellen
		if (this.intakt >= 0)
		{
			this.raumer = Utils.round(
					(double)Constants.FESTUNG_STAERKE * 
					(double)this.faktor *
					(double)this.intakt / (double)100);
			
			this.intakt = -1; // Zeichen, dass migriert wurde
		}
	}
}
