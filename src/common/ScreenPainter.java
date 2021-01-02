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
	public static final int			SCREEN_SIZE_W = 650;
	public static final int			SCREEN_SIZE_H = 480;
	
	private static final int		SPIELFELD_XOFF = 10;
	private static final int		SPIELFELD_YOFF = 10;
	static final int				SPIELFELD_DX = 18;
	
	private static final double		LINIE_OBJEKT_RADIUS = 0.25;
	private static final double		LINIE_OBJEKT_GROESSE = 0.5;
	private static final double		OBJEKT_GROESSE = 0.75;
	private static final int		OBJEKT_MIN_PIXEL = 2;
	
	private static final int 		sp1 = 5; // Objekttyp
	private static final int 		sp2 = 46; //Kaufpreis
	private static final int 		sp3 = 64; // Verkaufspreis

	
	private static final String 	CURSOR = "_";
	
	private static final int 		PLANETENLISTE_XOFF = 2 * SPIELFELD_XOFF + Constants.FELD_MAX_X * SPIELFELD_DX;
	private static final int 		PLANETENLISTE_YOFF = SPIELFELD_YOFF;
	private static final int 		PLANETENLISTE_W = SCREEN_SIZE_W - PLANETENLISTE_XOFF - SPIELFELD_XOFF;
	private static final int 		PLANETENLISTE_H = Constants.FELD_MAX_Y * SPIELFELD_DX;
	private static final int			PLANETENLISTE_ACHSE_X =Utils.round((double)PLANETENLISTE_W / 2. + (double)PLANETENLISTE_XOFF);
	private static final int			PLANETENLISTE_ACHSABSTAND = Utils.round((double)PLANETENLISTE_W / 3.);
	
	private static final int			CONSOLE_LINES = Console.MAX_LINES + 3; // + 1;
	private static final int			CONSOLE_ZEILENHOEHE = 16;
	private static final int			CONSOLE_HOEHE = CONSOLE_LINES * CONSOLE_ZEILENHOEHE;
	
	private static final int	    PROGRESS_BAR_RAND = 2;
	
	private double factor;
	
	private Graphics2D dbGraphics;
	static ArrayList<String> titelBildTextLines;
		
	private Font fontPlaneten, fontMinen, fontFelder;
	private FontMetrics fmPlaneten, fmMinen, fmFelder;
	
	private ScreenDisplayContent cont;
	private boolean inputEnabled;

	static {
		// ASCII-Art lesen
		titelBildTextLines = new ArrayList<String>();
		
		titelBildTextLines.add("STERN            `     `    `    `      `    `    `    `  `   `             ");
		titelBildTextLines.add(" `   `    `     `           `           `         `          jj.wwg@#@4k.  `");
		titelBildTextLines.add("                  `    `         `           `         .,;!'` Qyxsj#@Qm     ");
		titelBildTextLines.add("`     `    `   `                             `     .j!''`.` `..Q@$h$$@`     ");
		titelBildTextLines.add("     `    `     ` `    `    `    `      `     `..!|:. jJ\"^s, `..7@$$Qk     ");
		titelBildTextLines.add(" `                                           .z|`.``..b.jaoud,..j.7Qk   `   ");
		titelBildTextLines.add("               `  `    `    `    `        ..T;::..``..pJ$Xoooa :!xgk   `    ");
		titelBildTextLines.add("`    `    `      `                .jj,  .jl:``````````3yqSooo3.vzwt         ");
		titelBildTextLines.add("      `                  `    ..g@@@@@ggl:''`'`''`'''`'.=&gwZvoa2`          ");
		titelBildTextLines.add(" `         `   `  `    `    j2@@###$#$g@.:::'`'':'':'`:':::jxogP            ");
		titelBildTextLines.add("     `    `     `       ` .@@@X$$$$@g8l:;''':''::''`'':::!uxq@l             ");
		titelBildTextLines.add("`                `      j2@@$$qg#@gMu:::'::':':!''`':`::jxqpl            `  ");
		titelBildTextLines.add("     `     `   `       .@@QgpP`  .k||!:::::':':::`:.::jxq2n  `          `   ");
		titelBildTextLines.add(" `        `       `  .2@@@P`    jCv|,,,!::::::```:.jxzwH`      `   `        ");
		titelBildTextLines.add("     `         `  ` .@@@l      Jn||i!,,!!:`:`.::j|agZF                      ");
		titelBildTextLines.add("`                  .@R        jox||i,,::;:::!j2@#@E    `                    ");
		titelBildTextLines.add("     `    `    ` ` = ..jjjj, jpvxxx\"xvvxxcsj@@$$pQI `                      ");
		titelBildTextLines.add(" `         ` ``  j.qgpRsjuz|Yg.5paojaagpA^@@$XX#g@t   `                     ");
		titelBildTextLines.add("     ` `       .q#Q8xzgpT     4.3PT='    .@$h#$@@t     `                 `  ");
		titelBildTextLines.add("`          ` .@@@@sJo2l      .im       .@$$#$@@P         `                  ");
		titelBildTextLines.add("     ` `   .@$@gE.nuF     ..5!jk     .@###p@@l   `     `            `       ");
		titelBildTextLines.add(" `      ` .@@$qFjv2`   ..Zn;:j@   ..@$pg@PT       `           `         `   ");
		titelBildTextLines.add("     `  .@$eKqk.iZ ..!n!|'ujQm  .@@@pPT                `                    ");
		titelBildTextLines.add("`  `   .@@$Xgm.|;:!:|':jz@p@F .@MP`     `  ` `    `    `  `                 ");
		titelBildTextLines.add("  `   .@@##Qm `:jjjwW$#pgpP                  `    `                         ");
		titelBildTextLines.add("  `  .@@kh###D#VyyppogpP`   `           `              `  `                 ");
		titelBildTextLines.add("`   .@$kh#opV#pppgpHT`      `    `      `                `         `        ");
		titelBildTextLines.add("   .@$#k3f#gg#H='                `           `    `      (c) 1989-2021      ");
		titelBildTextLines.add("   @@$@8PT'           `     `    `      `              Michael Schweitzer   ");
		titelBildTextLines.add("`              `  `    `                     `    `        Build " + ReleaseGetter.getRelease() + "  ");

	}
	
	public ScreenPainter(
			ScreenDisplayContent cont, 
			boolean inputEnabled,
			Graphics2D dbGraphics, 
			Font fontPlaneten, 
			Font fontMinen, 
			Font fontFelder,
			double factor)
	{
		this.cont = cont;
		this.inputEnabled = inputEnabled;
		this.dbGraphics = dbGraphics;
		this.fontPlaneten = fontPlaneten;
		this.fontMinen = fontMinen;
		this.fontFelder = fontFelder;
		this.factor = factor;
		
		this.dbGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.setColor(
				this.cont != null && this.cont.getModus() == ScreenDisplayContent.MODUS_ENTFERUNGSTABELLE ?
						Color.WHITE:
						Color.BLACK);
		this.fillRect(0, 0, SCREEN_SIZE_W, SCREEN_SIZE_H);
		
		// Fonts
		this.fmPlaneten = this.dbGraphics.getFontMetrics(this.fontPlaneten);
					
		// Inhalte zeichnen
		if (this.cont != null)
		{
			this.fmFelder = this.dbGraphics.getFontMetrics(this.fontFelder);
			
			if (this.cont.getSpielfeld().getMinen() != null)
			{
				this.fmMinen = this.dbGraphics.getFontMetrics(this.fontMinen);
			}
			
			this.drawConsole();
			
			if (this.cont.getModus() == ScreenDisplayContent.MODUS_SPIELFELD)
			{
				this.drawSpielfeld();
				this.drawPlanetenliste();
			}
			else if (this.cont.getModus() == ScreenDisplayContent.MODUS_STATISTIK)
				this.drawStatistik();
			else if (this.cont.getModus() == ScreenDisplayContent.MODUS_ENTFERUNGSTABELLE)
				this.drawSpielfeld();
			else
				this.drawEditor();
			
			// Input disabled?
			if (!this.inputEnabled)
				this.drawLockSymbol();
		}
		else
			// Testbild zeigen
			this.drawTitelbild();
	}
	
	private void drawConsole()
	{
		if (this.cont == null)
			return;
		
		ConsoleDisplayContent cdc = this.cont.getCons();
		if (cdc == null)
			return;
		
		// Rahmen
		this.setColor(new Color(50, 50, 50));

		int x0 = SPIELFELD_XOFF;
		int y0 = 2 * SPIELFELD_YOFF + Constants.FELD_MAX_Y * SPIELFELD_DX;
		int w = SCREEN_SIZE_W - 2 * SPIELFELD_XOFF;
		
		this.drawRect(x0, y0, w, CONSOLE_HOEHE);
		this.drawRect(x0, y0, w, CONSOLE_ZEILENHOEHE);
		
		// --------------
		// Console-Inhalt
		// --------------
		if (cdc.getEvaluationProgressBar() != null)
		{
			ConsoleEvaluationProgressBarDisplayContent pb = cdc.getEvaluationProgressBar();
			
			int pbX = x0 + w/2;
			int pbY = y0 + PROGRESS_BAR_RAND;
			int pbW = w/2 - PROGRESS_BAR_RAND;
			int pbH = Math.max(1, CONSOLE_ZEILENHOEHE - 2 * PROGRESS_BAR_RAND) + 1;
			
			int wpb = 0;
			String text = "";
			
			if (pb.isJahresbeginn())
			{
				wpb = 0;
				text = SternResources.AuswertungEreignisJahresbeginn2(false);
			}
			else if (pb.isJahresenede())
			{
				wpb = pbW;
				text = SternResources.AuswertungEreignisJahresende2(false);
			}
			else
			{
				wpb = Utils.round(
						(double) pbW * (double)pb.getTag() / 
						(double)Constants.ANZ_TAGE_JAHR);
				text = SternResources.AuswertungEreignisTag2(
						false,
						Integer.toString(pb.getTag()),
						Integer.toString(Constants.ANZ_TAGE_JAHR));
			}
			
			this.drawRect(pbX, pbY, pbW, pbH);
			this.fillRect(pbX, pbY, wpb, pbH);
			
			this.setColor(Colors.get(cdc.getHeaderCol()));
			
			this.dbGraphics.setFont(this.fontFelder);
			
			int textWidth = fmFelder.stringWidth(text);
			int textHeight = this.fmFelder.getAscent() - this.fmFelder.getDescent();
			int textX = Utils.round(
					this.factor * (pbX + (pbW - textWidth) / 2));
			int textY = this.consoleGetY(0, y0, textHeight);
			this.dbGraphics.drawString(text, textX, textY);
		}
		
		// Header
		this.dbGraphics.setFont(this.fontPlaneten);
		int fontHeight = this.fmPlaneten.getAscent() - this.fmPlaneten.getDescent();
		int charWidth = this.fmPlaneten.charWidth('H');
		
		this.setColor(Colors.get(cdc.getHeaderCol()));
		
		int x = Utils.round(this.factor * (double)x0) + charWidth;
		int y = this.consoleGetY(0, y0, fontHeight);
		
		if (cdc.getHeaderText() != null)
			this.dbGraphics.drawString(SternResources.getString(cdc.getHeaderText()), x, y);
		
		// Textzeilen
		for (int i = 0; i < Console.MAX_LINES; i++)
		{
			this.setColor(Colors.get(cdc.getTextCol()[Console.MAX_LINES-i-1]));
		
			y = this.consoleGetY(i+1, y0, fontHeight);
			
			if (cdc.isWaitingForInput() && (Console.MAX_LINES-i-1) == cdc.getOutputLine())
				this.dbGraphics.drawString(
						SternResources.getString(
						cdc.getText()[Console.MAX_LINES-i-1]) + CURSOR, x, y);
			else
				this.dbGraphics.drawString(
						SternResources.getString(
						cdc.getText()[Console.MAX_LINES-i-1]), x, y);
		}
		
		// Keys-Anzeige (neu)
		if (cdc.getKeys() != null)
		{
			int spaltenCounter = 0;
			
			for (int counter = 0; counter < cdc.getKeys().size(); counter += 2)
			{
				ConsoleKey key1 = cdc.getKeys().get(counter);
				ConsoleKey key2 = counter+1 < cdc.getKeys().size() ? 
										cdc.getKeys().get(counter+1) :
										null;
				
				int maxKeyLength = ConsoleKey.getMaxKeyLength(key1, key2);
				int maxTextLength = ConsoleKey.getMaxTextLength(key1, key2);
				
				int xKey = this.consoleGetX(spaltenCounter, x0, charWidth);
				int xText = this.consoleGetX(spaltenCounter + maxKeyLength + 1, x0, charWidth);
				
				if (cdc.getKeys().size() == 1)
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				else
				{
					this.writeConsoleKey(key1, xKey, xText, maxKeyLength, y0, 1, charWidth, fontHeight);
					this.writeConsoleKey(key2, xKey, xText, maxKeyLength, y0, 0, charWidth, fontHeight);
				}
				
				spaltenCounter += (maxKeyLength + 1 + maxTextLength + 2);
			}
		}		
	}
	
	private void writeConsoleKey(ConsoleKey key, int xKey, int xText, int maxKeyLength, int y0, int zeile, int charWidth, int fontHeight)
	{
		if (key == null)
			return;
		
		this.setColor(Colors.get(Colors.INDEX_NEUTRAL));
		
		// Key mit einem Rechteck hinterlegen
		this.dbGraphics.fillRect(
				xKey + (maxKeyLength - key.getKey().length()) * charWidth,
				Utils.round(this.factor * (double)(y0 + (CONSOLE_LINES-1-zeile) * CONSOLE_ZEILENHOEHE)),
				key.getKey().length() * charWidth,
				Utils.round((double)CONSOLE_ZEILENHOEHE * this.factor));
		
		this.setColor(Color.black);
		this.dbGraphics.drawString(Utils.padString(key.getKey(), maxKeyLength), xKey, this.consoleGetY(CONSOLE_LINES-1-zeile, y0, fontHeight));
		
		this.setColor(Colors.get(Colors.INDEX_NEUTRAL));
		this.dbGraphics.drawString(
				key.getText(),
				xText,
				this.consoleGetY(CONSOLE_LINES-1-zeile, y0, fontHeight));
	}
	
	private int consoleGetX(int spalte, int xOff, int charWidth)
	{
		return Utils.round(this.factor * (double)xOff) + charWidth * (spalte+1);
	}
	private int consoleGetY(int zeile, int yOff, int fontHeight)
	{
		return Utils.round(this.factor * (double)(yOff + (zeile + 0.5) * CONSOLE_ZEILENHOEHE) + (double)fontHeight/2.);
	}
	private void drawSpielfeld()
	{
		if (this.cont == null)
			return;
		
		SpielfeldDisplayContent sdc = this.cont.getSpielfeld();
		if (sdc == null)
			return;
		
		// Gitter
		this.setColor(new Color(50, 50, 50));
		
		for (int senkrecht = 0; senkrecht <= Constants.FELD_MAX_X; senkrecht++)
			this.drawLine(
					SPIELFELD_XOFF + senkrecht * SPIELFELD_DX, 
					SPIELFELD_YOFF, 
					SPIELFELD_XOFF + senkrecht * SPIELFELD_DX,
					SPIELFELD_YOFF + Constants.FELD_MAX_Y * SPIELFELD_DX);
		
		for (int waagrecht = 0; waagrecht <= Constants.FELD_MAX_Y; waagrecht++)
			this.drawLine(
					SPIELFELD_XOFF, 
					SPIELFELD_YOFF + waagrecht * SPIELFELD_DX, 
					SPIELFELD_XOFF + Constants.FELD_MAX_X * SPIELFELD_DX,
					SPIELFELD_YOFF + waagrecht * SPIELFELD_DX);
		
		// Linien
		this.zeichneLinien(sdc.getLines());
		
		// Namen der Sektoren
		for (int y = 0; y < Constants.FELD_MAX_Y; y++)
		{
			for (int x = 0; x < Constants.FELD_MAX_X; x++)
			{
				Point pt = new Point(x,y);
				this.drawCTextSpielfeld(
						pt, 
						Spiel.getSectorNameFromPoint(pt),
						new Color(60, 60, 60),
						this.fontFelder,
						this.fmFelder);
			}
		}
		
		// Planeten
		if (sdc.getPlanets() != null)
		{
			for (SpielfeldPlanetDisplayContent pl: sdc.getPlanets())
			{
				this.fillCircleSpielfeld(pl.getPos(), 1.2, 
						this.cont.getModus() == ScreenDisplayContent.MODUS_ENTFERUNGSTABELLE ?
								Color.white :
								Colors.getColorDarker2(Colors.get(pl.getCol())));
				
				if (this.cont.getModus() == ScreenDisplayContent.MODUS_ENTFERUNGSTABELLE)
	                this.drawCircleSpielfeld(pl.getPos(), 1.2, Colors.get(pl.getCol())); 
				
				this.drawCTextSpielfeld(pl.getPos(), pl.getName(),Colors.get(pl.getCol()), this.fontPlaneten, this.fmPlaneten);
				
				this.zeichneRahmenUmPlanet(pl);
			}
		}
		
		// Minen
		this.zeichneMinenfelder(sdc.getMinen());
		
		// Radarbeobachtungskreis
		this.zeichneRadarkreis(sdc.getRadar());
		
		// Markierte Felder
		this.markiereFelder(sdc.getMarkedFields());		
		
		// Linien mit Objekten
		this.zeichneLinienObjekte(sdc.getLines());		
		
		// Punkte
		this.zeichnePunkte(sdc.getPoints());
	}

	private void drawPlanetenliste()
	{
		if (this.cont == null)
			return;
		
		PlanetenlisteDisplayContent pdc = this.cont.getPlaneten();
		if (pdc == null)
			return;
		
		// Rahmen
		this.setColor(new Color(50, 50, 50));
		
		this.drawRect(PLANETENLISTE_XOFF, PLANETENLISTE_YOFF, PLANETENLISTE_W, PLANETENLISTE_H);
		
		int counter = 0;
		int zeilenCounter = 0;
		byte lastColor = -1;

		this.dbGraphics.setFont(this.fontPlaneten);
		int height = this.fmPlaneten.getAscent() - this.fmPlaneten.getDescent();

		for (String text: pdc.getText())
		{
			int width = this.fmPlaneten.stringWidth(text);
			
			byte color = pdc.getTextCol().get(counter);
			this.setColor(Colors.get(color));
			
			int zeile = zeilenCounter % Constants.FELD_MAX_Y;
			int spalte = zeilenCounter / Constants.FELD_MAX_Y;
			
			if (counter > 0 && zeile == Constants.FELD_MAX_Y-1 && color != lastColor)
			{
				zeilenCounter++;
				zeile = zeilenCounter % Constants.FELD_MAX_Y;
				spalte = zeilenCounter / Constants.FELD_MAX_Y;
			}
			
			int dx = (spalte-1) * PLANETENLISTE_ACHSABSTAND + PLANETENLISTE_ACHSE_X;
			
			int x = Utils.round(this.factor * (double)dx - (double)width/2.);
			int y = Utils.round(this.factor * ((double)SPIELFELD_YOFF + ((double)zeile+0.5) * (double)SPIELFELD_DX) + (double)height/2.);
			
			this.dbGraphics.drawString(text, x, y);
			
			counter++;
			zeilenCounter++;
			
			lastColor = color;
		}
	}
	
	private void drawEditor()
	{
		if (this.cont == null)
			return;
		
		PlanetenEditorDisplayContent pedc = this.cont.getPlEdit();
		if (pedc == null)
			return;
		
		// Rahmen
		this.setColor(new Color(50, 50, 50));
		this.drawRect(
				SPIELFELD_XOFF,
				SPIELFELD_YOFF,
				SCREEN_SIZE_W - 2 * SPIELFELD_XOFF,
				Constants.FELD_MAX_Y * SPIELFELD_DX);
		
		// Text
		this.drawLTextEditor(Utils.padString(pedc.getEvorrat(),4), sp1, 1, Colors.get(pedc.getFarbeSpieler()));
		this.drawLTextEditor(SternResources.PlEditEeEnergievorrat(false), sp1+5, 1, Colors.get(Colors.INDEX_NEUTRAL));
		
		if (pedc.isKommandozentrale())
			this.drawLTextEditor(SternResources.Kommandozentrale(false), sp2+8, 1, Colors.get(pedc.getFarbeSpieler()));
		
		this.drawLTextEditor(SternResources.PlEditKaufpreis(false), sp2+2, 2, Colors.get(Colors.INDEX_NEUTRAL));
		this.drawLTextEditor(SternResources.PlEditVerkaufspreis(false), sp3, 2, Colors.get(Colors.INDEX_NEUTRAL));
		
		this.editorZeile(ObjektTyp.EPROD, pedc, SternResources.PlEditEprodPlus4(false), 3);
		this.editorZeile(ObjektTyp.ERAUM, pedc, SternResources.PlEditRaumerProd(false), 4);
		this.editorZeile(ObjektTyp.FESTUNG, pedc, SternResources.PlEditFestungen(false), 5);
		this.editorZeile(ObjektTyp.FESTUNG_REPARATUR, pedc, 
				SternResources.PlEditFestungRaumer(false, Integer.toString(Constants.FESTUNG_REPARATUR_ANZ_RAUMER)), 6);
		
		this.editorZeile(ObjektTyp.AUFKLAERER, pedc, SternResources.AufklaererPlural(false), 8);
		this.editorZeile(ObjektTyp.TRANSPORTER, pedc, SternResources.TransporterPlural(false), 9);
		this.editorZeile(ObjektTyp.PATROUILLE, pedc, SternResources.PatrouillePlural(false), 10);
		this.editorZeile(ObjektTyp.MINENRAEUMER, pedc, SternResources.MinenraeumerPlural(false), 11);
		
		this.editorZeile(ObjektTyp.MINE50, pedc, SternResources.Mine50Plural(false), 13);
		this.editorZeile(ObjektTyp.MINE100, pedc, SternResources.Mine100Plural(false), 14);
		this.editorZeile(ObjektTyp.MINE250, pedc, SternResources.Mine250Plural(false), 15);
		this.editorZeile(ObjektTyp.MINE500, pedc, SternResources.Mine500Plural(false), 16);
	}
	
	private void editorZeile(ObjektTyp typ, PlanetenEditorDisplayContent pedc, String name, int zeile)
	{
		this.drawLTextEditor(Utils.padString(pedc.getAnzahl().get(typ),4), sp1, zeile, Colors.get(pedc.getFarbeSpieler()));
		this.drawLTextEditor(name, sp1+5, zeile, 
				!pedc.isReadOnly() && pedc.getTypMarkiert() == typ ?
						Color.white :
						Colors.get(Colors.INDEX_NEUTRAL));
		
		// Zeilen-Marker
		if (!pedc.isReadOnly() && pedc.getTypMarkiert() == typ)
			this.drawLTextEditor(">>>>", 0, zeile, Color.white);

		if (typ == ObjektTyp.ERAUM)
			return;
		
		// Kaufpreis
		byte farbe = pedc.getFarbeSpieler();
		if (pedc.getKaufNichtMoeglich().contains(typ))
			farbe = Colors.INDEX_NEUTRAL;
				
		this.drawLTextEditor(Utils.padString(Spiel.editorPreiseKauf.get(typ),2), sp2 + 4, zeile, Colors.get(farbe));
		this.drawLTextEditor(SternResources.PlEditEe(false), sp2+7, zeile, Colors.get(farbe));
		
		// Verkaufspreis
		if (typ == ObjektTyp.EPROD || typ == ObjektTyp.FESTUNG_REPARATUR)
			return;
		
		farbe = pedc.getFarbeSpieler();
		if (pedc.getVerkaufNichtMoeglich().contains(typ))
			farbe = Colors.INDEX_NEUTRAL;
		
		this.drawLTextEditor(Utils.padString(Spiel.editorPreiseVerkauf.get(typ),2), sp3 + 4, zeile, Colors.get(farbe));
		this.drawLTextEditor(SternResources.PlEditEe(false), sp3+7, zeile, Colors.get(farbe));
	}
	
	private void drawLTextEditor(String text, int spalte, int zeile, Color col)
	{
		int charWidth = this.fmPlaneten.charWidth('H');
		int height = this.fmPlaneten.getAscent() - this.fmPlaneten.getDescent();
		
		this.dbGraphics.setColor(col);
		
		this.dbGraphics.drawString(
				text,
				Utils.round((double)charWidth + this.factor * (double)SPIELFELD_XOFF + (double)(spalte* charWidth)),
				Utils.round((double)height/2. + this.factor * ((double)SPIELFELD_YOFF + ((double)zeile+0.5) * (double)SPIELFELD_DX)));
	}
		
	private void drawStatistik()
	{
		if (this.cont == null)
			return;
		
		StatistikDisplayContent sdc = this.cont.getStatistik();
		if (sdc == null)
			return;
		
		// Rahmen
		this.setColor(new Color(50, 50, 50));
		this.drawRect(SPIELFELD_XOFF, SPIELFELD_YOFF, SCREEN_SIZE_W - 2 * SPIELFELD_XOFF, Constants.FELD_MAX_Y * SPIELFELD_DX);
		
		String titel = "";
		String jahrString = Integer.toString(sdc.getMarkiertesJahrIndex() + 1);
		char m = sdc.getTitel().charAt(0);
		
		switch (m)
		{
		case Constants.STATISTIK_MODUS_PUNKTE: // Punkte
			titel = SternResources.StatistikTitelPunkte(
					false,
					jahrString);
			break;
			
		case Constants.STATISTIK_MODUS_RAUMER: // Raumer
			titel = SternResources.StatistikTitelRaumer(
					false,
					jahrString);
			break;
			
		case Constants.STATISTIK_MODUS_PLANETEN: // Planeten
			titel = SternResources.StatistikTitelPlaneten(
					false,
					jahrString);
			break;
			
		case Constants.STATISTIK_MODUS_PRODUKTION: // Produktion
			titel = SternResources.StatistikTitelEnergieproduktion(
					false,
					jahrString);
			break;
		}

		
		this.drawLTextEditor(
				titel,
				0,
				0,
				Color.white);
		
		int wertSpielerJahr[] = sdc.getWerte()[sdc.getMarkiertesJahrIndex()];
		int seq[] = Utils.listeSortieren(wertSpielerJahr, true);
		
		// Detailansicht
		for (int i = 0; i < seq.length; i++)
		{
			int sp = seq[i];
			Spieler spieler = sdc.getSpieler()[sp];
		
			this.drawLTextEditor(sdc.getSpieler()[sp].getName(), 0, 2 + i, Colors.get(spieler.getColIndex()));
			
			this.drawLTextEditor(Utils.padString(Integer.toString(wertSpielerJahr[sp]), 7), 14, 2 + i, Colors.get(spieler.getColIndex()));
		}
		
		// Spieldaten
		this.drawLTextEditor(SternResources.StatistikSpielBegonnen(false), 0, 16, Color.white);
		
		this.drawLTextEditor(Utils.dateToString(sdc.getStartDatum()), 0, 17, Color.white);
		
		// Min und Max
		this.drawLTextEditor(
				SternResources.StatistikMax(
						false, 
						Integer.toString(sdc.getMaxWert()), 
						Integer.toString(sdc.getMaxWertJahr()+1)),
				25,
				0,
				Colors.get(sdc.getSpieler()[sdc.getMaxWertSpieler()].getColIndex()));
		
		this.drawLTextEditor(
				SternResources.StatistikMin(false,
						Integer.toString(sdc.getMinWert()),
						Integer.toString(sdc.getMinWertJahr()+1)),
				50,
				0,
				Colors.get(sdc.getSpieler()[sdc.getMinWertSpieler()].getColIndex()));
		
		// Grafik
		int charWidth = this.fmPlaneten.charWidth('H');
		int height = this.fmPlaneten.getAscent() - this.fmPlaneten.getDescent();
		
		int spalteGrafik = 25;
		int zeileGrafik0 = 1;
		int zeileGrafik1 = 17;
		
		int x0 = Utils.round((double)charWidth + this.factor * (double)SPIELFELD_XOFF + (double)(spalteGrafik* charWidth));
		int y0 = Utils.round((double)height/2. + this.factor * ((double)SPIELFELD_YOFF + ((double)zeileGrafik0+0.5) * (double)SPIELFELD_DX));
		
		int rahmenY = Utils.round((double)(Constants.FELD_MAX_Y * SPIELFELD_DX) * this.factor);
		int y1 = Utils.round((double)height/2. + this.factor * ((double)SPIELFELD_YOFF + ((double)zeileGrafik1+0.5) * (double)SPIELFELD_DX));
		
		int x1 = Utils.round((double)(SCREEN_SIZE_W - SPIELFELD_XOFF) * this.factor + (rahmenY - y1));
		
		y1 -= 2 * height;
		
		// Koordinatensystem
		this.setColor(Color.gray);
		this.dbGraphics.drawLine(x0, y0, x0, y1);
		this.dbGraphics.drawLine(x0, y1, x1, y1);
		this.dbGraphics.drawLine(x1, y0, x1, y1);
		
		// Kurven
		int yMax = y0 + height;
		
		// Max-Linie und Min-Linie
		Graphics2D g2d = (Graphics2D) this.dbGraphics.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{12}, 0);
        g2d.setStroke(dashed);		
        g2d.drawLine(
        		x0,
        		this.getStatistikY(sdc.getMaxWert(), sdc.getMaxWert(), yMax, y1),
        		x1, this.getStatistikY(sdc.getMaxWert(), sdc.getMaxWert(), yMax, y1));
        
        g2d.drawLine(
        		x0,
        		this.getStatistikY(sdc.getMinWert(), sdc.getMaxWert(), yMax, y1),
        		x1, this.getStatistikY(sdc.getMinWert(), sdc.getMaxWert(), yMax, y1));
        
		g2d.dispose();
		
		double dx = (double)(x1 - x0) / (double)(sdc.getJahre() - 1);
		
		for (int jahr = 0; jahr < sdc.getJahre(); jahr++)
		{
			int xx0 = x0 + Utils.round((double)jahr * dx);
			int xx1 = x0 + Utils.round((double)(jahr+1) * dx);
			
			// Champions im Jahr
			int x0champ = Math.max(x0, xx0 - Utils.round(dx / 2.));
			
			int anzChamps = sdc.getChampionsJahr()[jahr].length;
			int dy = Utils.round((2 * height -1 ) / anzChamps);
			
			for (int champ = 0; champ < anzChamps; champ++)
			{
				int colChamp = sdc.getChampionsJahr()[jahr][champ];
				
				this.dbGraphics.setColor(
						Colors.get(sdc.getSpieler()[colChamp].getColIndex()));
				
				int y = y1+1 + (champ * dy);
				int h = (y1 + 2 * height - 1) - y;
								
				this.dbGraphics.fillRect(
						x0champ, 
						y, 
						(jahr < sdc.getJahre() - 1) ? xx1-xx0 : Utils.round(dx / 2.), 
						h);
			}
			
			if (jahr < sdc.getJahre() - 1)
			{
				for (int spieler = 0; spieler < seq.length; spieler++)
				{
					this.dbGraphics.setColor(Colors.get(sdc.getSpieler()[spieler].getColIndex()));
					
					this.dbGraphics.drawLine(
							xx0,
							this.getStatistikY(sdc.getWerte()[jahr][spieler], sdc.getMaxWert(), yMax, y1),
							xx1,
							this.getStatistikY(sdc.getWerte()[jahr+1][spieler], sdc.getMaxWert(), yMax, y1));
				}
			}
		}
		
		// Markiertes Jahr
		this.dbGraphics.setColor(Color.white);
		int xx0 = x0 + Utils.round((double)sdc.getMarkiertesJahrIndex() * dx);
		this.dbGraphics.drawLine(xx0, y0, xx0, y1 + 2 * height - 1);		
	}
	
	private int getStatistikY(int value, int maxValue, int yMax, int y1)
	{
		double ratio = (double)value / (double)maxValue;
		return Utils.round((double)y1 - ratio * (double)(y1 - yMax));
	}
	
	private void drawRect(int x, int y, int w, int h)
	{
		this.dbGraphics.drawRect(
				   Utils.round((double)x * factor),
				   Utils.round((double)y * factor),
				   Utils.round((double)w * this.factor),
				   Utils.round((double)h * this.factor));
	}
	
	private void fillRect(int x, int y, int w, int h)
	{
		this.dbGraphics.fillRect(
				   Utils.round((double)x * factor),
				   Utils.round((double)y * factor),
				   Utils.round((double)w * this.factor),
				   Utils.round((double)h * this.factor));
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
	
	private void drawCircleSpielfeld(Point pos, double r, Color col)
	{
		this.setColor(col);
		
		double rr = r * (double)SPIELFELD_DX;
		
		double x = (double)(SPIELFELD_XOFF + pos.getX() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		double y = (double)(SPIELFELD_XOFF + pos.getY() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		
		this.dbGraphics.drawOval(
				Utils.round(this.factor * x),
				Utils.round(this.factor * y),
				Utils.round(this.factor * rr),
				Utils.round(this.factor * rr));
	}
	
	private void fillCircleSpielfeld(Point pos, double r, Color fillCol)
	{
		this.setColor(fillCol);
		
		double rr = r * (double)SPIELFELD_DX;
		
		double x = (double)(SPIELFELD_XOFF + pos.getX() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		double y = (double)(SPIELFELD_XOFF + pos.getY() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		
		this.dbGraphics.fillOval(
				Utils.round(this.factor * x),
				Utils.round(this.factor * y),
				Utils.round(this.factor * rr),
				Utils.round(this.factor * rr));
		
	}
	
	
	private void fillRauteSpielfeld(Point pos, Color col)
	{
		this.setColor(col);
		
		int[] x = new int[4];
		int[] y = new int[4];
		
		x[0] = Utils.round((double)((double)SPIELFELD_XOFF + pos.getX() * (double)SPIELFELD_DX) * this.factor);
		y[0] = Utils.round((double)((double)SPIELFELD_YOFF + (pos.getY() + 0.5) * (double)SPIELFELD_DX) * this.factor);
		
		x[1] = Utils.round((double)((double)SPIELFELD_XOFF + (pos.getX() + 0.5) * (double)SPIELFELD_DX) * this.factor);
		y[1] = Utils.round((double)((double)SPIELFELD_YOFF + pos.getY() * (double)SPIELFELD_DX) * this.factor);
		
		x[2] = Utils.round((double)((double)SPIELFELD_XOFF + (pos.getX()+1.) * (double)SPIELFELD_DX) * this.factor);
		y[2] = Utils.round((double)((double)SPIELFELD_YOFF + (pos.getY() + 0.5) * (double)SPIELFELD_DX) * this.factor);
		
		x[3] = Utils.round((double)((double)SPIELFELD_XOFF + ((double)pos.getX() + 0.5) * (double)SPIELFELD_DX) * this.factor);
		y[3] = Utils.round((double)((double)SPIELFELD_YOFF + ((double)pos.getY()+1.) * (double)SPIELFELD_DX) * this.factor);
		
		this.dbGraphics.fillPolygon(x, y, 4);
	}
	
	private void drawCTextSpielfeld(Point pos, String text, Color col, Font font, FontMetrics fm)
	{
		this.dbGraphics.setFont(font);
		
		int width = fm.stringWidth(text);
		int height = fm.getAscent() - fm.getDescent();
		
		this.setColor(col);
		
		int x = Utils.round(this.factor * ((double)SPIELFELD_XOFF + (pos.getX()+0.5) * (double)SPIELFELD_DX) - (double)width/2.);
		int y = Utils.round(this.factor * ((double)SPIELFELD_YOFF + (pos.getY()+0.5) * (double)SPIELFELD_DX) + (double)height/2.);
		
		this.dbGraphics.drawString(text, x, y);
	}
		
	private void setColor(Color col)
	{
		this.dbGraphics.setColor(col);
	}
	
	private void zeichneRahmenUmPlanet (SpielfeldPlanetDisplayContent pl)
	{
		if (pl.getFrameCols() == null || pl.getFrameCols().size() == 0)
			return;

		for (int i = 0; i < pl.getFrameCols().size(); i++)
		{
			int x = (int)((SPIELFELD_XOFF + pl.getPos().getX() * SPIELFELD_DX) - (i+1)*2);
			int y = (int)((SPIELFELD_YOFF + pl.getPos().getY() * SPIELFELD_DX) - (i+1)*2);
			
			this.setColor(Colors.get(pl.getFrameCols().get(i)));
			this.drawRect(
					x,
					y,
					SPIELFELD_DX + 4 * (i+1),
					SPIELFELD_DX + 4 * (i+1));
		}
	}
	
	private void markiereFelder (ArrayList<Point> points)
	{
		if (points == null)
			return;
		
		for (Point point: points)
		{
			this.drawCircleSpielfeld(point, 0.8, Color.white);
			
			this.setColor(Color.white);
			
			double x1 = (double)SPIELFELD_XOFF + point.getX() * (double)SPIELFELD_DX;
			double y1 = Utils.round((double)SPIELFELD_YOFF + ((double)point.getY()+0.5) * (double)SPIELFELD_DX);
			double x2 = (double)SPIELFELD_XOFF + (point.getX()+(double)1) * (double)SPIELFELD_DX;
			
			this.drawLine(x1, y1, x2, y1);
			
			x1 = Utils.round((double)SPIELFELD_XOFF + ((double)point.getX()+0.5) * (double)SPIELFELD_DX);
			y1 = (double)SPIELFELD_YOFF + point.getY() * (double)SPIELFELD_DX;
			double y2 = (double)SPIELFELD_YOFF + (point.getY()+1) * (double)SPIELFELD_DX;
			
			this.drawLine(x1, y1, x1, y2);
		}
	}
	
	private void zeichneMinenfelder (ArrayList<MinenfeldDisplayContent> minen)
	{
		if (minen == null || minen.size() == 0)
			return;

		for (MinenfeldDisplayContent mine: minen)
		{
			// Raute zeichnen
			Point pos = new Point(mine.getX(), mine.getY()); 
			
			this.fillRauteSpielfeld(
					pos,
					Color.darkGray);
		
			// Text zeichnen
			this.drawCTextSpielfeld(
					pos,
					Integer.toString(mine.getStaerke()),
					Colors.get(Colors.INDEX_NEUTRAL), this.fontMinen, this.fmMinen);
		}
	}
	
	
	private void zeichneLinien(ArrayList<SpielfeldLineDisplayContent> lines)
	{
		if (lines == null)
			return;
		
		for (SpielfeldLineDisplayContent line: lines)
		{
			if (line.getStart() == null || line.getEnd() == null)
				continue;
			
			this.setColor(Colors.get(line.getCol()));
			
			Point p1 = this.convertSpielfeld2Zeichenkoord(line.getStart());
			Point p2 = this.convertSpielfeld2Zeichenkoord(line.getEnd());
			
			this.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}
	
	private void zeichneLinienObjekte(ArrayList<SpielfeldLineDisplayContent> lines)
	{
		if (lines == null)
			return;
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		
		for (SpielfeldLineDisplayContent line: lines)
		{
			if (line.getEnd() != null && line.getStart() != null)
			{
				// Zeichne Richtungspfeil
				double dx = (double)(line.getEnd().getX() - line.getStart().getX());
				double dy = (double)(line.getEnd().getY() - line.getStart().getY());
				
				double dist = line.getStart().dist(line.getEnd());
				
				double w = (dy >= 0) ? 
						Math.acos(dx/dist) : 
						2.*Math.PI - Math.acos(dx/dist);
				
				double xPos = this.factor * ((double)SPIELFELD_XOFF + ((double)line.getPos().x + 0.5) * (double)SPIELFELD_DX);
				double yPos = this.factor * ((double)SPIELFELD_YOFF + ((double)line.getPos().y + 0.5) * (double)SPIELFELD_DX);
							
				double groesse = this.factor * (double)SPIELFELD_DX * LINIE_OBJEKT_GROESSE;
				
				xPoints[0] = Utils.round(xPos);
				yPoints[0] = Utils.round(yPos);
				
				xPoints[1] = Utils.round(Math.cos(w)*(xPos-1.*groesse)-Math.sin(w)*(yPos+0.3*groesse)-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
				yPoints[1] = Utils.round(Math.sin(w)*(xPos-1.*groesse)+Math.cos(w)*(yPos+0.3*groesse)-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
				
				xPoints[2] = Utils.round(Math.cos(w)*(xPos-0.8*groesse)-Math.sin(w)*yPos-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
				yPoints[2] = Utils.round(Math.sin(w)*(xPos-0.8*groesse)+Math.cos(w)*yPos-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
				
				xPoints[3] = Utils.round(Math.cos(w)*(xPos-1.*groesse)-Math.sin(w)*(yPos-0.3*groesse)-Math.cos(w)*xPos+Math.sin(w)*yPos+xPos);
				yPoints[3] = Utils.round(Math.sin(w)*(xPos-1.*groesse)+Math.cos(w)*(yPos-0.3*groesse)-Math.sin(w)*xPos-Math.cos(w)*yPos+yPos);
				
				this.setColor(Colors.get(line.getCol()));
				this.dbGraphics.fillPolygon(xPoints, yPoints, 4);
			}
			
			// Zeichne Punkt
			this.fillCircleSpielfeld(line.getPos(), LINIE_OBJEKT_RADIUS, Color.black);
			this.drawCircleSpielfeld(line.getPos(), LINIE_OBJEKT_RADIUS, Colors.get(line.getCol()));
		}
	}
	
	private void zeichnePunkte(ArrayList<SpielfeldPointDisplayContent> points)
	{
		if (points == null || points.size() == 0)
			return;

		int size = Math.max(
				Utils.round(4 * OBJEKT_GROESSE * this.factor),
				OBJEKT_MIN_PIXEL);
		
		for (SpielfeldPointDisplayContent point: points)
		{
			Point pt = point.getPos();
			this.setColor(Colors.get(point.getCol()));
			
			switch (point.getSymbol())
			{
			case 1: // Raumer
				// Sanduhr (90 Grad)
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size);
				break;
			case 2: // Aufklaerer
				// +
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y),
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y));
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) - size);
				break;
			case 3: // Patrouille
				// Stern
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y),
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y));
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) - size);
				break;
			case 4: // Transporter
				// Rechteck
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size);
				break;
						
			case 5: // Minenleger
				// Raute
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y));
				
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y),
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) + size);
				
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y));
				
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y),
						getPtSpielfeldX(pt.x),
						getPtSpielfeldY(pt.y) - size);
				
				break;
			case 6: // Minenraeumer
				// X
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) - size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) + size);
				this.dbGraphics.drawLine(
						getPtSpielfeldX(pt.x) - size,
						getPtSpielfeldY(pt.y) + size,
						getPtSpielfeldX(pt.x) + size,
						getPtSpielfeldY(pt.y) - size);

				break;
			default:
				// Kreis
				this.fillCircleSpielfeld(pt, LINIE_OBJEKT_RADIUS, Color.black);
				this.drawCircleSpielfeld(pt, LINIE_OBJEKT_RADIUS, Colors.get(point.getCol()));
			}
		}
	}
	
	private void zeichneRadarkreis(SpielfeldPointRadarDisplayContent radar)
	{
		if (radar == null)
			return;
				
		double rr = 2 * (double)Constants.PATROUILLE_BEOBACHTUNGSRADIUS * (double)SPIELFELD_DX;
		
		double x = (double)(SPIELFELD_XOFF + radar.getPos().getX() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		double y = (double)(SPIELFELD_XOFF + radar.getPos().getY() * SPIELFELD_DX) + ((double)SPIELFELD_DX - rr) / 2.;
		
		this.setColor(Colors.get(radar.getCol()));
		
		AlphaComposite compositeBefore = (AlphaComposite) this.dbGraphics.getComposite();
		float alpha = 0.3f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		this.dbGraphics.setComposite(composite);
		
		this.dbGraphics.fillArc(
				Utils.round(this.factor * x), 
				Utils.round(this.factor * y), 
				Utils.round(this.factor * rr), 
				Utils.round(this.factor * rr), 
				-radar.getWinkelFlugrichtung(), 
				radar.getWinkelRadarstrahl());
		
		double x1Line = Utils.round(SPIELFELD_XOFF + (0.5 + (double)radar.getPos().getX()) * (double)SPIELFELD_DX);
		double y1Line = Utils.round(SPIELFELD_XOFF + (0.5 + (double)radar.getPos().getY()) * (double)SPIELFELD_DX);
		
		double w = (double)(radar.getWinkelFlugrichtung()) * Math.PI / 180;
		
		double x2Line = x1Line + rr * Math.cos(w) / 2;
		double y2Line = y1Line + rr * Math.sin(w) / 2;
		
		this.dbGraphics.drawLine(
				Utils.round(this.factor * x1Line), 
				Utils.round(this.factor * y1Line), 
				Utils.round(this.factor * x2Line), 
				Utils.round(this.factor * y2Line));
		
		this.dbGraphics.setComposite(compositeBefore);		

		w = (double)(radar.getWinkelFlugrichtung() - radar.getWinkelRadarstrahl()) * Math.PI / 180;

		x2Line = x1Line + rr * Math.cos(w) / 2;
		y2Line = y1Line + rr * Math.sin(w) / 2;
		
		this.dbGraphics.drawLine(
				Utils.round(this.factor * x1Line), 
				Utils.round(this.factor * y1Line), 
				Utils.round(this.factor * x2Line), 
				Utils.round(this.factor * y2Line));
		
		this.dbGraphics.drawOval(
				Utils.round(this.factor * x), 
				Utils.round(this.factor * y), 
				Utils.round(this.factor * rr), 
				Utils.round(this.factor * rr));
	}

	private int getPtSpielfeldX(double ptX)
	{
		return  Utils.round(((double)SPIELFELD_XOFF + (ptX + 0.5) * (double)SPIELFELD_DX) * this.factor);
	}
	
	private int getPtSpielfeldY(double ptY)
	{
		return  Utils.round(((double)SPIELFELD_YOFF + (ptY + 0.5)* (double)SPIELFELD_DX) * this.factor);
	}
	
	private void drawTitelbild()
	{
		this.dbGraphics.setFont(this.fontPlaneten);
		this.dbGraphics.setColor(Colors.get((byte)8));
		
		int lineH = this.fmPlaneten.getHeight();
		
		int maxLineW = 0;
		
		for (String line: titelBildTextLines)
		{
			int lineW = this.fmPlaneten.stringWidth(line);
			if (lineW > maxLineW)
				maxLineW = lineW;
		}
		
		int maxTotalH = titelBildTextLines.size() * lineH;
		
		int x = Utils.round(((double)SCREEN_SIZE_W * this.factor - (double)maxLineW) / 2.);
		int yOff = Utils.round(((double)(SCREEN_SIZE_H * this.factor) - (double)maxTotalH) / 2.) +
				this.fmPlaneten.getAscent(); 
		
		for (int i = 0; i < titelBildTextLines.size(); i++)
		{
			String line = titelBildTextLines.get(i);
			
			
			this.dbGraphics.drawString(line, x, yOff + i * lineH);
		}		
	}
	
	private void drawLockSymbol()
	{
		this.dbGraphics.setFont(this.fontPlaneten);
		
		String text = SternResources.EingabeGesperrt(false);
		
		int lineH = this.fmPlaneten.getHeight();
		int lineW = this.fmPlaneten.stringWidth(text);
		
		this.dbGraphics.setColor(Color.DARK_GRAY);
		
		int x0 = Utils.round(((double)SCREEN_SIZE_W * this.factor - (double)lineW) / 2.);
		int y0 = Utils.round(((double)SCREEN_SIZE_H * this.factor - (double)lineH) / 2.);
		
		this.dbGraphics.fillRect(
				   x0 - lineH,
				   y0 - lineH,
				   lineW + 2 * lineH,
				   lineH + 2 * lineH);
		
		this.dbGraphics.setColor(Color.WHITE);
		
		this.dbGraphics.drawString(text, x0, y0 + fmPlaneten.getAscent());
	}
	
	private Point convertSpielfeld2Zeichenkoord(Point pt)
	{
		// Konvertiere eine Spielfeldkoordinate (Mittelpunkt) in Zeichenkoordinaten
		int x = Utils.round(SPIELFELD_XOFF + (0.5 + pt.getX()) * (double)SPIELFELD_DX); 
		int y = Utils.round(SPIELFELD_YOFF + (0.5 + pt.getY()) * (double)SPIELFELD_DX);
		
		return new Point(x,y);
	}
}
