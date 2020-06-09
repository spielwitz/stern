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

package sternServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.UUID;
import java.util.regex.Pattern;

import common.Constants;
import common.PostMovesResult;
import common.ReleaseGetter;
import common.Spiel;
import common.SpielInfo;
import common.Spieler;
import common.SternResources;
import common.Utils;
import commonServer.ClientUserCredentials;
import commonServer.LogEventType;
import commonServer.RequestMessage;
import commonServer.RequestMessageActivateUser;
import commonServer.RequestMessageGetEvaluations;
import commonServer.RequestMessageChangeUser;
import commonServer.RequestMessageGameHostDeleteGame;
import commonServer.RequestMessageGameHostFinalizeGame;
import commonServer.RequestMessagePostMoves;
import commonServer.RequestMessageSetLogLevel;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonServer.ResponseMessageGetEvaluations;
import commonServer.ResponseMessageGetLog;
import commonServer.ResponseMessageGetServerStatus;
import commonServer.ResponseMessageGetUsers;
import commonServer.ResponseMessageChangeUser;
import commonServer.RsaCrypt;
import commonServer.ServerConstants;
import commonServer.ServerUtils;

public class SternServer // NO_UCD (unused code)
{
	private static ServerConfiguration serverConfig;
	
	private final static String FOLDER_NAME_DATA = "ServerData";
	private final static String FOLDER_NAME_LOG = "Logs";
	private final static String FOLDER_NAME_USER = "Users";
	private final static String FOLDER_NAME_GAME = "Games";
	private final static String homeDir;
	
	private Path logFilePath;
	
	private Hashtable<String,UserServer> users;
	private Hashtable<String,SpielInfo> games;
	private boolean shutdown = false;
	private boolean adminCreated;
	private long serverStartDate;
	
	private Hashtable<String, Object> lockObjects;
	
	static
	{
		homeDir = ServerUtils.getHomeFolder();
	}
	
