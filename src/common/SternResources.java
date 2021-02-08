package common;

import java.util.Hashtable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
   * Diese Klasse wurde mit dem Resource Bundle Utility aus der Ressourcen-Datei
   *
   *   SternResources_de_DE
   *
   * erzeugt. Die Ressourcen-Datei wird mit dem Eclipse-Plugin ResourceBundle Editor gepflegt.
   */
public class SternResources
{
	private static Hashtable<String,String> symbolDict;;
	private static String languageCode;
	private static ResourceBundle messages;

	static {
		setLocale("de-DE");
		symbolDict = new Hashtable<String,String>();
		fillSymbolDict();
	}

	public static void setLocale(String newLanguageCode){
		languageCode = newLanguageCode;
		String[] language = languageCode.split("-");
		Locale currentLocale = new Locale(language[0], language[1]);
		messages = ResourceBundle.getBundle("SternResources", currentLocale);
	}

	public static String getLocale(){
		return languageCode;
	}

	private static void fillSymbolDict() {
		// Hoechstes vergebenes Symbol: IN
		symbolDict.put("00","Abbrechen_00");
		symbolDict.put("01","AbschlussEprod_01");
		symbolDict.put("02","AbschlussPlatz_02");
		symbolDict.put("03","AktionAbgebrochen_03");
		symbolDict.put("04","Aktualisieren_04");
		symbolDict.put("05","AufklaererPlural_05");
		symbolDict.put("06","Auswaehlen_06");
		symbolDict.put("07","AuswertungAngriffAngreiferFestung_07");
		symbolDict.put("08","AuswertungAngriffAngriffAufPlanet_08");
		symbolDict.put("09","AuswertungAngriffAngriffGescheitert_09");
		symbolDict.put("0A","InventurFlugobjekte_0A");
		symbolDict.put("0B","InventurFracht_0B");
		symbolDict.put("0C","InventurJahr1_0C");
		symbolDict.put("0D","InventurJahr2_0D");
		symbolDict.put("0E","InventurKeineFlugobjekte_0E");
		symbolDict.put("0F","InventurKeinePatrouillen_0F");
		symbolDict.put("0G","InventurKeinePlaneten_0G");
		symbolDict.put("0H","InventurKeineRaumer_0H");
		symbolDict.put("0I","InventurKommandant_0I");
		symbolDict.put("0J","InventurKommandozentraleKurz_0J");
		symbolDict.put("0K","InventurMine100Kurz_0K");
		symbolDict.put("0L","InventurMine250Kurz_0L");
		symbolDict.put("0M","InventurMine500Kurz_0M");
		symbolDict.put("0N","InventurMine50Kurz_0N");
		symbolDict.put("0O","InventurMinenleger100_0O");
		symbolDict.put("0P","InventurMinenleger250_0P");
		symbolDict.put("0Q","InventurMinenleger50_0Q");
		symbolDict.put("0R","InventurMinenleger500_0R");
		symbolDict.put("0S","InventurMinenraeumer_0S");
		symbolDict.put("0T","InventurPatrouilleKurz_0T");
		symbolDict.put("0U","InventurPatrouilleTransfer_0U");
		symbolDict.put("0V","InventurPatrouillen_0V");
		symbolDict.put("0W","InventurPatrrouillenTitel_0W");
		symbolDict.put("0X","InventurPdfFehler_0X");
		symbolDict.put("0Y","InventurPdfGeoeffnet_0Y");
		symbolDict.put("0Z","InventurPlanet_0Z");
		symbolDict.put("13","AuswertungAngriffAngreiferPlanet_13");
		symbolDict.put("15","AuswertungAngriffSpielerErobert_15");
		symbolDict.put("17","AuswertungAufklaererAngekommen_17");
		symbolDict.put("19","AuswertungBeginnt_19");
		symbolDict.put("1A","InventurPlanet1_1A");
		symbolDict.put("1B","InventurPlanet2_1B");
		symbolDict.put("1C","InventurPlanetKurz_1C");
		symbolDict.put("1D","InventurMinenraeumerKurz_1D");
		symbolDict.put("1E","InventurPlanetenTitel_1E");
		symbolDict.put("1F","InventurPlanetenTitelSimpel_1F");
		symbolDict.put("1G","InventurPunkte_1G");
		symbolDict.put("1H","InventurRaumer_1H");
		symbolDict.put("1I","InventurRaumerKurz_1I");
		symbolDict.put("1J","InventurRaumerproduktionJahr_1J");
		symbolDict.put("1K","InventurRaumerproduktionKurz_1K");
		symbolDict.put("1L","InventurSeite_1L");
		symbolDict.put("1M","InventurStart_1M");
		symbolDict.put("1N","InventurTitel_1N");
		symbolDict.put("1O","InventurTransporter_1O");
		symbolDict.put("1P","InventurTransporterEe_1P");
		symbolDict.put("1Q","InventurTransporterKurz_1Q");
		symbolDict.put("1R","InventurTyp_1R");
		symbolDict.put("1S","InventurZiel_1S");
		symbolDict.put("1T","Ja_1T");
		symbolDict.put("1U","Kommandozentrale_1U");
		symbolDict.put("1V","Links_1V");
		symbolDict.put("1W","Loeschen_1W");
		symbolDict.put("1X","MenuBestenliste_1X");
		symbolDict.put("1Z","MenuDatei_1Z");
		symbolDict.put("20","AuswertungBuendnisNichtGeaendert_20");
		symbolDict.put("21","AuswertungEnde_21");
		symbolDict.put("22","AuswertungKommandozentraleErobertNeutral_22");
		symbolDict.put("23","AuswertungKommandozentraleErobertSpieler_23");
		symbolDict.put("24","AuswertungMineGelegt_24");
		symbolDict.put("25","AuswertungMinenfeldGeraeumt_25");
		symbolDict.put("27","AuswertungMinenlegerAngekommen_27");
		symbolDict.put("28","AuswertungMinenlegerZerschellt_28");
		symbolDict.put("29","AuswertungMinenraeumerAngekommen_29");
		symbolDict.put("2A","MenuEinstellungen_2A");
		symbolDict.put("2B","MenuHilfe_2B");
		symbolDict.put("2C","MenuNeuesSpiel_2C");
		symbolDict.put("2D","MenuScreesharing_2D");
		symbolDict.put("2E","MenuServerAdmin_2E");
		symbolDict.put("2F","MenuServerCredentials_2F");
		symbolDict.put("2G","MenuServerbasierteSpiele_2G");
		symbolDict.put("2H","MenuSpielAusZwischenablageLaden_2H");
		symbolDict.put("2J","MenuSpielLaden_2J");
		symbolDict.put("2K","MenuSpielSpeichernAls_2K");
		symbolDict.put("2L","MenuSpielanleitung_2L");
		symbolDict.put("2M","MenuSternClientVerlassen_2M");
		symbolDict.put("2N","MenuSternVerlassen_2N");
		symbolDict.put("2O","MenuUeberStern_2O");
		symbolDict.put("2P","MenuVerbindungseinstellungen_2P");
		symbolDict.put("2Q","MinBuild_2Q");
		symbolDict.put("2R","Mine100Plural_2R");
		symbolDict.put("2S","Mine250Plural_2S");
		symbolDict.put("2T","Mine500Plural_2T");
		symbolDict.put("2U","Mine50Plural_2U");
		symbolDict.put("2V","MinenraeumerPlural_2V");
		symbolDict.put("2W","MoechtestDuSternVerlassen_2W");
		symbolDict.put("2X","Nein_2X");
		symbolDict.put("2Y","Neutral_2Y");
		symbolDict.put("2Z","OK_2Z");
		symbolDict.put("30","AuswertungMinenraeumerZerschellt_30");
		symbolDict.put("31","AuswertungNachrichtAnAusSektor_31");
		symbolDict.put("33","AuswertungPatrouilleAngekommen_33");
		symbolDict.put("34","AuswertungPatrouilleAufklaererGekapert_34");
		symbolDict.put("36","AuswertungPatrouilleMinenlegerGekapert_36");
		symbolDict.put("37","AuswertungPatrouilleMinenraeumerGekapert_37");
		symbolDict.put("38","AuswertungPatrouillePatrouilleGekapert_38");
		symbolDict.put("39","AuswertungPatrouillePatrouilleZerstoert_39");
		symbolDict.put("3A","PatrouillePlural_3A");
		symbolDict.put("3B","PdfOeffnenFrage_3B");
		symbolDict.put("3C","PlEditEe_3C");
		symbolDict.put("3D","PlEditEeEnergievorrat_3D");
		symbolDict.put("3E","PlEditEprodPlus4_3E");
		symbolDict.put("3F","PlEditFestungRaumer_3F");
		symbolDict.put("3G","PlEditFestungen_3G");
		symbolDict.put("3H","PlEditKaufpreis_3H");
		symbolDict.put("3I","PlEditRaumerProd_3I");
		symbolDict.put("3J","PlEditVerkaufspreis_3J");
		symbolDict.put("3K","Planeten_3K");
		symbolDict.put("3L","PlaneteneditorAb_3L");
		symbolDict.put("3M","PlaneteneditorAuf_3M");
		symbolDict.put("3N","PlaneteneditorAuswahlAendern_3N");
		symbolDict.put("3O","PlaneteneditorKaufen_3O");
		symbolDict.put("3P","PlaneteneditorUebernehmen_3P");
		symbolDict.put("3Q","PlaneteneditorVerkaufen_3Q");
		symbolDict.put("3R","ProgramArgument_3R");
		symbolDict.put("3S","Punkte_3S");
		symbolDict.put("3T","Raumer_3T");
		symbolDict.put("3U","Rechts_3U");
		symbolDict.put("3V","ReplayAuswertungWiedergeben_3V");
		symbolDict.put("40","AuswertungPatrouilleRaumerGekapert_40");
		symbolDict.put("41","AuswertungPatrouilleRaumerGesichtet_41");
		symbolDict.put("42","AuswertungPatrouilleTransporterGekapert_42");
		symbolDict.put("43","AuswertungPatrouilleZerschellt_43");
		symbolDict.put("44","AuswertungRaumerAngekommen_44");
		symbolDict.put("45","AuswertungRaumerAufMineGelaufenZerstoert_45");
		symbolDict.put("46","AuswertungRaumerAufMineGelaufen_46");
		symbolDict.put("47","AuswertungRaumerNichtGestartet_47");
		symbolDict.put("48","AuswertungRaumerVertrieben_48");
		symbolDict.put("49","AuswertungRaumerVertrieben2_49");
		symbolDict.put("4A","ReplayWiedergabeJahr_4A");
		symbolDict.put("4B","Schliessen_4B");
		symbolDict.put("4C","ServerSettingsJDialogIpServer_4C");
		symbolDict.put("4D","ServerSettingsJDialogTerminalServerAktiv_4D");
		symbolDict.put("4E","ServerSettingsJDialogUnbekannt_4E");
		symbolDict.put("4F","ServerSettingsJDialogVerbundeneClients_4F");
		symbolDict.put("4G","SpielAbschliessen_4G");
		symbolDict.put("4H","SpielAbschliessenFrage_4H");
		symbolDict.put("4I","SpielLaden_4I");
		symbolDict.put("4J","SpielSpeichern_4J");
		symbolDict.put("4K","Spieler_4K");
		symbolDict.put("4L","SpielfeldOkFrage_4L");
		symbolDict.put("4M","Spielinformationen_4M");
		symbolDict.put("4Q","SpielinformationenBuendnisPlanet_4Q");
		symbolDict.put("4R","SpielinformationenBuendnisse_4R");
		symbolDict.put("4T","SpielinformationenBuendnisseTitel_4T");
		symbolDict.put("4U","SpielinformationenBuendnisstruktur_4U");
		symbolDict.put("4V","SpielinformationenEnergieproduktion_4V");
		symbolDict.put("4W","SpielinformationenEnergieproduktionTitel_4W");
		symbolDict.put("4X","SpielinformationenFestungen_4X");
		symbolDict.put("4Y","SpielinformationenFestungenTitel_4Y");
		symbolDict.put("4Z","SpielinformationenKeinBuendnis_4Z");
		symbolDict.put("57","AuswertungSpielerTot_57");
		symbolDict.put("58","AuswertungTransporterAngekommen_58");
		symbolDict.put("59","AuswertungTransporterZerschellt_59");
		symbolDict.put("5B","SpielinformationenKeinePlanetenMitBuendnissen_5B");
		symbolDict.put("5C","SpielinformationenKommandozentralen_5C");
		symbolDict.put("5D","SpielinformationenKommandozentralenTitel_5D");
		symbolDict.put("5E","SpielinformationenKommandozentralenUnterwegs_5E");
		symbolDict.put("5F","SpielinformationenMinenfelder_5F");
		symbolDict.put("5G","SpielinformationenMinenfelderTitel_5G");
		symbolDict.put("5H","SpielinformationenPatrouillen_5H");
		symbolDict.put("5I","SpielinformationenPatrouillenTitel_5I");
		symbolDict.put("5J","SpielinformationenPlanet_5J");
		symbolDict.put("5K","SpielinformationenPlanetTitel_5K");
		symbolDict.put("5L","Spielleiter_5L");
		symbolDict.put("5M","Spielparameter_5M");
		symbolDict.put("5O","SpielparameterJDialogAutoSave_5O");
		symbolDict.put("5P","SpielparameterJDialogBot_5P");
		symbolDict.put("5Q","SpielparameterJDialogEMailEinstellungen_5Q");
		symbolDict.put("5R","SpielparameterJDialogEmailModus_5R");
		symbolDict.put("5S","SpielparameterJDialogFarbe_5S");
		symbolDict.put("5T","SpielparameterJDialogNameZuLang_5T");
		symbolDict.put("5U","SpielparameterJDialogSimpelStern_5U");
		symbolDict.put("5V","SpielparameterJDialogSpieleBisJahr_5V");
		symbolDict.put("5W","SpielparameterJDialogSpielerEMail_5W");
		symbolDict.put("5X","SpielparameterJDialogSpielleiterEMail_5X");
		symbolDict.put("5Y","SpielparameterJDialogUnendlich_5Y");
		symbolDict.put("60","AuswertungWiederholen_60");
		symbolDict.put("61","Bestenliste_61");
		symbolDict.put("62","ClientSettingsJDialogClientNichtRegistriert_62");
		symbolDict.put("63","ClientSettingsJDialogKeineVerbindung_63");
		symbolDict.put("64","ClientSettingsJDialogMeinName_64");
		symbolDict.put("65","ClientSettingsJDialogNichtVerbunden_65");
		symbolDict.put("66","ClientSettingsJDialogServerNichtErreichbar_66");
		symbolDict.put("67","ClientSettingsJDialogTitel_67");
		symbolDict.put("68","ClientSettingsJDialogVerbinden_68");
		symbolDict.put("69","ClientSettingsJDialogVerbindungsstatus_69");
		symbolDict.put("6A","Statistik_6A");
		symbolDict.put("6C","StatistikJahrMinus_6C");
		symbolDict.put("6D","StatistikJahrPlus_6D");
		symbolDict.put("6E","StatistikMax_6E");
		symbolDict.put("6F","StatistikMin_6F");
		symbolDict.put("6G","StatistikSchliessen_6G");
		symbolDict.put("6H","StatistikSpielBegonnen_6H");
		symbolDict.put("6I","StatistikStunden_6I");
		symbolDict.put("6J","StatistikTitelEnergieproduktion_6J");
		symbolDict.put("6K","StatistikTitelPlaneten_6K");
		symbolDict.put("6L","StatistikTitelPunkte_6L");
		symbolDict.put("6M","StatistikTitelRaumer_6M");
		symbolDict.put("6N","SternClientTitel_6N");
		symbolDict.put("6P","SternClientVerlassenFrage_6P");
		symbolDict.put("6Q","SternTerminalServer_6Q");
		symbolDict.put("6R","SternThinClientVerlassen_6R");
		symbolDict.put("6S","SternTitel_6S");
		symbolDict.put("6T","SternVerlassen_6T");
		symbolDict.put("6U","Terminalserver_6U");
		symbolDict.put("6V","ThinClientCode_6V");
		symbolDict.put("6W","TransporterPlural_6W");
		symbolDict.put("6X","UngueltigeEingabe_6X");
		symbolDict.put("6Y","UnterschiedlicheBuilds_6Y");
		symbolDict.put("6Z","Weiter_6Z");
		symbolDict.put("70","ClientSettingsJDialogVerbunden_70");
		symbolDict.put("71","ClipboardImportJDIalogImportFehler_71");
		symbolDict.put("72","ClipboardImportJDIalogInhaltHierEinfuegen_72");
		symbolDict.put("73","ClipboardImportJDIalogTitle_73");
		symbolDict.put("74","DateiExistiertNicht_74");
		symbolDict.put("76","DateiNichtGueltig_76");
		symbolDict.put("77","EMailAdresse_77");
		symbolDict.put("78","Einfuegen_78");
		symbolDict.put("79","EingabeGesperrt_79");
		symbolDict.put("7A","Zugeingabe_7A");
		symbolDict.put("7B","ZugeingabeAktionNichtMoeglich_7B");
		symbolDict.put("7C","ZugeingabeAlleEe_7C");
		symbolDict.put("7D","ZugeingabeAlleRaumer_7D");
		symbolDict.put("7E","ZugeingabeAnkunft_7E");
		symbolDict.put("7F","ZugeingabeAnzahl_7F");
		symbolDict.put("7G","ZugeingabeAufFremdenPlanetenNurKuendigen_7G");
		symbolDict.put("7H","ZugeingabeAufklaerer_7H");
		symbolDict.put("7I","ZugeingabeBeenden_7I");
		symbolDict.put("7J","ZugeingabeBeendenFrage_7J");
		symbolDict.put("7K","ZugeingabeBuendRaumer_7K");
		symbolDict.put("7L","ZugeingabeBuendnis_7L");
		symbolDict.put("7M","ZugeingabeEMailAktionen_7M");
		symbolDict.put("7N","ZugeingabeEMailBody_7N");
		symbolDict.put("7O","ZugeingabeEMailEndlosschleife_7O");
		symbolDict.put("7P","ZugeingabeEMailBody2_7P");
		symbolDict.put("7Q","ZugeingabeEMailErzeugt_7Q");
		symbolDict.put("7R","ZugeingabeEMailErzeugt2_7R");
		symbolDict.put("7S","ZugeingabeEMailErzeugt3_7S");
		symbolDict.put("7T","ZugeingabeEMailErzeugt4_7T");
		symbolDict.put("7U","ZugeingabeFertig_7U");
		symbolDict.put("7V","ZugeingabeInfo_7V");
		symbolDict.put("7W","ZugeingabeInventur_7W");
		symbolDict.put("7X","ZugeingabeKeinBuendnis_7X");
		symbolDict.put("7Y","ZugeingabeKeinBuendnismitglied_7Y");
		symbolDict.put("7Z","ZugeingabeKeineKommandozentrale_7Z");
		symbolDict.put("80","EmailAdressenJDialogTitel_80");
		symbolDict.put("81","EmailSettingsJDialogTitel_81");
		symbolDict.put("82","Energieproduktion_82");
		symbolDict.put("83","Entfernungstabelle_83");
		symbolDict.put("84","Fehler_84");
		symbolDict.put("85","FehlerBeimLaden_85");
		symbolDict.put("86","Hauptmenue_86");
		symbolDict.put("87","HighscoreFrage_87");
		symbolDict.put("88","InventurAnkunft_88");
		symbolDict.put("89","InventurAnkunftJahr_89");
		symbolDict.put("8A","ZugeingabeKeineSpielzuege_8A");
		symbolDict.put("8B","ZugeingabeKommandozentraleVerlegen_8B");
		symbolDict.put("8C","ZugeingabeKuendigen_8C");
		symbolDict.put("8D","ZugeingabeMaxAnzahlRaumer_8D");
		symbolDict.put("8E","ZugeingabeMine_8E");
		symbolDict.put("8F","ZugeingabeMine250_8F");
		symbolDict.put("8G","ZugeingabeMine50_8G");
		symbolDict.put("8H","ZugeingabeMine500_8H");
		symbolDict.put("8I","ZugeingabeMineTypFrage_8I");
		symbolDict.put("8J","ZugeingabeMineZielsektor_8J");
		symbolDict.put("8K","ZugeingabeMinenraeumer_8K");
		symbolDict.put("8L","ZugeingabeMissionTransferFrage_8L");
		symbolDict.put("8M","ZugeingabeMomentaneBuendnisstruktur_8M");
		symbolDict.put("8N","ZugeingabeNeueBuendnisstruktur_8N");
		symbolDict.put("8O","ZugeingabeNichtGenugRaumer_8O");
		symbolDict.put("8P","ZugeingabePatrouille_8P");
		symbolDict.put("8R","ZugeingabePatrouilleTransfer_8R");
		symbolDict.put("8S","ZugeingabePlanet_8S");
		symbolDict.put("8T","ZugeingabePlanetGehoertNicht_8T");
		symbolDict.put("8U","ZugeingabePlaneteninfo_8U");
		symbolDict.put("8V","ZugeingabeRaumer_8V");
		symbolDict.put("8W","ZugeingabeSpielstandVerschicken_8W");
		symbolDict.put("8X","ZugeingabeSpielzuegeFalscheRunde_8X");
		symbolDict.put("8Y","ZugeingabeSpielzuegeImportieren_8Y");
		symbolDict.put("8Z","ZugeingabeSpielzuegeImportiert_8Z");
		symbolDict.put("90","InventurAnzahl_90");
		symbolDict.put("91","InventurAufklaerer_91");
		symbolDict.put("92","InventurAufklaererKurz_92");
		symbolDict.put("93","InventurBesitzerKurz_93");
		symbolDict.put("94","InventurBuendnis_94");
		symbolDict.put("95","InventurBuendnisKurz_95");
		symbolDict.put("96","InventurEnergieproduktionKurz_96");
		symbolDict.put("97","InventurEnergievorratKurz_97");
		symbolDict.put("98","InventurFestungKurz_98");
		symbolDict.put("99","InventurFestungRaumerKurz_99");
		symbolDict.put("9A","ZugeingabeSpielzuegeNichtImportiert_9A");
		symbolDict.put("9B","ZugeingabeStartErfolgreich_9B");
		symbolDict.put("9C","ZugeingabeStartplanet_9C");
		symbolDict.put("9D","ZugeingabeTitel_9D");
		symbolDict.put("9E","ZugeingabeTransporter_9E");
		symbolDict.put("9F","ZugeingabeUndo_9F");
		symbolDict.put("9G","ZugeingabeUndoErfolg_9G");
		symbolDict.put("9H","ZugeingabeUndoFrage_9H");
		symbolDict.put("9I","ZugeingabeWartenAufSpielzuege_9I");
		symbolDict.put("9J","ZugeingabeWievieleEe_9J");
		symbolDict.put("9K","ZugeingabeWohin100erMine_9K");
		symbolDict.put("9L","ZugeingabeWohin250erMine_9L");
		symbolDict.put("9M","ZugeingabeWohin500erMine_9M");
		symbolDict.put("9N","ZugeingabeWohin50erMine_9N");
		symbolDict.put("9O","ZugeingabeWohinAufklaerer_9O");
		symbolDict.put("9P","ZugeingabeWohinMinenraumer_9P");
		symbolDict.put("9Q","ZugeingabeWohinPatrouille_9Q");
		symbolDict.put("9R","ZugeingabeWohinRaumer_9R");
		symbolDict.put("9S","ZugeingabeWohinTransporter_9S");
		symbolDict.put("9T","ZugeingabeZielplanet_9T");
		symbolDict.put("9U","ZugeingabeZielplanetIstStartplanet_9U");
		symbolDict.put("9V","ZugeingabeZuVielEe_9V");
		symbolDict.put("9W","ZugeingabeZuerstKuendigen_9W");
		symbolDict.put("9X","ZugeingabeZufaelligerSpieler_9X");
		symbolDict.put("9Y","Zurueck_9Y");
		symbolDict.put("9Z","Auswertung_9Z");
		symbolDict.put("AA","ClientSettingsJDialogKeineVerbindung2_AA");
		symbolDict.put("AB","StatistikMinuten_AB");
		symbolDict.put("AC","ZugeingabeBuendnis0NichtKombinieren_AC");
		symbolDict.put("AD","ZugeingabeKeineSpielzuegeAbbrechen_AD");
		symbolDict.put("AE","ZugeingabeMine100_AE");
		symbolDict.put("AF","ServerAdminAnDenSeverSchicken_AF");
		symbolDict.put("AG","UserId_AG");
		symbolDict.put("AH","Name_AH");
		symbolDict.put("AI","ServerAdminSpieler_AI");
		symbolDict.put("AJ","ServerAdminShutdown_AJ");
		symbolDict.put("AK","ServerAdminDatei_AK");
		symbolDict.put("AL","ServerAdminUrl_AL");
		symbolDict.put("AM","ServerAdminPort_AM");
		symbolDict.put("AN","ServerAdminVerbindungstest_AN");
		symbolDict.put("AO","ServerAdminAdminAuth_AO");
		symbolDict.put("AP","UngueltigeAnmeldedaten_AP");
		symbolDict.put("AQ","VerbindungErfolgreich_AQ");
		symbolDict.put("AR","VerbindungNichtErfolgreich_AR");
		symbolDict.put("AS","ServerAdminBenutzerAnlegenFrage_AS");
		symbolDict.put("AT","ServerAdminShutdownFrage_AT");
		symbolDict.put("AU","AreYouSure_AU");
		symbolDict.put("AV","ServerAdminShutdownDone_AV");
		symbolDict.put("AW","Verbindungsfehler_AW");
		symbolDict.put("AX","KeineDateiAusgewaehlt_AX");
		symbolDict.put("AY","ServerZugangsdaten_AY");
		symbolDict.put("AZ","ServerZugangsdatenNichtHinterlegt_AZ");
		symbolDict.put("BA","ServerbasierteSpiele_BA");
		symbolDict.put("BB","SternServerVerwalten_BB");
		symbolDict.put("BC","VerbundenMitServer_BC");
		symbolDict.put("BD","MitspielerWarten_BD");
		symbolDict.put("BE","EmailSubjectNeuerUser_BE");
		symbolDict.put("BF","NeuerUserEMailBody_BF");
		symbolDict.put("BG","Serververbindung_BG");
		symbolDict.put("BH","Aktivieren_BH");
		symbolDict.put("BI","BenutzerAktivieren_BI");
		symbolDict.put("BJ","BenutzerAktivierenFrage_BJ");
		symbolDict.put("BK","BenutzerAktivierenAbspeichern_BK");
		symbolDict.put("BL","BenutzerAktivierenErfolg_BL");
		symbolDict.put("BM","ServerGamesSpielerWarten_BM");
		symbolDict.put("BN","ServerGamesIchWarte_BN");
		symbolDict.put("BO","ServerGamesBeendeteSpiele_BO");
		symbolDict.put("BP","ServerGamesNeuesSpiel_BP");
		symbolDict.put("BQ","ServerGamesNeuesSpielfeld_BQ");
		symbolDict.put("BR","ServerGamesSubmit_BR");
		symbolDict.put("BS","ServerGamesSubmitNamenZuweisen_BS");
		symbolDict.put("BT","ServerGamesSubmitFrage_BT");
		symbolDict.put("BU","ServerGamesSubmitAngelegt_BU");
		symbolDict.put("BV","ServerGamesLaden_BV");
		symbolDict.put("BW","ServerGamesSpielerPlaneten_BW");
		symbolDict.put("BY","ServerGamesBegonnen_BY");
		symbolDict.put("BZ","ServerGamesSpielname_BZ");
		symbolDict.put("CA","ServerWillkommen_CA");
		symbolDict.put("CB","ServerVoreingestellt_CB");
		symbolDict.put("CC","ServerEmailAdmin_CC");
		symbolDict.put("CD","ServerInitConfirm_CD");
		symbolDict.put("CE","ServerInitAbort_CE");
		symbolDict.put("CF","ServerStarting_CF");
		symbolDict.put("CG","ServerStarted_CG");
		symbolDict.put("CH","ServerNotStarted_CH");
		symbolDict.put("CI","ServerWaiting_CI");
		symbolDict.put("CJ","ServerIncomingConnection_CJ");
		symbolDict.put("CK","ServerISocketClose_CK");
		symbolDict.put("CL","ServerIDateiAngelegt_CL");
		symbolDict.put("CM","ServerILogDatum_CM");
		symbolDict.put("CN","ServerILogEventId_CN");
		symbolDict.put("CO","ServerILogThreadId_CO");
		symbolDict.put("CP","ServerILogLevel_CP");
		symbolDict.put("CQ","ServerILogMeldung_CQ");
		symbolDict.put("CR","ServerErrorUngueltigeLaengeBenutzer_CR");
		symbolDict.put("CS","ServerBenutzer_CS");
		symbolDict.put("CT","ServerErrorUngueltigerBenutzer_CT");
		symbolDict.put("CU","ServerErrorRequestReceive_CU");
		symbolDict.put("CV","ServerErrorSendResponse_CV");
		symbolDict.put("CW","ServerErrorNichtAuthorisiert_CW");
		symbolDict.put("CX","ServerErrorDecode_CX");
		symbolDict.put("CY","ServerInfoMessageType_CY");
		symbolDict.put("CZ","ServerInfoClientClosing_CZ");
		symbolDict.put("DA","ServerErrorClientClosing_DA");
		symbolDict.put("DB","ServerThreadClosing_DB");
		symbolDict.put("DC","ServerErrorNotAuthorized_DC");
		symbolDict.put("DD","ServerErrorSpielerNimmNichtTeil_DD");
		symbolDict.put("DE","ServerErrorSpielExistiertNicht_DE");
		symbolDict.put("DF","ServerErrorAdminNeuerUser_DF");
		symbolDict.put("DG","ServerErrorAdminUserUnzulaessig_DG");
		symbolDict.put("DH","ServerInfoInaktiverBenutzerAngelegt_DH");
		symbolDict.put("DI","ServerErrorBenutzerBereitsAktiviert_DI");
		symbolDict.put("DK","ServerErrorJahrVorbei_DK");
		symbolDict.put("DM","ZugeingabePostMovesSuccess_DM");
		symbolDict.put("DN","ZugeingabePostMovesError_DN");
		symbolDict.put("DO","Taste_DO");
		symbolDict.put("DP","NochmalVersuchen_DP");
		symbolDict.put("DQ","WartenBisNaechsteZugeingabe_DQ");
		symbolDict.put("DR","AuswertungVerfuegbar_DR");
		symbolDict.put("DS","SpracheDialogTitle_DS");
		symbolDict.put("DT","Sprache_DT");
		symbolDict.put("DU","SpracheDialogFrage_DU");
		symbolDict.put("DV","MenuSpracheinstellungen_DV");
		symbolDict.put("DW","AndereTaste_DW");
		symbolDict.put("DX","ServerKommunikationInaktiv_DX");
		symbolDict.put("E0","InventurMinenraeumerTransfer_E0");
		symbolDict.put("E1","ZugeingabeZielTransfer_E1");
		symbolDict.put("E2","ClipboardImportJDIalogImportFehlerPassword_E2");
		symbolDict.put("E3","Aktivierungspasswort_E3");
		symbolDict.put("E5","PasswortWiederholen_E5");
		symbolDict.put("E6","PasswoerterUnterschiedlich_E6");
		symbolDict.put("E7","PasswortZuKurz_E7");
		symbolDict.put("E8","Passwort_E8");
		symbolDict.put("EC","SpielinformationenNeutralePlaneten_EC");
		symbolDict.put("ED","SpielinformationenNeutralePlanetenTitel_ED");
		symbolDict.put("EE","ServerBuildFalsch_EE");
		symbolDict.put("EF","SternScreenSharingServer_EF");
		symbolDict.put("EG","EmailSubjectEingeladen_EG");
		symbolDict.put("EH","EmailBodyEingeladen_EH");
		symbolDict.put("EI","SpielerNichtAngemeldet_EI");
		symbolDict.put("EJ","AuswertungAufklaererSender_EJ");
		symbolDict.put("EK","ZugeingabeMehr_EK");
		symbolDict.put("EL","ZugeingabeRaumerAufPlanet_EL");
		symbolDict.put("EM","ZugeingabeRaumerAnzeigen_EM");
		symbolDict.put("EN","AuswertungVerfuegbar2_EN");
		symbolDict.put("EO","ServerGamesSubmitAngelegt2_EO");
		symbolDict.put("EP","LetztesJahr_EP");
		symbolDict.put("EQ","LetztesJahr2_EQ");
		symbolDict.put("ER","AbgeschlossenesSpiel_ER");
		symbolDict.put("ET","ZugeingabeKapitulieren_ET");
		symbolDict.put("EU","AuswertungKapitulation_EU");
		symbolDict.put("EV","UpdateVerfuegbar_EV");
		symbolDict.put("EX","Update_EX");
		symbolDict.put("EY","ZugeingabeSpielzuegeSchonEingegeben_EY");
		symbolDict.put("EZ","ServerAnwendungsfehler_EZ");
		symbolDict.put("FB","ReleaseFormatted_FB");
		symbolDict.put("FC","UpdateVerfuegbarWichtig_FC");
		symbolDict.put("FD","MenuSearchForUpdates_FD");
		symbolDict.put("FE","UpdateAktuell_FE");
		symbolDict.put("FF","UpdateServerNichtErreichbar_FF");
		symbolDict.put("FG","ServerAdminSpielerLoeschen_FG");
		symbolDict.put("FH","ServerAdminSpielerAnlegen_FH");
		symbolDict.put("FI","ServerAdminAnmeldedatenErneuern_FI");
		symbolDict.put("FJ","ServerErrorAdminUserExistiertNicht_FJ");
		symbolDict.put("FK","ServerAdminUserNeuLaden_FK");
		symbolDict.put("FL","ServerAdminUserErfolg_FL");
		symbolDict.put("FM","ServerAdminUserRenewCredentials_FM");
		symbolDict.put("FN","ServerAdminUserUpdate_FN");
		symbolDict.put("FO","ServerErrorLogonWithInactiveUser_FO");
		symbolDict.put("FP","ServerAdminUserDelete_FP");
		symbolDict.put("FQ","ServerAdminUserDeleted_FQ");
		symbolDict.put("FR","ServerAdminUserCreated_FR");
		symbolDict.put("FS","MenuEmail_FS");
		symbolDict.put("FT","EmailErzeugen_FT");
		symbolDict.put("FU","EmailUnbekannt_FU");
		symbolDict.put("FV","ServerStatus_FV");
		symbolDict.put("FW","ServerBuild_FW");
		symbolDict.put("FX","ServerLaeuftSeit_FX");
		symbolDict.put("FY","ServerLogGroesse_FY");
		symbolDict.put("FZ","ServerLogDownload_FZ");
		symbolDict.put("GA","ServerLogLevel_GA");
		symbolDict.put("GB","ServerLogLevelAendern_GB");
		symbolDict.put("GC","ServerStatusAktualisieren_GC");
		symbolDict.put("GD","ServerLogLevelAendernAYS_GD");
		symbolDict.put("GE","ServerLogLevelAendernErfolg_GE");
		symbolDict.put("GF","ServerLogLeer_GF");
		symbolDict.put("GG","ReleaseFormatted2_GG");
		symbolDict.put("GH","AuswertungEreignisTag_GH");
		symbolDict.put("GI","AuswertungEreignisJahresbeginn_GI");
		symbolDict.put("GJ","AuswertungEreignisJahresende_GJ");
		symbolDict.put("GK","FlugzeitOutput_GK");
		symbolDict.put("GL","FlugzeitOutputJahresende_GL");
		symbolDict.put("GM","FlugzeitOutputShort_GM");
		symbolDict.put("GN","FlugzeitOutputJahresendeShort_GN");
		symbolDict.put("GP","ZugeingabeMinenraeumerMission_GP");
		symbolDict.put("GQ","ZugeingabePatrouilleMission_GQ");
		symbolDict.put("GT","SpielinformationenKommandozentralenUnterwegs2_GT");
		symbolDict.put("GV","ServerBuildVeraltet_GV");
		symbolDict.put("GW","ServerGamesLetzteAktivitaet_GW");
		symbolDict.put("GY","ServerGamesSpielleiteraktionen_GY");
		symbolDict.put("GZ","ServerGamesLoeschen_GZ");
		symbolDict.put("HA","ServerGamesBeenden_HA");
		symbolDict.put("HC","ServerErrorKeinSpielleiter_HC");
		symbolDict.put("HD","ServerGamesGameDeleted_HD");
		symbolDict.put("HE","ServerGamesLoeschenAys_HE");
		symbolDict.put("HF","ServerGamesAbgeschlossen_HF");
		symbolDict.put("HG","ServerGamesBeendenAys_HG");
		symbolDict.put("HH","ServerGamesGameFinalized_HH");
		symbolDict.put("HI","AuswertungEreignisJahresbeginn2_HI");
		symbolDict.put("HJ","AuswertungEreignisJahresende2_HJ");
		symbolDict.put("HK","AuswertungEreignisTag2_HK");
		symbolDict.put("HL","ZugeingabeObjekteAusblenden_HL");
		symbolDict.put("HM","ZugeingabeObjekteAusblendenAn_HM");
		symbolDict.put("HN","ZugeingabeObjekteAusblendenAus_HN");
		symbolDict.put("HO","ZugeingabeObjekteAusblendenAlleAus_HO");
		symbolDict.put("HP","ZugeingabeObjekteAusblendenAlleAn_HP");
		symbolDict.put("HQ","AuswertungVerfuegbarSymbol_HQ");
		symbolDict.put("HR","ServerErrorDh_HR");
		symbolDict.put("HS","ServerNeueSession_HS");
		symbolDict.put("HT","ServerILogSessionId_HT");
		symbolDict.put("HU","BenachrichtigungStumm_HU");
		symbolDict.put("HV","ServerUserLesen_HV");
		symbolDict.put("HW","ServerConfigLaden_HW");
		symbolDict.put("HX","ServerConfigErzeugen_HX");
		symbolDict.put("HY","ServerSpielLesen_HY");
		symbolDict.put("HZ","ServerOrdnerErzeugen_HZ");
		symbolDict.put("IA","ServerAdminErzeugen_IA");
		symbolDict.put("IB","ZugeingabeClientEingabeGesperrt_IB");
		symbolDict.put("IC","ServerSettingsJDialogInaktiv_IC");
		symbolDict.put("ID","ClientSettingsJDialogIpErmitteln_ID");
		symbolDict.put("IE","ClientSettingsJDialogMeineIp_IE");
		symbolDict.put("IF","SpielinformationenKampfschiffproduktionTitel_IF");
		symbolDict.put("IG","SpielinformationenKampfschiffproduktion_IG");
		symbolDict.put("IH","ServerUrlUebernehmen_IH");
		symbolDict.put("II","ServerZugangsdatenAendern_II");
		symbolDict.put("IJ","BrowserNichtGeoeffnet_IJ");
		symbolDict.put("IK","EmailNichtGeoeffnet_IK");
		symbolDict.put("IL","ClientBuild_IL");
		symbolDict.put("IM","ZugeingabePlaneteninfo2_IM");
		symbolDict.put("IN","MenuAusgabeFenster_IN");
	}
	public static String getString(String symbolString){
		StringBuilder sb = new StringBuilder();
		int pos = 0;

		do {
			int startPos = symbolString.indexOf("£", pos);
			if (startPos < 0){
				sb.append(symbolString.substring(pos, symbolString.length()));
				break;}
			sb.append(symbolString.substring(pos, startPos));
			int endPos = symbolString.indexOf("£", startPos + 1);
			String subString = symbolString.substring(startPos + 1, endPos);
			Object[] parts = subString.split("§");
			if (symbolDict.containsKey(parts[0])){
				if (parts.length == 1)
					sb.append(messages.getString(symbolDict.get(parts[0])));
				else{
					Object[] args = new Object[parts.length - 1];
					for (int i = 1; i < parts.length; i++)
						args[i-1] = parts[i];
						sb.append(MessageFormat.format(messages.getString(symbolDict.get(parts[0])) ,args));
			}}
			pos = endPos + 1;
		} while (true);
		return sb.toString();
	}

