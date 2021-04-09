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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.google.gson.Gson;

import common.Archive;
import common.Constants;
import common.EmailTransportBase;
import common.IGameThreadEventListener;
import common.KeyEventExtended;
import common.PostMovesResult;
import common.ReleaseGetter;
import common.ScreenContent;
import common.ScreenContentClient;
import common.ScreenUpdateEvent;
import common.Game;
import common.GameOptions;
import common.GameThread;
import common.GameThreadCommunicationStructure;
import common.Player;
import common.MovesTransportObject;
import common.SternResources;
import common.Utils;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageGamesAndUsers;
import commonServer.RequestMessageGetStatus;
import commonServer.RequestMessagePostMoves;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonServer.ResponseMessageGetStatus;
import commonServer.ServerUtils;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
import commonUi.IUpdateCheckerCallback;
import commonUi.LabelDark;
import commonUi.MessageWithLink;
import commonUi.PanelScreenContent;
import commonUi.PanelDark;
import commonUi.LanguageSelectionJDialog;
import commonUi.SpringUtilities;
import commonUi.SternAbout;
import commonUi.UpdateChecker;

@SuppressWarnings("serial") 
public class Stern extends Frame // NO_UCD (use default)
	implements 
		WindowListener, 
		IGameThreadEventListener, 
		ActionListener,
		MouseListener,
		IServerMethods,
		IUpdateCheckerCallback,
		IHostComponentMethods
{
	transient private final static String FILE_SUFFIX = ".stn";
	transient private final static String FILE_SUFFIX_BACKUP = ".BAK";
	transient private final static String FILE_SUFFIX_IMPORT = ".VEG";
	
	transient private static final String PROPERTIES_FILE_NAME = "SternProperties";
	transient private static final String HIGHSCORES_FILE_NAME = "SternHighscores";
	transient private static final String PROPERTY_NAME_DIRECTORY_NAME_LAST = "lastDir";
	transient private static final String PROPERTY_EMAILS = "emails";
	transient static final String PROPERTY_EMAIL_SEPARATOR = "emailSeparator";
	transient private static final String PROPERTY_LAST_UPDATE_FOUND = "lastUpdateFound";
	transient private static final String PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE = "serverAdminCredentials";
	transient private static final String PROPERTY_SERVER_USER_CREDENTIAL_FILE = "serverUserCredentials";
	transient private static final String PROPERTY_SERVER_COMMUNICATION_ENABLED = "serverCommunicationEnabled";
	transient private static final String PROPERTY_NAME_LANGUAGE = "language";
	transient private static final String PROPERTY_MUTE_NOTIFICATION_SOUND = "muteNotificationSound";
	transient private static final String PROPERTY_IP_ADDRESS = "myIpAddress";
    transient private static final String PROPERTY_CLIENTS_INACTIVE = "clientInactiveWhileEnterMoves";
	
	
	static final int HIGHSCORE_ENTRIES_COUNT = 20;
	
	private GameThread t;
	private GameThreadCommunicationStructure threadCommunicationStructure;
	
	private Game gameLastRawData;
	private String fileNameLast;
	private String directoryNameLast;
	String emailSeparator;
	
	private Properties properties;
	
	private ServerFunctions serverFunctions;
	
	private HighscoreEntries highscoreEntries;
	
	private ArrayList<String> emails = new ArrayList<String>();
	
	private ClientUserCredentials cuc;
	private String serverAdminCredentialFile;
	private String serverUserCredentialsFile;
	private boolean serverCommunicationEnabled;
	
    private MenuItem menuNewGame;
    private MenuItem menuLoad;
    private MenuItem menuEmailClipboard;
    private MenuItem menuParameters;
    private MenuItem menuEmailSend;
    private MenuItem menuSave;
    private MenuItem menuServerAdmin;
    private MenuItem menuServerGames;
    private MenuItem menuServerCredentials;
    private MenuItem menuLanguage;
    private MenuItem menuSearchForUpdates;
    private MenuItem menuOutputWindow;
    
    private MenuItem menuServer;
    private MenuItem menuWebserver;
    
    private MenuItem menuHighscore;
    
    private MenuItem menuQuit;
    
    private MenuItem menuHelp;
    private MenuItem menuAbout;
	
	private PanelScreenContent paintPanel;
	private OutputWindow outputWindow;
	private PanelDark panToolbar;
	private LabelDark labConnectionStatus;
	private LabelDark labGamesWaitingForInput;
	private LabelDark labCurrentGameNextYear;
	
	private ImageIcon iconConnected;
	private ImageIcon iconNotConnected;
	private ImageIcon iconWaitingForInput;
	private ImageIcon iconWaitingForInputDark;
	private ImageIcon iconReload;
	private ImageIcon iconReloadDark;
	
	private boolean inputEnabled;
	private String currentGameId;
	private int currentGameJahr;
	
	private Timer gameInfoUpdateTimer;
	
	private String lastUpdateCheck;
	
	private Clip soundClipNotification;
	
	private boolean muteNotificationSound;
	private boolean clientsInactiveWhileEnterMoves;
	private String myIpAddress;
	
	private Webserver webserver;
	
	public static void main(String[] args)
	{
		new Stern();
	}

	private Stern()
	{
		super();
		
		try {
		    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		    UIManager.put("ComboBox.disabledBackground", Color.black);
		 } catch (Exception e) {
		            e.printStackTrace();
		 }
		
		this.properties = this.getProperties();
		
		this.loadHighscores();
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.addWindowListener(this);
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
		
		this.setMenuBar(this.defineMenuBar());
		this.setMenuEnabled();
		
		this.paintPanel = new PanelScreenContent(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("ding.au"));
			this.soundClipNotification = AudioSystem.getClip();
			this.soundClipNotification.open(audioInputStream);
		}
		catch (Exception x) {}
		
		this.iconConnected = new ImageIcon (ClassLoader.getSystemResource("ic_cloud_done.png"));
		this.iconNotConnected = new ImageIcon (ClassLoader.getSystemResource("ic_cloud_off.png"));
		this.iconWaitingForInput = new ImageIcon (ClassLoader.getSystemResource("ic_assignment_late.png"));
		this.iconWaitingForInputDark = new ImageIcon (ClassLoader.getSystemResource("ic_assignment_late2.png"));
		this.iconReload = new ImageIcon (ClassLoader.getSystemResource("ic_refresh.png"));
		this.iconReloadDark = new ImageIcon (ClassLoader.getSystemResource("ic_refresh2.png"));
		
		this.panToolbar = new PanelDark(new SpringLayout());
		PanelDark panToolBarShell = new PanelDark(new BorderLayout());
		PanelDark panToolBar = new PanelDark(new GridLayout(3,1,10,5));
		
		this.labCurrentGameNextYear = new LabelDark(
				this.iconReload, 
				30, 
				true,
				this.iconReloadDark);
		this.labCurrentGameNextYear.setVisible(false);
		this.labCurrentGameNextYear.addMouseListener(this);
		
		panToolBar.add(this.labCurrentGameNextYear);
		
		this.labGamesWaitingForInput = new LabelDark(this.iconWaitingForInput, 30, true, this.iconWaitingForInputDark);
		this.labGamesWaitingForInput.setVisible(false);
		this.labGamesWaitingForInput.addMouseListener(this);
		
		panToolBar.add(this.labGamesWaitingForInput);
		
		this.labConnectionStatus = new LabelDark(this.iconNotConnected, 30, false, null);
		this.labConnectionStatus.addMouseListener(this);
		panToolBar.add(this.labConnectionStatus);
		
		panToolBarShell.add(panToolBar, BorderLayout.SOUTH);
		this.panToolbar.add(panToolBarShell);
		
		SpringUtilities.makeCompactGrid(this.panToolbar,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(this.panToolbar, BorderLayout.EAST);
		
		this.panToolbar.setVisible(this.serverCommunicationEnabled);
		
		this.serverFunctions = new ServerFunctions(myIpAddress);
						
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.updateTitle();
		this.paintPanel.requestFocusInWindow();
		
		this.gameInfoUpdateTimer = new Timer(30000, this);
		this.gameInfoUpdateTimer.start();
		
		this.getGameInfoByTimer();
		
		Thread tUpdateChecker = new Thread( new UpdateChecker(this, this.lastUpdateCheck, true) );
 		tUpdateChecker.start();
	}
	
	private void keyPressed(KeyEventExtended event)
	{
		if (this.inputEnabled && 
			this.threadCommunicationStructure != null&&
			this.t != null &&
			this.t.isAlive())
		{
			synchronized(this.threadCommunicationStructure)
			{
				this.threadCommunicationStructure.keyEvent = event;
				this.threadCommunicationStructure.notify();
			}
		}
	}
		
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		this.closeStern();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	private Game importGame(String filePath)
	{
		byte[] bytes = null;
		Game game = null;
		
		try {
			Path path = Paths.get(filePath);
			String gameName = path.getFileName().toString();
			if (gameName.contains("."))
				gameName = gameName.substring(0, gameName.indexOf("."));
			
			 bytes = Files.readAllBytes(path);
			 game = Game.importFromVega(gameName, bytes);
		} catch (IOException e)
		{
			game = null;
		}
		
		return game;

	}
	private MenuBar defineMenuBar()
	{
	    MenuBar menuBar = new MenuBar ();
	    
	    Menu menuStern = new Menu (SternResources.SternTitel(false));
	    
	    this.menuAbout = new MenuItem (SternResources.MenuUeberStern(false));
	    this.menuAbout.addActionListener(this);
	    menuStern.add (this.menuAbout);
	    
	    menuStern.addSeparator();
	    
	    Menu menuSettings = new Menu (SternResources.MenuEinstellungen(false));
	    
	    this.menuLanguage = new MenuItem(SternResources.MenuSpracheinstellungen(false));
	    this.menuLanguage.addActionListener(this);
	    menuSettings.add(this.menuLanguage);
	    
	    this.menuServerCredentials = new MenuItem(SternResources.MenuServerCredentials(false));
	    this.menuServerCredentials.addActionListener(this);
	    menuSettings.add(this.menuServerCredentials);
	    
	    this.menuServerAdmin = new MenuItem(SternResources.MenuServerAdmin(false));
	    this.menuServerAdmin.addActionListener(this);
	    menuSettings.add(this.menuServerAdmin);
	    
	    this.menuServer = new MenuItem(SternResources.MenuScreesharing(false));
	    this.menuServer.addActionListener(this);
	    menuSettings.add(this.menuServer);
	    
	    this.menuWebserver = new MenuItem(SternResources.MenuWebserverAktivieren(false));
	    this.menuWebserver.addActionListener(this);
	    menuSettings.add(this.menuWebserver);
	    
	    menuStern.add(menuSettings);
	    
	    this.menuOutputWindow = new MenuItem(SternResources.MenuAusgabeFenster(false));
	    this.menuOutputWindow.addActionListener(this);
	    menuStern.add(this.menuOutputWindow);
	    
	    menuStern.addSeparator();
	    
	    this.menuQuit = new MenuItem (SternResources.MenuSternVerlassen(false));
	    this.menuQuit.addActionListener(this);
	    menuStern.add (this.menuQuit);
	    
	    menuBar.add(menuStern);
	    
	    // -----------
	    
	    Menu menuGame = new Menu (SternResources.MenuDatei(false));

	    this.menuNewGame = new MenuItem (SternResources.MenuNeuesSpiel(false));
	    this.menuNewGame.addActionListener(this);
	    menuGame.add(this.menuNewGame);
	    
	    this.menuLoad = new MenuItem (SternResources.MenuSpielLaden(false));
	    this.menuLoad.addActionListener(this);
	    menuGame.add (menuLoad);
	    
	    this.menuSave = new MenuItem (SternResources.MenuSpielSpeichernAls(false));
	    this.menuSave.addActionListener(this);
	    menuGame.add (menuSave);
	    
	    this.menuHighscore = new MenuItem(SternResources.MenuBestenliste(false));
	    this.menuHighscore.addActionListener(this);
	    menuGame.add(this.menuHighscore);

	    menuGame.addSeparator();
	    
	    this.menuEmailClipboard = new MenuItem (SternResources.MenuSpielAusZwischenablageLaden(false));
	    this.menuEmailClipboard.addActionListener(this);
	    menuGame.add (menuEmailClipboard);
	    
	    menuGame.addSeparator();

	    this.menuServerGames = new MenuItem(SternResources.MenuServerbasierteSpiele(false));
	    this.menuServerGames.addActionListener(this);
	    menuGame.add(this.menuServerGames);
	    
	    menuGame.addSeparator();
	    
	    this.menuEmailSend = new MenuItem(SternResources.MenuEmail(false));
	    this.menuEmailSend.addActionListener(this);
	    menuGame.add(this.menuEmailSend);
	    
	    this.menuParameters = new MenuItem (SternResources.Spielparameter(false));
	    this.menuParameters.addActionListener(this);
	    menuGame.add (this.menuParameters);
	    
	    menuBar.add(menuGame);
	    
	    // ----
	    Menu menuHelp = new Menu(SternResources.MenuHilfe(false));
	    
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHelp = new MenuItem (SternResources.MenuSpielanleitung(false));
		    this.menuHelp.addActionListener(this);
		    menuHelp.add (this.menuHelp);
	    }
	    
	    this.menuSearchForUpdates = new MenuItem (SternResources.MenuSearchForUpdates(false));
 	    this.menuSearchForUpdates.addActionListener(this);
 	    menuHelp.add (this.menuSearchForUpdates);
	    
	    menuBar.add(menuHelp);
	    
	    return menuBar;
	}

	@Override
	public void updateDisplay(ScreenUpdateEvent event)
	{
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
		{
			ScreenContent screenContent = null;
			
			if (this.t != null && this.t.getGame() != null)
			{
				screenContent = this.t.getGame().getScreenContentWhileMovesEntered();
			}
			
			this.serverFunctions.updateClients(
					event.getScreenContent(), 
					screenContent, 
					this.inputEnabled);
		}

		if (this.outputWindow != null && this.outputWindow.isVisible())
		{
			ScreenContent screenContent = null;
			if (this.t != null && this.t.getGame() != null)
				screenContent = this.t.getGame().getScreenContentWhileMovesEntered();
			
			this.outputWindow.redraw(
					screenContent != null ? screenContent : event.getScreenContent());
		}

		this.paintPanel.redraw(event.getScreenContent(), this.inputEnabled, false);
	}
	
	private void redrawScreen ()
	{
		this.updateDisplay(new ScreenUpdateEvent(this, this.paintPanel.getScreenContent()));
	}

	@Override
	public void saveGame(Game game, boolean autoSave)
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		String fileName ="";
		String directoryName = "";
		
		if (autoSave && this.fileNameLast != null && !this.fileNameLast.isEmpty() &&
				        this.directoryNameLast != null && !this.directoryNameLast.isEmpty())
		{
			fileName = this.fileNameLast;
			directoryName = this.directoryNameLast;
			
			try {
				this.createBackup(new File(directoryName,fileName).getPath());
			} catch (IOException e) {}
		}
		else
		{
			fileName = null;
			
			do
			{
				FileDialog fd = new FileDialog(this, SternResources.SpielSpeichern(false), FileDialog.SAVE);
				
				if (this.directoryNameLast != null && !this.directoryNameLast.isEmpty())
					fd.setDirectory(this.directoryNameLast);
	
				fd.setFile(game.getName() + FILE_SUFFIX);
				fd.setVisible(true);
				
				fileName = fd.getFile();
				directoryName = fd.getDirectory();
				
				if (fileName == null)
				{
					break;
				}
				
				if (fileName.toLowerCase().endsWith(FILE_SUFFIX))
				{
					fileName = fileName.substring(0, fileName.indexOf(FILE_SUFFIX));
				}
				
				if (fileName.length() > 0)
				{
					game.setName(fileName);
					fileName = fileName + FILE_SUFFIX;
					break;
				}
				
			} while (true);
		}
		
		if (fileName != null)
		{
			game.setBuildRequired(Constants.BUILD_COMPATIBLE);
				
			File file = new File(directoryName,fileName);
			
			String errorText = Utils.writeGameToFile(game, file);
			
			if (errorText != null)
				DialogWindow.showError(
						this,
					    errorText,
					    "");
			else
			{
				this.directoryNameLast = directoryName;
				this.setProperty(PROPERTY_NAME_DIRECTORY_NAME_LAST, this.directoryNameLast);
				this.fileNameLast = fileName;
			}
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
		this.updateTitle();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.gameInfoUpdateTimer)
		{
			this.getGameInfoByTimer();
			return;
		}
		
		MenuItem menuItem = (MenuItem)e.getSource();
		
		if (menuItem == this.menuLoad)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.loadGame();
			if (game != null)
				this.setNewGame(game, false);
						
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuEmailClipboard)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ClipboardImportJDialog<Game> dlg = 
					new ClipboardImportJDialog<Game>(this, Game.class, false);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == DialogWindowResult.OK)
			{
				Game game = (Game)dlg.obj;
				
				if (game != null)
				{
					if (!RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
						game = null;
				}
				this.setNewGame(game, true);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuNewGame)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			GameParametersJDialog dlg = 
					new GameParametersJDialog(
							this,
							SternResources.Spielparameter(false),
							GameParametersDialogMode.NEW_GAME,
							this.gameLastRawData,
							this.emails);
			
			dlg.setVisible(true);
			this.setEmailsProperty();
			
			if (!dlg.isAbort())
			{
				Object[] playersArray = dlg.getPlayers().toArray();
				Player[] players =  Arrays.copyOf(playersArray,playersArray.length,Player[].class);

				Game game = new Game(dlg.getOptions(), players, dlg.getEmailGameHost(), dlg.getYearMax());
				this.gameLastRawData = (Game)Utils.klon(game);
				this.fileNameLast = "";
				this.setNewGame(game, false);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuParameters && this.t != null)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.t.getGame();
			
			GameParametersJDialog dlg = new GameParametersJDialog(
					this, 
					SternResources.Spielparameter(false),
					game.isSoloPlayer() ?
							GameParametersDialogMode.EMAIL_BASED_GAME :
							game.isFinalized() ?
									GameParametersDialogMode.FINALIZED_GAME :
									GameParametersDialogMode.ACTIVE_GAME,
					(Game)Utils.klon(game),
					this.emails);
			
			dlg.setVisible(true);
			this.setEmailsProperty();
			
			if (!dlg.isAbort())
				game.changeParameters(dlg.getOptions(), dlg.getYearMax(), dlg.getEmailGameHost(), dlg.getPlayers());
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuEmailSend && this.t != null)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Game game = this.t.getGame();
			
			EmailCreatorJDialog dlg = new EmailCreatorJDialog(
					this, 
					game.getPlayers(),
					game.getEmailAddressGameHost(),
					this.emailSeparator,
					"[STERN] " + game.getName(),
					"");
			
			dlg.setVisible(true);
			
			if (dlg.launched)
			{
				this.emailSeparator = dlg.separatorPreset;
				this.setProperty(PROPERTY_EMAIL_SEPARATOR, this.emailSeparator);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuSave && this.t != null)
		{
			this.saveGame(this.t.getGame(), false);
		}
		else if (menuItem == this.menuQuit)
		{
			this.closeStern();
		}
		else if (menuItem == this.menuServer)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ServerSettingsJDialog dlg = 
					new ServerSettingsJDialog(
							this, 
							SternResources.Terminalserver(false),
							this.myIpAddress,
							true, 
							this.serverFunctions);

			dlg.setVisible(true);
			
			this.myIpAddress = dlg.myIpAddress;
			this.setProperty(
					Stern.PROPERTY_IP_ADDRESS, this.myIpAddress);
			
			this.updateTitle();
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuServerAdmin)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ServerAdminJDialog dlg = new ServerAdminJDialog(
					this, 
					SternResources.SternServerVerwalten(false), 
					this.serverAdminCredentialFile);
			dlg.setVisible(true);
			
			this.serverAdminCredentialFile = dlg.serverAdminCredentialsFile;
			this.setProperty(PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE, dlg.serverAdminCredentialsFile);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuServerCredentials)
		{
			this.openServerCredentialsDialog();
		}
		else if (menuItem == this.menuServerGames)
		{
			this.openServerGamesDialog();
		}
		else if (menuItem == this.menuWebserver)
		{
			this.activateWebServer();
		}
		else if (menuItem == this.menuHighscore)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			HighscoreJDialog dlg = new HighscoreJDialog(this, SternResources.Bestenliste(false), true, this.highscoreEntries.list);
			dlg.setVisible(true);
						
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuHelp)
		{
			Desktop desktop = Desktop.getDesktop();   
		    InputStream resource = getClass().getResourceAsStream("/SternHelp.pdf");
		    try
		    {
		    	Path tempOutput = Files.createTempFile("SternHelp", ".pdf");
		        tempOutput.toFile().deleteOnExit();
		        try
		        {
		        	Files.copy(resource,tempOutput,StandardCopyOption.REPLACE_EXISTING);
		        }
		        finally
		        {
		            resource.close();
		        }
		        desktop.open(tempOutput.toFile());   
		    }   
		    catch (Exception x)
		    {
		    	
		    }
		    finally
		    {
		    	try
		    	{
		        resource.close();
		    	}
		    	catch (Exception xx) {}
		    }
		}
		else if (menuItem == this.menuLanguage)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					this, 
					SternResources.getLocale());
			dlg.setVisible(true);
			
			if (dlg.ok)
			{
				SternResources.setLocale(dlg.languageCode);
				
				this.properties.setProperty(PROPERTY_NAME_LANGUAGE, dlg.languageCode);
				writeProperties(this.properties);
				
				System.exit(0);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuAbout)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			SternAbout.show(this);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (menuItem == this.menuSearchForUpdates)
 		{
 			Thread tUpdateChecker = new Thread( new UpdateChecker(this, null, false) );
 			tUpdateChecker.start();
 		}
		else if (menuItem == this.menuOutputWindow)
		{
			if (this.outputWindow == null || !this.outputWindow.isVisible())
			{
				Point windowLocation = this.getLocation();
				Dimension windowSize = this.getSize();
				
				this.outputWindow = new OutputWindow(windowLocation.x + 20, windowLocation.y + 20, windowSize.width/2, windowSize.height/2);
				this.outputWindow.setVisible(true);
				this.redrawScreen();
			}
		}
		
		this.setMenuEnabled();
	}
	
	private Game loadGame()
	{
		Game game = null;
		
		FileDialog fd = new FileDialog(this, SternResources.SpielLaden(false), FileDialog.LOAD);
		
		if (this.directoryNameLast != null)
			fd.setDirectory(this.directoryNameLast);
		
		fd.setFile("*"+FILE_SUFFIX+";*"+FILE_SUFFIX_BACKUP + ";*" + FILE_SUFFIX_IMPORT);			
		
		fd.setVisible(true);
		String fileName = fd.getFile();
		if (fileName != null)
		{
			File file = new File(fd.getDirectory(),fileName);
			if (file.exists())
			{
				boolean error = false;
				String	errorText = "";
				boolean importFromOldVega = false;
				
				game = Utils.getGameFromFile(file);
				
				if (game == null)
				{
					game = this.importGame(file.getPath());
					
					if (game == null)
					{						
						errorText = SternResources.DateiNichtGueltig(false);
						error = true;
					}
					else
						importFromOldVega = true;
				}
				
				if (error == true)
				{
					DialogWindow.showError(
							this,
						    errorText,
						    SternResources.FehlerBeimLaden(false));
					
					game = null;
				}
				else if (!importFromOldVega)
				{
					if (!RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
						game = null;
				}
				
				if (game != null)
				{
					this.directoryNameLast = fd.getDirectory(); 
					this.setProperty(PROPERTY_NAME_DIRECTORY_NAME_LAST, this.directoryNameLast);

					if (!importFromOldVega)
						this.fileNameLast = fileName;
					
					game.getOptions().remove(GameOptions.SERVER_BASED);
				}
			}
			else
				DialogWindow.showError(
						this, 
						SternResources.DateiExistiertNicht(false), 
						SternResources.FehlerBeimLaden(false));
		}
		
		return game;
	}

	private void setNewGame(Game game, boolean isSoloPlayer)
	{
		this.inputEnabled = true;
		this.currentGameId = game.getName();
		
		if (this.t == null)
		{
			this.threadCommunicationStructure = new GameThreadCommunicationStructure();
			this.threadCommunicationStructure.isSoloPlayer = isSoloPlayer;
			this.t = new GameThread(this.threadCommunicationStructure, this, game);
			this.t.start();
		}
		else
		{
			synchronized(this.threadCommunicationStructure)
			{
				this.threadCommunicationStructure.gameNew = game;
				this.threadCommunicationStructure.isSoloPlayer = isSoloPlayer;
				this.threadCommunicationStructure.notify();
			}
		}
	}
	
	private void setMenuEnabled()
	{
		if (this.t != null && this.t.getGame() != null && !this.t.getGame().isInitial())
		{
			Game game = this.t.getGame();
			this.menuParameters.setEnabled(game.isParameterChangeEnabled());
			this.menuSave.setEnabled(game.isParameterChangeEnabled() && !game.isSoloPlayer());
			this.menuEmailSend.setEnabled(game.getOptions().contains(GameOptions.EMAIL_BASED) ||
					game.getOptions().contains(GameOptions.SERVER_BASED));
		}
		else
		{
			this.menuParameters.setEnabled(false);
			this.menuSave.setEnabled(false);
			this.menuEmailSend.setEnabled(false);
		}
		
		this.menuServerGames.setEnabled(this.serverCommunicationEnabled);
		
		this.updateTitle();
	}
	
	private void closeStern()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		DialogWindowResult result = DialogWindow.showYesNo(
				this,
				SternResources.MoechtestDuSternVerlassen(false),
				SternResources.SternVerlassen(false));
		
		if (result == DialogWindowResult.YES)
			System.exit(0);
		
		this.inputEnabled = true;
		this.redrawScreen();
	}
	
	@SuppressWarnings("unchecked")
	private Properties getProperties()
	{
		Reader reader = null;
		Properties properties = new Properties(); 

		try
		{
		  reader = new FileReader(PROPERTIES_FILE_NAME);

		  properties.load( reader );
		}
		catch ( Exception e )
		{
		}
		finally
		{
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
		if (properties.containsKey(PROPERTY_NAME_DIRECTORY_NAME_LAST))
			this.directoryNameLast = properties.getProperty(PROPERTY_NAME_DIRECTORY_NAME_LAST);
		
		if (properties.containsKey(PROPERTY_EMAILS))
		{
			String emailBase64 = properties.getProperty(PROPERTY_EMAILS);
			this.emails = 
					(ArrayList<String>) Utils.convertFromBase64(emailBase64, ArrayList.class, null);
		}
		
		if (properties.containsKey(PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE))
			this.serverAdminCredentialFile = properties.getProperty(PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE);
		
		if (properties.containsKey(PROPERTY_SERVER_USER_CREDENTIAL_FILE))
		{
			this.serverUserCredentialsFile = properties.getProperty(PROPERTY_SERVER_USER_CREDENTIAL_FILE);
			this.cuc = ServerUtils.readClientUserCredentials(this.serverUserCredentialsFile);
		}
		
		if (properties.containsKey(PROPERTY_SERVER_COMMUNICATION_ENABLED))
			this.serverCommunicationEnabled = Boolean.parseBoolean(properties.getProperty(PROPERTY_SERVER_COMMUNICATION_ENABLED));
		
		if (properties.containsKey(PROPERTY_NAME_LANGUAGE))
		{
			String languageCode = properties.getProperty(PROPERTY_NAME_LANGUAGE);
			if (languageCode != null)
				SternResources.setLocale(languageCode);
		}
		
		if (properties.containsKey(PROPERTY_EMAIL_SEPARATOR))
			this.emailSeparator = properties.getProperty(PROPERTY_EMAIL_SEPARATOR);
		
		if (properties.containsKey(PROPERTY_MUTE_NOTIFICATION_SOUND))
			this.muteNotificationSound = Boolean.parseBoolean(properties.getProperty(PROPERTY_MUTE_NOTIFICATION_SOUND));
		
		if (properties.containsKey(PROPERTY_IP_ADDRESS))
			this.myIpAddress = properties.getProperty(PROPERTY_IP_ADDRESS);
		
		if (properties.containsKey(PROPERTY_CLIENTS_INACTIVE))
			this.clientsInactiveWhileEnterMoves = 
				Boolean.parseBoolean(properties.getProperty(PROPERTY_CLIENTS_INACTIVE));
		
		if (properties.containsKey(PROPERTY_LAST_UPDATE_FOUND))
 			this.lastUpdateCheck = properties.getProperty(PROPERTY_LAST_UPDATE_FOUND);
		
		return properties;
	}
	
	private static void writeProperties(Properties properties)
	{
		Writer writer = null;

		try
		{
		  writer = new FileWriter(PROPERTIES_FILE_NAME);

		  properties.store( writer, "STERN properties" );
		}
		catch ( IOException e )
		{
		  e.printStackTrace();
		}
		finally
		{
		  try { writer.close(); } catch ( Exception e ) { }
		}
	}
	
	void setProperty(String propertyName, String propertyValue)
	{
		if (propertyValue == null)
			return;
		
		this.properties.setProperty(propertyName, propertyValue);
		writeProperties(this.properties);
	}
	
	private void createBackup (String fileName) throws IOException
	{
		InputStream in = null;
		OutputStream out = null; 
		
		if (!new File(fileName).exists())
			return;
		
		String fileNameBackup = fileName + FILE_SUFFIX_BACKUP;
		
		byte[] buffer = new byte[1000000];
		
		try {
			in = new FileInputStream(fileName);
			out = new FileOutputStream(fileNameBackup);
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead); 
				}
			} 
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public void rmiKeyPressed(String clientId, String languageCode, int id, long when, int modifiers, int keyCode, char keyChart) throws RemoteException
	{
		KeyEvent event = new KeyEvent(this.paintPanel, id, when, modifiers, keyCode, keyChart);
		
		if (this.serverFunctions.isClientRegistered(clientId))
			this.keyPressed(new KeyEventExtended(event, clientId, languageCode));
	}

	@Override
	public String rmiClientConnectionRequest(
			String clientId, 
			String release, 
			String ip,
			String clientCode, 
			String clientName) 
					throws RemoteException
	{
		if (release.equals(ReleaseGetter.getRelease()))
			return this.serverFunctions.connectClient(
					clientId, 
					ip, 
					clientCode, 
					clientName, 
					this.clientsInactiveWhileEnterMoves);
		else
			return SternResources.UnterschiedlicheBuilds(false);
	}

	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
		this.keyPressed(new KeyEventExtended(arg0, null, languageCode));
	}

	@Override
	public ScreenContentClient rmiGetCurrentScreenDisplayContent(String clientId)
			throws RemoteException
	{
		if (this.serverFunctions.isClientRegistered(clientId))
		{
			ScreenContentClient contentClient = new ScreenContentClient();
			contentClient.screenContent = this.paintPanel.getScreenContent();
			contentClient.inputEnabled = this.inputEnabled;
			
			return contentClient; 
		}
		else
			return null;
	}
	
	@Override
	public void rmiClientLogoff(String clientId) throws RemoteException
	{
		this.serverFunctions.disconnectClient(clientId);
	}

	@Override
	public boolean rmiClientCheckRegistration(String clientId)
	{
		return this.serverFunctions.isClientRegistered(clientId);
	}

	@Override
	public void addToHighscore(Archive archive, Player[] players)
	{
		for (int playerIndex = 0; playerIndex < players.length; playerIndex++)
		{
			int score = archive.getScore()[playerIndex]; 
			
			if (score <= 0)
				continue;
			
			int insertIndex = -1;
			boolean dontAdd = false;
			
			for (int entryIndex = 0; entryIndex < this.highscoreEntries.list.size(); entryIndex++)
			{
				HighscoreEntry entry = this.highscoreEntries.list.get(entryIndex);
				
				if (score >= entry.score)
				{
					insertIndex = entryIndex;
					
					for (int entryIndex2 = entryIndex; entryIndex2 < this.highscoreEntries.list.size(); entryIndex2++)
					{
						HighscoreEntry entry2 = this.highscoreEntries.list.get(entryIndex2);
						
						if (entry2.score != score)
							break;
						
						if (players[playerIndex].getName().toUpperCase().equals(entry2.playerName.toUpperCase()))
						{
							dontAdd = true;
							break;
						}
					}
				}
				
				if (dontAdd || insertIndex >= 0)
					break;
			}
			
			if (dontAdd)
				continue;
			
			HighscoreEntry newEntry = new HighscoreEntry();
			newEntry.playerName = players[playerIndex].getName();
			newEntry.score = score;
			
			if (insertIndex >= 0)
				this.highscoreEntries.list.add(insertIndex, newEntry);
			else
				this.highscoreEntries.list.add(newEntry);
			
			while (this.highscoreEntries.list.size() > Stern.HIGHSCORE_ENTRIES_COUNT)
				this.highscoreEntries.list.remove(Stern.HIGHSCORE_ENTRIES_COUNT);
		}
		
		String jsonString = new Gson().toJson(this.highscoreEntries);
		
		try {
			File file = new File(HIGHSCORES_FILE_NAME);
			FileOutputStream fs = new FileOutputStream(file.getPath());
			GZIPOutputStream zipout = new GZIPOutputStream(fs);
			ObjectOutputStream os = new ObjectOutputStream(zipout);
			os.writeObject(jsonString);
			os.close();
		} catch (Exception e) {
		}
	}
	
	private void loadHighscores()
	{
		boolean error = true;
		File file = new File(HIGHSCORES_FILE_NAME);
		if (file.exists())
		{
			try {
				FileInputStream fs = new FileInputStream(file.getPath());
				GZIPInputStream zipin = new GZIPInputStream (fs);
				ObjectInputStream is = new ObjectInputStream(zipin);
				String jsonString = (String)is.readObject();
				is.close();
				
				this.highscoreEntries = new Gson().fromJson(jsonString, HighscoreEntries.class);
				error = false;
			} catch (Exception e) {
				error = true;
			}
		}
		
		if (error)
			this.highscoreEntries = new HighscoreEntries();
	}
	
	private void updateTitle()
	{
		String fileName = (this.t != null && 
						   this.t.getGame() != null && 
						   this.t.getGame().getName() != null &&
						   this.t.getGame().getName().length() > 0) ?
							" <" + this.t.getGame().getName() + ">" :
							"";
		
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			this.setTitle(
					SternResources.SternTerminalServer(false)+
					fileName);
		else
			this.setTitle(SternResources.SternTitel(false) + fileName);
	}

	@Override
	public boolean openPdf(byte[] pdfBytes, String clientId)
	{
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			return this.serverFunctions.openPdf(pdfBytes, clientId);
		else
			return false;
	}
	
	@Override
	public void pause(int milliseconds)
	{
		this.inputEnabled = false;
		WaitThread t = new WaitThread(this, milliseconds);
		t.start();
	}
	
	private void resumeAfterPause()
	{
		this.inputEnabled = true;
		synchronized(this.threadCommunicationStructure)
		{
			this.threadCommunicationStructure.notify();
		}
	}
	
	private class WaitThread extends Thread
	{
		private Stern parent;
		private int milliseconds;
		
		public WaitThread(Stern parent, int milliseconds)
		{
			this.parent = parent;
			this.milliseconds = milliseconds;
		}
		
		@Override
		public void run()
		{
			super.run();
			
			try {
				Thread.sleep(this.milliseconds);
			} catch (InterruptedException e) {
			}
			
			this.parent.resumeAfterPause();
		}
	}

	@Override
	public void checkMenuEnabled() {
		this.setMenuEnabled();
	}
	
	private void setEmailsProperty()
	{
		String base64 = Utils.convertToBase64(this.emails, null);
		this.setProperty(PROPERTY_EMAILS, base64);
	}

	@Override
	public boolean launchEmailClient(String recipient, String subject, String bodyText, EmailTransportBase obj)
	{
		return EmailToolkit.launchEmailClient(
				this,
				recipient, 
				subject, 
				bodyText, 
				null, 
				obj);
	}

	@Override
	public MovesTransportObject importMovesFromEmail()
	{
		this.inputEnabled = false;
		this.redrawScreen();

		// ---------
		ClipboardImportJDialog<MovesTransportObject> dlg = 
				new ClipboardImportJDialog<MovesTransportObject>(
						this, MovesTransportObject.class, false);
		
		dlg.setVisible(true);
		// ---------
		
		MovesTransportObject movesTransportObject = (MovesTransportObject)dlg.obj;
		
		if (movesTransportObject != null)
		{
			if (!RequiredBuildChecker.doCheck(this, movesTransportObject.getBuildRequired()))
				movesTransportObject = null;
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
		this.updateTitle();

		return movesTransportObject;
	}
	
	private ResponseMessage sendAndReceive(ClientUserCredentials cuc, RequestMessage msg)
	{
		ResponseMessage respMsg = null;
		
		if (this.serverCommunicationEnabled)
		{
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			respMsg = ClientSocketManager.sendAndReceive(cuc, msg);
			this.setCursor(Cursor.getDefaultCursor());
			
		}
		else
		{
			respMsg = new ResponseMessage();
			respMsg.error = true;
			respMsg.errorMsg = SternResources.ServerKommunikationInaktiv(false);
			respMsg.payloadSerialized = PostMovesResult.ERROR.toString();
		}
		
		if (respMsg.error)
			DialogWindow.showError(
					this,
				    SternResources.getString(respMsg.errorMsg),
				    SternResources.Verbindungsfehler(false));
		
		return respMsg;

	}

	@Override
	public PostMovesResult postMovesToServer(String gameId, String playerName, MovesTransportObject movesTransportObject)
	{
		if (this.cuc.userId.equals(playerName))
		{		
			RequestMessage msg = new RequestMessage(RequestMessageType.POST_MOVES);
			
			RequestMessagePostMoves msgPayload = new RequestMessagePostMoves();
			
			msgPayload.gameId = gameId;
			msgPayload.moves = movesTransportObject;
			
			msg.payloadSerialized = msgPayload.toJson();
			
			ResponseMessage respMessage = this.sendAndReceive(this.cuc, msg);
			
			PostMovesResult result = PostMovesResult.valueOf(respMessage.payloadSerialized);
			
			return result;
		}
		else
			return PostMovesResult.USER_NOT_CONNECTED;
	}
	
	private void getGameInfoByTimer()
	{
		if (!this.serverCommunicationEnabled ||
			 ClientSocketManager.isBusy())
			return;
		
		boolean connected = false;
		boolean currentGameNextYear = false;
		boolean gamesWaitingForInput = false;
		boolean beep = false;
		
		if (this.cuc != null)
		{
			RequestMessageGetStatus msgPayload = new RequestMessageGetStatus();
			
			if (this.t != null && this.t.getGame() != null &&
				this.t.getGame().getOptions().contains(GameOptions.SERVER_BASED))
			{
				msgPayload.currentGameId = this.currentGameId;
				msgPayload.currentGameYear = this.currentGameJahr;
			}
								
			RequestMessage msg = new RequestMessage(RequestMessageType.GET_STATUS);
			msg.payloadSerialized = msgPayload.toJson();
			
			ResponseMessage respMsg = ClientSocketManager.sendAndReceive(this.cuc, msg);
			if (!respMsg.error)
			{
				connected = true;
				
				ResponseMessageGetStatus respMsgPayload;
				try {
					respMsgPayload = (ResponseMessageGetStatus)
							ResponseMessageGetStatus.fromJson(respMsg.payloadSerialized, ResponseMessageGetStatus.class);
				} catch (Exception e) {
					respMsgPayload = new ResponseMessageGetStatus();
				}
				
				currentGameNextYear = respMsgPayload.currentGameNextYear;
				gamesWaitingForInput = respMsgPayload.gamesWaitingForInput;
			}
		}
		
		if (connected)
		{
			this.labConnectionStatus.setIcon(iconConnected);
			this.labConnectionStatus.setToolTipText(
					SternResources.VerbundenMitServer(
							false, 
							this.cuc.url, 
							Integer.toString(this.cuc.port),
							this.cuc.userId));
			
			if (this.labGamesWaitingForInput.isVisible() == false &&
				gamesWaitingForInput == true)
			{
				beep = true;
			}
			
			this.labGamesWaitingForInput.setVisible(gamesWaitingForInput);
			this.labGamesWaitingForInput.setToolTipText(SternResources.MitspielerWarten(false));
			
			if (this.labCurrentGameNextYear.isVisible() == false &&
				currentGameNextYear == true)
			{
				beep = true;
			}
			
			this.labCurrentGameNextYear.setVisible(currentGameNextYear);
			this.labCurrentGameNextYear.setToolTipText(SternResources.AuswertungVerfuegbarSymbol(false));
		}
		else
		{
			this.labConnectionStatus.setIcon(iconNotConnected);
			
			if (this.cuc == null)			
				this.labConnectionStatus.setToolTipText(
					SternResources.ServerZugangsdatenNichtHinterlegt(false));
			else
				this.labConnectionStatus.setToolTipText(
					SternResources.ClientSettingsJDialogKeineVerbindung2(
							false, this.cuc.url));
					
			this.labCurrentGameNextYear.setVisible(false);
			this.labGamesWaitingForInput.setVisible(false);
		}
		
		if (this.muteNotificationSound == false &&
				this.soundClipNotification != null && 
				beep)
		{
			if (this.soundClipNotification.isRunning())
				this.soundClipNotification.stop();
			this.soundClipNotification.setFramePosition(0);
			this.soundClipNotification.start();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == this.labGamesWaitingForInput)
			this.openServerGamesDialog();
		else if (e.getSource() == this.labCurrentGameNextYear)
			this.reloadCurrentGame();
		else if (e.getSource() == this.labConnectionStatus)
			this.openServerCredentialsDialog();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	private void openServerCredentialsDialog()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		this.getGameInfoByTimer();
		ServerCredentialsJDialog dlg = new ServerCredentialsJDialog(
				this, 
				SternResources.ServerZugangsdaten(false),
				this.serverCommunicationEnabled,
				this.serverUserCredentialsFile,
				this.muteNotificationSound);
		dlg.setVisible(true);
		
		if (dlg.ok)
		{
			this.muteNotificationSound = dlg.muteNotificationSound;
			this.setProperty(
					Stern.PROPERTY_MUTE_NOTIFICATION_SOUND, Boolean.toString(this.muteNotificationSound));

			this.serverUserCredentialsFile = dlg.serverUserCredentialsFile;
			this.serverCommunicationEnabled = dlg.serverCommunicationEnabled;
			this.panToolbar.setVisible(this.serverCommunicationEnabled);
			this.setMenuEnabled();
			this.cuc = ServerUtils.readClientUserCredentials(this.serverUserCredentialsFile);
			
			this.setProperty(PROPERTY_SERVER_USER_CREDENTIAL_FILE, dlg.serverUserCredentialsFile);
			this.setProperty(PROPERTY_SERVER_COMMUNICATION_ENABLED, Boolean.toString(this.serverCommunicationEnabled));
			this.getGameInfoByTimer();
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

	}
	
	private void openServerGamesDialog()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		if (this.cuc == null)
		{
			DialogWindow.showError(
					this,
				    SternResources.ServerZugangsdatenNichtHinterlegt(false),
				    SternResources.Fehler(false));
		}
		else
		{
			this.getGameInfoByTimer();
			
			RequestMessage msg = new RequestMessage(RequestMessageType.GET_GAMES_AND_USERS);
			
			RequestMessageGamesAndUsers msgPayload = new RequestMessageGamesAndUsers();
			msgPayload.userId = this.cuc.userId;
			
			msg.payloadSerialized = msgPayload.toJson();
			
			ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
			
			if (!respMsg.error)
			{
				ResponseMessageGamesAndUsers gamesAndUser;
				try {
					gamesAndUser = (ResponseMessageGamesAndUsers)
							ResponseMessageGamesAndUsers.fromJson(respMsg.payloadSerialized, ResponseMessageGamesAndUsers.class);
				} catch (Exception e) {
					gamesAndUser = new ResponseMessageGamesAndUsers();
				}
				
				ServerGamesJDialog dlg = new ServerGamesJDialog(
						this, 
						SternResources.ServerbasierteSpiele(false, this.cuc.userId),
						this.currentGameId,
						this.cuc,
						gamesAndUser);
				dlg.setVisible(true);
				
				if (dlg.gameLoaded != null)
				{
					if (RequiredBuildChecker.doCheck(this, dlg.gameLoaded.getBuildRequired()))
					{
						this.currentGameJahr = dlg.gameLoaded.getYear();
						this.setNewGame(dlg.gameLoaded, true);
					}
				}
				this.getGameInfoByTimer();
			}
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

	}
	
	private void activateWebServer()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		if (this.webserver == null)
		{
			this.webserver = new Webserver(this);
			webserver.start();
			this.menuWebserver.setLabel(SternResources.MenuWebserverDeaktivieren(false));
			
			String url = this.webserver.getUrl();
			
			DialogWindow.showInformation(
					this, 
					new MessageWithLink(
							this,
							"<a href=\""+url+"\">"+url+"</a>"),
					SternResources.WebserverAktiviert(false));
		}
		else
		{
			this.webserver.stop();
			this.webserver = null;
			this.menuWebserver.setLabel(SternResources.MenuWebserverAktivieren(false));
			
			DialogWindow.showInformation(
					this, 
					SternResources.WebserverDeaktiviert(false), 
					SternResources.Webserver(false));
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

	}
	
	private void reloadCurrentGame()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		RequestMessage msg = new RequestMessage(RequestMessageType.GET_GAME);
		
		msg.payloadSerialized = this.currentGameId;
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			Gson gson = new Gson();
			Game game = gson.fromJson(respMsg.payloadSerialized, Game.class); 
			
			if (RequiredBuildChecker.doCheck(this, game.getBuildRequired()))
			{
				this.currentGameJahr = game.getYear();
				this.setNewGame(game, true);
			}		
		}
		
		this.getGameInfoByTimer();
		
		this.inputEnabled = true;
		this.redrawScreen();
	}

	@Override
	public void updateGameInfo()
	{
		this.getGameInfoByTimer();
	}
	
	@Override
 	public void lastUpdateFound(String updateBuild) 
 	{
 		this.setProperty(PROPERTY_LAST_UPDATE_FOUND, updateBuild);
 	}

	public boolean areClientsInactiveWhileEnterMoves() // NO_UCD (use default)
	{
		return this.clientsInactiveWhileEnterMoves;
	}
	
	public void setClientsInactiveWhileEnterMoves(boolean enabled)
	{
		this.clientsInactiveWhileEnterMoves = enabled;
		
		this.setProperty(PROPERTY_CLIENTS_INACTIVE, Boolean.toString(this.clientsInactiveWhileEnterMoves));
	}

	@Override
	public boolean isMoveEnteringOpen() 
	{
		if ((this.serverFunctions != null && this.serverFunctions.isServerEnabled() && !this.areClientsInactiveWhileEnterMoves()))
		{
			return false;
		}
		
		return
				(this.outputWindow != null && this.outputWindow.isVisible()) ||
				(this.serverFunctions != null && this.serverFunctions.isServerEnabled() && this.areClientsInactiveWhileEnterMoves());
	}
	
	public ScreenContent getScreenContentStartOfYear()
	{
		if (this.t != null && this.t.getGame() != null)
		{
			return this.t.getGame().getScreenContentStartOfYear();
		}
		else
		{
			return null;
		}
	}
}
