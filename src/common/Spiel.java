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

import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

@SuppressWarnings("serial")
public class Spiel extends EmailTransportBase implements Serializable
{
	private String name;
	private long startDatum;
	private int spieldauerSekunden;
	private String minBuild; // Mindestens vorausgesetzter Build
	private HashSet<SpielOptionen> optionen; // Entspricht "setup" im alten Vega 
	private int maxJahre;
	private int jahr; // Momentanes Jahr (startet mit 0)
	private int anzSp; // Anzahl der Spieler
	private int anzPl;
	
	private int xOff;
	private int yOff;
	private int breite;
	private int hoehe;
			  
	private Planet[] planeten;
	private Spieler[] spieler;
	private ArrayList<Flugobjekt> objekte;
	private Hashtable<Integer,ArrayList<Spielzug>> spielzuege;
	private UUID[] spielzuegeReferenzCodes;
	private Hashtable<String,Mine> minen;
	private ArrayList<Hashtable<ObjektTyp,Integer>> preiseKaufArray;
	private ArrayList<Hashtable<ObjektTyp,Integer>> preiseVerkaufArray;
	private String emailAdresseSpielleiter;
	
	private Loch loch;
	
	private boolean abgeschlossen;
	
	private Hashtable<Integer,Archiv> archiv; // Punktestaende und Auswertungen pro Jahr
	
	// Transiente Felder
	transient private boolean emailSpieler;
	transient private long currentTime;
	transient private boolean initial;
	transient private boolean enableParameterChange;
	
	transient private Console console;
	transient private ScreenDisplayContent screenDisplayContent;
	transient private SpielThread spielThread;
	
	transient private Hashtable<String,String> mapPlanetIndexToName;
	transient private Hashtable<String,String> mapPlanetNameToIndex;
	transient private Hashtable<String,Integer> planetenByPoint;
	transient private int[] planetenSortiert;
	transient static private ArrayList<Point> heimatPlanetUmkreis;
	
	transient private static Hashtable<ObjektTyp,Integer> preiseKaufUntergrenze;
	transient private static Hashtable<ObjektTyp,Integer> preiseKaufObergrenze;
	
	transient private boolean goToReplay;
	
	transient private int[][] distanzMatrix;
	
	static {
		preiseKaufUntergrenze = new Hashtable<ObjektTyp,Integer>();
		preiseKaufObergrenze = new Hashtable<ObjektTyp,Integer>();
		
		preiseKaufUntergrenze.put(ObjektTyp.AUFKLAERER, Constants.PREIS_MIN_AUFKL);
		preiseKaufObergrenze.put(ObjektTyp.AUFKLAERER, Constants.PREIS_MIN_AUFKL + Constants.PREIS_SPANNE_AUFKL-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.PATROUILLE, Constants.PREIS_MIN_PATR);
		preiseKaufObergrenze.put(ObjektTyp.PATROUILLE, Constants.PREIS_MIN_PATR + Constants.PREIS_SPANNE_PATR-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.FESTUNG, Constants.PREIS_MIN_FESTUNG);
		preiseKaufObergrenze.put(ObjektTyp.FESTUNG, Constants.PREIS_MIN_FESTUNG + Constants.PREIS_SPANNE_FESTUNG-1);
			
		preiseKaufUntergrenze.put(ObjektTyp.EPROD, Constants.PREIS_MIN_EPROD);
		preiseKaufObergrenze.put(ObjektTyp.EPROD, Constants.PREIS_MIN_EPROD + Constants.PREIS_SPANNE_EPROD-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.MINE50, Constants.PREIS_MIN_MINE50);
		preiseKaufObergrenze.put(ObjektTyp.MINE50, Constants.PREIS_MIN_MINE50 + Constants.PREIS_SPANNE_MINE50-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.MINE100, Constants.PREIS_MIN_MINE100);
		preiseKaufObergrenze.put(ObjektTyp.MINE100, Constants.PREIS_MIN_MINE100 + Constants.PREIS_SPANNE_MINE100-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.MINE250, Constants.PREIS_MIN_MINE250);
		preiseKaufObergrenze.put(ObjektTyp.MINE250, Constants.PREIS_MIN_MINE250 + Constants.PREIS_SPANNE_MINE250-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.MINE500, Constants.PREIS_MIN_MINE500);
		preiseKaufObergrenze.put(ObjektTyp.MINE500, Constants.PREIS_MIN_MINE500 + Constants.PREIS_SPANNE_MINE500-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.TRANSPORTER, Constants.PREIS_MIN_TRANSP);
		preiseKaufObergrenze.put(ObjektTyp.TRANSPORTER, Constants.PREIS_MIN_TRANSP + Constants.PREIS_SPANNE_TRANSP-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.MINENRAEUMER, Constants.PREIS_MIN_MINENR);
		preiseKaufObergrenze.put(ObjektTyp.MINENRAEUMER, Constants.PREIS_MIN_MINENR + Constants.PREIS_SPANNE_MINENR-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.FESTUNG_REPARATUR, Constants.PREIS_MIN_FESTUNG_REPARATUR);
		preiseKaufObergrenze.put(ObjektTyp.FESTUNG_REPARATUR, Constants.PREIS_MIN_FESTUNG_REPARATUR + Constants.PREIS_SPANNE_FESTUNG_REPARATUR-1);
		
		preiseKaufUntergrenze.put(ObjektTyp.ERAUM, Constants.PREIS_MIN_ERAUM);
		preiseKaufObergrenze.put(ObjektTyp.ERAUM, Constants.PREIS_MIN_ERAUM + Constants.PREIS_SPANNE_ERAUM-1);
	}
	
	protected Spiel() {}
	
	@SuppressWarnings("unchecked")
	public Spiel(HashSet<SpielOptionen> optionen,
			Spieler[] spieler,
			String emailAdresseSpielleiter,
			int anzPl,
			int maxJahre)
	{
		this.optionen = (HashSet<SpielOptionen>)Utils.klon(optionen);
		this.spieler = (Spieler[])Utils.klon(spieler);
		this.emailAdresseSpielleiter = emailAdresseSpielleiter;

		this.anzPl = anzPl;
		this.anzSp = spieler.length;
		this.maxJahre = maxJahre;
		
		this.initial = true;
		
		//Utils.getColor(241,196,15);
	}
	
	public static Spiel getSpiel(HashSet<SpielOptionen> optionen,
			Spieler[] spieler,
			String emailAdresseSpielleiter,
			int anzPl,
			int maxJahre)
	{
		Spiel spiel = new Spiel(optionen, spieler, emailAdresseSpielleiter, anzPl, maxJahre);
		spiel.erzeugeSpielfeld();
		
		return spiel;
	}
	
	public void initAfterLoad(SpielThread spielThread)
	{
		// Transiente Felder instanziieren
		this.spielThread = spielThread;
		this.console = new Console(this, false);
		
		this.spielThread.checkMenueEnabled();
		this.goToReplay = this.emailSpieler;
		
		this.hauptschleife();
	}
	
	public void initNewGame(
			SpielThread spielThread)
			
	{
		this.spielThread = spielThread;
		this.console = new Console(this, false);
		
		do
		{
			// Neues Spielfeld erzeugen
			this.erzeugeSpielfeld();
			
			this.spielThread.checkMenueEnabled();
			
			this.updateSpielfeldDisplayNeuesSpiel();
			this.updatePlanetenlisteDisplay(true, this.isSimple());
			
			this.console.clear();
			this.console.appendText(
					SternResources.SpielfeldOkFrage(true) + " ");
			String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (input.equals(Console.KEY_YES))
				break;
			
		} while (true);
		
		this.initial = false;
		this.spielThread.checkMenueEnabled();
		
		// Wenn das Spielfeld akzeptiert wurde, weiter zur Hauptschleife
		this.jahrVorbereiten();
		this.hauptschleife();
	}
	
	private String getSpielname()
	{
		char[] vokale = {'a','e','i','o','u','y'};
		char[] konsonanten = {'b','c','d','f','g','h','j','k','l','m','n','p','r','s','t','v','w','x','z'}; 
		
		int silben;
		StringBuilder sb = null;
		boolean ersterKons = false;
		
		// 2 oder 3 Silben
		silben = Utils.random(2)+2;
		sb = new StringBuilder();

		for (int s = 0; s < silben; s++)
		{					
			// Erster Konsonant
			if (Utils.random(2) == 0)
			{
				sb.append(konsonanten[Utils.random(konsonanten.length)]);
				ersterKons = true;
			}
			else
				ersterKons = false;

			// Vokal
			sb.append(vokale[Utils.random(vokale.length)]);

			// Zweiter Konsonant
			if (!ersterKons || Utils.random(2) == 0)
				sb.append(konsonanten[Utils.random(konsonanten.length)]);
		}
		
		// Erster Buchstabe in Grossbuchstaben umwandeln
		sb.setCharAt(0, sb.substring(0,1).toUpperCase().charAt(0));
		
		return sb.toString();
		
	}
	
	public boolean istSpielerEmail(int spIndex)
	{
		if (this.optionen.contains(SpielOptionen.EMAIL))
			return this.spieler[spIndex].istEmail();
		else
			return false;
	}
	
	public boolean istInitial()
	{
		return this.initial;
	}
	
	public void setInitial()
	{
		this.initial = true;
	}
	
	public void setEmailSpieler(boolean v)
	{
		this.emailSpieler = v;
	}
	
	public int getAnzSp()
	{
		return this.anzSp;
	}
	
	public int getAnzPl()
	{
		return this.anzPl;
	}
	
	public Spieler[] getSpieler()
	{
		return this.spieler;
	}
	
	public void setSpieler(Spieler[] spielerNeu)
	{
		this.spieler = spielerNeu;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getMinBuild()
	{
		return this.minBuild;
	}
	
	public boolean isSimple()
	{
		return this.optionen.contains(SpielOptionen.SIMPEL);
	}
	
	public int[][] getDistanzMatrix()
	{
		if (this.distanzMatrix == null)
		{
			this.distanzMatrix = new int[anzPl][anzPl];
			
			for (int pl = 0; pl < anzPl - 1; pl++)
			{
				for (int pl2 = pl + 1; pl2 < anzPl; pl2++)
				{
					this.distanzMatrix[pl][pl2] = Flugobjekt.getFlugdauer(
							ObjektTyp.RAUMER,
							false,
							this.planeten[pl].getPos(),
							this.planeten[pl2].getPos());
					
					this.distanzMatrix[pl2][pl] = this.distanzMatrix[pl][pl2];
				}
			}  			
		}
		
		return this.distanzMatrix;
	}
	
	public Hashtable<Integer,Archiv> getArchiv(int vonJahr, int bisJahr)
	{
		Hashtable<Integer,Archiv> retval = new Hashtable<Integer,Archiv>();
		
		for (Integer j: this.archiv.keySet())
		{
			if (j >= vonJahr && j <= bisJahr)
				retval.put(j, this.archiv.get(j));
		}
		
		return retval;
	}
	
	public void addArchiv(Hashtable<Integer,Archiv> neueArchive)
	{
		for (Integer j: neueArchive.keySet())
			this.archiv.put(j, neueArchive.get(j));
	}
	
	public void setMinBuild(String minBuild)
	{
		this.minBuild = minBuild;
	}
	
	public HashSet<SpielOptionen> getOptionen()
	{
		return this.optionen;
	}
	
	public void setOptionen(HashSet<SpielOptionen> optionen)
	{
		this.optionen = optionen;
	}
	
	public int getMaxJahre()
	{
		return this.maxJahre;
	}
	
	public void setMaxJahre(int maxJahre)
	{
		this.maxJahre = maxJahre;
	}
	
	public boolean getAbgeschlossen()
	{
		return this.abgeschlossen;
	}
	
	public boolean getEmailSpieler()
	{
		return this.emailSpieler;
	}
	
	public String getEmailAdresseSpielleiter()
	{
		return this.emailAdresseSpielleiter;
	}
	
	public ArrayList<PlanetenInfo> getPlanetenInfo()
	{
		ArrayList<PlanetenInfo> retval = new ArrayList<PlanetenInfo>(this.anzPl);
		
		for (Planet pl: this.planeten)
			retval.add(new PlanetenInfo(
					pl.getPos().getX(), 
					pl.getPos().getY(), 
					pl.getBes() == Constants.BESITZER_NEUTRAL ?
							Colors.INDEX_NEUTRAL :
							this.spieler[pl.getBes()].getColIndex()));
		
		return retval;
	}
	
	public SpielInfo getSpielInfo()
	{
		SpielInfo info = new SpielInfo();
		
		info.abgeschlossen = this.abgeschlossen;
		info.jahr = this.jahr;
		info.maxJahre = this.maxJahre;
		info.name = this.name;
		info.spieler = this.spieler;
		info.startDatum = this.startDatum;
		info.simpleStern = (this.optionen.contains(SpielOptionen.SIMPEL));
		info.planetenInfo = this.getPlanetenInfo();
		
		info.zugeingabeBeendet = new HashSet<String>();
		
		if (this.spielzuege != null)
		{
			for (Integer spIndex: this.spielzuege.keySet())
				info.zugeingabeBeendet.add(this.spieler[spIndex].getName());
		}
		
		return info;
	}
	
	@SuppressWarnings("unchecked")
	public void changeSpielData(
			HashSet<SpielOptionen> optionen,
			int maxJahre,
			String emailAdresseSpielleiter,
			ArrayList<Spieler> spieler)
	{
		this.optionen = (HashSet<SpielOptionen>) Utils.klon(optionen);
		
		for (int sp = 0; sp < spieler.size(); sp++)
		{
			this.spieler[sp].setName(spieler.get(sp).getName());
			this.spieler[sp].setColIndex(spieler.get(sp).getColIndex());
			this.spieler[sp].setEmail(spieler.get(sp).istEmail());
			this.spieler[sp].setEmailAdresse(spieler.get(sp).getEmailAdresse());
		}
		
		this.emailAdresseSpielleiter = emailAdresseSpielleiter;
		this.maxJahre = maxJahre;
		
		this.updateSpielfeldDisplay();
		this.updatePlanetenlisteDisplay(false, this.isSimple());
	}
	
	private int[] getPlanetenSortiert()
	{
		if (this.planetenSortiert == null)
			this.buildPlanetenNameMap();
		
		return this.planetenSortiert;
	}
	
	private void speichern(boolean autoSave)
	{
		this.spielThread.speichern(this, autoSave);
	}
	
	public Spiel copyWithReducedInfo(int spIndex, boolean nurAuswertungVomLetztenJahr)
	{
		// Erzeuge eine Kopie des Spiels ohne Informationen, die Spieler
		// 'spIndex' nichts angehen.
		Spiel spClone = (Spiel)Utils.klon(this);
		
		spClone.setMinBuild(Constants.MIN_BUILD);
		
		// Von einem abgeschlossenen Spiel darf der Spieler alles sehen
		if (spClone.abgeschlossen)
			return spClone;
		
		// Flugobjekte
		if (!spClone.optionen.contains(SpielOptionen.SIMPEL))
		{	
			spClone.objekte = new ArrayList<Flugobjekt>();
			
			for (Flugobjekt objOrig: this.objekte)
			{
				if (objOrig.getTyp() == ObjektTyp.SCHWARZES_LOCH ||
					objOrig.istBeteiligt(spIndex))
				{
					spClone.objekte.add((Flugobjekt)Utils.klon(objOrig));
				}
			}
		}
		
		// Spielzuege, Referenzcodes, Preise
		for (int sp = 0; sp < this.anzSp; sp++)
		{
			if (sp != spIndex)
			{
				// So tun als haetten alle anderen Spieler bereits Spielzuege eingegeben
				spClone.spielzuege.put(sp, new ArrayList<Spielzug>());
				spClone.spielzuegeReferenzCodes[sp] = null;
				
				spClone.preiseKaufArray.set(sp, new Hashtable<ObjektTyp,Integer>());
				spClone.preiseVerkaufArray.set(sp, new Hashtable<ObjektTyp,Integer>());
			}
		}
		
		// Minen
		spClone.minen = new Hashtable<String, Mine>();
		
		for (String pos: this.minen.keySet())
		{
			Mine mineClone = this.minen.get(pos).getSpielerInfo(spIndex);
			
			if (mineClone != null)
				spClone.minen.put(pos, mineClone);
		}
		
		// Planeten
		for (int plIndex = 0; plIndex < this.anzPl; plIndex++)
		{
			Planet plClone = this.planeten[plIndex].getSpielerInfo(
					spIndex, 
					this.jahr,
					this.optionen.contains(SpielOptionen.SIMPEL));
			
			spClone.planeten[plIndex] = plClone;
		}
		
		// Auswertungen
		if (nurAuswertungVomLetztenJahr)
		{
			// Loesche die Auswertungen.
			for(Integer j: spClone.archiv.keySet())
			{
				if (j < spClone.jahr - 1)
				{
					spClone.archiv.get(j).loescheAuswertung();
				}
			}
		}
		
		return spClone;
	}
	
	public static HashSet<SpielOptionen> getDefaultSpielOptionen()
	{
		HashSet<SpielOptionen> optionen = new HashSet<SpielOptionen>();
		
		optionen.add(SpielOptionen.KOMPAKTES_SPIELFELD);
		optionen.add(SpielOptionen.AUSSERIRDISCHE);
		optionen.add(SpielOptionen.KEIN_ENDLOSSPIEL);
		optionen.add(SpielOptionen.KOMMANDOZENTRALEN);
		optionen.add(SpielOptionen.SCHWARZES_LOCH);
		optionen.add(SpielOptionen.FESTUNGEN);
		optionen.add(SpielOptionen.AUTO_SAVE);
		
		return optionen;
	}
	
	public static ArrayList<Spieler> getDefaultSpieler()
	{
		ArrayList<Spieler> spieler = new ArrayList<Spieler>();
		
		for (int sp = 0; sp < Constants.ANZAHL_SPIELER_MAX; sp++)
			spieler.add(
					new Spieler(
							SternResources.Spieler(false)+(sp+1), 
							"", 
							(byte)(sp+Colors.SPIELER_FARBEN_OFFSET), // Farbindex 
							false, 
							false));
		
		return spieler;
	}
	
	private void erzeugeSpielfeld()
	{
		this.name = this.getSpielname();
		this.minBuild = Constants.MIN_BUILD;
		this.abgeschlossen = false;
		this.initial = true;
		
		this.archiv = new Hashtable<Integer,Archiv>();
		this.jahr = 0;
		
		if (!this.optionen.contains(SpielOptionen.SIMPEL))
			this.loch = new Loch(false, null, Constants.LOCH_ERSTES_JAHR);
		else
			this.loch = null;
		
		this.minen = new Hashtable<String,Mine>();
		
		this.objekte = new ArrayList<Flugobjekt>();
		
		this.planeten = new Planet[anzPl];
		
		this.screenDisplayContent = null;
		
		this.spieldauerSekunden = 0;
		
		this.startDatum = System.currentTimeMillis();
		
		// Kompaktes Spielfeld?
		this.xOff = 0;
		this.yOff = 0;
		this.breite = Constants.FELD_MAX_X;
		this.hoehe = Constants.FELD_MAX_Y;
		
		if (this.optionen.contains(SpielOptionen.KOMPAKTES_SPIELFELD) && this.anzPl < Constants.ANZAHL_PLANETEN_MAX)
		{
			double felderProPlanet = (double)(Constants.FELD_MAX_X * Constants.FELD_MAX_Y) / (double)Constants.ANZAHL_PLANETEN_MAX;
			double ratio = (double)Constants.FELD_MAX_X / (double)Constants.FELD_MAX_Y;
			
			double felder = (double)this.anzPl * felderProPlanet;
			
			this.hoehe = Utils.round(Math.sqrt(felder) / ratio);
			this.breite = Utils.round((double)this.hoehe * ratio);
			
			this.xOff = (int)((double)(Constants.FELD_MAX_X - this.breite) / 2.);
			this.yOff = (int)((double)(Constants.FELD_MAX_Y - this.hoehe) / 2.);
		}
		
		// Anzahl der Planeten nahe an den Heimatplaneten
		int anzNahePlaneten = Constants.ANZ_NAHE_PLANETEN;
		
		if (anzNahePlaneten * anzSp > anzPl - anzSp)
		{
			anzNahePlaneten = (int)Math.floor((double)(anzPl - anzSp) / (double)anzSp);
		}
		
		Point[] points = new Point[anzPl];
		
		// Verteile die Heimatplaneten in zufaelliger Spielerreihenfolge
		int[] seqSpieler = Utils.randomList(anzSp);
		
		for (int i = 0; i < anzSp; i++)
		{
			int heimatPlIndex = seqSpieler[i];
			
			Point heimatPlPoint;
			
			boolean ok = false;
			
			while(!ok)
			{
				// Position eines Heimatplaneten auswuerfeln
				heimatPlPoint = new Point(Utils.random(this.breite), Utils.random(this.hoehe));
				
				if (isTooClose(heimatPlPoint, points))
					continue;
				
				points[heimatPlIndex] = heimatPlPoint;
				
				if (anzNahePlaneten == 0)
				{
					ok = true;
					continue;
				}
				
				// Ermittle die moeglichen Felder, in den 'anzNahePlaneten' um diesen Heimatplaneten
				// herum liegen koennen
				ArrayList<Point> ptList = moeglicheFelderNahePlaneten(points, heimatPlPoint);
				
				if (ptList.size() < anzNahePlaneten)
					continue;
				
				int ptListSeq[] = Utils.randomList(ptList.size());
				
				// Verteile nun um den potenziellen Heimatplaneten herum 'anzNahePlaneten'.
				int plIndexStart = anzSp + heimatPlIndex * anzNahePlaneten;
				int plIndex = plIndexStart;
				
				for (int j = 0; j < ptListSeq.length; j++)
				{
					Point plPoint = ptList.get(ptListSeq[j]);
					
					if (isTooClose(plPoint, points))
						continue;
					
					// Ok, der Punkt is akzeptabel
					points[plIndex] = plPoint;
					plIndex++;
					
					if (plIndex - plIndexStart >= anzNahePlaneten)
						// Ok, wir haben genug Planeten befunden
						break;
				}
				
				if (plIndex - plIndexStart  < anzNahePlaneten)
				{
					// Wir haben nicht genug Planeten gefunden. Loesche die bisher um
					// den Heimatplanet verteilten Planeten und wuerfle einen neuen Heimatplaneten aus
					for (int index = plIndexStart; index < plIndex; index++)
						points[index] = null;
				}
				else
					// Erfolg. Wir haben einen geeigneten Heimatplaneten und 'anzNahePlaneten' herum
					// gefunden
					ok = true;
			}
		}
		
		// Verteile die restlichen Planeten. Diese duerfen nicht zu nah an den Heimatplaneten liegen
		for (int plIndex = anzSp + anzSp * anzNahePlaneten; plIndex < anzPl; plIndex++)
		{
			boolean ok = false;
			Point plPoint = null;
			
			while(!ok)
			{
				plPoint = new Point(Utils.random(this.breite), Utils.random(this.hoehe));
				
				if (isTooClose(plPoint, points))
					continue;
				
				// Berechne Abstand zu den Heimatplaneten
				ok = true;
				
				if (anzNahePlaneten <= 1)
					continue;
				
				for (int heimatPlIndex = 0; heimatPlIndex < anzSp; heimatPlIndex++)
				{
					double dist = Math.sqrt(
							Math.pow(plPoint.getX() - points[heimatPlIndex].getX(), 2) + 
							Math.pow(plPoint.getY() - points[heimatPlIndex].getY(), 2));
					
					if (dist <= Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE)
					{
						ok = false;
						break;
					}
				}
			}
			
			points[plIndex] = plPoint;
		}
		
		// Verteilung ist ok, jetzt Raumer, Energie zuweisen
		for (int pl = 0; pl < this.anzPl; pl++)
		{
			boolean heimatPlanet = (pl < this.anzSp);
											
			int bes 	  = heimatPlanet ? pl : Constants.BESITZER_NEUTRAL;
			int anzRaumer = heimatPlanet ? Constants.RAUMER_ZU_BEGINN_SPIELER : Utils.random(Constants.RAUMER_ZU_BEGINN_NEUTRAL);
			
			int eprod     = Constants.EPROD_ZU_BEGINN_SPIELER;
			if (!heimatPlanet)
			{
				eprod = Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL) + 1;
				if (Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W2) < Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W1)
					eprod += (Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA)+1);
			}
			
			Festung festung = (!this.optionen.contains(SpielOptionen.SIMPEL) && heimatPlanet) ? 
									new Festung(1,100) :
										null;
			Kommandozentrale kz = (heimatPlanet && (!this.optionen.contains(SpielOptionen.SIMPEL)))
					                  ? new Kommandozentrale(pl)
									  : null;
			
	        Hashtable<ObjektTyp, Integer> anz = new Hashtable<ObjektTyp, Integer>();
	        anz.put(ObjektTyp.RAUMER, anzRaumer);
	        
	        int evorrat = this.optionen.contains(SpielOptionen.SIMPEL) ? 
	        			  0 :	        		
			        		heimatPlanet ?
			        				Constants.EVORRAT_ZU_BEGINN_SPIELER :
			        				Utils.random(Constants.EVORRAT_ZU_BEGINN_NEUTRAL_MAX + 1);
	        
