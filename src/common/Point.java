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

package common;

import java.awt.geom.Point2D;
import java.io.Serializable;

@SuppressWarnings("serial") 
class Point extends Point2D.Double implements Serializable
{
	Point(double x, double y)
	{
		super(x, y);
	}

	public String getString()
	{
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
	  if (!(obj instanceof Point ))
	  {
	     return false;
	  }
	  Point other = (Point) obj;
	  
	  if (this.distance(other) < Constants.PRECISION)
		  return true;
	  else
		  return false;
	}
	
	Point klon()
	{
		return new Point(this.x, this.y);
	}	
}
