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
import java.util.UUID;

@SuppressWarnings("serial") 
class Flugobjekt implements Serializable
{
	// Felder, die abgespeichert werden
	private int spl; // Startplanet. Ist Constants.KEIN_PLANET, wenn Objekt von einem Feld abfliegt
	private int zpl; // Zielplanet. Ist Constants.KEIN_PLANET, wenn Objekt zu einem Feld fliegt
	private Point s; // Startpunkt. Immer gesetzt
	private Point z; // Zielpunkt. Immer gesetzt
	
	private int pos; // Positionszaehler
	private ObjektTyp typ; // Typ des Flugobjekts
	private int anz; // Anzahl
	private int bes; // Besitzer
	private boolean transfer; // nur fuer Patrouillen oder Minen(raeumer): Objekt fliegt nur von Planet zu Planet
	private boolean neu; // gerade erst gestartet. Wird nicht angezeigt.
	private UUID stopLabel; // Wenn ungleich null, dann ist das Objekt gestoppt. Entweder ein gekapertes Objekt oder Raumer, dessen Buendnis gekuendigt wurden.
	private Kommandozentrale kz;
	
	private Buendnis buendnis; // Darf keinesfalls von aussen manipuliert werden, da sonst nicht mehr sychron mit "anz"
	
	// Felder, die zur Laufzeit berechnet werden
	transient private boolean zuLoeschen;
	transient private boolean wenden;
	
	Flugobjekt(int spl, int zpl, Point start, Point ziel,
			int pos, ObjektTyp typ,
			int anz, int bes, boolean transfer,
			Buendnis buendnis, Kommandozentrale kz)
	{
		this.spl = spl;
		this.zpl = zpl;
		this.s = start;
		this.z = ziel;
		this.pos = pos;
		this.typ = typ;
		this.anz = anz;
		this.bes = bes;
		this.transfer = transfer;
		this.kz = kz;
		this.zuLoeschen = false;
		this.buendnis = buendnis;
	}
	
	Flugobjekt(int spl, int zpl, Point start, Point ziel,
			ObjektTyp typ,
			int anz, int bes, boolean transfer, boolean neu, 
			Buendnis buendnis, Kommandozentrale kz)
	{
		this.spl = spl;
		this.zpl = zpl;
		this.s = start;
		this.z = ziel;
		this.pos = 0;
		this.typ = typ;
		this.anz = anz;
		this.bes = bes;
		this.transfer = transfer;
		this.neu = neu;
		this.kz = kz;
		this.zuLoeschen = false;
		this.buendnis = buendnis;
	}

	public int getSpl() {
		return spl;
	}

	public void setSpl(int spl) {
		// Startfeld synchron halten
		this.spl = spl;
	}
	
	public boolean getNeu()
	{
		return this.neu;
	}
	
	void resetNeu()
	{
		this.neu = false;
	}
	
	public int getZpl() {
		return zpl;
	}

	public void setZpl(int zpl) {
		// Zielfeld synchron halten
		this.zpl = zpl;
	}

	public Point getStart() {
		return s;
	}

	public void setStart(Point start) {
		// sPl auf -1 setzen
		this.s = start;
	}

	public Point getZiel() {
		return z;
	}

	public void setZiel(Point ziel) {
		// zPl auf -1 setzen
		this.z = ziel;
	}

	public int getPos() {
		return pos;
	}
	
	public void incPos()
	{
		this.pos += getGeschwindigkeit(this.typ, this.transfer);
	}

	public int getAnz() {
		return anz;
	}

	public void setAnz(int anz) {
		// ==== Vorsicht bei der  Getter-Methode fuer "anz":
		// ==== Bei Raumern auf "anz" gehen, wenn kein Buendnis vorliegt und auf "buendnisRaumer",
		// ==== wenn ein Buendnis existiert
		this.anz = anz;
	}
	
	public boolean istZuWenden()
	{
		return this.wenden;
	}
	
	public void setZuWenden()
	{
		this.wenden = true;
	}

	int getRaumerProSpieler(int spieler)
	{
		if (this.typ != ObjektTyp.RAUMER)
			return 0;
		
		if (this.buendnis == null)
		{
			if (spieler == this.bes)
				return this.anz;
			else
				return 0;
		}
		else
			return this.buendnis.getAnz(spieler);
	}
	
	public ObjektTyp getTyp() {
		return typ;
	}

	public int getBes() {
		return bes;
	}

	public boolean isTransfer() {
		return transfer;
	}
	
	public boolean isStop()
	{
		return this.stopLabel != null;
	}
	
