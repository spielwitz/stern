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
import java.util.Hashtable;

@SuppressWarnings("serial")
public class Planet implements Serializable
{
	private Point pos; // Koordinaten des Planeten
	private Buendnis buendnis; // Nicht von aussen manipulieren, da sonst nicht mehr synchron mit "anz"
	private Hashtable<ObjektTyp,Integer> anz; // Anzahl Objekte nach Typ
	private int bes;
	private Festung festung;
	private int evorrat;
	private int eprod;
	private int eraum;
	private Kommandozentrale kz;
	private Hashtable<Integer,Integer> sender; // Spionagesender. Schluessel ist Spieler, Wert ist Jahr wenn Sender zum letzten Mal sendet
	
	public Planet(Point pos, Buendnis buendnis,
			Hashtable<ObjektTyp, Integer> anz, int bes, Festung festung,
			int evorrat, int eprod, int eraum, Kommandozentrale kz)
	{
		super();
		this.pos = pos;
		this.buendnis = buendnis;
		this.anz = anz;
		this.bes = bes;
		this.festung = festung;
		this.evorrat = evorrat;
		this.eprod = eprod;
		this.eraum = eraum;
		this.kz = kz;
	}
	
	// ==== Keine Getter-Methode fuer "anz" machen, sondern Methode nach Typ anbieten,
	// ==== die bei Raumern auf "anz" geht, wenn kein Buendnis vorliegt und auf "buendnisRaumer",
	// ==== wenn ein Buendnis existiert
	
	public int getAnz(ObjektTyp typ)
	{
		Integer anz = this.anz.get(typ);
		
		if (anz != null)
			return anz.intValue();
		else
			return 0;
	}
	
	public int getRaumerProSpieler(int spieler)
	{
		if (!this.istBuendnis())
		{
			if (spieler == this.bes)
				return this.getAnz(ObjektTyp.RAUMER);
			else
				return 0;
		}
		else
			return this.buendnis.getAnz(spieler);
	}
	
	public int getAnzProTypUndSpieler(ObjektTyp typ, int spieler)
	{
		if (typ == ObjektTyp.RAUMER && this.istBuendnis())
			return this.buendnis.getAnz(spieler);
		else if (this.bes == spieler)
			return this.getAnz(typ);
		else
			return 0;
	}
	
	public void incRaumerProSpieler(int spieler, int anz)
	{
		if (this.istBuendnis())
		{
			this.buendnis.addRaumer(spieler, anz);
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		}
		else
			this.incObjekt(ObjektTyp.RAUMER, anz);
	}
	
	public Point getPos()
	{
		return this.pos;
	}
	
	public void setPos(Point pt)
	{
		this.pos = pt;
	}
	
	public int getBes()
	{
		return this.bes;
	}
	
	public int getEvorrat()
	{
		return this.evorrat;
	}
	
	public boolean isNeutral()
	{
		return (this.bes == Constants.BESITZER_NEUTRAL);
	}
	
	public int getEprod()
	{
		return this.eprod;
	}
	
	public void setEprod(int eprod)
	{
		this.eprod = eprod;
	}
	
	public int getEraum()
	{
		return this.eraum;
	}
	
	public void setEraum(int eraum)
	{
		this.eraum = eraum;
	}
	
	public int getFestungFaktor()
	{
		if (this.festung == null)
			return 0;
		else
			return this.festung.getFaktor();
	}
	
	public int getFestungIntakt()
	{
		if (this.festung == null)
			return 0;
		else
			return this.festung.getIntakt();
	}
	
	public boolean istBuendnis()
	{
		if (this.buendnis == null)
			return false;
		
		return (this.buendnis.getAnzahlMitglieder() > 1);
	}
	
	public boolean istKommandozentrale()
	{
		return (this.kz != null);
	}
	
	public Kommandozentrale getKommandozentrale()
	{
		return this.kz;
	}
	
