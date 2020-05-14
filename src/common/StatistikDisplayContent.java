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

@SuppressWarnings("serial")
public class StatistikDisplayContent implements Serializable
{
	private int spieldauerSekunden;
	private long startDatum;
	private String titel;
	private Spieler[] spieler;
	private int[][] werte;
	private int[][] championsJahr;
	private int jahre;
	private int maxWert;
	private int maxWertJahr;
	private int maxWertSpieler;
	private int minWert;
	private int minWertJahr;
	private int minWertSpieler;
	private int markiertesJahrIndex;
	private boolean modusPunkte;
	
	public String getSpieldauer()
	{
		StringBuilder sb = new StringBuilder();
		
		int std = this.spieldauerSekunden / 3600;
		int min = (this.spieldauerSekunden - std * 3600) / 60;
		
		sb.append(std);
		sb.append(" "+SternResources.StatistikStunden(false)+" ");
		
		sb.append(min);
		sb.append(" "+SternResources.StatistikMinuten(false));		
		
		return sb.toString();
	}

	public long getStartDatum()
	{
		return this.startDatum;
	}

	public String getTitel() {
		return titel;
	}

	public Spieler[] getSpieler() {
		return spieler;
	}

	public int[][] getWerte() {
		return werte;
	}
	
	public int[][] getChampionsJahr()
	{
		return championsJahr;
	}

	public int getMarkiertesJahrIndex()
	{
		return this.markiertesJahrIndex;
	}

	public int getMaxWert() {
		return maxWert;
	}

	public int getMaxWertJahr() {
		return maxWertJahr;
	}

	public int getMaxWertSpieler() {
		return maxWertSpieler;
	}

	public int getMinWert() {
		return minWert;
	}

	public int getMinWertJahr() {
		return minWertJahr;
	}

	public int getMinWertSpieler() {
		return minWertSpieler;
	}

	public boolean isModusPunkte()
	{
		return modusPunkte;
	}
	
	public int getJahre()
	{
		return jahre;
	}

	public StatistikDisplayContent(int spieldauerSekunden, long startDatum,
			String titel, Spieler[] spieler, int[][] werte, int[][] championsJahr,
			int jahre, int maxWert,
			int maxWertJahr, int maxWertSpieler, int minWert, int minWertJahr,
			int minWertSpieler, int markiertesJahrIndex, boolean modusPunkte) {
		super();
		this.spieldauerSekunden = spieldauerSekunden;
		this.startDatum = startDatum;
		this.titel = titel;
		this.spieler = spieler;
		this.werte = werte;
		this.championsJahr = championsJahr; 
		this.jahre = jahre;
		this.maxWert = maxWert;
		this.maxWertJahr = maxWertJahr;
		this.maxWertSpieler = maxWertSpieler;
		this.minWert = minWert;
		this.minWertJahr = minWertJahr;
		this.minWertSpieler = minWertSpieler;
		this.markiertesJahrIndex = markiertesJahrIndex;
		this.modusPunkte = modusPunkte;
	}
}
