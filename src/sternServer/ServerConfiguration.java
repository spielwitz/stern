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

package sternServer;

import java.security.PrivateKey;

import com.google.gson.Gson;

import commonServer.CryptoLib;
import commonServer.LogEventType;
import commonServer.ServerConstants;

class ServerConfiguration
{
	String url;
	int port;
	String adminEmail;
	LogEventType logLevel;
	
	String serverPrivateKey; 
	String serverPublicKey;	
	
	String locale;
	
	transient PrivateKey serverPrivateKeyObject;
	
	public ServerConfiguration()
	{
		this.port = ServerConstants.SERVER_PORT;
		this.logLevel = LogEventType.valueOf(ServerConstants.SERVER_LOGLEVEL);
	}
	
	String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	static ServerConfiguration fromJson(String json) throws Exception
	{
		Gson gson = new Gson();
		ServerConfiguration serverConfiguration = gson.fromJson(json, ServerConfiguration.class);
		
		serverConfiguration.serverPrivateKeyObject = CryptoLib.decodePrivateKeyFromBase64(serverConfiguration.serverPrivateKey);
		
		return serverConfiguration;
	}
	
}
