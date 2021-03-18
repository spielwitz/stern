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

package version;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.Constants;
import common.ReleaseGetter;

public class VersionCreator  // NO_UCD (unused code)
{

	public static void main(String[] args)
	{
		String versionString = readFile(args[0], Constants.FILE_NAME_RELEASE);
		
		if (versionString != null)
		{
			int buildInt = Integer.parseInt(versionString);
			versionString = getBuildString(buildInt + 1);
		}
		else
		{
			versionString = getBuildString(1);
		}
		
		writeFile(versionString, args[0], Constants.FILE_NAME_RELEASE);
		writeFile(Constants.BUILD_IMPORTANT_UPDATE, args[0], Constants.FILE_NAME_RELEASE_RECOMMENDED);
		
		String dirrectoryNameBin = Paths.get(args[0], "bin").toString();
		writeFile(versionString, dirrectoryNameBin, Constants.FILE_NAME_RELEASE);
		writeFile(Constants.BUILD_IMPORTANT_UPDATE, dirrectoryNameBin, Constants.FILE_NAME_RELEASE_RECOMMENDED);
		
		String sWeb = readFile(args[1], "DownloadTemplate.html");		
		sWeb = sWeb.replaceAll("########", ReleaseGetter.getRelease());		
		writeFile(sWeb, args[1], "Download.html");
				
		System.out.println("Version file for build "+versionString+ " created");		
	}
	
	private static void writeFile(String release, String directoryName, String fileName)
	{
		Path path = Paths.get(directoryName, fileName);
		String s = path.toString();
		try {
			
			FileWriter writer = new FileWriter(s);
			writer.write(release);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String readFile(String directoryName, String fileName)
	{
		Path path = Paths.get(directoryName, fileName);
		
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
