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

package commonUi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.SternResources;
import common.Utils;

@SuppressWarnings("serial")
public class SpracheJDialog extends JDialog implements ActionListener
{
	private ButtonDark butOk;
	private ButtonDark butCancel;
	private ComboBoxDark<String> comboSprachen;
	
	public boolean ok = false;
	public String languageCode;
	
	private Hashtable<String, String> sprachen;

	private static Font font;
	
	public SpracheJDialog(
			Frame parent,
			String languageCode) // z.B. de-DE
	{
		super (parent, SternResources.SpracheDialogTitle(false), true);
		
		/// Font laden
		font = DialogFontHelper.getFont();
		
		this.languageCode = languageCode;
		
		this.sprachen = new Hashtable<String, String>();
		
		// Nicht übersetzen!
		this.sprachen.put("Deutsch", "de-DE");
		this.sprachen.put("English", "en-US");
		
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
		PanelDark panSprache = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		panSprache.add(new LabelDark(SternResources.Sprache(false), font));
		
		String[] sprachenArray = this.sprachen.keySet().toArray(new String[this.sprachen.size()]);
		
		this.comboSprachen = new ComboBoxDark<String>(
				sprachenArray, 
				font);
		this.comboSprachen.setPrototypeDisplayValue("WWWWWWWWWWWWWWWWWWWWWWW");
		this.comboSprachen.setSelectedItem(Utils.getKeyFromValue(this.sprachen, languageCode));
		
		panSprache.add(this.comboSprachen);
		
		panMain.add(panSprache, BorderLayout.CENTER);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butCancel);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
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
		if (event.getSource() == this.getRootPane())
		{
			this.close();
			return;
		}
		
		Object source = event.getSource();
		
		if (source == this.butCancel)
		{
			this.close();
		}
		else if (source == this.butOk)
		{
			int dialogResult = JOptionPane.showConfirmDialog(this,
					SternResources.SpracheDialogFrage(false),
					SternResources.SpracheDialogTitle(false),
				    JOptionPane.OK_CANCEL_OPTION);
			
			if (dialogResult == JOptionPane.OK_OPTION)
			{
				String selectedKey = (String)this.comboSprachen.getSelectedItem();
				this.languageCode = this.sprachen.get(selectedKey);
				this.ok = true;
				this.close();
			}
		}
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
}
