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

class Flugzeit implements Comparator<Flugzeit> 
{
	int jahr;
	private int tag;
	Flugobjekt obj;
	
	Flugzeit() {}
	
	Flugzeit(int jahr, int tag)
	{
		this.jahr = jahr;
		this.tag = tag;
	}
	
	String toOutputString(boolean symbol)
	{
		if (tag < Constants.ANZ_TAGE_JAHR)
			return SternResources.FlugzeitOutput(
					symbol, 
					Integer.toString(jahr+1), 
					Integer.toString(tag+1));
		else
			return SternResources.FlugzeitOutputJahresende(
					symbol, 
					Integer.toString(jahr+1));
	}
	
	String toOutputStringDistanzmatrix(boolean symbol)
	{
		if (tag < Constants.ANZ_TAGE_JAHR)
			return SternResources.FlugzeitOutputShort(
					symbol, 
					"+"+Integer.toString(jahr), 
					Integer.toString(tag+1));
		else
			return SternResources.FlugzeitOutputJahresendeShort(
					symbol, 
					"+"+Integer.toString(jahr));
	}

	@Override
	public int compare(Flugzeit o1, Flugzeit o2) 
	{
		int tage1 = o1.jahr * Constants.ANZ_TAGE_JAHR + o1.tag;
		int tage2 = o2.jahr * Constants.ANZ_TAGE_JAHR + o2.tag;
		
		if (tage1 == tage2)
			return 0;
		if (tage1 > tage2)
			return 1;
		else
			return -1;
	}
}
