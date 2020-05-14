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

import java.util.Hashtable;

import com.google.gson.Gson;

import common.Archiv;

public class ResponseMessageGetEvaluations 
{
	public Hashtable<Integer,Archiv> archiv; // Punktestaende und Auswertungen pro Jahr
	
	public String toJson()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static ResponseMessageGetEvaluations fromJson(String json)
	{
		Gson gson = new Gson();
		ResponseMessageGetEvaluations obj = gson.fromJson(json, ResponseMessageGetEvaluations.class);
		
		return obj;
	}	
}