	public static void main(String[] args)
	{
		SternServer vs = new SternServer();
		
		try {
			vs.run();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private void run()
	{
		// Init server
		this.lockObjects = new Hashtable<String, Object>();
		
		RsaCrypt.init();
		
		// Wird der Server zum ersten Mal gestartet?
		File dir = new File(homeDir, FOLDER_NAME_DATA);
		boolean setupServer = !dir.exists();
		
		String serverUrl = null;
		int port = 0;
		String adminEmail = null;
		String locale = null;
		
		if (setupServer)
		{
			// Ein paar Fragen beantworten
			while (true)
			{
				System.out.println("Deutsch [de] / English [en]: ");
				
				String language = this.getInput();
				
				if (language.toLowerCase().equals("de"))
				{
					locale = "de-DE";
					break;
				}
				else if (language.toLowerCase().equals("en"))
				{
					locale = "en-US";
					break;
				}
			}
			
			SternResources.setLocale(locale);
			
			System.out.println("\n"+SternResources.ServerWillkommen(false)+"\n");

			System.out.print(SternResources.ServerAdminUrl(false)+ " ("+SternResources.ServerVoreingestellt(false)+": "+ServerConstants.SERVER_HOSTNAME+"): ");
		    serverUrl = this.getInput();
		    
		    serverUrl = serverUrl.length() == 0 ?
		    				ServerConstants.SERVER_HOSTNAME :
		    				serverUrl;
			
		    System.out.print(SternResources.ServerAdminPort(false ) + " ("+SternResources.ServerVoreingestellt(false)+": "+ServerConstants.SERVER_PORT+"): ");
		    String serverPort = this.getInput();
		    
		    port = serverPort.length() == 0 ? 
		    		ServerConstants.SERVER_PORT :
		    		Integer.parseInt(serverPort);
		    
		    System.out.print(SternResources.ServerEmailAdmin(false)+": ");
		    adminEmail = this.getInput();
		    
		    System.out.println("\n"+SternResources.ServerAdminUrl(false)+": " + serverUrl);
		    System.out.println(SternResources.ServerAdminPort(false )+": " + port);
		    System.out.println(SternResources.ServerEmailAdmin(false)+": " + adminEmail);
		    
		    System.out.print("\n"+SternResources.ServerInitConfirm(false)+": ");
		    String ok = this.getInput();
		    		    
		    if (!ok.equals("1"))
		    {
		    	 System.out.print(SternResources.ServerInitAbort(false));
		    	 return;
		    }
		}

		this.initCreateDataFolders();
		this.initServerConfig(serverUrl, port, adminEmail, locale);
		this.initReadAllUsers();
		this.initCreateAdmin(serverUrl, port);
		this.initReadAllGames();
		
		this.logMessage(
				LogEventId.M1,
				LogEventType.Verbose,
				SternResources.ServerStarting(false));
		
		ServerSocket serverSocket = null;

		try
		{
		    serverSocket = new ServerSocket(serverConfig.port);
		    
		    System.out.println(
		    		SternResources.ServerStarted(false, Integer.toString(serverConfig.port)));
		    
		    this.logMessage(
					LogEventId.M2,
					LogEventType.Information,
					SternResources.ServerStarted(false, Integer.toString(serverConfig.port)));
		}
		catch (Exception x)
		{
			System.out.println(
					SternResources.ServerNotStarted(false, Integer.toString(serverConfig.port)));
			
			this.logMessage(
					LogEventId.M3,
					LogEventType.Error,
					SternResources.ServerNotStarted(false, Integer.toString(serverConfig.port)));
			return;
		}
		
		this.serverStartDate = System.currentTimeMillis();
			
		while (true)
		{
			try
			{
				this.logMessage(
						LogEventId.M4,
						LogEventType.Verbose,
						SternResources.ServerWaiting(false));
				
			    Socket clientSocket = serverSocket.accept();
			    clientSocket.setSoTimeout(ServerConstants.SOCKET_TIMEOUT);
			    
			    if (this.shutdown)
			    {
			    	clientSocket.close();
			    	break;
			    }
			    
			    ServerThread st = new ServerThread(clientSocket);
			    
			    logMessage(
						LogEventId.M5,
						LogEventType.Information,
						SternResources.ServerIncomingConnection(
								false, 
								clientSocket.getInetAddress().toString(),
								Long.toString(st.getId())));

			    st.start();			    
			}
			catch (Exception x)
			{
				x.printStackTrace();
				break;
			}
		}
		
		this.logMessage(
				LogEventId.M18,
				LogEventType.Information,
				SternResources.ServerAdminShutdownDone(false));
		
		try {
			System.out.println(
					SternResources.ServerISocketClose(false));
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initCreateDataFolders()
	{
		File dir = new File(homeDir, FOLDER_NAME_DATA);
		if (!dir.exists())
			dir.mkdir();
		
		File dirLog = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_LOG).toFile();
		if (!dirLog.exists())
			dirLog.mkdir();
		
		File dirUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USER).toFile();
		if (!dirUser.exists())
			dirUser.mkdir();
		
		File dirGame = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAME).toFile();
		if (!dirGame.exists())
			dirGame.mkdir();
	}
	
	private void initCreateAdmin(String url, int port)
	{
		if (!this.adminCreated)
		{
			KeyPair userKeyPair = RsaCrypt.getNewKeyPair();
			
			String user = ServerConstants.ADMIN_USER;
			
			UserServer adminUser = new UserServer(user);
			
			adminUser.email = "";
			adminUser.name = "";
			adminUser.userId = user;
			adminUser.active = true;
			
			adminUser.userPublicKey = 
					RsaCrypt.encodePublicKeyToBase64(userKeyPair.getPublic());
			
			this.userUpdate(adminUser);
			
			// Die Anmeldedaten für den Client in eine Datei schreiben.
			ClientUserCredentials aca = new ClientUserCredentials();
			aca.userId = user;
			aca.userPrivateKey = RsaCrypt.encodePrivateKeyToBase64(userKeyPair.getPrivate());
			
			aca.serverPublicKey = serverConfig.serverPublicKey;
			aca.url = url;
			aca.port = port;
			
			File fileClientInfo = Paths.get(
					homeDir, 
					FOLDER_NAME_DATA, 
					ServerUtils.getCredentialFileName(ServerConstants.ADMIN_USER, url, port)).toFile();
			
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileClientInfo.getAbsoluteFile())))
			{
				String text = aca.toJson();
				bw.write(text);
				
				System.out.println(
						SternResources.ServerIDateiAngelegt(
								false, 
								fileClientInfo.getAbsolutePath().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.adminCreated = true;
		}
	}
	
	private void initServerConfig(String url, int port, String adminEmail, String locale)
	{
		File fileServerCredentials = Paths.get(homeDir, FOLDER_NAME_DATA, ServerConstants.SERVER_CONFIG_FILE).toFile();
		
		if (fileServerCredentials.exists())
		{
			// lesen
			try (BufferedReader br = new BufferedReader(new FileReader(fileServerCredentials.getAbsoluteFile())))
			{
				String json = br.readLine();
				serverConfig = ServerConfiguration.fromJson(json); 
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (serverConfig.locale != null)
				SternResources.setLocale(serverConfig.locale);
		}
		else
		{
			// neu anlegen
			KeyPair keyPairRequest = RsaCrypt.getNewKeyPair();
			
			serverConfig = new ServerConfiguration();
			
			serverConfig.locale = locale;
			
			serverConfig.serverPrivateKeyObject = keyPairRequest.getPrivate();
			
			serverConfig.serverPrivateKey = RsaCrypt.encodePrivateKeyToBase64(serverConfig.serverPrivateKeyObject);
			serverConfig.serverPublicKey = RsaCrypt.encodePublicKeyToBase64(keyPairRequest.getPublic());
			
			serverConfig.url = url;
			serverConfig.port = port;
			serverConfig.adminEmail = adminEmail;
			
			this.updateServerConfig(fileServerCredentials, true);
		}
	}
	
	private void initReadAllUsers()
	{
		// Alle User in den Puffer lesen
		this.users = new Hashtable<String, UserServer>();
		this.adminCreated = false;
		
		File dirUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USER).toFile();
		for (String filename: dirUser.list())
			if (filename.equals(ServerConstants.ADMIN_USER) || Pattern.matches(Constants.SPIELER_REGEX_PATTERN, filename))
				this.userRead(filename);
	}
	
	private void initReadAllGames()
	{
		File dirGame = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAME).toFile();
		this.games = new Hashtable<String, SpielInfo>();
		
		for (String filename: dirGame.list())
		{
			Spiel spiel = this.gameRead(filename);
			
			if (spiel == null)
				continue;
			
			this.games.put(spiel.getName(), spiel.getSpielInfo());
			
			// Durch die Spielerliste gehen und den Spielern das Spiel zuordnen
			for (Spieler spieler: spiel.getSpieler())
			{
				if (this.users.containsKey(spieler.getName()))
					this.users.get(spieler.getName()).games.add(spiel.getName());
			}
		}		
	}
	
