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

package stern;

import java.awt.Frame;

import common.Constants;
import common.ReleaseGetter;
import common.SternResources;
import commonUi.DialogWindow;
import commonUi.MessageWithLink;

class MinBuildChecker
{
	static boolean doCheck(Frame parent, String minBuild)
	{
		boolean success = true;
		
		if (minBuild == null)
			return true;
		
		String thisBuild = ReleaseGetter.getRelease();
		
		if (thisBuild.equals(Constants.NO_BUILD_INFO))
			return true;
		
		if (thisBuild.compareTo(minBuild) < 0)
		{
			DialogWindow.showError(
					parent, 
					new MessageWithLink(
						parent,
						SternResources.MinBuild(false,
									ReleaseGetter.format(minBuild),
									ReleaseGetter.format(thisBuild),
							"<a href=\""+Constants.STERN_URL+"\">"+Constants.STERN_URL+"</a>")),
					SternResources.Fehler(false));
			
			success = false;
		}
		
		return success;
	}
}
