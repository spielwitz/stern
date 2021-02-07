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

package commonUi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import common.Constants;
import common.ScreenDisplayContent;
import common.ScreenPainter;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial")
public class PaintPanel extends Panel implements KeyListener
{
	// Das Panel ist ziemlich dumm. Es stellt nur Inhalte dar, die in der Struktur ScreenDisplayContent definiert sind 
	private IHostComponentMethods parent;
	
	public static final int		RAND = 10;
	private static final double	RATIO = (double)ScreenPainter.SCREEN_SIZE_W / (double)ScreenPainter.SCREEN_SIZE_H;
	
	public static final int		FONT_SIZE_PLANETEN = 11;
	public static final int		FONT_SIZE_FELDER = 8;
	public static final int		FONT_SIZE_MINEN = 8;
		
	private double factor;
	private int xOff, yOff;
	
	private Graphics2D dbGraphics;
		
	private Font fontBasis;
	private Font fontPlaneten, fontMinen, fontFelder;
	
	private ScreenDisplayContent cont;
	private boolean inputEnabled;
	private boolean showInputDisabled;
	
	public PaintPanel(IHostComponentMethods parent)
	{
		super();
		
		this.parent = parent;
		this.addKeyListener(this);
		
		this.setBackground(Color.BLACK);
		this.setFocusable(true); // Wichtig, sonst geht der Key Listener nicht!
		this.setFocusTraversalKeysEnabled(false); // Damit die TAB-Taste geht
		
		// Font laden
		try {
			this.fontBasis = Font.createFont( Font.TRUETYPE_FONT,
					getClass().getResourceAsStream(Constants.FONT_NAME));
			//this.fontBasis = new Font(Font.MONOSPACED, Font.PLAIN, 10);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void redraw(
			ScreenDisplayContent screenDisplayContent, 
			boolean inputEnabled,
			boolean showInputDisabled)
	{
		this.cont = screenDisplayContent;
		this.inputEnabled = inputEnabled;
		this.showInputDisabled = showInputDisabled;
		this.paint(this.getGraphics());
	}
	
	public ScreenDisplayContent getScreenDisplayContent()
	{
		return this.cont;
	}
	
	public void paint( Graphics g )
	{
		this.update(g);
	}
	
	public void update (Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		// Berechne den Zoom-Faktor
		this.calcZoomFactor();
		
		BufferedImage image = (BufferedImage)this.createImage(Utils.round(ScreenPainter.SCREEN_SIZE_W * this.factor), Utils.round(ScreenPainter.SCREEN_SIZE_H * this.factor));
		this.dbGraphics = image.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.fontPlaneten = this.fontBasis.deriveFont((float)(Utils.round((double)FONT_SIZE_PLANETEN * factor)));
		this.fontFelder = this.fontBasis.deriveFont((float)(Utils.round((double)FONT_SIZE_FELDER * factor)));
		this.fontMinen = this.fontBasis.deriveFont((float)(Utils.round((double)FONT_SIZE_MINEN * factor)));
		
		new ScreenPainter(
				this.cont, 
				!this.showInputDisabled, 
				this.dbGraphics, 
				this.fontPlaneten, 
				this.fontMinen, 
				this.fontFelder, 
				this.factor);
				
		// Offscreen anzeigen
		g2.drawImage(image, this.xOff, this.yOff, this);
	}
	
	private void calcZoomFactor()
	{
		// Berechne den Zoom-Faktor und die tatsaechlichen Ursprungskoordinaten
		Dimension dim = this.getSize();
		
		int hAvailable = Utils.round((double)dim.height - 2. * (double)RAND);
		int wAvailable = Utils.round((double)dim.width - 2. * (double)RAND);
		
		double ratioAvailable = (double)wAvailable / (double)hAvailable;
		
		if (ratioAvailable > RATIO)
		{
			
			this.factor = (double)hAvailable / (double)ScreenPainter.SCREEN_SIZE_H;
			
			this.yOff = RAND;
			this.xOff = Utils.round((((double)dim.width - 2. * (double)RAND) - (double)ScreenPainter.SCREEN_SIZE_W * this.factor) / 2. + (double)RAND);
		}
		else
		{
			
			this.factor = (double)wAvailable / (double)ScreenPainter.SCREEN_SIZE_W;
			
			this.xOff = RAND;
			this.yOff = Utils.round((((double)dim.height - 2. * (double)RAND) - (double)ScreenPainter.SCREEN_SIZE_H * this.factor) / 2. + (double)RAND);
		}			
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (this.inputEnabled)
		{
			this.parent.hostKeyPressed(arg0, SternResources.getLocale());
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0)
	{
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}
}
