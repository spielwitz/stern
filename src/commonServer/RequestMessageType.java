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

package commonServer;

public enum RequestMessageType
{
	//GET_GAME_INFO,
	GET_GAMES_AND_USERS,
	POST_MOVES,
	POST_NEW_GAME,
	ADMIN_CHANGE_USER,
	ADMIN_SERVER_SHUTDOWN,
	ADMIN_PING,
	ADMIN_GET_USERS,
	ADMIN_DELETE_USER,
	ADMIN_GET_SERVER_STATUS,
	ADMIN_GET_LOG,
	ADMIN_SET_LOG_LEVEL,
	PING,
	GET_GAME,
	GET_GAMES_ZUGEINGABE,
	GET_EVALUATIONS,
	GAME_HOST_FINALIZE_GAME,
	GAME_HOST_DELETE_GAME,
	ACTIVATE_USER
}
