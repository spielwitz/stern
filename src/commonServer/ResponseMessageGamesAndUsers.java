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

package commonServer;

import java.util.ArrayList;
import java.util.Hashtable;

import common.GameInfo;

public class ResponseMessageGamesAndUsers extends MessageBase
{
	public String emailGameHost;
	
	public Hashtable<String, UserInfo> users;
	
	public ArrayList<GameInfo> gamesWaitingForEnterMoves;
	public ArrayList<GameInfo> gamesWaitingForOtherPlayers;
	public ArrayList<GameInfo> gamesFinalized;
	public ArrayList<GameInfo> gamesGameHost;
	
	public ResponseMessageGamesAndUsers()
	{
		this.gamesFinalized = new ArrayList<GameInfo>();
		this.gamesWaitingForOtherPlayers = new ArrayList<GameInfo>();
		this.gamesGameHost = new ArrayList<GameInfo>();
		this.gamesWaitingForEnterMoves = new ArrayList<GameInfo>();
	}

	public class UserInfo
	{
		public String email;
		
		public UserInfo(String email)
		{
			this.email = email;
		}
	}
}