	public UUID getStopLabel()
	{
		return this.stopLabel;
	}
	
	public void setStopLabel(UUID stopLabel)
	{
		this.stopLabel = stopLabel; 
	}
	
	public void setStop(boolean stop)
	{
		if (stop)
			this.stopLabel = UUID.randomUUID();
		else
			this.stopLabel = null;
	}
	
	public Point getPositionOnDay(int day)
	{
		double distTotal = this.s.dist(this.z);
		
		if (distTotal < Constants.PRECISION)
		{
			return this.s;
		}
		
		// Position am Tag 0
		if (day <= 0)
		{
			double x0 = this.s.getX() + this.pos * (this.z.getX()-this.s.getX()) / distTotal;
			double y0 = this.s.getY() + this.pos * (this.z.getY()-this.s.getY()) / distTotal;
			
			return new Point(x0, y0);
		}
		
		int v = getGeschwindigkeit(this.getTyp(), this.transfer);
		
		double bruchteilJahr = Flugobjekt.getYearFraction(day);
		
		double posTag = (double)pos +  bruchteilJahr * (double)v;
		
		if (posTag > distTotal - Constants.PRECISION)
		{
			return this.z;
		}
		
		double x = this.s.getX() + posTag * (this.z.getX()-this.s.getX()) / distTotal;
		double y = this.s.getY() + posTag * (this.z.getY()-this.s.getY()) / distTotal;
		
		return new Point(x, y);
	}
	
	public Point getSectorOnDay(int day)
	{
		Point pos = this.getPositionOnDay(day);
		
		// Befinden wir uns auf einer Sektorengrenze?
		double fractionX = Math.abs(pos.x - (double)((int)pos.x) - 0.5);
		double fractionY = Math.abs(pos.y - (double)((int)pos.y) - 0.5);
		
		if (fractionX < Constants.PRECISION || fractionY < Constants.PRECISION)
		{
			return null;
		}
		else
		{
			return new Point(
					Utils.round(pos.x),
					Utils.round(pos.y));
		}
	}
	
	public Kommandozentrale getKz() {
		return kz;
	}
	
	static Flugzeit getFlugzeit(ObjektTyp typ, boolean transfer, Point startFeld, Point zielFeld)
	{
		double dist = startFeld.dist(zielFeld);
		double v = (double)getGeschwindigkeit(typ, transfer);
		
		return getFlugzeitInternal(dist, v);
	}
	
	Flugzeit getRestflugzeit()
	{
		Point posNow = this.getPositionOnDay(0);
		
		double dist = posNow.dist(this.z);
		double v = (double)getGeschwindigkeit(this.typ, this.transfer);
		
		return getFlugzeitInternal(dist, v);
	}
	
	private static Flugzeit getFlugzeitInternal(double dist, double v)
	{
		double bruchteilJahr = dist/v;
		
		int tage = Utils.round(bruchteilJahr * (double)Constants.ANZ_TAGE_JAHR);
		
		// Achtung: Sonderfall bei Tagen, die durch 365 teilbar sind. Diese zaehlen zum vorherigen
		// Jahr!
		if (tage % Constants.ANZ_TAGE_JAHR == 0)
			// Hier kommt sowas wie 1;365 zurueck!
			return new Flugzeit(tage / Constants.ANZ_TAGE_JAHR - 1, Constants.ANZ_TAGE_JAHR);
		else
			return new Flugzeit(tage / Constants.ANZ_TAGE_JAHR, tage % Constants.ANZ_TAGE_JAHR);
	}
	
	private static int getGeschwindigkeit(ObjektTyp typ, boolean transfer)
	{
		int v = Constants.GESCHWINDIGKEIT_NORMAL;
		
		if (typ == ObjektTyp.AUFKLAERER)
			v = Constants.GESCHWINDIGKEIT_SCHNELL;
		else if (typ == ObjektTyp.MINENRAEUMER && !transfer)
			v = Constants.GESCHWINDIGKEIT_LANGSAM;
		else if (typ == ObjektTyp.PATROUILLE && !transfer)
			v = Constants.GESCHWINDIGKEIT_LANGSAM;
		
		return v;
	}
		
	boolean istBuendnis()
	{
		return (this.buendnis != null);
	}
	
	public boolean[] getBuendnisMitglieder()
	{
		if (this.buendnis != null)
			return this.buendnis.getMitglieder();
		else
			return null;
	}
	
	boolean istBuendnisMitglied(int spieler)
	{
		if (this.buendnis == null)
			return false;
		else
			return this.buendnis.istMitglied(spieler);
	}
	