	private void logMessage(int eventId, LogEventType severity, String msg)
	{
		this.logMessage(eventId, 0, severity, msg);
	}
	private void logMessage(int eventId, long threadId, LogEventType severity, String msg)
	{
		if (serverConfig.logLevel == LogEventType.Information &&
				severity == LogEventType.Verbose)
			return;
		else if (serverConfig.logLevel == LogEventType.Warning &&
			(severity == LogEventType.Verbose ||
			 severity == LogEventType.Information))
			return;
		else if (serverConfig.logLevel == LogEventType.Error &&
				(severity != LogEventType.Error &&
				 severity != LogEventType.Critical))
				return;
		else if (serverConfig.logLevel == LogEventType.Critical &&
				 severity != LogEventType.Critical)
				return;
		
		StringBuilder sb = new StringBuilder();
		
		if (this.logFilePath == null)
		{
			sb.append(SternResources.ServerILogDatum(false) + "\t");
			sb.append(SternResources.ServerILogEventId(false) + "\t");
			sb.append(SternResources.ServerILogThreadId(false) + "\t");
			sb.append(SternResources.ServerILogLevel(false) + "\t");
			sb.append(SternResources.ServerILogMeldung(false) + "\n\n");
		}
		
		sb.append(Utils.currentTimeToLocalizedString());
		sb.append("\t");
		
		sb.append(eventId);
		sb.append("\t");
		
		if (threadId > 0)
			sb.append(threadId);
		sb.append("\t");
		
		sb.append(severity.toString());
		sb.append("\t");
		
		sb.append(msg);
		
		sb.append("\n");
		
		if (this.logFilePath == null)
		{
			this.logFilePath = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_LOG, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".csv");
			
			try {
			    Files.write(this.logFilePath, sb.toString().getBytes("UTF-8"), StandardOpenOption.CREATE);
			}catch (IOException e) {
			}
		}
		else
		{	
			synchronized(this.logFilePath)
			{
				try {
				    Files.write(this.logFilePath, sb.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
				}catch (IOException e) {
				}
			}
		}
	}

	private class ServerThread extends Thread
	{
		private Socket socket;
		