	public boolean istBuendnisMitglied(int spieler)
	{
		if (!this.istBuendnis())
			return false;
		else
			return this.buendnis.istMitglied(spieler);
	}
	
	public int[] subtractRaumer(int anzSp, int anz, int bevorzugterSpieler, boolean buendnis)
	{
		int[] abzuege = new int[anzSp];
		
		if (!this.istBuendnis())
		{
			if (this.anz.containsKey(ObjektTyp.RAUMER))
				this.anz.put(ObjektTyp.RAUMER, this.anz.get(ObjektTyp.RAUMER) - anz);
			
			if (this.bes != Constants.BESITZER_NEUTRAL)
				abzuege[this.bes] = anz;

		}
		else
		{
			if (buendnis)
				abzuege = this.buendnis.subtract(anz, bevorzugterSpieler);
			else
			{
				this.buendnis.subRaumer(bevorzugterSpieler, anz);
				
				abzuege[bevorzugterSpieler] = anz;
			}
			
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		}
		
		return abzuege;
	}
	
	public Buendnis getEmptyCopyOfBuendnis()
	{
		if (!this.istBuendnis())
			return null;
		else
		{
			Buendnis copy = new Buendnis(this.buendnis.getAnzSp());
			
			for (int spieler = 0; spieler < this.buendnis.getAnzSp(); spieler++)
				if (this.buendnis.istMitglied(spieler))
					copy.addSpieler(spieler);
			
			return copy;
		}
	}
	
	public void kaufeEprod(int preis)
	{
		if (this.evorrat >= preis && this.eprod < Constants.EPROD_MAX)
		{
			this.eprod += Constants.KAUF_EPROD;
			if (this.eprod > Constants.EPROD_MAX)
				this.eprod = Constants.EPROD_MAX;
			
			this.evorrat -= preis;
		}
	}
	
	public void kaufeFestung(int preis)
	{
		if (this.evorrat >= preis && this.getFestungFaktor() < Constants.MAX_ANZ_FESTUNGEN)
		{
			if (this.festung == null)
				this.festung = new Festung(1, 100);
			else
				this.festung.addFestung();
			
			this.evorrat -= preis;
		}
	}
	
	public void verkaufeFestung(int preis)
	{
		if (this.festung != null)
		{
			if (this.festung.getFaktor() == 1)
				this.festung = null;
			else
				this.festung.subFestung();
			
			this.evorrat += preis;
		}
	}
	
	public void repariereFestung(int preis)
	{
		if (this.evorrat >= preis && this.festung != null && this.festung.getIntakt() < 100)
		{
			this.festung.incIntakt();
			this.evorrat -= preis;
		}
	}
	
	public void incEraum()
	{
		if (this.eraum < this.eprod)
			this.eraum++;
	}
	
	public void decEraum()
	{
		if (this.eraum > 0)
			this.eraum--;
	}
	
	public void subEvorrat(int anz)
	{
		this.evorrat -= anz;
		
		if (this.evorrat < 0)
			this.evorrat = 0;
	}
	
	public void addEvorrat(int anz)
	{
		this.evorrat += anz;
	}
	
	public Kommandozentrale kommandozentraleVerlegen()
	{
		Kommandozentrale kz = (Kommandozentrale)Utils.klon(this.kz);
		this.kz = null;
		return kz;
	}
	
	public void kommandozentraleAufnehmen(Kommandozentrale kz)
	{
		this.kz = (Kommandozentrale)Utils.klon(kz);
	}
	
	public void incObjekt(ObjektTyp typ, int anz)
	{
		if (this.anz.containsKey(typ))
			this.anz.put(typ, this.anz.get(typ)+ anz);
		else
			this.anz.put(typ, anz);
	}
	
	public void decObjekt(ObjektTyp typ, int anz)
	{
		if (this.anz.containsKey(typ))
		{
			if (this.anz.get(typ) - anz > 0)
				this.anz.put(typ, this.anz.get(typ) - anz);			
			else
				this.anz.remove(typ);
		}
	}
	