	boolean istBeteiligt(int spieler)
	{
		if (this.bes == spieler)
			return true;
		else
			return this.istBuendnisMitglied(spieler);
	}

	void setZuLoeschen() {
		this.zuLoeschen = true;
	}
	
	boolean istZuLoeschen()
	{
		return this.zuLoeschen;
	}
	
	void wenden()
	{
		// Patrouille oder Minenraeumer dreht um
		int dummyPl = this.spl;
		Point dummyPoint = this.s.klon();
		
		this.spl = this.zpl;
		this.zpl = dummyPl;
		this.s = this.z;
		this.z = dummyPoint;
		this.pos = 0;
		
		this.transfer = (this.typ == ObjektTyp.MINENRAEUMER);
		
		this.zuLoeschen = false;
		this.wenden = false;
	}

	void subtractRaumer(int anz, int bevorzugterSpieler)
	{
		if (this.typ != ObjektTyp.RAUMER)
			return;
		
		// Reduziere Raumerflotte um eine bestimmte Menge
		// Anwendungsfaelle:
		// - Eine Raumerflotte hat einen Planeten erobert
		// - Eine Raumerflotte ist auf eine Mine gelaufen
		
		if (this.buendnis == null)
			this.anz -= anz;
		else
		{
			this.buendnis.subtract(anz, bevorzugterSpieler);
			this.anz = this.buendnis.getSum();
		}
	}
	
	void gekapert(int neuerBesitzer, Point pos)
	{
		this.bes = neuerBesitzer;
		this.pos = 0;
		this.transfer = true;
		this.z   = pos;
		this.s	 = pos;
		this.spl = Constants.KEIN_PLANET;
		this.zpl = Constants.KEIN_PLANET;
		
		// Objekt wird gestoppt
		this.setStop(true);
		
		if (this.buendnis != null)
		{
			// Buendnis wird beim kapern aufgeloest. Alle Raumer gehoeren dem neuen Besitzer
			int anz = this.getAnz();
			
			this.buendnis = null;
			this.anz = anz;
		}
	}
	
	int spielerwechsel (int alterSpieler, int neuerSpieler)
	{
		int weitererEroberterSpieler = Constants.BESITZER_NEUTRAL;
		
		if (this.zuLoeschen)
			return weitererEroberterSpieler;

		if (this.bes == alterSpieler && this.typ == ObjektTyp.KAPITULATION)
		{
			this.zuLoeschen = true;
			return weitererEroberterSpieler;
		}

		// Neuer Besitzer Ausserirdische: Alle Objekte, die unter dem Kommando des
		// alten Besitzers flogen, loeschen.
		if (this.bes == alterSpieler && neuerSpieler == Constants.BESITZER_NEUTRAL)
		{
			this.zuLoeschen = true;
			
			if (this.kz != null && this.kz.getSp() != alterSpieler)
			{
				// Alter Besitzer hatte einen Transporter mit einer fremden Kommandozentrale an Bord
				weitererEroberterSpieler = this.kz.getSp();
			}

			return weitererEroberterSpieler;
		}
		
		// Ist auf dem Transporter eines anderen Spielers die Kommandozentrale des
		// Spielers, der ersetzt werden soll?
		if (this.kz != null && this.kz.getSp() == alterSpieler)
			this.kz = null;
			

		// Beteiligungen bei Buendnisflotten aendern
		if (this.istBuendnis() && this.istBeteiligt(alterSpieler))
		{
			this.buendnis.spielerErsetzen(this.bes, neuerSpieler);
			this.anz = this.buendnis.getSum();
			
			if (this.buendnis.getAnzahlMitglieder() <= 1)
				this.buendnis = null;
			
			if (this.anz <= 0)
			{
				this.zuLoeschen = true;
				return weitererEroberterSpieler;
			}
		}
				
		// Besitzer wechseln
		if (this.bes == alterSpieler)
		{
			this.bes = neuerSpieler;
		}
		
		return weitererEroberterSpieler;
	}
	
	public void setBuendnis(Buendnis buendnis)
	{
		this.buendnis = buendnis;
	}
	
	private static double getYearFraction(int day)
	{
		return (double)day / (double)Constants.ANZ_TAGE_JAHR;
	}
	
	byte getScreenDisplaySymbol()
	{
		switch (this.typ)
		{
		case RAUMER:
			return 1;
		case AUFKLAERER:
			return 2;
		case PATROUILLE:
			return 3;
		case TRANSPORTER:
			return 4;
		case MINE50:
		case MINE100:
		case MINE250:
		case MINE500:
			return 5;
		case MINENRAEUMER:
			return 6;
		default:
			return 0;
		}
	}
}
