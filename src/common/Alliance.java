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

import java.io.Serializable;

@SuppressWarnings("serial") 
class Alliance implements Serializable
{
	private boolean[] members;
	private int[] fightersCount;
	
	Alliance(int playersCount)
	{
		this.members = new boolean[playersCount];
		this.fightersCount = new int[playersCount];
	}
	
	public int getPlayersCount()
	{
		return this.members.length;
	}
	
	private void setFightersCount(int playerIndex, int fightersCount)
	{
		this.members[playerIndex] = true;
		this.fightersCount[playerIndex] = fightersCount;
	}
	void addFightersCount(int playerIndex, int fightersCount)
	{
		if (this.members[playerIndex])
			this.fightersCount[playerIndex] += fightersCount;
		else
			this.setFightersCount(playerIndex, fightersCount);
	}
	
	void subtractFightersCount(int playerIndex, int fightersCount)
	{
		if (this.members[playerIndex])
		{
			this.fightersCount[playerIndex] -= fightersCount;
			if (this.fightersCount[playerIndex] < 0)
				this.fightersCount[playerIndex] = 0;
		}
	}
	
	void addPlayer(int playerIndex)
	{
		this.members[playerIndex] = true;
	}
	
	void removePlayer(int playerIndex, boolean removeFighters)
	{
		this.members[playerIndex] = false;
		
		if (removeFighters)
			this.fightersCount[playerIndex] = 0;
	}
	
	public int getFightersCount()
	{
		int sum = 0;
		
		for (int i = 0; i < this.members.length; i++)
			if (this.members[i])
				sum += this.fightersCount[i];
		
		return sum;
	}
	
	int getFightersCount(int playerIndex)
	{
		if (this.isMember(playerIndex))
			return this.fightersCount[playerIndex];
		else
			return 0;
	}
	
	boolean isMember(int playerIndex)
	{
		if (playerIndex != Constants.NEUTRAL)
			return this.members[playerIndex];
		else
			return false;
	}
	
	int[] subtractFighters(int fightersCount, int playerIndexPreferred)
	{
		int sumStart = this.getFightersCount();
		
		if (fightersCount <= 0 || sumStart <= 0)
			return new int[this.members.length];

		int[] reductions = new int[this.members.length];
		
		int rest = this.subtractFightersInternal(playerIndexPreferred, reductions, fightersCount, fightersCount, true);
		
		if (rest > 0)
		{
			int[] seq = Utils.getRandomList(this.members.length);
			
			for (int round = 0; round < 2; round++)
			{
				for (int i = 0; i < this.members.length; i++)
				{
					int playerIndex = seq[i];
					
					if (!this.isMember(playerIndex) || playerIndex == playerIndexPreferred)
						continue;

					rest = this.subtractFightersInternal(playerIndex, reductions, fightersCount, rest, (i==1));
					
					if (rest <= 0)
						break;
				}
				
				if (rest <= 0)
					break;
			}
		}
		
		for (int playerIndex = 0; playerIndex < this.members.length; playerIndex++)
			this.subtractFightersCount(playerIndex, reductions[playerIndex]);
		
		return reductions;
	}
	
	private int subtractFightersInternal(
			int playerIndex,
			int[] reductions, 
			int fightersCount, 
			int rest, 
			boolean radikal)
	{
		if (!this.isMember(playerIndex))
			return rest;
		
		double fightersCountStart = (double)this.getFightersCount(playerIndex);

		if (fightersCountStart > 0)
		{
			int reduction = Utils.round((fightersCountStart / (double)this.getFightersCount()) * (double)fightersCount);
			
			if (radikal && reduction == 0)
				reduction = 1;
			
			if (reduction > rest)
				reduction = rest;

			reductions[playerIndex] += reduction;
			
			rest -= reduction;
		}
		
		return rest;
	}	
	
	void replacePlayer(int playerIndexBefore, int playerIndexAfter)
	{
		if (!this.isMember(playerIndexBefore))
			return;
		
		if (playerIndexAfter != Constants.NEUTRAL)
			this.addFightersCount(playerIndexAfter, this.getFightersCount(playerIndexBefore));
		
		this.removePlayer(playerIndexBefore, true);
	}
	
	public int getMembersCount()
	{
		int membersCount = 0;
		
		for (int playerIndex = 0; playerIndex < this.members.length; playerIndex++)
			if (this.members[playerIndex])
				membersCount++;
		
		return membersCount;
	}
	
	public boolean[] getMembers()
	{
		return this.members;
	}
	
	int[] correct()
	{
		int[] reductions = new int[this.getPlayersCount()];
		
		for (int playerIndex = 0; playerIndex < this.getPlayersCount(); playerIndex++)
		{
			if (!this.members[playerIndex])
			{
				reductions[playerIndex] = this.fightersCount[playerIndex];
				this.fightersCount[playerIndex] = 0;
			}
		}
		
		return reductions;
	}
}
