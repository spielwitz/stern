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

import java.io.Serializable;

@SuppressWarnings("serial") class ScreenContentStatistics implements Serializable
{
	private long dateStart;
	private String title;
	private Player[] players;
	private int[][] values;
	private int[][] championsPerYear;
	private int years;
	private int valueMax;
	private int valueMaxYear;
	private int valueMaxPlayerIndex;
	private int valueMin;
	private int valueMinYear;
	private int valueMinPlayerIndex;
	private int selectedYearIndex;
	private boolean modeScore;
	
	public long getDateStart()
	{
		return this.dateStart;
	}

	public String getTitle() {
		return title;
	}

	public Player[] getPlayers() {
		return players;
	}

	public int[][] getValues() {
		return values;
	}
	
	public int[][] getChampionsPerYear()
	{
		return championsPerYear;
	}

	public int getSelectedYearIndex()
	{
		return this.selectedYearIndex;
	}

	public int getValueMax() {
		return valueMax;
	}

	public int getValueMaxYear() {
		return valueMaxYear;
	}

	public int getMaxValuePlayerIndex() {
		return valueMaxPlayerIndex;
	}

	public int getValueMin() {
		return valueMin;
	}

	public int getValueMinYear() {
		return valueMinYear;
	}

	public int getMinValuePlayerIndex() {
		return valueMinPlayerIndex;
	}

	public boolean isModeScore()
	{
		return modeScore;
	}
	
	public int getYears()
	{
		return years;
	}

	ScreenContentStatistics(
			long dateStart,
			String title, 
			Player[] players, 
			int[][] values, 
			int[][] championsPerYear,
			int years, 
			int valueMax,
			int valueMaxYear, 
			int valueMaxPlayerIndex, 
			int valueMin, 
			int valueMinYear,
			int valueMinPlayerIndex, 
			int selectedYearIndex, 
			boolean modeScore)
	{
		super();
		this.dateStart = dateStart;
		this.title = title;
		this.players = players;
		this.values = values;
		this.championsPerYear = championsPerYear; 
		this.years = years;
		this.valueMax = valueMax;
		this.valueMaxYear = valueMaxYear;
		this.valueMaxPlayerIndex = valueMaxPlayerIndex;
		this.valueMin = valueMin;
		this.valueMinYear = valueMinYear;
		this.valueMinPlayerIndex = valueMinPlayerIndex;
		this.selectedYearIndex = selectedYearIndex;
		this.modeScore = modeScore;
	}
}