	public void kaufeObjekt(ObjektTyp typ, int anz, int preis)
	{
		if (this.evorrat >= preis)
		{
			this.incObjekt(typ,anz);
			this.evorrat -= preis;
		}
	}
	
	public void verkaufeObjekt(ObjektTyp typ, int preis)
	{
		int anz = this.getAnz(typ);
		
		if (anz > 0)
		{
			if (anz > 1)
				this.anz.put(typ, this.anz.get(typ)- 1);
			else
				this.anz.remove(typ);
			
			this.evorrat += preis;
		}
	}
	
	public void produziereEvorrat()
	{
		this.evorrat += (this.eprod - this.eraum);
	}
	
	public void produziereRaumer()
	{
		if (this.eraum <= 0)
			return;
		
		if (this.istBuendnis())
		{
			this.buendnis.addRaumer(this.bes, this.eraum);
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		}
		else
		{
			if (this.anz.containsKey(ObjektTyp.RAUMER))
				this.anz.put(ObjektTyp.RAUMER, this.anz.get(ObjektTyp.RAUMER)+ this.eraum);
			else
				this.anz.put(ObjektTyp.RAUMER, eraum);
		}
	}
	
	public void mergeRaumer(int anzSp, Flugobjekt obj)
	{
		if (obj.istBuendnis())
		{
			// Buendnisflotte.
			// Ist der Planet schon ein Buendnisplanet?
			if (!this.istBuendnis())
			{
				// Planet wird zum Buendnisplaneten
				this.buendnis = new Buendnis(anzSp);
				this.buendnis.addRaumer(this.bes, this.getAnz(ObjektTyp.RAUMER));
			}
			
			for (int sp = 0; sp < anzSp; sp++)
				if (obj.istBuendnisMitglied(sp))
					// Jeder Spieler der Raumerflotte wird automatisch Buendnismitglied auf dem Planeten
					this.buendnis.addRaumer(sp, obj.getRaumerProSpieler(sp));
			
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		}
		else
			this.incRaumerProSpieler(obj.getBes(), obj.getRaumerProSpieler(obj.getBes()));
			
	}
	
	public void deleteFestung()
	{
		this.festung = null;
	}
	
	public Kommandozentrale erobert(int anzSp, int sp, Flugobjekt obj)
	{
		Kommandozentrale kz = null;
		if (this.istKommandozentrale())
			kz = (Kommandozentrale)Utils.klon(this.kz);
		
		// Zunaechst alle Raumer auf 0 setzen, ausserdem Buendnis und Festung loeschen
		this.anz.remove(ObjektTyp.RAUMER);
		this.buendnis = null;
		this.festung = null;
		
		// Kommandozentrale entfernen
		this.kz = null;

		// Besitzer aendern
		this.bes = sp;
		
		// Energieproduktion halbieren, wenn Planet wieder neutral wird
		if (sp == Constants.BESITZER_NEUTRAL)
		{
			this.eprod = Math.max(Utils.round((double)this.eprod / 2.), 1);
			this.eraum = this.eprod; // Ausserirdische produzieren nur Raumer
		}
		
		// Raumer hinzufuegen
		if (obj != null)
			this.mergeRaumer(anzSp, obj);
		
		return kz;
	}
	
