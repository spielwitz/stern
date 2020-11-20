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

@SuppressWarnings("serial") 
class ReplaySpielfeldDisplayContent implements Serializable
{
	private int p; // Planeten = ArrayList<SpielfeldPlanetDisplayContent> als einziges Json
	private int f; // Markierte Felder = ArrayList<PointLowRes> als einziges Json
	private int o; // Objekte = ArrayList<SpielfeldPointDisplayContent> o als einziges Json
	private int m; // Minen = 
	
	ReplaySpielfeldDisplayContent(
			SpielfeldDisplayContent sdc,
			Replays replays)
	{
		if (sdc == null)
			return;
		
		if (sdc.getPlanets() != null)
		{
			ArrayList<Integer> pIndices = new ArrayList<Integer>(sdc.getPlanets().size());
			
			for (SpielfeldPlanetDisplayContent p: sdc.getPlanets())
			{
				pIndices.add(replays.addObject(p));
			}
			
			this.p = replays.addObject(pIndices);
		}
		else
			this.p = -1;
		
		
		if (sdc.getMarkedFieldsRaw() != null)
		{
			this.f = replays.addObject(sdc.getMarkedFieldsRaw()); 
		}
		else
			this.f = -1;
		
		if (sdc.getPoints() != null)
		{
			ArrayList<Integer> oIndices = new ArrayList<Integer>(sdc.getPoints().size());
			
			for (SpielfeldPointDisplayContent o: sdc.getPoints())
			{
				oIndices.add(replays.addObject(o));
			}
			
			this.o = replays.addObject(oIndices);
		}
		else
			this.o = -1;
		
		if (sdc.getMinen() != null)
		{
			this.m = replays.addObject(sdc.getMinen());
		}
		else
			this.m = -1;
	}

	SpielfeldDisplayContent getSpielfeldDisplayContent(Replays replays)
	{
		Integer[] pIndicesArray = (Integer[])replays.fromJson(
				replays.getString(this.p), 
				Integer[].class); 

		ArrayList<SpielfeldPlanetDisplayContent> planets = null;
		
		if (pIndicesArray != null)
		{
			planets = new ArrayList<SpielfeldPlanetDisplayContent>(pIndicesArray.length);
		
			for (int i = 0; i < pIndicesArray.length; i++)
			{
				planets.add(
						(SpielfeldPlanetDisplayContent)replays.fromJson(
								replays.getString(pIndicesArray[i]), 
								SpielfeldPlanetDisplayContent.class));
			}
		}

		PointLowRes[] markedFieldsArray = 
				(PointLowRes[])replays.fromJson(replays.getString(this.f), PointLowRes[].class);

		ArrayList<Point2D.Double> markedFields = PointLowRes.fromArray(markedFieldsArray);
		
		Integer[] oIndicesArray = (Integer[])replays.fromJson(
				replays.getString(this.o), 
				Integer[].class); 
		
		ArrayList<SpielfeldPointDisplayContent> points = null; 
				
		if (oIndicesArray != null)
		{
			points = new ArrayList<SpielfeldPointDisplayContent>(oIndicesArray.length);
			
			for (int i = 0; i < oIndicesArray.length; i++)
			{
				points.add(
						(SpielfeldPointDisplayContent)replays.fromJson(
								replays.getString(oIndicesArray[i]), 
								SpielfeldPointDisplayContent.class));
			}
		}
		
		ArrayList<MinenfeldDisplayContent> minen = null;
		
		if (this.m >= 0)
		{
			MinenfeldDisplayContent[] minenArray = 
					(MinenfeldDisplayContent[])replays.fromJson(
							replays.getString(this.m), 
							MinenfeldDisplayContent[].class);
			
			minen = new ArrayList<MinenfeldDisplayContent>(minenArray.length);
			
			for (MinenfeldDisplayContent m: minenArray)
				minen.add(m);
		}
		
		return new SpielfeldDisplayContent(
				planets,
				markedFields,
				null,
				points,
				minen);
	}
}
