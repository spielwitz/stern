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

package stern;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.OutputStream;
import java.net.Socket;

import common.Constants;
import common.ReleaseGetter;
import common.SternResources;
import commonServer.ClientUserCredentials;
import commonServer.RequestMessage;
import commonServer.ResponseMessage;
import commonServer.RsaCrypt;
import commonServer.ServerConstants;
import commonServer.ServerUtils;

class ClientSocketManager
{
	static ResponseMessage sendAndReceive (
			ClientUserCredentials user,
			RequestMessage msg)
	{
		if (user == null
				||
			user.serverPublicKeyObject == null 
				||
			user.userPrivateKeyObject == null
				||
			user.port == 0 
				||
			user.url == null
				||
			user.userId == null)
		{
			ResponseMessage msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Unvollständige Anmeldedaten!";
	    	
	    	return msgResponse;
		}
		
		byte[] payloadBytes = null;
		Socket kkSocket = null;
		OutputStream out = null;
		
		// Build in Request-Nachricht setzen
		msg.build = ReleaseGetter.getRelease();
		
	    try {
			kkSocket = new Socket(user.url, user.port);
			out = kkSocket.getOutputStream();
			DataInputStream in = new DataInputStream(kkSocket.getInputStream());
			
			// Zuerst User ID an den Server schicken.
			byte[] userIdBytes = RsaCrypt.encrypt(user.userId, user.serverPublicKeyObject);
			byte[] userIdBytesLength = RsaCrypt.encryptIntValue(userIdBytes.length, user.serverPublicKeyObject);

			out.write(userIdBytesLength);
			out.write(userIdBytes);

			// Nur wenn es nicht um das Aktivieren eines Users warten: Nun auf den Token warten
			if (!user.userId.equals(ServerConstants.ACTIVATION_USER))
			{
				byte lengthTokenBytes[] = new byte[4];
				in.readFully(lengthTokenBytes);
				
				int lengthToken = ServerUtils.convertByteArrayToInt(lengthTokenBytes);			
				byte tokenBytes[] = new byte[lengthToken];
				in.readFully(tokenBytes);
				
				// Das Token mit dem persönlichen privaten Schlüssel des Users entschlüsseln
				msg.token = RsaCrypt.decrypt(tokenBytes, user.userPrivateKeyObject);
			}
			
			// Jetzt erst die eigentliche Request-Nachricht an den Server schicken.
			// Die Request-Nachricht enthält das vereinbarte Token.
			byte[] payload = RsaCrypt.encrypt(msg.toJson(), user.serverPublicKeyObject);
			
			int length = (payload.length); 
			byte[] lengthBytes = RsaCrypt.encryptIntValue(length, user.serverPublicKeyObject);
			
			out.write(lengthBytes);
			out.write(payload);
			
			// Warte auf die Response-Nachricht
			lengthBytes = new byte[RsaCrypt.BYTE_ARRAY_LENGTH]; // Laenge des nachfolgenden Payloads
		    in.readFully(lengthBytes);
		    
		    int payloadLength = RsaCrypt.decryptIntValue(lengthBytes, user.userPrivateKeyObject);
		    payloadBytes = new byte[payloadLength]; // Die eigentliche Nachricht
		    in.readFully(payloadBytes);
		  
		}
	    catch (EOFException e)
		{
	    	ResponseMessage msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Der Server hat die Verbindung beendet. Prüfen Sie Ihre Anmeldedaten oder probieren Sie es nochmal.";
	    	
	    	return msgResponse;
		}
	    catch (Exception e)
		{
	    	ResponseMessage msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Keine Verbindung mit dem Server:\n" + e.getMessage();
	    	
	    	return msgResponse;
		}
	    
	    try
	    {
		    kkSocket.close();
		    
		    // Die Response-Nachricht mit dem persönlichen privaten Schlüssel
		    // des Users entschlüsseln.
		    String json = RsaCrypt.decrypt(payloadBytes, user.userPrivateKeyObject);
		    ResponseMessage msgResponse = ResponseMessage.fromJson(json);
		    
		    if (!ReleaseGetter.getRelease().equals(Constants.NO_BUILD_INFO)) // Wenn Stern aus Eclipse heraus gestartet wird
			{
				 if (!(msgResponse.build != null && msgResponse.build.equals(Constants.NO_BUILD_INFO)))
				 {
					 if (msgResponse.build == null || msgResponse.build.compareTo(Constants.MIN_BUILD) < 0)
					 {
						 msgResponse.error = true;
						 msgResponse.errorMsg = SternResources.ServerBuildVeraltet(
									false,
									msgResponse.build == null ? "[null]" : ReleaseGetter.format(msgResponse.build),
									ReleaseGetter.format(Constants.MIN_BUILD)
									);
					 }
				 }
			}
		    
		    return msgResponse;
	    }
	    catch (Exception x)
	    {
	    	ResponseMessage msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Fehler beim Entschlüsseln der Serverantwort: " + x.getMessage();
	    	
	    	return msgResponse;
	    }
	}
}
