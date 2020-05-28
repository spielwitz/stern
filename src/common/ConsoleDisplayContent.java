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
class ConsoleDisplayContent implements Serializable
{
	// Ein Client meldet sich am Server an. Jeder Client ist gleichwertig!
	// Jeder Tastendruck wird an den Server per Event weitergeleitet und dort ueberprueft.
	// Wenn ein Client die Verbindung verliert, können die anderen Client übernehmen
	
	private String[] t; // Text, der in der Konsole angezeigt wird. Jedes Element ist eine Zeile
	private byte[] c; // Textfarbe einer Zeile als Farbindex
	private ArrayList<ConsoleKey> k; // Zeigt an, welche Keys gedrueckt werden koennen, z.B. [R]aumer
	private String h;
	private byte a;
	private int o;
	
	private boolean w;	// Wenn true, dann wird ein blinkender Cursor angezeigt.

	public String getHeaderText() {
		return h;
	}

	public String[] getText() {
		return t;
	}

	public byte[] getTextCol() {
		return c;
	}

	public ArrayList<ConsoleKey> getKeys() {
		return k;
	}

	public boolean isWaitingForInput() {
		return w;
	}
	
	public byte getHeaderCol() {
		return a;
	}

	ConsoleDisplayContent(String[] text, byte[] textCol,
			ArrayList<ConsoleKey> keys, String headerText, byte headerCol, int outputLine, boolean waitingForInput) {
		super();
		this.t = text;
		this.c = textCol;
		this.k = keys;
		this.h = headerText;
		this.a = headerCol;
		this.w = waitingForInput;
		this.o = outputLine;
	}

	public int getOutputLine() {
		return o;
	}

}
