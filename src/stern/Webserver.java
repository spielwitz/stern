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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.imageio.ImageIO;

import common.Constants;
import common.ScreenContent;
import common.ScreenPainter;
import common.Utils;
import commonUi.DialogFontHelper;
import commonUi.PanelScreenContent;

class Webserver implements Runnable
{
	private Stern callback;
	private Thread t;
	private ServerSocket serverSocket;
	private boolean serverSocketClosed;
	
	private static final int PORT = 8080;
	private static final double FACTOR = 1.5;
	
	Webserver(Stern callback)
	{
		this.callback = callback;
	}
	
	void start()
	{
		if (this.t != null && this.t.isAlive())
		{
			return;
		}
		
		this.t = new Thread(this);
		t.start();
	}
	
	void stop()
	{
		if (t != null && serverSocket != null)
		{
			this.serverSocketClosed = true;
			
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run()
	{
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
			
		do
		{
			Socket socket = null;
			BufferedReader in = null;
			PrintWriter out = null;
			BufferedOutputStream dataOut = null;
			
			try {
				socket = serverSocket.accept();
				socket.setSoTimeout(1000);

				synchronized(this)
				{
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream());
					dataOut = new BufferedOutputStream(socket.getOutputStream());
					
					try
					{
						while (in.read() != -1)
						{
						}
					}
					catch (Exception xx) {}
					
					byte[] picData = this.createPicture();
					
					out.println("HTTP/1.1 200 OK");
					out.println("Server: STERN HTTP Server");
					out.println("Date: " + new Date());
					out.println("Content-type: text/plain");
					out.println("Content-length: " + picData.length);
					out.println();
					out.flush();
					
					dataOut.write(picData, 0, picData.length);
					dataOut.flush();
				}
				
			} catch (Exception e)
			{
				if (this.serverSocketClosed)
				{
					break;
				}
				e.printStackTrace();
			}
			finally {
				try {
					if (in != null) in.close();
					if (out != null) out.close();
					if (dataOut != null) dataOut.close();
					if (socket != null) socket.close();
				} catch (Exception e) {
					e.printStackTrace();
			} 
		}
			
			
		} while (true);
	}
	
	String getUrl()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("http://");
		sb.append(Utils.getMyIPAddress());
		sb.append(":");
		sb.append(PORT);
		
		return sb.toString();
		
	}
	
	private byte[] createPicture()
	{
		byte[] bytes = null;

		ScreenContent screenContent = this.callback.getScreenContentStartOfYear();
		
		int width = Utils.round((double)ScreenPainter.SCREEN_WIDTH * FACTOR);
		int height =
				screenContent == null ?
						Utils.round((double)ScreenPainter.SCREEN_HEIGHT * FACTOR) :
						(int)((double)(Constants.BOARD_MAX_Y * ScreenPainter.BOARD_DX+ 2 * PanelScreenContent.BORDER_SIZE) * FACTOR);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)image.createGraphics();
		
		g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        Font fontBasis = DialogFontHelper.getFontBase();
        
        Font fontPlanets = fontBasis.deriveFont((float)Utils.round((double)PanelScreenContent.FONT_SIZE_PLANETS * FACTOR));
        Font fontMines = fontBasis.deriveFont((float)Utils.round((double)PanelScreenContent.FONT_SIZE_MINES * FACTOR));
        Font fontSectors = fontBasis.deriveFont((float)Utils.round((double)PanelScreenContent.FONT_SIZE_SECTORS * FACTOR));
        
        new ScreenPainter(screenContent, true, g2d, fontPlanets, fontMines, fontSectors, FACTOR);
        
        g2d.dispose();
				
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(image, "png", bos);
			bytes = bos.toByteArray();
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		return bytes;
	}	
}
