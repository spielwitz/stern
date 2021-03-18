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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;

public class ScreenPainter
{	
	public static final int			SCREEN_WIDTH = 650;
	public static final int			SCREEN_HEIGHT = 480;
	
	private static final int		BOARD_OFFSET_X = 10;
	private static final int		BOARD_OFFSET_Y = 10;
	public static final int			BOARD_DX = 18;
	
	private static final double		LINE_SIZE = 0.5;
	private static final double		SHIP_SIZE = 0.75;
	private static final int		SHIP_SIZE_PIXEL_MIN = 2;
	
	private static final int 		PLANET_EDITOR_COLUMN1 = 5;
	private static final int 		PLANET_EDITOR_COLUMN2 = 46;
	private static final int 		PLANET_EDITOR_COLUMN3 = 64;
	
	private static final String 	CURSOR_CHARACTER = "_";
	
	private static final int 		PLANETLIST_OFFSET_X = 2 * BOARD_OFFSET_X + Constants.BOARD_MAX_X * BOARD_DX;
	private static final int 		PLANETLIST_OFFSET_Y = BOARD_OFFSET_Y;
	private static final int 		PLANETLIST_WIDTH = SCREEN_WIDTH - PLANETLIST_OFFSET_X - BOARD_OFFSET_X;
	private static final int 		PLANETLIST_HEIGHT = Constants.BOARD_MAX_Y * BOARD_DX;
	private static final int		PLANETLIST_AXIS_X =Utils.round((double)PLANETLIST_WIDTH / 2. + (double)PLANETLIST_OFFSET_X);
	private static final int		PLANETLIST_AXIS_DISTANCE = Utils.round((double)PLANETLIST_WIDTH / 3.);
	
	private static final int		CONSOLE_LINES_COUNT = Console.TEXT_LINES_COUNT_MAX + 3;
	private static final int		CONSOLE_LINE_HEIGHT = 16;
	private static final int		CONSOLE_HEIGHT = CONSOLE_LINES_COUNT * CONSOLE_LINE_HEIGHT;
	
	private static final int	    PROGRESS_BAR_BORDER = 2;
	
	private double factor;
	
	private Graphics2D dbGraphics;
	static ArrayList<String> titleLinesCount;
		
	private Font fontPlanets, fontMines, fontSectors;
	private FontMetrics fmPlanets, fmMines, fmSectors;
	
	private ScreenContent screenContent;
	private boolean inputEnabled;
	static {
		titleLinesCount = new ArrayList<String>();
		
		titleLinesCount.add("STERN            `     `    `    `      `    `    `    `  `   `             ");
		titleLinesCount.add(" `   `    `     `           `           `         `          jj.wwg@#@4k.  `");
		titleLinesCount.add("                  `    `         `           `         .,;!'` Qyxsj#@Qm     ");
		titleLinesCount.add("`     `    `   `                             `     .j!''`.` `..Q@$h$$@`     ");
		titleLinesCount.add("     `    `     ` `    `    `    `      `     `..!|:. jJ\"^s, `..7@$$Qk     ");
		titleLinesCount.add(" `                                           .z|`.``..b.jaoud,..j.7Qk   `   ");
		titleLinesCount.add("               `  `    `    `    `        ..T;::..``..pJ$Xoooa :!xgk   `    ");
		titleLinesCount.add("`    `    `      `                .jj,  .jl:``````````3yqSooo3.vzwt         ");
		titleLinesCount.add("      `                  `    ..g@@@@@ggl:''`'`''`'''`'.=&gwZvoa2`          ");
		titleLinesCount.add(" `         `   `  `    `    j2@@###$#$g@.:::'`'':'':'`:':::jxogP            ");
		titleLinesCount.add("     `    `     `       ` .@@@X$$$$@g8l:;''':''::''`'':::!uxq@l             ");
		titleLinesCount.add("`                `      j2@@$$qg#@gMu:::'::':':!''`':`::jxqpl            `  ");
		titleLinesCount.add("     `     `   `       .@@QgpP`  .k||!:::::':':::`:.::jxq2n  `          `   ");
		titleLinesCount.add(" `        `       `  .2@@@P`    jCv|,,,!::::::```:.jxzwH`      `   `        ");
		titleLinesCount.add("     `         `  ` .@@@l      Jn||i!,,!!:`:`.::j|agZF                      ");
		titleLinesCount.add("`                  .@R        jox||i,,::;:::!j2@#@E    `                    ");
		titleLinesCount.add("     `    `    ` ` = ..jjjj, jpvxxx\"xvvxxcsj@@$$pQI `                      ");
		titleLinesCount.add(" `         ` ``  j.qgpRsjuz|Yg.5paojaagpA^@@$XX#g@t   `                     ");
		titleLinesCount.add("     ` `       .q#Q8xzgpT     4.3PT='    .@$h#$@@t     `                 `  ");
		titleLinesCount.add("`          ` .@@@@sJo2l      .im       .@$$#$@@P         `                  ");
		titleLinesCount.add("     ` `   .@$@gE.nuF     ..5!jk     .@###p@@l   `     `            `       ");
		titleLinesCount.add(" `      ` .@@$qFjv2`   ..Zn;:j@   ..@$pg@PT       `           `         `   ");
		titleLinesCount.add("     `  .@$eKqk.iZ ..!n!|'ujQm  .@@@pPT                `                    ");
		titleLinesCount.add("`  `   .@@$Xgm.|;:!:|':jz@p@F .@MP`     `  ` `    `    `  `                 ");
		titleLinesCount.add("  `   .@@##Qm `:jjjwW$#pgpP                  `    `                         ");
		titleLinesCount.add("  `  .@@kh###D#VyyppogpP`   `           `              `  `                 ");
		titleLinesCount.add("`   .@$kh#opV#pppgpHT`      `    `      `                `         `        ");
		titleLinesCount.add("   .@$#k3f#gg#H='                `           `    `      (c) 1989-2021      ");
		titleLinesCount.add("   @@$@8PT'           `     `    `      `              Michael Schweitzer   ");
		titleLinesCount.add("`              `  `    `                     `    `        Build " + ReleaseGetter.getRelease() + "  ");

	}
	