		public ServerThread(Socket socket)
		{
			this.socket = socket;
		}
		public void run()
		{
			byte[] payloadBytes = null;
			String userId = null;
			OutputStream out = null;
			DataInputStream in = null;
			
			try
			{
			    in = new DataInputStream(this.socket.getInputStream());
			    out = this.socket.getOutputStream();
			    
			    byte[] userBytesLength = new byte[4];
			    in.readFully(userBytesLength);
			    
			    int userLength = ServerUtils.convertByteArrayToInt(userBytesLength);
			    
			    byte[] userIdBytes = new byte[userLength]; // User-ID, mit Server-Credentials verschluesselt
			    in.readFully(userIdBytes);
			    
			    userId = RsaCrypt.decrypt(userIdBytes, serverConfig.serverPrivateKeyObject);
			    
			    if (userId.length() < Constants.SPIELER_NAME_MIN_LAENGE || 
				    	userId.length() > Constants.SPIELER_NAME_MAX_LAENGE)
				{
				    	logMessage(
								LogEventId.M21,
								this.getId(),
								LogEventType.Error,
								SternResources.ServerErrorUngueltigeLaengeBenutzer(
										false,
										Integer.toString(userLength)));
				    	
				    	this.closeSocket();
					    return;
			    }
			    
			    logMessage(
						LogEventId.M6,
						this.getId(),
						LogEventType.Verbose,
						SternResources.ServerBenutzer(false, userId));
			    
			    // Pruefen, ob es den user gibt. Wenn nicht, dann sofort aussteigen.
			    if (!userId.equals(ServerConstants.ACTIVATION_USER) && !users.containsKey(userId))
				{
				    	logMessage(
								LogEventId.M16,
								this.getId(),
								LogEventType.Error,
								SternResources.ServerErrorUngueltigerBenutzer(false, userId));
				    	
				    	this.closeSocket();
					    return;
			    }
			}
			catch (Exception x)
			{
				// Im Fehlerfall den Socket schliessen
				logMessage(
						LogEventId.M9,
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorRequestReceive(false, x.getMessage()));
				
				this.closeSocket();
				return;
			}
			
			UserServer user = userId.equals(ServerConstants.ACTIVATION_USER) ?
					null :
					userRead(userId);
			
			if (user != null && !user.active)
			{
				// Der Benutzer ist inaktiv
				logMessage(
						LogEventId.M24,
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorLogonWithInactiveUser(false, user.userId));
				
				this.closeSocket();
				return;
			}
			
			// ######
			// 1. Token (Guid) vereinbaren
			String token = userId.equals(ServerConstants.ACTIVATION_USER) ?
					null :
					UUID.randomUUID().toString();
			
			if (token != null)
			{
				// 2. Token verschluesselt mit dem oeffentlichen Schluessel des Users an Client schicken
				byte[] byteToken = RsaCrypt.encrypt(token, user.userPublicKeyObject);
			    
			    int lengthToken = (byteToken.length); 
				byte[] byteLengthToken = ServerUtils.convertIntToByteArray(lengthToken);
	
				try
				{
					out.write(byteLengthToken);
					out.write(byteToken);
				}
				catch (Exception x)
				{
					// Im Fehlerfall den Socket schliessen
					logMessage(
							LogEventId.M11,
							this.getId(),
							LogEventType.Error,
							SternResources.ServerErrorSendResponse(false, x.getMessage()));
					
					this.closeSocket();
					return;
				}
			}
			
			// 3. Der Client schickt die Laenge deseigentlichen Payloads und dann den Payload
			// Das vereinbarte Token steckt im Payload			
			try
			{
			    byte[] lengthBytes = new byte[4]; // Laenge des nachfolgenden Payloads
			    in.readFully(lengthBytes);
			    
			    int payloadLength = ServerUtils.convertByteArrayToInt(lengthBytes);
			    payloadBytes = new byte[payloadLength]; // Die eigentliche Nachricht
			    in.readFully(payloadBytes);
			}
			catch (Exception x)
			{
				// Im Fehlerfall den Socket schliessen
				logMessage(
						LogEventId.M9, // #### Neue Fehlernummer!
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorRequestReceive(false, x.getMessage()));
				
				this.closeSocket();
				return;
			}

			// Nachricht entschluesseln
			RequestMessage msg = null;
			
			try
			{
			    String json = RsaCrypt.decrypt(payloadBytes, serverConfig.serverPrivateKeyObject);
			    msg = RequestMessage.fromJson(json);
			    
			    // Stimmt der Token?
			    if (token!= null && !token.equals(msg.token))
			    	throw new Exception (SternResources.ServerErrorNichtAuthorisiert(false));
			}
			catch (Exception x)
			{
				// Im Fehlerfall den Socket schliessen
				logMessage(
						LogEventId.M10,
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorDecode(false, x.getMessage()));
				
				this.closeSocket();
				return;
			}
			
			// Vergleiche die Build-Version des Clients mit der vom Server mindestens
			// vorausgesetzten Build-Version.
			boolean falscherBuild = false;
			
			String serverBuild = ReleaseGetter.getRelease();
			
			if (!serverBuild.equals(Constants.NO_BUILD_INFO)) // Wenn der Server aus Eclipse heraus gestartet wird
			{
				 if (!(msg.build != null && msg.build.equals(Constants.NO_BUILD_INFO)))
				 {
					 if (msg.build == null || msg.build.compareTo(Constants.MIN_BUILD) < 0)
					 {
						falscherBuild = true;
					 }
				 }
			}
			
			logMessage(
					LogEventId.M17,
					this.getId(),
					LogEventType.Information,
					SternResources.ServerInfoMessageType(false, msg.type.toString(), userId));
			
		    ResponseMessage resp = null;
		    
		    if (falscherBuild)
		    {
		    	resp = new ResponseMessage();
		    	resp.error = true;
		    	resp.errorMsg = SternResources.ServerBuildFalsch(
						true,
						ReleaseGetter.format(Constants.MIN_BUILD),
						msg.build == null ? "" : ReleaseGetter.format(msg.build));
		    }
		    else
		    {
		    	try
		    	{
				    if (userId.equals(ServerConstants.ADMIN_USER))
				    {
					    switch (msg.type)
					    {
						case ADMIN_CHANGE_USER:
							resp = processRequestAdminChangeUser(
									userId, 
									this.getId(), 
									RequestMessageChangeUser.fromJson(msg.payloadSerialized));
							break;
						case ADMIN_DELETE_USER:
							resp = processRequestAdminDeleteUser(msg.payloadSerialized);
							break;
						case ADMIN_PING:
							resp = new ResponseMessage();
							break;
						case ADMIN_SERVER_SHUTDOWN:
							shutdown = true;
							// Wird dann beim naechsten Verbindungsversuch heruntergefahren
							resp = new ResponseMessage();
							break;
						case ADMIN_GET_USERS:
							resp = processRequestAdminGetUsers();
							break;
						case ADMIN_GET_SERVER_STATUS:
							resp = processRequestAdminGetServerStatus();
							break;
						case ADMIN_GET_LOG:
							resp = processRequestAdminGetLog();
							break;
						case ADMIN_SET_LOG_LEVEL:
							resp = processRequestAdminSetLogLevel(
									RequestMessageSetLogLevel.fromJson(msg.payloadSerialized));
							break;
							
						default:
							resp = this.notAuthorized(userId);
							break;
					    }
				    }
				    else if (userId.equals(ServerConstants.ACTIVATION_USER))
				    {
					    switch (msg.type)
					    {
						case ACTIVATE_USER:
							RequestMessageActivateUser activateUser = RequestMessageActivateUser.fromJson(msg.payloadSerialized);
							
							resp = processRequestActivateUser(
									this.getId(), 
									activateUser);	
							
							if (resp != null)
								user = userRead(activateUser.userId);
							break;
						default:
							resp = this.notAuthorized(userId);
							break;
					    }
				    }
				    else
				    {
				    	// Gewöhnliche Benutzer
					    switch (msg.type)
					    {
					    case GET_GAME:
					    	resp = processRequestGetGame(userId, msg.payloadSerialized);
					    	break;
					    case GET_GAMES_AND_USERS:
					    	resp = processRequestGetGamesAndUsers(userId);
					    	break;
						case PING:
							resp = new ResponseMessage();
							break;
						case POST_NEW_GAME:
							resp = processRequestPostNewGame(user.userId, msg);
							break;
						case POST_MOVES:
							resp = processRequestPostMoves(
									user.userId, 
									(RequestMessagePostMoves)Utils.base64ToObject(msg.payloadSerialized, RequestMessagePostMoves.class, null));
							break;
						case GET_GAMES_ZUGEINGABE:
							resp = processRequestGetGamesZugeingabe(userId);
					    	break;
						case GET_EVALUATIONS:
							resp = processRequestGetEvaluations(
									(RequestMessageGetEvaluations)Utils.base64ToObject(msg.payloadSerialized, RequestMessageGetEvaluations.class, null));
							break;
						case GAME_HOST_DELETE_GAME:
							resp = processRequestGameHostDeleteGame(
									(RequestMessageGameHostDeleteGame)Utils.base64ToObject(
											msg.payloadSerialized, RequestMessageGameHostDeleteGame.class, null));
							break;
						case GAME_HOST_FINALIZE_GAME:
							resp = processRequestGameHostFinalizeGame(
									(RequestMessageGameHostFinalizeGame)Utils.base64ToObject(
											msg.payloadSerialized, RequestMessageGameHostFinalizeGame.class, null));
							break;
						default:
							resp = this.notAuthorized(userId);
							break;
					    }
				    }
		    	}
		    	catch (Exception x)
		    	{
		    		StringBuilder sb = new StringBuilder();
		    		
		    		sb.append(x.toString());
		    		sb.append('\n');
		    		
		    		for (StackTraceElement ste: x.getStackTrace())
		    		{
		    			sb.append(ste.toString());
		    			sb.append('\n');
		    		}
		    		
		    		resp = new ResponseMessage();
					
					resp.error = true;
					resp.errorMsg = 
							SternResources.ServerAnwendungsfehler(
									true,
									sb.toString());
					
					logMessage(
							LogEventId.M23,
							this.getId(),
							LogEventType.Critical,
							sb.toString());
		    	}
		    }
		    
		    if (resp == null)
		    {
		    	// Unberechtigter Zugriff oder irgendein anderer Grund, keine Antwort zu schicken
		    	this.closeSocket();
		    	return;
		    }
		    
		    // Set build in response message
		    resp.build = ReleaseGetter.getRelease();
		    
		    // Send response message
		    byte[] byteResponse = RsaCrypt.encrypt(resp.toJson(), user.userPublicKeyObject);
		    
		    int length = (byteResponse.length); 
			byte[] lengthBytes = ServerUtils.convertIntToByteArray(length);

			try
			{
				out.write(lengthBytes);
				out.write(byteResponse);
			}
			catch (Exception x)
			{
				// Im Fehlerfall den Socket schliessen
				logMessage(
						LogEventId.M11,
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorSendResponse(false, x.getMessage()));
				
				this.closeSocket();
				return;
			}
			
		    // Socket schliessen
			this.closeSocket();
		}
		
