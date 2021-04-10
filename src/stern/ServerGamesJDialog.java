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

package stern;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;

import common.Colors;
import common.Constants;
import common.Game;
import common.Game.PlanetInfo;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageGameHostDeleteGame;
import commonServer.RequestMessageGameHostFinalizeGame;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonUi.ButtonDark;
import commonUi.ComboBoxDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;
import common.GameInfo;
import common.GameOptions;
import common.Player;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial")
class ServerGamesJDialog extends JDialog implements ActionListener, IColorChooserCallback
{
	private JTabbedPane tabpane;
	private PanelDark panNewGame;
	
	private ButtonDark butClose;
	private ButtonDark butNewGamenewBoard;
	private ButtonDark butNewGameSubmit;
	
	private BoardDisplay panBoardDisplay;
	
	private ComboBoxDark<String>[] comboPlayersArray;
	private PanelDark[] panPlayers;
	private CanvasPlayerColors[] canvasPlayersColors;
	private ArrayList<Player> players;
	private HashSet<GameOptions> options;
	
	private Hashtable<GameOptions,CheckBoxDark> cbOptions;
	private ComboBoxDark<String> comboYearLast;
	private ComboBoxDark<String> comboPlayers;
	private TextFieldDark tfGameName;
	
	Game gameLoaded;
	
	private ClientUserCredentials cuc;
	
	private ResponseMessageGamesAndUsers gamesAndUsers;
	
	private Game game;
	
	private Stern parent;
	
	private static Font font;
	
	private final static String ENDLESS_GAME_STRING = SternResources.SpielparameterJDialogUnendlich(false);
	
