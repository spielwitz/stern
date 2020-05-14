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

package common;

public class ConsoleInput
{
	private String inputText;
	private int lastKeyCode;
	private String clientId;
	private String languageCode;
	
	public String getInputText() {
		return inputText;
	}
	public int getLastKeyCode() {
		return lastKeyCode;
	}
	public String getClientId() {
		return clientId;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public ConsoleInput(String inputText, int lastKeyCode, String clientId, String languageCode)
	{
		super();
		this.inputText = inputText;
		this.lastKeyCode = lastKeyCode;
		this.clientId = clientId; 
		this.languageCode = languageCode;
	}
}