		private void closeSocket()
		{
			logMessage(
					LogEventId.M7,
					this.getId(),
					LogEventType.Verbose,
					SternResources.ServerInfoClientClosing(false, socket.getInetAddress().toString()));

		    try {
				this.socket.close();
			} catch (IOException x)
			{
				logMessage(
						LogEventId.M12,
						this.getId(),
						LogEventType.Error,
						SternResources.ServerErrorClientClosing(false, x.getMessage()));
			}
		    
		    logMessage(
					LogEventId.M8,
					this.getId(),
					LogEventType.Verbose,
					SternResources.ServerThreadClosing(false));
		}
		
		private ResponseMessage notAuthorized(String userId)
		{
			ResponseMessage respMessage = new ResponseMessage();
			
			respMessage.error = true;
			respMessage.errorMsg = 
					SternResources.ServerErrorNotAuthorized(true, userId);
			
			return respMessage;
		}
	}
	
	private ResponseMessage processRequestGetGame(String userId, String gameId)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.getLockObject(gameId))
		{
			Spiel spiel = this.gameRead(gameId);
			
			if (spiel != null)
			{
				int spIndex = -1;
				
				for (int i = 0; i < spiel.getSpieler().length; i++)
				{
					Spieler sp = spiel.getSpieler()[i];
					
					if (sp.getName().equals(userId))
					{
						spIndex = i;
						break;
					}
				}
				
				if (spIndex >= 0)
				{
					// Nur Auswertung des letzten Jahres mitgeben.
					// Fruehere Auswertungen werden bei Bedarf nachgeladen
					Spiel spielKopie = spiel.copyWithReducedInfo(spIndex, true);
					msgResponse.payloadSerialized = Utils.objectToBase64(spielKopie, null);
				}
				else
				{
					msgResponse.error = true;
					msgResponse.errorMsg = 
							SternResources.ServerErrorSpielerNimmNichtTeil(true, userId);
				}
			}
			else
			{
				msgResponse.error = true;
				msgResponse.errorMsg = 
						SternResources.ServerErrorSpielExistiertNicht(true, gameId);
			}
			
		}

		return msgResponse;
	}
	
	private ResponseMessage processRequestGetEvaluations(RequestMessageGetEvaluations msg)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.getLockObject(msg.gameId))
		{
			ResponseMessageGetEvaluations msgResponseGetEvaluations = new ResponseMessageGetEvaluations();

			Spiel spiel = this.gameRead(msg.gameId);
			
			if (spiel != null)
				msgResponseGetEvaluations.archiv = spiel.getArchiv(msg.vonJahr, msg.bisJahr);
			
			msgResponse.error = false;
			msgResponse.payloadSerialized = msgResponseGetEvaluations.toJson();
		}

		return msgResponse;
	}
	
	private ResponseMessage processRequestGameHostDeleteGame(RequestMessageGameHostDeleteGame msg)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.users)
		{		
			synchronized(this.getLockObject(msg.gameId))
			{
				Spiel spiel = this.gameRead(msg.gameId);
				
				if (spiel == null)
				{
					msgResponse.error = true;
					msgResponse.errorMsg = SternResources.ServerErrorSpielExistiertNicht(true, msg.gameId);
				}
				else
				{
					// Ist der User der Spielleiter?
					if (msg.gameHostUserId.equals(spiel.getSpieler()[0].getName()))
					{
						this.gameDelete(spiel);
						msgResponse.error = false;
					}
					else
					{
						msgResponse.error = true;
						msgResponse.errorMsg = SternResources.ServerErrorKeinSpielleiter(true, msg.gameId);
					}
				}
			}
		}

		return msgResponse;
	}
	
	private ResponseMessage processRequestGameHostFinalizeGame(RequestMessageGameHostFinalizeGame msg)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.getLockObject(msg.gameId))
		{
			Spiel spiel = this.gameRead(msg.gameId);
			
			if (spiel == null)
			{
				msgResponse.error = true;
				msgResponse.errorMsg = SternResources.ServerErrorSpielExistiertNicht(true, msg.gameId);
			}
			else
			{
				if (spiel.getAbgeschlossen())
				{
					msgResponse.error = true;
					msgResponse.errorMsg = 
							SternResources.ServerGamesAbgeschlossen(true);
				}
				else
				{
					// Ist der User der Spielleiter?
					if (msg.gameHostUserId.equals(spiel.getSpieler()[0].getName()))
					{
						spiel.abschliessenServer();
						this.gameUpdate(spiel, false);
						msgResponse.error = false;
					}
					else
					{
						msgResponse.error = true;
						msgResponse.errorMsg = SternResources.ServerErrorKeinSpielleiter(true, msg.gameId);
					}
				}
			}
		}

		return msgResponse;
	}


	
	private Object getLockObject(String uuid)
	{
		// Ist egal, ob uuid ein Spiel oder einen Spieler darstellt
		if (this.lockObjects.containsKey(uuid))
			return this.lockObjects.get(uuid);
		else
		{
			Object lockObject = new Object();
			this.lockObjects.put(uuid, lockObject);
			return lockObject;
		}
	}
	
	private ResponseMessage processRequestAdminChangeUser(String adminUserId, long threadId, RequestMessageChangeUser msgNeuerSpieler)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		if (msgNeuerSpieler == null ||
				msgNeuerSpieler.userId == null || msgNeuerSpieler.userId.length() == 0 ||
				msgNeuerSpieler.email == null  || msgNeuerSpieler.email.length() == 0 ||
				msgNeuerSpieler.name == null   || msgNeuerSpieler.name.length() == 0)
		{
			msgResponse.error = true;
			msgResponse.errorMsg = SternResources.ServerErrorAdminNeuerUser(true);
			
			logMessage(
					LogEventId.M14,
					threadId,
					LogEventType.Error,
					SternResources.ServerErrorAdminNeuerUser(false));

			return msgResponse;
		}
		
		// Pruefen, ob der gewuenschte Alias frei ist
		boolean isAllowed =
				msgNeuerSpieler.create ?
						this.isUserNameAllowed(msgNeuerSpieler.userId) :
						this.userExists(msgNeuerSpieler.userId);
		
		if (!isAllowed)
		{
			msgResponse.error = true;
			
			if (msgNeuerSpieler.create)				
				msgResponse.errorMsg = 
						SternResources.ServerErrorAdminUserUnzulaessig(true, 
								msgNeuerSpieler.userId, 
								Integer.toString(Constants.SPIELER_NAME_MIN_LAENGE), 
								Integer.toString(Constants.SPIELER_NAME_MAX_LAENGE));
			else
				msgResponse.errorMsg = 
						SternResources.ServerErrorAdminUserExistiertNicht(true, msgNeuerSpieler.userId);
		}
		else
		{
			UserServer user = 
					msgNeuerSpieler.create ?
							new UserServer(msgNeuerSpieler.userId) :
							this.users.get(msgNeuerSpieler.userId);

			user.email = msgNeuerSpieler.email.trim();
			user.name = msgNeuerSpieler.name.trim();
			
			if (msgNeuerSpieler.renewCredentials)
			{
				user.userPublicKey = null;
				user.userPublicKeyObject = null;
				
				user.active = false; // Benutzer muss spaeter noch vom Benutzer aktiviert werden
				user.activationCode = UUID.randomUUID().toString();
			}

			logMessage(
					LogEventId.M15,
					threadId,
					LogEventType.Information,
					SternResources.ServerInfoInaktiverBenutzerAngelegt(false, user.userId));
			
			this.userUpdate(user);
			
			// E-Mail-Adressen in den Spielen ersetzen, an denen der Spieler beteiligt ist
			for (String gameId: user.games)
			{
				synchronized(this.getLockObject(gameId))
				{
					Spiel spiel = this.gameRead(gameId);
					
					spiel.setSpielerEmailAdresse(user.userId, user.email);
					
					this.gameUpdate(spiel, false);
				}
			}
			
			msgResponse.error = false;
			
			ResponseMessageChangeUser msgResponseNewUser = new ResponseMessageChangeUser();

			msgResponseNewUser.activationCode = user.activationCode;
			msgResponseNewUser.adminEmail = serverConfig.adminEmail;
			msgResponseNewUser.serverPort = serverConfig.port;
			msgResponseNewUser.serverPublicKey = serverConfig.serverPublicKey;
			msgResponseNewUser.serverUrl = serverConfig.url;
			msgResponseNewUser.userId = msgNeuerSpieler.userId;
			
			msgResponse.payloadSerialized = msgResponseNewUser.toJson();
		}

		return msgResponse;
	}
	
	private ResponseMessage processRequestAdminDeleteUser(String userId)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.users)
		{
			UserServer user = this.users.get(userId);
			
			if (user == null)
			{
				msgResponse.error = true;
				msgResponse.errorMsg = SternResources.ServerErrorAdminUserExistiertNicht(true, userId);
				
				return msgResponse;
			}
			
			// Ueber alle Spiele des Users gehen und Spieler aus dem Spiel entfernen
			for (String gameId: user.games)
			{
				synchronized(this.getLockObject(gameId))
				{
					Spiel spiel = this.gameRead(gameId);
					
					spiel.spielerEntfernen(userId);
					
					this.gameUpdate(spiel, false);
				}
			}
			
			// Jetzt den Spieler vom Server loeschen
			this.userDelete(userId);
		}
		
		return msgResponse;
	}
	
	
	private ResponseMessage processRequestAdminGetUsers()
	{
		ResponseMessage msgResponse = new ResponseMessage();
					
		ResponseMessageGetUsers msgResponseGetUsers = new ResponseMessageGetUsers();
		
		msgResponseGetUsers.users = new ArrayList<ResponseMessageGetUsers.UserInfo>();
		
		synchronized(this.users)
		{
			for (UserServer user: this.users.values())
			{
				if (!user.userId.equals(ServerConstants.ADMIN_USER))
					msgResponseGetUsers.addUserInfo(
							user.userId, 
							user.name, 
							user.email,
							user.active);
			}
		}
		
		msgResponse.payloadSerialized = msgResponseGetUsers.toJson();

		return msgResponse;
	}
	
	private ResponseMessage processRequestAdminGetServerStatus()
	{
		ResponseMessage msgResponse = new ResponseMessage();
					
		ResponseMessageGetServerStatus msgResponsePayload = new ResponseMessageGetServerStatus();
		
		msgResponsePayload.logLevel = serverConfig.logLevel;
		msgResponsePayload.build = ReleaseGetter.getRelease();
		msgResponsePayload.serverStartDate = this.serverStartDate;
		
		if (this.logFilePath != null)
		{
			msgResponsePayload.logSizeBytes = new File(this.logFilePath.toString()).length();
		}
				
		msgResponse.payloadSerialized = msgResponsePayload.toJson();

		return msgResponse;
	}
	
	private ResponseMessage processRequestAdminGetLog()
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		ResponseMessageGetLog msgResponsePayload = new ResponseMessageGetLog();
		
		if (this.logFilePath != null)
		{
			try
	        {
				File file = new File(this.logFilePath.toString());
				msgResponsePayload.fileName = file.getName();
				msgResponsePayload.logCsv = new String ( Files.readAllBytes( this.logFilePath ) );
	        } 
	        catch (Exception e) 
	        {
	        	msgResponse.error = true;
	        	msgResponse.errorMsg = e.getMessage();
	        }
		}
		
		msgResponse.payloadSerialized = msgResponsePayload.toJson();

		return msgResponse;
	}
	
	private ResponseMessage processRequestAdminSetLogLevel(RequestMessageSetLogLevel requestMsg)
	{
		serverConfig.logLevel = requestMsg.logLevel;
		
		// Server-Konfiguration anpassen
		File fileServerCredentials = Paths.get(homeDir, FOLDER_NAME_DATA, ServerConstants.SERVER_CONFIG_FILE).toFile();
		this.updateServerConfig(fileServerCredentials, false);
		
		return new ResponseMessage();
	}
	
	private ResponseMessage processRequestActivateUser(
			long threadId, 
			RequestMessageActivateUser msg)
	{
		ResponseMessage respMsg = new ResponseMessage();
		respMsg.error = true;
		
		UserServer user = this.userRead(msg.userId);
		
		if (user == null)
		{
			// Benutzer ist nicht erlaubt. Einfach Socketverbindung schließen.
			// Die Rückantwort kann ohnehin nicht verschlüsselt werden.
			return null;
		}
		
		if (!msg.activationCode.equals(user.activationCode))
		{
			// Wir haben noch keinen Public Key zum Verschluesseln der Antwort,
			// deshalb einfach aussteigen
			return null;
		}
		
		if (user.active)
		{
			respMsg.errorMsg = 
					SternResources.ServerErrorBenutzerBereitsAktiviert(true, msg.userId);
			return respMsg;
		}
				
		user.activationCode = "";
		user.active = true;
		user.userPublicKey = msg.userPublicKey;
		
		this.userUpdate(user);
		
		respMsg.error = false;
		return respMsg;
	}
	
	private void userUpdate(UserServer user)
	{
		synchronized(this.users)
		{
			File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USER, user.userId).toFile();
			
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileUser.getAbsoluteFile())))
			{
				bw.write(user.toJson());
			} catch (IOException e) {

				e.printStackTrace();
				
			}
			
			user.userPublicKeyObject = RsaCrypt.decodePublicKeyFromBase64(user.userPublicKey);
			
			if (this.users.containsKey(user.userId))
				this.users.replace(user.userId, user);
			else
				this.users.put(user.userId, user);
			
			if (user.userId.equals(ServerConstants.ADMIN_USER))
				this.adminCreated = true;
		}
	}
	
	private UserServer userRead(String userId)
	{
		synchronized(this.users)
		{
			if (this.users.containsKey(userId))
				return this.users.get(userId);

			UserServer user = null;
			
			File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USER, userId).toFile();
			
			if (!fileUser.exists())
				return null;
			
			try (BufferedReader br = new BufferedReader(new FileReader(fileUser.getAbsoluteFile())))
			{
				String json = br.readLine();
				user = UserServer.fromJson(json);
				
				this.users.put(user.userId, user);
				
				if (user.userId.equals(ServerConstants.ADMIN_USER))
					this.adminCreated = true;
			} catch (Exception e)
			{

				e.printStackTrace();

			}
			
			return user;
		}
	}
	
	private void userDelete(String userId)
	{
		File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USER, userId).toFile();
		
		try
		{
			fileUser.delete();
		}
		catch (Exception e) {
	
			e.printStackTrace();
			
		}

		this.users.remove(userId);		
	}
	
	private Spiel gameRead(String gameId)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAME, gameId).toFile();
		
		Spiel spiel = Utils.readSpielFromFile(file);
		
		if (spiel != null)
		{
			// Alte Spiele ggf. migrieren
			spiel.migrieren();
			spiel.updateSaveBuild();
		}
		
		return spiel;
	}
	
	private String gameUpdate(Spiel spiel, boolean create)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAME, spiel.getName()).toFile();
		
		if (create && file.exists())
		{
			// Name ist schon belegt. Hänge eine Zahl an den Namen.
			int count = 1;
			
			do
			{
				file = Paths.get(
						homeDir, 
						FOLDER_NAME_DATA, 
						FOLDER_NAME_GAME, 
						spiel.getName() + count).toFile();
				
				count++;
			} while (file.exists());
			
			spiel.setName(file.getName());
		}
		
		spiel.setLetztesUpdate();
		Utils.writeSpielToFile(spiel, file);
		
		this.games.put(spiel.getName(), spiel.getSpielInfo());
		
		return spiel.getName();
	}
	
	private void gameDelete(Spiel spiel)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAME, spiel.getName()).toFile();
		
		if (file.exists())
			file.delete();
		
		this.games.remove(spiel.getName());
		
		for (UserServer user: this.users.values())
		{
			user.games.remove(spiel.getName());
		}
	}
	
	private boolean isUserNameAllowed(String userId)
	{
		boolean userNameAllowed = true;
		
		if (userId.length() >= Constants.SPIELER_NAME_MIN_LAENGE &&
			userId.length() <= Constants.SPIELER_NAME_MAX_LAENGE &&
			!userId.toLowerCase().equals(ServerConstants.ADMIN_USER.toLowerCase()) &&
			Pattern.matches(Constants.SPIELER_REGEX_PATTERN, userId)
			)
		{
			userNameAllowed = !this.userExists(userId);
		}
		else
		{
			userNameAllowed = false;
		}
				
		return userNameAllowed;
	}
	
	private boolean userExists(String userId)
	{
		boolean exists = false;
		
		synchronized(this.users)
		{
			for (UserServer user: this.users.values())
			{
				if (user.userId.toLowerCase().equals(userId.toLowerCase()))
				{
					exists = true;
					break;
				}
			}
		}
		
		return exists;
	}
	
	private String getInput()
	{
		String line = "";
		System.out.print(">");
		
		  try{
		     BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		     line = bufferRead.readLine();
		 }
		 catch(Exception ex)
		 {
		    ex.printStackTrace();
		 }
		
		return line;
	}

	private ResponseMessage processRequestPostNewGame(String userId, RequestMessage msg)
	{
		ResponseMessage msgResponse = new ResponseMessage();
		
		synchronized(this.users)
		{
			Spiel spiel = (Spiel)Utils.base64ToObject(msg.payloadSerialized, Spiel.class, null);
			
			for (Spieler spieler: spiel.getSpieler())
			{
				if (!spieler.istComputer())
				{
					UserServer user = this.users.get(spieler.getName());
					
					if (user == null)
					{
						msgResponse.error = true;
						msgResponse.errorMsg = 
								SternResources.ServerErrorAdminUserExistiertNicht(true, spieler.getName());
						
						return msgResponse;
					}
					else
						user.games.add(spiel.getName());
				}
			}
			
			String newName = this.gameUpdate(spiel, true);
			
			msgResponse.payloadSerialized = newName;
		}
			
		return msgResponse;
	}
	
	private ResponseMessage processRequestPostMoves(String userId, RequestMessagePostMoves msg )
	{
		synchronized(this.getLockObject(msg.gameId))
		{
			ResponseMessage msgResponse = new ResponseMessage();
			msgResponse.payloadSerialized = PostMovesResult.FEHLER.toString();
			
			Spiel spiel = this.gameRead(msg.gameId);
			
			boolean auswertungVerfuegbar = false;
			
			if (spiel == null)
			{
				msgResponse.error = true;
				msgResponse.errorMsg = 
						SternResources.ServerErrorSpielExistiertNicht(true, msg.gameId);
			}
			else
			{
				if (spiel.getAbgeschlossen())
				{
					msgResponse.error = true;
					msgResponse.errorMsg = 
							SternResources.ServerGamesAbgeschlossen(true);
				}
				else
				{
					int spIndex = spiel.spielzuegeEinfuegen(msg.zuege);
					
					if (spIndex >= 0)
					{
						// Haben alle Spieler ihre Spielzuege eingegeben?
						// Wenn ja, Auswertung machen
						auswertungVerfuegbar = spiel.starteAuswertungServer();
						
						// Spiel abspeichern
						this.gameUpdate(spiel, false);
						
						msgResponse.payloadSerialized =
								auswertungVerfuegbar ?
										PostMovesResult.AUSWERTUNG_VERFUEGBAR.toString() :
										PostMovesResult.WARTE.toString();
								
					}
					else
					{
						msgResponse.error = true;
						msgResponse.errorMsg = 
								SternResources.ServerErrorJahrVorbei(true);
					}
				}
			}
			
			return msgResponse;
		}
	}

	private ResponseMessage processRequestGetGamesAndUsers(String userId)
	{
		synchronized(this.users)
		{
			// Besorge alle User und alle Spiele eines bestimmten Spielers
			ResponseMessageGamesAndUsers msgResponse = new ResponseMessageGamesAndUsers();
			
			msgResponse.users = new Hashtable<String, ResponseMessageGamesAndUsers.UserInfo>();
			msgResponse.gamesZugeingabe = new ArrayList<SpielInfo>();
			msgResponse.gamesWarten = new ArrayList<SpielInfo>();
			msgResponse.gamesBeendet = new ArrayList<SpielInfo>();
			msgResponse.gamesSpielleiter = new ArrayList<SpielInfo>();
			
			msgResponse.emailAdresseSpielleiter = SternServer.serverConfig.adminEmail;
			
			// Alle aktiven user mitgeben
			for (UserServer user: this.users.values())
				if (!user.userId.equals(ServerConstants.ADMIN_USER) && user.active)
					msgResponse.users.put(user.userId, msgResponse.new UserInfo(user.email));		
			
			// Alle Spiele des Spielers mitgeben
			UserServer user = this.users.get(userId);
			
			for (String gameId: user.games)
			{
				SpielInfo spielInfo = this.games.get(gameId);
				
				if (spielInfo.abgeschlossen)
					msgResponse.gamesBeendet.add(spielInfo);
				else
				{
					if (spielInfo.zugeingabeBeendet.contains(userId))
						msgResponse.gamesWarten.add(spielInfo);
					else
						msgResponse.gamesZugeingabe.add(spielInfo);
				}
				
				if (spielInfo.spieler[0].getName().equals(userId))
					msgResponse.gamesSpielleiter.add(spielInfo);
			}
			
			ResponseMessage msg = new ResponseMessage(); 
			
			msg.payloadSerialized = msgResponse.toJson();
		
			return msg;
		}
	}
	
	private ResponseMessage processRequestGetGamesZugeingabe(String userId)
	{
		synchronized(this.users)
		{
			int count = 0;
			
			// Zaehle Spiele eines Spielers, die auf Zugeingabe warten.
			UserServer user = this.users.get(userId);
			
			for (String gameId: user.games)
			{
				SpielInfo spielInfo = this.games.get(gameId);
				
				if (!spielInfo.abgeschlossen && !spielInfo.zugeingabeBeendet.contains(userId))
					count++;
			}
			
			ResponseMessage msg = new ResponseMessage(); 
			
			msg.payloadSerialized = Integer.toString(count);
		
			return msg;
		}
	}

	private void updateServerConfig(File fileServerCredentials, boolean writeConsoleMessage)
	{
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileServerCredentials.getAbsoluteFile())))
		{
			String text = serverConfig.toJson();
			bw.write(text);
			
			if (writeConsoleMessage)
			{
				System.out.println(
						SternResources.ServerIDateiAngelegt(
								false, 
								fileServerCredentials.getAbsolutePath().toString()));
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
