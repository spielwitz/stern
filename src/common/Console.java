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

package common;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

class Console
{
	private String[] textLines;
	private byte[] lineColors;
	private ArrayList<ConsoleKey> allowedKeys;
	private String headerText;
	private byte headerCol;
	private ConsoleModus mode;
	private int progressBarDay;
	
	private int outputLine;
	
	private boolean background;
	
	private Game parent;
		
	static final int TEXT_LINES_COUNT_MAX = 5;
	private static final char HIDDEN_CHAR = '-';
	
	static final String KEY_YES = "1";
	
	Console(Game game, boolean background)
	{
		// Line 0 is current line
		this.textLines = new String[TEXT_LINES_COUNT_MAX];
		this.lineColors = new byte[TEXT_LINES_COUNT_MAX];
		
		this.mode = ConsoleModus.TEXT_INPUT;
		
		this.background = background;
		this.parent = game;
		this.progressBarDay = -1;
		
		this.clear();
	}
	
	public boolean isBackground() {
		return background;
	}
	
	public void setBackground(boolean background)
	{
		this.background = background;
	}
	
	void lineBreak()
	{
		byte currentCol = this.lineColors[this.outputLine];
		
		if (this.outputLine > 0)
			this.outputLine--;
		else
		{
			for (int line = TEXT_LINES_COUNT_MAX - 1; line > 0; line--)
			{
				this.textLines[line] = this.textLines[line-1];
				this.lineColors[line] = this.lineColors[line-1];
			}
		}
		
		this.textLines[outputLine] = "";
		this.lineColors[outputLine] = currentCol; 		
	}
	
	public void deleteLine()
	{
		this.textLines[outputLine] = "";
	}
	
	@SuppressWarnings("unchecked") 
	ConsoleInput waitForKeyPressed(
			ArrayList<ConsoleKey> allowedKeys, 
			boolean hidden)
	{
		this.allowedKeys = (ArrayList<ConsoleKey>)Utils.klon(allowedKeys);
		return this.keyInput(1, hidden, false, "");
	}
	
	ConsoleInput waitForKeyPressedYesNo(
			boolean hidden)
	{
		this.allowedKeys = new ArrayList<ConsoleKey>();
		
		this.allowedKeys.add(new ConsoleKey(KEY_YES, SternResources.Ja(true)));
		this.allowedKeys.add(new ConsoleKey(SternResources.AndereTaste(true), SternResources.Nein(true)));
		
		return this.keyInput(1, hidden, false, "");
	}
	
	ConsoleInput waitForTextEntered(
			int textLengthMax,
			ArrayList<ConsoleKey> allowedKeys,
			boolean hidden,
			boolean appendStandardKeys)
	{
		return this.waitForTextEntered(textLengthMax, allowedKeys, hidden, appendStandardKeys, "");
	}
	
	@SuppressWarnings("unchecked")
	private ConsoleInput waitForTextEntered(
			int textLengthMax,
			ArrayList<ConsoleKey> allowedKeys,
			boolean hidden,
			boolean appendStandardKeys,
			String presetText)
	{
		this.allowedKeys = (ArrayList<ConsoleKey>)Utils.klon(allowedKeys);
		
		if (appendStandardKeys)
			this.allowedKeys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

		return this.keyInput(textLengthMax, hidden, false, presetText);
	}
	
	void waitForKeyPressed()
	{
		this.allowedKeys = new ArrayList<ConsoleKey>();
		this.allowedKeys.add(new ConsoleKey("TAB", SternResources.Weiter(true)));
		
		if (this.isBackground())
			this.allowedKeys.add(new ConsoleKey("ESC", SternResources.Abbrechen(true)));
		
		this.keyInput(1, false, true, "");
	}
	
	public void pause(int milliseconds)
	{
		this.allowedKeys = new ArrayList<ConsoleKey>();
		this.updateScreen(true);
		
		this.parent.pause(milliseconds);
	}
	
	boolean waitForKeyPressedReplay()
	{
		KeyEventExtended keyEvent = this.parent.waitForKeyInput(); 	
		
		return (keyEvent.keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE);
	}
	
	public void setMode(ConsoleModus mode)
	{
		this.mode = mode;
	}
	
	public void setLineColor(byte col)
	{
		this.lineColors[this.outputLine] = col;
	}
	
	void clear()
	{
		this.outputLine = TEXT_LINES_COUNT_MAX - 1;
		
		for (int line = 0; line < TEXT_LINES_COUNT_MAX; line++)
		{
			this.textLines[line] = "";
			this.lineColors[line] = Colors.WHITE;
		}
	}
	
	private ConsoleInput keyInput(int textLengthMax, boolean hidden, boolean noDisplay, String presetText)
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
			else if (keyCode == KeyEvent.VK_TAB && textLengthMax == 1)
			{
				inputText.append('\t');
				break;
			}
			else if (this.mode == ConsoleModus.PLANET_EDITOR)
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
			else if (this.mode == ConsoleModus.STATISTICS)
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
					this.textLines[this.outputLine] = this.textLines[this.outputLine].substring(0, this.textLines[this.outputLine].length()-1);
					
					this.updateScreen(noDisplay);
				}
			}
			else if (this.mode == ConsoleModus.TEXT_INPUT)
			{
				char c = keyEvent.keyEvent.getKeyChar();
				
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
					c = Character.toUpperCase(c);
					
					if (!noDisplay)
					{
						if (hidden)
							this.textLines[this.outputLine] = this.textLines[this.outputLine] + HIDDEN_CHAR;
						else
							this.textLines[this.outputLine] = this.textLines[this.outputLine] + c;
					}
					
					inputText.append(c);
					
					if (inputText.length() >= textLengthMax)
						break;
					else
						this.updateScreen(noDisplay);
				}
			}
		} while (true);

		if (this.mode == ConsoleModus.TEXT_INPUT)
			this.lineBreak();
		else if (this.mode == ConsoleModus.EVALUATION)
		{
			this.lineBreak();
			this.setLineColor(Colors.WHITE);
		}
		else
			this.clear();
		
		return new ConsoleInput(inputText.toString(), keyCode, clientId, languageCode);
	}
	
	void appendText(String text)
	{
		this.textLines[this.outputLine] = this.textLines[this.outputLine] + text;
	}
	
	void setHeaderText(String text, byte col)
	{
		this.headerText = text;
		this.headerCol = col;
	}
	
	private void updateScreen(boolean noCursor)
	{
		this.parent.updateConsole(
				new ScreenContentConsole(
						this.textLines,
						this.lineColors,
						this.allowedKeys,
						this.headerText,
						this.headerCol,
						this.outputLine,
						(!noCursor && this.mode != ConsoleModus.EVALUATION),
						this.progressBarDay),
				this.isBackground());
	}
	
	void outAbort()
	{
		this.appendText(SternResources.AktionAbgebrochen(true));
		this.lineBreak();
	}
	
	void outInvalidInput()
	{
		this.appendText(SternResources.UngueltigeEingabe(true));
		this.lineBreak();
	}
	
	public void enableEvaluationProgressBar(boolean enabled)
	{
		if (enabled)
			this.progressBarDay = 0;
		else
			this.progressBarDay = -1;
	}
	
	public void setEvaluationProgressBarDay(int day)
	{
		this.progressBarDay = day;
	}
		
	enum ConsoleModus
	{
		TEXT_INPUT,
		PLANET_EDITOR,
		EVALUATION,
		STATISTICS
	}
}

