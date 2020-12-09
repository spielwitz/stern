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
import commonServer.Ciphers;
import commonServer.ClientUserCredentials;
import commonServer.CryptoLib;
import commonServer.RequestMessage;
import commonServer.RequestMessageUserId;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageUserId;
import commonServer.ServerConstants;

class ClientSocketManager
{
	private static ClientSocketManager obj;
	private static Object lockObj = new Object();
	
	private Ciphers aesCiphers;
	
	private static boolean isBusy;
	
	static {
		obj = new ClientSocketManager();
	}
	
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
		
		setBusy(true);
		
		ResponseMessage msgResponse  = null;
		Socket kkSocket = null;
		OutputStream out = null;
		
		// Build in Request-Nachricht setzen
		msg.build = ReleaseGetter.getRelease();
		
	    try {
			kkSocket = new Socket(user.url, user.port);
			out = kkSocket.getOutputStream();
			DataInputStream in = new DataInputStream(kkSocket.getInputStream());
			
			// Zuerst User ID an den Server schicken.
			RequestMessageUserId msgRequest = new RequestMessageUserId();
			
			msgRequest.userId = user.userId;
			
			Ciphers ciphers = obj.getAesCiphers();
			String sessionId = null;
			
			if (ciphers == null || user.userId.equals(ServerConstants.ACTIVATION_USER))
			{
				sessionId = CryptoLib.NULL_UUID;
			}
			else
			{
				sessionId = ciphers.sessionId;
			}
			
			msgRequest.sessionId = sessionId;
			
			CryptoLib.sendStringRsaEncrypted(
					out, 
					msgRequest.toJson(), 
					user.serverPublicKeyObject);
			
			String token = null;
			
			if (user.userId.equals(ServerConstants.ACTIVATION_USER))
			{
				token = CryptoLib.NULL_UUID;
			}
			else
			{
				ResponseMessageUserId respMsg = (ResponseMessageUserId)
						ResponseMessageUserId.fromJson(
							CryptoLib.receiveStringRsaEncrypted(in, user.userPrivateKeyObject),
							ResponseMessageUserId.class);
				
				sessionId = respMsg.sessionValid ? sessionId : CryptoLib.NULL_UUID;
				token = respMsg.token;
			}
			
			if (sessionId.equals(CryptoLib.NULL_UUID))
			{
				// Ciphers sind nicht mehr gueltig. Neue aushandeln.
				ciphers = CryptoLib.diffieHellmanKeyAgreementClient(in, out);
			}
			
			// Token mit der eigentlichen Nachricht schicken!
			msg.token = token;
			
			// Jetzt erst die eigentliche Request-Nachricht an den Server schicken.
			// Die Request-Nachricht enthält das vereinbarte Token.
			CryptoLib.sendStringAesEncrypted(
					out, 
					msg.toJson(), 
					ciphers.cipherEncrypt);
			
			// Warte auf die Response-Nachricht
			msgResponse = 
					ResponseMessage.fromJson(
							CryptoLib.receiveStringAesEncrypted(in, ciphers.cipherDecrypt));
			
			kkSocket.close();
			
			if (user.userId.equals(ServerConstants.ACTIVATION_USER))
			{
				obj.setAesCiphers(null);
			}
			else
			{
				obj.setAesCiphers(ciphers);
			}
		}
	    catch (EOFException e)
		{
	    	msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Der Server hat die Verbindung beendet. Prüfen Sie Ihre Anmeldedaten oder probieren Sie es nochmal.";
	    	
	    	setBusy(false);
	    	
	    	return msgResponse;
		}
	    catch (Exception e)
		{
	    	msgResponse = new ResponseMessage();
	    	
	    	msgResponse.error = true;
	    	msgResponse.errorMsg = "Keine Verbindung mit dem Server:\n" + e.getMessage();
	    	
	    	setBusy(false);
	    	
	    	return msgResponse;
		}
	    
	    if (!ReleaseGetter.getRelease().equals(Constants.BUILD_NO_INFO)) // Wenn Stern aus Eclipse heraus gestartet wird
		{
			 if (!(msgResponse.build != null && msgResponse.build.equals(Constants.BUILD_NO_INFO)))
			 {
				 if (msgResponse.build == null || msgResponse.build.compareTo(Constants.BUILD_COMPATIBLE) < 0)
				 {
					 msgResponse.error = true;
					 msgResponse.errorMsg = SternResources.ServerBuildVeraltet(
								false,
								msgResponse.build == null ? "[null]" : msgResponse.build,
								Constants.BUILD_COMPATIBLE
								);
				 }
			 }
		}
	    
	    setBusy(false);
	    
	    return msgResponse;
    }

	private Ciphers getAesCiphers()
	{
		synchronized(lockObj)
		{
			return aesCiphers;
		}
	}

	private void setAesCiphers(Ciphers aesCiphers)
	{
		synchronized(lockObj)
		{
			this.aesCiphers = aesCiphers;
		}
	}
	
	static boolean isBusy()
	{
		synchronized(lockObj)
		{
			return isBusy;
		}
	}
	
	private static void setBusy(boolean busy)
	{
		synchronized(lockObj)
		{
			isBusy = busy;
		}
	}
}
