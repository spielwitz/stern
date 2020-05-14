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

import java.io.InputStream;
import java.util.Scanner;

public class ReleaseGetter
{
	private static String release;
	
	public static String getRelease()
	{
		if (release == null)
			new ReleaseGetter();
		
		return release;
	}
	
	public static String format(String unformattedString)
	{
		String jahr = unformattedString.substring(0, 4);
		String monat = unformattedString.substring(4, 6);
		String tag = unformattedString.substring(6, 8);
		
		String stunde = unformattedString.substring(8, 10);
		String minute = unformattedString.substring(10, 12);
		
		return SternResources.ReleaseFormatted(
				false, tag, monat, jahr, stunde, minute);
	}
	
	private ReleaseGetter()
	{
		InputStream inputStream = getClass().getResourceAsStream("/release.txt");
		try
		{
			@SuppressWarnings("resource")
			Scanner s = new Scanner(inputStream).useDelimiter("\\A");
			release = s.hasNext() ? s.next() : "";
		}
		catch (Exception e)
		{
			release = Constants.NO_BUILD_INFO;
		}
	}
}
