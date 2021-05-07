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
import java.util.HashSet;
import java.util.Hashtable;

@SuppressWarnings("serial") 
class ScreenContentPlanetEditor implements Serializable
{
	private ShipType typeHighlighted;
	private Hashtable<ShipType,String> count;
	private Hashtable<ShipType, Integer> pricesBuy;
	private Hashtable<ShipType, Integer> pricesSell;
	private HashSet<ShipType> buyImpossible;
	private HashSet<ShipType> sellImpossible;
	
	private boolean readOnly;
	private byte colorIndex;
	private int moneySupply;
	
	ScreenContentPlanetEditor(
			ShipType typeHighlighted,
			Hashtable<ShipType, String> count,
			Hashtable<ShipType, Integer> pricesBuy,
			Hashtable<ShipType, Integer> pricesSell,
			HashSet<ShipType> buyImpossible,
			HashSet<ShipType> sellImpossible,
			byte colorIndex,
			int moneySupply,
			boolean readOnly) 
	{
		super();
		this.typeHighlighted = typeHighlighted;
		this.count = count;
		this.pricesBuy = pricesBuy;
		this.pricesSell = pricesSell;
		this.buyImpossible = buyImpossible;
		this.sellImpossible = sellImpossible;
		this.colorIndex = colorIndex;
		this.moneySupply = moneySupply;
		this.readOnly = readOnly;
	}

	public ShipType getTypeHighlighted() {
		return typeHighlighted;
	}

	public boolean isReadOnly()
	{
		return this.readOnly;
	}
	
	public Hashtable<ShipType, String> getCount() {
		return count;
	}

	public HashSet<ShipType> getBuyImpossible() {
		return buyImpossible;
	}


	public HashSet<ShipType> getSellImpossible() {
		return sellImpossible;
	}


	public byte getColorIndex() {
		return colorIndex;
	}


	public int getMoneySupply() {
		return moneySupply;
	}


	public int getPriceBuy(ShipType shipType) 
	{
		return this.pricesBuy.get(shipType);
	}
	
	public int getPriceSell(ShipType shipType) 
	{
		return this.pricesSell.get(shipType);
	}
}
