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

package commonUi;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import common.Constants;

public class DialogFontHelper
{
	private static Font font;
	private static Font fontBasis ;
	
	public static Font getFont()
	{
		if (font == null)
			new DialogFontHelper();
		
		return font;
	}
	
	public static Font getFontBasis()
	{
		if (fontBasis == null)
			new DialogFontHelper();
		
		return fontBasis;
	}
	
	private DialogFontHelper()
	{
		try {
			fontBasis = Font.createFont( Font.TRUETYPE_FONT,
					getClass().getResourceAsStream(Constants.FONT_NAME));
			font = fontBasis.deriveFont(Constants.DIALOG_FONT_SIZE);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