	public void spielerwechsel(int alterSpieler, int neuerSpieler)
	{
		// Besitzer des Planeten wechseln
		if (this.bes == alterSpieler)
		{
			// Kommandozentrale entfernen
			this.kz = null;
			
			// Wird aufgerufen, wenn die Kommandozentrale in fremde Haende gefallen ist
			// Energieproduktion halbieren, wenn Planet von Ausserirdischen erobert wurde
			if (neuerSpieler == Constants.BESITZER_NEUTRAL)
			{
				this.eprod = Math.max(Utils.round((double)this.eprod / 2.), 1);
				this.eraum = this.eprod; // Ausserirdische produzieren nur Raumer
				
				if (this.istBuendnis())
				{
					// Keine Buendnisse auf neutralen Planeten. Alle fremden Spieler
					// "toeten"
					this.anz.put(ObjektTyp.RAUMER, this.buendnis.getAnz(this.bes));
					this.buendnis = null;
				}
			}
			
			// Besitzer aendern
			this.bes = neuerSpieler;

		}
		
		// Sender ersetzen
		if (this.sender != null && this.sender.containsKey(alterSpieler))
		{
			if (neuerSpieler == Constants.BESITZER_NEUTRAL)
				// Sender loeschen, wenn Planet von Ausserirdischen erobert wurde
				this.sender.remove(alterSpieler);
			else
			{
				// Sender auf den neuen Spieler uebertragen
				int jahrEndeAlt = this.sender.get(alterSpieler);
				int jahrEndeNeu = (this.sender.contains(neuerSpieler)) ?
										this.sender.get(neuerSpieler) :
										-1;
										
				if (jahrEndeAlt > jahrEndeNeu)
					this.sender.put(neuerSpieler, jahrEndeAlt);
			}
		}

		// Buendnis ersetzen
		if (this.istBuendnisMitglied(alterSpieler))
		{
			this.buendnis.spielerErsetzen(alterSpieler, neuerSpieler);
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
			
			if (this.buendnis.getAnzahlMitglieder() <= 1)
				this.buendnis = null;
		}
	}
	
	public void setFestungIntakt(int arg)
	{
		if (this.festung != null)
			this.festung.setIntakt(arg);
	}
	
