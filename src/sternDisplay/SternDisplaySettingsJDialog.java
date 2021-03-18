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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.Constants;
import common.ReleaseGetter;
import common.SternResources;
import common.Utils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.IServerMethods;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.PasswordFieldDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") 
class SternDisplaySettingsJDialog extends JDialog implements ChangeListener, ActionListener
{
	private ButtonDark butConnect;
	private ButtonDark butClose;
	private ButtonDark butGetMyIpAddress;
	private TextFieldDark tfServerIpAddress;
	private TextFieldDark tfMyIpAddress;
	private PasswordFieldDark tfClientCode;
	private TextFieldDark tfMyName;
	private LabelDark labStatus;

	private SternDisplaySettings settings;
	
	private SternDisplay parent;
	private static Font font;
	
	SternDisplaySettingsJDialog(
			SternDisplay parent,
			String title,
			boolean modal,
			SternDisplaySettings settings)
	{
		super (parent, title, modal);
		
		font = DialogFontHelper.getFont();
		
		this.settings = settings;
		this.parent = parent;
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark();
		panMain.setLayout(new BorderLayout(20,10));
		
		// ---------------
		PanelDark panServer = this.getGroupBox(SternResources.SternScreenSharingServer(false));
		panServer.setLayout(new SpringLayout());
		
		panServer.add(new LabelDark(SternResources.ClientSettingsJDialogMeineIp(false), font));

		PanelDark panIpAddresses = new PanelDark(new GridLayout(1,2, 10, 0));
		
		this.tfMyIpAddress = new TextFieldDark(font, 18);
		this.tfMyIpAddress.setText(this.settings.myIpAddress);
		panIpAddresses.add(this.tfMyIpAddress);
		
		this.butGetMyIpAddress = new ButtonDark(this, SternResources.ClientSettingsJDialogIpErmitteln(false) , font);
		panIpAddresses.add(this.butGetMyIpAddress);
		
		panServer.add(panIpAddresses);
		
		panServer.add(new LabelDark(SternResources.ServerSettingsJDialogIpServer(false), font));
		
		this.tfServerIpAddress = new TextFieldDark(this.settings.serverIpAddress, font);
		panServer.add(this.tfServerIpAddress);
		
		panServer.add(new LabelDark(SternResources.ThinClientCode(false), font));
		
		this.tfClientCode = new PasswordFieldDark(this.settings.clientCode, font);		
		panServer.add(this.tfClientCode);
		
		panServer.add(new LabelDark(SternResources.ClientSettingsJDialogMeinName(false), font));
		
		this.tfMyName = new TextFieldDark(this.settings.myName, font);
		panServer.add(this.tfMyName);
		
		SpringUtilities.makeCompactGrid(panServer,
                4, 2, //rows, cols
                5, 5, //initialX, initialY
                20, 5);//xPad, yPad
		
		
		panMain.add(panServer, BorderLayout.CENTER);
		
		// ----
		PanelDark panStatus = this.getGroupBox(SternResources.ClientSettingsJDialogVerbindungsstatus(false));
		panStatus.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.labStatus = new LabelDark(font);
		panStatus.add(this.labStatus);
		this.updateConnectionStatus();
		
		panMain.add(panStatus, BorderLayout.SOUTH);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butConnect = new ButtonDark(this, SternResources.ClientSettingsJDialogVerbinden(false), font);
		panButtons.add(this.butConnect);
		
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
		if (event.getSource() == this.getRootPane())
		{
			this.close();
			return;
		}
		
		Object source = event.getSource();
		
		if (source == this.butClose)
		{
			this.updateSettings();
			
			this.close();
		}
		else if (source == this.butGetMyIpAddress)
		{
			this.tfMyIpAddress.setText(Utils.getMyIPAddress());
		}
		else if (source == this.butConnect)
		{
			this.updateSettings();
			System.setProperty("java.rmi.server.hostname",this.settings.myIpAddress);
			
			String errorMsg = null;
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			try {
				if (!InetAddress.getByName( this.settings.serverIpAddress ).isReachable( 2000 ))
					throw new Exception(
							SternResources.ClientSettingsJDialogServerNichtErreichbar(false,
									this.settings.serverIpAddress));
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIpAddress);
				rmiServer = (IServerMethods) registry.lookup( Constants.RMI_REGISTRATION_NAME_SERVER );
				
				errorMsg = rmiServer.rmiClientConnectionRequest(
						this.settings.clientId,
						ReleaseGetter.getRelease(),
						this.settings.myIpAddress,
						this.settings.clientCode,
						this.settings.myName);
				
				if (errorMsg.length() > 0)
					DialogWindow.showError(
							this,
							errorMsg,
							SternResources.Fehler(false));
				else
					this.parent.connected = true;
			}
			catch (Exception e) {
				this.setCursor(Cursor.getDefaultCursor());
				
				DialogWindow.showError(
						this,
						SternResources.ClientSettingsJDialogKeineVerbindung(false, e.getMessage()),
						SternResources.Fehler(false));
				
				this.parent.connected = false;
			}
			
			this.setCursor(Cursor.getDefaultCursor());
			
			this.updateConnectionStatus();
		}
	}
	
	private void updateConnectionStatus()
	{
		boolean authorized = false;
		
		String text = "";
		
		if (this.parent.connected)
		{

			try 
			{
				IServerMethods rmiServer;
				Registry registry = LocateRegistry.getRegistry(this.settings.serverIpAddress);
				rmiServer = (IServerMethods) registry.lookup( Constants.RMI_REGISTRATION_NAME_SERVER );

				authorized = rmiServer.rmiClientCheckRegistration(this.settings.clientId);
			}
			catch (Exception e)
			{
				text = SternResources.ClientSettingsJDialogKeineVerbindung2(false, this.settings.serverIpAddress);
			}

			if (text.length() == 0)
			{
				if (authorized)
					text = SternResources.ClientSettingsJDialogVerbunden(false, this.settings.serverIpAddress); 
				else
					text = SternResources.ClientSettingsJDialogClientNichtRegistriert(false, this.settings.serverIpAddress); 
			}
		}
		else
			text = SternResources.ClientSettingsJDialogNichtVerbunden(false);
		
		this.labStatus.setText(text);
	}
	
	@SuppressWarnings("deprecation")
	private void updateSettings()
	{
		this.settings.clientCode = this.tfClientCode.getText();
		this.settings.myName = this.tfMyName.getText();
		this.settings.myIpAddress = this.tfMyIpAddress.getText();
		this.settings.serverIpAddress = this.tfServerIpAddress.getText();
	}

	@Override
	public void stateChanged(ChangeEvent e){
		
	}

	private PanelDark getGroupBox(String title)
	{
		PanelDark panel = new PanelDark();
		
		TitledBorder titledBorder = new TitledBorder(title);
		titledBorder.setTitleFont(font);
		titledBorder.setTitleColor(Color.white);
		panel.setBorder(titledBorder);
		
		return panel;
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
}
