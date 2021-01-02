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

import java.awt.Color;

public class Colors
{	
	//public static final int[] FARBE_SPIELER = new int[] {16711680, 65280, 65535, 16776960, 16711935, 32832};
	private static Color[] colors;
	private static Color[] colorsDarker;
	
	static final byte INDEX_SCHWARZ = 0;
	static final byte INDEX_WEISS = 1;
	public static final byte INDEX_NEUTRAL = 2;
	public static final byte SPIELER_FARBEN_OFFSET = 3;
	
	static
	{
		colors = new Color[] 
			{
					new Color(0),								 // 0: Schwarz
					new Color(255 + 256 *255 + 256 * 256 * 255), // 1: Weiss
					new Color(128 + 256 *128 + 256 * 256 * 128), // 2: Neutral (grau)
					new Color(getColor("#F44336")),              // 3: Spieler 1
					new Color(getColor("#8BC34A")), 			 // 4: Spieler 2
					new Color(getColor("#03A9F4")),              // 5: Spieler 3
					new Color(getColor("#FFC107")),              // 6: Spieler 4
					new Color(getColor("#9C27B0")),              // 7: Spieler 5
					new Color(getColor("#009688"))			     // 8: Spieler 6  
			};
		
		colorsDarker = new Color[colors.length + 1];
		
		colorsDarker[0] = new Color(0);
		
		for (int i = 0; i < colors.length; i++)
			colorsDarker[i+1] = getColorDarker(colors[i]);
	}
	
	public static Color get(byte index)
	{
		if (index < 0)
			return colorsDarker[-index];
		else
			return colors[index];
	}
	
	static byte getColorIndexDarker(byte index)
	{
		return (byte)(-(index + 1));
	}

	private static int getColorRGB(int red, int green, int blue)
	{
		return red * 256 * 256 + green * 256 + blue;
	}
	
	private static int getColor(String hex)
	{
		Color col = Color.decode(hex);
		return getColorRGB(col.getRed(), col.getGreen(), col.getBlue());
	}
	
	private static Color getColorDarker(Color color)
	{
		int red = (int)(color.getRed() *0.66);
		int green = (int)(color.getGreen() *0.66);
		int blue = (int)(color.getBlue() *0.66);
		
		return new Color(getColorRGB(red, green, blue));
	}
	
	static Color getColorDarker2(Color color)
	{
		int red = (int)(color.getRed() *0.33);
		int green = (int)(color.getGreen() *0.33);
		int blue = (int)(color.getBlue() *0.33);
		
		return new Color(getColorRGB(red, green, blue));
	}


}
