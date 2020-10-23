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

package commonServer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoLib 
{
	private static final String ALGORITHM_RSA = "RSA";
	private static final String STRING_ENCODING = "UTF-8"; 
	private static final int ENCRYPT_CHUNK_LENGTH = 50;
	private static final int KEY_SIZE = 512;
	private static final int CHUNK_LENGTH_BYTE_SIZE = 1;
	
	private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	
	private static Hashtable<Character,Integer> codeIndices;
	
	public static final String NULL_UUID = "00000000-0000-0000-0000-000000000000";
	public static final long CIPHERS_MAX_INACTIVITY_MILLISECONDS = 2 * 60 * 1000;
	public static final long CIPHERS_MAX_VALIDITY_MILLISECONDS = 30 * 60 * 1000;
	
	static
	{
		codeIndices = new Hashtable<Character,Integer>(CODES.length());
		
		for (int i = 0; i < CODES.length(); i++)
			codeIndices.put(CODES.charAt(i), i);
		
		// Einmal zu Beginn aufrufen, dann gehe alle spaeteren Verschluesselungen schneller
		try {
			Cipher.getInstance(ALGORITHM_RSA);
		} catch (Exception e) {}
	}
	
	public static KeyPair getNewKeyPair()
	{
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		keyGen.initialize(KEY_SIZE);
		return keyGen.generateKeyPair();
	}

	private static byte[] encryptRsa(String text, PublicKey key)
	{
		ArrayList<byte[]> chunkBytesEncrypted = new ArrayList<byte[]>();

		int pos = 0;
		int encryptedBytesTotalLength = 0;

		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] textBytes = text.getBytes(STRING_ENCODING);

			// Split zipped text into chunks of a maximum length. This is because
			// the RSA algorithm can only zip 53 bytes.
			while (pos < textBytes.length)
			{
				byte[] chunkBytes = null;

				if (textBytes.length - pos > ENCRYPT_CHUNK_LENGTH)
				{
					chunkBytes = new byte[ENCRYPT_CHUNK_LENGTH];
					System.arraycopy(textBytes, pos, chunkBytes, 0, ENCRYPT_CHUNK_LENGTH);
				}
				else
				{
					chunkBytes = new byte[textBytes.length - pos];
					System.arraycopy(textBytes, pos, chunkBytes, 0, textBytes.length - pos);
				}

				pos += chunkBytes.length;

				chunkBytesEncrypted.add(cipher.doFinal(chunkBytes));
				encryptedBytesTotalLength += 
						chunkBytesEncrypted.get(chunkBytesEncrypted.size() - 1).length 
						+ CHUNK_LENGTH_BYTE_SIZE;
			}
		} catch (Exception e) {
			return null;
		}
		
		byte[] encryptedBytes = new byte[encryptedBytesTotalLength];
		pos = 0;
		
		for (byte[] chunkBytesEncryptedSingle: chunkBytesEncrypted)
		{
			encryptedBytes[pos] = (byte)chunkBytesEncryptedSingle.length;
			
			System.arraycopy(
					chunkBytesEncryptedSingle, 
					0, 
					encryptedBytes, 
					pos + CHUNK_LENGTH_BYTE_SIZE, 
					chunkBytesEncryptedSingle.length);
			
			pos += (chunkBytesEncryptedSingle.length + CHUNK_LENGTH_BYTE_SIZE);
		}
		
		return encryptedBytes;
	}
	
	private static String decryptRsa(byte[] text, PrivateKey key) throws Exception 
	{
		ArrayList<byte[]> textBytes2 = new ArrayList<byte[]>(); 

		final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(Cipher.DECRYPT_MODE, key);

		int pos = 0;
		int textBytes2TotalLength = 0;

		while (pos < text.length)
		{	      
			int chunkLength = (int)text[pos];

			byte[] chunkBytesEncrypted = new byte[chunkLength];

			System.arraycopy(
					text, 
					pos + CHUNK_LENGTH_BYTE_SIZE, 
					chunkBytesEncrypted, 
					0, 
					chunkLength);

			pos+= (chunkLength + CHUNK_LENGTH_BYTE_SIZE);

			textBytes2.add(cipher.doFinal(chunkBytesEncrypted));
			textBytes2TotalLength += textBytes2.get(textBytes2.size()-1).length;
		}
		
		byte[] textBytes = new byte[textBytes2TotalLength];
		pos = 0;
		
		for (byte[] textBytesSingle: textBytes2)
		{
			System.arraycopy(
					textBytesSingle, 
					0, 
					textBytes, 
					pos, 
					textBytesSingle.length);
			
			pos += textBytesSingle.length;
		}

		return new String(textBytes, STRING_ENCODING);
	}
	
	private static byte[] compress(byte[] content){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(content);
			gzipOutputStream.close();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	private static byte[] decompress(byte[] contentBytes)
	{
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(contentBytes);
			GZIPInputStream gzipper = new GZIPInputStream(bin);

			int len;
			while ((len = gzipper.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			gzipper.close();
			out.close();
		}
		catch (Exception x)
		{
			return null;
		}
		return out.toByteArray();
	}

	private static byte[] convertIntToByteArray(int a)
	{
		return new byte[] {
		        (byte) ((a >> 24) & 0xFF),
		        (byte) ((a >> 16) & 0xFF),   
		        (byte) ((a >> 8) & 0xFF),   
		        (byte) (a & 0xFF)
		    };
	}
	
	private static int convertByteArrayToInt(byte[] b)
	{
		return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24; 
	}
	
	public static void sendStringRsaEncrypted(
			OutputStream out,
			String text, 
			PublicKey key) throws Exception
	{
		// System.out.println("Sending string: " + text);
		
		byte[] bytesStringEncrypted = CryptoLib.encryptRsa(text, key);
	    
		byte[] byteStringLength = CryptoLib.convertIntToByteArray(bytesStringEncrypted.length);

		out.write(byteStringLength);
		out.write(bytesStringEncrypted);
	}
	
	public static String receiveStringRsaEncrypted(
			DataInputStream in,
			PrivateKey key) throws Exception
	{
		byte[] lengthBytes = new byte[4];
	    in.readFully(lengthBytes);
	    
	    int length = CryptoLib.convertByteArrayToInt(lengthBytes);
	    
	    byte[] bytes = new byte[length];
	    in.readFully(bytes);
	    
	    return decryptRsa(bytes, key);
	}
	
	public static void sendStringAesEncrypted(
			OutputStream out,
			String text,
			Cipher encryptCipher) throws Exception
	{
		// System.out.println("Sending string (AES): " + text);
		
        byte[] ciphertext = encryptCipher.doFinal(
        		compress(text.getBytes(STRING_ENCODING)));
		
		byte[] byteCiphertextLength = CryptoLib.convertIntToByteArray(ciphertext.length);

		out.write(byteCiphertextLength);
		out.write(ciphertext);
	}
	
	public static String receiveStringAesEncrypted(
			DataInputStream in,
			Cipher decryptCipher) throws Exception
	{
		byte[] byteCiphertextLength = new byte[4];
	    in.readFully(byteCiphertextLength);
	    
	    int length = CryptoLib.convertByteArrayToInt(byteCiphertextLength);
	    
	    byte[] ciphertext = new byte[length];
	    in.readFully(ciphertext);
	    
	    byte[] recovered = decompress(decryptCipher.doFinal(ciphertext));
	    
	    return new String(recovered, STRING_ENCODING);
	}
	
	private static void sendByteArray(
			OutputStream out,
			byte[] bytesString) throws Exception
	{
		byte[] byteStringLength = CryptoLib.convertIntToByteArray(bytesString.length);

		out.write(byteStringLength);
		out.write(bytesString);
	}
	
	private static byte[] receiveByteArray(
			DataInputStream in) throws Exception
	{
		byte[] lengthBytes = new byte[4];
	    in.readFully(lengthBytes);
	    
	    int length = CryptoLib.convertByteArrayToInt(lengthBytes);
	    
	    byte[] bytes = new byte[length];
	    in.readFully(bytes);
	    
	    return bytes;
	}
	
	public static String encodePrivateKeyToBase64(PrivateKey privateKey)
	{
		if (privateKey == null)
			return null;
		
		return base64Encode(privateKey.getEncoded());
	}
	
	public static PrivateKey decodePrivateKeyFromBase64(String base64String)
	{
		if (base64String == null)
			return null;
		
		byte[] keyBytes = base64Decode(base64String);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

		PrivateKey privateKey = null;

		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
			privateKey = kf.generatePrivate(spec);
		} catch (Exception e) {
		}

		return privateKey;
	}
	
	public static String encodePublicKeyToBase64(PublicKey publicKey)
	{
		if (publicKey == null)
			return null;
		
		return base64Encode(publicKey.getEncoded());
	}
	
	public static PublicKey decodePublicKeyFromBase64(String base64String)
	{
		if (base64String == null)
			return null;
		
		byte[] keyBytes = base64Decode(base64String);

		X509EncodedKeySpec spec =
				new X509EncodedKeySpec(keyBytes);

		PublicKey publicKey = null;

		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
			publicKey = kf.generatePublic(spec);
		} catch (Exception e) {
		}

		return publicKey;
	}
	
	private static String base64Encode(byte[] in)
	{
        StringBuilder out = new StringBuilder((in.length * 4) / 3);
        int b;
        for (int i = 0; i < in.length; i += 3)  {
            b = (in[i] & 0xFC) >> 2;
            out.append(CODES.charAt(b));
            b = (in[i] & 0x03) << 4;
            if (i + 1 < in.length)      {
                b |= (in[i + 1] & 0xF0) >> 4;
                out.append(CODES.charAt(b));
                b = (in[i + 1] & 0x0F) << 2;
                if (i + 2 < in.length)  {
                    b |= (in[i + 2] & 0xC0) >> 6;
                    out.append(CODES.charAt(b));
                    b = in[i + 2] & 0x3F;
                    out.append(CODES.charAt(b));
                } else  {
                    out.append(CODES.charAt(b));
                    out.append('=');
                }
            } else      {
                out.append(CODES.charAt(b));
                out.append("==");
            }
        }

        return out.toString();
    }
	
	private static byte[] base64Decode(String input)
	{
        if (input.length() % 4 != 0)    {
            throw new IllegalArgumentException("Invalid base64 input");
        }
        byte decoded[] = new byte[((input.length() * 3) / 4) - (input.indexOf('=') > 0 ? (input.length() - input.indexOf('=')) : 0)];
        char[] inChars = input.toCharArray();
        int j = 0;
        int b[] = new int[4];
        for (int i = 0; i < inChars.length; i += 4)
        {
            b[0] = codeIndices.get(inChars[i]);
            b[1] = codeIndices.get(inChars[i+1]);
            b[2] = codeIndices.get(inChars[i+2]);
            b[3] = codeIndices.get(inChars[i+3]);
            
            decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
            if (b[2] < 64)      {
                decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
                if (b[3] < 64)  {
                    decoded[j++] = (byte) ((b[2] << 6) | b[3]);
                }
            }
        }

        return decoded;
    }

	public static Ciphers diffieHellmanKeyAgreementClient (
			DataInputStream in, 
			OutputStream out) throws Exception
	{
        KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
        clientKpairGen.initialize(2048);
        KeyPair clientKpair = clientKpairGen.generateKeyPair();
        
        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
        clientKeyAgree.init(clientKpair.getPrivate());
        
        byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
        
        sendByteArray(out, clientPubKeyEnc);
        
        byte[] serverPubKeyEnc = receiveByteArray(in);
        
        KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
        PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
        clientKeyAgree.doPhase(serverPubKey, true);
                
        byte[] clientSharedSecret = clientKeyAgree.generateSecret();

        int clientLen = clientSharedSecret.length;
        
        sendByteArray(out, convertIntToByteArray(clientLen));
        
        byte[] serverEncodedParams = receiveByteArray(in);
        SecretKeySpec clientAesKey = new SecretKeySpec(clientSharedSecret, 0, 16, "AES");

        AlgorithmParameters clientAesParams = AlgorithmParameters.getInstance("AES");
        clientAesParams.init(serverEncodedParams);
        Cipher clientCipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        clientCipherDecrypt.init(Cipher.DECRYPT_MODE, clientAesKey, clientAesParams);
        
        Cipher clientCipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        clientCipherEncrypt.init(Cipher.ENCRYPT_MODE, clientAesKey);
        
        byte[] clientEncodedParams = clientCipherEncrypt.getParameters().getEncoded();

        sendByteArray(out, clientEncodedParams);
        
        String sessionId = receiveStringAesEncrypted(in, clientCipherDecrypt);
        
        Ciphers ciphers = new Ciphers(clientCipherEncrypt, clientCipherDecrypt, sessionId);
        return ciphers;
	}

	public static Ciphers diffieHellmanKeyAgreementServer(
			DataInputStream in, 
			OutputStream out) throws Exception
	{
		byte[] clientPubKeyEnc = receiveByteArray(in);
        
        KeyFactory serverKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);

        PublicKey clientPubKey = serverKeyFac.generatePublic(x509KeySpec);

        DHParameterSpec dhParamFromclientPubKey = ((DHPublicKey)clientPubKey).getParams();

        KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
        serverKpairGen.initialize(dhParamFromclientPubKey);
        KeyPair serverKpair = serverKpairGen.generateKeyPair();

        KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
        serverKeyAgree.init(serverKpair.getPrivate());
        
        serverKeyAgree.doPhase(clientPubKey, true);

        byte[] serverPubKeyEnc = serverKpair.getPublic().getEncoded();
        
        sendByteArray(out, serverPubKeyEnc);
        
        int clientLen = convertByteArrayToInt(receiveByteArray(in));
        
        byte[] serverSharedSecret = new byte[clientLen];
        
        serverKeyAgree.generateSecret(serverSharedSecret, 0);
        
        SecretKeySpec serverAesKey = new SecretKeySpec(serverSharedSecret, 0, 16, "AES");

        Cipher serverCipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        serverCipherEncrypt.init(Cipher.ENCRYPT_MODE, serverAesKey);
        byte[] encodedParamsServer = serverCipherEncrypt.getParameters().getEncoded();
        sendByteArray(out, encodedParamsServer);
        
        byte[] clientEncodedParams = receiveByteArray(in);
        AlgorithmParameters serverAesParams = AlgorithmParameters.getInstance("AES");
        serverAesParams.init(clientEncodedParams);
        Cipher serverCipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        serverCipherDecrypt.init(Cipher.DECRYPT_MODE, serverAesKey, serverAesParams);
        
        String sessionId = UUID.randomUUID().toString();
        sendStringAesEncrypted(out, sessionId, serverCipherEncrypt);

        Ciphers ciphers = new Ciphers(serverCipherEncrypt, serverCipherDecrypt, sessionId);
        return ciphers;
	}
	
}
