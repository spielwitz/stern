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
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Mine implements Serializable
{
	private Point pos; // Ort
	private int staerke; // Momentane Staerke

	private ArrayList<MineHistorie> historie;

	public Mine(Point pos, int staerke, ArrayList<MineHistorie> historie) {
		super();
		this.pos = pos;
		this.staerke = staerke;
		this.historie = historie;
	}

	public Point getPos() {
		return pos;
	}

	public int getStaerke() {
		return staerke;
	}
	
	public void setStaerke(int staerke) {
		this.staerke = staerke;
	}

	public ArrayList<MineHistorie> getHistorie() {
		return historie;
	}
	
	public void addHistorie(MineHistorie historie)
	{
		if (this.historie == null)
			this.historie = new ArrayList<MineHistorie>();
		
		this.historie.add(historie);
	}
	
	public void add(int staerke, MineHistorie historie)
	{
		this.staerke += staerke;
		this.addHistorie(historie);
	}
	
	public Mine getSpielerInfo(int spIndex)
	{
		if (this.historie == null)
			return null;
		
		// Gibt waehrend eines Spieles die Information zurueck,
		// wie stark das Minenfeld urspruenglich war. Wenn ein Spieler
		// ueberhaupt nicht am Feld beteiligt war, kommt null zurueck
		Mine mineClone = new Mine(this.pos, 0, new ArrayList<MineHistorie>());
		
		for (MineHistorie hist: this.historie)
		{
			if (hist.getSp() == spIndex)
			{
				if (hist.getTyp() == ObjektTyp.MINE50)
					mineClone.staerke += 50;
				else if (hist.getTyp() == ObjektTyp.MINE100)
					mineClone.staerke += 100;
				else if (hist.getTyp() == ObjektTyp.MINE250)
					mineClone.staerke += 250;
				else if (hist.getTyp() == ObjektTyp.MINE500)
					mineClone.staerke += 500;
				
				mineClone.historie.add(hist);
			}
		}
		
		if (mineClone.historie.size() == 0)
			return null;
		else
			return mineClone;
	}
}
