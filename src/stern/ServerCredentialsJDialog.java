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
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.KeyPair;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.SternResources;
import commonServer.ClientUserCredentials;
import commonServer.CryptoLib;
import commonServer.RequestMessage;
import commonServer.RequestMessageActivateUser;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageChangeUser;
import commonServer.ServerConstants;
import commonServer.ServerUtils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") 
class ServerCredentialsJDialog extends JDialog implements ActionListener, IIntegerTextFieldDarkCallback
{
	private ButtonDark butOk;
	private ButtonDark butClose;
	private ButtonDark butPing;
	private ButtonDark butAuthBrowse;
	private ButtonDark butAuthActivate;
	private LabelDark labPing;
	private TextFieldDark tfAuthUrl;
	private IntegerTextFieldDark tfAuthPort;
	private TextFieldDark tfAuthUserId;
	private TextFieldDark tfAdminEmail;
	private TextFieldDark tfAuthFile;
	
	private CheckBoxDark cbServerCommunicationEnabled;
	private CheckBoxDark cbMuteNotificationSound;
	
	String serverUserCredentialsFile;
	boolean serverCommunicationEnabled;
	private ClientUserCredentials cuc;
	private String authUrlBefore;
	private int authPortBefore;
	boolean ok = false;
	
	private static Font font;
	public boolean muteNotificationSound;
	
