/**	STERN, das Strategiespiel.
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.SternResources;
import common.Utils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") class ServerSettingsJDialog extends JDialog implements ChangeListener, ActionListener
{
	private ButtonDark butClose;
	private ButtonDark butRefreshClients;
	private ButtonDark butGetIp;
	
	private TextFieldDark tfIp;
	
	private CheckBoxDark cbServerEnabled;
    private CheckBoxDark cbInaktivBeiZugeingabe;
	private JList<String> listClients;
	
	private DefaultListModel<String> listClientsModel;
	
	private ServerFunctions serverFunctions;
	
	private Stern parent;
	
	private static Font font;
	
	public String meineIp;
	private boolean inaktivBeiZugeingabe;
	
	ServerSettingsJDialog(
			Stern parent,
			String title,
			String meineIp,
			boolean modal,
			ServerFunctions serverFunctions)
	{
		super (parent, title, modal);
		
		this.meineIp = meineIp == null || meineIp.equals("") ?
				serverFunctions.getMeineIp() : meineIp;
		
		// Font laden
		font = DialogFontHelper.getFont();
		
		this.serverFunctions = serverFunctions;
		this.parent = parent;
		this.inaktivBeiZugeingabe = parent.getClientsInaktivBeiZugeingabe();
		
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
		PanelDark panServer = new GroupBoxDark(SternResources.Terminalserver(false), font);
		panServer.setLayout(new GridLayout(2,1));
		
		PanelDark panServerSub = new PanelDark(new GridLayout(2,1));
		
		this.cbInaktivBeiZugeingabe = new CheckBoxDark(
				SternResources.ServerSettingsJDialogInaktiv(false), 
				this.inaktivBeiZugeingabe, 
				font);
		this.cbInaktivBeiZugeingabe.addActionListener(this);
		panServerSub.add(this.cbInaktivBeiZugeingabe);
		
		this.cbServerEnabled = new CheckBoxDark(SternResources.ServerSettingsJDialogTerminalServerAktiv(false), serverFunctions.isServerEnabled(), font);
		this.cbServerEnabled.addActionListener(this);
		panServerSub.add(this.cbServerEnabled);
		
		panServer.add(panServerSub);
		
		PanelDark panServerCodes = new PanelDark();
		panServerCodes.setLayout(new SpringLayout());
		
		panServerCodes.add(new LabelDark(SternResources.ServerSettingsJDialogIpServer(false), font));
		
		PanelDark panIp = new PanelDark(new GridLayout(1,2, 10, 0));
		
		this.tfIp = new TextFieldDark(font, 18);
		this.tfIp.setText(this.meineIp);
		panIp.add(this.tfIp);
		
		this.butGetIp = new ButtonDark(this, SternResources.ClientSettingsJDialogIpErmitteln(false) , font);
		panIp.add(this.butGetIp);
		
		panServerCodes.add(panIp);
		
		panServerCodes.add(new LabelDark(SternResources.ThinClientCode(false), font));
		panServerCodes.add(new TextFieldDark(serverFunctions.getClientCode(), font, false));
		
		SpringUtilities.makeCompactGrid(panServerCodes,
                2, 2, //rows, cols
                5, 5, //initialX, initialY
                20, 5);//xPad, yPad
		
		panServer.add(panServerCodes);
		
		panMain.add(panServer, BorderLayout.NORTH);
		
		// ---------------
		PanelDark panClients = new GroupBoxDark(SternResources.ServerSettingsJDialogVerbundeneClients(false), font);
		panClients.setLayout(new BorderLayout(10,10));
		
		this.listClientsModel = new DefaultListModel<String>();
		this.updateClientList();
		
		this.listClients = new JList<String>(this.listClientsModel);
		this.listClients.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.listClients.setLayoutOrientation(JList.VERTICAL);
		this.listClients.setBackground(new Color(30, 30, 30));
		this.listClients.setForeground(Color.white);
		this.listClients.setFont(font);
		
		JScrollPane listClientsScroller = new JScrollPane();
		listClientsScroller.setViewportView(this.listClients);
		listClientsScroller.setPreferredSize(new Dimension(300, 150));
				
		panClients.add(listClientsScroller, BorderLayout.CENTER);
		
		PanelDark panClientsButtonWrap = new PanelDark();
		panClientsButtonWrap.setLayout(new BorderLayout());
		
		PanelDark panClientsButton = new PanelDark();
		panClientsButton.setLayout(new GridLayout(2,1));
		
		this.butRefreshClients = new ButtonDark(this, SternResources.Aktualisieren(false), font);
		panClientsButton.add(this.butRefreshClients);
		
		panClientsButtonWrap.add(panClientsButton, BorderLayout.NORTH);
		
		panClients.add(panClientsButtonWrap, BorderLayout.EAST);
		
		panMain.add(panClients, BorderLayout.CENTER);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
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
		this.setControlsEnabled();
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (source == this.butClose || source == this.getRootPane())
			this.close();
		else if (source == this.cbServerEnabled)
		{
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			if (this.cbServerEnabled.isSelected())
			{
				this.serverFunctions.setIp(this.tfIp.getText());
				
				// Start RMI
				this.serverFunctions.startServer(this.parent);
			}
			else
			{
				// Stop RMI
				this.serverFunctions.stopServer(this.parent);
			}
			
			this.setCursor(Cursor.getDefaultCursor());
			this.setControlsEnabled();
		}
		else if (source == this.cbInaktivBeiZugeingabe)
		{
			this.inaktivBeiZugeingabe = this.cbInaktivBeiZugeingabe.isSelected();
			this.parent.setClientsInaktivBeiZugeingabe(this.inaktivBeiZugeingabe);
		}
		else if (source == this.butRefreshClients)
			this.updateClientList();
		else if (source == this.butGetIp)
		{
			this.tfIp.setText(Utils.getMyIPAddress());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
	}
	
	private void close()
	{
		this.meineIp = this.tfIp.getText();
		
		this.setVisible(false);
		this.dispose();
	}
	
	private void updateClientList()
	{
		this.listClientsModel.clear();
		
		Object[] registeredClients = this.serverFunctions.getRegisteredClients();
		
		for (Object clientObj: registeredClients)
		{
			ServerFunctions.ClientScreenDisplayContentUpdater client =
					(ServerFunctions.ClientScreenDisplayContentUpdater)clientObj;
			
			String name = client.getClientName().length() == 0 ?
							SternResources.ServerSettingsJDialogUnbekannt(false) :
							client.getClientName();
			this.listClientsModel.addElement(name + " (" + client.getClientIp() + ")");
		}
	}
	
	private void setControlsEnabled()
	{
		this.cbInaktivBeiZugeingabe.setEnabled(!this.cbServerEnabled.isSelected());
	}
}
