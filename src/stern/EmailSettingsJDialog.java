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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Player;
import common.SternResources;
import common.Utils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindowResult;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") 
class EmailSettingsJDialog extends JDialog implements ActionListener, FocusListener
{
	private final static int COLUMNS_TEXT_FIELS = 40;
	private final static String EMAIL_SELECT_BUTTON_TEXT = ".";
	
	private ButtonDark butOk;
	private ButtonDark butCancel;
	String emailGameHost;
	ArrayList<Player> players;
	
	private ButtonDark butEmailGameHost;
	
	private CheckBoxDark[] cbEmailEnabled;
	private TextFieldDark[] tfEmailPlayer;
	private ButtonDark[] butEmailPlayer;
	
	private TextFieldDark tfEmailGameHost;
		
	private ArrayList<String> emailAdresses;
	
	private static Font font;
	
	public DialogWindowResult dlgResult = DialogWindowResult.CANCEL; // NO_UCD (unused code)
	
	@SuppressWarnings("unchecked") 
	EmailSettingsJDialog(
			JDialog parent,
			String emailGameHost,
			ArrayList<Player> players,
			ArrayList<String> emailAdresses,
			boolean readOnly)
	{
		super (parent, SternResources.EmailSettingsJDialogTitel(false), true);
		
		font = DialogFontHelper.getFont();

		this.emailAdresses = emailAdresses;
		this.emailGameHost = (String)Utils.klon(emailGameHost);
		this.players = (ArrayList<Player>)Utils.klon(players);

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
		PanelDark panGameHost = new GroupBoxDark(SternResources.Spielleiter(false), font);
		panGameHost.setLayout(new SpringLayout());
		
		panGameHost.add(new LabelDark(SternResources.EMailAdresse(false), font));
		
		this.tfEmailGameHost = new TextFieldDark(this.emailGameHost, font);
		this.tfEmailGameHost.setColumns(COLUMNS_TEXT_FIELS);
		this.tfEmailGameHost.setEditable(!readOnly);
		this.tfEmailGameHost.addFocusListener(this);
		
		panGameHost.add(this.tfEmailGameHost);
		
		this.butEmailGameHost = new ButtonDark(this, EMAIL_SELECT_BUTTON_TEXT, font);
		this.butEmailGameHost.setEnabled(!readOnly);
		panGameHost.add(this.butEmailGameHost);
		
		SpringUtilities.makeCompactGrid(panGameHost,
                1, 3, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
				
		panMain.add(panGameHost, BorderLayout.NORTH);
		
		PanelDark panPlayers = new GroupBoxDark(SternResources.Players(false), font);
		panPlayers.setLayout(new SpringLayout());
		
		this.butEmailPlayer = new ButtonDark[players.size()];
		this.cbEmailEnabled = new CheckBoxDark[players.size()];
		this.tfEmailPlayer = new TextFieldDark[players.size()];
		
		for (int playerIndex = 0; playerIndex < players.size(); playerIndex++)
		{
			Player player = this.players.get(playerIndex);
			
			this.cbEmailEnabled[playerIndex] = new CheckBoxDark(player.getName(), player.isEmailPlayer(), font);
			this.cbEmailEnabled[playerIndex].setEnabled(!player.isBot() && !readOnly);
			this.cbEmailEnabled[playerIndex].setForeground(Colors.get(player.getColorIndex()));
			panPlayers.add(this.cbEmailEnabled[playerIndex]);
			
			this.tfEmailPlayer[playerIndex] = new TextFieldDark(player.getEmail(), font);
			this.tfEmailPlayer[playerIndex].setColumns(COLUMNS_TEXT_FIELS);
			this.tfEmailPlayer[playerIndex].setEditable(!player.isBot() && !readOnly);	
			this.tfEmailPlayer[playerIndex].addFocusListener(this);
			panPlayers.add(this.tfEmailPlayer[playerIndex]);
			
			this.butEmailPlayer[playerIndex] = new ButtonDark(this, EMAIL_SELECT_BUTTON_TEXT, font);
			this.butEmailPlayer[playerIndex].setEnabled(!player.isBot() && !readOnly);
			panPlayers.add(this.butEmailPlayer[playerIndex]);
		}
		
		SpringUtilities.makeCompactGrid(panPlayers,
                players.size(), 3, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
				
		panMain.add(panPlayers, BorderLayout.CENTER);
				
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		this.butCancel = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butCancel);
		
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
		getRootPane().setDefaultButton(this.butCancel);
		
		this.pack();
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (source == this.butCancel || source == this.getRootPane())
			this.close();
		else if (source == this.butEmailGameHost)
		{
			EmailAddressesJDialog dlg = new EmailAddressesJDialog(this, emailAdresses);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
				this.tfEmailGameHost.setText(emailAdresses.get(dlg.selectedIndex));
		}
		else if (source == this.butOk)
		{
			for (int i = 0; i < this.butEmailPlayer.length; i++)
			{
				this.players.get(i).setEmailPlayer(this.cbEmailEnabled[i].isSelected());
				this.players.get(i).setEmail(this.tfEmailPlayer[i].getText().trim());
			}
			
			this.emailGameHost = this.tfEmailGameHost.getText().trim();
			
			boolean ok = GameParametersJDialog.checkEmailSettings(this, this.emailGameHost, this.players)				;
			
			if (ok)
			{
				this.dlgResult = DialogWindowResult.OK;
				this.close();
			}
		}
		else if (source.getClass() == ButtonDark.class)
		{
			int index = -1;
			
			for (int i = 0; i < this.butEmailPlayer.length; i++)
			{
				if (source == this.butEmailPlayer[i])
				{
					index = i;
					break;
				}
			}
			
			EmailAddressesJDialog dlg = new EmailAddressesJDialog(this, emailAdresses);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
			{
				this.tfEmailPlayer[index].setText(emailAdresses.get(dlg.selectedIndex));
				this.cbEmailEnabled[index].setSelected(true);
			}
		}
	}

	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) 
	{
		String a = this.tfEmailGameHost.getText().trim();
		
		if (a.length() > 0 && !this.emailAdresses.contains(a) && a.contains("@"))
			this.emailAdresses.add(a);
		
		for (int i = 0; i < this.tfEmailPlayer.length; i++)
		{
			a = this.tfEmailPlayer[i].getText().trim();
			if (a.length() > 0 && !this.emailAdresses.contains(a) && a.contains("@"))
				this.emailAdresses.add(a);
		}
		
		Collections.sort(this.emailAdresses);
	}
}
