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

@SuppressWarnings("serial")
public class Point implements Serializable
{
	private int x;
	private int y;
	
	public Point(int x, int y)
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
	
	public double dist(Point p)
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
	
	public Point klon()
	{
		return new Point(this.x, this.y);
	}
	
	public static Point getRandom(int dx, int dy, int breite, int hoehe)
	{
		int x = Utils.random(breite) + dx;
		int y = Utils.random(hoehe) + dy;
		
		return new Point(x,y);
	}
	
	public boolean isPlanetTooClose(Point p)
	{
		return (Math.abs(this.x - p.x) < Constants.PLANETEN_MINDESTABSTAND &&
				Math.abs(this.y - p.y) < Constants.PLANETEN_MINDESTABSTAND);
	}	
}
