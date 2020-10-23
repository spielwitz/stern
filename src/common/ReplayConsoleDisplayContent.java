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
class ReplayConsoleDisplayContent implements Serializable
{
	private int[] t; // Text, der in der Konsole angezeigt wird. Jedes Element ist eine Zeile
	private int c; // Textfarben der vier Zeilen als serialisiertes Byte-Array
	private int[] k; // Indices der serialiserten ConsoleKey-Objekte
	private int h; // Index des Header-Texts
	private byte a; // Farbcode des Headers
	private int o; // Output-Zeile
	
	private boolean w;	// Wenn true, dann wird ein blinkender Cursor angezeigt.
	private int p; // Index des serialisierten ConsoleEvaluationProgressBarDisplayContent-Objekts
	
	ReplayConsoleDisplayContent(
			ConsoleDisplayContent cdc, 
			Replays replays)
	{
		if (cdc.getText() != null)
		{
			this.t = replays.addStringArray(cdc.getText());
			this.c = replays.addObject(cdc.getTextCol());
					
			if (cdc.getKeys() != null)
			{
				this.k = new int[cdc.getKeys().size()];
				
				for (int i = 0; i < cdc.getKeys().size(); i++)
				{
					this.k[i] = replays.addObject(cdc.getKeys().get(i));
				}
			}
			
			this.h = replays.addString(cdc.getHeaderText());
			this.a = cdc.getHeaderCol();
			this.o = cdc.getOutputLine();
			this.w = cdc.isWaitingForInput();
			
			this.p = replays.addObject(cdc.getEvaluationProgressBar());
		}
	}
	
	ConsoleDisplayContent getConsoleDisplayContent(Replays replays)
	{
		String[] text = replays.getStringArray(this.t);
		
		byte[] textCol = (byte[])replays.fromJson(
				replays.getString(this.c),
				byte[].class);
		
		ArrayList<ConsoleKey> keys = null;
		
		if (this.k != null)
		{
			keys = new ArrayList<ConsoleKey>(this.k.length);
			
			for (int i = 0; i < this.k.length; i++)
			{
				keys.add((ConsoleKey)replays.fromJson(
						replays.getString(this.k[i]), 
						ConsoleKey.class));
			}
		}
		
		return new ConsoleDisplayContent(
				text, 
				textCol,
				keys, 
				replays.getString(this.h),
				this.a, 
				this.o, 
				this.w,
				(ConsoleEvaluationProgressBarDisplayContent)replays.fromJson(
						replays.getString(this.p), 
						ConsoleEvaluationProgressBarDisplayContent.class));
	}
}
