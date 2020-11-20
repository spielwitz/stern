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
class Flugobjekt implements Serializable
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
	transient private boolean bewegt;
	private transient Point2D.Double exactPosStart;
	private transient Point2D.Double exactPosZiel;
	
	static final double PRECISION = 0.000001;
	
	Flugobjekt(int spl, int zpl, Point start, Point ziel,
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
	
	Flugobjekt(int spl, int zpl, Point start, Point ziel,
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
	
	void resetNeu()
	{
		this.neu = false;
	}
	
	void resetBewegt()
	{
		this.bewegt = false;
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

	public Point getCurrentField()
	{
		Point2D.Double exactPos = this.getCurrentPos();
		return new Point(Utils.round(exactPos.x), Utils.round(exactPos.y));
	}
	
	public Point2D.Double getCurrentPos()
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
	
	public Point2D.Double getPosOnDay(int tag)
	{
		if (this.bewegt)
		{
			int v = getGeschwindigkeit(this.getTyp(), this.transfer);
			double distTotal = this.start.dist(this.ziel);
			
			// Position am Tag 0
			double pos = (double)(this.pos - v);
			
			if (tag <= 0)
			{
				double x0 = (double)this.start.getX() + pos * (double)(this.ziel.getX()-this.start.getX()) / distTotal;
				double y0 = (double)this.start.getY() + pos * (double)(this.ziel.getY()-this.start.getY()) / distTotal;
				
				return new Point2D.Double(x0, y0);
			}
			
			double bruchteilJahr = AuswertungEreignis.getBruchteilTag(tag);
			
			double posTag = (double)pos +  bruchteilJahr * (double)v;
			
			if (posTag >= distTotal)
			{
				return this.ziel.toPoint2dDouble();
			}
			
			double x = (double)this.start.getX() + posTag * (double)(this.ziel.getX()-this.start.getX()) / distTotal;
			double y = (double)this.start.getY() + posTag * (double)(this.ziel.getY()-this.start.getY()) / distTotal;
			
			return new Point2D.Double(x, y);
		}
		else
			return this.getCurrentPos();
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
		Point2D.Double posNow = this.getCurrentPos();
		Point2D.Double posZiel = this.ziel.toPoint2dDouble();
		
		double dist = Utils.dist(posNow, posZiel);
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
	
//	static int getFlugzeitTage(ObjektTyp typ, boolean transfer, Point startFeld, Point zielFeld)
//	{
//		double dist = startFeld.dist(zielFeld);
//		double v = (double)getGeschwindigkeit(typ, transfer);
//		
//		return Utils.round(dist * (double)Constants.ANZ_TAGE_JAHR / v);
//	}
//	
//	public int getFlugzeitTage()
//	{
//		return getFlugzeitTage(this.typ, this.transfer, this.start, this.ziel);
//	}
//		
//	public int getRestFlugzeitTage()
//	{
//		Point2D.Double posNow = this.getExactPos();
//		Point2D.Double posZiel = this.ziel.toPoint2dDouble();
//		
//		double dist = Utils.dist(posNow, posZiel);
//		double v = (double)getGeschwindigkeit(this.typ, this.transfer);
//		
//		return Utils.round(dist * (double)Constants.ANZ_TAGE_JAHR / v);
//	}
	
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
	
	ArrayList<AuswertungEreignis> bewegen()
	{
		ArrayList<AuswertungEreignis> ereignisse = new ArrayList<AuswertungEreignis>();
		
		double geschwindigkeit = (double)getGeschwindigkeit(this.typ, this.transfer);
		
		// Startpunkt
		Point feldStart = this.getCurrentField();
		this.exactPosStart = this.getCurrentPos();
		
		// Bewegen
		this.pos += geschwindigkeit;
		
		this.angekommen = (this.pos >= this.start.dist(this.ziel));
		this.bewegt = true;
		
		// Zielpunkt
		Point feldZiel = this.getCurrentField();
		this.exactPosZiel = this.getCurrentPos();
		
		double distJahr = this.angekommen ? 
					Utils.dist(exactPosZiel, exactPosStart) :
					geschwindigkeit;
		
		double bruchteilJahrZiel = distJahr / geschwindigkeit;
		
		if (this.angekommen)
		{
			// Objekt ist angekommen.
			AuswertungEreignis ereignis = new AuswertungEreignis(AuswertungEreignisTyp.ANKUNFT, this);
			
			ereignis.feld = this.ziel;
			ereignis.setTag(bruchteilJahrZiel);
			
			ereignisse.add(ereignis);
		}

		if (this.typ != ObjektTyp.RAUMER && this.typ != ObjektTyp.MINENRAEUMER)
			return ereignisse;
		
		// -------------------
		// Felder, ueber die ein Objekt fliegt, ermittln. Das Startfeld ist NICHT mit dabei.
		// Die Ereignisse sind noch nicht zeitlich geordnet!
		if (!feldStart.equals(feldZiel))
		{
			int xMin = Math.min(feldStart.getX(), feldZiel.getX());
			int yMin = Math.min(feldStart.getY(), feldZiel.getY());
			
			int xMax = Math.max(feldStart.getX(), feldZiel.getX());
			int yMax = Math.max(feldStart.getY(), feldZiel.getY());
			
			// Pruefe alle Felder im Rechteck xmin, ymin, xmax, ymax, ob sie
			// durchflogen werden
			for (int xCheck = xMin; xCheck <= xMax; xCheck++)
			{
				for (int yCheck = yMin; yCheck <= yMax; yCheck++)
				{
					// Startfeld ausschliessen
					if (xCheck == feldStart.getX() && yCheck == feldStart.getY())
						continue;
					
					// Pruefe, auf welcher Seite der Geraden die 4 Ecken des Feldes liegen
					Point2D.Double pts[] = new Point2D.Double[4];
					
					pts[0] = new Point2D.Double(xCheck + 0.5, yCheck+0.5);
					pts[1] = new Point2D.Double(xCheck + 0.5, yCheck-0.5);
					pts[2] = new Point2D.Double(xCheck - 0.5, yCheck-0.5);
					pts[3] = new Point2D.Double(xCheck - 0.5, yCheck+0.5);
					
					double vp[] = new double[4];
					
					for (int i = 0; i < 4; i++)
						vp[i] = Utils.VektorproduktBetrag(exactPosStart, exactPosZiel, pts[i]); 
					
					boolean found = false;
					double tMin = 0.;
					
					// Liegen wenigstens zwei Punkte auf unterschiedlichen Seiten der Geraden?
					double vpMax = Math.max(vp[0], Math.max(vp[1], Math.max(vp[2], vp[3])));				
					double vpMin = Math.min(vp[0], Math.min(vp[1], Math.min(vp[2], vp[3])));
					
					if (!(vpMin <= -0.000001 && vpMax >= 0.000001))
						continue;
					
					// Pruefe, wann die Kanten des Feldes die Gerade schneiden
					for (int i = 0; i < 4; i++)
					{
						int j = (i + 1) % 4; 
						
						// Ist die Kante i-j parallel zur Geraden?
						double vprod = Utils.VektorproduktBetrag(
								new Point2D.Double(0, 0),
								new Point2D.Double(pts[j].x - pts[i].x, pts[j].y - pts[i].y),
								new Point2D.Double(this.exactPosZiel.x - this.exactPosStart.x, this.exactPosZiel.y - this.exactPosStart.y));
						
						if (Math.abs(vprod) < PRECISION)
							continue;

						// Liegt der erste Eckpunkt auf der Geraden?
						if (Math.abs(vp[i]) < PRECISION)
						{
							double t = Utils.dist(pts[i], this.exactPosStart) / distJahr;
							
							if (t >= 0 && (!found || t < tMin))
							{
								found = true;
								tMin = t;
							}
						}
						else if (Math.abs(vp[j]) >= PRECISION &&
								 Math.signum(vp[i]) != Math.signum(vp[j]))
					    {
							double t; 
							
							if (Math.abs(pts[i].x - pts[j].x) < PRECISION)
								// X-Kante des Feldes
								t =  (pts[i].x - this.exactPosStart.x) / 
								     (this.exactPosZiel.x - this.exactPosStart.x);
							else
								// Y-Kante des Feldes
								t =  (pts[i].y - this.exactPosStart.y) / 
							     	 (this.exactPosZiel.y - this.exactPosStart.y);
							
							if (t >= 0 && (!found || t < tMin))
							{
								found = true;
								tMin = t;
							}
					    }
					}
					
					if (found)
					{
						// Das Feld xCheck/yCheck wird durchflogen 
						// -> Ereignis daraus machen.
						AuswertungEreignis feldGrenzeEreignis = 
								new AuswertungEreignis(AuswertungEreignisTyp.SEKTOR_BETRETEN, this);
						
						feldGrenzeEreignis.feld = new Point(xCheck, yCheck);
						feldGrenzeEreignis.setTag(tMin * bruchteilJahrZiel);
						
						ereignisse.add(feldGrenzeEreignis);
					}
				}
			}
		}
		
		return ereignisse;
	}
		
	boolean istAngekommen() {
		return this.angekommen;
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
		// Transfer-Kennzeichen unveraendert lassen
		this.wenden(this.transfer);
	}
	void wenden(boolean transfer)
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
		this.bewegt = false;
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
	
	void gekapert(int neuerBesitzer, Point start)
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
	
	Point getFeldTag(int tag)
	{
		// Berechne, auf welchem Feld sich das Flugobjekt an einem bestimmten Tag befindet
		double bruchteilJahr = AuswertungEreignis.getBruchteilTag(tag);
		
		int x = Utils.round(bruchteilJahr * (this.exactPosZiel.x - this.exactPosStart.x) + this.exactPosStart.x);
		int y = Utils.round(bruchteilJahr * (this.exactPosZiel.y - this.exactPosStart.y) + this.exactPosStart.y);
		
		return new Point(x, y);
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
