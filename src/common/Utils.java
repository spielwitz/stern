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

import java.awt.geom.Point2D;
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
		
	public static int random(int max)
	{
		if (Utils.secRandom == null)
			Utils.secRandom = new SecureRandom();
		
		return (int) (Utils.secRandom.nextDouble() * max);
	}
	
	static double dist(Point2D.Double p1, Point2D.Double p2)
	{
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
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
			//Deserialisieren des Objekts
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
	static int[] randomList(int num)
	{
		return randomList(num, num);
	}
	static int[] sequentialList(int num)
	{
		int retval[] = new int[num];
		
		for (int t = 0; t < num; t++)
			retval[t] = t;
		
		return retval;
	}
	private static int[] randomList(int maxNum, int anz)
	{
		int retval[] = new int[anz];
		int t, tt;

		BitSet b = new BitSet(maxNum);

		for (t = 0; t < anz; t++)
		{
			tt = Utils.random(maxNum);

			while (b.get(tt) == true)
				tt = (tt + 1) % maxNum;

			retval[t] = tt;
			b.set(tt);
		}

		b = null;

		return retval;
	}
	static int[] listeSortieren (int liste[], boolean absteigend)
	{
		int reihenfolge[] = new int[liste.length];
		int swap;
		boolean ok = false;
		
		// Initiale Verteilung
		for (int t = 0; t < liste.length; t++)
			reihenfolge[t] = t;
		
		// Sortieren
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < liste.length - 1; t++)
			{
				if (absteigend == false && liste[reihenfolge[t]] > liste[reihenfolge[t+1]])
					ok = false;
				else if (absteigend == true && liste[reihenfolge[t]] < liste[reihenfolge[t+1]])
					ok = false;
				
				if (ok == false)
				{
					swap = reihenfolge[t];
					reihenfolge[t] = reihenfolge[t+1];
					reihenfolge[t+1] = swap;
				}
			}
		}
		
		return reihenfolge;
	}
	public static int[] listeSortieren (String liste[], boolean absteigend)
	{
		int reihenfolge[] = new int[liste.length];
		int swap;
		boolean ok = false;
		
		// Initiale Verteilung
		for (int t = 0; t < liste.length; t++)
			reihenfolge[t] = t;
		
		// Sortieren
		while (ok == false)
		{
			ok = true;
			
			for (int t = 0; t < liste.length - 1; t++)
			{
				if (absteigend == false && liste[reihenfolge[t]].compareTo(liste[reihenfolge[t+1]]) > 0)
					ok = false;
				else if (absteigend == true && liste[reihenfolge[t]].compareTo(liste[reihenfolge[t+1]]) < 0)
					ok = false;
				
				if (ok == false)
				{
					swap = reihenfolge[t];
					reihenfolge[t] = reihenfolge[t+1];
					reihenfolge[t+1] = swap;
				}
			}
		}
		
		return reihenfolge;
	}
	
	static String numToString(int num)
	{
		if (num == 0)
			return "";
		else
			return Integer.toString(num);
	}
	static String padString(int num, int length)
	{
		return padString(Integer.toString(num), length);
	}
	static  String padString(String text, int length)
	{
		StringBuilder sb = null;
		String fillUpString = new String(new char[length]).replace('\0', ' ');
		
		sb = new StringBuilder(fillUpString);
		sb.append(text);
		
		return sb.substring(sb.length()-length, sb.length());
	}	
	
	static String padStringLeft(String text, int length)
	{
		return (text + getStringWithGivenLength(' ', length)).substring(0, length);
	}
	
	private static String getStringWithGivenLength(char c, int length)
	{
		if (length <= 0)
			return "";
		else
		{
			char[] chars = new char[length];
			Arrays.fill(chars, c);
			return new String(chars);
		}
	}
	
	static int winkelVektoren(Point2D.Double pt1, Point2D.Double pt2, Point2D.Double pt3)
	{
		// Gibt den Winkel zwischen den Vektoren pt1-pt2 und pt1-pt3 in ganzen Grad zurueck
		double x1 = pt2.x - pt1.x;
		double y1 = pt2.y - pt1.y;
		
		double x2 = pt3.x - pt1.x;
		double y2 = pt3.y - pt1.y;
		
		double w1 = Math.atan2(y1, x1);
		double w2 = Math.atan2(y2, x2);
		
		double w = w2 - w1;
		
		int wGrad = Utils.round(180 * (w / Math.PI)); 
		
		if (wGrad < 0)
			return 360 + wGrad;
		else
			return wGrad;
	}
	
	static long getDateFromOldVega(String dateString)
	{
		int year = Integer.parseInt(dateString.substring(0, 4)); // - 1900, 
		int month = Integer.parseInt(dateString.substring(4, 6)) -1; 
		int day = Integer.parseInt(dateString.substring(6, 8));

		Date date = new GregorianCalendar(year, month, day).getTime();
		
		return date.getTime();
	}
	
	public static String dateToString(long dateLong)
	{
		Date date = new Date(dateLong);
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM); 
		return df.format(date);
	}
	
	public static String millisecondsToString(long dateLong)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		
		Instant instant = Instant.ofEpochMilli(dateLong);
	    LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	    
	    return date.format(formatter);
	}
	
	public static String currentTimeToLocalizedString()
	{
		Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
	    LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	    
	    String dateBuildStyle = date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	    
	    return ReleaseGetter.format2(dateBuildStyle);
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
	
	public static Hashtable<String,String> resolveProgramArgs(String[] args)
	{
		Hashtable<String,String> retval = new Hashtable<String,String>();
		
		for (String arg: args)
		{
			try
			{
				String[] parts = arg.split("=");
				
				retval.put(parts[0].toLowerCase().trim(), parts[1].toLowerCase().trim());
			}
			catch (Exception x)
			{
				System.out.println(
						SternResources.ProgramArgument(false, arg));
			}
		}
		
		return retval;
	}
	
	static Point2D.Double toPoint2D(Point p)
	{
		return new Point2D.Double(p.getX(), p.getY());
	}
	
	public static String objectToBase64(Object obj, String password)
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
		
	public static <T> Object base64ToObject(String base64, Class<T> expectedClass, String password)
	{
		Object retval = null;
		Gson serializer = new Gson();
		
		// Erzeuge Objekt aus der Zeichenkette
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
		// Verschluesseln
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
		// nur die ersten 128 bit nutzen
		key = Arrays.copyOf(key, 16); 
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		
		return secretKeySpec;
	}

	public static Spiel readSpielFromFile(File file)
	{
		Spiel spiel = null;
		
		try {
			FileInputStream fs = new FileInputStream(file.getPath());
			GZIPInputStream zipin = new GZIPInputStream (fs);
			ObjectInputStream is = new ObjectInputStream(zipin);
			String jsonString = (String)is.readObject();
			is.close();
			
			spiel = new Gson().fromJson(jsonString, Spiel.class);
		} catch (Exception e) {
		}		
		
		return spiel;
	}

	public static String writeSpielToFile(Spiel spiel, File file)
	{
		String jsonString = new Gson().toJson(spiel);
		
		String errorText = null;
		
		try {
			FileOutputStream fs = new FileOutputStream(file.getPath());
			GZIPOutputStream zipout = new GZIPOutputStream(fs);
			ObjectOutputStream os = new ObjectOutputStream(zipout);
			os.writeObject(jsonString);
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
	
	static double VektorproduktBetrag(Point2D.Double pt1, Point2D.Double pt2, Point2D.Double pt3)
	{
		// pt1 ist der gemeinsame Startpunkt von zwei Vektoren
		// Vektor a= pt1 -> pt2, Vektor b = pt1 -> pt3
		
		double ax = pt2.x - pt1.x;
		double ay = pt2.y - pt1.y;
		
		double bx = pt3.x - pt1.x;
		double by = pt3.y - pt1.y;
		
		return ax * by - ay * bx;
	}	
}
