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

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Console
{
	// Das Datenmodell der Text-Console
	private String[] text; // Text, der in der Konsole angezeigt wird. Jedes Element ist eine Zeile
	private byte[] textCol; // Textfarbenindex
	private ArrayList<ConsoleKey> keys; // Zeigt an, welche Keys gedrueckt werden koennen, z.B. [R]aumer
	private String headerText;
	private byte headerCol;
	private ConsoleModus mode;
	
	private int outputLine;
	
	private boolean background;
	
	private Spiel parent;
		
	public static final int MAX_LINES = 5;
	public static final char HIDDEN_CHAR = '-';
	
	public static final String KEY_YES = "1";
	
	public Console(Spiel spiel, boolean background)
	{
		// Zeile 0 ist immer die aktuelle Zeile
		this.text = new String[MAX_LINES];
		this.textCol = new byte[MAX_LINES];
		
		this.mode = ConsoleModus.TEXT_INPUT;
		
		this.background = background;
		this.parent = spiel;
		
		this.clear();
	}
	
	public boolean isBackground() {
		return background;
	}
	
	public void setBackground(boolean background)
	{
		this.background = background;
	}
	
	public void lineBreak()
	{
		byte currentCol = this.textCol[this.outputLine];
		
		if (this.outputLine > 0)
			this.outputLine--;
		else
		{
			for (int line = MAX_LINES - 1; line > 0; line--)
			{
				this.text[line] = this.text[line-1];
				this.textCol[line] = this.textCol[line-1];
			}
		}
		
		this.text[outputLine] = "";
		this.textCol[outputLine] = currentCol; 		
	}
	
	public void deleteLine()
	{
		this.text[outputLine] = "";
	}
		
	@SuppressWarnings("unchecked")
	public ConsoleInput waitForKeyPressed(
			ArrayList<ConsoleKey> allowedKeys, 
			boolean hidden)
	{
		this.keys = (ArrayList<ConsoleKey>)Utils.klon(allowedKeys);
		return this.keyInput(1, hidden, false, "");
	}
	
	public ConsoleInput waitForKeyPressedYesNo(
			boolean hidden)
	{
		this.keys = new ArrayList<ConsoleKey>();
		
		this.keys.add(new ConsoleKey(KEY_YES, SternResources.Ja(true)));
		this.keys.add(new ConsoleKey(SternResources.AndereTaste(true), SternResources.Nein(true)));
		
		return this.keyInput(1, hidden, false, "");
	}
	
	public ConsoleInput waitForTextEntered(
			int maxLength,
			ArrayList<ConsoleKey> allowedKeys,
			boolean hidden,
			boolean appendStandardKeys)
	{
		return this.waitForTextEntered(maxLength, allowedKeys, hidden, appendStandardKeys, "");
	}
	
	@SuppressWarnings("unchecked")
	public ConsoleInput waitForTextEntered(
			int maxLength,
			ArrayList<ConsoleKey> allowedKeys,
			boolean hidden,
			boolean appendStandardKeys,
			String presetText)
	{
		this.keys = (ArrayList<ConsoleKey>)Utils.klon(allowedKeys);
		
		if (appendStandardKeys)
			this.keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

		return this.keyInput(maxLength, hidden, false, presetText);
	}
	
	public void waitForTaste()
	{
		this.keys = new ArrayList<ConsoleKey>();
		this.keys.add(new ConsoleKey("TAB", SternResources.Weiter(true)));
		
		if (this.isBackground())
			this.keys.add(new ConsoleKey("ESC", SternResources.Abbrechen(true)));
		
		this.keyInput(1, false, true, "");
	}
	
	public void pause(int milliseconds)
	{
		this.keys = new ArrayList<ConsoleKey>();
		this.updateScreen(true);
		
		this.parent.pause(milliseconds);
	}
	
	public boolean waitForTasteReplay()
	{
		KeyEventExtended keyEvent = this.parent.waitForKeyInput(); 	
		
		return (keyEvent.keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE); // Return true, wenn ESC gedrueckt wurde
	}
	
	public void setModus(ConsoleModus mode)
	{
		this.mode = mode;
	}
	
	public void setLineColor(byte col)
	{
		this.textCol[this.outputLine] = col;
	}
	
	public void clear()
	{
		this.outputLine = MAX_LINES - 1;
		
		for (int line = 0; line < MAX_LINES; line++)
		{
			this.text[line] = "";
			this.textCol[line] = Colors.INDEX_WEISS;
		}
	}
	
	private ConsoleInput keyInput(int maxLength, boolean hidden, boolean noDisplay, String presetText)
	{
		if (!noDisplay)
			this.appendText(">" + presetText);
		
		StringBuilder inputText = (presetText.length() > 0) ?
						new StringBuilder(presetText) :
						new StringBuilder();
						
		this.updateScreen(noDisplay);
		
		int keyCode = 0;
		
		String clientId = null;
		String languageCode = null;
		
		do
		{
			KeyEventExtended keyEvent = this.parent.waitForKeyInput(); 			
			keyCode = keyEvent.keyEvent.getKeyCode();
			clientId = keyEvent.ClientId;
			languageCode = keyEvent.languageCode;
			
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE)
			{
				break;
			}
			else if (keyCode == KeyEvent.VK_TAB && maxLength == 1)
			{
				inputText.append('\t');
				break;
			}
			else if (this.mode == ConsoleModus.PLANETEN_EDITOR)
			{
				if (keyCode == KeyEvent.VK_UP || 
					keyCode == KeyEvent.VK_DOWN ||
					keyCode == KeyEvent.VK_LEFT ||
					keyCode == KeyEvent.VK_RIGHT
					)
					break;
				else
				{
					inputText.append(keyEvent.keyEvent.getKeyChar());
					break;
				}
			}
			else if (this.mode == ConsoleModus.STATISTIK)
			{
				if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
					break;
				else
				{
					inputText.append(keyEvent.keyEvent.getKeyChar());
					break;
				}
			}
			else if (this.mode == ConsoleModus.TEXT_INPUT && keyCode == KeyEvent.VK_BACK_SPACE)
			{
				if (inputText.length() > 0)
				{
					inputText.deleteCharAt(inputText.length()-1);
					this.text[this.outputLine] = this.text[this.outputLine].substring(0, this.text[this.outputLine].length()-1);
					
					this.updateScreen(noDisplay);
				}
			}
			else if (this.mode == ConsoleModus.TEXT_INPUT)
			{
				char c = keyEvent.keyEvent.getKeyChar();
				
				// Lasse nur Tasten zu, die zur Eingabe von Planeten,
				// Zahlenwerten und E-Mail-Adressen erforderlich sind
				
				// Erlaubte Tasten sind:
				// A-Za-z0-9.!#$%&'*+-/=?^_`{|}~ und die TAB-Taste
				if (
					(c >= 'A' && c <= 'Z') ||
					(c >= 'a' && c <= 'z') ||
					(c >= '0' && c <= '9') ||
					 c == '@' ||
					 c == '+' || 
					 c == '-' || 
					 c == '.' ||
					 c == '!' ||
					 c == '#' ||
					 c == '$' ||
					 c == '%' ||
					 c == '&' ||
					 c == '\'' ||
					 c == '*' ||
					 c == '/' ||
					 c == '=' ||
					 c == '?' ||
					 c == '^' ||
					 c == '_' ||
					 c == '`' ||
					 c == '{' ||
					 c == '}' ||
					 c == '|' ||
					 c == '~'
					 )
				{
					if (!noDisplay)
					{
						if (hidden)
							this.text[this.outputLine] = this.text[this.outputLine] + HIDDEN_CHAR;
						else
							this.text[this.outputLine] = this.text[this.outputLine] + c;
					}
					
					inputText.append(c);
					
					if (inputText.length() >= maxLength)
						break;
					else
						this.updateScreen(noDisplay);
				}
			}
		} while (true);

		// Ende der Eingabe
		if (this.mode == ConsoleModus.TEXT_INPUT)
			this.lineBreak();
		else if (this.mode == ConsoleModus.AUSWERTUNG)
		{
			this.lineBreak();
			this.setLineColor(Colors.INDEX_WEISS);
		}
		else
			this.clear();
		
		return new ConsoleInput(inputText.toString(), keyCode, clientId, languageCode);
	}
	
	public void appendText(String text)
	{
		this.text[this.outputLine] = this.text[this.outputLine] + text;
	}
	
	public void setHeaderText(String text, byte col)
	{
		this.headerText = text;
		this.headerCol = col;
	}
	
	private void updateScreen(boolean noCursor)
	{
		this.parent.updateConsoleDisplay(
				new ConsoleDisplayContent(
						this.text,
						this.textCol,
						this.keys,
						this.headerText,
						this.headerCol,
						this.outputLine,
						(!noCursor && this.mode != ConsoleModus.AUSWERTUNG)),
				this.isBackground());
	}
	
	public void outAbbruch()
	{
		this.appendText(SternResources.AktionAbgebrochen(true));
		this.lineBreak();
	}
	
	public void outUngueltigeEingabe()
	{
		this.appendText(SternResources.UngueltigeEingabe(true));
		this.lineBreak();
	}
		

	
	public enum ConsoleModus
	{
		TEXT_INPUT,
		PLANETEN_EDITOR,
		AUSWERTUNG,
		STATISTIK
	}
}

