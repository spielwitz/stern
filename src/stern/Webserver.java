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
import common.ScreenDisplayContent;
import common.ScreenPainter;
import common.Utils;
import commonUi.PaintPanel;

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
				// TODO Auto-generated catch block
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
					
					// Es interessiert nicht, was der Sender schickt. Wir schicken immer dasselbe Bild zurueck.
					try
					{
					while (in.read() != -1)
					{
					}
					}
					catch (Exception xx) {}
					
					// Bild mit dem Spielfeld erzeugen
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
		
		System.out.println("Webserver gestoppt");
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

		ScreenDisplayContent sdc = this.callback.getScreenDisplayContentStartOfYear();
		
		int width = Utils.round((double)ScreenPainter.SCREEN_SIZE_W * FACTOR);
		int height =
				sdc == null ?
						Utils.round((double)ScreenPainter.SCREEN_SIZE_H * FACTOR) :
						(int)((double)(Constants.FELD_MAX_Y * ScreenPainter.SPIELFELD_DX+ 2 * PaintPanel.RAND) * FACTOR);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)image.createGraphics();
		
		g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        Font fontPlaneten = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_PLANETEN * FACTOR));
        Font fontMinen = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_MINEN * FACTOR));
        Font fontFelder = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_FELDER * FACTOR));
        
        new ScreenPainter(sdc, true, g2d, fontPlaneten, fontMinen, fontFelder, FACTOR);
        
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
