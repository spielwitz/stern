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
public class MineHistorie implements Serializable
{
	private int sp; // Gelegt von Spieler
	private ObjektTyp typ;
	
	public MineHistorie(int sp, ObjektTyp typ) {
		super();
		this.sp = sp;
		this.typ = typ;
	}

	public int getSp() {
		return sp;
	}

	public ObjektTyp getTyp() {
		return typ;
	}
	
	public void setSp(int sp)
	{
		this.sp = sp;
	}
}
