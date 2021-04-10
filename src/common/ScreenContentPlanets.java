/**	STERN - a strategy game
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
import java.util.ArrayList;

@SuppressWarnings("serial") 
class ScreenContentPlanets implements Serializable
{
	private String l;
	private byte o;
	private ArrayList<String> t;
	private ArrayList<Byte> c;
	
	ScreenContentPlanets(
			String title,
			byte titleColor,
			ArrayList<String> text,
			ArrayList<Byte> textColorIndices) 
	{
		super();
		this.l = title;
		this.o = titleColor;
		this.t = text;
		this.c = textColorIndices;
	}
	
	public String getTitle()
	{
		return this.l;
	}

	public byte getTitleColor()
	{
		return this.o;
	}
	
	public ArrayList<String> getText() {
		return t;
	}
	public ArrayList<Byte> getTextColorIndices() {
		return c;
	}
}
