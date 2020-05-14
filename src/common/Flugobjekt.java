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

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("serial")
public class Flugobjekt implements Serializable
{
	// Felder, die abgespeichert werden
	private int spl; // Startplanet. Ist Constants.KEIN_PLANET, wenn Objekt von einem Feld abfliegt
	private int zpl; // Zielplanet. Ist Constants.KEIN_PLANET, wenn Objekt zu einem Feld fliegt
	private Point start; // Startpunkt. Immer gesetzt
	private Point ziel; // Zielpunkt. Immer gesetzt
	
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
	transient private boolean angekommen;
	transient private boolean zuLoeschen;
	
	public Flugobjekt(int spl, int zpl, Point start, Point ziel,
			int pos, ObjektTyp typ,
			int anz, int bes, boolean transfer,
			Buendnis buendnis, Kommandozentrale kz)
	{
		this.spl = spl;
		this.zpl = zpl;
		this.start = start;
		this.ziel = ziel;
		this.pos = pos;
		this.typ = typ;
		this.anz = anz;
		this.bes = bes;
		this.transfer = transfer;
		this.kz = kz;
		this.angekommen = false;
		this.zuLoeschen = false;
		this.buendnis = buendnis;
	}
	
	public Flugobjekt(int spl, int zpl, Point start, Point ziel,
			ObjektTyp typ,
			int anz, int bes, boolean transfer, boolean neu, 
			Buendnis buendnis, Kommandozentrale kz)
	{
		this.spl = spl;
		this.zpl = zpl;
		this.start = start;
		this.ziel = ziel;
		this.pos = 0;
		this.typ = typ;
		this.anz = anz;
		this.bes = bes;
		this.transfer = transfer;
		this.neu = neu;
		this.kz = kz;
		this.angekommen = false;
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
	
	public void resetNeu()
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
		return start;
	}

	public void setStart(Point start) {
		// sPl auf -1 setzen
		this.start = start;
	}

	public Point getZiel() {
		return ziel;
	}

	public void setZiel(Point ziel) {
		// zPl auf -1 setzen
		this.ziel = ziel;
	}

