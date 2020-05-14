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
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;

@SuppressWarnings("deprecation")
public class RsaCrypt {

	private static final String ALGORITHM = "RSA";
	public static final String STRING_ENCODING = "UTF-8"; 
	private static final int encryptChunkLength = 50;
	
	private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	
	private static Hashtable<Character,Integer> codeIndices;
	
	static
	{
		codeIndices = new Hashtable<Character,Integer>(CODES.length());
		
		for (int i = 0; i < CODES.length(); i++)
			codeIndices.put(new Character(CODES.charAt(i)), new Integer(i));
	}
	
	public static void init()
	{
		// Einmal zu Beginn aufrufen, dann gehe alle spaeteren Verschluesselungen schneller
		try {
			Cipher.getInstance(ALGORITHM);
		} catch (Exception e) {}
	}
	public static KeyPair getNewKeyPair()
	{
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		keyGen.initialize(512);
		return keyGen.generateKeyPair();
	}

	public static byte[] encrypt(String text, PublicKey key)
	{
		byte[] encryptedBytes = null;

		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);

			// Zip the text
			byte[] textBytesZipped = compress(text.getBytes(STRING_ENCODING));

			int pos = 0;

			// Split zipped text into chunks of a maximum length. This is because
			// the RSA algorithm can only zip 53 bytes.
			while (pos < textBytesZipped.length)
			{
				byte[] chunkBytes = null;

				if (textBytesZipped.length - pos > encryptChunkLength)
				{
					chunkBytes = new byte[encryptChunkLength];
					System.arraycopy(textBytesZipped, pos, chunkBytes, 0, encryptChunkLength);
				}
				else
				{
					chunkBytes = new byte[textBytesZipped.length - pos];
					System.arraycopy(textBytesZipped, pos, chunkBytes, 0, textBytesZipped.length - pos);
				}

				pos += chunkBytes.length;

				byte[] chunkBytesEncrypted = cipher.doFinal(chunkBytes);

				if (encryptedBytes == null)
				{
					encryptedBytes = new byte[chunkBytesEncrypted.length + 1];
					encryptedBytes[0] = (byte)chunkBytesEncrypted.length; // Insert the length of the encrypted chunk
					System.arraycopy(chunkBytesEncrypted, 0, encryptedBytes, 1, chunkBytesEncrypted.length);
				}
				else
				{
					byte[] encryptedBytes2 = new byte[encryptedBytes.length + chunkBytesEncrypted.length + 1];
					System.arraycopy(encryptedBytes, 0, encryptedBytes2, 0, encryptedBytes.length);
					encryptedBytes2[encryptedBytes.length] = (byte)chunkBytesEncrypted.length;
					System.arraycopy(chunkBytesEncrypted, 0, encryptedBytes2, encryptedBytes.length+1, chunkBytesEncrypted.length);

					encryptedBytes = encryptedBytes2;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return encryptedBytes;
	}

	public static String decrypt(byte[] text, PrivateKey key) throws Exception 
	{
		byte[] textBytesZipped = null;

		final Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);

		int pos = 0;

		while (pos < text.length)
		{	      
			int chunkLength = (int)text[pos];

			byte[] chunkBytesEncrypted = new byte[chunkLength];

			System.arraycopy(text, pos + 1, chunkBytesEncrypted, 0, chunkLength);

			pos+= (chunkLength + 1);

			byte[] chunkBytes = cipher.doFinal(chunkBytesEncrypted);

			if (textBytesZipped == null)
			{
				textBytesZipped = new byte[chunkBytes.length];
				System.arraycopy(chunkBytes, 0, textBytesZipped, 0, chunkBytes.length);
			}
			else
			{
				byte[] textBytesZipped2 = new byte[textBytesZipped.length + chunkBytes.length];
				System.arraycopy(textBytesZipped, 0, textBytesZipped2, 0, textBytesZipped.length);
				System.arraycopy(chunkBytes, 0, textBytesZipped2, textBytesZipped.length, chunkBytes.length);

				textBytesZipped = textBytesZipped2;
			}
		}

		byte[] textBytes = decompress(textBytesZipped);

		return new String(textBytes, STRING_ENCODING);
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
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			publicKey = kf.generatePublic(spec);
		} catch (Exception e) {
		}

		return publicKey;
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
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			privateKey = kf.generatePrivate(spec);
		} catch (Exception e) {
		}

		return privateKey;
	}
	
	public static byte[] base64Decode(String input)
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
            b[0] = new Integer(codeIndices.get(new Character(inChars[i]))).intValue();
            b[1] = new Integer(codeIndices.get(new Character(inChars[i+1]))).intValue();
            b[2] = new Integer(codeIndices.get(new Character(inChars[i+2]))).intValue();
            b[3] = new Integer(codeIndices.get(new Character(inChars[i+3]))).intValue();
            
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
	
	public static String base64Encode(byte[] in)
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
}
