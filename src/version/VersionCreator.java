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

package version;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.Constants;
import common.ReleaseGetter;

public class VersionCreator { // NO_UCD (unused code)

	public static void main(String[] args)
	{
		// Letzte Versionsdatei lesen
		String versionString = readFile(args[0], Constants.RELEASE_FILE_NAME);
		
		if (versionString != null)
		{
			// Build hochzaehlen
			int buildInt = Integer.parseInt(versionString);
			versionString = getBuildString(buildInt + 1);
		}
		else
		{
			versionString = getBuildString(1);
		}
		
		writeFile(versionString, args[0], Constants.RELEASE_FILE_NAME);
		writeFile(Constants.BUILD_COMPATIBLE, args[0], Constants.RELEASE_RECOMMENDED_FILE_NAME);
		
		String dirBin = Paths.get(args[0], "bin").toString();
		writeFile(versionString, dirBin, Constants.RELEASE_FILE_NAME);
		writeFile(Constants.BUILD_COMPATIBLE, dirBin, Constants.RELEASE_RECOMMENDED_FILE_NAME);
		
		// Build in HTML-Seite anpassen
		String sWeb = readFile(args[1], "DownloadTemplate.html");		
		sWeb = sWeb.replaceAll("########", ReleaseGetter.getRelease());		
		writeFile(sWeb, args[1], "Download.html");
				
		System.out.println("Versionsdatei mit Build "+versionString+ " erzeugt");		
	}
	
	private static void writeFile(String release, String dir, String fileName)
	{
		Path path = Paths.get(dir, fileName);
		String s = path.toString();
		try {
			
			FileWriter writer = new FileWriter(s);
			writer.write(release);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String readFile(String dir, String fileName)
	{
		Path path = Paths.get(dir, fileName);
		
		try
		{
			byte[] encoded = Files.readAllBytes(path);
			return new String(encoded, StandardCharsets.UTF_8); 
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	private static String getBuildString(int arg)
	{
		return String.format("%04d", arg);
	}
}
