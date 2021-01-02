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
class ConsoleEvaluationProgressBarDisplayContent implements Serializable
{
	private int t; // Tag des Ereignisses
	private boolean a; // True, wenn Jahresbeginn
	private boolean e; // True, wenn Jahresende
	
	public int getTag()
	{
		return this.t;
	}
	
	public boolean isJahresbeginn()
	{
		return this.a;
	}
	
	public boolean isJahresenede()
	{
		return this.e;
	}
	
	public ConsoleEvaluationProgressBarDisplayContent()
	{
		// Nur zum Deserialisieren!
	}
	
	public static ConsoleEvaluationProgressBarDisplayContent setJahrersbeginn()
	{
		ConsoleEvaluationProgressBarDisplayContent obj = new ConsoleEvaluationProgressBarDisplayContent();
		
		obj.t = 0;
		obj.a = true;
		obj.e = false;
		
		return obj;
	}
	
	public static ConsoleEvaluationProgressBarDisplayContent setJahrersende()
	{
		ConsoleEvaluationProgressBarDisplayContent obj = new ConsoleEvaluationProgressBarDisplayContent();
		
		obj.t = 0;
		obj.a = false;
		obj.e = true;
		
		return obj;
	}
	
	public static ConsoleEvaluationProgressBarDisplayContent setTag(int tag)
	{
		ConsoleEvaluationProgressBarDisplayContent obj = new ConsoleEvaluationProgressBarDisplayContent();
		
		obj.t = tag;
		obj.a = false;
		obj.e = false;
		
		return obj;
	}
}
