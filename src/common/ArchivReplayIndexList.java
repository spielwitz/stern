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
class ArchivReplayIndexList implements Serializable
{
	ConsoleDisplayContent c; // Console-Inhalt
	int p; // Planeten Index
	int s; // Spielfeld Index
	int g; // Ereignistag
		
	private int u; // Pause (0 oder 1)
	
	ArchivReplayIndexList(
			ConsoleDisplayContent c, 
			int p, 
			int s,
			boolean u,
			int g)
	{
		this.c = c;
		this.p = p;
		this.s = s;
		this.u = u ? 1 : 0;
		this.g = g;
	}
	
	public boolean getPause()
	{
		return (u == 1);
	}
}
