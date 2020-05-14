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
public class Buendnis implements Serializable
{
	private boolean[] mitglieder;
	private int[] raumer;
	
	public Buendnis(int anzSpieler)
	{
		this.mitglieder = new boolean[anzSpieler];
		this.raumer = new int[anzSpieler];
	}
	
	public int getAnzSp()
	{
		return this.mitglieder.length;
	}
	
	public void setRaumer(int spieler, int anz)
	{
		this.mitglieder[spieler] = true;
		this.raumer[spieler] = anz;
	}
	public void addRaumer(int spieler, int anz)
	{
		if (this.mitglieder[spieler])
			this.raumer[spieler] += anz;
		else
			this.setRaumer(spieler, anz);
	}
	
	public void subRaumer(int spieler, int anz)
	{
		if (this.mitglieder[spieler])
		{
			this.raumer[spieler] -= anz;
			if (this.raumer[spieler] < 0)
				this.raumer[spieler] = 0;
		}
	}
	
	public void addSpieler(int spieler)
	{
		this.mitglieder[spieler] = true;
	}
	
	public void removeSpieler(int spieler, boolean raumerLoeschen)
	{
		this.mitglieder[spieler] = false;
		
		if (raumerLoeschen)
			this.raumer[spieler] = 0;
	}
	
	public int getSum()
	{
		int sum = 0;
		
		for (int i = 0; i < this.mitglieder.length; i++)
			if (this.mitglieder[i])
				sum += this.raumer[i];
		
		return sum;
	}
	
	public int getAnz(int spieler)
	{
		if (this.istMitglied(spieler))
			return this.raumer[spieler];
		else
			return 0;
	}
	
	public boolean istMitglied(int spieler)
	{
		if (spieler != Constants.BESITZER_NEUTRAL)
			return this.mitglieder[spieler];
		else
			return false;
	}
	
	public int[] subtract(int anz, int bevorzugterSpieler)
	{
		// Dem "bevorzugten" Spieler wird immer zuerst abgezogen
		int summeStart = this.getSum();
		
		if (anz <= 0 || summeStart <= 0)
			return new int[this.mitglieder.length];

		int[] abzuege = new int[this.mitglieder.length];
		
		// Zuerst anteilsmaessig verteilen. Beginne mit dem "bevorzugten Spieler"
		// Diesem Spieler wird immer mindestens ein Raumer abgezogen.
		int rest = this.subtract2(bevorzugterSpieler, abzuege, anz, anz, true);
		
		if (rest > 0)
		{
			// Rest auf die anderen Spieler in zufaelliger Reihenfolge verteilen
			int[] seq = Utils.randomList(this.mitglieder.length);
			
			for (int runde = 0; runde < 2; runde++)
			{
				// In der ersten Runde wenig radikal. In der zweiten Runde dann
				// immer mindestens einen Raumer abziehen
				for (int i = 0; i < this.mitglieder.length; i++)
				{
					int spieler = seq[i];
					
					if (!this.istMitglied(spieler) || spieler == bevorzugterSpieler)
						continue;

					rest = this.subtract2(spieler, abzuege, anz, rest, (i==1));
					
					if (rest <= 0)
						break;
				}
				
				if (rest <= 0)
					break;
			}
		}
		
		// Von den bestehenden Werten abziehen
		for (int sp = 0; sp < this.mitglieder.length; sp++)
			this.subRaumer(sp, abzuege[sp]);
		
		return abzuege;
	}
	
	private int subtract2(
			int spieler,
			int[] abzuege, 
			int anz, 
			int rest, 
			boolean radikal)
	{
		if (!this.istMitglied(spieler))
			return rest;
		
		double anzBeginn = (double)this.getAnz(spieler);

		if (anzBeginn > 0)
		{
			int abzug = Utils.round((anzBeginn / (double)this.getSum()) * (double)anz);
			
			if (radikal && abzug == 0)
				abzug = 1;
			
			if (abzug > rest)
				abzug = rest;

			abzuege[spieler] += abzug;
			
			rest -= abzug;
		}
		
		return rest;
	}	
	
	public void spielerErsetzen(int spielerAlt, int spielerNeu)
	{
		if (!this.istMitglied(spielerAlt))
			return;
		
		if (spielerNeu != Constants.BESITZER_NEUTRAL)
			this.addRaumer(spielerNeu, this.getAnz(spielerAlt));
		
		this.removeSpieler(spielerAlt, true);
	}
	
	public int getAnzahlMitglieder()
	{
		int anz = 0;
		
		for (int sp = 0; sp < this.mitglieder.length; sp++)
			if (this.mitglieder[sp])
				anz++;
		
		return anz;
	}
	
	public boolean[] getMitglieder()
	{
		return this.mitglieder;
	}
	
	public int[] aufraeumen()
	{
		int[] abzuege = new int[this.getAnzSp()];
		
		for (int sp = 0; sp < this.getAnzSp(); sp++)
		{
			if (!this.mitglieder[sp])
			{
				abzuege[sp] = this.raumer[sp];
				this.raumer[sp] = 0;
			}
		}
		
		return abzuege;
	}
}
