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
import java.util.HashSet;
import java.util.Hashtable;

@SuppressWarnings("serial") 
class PlanetenEditorDisplayContent implements Serializable
{
	private ObjektTyp typMarkiert;
	private Hashtable<ObjektTyp,String> anzahl;
	private HashSet<ObjektTyp> kaufNichtMoeglich;
	private HashSet<ObjektTyp> verkaufNichtMoeglich;
	
	private boolean readOnly;
	private boolean reduzierteAnzeige;
	private byte farbeSpieler;
	private int evorrat;
	private boolean kommandozentrale;
	
	
	PlanetenEditorDisplayContent(ObjektTyp typMarkiert,
			Hashtable<ObjektTyp, String> anzahl,
			HashSet<ObjektTyp> kaufNichtMoeglich,
			HashSet<ObjektTyp> verkaufNichtMoeglich,
			byte farbeSpieler,
			int evorrat,
			boolean kommandozentrale,
			boolean readOnly,
			boolean reduzierteAnzeige) {
		super();
		this.typMarkiert = typMarkiert;
		this.anzahl = anzahl;
		this.kaufNichtMoeglich = kaufNichtMoeglich;
		this.verkaufNichtMoeglich = verkaufNichtMoeglich;
		this.farbeSpieler = farbeSpieler;
		this.evorrat = evorrat;
		this.kommandozentrale = kommandozentrale;
		this.readOnly = readOnly;
		this.reduzierteAnzeige = reduzierteAnzeige;
	}


	public ObjektTyp getTypMarkiert() {
		return typMarkiert;
	}


	public boolean isReadOnly()
	{
		return this.readOnly;
	}
	
	public boolean isReduzierteAnzeige()
	{
		return this.reduzierteAnzeige;
	}

	public Hashtable<ObjektTyp, String> getAnzahl() {
		return anzahl;
	}

	public HashSet<ObjektTyp> getKaufNichtMoeglich() {
		return kaufNichtMoeglich;
	}


	public HashSet<ObjektTyp> getVerkaufNichtMoeglich() {
		return verkaufNichtMoeglich;
	}


	public byte getFarbeSpieler() {
		return farbeSpieler;
	}


	public int getEvorrat() {
		return evorrat;
	}


	public boolean isKommandozentrale() {
		return kommandozentrale;
	}
}
