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

import java.io.Serializable;

import com.google.gson.Gson;

import common.EmailTransportBase;

@SuppressWarnings("serial")
public class ResponseMessageChangeUser extends EmailTransportBase implements Serializable
{
	public String userId;
	public String activationCode;
	
	public String serverUrl;
	public int serverPort;
	public String adminEmail;
	
	public String serverPublicKey;
	
	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static ResponseMessageChangeUser fromJson(String json)
	{
		Gson gson = new Gson();
		ResponseMessageChangeUser obj = gson.fromJson(json, ResponseMessageChangeUser.class);
		
		return obj;
	}
}
