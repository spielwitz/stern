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

package commonUi;

import java.awt.Component;

import javax.swing.JOptionPane;

import common.SternResources;

public class DialogWindow 
{
	public static DialogWindowResult showOkCancel(Component parent, Object text, String title)
	{
		String[] buttons = new String[] 
				{
						SternResources.OK(false),
						SternResources.Abbrechen(false)
				};
		
		int result = JOptionPane.showOptionDialog(
					parent, 
					text, 
					title,
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, buttons, buttons[0]);

		if (result == 0)
			return DialogWindowResult.OK;
		else
			return DialogWindowResult.CANCEL;
	}
	
	public static DialogWindowResult showYesNo(Component parent, Object text, String title)
	{
		String[] buttons = new String[] 
				{
						SternResources.Ja(false),
						SternResources.Nein(false)
				};
		
		int result = JOptionPane.showOptionDialog(
					parent, 
					text, 
					title,
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, buttons, buttons[0]);

		if (result == 0)
			return DialogWindowResult.YES;
		else
			return DialogWindowResult.NO;
	}
	
	public static DialogWindowResult showYesNoCancel(Component parent, Object text, String title)
	{
		String[] buttons = new String[] 
				{
						SternResources.Ja(false),
						SternResources.Nein(false),
						SternResources.Abbrechen(false)
				};
		
		int result = JOptionPane.showOptionDialog(
					parent, 
					text, 
					title,
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, buttons, buttons[0]);

		if (result == 0)
			return DialogWindowResult.YES;
		else if (result == 1)
			return DialogWindowResult.NO;
		else
			return DialogWindowResult.CANCEL;
	}

	
	public static void showError(Component parent, Object text, String title)
	{
		String[] buttons = new String[] 
				{
						SternResources.OK(false)
				};
		
		JOptionPane.showOptionDialog(
				parent, 
				text, 
				title,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null, buttons, buttons[0]);
	}
	
	public static void showInformation(Component parent, Object text, String title)
	{
		String[] buttons = new String[] 
				{
						SternResources.OK(false)
				};
		
		JOptionPane.showOptionDialog(
				parent, 
				text, 
				title,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null, buttons, buttons[0]);
	}
}
