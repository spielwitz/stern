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
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.google.gson.Gson;

import common.Archiv;
import common.Constants;
import common.EmailTransportBase;
import common.ISpielThreadEventListener;
import common.KeyEventExtended;
import common.PostMovesResult;
import common.ReleaseGetter;
import common.ScreenDisplayContentClient;
import common.ScreenUpdateEvent;
import common.Spiel;
import common.SpielOptionen;
import common.SpielThread;
import common.SpielThreadCommunicationStructure;
import common.Spieler;
import common.SpielzuegeEmailTransport;
import common.SternResources;
import common.Utils;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageGamesAndUsers;
import commonServer.RequestMessageGetEvaluations;
import commonServer.RequestMessagePostMoves;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonServer.ResponseMessageGetEvaluations;
import commonServer.ServerUtils;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
import commonUi.IUpdateCheckerCallback;
import commonUi.LabelDark;
import commonUi.PaintPanel;
import commonUi.PanelDark;
import commonUi.ServerFunctions;
import commonUi.SpracheJDialog;
import commonUi.SpringUtilities;
import commonUi.SternAbout;
import commonUi.UpdateChecker;

@SuppressWarnings("serial") 
public class Stern extends Frame  // NO_UCD (use default)
	implements 
		WindowListener, 
		ISpielThreadEventListener, 
		ActionListener,
		MouseListener,
		IServerMethods,
		IHostComponentMethods,
		IUpdateCheckerCallback
{
	transient private final static String FILE_SUFFIX = ".stn";
	transient private final static String FILE_SUFFIX_BACKUP = ".BAK";
	transient private final static String FILE_SUFFIX_IMPORT = ".VEG";
	
	transient private static final String PROPERTIES_FILE_NAME = "SternProperties";
	transient private static final String HIGHSCORES_FILE_NAME = "SternHighscores";
	transient private static final String PROPERTY_NAME_LETZTES_VERZEICHNIS = "lastDir";
	transient private static final String PROPERTY_EMAIL_ADRESSEN = "emailAdressen";
	transient static final String PROPERTY_EMAIL_SEPARATOR = "emailSeparator";
	transient private static final String PROPERTY_LAST_UPDATE_FOUND = "lastUpdateFound";
	transient private static final String PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE = "serverAdminCredentials";
	transient private static final String PROPERTY_SERVER_USER_CREDENTIAL_FILE = "serverUserCredentials";
	transient private static final String PROPERTY_SERVER_COMMUNICATION_ENABLED = "serverCommunicationEnabled";
	transient private static final String PROPERTY_NAME_SPRACHE = "sprache";
	
	static final int HIGHSCORE_NUM_ENTRIES = 20;
	
	private SpielThread t;
	private SpielThreadCommunicationStructure threadCommunicationStructure;
	
	private Spiel letztesSpielRohdaten;
	private String letztesFile;
	private String letztesVerzeichnis;
	String emailSeparator;
	
	private Properties props;
	
	private ServerFunctions serverFunctions;
	
	private HighscoreEntries highscoreEntries;
	
	private ArrayList<String> emailAdressen = new ArrayList<String>();
	
	private ClientUserCredentials cuc;
	private String serverAdminCredentialFile;
	private String serverUserCredentialsFile;
	private boolean serverCommunicationEnabled;
	
	// Menues
    private MenuItem menuNeuesSpiel;
    private MenuItem menuLaden;
    private MenuItem menuEmailClipboard;
    private MenuItem menuParameter;
    private MenuItem menuEmail;
    private MenuItem menuSpeichern;
    private MenuItem menuServerAdmin;
    private MenuItem menuServerGames;
    private MenuItem menuServerCredentials;
    private MenuItem menuSprache;
    private MenuItem menuSearchForUpdates;
    
    private MenuItem menuServer;
    
    private MenuItem menuHighscore;
    
    private MenuItem menuQuit;
    
    private MenuItem menuHilfe;
    private MenuItem menuAbout;
	
	// UI components
	private PaintPanel paintPanel;
	private PanelDark panToolbar;
	private LabelDark labConnectionStatus;
	private LabelDark labRefresh;
	
	private ImageIcon iconConnected;
	private ImageIcon iconNotConnected;
	private ImageIcon iconStern;
	
	
	private boolean inputEnabled;
	private String currentGameId;
	
	private Timer gameInfoUpdateTimer;
	
	private String lastUpdateCheck;
	
	public static void main(String[] args)
	{
		Hashtable<String,String> argsTable = Utils.resolveProgramArgs(args);
		
		String meineIp = argsTable.containsKey("ip") ?
								argsTable.get("ip") :
								null;
		
		new Stern(meineIp);
	}

	private Stern(String meineIp)
	{
		super();
		
		System.setProperty("java.net.preferIPv4Stack" , "true");
		
		try {
		    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		    UIManager.put("ComboBox.disabledBackground", Color.black);
		 } catch (Exception e) {
		            e.printStackTrace();
		 }
		
		// Properties lesen
		this.props = this.getProperties();
		
		// Highscoreliste lesen
		this.getHighscores();
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.addWindowListener(this);
		this.setFocusable(true); // Achtung, das Panel hat den KeyListener!
		this.setLayout(new BorderLayout());
		
		this.setMenuBar(this.getMenubar());
		this.setMenuEnabled();
		
		this.paintPanel = new PaintPanel(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		// Toolbar
		this.iconConnected = new ImageIcon (ClassLoader.getSystemResource("iconConnected.png"));
		this.iconNotConnected = new ImageIcon (ClassLoader.getSystemResource("iconNotConnected.png"));
		this.iconStern = new ImageIcon (ClassLoader.getSystemResource("iconStern.png"));
		
		this.panToolbar = new PanelDark(new SpringLayout());
		PanelDark panToolBarShell = new PanelDark(new BorderLayout());
		PanelDark panToolBar = new PanelDark(new GridLayout(2,1,10,5));
		
		this.labRefresh = new LabelDark(this.iconStern, 30, true);
		this.labRefresh.setVisible(false);
		this.labRefresh.addMouseListener(this);
		
		panToolBar.add(this.labRefresh);
		
		this.labConnectionStatus = new LabelDark(this.iconNotConnected, 30, false);
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
		
		// RMI-Server-Objekt anlegen, aber Server noch nicht starten
		this.serverFunctions = new ServerFunctions(meineIp);
						
		// Muss ganz zum Schluss kommen, damit der Tastaturfokus zieht
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.updateTitle();
		this.paintPanel.requestFocusInWindow(); // Muss nach SetVisible kommen!
		
		// Timer starten
		this.gameInfoUpdateTimer = new Timer(30000, this);
		this.gameInfoUpdateTimer.start();
		
		// Verbindungsstatus setzen
		this.getGameInfoByTimer();
		
		// Auf Updates checken
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

	private Spiel spielImport(String filePath)
	{
		byte[] bytes = null;
		Spiel spiel = null;
		
		try {
			Path path = Paths.get(filePath);
			String spielName = path.getFileName().toString();
			if (spielName.contains("."));
				spielName = spielName.substring(0, spielName.indexOf("."));
			
			 bytes = Files.readAllBytes(path);
			 spiel = Spiel.importFromOldVega(spielName, bytes);
		} catch (IOException e)
		{
			spiel = null;
		}
		
		return spiel;

	}
	private MenuBar getMenubar()
	{
	    MenuBar menueLeiste = new MenuBar ();
	    
	    Menu stern = new Menu (SternResources.MenuHilfe(false));
	    
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHilfe = new MenuItem (SternResources.MenuSpielanleitung(false));
		    this.menuHilfe.addActionListener(this);
		    stern.add (this.menuHilfe);
	    }
	    
	    this.menuAbout = new MenuItem (SternResources.MenuUeberStern(false));
	    this.menuAbout.addActionListener(this);
	    stern.add (this.menuAbout);
	    
	    this.menuSearchForUpdates = new MenuItem (SternResources.MenuSearchForUpdates(false));
	    this.menuSearchForUpdates.addActionListener(this);
	    stern.add (this.menuSearchForUpdates);
	    
	    stern.addSeparator();
	    
	    this.menuSprache = new MenuItem(SternResources.MenuSpracheinstellungen(false));
	    this.menuSprache.addActionListener(this);
	    stern.add(this.menuSprache);
	    
	    stern.addSeparator();
	    
	    this.menuQuit = new MenuItem (SternResources.MenuSternVerlassen(false));
	    this.menuQuit.addActionListener(this);
	    stern.add (this.menuQuit);
	    
	    menueLeiste.add(stern);
	    
	    // -----------
	    
	    Menu spiel = new Menu (SternResources.MenuDatei(false));

	    this.menuNeuesSpiel = new MenuItem (SternResources.MenuNeuesSpiel(false));
	    this.menuNeuesSpiel.addActionListener(this);
	    spiel.add(this.menuNeuesSpiel);
	    
	    this.menuLaden = new MenuItem (SternResources.MenuSpielLaden(false));
	    this.menuLaden.addActionListener(this);
	    spiel.add (menuLaden);
	    
	    this.menuSpeichern = new MenuItem (SternResources.MenuSpielSpeichernAls(false));
	    this.menuSpeichern.addActionListener(this);
	    spiel.add (menuSpeichern);
	    
	    this.menuHighscore = new MenuItem(SternResources.MenuBestenliste(false));
	    this.menuHighscore.addActionListener(this);
	    spiel.add(this.menuHighscore);

	    spiel.addSeparator();
	    
	    this.menuEmailClipboard = new MenuItem (SternResources.MenuSpielAusZwischenablageLaden(false));
	    this.menuEmailClipboard.addActionListener(this);
	    spiel.add (menuEmailClipboard);
	    
	    spiel.addSeparator();

	    this.menuServerCredentials = new MenuItem(SternResources.MenuServerCredentials(false));
	    this.menuServerCredentials.addActionListener(this);
	    spiel.add(this.menuServerCredentials);
	    
	    this.menuServerGames = new MenuItem(SternResources.MenuServerbasierteSpiele(false));
	    this.menuServerGames.addActionListener(this);
	    spiel.add(this.menuServerGames);
	    
	    spiel.addSeparator();
	    
	    this.menuEmail = new MenuItem(SternResources.MenuEmail(false));
	    this.menuEmail.addActionListener(this);
	    spiel.add(this.menuEmail);
	    
	    this.menuParameter = new MenuItem (SternResources.Spielparameter(false));
	    this.menuParameter.addActionListener(this);
	    spiel.add (this.menuParameter);
	    
	    menueLeiste.add(spiel);
	    // ----
	    
	    Menu server = new Menu (SternResources.MenuEinstellungen(false));
	    
	    this.menuServerAdmin = new MenuItem(SternResources.MenuServerAdmin(false));
	    this.menuServerAdmin.addActionListener(this);
	    server.add(this.menuServerAdmin);
	    
	    server.addSeparator();
	    
	    this.menuServer = new MenuItem(SternResources.MenuScreesharing(false));
	    this.menuServer.addActionListener(this);
	    server.add(this.menuServer);
	    
	    menueLeiste.add(server);
	    
	    // ----
	    return menueLeiste;
	}

	@Override
	public void update(ScreenUpdateEvent event)
	{
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			this.serverFunctions.updateClients(event.getContent(), this.inputEnabled);
		
		this.paintPanel.redraw(event.getContent(), this.inputEnabled);		
	}
	
	private void redrawScreen ()
	{
		this.update(new ScreenUpdateEvent(this, this.paintPanel.getScreenDisplayContent()));
	}

	@Override
	public void speichern(Spiel spiel, boolean autoSave)
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		String filename ="";
		String directory = "";
		
		if (autoSave && this.letztesFile != null && !this.letztesFile.isEmpty() &&
				        this.letztesVerzeichnis != null && !this.letztesVerzeichnis.isEmpty())
		{
			filename = this.letztesFile;
			directory = this.letztesVerzeichnis;
			
			try {
				this.createBackup(new File(directory,filename).getPath());
			} catch (IOException e) {}
		}
		else
		{
			FileDialog fd = new FileDialog(this, SternResources.SpielSpeichern(false), FileDialog.SAVE);
			
			if (this.letztesVerzeichnis != null && !this.letztesVerzeichnis.isEmpty())
				fd.setDirectory(this.letztesVerzeichnis);

			fd.setFile(spiel.getName() + FILE_SUFFIX);
			fd.setVisible(true);
			
			filename = fd.getFile();
			directory = fd.getDirectory();
		}
		
		if (filename != null && filename.length() > 0 && !filename.equals("*"+FILE_SUFFIX))
		{
			if (!filename.toLowerCase().endsWith(FILE_SUFFIX))
				filename = filename + FILE_SUFFIX;
			
			// Dateiname als Spielname übernehmen
			spiel.setName(filename.substring(0, filename.indexOf(FILE_SUFFIX)));
			
			// Mindest-Build-Version setzen
			spiel.setMinBuild(Constants.MIN_BUILD);
				
			File file = new File(directory,filename);
			
			String errorText = Utils.writeSpielToFile(spiel, file);
			
			if (errorText != null)
				JOptionPane.showMessageDialog(this,
					    errorText,
					    "",
					    JOptionPane.ERROR_MESSAGE);
			else
			{
				this.letztesVerzeichnis = directory;
				this.setProperty(PROPERTY_NAME_LETZTES_VERZEICHNIS, this.letztesVerzeichnis);
				this.letztesFile = filename;
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
		
		MenuItem item = (MenuItem)e.getSource();
		
		if (item == this.menuLaden)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Spiel spiel = this.laden();
			if (spiel != null)
				this.setNeuesSpiel(spiel, false);
						
			this.inputEnabled = true;
			this.redrawScreen();
			//this.updateTitle();
		}
		else if (item == this.menuEmailClipboard)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ClipboardImportJDialog<Spiel> dlg = 
					new ClipboardImportJDialog<Spiel>(this, Spiel.class, false);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == JOptionPane.OK_OPTION)
			{
				Spiel spiel = (Spiel)dlg.obj;
				
				if (spiel != null)
				{
					if (!MinBuildChecker.doCheck(this, spiel.getMinBuild()))
						spiel = null;
				}
				this.setNeuesSpiel(spiel, true);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuNeuesSpiel)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			SpielparameterJDialog dlg = 
					new SpielparameterJDialog(
							this,
							SternResources.Spielparameter(false),
							SpielparameterDialogModus.NEUES_SPIEL,
							this.letztesSpielRohdaten,
							this.emailAdressen);
			
			dlg.setVisible(true);
			this.setEmailAdressenProperty();
			
			if (!dlg.isAbort())
			{
				Object[] objectList = dlg.getSpieler().toArray();
				Spieler[] spielerArray =  Arrays.copyOf(objectList,objectList.length,Spieler[].class);

				Spiel spiel = new Spiel(dlg.getOptionen(), spielerArray, dlg.getEmailAdresseSpielleiter(), dlg.getAnzPl(), dlg.getMaxJahre());
				this.letztesSpielRohdaten = (Spiel)Utils.klon(spiel);
				this.letztesFile = "";
				this.setNeuesSpiel(spiel, false);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuParameter && this.t != null)
		{
			// Aenderungen waehrend des Spiels
			this.inputEnabled = false;
			this.redrawScreen();
			
			Spiel spiel = this.t.getSpiel();
			
			SpielparameterJDialog dlg = new SpielparameterJDialog(
					this, 
					SternResources.Spielparameter(false),
					spiel.getEmailSpieler() ?
							SpielparameterDialogModus.EMAIL_SPIEL :
							spiel.getAbgeschlossen() ?
									SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL :
									SpielparameterDialogModus.LAUFENDES_SPIEL,
					(Spiel)Utils.klon(spiel),
					this.emailAdressen);
			
			dlg.setVisible(true);
			this.setEmailAdressenProperty();
			
			if (!dlg.isAbort())
				spiel.changeSpielData(dlg.getOptionen(), dlg.getMaxJahre(), dlg.getEmailAdresseSpielleiter(), dlg.getSpieler());
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuEmail && this.t != null)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			Spiel spiel = this.t.getSpiel();
			
			EmailCreatorJDialog dlg = new EmailCreatorJDialog(
					this, 
					spiel.getSpieler(),
					spiel.getEmailAdresseSpielleiter(),
					this.emailSeparator,
					"[STERN] " + spiel.getName(),
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
		else if (item == this.menuSpeichern && this.t != null)
		{
			this.speichern(this.t.getSpiel(), false);
		}
		else if (item == this.menuQuit)
		{
			this.closeStern();
		}
		else if (item == this.menuServer)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			ServerSettingsJDialog dlg = new ServerSettingsJDialog(this, SternResources.Terminalserver(false), true, this.serverFunctions);
			dlg.setVisible(true);
			
			this.updateTitle();
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuServerAdmin)
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
		else if (item == this.menuServerCredentials)
		{
			this.openServerCredentialsDialog();
		}
		else if (item == this.menuServerGames)
		{
			this.openServerGamesDialog();
		}
		else if (item == this.menuHighscore)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			HighscoreJDialog dlg = new HighscoreJDialog(this, SternResources.Bestenliste(false), true, this.highscoreEntries.list);
			dlg.setVisible(true);
						
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuHilfe)
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
		else if (item == this.menuSprache)
		{
			this.inputEnabled = false;
			this.redrawScreen();
			
			SpracheJDialog dlg = new SpracheJDialog(
					this, 
					SternResources.getLocale());
			dlg.setVisible(true);
			
			if (dlg.ok)
			{
				// Neue Sprache uebernehmen
				SternResources.setLocale(dlg.languageCode);
				
				this.props.setProperty(PROPERTY_NAME_SPRACHE, dlg.languageCode);
				writeProperties(this.props);
				
				// Anwendung schließen
				System.exit(0);
			}
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuAbout)
		{
			this.inputEnabled = false;
			this.redrawScreen();

			SternAbout.show(this);
			
			this.inputEnabled = true;
			this.redrawScreen();
		}
		else if (item == this.menuSearchForUpdates)
		{
			Thread tUpdateChecker = new Thread( new UpdateChecker(this, null, false) );
			tUpdateChecker.start();
		}
		this.setMenuEnabled();
	}
	
	private Spiel laden()
	{
		Spiel spiel = null;
		
		FileDialog fd = new FileDialog(this, SternResources.SpielLaden(false), FileDialog.LOAD);
		
		if (this.letztesVerzeichnis != null)
			fd.setDirectory(this.letztesVerzeichnis);
		
		fd.setFile("*"+FILE_SUFFIX+";*"+FILE_SUFFIX_BACKUP + ";*" + FILE_SUFFIX_IMPORT);			
		
		fd.setVisible(true);
		String filename = fd.getFile();
		if (filename != null)
		{
			File file = new File(fd.getDirectory(),filename);
			if (file.exists())
			{
				boolean error = false;
				String	errorText = "";
				boolean importVonWindows16 = false;
				
				spiel = Utils.readSpielFromFile(file);
				
				if (spiel == null)
				{
					// Versuche, ein Vega-File zu importieren
					spiel = this.spielImport(file.getPath());
					
					if (spiel == null)
					{						
						errorText = SternResources.DateiNichtGueltig(false);
						error = true;
					}
					else
						importVonWindows16 = true;
				}
				
				if (error == true)
				{
					JOptionPane.showMessageDialog(this,
						    errorText,
						    SternResources.FehlerBeimLaden(false),
						    JOptionPane.ERROR_MESSAGE);
					
					spiel = null;
				}
				else if (!importVonWindows16)
				{
					if (!MinBuildChecker.doCheck(this, spiel.getMinBuild()))
						spiel = null;
				}
				
				if (spiel != null)
				{
					this.letztesVerzeichnis = fd.getDirectory(); 
					this.setProperty(PROPERTY_NAME_LETZTES_VERZEICHNIS, this.letztesVerzeichnis);

					if (!importVonWindows16)
						this.letztesFile = filename;
					
					spiel.getOptionen().remove(SpielOptionen.SERVER_BASIERT);
				}
			}
			else
				JOptionPane.showMessageDialog(
						this, 
						SternResources.DateiExistiertNicht(false), 
						SternResources.FehlerBeimLaden(false),
						JOptionPane.OK_OPTION);
		}
		
		return spiel;
	}

	private void setNeuesSpiel(Spiel spiel, boolean emailSpieler)
	{
		this.inputEnabled = true;
		this.currentGameId = spiel.getName();
		
		if (this.t == null)
		{
			this.threadCommunicationStructure = new SpielThreadCommunicationStructure();
			this.threadCommunicationStructure.emailSpieler = emailSpieler;
			this.t = new SpielThread(this.threadCommunicationStructure, this, spiel);
			this.t.start();
		}
		else
		{
			synchronized(this.threadCommunicationStructure)
			{
				this.threadCommunicationStructure.neuesSpiel = spiel;
				this.threadCommunicationStructure.emailSpieler = emailSpieler;
				this.threadCommunicationStructure.notify();
			}
		}
	}
	
	private void setMenuEnabled()
	{
		if (this.t != null && this.t.getSpiel() != null && !this.t.getSpiel().istInitial())
		{
			Spiel sp = this.t.getSpiel();
			this.menuParameter.setEnabled(true);
			this.menuSpeichern.setEnabled(sp.isParameterChangeEnabled() && !sp.getEmailSpieler());
			this.menuEmail.setEnabled(sp.getOptionen().contains(SpielOptionen.EMAIL) ||
					sp.getOptionen().contains(SpielOptionen.SERVER_BASIERT));
		}
		else
		{
			this.menuParameter.setEnabled(false);
			this.menuSpeichern.setEnabled(false);
			this.menuEmail.setEnabled(false);
		}
		
		this.menuServerGames.setEnabled(this.serverCommunicationEnabled);
		
		this.updateTitle();
	}
	
	private void closeStern()
	{
		this.inputEnabled = false;
		this.redrawScreen();
		
		int result = JOptionPane.showConfirmDialog(
				this,
				SternResources.MoechtestDuSternVerlassen(false),
				SternResources.SternVerlassen(false),
				JOptionPane.YES_NO_OPTION);
		
		if (result == JOptionPane.YES_OPTION)
			System.exit(0);
		
		this.inputEnabled = true;
		this.redrawScreen();
	}
	
	@SuppressWarnings("unchecked")
	private Properties getProperties()
	{
		Reader reader = null;
		Properties prop = new Properties(); 

		try
		{
		  reader = new FileReader(PROPERTIES_FILE_NAME);

		  prop.load( reader );
		}
		catch ( Exception e )
		{
		}
		finally
		{
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
		// Properties den Feldern zuweisen
		if (prop.containsKey(PROPERTY_NAME_LETZTES_VERZEICHNIS))
			this.letztesVerzeichnis = prop.getProperty(PROPERTY_NAME_LETZTES_VERZEICHNIS);
		
		if (prop.containsKey(PROPERTY_EMAIL_ADRESSEN))
		{
			String emailBase64 = prop.getProperty(PROPERTY_EMAIL_ADRESSEN);
			this.emailAdressen = 
					(ArrayList<String>) Utils.base64ToObject(emailBase64, ArrayList.class, null);
		}
		
		if (prop.containsKey(PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE))
			this.serverAdminCredentialFile = prop.getProperty(PROPERTY_SERVER_ADMIN_CREDENTIAL_FILE);
		
		if (prop.containsKey(PROPERTY_SERVER_USER_CREDENTIAL_FILE))
		{
			this.serverUserCredentialsFile = prop.getProperty(PROPERTY_SERVER_USER_CREDENTIAL_FILE);
			this.cuc = ServerUtils.readClientUserCredentials(this.serverUserCredentialsFile);
		}
		
		if (prop.containsKey(PROPERTY_SERVER_COMMUNICATION_ENABLED))
			this.serverCommunicationEnabled = Boolean.parseBoolean(prop.getProperty(PROPERTY_SERVER_COMMUNICATION_ENABLED));
		
		if (prop.containsKey(PROPERTY_NAME_SPRACHE))
		{
			String languageCode = prop.getProperty(PROPERTY_NAME_SPRACHE);
			if (languageCode != null)
				SternResources.setLocale(languageCode);
		}
		
		if (prop.containsKey(PROPERTY_LAST_UPDATE_FOUND))
			this.lastUpdateCheck = prop.getProperty(PROPERTY_LAST_UPDATE_FOUND);
		
		if (prop.containsKey(PROPERTY_EMAIL_SEPARATOR))
			this.emailSeparator = prop.getProperty(PROPERTY_EMAIL_SEPARATOR);
		
		return prop;
	}
	
	private static void writeProperties(Properties props)
	{
		Writer writer = null;

		try
		{
		  writer = new FileWriter(PROPERTIES_FILE_NAME);

		  props.store( writer, "Stern-Profil" );
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
	
	void setProperty(String propName, String propValue)
	{
		if (propValue == null)
			return;
		
		this.props.setProperty(propName, propValue);
		writeProperties(this.props);
	}
	
	private void createBackup (String filename) throws IOException
	{
		InputStream in = null;
		OutputStream out = null; 
		
		if (!new File(filename).exists())
			return;
		
		String filenameBackup = filename + FILE_SUFFIX_BACKUP;
		
		byte[] buffer = new byte[1000000];
		
		try {
			in = new FileInputStream(filename);
			out = new FileOutputStream(filenameBackup);
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
		
		// Ist der Client registriert?
		if (this.serverFunctions.isClientRegistered(clientId))
			this.keyPressed(new KeyEventExtended(event, clientId, languageCode));
	}

	@Override
	public String rmiClientConnectionRequest(String clientId, String release, String ip,
			String clientCode, String clientName) throws RemoteException
	{
		if (release.equals(ReleaseGetter.getRelease()))
			return this.serverFunctions.connectClient(clientId, ip, clientCode, clientName);
		else
			return SternResources.UnterschiedlicheBuilds(false);
	}

	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
		this.keyPressed(new KeyEventExtended(arg0, null, languageCode));
	}

	@Override
	public ScreenDisplayContentClient rmiGetCurrentScreenDisplayContent(String clientId)
			throws RemoteException
	{
		//System.out.println("rmiGetCurrentScreenDisplayContent: " + clientId);
		if (this.serverFunctions.isClientRegistered(clientId))
		{
			ScreenDisplayContentClient contentClient = new ScreenDisplayContentClient();
			contentClient.content = this.paintPanel.getScreenDisplayContent();
			contentClient.inputEnabled = this.inputEnabled;
			
			return contentClient; 
		}
		else
			return null;
	}
	
	@Override
	public void rmiClientLogoff(String clientId) throws RemoteException
	{
		//System.out.println("rmiClientLogoff: " + clientId);
		this.serverFunctions.disconnectClient(clientId);
	}

	@Override
	public boolean rmiClientCheckRegistration(String clientId)
	{
		//System.out.println("rmiClientCheckRegistration: " + clientId);
		return this.serverFunctions.isClientRegistered(clientId);
	}

	@Override
	public void addToHighscore(Archiv spielstand, Spieler[] spieler)
	{
		for (int sp = 0; sp < spieler.length; sp++)
		{
//			int punkte = Spiel.getPunkteFromPlaneten(
//					spielstand.getAnzPl()[sp], spieler.length);
			int punkte = spielstand.getPunkte()[sp]; 
			
			if (punkte <= 0)
				continue;
			
			int insertIndex = -1;
			boolean dontAdd = false;
			
			for (int entryIndex = 0; entryIndex < this.highscoreEntries.list.size(); entryIndex++)
			{
				HighscoreEntry entry = this.highscoreEntries.list.get(entryIndex);
				
				if (punkte >= entry.punkte)
				{
					insertIndex = entryIndex;
					
					for (int entryIndex2 = entryIndex; entryIndex2 < this.highscoreEntries.list.size(); entryIndex2++)
					{
						HighscoreEntry entry2 = this.highscoreEntries.list.get(entryIndex2);
						
						if (entry2.punkte != punkte)
							break;
						
						// Gibt es schon einen Eintrag mit derselben Punktzahl fuer denselben Spieler?
						if (spieler[sp].getName().toUpperCase().equals(entry2.name.toUpperCase()))
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
			newEntry.name = spieler[sp].getName();
			newEntry.punkte = punkte;
			
			if (insertIndex >= 0)
				this.highscoreEntries.list.add(insertIndex, newEntry);
			else
				this.highscoreEntries.list.add(newEntry);
			
			while (this.highscoreEntries.list.size() > Stern.HIGHSCORE_NUM_ENTRIES)
				this.highscoreEntries.list.remove(Stern.HIGHSCORE_NUM_ENTRIES);
		}
		
		// Highscoreliste abspeichern
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
	
	private void getHighscores()
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
		String filename = (this.t != null && this.t.getSpiel() != null && this.t.getSpiel().getName() != null) ?
							" <" + this.t.getSpiel().getName() + ">" :
							"";
		
		if (this.serverFunctions != null && this.serverFunctions.isServerEnabled())
			this.setTitle(
					SternResources.SternTerminalServer(false)+
					filename+
					" (IP:"+this.serverFunctions.getMeineIp()+", "+SternResources.ThinClientCode(false)+" " + this.serverFunctions.getClientCode()+")");
		else
			this.setTitle(SternResources.SternTitel(false) + filename);
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
	
	private void setEmailAdressenProperty()
	{
		String base64 = Utils.objectToBase64(this.emailAdressen, null);
		this.setProperty(PROPERTY_EMAIL_ADRESSEN, base64);
	}

	@Override
	public boolean launchEmail(String recipient, String subject, String bodyText, EmailTransportBase obj)
	{
		return EmailToolkit.launchEmailClient(recipient, subject, bodyText, null, obj);
	}

	@Override
	public SpielzuegeEmailTransport importSpielzuegeAusEmail()
	{
		this.inputEnabled = false;
		this.redrawScreen();

		// ---------
		ClipboardImportJDialog<SpielzuegeEmailTransport> dlg = 
				new ClipboardImportJDialog<SpielzuegeEmailTransport>(
						this, SpielzuegeEmailTransport.class, false);
		
		dlg.setVisible(true);
		// ---------
		
		SpielzuegeEmailTransport set = (SpielzuegeEmailTransport)dlg.obj;
		
		if (set != null)
		{
			if (!MinBuildChecker.doCheck(this, set.getMinBuild()))
				set = null;
		}
		
		this.inputEnabled = true;
		this.redrawScreen();
		this.updateTitle();

		return set;
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
			respMsg.payloadSerialized = PostMovesResult.FEHLER.toString();
		}
		
		if (respMsg.error)
			JOptionPane.showMessageDialog(this,
				    respMsg.errorMsg,
				    SternResources.Verbindungsfehler(false),
				    JOptionPane.ERROR_MESSAGE);
		
		return respMsg;

	}

	@Override
	public PostMovesResult postMovesToServer(String gameId, String spielerName, SpielzuegeEmailTransport set)
	{
		// Ist der Spieler ueberhaupt als solcher angemeldet?
		if (this.cuc.userId.equals(spielerName))
		{		
			RequestMessage msg = new RequestMessage(RequestMessageType.POST_MOVES);
			
			RequestMessagePostMoves msgPayload = new RequestMessagePostMoves();
			
			msgPayload.gameId = gameId;
			msgPayload.zuege = set;
			
			msg.payloadSerialized = Utils.objectToBase64(msgPayload, null);
			
			ResponseMessage respMessage = this.sendAndReceive(this.cuc, msg);
			
			PostMovesResult result = PostMovesResult.valueOf(respMessage.payloadSerialized);
			
			return result;
		}
		else
			return PostMovesResult.BENUTZER_NICHT_ANGEMELDET;
	}
	
	private void getGameInfoByTimer()
	{
		if (!this.serverCommunicationEnabled)
			return;
		
		boolean connected = false;
		int count = 0;
		
		if (this.cuc != null)
		{
			RequestMessage msg = new RequestMessage(RequestMessageType.GET_GAMES_ZUGEINGABE);
			
			ResponseMessage respMsg = ClientSocketManager.sendAndReceive(this.cuc, msg);
			if (!respMsg.error)
			{
				connected = true;
				count = Integer.parseInt(respMsg.payloadSerialized);
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
			
			this.labRefresh.setVisible(count > 0);
			this.labRefresh.setToolTipText(SternResources.MitspielerWarten(false));
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
					
			this.labRefresh.setVisible(false);
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
		if (e.getSource() == this.labRefresh)
			this.openServerGamesDialog();
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
				this.serverUserCredentialsFile);
		dlg.setVisible(true);
		
		if (dlg.ok)
		{
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
			JOptionPane.showMessageDialog(this,
				    SternResources.ServerZugangsdatenNichtHinterlegt(false),
				    SternResources.Fehler(false),
				    JOptionPane.ERROR_MESSAGE);
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
				ServerGamesJDialog dlg = new ServerGamesJDialog(
						this, 
						SternResources.ServerbasierteSpiele(false, this.cuc.userId),
						this.currentGameId,
						this.cuc,
						ResponseMessageGamesAndUsers.fromJson(respMsg.payloadSerialized));
				dlg.setVisible(true);
				
				if (dlg.spielGeladen != null)
				{
					if (MinBuildChecker.doCheck(this, dlg.spielGeladen.getMinBuild()))
					{
						this.setNeuesSpiel(dlg.spielGeladen, true);
					}
				}
				this.getGameInfoByTimer();
			}
		}
		
		this.inputEnabled = true;
		this.redrawScreen();

	}

	@Override
	public void triggerGameInfoUpdate()
	{
		this.getGameInfoByTimer();
	}

	@Override
	public Hashtable<Integer, Archiv> getEvaluations(String gameId, int vonJahr, int bisJahr) 
	{
		RequestMessage msg = new RequestMessage(RequestMessageType.GET_EVALUATIONS);
		
		RequestMessageGetEvaluations msgPayload = new RequestMessageGetEvaluations();
		
		msgPayload.gameId = gameId;
		msgPayload.vonJahr = vonJahr;
		msgPayload.bisJahr = bisJahr;
				
		msg.payloadSerialized = Utils.objectToBase64(msgPayload, null);
		
		ResponseMessage respMessage = this.sendAndReceive(this.cuc, msg);
		
		if (respMessage.error == false && respMessage.payloadSerialized != null)
			return ResponseMessageGetEvaluations.fromJson(respMessage.payloadSerialized).archiv;
		else
			return null;
	}

	@Override
	public void lastUpdateFound(String updateBuild) 
	{
		this.setProperty(PROPERTY_LAST_UPDATE_FOUND, updateBuild);
	}
}
