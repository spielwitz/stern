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
public class Spieler implements Serializable
{
	private String name; // Max. 10 Buchstaben
	private String emailAdresse;
	private byte colIndex; // Farbwert, mit der eine Instanz von java.awt.Color erzeugt wird
	private boolean istComputer; // Ist ein Computer-Spieler
	private boolean istEmail; // Ist ein E-Mail-Spieler
	private boolean tot; // Spieler ist ausgeschieden
	
	public Spieler(String name, String emailAdresse, byte colIndex, boolean istComputer, boolean istEmail) {
		super();
		this.name = name;
		this.emailAdresse = emailAdresse;
		this.colIndex = colIndex;
		this.istComputer = istComputer;
		this.istEmail = istEmail;
	}

	public String getName() {
		return name;
	}
	
	public String getEmailAdresse() {
		return this.emailAdresse;
	}

	public byte getColIndex() {
		return colIndex;
	}
	
	public void setColIndex(byte colIndex)
	{
		this.colIndex = colIndex;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setEmailAdresse(String emailAdresse)
	{
		this.emailAdresse = emailAdresse;
	}
	
	public boolean istComputer()
	{
		return this.istComputer;
	}
	
	public void setComputer(boolean computer)
	{
		this.istComputer = computer;
	}
	
	public boolean istEmail()
	{
		return this.istEmail;
	}
	
	public void setEmail(boolean email)
	{
		this.istEmail = email;
	}
	
	public void setTot(boolean tot)
	{
		this.tot = tot;
	}
	
	boolean istTot()
	{
		return this.tot;
	}
}
