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

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.URI;
import java.net.URLEncoder;

import common.EmailTransportBase;
import common.ReleaseGetter;
import common.SternResources;
import common.Utils;
import commonUi.DialogWindow;

class EmailToolkit
{
	private static final String BASE64_START = "----------begin:\n";
	private static final String BASE64_END = "\n:end----------";
	
	static boolean launchEmailClient(
			Component parent,
			String recipient, 
			String subject, 
			String bodyText, 
			String password, 
			EmailTransportBase obj)
	{
		boolean ok = true;
		
		String uriStr = null;
		
		if (obj != null)
		{
			obj.className = obj.getClass().getName();
			obj.build = ReleaseGetter.getRelease();
			
			String base64 = Utils.objectToBase64(obj, password);
			
			uriStr = String.format("mailto:%s?subject=%s&body=%s",
		            recipient, // use semicolon ";" for Outlook!
		            urlEncode(subject),
		            urlEncode(bodyText + "\n\n" + BASE64_START + base64 + BASE64_END));
		}
		else
			uriStr = String.format("mailto:%s?subject=%s&body=%s",
		            recipient, // use semicolon ";" for Outlook!
		            urlEncode(subject),
		            urlEncode(bodyText));
		
		try
		{
			Desktop.getDesktop().browse(new URI(uriStr));
		}
		catch (Exception x)
		{
			DialogWindow.showError(
					parent, 
					SternResources.EmailNichtGeoeffnet(false, x.getMessage()), 
					SternResources.Fehler(false));
			ok = false;
		}
		
		return ok;
	}
	
	static <T> EmailTransportBase parseEmail(String body, Class<T> expectedClass, String password)
	{
		// Zuerst alle Leerzeichen aus der Mail entfernen
		body = body.replace(" ", "");
		
		int posStart = body.indexOf(BASE64_START);
		if (posStart == -1)
			return null;
		
		int posEnd = body.indexOf(BASE64_END);
		if (posEnd == -1 || posEnd < posStart)
			return null;
		
		String base64 = body.substring(posStart + BASE64_START.length(), posEnd);
		
		EmailTransportBase obj = null;
		
		try
		{
			obj = (EmailTransportBase)Utils.base64ToObject(base64, expectedClass, password);
			
			if (obj != null && !obj.className.equals(expectedClass.getName()))
				obj = null;
		}
		catch (Exception x)
		{
			obj = null;
		}
		
		return obj;
	}
	
	private static final String urlEncode(String str) {
	    try {
	        return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	static String getClipboardContent()
	{
		String retval = "";
		
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipData = clip.getContents(clip);
		
		if (clipData != null)
		{
			try
			{
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					retval = (String)(clipData.getTransferData(
							DataFlavor.stringFlavor));
				}
			}
			catch (Exception e) {
			}
		}			

		return retval;
	}
}
