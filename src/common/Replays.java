/**	STERN, das Strategiespiel.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.Gson;

@SuppressWarnings("serial")
class Replays implements Serializable 
{
	static Gson serializer = new Gson();
	
	private ArrayList<String> serializedObjects = new ArrayList<String>();
	
	private Hashtable<Integer,ArrayList<ReplayEvent>> replayEventsPerYear = 
			new Hashtable<Integer,ArrayList<ReplayEvent>>();
	
	private transient Hashtable<String,Integer> map;
	
	void add(int jahr, ArrayList<ScreenDisplayContent> sdcs)
	{
		ArrayList<ReplayEvent> replayEvents = new ArrayList<ReplayEvent>();
		
		for (ScreenDisplayContent sdc: sdcs)
		{
			// Jeder ScreenDisplayContent entspricht einem ReplayEvent
			replayEvents.add(new ReplayEvent(sdc, this));
		}
		
		this.replayEventsPerYear.put(jahr, replayEvents);
	}
	
	ArrayList<ScreenDisplayContent> get(int jahr)
	{
		if (replayEventsPerYear.containsKey(jahr))
		{
			ArrayList<ReplayEvent> replayEvents = this.replayEventsPerYear.get(jahr);
			
			ArrayList<ScreenDisplayContent> sdcs = new ArrayList<ScreenDisplayContent>(replayEvents.size());
			
			for (ReplayEvent replayEvent: replayEvents)
			{
				sdcs.add(replayEvent.getScreenDisplayContent(this));
			}
			
			return sdcs;
		}
		else
			return null;
	}
	
	int addString(String text)
	{
		if (text == null)
			return -1;
		
		if (this.map == null)
		{
			// Puffer aufbauen
			this.map = new Hashtable<String,Integer>();
			
			for (int i = 0; i < this.serializedObjects.size(); i++)
				this.map.put(this.serializedObjects.get(i), i);
		}

		Integer index = this.map.get(text);
		
		if (index == null)
		{
			serializedObjects.add(text);
			index = serializedObjects.size() - 1;
			this.map.put(text, index);
		}
		
		return index;
	}
	
	int addObject(Object obj)
	{
		if (obj == null)
			return -1;
		else
			return this.addString(serializer.toJson(obj));
	}
	
	int[] addStringArray(String[] array)
	{
		if (array == null)
			return null;
		
		int[] intArray = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
		{
			intArray[i] = this.addString(array[i]);
		}
		
		return intArray;
	}
	
	Integer[] addStringArrayList2(ArrayList<String> array)
	{
		if (array == null)
			return null;
		
		Integer[] intArray = new Integer[array.size()];
		
		for (int i = 0; i < array.size(); i++)
		{
			intArray[i] = this.addString(array.get(i));
		}
		
		return intArray;
	}
	
	String[] getStringArray(int[] indices)
	{
		if (indices == null)
			return null;
		
		String[] array = new String[indices.length];
		
		for (int i = 0; i < indices.length; i++)
		{
			array[i] = this.getString(indices[i]);
		}
		
		return array;
	}
	
	ArrayList<String> getStringArrayList(Integer[] indices)
	{
		if (indices == null)
			return null;
		
		ArrayList<String> array = new ArrayList<String>(indices.length);
		
		for (int i = 0; i < indices.length; i++)
		{
			array.add(this.getString(indices[i]));
		}
		
		return array;
	}
	
	String getString(int index)
	{
		if (index == -1)
			return null;
		
		return serializedObjects.get(index);
	}

	<T> Object fromJson(String json, Class<T> expectedClass)
	{
		if (json == null)
			return null;
		
		return Replays.serializer.fromJson(json, expectedClass);
	}	
	
	boolean auswertungExists(int jahr)
	{
		return this.replayEventsPerYear.containsKey(jahr);
	}
	
	int getReplaySize(int jahr)
	{
		if (this.auswertungExists(jahr))
			return this.replayEventsPerYear.get(jahr).size();
		else
			return 0;
	}
}
