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
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.SternResources;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageChangeUser;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGetUsers;
import commonServer.ResponseMessageChangeUser;
import commonServer.ServerUtils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.PasswordFieldDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial")
public class ServerAdminJDialog extends JDialog 
			implements ChangeListener, ActionListener, ListSelectionListener
{
	private ButtonDark butClose;
	private ButtonDark butNewUserOk;
	private ButtonDark butPing;
	private ButtonDark butAuthBrowse;
	private ButtonDark butShutdown;
	private ButtonDark butAddUser;
	private ButtonDark butDeleteUser;
	private ButtonDark butRefreshUserList;
	private LabelDark labPing;
	private LabelDark labAuthUrl;
	private LabelDark labAuthPort;
	private LabelDark labAuthUserId;
	private TextFieldDark tfAuthFile;
	
	private PanelUserData panUsersDetailsInner;
	
	public String serverAdminCredentialsFile;
	private ClientUserCredentials cuc;
	
	private JList<String> listUsers;
	private Hashtable<String, ResponseMessageGetUsers.UserInfo> usersOnServer;

	
	private static Font font;
	
	public ServerAdminJDialog(
			Stern parent,
			String title,
			String serverAdminCredentialsFile)
	{
		super (parent, title, true);
		
		this.serverAdminCredentialsFile = serverAdminCredentialsFile;
		this.usersOnServer = new Hashtable<String, ResponseMessageGetUsers.UserInfo>();
		
		this.cuc = ServerUtils.readClientUserCredentials(this.serverAdminCredentialsFile);
		
		// Font laden
		font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		JTabbedPane tabpane = new JTabbedPane
	            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
		
		// ---------------
		PanelDark panUsersOuter = new PanelDark(new SpringLayout());
		
		PanelDark panUsersInner = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panUsersButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		this.butRefreshUserList = new ButtonDark(this, SternResources.ServerAdminUserNeuLaden(false), font);
		panUsersButtons.add(this.butRefreshUserList);
		this.butAddUser = new ButtonDark(this, SternResources.ServerAdminSpielerAnlegen(false), font);
		panUsersButtons.add(this.butAddUser);

		this.butNewUserOk = new ButtonDark(this, SternResources.ServerAdminAnDenSeverSchicken(false), font);
		panUsersButtons.add(this.butNewUserOk);
		panUsersInner.add(panUsersButtons, BorderLayout.SOUTH);
		
		PanelDark panUsersListAndDetails = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panUsersList = new PanelDark(new BorderLayout(0,0));
		
		this.listUsers = new JList<String>(new DefaultListModel<String>());
		
		listUsers.setBackground(new Color(30, 30, 30));
		listUsers.setForeground(Color.white);
		listUsers.setFont(font);
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listUsers.setLayoutOrientation(JList.VERTICAL);
		listUsers.setVisibleRowCount(-1);
		
		JScrollPane sc = new JScrollPane(listUsers);
		sc.setBorder(BorderFactory.createLineBorder(Color.white));		
		sc.setPreferredSize(new Dimension(125, 200));
		
		panUsersList.add(sc, BorderLayout.CENTER);
		
		PanelDark panListButtons = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		this.butDeleteUser = new ButtonDark(this, SternResources.ServerAdminSpielerLoeschen(false), font);
		panListButtons.add(this.butDeleteUser);
		
		panUsersList.add(panListButtons, BorderLayout.SOUTH);
				
		panUsersListAndDetails.add(panUsersList, BorderLayout.WEST);
		
		PanelDark panUsersDetailsOuter = new PanelDark(new BorderLayout());
		
		this.panUsersDetailsInner = new PanelUserData(Mode.NoUserSelected);
		panUsersDetailsOuter.add(this.panUsersDetailsInner, BorderLayout.NORTH);
		
		panUsersListAndDetails.add(panUsersDetailsOuter, BorderLayout.CENTER);
		
		panUsersInner.add(panUsersListAndDetails, BorderLayout.CENTER);
		
		panUsersOuter.add(panUsersInner);
		
		SpringUtilities.makeCompactGrid(panUsersOuter,
			      1, 1, //rows, cols
			      10, 10, //initialX, initialY
			      0, 0);//xPad, yPad
				
		tabpane.addTab(SternResources.ServerAdminSpieler(false), panUsersOuter);
		
		// ---------------
		PanelDark panShutdownOuter = new PanelDark(new SpringLayout());
		
		PanelDark panShutdown = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panButtonsShutdown = new PanelDark(new FlowLayout());
		
		this.butShutdown = new ButtonDark(this, SternResources.ServerAdminShutdown(false), font);
		panButtonsShutdown.add(this.butShutdown);
		
		panShutdown.add(panButtonsShutdown, BorderLayout.CENTER);
		
		panShutdownOuter.add(panShutdown);
		
		SpringUtilities.makeCompactGrid(panShutdownOuter,
			      1, 1, //rows, cols
			      10, 10, //initialX, initialY
			      0, 0);//xPad, yPad
		
		tabpane.addTab(SternResources.ServerAdminShutdown(false), panShutdownOuter);
		
		// ---------------
		PanelDark panAuthOuter = new PanelDark(new SpringLayout());
		
		PanelDark panAuth = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panAuthFile = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		panAuthFile.add(new LabelDark(SternResources.ServerAdminDatei(false), font));
		this.tfAuthFile = new TextFieldDark(font, 30);
		this.tfAuthFile.setEditable(false);
		panAuthFile.add(this.tfAuthFile);
		this.butAuthBrowse = new ButtonDark(this, SternResources.Auswaehlen(false), font);
		panAuthFile.add(this.butAuthBrowse);
		
		panAuth.add(panAuthFile, BorderLayout.NORTH);
		
		PanelDark panAuthInfo = new PanelDark(new SpringLayout());
		
		panAuthInfo.add(new LabelDark(SternResources.ServerAdminUrl(false)+":", font));
		this.labAuthUrl = new LabelDark("", font); 
		panAuthInfo.add(this.labAuthUrl);
		
		panAuthInfo.add(new LabelDark(SternResources.ServerAdminPort(false)+ ":", font));
		this.labAuthPort = new LabelDark("", font);
		panAuthInfo.add(this.labAuthPort);
		
		panAuthInfo.add(new LabelDark(SternResources.UserId(false) + ":", font));
		this.labAuthUserId = new LabelDark("", font);
		panAuthInfo.add(this.labAuthUserId);
		
		this.fillAuthCredentials(this.cuc);
		
		SpringUtilities.makeCompactGrid(panAuthInfo,
			      3, 2, //rows, cols
			      5, 5, //initialX, initialY
			      20, 5);//xPad, yPad
		
		panAuth.add(panAuthInfo, BorderLayout.CENTER);
		
		PanelDark panPing = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		this.butPing = new ButtonDark(this, SternResources.ServerAdminVerbindungstest(false), font);
		panPing.add(this.butPing);
		
		this.labPing = new LabelDark("", font);
		panPing.add(this.labPing);
		
		panAuth.add(panPing, BorderLayout.SOUTH);
		
		panAuthOuter.add(panAuth);
		
		SpringUtilities.makeCompactGrid(panAuthOuter,
			      1, 1, //rows, cols
			      10, 10, //initialX, initialY
			      0, 0);//xPad, yPad
		
		tabpane.add(SternResources.ServerAdminAdminAuth(false), panAuthOuter);
		
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
		
		if (this.cuc == null)
			tabpane.setSelectedComponent(panAuth);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
		
		if (this.cuc != null)
			this.refreshUserList(false);
		
		this.setControlsEnabledUsers();
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (source == this.butClose || source == this.getRootPane())
			this.close();
		else if (source == this.butAuthBrowse)
		{
			FileDialog fd = new FileDialog(this, SternResources.SpielLaden(false), FileDialog.LOAD);
			
			try
			{
				File file = new File(this.serverAdminCredentialsFile);
				String directory = file.getParent();
				fd.setDirectory(directory);
			}
			catch (Exception x)
			{
				fd.setDirectory(ServerUtils.getHomeFolder());
			}
			
			fd.setVisible(true);
			
			String dirname = fd.getDirectory();
			String filename = fd.getFile();
			
			if (filename != null)
			{
				File file = new File(dirname, filename);
				
				this.cuc = ServerUtils.readClientUserCredentials(file.getAbsolutePath());
				
				if (cuc != null)
				{
					this.serverAdminCredentialsFile = file.getAbsolutePath();
					this.fillAuthCredentials(this.cuc);
				}
				else
					JOptionPane.showMessageDialog(this,
							SternResources.UngueltigeAnmeldedaten(false, file.getAbsoluteFile().toString()),
						    SternResources.Fehler(false),
						    JOptionPane.ERROR_MESSAGE);

			}
		}
		else if (source == this.butPing)
		{
			RequestMessage msg = new RequestMessage(RequestMessageType.ADMIN_PING);
			
			ResponseMessage respMsg = this.sendAndReceive(msg, true);
			
			if (respMsg.error)
				this.labPing.setText(SternResources.VerbindungNichtErfolgreich(false));
			else
				this.labPing.setText(SternResources.VerbindungErfolgreich(false));
		}
		else if (source == this.butNewUserOk)
		{
			this.usersPostChangesToServer();
		}
		else if (source == this.butShutdown)
		{
			int dialogResult = JOptionPane.showConfirmDialog(this,
					SternResources.ServerAdminShutdownFrage(false),
					SternResources.ServerAdminShutdown(false),
				    JOptionPane.OK_CANCEL_OPTION);
			
			if (dialogResult == JOptionPane.OK_OPTION)
			{
				dialogResult = JOptionPane.showConfirmDialog(this,
					    SternResources.AreYouSure(false),
					    SternResources.ServerAdminShutdown(false),
					    JOptionPane.YES_NO_OPTION);
				
				if (dialogResult == JOptionPane.YES_OPTION)
				{
					RequestMessage msgRequest = new RequestMessage(RequestMessageType.ADMIN_SERVER_SHUTDOWN);
					
					ResponseMessage msgResponse = this.sendAndReceive(msgRequest, true);
					
					if (!msgResponse.error)
						JOptionPane.showMessageDialog(this,
							    SternResources.ServerAdminShutdownDone(false),
							    SternResources.ServerAdminShutdown(false),
							    JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		else if (source == this.butAddUser)
		{
			this.panUsersDetailsInner.setMode(Mode.NewUser);
			this.setControlsEnabledUsers();
		}
		else if (source == this.butRefreshUserList)
		{
			this.refreshUserList(true);
			this.panUsersDetailsInner.setMode(Mode.NoUserSelected);
			this.setControlsEnabledUsers();			
		}
		else if (source == this.butDeleteUser)
		{
			this.userDelete();
		}
	}
	
	private ResponseMessage sendAndReceive(RequestMessage msg, boolean message)
	{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ResponseMessage respMsg = ClientSocketManager.sendAndReceive(this.cuc, msg);
		this.setCursor(Cursor.getDefaultCursor());
		
		if (respMsg.error && message)
			JOptionPane.showMessageDialog(this,
				    respMsg.errorMsg,
				    SternResources.Verbindungsfehler(false),
				    JOptionPane.ERROR_MESSAGE);
		
		return respMsg;
	}
	
	private void refreshUserList(boolean message)
	{
		this.listUsers.removeListSelectionListener(this);
		
		ResponseMessage resp = this.sendAndReceive(
				new RequestMessage(RequestMessageType.ADMIN_GET_USERS), message);
		
		if (!resp.error)
		{
			ResponseMessageGetUsers respGetUser = ResponseMessageGetUsers.fromJson(resp.payloadSerialized);
			
			ArrayList<String> users = new ArrayList<String>();
			this.usersOnServer.clear();
			
			for (ResponseMessageGetUsers.UserInfo userInfo: respGetUser.users)
			{
				users.add(userInfo.userId);
				
				this.usersOnServer.put(userInfo.userId, userInfo);
			}
			
			Collections.sort(users);
			
			DefaultListModel<String> lm = (DefaultListModel<String>) this.listUsers.getModel();
			lm.clear();
			
			for (String user: users)
				lm.addElement(user);

		}
		
		this.listUsers.addListSelectionListener(this);
	}
	

	@Override
	public void stateChanged(ChangeEvent e) {
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	private void fillAuthCredentials(ClientUserCredentials cuc)
	{
		try
		{
			File f = new File(this.serverAdminCredentialsFile);
			String fileName = f.getName();
			this.tfAuthFile.setText(fileName);
		}
		catch (Exception x)
		{
			this.tfAuthFile.setText("");
		}
		
		
		this.labAuthUrl.setText(
				cuc == null || cuc.url == null ? SternResources.KeineDateiAusgewaehlt(false) : cuc.url);
		
		this.labAuthPort.setText(
				cuc == null || cuc.port == 0 ? "" : Integer.toString(cuc.port));
		
		this.labAuthUserId.setText(
				cuc == null || cuc.userId == null ? "" : cuc.userId);
	}	
	
	private void usersPostChangesToServer()
	{
		Mode mode = this.panUsersDetailsInner.mode;
		
		// Passwort prüfen
		String password1 = null; 
				
		if (mode == Mode.NewUser || mode == Mode.RenewCredentials)
		{	
			password1 = new String(this.panUsersDetailsInner.tfPassword1.getPassword());
			String password2 = new String(this.panUsersDetailsInner.tfPassword2.getPassword());
			
			if (!password1.equals(password2))
			{
				JOptionPane.showMessageDialog(this,
						SternResources.PasswoerterUnterschiedlich(false),
					    SternResources.ServerAdminSpieler(false),
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (password1.length() < 3)
			{
				JOptionPane.showMessageDialog(this,
						SternResources.PasswortZuKurz(false),
					    SternResources.ServerAdminSpieler(false),
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		RequestMessageChangeUser reqMsgChangeUser = new RequestMessageChangeUser();
		
		reqMsgChangeUser.userId = this.panUsersDetailsInner.tfUserId.getText().trim();
		reqMsgChangeUser.name = this.panUsersDetailsInner.tfName.getText().trim();
		reqMsgChangeUser.email = this.panUsersDetailsInner.tfEmail.getText().trim();
		reqMsgChangeUser.create = (mode == Mode.NewUser);
		reqMsgChangeUser.renewCredentials = (mode == Mode.NewUser || mode == Mode.RenewCredentials);
		
		int dialogResult = 0;
		
		if (mode == Mode.NewUser)
		{
			dialogResult = JOptionPane.showConfirmDialog(this,
					SternResources.ServerAdminBenutzerAnlegenFrage(false, reqMsgChangeUser.userId),
				    SternResources.ServerAdminSpieler(false),
				    JOptionPane.OK_CANCEL_OPTION);
		}
		else if (mode == Mode.RenewCredentials)
		{
			dialogResult = JOptionPane.showConfirmDialog(this,
					SternResources.ServerAdminUserRenewCredentials(false, reqMsgChangeUser.userId),
				    SternResources.ServerAdminSpieler(false),
				    JOptionPane.OK_CANCEL_OPTION);
		}
		else if (mode == Mode.ChangeUser)
		{
			dialogResult = JOptionPane.showConfirmDialog(this,
					SternResources.ServerAdminUserUpdate(false, reqMsgChangeUser.userId),
				    SternResources.ServerAdminSpieler(false),
				    JOptionPane.OK_CANCEL_OPTION);
		}
		else
			return; // Da ist was falsch implementiert 
			
		
		if (dialogResult != JOptionPane.OK_OPTION)
			return;
		
		RequestMessage msg = new RequestMessage(RequestMessageType.ADMIN_CHANGE_USER);
		
		msg.payloadSerialized = reqMsgChangeUser.toJson();
		
		ResponseMessage respMsg = this.sendAndReceive(msg, true);
		
		if (!respMsg.error)
		{
			// Userliste aktualisieren
			this.butRefreshUserList.doClick();
			
			ResponseMessageChangeUser msgResponseNewUser = 
					ResponseMessageChangeUser.fromJson(respMsg.payloadSerialized);
			
			if (reqMsgChangeUser.renewCredentials)
			{
				// Einfach nur eine Erfolgsnachricht bringen
				JOptionPane.showMessageDialog(
						this, 
						SternResources.ServerAdminUserCreated(false, reqMsgChangeUser.userId), 
						SternResources.ServerAdminSpieler(false),
						JOptionPane.INFORMATION_MESSAGE);
				
				EmailToolkit.launchEmailClient(
						reqMsgChangeUser.email, 
						SternResources.EmailSubjectNeuerUser(false, reqMsgChangeUser.userId), 
						SternResources.NeuerUserEMailBody(
								false, 
								reqMsgChangeUser.name, 
								reqMsgChangeUser.userId, 
								this.cuc.url, 
								Integer.toString(this.cuc.port)), 
						password1,
						msgResponseNewUser);
			}
			else
			{
				// Einfach nur eine Erfolgsnachricht bringen
				JOptionPane.showMessageDialog(
						this, 
						SternResources.ServerAdminUserErfolg(false, reqMsgChangeUser.userId), 
						SternResources.ServerAdminSpieler(false),
						JOptionPane.INFORMATION_MESSAGE);
			}
		}		
	}
	
	private void userDelete()
	{
		String userId = listUsers.getSelectedValue();
		
		if (userId == null)
			return;
		
		RequestMessage requestMessage = new RequestMessage(RequestMessageType.ADMIN_DELETE_USER);
		
		requestMessage.payloadSerialized = userId;
		
		int dialogResult = JOptionPane.showConfirmDialog(this,
				SternResources.ServerAdminUserDelete(false, userId),
			    SternResources.ServerAdminSpieler(false),
			    JOptionPane.OK_CANCEL_OPTION);
		
		if (dialogResult != JOptionPane.OK_OPTION)
			return;
		
		dialogResult = JOptionPane.showConfirmDialog(this,
				SternResources.AreYouSure(false),
			    SternResources.ServerAdminSpieler(false),
			    JOptionPane.YES_NO_OPTION);
		
		if (dialogResult != JOptionPane.YES_OPTION)
			return;
		
		ResponseMessage respMsg = this.sendAndReceive(requestMessage, true);
		
		if (!respMsg.error)
		{
			// Userliste aktualisieren
			this.butRefreshUserList.doClick();
			
			JOptionPane.showMessageDialog(
					this, 
					SternResources.ServerAdminUserDeleted(false, userId), 
					SternResources.ServerAdminSpieler(false),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	// ----------------------
	private class PanelUserData extends PanelDark implements ActionListener
	{
		private TextFieldDark tfUserId;
		private TextFieldDark tfName;
		private TextFieldDark tfEmail;
		private LabelDark labUserId;
		private LabelDark labName;
		private LabelDark labEmail;
		private LabelDark labPassword1;
		private LabelDark labPassword2;
		private PasswordFieldDark tfPassword1;
		private PasswordFieldDark tfPassword2;
		private CheckBoxDark cbCredentials;
		private CheckBoxDark cbUserActive;
		
		private Mode mode;
		
		public PanelUserData(Mode mode)
		{
			super(new SpringLayout());
			
			this.mode = mode;
			
			this.labUserId = new LabelDark(SternResources.UserId(false), font); 
			this.add(this.labUserId);
			this.tfUserId = new TextFieldDark(font, 30);
			this.add(this.tfUserId);
			this.labName = new LabelDark(SternResources.Name(false), font);
			this.add(this.labName);
			this.tfName = new TextFieldDark(font, 30);
			this.add(this.tfName);
			this.labEmail = new LabelDark(SternResources.EMailAdresse(false), font);
			this.add(this.labEmail);
			this.tfEmail = new TextFieldDark(font, 30);
			this.add(this.tfEmail);
			this.labPassword1 = new LabelDark(SternResources.Aktivierungspasswort(false), font);
			this.add(this.labPassword1);
			this.tfPassword1 = new PasswordFieldDark("", font);
			this.tfPassword1.setColumns(30);
			this.add(this.tfPassword1);
			this.labPassword2 = new LabelDark(SternResources.PasswortWiederholen(false), font);
			this.add(this.labPassword2);
			this.tfPassword2 = new PasswordFieldDark("", font);
			this.tfPassword2.setColumns(30);
			this.add(this.tfPassword2);
			this.add(new LabelDark("", font));
			this.cbCredentials = new CheckBoxDark(SternResources.ServerAdminAnmeldedatenErneuern(false), false, font);
			this.add(this.cbCredentials);
			this.cbCredentials.addActionListener(this);
			this.add(new LabelDark("", font));
			this.cbUserActive = new CheckBoxDark("Spieler ist aktiv", false, font);
			this.add(this.cbUserActive);
			
			SpringUtilities.makeCompactGrid(this,
				      7, 2, //rows, cols
				      5, 5, //initialX, initialY
				      20, 5);//xPad, yPad
		}
		
		public void setMode(Mode mode)
		{
			this.mode = mode;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == this.cbCredentials)
			{
				if (this.cbCredentials.isSelected())
				{
					this.setMode(Mode.RenewCredentials);
					setControlsEnabledUsers();
				}
				else if (this.mode == Mode.RenewCredentials)
				{
					this.setMode(Mode.ChangeUser);
					setControlsEnabledUsers();
				}
			}
		}
	}
	
	private enum Mode 
	{
		NoUserSelected,
		ChangeUser,
		NewUser,
		RenewCredentials
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() != this.listUsers)
			return;
		
		this.panUsersDetailsInner.setMode(Mode.ChangeUser);		
		this.setControlsEnabledUsers();	
	}
	
	private void setControlsEnabledUsers()
	{
		Mode mode = this.panUsersDetailsInner.mode;
		String selectedUser = listUsers.getSelectedValue();
		ResponseMessageGetUsers.UserInfo userInfo = null;
		
		if (selectedUser != null)
			userInfo = this.usersOnServer.get(selectedUser);
		
		switch (mode)
		{
		case NoUserSelected:
				this.userListClearSelection();
			
				this.panUsersDetailsInner.labUserId.setEnabled(false);
				this.panUsersDetailsInner.tfUserId.setText("");
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(false);
				this.panUsersDetailsInner.tfName.setText("");
				this.panUsersDetailsInner.tfName.setEditable(false);
				
				this.panUsersDetailsInner.labEmail.setEnabled(false);
				this.panUsersDetailsInner.tfEmail.setText("");
				this.panUsersDetailsInner.tfEmail.setEditable(false);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(false);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(false);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(false);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(false);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(false);
				this.panUsersDetailsInner.cbCredentials.setSelected(false);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(false);
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(false);
				this.butNewUserOk.setEnabled(false);
				
				break;
		
		case ChangeUser:
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText(userInfo.userId);
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText(userInfo.name);
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText(userInfo.email);
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(false);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(false);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(false);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(false);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(true);
				this.panUsersDetailsInner.cbCredentials.setSelected(false);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(userInfo.activated);
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(true);
				this.butNewUserOk.setEnabled(true);
				
				break;
		
		case NewUser:
				this.userListClearSelection();
				
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText("");
				this.panUsersDetailsInner.tfUserId.setEditable(true);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText("");
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText("");
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(true);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(true);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(true);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(true);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(false);
				this.panUsersDetailsInner.cbCredentials.setSelected(true);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(false);
				
				this.butAddUser.setEnabled(false);
				this.butDeleteUser.setEnabled(false);
				this.butNewUserOk.setEnabled(true);
				
				break;
		
		case RenewCredentials:
				this.panUsersDetailsInner.labUserId.setEnabled(true);
				this.panUsersDetailsInner.tfUserId.setText(userInfo.userId);
				this.panUsersDetailsInner.tfUserId.setEditable(false);
				
				this.panUsersDetailsInner.labName.setEnabled(true);
				this.panUsersDetailsInner.tfName.setText(userInfo.name);
				this.panUsersDetailsInner.tfName.setEditable(true);
				
				this.panUsersDetailsInner.labEmail.setEnabled(true);
				this.panUsersDetailsInner.tfEmail.setText(userInfo.email);
				this.panUsersDetailsInner.tfEmail.setEditable(true);
				
				this.panUsersDetailsInner.labPassword1.setEnabled(true);
				this.panUsersDetailsInner.tfPassword1.setText("");
				this.panUsersDetailsInner.tfPassword1.setEditable(true);
				
				this.panUsersDetailsInner.labPassword2.setEnabled(true);
				this.panUsersDetailsInner.tfPassword2.setText("");
				this.panUsersDetailsInner.tfPassword2.setEditable(true);
				
				this.panUsersDetailsInner.cbCredentials.setEnabled(true);
				this.panUsersDetailsInner.cbCredentials.setSelected(true);
				
				this.panUsersDetailsInner.cbUserActive.setEnabled(false);
				this.panUsersDetailsInner.cbUserActive.setSelected(userInfo.activated);
				
				this.butAddUser.setEnabled(true);
				this.butDeleteUser.setEnabled(true);
				this.butNewUserOk.setEnabled(true);
				
				break;
		}		
	}
	
	private void userListClearSelection()
	{
		this.listUsers.removeListSelectionListener(this);
		this.listUsers.clearSelection();
		this.listUsers.addListSelectionListener(this);
	}
}
