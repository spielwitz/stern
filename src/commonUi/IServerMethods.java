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

package commonUi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.ScreenDisplayContentClient;

public interface IServerMethods extends Remote
{
	public void rmiKeyPressed(String clientId, String languageCode, int id, long when, int modifiers, int keyCode, char keyChar) throws RemoteException;
	public String rmiClientConnectionRequest(String clientId, String release, String ip, String code, String clientName) throws RemoteException;
	public ScreenDisplayContentClient rmiGetCurrentScreenDisplayContent(String clientId) throws RemoteException;
	public void rmiClientLogoff(String clientId) throws RemoteException;
	public boolean rmiClientCheckRegistration(String clientId) throws RemoteException;
}
