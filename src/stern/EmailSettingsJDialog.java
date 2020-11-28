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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Spieler;
import common.SternResources;
import common.Utils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") class EmailSettingsJDialog extends JDialog implements ActionListener, FocusListener
{
	private final static int COLUMNS_TEXT_FIELS = 40;
	private final static String EMAIL_SELECT_BUTTON_TEXT = ".";
	
	private ButtonDark butOk;
	private ButtonDark butCancel;
	String emailAdresseSpielleiter;
	ArrayList<Spieler> spieler;
	
	private ButtonDark butEmailSpielleiter;
	
	private CheckBoxDark[] cbEmailAktiv;
	private TextFieldDark[] tfEmailSpieler;
	private ButtonDark[] butEmailSpieler;
	
	private TextFieldDark tfEmailSpielleiter;
		
	private ArrayList<String> emailAdressen;
	
	private static Font font;
	
	public int dlgResult = JOptionPane.CANCEL_OPTION; // NO_UCD (unused code)
	
	@SuppressWarnings("unchecked") EmailSettingsJDialog(
			JDialog parent,
			String emailAdresseSpielleiter,
			ArrayList<Spieler> spieler,
			ArrayList<String> emailAdressen,
			boolean readOnly)
	{
		super (parent, SternResources.EmailSettingsJDialogTitel(false), true);
		
		// Font laden
		font = DialogFontHelper.getFont();

		this.emailAdressen = emailAdressen;
		this.emailAdresseSpielleiter = (String)Utils.klon(emailAdresseSpielleiter);
		this.spieler = (ArrayList<Spieler>)Utils.klon(spieler);

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
		PanelDark panSpielleiter = new GroupBoxDark(SternResources.Spielleiter(false), font);
		panSpielleiter.setLayout(new SpringLayout());
		
		panSpielleiter.add(new LabelDark(SternResources.EMailAdresse(false), font));
		
		this.tfEmailSpielleiter = new TextFieldDark(this.emailAdresseSpielleiter, font);
		this.tfEmailSpielleiter.setColumns(COLUMNS_TEXT_FIELS);
		this.tfEmailSpielleiter.setEditable(!readOnly);
		this.tfEmailSpielleiter.addFocusListener(this);
		
		panSpielleiter.add(this.tfEmailSpielleiter);
		
		this.butEmailSpielleiter = new ButtonDark(this, EMAIL_SELECT_BUTTON_TEXT, font);
		this.butEmailSpielleiter.setEnabled(!readOnly);
		panSpielleiter.add(this.butEmailSpielleiter);
		
		SpringUtilities.makeCompactGrid(panSpielleiter,
                1, 3, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
				
		panMain.add(panSpielleiter, BorderLayout.NORTH);
		
		PanelDark panSpieler = new GroupBoxDark(SternResources.Spieler(false), font);
		panSpieler.setLayout(new SpringLayout());
		
		this.butEmailSpieler = new ButtonDark[spieler.size()];
		this.cbEmailAktiv = new CheckBoxDark[spieler.size()];
		this.tfEmailSpieler = new TextFieldDark[spieler.size()];
		
		for (int spIndex = 0; spIndex < spieler.size(); spIndex++)
		{
			Spieler sp = this.spieler.get(spIndex);
			
			this.cbEmailAktiv[spIndex] = new CheckBoxDark(sp.getName(), sp.istEmail(), font);
			this.cbEmailAktiv[spIndex].setEnabled(!sp.istComputer() && !readOnly);
			this.cbEmailAktiv[spIndex].setForeground(Colors.get(sp.getColIndex()));
			panSpieler.add(this.cbEmailAktiv[spIndex]);
			
			this.tfEmailSpieler[spIndex] = new TextFieldDark(sp.getEmailAdresse(), font);
			this.tfEmailSpieler[spIndex].setColumns(COLUMNS_TEXT_FIELS);
			this.tfEmailSpieler[spIndex].setEditable(!sp.istComputer() && !readOnly);	
			this.tfEmailSpieler[spIndex].addFocusListener(this);
			panSpieler.add(this.tfEmailSpieler[spIndex]);
			
			this.butEmailSpieler[spIndex] = new ButtonDark(this, EMAIL_SELECT_BUTTON_TEXT, font);
			this.butEmailSpieler[spIndex].setEnabled(!sp.istComputer() && !readOnly);
			panSpieler.add(this.butEmailSpieler[spIndex]);
		}
		
		SpringUtilities.makeCompactGrid(panSpieler,
                spieler.size(), 3, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
				
		panMain.add(panSpieler, BorderLayout.CENTER);
				
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
		else if (source == this.butEmailSpielleiter)
		{
			EmailAdressenJDialog dlg = new EmailAdressenJDialog(this, emailAdressen);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
				this.tfEmailSpielleiter.setText(emailAdressen.get(dlg.selectedIndex));
		}
		else if (source == this.butOk)
		{
			for (int i = 0; i < this.butEmailSpieler.length; i++)
			{
				this.spieler.get(i).setEmail(this.cbEmailAktiv[i].isSelected());
				this.spieler.get(i).setEmailAdresse(this.tfEmailSpieler[i].getText().trim());
			}
			
			this.emailAdresseSpielleiter = this.tfEmailSpielleiter.getText().trim();
			
			boolean ok = SpielparameterJDialog.checkEmailSettings(this, this.emailAdresseSpielleiter, this.spieler)				;
			
			if (ok)
			{
				this.dlgResult = JOptionPane.OK_OPTION;
				this.close();
			}
		}
		else if (source.getClass() == ButtonDark.class)
		{
			int index = -1;
			
			for (int i = 0; i < this.butEmailSpieler.length; i++)
			{
				if (source == this.butEmailSpieler[i])
				{
					index = i;
					break;
				}
			}
			
			EmailAdressenJDialog dlg = new EmailAdressenJDialog(this, emailAdressen);
			dlg.setVisible(true);
			
			if (dlg.selectedIndex >= 0)
			{
				this.tfEmailSpieler[index].setText(emailAdressen.get(dlg.selectedIndex));
				this.cbEmailAktiv[index].setSelected(true);
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
	public void focusLost(FocusEvent e) {
		// Email-Adressen merken
		String a = this.tfEmailSpielleiter.getText().trim();
		
		if (a.length() > 0 && !this.emailAdressen.contains(a) && a.contains("@"))
			this.emailAdressen.add(a);
		
		for (int i = 0; i < this.tfEmailSpieler.length; i++)
		{
			a = this.tfEmailSpieler[i].getText().trim();
			if (a.length() > 0 && !this.emailAdressen.contains(a) && a.contains("@"))
				this.emailAdressen.add(a);
		}
		
		Collections.sort(this.emailAdressen);
	}
}
