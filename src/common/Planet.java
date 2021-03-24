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
import java.util.HashSet;
import java.util.Hashtable;

@SuppressWarnings("serial") class Planet implements Serializable
{
	private Point position;
	private Alliance alliance;
	private Hashtable<ShipType,Integer> ships;
	private int owner;
	private DefenceShield defenceShield;
	private int moneySupply;
	private int moneyProduction;
	private int fighterProduction;
	private CommandRoom commandRoom;
	private HashSet<Integer> radioStationsByPlayer;
	
	Planet(Point position, Alliance alliance,
			Hashtable<ShipType, Integer> ships, int owner, DefenceShield defenceShield,
			int moneySupply, int moneyProduction, int fighterProduction, CommandRoom commandRoom)
	{
		super();
		this.position = position;
		this.alliance = alliance;
		this.ships = ships;
		this.owner = owner;
		this.defenceShield = defenceShield;
		this.moneySupply = moneySupply;
		this.moneyProduction = moneyProduction;
		this.fighterProduction = fighterProduction;
		this.commandRoom = commandRoom;
	}
	
	int getShipsCount(ShipType type)
	{
		Integer count = this.ships.get(type);
		
		if (count != null)
			return count.intValue();
		else
			return 0;
	}
	
	int getFightersCount(int playerIndex)
	{
		if (!this.allianceExists())
		{
			if (playerIndex == this.owner)
				return this.getShipsCount(ShipType.FIGHTERS);
			else
				return 0;
		}
		else
			return this.alliance.getFightersCount(playerIndex);
	}
	
	int getShipsCount(ShipType type, int playerIndex)
	{
		if (type == ShipType.FIGHTERS && this.allianceExists())
			return this.alliance.getFightersCount(playerIndex);
		else if (this.owner == playerIndex)
			return this.getShipsCount(type);
		else
			return 0;
	}
	
	private void incrementFightersCount(int playerIndex, int count)
	{
		if (this.allianceExists())
		{
			this.alliance.addFightersCount(playerIndex, count);
			this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
		}
		else
			this.incrementShipsCount(ShipType.FIGHTERS, count);
	}
	
