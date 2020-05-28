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

@SuppressWarnings("serial") class Point implements Serializable
{
	private int x;
	private int y;
	
	Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	double dist(Point p)
	{
		return Math.sqrt(Math.pow((double)(this.x - p.x), 2) + Math.pow((double)(this.y - p.y), 2));
	}
	
	public String getString()
	{
		// Nur fuer die Schluesselbildung der Minen-Hashtable
		return this.x + ";" + this.y;
	}
	
	public boolean equals(Object obj)
	{
	  if (this==obj) {
	     return true;
	  }
	  if (obj==null) {
	     return false;
	  }
	  if (!(obj instanceof Point )) {
	     return false; // different class
	  }
	  Point other = (Point) obj;
	  
	  if (other.x == this.x && other.y == this.y)
		  return true;
	  else
		  return false;
	}
	
	Point klon()
	{
		return new Point(this.x, this.y);
	}
	
	Point2D.Double toPoint2dDouble()
	{
		return new Point2D.Double(this.x, this.y);
	}
}