	/**
	   * Abbrechen [00]
	   */
	public static String Abbrechen(boolean symbol) {
		return symbol ? "£00£":messages.getString("Abbrechen_00");
	}

	/**
	   * $ Prod. [01]
	   */
	public static String AbschlussEprod(boolean symbol) {
		return symbol ? "£01£":messages.getString("AbschlussEprod_01");
	}

	/**
	   * Platz {0}: [02]
	   */
	public static String AbschlussPlatz(boolean symbol, String arg0) {
		return symbol ? "£02§"+arg0+"£":MessageFormat.format(messages.getString("AbschlussPlatz_02"), arg0);
	}

	/**
	   * --- Aktion wurde abgebrochen --- [03]
	   */
	public static String AktionAbgebrochen(boolean symbol) {
		return symbol ? "£03£":messages.getString("AktionAbgebrochen_03");
	}

	/**
	   * Aktualisieren [04]
	   */
	public static String Aktualisieren(boolean symbol) {
		return symbol ? "£04£":messages.getString("Aktualisieren_04");
	}

	/**
	   * Aufkl\u00E4rer [05]
	   */
	public static String AufklaererPlural(boolean symbol) {
		return symbol ? "£05£":messages.getString("AufklaererPlural_05");
	}

	/**
	   * Ausw\u00E4hlen [06]
	   */
	public static String Auswaehlen(boolean symbol) {
		return symbol ? "£06£":messages.getString("Auswaehlen_06");
	}