	public int getPos() {
		return pos;
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

	public int getRaumerProSpieler(int spieler)
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

	public Point getCurrentField()
	{
		Point2D.Double exactPos = this.getExactPos();
		return new Point(Utils.round(exactPos.x), Utils.round(exactPos.y));
	}
	
	public Point2D.Double getExactPos()
	{
		double dist = this.start.dist(this.ziel);
		
		if (this.pos < dist)
		{
			double x0 = (double)this.start.getX() + (double)this.pos * (double)(this.ziel.getX()-this.start.getX()) / dist;
			double y0 = (double)this.start.getY() + (double)this.pos * (double)(this.ziel.getY()-this.start.getY()) / dist;
			return new Point2D.Double(x0, y0);
		}
		else	
			return new Point2D.Double(this.ziel.getX(), this.ziel.getY());
	}

	public Kommandozentrale getKz() {
		return kz;
	}
	
	public static int getFlugdauer(ObjektTyp typ, boolean transfer, Point startFeld, Point zielFeld)
	{
		double dist = startFeld.dist(zielFeld);
		double v = (double)getGeschwindigkeit(typ, transfer);
		double a = dist / v;
		
		return (int)Math.ceil(a) - 1;
	}
	
	public int getAnkunftsjahr()
	{ 
		int dauerGesamt = Flugobjekt.getFlugdauer(
							this.typ,
							this.transfer,
							this.start,
							this.ziel);
		
		return Math.max(0, dauerGesamt - this.pos / getGeschwindigkeit(this.typ, this.transfer));
	}
	
	public int getRestflugzeit()
	{
		Point2D.Double posNow = this.getExactPos();
		double dist = Math.sqrt(Math.pow((double)this.ziel.getX() - posNow.x, 2) + Math.pow((double)this.ziel.getY() - posNow.y, 2));
		double v = (double)getGeschwindigkeit(this.typ, this.transfer);
		
		double a = dist / v;
		
		return (int)Math.ceil(a) - 1;
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
	
	public ArrayList<Point> bewegen()
	{
		ArrayList<Point> felder = new ArrayList<Point>();
		
		// Startpunkt
		Point feldStart = this.getCurrentField();
		Point2D.Double exactPosStart = this.getExactPos();
		
		// Bewegen
		this.pos += getGeschwindigkeit(this.typ, this.transfer);
		
		// Zielpunkt
		Point feldZiel = this.getCurrentField();
		Point2D.Double exactPosZiel = this.getExactPos();
		
		if (this.typ == ObjektTyp.SCHWARZES_LOCH ||
			this.pos >= this.start.dist(this.ziel))
		{
			// Objekt ist angekommen. Das Schwarze Loch kommt immer an :-)
			feldZiel = this.ziel;
			exactPosZiel = this.getExactPos();
			this.angekommen = true;
		}
		
		// Pruefe alle Felder zwischen Start und Zielfeld, ob sie vom Objekt durchflogen werden
		// Fuege schon mal das Startfeld hinzu
		felder.add(feldStart);
		
		if (!feldStart.equals(feldZiel))
		{
			int deltaX = Math.abs(feldZiel.getX() - feldStart.getX());
			int deltaY = Math.abs(feldZiel.getY() - feldStart.getY());
			int dx = (int)Math.signum(feldZiel.getX() - feldStart.getX());
			int dy = (int)Math.signum(feldZiel.getY() - feldStart.getY());
			
			int counter = 0;
			Point feldCheck = null;
			
			do
			{
				counter++;
				
				int xCheck = 0;
				int yCheck = 0;
				
				if (dx == 0)
				{
					xCheck = feldStart.getX();
					yCheck = feldStart.getY() + dy * counter;
				}
				else if (dy == 0)
				{
					xCheck = feldStart.getX() + dx * counter;
					yCheck = feldStart.getY();
				}
				else if (deltaX > deltaY)
				{
					xCheck = feldStart.getX() + dx * (counter / (1+Math.abs(feldZiel.getX() - feldStart.getX())));
					yCheck = feldStart.getY() + dy * (counter % (1+Math.abs(feldZiel.getX() - feldStart.getX())));
				}
				else
				{
					xCheck = feldStart.getX() + dx * (counter % (1+Math.abs(feldZiel.getX() - feldStart.getX())));
					yCheck = feldStart.getY() + dy * (counter / (1+Math.abs(feldZiel.getX() - feldStart.getX())));
				}
				 
				feldCheck = new Point(xCheck, yCheck);
				
				if (feldCheck.equals(feldZiel))
				{
					// Wenn wir das Zielfeld erreicht haben, koennen wir aufhoeren
					felder.add(feldZiel);
					break;
				}
				
				// Pruefe, ob die Ecken des Feldes an unterschiedlichen Seiten der Geraden liegen
				double vp1 = Utils.VektorproduktBetrag(
						exactPosStart, exactPosZiel, new Point2D.Double(xCheck + 0.5, yCheck+0.5));
				double vp2 = Utils.VektorproduktBetrag(
						exactPosStart, exactPosZiel, new Point2D.Double(xCheck + 0.5, yCheck-0.5));
				double vp3 = Utils.VektorproduktBetrag(
						exactPosStart, exactPosZiel, new Point2D.Double(xCheck - 0.5, yCheck-0.5));
				double vp4 = Utils.VektorproduktBetrag(
						exactPosStart, exactPosZiel, new Point2D.Double(xCheck - 0.5, yCheck+0.5));
				
				double max = Math.max(vp1, Math.max(vp2, Math.max(vp3, vp4)));				
				double min = Math.min(vp1, Math.min(vp2, Math.min(vp3, vp4)));
				
				if (min <= -0.000001 && max >= 0.000001)
					felder.add(feldCheck);
				
			}	while (true);
		}
		
		return felder;
	}
	
	public boolean istAngekommen() {
		return this.angekommen;
	}
	
	public boolean istBuendnis()
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
	
	public boolean istBuendnisMitglied(int spieler)
	{
		if (this.buendnis == null)
			return false;
		else
			return this.buendnis.istMitglied(spieler);
	}
	
	public boolean istBeteiligt(int spieler)
	{
		if (this.bes == spieler)
			return true;
		else
			return this.istBuendnisMitglied(spieler);
	}

	public void setZuLoeschen() {
		this.zuLoeschen = true;
	}
	
	public boolean istZuLoeschen()
	{
		return this.zuLoeschen;
	}
	
	public void wenden()
	{
		// Transfer-Kennzeichen unveraendert lassen
		this.wenden(this.transfer);
	}
	public void wenden(boolean transfer)
	{
		// Patrouille oder aehnlich dreht um
		int dummyPl = this.spl;
		Point dummyPoint = this.start.klon();
		
		this.spl = this.zpl;
		this.zpl = dummyPl;
		this.start = this.ziel;
		this.ziel = dummyPoint;
		this.pos = 0;
		this.transfer = transfer;
		
		this.angekommen = false;
		this.zuLoeschen = false;
	}

	public void subtractRaumer(int anz, int bevorzugterSpieler)
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
	
	public void gekapert(int neuerBesitzer, Point start)
	{
		this.bes = neuerBesitzer;
		this.pos = 0;
		this.transfer = true;
		this.start = start;
		this.spl = Constants.KEIN_PLANET;
		
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
	
	public int spielerwechsel (int alterSpieler, int neuerSpieler)
	{
		int weitererEroberterSpieler = Constants.BESITZER_NEUTRAL;
		
		if (this.zuLoeschen ||
			this.typ == ObjektTyp.SCHWARZES_LOCH)
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
		if (this.istBeteiligt(alterSpieler))
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
}
