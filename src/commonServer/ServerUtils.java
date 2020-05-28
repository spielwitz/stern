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

package commonServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ServerUtils
{
	public static final String ARG_PORT = "port";
	public static final String ARG_HOSTNAME = "host";
	public static final String ARG_LOGLEVEL = "log";
	public static final String ARG_CLIENT_CREDENTIALS = "credentials";
	
	public static byte[] convertIntToByteArray(int a)
	{
		return new byte[] {
		        (byte) ((a >> 24) & 0xFF),
		        (byte) ((a >> 16) & 0xFF),   
		        (byte) ((a >> 8) & 0xFF),   
		        (byte) (a & 0xFF)
		    };
	}
	
	public static int convertByteArrayToInt(byte[] b)
	{
		return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24; 
	}
	
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
	

}
