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

import java.util.Comparator;

class AuswertungEreignisPatrouille implements Comparator<AuswertungEreignisPatrouille>
{
	int winkel; // Ein Wert zwischen 0 und 364. Stellt den Erfasungswinkel dar
	
	Point markierungPos;
	Point posObjAnderes;
	Flugobjekt objPatrouille; 
	Flugobjekt objAnderes;
	
	public AuswertungEreignisPatrouille()
	{
		
	}
	
	AuswertungEreignisPatrouille(Flugobjekt objPatrouille, Flugobjekt objAnderes)
	{
		this.objPatrouille = objPatrouille;
		this.objAnderes = objAnderes;
	}
		
	@Override
	public int compare(AuswertungEreignisPatrouille o1, AuswertungEreignisPatrouille o2) 
	{
		if (o1.winkel > o2.winkel)
			return 1;
		else if (o1.winkel < o2.winkel)
			return -1;
		else
			return 0;
	}
}
