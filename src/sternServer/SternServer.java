/**	STERN - a strategy game
    Copyright (C) 1989-2021 Michael Schweitzer, spielwitz@icloud.com

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

import com.google.gson.Gson;

import common.Constants;
import common.PostMovesResult;
import common.ReleaseGetter;
import common.Game;
import common.GameInfo;
import common.Player;
import common.SternResources;
import common.Utils;
import commonServer.Ciphers;
import commonServer.ClientUserCredentials;
import commonServer.CryptoLib;
import commonServer.LogEventType;
import commonServer.RequestMessage;
import commonServer.RequestMessageActivateUser;
import commonServer.RequestMessageGetStatus;
import commonServer.RequestMessageChangeUser;
import commonServer.RequestMessageGameHostDeleteGame;
import commonServer.RequestMessageGameHostFinalizeGame;
import commonServer.RequestMessagePostMoves;
import commonServer.RequestMessageSetLogLevel;
import commonServer.RequestMessageUserId;
import commonServer.ResponseMessage;
import commonServer.ResponseMessageGamesAndUsers;
import commonServer.ResponseMessageGetLog;
import commonServer.ResponseMessageGetServerStatus;
import commonServer.ResponseMessageGetStatus;
import commonServer.ResponseMessageGetUsers;
import commonServer.ResponseMessageUserId;
import commonServer.ResponseMessageChangeUser;
import commonServer.ServerConstants;
import commonServer.ServerUtils;

public class SternServer // NO_UCD (unused code)
{
	private static ServerConfiguration serverConfig;
	
	private final static String FOLDER_NAME_DATA = "ServerData";
	private final static String FOLDER_NAME_LOGS = "Logs";
	private final static String FOLDER_NAME_USERS = "Users";
	private final static String FOLDER_NAME_GAMES = "Games";
	private final static String homeDir;
	
	private Path logFilePath;
	
	private Hashtable<String,UserServer> users;
	private Hashtable<String,GameInfo> games;
	private Hashtable<String, Ciphers> ciphersPerSession;
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
		SternServer server = new SternServer();
		
		try {
			server.run();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private void run()
	{
		System.out.println(SternResources.ServerStarting(false));
		
		this.lockObjects = new Hashtable<String, Object>();
		this.ciphersPerSession = new Hashtable<String, Ciphers>();
		
		this.initCreateDataFolders();
		this.initServerConfig();
		this.initReadAllUsers();
		this.initCreateAdmin();
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
			    
			    ServerThread serverThread = new ServerThread(clientSocket);
			    
			    logMessage(
						LogEventId.M5,
						LogEventType.Information,
						SternResources.ServerIncomingConnection(
								false, 
								clientSocket.getInetAddress().toString(),
								Long.toString(serverThread.getId())));

			    serverThread.start();			    
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
		this.createFolder(new File(homeDir, FOLDER_NAME_DATA));
		this.createFolder(Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_LOGS).toFile());
		this.createFolder(Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USERS).toFile());
		this.createFolder(Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAMES).toFile());
	}
	
	private void createFolder(File dir)
	{
		if (!dir.exists())
		{
			System.out.println(
					SternResources.ServerOrdnerErzeugen(
							false, 
							dir.getAbsolutePath().toString()));
			dir.mkdir();
		}
	}
	
	private void initCreateAdmin()
	{
		if (!this.adminCreated)
		{
			System.out.println(SternResources.ServerAdminErzeugen(false));
			
			KeyPair userKeyPair = CryptoLib.getNewKeyPair();
			
			String user = ServerConstants.ADMIN_USER;
			
			UserServer adminUser = new UserServer(user);
			
			adminUser.email = "";
			adminUser.name = "";
			adminUser.userId = user;
			adminUser.active = true;
			
			adminUser.userPublicKey = 
					CryptoLib.encodePublicKeyToBase64(userKeyPair.getPublic());
			
			this.updateUser(adminUser);
			
			ClientUserCredentials clientUserCredentialsAdmin = new ClientUserCredentials();
			clientUserCredentialsAdmin.userId = user;
			clientUserCredentialsAdmin.userPrivateKey = CryptoLib.encodePrivateKeyToBase64(userKeyPair.getPrivate());
			
			clientUserCredentialsAdmin.serverPublicKey = serverConfig.serverPublicKey;
			clientUserCredentialsAdmin.url = serverConfig.url;
			clientUserCredentialsAdmin.port = serverConfig.port;
			
			File fileClientInfo = Paths.get(
					homeDir, 
					FOLDER_NAME_DATA, 
					ServerUtils.getCredentialFileName(
							ServerConstants.ADMIN_USER, 
							serverConfig.url, 
							serverConfig.port)).toFile();
			
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileClientInfo.getAbsoluteFile())))
			{
				bw.write(clientUserCredentialsAdmin.toJson());
				
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
	
	private void initServerConfig()
	{
		File fileServerCredentials = Paths.get(homeDir, FOLDER_NAME_DATA, ServerConstants.SERVER_CONFIG_FILE).toFile();
		
		if (fileServerCredentials.exists())
		{
			System.out.println(SternResources.ServerConfigLaden(false));
			
			try (BufferedReader br = new BufferedReader(new FileReader(fileServerCredentials.getAbsoluteFile())))
			{
				serverConfig = ServerConfiguration.fromJson(br.readLine()); 
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (serverConfig.locale != null)
				SternResources.setLocale(serverConfig.locale);
		}
		else
		{
			String serverUrl = null;
			int port = 0;
			String adminEmail = null;
			String locale = null;
			
			while (true)
			{
				System.out.println("Deutsch [de] / English [en]: ");
				
				String language = this.getKeyInput();
				
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
		    serverUrl = this.getKeyInput();
		    
		    serverUrl = serverUrl.length() == 0 ?
		    				ServerConstants.SERVER_HOSTNAME :
		    				serverUrl;
			
		    System.out.print(SternResources.ServerAdminPort(false ) + " ("+SternResources.ServerVoreingestellt(false)+": "+ServerConstants.SERVER_PORT+"): ");
		    String serverPort = this.getKeyInput();
		    
		    port = serverPort.length() == 0 ? 
		    		ServerConstants.SERVER_PORT :
		    		Integer.parseInt(serverPort);
		    
		    System.out.print(SternResources.ServerEmailAdmin(false)+": ");
		    adminEmail = this.getKeyInput();
		    
		    System.out.println("\n"+SternResources.ServerAdminUrl(false)+": " + serverUrl);
		    System.out.println(SternResources.ServerAdminPort(false )+": " + port);
		    System.out.println(SternResources.ServerEmailAdmin(false)+": " + adminEmail);
		    
		    System.out.print("\n"+SternResources.ServerInitConfirm(false)+": ");
		    String ok = this.getKeyInput();
		    		    
		    if (!ok.equals("1"))
		    {
		    	 System.out.print(SternResources.ServerInitAbort(false));
		    	 System.exit(0);
		    }
			
			KeyPair keyPairRequest = CryptoLib.getNewKeyPair();
			
			serverConfig = new ServerConfiguration();
			
			serverConfig.locale = locale;
			
			serverConfig.serverPrivateKeyObject = keyPairRequest.getPrivate();
			
			serverConfig.serverPrivateKey = CryptoLib.encodePrivateKeyToBase64(serverConfig.serverPrivateKeyObject);
			serverConfig.serverPublicKey = CryptoLib.encodePublicKeyToBase64(keyPairRequest.getPublic());
			
			serverConfig.url = serverUrl;
			serverConfig.port = port;
			serverConfig.adminEmail = adminEmail;
			
			this.updateServerConfig(fileServerCredentials, true);
		}
	}
	
	private void initReadAllUsers()
	{
		this.users = new Hashtable<String, UserServer>();
		this.adminCreated = false;
		
		File directoryUsers = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USERS).toFile();
		for (String fileName: directoryUsers.list())
		{
			if (fileName.equals(ServerConstants.ADMIN_USER) || Pattern.matches(Constants.PLAYER_NAME_REGEX_PATTERN, fileName))
			{
				System.out.println(SternResources.ServerUserLesen(false, fileName));
				this.readUser(fileName);
			}
		}
	}
	
	private void initReadAllGames()
	{
		File directoryGames = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAMES).toFile();
		this.games = new Hashtable<String, GameInfo>();
		
		for (String fileName: directoryGames.list())
		{
			System.out.println(SternResources.ServerSpielLesen(false, fileName));
			Game game = this.readGame(fileName);
			
			if (game == null)
				continue;
			
			this.games.put(game.getName(), game.getGameInfo());
			
			for (Player player: game.getPlayers())
			{
				if (this.users.containsKey(player.getName()))
					this.users.get(player.getName()).games.add(game.getName());
			}
		}		
	}
	
	private void logMessage(int eventId, LogEventType severity, String msg)
	{
		this.logMessage(eventId, 0, severity, null, msg);
	}
	private void logMessage(
			int eventId, 
			long threadId, 
			LogEventType severity,
			String sessionId,
			String msg)
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
			sb.append(SternResources.ServerILogSessionId(false) + "\t");
			sb.append(SternResources.ServerILogLevel(false) + "\t");
			sb.append(SternResources.ServerILogMeldung(false) + "\n\n");
		}
		
		sb.append(Utils.getLocalizedDateString());
		sb.append("\t");
		
		sb.append(eventId);
		sb.append("\t");
		
		if (threadId > 0)
			sb.append(threadId);
		sb.append("\t");
		
		if (sessionId != null)
			sb.append(sessionId);
		sb.append("\t");
		
		sb.append(severity.toString());
		sb.append("\t");
		
		sb.append(msg);
		
		sb.append("\n");
		
		if (this.logFilePath == null)
		{
			this.logFilePath = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_LOGS, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".csv");
			
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
			String userId = null;
			String sessionId = null;
			OutputStream out = null;
			DataInputStream in = null;
			
			try
			{
			    in = new DataInputStream(this.socket.getInputStream());
			    out = this.socket.getOutputStream();
			    
			    RequestMessageUserId msgRequest = (RequestMessageUserId)
			    		RequestMessageUserId.fromJson(
			    				CryptoLib.receiveStringRsaEncrypted(in, 
			    				serverConfig.serverPrivateKeyObject),
			    				RequestMessageUserId.class);
			    
			    userId = msgRequest.userId;
			    sessionId = msgRequest.sessionId;
			    
			    if (userId.length() < Constants.PLAYER_NAME_LENGTH_MIN || 
				    userId.length() > Constants.PLAYER_NAME_LENGTH_MAX)
				{
				    	logMessage(
								LogEventId.M21,
								this.getId(),
								LogEventType.Error,
								sessionId,
								SternResources.ServerErrorUngueltigeLaengeBenutzer(
										false,
										Integer.toString(userId.length())));
				    	
				    	this.closeSocket(sessionId);
					    return;
			    }
			    
			    logMessage(
						LogEventId.M6,
						this.getId(),
						LogEventType.Verbose,
						sessionId,
						SternResources.ServerBenutzer(false, userId));
			    
			    if (!userId.equals(ServerConstants.ACTIVATION_USER) && !users.containsKey(userId))
				{
				    	logMessage(
								LogEventId.M16,
								this.getId(),
								LogEventType.Error,
								sessionId,
								SternResources.ServerErrorUngueltigerBenutzer(false, userId));
				    	
				    	this.closeSocket(sessionId);
					    return;
			    }
			}
			catch (Exception x)
			{
				logMessage(
						LogEventId.M9,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorRequestReceive(false, x.getMessage()));
				
				this.closeSocket(sessionId);
				return;
			}
			
			UserServer user = userId.equals(ServerConstants.ACTIVATION_USER) ?
					null :
					readUser(userId);
			
			if (user != null && !user.active)
			{
				logMessage(
						LogEventId.M24,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorLogonWithInactiveUser(false, user.userId));
				
				this.closeSocket(sessionId);
				return;
			}
			
			String token = 
					userId.equals(ServerConstants.ACTIVATION_USER) ?
							CryptoLib.NULL_UUID :
							UUID.randomUUID().toString();
			
			Ciphers ciphers = getCiphers(sessionId);
			
			try
			{
				if (!userId.equals(ServerConstants.ACTIVATION_USER))
				{
					ResponseMessageUserId respMsg = new ResponseMessageUserId();
					respMsg.token = token;
					respMsg.sessionValid = ciphers != null;

					CryptoLib.sendStringRsaEncrypted(
							out, 
							respMsg.toJson(), 
							user.userPublicKeyObject);
				}
				
				if (ciphers == null)
				{
					ciphers = CryptoLib.diffieHellmanKeyAgreementServer(in, out);
					sessionId = ciphers.sessionId;
					
					if (!userId.equals(ServerConstants.ACTIVATION_USER))
					{
						setCiphers(sessionId, ciphers);
					}
					
					logMessage(
							LogEventId.M26,
							this.getId(),
							LogEventType.Information,
							sessionId,
							SternResources.ServerNeueSession(false, ciphers.sessionId, userId));
				}
			}
			catch (Exception x)
			{
				logMessage(
						LogEventId.M25,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorDh(false, x.getMessage()));
				
				this.closeSocket(sessionId);
				return;
			}

			RequestMessage requestMessage = null;
			
			try
			{
				requestMessage = (RequestMessage)RequestMessage.fromJson(
						CryptoLib.receiveStringAesEncrypted(in, ciphers.cipherDecrypt),
						RequestMessage.class);
			    
			    if (token!= null && !token.equals(requestMessage.token))
			    	throw new Exception (SternResources.ServerErrorNichtAuthorisiert(false));
			}
			catch (Exception x)
			{
				logMessage(
						LogEventId.M10,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorDecode(false, x.getMessage()));
				
				this.closeSocket(sessionId);
				return;
			}
			
			boolean buildNotSuitable = false;
			
			String buildServer = ReleaseGetter.getRelease();
			
			if (!buildServer.equals(Constants.BUILD_NO_INFO))
			{
				 if (!(requestMessage.build != null && requestMessage.build.equals(Constants.BUILD_NO_INFO)))
				 {
					 if (requestMessage.build == null || requestMessage.build.compareTo(Constants.BUILD_COMPATIBLE) < 0)
					 {
						buildNotSuitable = true;
					 }
				 }
			}
			
			logMessage(
					LogEventId.M17,
					this.getId(),
					LogEventType.Information,
					sessionId,
					SternResources.ServerInfoMessageType(false, requestMessage.type.toString(), userId));
			
		    ResponseMessage responseMessage = null;
		    
		    if (buildNotSuitable)
		    {
		    	responseMessage = new ResponseMessage();
		    	responseMessage.error = true;
		    	responseMessage.errorMsg = SternResources.ServerBuildFalsch(
						true,
						Constants.BUILD_COMPATIBLE,
						requestMessage.build == null ? "" : requestMessage.build);
		    }
		    else
		    {
		    	try
		    	{
				    if (userId.equals(ServerConstants.ADMIN_USER))
				    {
					    switch (requestMessage.type)
					    {
						case ADMIN_CHANGE_USER:
							responseMessage = processRequestAdminChangeUser(
									userId, 
									this.getId(), 
									sessionId,
									(RequestMessageChangeUser)RequestMessageChangeUser.fromJson(
											requestMessage.payloadSerialized, RequestMessageChangeUser.class));
							break;
						case ADMIN_DELETE_USER:
							responseMessage = processRequestAdminDeleteUser(requestMessage.payloadSerialized);
							break;
						case ADMIN_PING:
							responseMessage = new ResponseMessage();
							break;
						case ADMIN_SERVER_SHUTDOWN:
							shutdown = true;
							responseMessage = new ResponseMessage();
							break;
						case ADMIN_GET_USERS:
							responseMessage = processRequestAdminGetUsers();
							break;
						case ADMIN_GET_SERVER_STATUS:
							responseMessage = processRequestAdminGetServerStatus();
							break;
						case ADMIN_GET_LOG:
							responseMessage = processRequestAdminGetLog();
							break;
						case ADMIN_SET_LOG_LEVEL:
							responseMessage = processRequestAdminSetLogLevel(
									(RequestMessageSetLogLevel)RequestMessageSetLogLevel.fromJson(
											requestMessage.payloadSerialized, RequestMessageSetLogLevel.class));
							break;
							
						default:
							responseMessage = this.getResponseMessageNotAuthorized(userId);
							break;
					    }
				    }
				    else if (userId.equals(ServerConstants.ACTIVATION_USER))
				    {
					    switch (requestMessage.type)
					    {
						case ACTIVATE_USER:
							RequestMessageActivateUser requestMessageActivateUser = (RequestMessageActivateUser)
								RequestMessageActivateUser.fromJson(requestMessage.payloadSerialized, RequestMessageActivateUser.class);
							
							responseMessage = processRequestActivateUser(
									this.getId(), 
									requestMessageActivateUser);	
							
							if (responseMessage != null)
								user = readUser(requestMessageActivateUser.userId);
							break;
						default:
							responseMessage = this.getResponseMessageNotAuthorized(userId);
							break;
					    }
				    }
				    else
				    {
					    switch (requestMessage.type)
					    {
					    case GET_GAME:
					    	responseMessage = processRequestGetGame(userId, requestMessage.payloadSerialized);
					    	break;
					    case GET_GAMES_AND_USERS:
					    	responseMessage = processRequestGetGamesAndUsers(userId);
					    	break;
						case PING:
							responseMessage = new ResponseMessage();
							break;
						case POST_NEW_GAME:
							responseMessage = processRequestPostNewGame(user.userId, requestMessage);
							break;
						case POST_MOVES:
							responseMessage = processRequestPostMoves(
									user.userId, 
									(RequestMessagePostMoves)RequestMessagePostMoves.fromJson(
											requestMessage.payloadSerialized, RequestMessagePostMoves.class));
							break;
						case GET_STATUS:
							responseMessage = processRequestGetStatus(
									userId,
									(RequestMessageGetStatus)RequestMessageGetStatus.fromJson(
											requestMessage.payloadSerialized, RequestMessageGetStatus.class));
					    	break;
						case GAME_HOST_DELETE_GAME:
							responseMessage = processRequestGameHostDeleteGame(
									(RequestMessageGameHostDeleteGame)RequestMessageGameHostDeleteGame.fromJson(
											requestMessage.payloadSerialized, RequestMessageGameHostDeleteGame.class));
							break;
						case GAME_HOST_FINALIZE_GAME:
							responseMessage = processRequestGameHostFinalizeGame(
									(RequestMessageGameHostFinalizeGame)RequestMessageGameHostFinalizeGame.fromJson(
											requestMessage.payloadSerialized, RequestMessageGameHostFinalizeGame.class));
							break;
						default:
							responseMessage = this.getResponseMessageNotAuthorized(userId);
							break;
					    }
				    }
		    	}
		    	catch (Exception x)
		    	{
		    		StringBuilder sb = new StringBuilder();
		    		
		    		sb.append(x.toString());
		    		sb.append('\n');
		    		
		    		for (StackTraceElement stackTraceElement: x.getStackTrace())
		    		{
		    			sb.append(stackTraceElement.toString());
		    			sb.append('\n');
		    		}
		    		
		    		responseMessage = new ResponseMessage();
					
					responseMessage.error = true;
					responseMessage.errorMsg = 
							SternResources.ServerAnwendungsfehler(
									true,
									sb.toString());
					
					logMessage(
							LogEventId.M23,
							this.getId(),
							LogEventType.Critical,
							sessionId,
							sb.toString());
		    	}
		    }
		    
		    if (responseMessage == null)
		    {
		    	this.closeSocket(sessionId);
		    	return;
		    }
		    
		    responseMessage.build = ReleaseGetter.getRelease();
		    
			try
			{
				 CryptoLib.sendStringAesEncrypted(out, responseMessage.toJson(), ciphers.cipherEncrypt);
			}
			catch (Exception x)
			{
				logMessage(
						LogEventId.M11,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorSendResponse(false, x.getMessage()));
				
				this.closeSocket(sessionId);
				return;
			}
			
			this.closeSocket(sessionId);
		}
		
		private void closeSocket(String sessionId)
		{
			logMessage(
					LogEventId.M7,
					this.getId(),
					LogEventType.Verbose,
					sessionId,
					SternResources.ServerInfoClientClosing(false, socket.getInetAddress().toString()));

		    try {
				this.socket.close();
			} catch (IOException x)
			{
				logMessage(
						LogEventId.M12,
						this.getId(),
						LogEventType.Error,
						sessionId,
						SternResources.ServerErrorClientClosing(false, x.getMessage()));
			}
		    
		    logMessage(
					LogEventId.M8,
					this.getId(),
					LogEventType.Verbose,
					sessionId,
					SternResources.ServerThreadClosing(false));
		}
		
		private ResponseMessage getResponseMessageNotAuthorized(String userId)
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
		ResponseMessage responseMessage = new ResponseMessage();
		
		synchronized(this.getLockObject(gameId))
		{
			Game game = this.readGame(gameId);
			
			if (game != null)
			{
				int playerIndex = -1;
				
				for (int i = 0; i < game.getPlayers().length; i++)
				{
					Player player = game.getPlayers()[i];
					
					if (player.getName().equals(userId))
					{
						playerIndex = i;
						break;
					}
				}
				
				if (playerIndex >= 0)
				{
					Game gameCopy = game.createCopyForPlayer(playerIndex);
					Gson gson = new Gson();
					responseMessage.payloadSerialized = gson.toJson(gameCopy);
				}
				else
				{
					responseMessage.error = true;
					responseMessage.errorMsg = 
							SternResources.ServerErrorSpielerNimmNichtTeil(true, userId);
				}
			}
			else
			{
				responseMessage.error = true;
				responseMessage.errorMsg = 
						SternResources.ServerErrorSpielExistiertNicht(true, gameId);
			}
			
		}

		return responseMessage;
	}
	
	private ResponseMessage processRequestGameHostDeleteGame(
			RequestMessageGameHostDeleteGame requestMessage)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		synchronized(this.users)
		{		
			synchronized(this.getLockObject(requestMessage.gameId))
			{
				Game game = this.readGame(requestMessage.gameId);
				
				if (game == null)
				{
					responseMessage.error = true;
					responseMessage.errorMsg = SternResources.ServerErrorSpielExistiertNicht(true, requestMessage.gameId);
				}
				else
				{
					if (requestMessage.gameHostUserId.equals(game.getPlayers()[0].getName()))
					{
						this.deleteGame(game);
						responseMessage.error = false;
					}
					else
					{
						responseMessage.error = true;
						responseMessage.errorMsg = SternResources.ServerErrorKeinSpielleiter(true, requestMessage.gameId);
					}
				}
			}
		}

		return responseMessage;
	}
	
	private ResponseMessage processRequestGameHostFinalizeGame(
			RequestMessageGameHostFinalizeGame requestMessage)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		synchronized(this.getLockObject(requestMessage.gameId))
		{
			Game game = this.readGame(requestMessage.gameId);
			
			if (game == null)
			{
				responseMessage.error = true;
				responseMessage.errorMsg = SternResources.ServerErrorSpielExistiertNicht(true, requestMessage.gameId);
			}
			else
			{
				if (game.isFinalized())
				{
					responseMessage.error = true;
					responseMessage.errorMsg = 
							SternResources.ServerGamesAbgeschlossen(true);
				}
				else
				{
					if (requestMessage.gameHostUserId.equals(game.getPlayers()[0].getName()))
					{
						game.finalizeGameServer();
						this.updateGame(game, false);
						responseMessage.error = false;
					}
					else
					{
						responseMessage.error = true;
						responseMessage.errorMsg = SternResources.ServerErrorKeinSpielleiter(true, requestMessage.gameId);
					}
				}
			}
		}

		return responseMessage;
	}
	
	private Object getLockObject(String uuid)
	{
		if (this.lockObjects.containsKey(uuid))
			return this.lockObjects.get(uuid);
		else
		{
			Object lockObject = new Object();
			this.lockObjects.put(uuid, lockObject);
			return lockObject;
		}
	}
	
	private ResponseMessage processRequestAdminChangeUser(
			String adminUserId, 
			long threadId,
			String sessionId,
			RequestMessageChangeUser requestMessage)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		if (requestMessage == null ||
				requestMessage.userId == null || requestMessage.userId.length() == 0 ||
				requestMessage.email == null  || requestMessage.email.length() == 0 ||
				requestMessage.name == null   || requestMessage.name.length() == 0)
		{
			responseMessage.error = true;
			responseMessage.errorMsg = SternResources.ServerErrorAdminNeuerUser(true);
			
			logMessage(
					LogEventId.M14,
					threadId,
					LogEventType.Error,
					sessionId,
					SternResources.ServerErrorAdminNeuerUser(false));

			return responseMessage;
		}
		
		boolean isAllowed =
				requestMessage.create ?
						this.isUserIdAllowed(requestMessage.userId) :
						this.userExists(requestMessage.userId);
		
		if (!isAllowed)
		{
			responseMessage.error = true;
			
			if (requestMessage.create)				
				responseMessage.errorMsg = 
						SternResources.ServerErrorAdminUserUnzulaessig(true, 
								requestMessage.userId, 
								Integer.toString(Constants.PLAYER_NAME_LENGTH_MIN), 
								Integer.toString(Constants.PLAYER_NAME_LENGTH_MAX));
			else
				responseMessage.errorMsg = 
						SternResources.ServerErrorAdminUserExistiertNicht(true, requestMessage.userId);
		}
		else
		{
			UserServer userServer = 
					requestMessage.create ?
							new UserServer(requestMessage.userId) :
							this.users.get(requestMessage.userId);

			userServer.email = requestMessage.email.trim();
			userServer.name = requestMessage.name.trim();
			
			if (requestMessage.renewCredentials)
			{
				userServer.userPublicKey = null;
				userServer.userPublicKeyObject = null;
				
				userServer.active = false;
				userServer.activationCode = UUID.randomUUID().toString();
			}

			logMessage(
					LogEventId.M15,
					threadId,
					LogEventType.Information,
					sessionId,
					SternResources.ServerInfoInaktiverBenutzerAngelegt(false, userServer.userId));
			
			this.updateUser(userServer);
			
			for (String gameId: userServer.games)
			{
				synchronized(this.getLockObject(gameId))
				{
					Game game = this.readGame(gameId);
					
					game.setPlayerEmailAddress(userServer.userId, userServer.email);
					
					this.updateGame(game, false);
				}
			}
			
			responseMessage.error = false;
			
			ResponseMessageChangeUser responseMessageNewUser = new ResponseMessageChangeUser();

			responseMessageNewUser.activationCode = userServer.activationCode;
			responseMessageNewUser.adminEmail = serverConfig.adminEmail;
			responseMessageNewUser.serverPort = serverConfig.port;
			responseMessageNewUser.serverPublicKey = serverConfig.serverPublicKey;
			responseMessageNewUser.serverUrl = serverConfig.url;
			responseMessageNewUser.userId = requestMessage.userId;
			
			responseMessage.payloadSerialized = responseMessageNewUser.toJson();
		}

		return responseMessage;
	}
	
	private ResponseMessage processRequestAdminDeleteUser(String userId)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		synchronized(this.users)
		{
			UserServer user = this.users.get(userId);
			
			if (user == null)
			{
				responseMessage.error = true;
				responseMessage.errorMsg = SternResources.ServerErrorAdminUserExistiertNicht(true, userId);
				
				return responseMessage;
			}
			
			for (String gameId: user.games)
			{
				synchronized(this.getLockObject(gameId))
				{
					Game game = this.readGame(gameId);
					
					game.removePlayerFromServerGame(userId);
					
					this.updateGame(game, false);
				}
			}
			
			this.deleteUser(userId);
		}
		
		return responseMessage;
	}
	
	
	private ResponseMessage processRequestAdminGetUsers()
	{
		ResponseMessage responseMessage = new ResponseMessage();
					
		ResponseMessageGetUsers responseMessageGetUsers = new ResponseMessageGetUsers();
		
		responseMessageGetUsers.users = new ArrayList<ResponseMessageGetUsers.UserInfo>();
		
		synchronized(this.users)
		{
			for (UserServer user: this.users.values())
			{
				if (!user.userId.equals(ServerConstants.ADMIN_USER))
					responseMessageGetUsers.addUserInfo(
							user.userId, 
							user.name, 
							user.email,
							user.active);
			}
		}
		
		responseMessage.payloadSerialized = responseMessageGetUsers.toJson();

		return responseMessage;
	}
	
	private ResponseMessage processRequestAdminGetServerStatus()
	{
		ResponseMessage responseMessage = new ResponseMessage();
					
		ResponseMessageGetServerStatus responseMessageGetServerStatus = new ResponseMessageGetServerStatus();
		
		responseMessageGetServerStatus.logLevel = serverConfig.logLevel;
		responseMessageGetServerStatus.build = ReleaseGetter.getRelease();
		responseMessageGetServerStatus.serverStartDate = this.serverStartDate;
		
		if (this.logFilePath != null)
		{
			responseMessageGetServerStatus.logSizeBytes = new File(this.logFilePath.toString()).length();
		}
				
		responseMessage.payloadSerialized = responseMessageGetServerStatus.toJson();

		return responseMessage;
	}
	
	private ResponseMessage processRequestAdminGetLog()
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		ResponseMessageGetLog responseMessageGetLog = new ResponseMessageGetLog();
		
		if (this.logFilePath != null)
		{
			try
	        {
				File file = new File(this.logFilePath.toString());
				responseMessageGetLog.fileName = file.getName();
				responseMessageGetLog.logCsv = new String ( Files.readAllBytes( this.logFilePath ) );
	        } 
	        catch (Exception e) 
	        {
	        	responseMessage.error = true;
	        	responseMessage.errorMsg = e.getMessage();
	        }
		}
		
		responseMessage.payloadSerialized = responseMessageGetLog.toJson();

		return responseMessage;
	}
	
	private ResponseMessage processRequestAdminSetLogLevel(RequestMessageSetLogLevel requestMessage)
	{
		serverConfig.logLevel = requestMessage.logLevel;
		
		File fileServerCredentials = Paths.get(homeDir, FOLDER_NAME_DATA, ServerConstants.SERVER_CONFIG_FILE).toFile();
		this.updateServerConfig(fileServerCredentials, false);
		
		return new ResponseMessage();
	}
	
	private ResponseMessage processRequestActivateUser(
			long threadId, 
			RequestMessageActivateUser requestMessage)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.error = true;
		
		UserServer user = this.readUser(requestMessage.userId);
		
		if (user == null)
		{
			return null;
		}
		
		if (!requestMessage.activationCode.equals(user.activationCode))
		{
			return null;
		}
		
		if (user.active)
		{
			responseMessage.errorMsg = 
					SternResources.ServerErrorBenutzerBereitsAktiviert(true, requestMessage.userId);
			return responseMessage;
		}
				
		user.activationCode = "";
		user.active = true;
		user.userPublicKey = requestMessage.userPublicKey;
		
		this.updateUser(user);
		
		responseMessage.error = false;
		return responseMessage;
	}
	
	private void updateUser(UserServer user)
	{
		synchronized(this.users)
		{
			File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USERS, user.userId).toFile();
			
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileUser.getAbsoluteFile())))
			{
				bw.write(user.toJson());
			} catch (IOException e) {

				e.printStackTrace();
				
			}
			
			user.userPublicKeyObject = CryptoLib.decodePublicKeyFromBase64(user.userPublicKey);
			
			if (this.users.containsKey(user.userId))
				this.users.replace(user.userId, user);
			else
				this.users.put(user.userId, user);
			
			if (user.userId.equals(ServerConstants.ADMIN_USER))
				this.adminCreated = true;
		}
	}
	
	private UserServer readUser(String userId)
	{
		synchronized(this.users)
		{
			if (this.users.containsKey(userId))
				return this.users.get(userId);

			UserServer user = null;
			
			File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USERS, userId).toFile();
			
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
	
	private void deleteUser(String userId)
	{
		File fileUser = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_USERS, userId).toFile();
		
		try
		{
			fileUser.delete();
		}
		catch (Exception e) {
	
			e.printStackTrace();
			
		}

		this.users.remove(userId);		
	}
	
	private Game readGame(String gameId)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAMES, gameId).toFile();
		
		Game game = Utils.getGameFromFile(file);
		
		if (game != null)
		{
			game.migrate();
			game.updateSaveBuild();
		}
		
		return game;
	}
	
	private String updateGame(Game game, boolean create)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAMES, game.getName()).toFile();
		
		if (create && file.exists())
		{
			int count = 1;
			
			do
			{
				file = Paths.get(
						homeDir, 
						FOLDER_NAME_DATA, 
						FOLDER_NAME_GAMES, 
						game.getName() + count).toFile();
				
				count++;
			} while (file.exists());
			
			game.setName(file.getName());
		}
		
		game.setDateUpdate();
		Utils.writeGameToFile(game, file);
		
		this.games.put(game.getName(), game.getGameInfo());
		
		return game.getName();
	}
	
	private void deleteGame(Game game)
	{
		File file = Paths.get(homeDir, FOLDER_NAME_DATA, FOLDER_NAME_GAMES, game.getName()).toFile();
		
		if (file.exists())
			file.delete();
		
		this.games.remove(game.getName());
		
		for (UserServer user: this.users.values())
		{
			user.games.remove(game.getName());
		}
	}
	
	private boolean isUserIdAllowed(String userId)
	{
		boolean userIdAllowed = true;
		
		if (userId.length() >= Constants.PLAYER_NAME_LENGTH_MIN &&
			userId.length() <= Constants.PLAYER_NAME_LENGTH_MAX &&
			!userId.toLowerCase().equals(ServerConstants.ADMIN_USER.toLowerCase()) &&
			Pattern.matches(Constants.PLAYER_NAME_REGEX_PATTERN, userId)
			)
		{
			userIdAllowed = !this.userExists(userId);
		}
		else
		{
			userIdAllowed = false;
		}
				
		return userIdAllowed;
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
	
	private String getKeyInput()
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

	private ResponseMessage processRequestPostNewGame(String userId, RequestMessage requestMessage)
	{
		ResponseMessage responseMessage = new ResponseMessage();
		
		synchronized(this.users)
		{
			Gson gson = new Gson();
			Game game = gson.fromJson(requestMessage.payloadSerialized, Game.class);
			
			if (this.games.containsKey(game.getName()))
			{
				responseMessage.error = true;
				responseMessage.errorMsg = 
						SternResources.ServerErrorSpielExistiert(true);
				
				return responseMessage;
			}
			
			for (Player player: game.getPlayers())
			{
				UserServer user = this.users.get(player.getName());
				
				if (user == null)
				{
					responseMessage.error = true;
					responseMessage.errorMsg = 
							SternResources.ServerErrorAdminUserExistiertNicht(true, player.getName());
					
					return responseMessage;
				}
				else
					user.games.add(game.getName());
			}
			
			String gameNameNew = this.updateGame(game, true);
			
			responseMessage.payloadSerialized = gameNameNew;
		}
			
		return responseMessage;
	}
	
	private ResponseMessage processRequestPostMoves(String userId, RequestMessagePostMoves requestMessage )
	{
		synchronized(this.getLockObject(requestMessage.gameId))
		{
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.payloadSerialized = PostMovesResult.ERROR.toString();
			
			Game game = this.readGame(requestMessage.gameId);
			
			boolean allPlayersHaveEnteredMoves = false;
			
			if (game == null)
			{
				responseMessage.error = true;
				responseMessage.errorMsg = 
						SternResources.ServerErrorSpielExistiertNicht(true, requestMessage.gameId);
			}
			else
			{
				if (game.isFinalized())
				{
					responseMessage.error = true;
					responseMessage.errorMsg = 
							SternResources.ServerGamesAbgeschlossen(true);
				}
				else
				{
					int playerIndex = game.importMovesFromEmail(requestMessage.moves);
					
					if (playerIndex >= 0)
					{
						allPlayersHaveEnteredMoves = game.startEvaluationServer();
						
						this.updateGame(game, false);
						
						responseMessage.payloadSerialized =
								allPlayersHaveEnteredMoves ?
										PostMovesResult.EVALUATION_AVAILABLE.toString() :
										PostMovesResult.WAIT_FOR_EVAULATION.toString();
								
					}
					else
					{
						responseMessage.error = true;
						responseMessage.errorMsg = 
								SternResources.ServerErrorJahrVorbei(true);
					}
				}
			}
			
			return responseMessage;
		}
	}

	private ResponseMessage processRequestGetGamesAndUsers(String userId)
	{
		synchronized(this.users)
		{
			ResponseMessageGamesAndUsers responseMessageGamesAndUsers = new ResponseMessageGamesAndUsers();
			
			responseMessageGamesAndUsers.users = new Hashtable<String, ResponseMessageGamesAndUsers.UserInfo>();
			responseMessageGamesAndUsers.gamesWaitingForEnterMoves = new ArrayList<GameInfo>();
			responseMessageGamesAndUsers.gamesWaitingForOtherPlayers = new ArrayList<GameInfo>();
			responseMessageGamesAndUsers.gamesFinalized = new ArrayList<GameInfo>();
			responseMessageGamesAndUsers.gamesGameHost = new ArrayList<GameInfo>();
			
			responseMessageGamesAndUsers.emailGameHost = SternServer.serverConfig.adminEmail;
			
			for (UserServer user: this.users.values())
				if (!user.userId.equals(ServerConstants.ADMIN_USER) && user.active)
					responseMessageGamesAndUsers.users.put(user.userId, responseMessageGamesAndUsers.new UserInfo(user.email));		
			
			UserServer user = this.users.get(userId);
			
			for (String gameId: user.games)
			{
				GameInfo spielInfo = this.games.get(gameId);
				
				if (spielInfo.finalized)
					responseMessageGamesAndUsers.gamesFinalized.add(spielInfo);
				else
				{
					if (spielInfo.moveEnteringFinalized.contains(userId))
						responseMessageGamesAndUsers.gamesWaitingForOtherPlayers.add(spielInfo);
					else
						responseMessageGamesAndUsers.gamesWaitingForEnterMoves.add(spielInfo);
				}
				
				if (spielInfo.players[0].getName().equals(userId))
					responseMessageGamesAndUsers.gamesGameHost.add(spielInfo);
			}
			
			ResponseMessage responseMessage = new ResponseMessage(); 
			
			responseMessage.payloadSerialized = responseMessageGamesAndUsers.toJson();
		
			return responseMessage;
		}
	}
	
	private ResponseMessage processRequestGetStatus(
			String userId,
			RequestMessageGetStatus requestMessage) 
	{
		synchronized(this.users)
		{
			ResponseMessageGetStatus responseMessageGetStatus = new ResponseMessageGetStatus();
			
			boolean currentGameProvided = requestMessage.currentGameId != null && requestMessage.currentGameId.length() > 0;
			
			UserServer user = this.users.get(userId);
			
			for (String gameId: user.games)
			{
				GameInfo spielInfo = this.games.get(gameId);
				
				if (currentGameProvided &&
					requestMessage.currentGameId.equals(gameId))
				{
					responseMessageGetStatus.currentGameNextYear = spielInfo.year > requestMessage.currentGameYear;
				}
				
				if (!spielInfo.finalized && 
						 !spielInfo.moveEnteringFinalized.contains(userId))
				{
					responseMessageGetStatus.gamesWaitingForInput = true;
				}
				
				if (responseMessageGetStatus.currentGameNextYear && responseMessageGetStatus.gamesWaitingForInput)
					break;
			}
			
			ResponseMessage responseMessage = new ResponseMessage(); 
			
			responseMessage.payloadSerialized = responseMessageGetStatus.toJson();
		
			return responseMessage;
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
	
	private Ciphers getCiphers(String sessionId)
	{
		if (sessionId == null || 
			sessionId.equals(CryptoLib.NULL_UUID))
		{
			return null;
		}
		
		synchronized (this.ciphersPerSession)
		{
			Ciphers ciphers = this.ciphersPerSession.get(sessionId);
			
			if (ciphers == null)
				return null;
			
			if (!ciphers.sessionId.equals(sessionId))
				return null;
			
			long timeNow = System.currentTimeMillis();
			
			if (timeNow - ciphers.lastUsed > CryptoLib.CIPHERS_MAX_INACTIVITY_MILLISECONDS)
			{
				return null;
			}				
			
			if (timeNow - ciphers.created > CryptoLib.CIPHERS_MAX_VALIDITY_MILLISECONDS)
			{
				return null;
			}
			
			ciphers.lastUsed = timeNow;
			return ciphers;
		}
	}
	
	private void setCiphers(String sessionId, Ciphers ciphers)
	{
		if (sessionId == null || 
			sessionId.equals(CryptoLib.NULL_UUID) ||
			ciphers == null)
		{
			return;
		}
		
		synchronized (this.ciphersPerSession)
		{
			ciphers.lastUsed = System.currentTimeMillis();
			
			if (ciphers.created == 0)
				ciphers.created = ciphers.lastUsed;
			
			this.ciphersPerSession.put(sessionId, ciphers);
		}
	}
}
