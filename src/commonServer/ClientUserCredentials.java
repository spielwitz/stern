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

import java.security.PrivateKey;
import java.security.PublicKey;

import com.google.gson.Gson;

public class ClientUserCredentials
{
	public String userId;
	public String url;
	public int port;
	public String userPrivateKey;
	public String serverPublicKey;
	public String adminEmail;
	
	public transient PrivateKey userPrivateKeyObject;
	public transient PublicKey serverPublicKeyObject;
	
	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	static ClientUserCredentials fromJson(String json) throws Exception
	{
		Gson gson = new Gson();
		ClientUserCredentials user = gson.fromJson(json, ClientUserCredentials.class);
		
		user.serverPublicKeyObject = CryptoLib.decodePublicKeyFromBase64(user.serverPublicKey);
		user.userPrivateKeyObject = CryptoLib.decodePrivateKeyFromBase64(user.userPrivateKey); 
		
		return user;
	}
}
