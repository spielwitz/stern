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

import java.io.Serializable;

@SuppressWarnings("serial")
class ReplayEvent implements Serializable 
{
	// Index des serialisierten Console-Inhalts 
	private int c;
	
	// Index des serialisierten Planetenliste-Inhalts
	private int p;
	
	// Index des serialisierten Spielfeldinhalts
	private int s;
	
	// Ereignistag
	private int g;
	
	// Pause
	private boolean u; 
	
	ReplayEvent(ScreenDisplayContent sdc, Replays replays)
	{
		if (sdc.getCons() != null)
		{
			this.c = replays.addObject( 
					new ReplayConsoleDisplayContent(
							sdc.getCons(),
							replays));
		}
		
		if (sdc.getPlaneten() != null)
		{
			this.p = replays.addObject(
					new ReplayPlanetenlisteDisplayContent(
							sdc.getPlaneten(),
							replays));
		}
		
		if (sdc.getSpielfeld() != null)
		{
			this.s = replays.addObject(
					new ReplaySpielfeldDisplayContent(
							sdc.getSpielfeld(),
							replays));
		}
		
		this.g = sdc.getEreignisTag();
		this.u = sdc.getPause();
	}
	
	ScreenDisplayContent getScreenDisplayContent(Replays replays)
	{
		ScreenDisplayContent sdc = new ScreenDisplayContent();
		
		ReplayConsoleDisplayContent rcdc = (ReplayConsoleDisplayContent)replays.fromJson(
				replays.getString(this.c), 
				ReplayConsoleDisplayContent.class);
		
		sdc.setCons(rcdc.getConsoleDisplayContent(replays));
		
		ReplayPlanetenlisteDisplayContent rpdc = (ReplayPlanetenlisteDisplayContent)replays.fromJson(
				replays.getString(this.p), 
				ReplayPlanetenlisteDisplayContent.class);
				
		sdc.setPlaneten(rpdc.getPlanetenlisteDisplayContent(replays));
		
		ReplaySpielfeldDisplayContent spdc = (ReplaySpielfeldDisplayContent)replays.fromJson(
				replays.getString(this.s), 
				ReplaySpielfeldDisplayContent.class);
		
		sdc.setSpielfeld(spdc.getSpielfeldDisplayContent(replays));
		
		sdc.setPause(this.u);
		sdc.setEreignisTag(this.g);
		
		return sdc;
	}
}
