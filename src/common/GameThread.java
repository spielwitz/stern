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

package common;

public class GameThread extends Thread
{
	private GameThreadCommunicationStructure threadCommunicationStructure;
	private IGameThreadEventListener gameThreadEventListener;
	private Game game;
	
	public GameThread(
			GameThreadCommunicationStructure threadCommunicationStructure, 
			IGameThreadEventListener gameThreadEventListener, 
			Game game)
	{
		this.threadCommunicationStructure = threadCommunicationStructure;
		this.gameThreadEventListener = gameThreadEventListener;
		this.game = game;
		this.game.setSoloPlayer(threadCommunicationStructure.isSoloPlayer);
	}
	
	public void run()
	{
		if (this.game.isInitial())
			this.game.initNewGame(this);
		else
			this.game.initAfterLoad(this);
	}
	
	void updateDisplay(ScreenContent screenContent)
	{
		this.gameThreadEventListener.updateDisplay(new ScreenUpdateEvent(this, screenContent));
	}
	
	void checkMenueEnabled()
	{
		this.gameThreadEventListener.checkMenuEnabled();
	}
	
	void save(Game game, boolean autoSave)
	{
		this.gameThreadEventListener.saveGame(game, autoSave);
	}
	
	boolean launchEmail(String recipient, String subject, String bodyText, EmailTransportBase obj)
	{
		return this.gameThreadEventListener.launchEmailClient(recipient, subject, bodyText, obj);
	}
	
	PostMovesResult postMovesToServer(String gameId, String playerName, MovesTransportObject set)
	{
		return this.gameThreadEventListener.postMovesToServer(gameId, playerName, set);
	}
	
	MovesTransportObject importMovesFromEmail()
	{
		return this.gameThreadEventListener.importMovesFromEmail();
	}
	
	boolean openPdf(byte[] pdfBytes, String clientId)
	{
		return this.gameThreadEventListener.openPdf(pdfBytes, clientId);
	}
	
	void addToHighscore(Archive archive, Player[] players)
	{
		this.gameThreadEventListener.addToHighscore(archive, players);
	}
	
	KeyEventExtended waitForKeyInput()
	{
		Game gameNew = null;
		
		synchronized(this.threadCommunicationStructure)
		{
			try
			{
				threadCommunicationStructure.wait();
				
				if (threadCommunicationStructure.gameNew != null)
				{
					gameNew = (Game)Utils.klon(threadCommunicationStructure.gameNew);
					gameNew.setSoloPlayer(threadCommunicationStructure.isSoloPlayer);
					if (threadCommunicationStructure.gameNew.isInitial())
						gameNew.setInitial();
					threadCommunicationStructure.gameNew = null;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		if (gameNew != null)
		{
			this.game = null;
			this.game = gameNew;
			
			this.run();
			
			return null;
		}
		else
			return this.threadCommunicationStructure.keyEvent;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	void pause(int milliseconds)
	{
		this.gameThreadEventListener.pause(milliseconds);
		
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
	
	void updateGameInfo()
	{
		this.gameThreadEventListener.updateGameInfo();
	}
	
	boolean isMoveEnteringOpen()
	{
		return this.gameThreadEventListener.isMoveEnteringOpen();
	}
}