	public Point getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Point position)
	{
		this.position = position;
	}
	
	public int getOwner()
	{
		return this.owner;
	}
	
	public int getMoneySupply()
	{
		return this.moneySupply;
	}
	
	public boolean isNeutral()
	{
		return (this.owner == Constants.NEUTRAL);
	}
	
	public int getMoneyProduction()
	{
		return this.moneyProduction;
	}
	
	public void setMoneyProduction(int moneyProduction)
	{
		this.moneyProduction = moneyProduction;
	}
	
	public int getFighterProduction()
	{
		return this.fighterProduction;
	}
	
	public void setFighterProduction(int fighterProduction)
	{
		this.fighterProduction = fighterProduction;
	}
	
	public int getDefenceShieldFightersCount()
	{
		if (this.defenceShield == null)
			return 0;
		else
			return this.defenceShield.getFightersCount();
	}
	
	public int getDefenceShieldFactor()
	{
		if (this.defenceShield == null)
			return 0;
		else
			return this.defenceShield.getFactor();
	}
	
	boolean allianceExists()
	{
		if (this.alliance == null)
			return false;
		
		return (this.alliance.getMembersCount() > 1);
	}
	
	boolean hasCommandRoom()
	{
		return (this.commandRoom != null);
	}
	
	public CommandRoom getCommandRoom()
	{
		return this.commandRoom;
	}
	
	boolean isAllianceMember(int playerIndex)
	{
		if (!this.allianceExists())
			return false;
		else
			return this.alliance.isMember(playerIndex);
	}
	
	int[] subtractFightersCount(int playersCount, int count, int playerIndexPreferred, boolean isAlliance)
	{
		int[] reductions = new int[playersCount];
		
		if (!this.allianceExists())
		{
			if (this.ships.containsKey(ShipType.FIGHTERS))
				this.ships.put(ShipType.FIGHTERS, this.ships.get(ShipType.FIGHTERS) - count);
			
			if (this.owner != Constants.NEUTRAL)
				reductions[this.owner] = count;
		}
		else
		{
			if (isAlliance)
				reductions = this.alliance.subtractFighters(count, playerIndexPreferred);
			else
			{
				this.alliance.subtractFightersCount(playerIndexPreferred, count);
				
				reductions[playerIndexPreferred] = count;
			}
			
			this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
		}
		
		return reductions;
	}
	
	public Alliance getAllianceStructure()
	{
		if (!this.allianceExists())
			return null;
		else
		{
			Alliance allianceCopy = new Alliance(this.alliance.getPlayersCount());
			
			for (int playerIndex = 0; playerIndex < this.alliance.getPlayersCount(); playerIndex++)
				if (this.alliance.isMember(playerIndex))
					allianceCopy.addPlayer(playerIndex);
			
			return allianceCopy;
		}
	}
	
	void buyMoneyProduction(int price)
	{
		if (this.moneySupply >= price && this.moneyProduction < Constants.MONEY_PRODUCTION_MAX)
		{
			this.moneyProduction += Constants.MONEY_PRODUCTION_PURCHASE;
			if (this.moneyProduction > Constants.MONEY_PRODUCTION_MAX)
				this.moneyProduction = Constants.MONEY_PRODUCTION_MAX;
			
			this.moneySupply -= price;
		}
	}
	
	void buyDefenceShield(int price)
	{
		if (this.moneySupply >= price && this.getDefenceShieldFactor() < Constants.DEFENSE_SHIELDS_COUNT_MAX)
		{
			if (this.defenceShield == null)
				this.defenceShield = new DefenceShield();
			else
				this.defenceShield.add();
			
			this.moneySupply -= price;
		}
	}
	
	void sellDefenceShield(int price)
	{
		if (this.defenceShield != null)
		{
			if (this.defenceShield.getFactor() == 1)
				this.defenceShield = null;
			else
			{
				this.defenceShield.subtract();
				
				if (this.defenceShield.getFightersCount() == 0)
					this.defenceShield = null;
			}
			
			this.moneySupply += price;
		}
	}
	
	void repairDefenceShield(int price)
	{
		if (this.moneySupply >= price && this.defenceShield != null && !this.defenceShield.isComplete())
		{
			this.defenceShield.repair();
			this.moneySupply -= price;
		}
	}
	
	boolean isDefenceShieldComplete()
	{
		if (this.defenceShield == null)
			return true;
		else
			return this.defenceShield.isComplete();
	}
	
	double getDefenceShieldStateFactor()
	{
		if (this.defenceShield == null)
			return 0;
		else
			return this.defenceShield.getStateFactor();
	}
	
	void setDefenceShieldFightersCount(int fightersCount)
	{
		if (this.defenceShield != null)
			this.defenceShield.setFightersCount(fightersCount);
	}
	
	void incrementFighterProduction()
	{
		if (this.fighterProduction < this.moneyProduction)
			this.fighterProduction++;
	}
	
	void decrementFighterProduction()
	{
		if (this.fighterProduction > 0)
			this.fighterProduction--;
	}
	
	void subtractMoneySupply(int count)
	{
		this.moneySupply -= count;
		
		if (this.moneySupply < 0)
			this.moneySupply = 0;
	}
	
	void addToMoneySupply(int count)
	{
		this.moneySupply += count;
	}
	
	CommandRoom removeCommandRoom()
	{
		CommandRoom commandRoom = (CommandRoom)Utils.klon(this.commandRoom);
		this.commandRoom = null;
		return commandRoom;
	}
	
	void setCommandRoom(CommandRoom commandRoom)
	{
		this.commandRoom = (CommandRoom)Utils.klon(commandRoom);
	}
	
	void incrementShipsCount(ShipType type, int count)
	{
		if (this.ships.containsKey(type))
			this.ships.put(type, this.ships.get(type)+ count);
		else
			this.ships.put(type, count);
	}
	
	void decrementShipsCount(ShipType type, int ount)
	{
		if (this.ships.containsKey(type))
		{
			if (this.ships.get(type) - ount > 0)
				this.ships.put(type, this.ships.get(type) - ount);			
			else
				this.ships.remove(type);
		}
	}
	
	void buyShip(ShipType type, int count, int price)
	{
		if (this.moneySupply >= price)
		{
			this.incrementShipsCount(type,count);
			this.moneySupply -= price;
		}
	}
	
	void sellShip(ShipType type, int price)
	{
		int count = this.getShipsCount(type);
		
		if (count > 0)
		{
			if (count > 1)
				this.ships.put(type, this.ships.get(type)- 1);
			else
				this.ships.remove(type);
			
			this.moneySupply += price;
		}
	}
	
	void produceMoneySupply()
	{
		this.moneySupply += (this.moneyProduction - this.fighterProduction);
	}
	
	void produceFighters()
	{
		if (this.fighterProduction <= 0)
			return;
		
		if (this.allianceExists())
		{
			this.alliance.addFightersCount(this.owner, this.fighterProduction);
			this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
		}
		else
		{
			if (this.ships.containsKey(ShipType.FIGHTERS))
				this.ships.put(ShipType.FIGHTERS, this.ships.get(ShipType.FIGHTERS)+ this.fighterProduction);
			else
				this.ships.put(ShipType.FIGHTERS, fighterProduction);
		}
	}
	
	void mergeFighters(int playerIndex, Ship ship)
	{
		if (ship.isAlliance())
		{
			if (!this.allianceExists())
			{
				this.alliance = new Alliance(playerIndex);
				this.alliance.addFightersCount(this.owner, this.getShipsCount(ShipType.FIGHTERS));
			}
			
			for (int playerIndex2 = 0; playerIndex2 < playerIndex; playerIndex2++)
				if (ship.isAllianceMember(playerIndex2))
					this.alliance.addFightersCount(playerIndex2, ship.getFightersCount(playerIndex2));
			
			this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
		}
		else
			this.incrementFightersCount(ship.getOwner(), ship.getFightersCount(ship.getOwner()));
			
	}
	
	void deleteDefenceShield()
	{
		this.defenceShield = null;
	}
	
	CommandRoom conquer(int playersCount, int newOwner, Ship ship)
	{
		CommandRoom commandRoom = null;
		if (this.hasCommandRoom())
			commandRoom = (CommandRoom)Utils.klon(this.commandRoom);
		
		this.ships.remove(ShipType.FIGHTERS);
		this.alliance = null;
		this.defenceShield = null;
		
		this.commandRoom = null;

		this.owner = newOwner;
		
		if (ship != null)
			this.mergeFighters(playersCount, ship);
		
		return commandRoom;
	}
	
	void changeOwner(int playerIndexBefore, int playerIndexAfter)
	{
		if (this.owner == playerIndexBefore)
		{
			this.commandRoom = null;
			
			if (playerIndexAfter == Constants.NEUTRAL)
			{
				if (this.allianceExists())
				{
					this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount(this.owner));
					this.alliance = null;
				}
			}
			
			this.owner = playerIndexAfter;

		}
		
		if (this.radioStationsByPlayer != null && this.radioStationsByPlayer.contains(playerIndexBefore))
		{
			if (playerIndexAfter == Constants.NEUTRAL)
				this.radioStationsByPlayer.remove(playerIndexBefore);
			else
			{
				this.radioStationsByPlayer.add(playerIndexAfter);
			}
		}

		if (this.isAllianceMember(playerIndexBefore))
		{
			this.alliance.replacePlayer(playerIndexBefore, playerIndexAfter);
			this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
			
			if (this.alliance.getMembersCount() <= 1)
				this.alliance = null;
		}
		
		this.fighterProduction = this.moneyProduction;
	}
	
	void dissolveAlliance()
	{
		if (!this.allianceExists())
			return;
		
		this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount(this.owner));
		this.alliance = null;
	}
	
	void addPlayerToAlliance(int playersCount, int playerIndex)
	{
		if (!this.allianceExists())
		{
			if (this.alliance == null)
				this.alliance = new Alliance(playersCount);
			
			this.alliance.addFightersCount(this.owner, this.getShipsCount(ShipType.FIGHTERS));
		}
		
		this.alliance.addPlayer(playerIndex);
		this.ships.put(ShipType.FIGHTERS, this.alliance.getFightersCount());
	}
	
	boolean[] getAllianceMembers()
	{
		if (this.alliance != null)
			return this.alliance.getMembers();
		else
			return null;
	}

	@SuppressWarnings("unchecked") 
	void acceptPlanetDataChange(Planet planet)
	{
		Integer fightersCount = this.ships.get(ShipType.FIGHTERS);
		
		this.ships = (Hashtable<ShipType, Integer>)Utils.klon(planet.ships);
		
		if (fightersCount == null)
			this.ships.remove(ShipType.FIGHTERS);
		else
			this.ships.replace(ShipType.FIGHTERS, fightersCount.intValue());
		
		this.moneySupply = planet.moneySupply;
		this.moneyProduction = planet.moneyProduction;
		this.fighterProduction = planet.fighterProduction;
		this.defenceShield = planet.defenceShield;
	}
		
	Alliance copyAllianceStructure(int[] reductions)
	{
		Alliance alliance = this.getAllianceStructure();
		if (alliance == null)
			return null;
		
		for (int playerIndex = 0; playerIndex < alliance.getPlayersCount(); playerIndex++)
			if (this.isAllianceMember(playerIndex))
				alliance.addFightersCount(playerIndex, reductions[playerIndex]);
		
		return alliance;
	}
	
	void setRadioStation(int playerIndex)
	{
		if (this.radioStationsByPlayer == null)
			this.radioStationsByPlayer = new HashSet<Integer>();
		
		this.radioStationsByPlayer.add(playerIndex);
	}
	
	boolean hasRadioStation(int playerIndex)
	{
		if (this.radioStationsByPlayer == null)
			return false;
		
		return this.radioStationsByPlayer.contains(playerIndex);
	}
	
	Planet createCopyForPlayer(int playerIndex)
	{
		Planet plClone = (Planet)Utils.klon(this);
		
		if (this.owner == playerIndex ||
			this.hasRadioStation(playerIndex))
		{
			return plClone;
		}

		if (!this.isAllianceMember(playerIndex))
		{
			plClone.alliance = null;
		}

		plClone.ships = new Hashtable<ShipType,Integer>();
		plClone.ships.put(ShipType.FIGHTERS, this.getShipsCount(ShipType.FIGHTERS));
		
		plClone.moneyProduction = 0;
		plClone.fighterProduction = 0;
		plClone.moneySupply = 0;
		plClone.defenceShield = null;
		plClone.commandRoom = null;
		plClone.radioStationsByPlayer = null;
		
		return plClone;
	}
}
