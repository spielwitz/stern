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
import java.awt.Font;

import javax.swing.JPasswordField;

@SuppressWarnings("serial")
public class PasswordFieldDark extends JPasswordField
{
	public PasswordFieldDark(String text, Font f)
	{
		super(text);
		
		this.setBackground(new Color(30, 30, 30));
		this.setForeground(Color.white);
		this.setFont(f);
	}
}