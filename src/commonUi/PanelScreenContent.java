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
import common.ScreenContent;
import common.ScreenPainter;
import common.SternResources;
import common.Utils;

@SuppressWarnings("serial")
public class PanelScreenContent extends Panel implements KeyListener
{
	private IHostComponentMethods parent;
	
	public static final int		BORDER_SIZE = 10;
	private static final double	RATIO = (double)ScreenPainter.SCREEN_WIDTH / (double)ScreenPainter.SCREEN_HEIGHT;
	
	public static final int		FONT_SIZE_PLANETS = 11;
	public static final int		FONT_SIZE_SECTORS = 8;
	public static final int		FONT_SIZE_MINES = 8;
		
	private double factor;
	private int xOff, yOff;
	
	private Graphics2D dbGraphics;
		
	private Font fontBase;
	private Font fontPlanets, fontMines, fontSectors;
	
	private ScreenContent screenContent;
	private boolean inputEnabled;
	private boolean showInputDisabled;
	
	public PanelScreenContent(IHostComponentMethods parent)
	{
		super();
		
		this.parent = parent;
		this.addKeyListener(this);
		
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
		
		try {
			this.fontBase = Font.createFont( Font.TRUETYPE_FONT,
					getClass().getResourceAsStream(Constants.FONT_NAME));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void redraw(
			ScreenContent screenContent, 
			boolean inputEnabled,
			boolean showInputDisabled)
	{
		this.screenContent = screenContent;
		this.inputEnabled = inputEnabled;
		this.showInputDisabled = showInputDisabled;
		this.paint(this.getGraphics());
	}
	
	public ScreenContent getScreenContent()
	{
		return this.screenContent;
	}
	
	public void paint( Graphics g )
	{
		this.update(g);
	}
	
	public void update (Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		this.calculateZoomFactor();
		
		BufferedImage image = (BufferedImage)this.createImage(Utils.round(ScreenPainter.SCREEN_WIDTH * this.factor), Utils.round(ScreenPainter.SCREEN_HEIGHT * this.factor));
		this.dbGraphics = image.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		this.fontPlanets = this.fontBase.deriveFont((float)(Utils.round((double)FONT_SIZE_PLANETS * factor)));
		this.fontSectors = this.fontBase.deriveFont((float)(Utils.round((double)FONT_SIZE_SECTORS * factor)));
		this.fontMines = this.fontBase.deriveFont((float)(Utils.round((double)FONT_SIZE_MINES * factor)));
		
		new ScreenPainter(
				this.screenContent, 
				!this.showInputDisabled, 
				this.dbGraphics, 
				this.fontPlanets, 
				this.fontMines, 
				this.fontSectors, 
				this.factor);
				
		g2.drawImage(image, this.xOff, this.yOff, this);
	}
	
	private void calculateZoomFactor()
	{
		Dimension dim = this.getSize();
		
		int hAvailable = Utils.round((double)dim.height - 2. * (double)BORDER_SIZE);
		int wAvailable = Utils.round((double)dim.width - 2. * (double)BORDER_SIZE);
		
		double ratioAvailable = (double)wAvailable / (double)hAvailable;
		
		if (ratioAvailable > RATIO)
		{
			this.factor = (double)hAvailable / (double)ScreenPainter.SCREEN_HEIGHT;
			
			this.yOff = BORDER_SIZE;
			this.xOff = Utils.round((((double)dim.width - 2. * (double)BORDER_SIZE) - (double)ScreenPainter.SCREEN_WIDTH * this.factor) / 2. + (double)BORDER_SIZE);
		}
		else
		{
			this.factor = (double)wAvailable / (double)ScreenPainter.SCREEN_WIDTH;
			
			this.xOff = BORDER_SIZE;
			this.yOff = Utils.round((((double)dim.height - 2. * (double)BORDER_SIZE) - (double)ScreenPainter.SCREEN_HEIGHT * this.factor) / 2. + (double)BORDER_SIZE);
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
