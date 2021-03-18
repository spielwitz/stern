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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import common.Colors;

@SuppressWarnings("serial") class CanvasPlayerColors extends JButton implements ActionListener
{
	byte colorIndex;
	private JDialog parent;
	private IColorChooserCallback callback;
	int playerIndex;
	private Font font;
	
	CanvasPlayerColors(JDialog parent, IColorChooserCallback callback, int playerIndex, byte colorIndex, Font font)
	{
		super();
		this.colorIndex = colorIndex;
		this.parent = parent;
		this.playerIndex = playerIndex;
		this.font = font;
		this.callback = callback;
		
		this.setBackground(Colors.get(colorIndex));
		
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ColorChooser dlg = new ColorChooser(this.parent, this.colorIndex, this.font);
		dlg.setVisible(true);
		
		if (!dlg.abort)
		{
			byte oldColorIndex = this.colorIndex;
			this.setColor(dlg.selectedColor);				
			this.callback.colorChanged(this.playerIndex, this.colorIndex, oldColorIndex);
		}
	}
	
	public void setColor(byte colorIndex)
	{
		this.colorIndex = colorIndex;
		this.setBackground(Colors.get(this.colorIndex));
	}

}
