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

import java.util.Comparator;

class AuswertungEreignis implements Comparator<AuswertungEreignis>
{
	private int tag; // Ein Wert zwischen 1 und 365. Stellt den Ereigniszeitpunkt innerhalb eines Jahres da
	AuswertungEreignisTyp typ;
	Point feld;
	Flugobjekt obj;
	
	public AuswertungEreignis()
	{
	}
	AuswertungEreignis(AuswertungEreignisTyp typ, Flugobjekt obj)
	{
		this.obj = obj;
		this.typ = typ;
	}
	
	AuswertungEreignis(AuswertungEreignisTyp typ, int tag, Flugobjekt obj)
	{
		this.obj = obj;
		this.typ = typ;
		this.tag = tag;
	}
	
	public int getTag()
	{
		return this.tag;
	}
	
	public void setTag(double bruchteilJahr) // 0 = Jahresanfang, 1 = Jahresende
	{
		// Umrechnen auf die Tage 1 bis 366(!)
		// bruchteilJahr = 0 => Tag 1
		// bruchteilJahr = 1 => Tag 366
		this.tag = Utils.round(bruchteilJahr * (double)Constants.ANZ_TAGE_JAHR) + 1;
	}
	
	static double getBruchteilTag(int tag)
	{
		// Tag: Ein Wert zwischen 1 und 366
		return (double)(tag-1) / (double)Constants.ANZ_TAGE_JAHR;
	}
	
	@Override
	public int compare(AuswertungEreignis o1, AuswertungEreignis o2) 
	{
		// Zwei Ereignisse sind gleich, wenn sie am gleichen Tag stattfinden,
		// vom selben Typ sind und sich auf dasselbe Feld beziehen
		if (o1.tag > o2.tag)
			return 1;
		else if (o1.tag < o2.tag)
			return -1;
		else
		{
			if (o1.typ == o2.typ)
			{
				if (o1.feld.getX() == o2.feld.getX() &&
					o1.feld.getY() == o2.feld.getY())
				
					return 0;
				else
					return 1;
			}
			else if (o1.typ == AuswertungEreignisTyp.SEKTOR_BETRETEN && o2.typ == AuswertungEreignisTyp.ANKUNFT)
				// Ankunftsereignis muss nach Sektor-betreten-Ereignis stattfinden
				return -1;
			else
				return 1;
				
		}
	}
}
