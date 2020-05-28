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

package version;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.Constants;
import common.ReleaseGetter;

public class VersionCreator { // NO_UCD (unused code)

	public static void main(String[] args)
	{
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		String versionString = formatter.format(d);
		String recommendedBuildString = Constants.RECOMMENDED_BUILD;
		
		writeFile(versionString, args[0], Constants.RELEASE_FILE_NAME);
		writeFile(recommendedBuildString, args[0], Constants.RELEASE_RECOMMENDED_FILE_NAME);
		
		// HTML-Seiten anpassen
		Path pathWeb = Paths.get(args[1], "DownloadTemplate.html");
		Path pathWeb2 = Paths.get(args[1], "Download.html");
		
		try {
			byte[] encoded = Files.readAllBytes(pathWeb);
			String sWeb = new String(encoded, StandardCharsets.UTF_8); 
			
			sWeb = sWeb.replaceAll("########", ReleaseGetter.format(versionString));
			
			Files.write(pathWeb2, sWeb.getBytes());
			
			System.out.println("Datei "+pathWeb2.toString()+ " angepasst");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeFile(String release, String dir, String fileName)
	{
		Path path = Paths.get(dir, fileName);
		String s = path.toString();
		try {
			
			FileWriter writer = new FileWriter(s);
			writer.write(release);
			writer.close();
			
			System.out.println("Versionsdatei mit Build "+release+ " erzeugt");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