	public ScreenPainter(
			ScreenContent screenContent, 
			boolean inputEnabled,
			Graphics2D dbGraphics, 
			Font fontPlanets, 
			Font fontMines, 
			Font fontSector,
			double factor)
	{
		this.screenContent = screenContent;
		this.inputEnabled = inputEnabled;
		this.dbGraphics = dbGraphics;
		this.fontPlanets = fontPlanets;
		this.fontMines = fontMines;
		this.fontSectors = fontSector;
		this.factor = factor;
		
		this.dbGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.setColor(
				this.screenContent != null && this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX ?
						Color.WHITE:
						Color.BLACK);
		this.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		this.fmPlanets = this.dbGraphics.getFontMetrics(this.fontPlanets);
					
		if (this.screenContent != null)
		{
			this.fmSectors = this.dbGraphics.getFontMetrics(this.fontSectors);
			this.fmMines = this.dbGraphics.getFontMetrics(this.fontMines);
			
			this.drawConsole();
			
			if (this.screenContent.getMode() == ScreenContent.MODE_BOARD)
			{
				this.drawBoard();
				this.drawPlanetList();
			}
			else if (this.screenContent.getMode() == ScreenContent.MODE_STATISTICS)
				this.drawStatistics();
			else if (this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX)
				this.drawBoard();
			else
				this.drawPlanetEditor();
			
			if (!this.inputEnabled)
				this.drawLockSymbol();
		}
		else
			this.drawTitle();
	}
	
	private void drawConsole()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentConsole screenContentConsole = this.screenContent.getConsole();
		if (screenContentConsole == null)
			return;

		this.setColor(new Color(50, 50, 50));

		int x0 = BOARD_OFFSET_X;
		int y0 = 2 * BOARD_OFFSET_Y + Constants.BOARD_MAX_Y * BOARD_DX;
		int w = SCREEN_WIDTH - 2 * BOARD_OFFSET_X;
		
		this.drawRect(x0, y0, w, CONSOLE_HEIGHT);
		this.drawRect(x0, y0, w, CONSOLE_LINE_HEIGHT);
		
		if (screenContentConsole.getProgressBarDay() >= 0)
		{
			int progressBarX = x0 + w/2;
			int progressBarY = y0 + PROGRESS_BAR_BORDER;
			int progressBarWidth = w/2 - PROGRESS_BAR_BORDER;
			int progressBarHeight = Math.max(1, CONSOLE_LINE_HEIGHT - 2 * PROGRESS_BAR_BORDER) + 1;
			
			int progressActual = 0;
			String text = "";
			
			if (screenContentConsole.getProgressBarDay() == 0)
			{
				progressActual = 0;
				text = SternResources.AuswertungEreignisJahresbeginn2(false);
			}
			else if (screenContentConsole.getProgressBarDay() == Constants.DAYS_OF_YEAR_COUNT)
			{
				progressActual = progressBarWidth;
				text = SternResources.AuswertungEreignisJahresende2(false);
			}
			else
			{
				progressActual = Utils.round(
						(double) progressBarWidth * (double)screenContentConsole.getProgressBarDay() / 
						(double)Constants.DAYS_OF_YEAR_COUNT);
				text = SternResources.AuswertungEreignisTag2(
						false,
						Integer.toString(screenContentConsole.getProgressBarDay()),
						Integer.toString(Constants.DAYS_OF_YEAR_COUNT));
			}
			
			this.drawRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
			this.fillRect(progressBarX, progressBarY, progressActual, progressBarHeight);
			
			this.setColor(Colors.get(screenContentConsole.getHeaderCol()));
			
			this.dbGraphics.setFont(this.fontSectors);
			
			int textWidth = fmSectors.stringWidth(text);
			int textHeight = this.fmSectors.getAscent() - this.fmSectors.getDescent();
			int textX = Utils.round(
					this.factor * (progressBarX + (progressBarWidth - textWidth) / 2));
			int textY = this.consoleGetY(0, y0, textHeight);
			this.dbGraphics.drawString(text, textX, textY);
		}
		
		this.dbGraphics.setFont(this.fontPlanets);
		int fontHeight = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		int charWidth = this.fmPlanets.charWidth('H');
		
		this.setColor(Colors.get(screenContentConsole.getHeaderCol()));
		
		int x = Utils.round(this.factor * (double)x0) + charWidth;
		int y = this.consoleGetY(0, y0, fontHeight);
		
		if (screenContentConsole.getHeaderText() != null)
			this.dbGraphics.drawString(SternResources.getString(screenContentConsole.getHeaderText()), x, y);
		
		for (int i = 0; i < Console.TEXT_LINES_COUNT_MAX; i++)
		{
			this.setColor(Colors.get(screenContentConsole.getLineColors()[Console.TEXT_LINES_COUNT_MAX-i-1]));
		
			y = this.consoleGetY(i+1, y0, fontHeight);
			
			if (screenContentConsole.isWaitingForInput() && (Console.TEXT_LINES_COUNT_MAX-i-1) == screenContentConsole.getOutputLine())
				this.dbGraphics.drawString(
						SternResources.getString(
						screenContentConsole.getTextLines()[Console.TEXT_LINES_COUNT_MAX-i-1]) + CURSOR_CHARACTER, x, y);
			else
				this.dbGraphics.drawString(
						SternResources.getString(
						screenContentConsole.getTextLines()[Console.TEXT_LINES_COUNT_MAX-i-1]), x, y);
		}
		
