package common;

import java.util.Hashtable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
   * This class was created with the Resource Bundle Utility from the resource file
   *
   *   SternResources_en_US
   *
   * The resource file is maintained with the Eclipse-Plugin ResourceBundle Editor.
   */
public class SternResources 
{
	private static Hashtable<String,String> symbolDict;;
	private static String languageCode;
	private static ResourceBundle messages;

	static {
		setLocale("en-US");
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
		// Last used symbolic key: JL
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
		symbolDict.put("0I","InventurKommandant_0I");
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
		symbolDict.put("13","AuswertungAngriffAngreiferPlanet_13");
		symbolDict.put("15","AuswertungAngriffSpielerErobert_15");
		symbolDict.put("17","AuswertungAufklaererAngekommen_17");
		symbolDict.put("19","AuswertungBeginnt_19");
		symbolDict.put("1A","InventurPlanet1_1A");
		symbolDict.put("1B","InventurPlanet2_1B");
		symbolDict.put("1C","InventurPlanetKurz_1C");
		symbolDict.put("1D","InventurMinenraeumerKurz_1D");
		symbolDict.put("1E","InventurPlanetenTitel_1E");
		symbolDict.put("1G","InventurPunkte_1G");
		symbolDict.put("1I","InventurRaumerKurz_1I");
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
		symbolDict.put("1W","Loeschen_1W");
		symbolDict.put("1X","MenuBestenliste_1X");
		symbolDict.put("1Z","MenuDatei_1Z");
		symbolDict.put("20","AuswertungBuendnisNichtGeaendert_20");
		symbolDict.put("21","AuswertungEnde_21");
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
		symbolDict.put("3N","PlaneteneditorAuswahlAendern_3N");
		symbolDict.put("3O","PlaneteneditorKaufen_3O");
		symbolDict.put("3P","PlaneteneditorUebernehmen_3P");
		symbolDict.put("3Q","PlaneteneditorVerkaufen_3Q");
		symbolDict.put("3S","Punkte_3S");
		symbolDict.put("3T","Raumer_3T");
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
		symbolDict.put("5H","SpielinformationenPatrouillen_5H");
		symbolDict.put("5I","SpielinformationenPatrouillenTitel_5I");
		symbolDict.put("5J","SpielinformationenPlanet_5J");
		symbolDict.put("5K","SpielinformationenPlanetTitel_5K");
		symbolDict.put("5L","Spielleiter_5L");
		symbolDict.put("5M","Spielparameter_5M");
		symbolDict.put("5O","SpielparameterJDialogAutoSave_5O");
		symbolDict.put("5Q","SpielparameterJDialogEMailEinstellungen_5Q");
		symbolDict.put("5R","SpielparameterJDialogEmailModus_5R");
		symbolDict.put("5S","SpielparameterJDialogFarbe_5S");
		symbolDict.put("5T","SpielparameterJDialogNameZuLang_5T");
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
		symbolDict.put("80","EmailAdressenJDialogTitel_80");
		symbolDict.put("81","EmailSettingsJDialogTitel_81");
		symbolDict.put("82","Energieproduktion_82");
		symbolDict.put("83","Entfernungstabelle_83");
		symbolDict.put("84","Fehler_84");
		symbolDict.put("85","FehlerBeimLaden_85");
		symbolDict.put("86","Hauptmenue_86");
		symbolDict.put("87","HighscoreFrage_87");
		symbolDict.put("88","InventurAnkunft_88");
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
		symbolDict.put("9X","ZugeingabeZufaelligerSpieler_9X");
		symbolDict.put("9Y","Zurueck_9Y");
		symbolDict.put("9Z","Auswertung_9Z");
		symbolDict.put("AA","ClientSettingsJDialogKeineVerbindung2_AA");
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
		symbolDict.put("EE","ServerBuildFalsch_EE");
		symbolDict.put("EF","SternScreenSharingServer_EF");
		symbolDict.put("EG","EmailSubjectEingeladen_EG");
		symbolDict.put("EH","EmailBodyEingeladen_EH");
		symbolDict.put("EI","SpielerNichtAngemeldet_EI");
		symbolDict.put("EJ","AuswertungAufklaererSender_EJ");
		symbolDict.put("EK","ZugeingabeMehr_EK");
		symbolDict.put("EN","AuswertungVerfuegbar2_EN");
		symbolDict.put("EO","ServerGamesSubmitAngelegt2_EO");
		symbolDict.put("EP","LetztesJahr_EP");
		symbolDict.put("EQ","LetztesJahr2_EQ");
		symbolDict.put("ER","AbgeschlossenesSpiel_ER");
		symbolDict.put("ET","ZugeingabeKapitulieren_ET");
		symbolDict.put("EU","AuswertungKapitulation_EU");
		symbolDict.put("EV","UpdateAvailable_EV");
		symbolDict.put("EX","Update_EX");
		symbolDict.put("EZ","ServerAnwendungsfehler_EZ");
		symbolDict.put("FB","ReleaseFormatted_FB");
		symbolDict.put("FC","UpdateAvailableImportant_FC");
		symbolDict.put("FD","MenuSearchForUpdates_FD");
		symbolDict.put("FE","UpdateUpToDate_FE");
		symbolDict.put("FF","UpdateServerCannotBeReached_FF");
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
		symbolDict.put("GK","FlugzeitOutput_GK");
		symbolDict.put("GL","FlugzeitOutputJahresende_GL");
		symbolDict.put("GM","FlugzeitOutputShort_GM");
		symbolDict.put("GN","FlugzeitOutputJahresendeShort_GN");
		symbolDict.put("GP","ZugeingabeMinenraeumerMission_GP");
		symbolDict.put("GQ","ZugeingabePatrouilleMission_GQ");
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
		symbolDict.put("HY","ServerSpielLesen_HY");
		symbolDict.put("HZ","ServerOrdnerErzeugen_HZ");
		symbolDict.put("IA","ServerAdminErzeugen_IA");
		symbolDict.put("IB","ZugeingabeClientEingabeGesperrt_IB");
		symbolDict.put("IC","ServerSettingsJDialogInaktiv_IC");
		symbolDict.put("ID","ClientSettingsJDialogIpErmitteln_ID");
		symbolDict.put("IE","ClientSettingsJDialogMeineIp_IE");
		symbolDict.put("IH","ServerUrlUebernehmen_IH");
		symbolDict.put("II","ServerZugangsdatenAendern_II");
		symbolDict.put("IJ","BrowserNichtGeoeffnet_IJ");
		symbolDict.put("IK","EmailNichtGeoeffnet_IK");
		symbolDict.put("IL","ClientBuild_IL");
		symbolDict.put("IN","MenuAusgabeFenster_IN");
		symbolDict.put("IO","ServerGamesSubmitSpielname_IO");
		symbolDict.put("IP","ServerErrorSpielExistiert_IP");
		symbolDict.put("IQ","SpielinformationenSender_IQ");
		symbolDict.put("IR","MenuWebserverAktivieren_IR");
		symbolDict.put("IS","MenuWebserverDeaktivieren_IS");
		symbolDict.put("IT","WebserverAktiviert_IT");
		symbolDict.put("IU","WebserverDeaktiviert_IU");
		symbolDict.put("IV","Webserver_IV");
		symbolDict.put("IW","LogOnDataIncomplete_IW");
		symbolDict.put("IX","ConnectionClosed_IX");
		symbolDict.put("IY","NoConnectionToServer_IY");
		symbolDict.put("IZ","AddressSeparator_IZ");
		symbolDict.put("JA","ClientCodeInvalid_JA");
		symbolDict.put("JB","AllianceOwnerNotIncluded_JB");
		symbolDict.put("JC","ServerAdminUserActive_JC");
		symbolDict.put("JD","EvaluationPlanetsProducing_JD");
		symbolDict.put("JE","HomePlanet_JE");
		symbolDict.put("JG","EvaluationHomePlanetConquered_JG");
		symbolDict.put("JH","EnterMovesPlanet_JH");
		symbolDict.put("JI","Players_JI");
		symbolDict.put("JJ","PlanetListTitleFighters_JJ");
		symbolDict.put("JK","PlanetListTitleDefenceShiels_JK");
		symbolDict.put("JL","Evaluation_JL");
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
	   * Cancel [00]
	   */
	public static String Abbrechen(boolean symbol) {
		return symbol ? "£00£":messages.getString("Abbrechen_00");
	}

	/**
	   * $ prod. [01]
	   */
	public static String AbschlussEprod(boolean symbol) {
		return symbol ? "£01£":messages.getString("AbschlussEprod_01");
	}

	/**
	   * Position {0}: [02]
	   */
	public static String AbschlussPlatz(boolean symbol, String arg0) {
		return symbol ? "£02§"+arg0+"£":MessageFormat.format(messages.getString("AbschlussPlatz_02"), arg0);
	}

	/**
	   * --- Action was cancelled --- [03]
	   */
	public static String AktionAbgebrochen(boolean symbol) {
		return symbol ? "£03£":messages.getString("AktionAbgebrochen_03");
	}

	/**
	   * Refresh [04]
	   */
	public static String Aktualisieren(boolean symbol) {
		return symbol ? "£04£":messages.getString("Aktualisieren_04");
	}

	/**
	   * Scouts [05]
	   */
	public static String AufklaererPlural(boolean symbol) {
		return symbol ? "£05£":messages.getString("AufklaererPlural_05");
	}

	/**
	   * Select [06]
	   */
	public static String Auswaehlen(boolean symbol) {
		return symbol ? "£06£":messages.getString("Auswaehlen_06");
	}

	/**
	   * Attacker: {0}, Defence shield: {1} [07]
	   */
	public static String AuswertungAngriffAngreiferFestung(boolean symbol, String arg0, String arg1) {
		return symbol ? "£07§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngreiferFestung_07"), arg0, arg1);
	}

	/**
	   * {0} attacks planet {1} [08]
	   */
	public static String AuswertungAngriffAngriffAufPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£08§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngriffAufPlanet_08"), arg0, arg1);
	}

	/**
	   * Attack failed. [09]
	   */
	public static String AuswertungAngriffAngriffGescheitert(boolean symbol) {
		return symbol ? "£09£":messages.getString("AuswertungAngriffAngriffGescheitert_09");
	}

	/**
	   * Spaceships (without patrols in service) [0A]
	   */
	public static String InventurFlugobjekte(boolean symbol) {
		return symbol ? "£0A£":messages.getString("InventurFlugobjekte_0A");
	}

	/**
	   * Freight [0B]
	   */
	public static String InventurFracht(boolean symbol) {
		return symbol ? "£0B£":messages.getString("InventurFracht_0B");
	}

	/**
	   * Year {0} [0C]
	   */
	public static String InventurJahr1(boolean symbol, String arg0) {
		return symbol ? "£0C§"+arg0+"£":MessageFormat.format(messages.getString("InventurJahr1_0C"), arg0);
	}

	/**
	   * Year {0} of {1} [0D]
	   */
	public static String InventurJahr2(boolean symbol, String arg0, String arg1) {
		return symbol ? "£0D§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("InventurJahr2_0D"), arg0, arg1);
	}

	/**
	   * There are no spaceships. [0E]
	   */
	public static String InventurKeineFlugobjekte(boolean symbol) {
		return symbol ? "£0E£":messages.getString("InventurKeineFlugobjekte_0E");
	}

	/**
	   * There are no patrols in service. [0F]
	   */
	public static String InventurKeinePatrouillen(boolean symbol) {
		return symbol ? "£0F£":messages.getString("InventurKeinePatrouillen_0F");
	}

	/**
	   * You do not own any planets. [0G]
	   */
	public static String InventurKeinePlaneten(boolean symbol) {
		return symbol ? "£0G£":messages.getString("InventurKeinePlaneten_0G");
	}

	/**
	   * Commander [0I]
	   */
	public static String InventurKommandant(boolean symbol) {
		return symbol ? "£0I£":messages.getString("InventurKommandant_0I");
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
	   * Minelayer (100) [0O]
	   */
	public static String InventurMinenleger100(boolean symbol) {
		return symbol ? "£0O£":messages.getString("InventurMinenleger100_0O");
	}

	/**
	   * Minelayer (250) [0P]
	   */
	public static String InventurMinenleger250(boolean symbol) {
		return symbol ? "£0P£":messages.getString("InventurMinenleger250_0P");
	}

	/**
	   * Minelayer (50) [0Q]
	   */
	public static String InventurMinenleger50(boolean symbol) {
		return symbol ? "£0Q£":messages.getString("InventurMinenleger50_0Q");
	}

	/**
	   * Minelayer (500) [0R]
	   */
	public static String InventurMinenleger500(boolean symbol) {
		return symbol ? "£0R£":messages.getString("InventurMinenleger500_0R");
	}

	/**
	   * Minesweeper [0S]
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
	   * Patrol (Transfer) [0U]
	   */
	public static String InventurPatrouilleTransfer(boolean symbol) {
		return symbol ? "£0U£":messages.getString("InventurPatrouilleTransfer_0U");
	}

	/**
	   * Patrols [0V]
	   */
	public static String InventurPatrouillen(boolean symbol) {
		return symbol ? "£0V£":messages.getString("InventurPatrouillen_0V");
	}

	/**
	   * Patrols in service [0W]
	   */
	public static String InventurPatrrouillenTitel(boolean symbol) {
		return symbol ? "£0W£":messages.getString("InventurPatrrouillenTitel_0W");
	}

	/**
	   * Error: PDF viewer could not be opened. [0X]
	   */
	public static String InventurPdfFehler(boolean symbol) {
		return symbol ? "£0X£":messages.getString("InventurPdfFehler_0X");
	}

	/**
	   * PDF viewer was opened. [0Y]
	   */
	public static String InventurPdfGeoeffnet(boolean symbol) {
		return symbol ? "£0Y£":messages.getString("InventurPdfGeoeffnet_0Y");
	}

	/**
	   * Attacker: {0}, Planet: {1} [13]
	   */
	public static String AuswertungAngriffAngreiferPlanet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£13§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAngriffAngreiferPlanet_13"), arg0, arg1);
	}

	/**
	   * {0} conquered the planet! [15]
	   */
	public static String AuswertungAngriffSpielerErobert(boolean symbol, String arg0) {
		return symbol ? "£15§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungAngriffSpielerErobert_15"), arg0);
	}

	/**
	   * {0}: 1 scout arrived on planet {1}. [17]
	   */
	public static String AuswertungAufklaererAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£17§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAufklaererAngekommen_17"), arg0, arg1);
	}

	/**
	   * The evaluation begins. [19]
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
	   * Msw [1D]
	   */
	public static String InventurMinenraeumerKurz(boolean symbol) {
		return symbol ? "£1D£":messages.getString("InventurMinenraeumerKurz_1D");
	}

	/**
	   * Planets and alliances [1E]
	   */
	public static String InventurPlanetenTitel(boolean symbol) {
		return symbol ? "£1E£":messages.getString("InventurPlanetenTitel_1E");
	}

	/**
	   * {0} points [1G]
	   */
	public static String InventurPunkte(boolean symbol, String arg0) {
		return symbol ? "£1G§"+arg0+"£":MessageFormat.format(messages.getString("InventurPunkte_1G"), arg0);
	}

	/**
	   * Fght [1I]
	   */
	public static String InventurRaumerKurz(boolean symbol) {
		return symbol ? "£1I£":messages.getString("InventurRaumerKurz_1I");
	}

	/**
	   * EFs [1K]
	   */
	public static String InventurRaumerproduktionKurz(boolean symbol) {
		return symbol ? "£1K£":messages.getString("InventurRaumerproduktionKurz_1K");
	}

	/**
	   * Page {0} [1L]
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
	   * Physical Inventory [1N]
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
	   * Type [1R]
	   */
	public static String InventurTyp(boolean symbol) {
		return symbol ? "£1R£":messages.getString("InventurTyp_1R");
	}

	/**
	   * Dest [1S]
	   */
	public static String InventurZiel(boolean symbol) {
		return symbol ? "£1S£":messages.getString("InventurZiel_1S");
	}

	/**
	   * Yes [1T]
	   */
	public static String Ja(boolean symbol) {
		return symbol ? "£1T£":messages.getString("Ja_1T");
	}

	/**
	   * Delete [1W]
	   */
	public static String Loeschen(boolean symbol) {
		return symbol ? "£1W£":messages.getString("Loeschen_1W");
	}

	/**
	   * Local high score list [1X]
	   */
	public static String MenuBestenliste(boolean symbol) {
		return symbol ? "£1X£":messages.getString("MenuBestenliste_1X");
	}

	/**
	   * Game [1Z]
	   */
	public static String MenuDatei(boolean symbol) {
		return symbol ? "£1Z£":messages.getString("MenuDatei_1Z");
	}

	/**
	   * Not all concerned players have agreed to the change of the alliance on planet {0}. [20]
	   */
	public static String AuswertungBuendnisNichtGeaendert(boolean symbol, String arg0) {
		return symbol ? "£20§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungBuendnisNichtGeaendert_20"), arg0);
	}

	/**
	   * End of evaluation. [21]
	   */
	public static String AuswertungEnde(boolean symbol) {
		return symbol ? "£21£":messages.getString("AuswertungEnde_21");
	}

	/**
	   * {0}: Mine was planted. [24]
	   */
	public static String AuswertungMineGelegt(boolean symbol, String arg0) {
		return symbol ? "£24§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungMineGelegt_24"), arg0);
	}

	/**
	   * Mine field of strength {0} was swept. [25]
	   */
	public static String AuswertungMinenfeldGeraeumt(boolean symbol, String arg0) {
		return symbol ? "£25§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungMinenfeldGeraeumt_25"), arg0);
	}

	/**
	   * {0}: 1 minelayer arrived on planet {1}. [27]
	   */
	public static String AuswertungMinenlegerAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£27§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenlegerAngekommen_27"), arg0, arg1);
	}

	/**
	   * {0}: 1 minelayer crashed on planet {1}. [28]
	   */
	public static String AuswertungMinenlegerZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£28§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenlegerZerschellt_28"), arg0, arg1);
	}

	/**
	   * {0}: 1 minesweeper arrived on planet {1}. [29]
	   */
	public static String AuswertungMinenraeumerAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£29§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenraeumerAngekommen_29"), arg0, arg1);
	}

	/**
	   * Settings [2A]
	   */
	public static String MenuEinstellungen(boolean symbol) {
		return symbol ? "£2A£":messages.getString("MenuEinstellungen_2A");
	}

	/**
	   * Help [2B]
	   */
	public static String MenuHilfe(boolean symbol) {
		return symbol ? "£2B£":messages.getString("MenuHilfe_2B");
	}

	/**
	   * New local game [2C]
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
	   * Administrate the STERN server [2E]
	   */
	public static String MenuServerAdmin(boolean symbol) {
		return symbol ? "£2E£":messages.getString("MenuServerAdmin_2E");
	}

	/**
	   * STERN server credentials [2F]
	   */
	public static String MenuServerCredentials(boolean symbol) {
		return symbol ? "£2F£":messages.getString("MenuServerCredentials_2F");
	}

	/**
	   * Games on the STERN server [2G]
	   */
	public static String MenuServerbasierteSpiele(boolean symbol) {
		return symbol ? "£2G£":messages.getString("MenuServerbasierteSpiele_2G");
	}

	/**
	   * Import e-mail game from clipboard [2H]
	   */
	public static String MenuSpielAusZwischenablageLaden(boolean symbol) {
		return symbol ? "£2H£":messages.getString("MenuSpielAusZwischenablageLaden_2H");
	}

	/**
	   * Load local game [2J]
	   */
	public static String MenuSpielLaden(boolean symbol) {
		return symbol ? "£2J£":messages.getString("MenuSpielLaden_2J");
	}

	/**
	   * Save local game as [2K]
	   */
	public static String MenuSpielSpeichernAls(boolean symbol) {
		return symbol ? "£2K£":messages.getString("MenuSpielSpeichernAls_2K");
	}

	/**
	   * Manual (German only) [2L]
	   */
	public static String MenuSpielanleitung(boolean symbol) {
		return symbol ? "£2L£":messages.getString("MenuSpielanleitung_2L");
	}

	/**
	   * Quit STERN Display [2M]
	   */
	public static String MenuSternClientVerlassen(boolean symbol) {
		return symbol ? "£2M£":messages.getString("MenuSternClientVerlassen_2M");
	}

	/**
	   * Quit STERN [2N]
	   */
	public static String MenuSternVerlassen(boolean symbol) {
		return symbol ? "£2N£":messages.getString("MenuSternVerlassen_2N");
	}

	/**
	   * About STERN [2O]
	   */
	public static String MenuUeberStern(boolean symbol) {
		return symbol ? "£2O£":messages.getString("MenuUeberStern_2O");
	}

	/**
	   * Connection settings [2P]
	   */
	public static String MenuVerbindungseinstellungen(boolean symbol) {
		return symbol ? "£2P£":messages.getString("MenuVerbindungseinstellungen_2P");
	}

	/**
	   * The loaded data require a newer<br>STERN build. The minimum required build is <br>{0}, your STERN build is<br>{1}.<br><br>Please download the newest build from <br>{2}. [2Q]
	   */
	public static String MinBuild(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£2Q§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("MinBuild_2Q"), arg0, arg1, arg2);
	}

	/**
	   * Mines (100) [2R]
	   */
	public static String Mine100Plural(boolean symbol) {
		return symbol ? "£2R£":messages.getString("Mine100Plural_2R");
	}

	/**
	   * Mines (250) [2S]
	   */
	public static String Mine250Plural(boolean symbol) {
		return symbol ? "£2S£":messages.getString("Mine250Plural_2S");
	}

	/**
	   * Mines (500) [2T]
	   */
	public static String Mine500Plural(boolean symbol) {
		return symbol ? "£2T£":messages.getString("Mine500Plural_2T");
	}

	/**
	   * Mines (50) [2U]
	   */
	public static String Mine50Plural(boolean symbol) {
		return symbol ? "£2U£":messages.getString("Mine50Plural_2U");
	}

	/**
	   * Minesweepers [2V]
	   */
	public static String MinenraeumerPlural(boolean symbol) {
		return symbol ? "£2V£":messages.getString("MinenraeumerPlural_2V");
	}

	/**
	   * Do you really want to quit STERN? [2W]
	   */
	public static String MoechtestDuSternVerlassen(boolean symbol) {
		return symbol ? "£2W£":messages.getString("MoechtestDuSternVerlassen_2W");
	}

	/**
	   * No [2X]
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
	   * {0}: 1 minesweeper crashed on planet {1}. [30]
	   */
	public static String AuswertungMinenraeumerZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£30§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungMinenraeumerZerschellt_30"), arg0, arg1);
	}

	/**
	   * {0}: Message from sector {1}: [31]
	   */
	public static String AuswertungNachrichtAnAusSektor(boolean symbol, String arg0, String arg1) {
		return symbol ? "£31§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungNachrichtAnAusSektor_31"), arg0, arg1);
	}

	/**
	   * {0}: 1 patrol arrived on planet {1}. [33]
	   */
	public static String AuswertungPatrouilleAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£33§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleAngekommen_33"), arg0, arg1);
	}

	/**
	   * {0}: You captured a scout with destination {1} from {2}. [34]
	   */
	public static String AuswertungPatrouilleAufklaererGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£34§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleAufklaererGekapert_34"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a minelayer with destination {1} from {2}. [36]
	   */
	public static String AuswertungPatrouilleMinenlegerGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£36§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleMinenlegerGekapert_36"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a minesweeper with destination {1} from {2}. [37]
	   */
	public static String AuswertungPatrouilleMinenraeumerGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£37§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleMinenraeumerGekapert_37"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a patrol with destination {1} from {2}. [38]
	   */
	public static String AuswertungPatrouillePatrouilleGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£38§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouillePatrouilleGekapert_38"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You destroyed a patrol of {1}. [39]
	   */
	public static String AuswertungPatrouillePatrouilleZerstoert(boolean symbol, String arg0, String arg1) {
		return symbol ? "£39§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouillePatrouilleZerstoert_39"), arg0, arg1);
	}

	/**
	   * Patrols [3A]
	   */
	public static String PatrouillePlural(boolean symbol) {
		return symbol ? "£3A£":messages.getString("PatrouillePlural_3A");
	}

	/**
	   * Open PDF viewer? [3B]
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
	   * $ supply [3D]
	   */
	public static String PlEditEeEnergievorrat(boolean symbol) {
		return symbol ? "£3D£":messages.getString("PlEditEeEnergievorrat_3D");
	}

	/**
	   * $ production/year (buy +4) [3E]
	   */
	public static String PlEditEprodPlus4(boolean symbol) {
		return symbol ? "£3E£":messages.getString("PlEditEprodPlus4_3E");
	}

	/**
	   * Defence shield fighters (buy +{0}) [3F]
	   */
	public static String PlEditFestungRaumer(boolean symbol, String arg0) {
		return symbol ? "£3F§"+arg0+"£":MessageFormat.format(messages.getString("PlEditFestungRaumer_3F"), arg0);
	}

	/**
	   * Defence shields [3G]
	   */
	public static String PlEditFestungen(boolean symbol) {
		return symbol ? "£3G£":messages.getString("PlEditFestungen_3G");
	}

	/**
	   * Buy [3H]
	   */
	public static String PlEditKaufpreis(boolean symbol) {
		return symbol ? "£3H£":messages.getString("PlEditKaufpreis_3H");
	}

	/**
	   * Production of fighters/year [3I]
	   */
	public static String PlEditRaumerProd(boolean symbol) {
		return symbol ? "£3I£":messages.getString("PlEditRaumerProd_3I");
	}

	/**
	   * Sell [3J]
	   */
	public static String PlEditVerkaufspreis(boolean symbol) {
		return symbol ? "£3J£":messages.getString("PlEditVerkaufspreis_3J");
	}

	/**
	   * Planets [3K]
	   */
	public static String Planeten(boolean symbol) {
		return symbol ? "£3K£":messages.getString("Planeten_3K");
	}

	/**
	   * Change selection [3N]
	   */
	public static String PlaneteneditorAuswahlAendern(boolean symbol) {
		return symbol ? "£3N£":messages.getString("PlaneteneditorAuswahlAendern_3N");
	}

	/**
	   * Buy\n [3O]
	   */
	public static String PlaneteneditorKaufen(boolean symbol) {
		return symbol ? "£3O£":messages.getString("PlaneteneditorKaufen_3O");
	}

	/**
	   * Take over changes [3P]
	   */
	public static String PlaneteneditorUebernehmen(boolean symbol) {
		return symbol ? "£3P£":messages.getString("PlaneteneditorUebernehmen_3P");
	}

	/**
	   * Sell [3Q]
	   */
	public static String PlaneteneditorVerkaufen(boolean symbol) {
		return symbol ? "£3Q£":messages.getString("PlaneteneditorVerkaufen_3Q");
	}

	/**
	   * Points [3S]
	   */
	public static String Punkte(boolean symbol) {
		return symbol ? "£3S£":messages.getString("Punkte_3S");
	}

	/**
	   * Fighters [3T]
	   */
	public static String Raumer(boolean symbol) {
		return symbol ? "£3T£":messages.getString("Raumer_3T");
	}

	/**
	   * Replay evaluation [3V]
	   */
	public static String ReplayAuswertungWiedergeben(boolean symbol) {
		return symbol ? "£3V£":messages.getString("ReplayAuswertungWiedergeben_3V");
	}

	/**
	   * {0}: You captured {1} fighter(s) with destination {2} from {3}. [40]
	   */
	public static String AuswertungPatrouilleRaumerGekapert(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£40§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleRaumerGekapert_40"), arg0, arg1, arg2, arg3);
	}

	/**
	   * {0} fighter(s) of {1} with destination {2} sighted. [41]
	   */
	public static String AuswertungPatrouilleRaumerGesichtet(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£41§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleRaumerGesichtet_41"), arg0, arg1, arg2);
	}

	/**
	   * {0}: You captured a transporter with destination {1} from {2}. [42]
	   */
	public static String AuswertungPatrouilleTransporterGekapert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£42§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleTransporterGekapert_42"), arg0, arg1, arg2);
	}

	/**
	   * {0}: 1 patrol crashed on planet {1}. [43]
	   */
	public static String AuswertungPatrouilleZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£43§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungPatrouilleZerschellt_43"), arg0, arg1);
	}

	/**
	   * {0}: {1} fighters arrived on planet {2}. [44]
	   */
	public static String AuswertungRaumerAngekommen(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£44§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAngekommen_44"), arg0, arg1, arg2);
	}

	/**
	   * {0}: {1} fighters killed by a mine in sector {2}. The mine was destroyed. [45]
	   */
	public static String AuswertungRaumerAufMineGelaufenZerstoert(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£45§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAufMineGelaufenZerstoert_45"), arg0, arg1, arg2);
	}

	/**
	   * {0}: {1} fighters killed by a mine in sector {2}. [46]
	   */
	public static String AuswertungRaumerAufMineGelaufen(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£46§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerAufMineGelaufen_46"), arg0, arg1, arg2);
	}

	/**
	   * {0}: Fighters cannot be launched from planet {1}. [47]
	   */
	public static String AuswertungRaumerNichtGestartet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£47§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungRaumerNichtGestartet_47"), arg0, arg1);
	}

	/**
	   * {1}: {0} fighter(s) have to leave planet {2}, because the alliance was terminated. [48]
	   */
	public static String AuswertungRaumerVertrieben(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£48§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("AuswertungRaumerVertrieben_48"), arg0, arg1, arg2);
	}

	/**
	   * The fighters are waiting outside the planet for a new destination. [49]
	   */
	public static String AuswertungRaumerVertrieben2(boolean symbol) {
		return symbol ? "£49£":messages.getString("AuswertungRaumerVertrieben2_49");
	}

	/**
	   * Replay of the evaluation of year {0} [4A]
	   */
	public static String ReplayWiedergabeJahr(boolean symbol, String arg0) {
		return symbol ? "£4A§"+arg0+"£":MessageFormat.format(messages.getString("ReplayWiedergabeJahr_4A"), arg0);
	}

	/**
	   * Close [4B]
	   */
	public static String Schliessen(boolean symbol) {
		return symbol ? "£4B£":messages.getString("Schliessen_4B");
	}

	/**
	   * Server IP address [4C]
	   */
	public static String ServerSettingsJDialogIpServer(boolean symbol) {
		return symbol ? "£4C£":messages.getString("ServerSettingsJDialogIpServer_4C");
	}

	/**
	   * Activate server [4D]
	   */
	public static String ServerSettingsJDialogTerminalServerAktiv(boolean symbol) {
		return symbol ? "£4D£":messages.getString("ServerSettingsJDialogTerminalServerAktiv_4D");
	}

	/**
	   * [Unknown] [4E]
	   */
	public static String ServerSettingsJDialogUnbekannt(boolean symbol) {
		return symbol ? "£4E£":messages.getString("ServerSettingsJDialogUnbekannt_4E");
	}

	/**
	   * Connected STERN Display computers [4F]
	   */
	public static String ServerSettingsJDialogVerbundeneClients(boolean symbol) {
		return symbol ? "£4F£":messages.getString("ServerSettingsJDialogVerbundeneClients_4F");
	}

	/**
	   * Finalize [4G]
	   */
	public static String SpielAbschliessen(boolean symbol) {
		return symbol ? "£4G£":messages.getString("SpielAbschliessen_4G");
	}

	/**
	   * Do you really want to finalize the game? [4H]
	   */
	public static String SpielAbschliessenFrage(boolean symbol) {
		return symbol ? "£4H£":messages.getString("SpielAbschliessenFrage_4H");
	}

	/**
	   * Load game [4I]
	   */
	public static String SpielLaden(boolean symbol) {
		return symbol ? "£4I£":messages.getString("SpielLaden_4I");
	}

	/**
	   * Save game [4J]
	   */
	public static String SpielSpeichern(boolean symbol) {
		return symbol ? "£4J£":messages.getString("SpielSpeichern_4J");
	}

	/**
	   * Player [4K]
	   */
	public static String Spieler(boolean symbol) {
		return symbol ? "£4K£":messages.getString("Spieler_4K");
	}

	/**
	   * Do you agree with this playing field? [4L]
	   */
	public static String SpielfeldOkFrage(boolean symbol) {
		return symbol ? "£4L£":messages.getString("SpielfeldOkFrage_4L");
	}

	/**
	   * Game info [4M]
	   */
	public static String Spielinformationen(boolean symbol) {
		return symbol ? "£4M£":messages.getString("Spielinformationen_4M");
	}

	/**
	   * Display alliance structure on planet [4Q]
	   */
	public static String SpielinformationenBuendnisPlanet(boolean symbol) {
		return symbol ? "£4Q£":messages.getString("SpielinformationenBuendnisPlanet_4Q");
	}

	/**
	   * Alliances [4R]
	   */
	public static String SpielinformationenBuendnisse(boolean symbol) {
		return symbol ? "£4R£":messages.getString("SpielinformationenBuendnisse_4R");
	}

	/**
	   * Alliances [4T]
	   */
	public static String SpielinformationenBuendnisseTitel(boolean symbol) {
		return symbol ? "£4T£":messages.getString("SpielinformationenBuendnisseTitel_4T");
	}

	/**
	   * Alliance structure on planet {0} [4U]
	   */
	public static String SpielinformationenBuendnisstruktur(boolean symbol, String arg0) {
		return symbol ? "£4U§"+arg0+"£":MessageFormat.format(messages.getString("SpielinformationenBuendnisstruktur_4U"), arg0);
	}

	/**
	   * $ production [4V]
	   */
	public static String SpielinformationenEnergieproduktion(boolean symbol) {
		return symbol ? "£4V£":messages.getString("SpielinformationenEnergieproduktion_4V");
	}

	/**
	   * $ production of the planets [4W]
	   */
	public static String SpielinformationenEnergieproduktionTitel(boolean symbol) {
		return symbol ? "£4W£":messages.getString("SpielinformationenEnergieproduktionTitel_4W");
	}

	/**
	   * Defence shields [4X]
	   */
	public static String SpielinformationenFestungen(boolean symbol) {
		return symbol ? "£4X£":messages.getString("SpielinformationenFestungen_4X");
	}

	/**
	   * Defence shields [4Y]
	   */
	public static String SpielinformationenFestungenTitel(boolean symbol) {
		return symbol ? "£4Y£":messages.getString("SpielinformationenFestungenTitel_4Y");
	}

	/**
	   * The is no alliance on planet {0}. [4Z]
	   */
	public static String SpielinformationenKeinBuendnis(boolean symbol, String arg0) {
		return symbol ? "£4Z§"+arg0+"£":MessageFormat.format(messages.getString("SpielinformationenKeinBuendnis_4Z"), arg0);
	}

	/**
	   * Well, {0}. The game is over! [57]
	   */
	public static String AuswertungSpielerTot(boolean symbol, String arg0) {
		return symbol ? "£57§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungSpielerTot_57"), arg0);
	}

	/**
	   * {0}: 1 transporter arrived on planet {1}. [58]
	   */
	public static String AuswertungTransporterAngekommen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£58§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungTransporterAngekommen_58"), arg0, arg1);
	}

	/**
	   * {0}: 1 transporter crashed on planet {1}. [59]
	   */
	public static String AuswertungTransporterZerschellt(boolean symbol, String arg0, String arg1) {
		return symbol ? "£59§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungTransporterZerschellt_59"), arg0, arg1);
	}

	/**
	   * There are no planets with alliances. [5B]
	   */
	public static String SpielinformationenKeinePlanetenMitBuendnissen(boolean symbol) {
		return symbol ? "£5B£":messages.getString("SpielinformationenKeinePlanetenMitBuendnissen_5B");
	}

	/**
	   * Patrols [5H]
	   */
	public static String SpielinformationenPatrouillen(boolean symbol) {
		return symbol ? "£5H£":messages.getString("SpielinformationenPatrouillen_5H");
	}

	/**
	   * Patrols [5I]
	   */
	public static String SpielinformationenPatrouillenTitel(boolean symbol) {
		return symbol ? "£5I£":messages.getString("SpielinformationenPatrouillenTitel_5I");
	}

	/**
	   * Planets [5J]
	   */
	public static String SpielinformationenPlanet(boolean symbol) {
		return symbol ? "£5J£":messages.getString("SpielinformationenPlanet_5J");
	}

	/**
	   * Planets [5K]
	   */
	public static String SpielinformationenPlanetTitel(boolean symbol) {
		return symbol ? "£5K£":messages.getString("SpielinformationenPlanetTitel_5K");
	}

	/**
	   * Game host [5L]
	   */
	public static String Spielleiter(boolean symbol) {
		return symbol ? "£5L£":messages.getString("Spielleiter_5L");
	}

	/**
	   * Game parameters [5M]
	   */
	public static String Spielparameter(boolean symbol) {
		return symbol ? "£5M£":messages.getString("Spielparameter_5M");
	}

	/**
	   * Automatic save [5O]
	   */
	public static String SpielparameterJDialogAutoSave(boolean symbol) {
		return symbol ? "£5O£":messages.getString("SpielparameterJDialogAutoSave_5O");
	}

	/**
	   * E-mail settings [5Q]
	   */
	public static String SpielparameterJDialogEMailEinstellungen(boolean symbol) {
		return symbol ? "£5Q£":messages.getString("SpielparameterJDialogEMailEinstellungen_5Q");
	}

	/**
	   * E-mail mode [5R]
	   */
	public static String SpielparameterJDialogEmailModus(boolean symbol) {
		return symbol ? "£5R£":messages.getString("SpielparameterJDialogEmailModus_5R");
	}

	/**
	   * Color [5S]
	   */
	public static String SpielparameterJDialogFarbe(boolean symbol) {
		return symbol ? "£5S£":messages.getString("SpielparameterJDialogFarbe_5S");
	}

	/**
	   * The user name [{0}] ist invalid.\nThe length of a user name must be between {1} and {2} characters.\nIt must only contain the characters a-z, A-Z, and 0-9. [5T]
	   */
	public static String SpielparameterJDialogNameZuLang(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£5T§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("SpielparameterJDialogNameZuLang_5T"), arg0, arg1, arg2);
	}

	/**
	   * Years [5V]
	   */
	public static String SpielparameterJDialogSpieleBisJahr(boolean symbol) {
		return symbol ? "£5V£":messages.getString("SpielparameterJDialogSpieleBisJahr_5V");
	}

	/**
	   * The e-mail address of player [{0}] is invalid. [5W]
	   */
	public static String SpielparameterJDialogSpielerEMail(boolean symbol, String arg0) {
		return symbol ? "£5W§"+arg0+"£":MessageFormat.format(messages.getString("SpielparameterJDialogSpielerEMail_5W"), arg0);
	}

	/**
	   * The e-mail address of the game host is invalid. [5X]
	   */
	public static String SpielparameterJDialogSpielleiterEMail(boolean symbol) {
		return symbol ? "£5X£":messages.getString("SpielparameterJDialogSpielleiterEMail_5X");
	}

	/**
	   * Infinite [5Y]
	   */
	public static String SpielparameterJDialogUnendlich(boolean symbol) {
		return symbol ? "£5Y£":messages.getString("SpielparameterJDialogUnendlich_5Y");
	}

	/**
	   * Replay [60]
	   */
	public static String AuswertungWiederholen(boolean symbol) {
		return symbol ? "£60£":messages.getString("AuswertungWiederholen_60");
	}

	/**
	   * High score list [61]
	   */
	public static String Bestenliste(boolean symbol) {
		return symbol ? "£61£":messages.getString("Bestenliste_61");
	}

	/**
	   * STERN Display has not been registered at server {0} [62]
	   */
	public static String ClientSettingsJDialogClientNichtRegistriert(boolean symbol, String arg0) {
		return symbol ? "£62§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogClientNichtRegistriert_62"), arg0);
	}

	/**
	   * Connection to the server could not be established.\nError message:\n\n{0} [63]
	   */
	public static String ClientSettingsJDialogKeineVerbindung(boolean symbol, String arg0) {
		return symbol ? "£63§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogKeineVerbindung_63"), arg0);
	}

	/**
	   * My name [64]
	   */
	public static String ClientSettingsJDialogMeinName(boolean symbol) {
		return symbol ? "£64£":messages.getString("ClientSettingsJDialogMeinName_64");
	}

	/**
	   * Not connected [65]
	   */
	public static String ClientSettingsJDialogNichtVerbunden(boolean symbol) {
		return symbol ? "£65£":messages.getString("ClientSettingsJDialogNichtVerbunden_65");
	}

	/**
	   * Server {0} cannot be reached [66]
	   */
	public static String ClientSettingsJDialogServerNichtErreichbar(boolean symbol, String arg0) {
		return symbol ? "£66§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogServerNichtErreichbar_66"), arg0);
	}

	/**
	   * Connection settings [67]
	   */
	public static String ClientSettingsJDialogTitel(boolean symbol) {
		return symbol ? "£67£":messages.getString("ClientSettingsJDialogTitel_67");
	}

	/**
	   * Connect [68]
	   */
	public static String ClientSettingsJDialogVerbinden(boolean symbol) {
		return symbol ? "£68£":messages.getString("ClientSettingsJDialogVerbinden_68");
	}

	/**
	   * Connection status [69]
	   */
	public static String ClientSettingsJDialogVerbindungsstatus(boolean symbol) {
		return symbol ? "£69£":messages.getString("ClientSettingsJDialogVerbindungsstatus_69");
	}

	/**
	   * Statistics [6A]
	   */
	public static String Statistik(boolean symbol) {
		return symbol ? "£6A£":messages.getString("Statistik_6A");
	}

	/**
	   * Year -\n [6C]
	   */
	public static String StatistikJahrMinus(boolean symbol) {
		return symbol ? "£6C£":messages.getString("StatistikJahrMinus_6C");
	}

	/**
	   * Year +\n [6D]
	   */
	public static String StatistikJahrPlus(boolean symbol) {
		return symbol ? "£6D£":messages.getString("StatistikJahrPlus_6D");
	}

	/**
	   * Max: {0} (year {1}) [6E]
	   */
	public static String StatistikMax(boolean symbol, String arg0, String arg1) {
		return symbol ? "£6E§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("StatistikMax_6E"), arg0, arg1);
	}

	/**
	   * Min: {0} (year {1}) [6F]
	   */
	public static String StatistikMin(boolean symbol, String arg0, String arg1) {
		return symbol ? "£6F§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("StatistikMin_6F"), arg0, arg1);
	}

	/**
	   * Close statistics [6G]
	   */
	public static String StatistikSchliessen(boolean symbol) {
		return symbol ? "£6G£":messages.getString("StatistikSchliessen_6G");
	}

	/**
	   * Game started on [6H]
	   */
	public static String StatistikSpielBegonnen(boolean symbol) {
		return symbol ? "£6H£":messages.getString("StatistikSpielBegonnen_6H");
	}

	/**
	   * $ prod. in year {0} [6J]
	   */
	public static String StatistikTitelEnergieproduktion(boolean symbol, String arg0) {
		return symbol ? "£6J§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelEnergieproduktion_6J"), arg0);
	}

	/**
	   * Planets in year {0} [6K]
	   */
	public static String StatistikTitelPlaneten(boolean symbol, String arg0) {
		return symbol ? "£6K§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelPlaneten_6K"), arg0);
	}

	/**
	   * Points in year {0} [6L]
	   */
	public static String StatistikTitelPunkte(boolean symbol, String arg0) {
		return symbol ? "£6L§"+arg0+"£":MessageFormat.format(messages.getString("StatistikTitelPunkte_6L"), arg0);
	}

	/**
	   * Fighters in year {0} [6M]
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
	   * Do you really want to quit STERN Display? [6P]
	   */
	public static String SternClientVerlassenFrage(boolean symbol) {
		return symbol ? "£6P£":messages.getString("SternClientVerlassenFrage_6P");
	}

	/**
	   * STERN Display server is active [6Q]
	   */
	public static String SternTerminalServer(boolean symbol) {
		return symbol ? "£6Q£":messages.getString("SternTerminalServer_6Q");
	}

	/**
	   * Quit STERN Display [6R]
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
	   * Quit STERN [6T]
	   */
	public static String SternVerlassen(boolean symbol) {
		return symbol ? "£6T£":messages.getString("SternVerlassen_6T");
	}

	/**
	   * STERN Display server [6U]
	   */
	public static String Terminalserver(boolean symbol) {
		return symbol ? "£6U£":messages.getString("Terminalserver_6U");
	}

	/**
	   * Security code [6V]
	   */
	public static String ThinClientCode(boolean symbol) {
		return symbol ? "£6V£":messages.getString("ThinClientCode_6V");
	}

	/**
	   * Transporters [6W]
	   */
	public static String TransporterPlural(boolean symbol) {
		return symbol ? "£6W£":messages.getString("TransporterPlural_6W");
	}

	/**
	   * Invalid input. [6X]
	   */
	public static String UngueltigeEingabe(boolean symbol) {
		return symbol ? "£6X£":messages.getString("UngueltigeEingabe_6X");
	}

	/**
	   * Client and server are using different STERN builds [6Y]
	   */
	public static String UnterschiedlicheBuilds(boolean symbol) {
		return symbol ? "£6Y£":messages.getString("UnterschiedlicheBuilds_6Y");
	}

	/**
	   * Next [6Z]
	   */
	public static String Weiter(boolean symbol) {
		return symbol ? "£6Z£":messages.getString("Weiter_6Z");
	}

	/**
	   * Connected with server {0} [70]
	   */
	public static String ClientSettingsJDialogVerbunden(boolean symbol, String arg0) {
		return symbol ? "£70§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogVerbunden_70"), arg0);
	}

	/**
	   * Data could not be interpreted. Check the following:\n\n1. Is the data coming from a STERN e-mail?\n2. Did you use an e-mail from a wrong context?\n3. Your STERN build ({0}) and the build of the sender (see e-mail) are too different.\n\u0009\u0009\u0009\u0009\u0009\u0009 [71]
	   */
	public static String ClipboardImportJDIalogImportFehler(boolean symbol, String arg0) {
		return symbol ? "£71§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportJDIalogImportFehler_71"), arg0);
	}

	/**
	   * Paste from the clipboard in here [72]
	   */
	public static String ClipboardImportJDIalogInhaltHierEinfuegen(boolean symbol) {
		return symbol ? "£72£":messages.getString("ClipboardImportJDIalogInhaltHierEinfuegen_72");
	}

	/**
	   * Import data from the clipboard [73]
	   */
	public static String ClipboardImportJDIalogTitle(boolean symbol) {
		return symbol ? "£73£":messages.getString("ClipboardImportJDIalogTitle_73");
	}

	/**
	   * The file does not exist. [74]
	   */
	public static String DateiExistiertNicht(boolean symbol) {
		return symbol ? "£74£":messages.getString("DateiExistiertNicht_74");
	}

	/**
	   * This file is not a valid STERN game file. [76]
	   */
	public static String DateiNichtGueltig(boolean symbol) {
		return symbol ? "£76£":messages.getString("DateiNichtGueltig_76");
	}

	/**
	   * E-mail address [77]
	   */
	public static String EMailAdresse(boolean symbol) {
		return symbol ? "£77£":messages.getString("EMailAdresse_77");
	}

	/**
	   * Insert [78]
	   */
	public static String Einfuegen(boolean symbol) {
		return symbol ? "£78£":messages.getString("Einfuegen_78");
	}

	/**
	   * Input disabled [79]
	   */
	public static String EingabeGesperrt(boolean symbol) {
		return symbol ? "£79£":messages.getString("EingabeGesperrt_79");
	}

	/**
	   * Enter moves [7A]
	   */
	public static String Zugeingabe(boolean symbol) {
		return symbol ? "£7A£":messages.getString("Zugeingabe_7A");
	}

	/**
	   * Action not possible. [7B]
	   */
	public static String ZugeingabeAktionNichtMoeglich(boolean symbol) {
		return symbol ? "£7B£":messages.getString("ZugeingabeAktionNichtMoeglich_7B");
	}

	/**
	   * Maximum load [7C]
	   */
	public static String ZugeingabeAlleEe(boolean symbol) {
		return symbol ? "£7C£":messages.getString("ZugeingabeAlleEe_7C");
	}

	/**
	   * All fighters [7D]
	   */
	public static String ZugeingabeAlleRaumer(boolean symbol) {
		return symbol ? "£7D£":messages.getString("ZugeingabeAlleRaumer_7D");
	}

	/**
	   * Arrival: [7E]
	   */
	public static String ZugeingabeAnkunft(boolean symbol) {
		return symbol ? "£7E£":messages.getString("ZugeingabeAnkunft_7E");
	}

	/**
	   * Count [7F]
	   */
	public static String ZugeingabeAnzahl(boolean symbol) {
		return symbol ? "£7F£":messages.getString("ZugeingabeAnzahl_7F");
	}

	/**
	   * Scout [7H]
	   */
	public static String ZugeingabeAufklaerer(boolean symbol) {
		return symbol ? "£7H£":messages.getString("ZugeingabeAufklaerer_7H");
	}

	/**
	   * Finish [7I]
	   */
	public static String ZugeingabeBeenden(boolean symbol) {
		return symbol ? "£7I£":messages.getString("ZugeingabeBeenden_7I");
	}

	/**
	   * Do you want to finish entering moves? [7J]
	   */
	public static String ZugeingabeBeendenFrage(boolean symbol) {
		return symbol ? "£7J£":messages.getString("ZugeingabeBeendenFrage_7J");
	}

	/**
	   * Allied f's [7K]
	   */
	public static String ZugeingabeBuendRaumer(boolean symbol) {
		return symbol ? "£7K£":messages.getString("ZugeingabeBuendRaumer_7K");
	}

	/**
	   * Alliance [7L]
	   */
	public static String ZugeingabeBuendnis(boolean symbol) {
		return symbol ? "£7L£":messages.getString("ZugeingabeBuendnis_7L");
	}

	/**
	   * E-mail actions [7M]
	   */
	public static String ZugeingabeEMailAktionen(boolean symbol) {
		return symbol ? "£7M£":messages.getString("ZugeingabeEMailAktionen_7M");
	}

	/**
	   * Game: {0}\nYear: {1}\nMoves of: {2}\nBuild: {3}\n\nDear game host,\n\nhere are the moves of {4}. Please proceed as follows:\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start STERN. Load the local game {5}.stn\n\n3. Go to "entering moves". Select "E-mail actions > Import moves of a player".\n\n4. Press the button "Insert" to insert the contents of the clipboard into the text field.\n\n5. Press the button "OK". [7N]
	   */
	public static String ZugeingabeEMailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£7N§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("ZugeingabeEMailBody_7N"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * Please wait for the next e-mail from the game host. [7O]
	   */
	public static String ZugeingabeEMailEndlosschleife(boolean symbol) {
		return symbol ? "£7O£":messages.getString("ZugeingabeEMailEndlosschleife_7O");
	}

	/**
	   * Game: {0}\nYear: {1}\nBuild: {2}\n\nHi {3},\n\nthe game has been evaluated. Please proceed with entering your moves.\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start STERN and select  "Game -> Import e-mail game from clipboard".\n\n3. Press the button "Insert" to copy the contents of the clipboard into the text field.\n\n4. Press the button "OK".\n\nNow enter your moves. When you are finished, an e-mail opens automatically in your e-mail client. This e-mail contains your moves and the address of the game host. Please send it without changes.\n\nThanks!\nYou game host [7P]
	   */
	public static String ZugeingabeEMailBody2(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£7P§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("ZugeingabeEMailBody2_7P"), arg0, arg1, arg2, arg3);
	}

	/**
	   * An e-mail was created in your standard e-mail client. [7Q]
	   */
	public static String ZugeingabeEMailErzeugt(boolean symbol) {
		return symbol ? "£7Q£":messages.getString("ZugeingabeEMailErzeugt_7Q");
	}

	/**
	   * Please send the e-mail to the game host without changes. [7R]
	   */
	public static String ZugeingabeEMailErzeugt2(boolean symbol) {
		return symbol ? "£7R£":messages.getString("ZugeingabeEMailErzeugt2_7R");
	}

	/**
	   * {0} e-mails were created in your standard e-mail client. [7S]
	   */
	public static String ZugeingabeEMailErzeugt3(boolean symbol, String arg0) {
		return symbol ? "£7S§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeEMailErzeugt3_7S"), arg0);
	}

	/**
	   * Please send the e-mail to the players without changes. [7T]
	   */
	public static String ZugeingabeEMailErzeugt4(boolean symbol) {
		return symbol ? "£7T£":messages.getString("ZugeingabeEMailErzeugt4_7T");
	}

	/**
	   * Finish [7U]
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
	   * Phys. Inventory [7W]
	   */
	public static String ZugeingabeInventur(boolean symbol) {
		return symbol ? "£7W£":messages.getString("ZugeingabeInventur_7W");
	}

	/**
	   * No alliance. [7X]
	   */
	public static String ZugeingabeKeinBuendnis(boolean symbol) {
		return symbol ? "£7X£":messages.getString("ZugeingabeKeinBuendnis_7X");
	}

	/**
	   * Recently used e-mail addresses [80]
	   */
	public static String EmailAdressenJDialogTitel(boolean symbol) {
		return symbol ? "£80£":messages.getString("EmailAdressenJDialogTitel_80");
	}

	/**
	   * E-mail mode settings [81]
	   */
	public static String EmailSettingsJDialogTitel(boolean symbol) {
		return symbol ? "£81£":messages.getString("EmailSettingsJDialogTitel_81");
	}

	/**
	   * $ production [82]
	   */
	public static String Energieproduktion(boolean symbol) {
		return symbol ? "£82£":messages.getString("Energieproduktion_82");
	}

	/**
	   * Distance matrix [83]
	   */
	public static String Entfernungstabelle(boolean symbol) {
		return symbol ? "£83£":messages.getString("Entfernungstabelle_83");
	}

	/**
	   * Error [84]
	   */
	public static String Fehler(boolean symbol) {
		return symbol ? "£84£":messages.getString("Fehler_84");
	}

	/**
	   * Load error [85]
	   */
	public static String FehlerBeimLaden(boolean symbol) {
		return symbol ? "£85£":messages.getString("FehlerBeimLaden_85");
	}

	/**
	   * Main menu [86]
	   */
	public static String Hauptmenue(boolean symbol) {
		return symbol ? "£86£":messages.getString("Hauptmenue_86");
	}

	/**
	   * Do you want to add the positions to the high score list? [87]
	   */
	public static String HighscoreFrage(boolean symbol) {
		return symbol ? "£87£":messages.getString("HighscoreFrage_87");
	}

	/**
	   * Arrival [88]
	   */
	public static String InventurAnkunft(boolean symbol) {
		return symbol ? "£88£":messages.getString("InventurAnkunft_88");
	}

	/**
	   * Terminate alliance [8C]
	   */
	public static String ZugeingabeKuendigen(boolean symbol) {
		return symbol ? "£8C£":messages.getString("ZugeingabeKuendigen_8C");
	}

	/**
	   * You cannot start more than {0} fighter(s). [8D]
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
	   * Mine (250) [8F]
	   */
	public static String ZugeingabeMine250(boolean symbol) {
		return symbol ? "£8F£":messages.getString("ZugeingabeMine250_8F");
	}

	/**
	   * Mine (50) [8G]
	   */
	public static String ZugeingabeMine50(boolean symbol) {
		return symbol ? "£8G£":messages.getString("ZugeingabeMine50_8G");
	}

	/**
	   * Mine (500) [8H]
	   */
	public static String ZugeingabeMine500(boolean symbol) {
		return symbol ? "£8H£":messages.getString("ZugeingabeMine500_8H");
	}

	/**
	   * Which type? [8I]
	   */
	public static String ZugeingabeMineTypFrage(boolean symbol) {
		return symbol ? "£8I£":messages.getString("ZugeingabeMineTypFrage_8I");
	}

	/**
	   * Destination sector/planet [8J]
	   */
	public static String ZugeingabeMineZielsektor(boolean symbol) {
		return symbol ? "£8J£":messages.getString("ZugeingabeMineZielsektor_8J");
	}

	/**
	   * Minesweeper [8K]
	   */
	public static String ZugeingabeMinenraeumer(boolean symbol) {
		return symbol ? "£8K£":messages.getString("ZugeingabeMinenraeumer_8K");
	}

	/**
	   * Mission or transfer? [8L]
	   */
	public static String ZugeingabeMissionTransferFrage(boolean symbol) {
		return symbol ? "£8L£":messages.getString("ZugeingabeMissionTransferFrage_8L");
	}

	/**
	   * Current allies [8M]
	   */
	public static String ZugeingabeMomentaneBuendnisstruktur(boolean symbol) {
		return symbol ? "£8M£":messages.getString("ZugeingabeMomentaneBuendnisstruktur_8M");
	}

	/**
	   * Enter alliance members [8N]
	   */
	public static String ZugeingabeNeueBuendnisstruktur(boolean symbol) {
		return symbol ? "£8N£":messages.getString("ZugeingabeNeueBuendnisstruktur_8N");
	}

	/**
	   * You don't have enough fighters. [8O]
	   */
	public static String ZugeingabeNichtGenugRaumer(boolean symbol) {
		return symbol ? "£8O£":messages.getString("ZugeingabeNichtGenugRaumer_8O");
	}

	/**
	   * Patrol [8P]
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
	   * You are not the owner of the planet. [8T]
	   */
	public static String ZugeingabePlanetGehoertNicht(boolean symbol) {
		return symbol ? "£8T£":messages.getString("ZugeingabePlanetGehoertNicht_8T");
	}

	/**
	   * Fighters [8V]
	   */
	public static String ZugeingabeRaumer(boolean symbol) {
		return symbol ? "£8V£":messages.getString("ZugeingabeRaumer_8V");
	}

	/**
	   * Send current game to all players [8W]
	   */
	public static String ZugeingabeSpielstandVerschicken(boolean symbol) {
		return symbol ? "£8W£":messages.getString("ZugeingabeSpielstandVerschicken_8W");
	}

	/**
	   * The moves do not belong to this year. [8X]
	   */
	public static String ZugeingabeSpielzuegeFalscheRunde(boolean symbol) {
		return symbol ? "£8X£":messages.getString("ZugeingabeSpielzuegeFalscheRunde_8X");
	}

	/**
	   * Import moves of a player [8Y]
	   */
	public static String ZugeingabeSpielzuegeImportieren(boolean symbol) {
		return symbol ? "£8Y£":messages.getString("ZugeingabeSpielzuegeImportieren_8Y");
	}

	/**
	   * Moves of {0} successfully imported. [8Z]
	   */
	public static String ZugeingabeSpielzuegeImportiert(boolean symbol, String arg0) {
		return symbol ? "£8Z§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeSpielzuegeImportiert_8Z"), arg0);
	}

	/**
	   * Count [90]
	   */
	public static String InventurAnzahl(boolean symbol) {
		return symbol ? "£90£":messages.getString("InventurAnzahl_90");
	}

	/**
	   * Scout [91]
	   */
	public static String InventurAufklaerer(boolean symbol) {
		return symbol ? "£91£":messages.getString("InventurAufklaerer_91");
	}

	/**
	   * Sco [92]
	   */
	public static String InventurAufklaererKurz(boolean symbol) {
		return symbol ? "£92£":messages.getString("InventurAufklaererKurz_92");
	}

	/**
	   * Owner [93]
	   */
	public static String InventurBesitzerKurz(boolean symbol) {
		return symbol ? "£93£":messages.getString("InventurBesitzerKurz_93");
	}

	/**
	   * Alliance [94]
	   */
	public static String InventurBuendnis(boolean symbol) {
		return symbol ? "£94£":messages.getString("InventurBuendnis_94");
	}

	/**
	   * Alliance [95]
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
	   * $Sup [97]
	   */
	public static String InventurEnergievorratKurz(boolean symbol) {
		return symbol ? "£97£":messages.getString("InventurEnergievorratKurz_97");
	}

	/**
	   * Ds [98]
	   */
	public static String InventurFestungKurz(boolean symbol) {
		return symbol ? "£98£":messages.getString("InventurFestungKurz_98");
	}

	/**
	   * DsFg [99]
	   */
	public static String InventurFestungRaumerKurz(boolean symbol) {
		return symbol ? "£99£":messages.getString("InventurFestungRaumerKurz_99");
	}

	/**
	   * Moves were not imported. [9A]
	   */
	public static String ZugeingabeSpielzuegeNichtImportiert(boolean symbol) {
		return symbol ? "£9A£":messages.getString("ZugeingabeSpielzuegeNichtImportiert_9A");
	}

	/**
	   * +++ Move entered +++ [9B]
	   */
	public static String ZugeingabeStartErfolgreich(boolean symbol) {
		return symbol ? "£9B£":messages.getString("ZugeingabeStartErfolgreich_9B");
	}

	/**
	   * Start planet [9C]
	   */
	public static String ZugeingabeStartplanet(boolean symbol) {
		return symbol ? "£9C£":messages.getString("ZugeingabeStartplanet_9C");
	}

	/**
	   * Enter moves [9D]
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
	   * Last move undone. [9G]
	   */
	public static String ZugeingabeUndoErfolg(boolean symbol) {
		return symbol ? "£9G£":messages.getString("ZugeingabeUndoErfolg_9G");
	}

	/**
	   * Do you want to undo the last move? [9H]
	   */
	public static String ZugeingabeUndoFrage(boolean symbol) {
		return symbol ? "£9H£":messages.getString("ZugeingabeUndoFrage_9H");
	}

	/**
	   * How many $ (max. {0})? [9J]
	   */
	public static String ZugeingabeWievieleEe(boolean symbol, String arg0) {
		return symbol ? "£9J§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeWievieleEe_9J"), arg0);
	}

	/**
	   * To which planet do you want to transfer a minelayer (100)? [9K]
	   */
	public static String ZugeingabeWohin100erMine(boolean symbol) {
		return symbol ? "£9K£":messages.getString("ZugeingabeWohin100erMine_9K");
	}

	/**
	   * To which planet do you want to transfer a minelayer (250)? [9L]
	   */
	public static String ZugeingabeWohin250erMine(boolean symbol) {
		return symbol ? "£9L£":messages.getString("ZugeingabeWohin250erMine_9L");
	}

	/**
	   * To which planet do you want to transfer a minelayer (500)? [9M]
	   */
	public static String ZugeingabeWohin500erMine(boolean symbol) {
		return symbol ? "£9M£":messages.getString("ZugeingabeWohin500erMine_9M");
	}

	/**
	   * To which planet do you want to transfer a minelayer (50)? [9N]
	   */
	public static String ZugeingabeWohin50erMine(boolean symbol) {
		return symbol ? "£9N£":messages.getString("ZugeingabeWohin50erMine_9N");
	}

	/**
	   * To which planet do you want to transfer a scout? [9O]
	   */
	public static String ZugeingabeWohinAufklaerer(boolean symbol) {
		return symbol ? "£9O£":messages.getString("ZugeingabeWohinAufklaerer_9O");
	}

	/**
	   * To which planet do you want to transfer a minesweeper? [9P]
	   */
	public static String ZugeingabeWohinMinenraumer(boolean symbol) {
		return symbol ? "£9P£":messages.getString("ZugeingabeWohinMinenraumer_9P");
	}

	/**
	   * To which planet do you want to transfer a patrol? [9Q]
	   */
	public static String ZugeingabeWohinPatrouille(boolean symbol) {
		return symbol ? "£9Q£":messages.getString("ZugeingabeWohinPatrouille_9Q");
	}

	/**
	   * To which planet do you want to transfer {0} fighter(s)? [9R]
	   */
	public static String ZugeingabeWohinRaumer(boolean symbol, String arg0) {
		return symbol ? "£9R§"+arg0+"£":MessageFormat.format(messages.getString("ZugeingabeWohinRaumer_9R"), arg0);
	}

	/**
	   * To which planet do you want to transfer a transporter? [9S]
	   */
	public static String ZugeingabeWohinTransporter(boolean symbol) {
		return symbol ? "£9S£":messages.getString("ZugeingabeWohinTransporter_9S");
	}

	/**
	   * Destination planet [9T]
	   */
	public static String ZugeingabeZielplanet(boolean symbol) {
		return symbol ? "£9T£":messages.getString("ZugeingabeZielplanet_9T");
	}

	/**
	   * This is the start planet. Enter another planet. [9U]
	   */
	public static String ZugeingabeZielplanetIstStartplanet(boolean symbol) {
		return symbol ? "£9U£":messages.getString("ZugeingabeZielplanetIstStartplanet_9U");
	}

	/**
	   * You cannot transport this amount of $. [9V]
	   */
	public static String ZugeingabeZuVielEe(boolean symbol) {
		return symbol ? "£9V£":messages.getString("ZugeingabeZuVielEe_9V");
	}

	/**
	   * Random [9X]
	   */
	public static String ZugeingabeZufaelligerSpieler(boolean symbol) {
		return symbol ? "£9X£":messages.getString("ZugeingabeZufaelligerSpieler_9X");
	}

	/**
	   * Back [9Y]
	   */
	public static String Zurueck(boolean symbol) {
		return symbol ? "£9Y£":messages.getString("Zurueck_9Y");
	}

	/**
	   * Evaluation [9Z]
	   */
	public static String Auswertung(boolean symbol) {
		return symbol ? "£9Z£":messages.getString("Auswertung_9Z");
	}

	/**
	   * No connection to server {0} [AA]
	   */
	public static String ClientSettingsJDialogKeineVerbindung2(boolean symbol, String arg0) {
		return symbol ? "£AA§"+arg0+"£":MessageFormat.format(messages.getString("ClientSettingsJDialogKeineVerbindung2_AA"), arg0);
	}

	/**
	   * You must not combine '0' with other inputs. [AC]
	   */
	public static String ZugeingabeBuendnis0NichtKombinieren(boolean symbol) {
		return symbol ? "£AC£":messages.getString("ZugeingabeBuendnis0NichtKombinieren_AC");
	}

	/**
	   * There are no moves. Do you want to abort entering moves? [AD]
	   */
	public static String ZugeingabeKeineSpielzuegeAbbrechen(boolean symbol) {
		return symbol ? "£AD£":messages.getString("ZugeingabeKeineSpielzuegeAbbrechen_AD");
	}

	/**
	   * Mine (100) [AE]
	   */
	public static String ZugeingabeMine100(boolean symbol) {
		return symbol ? "£AE£":messages.getString("ZugeingabeMine100_AE");
	}

	/**
	   * Send changes to server [AF]
	   */
	public static String ServerAdminAnDenSeverSchicken(boolean symbol) {
		return symbol ? "£AF£":messages.getString("ServerAdminAnDenSeverSchicken_AF");
	}

	/**
	   * User ID [AG]
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
	   * Users [AI]
	   */
	public static String ServerAdminSpieler(boolean symbol) {
		return symbol ? "£AI£":messages.getString("ServerAdminSpieler_AI");
	}

	/**
	   * Shut down server [AJ]
	   */
	public static String ServerAdminShutdown(boolean symbol) {
		return symbol ? "£AJ£":messages.getString("ServerAdminShutdown_AJ");
	}

	/**
	   * File [AK]
	   */
	public static String ServerAdminDatei(boolean symbol) {
		return symbol ? "£AK£":messages.getString("ServerAdminDatei_AK");
	}

	/**
	   * Server URL [AL]
	   */
	public static String ServerAdminUrl(boolean symbol) {
		return symbol ? "£AL£":messages.getString("ServerAdminUrl_AL");
	}

	/**
	   * Server port [AM]
	   */
	public static String ServerAdminPort(boolean symbol) {
		return symbol ? "£AM£":messages.getString("ServerAdminPort_AM");
	}

	/**
	   * Connection test [AN]
	   */
	public static String ServerAdminVerbindungstest(boolean symbol) {
		return symbol ? "£AN£":messages.getString("ServerAdminVerbindungstest_AN");
	}

	/**
	   * Administrator credentials [AO]
	   */
	public static String ServerAdminAdminAuth(boolean symbol) {
		return symbol ? "£AO£":messages.getString("ServerAdminAdminAuth_AO");
	}

	/**
	   * The file {0}\ndoes not contain valid credentials. [AP]
	   */
	public static String UngueltigeAnmeldedaten(boolean symbol, String arg0) {
		return symbol ? "£AP§"+arg0+"£":MessageFormat.format(messages.getString("UngueltigeAnmeldedaten_AP"), arg0);
	}

	/**
	   * Connection successful [AQ]
	   */
	public static String VerbindungErfolgreich(boolean symbol) {
		return symbol ? "£AQ£":messages.getString("VerbindungErfolgreich_AQ");
	}

	/**
	   * Connection not successful [AR]
	   */
	public static String VerbindungNichtErfolgreich(boolean symbol) {
		return symbol ? "£AR£":messages.getString("VerbindungNichtErfolgreich_AR");
	}

	/**
	   * Do you really want to create user [{0}]? [AS]
	   */
	public static String ServerAdminBenutzerAnlegenFrage(boolean symbol, String arg0) {
		return symbol ? "£AS§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminBenutzerAnlegenFrage_AS"), arg0);
	}

	/**
	   * Do you want to shut down the STERN server? [AT]
	   */
	public static String ServerAdminShutdownFrage(boolean symbol) {
		return symbol ? "£AT£":messages.getString("ServerAdminShutdownFrage_AT");
	}

	/**
	   * Are you really sure? [AU]
	   */
	public static String AreYouSure(boolean symbol) {
		return symbol ? "£AU£":messages.getString("AreYouSure_AU");
	}

	/**
	   * The STERN server shutting down... [AV]
	   */
	public static String ServerAdminShutdownDone(boolean symbol) {
		return symbol ? "£AV£":messages.getString("ServerAdminShutdownDone_AV");
	}

	/**
	   * Connection error [AW]
	   */
	public static String Verbindungsfehler(boolean symbol) {
		return symbol ? "£AW£":messages.getString("Verbindungsfehler_AW");
	}

	/**
	   * (No file selected) [AX]
	   */
	public static String KeineDateiAusgewaehlt(boolean symbol) {
		return symbol ? "£AX£":messages.getString("KeineDateiAusgewaehlt_AX");
	}

	/**
	   * Server credentials [AY]
	   */
	public static String ServerZugangsdaten(boolean symbol) {
		return symbol ? "£AY£":messages.getString("ServerZugangsdaten_AY");
	}

	/**
	   * You haven't entered the server credentials yet. [AZ]
	   */
	public static String ServerZugangsdatenNichtHinterlegt(boolean symbol) {
		return symbol ? "£AZ£":messages.getString("ServerZugangsdatenNichtHinterlegt_AZ");
	}

	/**
	   * Games on the STERN server (user {0}) [BA]
	   */
	public static String ServerbasierteSpiele(boolean symbol, String arg0) {
		return symbol ? "£BA§"+arg0+"£":MessageFormat.format(messages.getString("ServerbasierteSpiele_BA"), arg0);
	}

	/**
	   * Administrate the STERN server [BB]
	   */
	public static String SternServerVerwalten(boolean symbol) {
		return symbol ? "£BB£":messages.getString("SternServerVerwalten_BB");
	}

	/**
	   * Connected to STERN server {0}:{1} as user {2} [BC]
	   */
	public static String VerbundenMitServer(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£BC§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("VerbundenMitServer_BC"), arg0, arg1, arg2);
	}

	/**
	   * Other players are awaiting your moves. [BD]
	   */
	public static String MitspielerWarten(boolean symbol) {
		return symbol ? "£BD£":messages.getString("MitspielerWarten_BD");
	}

	/**
	   * [STERN] Your new user [{0}] [BE]
	   */
	public static String EmailSubjectNeuerUser(boolean symbol, String arg0) {
		return symbol ? "£BE§"+arg0+"£":MessageFormat.format(messages.getString("EmailSubjectNeuerUser_BE"), arg0);
	}

	/**
	   * Hi {0},\n\nwelcome to STERN! Your new user [{1}] on server {2}:{3} has been created and only needs to be activated.\n\nPlease proceed as follows:\n\n1. Select this whole e-mail text (for example, with ctrl + A), and copy it to the clipboard of your computer (for example, with ctrl + C).\n\n2. Start STERN and select "Game -> Server credentials...".\n\n3. Check the option "Connection to server -> Activate"\n\n4. Press the button "Activate user", and insert the contents of the clipboard into the text field.\n\n5. Enter the password that you got from your server administrator.\n\n6. Press the button "OK". Choose a storage location for the file containing the user credentials.\n\nYour user is now active, and your user credentials are stored in STERN.\n\nEnjoy STERN!\nYour server administrator [BF]
	   */
	public static String NeuerUserEMailBody(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£BF§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("NeuerUserEMailBody_BF"), arg0, arg1, arg2, arg3);
	}

	/**
	   * Server connection [BG]
	   */
	public static String Serververbindung(boolean symbol) {
		return symbol ? "£BG£":messages.getString("Serververbindung_BG");
	}

	/**
	   * Activate [BH]
	   */
	public static String Aktivieren(boolean symbol) {
		return symbol ? "£BH£":messages.getString("Aktivieren_BH");
	}

	/**
	   * Activate user [BI]
	   */
	public static String BenutzerAktivieren(boolean symbol) {
		return symbol ? "£BI£":messages.getString("BenutzerAktivieren_BI");
	}

	/**
	   * Do you want to acticate your user [{0}] on server\n{1}:{2}?\nSelect the storage location for the authentication file in the following dialog. [BJ]
	   */
	public static String BenutzerAktivierenFrage(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£BJ§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("BenutzerAktivierenFrage_BJ"), arg0, arg1, arg2);
	}

	/**
	   * Save authentication file [BK]
	   */
	public static String BenutzerAktivierenAbspeichern(boolean symbol) {
		return symbol ? "£BK£":messages.getString("BenutzerAktivierenAbspeichern_BK");
	}

	/**
	   * The user was activated successfully. [BL]
	   */
	public static String BenutzerAktivierenErfolg(boolean symbol) {
		return symbol ? "£BL£":messages.getString("BenutzerAktivierenErfolg_BL");
	}

	/**
	   * Players are waiting for me [BM]
	   */
	public static String ServerGamesSpielerWarten(boolean symbol) {
		return symbol ? "£BM£":messages.getString("ServerGamesSpielerWarten_BM");
	}

	/**
	   * I am waiting for other players [BN]
	   */
	public static String ServerGamesIchWarte(boolean symbol) {
		return symbol ? "£BN£":messages.getString("ServerGamesIchWarte_BN");
	}

	/**
	   * Finalized games [BO]
	   */
	public static String ServerGamesBeendeteSpiele(boolean symbol) {
		return symbol ? "£BO£":messages.getString("ServerGamesBeendeteSpiele_BO");
	}

	/**
	   * New game [BP]
	   */
	public static String ServerGamesNeuesSpiel(boolean symbol) {
		return symbol ? "£BP£":messages.getString("ServerGamesNeuesSpiel_BP");
	}

	/**
	   * New play field [BQ]
	   */
	public static String ServerGamesNeuesSpielfeld(boolean symbol) {
		return symbol ? "£BQ£":messages.getString("ServerGamesNeuesSpielfeld_BQ");
	}

	/**
	   * Publish game [BR]
	   */
	public static String ServerGamesSubmit(boolean symbol) {
		return symbol ? "£BR£":messages.getString("ServerGamesSubmit_BR");
	}

	/**
	   * You have to assign users to all players. [BS]
	   */
	public static String ServerGamesSubmitNamenZuweisen(boolean symbol) {
		return symbol ? "£BS£":messages.getString("ServerGamesSubmitNamenZuweisen_BS");
	}

	/**
	   * Do you really want to publish the new game [{0}] on the server? [BT]
	   */
	public static String ServerGamesSubmitFrage(boolean symbol, String arg0) {
		return symbol ? "£BT§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitFrage_BT"), arg0);
	}

	/**
	   * The new game [{0}] was created on the server. [BU]
	   */
	public static String ServerGamesSubmitAngelegt(boolean symbol, String arg0) {
		return symbol ? "£BU§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitAngelegt_BU"), arg0);
	}

	/**
	   * Load game [BV]
	   */
	public static String ServerGamesLaden(boolean symbol) {
		return symbol ? "£BV£":messages.getString("ServerGamesLaden_BV");
	}

	/**
	   * {0} players, {1} planets [BW]
	   */
	public static String ServerGamesSpielerPlaneten(boolean symbol, String arg0, String arg1) {
		return symbol ? "£BW§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerGamesSpielerPlaneten_BW"), arg0, arg1);
	}

	/**
	   * Start: {0} [BY]
	   */
	public static String ServerGamesBegonnen(boolean symbol, String arg0) {
		return symbol ? "£BY§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesBegonnen_BY"), arg0);
	}

	/**
	   * Name of the game [BZ]
	   */
	public static String ServerGamesSpielname(boolean symbol) {
		return symbol ? "£BZ£":messages.getString("ServerGamesSpielname_BZ");
	}

	/**
	   * Welcome to the setup of the STERN server! [CA]
	   */
	public static String ServerWillkommen(boolean symbol) {
		return symbol ? "£CA£":messages.getString("ServerWillkommen_CA");
	}

	/**
	   * default [CB]
	   */
	public static String ServerVoreingestellt(boolean symbol) {
		return symbol ? "£CB£":messages.getString("ServerVoreingestellt_CB");
	}

	/**
	   * E-mail address of the administrator [CC]
	   */
	public static String ServerEmailAdmin(boolean symbol) {
		return symbol ? "£CC£":messages.getString("ServerEmailAdmin_CC");
	}

	/**
	   * Are all your entries correct? Yes = [1] / No = [other key] [CD]
	   */
	public static String ServerInitConfirm(boolean symbol) {
		return symbol ? "£CD£":messages.getString("ServerInitConfirm_CD");
	}

	/**
	   * Server setup aborted. Application terminated. [CE]
	   */
	public static String ServerInitAbort(boolean symbol) {
		return symbol ? "£CE£":messages.getString("ServerInitAbort_CE");
	}

	/**
	   * Starting server... [CF]
	   */
	public static String ServerStarting(boolean symbol) {
		return symbol ? "£CF£":messages.getString("ServerStarting_CF");
	}

	/**
	   * STERN server started on port {0} [CG]
	   */
	public static String ServerStarted(boolean symbol, String arg0) {
		return symbol ? "£CG§"+arg0+"£":MessageFormat.format(messages.getString("ServerStarted_CG"), arg0);
	}

	/**
	   * The STERN server on port {0} cannot be started. The port might already be in use. Application terminated. [CH]
	   */
	public static String ServerNotStarted(boolean symbol, String arg0) {
		return symbol ? "£CH§"+arg0+"£":MessageFormat.format(messages.getString("ServerNotStarted_CH"), arg0);
	}

	/**
	   * Waiting for inbound connection... [CI]
	   */
	public static String ServerWaiting(boolean symbol) {
		return symbol ? "£CI£":messages.getString("ServerWaiting_CI");
	}

	/**
	   * Incoming connection from client IP {0}. Thread {1} is being launched. [CJ]
	   */
	public static String ServerIncomingConnection(boolean symbol, String arg0, String arg1) {
		return symbol ? "£CJ§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerIncomingConnection_CJ"), arg0, arg1);
	}

	/**
	   * Closing server socket. [CK]
	   */
	public static String ServerISocketClose(boolean symbol) {
		return symbol ? "£CK£":messages.getString("ServerISocketClose_CK");
	}

	/**
	   * File {0} created. [CL]
	   */
	public static String ServerIDateiAngelegt(boolean symbol, String arg0) {
		return symbol ? "£CL§"+arg0+"£":MessageFormat.format(messages.getString("ServerIDateiAngelegt_CL"), arg0);
	}

	/**
	   * Date [CM]
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
	   * Message [CQ]
	   */
	public static String ServerILogMeldung(boolean symbol) {
		return symbol ? "£CQ£":messages.getString("ServerILogMeldung_CQ");
	}

	/**
	   * Log-on attempt with invalid user ID length {0} [CR]
	   */
	public static String ServerErrorUngueltigeLaengeBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CR§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorUngueltigeLaengeBenutzer_CR"), arg0);
	}

	/**
	   * Incoming request from user {0} [CS]
	   */
	public static String ServerBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CS§"+arg0+"£":MessageFormat.format(messages.getString("ServerBenutzer_CS"), arg0);
	}

	/**
	   * Log-on attempt with invalid user [{0}] [CT]
	   */
	public static String ServerErrorUngueltigerBenutzer(boolean symbol, String arg0) {
		return symbol ? "£CT§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorUngueltigerBenutzer_CT"), arg0);
	}

	/**
	   * Error when receiving the request: {0} [CU]
	   */
	public static String ServerErrorRequestReceive(boolean symbol, String arg0) {
		return symbol ? "£CU§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorRequestReceive_CU"), arg0);
	}

	/**
	   * Error when sending the response message: {0} [CV]
	   */
	public static String ServerErrorSendResponse(boolean symbol, String arg0) {
		return symbol ? "£CV§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSendResponse_CV"), arg0);
	}

	/**
	   * Wrong token. Authorization failed. [CW]
	   */
	public static String ServerErrorNichtAuthorisiert(boolean symbol) {
		return symbol ? "£CW£":messages.getString("ServerErrorNichtAuthorisiert_CW");
	}

	/**
	   * Error when decrypting the request message: {0} [CX]
	   */
	public static String ServerErrorDecode(boolean symbol, String arg0) {
		return symbol ? "£CX§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorDecode_CX"), arg0);
	}

	/**
	   * Request message of type {0} from user {1} [CY]
	   */
	public static String ServerInfoMessageType(boolean symbol, String arg0, String arg1) {
		return symbol ? "£CY§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerInfoMessageType_CY"), arg0, arg1);
	}

	/**
	   * STERN Display connection IP {0} is closing. [CZ]
	   */
	public static String ServerInfoClientClosing(boolean symbol, String arg0) {
		return symbol ? "£CZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerInfoClientClosing_CZ"), arg0);
	}

	/**
	   * Error when closing the socket connection: {0} [DA]
	   */
	public static String ServerErrorClientClosing(boolean symbol, String arg0) {
		return symbol ? "£DA§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorClientClosing_DA"), arg0);
	}

	/**
	   * Thread is being closed. [DB]
	   */
	public static String ServerThreadClosing(boolean symbol) {
		return symbol ? "£DB£":messages.getString("ServerThreadClosing_DB");
	}

	/**
	   * As the user [{0}] you are not authorized to perform this action. [DC]
	   */
	public static String ServerErrorNotAuthorized(boolean symbol, String arg0) {
		return symbol ? "£DC§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorNotAuthorized_DC"), arg0);
	}

	/**
	   * The user [{0}] does not participate in this game. [DD]
	   */
	public static String ServerErrorSpielerNimmNichtTeil(boolean symbol, String arg0) {
		return symbol ? "£DD§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSpielerNimmNichtTeil_DD"), arg0);
	}

	/**
	   * The game [{0}] does not exist! [DE]
	   */
	public static String ServerErrorSpielExistiertNicht(boolean symbol, String arg0) {
		return symbol ? "£DE§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorSpielExistiertNicht_DE"), arg0);
	}

	/**
	   * User data is incomplete. [DF]
	   */
	public static String ServerErrorAdminNeuerUser(boolean symbol) {
		return symbol ? "£DF£":messages.getString("ServerErrorAdminNeuerUser_DF");
	}

	/**
	   * The user ID [{0}] already exists or is invalid. The length of a user ID must be between {1} and {2} characters. It must only contains the characters a-z, A-Z, and 0-9. [DG]
	   */
	public static String ServerErrorAdminUserUnzulaessig(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£DG§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("ServerErrorAdminUserUnzulaessig_DG"), arg0, arg1, arg2);
	}

	/**
	   * New inactive user [{0}] created. [DH]
	   */
	public static String ServerInfoInaktiverBenutzerAngelegt(boolean symbol, String arg0) {
		return symbol ? "£DH§"+arg0+"£":MessageFormat.format(messages.getString("ServerInfoInaktiverBenutzerAngelegt_DH"), arg0);
	}

	/**
	   * The user [{0}] has already been activated. [DI]
	   */
	public static String ServerErrorBenutzerBereitsAktiviert(boolean symbol, String arg0) {
		return symbol ? "£DI§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorBenutzerBereitsAktiviert_DI"), arg0);
	}

	/**
	   * The year has already been evaluated. [DK]
	   */
	public static String ServerErrorJahrVorbei(boolean symbol) {
		return symbol ? "£DK£":messages.getString("ServerErrorJahrVorbei_DK");
	}

	/**
	   * The moves were successfully transmitted to the server. [DM]
	   */
	public static String ZugeingabePostMovesSuccess(boolean symbol) {
		return symbol ? "£DM£":messages.getString("ZugeingabePostMovesSuccess_DM");
	}

	/**
	   * The moves could not be transmitted to te server. [DN]
	   */
	public static String ZugeingabePostMovesError(boolean symbol) {
		return symbol ? "£DN£":messages.getString("ZugeingabePostMovesError_DN");
	}

	/**
	   * Try again [DP]
	   */
	public static String NochmalVersuchen(boolean symbol) {
		return symbol ? "£DP£":messages.getString("NochmalVersuchen_DP");
	}

	/**
	   * Please wait until the reload icon appears. [DQ]
	   */
	public static String WartenBisNaechsteZugeingabe(boolean symbol) {
		return symbol ? "£DQ£":messages.getString("WartenBisNaechsteZugeingabe_DQ");
	}

	/**
	   * The year's evaluation is available. [DR]
	   */
	public static String AuswertungVerfuegbar(boolean symbol) {
		return symbol ? "£DR£":messages.getString("AuswertungVerfuegbar_DR");
	}

	/**
	   * Language settings [DS]
	   */
	public static String SpracheDialogTitle(boolean symbol) {
		return symbol ? "£DS£":messages.getString("SpracheDialogTitle_DS");
	}

	/**
	   * Language [DT]
	   */
	public static String Sprache(boolean symbol) {
		return symbol ? "£DT£":messages.getString("Sprache_DT");
	}

	/**
	   * The new language settings become effective only after a restart of the application. The application will be closed now. [DU]
	   */
	public static String SpracheDialogFrage(boolean symbol) {
		return symbol ? "£DU£":messages.getString("SpracheDialogFrage_DU");
	}

	/**
	   * Language [DV]
	   */
	public static String MenuSpracheinstellungen(boolean symbol) {
		return symbol ? "£DV£":messages.getString("MenuSpracheinstellungen_DV");
	}

	/**
	   * Other key [DW]
	   */
	public static String AndereTaste(boolean symbol) {
		return symbol ? "£DW£":messages.getString("AndereTaste_DW");
	}

	/**
	   * Server communication has been deactivated. [DX]
	   */
	public static String ServerKommunikationInaktiv(boolean symbol) {
		return symbol ? "£DX£":messages.getString("ServerKommunikationInaktiv_DX");
	}

	/**
	   * Minesweeper (transfer) [E0]
	   */
	public static String InventurMinenraeumerTransfer(boolean symbol) {
		return symbol ? "£E0£":messages.getString("InventurMinenraeumerTransfer_E0");
	}

	/**
	   * Destination of a transfer must be a planet. [E1]
	   */
	public static String ZugeingabeZielTransfer(boolean symbol) {
		return symbol ? "£E1£":messages.getString("ZugeingabeZielTransfer_E1");
	}

	/**
	   * Data could not be interpreted. Check the following:\n\n1. Is the data coming from a STERN e-mail?\n2. Is the password correct?\n3. Did you use an e-mail from a wrong context?\n4. Your STERN build ({0}) and the build of the sender (see e-mail) are too different.\n\u0009\u0009\u0009\u0009\u0009\u0009 [E2]
	   */
	public static String ClipboardImportJDIalogImportFehlerPassword(boolean symbol, String arg0) {
		return symbol ? "£E2§"+arg0+"£":MessageFormat.format(messages.getString("ClipboardImportJDIalogImportFehlerPassword_E2"), arg0);
	}

	/**
	   * Activation password [E3]
	   */
	public static String Aktivierungspasswort(boolean symbol) {
		return symbol ? "£E3£":messages.getString("Aktivierungspasswort_E3");
	}

	/**
	   * (Repeat password) [E5]
	   */
	public static String PasswortWiederholen(boolean symbol) {
		return symbol ? "£E5£":messages.getString("PasswortWiederholen_E5");
	}

	/**
	   * The passwords are not equal. [E6]
	   */
	public static String PasswoerterUnterschiedlich(boolean symbol) {
		return symbol ? "£E6£":messages.getString("PasswoerterUnterschiedlich_E6");
	}

	/**
	   * The activation password must be at least three characters long. [E7]
	   */
	public static String PasswortZuKurz(boolean symbol) {
		return symbol ? "£E7£":messages.getString("PasswortZuKurz_E7");
	}

	/**
	   * Password [E8]
	   */
	public static String Passwort(boolean symbol) {
		return symbol ? "£E8£":messages.getString("Passwort_E8");
	}

	/**
	   * The server requires at least build {0}. You are using build {1}. [EE]
	   */
	public static String ServerBuildFalsch(boolean symbol, String arg0, String arg1) {
		return symbol ? "£EE§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerBuildFalsch_EE"), arg0, arg1);
	}

	/**
	   * STERN Display server [EF]
	   */
	public static String SternScreenSharingServer(boolean symbol) {
		return symbol ? "£EF£":messages.getString("SternScreenSharingServer_EF");
	}

	/**
	   * [STERN] {0} invited you to the new game {1} [EG]
	   */
	public static String EmailSubjectEingeladen(boolean symbol, String arg0, String arg1) {
		return symbol ? "£EG§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("EmailSubjectEingeladen_EG"), arg0, arg1);
	}

	/**
	   * Hello,\n\nwelcome to STERN! {0} invited you to the game {1} on server {2}:{3}.\n\nHave fun!\nYour game host [EH]
	   */
	public static String EmailBodyEingeladen(boolean symbol, String arg0, String arg1, String arg2, String arg3) {
		return symbol ? "£EH§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"£":MessageFormat.format(messages.getString("EmailBodyEingeladen_EH"), arg0, arg1, arg2, arg3);
	}

	/**
	   * You are not logged in as player {0} at the STERN server. [EI]
	   */
	public static String SpielerNichtAngemeldet(boolean symbol, String arg0) {
		return symbol ? "£EI§"+arg0+"£":MessageFormat.format(messages.getString("SpielerNichtAngemeldet_EI"), arg0);
	}

	/**
	   * {0}: Radio station installed on planet {1}. [EJ]
	   */
	public static String AuswertungAufklaererSender(boolean symbol, String arg0, String arg1) {
		return symbol ? "£EJ§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungAufklaererSender_EJ"), arg0, arg1);
	}

	/**
	   * More... [EK]
	   */
	public static String ZugeingabeMehr(boolean symbol) {
		return symbol ? "£EK£":messages.getString("ZugeingabeMehr_EK");
	}

	/**
	   * Click on the blinking reload icon. [EN]
	   */
	public static String AuswertungVerfuegbar2(boolean symbol) {
		return symbol ? "£EN£":messages.getString("AuswertungVerfuegbar2_EN");
	}

	/**
	   * The new game [{0}] was created on the server. You may send an invitation e-mail to the other players after closing this message. [EO]
	   */
	public static String ServerGamesSubmitAngelegt2(boolean symbol, String arg0) {
		return symbol ? "£EO§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesSubmitAngelegt2_EO"), arg0);
	}

	/**
	   * This was the last time in this game that you entered your moves. [EP]
	   */
	public static String LetztesJahr(boolean symbol) {
		return symbol ? "£EP£":messages.getString("LetztesJahr_EP");
	}

	/**
	   * You will find the finalized game under "My server-based games" soon. [EQ]
	   */
	public static String LetztesJahr2(boolean symbol) {
		return symbol ? "£EQ£":messages.getString("LetztesJahr2_EQ");
	}

	/**
	   * Finalized game in year {0} [ER]
	   */
	public static String AbgeschlossenesSpiel(boolean symbol, String arg0) {
		return symbol ? "£ER§"+arg0+"£":MessageFormat.format(messages.getString("AbgeschlossenesSpiel_ER"), arg0);
	}

	/**
	   * Capitulate [ET]
	   */
	public static String ZugeingabeKapitulieren(boolean symbol) {
		return symbol ? "£ET£":messages.getString("ZugeingabeKapitulieren_ET");
	}

	/**
	   * Player {0} capitulated. [EU]
	   */
	public static String AuswertungKapitulation(boolean symbol, String arg0) {
		return symbol ? "£EU§"+arg0+"£":MessageFormat.format(messages.getString("AuswertungKapitulation_EU"), arg0);
	}

	/**
	   * An update is available [EV]
	   */
	public static String UpdateAvailable(boolean symbol) {
		return symbol ? "£EV£":messages.getString("UpdateAvailable_EV");
	}

	/**
	   * Update [EX]
	   */
	public static String Update(boolean symbol) {
		return symbol ? "£EX£":messages.getString("Update_EX");
	}

	/**
	   * Application error on the server:\n{0} [EZ]
	   */
	public static String ServerAnwendungsfehler(boolean symbol, String arg0) {
		return symbol ? "£EZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerAnwendungsfehler_EZ"), arg0);
	}

	/**
	   * {1}/{0}/{2} {3}:{4} [FB]
	   */
	public static String ReleaseFormatted(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4) {
		return symbol ? "£FB§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"£":MessageFormat.format(messages.getString("ReleaseFormatted_FB"), arg0, arg1, arg2, arg3, arg4);
	}

	/**
	   * An important update is available [FC]
	   */
	public static String UpdateAvailableImportant(boolean symbol) {
		return symbol ? "£FC£":messages.getString("UpdateAvailableImportant_FC");
	}

	/**
	   * Search for updates [FD]
	   */
	public static String MenuSearchForUpdates(boolean symbol) {
		return symbol ? "£FD£":messages.getString("MenuSearchForUpdates_FD");
	}

	/**
	   * Your STERN build is up to date. [FE]
	   */
	public static String UpdateUpToDate(boolean symbol) {
		return symbol ? "£FE£":messages.getString("UpdateUpToDate_FE");
	}

	/**
	   * The update server cannot be reached. [FF]
	   */
	public static String UpdateServerCannotBeReached(boolean symbol) {
		return symbol ? "£FF£":messages.getString("UpdateServerCannotBeReached_FF");
	}

	/**
	   * Delete user [FG]
	   */
	public static String ServerAdminSpielerLoeschen(boolean symbol) {
		return symbol ? "£FG£":messages.getString("ServerAdminSpielerLoeschen_FG");
	}

	/**
	   * Create new user [FH]
	   */
	public static String ServerAdminSpielerAnlegen(boolean symbol) {
		return symbol ? "£FH£":messages.getString("ServerAdminSpielerAnlegen_FH");
	}

	/**
	   * Renew credentials [FI]
	   */
	public static String ServerAdminAnmeldedatenErneuern(boolean symbol) {
		return symbol ? "£FI£":messages.getString("ServerAdminAnmeldedatenErneuern_FI");
	}

	/**
	   * The user ID [{0}] does not exist on the server. [FJ]
	   */
	public static String ServerErrorAdminUserExistiertNicht(boolean symbol, String arg0) {
		return symbol ? "£FJ§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorAdminUserExistiertNicht_FJ"), arg0);
	}

	/**
	   * Reload user list [FK]
	   */
	public static String ServerAdminUserNeuLaden(boolean symbol) {
		return symbol ? "£FK£":messages.getString("ServerAdminUserNeuLaden_FK");
	}

	/**
	   * The user [{0}] was updated successfully on the server. [FL]
	   */
	public static String ServerAdminUserErfolg(boolean symbol, String arg0) {
		return symbol ? "£FL§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserErfolg_FL"), arg0);
	}

	/**
	   * Do you really want to update user [{0}]? BE CAREFUL: You will also renew the user credentials, so that the user needs to be activated again! [FM]
	   */
	public static String ServerAdminUserRenewCredentials(boolean symbol, String arg0) {
		return symbol ? "£FM§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserRenewCredentials_FM"), arg0);
	}

	/**
	   * Do you really want to update user [{0}]? [FN]
	   */
	public static String ServerAdminUserUpdate(boolean symbol, String arg0) {
		return symbol ? "£FN§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserUpdate_FN"), arg0);
	}

	/**
	   * Logon attempt with inactive user [{0}]. [FO]
	   */
	public static String ServerErrorLogonWithInactiveUser(boolean symbol, String arg0) {
		return symbol ? "£FO§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorLogonWithInactiveUser_FO"), arg0);
	}

	/**
	   * Do you really want to delete user [{0}] from the server? This action cannot be made undone! [FP]
	   */
	public static String ServerAdminUserDelete(boolean symbol, String arg0) {
		return symbol ? "£FP§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserDelete_FP"), arg0);
	}

	/**
	   * The user [{0}] was successfully deleted from the server. [FQ]
	   */
	public static String ServerAdminUserDeleted(boolean symbol, String arg0) {
		return symbol ? "£FQ§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserDeleted_FQ"), arg0);
	}

	/**
	   * The user [{0}] was successfully created on the server. Please send the subsequently created e-mail without changes. [FR]
	   */
	public static String ServerAdminUserCreated(boolean symbol, String arg0) {
		return symbol ? "£FR§"+arg0+"£":MessageFormat.format(messages.getString("ServerAdminUserCreated_FR"), arg0);
	}

	/**
	   * Write e-mail [FS]
	   */
	public static String MenuEmail(boolean symbol) {
		return symbol ? "£FS£":messages.getString("MenuEmail_FS");
	}

	/**
	   * Create e-mail [FT]
	   */
	public static String EmailErzeugen(boolean symbol) {
		return symbol ? "£FT£":messages.getString("EmailErzeugen_FT");
	}

	/**
	   * (E-mail address unknown) [FU]
	   */
	public static String EmailUnbekannt(boolean symbol) {
		return symbol ? "£FU£":messages.getString("EmailUnbekannt_FU");
	}

	/**
	   * Server status [FV]
	   */
	public static String ServerStatus(boolean symbol) {
		return symbol ? "£FV£":messages.getString("ServerStatus_FV");
	}

	/**
	   * Server build [FW]
	   */
	public static String ServerBuild(boolean symbol) {
		return symbol ? "£FW£":messages.getString("ServerBuild_FW");
	}

	/**
	   * Running since [FX]
	   */
	public static String ServerLaeuftSeit(boolean symbol) {
		return symbol ? "£FX£":messages.getString("ServerLaeuftSeit_FX");
	}

	/**
	   * Log size [FY]
	   */
	public static String ServerLogGroesse(boolean symbol) {
		return symbol ? "£FY£":messages.getString("ServerLogGroesse_FY");
	}

	/**
	   * Download log [FZ]
	   */
	public static String ServerLogDownload(boolean symbol) {
		return symbol ? "£FZ£":messages.getString("ServerLogDownload_FZ");
	}

	/**
	   * Log level [GA]
	   */
	public static String ServerLogLevel(boolean symbol) {
		return symbol ? "£GA£":messages.getString("ServerLogLevel_GA");
	}

	/**
	   * Change log level [GB]
	   */
	public static String ServerLogLevelAendern(boolean symbol) {
		return symbol ? "£GB£":messages.getString("ServerLogLevelAendern_GB");
	}

	/**
	   * Refresh status [GC]
	   */
	public static String ServerStatusAktualisieren(boolean symbol) {
		return symbol ? "£GC£":messages.getString("ServerStatusAktualisieren_GC");
	}

	/**
	   * Do you really want to change the log level of the server to "{0}"? [GD]
	   */
	public static String ServerLogLevelAendernAYS(boolean symbol, String arg0) {
		return symbol ? "£GD§"+arg0+"£":MessageFormat.format(messages.getString("ServerLogLevelAendernAYS_GD"), arg0);
	}

	/**
	   * The log level of the server was set successfully. [GE]
	   */
	public static String ServerLogLevelAendernErfolg(boolean symbol) {
		return symbol ? "£GE£":messages.getString("ServerLogLevelAendernErfolg_GE");
	}

	/**
	   * The server log does not contain any data. [GF]
	   */
	public static String ServerLogLeer(boolean symbol) {
		return symbol ? "£GF£":messages.getString("ServerLogLeer_GF");
	}

	/**
	   * {1}/{0}/{2} {3}:{4}:{5} [GG]
	   */
	public static String ReleaseFormatted2(boolean symbol, String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return symbol ? "£GG§"+arg0+"§"+arg1+"§"+arg2+"§"+arg3+"§"+arg4+"§"+arg5+"£":MessageFormat.format(messages.getString("ReleaseFormatted2_GG"), arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/**
	   * Year {0}, day {1} [GK]
	   */
	public static String FlugzeitOutput(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GK§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("FlugzeitOutput_GK"), arg0, arg1);
	}

	/**
	   * Year {0}, end of the year [GL]
	   */
	public static String FlugzeitOutputJahresende(boolean symbol, String arg0) {
		return symbol ? "£GL§"+arg0+"£":MessageFormat.format(messages.getString("FlugzeitOutputJahresende_GL"), arg0);
	}

	/**
	   * Y{0}\nD{1} [GM]
	   */
	public static String FlugzeitOutputShort(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GM§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("FlugzeitOutputShort_GM"), arg0, arg1);
	}

	/**
	   * Y{0}\nEnd [GN]
	   */
	public static String FlugzeitOutputJahresendeShort(boolean symbol, String arg0) {
		return symbol ? "£GN§"+arg0+"£":MessageFormat.format(messages.getString("FlugzeitOutputJahresendeShort_GN"), arg0);
	}

	/**
	   * Service [GP]
	   */
	public static String ZugeingabeMinenraeumerMission(boolean symbol) {
		return symbol ? "£GP£":messages.getString("ZugeingabeMinenraeumerMission_GP");
	}

	/**
	   * Mission [GQ]
	   */
	public static String ZugeingabePatrouilleMission(boolean symbol) {
		return symbol ? "£GQ£":messages.getString("ZugeingabePatrouilleMission_GQ");
	}

	/**
	   * The server is using build {0}. Your STERN build expects a server build of at least {1}. [GV]
	   */
	public static String ServerBuildVeraltet(boolean symbol, String arg0, String arg1) {
		return symbol ? "£GV§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("ServerBuildVeraltet_GV"), arg0, arg1);
	}

	/**
	   * Last activity: {0} [GW]
	   */
	public static String ServerGamesLetzteAktivitaet(boolean symbol, String arg0) {
		return symbol ? "£GW§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesLetzteAktivitaet_GW"), arg0);
	}

	/**
	   * Game host actions [GY]
	   */
	public static String ServerGamesSpielleiteraktionen(boolean symbol) {
		return symbol ? "£GY£":messages.getString("ServerGamesSpielleiteraktionen_GY");
	}

	/**
	   * Delete game [GZ]
	   */
	public static String ServerGamesLoeschen(boolean symbol) {
		return symbol ? "£GZ£":messages.getString("ServerGamesLoeschen_GZ");
	}

	/**
	   * Finalize game [HA]
	   */
	public static String ServerGamesBeenden(boolean symbol) {
		return symbol ? "£HA£":messages.getString("ServerGamesBeenden_HA");
	}

	/**
	   * You are not the host of game {0}. [HC]
	   */
	public static String ServerErrorKeinSpielleiter(boolean symbol, String arg0) {
		return symbol ? "£HC§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorKeinSpielleiter_HC"), arg0);
	}

	/**
	   * Game {0} was deleted successfully. [HD]
	   */
	public static String ServerGamesGameDeleted(boolean symbol, String arg0) {
		return symbol ? "£HD§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesGameDeleted_HD"), arg0);
	}

	/**
	   * Do you really want to delete the game {0} from the server? [HE]
	   */
	public static String ServerGamesLoeschenAys(boolean symbol, String arg0) {
		return symbol ? "£HE§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesLoeschenAys_HE"), arg0);
	}

	/**
	   * The game has already been finalized. [HF]
	   */
	public static String ServerGamesAbgeschlossen(boolean symbol) {
		return symbol ? "£HF£":messages.getString("ServerGamesAbgeschlossen_HF");
	}

	/**
	   * Do you really want to finalize the game {0} immediately? All moves from the playes will get lost. [HG]
	   */
	public static String ServerGamesBeendenAys(boolean symbol, String arg0) {
		return symbol ? "£HG§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesBeendenAys_HG"), arg0);
	}

	/**
	   * Game {0} was finalized successfully. [HH]
	   */
	public static String ServerGamesGameFinalized(boolean symbol, String arg0) {
		return symbol ? "£HH§"+arg0+"£":MessageFormat.format(messages.getString("ServerGamesGameFinalized_HH"), arg0);
	}

	/**
	   * Beginning of the year [HI]
	   */
	public static String AuswertungEreignisJahresbeginn2(boolean symbol) {
		return symbol ? "£HI£":messages.getString("AuswertungEreignisJahresbeginn2_HI");
	}

	/**
	   * End of the year [HJ]
	   */
	public static String AuswertungEreignisJahresende2(boolean symbol) {
		return symbol ? "£HJ£":messages.getString("AuswertungEreignisJahresende2_HJ");
	}

	/**
	   * Day {0} of {1} [HK]
	   */
	public static String AuswertungEreignisTag2(boolean symbol, String arg0, String arg1) {
		return symbol ? "£HK§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("AuswertungEreignisTag2_HK"), arg0, arg1);
	}

	/**
	   * Hide/show spaceships [HL]
	   */
	public static String ZugeingabeObjekteAusblenden(boolean symbol) {
		return symbol ? "£HL£":messages.getString("ZugeingabeObjekteAusblenden_HL");
	}

	/**
	   * on [HM]
	   */
	public static String ZugeingabeObjekteAusblendenAn(boolean symbol) {
		return symbol ? "£HM£":messages.getString("ZugeingabeObjekteAusblendenAn_HM");
	}

	/**
	   * off [HN]
	   */
	public static String ZugeingabeObjekteAusblendenAus(boolean symbol) {
		return symbol ? "£HN£":messages.getString("ZugeingabeObjekteAusblendenAus_HN");
	}

	/**
	   * All off [HO]
	   */
	public static String ZugeingabeObjekteAusblendenAlleAus(boolean symbol) {
		return symbol ? "£HO£":messages.getString("ZugeingabeObjekteAusblendenAlleAus_HO");
	}

	/**
	   * All on [HP]
	   */
	public static String ZugeingabeObjekteAusblendenAlleAn(boolean symbol) {
		return symbol ? "£HP£":messages.getString("ZugeingabeObjekteAusblendenAlleAn_HP");
	}

	/**
	   * The year has been evaluated. Click here to reload the game. [HQ]
	   */
	public static String AuswertungVerfuegbarSymbol(boolean symbol) {
		return symbol ? "£HQ£":messages.getString("AuswertungVerfuegbarSymbol_HQ");
	}

	/**
	   * Diffie\u2013Hellman key exchange failed: {0} [HR]
	   */
	public static String ServerErrorDh(boolean symbol, String arg0) {
		return symbol ? "£HR§"+arg0+"£":MessageFormat.format(messages.getString("ServerErrorDh_HR"), arg0);
	}

	/**
	   * New session {0} created for user {1}. [HS]
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
	   * Mute notification sound [HU]
	   */
	public static String BenachrichtigungStumm(boolean symbol) {
		return symbol ? "£HU£":messages.getString("BenachrichtigungStumm_HU");
	}

	/**
	   * Read user {0}... [HV]
	   */
	public static String ServerUserLesen(boolean symbol, String arg0) {
		return symbol ? "£HV§"+arg0+"£":MessageFormat.format(messages.getString("ServerUserLesen_HV"), arg0);
	}

	/**
	   * Read configuration... [HW]
	   */
	public static String ServerConfigLaden(boolean symbol) {
		return symbol ? "£HW£":messages.getString("ServerConfigLaden_HW");
	}

	/**
	   * Read game {0}... [HY]
	   */
	public static String ServerSpielLesen(boolean symbol, String arg0) {
		return symbol ? "£HY§"+arg0+"£":MessageFormat.format(messages.getString("ServerSpielLesen_HY"), arg0);
	}

	/**
	   * Create folder {0}... [HZ]
	   */
	public static String ServerOrdnerErzeugen(boolean symbol, String arg0) {
		return symbol ? "£HZ§"+arg0+"£":MessageFormat.format(messages.getString("ServerOrdnerErzeugen_HZ"), arg0);
	}

	/**
	   * Create admin user... [IA]
	   */
	public static String ServerAdminErzeugen(boolean symbol) {
		return symbol ? "£IA£":messages.getString("ServerAdminErzeugen_IA");
	}

	/**
	   * >>> Players are entering their moves. Input is disabled. <<< [IB]
	   */
	public static String ZugeingabeClientEingabeGesperrt(boolean symbol) {
		return symbol ? "£IB£":messages.getString("ZugeingabeClientEingabeGesperrt_IB");
	}

	/**
	   * STERN Displays passive while moves are being entered [IC]
	   */
	public static String ServerSettingsJDialogInaktiv(boolean symbol) {
		return symbol ? "£IC£":messages.getString("ServerSettingsJDialogInaktiv_IC");
	}

	/**
	   * Get [ID]
	   */
	public static String ClientSettingsJDialogIpErmitteln(boolean symbol) {
		return symbol ? "£ID£":messages.getString("ClientSettingsJDialogIpErmitteln_ID");
	}

	/**
	   * My IP address [IE]
	   */
	public static String ClientSettingsJDialogMeineIp(boolean symbol) {
		return symbol ? "£IE£":messages.getString("ClientSettingsJDialogMeineIp_IE");
	}

	/**
	   * Do you want to save the changed server credentials? [IH]
	   */
	public static String ServerUrlUebernehmen(boolean symbol) {
		return symbol ? "£IH£":messages.getString("ServerUrlUebernehmen_IH");
	}

	/**
	   * Save server credentials? [II]
	   */
	public static String ServerZugangsdatenAendern(boolean symbol) {
		return symbol ? "£II£":messages.getString("ServerZugangsdatenAendern_II");
	}

	/**
	   * The browser cannot be opened:\n{0} [IJ]
	   */
	public static String BrowserNichtGeoeffnet(boolean symbol, String arg0) {
		return symbol ? "£IJ§"+arg0+"£":MessageFormat.format(messages.getString("BrowserNichtGeoeffnet_IJ"), arg0);
	}

	/**
	   * E-mail client cannot be opened:\n{0} [IK]
	   */
	public static String EmailNichtGeoeffnet(boolean symbol, String arg0) {
		return symbol ? "£IK§"+arg0+"£":MessageFormat.format(messages.getString("EmailNichtGeoeffnet_IK"), arg0);
	}

	/**
	   * STERN build [IL]
	   */
	public static String ClientBuild(boolean symbol) {
		return symbol ? "£IL£":messages.getString("ClientBuild_IL");
	}

	/**
	   * Open output window [IN]
	   */
	public static String MenuAusgabeFenster(boolean symbol) {
		return symbol ? "£IN£":messages.getString("MenuAusgabeFenster_IN");
	}

	/**
	   * The game name [{0}] ist invalid.\nThe length of a game name must be between {1} and {2} characters.\nIt must only contain the characters a-z, A-Z, and 0-9. [IO]
	   */
	public static String ServerGamesSubmitSpielname(boolean symbol, String arg0, String arg1, String arg2) {
		return symbol ? "£IO§"+arg0+"§"+arg1+"§"+arg2+"£":MessageFormat.format(messages.getString("ServerGamesSubmitSpielname_IO"), arg0, arg1, arg2);
	}

	/**
	   * A game with the same name already exists! [IP]
	   */
	public static String ServerErrorSpielExistiert(boolean symbol) {
		return symbol ? "£IP£":messages.getString("ServerErrorSpielExistiert_IP");
	}

	/**
	   * Radio stations [IQ]
	   */
	public static String SpielinformationenSender(boolean symbol) {
		return symbol ? "£IQ£":messages.getString("SpielinformationenSender_IQ");
	}

	/**
	   * Activate Web server [IR]
	   */
	public static String MenuWebserverAktivieren(boolean symbol) {
		return symbol ? "£IR£":messages.getString("MenuWebserverAktivieren_IR");
	}

	/**
	   * Deactivate Web server [IS]
	   */
	public static String MenuWebserverDeaktivieren(boolean symbol) {
		return symbol ? "£IS£":messages.getString("MenuWebserverDeaktivieren_IS");
	}

	/**
	   * Web server active [IT]
	   */
	public static String WebserverAktiviert(boolean symbol) {
		return symbol ? "£IT£":messages.getString("WebserverAktiviert_IT");
	}

	/**
	   * Web server deactivated [IU]
	   */
	public static String WebserverDeaktiviert(boolean symbol) {
		return symbol ? "£IU£":messages.getString("WebserverDeaktiviert_IU");
	}

	/**
	   * Web server [IV]
	   */
	public static String Webserver(boolean symbol) {
		return symbol ? "£IV£":messages.getString("Webserver_IV");
	}

	/**
	   * Logon data incomplete. [IW]
	   */
	public static String LogOnDataIncomplete(boolean symbol) {
		return symbol ? "£IW£":messages.getString("LogOnDataIncomplete_IW");
	}

	/**
	   * The server has closed the connection. Check your logon credentials or try later. [IX]
	   */
	public static String ConnectionClosed(boolean symbol) {
		return symbol ? "£IX£":messages.getString("ConnectionClosed_IX");
	}

	/**
	   * No connection to server:\n{0} [IY]
	   */
	public static String NoConnectionToServer(boolean symbol, String arg0) {
		return symbol ? "£IY§"+arg0+"£":MessageFormat.format(messages.getString("NoConnectionToServer_IY"), arg0);
	}

	/**
	   * Address separator [IZ]
	   */
	public static String AddressSeparator(boolean symbol) {
		return symbol ? "£IZ£":messages.getString("AddressSeparator_IZ");
	}

	/**
	   * Invalid security code. [JA]
	   */
	public static String ClientCodeInvalid(boolean symbol) {
		return symbol ? "£JA£":messages.getString("ClientCodeInvalid_JA");
	}

	/**
	   * You must not miss the owner of the planet, current alliance members, and yourself! [JB]
	   */
	public static String AllianceOwnerNotIncluded(boolean symbol) {
		return symbol ? "£JB£":messages.getString("AllianceOwnerNotIncluded_JB");
	}

	/**
	   * User activated [JC]
	   */
	public static String ServerAdminUserActive(boolean symbol) {
		return symbol ? "£JC£":messages.getString("ServerAdminUserActive_JC");
	}

	/**
	   * Planets produce fighters and $. [JD]
	   */
	public static String EvaluationPlanetsProducing(boolean symbol) {
		return symbol ? "£JD£":messages.getString("EvaluationPlanetsProducing_JD");
	}

	/**
	   * Home planet [JE]
	   */
	public static String HomePlanet(boolean symbol) {
		return symbol ? "£JE£":messages.getString("HomePlanet_JE");
	}

	/**
	   * {0} has conquered the home planet of {1}! [JG]
	   */
	public static String EvaluationHomePlanetConquered(boolean symbol, String arg0, String arg1) {
		return symbol ? "£JG§"+arg0+"§"+arg1+"£":MessageFormat.format(messages.getString("EvaluationHomePlanetConquered_JG"), arg0, arg1);
	}

	/**
	   * Planet {0} [JH]
	   */
	public static String EnterMovesPlanet(boolean symbol, String arg0) {
		return symbol ? "£JH§"+arg0+"£":MessageFormat.format(messages.getString("EnterMovesPlanet_JH"), arg0);
	}

	/**
	   * Players [JI]
	   */
	public static String Players(boolean symbol) {
		return symbol ? "£JI£":messages.getString("Players_JI");
	}

	/**
	   * Fighters [JJ]
	   */
	public static String PlanetListTitleFighters(boolean symbol) {
		return symbol ? "£JJ£":messages.getString("PlanetListTitleFighters_JJ");
	}

	/**
	   * Defence shield fighters [JK]
	   */
	public static String PlanetListTitleDefenceShiels(boolean symbol) {
		return symbol ? "£JK£":messages.getString("PlanetListTitleDefenceShiels_JK");
	}

	/**
	   * Evaluation [JL]
	   */
	public static String Evaluation(boolean symbol) {
		return symbol ? "£JL£":messages.getString("Evaluation_JL");
	}
}