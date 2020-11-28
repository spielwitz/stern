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

package sternClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import common.Constants;
import common.PdfLauncher;
import common.ScreenDisplayContent;
import common.ScreenDisplayContentClient;
import commonUi.IClientMethods;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
import commonUi.IUpdateCheckerCallback;
import commonUi.PaintPanel;
import commonUi.SpracheJDialog;
import commonUi.SternAbout;
import commonUi.UpdateChecker;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial") 
public class SternClient extends Frame // NO_UCD (use default)
	implements 
		WindowListener, 
		ActionListener,
		IClientMethods,
		IHostComponentMethods,
		IUpdateCheckerCallback
{
	boolean connected = false;
	
	// UI components
	private PaintPanel paintPanel;
	
	private ClientSettings settings;
	
	private Properties props;
	
	// Menues
    private MenuItem menuVerbindungseinstellungen;  
    private MenuItem menuQuit;
    
    private MenuItem menuHilfe;
    private MenuItem menuAbout;
    private MenuItem menuSprache;
    private MenuItem menuSearchForUpdates;
    
    private String lastUpdateCheck;
    
    transient private static final String PROPERTIES_FILE_NAME = "SternClientProperties";
	transient private static final String PROPERTY_NAME_SERVER_IP = "serverIp";
	transient private static final String PROPERTY_NAME_MEINE_IP = "ip";
	transient private static final String PROPERTY_NAME_MEIN_NAME = "meinName";
	transient private static final String PROPERTY_NAME_SPRACHE = "sprache";
	transient private static final String PROPERTY_LAST_UPDATE_FOUND = "lastUpdateFound";
	
	public static void main(String[] args)
	{
		new SternClient();
	}
	
	private SternClient()
	{
		super();
		
		this.settings = new ClientSettings();
		
		UUID uuid = UUID.randomUUID();
        settings.clientId = uuid.toString();
        settings.clientCode = "";
        settings.meinName = "";
        
		// Properties lesen
		this.props = this.getProperties();
		
		if (settings.meineIp == null || settings.meineIp.equals(""))
			settings.meineIp = Utils.getMyIPAddress();
		
		if (settings.serverIp == null || settings.serverIp.equals(""))
			settings.serverIp = settings.meineIp;
		
		this.setTitle(SternResources.SternClientTitel(false));
        
		try {
		    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		    UIManager.put("ComboBox.disabledBackground", Color.black);
		 } catch (Exception e) {
		            e.printStackTrace();
		 }

		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.setMenuBar(this.getMenubar());
		
		this.addWindowListener(this);
		this.setFocusable(true); // Achtung, das Panel hat den KeyListener!
		this.setLayout(new BorderLayout());
		
		this.paintPanel = new PaintPanel(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		// RMI-Server aktivieren
		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT    );
		}
		catch ( RemoteException e ) 
		{}

		IClientMethods stub;
		try {
			stub = (IClientMethods) UnicastRemoteObject.exportObject( this, 0 );
			Registry registry;
			registry = LocateRegistry.getRegistry();
			registry.rebind( this.settings.clientId, stub );			
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
				
		// Muss ganz zum Schluss kommen, damit der Tastaturfokus zieht
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.paintPanel.requestFocusInWindow(); // Muss nach SetVisible kommen!
		
		// Auf Updates checken
		Thread tUpdateChecker = new Thread( new UpdateChecker(this, this.lastUpdateCheck, true) );
		tUpdateChecker.start();

		// Gleich nach dem Programmstart die Verbindungseinstellungen aufmachen.
		// Sonst ist der Client ja sinnlos.
		ActionEvent e = new ActionEvent(
				this.menuVerbindungseinstellungen, 
				ActionEvent.ACTION_PERFORMED,
				"Verbindungseinstellungen", System.currentTimeMillis(), 0);
		
		this.actionPerformed(e);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MenuItem item = (MenuItem)e.getSource();
		
		if (item == this.menuQuit)
		{
			this.closeSternClient();
		}
		else if (item == this.menuVerbindungseinstellungen)
		{
			ClientSettingsJDialog dlg = new ClientSettingsJDialog(
												this, 
												SternResources.ClientSettingsJDialogTitel(false),
												true,
												settings);
			dlg.setVisible(true);
			this.setProperties();
			
			this.updateScreenDisplayContent();
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
				
				// Abmelden vom Server
				this.logoff();
				
				// Anwendung schließen
				System.exit(0);
			}
		}
		else if (item == this.menuAbout)
		{
			SternAbout.show(this);
		}
		else if (item == this.menuSearchForUpdates)
		{
			Thread tUpdateChecker = new Thread( new UpdateChecker(this, null, false) );
			tUpdateChecker.start();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		this.closeSternClient();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	
	private void updateScreenDisplayContent()
	{
		ScreenDisplayContentClient contentClient = null;
		
		if (this.connected)
		{		
			try {
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIp);
				rmiServer = (IServerMethods) registry.lookup( Constants.REG_NAME_SERVER );
				contentClient = rmiServer.rmiGetCurrentScreenDisplayContent(this.settings.clientId);
			}
			catch (Exception e) {
			}
		}
	
		if (contentClient != null)
			this.paintPanel.redraw(
					contentClient.content, 
					contentClient.inputEnabled,
					contentClient.showInputDisabled);
		else
			this.paintPanel.redraw(null, false, true);
	}
	
	private void logoff()
	{
		if (!this.connected)
			return;
		
		try {
			IServerMethods rmiServer;
			Registry registry = LocateRegistry.getRegistry(this.settings.serverIp);
			rmiServer = (IServerMethods) registry.lookup( Constants.REG_NAME_SERVER );
			rmiServer.rmiClientLogoff(this.settings.clientId);
		}
		catch (Exception e) {}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
		if (this.connected)
		{
			try {
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIp);
				rmiServer = (IServerMethods) registry.lookup( Constants.REG_NAME_SERVER );
				rmiServer.rmiKeyPressed(
						this.settings.clientId, 
						languageCode,
						arg0.getID(), 
						arg0.getWhen(), 
						arg0.getModifiers(), 
						arg0.getKeyCode(), 
						arg0.getKeyChar());
			}
			catch (Exception e) {
			}
		}
	}

	@Override
	public void updateScreenDisplayContent(
			ScreenDisplayContent content, 
			boolean inputEnabled,
			boolean showInputDisabled)
			throws RemoteException
	{
		this.paintPanel.redraw(content, inputEnabled, showInputDisabled);
	}
	
	private void closeSternClient()
	{
		int result = JOptionPane.showConfirmDialog(
				this,
				SternResources.SternClientVerlassenFrage(false),
				SternResources.SternThinClientVerlassen(false),
				JOptionPane.YES_NO_OPTION);
		
		if (result == JOptionPane.YES_OPTION)
		{
			// Abmelden vom Server
			this.logoff();
			
			System.exit(0);
		}
	}

	private MenuBar getMenubar()
	{
	    MenuBar menueLeiste = new MenuBar ();
	    
	    Menu hilfe = new Menu (SternResources.MenuHilfe(false));
	    
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHilfe = new MenuItem (SternResources.MenuSpielanleitung(false));
		    this.menuHilfe.addActionListener(this);
		    hilfe.add (this.menuHilfe);
	    }
	    
	    this.menuAbout = new MenuItem (SternResources.MenuUeberStern(false));
	    this.menuAbout.addActionListener(this);
	    hilfe.add (this.menuAbout);
	    
	    this.menuSearchForUpdates = new MenuItem (SternResources.MenuSearchForUpdates(false));
	    this.menuSearchForUpdates.addActionListener(this);
	    hilfe.add (this.menuSearchForUpdates);
	    
	    hilfe.addSeparator();
	    
	    this.menuVerbindungseinstellungen = new MenuItem(SternResources.MenuVerbindungseinstellungen(false));
	    this.menuVerbindungseinstellungen.addActionListener(this);
	    hilfe.add(this.menuVerbindungseinstellungen);
	    
	    this.menuSprache = new MenuItem(SternResources.MenuSpracheinstellungen(false));
	    this.menuSprache.addActionListener(this);
	    hilfe.add(this.menuSprache);
	    
	    hilfe.addSeparator();
	    
	    this.menuQuit = new MenuItem (SternResources.MenuSternClientVerlassen(false));
	    this.menuQuit.addActionListener(this);
	    hilfe.add (this.menuQuit);
	    
	    menueLeiste.add(hilfe);

	    return menueLeiste;
	}
	
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
		if (prop.containsKey(PROPERTY_NAME_SERVER_IP))
			this.settings.serverIp = prop.getProperty(PROPERTY_NAME_SERVER_IP);
		if (prop.containsKey(PROPERTY_NAME_SERVER_IP))
			this.settings.meineIp = prop.getProperty(PROPERTY_NAME_MEINE_IP);
		if (prop.containsKey(PROPERTY_NAME_MEIN_NAME))
			this.settings.meinName = prop.getProperty(PROPERTY_NAME_MEIN_NAME);
		if (prop.containsKey(PROPERTY_NAME_SPRACHE))
		{
			String languageCode = prop.getProperty(PROPERTY_NAME_SPRACHE);
			if (languageCode != null)
				SternResources.setLocale(languageCode);
		}
		if (prop.containsKey(PROPERTY_LAST_UPDATE_FOUND))
			this.lastUpdateCheck = prop.getProperty(PROPERTY_LAST_UPDATE_FOUND);

		return prop;
	}
	
	private void setProperties()
	{
		this.props.setProperty(PROPERTY_NAME_SERVER_IP, this.settings.serverIp);
		this.props.setProperty(PROPERTY_NAME_MEINE_IP, this.settings.meineIp);
		this.props.setProperty(PROPERTY_NAME_MEIN_NAME, this.settings.meinName);
		
		writeProperties(this.props);
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

	@Override
	public boolean openPdf(byte[] pdfBytes) throws RemoteException
	{
		return PdfLauncher.showPdf(pdfBytes);
	}

	@Override
	public void lastUpdateFound(String updateBuild)
	{
		this.props.setProperty(PROPERTY_LAST_UPDATE_FOUND, updateBuild);
		writeProperties(this.props);
		
	}
}
