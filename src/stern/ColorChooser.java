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

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Constants;
import common.SternResources;
import commonUi.ButtonDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;

@SuppressWarnings("serial")
public class ColorChooser  extends JDialog implements ActionListener
{
	private ColorPanel[] colorPanels;
	private ButtonDark butOk;
	private ButtonDark butAbbruch;
	public boolean abort = false;
	public byte selectedColor;
	
	public ColorChooser(JDialog parent, byte currentColor, Font font)
	{
		super (parent, SternResources.SpielparameterJDialogFarbe(false), true);
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark(new BorderLayout(10,10));
		
		// --------
		PanelDark panMain = new PanelDark(new GridLayout(3,2,10,10));
		
		this.colorPanels = new ColorPanel[Constants.ANZAHL_SPIELER_MAX];
		
		for (int i = 0; i < Constants.ANZAHL_SPIELER_MAX; i++)
		{
			this.colorPanels[i] = new ColorPanel(this, (byte)(i+Colors.SPIELER_FARBEN_OFFSET));
			this.colorPanels[i].setPreferredSize(new Dimension(50, 50));
			panMain.add(this.colorPanels[i]);
		}
		// --------
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// ----
		
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		this.butAbbruch = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butAbbruch);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		// ---
		
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
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
		
		this.colorChanged(currentColor);
	}
	
	public void colorChanged(byte colorIndex)
	{
		this.selectedColor = colorIndex;
		
		for (int i = 0; i < this.colorPanels.length; i++)
			this.colorPanels[i].setSelected(i == colorIndex);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.getRootPane())
		{
			this.abort = true;
			this.close();
		}
		else if (e.getSource() == this.butAbbruch)
		{
			this.abort = true;
			this.close();
		}
		else if (e.getSource() == this.butOk)
		{
			this.abort = false;
			this.close();
		}
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	private class ColorPanel extends JPanel implements MouseListener
	{
		private static final int RAND = 5;
		
		private boolean selected;
		private byte colorIndex;
		private ColorChooser parent;
		
		public ColorPanel(ColorChooser parent, byte colorIndex)
		{
			super();
			this.parent = parent;
			this.colorIndex = colorIndex;
			this.addMouseListener(this);
		}
		
		public void setSelected(boolean selected)
		{
			this.selected = selected;
			this.repaint();
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = this.getSize();
			
			if (this.selected)
			{
				g.setColor(Color.white);
				g.fillRect(0, 0, dim.width, dim.height);
				
				g.setColor(Colors.get(this.colorIndex));
				g.fillRect(RAND, RAND, dim.width-2*RAND, dim.height-2*RAND);

				g.setColor(Color.BLACK);
				g.drawRect(RAND, RAND, dim.width-2*RAND, dim.height-2*RAND);
			}
			else
			{
				g.setColor(Colors.get(this.colorIndex));
				g.fillRect(0, 0, dim.width, dim.height);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			this.parent.colorChanged(this.colorIndex);
			
			if (e.getClickCount() == 2)
				this.parent.butOk.doClick();

		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

}
