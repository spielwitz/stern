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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class IntegerTextFieldDark extends JTextField  implements DocumentListener
{
	public boolean error = false;
	private boolean checkInput = false;
	private IIntegerTextFieldDarkCallback callback;
	
	public IntegerTextFieldDark(IIntegerTextFieldDarkCallback callback, Font f)
	{
		super();
		this.callback = callback;
		this.init(f);
		this.getDocument().addDocumentListener(this);
		this.checkInput();
	}
	
	private void init(Font f)
	{
		this.setFont(f);
		this.setBackground(Color.black);
		this.setForeground(Color.white);
		this.setCaretColor(Color.white);
	}
	
	@Override
	public void insertUpdate(DocumentEvent e)
	{
		this.checkInput();
	}

	@Override
	public void removeUpdate(DocumentEvent e) 
	{
		this.checkInput();
	}

	@Override
	public void changedUpdate(DocumentEvent e) 
	{
		this.checkInput();
	}
	
	private void checkInput()
	{
		if (checkInput)
		{
			try
			{
				Integer.parseInt(this.getText());
				this.error = false;
			}
			catch (Exception x)
			{
				this.error = true;
			}
			
			this.callback.setControlsEnabled();
		}
	}
	
	public void enableCheckInput()
	{
		this.checkInput = true;
		this.checkInput();
	}
}