	/**
	   * Angreifer: {0}, Abwehrschild: {1} [07]
	   */
	public static String AuswertungAngriffAngreiferFestung(boolean symbol, String arg0, String arg1) {
		return symbol ? "£07§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngreiferFestung_07"), arg0, arg1);
	}

	/**
	   * {0} greift Planet {1} an [08]
	   */
	public static String AuswertungAngriffAngriffAufPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£08§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngriffAufPlanet_08"), arg0, arg1);
	}

	/**
	   * Angriff gescheitert. [09]
	   */
	public static String AuswertungAngriffAngriffGescheitert(boolean symbol) {
		return symbol ? "£09£":messages.getString("AuswertungAngriffAngriffGescheitert_09");
	}

	/**
	   * Raumschiffe (ohne Patrouillen im Einsatz) [0A]
	   */
	public static String InventurFlugobjekte(boolean symbol) {
		return symbol ? "£0A£":messages.getString("InventurFlugobjekte_0A");
	}

	/**
	   * Fracht [0B]
	   */
	public static String InventurFracht(boolean symbol) {
		return symbol ? "£0B£":messages.getString("InventurFracht_0B");
	}

	/**
	   * Jahr {0} [0C]
	   */
	public static String InventurJahr1(boolean symbol, String arg0) {
		return symbol ? "£0C§"+arg0+"£":MessageFormat.format(messages.getString("InventurJahr1_0C"), arg0);
	}

	/**
	   * Jahr {0} von {1} [0D]
	   */
	public static String InventurJahr2(boolean symbol, String arg0, String arg1) {
		return symbol ? "£0D§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("InventurJahr2_0D"), arg0, arg1);
	}

	/**
	   * Es sind keine Raumschiffe unterwegs. [0E]
	   */
	public static String InventurKeineFlugobjekte(boolean symbol) {
		return symbol ? "£0E£":messages.getString("InventurKeineFlugobjekte_0E");
	}

	/**
	   * Es befinden sich keine Patrouillen im Einsatz. [0F]
	   */
	public static String InventurKeinePatrouillen(boolean symbol) {
		return symbol ? "£0F£":messages.getString("InventurKeinePatrouillen_0F");
	}

	/**
	   * Sie besitzen keine Planeten. [0G]
	   */
	public static String InventurKeinePlaneten(boolean symbol) {
		return symbol ? "£0G£":messages.getString("InventurKeinePlaneten_0G");
	}

	/**
	   * Es sind keine Kampfschiffe unterwegs. [0H]
	   */
	public static String InventurKeineRaumer(boolean symbol) {
		return symbol ? "£0H£":messages.getString("InventurKeineRaumer_0H");
	}

	/**
	   * Kommandant [0I]
	   */
	public static String InventurKommandant(boolean symbol) {
		return symbol ? "£0I£":messages.getString("InventurKommandant_0I");
	}

	/**
	   * Kz [0J]
	   */
	public static String InventurKommandozentraleKurz(boolean symbol) {
		return symbol ? "£0J£":messages.getString("InventurKommandozentraleKurz_0J");
	}

	/**
	   * M100 [0K]
	   */
	public static String InventurMine100Kurz(boolean symbol) {
		return symbol ? "£0K£":messages.getString("InventurMine100Kurz_0K");
	}

	/**
	   * M250 [0L]
	   */
	public static String InventurMine250Kurz(boolean symbol) {
		return symbol ? "£0L£":messages.getString("InventurMine250Kurz_0L");
	}

	/**
	   * M500 [0M]
	   */
	public static String InventurMine500Kurz(boolean symbol) {
		return symbol ? "£0M£":messages.getString("InventurMine500Kurz_0M");
	}

	/**
	   * M50 [0N]
	   */
	public static String InventurMine50Kurz(boolean symbol) {
		return symbol ? "£0N£":messages.getString("InventurMine50Kurz_0N");
	}

	/**
	   * Minenleger (100) [0O]
	   */
	public static String InventurMinenleger100(boolean symbol) {
		return symbol ? "£0O£":messages.getString("InventurMinenleger100_0O");
	}

	/**
	   * Minenleger (250) [0P]
	   */
	public static String InventurMinenleger250(boolean symbol) {
		return symbol ? "£0P£":messages.getString("InventurMinenleger250_0P");
	}

	/**
	   * Minenleger (50) [0Q]
	   */
	public static String InventurMinenleger50(boolean symbol) {
		return symbol ? "£0Q£":messages.getString("InventurMinenleger50_0Q");
	}

	/**
	   * Minenleger (500) [0R]
	   */
	public static String InventurMinenleger500(boolean symbol) {
		return symbol ? "£0R£":messages.getString("InventurMinenleger500_0R");
	}

	/**
	   * Minenr\u00E4umer [0S]
	   */
	public static String InventurMinenraeumer(boolean symbol) {
		return symbol ? "£0S£":messages.getString("InventurMinenraeumer_0S");
	}

	/**
	   * Pat [0T]
	   */
	public static String InventurPatrouilleKurz(boolean symbol) {
		return symbol ? "£0T£":messages.getString("InventurPatrouilleKurz_0T");
	}

	/**
	   * Patrouille (Transfer) [0U]
	   */
	public static String InventurPatrouilleTransfer(boolean symbol) {
		return symbol ? "£0U£":messages.getString("InventurPatrouilleTransfer_0U");
	}

	/**
	   * Patrouillen [0V]
	   */
	public static String InventurPatrouillen(boolean symbol) {
		return symbol ? "£0V£":messages.getString("InventurPatrouillen_0V");
	}

	/**
	   * Patrouillen im Einsatz [0W]
	   */
	public static String InventurPatrrouillenTitel(boolean symbol) {
		return symbol ? "£0W£":messages.getString("InventurPatrrouillenTitel_0W");
	}

	/**
	   * Fehler: PDF-Anzeigeprogramm konnte nicht ge\u00F6ffnet werden. [0X]
	   */
	public static String InventurPdfFehler(boolean symbol) {
		return symbol ? "£0X£":messages.getString("InventurPdfFehler_0X");
	}

	/**
	   * PDF-Anzeigeprogramm wurde ge\u00F6ffnet. [0Y]
	   */
	public static String InventurPdfGeoeffnet(boolean symbol) {
		return symbol ? "£0Y£":messages.getString("InventurPdfGeoeffnet_0Y");
	}

	/**
	   * Planet [0Z]
	   */
	public static String InventurPlanet(boolean symbol) {
		return symbol ? "£0Z£":messages.getString("InventurPlanet_0Z");
	}

	/**
	   * Angreifer: {0}, Planet: {1} [13]
	   */
	public static String AuswertungAngriffAngreiferPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£13§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngreiferPlanet_13"), arg0, arg1);
	}

	/**
	   * {0} hat den Planeten erobert! [15]
	   */
	public static String AuswertungAngriffSpielerErobert(boolean symbol, String arg0) {
		return symbol ? "£15§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungAngriffSpielerErobert_15"), arg0);
	}

	/**
	   * {0}: 1 Aufkl\u00E4rer auf Planet {1} angekommen. [17]
	   */
	public static String AuswertungAufklaererAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£17§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAufklaererAngekommen_17"), arg0, arg1);
	}

	/**
	   * Raumschiffe starten, Planeten produzieren. [19]
	   */
	public static String AuswertungBeginnt(boolean symbol) {
		return symbol ? "£19£":messages.getString("AuswertungBeginnt_19");
	}

	/**
	   * Planet 1 [1A]
	   */
	public static String InventurPlanet1(boolean symbol) {
		return symbol ? "£1A£":messages.getString("InventurPlanet1_1A");
	}

	/**
	   * Planet 2 [1B]
	   */
	public static String InventurPlanet2(boolean symbol) {
		return symbol ? "£1B£":messages.getString("InventurPlanet2_1B");
	}

	/**
	   * Pl [1C]
	   */
	public static String InventurPlanetKurz(boolean symbol) {
		return symbol ? "£1C£":messages.getString("InventurPlanetKurz_1C");
	}

	/**
	   * MRa [1D]
	   */
	public static String InventurMinenraeumerKurz(boolean symbol) {
		return symbol ? "£1D£":messages.getString("InventurMinenraeumerKurz_1D");
	}

	/**
	   * Planeten und B\u00FCndnisse [1E]
	   */
	public static String InventurPlanetenTitel(boolean symbol) {
		return symbol ? "£1E£":messages.getString("InventurPlanetenTitel_1E");
	}

	/**
	   * Planeten [1F]
	   */
	public static String InventurPlanetenTitelSimpel(boolean symbol) {
		return symbol ? "£1F£":messages.getString("InventurPlanetenTitelSimpel_1F");
	}

	/**
	   * {0} Punkte [1G]
	   */
	public static String InventurPunkte(boolean symbol, String arg0) {
		return symbol ? "£1G§"+arg0+"£":MessageFormat.format(messages.getString("InventurPunkte_1G"), arg0);
	}

	/**
	   * Kampfschiffe [1H]
	   */
	public static String InventurRaumer(boolean symbol) {
		return symbol ? "£1H£":messages.getString("InventurRaumer_1H");
	}

	/**
	   * Ks [1I]
	   */
	public static String InventurRaumerKurz(boolean symbol) {
		return symbol ? "£1I£":messages.getString("InventurRaumerKurz_1I");
	}

	/**
	   * Kampfschiffproduktion/Jahr [1J]
	   */
	public static String InventurRaumerproduktionJahr(boolean symbol) {
		return symbol ? "£1J£":messages.getString("InventurRaumerproduktionJahr_1J");
	}

	/**
	   * EKs [1K]
	   */
	public static String InventurRaumerproduktionKurz(boolean symbol) {
		return symbol ? "£1K£":messages.getString("InventurRaumerproduktionKurz_1K");
	}

	/**
	   * Seite {0} [1L]
	   */
	public static String InventurSeite(boolean symbol, String arg0) {
		return symbol ? "£1L§"+arg0+"£":MessageFormat.format(messages.getString("InventurSeite_1L"), arg0);
	}

	/**
	   * Start [1M]
	   */
	public static String InventurStart(boolean symbol) {
		return symbol ? "£1M£":messages.getString("InventurStart_1M");
	}

	/**
	   * Inventur [1N]
	   */
	public static String InventurTitel(boolean symbol) {
		return symbol ? "£1N£":messages.getString("InventurTitel_1N");
	}

	/**
	   * Transporter [1O]
	   */
	public static String InventurTransporter(boolean symbol) {
		return symbol ? "£1O£":messages.getString("InventurTransporter_1O");
	}

	/**
	   * {0} $ [1P]
	   */
	public static String InventurTransporterEe(boolean symbol, String arg0) {
		return symbol ? "£1P§"+arg0+"£":MessageFormat.format(messages.getString("InventurTransporterEe_1P"), arg0);
	}

	/**
	   * Tra [1Q]
	   */
	public static String InventurTransporterKurz(boolean symbol) {
		return symbol ? "£1Q£":messages.getString("InventurTransporterKurz_1Q");
	}

	/**
	   * Typ [1R]
	   */
	public static String InventurTyp(boolean symbol) {
		return symbol ? "£1R£":messages.getString("InventurTyp_1R");
	}

	/**
	   * Ziel [1S]
	   */
	public static String InventurZiel(boolean symbol) {
		return symbol ? "£1S£":messages.getString("InventurZiel_1S");
	}

	/**
	   * Ja [1T]
	   */
	public static String Ja(boolean symbol) {
		return symbol ? "£1T£":messages.getString("Ja_1T");
	}

	/**
	   * Kommandozentrale [1U]
	   */
	public static String Kommandozentrale(boolean symbol) {
		return symbol ? "£1U£":messages.getString("Kommandozentrale_1U");
	}

	/**
	   * Links [1V]
	   */
	public static String Links(boolean symbol) {
		return symbol ? "£1V£":messages.getString("Links_1V");
	}

	/**
	   * L\u00F6schen [1W]
	   */
	public static String Loeschen(boolean symbol) {
		return symbol ? "£1W£":messages.getString("Loeschen_1W");
	}

	/**
	   * Lokale Bestenliste [1X]
	   */
	public static String MenuBestenliste(boolean symbol) {
		return symbol ? "£1X£":messages.getString("MenuBestenliste_1X");
	}

	/**
	   * Spiel [1Z]
	   */
	public static String MenuDatei(boolean symbol) {
		return symbol ? "£1Z£":messages.getString("MenuDatei_1Z");
	}

	/**
	   * {0}: B\u00FCndnis auf Planet {1} kann nicht ge\u00E4ndert werden. [20]
	   */
	public static String AuswertungBuendnisNichtGeaendert(boolean symbol, String arg0, String arg1) {
		return symbol ? "£20§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungBuendnisNichtGeaendert_20"), arg0, arg1);
	}

	/**
	   * Ende der Auswertung. [21]
	   */
	public static String AuswertungEnde(boolean symbol) {
		return symbol ? "£21£":messages.getString("AuswertungEnde_21");
	}

	/**
	   * 'Die Neutralen' erobern die Kommandozentrale von {0}! [22]
	   */
	public static String AuswertungKommandozentraleErobertNeutral(boolean symbol, String arg0) {
		return symbol ? "£22§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungKommandozentraleErobertNeutral_22"), arg0);
	}

	/**
	   * {0} erobert die Kommandozentrale von {1}! [23]
	   */
	public static String AuswertungKommandozentraleErobertSpieler(boolean symbol, String arg0, String arg1) {
		return symbol ? "£23§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungKommandozentraleErobertSpieler_23"), arg0, arg1);
	}

	/**
	   * {0}: Mine wurde gelegt. [24]
	   */
	public static String AuswertungMineGelegt(boolean symbol, String arg0) {
		return symbol ? "£24§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungMineGelegt_24"), arg0);
	}

	/**
	   * Minenfeld der St\u00E4rke {0} ger\u00E4umt. [25]
	   */
	public static String AuswertungMinenfeldGeraeumt(boolean symbol, String arg0) {
		return symbol ? "£25§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungMinenfeldGeraeumt_25"), arg0);
	}

	/**
	   * {0}: 1 Minenleger auf Planet {1} angekommen. [27]
	   */
	public static String AuswertungMinenlegerAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£27§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenlegerAngekommen_27"), arg0, arg1);
	}

	/**
	   * {0}: 1 Minenleger auf Planet {1} zerschellt. [28]
	   */
	public static String AuswertungMinenlegerZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£28§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenlegerZerschellt_28"), arg0, arg1);
	}

	/**
	   * {0}: 1 Minenr\u00E4umer auf Planet {1} angekommen. [29]
	   */
	public static String AuswertungMinenraeumerAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£29§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenraeumerAngekommen_29"), arg0, arg1);
	}

	/**
	   * Einstellungen [2A]
	   */
	public static String MenuEinstellungen(boolean symbol) {
		return symbol ? "£2A£":messages.getString("MenuEinstellungen_2A");
	}

	/**
	   * Hilfe [2B]
	   */
	public static String MenuHilfe(boolean symbol) {
		return symbol ? "£2B£":messages.getString("MenuHilfe_2B");
	}

	/**
	   * Neues lokales Spiel [2C]
	   */
	public static String MenuNeuesSpiel(boolean symbol) {
		return symbol ? "£2C£":messages.getString("MenuNeuesSpiel_2C");
	}

	/**
	   * STERN Display [2D]
	   */
	public static String MenuScreesharing(boolean symbol) {
		return symbol ? "£2D£":messages.getString("MenuScreesharing_2D");
	}

	/**
	   * STERN Server verwalten [2E]
	   */
	public static String MenuServerAdmin(boolean symbol) {
		return symbol ? "£2E£":messages.getString("MenuServerAdmin_2E");
	}

	/**
	   * STERN Server-Zugangsdaten [2F]
	   */
	public static String MenuServerCredentials(boolean symbol) {
		return symbol ? "£2F£":messages.getString("MenuServerCredentials_2F");
	}

	/**
	   * Spiele auf dem STERN Server [2G]
	   */
	public static String MenuServerbasierteSpiele(boolean symbol) {
		return symbol ? "£2G£":messages.getString("MenuServerbasierteSpiele_2G");
	}

	/**
	   * E-Mail-Spiel aus Zwischenablage laden [2H]
	   */
	public static String MenuSpielAusZwischenablageLaden(boolean symbol) {
		return symbol ? "£2H£":messages.getString("MenuSpielAusZwischenablageLaden_2H");
	}

	/**
	   * Lokales Spiel laden [2J]
	   */
	public static String MenuSpielLaden(boolean symbol) {
		return symbol ? "£2J£":messages.getString("MenuSpielLaden_2J");
	}

	/**
	   * Lokales Spiel speichern als [2K]
	   */
	public static String MenuSpielSpeichernAls(boolean symbol) {
		return symbol ? "£2K£":messages.getString("MenuSpielSpeichernAls_2K");
	}

	/**
	   * Spielanleitung [2L]
	   */
	public static String MenuSpielanleitung(boolean symbol) {
		return symbol ? "£2L£":messages.getString("MenuSpielanleitung_2L");
	}

	/**
	   * STERN Display verlassen [2M]
	   */
	public static String MenuSternClientVerlassen(boolean symbol) {
		return symbol ? "£2M£":messages.getString("MenuSternClientVerlassen_2M");
	}

	/**
	   * STERN verlassen [2N]
	   */
	public static String MenuSternVerlassen(boolean symbol) {
		return symbol ? "£2N£":messages.getString("MenuSternVerlassen_2N");
	}

	/**
	   * \u00DCber STERN [2O]
	   */
	public static String MenuUeberStern(boolean symbol) {
		return symbol ? "£2O£":messages.getString("MenuUeberStern_2O");
	}

	/**
	   * Verbindungseinstellungen [2P]
	   */
	public static String MenuVerbindungseinstellungen(boolean symbol) {
		return symbol ? "£2P£":messages.getString("MenuVerbindungseinstellungen_2P");
	}

	/**
	   * Die geladenen Daten erfordern einen neueren<br>STERN-Build. Der mindestens vorausgesetzte<br>Build ist {0}, Ihr STERN-Build ist<br>{1}.<br><br>Bitte laden Sie sich den neusten Build unter<br>{2} herunter. [2Q]
	   */
	public static String MinBuild(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£2Q§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("MinBuild_2Q"), arg0, arg1, arg2);
	}

	/**
	   * 100er-Minen [2R]
	   */
	public static String Mine100Plural(boolean symbol) {
		return symbol ? "£2R£":messages.getString("Mine100Plural_2R");
	}

	/**
	   * 250er-Minen [2S]
	   */
	public static String Mine250Plural(boolean symbol) {
		return symbol ? "£2S£":messages.getString("Mine250Plural_2S");
	}

	/**
	   * 500er-Minen [2T]
	   */
	public static String Mine500Plural(boolean symbol) {
		return symbol ? "£2T£":messages.getString("Mine500Plural_2T");
	}

	/**
	   * 50er-Minen [2U]
	   */
	public static String Mine50Plural(boolean symbol) {
		return symbol ? "£2U£":messages.getString("Mine50Plural_2U");
	}

	/**
	   * Minenr\u00E4umer [2V]
	   */
	public static String MinenraeumerPlural(boolean symbol) {
		return symbol ? "£2V£":messages.getString("MinenraeumerPlural_2V");
	}

	/**
	   * M\u00F6chten Sie STERN wirklich verlassen? [2W]
	   */
	public static String MoechtestDuSternVerlassen(boolean symbol) {
		return symbol ? "£2W£":messages.getString("MoechtestDuSternVerlassen_2W");
	}

	/**
	   * Nein [2X]
	   */
	public static String Nein(boolean symbol) {
		return symbol ? "£2X£":messages.getString("Nein_2X");
	}

	/**
	   * Neutral [2Y]
	   */
	public static String Neutral(boolean symbol) {
		return symbol ? "£2Y£":messages.getString("Neutral_2Y");
	}

	/**
	   * OK [2Z]
	   */
	public static String OK(boolean symbol) {
		return symbol ? "£2Z£":messages.getString("OK_2Z");
	}

	/**
	   * {0}: 1 Minenr\u00E4umer auf Planet {1} zerschellt. [30]
	   */
	public static String AuswertungMinenraeumerZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£30§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenraeumerZerschellt_30"), arg0, arg1);
	}

	/**
	   * {0}: Nachricht aus Sektor {1}: [31]
	   */
	public static String AuswertungNachrichtAnAusSektor(boolean symbol, String arg0, String arg1) {
		return symbol ? "£31§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungNachrichtAnAusSektor_31"), arg0, arg1);
	}

	/**
	   * {0}: 1 Patrouille auf Planet {1} angekommen. [33]
	   */
	public static String AuswertungPatrouilleAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£33§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleAngekommen_33"), arg0, arg1);
	}

	/**
	   * {0}: Sie haben einen Aufkl\u00E4rer mit Ziel {1} von {2} gekapert. [34]
	   */
	public static String AuswertungPatrouilleAufklaererGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£34§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleAufklaererGekapert_34"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Sie haben einen Minenleger mit Ziel {1} von {2} gekapert. [36]
	   */
	public static String AuswertungPatrouilleMinenlegerGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£36§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleMinenlegerGekapert_36"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Sie haben einen Minenr\u00E4umer mit Ziel {1} von {2} gekapert. [37]
	   */
	public static String AuswertungPatrouilleMinenraeumerGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£37§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleMinenraeumerGekapert_37"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Sie haben eine Patrouille mit Ziel {1} von {2} gekapert. [38]
	   */
	public static String AuswertungPatrouillePatrouilleGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£38§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouillePatrouilleGekapert_38"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Sie haben eine Patrouille von {1} zerst\u00F6rt. [39]
	   */
	public static String AuswertungPatrouillePatrouilleZerstoert(boolean symbol, String arg0, String arg1) {
		return symbol ? "£39§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouillePatrouilleZerstoert_39"), arg0, arg1);
	}

	/**
	   * Patrouillen [3A]
	   */
	public static String PatrouillePlural(boolean symbol) {
		return symbol ? "£3A£":messages.getString("PatrouillePlural_3A");
	}

	/**
	   * PDF-Anzeigeprogramm \u00F6ffnen? [3B]
	   */
	public static String PdfOeffnenFrage(boolean symbol) {
		return symbol ? "£3B£":messages.getString("PdfOeffnenFrage_3B");
	}

	/**
	   * $ [3C]
	   */
	public static String PlEditEe(boolean symbol) {
		return symbol ? "£3C£":messages.getString("PlEditEe_3C");
	}

	/**
	   * $ Vorrat [3D]
	   */
	public static String PlEditEeEnergievorrat(boolean symbol) {
		return symbol ? "£3D£":messages.getString("PlEditEeEnergievorrat_3D");
	}

	/**
	   * $ Produktion/Jahr (Kauf +4) [3E]
	   */
	public static String PlEditEprodPlus4(boolean symbol) {
		return symbol ? "£3E£":messages.getString("PlEditEprodPlus4_3E");
	}

	/**
	   * Abwehrschild-Kampfschiffe (Kauf +{0}) [3F]
	   */
	public static String PlEditFestungRaumer(boolean symbol, String arg0) {
		return symbol ? "£3F§"+arg0+"£":MessageFormat.format(messages.getString("PlEditFestungRaumer_3F"), arg0);
	}

	/**
	   * Abwehrschilde [3G]
	   */
	public static String PlEditFestungen(boolean symbol) {
		return symbol ? "£3G£":messages.getString("PlEditFestungen_3G");
	}

	/**
	   * Kaufpreis [3H]
	   */
	public static String PlEditKaufpreis(boolean symbol) {
		return symbol ? "£3H£":messages.getString("PlEditKaufpreis_3H");
	}

	/**
	   * Kampfschiffproduktion/Jahr [3I]
	   */
	public static String PlEditRaumerProd(boolean symbol) {
		return symbol ? "£3I£":messages.getString("PlEditRaumerProd_3I");
	}

	/**
	   * Verkaufspreis [3J]
	   */
	public static String PlEditVerkaufspreis(boolean symbol) {
		return symbol ? "£3J£":messages.getString("PlEditVerkaufspreis_3J");
	}

	/**
	   * Planeten [3K]
	   */
	public static String Planeten(boolean symbol) {
		return symbol ? "£3K£":messages.getString("Planeten_3K");
	}

	/**
	   * Ab [3L]
	   */
	public static String PlaneteneditorAb(boolean symbol) {
		return symbol ? "£3L£":messages.getString("PlaneteneditorAb_3L");
	}

	/**
	   * Auf [3M]
	   */
	public static String PlaneteneditorAuf(boolean symbol) {
		return symbol ? "£3M£":messages.getString("PlaneteneditorAuf_3M");
	}

	/**
	   * Auswahl \u00E4ndern [3N]
	   */
	public static String PlaneteneditorAuswahlAendern(boolean symbol) {
		return symbol ? "£3N£":messages.getString("PlaneteneditorAuswahlAendern_3N");
	}

	/**
	   * Kaufen\n [3O]
	   */
	public static String PlaneteneditorKaufen(boolean symbol) {
		return symbol ? "£3O£":messages.getString("PlaneteneditorKaufen_3O");
	}

	/**
	   * \u00C4nderungen \u00FCbernehmen [3P]
	   */
	public static String PlaneteneditorUebernehmen(boolean symbol) {
		return symbol ? "£3P£":messages.getString("PlaneteneditorUebernehmen_3P");
	}

	/**
	   * Verkaufen [3Q]
	   */
	public static String PlaneteneditorVerkaufen(boolean symbol) {
		return symbol ? "£3Q£":messages.getString("PlaneteneditorVerkaufen_3Q");
	}

	/**
	   * Ung\u00FCltiges Programmargument [{0}] [3R]
	   */
	public static String ProgramArgument(boolean symbol, String arg0) {
		return symbol ? "£3R§"+arg0+"£":MessageFormat.format(messages.getString("ProgramArgument_3R"), arg0);
	}

	/**
	   * Punkte [3S]
	   */
	public static String Punkte(boolean symbol) {
		return symbol ? "£3S£":messages.getString("Punkte_3S");
	}

	/**
	   * Kampfschiffe [3T]
	   */
	public static String Raumer(boolean symbol) {
		return symbol ? "£3T£":messages.getString("Raumer_3T");
	}

	/**
	   * Rechts [3U]
	   */
	public static String Rechts(boolean symbol) {
		return symbol ? "£3U£":messages.getString("Rechts_3U");
	}

	/**
	   * Auswertung wiedergeben [3V]
	   */
	public static String ReplayAuswertungWiedergeben(boolean symbol) {
		return symbol ? "£3V£":messages.getString("ReplayAuswertungWiedergeben_3V");
	}

	/**
	   * {0}: Sie haben {1} Kampfschiffe mit Ziel {2} von {3} gekapert. [40]
	   */
	public static String AuswertungPatrouilleRaumerGekapert(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£40§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleRaumerGekapert_40"), arg0, arg1, arg2, arg3);
	}

	/**
	   * {0} Kampfschiffe von {1} mit Ziel {2} gesichtet. [41]
	   */
	public static String AuswertungPatrouilleRaumerGesichtet(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£41§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleRaumerGesichtet_41"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Sie haben einen Transporter mit Ziel {1} von {2} gekapert. [42]
	   */
	public static String AuswertungPatrouilleTransporterGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£42§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleTransporterGekapert_42"), arg0, arg1, arg2);
	}

	/**
	   * {0}: 1 Patrouille auf Planet {1} zerschellt. [43]
	   */
	public static String AuswertungPatrouilleZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£43§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleZerschellt_43"), arg0, arg1);
	}

	/**
	   * {0}: {1} Kampfschiff(e) auf Planet {2} angekommen. [44]
	   */
	public static String AuswertungRaumerAngekommen(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£44§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAngekommen_44"), arg0, arg1, arg2);
	}

	/**
	   * {0}: {1} Kamfpschiff(e) in Sektor {2} auf eine Mine gelaufen. Die Mine wurde zerst\u00F6rt. [45]
	   */
	public static String AuswertungRaumerAufMineGelaufenZerstoert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£45§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAufMineGelaufenZerstoert_45"), arg0, arg1, arg2);
	}

	/**
	   * {0}: {1} Kampfschiff(e) in Sektor {2} auf eine Mine gelaufen. [46]
	   */
	public static String AuswertungRaumerAufMineGelaufen(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£46§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAufMineGelaufen_46"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Kampfschiffe k\u00F6nnen nicht vom Planeten {1} gestartet werden. [47]
	   */
	public static String AuswertungRaumerNichtGestartet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£47§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungRaumerNichtGestartet_47"), arg0, arg1);
	}

	/**
	   * {1}: {0} Kampfschiffe wurden von Planet {2} vertrieben. [48]
	   */
	public static String AuswertungRaumerVertrieben(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£48§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerVertrieben_48"), arg0, arg1, arg2);
	}

	/**
	   * Sie warten au\u00DFerhalb des Planeten auf ein neues Ziel. [49]
	   */
	public static String AuswertungRaumerVertrieben2(boolean symbol) {
		return symbol ? "£49£":messages.getString("AuswertungRaumerVertrieben2_49");
	}

	/**
	   * Wiedergabe der Auswertung des Jahres {0} [4A]
	   */
	public static String ReplayWiedergabeJahr(boolean symbol, String arg0) {
		return symbol ? "£4A§"+arg0+"£":MessageFormat.format(messages.getString("ReplayWiedergabeJahr_4A"), arg0);
	}

	/**
	   * Schlie\u00DFen [4B]
	   */
	public static String Schliessen(boolean symbol) {
		return symbol ? "£4B£":messages.getString("Schliessen_4B");
	}

	/**
	   * IP-Adresse des Servers [4C]
	   */
	public static String ServerSettingsJDialogIpServer(boolean symbol) {
		return symbol ? "£4C£":messages.getString("ServerSettingsJDialogIpServer_4C");
	}

	/**
	   * Server aktivieren [4D]
	   */
	public static String ServerSettingsJDialogTerminalServerAktiv(boolean symbol) {
		return symbol ? "£4D£":messages.getString("ServerSettingsJDialogTerminalServerAktiv_4D");
	}

	/**
	   * [Unbekannt] [4E]
	   */
	public static String ServerSettingsJDialogUnbekannt(boolean symbol) {
		return symbol ? "£4E£":messages.getString("ServerSettingsJDialogUnbekannt_4E");
	}

	/**
	   * Verbundene STERN Display-Rechner [4F]
	   */
	public static String ServerSettingsJDialogVerbundeneClients(boolean symbol) {
		return symbol ? "£4F£":messages.getString("ServerSettingsJDialogVerbundeneClients_4F");
	}

	/**
	   * Spiel abschlie\u00DFen [4G]
	   */
	public static String SpielAbschliessen(boolean symbol) {
		return symbol ? "£4G£":messages.getString("SpielAbschliessen_4G");
	}

	/**
	   * Wollen Sie das Spiel wirklich abschlie\u00DFen? [4H]
	   */
	public static String SpielAbschliessenFrage(boolean symbol) {
		return symbol ? "£4H£":messages.getString("SpielAbschliessenFrage_4H");
	}

	/**
	   * Spiel laden [4I]
	   */
	public static String SpielLaden(boolean symbol) {
		return symbol ? "£4I£":messages.getString("SpielLaden_4I");
	}

	/**
	   * Spiel speichern [4J]
	   */
	public static String SpielSpeichern(boolean symbol) {
		return symbol ? "£4J£":messages.getString("SpielSpeichern_4J");
	}

	/**
	   * Spieler [4K]
	   */
	public static String Spieler(boolean symbol) {
		return symbol ? "£4K£":messages.getString("Spieler_4K");
	}

	/**
	   * Sind Sie mit diesem Spielfeld einverstanden? [4L]
	   */
	public static String SpielfeldOkFrage(boolean symbol) {
		return symbol ? "£4L£":messages.getString("SpielfeldOkFrage_4L");
	}

	/**
	   * Spielinformationen [4M]
	   */
	public static String Spielinformationen(boolean symbol) {
		return symbol ? "£4M£":messages.getString("Spielinformationen_4M");
	}

	/**
	   * Zeige B\u00FCndnisstruktur auf Planet [4Q]
	   */
	public static String SpielinformationenBuendnisPlanet(boolean symbol) {
		return symbol ? "£4Q£":messages.getString("SpielinformationenBuendnisPlanet_4Q");
	}

	/**
	   * B\u00FCndnisse [4R]
	   */
	public static String SpielinformationenBuendnisse(boolean symbol) {
		return symbol ? "£4R£":messages.getString("SpielinformationenBuendnisse_4R");
	}

	/**
	   * B\u00FCndnisse [4T]
	   */
	public static String SpielinformationenBuendnisseTitel(boolean symbol) {
		return symbol ? "£4T£":messages.getString("SpielinformationenBuendnisseTitel_4T");
	}

	/**
	   * B\u00FCndnisstruktur auf Planet {0} [4U]
	   */
	public static String SpielinformationenBuendnisstruktur(boolean symbol, String arg0) {
		return symbol ? "£4U§"+arg0+"£":MessageFormat.format(messages.getString("SpielinformationenBuendnisstruktur_4U"), arg0);
	}

	/**
	   * $ Produktion [4V]
	   */
	public static String SpielinformationenEnergieproduktion(boolean symbol) {
		return symbol ? "£4V£":messages.getString("SpielinformationenEnergieproduktion_4V");
	}

	/**
	   * $ Produktion der Planeten [4W]
	   */
	public static String SpielinformationenEnergieproduktionTitel(boolean symbol) {
		return symbol ? "£4W£":messages.getString("SpielinformationenEnergieproduktionTitel_4W");
	}

	/**
	   * Abwehrschilde [4X]
	   */
	public static String SpielinformationenFestungen(boolean symbol) {
		return symbol ? "£4X£":messages.getString("SpielinformationenFestungen_4X");
	}

	/**
	   * Abwehrschilde [4Y]
	   */
	public static String SpielinformationenFestungenTitel(boolean symbol) {
		return symbol ? "£4Y£":messages.getString("SpielinformationenFestungenTitel_4Y");
	}

	/**
	   * Auf Planet {0} gibt es kein B\u00FCndnis. [4Z]
	   */
	public static String SpielinformationenKeinBuendnis(boolean symbol, String arg0) {
		return symbol ? "£4Z§"+arg0+"£":MessageFormat.format(messages.getString("SpielinformationenKeinBuendnis_4Z"), arg0);
	}

	/**
	   * Tja, {0}. Das Spiel ist aus! [57]
	   */
	public static String AuswertungSpielerTot(boolean symbol, String arg0) {
		return symbol ? "£57§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungSpielerTot_57"), arg0);
	}

	/**
	   * {0}: 1 Transporter auf Planet {1} angekommen. [58]
	   */
	public static String AuswertungTransporterAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£58§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungTransporterAngekommen_58"), arg0, arg1);
	}

	/**
	   * {0}: 1 Transporter auf Planet {1} zerschellt. [59]
	   */
	public static String AuswertungTransporterZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£59§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungTransporterZerschellt_59"), arg0, arg1);
	}

	/**
	   * Es gibt keine Planeten mit B\u00FCndnissen. [5B]
	   */
	public static String SpielinformationenKeinePlanetenMitBuendnissen(boolean symbol) {
		return symbol ? "£5B£":messages.getString("SpielinformationenKeinePlanetenMitBuendnissen_5B");
	}

	/**
	   * Kommandozentr. [5C]
	   */
	public static String SpielinformationenKommandozentralen(boolean symbol) {
		return symbol ? "£5C£":messages.getString("SpielinformationenKommandozentralen_5C");
	}

	/**
	   * Kommandozentralen [5D]
	   */
	public static String SpielinformationenKommandozentralenTitel(boolean symbol) {
		return symbol ? "£5D£":messages.getString("SpielinformationenKommandozentralenTitel_5D");
	}

	/**
	   * Die K.-Zentrale von {0} ist unterwegs zu Planet {1}. [5E]
	   */
	public static String SpielinformationenKommandozentralenUnterwegs(boolean symbol, String arg0, String arg1) {
		return symbol ? "£5E§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("SpielinformationenKommandozentralenUnterwegs_5E"), arg0, arg1);
	}

	/**
	   * Minenfelder [5F]
	   */
	public static String SpielinformationenMinenfelder(boolean symbol) {
		return symbol ? "£5F£":messages.getString("SpielinformationenMinenfelder_5F");
	}

	/**
	   * Minenfelder [5G]
	   */
	public static String SpielinformationenMinenfelderTitel(boolean symbol) {
		return symbol ? "£5G£":messages.getString("SpielinformationenMinenfelderTitel_5G");
	}

	/**
	   * Patrouillen [5H]
	   */
	public static String SpielinformationenPatrouillen(boolean symbol) {
		return symbol ? "£5H£":messages.getString("SpielinformationenPatrouillen_5H");
	}

	/**
	   * Patrouillen [5I]
	   */
	public static String SpielinformationenPatrouillenTitel(boolean symbol) {
		return symbol ? "£5I£":messages.getString("SpielinformationenPatrouillenTitel_5I");
	}

	/**
	   * Planet [5J]
	   */
	public static String SpielinformationenPlanet(boolean symbol) {
		return symbol ? "£5J£":messages.getString("SpielinformationenPlanet_5J");
	}

	/**
	   * Planet [5K]
	   */
	public static String SpielinformationenPlanetTitel(boolean symbol) {
		return symbol ? "£5K£":messages.getString("SpielinformationenPlanetTitel_5K");
	}

	/**
	   * Spielleiter [5L]
	   */
	public static String Spielleiter(boolean symbol) {
		return symbol ? "£5L£":messages.getString("Spielleiter_5L");
	}

	/**
	   * Spielparameter [5M]
	   */
	public static String Spielparameter(boolean symbol) {
		return symbol ? "£5M£":messages.getString("Spielparameter_5M");
	}

	/**
	   * Auto-Save [5O]
	   */
	public static String SpielparameterJDialogAutoSave(boolean symbol) {
		return symbol ? "£5O£":messages.getString("SpielparameterJDialogAutoSave_5O");
	}

	/**
	   * Bot [5P]
	   */
	public static String SpielparameterJDialogBot(boolean symbol) {
		return symbol ? "£5P£":messages.getString("SpielparameterJDialogBot_5P");
	}

	/**
	   * E-Mail-Einstellungen [5Q]
	   */
	public static String SpielparameterJDialogEMailEinstellungen(boolean symbol) {
		return symbol ? "£5Q£":messages.getString("SpielparameterJDialogEMailEinstellungen_5Q");
	}

	/**
	   * E-Mail-Modus [5R]
	   */
	public static String SpielparameterJDialogEmailModus(boolean symbol) {
		return symbol ? "£5R£":messages.getString("SpielparameterJDialogEmailModus_5R");
	}

	/**
	   * Farbe [5S]
	   */
	public static String SpielparameterJDialogFarbe(boolean symbol) {
		return symbol ? "£5S£":messages.getString("SpielparameterJDialogFarbe_5S");
	}

	/**
	   * Der Spielername [{0}] ist unzul\u00E4ssig.\nEin Spielername muss zwischen {1} und {2} Zeichen lang sein\nund darf nur die Zeichen a-z, A-Z und 0-9 enthalten. [5T]
	   */
	public static String SpielparameterJDialogNameZuLang(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£5T§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("SpielparameterJDialogNameZuLang_5T"), arg0, arg1, arg2);
	}

	/**
	   * STERN LIGHT [5U]
	   */
	public static String SpielparameterJDialogSimpelStern(boolean symbol) {
		return symbol ? "£5U£":messages.getString("SpielparameterJDialogSimpelStern_5U");
	}

	/**
	   * Spiele bis Jahr [5V]
	   */
	public static String SpielparameterJDialogSpieleBisJahr(boolean symbol) {
		return symbol ? "£5V£":messages.getString("SpielparameterJDialogSpieleBisJahr_5V");
	}

	/**
	   * Die E-Mail-Adresse des Spielers [{0}] ist ung\u00FCltig. [5W]
	   */
	public static String SpielparameterJDialogSpielerEMail(boolean symbol, String arg0) {
		return symbol ? "£5W§"+arg0+"£":MessageFormat.format(messages.getString("SpielparameterJDialogSpielerEMail_5W"), arg0);
	}

	/**
	   * Die E-Mail-Adresse des Spielleiters ist ung\u00FCltig. [5X]
	   */
	public static String SpielparameterJDialogSpielleiterEMail(boolean symbol) {
		return symbol ? "£5X£":messages.getString("SpielparameterJDialogSpielleiterEMail_5X");
	}

	/**
	   * Unendlich [5Y]
	   */
	public static String SpielparameterJDialogUnendlich(boolean symbol) {
		return symbol ? "£5Y£":messages.getString("SpielparameterJDialogUnendlich_5Y");
	}

	/**
	   * Auswertung wiederholen [60]
	   */
	public static String AuswertungWiederholen(boolean symbol) {
		return symbol ? "£60£":messages.getString("AuswertungWiederholen_60");
	}

	/**
	   * Bestenliste [61]
	   */
	public static String Bestenliste(boolean symbol) {
		return symbol ? "£61£":messages.getString("Bestenliste_61");
	}

	/**
	   * STERN Display ist nicht am Server {0} registriert [62]
	   */
	public static String ClientSettingsJDialogClientNichtRegistriert(boolean symbol, String arg0) {
		return symbol ? "£62§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogClientNichtRegistriert_62"), arg0);
	}

	/**
	   * Es konnte keine Verbindung zum Server aufgebaut werden. Fehlermeldung:\n\n{0} [63]
	   */
	public static String ClientSettingsJDialogKeineVerbindung(boolean symbol, String arg0) {
		return symbol ? "£63§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogKeineVerbindung_63"), arg0);
	}

	/**
	   * Mein Name [64]
	   */
	public static String ClientSettingsJDialogMeinName(boolean symbol) {
		return symbol ? "£64£":messages.getString("ClientSettingsJDialogMeinName_64");
	}

	/**
	   * Nicht verbunden [65]
	   */
	public static String ClientSettingsJDialogNichtVerbunden(boolean symbol) {
		return symbol ? "£65£":messages.getString("ClientSettingsJDialogNichtVerbunden_65");
	}

	/**
	   * Server {0} ist nicht erreichbar [66]
	   */
	public static String ClientSettingsJDialogServerNichtErreichbar(boolean symbol, String arg0) {
		return symbol ? "£66§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogServerNichtErreichbar_66"), arg0);
	}

	/**
	   * Verbindungseinstellungen [67]
	   */
	public static String ClientSettingsJDialogTitel(boolean symbol) {
		return symbol ? "£67£":messages.getString("ClientSettingsJDialogTitel_67");
	}

	/**
	   * Verbinden [68]
	   */
	public static String ClientSettingsJDialogVerbinden(boolean symbol) {
		return symbol ? "£68£":messages.getString("ClientSettingsJDialogVerbinden_68");
	}

	/**
	   * Verbindungsstatus [69]
	   */
	public static String ClientSettingsJDialogVerbindungsstatus(boolean symbol) {
		return symbol ? "£69£":messages.getString("ClientSettingsJDialogVerbindungsstatus_69");
	}

	/**
	   * Statistik [6A]
	   */
	public static String Statistik(boolean symbol) {
		return symbol ? "£6A£":messages.getString("Statistik_6A");
	}

	/**
	   * Jahr -\n [6C]
	   */
	public static String StatistikJahrMinus(boolean symbol) {
		return symbol ? "£6C£":messages.getString("StatistikJahrMinus_6C");
	}

	/**
	   * Jahr +\n [6D]
	   */
	public static String StatistikJahrPlus(boolean symbol) {
		return symbol ? "£6D£":messages.getString("StatistikJahrPlus_6D");
	}

	/**
	   * Max: {0} (Jahr {1}) [6E]
	   */
	public static String StatistikMax(boolean symbol, String arg0, String arg1) {
		return symbol ? "£6E§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("StatistikMax_6E"), arg0, arg1);
	}

	/**
	   * Min: {0} (Jahr {1}) [6F]
	   */
	public static String StatistikMin(boolean symbol, String arg0, String arg1) {
		return symbol ? "£6F§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("StatistikMin_6F"), arg0, arg1);
	}

	/**
	   * Statistik schlie\u00DFen [6G]
	   */
	public static String StatistikSchliessen(boolean symbol) {
		return symbol ? "£6G£":messages.getString("StatistikSchliessen_6G");
	}

	/**
	   * Spiel begonnen am [6H]
	   */
	public static String StatistikSpielBegonnen(boolean symbol) {
		return symbol ? "£6H£":messages.getString("StatistikSpielBegonnen_6H");
	}

	/**
	   * Std. [6I]
	   */
	public static String StatistikStunden(boolean symbol) {
		return symbol ? "£6I£":messages.getString("StatistikStunden_6I");
	}

	/**
	   * $ Prod. im Jahr {0} [6J]
	   */
	public static String StatistikTitelEnergieproduktion(boolean symbol, String arg0) {
		return symbol ? "£6J§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelEnergieproduktion_6J"), arg0);
	}

	/**
	   * Planeten im Jahr {0} [6K]
	   */
	public static String StatistikTitelPlaneten(boolean symbol, String arg0) {
		return symbol ? "£6K§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelPlaneten_6K"), arg0);
	}

	/**
	   * Punkte im Jahr {0} [6L]
	   */
	public static String StatistikTitelPunkte(boolean symbol, String arg0) {
		return symbol ? "£6L§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelPunkte_6L"), arg0);
	}

	/**
	   * Kampfschiffe im Jahr {0} [6M]
	   */
	public static String StatistikTitelRaumer(boolean symbol, String arg0) {
		return symbol ? "£6M§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelRaumer_6M"), arg0);
	}

	/**
	   * STERN Display [6N]
	   */
	public static String SternClientTitel(boolean symbol) {
		return symbol ? "£6N£":messages.getString("SternClientTitel_6N");
	}

	/**
	   * M\u00F6chten Sie STERN Display wirklich verlassen? [6P]
	   */
	public static String SternClientVerlassenFrage(boolean symbol) {
		return symbol ? "£6P£":messages.getString("SternClientVerlassenFrage_6P");
	}

	/**
	   * STERN Display-Server aktiv [6Q]
	   */
	public static String SternTerminalServer(boolean symbol) {
		return symbol ? "£6Q£":messages.getString("SternTerminalServer_6Q");
	}

	/**
	   * STERN Display verlassen [6R]
	   */
	public static String SternThinClientVerlassen(boolean symbol) {
		return symbol ? "£6R£":messages.getString("SternThinClientVerlassen_6R");
	}

	/**
	   * STERN [6S]
	   */
	public static String SternTitel(boolean symbol) {
		return symbol ? "£6S£":messages.getString("SternTitel_6S");
	}

	/**
	   * STERN verlassen [6T]
	   */
	public static String SternVerlassen(boolean symbol) {
		return symbol ? "£6T£":messages.getString("SternVerlassen_6T");
	}

	/**
	   * STERN Display-Server [6U]
	   */
	public static String Terminalserver(boolean symbol) {
		return symbol ? "£6U£":messages.getString("Terminalserver_6U");
	}

	/**
	   * Sicherheitscode [6V]
	   */
	public static String ThinClientCode(boolean symbol) {
		return symbol ? "£6V£":messages.getString("ThinClientCode_6V");
	}

	/**
	   * Transporter [6W]
	   */
	public static String TransporterPlural(boolean symbol) {
		return symbol ? "£6W£":messages.getString("TransporterPlural_6W");
	}

	/**
	   * Ung\u00FCltige Eingabe. [6X]
	   */
	public static String UngueltigeEingabe(boolean symbol) {
		return symbol ? "£6X£":messages.getString("UngueltigeEingabe_6X");
	}

	/**
	   * Client und Server verwenden unterschiedliche STERN-Builds [6Y]
	   */
	public static String UnterschiedlicheBuilds(boolean symbol) {
		return symbol ? "£6Y£":messages.getString("UnterschiedlicheBuilds_6Y");
	}

	/**
	   * Weiter [6Z]
	   */
	public static String Weiter(boolean symbol) {
		return symbol ? "£6Z£":messages.getString("Weiter_6Z");
	}

	/**
	   * Vebunden mit Server {0} [70]
	   */
	public static String ClientSettingsJDialogVerbunden(boolean symbol, String arg0) {
		return symbol ? "£70§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogVerbunden_70"), arg0);
	}

	/**
	   * Daten k\u00F6nnen nicht interpretiert werden. Pr\u00FCfen Sie folgendes:\n\n1. Stammen die Daten aus einer STERN-Email?\n2. Haben Sie eine E-Mail aus einem falschen Kontext verwendet?\n3. Ihr STERN-Build ({0}) und der Build des Absenders (siehe E-Mail) sind zu unterschiedlich.\n\u0009\u0009\u0009\u0009\u0009\u0009 [71]
	   */
	public static String ClipboardImportJDIalogImportFehler(boolean symbol, String arg0) {
		return symbol ? "£71§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportJDIalogImportFehler_71"), arg0);
	}

	/**
	   * Inhalt der Zwischenablage hier einf\u00FCgen [72]
	   */
	public static String ClipboardImportJDIalogInhaltHierEinfuegen(boolean symbol) {
		return symbol ? "£72£":messages.getString("ClipboardImportJDIalogInhaltHierEinfuegen_72");
	}

	/**
	   * Daten aus Zwischenablage importieren [73]
	   */
	public static String ClipboardImportJDIalogTitle(boolean symbol) {
		return symbol ? "£73£":messages.getString("ClipboardImportJDIalogTitle_73");
	}

	/**
	   * Die Datei existiert nicht. [74]
	   */
	public static String DateiExistiertNicht(boolean symbol) {
		return symbol ? "£74£":messages.getString("DateiExistiertNicht_74");
	}

	/**
	   * Die Datei ist keine g\u00FCltige STERN-Spieldatei. [76]
	   */
	public static String DateiNichtGueltig(boolean symbol) {
		return symbol ? "£76£":messages.getString("DateiNichtGueltig_76");
	}

	/**
	   * E-Mail-Adresse [77]
	   */
	public static String EMailAdresse(boolean symbol) {
		return symbol ? "£77£":messages.getString("EMailAdresse_77");
	}

	/**
	   * Einf\u00FCgen [78]
	   */
	public static String Einfuegen(boolean symbol) {
		return symbol ? "£78£":messages.getString("Einfuegen_78");
	}

	/**
	   * Eingabe gesperrt [79]
	   */
	public static String EingabeGesperrt(boolean symbol) {
		return symbol ? "£79£":messages.getString("EingabeGesperrt_79");
	}

	/**
	   * Zugeingabe [7A]
	   */
	public static String Zugeingabe(boolean symbol) {
		return symbol ? "£7A£":messages.getString("Zugeingabe_7A");
	}

	/**
	   * Aktion nicht m\u00F6glich. [7B]
	   */
	public static String ZugeingabeAktionNichtMoeglich(boolean symbol) {
		return symbol ? "£7B£":messages.getString("ZugeingabeAktionNichtMoeglich_7B");
	}

	/**
	   * Maximale Beladung [7C]
	   */
	public static String ZugeingabeAlleEe(boolean symbol) {
		return symbol ? "£7C£":messages.getString("ZugeingabeAlleEe_7C");
	}

	/**
	   * Alle Kampfschiffe [7D]
	   */
	public static String ZugeingabeAlleRaumer(boolean symbol) {
		return symbol ? "£7D£":messages.getString("ZugeingabeAlleRaumer_7D");
	}

	/**
	   * Ankunft: [7E]
	   */
	public static String ZugeingabeAnkunft(boolean symbol) {
		return symbol ? "£7E£":messages.getString("ZugeingabeAnkunft_7E");
	}

	/**
	   * Anzahl [7F]
	   */
	public static String ZugeingabeAnzahl(boolean symbol) {
		return symbol ? "£7F£":messages.getString("ZugeingabeAnzahl_7F");
	}

	/**
	   * Auf fremden Planeten d\u00FCrfen Sie nur Ihr eigene Teilnahme aufk\u00FCndigen. [7G]
	   */
	public static String ZugeingabeAufFremdenPlanetenNurKuendigen(boolean symbol) {
		return symbol ? "£7G£":messages.getString("ZugeingabeAufFremdenPlanetenNurKuendigen_7G");
	}

	/**
	   * Aufkl\u00E4rer [7H]
	   */
	public static String ZugeingabeAufklaerer(boolean symbol) {
		return symbol ? "£7H£":messages.getString("ZugeingabeAufklaerer_7H");
	}

	/**
	   * Zugeingabe beenden [7I]
	   */
	public static String ZugeingabeBeenden(boolean symbol) {
		return symbol ? "£7I£":messages.getString("ZugeingabeBeenden_7I");
	}

	/**
	   * Wollen Sie die Zugeingabe wirklich beenden? [7J]
	   */
	public static String ZugeingabeBeendenFrage(boolean symbol) {
		return symbol ? "£7J£":messages.getString("ZugeingabeBeendenFrage_7J");
	}

	/**
	   * B\u00FCndnisflotte [7K]
	   */
	public static String ZugeingabeBuendRaumer(boolean symbol) {
		return symbol ? "£7K£":messages.getString("ZugeingabeBuendRaumer_7K");
	}

	/**
	   * B\u00FCndnis [7L]
	   */
	public static String ZugeingabeBuendnis(boolean symbol) {
		return symbol ? "£7L£":messages.getString("ZugeingabeBuendnis_7L");
	}

	/**
	   * E-Mail-Aktionen [7M]
	   */
	public static String ZugeingabeEMailAktionen(boolean symbol) {
		return symbol ? "£7M£":messages.getString("ZugeingabeEMailAktionen_7M");
	}

	/**
	   * Spiel: {0}\nJahr: {1}\nSpielz\u00FCge von: {2}\nBuild: {3}\n\nHallo Spielleiter,\n\nhier sind die Spielz\u00FCge von {4}. Bitte f\u00FChren Sie folgende Schritte aus:\n\n1. Markieren Sie diesen gesamten E-Mail-Text (z.B. mit Strg + A) und kopieren Sie ihn in die Zwischenablage Ihres Rechners (z.B. mit Strg + C).\n\n2. Starten Sie Stern. Laden Sie das Spiel {5}.stn\n\n3. Gehen Sie zur Zugeingabe. W\u00E4hlen Sie "E-Mail-Aktionen -> Spielz\u00FCge eines Spielers importieren".\n\n4. Dr\u00FCcken Sie den Knopf "Einf\u00FCgen", um den Inhalt der Zwischenablage in das Textfeld einzuf\u00FCgen.\n\n5. Dr\u00FCcken Sie den Knopf 'OK'. [7N]
	   */
	public static String ZugeingabeEMailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£7N§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("ZugeingabeEMailBody_7N"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * Bitte warten Sie auf die n\u00E4chste E-Mail vom Spielleiter. [7O]
	   */
	public static String ZugeingabeEMailEndlosschleife(boolean symbol) {
		return symbol ? "£7O£":messages.getString("ZugeingabeEMailEndlosschleife_7O");
	}

	/**
	   * Spiel: {0}\nJahr: {1}\nBuild: {2}\n\nHallo {3},\n\nhier sind die aktuellen Spieldaten f\u00FCr Ihr Stern-E-Mail-Spiel. Bitte f\u00FChren Sie folgende Schritte aus:\n\n1. Markieren Sie diesen gesamten E-Mail-Text (z.B. mit Strg + A) und kopieren Sie ihn in die Zwischenablage Ihres Rechners (z.B. mit Strg + C).\n\n2. Starten Sie Stern und w\u00E4hlen Sie "Spiel -> E-Mail-Spiel aus Zwischenablage laden".\n\n3. Dr\u00FCcken Sie den Knopf "Einf\u00FCgen", um den Inhalt der Zwischenablage in das Textfeld einzuf\u00FCgen.\n\n4. Dr\u00FCcken Sie den Knopf "OK".\n\nDann geben Sie Ihre Spielz\u00FCge ein. Wenn Sie damit fertig sind, \u00F6ffnet sich eine weitere E-Mail in Ihrem E-Mail-Client. Diese weitere E-Mail, die Ihre Spielz\u00FCge enth\u00E4lt und an mich adressiert ist, schicken Sie bitte unver\u00E4ndert ab.\n\nDanke und viel Spa\u00DF!\nIhr Spielleiter [7P]
	   */
	public static String ZugeingabeEMailBody2(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£7P§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("ZugeingabeEMailBody2_7P"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Es wurde eine E-Mail in Ihrem Standard-E-Mail-Client erzeugt. [7Q]
	   */
	public static String ZugeingabeEMailErzeugt(boolean symbol) {
		return symbol ? "£7Q£":messages.getString("ZugeingabeEMailErzeugt_7Q");
	}

	/**
	   * Bitte schicken Sie die Mail unver\u00E4ndert an den Spielleiter. [7R]
	   */
	public static String ZugeingabeEMailErzeugt2(boolean symbol) {
		return symbol ? "£7R£":messages.getString("ZugeingabeEMailErzeugt2_7R");
	}

	/**
	   * Es wurden {0} E-Mail(s) in Ihrem Standard-E-Mail-Client erzeugt. [7S]
	   */
	public static String ZugeingabeEMailErzeugt3(boolean symbol, String arg0) {
		return symbol ? "£7S§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeEMailErzeugt3_7S"), arg0);
	}

	/**
	   * Bitte schicken Sie die Mail(s) unver\u00E4ndert an die Spieler. [7T]
	   */
	public static String ZugeingabeEMailErzeugt4(boolean symbol) {
		return symbol ? "£7T£":messages.getString("ZugeingabeEMailErzeugt4_7T");
	}

	/**
	   * Fertig [7U]
	   */
	public static String ZugeingabeFertig(boolean symbol) {
		return symbol ? "£7U£":messages.getString("ZugeingabeFertig_7U");
	}

	/**
	   * Info [7V]
	   */
	public static String ZugeingabeInfo(boolean symbol) {
		return symbol ? "£7V£":messages.getString("ZugeingabeInfo_7V");
	}

	/**
	   * Inventur [7W]
	   */
	public static String ZugeingabeInventur(boolean symbol) {
		return symbol ? "£7W£":messages.getString("ZugeingabeInventur_7W");
	}

	/**
	   * Kein B\u00FCndnis. [7X]
	   */
	public static String ZugeingabeKeinBuendnis(boolean symbol) {
		return symbol ? "£7X£":messages.getString("ZugeingabeKeinBuendnis_7X");
	}

	/**
	   * Sie sind kein B\u00FCndnismitglied auf diesem Planeten. [7Y]
	   */
	public static String ZugeingabeKeinBuendnismitglied(boolean symbol) {
		return symbol ? "£7Y£":messages.getString("ZugeingabeKeinBuendnismitglied_7Y");
	}

	/**
	   * Auf dem Startplaneten befindet sich keine Kommandozentrale. [7Z]
	   */
	public static String ZugeingabeKeineKommandozentrale(boolean symbol) {
		return symbol ? "£7Z£":messages.getString("ZugeingabeKeineKommandozentrale_7Z");
	}

	/**
	   * Bisher verwendete E-Mail-Adressen [80]
	   */
	public static String EmailAdressenJDialogTitel(boolean symbol) {
		return symbol ? "£80£":messages.getString("EmailAdressenJDialogTitel_80");
	}

	/**
	   * E-Mail-Modus-Einstellungen [81]
	   */
	public static String EmailSettingsJDialogTitel(boolean symbol) {
		return symbol ? "£81£":messages.getString("EmailSettingsJDialogTitel_81");
	}

	/**
	   * $ Produktion [82]
	   */
	public static String Energieproduktion(boolean symbol) {
		return symbol ? "£82£":messages.getString("Energieproduktion_82");
	}

	/**
	   * Distanzmatrix [83]
	   */
	public static String Entfernungstabelle(boolean symbol) {
		return symbol ? "£83£":messages.getString("Entfernungstabelle_83");
	}

	/**
	   * Fehler [84]
	   */
	public static String Fehler(boolean symbol) {
		return symbol ? "£84£":messages.getString("Fehler_84");
	}

	/**
	   * Fehler beim Laden [85]
	   */
	public static String FehlerBeimLaden(boolean symbol) {
		return symbol ? "£85£":messages.getString("FehlerBeimLaden_85");
	}

	/**
	   * Hauptmen\u00FC [86]
	   */
	public static String Hauptmenue(boolean symbol) {
		return symbol ? "£86£":messages.getString("Hauptmenue_86");
	}

	/**
	   * M\u00F6chten Sie die Ergebnisse in die Bestenliste eintragen? [87]
	   */
	public static String HighscoreFrage(boolean symbol) {
		return symbol ? "£87£":messages.getString("HighscoreFrage_87");
	}

	/**
	   * Ankunft [88]
	   */
	public static String InventurAnkunft(boolean symbol) {
		return symbol ? "£88£":messages.getString("InventurAnkunft_88");
	}

	/**
	   * J{0}T{1} [89]
	   */
	public static String InventurAnkunftJahr(boolean symbol, String arg0, String arg1) {
		return symbol ? "£89§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("InventurAnkunftJahr_89"), arg0, arg1);
	}

	/**
	   * Es sind keine Spielz\u00FCge vorhanen. [8A]
	   */
	public static String ZugeingabeKeineSpielzuege(boolean symbol) {
		return symbol ? "£8A£":messages.getString("ZugeingabeKeineSpielzuege_8A");
	}

	/**
	   * Kommandozentrale verlegen [8B]
	   */
	public static String ZugeingabeKommandozentraleVerlegen(boolean symbol) {
		return symbol ? "£8B£":messages.getString("ZugeingabeKommandozentraleVerlegen_8B");
	}

	/**
	   * K\u00FCndigen [8C]
	   */
	public static String ZugeingabeKuendigen(boolean symbol) {
		return symbol ? "£8C£":messages.getString("ZugeingabeKuendigen_8C");
	}

	/**
	   * Sie k\u00F6nnen maximal {0} Kampfschiffe starten. [8D]
	   */
	public static String ZugeingabeMaxAnzahlRaumer(boolean symbol, String arg0) {
		return symbol ? "£8D§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeMaxAnzahlRaumer_8D"), arg0);
	}

	/**
	   * Mine [8E]
	   */
	public static String ZugeingabeMine(boolean symbol) {
		return symbol ? "£8E£":messages.getString("ZugeingabeMine_8E");
	}

	/**
	   * 250er-Mine [8F]
	   */
	public static String ZugeingabeMine250(boolean symbol) {
		return symbol ? "£8F£":messages.getString("ZugeingabeMine250_8F");
	}

	/**
	   * 50er-Mine [8G]
	   */
	public static String ZugeingabeMine50(boolean symbol) {
		return symbol ? "£8G£":messages.getString("ZugeingabeMine50_8G");
	}

	/**
	   * 500er-Mine [8H]
	   */
	public static String ZugeingabeMine500(boolean symbol) {
		return symbol ? "£8H£":messages.getString("ZugeingabeMine500_8H");
	}

	/**
	   * Welcher Typ? [8I]
	   */
	public static String ZugeingabeMineTypFrage(boolean symbol) {
		return symbol ? "£8I£":messages.getString("ZugeingabeMineTypFrage_8I");
	}

	/**
	   * Zielsektor/-planet [8J]
	   */
	public static String ZugeingabeMineZielsektor(boolean symbol) {
		return symbol ? "£8J£":messages.getString("ZugeingabeMineZielsektor_8J");
	}

	/**
	   * Minenr\u00E4umer [8K]
	   */
	public static String ZugeingabeMinenraeumer(boolean symbol) {
		return symbol ? "£8K£":messages.getString("ZugeingabeMinenraeumer_8K");
	}

	/**
	   * Einsatz oder Transfer? [8L]
	   */
	public static String ZugeingabeMissionTransferFrage(boolean symbol) {
		return symbol ? "£8L£":messages.getString("ZugeingabeMissionTransferFrage_8L");
	}

	/**
	   * Momentane B\u00FCndnisstruktur [8M]
	   */
	public static String ZugeingabeMomentaneBuendnisstruktur(boolean symbol) {
		return symbol ? "£8M£":messages.getString("ZugeingabeMomentaneBuendnisstruktur_8M");
	}

	/**
	   * Neue B\u00FCndnisstruktur [8N]
	   */
	public static String ZugeingabeNeueBuendnisstruktur(boolean symbol) {
		return symbol ? "£8N£":messages.getString("ZugeingabeNeueBuendnisstruktur_8N");
	}

	/**
	   * So viele Kampfschiffe sind nicht verf\u00FCgbar. [8O]
	   */
	public static String ZugeingabeNichtGenugRaumer(boolean symbol) {
		return symbol ? "£8O£":messages.getString("ZugeingabeNichtGenugRaumer_8O");
	}

	/**
	   * Patrouille [8P]
	   */
	public static String ZugeingabePatrouille(boolean symbol) {
		return symbol ? "£8P£":messages.getString("ZugeingabePatrouille_8P");
	}

	/**
	   * Transfer [8R]
	   */
	public static String ZugeingabePatrouilleTransfer(boolean symbol) {
		return symbol ? "£8R£":messages.getString("ZugeingabePatrouilleTransfer_8R");
	}

	/**
	   * Planet [8S]
	   */
	public static String ZugeingabePlanet(boolean symbol) {
		return symbol ? "£8S£":messages.getString("ZugeingabePlanet_8S");
	}

	/**
	   * Dieser Planet geh\u00F6rt Ihnen nicht. [8T]
	   */
	public static String ZugeingabePlanetGehoertNicht(boolean symbol) {
		return symbol ? "£8T£":messages.getString("ZugeingabePlanetGehoertNicht_8T");
	}

	/**
	   * Planeteninfo [8U]
	   */
	public static String ZugeingabePlaneteninfo(boolean symbol) {
		return symbol ? "£8U£":messages.getString("ZugeingabePlaneteninfo_8U");
	}

	/**
	   * Kampfschiffe [8V]
	   */
	public static String ZugeingabeRaumer(boolean symbol) {
		return symbol ? "£8V£":messages.getString("ZugeingabeRaumer_8V");
	}

	/**
	   * Spielstand an alle Spieler schicken [8W]
	   */
	public static String ZugeingabeSpielstandVerschicken(boolean symbol) {
		return symbol ? "£8W£":messages.getString("ZugeingabeSpielstandVerschicken_8W");
	}

	/**
	   * Die Spielz\u00FCge geh\u00F6ren nicht zu dieser Spielrunde. [8X]
	   */
	public static String ZugeingabeSpielzuegeFalscheRunde(boolean symbol) {
		return symbol ? "£8X£":messages.getString("ZugeingabeSpielzuegeFalscheRunde_8X");
	}

	/**
	   * Spielz\u00FCge eines Spielers importieren [8Y]
	   */
	public static String ZugeingabeSpielzuegeImportieren(boolean symbol) {
		return symbol ? "£8Y£":messages.getString("ZugeingabeSpielzuegeImportieren_8Y");
	}

	/**
	   * Spielz\u00FCge von {0} erfolgreich importiert. [8Z]
	   */
	public static String ZugeingabeSpielzuegeImportiert(boolean symbol, String arg0) {
		return symbol ? "£8Z§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeSpielzuegeImportiert_8Z"), arg0);
	}

	/**
	   * Anzahl [90]
	   */
	public static String InventurAnzahl(boolean symbol) {
		return symbol ? "£90£":messages.getString("InventurAnzahl_90");
	}

	/**
	   * Aufkl\u00E4rer [91]
	   */
	public static String InventurAufklaerer(boolean symbol) {
		return symbol ? "£91£":messages.getString("InventurAufklaerer_91");
	}

	/**
	   * Auf [92]
	   */
	public static String InventurAufklaererKurz(boolean symbol) {
		return symbol ? "£92£":messages.getString("InventurAufklaererKurz_92");
	}

	/**
	   * Besitzer [93]
	   */
	public static String InventurBesitzerKurz(boolean symbol) {
		return symbol ? "£93£":messages.getString("InventurBesitzerKurz_93");
	}

	/**
	   * B\u00FCndnis [94]
	   */
	public static String InventurBuendnis(boolean symbol) {
		return symbol ? "£94£":messages.getString("InventurBuendnis_94");
	}

	/**
	   * B\u00FCndnis [95]
	   */
	public static String InventurBuendnisKurz(boolean symbol) {
		return symbol ? "£95£":messages.getString("InventurBuendnisKurz_95");
	}

	/**
	   * $Pr [96]
	   */
	public static String InventurEnergieproduktionKurz(boolean symbol) {
		return symbol ? "£96£":messages.getString("InventurEnergieproduktionKurz_96");
	}

	/**
	   * $Vor [97]
	   */
	public static String InventurEnergievorratKurz(boolean symbol) {
		return symbol ? "£97£":messages.getString("InventurEnergievorratKurz_97");
	}

	/**
	   * As [98]
	   */
	public static String InventurFestungKurz(boolean symbol) {
		return symbol ? "£98£":messages.getString("InventurFestungKurz_98");
	}

	/**
	   * AsKs [99]
	   */
	public static String InventurFestungRaumerKurz(boolean symbol) {
		return symbol ? "£99£":messages.getString("InventurFestungRaumerKurz_99");
	}

	/**
	   * Spielz\u00FCge wurden nicht importiert. [9A]
	   */
	public static String ZugeingabeSpielzuegeNichtImportiert(boolean symbol) {
		return symbol ? "£9A£":messages.getString("ZugeingabeSpielzuegeNichtImportiert_9A");
	}

	/**
	   * +++ Spielzug wurde registriert +++ [9B]
	   */
	public static String ZugeingabeStartErfolgreich(boolean symbol) {
		return symbol ? "£9B£":messages.getString("ZugeingabeStartErfolgreich_9B");
	}

	/**
	   * Startplanet [9C]
	   */
	public static String ZugeingabeStartplanet(boolean symbol) {
		return symbol ? "£9C£":messages.getString("ZugeingabeStartplanet_9C");
	}

	/**
	   * Spielz\u00FCge eingeben [9D]
	   */
	public static String ZugeingabeTitel(boolean symbol) {
		return symbol ? "£9D£":messages.getString("ZugeingabeTitel_9D");
	}

	/**
	   * Transporter [9E]
	   */
	public static String ZugeingabeTransporter(boolean symbol) {
		return symbol ? "£9E£":messages.getString("ZugeingabeTransporter_9E");
	}

	/**
	   * Undo [9F]
	   */
	public static String ZugeingabeUndo(boolean symbol) {
		return symbol ? "£9F£":messages.getString("ZugeingabeUndo_9F");
	}

	/**
	   * Letzter Spielzug wurde r\u00FCckg\u00E4ngig gemacht. [9G]
	   */
	public static String ZugeingabeUndoErfolg(boolean symbol) {
		return symbol ? "£9G£":messages.getString("ZugeingabeUndoErfolg_9G");
	}

	/**
	   * Letzten Spielzug r\u00FCckg\u00E4ngig machen? [9H]
	   */
	public static String ZugeingabeUndoFrage(boolean symbol) {
		return symbol ? "£9H£":messages.getString("ZugeingabeUndoFrage_9H");
	}

	/**
	   * Folgende Spieler m\u00FCssen noch ihre Spielz\u00FCge eingeben: [9I]
	   */
	public static String ZugeingabeWartenAufSpielzuege(boolean symbol) {
		return symbol ? "£9I£":messages.getString("ZugeingabeWartenAufSpielzuege_9I");
	}

	/**
	   * Wieviele $ (max. {0})? [9J]
	   */
	public static String ZugeingabeWievieleEe(boolean symbol, String arg0) {
		return symbol ? "£9J§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeWievieleEe_9J"), arg0);
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen 100er-Minenleger schicken? [9K]
	   */
	public static String ZugeingabeWohin100erMine(boolean symbol) {
		return symbol ? "£9K£":messages.getString("ZugeingabeWohin100erMine_9K");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen 250er-Minenleger schicken? [9L]
	   */
	public static String ZugeingabeWohin250erMine(boolean symbol) {
		return symbol ? "£9L£":messages.getString("ZugeingabeWohin250erMine_9L");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen 500er-Minenleger schicken? [9M]
	   */
	public static String ZugeingabeWohin500erMine(boolean symbol) {
		return symbol ? "£9M£":messages.getString("ZugeingabeWohin500erMine_9M");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen 50er-Minenleger schicken? [9N]
	   */
	public static String ZugeingabeWohin50erMine(boolean symbol) {
		return symbol ? "£9N£":messages.getString("ZugeingabeWohin50erMine_9N");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen Aufkl\u00E4rer schicken? [9O]
	   */
	public static String ZugeingabeWohinAufklaerer(boolean symbol) {
		return symbol ? "£9O£":messages.getString("ZugeingabeWohinAufklaerer_9O");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen Minenr\u00E4umer schicken? [9P]
	   */
	public static String ZugeingabeWohinMinenraumer(boolean symbol) {
		return symbol ? "£9P£":messages.getString("ZugeingabeWohinMinenraumer_9P");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie eine Patrouille schicken? [9Q]
	   */
	public static String ZugeingabeWohinPatrouille(boolean symbol) {
		return symbol ? "£9Q£":messages.getString("ZugeingabeWohinPatrouille_9Q");
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie {0} Kampfschiffe schicken? [9R]
	   */
	public static String ZugeingabeWohinRaumer(boolean symbol, String arg0) {
		return symbol ? "£9R§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeWohinRaumer_9R"), arg0);
	}

	/**
	   * Auf welchen Planeten m\u00F6chten Sie einen Transporter schicken? [9S]
	   */
	public static String ZugeingabeWohinTransporter(boolean symbol) {
		return symbol ? "£9S£":messages.getString("ZugeingabeWohinTransporter_9S");
	}

	/**
	   * Zielplanet [9T]
	   */
	public static String ZugeingabeZielplanet(boolean symbol) {
		return symbol ? "£9T£":messages.getString("ZugeingabeZielplanet_9T");
	}

	/**
	   * Das ist der Startplanet. W\u00E4hlen Sie einen anderen Planeten. [9U]
	   */
	public static String ZugeingabeZielplanetIstStartplanet(boolean symbol) {
		return symbol ? "£9U£":messages.getString("ZugeingabeZielplanetIstStartplanet_9U");
	}

	/**
	   * So viele $ k\u00F6nnen Sie nicht transportieren. [9V]
	   */
	public static String ZugeingabeZuVielEe(boolean symbol) {
		return symbol ? "£9V£":messages.getString("ZugeingabeZuVielEe_9V");
	}

	/**
	   * Sie m\u00FCssen erst alle Ihre Kampfschiffe abziehen, bevor Sie k\u00FCndigen k\u00F6nnen. [9W]
	   */
	public static String ZugeingabeZuerstKuendigen(boolean symbol) {
		return symbol ? "£9W£":messages.getString("ZugeingabeZuerstKuendigen_9W");
	}

	/**
	   * Zuf\u00E4lliger Spieler [9X]
	   */
	public static String ZugeingabeZufaelligerSpieler(boolean symbol) {
		return symbol ? "£9X£":messages.getString("ZugeingabeZufaelligerSpieler_9X");
	}

	/**
	   * Zur\u00FCck [9Y]
	   */
	public static String Zurueck(boolean symbol) {
		return symbol ? "£9Y£":messages.getString("Zurueck_9Y");
	}

	/**
	   * Auswertung [9Z]
	   */
	public static String Auswertung(boolean symbol) {
		return symbol ? "£9Z£":messages.getString("Auswertung_9Z");
	}

	/**
	   * Keine Verbindung mit Server {0} [AA]
	   */
	public static String ClientSettingsJDialogKeineVerbindung2(boolean symbol, String arg0) {
		return symbol ? "£AA§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogKeineVerbindung2_AA"), arg0);
	}

	/**
	   * Min. [AB]
	   */
	public static String StatistikMinuten(boolean symbol) {
		return symbol ? "£AB£":messages.getString("StatistikMinuten_AB");
	}

	/**
	   * Sie d\u00FCrfen '0' nicht mit anderen Eingaben kombinieren. [AC]
	   */
	public static String ZugeingabeBuendnis0NichtKombinieren(boolean symbol) {
		return symbol ? "£AC£":messages.getString("ZugeingabeBuendnis0NichtKombinieren_AC");
	}

	/**
	   * Keine Spielz\u00FCge vorhanden. M\u00F6chten Sie die Zugeingabe ganz abbrechen? [AD]
	   */
	public static String ZugeingabeKeineSpielzuegeAbbrechen(boolean symbol) {
		return symbol ? "£AD£":messages.getString("ZugeingabeKeineSpielzuegeAbbrechen_AD");
	}

	/**
	   * 100er-Mine [AE]
	   */
	public static String ZugeingabeMine100(boolean symbol) {
		return symbol ? "£AE£":messages.getString("ZugeingabeMine100_AE");
	}

	/**
	   * \u00C4nderungen an den Server schicken [AF]
	   */
	public static String ServerAdminAnDenSeverSchicken(boolean symbol) {
		return symbol ? "£AF£":messages.getString("ServerAdminAnDenSeverSchicken_AF");
	}

	/**
	   * User-ID [AG]
	   */
	public static String UserId(boolean symbol) {
		return symbol ? "£AG£":messages.getString("UserId_AG");
	}

	/**
	   * Name [AH]
	   */
	public static String Name(boolean symbol) {
		return symbol ? "£AH£":messages.getString("Name_AH");
	}

	/**
	   * User [AI]
	   */
	public static String ServerAdminSpieler(boolean symbol) {
		return symbol ? "£AI£":messages.getString("ServerAdminSpieler_AI");
	}

	/**
	   * Server herunterfahren [AJ]
	   */
	public static String ServerAdminShutdown(boolean symbol) {
		return symbol ? "£AJ£":messages.getString("ServerAdminShutdown_AJ");
	}

	/**
	   * Datei [AK]
	   */
	public static String ServerAdminDatei(boolean symbol) {
		return symbol ? "£AK£":messages.getString("ServerAdminDatei_AK");
	}

	/**
	   * Server-URL [AL]
	   */
	public static String ServerAdminUrl(boolean symbol) {
		return symbol ? "£AL£":messages.getString("ServerAdminUrl_AL");
	}

	/**
	   * Server-Port [AM]
	   */
	public static String ServerAdminPort(boolean symbol) {
		return symbol ? "£AM£":messages.getString("ServerAdminPort_AM");
	}

	/**
	   * Verbindungstest [AN]
	   */
	public static String ServerAdminVerbindungstest(boolean symbol) {
		return symbol ? "£AN£":messages.getString("ServerAdminVerbindungstest_AN");
	}

	/**
	   * Admin-Authentifizierung [AO]
	   */
	public static String ServerAdminAdminAuth(boolean symbol) {
		return symbol ? "£AO£":messages.getString("ServerAdminAdminAuth_AO");
	}

	/**
	   * Die Datei {0}\nenth\u00E4lt keine g\u00FCltigen Anmeldedaten. [AP]
	   */
	public static String UngueltigeAnmeldedaten(boolean symbol, String arg0) {
		return symbol ? "£AP§"+arg0+"£":MessageFormat.format(messages.getString("UngueltigeAnmeldedaten_AP"), arg0);
	}

	/**
	   * Verbindung erfolgreich [AQ]
	   */
	public static String VerbindungErfolgreich(boolean symbol) {
		return symbol ? "£AQ£":messages.getString("VerbindungErfolgreich_AQ");
	}

	/**
	   * Verbindung nicht erfolgreich [AR]
	   */
	public static String VerbindungNichtErfolgreich(boolean symbol) {
		return symbol ? "£AR£":messages.getString("VerbindungNichtErfolgreich_AR");
	}

	/**
	   * M\u00F6chten Sie den User [{0}] wirklich anlegen? [AS]
	   */
	public static String ServerAdminBenutzerAnlegenFrage(boolean symbol, String arg0) {
		return symbol ? "£AS§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminBenutzerAnlegenFrage_AS"), arg0);
	}

	/**
	   * M\u00F6chten Sie den STERN Server herunterfahren? [AT]
	   */
	public static String ServerAdminShutdownFrage(boolean symbol) {
		return symbol ? "£AT£":messages.getString("ServerAdminShutdownFrage_AT");
	}

	/**
	   * Sind Sie wirklich sicher? [AU]
	   */
	public static String AreYouSure(boolean symbol) {
		return symbol ? "£AU£":messages.getString("AreYouSure_AU");
	}

	/**
	   * Der STERN Server wird jetzt heruntergefahren... [AV]
	   */
	public static String ServerAdminShutdownDone(boolean symbol) {
		return symbol ? "£AV£":messages.getString("ServerAdminShutdownDone_AV");
	}

	/**
	   * Verbindungsfehler [AW]
	   */
	public static String Verbindungsfehler(boolean symbol) {
		return symbol ? "£AW£":messages.getString("Verbindungsfehler_AW");
	}

	/**
	   * (Keine Datei ausgew\u00E4hlt) [AX]
	   */
	public static String KeineDateiAusgewaehlt(boolean symbol) {
		return symbol ? "£AX£":messages.getString("KeineDateiAusgewaehlt_AX");
	}

	/**
	   * Server-Zugangsdaten [AY]
	   */
	public static String ServerZugangsdaten(boolean symbol) {
		return symbol ? "£AY£":messages.getString("ServerZugangsdaten_AY");
	}

	/**
	   * Sie haben noch keine Server-Zugangsdaten hinterlegt. [AZ]
	   */
	public static String ServerZugangsdatenNichtHinterlegt(boolean symbol) {
		return symbol ? "£AZ£":messages.getString("ServerZugangsdatenNichtHinterlegt_AZ");
	}

	/**
	   * Spiele auf dem STERN Server (Spieler {0}) [BA]
	   */
	public static String ServerbasierteSpiele(boolean symbol, String arg0) {
		return symbol ? "£BA§"+arg0+"£":MessageFormat.format(messages.getString("ServerbasierteSpiele_BA"), arg0);
	}

	/**
	   * STERN Server verwalten [BB]
	   */
	public static String SternServerVerwalten(boolean symbol) {
		return symbol ? "£BB£":messages.getString("SternServerVerwalten_BB");
	}

	/**
	   * Verbunden mit dem STERN Server {0}:{1} als Benutzer {2} [BC]
	   */
	public static String VerbundenMitServer(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£BC§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("VerbundenMitServer_BC"), arg0, arg1, arg2);
	}

	/**
	   * Mitspieler warten auf Ihre Zugeingaben [BD]
	   */
	public static String MitspielerWarten(boolean symbol) {
		return symbol ? "£BD£":messages.getString("MitspielerWarten_BD");
	}

	/**
	   * [STERN] Ihr neuer User [{0}] [BE]
	   */
	public static String EmailSubjectNeuerUser(boolean symbol, String arg0) {
		return symbol ? "£BE§"+arg0+"£":MessageFormat.format(messages.getString("EmailSubjectNeuerUser_BE"), arg0);
	}

	/**
	   * Hallo {0},\n\nwillkommen bei STERN! Ihr neuer User [{1}] auf dem Server {2}:{3} ist angelegt und muss nur noch aktiviert werden.\n\nBitte f\u00FChren Sie dazu folgende Schritte aus:\n\n1. Markieren Sie diesen gesamten E-Mail-Text (z.B. mit Strg + A) und kopieren Sie ihn in die Zwischenablage Ihres Rechners (z.B. mit Strg + C).\n\n2. Starten Sie STERN und w\u00E4hlen Sie "Spiel -> Server-Zugangsdaten...".\n\n3. Setzen Sie das H\u00E4kchen bei "Serververbindung -> Aktivieren"\n\n4. Dr\u00FCcken Sie den Knopf "Benutzer aktivieren" und f\u00FCgen Sie den Inhalt der Zwischenablage in das gro\u00DFe Textfeld ein.\n\n5. Geben Sie das Passwort ein, dass Ihnen vom Spielleiter mitgeteilt wurde.\n\n6. Dr\u00FCcken Sie den Knopf "OK". W\u00E4hlen Sie einen Speicherort f\u00FCr die Datei mir den Zugangsdaten aus.\n\nIhr Benutzer ist nun aktiviert, und Ihre Benutzerdaten sind  in STERN hinterlegt.\n\nViel Spa\u00DF beim Spielen!\nIhr Spielleiter [BF]
	   */
	public static String NeuerUserEMailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£BF§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("NeuerUserEMailBody_BF"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Serververbindung [BG]
	   */
	public static String Serververbindung(boolean symbol) {
		return symbol ? "£BG£":messages.getString("Serververbindung_BG");
	}

	/**
	   * Aktivieren [BH]
	   */
	public static String Aktivieren(boolean symbol) {
		return symbol ? "£BH£":messages.getString("Aktivieren_BH");
	}

	/**
	   * Benutzer aktivieren [BI]
	   */
	public static String BenutzerAktivieren(boolean symbol) {
		return symbol ? "£BI£":messages.getString("BenutzerAktivieren_BI");
	}

	/**
	   * M\u00F6chten Sie Ihren Benutzer [{0}] auf dem Server\n{1}:{2} aktivieren?\nW\u00E4hlen Sie im folgenden Dialog den Ablageort\nf\u00FCr die Authentifizierungsdatei. [BJ]
	   */
	public static String BenutzerAktivierenFrage(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£BJ§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("BenutzerAktivierenFrage_BJ"), arg0, arg1, arg2);
	}

	/**
	   * Authentifizierungsdatei abspeichern [BK]
	   */
	public static String BenutzerAktivierenAbspeichern(boolean symbol) {
		return symbol ? "£BK£":messages.getString("BenutzerAktivierenAbspeichern_BK");
	}

	/**
	   * Der Benutzer wurde erfolgreich aktiviert. [BL]
	   */
	public static String BenutzerAktivierenErfolg(boolean symbol) {
		return symbol ? "£BL£":messages.getString("BenutzerAktivierenErfolg_BL");
	}

	/**
	   * Spieler warten auf mich [BM]
	   */
	public static String ServerGamesSpielerWarten(boolean symbol) {
		return symbol ? "£BM£":messages.getString("ServerGamesSpielerWarten_BM");
	}

	/**
	   * Ich warte auf andere Spieler [BN]
	   */
	public static String ServerGamesIchWarte(boolean symbol) {
		return symbol ? "£BN£":messages.getString("ServerGamesIchWarte_BN");
	}

	/**
	   * Beendete Spiele [BO]
	   */
	public static String ServerGamesBeendeteSpiele(boolean symbol) {
		return symbol ? "£BO£":messages.getString("ServerGamesBeendeteSpiele_BO");
	}

	/**
	   * Neues Spiel [BP]
	   */
	public static String ServerGamesNeuesSpiel(boolean symbol) {
		return symbol ? "£BP£":messages.getString("ServerGamesNeuesSpiel_BP");
	}

	/**
	   * Neues Spielfeld [BQ]
	   */
	public static String ServerGamesNeuesSpielfeld(boolean symbol) {
		return symbol ? "£BQ£":messages.getString("ServerGamesNeuesSpielfeld_BQ");
	}

	/**
	   * Spiel ver\u00F6ffentlichen [BR]
	   */
	public static String ServerGamesSubmit(boolean symbol) {
		return symbol ? "£BR£":messages.getString("ServerGamesSubmit_BR");
	}

	/**
	   * Sie m\u00FCssen allen Spielern Namen zuweisen. [BS]
	   */
	public static String ServerGamesSubmitNamenZuweisen(boolean symbol) {
		return symbol ? "£BS£":messages.getString("ServerGamesSubmitNamenZuweisen_BS");
	}

	/**
	   * M\u00F6chten Sie das neue Spiel [{0}] wirklich auf dem Server ver\u00F6ffentlichen? [BT]
	   */
	public static String ServerGamesSubmitFrage(boolean symbol, String arg0) {
		return symbol ? "£BT§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitFrage_BT"), arg0);
	}

	/**
	   * Das neue Spiel [{0}] wurde auf dem Server angelegt. [BU]
	   */
	public static String ServerGamesSubmitAngelegt(boolean symbol, String arg0) {
		return symbol ? "£BU§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitAngelegt_BU"), arg0);
	}

	/**
	   * Spiel laden [BV]
	   */
	public static String ServerGamesLaden(boolean symbol) {
		return symbol ? "£BV£":messages.getString("ServerGamesLaden_BV");
	}

	/**
	   * {0} Spieler, {1} Planeten [BW]
	   */
	public static String ServerGamesSpielerPlaneten(boolean symbol, String arg0, String arg1) {
		return symbol ? "£BW§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerGamesSpielerPlaneten_BW"), arg0, arg1);
	}

	/**
	   * Beginn: {0} [BY]
	   */
	public static String ServerGamesBegonnen(boolean symbol, String arg0) {
		return symbol ? "£BY§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesBegonnen_BY"), arg0);
	}

	/**
	   * Spielname [BZ]
	   */
	public static String ServerGamesSpielname(boolean symbol) {
		return symbol ? "£BZ£":messages.getString("ServerGamesSpielname_BZ");
	}

	/**
	   * Willkommen beim Einrichten des STERN Servers! [CA]
	   */
	public static String ServerWillkommen(boolean symbol) {
		return symbol ? "£CA£":messages.getString("ServerWillkommen_CA");
	}

	/**
	   * voreingestellt [CB]
	   */
	public static String ServerVoreingestellt(boolean symbol) {
		return symbol ? "£CB£":messages.getString("ServerVoreingestellt_CB");
	}

	/**
	   * E-Mail-Adresse des Admins [CC]
	   */
	public static String ServerEmailAdmin(boolean symbol) {
		return symbol ? "£CC£":messages.getString("ServerEmailAdmin_CC");
	}

	/**
	   * Sind alle Angaben richtig? Ja = [1]/ Nein = [andere Taste] [CD]
	   */
	public static String ServerInitConfirm(boolean symbol) {
		return symbol ? "£CD£":messages.getString("ServerInitConfirm_CD");
	}

	/**
	   * Einrichten des Servers abgebrochen. Programmende. [CE]
	   */
	public static String ServerInitAbort(boolean symbol) {
		return symbol ? "£CE£":messages.getString("ServerInitAbort_CE");
	}

	/**
	   * Starte Server... [CF]
	   */
	public static String ServerStarting(boolean symbol) {
		return symbol ? "£CF£":messages.getString("ServerStarting_CF");
	}

	/**
	   * STERN Server auf Port {0} gestartet [CG]
	   */
	public static String ServerStarted(boolean symbol, String arg0) {
		return symbol ? "£CG§"+arg0+"£":MessageFormat.format(messages.getString("ServerStarted_CG"), arg0);
	}

	/**
	   * STERN Server auf Port {0} kann nicht gestartet werden. Wahrscheinlich ist der Port belegt. Programmende. [CH]
	   */
	public static String ServerNotStarted(boolean symbol, String arg0) {
		return symbol ? "£CH§"+arg0+"£":MessageFormat.format(messages.getString("ServerNotStarted_CH"), arg0);
	}

	/**
	   * Warte auf eingehende Verbindung... [CI]
	   */
	public static String ServerWaiting(boolean symbol) {
		return symbol ? "£CI£":messages.getString("ServerWaiting_CI");
	}

	/**
	   * Eingehende Verbindung von Client IP {0}. Thread {1} wird gestartet. [CJ]
	   */
	public static String ServerIncomingConnection(boolean symbol, String arg0, String arg1) {
		return symbol ? "£CJ§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerIncomingConnection_CJ"), arg0, arg1);
	}

	/**
	   * Schlie\u00DFe Server-Socket. [CK]
	   */
	public static String ServerISocketClose(boolean symbol) {
		return symbol ? "£CK£":messages.getString("ServerISocketClose_CK");
	}

	/**
	   * Datei {0} angelegt. [CL]
	   */
	public static String ServerIDateiAngelegt(boolean symbol, String arg0) {
		return symbol ? "£CL§"+arg0+"£":MessageFormat.format(messages.getString("ServerIDateiAngelegt_CL"), arg0);
	}

	/**
	   * Datum [CM]
	   */
	public static String ServerILogDatum(boolean symbol) {
		return symbol ? "£CM£":messages.getString("ServerILogDatum_CM");
	}

	/**
	   * Event ID [CN]
	   */
	public static String ServerILogEventId(boolean symbol) {
		return symbol ? "£CN£":messages.getString("ServerILogEventId_CN");
	}

	/**
	   * Thread ID [CO]
	   */
	public static String ServerILogThreadId(boolean symbol) {
		return symbol ? "£CO£":messages.getString("ServerILogThreadId_CO");
	}

	/**
	   * Level [CP]
	   */
	public static String ServerILogLevel(boolean symbol) {
		return symbol ? "£CP£":messages.getString("ServerILogLevel_CP");
	}

	/**
	   * Meldung [CQ]
	   */
	public static String ServerILogMeldung(boolean symbol) {
		return symbol ? "£CQ£":messages.getString("ServerILogMeldung_CQ");
	}

	/**
	   * Anmeldungsversuch mit ung\u00FCltiger Benutzernamenl\u00E4nge {0} [CR]
	   */
	public static String ServerErrorUngueltigeLaengeBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CR§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorUngueltigeLaengeBenutzer_CR"), arg0);
	}

	/**
	   * Eingehender Request von User {0} [CS]
	   */
	public static String ServerBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CS§"+arg0+"£":MessageFormat.format(messages.getString("ServerBenutzer_CS"), arg0);
	}

	/**
	   * Anmeldungsversuch mit ung\u00FCltigem Benutzer [{0}] [CT]
	   */
	public static String ServerErrorUngueltigerBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CT§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorUngueltigerBenutzer_CT"), arg0);
	}

	/**
	   * Fehler beim Empfangen des Requests: {0} [CU]
	   */
	public static String ServerErrorRequestReceive(boolean symbol, String arg0) {
		return symbol ? "£CU§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorRequestReceive_CU"), arg0);
	}

	/**
	   * Fehler beim Senden der Responsenachricht: {0} [CV]
	   */
	public static String ServerErrorSendResponse(boolean symbol, String arg0) {
		return symbol ? "£CV§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSendResponse_CV"), arg0);
	}

	/**
	   * Falscher Token. Authorisierung fehlgeschlagen. [CW]
	   */
	public static String ServerErrorNichtAuthorisiert(boolean symbol) {
		return symbol ? "£CW£":messages.getString("ServerErrorNichtAuthorisiert_CW");
	}

	/**
	   * Fehler beim Entschl\u00FCsseln der Requestnachricht: {0} [CX]
	   */
	public static String ServerErrorDecode(boolean symbol, String arg0) {
		return symbol ? "£CX§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorDecode_CX"), arg0);
	}

	/**
	   * Request-Nachricht vom Typ {0} von User {1} [CY]
	   */
	public static String ServerInfoMessageType(boolean symbol, String arg0, String arg1) {
		return symbol ? "£CY§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerInfoMessageType_CY"), arg0, arg1);
	}

	/**
	   * STERN Display-Verbindung IP {0} wird geschlossen. [CZ]
	   */
	public static String ServerInfoClientClosing(boolean symbol, String arg0) {
		return symbol ? "£CZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerInfoClientClosing_CZ"), arg0);
	}

	/**
	   * Fehler beim Schliessen der Socketverbindung: {0} [DA]
	   */
	public static String ServerErrorClientClosing(boolean symbol, String arg0) {
		return symbol ? "£DA§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorClientClosing_DA"), arg0);
	}

	/**
	   * Thread wird beendet. [DB]
	   */
	public static String ServerThreadClosing(boolean symbol) {
		return symbol ? "£DB£":messages.getString("ServerThreadClosing_DB");
	}

	/**
	   * Sie sind als Benutzer [{0}] nicht authorisiert, diese Aktion auszuf\u00FChren. [DC]
	   */
	public static String ServerErrorNotAuthorized(boolean symbol, String arg0) {
		return symbol ? "£DC§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorNotAuthorized_DC"), arg0);
	}

	/**
	   * Der User [{0}] nimmt nicht an diesem Spiel teil. [DD]
	   */
	public static String ServerErrorSpielerNimmNichtTeil(boolean symbol, String arg0) {
		return symbol ? "£DD§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSpielerNimmNichtTeil_DD"), arg0);
	}

	/**
	   * Das Spiel [{0}] existiert nicht! [DE]
	   */
	public static String ServerErrorSpielExistiertNicht(boolean symbol, String arg0) {
		return symbol ? "£DE§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSpielExistiertNicht_DE"), arg0);
	}

	/**
	   * Userdaten sind unvollst\u00E4ndig. [DF]
	   */
	public static String ServerErrorAdminNeuerUser(boolean symbol) {
		return symbol ? "£DF£":messages.getString("ServerErrorAdminNeuerUser_DF");
	}

	/**
	   * Die User-ID [{0}] ist bereits vergeben oder unzul\u00E4ssig.\nEine User-ID muss zwischen {1} und {2} Zeichen lang sein\nund darf nur die Zeichen a-z, A-Z und 0-9 enthalten. [DG]
	   */
	public static String ServerErrorAdminUserUnzulaessig(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£DG§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("ServerErrorAdminUserUnzulaessig_DG"), arg0, arg1, arg2);
	}

	/**
	   * Neuer inaktiver User [{0}] angelegt. [DH]
	   */
	public static String ServerInfoInaktiverBenutzerAngelegt(boolean symbol, String arg0) {
		return symbol ? "£DH§"+arg0+"£":MessageFormat.format(messages.getString("ServerInfoInaktiverBenutzerAngelegt_DH"), arg0);
	}

	/**
	   * Der Benutzer [{0}] wurde bereits aktiviert. [DI]
	   */
	public static String ServerErrorBenutzerBereitsAktiviert(boolean symbol, String arg0) {
		return symbol ? "£DI§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorBenutzerBereitsAktiviert_DI"), arg0);
	}

	/**
	   * Das Jahr wurde bereits ausgewertet. [DK]
	   */
	public static String ServerErrorJahrVorbei(boolean symbol) {
		return symbol ? "£DK£":messages.getString("ServerErrorJahrVorbei_DK");
	}

	/**
	   * Die Spielz\u00FCge wurden erfolgreich an den Server \u00FCbermittelt. [DM]
	   */
	public static String ZugeingabePostMovesSuccess(boolean symbol) {
		return symbol ? "£DM£":messages.getString("ZugeingabePostMovesSuccess_DM");
	}

	/**
	   * Die Spielz\u00FCge konnten nicht an den Server \u00FCbermittelt werden. [DN]
	   */
	public static String ZugeingabePostMovesError(boolean symbol) {
		return symbol ? "£DN£":messages.getString("ZugeingabePostMovesError_DN");
	}

	/**
	   * Taste [DO]
	   */
	public static String Taste(boolean symbol) {
		return symbol ? "£DO£":messages.getString("Taste_DO");
	}

	/**
	   * Nochmal versuchen [DP]
	   */
	public static String NochmalVersuchen(boolean symbol) {
		return symbol ? "£DP£":messages.getString("NochmalVersuchen_DP");
	}

	/**
	   * Bitte warten Sie, bis das Neuladen-Symbol erscheint. [DQ]
	   */
	public static String WartenBisNaechsteZugeingabe(boolean symbol) {
		return symbol ? "£DQ£":messages.getString("WartenBisNaechsteZugeingabe_DQ");
	}

	/**
	   * Die Auswertung des Jahres ist verf\u00FCgbar. [DR]
	   */
	public static String AuswertungVerfuegbar(boolean symbol) {
		return symbol ? "£DR£":messages.getString("AuswertungVerfuegbar_DR");
	}

	/**
	   * Spracheinstellungen [DS]
	   */
	public static String SpracheDialogTitle(boolean symbol) {
		return symbol ? "£DS£":messages.getString("SpracheDialogTitle_DS");
	}

	/**
	   * Sprache [DT]
	   */
	public static String Sprache(boolean symbol) {
		return symbol ? "£DT£":messages.getString("Sprache_DT");
	}

	/**
	   * Die neuen Spracheinstellungen werden erst nach einem Neustart der Anwendung wirksam. Die Anwendung wird nun geschlossen. [DU]
	   */
	public static String SpracheDialogFrage(boolean symbol) {
		return symbol ? "£DU£":messages.getString("SpracheDialogFrage_DU");
	}

	/**
	   * Sprache / Language [DV]
	   */
	public static String MenuSpracheinstellungen(boolean symbol) {
		return symbol ? "£DV£":messages.getString("MenuSpracheinstellungen_DV");
	}

	/**
	   * Andere Taste [DW]
	   */
	public static String AndereTaste(boolean symbol) {
		return symbol ? "£DW£":messages.getString("AndereTaste_DW");
	}

	/**
	   * Serverkommunikation ist deaktiviert. [DX]
	   */
	public static String ServerKommunikationInaktiv(boolean symbol) {
		return symbol ? "£DX£":messages.getString("ServerKommunikationInaktiv_DX");
	}

	/**
	   * Minenr\u00E4umer (Transfer) [E0]
	   */
	public static String InventurMinenraeumerTransfer(boolean symbol) {
		return symbol ? "£E0£":messages.getString("InventurMinenraeumerTransfer_E0");
	}

	/**
	   * Ziel eines Transfers muss ein Planet sein. [E1]
	   */
	public static String ZugeingabeZielTransfer(boolean symbol) {
		return symbol ? "£E1£":messages.getString("ZugeingabeZielTransfer_E1");
	}

	/**
	   * Daten k\u00F6nnen nicht interpretiert werden. Pr\u00FCfen Sie folgendes:\n\n1. Stammen die Daten aus einer STERN-Email?\n2. Ist das Passwort korrekt?\n3. Haben Sie eine E-Mail aus einem falschen Kontext verwendet?\n4. Ihr STERN-Build ({0}) und der Build des Absenders (siehe E-Mail) sind zu unterschiedlich.\n\u0009\u0009\u0009\u0009\u0009\u0009 [E2]
	   */
	public static String ClipboardImportJDIalogImportFehlerPassword(boolean symbol, String arg0) {
		return symbol ? "£E2§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportJDIalogImportFehlerPassword_E2"), arg0);
	}

	/**
	   * Aktivierungspasswort [E3]
	   */
	public static String Aktivierungspasswort(boolean symbol) {
		return symbol ? "£E3£":messages.getString("Aktivierungspasswort_E3");
	}

	/**
	   * (Passwort wiederholen) [E5]
	   */
	public static String PasswortWiederholen(boolean symbol) {
		return symbol ? "£E5£":messages.getString("PasswortWiederholen_E5");
	}

	/**
	   * Die Passw\u00F6rter sind unterschiedlich. [E6]
	   */
	public static String PasswoerterUnterschiedlich(boolean symbol) {
		return symbol ? "£E6£":messages.getString("PasswoerterUnterschiedlich_E6");
	}

	/**
	   * Das Aktivierungspasswort muss mindestens 3 Zeichen lang sein. [E7]
	   */
	public static String PasswortZuKurz(boolean symbol) {
		return symbol ? "£E7£":messages.getString("PasswortZuKurz_E7");
	}

	/**
	   * Passwort [E8]
	   */
	public static String Passwort(boolean symbol) {
		return symbol ? "£E8£":messages.getString("Passwort_E8");
	}

	/**
	   * Neutrale Pl. [EC]
	   */
	public static String SpielinformationenNeutralePlaneten(boolean symbol) {
		return symbol ? "£EC£":messages.getString("SpielinformationenNeutralePlaneten_EC");
	}

	/**
	   * Neutrale Planeten [ED]
	   */
	public static String SpielinformationenNeutralePlanetenTitel(boolean symbol) {
		return symbol ? "£ED£":messages.getString("SpielinformationenNeutralePlanetenTitel_ED");
	}

	/**
	   * Der Server setzt mindestens Build {0} voraus. Sie verwenden Build {1}. [EE]
	   */
	public static String ServerBuildFalsch(boolean symbol, String arg0, String arg1) {
		return symbol ? "£EE§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerBuildFalsch_EE"), arg0, arg1);
	}

	/**
	   * STERN Display-Server [EF]
	   */
	public static String SternScreenSharingServer(boolean symbol) {
		return symbol ? "£EF£":messages.getString("SternScreenSharingServer_EF");
	}

	/**
	   * [STERN] {0} hat Sie zum neuen Spiel {1} eingeladen [EG]
	   */
	public static String EmailSubjectEingeladen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£EG§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("EmailSubjectEingeladen_EG"), arg0, arg1);
	}

	/**
	   * Hallo,\n\nwillkommen bei STERN! {0} hat Sie zum Spiel {1} auf dem Server {2}:{3} eingeladen.\n\nViel Spa\u00DF beim Spielen!\nIhr Spielleiter [EH]
	   */
	public static String EmailBodyEingeladen(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£EH§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("EmailBodyEingeladen_EH"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Sie sind nicht als Spieler {0} am STERN Server angemeldet. [EI]
	   */
	public static String SpielerNichtAngemeldet(boolean symbol, String arg0) {
		return symbol ? "£EI§"+arg0+"£":MessageFormat.format(messages.getString("SpielerNichtAngemeldet_EI"), arg0);
	}

	/**
	   * {0}: Spionagesender installiert. [EJ]
	   */
	public static String AuswertungAufklaererSender(boolean symbol, String arg0) {
		return symbol ? "£EJ§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungAufklaererSender_EJ"), arg0);
	}

	/**
	   * Mehr... [EK]
	   */
	public static String ZugeingabeMehr(boolean symbol) {
		return symbol ? "£EK£":messages.getString("ZugeingabeMehr_EK");
	}

	/**
	   * Kampfschiffe auf Planet anzeigen [EL]
	   */
	public static String ZugeingabeRaumerAufPlanet(boolean symbol) {
		return symbol ? "£EL£":messages.getString("ZugeingabeRaumerAufPlanet_EL");
	}

	/**
	   * Auf dem Planeten befinden sich {0} Kampfschiffe. [EM]
	   */
	public static String ZugeingabeRaumerAnzeigen(boolean symbol, String arg0) {
		return symbol ? "£EM§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeRaumerAnzeigen_EM"), arg0);
	}

	/**
	   * Klicken Sie auf das blinkende Neuladen-Symbol. [EN]
	   */
	public static String AuswertungVerfuegbar2(boolean symbol) {
		return symbol ? "£EN£":messages.getString("AuswertungVerfuegbar2_EN");
	}

	/**
	   * Das neue Spiel [{0}] wurde auf dem Server angelegt. Sie k\u00F6nnen nachfolgend eine Einladungsmail an Ihre Mitspieler schicken. [EO]
	   */
	public static String ServerGamesSubmitAngelegt2(boolean symbol, String arg0) {
		return symbol ? "£EO§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitAngelegt2_EO"), arg0);
	}

	/**
	   * Dies war die letzte Zugeingabe dieses Spiels. [EP]
	   */
	public static String LetztesJahr(boolean symbol) {
		return symbol ? "£EP£":messages.getString("LetztesJahr_EP");
	}

	/**
	   * Sie finden das beendete Spiel demn\u00E4chst unter "Meine serverbasierten Spiele". [EQ]
	   */
	public static String LetztesJahr2(boolean symbol) {
		return symbol ? "£EQ£":messages.getString("LetztesJahr2_EQ");
	}

	/**
	   * Beendetes Spiel im Jahr {0} [ER]
	   */
	public static String AbgeschlossenesSpiel(boolean symbol, String arg0) {
		return symbol ? "£ER§"+arg0+"£":MessageFormat.format(messages.getString("AbgeschlossenesSpiel_ER"), arg0);
	}

	/**
	   * Kapitulieren [ET]
	   */
	public static String ZugeingabeKapitulieren(boolean symbol) {
		return symbol ? "£ET£":messages.getString("ZugeingabeKapitulieren_ET");
	}

	/**
	   * Spieler {0} hat kapituliert. [EU]
	   */
	public static String AuswertungKapitulation(boolean symbol, String arg0) {
		return symbol ? "£EU§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungKapitulation_EU"), arg0);
	}

	/**
	   * Ein Update ist verf\u00FCgbar [EV]
	   */
	public static String UpdateVerfuegbar(boolean symbol) {
		return symbol ? "£EV£":messages.getString("UpdateVerfuegbar_EV");
	}

	/**
	   * Update [EX]
	   */
	public static String Update(boolean symbol) {
		return symbol ? "£EX£":messages.getString("Update_EX");
	}

	/**
	   * Sie haben Ihre Spielz\u00FCge bereits eingegeben. Bitte warten Sie auf die Auswertung. [EY]
	   */
	public static String ZugeingabeSpielzuegeSchonEingegeben(boolean symbol) {
		return symbol ? "£EY£":messages.getString("ZugeingabeSpielzuegeSchonEingegeben_EY");
	}

	/**
	   * Anwendungsfehler auf dem Server:\n{0} [EZ]
	   */
	public static String ServerAnwendungsfehler(boolean symbol, String arg0) {
		return symbol ? "£EZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerAnwendungsfehler_EZ"), arg0);
	}

	/**
	   * {0}.{1}.{2} {3}:{4} [FB]
	   */
	public static String ReleaseFormatted(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4) {
		return symbol ? "£FB§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"£":MessageFormat.format(messages.getString("ReleaseFormatted_FB"), arg0, arg1, arg2, arg3, arg4);
	}

	/**
	   * Ein wichtiges Update ist verf\u00FCgbar [FC]
	   */
	public static String UpdateVerfuegbarWichtig(boolean symbol) {
		return symbol ? "£FC£":messages.getString("UpdateVerfuegbarWichtig_FC");
	}

	/**
	   * Nach Updates suchen [FD]
	   */
	public static String MenuSearchForUpdates(boolean symbol) {
		return symbol ? "£FD£":messages.getString("MenuSearchForUpdates_FD");
	}

	/**
	   * Ihr STERN-Build ist auf dem neusten Stand. [FE]
	   */
	public static String UpdateAktuell(boolean symbol) {
		return symbol ? "£FE£":messages.getString("UpdateAktuell_FE");
	}

	/**
	   * Der Update-Server ist nicht erreichbar. [FF]
	   */
	public static String UpdateServerNichtErreichbar(boolean symbol) {
		return symbol ? "£FF£":messages.getString("UpdateServerNichtErreichbar_FF");
	}

	/**
	   * User l\u00F6schen [FG]
	   */
	public static String ServerAdminSpielerLoeschen(boolean symbol) {
		return symbol ? "£FG£":messages.getString("ServerAdminSpielerLoeschen_FG");
	}

	/**
	   * Neuen User anlegen [FH]
	   */
	public static String ServerAdminSpielerAnlegen(boolean symbol) {
		return symbol ? "£FH£":messages.getString("ServerAdminSpielerAnlegen_FH");
	}

	/**
	   * Anmeldedaten erneuern [FI]
	   */
	public static String ServerAdminAnmeldedatenErneuern(boolean symbol) {
		return symbol ? "£FI£":messages.getString("ServerAdminAnmeldedatenErneuern_FI");
	}

	/**
	   * User-ID [{0}] existiert nicht auf dem Server. [FJ]
	   */
	public static String ServerErrorAdminUserExistiertNicht(boolean symbol, String arg0) {
		return symbol ? "£FJ§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorAdminUserExistiertNicht_FJ"), arg0);
	}

	/**
	   * User-Liste neu laden [FK]
	   */
	public static String ServerAdminUserNeuLaden(boolean symbol) {
		return symbol ? "£FK£":messages.getString("ServerAdminUserNeuLaden_FK");
	}

	/**
	   * Der User [{0}] wurde erfolgreich auf dem Server aktualisiert. [FL]
	   */
	public static String ServerAdminUserErfolg(boolean symbol, String arg0) {
		return symbol ? "£FL§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserErfolg_FL"), arg0);
	}

	/**
	   * M\u00F6chten Sie den User [{0}] wirklich aktualisieren? ACHTUNG: Sie \u00E4ndern auch die Anmeldedaten des Users, so dass der User neu aktiviert werden muss! [FM]
	   */
	public static String ServerAdminUserRenewCredentials(boolean symbol, String arg0) {
		return symbol ? "£FM§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserRenewCredentials_FM"), arg0);
	}

	/**
	   * M\u00F6chten Sie den User [{0}] wirklich aktualisieren? [FN]
	   */
	public static String ServerAdminUserUpdate(boolean symbol, String arg0) {
		return symbol ? "£FN§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserUpdate_FN"), arg0);
	}

	/**
	   * Logon-Versuch mit inaktivem User [{0}]. [FO]
	   */
	public static String ServerErrorLogonWithInactiveUser(boolean symbol, String arg0) {
		return symbol ? "£FO§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorLogonWithInactiveUser_FO"), arg0);
	}

	/**
	   * M\u00F6chten Sie den User [{0}] tats\u00E4chlich vom Server l\u00F6schen? Diese Aktion kann nicht widerrufen werden! [FP]
	   */
	public static String ServerAdminUserDelete(boolean symbol, String arg0) {
		return symbol ? "£FP§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserDelete_FP"), arg0);
	}

	/**
	   * Der User [{0}] wurde erfolgreich vom Server gel\u00F6scht. [FQ]
	   */
	public static String ServerAdminUserDeleted(boolean symbol, String arg0) {
		return symbol ? "£FQ§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserDeleted_FQ"), arg0);
	}

	/**
	   * Der User [{0}] wurde erfolgreich auf dem Server angelegt. Bitte schicken Sie die nachfolgend erzeugte E-Mail unver\u00E4ndert ab. [FR]
	   */
	public static String ServerAdminUserCreated(boolean symbol, String arg0) {
		return symbol ? "£FR§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserCreated_FR"), arg0);
	}

	/**
	   * E-Mail schreiben [FS]
	   */
	public static String MenuEmail(boolean symbol) {
		return symbol ? "£FS£":messages.getString("MenuEmail_FS");
	}

	/**
	   * E-Mail erzeugen [FT]
	   */
	public static String EmailErzeugen(boolean symbol) {
		return symbol ? "£FT£":messages.getString("EmailErzeugen_FT");
	}

	/**
	   * (E-Mail-Adresse unbekannt) [FU]
	   */
	public static String EmailUnbekannt(boolean symbol) {
		return symbol ? "£FU£":messages.getString("EmailUnbekannt_FU");
	}

	/**
	   * Server-Status [FV]
	   */
	public static String ServerStatus(boolean symbol) {
		return symbol ? "£FV£":messages.getString("ServerStatus_FV");
	}

	/**
	   * Server-Build [FW]
	   */
	public static String ServerBuild(boolean symbol) {
		return symbol ? "£FW£":messages.getString("ServerBuild_FW");
	}

	/**
	   * L\u00E4uft seit [FX]
	   */
	public static String ServerLaeuftSeit(boolean symbol) {
		return symbol ? "£FX£":messages.getString("ServerLaeuftSeit_FX");
	}

	/**
	   * Gr\u00F6\u00DFe des Logs [FY]
	   */
	public static String ServerLogGroesse(boolean symbol) {
		return symbol ? "£FY£":messages.getString("ServerLogGroesse_FY");
	}

	/**
	   * Log herunterladen [FZ]
	   */
	public static String ServerLogDownload(boolean symbol) {
		return symbol ? "£FZ£":messages.getString("ServerLogDownload_FZ");
	}

	/**
	   * Log-Level [GA]
	   */
	public static String ServerLogLevel(boolean symbol) {
		return symbol ? "£GA£":messages.getString("ServerLogLevel_GA");
	}

	/**
	   * Log-Level \u00E4ndern [GB]
	   */
	public static String ServerLogLevelAendern(boolean symbol) {
		return symbol ? "£GB£":messages.getString("ServerLogLevelAendern_GB");
	}

	/**
	   * Status aktualisieren [GC]
	   */
	public static String ServerStatusAktualisieren(boolean symbol) {
		return symbol ? "£GC£":messages.getString("ServerStatusAktualisieren_GC");
	}

	/**
	   * M\u00F6chten Sie das Log-Level des Servers wirklich auf "{0}" \u00E4ndern? [GD]
	   */
	public static String ServerLogLevelAendernAYS(boolean symbol, String arg0) {
		return symbol ? "£GD§"+arg0+"£":MessageFormat.format(messages.getString("ServerLogLevelAendernAYS_GD"), arg0);
	}

	/**
	   * Das Log-Level des Servers wurde erfolgreich ge\u00E4ndert. [GE]
	   */
	public static String ServerLogLevelAendernErfolg(boolean symbol) {
		return symbol ? "£GE£":messages.getString("ServerLogLevelAendernErfolg_GE");
	}

	/**
	   * Das Server-Log enth\u00E4lt keine Daten. [GF]
	   */
	public static String ServerLogLeer(boolean symbol) {
		return symbol ? "£GF£":messages.getString("ServerLogLeer_GF");
	}

	/**
	   * {0}.{1}.{2} {3}:{4}:{5} [GG]
	   */
	public static String ReleaseFormatted2(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£GG§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("ReleaseFormatted2_GG"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * ++ Jahr {0}, Tag {1} ++ [GH]
	   */
	public static String AuswertungEreignisTag(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GH§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungEreignisTag_GH"), arg0, arg1);
	}

	/**
	   * ++ Jahr {0}, Jahresbeginn ++ [GI]
	   */
	public static String AuswertungEreignisJahresbeginn(boolean symbol, String arg0) {
		return symbol ? "£GI§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungEreignisJahresbeginn_GI"), arg0);
	}

	/**
	   * ++ Jahr {0}, Jahresende ++ [GJ]
	   */
	public static String AuswertungEreignisJahresende(boolean symbol, String arg0) {
		return symbol ? "£GJ§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungEreignisJahresende_GJ"), arg0);
	}

	/**
	   * Jahr {0}, Tag {1} [GK]
	   */
	public static String FlugzeitOutput(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GK§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("FlugzeitOutput_GK"), arg0, arg1);
	}

	/**
	   * Jahr {0}, Jahresende [GL]
	   */
	public static String FlugzeitOutputJahresende(boolean symbol, String arg0) {
		return symbol ? "£GL§"+arg0+"£":MessageFormat.format(messages.getString("FlugzeitOutputJahresende_GL"), arg0);
	}

	/**
	   * J{0}\nT{1} [GM]
	   */
	public static String FlugzeitOutputShort(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GM§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("FlugzeitOutputShort_GM"), arg0, arg1);
	}

	/**
	   * J{0}\nEnde [GN]
	   */
	public static String FlugzeitOutputJahresendeShort(boolean symbol, String arg0) {
		return symbol ? "£GN§"+arg0+"£":MessageFormat.format(messages.getString("FlugzeitOutputJahresendeShort_GN"), arg0);
	}

	/**
	   * Einsatz [GP]
	   */
	public static String ZugeingabeMinenraeumerMission(boolean symbol) {
		return symbol ? "£GP£":messages.getString("ZugeingabeMinenraeumerMission_GP");
	}

	/**
	   * Einsatz [GQ]
	   */
	public static String ZugeingabePatrouilleMission(boolean symbol) {
		return symbol ? "£GQ£":messages.getString("ZugeingabePatrouilleMission_GQ");
	}

	/**
	   * Ankunft: {0}. [GT]
	   */
	public static String SpielinformationenKommandozentralenUnterwegs2(boolean symbol, String arg0) {
		return symbol ? "£GT§"+arg0+"£":MessageFormat.format(messages.getString("SpielinformationenKommandozentralenUnterwegs2_GT"), arg0);
	}

	/**
	   * Der Server benutzt Build {0}. Ihr STERN-Build erwartet einen Server-Build von mindestens {1}. [GV]
	   */
	public static String ServerBuildVeraltet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GV§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerBuildVeraltet_GV"), arg0, arg1);
	}

	/**
	   * Letzte Aktivit\u00E4t: {0} [GW]
	   */
	public static String ServerGamesLetzteAktivitaet(boolean symbol, String arg0) {
		return symbol ? "£GW§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesLetzteAktivitaet_GW"), arg0);
	}

	/**
	   * Spielleiteraktionen [GY]
	   */
	public static String ServerGamesSpielleiteraktionen(boolean symbol) {
		return symbol ? "£GY£":messages.getString("ServerGamesSpielleiteraktionen_GY");
	}

	/**
	   * Spiel l\u00F6schen [GZ]
	   */
	public static String ServerGamesLoeschen(boolean symbol) {
		return symbol ? "£GZ£":messages.getString("ServerGamesLoeschen_GZ");
	}

	/**
	   * Spiel beenden [HA]
	   */
	public static String ServerGamesBeenden(boolean symbol) {
		return symbol ? "£HA£":messages.getString("ServerGamesBeenden_HA");
	}

	/**
	   * Sie sind nicht der Spielleiter des Spiels {0}. [HC]
	   */
	public static String ServerErrorKeinSpielleiter(boolean symbol, String arg0) {
		return symbol ? "£HC§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorKeinSpielleiter_HC"), arg0);
	}

	/**
	   * Spiel {0} wurde erfolgreich gel\u00F6scht. [HD]
	   */
	public static String ServerGamesGameDeleted(boolean symbol, String arg0) {
		return symbol ? "£HD§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesGameDeleted_HD"), arg0);
	}

	/**
	   * M\u00F6chten Sie das Spiel {0} wirklich vom Server l\u00F6schen? [HE]
	   */
	public static String ServerGamesLoeschenAys(boolean symbol, String arg0) {
		return symbol ? "£HE§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesLoeschenAys_HE"), arg0);
	}

	/**
	   * Das Spiel ist bereits abgeschlossen. [HF]
	   */
	public static String ServerGamesAbgeschlossen(boolean symbol) {
		return symbol ? "£HF£":messages.getString("ServerGamesAbgeschlossen_HF");
	}

	/**
	   * M\u00F6chten Sie das Spiel {0} wirklich sofort beenden? Alle Spielz\u00FCge der Spieler gehen verloren. [HG]
	   */
	public static String ServerGamesBeendenAys(boolean symbol, String arg0) {
		return symbol ? "£HG§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesBeendenAys_HG"), arg0);
	}

	/**
	   * Spiel {0} wurde erfolgreich beendet. [HH]
	   */
	public static String ServerGamesGameFinalized(boolean symbol, String arg0) {
		return symbol ? "£HH§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesGameFinalized_HH"), arg0);
	}

	/**
	   * Jahresbeginn [HI]
	   */
	public static String AuswertungEreignisJahresbeginn2(boolean symbol) {
		return symbol ? "£HI£":messages.getString("AuswertungEreignisJahresbeginn2_HI");
	}

	/**
	   * Jahresende [HJ]
	   */
	public static String AuswertungEreignisJahresende2(boolean symbol) {
		return symbol ? "£HJ£":messages.getString("AuswertungEreignisJahresende2_HJ");
	}

	/**
	   * Tag {0} von {1} [HK]
	   */
	public static String AuswertungEreignisTag2(boolean symbol, String arg0, String arg1) {
		return symbol ? "£HK§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungEreignisTag2_HK"), arg0, arg1);
	}

	/**
	   * Raumschiffe aus-/einblenden [HL]
	   */
	public static String ZugeingabeObjekteAusblenden(boolean symbol) {
		return symbol ? "£HL£":messages.getString("ZugeingabeObjekteAusblenden_HL");
	}

	/**
	   * an [HM]
	   */
	public static String ZugeingabeObjekteAusblendenAn(boolean symbol) {
		return symbol ? "£HM£":messages.getString("ZugeingabeObjekteAusblendenAn_HM");
	}

	/**
	   * aus [HN]
	   */
	public static String ZugeingabeObjekteAusblendenAus(boolean symbol) {
		return symbol ? "£HN£":messages.getString("ZugeingabeObjekteAusblendenAus_HN");
	}

	/**
	   * Alle aus [HO]
	   */
	public static String ZugeingabeObjekteAusblendenAlleAus(boolean symbol) {
		return symbol ? "£HO£":messages.getString("ZugeingabeObjekteAusblendenAlleAus_HO");
	}

	/**
	   * Alle an [HP]
	   */
	public static String ZugeingabeObjekteAusblendenAlleAn(boolean symbol) {
		return symbol ? "£HP£":messages.getString("ZugeingabeObjekteAusblendenAlleAn_HP");
	}

	/**
	   * Das Jahr ist ausgewertet. Klicken Sie hier, um das Spiel neu zu laden. [HQ]
	   */
	public static String AuswertungVerfuegbarSymbol(boolean symbol) {
		return symbol ? "£HQ£":messages.getString("AuswertungVerfuegbarSymbol_HQ");
	}

	/**
	   * Diffie\u2013Hellman-Schl\u00FCsselaustausch gescheitert: {0} [HR]
	   */
	public static String ServerErrorDh(boolean symbol, String arg0) {
		return symbol ? "£HR§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorDh_HR"), arg0);
	}

	/**
	   * Neue Session {0} f\u00FCr User {1} erzeugt. [HS]
	   */
	public static String ServerNeueSession(boolean symbol, String arg0, String arg1) {
		return symbol ? "£HS§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerNeueSession_HS"), arg0, arg1);
	}

	/**
	   * Session ID [HT]
	   */
	public static String ServerILogSessionId(boolean symbol) {
		return symbol ? "£HT£":messages.getString("ServerILogSessionId_HT");
	}

	/**
	   * Benachrichtigungsmeldung stummschalten [HU]
	   */
	public static String BenachrichtigungStumm(boolean symbol) {
		return symbol ? "£HU£":messages.getString("BenachrichtigungStumm_HU");
	}

	/**
	   * User {0} einlesen... [HV]
	   */
	public static String ServerUserLesen(boolean symbol, String arg0) {
		return symbol ? "£HV§"+arg0+"£":MessageFormat.format(messages.getString("ServerUserLesen_HV"), arg0);
	}

	/**
	   * Konfiguration lesen... [HW]
	   */
	public static String ServerConfigLaden(boolean symbol) {
		return symbol ? "£HW£":messages.getString("ServerConfigLaden_HW");
	}

	/**
	   * Konfiguration neu anlegen... [HX]
	   */
	public static String ServerConfigErzeugen(boolean symbol) {
		return symbol ? "£HX£":messages.getString("ServerConfigErzeugen_HX");
	}

	/**
	   * Spiel {0} einlesen... [HY]
	   */
	public static String ServerSpielLesen(boolean symbol, String arg0) {
		return symbol ? "£HY§"+arg0+"£":MessageFormat.format(messages.getString("ServerSpielLesen_HY"), arg0);
	}

	/**
	   * Order {0} erzeugen... [HZ]
	   */
	public static String ServerOrdnerErzeugen(boolean symbol, String arg0) {
		return symbol ? "£HZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerOrdnerErzeugen_HZ"), arg0);
	}

	/**
	   * Admin-User erzeugen... [IA]
	   */
	public static String ServerAdminErzeugen(boolean symbol) {
		return symbol ? "£IA£":messages.getString("ServerAdminErzeugen_IA");
	}

	/**
	   * >>> Spieler geben Ihre Spielz\u00FCge ein. Die Eingabe ist gesperrt. <<< [IB]
	   */
	public static String ZugeingabeClientEingabeGesperrt(boolean symbol) {
		return symbol ? "£IB£":messages.getString("ZugeingabeClientEingabeGesperrt_IB");
	}

	/**
	   * STERN Displays passiv w\u00E4hrend Zugeingabe [IC]
	   */
	public static String ServerSettingsJDialogInaktiv(boolean symbol) {
		return symbol ? "£IC£":messages.getString("ServerSettingsJDialogInaktiv_IC");
	}

	/**
	   * Ermitteln [ID]
	   */
	public static String ClientSettingsJDialogIpErmitteln(boolean symbol) {
		return symbol ? "£ID£":messages.getString("ClientSettingsJDialogIpErmitteln_ID");
	}

	/**
	   * Meine IP-Adresse [IE]
	   */
	public static String ClientSettingsJDialogMeineIp(boolean symbol) {
		return symbol ? "£IE£":messages.getString("ClientSettingsJDialogMeineIp_IE");
	}

	/**
	   * Kampfschiffproduktion der Planeten [IF]
	   */
	public static String SpielinformationenKampfschiffproduktionTitel(boolean symbol) {
		return symbol ? "£IF£":messages.getString("SpielinformationenKampfschiffproduktionTitel_IF");
	}

	/**
	   * Kampfschiffproduktion [IG]
	   */
	public static String SpielinformationenKampfschiffproduktion(boolean symbol) {
		return symbol ? "£IG£":messages.getString("SpielinformationenKampfschiffproduktion_IG");
	}

	/**
	   * Wollen Sie die ge\u00E4nderten Server-Zugangsdaten abspeichern? [IH]
	   */
	public static String ServerUrlUebernehmen(boolean symbol) {
		return symbol ? "£IH£":messages.getString("ServerUrlUebernehmen_IH");
	}

	/**
	   * Zugangsdaten speichern? [II]
	   */
	public static String ServerZugangsdatenAendern(boolean symbol) {
		return symbol ? "£II£":messages.getString("ServerZugangsdatenAendern_II");
	}

	/**
	   * Der Browser kann nicht ge\u00F6ffnet werden:\n{0} [IJ]
	   */
	public static String BrowserNichtGeoeffnet(boolean symbol, String arg0) {
		return symbol ? "£IJ§"+arg0+"£":MessageFormat.format(messages.getString("BrowserNichtGeoeffnet_IJ"), arg0);
	}

	/**
	   * E-Mail-Client kann nicht ge\u00F6ffnet werden:\n{0} [IK]
	   */
	public static String EmailNichtGeoeffnet(boolean symbol, String arg0) {
		return symbol ? "£IK§"+arg0+"£":MessageFormat.format(messages.getString("EmailNichtGeoeffnet_IK"), arg0);
	}

	/**
	   * STERN-Build [IL]
	   */
	public static String ClientBuild(boolean symbol) {
		return symbol ? "£IL£":messages.getString("ClientBuild_IL");
	}

	/**
	   * Produziert {0} Kampfschiffe pro Jahr. [IM]
	   */
	public static String ZugeingabePlaneteninfo2(boolean symbol, String arg0) {
		return symbol ? "£IM§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabePlaneteninfo2_IM"), arg0);
	}

	/**
	   * Ausgabefenster \u00F6ffnen [IN]
	   */
	public static String MenuAusgabeFenster(boolean symbol) {
		return symbol ? "£IN£":messages.getString("MenuAusgabeFenster_IN");
	}
}