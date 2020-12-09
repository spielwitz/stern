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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;

import common.Colors;
import common.Constants;
import common.Spiel;
import common.Spiel.PlanetenInfo;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.RequestMessageGameHostDeleteGame;
import commonServer.RequestMessageGameHostFinalizeGame;
import commonServer.RequestMessageType;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonUi.ButtonDark;
import commonUi.ComboBoxDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;
import common.SpielInfo;
import common.SpielOptionen;
import common.Spieler;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial") class ServerGamesJDialog extends JDialog implements ActionListener, IColorChooserCallback
{
	private JTabbedPane tabpane;
	private PanelDark panNewGame;
	
	private ButtonDark butClose;
	private ButtonDark butNewGameNeuesSpielfeld;
	private ButtonDark butNewGameSubmit;
	
	private SpielfeldDisplay panSpielfeldDisplay;
	
	private ComboBoxDark<String>[] comboSpielerArray;
	private PanelDark[] panSpieler;
	private SpielerFarbenCanvas[] canvasSpielerFarben;
	private ArrayList<Spieler> spieler;
	private CheckBoxDark[] cbBot;
	private HashSet<SpielOptionen> optionen;
	
	private Hashtable<SpielOptionen,CheckBoxDark> cbOptionen;
	private ComboBoxDark<String> comboLetztesJahr;
	private ComboBoxDark<String> comboSpieler;
	private ComboBoxDark<String> comboPlaneten;
	private TextFieldDark tfSpielName;
	
	private CheckBoxDark cbMuteNotificationSound;
	
	Spiel spielGeladen;
	
	private ClientUserCredentials cuc;
	
	private ResponseMessageGamesAndUsers gamesAndUsers;
	
	private Spiel spiel;
	
	private Stern parent;
	
	private static Font font;
	
	private final static String MAX_JAHRE_UNENDLICH = SternResources.SpielparameterJDialogUnendlich(false);
	
	public boolean muteNotificationSound;
	
	ServerGamesJDialog(
			Stern parent,
			String title,
			String currentGameId,
			ClientUserCredentials cuc,
			boolean muteNotificationSound,
			ResponseMessageGamesAndUsers gamesAndUsers)
	{
		super (parent, title, true);

		this.parent = parent;
		this.cuc = cuc;
		this.gamesAndUsers = gamesAndUsers;
		this.muteNotificationSound = muteNotificationSound;
		
		// Font laden
		font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		this.tabpane = new JTabbedPane
	            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
				
		// ---------------
		if (this.gamesAndUsers.gamesZugeingabe.size() > 0)
		{
			PanelSpielSelector panZugeingabe = new PanelSpielSelector(
					this.gamesAndUsers.gamesZugeingabe, false, currentGameId);
			tabpane.add(SternResources.ServerGamesSpielerWarten(false), panZugeingabe);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesWarten.size() > 0)
		{
			PanelSpielSelector panWarten = new PanelSpielSelector(
					this.gamesAndUsers.gamesWarten, false, currentGameId);
			tabpane.add(SternResources.ServerGamesIchWarte(false), panWarten);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesBeendet.size() > 0)
		{
			PanelSpielSelector panBeendet = new PanelSpielSelector(
					this.gamesAndUsers.gamesBeendet, false, currentGameId);
			tabpane.add(SternResources.ServerGamesBeendeteSpiele(false), panBeendet);
		}
		
		// ---------------
		if (this.gamesAndUsers.gamesSpielleiter.size() > 0)
		{
			PanelSpielSelector panSpielleiter = new PanelSpielSelector(
					this.gamesAndUsers.gamesSpielleiter, true, currentGameId);
			tabpane.add(SternResources.ServerGamesSpielleiteraktionen(false), panSpielleiter);
		}

		// ---------------
		this.panNewGame = this.getPanelNewGame();
		tabpane.add(SternResources.ServerGamesNeuesSpiel(false), this.panNewGame);
		
		// ----
		panBase.add(tabpane, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.cbMuteNotificationSound = 
				new CheckBoxDark(SternResources.BenachrichtigungStumm(false), muteNotificationSound, font);
		panButtons.add(this.cbMuteNotificationSound);
		
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
		Object source = event.getSource();
		
		if (source == this.butClose || source == this.getRootPane())
			this.close();
		else if (source == this.butNewGameSubmit)
			this.submitSpiel();
		else if (source == this.comboSpieler)
		{
			int anzSp = Integer.parseInt((String)this.comboSpieler.getSelectedItem());
			
			// Planetenzahl anpassen
			int anzPl = Utils.round(anzSp * ( 
					(double)Constants.ANZAHL_PLANETEN_MAX / (double)Constants.ANZAHL_SPIELER_MAX));
			
			this.comboPlaneten.removeActionListener(this);
			this.comboPlaneten.setSelectedItem(Integer.toString(anzPl));
			this.updateSpiel();
			this.comboPlaneten.addActionListener(this);
		}
		else if (source == this.comboPlaneten || 
				source == this.cbOptionen.get(SpielOptionen.SIMPEL) ||
				source == this.butNewGameNeuesSpielfeld)
			this.updateSpiel();
		else if (source.getClass() == CheckBoxDark.class)
		{
			for (int i = 0; i < Constants.ANZAHL_SPIELER_MAX; i++)
			{
				if (source == this.cbBot[i])
				{
					this.comboSpielerArray[i].setEnabled(!this.cbBot[i].isSelected());
					
					if (this.cbBot[i].isSelected())
					{
						this.comboSpielerArray[i].setSelectedIndex(0);
					}
					break;
				}
			}
		}
		else if (source.getClass() == ComboBoxDark.class)
		{
			for (int i = 1; i < Constants.ANZAHL_SPIELER_MAX; i++)
			{
				if (source == this.comboSpielerArray[i])
				{
					String selectedUser = (String)this.comboSpielerArray[i].getSelectedItem();
					if (selectedUser.equals(""))
						continue;
					
					// Pruefen, ob in anderen Boxen derselbe Name steht. Wenn ja, diese durch "" ersetzen
					for (int j = 1; j < Constants.ANZAHL_SPIELER_MAX; j++)
					{
						int otherIndex = (i + j) % Constants.ANZAHL_SPIELER_MAX;
						String otherSelectedUser = 
								(String)this.comboSpielerArray[otherIndex].getSelectedItem();
						
						if (otherSelectedUser.equals(selectedUser))
							this.comboSpielerArray[otherIndex].setSelectedItem("");
					}
					break;
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
				    respMsg.errorMsg,
				    SternResources.Verbindungsfehler(false));
		
		return respMsg;
	}
	
	private void selectGame(String gameId)
	{
		// Spiel vom Server laden
		RequestMessage msg = new RequestMessage(RequestMessageType.GET_GAME);
		
		msg.payloadSerialized = gameId;
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			// Jetzt das Spiel wie ein E-Mail-Spiel laden.
			Gson gson = new Gson();
			this.spielGeladen = gson.fromJson(respMsg.payloadSerialized, Spiel.class); 
			this.close();
		}
	}
	
	private void deleteGame(String gameId)
	{
		DialogWindowResult dialogResult = DialogWindow.showYesNo(
				this,
			    SternResources.ServerGamesLoeschenAys(false, gameId),
			    SternResources.ServerGamesLoeschen(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		RequestMessage msg = new RequestMessage(RequestMessageType.GAME_HOST_DELETE_GAME);
		
		RequestMessageGameHostDeleteGame msgPayload = new RequestMessageGameHostDeleteGame();
		
		msgPayload.gameHostUserId = this.cuc.userId;
		msgPayload.gameId = gameId;
		
		msg.payloadSerialized = msgPayload.toJson();
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			// Erfolgmeldung
			DialogWindow.showInformation(
					this, 
					SternResources.ServerGamesGameDeleted(false, gameId), 
					SternResources.ServerGamesLoeschen(false));
			
			// Dialog schließen
			this.close();
		}
	}
	
	private void finalizeGame(String gameId)
	{
		DialogWindowResult dialogResult = DialogWindow.showYesNo(
				this,
			    SternResources.ServerGamesBeendenAys(false, gameId),
			    SternResources.ServerGamesBeenden(false));
		
		if (dialogResult != DialogWindowResult.YES)
			return;
		
		RequestMessage msg = new RequestMessage(RequestMessageType.GAME_HOST_FINALIZE_GAME);
		
		RequestMessageGameHostFinalizeGame msgPayload = new RequestMessageGameHostFinalizeGame();
		
		msgPayload.gameHostUserId = this.cuc.userId;
		msgPayload.gameId = gameId;
		
		msg.payloadSerialized = msgPayload.toJson();
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			// Erfolgmeldung
			DialogWindow.showInformation(
					this, 
					SternResources.ServerGamesGameFinalized(false, gameId), 
					SternResources.ServerGamesBeenden(false));
			
			// Dialog schließen
			this.close();
		}
	}
	
	private void close()
	{
		this.muteNotificationSound = this.cbMuteNotificationSound.isSelected();
		this.setVisible(false);
		this.dispose();
	}
	
	@SuppressWarnings("unchecked")
	private PanelDark getPanelNewGame()
	{
		this.spieler = Spiel.getDefaultSpieler();
		this.optionen = Spiel.getDefaultSpielOptionen();
		
		this.panSpieler = new PanelDark[Constants.ANZAHL_SPIELER_MAX];
		this.comboSpielerArray = new ComboBoxDark[Constants.ANZAHL_SPIELER_MAX];
		this.cbBot = new CheckBoxDark[Constants.ANZAHL_SPIELER_MAX];
		this.canvasSpielerFarben = new SpielerFarbenCanvas[Constants.ANZAHL_SPIELER_MAX];
		this.cbOptionen = new Hashtable<SpielOptionen,CheckBoxDark>();		
		
		//
		PanelDark panShell = new PanelDark(new SpringLayout());
		
		PanelDark panBase = new PanelDark(new BorderLayout(10, 10));
		
		PanelDark panMain = new PanelDark(new GridLayout(6, 2, 20, 0));
		
		panMain.add(add(this.getSpielerPanel(0)));
		
		PanelDark panPlanetenSub1 = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panPlanetenSub1.add(new LabelDark(SternResources.Spieler(false), font));
		String[] spieler = new String[Constants.ANZAHL_SPIELER_MAX - Constants.ANZAHL_SPIELER_MIN + 1];
		for (int i = Constants.ANZAHL_SPIELER_MIN; i <= Constants.ANZAHL_SPIELER_MAX; i++)
			spieler[i-Constants.ANZAHL_SPIELER_MIN] = Integer.toString(i);
		this.comboSpieler = new ComboBoxDark<String>(spieler, font);
		panPlanetenSub1.add(this.comboSpieler);
		
		panPlanetenSub1.add(new JSeparator());
		
		panPlanetenSub1.add(new LabelDark(SternResources.Planeten(false), font));
		String[] planeten = new String[Constants.ANZAHL_PLANETEN_MAX - Constants.ANZAHL_SPIELER_MAX + 1];
		for (int i = Constants.ANZAHL_SPIELER_MAX; i <= Constants.ANZAHL_PLANETEN_MAX; i++)
			planeten[i-Constants.ANZAHL_SPIELER_MAX] = Integer.toString(i);
		this.comboPlaneten = new ComboBoxDark<String>(planeten, font);
		panPlanetenSub1.add(this.comboPlaneten);
		
		panMain.add(panPlanetenSub1);
		
		panMain.add(add(this.getSpielerPanel(1)));
		
		PanelDark panBisJahr = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panBisJahr.add(new LabelDark(SternResources.SpielparameterJDialogSpieleBisJahr(false)+" ", font));
		String[] jahre = { MAX_JAHRE_UNENDLICH, "15", "20", "30", "40", "50", "75", "100", "150", "200" };
		this.comboLetztesJahr = new ComboBoxDark<String>(jahre, font);
		panBisJahr.add(this.comboLetztesJahr);
		panMain.add(panBisJahr);
		
		panMain.add(add(this.getSpielerPanel(2)));
		
		CheckBoxDark cbSimpel = new CheckBoxDark(SternResources.SpielparameterJDialogSimpelStern(false), true, font);
		this.cbOptionen.put(SpielOptionen.SIMPEL, cbSimpel);
		this.cbOptionen.get(SpielOptionen.SIMPEL).addActionListener(this);
		panMain.add(cbSimpel);
		
		panMain.add(add(this.getSpielerPanel(3)));
		
		PanelDark panSpielName = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panSpielName.add(new LabelDark(SternResources.ServerGamesSpielname(false), font));
		this.tfSpielName = new TextFieldDark(font, 18);
		this.tfSpielName.setEditable(false);
		panSpielName.add(this.tfSpielName);
		panMain.add(panSpielName);
		
		panMain.add(add(this.getSpielerPanel(4)));
		
		panMain.add(new LabelDark(font));
		
		panMain.add(add(this.getSpielerPanel(5)));
		
		panMain.add(new LabelDark(font));

		// ----
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// ----
		PanelDark panSpielfeld = new PanelDark(new BorderLayout(10, 10));
		
		this.panSpielfeldDisplay = new SpielfeldDisplay(false);
		panSpielfeld.add(this.panSpielfeldDisplay, BorderLayout.CENTER);
						
		panBase.add(panSpielfeld, BorderLayout.EAST);
		
		// ----
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		this.butNewGameNeuesSpielfeld = new ButtonDark(this, SternResources.ServerGamesNeuesSpielfeld(false), font);
		panButtons.add(this.butNewGameNeuesSpielfeld);
		
		this.butNewGameSubmit = new ButtonDark(this, SternResources.ServerGamesSubmit(false), font);
		panButtons.add(this.butNewGameSubmit);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		// ---- Voreinstellungen
		this.comboSpielerArray[0].setSelectedItem(this.cuc.userId);
		this.comboSpielerArray[0].setEnabled(false);
		this.cbBot[0].setEnabled(false);
		
		for (SpielOptionen option: this.cbOptionen.keySet())
			this.cbOptionen.get(option).setSelected(this.optionen.contains(option));
		
		if (this.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL))
			this.comboLetztesJahr.setSelectedItem(Integer.toString(Constants.DEFAULT_MAX_JAHRE));
		else
			this.comboLetztesJahr.setSelectedItem(MAX_JAHRE_UNENDLICH);
		
		this.comboSpieler.setSelectedItem(Integer.toString(Constants.ANZAHL_SPIELER_MAX));
		this.comboPlaneten.setSelectedItem(Integer.toString(Constants.ANZAHL_PLANETEN_MAX));
		
		this.updateSpiel();
		
		this.comboSpieler.addActionListener(this);
		this.comboPlaneten.addActionListener(this);
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad

		return panShell;
	}

	@Override
	public void colorChanged(int sp, byte newColorIndex, byte oldColorIndex) 
	{
		for (SpielerFarbenCanvas c: this.canvasSpielerFarben)
		{
			if (c.spieler != sp && c.colIndex == newColorIndex)
			{
				c.setColor(oldColorIndex);
				break;
			}
		}
	}	
	
	private PanelDark getSpielerPanel(int spIndex)
	{
		this.panSpieler[spIndex] = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		canvasSpielerFarben[spIndex] = new SpielerFarbenCanvas(
				this, 
				this,
				spIndex, 
				this.spieler.get(spIndex).getColIndex(),
				font);
		canvasSpielerFarben[spIndex].setPreferredSize(new Dimension(14,14));
		this.panSpieler[spIndex].add(canvasSpielerFarben[spIndex]);
		
		String[] spieler = null;
		
		if (spIndex == 0)
			spieler = new String[] {this.cuc.userId};
		else
		{
			spieler = new String[this.gamesAndUsers.users.size()];
			spieler[0] = "";
			
			int counter = 1;
			for (String userId: this.gamesAndUsers.users.keySet())
			{
				if (!userId.equals(this.cuc.userId))
				{
					spieler[counter] = userId; 
					counter++;
				}
			}
		}

		this.comboSpielerArray[spIndex] = new ComboBoxDark<String>(spieler, font);
		this.comboSpielerArray[spIndex].setPrototypeDisplayValue("WWWWWWWWWWWWWW");
		
		if (spIndex != 0)
			this.comboSpielerArray[spIndex].addActionListener(this);
			
		//this.tfSpieler[spIndex].setText(this.spieler.get(spIndex).getName());
		
		this.panSpieler[spIndex].add(this.comboSpielerArray[spIndex]);
		
		this.cbBot[spIndex] = new CheckBoxDark(
				SternResources.SpielparameterJDialogBot(false), 
				this.spieler.get(spIndex).istComputer(), 
				font);
		
		this.cbBot[spIndex].addActionListener(this);
		this.panSpieler[spIndex].add(this.cbBot[spIndex]);
		
		return this.panSpieler[spIndex];
	}
		
	// ================================================
	
	private class SpielfeldDisplay extends JPanel
	{
		private static final int PIXEL_PRO_FELD = 10;
		private ArrayList<PlanetenInfo> plInfo;
		private boolean zeigeBesitzer;
		
		public SpielfeldDisplay(boolean zeigeBesitzer)
		{
			super();
			
			this.zeigeBesitzer = zeigeBesitzer;
			
			this.setPreferredSize(new Dimension(
					(Constants.FELD_MAX_X +1) * PIXEL_PRO_FELD, 
					(Constants.FELD_MAX_Y +1) * PIXEL_PRO_FELD));
		}
		
		public void refresh(ArrayList<PlanetenInfo> plInfo)
		{
			this.plInfo = plInfo;
			this.repaint();
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = this.getSize();
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, dim.width, dim.height);
			
			g.setColor(Color.WHITE);
			g.drawLine(0, 0, dim.width-1, 0);
			g.drawLine(0, 0, 0, dim.height-1);
			g.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
			g.drawLine(0, dim.height-1, dim.width, dim.height-1);
			
			if (this.plInfo == null)
				return;
			
			int offset = PIXEL_PRO_FELD / 2;
			
			for (Spiel.PlanetenInfo plInfo: this.plInfo)
			{
				if (this.zeigeBesitzer)					
					g.setColor(Colors.get(plInfo.col));
				else
					g.setColor(plInfo.col == Colors.INDEX_NEUTRAL ? 
							Colors.get(Colors.INDEX_NEUTRAL) : 
							Color.white);
				
				int x = Utils.round(offset + plInfo.x * PIXEL_PRO_FELD);
				int y = Utils.round(offset + plInfo.y * PIXEL_PRO_FELD);
				
				g.fillOval(x, y, PIXEL_PRO_FELD, PIXEL_PRO_FELD);
			}
		}

	}
	
	private void updateSpiel()
	{
		int anzSp = Integer.parseInt((String)this.comboSpieler.getSelectedItem());
		int anzPl = Integer.parseInt((String)this.comboPlaneten.getSelectedItem());
		
		for (SpielOptionen option: this.cbOptionen.keySet())
		{
			if (this.cbOptionen.get(option).isSelected())
				this.optionen.add(option);
			else
				this.optionen.remove(option);
		}

		for (int i = 0; i < Constants.ANZAHL_SPIELER_MAX; i++)
		{
			this.panSpieler[i].setVisible(i < anzSp);
			
			if (!this.optionen.contains(SpielOptionen.SIMPEL))
				this.cbBot[i].setSelected(false);
			
			this.cbBot[i].setEnabled(i > 0 && this.optionen.contains(SpielOptionen.SIMPEL));
		}
		
		this.spiel = Spiel.getSpiel(this.optionen,
				this.getSpieler(anzSp),
				this.gamesAndUsers.emailAdresseSpielleiter,
				anzPl,
				30); // Wird spaeter noch angepasst
		
		this.tfSpielName.setText(this.spiel.getName());
		this.panSpielfeldDisplay.refresh(this.spiel.getPlanetenInfo());
	}
	
	private void submitSpiel()
	{
		// Pruefen, ob alle Spieler zugewiesen sind.
		Spieler[] alleSpieler = this.getSpieler(this.spiel.getAnzSp());
		
		for (int i = 0; i < alleSpieler.length; i++)
		{
			Spieler spieler = alleSpieler[i];
			if (spieler.getName().equals("") && !spieler.istComputer())
			{
				DialogWindow.showError(
						this,
						SternResources.ServerGamesSubmitNamenZuweisen(false),
					    SternResources.Fehler(false));
				return;
			}
			
			// Wenn Bot, dann Namen korrigieren
			if (spieler.istComputer())
				spieler.setName("_" + SternResources.SpielparameterJDialogBot(false)+ (i+1)+"_");
			else
			{
				ResponseMessageGamesAndUsers.UserInfo userInfo = this.gamesAndUsers.users.get(spieler.getName());
				spieler.setEmailAdresse(userInfo.email);
			}
		}
		
		DialogWindowResult dialogResult = DialogWindow.showOkCancel(
				this,
				SternResources.ServerGamesSubmitFrage(false, spiel.getName()),
				SternResources.ServerGamesSubmit(false));
		
		if (dialogResult != DialogWindowResult.OK)
			return;
		
		this.spiel.setSpieler(alleSpieler);
		
		HashSet<SpielOptionen> optionen = this.spiel.getOptionen();
		String maxJahreString = (String)this.comboLetztesJahr.getSelectedItem();
		
		// Spieloptionen
		if (optionen.contains(SpielOptionen.SIMPEL))
		{
			optionen.remove(SpielOptionen.FESTUNGEN);
			optionen.remove(SpielOptionen.KOMMANDOZENTRALEN);
			optionen.remove(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH);
		}
		else
		{
			optionen.add(SpielOptionen.FESTUNGEN);
			optionen.add(SpielOptionen.KOMMANDOZENTRALEN);
		}
		
		optionen.remove(SpielOptionen.AUTO_SAVE);
		optionen.remove(SpielOptionen.EMAIL);
		optionen.add(SpielOptionen.SERVER_BASIERT);
		
		if (maxJahreString.equals(MAX_JAHRE_UNENDLICH))
		{
			this.spiel.setMaxJahre(0);
			optionen.remove(SpielOptionen.KEIN_ENDLOSSPIEL);
		}
		else
		{
			this.spiel.setMaxJahre(Integer.parseInt(maxJahreString));
			this.optionen.add(SpielOptionen.KEIN_ENDLOSSPIEL);
		}
		
		this.spiel.setOptionen(optionen);
		
		// Preise, Spielzug-Codes, etc. vorbelegen
		this.spiel.jahrVorbereiten();
		
		// Spiel an den Server schicken
		RequestMessage msg = new RequestMessage(RequestMessageType.POST_NEW_GAME);
		
		Gson gson = new Gson();
		msg.payloadSerialized = gson.toJson(this.spiel); 
		
		ResponseMessage respMsg = this.sendAndReceive(this.cuc, msg);
		
		if (!respMsg.error)
		{
			ArrayList<Spieler> spielerEmail = new ArrayList<Spieler>();
			
			for (Spieler spieler: alleSpieler)
			{
				if (spieler.getName().equals(this.cuc.userId) ||
					spieler.istComputer())
					continue;
				
				spielerEmail.add(spieler);
			}
			
			if (spielerEmail.size() > 0)
			{
				DialogWindow.showInformation(
						this, 
						SternResources.ServerGamesSubmitAngelegt2(false, respMsg.payloadSerialized), 
						SternResources.ServerGamesSubmit(false));
				
				EmailCreatorJDialog dlg = new EmailCreatorJDialog(
						this, 
						spiel.getSpieler(),
						null,
						parent.emailSeparator,
						SternResources.EmailSubjectEingeladen(
								false, 
								this.cuc.userId, 
								this.spiel.getName()), 
						SternResources.EmailBodyEingeladen(
								false, 
								this.cuc.userId, 
								this.spiel.getName(), 
								this.cuc.url, 
								Integer.toString(this.cuc.port)));
				
				dlg.setVisible(true);
				
				if (dlg.launched)
				{
					parent.emailSeparator = dlg.separatorPreset;
					parent.setProperty(Stern.PROPERTY_EMAIL_SEPARATOR, parent.emailSeparator);
				}	
			}
			else
				DialogWindow.showInformation(
						this, 
						SternResources.ServerGamesSubmitAngelegt(false, respMsg.payloadSerialized), 
						SternResources.ServerGamesSubmit(false));
			
			// Spiel laden
			this.selectGame(this.spiel.getName());
		}
	}
	
	private Spieler[] getSpieler(int anzSp)
	{
		Spieler[] spielerArray = new Spieler[anzSp];
		for (int i = 0; i < spielerArray.length; i++)
			spielerArray[i] = new Spieler(
					(String)this.comboSpielerArray[i].getSelectedItem(),
					"", 
					this.canvasSpielerFarben[i].colIndex, 
					this.cbBot[i].isSelected(),
					false);
		
		return spielerArray;
	}
	
	private class PanelSpielSelector extends PanelDark implements 
			ActionListener,
			ListSelectionListener,
			MouseListener
	{
		private ButtonDark butOk;
		private ButtonDark butSpielLoeschen;
		private ButtonDark butSpielBeenden;
		
		private SpielfeldDisplay spielfeld;
		
		private JList<String> listGames;
		
		private Hashtable<String,SpielInfo> gamesDict;
		
		private SpielerPanel[] spielerPanels;
		
		private SpielInfo selectedGame;

		private LabelDark labSpielerUndPlaneten;
		private LabelDark labJahr;
		private LabelDark labStartDatum;
		private LabelDark labLetztesUpdate;
		private CheckBoxDark cbSimple;
		
		public PanelSpielSelector(
				ArrayList<SpielInfo> games, 
				boolean spielleiterAktionen,
				String currentGameId)
		{
			super();
			
			this.setLayout(new SpringLayout());
			
			PanelDark panShell = new PanelDark(new BorderLayout(10, 10));
			
			PanelDark panMain = new PanelDark(new BorderLayout(20, 10));
			
			this.gamesDict = new Hashtable<String,SpielInfo>();
			DefaultListModel<String> lm = new DefaultListModel<String>();
			
			int selectedIndex = -1;
			
			for (SpielInfo info: games)
			{
				lm.addElement(info.name);
				this.gamesDict.put(info.name, info);
				
				if (currentGameId != null && info.name.equals(currentGameId))
					selectedIndex = lm.size() - 1;
			}
						
			this.listGames = new JList<String>(lm);
			
			listGames.setBackground(new Color(30, 30, 30));
			listGames.setForeground(Color.white);
			listGames.setFont(font);
			listGames.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			listGames.setLayoutOrientation(JList.VERTICAL);
			listGames.addMouseListener(this);
			listGames.setVisibleRowCount(-1);
			
			JScrollPane sc = new JScrollPane(listGames);
			sc.setBorder(BorderFactory.createLineBorder(Color.white));
			
			sc.setPreferredSize(new Dimension(125, 200));

			panMain.add(sc, BorderLayout.WEST);
			
			// ----
			
			PanelDark panGrid = new PanelDark(new GridLayout(6, 2, 0, 0));
			
			this.spielerPanels = new SpielerPanel[Constants.ANZAHL_SPIELER_MAX];
			
			this.spielerPanels[0] = new SpielerPanel();
			panGrid.add(this.spielerPanels[0]);
			
			this.labSpielerUndPlaneten = new LabelDark("6 Spieler, 42 Planeten", font);
			panGrid.add(this.labSpielerUndPlaneten);
			
			this.spielerPanels[1] = new SpielerPanel();
			panGrid.add(this.spielerPanels[1]);
			
			this.labJahr = new LabelDark("Jahr 1 von 30", font);
			panGrid.add(this.labJahr);
			
			this.spielerPanels[2] = new SpielerPanel();
			panGrid.add(this.spielerPanels[2]);
			
			this.labStartDatum = new LabelDark("Begonnen am 24.04.2019 00:00", font);
			panGrid.add(this.labStartDatum);
			
			this.spielerPanels[3] = new SpielerPanel();
			panGrid.add(this.spielerPanels[3]);
			
			this.labLetztesUpdate = new LabelDark("Letzte Aktivität 24.04.2019", font);
			panGrid.add(this.labLetztesUpdate);
			
			this.spielerPanels[4] = new SpielerPanel();
			panGrid.add(this.spielerPanels[4]);
			
			this.cbSimple = new CheckBoxDark(
					SternResources.SpielparameterJDialogSimpelStern(false), 
					false, font);
			this.cbSimple.setEnabled(false);
			panGrid.add(this.cbSimple);
			
			this.spielerPanels[5] = new SpielerPanel();
			panGrid.add(this.spielerPanels[5]);
			
			panGrid.add(new LabelDark("", font));
			
			panMain.add(panGrid, BorderLayout.CENTER);
			
			// ----
			
			// Spielfeld
			this.spielfeld = new SpielfeldDisplay(true);
			panMain.add(this.spielfeld, BorderLayout.EAST);
			
			panShell.add(panMain, BorderLayout.CENTER);
			// ----
			PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
			
			if (spielleiterAktionen)
			{
				this.butSpielBeenden = new ButtonDark(this, SternResources.ServerGamesBeenden(false), font);
				panButtons.add(this.butSpielBeenden);
				
				this.butSpielLoeschen = new ButtonDark(this, SternResources.ServerGamesLoeschen(false), font);
				panButtons.add(this.butSpielLoeschen);
			}

			this.butOk = new ButtonDark(this, SternResources.ServerGamesLaden(false), font);
			panButtons.add(this.butOk);
			
			panShell.add(panButtons, BorderLayout.SOUTH);
			
			this.add(panShell);

			SpringUtilities.makeCompactGrid(this,
	                1, 1, //rows, cols
	                10, 10, //initialX, initialY
	                10, 10);//xPad, yPad
			
			this.setValues();
			listGames.addListSelectionListener(this);
			
			if (selectedIndex >= 0)
				listGames.setSelectedIndex(selectedIndex);
			else
				listGames.setSelectedIndex(0);
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (this.selectedGame != null)
			{
				if (e.getSource() == this.butOk)
				{
					selectGame(this.selectedGame.name);
				}
				else if (e.getSource() == this.butSpielLoeschen)
				{
					deleteGame(this.selectedGame.name);
				}
				else if (e.getSource() == this.butSpielBeenden)
				{
					finalizeGame(this.selectedGame.name);
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getSource() != this.listGames)
				return;
			
			this.selectedGame = this.gamesDict.get(listGames.getSelectedValue());
			
			this.setValues();

		}
		
		private void setValues()
		{
			for (int sp = 0; sp < Constants.ANZAHL_SPIELER_MAX; sp++)
			{
				boolean visible = this.selectedGame != null && 
						this.selectedGame.spieler.length -1 >= sp;
				this.spielerPanels[sp].setVisible(visible);
				
				if (!visible)
					continue;
				
				Spieler spieler = this.selectedGame.spieler[sp];
				
				this.spielerPanels[sp].cbBot.setSelected(spieler.istComputer());
				this.spielerPanels[sp].cbZugeingabeFertig.setSelected(
						this.selectedGame.zugeingabeBeendet.contains(spieler.getName()));
				this.spielerPanels[sp].tfName.setText(spieler.getName());
				this.spielerPanels[sp].tfName.setForeground(
						Colors.get(spieler.getColIndex()));
			}
			
			this.spielfeld.refresh(this.selectedGame == null ? null : this.selectedGame.planetenInfo);
			
			this.labJahr.setVisible(this.selectedGame != null);
			this.labSpielerUndPlaneten.setVisible(this.selectedGame != null);
			//this.labSpieler.setVisible(this.selectedGame != null);
			this.labStartDatum.setVisible(this.selectedGame != null);
			this.cbSimple.setVisible(this.selectedGame != null);
			
			if (this.selectedGame != null)
			{
				if (this.selectedGame.maxJahre == 0)
					this.labJahr.setText(
							SternResources.InventurJahr1(
									false, 
									Integer.toString(this.selectedGame.jahr + 1)));
				else
					this.labJahr.setText(
							SternResources.InventurJahr2(
									false,
									Integer.toString(this.selectedGame.jahr + 1),
									Integer.toString(this.selectedGame.maxJahre)));
				
				this.labSpielerUndPlaneten.setText(
						SternResources.ServerGamesSpielerPlaneten(
								false, 
								Integer.toString(this.selectedGame.spieler.length),
								Integer.toString(this.selectedGame.planetenInfo.size())));
				
				this.labLetztesUpdate.setText(
						SternResources.ServerGamesLetzteAktivitaet(
								false, 
								Utils.dateStringFormat(
										Utils.millisecondsToString(this.selectedGame.letztesUpdate))));
				
				this.labStartDatum.setText(
						SternResources.ServerGamesBegonnen(
								false, 
								Utils.dateStringFormat(
										Utils.millisecondsToString(this.selectedGame.startDatum))));
				
				this.cbSimple.setSelected(this.selectedGame.simpleStern);
			}
			
			this.spielfeld.repaint();
		}
		
		private class SpielerPanel extends PanelDark
		{
			public CheckBoxDark cbZugeingabeFertig;
			public TextFieldDark tfName;
			public CheckBoxDark cbBot;
			
			public SpielerPanel()
			{
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				
				this.cbZugeingabeFertig = new CheckBoxDark("", false, font);
				this.cbZugeingabeFertig.setEnabled(false);
				this.add(cbZugeingabeFertig);
				
				this.tfName = new TextFieldDark(font, 12);
				this.tfName.setEditable(false);
				this.add(this.tfName);
				
				this.cbBot = new CheckBoxDark(SternResources.SpielparameterJDialogBot(false), false, font);
				this.cbBot.setEnabled(false);
				this.add(this.cbBot);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void mouseClicked(MouseEvent evt) 
		{
			JList<String> list = (JList<String>)evt.getSource();
	        if (evt.getClickCount() == 2)
	        {
	            // Double-click detected
	            int index = list.locationToIndex(evt.getPoint());
	            
	            if (index >= 0)
	            {
	            	DefaultListModel<String> lm = (DefaultListModel<String>)list.getModel();
	            	
	            	this.selectedGame = this.gamesDict.get(lm.get(index));
	            	selectGame(this.selectedGame.name);
	            }
	        }
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
