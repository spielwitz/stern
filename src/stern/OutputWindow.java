/**	STERN, das Strategiespiel.
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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import common.ScreenDisplayContent;
import common.SternResources;
import commonUi.IHostComponentMethods;
import commonUi.PaintPanel;

@SuppressWarnings("serial")
class OutputWindow extends Frame
							implements 
							IHostComponentMethods,
							WindowListener
{
	private PaintPanel paintPanel;
	
	OutputWindow()
	{
		super();
		
		Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, dim.width/2, dim.height/2);
		
		this.setLayout(new BorderLayout());
		this.addWindowListener(this);
		
		this.paintPanel = new PaintPanel(this);
		this.add(this.paintPanel, BorderLayout.CENTER);
		
		this.setTitle(SternResources.SternTitel(false));
	}
	
	void redraw (ScreenDisplayContent cont)
	{
		this.paintPanel.redraw(cont, false, false);
	}

	@Override
	public void hostKeyPressed(KeyEvent arg0, String languageCode)
	{
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) 
	{
		this.setVisible(false);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}	
}
