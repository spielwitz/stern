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
import java.util.UUID;

@SuppressWarnings("serial") 
class Ship implements Serializable
{
	private int planetIndexStart;
	private int planetIndexDestination;
	private Point positionStart;
	private Point positionDestination;
	
	private int yearCount;
	private ShipType type;
	private int count;
	private int owner;
	private boolean transfer;
	private boolean startedRecently;
	private UUID stopLabel;
	
	private Alliance alliance;
	
	transient private boolean toBeDeleted;
	transient private boolean toBeTurned;
	
	Ship(
			int planetIndexStart, 
			int planetIndexDestination,
			Point pointStart,
			Point pointDestination,
			int yearCount,
			ShipType type,
			int count,
			int owner,
			boolean transfer,
			Alliance alliance)
	{
		this.planetIndexStart = planetIndexStart;
		this.planetIndexDestination = planetIndexDestination;
		this.positionStart = pointStart;
		this.positionDestination = pointDestination;
		this.yearCount = yearCount;
		this.type = type;
		this.count = count;
		this.owner = owner;
		this.transfer = transfer;
		this.toBeDeleted = false;
		this.alliance = alliance;
	}
	
	Ship(
			int planetIndexStart, 
			int planetIndexDestination,
			Point pointStart,
			Point pointDestination,
			ShipType type,
			int count, 
			int owner, 
			boolean transfer, 
			boolean recentlyStarted, 
			Alliance alliance)
	{
		this.planetIndexStart = planetIndexStart;
		this.planetIndexDestination = planetIndexDestination;
		this.positionStart = pointStart;
		this.positionDestination = pointDestination;
		this.yearCount = 0;
		this.type = type;
		this.count = count;
		this.owner = owner;
		this.transfer = transfer;
		this.startedRecently = recentlyStarted;
		this.toBeDeleted = false;
		this.alliance = alliance;
	}

	public int getPlanetIndexStart()
	{
		return planetIndexStart;
	}

	public void setPlanetIndexStart(int planetIndexStart)
	{
		this.planetIndexStart = planetIndexStart;
	}
	
	public boolean isStartedRecently()
	{
		return this.startedRecently;
	}
	
	void resetStartedRecently()
	{
		this.startedRecently = false;
	}
	
	public int getPlanetIndexDestination() {
		return planetIndexDestination;
	}

	public void setPlanetIndexDestination(int planetIndexDestination) 
	{
		this.planetIndexDestination = planetIndexDestination;
	}

	public Point getPositionStart() {
		return positionStart;
	}

	public void setPositionStart(Point positionStart)
	{
		this.positionStart = positionStart;
	}

	public Point getPositionDestination() {
		return positionDestination;
	}

	public void setPositionDestination(Point positionDestination) 
	{
		this.positionDestination = positionDestination;
	}

	public int getYearCount()
	{
		return yearCount;
	}
	
