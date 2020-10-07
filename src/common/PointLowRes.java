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

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

// Diese Klasse speichert einen Punkt, der ursprünglich ein Point2D.Double war,
// als Integer um Faktor 10 erhöht ab. Also aus (3.1234564,-223.44663456) wird
// (31, -2234). Damit wird beim Abspeichern der Auswertung nicht so viel Speicher
// verwendet
@SuppressWarnings("serial") class PointLowRes implements Serializable
{
	private int x;
	private int y;
	
	PointLowRes(Point2D.Double pt)
	{
		this.x = Utils.round(pt.x * 100.);
		this.y = Utils.round(pt.y * 100.);
	}
	
	Point2D.Double toPoint2D()
	{
		return new Point2D.Double(
				(double)this.x / (double)100, 
				(double)this.y / (double)100);
	}
	
	static ArrayList<PointLowRes> toArrayList(ArrayList<Point2D.Double> list)
	{
		if (list == null)
			return null;
		
		ArrayList<PointLowRes> resultList = new ArrayList<PointLowRes>(list.size());
		
		for (int i = 0; i < list.size(); i++)
			resultList.add(i, new PointLowRes(list.get(i)));
		
		return resultList;
	}
	
	static ArrayList<Point2D.Double> fromArrayList(ArrayList<PointLowRes> list)
	{
		if (list == null)
			return null;
		
		ArrayList<Point2D.Double> resultList = new ArrayList<Point2D.Double>(list.size());
		
		for (int i = 0; i < list.size(); i++)
			resultList.add(i, list.get(i).toPoint2D());
		
		return resultList;
	}
}
