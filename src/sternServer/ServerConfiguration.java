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

package sternServer;

import java.security.PrivateKey;

import com.google.gson.Gson;

import commonServer.LogEventType;
import commonServer.RsaCrypt;
import commonServer.ServerConstants;

public class ServerConfiguration
{
	public String url;
	public int port;
	public String adminEmail;
	public LogEventType logLevel;
	
	public String serverPrivateKey; 
	public String serverPublicKey;	
	
	public String locale;
	
	public transient PrivateKey serverPrivateKeyObject;
	
	public ServerConfiguration()
	{
		this.port = ServerConstants.SERVER_PORT;
		this.logLevel = LogEventType.valueOf(ServerConstants.SERVER_LOGLEVEL);
	}
	
	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static ServerConfiguration fromJson(String json) throws Exception
	{
		Gson gson = new Gson();
		ServerConfiguration sc = gson.fromJson(json, ServerConfiguration.class);
		
		sc.serverPrivateKeyObject = RsaCrypt.decodePrivateKeyFromBase64(sc.serverPrivateKey);
		
		return sc;
	}
	
}