	public void incrementYearCount()
	{
		this.yearCount += getSpeed(this.type, this.transfer);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public boolean isToBeTurned()
	{
		return this.toBeTurned;
	}
	
	public void setToBeTurned()
	{
		this.toBeTurned = true;
	}

	int getFightersCount(int playerIndex)
	{
		if (this.type != ShipType.FIGHTERS)
			return 0;
		
		if (this.alliance == null)
		{
			if (playerIndex == this.owner)
				return this.count;
			else
				return 0;
		}
		else
			return this.alliance.getFightersCount(playerIndex);
	}
	
	public ShipType getType() {
		return type;
	}

	public int getOwner() {
		return owner;
	}

	public boolean isTransfer() {
		return transfer;
	}
	
	public boolean isStopped()
	{
		return this.stopLabel != null;
	}
	
	public UUID getStopLabel()
	{
		return this.stopLabel;
	}
	
	public void setStopLabel(UUID stopLabel)
	{
		this.stopLabel = stopLabel; 
	}
	
	public void setStopped(boolean stop)
	{
		if (stop)
			this.stopLabel = UUID.randomUUID();
		else
			this.stopLabel = null;
	}
	
	public Point getPositionOnDay(int day)
	{
		double distTotal = this.positionStart.distance(this.positionDestination);
		
		if (distTotal < Constants.PRECISION)
		{
			return this.positionStart;
		}
		
		if (day <= 0)
		{
			double x0 = this.positionStart.getX() + (double)this.yearCount * (this.positionDestination.getX()-this.positionStart.getX()) / distTotal;
			double y0 = this.positionStart.getY() + (double)this.yearCount * (this.positionDestination.getY()-this.positionStart.getY()) / distTotal;
			
			return new Point(x0, y0);
		}
		
		double v = getSpeed(this.getType(), this.transfer);
		
		double yearFraction = Ship.getYearFraction(day);
		
		double yearCountDay = (double)this.yearCount +  yearFraction * v;
		
		if (yearCountDay > distTotal - Constants.PRECISION)
		{
			return this.positionDestination;
		}
		
		double x = this.positionStart.getX() + yearCountDay * (this.positionDestination.getX()-this.positionStart.getX()) / distTotal;
		double y = this.positionStart.getY() + yearCountDay * (this.positionDestination.getY()-this.positionStart.getY()) / distTotal;
		
		return new Point(x, y);
	}
	
	public Point getSectorOnDay(int day)
	{
		Point pos = this.getPositionOnDay(day);
		
		double fractionX = Math.abs(pos.x - (double)((int)pos.x) - 0.5);
		double fractionY = Math.abs(pos.y - (double)((int)pos.y) - 0.5);
		
		if (fractionX < Constants.PRECISION || fractionY < Constants.PRECISION)
		{
			// When the ship is exactly in between two sectors, return null.
			return null;
		}
		else
		{
			return new Point(
					Utils.round(pos.x),
					Utils.round(pos.y));
		}
	}
	
	static ShipTravelTime getTravelTime(
			ShipType type, 
			boolean transfer, 
			Point positionStart,
			Point positionDestination)
	{
		double dist = positionStart.distance(positionDestination);
		double v = (double)getSpeed(type, transfer);
		
		return getTravelTimeInternal(dist, v);
	}
	
	ShipTravelTime getTravelTimeRemaining()
	{
		Point posNow = this.getPositionOnDay(0);
		
		double dist = posNow.distance(this.positionDestination);
		double v = (double)getSpeed(this.type, this.transfer);
		
		return getTravelTimeInternal(dist, v);
	}
	
	private static ShipTravelTime getTravelTimeInternal(double dist, double v)
	{
		double yearFraction = dist/v;
		
		int daysCount = Utils.round(yearFraction * (double)Constants.DAYS_OF_YEAR_COUNT);
		
		if (daysCount % Constants.DAYS_OF_YEAR_COUNT == 0)
		{
			return new ShipTravelTime(daysCount / Constants.DAYS_OF_YEAR_COUNT - 1, Constants.DAYS_OF_YEAR_COUNT);
		}
		else
		{
			return new ShipTravelTime(daysCount / Constants.DAYS_OF_YEAR_COUNT, daysCount % Constants.DAYS_OF_YEAR_COUNT);
		}
	}
	
	private static int getSpeed(ShipType type, boolean transfer)
	{
		if (type == ShipType.SCOUT)
			return Constants.SPEED_FAST;
		else if (type == ShipType.MINESWEEPER && !transfer)
			return Constants.SPEED_SLOW;
		else if (type == ShipType.PATROL && !transfer)
			return Constants.SPEED_SLOW;
		else
			return Constants.SPEED_NORMAL;
	}
		
	boolean isAlliance()
	{
		return (this.alliance != null);
	}
	
	public boolean[] getAllianceMembers()
	{
		if (this.alliance != null)
			return this.alliance.getMembers();
		else
			return null;
	}
	
	boolean isAllianceMember(int playerIndex)
	{
		if (this.alliance == null)
			return false;
		else
			return this.alliance.isMember(playerIndex);
	}
	
	boolean isPlayerInvolved(int playerIndex)
	{
		if (this.owner == playerIndex)
			return true;
		else
			return this.isAllianceMember(playerIndex);
	}

	void setToBeDeleted() {
		this.toBeDeleted = true;
	}
	
	boolean isToBeDeleted()
	{
		return this.toBeDeleted;
	}
	
	void turn()
	{
		int planetIndexTemp = this.planetIndexStart;
		Point positionTemp = this.positionStart.klon();
		
		this.planetIndexStart = this.planetIndexDestination;
		this.planetIndexDestination = planetIndexTemp;
		this.positionStart = this.positionDestination;
		this.positionDestination = positionTemp;
		this.yearCount = 0;
		
		this.transfer = (this.type == ShipType.MINESWEEPER);
		
		this.toBeDeleted = false;
		this.toBeTurned = false;
	}

	void subtractFighters(int count, int playerIndexPreferred)
	{
		if (this.type != ShipType.FIGHTERS)
			return;
		
		if (this.alliance == null)
			this.count -= count;
		else
		{
			this.alliance.subtractFighters(count, playerIndexPreferred);
			this.count = this.alliance.getFightersCount();
		}
	}
	
	void capture(int ownerNew, Point positionCurrent)
	{
		this.owner = ownerNew;
		this.yearCount = 0;
		this.transfer = true;
		this.positionDestination   = positionCurrent;
		this.positionStart	 = positionCurrent;
		this.planetIndexStart = Constants.NO_PLANET;
		this.planetIndexDestination = Constants.NO_PLANET;
		
		this.setStopped(true);
		
		if (this.alliance != null)
		{
			int count = this.getCount();
			
			this.alliance = null;
			this.count = count;
		}
	}
	
	void changeOwner (int ownerBefore, int ownerAfter)
	{
		if (this.toBeDeleted)
			return;

		if (this.owner == ownerBefore && this.type == ShipType.CAPITULATION)
		{
			this.toBeDeleted = true;
			return;
		}

		if (this.owner == ownerBefore && ownerAfter == Constants.NEUTRAL)
		{
			this.toBeDeleted = true;
			
			return;
		}
		
		if (this.isAlliance() && this.isPlayerInvolved(ownerBefore))
		{
			this.alliance.replacePlayer(this.owner, ownerAfter);
			this.count = this.alliance.getFightersCount();
			
			if (this.alliance.getMembersCount() <= 1)
				this.alliance = null;
			
			if (this.count <= 0)
			{
				this.toBeDeleted = true;
				return;
			}
		}
				
		if (this.owner == ownerBefore)
		{
			this.owner = ownerAfter;
		}
		
		return;
	}
	
	public void setAlliance(Alliance alliance)
	{
		this.alliance = alliance;
	}
	
	private static double getYearFraction(int day)
	{
		return (double)day / (double)Constants.DAYS_OF_YEAR_COUNT;
	}
	
	byte getScreenDisplaySymbol()
	{
		switch (this.type)
		{
		case FIGHTERS:
			return 1;
		case SCOUT:
			return 2;
		case PATROL:
			return 3;
		case TRANSPORT:
			return 4;
		case MINE50:
		case MINE100:
		case MINE250:
		case MINE500:
			return 5;
		case MINESWEEPER:
			return 6;
		default:
			return 0;
		}
	}
}
