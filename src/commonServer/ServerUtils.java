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

package commonServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ServerUtils
{
	public static String getHomeFolder()
	{
		return System.getProperty("user.dir");
	}
	
	public static String getCredentialFileName(String userId, String url, int port)
	{
		String userIdTrimmed = userId.replaceAll("[^a-zA-Z0-9.-]", "_");
		String urlTrimmed = url.replaceAll("[^a-zA-Z0-9.-]", "_");
		return userIdTrimmed + "_" + urlTrimmed + "_" + port; 
	}
	
	public static ClientUserCredentials readClientUserCredentials(String fileName)
	{
		ClientUserCredentials aca = null;
		
		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(fileName))))
		{
			String json = br.readLine();
			aca = ClientUserCredentials.fromJson(json);
		} catch (Exception e)
		{
		}
		
		return aca;
	}
	
	public static boolean writeClientUserCredentials(ClientUserCredentials cuc, String fileName)
	{
		boolean success = true;
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName)))
		{
			String text = cuc.toJson();
			bw.write(text);			
		} catch (IOException e)
		{
			success = false;
		}
		
		return success;
	}
}
