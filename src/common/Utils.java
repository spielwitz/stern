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

package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

public class Utils
{
	private static SecureRandom secRandom;
	
	public static double getRandom()
	{
		if (Utils.secRandom == null)
			Utils.secRandom = new SecureRandom();
		
		return Utils.secRandom.nextDouble();
	}
		
	public static int getRandomInteger(int valueMax)
	{
		return (int) (Utils.getRandom() * valueMax);
	}
	
	public static Object klon(Object obj)
	{
		if (obj == null)
			return null;

		Object retval = null;
		
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(obj);
			os.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			ObjectInputStream is = new ObjectInputStream(in);
			retval = (Object)is.readObject();
			is.close();
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			retval = null;
		}

		return retval;
	}
	public static int round (double arg)
	{
		return (int)Math.round(arg);		
	}
	static int[] getRandomList(int elementsCount)
	{
		return getRandomList(elementsCount, elementsCount);
	}
	static int[] getSequentialList(int elementsCount)
	{
		int retval[] = new int[elementsCount];
		
		for (int t = 0; t < elementsCount; t++)
			retval[t] = t;
		
		return retval;
	}
	private static int[] getRandomList(int valueMax, int elementsCount)
	{
		int retval[] = new int[elementsCount];
		int t, tt;

		BitSet b = new BitSet(valueMax);

		for (t = 0; t < elementsCount; t++)
		{
			tt = Utils.getRandomInteger(valueMax);

			while (b.get(tt) == true)
				tt = (tt + 1) % valueMax;

			retval[t] = tt;
			b.set(tt);
		}

		b = null;

		return retval;
	}
	static int[] sortValues (int values[], boolean descending)
	{
		int sequence[] = new int[values.length];
		int swap;
		boolean ok = false;
		
		for (int t = 0; t < values.length; t++)
			sequence[t] = t;
		
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < values.length - 1; t++)
			{
				if (descending == false && values[sequence[t]] > values[sequence[t+1]])
					ok = false;
				else if (descending == true && values[sequence[t]] < values[sequence[t+1]])
					ok = false;
				
				if (ok == false)
				{
					swap = sequence[t];
					sequence[t] = sequence[t+1];
					sequence[t+1] = swap;
				}
			}
		}
		
		return sequence;
	}
	public static int[] sortList (String values[], boolean descending)
	{
		int sequence[] = new int[values.length];
		int swap;
		boolean ok = false;
		
		for (int t = 0; t < values.length; t++)
			sequence[t] = t;
		
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < values.length - 1; t++)
			{
				if (descending == false && values[sequence[t]].compareTo(values[sequence[t+1]]) > 0)
					ok = false;
				else if (descending == true && values[sequence[t]].compareTo(values[sequence[t+1]]) < 0)
					ok = false;
				
				if (ok == false)
				{
					swap = sequence[t];
					sequence[t] = sequence[t+1];
					sequence[t+1] = swap;
				}
			}
		}
		
		return sequence;
	}
	
	static String convertToString(int value)
	{
		if (value == 0)
			return "";
		else
			return Integer.toString(value);
	}
	static String padString(int value, int stringLength)
	{
		return padString(Integer.toString(value), stringLength);
	}
	static  String padString(String text, int stringLength)
	{
		StringBuilder sb = null;
		String stringTemplate = new String(new char[stringLength]).replace('\0', ' ');
		
		sb = new StringBuilder(stringTemplate);
		sb.append(text);
		
		return sb.substring(sb.length()-stringLength, sb.length());
	}	
	
	static String padStringLeft(String text, int stringLength)
	{
		return (text + getStringWithGivenLength(' ', stringLength)).substring(0, stringLength);
	}
	
	private static String getStringWithGivenLength(char c, int stringLength)
	{
		if (stringLength <= 0)
			return "";
		else
		{
			char[] chars = new char[stringLength];
			Arrays.fill(chars, c);
			return new String(chars);
		}
	}
	
	static long getDateFromOldVega(String dateString)
	{
		int year = Integer.parseInt(dateString.substring(0, 4)); // - 1900, 
		int month = Integer.parseInt(dateString.substring(4, 6)) -1; 
		int day = Integer.parseInt(dateString.substring(6, 8));

		Date date = new GregorianCalendar(year, month, day).getTime();
		
		return date.getTime();
	}
	
	static String convertDateToString(long dateLong)
	{
		Date date = new Date(dateLong);
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM); 
		return df.format(date);
	}
	
	public static String convertMillisecondsToString(long dateLong)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		
		Instant instant = Instant.ofEpochMilli(dateLong);
	    LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	    
	    return date.format(formatter);
	}
	
	public static String getLocalizedDateString()
	{
		Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
	    LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	    
	    String dateBuildStyle = date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	    
		String jahr = dateBuildStyle.substring(0, 4);
		String monat = dateBuildStyle.substring(4, 6);
		String tag = dateBuildStyle.substring(6, 8);
		
		String stunde = dateBuildStyle.substring(8, 10);
		String minute = dateBuildStyle.substring(10, 12);
		String sekunde = dateBuildStyle.substring(12, 14);
		
		return SternResources.ReleaseFormatted2(
				false, tag, monat, jahr, stunde, minute, sekunde);

	}
	
	public static String formatDateString(String unformattedString)
	{
		String jahr = unformattedString.substring(0, 4);
		String monat = unformattedString.substring(4, 6);
		String tag = unformattedString.substring(6, 8);
		
		String stunde = unformattedString.substring(8, 10);
		String minute = unformattedString.substring(10, 12);
		
		return SternResources.ReleaseFormatted(
				false, tag, monat, jahr, stunde, minute);
	}

	
	public static String getMyIPAddress()
	{
		String meineIP = null;
		try {
			InetAddress myAddr = InetAddress.getLocalHost();
			meineIP = myAddr.getHostAddress();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
		
		return meineIP;
	}
	
	public static String convertToBase64(Object obj, String password)
	{
		String outString = "";
		
		Gson serializer = new Gson();
		String json = serializer.toJson(obj);
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream zipout = new GZIPOutputStream(out);
			ObjectOutputStream oos = new ObjectOutputStream(zipout);
			oos.writeObject(json);
			oos.close();
			
			byte[] byteArray = out.toByteArray();
			
			if (password != null)
				byteArray = aesEncrypt(byteArray, password);
			
			outString = Base64.getMimeEncoder().encodeToString(byteArray);
		}
			catch (Exception e) {
		}

		return outString;
	}
		
	public static <T> Object convertFromBase64(String base64, Class<T> expectedClass, String password)
	{
		Object retval = null;
		Gson serializer = new Gson();
		
		try {
			byte[] byteArray = Base64.getMimeDecoder().decode(base64.getBytes());
			
			if (password != null)
				byteArray = aesDecrypt(byteArray, password);
			
			ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
			GZIPInputStream zipin = new GZIPInputStream (in);
			ObjectInputStream iis = new ObjectInputStream(zipin);
			String json = (String) iis.readObject();
			retval = serializer.fromJson(json, expectedClass);
			in.close();
		}
		catch (Exception e) {
			retval = null;
		}

		return retval;
	}
	
	private static byte[] aesEncrypt(byte[] unencryptedBytes, String password) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesGetKey(password));
		byte[] encrypted = cipher.doFinal(unencryptedBytes);
		
		return encrypted;
	}
	
	private static byte[] aesDecrypt(byte[] encryptedBytes, String password) throws Exception
	{
		Cipher cipher2 = Cipher.getInstance("AES");
		cipher2.init(Cipher.DECRYPT_MODE, aesGetKey(password));
		byte[] decrypted = cipher2.doFinal(encryptedBytes);
		
		return decrypted;
	}
	
	private static SecretKeySpec aesGetKey(String password) throws Exception
	{
		byte[] key = password.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); 
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		
		return secretKeySpec;
	}

	public static Game getGameFromFile(File file)
	{
		Game game = null;
		
		try {
			FileInputStream fs = new FileInputStream(file.getPath());
			GZIPInputStream zipin = new GZIPInputStream (fs);
			ObjectInputStream is = new ObjectInputStream(zipin);
			String gameJson = (String)is.readObject();
			is.close();
			
			game = new Gson().fromJson(gameJson, Game.class);
		} catch (Exception e) {
		}		
		
		return game;
	}

	public static String writeGameToFile(Game game, File file)
	{
		String gameJson = new Gson().toJson(game);
		
		String errorText = null;
		
		try {
			FileOutputStream fs = new FileOutputStream(file.getPath());
			GZIPOutputStream zipout = new GZIPOutputStream(fs);
			ObjectOutputStream os = new ObjectOutputStream(zipout);
			os.writeObject(gameJson);
			os.close();
		} catch (Exception e) {
			errorText = e.toString();
		}
		
		return errorText;
	}
	
	public static String getKeyFromValue(Hashtable<String,String> ht, String value)
	{
		String retval = null;
		
		for (String key: ht.keySet())
		{
			String value2 = ht.get(key);
			
			if (value.equals(value2))
			{
				retval = key;
				break;
			}
		}
		
		return retval;
	}
}
