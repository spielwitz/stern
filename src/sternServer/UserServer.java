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

import java.security.PublicKey;
import java.util.HashSet;

import com.google.gson.Gson;

import commonServer.RsaCrypt;

public class UserServer
{
	public String userId; // z.B. "Zorro", 3-10 Zeichen, A-Z, a-z, 0-9
	
	public boolean active; // Benutzer ist aktiv
	
	public String userPublicKey; // Public key des Users: Verschluesselung der Response auf Server-Seite
	public String name;
	
	public String email;
	
	public String activationCode;
	
	public transient HashSet<String> games;
	public transient PublicKey userPublicKeyObject;
	
	public UserServer(String userId)
	{
		this.userId = userId;
		this.email = "";
		this.name = "";
		
		this.games = new HashSet<String>();
	}
	
	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static UserServer fromJson(String json) throws Exception
	{
		Gson gson = new Gson();
		UserServer user = gson.fromJson(json, UserServer.class);
		user.games = new HashSet<String>();
		
		user.userPublicKeyObject = RsaCrypt.decodePublicKeyFromBase64(user.userPublicKey);
		
		return user;
	}
}