	ServerCredentialsJDialog(
			Stern parent,
			String title,
			boolean serverCommunicationEnabled,
			String serverUserCredentialsFile,
			boolean muteNotificationSound)
	{
		super (parent, title, true);
		
		this.serverCommunicationEnabled = serverCommunicationEnabled;
		this.serverUserCredentialsFile = serverUserCredentialsFile;
		this.cuc = ServerUtils.readClientUserCredentials(this.serverUserCredentialsFile);
		this.muteNotificationSound = muteNotificationSound;
		
		if (this.cuc != null)
		{
			this.authUrlBefore = this.cuc.url;
			this.authPortBefore = this.cuc.port;
		}
		
		font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		PanelDark panAuth = new PanelDark(new BorderLayout(10, 10));
		
		GroupBoxDark panServerCommunicationEnabled = new GroupBoxDark(
				SternResources.Serververbindung(false), font);
		panServerCommunicationEnabled.setLayout(new SpringLayout());
		
		this.cbServerCommunicationEnabled = new CheckBoxDark(
				SternResources.Aktivieren(false),
				this.serverCommunicationEnabled,
				font);
		this.cbServerCommunicationEnabled.addActionListener(this);
		panServerCommunicationEnabled.add(this.cbServerCommunicationEnabled);
		
		this.cbMuteNotificationSound = 
				new CheckBoxDark(SternResources.BenachrichtigungStumm(false), muteNotificationSound, font);
		panServerCommunicationEnabled.add(this.cbMuteNotificationSound);
		
		SpringUtilities.makeCompactGrid(panServerCommunicationEnabled,
			      2, 1, //rows, cols
			      0, 0, //initialX, initialY
			      0, 0);//xPad, yPad
		
		panAuth.add(panServerCommunicationEnabled, BorderLayout.NORTH);
		
		GroupBoxDark panAuthMain = new GroupBoxDark(SternResources.ServerZugangsdaten(false), font);
		panAuthMain.setLayout(new BorderLayout(10, 10));
		
		PanelDark panAuthFile = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		panAuthFile.add(new LabelDark(SternResources.ServerAdminDatei(false), font));
		this.tfAuthFile = new TextFieldDark(font, 30);
		this.tfAuthFile.setEditable(false);
		panAuthFile.add(this.tfAuthFile);
		this.butAuthBrowse = new ButtonDark(this, SternResources.Auswaehlen(false), font);
		panAuthFile.add(this.butAuthBrowse);
		
		panAuthMain.add(panAuthFile, BorderLayout.NORTH);
		
		PanelDark panAuthInfo = new PanelDark(new SpringLayout());
		
		panAuthInfo.add(new LabelDark(SternResources.ServerAdminUrl(false)+":", font));
		this.tfAuthUrl = new TextFieldDark("", font, true); 
		panAuthInfo.add(this.tfAuthUrl);
		
		panAuthInfo.add(new LabelDark(SternResources.ServerAdminPort(false)+":", font));
		this.tfAuthPort = new IntegerTextFieldDark(this, font);
		panAuthInfo.add(this.tfAuthPort);
		
		panAuthInfo.add(new LabelDark(SternResources.UserId(false)+":", font));
		this.tfAuthUserId = new TextFieldDark("", font, false);
		panAuthInfo.add(this.tfAuthUserId);
		
		panAuthInfo.add(new LabelDark(SternResources.ServerEmailAdmin(false)+":", font));
		this.tfAdminEmail = new TextFieldDark("", font, false);
		panAuthInfo.add(this.tfAdminEmail);
		
		this.fillAuthCredentials(this.cuc);
		
		SpringUtilities.makeCompactGrid(panAuthInfo,
			      4, 2, //rows, cols
			      5, 5, //initialX, initialY
			      20, 5);//xPad, yPad
		
		panAuthMain.add(panAuthInfo, BorderLayout.CENTER);
		
		PanelDark panPing = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		this.butPing = new ButtonDark(this, SternResources.ServerAdminVerbindungstest(false), font);
		panPing.add(this.butPing);
		
		this.labPing = new LabelDark("", font);
		panPing.add(this.labPing);
		
		panAuthMain.add(panPing, BorderLayout.SOUTH);
		
		panAuth.add(panAuthMain, BorderLayout.CENTER);
		
		// ---------------				
		// ----
		panBase.add(panAuth, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butAuthActivate = new ButtonDark(this, SternResources.BenutzerAktivieren(false), font);
		panButtons.add(this.butAuthActivate);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		this.butClose = new ButtonDark(this, SternResources.Abbrechen(false), font);
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
		
		this.tfAuthPort.enableCheckInput();
		
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
		if (source == this.cbServerCommunicationEnabled)
		{
			this.serverCommunicationEnabled = this.cbServerCommunicationEnabled.isSelected();
			this.setControlsEnabled();
		}
		if (source == this.butOk)
		{
			if (this.cuc != null && this.authUrlBefore != null &&
				(!this.authUrlBefore.equals(this.tfAuthUrl.getText()) ||
				 this.authPortBefore != this.tfAuthPort.getValue()))
			{
				DialogWindowResult dialogResult = DialogWindow.showYesNoCancel(
						this,
						SternResources.ServerUrlUebernehmen(false),
					    SternResources.ServerZugangsdatenAendern(false));

				if (dialogResult == DialogWindowResult.YES)
				{
					this.cuc.url = this.tfAuthUrl.getText();
					this.cuc.port = this.tfAuthPort.getValue();
					ServerUtils.writeClientUserCredentials(cuc, this.serverUserCredentialsFile);
				}
				else if (dialogResult == DialogWindowResult.CANCEL)
				{
					return;
				}
			}
			
			this.ok = true;
			this.close();
		}
		else if (source == this.butAuthBrowse)
		{
			FileDialog fd = new FileDialog(this, SternResources.SpielLaden(false), FileDialog.LOAD);
			
			try
			{
				File file = new File(this.serverUserCredentialsFile);
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
					this.serverUserCredentialsFile = file.getAbsolutePath();
					this.fillAuthCredentials(this.cuc);
					this.authUrlBefore = this.cuc.url;
					this.authPortBefore = this.cuc.port;
				}
				else
					DialogWindow.showError(
						this,
						SternResources.UngueltigeAnmeldedaten(false, file.getAbsolutePath().toString()),
					    SternResources.Fehler(false));

			}
		}
		else if (source == this.butPing)
		{
			if (this.cuc != null)
			{
				this.cuc.url = this.tfAuthUrl.getText();
				this.cuc.port = this.tfAuthPort.getValue();
			}
			
			RequestMessage msg = new RequestMessage(RequestMessageType.PING);
			
			ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
			
			if (respMsg.error)
				this.labPing.setText(SternResources.VerbindungNichtErfolgreich(false));
			else
				this.labPing.setText(SternResources.VerbindungErfolgreich(false));
		}
		else if (source == this.butAuthActivate)
		{
			ClipboardImportJDialog<ResponseMessageChangeUser> dlg = 
					new ClipboardImportJDialog<ResponseMessageChangeUser>(
							this, ResponseMessageChangeUser.class, true);
			
			dlg.setVisible(true);
			
			if (dlg.dlgResult == DialogWindowResult.OK)
			{
				ResponseMessageChangeUser newUser = (ResponseMessageChangeUser)dlg.obj;
				
				if (newUser != null)
				{
					DialogWindowResult dialogResult = DialogWindow.showOkCancel(
							this,
							SternResources.BenutzerAktivierenFrage(
									false, 
									newUser.userId, 
									newUser.serverUrl, 
									Integer.toString(newUser.serverPort)),
						    SternResources.BenutzerAktivieren(false));
					
					if (dialogResult != DialogWindowResult.OK)
						return;
					
					String filename = ServerUtils.getCredentialFileName(newUser.userId, newUser.serverUrl, newUser.serverPort);
					
					FileDialog fd = new FileDialog(
							this, 
							SternResources.BenutzerAktivierenAbspeichern(false), FileDialog.SAVE);
					
					fd.setDirectory(ServerUtils.getHomeFolder());

					fd.setFile(filename);
					fd.setVisible(true);
					
					filename = fd.getFile();
					
					if (filename == null)
						return;
					
					String directory = fd.getDirectory();
					
					KeyPair userKeyPair = CryptoLib.getNewKeyPair();
					
					RequestMessageActivateUser msg = new RequestMessageActivateUser();
					
					msg.userId = newUser.userId;
					msg.activationCode = newUser.activationCode;
					msg.userPublicKey = CryptoLib.encodePublicKeyToBase64(userKeyPair.getPublic());
					
					ClientUserCredentials cucTemp = new ClientUserCredentials();
					
					cucTemp.userId = ServerConstants.ACTIVATION_USER;
					cucTemp.url = newUser.serverUrl;
					cucTemp.port = newUser.serverPort;
					cucTemp.serverPublicKey = newUser.serverPublicKey;
					cucTemp.userPrivateKey = CryptoLib.encodePrivateKeyToBase64(userKeyPair.getPrivate());
					cucTemp.adminEmail = newUser.adminEmail;
					
					cucTemp.serverPublicKeyObject = CryptoLib.decodePublicKeyFromBase64(cucTemp.serverPublicKey);
					cucTemp.userPrivateKeyObject = CryptoLib.decodePrivateKeyFromBase64(cucTemp.userPrivateKey); 
					
					RequestMessage reqMsg = new RequestMessage(RequestMessageType.ACTIVATE_USER);
					reqMsg.payloadSerialized = msg.toJson();
					
					ResponseMessage respMsg = this.sendAndReceive(cucTemp, reqMsg);
					
					if (!respMsg.error)
					{
						cucTemp.userId = newUser.userId;
						
						File fileClientInfo = new File(directory, filename);
						
						boolean success = ServerUtils.writeClientUserCredentials(cucTemp, fileClientInfo.getAbsolutePath());
						
						if (success)
						{
							DialogWindow.showInformation(
									this,
									SternResources.BenutzerAktivierenErfolg(false),
								    SternResources.BenutzerAktivieren(false));
							
							this.serverUserCredentialsFile = fileClientInfo.getAbsolutePath();
							this.cuc = cucTemp;
							
							this.fillAuthCredentials(this.cuc);
						}						
					}
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
	
	private void close()
	{
		this.muteNotificationSound = this.cbMuteNotificationSound.isSelected();
		this.setVisible(false);
		this.dispose();
	}
	
	private void fillAuthCredentials(ClientUserCredentials cuc)
	{
		try
		{
			File f = new File(this.serverUserCredentialsFile);
			String fileName = f.getName();
			this.tfAuthFile.setText(fileName);
		}
		catch (Exception x)
		{
			this.tfAuthFile.setText("");
		}
		
		
		this.tfAuthUrl.setText(
				cuc == null || cuc.url == null ? SternResources.KeineDateiAusgewaehlt(false): cuc.url);
		
		this.tfAuthPort.setText(
				cuc == null || cuc.port == 0 ? "" : Integer.toString(cuc.port));
		
		this.tfAuthUserId.setText(
				cuc == null || cuc.userId == null ? "" : cuc.userId);		
		
		this.tfAdminEmail.setText(
				cuc == null || cuc.adminEmail == null ? "" : cuc.adminEmail);
	}
	
	@Override
	public void setControlsEnabled()
	{
		this.butAuthActivate.setEnabled(this.serverCommunicationEnabled && !this.tfAuthPort.error);
		this.butAuthBrowse.setEnabled(this.serverCommunicationEnabled);
		this.butPing.setEnabled(this.serverCommunicationEnabled && !this.tfAuthPort.error);
		this.butOk.setEnabled(!this.tfAuthPort.error);
	}
}
