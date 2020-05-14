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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Spieler;
import common.SternResources;
import commonUi.ButtonDark;
import commonUi.ComboBoxDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;

@SuppressWarnings("serial")
public class EmailCreatorJDialog extends JDialog
			implements 	ActionListener
{
	private Hashtable<CheckBoxDark, String> checkbox2emailMap;
	
	private ButtonDark butLaunchEmailClient;
	private ButtonDark butCancel;
	
	private ComboBoxDark<String> comboSeparators;
	
	private String body;
	private String subject;
	
	public String separatorPreset;
	public boolean launched = false;
	
	private static final String[] separators = new String[] {";", ","};
	
	public EmailCreatorJDialog(
			JDialog parent,
			Spieler[] spieler,
			String emailSpielleiter,
			String separatorPreset,
			String subject,
			String body)
	{
		super (parent, SternResources.EmailErzeugen(false), true);
		
		this.Initialize(spieler, emailSpielleiter, separatorPreset, subject, body);

		this.setLocationRelativeTo(parent);	
	}
	
	public EmailCreatorJDialog(
			Frame parent,
			Spieler[] spieler,
			String emailSpielleiter,
			String separatorPreset,
			String subject,
			String body)
	{
		super (parent, SternResources.EmailErzeugen(false), true);
		
		this.Initialize(spieler, emailSpielleiter, separatorPreset, subject, body);

		this.setLocationRelativeTo(parent);	
	}
	
	private void Initialize(
			Spieler[] spieler,
			String emailSpielleiter,
			String separatorPreset,
			String subject,
			String body)
	{
		this.separatorPreset = separatorPreset == null ? separators[0] : separatorPreset;
		this.body = body;
		this.subject = subject;
		
		Font font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout(10, 10));
		
		PanelDark panDialogShellOuter = new PanelDark(new SpringLayout());
		
		PanelDark panDialogShell = new PanelDark(new BorderLayout(10, 10));
		
		// ------------------
				
		PanelDark panTable = new PanelDark(new SpringLayout());
		
		checkbox2emailMap = new Hashtable<CheckBoxDark, String>();
		
		int rows = 0;
		
		// Erst die Spieler hinzufuegen
		for (Spieler sp: spieler)
		{
			String email = sp.getEmailAdresse();
			
			CheckBoxDark cb = new CheckBoxDark(sp.getName(), false, font);
			cb.setForeground(Colors.get(sp.getColIndex()));
			cb.setEnabled(email.length() > 0);
			cb.addActionListener(this);
			panTable.add(cb);
			
			checkbox2emailMap.put(cb, email);
			
			LabelDark lab = new LabelDark(
					email.length() > 0 ?
							email :
							SternResources.EmailUnbekannt(false),
					font);
			
			panTable.add(lab);
			
			rows++;
		}
		
		if (emailSpielleiter != null && emailSpielleiter.length() > 0)
		{
			CheckBoxDark cb = new CheckBoxDark(SternResources.Spielleiter(false), false, font);
			cb.addActionListener(this);
			panTable.add(cb);
			
			checkbox2emailMap.put(cb, emailSpielleiter);
			
			LabelDark lab = new LabelDark(emailSpielleiter, font);
			panTable.add(lab);
			
			rows++;
		}
		
		SpringUtilities.makeCompactGrid(panTable,
                rows, 2, //rows, cols
                10, 10, //initialX, initialY
                20, 10);//xPad, yPad
		
		panDialogShell.add(panTable, BorderLayout.CENTER);
		
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		panButtons.add(new LabelDark("Adressen-Separator", font));
		this.comboSeparators = new ComboBoxDark<String>(separators, font);
		this.comboSeparators.setSelectedItem(this.separatorPreset);
		panButtons.add(this.comboSeparators);
		
		panButtons.add(new JSeparator());
		
		this.butCancel = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butCancel);
		
		this.butLaunchEmailClient = new ButtonDark(this, SternResources.EmailErzeugen(false), font);
		this.butLaunchEmailClient.setEnabled(false);
		panButtons.add(this.butLaunchEmailClient);
		
		panDialogShell.add(panButtons, BorderLayout.SOUTH);
		
		// ------------------
		
		panDialogShellOuter.add(panDialogShell);
		
		SpringUtilities.makeCompactGrid(panDialogShellOuter,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panDialogShellOuter, BorderLayout.CENTER);
		
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
		this.setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.butCancel || e.getSource() == this.getRootPane())
			this.close();
		else if (e.getSource() == this.butLaunchEmailClient)
		{
			HashSet<String> emails = new HashSet<String>();
			
			for (CheckBoxDark cb: this.checkbox2emailMap.keySet())
			{
				if (cb.isSelected())
					emails.add(this.checkbox2emailMap.get(cb));
			}
			
			this.separatorPreset = (String)this.comboSeparators.getSelectedItem();
			StringBuilder sbEmail = new StringBuilder();
			
			for (String email: emails)
			{
				if (sbEmail.length() > 0)
					sbEmail.append(this.separatorPreset);
				
				sbEmail.append(email);
			}
			
			EmailToolkit.launchEmailClient(
					sbEmail.toString(), 
					this.subject, 
					this.body, 
					null, 
					null);
			
			this.launched = true;
			this.close();
		}
		else if (e.getSource().getClass() == CheckBoxDark.class)
		{
			int checkBoxesSelected = 0;
			
			for (CheckBoxDark cb: this.checkbox2emailMap.keySet())
			{
				if (cb.isSelected())
					checkBoxesSelected++;
			}
			
			this.butLaunchEmailClient.setEnabled(checkBoxesSelected > 0);
		}
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}

}
