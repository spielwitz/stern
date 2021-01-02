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
import java.util.ArrayList;

@SuppressWarnings("serial") 
class ReplayPlanetenlisteDisplayContent implements Serializable
{
	private int t; // Text-Indices, als ein einziger Json-String
	private int c; // Textfarben der Zeilen als serialisiertes Byte-Array
	
	ReplayPlanetenlisteDisplayContent(
			PlanetenlisteDisplayContent pdc,
			Replays replays) 
	{
		if (pdc != null)
		{
			Integer[] tArray = replays.addStringArrayList2(pdc.getText());
			
			this.t = replays.addObject(tArray);
			this.c = replays.addObject(pdc.getTextCol());
		}
		else
		{
			this.t = -1;
			this.c = -1;
		}
	}
	
	PlanetenlisteDisplayContent getPlanetenlisteDisplayContent(
			Replays replays)
	{
		Integer[] textIndexArray = (Integer[])replays.fromJson(
				replays.getString(this.t),
				Integer[].class);
		
		ArrayList<String> text = replays.getStringArrayList(textIndexArray);
		
		Byte[] textColArray = (Byte[])replays.fromJson(
				replays.getString(this.c),
				Byte[].class);
		
		ArrayList<Byte> textCol = null;
		
		if (textColArray != null)
		{
			textCol = new ArrayList<Byte>(textColArray.length);
		
			for (Byte col: textColArray)
				textCol.add(col);
		}
		
		return new PlanetenlisteDisplayContent(
				text,
				textCol);
	}
}
