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

import java.util.Hashtable;

public class SpielThread extends Thread
{
	private SpielThreadCommunicationStructure threadCommunicationStructure;
	private ISpielThreadEventListener screenUpdateListener;
	private Spiel spiel;
	
	public SpielThread(SpielThreadCommunicationStructure threadCommunicationStructure, ISpielThreadEventListener screenUpdateListener, Spiel spiel)
	{
		this.threadCommunicationStructure = threadCommunicationStructure;
		this.screenUpdateListener = screenUpdateListener;
		this.spiel = spiel;
		this.spiel.setEmailSpieler(threadCommunicationStructure.emailSpieler);
	}
	
	public void run()
	{
		if (this.spiel.istInitial())
			this.spiel.initNewGame(this);
		else
			this.spiel.initAfterLoad(this);
	}
	
	public void updateDisplay(ScreenDisplayContent cont)
	{
		this.screenUpdateListener.update(new ScreenUpdateEvent(this, cont));
	}
	
	public void checkMenueEnabled()
	{
		this.screenUpdateListener.checkMenuEnabled();
	}
	
	public void speichern(Spiel spiel, boolean autoSave)
	{
		this.screenUpdateListener.speichern(spiel, autoSave);
	}
	
	public boolean launchEmail(String recipient, String subject, String bodyText, EmailTransportBase obj)
	{
		return this.screenUpdateListener.launchEmail(recipient, subject, bodyText, obj);
	}
	
	public PostMovesResult postMovesToServer(String gameId, String spielerName, SpielzuegeEmailTransport set)
	{
		return this.screenUpdateListener.postMovesToServer(gameId, spielerName, set);
	}
	
	public Hashtable<Integer,Archiv> getEvaluations(String gameId, int vonJahr, int bisJahr)
	{
		return this.screenUpdateListener.getEvaluations(gameId, vonJahr, bisJahr);
	}
	
	public SpielzuegeEmailTransport importSpielzuegeAusEmail()
	{
		return this.screenUpdateListener.importSpielzuegeAusEmail();
	}
	
	public boolean openPdf(byte[] pdfBytes, String clientId)
	{
		return this.screenUpdateListener.openPdf(pdfBytes, clientId);
	}
	
	public void addToHighscore(Archiv spielstand, Spieler[] spieler)
	{
		this.screenUpdateListener.addToHighscore(spielstand, spieler);
	}
	
	public KeyEventExtended waitForKeyInput()
	{
		// Key-Event programmatisch erzeugen:
		// https://coderanch.com/t/330281/java/Creating-Key-Event-Component
		
		Spiel neuesSpiel = null;
		
		synchronized(this.threadCommunicationStructure)
		{
			try
			{
				threadCommunicationStructure.wait();
				
				if (threadCommunicationStructure.neuesSpiel != null)
				{
					neuesSpiel = (Spiel)Utils.klon(threadCommunicationStructure.neuesSpiel);
					neuesSpiel.setEmailSpieler(threadCommunicationStructure.emailSpieler);
					if (threadCommunicationStructure.neuesSpiel.istInitial())
						neuesSpiel.setInitial();
					threadCommunicationStructure.neuesSpiel = null;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		if (neuesSpiel != null)
		{
			this.spiel = null;
			this.spiel = neuesSpiel;
			
			this.run();
			
			return null;
		}
		else
			// Normaler Tastendruck
			return this.threadCommunicationStructure.keyEvent;
	}
	
	public Spiel getSpiel()
	{
		return spiel;
	}
	
	public void pause(int milliseconds)
	{
		this.screenUpdateListener.pause(milliseconds);
		
		synchronized(this.threadCommunicationStructure)
		{
			try
			{
				threadCommunicationStructure.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void triggerGameInfoUpdate()
	{
		this.screenUpdateListener.triggerGameInfoUpdate();
	}
	
}
