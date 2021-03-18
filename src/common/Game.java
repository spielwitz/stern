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

import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

@SuppressWarnings("serial")
public class Game extends EmailTransportBase implements Serializable
{
	private String buildSaved;
	private String buildRequired;

	private String name;
	private long dateStart;
	private long dateUpdate;
	private HashSet<GameOptions> options; 
	private int yearMax;
	private int year;
	private int playersCount;
	private int planetsCount;
	
	private int boardXOffset;
	private int boardYOffset;
	private int boardWidth;
	private int boardHeight;
			  
	private Planet[] planets;
	private Player[] players;
	private ArrayList<Ship> ships;
	private Hashtable<Integer,ArrayList<Move>> moves;
	private UUID[] playerReferenceCodes;
	private Hashtable<String,Mine> mines;
	private String emailAddressGameHost;
	
	private boolean finalized;
	
	private Hashtable<Integer,Archive> archive;
	private ArrayList<ScreenContent> replayLast;
	
	transient private boolean soloPlayer;
	transient private boolean initial;
	transient private boolean enableParameterChange;
	
	transient private Console console;
	transient private ScreenContent screenContent;
	transient private ScreenContent screenContentStartOfYear;
	transient private GameThread gameThread;
	
	transient private Hashtable<Integer,String> mapPlanetIndexToName;
	transient private Hashtable<String,Integer> mapPlanetNameToIndex;
	transient private Hashtable<String,Integer> planetsByPosition;
	transient private int[] planetIndicesSorted;
	transient static private ArrayList<Point> sectorsAroundHomePlanet;
	transient static Hashtable<ShipType, Integer> editorPricesBuy;
	transient static Hashtable<ShipType, Integer> editorPricesSell;
	
	transient private boolean goToReplay;
	
	transient private BitSet shipsOfPlayerHidden; 
	transient private ScreenContent screenContentWhileMovesEntered;
	
	transient private ShipTravelTime[][] distanceMatrix;
	transient private int[][] distanceMatrixYears;
		
	static {
		editorPricesBuy = new Hashtable<ShipType, Integer>();
		
		editorPricesBuy.put(ShipType.SCOUT, Constants.PRICE_SCOUT);
		editorPricesBuy.put(ShipType.MONEY_PRODUCTION, Constants.PRICE_MONEY_PRODUCTION_INCREASE);
		editorPricesBuy.put(ShipType.FIGHTER_PRODUCTION, Constants.PRICE_FIGHTER_PRODUCTION);
		editorPricesBuy.put(ShipType.DEFENCE_SHIELD, Constants.PRICE_DEFENSE_SHIELD);
		editorPricesBuy.put(ShipType.DEFENCE_SHIELD_REPAIR, Constants.PRICE_DEFENSE_SHIELD_REPAIR);
		editorPricesBuy.put(ShipType.MINE50, Constants.PRICE_MINE50);
		editorPricesBuy.put(ShipType.MINE100, Constants.PRICE_MINE100);
		editorPricesBuy.put(ShipType.MINE250, Constants.PRICE_MINE250);
		editorPricesBuy.put(ShipType.MINE500, Constants.PRICE_MINE500);
		editorPricesBuy.put(ShipType.MINESWEEPER, Constants.PRICE_MINENSWEEPER);
		editorPricesBuy.put(ShipType.PATROL, Constants.PRICE_PATROL);
		editorPricesBuy.put(ShipType.TRANSPORT, Constants.PRICE_TRANSPORT);
		
		editorPricesSell = new Hashtable<ShipType, Integer>();
		
		for (ShipType typ: editorPricesBuy.keySet())
			editorPricesSell.put(typ, 
					Utils.round((double)editorPricesBuy.get(typ) * Constants.PRICE_RATIO_BUY_SELL));
		
	}
	protected Game() {}
	
	@SuppressWarnings("unchecked")
	public Game(HashSet<GameOptions> options,
			Player[] players,
			String emailAddressGameHost,
			int planetsCount,
			int yearMax)
	{
		this.options = (HashSet<GameOptions>)Utils.klon(options);
		this.players = (Player[])Utils.klon(players);
		this.emailAddressGameHost = emailAddressGameHost;

		this.planetsCount = planetsCount;
		this.playersCount = players.length;
		this.yearMax = yearMax;
		
		this.build = ReleaseGetter.getRelease();
		
		this.initial = true;
	}
	
	public static Game create(HashSet<GameOptions> options,
			Player[] players,
			String emailAddressGameHost,
			int planetsCount,
			int yearMax)
	{
		Game game = new Game(options, players, emailAddressGameHost, planetsCount, yearMax);
		game.createBoard();
		
		return game;
	}
	
	void initAfterLoad(GameThread gameThread)
	{
		this.gameThread = gameThread;
		this.console = new Console(this, false);
		
		this.gameThread.checkMenueEnabled();
		this.goToReplay = this.soloPlayer;
		
		this.migrate();
		
		this.mainLoop();
	}
	
	void initNewGame(
			GameThread gameThread)
			
	{
		this.gameThread = gameThread;
		this.console = new Console(this, false);
		
		do
		{
			this.createBoard();
			
			this.gameThread.checkMenueEnabled();
			
			this.updateBoardNewGame();
			this.updatePlanets(true);
			
			this.console.clear();
			this.console.appendText(
					SternResources.SpielfeldOkFrage(true) + " ");
			String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (input.equals(Console.KEY_YES))
				break;
			
		} while (true);
		
		this.initial = false;
		this.gameThread.checkMenueEnabled();
		
		this.prepareYear();
		this.mainLoop();
	}
	
	private boolean isPlayerEmail(int playerIndex)
	{
		if (this.options.contains(GameOptions.EMAIL_BASED))
			return this.players[playerIndex].isEmailPlayer();
		else
			return false;
	}
	
	public boolean isInitial()
	{
		return this.initial;
	}
	
	void setInitial()
	{
		this.initial = true;
	}
	
	public void setSoloPlayer(boolean v)
	{
		this.soloPlayer = v;
	}
	
	public int getPlayersCount()
	{
		return this.playersCount;
	}
	
	public int getPlanetsCount()
	{
		return this.planetsCount;
	}
	
	public Player[] getPlayers()
	{
		return this.players;
	}
	
	public void setPlayers(Player[] players)
	{
		this.players = players;
	}
	
	public int getYear()
	{
		return this.year;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDateUpdate()
	{
		this.dateUpdate = System.currentTimeMillis();
	}
	
	public long getDateUpdate()
	{
		return this.dateUpdate;
	}
	
	public String getBuildRequired()
	{
		return this.buildRequired;
	}
	
	public boolean isSimple()
	{
		return this.options.contains(GameOptions.SIMPLE);
	}
	
	public ShipTravelTime[][] getDistanceMatrix()
	{
		if (this.distanceMatrix == null)
		{
			this.distanceMatrix = new ShipTravelTime[planetsCount][planetsCount];
			this.distanceMatrixYears = new int[planetsCount][planetsCount];
			
			for (int planetIndex = 0; planetIndex < planetsCount - 1; planetIndex++)
			{
				for (int planetIndex2 = planetIndex + 1; planetIndex2 < planetsCount; planetIndex2++)
				{
					this.distanceMatrix[planetIndex][planetIndex2] = Ship.getTravelTime(
							ShipType.FIGHTERS,
							false,
							this.planets[planetIndex].getPosition(),
							this.planets[planetIndex2].getPosition());
					
					this.distanceMatrix[planetIndex2][planetIndex] = this.distanceMatrix[planetIndex][planetIndex2];
					
					this.distanceMatrixYears[planetIndex][planetIndex2] = this.distanceMatrix[planetIndex][planetIndex2].year;
					this.distanceMatrixYears[planetIndex2][planetIndex] = this.distanceMatrixYears[planetIndex][planetIndex2];
				}
			}  			
		}
		
		return this.distanceMatrix;
	}
	
	public void setBuildRequired(String buildRequired)
	{
		this.buildRequired = buildRequired;
	}
	
	public HashSet<GameOptions> getOptions()
	{
		return this.options;
	}
	
	public void setOptions(HashSet<GameOptions> options)
	{
		this.options = options;
	}
	
	public int getYearMax()
	{
		return this.yearMax;
	}
	
	public void setYearMax(int yearMax)
	{
		this.yearMax = yearMax;
	}
	
	public boolean isFinalized()
	{
		return this.finalized;
	}
	
	public boolean isSoloPlayer()
	{
		return this.soloPlayer;
	}
	
	private boolean isMoveEnteringOpen()
	{
		return this.soloPlayer ||
			   this.gameThread.isMoveEnteringOpen();
	}
	
	public String getEmailAddressGameHost()
	{
		return this.emailAddressGameHost;
	}
	
	public ArrayList<PlanetInfo> getPlanetInfo()
	{
		ArrayList<PlanetInfo> retval = new ArrayList<PlanetInfo>(this.planetsCount);
		
		for (Planet pl: this.planets)
			retval.add(new PlanetInfo(
					(int)pl.getPosition().getX(), 
					(int)pl.getPosition().getY(), 
					pl.getOwner() == Constants.NEUTRAL ?
							Colors.NEUTRAL :
							this.players[pl.getOwner()].getColorIndex()));
		
		return retval;
	}
	
	public GameInfo getGameInfo()
	{
		GameInfo info = new GameInfo();
		
		info.finalized = this.finalized;
		info.year = this.year;
		info.yearMax = this.yearMax;
		info.name = this.name;
		info.players = this.players;
		info.dateStart = this.dateStart;
		info.dateUpdate = this.dateUpdate;
		info.simple = (this.options.contains(GameOptions.SIMPLE));
		info.planetInfo = this.getPlanetInfo();
		
		info.moveEnteringFinalized = new HashSet<String>();
		
		if (this.moves != null)
		{
			for (Integer playerIndex: this.moves.keySet())
				info.moveEnteringFinalized.add(this.players[playerIndex].getName());
		}
		
		return info;
	}
	
	public ScreenContent getScreenContentStartOfYear()
	{
		return this.screenContentStartOfYear;
	}
	
	@SuppressWarnings("unchecked")
	public void changeParameters(
			HashSet<GameOptions> options,
			int yearMax,
			String emailAddressGameHost,
			ArrayList<Player> players)
	{
		this.options = (HashSet<GameOptions>) Utils.klon(options);
		
		for (int sp = 0; sp < players.size(); sp++)
		{
			this.players[sp].setName(players.get(sp).getName());
			this.players[sp].setColorIndex(players.get(sp).getColorIndex());
			this.players[sp].setEmailPlayer(players.get(sp).isEmailPlayer());
			this.players[sp].setEmail(players.get(sp).getEmail());
		}
		
		this.emailAddressGameHost = emailAddressGameHost;
		this.yearMax = yearMax;
		
		this.updateBoard();
		this.updatePlanets(false);
	}
	
	private int[] getPlanetsSorted()
	{
		if (this.planetIndicesSorted == null)
			this.buildPlanetMap();
		
		return this.planetIndicesSorted;
	}
	
	private void save(boolean autoSave)
	{
		this.gameThread.save(this, autoSave);
	}
	
	public ScreenContent getScreenContentWhileMovesEntered()
	{
		return this.screenContentWhileMovesEntered;
	}
	
	private void setScreenContentWhileMovesEntered()
	{
		this.screenContentWhileMovesEntered = 
				(ScreenContent)Utils.klon(this.screenContent);
		
		ScreenContentConsole cons = this.screenContentWhileMovesEntered.getConsole();
		
		String[] textLines = cons.getTextLines();
		textLines[Console.TEXT_LINES_COUNT_MAX - 1] = 
				SternResources.ZugeingabeClientEingabeGesperrt(true);
		
		cons = new ScreenContentConsole(
						textLines, 
						cons.getLineColors(),
						new ArrayList<ConsoleKey>(), 
						cons.getHeaderText(),
						cons.getHeaderCol(), 
						0, 
						false,
						cons.getProgressBarDay());
		
		this.screenContentWhileMovesEntered.setConsole(cons);
	}
	
	public Game createCopyForPlayer(int playerIndex)
	{
		Game gameClone = (Game)Utils.klon(this);
		
		gameClone.setBuildRequired(Constants.BUILD_COMPATIBLE);
		
		if (gameClone.options.contains(GameOptions.SERVER_BASED))
		{
			gameClone.emailAddressGameHost = gameClone.players[0].getEmail();
		}
		
		if (gameClone.finalized)
			return gameClone;
		
		for (int playerIndex2 = 0; playerIndex2 < this.playersCount; playerIndex2++)
		{
			if (playerIndex2 != playerIndex)
			{
				gameClone.moves.put(playerIndex2, new ArrayList<Move>());
				gameClone.playerReferenceCodes[playerIndex2] = null;
			}
		}
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			Planet plClone = this.planets[planetIndex].createCopyForPlayer(
					playerIndex);
			
			gameClone.planets[planetIndex] = plClone;
		}
		
		return gameClone;
	}
	
	public static HashSet<GameOptions> getOptionsDefault()
	{
		HashSet<GameOptions> options = new HashSet<GameOptions>();
		
		options.add(GameOptions.LIMITED_NUMBER_OF_YEARS);
		options.add(GameOptions.COMMAND_ROOMS);
		options.add(GameOptions.DEFENCE_SHIELDS);
		options.add(GameOptions.AUTO_SAVE);
		
		return options;
	}
	
	public static ArrayList<Player> getPlayersDefault()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (int playerIndex = 0; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
			players.add(
					new Player(
							SternResources.Spieler(false)+(playerIndex+1), 
							"", 
							(byte)(playerIndex+Colors.COLOR_OFFSET_PLAYERS), 
							false, 
							false));
		
		return players;
	}
	
