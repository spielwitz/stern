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
class DefenceShield implements Serializable
{
	private int factor;
	private int fightersCount;
	
	DefenceShield()
	{
		this.factor = 0;
		this.fightersCount = 0;
		this.add();
	}
	
	void add()
	{
		if (this.factor < Constants.DEFENSE_SHIELDS_COUNT_MAX)
		{
			this.factor++;
			this.fightersCount += Constants.DEFENSE_SHIELD_FIGHTERS;
		}
	}
	
	void subtract()
	{
		if (this.factor > 0)
		{
			this.fightersCount -= 
					Utils.round(Constants.DEFENSE_SHIELD_FIGHTERS * this.getStateFactor());
			this.factor--;
			
			if (this.fightersCount <= 0)
			{
				this.factor = 0;
				this.fightersCount = 0;
			}
		}
	}
	
	void repair()
	{
		int maxRepairRaumer = Math.min(
								this.factor * Constants.DEFENSE_SHIELD_FIGHTERS - this.fightersCount,
								Constants.DEFENSE_SHIELD_REPAIR_FIGHTERS_COUNT);
		
		if (maxRepairRaumer > 0)
			this.fightersCount += maxRepairRaumer;
				
	}
	
	boolean isComplete()
	{
		return this.fightersCount == this.factor * Constants.DEFENSE_SHIELD_FIGHTERS;
	}
	
	int getFightersCount()
	{
		return this.fightersCount;
	}
	
	void setFightersCount(int fightersCount)
	{
		this.fightersCount = fightersCount;
		
		if (this.fightersCount <= 0)
		{
			this.fightersCount = 0;
			this.factor = 0;
		}
	}
	
	int getFactor()
	{
		return this.factor;
	}
	
	double getStateFactor()
	{
		if (this.factor > 0)
			return (double)this.fightersCount / (double)(this.factor * Constants.DEFENSE_SHIELD_FIGHTERS); 
		else
			return 0;
	}
	
	static DefenceShield migrate(int factor, int intactPercent)
	{
		DefenceShield defenceShield = new DefenceShield();
		defenceShield.factor = factor;
		
		if (intactPercent >= 0)
		{
			defenceShield.fightersCount = Utils.round(
					(double)Constants.DEFENSE_SHIELD_FIGHTERS * 
					(double)defenceShield.factor *
					(double)intactPercent / (double)100);
			
		}
		
		return defenceShield;
	}
}
