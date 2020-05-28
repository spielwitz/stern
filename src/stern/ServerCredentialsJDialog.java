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
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.SternResources;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageActivateUser;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageChangeUser;
import commonServer.RsaCrypt;
import commonServer.ServerConstants;
import commonServer.ServerUtils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") class ServerCredentialsJDialog extends JDialog implements ActionListener
{
	private ButtonDark butOk;
	private ButtonDark butClose;
	private ButtonDark butPing;
	private ButtonDark butAuthBrowse;
	private ButtonDark butAuthActivate;
	private LabelDark labPing;
	private LabelDark labAuthUrl;
	private LabelDark labAuthPort;
	private LabelDark labAuthUserId;
	private TextFieldDark tfAuthFile;
	
	private CheckBoxDark cbServerCommunicationEnabled;
	
	String serverUserCredentialsFile;
	boolean serverCommunicationEnabled;
	private ClientUserCredentials cuc;
	boolean ok = false;
	
	private static Font font;
	
	ServerCredentialsJDialog(
			Stern parent,
			String title,
			boolean serverCommunicationEnabled,
			String serverUserCredentialsFile)
	{
		super (parent, title, true);
		
		this.serverCommunicationEnabled = serverCommunicationEnabled;
		this.serverUserCredentialsFile = serverUserCredentialsFile;
		this.cuc = ServerUtils.readClientUserCredentials(this.serverUserCredentialsFile);
		
		// Font laden
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
		panServerCommunicationEnabled.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.cbServerCommunicationEnabled = new CheckBoxDark(
				SternResources.Aktivieren(false),
				this.serverCommunicationEnabled,
				font);
		this.cbServerCommunicationEnabled.addActionListener(this);
		panServerCommunicationEnabled.add(this.cbServerCommunicationEnabled);
		
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
		this.labAuthUrl = new LabelDark("", font); 
		panAuthInfo.add(this.labAuthUrl);
		
		panAuthInfo.add(new LabelDark(SternResources.ServerAdminPort(false)+":", font));
		this.labAuthPort = new LabelDark("", font);
		panAuthInfo.add(this.labAuthPort);
		
		panAuthInfo.add(new LabelDark(SternResources.UserId(false)+":", font));
		this.labAuthUserId = new LabelDark("", font);
		panAuthInfo.add(this.labAuthUserId);
		
		this.fillAuthCredentials(this.cuc);
		
		SpringUtilities.makeCompactGrid(panAuthInfo,
			      3, 2, //rows, cols
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
		
		this.setControlsEnabled();
		
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
				}
				else
					JOptionPane.showMessageDialog(this,
						SternResources.UngueltigeAnmeldedaten(false, file.getAbsolutePath().toString()),
					    SternResources.Fehler(false),
					    JOptionPane.ERROR_MESSAGE);

			}
		}
		else if (source == this.butPing)
		{
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
			
			if (dlg.dlgResult == JOptionPane.OK_OPTION)
			{
				ResponseMessageChangeUser newUser = (ResponseMessageChangeUser)dlg.obj;
				
				if (newUser != null)
				{
					int dialogResult = JOptionPane.showConfirmDialog(this,
							SternResources.BenutzerAktivierenFrage(
									false, 
									newUser.userId, 
									newUser.serverUrl, 
									Integer.toString(newUser.serverPort)),
						    SternResources.BenutzerAktivieren(false),
						    JOptionPane.OK_CANCEL_OPTION);
					
					if (dialogResult != JOptionPane.OK_OPTION)
						return;
					
					// Wohin soll das Credential-File gespeichert werden?
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
					
					// Eigenen Key anlegen
					KeyPair userKeyPair = RsaCrypt.getNewKeyPair();
					
					RequestMessageActivateUser msg = new RequestMessageActivateUser();
					
					msg.userId = newUser.userId;
					msg.activationCode = newUser.activationCode;
					msg.userPublicKey = RsaCrypt.encodePublicKeyToBase64(userKeyPair.getPublic());
					
					// Client Credential Objekt anlegen
					ClientUserCredentials cucTemp = new ClientUserCredentials();
					
					cucTemp.userId = ServerConstants.ACTIVATION_USER;
					cucTemp.url = newUser.serverUrl;
					cucTemp.port = newUser.serverPort;
					cucTemp.serverPublicKey = newUser.serverPublicKey;
					cucTemp.userPrivateKey = RsaCrypt.encodePrivateKeyToBase64(userKeyPair.getPrivate());
					
					cucTemp.serverPublicKeyObject = RsaCrypt.decodePublicKeyFromBase64(cucTemp.serverPublicKey);
					cucTemp.userPrivateKeyObject = RsaCrypt.decodePrivateKeyFromBase64(cucTemp.userPrivateKey); 
					
					// Aktivieren-Nachricht an den Server schicken
					RequestMessage reqMsg = new RequestMessage(RequestMessageType.ACTIVATE_USER);
					reqMsg.payloadSerialized = msg.toJson();
					
					ResponseMessage respMsg = this.sendAndReceive(cucTemp, reqMsg);
					
					if (!respMsg.error)
					{
						// Wenn die Antwort vom Server positiv ist, Credential-File abspeichern
						cucTemp.userId = newUser.userId;
						
						File fileClientInfo = new File(directory, filename);
						
						try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileClientInfo.getAbsoluteFile())))
						{
							String text = cucTemp.toJson();
							bw.write(text);
							
							JOptionPane.showMessageDialog(this,
									SternResources.BenutzerAktivierenErfolg(false),
								    SternResources.BenutzerAktivieren(false),
								    JOptionPane.INFORMATION_MESSAGE);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						this.serverUserCredentialsFile = fileClientInfo.getAbsolutePath();
						this.cuc = cucTemp;
						
						// UI aktuaslisieren
						this.fillAuthCredentials(this.cuc);
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
			JOptionPane.showMessageDialog(this,
				    respMsg.errorMsg,
				    SternResources.Verbindungsfehler(false),
				    JOptionPane.ERROR_MESSAGE);
		
		return respMsg;
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
			File f = new File(this.serverUserCredentialsFile);
			String fileName = f.getName();
			this.tfAuthFile.setText(fileName);
		}
		catch (Exception x)
		{
			this.tfAuthFile.setText("");
		}
		
		
		this.labAuthUrl.setText(
				cuc == null || cuc.url == null ? SternResources.KeineDateiAusgewaehlt(false): cuc.url);
		
		this.labAuthPort.setText(
				cuc == null || cuc.port == 0 ? "" : Integer.toString(cuc.port));
		
		this.labAuthUserId.setText(
				cuc == null || cuc.userId == null ? "" : cuc.userId);		
	}
	
	private void setControlsEnabled()
	{
		this.butAuthActivate.setEnabled(this.serverCommunicationEnabled);
		this.butAuthBrowse.setEnabled(this.serverCommunicationEnabled);
		this.butPing.setEnabled(this.serverCommunicationEnabled);
	}
}
