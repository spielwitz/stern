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
public class ScreenDisplayContent implements Serializable
{
	// Ein Client bekommt diese Struktur mitgeteilt. Sie bestimmt, welche
	// Inhalte auf dem Bildschirm dargestellt werden.
	
	// Updates ueber Events. Server erzeugt ein Event -> Clients reagieren darauf -> Redraw
	// Tastendruck -> Event, das der Server auffaengt
	
	// Mit jedem Update der Struktur hat ein Client ALLE Informationen. Inkrementelle
	// Updates gibt es nicht. Somit kann ein Client jederzeit einsteigen.
	
	// Die Clients kennen die Spielregeln nicht. D.h. Namen der Spieler, Planetenkoordinaten
	// usw. müssen den Client mitgegeben werden.

	private ConsoleDisplayContent c;
	private PlanetenlisteDisplayContent p;
	private SpielfeldDisplayContent f;
	private PlanetenEditorDisplayContent e;
	private StatistikDisplayContent t;
	
	private int m;
	private boolean u;
	
	transient public final static int MODUS_SPIELFELD = 0;
	transient public final static int MODUS_PLANETENEDITOR = 1;
	transient public final static int MODUS_STATISTIK = 2;
	transient public final static int MODUS_ENTFERUNGSTABELLE = 3;
	
	public ScreenDisplayContent()
	{
		this.m = MODUS_SPIELFELD;
	}
	
	public int getModus() {
		return m;
	}

	public void setModus(int modus)
	{
		if (modus == MODUS_PLANETENEDITOR || modus == MODUS_STATISTIK || modus == MODUS_ENTFERUNGSTABELLE)
			this.m = modus;
		else
			this.m = MODUS_SPIELFELD;
	}

	public ConsoleDisplayContent getCons() {
		return c;
	}

	public PlanetenlisteDisplayContent getPlaneten() {
		return p;
	}

	public SpielfeldDisplayContent getSpielfeld() {
		return f;
	}
	
	public PlanetenEditorDisplayContent getPlEdit() {
		return e;
	}

	public void setCons(ConsoleDisplayContent cons) {
		this.c = cons;
	}

	public void setPlaneten(PlanetenlisteDisplayContent planeten) {
		this.p = planeten;
	}

	public void setSpielfeld(SpielfeldDisplayContent spielfeld) {
		this.f = spielfeld;
	}

	public void setPlEdit(PlanetenEditorDisplayContent plEdit) {
		this.e = plEdit;
	}
	
	public void switchDisplayMode (int mode)
	{
		this.m = mode;
	}

	public void deletePlEditData()
	{
		this.e = null;
	}

	public StatistikDisplayContent getStatistik() {
		return t;
	}

	public void setStatistik(StatistikDisplayContent statistik) {
		this.t = statistik;
	}
	
	public void setPause(boolean pause)
	{
		this.u = pause;
	}
	
	public boolean getPause()
	{
		return this.u;
	}
}