	ServerGamesJDialog(
			Stern parent,
			String title,
			String currentGameId,
			ClientUserCredentials cuc,
			ResponseMessageGamesAndUsers gamesAndUsers)
	{
		super (parent, title, true);

		this.parent = parent;
		this.cuc = cuc;
		this.gamesAndUsers = gamesAndUsers;
		
		font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		this.tabpane = new JTabbedPane
	            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
				
		// ---------------
		if (this.gamesAndUsers.gamesWaitingForEnterMoves.size() > 0)
		{
			PanelGameSelector panWaitingForEnterMoves = new PanelGameSelector(
					this.gamesAndUsers.gamesWaitingForEnterMoves, false, currentGameId);
			tabpane.add(SternResources.ServerGamesSpielerWarten(false), panWaitingForEnterMoves);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesWaitingForOtherPlayers.size() > 0)
		{
			PanelGameSelector panWaitingForOtherPlayers = new PanelGameSelector(
					this.gamesAndUsers.gamesWaitingForOtherPlayers, false, currentGameId);
			tabpane.add(SternResources.ServerGamesIchWarte(false), panWaitingForOtherPlayers);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesFinalized.size() > 0)
		{
			PanelGameSelector panGamesFinalized = new PanelGameSelector(
					this.gamesAndUsers.gamesFinalized, false, currentGameId);
			tabpane.add(SternResources.ServerGamesBeendeteSpiele(false), panGamesFinalized);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesGameHost.size() > 0)
		{
			PanelGameSelector panGamesHost = new PanelGameSelector(
					this.gamesAndUsers.gamesGameHost, true, currentGameId);
			tabpane.add(SternResources.ServerGamesSpielleiteraktionen(false), panGamesHost);
		}

		// ---------------
		this.panNewGame = this.getPanelNewGame();
		tabpane.add(SternResources.ServerGamesNeuesSpiel(false), this.panNewGame);
		
		// ----
		panBase.add(tabpane, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butClose = new ButtonDark(this, SternResources.Schliessen(false), font);
		panButtons.add(this.butClose);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panShell, BorderLayout.CENTER);
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				setVisible(false);
				dispose();
			}
		}
		);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().setDefaultButton(this.butClose);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (source == this.butClose || source == this.getRootPane())
			this.close();
		else if (source == this.butNewGameSubmit)
			this.submitGame();
		else if (source == this.comboPlayers || 
				source == this.butNewGamenewBoard)
			this.updateGame();
		else if (source.getClass() == ComboBoxDark.class)
		{
			for (int playerIndex = 1; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
			{
				if (source == this.comboPlayersArray[playerIndex])
				{
					String selectedUser = (String)this.comboPlayersArray[playerIndex].getSelectedItem();
					if (selectedUser.equals(""))
						continue;
					
					for (int playerIndex2 = 1; playerIndex2 < Constants.PLAYERS_COUNT_MAX; playerIndex2++)
					{
						int otherPlayerIndex = (playerIndex + playerIndex2) % Constants.PLAYERS_COUNT_MAX;
						String otherPlayerName = 
								(String)this.comboPlayersArray[otherPlayerIndex].getSelectedItem();
						
						if (otherPlayerName.equals(selectedUser))
							this.comboPlayersArray[otherPlayerIndex].setSelectedItem("");
					}
					break;
				}
			}
		}
	}
	
	private ResponseMessage sendAndReceive(ClientUserCredentials cuc, RequestMessage msg)
	{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ResponseMessage respMsg = ClientSocketManager.sendAndReceive(cuc, msg);
		this.setCursor(Cursor.getDefaultCursor());
		
		if (respMsg.error)
			DialogWindow.showError(
					this,
				    SternResources.getString(respMsg.errorMsg),
				    SternResources.Verbindungsfehler(false));
		
		return respMsg;
	}
	
	private void selectGame(String gameId)
	{
		RequestMessage msg = new RequestMessage(RequestMessageType.GET_GAME);
		
		msg.payloadSerialized = gameId;
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			Gson gson = new Gson();
			this.gameLoaded = gson.fromJson(respMsg.payloadSerialized, Game.class); 
			this.close();
		}
	}
	
	private void deleteGame(String gameId)
	{
		DialogWindowResult dialogResult = DialogWindow.showYesNo(
				this,
			    SternResources.ServerGamesLoeschenAys(false, gameId),
			    SternResources.ServerGamesLoeschen(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		RequestMessage msg = new RequestMessage(RequestMessageType.GAME_HOST_DELETE_GAME);
		
		RequestMessageGameHostDeleteGame msgPayload = new RequestMessageGameHostDeleteGame();
		
		msgPayload.gameHostUserId = this.cuc.userId;
		msgPayload.gameId = gameId;
		
		msg.payloadSerialized = msgPayload.toJson();
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			DialogWindow.showInformation(
					this, 
					SternResources.ServerGamesGameDeleted(false, gameId), 
					SternResources.ServerGamesLoeschen(false));
			
			this.close();
		}
	}
	
	private void finalizeGame(String gameId)
	{
		DialogWindowResult dialogResult = DialogWindow.showYesNo(
				this,
			    SternResources.ServerGamesBeendenAys(false, gameId),
			    SternResources.ServerGamesBeenden(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		RequestMessage msg = new RequestMessage(RequestMessageType.GAME_HOST_FINALIZE_GAME);
		
		RequestMessageGameHostFinalizeGame msgPayload = new RequestMessageGameHostFinalizeGame();
		
		msgPayload.gameHostUserId = this.cuc.userId;
		msgPayload.gameId = gameId;
		
		msg.payloadSerialized = msgPayload.toJson();
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			DialogWindow.showInformation(
					this, 
					SternResources.ServerGamesGameFinalized(false, gameId), 
					SternResources.ServerGamesBeenden(false));
			
			this.close();
		}
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	@SuppressWarnings("unchecked")
	private PanelDark getPanelNewGame()
	{
		this.players = Game.getPlayersDefault();
		this.options = Game.getOptionsDefault();
		
		this.panPlayers = new PanelDark[Constants.PLAYERS_COUNT_MAX];
		this.comboPlayersArray = new ComboBoxDark[Constants.PLAYERS_COUNT_MAX];
		this.canvasPlayersColors = new CanvasPlayerColors[Constants.PLAYERS_COUNT_MAX];
		this.cbOptions = new Hashtable<GameOptions,CheckBoxDark>();		
		
		//
		PanelDark panShell = new PanelDark(new SpringLayout());
		
		PanelDark panBase = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panMain = new PanelDark(new GridLayout(6, 2, 20, 0));
		
		panMain.add(add(this.getPlayerPanel(0)));
		
		PanelDark panPlanetsSub1 = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panPlanetsSub1.add(new LabelDark(SternResources.Players(false), font));
		String[] players = new String[Constants.PLAYERS_COUNT_MAX - Constants.PLAYERS_COUNT_MIN + 1];
		for (int playerIndex = Constants.PLAYERS_COUNT_MIN; playerIndex <= Constants.PLAYERS_COUNT_MAX; playerIndex++)
			players[playerIndex-Constants.PLAYERS_COUNT_MIN] = Integer.toString(playerIndex);
		this.comboPlayers = new ComboBoxDark<String>(players, font);
		panPlanetsSub1.add(this.comboPlayers);
		
		panMain.add(panPlanetsSub1);
		
		panMain.add(add(this.getPlayerPanel(1)));
		
		PanelDark panYearMax = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panYearMax.add(new LabelDark(SternResources.SpielparameterJDialogSpieleBisJahr(false)+" ", font));
		String[] years = { ENDLESS_GAME_STRING, "15", "20", "30", "40", "50", "75", "100", "150", "200" };
		this.comboYearLast = new ComboBoxDark<String>(years, font);
		panYearMax.add(this.comboYearLast);
		panMain.add(panYearMax);
		
		panMain.add(add(this.getPlayerPanel(2)));
		
		PanelDark panGameName = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panGameName.add(new LabelDark(SternResources.ServerGamesSpielname(false), font));
		this.tfGameName = new TextFieldDark(font, 18);
		panGameName.add(this.tfGameName);
		panMain.add(panGameName);

		panMain.add(add(this.getPlayerPanel(3)));
		
		panMain.add(new LabelDark(font));

		panMain.add(add(this.getPlayerPanel(4)));
		
		panMain.add(new LabelDark(font));

		panMain.add(add(this.getPlayerPanel(5)));
				
		panMain.add(new LabelDark(font));

		// ----
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// ----
		PanelDark panBoard = new PanelDark(new BorderLayout(10, 10));
		
		this.panBoardDisplay = new BoardDisplay(false);
		panBoard.add(this.panBoardDisplay, BorderLayout.CENTER);
						
		panBase.add(panBoard, BorderLayout.EAST);
		
		// ----
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		this.butNewGamenewBoard = new ButtonDark(this, SternResources.ServerGamesNeuesSpielfeld(false), font);
		panButtons.add(this.butNewGamenewBoard);
		
		this.butNewGameSubmit = new ButtonDark(this, SternResources.ServerGamesSubmit(false), font);
		panButtons.add(this.butNewGameSubmit);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		this.comboPlayersArray[0].setSelectedItem(this.cuc.userId);
		this.comboPlayersArray[0].setEnabled(false);
		
		for (GameOptions option: this.cbOptions.keySet())
			this.cbOptions.get(option).setSelected(this.options.contains(option));
		
		if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			this.comboYearLast.setSelectedItem(Integer.toString(Constants.YEARS_COUNT_MAX_DEFAULT));
		else
			this.comboYearLast.setSelectedItem(ENDLESS_GAME_STRING);
		
		this.comboPlayers.setSelectedItem(Integer.toString(Constants.PLAYERS_COUNT_MAX));
		
		this.updateGame();
		
		this.comboPlayers.addActionListener(this);
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad

		return panShell;
	}

	@Override
	public void colorChanged(int playerIndex, byte newColorIndex, byte oldColorIndex) 
	{
		for (CanvasPlayerColors c: this.canvasPlayersColors)
		{
			if (c.playerIndex != playerIndex && c.colorIndex == newColorIndex)
			{
				c.setColor(oldColorIndex);
				break;
			}
		}
	}	
	
	private PanelDark getPlayerPanel(int playerIndex)
	{
		this.panPlayers[playerIndex] = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		canvasPlayersColors[playerIndex] = new CanvasPlayerColors(
				this, 
				this,
				playerIndex, 
				this.players.get(playerIndex).getColorIndex(),
				font);
		canvasPlayersColors[playerIndex].setPreferredSize(new Dimension(14,14));
		this.panPlayers[playerIndex].add(canvasPlayersColors[playerIndex]);
		
		String[] playerNames = null;
		
		if (playerIndex == 0)
			playerNames = new String[] {this.cuc.userId};
		else
		{
			playerNames = new String[this.gamesAndUsers.users.size()];
			playerNames[0] = "";
			
			int counter = 1;
			for (String userId: this.gamesAndUsers.users.keySet())
			{
				if (!userId.equals(this.cuc.userId))
				{
					playerNames[counter] = userId; 
					counter++;
				}
			}
		}

		this.comboPlayersArray[playerIndex] = new ComboBoxDark<String>(playerNames, font);
		this.comboPlayersArray[playerIndex].setPrototypeDisplayValue("WWWWWWWWWWWWWW");
		
		if (playerIndex != 0)
			this.comboPlayersArray[playerIndex].addActionListener(this);
			
		this.panPlayers[playerIndex].add(this.comboPlayersArray[playerIndex]);
		
		return this.panPlayers[playerIndex];
	}
		
	// ================================================
	
	private class BoardDisplay extends JPanel
	{
		private static final int PIXEL_PER_SECTOR = 10;
		private ArrayList<PlanetInfo> planetInfo;
		private boolean showOwners;
		
		public BoardDisplay(boolean showOwners)
		{
			super();
			
			this.showOwners = showOwners;
			
			this.setPreferredSize(new Dimension(
					(Constants.BOARD_MAX_X +1) * PIXEL_PER_SECTOR, 
					(Constants.BOARD_MAX_Y +1) * PIXEL_PER_SECTOR));
		}
		
		public void refresh(ArrayList<PlanetInfo> planetInfo)
		{
			this.planetInfo = planetInfo;
			this.repaint();
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = this.getSize();
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dim.width, dim.height);
			
			g.setColor(Color.WHITE);
			g.drawLine(0, 0, dim.width-1, 0);
			g.drawLine(0, 0, 0, dim.height-1);
			g.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
			g.drawLine(0, dim.height-1, dim.width, dim.height-1);
			
			if (this.planetInfo == null)
				return;
			
			int offset = PIXEL_PER_SECTOR / 2;
			
			for (Game.PlanetInfo plInfo: this.planetInfo)
			{
				if (this.showOwners)					
					g.setColor(Colors.get(plInfo.colorIndex));
				else
					g.setColor(plInfo.colorIndex == Colors.NEUTRAL ? 
							Colors.get(Colors.NEUTRAL) : 
							Color.white);
				
				int x = Utils.round(offset + plInfo.positionX * PIXEL_PER_SECTOR);
				int y = Utils.round(offset + plInfo.positionY * PIXEL_PER_SECTOR);
				
				g.fillOval(x, y, PIXEL_PER_SECTOR, PIXEL_PER_SECTOR);
			}
		}

	}
	
	private void updateGame()
	{
		int playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
		
		for (GameOptions option: this.cbOptions.keySet())
		{
			if (this.cbOptions.get(option).isSelected())
				this.options.add(option);
			else
				this.options.remove(option);
		}

		for (int playerIndex = 0; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
		{
			this.panPlayers[playerIndex].setVisible(playerIndex < playersCount);			
		}
		
		this.game = Game.create(
				this.options,
				this.getPlayers(playersCount),
				this.gamesAndUsers.emailGameHost,
				30);
		
		this.panBoardDisplay.refresh(this.game.getPlanetInfo());
	}
	
	private void submitGame()
	{
		String gameName = this.tfGameName.getText().trim();
		this.tfGameName.setText(gameName);
		
		if (gameName.length() < Constants.GAME_NAME_LENGTH_MIN ||
			gameName.length() > Constants.GAME_NAME_LENGTH_MAX ||
			!Pattern.matches(Constants.PLAYER_NAME_REGEX_PATTERN, gameName))
		{
			DialogWindow.showError(
					this,
					SternResources.ServerGamesSubmitSpielname(
							false, 
							gameName, 
							Integer.toString(Constants.GAME_NAME_LENGTH_MIN), 
							Integer.toString(Constants.GAME_NAME_LENGTH_MAX)),
					SternResources.Fehler(false));
			return;
		}
		
		this.game.setName(gameName);
		
		Player[] players = this.getPlayers(this.game.getPlayersCount());
		
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
		{
			Player player = players[playerIndex];
			if (player.getName().equals(""))
			{
				DialogWindow.showError(
						this,
						SternResources.ServerGamesSubmitNamenZuweisen(false),
					    SternResources.Fehler(false));
				return;
			}
			
			ResponseMessageGamesAndUsers.UserInfo userInfo = this.gamesAndUsers.users.get(player.getName());
			player.setEmail(userInfo.email);
		}
		
		DialogWindowResult dialogResult = DialogWindow.showOkCancel(
				this,
				SternResources.ServerGamesSubmitFrage(false, game.getName()),
				SternResources.ServerGamesSubmit(false));
		
		if (dialogResult != DialogWindowResult.OK)
			return;
		
		this.game.setPlayers(players);
		
		HashSet<GameOptions> options = this.game.getOptions();
		String yearsMaxString = (String)this.comboYearLast.getSelectedItem();
		
		options.remove(GameOptions.AUTO_SAVE);
		options.remove(GameOptions.EMAIL_BASED);
		options.add(GameOptions.SERVER_BASED);
		
		if (yearsMaxString.equals(ENDLESS_GAME_STRING))
		{
			this.game.setYearMax(0);
			options.remove(GameOptions.LIMITED_NUMBER_OF_YEARS);
		}
		else
		{
			this.game.setYearMax(Integer.parseInt(yearsMaxString));
			this.options.add(GameOptions.LIMITED_NUMBER_OF_YEARS);
		}
		
		this.game.setOptions(options);
		
		this.game.prepareYear();
		
		RequestMessage msg = new RequestMessage(RequestMessageType.POST_NEW_GAME);
		
		Gson gson = new Gson();
		msg.payloadSerialized = gson.toJson(this.game); 
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			ArrayList<Player> playersForEmail = new ArrayList<Player>();
			
			for (Player spieler: players)
			{
				if (spieler.getName().equals(this.cuc.userId))
					continue;
				
				playersForEmail.add(spieler);
			}
			
			if (playersForEmail.size() > 0)
			{
				DialogWindow.showInformation(
						this, 
						SternResources.ServerGamesSubmitAngelegt2(false, respMsg.payloadSerialized), 
						SternResources.ServerGamesSubmit(false));
				
				EmailCreatorJDialog dlg = new EmailCreatorJDialog(
						this, 
						game.getPlayers(),
						null,
						parent.emailSeparator,
						SternResources.EmailSubjectEingeladen(
								false, 
								this.cuc.userId, 
								this.game.getName()), 
						SternResources.EmailBodyEingeladen(
								false, 
								this.cuc.userId, 
								this.game.getName(), 
								this.cuc.url, 
								Integer.toString(this.cuc.port)));
				
				dlg.setVisible(true);
				
				if (dlg.launched)
				{
					parent.emailSeparator = dlg.separatorPreset;
					parent.setProperty(Stern.PROPERTY_EMAIL_SEPARATOR, parent.emailSeparator);
				}	
			}
			else
				DialogWindow.showInformation(
						this, 
						SternResources.ServerGamesSubmitAngelegt(false, respMsg.payloadSerialized), 
						SternResources.ServerGamesSubmit(false));
			
			this.selectGame(this.game.getName());
		}
	}
	
	private Player[] getPlayers(int playersCount)
	{
		Player[] players = new Player[playersCount];
		
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
			players[playerIndex] = new Player(
					(String)this.comboPlayersArray[playerIndex].getSelectedItem(),
					"", 
					this.canvasPlayersColors[playerIndex].colorIndex, 
					false);
		
		return players;
	}
	
	private class PanelGameSelector extends PanelDark implements 
			ActionListener,
			ListSelectionListener,
			MouseListener
	{
		private ButtonDark butOk;
		private ButtonDark butGameDelete;
		private ButtonDark butGameFinalize;
		
		private BoardDisplay board;
		
		private JList<String> listGames;
		
		private Hashtable<String,GameInfo> gamesDict;
		
		private PanelPlayer[] panelsPlayers;
		
		private GameInfo selectedGame;

		private LabelDark labPlayersAndPlanets;
		private LabelDark labYear;
		private LabelDark labDateStart;
		private LabelDark labUpdateLast;
		
		public PanelGameSelector(
				ArrayList<GameInfo> games, 
				boolean allowGameHostActions,
				String currentGameId)
		{
			super();
			
			this.setLayout(new SpringLayout());
			
			PanelDark panShell = new PanelDark(new BorderLayout(10, 10));
			
			PanelDark panMain = new PanelDark(new BorderLayout(20, 10));
			
			this.gamesDict = new Hashtable<String,GameInfo>();
			DefaultListModel<String> lm = new DefaultListModel<String>();
			
			int selectedIndex = -1;
			
			for (GameInfo info: games)
			{
				lm.addElement(info.name);
				this.gamesDict.put(info.name, info);
				
				if (currentGameId != null && info.name.equals(currentGameId))
					selectedIndex = lm.size() - 1;
			}
						
			this.listGames = new JList<String>(lm);
			
			listGames.setBackground(new Color(30, 30, 30));
			listGames.setForeground(Color.white);
			listGames.setFont(font);
			listGames.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			listGames.setLayoutOrientation(JList.VERTICAL);
			listGames.addMouseListener(this);
			listGames.setVisibleRowCount(-1);
			
			JScrollPane sc = new JScrollPane(listGames);
			sc.setBorder(BorderFactory.createLineBorder(Color.white));
			
			sc.setPreferredSize(new Dimension(125, 200));

			panMain.add(sc, BorderLayout.WEST);
			
			// ----
			
			PanelDark panGrid = new PanelDark(new GridLayout(6, 2, 0, 0));
			
			this.panelsPlayers = new PanelPlayer[Constants.PLAYERS_COUNT_MAX];
			
			this.panelsPlayers[0] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[0]);
			
			this.labPlayersAndPlanets = new LabelDark("6 players, 42 planets", font);
			panGrid.add(this.labPlayersAndPlanets);
			
			this.panelsPlayers[1] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[1]);
			
			this.labYear = new LabelDark("Year 1 of 30", font);
			panGrid.add(this.labYear);
			
			this.panelsPlayers[2] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[2]);
			
			this.labDateStart = new LabelDark("Started on 04/24/2019 00:00", font);
			panGrid.add(this.labDateStart);
			
			this.panelsPlayers[3] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[3]);
			
			this.labUpdateLast = new LabelDark("Last activity 04/24/2019", font);
			panGrid.add(this.labUpdateLast);
			
			this.panelsPlayers[4] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[4]);
			
			panGrid.add(new LabelDark(font));
			
			this.panelsPlayers[5] = new PanelPlayer();
			panGrid.add(this.panelsPlayers[5]);
			
			panGrid.add(new LabelDark("", font));
			
			panMain.add(panGrid, BorderLayout.CENTER);
			
			// ----
			
			this.board = new BoardDisplay(true);
			panMain.add(this.board, BorderLayout.EAST);
			
			panShell.add(panMain, BorderLayout.CENTER);
			// ----
			PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
			
			if (allowGameHostActions)
			{
				this.butGameFinalize = new ButtonDark(this, SternResources.ServerGamesBeenden(false), font);
				panButtons.add(this.butGameFinalize);
				
				this.butGameDelete = new ButtonDark(this, SternResources.ServerGamesLoeschen(false), font);
				panButtons.add(this.butGameDelete);
			}

			this.butOk = new ButtonDark(this, SternResources.ServerGamesLaden(false), font);
			panButtons.add(this.butOk);
			
			panShell.add(panButtons, BorderLayout.SOUTH);
			
			this.add(panShell);

			SpringUtilities.makeCompactGrid(this,
	                1, 1, //rows, cols
	                10, 10, //initialX, initialY
	                10, 10);//xPad, yPad
			
			this.setValues();
			listGames.addListSelectionListener(this);
			
			if (selectedIndex >= 0)
				listGames.setSelectedIndex(selectedIndex);
			else
				listGames.setSelectedIndex(0);
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (this.selectedGame != null)
			{
				if (e.getSource() == this.butOk)
				{
					selectGame(this.selectedGame.name);
				}
				else if (e.getSource() == this.butGameDelete)
				{
					deleteGame(this.selectedGame.name);
				}
				else if (e.getSource() == this.butGameFinalize)
				{
					finalizeGame(this.selectedGame.name);
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getSource() != this.listGames)
				return;
			
			this.selectedGame = this.gamesDict.get(listGames.getSelectedValue());
			
			this.setValues();

		}
		
		private void setValues()
		{
			for (int playerIndex = 0; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
			{
				boolean visible = this.selectedGame != null && 
						this.selectedGame.players.length -1 >= playerIndex;
				this.panelsPlayers[playerIndex].setVisible(visible);
				
				if (!visible)
					continue;
				
				Player player = this.selectedGame.players[playerIndex];
				
				this.panelsPlayers[playerIndex].cbEnterMovesFinished.setSelected(
						this.selectedGame.moveEnteringFinalized.contains(player.getName()));
				this.panelsPlayers[playerIndex].tfPlayerName.setText(player.getName());
				this.panelsPlayers[playerIndex].tfPlayerName.setForeground(
						Colors.get(player.getColorIndex()));
			}
			
			this.board.refresh(this.selectedGame == null ? null : this.selectedGame.planetInfo);
			
			this.labYear.setVisible(this.selectedGame != null);
			this.labPlayersAndPlanets.setVisible(this.selectedGame != null);
			this.labDateStart.setVisible(this.selectedGame != null);
			
			if (this.selectedGame != null)
			{
				if (this.selectedGame.yearMax == 0)
					this.labYear.setText(
							SternResources.InventurJahr1(
									false, 
									Integer.toString(this.selectedGame.year + 1)));
				else
					this.labYear.setText(
							SternResources.InventurJahr2(
									false,
									Integer.toString(this.selectedGame.year + 1),
									Integer.toString(this.selectedGame.yearMax)));
				
				this.labPlayersAndPlanets.setText(
						SternResources.ServerGamesSpielerPlaneten(
								false, 
								Integer.toString(this.selectedGame.players.length),
								Integer.toString(this.selectedGame.planetInfo.size())));
				
				this.labUpdateLast.setText(
						SternResources.ServerGamesLetzteAktivitaet(
								false, 
								Utils.formatDateString(
										Utils.convertMillisecondsToString(this.selectedGame.dateUpdate))));
				
				this.labDateStart.setText(
						SternResources.ServerGamesBegonnen(
								false, 
								Utils.formatDateString(
										Utils.convertMillisecondsToString(this.selectedGame.dateStart))));
				
			}
			
			this.board.repaint();
		}
		
		private class PanelPlayer extends PanelDark
		{
			public CheckBoxDark cbEnterMovesFinished;
			public TextFieldDark tfPlayerName;
			
			public PanelPlayer()
			{
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				
				this.cbEnterMovesFinished = new CheckBoxDark("", false, font);
				this.cbEnterMovesFinished.setEnabled(false);
				this.add(cbEnterMovesFinished);
				
				this.tfPlayerName = new TextFieldDark(font, 12);
				this.tfPlayerName.setEditable(false);
				this.add(this.tfPlayerName);				
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void mouseClicked(MouseEvent evt) 
		{
			JList<String> list = (JList<String>)evt.getSource();
	        if (evt.getClickCount() == 2)
	        {
	            int index = list.locationToIndex(evt.getPoint());
	            
	            if (index >= 0)
	            {
	            	DefaultListModel<String> lm = (DefaultListModel<String>)list.getModel();
	            	
	            	this.selectedGame = this.gamesDict.get(lm.get(index));
	            	selectGame(this.selectedGame.name);
	            }
	        }
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
}
