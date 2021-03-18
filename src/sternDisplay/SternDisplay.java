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

package sternDisplay;

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

import javax.swing.UIManager;

import common.Constants;
import common.PdfLauncher;
import common.ScreenContent;
import common.ScreenContentClient;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.ISternDisplayMethods;
import commonUi.IHostComponentMethods;
import commonUi.IServerMethods;
import commonUi.PanelScreenContent;
import commonUi.LanguageSelectionJDialog;
import commonUi.SternAbout;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial") 
public class SternDisplay extends Frame // NO_UCD (use default)
	implements 
		WindowListener, 
		ActionListener,
		ISternDisplayMethods,
		IHostComponentMethods
{
	boolean connected = false;
	
	private PanelScreenContent paintPanel;
	
	private SternDisplaySettings settings;
	
	private Properties properties;
	
    private MenuItem menuConnectionSettings;  
    private MenuItem menuQuit;
    
    private MenuItem menuHelp;
    private MenuItem menuAbout;
    private MenuItem menuLanguage;
    
    transient private static final String PROPERTIES_FILE_NAME = "SternDisplayProperties";
	transient private static final String PROPERTY_NAME_SERVER_IP_ADDRESS = "serverIpAddress";
	transient private static final String PROPERTY_NAME_MY_IP_ADDRESS = "myIpAddress";
	transient private static final String PROPERTY_NAME_MY_NAME = "myName";
	transient private static final String PROPERTY_NAME_LANGUAGE = "language";
	
	public static void main(String[] args)
	{
		new SternDisplay();
	}
	
	private SternDisplay()
	{
		super();
		
		this.settings = new SternDisplaySettings();
		
        settings.clientId = UUID.randomUUID().toString();
        settings.clientCode = "";
        settings.myName = "";
        
		this.properties = this.getProperties();
		
		if (settings.myIpAddress == null || settings.myIpAddress.equals(""))
			settings.myIpAddress = Utils.getMyIPAddress();
		
		if (settings.serverIpAddress == null || settings.serverIpAddress.equals(""))
			settings.serverIpAddress = settings.myIpAddress;
		
		this.setTitle(SternResources.SternClientTitel(false));
        
		try {
		    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		    UIManager.put("ComboBox.disabledBackground", Color.black);
		 } catch (Exception e) {
		            e.printStackTrace();
		 }

		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width, dim.height);

		this.setMenuBar(this.defineMenubar());
		
		this.addWindowListener(this);
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
		
		this.paintPanel = new PanelScreenContent(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		try {
			LocateRegistry.createRegistry( Registry.REGISTRY_PORT    );
		}
		catch ( RemoteException e ) 
		{}

		ISternDisplayMethods stub;
		try {
			stub = (ISternDisplayMethods) UnicastRemoteObject.exportObject( this, 0 );
			Registry registry;
			registry = LocateRegistry.getRegistry();
			registry.rebind( this.settings.clientId, stub );			
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
				
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setVisible(true);
		this.paintPanel.requestFocusInWindow();
		
		ActionEvent e = new ActionEvent(
				this.menuConnectionSettings, 
				ActionEvent.ACTION_PERFORMED,
				"Connection settings", 
				System.currentTimeMillis(), 
				0);
		
		this.actionPerformed(e);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MenuItem menuItem = (MenuItem)e.getSource();
		
		if (menuItem == this.menuQuit)
		{
			this.closeSternClient();
		}
		else if (menuItem == this.menuConnectionSettings)
		{
			SternDisplaySettingsJDialog dlg = new SternDisplaySettingsJDialog(
												this, 
												SternResources.ClientSettingsJDialogTitel(false),
												true,
												settings);
			dlg.setVisible(true);
			this.setProperties();
			
			this.updateScreenDisplayContent();
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
			LanguageSelectionJDialog dlg = new LanguageSelectionJDialog(
					this, 
					SternResources.getLocale());
			dlg.setVisible(true);
			
			if (dlg.ok)
			{
				SternResources.setLocale(dlg.languageCode);
				
				this.properties.setProperty(PROPERTY_NAME_LANGUAGE, dlg.languageCode);
				writeProperties(this.properties);
				
				this.logoff();
				
				System.exit(0);
			}
		}
		else if (menuItem == this.menuAbout)
		{
			SternAbout.show(this);
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
		ScreenContentClient screenContentClient = null;
		
		if (this.connected)
		{		
			try {
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIpAddress);
				rmiServer = (IServerMethods) registry.lookup( Constants.RMI_REGISTRATION_NAME_SERVER );
				screenContentClient = rmiServer.rmiGetCurrentScreenDisplayContent(this.settings.clientId);
			}
			catch (Exception e) {
			}
		}
	
		if (screenContentClient != null)
			this.paintPanel.redraw(
					screenContentClient.screenContent, 
					screenContentClient.inputEnabled,
					screenContentClient.showInputDisabled);
		else
			this.paintPanel.redraw(null, false, true);
	}
	
	private void logoff()
	{
		if (!this.connected)
			return;
		
		try {
			IServerMethods rmiServer;
			Registry registry = LocateRegistry.getRegistry(this.settings.serverIpAddress);
			rmiServer = (IServerMethods) registry.lookup( Constants.RMI_REGISTRATION_NAME_SERVER );
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
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIpAddress);
				rmiServer = (IServerMethods) registry.lookup( Constants.RMI_REGISTRATION_NAME_SERVER );
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
	public void updateScreen(
			ScreenContent screenContent, 
			boolean inputEnabled,
			boolean showInputDisabled)
			throws RemoteException
	{
		this.paintPanel.redraw(screenContent, inputEnabled, showInputDisabled);
	}
	
	private void closeSternClient()
	{
		DialogWindowResult result = DialogWindow.showYesNo(
				this,
				SternResources.SternClientVerlassenFrage(false),
				SternResources.SternThinClientVerlassen(false));
		
		if (result == DialogWindowResult.YES)
		{
			this.logoff();
			System.exit(0);
		}
	}

	private MenuBar defineMenubar()
	{
	    MenuBar menuBar = new MenuBar ();
	    
	    Menu menuStern = new Menu (SternResources.SternTitel(false));
	    
	    if (Desktop.isDesktopSupported())
	    {
		    this.menuHelp = new MenuItem (SternResources.MenuSpielanleitung(false));
		    this.menuHelp.addActionListener(this);
		    menuStern.add (this.menuHelp);
	    }
	    
	    this.menuAbout = new MenuItem (SternResources.MenuUeberStern(false));
	    this.menuAbout.addActionListener(this);
	    menuStern.add (this.menuAbout);
	    
	    menuStern.addSeparator();
	    
	    this.menuConnectionSettings = new MenuItem(SternResources.MenuVerbindungseinstellungen(false));
	    this.menuConnectionSettings.addActionListener(this);
	    menuStern.add(this.menuConnectionSettings);
	    
	    this.menuLanguage = new MenuItem(SternResources.MenuSpracheinstellungen(false));
	    this.menuLanguage.addActionListener(this);
	    menuStern.add(this.menuLanguage);
	    
	    menuStern.addSeparator();
	    
	    this.menuQuit = new MenuItem (SternResources.MenuSternClientVerlassen(false));
	    this.menuQuit.addActionListener(this);
	    menuStern.add (this.menuQuit);
	    
	    menuBar.add(menuStern);

	    return menuBar;
	}
	
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
		
		if (properties.containsKey(PROPERTY_NAME_SERVER_IP_ADDRESS))
			this.settings.serverIpAddress = properties.getProperty(PROPERTY_NAME_SERVER_IP_ADDRESS);
		if (properties.containsKey(PROPERTY_NAME_SERVER_IP_ADDRESS))
			this.settings.myIpAddress = properties.getProperty(PROPERTY_NAME_MY_IP_ADDRESS);
		if (properties.containsKey(PROPERTY_NAME_MY_NAME))
			this.settings.myName = properties.getProperty(PROPERTY_NAME_MY_NAME);
		if (properties.containsKey(PROPERTY_NAME_LANGUAGE))
		{
			String languageCode = properties.getProperty(PROPERTY_NAME_LANGUAGE);
			if (languageCode != null)
				SternResources.setLocale(languageCode);
		}

		return properties;
	}
	
	private void setProperties()
	{
		this.properties.setProperty(PROPERTY_NAME_SERVER_IP_ADDRESS, this.settings.serverIpAddress);
		this.properties.setProperty(PROPERTY_NAME_MY_IP_ADDRESS, this.settings.myIpAddress);
		this.properties.setProperty(PROPERTY_NAME_MY_NAME, this.settings.myName);
		
		writeProperties(this.properties);
	}
	
	private static void writeProperties(Properties properties)
	{
		Writer writer = null;

		try
		{
		  writer = new FileWriter(PROPERTIES_FILE_NAME);

		  properties.store( writer, "STERN Display properties" );
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
}
