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
			release = Constants.BUILD_NO_INFO;
		}
	}
}
