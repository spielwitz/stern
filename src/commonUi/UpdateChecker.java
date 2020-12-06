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

package commonUi;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import common.Constants;
import common.ReleaseGetter;
import common.SternResources;

public class UpdateChecker implements Runnable
{
	private IUpdateCheckerCallback parent;
	private long lastReleaseFoundLong;
	private boolean background;
	
	public UpdateChecker(IUpdateCheckerCallback parent, String lastReleaseFound, boolean background)
	{
		this.parent = parent;
		this.lastReleaseFoundLong =
				lastReleaseFound != null ? 
						Long.parseLong(lastReleaseFound) :
						-1;
	    this.background = background;
	}

	@Override
	public void run()
	{
		String availableRelease = this.getReleaseFromServer(Constants.RELEASE_FILE_NAME);
		if (availableRelease == null)
		{
			if (!this.background)
			{
				DialogWindow.showError(
						(Frame)parent, 
						SternResources.UpdateServerNichtErreichbar(false),
						SternResources.Update(false));
			}
			
			return;
		}
		
        long availableReleaseLong = Long.parseLong(availableRelease);
        
        this.parent.lastUpdateFound(availableRelease);
        
        String recommendedRelease = this.getReleaseFromServer(Constants.RELEASE_RECOMMENDED_FILE_NAME);
        long recommendedReleaseLong =  recommendedRelease != null ?
        			Long.parseLong(recommendedRelease) :
        			-1;
        
        String currentRelease = ReleaseGetter.getRelease();		
		long currentReleaseLong = Long.parseLong(currentRelease);
		
		boolean aktuell = true;
		
		if (currentReleaseLong < availableReleaseLong)
		{
			String urlString = Constants.STERN_URL + "/Download.html";
			
			if (currentReleaseLong >= recommendedReleaseLong)
			{
				// Kleineres Update. Nur einmal melden.
				if (this.lastReleaseFoundLong < availableReleaseLong)
				{
					aktuell = false;
					DialogWindow.showInformation(
							(Frame)parent, 
							new MessageWithLink(
									(Frame)parent,
									SternResources.UpdateVerfuegbar(false) + " - <a href=\""+urlString+"\">"+urlString+"</a>"),
							SternResources.Update(false));
				}
			}
			else
			{
				aktuell = false;
				DialogWindow.showInformation(
						(Frame)parent, 
						new MessageWithLink(
								(Frame)parent,
								SternResources.UpdateVerfuegbarWichtig(false) + " - <a href=\""+urlString+"\">"+urlString+"</a>"),
						SternResources.Update(false));
			}
		}	
		
		if (!this.background && aktuell)
		{
			DialogWindow.showInformation(
					(Frame)parent, 
					SternResources.UpdateAktuell(false),
					SternResources.Update(false));
		}
	}
	
	private String getReleaseFromServer(String fileName)
	{
		try
		{
			String httpsURL = Constants.STERN_URL + "/" + fileName;
	        URL myUrl = new URL(httpsURL);
	        HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
	        InputStream is = conn.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	
	        String inputLine = br.readLine();
	
	        br.close();
	        
	        return inputLine;
		}
		catch (Exception x)
		{
			return null;
		}
	}

}