			this.planeten[pl] = new Planet(
										new Point(
												points[pl].getX() + this.xOff,
												points[pl].getY() + this.yOff),
										null,
										anz,
										bes, 
										festung,
										evorrat,
										eprod,
										eprod,
										kz);
		}
		
		// Jetzt noch die Energie der Planeten um die Heimatplaneten herum optimieren.
		if (anzNahePlaneten > 0)
		{
			for (int spIndex = 0; spIndex < this.anzSp; spIndex++)
			{
				// Die durchschnittliche Energieproduktion der nahen Planeten soll 
				// zwischen 5 und 7 EE betragen.
				boolean ok = false;
				
				int eProd[] = null;
				
				while (!ok)
				{
					int summe = 0;
					eProd = new int[anzNahePlaneten];
					
					for (int i = 0; i < anzNahePlaneten; i++)
					{
						eProd[i] = Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL) + 1;
						if (Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W2) < Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA_W1)
							eProd[i] += (Utils.random(Constants.EPROD_ZU_BEGINN_NEUTRAL_EXTRA)+1);
						
						summe += eProd[i];
					}
					
					double durchschnitt = (double)summe / (double)anzNahePlaneten;
					
					if (durchschnitt >= 5 && durchschnitt <= 7)
						ok = true;
				}
				
				int plIndexStart = this.anzSp + anzNahePlaneten * spIndex;
				
				for (int i = 0; i < anzNahePlaneten; i++)
				{
					this.planeten[plIndexStart + i].setEprod(eProd[i]);
					this.planeten[plIndexStart + i].setEraum(eProd[i]);
				}
			}
		}
		
		this.buildPlanetenNameMap();
		this.punktestandBerechnen();	
	}
	
	private boolean isTooClose (Point pt, Point[] points)
	{
		boolean retval = false;
		
		for (int i = 0; i < points.length; i++)
		{
			if (points[i] == null)
				continue;
			
			if (Math.abs(points[i].getX() - pt.getX()) < 2 && Math.abs(points[i].getY() - pt.getY()) < 2)
			{
				retval = true;
				break;
			}
		}
		
		return retval;
	}
	
	private ArrayList<Point> moeglicheFelderNahePlaneten(Point[] points, Point ptHeimatplanet)
	{
		if (heimatPlanetUmkreis == null)
		{
			// Baue eine Liste mit dx/dy auf
			heimatPlanetUmkreis = new ArrayList<Point>();
			
			for (int dx = -Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE;
					 dx < +Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE;
				     dx++)
			{
				for (int dy = -Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE;
						 dy < +Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE;
					     dy++)
				{
					// Ist das Feld zu nah am Heimatplaneten?
					if (Math.abs(dx) < 2 && Math.abs(dy) < 2)
						continue;
					
					// Ist die Reisedauer vom Heimatplaneten zu lang?
					double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); 
					if (dist > Constants.GESCHWINDIGKEIT_NORMAL * Constants.ANZ_NAHE_PLANETEN_MAX_REISEDAUER_JAHRE)
						continue;
					
					heimatPlanetUmkreis.add(new Point(dx, dy));
				}
			}
		}
		
		ArrayList<Point> ptList = new ArrayList<Point>();
		
		for (int i = 0; i < heimatPlanetUmkreis.size(); i++)
		{
			int x = ptHeimatplanet.getX() + heimatPlanetUmkreis.get(i).getX();
			int y = ptHeimatplanet.getY() + heimatPlanetUmkreis.get(i).getY();
			
			// Liegt der Punkt ausserhalb des Spielfelds?
			if (x < 0 || x >= this.breite || y < 0 || y >= this.hoehe)
				continue;
			
			Point pt = new Point(x, y);
			
			// Zu nah an anderen Planeten?
			if (isTooClose(pt, points))
				continue;
			
			// Feld ist ein moeglicher Kandidat fuer einen Planeten
			ptList.add(pt);
		}
				
		return ptList;
	}
	
	private void hauptschleife()
	{
		this.currentTime = System.currentTimeMillis(); // Zur Berechnung der Gesamtspieldauer
		
		if (this.spielzuege == null)
			this.spielzuege = new Hashtable<Integer, ArrayList<Spielzug>>();
		
		do
		{
			this.updateSpielfeldDisplay();
			this.updatePlanetenlisteDisplay(false, this.isSimple());
			
			this.console.clear();
			
			// Ist das Spiel abgeschlossen?
			this.abschliessenPruefung(false);
			
			// Zum Hauptmenue mit Zugeingabe
			this.hauptmenue();
			
			// Auswertung im Hintergrund durchfuehren
			this.console.setBackground(true);
			new Auswertung(this);
			this.console.setBackground(false);
			
			// Auto-Save
			this.autosave();
			
			// Letzte Auswertung wiedergeben
			this.goToReplay = true;
			new Replay(this);
			
		} while (true);
	}
	
	private void autosave()
	{
		if (this.optionen.contains(SpielOptionen.AUTO_SAVE))
			this.speichern(true);
	}
	
	private void hauptmenue()
	{
		do
		{
			this.setEnableParameterChange(true);
			
			if (this.goToReplay)
			{
				new Replay(this);
			}
			else
			{			
				this.console.setHeaderText(
						this.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Hauptmenue(true), Colors.INDEX_NEUTRAL);
				
				ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
				if (!this.abgeschlossen)
					keys.add(new ConsoleKey("TAB",SternResources.Zugeingabe(true)));
				if (this.jahr > 0)
					keys.add(new ConsoleKey("1",SternResources.AuswertungWiederholen(true)));
				if (this.jahr > 0)
					keys.add(new ConsoleKey("2",SternResources.Statistik(true)));
				keys.add(new ConsoleKey("3",SternResources.Entfernungstabelle(true)));
				keys.add(new ConsoleKey("4",SternResources.PunkteAnzeigen(true)));
				if (!this.emailSpieler || this.abgeschlossen)
					keys.add(new ConsoleKey("9",SternResources.Spielinformationen(true)));
				if (!this.emailSpieler && this.jahr > 0 && !this.abgeschlossen)
					keys.add(new ConsoleKey("0",SternResources.SpielAbschliessen(true)));
				
				ConsoleInput consoleInput = this.console.waitForKeyPressed(keys, false);
				
	 			this.setEnableParameterChange(false);
	
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					this.console.clear();
				else if (!this.abgeschlossen && input.equals("\t"))
				{
					this.console.clear();
					// Zur Zugeingabe
					Zugeingabe ze = new Zugeingabe(this);
					if (ze.eingabeAbgeschlossen)
						break;
				}
				else if (!this.emailSpieler && this.jahr > 0 && !this.abgeschlossen && input.equals("0"))
					this.abschliessenFrage();
				else if (this.jahr > 0 && input.equals("1"))
					new Replay(this);
				else if (input.equals("3"))
					new Entfernungstabelle(this);
				else if (input.equals("4"))
					this.siegerehrung();
				else if ((!this.emailSpieler  || this.abgeschlossen) && input.equals("9"))
				{
					this.console.clear();
					new Spezialmenue(this);
				}
				else if (this.jahr > 0 && input.equals("2"))
				{
					this.console.clear();
					new Statistik(this);
				}
				else
				{
					console.appendText(SternResources.UngueltigeEingabe(true));
					console.lineBreak();
				}
			}
		}
		while (true);
		
	}
	private void abschliessenFrage()
	{
		// Spiel abschliessen
		this.console.appendText(SternResources.SpielAbschliessenFrage(true) + " ");
		String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
		
		if (input.equals(Console.KEY_YES))
			this.abschliessen(false);
		else
			this.console.outAbbruch();		
	}
	
	private void abschliessenPruefung(boolean background)
	{
		if (!this.abgeschlossen)
		{
			if (this.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL) && this.jahr >= this.maxJahre)
				this.abschliessen(background);
			else
			{
				// Wieviele Spieler sind noch uebrig?
				int countMensch = 0;
				int countBot = 0;
				
				for (Spieler sp: this.spieler)
				{
					if (!sp.istTot())
					{
						if (sp.istComputer())
							countBot++;
						else
							countMensch++;
					}
				}
				
				if ((countMensch <= 1 && countBot == 0) ||
					(countMensch == 0 && countBot > 0))
					this.abschliessen(background);
			}
		}
	}
	private void abschliessen(boolean background)
	{
		// Spiel abschliessen
		this.refreshSpieldauerSekunden();
		
		this.abgeschlossen = true;
		
		// Gratulation
		this.siegerehrung();
		
		// Frage nach Highscore
		if (!background)
		{
			this.console.setLineColor(Colors.INDEX_WEISS);
			this.console.appendText(SternResources.HighscoreFrage(true) + " ");
			
			String input = this.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			if (input.equals(Console.KEY_YES))
			{
				this.spielThread.addToHighscore(this.archiv.get(this.jahr), this.spieler);
			}
				
			// Statistik oefnnen
			this.console.clear();
			new Statistik(this);
			
			// Auto-Save
			if (this.optionen.contains(SpielOptionen.AUTO_SAVE))
			{
				this.optionen.remove(SpielOptionen.AUTO_SAVE);
				this.speichern(true);
			}
		}
		
		this.console.clear();
	}
	
	private void siegerehrung()
	{
		this.console.clear();
		
		// Wer ist der Sieger?
		int seq[] = Utils.listeSortieren(this.archiv.get(this.jahr).getPunkte(), true);		
		
		int platz = 1;
		
		for (int i = 0; i < this.anzSp; i++)
		{
			this.console.setLineColor(this.spieler[seq[i]].getColIndex());
			
			StringBuilder sb = new StringBuilder();
			
			if (i > 0 && this.archiv.get(this.jahr).getPunkte()[seq[i]] < this.archiv.get(this.jahr).getPunkte()[seq[i-1]])
				platz++;
			
			sb.append(
					SternResources.AbschlussPlatz(true, Integer.toString(platz)) + " ");

			sb.append(Utils.padStringLeft(this.spieler[seq[i]].getName(), Constants.SPIELER_NAME_MAX_LAENGE));
			
			sb.append(" " + Utils.padString(this.archiv.get(this.jahr).getAnzPl()[seq[i]], 2) + " "+SternResources.Planeten(true)+" ");
			sb.append(Utils.padString(this.archiv.get(this.jahr).getRaumer()[seq[i]], 5) + " "+SternResources.Raumer(true)+" ");
			sb.append(Utils.padString(this.archiv.get(this.jahr).getEprod()[seq[i]], 4) + " "+SternResources.AbschlussEprod(true)+" ");
			sb.append(Utils.padString(this.archiv.get(this.jahr).getPunkte()[seq[i]], 5) + " "+SternResources.Punkte(true)+".");
			
			this.console.appendText(sb.toString());
			
			if (i == (Console.MAX_LINES - 1))
				this.console.waitForTaste();
			else if (i < anzSp-1)
				this.console.lineBreak();
		}
		
		this.console.waitForTaste();
		this.console.clear();
	}
	
	public static Spiel importFromOldVega(String name, byte[] bytes)
	{
		Import imp = new Import(bytes);
		return imp.start(name);
	}
	
	private void punktestandBerechnen()
	{
		int raumer[] = new int[this.anzSp];
		int anzPl[] = new int[this.anzSp];
		int eprod[] = new int[this.anzSp];
		int punkte[] = new int[this.anzSp];
		
		// Planeten
		for (Planet pl: this.planeten)
		{
			int bes = pl.getBes();
			
			if (pl.getBes() == Constants.BESITZER_NEUTRAL)
				continue;
			
			anzPl[bes]++;
			
			eprod[bes] += pl.getEprod();
			
			// Raumer auf dem Planeten
			for (int spieler = 0; spieler < this.anzSp; spieler++)
			{
				int a = pl.getRaumerProSpieler(spieler);
				raumer[spieler]+=a;
			}
		}
		
		// Flugobjekte
		for (Flugobjekt obj: this.objekte)
		{
			int bes = obj.getBes();
			
			if (bes == Constants.BESITZER_NEUTRAL)
				continue;
			
			if (obj.getTyp() == ObjektTyp.RAUMER)
			{
				for (int spieler = 0; spieler < this.anzSp; spieler++)
				{
					int a = obj.getRaumerProSpieler(spieler);
					raumer[spieler]+=a;
				}				
			}
		}
		
		// Punkte berechnen
		for (int sp = 0; sp < this.anzSp; sp++)
		{
			punkte[sp] = punkteAusPlanetenBerechnen(anzPl[sp], this.anzSp);
		}
		
		this.archiv.put(this.jahr, new Archiv(punkte, raumer, anzPl, eprod));
	}
	
	private static int punkteAusPlanetenBerechnen(int anzPl, int anzSp)
	{
		return (int)Math.round(1000 * ((double)anzSp / (double)Constants.ANZAHL_SPIELER_MAX) *
			     ((double)anzPl / (double)Constants.ANZAHL_PLANETEN_MAX));
	}
	
	public void jahrVorbereiten()
	{
		// Preise berechnen
		this.preiseKaufArray = new ArrayList<Hashtable<ObjektTyp,Integer>>();
		this.preiseVerkaufArray = new ArrayList<Hashtable<ObjektTyp,Integer>>();

		// Preise fuer alle Spieler und neutrale Planeten berechnen
		for (int sp = 0; sp <= this.anzSp; sp++)
		{
			Hashtable<ObjektTyp,Integer> preiseKauf2 = new Hashtable<ObjektTyp,Integer>();
			Hashtable<ObjektTyp,Integer> preiseVerkauf2 = new Hashtable<ObjektTyp,Integer>();
			
			for (ObjektTyp typ: preiseKaufUntergrenze.keySet())
			{
				preiseKauf2.put(typ,
						preiseKaufUntergrenze.get(typ) + 
						Utils.random(
								preiseKaufObergrenze.get(typ)-preiseKaufUntergrenze.get(typ)+1));
				
				preiseVerkauf2.put(typ, 
						Utils.round((double)preiseKauf2.get(typ) * Constants.PREIS_KAUF_VERKAUF_RATIO));
			}
			
			preiseKaufArray.add(preiseKauf2);
			preiseVerkaufArray.add(preiseVerkauf2);
		}
		
		// Spielzuege loeschen
		this.spielzuege = new Hashtable<Integer, ArrayList<Spielzug>> ();
		
		// Spielzuege-Referenzcode anlegen (nur fuer E-Mail-Spiel)
		this.spielzuegeReferenzCodes = new UUID[this.anzSp];
		for (int i = 0; i < this.anzSp; i++)
			this.spielzuegeReferenzCodes[i] = UUID.randomUUID();
	}
		
	private void updateSpielfeldDisplay ()
	{
		this.updateSpielfeldDisplay(null, null);
	}
	
	private void updateSpielfeldDisplay (Hashtable<Integer,ArrayList<Byte>> frames)
	{
		this.updateSpielfeldDisplay(frames, null);
	}
	
	private void updateSpielfeldDisplay (ArrayList<Point2D.Double> markedField)
	{
		this.updateSpielfeldDisplay(null, markedField);
	}
	
	private void updateSpielfeldDisplayNeuesSpiel()
	{
		ArrayList<SpielfeldPlanetDisplayContent> plData = new ArrayList<SpielfeldPlanetDisplayContent>(this.anzPl);
		
		// Zeichne die Planeten
		for (int plIndex = 0; plIndex < this.anzPl; plIndex++)
		{
			
			if (this.planeten[plIndex].isNeutral())
				plData.add(new SpielfeldPlanetDisplayContent(
						"?",
						this.planeten[plIndex].getPos(),
						Colors.INDEX_NEUTRAL,
						false,
						null));
			else
				plData.add(new SpielfeldPlanetDisplayContent(
						"??",
						this.planeten[plIndex].getPos(),
						Colors.INDEX_WEISS,
						false,
						null));
				
		}
		
		if (this.screenDisplayContent == null)
			this.screenDisplayContent = new ScreenDisplayContent();
		
		this.screenDisplayContent.setSpielfeld(
				new SpielfeldDisplayContent(plData,
				null,
				null,
				null,
				null));
		
		this.spielThread.updateDisplay(this.screenDisplayContent);
	}
	
	private void updateSpielfeldDisplay (
			Hashtable<Integer, ArrayList<Byte>> frames, 
			ArrayList<Point2D.Double> markedField)
	{
		ArrayList<SpielfeldPlanetDisplayContent> plData = new ArrayList<SpielfeldPlanetDisplayContent>(this.anzPl);
		
		// Zeichne die Planeten
		for (int plIndex = 0; plIndex < this.anzPl; plIndex++)
		{
			ArrayList<Byte> frameCol = null;
			
			if (frames != null)
				frameCol = frames.get(plIndex);
			
			if (this.planeten[plIndex].isNeutral())
				plData.add(new SpielfeldPlanetDisplayContent(
						this.getPlanetenNameFromIndex(plIndex),
						this.planeten[plIndex].getPos(),
						Colors.INDEX_NEUTRAL,
						false,
						frameCol));
			else
			{
				boolean invers = (this.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH) && this.planeten[plIndex].istKommandozentrale());
				plData.add(new SpielfeldPlanetDisplayContent(
						this.getPlanetenNameFromIndex(plIndex),
						this.planeten[plIndex].getPos(),
						this.spieler[this.planeten[plIndex].getBes()].getColIndex(),
						invers,
						frameCol));
			}
				
		}
		
		Point lochPos = (this.loch != null && this.loch.isActive()) ? this.loch.getPos() : null;
		
		//Flugobjekte zeichnen (nur bei Simple-Stern)
		ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();

		if (this.optionen.contains(SpielOptionen.SIMPEL))
		{
			for (Flugobjekt obj: this.objekte)
			{
				if (obj.istZuLoeschen() || obj.getNeu() || obj.getTyp() == ObjektTyp.SCHWARZES_LOCH
					|| obj.getTyp() == ObjektTyp.KAPITULATION)
					continue;
				
				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
	 					null, null, obj.getExactPos(), Colors.INDEX_WEISS);
				
				lines.add(line);
			}
		}
				
		if (this.screenDisplayContent == null)
			this.screenDisplayContent = new ScreenDisplayContent();
		
		this.screenDisplayContent.setSpielfeld(
				new SpielfeldDisplayContent(plData,
				markedField,
				lines, // lines, wenn Flugobjekte gezeichnet werden sollen, sonst null.
				lochPos,
				null));
		
		if (!this.console.isBackground())
			this.spielThread.updateDisplay(this.screenDisplayContent);
	}
	
	private void updatePlanetenlisteDisplay (boolean newGame, boolean showNeutral)
	{
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Byte> textCol = new ArrayList<Byte>();
		
		// Sortiere nach Punktestand
		int[] reihenfolge = this.archiv.get(this.jahr) != null
				    ? Utils.listeSortieren(this.archiv.get(this.jahr).getPunkte(), true)
				    : Utils.sequentialList(this.anzSp);
		
	    for (int i = Constants.BESITZER_NEUTRAL; i < this.anzSp; i++)
		{
	    	if (i == Constants.BESITZER_NEUTRAL && !showNeutral)
	    		continue;
	    	
			int sp = i == Constants.BESITZER_NEUTRAL ? Constants.BESITZER_NEUTRAL : reihenfolge[i];
			boolean ersteZeile = true;
			
			for (int index = 0; index < this.anzPl; index++)
			{
				int plIndex = this.getPlanetenSortiert()[index];
				
				if (this.planeten[plIndex].getBes() != sp)
					continue;
				
				if (newGame && this.planeten[plIndex].getBes() == Constants.BESITZER_NEUTRAL)
					continue;
				
				if (ersteZeile)
				{
					if (sp == Constants.BESITZER_NEUTRAL)
					{
						text.add(SternResources.Neutral(false));
						textCol.add(Colors.INDEX_NEUTRAL);
					}
					else
					{
						text.add(this.spieler[sp].getName());
						textCol.add(this.spieler[sp].getColIndex());
					}
					ersteZeile = false;
				}
				
				String plName = newGame ?
						" ??" :
						" " + this.getPlanetenNameFromIndex(plIndex);
				String anzRaum = "     " + this.planeten[plIndex].getAnz(ObjektTyp.RAUMER);
				text.add(plName.substring(plName.length()-2, plName.length()) + 
						":" +
						anzRaum.substring(anzRaum.length()-5, anzRaum.length()));
				
				textCol.add(
						sp == Constants.BESITZER_NEUTRAL ?
								Colors.INDEX_NEUTRAL :
								this.spieler[sp].getColIndex());
			}
		}
		
		if (this.screenDisplayContent == null)
			this.screenDisplayContent = new ScreenDisplayContent();
		
		this.screenDisplayContent.setPlaneten(
				new PlanetenlisteDisplayContent(text, textCol));
		
		if (!this.console.isBackground())
			this.spielThread.updateDisplay(this.screenDisplayContent);
	}
	
	public void updateConsoleDisplay(ConsoleDisplayContent cont, boolean isBackground)
	{
		if (this.screenDisplayContent == null)
			this.screenDisplayContent = new ScreenDisplayContent();
		
		this.screenDisplayContent.setCons(cont);
		
		if (this.spielThread != null && !isBackground)
			this.spielThread.updateDisplay(this.screenDisplayContent);
	}
	
	
	private void switchDisplayMode (int mode)
	{
		if (this.screenDisplayContent == null)
			this.screenDisplayContent = new ScreenDisplayContent();
		
		this.screenDisplayContent.setModus(mode);
	}
	
	private String getPlanetenNameFromIndex(int plIndex)
	{
		if (this.mapPlanetIndexToName == null)
			this.buildPlanetenNameMap();
		
		return this.mapPlanetIndexToName.get(Integer.toString(plIndex));
	}
	
	private int getPlanetenIndexFromName(String name)
	{
		if (this.mapPlanetNameToIndex == null)
			this.buildPlanetenNameMap();

		try
		{
			int plIndex = Integer.parseInt(name) - 1;
			
			if (plIndex >= 0 && plIndex < this.anzPl)
				return plIndex;
			else
				return -1;
		}
		catch (Exception x)
		{
			return -1;
		}
	}
	
	public void buildPlanetenNameMap()
	{
		this.mapPlanetIndexToName = new Hashtable<String,String>();
		this.mapPlanetNameToIndex = new Hashtable<String,String>();
		this.planetenByPoint = new Hashtable<String,Integer>();
		
		String[] planetenUnsortiert = new String[this.anzPl];
		
		for (int index = 0; index < this.anzPl; index++)
		{
			String name = Integer.toString(index + 1);
			
			this.mapPlanetIndexToName.put(Integer.toString(index), name);
			this.mapPlanetNameToIndex.put(name, Integer.toString(index));
			
			planetenUnsortiert[index] = Utils.padString("00" + name, 2);
			
			Planet pl = this.planeten[index];
			String pos = Integer.toString(pl.getPos().getX()) + ";" + Integer.toString(pl.getPos().getY());
			
			this.planetenByPoint.put(pos, index);
		}
		
		this.planetenSortiert = Utils.listeSortieren(planetenUnsortiert, false);
	}
	
	public int getPlanetIndexFromPoint(Point pt)
	{
		if (this.planetenByPoint == null)
			this.buildPlanetenNameMap();
		
		String pos = Integer.toString(pt.getX()) + ";" + Integer.toString(pt.getY()); 
		
		Integer plIndex = this.planetenByPoint.get(pos);
		
		if (plIndex == null)
			return -1;
		else
			return plIndex.intValue();
	}
	
	public Point getPointFromFeldName(String name)
	{
		if (name.length() != 2)
			return null;
		
		int y = -1;
		
		try
		{
			y = name.toUpperCase().codePointAt(0) - 65;
		}
		catch (Exception x) {}
		
		int x = -1;
		
		try
		{
			x = name.toUpperCase().codePointAt(1) - 65;
		}
		catch (Exception xx) {}
		
		if (x >= 0 && x < Constants.FELD_MAX_X && y >= 0 && y < Constants.FELD_MAX_Y)
			return new Point(x, y);
		else
			return null;
	}
	
	public String getFeldNameFromPoint(Point pt)
	{
		int plIndex = this.getPlanetIndexFromPoint(pt);
		
		if (plIndex != -1)
			return this.getPlanetenNameFromIndex(plIndex);
		else
			return Spiel.getSectorNameFromPoint(pt);
		
	}
	
	public static String getSectorNameFromPoint(Point pt)
	{
		return Character.toString((char)(65+(pt.getY()))) +
			   Character.toString((char)(65+(pt.getX())));
		
	}
	
	private void outBuendnisStruktur(int plIndex)
	{
		Planet pl = this.planeten[plIndex];
		
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		for (int sp = 0; sp < this.anzSp; sp++)
		{
			if (!pl.istBuendnisMitglied(sp))
				continue;
			
			if (counter >= 3)
			{
				this.console.appendText(sb.toString());
				this.console.lineBreak();
				sb = new StringBuilder();
				counter = 0;
			}
			
			if (sb.length() > 0)
				sb.append(", ");
			
			sb.append(this.spieler[sp].getName());
			sb.append(" (");
			sb.append(pl.getAnzProTypUndSpieler(ObjektTyp.RAUMER, sp));
			sb.append(" "+SternResources.Raumer(true)+")");
				
			counter++; 							
		}
		
		this.console.appendText(sb.toString());
	}

	private String hauptmenueHeaderGetJahrText()
	{
		if (this.abgeschlossen)
		{
			return SternResources.AbgeschlossenesSpiel(true, Integer.toString(this.jahr+1));
		}
		else
		{
			if (this.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL))
				return SternResources.InventurJahr2(
						true, 
						Integer.toString(this.jahr+1), 
						Integer.toString(this.maxJahre));
			else
				return SternResources.InventurJahr1(true, Integer.toString(this.jahr+1));
		}
	}
		
	private Hashtable<Integer,ArrayList<Byte>> getSimpleFrameObjekt(int plIndex, byte col)
	{
		ArrayList<Byte> frameCol = new ArrayList<Byte>();
		frameCol.add(col);
		
		Hashtable<Integer,ArrayList<Byte>> frames = new Hashtable<Integer,ArrayList<Byte>>();
		frames.put(plIndex, frameCol);
		
		return frames;
	}
	
	private ArrayList<Point2D.Double> getSimpleMarkedField(Point pt)
	{
		ArrayList<Point2D.Double> markedFields = new ArrayList<Point2D.Double>();
		markedFields.add(Utils.toPoint2D(pt));
		
		return markedFields;
	}
	
	private ArrayList<Point2D.Double> getSimpleMarkedField(Point2D.Double pt)
	{
		ArrayList<Point2D.Double> markedFields = new ArrayList<Point2D.Double>();
		markedFields.add(pt);
		
		return markedFields;
	}
	
	private ArrayList<Integer> getFehlendeZugeingaben()
	{
		ArrayList<Integer> retval = new ArrayList<Integer>();
		
		for (int sp = 0; sp < this.anzSp; sp++)
			if (!this.spielzuege.containsKey(sp) && !this.spieler[sp].istTot())
				retval.add(sp);
		
		return retval;
	}
	
	public KeyEventExtended waitForKeyInput()
	{
		if (!this.console.isBackground())
			return this.spielThread.waitForKeyInput();
		else
			// Für Hintergrundbetrieb (Auswertung am STERN-Server immer nur TAB zurückgeben)
			return new KeyEventExtended(
					new KeyEvent(
							new Panel(), 
							0, 
							0, 
							0,
		                    KeyEvent.VK_TAB,
		                    '\t'), 
						"",
						"");
	}
	
	public void pause(int milliseconds)
	{
		if (!this.console.isBackground())
			this.spielThread.pause(milliseconds);
	}
	
	private void refreshSpieldauerSekunden()
	{
		if (this.abgeschlossen)
			return;
		
		long timeNow = System.currentTimeMillis();
		
		int diffSekunden = Utils.round((double)(timeNow - this.currentTime) / 1000.0);
		this.spieldauerSekunden += diffSekunden;
		
		this.currentTime = timeNow;
	}
	
	// =========================================
 	private static class Import
	{
		// Import von Spielstaenden aus Vega f�r Windows 3.1
		private byte[] bytes;
		private int pos;
		
		private Import(byte[] bytes)
		{
			this.pos = 0;
			this.bytes = bytes;
		}
		
		public Spiel start(String spielName)
		{
			Spiel spiel = new Spiel();
			spiel.name = spielName;
			
			if (this.inchar() != 'V') // Erstes Zeichen ist immer "V"
				return null;
			
			if (this.inasc() != 6) // Version muss 6 sein
				return null;
			
			spiel.initial = false;
			
			// In frueheren Versionen war das Spielfeld immer gleich gross
			spiel.xOff = 0;
			spiel.yOff = 0;
			spiel.breite = Constants.FELD_MAX_X;
			spiel.hoehe = Constants.FELD_MAX_Y;
			
			@SuppressWarnings("unused")
			short ladecode = this.inmki();
			
			spiel.startDatum = Utils.getDateFromOldVega(this.getDate(this.inmki()));
			spiel.spieldauerSekunden = this.inmkl() * 60;
			
			short setup = this.inmki();
			@SuppressWarnings("unused")
			short raum_beginn = this.inmki(); // Wird im neuen Spiel nicht mehr gebraucht
		    @SuppressWarnings("unused")
			int eingabezeitMinuten = this.inmki(); // Wird im neuen Spiel nicht mehr gebraucht
			spiel.maxJahre = this.inmki();
			spiel.jahr = this.inmki()-1; // Achtung: Frueher begann das Spiel mit jahr=1, heute mit jahr=0!
			
			spiel.optionen = new HashSet<SpielOptionen>();
			if ((setup & 32) > 0)
				spiel.optionen.add(SpielOptionen.KOMMANDOZENTRALEN);
//			if ((setup & 8) > 0)
			spiel.optionen.add(SpielOptionen.SCHWARZES_LOCH);
//			if ((setup & 1) > 0  && spiel.eingabezeitMinuten > 0)
//				spiel.optionen.add(SpielOptionen.BEGRENZTE_EINGABEZEIT);
			if ((setup & 4) > 0 && spiel.maxJahre > 0)
				spiel.optionen.add(SpielOptionen.KEIN_ENDLOSSPIEL);
//			if ((setup & 2) > 0)
			spiel.optionen.add(SpielOptionen.AUSSERIRDISCHE);
//			if ((setup & 16) > 0)
//				spiel.optionen.add(SpielOptionen.AUTO_SAVE);
			
			spiel.optionen.add(SpielOptionen.FESTUNGEN);
			spiel.optionen.add(SpielOptionen.KOMPAKTES_SPIELFELD);
		
			// Spieler
			spiel.anzSp = this.inasc();
			spiel.spieler = new Spieler[spiel.anzSp];
			
			int[] kzPlanet = new int[spiel.anzSp]; 
			
			for (int t = 0; t < spiel.anzSp; t++)
			{
				String name = this.instring();
				@SuppressWarnings("unused")
				int pw;
				
				if ((setup & 64) > 0)
					pw = this.inmkl(); // Wird nicht mehr benoetigt
				
				if ((setup & 32) > 0)
					kzPlanet[t] = this.inasc();
				
				this.inmkl(); // Dummy
				
				spiel.spieler[t] = new Spieler(name, "", (byte)(t+Colors.SPIELER_FARBEN_OFFSET), false, false);
			}
			
			// Planeten
			spiel.anzPl = this.inasc() + 1; // !!!!
			spiel.planeten = new Planet[spiel.anzPl];
			
			for (int t = 0; t < spiel.anzPl; t++)
			{
				Hashtable<ObjektTyp, Integer> anz = new Hashtable<ObjektTyp, Integer>();
				
				short anz_raum_pl = this.inmki();
				if (anz_raum_pl > 0)
					anz.put(ObjektTyp.RAUMER, (int)anz_raum_pl);
				
			    short anz_aufkl_pl = this.inmki();
			    if (anz_aufkl_pl > 0)
					anz.put(ObjektTyp.AUFKLAERER, (int)anz_aufkl_pl);
			    
				short anz_patr_pl = this.inmki();
				if (anz_patr_pl > 0)
					anz.put(ObjektTyp.PATROUILLE, (int)anz_patr_pl);
				
				short anz_min50_pl = this.inmki();
				if (anz_min50_pl > 0)
					anz.put(ObjektTyp.MINE50, (int)anz_min50_pl);
				
				short anz_min100_pl = this.inmki();
				if (anz_min100_pl > 0)
					anz.put(ObjektTyp.MINE100, (int)anz_min100_pl);
				
				short anz_min250_pl = this.inmki();
				if (anz_min250_pl > 0)
					anz.put(ObjektTyp.MINE250, (int)anz_min250_pl);
				
				short anz_min500_pl = this.inmki();
				if (anz_min500_pl > 0)
					anz.put(ObjektTyp.MINE50, (int)anz_min500_pl);
				
				short anz_etrans_pl = this.inmki();
				if (anz_etrans_pl > 0)
					anz.put(ObjektTyp.TRANSPORTER, (int)anz_etrans_pl);
				
				short anz_mr_pl = this.inmki();
				if (anz_mr_pl > 0)
					anz.put(ObjektTyp.MINENRAEUMER, (int)anz_mr_pl);
				
				short xpos = this.inasc();
				short ypos = this.inasc();
				
				Point pos = new Point(xpos, ypos);
				
				short besitzer = (short)(this.inasc() - 1); // !!!
				
				short festung = this.inasc();
				Festung fest = null;
				if (festung > 0)
				{
					short intakt = this.inasc();
					fest = new Festung(festung, intakt);
				}
				
				short evorrat = this.inmki();
				short eprod = this.inasc();
				short eraum = this.inasc();
				
				Buendnis buendnis = null;
				
				short allianz = this.inasc(); // Boolean
				if (allianz > 0)
				{
					buendnis = new Buendnis(spiel.anzSp);
					
					for (int tt = 0; tt < spiel.anzSp; tt++)
					{
						int all_pl = this.inmkl();  // !!!!!!!!

						if (all_pl >= 0)
							buendnis.addRaumer(tt, all_pl);
					}
				}
			    
			    Kommandozentrale kz = null;
			    if (spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN))
			    {
			    	for (int sp = 0; sp < spiel.anzSp; sp++)
			    	{
			    		if (kzPlanet[sp] == t)
			    		{
			    			kz = new Kommandozentrale(sp);
			    			break;
			    		}
			    	}
			    }
			    
			    if (buendnis!= null)
			    {
			    	// Gesamtzahl Raumer anpassen
			    	anz.put(ObjektTyp.RAUMER, buendnis.getSum());
			    }
			    
			    spiel.planeten[t] = 
			    		new Planet(
			    				pos, 
			    				buendnis, 
			    				anz, 
			    				besitzer, 
			    				fest, 
			    				evorrat, 
			    				eprod, 
			    				eraum, kz);
			}
			
			// Flugobjekte
			short obj = this.inmki();
			spiel.objekte = new ArrayList<Flugobjekt>(obj);
			
			if (obj > 0)
			{
				for (int t = 0; t < obj; t++)
				{
					short spl = this.inasc();
					short zpl = this.inasc();
					short vonx = this.inasc();
					short vony = this.inasc();
					Point start = new Point(vonx, vony);
					short nachx = this.inasc();
					short nachy = this.inasc();
					Point ziel = new Point(nachx, nachy);
					
					// Stimmen die Start- und Zielkoordinaten mit den jeweiligen Koordinaten
					// der angeblichen Start- und Zielplaneten uberein?
					if (!start.equals(spiel.planeten[spl].getPos()))
						spl = Constants.KEIN_PLANET;
					
					if (!ziel.equals(spiel.planeten[zpl].getPos()))
						zpl = Constants.KEIN_PLANET;
					
					short pos = this.inasc();
					short art = this.inasc();
					
					ObjektTyp typ = ObjektTyp.RAUMER;
					boolean transfer = false;
					
					switch (art)
					{
					case 1:		typ = ObjektTyp.RAUMER;
								transfer = false;
								break;
					case 2:		typ = ObjektTyp.AUFKLAERER;
								transfer = false;
								break;
					case 3:		typ = ObjektTyp.PATROUILLE;
								transfer = false;
								break;
					case 4:		typ = ObjektTyp.PATROUILLE; // im Transfer
								transfer = true;
								break;
					case 5:		typ = ObjektTyp.MINE50;
								transfer = false;
								break;
					case 6:		typ = ObjektTyp.MINE100;
								transfer = false;
								break;
					case 7:		typ = ObjektTyp.MINE250;
								transfer = false;
								break;
					case 8:		typ = ObjektTyp.MINE500;
								transfer = false;
								break;
					case 9:		typ = ObjektTyp.MINE50;
								transfer = true;
								break;
					case 10:	typ = ObjektTyp.MINE100;
								transfer = true;
								break;
					case 11:	typ = ObjektTyp.MINE250;
								transfer = true;
								break;
					case 12:	typ = ObjektTyp.MINE500;
								transfer = true;
								break;
					case 13:	typ = ObjektTyp.RAUMER;
								transfer = false;
								break;
					case 14:	typ = ObjektTyp.TRANSPORTER;
								transfer = false;
								break;
					case 15:	typ = ObjektTyp.MINENRAEUMER; // Auf dem Hinweg, also in Aktion
								transfer = false;
								break;
					case 16:	typ = ObjektTyp.MINENRAEUMER; // Auf dem Rueckweg, also tatenlos
								transfer = true;
								break;
					case 17:	// !!! Loch war als Flugobjekt modelliert, um es in zufaelliger Reihenfolge zu bewegen
								continue;
					}
					
					short anz = this.inmki(); // TRANSPORTER und anz > 90 --> KOmmandozentrale
					
					Kommandozentrale kz = null;
					if (typ == ObjektTyp.TRANSPORTER && anz > 90)
						kz = new Kommandozentrale(anz - 90 - 1);
					
					Buendnis buendnis = null;
					if (art == 13) // Buendnisraumer
					{
						buendnis = new Buendnis(spiel.anzSp);
						
						for (int tt = 0; tt < 6; tt++)
						{
							int all_obj = this.inmkl(); // !!!
							if (all_obj >= 0 && tt < spiel.anzSp)
								buendnis.addRaumer(tt, all_obj);
						}
					}
					short abs = (short)(this.inasc() - 1);
					if (abs >= spiel.anzSp)
						abs = Constants.BESITZER_NEUTRAL; // Ausserirdische
					
					@SuppressWarnings("unused")
					short x = this.inmki(); // wird nicht ausgewertet
					@SuppressWarnings("unused")
					short y = this.inmki(); // wird nicht ausgewertet

					@SuppressWarnings("unused")
					short gelandet = this.inasc(); // wird nicht ausgewertet
					
					Flugobjekt objekt = new Flugobjekt(spl, zpl, start, ziel,
							pos, typ,
							anz, abs, transfer,
							buendnis, kz);
					
					spiel.objekte.add(objekt);
				}
			}
			
			// Schwarzes Loch			
			if (spiel.optionen.contains(SpielOptionen.SCHWARZES_LOCH))
			{
				
				short loch_time = this.inmki();
				short loch = this.inasc(); // Boolean
				short lochx = 0, lochy = 0;
				
				if (loch > 0)
				{
					lochx = this.inasc();
					lochy = this.inasc();
				}
				
				spiel.loch = new Loch((loch > 0), new Point(lochx, lochy), loch_time);
			}
			else
				spiel.loch = new Loch(false, new Point(0,0), 0);
			
			// Punktestand-Historie
			spiel.archiv = new Hashtable<Integer,Archiv>();
			
			for (int j = 0; j <= spiel.jahr; j++)
			{
				int[] punkte = new int[spiel.anzSp];
				int[] raumer = new int[spiel.anzSp];
				int[] anzPl = new int[spiel.anzSp];
				int[] eprod = new int[spiel.anzSp];

				for (int sp = 0; sp < spiel.anzSp; sp++)
				{
					punkte[sp] = this.inmkl();
				    raumer[sp] = this.inmkl();
				    anzPl[sp] = this.inasc();
				    eprod[sp] = this.inmki();
				    
					// Punkte nach dem neuen Schema neu berechnen
					punkte[sp] = Spiel.punkteAusPlanetenBerechnen(anzPl[sp], spiel.anzSp);
				}
								
				if (j < spiel.jahr)
					spiel.archiv.put(j, new Archiv(punkte, raumer, anzPl, eprod));
			}
			
			// Minen
			spiel.minen = new Hashtable<String,Mine>();
			
			short mcount = this.inmki();
			if (mcount > 0)
			{
				// Zunaechst nur die Minen-Historie. Die aktuellen Staerken kommen spaeter
				for (int t = 0; t < mcount; t++)
				{
					short mrec = this.inmki();
					
					int x = mrec % 20;
					int y = (mrec % 380) / 20;
					int sp = (mrec % 2280) / 380;
					int staerke = mrec / 2280;
					
					ObjektTyp typ = ObjektTyp.MINE50; 
					switch(staerke)
					{
					case 0: typ = ObjektTyp.MINE50; break;
					case 1: typ = ObjektTyp.MINE100; break;
					case 2: typ = ObjektTyp.MINE250; break;
					case 3: typ = ObjektTyp.MINE500; break;
					}
										
					MineHistorie mineHist = new MineHistorie(sp,typ);
					
					Point pos = new Point(x,y);
					
					Mine mine = spiel.minen.get(pos.getString()); // Ist auf dem Sektor schon eine Mine registriert?
					if (mine == null)
					{
						mine = new Mine(pos, 0, null);
						spiel.minen.put(pos.getString(), mine);
					}
					mine.addHistorie(mineHist);
				}
			}
			
			char dummy = this.inchar();
			
			if (dummy == '1')
			{
				// Aktuelle Staerken der Minen
				while (this.pos < this.bytes.length)
				{
					short mx = this.inasc();
					short my = this.inasc();
					short staerke = this.inmki();
					
					Point pt = new Point(mx,my);
					
					Mine mine = spiel.minen.get(pt.getString());
					if (mine == null)
					{
						mine = new Mine(pt, staerke, null);
						spiel.minen.put(pt.getString(), mine);
					}
					else
						mine.setStaerke(staerke);
				}
			}
			
			// Spiel abgeschlossen?
			if (spiel.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL))
			{
				if (spiel.jahr >= spiel.maxJahre)
					spiel.abgeschlossen = true;
			}
			
			spiel.buildPlanetenNameMap();
			spiel.punktestandBerechnen();
			spiel.jahrVorbereiten();
			
			return spiel;
		}
		
		private char inchar()
		{
			return (char)this.getByteValue();
		}
		private short inasc()
		{
			return this.getByteValue();
		}
		
		private short inmki()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			
			return (short)(byte0 + byte1 * 256 - 65536);
		}
		
		private int inmkl()
		{
			short byte0 = this.getByteValue();
			short byte1 = this.getByteValue();
			short byte2 = this.getByteValue();
			short byte3 = this.getByteValue();
			
			return (int)(byte0 + byte1 * 256 + byte2 * 256 * 256 + byte3 * 256 * 256 * 256);
		}
		
		private String instring()
		{
			String retval = "";
			
			while ((char)this.bytes[this.pos] != '\r')
				retval = retval + this.inchar();
			
			this.pos += 2; // wegen \r\n
			
			return retval;
		}
		
		private short getByteValue()
		{
			short b = (short)this.bytes[this.pos];
			if (b < 0)
				b += 256;
			
			this.pos++;
			
			return b;
		}
		
		private String getDate(short val)
		{
			String day = "0" + Integer.toString(((val + 32767) % (31*100))/100 + 1);
			String month = "0" + Integer.toString((val + 32767) / (100*31) + 1);
			String year = Integer.toString((val + 32767) % 100 + 1980);
			
			return year + month.substring(month.length()-2, month.length()) + day.substring(day.length()-2, day.length()); 
		}
	}
 	
 	// =====================
 	
 	public class PlanetenInfo
 	{
 		public int x;
 		public int y;
 		public byte col;
 		
 		public PlanetenInfo(int x, int y, byte col)
 		{
 			this.x = x;
 			this.y = y;
 			this.col = col;
 		}
 	}
 	// =====================
 	protected class Zugeingabe
 	{
 		private Spiel spiel;
 		private int spielerJetzt;
 		public boolean eingabeAbgeschlossen;
 		public boolean kapituliert;
 		
 		@SuppressWarnings("unchecked")
 		public Zugeingabe(Spiel spiel)
 		{
 			this.spiel = spiel;

 			// Planeten und Flugobjekte sichern
 			Planet[] planetenKopie = (Planet[])Utils.klon(this.spiel.planeten);
			ArrayList<Flugobjekt> objekteKopie = (ArrayList<Flugobjekt>)Utils.klon(this.spiel.objekte);
			
			if (this.spiel.emailSpieler)
			{
				int spIndex = -1;
				
				for (int i = 0; i < this.spiel.anzSp; i++)
				{
					if (this.spiel.spielzuegeReferenzCodes[i] != null)
					{
						spIndex = i;
						break;
					}
				}
				
				// Hat der Spieler bereits seine Zuege eingegeben?
				if (this.spiel.spielzuege != null && this.spiel.spielzuege.containsKey(spIndex))
				{
					this.spiel.console.appendText(
								SternResources.ZugeingabeSpielzuegeSchonEingegeben(true));
					this.spiel.console.lineBreak();
					return;
				}
				
				this.zugeingabeSpieler(spIndex);
				
 				this.spiel.planeten = (Planet[])Utils.klon(planetenKopie);
 				this.spiel.objekte = (ArrayList<Flugobjekt>)Utils.klon(objekteKopie);
 				
 				// Spielzuege abschicken
 				SpielzuegeEmailTransport set = new SpielzuegeEmailTransport(
 												this.spiel.spielzuegeReferenzCodes[spIndex],
 												Constants.MIN_BUILD,
 												this.spiel.spielzuege.get(spIndex));
 				
 				boolean success = false;
 				PostMovesResult result = PostMovesResult.FEHLER;
 				
 				do
 				{
	 				if (this.spiel.optionen.contains(SpielOptionen.SERVER_BASIERT))
	 				{
	 					// Spielzuege an den Server schicken
	 					result = this.spiel.spielThread.postMovesToServer(
								spiel.name,
								this.spiel.spieler[this.spielerJetzt].getName(),
								set);
	 						
	 					if (result == PostMovesResult.BENUTZER_NICHT_ANGEMELDET)
	 					{
	 						this.spiel.console.appendText(
	 								SternResources.SpielerNichtAngemeldet(
	 										true,
	 										this.spiel.spieler[this.spielerJetzt].getName()));
	 						this.spiel.console.lineBreak();
	 					}
	 					else if (result != PostMovesResult.FEHLER)
	 					{
	 						this.spiel.console.appendText(
	 								SternResources.ZugeingabePostMovesSuccess(true));
	 						this.spiel.console.lineBreak();
	 						success = true;
	 					}
	 				}
	 				else
	 				{
	 					// E-Mail erzeugen
		 				this.spiel.console.appendText(
		 						SternResources.ZugeingabeEMailErzeugt(true));
		 				this.spiel.console.lineBreak();
		 				this.spiel.console.appendText(
		 						SternResources.ZugeingabeEMailErzeugt2(true));
		 				this.spiel.console.lineBreak();
		 				
		 				// E-Mail erzeugen
		 				String subject = "[Stern] " + this.spiel.name;
		 				
		 				String bodyText = 
		 						SternResources.ZugeingabeEMailBody(false,
		 								this.spiel.name,
		 								Integer.toString(this.spiel.jahr + 1),
		 								this.spiel.spieler[spIndex].getName(),
		 								ReleaseGetter.format(ReleaseGetter.getRelease()),
		 								this.spiel.spieler[spIndex].getName(),
		 								this.spiel.getName());
		 				
		 				success = this.spiel.spielThread.launchEmail(
		 						this.spiel.getEmailAdresseSpielleiter(), 
		 						subject, 
		 						bodyText, 
		 						set);
		 				
	 				}
	 				
	 				if (!success)
	 				{
	 					this.spiel.console.appendText(
	 							SternResources.ZugeingabePostMovesError(true));
	 					this.spiel.console.lineBreak();
	 					
	 					ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
	 					
	 					keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
	 					keys.add(new ConsoleKey(
	 							SternResources.AndereTaste(true),
	 							SternResources.NochmalVersuchen(true)));
						
	 					ConsoleInput consoleInput = this.spiel.console.waitForKeyPressed(keys, false);
						
						if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
						{
							success = true; // Ja, das ist Absicht
						}
	 				}
						
	 				
 				} while (!success);
 				
 				do
 				{
 					// Endlosschleife!
 					if (this.spiel.optionen.contains(SpielOptionen.SERVER_BASIERT))
 					{
 						this.spiel.spielThread.triggerGameInfoUpdate();
 						
 						if (this.spiel.jahr >= this.spiel.maxJahre - 1)
 						{
 							this.spiel.console.appendText(
	 								SternResources.LetztesJahr(true));
 							this.spiel.console.lineBreak();
 							this.spiel.console.appendText(
 									SternResources.LetztesJahr2(true));
 						}
 						else
 						{
	 						if (result == PostMovesResult.WARTE)
		 						this.spiel.console.appendText(
		 								SternResources.WartenBisNaechsteZugeingabe(true));
	 						else
	 						{
	 							this.spiel.console.appendText(
		 								SternResources.AuswertungVerfuegbar(true));
	 							this.spiel.console.lineBreak();
	 							this.spiel.console.appendText(
		 								SternResources.AuswertungVerfuegbar2(true));
	 						}
 						}
 					}
 					else
 						this.spiel.console.appendText(
		 						SternResources.ZugeingabeEMailEndlosschleife(true));
 					
	 				this.spiel.console.waitForTaste();
 				} while (true);
			}
			else
			{
				// Computer geben ihre Zuege ein
				for (int spIndex = 0; spIndex < this.spiel.anzSp; spIndex++)
				{
					Spieler sp = this.spiel.spieler[spIndex];
					
					if (!this.spiel.spielzuege.containsKey(spIndex))
					{
						if (sp.istComputer())
						{
							Spiel.zugeingabeKi(this.spiel, spIndex);
	
			 				this.spiel.planeten = (Planet[])Utils.klon(planetenKopie);
			 				this.spiel.objekte = (ArrayList<Flugobjekt>)Utils.klon(objekteKopie);
							continue;
						}
					}
				}
				
				do
				{
					// Welche Spieler haben ihre Zuege noch nicht eingegeben?
					ArrayList<Integer> fehlendeZuege = this.spiel.getFehlendeZugeingaben();
					this.eingabeAbgeschlossen = (fehlendeZuege.size() == 0);
					
					if (this.eingabeAbgeschlossen)
						return;
					
					//Die Spieler geben ihre Zuege ein oder importieren sie aus E-Mail
					this.spiel.console.setHeaderText(
								this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.ZugeingabeTitel(true), Colors.INDEX_NEUTRAL);
					
					ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
					StringBuilder sb = new StringBuilder();
					
					boolean spielerAusstehend = false;

					for (int i = 0; i < fehlendeZuege.size(); i++)
					{
						int spIndex = fehlendeZuege.get(i).intValue();
						if (!this.spiel.istSpielerEmail(spIndex) && !this.spiel.emailSpieler)
						{
							keys.add(new ConsoleKey(
									Integer.toString(spIndex + 1),
									this.spiel.spieler[spIndex].getName()));
							spielerAusstehend = true;
						}
						
						if (sb.length() > 0)
							sb.append(", ");
						sb.append(this.spiel.spieler[spIndex].getName());
					}
					
					if (this.spiel.optionen.contains(SpielOptionen.EMAIL))
						keys.add(
								new ConsoleKey("9",SternResources.ZugeingabeEMailAktionen(true)));
					
					keys.add(new ConsoleKey("ESC",SternResources.Hauptmenue(true)));
					if (spielerAusstehend)
						keys.add(new ConsoleKey("TAB",SternResources.ZugeingabeZufaelligerSpieler(true)));
										
					int spIndex = -1;
					
					this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
					this.spiel.console.appendText(SternResources.ZugeingabeWartenAufSpielzuege(true));
					this.spiel.console.lineBreak();
					this.spiel.console.setLineColor(Colors.INDEX_WEISS);
					this.spiel.console.appendText(sb.toString());
					this.spiel.console.lineBreak();
					
					ConsoleInput consoleInput = this.spiel.console.waitForKeyPressed(keys, false);
					String input = consoleInput.getInputText().toUpperCase();
					
					if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.spiel.console.clear();
						break;
					}
					else if (input.equals("9"))
					{
						this.emailMenu();
					}
					else
					{
						boolean ok = false;
						
						try
						{
							if (spielerAusstehend && input.equals("\t"))
							{
								spIndex = Utils.random(spiel.anzSp);
								
								for (int i = 0; i < spiel.anzSp; i++)
								{
									if (fehlendeZuege.contains(spIndex) && !(this.spiel.istSpielerEmail(spIndex) && !this.spiel.emailSpieler))
									{
										ok = true;
										break;
									}
									spIndex = (spIndex + 1) % spiel.anzSp;
								}
							}
							else
							{
								spIndex = Integer.parseInt(input) - 1;
								
								if (fehlendeZuege.contains(spIndex) && !(this.spiel.istSpielerEmail(spIndex) && !this.spiel.emailSpieler))
									ok = true;
							}
						}
						catch (Exception x)
						{ }
						
						if (ok)
						{
							boolean raus = this.zugeingabeSpieler(spIndex);
							
							if (raus)
								this.spiel.spielzuege.remove(spIndex);
							
			 				this.spiel.planeten = (Planet[])Utils.klon(planetenKopie);
			 				this.spiel.objekte = (ArrayList<Flugobjekt>)Utils.klon(objekteKopie);
			 				
			 				this.spiel.console.clear();
			 				
			 				// Auto-Save
			 				this.spiel.autosave();
						}
						else
							this.spiel.console.outUngueltigeEingabe();
					}
				} while (true);
			}
 		}
 		
 		private boolean zugeingabeSpieler(int spieler)
 		{
 			this.spielerJetzt = spieler; 			
			this.spiel.console.clear();
			
			ArrayList<Spielzug> sz = new ArrayList<Spielzug>();
			this.spiel.spielzuege.put(spieler, sz);
			boolean simpel = this.spiel.optionen.contains(SpielOptionen.SIMPEL);
				
			boolean exit = false;
			this.kapituliert = false;
			
 			do
 			{ 				
 				this.spiel.console.setHeaderText(
 							this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName(),
 							this.spiel.spieler[this.spielerJetzt].getColIndex());
 				
 				// Moegliche Keys
 				ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 				
 				keys.add(new ConsoleKey("TAB",SternResources.ZugeingabeFertig(true)));
 				keys.add(new ConsoleKey("-",SternResources.ZugeingabeUndo(true)));
 				
 				if (!this.kapituliert)
 				{
 					if (!simpel)
 						keys.add(new ConsoleKey("0",SternResources.ZugeingabePlanet(true)));
 					else
 						keys.add(new ConsoleKey("0",SternResources.ZugeingabePlaneteninfo(true)));
 					
 					keys.add(new ConsoleKey("1",SternResources.ZugeingabeRaumer(true)));
 					
 					if (!simpel)
 					{
 						keys.add(new ConsoleKey("2",SternResources.ZugeingabeBuendRaumer(true)));
 						keys.add(new ConsoleKey("3",SternResources.ZugeingabeAufklaerer(true)));
 						keys.add(new ConsoleKey("4",SternResources.ZugeingabePatrouille(true)));
 						keys.add(new ConsoleKey("5",SternResources.ZugeingabeTransporter(true)));
 						keys.add(new ConsoleKey("6",SternResources.ZugeingabeMine(true)));
 						keys.add(new ConsoleKey("7",SternResources.ZugeingabeMinenraeumer(true)));
 						
 						keys.add(new ConsoleKey("8",SternResources.ZugeingabeBuendnis(true)));
 						keys.add(new ConsoleKey("9",SternResources.ZugeingabeMehr(true)));
 					}
 					else
 					{
 						keys.add(new ConsoleKey("5",SternResources.ZugeingabeKapitulieren(true)));
 						keys.add(new ConsoleKey("9",SternResources.ZugeingabeInventur(true)));
 					}
 				}
 				
 				// Gibt es noch gestoppte Objekte?
 				boolean raus = this.gestoppteObjekte();
				if (raus)
					return true;
		
 				ConsoleInput consoleInput = this.spiel.console.waitForKeyPressed(keys, true);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					this.spiel.console.clear();
				else if (input.equals("\t"))
					exit = this.ende();
				else if (!kapituliert && input.equals("1"))
					this.raumer(false);
				else if (!kapituliert && !simpel && (input.equals("2")))
					this.raumer(true);
				else if (!kapituliert && !simpel && (input.equals("3")))
					this.aufklaererPatrouillenTransporter(ObjektTyp.AUFKLAERER);
				else if (!kapituliert && !simpel && (input.equals("4")))
					this.aufklaererPatrouillenTransporter(ObjektTyp.PATROUILLE);
				else if (!kapituliert && !simpel && (input.equals("5")))
					this.aufklaererPatrouillenTransporter(ObjektTyp.TRANSPORTER);
				else if (!kapituliert && simpel && (input.equals("5")))
					this.kapitulieren();
				else if (!kapituliert && !simpel && (input.equals("6")))
					this.minenUndRaeumer(ObjektTyp.MINE50);
				else if (!kapituliert && !simpel && (input.equals("7")))
					this.minenUndRaeumer(ObjektTyp.MINENRAEUMER);
				else if (!kapituliert && !simpel && (input.equals("8")))
					this.buendnis();
				else if (!kapituliert && !simpel && (input.equals("0")))
					this.planeteneditor();
				else if (!kapituliert && simpel && (input.equals("0")))
					this.planeteninfo();
				else if (!kapituliert && input.equals("9"))
				{
					if (simpel)
						this.inventur();
					else
						this.zugeingabeSpielerMehr(spieler);
				}
				else if (input.equals("-"))
				{
					raus = this.undo();
					if (raus)
						return true;
				}
				else
						this.spiel.console.outUngueltigeEingabe();
				
				if (spiel.emailSpieler)
					spiel.updatePlanetenlisteDisplay(false, spiel.isSimple());

 			} while (!exit);
 			
 			return false;
 		}
 		
 		private boolean zugeingabeSpielerMehr(int spieler)
 		{
 			this.spielerJetzt = spieler; 			
			this.spiel.console.clear();
			
			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
			
			keys.add(new ConsoleKey("ESC",SternResources.Zurueck(true)));
			
			keys.add(new ConsoleKey("0",SternResources.ZugeingabeRaumerAufPlanet(true)));
			keys.add(new ConsoleKey("9",SternResources.ZugeingabeInventur(true)));
			keys.add(new ConsoleKey("-",SternResources.ZugeingabeKapitulieren(true)));
	
			boolean exit = false;
			
 			do
 			{ 				
 				this.spiel.console.setHeaderText(
 							this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName(),
 							this.spiel.spieler[this.spielerJetzt].getColIndex());
 				
 				ConsoleInput consoleInput = this.spiel.console.waitForKeyPressed(keys, true);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.clear();
					exit = true;
				}
				else if (input.equals("0"))
					this.raumerAnzeigen();
				else if (input.equals("9"))
					this.inventur();
				else if (input.equals("-"))
				{
					this.kapitulieren();
					exit = this.kapituliert;
				}
				else
					this.spiel.console.outUngueltigeEingabe();
 			} while (!exit);
 			
 			return false;
 		}
 		
 		private void kapitulieren()
 		{
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeKapitulieren(true),
 					this.spiel.spieler[this.spielerJetzt].getColIndex());

 			this.spiel.console.clear();
 				
			this.spiel.console.appendText(SternResources.AreYouSure(true));
			this.spiel.console.lineBreak();

			String input = this.spiel.console.waitForKeyPressedYesNo(!spiel.emailSpieler).getInputText().toUpperCase();
			
			if (!input.equals(Console.KEY_YES))
			{
				this.spiel.console.outAbbruch();
				return;
			}
			
			// Ein "Kapitulations-Flugobjekt" starten
			Flugobjekt obj = new Flugobjekt(
					0,
					0,
					null,
					null,
					ObjektTyp.KAPITULATION,
					1,
					this.spielerJetzt,
					false,
					true,
					null,
					null); 				

			this.spiel.objekte.add(obj);
	
			this.spiel.spielzuege.get(this.spielerJetzt).add(
					new Spielzug(
							0,
							obj,
							null));
			
			this.kapituliert = true;
	
			this.spiel.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.spiel.console.lineBreak();
 		}
 		
 		private void raumerAnzeigen()
 		{
 			// Einstieg: Startplanet
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeRaumerAufPlanet(true),
 					this.spiel.spieler[this.spielerJetzt].getColIndex());

 			this.spiel.console.clear();

 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();

 			int plIndex = -1;
 			Planet pl = null;

 			do
 			{
 				this.spiel.console.appendText(SternResources.ZugeingabePlanet(true)+": ");

 				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.outAbbruch();
 					plIndex = -1;
 					return;
 				}

 				plIndex = this.spiel.getPlanetenIndexFromName(input.getInputText());

 				if (plIndex < 0 || plIndex >= this.spiel.anzPl)
 				{
 					this.spiel.console.outUngueltigeEingabe();
 					continue;
 				}

 				pl = this.spiel.planeten[plIndex];

 				if (pl.getBes() != this.spielerJetzt && 
 				   !pl.hatSender(this.spielerJetzt, this.spiel.jahr))
 				{
 					console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
 					console.lineBreak();
 					continue;
 				}

 				break;
 			} while (true);

 			if (plIndex < 0)
 				return;

 			console.appendText((SternResources.ZugeingabeRaumerAnzeigen(
 					true, 
 					Integer.toString(pl.getAnz(ObjektTyp.RAUMER)))));
 			console.lineBreak();
 		}
 		
 		private void emailMenu()
 		{
 			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.ZugeingabeTitel(true)+" -> "+SternResources.ZugeingabeEMailAktionen(true), Colors.INDEX_NEUTRAL);
		
			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
			keys.add(new ConsoleKey("1",SternResources.ZugeingabeSpielstandVerschicken(true)));
			keys.add(new ConsoleKey("2",SternResources.ZugeingabeSpielzuegeImportieren(true)));
			keys.add(new ConsoleKey("ESC",SternResources.Zurueck(true)));
			
			do
			{
				ConsoleInput consoleInput = this.spiel.console.waitForKeyPressed(keys, false);
				String input = consoleInput.getInputText().toUpperCase();
				
				if (consoleInput.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					break;
				else if (input.equals("1"))
				{
					// Auto-Save
					this.spiel.autosave();
					
					this.sendSpielAnEMailSpieler();
					break;
				}
				else if (input.equals("2"))
				{
					SpielzuegeEmailTransport set = this.spiel.spielThread.importSpielzuegeAusEmail();
					
					if (set != null)
					{
						int spIndex = this.spiel.spielzuegeEinfuegen(set);
						
						if (spIndex >= 0)
						{
							this.spiel.console.setLineColor(this.spiel.spieler[spIndex].getColIndex());
							this.spiel.console.appendText(
									SternResources.ZugeingabeSpielzuegeImportiert(true, this.spiel.spieler[spIndex].getName()));
							this.spiel.console.lineBreak();
							this.spiel.console.setLineColor(Colors.INDEX_WEISS);
							
							// Auto-Save
							this.spiel.autosave();
						}
						else
						{
							this.spiel.console.appendText(SternResources.ZugeingabeSpielzuegeFalscheRunde(true));
							this.spiel.console.lineBreak();
						}
					}
					else
					{
						this.spiel.console.appendText(SternResources.ZugeingabeSpielzuegeNichtImportiert(true));
						this.spiel.console.lineBreak();
					}
				}
				else
					this.spiel.console.outUngueltigeEingabe();
					
				
			} while (true);
 		}
 		
 		private void sendSpielAnEMailSpieler()
 		{
 			int counter = 0;
 			
 			for (int spIndex = 0; spIndex < this.spiel.anzSp; spIndex++)
 			{
 				Spieler sp = this.spiel.spieler[spIndex];
 				if (!this.spiel.istSpielerEmail(spIndex))
 					continue;
 				
 				Spiel spielKopie = this.spiel.copyWithReducedInfo(spIndex, false);
 				
 				// E-Mail erzeugen
 				String subject = "[Stern] " + spielKopie.name;
 				
 				String bodyText = 
 						SternResources.ZugeingabeEMailBody2(false,
 								spielKopie.name,
 								Integer.toString(spielKopie.jahr + 1),
 								ReleaseGetter.format(ReleaseGetter.getRelease()),
 								sp.getName());
 				
 				this.spiel.spielThread.launchEmail(
 						sp.getEmailAdresse(), 
 						subject, 
 						bodyText, 
 						spielKopie);
 				
 				counter++;
 			}
 			
 			if (counter > 0)
 			{
 				this.spiel.console.appendText(
 						SternResources.ZugeingabeEMailErzeugt3(true,
 							Integer.toString(counter)));
 				this.spiel.console.lineBreak();
 				this.spiel.console.appendText(SternResources.ZugeingabeEMailErzeugt4(true));
 				this.spiel.console.lineBreak();
 			}
 		}
 		
 		private void raumer(boolean buend)
 		{
			this.spiel.console.setHeaderText(
				this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeRaumer(true),
				this.spiel.spieler[this.spielerJetzt].getColIndex());

			this.spiel.console.clear();
			
			// Startplanet
			int startPl = -1;

			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();	

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabeStartplanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					startPl = -1;
					return;
				}

				startPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (startPl >= 0)
				{
					if (!buend && this.spiel.planeten[startPl].getAnzProTypUndSpieler(ObjektTyp.RAUMER,this.spielerJetzt) > 0)
						break; // Ok. Weiter
					else if (buend && this.spiel.planeten[startPl].istBuendnis() && this.spiel.planeten[startPl].getRaumerProSpieler(this.spielerJetzt) > 0)
						break;
					else
					{
						this.spiel.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						this.spiel.console.lineBreak();
					}
				}
				else
					this.spiel.console.outUngueltigeEingabe();

			} while (true);

			if (startPl < 0)
				return;

			// Zielplanet
			int zielPl = -1;

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabeZielplanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					zielPl = -1;
					break;
				}

				zielPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (zielPl >= 0)
				{
					if (startPl != zielPl)
						break; // Ok. Weiter
					else
					{
						this.spiel.console.appendText(SternResources.ZugeingabeZielplanetIstStartplanet(true));
						this.spiel.console.lineBreak();
					}
				}
				else
					this.spiel.console.outUngueltigeEingabe();

			} while (true);

			if (zielPl < 0)
				return;

			// Anzahl
			int anzahl = -1;
			String inputText = "";

			keys = new ArrayList<ConsoleKey>();

			keys.add(new ConsoleKey("+",SternResources.ZugeingabeAlleRaumer(true)));
			keys.add(new ConsoleKey("-",SternResources.ZugeingabeInfo(true)));
			
			do
			{
				//buendnis = false;
				this.spiel.console.appendText(SternResources.ZugeingabeAnzahl(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(10, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					anzahl = -1;
					break;
				}

				inputText = input.getInputText().toUpperCase();

				if (inputText.length() == 0)
				{
					this.spiel.console.outUngueltigeEingabe();
					continue;
				}

				// Maximale Anzahl berechnen
				int anz = 0;
				int maxAnz = 0;

				if (buend)
					maxAnz = this.spiel.planeten[startPl].getAnz(ObjektTyp.RAUMER);
				else
					maxAnz = this.spiel.planeten[startPl].getAnzProTypUndSpieler(ObjektTyp.RAUMER, this.spielerJetzt);

				// Info?
				if (inputText.equals("-"))
				{
					int ankunft = this.spiel.jahr + 
							this.spiel.getDistanzMatrix()[startPl][zielPl] + 1;

					this.spiel.console.appendText(
							SternResources.ZugeingabeMaxAnzahlRaumer(true, 
									Integer.toString((maxAnz))));
					this.spiel.console.lineBreak();
					this.spiel.console.appendText(
							SternResources.ZugeingabeAnkunft(true, 
									Integer.toString(ankunft)));

					this.spiel.console.waitForTaste();
					continue;
				}

				// Anzahl
				if (inputText.equals("+"))
					anz = maxAnz;
				else
				{
					try { anz = Integer.parseInt(inputText); }
					catch (Exception e)
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}

					if (anz < 0 || anz > maxAnz)
					{
						console.appendText(SternResources.ZugeingabeNichtGenugRaumer(true));
						console.lineBreak();
						continue;
					}
				}

				if (anz > 0)
				{
					// Ok
					anzahl = anz;
					break;
				}


			} while (true); // Nochmal Anzahl eingeben

			if (anzahl < 0)
				return;

			// Raumer abziehen
			Planet plKopie = (Planet)Utils.klon(this.spiel.planeten[startPl]);

			int[] abzuege = 
					this.spiel.planeten[startPl].subtractRaumer(this.spiel.anzSp, anzahl, this.spielerJetzt, buend);

			Buendnis objBuendnis = null;

			if (buend)
				// Starte eine Buendnisflotte. Die Buendnisstruktur des Planeten wird auf die Flotte 
				// uebertragen
				objBuendnis = this.spiel.planeten[startPl].uebertrageBuendnisAufFlotte(abzuege);

			Flugobjekt obj = new Flugobjekt(
					startPl,
					zielPl,
					this.spiel.planeten[startPl].getPos(),
					this.spiel.planeten[zielPl].getPos(),
					ObjektTyp.RAUMER,
					anzahl,
					this.spielerJetzt,
					false,
					true,
					objBuendnis,
					null);

			this.spiel.objekte.add(obj);

			this.spiel.spielzuege.get(this.spielerJetzt).add(
					new Spielzug(
							startPl, 
							obj,
							plKopie));

			this.spiel.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.spiel.console.lineBreak();
 		}
 		
 		private void aufklaererPatrouillenTransporter(ObjektTyp typ)
 		{
 			String typName = "";
 			if (typ == ObjektTyp.AUFKLAERER)
 				typName = SternResources.ZugeingabeAufklaerer(true);
 			else if (typ == ObjektTyp.TRANSPORTER)
 				typName = SternResources.ZugeingabeTransporter(true);
 			else if (typ == ObjektTyp.PATROUILLE)
 				typName = SternResources.ZugeingabePatrouille(true);
 			
 			// Einstieg: Startplanet
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+typName,
 					this.spiel.spieler[this.spielerJetzt].getColIndex());

 			this.spiel.console.clear();

			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();			

			int startPl = -1;

			do
			{
				this.spiel.console.appendText(
						SternResources.ZugeingabeStartplanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					startPl = -1;
					this.spiel.console.outAbbruch();
					break;
				}

				// Gueltiger Planet?
				startPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (startPl < 0 || startPl >= this.spiel.anzPl)
				{
					this.spiel.console.outUngueltigeEingabe();
					continue;
				}

				// Hat der Spieler ein Objekt dieses Typs
				if (this.spiel.planeten[startPl].getBes() != this.spielerJetzt || this.spiel.planeten[startPl].getAnz(typ) < 1)
				{
					console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
					console.lineBreak();
					continue;
				}

				break;

			} while(true);

			if (startPl < 0)
				return;

			// Zielplanet
			int zielPl = -1;

			do
			{
				this.spiel.console.appendText(
						SternResources.ZugeingabeZielplanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				// Zielplanet: Pruefung
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					zielPl = -1;
					this.spiel.console.outAbbruch();
					break;
				}

				// Gueltiger Planet?
				zielPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (zielPl < 0 || zielPl >= this.spiel.anzPl)
				{
					zielPl = -1;
					this.spiel.console.outUngueltigeEingabe();
					continue;
				}

				// Start- und Zielplanet duerfen nicht gleich sein
				if (zielPl == startPl)
				{
					console.appendText(
							SternResources.ZugeingabeZielplanetIstStartplanet(true));
					console.lineBreak();
					continue;
				}

				break;

			} while (true);

			if (zielPl < 0)
				return;

			// Objekt Starten
			Planet plKopie = (Planet)Utils.klon(this.spiel.planeten[startPl]);

			Flugobjekt obj = null;

			if (typ == ObjektTyp.AUFKLAERER)
			{
				obj = new Flugobjekt(
						startPl,
						zielPl,
						this.spiel.planeten[startPl].getPos(),
						this.spiel.planeten[zielPl].getPos(),
						typ,
						1,
						this.spielerJetzt,
						false,
						true,
						null,
						null);

			}
			else if (typ == ObjektTyp.TRANSPORTER)
			{
				// Transporter: Energie
				keys = new ArrayList<ConsoleKey>();

				keys.add(new ConsoleKey("+",SternResources.ZugeingabeAlleEe(true)));

				if (this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN))
					keys.add(new ConsoleKey("99",SternResources.ZugeingabeKommandozentraleVerlegen(true)));

				int anz = -1;

				do
				{
					this.spiel.console.appendText(
						SternResources.ZugeingabeWievieleEe(true,
									Integer.toString(Constants.TRANSPORTER_MAX_ENEGER)) + " ");

					ConsoleInput input = this.spiel.console.waitForTextEntered(10, keys, !spiel.emailSpieler, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						anz = -1;
						this.spiel.console.outAbbruch();
						break;
					}

					if (input.getInputText().toUpperCase().equals("+"))
						anz = Math.min(this.spiel.planeten[startPl].getEvorrat(), Constants.TRANSPORTER_MAX_ENEGER);
					else
					{
						try
						{
							anz = Integer.parseInt(input.getInputText());
						}
						catch (Exception e)
						{
							this.spiel.console.outUngueltigeEingabe();
							continue;
						}

						if (anz == 99)
						{
							if (!this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN))
							{
								this.spiel.console.outUngueltigeEingabe();
								continue;
							}
							else
							{
								if (!this.spiel.planeten[startPl].istKommandozentrale())
								{
									console.appendText(SternResources.ZugeingabeKeineKommandozentrale(true));
									console.lineBreak();
									continue;
								}
							}
						}
						else if (anz < 0 || anz > Math.min(this.spiel.planeten[startPl].getEvorrat(), Constants.TRANSPORTER_MAX_ENEGER))
						{
							console.appendText(SternResources.ZugeingabeZuVielEe(true));
							console.lineBreak();
							continue;
						}
					}

					// wenn wir hierher kommen, ist alles ok
					break;

				} while (true);

				if (anz < 0)
					return;

				// Transporter starten
				Kommandozentrale kz = null;

				if (anz == 99)
				{
					kz = this.spiel.planeten[startPl].kommandozentraleVerlegen();
					anz = 0;
				}
				else
					this.spiel.planeten[startPl].subEvorrat(anz);

				obj = new Flugobjekt(
						startPl,
						zielPl,
						this.spiel.planeten[startPl].getPos(),
						this.spiel.planeten[zielPl].getPos(),
						typ,
						anz,
						this.spielerJetzt,
						false,
						true,
						null,
						kz);

			}
			else if (typ == ObjektTyp.PATROUILLE)
			{
				// Patrouille: Mission oder Transfer?
				keys = new ArrayList<ConsoleKey>();

				keys.add(new ConsoleKey("1",SternResources.ZugeingabePatrouilleMission(true)));
				keys.add(new ConsoleKey("2", SternResources.ZugeingabePatrouilleTransfer(true)));
				keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

				boolean transfer = false;
				boolean abbruch = false;

				do
				{
					this.spiel.console.appendText(SternResources.ZugeingabeMissionTransferFrage(true) + " ");

					ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.spiel.console.outAbbruch();
						abbruch = true;
						break;
					}

					if (!input.getInputText().toUpperCase().equals("1") && 
					    !input.getInputText().toUpperCase().equals("2"))
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}

					transfer = (input.getInputText().toUpperCase().equals("2"));

					break;

				} while (true);

				if (abbruch)
					return;

				obj = new Flugobjekt(
						startPl,
						zielPl,
						this.spiel.planeten[startPl].getPos(),
						this.spiel.planeten[zielPl].getPos(),
						typ,
						1,
						this.spielerJetzt,
						transfer,
						true,
						null,
						null); 				
			}

			this.spiel.planeten[startPl].decObjekt(typ, 1);
			this.spiel.objekte.add(obj);

			this.spiel.spielzuege.get(this.spielerJetzt).add(
					new Spielzug(
							startPl,
							obj,
							plKopie));

			this.spiel.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.spiel.console.lineBreak();
 		}
 		
 		private void minenUndRaeumer(ObjektTyp cat)
 		{
			if (cat == ObjektTyp.MINENRAEUMER)
				this.spiel.console.setHeaderText(
						this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeMinenraeumer(true),
						this.spiel.spieler[this.spielerJetzt].getColIndex());
			else					
				this.spiel.console.setHeaderText(
						this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeMine(true),
						this.spiel.spieler[this.spielerJetzt].getColIndex());
 				
			this.spiel.console.clear();
			
			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();

			int startPl = -1;

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabeStartplanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					startPl = -1;
					this.spiel.console.outAbbruch();
					break;
				}

				// Gueltiger Planet?
				startPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (startPl < 0 || startPl >= this.spiel.anzPl)
				{
					this.spiel.console.outUngueltigeEingabe();
					continue;
				}

				// Gehoehrt der Planet dem Spieler?
				if (this.spiel.planeten[startPl].getBes() != this.spielerJetzt)
				{
					console.appendText(SternResources.ZugeingabePlanetGehoertNicht(true));
					console.lineBreak();
					continue;
				}

				// Nur bei Minenraeumer: Hat der Spieler einen Minenraeumer?
				if (cat == ObjektTyp.MINENRAEUMER)
				{
					if (this.spiel.planeten[startPl].getAnz(ObjektTyp.MINENRAEUMER) <= 0)
					{
						console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						console.lineBreak();
						continue;
					}
				}

				break;

			} while (true);

			if (startPl < 0)
				return;

			boolean transfer = false;
			ObjektTyp typ = ObjektTyp.MINENRAEUMER;

			if (cat != ObjektTyp.MINENRAEUMER)
			{
				// Nur fuer Minen: Typ
				keys = new ArrayList<ConsoleKey>();

				keys.add(new ConsoleKey("1",SternResources.ZugeingabeMine50(true)));
				keys.add(new ConsoleKey("2",SternResources.ZugeingabeMine100(true)));
				keys.add(new ConsoleKey("3",SternResources.ZugeingabeMine250(true)));
				keys.add(new ConsoleKey("4",SternResources.ZugeingabeMine500(true)));

				boolean abbruch = false;

				do
				{
					this.spiel.console.appendText(SternResources.ZugeingabeMineTypFrage(true) + " ");

					ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.spiel.console.outAbbruch();
						abbruch = true;
						break;
					}

					if (input.getInputText().equals("1"))
					{
						typ = ObjektTyp.MINE50;
					}
					else if (input.getInputText().equals("2"))
					{
						typ = ObjektTyp.MINE100;
					}
					else if (input.getInputText().equals("3"))
					{
						typ = ObjektTyp.MINE250;
					}
					else if (input.getInputText().equals("4"))
					{
						typ = ObjektTyp.MINE500;
					}
					else
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}

					if (this.spiel.planeten[startPl].getAnz(typ) <= 0)
					{
						console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						console.lineBreak();
						continue;
					}

					break;

				} while (true);

				if (abbruch)
					return;
			}
			else
			{
				// Nur fuer Minenraeumer: Mission oder Transfer?
				// Patrouille: Mission oder Transfer?
				keys = new ArrayList<ConsoleKey>();

				keys.add(new ConsoleKey("1",SternResources.ZugeingabePatrouilleMission(true)));
				keys.add(new ConsoleKey("2", SternResources.ZugeingabePatrouilleTransfer(true)));
				keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));

				boolean abbruch = false;

				do
				{
					this.spiel.console.appendText(SternResources.ZugeingabeMissionTransferFrage(true) + " ");

					ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, true);

					if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
					{
						this.spiel.console.outAbbruch();
						abbruch = true;
						break;
					}

					if (!input.getInputText().toUpperCase().equals("1") && 
					    !input.getInputText().toUpperCase().equals("2"))
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}

					transfer = (input.getInputText().toUpperCase().equals("2"));

					break;

				} while (true);

				if (abbruch)
					return;
			}

			// Typ steht fest. Ziel festlegen
			Point ptZiel = null;

			keys = new ArrayList<ConsoleKey>();						

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabeMineZielsektor(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					ptZiel = null;
					break;
				}

				// Gueltiger Sektor?
				ptZiel = this.spiel.getPointFromFeldName(input.getInputText());

				if (ptZiel == null)
				{
					// Vielleicht war es ein Planet?
					int plZielIndex = this.spiel.getPlanetenIndexFromName(input.getInputText());
					
					if (plZielIndex == -1)
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}
					
					ptZiel = this.spiel.planeten[plZielIndex].getPos();
				}

				// Zielsektor == Startplanet?
				if (ptZiel.equals(this.spiel.planeten[startPl].getPos()))
				{
					console.appendText(SternResources.ZugeingabeZielplanetIstStartplanet(true));
					console.lineBreak();
					continue;
				}
				
				// Nur fuer Minenraeumer: Transfert darf nur auf einen Planeten gehen
				if (typ == ObjektTyp.MINENRAEUMER && transfer)
				{
					int plZielIndex = this.spiel.getPlanetenIndexFromName(input.getInputText());
					
					if (plZielIndex == -1)
					{
						console.appendText(SternResources.ZugeingabeZielTransfer(true));
						console.lineBreak();
						continue;
					}
				}

				break;
			} while (true);

			if (ptZiel == null)
				return;

			Planet plKopie = (Planet)Utils.klon(this.spiel.planeten[startPl]);

			int zielPl = this.spiel.getPlanetIndexFromPoint(ptZiel);

			if (typ != ObjektTyp.MINENRAEUMER)
				transfer = (zielPl != Constants.KEIN_PLANET);

			this.spiel.planeten[startPl].decObjekt(typ, 1);

			Flugobjekt obj = new Flugobjekt(
					startPl,
					zielPl,
					this.spiel.planeten[startPl].getPos(),
					ptZiel,
					typ,
					1,
					this.spielerJetzt,
					transfer,
					true,
					null,
					null);

			this.spiel.objekte.add(obj);

			this.spiel.spielzuege.get(this.spielerJetzt).add(
					new Spielzug(
							startPl,
							obj,
							plKopie));

			this.spiel.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			this.spiel.console.lineBreak();
 		}
 		
 		private void buendnis()
 		{
 			// Einstieg: Startplanet
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeBuendnis(true),
 					this.spiel.spieler[this.spielerJetzt].getColIndex());

 			this.spiel.console.clear();

 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();

 			int plIndex = -1;
 			Planet pl = null;

 			do
 			{
 				this.spiel.console.appendText(SternResources.ZugeingabePlanet(true)+": ");

 				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.outAbbruch();
 					plIndex = -1;
 					return;
 				}

 				plIndex = this.spiel.getPlanetenIndexFromName(input.getInputText());

 				if (plIndex < 0 || plIndex >= this.spiel.anzPl)
 				{
 					this.spiel.console.outUngueltigeEingabe();
 					continue;
 				}

 				pl = this.spiel.planeten[plIndex];

 				if (pl.getBes() != this.spielerJetzt && 
 				   !pl.istBuendnisMitglied(this.spielerJetzt) &&
 				   !pl.hatSender(this.spielerJetzt, this.spiel.jahr))
 				{
 					console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
 					console.lineBreak();
 					continue;
 				}

 				break;
 			} while (true);

 			if (plIndex < 0)
 				return;
 			
 			if (pl.getBes() != this.spielerJetzt && 
 	 				    !pl.istBuendnisMitglied(this.spielerJetzt))
			{
				this.buendnisAnzeigen(pl, plIndex);
				this.spiel.console.waitForTaste();
				this.spiel.console.clear();
				return;
			}

 			keys = new ArrayList<ConsoleKey>();

 			keys.add(new ConsoleKey("0",SternResources.ZugeingabeKuendigen(true)));

 			for (int sp = 0; sp < this.spiel.anzSp; sp++)
 				if (sp != this.spielerJetzt)
 					keys.add(new ConsoleKey(
 							Integer.toString(sp+1), 
 							this.spiel.spieler[sp].getName()));

 			keys.add(new ConsoleKey("-", SternResources.ZugeingabeInfo(true)));

 			do
 			{
 				this.spiel.console.appendText(
 						SternResources.ZugeingabeNeueBuendnisstruktur(true)+": ");

 				ConsoleInput input = this.spiel.console.waitForTextEntered(10, keys, !spiel.emailSpieler, true);

 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.outAbbruch();
 					return;
 				}

 				// Buendnisinfo
 				if (input.getInputText().toUpperCase().equals("-"))
 				{
 					this.buendnisAnzeigen(pl, plIndex);
 					this.spiel.console.waitForTaste();
 					continue;
 				}
 				
 				// Eingabe darf nur aus Ziffern bestehen
 				try
 				{
 					Integer.parseInt(input.getInputText());
 				}
 				catch (Exception e)
 				{
 					this.spiel.console.outUngueltigeEingabe();
 					continue;
 				}

 				// Entweder kuendigen, oder neue Struktur festlegen
 				if (input.getInputText().indexOf('0') >= 0 && input.getInputText().length() > 1)
 				{
 					console.appendText(
 							SternResources.ZugeingabeBuendnis0NichtKombinieren(true));
 					console.lineBreak();
 					continue;
 				}

 				// Aenderungen anwenden
 				Planet plKopie = (Planet)Utils.klon(pl);

 				if (input.getInputText().equals("0"))
 				{
 					// Kuendigen
 					if (!pl.istBuendnisMitglied(this.spielerJetzt))
 					{
 						console.appendText(
 								SternResources.ZugeingabeKeinBuendnismitglied(true));
 						console.lineBreak();
 						break;
 					}
 					if (pl.getBes() != this.spielerJetzt && pl.getRaumerProSpieler(this.spielerJetzt) > 0)
 					{
 						console.appendText(
 								SternResources.ZugeingabeZuerstKuendigen(true));
 						console.lineBreak();
 						break;
 					}
 					
 					pl.buendnisKuendigen(this.spielerJetzt);
 				}
 				else
 				{
 					if (this.spielerJetzt != pl.getBes() && !pl.istBuendnisMitglied(this.spielerJetzt))
 					{
 						console.appendText(
 								SternResources.ZugeingabeKeinBuendnismitglied(true));
 						console.lineBreak();
 						break;
 					}
 					
 					if (this.spielerJetzt != pl.getBes())
 					{
 						console.appendText(
 								SternResources.ZugeingabeAufFremdenPlanetenNurKuendigen(true));
 						console.lineBreak();
 						continue;
 					}

 					// Eingabestring analysieren
 					BitSet bitSet = new BitSet(this.spiel.anzSp);
 					boolean error = false;

 					for (int i = 0; i < input.getInputText().length(); i++)
 					{
 						int sp = Integer.parseInt(input.getInputText().substring(i,i+1)) - 1;

 						if (sp < 0 || sp >= this.spiel.anzSp || sp == this.spielerJetzt)
 						{
 							error = true;
 							break;
 						}

 						bitSet.set(sp);
 					}

 					if (error)
 					{
 						this.spiel.console.outUngueltigeEingabe();
 						continue;
 					}

 					for (int sp = 0; sp < this.spiel.anzSp; sp++)
 					{
 						if (bitSet.get(sp))
 							pl.buendnisSpielerHinzufuegen(this.spiel.anzSp, sp);
 						else
 							pl.buendnisKuendigen(sp);
 					}
 				}
 				
 				this.spiel.spielzuege.get(this.spielerJetzt).add(
 						new Spielzug(plIndex, plKopie, pl.buendnisGetMitglieder()));

 				this.spiel.console.appendText(
 						SternResources.ZugeingabeStartErfolgreich(true));
 				this.spiel.console.lineBreak();

 				break;
 			} while (true);
 		}
 		
 		private void buendnisAnzeigen(Planet pl, int plIndex)
 		{
 			if (pl.istBuendnis())
				{
					this.spiel.console.appendText(
							SternResources.ZugeingabeMomentaneBuendnisstruktur(true)+":");
					this.spiel.console.lineBreak();

					this.spiel.outBuendnisStruktur(plIndex);
				}
				else
				{
					this.spiel.console.appendText(
							SternResources.ZugeingabeKeinBuendnis(true));
				}
 		}
 		
 		private void inventur()
 		{
 			new Inventur(this.spiel, this.spielerJetzt);
 		}
 		
 		private boolean gestoppteObjekte()
 		{
 			boolean zugeingabeVerlassen = false;
 			
 			do
 			{
 				Flugobjekt obj = null;
 				
 				for (Flugobjekt obj2: this.spiel.objekte)
 				{
 					if (obj2.getBes() == this.spielerJetzt && obj2.isStop())
 					{
 						obj = obj2;
 						break;
 					}
 				}
 				
 				if (obj == null)
 					break;
 				
 				// Position markieren
 				ArrayList<Point2D.Double> markedFields = new ArrayList<Point2D.Double>();
 				markedFields.add(Utils.toPoint2D(obj.getStart()));
 				this.spiel.updateSpielfeldDisplay(markedFields);
 				
 				// Console-Keys
 				ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 				
 				keys.add(new ConsoleKey("-", SternResources.ZugeingabeUndo(true)));
 				
 				String objName = "";
 				
 				switch (obj.getTyp())
 				{
 				case RAUMER:
 					objName = SternResources.ZugeingabeWohinRaumer(true, Integer.toString(obj.getAnz()));
 					break;
 				case AUFKLAERER:
 					objName = SternResources.ZugeingabeWohinAufklaerer(true);
 					break;
 				case TRANSPORTER:
 					objName = SternResources.ZugeingabeWohinTransporter(true);
 					break;
 				case PATROUILLE:
 					objName = SternResources.ZugeingabeWohinPatrouille(true);
 					break;
 				case MINE50:
 					objName = SternResources.ZugeingabeWohin50erMine(true);
 					break;
 				case MINE100:
 					objName = SternResources.ZugeingabeWohin100erMine(true);
 					break;
 				case MINE250:
 					objName = SternResources.ZugeingabeWohin250erMine(true);
 					break;
 				case MINE500:
 					objName = SternResources.ZugeingabeWohin500erMine(true);
 					break;
 				case MINENRAEUMER:
 					objName = SternResources.ZugeingabeWohinMinenraumer(true);
 					break;
				default:
					break;
 				}
 				
 				do
 				{
 					this.spiel.console.appendText(objName + " ");
	 				
	 				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, false);
	 				
	 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
	 				{
	 					this.spiel.console.outUngueltigeEingabe();
	 					continue;
	 				}
	 				
	 				if (input.getInputText().toUpperCase().equals(("-")))
					{
	 					zugeingabeVerlassen = this.undo();
	 					break;
					}
					
	 				int zielPl = this.spiel.getPlanetenIndexFromName(input.getInputText());
	
					if (zielPl >= 0)
					{
						this.spiel.spielzuege.get(this.spielerJetzt).add(
								new Spielzug(obj, (UUID)Utils.klon(obj.getStopLabel()), zielPl));
						
						obj.setZpl(zielPl);
						obj.setZiel(this.spiel.planeten[zielPl].getPos());
						obj.setStop(false);
						
						break;
					}
					else
						this.spiel.console.outUngueltigeEingabe();
				
 				} while (true);
 				
 				// Position wieder loeschen
 				this.spiel.updateSpielfeldDisplay();
 				
 				if (zugeingabeVerlassen)
 					break;
 				
 			} while (true);
 			
 			return zugeingabeVerlassen;
 		}
 		
 		private boolean undo()
 		{
 			ArrayList<Spielzug> zuege = this.spiel.spielzuege.get(this.spielerJetzt);
 			
			if (zuege.size() == 0)
			{
				if (this.spiel.emailSpieler)
				{
					this.spiel.console.appendText(SternResources.ZugeingabeKeineSpielzuege(true));
					this.spiel.console.lineBreak();
					return false;
				}
				else
				{
					this.spiel.console.appendText(SternResources.ZugeingabeKeineSpielzuegeAbbrechen(true) + " ");
					
					String input = this.spiel.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
					if (input.equals(Console.KEY_YES))
					{
						this.spiel.console.lineBreak();
						return true;
					}
					else
					{
						return false;
					}
				}
			}
			
			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> " + SternResources.ZugeingabeUndo(true),
					this.spiel.spieler[this.spielerJetzt].getColIndex());
			
			this.spiel.console.appendText(SternResources.ZugeingabeUndoFrage(true)+" ");
			
			String input = this.spiel.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (input.equals(Console.KEY_YES))
			{
				Spielzug zug = zuege.get(zuege.size()-1);
				
				if (zug.getStopLabel() != null)
				{
					// Gestopptes Objekt
					zug.getObj().setStopLabel(zug.getStopLabel());
					zug.getObj().setZiel(zug.getObj().getStart());
					zug.getObj().setZpl(zug.getObj().getSpl());
				}
				else
				{
					if (zug.getPlanetVorher() != null)
						this.spiel.planeten[zug.getPlIndex()] = zug.getPlanetVorher();
					
					if (zug.getObj() != null)
					{
						if (zug.getObj().getTyp() == ObjektTyp.KAPITULATION)
							this.kapituliert = false;
						else
							this.spiel.objekte.remove(zug.getObj());
					}
				}
				
				zuege.remove(zuege.size()-1);
				
				this.spiel.console.appendText(SternResources.ZugeingabeUndoErfolg(true));
				this.spiel.console.lineBreak();
			}
			else
				this.spiel.console.outAbbruch();
			
			return false;
 		}
 		
 		private void planeteneditor()
 		{
			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> " + SternResources.ZugeingabePlanet(true),
					this.spiel.spieler[this.spielerJetzt].getColIndex());

			this.spiel.console.clear();
			
			// Startplanet
			int startPl = -1;

			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();	

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabePlanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					startPl = -1;
					break;
				}

				startPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (startPl >= 0)
				{
					if (this.spiel.planeten[startPl].getBes() == this.spielerJetzt ||
						this.spiel.planeten[startPl].hatSender(this.spielerJetzt, this.spiel.jahr))
						break; // Weiter
					else
					{
						this.spiel.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						this.spiel.console.lineBreak();
					}
				}
				else
					this.spiel.console.outUngueltigeEingabe();

			} while (true);

			if (startPl < 0)
				return;

			new Planeteneditor(
					this.spiel,
					startPl,
					this.spiel.spielzuege.get(this.spielerJetzt),
					this.spiel.planeten[startPl].getBes() != this.spielerJetzt,
					false);
 		}
 		 		
 		private void planeteninfo()
 		{
			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true) +" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+ SternResources.ZugeingabePlaneteninfo(true),
					this.spiel.spieler[this.spielerJetzt].getColIndex());

			this.spiel.console.clear();
			
			// Nur im Simpel-Modus
			int startPl = -1;

			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();	

			do
			{
				this.spiel.console.appendText(SternResources.ZugeingabePlanet(true)+": ");

				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, !spiel.emailSpieler, true);

				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					startPl = -1;
					break;
				}

				startPl = this.spiel.getPlanetenIndexFromName(input.getInputText());

				if (startPl >= 0)
				{
					if (this.spiel.planeten[startPl].getBes() == this.spielerJetzt)
						break; // Weiter
					else
					{
						this.spiel.console.appendText(SternResources.ZugeingabeAktionNichtMoeglich(true));
						this.spiel.console.lineBreak();
					}
				}
				else
					this.spiel.console.outUngueltigeEingabe();

			} while (true);

			if (startPl < 0)
				return;

			spiel.console.appendText(
					SternResources.InventurRaumerproduktionJahr(true)+": " + this.spiel.planeten[startPl].getEraum() + " " + SternResources.PlEditEe(true));
			
			spiel.console.waitForTaste();
			spiel.console.clear();
 		}

 		
 		private boolean ende()
 		{
			this.spiel.console.setHeaderText(
			this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Zugeingabe(true)+" " + this.spiel.spieler[this.spielerJetzt].getName() + " -> "+SternResources.ZugeingabeBeenden(true),
			this.spiel.spieler[this.spielerJetzt].getColIndex());
			
			this.spiel.console.appendText(SternResources.ZugeingabeBeendenFrage(true));
			this.spiel.console.lineBreak();
			
			String input2 = this.spiel.console.waitForKeyPressedYesNo(false).getInputText().toUpperCase();
			
			if (!input2.equals(Console.KEY_YES))
			{
				this.spiel.console.outAbbruch();
				return false;
			}
			
			return true;
 		}
 	}
 	
 	// =============================
 	protected class Planeteneditor
 	{
 		private Hashtable<ObjektTyp,Integer> preiseKauf;
 		private Hashtable<ObjektTyp,Integer> preiseVerkauf;
 		private ArrayList<ObjektTyp> editorReihenfolge;
 		private Spiel spiel;
 		private boolean readOnly;
 		private boolean reduzierteAnzeige;
 		
 		public Planeteneditor(
 				Spiel spiel,
 				int plIndex,
 				ArrayList<Spielzug> spielzuege,
 		 		boolean readOnly,
 		 		boolean reduzierteAnzeige)
 		{
 			this.readOnly = readOnly;
 			this.reduzierteAnzeige = reduzierteAnzeige;
 			
 			this.preiseKauf = spiel.planeten[plIndex].getBes() == Constants.BESITZER_NEUTRAL ?
 								spiel.preiseKaufArray.get(spiel.anzSp) :
 								spiel.preiseKaufArray.get(spiel.planeten[plIndex].getBes());
 								
			this.preiseVerkauf = spiel.planeten[plIndex].getBes() == Constants.BESITZER_NEUTRAL ?
									spiel.preiseVerkaufArray.get(spiel.anzSp) :
									spiel.preiseVerkaufArray.get(spiel.planeten[plIndex].getBes());
 								
 			this.spiel = spiel;
 			
 			this.editorReihenfolge = new ArrayList<ObjektTyp>();
 			
 			this.editorReihenfolge.add(ObjektTyp.EPROD);
 			this.editorReihenfolge.add(ObjektTyp.ERAUM);
 			this.editorReihenfolge.add(ObjektTyp.FESTUNG);
 			this.editorReihenfolge.add(ObjektTyp.FESTUNG_REPARATUR);
			
 			this.editorReihenfolge.add(ObjektTyp.AUFKLAERER);
 			this.editorReihenfolge.add(ObjektTyp.TRANSPORTER);
 			this.editorReihenfolge.add(ObjektTyp.PATROUILLE);
 			this.editorReihenfolge.add(ObjektTyp.MINENRAEUMER);
			
 			this.editorReihenfolge.add(ObjektTyp.MINE50);
 			this.editorReihenfolge.add(ObjektTyp.MINE100);
 			this.editorReihenfolge.add(ObjektTyp.MINE250);
 			this.editorReihenfolge.add(ObjektTyp.MINE500);

			int zeile = 1;
			
			Planet planet = (Planet)Utils.klon(spiel.planeten[plIndex]);
			
			spiel.console.clear();
			spiel.console.setModus(Console.ConsoleModus.PLANETEN_EDITOR);
			this.updateDisplay(planet, zeile, false);
			spiel.switchDisplayMode(ScreenDisplayContent.MODUS_PLANETENEDITOR);
			
			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
			
			if (this.reduzierteAnzeige)
				keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
			else
			{
				keys.add(new ConsoleKey(SternResources.PlaneteneditorAuf(true),SternResources.PlaneteneditorAuswahlAendern(true)));
				keys.add(new ConsoleKey(SternResources.PlaneteneditorAb(true),SternResources.PlaneteneditorAuswahlAendern(true)));
				keys.add(new ConsoleKey(SternResources.Rechts(true),SternResources.PlaneteneditorKaufen(true)));
				keys.add(new ConsoleKey(SternResources.Links(true),SternResources.PlaneteneditorVerkaufen(true)));
				keys.add(new ConsoleKey("ESC",SternResources.Abbrechen(true)));
				keys.add(new ConsoleKey("ENTER",SternResources.PlaneteneditorUebernehmen(true)));
			}
			
			boolean uebernehmen = false;
			
			do
			{
				ConsoleInput input = spiel.console.waitForKeyPressed(keys, false);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					uebernehmen = false;
					break;
				}
				
				if (this.reduzierteAnzeige)
					continue;
				
				if (input.getLastKeyCode() == KeyEvent.VK_ENTER)
				{
					uebernehmen = !this.readOnly ;
					break;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_DOWN ||
					input.getInputText().equals("2"))
				{
					zeile++;
					if (zeile >= editorReihenfolge.size())
						zeile = 0;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_UP  ||
						input.getInputText().equals("8"))
				{
					zeile--;
					if (zeile < 0)
						zeile = editorReihenfolge.size() - 1;
				}
				
				if (input.getLastKeyCode() == KeyEvent.VK_RIGHT  ||
						input.getInputText().equals("6"))
					this.kaufVerkauf(true, plIndex, planet, editorReihenfolge.get(zeile)); //  transactionData);
				
				if (input.getLastKeyCode() == KeyEvent.VK_LEFT  ||
						input.getInputText().equals("4"))
					this.kaufVerkauf(false, plIndex, planet, editorReihenfolge.get(zeile)); //  transactionData);
	
				this.updateDisplay(planet, zeile, false);
				
			} while (true);
			
			spiel.console.clear();
			spiel.console.setModus(Console.ConsoleModus.TEXT_INPUT);
			spiel.switchDisplayMode(ScreenDisplayContent.MODUS_SPIELFELD);
			
			if (uebernehmen)
			{
				spielzuege.add(new Spielzug(plIndex, spiel.planeten[plIndex], planet));
				spiel.planeten[plIndex] = planet;

				spiel.console.appendText(SternResources.ZugeingabeStartErfolgreich(true));
			}
			else
				spiel.console.appendText(SternResources.AktionAbgebrochen(true));
			
			spiel.console.lineBreak();
 		}
 		
 		private void kaufVerkauf(
 				boolean kauf,
 				int plIndex,
 				Planet pl,
 				ObjektTyp typ)

 		{
 			if (typ == ObjektTyp.EPROD)
 			{
 				if (kauf)
 					// Kaufen
 					pl.kaufeEprod(this.preiseKauf.get(ObjektTyp.EPROD));
 			}
 			else if (typ == ObjektTyp.ERAUM)
 			{
 				if (kauf)
 					pl.incEraum();
 				else
 					pl.decEraum();
 			}
 			else if (typ == ObjektTyp.FESTUNG)
 			{
 				if (!spiel.optionen.contains(SpielOptionen.FESTUNGEN))
 					return;
 				
 				if (kauf)
 					pl.kaufeFestung(this.preiseKauf.get(typ));				
 				else
 					pl.verkaufeFestung(Utils.round((double)(this.preiseVerkauf.get(typ) * pl.getFestungIntakt()) / 100.));
 			}
 			else if (typ == ObjektTyp.FESTUNG_REPARATUR)
 			{
 				if (kauf && pl.getFestungFaktor() > 0 && pl.getFestungIntakt() < 100)
 					pl.repariereFestung(Constants.PREIS_MIN_FESTUNG_REPARATUR * pl.getFestungFaktor());
 			}
 			else
 			{
 				// Flugobjekte
 				if (kauf)
 					pl.kaufeObjekt(typ, 1, this.preiseKauf.get(typ));
 				else
 					pl.verkaufeObjekt(typ, this.preiseVerkauf.get(typ));
 			}
 		}
 		
 		private void updateDisplay (Planet pl, int zeile, boolean readOnly)
 		{
 			Hashtable<ObjektTyp,String> preisspanneKauf = new Hashtable<ObjektTyp,String>();
 			Hashtable<ObjektTyp,String> preisspanneVerkauf = new Hashtable<ObjektTyp,String>();
 			Hashtable<ObjektTyp,String> anzahl = new Hashtable<ObjektTyp,String>();
 			HashSet<ObjektTyp> kaufNichtMoeglich = new HashSet<ObjektTyp>();
 			HashSet<ObjektTyp> verkaufNichtMoeglich = new HashSet<ObjektTyp>();
 			
 			@SuppressWarnings("unchecked")
 			Hashtable<ObjektTyp,Integer> preiseKauf = (Hashtable<ObjektTyp,Integer>)Utils.klon(this.preiseKauf);
 			@SuppressWarnings("unchecked")
 			Hashtable<ObjektTyp,Integer> preiseVerkauf = (Hashtable<ObjektTyp,Integer>)Utils.klon(this.preiseVerkauf);
 			
 			for (ObjektTyp typ: this.editorReihenfolge)
 			{
 				// Verkaufspreis fuer angeschlagene Festungen
 				if (typ == ObjektTyp.FESTUNG && pl.getFestungFaktor() > 0 && pl.getFestungIntakt() < 100)
 					preiseVerkauf.put(typ, Utils.round((double)preiseVerkauf.get(typ) * (double)pl.getFestungIntakt() / 100.));
 				
 				// Preis fuer Doppelfestungen
 				if (typ == ObjektTyp.FESTUNG_REPARATUR && pl.getFestungFaktor() > 1)
 					preiseKauf.put(typ, pl.getFestungFaktor() * Constants.PREIS_MIN_FESTUNG_REPARATUR);
 				
 				// Preisspannen
 				StringBuilder sb = new StringBuilder();
 				sb.append("(");
 				sb.append(Utils.padString(Spiel.preiseKaufUntergrenze.get(typ),2));
 				sb.append("-");
 				sb.append(Utils.padString(Spiel.preiseKaufObergrenze.get(typ),2));
 				sb.append(")");
 				
 				preisspanneKauf.put(typ, sb.toString());
 				
 				if (typ != ObjektTyp.EPROD && typ != ObjektTyp.FESTUNG_REPARATUR)
 				{
 					sb = new StringBuilder();
 					sb.append("(");
 					sb.append(Utils.padString(Utils.round((double)Spiel.preiseKaufUntergrenze.get(typ)*Constants.PREIS_KAUF_VERKAUF_RATIO),2));
 					sb.append("-");
 					sb.append(Utils.padString(Utils.round((double)Spiel.preiseKaufObergrenze.get(typ)*Constants.PREIS_KAUF_VERKAUF_RATIO),2));
 					sb.append(")");
 					
 					preisspanneVerkauf.put(typ, sb.toString());
 				}
 				
 				// Angezeigte Menge
 				int evorrat = pl.getEvorrat();
 				
 				String anzString = "";
 				int anz = 0;
 				
 				if (typ == ObjektTyp.EPROD)
 				{
 					anz = pl.getEprod();
 					anzString = Integer.toString(anz);
 				}
 				else if (typ == ObjektTyp.ERAUM)
 				{
 					anz = pl.getEraum();
 					anzString = Integer.toString(anz);
 				}
 				else if (typ == ObjektTyp.FESTUNG)
 				{
 					anz = pl.getFestungFaktor();
 					anzString = Utils.numToString(anz);
 				}
 				else if (typ == ObjektTyp.FESTUNG_REPARATUR)
 				{
 					anz = pl.getFestungIntakt();
 					anzString = Utils.numToString(anz);
 				}
 				else
 				{
 					anz = pl.getAnz(typ);
 					anzString = Utils.numToString(anz);
 				}
 				
 				anzahl.put(typ, anzString);
 				
 				// Was kann man kaufen, was nicht?
 				if (typ == ObjektTyp.FESTUNG_REPARATUR)
 				{
 					if (pl.getFestungFaktor() == 0 || (pl.getFestungFaktor() > 0 && pl.getFestungIntakt() >= 100))
 						kaufNichtMoeglich.add(typ);
 				}
 				else if (typ == ObjektTyp.EPROD && pl.getEprod() >= Constants.EPROD_MAX)
 					kaufNichtMoeglich.add(typ);
 				else if (typ == ObjektTyp.FESTUNG && (!spiel.optionen.contains(SpielOptionen.FESTUNGEN) || anz >= Constants.MAX_ANZ_FESTUNGEN))
 					kaufNichtMoeglich.add(typ);
 				else if (this.preiseKauf.get(typ) > evorrat)
 					kaufNichtMoeglich.add(typ);
 				
 				// Was kann man verkaufen, was nicht?
 				if (anz < 1)
 					verkaufNichtMoeglich.add(typ);
 			}
 			
 			if (this.spiel.screenDisplayContent == null)
 				this.spiel.screenDisplayContent = new ScreenDisplayContent();
 			
 			byte col = // pl.isNeutral() ?
 						Colors.INDEX_WEISS; // :
 						//this.spiel.spieler[pl.getBes()].getColIndex();
 			
 			this.spiel.screenDisplayContent.setPlEdit(new
 				PlanetenEditorDisplayContent(
 						this.editorReihenfolge.get(zeile),
 						preiseKauf,
 						preiseVerkauf,
 						preisspanneKauf,
 						preisspanneVerkauf,
 						anzahl,
 						kaufNichtMoeglich,
 						verkaufNichtMoeglich,
 						col,
 						pl.getEvorrat(),
 						pl.istKommandozentrale(),
 						this.readOnly,
 						this.reduzierteAnzeige));
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);
 		}
 		
 	}
 	
 // =============================
 	protected class Statistik
 	{ 		
 		private Spiel spiel;
 		
 		private int markiertesJahrIndex;
 		private char modus;
	 		
 		public Statistik(
 				Spiel spiel)
 		{
 			this.spiel = spiel;
 			
 			this.markiertesJahrIndex = spiel.jahr;
 			
 			this.modus = Constants.STATISTIK_MODUS_PUNKTE; // Punkte
			
			spiel.console.clear();
			spiel.console.setModus(Console.ConsoleModus.STATISTIK);
			this.updateDisplay();
			spiel.switchDisplayMode(ScreenDisplayContent.MODUS_STATISTIK);
			
			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
			
			keys.add(new ConsoleKey(SternResources.Links(true),SternResources.StatistikJahrMinus(true)));
			keys.add(new ConsoleKey(SternResources.Rechts(true),SternResources.StatistikJahrPlus(true)));
			keys.add(new ConsoleKey(Character.toString(Constants.STATISTIK_MODUS_PUNKTE),SternResources.Punkte(true)));
			keys.add(new ConsoleKey(Character.toString(Constants.STATISTIK_MODUS_RAUMER),SternResources.Raumer(true)));
			keys.add(new ConsoleKey(Character.toString(Constants.STATISTIK_MODUS_PLANETEN),SternResources.Planeten(true)));
			keys.add(new ConsoleKey(Character.toString(Constants.STATISTIK_MODUS_PRODUKTION),SternResources.Energieproduktion(true)));

			keys.add(new ConsoleKey("ESC",SternResources.StatistikSchliessen(true)));
			
			do
			{
				ConsoleInput input = spiel.console.waitForKeyPressed(keys, false);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					break;
				}
				
				else if (input.getLastKeyCode() == KeyEvent.VK_LEFT && this.markiertesJahrIndex > 0)
				{
					this.markiertesJahrIndex--;
				}
				
				else if (input.getLastKeyCode() == KeyEvent.VK_RIGHT && this.markiertesJahrIndex < this.spiel.jahr)
				{
					this.markiertesJahrIndex++;
				}
				else
				{
					char c = ' ';
					
					try
					{
						c = input.getInputText().toUpperCase().charAt(0);
					}
					catch (Exception x)
					{}
					
					if (c == Constants.STATISTIK_MODUS_PUNKTE ||
					    c == Constants.STATISTIK_MODUS_RAUMER ||
					    c == Constants.STATISTIK_MODUS_PLANETEN ||
					    c == Constants.STATISTIK_MODUS_PRODUKTION)

						this.modus = c;
				}
				
				this.updateDisplay();
				
			} while (true);
			
			spiel.console.clear();
			spiel.console.setModus(Console.ConsoleModus.TEXT_INPUT);
			spiel.switchDisplayMode(ScreenDisplayContent.MODUS_SPIELFELD);
			
			spiel.console.lineBreak();
 		}
 		
 		private void updateDisplay()
 		{
 			// Wieviele Jahre werden angezeigt?
 			int[][] werte = new int[this.spiel.jahr + 1][this.spiel.anzSp];
 			int[][] championsJahr = new int[this.spiel.jahr + 1][];
 			
 			// Hervorgehobenes Jahr (als Index) 
 			int maxWert = 0;
 			int maxWertJahr = 0;
 			int maxWertSpieler = 0;
 			int minWert = 0;
 			int minWertJahr = 0;
 			int minWertSpieler = 0;
 			
 			// Werte fuellen, Min und Max ermitteln
 			boolean start = true;
 			
 			ArrayList<Integer> jahre = new ArrayList<Integer>(this.spiel.archiv.keySet());
 			Collections.sort(jahre);
 			
 			int counter = 0;
 			for (int jahrIndex = 0; jahrIndex < jahre.size(); jahrIndex++)
 			{
 				int jahr = jahre.get(jahrIndex);
 				
 				Archiv a = this.spiel.archiv.get(jahr);
 				int jahresbestwert = 0;
 				
 				for (int spielerIndex = 0; spielerIndex < this.spiel.anzSp; spielerIndex++)
 				{
 					int wert = 0;
 					
	 				switch (this.modus)
	 				{
	 				case Constants.STATISTIK_MODUS_PUNKTE: // Punkte
	 					wert = a.getPunkte()[spielerIndex];
	 					break;
	 					
	 				case Constants.STATISTIK_MODUS_RAUMER: // Raumer
	 					wert = a.getRaumer()[spielerIndex];
	 					break;
	 					
	 				case Constants.STATISTIK_MODUS_PLANETEN: // Planeten
	 					wert = a.getAnzPl()[spielerIndex];
	 					break;
	 					
	 				case Constants.STATISTIK_MODUS_PRODUKTION: // Produktion
	 					wert = a.getEprod()[spielerIndex];
	 					break;
	 				}
	 				
	 				if (wert <= minWert || start)
	 				{
	 					minWert = wert;
	 					minWertSpieler = spielerIndex;
	 					minWertJahr = jahr;
	 				}
	 				if (wert >= maxWert || start)
	 				{
	 					maxWert = wert;
	 					maxWertSpieler = spielerIndex;
	 					maxWertJahr = jahr;
	 				}
	 				
	 				start = false;
	 				
 					werte[counter][spielerIndex] = wert;
 					
 					if (wert > jahresbestwert)
 					{
 						jahresbestwert = wert;
 					}
 				}
 				
 				// Bestwerte ermitteln
 				int[] champs = Utils.listeSortieren(werte[counter], true);
 				int anzChamps = 1;
 				
 				for (int t = 1; t < champs.length; t++)
 				{
 					if (werte[counter][champs[t]] < werte[counter][champs[0]])
 						break;
 					
 					anzChamps++;
 				}
 				
 				championsJahr[jahr] = new int[anzChamps];
 				
 				int c = 0;
 				for (int t = 0; t < anzSp; t++)
 				{
 					if (werte[counter][champs[t]] == werte[counter][champs[0]])
 					{
 						championsJahr[jahr][c] = champs[t];
 						c++;
 					}
 				}
 				
 				counter++;
 			} 			
 			
 			String titel = Character.toString(this.modus);
 			
 			if (this.spiel.screenDisplayContent == null)
 				this.spiel.screenDisplayContent = new ScreenDisplayContent();
 			
 			this.spiel.screenDisplayContent.setStatistik(
 				new StatistikDisplayContent(
 						this.spiel.spieldauerSekunden,
 						this.spiel.startDatum,
 						titel,
 						(Spieler[])Utils.klon(this.spiel.spieler),
 						werte,
 						championsJahr,
 						this.spiel.jahr + 1,
 						maxWert,
 						maxWertJahr,
 						maxWertSpieler,
 						minWert,
 						minWertJahr,
 						minWertSpieler,
 						this.markiertesJahrIndex,
 						this.modus == Constants.STATISTIK_MODUS_PUNKTE));
 			 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);
 		}	
 	}
 	
 // =============================
  	protected class Auswertung
  	{
  		private Spiel spiel;
  		private ArrayList<ScreenDisplayContent> replay = new ArrayList<ScreenDisplayContent>();
  		
  		public Auswertung(Spiel spiel)
  		{
  			this.spiel = spiel;
  			
			this.replay = new ArrayList<ScreenDisplayContent>();
			
			this.spiel.console.setModus(Console.ConsoleModus.AUSWERTUNG);
			
			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Auswertung(true),
					Colors.INDEX_NEUTRAL);
			
			this.spiel.console.clear();
			this.spiel.console.appendText(
					SternResources.AuswertungBeginnt(
							true, Integer.toString(this.spiel.jahr+1)));
			this.taste();
			
			// Die Spielzuege werden eingearbeitet
			this.spielzuege();
			
			// Planeten produzieren Energie und Raumer
			for (Planet pl: this.spiel.planeten)
			{
				pl.produziereEvorrat();
				pl.produziereRaumer();
			}
			
			// Ausserirdische einstreuen
			this.ausserirdischeStarten();
			
			// Schwarzes Loch einsetzen
			this.lochEinsetzen();
			
			// Das Kennzeichen "Neu" bei allen Flugobjekten loeschen
			for (Flugobjekt obj: objekte)
				obj.resetNeu();
			
			// Alle Bildschirminhalte aktualisieren
			this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
			this.spiel.updateSpielfeldDisplay();
			
			// Buendnisse aufraeumen
			this.buendnisseAufraeumen();
		
			// Flugobjekte in zufaelliger Reihenfolge bewegen
			int[] seq = Utils.randomList(this.spiel.objekte.size());
			
			for (int i = 0; i < seq.length; i++)
			{
				Flugobjekt obj = this.spiel.objekte.get(seq[i]);
				
				if (obj.isStop() || obj.istZuLoeschen())
					continue;
				
				// Eine Kapitulation
				if (obj.getTyp() == ObjektTyp.KAPITULATION)
				{
					this.kommandozentraleErobert(obj.getBes(), Constants.BESITZER_NEUTRAL, true);
					obj.setZuLoeschen();
					continue;
				}
				
				ArrayList<Flugobjekt> beobachteteAndereObjekte = new ArrayList<Flugobjekt>(); 
				
				ArrayList<Point> felder = obj.bewegen();
				
				// Objekt wird ueber die Sektoren bewegt
				for (int feldIndex = 0; feldIndex < felder.size(); feldIndex++)
				{
					Point feld = felder.get(feldIndex);
					
					// 1. Schwarzes Loch bewegt sich
					this.lochBewegen(obj, feld, (feldIndex > 0));
					
					// 2. Pruefen, ob sich das Objekt ueber das Schwarze Loch bewegt
					this.objektFaelltInsLoch(obj, feld);
					if (obj.istZuLoeschen())
						break;
					
					// 3. Minenfelder
					this.minenfeld(obj, feld);
					if (obj.istZuLoeschen())
						break;
					
					// 4. Patrouillen beobachten ihr Umfeld
					this.patrouilleBeobachtet(obj, feld, beobachteteAndereObjekte);
					if (obj.istZuLoeschen())
						break;
				}
				
				// Das Schwarze Loch wird nach der Bewegung immer geloescht
				if (obj.getTyp() == ObjektTyp.SCHWARZES_LOCH)
					obj.setZuLoeschen();
				
				// Ankunft testen?
				if (obj.istZuLoeschen() || !obj.istAngekommen())
					continue;
				
				// Objekt ist angekommen
				this.ankunft(obj);
			}
			
			// Nicht mehr benoetigte Objekte loeschen
			for (int i = this.spiel.objekte.size() - 1; i >= 0; i--)
			{
				Flugobjekt obj = this.spiel.objekte.get(i);
				if (obj.istZuLoeschen())
					this.spiel.objekte.remove(i);
			}

			// Ende der Auwertung
			this.spiel.updateSpielfeldDisplay();
			this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
			
			this.checkSpielerTot();

			this.spiel.console.setLineColor(Colors.INDEX_WEISS);
			this.spiel.console.appendText(SternResources.AuswertungEnde(true));

			this.taste();
			
			this.spiel.console.setModus(Console.ConsoleModus.TEXT_INPUT);
			
			this.spiel.archiv.get(this.spiel.jahr).setReplay(this.replay);
			
			// Neues Jahr beginnen
			this.spiel.jahr++;
			
			// Punktestand berechnen
			this.spiel.punktestandBerechnen();
			
			// Neue Preise berechnen
			this.spiel.jahrVorbereiten();
			
			// Zeit aktualisieren
			if (!this.spiel.console.isBackground())
				this.spiel.refreshSpieldauerSekunden();
  		}
  		
  		private void spielzuege()
  		{
  			// Spielzuege einarbeiten
  			int[] seq = Utils.randomList(this.spiel.anzSp);
  			
  			for (int i = 0; i < this.spiel.anzSp; i++)
  			{
  				int spieler = seq[i];
  				ArrayList<Spielzug> zuegeSpieler = spielzuege.get(spieler);
  				
  				if (zuegeSpieler == null)
  					continue;
  				
  				for (Spielzug zug: zuegeSpieler)
  				{
  					if (zug.getStopLabel() != null)
  		  			{
  		  				// Gestopptes Objekt
  		  				Flugobjekt obj = null;
  						
  						for (Flugobjekt obj2: this.spiel.objekte)
  						{
  							if (obj2.getStopLabel() != null && obj2.getStopLabel().equals(zug.getStopLabel()))
  							{
  								obj = obj2;
  								break;
  							}
  						}
  						
  						obj.setZpl(zug.getPlIndex());
  						obj.setZiel(this.spiel.planeten[zug.getPlIndex()].getPos());
  						obj.setStop(false);
  		  			}
  		  			else if (zug.getObj() != null)
  		  			{
  		  				// Flugobjekt
  		  				Flugobjekt obj = zug.getObj();
  		  				Planet pl = this.spiel.planeten[obj.getSpl()];
  		  				
  		  				if (obj.getTyp() == ObjektTyp.RAUMER)
  		  				{
  		  					// Pruefen, ob die Raumer gestartet werden koennen
  		  					if (obj.istBuendnis())
  		  					{
  		  						boolean ok = true;
  		  						
  		  						// Gibt es das Buendnis ueberhaupt noch?
  		  						if (pl.buendnisGetMitglieder() == null || pl.buendnisGetMitglieder().length <= 1)
  		  							ok = false;
  		  						// Hat der startende Spieler selbst noch mindestens einen Raumer auf dem Planeten und
  		  						// gibt es noch genuegend Raumer auf dem Planeten?
  		  						else if (!(pl.getRaumerProSpieler(spieler) > 0  &&
  		  							pl.getAnz(ObjektTyp.RAUMER) >= obj.getAnz()))
  		  							ok = false;
  		  						
  		  						if (ok)
  		  						{
	  								// Raumerflotte neu zusammenstellen
	  								int[] abzuege = 
	  										pl.subtractRaumer(this.spiel.anzSp, obj.getAnz(), spieler, true);

	  								Buendnis objBuendnis = pl.uebertrageBuendnisAufFlotte(abzuege);
	  								if (objBuendnis == null)
	  									ok = false;
	  								else
	  									obj.setBuendnis(objBuendnis);
  		  						}
  		  						
  		  						if (!ok)
  		  						{
  		  							this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(obj.getSpl(), Colors.INDEX_WEISS));
  		  							this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
  		  							this.spiel.console.appendText(
  		  								SternResources.AuswertungRaumerNichtGestartet(true,
  		  											this.spiel.spieler[obj.getBes()].getName(),
  		  											this.spiel.getPlanetenNameFromIndex(obj.getSpl())));
  		  	  	 	 				this.taste();
  		  	  	 	 				continue;
  		  						}
  		  					}
  		  					else
  		  					{
  		  						// Kein Buendnisraumer
  		  						if (pl.getRaumerProSpieler(obj.getBes()) < obj.getAnz())
  		  						{
  		  							this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(obj.getSpl(), Colors.INDEX_WEISS));
  		  							this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
  		  							this.spiel.console.appendText(
		  								SternResources.AuswertungRaumerNichtGestartet(true,
		  											this.spiel.spieler[obj.getBes()].getName(),
		  											this.spiel.getPlanetenNameFromIndex(obj.getSpl())));
  		  	  	 	 				this.taste();
  		  	  	 	 				
  		  							continue;
  		  						}
  		  						
  		  						if (pl.istBuendnisMitglied(spieler))
  		  							pl.subtractRaumer(this.spiel.anzSp, obj.getAnz(), spieler, false);
  		  						else
  		  							pl.decObjekt(ObjektTyp.RAUMER, obj.getAnz());
  		  					}

  		  					this.spiel.objekte.add(obj);
  		  				}
  		  				else
  		  				{
  		  					// Andere objekte einfach starten
  		  					if (obj.getTyp() == ObjektTyp.TRANSPORTER)
  		  					{
  		  						pl.subEvorrat(obj.getAnz());
  		  						if (obj.getKz() != null)
  		  							pl.kommandozentraleVerlegen();
  		  					}
  		  					
  		  					pl.decObjekt(obj.getTyp(), 1);
  		  					this.spiel.objekte.add(obj);  					
  		  				}
  		  			}
  		  			else if (zug.getBuendnis() != null)
  		  			{
  		  				// Buendnis geaendert
  		  				if (!this.spiel.planeten[zug.getPlIndex()].spielzugUebernahmeBuendnis(spieler, zug.getBuendnis()))
  		  				{
  		  					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(spieler, Colors.INDEX_WEISS));
  							this.spiel.console.setLineColor(this.spiel.spieler[spieler].getColIndex());
  							
  							this.spiel.console.appendText(
								SternResources.AuswertungBuendnisNichtGeaendert(true,
										this.spiel.spieler[spieler].getName(),
										this.spiel.getPlanetenNameFromIndex(spieler)));
  		 	 				this.taste();
  		  				}
  		  			}
  		  			else if (zug.getPlanetNachher() != null)
  		  			{
  		  				// Planeteneditor
  		  				this.spiel.planeten[zug.getPlIndex()].spielzugUebernahmePlanet(zug.getPlanetNachher());
  		  			}
  				}	// Nachster Spielzug eines Spielers
  			}	// Naechster Spieler
  			
  			this.spiel.spielzuege = new Hashtable<Integer, ArrayList<Spielzug>>();
  		}
  		
  		private void buendnisseAufraeumen()
  		{
  			// Buendnisse aufräumen
  			for (int plIndex = 0; plIndex < this.spiel.anzPl; plIndex++)
  			{
  				Planet pl = this.spiel.planeten[plIndex];
  				int[] abzuege = pl.buendnisAufraeumen(this.spiel.anzSp);
  				
  				for (int sp = 0; sp < abzuege.length; sp++)
  				{
  					if (sp != pl.getBes() && abzuege[sp] > 0)
  					{
  						// Flugobjekt starten, aber gleich wieder stoppen
  						Flugobjekt obj = new Flugobjekt(
  								Constants.KEIN_PLANET,
  								Constants.KEIN_PLANET,
  								this.spiel.planeten[plIndex].getPos(),
  								this.spiel.planeten[plIndex].getPos(),
  								ObjektTyp.RAUMER,
  								abzuege[sp],
  								sp,
  								false,
  								false,
  								null,
  								null);
  						
  						obj.setStop(true);

  						this.spiel.objekte.add(obj);
  						
  						this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
  						
  	 	 				this.spiel.console.setLineColor(this.spiel.spieler[sp].getColIndex());
  	 	 				
  	 	 				this.spiel.console.appendText(
  	 	 						SternResources.AuswertungRaumerVertrieben(true,
  	 	 								Integer.toString(obj.getAnz()),
  	 	 								this.spiel.spieler[sp].getName(),
  	 	 								this.spiel.getPlanetenNameFromIndex(plIndex)));
  	 	 				this.spiel.console.lineBreak();
  	 	 				this.spiel.console.appendText(
  	 	 						SternResources.AuswertungRaumerVertrieben2(true));
  	 	 				this.taste();
  					}
  				}
  			}
  		}
  		
  		private void checkSpielerTot()
  		{
  			// Ist ein Spieler tot?
  			for (int sp = 0; sp < this.spiel.anzSp; sp++)
  			{
  				if (this.spiel.spieler[sp].istTot())
  					continue;
  				
  				// Zaehle Planeten
  				boolean planet = false;
  				for (Planet pl: spiel.planeten)
  				{
  					if (pl.getBes() == sp)
  					{
  						planet = true;
  						break;
  					}
  				}
  				if (planet)
  					continue;
  				
  				// Zahle Raumer, die unterwegs sind
  				boolean raumer = false;
  				
  				for (Flugobjekt obj: spiel.objekte)
  				{
  					if (obj.getRaumerProSpieler(sp) > 0)
  					{
  						raumer = true;
  						break;
  					}
  				}
  				
  				if (raumer)
  					continue;
  				
  				boolean kommandozentraleOk = false;
  				
  				if (!this.spiel.optionen.contains(SpielOptionen.SIMPEL))
  				{
	  				// Kommandozentrale eines Spielers suchen
	  				for (Planet pl: this.spiel.planeten)
	  				{
	  					if (pl.getBes() == sp)
	  					{	  						
	  						if (pl.istKommandozentrale())
	  							kommandozentraleOk = true;
	  					}
	  				}
	  				
	  				for (Flugobjekt obj: this.spiel.objekte)
	  				{
	  					if (obj.getKz() != null && obj.getKz().getSp() == sp)
	  						kommandozentraleOk = true;
	  				}
  				}

  				if (!kommandozentraleOk)
  				{
  					this.spiel.spieler[sp].setTot(true);
  					
  					this.spiel.console.setLineColor(this.spiel.spieler[sp].getColIndex());
  	 				this.spiel.console.appendText(
  	 						SternResources.AuswertungSpielerTot(
  	 								true, this.spiel.spieler[sp].getName()));
  	 				this.taste();
  				}
  			}
  		}
  		
  		private byte getColByPlanet(Planet pl)
  		{
  			if (pl == null)
  				return Colors.INDEX_WEISS;
  			else
  			{
	  			if (pl.getBes() == Constants.BESITZER_NEUTRAL)
	  				return Colors.INDEX_NEUTRAL;
	  			else
	  				return this.spiel.spieler[pl.getBes()].getColIndex();
  			}
  		}
  		
  		private void taste()
  		{
  			this.spiel.console.waitForTaste();
  			
  			ScreenDisplayContent cont = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
  			cont.setPlEdit(null);
			this.replay.add(cont);
  		}
  		
  		private void pause(int milliseconds)
  		{
  			this.spiel.console.pause(milliseconds);
  			
  			ScreenDisplayContent cont = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
  			cont.setPlEdit(null);
  			cont.setPause(true);
			this.replay.add(cont);
  		}
  		
  		private void ausserirdischeStarten()
  		{
  			if (!this.spiel.optionen.contains(SpielOptionen.AUSSERIRDISCHE) || this.spiel.jahr < Constants.AUSSERIRDISCHE_ERSTES_JAHR)
  				return;
	
			if (Utils.random(Constants.AUSSERIRDISCHE_WAHRSCHEINLICHKEIT) != 0)
				return;
			
			// Suche einen Planeten zufaellig aus
			int[] plListe = Utils.randomList(this.spiel.anzPl);
			
			for (int i = 0; i < this.spiel.anzPl; i++)
			{
				int pl = plListe[i];
				
				if (this.spiel.planeten[pl].getBes() != Constants.BESITZER_NEUTRAL)
				{
					Planet zielPlanet = this.spiel.planeten[pl];
					
					// Startfeld zufaellig bestimmen
					Point startPos = Point.getRandom(this.spiel.xOff, this.spiel.yOff, this.spiel.breite, this.spiel.hoehe);
					
					// Anzahl
					int anz = (int)(Math.log(this.spiel.jahr+1) / Math.log(1.02));
					anz += (int)((double)anz * (double)(Utils.random(161) - 80) / 100.);
					
					// Raumerflotte starten
					this.spiel.objekte.add(new Flugobjekt(
							Constants.KEIN_PLANET,
							pl,
							startPos,
							zielPlanet.getPos(),
							ObjektTyp.RAUMER,
							anz,
							Constants.BESITZER_NEUTRAL,
							false,
							false,
							null,
							null));
					
					break;
				}
			}
  		}
  	
  		private void lochEinsetzen()
  		{
			if (!this.spiel.optionen.contains(SpielOptionen.SCHWARZES_LOCH) || this.spiel.loch == null)
				return;
	
			if (this.spiel.loch.isActive())
			{
				// Loch ist aktiv. Soll es verschwinden?
				if (this.spiel.jahr >= this.spiel.loch.getNextActionJahr())
				{
					this.spiel.loch = new Loch(false,
							null,
							this.spiel.jahr + Constants.LOCH_MINDESTDAUER_WEG +
									Utils.random(Constants.LOCH_DAUER_SPANNE_WEG));
					return;
				}	
			}
			else
			{
				// Loch ist inaktiv. Soll es erscheinen?
				if (this.spiel.jahr < this.spiel.loch.getNextActionJahr())
					return;
				
				Point zielPosition = null;
				boolean ok = true;
				
				do
				{
					// Loch soll an einem zufaelligen Ort erscheinen, aber nicht auf einem Planeten
					zielPosition = Point.getRandom(this.spiel.xOff, this.spiel.yOff, this.spiel.breite, this.spiel.hoehe);
					ok = true;
					
					for (Planet pl: this.spiel.planeten)
					{
						if (pl.getPos().equals(zielPosition))
						{
							ok = false;
							break;
						}
					}
				} while (!ok);
				
				this.spiel.loch = new Loch(true,
									zielPosition,
									this.spiel.jahr + Constants.LOCH_MINDESTDAUER +
											Utils.random(Constants.LOCH_DAUER_SPANNE));
			}
			
			if (!this.spiel.loch.isActive())
				return;
		
			// Loch bewegen
			// Gehe alle Sektoren um den momentanen Standort herum durch
			int[] seq = Utils.randomList((Constants.LOCH_MAX_DX * 2 + 1) * (Constants.LOCH_MAX_DY * 2 + 1));
			boolean ok = true;
			Point zielPosition = null;
			
			for (int i = 0; i < seq.length; i++)
			{
				ok = true;
				
				int dx = (seq[i] % (Constants.LOCH_MAX_DX * 2 + 1)) - Constants.LOCH_MAX_DX;
				int dy= (seq[i] / (Constants.LOCH_MAX_DY * 2 + 1)) - Constants.LOCH_MAX_DY;
				
				// Neue Position darf nicht die alte Position sein und muss innerhalb des Spielfeldes liegen
				if ((dx == 0 && dy == 0) ||
						this.spiel.loch.getPos().getX() + dx < this.spiel.xOff || this.spiel.loch.getPos().getX() + dx >= this.spiel.xOff + this.spiel.breite ||
					    this.spiel.loch.getPos().getY() + dy < this.spiel.yOff || this.spiel.loch.getPos().getY() + dy >= this.spiel.yOff + this.spiel.hoehe)
					continue;
				
				zielPosition = new Point(this.spiel.loch.getPos().getX() + dx, this.spiel.loch.getPos().getY() + dy);
				
				// Neue Position darf nicht auf einem anderen Planeten liegen
				for (Planet pl: this.spiel.planeten)
				{
					if (pl.getPos().equals(zielPosition))
					{
						ok = false;
						break;
					}
				}
				
				if (ok == true)
					break;
			}
			
			// "Flugobjekt" fuer das schwarze Loch anlegen
			Flugobjekt obj = new Flugobjekt(
					Constants.KEIN_PLANET,
					Constants.KEIN_PLANET,
					this.spiel.loch.getPos(),
					zielPosition,
					ObjektTyp.SCHWARZES_LOCH,
					0,
					Constants.BESITZER_NEUTRAL,
					false,
					true,
					null,
					null);
			
			this.spiel.objekte.add(obj);
  			
  		}
  		
  		private void lochBewegen(Flugobjekt objLoch, Point feld, boolean taste)
  		{
  			if (objLoch.getTyp() != ObjektTyp.SCHWARZES_LOCH)
  				return;
  			
  			if (taste)
  			{
  				this.spiel.console.setLineColor(Colors.INDEX_WEISS);
	  			this.spiel.console.appendText(
	  					SternResources.AuswertungSchwarzesLochBewegtSich(true));
	  			this.pause(Constants.PAUSE_MILLISECS);
	  			this.spiel.console.lineBreak();
  			}
  			
  			this.spiel.loch.setPosition(feld);
  			this.spiel.updateSpielfeldDisplay();
  			
  			ArrayList<Flugobjekt> andereObjekte = this.findeFlugobjekteAufFeld(feld, objLoch);
  			if (andereObjekte.size() == 0)
  				return;
  			
  			// Alle "anderen Objekte" fallen ins Loch
  			for (Flugobjekt anderesObj: andereObjekte)
  				this.objektFaelltInsLoch(anderesObj, feld);  			
  		}
  		
  		private void objektFaelltInsLoch(Flugobjekt obj, Point feld)
  		{
  			if (obj.getTyp() == ObjektTyp.SCHWARZES_LOCH || obj.istZuLoeschen())
				return;
		
			if (this.spiel.loch == null || !this.spiel.loch.isActive() || !this.spiel.loch.getPos().equals(feld))
				return;
			
			if (obj.getBes() != Constants.BESITZER_NEUTRAL)
			{
				this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
				this.spiel.console.appendText(
						SternResources.AuswertungNachrichtAnAusSektor(true,
								this.spiel.spieler[obj.getBes()].getName(),
								Spiel.getSectorNameFromPoint(feld)));
			}
			else
			{
				this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
				this.spiel.console.appendText(
						SternResources.AuswertungNachrichtAnAusserirdischeAusSektor(true,
								Spiel.getSectorNameFromPoint(feld)));
			}
			
			this.spiel.console.lineBreak();
			
			if (obj.getTyp() == ObjektTyp.RAUMER)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochRaumer(true, 
								Integer.toString(obj.getAnz())));
			else if (obj.getTyp() == ObjektTyp.AUFKLAERER)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochAufklaerer(true));
			else if (obj.getTyp() == ObjektTyp.MINE50 || obj.getTyp() == ObjektTyp.MINE100 || obj.getTyp() == ObjektTyp.MINE250 || obj.getTyp() == ObjektTyp.MINE500)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochMinenleger(true));
			else if (obj.getTyp() == ObjektTyp.MINENRAEUMER)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochMinenraeumer(true));
			else if (obj.getTyp() == ObjektTyp.PATROUILLE)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochPatrouille(true));
			else if (obj.getTyp() == ObjektTyp.TRANSPORTER)
				this.spiel.console.appendText(
						SternResources.AuswertungSchwarzesLochTransporter(true));
			
			obj.setZuLoeschen();
			
			this.spiel.updateSpielfeldDisplay(
					this.spiel.getSimpleMarkedField(feld));
			
			// Taste. Weiter mit naechstem Objekt.
			this.taste();
			
			// Kommandozentrale im Loch verschwunden?
			if (obj.getKz() != null)
				this.kommandozentraleErobert(obj.getBes(), Constants.BESITZER_NEUTRAL, false);
  		}
  		
  		private void patrouilleBeobachtet(Flugobjekt objPatr, Point feld, ArrayList<Flugobjekt> beobachteteAndereObjekte)
  		{
  			if (!(objPatr.getTyp() == ObjektTyp.PATROUILLE && !objPatr.isTransfer()))
  				return;
  			
  			int[] umfeldreihenfolge = Utils.randomList((Constants.PATROUILLE_BEOBACHTUNG_DX * 2 + 1) * (Constants.PATROUILLE_BEOBACHTUNG_DY * 2 + 1));
  			
  			for (int i = 0; i < umfeldreihenfolge.length; i++)
  			{
				int dx = (umfeldreihenfolge[i] % (Constants.PATROUILLE_BEOBACHTUNG_DX * 2 + 1)) - Constants.PATROUILLE_BEOBACHTUNG_DX;
				int dy = (umfeldreihenfolge[i] / (Constants.PATROUILLE_BEOBACHTUNG_DY * 2 + 1)) - Constants.PATROUILLE_BEOBACHTUNG_DY;

				Point umfeld = new Point(feld.getX()+dx,feld.getY()+dy);
				
				ArrayList<Flugobjekt> andereObjekte = this.findeFlugobjekteAufFeld(umfeld, objPatr);
				if (andereObjekte.size() == 0)
					continue;
				
				for (Flugobjekt anderesObj: andereObjekte)
				{
					if (anderesObj.istZuLoeschen() || 
						anderesObj.getTyp() == ObjektTyp.SCHWARZES_LOCH ||
						objPatr.getBes() == anderesObj.getBes() ||
						beobachteteAndereObjekte.contains(anderesObj))
						continue;
					
					// Anderes Objekt als "beobachtet" markieren, damit es in derselben
					// Runde von derselben Patrouille nicht zweimal beobachtet wird.
					beobachteteAndereObjekte.add(anderesObj);
					
					// Spezialfall Buendnisraumer. Wenn der Besitzer der Flotte Buendnismitglied auf dem Zielplaneten ist, 
					// dann nicht melden
					if (!anderesObj.isStop() && anderesObj.getTyp() == ObjektTyp.RAUMER && this.spiel.planeten[anderesObj.getZpl()].istBuendnisMitglied(anderesObj.getBes()))
						continue;
					
					// Wenn der Besitzer der Patrouille an der Buendnisflotte beteiligt ist, dann auch nicht melden.
					if (anderesObj.istBeteiligt(objPatr.getBes()))
						continue;

					// Zwei Patrouillen kaempfen gegeneinander
					if (anderesObj.getTyp() == ObjektTyp.PATROUILLE && !anderesObj.isTransfer())
					{
						boolean entdeckerVerliert = (Utils.random(6) + 1 > 3);
						
						Flugobjekt objSieger = entdeckerVerliert ?
												anderesObj : objPatr;
						Flugobjekt objVerlierer = entdeckerVerliert ?
												objPatr : anderesObj;
						
						this.spiel.console.setLineColor(this.spiel.spieler[objSieger.getBes()].getColIndex());
						this.spiel.console.appendText(
							SternResources.AuswertungPatrouilleMeldetAusSektor(true,
										this.spiel.spieler[objSieger.getBes()].getName(),
										Spiel.getSectorNameFromPoint(objVerlierer.getCurrentField())));
						this.spiel.console.lineBreak();
						
						// Sektor markieren
						this.spiel.updateSpielfeldDisplay(
								this.spiel.getSimpleMarkedField(
										objVerlierer.getExactPos()));
						
						this.spiel.console.appendText(
								SternResources.AuswertungPatrouillePatrouilleZerstoert(true));
						
						
						this.taste();
						objVerlierer.setZuLoeschen();
					}
					else
					{
						// Meldung machen. Kleinobjekte kapern
						// Feld markieren
						this.spiel.updateSpielfeldDisplay(
								this.spiel.getSimpleMarkedField(anderesObj.getExactPos()));
						
						this.spiel.console.setLineColor(this.spiel.spieler[objPatr.getBes()].getColIndex());
						this.spiel.console.appendText(
								SternResources.AuswertungPatrouilleMeldetAusSektor(true,
										this.spiel.spieler[objPatr.getBes()].getName(),
										getSectorNameFromPoint(anderesObj.getCurrentField())));
						this.spiel.console.lineBreak();
						
						boolean kapern = true;
						
						if (anderesObj.getTyp() == ObjektTyp.AUFKLAERER)
							this.spiel.console.appendText(
											SternResources.AuswertungPatrouilleAufklaererGekapert(true));
						else if (anderesObj.getTyp() == ObjektTyp.TRANSPORTER)
							this.spiel.console.appendText(
											SternResources.AuswertungPatrouilleTransporterGekapert(true));
						else if (anderesObj.getTyp() == ObjektTyp.MINENRAEUMER)
							this.spiel.console.appendText(
											SternResources.AuswertungPatrouilleMinenraeumerGekapert(true));
						else if (anderesObj.getTyp() == ObjektTyp.MINE50 || 
								 anderesObj.getTyp() == ObjektTyp.MINE100 ||
								 anderesObj.getTyp() == ObjektTyp.MINE250 ||
								 anderesObj.getTyp() == ObjektTyp.MINE500)
							this.spiel.console.appendText(
											SternResources.AuswertungPatrouilleMinenlegerGekapert(true));
						else if (anderesObj.getTyp() == ObjektTyp.PATROUILLE)
							this.spiel.console.appendText(
											SternResources.AuswertungPatrouillePatrouilleGekapert(true));
						else if (anderesObj.getTyp() == ObjektTyp.RAUMER)
						{
							if (anderesObj.getAnz() > Constants.PATROUILLE_KAPERT_RAUMER)
							{
								this.spiel.console.appendText(
										SternResources.AuswertungPatrouilleRaumerGesichtet(true, 
												Integer.toString(anderesObj.getAnz())));
								kapern = false;
								this.taste();
							}
							else
							{
								this.spiel.console.appendText(
										SternResources.AuswertungPatrouilleRaumerGekapert(true, 
												Integer.toString(anderesObj.getAnz())));
							}
						}
						
						if (kapern)
						{
							this.taste();
							
							anderesObj.gekapert(
									objPatr.getBes(),
									umfeld);
						}	
					}
					if (objPatr.istZuLoeschen())
						break;
				} // Naechstes anderes Objekt
				
				if (objPatr.istZuLoeschen())
					break;
  			} // Naechstes Umfeld
  		}
  		
  		private void minenfeld(Flugobjekt obj, Point feld)
  		{
  			if (this.spiel.minen == null || this.spiel.minen.size() == 0 || obj.istZuLoeschen())
  				return;
		
			Mine mine = this.spiel.minen.get(feld.getString());
			if (mine == null)
				return;
			
			if (obj.getTyp() == ObjektTyp.RAUMER)
			{
				if (obj.getBes() != Constants.BESITZER_NEUTRAL)
				{
					this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
					this.spiel.console.appendText (
							SternResources.AuswertungNachrichtAnAusSektor(true,
									this.spiel.spieler[obj.getBes()].getName(),
									Spiel.getSectorNameFromPoint(mine.getPos())));
				}
				else
				{
					this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
					this.spiel.console.appendText (
							SternResources.AuswertungNachrichtAnAusserirdischeAusSektor(true,
									Spiel.getSectorNameFromPoint(mine.getPos())));
				}
				
				this.spiel.console.lineBreak();
				
				if (obj.getAnz() >= mine.getStaerke())
				{
					// Mine wurde geraeumt
					this.spiel.console.appendText(
						SternResources.AuswertungRaumerAufMineGelaufenZerstoert(true, 
								Integer.toString(Math.min(obj.getAnz(),mine.getStaerke()))));
					
					// Anzahl der Raumerflotte reduzieren
					obj.subtractRaumer(mine.getStaerke(), obj.getBes());
					
					this.spiel.minen.remove(feld.getString());
				}
				else
				{
					this.spiel.console.appendText(
							SternResources.AuswertungRaumerAufMineGelaufen(
									true, 
									Integer.toString(Math.min(obj.getAnz(),mine.getStaerke()))));
					
					// Flotte wurde zerstoert
					obj.setZuLoeschen();
					
					// Staerke des Minenfeldes reduzieren
					mine.setStaerke(mine.getStaerke() - obj.getAnz());
				}
			}
			else if (obj.getTyp() == ObjektTyp.SCHWARZES_LOCH)
			{
				this.spiel.console.appendText(
						SternResources.AuswertungMinenfeldImLoch(
								true, 
								Integer.toString(mine.getStaerke())));
				this.spiel.minen.remove(feld.getString());
			}
			else if (obj.getTyp() == ObjektTyp.MINENRAEUMER)
			{
				this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
				this.spiel.console.appendText (
						SternResources.AuswertungNachrichtAnAusSektor(true,
								this.spiel.spieler[obj.getBes()].getName(),
								Spiel.getSectorNameFromPoint(mine.getPos())));
				this.spiel.console.lineBreak();
				this.spiel.console.appendText (
						SternResources.AuswertungMinenfeldGeraeumt(true,
								Integer.toString(mine.getStaerke())));
				
				
				this.spiel.minen.remove(feld.getString());
			}
			else
				return;
			
			// Spielfeld aktualisieren
			this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleMarkedField(feld));
			
			// Taste.
			this.taste();

  		}
  		
  		private void ankunft(Flugobjekt obj)
  		{
  			if (obj.istZuLoeschen() || !obj.istAngekommen())
  				return;
  			
			int plIndex = obj.getZpl();
			Planet pl = null;
			
			if (plIndex != Constants.KEIN_PLANET)
				pl = this.spiel.planeten[plIndex];
			
			this.spiel.console.setLineColor(this.getColByPlanet(pl));
			
			if (obj.getTyp() == ObjektTyp.AUFKLAERER)
			{
				if (pl.getBes() == obj.getBes())
				{
					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
					
					pl.incObjekt(obj.getTyp(), 1);
					
					this.spiel.console.appendText(
							SternResources.AuswertungAufklaererAngekommen(
									true,
									this.spiel.getPlanetenNameFromIndex(plIndex)));
				}
				else
				{
					// Aufklaerer installiert einen Spionagesender
					this.spiel.updateSpielfeldDisplay();
					pl.setSender(obj.getBes(), this.spiel.jahr + Constants.SENDER_JAHRE);
					
					this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
					this.spiel.console.appendText(
							SternResources.AuswertungAufklaererSender(
									true,
									this.spiel.spieler[obj.getBes()].getName()));								
				}
				this.taste();
				obj.setZuLoeschen();
			}
			else if (obj.getTyp() == ObjektTyp.TRANSPORTER)
			{
				this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
				
				// Energie uebertragen
				pl.addEvorrat(obj.getAnz());
				
				if (pl.getBes() == obj.getBes())
				{
					pl.incObjekt(obj.getTyp(), 1);
					
					this.spiel.console.appendText(
							SternResources.AuswertungTransporterAngekommen(
									true, 
									this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
				}
				else
					this.spiel.console.appendText(
							SternResources.AuswertungTransporterZerschellt(
									true, this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
				
				Kommandozentrale kz = obj.getKz();
				
				if (kz != null)
				{
					if (kz.getSp() == pl.getBes())
					{
						pl.kommandozentraleAufnehmen(obj.getKz());
						this.taste();
					}
					else
					{
						this.taste();
						this.kommandozentraleErobert(obj.getKz().getSp(), pl.getBes(), false);
					}
				}
				else
					this.taste();
				
				obj.setZuLoeschen();
			}
			else if (obj.getTyp() == ObjektTyp.PATROUILLE)
			{
				if (pl.getBes() == obj.getBes())
				{
					if (obj.isTransfer())
					{
						this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
						if (obj.getBes() == pl.getBes())
						{
							pl.incObjekt(obj.getTyp(), 1);
							this.spiel.console.appendText(
									SternResources.AuswertungPatrouilleAngekommen(
											true, 
											this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
						}
						else
							this.spiel.console.appendText(
									SternResources.AuswertungPatrouilleZerschellt(
											true,
											this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
						this.taste();
						obj.setZuLoeschen();
					}
					else
						// Patrouille wenden
						obj.wenden();
				}
				else
				{
					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
					this.spiel.console.appendText(
						SternResources.AuswertungPatrouilleZerschellt(
								true, 
								this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					this.taste();
					obj.setZuLoeschen();
				}
			}
			else if (obj.getTyp() == ObjektTyp.MINE50 ||
					 obj.getTyp() == ObjektTyp.MINE100 ||
					 obj.getTyp() == ObjektTyp.MINE250 ||
					 obj.getTyp() == ObjektTyp.MINE500)
			{
				if (obj.isTransfer())
				{
					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
					if (obj.getBes() == pl.getBes())
					{
						pl.incObjekt(obj.getTyp(), 1);
						this.spiel.console.appendText(
								SternResources.AuswertungMinenlegerAngekommen(
										true, 
										this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					}
					else
						this.spiel.console.appendText(
							SternResources.AuswertungMinenlegerZerschellt(
									true, 
									this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					
					this.taste();
					obj.setZuLoeschen();
				}
				else
				{
					obj.setZuLoeschen();
					this.spiel.updateSpielfeldDisplay();
					this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
					this.spiel.console.appendText(
							SternResources.AuswertungMineGelegt(true, this.spiel.spieler[obj.getBes()].getName()));
					this.mineLegen(obj);
					this.taste();
				}
			}
			else if (obj.getTyp() == ObjektTyp.MINENRAEUMER)
			{
				if (obj.getZpl() == Constants.KEIN_PLANET)
					// Minenraeumer wenden. Transfer-Kennzeichen aendern
					obj.wenden(true);
				else
				{
					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
					if (obj.getBes() == pl.getBes())
					{
						pl.incObjekt(obj.getTyp(), 1);
						this.spiel.console.appendText(
							SternResources.AuswertungMinenraeumerAngekommen(true, this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					}
					else
						this.spiel.console.appendText(
							SternResources.AuswertungMinenraeumerZerschellt(true, this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					this.taste();
					obj.setZuLoeschen();

				}
			}
			else if (obj.getTyp() == ObjektTyp.RAUMER)
			{
				if (this.spiel.planeten[obj.getZpl()].getBes() == obj.getBes() ||
					this.spiel.planeten[obj.getZpl()].istBuendnisMitglied(obj.getBes()))
				{
					// Freund
					this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
					this.spiel.planeten[obj.getZpl()].mergeRaumer(this.spiel.anzSp, obj);
					
					this.spiel.console.appendText(
						SternResources.AuswertungRaumerAngekommen(true,
									Integer.toString(obj.getAnz()),
									this.spiel.getPlanetenNameFromIndex(obj.getZpl())));
					
					this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
					this.taste();
				}
				else
					// Feind. Angriff auf Planet
					this.raumerAngriff(obj, plIndex);
				
				obj.setZuLoeschen();
			}
  		}
  		
  		private void raumerAngriff(Flugobjekt obj, int plIndex)
  		{
  			Planet pl = this.spiel.planeten[plIndex];
  			
  			this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
  			
			int angr = obj.getAnz();
			
			int festung = pl.getFestungFaktor() * 
					(Constants.FESTUNG_STAERKE_MIN + Utils.random(Constants.FESTUNG_STAERKE_SPANNE));
			festung = Utils.round(((double)pl.getFestungIntakt() / 100.) * (double)festung);
			
			int vert = festung > 0 ? vert = festung : pl.getAnz(ObjektTyp.RAUMER);
			String vertBezeichnung = 
					festung > 0 ? 
							SternResources.AuswertungAngriffFestung(true) : 
							SternResources.AuswertungAngriffPlanet(true);
			
			this.spiel.console.clear();
			this.spiel.console.setLineColor(pl.getCol(this.spiel.spieler));
			this.spiel.console.appendText(
							SternResources.AuswertungAngriffAngriffAufPlanet(
									true,
									this.spiel.getPlanetenNameFromIndex(plIndex)));

			this.spiel.console.lineBreak();
			this.spiel.console.setLineColor(Colors.INDEX_WEISS);
			this.spiel.console.appendText(SternResources.AuswertungAngriffAngreifer(true) +": " + angr + ", "+vertBezeichnung+": " + vert + ".");
			this.pause(Constants.PAUSE_MILLISECS);
			this.spiel.console.lineBreak();
						
			while (angr > 0 && vert > 0)
			{
				int sa = angr;
				int sb = vert + 
						Utils.round((double)vert * 
								(double)(Constants.VERT_BONUS_PROZENT_MIN + Utils.random(Constants.VERT_BONUS_PROZENT_SPANNE)) / 100.);

				sa = (int)(Utils.random(sa) / 6) + 1;
				sb = (int)(Utils.random(sb) / 6) + 1;
				
				angr -= sb;
				vert -= sa;
				
				if (angr < 0)
					angr = 0;
				if (vert < 0)
					vert = 0;
				
				if (vert == 0 && pl.getFestungFaktor() > 0)
				{
					this.spiel.console.appendText(
							SternResources.AuswertungAngriffAngreifer(true) +": " + angr + ", "+vertBezeichnung+": " + vert + ".");
					this.pause(Constants.PAUSE_MILLISECS);
					this.spiel.console.lineBreak();
					
					// Festung zerstoert!
					pl.deleteFestung();
					
					// Angreifer kaempfen ab jetzt gegen Raumer 
					vert = pl.getAnz(ObjektTyp.RAUMER);

					this.spiel.console.appendText(
							SternResources.AuswertungAngriffFestungZerstoert(true));
					this.pause(Constants.PAUSE_MILLISECS);
					this.spiel.console.lineBreak();
					vertBezeichnung = SternResources.AuswertungAngriffPlanet(true);
				}
				
				this.spiel.console.appendText(
						SternResources.AuswertungAngriffAngreifer(true) +": " + angr + ", "+vertBezeichnung+": " + vert + ".");
				this.pause(Constants.PAUSE_MILLISECS);
				
				if (angr == 0 || vert == 0)
					this.spiel.console.lineBreak();
				else
					this.spiel.console.deleteLine();
				
			}
			
			if (angr == 0 && vert != 0)
			{
				// Reststaerke der Festung berechnen
				if (pl.getFestungFaktor() > 0)
					pl.setFestungIntakt(Math.max(Utils.round(100. * (double)vert / (double)festung), 1));
				else
					// Raumer auf Planet reduzieren
					pl.subtractRaumer(this.spiel.anzSp, pl.getAnz(ObjektTyp.RAUMER) - vert, pl.getBes(), true);
			}
			
			// Ende des Kampfes
			Kommandozentrale kz = null;
			
			if (angr == 0 && vert == 0)
			{
				this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
				this.spiel.console.appendText(
						SternResources.AuswertungAngriffUntentschieden(true));
				this.spiel.console.lineBreak();
				this.spiel.console.appendText(
						SternResources.AuswertungAngriffEnergieproduktionHalbiert(true));
				kz = pl.erobert(this.spiel.anzSp, Constants.BESITZER_NEUTRAL, null);
			}
			else if (angr == 0 && vert != 0)
			{
				this.spiel.console.appendText(
						SternResources.AuswertungAngriffAngriffGescheitert(true));					
			}
			else
			{
				if (obj.getBes() == Constants.BESITZER_NEUTRAL)
				{
					this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
					this.spiel.console.appendText(
							SternResources.AuswertungAngriffAusserirdischeErobert(true));
					this.spiel.console.lineBreak();
					this.spiel.console.appendText(
							SternResources.AuswertungAngriffEnergieproduktionHalbiert(true));
				}
				else
				{
					this.spiel.console.setLineColor(this.spiel.spieler[obj.getBes()].getColIndex());
					this.spiel.console.appendText(
							SternResources.AuswertungAngriffSpielerErobert(true,
									this.spiel.spieler[obj.getBes()].getName()));
				}
				
				// Verluste von der Flotte abziehen
				obj.subtractRaumer(obj.getAnz() - angr,obj.getBes());
				
				// Eigentum uebertragen. Evtl. Produktion halbieren, wenn Planet wieder neutral ist
				kz = pl.erobert(this.spiel.anzSp, obj.getBes(), obj);
			}
			
			this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
			this.spiel.updateSpielfeldDisplay(this.spiel.getSimpleFrameObjekt(plIndex, Colors.INDEX_WEISS));
			
			this.taste();
			
			if (kz != null)
				this.kommandozentraleErobert(kz.getSp(), obj.getBes(), false);
  		}
  		
  		private void mineLegen(Flugobjekt obj)
  		{
  			if (this.spiel.minen == null)
  				this.spiel.minen = new Hashtable<String,Mine>();
  			
  			int staerke = 0;
  			
  			if (obj.getTyp() == ObjektTyp.MINE50)
  				staerke = 50;
  			else if (obj.getTyp() == ObjektTyp.MINE100)
  				staerke = 100;
  			else if (obj.getTyp() == ObjektTyp.MINE250)
  				staerke = 250;
  			else
  				staerke = 500;
  			
  			MineHistorie mh = new MineHistorie(obj.getBes(), obj.getTyp());
  			
  			String key = obj.getZiel().getString();
  			Mine mine = this.spiel.minen.get(key);
  			
  			if (mine == null)
  			{
  				ArrayList<MineHistorie> historie = new ArrayList<MineHistorie>();
  				historie.add(mh);
  				this.spiel.minen.put(key, new Mine(obj.getZiel(), staerke, historie));
  			}
  			else
  				mine.add(staerke, mh);
  		}
  		
  		private void kommandozentraleErobert(int spVerlierer, int spGewinner, boolean kapituliert)
  		{
  			// Spieler "spGewinner" hat die Kommandozentrale von Spieler "spVerlierer" erobert
  			if (spGewinner == Constants.BESITZER_NEUTRAL)
  			{
  				if (kapituliert)
  				{
  					this.spiel.console.setLineColor(this.spiel.spieler[spVerlierer].getColIndex());
  					
  					this.spiel.console.appendText(
	  						SternResources.AuswertungKapitulation(
	  								true,
	  								this.spiel.spieler[spVerlierer].getName()));
  				}
  				else
  				{
	  				this.spiel.console.setLineColor(Colors.INDEX_NEUTRAL);
	  				
	  				this.spiel.console.appendText(
	  						SternResources.AuswertungKommandozentraleErobertNeutral(
	  								true,
	  								this.spiel.spieler[spVerlierer].getName()));
  				}
  			}
  			else
  			{
  				this.spiel.console.setLineColor(this.spiel.spieler[spGewinner].getColIndex());
  				
  				this.spiel.console.appendText(
  						SternResources.AuswertungKommandozentraleErobertSpieler(true,
  								this.spiel.spieler[spGewinner].getName(),
  								this.spiel.spieler[spVerlierer].getName()));
  			}
  			
  			// Besitzer aller Planeten wechseln
  			for (Planet pl: this.spiel.planeten)
  			{
  				pl.spielerwechsel(spVerlierer, spGewinner);
  			}
  				
  			// Besitzer aller Flugobjekte und Teilnahme an Buendnissen wechseln
  			for (Flugobjekt obj: this.spiel.objekte)
  			{
  				int weitererEroberterSpieler = obj.spielerwechsel(spVerlierer, spGewinner);
  				
  				// Falls die Kommandozentrale eines weiteren Spielers in die Haende
  				// der Ausserirdischen faellt...
  				if (weitererEroberterSpieler != Constants.BESITZER_NEUTRAL)
  				{
  					this.spiel.console.lineBreak();
  					this.kommandozentraleErobert(weitererEroberterSpieler, spGewinner, false);
  				}
  			}
  			  			
  			this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
  			this.spiel.updateSpielfeldDisplay();
  			
  			this.taste();
  				
  		}

  		
  		private ArrayList<Flugobjekt> findeFlugobjekteAufFeld(Point pt, Flugobjekt excludeObj)
  		{
  			// Finde alle Flugobjekte auf einem bestimmten Feld. Sortiere die zurueckgegebenen Objekte in
  			// zufaelliger Reihenfolge. "excludeObj" kann mitgegeben werden. Dieses Objekte taucht nicht
  			// in der Rueckgabeliste auf
  			ArrayList<Integer> objekteIDs = new ArrayList<Integer>();
  			
  			for (int i = 0; i < this.spiel.objekte.size(); i++)
  			{
  				Flugobjekt obj = this.spiel.objekte.get(i);
  				
  				if (obj.getTyp() == ObjektTyp.KAPITULATION)
  					continue;	
  				
  				if (excludeObj != null && obj == excludeObj)
  					continue;
  				
  				// Objekte mit Loeschvormerkung werden ausgeschlossen
  				if (!obj.istZuLoeschen() && obj.getCurrentField().equals(pt))
  					objekteIDs.add(i);
  			}
  			
  			// Objekte zufaellig sorieren
  			int[] seq = Utils.randomList(objekteIDs.size());
  			ArrayList<Flugobjekt> retval = new ArrayList<Flugobjekt>();
  			
  			for (int i = 0; i < seq.length; i++)
  				retval.add(this.spiel.objekte.get(objekteIDs.get(seq[i])));
  			
  			return retval;
  		}

  	}
  	
  	// ===============
  	
  	protected class Replay
 	{
 		private Spiel spiel;
 		
 		public Replay(Spiel spiel)
 		{
 			this.spiel = spiel;
 			
 			if (spiel.goToReplay)
 			{
 				spiel.goToReplay = false;
 				
 				if (this.spiel.archiv.containsKey(this.spiel.jahr - 1))
 				{
 					// Zeige die letzte Auswertung
 					Archiv archiv = this.spiel.archiv.get(this.spiel.jahr - 1);
 					
 					if (archiv.getReplaySize() > 0)
 						this.replayArchive(this.spiel.jahr - 1, archiv);
 				}
 				
 				return;
 			}
 						
			this.spiel.console.clear();
			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> " + SternResources.ReplayAuswertungWiedergeben(true), Colors.INDEX_NEUTRAL);
			
			// Ab welchem Jahr gibt es Auswertungen?
			ArrayList<Integer> jahre;
			
			if (this.spiel.archiv != null)
			{
				jahre = new ArrayList<Integer>(this.spiel.archiv.keySet());
				Collections.sort(jahre);
			}
			else
				jahre = new ArrayList<Integer>();
			
			if (jahre.size() == 0)
			{
				this.spiel.console.appendText(SternResources.ReplayKeineAuswertungen(true));
				this.spiel.console.waitForTaste();
				return;
			}
			
 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
			
 			if (this.spiel.optionen.contains(SpielOptionen.SERVER_BASIERT))
 				keys.add(new ConsoleKey("1-" + this.spiel.jahr,SternResources.ReplayErstesJahrServer(true)));
 			else
 				keys.add(new ConsoleKey("1-" + this.spiel.jahr,SternResources.ReplayErstesJahr(true)));
			keys.add(new ConsoleKey("-",SternResources.ReplayLetztesJahr(true)));
			
			int erstesJahr = -1;
			
			do
			{
				this.spiel.console.appendText(
						SternResources.ReplayWiedergabeAbWelchemJahr(true) + " ");
				ConsoleInput input = this.spiel.console.waitForTextEntered(10, keys, false, true);
				
				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
				{
					this.spiel.console.outAbbruch();
					return;
				}
				
				if (input.getInputText().toUpperCase().equals("-"))
					erstesJahr = this.spiel.jahr - 1;
				else
				{
					try
					{
						erstesJahr = Integer.parseInt(input.getInputText()) - 1; 
					}
					catch (Exception e)
					{
						this.spiel.console.outUngueltigeEingabe();
						continue;
					}
				}
				
				if (erstesJahr < 0 || erstesJahr >= this.spiel.jahr)
				{
					this.spiel.console.outUngueltigeEingabe();
					continue;
				}
				
				if (erstesJahr == 0)
					erstesJahr = jahre.get(0);
				
				break;
			} while (true);
					
			// Startjahr ausgewaehlt
			
			// Bei serverbasierten Spielen eventuell fehlende Jahre nachladen
			if (this.spiel.optionen.contains(SpielOptionen.SERVER_BASIERT))
			{
				int vonJahr = -1;
				int bisJahr = -1;
				
				for (Integer j = erstesJahr; j <  this.spiel.jahr - 1; j++)
				{
					if (!this.spiel.archiv.get(j).auswertungExists())
					{
						bisJahr = j;
						
						if (vonJahr == -1)
							vonJahr = j;
					}
				}
				
				if (vonJahr != -1)
				{
					Hashtable<Integer,Archiv> neueArchive = this.spiel.spielThread.getEvaluations(
							this.spiel.name, 
							vonJahr, 
							bisJahr);
					
					this.spiel.console.lineBreak();
					
					if (neueArchive != null)
					{
						this.spiel.addArchiv(neueArchive);
					}
				}
			}
			
			boolean ende = false;
			for (int jahrIndex = 0; jahrIndex < jahre.size(); jahrIndex++)
			{
				int jahr = jahre.get(jahrIndex);
				if (jahr < erstesJahr ||
					!this.spiel.archiv.containsKey(jahr) ||
					this.spiel.archiv.get(jahr).getReplaySize() <= 0)
					continue;
				
				ende = this.replayArchive(jahr, this.spiel.archiv.get(jahr));
				
				if (ende)
					break;
			}
			
			this.spiel.updatePlanetenlisteDisplay(false, this.spiel.isSimple());
			this.spiel.updateSpielfeldDisplay();
 		}
 		
 		private boolean replayArchive(int jahr, Archiv archiv)
 		{
 			boolean ende = false;
 			
 			this.spiel.console.clear();
			this.spiel.console.setHeaderText(
					SternResources.ReplayWiedergabeJahr(true,
							Integer.toString(jahr+1)),
					Colors.INDEX_NEUTRAL);
			
			ArrayList<ScreenDisplayContent> replay = archiv.getReplay();
			
			for (ScreenDisplayContent cont: replay)
			{
				this.spiel.spielThread.updateDisplay(cont);
				
				if (cont.getPause())
					this.spiel.pause(Constants.PAUSE_MILLISECS);
				else
					ende = this.spiel.console.waitForTasteReplay();
				
				if (ende)
					break;
			}
			
 			return ende;
 		}
 	}
  	
  	// ==============
  	
  	protected class Spezialmenue
 	{
 		private Spiel spiel;
 		
 		public void setSpiel(Spiel spiel)
 		{
 			// Nur für Inventur benutzt
 			this.spiel = spiel;
 		}
 		public Spezialmenue()
 		{
 			// Konstruktor nur für Inventur benutzt
 		}
 		
 		public Spezialmenue(Spiel spiel)
 		{
 			this.spiel = spiel;
 			boolean simpel = this.spiel.isSimple();
 			
 			do
 			{
 				this.spiel.console.clear();
 				
 				this.spiel.updateSpielfeldDisplay();
 				this.spiel.updatePlanetenlisteDisplay(false, simpel);
 				
 				this.spiel.console.setHeaderText(
 						this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true), Colors.INDEX_NEUTRAL);
 				
 				ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 				
 				keys.add(new ConsoleKey("ESC", SternResources.Hauptmenue(true)));
 				keys.add(new ConsoleKey("0",SternResources.SpielinformationenPlanet(true)));
 				
 				keys.add(new ConsoleKey("1",SternResources.SpielinformationenEnergieproduktion(true)));
 				if (!simpel && this.spiel.optionen.contains(SpielOptionen.AUSSERIRDISCHE))
 					keys.add(new ConsoleKey("2",SternResources.SpielinformationenAusserirdische(true)));
 				
 				if (!simpel && this.spiel.optionen.contains(SpielOptionen.FESTUNGEN))
 					keys.add(new ConsoleKey("3", SternResources.SpielinformationenFestungen(true)));
 				if (!simpel)
 					keys.add(new ConsoleKey("4",SternResources.SpielinformationenMinenfelder(true)));
 				if (!simpel)
 					keys.add(new ConsoleKey("5",SternResources.SpielinformationenPatrouillen(true)));
 				if (!simpel && (this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN) ||
 						this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH)))
 					keys.add(new ConsoleKey("6", SternResources.SpielinformationenKommandozentralen(true)));
 				if (!simpel)
 					keys.add(new ConsoleKey("7",SternResources.SpielinformationenBuendnisse(true)));
 				if (!simpel)
 					keys.add(new ConsoleKey("8",SternResources.SpielinformationenNeutralePlaneten(true)));
 				 				
 				ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, false);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.clear();
 					break;
 				}
 				
 				String inputString = input.getInputText().toUpperCase();
 				
 				if (inputString.equals("1"))
 					this.energieprod();
 				else if (!simpel && inputString.equals("4"))
 					this.minenfelder();
 				else if (!simpel && inputString.equals("7"))
 					this.buendnisse();
 				else if (!simpel && inputString.equals("3") && this.spiel.optionen.contains(SpielOptionen.FESTUNGEN))
 					this.festungen();
 				else if (!simpel && inputString.equals("5"))
 					this.patrouillen();
 				else if (!simpel && inputString.equals("2") && this.spiel.optionen.contains(SpielOptionen.AUSSERIRDISCHE))
 					this.ausserirdische();
 				else if (!simpel && inputString.equals("6") && 
 						( this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN) ||
 								this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH)))
 					this.kommandozentralen();
 				else if (!simpel && inputString.equals("8"))
 					this.neutralePlaneten();
 				else if (inputString.equals("0"))
 					this.editor();
 				else
 					this.spiel.console.outUngueltigeEingabe();
 				
 			} while (true);
 		}
 		
 		private void energieprod()
 		{
 			this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenEnergieproduktionTitel(true), Colors.INDEX_NEUTRAL);
 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = new ArrayList<SpielfeldPlanetDisplayContent>(this.spiel.anzPl);
 			
 			// Zeichne die Planeten
 			for (int index = 0; index < this.spiel.anzPl; index++)
 			{
 				int plIndex = getPlanetenSortiert()[index];
 				
 				byte col = this.spiel.planeten[plIndex].isNeutral() ?
 						Colors.INDEX_NEUTRAL :
 						this.spiel.spieler[this.spiel.planeten[plIndex].getBes()].getColIndex();
 				
 				boolean invers = (this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH) && 
 								  this.spiel.planeten[plIndex].istKommandozentrale());
 				
 				plData.add(new SpielfeldPlanetDisplayContent(
 							Integer.toString(this.spiel.planeten[plIndex].getEprod()),
 							this.spiel.planeten[plIndex].getPos(),
 							col,
 							invers,
 							null)); 					
 			}
 			 			
 			if (this.spiel.screenDisplayContent == null)
 				this.spiel.screenDisplayContent = new ScreenDisplayContent();
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null,
 					null,
 					null));
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			this.spiel.console.waitForTaste();
 		}
 		
 		private void neutralePlaneten()
 		{
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenNeutralePlanetenTitel(true), Colors.INDEX_NEUTRAL);
 			
 			this.spiel.updatePlanetenlisteDisplay(false, true);

 			this.spiel.console.waitForTaste();
 		}

 		
 		private void minenfelder()
 		{
 			this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenMinenfelderTitel(true), Colors.INDEX_NEUTRAL);
 			
 			// Minenfelder
 			ArrayList<MinenfeldDisplayContent> minen = new ArrayList<MinenfeldDisplayContent>();
 			
 			for (Mine mine: this.spiel.minen.values())
 			{
 				ArrayList<Byte> spielerMinen = new ArrayList<Byte>();
 				BitSet spieler = new BitSet(Constants.ANZAHL_SPIELER_MAX);
 				
 				if (mine.getHistorie() != null)
 				{
	 				for (MineHistorie hist: mine.getHistorie())
	 				{
	 					if (hist.getSp() != Constants.BESITZER_NEUTRAL)
	 						spieler.set(hist.getSp());
	 				}
	 				
	 				for (int sp = 0; sp < this.spiel.anzSp; sp++)
	 					if (spieler.get(sp))
	 						spielerMinen.add(this.spiel.spieler[sp].getColIndex());
 				}
 				
 				minen.add(new MinenfeldDisplayContent(mine.getPos(), mine.getStaerke(), spielerMinen));
 			}
 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(null, new HashSet<Integer>());
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null,
 					null,
 					minen));
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			this.spiel.console.waitForTaste();
 		}
 		
 		private void buendnisse()
 		{
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenBuendnisseTitel(true), Colors.INDEX_NEUTRAL);
 			
 			// Buendnisse
 			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int index = 0; index < this.spiel.anzPl; index++)
 			{
 				int plIndex = getPlanetenSortiert()[index];
 				
 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				
 				for (int sp = 0; sp < this.spiel.anzSp; sp++)
 				{
 					if (this.spiel.planeten[plIndex].getBes() != sp && this.spiel.planeten[plIndex].istBuendnisMitglied(sp))
 						frameCols.add(this.spiel.spieler[sp].getColIndex());
 				}
 						
 				if (frameCols.size() > 0)
 				{
 					frames.put(plIndex, frameCols);
 					brighterPlanets.add(plIndex);
 				}
 			}
 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(frames, brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null,
 					null,
 					null));
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);
 			
 			if (brighterPlanets.size() == 0)
 			{
 				this.spiel.console.appendText(
 						SternResources.SpielinformationenKeinePlanetenMitBuendnissen(true));
 				this.spiel.console.waitForTaste();
 				return;
 			}

 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 			
 			do
 			{
 				this.spiel.console.appendText(
 						SternResources.SpielinformationenBuendnisPlanet(true) + ": ");
 				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, false, true);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.outAbbruch();
 					break;
 				}
 				
 				int plIndex = this.spiel.getPlanetenIndexFromName(input.getInputText().toUpperCase());
 				
 				if (plIndex < 0 || plIndex >= this.spiel.anzPl)
 				{
 					this.spiel.console.outUngueltigeEingabe();
 					continue;
 				}
 				
 				if (this.spiel.planeten[plIndex].istBuendnis())
 				{
 					this.spiel.console.setLineColor(this.spiel.spieler[this.spiel.planeten[plIndex].getBes()].getColIndex());
 					this.spiel.console.appendText(
 							SternResources.SpielinformationenBuendnisstruktur(true, this.spiel.getPlanetenNameFromIndex(plIndex)) +
 							":");
 					this.spiel.console.lineBreak();
 					this.spiel.console.setLineColor(Colors.INDEX_WEISS);
						
					this.spiel.outBuendnisStruktur(plIndex);
 				}
 				else
 					this.spiel.console.appendText(
 							SternResources.SpielinformationenKeinBuendnis(true, this.spiel.getPlanetenNameFromIndex(plIndex)));
 				
 				this.spiel.console.lineBreak();
 			} while (true);
 		}
 		
 		private void festungen()
 		{
 			this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenFestungenTitel(true), Colors.INDEX_NEUTRAL);
 			
 			// Festungen
 			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int index = 0; index < this.spiel.anzPl; index++)
 			{
 				int plIndex = getPlanetenSortiert()[index];
 				
 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				
 				for (int i = 0; i < this.spiel.planeten[plIndex].getFestungFaktor(); i++)
 					frameCols.add(Colors.INDEX_WEISS);
 						
 				if (frameCols.size() > 0)
 				{
 					frames.put(plIndex, frameCols);
 					brighterPlanets.add(plIndex);
 				}
 			}
 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(frames, brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null,
 					null,
 					null));
 			
 			// Intakt
 			ArrayList<String> text = new ArrayList<String>();
 			ArrayList<Byte> textCol = new ArrayList<Byte>();
 			
 			for (int sp = Constants.BESITZER_NEUTRAL; sp < this.spiel.anzSp; sp++)
 			{
 				boolean ersteZeile = true;
 				
 				for (int index = 0; index < this.spiel.anzPl; index++)
 				{
 					int plIndex = getPlanetenSortiert()[index];
 					
 					if (this.spiel.planeten[plIndex].getBes() != sp ||
 						this.spiel.planeten[plIndex].getFestungFaktor() == 0)
 						continue;
 					
 					String spName = (sp == Constants.BESITZER_NEUTRAL) ?
 										SternResources.Neutral(false) :
 										this.spiel.spieler[sp].getName();
 										
 					byte colIndex = (sp == Constants.BESITZER_NEUTRAL) ?
 										(byte)Colors.INDEX_NEUTRAL :
 										this.spiel.spieler[sp].getColIndex();
 					
 					if (ersteZeile)
 					{
 						text.add(spName);
 						textCol.add(colIndex);
 						
 						ersteZeile = false;
 					}
 					
 					String plName = " " + this.spiel.getPlanetenNameFromIndex(plIndex);
 					String anzRaum = "     " + this.spiel.planeten[plIndex].getFestungIntakt()+"%";
 					text.add(plName.substring(plName.length()-2, plName.length()) + 
 							":" +
 							anzRaum.substring(anzRaum.length()-5, anzRaum.length()));
 					
 					textCol.add(colIndex);
 				}
 			}
 			
 			this.spiel.screenDisplayContent.setPlaneten(
 					new PlanetenlisteDisplayContent(text, textCol));
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			this.spiel.console.waitForTaste();
 		}
 		
 		private void kommandozentralen()
 		{
 			this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> " + SternResources.SpielinformationenKommandozentralenTitel(true), Colors.INDEX_NEUTRAL);
 			
 			// Kommandozentralen auf Planeten
 			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			Hashtable<Integer, ArrayList<Byte>> frames = new Hashtable<Integer, ArrayList<Byte>>();
 			
 			for (int plIndex = 0; plIndex < this.spiel.anzPl; plIndex++)
 			{
 				if (!this.spiel.planeten[plIndex].istKommandozentrale())
 					continue;
 				
 				Kommandozentrale kz = this.spiel.planeten[plIndex].getKommandozentrale();

 				ArrayList<Byte> frameCols = new ArrayList<Byte>();
 				frameCols.add(this.spiel.spieler[kz.getSp()].getColIndex());
 				
				frames.put(plIndex, frameCols);
				brighterPlanets.add(plIndex);
 			}
 			
 			// Kommandozentralen, die unterwegs sind
 			ArrayList<Flugobjekt> zentralen = new ArrayList<Flugobjekt>();
 			
 			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();

 			for (Flugobjekt obj: this.spiel.objekte)
 			{
 				if (obj.getTyp() != ObjektTyp.TRANSPORTER || obj.getKz() == null)
 					continue;
 				
 				zentralen.add(obj);
 				
 				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
 	 					obj.getStart(), obj.getZiel(), obj.getExactPos(), 
 	 					this.spiel.spieler[obj.getKz().getSp()].getColIndex());
 				
 				lines.add(line);
 				
 				if (obj.getSpl() != Constants.KEIN_PLANET)
 					brighterPlanets.add(obj.getSpl());
 				
 				brighterPlanets.add(obj.getZpl());
 			}
 			
 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(frames, brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					lines,
 					null,
 					null));
 			
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			if (zentralen.size() == 0)
 			{
 				// Keine Kommandozentralen in Transportern unterwges
 				this.spiel.console.waitForTaste();
 				return;
 			}
 			
 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 			keys.add(new ConsoleKey(SternResources.Taste(true), SternResources.Weiter(true)));
 			keys.add(new ConsoleKey("ESC", SternResources.Abbrechen(true)));
 			
 			for (Flugobjekt zentrale: zentralen)
 			{
 				int flugdauerRestjahr = zentrale.getAnkunftsjahr();
 				Kommandozentrale kz = zentrale.getKz();
 				
 				String spieler = this.spiel.spieler[kz.getSp()].getName();
 				
 				this.spiel.console.setLineColor(this.spiel.spieler[kz.getSp()].getColIndex());
 				
 				this.spiel.console.appendText(
 						SternResources.SpielinformationenKommandozentralenUnterwegs(true,
 								spieler,
 								this.spiel.getPlanetenNameFromIndex(zentrale.getZpl()),
 								Integer.toString((this.spiel.jahr + flugdauerRestjahr + 1))) + " ");
 				
 				ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, false);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 					break;
 			}
 		}


 		private void patrouillen()
 		{
 			this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPatrouillenTitel(true), Colors.INDEX_NEUTRAL);
 			// Patrouillen
 			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();

 			for (Flugobjekt obj: this.spiel.objekte)
 			{
 				if (obj.getTyp() != ObjektTyp.PATROUILLE || obj.isTransfer())
 					continue;
 				
 				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
 	 					obj.getStart(), obj.getZiel(), obj.getExactPos(), this.spiel.spieler[obj.getBes()].getColIndex());
 				
 				lines.add(line);
 				
 				brighterPlanets.add(obj.getSpl());
 				brighterPlanets.add(obj.getZpl());
 			}
 			 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(null, brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					lines,
 					null,
 					null));
 			
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			this.spiel.console.waitForTaste();
 		}
 		
 		private void ausserirdische()
 		{
 			this.spiel.console.setHeaderText(
 					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenAusserirdischeTitel(true), Colors.INDEX_NEUTRAL);

 			ArrayList<Flugobjekt> aliens = new ArrayList<Flugobjekt>();
 			
 			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();

 			for (Flugobjekt obj: this.spiel.objekte)
 			{
 				if (obj.getTyp() != ObjektTyp.RAUMER || obj.getBes() != Constants.BESITZER_NEUTRAL)
 					continue;
 				
 				aliens.add(obj);
 				
 				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
 	 					obj.getStart(), obj.getZiel(), obj.getExactPos(), Colors.INDEX_WEISS);
 				
 				lines.add(line);
 				
 				brighterPlanets.add(obj.getZpl());
 			}
 			 			
 			ArrayList<SpielfeldPlanetDisplayContent> plData = this.standardSpielfeld(null, brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					lines,
 					null,
 					null));
 			
 			
 			this.spiel.spielThread.updateDisplay(this.spiel.screenDisplayContent);

 			if (aliens.size() == 0)
 			{
 				this.spiel.console.appendText(
 						SternResources.SpielinformationenKeineAusserirdische(true));
 				this.spiel.console.waitForTaste();
 				return;
 			}
 			
 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 			keys.add(new ConsoleKey(SternResources.Taste(true), SternResources.Weiter(true)));
 			keys.add(new ConsoleKey("ESC", SternResources.Abbrechen(true)));
 			
 			for (Flugobjekt alien: aliens)
 			{
 				int flugdauerRestjahr = alien.getAnkunftsjahr();
 				
 				this.spiel.console.appendText(
 						SternResources.SpielinformationenAusserirdischeUnterwegs(true,
 								Integer.toString(alien.getAnz()), 
 								this.spiel.getPlanetenNameFromIndex(alien.getZpl()), 
 								Integer.toString(this.spiel.jahr + flugdauerRestjahr + 1)) + " ");
 				
 				ConsoleInput input = this.spiel.console.waitForKeyPressed(keys, false);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 					break;
 			}
 		}
 		
 		private void editor()
 		{
 			ArrayList<ConsoleKey> keys = new ArrayList<ConsoleKey>();
 			
 			do
 			{
 				this.spiel.console.setHeaderText(this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPlanetTitel(true), Colors.INDEX_NEUTRAL);
 				this.spiel.console.appendText(SternResources.SpielinformationenPlanet(true)+": ");
 				ConsoleInput input = this.spiel.console.waitForTextEntered(Constants.PLANETEN_NAME_MAX_LAENGE, keys, false, true);
 				
 				if (input.getLastKeyCode() == KeyEvent.VK_ESCAPE)
 				{
 					this.spiel.console.outAbbruch();
 					break;
 				}
 				
 				int plIndex = this.spiel.getPlanetenIndexFromName(input.getInputText().toUpperCase());
 				
 				if (plIndex < 0 || plIndex >= this.spiel.anzPl)
 				{
 					this.spiel.console.outUngueltigeEingabe();
 					continue;
 				}
 				
 				Planet pl = this.spiel.planeten[plIndex];
 				
 				byte col = (this.spiel.planeten[plIndex].getBes() == Constants.BESITZER_NEUTRAL) ?
 						Colors.INDEX_NEUTRAL :
 							this.spiel.spieler[pl.getBes()].getColIndex();
 				
 				this.spiel.console.setHeaderText(
 						this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Spielinformationen(true)+" -> "+SternResources.SpielinformationenPlanetTitel(true)+" " + this.spiel.getPlanetenNameFromIndex(plIndex), col);
 				
 				if (this.spiel.optionen.contains(SpielOptionen.SIMPEL))
 				{
 					spiel.console.appendText(
 							SternResources.InventurRaumerproduktionJahr(true)+": " + this.spiel.planeten[plIndex].getEraum() + " " + SternResources.PlEditEe(true));
 					
 					spiel.console.lineBreak();
 				}
 				else
 				{ 				
	 				new Planeteneditor(
	 						this.spiel,
	 						plIndex,
	 						null,
	 						true,
	 						true);
 				}
 				
 			} while (true);
 		}

 		public ArrayList<SpielfeldPlanetDisplayContent> standardSpielfeld(Hashtable<Integer, ArrayList<Byte>> frames, HashSet<Integer> brighterPlanets)
 		{
 			ArrayList<SpielfeldPlanetDisplayContent> plData = new ArrayList<SpielfeldPlanetDisplayContent>(this.spiel.anzPl);
 			
 			// Zeichne die Planeten
 			for (int plIndex = 0; plIndex < this.spiel.anzPl; plIndex++)
 			{
 				ArrayList<Byte> frameCols = null;
 				
 				if (frames != null && frames.containsKey(plIndex))
 					frameCols = frames.get(plIndex);
 				
 				byte col = (this.spiel.planeten[plIndex].isNeutral()) ? 
 						Colors.INDEX_NEUTRAL :
 						this.spiel.spieler[this.spiel.planeten[plIndex].getBes()].getColIndex();

 				if (brighterPlanets != null && !brighterPlanets.contains(plIndex))
 				{
 					col = Colors.getColorIndexDarker(col);
 					if (frameCols != null)
 					{
 						ArrayList<Byte> frameColsDarker = new ArrayList<Byte>(frameCols.size());
 						for (Byte fc: frameCols)
 							frameColsDarker.add(Colors.getColorIndexDarker(fc));
 						frameCols = frameColsDarker;
 					}
 				}
 				
 				boolean invers = (this.spiel.optionen.contains(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH) && 
						  this.spiel.planeten[plIndex].istKommandozentrale());
 				
 				plData.add(new SpielfeldPlanetDisplayContent(
							this.spiel.getPlanetenNameFromIndex(plIndex),
							this.spiel.planeten[plIndex].getPos(),
							col,
							invers,
							frameCols)); 				
 					
 			}
 			
 			if (this.spiel.screenDisplayContent == null)
 				this.spiel.screenDisplayContent = new ScreenDisplayContent();
 			
 			return plData;
 		}
 	}
  	
  	// ================
  	
  	protected class Inventur
  	{
  		private Spiel spiel;
  		private int spieler;
  		private InventurPdfDaten pdfDaten;
  		private Spezialmenue sm;
  		private ScreenDisplayContent sdcBefore;
  		private boolean simple;
  		
  		public Inventur(Spiel spiel, int spieler)
  		{
  			this.spiel = spiel;
  			this.spieler = spieler;
  			this.simple = this.spiel.optionen.contains(SpielOptionen.SIMPEL);
  			
  			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> Zugeingabe " + this.spiel.spieler[this.spieler].getName() + " -> "+SternResources.InventurTitel(true),
					this.spiel.spieler[this.spieler].getColIndex());
  			
  			this.spiel.console.clear();

  			// Abfrage Mail oder Anzeige im Client oder abbrechen?
			this.spiel.console.appendText(SternResources.PdfOeffnenFrage(true) + " ");
			
			ConsoleInput input = this.spiel.console.waitForKeyPressedYesNo(false);
			
			if (input.getInputText().equals(Console.KEY_YES))
			{
				// Auf dem Client oeffnen
				byte[] pdfBytes = this.create(input.getLanguageCode());
				boolean success = false;
				
				if (input.getClientId() == null)
					success = PdfLauncher.showPdf(pdfBytes);
				else
					success = spiel.spielThread.openPdf(pdfBytes, input.getClientId());
				
				if (success)
					this.spiel.console.appendText(SternResources.InventurPdfGeoeffnet(true));
				else
					this.spiel.console.appendText(SternResources.InventurPdfFehler(true));
				
				this.spiel.console.lineBreak();
			}
			else
			{
				this.spiel.console.outAbbruch();
			}
  		}
  		
  		private byte[] create(String languageCode)
  		{
  			// Bildschirminhalt sichern
  			this.sdcBefore = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
  			
  			// Sprachcode umstellen
  			String languageCodeServer = SternResources.getLocale();
  			SternResources.setLocale(languageCode);

  			// Inventur aufbauen
  			this.sm = new Spezialmenue();
  			this.sm.setSpiel(this.spiel);
  			
  			this.pdfDaten = new InventurPdfDaten(
  					this.spiel.spieler[this.spieler].getName(),
  					this.spiel.jahr,
  					this.spiel.maxJahre,
  					this.spiel.archiv.get(this.spiel.jahr).getPunkte()[this.spieler],
  					false);
  			
  			this.planeten();
  			this.flugobjekte();
  			
  			if (!this.simple)
  				this.patrouillen();
  			
  			// Bildschirminhalt wiederherstellen
  			this.spiel.screenDisplayContent = sdcBefore;
  			
  			byte[] pdfByteArray = null;			
 			try
 			{
 				pdfByteArray = InventurPdf.create(pdfDaten);
 			}
 			catch (Exception e)
 			{
 			}
 			
 			// Sprachcode wiederherstellen
 			SternResources.setLocale(languageCodeServer);
 			
 			return pdfByteArray;
  		}
  		
  		private void flugobjekte()
  		{
  			InventurPdfChapter chapter = 
  					new InventurPdfChapter(
  							this.simple ? SternResources.InventurRaumer(false) : SternResources.InventurFlugobjekte(false),
  							this.simple ?
  									SternResources.InventurKeineRaumer(false) :
  									SternResources.InventurKeineFlugobjekte(false));
  			
  			// ---
  			// Alle Objekte sammeln und dann nach Ankunftsjahr sortieren
  			// Nicht sammeln: Patrouillen im Einsatz
  			ArrayList<Integer> restflugzeitenTemp = new ArrayList<Integer>();
  			ArrayList<Flugobjekt> objekte = new ArrayList<Flugobjekt>();
  			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();
  			
  			for (Flugobjekt obj: spiel.objekte)
  			{
  				if (obj.istZuLoeschen())
  					continue;
  				
  				if (!obj.istBeteiligt(spieler))
  				{
  					// Fremde Objekte als Punkte hinzufuegen (nur bei Simple-Stern)
  					if (spiel.optionen.contains(SpielOptionen.SIMPEL))
  					{
		  				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
			 	 					null, null, obj.getExactPos(), Colors.INDEX_WEISS);
		 				
		 				lines.add(line);
  					}
	 				continue;
  				}
  				
  				if (obj.getTyp() == ObjektTyp.PATROUILLE && !obj.isTransfer())
  				{
  					continue;
  				}

  				objekte.add(obj);
  				
  				// Restflugzeit
  				restflugzeitenTemp.add(obj.getRestflugzeit());
  			}
  			
  			this.spiel.screenDisplayContent = new ScreenDisplayContent();
			chapter.table = new InventurPdfTable(this.simple? 4 : 8);
			HashSet<Integer> brighterPlanets = new HashSet<Integer>();
 			ArrayList<Point2D.Double> markedFields = new ArrayList<Point2D.Double>();
			
			// Spaltenueberschriften
 			if (this.simple)
 			{
 				chapter.table.cells.add(SternResources.InventurAnzahl(false));
  				chapter.table.colAlignRight[0] = true;
  				
  				chapter.table.cells.add(SternResources.InventurStart(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurZiel(false));
  				chapter.table.colAlignRight[2] = false;
  				
  				chapter.table.cells.add(SternResources.InventurAnkunft(false));
  				chapter.table.colAlignRight[3] = true;
 			}
 			else
 			{
  				chapter.table.cells.add(SternResources.InventurAnzahl(false));
  				chapter.table.colAlignRight[0] = true;
  				
  				chapter.table.cells.add(SternResources.InventurTyp(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurKommandant(false));
  				chapter.table.colAlignRight[2] = false;
  				
  				chapter.table.cells.add(SternResources.InventurStart(false));
  				chapter.table.colAlignRight[3] = false;
  				
  				chapter.table.cells.add(SternResources.InventurZiel(false));
  				chapter.table.colAlignRight[4] = false;
  				
  				chapter.table.cells.add(SternResources.InventurFracht(false));
  				chapter.table.colAlignRight[5] = false;
  				
  				chapter.table.cells.add(SternResources.InventurAnkunft(false));
  				chapter.table.colAlignRight[6] = true;
  				
  				chapter.table.cells.add(SternResources.InventurBuendnis(false));
  				chapter.table.colAlignRight[7] = false;
 			}
			
			// Objekte nach Ankunftzeit sortieren
			int[] restflugzeiten = new int[restflugzeitenTemp.size()];
			for (int i = 0; i < restflugzeiten.length; i++)
				restflugzeiten[i] = restflugzeitenTemp.get(i).intValue();
			
			int[] seq = Utils.listeSortieren(restflugzeiten, false);
			
			for (int i = 0; i < restflugzeiten.length; i++)
			{
				Flugobjekt obj = objekte.get(seq[i]);
				
				// Zur Grafik hinzufuegen
				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
 	 					obj.getStart(), obj.getZiel(), obj.getExactPos(), this.spiel.spieler[obj.getBes()].getColIndex());
 				
 				lines.add(line);
 				
 				if (this.spiel.getPlanetIndexFromPoint(obj.getStart()) != Constants.KEIN_PLANET)
 					brighterPlanets.add(obj.getSpl());
 				else
 					markedFields.add(Utils.toPoint2D(obj.getStart()));
 				
 				if (!obj.isStop())
 				{
  	 				if (this.spiel.getPlanetIndexFromPoint(obj.getZiel()) != Constants.KEIN_PLANET)
  	 					brighterPlanets.add(obj.getZpl());
  	 				else
  	 					markedFields.add(Utils.toPoint2D(obj.getZiel()));
 				}
 				
 				// Zur Tabelle hinzufuegen
 				chapter.table.cells.add(
 						obj.getTyp() == ObjektTyp.RAUMER ?
 								Integer.toString(obj.getAnz()) :
 								"1");
 				
 				if (!this.simple)
 				{
  	 				switch (obj.getTyp())
  	 				{
  	 				case RAUMER:
  	 					chapter.table.cells.add(SternResources.Raumer(false));
  	 					break;
  	 				case AUFKLAERER:
  	 					chapter.table.cells.add(SternResources.InventurAufklaerer(false));
  	 					break;
  	 				case PATROUILLE:
  	 					chapter.table.cells.add(SternResources.InventurPatrouilleTransfer(false));
  	 					break;
  	 				case TRANSPORTER:
  	 					chapter.table.cells.add(SternResources.InventurTransporter(false));
  	 					break;
  	 				case MINE50:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger50(false));
  	 					break;
  	 				case MINE100:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger100(false));
  	 					break;
  	 				case MINE250:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger250(false));
  	 					break;
  	 				case MINE500:
  	 					chapter.table.cells.add(SternResources.InventurMinenleger500(false));
  	 					break;
  	 				case MINENRAEUMER:
  	 					if (obj.isTransfer())
  	 						chapter.table.cells.add(SternResources.InventurMinenraeumerTransfer(false));
  	 					else
  	 						chapter.table.cells.add(SternResources.InventurMinenraeumer(false));
  	 					break;
					default:
						chapter.table.cells.add("");
						break;
  	 				}
  	 				
  	 				chapter.table.cells.add(this.spiel.spieler[obj.getBes()].getName());
 				}
 				
 				chapter.table.cells.add(Utils.padString(" " + spiel.getFeldNameFromPoint(obj.getStart()), 2));
 				
 				if (obj.isStop())
 					chapter.table.cells.add("??");
 				else
 					chapter.table.cells.add(Utils.padString(" " + spiel.getFeldNameFromPoint(obj.getZiel()), 2));
 				
 				if (!this.simple)
 				{
  	 				if (obj.getTyp() == ObjektTyp.TRANSPORTER)
  	 				{
  	 					if (obj.getKz() != null)
  	 						chapter.table.cells.add(SternResources.Kommandozentrale(false));
  	 					else
  	 						chapter.table.cells.add(
  	 								SternResources.InventurTransporterEe(
  	 										false, 
  	 										Integer.toString(obj.getAnz())));
  	 				}
  	 				else
  	 					chapter.table.cells.add("");
 				}
 				
 				// Ankunftsjahr
 				chapter.table.cells.add(
 						SternResources.InventurAnkunftJahr(
 								false, 
 								Integer.toString(this.spiel.jahr + 1 + restflugzeitenTemp.get(seq[i])).toString()));
 				
 				if (!this.simple)
 				{
  	 				if (obj.getTyp() == ObjektTyp.RAUMER && obj.istBuendnis())
  	 				{
  	 					// Buendnisse
 						StringBuilder sb = new StringBuilder(this.spiel.spieler[obj.getBes()].getName() + ": "+
 											obj.getRaumerProSpieler(obj.getBes()));

 						for (int sp = 0; sp < spiel.anzSp; sp++)
 						{
 							if (sp == obj.getBes() || obj.getRaumerProSpieler(sp) <= 0)
 								continue;

 							sb.append("\n" + this.spiel.spieler[sp].getName() + ": "+
 											obj.getRaumerProSpieler(sp));
 						}
 						
 						chapter.table.cells.add(sb.toString());
  	 				}
  	 				else
  	 					chapter.table.cells.add("");
 				}
			}
			
			ArrayList<SpielfeldPlanetDisplayContent> plData = this.sm.standardSpielfeld(this.planetenBuendnisse(), brighterPlanets);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					markedFields,
 					lines,
 					(spiel.loch != null && spiel.loch.isActive()) ?
 							spiel.loch.getPos() : null,
 					null));
 			
 			chapter.sdc = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
 			chapter.sdc.setPlaneten(this.sdcBefore.getPlaneten());
 			
  			this.pdfDaten.chapters.add(chapter);
  		}
  		
  		private void patrouillen()
  		{
  			InventurPdfChapter chapter = 
  					new InventurPdfChapter(
  							SternResources.InventurPatrrouillenTitel(false),
  							SternResources.InventurKeinePatrouillen(false));
  			boolean found = false;
  			
  			HashSet<Integer> brighterPlanets = null;
  			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();;
  			Hashtable<String, Integer> patPerLine = null;
  			
  			for (Flugobjekt obj: spiel.objekte)
  			{
  				if (obj.istZuLoeschen()) 
  					continue;
  				
  				if (!obj.istBeteiligt(this.spieler ))
  				{
  					// Fremde Objekte als Punkte hinzufuegen (nur Simple-Stern)
  					if (spiel.optionen.contains(SpielOptionen.SIMPEL))
  					{
		  				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
			 	 					null, null, obj.getExactPos(), Colors.INDEX_WEISS);
		 				
		 				lines.add(line);
  					}
	 				continue;
  				}
  				
  				if (!(obj.getTyp() == ObjektTyp.PATROUILLE && !obj.isTransfer()))
  					continue;
  				
  				if (!found)
  				{
  					found = true;
  					
  					this.spiel.screenDisplayContent = new ScreenDisplayContent();
  	  				
  	  				brighterPlanets = new HashSet<Integer>();
  	  	 			patPerLine = new Hashtable<String, Integer>();  
  				}
  				
  				brighterPlanets.add(obj.getSpl());
  				brighterPlanets.add(obj.getZpl());
  				
				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
 	 					obj.getStart(), obj.getZiel(), obj.getExactPos(), this.spiel.spieler[obj.getBes()].getColIndex());
 				
 				lines.add(line);
 				
 				String plNameStart = Utils.padString("  " + spiel.getPlanetenNameFromIndex(obj.getSpl()), 2);
 				String plNameZiel = Utils.padString("  " + spiel.getPlanetenNameFromIndex(obj.getZpl()), 2);
 				
 				String lineConcat = obj.getSpl() < obj.getZpl() ?
 							plNameStart + plNameZiel :
 							plNameZiel + plNameStart;
 							
 				if (patPerLine.containsKey(lineConcat))
 					patPerLine.replace(lineConcat, 
 							1 + patPerLine.get(lineConcat));
 				else
 					patPerLine.put(lineConcat, 1);
  			}
  			
  			if (patPerLine != null)
  			{
  				chapter.table = new InventurPdfTable(3);
  				
  				// Spaltenueberschriften
  				chapter.table.cells.add(SternResources.InventurPlanet1(false));
  				chapter.table.colAlignRight[0] = false;
  				
  				chapter.table.cells.add(SternResources.InventurPlanet2(false));
  				chapter.table.colAlignRight[1] = false;
  				
  				chapter.table.cells.add(SternResources.InventurPatrouillen(false));
  				chapter.table.colAlignRight[2] = true;
  				
  				for (String key: patPerLine.keySet())
  				{
  					chapter.table.cells.add(key.substring(0, 2));
  					chapter.table.cells.add(key.substring(2));
  					chapter.table.cells.add(patPerLine.get(key).toString());
  				}
	 			
	  			ArrayList<SpielfeldPlanetDisplayContent> plData = this.sm.standardSpielfeld(null, brighterPlanets);
		 			
	 			this.spiel.screenDisplayContent.setSpielfeld(
	 					new SpielfeldDisplayContent(plData,
	 					null,
	 					lines,
	 					(spiel.loch != null && spiel.loch.isActive()) ?
	 							spiel.loch.getPos() : null,
	 					null));
	 			
	 			chapter.sdc = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
	 			chapter.sdc.setPlaneten(this.sdcBefore.getPlaneten());
  			}
 			
  			this.pdfDaten.chapters.add(chapter);
  		}

  		private void planeten()
  		{
  			InventurPdfChapter chapter = 
  					new InventurPdfChapter(
  							simple ? SternResources.InventurPlanetenTitelSimpel(false) : 
  								SternResources.InventurPlanetenTitel(false),
  							SternResources.InventurKeinePlaneten(false));
  			
  			this.spiel.screenDisplayContent = new ScreenDisplayContent();
  			
  			chapter.table = new InventurPdfTable(simple ? 3 : 18);
				
			// Spaltenueberschriften
  			if (this.simple)
  			{
  				chapter.table.cells.add(SternResources.InventurPlanet(false));
				chapter.table.colAlignRight[0] = false;
				
				chapter.table.cells.add(SternResources.InventurRaumerproduktionJahr(false));
				chapter.table.colAlignRight[1] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumer(false));
				chapter.table.colAlignRight[2] = true;
  			}
  			else
  			{
	  			chapter.table.cells.add(SternResources.InventurPlanetKurz(false));
				chapter.table.colAlignRight[0] = false;
				
				chapter.table.cells.add(SternResources.InventurBesitzerKurz(false));
				chapter.table.colAlignRight[1] = false;
				
				chapter.table.cells.add(SternResources.InventurEnergievorratKurz(false));
				chapter.table.colAlignRight[2] = true;
				
				chapter.table.cells.add(SternResources.InventurEnergieproduktionKurz(false));
				chapter.table.colAlignRight[3] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumerproduktionKurz(false));
				chapter.table.colAlignRight[4] = true;
				
				chapter.table.cells.add(SternResources.InventurFestungKurz(false));
				chapter.table.colAlignRight[5] = true;
				
				chapter.table.cells.add(SternResources.InventurFestungZustandKurz(false));
				chapter.table.colAlignRight[6] = true;
				
				chapter.table.cells.add(SternResources.InventurRaumerKurz(false));
				chapter.table.colAlignRight[7] = true;
				
				chapter.table.cells.add(SternResources.InventurAufklaererKurz(false));
				chapter.table.colAlignRight[8] = true;
				
				chapter.table.cells.add(SternResources.InventurTransporterKurz(false));
				chapter.table.colAlignRight[9] = true;
				
				chapter.table.cells.add(SternResources.InventurPatrouilleKurz(false));
				chapter.table.colAlignRight[10] = true;
				
				chapter.table.cells.add(SternResources.InventurMinenraeumerKurz(false));
				chapter.table.colAlignRight[11] = true;
				
				chapter.table.cells.add(SternResources.InventurMine50Kurz(false));
				chapter.table.colAlignRight[12] = true;
				
				chapter.table.cells.add(SternResources.InventurMine100Kurz(false));
				chapter.table.colAlignRight[13] = true;
				
				chapter.table.cells.add(SternResources.InventurMine250Kurz(false));
				chapter.table.colAlignRight[14] = true;
				
				chapter.table.cells.add(SternResources.InventurMine500Kurz(false));
				chapter.table.colAlignRight[15] = true;
				
				chapter.table.cells.add(SternResources.InventurKommandozentraleKurz(false));
				chapter.table.colAlignRight[16] = true;
				
				chapter.table.cells.add(SternResources.InventurBuendnisKurz(false));
				chapter.table.colAlignRight[17] = false;
  			}
  			  			
  			for (int index = 0; index < spiel.anzPl; index++)
  			{
  				int plIndex = spiel.getPlanetenSortiert()[index];
  				
  				if (spiel.planeten[plIndex].getBes() != spieler &&
  					!spiel.planeten[plIndex].istBuendnisMitglied(spieler) &&
  					!spiel.planeten[plIndex].hatSender(spieler, spiel.jahr))
  					continue;
  				
  				boolean istBes = (spiel.planeten[plIndex].getBes() == spieler ||
  								  spiel.planeten[plIndex].hatSender(spieler, spiel.jahr));
  				Planet pl = spiel.planeten[plIndex];
  				
  				chapter.table.cells.add(Utils.padString(" " + spiel.getPlanetenNameFromIndex(plIndex), 2));
  				
  				if (!simple)
  				{
  					chapter.table.cells.add(pl.getBes() == Constants.BESITZER_NEUTRAL ?
  	  						"" :
  	  						spiel.spieler[pl.getBes()].getName());
  					
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getEvorrat()) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getEprod()) : "?");
  				}
  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getEraum()) : "?");
  				
  				if (!simple)
  				{
  					chapter.table.cells.add(istBes ? Utils.numToString(pl.getFestungFaktor()) : "?");
  					chapter.table.cells.add(istBes ? Utils.numToString(pl.getFestungIntakt()) : "?");
  				}
  				chapter.table.cells.add(Utils.numToString(pl.getAnz(ObjektTyp.RAUMER)));
  				
  				if (!simple)
  				{
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.AUFKLAERER)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.TRANSPORTER)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.PATROUILLE)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.MINENRAEUMER)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.MINE50)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.MINE100)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.MINE250)) : "?");
	  				chapter.table.cells.add(istBes ? Utils.numToString(pl.getAnz(ObjektTyp.MINE500)) : "?");
	  				
	  				if (istBes)
		  				chapter.table.cells.add((pl.getKommandozentrale() == null ?
		  						"" : "X"));
	  				else
	  					chapter.table.cells.add("?");
	
					// Buendnisse
					if (pl.istBuendnis())
					{
						StringBuilder sb = new StringBuilder(this.spiel.spieler[pl.getBes()].getName() + ": "+
											pl.getRaumerProSpieler(pl.getBes()));
	
						for (int sp = 0; sp < spiel.anzSp; sp++)
						{
							if (sp == pl.getBes() || !pl.istBuendnisMitglied(sp))
								continue;
	
							sb.append("\n" + this.spiel.spieler[sp].getName() + ": "+
											pl.getRaumerProSpieler(sp));
						}
						
						chapter.table.cells.add(sb.toString());
					}
					else
						chapter.table.cells.add("");
  				}
  			}
  			
  			// Flugobjekte zeichnen
  			ArrayList<SpielfeldLineDisplayContent> lines = new ArrayList<SpielfeldLineDisplayContent>();

  			for (Flugobjekt obj: spiel.objekte)
  			{
  				if (obj.istZuLoeschen() || obj.getNeu() || obj.getTyp() == ObjektTyp.SCHWARZES_LOCH)
  					continue;
  				
  				SpielfeldLineDisplayContent line = new SpielfeldLineDisplayContent(
  	 					null, null, obj.getExactPos(), Colors.INDEX_WEISS);
  				
  				lines.add(line);
  			}

			ArrayList<SpielfeldPlanetDisplayContent> plData = this.sm.standardSpielfeld(this.planetenBuendnisse(), null);
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null, // lines,
 					(spiel.loch != null && spiel.loch.isActive()) ?
 							spiel.loch.getPos() : null,
 					this.minen()));
 			
 			chapter.sdc = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
 			chapter.sdc.setPlaneten(this.sdcBefore.getPlaneten());
  			this.pdfDaten.chapters.add(chapter);
  		}
  		
  	  	private ArrayList<MinenfeldDisplayContent> minen()
  	  	{
  	  		ArrayList<MinenfeldDisplayContent> minen = new ArrayList<MinenfeldDisplayContent>();
  	  		
			if (spiel.minen != null)
			{
  	  			for (Mine mine: spiel.minen.values())
  	  			{
  	  				Mine mineClon = mine.getSpielerInfo(spieler);
  	  				if (mineClon == null)
  	  					continue;
  	  				
  	  				if (mineClon.getStaerke() > 0)
  	  					minen.add(new MinenfeldDisplayContent(
  	  							mine.getPos(), 
  	  							mineClon.getStaerke(), 
  	  							new ArrayList<Byte>()));
  				}
			}
  	  		
  	  		return minen;
  	  	}

  	  	private Hashtable<Integer, ArrayList<Byte>> planetenBuendnisse()
  	  	{
  	  		Hashtable<Integer, ArrayList<Byte>> buendnisFrames = new Hashtable<Integer, ArrayList<Byte>>();
  	  		
  	  		for (int plIndex = 0; plIndex < this.spiel.anzPl; plIndex++)
  	  		{
  	  			Planet pl = this.spiel.planeten[plIndex];
  	  			if (!pl.istBuendnis() || !pl.istBuendnisMitglied(this.spieler))
  	  				continue;
  	  			
  	  			ArrayList<Byte> frameColors = new ArrayList<Byte>(); 
  	  			for (int sp = 0; sp < this.spiel.anzSp; sp++)
  	  				if (sp != pl.getBes() && pl.istBuendnisMitglied(sp))
  	  					frameColors.add(this.spiel.spieler[sp].getColIndex());
  	  			
  	  			buendnisFrames.put(plIndex, frameColors);
  	  		}
			return buendnisFrames;
  	  	}
  	}
  	  	
  	// =========================================
  	
  	protected class Entfernungstabelle
  	{
  		private Spiel spiel;
  		private InventurPdfDaten pdfDaten;
  		private Spezialmenue sm;
  		private ScreenDisplayContent sdcBefore;
  		
  		public Entfernungstabelle(Spiel spiel)
  		{
  			this.spiel = spiel;
  			
  			this.spiel.console.setHeaderText(
					this.spiel.hauptmenueHeaderGetJahrText() + " -> "+SternResources.Hauptmenue(true)+" -> "+SternResources.Entfernungstabelle(true),
					Colors.INDEX_NEUTRAL);

  			this.spiel.console.appendText(SternResources.PdfOeffnenFrage(true) + " ");
  			
			ConsoleInput input = this.spiel.console.waitForKeyPressedYesNo(false);
			
			if (input.getInputText().equals(Console.KEY_YES))
			{
				// Auf dem Client oeffnen
				byte[] pdfBytes = this.create(input.getLanguageCode());
				boolean success = false;
				
				if (input.getClientId() == null)
					success = PdfLauncher.showPdf(pdfBytes);
				else
					success = spiel.spielThread.openPdf(pdfBytes, input.getClientId());
				
				if (success)
					this.spiel.console.appendText(SternResources.InventurPdfGeoeffnet(true));
				else
					this.spiel.console.appendText(SternResources.InventurPdfFehler(true));
				
				this.spiel.console.lineBreak();
			}
			else
			{
				this.spiel.console.outAbbruch();
			}
  		}
  		
  		private byte[] create(String languageCode)
  		{
  			// Bildschirminhalt sichern
  			this.sdcBefore = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
  			
  			// Sprachcode umstellen
  			String languageCodeServer = SternResources.getLocale();
  			SternResources.setLocale(languageCode);
  			
  			// Inventur aufbauen
  			this.sm = new Spezialmenue();
  			this.sm.setSpiel(this.spiel);
  			
  			this.pdfDaten = new InventurPdfDaten(
  					"",
  					this.spiel.jahr,
  					this.spiel.maxJahre,
  					0,
  					true);
  			
  			this.daten();
  			
  			// Bildschirminhalt wiederherstellen
  			this.spiel.screenDisplayContent = sdcBefore;
  			
  			byte[] pdfByteArray = null;			
 			try
 			{
 				pdfByteArray = InventurPdf.create(pdfDaten);
 			}
 			catch (Exception e)
 			{
 			}
 			
 			// Sprachcode wiederherstellen
 			SternResources.setLocale(languageCodeServer);
 			
 			return pdfByteArray;
  		}
  		
  		private void daten()
  		{
  			InventurPdfChapter chapter = 
  					new InventurPdfChapter(SternResources.Entfernungstabelle(false), "");
  			
  			this.spiel.screenDisplayContent = new ScreenDisplayContent();
  			
  			chapter.table = new InventurPdfTable(this.spiel.anzPl + 1);
  			chapter.table.highlightFirstCol = true;
  			
			// Spaltenueberschriften
  			chapter.table.cells.add("");
			chapter.table.colAlignRight[0] = true;
			
  			for (int pl = 0; pl < this.spiel.anzPl; pl++)
  			{
  				int plIndex = spiel.getPlanetenSortiert()[pl];
  				chapter.table.cells.add(Utils.padString(" "+this.spiel.getPlanetenNameFromIndex(plIndex), 2));
  				chapter.table.colAlignRight[pl+1] = true;
  			}
  			
  			for (int plVon = 0; plVon < this.spiel.anzPl; plVon++)
  			{
  				chapter.table.cells.add(Utils.padString(" "+this.spiel.getPlanetenNameFromIndex(plVon), 2));
  				
  				for (int plNach = 0; plNach < this.spiel.anzPl; plNach++)
  				{
  					chapter.table.cells.add(
  							plVon != plNach ?
  									Integer.toString(
  											this.spiel.getDistanzMatrix()[plVon][plNach]) :
  										"");
  				}
  			}
  			
			ArrayList<SpielfeldPlanetDisplayContent> plData = this.getSpielfeld();
 			
 			this.spiel.screenDisplayContent.setSpielfeld(
 					new SpielfeldDisplayContent(plData,
 					null,
 					null,
 					null,
 					null));
 			
 			chapter.sdc = (ScreenDisplayContent)Utils.klon(this.spiel.screenDisplayContent);
 			chapter.sdc.setModus(ScreenDisplayContent.MODUS_ENTFERUNGSTABELLE);
  			this.pdfDaten.chapters.add(chapter);
  		}
  		
  		private ArrayList<SpielfeldPlanetDisplayContent> getSpielfeld()
  		{
  			ArrayList<SpielfeldPlanetDisplayContent> plData = new ArrayList<SpielfeldPlanetDisplayContent>(this.spiel.anzPl);
 			
 			// Zeichne die Planeten
 			for (int plIndex = 0; plIndex < this.spiel.anzPl; plIndex++)
 			{
 				plData.add(new SpielfeldPlanetDisplayContent(
							this.spiel.getPlanetenNameFromIndex(plIndex),
							this.spiel.planeten[plIndex].getPos(),
							Colors.INDEX_SCHWARZ,
							false,
							null)); 				
 					
 			}
 			
 			if (this.spiel.screenDisplayContent == null)
 				this.spiel.screenDisplayContent = new ScreenDisplayContent();
 			
 			return plData;
  		}
  		
  	}
  	  	
  	// =========================================
  	
  	protected static class KI
  	{
  		public static Vector<KiZug> calc (Spiel spielKopie, int sp)
  		{
  			Vector<KiZug> alleZuege = new Vector<KiZug>();
  			
			// Entfernungstabelle berechnen
  			int[][] dist = spielKopie.getDistanzMatrix();
  			
  			// Berechne Asugangslage
  			int score[] = new int [spielKopie.anzPl];

  			score = calcAusgangslage(spielKopie, sp, dist);
  			
  			// Ermittle Spielzuege
  			Vector<KiZug> kiZuege;
  			
  			int plStart, plZiel, momDist, anzRaumerGes = 0, minRaumer = 0;
  			short anzRaumer = 0;
  			double	angrZuschlag;
  			
  			// Sortiere Score absteigend. Damit stehen die attraktivsten und bedrohtesten Planeten
  			// ganz oben.
  			int seq[] = Utils.listeSortieren(score, true);
  			
  			// Arbeite die sortierte Zuege ab
  			for (int t = 0; t < seq.length; t++)
  			{
  				plZiel = seq[t];
  				Planet planetZiel = spielKopie.planeten[plZiel];
  				
  				if (score[plZiel] < 0)
  					continue;
  						
  				// Finde naechstgelegene eigenen Startplanetn, der nicht bedroht ist
  				int distPl[] = Utils.listeSortieren(dist[plZiel], false);
  				
  				momDist = -1;
  				anzRaumerGes = 0;
  				
  				kiZuege = new Vector<KiZug>();
  				
  				angrZuschlag = 1. + (double)Utils.random(Constants.KI_FAKTOR_ANGRIFF_MAX_RND) / 100.;
  				
  				for (int tt = 0; tt < dist.length; tt++)
  				{
  					plStart = distPl[tt];
  					Planet planetStart = spielKopie.planeten[plStart];
  					
  					if (plStart == plZiel || planetStart.getBes() != sp || score[plStart] > 0)
  						continue;
  					
  					if (momDist >= 0 && dist[plZiel][plStart] != momDist)
  					{
  						kiZuege = new Vector<KiZug>();
  						anzRaumerGes = 0;
  					}
  					
  					momDist = dist[plZiel][plStart];
  					anzRaumer = 0;
  					minRaumer = 999999999;
  					
  					// Mindestraumerzahl fuer Planeten bestimmen
  					if (planetZiel.getBes() == sp)
  						minRaumer = score[plZiel];
  					else
  					{		
  						if (planetZiel.getBes() != Constants.BESITZER_NEUTRAL) // kein neutraler Planet
  							minRaumer = Utils.round(angrZuschlag * ((double)(planetZiel.getAnz(ObjektTyp.RAUMER)+planetZiel.getEraum()*(momDist+1))) * Constants.KI_FAKTOR_ANGRIFF);
  						else
  							minRaumer = Utils.round(angrZuschlag * (Constants.KI_MIN_RAUMER_NEUTR_PL+(double)planetZiel.getEraum()*(spielKopie.jahr+momDist+1) * Constants.KI_FAKTOR_ANGRIFF));
  					}

  					if (score[plStart] < 0)
  					{
  						anzRaumer = (short)Math.min(planetStart.getAnz(ObjektTyp.RAUMER), -Utils.round(((double)score[plStart] / Constants.KI_BEDROHUNG_ERHOEHUNG)));
  						anzRaumer = (short)Math.min(anzRaumer, minRaumer);
  					}

  					if (anzRaumer > 0)
  					{
  						KiZug kiZug = new KiZug();
  						
  						kiZug.startPl = plStart;
  						kiZug.zielPl  = plZiel;
  						kiZug.anz = anzRaumer;
  						
  						kiZuege.add(kiZug);
  						
  						anzRaumerGes += anzRaumer;
  					}
  					
  					if (anzRaumerGes >= minRaumer)
  						break;
  				}
  				
  				if (anzRaumerGes >= minRaumer && kiZuege.size() > 0)
  				{
  					// Raumer in der Spielkopie starten und Score von Start- und Zielplanet anpassen 
  					// Die Raumer werden erst in der Zugeingabe tatsaechlich gestartet
  					for (KiZug kiZug: kiZuege)
  		  			{
  						alleZuege.add(kiZug);
  						
  						Flugobjekt obj = new Flugobjekt(
  								kiZug.startPl,
  								kiZug.zielPl,
  								spielKopie.planeten[kiZug.startPl].getPos(),
  								spielKopie.planeten[kiZug.zielPl].getPos(),
  								ObjektTyp.RAUMER,
  								kiZug.anz,
  								sp,
  								false,
  								true,
  								null,
  								null);
  						
  						spielKopie.objekte.add(obj);
  						spielKopie.planeten[kiZug.startPl].subtractRaumer(spielKopie.anzSp, kiZug.anz, sp, false);
  						
  		  				// score anpassen
  		  				score[kiZug.startPl] += Utils.round((double)kiZug.anz * Constants.KI_BEDROHUNG_ERHOEHUNG);
  		  				score[kiZug.zielPl]  -= kiZug.anz;
  		  			}
  				}
  			} // Naechster Zielplanet

  			return alleZuege;
  		}
  		
  		private static int[] calcAusgangslage(Spiel spielKopie, int sp, int[][] dist)
  		{
  			// Bereche Ausgangslage fuer Spieler sp.
  			int scoreAusgang[] = new int[spielKopie.anzPl];
  			
  			int		jahre = 0;
  			
  			double 	valDist = 0.,
  					valRaumer = 0.;
  			
  			for (int plZiel = 0; plZiel < spielKopie.anzPl; plZiel++)
  			{
  				// -----------------------------------------------------------------------------------
  				// Berechne Bedrohung eines Planeten plZiel bzw. Chancen auf den Planeten plZiel durch
  				// Raumer vom Planeten plStart
  				// -----------------------------------------------------------------------------------
  				valRaumer = 0.;
  				int diff = 0;
  				
  				Planet planetZiel = spielKopie.planeten[plZiel];
  				
  				for (int plStart = 0; plStart < spielKopie.anzPl; plStart++)
  				{
  					Planet planetStart = spielKopie.planeten[plStart];
  					if (planetZiel.getBes() == planetStart.getBes() || planetStart.getBes() == Constants.BESITZER_NEUTRAL)
  						continue;
  					
  					// Distanzfaktor. Je weiter der Planet entfernt ist, desto geringer
  					// die Bedrohung bzw. desto hoeher die Chance
  					jahre = dist[plStart][plZiel] + 1;
  					valDist = (double)Constants.KI_FAKTOR_DISTANZ/(Math.pow((double)jahre, 2.));
  					
  					if (planetZiel.getBes() != Constants.BESITZER_NEUTRAL)
  						diff = planetStart.getAnz(ObjektTyp.RAUMER) - planetZiel.getAnz(ObjektTyp.RAUMER); 
  					else
  						diff = planetStart.getAnz(ObjektTyp.RAUMER) - (Constants.KI_MIN_RAUMER_NEUTR_PL + spielKopie.jahr * planetZiel.getEraum());

  					valRaumer += (double)diff  * valDist;

  				} // Next plStart
  							
  				// Sind bereits Raumer mit meiner Beteiligung zum Planeten unterwegs?
  				for (Flugobjekt obj: spielKopie.objekte)
  				{
  					if (obj.getBes() == sp && 
  						obj.getTyp() == ObjektTyp.RAUMER &&
  						obj.getZpl() == plZiel)
  					{
  						if (planetZiel.getBes() != Constants.BESITZER_NEUTRAL)
  							valRaumer -= obj.getAnz();
  						else
  							valRaumer = -9999;
  					}
  				}
  				
  				scoreAusgang[plZiel] += Utils.round(valRaumer);
  				
  				// --------------------
  				// Verteidigungsraumer
  				// --------------------
  				if (planetZiel.getBes() == sp)
 					// Eigener Planet. Verteidungsraumer sind bekannt
  					scoreAusgang[plZiel] -= planetZiel.getAnz(ObjektTyp.RAUMER);
  				else
  				{
  					// Fremder Planet
  					if (planetZiel.getBes() != Constants.BESITZER_NEUTRAL)
  						scoreAusgang[plZiel] -= (planetZiel.getAnz(ObjektTyp.RAUMER) + Constants.FESTUNG_STAERKE_MIN + Constants.FESTUNG_STAERKE_SPANNE / 2);
  				}
  			}
  			
  			return scoreAusgang;
  		}  		
  	}
  	
  	public void setEnableParameterChange(boolean enabled)
  	{
  		this.enableParameterChange = enabled;
  		this.spielThread.checkMenueEnabled();
  	}
  	
  	public boolean isParameterChangeEnabled()
  	{
  		return this.enableParameterChange;
  	}
  	
  	public int spielzuegeEinfuegen(SpielzuegeEmailTransport set)
  	{
		// Von welchem Spieler sind die Zuege?
		int spIndex = -1;
		
		for (int i = 0; i < this.anzSp; i++)
		{
			if (this.spielzuegeReferenzCodes[i].equals(set.getSpielzugReferenzCode()))
			{
				spIndex = i;
				
				this.spielzuege.put(spIndex, set.getSpielzuege());
				break;
			}
		}
				
		return spIndex;

  	}
  	
  	@SuppressWarnings("unchecked")
	public boolean starteAuswertungServer()
  	{
  		boolean auswertungAusgefuehrt = true;
  		
  		// Haben alle Spieler ihre Spielzuege eingegeben?
  		for (int spIndex = 0; spIndex < this.anzSp; spIndex++)
  		{
  			Spieler spieler = this.spieler[spIndex];
  			
  			if (!spieler.istComputer() && !this.spielzuege.containsKey(spIndex))
  			{
  				auswertungAusgefuehrt = false;
  				break;
  			}
  		}
  		
  		if (auswertungAusgefuehrt)
  		{
  			// Computer geben ihre Zuege ein
  			Planet[] planetenKopie = (Planet[])Utils.klon(this.planeten);
			ArrayList<Flugobjekt> objekteKopie = (ArrayList<Flugobjekt>)Utils.klon(this.objekte);
  			
			for (int spIndex = 0; spIndex < this.anzSp; spIndex++)
			{
				Spieler sp = this.spieler[spIndex];
				
				if (!this.spielzuege.containsKey(spIndex))
				{
					if (sp.istComputer())
					{
						Spiel.zugeingabeKi(this, spIndex);

		 				this.planeten = (Planet[])Utils.klon(planetenKopie);
		 				this.objekte = (ArrayList<Flugobjekt>)Utils.klon(objekteKopie);
						continue;
					}
				}
			}

	  		// Auswertung im Hintergrund, d.h. ohne notwendige Tastendruecke oder
	  		// Zeitverzoegerungen oder Autosave ablaufen lassen
  			this.console = new Console(this, true);
  			
			this.updateSpielfeldDisplay();
			this.updatePlanetenlisteDisplay(false, this.isSimple());
  			
	  		new Auswertung(this);
	  		
	  		this.abschliessenPruefung(true);
  		}
  		
  		return auswertungAusgefuehrt;
  	}
  	
  	public static void zugeingabeKi(Spiel spiel, int spieler)
  	{
  		Vector<KiZug> zuege = KI.calc(spiel, spieler);
			
		ArrayList<Spielzug> sz = new ArrayList<Spielzug>();
		spiel.spielzuege.put(spieler, sz);
			
		for (KiZug kiZug: zuege)
		{
			Flugobjekt obj = new Flugobjekt(
					kiZug.startPl,
					kiZug.zielPl,
					spiel.planeten[kiZug.startPl].getPos(),
					spiel.planeten[kiZug.zielPl].getPos(),
					ObjektTyp.RAUMER,
					kiZug.anz,
					spieler,
					false,
					true,
					null,
					null);
			
			spiel.planeten[kiZug.startPl].subtractRaumer(spiel.anzSp, kiZug.anz, spieler, false);
			
			spiel.spielzuege.get(spieler).add(
					new Spielzug(
							kiZug.startPl,
							obj,
							null));
		}
  	}
  	
  	public void setSpielerEmailAdresse(String userId, String emailAdresse)
  	{
  		int spIndex = this.getSpielerIndexByName(userId);
  		
  		if (spIndex >= 0)
  		{
  			this.spieler[spIndex].setEmailAdresse(emailAdresse);
  		}
  	}
  	
  	public int getSpielerIndexByName(String userId)
  	{
  		int spIndex = 1;
  		
  		for (int i = 0; i < this.anzSp; i++)
  		{
  			if (this.spieler[i].getName().equals(userId))
  			{
  				spIndex = i;
  				
  				break;
  			}
  		}
  		
  		return spIndex;
  	}
  	
  	public void spielerEntfernen(String userId)
  	{
  		// Achtung: Methode darf nur vom Server aufgerufen werden, wenn ein User
  		// vom Server geloescht werden soll
  		
  		// Spielerindex ermitteln
  		int spIndex = this.getSpielerIndexByName(userId);
  		
  		// Name ersetzen
		this.spieler[spIndex].setName(Constants.GELOESCHTER_SPIELER);
		
		// E-Mail loeschen
		this.spieler[spIndex].setEmailAdresse("");
  		  		
  		if (!this.abgeschlossen)
  		{
  			// Hat der Spieler schon Zugeingaben gemacht?
  			// Wenn ja, Zugeingaben loeschen und durch ein einziges Kommando "Kapitulation" ersetzen
  			
  			// Alle Flugobjekte, die dem geloeschten Spieler gehoeren und ein "StopLabel" haben,
  			// also vom Spieler gekapert wurden, entfernen
  			for (int i = this.objekte.size() - 1; i >= 0; i--)
  			{
  				Flugobjekt obj = this.objekte.get(i);
  				
  				if (obj.getBes() == spIndex && obj.getStopLabel() != null)
  					this.objekte.remove(i);
  			}
  			
  			// Ein "Kapitulations-Flugobjekt" starten
			Flugobjekt obj = new Flugobjekt(
					0,
					0,
					null,
					null,
					ObjektTyp.KAPITULATION,
					1,
					spIndex,
					false,
					true,
					null,
					null); 				

  			ArrayList<Spielzug> spielzuegeNeu = new ArrayList<Spielzug>();
  	
			spielzuegeNeu.add(
					new Spielzug(
							0,
							obj,
							null));

			this.spielzuege.put(spIndex, spielzuegeNeu);
  			
  			this.starteAuswertungServer();
  		}
  	}
}