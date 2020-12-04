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

package common;

import java.io.Serializable;
import java.util.ArrayList;

// Diese Klasse speichert einen Punkt, der ursprünglich ein PointHighRes war,
// als Integer um Faktor 100 erhöht ab. Also aus (3.1234564,-223.44663456) wird
// (312, -22345). Damit wird beim Abspeichern der Auswertungen nicht so viel Speicher
// verwendet
@SuppressWarnings("serial") class PointLowRes implements Serializable
{
	private int x;
	private int y;
	
	PointLowRes(Point pt)
	{
		this.x = Utils.round(pt.x * 100.);
		this.y = Utils.round(pt.y * 100.);
	}
	
	Point toPoint()
	{
		return new Point(
				(double)this.x / (double)100, 
				(double)this.y / (double)100);
	}
	
	static ArrayList<PointLowRes> toArrayList(ArrayList<Point> list)
	{
		if (list == null)
			return null;
		
		ArrayList<PointLowRes> resultList = new ArrayList<PointLowRes>(list.size());
		
		for (int i = 0; i < list.size(); i++)
			resultList.add(i, new PointLowRes(list.get(i)));
		
		return resultList;
	}
	
	static ArrayList<Point> fromArrayList(ArrayList<PointLowRes> list)
	{
		if (list == null)
			return null;
		
		ArrayList<Point> resultList = new ArrayList<Point>(list.size());
		
		for (int i = 0; i < list.size(); i++)
			resultList.add(i, list.get(i).toPoint());
		
		return resultList;
	}
	
	static ArrayList<Point> fromArray(PointLowRes[] list)
	{
		if (list == null)
			return null;
		
		ArrayList<Point> resultList = new ArrayList<Point>(list.length);
		
		for (int i = 0; i < list.length; i++)
			resultList.add(i, list[i].toPoint());
		
		return resultList;
	}
}
