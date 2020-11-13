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

import java.util.ArrayList;

public class ResponseMessageGetUsers extends MessageBase
{
	public ArrayList<UserInfo> users;
	
	public ResponseMessageGetUsers()
	{
		this.users = new ArrayList<UserInfo>();
	}
	
	public void addUserInfo(String userId, String name, String email, boolean activated)
	{
		UserInfo userInfo = new UserInfo();
		
		userInfo.userId = userId;
		userInfo.name = name;
		userInfo.email = email;
		userInfo.activated = activated;
		
		this.users.add(userInfo);
	}
	
	public class UserInfo
	{
		public String userId;
		public String email;
		public String name;
		public boolean activated;
	}
}
