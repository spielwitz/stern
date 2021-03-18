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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial") 
class IntegerTextFieldDark extends JTextField  implements DocumentListener
{
	boolean error = false;
	private boolean checkInput = false;
	private IIntegerTextFieldDarkCallback callback;
	
	IntegerTextFieldDark(IIntegerTextFieldDarkCallback callback, Font f)
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
			if (this.getText().length() > 0)
			{
				this.error = this.getValue() <= 0;
			}
			
			this.callback.setControlsEnabled();
		}
	}
	
	public int getValue()
	{
		int retval = 0;
		
		try
		{
			retval = Integer.parseInt(this.getText());
		}
		catch (Exception x)
		{
			retval = 0;
		}
		
		return retval;
	}
	
	void enableCheckInput()
	{
		this.checkInput = true;
		this.checkInput();
	}
}