	public void buendnisKuendigen(int spieler)
	{
		// spieler: Spieler, der die Kuendigung ausgesprochen hat
		if (!this.istBuendnis())
			return;
		
		if (!this.istBuendnisMitglied(spieler))
			return;
		
		if (spieler == this.bes)
		{
			// Spieler kuendigt auf seinem eigenen Planeten allen anderen Spielern
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getAnz(spieler));
			
			for (int sp = 0; sp < this.buendnis.getAnzSp(); sp++)
				this.buendnis.removeSpieler(sp, false);
			
		}
		else
		{
			// Spieler kuendigt auf fremdem Planeten
			this.buendnis.removeSpieler(spieler, false);
			this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		}
		
	}
	
	public void buendnisSpielerErsetzen(int spielerAlt, int spielerNeu)
	{
		if (!this.istBuendnisMitglied(spielerAlt))
			return;
		
		this.buendnis.spielerErsetzen(spielerAlt, spielerNeu);
		
		this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
		
		if (this.buendnis.getAnzahlMitglieder() <= 1)
			this.buendnis = null;
	}
	
	public void buendnisSpielerHinzufuegen(int anzSp, int spieler)
	{
		// Fremde Spieler zum Buendnis auf einem Planeten hinzufuegen
		if (!this.istBuendnis())
		{
			if (this.buendnis == null)
				this.buendnis = new Buendnis(anzSp);
			
			this.buendnis.addRaumer(this.bes, this.getAnz(ObjektTyp.RAUMER));
		}
		
		this.buendnis.addSpieler(spieler);
		this.anz.put(ObjektTyp.RAUMER, this.buendnis.getSum());
	}
	
	public boolean[] buendnisGetMitglieder()
	{
		if (this.buendnis != null)
			return this.buendnis.getMitglieder();
		else
			return null;
	}

	public int[] buendnisAufraeumen(int anzSp)
	{
		if (this.buendnis == null)
			return new int[anzSp];
		
		int[] abzuege = this.buendnis.aufraeumen();

		if (this.buendnis.getAnzahlMitglieder() <= 1)
			this.buendnis = null;
		
		return abzuege;
	}
	
	@SuppressWarnings("unchecked")
	public void spielzugUebernahmePlanet(Planet pl)
	{
		// Flugobjekte kopieren, vorher Raumer sichern
		Integer raumer = this.anz.get(ObjektTyp.RAUMER);
		
		this.anz = (Hashtable<ObjektTyp, Integer>)Utils.klon(pl.anz);
		
		if (raumer == null)
			this.anz.remove(ObjektTyp.RAUMER);
		else
			this.anz.replace(ObjektTyp.RAUMER, raumer.intValue());
		
		// Andere Daten des Planeten, die im Planeteneditor geaendert werden koennen, kopieren
		this.evorrat = pl.evorrat;
		this.eprod = pl.eprod;
		this.eraum = pl.eraum;
		this.festung = pl.festung;
	}
	
	public boolean spielzugUebernahmeBuendnis(int spieler, boolean[] mitglieder)
	{
		if (spieler == this.bes)
		{
			for (int sp = 0; sp < mitglieder.length; sp++)
			{
				if (sp != this.bes)
				{
					if (this.istBuendnisMitglied(sp) && !mitglieder[sp])
						this.buendnisKuendigen(sp);
					else
						this.buendnisSpielerHinzufuegen(mitglieder.length, sp);
				}
			}
		}
		else
		{		
			// Pruefen, ob ein Spieler seine Teilenahme auf einem fremden Planeten
			// kuendigt, aber dort noch Raumer hat.
			if (this.getRaumerProSpieler(spieler) > 0)
				return false;
			
			this.buendnisKuendigen(spieler);
		}
		
		return true;
	}
	
	public Buendnis uebertrageBuendnisAufFlotte(int[] abzuege)
	{
		// Starte eine Buendnisflotte. Die Buendnisstruktur des Planeten wird auf die Flotte 
		// uebertragen
		Buendnis objBuendnis = this.getEmptyCopyOfBuendnis();
		if (objBuendnis == null)
			return null;
		
		for (int spieler = 0; spieler < objBuendnis.getAnzSp(); spieler++)
			if (this.istBuendnisMitglied(spieler))
				objBuendnis.addRaumer(spieler, abzuege[spieler]);
		
		return objBuendnis;
	}
	
	public byte getCol(Spieler[] spieler)
	{
		return this.isNeutral() ?
				Colors.INDEX_NEUTRAL :
				spieler[this.getBes()].getColIndex();
	}
	
	public void setSender(int spieler, int jahr)
	{
		if (this.sender == null)
			this.sender = new Hashtable<Integer, Integer>();
		
		this.sender.put(spieler, jahr);
	}
	
	public boolean hatSender(int spieler, int momentanesJahr)
	{
		if (this.sender == null)
			return false;
		
		if (this.sender.containsKey(spieler))
			return momentanesJahr <= this.sender.get(spieler);
		else
			return false;
	}
	
	public Planet getSpielerInfo(int spIndex, int momentanesJahr, boolean zeigeNeutralePlaneten)
	{
		// Erzeuge einen Clone und reduziere alle Informationen,
		// die den Spieler nichts angehen
		Planet plClone = (Planet)Utils.klon(this);
		
		if (this.bes == spIndex ||
			this.hatSender(spIndex, momentanesJahr))
		{
			return plClone;
		}

		if (!this.istBuendnisMitglied(spIndex))
		{
			plClone.buendnis = null;
		}

		plClone.anz = new Hashtable<ObjektTyp,Integer>();
		
		if ((this.bes == Constants.BESITZER_NEUTRAL && zeigeNeutralePlaneten) ||
			 this.bes != Constants.BESITZER_NEUTRAL)
		{
			if (this.getAnz(ObjektTyp.RAUMER) > 0)
				plClone.anz.put(ObjektTyp.RAUMER, this.getAnz(ObjektTyp.RAUMER));
		}
		
		plClone.eprod = 0;
		plClone.eraum = 0;
		plClone.evorrat = 0;
		plClone.festung = null;
		plClone.kz = null;
		plClone.sender = null;
		
		return plClone;
	}
}
