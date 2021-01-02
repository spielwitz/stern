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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import common.Colors;

@SuppressWarnings("serial") class SpielerFarbenCanvas extends JButton implements ActionListener
{
	byte colIndex;
	private JDialog parent;
	private IColorChooserCallback callback;
	int spieler;
	private Font font;
	
	SpielerFarbenCanvas(JDialog parent, IColorChooserCallback callback, int spieler, byte colIndex, Font font)
	{
		super();
		this.colIndex = colIndex;
		this.parent = parent;
		this.spieler = spieler;
		this.font = font;
		this.callback = callback;
		
		this.setBackground(Colors.get(colIndex));
		
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ColorChooser dlg = new ColorChooser(this.parent, this.colIndex, this.font);
		dlg.setVisible(true);
		
		if (!dlg.abort)
		{
			byte oldColorIndex = this.colIndex;
			this.setColor(dlg.selectedColor);				
			this.callback.colorChanged(this.spieler, this.colIndex, oldColorIndex);
		}
	}
	
	public void setColor(byte colorIndex)
	{
		this.colIndex = colorIndex;
		this.setBackground(Colors.get(this.colIndex));
	}

}
