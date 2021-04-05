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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Constants;
import common.Game;
import common.GameOptions;
import common.Player;
import common.SternResources;
import common.Utils;
import commonServer.ServerConstants;
import commonUi.ButtonDark;
import commonUi.ComboBoxDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") 
class GameParametersJDialog extends JDialog implements ActionListener, IColorChooserCallback
{
	private ArrayList<Player> players;
	private String emailGameHost;
	private int playersCount;
	private int yearMax;
	private HashSet<GameOptions> options;
	private boolean abort;
	
	private GameParametersDialogMode mode;
	
	private PanelDark[] panPlayers;
	private TextFieldDark[] tfPlayer;
	private CheckBoxDark[] cbPlayer;
	private CanvasPlayerColors[] canvasPlayerColors;
	private Hashtable<GameOptions,CheckBoxDark> cbOptions;
	private ComboBoxDark<String> comboYearLast;
	private ComboBoxDark<String> comboPlayers;
	private ButtonDark butOk;
	private ButtonDark butCancel;
	private ButtonDark butEmailConfiguration;
	private static Font font;
	private ArrayList<String> emails;
	
	private final static String ENDLESS_GAME_STRING = SternResources.SpielparameterJDialogUnendlich(false);
	
	GameParametersJDialog (
			Frame parent,
			String title,
			GameParametersDialogMode mode,
			Game game,
			ArrayList<String> emails)
	{
		super (parent, title, true);
		
		font = DialogFontHelper.getFont();
		
		this.mode = mode;
		this.emails = emails;
		
		this.getInitialValues(game);
		
		this.panPlayers = new PanelDark[Constants.PLAYERS_COUNT_MAX];
		this.tfPlayer = new TextFieldDark[Constants.PLAYERS_COUNT_MAX];
		this.cbPlayer = new CheckBoxDark[Constants.PLAYERS_COUNT_MAX];
		this.canvasPlayerColors = new CanvasPlayerColors[Constants.PLAYERS_COUNT_MAX];
		this.cbOptions = new Hashtable<GameOptions,CheckBoxDark>();		
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				setVisible(false);
				dispose();
			}
		}
		);
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark(new GridLayout(6, 2, 20, 0));
		
		panMain.add(add(this.getPlayerPanel(0)));
		
		PanelDark panPlanetsSub1 = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		String[] players = new String[Constants.PLAYERS_COUNT_MAX - Constants.PLAYERS_COUNT_MIN + 1];
		for (int playerIndex = Constants.PLAYERS_COUNT_MIN; playerIndex <= Constants.PLAYERS_COUNT_MAX; playerIndex++)
			players[playerIndex-Constants.PLAYERS_COUNT_MIN] = Integer.toString(playerIndex);
		this.comboPlayers = new ComboBoxDark<String>(players, font);
		this.comboPlayers.setSelectedItem(Integer.toString(this.playersCount));
		this.comboPlayers.addActionListener(this);
		panPlanetsSub1.add(this.comboPlayers);
		panPlanetsSub1.add(new LabelDark(SternResources.Players(false), font));
		
		panMain.add(panPlanetsSub1);
		
		panMain.add(add(this.getPlayerPanel(1)));
		
		PanelDark panYearMax = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panYearMax.add(new LabelDark(SternResources.SpielparameterJDialogSpieleBisJahr(false)+" ", font));
		String[] years = { ENDLESS_GAME_STRING, "15", "20", "30", "40", "50", "75", "100", "150", "200" };
		this.comboYearLast = new ComboBoxDark<String>(years, font);
		panYearMax.add(this.comboYearLast);
		panMain.add(panYearMax);
		
		panMain.add(add(this.getPlayerPanel(2)));

		CheckBoxDark cbSimple = new CheckBoxDark(SternResources.SpielparameterJDialogSimpelStern(false), true, font);
		this.cbOptions.put(GameOptions.SIMPLE, cbSimple);
		this.cbOptions.get(GameOptions.SIMPLE).addActionListener(this);
		panMain.add(cbSimple);
		
		panMain.add(add(this.getPlayerPanel(3)));

		CheckBoxDark cbAutoSave = new CheckBoxDark(SternResources.SpielparameterJDialogAutoSave(false), true, font);
		this.cbOptions.put(GameOptions.AUTO_SAVE, cbAutoSave);
		panMain.add(cbAutoSave);
		
		panMain.add(add(this.getPlayerPanel(4)));
		
		CheckBoxDark cbEmail = new CheckBoxDark(SternResources.SpielparameterJDialogEmailModus(false), false, font);
		this.cbOptions.put(GameOptions.EMAIL_BASED, cbEmail);
		panMain.add(cbEmail);
		
		panMain.add(add(this.getPlayerPanel(5)));
				
		PanelDark panEmailConfigurationButton = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		this.butEmailConfiguration = new ButtonDark(this, SternResources.SpielparameterJDialogEMailEinstellungen(false), font);
		panEmailConfigurationButton.add(this.butEmailConfiguration);
		panMain.add(panEmailConfigurationButton);
		// ----
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// ----
		
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butCancel);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		// ---
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panShell, BorderLayout.CENTER);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().setDefaultButton(this.butOk);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
		
		this.setInitialControlValues();
		this.setControlsEnabled();
	}
	
	@SuppressWarnings("unchecked")
	private void getInitialValues(Game game)
	{
		if (game == null)
		{
			this.players = Game.getPlayersDefault();
			this.options = Game.getOptionsDefault();
			
			this.emailGameHost = "";
			
			this.playersCount = Constants.PLAYERS_COUNT_DEFAULT;
			this.yearMax = Constants.YEARS_COUNT_MAX_DEFAULT;
		}
		else
		{
			this.playersCount = game.getPlayersCount();
			
			this.emailGameHost = game.getEmailAddressGameHost();
			
			this.options = (HashSet<GameOptions>)Utils.klon(game.getOptions());
			
			if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			{
				this.yearMax = game.getYearMax();
			}
			else
				this.yearMax = Constants.YEARS_COUNT_MAX_DEFAULT;
			
			this.players = new ArrayList<Player>();
			ArrayList<Player> playersDefault = Game.getPlayersDefault();
			
			boolean[] colorIndicesUsed = new boolean[Constants.PLAYERS_COUNT_MAX  + Colors.COLOR_OFFSET_PLAYERS];
			
			for (int playerIndex = 0; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
			{
				if (playerIndex < this.playersCount)
				{
					colorIndicesUsed[game.getPlayers()[playerIndex].getColorIndex()] = true;
					players.add((Player)Utils.klon(game.getPlayers()[playerIndex]));
				}
				else
				{
					byte colorIndexTemp = playersDefault.get(playerIndex).getColorIndex();
					
					for (int playerIndex2 = 0; playerIndex2 < Constants.PLAYERS_COUNT_MAX; playerIndex2++)
					{
						if (!colorIndicesUsed[colorIndexTemp])
						{
							colorIndicesUsed[colorIndexTemp] = true;
							players.add(new Player("", "", colorIndexTemp, false, false));
							break;
						}
						colorIndexTemp = (byte)((colorIndexTemp + 1) % Constants.PLAYERS_COUNT_MAX);
					}
				}
			}
		}		
	}
	
	private void setInitialControlValues()
	{
		for (GameOptions option: this.cbOptions.keySet())
			this.cbOptions.get(option).setSelected(this.options.contains(option));

		if (this.options.contains(GameOptions.LIMITED_NUMBER_OF_YEARS))
			this.comboYearLast.setSelectedItem(Integer.toString(this.yearMax));
		else
			this.comboYearLast.setSelectedItem(ENDLESS_GAME_STRING);
		
	}
	
	private void setControlsEnabled()
	{
		boolean simple = this.cbOptions.get(GameOptions.SIMPLE).isSelected();
		
		for (GameOptions option: this.cbOptions.keySet())
		{
			if (option == GameOptions.AUTO_SAVE)
				this.cbOptions.get(option).setEnabled(this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);
			else if (option == GameOptions.SIMPLE)
				this.cbOptions.get(option).setEnabled(this.mode == GameParametersDialogMode.NEW_GAME);
			else if (option == GameOptions.EMAIL_BASED)
				this.cbOptions.get(option).setEnabled(
						this.mode != GameParametersDialogMode.FINALIZED_GAME &&
						this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);			
		}
		
		this.comboYearLast.setEnabled(
				this.mode != GameParametersDialogMode.FINALIZED_GAME &&
				this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);
		this.comboPlayers.setEnabled(this.mode == GameParametersDialogMode.NEW_GAME);
		
		for (int playerIndex = 0; playerIndex < Constants.PLAYERS_COUNT_MAX; playerIndex++)
		{
			if (playerIndex < this.playersCount)
			{
				this.panPlayers[playerIndex].setVisible(true);
				this.canvasPlayerColors[playerIndex].setEnabled(
						this.mode != GameParametersDialogMode.FINALIZED_GAME &&
						this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);
				this.tfPlayer[playerIndex].setEditable(
						this.mode != GameParametersDialogMode.FINALIZED_GAME &&
						this.mode != GameParametersDialogMode.EMAIL_BASED_GAME);
			}
			else
				this.panPlayers[playerIndex].setVisible(false);
			
			if (!simple)
				this.cbPlayer[playerIndex].setSelected(false);
			
			this.cbPlayer[playerIndex].setEnabled(this.mode == GameParametersDialogMode.NEW_GAME && simple);
		}
	}
	
	private PanelDark getPlayerPanel(int playerIndex)
	{
		this.panPlayers[playerIndex] = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		canvasPlayerColors[playerIndex] = new CanvasPlayerColors(
				this, 
				this,
				playerIndex, 
				this.players.get(playerIndex).getColorIndex(),
				font);
		canvasPlayerColors[playerIndex].setPreferredSize(new Dimension(14,14));
		this.panPlayers[playerIndex].add(canvasPlayerColors[playerIndex]);
		
		this.tfPlayer[playerIndex] = new TextFieldDark(font, 20);
		this.tfPlayer[playerIndex].setText(this.players.get(playerIndex).getName());
		
		this.panPlayers[playerIndex].add(this.tfPlayer[playerIndex]);
		
		this.cbPlayer[playerIndex] = new CheckBoxDark(SternResources.SpielparameterJDialogBot(false), this.players.get(playerIndex).isBot(), font);
		
		this.cbPlayer[playerIndex].addActionListener(this);
		this.panPlayers[playerIndex].add(this.cbPlayer[playerIndex]);
		
		return this.panPlayers[playerIndex];
	}
	
	public ArrayList<Player> getPlayers()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		for (int playerIndex = 0; playerIndex < this.playersCount; playerIndex++)
			players.add((Player)Utils.klon(this.players.get(playerIndex)));
			
		return players;
	}

	public int getYearMax() {
		return yearMax;
	}
	
	public String getEmailGameHost()
	{
		return this.emailGameHost;
	}

	public HashSet<GameOptions> getOptions() {
		return options;
	}
	
	public boolean isAbort() {
		return abort;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.getRootPane())
		{
			this.abort = true;
			this.close();
		}
		else if (e.getSource() == this.cbOptions.get(GameOptions.SIMPLE))
		{
			this.setControlsEnabled();
		}
		else if (e.getSource() == this.comboPlayers)
		{
			this.playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
			
			this.setControlsEnabled();
		}
		else if (e.getSource().getClass() == CheckBoxDark.class)
		{
			for (int i = 0; i < Constants.PLAYERS_COUNT_MAX; i++)
			{
				if (e.getSource() == this.cbPlayer[i])
				{
					this.tfPlayer[i].setEnabled(!this.cbPlayer[i].isSelected());
					
					if (this.cbPlayer[i].isSelected())
					{
						this.tfPlayer[i].setText(SternResources.SpielparameterJDialogBot(false)+(i+1));
					}
					break;
				}
			}
		}
		else
		{
			JButton button = (JButton)e.getSource();
			
			if (button == this.butCancel)
			{
				this.abort = true;
				this.close();
			}
			else if (button == this.butEmailConfiguration)
			{
				boolean ok = this.getPlayersFromControls(
						this.players,
						Integer.parseInt((String)this.comboPlayers.getSelectedItem()));
				
				if (ok)
				{
					EmailSettingsJDialog dlg = new EmailSettingsJDialog(
													this,
													this.emailGameHost,
													this.players,
													this.emails,
													this.mode == GameParametersDialogMode.FINALIZED_GAME ||
													this.mode == GameParametersDialogMode.EMAIL_BASED_GAME);
					
					dlg.setVisible(true);
					
					this.emailGameHost = dlg.emailGameHost;
					
					for (int playerIndex = 0; playerIndex < dlg.players.size(); playerIndex++)
					{
						this.players.get(playerIndex).setEmailPlayer(dlg.players.get(playerIndex).isEmailPlayer());
						this.players.get(playerIndex).setEmail(dlg.players.get(playerIndex).getEmail());
					}
				}
			}
			else if (button == this.butOk)
			{
				boolean ok = true;
				
				this.playersCount = Integer.parseInt((String)this.comboPlayers.getSelectedItem());
				
				ok = this.getPlayersFromControls(this.players, this.playersCount);
				
				for (GameOptions option: this.cbOptions.keySet())
				{
					if (this.cbOptions.get(option).isSelected())
						this.options.add(option);
					else
						this.options.remove(option);
				}
				
				String yearMaxString = (String)this.comboYearLast.getSelectedItem();
				
				if (yearMaxString.equals(ENDLESS_GAME_STRING))
				{
					this.yearMax = 0;
					this.options.remove(GameOptions.LIMITED_NUMBER_OF_YEARS);
				}
				else
				{
					this.yearMax = Integer.parseInt(yearMaxString);
					this.options.add(GameOptions.LIMITED_NUMBER_OF_YEARS);
				}
				
				if (ok)
				{
					if (this.options.contains(GameOptions.EMAIL_BASED))
						ok = checkEmailSettings(this, this.emailGameHost, this.players);
				}
				
				if (ok)
				{
					this.abort = false;
					this.close();
				}
			}
		}
	}
	
	private boolean getPlayersFromControls(ArrayList<Player> players, int playersCount)
	{
		if (players == null)
			players = new ArrayList<Player>();
		
		for (int playerIndex = players.size() - 1; playerIndex >= playersCount; playerIndex--)
			players.remove(playerIndex);
		
		boolean ok = true;
		
		for (int playerIndex = 0; playerIndex < playersCount; playerIndex++)
		{						
			this.tfPlayer[playerIndex].setText(this.tfPlayer[playerIndex].getText().trim());
			
			Player player = null;
			
			if (playerIndex < players.size())
			{
				player = players.get(playerIndex);
				
				player.setName(this.tfPlayer[playerIndex].getText());
				player.setColorIndex(this.canvasPlayerColors[playerIndex].colorIndex);
				player.setBot(this.cbPlayer[playerIndex].isSelected());
			}
			else
			{
				player = new Player(this.tfPlayer[playerIndex].getText(), "", 
						this.canvasPlayerColors[playerIndex].colorIndex, this.cbPlayer[playerIndex].isSelected(), false);
				
				players.add(player);
			}
			
			if (player.isBot())
			{
				player.setEmailPlayer(false);
				player.setEmail("");
			}
			
			boolean isUserNameAllowed = 
					(player.getName().length() >= Constants.PLAYER_NAME_LENGTH_MIN &&
					player.getName().length() <= Constants.PLAYER_NAME_LENGTH_MAX &&
					!player.getName().toLowerCase().equals(ServerConstants.ADMIN_USER.toLowerCase()) &&
					Pattern.matches(Constants.PLAYER_NAME_REGEX_PATTERN, player.getName())
					);
			
			if (this.tfPlayer[playerIndex].isEditable() && !isUserNameAllowed)
			{
				DialogWindow.showError(
						this,
						SternResources.SpielparameterJDialogNameZuLang(
								false, 
								player.getName(), 
								Integer.toString(Constants.PLAYER_NAME_LENGTH_MIN), 
								Integer.toString(Constants.PLAYER_NAME_LENGTH_MAX)),
						SternResources.Fehler(false));
				ok = false;
				break;
			}
		}

		return ok;
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	@Override
	public void colorChanged(int playerIndex, byte newColorIndex, byte oldColorIndex)
	{
		for (CanvasPlayerColors c: this.canvasPlayerColors)
		{
			if (c.playerIndex != playerIndex && c.colorIndex == newColorIndex)
			{
				c.setColor(oldColorIndex);
				break;
			}
		}
	}
	
	static boolean checkEmailSettings(Component c, String emailGameHost, ArrayList<Player> players)
	{
		boolean ok = true;
		
		if (emailGameHost == null || !emailGameHost.contains("@"))
		{
			DialogWindow.showError(
					c,
					SternResources.SpielparameterJDialogSpielleiterEMail(false), 
					SternResources.Fehler(false));
			return false;
		}
		
		for (Player player: players)
		{
			if (player.isBot())
			{
				player.setEmail("");
				player.setEmailPlayer(false);
			}
			else
			{
				if (player.isEmailPlayer() && !player.getEmail().contains("@"))
				{
					ok = false;
					DialogWindow.showError(
							c,
							SternResources.SpielparameterJDialogSpielerEMail(false, player.getName()),
							SternResources.Fehler(false));
					break;
				}
			}
		}
		
		return ok;
	}
}
