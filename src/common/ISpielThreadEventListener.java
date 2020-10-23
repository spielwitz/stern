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

import java.util.EventListener;

public interface ISpielThreadEventListener extends EventListener
{
	void update(ScreenUpdateEvent event);
	void speichern(Spiel spiel, boolean autoSave);
	boolean openPdf(byte[] pdfBytes, String clientId);
	void addToHighscore(Archiv spielstand, Spieler[] spieler);
	void pause(int milliseconds);
	void checkMenuEnabled();
	boolean launchEmail(String recipient, String subject, String body, EmailTransportBase obj);
	PostMovesResult postMovesToServer(String gameId, String spielerName, SpielzuegeEmailTransport set);
	SpielzuegeEmailTransport importSpielzuegeAusEmail();
	void triggerGameInfoUpdate();
}