		if (screenContentConsole.getAllowedKeys() != null)
		{
			int column = 0;
			
			for (int counter = 0; counter < screenContentConsole.getAllowedKeys().size(); counter += 2)
			{
				ConsoleKey key1 = screenContentConsole.getAllowedKeys().get(counter);
				ConsoleKey key2 = counter+1 < screenContentConsole.getAllowedKeys().size() ? 
										screenContentConsole.getAllowedKeys().get(counter+1) :
										null;
				
				int maxKeyLength = ConsoleKey.getMaxKeyLength(key1, key2);
				int maxTextLength = ConsoleKey.getMaxTextLength(key1, key2);
				
				int xKey = this.consoleGetX(column, x0, charWidth);
				int xText = this.consoleGetX(column + maxKeyLength + 1, x0, charWidth);
				
				if (screenContentConsole.getAllowedKeys().size() == 1)
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				else
				{
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 1, charWidth, fontHeight);
					this.writeConsoleKey(key2, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				}
				
				column += (maxKeyLength + 1 + maxTextLength + 2);
			}
		}		
	}
	
	private void writeConsoleKey(ConsoleKey key, int xKey, int xText, int maxKeyLength, int y0, int line, int charWidth, int fontHeight)
	{
		if (key == null)
			return;
		
		this.setColor(Colors.get(Colors.NEUTRAL));
		
		this.dbGraphics.fillRect(
				xKey + (maxKeyLength - key.getKey().length()) * charWidth,
				Utils.round(this.factor * (double)(y0 + (CONSOLE_LINES_COUNT-1-line) * CONSOLE_LINE_HEIGHT)),
				key.getKey().length() * charWidth,
				Utils.round((double)CONSOLE_LINE_HEIGHT * this.factor));
		
		this.setColor(Color.black);
		this.dbGraphics.drawString(Utils.padString(key.getKey(), maxKeyLength), xKey, this.consoleGetY(CONSOLE_LINES_COUNT-1-line, y0, fontHeight));
		
		this.setColor(Colors.get(Colors.NEUTRAL));
		this.dbGraphics.drawString(
				key.getText(),
				xText,
				this.consoleGetY(CONSOLE_LINES_COUNT-1-line, y0, fontHeight));
	}
	
	private int consoleGetX(int column, int xOff, int charWidth)
	{
		return Utils.round(this.factor * (double)xOff) + charWidth * (column+1);
	}
	private int consoleGetY(int line, int yOff, int fontHeight)
	{
		return Utils.round(this.factor * (double)(yOff + (line + 0.5) * CONSOLE_LINE_HEIGHT) + (double)fontHeight/2.);
	}
	private void drawBoard()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentBoard screenContentBoard = this.screenContent.getBoard();
		if (screenContentBoard == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		
		for (int x = 0; x <= Constants.BOARD_MAX_X; x++)
			this.drawLine(
					BOARD_OFFSET_X + x * BOARD_DX, 
					BOARD_OFFSET_Y, 
					BOARD_OFFSET_X + x * BOARD_DX,
					BOARD_OFFSET_Y + Constants.BOARD_MAX_Y * BOARD_DX);
		
		for (int y = 0; y <= Constants.BOARD_MAX_Y; y++)
			this.drawLine(
					BOARD_OFFSET_X, 
					BOARD_OFFSET_Y + y * BOARD_DX, 
					BOARD_OFFSET_X + Constants.BOARD_MAX_X * BOARD_DX,
					BOARD_OFFSET_Y + y * BOARD_DX);
		
		this.drawBoardLines(screenContentBoard.getLines());
		
		for (int y = 0; y < Constants.BOARD_MAX_Y; y++)
		{
			for (int x = 0; x < Constants.BOARD_MAX_X; x++)
			{
				Point pt = new Point(x,y);
				this.drawTextCenteredBoard(
						pt, 
						Game.getSectorNameFromPositionStatic(pt),
						new Color(60, 60, 60),
						this.fontSectors,
						this.fmSectors);
			}
		}
		
		if (screenContentBoard.getPlanets() != null)
		{
			for (ScreenContentBoardPlanet screenContentBoardPlanet: screenContentBoard.getPlanets())
			{
				this.fillCircleBoard(screenContentBoardPlanet.getPosition(), 1.2, 
						this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX ?
								Color.white :
								Colors.getColorDarker2(Colors.get(screenContentBoardPlanet.getColorIndex())));
				
				if (this.screenContent.getMode() == ScreenContent.MODE_DISTANCE_MATRIX)
	                this.drawCircleBoard(screenContentBoardPlanet.getPosition(), 1.2, Colors.get(screenContentBoardPlanet.getColorIndex())); 
				
				this.drawTextCenteredBoard(screenContentBoardPlanet.getPosition(), screenContentBoardPlanet.getName(),Colors.get(screenContentBoardPlanet.getColorIndex()), this.fontPlanets, this.fmPlanets);
				
				this.drawPlanetFrames(screenContentBoardPlanet);
			}
		}
		
		this.drawBoardMines(screenContentBoard.getMines());
		
		this.drawBoardRadarCircles(screenContentBoard.getRadarCircle());
		
		this.drawPositionsMarked(screenContentBoard.getPositionsMarked());		
		
		this.drawBoardLinesWithShips(screenContentBoard.getLines());		
		
		this.drawBoardPositions(screenContentBoard.getPositions());
	}

	private void drawPlanetList()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentPlanets screenContentPlanets = this.screenContent.getPlanets();
		if (screenContentPlanets == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		
		this.drawRect(PLANETLIST_OFFSET_X, PLANETLIST_OFFSET_Y, PLANETLIST_WIDTH, PLANETLIST_HEIGHT);
		
		int counter = 0;
		int lineCounter = 0;
		byte lastColorIndex = -1;

		this.dbGraphics.setFont(this.fontPlanets);
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();

		for (String text: screenContentPlanets.getText())
		{
			int width = this.fmPlanets.stringWidth(text);
			
			byte colorIndex = screenContentPlanets.getTextColorIndices().get(counter);
			this.setColor(Colors.get(colorIndex));
			
			int line = lineCounter % Constants.BOARD_MAX_Y;
			int column = lineCounter / Constants.BOARD_MAX_Y;
			
			if (counter > 0 && line == Constants.BOARD_MAX_Y-1 && colorIndex != lastColorIndex)
			{
				lineCounter++;
				line = lineCounter % Constants.BOARD_MAX_Y;
				column = lineCounter / Constants.BOARD_MAX_Y;
			}
			
			int dx = (column-1) * PLANETLIST_AXIS_DISTANCE + PLANETLIST_AXIS_X;
			
			int x = Utils.round(this.factor * (double)dx - (double)width/2.);
			int y = Utils.round(this.factor * ((double)BOARD_OFFSET_Y + ((double)line+0.5) * (double)BOARD_DX) + (double)height/2.);
			
			this.dbGraphics.drawString(text, x, y);
			
			counter++;
			lineCounter++;
			
			lastColorIndex = colorIndex;
		}
	}
	
	private void drawPlanetEditor()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentPlanetEditor screenContentPlanetEditor = this.screenContent.getPlanetEditor();
		if (screenContentPlanetEditor == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		this.drawRect(
				BOARD_OFFSET_X,
				BOARD_OFFSET_Y,
				SCREEN_WIDTH - 2 * BOARD_OFFSET_X,
				Constants.BOARD_MAX_Y * BOARD_DX);
		
		this.drawTextLeftEditor(Utils.padString(screenContentPlanetEditor.getMoneySupply(),4), PLANET_EDITOR_COLUMN1, 1, Colors.get(screenContentPlanetEditor.getColorIndex()));
		this.drawTextLeftEditor(SternResources.PlEditEeEnergievorrat(false), PLANET_EDITOR_COLUMN1+5, 1, Colors.get(Colors.NEUTRAL));
		
		if (screenContentPlanetEditor.hasCommandRoom())
			this.drawTextLeftEditor(SternResources.Kommandozentrale(false), PLANET_EDITOR_COLUMN2+8, 1, Colors.get(screenContentPlanetEditor.getColorIndex()));
		
		this.drawTextLeftEditor(SternResources.PlEditKaufpreis(false), PLANET_EDITOR_COLUMN2+2, 2, Colors.get(Colors.NEUTRAL));
		this.drawTextLeftEditor(SternResources.PlEditVerkaufspreis(false), PLANET_EDITOR_COLUMN3, 2, Colors.get(Colors.NEUTRAL));
		
		this.drawPlanetEditorLine(ShipType.MONEY_PRODUCTION, screenContentPlanetEditor, SternResources.PlEditEprodPlus4(false), 3);
		this.drawPlanetEditorLine(ShipType.FIGHTER_PRODUCTION, screenContentPlanetEditor, SternResources.PlEditRaumerProd(false), 4);
		this.drawPlanetEditorLine(ShipType.DEFENCE_SHIELD, screenContentPlanetEditor, SternResources.PlEditFestungen(false), 5);
		this.drawPlanetEditorLine(ShipType.DEFENCE_SHIELD_REPAIR, screenContentPlanetEditor, 
				SternResources.PlEditFestungRaumer(false, Integer.toString(Constants.DEFENSE_SHIELD_REPAIR_FIGHTERS_COUNT)), 6);
		
		this.drawPlanetEditorLine(ShipType.SCOUT, screenContentPlanetEditor, SternResources.AufklaererPlural(false), 8);
		this.drawPlanetEditorLine(ShipType.TRANSPORT, screenContentPlanetEditor, SternResources.TransporterPlural(false), 9);
		this.drawPlanetEditorLine(ShipType.PATROL, screenContentPlanetEditor, SternResources.PatrouillePlural(false), 10);
		this.drawPlanetEditorLine(ShipType.MINESWEEPER, screenContentPlanetEditor, SternResources.MinenraeumerPlural(false), 11);
		
		this.drawPlanetEditorLine(ShipType.MINE50, screenContentPlanetEditor, SternResources.Mine50Plural(false), 13);
		this.drawPlanetEditorLine(ShipType.MINE100, screenContentPlanetEditor, SternResources.Mine100Plural(false), 14);
		this.drawPlanetEditorLine(ShipType.MINE250, screenContentPlanetEditor, SternResources.Mine250Plural(false), 15);
		this.drawPlanetEditorLine(ShipType.MINE500, screenContentPlanetEditor, SternResources.Mine500Plural(false), 16);
	}
	
	private void drawPlanetEditorLine(ShipType type, ScreenContentPlanetEditor screenContentPlanetEditor, String name, int line)
	{
		this.drawTextLeftEditor(Utils.padString(screenContentPlanetEditor.getCount().get(type),4), PLANET_EDITOR_COLUMN1, line, Colors.get(screenContentPlanetEditor.getColorIndex()));
		this.drawTextLeftEditor(name, PLANET_EDITOR_COLUMN1+5, line, 
				!screenContentPlanetEditor.isReadOnly() && screenContentPlanetEditor.getTypeHighlighted() == type ?
						Color.white :
						Colors.get(Colors.NEUTRAL));
		
		if (!screenContentPlanetEditor.isReadOnly() && screenContentPlanetEditor.getTypeHighlighted() == type)
			this.drawTextLeftEditor(">>>>", 0, line, Color.white);

		if (type == ShipType.FIGHTER_PRODUCTION)
			return;
		
		byte colorIndex = screenContentPlanetEditor.getColorIndex();
		if (screenContentPlanetEditor.getBuyImpossible().contains(type))
			colorIndex = Colors.NEUTRAL;
				
		this.drawTextLeftEditor(Utils.padString(Game.editorPricesBuy.get(type),2), PLANET_EDITOR_COLUMN2 + 4, line, Colors.get(colorIndex));
		this.drawTextLeftEditor(SternResources.PlEditEe(false), PLANET_EDITOR_COLUMN2+7, line, Colors.get(colorIndex));
		
		if (type == ShipType.MONEY_PRODUCTION || type == ShipType.DEFENCE_SHIELD_REPAIR)
			return;
		
		colorIndex = screenContentPlanetEditor.getColorIndex();
		if (screenContentPlanetEditor.getSellImpossible().contains(type))
			colorIndex = Colors.NEUTRAL;
		
		this.drawTextLeftEditor(Utils.padString(Game.editorPricesSell.get(type),2), PLANET_EDITOR_COLUMN3 + 4, line, Colors.get(colorIndex));
		this.drawTextLeftEditor(SternResources.PlEditEe(false), PLANET_EDITOR_COLUMN3+7, line, Colors.get(colorIndex));
	}
	
	private void drawTextLeftEditor(String text, int column, int line, Color color)
	{
		int charWidth = this.fmPlanets.charWidth('H');
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		this.dbGraphics.setColor(color);
		
		this.dbGraphics.drawString(
				text,
				Utils.round((double)charWidth + this.factor * (double)BOARD_OFFSET_X + (double)(column* charWidth)),
				Utils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)line+0.5) * (double)BOARD_DX)));
	}
		
	private void drawStatistics()
	{
		if (this.screenContent == null)
			return;
		
		ScreenContentStatistics screenContentStatistics = this.screenContent.getStatistics();
		if (screenContentStatistics == null)
			return;
		
		this.setColor(new Color(50, 50, 50));
		this.drawRect(BOARD_OFFSET_X, BOARD_OFFSET_Y, SCREEN_WIDTH - 2 * BOARD_OFFSET_X, Constants.BOARD_MAX_Y * BOARD_DX);
		
		String title = "";
		String yearString = Integer.toString(screenContentStatistics.getSelectedYearIndex() + 1);
		char mode = screenContentStatistics.getTitle().charAt(0);
		
		switch (mode)
		{
		case Constants.STATISTICS_MODE_SCORE:
			title = SternResources.StatistikTitelPunkte(
					false,
					yearString);
			break;
			
		case Constants.STATISTICS_MODE_FIGHTERS:
			title = SternResources.StatistikTitelRaumer(
					false,
					yearString);
			break;
			
		case Constants.STATISTICS_MODE_PLANETS:
			title = SternResources.StatistikTitelPlaneten(
					false,
					yearString);
			break;
			
		case Constants.STATISTICS_MODE_MONEY_PRODUCTION:
			title = SternResources.StatistikTitelEnergieproduktion(
					false,
					yearString);
			break;
		}

		
		this.drawTextLeftEditor(
				title,
				0,
				0,
				Color.white);
		
		int valuePerPlayer[] = screenContentStatistics.getValues()[screenContentStatistics.getSelectedYearIndex()];
		int playerListSequence[] = Utils.sortValues(valuePerPlayer, true);
		
		for (int i = 0; i < playerListSequence.length; i++)
		{
			int playerIndex = playerListSequence[i];
			Player player = screenContentStatistics.getPlayers()[playerIndex];
		
			this.drawTextLeftEditor(screenContentStatistics.getPlayers()[playerIndex].getName(), 0, 2 + i, Colors.get(player.getColorIndex()));
			
			this.drawTextLeftEditor(Utils.padString(Integer.toString(valuePerPlayer[playerIndex]), 7), 14, 2 + i, Colors.get(player.getColorIndex()));
		}
		
		this.drawTextLeftEditor(SternResources.StatistikSpielBegonnen(false), 0, 16, Color.white);
		
		this.drawTextLeftEditor(Utils.convertDateToString(screenContentStatistics.getDateStart()), 0, 17, Color.white);
		
		this.drawTextLeftEditor(
				SternResources.StatistikMax(
						false, 
						Integer.toString(screenContentStatistics.getValueMax()), 
						Integer.toString(screenContentStatistics.getValueMaxYear()+1)),
				25,
				0,
				Colors.get(screenContentStatistics.getPlayers()[screenContentStatistics.getMaxValuePlayerIndex()].getColorIndex()));
		
		this.drawTextLeftEditor(
				SternResources.StatistikMin(false,
						Integer.toString(screenContentStatistics.getValueMin()),
						Integer.toString(screenContentStatistics.getValueMinYear()+1)),
				50,
				0,
				Colors.get(screenContentStatistics.getPlayers()[screenContentStatistics.getMinValuePlayerIndex()].getColorIndex()));
		
		int charWidth = this.fmPlanets.charWidth('H');
		int height = this.fmPlanets.getAscent() - this.fmPlanets.getDescent();
		
		int columnGraphic = 25;
		int columnGraphic0 = 1;
		int columnGraphic1 = 17;
		
		int x0 = Utils.round((double)charWidth + this.factor * (double)BOARD_OFFSET_X + (double)(columnGraphic* charWidth));
		int y0 = Utils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)columnGraphic0+0.5) * (double)BOARD_DX));
		
		int frameY = Utils.round((double)(Constants.BOARD_MAX_Y * BOARD_DX) * this.factor);
		int y1 = Utils.round((double)height/2. + this.factor * ((double)BOARD_OFFSET_Y + ((double)columnGraphic1+0.5) * (double)BOARD_DX));
		
		int x1 = Utils.round((double)(SCREEN_WIDTH - BOARD_OFFSET_X) * this.factor + (frameY - y1));
		
		y1 -= 2 * height;
		
		this.setColor(Color.gray);
		this.dbGraphics.drawLine(x0, y0, x0, y1);
		this.dbGraphics.drawLine(x0, y1, x1, y1);
		this.dbGraphics.drawLine(x1, y0, x1, y1);
		
		int yMax = y0 + height;
		
		Graphics2D g2d = (Graphics2D) this.dbGraphics.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{12}, 0);
        g2d.setStroke(dashed);		
        g2d.drawLine(
        		x0,
        		this.getStatisticsY(screenContentStatistics.getValueMax(), screenContentStatistics.getValueMax(), yMax, y1),
        		x1, this.getStatisticsY(screenContentStatistics.getValueMax(), screenContentStatistics.getValueMax(), yMax, y1));
        
        g2d.drawLine(
        		x0,
        		this.getStatisticsY(screenContentStatistics.getValueMin(), screenContentStatistics.getValueMax(), yMax, y1),
        		x1, this.getStatisticsY(screenContentStatistics.getValueMin(), screenContentStatistics.getValueMax(), yMax, y1));
        
		g2d.dispose();
		
		double dx = (double)(x1 - x0) / (double)(screenContentStatistics.getYears() - 1);
		
		for (int year = 0; year < screenContentStatistics.getYears(); year++)
		{
			int xx0 = x0 + Utils.round((double)year * dx);
			int xx1 = x0 + Utils.round((double)(year+1) * dx);
			
			int x0champ = Math.max(x0, xx0 - Utils.round(dx / 2.));
			
			int champsCount = screenContentStatistics.getChampionsPerYear()[year].length;
			int dy = Utils.round((2 * height -1 ) / champsCount);
			
			for (int champ = 0; champ < champsCount; champ++)
			{
				int colChamp = screenContentStatistics.getChampionsPerYear()[year][champ];
				
				this.dbGraphics.setColor(
						Colors.get(screenContentStatistics.getPlayers()[colChamp].getColorIndex()));
				
				int y = y1+1 + (champ * dy);
				int h = (y1 + 2 * height - 1) - y;
								
				this.dbGraphics.fillRect(
						x0champ, 
						y, 
						(year < screenContentStatistics.getYears() - 1) ? xx1-xx0 : Utils.round(dx / 2.), 
						h);
			}
			
			if (year < screenContentStatistics.getYears() - 1)
			{
				for (int playerIndex = 0; playerIndex < playerListSequence.length; playerIndex++)
				{
					this.dbGraphics.setColor(Colors.get(screenContentStatistics.getPlayers()[playerIndex].getColorIndex()));
					
					this.dbGraphics.drawLine(
							xx0,
							this.getStatisticsY(screenContentStatistics.getValues()[year][playerIndex], screenContentStatistics.getValueMax(), yMax, y1),
							xx1,
							this.getStatisticsY(screenContentStatistics.getValues()[year+1][playerIndex], screenContentStatistics.getValueMax(), yMax, y1));
				}
			}
		}
		
		this.dbGraphics.setColor(Color.white);
		int xx0 = x0 + Utils.round((double)screenContentStatistics.getSelectedYearIndex() * dx);
		this.dbGraphics.drawLine(xx0, y0, xx0, y1 + 2 * height - 1);		
	}
	
	private int getStatisticsY(int value, int valueMax, int yMax, int y1)
	{
		double ratio = (double)value / (double)valueMax;
		return Utils.round((double)y1 - ratio * (double)(y1 - yMax));
	}
	
	private void drawRect(int x, int y, int width, int height)
	{
		this.dbGraphics.drawRect(
				   Utils.round((double)x * factor),
				   Utils.round((double)y * factor),
				   Utils.round((double)width * this.factor),
				   Utils.round((double)height * this.factor));
	}
	
	private void fillRect(int x, int y, int width, int height)
	{
		this.dbGraphics.fillRect(
				   Utils.round((double)x * factor),
				   Utils.round((double)y * factor),
				   Utils.round((double)width * this.factor),
				   Utils.round((double)height * this.factor));
	}
	
	private void drawLine(int x1, int y1, int x2, int y2)
	{
		int xx1 = Utils.round((double)x1 * factor);
		int xx2 = Utils.round((double)x2 * factor);
		int yy1 = Utils.round((double)y1 * factor);
		int yy2 = Utils.round((double)y2 * factor);
		
		this.dbGraphics.drawLine(xx1, yy1, xx2, yy2);
	}
	
	private void drawLine(double x1, double y1, double x2, double y2)
	{
		int xx1 = Utils.round(x1 * factor);
		int xx2 = Utils.round(x2 * factor);
		int yy1 = Utils.round(y1 * factor);
		int yy2 = Utils.round(y2 * factor);
		
		this.dbGraphics.drawLine(xx1, yy1, xx2, yy2);
	}
	
	private void drawCircleBoard(Point position, double radius, Color color)
	{
		this.setColor(color);
		
		double rr = radius * (double)BOARD_DX;
		
		double x = (double)(BOARD_OFFSET_X + position.getX() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		double y = (double)(BOARD_OFFSET_X + position.getY() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		
		this.dbGraphics.drawOval(
				Utils.round(this.factor * x),
				Utils.round(this.factor * y),
				Utils.round(this.factor * rr),
				Utils.round(this.factor * rr));
	}
	
	private void fillCircleBoard(Point position, double radius, Color color)
	{
		this.setColor(color);
		
		double rr = radius * (double)BOARD_DX;
		
		double x = (double)(BOARD_OFFSET_X + position.getX() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		double y = (double)(BOARD_OFFSET_X + position.getY() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		
		this.dbGraphics.fillOval(
				Utils.round(this.factor * x),
				Utils.round(this.factor * y),
				Utils.round(this.factor * rr),
				Utils.round(this.factor * rr));
		
	}
	
	
	private void fillDiamondBoard(Point position, Color color)
	{
		this.setColor(color);
		
		int[] x = new int[4];
		int[] y = new int[4];
		
		x[0] = Utils.round((double)((double)BOARD_OFFSET_X + position.getX() * (double)BOARD_DX) * this.factor);
		y[0] = Utils.round((double)((double)BOARD_OFFSET_Y + (position.getY() + 0.5) * (double)BOARD_DX) * this.factor);
		
		x[1] = Utils.round((double)((double)BOARD_OFFSET_X + (position.getX() + 0.5) * (double)BOARD_DX) * this.factor);
		y[1] = Utils.round((double)((double)BOARD_OFFSET_Y + position.getY() * (double)BOARD_DX) * this.factor);
		
		x[2] = Utils.round((double)((double)BOARD_OFFSET_X + (position.getX()+1.) * (double)BOARD_DX) * this.factor);
		y[2] = Utils.round((double)((double)BOARD_OFFSET_Y + (position.getY() + 0.5) * (double)BOARD_DX) * this.factor);
		
		x[3] = Utils.round((double)((double)BOARD_OFFSET_X + ((double)position.getX() + 0.5) * (double)BOARD_DX) * this.factor);
		y[3] = Utils.round((double)((double)BOARD_OFFSET_Y + ((double)position.getY()+1.) * (double)BOARD_DX) * this.factor);
		
		this.dbGraphics.fillPolygon(x, y, 4);
	}
	
	private void drawTextCenteredBoard(Point position, String text, Color color, Font font, FontMetrics fm)
	{
		this.dbGraphics.setFont(font);
		
		int width = fm.stringWidth(text);
		int height = fm.getAscent() - fm.getDescent();
		
		this.setColor(color);
		
		int x = Utils.round(this.factor * ((double)BOARD_OFFSET_X + (position.getX()+0.5) * (double)BOARD_DX) - (double)width/2.);
		int y = Utils.round(this.factor * ((double)BOARD_OFFSET_Y + (position.getY()+0.5) * (double)BOARD_DX) + (double)height/2.);
		
		this.dbGraphics.drawString(text, x, y);
	}
		
	private void setColor(Color color)
	{
		this.dbGraphics.setColor(color);
	}
	
	private void drawPlanetFrames (ScreenContentBoardPlanet screenContentBoardPlanet)
	{
		if (screenContentBoardPlanet.getFrameColors() == null || screenContentBoardPlanet.getFrameColors().size() == 0)
			return;

		for (int i = 0; i < screenContentBoardPlanet.getFrameColors().size(); i++)
		{
			int x = (int)((BOARD_OFFSET_X + screenContentBoardPlanet.getPosition().getX() * BOARD_DX) - (i+1)*2);
			int y = (int)((BOARD_OFFSET_Y + screenContentBoardPlanet.getPosition().getY() * BOARD_DX) - (i+1)*2);
			
			this.setColor(Colors.get(screenContentBoardPlanet.getFrameColors().get(i)));
			this.drawRect(
					x,
					y,
					BOARD_DX + 4 * (i+1),
					BOARD_DX + 4 * (i+1));
		}
	}
	
	private void drawPositionsMarked (ArrayList<Point> positions)
	{
		if (positions == null)
			return;
		
		for (Point position: positions)
		{
			this.drawCircleBoard(position, 0.8, Color.white);
			
			this.setColor(Color.white);
			
			double x1 = (double)BOARD_OFFSET_X + position.getX() * (double)BOARD_DX;
			double y1 = Utils.round((double)BOARD_OFFSET_Y + ((double)position.getY()+0.5) * (double)BOARD_DX);
			double x2 = (double)BOARD_OFFSET_X + (position.getX()+(double)1) * (double)BOARD_DX;
			
			this.drawLine(x1, y1, x2, y1);
			
			x1 = Utils.round((double)BOARD_OFFSET_X + ((double)position.getX()+0.5) * (double)BOARD_DX);
			y1 = (double)BOARD_OFFSET_Y + position.getY() * (double)BOARD_DX;
			double y2 = (double)BOARD_OFFSET_Y + (position.getY()+1) * (double)BOARD_DX;
			
			this.drawLine(x1, y1, x1, y2);
		}
	}
	
	private void drawBoardMines (ArrayList<ScreenContentBoardMine> screenContentBoardMine)
	{
		if (screenContentBoardMine == null || screenContentBoardMine.size() == 0)
			return;

		for (ScreenContentBoardMine mine: screenContentBoardMine)
		{
			Point position = new Point(mine.getPositionX(), mine.getPositionY()); 
			
			this.fillDiamondBoard(
					position,
					Color.darkGray);
		
			this.drawTextCenteredBoard(
					position,
					Integer.toString(mine.getStrength()),
					Colors.get(Colors.NEUTRAL), this.fontMines, this.fmMines);
		}
	}
	
	private void drawBoardLines(ArrayList<ScreenContentBoardLine> lines)
	{
		if (lines == null)
			return;
		
		for (ScreenContentBoardLine line: lines)
		{
			if (line.getPositionStart() == null || line.getPositionDestination() == null)
				continue;
			
			this.setColor(Colors.get(line.getColorIndex()));
			
			Point p1 = this.getScreenPosition(line.getPositionStart());
			Point p2 = this.getScreenPosition(line.getPositionDestination());
			
			this.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}
	
	private void drawBoardLinesWithShips(ArrayList<ScreenContentBoardLine> lines)
	{
		if (lines == null)
			return;
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		
		int size = Math.max(
				Utils.round(4 * SHIP_SIZE * this.factor),
				SHIP_SIZE_PIXEL_MIN); 
		
		for (ScreenContentBoardLine line: lines)
		{
			if (line.getPosition() == null)
			{
				continue;
			}
			
			double dx = (double)(line.getPositionDestination().getX() - line.getPositionStart().getX());
			double dy = (double)(line.getPositionDestination().getY() - line.getPositionStart().getY());
			
			double dist = line.getPositionStart().distance(line.getPositionDestination());
			
			double w = (dy >= 0) ? 
					Math.acos(dx/dist) : 
					2.*Math.PI - Math.acos(dx/dist);
			
			double xPos = this.factor * ((double)BOARD_OFFSET_X + ((double)line.getPosition().x + 0.5) * (double)BOARD_DX);
			double yPos = this.factor * ((double)BOARD_OFFSET_Y + ((double)line.getPosition().y + 0.5) * (double)BOARD_DX);
						
			double size2 = this.factor * (double)BOARD_DX * LINE_SIZE;
			
			xPoints[0] = Utils.round(xPos);
			yPoints[0] = Utils.round(yPos);
			
			xPoints[1] = Utils.round(Math.cos(w)*(xPos-1.*size2)-Math.sin(w)*(yPos+0.3*size2)-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
			yPoints[1] = Utils.round(Math.sin(w)*(xPos-1.*size2)+Math.cos(w)*(yPos+0.3*size2)-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
			
			xPoints[2] = Utils.round(Math.cos(w)*(xPos-0.8*size2)-Math.sin(w)*yPos-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
			yPoints[2] = Utils.round(Math.sin(w)*(xPos-0.8*size2)+Math.cos(w)*yPos-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
			
			xPoints[3] = Utils.round(Math.cos(w)*(xPos-1.*size2)-Math.sin(w)*(yPos-0.3*size2)-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
			yPoints[3] = Utils.round(Math.sin(w)*(xPos-1.*size2)+Math.cos(w)*(yPos-0.3*size2)-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
			
			this.setColor(Colors.get(line.getColorIndex()));
			this.dbGraphics.fillPolygon(xPoints, yPoints, 4);
			
			this.drawBoardShipSymbol(line.getPosition(), size, line.getColorIndex(), line.getSymbol());
		}
	}
	
	private void drawBoardPositions(ArrayList<ScreenContentBoardPosition> positions)
	{
		if (positions == null || positions.size() == 0)
			return;

		int size = Math.max(
				Utils.round(4 * SHIP_SIZE * this.factor),
				SHIP_SIZE_PIXEL_MIN);
		
		for (ScreenContentBoardPosition position: positions)
		{
			Point pt = position.getPosition();
			
			this.drawBoardShipSymbol(pt, size, position.getColorIndex(), position.getSymbol());
		}
	}
	
	private void drawBoardShipSymbol(Point position, int size, byte colorIndex, byte symbol)
	{
		this.setColor(Colors.get(colorIndex));
		
		switch (symbol)
		{
		case 1: // Fighter
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size);
			break;
		case 2: // Scout
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y),
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y));
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) - size);
			break;
		case 3: // Patrol
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y),
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y));
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) - size);
			break;
		case 4: // Transport
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size);
			break;
					
		case 5: // Minelayer
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y));
			
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y),
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) + size);
			
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y));
			
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y),
					getBoardPositionX(position.x),
					getBoardPositionY(position.y) - size);
			
			break;
		case 6: // Minesweeper
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) - size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) + size);
			this.dbGraphics.drawLine(
					getBoardPositionX(position.x) - size,
					getBoardPositionY(position.y) + size,
					getBoardPositionX(position.x) + size,
					getBoardPositionY(position.y) - size);

			break;
		}
	}
	
	private void drawBoardRadarCircles(ScreenContentBoardRadar screenContentBoardRadar)
	{
		if (screenContentBoardRadar == null)
			return;
				
		double rr = 2 * (double)Constants.PATROL_RADAR_RANGE * (double)BOARD_DX;
		
		double x = (double)(BOARD_OFFSET_X + screenContentBoardRadar.getPosition().getX() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		double y = (double)(BOARD_OFFSET_X + screenContentBoardRadar.getPosition().getY() * BOARD_DX) + ((double)BOARD_DX - rr) / 2.;
		
		this.setColor(Colors.get(screenContentBoardRadar.getColorIndex()));
		
		AlphaComposite compositeBefore = (AlphaComposite) this.dbGraphics.getComposite();
		float alpha = 0.3f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		this.dbGraphics.setComposite(composite);
		
		this.dbGraphics.fillOval(
				Utils.round(this.factor * x), 
				Utils.round(this.factor * y), 
				Utils.round(this.factor * rr), 
				Utils.round(this.factor * rr));
		
		this.dbGraphics.setComposite(compositeBefore);		
		
		this.dbGraphics.drawOval(
				Utils.round(this.factor * x), 
				Utils.round(this.factor * y), 
				Utils.round(this.factor * rr), 
				Utils.round(this.factor * rr));
	}

	private int getBoardPositionX(double ptX)
	{
		return  Utils.round(((double)BOARD_OFFSET_X + (ptX + 0.5) * (double)BOARD_DX) * this.factor);
	}
	
	private int getBoardPositionY(double ptY)
	{
		return  Utils.round(((double)BOARD_OFFSET_Y + (ptY + 0.5)* (double)BOARD_DX) * this.factor);
	}
	
	private void drawTitle()
	{
		this.dbGraphics.setFont(this.fontPlanets);
		this.dbGraphics.setColor(Colors.get((byte)8));
		
		int lineHeight = this.fmPlanets.getHeight();
		
		int maxLineWidth = 0;
		
		for (String line: titleLinesCount)
		{
			int lineWidth = this.fmPlanets.stringWidth(line);
			if (lineWidth > maxLineWidth)
				maxLineWidth = lineWidth;
		}
		
		int maxTotalHeight = titleLinesCount.size() * lineHeight;
		
		int x = Utils.round(((double)SCREEN_WIDTH * this.factor - (double)maxLineWidth) / 2.);
		int yOff = Utils.round(((double)(SCREEN_HEIGHT * this.factor) - (double)maxTotalHeight) / 2.) +
				this.fmPlanets.getAscent(); 
		
		for (int i = 0; i < titleLinesCount.size(); i++)
		{
			String line = titleLinesCount.get(i);
			this.dbGraphics.drawString(line, x, yOff + i * lineHeight);
		}		
	}
	
	private void drawLockSymbol()
	{
		this.dbGraphics.setFont(this.fontPlanets);
		
		String text = SternResources.EingabeGesperrt(false);
		
		int lineHeight = this.fmPlanets.getHeight();
		int lineWidth = this.fmPlanets.stringWidth(text);
		
		this.dbGraphics.setColor(Color.DARK_GRAY);
		
		int x0 = Utils.round(((double)SCREEN_WIDTH * this.factor - (double)lineWidth) / 2.);
		int y0 = Utils.round(((double)SCREEN_HEIGHT * this.factor - (double)lineHeight) / 2.);
		
		this.dbGraphics.fillRect(
				   x0 - lineHeight,
				   y0 - lineHeight,
				   lineWidth + 2 * lineHeight,
				   lineHeight + 2 * lineHeight);
		
		this.dbGraphics.setColor(Color.WHITE);
		
		this.dbGraphics.drawString(text, x0, y0 + fmPlanets.getAscent());
	}
	
	private Point getScreenPosition(Point boardPosition)
	{
		int x = Utils.round(BOARD_OFFSET_X + (0.5 + boardPosition.getX()) * (double)BOARD_DX); 
		int y = Utils.round(BOARD_OFFSET_Y + (0.5 + boardPosition.getY()) * (double)BOARD_DX);
		
		return new Point(x,y);
	}
}