	private void createBoard()
	{
		this.name = "";
		this.buildRequired = Constants.BUILD_COMPATIBLE;
		this.finalized = false;
		this.initial = true;
		
		this.archive = new Hashtable<Integer,Archive>();
		this.replayLast = new ArrayList<ScreenContent>();
		this.year = 0;
		
		this.mines = new Hashtable<String,Mine>();
		
		this.ships = new ArrayList<Ship>();
		
		this.planets = new Planet[planetsCount];
		
		this.screenContent = null;
		
		this.dateStart = System.currentTimeMillis();
		
		this.boardXOffset = 0;
		this.boardYOffset = 0;
		this.boardWidth = Constants.BOARD_MAX_X;
		this.boardHeight = Constants.BOARD_MAX_Y;
		
		if (this.planetsCount < Constants.PLANETS_COUNT_MAX)
		{
			double sectorsPerPlanet = (double)(Constants.BOARD_MAX_X * Constants.BOARD_MAX_Y) / (double)Constants.PLANETS_COUNT_MAX;
			double boardRatio = (double)Constants.BOARD_MAX_X / (double)Constants.BOARD_MAX_Y;
			
			double sectorsCount = (double)this.planetsCount * sectorsPerPlanet;
			
			this.boardHeight = Utils.round(Math.sqrt(sectorsCount) / boardRatio);
			this.boardWidth = Utils.round((double)this.boardHeight * boardRatio);
			
			this.boardXOffset = (int)((double)(Constants.BOARD_MAX_X - this.boardWidth) / 2.);
			this.boardYOffset = (int)((double)(Constants.BOARD_MAX_Y - this.boardHeight) / 2.);
		}
		
		int planetsNearbyCount = Constants.GAME_PLANETS_NEARBY_COUNT;
		
		if (planetsNearbyCount * playersCount > planetsCount - playersCount)
		{
			planetsNearbyCount = (int)Math.floor((double)(planetsCount - playersCount) / (double)playersCount);
		}
		
		Point[] positions = new Point[planetsCount];
		
		int[] sequencePlayers = Utils.getRandomList(playersCount);
		
		for (int i = 0; i < playersCount; i++)
		{
			int planetIndexHome = sequencePlayers[i];
			
			Point planetPositionHome;
			
			boolean ok = false;
			
			while(!ok)
			{
				planetPositionHome = new Point(Utils.getRandomInteger(this.boardWidth), Utils.getRandomInteger(this.boardHeight));
				
				if (isTooClose(planetPositionHome, positions))
					continue;
				
				positions[planetIndexHome] = planetPositionHome;
				
				if (planetsNearbyCount == 0)
				{
					ok = true;
					continue;
				}
				
				ArrayList<Point> potentialNearbyPositions = getPotentialNearbyPositions(positions, planetPositionHome);
				
				if (potentialNearbyPositions.size() < planetsNearbyCount)
					continue;
				
				int sequencePotentialNearbyPositions[] = Utils.getRandomList(potentialNearbyPositions.size());
				
				int planetIndexStart = playersCount + planetIndexHome * planetsNearbyCount;
				int planetIndex = planetIndexStart;
				
				for (int j = 0; j < sequencePotentialNearbyPositions.length; j++)
				{
					Point planetPosition = potentialNearbyPositions.get(sequencePotentialNearbyPositions[j]);
					
					if (isTooClose(planetPosition, positions))
						continue;
					
					positions[planetIndex] = planetPosition;
					planetIndex++;
					
					if (planetIndex - planetIndexStart >= planetsNearbyCount)
						break;
				}
				
				if (planetIndex - planetIndexStart  < planetsNearbyCount)
				{
					for (int index = planetIndexStart; index < planetIndex; index++)
						positions[index] = null;
				}
				else
					ok = true;
			}
		}
		
		for (int planetIndex = playersCount + playersCount * planetsNearbyCount; planetIndex < planetsCount; planetIndex++)
		{
			boolean ok = false;
			Point planetPosition = null;
			
			while(!ok)
			{
				planetPosition = new Point(Utils.getRandomInteger(this.boardWidth), Utils.getRandomInteger(this.boardHeight));
				
				if (isTooClose(planetPosition, positions))
					continue;
				
				ok = true;
				
				if (planetsNearbyCount <= 1)
					continue;
				
				for (int planetIndexHome = 0; planetIndexHome < playersCount; planetIndexHome++)
				{
					double dist = planetPosition.distance(positions[planetIndexHome]);
					
					if (dist <= Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME)
					{
						ok = false;
						break;
					}
				}
			}
			
			positions[planetIndex] = planetPosition;
		}
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			boolean isPlanetHome = (planetIndex < this.playersCount);
											
			int owner 	  	  = isPlanetHome ? planetIndex : Constants.NEUTRAL;
			int fightersCount = isPlanetHome ? Constants.FIGHTERS_COUNT_INITIAL_PLAYERS : Utils.getRandomInteger(Constants.FIGHTERS_COUNT_INITIAL_NEUTRAL);
			
			int moneyProduction = Constants.MONEY_PRODUCTION_INITIAL_PLAYERS;
			if (!isPlanetHome)
			{
				moneyProduction = Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL) + 1;
				if (Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W2) < Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W1)
					moneyProduction += (Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA)+1);
			}
			
			DefenceShield defenceShield = (!this.options.contains(GameOptions.SIMPLE) && isPlanetHome) ? 
									new DefenceShield() :
									null;
									
			CommandRoom commandRoom = (isPlanetHome && (!this.options.contains(GameOptions.SIMPLE)))
					                  ? new CommandRoom(planetIndex)
									  : null;
			
	        Hashtable<ShipType, Integer> ships = new Hashtable<ShipType, Integer>();
	        ships.put(ShipType.FIGHTERS, fightersCount);
	        
	        int moneySupply = this.options.contains(GameOptions.SIMPLE) ? 
	        			  0 :	        		
			        		isPlanetHome ?
			        				Constants.MONEY_SUPPLY_INITIAL_PLAYERS :
			        				Utils.getRandomInteger(Constants.MONEY_SUPPLY_INITIAL_NEUTRAL_MAX + 1);
	        
			this.planets[planetIndex] = new Planet(
										new Point(
												positions[planetIndex].getX() + this.boardXOffset,
												positions[planetIndex].getY() + this.boardYOffset),
										null,
										ships,
										owner, 
										defenceShield,
										moneySupply,
										moneyProduction,
										moneyProduction,
										commandRoom);
		}
		
		if (planetsNearbyCount > 0)
		{
			for (int planetIndexHome = 0; planetIndexHome < this.playersCount; planetIndexHome++)
			{
				boolean ok = false;
				
				int moneyProductions[] = null;
				
				while (!ok)
				{
					int moneyProductionSum = 0;
					moneyProductions = new int[planetsNearbyCount];
					
					for (int i = 0; i < planetsNearbyCount; i++)
					{
						moneyProductions[i] = Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL) + 1;
						if (Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W2) < Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA_W1)
							moneyProductions[i] += (Utils.getRandomInteger(Constants.MONEY_PRODUCTION_INITIAL_NEUTRAL_EXTRA)+1);
						
						moneyProductionSum += moneyProductions[i];
					}
					
					double moneyProductionAverage = (double)moneyProductionSum / (double)planetsNearbyCount;
					
					if (moneyProductionAverage >= 5 && moneyProductionAverage <= 7)
						ok = true;
				}
				
				int planetIndex = this.playersCount + planetsNearbyCount * planetIndexHome;
				
				for (int i = 0; i < planetsNearbyCount; i++)
				{
					this.planets[planetIndex + i].setMoneyProduction(moneyProductions[i]);
					this.planets[planetIndex + i].setFighterProduction(moneyProductions[i]);
				}
			}
		}
		
		this.buildPlanetMap();
		this.calculateScores();	
	}
	
	private boolean isTooClose (Point position, Point[] positions)
	{
		boolean retval = false;
		
		for (int i = 0; i < positions.length; i++)
		{
			if (positions[i] == null)
				continue;
			
			if (positions[i].distance(position) < 2)
			{
				retval = true;
				break;
			}
		}
		
		return retval;
	}
	
	private ArrayList<Point> getPotentialNearbyPositions(Point[] positions, Point planetPositionHome)
	{
		if (sectorsAroundHomePlanet == null)
		{
			sectorsAroundHomePlanet = new ArrayList<Point>();
			
			for (int dx = -Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME;
					 dx < +Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME;
				     dx++)
			{
				for (int dy = -Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME;
						 dy < +Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME;
					     dy++)
				{
					if (Math.abs(dx) < 2 && Math.abs(dy) < 2)
						continue;
					
					double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); 
					if (dist > Constants.SPEED_NORMAL * Constants.GAME_PLANETS_NEARBY_TRAVEL_TIME)
						continue;
					
					sectorsAroundHomePlanet.add(new Point(dx, dy));
				}
			}
		}
		
		ArrayList<Point> positionsPotential = new ArrayList<Point>();
		
		for (int i = 0; i < sectorsAroundHomePlanet.size(); i++)
		{
			int x = (int)(planetPositionHome.getX() + sectorsAroundHomePlanet.get(i).getX());
			int y = (int)(planetPositionHome.getY() + sectorsAroundHomePlanet.get(i).getY());
			
			if (x < 0 || x >= this.boardWidth || y < 0 || y >= this.boardHeight)
				continue;
			
			Point position = new Point(x, y);
			
			if (isTooClose(position, positions))
				continue;
			
			positionsPotential.add(position);
		}
				
		return positionsPotential;
	}
	
	private void mainLoop()
	{
		this.updateSaveBuild();
		
		if (this.moves == null)
			this.moves = new Hashtable<Integer, ArrayList<Move>>();
		
		do
		{
			this.updateBoard();
			this.updatePlanets(false);
			
			this.console.clear();
			
			this.screenContentStartOfYear = (ScreenContent) Utils.klon(this.screenContent);
			this.screenContentStartOfYear.setConsole(null);
			
			this.checkIsGameFinalized(false);
			
			this.mainMenu();
			
			this.console.setBackground(true);
			new Evaluation(this);
			this.console.setBackground(false);
			
			this.autosave();
			
			this.goToReplay = true;
			new Replay(this);
			
		} while (true);
	}
	
	private void autosave()
	{
		if (this.options.contains(GameOptions.AUTO_SAVE))
			this.save(true);
	}
	
	private void mainMenu()
	{
		do
		{
			this.setEnableParameterChange(true);
			
			if (this.goToReplay)
			{
				new Replay(this);
			}
			else
			{			
				this.console.setHeaderText(
						this.mainMenuGetYearDisplayText() + " -> "+SternResources.Hauptmenue(true), Colors.NEUTRAL);
				
				ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
				if (!this.finalized)
					allowedKeys.add(new ConsoleKey("TAB",SternResources.Zugeingabe(true)));
				if (this.evaluationExists())
					allowedKeys.add(new ConsoleKey("1",SternResources.AuswertungWiederholen(true)));
				if (this.year > 0 && (!this.soloPlayer || this.finalized))
					allowedKeys.add(new ConsoleKey("2",SternResources.Statistik(true)));
				if (!this.soloPlayer || this.finalized)
					allowedKeys.add(new ConsoleKey("9",SternResources.Spielinformationen(true)));
				if (!this.soloPlayer && this.year > 0 && !this.finalized)
					allowedKeys.add(new ConsoleKey("0",SternResources.SpielAbschliessen(true)));
				
				ConsoleInput consoleInput = this.console.waitForKeyPressed(allowedKeys, false);
				
	 			this.setEnableParameterChange(false);
	
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					this.console.clear();
				else if (!this.finalized && input.equals("\t"))
				{
					this.console.clear();
					
					this.setScreenContentWhileMovesEntered();
					
					EnterMoves enterMoves = new EnterMoves(this);
					
					this.screenContentWhileMovesEntered = null;
					
					if (enterMoves.enterMovesFinished)
						break;
				}
				else if (!this.soloPlayer && this.year > 0 && !this.finalized && input.equals("0"))
					this.askFinalizeGame();
				else if (this.evaluationExists() && input.equals("1"))
					new Replay(this);
				else if ((!this.soloPlayer  || this.finalized) && input.equals("9"))
				{
					this.console.clear();
					new GameInformation(this);
				}
				else if (this.year > 0 && input.equals("2") && (!this.soloPlayer || this.finalized))
				{
					this.console.clear();
					new Statistics(this);
				}
				else
				{
					console.appendText(SternResources.UngueltigeEingabe(true));
					console.lineBreak();
				}
			}
		}
		while (true);
		
	}
	private void askFinalizeGame()
	{
		this.console.appendText(SternResources.SpielAbschliessenFrage(true) + " ");
		String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
		
		if (input.equals(Console.KEY_YES))
			this.finalizeGame(false);
		else
			this.console.outAbort();		
	}
	
	private void checkIsGameFinalized(boolean background)
	{
		if (!this.finalized)
		{
			if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS) && this.year >= this.yearMax)
				this.finalizeGame(background);
			else
			{
				int playersRemainingCount = 0;
				int botsRemainingCount = 0;
				
				for (Player player: this.players)
				{
					if (!player.isDead())
					{
						if (player.isBot())
							botsRemainingCount++;
						else
							playersRemainingCount++;
					}
				}
				
				if ((playersRemainingCount <= 1 && botsRemainingCount == 0) ||
					(playersRemainingCount == 0 && botsRemainingCount > 0))
					this.finalizeGame(background);
			}
		}
	}
	private void finalizeGame(boolean background)
	{
		this.finalized = true;
		
		this.performAwardCeremony();
		
		if (!background)
		{
			this.console.setLineColor(Colors.WHITE);
			this.console.appendText(SternResources.HighscoreFrage(true) + " ");
			
			String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			if (input.equals(Console.KEY_YES))
			{
				this.gameThread.addToHighscore(this.archive.get(this.year), this.players);
			}
				
			this.console.clear();
			new Statistics(this);
			
			if (this.options.contains(GameOptions.AUTO_SAVE))
			{
				this.options.remove(GameOptions.AUTO_SAVE);
				this.save(true);
			}
		}
		
		this.console.clear();
	}
	
	public void finalizeGameServer()
	{
		this.moves = new Hashtable<Integer,ArrayList<Move>>();
		this.yearMax = this.year;
		this.finalized = true;
	}
	
	private void performAwardCeremony()
	{
		this.console.clear();
		
		int seqArchive[] = Utils.sortValues(this.archive.get(this.year).getScore(), true);		
		
		int position = 1;
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			this.console.setLineColor(this.players[seqArchive[playerIndex]].getColorIndex());
			
			StringBuilder sb = new StringBuilder();
			
			if (playerIndex > 0 && this.archive.get(this.year).getScore()[seqArchive[playerIndex]] < this.archive.get(this.year).getScore()[seqArchive[playerIndex-1]])
				position++;
			
			sb.append(
					SternResources.AbschlussPlatz(true, Integer.toString(position)) + " ");

			sb.append(Utils.padStringLeft(this.players[seqArchive[playerIndex]].getName(), Constants.PLAYER_NAME_LENGTH_MAX));
			
			sb.append(" " + Utils.padString(this.archive.get(this.year).getPlanetsCount()[seqArchive[playerIndex]], 2) + " "+SternResources.Planeten(true)+" ");
			sb.append(Utils.padString(this.archive.get(this.year).getFighters()[seqArchive[playerIndex]], 5) + " "+SternResources.Raumer(true)+" ");
			sb.append(Utils.padString(this.archive.get(this.year).getMoneyProduction()[seqArchive[playerIndex]], 4) + " "+SternResources.AbschlussEprod(true)+" ");
			sb.append(Utils.padString(this.archive.get(this.year).getScore()[seqArchive[playerIndex]], 5) + " "+SternResources.Punkte(true)+".");
			
			this.console.appendText(sb.toString());
			
			if (playerIndex == (Console.TEXT_LINES_COUNT_MAX - 1))
				this.console.waitForKeyPressed();
			else if (playerIndex < playersCount-1)
				this.console.lineBreak();
		}
		
		this.console.waitForKeyPressed();
		this.console.clear();
	}
	
	public static Game importFromVega(String name, byte[] bytes)
	{
		Import imp = new Import(bytes);
		return imp.start(name);
	}
	
	private void calculateScores()
	{
		int fightersCount[] = new int[this.playersCount];
		int planetsCount[] = new int[this.playersCount];
		int moneyProductions[] = new int[this.playersCount];
		int scores[] = new int[this.playersCount];
		
		for (Planet planet: this.planets)
		{
			int owner = planet.getOwner();
			
			if (planet.getOwner() == Constants.NEUTRAL)
				continue;
			
			planetsCount[owner]++;
			
			moneyProductions[owner] += planet.getMoneyProduction();
			
			for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			{
				fightersCount[playerIndex]+=planet.getFightersCount(playerIndex);
			}
		}
		
		for (Ship ship: this.ships)
		{
			int owner = ship.getOwner();
			
			if (owner == Constants.NEUTRAL)
				continue;
			
			if (ship.getType() == ShipType.FIGHTERS)
			{
				for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
				{
					fightersCount[playerIndex]+=ship.getFightersCount(playerIndex);;
				}				
			}
		}
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			scores[playerIndex] = getScore(planetsCount[playerIndex], this.playersCount);
		}
		
		this.archive.put(this.year, new Archive(scores, fightersCount, planetsCount, moneyProductions));
	}
	
	private static int getScore(int planetsCount, int playersCount)
	{
		return (int)Math.round(1000 * ((double)playersCount / (double)Constants.PLAYERS_COUNT_MAX) *
			     ((double)planetsCount / (double)Constants.PLANETS_COUNT_MAX));
	}
	
	public void prepareYear()
	{
		this.moves = new Hashtable<Integer, ArrayList<Move>> ();
		
		this.playerReferenceCodes = new UUID[this.playersCount];
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			this.playerReferenceCodes[playerIndex] = UUID.randomUUID();
	}
		
	private void updateBoard()
	{
		this.updateBoard(null, null, 0);
	}
	
	private void updateBoard (int day)
	{
		this.updateBoard(null, null, day);
	}
	
	private void updateBoard (
			Hashtable<Integer,ArrayList<Byte>> frames, 
			int day)
	{
		this.updateBoard(frames, null, day);
	}
	
	private void updateBoard (
			ArrayList<Point> positionsMarked, 
			int day)
	{
		this.updateBoard(null, positionsMarked, day);
	}
	
	private void updateBoardNewGame()
	{
		ArrayList<ScreenContentBoardPlanet> plData = new ArrayList<ScreenContentBoardPlanet>(this.planetsCount);
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			if (this.planets[planetIndex].isNeutral())
				plData.add(new ScreenContentBoardPlanet(
						"?",
						this.planets[planetIndex].getPosition(),
						Colors.NEUTRAL,
						null));
			else
				plData.add(new ScreenContentBoardPlanet(
						"??",
						this.planets[planetIndex].getPosition(),
						Colors.WHITE,
						null));
		}
		
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setBoard(
				new ScreenContentBoard(plData,
				null,
				null,
				null,
				null,
				null));
		
		this.gameThread.updateDisplay(this.screenContent);
	}
	
	private void updateBoard (
			Hashtable<Integer, ArrayList<Byte>> frames, 
			ArrayList<Point> positionsMarked, 
			int day)
	{
		this.updateBoard(frames, positionsMarked, null, day);
	}
	
	private void updateBoard (
			Hashtable<Integer, 
			ArrayList<Byte>> frames, 
			ArrayList<Point> positionsMarked, 
			ScreenContentBoardRadar radar,
			int day)
	{
		ArrayList<ScreenContentBoardPlanet> plData = new ArrayList<ScreenContentBoardPlanet>(this.planetsCount);
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			ArrayList<Byte> frameCol = null;
			
			if (frames != null)
				frameCol = frames.get(planetIndex);
			
			plData.add(new ScreenContentBoardPlanet(
					this.getPlanetNameFromIndex(planetIndex),
					this.planets[planetIndex].getPosition(),
					this.planets[planetIndex].isNeutral() ?
							Colors.NEUTRAL :
							this.players[this.planets[planetIndex].getOwner()].getColorIndex(),
					frameCol));
		}
		
		ArrayList<ScreenContentBoardPosition> positions = new ArrayList<ScreenContentBoardPosition>();

		for (Ship ship: this.ships)
		{
			if (ship.isToBeDeleted() || ship.isStartedRecently()
				|| ship.getType() == ShipType.CAPITULATION)
				continue;
			
			int owner = ship.getOwner();
			
			if (this.shipsOfPlayerHidden != null &&
				this.shipsOfPlayerHidden.get(owner))
			{
				continue;
			}
			
			ScreenContentBoardPosition position = new ScreenContentBoardPosition(
 					ship.getPositionOnDay(day), 
 					this.players[ship.getOwner()].getColorIndex(),
 					ship.getScreenDisplaySymbol(),
 					ship.hashCode());
			
			positions.add(position);			
		}
		
		ArrayList<ScreenContentBoardMine> mines = new ArrayList<ScreenContentBoardMine>();
		
		for (Mine mine: this.mines.values())
		{
			mines.add(
					new ScreenContentBoardMine(
							mine.getPositionX(), 
							mine.getPositionY(), 
							mine.getStrength()));
		}
				
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setBoard(
				new ScreenContentBoard(
						plData,
						positionsMarked,
						null,
						positions,
						mines,
						radar));
		
		this.screenContent.setEventDay(day);
		
		if (!this.console.isBackground())
			this.gameThread.updateDisplay(this.screenContent);
	}
	
	private void updatePlanets (boolean newGame)
	{
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Byte> textCol = new ArrayList<Byte>();
		
		int[] sequenceByScore = this.archive.get(this.year) != null
				    ? Utils.sortValues(this.archive.get(this.year).getScore(), true)
				    : Utils.getSequentialList(this.playersCount);
		
	    for (int i = Constants.NEUTRAL; i < this.playersCount; i++)
		{
			int playerIndex = i == Constants.NEUTRAL ? Constants.NEUTRAL : sequenceByScore[i];
			boolean isFirstLine = true;
			
			for (int index = 0; index < this.planetsCount; index++)
			{
				int planetIndex = this.getPlanetsSorted()[index];
				
				if (this.planets[planetIndex].getOwner() != playerIndex)
					continue;
				
				if (newGame && this.planets[planetIndex].getOwner() == Constants.NEUTRAL)
					continue;
				
				if (isFirstLine)
				{
					if (playerIndex != Constants.NEUTRAL)
					{
						text.add(this.players[playerIndex].getName());
						textCol.add(this.players[playerIndex].getColorIndex());
					}
					isFirstLine = false;
				}
				
				String planetName = newGame ?
						" ??" :
						" " + this.getPlanetNameFromIndex(planetIndex);
				String fightersCount = "     " + this.planets[planetIndex].getShipsCount(ShipType.FIGHTERS);
				text.add(planetName.substring(planetName.length()-2, planetName.length()) + 
						":" +
						fightersCount.substring(fightersCount.length()-5, fightersCount.length()));
				
				textCol.add(
						playerIndex == Constants.NEUTRAL ?
								Colors.NEUTRAL :
								this.players[playerIndex].getColorIndex());
			}
		}
		
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setPlanets(
				new ScreenContentPlanets(text, textCol));
		
		if (!this.console.isBackground())
			this.gameThread.updateDisplay(this.screenContent);
	}
	
	void updateConsole(ScreenContentConsole contentConsole, boolean isBackground)
	{
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setConsole(contentConsole);
		
		if (this.gameThread != null && !isBackground)
			this.gameThread.updateDisplay(this.screenContent);
	}
	
	
	private void setScreenContentMode (int mode)
	{
		if (this.screenContent == null)
			this.screenContent = new ScreenContent();
		
		this.screenContent.setMode(mode);
	}
	
	private String getPlanetNameFromIndex(int planetIndex)
	{
		if (this.mapPlanetIndexToName == null)
			this.buildPlanetMap();
		
		return this.mapPlanetIndexToName.get(planetIndex);
	}
	
	private int getPlanetIndexFromName(String planetName)
	{
		if (this.mapPlanetNameToIndex == null)
			this.buildPlanetMap();
		
		Integer index = this.mapPlanetNameToIndex.get(planetName.toUpperCase());
		
		if (index == null)
			return Constants.NO_PLANET;
		else
			return index;
	}
	
	private void buildPlanetMap()
	{
		this.mapPlanetIndexToName = new Hashtable<Integer,String>();
		this.mapPlanetNameToIndex = new Hashtable<String,Integer>();
		this.planetsByPosition = new Hashtable<String,Integer>();
		
		String[] planetNamesUnsorted = new String[this.planetsCount];
		
		for (int planetIndex = 0; planetIndex < this.planetsCount; planetIndex++)
		{
			Planet planet = this.planets[planetIndex];
			String positionString = Integer.toString(
					(int)(planet.getPosition().getX())) + ";" + Integer.toString((int)(planet.getPosition().getY()));

			String planetName = this.getSectorNameFromPosition(planet.getPosition());
			
			this.mapPlanetIndexToName.put(planetIndex, planetName);
			this.mapPlanetNameToIndex.put(planetName, planetIndex);
			
			planetNamesUnsorted[planetIndex] = planetName;
			
			this.planetsByPosition.put(positionString, planetIndex);
		}
		
		this.planetIndicesSorted = Utils.sortList(planetNamesUnsorted, false);
	}
	
	private int getPlanetIndexFromPosition(Point position)
	{
		if (this.planetsByPosition == null)
			this.buildPlanetMap();
		
		String positionString = Integer.toString((int)(position.getX())) + ";" + Integer.toString((int)(position.getY())); 
		
		Integer planetIndex = this.planetsByPosition.get(positionString);
		
		if (planetIndex == null)
			return -1;
		else
			return planetIndex.intValue();
	}
	
	private Point getPositionFromSectorName(String sectorName)
	{
		if (sectorName.length() != 2)
			return null;
		
		int y = -1;
		
		try
		{
			y = sectorName.toUpperCase().codePointAt(0) - 65;
		}
		catch (Exception x) {}
		
		int x = -1;
		
		try
		{
			x = sectorName.toUpperCase().codePointAt(1) - 65;
		}
		catch (Exception xx) {}
		
		if (x >= 0 && x < Constants.BOARD_MAX_X && y >= 0 && y < Constants.BOARD_MAX_Y)
			return new Point(x, y);
		else
			return null;
	}
	
	private String getSectorNameFromPosition(Point position)
	{
		int plIndex = this.getPlanetIndexFromPosition(position);
		
		if (plIndex != -1)
			return this.getPlanetNameFromIndex(plIndex);
		else
			return Game.getSectorNameFromPositionStatic(position);
		
	}
	
	static String getSectorNameFromPositionStatic(Point pt)
	{
		return Character.toString((char)(65+(Utils.round(pt.getY())))) +
			   Character.toString((char)(65+(Utils.round(pt.getX()))));
		
	}
	
	private void printAllianceInfo(int planetIndex)
	{
		Planet planet = this.planets[planetIndex];
		
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
		{
			if (!planet.isAllianceMember(playerIndex))
				continue;
			
			if (counter >= 3)
			{
				this.console.appendText(sb.toString());
				this.console.lineBreak();
				sb = new StringBuilder();
				counter = 0;
			}
			
			if (sb.length() > 0)
				sb.append(", ");
			
			sb.append(this.players[playerIndex].getName());
			sb.append(" (");
			sb.append(planet.getShipsCount(ShipType.FIGHTERS, playerIndex));
			sb.append(" "+SternResources.Raumer(true)+")");
				
			counter++; 							
		}
		
		this.console.appendText(sb.toString());
	}

	private String mainMenuGetYearDisplayText()
	{
		if (this.finalized)
		{
			return SternResources.AbgeschlossenesSpiel(true, Integer.toString(this.year+1));
		}
		else
		{
			if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
				return SternResources.InventurJahr2(
						true, 
						Integer.toString(this.year+1), 
						Integer.toString(this.yearMax));
			else
				return SternResources.InventurJahr1(true, Integer.toString(this.year+1));
		}
	}
		
	private Hashtable<Integer,ArrayList<Byte>> getSimpleFrameObjekt(int planetIndex, byte colorIndex)
	{
		ArrayList<Byte> frameCol = new ArrayList<Byte>();
		frameCol.add(colorIndex);
		
		Hashtable<Integer,ArrayList<Byte>> frames = new Hashtable<Integer,ArrayList<Byte>>();
		frames.put(planetIndex, frameCol);
		
		return frames;
	}
	
	private ArrayList<Point> getSimpleMarkedPosition(Point position)
	{
		ArrayList<Point> markedPositions = new ArrayList<Point>();
		markedPositions.add(position);
		
		return markedPositions;
	}
	
	private ArrayList<Integer> getPlayersWithMissingMoves()
	{
		ArrayList<Integer> retval = new ArrayList<Integer>();
		
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			if (!this.moves.containsKey(playerIndex) && !this.players[playerIndex].isDead())
				retval.add(playerIndex);
		
		return retval;
	}
	
	KeyEventExtended waitForKeyInput()
	{
		if (!this.console.isBackground())
			return this.gameThread.waitForKeyInput();
		else
			return new KeyEventExtended(
					new KeyEvent(
							new Panel(), 
							0, 
							0, 
							0,
		                    KeyEvent.VK_TAB,
		                    '\t'), 
						"",
						"");
	}
	
	void pause(int milliseconds)
	{
		if (!this.console.isBackground())
			this.gameThread.pause(milliseconds);
	}
	
	// =========================================
 	private static class Import
	{
		// Import games from Vega (Windows 3.11)
		private byte[] bytes;
		private int pos;
		
		private Import(byte[] bytes)
		{
			this.pos = 0;
			this.bytes = bytes;
		}
		
		public Game start(String gameName)
		{
			Game game = new Game();
			game.name = gameName;
			
			if (this.inchar() != 'V')
				return null;
			
			if (this.inasc() != 6)
				return null;
			
			game.initial = false;
			
			game.boardXOffset = 0;
			game.boardYOffset = 0;
			game.boardWidth = Constants.BOARD_MAX_X;
			game.boardHeight = Constants.BOARD_MAX_Y;
			
			@SuppressWarnings("unused")
			short ladecode = this.inmki();
			
			game.dateStart = Utils.getDateFromOldVega(this.getDate(this.inmki()));
			this.inmkl(); // Game duration in seconds -> ignore
			
			short setup = this.inmki();
			this.inmki(); // Fighters at beginning -> ignore
			this.inmki(); // Number of minutes used for entering moves -> ignore
			game.yearMax = this.inmki();
			game.year = this.inmki()-1;
			
			boolean blackHole = false;
			game.options = new HashSet<GameOptions>();
			if ((setup & 32) > 0)
				game.options.add(GameOptions.COMMAND_ROOMS);
			if ((setup & 8) > 0)
				blackHole = true;
			if ((setup & 4) > 0 && game.yearMax > 0)
				game.options.add(GameOptions.LIMITED_NUMBER_OF_YEARS);
			
			game.options.add(GameOptions.DEFENCE_SHIELDS);
		
			game.playersCount = this.inasc();
			game.players = new Player[game.playersCount];
			
			int[] commandRooms = new int[game.playersCount]; 
			
			for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
			{
				String playerName = this.instring();
				
				if ((setup & 64) > 0)
					this.inmkl(); // Password -> ignore
				
				if ((setup & 32) > 0)
					commandRooms[playerIndex] = this.inasc();
				
				this.inmkl();
				
				game.players[playerIndex] = new Player(playerName, "", (byte)(playerIndex+Colors.COLOR_OFFSET_PLAYERS), false, false);
			}
			
			game.planetsCount = this.inasc() + 1;
			game.planets = new Planet[game.planetsCount];
			
			for (int planetIndex = 0; planetIndex < game.planetsCount; planetIndex++)
			{
				Hashtable<ShipType, Integer> ships = new Hashtable<ShipType, Integer>();
				
				short fightersCount = this.inmki();
				if (fightersCount > 0)
					ships.put(ShipType.FIGHTERS, (int)fightersCount);
				
			    short scoutsCount = this.inmki();
			    if (scoutsCount > 0)
					ships.put(ShipType.SCOUT, (int)scoutsCount);
			    
				short patrolsCount = this.inmki();
				if (patrolsCount > 0)
					ships.put(ShipType.PATROL, (int)patrolsCount);
				
				short mine50Count = this.inmki();
				if (mine50Count > 0)
					ships.put(ShipType.MINE50, (int)mine50Count);
				
				short mine100Count = this.inmki();
				if (mine100Count > 0)
					ships.put(ShipType.MINE100, (int)mine100Count);
				
				short mine250Count = this.inmki();
				if (mine250Count > 0)
					ships.put(ShipType.MINE250, (int)mine250Count);
				
				short mine500Count = this.inmki();
				if (mine500Count > 0)
					ships.put(ShipType.MINE50, (int)mine500Count);
				
				short transportsCount = this.inmki();
				if (transportsCount > 0)
					ships.put(ShipType.TRANSPORT, (int)transportsCount);
				
				short minesweepersCount = this.inmki();
				if (minesweepersCount > 0)
					ships.put(ShipType.MINESWEEPER, (int)minesweepersCount);
				
				short xpos = this.inasc();
				short ypos = this.inasc();
				
				Point position = new Point(xpos, ypos);
				
				short owner = (short)(this.inasc() - 1);
				
				short defenseShielCount = this.inasc();
				DefenceShield defenceShield = null;
				if (defenseShielCount > 0)
				{
					short intactPercent = this.inasc();
					defenceShield = DefenceShield.migrate(defenseShielCount, intactPercent);
				}
				
				short moneySupply = this.inmki();
				short moneyProduction = this.inasc();
				short fighterProduction = this.inasc();
				
				Alliance alliance = null;
				
				short isAlliance = this.inasc();
				if (isAlliance > 0)
				{
					alliance = new Alliance(game.playersCount);
					
					for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
					{
						int all_pl = this.inmkl();

						if (all_pl >= 0)
							alliance.addFightersCount(playerIndex, all_pl);
					}
				}
			    
			    CommandRoom commandRoom = null;
			    if (game.options.contains(GameOptions.COMMAND_ROOMS))
			    {
			    	for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
			    	{
			    		if (commandRooms[playerIndex] == planetIndex)
			    		{
			    			commandRoom = new CommandRoom(playerIndex);
			    			break;
			    		}
			    	}
			    }
			    
			    if (alliance!= null)
			    {
			    	ships.put(ShipType.FIGHTERS, alliance.getFightersCount());
			    }
			    
			    game.planets[planetIndex] = 
			    		new Planet(
			    				position, 
			    				alliance, 
			    				ships, 
			    				owner, 
			    				defenceShield, 
			    				moneySupply, 
			    				moneyProduction, 
			    				fighterProduction, 
			    				commandRoom);
			}
			
			short shipsCount = this.inmki();
			game.ships = new ArrayList<Ship>(shipsCount);
			
			if (shipsCount > 0)
			{
				for (int t = 0; t < shipsCount; t++)
				{
					short planetIndexStart = this.inasc();
					short planetIndexDestination = this.inasc();
					short positionStartX = this.inasc();
					short positionStartY = this.inasc();
					Point positionStart = new Point(positionStartX, positionStartY);
					short positionDestinationX = this.inasc();
					short positionDestinationY = this.inasc();
					Point positionDestination = new Point(positionDestinationX, positionDestinationY);
					
					if (!positionStart.equals(game.planets[planetIndexStart].getPosition()))
						planetIndexStart = Constants.NO_PLANET;
					
					if (!positionDestination.equals(game.planets[planetIndexDestination].getPosition()))
						planetIndexDestination = Constants.NO_PLANET;
					
					short yearCount = this.inasc();
					short typeValue = this.inasc();
					
					ShipType type = ShipType.FIGHTERS;
					boolean transfer = false;
					
					switch (typeValue)
					{
					case 1:		type = ShipType.FIGHTERS;
								transfer = false;
								break;
					case 2:		type = ShipType.SCOUT;
								transfer = false;
								break;
					case 3:		type = ShipType.PATROL;
								transfer = false;
								break;
					case 4:		type = ShipType.PATROL;
								transfer = true;
								break;
					case 5:		type = ShipType.MINE50;
								transfer = false;
								break;
					case 6:		type = ShipType.MINE100;
								transfer = false;
								break;
					case 7:		type = ShipType.MINE250;
								transfer = false;
								break;
					case 8:		type = ShipType.MINE500;
								transfer = false;
								break;
					case 9:		type = ShipType.MINE50;
								transfer = true;
								break;
					case 10:	type = ShipType.MINE100;
								transfer = true;
								break;
					case 11:	type = ShipType.MINE250;
								transfer = true;
								break;
					case 12:	type = ShipType.MINE500;
								transfer = true;
								break;
					case 13:	type = ShipType.FIGHTERS;
								transfer = false;
								break;
					case 14:	type = ShipType.TRANSPORT;
								transfer = false;
								break;
					case 15:	type = ShipType.MINESWEEPER;
								transfer = false;
								break;
					case 16:	type = ShipType.MINESWEEPER;
								transfer = true;
								break;
					case 17:	// Former "black hole" -> ignored
								continue;
					}
					
					short count = this.inmki();
					
					CommandRoom commandRoom = null;
					if (type == ShipType.TRANSPORT && count > 90)
						commandRoom = new CommandRoom(count - 90 - 1);
					
					Alliance alliance = null;
					if (typeValue == 13) // Allied fighters
					{
						alliance = new Alliance(game.playersCount);
						
						for (int playerIndex = 0; playerIndex < 6; playerIndex++)
						{
							int alliedFightersOfPlayerCount = this.inmkl();
							if (alliedFightersOfPlayerCount >= 0 && playerIndex < game.playersCount)
								alliance.addFightersCount(playerIndex, alliedFightersOfPlayerCount);
						}
					}
					short owner = (short)(this.inasc() - 1);
					if (owner >= game.playersCount)
						owner = Constants.NEUTRAL; // Former "extraterrestrials" -> ignored
					
					this.inmki(); // Former x position -> ignored
					this.inmki(); // Former y position -> ignored
					this.inasc(); // Flag "landed" -> ignored
					
					Ship objekt = new Ship(
							planetIndexStart, 
							planetIndexDestination, 
							positionStart,
							positionDestination,
							yearCount, 
							type,
							count, 
							owner, 
							transfer,
							alliance, 
							commandRoom);
					
					game.ships.add(objekt);
				}
			}
			
			if (blackHole)
			{
				this.inmki();
				short loch = this.inasc();
				
				if (loch > 0)
				{
					this.inasc();
					this.inasc();
				}
			}
			
			game.archive = new Hashtable<Integer,Archive>();
			
			for (int j = 0; j <= game.year; j++)
			{
				int[] scores = new int[game.playersCount];
				int[] fightersCount = new int[game.playersCount];
				int[] planetsCount = new int[game.playersCount];
				int[] moneyProductions = new int[game.playersCount];

				for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
				{
					scores[playerIndex] = this.inmkl();
				    fightersCount[playerIndex] = this.inmkl();
				    planetsCount[playerIndex] = this.inasc();
				    moneyProductions[playerIndex] = this.inmki();
				    
					scores[playerIndex] = Game.getScore(planetsCount[playerIndex], game.playersCount);
				}
								
				if (j < game.year)
					game.archive.put(j, new Archive(scores, fightersCount, planetsCount, moneyProductions));
			}
			
			game.mines = new Hashtable<String,Mine>();
			
			short minesCount = this.inmki();
			if (minesCount > 0)
			{
				for (int t = 0; t < minesCount; t++)
				{
					this.inmki(); // Former mine "history" -> ignore
				}
			}
			
			char dummy = this.inchar();
			
			if (dummy == '1')
			{
				while (this.pos < this.bytes.length)
				{
					short positionX = this.inasc();
					short positionY = this.inasc();
					short strength = this.inmki();
					
					Point position = new Point(positionX,positionY);
					
					Mine mine = game.mines.get(position.getString());
					if (mine == null)
					{
						mine = new Mine((int)position.x, (int)position.y, strength);
						game.mines.put(position.getString(), mine);
					}
					else
						mine.setStrength(strength);
				}
			}
			
			if (game.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			{
				if (game.year >= game.yearMax)
					game.finalized = true;
			}
			
			game.buildPlanetMap();
			game.calculateScores();
			game.prepareYear();
			
			return game;
		}
		
		private char inchar()
		{
			return (char)this.getByteValue();
		}
		private short inasc()
		{
			return this.getByteValue();
		}
		
		private short inmki()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			
			return (short)(byte0 + byte1 * 256 - 65536);
		}
		
		private int inmkl()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			short byte2 = this.getByteValue();
			short byte3 = this.getByteValue();
			
			return (int)(byte0 + byte1 * 256 + byte2 * 256 * 256 + byte3 * 256 * 256 * 256);
		}
		
		private String instring()
		{
			String retval = "";
			
			while ((char)this.bytes[this.pos] != '\r')
				retval = retval + this.inchar();
			
			this.pos += 2; // because of \r\n
			
			return retval;
		}
		
		private short getByteValue()
		{
			short b = (short)this.bytes[this.pos];
			if (b < 0)
				b += 256;
			
			this.pos++;
			
			return b;
		}
		
		private String getDate(short val)
		{
			String day = "0" + Integer.toString(((val + 32767) % (31*100))/100 + 1);
			String month = "0" + Integer.toString((val + 32767) / (100*31) + 1);
			String year = Integer.toString((val + 32767) % 100 + 1980);
			
			return year + month.substring(month.length()-2, month.length()) + day.substring(day.length()-2, day.length()); 
		}
	}
 	
 	// =====================
 	
 	public class PlanetInfo
 	{
 		public int positionX;
 		public int positionY;
 		public byte colorIndex;
 		
 		private PlanetInfo(int positionX, int positionY, byte colorIndex)
 		{
 			this.positionX = positionX;
 			this.positionY = positionY;
 			this.colorIndex = colorIndex;
 		}
 	}
 	// =====================
 	private class EnterMoves
 	{
 		private Game game;
 		private int playerIndexNow;
 		private boolean enterMovesFinished;
 		private boolean capitulated;
 		
 		@SuppressWarnings("unchecked")
 		private EnterMoves(Game game)
 		{
 			this.game = game;

 			Planet[] planetsCopy = (Planet[])Utils.klon(this.game.planets);
			ArrayList<Ship> shipsCopy = (ArrayList<Ship>)Utils.klon(this.game.ships);
			
			if (this.game.soloPlayer)
			{
				int playerIndex = -1;
				
				for (int i = 0; i < this.game.playersCount; i++)
				{
					if (this.game.playerReferenceCodes[i] != null)
					{
						playerIndex = i;
						break;
					}
				}
				
				if (this.game.moves != null && this.game.moves.containsKey(playerIndex))
				{
					this.game.console.appendText(
								SternResources.ZugeingabeSpielzuegeSchonEingegeben(true));
					this.game.console.lineBreak();
					return;
				}
				
				this.enterMovesPlayer(playerIndex);
				
 				this.game.planets = (Planet[])Utils.klon(planetsCopy);
 				this.game.ships = (ArrayList<Ship>)Utils.klon(shipsCopy);
 				
 				MovesTransportObject movesTransportObject = new MovesTransportObject(
 												this.game.playerReferenceCodes[playerIndex],
 												Constants.BUILD_COMPATIBLE,
 												this.game.moves.get(playerIndex));
 				
 				boolean success = false;
 				PostMovesResult result = PostMovesResult.ERROR;
 				
 				do
 				{
	 				if (this.game.options.contains(GameOptions.SERVER_BASED))
	 				{
	 					result = this.game.gameThread.postMovesToServer(
								game.name,
								this.game.players[this.playerIndexNow].getName(),
								movesTransportObject);
	 						
	 					if (result == PostMovesResult.USER_NOT_CONNECTED)
	 					{
	 						this.game.console.appendText(
	 								SternResources.SpielerNichtAngemeldet(
	 										true,
	 										this.game.players[this.playerIndexNow].getName()));
	 						this.game.console.lineBreak();
	 					}
	 					else if (result != PostMovesResult.ERROR)
	 					{
	 						this.game.console.appendText(
	 								SternResources.ZugeingabePostMovesSuccess(true));
	 						this.game.console.lineBreak();
	 						success = true;
	 					}
	 				}
	 				else
	 				{
		 				this.game.console.appendText(
		 						SternResources.ZugeingabeEMailErzeugt(true));
		 				this.game.console.lineBreak();
		 				this.game.console.appendText(
		 						SternResources.ZugeingabeEMailErzeugt2(true));
		 				this.game.console.lineBreak();
		 				
		 				String subject = "[Stern] " + this.game.name;
		 				
		 				String bodyText = 
		 						SternResources.ZugeingabeEMailBody(false,
		 								this.game.name,
		 								Integer.toString(this.game.year + 1),
		 								this.game.players[playerIndex].getName(),
		 								ReleaseGetter.getRelease(),
		 								this.game.players[playerIndex].getName(),
		 								this.game.getName());
		 				
		 				success = this.game.gameThread.launchEmail(
		 						this.game.getEmailAddressGameHost(), 
		 						subject, 
		 						bodyText, 
		 						movesTransportObject);
	 				}
	 				
	 				if (!success)
	 				{
	 					this.game.console.appendText(
	 							SternResources.ZugeingabePostMovesError(true));
	 					this.game.console.lineBreak();
	 					
	 					ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
	 					
	 					allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
	 					allowedKeys.add(new ConsoleKey(
	 							SternResources.AndereTaste(true),
	 							SternResources.NochmalVersuchen(true)));
						
	 					ConsoleInput consoleInput = this.game.console.waitForKeyPressed(allowedKeys, false);
						
						if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
						{
							success = true;
						}
	 				}
						
	 				
 				} while (!success);
 				
 				do
 				{
 					// Endless loop!
 					if (this.game.options.contains(GameOptions.SERVER_BASED))
 					{
 						this.game.gameThread.updateGameInfo();
 						
 						if (this.game.year >= this.game.yearMax - 1)
 						{
 							this.game.console.appendText(
	 								SternResources.LetztesJahr(true));
 							this.game.console.lineBreak();
 							this.game.console.appendText(
 									SternResources.LetztesJahr2(true));
 						}
 						else
 						{
	 						if (result == PostMovesResult.WAIT_FOR_EVAULATION)
		 						this.game.console.appendText(
		 								SternResources.WartenBisNaechsteZugeingabe(true));
	 						else
	 						{
	 							this.game.console.appendText(
		 								SternResources.AuswertungVerfuegbar(true));
	 							this.game.console.lineBreak();
	 							this.game.console.appendText(
		 								SternResources.AuswertungVerfuegbar2(true));
	 						}
 						}
 					}
 					else
 						this.game.console.appendText(
		 						SternResources.ZugeingabeEMailEndlosschleife(true));
 					
	 				this.game.console.waitForKeyPressed();
 				} while (true);
			}
			else
			{
				for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
				{
					Player player = this.game.players[playerIndex];
					
					if (!this.game.moves.containsKey(playerIndex))
					{
						if (player.isBot())
						{
							Game.enterMovesBot(this.game, playerIndex);
	
			 				this.game.planets = (Planet[])Utils.klon(planetsCopy);
			 				this.game.ships = (ArrayList<Ship>)Utils.klon(shipsCopy);
							continue;
						}
					}
				}
				
				do
				{
					ArrayList<Integer> playersWithMissingMoves = this.game.getPlayersWithMissingMoves();
					this.enterMovesFinished = (playersWithMissingMoves.size() == 0);
					
					if (this.enterMovesFinished)
						return;
					
					this.game.console.setHeaderText(
								this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.ZugeingabeTitel(true), Colors.NEUTRAL);
					
					ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
					StringBuilder sb = new StringBuilder();
					
					boolean missingMoves = false;

					for (int i = 0; i < playersWithMissingMoves.size(); i++)
					{
						int playerIndex = playersWithMissingMoves.get(i).intValue();
						if (!this.game.isPlayerEmail(playerIndex) && !this.game.soloPlayer)
						{
							allowedKeys.add(new ConsoleKey(
									Integer.toString(playerIndex + 1),
									this.game.players[playerIndex].getName()));
							missingMoves = true;
						}
						
						if (sb.length() > 0)
							sb.append(", ");
						sb.append(this.game.players[playerIndex].getName());
					}
					
					if (this.game.options.contains(GameOptions.EMAIL_BASED))
						allowedKeys.add(
								new ConsoleKey("9",SternResources.ZugeingabeEMailAktionen(true)));
					
					allowedKeys.add(new ConsoleKey("ESC",SternResources.Hauptmenue(true)));
					if (missingMoves)
						allowedKeys.add(new ConsoleKey("TAB",SternResources.ZugeingabeZufaelligerSpieler(true)));
										
					int playerIndex = -1;
					
					this.game.console.setLineColor(Colors.NEUTRAL);
					this.game.console.appendText(SternResources.ZugeingabeWartenAufSpielzuege(true));
					this.game.console.lineBreak();
					this.game.console.setLineColor(Colors.WHITE);
					this.game.console.appendText(sb.toString());
					this.game.console.lineBreak();
					
					ConsoleInput consoleInput = this.game.console.waitForKeyPressed(allowedKeys, false);
					String input = consoleInput.getInputText().toUpperCase();
					
					if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.console.clear();
						break;
					}
					else if (input.equals("9"))
					{
						this.emailMenu();
					}
					else
					{
						boolean ok = false;
						
						try
						{
							if (missingMoves && input.equals("\t"))
							{
								playerIndex = Utils.getRandomInteger(game.playersCount);
								
								for (int i = 0; i < game.playersCount; i++)
								{
									if (playersWithMissingMoves.contains(playerIndex) && !(this.game.isPlayerEmail(playerIndex) && !this.game.soloPlayer))
									{
										ok = true;
										break;
									}
									playerIndex = (playerIndex + 1) % game.playersCount;
								}
							}
							else
							{
								playerIndex = Integer.parseInt(input) - 1;
								
								if (playersWithMissingMoves.contains(playerIndex) && !(this.game.isPlayerEmail(playerIndex) && !this.game.soloPlayer))
									ok = true;
							}
						}
						catch (Exception x)
						{ }
						
						if (ok)
						{
							boolean abort = this.enterMovesPlayer(playerIndex);
							
							if (abort)
								this.game.moves.remove(playerIndex);
							
			 				this.game.planets = (Planet[])Utils.klon(planetsCopy);
			 				this.game.ships = (ArrayList<Ship>)Utils.klon(shipsCopy);
			 				
			 				this.game.console.clear();
			 				
			 				this.game.autosave();
						}
						else
							this.game.console.outInvalidInput();
					}
				} while (true);
			}
 		}
 		
 		private boolean enterMovesPlayer(int playerIndex)
 		{
 			this.playerIndexNow = playerIndex; 			
			this.game.console.clear();
			
			ScreenContentPlanets pdc = 
					(ScreenContentPlanets)Utils.klon(game.screenContent.getPlanets());
			
			ArrayList<Move> moves = new ArrayList<Move>();
			this.game.moves.put(playerIndex, moves);
			boolean simple = this.game.options.contains(GameOptions.SIMPLE);
				
			boolean exit = false;
			this.capitulated = false;
			
			this.game.shipsOfPlayerHidden = new BitSet(game.playersCount);
			
 			do
 			{ 				
 				this.game.console.setHeaderText(
 							this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName(),
 							this.game.players[this.playerIndexNow].getColorIndex());
 				
 				ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
 				
 				allowedKeys.add(new ConsoleKey("TAB",SternResources.ZugeingabeFertig(true)));
 				allowedKeys.add(new ConsoleKey("-",SternResources.ZugeingabeUndo(true)));
 				
 				if (!this.capitulated)
 				{
 					if (!simple)
 						allowedKeys.add(new ConsoleKey("0",SternResources.ZugeingabePlanet(true)));
 					else
 						allowedKeys.add(new ConsoleKey("0",SternResources.ZugeingabePlaneteninfo(true)));
 					
 					allowedKeys.add(new ConsoleKey("1",SternResources.ZugeingabeRaumer(true)));
 					
 					if (!simple)
 					{
 						allowedKeys.add(new ConsoleKey("2",SternResources.ZugeingabeBuendRaumer(true)));
 						allowedKeys.add(new ConsoleKey("3",SternResources.ZugeingabeAufklaerer(true)));
 						allowedKeys.add(new ConsoleKey("4",SternResources.ZugeingabePatrouille(true)));
 						allowedKeys.add(new ConsoleKey("5",SternResources.ZugeingabeTransporter(true)));
 						allowedKeys.add(new ConsoleKey("6",SternResources.ZugeingabeMine(true)));
 						allowedKeys.add(new ConsoleKey("7",SternResources.ZugeingabeMinenraeumer(true)));
 						
 						allowedKeys.add(new ConsoleKey("8",SternResources.ZugeingabeBuendnis(true)));
 						allowedKeys.add(new ConsoleKey("9",SternResources.ZugeingabeMehr(true)));
 					}
 					else
 					{
 						allowedKeys.add(new ConsoleKey("5",SternResources.ZugeingabeKapitulieren(true)));
 						allowedKeys.add(new ConsoleKey("7",SternResources.Entfernungstabelle(true)));
 						allowedKeys.add(new ConsoleKey("8",SternResources.ZugeingabeObjekteAusblenden(true))); 						
 						allowedKeys.add(new ConsoleKey("9",SternResources.ZugeingabeInventur(true)));
 					}
 				}
 				
 				boolean quit = this.stoppedShips();
				if (quit)
				{
					this.game.shipsOfPlayerHidden = null;
		 			this.game.updateBoard();

					return true;
				}
		
 				ConsoleInput consoleInput = this.game.console.waitForKeyPressed(allowedKeys, true);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					this.game.console.clear();
				else if (input.equals("\t"))
					exit = this.finish();
				else if (!capitulated && input.equals("1"))
					this.fighters(false);
				else if (!capitulated && !simple && (input.equals("2")))
					this.fighters(true);
				else if (!capitulated && !simple && (input.equals("3")))
					this.scoutsPatrolsTransports(ShipType.SCOUT);
				else if (!capitulated && !simple && (input.equals("4")))
					this.scoutsPatrolsTransports(ShipType.PATROL);
				else if (!capitulated && !simple && (input.equals("5")))
					this.scoutsPatrolsTransports(ShipType.TRANSPORT);
				else if (!capitulated && simple && (input.equals("5")))
					this.capitulate();
				else if (!capitulated && !simple && (input.equals("6")))
					this.MinesAndSweepers(ShipType.MINE50);
				else if (!capitulated && !simple && (input.equals("7")))
					this.MinesAndSweepers(ShipType.MINESWEEPER);
				else if (simple && (input.equals("7")))
					new DistanceMatrix(this.game);
				else if (!capitulated && !simple && (input.equals("8")))
					this.alliance();
				else if (!capitulated && simple && (input.equals("8")))
					this.hideShips();
				else if (!capitulated && !simple && (input.equals("0")))
					this.planetEditor();
				else if (!capitulated && simple && (input.equals("0")))
					this.planetInfo();
				else if (!capitulated && input.equals("9"))
				{
					if (simple)
						this.inventory();
					else
						this.enterMovesPlayerMore(playerIndex);
				}
				else if (input.equals("-"))
				{
					quit = this.undo();
					if (quit)
					{
						this.game.shipsOfPlayerHidden = null;
			 			this.game.updateBoard();

						return true;
					}
				}
				else
					this.game.console.outInvalidInput();
				
				if (game.isMoveEnteringOpen())
					game.updatePlanets(false);
				else
				{
					this.game.screenContent.setPlanets(pdc);
	 				this.game.gameThread.updateDisplay(this.game.screenContent);
				}

 			} while (!exit);
 			
 			this.game.shipsOfPlayerHidden = null;
 			this.game.updateBoard();
 			
 			if (!game.soloPlayer)
 			{
 				this.game.screenContent.setPlanets(pdc);
 				this.game.gameThread.updateDisplay(this.game.screenContent);
 			}

 			return false;
 		}
 		
 		private boolean enterMovesPlayerMore(int playerIndex)
 		{
 			this.playerIndexNow = playerIndex; 			
			this.game.console.clear();
			
			this.game.shipsOfPlayerHidden = new BitSet(this.game.playersCount);
			
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			
			allowedKeys.add(new ConsoleKey("ESC",SternResources.Zurueck(true)));
			allowedKeys.add(new ConsoleKey("7",SternResources.Entfernungstabelle(true)));
			allowedKeys.add(new ConsoleKey("8",SternResources.ZugeingabeObjekteAusblenden(true)));
			allowedKeys.add(new ConsoleKey("9",SternResources.ZugeingabeInventur(true)));
			allowedKeys.add(new ConsoleKey("-",SternResources.ZugeingabeKapitulieren(true)));
	
			boolean exit = false;
			
 			do
 			{ 				
 				this.game.console.setHeaderText(
 							this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName(),
 							this.game.players[this.playerIndexNow].getColorIndex());
 				
 				ConsoleInput consoleInput = this.game.console.waitForKeyPressed(allowedKeys, true);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.game.console.clear();
					exit = true;
				}
				else if (input.equals("7"))
					new DistanceMatrix(this.game);
				else if (input.equals("8"))
					this.hideShips();
				else if (input.equals("9"))
					this.inventory();
				else if (input.equals("-"))
				{
					this.capitulate();
					exit = this.capitulated;
				}
				else
					this.game.console.outInvalidInput();
 			} while (!exit);
 			
 			return false;
 		}
 		
 		private void capitulate()
 		{
 			this.game.console.setHeaderText(
 					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeKapitulieren(true),
 					this.game.players[this.playerIndexNow].getColorIndex());

 			this.game.console.clear();
 				
			this.game.console.appendText(SternResources.AreYouSure(true));
			this.game.console.lineBreak();

			String input = this.game.console.waitForKeyPressedYesNo(!game.isMoveEnteringOpen()).getInputText().toUpperCase();
			
			if (!input.equals(Console.KEY_YES))
			{
				this.game.console.outAbort();
				return;
			}
			
			Ship ship = new Ship(
					0,
					0,
					null,
					null,
					ShipType.CAPITULATION,
					1,
					this.playerIndexNow,
					false,
					true,
					null,
					null); 				

			this.game.ships.add(ship);
	
			this.game.moves.get(this.playerIndexNow).add(
					new Move(
							0,
							ship,
							null));
			
			this.capitulated = true;
	
			this.game.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.game.console.lineBreak();
 		}
 		
 		private void emailMenu()
 		{
 			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.ZugeingabeTitel(true)+" -> "+SternResources.ZugeingabeEMailAktionen(true), Colors.NEUTRAL);
		
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			allowedKeys.add(new ConsoleKey("1",SternResources.ZugeingabeSpielstandVerschicken(true)));
			allowedKeys.add(new ConsoleKey("2",SternResources.ZugeingabeSpielzuegeImportieren(true)));
			allowedKeys.add(new ConsoleKey("ESC",SternResources.Zurueck(true)));
			
			do
			{
				ConsoleInput consoleInput = this.game.console.waitForKeyPressed(allowedKeys, false);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					break;
				else if (input.equals("1"))
				{
					this.game.autosave();
					
					this.sendGameToEmailPlayer();
					break;
				}
				else if (input.equals("2"))
				{
					MovesTransportObject movesTransportObject = this.game.gameThread.importMovesFromEmail();
					
					if (movesTransportObject != null)
					{
						int playerIndex = this.game.importMovesFromEmail(movesTransportObject);
						
						if (playerIndex >= 0)
						{
							this.game.console.setLineColor(this.game.players[playerIndex].getColorIndex());
							this.game.console.appendText(
									SternResources.ZugeingabeSpielzuegeImportiert(true, this.game.players[playerIndex].getName()));
							this.game.console.lineBreak();
							this.game.console.setLineColor(Colors.WHITE);
							
							this.game.autosave();
						}
						else
						{
							this.game.console.appendText(SternResources.ZugeingabeSpielzuegeFalscheRunde(true));
							this.game.console.lineBreak();
						}
					}
					else
					{
						this.game.console.appendText(SternResources.ZugeingabeSpielzuegeNichtImportiert(true));
						this.game.console.lineBreak();
					}
				}
				else
					this.game.console.outInvalidInput();
					
				
			} while (true);
 		}
 		
 		private void sendGameToEmailPlayer()
 		{
 			int emailsCount = 0;
 			
 			for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 			{
 				Player player = this.game.players[playerIndex];
 				if (!this.game.isPlayerEmail(playerIndex))
 					continue;
 				
 				Game gameCopy = this.game.createCopyForPlayer(playerIndex);
 				
 				String subject = "[Stern] " + gameCopy.name;
 				
 				String bodyText = 
 						SternResources.ZugeingabeEMailBody2(false,
 								gameCopy.name,
 								Integer.toString(gameCopy.year + 1),
 								ReleaseGetter.getRelease(),
 								player.getName());
 				
 				this.game.gameThread.launchEmail(
 						player.getEmail(), 
 						subject, 
 						bodyText, 
 						gameCopy);
 				
 				emailsCount++;
 			}
 			
 			if (emailsCount > 0)
 			{
 				this.game.console.appendText(
 						SternResources.ZugeingabeEMailErzeugt3(true,
 							Integer.toString(emailsCount)));
 				this.game.console.lineBreak();
 				this.game.console.appendText(SternResources.ZugeingabeEMailErzeugt4(true));
 				this.game.console.lineBreak();
 			}
 		}
 		
 		private void fighters(boolean isAlliance)
 		{
			this.game.console.setHeaderText(
				this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeRaumer(true),
				this.game.players[this.playerIndexNow].getColorIndex());

			this.game.console.clear();
			
			int planetIndexStart = -1;

			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();	

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabeStartplanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndexStart = input.planetIndex;

				if (!isAlliance && this.game.planets[planetIndexStart].getShipsCount(ShipType.FIGHTERS,this.playerIndexNow) > 0)
					break;
				else if (isAlliance && this.game.planets[planetIndexStart].allianceExists() && this.game.planets[planetIndexStart].getFightersCount(this.playerIndexNow) > 0)
					break;
				else
				{
					this.game.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
					this.game.console.lineBreak();
				}

			} while (true);

			int planetIndexDestination = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabeZielplanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndexDestination = input.planetIndex;

				if (planetIndexStart != planetIndexDestination)
					break;
				else
				{
					this.game.console.appendText(SternResources.ZugeingabeZielplanetIstStartplanet(true));
					this.game.console.lineBreak();
				}

			} while (true);

			int count = -1;
			String inputText = "";

			allowedKeys = new ArrayList<ConsoleKey>();

			allowedKeys.add(new ConsoleKey("+",SternResources.ZugeingabeAlleRaumer(true)));
			allowedKeys.add(new ConsoleKey("-",SternResources.ZugeingabeInfo(true)));
			
			do
			{
				this.game.console.appendText(SternResources.ZugeingabeAnzahl(true)+": ");

				ConsoleInput input = this.game.console.waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.game.console.outAbort();
					count = -1;
					break;
				}

				inputText = input.getInputText().toUpperCase();

				if (inputText.length() == 0)
				{
					this.game.console.outInvalidInput();
					continue;
				}

				int countTemp = 0;
				int countMaxTemp = 0;

				if (isAlliance)
					countMaxTemp = this.game.planets[planetIndexStart].getShipsCount(ShipType.FIGHTERS);
				else
					countMaxTemp = this.game.planets[planetIndexStart].getShipsCount(ShipType.FIGHTERS, this.playerIndexNow);

				if (inputText.equals("-"))
				{
					this.game.console.appendText(
							SternResources.ZugeingabeMaxAnzahlRaumer(true, 
									Integer.toString((countMaxTemp))) + " ");
					
					ShipTravelTime travelTime = Ship.getTravelTime(
							ShipType.FIGHTERS, 
							false, 
							this.game.planets[planetIndexStart].getPosition(), 
							this.game.planets[planetIndexDestination].getPosition());
					
					this.game.console.appendText(
							SternResources.ZugeingabeAnkunft(true));
					
					travelTime.year += this.game.year;
					this.game.console.appendText(
							travelTime.toOutputString(true));

					this.game.console.waitForKeyPressed();
					continue;
				}

				if (inputText.equals("+"))
					countTemp = countMaxTemp;
				else
				{
					try { countTemp = Integer.parseInt(inputText); }
					catch (Exception e)
					{
						this.game.console.outInvalidInput();
						continue;
					}

					if (countTemp < 0 || countTemp > countMaxTemp)
					{
						console.appendText(SternResources.ZugeingabeNichtGenugRaumer(true));
						console.lineBreak();
						continue;
					}
				}

				if (countTemp > 0)
				{
					count = countTemp;
					break;
				}

			} while (true);

			if (count < 0)
				return;

			Planet planetCopy = (Planet)Utils.klon(this.game.planets[planetIndexStart]);

			int[] reductions = 
					this.game.planets[planetIndexStart].subtractFightersCount(this.game.playersCount, count, this.playerIndexNow, isAlliance);

			Alliance alliance = null;

			if (isAlliance)
				alliance = this.game.planets[planetIndexStart].copyAllianceStructure(reductions);

			Ship ship = new Ship(
					planetIndexStart,
					planetIndexDestination,
					this.game.planets[planetIndexStart].getPosition(),
					this.game.planets[planetIndexDestination].getPosition(),
					ShipType.FIGHTERS,
					count,
					this.playerIndexNow,
					false,
					true,
					alliance,
					null);

			this.game.ships.add(ship);

			this.game.moves.get(this.playerIndexNow).add(
					new Move(
							planetIndexStart, 
							ship,
							planetCopy));

			this.game.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.game.console.lineBreak();
 		}
 		
 		private void scoutsPatrolsTransports(ShipType typ)
 		{
 			String typeDisplayName = "";
 			if (typ == ShipType.SCOUT)
 				typeDisplayName = SternResources.ZugeingabeAufklaerer(true);
 			else if (typ == ShipType.TRANSPORT)
 				typeDisplayName = SternResources.ZugeingabeTransporter(true);
 			else if (typ == ShipType.PATROL)
 				typeDisplayName = SternResources.ZugeingabePatrouille(true);
 			
 			this.game.console.setHeaderText(
 					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+typeDisplayName,
 					this.game.players[this.playerIndexNow].getColorIndex());

 			this.game.console.clear();

			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();			

			int planetIndexStart = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabeStartplanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndexStart = input.planetIndex;

				if (this.game.planets[planetIndexStart].getOwner() != this.playerIndexNow || this.game.planets[planetIndexStart].getShipsCount(typ) < 1)
				{
					console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
					console.lineBreak();
					continue;
				}

				break;

			} while(true);

			int planetIndexDestination = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabeZielplanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndexDestination = input.planetIndex;

				if (planetIndexDestination == planetIndexStart)
				{
					console.appendText(
							SternResources.ZugeingabeZielplanetIstStartplanet(true));
					console.lineBreak();
					continue;
				}

				break;

			} while (true);

			Planet planetCopy = (Planet)Utils.klon(this.game.planets[planetIndexStart]);

			Ship ship = null;

			if (typ == ShipType.SCOUT)
			{
				ship = new Ship(
						planetIndexStart,
						planetIndexDestination,
						this.game.planets[planetIndexStart].getPosition(),
						this.game.planets[planetIndexDestination].getPosition(),
						typ,
						1,
						this.playerIndexNow,
						false,
						true,
						null,
						null);

			}
			else if (typ == ShipType.TRANSPORT)
			{
				allowedKeys = new ArrayList<ConsoleKey>();

				allowedKeys.add(new ConsoleKey("+",SternResources.ZugeingabeAlleEe(true)));

				if (this.game.options.contains(GameOptions.COMMAND_ROOMS))
					allowedKeys.add(new ConsoleKey("99",SternResources.ZugeingabeKommandozentraleVerlegen(true)));

				int count = -1;

				do
				{
					this.game.console.appendText(
						SternResources.ZugeingabeWievieleEe(true,
									Integer.toString(Constants.TRANSPORT_MONEY_MAX)) + " ");

					ConsoleInput input = this.game.console.waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						count = -1;
						this.game.console.outAbort();
						break;
					}

					if (input.getInputText().toUpperCase().equals("+"))
						count = Math.min(this.game.planets[planetIndexStart].getMoneySupply(), Constants.TRANSPORT_MONEY_MAX);
					else
					{
						try
						{
							count = Integer.parseInt(input.getInputText());
						}
						catch (Exception e)
						{
							this.game.console.outInvalidInput();
							continue;
						}

						if (count == 99)
						{
							if (!this.game.options.contains(GameOptions.COMMAND_ROOMS))
							{
								this.game.console.outInvalidInput();
								continue;
							}
							else
							{
								if (!this.game.planets[planetIndexStart].hasCommandRoom())
								{
									console.appendText(SternResources.ZugeingabeKeineKommandozentrale(true));
									console.lineBreak();
									continue;
								}
							}
						}
						else if (count < 0 || count > Math.min(this.game.planets[planetIndexStart].getMoneySupply(), Constants.TRANSPORT_MONEY_MAX))
						{
							console.appendText(SternResources.ZugeingabeZuVielEe(true));
							console.lineBreak();
							continue;
						}
					}

					break;

				} while (true);

				if (count < 0)
					return;

				CommandRoom commandRoom = null;

				if (count == 99)
				{
					commandRoom = this.game.planets[planetIndexStart].removeCommandRoom();
					count = 0;
				}
				else
					this.game.planets[planetIndexStart].subtractMoneySupply(count);

				ship = new Ship(
						planetIndexStart,
						planetIndexDestination,
						this.game.planets[planetIndexStart].getPosition(),
						this.game.planets[planetIndexDestination].getPosition(),
						typ,
						count,
						this.playerIndexNow,
						false,
						true,
						null,
						commandRoom);

			}
			else if (typ == ShipType.PATROL)
			{
				allowedKeys = new ArrayList<ConsoleKey>();

				allowedKeys.add(new ConsoleKey("1",SternResources.ZugeingabePatrouilleMission(true)));
				allowedKeys.add(new ConsoleKey("2", SternResources.ZugeingabePatrouilleTransfer(true)));
				allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

				boolean transfer = false;
				int count = 1;
				boolean abort = false;

				do
				{
					this.game.console.appendText(SternResources.ZugeingabeMissionTransferFrage(true) + " ");

					ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.console.outAbort();
						abort = true;
						break;
					}
					
					String key = input.getInputText().toUpperCase();

					if (!key.equals("1") &&
						!key.equals("2"))
					{
						this.game.console.outInvalidInput();
						continue;
					}

					transfer = (key.equals("2"));

					break;

				} while (true);

				if (abort)
					return;

				ship = new Ship(
						planetIndexStart,
						planetIndexDestination,
						this.game.planets[planetIndexStart].getPosition(),
						this.game.planets[planetIndexDestination].getPosition(),
						typ,
						count,
						this.playerIndexNow,
						transfer,
						true,
						null,
						null); 				
			}

			this.game.planets[planetIndexStart].decrementShipsCount(typ, 1);
			this.game.ships.add(ship);

			this.game.moves.get(this.playerIndexNow).add(
					new Move(
							planetIndexStart,
							ship,
							planetCopy));

			this.game.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.game.console.lineBreak();
 		}
 		
 		private void MinesAndSweepers(ShipType shipCategory)
 		{
			if (shipCategory == ShipType.MINESWEEPER)
				this.game.console.setHeaderText(
						this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeMinenraeumer(true),
						this.game.players[this.playerIndexNow].getColorIndex());
			else					
				this.game.console.setHeaderText(
						this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeMine(true),
						this.game.players[this.playerIndexNow].getColorIndex());
 				
			this.game.console.clear();
			
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

			int planetIndexStart = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabeStartplanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndexStart = input.planetIndex;

				if (this.game.planets[planetIndexStart].getOwner() != this.playerIndexNow)
				{
					console.appendText(SternResources.ZugeingabePlanetGehoertNicht(true));
					console.lineBreak();
					continue;
				}

				if (shipCategory == ShipType.MINESWEEPER)
				{
					if (this.game.planets[planetIndexStart].getShipsCount(ShipType.MINESWEEPER) <= 0)
					{
						console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						console.lineBreak();
						continue;
					}
				}

				break;

			} while (true);
			
			boolean transfer = false;
			ShipType type = ShipType.MINESWEEPER;

			if (shipCategory != ShipType.MINESWEEPER)
			{
				allowedKeys = new ArrayList<ConsoleKey>();

				allowedKeys.add(new ConsoleKey("1",SternResources.ZugeingabeMine50(true)));
				allowedKeys.add(new ConsoleKey("2",SternResources.ZugeingabeMine100(true)));
				allowedKeys.add(new ConsoleKey("3",SternResources.ZugeingabeMine250(true)));
				allowedKeys.add(new ConsoleKey("4",SternResources.ZugeingabeMine500(true)));

				boolean abort = false;

				do
				{
					this.game.console.appendText(SternResources.ZugeingabeMineTypFrage(true) + " ");

					ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.console.outAbort();
						abort = true;
						break;
					}

					if (input.getInputText().equals("1"))
					{
						type = ShipType.MINE50;
					}
					else if (input.getInputText().equals("2"))
					{
						type = ShipType.MINE100;
					}
					else if (input.getInputText().equals("3"))
					{
						type = ShipType.MINE250;
					}
					else if (input.getInputText().equals("4"))
					{
						type = ShipType.MINE500;
					}
					else
					{
						this.game.console.outInvalidInput();
						continue;
					}

					if (this.game.planets[planetIndexStart].getShipsCount(type) <= 0)
					{
						console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						console.lineBreak();
						continue;
					}

					break;

				} while (true);

				if (abort)
					return;
			}
			else
			{
				allowedKeys = new ArrayList<ConsoleKey>();

				allowedKeys.add(new ConsoleKey("1",SternResources.ZugeingabeMinenraeumerMission(true)));
				allowedKeys.add(new ConsoleKey("2", SternResources.ZugeingabePatrouilleTransfer(true)));
				allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

				boolean abort = false;

				do
				{
					this.game.console.appendText(SternResources.ZugeingabeMissionTransferFrage(true) + " ");

					ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.game.console.outAbort();
						abort = true;
						break;
					}

					if (!input.getInputText().toUpperCase().equals("1") && 
					    !input.getInputText().toUpperCase().equals("2"))
					{
						this.game.console.outInvalidInput();
						continue;
					}

					transfer = (input.getInputText().toUpperCase().equals("2"));

					break;

				} while (true);

				if (abort)
					return;
			}

			PlanetInputStruct inputDestination;

			allowedKeys = new ArrayList<ConsoleKey>();						

			do
			{
				inputDestination = this.game.getPlanetInput(
						SternResources.ZugeingabeMineZielsektor(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_SECTOR);
				
				if (inputDestination == null)
				{
					return;
				}
				
				if (inputDestination.sector.equals(this.game.planets[planetIndexStart].getPosition()))
				{
					console.appendText(SternResources.ZugeingabeZielplanetIstStartplanet(true));
					console.lineBreak();
					continue;
				}
				
				if ((type == ShipType.MINESWEEPER && transfer) &&
						inputDestination.planetIndex == Constants.NO_PLANET)
				{
					console.appendText(SternResources.ZugeingabeZielTransfer(true));
					console.lineBreak();
					continue;
				}

				break;
			} while (true);

			Planet planetCopy = (Planet)Utils.klon(this.game.planets[planetIndexStart]);

			if (type != ShipType.MINESWEEPER)
				transfer = (inputDestination.planetIndex != Constants.NO_PLANET);

			this.game.planets[planetIndexStart].decrementShipsCount(type, 1);

			Ship ship = new Ship(
					planetIndexStart,
					inputDestination.planetIndex,
					this.game.planets[planetIndexStart].getPosition(),
					inputDestination.sector,
					type,
					1,
					this.playerIndexNow,
					transfer,
					true,
					null,
					null);

			this.game.ships.add(ship);

			this.game.moves.get(this.playerIndexNow).add(
					new Move(
							planetIndexStart,
							ship,
							planetCopy));

			this.game.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.game.console.lineBreak();
 		}
 		
 		private void alliance()
 		{
 			this.game.console.setHeaderText(
 					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeBuendnis(true),
 					this.game.players[this.playerIndexNow].getColorIndex());

 			this.game.console.clear();

 			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();

 			int planetIndex = -1;
 			Planet planet = null;

 			do
 			{
 				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabePlanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
 				planetIndex = input.planetIndex;
 				planet = this.game.planets[planetIndex];

 				if (planet.getOwner() != this.playerIndexNow && 
 				   !planet.isAllianceMember(this.playerIndexNow) &&
 				   !planet.hasRadioStation(this.playerIndexNow))
 				{
 					console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
 					console.lineBreak();
 					continue;
 				}

 				break;
 			} while (true);

 			if (planet.getOwner() != this.playerIndexNow && 
 	 				    !planet.isAllianceMember(this.playerIndexNow))
			{
				this.printAlliance(planet, planetIndex);
				this.game.console.waitForKeyPressed();
				this.game.console.clear();
				return;
			}

 			allowedKeys = new ArrayList<ConsoleKey>();

 			allowedKeys.add(new ConsoleKey("0",SternResources.ZugeingabeKuendigen(true)));

 			for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 				if (playerIndex != this.playerIndexNow)
 					allowedKeys.add(new ConsoleKey(
 							Integer.toString(playerIndex+1), 
 							this.game.players[playerIndex].getName()));

 			allowedKeys.add(new ConsoleKey("-", SternResources.ZugeingabeInfo(true)));

 			do
 			{
 				this.game.console.appendText(
 						SternResources.ZugeingabeNeueBuendnisstruktur(true)+": ");

 				ConsoleInput input = this.game.console.waitForTextEntered(10, allowedKeys, !game.isMoveEnteringOpen(), true);

 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.game.console.outAbort();
 					return;
 				}

 				if (input.getInputText().toUpperCase().equals("-"))
 				{
 					this.printAlliance(planet, planetIndex);
 					this.game.console.waitForKeyPressed();
 					continue;
 				}
 				
 				try
 				{
 					Integer.parseInt(input.getInputText());
 				}
 				catch (Exception e)
 				{
 					this.game.console.outInvalidInput();
 					continue;
 				}

 				if (input.getInputText().indexOf('0') >= 0 && input.getInputText().length() > 1)
 				{
 					console.appendText(
 							SternResources.ZugeingabeBuendnis0NichtKombinieren(true));
 					console.lineBreak();
 					continue;
 				}

 				Planet planetCopy = (Planet)Utils.klon(planet);

 				if (input.getInputText().equals("0"))
 				{
 					if (!planet.isAllianceMember(this.playerIndexNow))
 					{
 						console.appendText(
 								SternResources.ZugeingabeKeinBuendnismitglied(true));
 						console.lineBreak();
 						break;
 					}
 					if (planet.getOwner() != this.playerIndexNow && planet.getFightersCount(this.playerIndexNow) > 0)
 					{
 						console.appendText(
 								SternResources.ZugeingabeZuerstKuendigen(true));
 						console.lineBreak();
 						break;
 					}
 					
 					planet.cancelAlliance(this.playerIndexNow);
 				}
 				else
 				{
 					if (this.playerIndexNow != planet.getOwner() && !planet.isAllianceMember(this.playerIndexNow))
 					{
 						console.appendText(
 								SternResources.ZugeingabeKeinBuendnismitglied(true));
 						console.lineBreak();
 						break;
 					}
 					
 					if (this.playerIndexNow != planet.getOwner())
 					{
 						console.appendText(
 								SternResources.ZugeingabeAufFremdenPlanetenNurKuendigen(true));
 						console.lineBreak();
 						continue;
 					}

 					BitSet bitSet = new BitSet(this.game.playersCount);
 					boolean error = false;

 					for (int i = 0; i < input.getInputText().length(); i++)
 					{
 						int playerIndex = Integer.parseInt(input.getInputText().substring(i,i+1)) - 1;

 						if (playerIndex < 0 || playerIndex >= this.game.playersCount || playerIndex == this.playerIndexNow)
 						{
 							error = true;
 							break;
 						}

 						bitSet.set(playerIndex);
 					}

 					if (error)
 					{
 						this.game.console.outInvalidInput();
 						continue;
 					}

 					for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 					{
 						if (bitSet.get(playerIndex))
 							planet.addPlayerToAlliance(this.game.playersCount, playerIndex);
 						else
 							planet.cancelAlliance(playerIndex);
 					}
 				}
 				
 				this.game.moves.get(this.playerIndexNow).add(
 						new Move(planetIndex, planetCopy, planet.getAllianceMembers()));

 				this.game.console.appendText(
 						SternResources.ZugeingabeStartErfolgreich(true));
 				this.game.console.lineBreak();

 				break;
 			} while (true);
 		}
 		
 		private void printAlliance(Planet planet, int planetIndex)
 		{
 			if (planet.allianceExists())
				{
					this.game.console.appendText(
							SternResources.ZugeingabeMomentaneBuendnisstruktur(true)+":");
					this.game.console.lineBreak();

					this.game.printAllianceInfo(planetIndex);
				}
				else
				{
					this.game.console.appendText(
							SternResources.ZugeingabeKeinBuendnis(true));
				}
 		}
 		
 		private void inventory()
 		{
 			new Inventory(this.game, this.playerIndexNow);
 		}
 		
 		private void hideShips()
 		{
 			this.game.console.setHeaderText(
 					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeObjekteAusblenden(true),
 					this.game.players[this.playerIndexNow].getColorIndex());

 			this.game.console.clear();
 			
 			do
 			{
	 			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
				
				allowedKeys.add(new ConsoleKey("ESC",SternResources.Zurueck(true)));
				allowedKeys.add(new ConsoleKey("-",SternResources.ZugeingabeObjekteAusblendenAlleAus(true)));
				allowedKeys.add(new ConsoleKey("+",SternResources.ZugeingabeObjekteAusblendenAlleAn(true)));
		 				
				for (int i = 1; i <= game.playersCount; i++)
				{
					String onOff = 
							game.shipsOfPlayerHidden.get(i-1) ?
									SternResources.ZugeingabeObjekteAusblendenAn(true) :
									SternResources.ZugeingabeObjekteAusblendenAus(true);
					allowedKeys.add(
							new ConsoleKey(
									Integer.toString(i), 
								game.players[i-1].getName() + " " + onOff));
				}
				
				ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, false);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					break;
				
				String key = input.getInputText().toLowerCase();
				boolean error = false;
				
				if (key.equals("+"))
					this.game.shipsOfPlayerHidden.clear();
				else if (key.equals("-"))
					this.game.shipsOfPlayerHidden.set(0, game.playersCount);
				else
				{
					try
					{
						int playerIndex = Integer.parseInt(key) - 1;
						
						if (playerIndex < 0 || playerIndex >= game.playersCount)
							error = true;
						else
							this.game.shipsOfPlayerHidden.flip(playerIndex);
					}
					catch (Exception x)
					{
						error = true;
					}
				}
				
				if (error)
					this.game.console.outInvalidInput();
				else
					this.game.updateBoard();
				
 			} while (true);
 			
 			this.game.console.clear();
 		}
 		
 		private boolean stoppedShips()
 		{
 			boolean quitEnterMoves = false;
 			
 			do
 			{
 				Ship ship = null;
 				
 				for (Ship ship2: this.game.ships)
 				{
 					if (ship2.getOwner() == this.playerIndexNow && ship2.isStopped())
 					{
 						ship = ship2;
 						break;
 					}
 				}
 				
 				if (ship == null)
 					break;
 				
 				ArrayList<Point> positionsMarked = new ArrayList<Point>();
 				positionsMarked.add(ship.getPositionStart());
 				this.game.updateBoard(positionsMarked, 0);
 				
 				ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
 				
 				allowedKeys.add(new ConsoleKey("-", SternResources.ZugeingabeUndo(true)));
 				
 				String shipDisplayName = "";
 				
 				switch (ship.getType())
 				{
 				case FIGHTERS:
 					shipDisplayName = SternResources.ZugeingabeWohinRaumer(true, Integer.toString(ship.getCount()));
 					break;
 				case SCOUT:
 					shipDisplayName = SternResources.ZugeingabeWohinAufklaerer(true);
 					break;
 				case TRANSPORT:
 					shipDisplayName = SternResources.ZugeingabeWohinTransporter(true);
 					break;
 				case PATROL:
 					shipDisplayName = SternResources.ZugeingabeWohinPatrouille(true);
 					break;
 				case MINE50:
 					shipDisplayName = SternResources.ZugeingabeWohin50erMine(true);
 					break;
 				case MINE100:
 					shipDisplayName = SternResources.ZugeingabeWohin100erMine(true);
 					break;
 				case MINE250:
 					shipDisplayName = SternResources.ZugeingabeWohin250erMine(true);
 					break;
 				case MINE500:
 					shipDisplayName = SternResources.ZugeingabeWohin500erMine(true);
 					break;
 				case MINESWEEPER:
 					shipDisplayName = SternResources.ZugeingabeWohinMinenraumer(true);
 					break;
				default:
					break;
 				}
 				
 				do
 				{
 					this.game.console.appendText(shipDisplayName + " ");
	 				
	 				ConsoleInput input = this.game.console.waitForTextEntered(Constants.PLANET_NAME_LENGTH_MAX, allowedKeys, !game.isMoveEnteringOpen(), false);
	 				
	 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
	 				{
	 					this.game.console.outInvalidInput();
	 					continue;
	 				}
	 				
	 				if (input.getInputText().toUpperCase().equals(("-")))
					{
	 					quitEnterMoves = this.undo();
	 					break;
					}
					
	 				int planetIndexDestination = this.game.getPlanetIndexFromName(input.getInputText());
	
					if (planetIndexDestination >= 0)
					{
						this.game.moves.get(this.playerIndexNow).add(
								new Move(ship, (UUID)Utils.klon(ship.getStopLabel()), planetIndexDestination));
						
						ship.setPlanetIndexDestination(planetIndexDestination);
						ship.setPositionDestination(this.game.planets[planetIndexDestination].getPosition());
						ship.setStopped(false);
						
						break;
					}
					else
						this.game.console.outInvalidInput();
				
 				} while (true);
 				
 				this.game.updateBoard();
 				
 				if (quitEnterMoves)
 					break;
 				
 			} while (true);
 			
 			return quitEnterMoves;
 		}
 		
 		private boolean undo()
 		{
 			ArrayList<Move> moves = this.game.moves.get(this.playerIndexNow);
 			
			if (moves.size() == 0)
			{
				if (this.game.soloPlayer)
				{
					this.game.console.appendText(SternResources.ZugeingabeKeineSpielzuege(true));
					this.game.console.lineBreak();
					return false;
				}
				else
				{
					this.game.console.appendText(SternResources.ZugeingabeKeineSpielzuegeAbbrechen(true) + " ");
					
					String input = this.game.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
					if (input.equals(Console.KEY_YES))
					{
						this.game.console.lineBreak();
						return true;
					}
					else
					{
						return false;
					}
				}
			}
			
			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> " + SternResources.ZugeingabeUndo(true),
					this.game.players[this.playerIndexNow].getColorIndex());
			
			this.game.console.appendText(SternResources.ZugeingabeUndoFrage(true)+" ");
			
			String input = this.game.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (input.equals(Console.KEY_YES))
			{
				Move move = moves.get(moves.size()-1);
				
				if (move.getStopLabel() != null)
				{
					move.getShip().setStopLabel(move.getStopLabel());
					move.getShip().setPositionDestination(move.getShip().getPositionStart());
					move.getShip().setPlanetIndexDestination(move.getShip().getPlanetIndexStart());
				}
				else
				{
					if (move.getPlanetBefore() != null)
						this.game.planets[move.getPlanetIndex()] = move.getPlanetBefore();
					
					if (move.getShip() != null)
					{
						if (move.getShip().getType() == ShipType.CAPITULATION)
							this.capitulated = false;
						else
							this.game.ships.remove(move.getShip());
					}
				}
				
				moves.remove(moves.size()-1);
				
				this.game.console.appendText(SternResources.ZugeingabeUndoErfolg(true));
				this.game.console.lineBreak();
			}
			else
				this.game.console.outAbort();
			
			return false;
 		}
 		
 		private void planetEditor()
 		{
			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> " + SternResources.ZugeingabePlanet(true),
					this.game.players[this.playerIndexNow].getColorIndex());

			this.game.console.clear();
			
			int planetIndex = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabePlanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndex = input.planetIndex;

				if (this.game.planets[planetIndex].getOwner() == this.playerIndexNow ||
					this.game.planets[planetIndex].hasRadioStation(this.playerIndexNow))
					break;
				else
				{
					this.game.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
					this.game.console.lineBreak();
				}

			} while (true);

			if (planetIndex < 0)
				return;

			new PlanetEditor(
					this.game,
					planetIndex,
					this.game.moves.get(this.playerIndexNow),
					this.game.planets[planetIndex].getOwner() != this.playerIndexNow);
 		}
 		 		
 		private void planetInfo()
 		{
			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true) +" " + this.game.players[this.playerIndexNow].getName() + " -> "+ SternResources.ZugeingabePlaneteninfo(true),
					this.game.players[this.playerIndexNow].getColorIndex());

			this.game.console.clear();
			
			int planetIndex = -1;

			do
			{
				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.ZugeingabePlanet(true), 
						!game.isMoveEnteringOpen(), 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					return;
				}
				
				planetIndex = input.planetIndex;

				if (this.game.planets[planetIndex].getOwner() == this.playerIndexNow)
					break;
				else
				{
					this.game.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
					this.game.console.lineBreak();
				}

			} while (true);

			game.console.appendText(
					SternResources.ZugeingabePlaneteninfo2(
							true, 
							Integer.toString(this.game.planets[planetIndex].getFighterProduction())));
			
			game.console.waitForKeyPressed();
			game.console.clear();
 		}

 		
 		private boolean finish()
 		{
			this.game.console.setHeaderText(
			this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Zugeingabe(true)+" " + this.game.players[this.playerIndexNow].getName() + " -> "+SternResources.ZugeingabeBeenden(true),
			this.game.players[this.playerIndexNow].getColorIndex());
			
			this.game.console.appendText(SternResources.ZugeingabeBeendenFrage(true));
			this.game.console.lineBreak();
			
			String input = this.game.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (!input.equals(Console.KEY_YES))
			{
				this.game.console.outAbort();
				return false;
			}
			
			return true;
 		}
 	}
 	
 	// =============================
 	
 	private class PlanetInputStruct
 	{
 		public static final int ALLOWED_INPUT_PLANET = 1;
 		public static final int ALLOWED_INPUT_SECTOR = 2;
 		
 		public int planetIndex = Constants.NO_PLANET;
 		public Point sector = null;
 		
 		public PlanetInputStruct(int planetIndex)
 		{
 			this.planetIndex = planetIndex;
 		}
 		
 		public PlanetInputStruct(Point position, int planetIndex)
 		{
 			this.sector = position;
 			this.planetIndex = planetIndex;
 		}
 	}
 	
 	// =============================
 	private class PlanetEditor
 	{
 		private ArrayList<ShipType> itemSequence;
 		private Game game;
 		private boolean readOnly;
 		
 		private PlanetEditor(
 				Game game,
 				int planetIndex,
 				ArrayList<Move> moves,
 		 		boolean readOnly)
 		{
 			this.readOnly = readOnly;
 			
 			this.game = game;
 			
 			this.itemSequence = new ArrayList<ShipType>();
 			
 			this.itemSequence.add(ShipType.MONEY_PRODUCTION);
 			this.itemSequence.add(ShipType.FIGHTER_PRODUCTION);
 			this.itemSequence.add(ShipType.DEFENCE_SHIELD);
 			this.itemSequence.add(ShipType.DEFENCE_SHIELD_REPAIR);
			
 			this.itemSequence.add(ShipType.SCOUT);
 			this.itemSequence.add(ShipType.TRANSPORT);
 			this.itemSequence.add(ShipType.PATROL);
 			this.itemSequence.add(ShipType.MINESWEEPER);
			
 			this.itemSequence.add(ShipType.MINE50);
 			this.itemSequence.add(ShipType.MINE100);
 			this.itemSequence.add(ShipType.MINE250);
 			this.itemSequence.add(ShipType.MINE500);

			int lineIndex = 1;
			
			Planet planet = (Planet)Utils.klon(game.planets[planetIndex]);
			
			game.console.clear();
			game.console.setMode(Console.ConsoleModus.PLANET_EDITOR);
			this.updateDisplay(planet, lineIndex, false);
			game.setScreenContentMode(ScreenContent.MODE_PLANET_EDITOR);
			
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			
			if (this.readOnly)
				allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
			else
			{
				allowedKeys.add(new ConsoleKey(SternResources.PlaneteneditorAuf(true),SternResources.PlaneteneditorAuswahlAendern(true)));
				allowedKeys.add(new ConsoleKey(SternResources.PlaneteneditorAb(true),SternResources.PlaneteneditorAuswahlAendern(true)));
				allowedKeys.add(new ConsoleKey(SternResources.Rechts(true),SternResources.PlaneteneditorKaufen(true)));
				allowedKeys.add(new ConsoleKey(SternResources.Links(true),SternResources.PlaneteneditorVerkaufen(true)));
				allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
				allowedKeys.add(new ConsoleKey("ENTER",SternResources.PlaneteneditorUebernehmen(true)));
			}
			
			boolean takeOver = false;
			
			do
			{
				ConsoleInput input = game.console.waitForKeyPressed(allowedKeys, false);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					takeOver = false;
					break;
				}
				
				if (this.readOnly)
					continue;
				
				if (input.getLastKeyCode() == KeyEvent.VK_ENTER)
				{
					takeOver = !this.readOnly ;
					break;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_DOWN ||
					input.getInputText().equals("2"))
				{
					lineIndex++;
					if (lineIndex >= itemSequence.size())
						lineIndex = 0;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_UP  ||
						input.getInputText().equals("8"))
				{
					lineIndex--;
					if (lineIndex < 0)
						lineIndex = itemSequence.size() - 1;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_RIGHT  ||
						input.getInputText().equals("6"))
					this.buySell(true, planetIndex, planet, itemSequence.get(lineIndex));
				
				if (input.getLastKeyCode() == KeyEvent.VK_LEFT  ||
						input.getInputText().equals("4"))
					this.buySell(false, planetIndex, planet, itemSequence.get(lineIndex));
	
				this.updateDisplay(planet, lineIndex, false);
				
			} while (true);
			
			game.console.clear();
			game.console.setMode(Console.ConsoleModus.TEXT_INPUT);
			game.setScreenContentMode(ScreenContent.MODE_BOARD);
			
			if (takeOver)
			{
				moves.add(new Move(planetIndex, game.planets[planetIndex], planet));
				game.planets[planetIndex] = planet;

				game.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			}
			else
				game.console.appendText(SternResources.AktionAbgebrochen(true));
			
			game.console.lineBreak();
 		}
 		
 		private void buySell(
 				boolean buy,
 				int planetIndex,
 				Planet planet,
 				ShipType itemType)

 		{
 			if (itemType == ShipType.MONEY_PRODUCTION)
 			{
 				if (buy)
 					planet.buyMoneyProduction(Game.editorPricesBuy.get(ShipType.MONEY_PRODUCTION));
 			}
 			else if (itemType == ShipType.FIGHTER_PRODUCTION)
 			{
 				if (buy)
 					planet.incrementFighterProduction();
 				else
 					planet.decrementFighterProduction();
 			}
 			else if (itemType == ShipType.DEFENCE_SHIELD)
 			{
 				if (!game.options.contains(GameOptions.DEFENCE_SHIELDS))
 					return;
 				
 				if (buy)
 					planet.buyDefenceShield(Game.editorPricesBuy.get(itemType));				
 				else
 					planet.sellDefenceShield(Utils.round((double)(
 							Game.editorPricesSell.get(itemType) * planet.getDefenceShieldStateFactor())));
 			}
 			else if (itemType == ShipType.DEFENCE_SHIELD_REPAIR)
 			{
 				if (buy && planet.getDefenceShieldFactor() > 0 && !planet.isDefenceShieldComplete())
 					planet.repairDefenceShield(Constants.PRICE_DEFENSE_SHIELD_REPAIR * planet.getDefenceShieldFactor());
 			}
 			else
 			{
 				if (buy)
 					planet.buyShip(itemType, 1, Game.editorPricesBuy.get(itemType));
 				else
 					planet.sellShip(itemType, Game.editorPricesSell.get(itemType));
 			}
 		}
 		
 		private void updateDisplay (Planet planet, int lineIndex, boolean readOnly)
 		{
 			Hashtable<ShipType,String> ships = new Hashtable<ShipType,String>();
 			HashSet<ShipType> buyImpossible = new HashSet<ShipType>();
 			HashSet<ShipType> sellImpossible = new HashSet<ShipType>();
 			
 			for (ShipType itemType: this.itemSequence)
 			{
 				int moneySupply = planet.getMoneySupply();
 				
 				String countString = "";
 				int count = 0;
 				
 				if (itemType == ShipType.MONEY_PRODUCTION)
 				{
 					count = planet.getMoneyProduction();
 					countString = Integer.toString(count);
 				}
 				else if (itemType == ShipType.FIGHTER_PRODUCTION)
 				{
 					count = planet.getFighterProduction();
 					countString = Integer.toString(count);
 				}
 				else if (itemType == ShipType.DEFENCE_SHIELD)
 				{
 					count = planet.getDefenceShieldFactor();
 					countString = Utils.convertToString(count);
 				}
 				else if (itemType == ShipType.DEFENCE_SHIELD_REPAIR)
 				{
 					count = planet.getDefenceShieldFightersCount();
 					countString = Utils.convertToString(count);
 				}
 				else
 				{
 					count = planet.getShipsCount(itemType);
 					countString = Utils.convertToString(count);
 				}
 				
 				ships.put(itemType, countString);
 				
 				if (itemType == ShipType.DEFENCE_SHIELD_REPAIR)
 				{
 					if (planet.getDefenceShieldFactor() == 0 || (planet.getDefenceShieldFactor() > 0 && planet.isDefenceShieldComplete()))
 						buyImpossible.add(itemType);
 				}
 				else if (itemType == ShipType.MONEY_PRODUCTION && planet.getMoneyProduction() >= Constants.MONEY_PRODUCTION_MAX)
 					buyImpossible.add(itemType);
 				else if (itemType == ShipType.DEFENCE_SHIELD && (!game.options.contains(GameOptions.DEFENCE_SHIELDS) || count >= Constants.DEFENSE_SHIELDS_COUNT_MAX))
 					buyImpossible.add(itemType);
 				else if (Game.editorPricesBuy.get(itemType) > moneySupply)
 					buyImpossible.add(itemType);
 				
 				if (count < 1)
 					sellImpossible.add(itemType);
 			}
 			
 			if (this.game.screenContent == null)
 				this.game.screenContent = new ScreenContent();
 			
 			byte colorIndex = Colors.WHITE;
 			
 			this.game.screenContent.setPlanetEditor(
 					new ScreenContentPlanetEditor(
 						this.itemSequence.get(lineIndex),
 						ships,
 						buyImpossible,
 						sellImpossible,
 						colorIndex,
 						planet.getMoneySupply(),
 						planet.hasCommandRoom(),
 						this.readOnly));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);
 		}
 	}
 	
 // =============================
 	private class Statistics
 	{ 		
 		private Game game;
 		
 		private int yearMarked;
 		private char mode;
	 		
 		private Statistics(
 				Game game)
 		{
 			this.game = game;
 			
 			this.yearMarked = game.year;
 			
 			this.mode = Constants.STATISTICS_MODE_SCORE;
			
			game.console.clear();
			game.console.setMode(Console.ConsoleModus.STATISTICS);
			this.updateDisplay();
			game.setScreenContentMode(ScreenContent.MODE_STATISTICS);
			
			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
			
			allowedKeys.add(new ConsoleKey(SternResources.Links(true),SternResources.StatistikJahrMinus(true)));
			allowedKeys.add(new ConsoleKey(SternResources.Rechts(true),SternResources.StatistikJahrPlus(true)));
			allowedKeys.add(new ConsoleKey(Character.toString(Constants.STATISTICS_MODE_SCORE),SternResources.Punkte(true)));
			allowedKeys.add(new ConsoleKey(Character.toString(Constants.STATISTICS_MODE_FIGHTERS),SternResources.Raumer(true)));
			allowedKeys.add(new ConsoleKey(Character.toString(Constants.STATISTICS_MODE_PLANETS),SternResources.Planeten(true)));
			allowedKeys.add(new ConsoleKey(Character.toString(Constants.STATISTICS_MODE_MONEY_PRODUCTION),SternResources.Energieproduktion(true)));

			allowedKeys.add(new ConsoleKey("ESC",SternResources.StatistikSchliessen(true)));
			
			do
			{
				ConsoleInput input = game.console.waitForKeyPressed(allowedKeys, false);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					break;
				}
				
				else if (input.getLastKeyCode() == KeyEvent.VK_LEFT && this.yearMarked > 0)
				{
					this.yearMarked--;
				}
				
				else if (input.getLastKeyCode() == KeyEvent.VK_RIGHT && this.yearMarked < this.game.year)
				{
					this.yearMarked++;
				}
				else
				{
					char c = ' ';
					
					try
					{
						c = input.getInputText().toUpperCase().charAt(0);
					}
					catch (Exception x)
					{}
					
					if (c == Constants.STATISTICS_MODE_SCORE ||
					    c == Constants.STATISTICS_MODE_FIGHTERS ||
					    c == Constants.STATISTICS_MODE_PLANETS ||
					    c == Constants.STATISTICS_MODE_MONEY_PRODUCTION)

						this.mode = c;
				}
				
				this.updateDisplay();
				
			} while (true);
			
			game.console.clear();
			game.console.setMode(Console.ConsoleModus.TEXT_INPUT);
			game.setScreenContentMode(ScreenContent.MODE_BOARD);
			
			game.console.lineBreak();
 		}
 		
 		private void updateDisplay()
 		{
 			int[][] values = new int[this.game.year + 1][this.game.playersCount];
 			int[][] championsByYear = new int[this.game.year + 1][];
 			
 			int valueMax = 0;
 			int yearValueMax = 0;
 			int playerValueMax = 0;
 			int valueMin = 0;
 			int yearValueMin = 0;
 			int playerValueMin = 0;
 			
 			boolean start = true;
 			
 			ArrayList<Integer> years = new ArrayList<Integer>(this.game.archive.keySet());
 			Collections.sort(years);
 			
 			int counter = 0;
 			for (int i = 0; i < years.size(); i++)
 			{
 				int year = years.get(i);
 				
 				Archive archive = this.game.archive.get(year);
 				int valueBest = 0;
 				
 				for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 				{
 					int value = 0;
 					
	 				switch (this.mode)
	 				{
	 				case Constants.STATISTICS_MODE_SCORE:
	 					value = archive.getScore()[playerIndex];
	 					break;
	 					
	 				case Constants.STATISTICS_MODE_FIGHTERS:
	 					value = archive.getFighters()[playerIndex];
	 					break;
	 					
	 				case Constants.STATISTICS_MODE_PLANETS:
	 					value = archive.getPlanetsCount()[playerIndex];
	 					break;
	 					
	 				case Constants.STATISTICS_MODE_MONEY_PRODUCTION:
	 					value = archive.getMoneyProduction()[playerIndex];
	 					break;
	 				}
	 				
	 				if (value <= valueMin || start)
	 				{
	 					valueMin = value;
	 					playerValueMin = playerIndex;
	 					yearValueMin = year;
	 				}
	 				if (value >= valueMax || start)
	 				{
	 					valueMax = value;
	 					playerValueMax = playerIndex;
	 					yearValueMax = year;
	 				}
	 				
	 				start = false;
	 				
 					values[counter][playerIndex] = value;
 					
 					if (value > valueBest)
 					{
 						valueBest = value;
 					}
 				}
 				
 				int[] champions = Utils.sortValues(values[counter], true);
 				int championsCount = 1;
 				
 				for (int t = 1; t < champions.length; t++)
 				{
 					if (values[counter][champions[t]] < values[counter][champions[0]])
 						break;
 					
 					championsCount++;
 				}
 				
 				championsByYear[year] = new int[championsCount];
 				
 				int c = 0;
 				for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
 				{
 					if (values[counter][champions[playerIndex]] == values[counter][champions[0]])
 					{
 						championsByYear[year][c] = champions[playerIndex];
 						c++;
 					}
 				}
 				
 				counter++;
 			} 			
 			
 			String title = Character.toString(this.mode);
 			
 			if (this.game.screenContent == null)
 				this.game.screenContent = new ScreenContent();
 			
 			this.game.screenContent.setStatistik(
 				new ScreenContentStatistics(
 						this.game.dateStart,
 						title,
 						(Player[])Utils.klon(this.game.players),
 						values,
 						championsByYear,
 						this.game.year + 1,
 						valueMax,
 						yearValueMax,
 						playerValueMax,
 						valueMin,
 						yearValueMin,
 						playerValueMin,
 						this.yearMarked,
 						this.mode == Constants.STATISTICS_MODE_SCORE));
 			 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);
 		}	
 	}
 	
 // =============================
  	private class Evaluation
  	{
  		private Game game;
  		private ArrayList<ScreenContent> replay = new ArrayList<ScreenContent>();
  		
  		// First key: Hashcode of the sighted ship
  		// Second key: Hashcode of the patrol
  		// Values: Events
  		private Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> events = new Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>>(); 
  		
  		@SuppressWarnings("unchecked")
		private Evaluation(Game game)
  		{
  			this.game = game;
  			
			this.replay = new ArrayList<ScreenContent>();
			
			this.game.console.setMode(Console.ConsoleModus.EVALUATION);
			
			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Auswertung(true),
					Colors.NEUTRAL);
			
			this.game.console.clear();
			this.printDayBeginOfYear();
			this.game.console.appendText(
					SternResources.AuswertungBeginnt(true));
			this.waitForKeyPressed();
			
			this.processMoves();
			
			for (Planet planet: this.game.planets)
			{
				planet.produceMoneySupply();
				planet.produceFighters();
			}
			
			for (Ship ship: this.game.ships)
			{
				ship.resetStartedRecently();
			}
			
			this.game.updatePlanets(false);
			this.game.updateBoard();
			
			this.addScreenSnapshotToReplay(0);
			
			this.cleanAlliances();
			
			this.processCapitulations();
			
			for (int day = 0; day <= Constants.DAYS_OF_YEAR_COUNT; day++)
			{
				if (day > 0)
				{
					int shipsSequence[] = Utils.getRandomList(this.game.ships.size());
					
					for (int i = 0; i < shipsSequence.length; i ++)
					{
						this.moveShip(this.game.ships.get(shipsSequence[i]), day);
					}
				}
				
				this.patrolsDoWatch(day);
				
				for (int i = this.game.ships.size() - 1; i >= 0; i--)
				{
					Ship ships = this.game.ships.get(i);
					
					if (ships.isToBeDeleted())
					{
						this.game.ships.remove(i);
					}
				}
			}

			this.game.updateBoard(Constants.DAYS_OF_YEAR_COUNT);
			this.game.updatePlanets(false);
			
			for (Ship ships: this.game.ships)
			{
				if (ships.isToBeTurned())
				{
					ships.turn();
				}
				else if (!ships.isStopped())
				{
					ships.incrementYearCount();
				}
			}
			
			this.checkIfPlayerIsDead();

			this.game.console.setLineColor(Colors.WHITE);
			this.printDayEndOfYear();
			this.game.console.appendText(SternResources.AuswertungEnde(true));

			this.waitForKeyPressed();
			
			this.game.console.enableEvaluationProgressBar(false);
			this.game.console.setMode(Console.ConsoleModus.TEXT_INPUT);
			
			this.reducePatrolEvents(this.events);
			
			this.game.replayLast = (ArrayList<ScreenContent>) Utils.klon(this.replay);
			
			this.game.year++;
			this.game.calculateScores();
			this.game.prepareYear();			
  		}
  		
  		private void processMoves()
  		{
  			int[] playersSequence = Utils.getRandomList(this.game.playersCount);
  			
  			for (int i = 0; i < this.game.playersCount; i++)
  			{
  				int playerIndex = playersSequence[i];
  				ArrayList<Move> movesOfPlayer = moves.get(playerIndex);
  				
  				if (movesOfPlayer == null)
  					continue;
  				
  				for (Move move: movesOfPlayer)
  				{
  					if (move.getStopLabel() != null)
  		  			{
  		  				Ship ship = null;
  						
  						for (Ship ship2: this.game.ships)
  						{
  							if (ship2.getStopLabel() != null && ship2.getStopLabel().equals(move.getStopLabel()))
  							{
  								ship = ship2;
  								break;
  							}
  						}
  						
  						ship.setPositionStart(ship.getPositionDestination());
  						ship.setPlanetIndexStart(Constants.NO_PLANET);
  						ship.setPlanetIndexDestination(move.getPlanetIndex());
  						ship.setPositionDestination(this.game.planets[move.getPlanetIndex()].getPosition());
  						ship.setStopped(false);
  		  			}
  		  			else if (move.getShip() != null)
  		  			{
  		  				Ship ship = move.getShip();
  		  				Planet planet = this.game.planets[ship.getPlanetIndexStart()];
  		  				
  		  				if (ship.getType() == ShipType.FIGHTERS)
  		  				{
  		  					if (ship.isAlliance())
  		  					{
  		  						boolean ok = true;
  		  						
  		  						if (planet.getAllianceMembers() == null || planet.getAllianceMembers().length <= 1)
  		  							ok = false;
  		  						else if (!(planet.getFightersCount(playerIndex) > 0  &&
  		  							planet.getShipsCount(ShipType.FIGHTERS) >= ship.getCount()))
  		  							ok = false;
  		  						
  		  						if (ok)
  		  						{
	  								int[] reductions = 
	  										planet.subtractFightersCount(this.game.playersCount, ship.getCount(), playerIndex, true);

	  								Alliance alliance = planet.copyAllianceStructure(reductions);
	  								if (alliance == null)
	  									ok = false;
	  								else
	  									ship.setAlliance(alliance);
  		  						}
  		  						
  		  						if (!ok)
  		  						{
  		  							this.game.updateBoard(this.game.getSimpleFrameObjekt(ship.getPlanetIndexStart(), Colors.WHITE), 0);
  		  							this.game.console.setLineColor(this.game.players[ship.getOwner()].getColorIndex());
  		  							this.game.console.appendText(
  		  								SternResources.AuswertungRaumerNichtGestartet(true,
  		  											this.game.players[ship.getOwner()].getName(),
  		  											this.game.getPlanetNameFromIndex(ship.getPlanetIndexStart())));
  		  	  	 	 				this.waitForKeyPressed();
  		  	  	 	 				continue;
  		  						}
  		  					}
  		  					else
  		  					{
  		  						if (planet.getFightersCount(ship.getOwner()) < ship.getCount())
  		  						{
  		  							this.game.updateBoard(this.game.getSimpleFrameObjekt(ship.getPlanetIndexStart(), Colors.WHITE), 0);
  		  							this.game.console.setLineColor(this.game.players[ship.getOwner()].getColorIndex());
  		  							this.game.console.appendText(
		  								SternResources.AuswertungRaumerNichtGestartet(true,
		  											this.game.players[ship.getOwner()].getName(),
		  											this.game.getPlanetNameFromIndex(ship.getPlanetIndexStart())));
  		  	  	 	 				this.waitForKeyPressed();
  		  	  	 	 				
  		  							continue;
  		  						}
  		  						
  		  						if (planet.isAllianceMember(playerIndex))
  		  							planet.subtractFightersCount(this.game.playersCount, ship.getCount(), playerIndex, false);
  		  						else
  		  							planet.decrementShipsCount(ShipType.FIGHTERS, ship.getCount());
  		  					}

  		  					this.game.ships.add(ship);
  		  				}
  		  				else
  		  				{
  		  					if (ship.getType() == ShipType.TRANSPORT)
  		  					{
  		  						planet.subtractMoneySupply(ship.getCount());
  		  						if (ship.getCommandRoom() != null)
  		  							planet.removeCommandRoom();
  		  					}
  		  					
  		  					planet.decrementShipsCount(ship.getType(), 1);
  		  					this.game.ships.add(ship);  					
  		  				}
  		  			}
  		  			else if (move.getAllianceMembers() != null)
  		  			{
  		  				if (!this.game.planets[move.getPlanetIndex()].acceptAllianceChange(playerIndex, move.getAllianceMembers()))
  		  				{
  		  					this.game.updateBoard(this.game.getSimpleFrameObjekt(playerIndex, Colors.WHITE), 0);
  							this.game.console.setLineColor(this.game.players[playerIndex].getColorIndex());
  							
  							this.game.console.appendText(
								SternResources.AuswertungBuendnisNichtGeaendert(true,
										this.game.players[playerIndex].getName(),
										this.game.getPlanetNameFromIndex(playerIndex)));
  		 	 				this.waitForKeyPressed();
  		  				}
  		  			}
  		  			else if (move.getPlanetAfter() != null)
  		  			{
  		  				this.game.planets[move.getPlanetIndex()].acceptPlanetDataChange(move.getPlanetAfter());
  		  			}
  				}
  			}
  			
  			this.game.moves = new Hashtable<Integer, ArrayList<Move>>();
  		}
  		
  		private void cleanAlliances()
  		{
  			for (int planetIndex = 0; planetIndex < this.game.planetsCount; planetIndex++)
  			{
  				Planet planet = this.game.planets[planetIndex];
  				int[] reductions = planet.correctAlliance(this.game.playersCount);
  				
  				for (int playerIndex = 0; playerIndex < reductions.length; playerIndex++)
  				{
  					if (playerIndex != planet.getOwner() && reductions[playerIndex] > 0)
  					{
  						Ship obj = new Ship(
  								Constants.NO_PLANET,
  								Constants.NO_PLANET,
  								this.game.planets[planetIndex].getPosition(),
  								this.game.planets[planetIndex].getPosition(),
  								ShipType.FIGHTERS,
  								reductions[playerIndex],
  								playerIndex,
  								false,
  								false,
  								null,
  								null);
  						
  						obj.setStopped(true);

  						this.game.ships.add(obj);
  						
  						this.game.updateBoard(this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE), 0);
  						
  	 	 				this.game.console.setLineColor(this.game.players[playerIndex].getColorIndex());
  	 	 				
  	 	 				this.game.console.appendText(
  	 	 						SternResources.AuswertungRaumerVertrieben(true,
  	 	 								Integer.toString(obj.getCount()),
  	 	 								this.game.players[playerIndex].getName(),
  	 	 								this.game.getPlanetNameFromIndex(planetIndex)));
  	 	 				this.game.console.lineBreak();
  	 	 				this.game.console.appendText(
  	 	 						SternResources.AuswertungRaumerVertrieben2(true));
  	 	 				this.waitForKeyPressed();
  					}
  				}
  			}
  		}
  		
  		private void processCapitulations()
  		{
  			int[] shipsSequence = Utils.getRandomList(game.ships.size());
  			
  			for (int i = 0; i < game.ships.size(); i++)
  			{
  				Ship ship = game.ships.get(shipsSequence[i]);
  				
  				if (!ship.isToBeDeleted() && ship.getType() == ShipType.CAPITULATION)
  				{
  					this.commandRoomConquered(
							ship.getOwner(), 
							Constants.NEUTRAL, 
							true,
							0);
					ship.setToBeDeleted();
  				}
  			}
  		}
  		
  		private void checkIfPlayerIsDead()
  		{
  			for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
  			{
  				if (this.game.players[playerIndex].isDead())
  					continue;
  				
  				boolean playerHasPlanet = false;
  				for (Planet planet: game.planets)
  				{
  					if (planet.getOwner() == playerIndex)
  					{
  						playerHasPlanet = true;
  						break;
  					}
  				}
  				if (playerHasPlanet)
  					continue;
  				
  				boolean playerHasShips = false;
  				
  				for (Ship ships: game.ships)
  				{
  					if (ships.getFightersCount(playerIndex) > 0)
  					{
  						playerHasShips = true;
  						break;
  					}
  				}
  				
  				if (playerHasShips)
  					continue;
  				
  				boolean playerHasCommandRoom = false;
  				
  				if (!this.game.options.contains(GameOptions.SIMPLE))
  				{
	  				for (Planet planet: this.game.planets)
	  				{
	  					if (planet.getOwner() == playerIndex)
	  					{	  						
	  						if (planet.hasCommandRoom())
	  							playerHasCommandRoom = true;
	  					}
	  				}
	  				
	  				for (Ship ship: this.game.ships)
	  				{
	  					if (ship.getCommandRoom() != null && ship.getCommandRoom().getOwner() == playerIndex)
	  						playerHasCommandRoom = true;
	  				}
  				}

  				if (!playerHasCommandRoom)
  				{
  					this.game.players[playerIndex].setDead(true);

  					this.game.console.setLineColor(this.game.players[playerIndex].getColorIndex());
  	 				this.printDayEndOfYear();
  	 				this.game.console.appendText(
  	 						SternResources.AuswertungSpielerTot(
  	 								true, this.game.players[playerIndex].getName()));
  	 				this.waitForKeyPressed();
  				}
  			}
  		}
  		
  		private void waitForKeyPressed()
  		{
  			this.game.console.waitForKeyPressed();
  			
  			ScreenContent cont = (ScreenContent)Utils.klon(this.game.screenContent);
  			cont.setPlanetEditor(null);
			this.replay.add(cont);
  		}
  		
  		private void pause(int milliseconds)
  		{
  			this.game.console.pause(milliseconds);
  			
  			ScreenContent cont = (ScreenContent)Utils.klon(this.game.screenContent);
  			cont.setPlanetEditor(null);
  			cont.setPause(true);
  			cont.getConsole().clearKeys();
			this.replay.add(cont);
  		}
  		
  		private void addScreenSnapshotToReplay(int day)
  		{
			this.game.updateBoard(day);
  			ScreenContent cont = (ScreenContent)Utils.klon(this.game.screenContent);
  			cont.setPlanetEditor(null);
  			cont.setSnapshot();
  			cont.getConsole().clearKeys();
  			cont.getConsole().setProgressBarDay(day);
			this.replay.add(cont);
  		}
  		
  		private void printDayEvent(int day)
  		{
  			if (day < 1)
  				printDayBeginOfYear();
  			else if (day >= Constants.DAYS_OF_YEAR_COUNT)
  				printDayEndOfYear();
  			else
  			{		
  				this.game.console.appendText(">>> ");
  				this.game.console.setEvaluationProgressBarDay(day);
  			}
  		}
  		
  		private void printDayBeginOfYear()
  		{
			this.game.console.appendText(">>> ");
			this.game.console.enableEvaluationProgressBar(true);
  		}
  		
  		private void printDayEndOfYear()
  		{
  			this.game.console.appendText(">>> ");
			this.game.console.setEvaluationProgressBarDay(Constants.DAYS_OF_YEAR_COUNT);
  		}
  		
  		private void patrolsDoWatch(int day)
  		{
  			ArrayList<Integer> patrolIndices = new ArrayList<Integer>();
  			
  			for (int shipIndex = 0; shipIndex < this.game.ships.size(); shipIndex++)
  			{
  				Ship ship = this.game.ships.get(shipIndex);
  				
  				if (!ship.isToBeDeleted() && 
  					 ship.getType() == ShipType.PATROL &&
  					!ship.isTransfer())
  				{
  					patrolIndices.add(shipIndex);
  				}
  			}
  			
  			if (patrolIndices.size() == 0)
  			{
  				return;
  			}
  			
  			int patrolsSequence[] = Utils.getRandomList(patrolIndices.size());
  			int shipsSequence[] = Utils.getRandomList(this.game.ships.size());
  			
  			for (int i = 0; i < patrolIndices.size(); i++)
  			{
  				Ship patrol = this.game.ships.get(patrolIndices.get(patrolsSequence[i]));
  				
  				if (patrol.isToBeDeleted())
  				{
  					continue;
  				}
  				
  				Point patrolPosition = patrol.getPositionOnDay(day);
  				
  				for (int j = 0; j <  this.game.ships.size(); j++)
  				{
  					Ship otherShip = this.game.ships.get(shipsSequence[j]);
  					
  					if (otherShip == patrol || otherShip.isToBeDeleted())
  					{
  						continue;
  					}
  					  					
  					Point otherShipPosition = otherShip.getPositionOnDay(day);
  					
  					if (patrolPosition.distance(otherShipPosition) > Constants.PATROL_RADAR_RANGE + Constants.PRECISION)
					{
						continue;
					}
  					
  					this.patrolEvent(
  							patrol, 
  							otherShip, 
  							day);
  					
  					if (patrol.isToBeDeleted())
  					{
  						break;
  					}
  				}
  			}
  		}
  		
  		private void patrolEvent(
  				Ship patrol1, 
  				Ship otherShip1, 
  				int day)
  		{
			if (patrol1.isToBeDeleted() || otherShip1.isToBeDeleted())
				return;
			
			if (patrol1.getOwner() == otherShip1.getOwner())
				return;
			
			if (otherShip1.getType() == ShipType.FIGHTERS &&
				otherShip1.getPlanetIndexDestination() >= 0 &&
				this.game.planets[otherShip1.getPlanetIndexDestination()].isAllianceMember(otherShip1.getOwner()))
			{
				return;
			}
			
			if (otherShip1.isPlayerInvolved(patrol1.getOwner()))
			{
				return;
			}
			
			boolean swapObjects = false;
			
			if (otherShip1.getType() == ShipType.PATROL &&
			   !otherShip1.isTransfer())
			{
				swapObjects = Utils.getRandomInteger(100) < 50;
			}
			
			Ship patrol = swapObjects ? otherShip1 : patrol1;
			Ship otherShip = swapObjects ? patrol1 : otherShip1;
			
			this.printDayEvent(day);			
			this.game.console.setLineColor(this.game.players[patrol.getOwner()].getColorIndex());
			
			Point patrolPosition = patrol.getPositionOnDay(day);
			Point otherShipPosition = otherShip.getPositionOnDay(day);
			
			ScreenContentBoardRadar radar = 
					new ScreenContentBoardRadar(
							patrolPosition,
							this.game.players[patrol.getOwner()].getColorIndex());
			
			String patrolOwnerName = this.game.players[patrol.getOwner()].getName();
			String otherShipOwnerName = this.game.players[otherShip.getOwner()].getName();
			String otherShipDestinationName = this.game.getSectorNameFromPosition(otherShip.getPositionDestination());
			
			if (otherShip.getType() == ShipType.PATROL &&
				!otherShip.isTransfer())
			{
				this.game.updateBoard(
						null,
						this.game.getSimpleMarkedPosition(otherShipPosition),
						radar,
						day);
				
				this.game.console.setLineColor(this.game.players[patrol.getOwner()].getColorIndex());
				this.game.console.appendText(
						SternResources.AuswertungPatrouillePatrouilleZerstoert(
								true, patrolOwnerName, otherShipOwnerName));
				
				this.waitForKeyPressed();
				otherShip.setToBeDeleted();
			}
			else
			{
				this.game.updateBoard(
						null,
						this.game.getSimpleMarkedPosition(otherShipPosition),
						radar,
						day);

				boolean capture = true;
				this.game.console.setLineColor(this.game.players[patrol.getOwner()].getColorIndex());
				
				if (otherShip.getType() == ShipType.SCOUT)
					this.game.console.appendText(
									SternResources.AuswertungPatrouilleAufklaererGekapert(true, patrolOwnerName, otherShipDestinationName, otherShipOwnerName));
				else if (otherShip.getType() == ShipType.TRANSPORT)
					this.game.console.appendText(
									SternResources.AuswertungPatrouilleTransporterGekapert(true, patrolOwnerName, otherShipDestinationName, otherShipOwnerName));
				else if (otherShip.getType() == ShipType.MINESWEEPER)
					this.game.console.appendText(
									SternResources.AuswertungPatrouilleMinenraeumerGekapert(true, patrolOwnerName, otherShipDestinationName, otherShipOwnerName));
				else if (otherShip.getType() == ShipType.MINE50 || 
						 otherShip.getType() == ShipType.MINE100 ||
						 otherShip.getType() == ShipType.MINE250 ||
						 otherShip.getType() == ShipType.MINE500)
					this.game.console.appendText(
									SternResources.AuswertungPatrouilleMinenlegerGekapert(true, patrolOwnerName, otherShipDestinationName, otherShipOwnerName));
				else if (otherShip.getType() == ShipType.PATROL)
					this.game.console.appendText(
									SternResources.AuswertungPatrouillePatrouilleGekapert(true, patrolOwnerName, otherShipDestinationName, otherShipOwnerName));
				else if (otherShip.getType() == ShipType.FIGHTERS)
				{
					if (otherShip.getCount() > Constants.PATROL_CAPUTURES_FIGHTERS_COUNT_MAX)
					{
						this.game.console.setLineColor(this.game.players[otherShip.getOwner()].getColorIndex());
						this.game.console.appendText(
								SternResources.AuswertungPatrouilleRaumerGesichtet(true, 
										Integer.toString(otherShip.getCount()), otherShipOwnerName, otherShipDestinationName));
						capture = false;
						this.waitForKeyPressed();
					}
					else
					{
						this.game.console.appendText(
								SternResources.AuswertungPatrouilleRaumerGekapert(true, 
										patrolOwnerName, Integer.toString(otherShip.getCount()), otherShipDestinationName, otherShipOwnerName));
					}
				}
				
				if (capture)
				{
					otherShip.capture(
							patrol.getOwner(),
							otherShipPosition);
					
					this.game.updateBoard(
							null,
							this.game.getSimpleMarkedPosition(otherShipPosition),
							radar,
							day);
					
					this.waitForKeyPressed();
				}
				
				this.patrolAddEvent(
						otherShip.hashCode(),
						patrol.hashCode(),
						this.events);
			}
  		}
  		
  		private void patrolAddEvent(
  				int patrolHashCode, 
  				int otherShipHashCode, 
  				Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> list)
  		{
  			Hashtable<Integer, ArrayList<ScreenContent>> patrols = list.get(otherShipHashCode);
  			
  			if (patrols == null)
  			{
  				patrols = new Hashtable<Integer, ArrayList<ScreenContent>>();
  				list.put(otherShipHashCode, patrols);
  			}
  			
  			ArrayList<ScreenContent> eventList = patrols.get(patrolHashCode);
  			
  			if (eventList == null)
  			{
  				eventList = new ArrayList<ScreenContent>();
  				patrols.put(patrolHashCode, eventList);
  			}
  			
  			eventList.add(this.replay.get(this.replay.size() - 1));
  		}
  		
  		private void reducePatrolEvents(
  				Hashtable<Integer, Hashtable<Integer, ArrayList<ScreenContent>>> list)
  		{
  			for (Integer i1: list.keySet())
  			{
  				Hashtable<Integer, ArrayList<ScreenContent>> patrols = list.get(i1);
  				
  				for (Integer i2: patrols.keySet())
  				{
  					ArrayList<ScreenContent> eventList = patrols.get(i2);
  					
  					for (int eventIndex = 0; eventIndex < eventList.size(); eventIndex++)
  					{
  						if (eventIndex == 0 || eventIndex == eventList.size() - 1)
  						{
  							continue;
  						}
  						
  						this.replay.remove(eventList.get(eventIndex));
  					}
  				}
  			}
  		}
  		
  		private void moveShip(Ship ship, int day)
  		{
  			if (ship.isToBeDeleted() || ship.isToBeTurned() || ship.isStopped())
  			{
  				return;
  			}

  			Point sectorCurrent = ship.getSectorOnDay(day);
  			
  			if (sectorCurrent != null)
  			{
  	  			Point sectorPrevious = ship.getSectorOnDay(day-1);
  	  			
  	  			if (sectorPrevious == null || !sectorCurrent.equals(sectorPrevious))
  	  			{
  	  				this.checkForMines(ship, sectorCurrent, day);
  	  			}
  			}
  			
  			if (ship.isToBeDeleted())
  			{
  				return;
  			}
  			
  			this.checkForArrival(ship, day);
  		}
  		  		
  		private void checkForMines(Ship ship, Point sector, int day)
  		{
  			if (ship.isToBeDeleted() ||
  				sector == null ||
  				this.game.mines == null || 
	  	  		this.game.mines.size() == 0)
  			{
  				return;
  			}
  			
  			Point positionShip = ship.getSectorOnDay(day);
  			if (positionShip == null || !positionShip.equals(sector))
  			{
  				return;
  			}
  			
			Mine mine = this.game.mines.get(sector.getString());
			if (mine == null)
				return;
			
			String playerName = this.game.players[ship.getOwner()].getName();
			this.game.console.setLineColor(this.game.players[ship.getOwner()].getColorIndex());
			
			boolean deleteShip = false;
			
			if (ship.getType() == ShipType.FIGHTERS)
			{
				this.printDayEvent(day);
				
				if (ship.getCount() >= mine.getStrength())
				{
					this.game.console.appendText(
						SternResources.AuswertungRaumerAufMineGelaufenZerstoert(
								true,
								playerName,
								Integer.toString(Math.min(ship.getCount(),mine.getStrength())),
								Game.getSectorNameFromPositionStatic(
										new Point(mine.getPositionX(), mine.getPositionY())
										)));
					
					ship.subtractFighters(mine.getStrength(), ship.getOwner());
					
					if (ship.getCount() <= 0)
						deleteShip = true;
					
					this.game.mines.remove(sector.getString());
				}
				else
				{
					this.game.console.appendText(
							SternResources.AuswertungRaumerAufMineGelaufen(
									true, 
									playerName,
									Integer.toString(Math.min(ship.getCount(),mine.getStrength())),
									Game.getSectorNameFromPositionStatic(
											new Point(mine.getPositionX(), mine.getPositionY())
											)));
					
					deleteShip = true;
					
					mine.setStrength(mine.getStrength() - ship.getCount());
				}
			}
			else if (ship.getType() == ShipType.MINESWEEPER)
			{
				this.printDayEvent(day);
				this.game.console.appendText (
						SternResources.AuswertungNachrichtAnAusSektor(true,
								this.game.players[ship.getOwner()].getName(),
								Game.getSectorNameFromPositionStatic(
										new Point(mine.getPositionX(), mine.getPositionY())
										)));
				this.game.console.lineBreak();
				this.game.console.appendText (
						SternResources.AuswertungMinenfeldGeraeumt(true,
								Integer.toString(mine.getStrength())));
				
				
				this.game.mines.remove(sector.getString());
			}
			else
				return;
			
			this.game.updateBoard(
					this.game.getSimpleMarkedPosition(ship.getPositionOnDay(day)),
					day);
			
			this.waitForKeyPressed();

			if (deleteShip)
				ship.setToBeDeleted();
  		}
  		
  		private void checkForArrival(Ship ship, int day)
  		{
  			double distanceTotal = ship.getPositionStart().distance(ship.getPositionDestination());
  			double distanceCurrent = ship.getPositionStart().distance(ship.getPositionOnDay(day));
  			
  			if (distanceCurrent <= distanceTotal - Constants.PRECISION)
  			{
  				return;
  			}
  			
			int planetIndex = ship.getPlanetIndexDestination();
			Planet planet = null;
			
			if (planetIndex != Constants.NO_PLANET)
				planet = this.game.planets[planetIndex];
			
			String playerName = this.game.players[ship.getOwner()].getName();
			this.game.console.setLineColor(this.game.players[ship.getOwner()].getColorIndex());
			
			if (ship.getType() == ShipType.SCOUT)
			{
				this.printDayEvent(day);
				
				if (planet.getOwner() == ship.getOwner())
				{
					this.game.updateBoard(
							this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
							day);
					
					planet.incrementShipsCount(ship.getType(), 1);
					
					this.game.console.appendText(
							SternResources.AuswertungAufklaererAngekommen(
									true,
									playerName,
									this.game.getPlanetNameFromIndex(planetIndex)));
				}
				else
				{
					this.game.updateBoard(day);
					planet.setRadioStation(ship.getOwner());
					
					this.game.console.appendText(
							SternResources.AuswertungAufklaererSender(
									true,
									this.game.players[ship.getOwner()].getName(),
									this.game.getPlanetNameFromIndex(planetIndex)));								
				}
				this.waitForKeyPressed();
				ship.setToBeDeleted();
			}
			else if (ship.getType() == ShipType.TRANSPORT)
			{
				this.printDayEvent(day);
				
				this.game.updateBoard(
						this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
						day);
				
				planet.addToMoneySupply(ship.getCount());
				
				if (planet.getOwner() == ship.getOwner())
				{
					planet.incrementShipsCount(ship.getType(), 1);
					
					this.game.console.appendText(
							SternResources.AuswertungTransporterAngekommen(
									true, 
									playerName,
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
				}
				else
					this.game.console.appendText(
							SternResources.AuswertungTransporterZerschellt(
									true,
									playerName,
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
				
				CommandRoom commandRoom = ship.getCommandRoom();
				
				if (commandRoom != null)
				{
					if (commandRoom.getOwner() == planet.getOwner())
					{
						planet.setCommandRoom(ship.getCommandRoom());
						this.waitForKeyPressed();
					}
					else
					{
						this.waitForKeyPressed();
						this.commandRoomConquered(
								ship.getCommandRoom().getOwner(), 
								planet.getOwner(), 
								false,
								day);
					}
				}
				else
					this.waitForKeyPressed();
				
				ship.setToBeDeleted();
			}
			else if (ship.getType() == ShipType.PATROL)
			{
				if (planet.getOwner() == ship.getOwner())
				{
					if (ship.isTransfer())
					{
						this.printDayEvent(day);
						
						this.game.updateBoard(
								this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
								day);
						if (ship.getOwner() == planet.getOwner())
						{
							planet.incrementShipsCount(ship.getType(), 1);
							this.game.console.appendText(
									SternResources.AuswertungPatrouilleAngekommen(
											true, 
											playerName,
											this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
						}
						else
							this.game.console.appendText(
									SternResources.AuswertungPatrouilleZerschellt(
											true,
											playerName,
											this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
						this.waitForKeyPressed();
						ship.setToBeDeleted();
					}
					else
					{
						this.addScreenSnapshotToReplay(day);
						ship.setToBeTurned();
					}
				}
				else
				{
					this.printDayEvent(day);
					
					this.game.updateBoard(
							this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
							day);
					this.game.console.appendText(
						SternResources.AuswertungPatrouilleZerschellt(
								true, 
								playerName,
								this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					this.waitForKeyPressed();
					ship.setToBeDeleted();
				}
			}
			else if (ship.getType() == ShipType.MINE50 ||
					 ship.getType() == ShipType.MINE100 ||
					 ship.getType() == ShipType.MINE250 ||
					 ship.getType() == ShipType.MINE500)
			{
				this.printDayEvent(day);
				
				if (ship.isTransfer())
				{
					this.game.updateBoard(
							this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
							day);
					if (ship.getOwner() == planet.getOwner())
					{
						planet.incrementShipsCount(ship.getType(), 1);
						this.game.console.appendText(
								SternResources.AuswertungMinenlegerAngekommen(
										true, 
										playerName,
										this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					}
					else
						this.game.console.appendText(
							SternResources.AuswertungMinenlegerZerschellt(
									true, 
									playerName,
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					
					this.waitForKeyPressed();
					ship.setToBeDeleted();
				}
				else
				{
					this.placeMine(ship);

					this.game.updateBoard(
							this.game.getSimpleMarkedPosition(ship.getPositionOnDay(day)),
							day);
							
					this.game.console.appendText(
							SternResources.AuswertungMineGelegt(
									true, 
									this.game.players[ship.getOwner()].getName()));
					this.waitForKeyPressed();
					ship.setToBeDeleted();
					
					Point sectorShip = ship.getSectorOnDay(day);
					int shipsSequence[] = Utils.getRandomList(this.game.ships.size());
					
					for (int i = 0; i < shipsSequence.length; i++)
					{
						this.checkForMines(this.game.ships.get(shipsSequence[i]), sectorShip, day);
					}
				}
			}
			else if (ship.getType() == ShipType.MINESWEEPER)
			{
				if (ship.getPlanetIndexDestination() == Constants.NO_PLANET)
				{
					this.addScreenSnapshotToReplay(day);
					ship.setToBeTurned();
				}
				else
				{
					this.game.updateBoard(
							this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
							day);
					
					this.printDayEvent(day);
					
					if (ship.getOwner() == planet.getOwner())
					{
						planet.incrementShipsCount(ship.getType(), 1);
						this.game.console.appendText(
							SternResources.AuswertungMinenraeumerAngekommen(
									true,
									playerName,
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					}
					else
						this.game.console.appendText(
							SternResources.AuswertungMinenraeumerZerschellt(
									true,
									playerName,
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					this.waitForKeyPressed();
					ship.setToBeDeleted();

				}
			}
			else if (ship.getType() == ShipType.FIGHTERS)
			{
				this.printDayEvent(day);

				if (this.game.planets[ship.getPlanetIndexDestination()].getOwner() == ship.getOwner() ||
					this.game.planets[ship.getPlanetIndexDestination()].isAllianceMember(ship.getOwner()))
				{
					this.game.updateBoard(
							this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
							day);
					this.game.planets[ship.getPlanetIndexDestination()].mergeFighters(this.game.playersCount, ship);
					
					this.game.console.appendText(
						SternResources.AuswertungRaumerAngekommen(true,
									playerName,
									Integer.toString(ship.getCount()),
									this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
					
					this.game.updatePlanets(false);
					this.waitForKeyPressed();
				}
				else
					this.fightersAttack(ship, planetIndex, day);
				
				ship.setToBeDeleted();
			}
  		}
  		  		
  		private void fightersAttack(Ship ship, int planetIndex, int day)
  		{
  			String playerName = this.game.players[ship.getOwner()].getName();
  			
  			Planet planet = this.game.planets[planetIndex];
  			
  			this.game.updateBoard(
  					this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
  					day);
  			
  			FightStruct fightStruct = new FightStruct();
  			
			fightStruct.offenderCount = ship.getCount();
			
			this.game.console.appendText(
							SternResources.AuswertungAngriffAngriffAufPlanet(
									true,
									playerName,
									this.game.getPlanetNameFromIndex(planetIndex)));

			this.game.console.lineBreak();
			this.game.console.setLineColor(Colors.WHITE);

			int defenceShieldFightersCount = planet.getDefenceShieldFightersCount();
			
			if (defenceShieldFightersCount > 0)
			{
				fightStruct.defenderCount = defenceShieldFightersCount;
				fightStruct.defenceShieldExists = true;
				
				this.fight(fightStruct);
				
				if (fightStruct.defenderCount == 0)
				{
					planet.deleteDefenceShield();
				}
				else
				{
					if (fightStruct.defenderCount <= 0)
						planet.deleteDefenceShield();
					else
						planet.setDefenceShieldFightersCount(fightStruct.defenderCount);
					
				}
				
				if (fightStruct.offenderCount <= 0)
					this.game.console.appendText(
							SternResources.AuswertungAngriffAngriffGescheitert(true));
			}

			CommandRoom commandRoom = null;
			
			if (fightStruct.offenderCount > 0)
			{			
				fightStruct.defenceShieldExists = false;
				
				fightStruct.defenderCount = planet.getShipsCount(ShipType.FIGHTERS);
				
				this.fight(fightStruct);
				
				if (fightStruct.offenderCount > fightStruct.defenderCount)
				{
					ship.subtractFighters(ship.getCount() - fightStruct.offenderCount,ship.getOwner());
					
					commandRoom = planet.conquer(this.game.playersCount, ship.getOwner(), ship);
					
					this.game.console.setLineColor(this.game.players[ship.getOwner()].getColorIndex());
					this.game.console.appendText(
							SternResources.AuswertungAngriffSpielerErobert(true,
									playerName));
				}
				else
				{
					planet.subtractFightersCount(this.game.playersCount, planet.getShipsCount(ShipType.FIGHTERS) - fightStruct.defenderCount, planet.getOwner(), true);
					
					this.game.console.appendText(
							SternResources.AuswertungAngriffAngriffGescheitert(true));					
				}
			}
			
			this.game.updatePlanets(false);
			this.game.updateBoard(
					this.game.getSimpleFrameObjekt(planetIndex, Colors.WHITE),
					day);
			
			this.waitForKeyPressed();
			
			if (commandRoom != null)
				this.commandRoomConquered(
						commandRoom.getOwner(), 
						ship.getOwner(), 
						false,
						day);
  		}
  		
  		private void fight(FightStruct fightStruct)
  		{
  			boolean firstRound = true;
  			
			do
			{
				if (fightStruct.defenceShieldExists)
	  			{
		  			this.game.console.appendText(
							SternResources.AuswertungAngriffAngreiferFestung(
									true, Integer.toString(fightStruct.offenderCount), Integer.toString(fightStruct.defenderCount)) + " ");
	  			}
	  			else
	  			{
	  				this.game.console.appendText(
							SternResources.AuswertungAngriffAngreiferPlanet(
									true, Integer.toString(fightStruct.offenderCount), Integer.toString(fightStruct.defenderCount)));
	  			}
				
				if (fightStruct.offenderCount == 0 || fightStruct.defenderCount == 0)
				{
					this.game.console.lineBreak();
					break;
				}
				
				this.pause(Constants.PAUSE_MILLISECS);
				
				if (firstRound)
				{
					this.game.console.lineBreak();
					firstRound = false;
				}
				else
					this.game.console.deleteLine();
				
				int reductionMaxDefender = Math.max(1, Utils.round((double)fightStruct.offenderCount / (double)6));
				int reductionMaxOffender = Math.max(1, Utils.round((double)fightStruct.defenderCount *  Constants.DEFENSE_BONUS / (double)6));
				
				int reductionDefender = Utils.getRandomInteger(reductionMaxDefender) + 1;
				int reductionOffender = Utils.getRandomInteger(reductionMaxOffender) + 1;
				
				fightStruct.offenderCount -= reductionOffender;
				fightStruct.defenderCount -= reductionDefender;
				
				if (fightStruct.offenderCount < 0)
					fightStruct.offenderCount = 0;
				if (fightStruct.defenderCount < 0)
					fightStruct.defenderCount = 0;
				
			} while (true);

  		}
  		
  		private class FightStruct
  		{
  			public int offenderCount;
  			public int defenderCount;
  			public boolean defenceShieldExists;
  		}
  		
  		private void placeMine(Ship obj)
  		{
  			if (this.game.mines == null)
  				this.game.mines = new Hashtable<String,Mine>();
  			
  			int strength = 0;
  			
  			if (obj.getType() == ShipType.MINE50)
  				strength = 50;
  			else if (obj.getType() == ShipType.MINE100)
  				strength = 100;
  			else if (obj.getType() == ShipType.MINE250)
  				strength = 250;
  			else
  				strength = 500;
  			
  			String positionString = obj.getPositionDestination().getString();
  			Mine mine = this.game.mines.get(positionString);
  			
  			if (mine == null)
  			{
  				this.game.mines.put(positionString, new Mine((int)obj.getPositionDestination().x, (int)obj.getPositionDestination().y, strength));
  			}
  			else
  				mine.addToStrength(strength);
  		}
  		
  		private void commandRoomConquered(int playerIndexLoser, int playerIndexWinner, boolean capitulated, int day)
  		{
  			this.printDayEvent(day);
  			
  			if (playerIndexWinner == Constants.NEUTRAL)
  			{
  				if (capitulated)
  				{
  					this.game.console.setLineColor(this.game.players[playerIndexLoser].getColorIndex());
  					this.game.console.appendText(
	  						SternResources.AuswertungKapitulation(
	  								true,
	  								this.game.players[playerIndexLoser].getName()));
  				}
  				else
  				{
	  				this.game.console.setLineColor(Colors.NEUTRAL);
	  				this.game.console.appendText(
	  						SternResources.AuswertungKommandozentraleErobertNeutral(
	  								true,
	  								this.game.players[playerIndexLoser].getName()));
  				}
  			}
  			else
  			{
  				this.game.console.setLineColor(this.game.players[playerIndexWinner].getColorIndex());
  				
  				this.game.console.appendText(
  						SternResources.AuswertungKommandozentraleErobertSpieler(true,
  								this.game.players[playerIndexWinner].getName(),
  								this.game.players[playerIndexLoser].getName()));
  			}
  			
  			for (Planet planet: this.game.planets)
  			{
  				planet.changeOwner(playerIndexLoser, playerIndexWinner);
  			}
  				
  			for (Ship ship: this.game.ships)
  			{
  				int playerIndexLoserAdditional = ship.changeOwner(playerIndexLoser, playerIndexWinner);
  				
  				if (playerIndexLoserAdditional != Constants.NEUTRAL)
  				{
  					this.commandRoomConquered(
  							playerIndexLoserAdditional, 
  							playerIndexWinner,
  							false,
  							day);
  				}
  			}
  			
  			this.game.updatePlanets(false);
  			this.game.updateBoard(day);
  			
  			this.waitForKeyPressed();
  		}  		
  	}
  	
  	// ===============
  	
  	private class Replay
 	{
 		private Game game;
 		
 		private Replay(Game game)
 		{
 			this.game = game;
 			
 			if (game.goToReplay)
 			{
 				game.goToReplay = false;
 				
 				if (this.game.evaluationExists())
 				{
 					this.replayArchive();
 				}
 				
 				return;
 			}
 			
 			if (!this.game.evaluationExists())
 			{
 				return;
 			}
 						
			this.game.console.clear();
			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> " + SternResources.ReplayAuswertungWiedergeben(true), Colors.NEUTRAL);
			
			this.replayArchive();
				
			this.game.updatePlanets(false);
			this.game.updateBoard();
 		}
 		
 		private void replayArchive()
 		{
 			boolean finished = false;
 			
 			this.game.console.clear();
			this.game.console.setHeaderText(
					SternResources.ReplayWiedergabeJahr(true,
							Integer.toString(year)),
					Colors.NEUTRAL);
			
			ScreenContent screenContentDayEventPrevious = null;
			
			for (ScreenContent screenContentDayEventCurrent: this.game.replayLast)
			{
				if (screenContentDayEventPrevious != null && 
					screenContentDayEventCurrent.getEventDay() != screenContentDayEventPrevious.getEventDay())
				{
					this.animate(
							screenContentDayEventPrevious, 
							screenContentDayEventCurrent);
				}
				
				this.game.gameThread.updateDisplay(screenContentDayEventCurrent);
				
				if (screenContentDayEventCurrent.isPause())
					this.game.pause(Constants.PAUSE_MILLISECS);
				else if (!screenContentDayEventCurrent.isSnapshot())
					finished = this.game.console.waitForKeyPressedReplay();
				
				if (finished)
					break;
				
				screenContentDayEventPrevious = (ScreenContent)Utils.klon(screenContentDayEventCurrent);
			}				
 		}
 		
 		private void animate(ScreenContent screenContentDayEventPrevious, ScreenContent screenContentDayEventCurrent)
 		{
 			int day = screenContentDayEventPrevious.getEventDay() + 2;
 			
 			if (day >= screenContentDayEventCurrent.getEventDay() - 1)
 				return;
 			
 			screenContentDayEventPrevious.getBoard().clearMarks();
 			screenContentDayEventPrevious.getConsole().clearKeys();
 			
 			Hashtable<Integer, ScreenContentBoardPosition> shipPositionsPrevious = new Hashtable<Integer, ScreenContentBoardPosition>();
 			
 			for (ScreenContentBoardPosition position: screenContentDayEventPrevious.getBoard().getPositions())
 			{
 				shipPositionsPrevious.put(position.getHashShip(), position);
 			}
 			
 			Hashtable<Integer, ScreenContentBoardPosition> shipPositionsCurrent = new Hashtable<Integer, ScreenContentBoardPosition>();
 			
 			for (ScreenContentBoardPosition position: screenContentDayEventCurrent.getBoard().getPositions())
 			{
 				shipPositionsCurrent.put(position.getHashShip(), position);
 			}
 			
			ArrayList<ScreenContentBoardPosition> positions = 
					new ArrayList<ScreenContentBoardPosition>();

 			while (day < screenContentDayEventCurrent.getEventDay())
 			{
 				this.game.pause(Constants.PAUSE_MILLISECS_ANIMATION);
 				
 				screenContentDayEventPrevious.getConsole().setProgressBarDay(day);
	 			
 				positions.clear();
 				
 				for (Integer hashShip: shipPositionsPrevious.keySet())
 				{
 					if (!shipPositionsCurrent.containsKey(hashShip))
 						continue;
 					
 					ScreenContentBoardPosition positionPrevious = shipPositionsPrevious.get(hashShip);
 					ScreenContentBoardPosition positionCurrent = shipPositionsCurrent.get(hashShip);
 					
 					double t = (double)(day - screenContentDayEventPrevious.getEventDay()) / 
 							   (double)(screenContentDayEventCurrent.getEventDay() - screenContentDayEventPrevious.getEventDay());
 					
 					double x = positionPrevious.getPosition().x + t * (positionCurrent.getPosition().x - positionPrevious.getPosition().x);
 					double y = positionPrevious.getPosition().y + t * (positionCurrent.getPosition().y - positionPrevious.getPosition().y);
 					
 					ScreenContentBoardPosition point = 
 							new ScreenContentBoardPosition(
 									new Point(x, y), 
 									positionPrevious.getColorIndex(), 
 									positionPrevious.getSymbol(),
 									positionPrevious.getHashShip());
 					
 					positions.add(point);
 				}
 				
 				screenContentDayEventPrevious.getBoard().setPositions(positions);
 				
 				this.game.gameThread.updateDisplay(screenContentDayEventPrevious);
 				
 				day += 2;
 			}
 			
 			this.game.pause(Constants.PAUSE_MILLISECS_ANIMATION);
 		}
 	}
  	
  	// ==============
  	
  	private class GameInformation
 	{
 		private Game game;
 		
 		public void setGame(Game game)
 		{
 			this.game = game;
 		}
 		
 		public GameInformation()
 		{
 		}
 		
 		private GameInformation(Game game)
 		{
 			this.game = game;
 			boolean simple = this.game.isSimple();
 			
 			do
 			{
 				this.game.console.clear();
 				
 				this.game.updateBoard();
 				this.game.updatePlanets(false);
 				
 				this.game.console.setHeaderText(
 						this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true), Colors.NEUTRAL);
 				
 				ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
 				
 				allowedKeys.add(new ConsoleKey("ESC", SternResources.Hauptmenue(true)));
 				allowedKeys.add(new ConsoleKey("0",SternResources.SpielinformationenPlanet(true)));
 				
 				allowedKeys.add(new ConsoleKey("1",
 						simple ?
 								SternResources.SpielinformationenKampfschiffproduktion(true) :
 								SternResources.SpielinformationenEnergieproduktion(true)));
 				
 				if (!simple && this.game.options.contains(GameOptions.DEFENCE_SHIELDS))
 					allowedKeys.add(new ConsoleKey("2", SternResources.SpielinformationenFestungen(true)));
 				if (!simple)
 					allowedKeys.add(new ConsoleKey("3",SternResources.SpielinformationenPatrouillen(true)));
 				if (!simple && (this.game.options.contains(GameOptions.COMMAND_ROOMS) ||
 						this.game.options.contains(GameOptions.COMMAND_ROOMS_STATIC)))
 					allowedKeys.add(new ConsoleKey("4", SternResources.SpielinformationenKommandozentralen(true)));
 				if (!simple)
 					allowedKeys.add(new ConsoleKey("5",SternResources.SpielinformationenBuendnisse(true)));
 				if (!simple)
 					allowedKeys.add(new ConsoleKey("6",SternResources.SpielinformationenSender(true)));
 				 				
 				ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, false);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.game.console.clear();
 					break;
 				}
 				
 				String inputString = input.getInputText().toUpperCase();
 				
 				if (inputString.equals("1"))
 					this.moneyProduction(simple);
 				else if (!simple && inputString.equals("5"))
 					this.alliances();
 				else if (!simple && inputString.equals("6"))
 					this.radioStations();
 				else if (!simple && inputString.equals("2") && this.game.options.contains(GameOptions.DEFENCE_SHIELDS))
 					this.defenceShields();
 				else if (!simple && inputString.equals("3"))
 					this.patrols();
 				else if (!simple && inputString.equals("4") && 
 						( this.game.options.contains(GameOptions.COMMAND_ROOMS) ||
 								this.game.options.contains(GameOptions.COMMAND_ROOMS_STATIC)))
 					this.commandRooms();
 				else if (inputString.equals("0"))
 					this.planetEditorDisplay();
 				else
 					this.game.console.outInvalidInput();
 				
 			} while (true);
 		}
 		
 		private void moneyProduction(boolean simple)
 		{
 			if (simple)
 			{
 				this.game.console.setHeaderText(
							this.game.mainMenuGetYearDisplayText() + 
							" -> "+SternResources.Spielinformationen(true)+
							" -> "+SternResources.SpielinformationenKampfschiffproduktionTitel(true), Colors.NEUTRAL);
 			}
	 		else
	 		{
	 			this.game.console.setHeaderText(
	 							this.game.mainMenuGetYearDisplayText() + 
	 							" -> "+SternResources.Spielinformationen(true)+
	 							" -> "+SternResources.SpielinformationenEnergieproduktionTitel(true), Colors.NEUTRAL);
	 		}
 			
 			ArrayList<ScreenContentBoardPlanet> screenContentsPlanet = new ArrayList<ScreenContentBoardPlanet>(this.game.planetsCount);
 			
 			for (int index = 0; index < this.game.planetsCount; index++)
 			{
 				int planetIndex = getPlanetsSorted()[index];
 				
 				byte colorIndex = this.game.planets[planetIndex].isNeutral() ?
 						Colors.NEUTRAL :
 						this.game.players[this.game.planets[planetIndex].getOwner()].getColorIndex();
 				
 				screenContentsPlanet.add(new ScreenContentBoardPlanet(
 							Integer.toString(this.game.planets[planetIndex].getMoneyProduction()),
 							this.game.planets[planetIndex].getPosition(),
 							colorIndex,
 							null)); 					
 			}
 			 			
 			if (this.game.screenContent == null)
 				this.game.screenContent = new ScreenContent();
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(screenContentsPlanet,
 					null,
 					null,
 					null,
 					null,
 					null));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);

 			this.game.console.waitForKeyPressed();
 		}
 		
 		private void alliances()
 		{
 			this.game.console.setHeaderText(
 					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenBuendnisseTitel(true), Colors.NEUTRAL);
 			
 			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int index = 0; index < this.game.planetsCount; index++)
 			{
 				int planetIndex = getPlanetsSorted()[index];
 				
 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				
 				for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 				{
 					if (this.game.planets[planetIndex].getOwner() != playerIndex && this.game.planets[planetIndex].isAllianceMember(playerIndex))
 						frameCols.add(this.game.players[playerIndex].getColorIndex());
 				}
 						
 				if (frameCols.size() > 0)
 				{
 					frames.put(planetIndex, frameCols);
 					planetIndicesHighlighted.add(planetIndex);
 				}
 			}
 			
 			ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
		 					null,
		 					null,
		 					null,
		 					null,
		 					null));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);
 			
 			if (planetIndicesHighlighted.size() == 0)
 			{
 				this.game.console.appendText(
 						SternResources.SpielinformationenKeinePlanetenMitBuendnissen(true));
 				this.game.console.waitForKeyPressed();
 				return;
 			}

 			do
 			{
 				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.SpielinformationenBuendnisPlanet(true), 
						false, 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					break;
				}
				
 				int planetIndex = input.planetIndex;
 				
 				if (this.game.planets[planetIndex].allianceExists())
 				{
 					this.game.console.setLineColor(this.game.players[this.game.planets[planetIndex].getOwner()].getColorIndex());
 					this.game.console.appendText(
 							SternResources.SpielinformationenBuendnisstruktur(true, this.game.getPlanetNameFromIndex(planetIndex)) +
 							":");
 					this.game.console.lineBreak();
 					this.game.console.setLineColor(Colors.WHITE);
						
					this.game.printAllianceInfo(planetIndex);
 				}
 				else
 					this.game.console.appendText(
 							SternResources.SpielinformationenKeinBuendnis(true, this.game.getPlanetNameFromIndex(planetIndex)));
 				
 				this.game.console.lineBreak();
 			} while (true);
 		}
 		
 		private void radioStations()
 		{
 			this.game.console.setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenSender(true), Colors.NEUTRAL);
 			
 			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int index = 0; index < this.game.planetsCount; index++)
 			{
 				int planetIndex = getPlanetsSorted()[index];
 				
 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				
 				for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
 				{
 					if (this.game.planets[planetIndex].hasRadioStation(playerIndex))
 					{
 						frameCols.add(this.game.players[playerIndex].getColorIndex());
 					}
 				}
 				
 				if (frameCols.size() > 0)
 				{
 					frames.put(planetIndex, frameCols);
 					planetIndicesHighlighted.add(planetIndex);
 				}
 			}
 			
 			ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
		 					null,
		 					null,
		 					null,
		 					null,
		 					null));

 			this.game.gameThread.updateDisplay(this.game.screenContent);

 			this.game.console.waitForKeyPressed();
 		}
 		
 		private void defenceShields()
 		{
 			this.game.console.setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenFestungenTitel(true), Colors.NEUTRAL);
 			
 			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int index = 0; index < this.game.planetsCount; index++)
 			{
 				int planetIndex = getPlanetsSorted()[index];
 				
 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				
 				for (int i = 0; i < this.game.planets[planetIndex].getDefenceShieldFactor(); i++)
 					frameCols.add(Colors.WHITE);
 						
 				if (frameCols.size() > 0)
 				{
 					frames.put(planetIndex, frameCols);
 					planetIndicesHighlighted.add(planetIndex);
 				}
 			}
 			
 			ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
		 					null,
		 					null,
		 					null,
		 					null,
		 					null));
 			
 			ArrayList<String> text = new ArrayList<String>();
 			ArrayList<Byte> textCol = new ArrayList<Byte>();
 			
 			for (int playerIndex = Constants.NEUTRAL; playerIndex < this.game.playersCount; playerIndex++)
 			{
 				boolean firstLine = true;
 				
 				for (int index = 0; index < this.game.planetsCount; index++)
 				{
 					int planetIndex = getPlanetsSorted()[index];
 					
 					if (this.game.planets[planetIndex].getOwner() != playerIndex ||
 						this.game.planets[planetIndex].getDefenceShieldFactor() == 0)
 						continue;
 					
 					String playerName = (playerIndex == Constants.NEUTRAL) ?
 										SternResources.Neutral(false) :
 										this.game.players[playerIndex].getName();
 										
 					byte colorIndex = (playerIndex == Constants.NEUTRAL) ?
 										(byte)Colors.NEUTRAL :
 										this.game.players[playerIndex].getColorIndex();
 					
 					if (firstLine)
 					{
 						text.add(playerName);
 						textCol.add(colorIndex);
 						
 						firstLine = false;
 					}
 					
 					String planetName = " " + this.game.getPlanetNameFromIndex(planetIndex);
 					String defenceShieldFightersCount = "     " + this.game.planets[planetIndex].getDefenceShieldFightersCount();
 					text.add(planetName.substring(planetName.length()-2, planetName.length()) + 
 							":" +
 							defenceShieldFightersCount.substring(defenceShieldFightersCount.length()-5, defenceShieldFightersCount.length()));
 					
 					textCol.add(colorIndex);
 				}
 			}
 			
 			this.game.screenContent.setPlanets(
 					new ScreenContentPlanets(text, textCol));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);

 			this.game.console.waitForKeyPressed();
 		}
 		
 		private void commandRooms()
 		{
 			this.game.console.setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> " + SternResources.SpielinformationenKommandozentralenTitel(true), Colors.NEUTRAL);
 			
 			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int planetIndex = 0; planetIndex < this.game.planetsCount; planetIndex++)
 			{
 				if (!this.game.planets[planetIndex].hasCommandRoom())
 					continue;
 				
 				CommandRoom commandRoom = this.game.planets[planetIndex].getCommandRoom();

 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				frameCols.add(this.game.players[commandRoom.getOwner()].getColorIndex());
 				
				frames.put(planetIndex, frameCols);
				planetIndicesHighlighted.add(planetIndex);
 			}
 			
 			ArrayList<Ship> shipsWithCommandRooms = new ArrayList<Ship>();
 			
 			ArrayList<ScreenContentBoardLine> lines = new ArrayList<ScreenContentBoardLine>();

 			for (Ship ship: this.game.ships)
 			{
 				if (ship.getType() != ShipType.TRANSPORT || ship.getCommandRoom() == null)
 					continue;
 				
 				shipsWithCommandRooms.add(ship);
 				
 				ScreenContentBoardLine line = new ScreenContentBoardLine(
 	 					ship.getPositionStart(), 
 	 					ship.getPositionDestination(), 
 	 					ship.getPositionOnDay(0), 
 	 					this.game.players[ship.getCommandRoom().getOwner()].getColorIndex(),
 	 					ship.getScreenDisplaySymbol());
 				
 				lines.add(line);
 				
 				if (ship.getPlanetIndexStart() != Constants.NO_PLANET)
 					planetIndicesHighlighted.add(ship.getPlanetIndexStart());
 				
 				planetIndicesHighlighted.add(ship.getPlanetIndexDestination());
 			}
 			
 			ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(frames, planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
		 					null,
		 					lines,
		 					null,
		 					null,
		 					null));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);

 			if (shipsWithCommandRooms.size() == 0)
 			{
 				this.game.console.waitForKeyPressed();
 				return;
 			}
 			
 			ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();
 			allowedKeys.add(new ConsoleKey(SternResources.Taste(true), SternResources.Weiter(true)));
 			allowedKeys.add(new ConsoleKey("ESC", SternResources.Abbrechen(true)));
 			
 			for (Ship ship: shipsWithCommandRooms)
 			{
 				ShipTravelTime flugzeitRest = ship.getTravelTimeRemaining();
 				CommandRoom commandRoom = ship.getCommandRoom();
 				
 				String playerName = this.game.players[commandRoom.getOwner()].getName();
 				
 				this.game.console.setLineColor(this.game.players[commandRoom.getOwner()].getColorIndex());
 				
 				this.game.console.appendText(
 						SternResources.SpielinformationenKommandozentralenUnterwegs(true,
 								playerName,
 								this.game.getPlanetNameFromIndex(ship.getPlanetIndexDestination())));
 				this.game.console.lineBreak();
 				this.game.console.appendText(
 						SternResources.SpielinformationenKommandozentralenUnterwegs2(true,
 								flugzeitRest.toOutputString(false)) + " ");
 				
 				ConsoleInput input = this.game.console.waitForKeyPressed(allowedKeys, false);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 					break;
 			}
 		}


 		private void patrols()
 		{
 			this.game.console.setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPatrouillenTitel(true), Colors.NEUTRAL);

 			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			ArrayList<ScreenContentBoardLine> lines = new ArrayList<ScreenContentBoardLine>();

 			for (Ship ships: this.game.ships)
 			{
 				if (ships.getType() != ShipType.PATROL || ships.isTransfer())
 					continue;
 				
 				ScreenContentBoardLine line = new ScreenContentBoardLine(
 	 					ships.getPositionStart(), 
 	 					ships.getPositionDestination(), 
 	 					ships.getPositionOnDay(0), 
 	 					this.game.players[ships.getOwner()].getColorIndex(),
 	 					ships.getScreenDisplaySymbol());
 				
 				lines.add(line);
 				
 				planetIndicesHighlighted.add(ships.getPlanetIndexStart());
 				planetIndicesHighlighted.add(ships.getPlanetIndexDestination());
 			}
 			 			
 			ArrayList<ScreenContentBoardPlanet> planets = this.getDefaultBoard(null, planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
		 					null,
		 					lines,
		 					null,
		 					null,
		 					null));
 			
 			this.game.gameThread.updateDisplay(this.game.screenContent);

 			this.game.console.waitForKeyPressed();
 		}
 		
 		private void planetEditorDisplay()
 		{
 			do
 			{
 				this.game.console.setHeaderText(this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPlanetTitel(true), Colors.NEUTRAL);
 				
 				PlanetInputStruct input = this.game.getPlanetInput(
						SternResources.SpielinformationenPlanet(true), 
						false, 
						PlanetInputStruct.ALLOWED_INPUT_PLANET);
				
				if (input == null)
				{
					break;
				}
				
 				int planetIndex = input.planetIndex;
 				Planet planet = this.game.planets[planetIndex];
 				
 				byte colorIndex = (this.game.planets[planetIndex].getOwner() == Constants.NEUTRAL) ?
 						Colors.NEUTRAL :
 							this.game.players[planet.getOwner()].getColorIndex();
 				
 				this.game.console.setHeaderText(
 						this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPlanetTitel(true)+" " + this.game.getPlanetNameFromIndex(planetIndex), colorIndex);
 				
 				if (this.game.options.contains(GameOptions.SIMPLE))
 				{
 					game.console.appendText(
 							SternResources.InventurRaumerproduktionJahr(true)+": " + this.game.planets[planetIndex].getFighterProduction() + " " + SternResources.PlEditEe(true));
 					
 					game.console.lineBreak();
 				}
 				else
 				{ 				
	 				new PlanetEditor(
	 						this.game,
	 						planetIndex,
	 						null,
	 						true);
 				}
 				
 			} while (true);
 		}

 		private ArrayList<ScreenContentBoardPlanet> getDefaultBoard(Hashtable<Integer, ArrayList<Byte>> frames, HashSet<Integer> planetIndicesHighlighted)
 		{
 			ArrayList<ScreenContentBoardPlanet> planets = new ArrayList<ScreenContentBoardPlanet>(this.game.planetsCount);
 			
 			for (int planetIndex = 0; planetIndex < this.game.planetsCount; planetIndex++)
 			{
 				ArrayList<Byte> frameCols = null;
 				
 				if (frames != null && frames.containsKey(planetIndex))
 					frameCols = frames.get(planetIndex);
 				
 				byte colorIndex = (this.game.planets[planetIndex].isNeutral()) ? 
 						Colors.NEUTRAL :
 						this.game.players[this.game.planets[planetIndex].getOwner()].getColorIndex();

 				if (planetIndicesHighlighted != null && !planetIndicesHighlighted.contains(planetIndex))
 				{
 					colorIndex = Colors.getColorIndexDarker(colorIndex);
 					if (frameCols != null)
 					{
 						ArrayList<Byte> frameColsDarker = new ArrayList<Byte>(frameCols.size());
 						for (Byte fc: frameCols)
 							frameColsDarker.add(Colors.getColorIndexDarker(fc));
 						frameCols = frameColsDarker;
 					}
 				}
 				
 				planets.add(new ScreenContentBoardPlanet(
							this.game.getPlanetNameFromIndex(planetIndex),
							this.game.planets[planetIndex].getPosition(),
							colorIndex,
							frameCols)); 				
 					
 			}
 			
 			if (this.game.screenContent == null)
 				this.game.screenContent = new ScreenContent();
 			
 			return planets;
 		}
 	}
  	
  	// ================
  	
  	private class Inventory
  	{
  		private Game game;
  		private int playerIndex;
  		private InventoryPdfData pdfData;
  		private GameInformation gameInformation;
  		private ScreenContent screenContentCopy;
  		private boolean simple;
  		
  		private Inventory(Game game, int playerIndex)
  		{
  			this.game = game;
  			this.playerIndex = playerIndex;
  			this.simple = this.game.options.contains(GameOptions.SIMPLE);
  			
  			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> Zugeingabe " + this.game.players[this.playerIndex].getName() + " -> "+SternResources.InventurTitel(true),
					this.game.players[this.playerIndex].getColorIndex());
  			
  			this.game.console.clear();

			this.game.console.appendText(SternResources.PdfOeffnenFrage(true) + " ");
			
			ConsoleInput input = this.game.console.waitForKeyPressedYesNo(false);
			
			if (input.getInputText().equals(Console.KEY_YES))
			{
				byte[] pdfBytes = this.create(input.getLanguageCode());
				boolean success = false;
				
				if (input.getClientId() == null)
					success = PdfLauncher.showPdf(pdfBytes);
				else
					success = game.gameThread.openPdf(pdfBytes, input.getClientId());
				
				if (success)
					this.game.console.appendText(SternResources.InventurPdfGeoeffnet(true));
				else
					this.game.console.appendText(SternResources.InventurPdfFehler(true));
				
				this.game.console.lineBreak();
			}
			else
			{
				this.game.console.outAbort();
			}
  		}
  		
  		private byte[] create(String languageCode)
  		{
  			this.screenContentCopy = (ScreenContent)Utils.klon(this.game.screenContent);
  			
  			String languageCodeCopy = SternResources.getLocale();
  			SternResources.setLocale(languageCode);

  			this.gameInformation = new GameInformation();
  			this.gameInformation.setGame(this.game);
  			
  			this.pdfData = new InventoryPdfData(
  					this.game.players[this.playerIndex].getName(),
  					this.game.year,
  					this.game.yearMax,
  					this.game.archive.get(this.game.year).getScore()[this.playerIndex],
  					false);
  			
  			this.planets();
  			this.ships();
  			
  			if (!this.simple)
  				this.patrols();
  			
  			this.game.screenContent = screenContentCopy;
  			
  			byte[] pdfByteArray = null;			
 			try
 			{
 				pdfByteArray = InventoryPdf.create(pdfData);
 			}
 			catch (Exception e)
 			{
 			}
 			
 			SternResources.setLocale(languageCodeCopy);
 			
 			return pdfByteArray;
  		}
  		
  		private void ships()
  		{
  			InventoryPdfChapter chapter = 
  					new InventoryPdfChapter(
  							this.simple ? SternResources.InventurRaumer(false) : SternResources.InventurFlugobjekte(false),
  							this.simple ?
  									SternResources.InventurKeineRaumer(false) :
  									SternResources.InventurKeineFlugobjekte(false));
  			
  			ArrayList<ShipTravelTime> travelTimesRemaining = new ArrayList<ShipTravelTime>();
  			ArrayList<Ship> ships = new ArrayList<Ship>();
  			ArrayList<ScreenContentBoardLine> lines = new ArrayList<ScreenContentBoardLine>();
  			ArrayList<ScreenContentBoardPosition> positions = new ArrayList<ScreenContentBoardPosition>();
  			
  			for (Ship ship: game.ships)
  			{
  				if (ship.isToBeDeleted())
  					continue;
  				
  				if (!ship.isPlayerInvolved(playerIndex))
  				{
  					ScreenContentBoardPosition position = new ScreenContentBoardPosition(
		 	 					ship.getPositionOnDay(0), 
		 	 					game.players[ship.getOwner()].getColorIndex(),
		 	 					ship.getScreenDisplaySymbol(),
		 	 					ship.hashCode());
	 				
	 				positions.add(position);

	 				continue;
  				}
  				
  				if (ship.getType() == ShipType.PATROL && !ship.isTransfer())
  				{
  					continue;
  				}

  				ships.add(ship);
  				
  				ShipTravelTime travelTimeRemaining = ship.getTravelTimeRemaining();
  				travelTimeRemaining.ship = ship;
  				travelTimesRemaining.add(travelTimeRemaining);
  			}
  			
  			this.game.screenContent = new ScreenContent();
			chapter.table = new InventoryPdfTable(this.simple? 4 : 8);
			HashSet<Integer> planetIndicesHighlighted = new HashSet<Integer>();
 			ArrayList<Point> positionsMarked = new ArrayList<Point>();
			
			// Column headers
 			if (this.simple)
 			{
 				chapter.table.cells.add(SternResources.InventurAnzahl(false));
  				chapter.table.colAlignRight[0] = true;
  				
  				chapter.table.cells.add(SternResources.InventurStart(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurZiel(false));
  				chapter.table.colAlignRight[2] = false;
  				
  				chapter.table.cells.add(SternResources.InventurAnkunft(false));
  				chapter.table.colAlignRight[3] = true;
 			}
 			else
 			{
  				chapter.table.cells.add(SternResources.InventurAnzahl(false));
  				chapter.table.colAlignRight[0] = true;
  				
  				chapter.table.cells.add(SternResources.InventurTyp(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurKommandant(false));
  				chapter.table.colAlignRight[2] = false;
  				
  				chapter.table.cells.add(SternResources.InventurStart(false));
  				chapter.table.colAlignRight[3] = false;
  				
  				chapter.table.cells.add(SternResources.InventurZiel(false));
  				chapter.table.colAlignRight[4] = false;
  				
  				chapter.table.cells.add(SternResources.InventurFracht(false));
  				chapter.table.colAlignRight[5] = false;
  				
  				chapter.table.cells.add(SternResources.InventurAnkunft(false));
  				chapter.table.colAlignRight[6] = false;
  				
  				chapter.table.cells.add(SternResources.InventurBuendnis(false));
  				chapter.table.colAlignRight[7] = false;
 			}
			
			// Sort ships by time of arrival
 			Collections.sort(travelTimesRemaining, new ShipTravelTime());
			
			for (ShipTravelTime travelTimeRemaining: travelTimesRemaining)
			{
				Ship ship2 = travelTimeRemaining.ship;
				
				ScreenContentBoardLine line = new ScreenContentBoardLine(
 	 					ship2.getPositionStart(), 
 	 					ship2.getPositionDestination(), 
 	 					ship2.getPositionOnDay(0), 
 	 					this.game.players[ship2.getOwner()].getColorIndex(),
 	 					ship2.getScreenDisplaySymbol());
 				
 				lines.add(line);
 				
 				if (this.game.getPlanetIndexFromPosition(ship2.getPositionStart()) != Constants.NO_PLANET)
 					planetIndicesHighlighted.add(ship2.getPlanetIndexStart());
 				else
 					positionsMarked.add(ship2.getPositionStart());
 				
 				if (!ship2.isStopped())
 				{
  	 				if (this.game.getPlanetIndexFromPosition(ship2.getPositionDestination()) != Constants.NO_PLANET)
  	 					planetIndicesHighlighted.add(ship2.getPlanetIndexDestination());
  	 				else
  	 					positionsMarked.add(ship2.getPositionDestination());
 				}
 				
 				chapter.table.cells.add(
 						ship2.getType() == ShipType.FIGHTERS ?
 								Integer.toString(ship2.getCount()) :
 								"1");
 				
 				if (!this.simple)
 				{
  	 				switch (ship2.getType())
  	 				{
  	 				case FIGHTERS:
  	 					chapter.table.cells.add(SternResources.Raumer(false));
  	 					break;
  	 				case SCOUT:
  	 					chapter.table.cells.add(SternResources.InventurAufklaerer(false));
  	 					break;
  	 				case PATROL:
  	 					chapter.table.cells.add(SternResources.InventurPatrouilleTransfer(false));
  	 					break;
  	 				case TRANSPORT:
  	 					chapter.table.cells.add(SternResources.InventurTransporter(false));
  	 					break;
  	 				case MINE50:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger50(false));
  	 					break;
  	 				case MINE100:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger100(false));
  	 					break;
  	 				case MINE250:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger250(false));
  	 					break;
  	 				case MINE500:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger500(false));
  	 					break;
  	 				case MINESWEEPER:
  	 					if (ship2.isTransfer())
  	 						chapter.table.cells.add(SternResources.InventurMinenraeumerTransfer(false));
  	 					else
  	 						chapter.table.cells.add(SternResources.InventurMinenraeumer(false));
  	 					break;
					default:
						chapter.table.cells.add("");
						break;
  	 				}
  	 				
  	 				chapter.table.cells.add(this.game.players[ship2.getOwner()].getName());
 				}
 				
 				chapter.table.cells.add(Utils.padString(" " + game.getSectorNameFromPosition(ship2.getPositionStart()), 2));
 				
 				if (ship2.isStopped())
 					chapter.table.cells.add("??");
 				else
 					chapter.table.cells.add(Utils.padString(" " + game.getSectorNameFromPosition(ship2.getPositionDestination()), 2));
 				
 				if (!this.simple)
 				{
  	 				if (ship2.getType() == ShipType.TRANSPORT)
  	 				{
  	 					if (ship2.getCommandRoom() != null)
  	 						chapter.table.cells.add(SternResources.Kommandozentrale(false));
  	 					else
  	 						chapter.table.cells.add(
  	 								SternResources.InventurTransporterEe(
  	 										false, 
  	 										Integer.toString(ship2.getCount())));
  	 				}
  	 				else
  	 					chapter.table.cells.add("");
 				}
 				
 				travelTimeRemaining.year += this.game.year;
 				chapter.table.cells.add(travelTimeRemaining.toOutputString(false));
 				
 				if (!this.simple)
 				{
  	 				if (ship2.getType() == ShipType.FIGHTERS && ship2.isAlliance())
  	 				{
 						StringBuilder sb = new StringBuilder(this.game.players[ship2.getOwner()].getName() + ": "+
 											ship2.getFightersCount(ship2.getOwner()));

 						for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
 						{
 							if (playerIndex == ship2.getOwner() || ship2.getFightersCount(playerIndex) <= 0)
 								continue;

 							sb.append("\n" + this.game.players[playerIndex].getName() + ": "+
 											ship2.getFightersCount(playerIndex));
 						}
 						
 						chapter.table.cells.add(sb.toString());
  	 				}
  	 				else
  	 					chapter.table.cells.add("");
 				}
			}
			
			ArrayList<ScreenContentBoardPlanet> planets = this.gameInformation.getDefaultBoard(this.alliances(), planetIndicesHighlighted);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 							planets,
 							positionsMarked,
 							lines,
 							positions,
 							this.mines(),
 							null));
 			
 			chapter.screenContent = (ScreenContent)Utils.klon(this.game.screenContent);
 			chapter.screenContent.setPlanets(this.screenContentCopy.getPlanets());
 			
  			this.pdfData.chapters.add(chapter);
  		}
  		
  		private void patrols()
  		{
  			InventoryPdfChapter chapter = 
  					new InventoryPdfChapter(
  							SternResources.InventurPatrrouillenTitel(false),
  							SternResources.InventurKeinePatrouillen(false));
  			boolean found = false;
  			
  			HashSet<Integer> planetIndicesHighlighted = null;
  			ArrayList<ScreenContentBoardLine> lines = new ArrayList<ScreenContentBoardLine>();
  			ArrayList<ScreenContentBoardPosition> positions = new ArrayList<ScreenContentBoardPosition>();
  			Hashtable<String, Integer> patrolsPerLineCount = null;
  			
  			for (Ship ship: game.ships)
  			{
  				if (ship.isToBeDeleted()) 
  					continue;
  				
  				if (!ship.isPlayerInvolved(this.playerIndex ))
  				{
  					ScreenContentBoardPosition position = new ScreenContentBoardPosition(
		 	 					ship.getPositionOnDay(0), 
		 	 					game.players[ship.getOwner()].getColorIndex(),
		 	 					ship.getScreenDisplaySymbol(),
		 	 					ship.hashCode());
	 				
	 				positions.add(position);

	 				continue;
  				}
  				
  				if (!(ship.getType() == ShipType.PATROL && !ship.isTransfer()))
  					continue;
  				
  				if (!found)
  				{
  					found = true;
  					
  					this.game.screenContent = new ScreenContent();
  	  				
  	  				planetIndicesHighlighted = new HashSet<Integer>();
  	  	 			patrolsPerLineCount = new Hashtable<String, Integer>();  
  				}
  				
  				planetIndicesHighlighted.add(ship.getPlanetIndexStart());
  				planetIndicesHighlighted.add(ship.getPlanetIndexDestination());
  				
				ScreenContentBoardLine line = new ScreenContentBoardLine(
 	 					ship.getPositionStart(), 
 	 					ship.getPositionDestination(), 
 	 					ship.getPositionOnDay(0), 
 	 					this.game.players[ship.getOwner()].getColorIndex(),
 	 					ship.getScreenDisplaySymbol());
 				
 				lines.add(line);
 				
 				String planetNameStart = Utils.padString("  " + game.getPlanetNameFromIndex(ship.getPlanetIndexStart()), 2);
 				String planetNameDestination = Utils.padString("  " + game.getPlanetNameFromIndex(ship.getPlanetIndexDestination()), 2);
 				
 				String lineConcat = ship.getPlanetIndexStart() < ship.getPlanetIndexDestination() ?
 							planetNameStart + planetNameDestination:
 							planetNameDestination + planetNameStart;
 							
 				if (patrolsPerLineCount.containsKey(lineConcat))
 					patrolsPerLineCount.replace(lineConcat, 
 							1 + patrolsPerLineCount.get(lineConcat));
 				else
 					patrolsPerLineCount.put(lineConcat, 1);
  			}
  			
  			if (patrolsPerLineCount != null)
  			{
  				chapter.table = new InventoryPdfTable(3);
  				
  				// Column headers
  				chapter.table.cells.add(SternResources.InventurPlanet1(false));
  				chapter.table.colAlignRight[0] = false;
  				
  				chapter.table.cells.add(SternResources.InventurPlanet2(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurPatrouillen(false));
  				chapter.table.colAlignRight[2] = true;
  				
  				ArrayList<String> patrolKeys = new ArrayList<String>(patrolsPerLineCount.keySet());
  				Collections.sort(patrolKeys);
  				
  				for (String patrolKey: patrolKeys)
  				{
  					chapter.table.cells.add(patrolKey.substring(0, 2));
  					chapter.table.cells.add(patrolKey.substring(2, 4));
  					
  					chapter.table.cells.add(patrolsPerLineCount.get(patrolKey).toString());
  				}
	 			
	  			ArrayList<ScreenContentBoardPlanet> planets = this.gameInformation.getDefaultBoard(null, planetIndicesHighlighted);
		 			
	 			this.game.screenContent.setBoard(
	 					new ScreenContentBoard(
	 							planets,
			 					null,
			 					lines,
			 					positions,
			 					null,
			 					null));
	 			
	 			chapter.screenContent = (ScreenContent)Utils.klon(this.game.screenContent);
	 			chapter.screenContent.setPlanets(this.screenContentCopy.getPlanets());
  			}
 			
  			this.pdfData.chapters.add(chapter);
  		}

  		private void planets()
  		{
  			InventoryPdfChapter chapter = 
  					new InventoryPdfChapter(
  							simple ? SternResources.InventurPlanetenTitelSimpel(false) : 
  								SternResources.InventurPlanetenTitel(false),
  							SternResources.InventurKeinePlaneten(false));
  			
  			this.game.screenContent = new ScreenContent();
  			
  			chapter.table = new InventoryPdfTable(simple ? 3 : 18);
				
			// Columns headers
  			if (this.simple)
  			{
  				chapter.table.cells.add(SternResources.InventurPlanet(false));
				chapter.table.colAlignRight[0] = false;
				
				chapter.table.cells.add(SternResources.InventurRaumerproduktionJahr(false));
				chapter.table.colAlignRight[1] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumer(false));
				chapter.table.colAlignRight[2] = true;
  			}
  			else
  			{
	  			chapter.table.cells.add(SternResources.InventurPlanetKurz(false));
				chapter.table.colAlignRight[0] = false;
				
				chapter.table.cells.add(SternResources.InventurBesitzerKurz(false));
				chapter.table.colAlignRight[1] = false;
				
				chapter.table.cells.add(SternResources.InventurEnergievorratKurz(false));
				chapter.table.colAlignRight[2] = true;
				
				chapter.table.cells.add(SternResources.InventurEnergieproduktionKurz(false));
				chapter.table.colAlignRight[3] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumerproduktionKurz(false));
				chapter.table.colAlignRight[4] = true;
				
				chapter.table.cells.add(SternResources.InventurFestungKurz(false));
				chapter.table.colAlignRight[5] = true;
				
				chapter.table.cells.add(SternResources.InventurFestungRaumerKurz(false));
				chapter.table.colAlignRight[6] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumerKurz(false));
				chapter.table.colAlignRight[7] = true;
				
				chapter.table.cells.add(SternResources.InventurAufklaererKurz(false));
				chapter.table.colAlignRight[8] = true;
				
				chapter.table.cells.add(SternResources.InventurTransporterKurz(false));
				chapter.table.colAlignRight[9] = true;
				
				chapter.table.cells.add(SternResources.InventurPatrouilleKurz(false));
				chapter.table.colAlignRight[10] = true;
				
				chapter.table.cells.add(SternResources.InventurMinenraeumerKurz(false));
				chapter.table.colAlignRight[11] = true;
				
				chapter.table.cells.add(SternResources.InventurMine50Kurz(false));
				chapter.table.colAlignRight[12] = true;
				
				chapter.table.cells.add(SternResources.InventurMine100Kurz(false));
				chapter.table.colAlignRight[13] = true;
				
				chapter.table.cells.add(SternResources.InventurMine250Kurz(false));
				chapter.table.colAlignRight[14] = true;
				
				chapter.table.cells.add(SternResources.InventurMine500Kurz(false));
				chapter.table.colAlignRight[15] = true;
				
				chapter.table.cells.add(SternResources.InventurKommandozentraleKurz(false));
				chapter.table.colAlignRight[16] = true;
				
				chapter.table.cells.add(SternResources.InventurBuendnisKurz(false));
				chapter.table.colAlignRight[17] = false;
  			}
  			  			
  			for (int index = 0; index < game.planetsCount; index++)
  			{
  				int planetIndex = game.getPlanetsSorted()[index];
  				
  				if (game.planets[planetIndex].getOwner() != playerIndex &&
  					!game.planets[planetIndex].isAllianceMember(playerIndex) &&
  					!game.planets[planetIndex].hasRadioStation(playerIndex))
  					continue;
  				
  				boolean playerIsOwner = (game.planets[planetIndex].getOwner() == playerIndex ||
  								  game.planets[planetIndex].hasRadioStation(playerIndex));
  				Planet planet = game.planets[planetIndex];
  				
  				chapter.table.cells.add(Utils.padString(" " + game.getPlanetNameFromIndex(planetIndex), 2));
  				
  				if (!simple)
  				{
  					chapter.table.cells.add(planet.getOwner() == Constants.NEUTRAL ?
  	  						"" :
  	  						game.players[planet.getOwner()].getName());
  					
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getMoneySupply()) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getMoneyProduction()) : "?");
  				}
  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getFighterProduction()) : "?");
  				
  				if (!simple)
  				{
  					chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getDefenceShieldFactor()) : "?");
  					chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getDefenceShieldFightersCount()) : "?");
  				}
  				chapter.table.cells.add(Utils.convertToString(planet.getShipsCount(ShipType.FIGHTERS)));
  				
  				if (!simple)
  				{
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.SCOUT)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.TRANSPORT)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.PATROL)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.MINESWEEPER)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.MINE50)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.MINE100)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.MINE250)) : "?");
	  				chapter.table.cells.add(playerIsOwner ? Utils.convertToString(planet.getShipsCount(ShipType.MINE500)) : "?");
	  				
	  				if (playerIsOwner)
		  				chapter.table.cells.add((planet.getCommandRoom() == null ?
		  						"" : "X"));
	  				else
	  					chapter.table.cells.add("?");
	
					if (planet.allianceExists())
					{
						StringBuilder sb = new StringBuilder(this.game.players[planet.getOwner()].getName() + ": "+
											planet.getFightersCount(planet.getOwner()));
	
						for (int playerIndex = 0; playerIndex < game.playersCount; playerIndex++)
						{
							if (playerIndex == planet.getOwner() || !planet.isAllianceMember(playerIndex))
								continue;
	
							sb.append("\n" + this.game.players[playerIndex].getName() + ": "+
											planet.getFightersCount(playerIndex));
						}
						
						chapter.table.cells.add(sb.toString());
					}
					else
						chapter.table.cells.add("");
  				}
  			}
  			
			ArrayList<ScreenContentBoardPlanet> planets = this.gameInformation.getDefaultBoard(this.alliances(), null);
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(
 						planets,
	 					null,
	 					null,
	 					null,
	 					this.mines(),
	 					null));
 			
 			chapter.screenContent = (ScreenContent)Utils.klon(this.game.screenContent);
 			chapter.screenContent.setPlanets(this.screenContentCopy.getPlanets());
  			this.pdfData.chapters.add(chapter);
  		}
  		
  	  	private ArrayList<ScreenContentBoardMine> mines()
  	  	{
  	  		ArrayList<ScreenContentBoardMine> mines = new ArrayList<ScreenContentBoardMine>();
  	  		
			if (game.mines != null)
			{
  	  			for (Mine mine: game.mines.values())
  	  			{
  					mines.add(new ScreenContentBoardMine(
  							mine.getPositionX(),
  							mine.getPositionY(),
  							mine.getStrength()));
  				}
			}
  	  		
  	  		return mines;
  	  	}

  	  	private Hashtable<Integer, ArrayList<Byte>> alliances()
  	  	{
  	  		Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
  	  		
  	  		for (int planetIndex = 0; planetIndex < this.game.planetsCount; planetIndex++)
  	  		{
  	  			Planet planet = this.game.planets[planetIndex];
  	  			if (!planet.allianceExists() || !planet.isAllianceMember(this.playerIndex))
  	  				continue;
  	  			
  	  			ArrayList<Byte> frameColors = new ArrayList<Byte>(); 
  	  			for (int playerIndex = 0; playerIndex < this.game.playersCount; playerIndex++)
  	  				if (playerIndex != planet.getOwner() && planet.isAllianceMember(playerIndex))
  	  					frameColors.add(this.game.players[playerIndex].getColorIndex());
  	  			
  	  			frames.put(planetIndex, frameColors);
  	  		}
			return frames;
  	  	}
  	}
  	  	
  	// =========================================
  	
  	private class DistanceMatrix
  	{
  		private Game game;
  		private InventoryPdfData pdfData;
  		private GameInformation gameInformation;
  		private ScreenContent screenContentCopy;
  		
  		private DistanceMatrix(Game game)
  		{
  			this.game = game;
  			
  			this.game.console.setHeaderText(
					this.game.mainMenuGetYearDisplayText() + " -> "+SternResources.Hauptmenue(true)+" -> "+SternResources.Entfernungstabelle(true),
					Colors.NEUTRAL);

  			this.game.console.appendText(SternResources.PdfOeffnenFrage(true) + " ");
  			
			ConsoleInput input = this.game.console.waitForKeyPressedYesNo(false);
			
			if (input.getInputText().equals(Console.KEY_YES))
			{
				byte[] pdfBytes = this.create(input.getLanguageCode());
				boolean success = false;
				
				if (input.getClientId() == null)
					success = PdfLauncher.showPdf(pdfBytes);
				else
					success = game.gameThread.openPdf(pdfBytes, input.getClientId());
				
				if (success)
					this.game.console.appendText(SternResources.InventurPdfGeoeffnet(true));
				else
					this.game.console.appendText(SternResources.InventurPdfFehler(true));
				
				this.game.console.lineBreak();
			}
			else
			{
				this.game.console.outAbort();
			}
  		}
  		
  		private byte[] create(String languageCode)
  		{
  			this.screenContentCopy = (ScreenContent)Utils.klon(this.game.screenContent);
  			
  			String languageCodeCopy = SternResources.getLocale();
  			SternResources.setLocale(languageCode);
  			
  			this.gameInformation = new GameInformation();
  			this.gameInformation.setGame(this.game);
  			
  			this.pdfData = new InventoryPdfData(
  					"",
  					this.game.year,
  					this.game.yearMax,
  					0,
  					true);
  			
  			this.createData();
  			
  			this.game.screenContent = screenContentCopy;
  			
  			byte[] pdfByteArray = null;			
 			try
 			{
 				pdfByteArray = InventoryPdf.create(pdfData);
 			}
 			catch (Exception e)
 			{
 			}
 			
 			SternResources.setLocale(languageCodeCopy);
 			
 			return pdfByteArray;
  		}
  		
  		private void createData()
  		{
  			InventoryPdfChapter chapter = 
  					new InventoryPdfChapter(SternResources.Entfernungstabelle(false), "");
  			
  			this.game.screenContent = new ScreenContent();
  			
  			chapter.table = new InventoryPdfTable(this.game.planetsCount + 1);
  			chapter.table.highlightFirstColumn = true;
  			chapter.table.smallFont = true;
  			
  			chapter.table.cells.add("");
			chapter.table.colAlignRight[0] = true;
			
  			for (int i = 0; i < this.game.planetsCount; i++)
  			{
  				int planetIndex = game.getPlanetsSorted()[i];
  				chapter.table.cells.add(Utils.padString(" "+this.game.getPlanetNameFromIndex(planetIndex), 2));
  				chapter.table.colAlignRight[i+1] = false;
  			}
  			
  			for (int i = 0; i < this.game.planetsCount; i++)
  			{
  				int planetIndexStart = game.getPlanetsSorted()[i];
  				
  				chapter.table.cells.add(this.game.getPlanetNameFromIndex(planetIndexStart));
  				
  				for (int j = 0; j < this.game.planetsCount; j++)
  				{
  					int planetIndexDestination = game.getPlanetsSorted()[j];
  					
  					chapter.table.cells.add(
  							planetIndexStart != planetIndexDestination ?
  										this.game.getDistanceMatrix()[planetIndexStart][planetIndexDestination].toOutputStringDistanceMatrix(false) :
  										"");
  				}
  			}
  			
			ArrayList<ScreenContentBoardPlanet> planets = this.getBoard();
 			
 			this.game.screenContent.setBoard(
 					new ScreenContentBoard(planets,
 					null,
 					null,
 					null,
 					null,
 					null));
 			
 			chapter.screenContent = (ScreenContent)Utils.klon(this.game.screenContent);
 			chapter.screenContent.setMode(ScreenContent.MODE_DISTANCE_MATRIX);
  			this.pdfData.chapters.add(chapter);
  		}
  		
  		private ArrayList<ScreenContentBoardPlanet> getBoard()
  		{
  			ArrayList<ScreenContentBoardPlanet> planets = new ArrayList<ScreenContentBoardPlanet>(this.game.planetsCount);
 			
 			for (int planetIndex = 0; planetIndex < this.game.planetsCount; planetIndex++)
 			{
 				planets.add(new ScreenContentBoardPlanet(
							this.game.getPlanetNameFromIndex(planetIndex),
							this.game.planets[planetIndex].getPosition(),
							Colors.BLACK,
							null)); 				
 			}
 			
 			if (this.game.screenContent == null)
 				this.game.screenContent = new ScreenContent();
 			
 			return planets;
  		}
  		
  	}
  	  	
  	// =========================================
  	
  	private static class AI
  	{
  		private static Vector<MoveAi> calc (Game gameCopy, int playerIndex)
  		{
  			Vector<MoveAi> moves = new Vector<MoveAi>();
  			
  			gameCopy.getDistanceMatrix();
  			
  			int score[] = new int [gameCopy.planetsCount];

  			score = evaluateInitialSituation(gameCopy, playerIndex, gameCopy.distanceMatrixYears);
  			
  			Vector<MoveAi> movesAi;
  			
  			int planetIndexStart, planetIndexDestination, distanceCurrent, fightersCountTotal = 0, fightersCountMin = 0;
  			short fightersCount = 0;
  			double	offenderExtra;
  			
  			int scoreSequence[] = Utils.sortValues(score, true);
  			
  			for (int t = 0; t < scoreSequence.length; t++)
  			{
  				planetIndexDestination = scoreSequence[t];
  				Planet planetDestination = gameCopy.planets[planetIndexDestination];
  				
  				if (score[planetIndexDestination] < 0)
  					continue;
  						
  				int distPl[] = Utils.sortValues(gameCopy.distanceMatrixYears[planetIndexDestination], false);
  				
  				distanceCurrent = -1;
  				fightersCountTotal = 0;
  				
  				movesAi = new Vector<MoveAi>();
  				
  				offenderExtra = 1. + (double)Utils.getRandomInteger(Constants.AI_ATTACK_FACTOR_RND_MAX) / 100.;
  				
  				for (int tt = 0; tt < gameCopy.distanceMatrixYears.length; tt++)
  				{
  					planetIndexStart = distPl[tt];
  					Planet planetStart = gameCopy.planets[planetIndexStart];
  					
  					if (planetIndexStart == planetIndexDestination || planetStart.getOwner() != playerIndex || score[planetIndexStart] > 0)
  						continue;
  					
  					if (distanceCurrent >= 0 && gameCopy.distanceMatrixYears[planetIndexDestination][planetIndexStart] != distanceCurrent)
  					{
  						movesAi = new Vector<MoveAi>();
  						fightersCountTotal = 0;
  					}
  					
  					distanceCurrent = gameCopy.distanceMatrixYears[planetIndexDestination][planetIndexStart];
  					fightersCount = 0;
  					fightersCountMin = 999999999;
  					
  					if (planetDestination.getOwner() == playerIndex)
  						fightersCountMin = score[planetIndexDestination];
  					else
  					{		
  						if (planetDestination.getOwner() != Constants.NEUTRAL)
  							fightersCountMin = Utils.round(offenderExtra * ((double)(planetDestination.getShipsCount(ShipType.FIGHTERS)+planetDestination.getFighterProduction()*(distanceCurrent+1))) * Constants.AI_ATTACK_FACTOR);
  						else
  							fightersCountMin = Utils.round(offenderExtra * (Constants.AI_FIGHTERS_NEUTRAL_COUNT_MIN+(double)planetDestination.getFighterProduction()*(gameCopy.year+distanceCurrent+1) * Constants.AI_ATTACK_FACTOR));
  					}

  					if (score[planetIndexStart] < 0)
  					{
  						fightersCount = (short)Math.min(planetStart.getShipsCount(ShipType.FIGHTERS), -Utils.round(((double)score[planetIndexStart] / Constants.AI_THREAT_FACTOR)));
  						fightersCount = (short)Math.min(fightersCount, fightersCountMin);
  					}

  					if (fightersCount > 0)
  					{
  						MoveAi moveAi = new MoveAi();
  						
  						moveAi.planetIndexStart = planetIndexStart;
  						moveAi.planetIndexDestination  = planetIndexDestination;
  						moveAi.fightersCount = fightersCount;
  						
  						movesAi.add(moveAi);
  						
  						fightersCountTotal += fightersCount;
  					}
  					
  					if (fightersCountTotal >= fightersCountMin)
  						break;
  				}
  				
  				if (fightersCountTotal >= fightersCountMin && movesAi.size() > 0)
  				{
  					for (MoveAi moveAi: movesAi)
  		  			{
  						moves.add(moveAi);
  						
  						Ship ship = new Ship(
  								moveAi.planetIndexStart,
  								moveAi.planetIndexDestination,
  								gameCopy.planets[moveAi.planetIndexStart].getPosition(),
  								gameCopy.planets[moveAi.planetIndexDestination].getPosition(),
  								ShipType.FIGHTERS,
  								moveAi.fightersCount,
  								playerIndex,
  								false,
  								true,
  								null,
  								null);
  						
  						gameCopy.ships.add(ship);
  						gameCopy.planets[moveAi.planetIndexStart].subtractFightersCount(gameCopy.playersCount, moveAi.fightersCount, playerIndex, false);
  						
  		  				score[moveAi.planetIndexStart] += Utils.round((double)moveAi.fightersCount * Constants.AI_THREAT_FACTOR
  		  						);
  		  				score[moveAi.planetIndexDestination]  -= moveAi.fightersCount;
  		  			}
  				}
  			}

  			return moves;
  		}
  		
  		private static int[] evaluateInitialSituation(Game gameCopy, int playerIndex, int[][] distances)
  		{
  			int score[] = new int[gameCopy.planetsCount];
  			
  			int		years = 0;
  			
  			double 	valueDistance = 0.,
  					valueFighters = 0.;
  			
  			for (int planetIndexDestination = 0; planetIndexDestination < gameCopy.planetsCount; planetIndexDestination++)
  			{
  				valueFighters = 0.;
  				int diff = 0;
  				
  				Planet planetDestination = gameCopy.planets[planetIndexDestination];
  				
  				for (int planetIndexStart = 0; planetIndexStart < gameCopy.planetsCount; planetIndexStart++)
  				{
  					Planet planetStart = gameCopy.planets[planetIndexStart];
  					if (planetDestination.getOwner() == planetStart.getOwner() || planetStart.getOwner() == Constants.NEUTRAL)
  						continue;
  					
  					years = distances[planetIndexStart][planetIndexDestination] + 1;
  					valueDistance = (double)Constants.AI_DISTANCE_FACTOR/(Math.pow((double)years, 2.));
  					
  					if (planetDestination.getOwner() != Constants.NEUTRAL)
  						diff = planetStart.getShipsCount(ShipType.FIGHTERS) - planetDestination.getShipsCount(ShipType.FIGHTERS); 
  					else
  						diff = planetStart.getShipsCount(ShipType.FIGHTERS) - (Constants.AI_FIGHTERS_NEUTRAL_COUNT_MIN + gameCopy.year * planetDestination.getFighterProduction());

  					valueFighters += (double)diff  * valueDistance;

  				}
  							
  				for (Ship ships: gameCopy.ships)
  				{
  					if (ships.getOwner() == playerIndex && 
  						ships.getType() == ShipType.FIGHTERS &&
  						ships.getPlanetIndexDestination() == planetIndexDestination)
  					{
  						if (planetDestination.getOwner() != Constants.NEUTRAL)
  							valueFighters -= ships.getCount();
  						else
  							valueFighters = -9999;
  					}
  				}
  				
  				score[planetIndexDestination] += Utils.round(valueFighters);
  				
  				if (planetDestination.getOwner() == playerIndex)
  					score[planetIndexDestination] -= planetDestination.getShipsCount(ShipType.FIGHTERS);
  				else
  				{
  					if (planetDestination.getOwner() != Constants.NEUTRAL)
  						score[planetIndexDestination] -= (planetDestination.getShipsCount(ShipType.FIGHTERS) + Constants.DEFENSE_SHIELD_FIGHTERS);
  				}
  			}
  			
  			return score;
  		}  		
  	}
  	
  	public void setEnableParameterChange(boolean enabled)
  	{
  		this.enableParameterChange = enabled;
  		this.gameThread.checkMenueEnabled();
  	}
  	
  	public boolean isParameterChangeEnabled()
  	{
  		return this.enableParameterChange;
  	}
  	
  	public int importMovesFromEmail(MovesTransportObject movesTransportObject)
  	{
		int playerIndex = -1;
		
		for (int i = 0; i < this.playersCount; i++)
		{
			if (this.playerReferenceCodes[i].equals(movesTransportObject.getPlayerReferenceCode()))
			{
				playerIndex = i;
				
				this.moves.put(playerIndex, movesTransportObject.getMoves());
				break;
			}
		}
				
		return playerIndex;
  	}
  	
  	@SuppressWarnings("unchecked")
	public boolean startEvaluationServer()
  	{
  		boolean allPlayersHaveEnteredMoves = true;
  		
  		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
  		{
  			Player player = this.players[playerIndex];
  			
  			if (!player.isBot() && !this.moves.containsKey(playerIndex))
  			{
  				allPlayersHaveEnteredMoves = false;
  				break;
  			}
  		}
  		
  		if (allPlayersHaveEnteredMoves)
  		{
  			Planet[] planetsCopy = (Planet[])Utils.klon(this.planets);
			ArrayList<Ship> shipsCopy = (ArrayList<Ship>)Utils.klon(this.ships);
  			
			for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			{
				Player player = this.players[playerIndex];
				
				if (!this.moves.containsKey(playerIndex))
				{
					if (player.isBot())
					{
						Game.enterMovesBot(this, playerIndex);

		 				this.planets = (Planet[])Utils.klon(planetsCopy);
		 				this.ships = (ArrayList<Ship>)Utils.klon(shipsCopy);
						continue;
					}
				}
			}

  			this.console = new Console(this, true);
  			
			this.updateBoard();
			this.updatePlanets(false);
  			
	  		new Evaluation(this);
	  		
	  		this.checkIsGameFinalized(true);
  		}
  		
  		return allPlayersHaveEnteredMoves;
  	}
  	
  	private static void enterMovesBot(Game game, int playerIndex)
  	{
  		Vector<MoveAi> movesAi = AI.calc(game, playerIndex);
			
		ArrayList<Move> moves = new ArrayList<Move>();
		game.moves.put(playerIndex, moves);
			
		for (MoveAi moveAi: movesAi)
		{
			Ship ship = new Ship(
					moveAi.planetIndexStart,
					moveAi.planetIndexDestination,
					game.planets[moveAi.planetIndexStart].getPosition(),
					game.planets[moveAi.planetIndexDestination].getPosition(),
					ShipType.FIGHTERS,
					moveAi.fightersCount,
					playerIndex,
					false,
					true,
					null,
					null);
			
			game.planets[moveAi.planetIndexStart].subtractFightersCount(game.playersCount, moveAi.fightersCount, playerIndex, false);
			
			game.moves.get(playerIndex).add(
					new Move(
							moveAi.planetIndexStart,
							ship,
							null));
		}
  	}
  	
  	public void setPlayerEmailAddress(String userId, String email)
  	{
  		int playerIndex = this.getPlayerIndexByName(userId);
  		
  		if (playerIndex >= 0)
  		{
  			this.players[playerIndex].setEmail(email);
  		}
  	}
  	
  	private int getPlayerIndexByName(String userId)
  	{
  		int playerIndex = 1;
  		
  		for (int i = 0; i < this.playersCount; i++)
  		{
  			if (this.players[i].getName().equals(userId))
  			{
  				playerIndex = i;
  				
  				break;
  			}
  		}
  		
  		return playerIndex;
  	}
  	
  	public void removePlayerFromServerGame(String userId)
  	{
  		int playerIndex = this.getPlayerIndexByName(userId);
  		
		this.players[playerIndex].setName(Constants.PLAYER_DELETED_NAME);
		this.players[playerIndex].setEmail("");
  		  		
  		if (!this.finalized)
  		{
  			for (int i = this.ships.size() - 1; i >= 0; i--)
  			{
  				Ship ship = this.ships.get(i);
  				
  				if (ship.getOwner() == playerIndex && ship.getStopLabel() != null)
  					this.ships.remove(i);
  			}
  			
			Ship ship = new Ship(
					0,
					0,
					null,
					null,
					ShipType.CAPITULATION,
					1,
					playerIndex,
					false,
					true,
					null,
					null); 				

  			ArrayList<Move> moves = new ArrayList<Move>();
  	
			moves.add(
					new Move(
							0,
							ship,
							null));

			this.moves.put(playerIndex, moves);
  			
  			this.startEvaluationServer();
  		}
  	}
  	
  	public void migrate()
  	{
  		boolean isMigrationRequired =
  				this.buildSaved == null ||
  				(!this.buildSaved.equals(Constants.BUILD_NO_INFO) && 
  				  this.buildSaved.compareTo(Constants.BUILD_MIGRATION) < 0);
  		
  		if (!isMigrationRequired)
  			return;
  		
  		this.replayLast = new ArrayList<ScreenContent>();
  		
  		if (this.ships != null)
  		{
	  		for (int i = this.ships.size() - 1; i>= 0; i--)
			{
				Ship ships = this.ships.get(i);
	
				if (ships.getOwner() == Constants.NEUTRAL)
				{
					this.ships.remove(i);
					continue;
				}
			}
  		}
  	}
  	
  	public void updateSaveBuild()
  	{
  		this.buildSaved = ReleaseGetter.getRelease();
  	}
  	
  	private boolean evaluationExists()
  	{
  		return (this.replayLast != null && this.replayLast.size() > 0);
  	}
  	
  	private PlanetInputStruct getPlanetInput(String label, boolean hidden, int allowedInput)
  	{
  		ArrayList<ConsoleKey> allowedKeys = new ArrayList<ConsoleKey>();			  		
  		
  		do
  		{
	  		this.console.appendText(label+": ");
	
			ConsoleInput input = this.console.waitForTextEntered(Constants.PLANET_NAME_LENGTH_MAX, allowedKeys, hidden, true);
	
			if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.console.outAbort();
				return null;
			}
	
			if (allowedInput == PlanetInputStruct.ALLOWED_INPUT_SECTOR)
			{
				Point positionDestination = this.getPositionFromSectorName(input.getInputText());
				
				if (positionDestination != null)
				{
					return new PlanetInputStruct(positionDestination, this.getPlanetIndexFromName(input.getInputText()));
				}
			}
			
			if (allowedInput == PlanetInputStruct.ALLOWED_INPUT_PLANET)
			{
				int planetIndexDestination = this.getPlanetIndexFromName(input.getInputText());
				
				if (planetIndexDestination != Constants.NO_PLANET)
				{
					return new PlanetInputStruct(planetIndexDestination);
				}
			}
			
			this.console.outInvalidInput();

		}
  		
		while (true);

  	}
}