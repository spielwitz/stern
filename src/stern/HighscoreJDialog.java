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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import common.SternResources;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;

@SuppressWarnings("serial") class HighscoreJDialog extends JDialog implements ActionListener
{
	private ButtonDark butOk;
	private static Font font;
	
	HighscoreJDialog(
			Frame owner,
			String title,
			boolean modal,
			ArrayList<HighscoreEntry> highscoreEntries)
			
	{
		super (owner, title, modal);
		
		font = DialogFontHelper.getFont();
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				setVisible(false);
				dispose();
			}
		}
		);
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark();
		panMain.setLayout(new SpringLayout());
		
		for (int row = 0; row < Stern.HIGHSCORE_ENTRIES_COUNT; row++)
		{
			panMain.add(new LabelDark(Integer.toString(row+1), SwingConstants.RIGHT, font));
					
			if (row < highscoreEntries.size())
			{
				panMain.add(new LabelDark(highscoreEntries.get(row).playerName, SwingConstants.LEFT, font));
				panMain.add(new LabelDark(Integer.toString(highscoreEntries.get(row).score), SwingConstants.RIGHT, font));
			}
			else
			{
				panMain.add(new LabelDark("-----", SwingConstants.LEFT, font));
				panMain.add(new LabelDark("---", SwingConstants.RIGHT, font));
			}
		}
		
		SpringUtilities.makeGrid(panMain,
                Stern.HIGHSCORE_ENTRIES_COUNT, 3, //rows, cols
                5, 5, //initialX, initialY
                15, 5);//xPad, yPad
		// ---------------
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panShell, BorderLayout.CENTER);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().setDefaultButton(this.butOk);
		
		this.pack();
		this.setLocationRelativeTo(owner);	
		this.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.setVisible(false);
		this.dispose();
	}
}
