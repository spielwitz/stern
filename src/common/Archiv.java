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

package common;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Archiv implements Serializable
{
	private int punkte[]; // Punkte pro Spieler
	private int raumer[]; // Raumer pro Spieler
	private int anzPl[]; // Anzahl Planeten pro Spieler
	private int eprod[]; // Energieproduktion pro Spieler
	
	private ArrayList<ArchivReplayIndexList> replayIndexed;
	
	private ArrayList<PlanetenlisteDisplayContent> p; // Planetenliste-Inhalte
	private ArrayList<SpielfeldDisplayContent> s; // Spielfeld-Inhalte
	
	Archiv(int[] punkte, int[] raumer, int[] anzPl, int[] eprod)
	{
		super();
		this.punkte = punkte;
		this.raumer = raumer;
		this.anzPl = anzPl;
		this.eprod = eprod;
	}

	public int[] getRaumer() {
		return raumer;
	}

	public int[] getAnzPl() {
		return anzPl;
	}

	public int[] getEprod() {
		return eprod;
	}
	
	public int[] getPunkte()
	{
		return punkte;
	}
	
	public int getReplaySize()
	{
		if (this.replayIndexed == null)
			return 0;
		else
			return this.replayIndexed.size();
	}
	
	public ArrayList<ScreenDisplayContent> getReplay()
	{
		if (this.replayIndexed == null)
			return null;
		
		ArrayList<ScreenDisplayContent> replay = new ArrayList<ScreenDisplayContent>(this.replayIndexed.size());
		
		for (ArchivReplayIndexList indices: this.replayIndexed)
		{
			ScreenDisplayContent sdc = new ScreenDisplayContent();
			
			sdc.setCons(indices.c);
			sdc.setPlaneten(this.p.get(indices.p));
			sdc.setSpielfeld(this.s.get(indices.s));
			sdc.setPause(indices.getPause());
			sdc.setEreignisTag(indices.g);
			
			replay.add(sdc);
		}
		
		return replay;
	}
	
	public void setReplay(ArrayList<ScreenDisplayContent> replay)
	{
		// Wandle die liste der ScreenDisplayContent in platzsparende Ablage um
		this.replayIndexed = new ArrayList<ArchivReplayIndexList>(replay.size());
		
		this.p = new ArrayList<PlanetenlisteDisplayContent>();
		this.s = new ArrayList<SpielfeldDisplayContent>();
		
		// Parallele Listen mit den Darstellungen der Objekt als JSON
		ArrayList<String> pJsonList = new ArrayList<String>();
		ArrayList<String> sJsonList = new ArrayList<String>();
		
		Gson serializer = new Gson();
		
		for (ScreenDisplayContent sdc: replay)
		{
			// Planetenliste
			String pJson = serializer.toJson(sdc.getPlaneten());
			
			int pIndex = pJsonList.indexOf(pJson);
			
			if (pIndex < 0)
			{
				this.p.add(sdc.getPlaneten());
				pJsonList.add(pJson);
				
				pIndex = this.p.size() - 1;
			}
			
			// Spielfeld
			String sJson = serializer.toJson(sdc.getSpielfeld());
			
			int sIndex = sJsonList.indexOf(sJson);
			
			if (sIndex < 0)
			{
				this.s.add(sdc.getSpielfeld());
				sJsonList.add(sJson);
				
				sIndex = this.s.size() - 1;
			}
			
			// Jetzt alle Indices abspeichern
			this.replayIndexed.add(
					new ArchivReplayIndexList(
							sdc.getCons(), 
							pIndex, 
							sIndex,
							sdc.getPause(),
							sdc.getEreignisTag()));
		}
	}
	
	void loescheAuswertung()
	{
		this.replayIndexed = null;
		this.p = null;
		this.s = null;
	}
	
	boolean auswertungExists()
	{
		return this.replayIndexed != null;
	}
	
}
