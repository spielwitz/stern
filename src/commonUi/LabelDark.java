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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class LabelDark extends JLabel implements ActionListener
{
	private Timer timer;
	private boolean colorToggled;
	private ImageIcon iconOrig;
	
	public LabelDark(Font f)
	{
		super();
		
		this.setBackground(new Color(30, 30, 30));
		this.setForeground(Color.white);
		this.setFont(f);
	}
	public LabelDark(String text, int alignment, Font f)
	{
		super(text, alignment);
		
		this.setBackground(new Color(30, 30, 30));
		this.setForeground(Color.white);
		this.setFont(f);
	}
	public LabelDark(String text, Font f)
	{
		super(text);
		
		this.setBackground(new Color(30, 30, 30));
		this.setForeground(Color.white);
		this.setFont(f);
	}
	
	public LabelDark(ImageIcon icon, int size, boolean blink)
	{
		super();

		this.setPreferredSize(new Dimension(size, size));

		this.setBackground(new Color(30, 30, 30));
		this.setForeground(Color.white);
		this.setIcon(icon);
		
		this.iconOrig = icon;
		
		if (blink)
			this.startBlinking();
	}
	
	private void startBlinking()
	{
		if (this.timer == null)
		{
			this.timer = new Timer (1500, this);
			this.timer.start();
		}
		else if (!this.timer.isRunning())
			this.timer.restart();
	}
	
	private void resetColors()
	{
		this.setIcon(this.iconOrig);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (this.colorToggled)
			this.resetColors();
		else
		{
			this.setIcon(null);
		}
		
		this.colorToggled = !this.colorToggled;
	}
}
