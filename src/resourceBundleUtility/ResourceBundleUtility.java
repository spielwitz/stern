/**	STERN, das Strategiespiel.
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

package resourceBundleUtility;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import commonUi.SpringUtilities;

@SuppressWarnings("serial")
public class ResourceBundleUtility extends Frame implements ActionListener, WindowListener // NO_UCD (unused code)
{
	private Properties props;
	
	private String propertiesFile = "";
	private String outputPackageName = "";
	private String outputClassName = "";
	private String outputClassPath = "";
	
	private TextField tfPropertiesFile;
	private TextField tfOutputPackageName;
	private TextField tfOutputClassName;
	private TextField tfOutputClassPath;
	
	private Button butBrowsePropertiesFile;
	private Button butBrowseOutputClassPath;
	
	private Button butCreate;
	private Button butCancel;
	
	transient private static final String PROPERTIES_FILE_NAME = "ResourceBundleUtilityProperties";
	transient private static final String PROPERTY_NAME_PROPERTIES_FILE = "propertiesFile";
	transient private static final String PROPERTY_NAME_OUTPUT_CLASS_NAME = "outputClassName";
	transient private static final String PROPERTY_NAME_OUTPUT_PACKAGE_NAME = "outputPackageName";
	transient private static final String PROPERTY_NAME_OUTPUT_PATH = "outputClassPath";
	
	private static final String SYMBOL_SEPARATOR = "_";
	private static final String SYMBOL_PREFIX = "£";
	private static final String SYMBOL_PARAMETER_SEPARATOR = "§";
	
	public static void main(String[] args) 
	{
		new ResourceBundleUtility();
	}
	
	public ResourceBundleUtility()
	{
		super("Resource Bundle Utility (c) M. Schweitzer");
		
		// Properties lesen
		this.props = this.getProperties();
		
		this.addWindowListener(this);
		this.setFocusable(true); // Achtung, das Panel hat den KeyListener!
		this.setLayout(new BorderLayout(10, 10));
		
		// -----
		Panel panMain = new Panel(new SpringLayout());
		
		panMain.add(new Label("Properties-Datei"));
		this.tfPropertiesFile = new TextField(this.propertiesFile, 50);
		panMain.add(this.tfPropertiesFile);
		this.butBrowsePropertiesFile = new Button("Suchen...");
		this.butBrowsePropertiesFile.addActionListener(this);
		panMain.add(this.butBrowsePropertiesFile);
		
		panMain.add(new Label("Paket der erzeugten Klasse"));
		this.tfOutputPackageName = new TextField(this.outputPackageName, 50);
		panMain.add(this.tfOutputPackageName);
		panMain.add(new Label(" "));
		
		panMain.add(new Label("Name der erzeugten Klasse"));
		this.tfOutputClassName = new TextField(this.outputClassName, 50);
		panMain.add(this.tfOutputClassName);
		panMain.add(new Label(" "));
		
		panMain.add(new Label("Ausgabepfad"));
		this.tfOutputClassPath = new TextField(this.outputClassPath, 50);
		panMain.add(this.tfOutputClassPath);
		this.butBrowseOutputClassPath = new Button("Suchen...");
		this.butBrowseOutputClassPath.addActionListener(this);
		panMain.add(this.butBrowseOutputClassPath);
		
		SpringUtilities.makeCompactGrid(panMain,
                4, 3, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panMain, BorderLayout.CENTER);
		
		// -----
		Panel panButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		this.butCancel = new Button("Beenden");
		this.butCancel.addActionListener(this);
		panButtons.add(this.butCancel);
		
		this.butCreate = new Button("Anlegen");
		this.butCreate.addActionListener(this);
		panButtons.add(this.butCreate);
		
		this.add(panButtons, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}

	private Properties getProperties()
	{
		Reader reader = null;
		Properties prop = new Properties(); 

		try
		{
		  reader = new FileReader(PROPERTIES_FILE_NAME);

		  prop.load( reader );
		}
		catch ( Exception e )
		{
		}
		finally
		{
		  try { reader.close(); } catch ( Exception e ) { }
		}
		
		// Properties den Feldern zuweisen
		if (prop.containsKey(PROPERTY_NAME_PROPERTIES_FILE))
			this.propertiesFile = prop.getProperty(PROPERTY_NAME_PROPERTIES_FILE);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_PACKAGE_NAME))
			this.outputPackageName = prop.getProperty(PROPERTY_NAME_OUTPUT_PACKAGE_NAME);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_CLASS_NAME))
			this.outputClassName = prop.getProperty(PROPERTY_NAME_OUTPUT_CLASS_NAME);
		
		if (prop.containsKey(PROPERTY_NAME_OUTPUT_PATH))
			this.outputClassPath = prop.getProperty(PROPERTY_NAME_OUTPUT_PATH);
		
		return prop;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.butCancel)
			this.close();
		else if (e.getSource() == this.butBrowsePropertiesFile)
		{
			FileDialog fd = new FileDialog(this, "Spiel laden", FileDialog.LOAD);
			
			if (this.propertiesFile != null)
				fd.setFile(this.propertiesFile);
			
			fd.setVisible(true);
			String filename = fd.getFile();
			
			if (filename != null)
			{
				File f = new File(fd.getDirectory(), fd.getFile());
				this.propertiesFile = f.getAbsolutePath();
				this.tfPropertiesFile.setText(this.propertiesFile);
			}
		}
		else if (e.getSource() == this.butBrowseOutputClassPath)
		{
			JFileChooser chooser = new JFileChooser();
			
			if (this.outputClassPath != null)
				chooser.setCurrentDirectory(new File(this.outputClassPath));
		    chooser.setDialogTitle("choosertitle");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);

		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		    {
		    	this.outputClassPath = chooser.getSelectedFile().getAbsolutePath();
		    	this.tfOutputClassPath.setText(this.outputClassPath);
		    }
		}
		else if (e.getSource() == this.butCreate)
			this.create();
	}
	
	private void create()
	{
		this.outputClassName = this.tfOutputClassName.getText().trim();
		this.outputPackageName = this.tfOutputPackageName.getText().trim();
		this.outputClassPath = this.tfOutputClassPath.getText().trim();
		this.propertiesFile = this.tfPropertiesFile.getText().trim();
		
		if (outputClassName.length() == 0 || outputClassPath.length() == 0 || propertiesFile.length() == 0)
		{
			JOptionPane.showMessageDialog(this,
				    "Bitte alle Textfelder ausfüllen",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Properties-Datei laden
		File f = null;
		
		try
		{
			f = new File(this.propertiesFile);
		}
		catch (Exception x)
		{
			JOptionPane.showMessageDialog(this,
				    "Die Properties-Datei existiert nicht.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String propFileName = f.getName();
		
		int i = propFileName.indexOf(".properties");
		
		if (i < 0)
		{
			JOptionPane.showMessageDialog(this,
				    "Der Name der Properties-Datei muss dem folgenden Schema entsprechen:\n[Name]_[Sprache]_[Land].properties\nz.B. MyApp_de_DE.properties",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		propFileName = propFileName.substring(0, i);
		
		List<String> list = new ArrayList<>();
		
		String[] parts = propFileName.split("_");
		
		if (parts.length != 3)
		{
			JOptionPane.showMessageDialog(this,
				    "Der Name der Properties-Datei muss dem folgenden Schema entsprechen:\n[Name]_[Sprache]_[Land].properties\nz.B. MyApp_de_DE.properties",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String resourceBundleName = parts[0];
		String defaultLanguage = parts[1];
		String defaultCountry = parts[2];

		try (Stream<String> stream = Files.lines(Paths.get(this.propertiesFile))) {

			list = stream
					.filter(line -> !line.startsWith("#") && line.length() > 0)
					//.map(String::toUpperCase)
					.collect(Collectors.toList());

		} catch (IOException e) 
		{
			JOptionPane.showMessageDialog(this,
				    "Die Properties-Datei kann nicht gelesen werden.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Java-Datei aufbauen
		Hashtable<String,String> symbolDict = new Hashtable<String,String>();
		Hashtable<String,String> textDict = new Hashtable<String,String>();
		ArrayList<String> symbolList = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder();
		
		if (this.outputPackageName.length() > 0)
			sb.append("package "+this.outputPackageName+";\n\n");
		sb.append("import java.util.Hashtable;\n");
		sb.append("import java.text.MessageFormat;\n");
		sb.append("import java.util.Locale;\n");
		sb.append("import java.util.ResourceBundle;\n\n");
		
		sb.append("/**\n");
		sb.append("   * Diese Klasse wurde mit dem Resource Bundle Utility aus der Ressourcen-Datei\n");
		sb.append("   *\n");
		sb.append("   *   "+propFileName+"\n");
		sb.append("   *\n");
		sb.append("   * erzeugt. Die Ressourcen-Datei wird mit dem Eclipse-Plugin ResourceBundle Editor gepflegt.\n");
		sb.append("   */\n");
		sb.append("public class "+this.outputClassName+"\n{\n");
				
		sb.append("\tprivate static Hashtable<String,String> symbolDict;;\n");
		sb.append("\tprivate static String languageCode;\n");
		sb.append("\tprivate static ResourceBundle messages;\n\n");
		sb.append("\tstatic {\n");
		sb.append("\t\tsetLocale(\""+defaultLanguage+"-"+defaultCountry+"\");\n");
		sb.append("\t\tsymbolDict = new Hashtable<String,String>();\n");
		sb.append("\t\tfillSymbolDict();\n");
		sb.append("\t}\n\n");
		sb.append("\tpublic static void setLocale(String newLanguageCode){\n");
		sb.append("\t\tlanguageCode = newLanguageCode;\n");
		sb.append("\t\tString[] language = languageCode.split(\"-\");\n");
		sb.append("\t\tLocale currentLocale = new Locale(language[0], language[1]);\n");
		sb.append("\t\tmessages = ResourceBundle.getBundle(\""+resourceBundleName+"\", currentLocale);\n");
		sb.append("\t}\n\n");
		sb.append("\tpublic static String getLocale(){\n");
		sb.append("\t\treturn languageCode;\n");
		sb.append("\t}\n");
		
		for (String line: list)
		{
			int posEquals = line.indexOf('=');
			if (posEquals < 0)
				continue;
			
			String key = line.substring(0, posEquals).trim();
			String text = line.substring(posEquals + 1).trim();
						
			// Dictionary mit Symbol als Schlüssel aufbauen
			int symbolSep = key.indexOf(SYMBOL_SEPARATOR);
			
			if (symbolSep < 0)
			{
				JOptionPane.showMessageDialog(this,
					    "Das Textelement " + key + " enthält keinen Symbolschlüssel,\n"
					    		+ "der mit "+SYMBOL_SEPARATOR+" vom Schlüssel getrennt ist.",
					    "Fehler",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			//String keyReal = key.substring(0, symbolSep);
			String symbol = key.substring(symbolSep+1, key.length()).trim();
			
			if (symbol.length() == 0)
			{
				JOptionPane.showMessageDialog(this,
					    "Das Textelement " + key + " enthält keinen Symbolschlüssel,\n"
					    		+ "der mit "+SYMBOL_SEPARATOR+" vom Schlüssel getrennt ist.",
					    "Fehler",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			if (symbolDict.containsKey(symbol))
			{
				JOptionPane.showMessageDialog(this,
					    "Das Textelement " + key + " verwendet den Symbolschlüssel\n"
					    		+ symbol +", der mit bereits vergeben ist.",
					    "Fehler",
					    JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			textDict.put(key, text);
			symbolDict.put(symbol, key);
			symbolList.add(symbol);
		}
		
		// Dictionary anlegen
		Collections.sort(symbolList);
		
		sb.append("\n\tprivate static void fillSymbolDict() {\n");
		if (symbolList.size() > 0)
			sb.append("\t\t// Hoechstes vergebenes Symbol: " + symbolList.get(symbolList.size() - 1) + "\n");
		
		for (String symbol: symbolList)
		{
			sb.append("\t\tsymbolDict.put(\""+symbol+"\",\""+symbolDict.get(symbol)+"\");\n");
		}
		sb.append("\t}\n");
		
		// Hilfsmethode
		createConvertSymbolStringMethod(sb);
		
		// Methoden anlegen
		
		for (String symbol: symbolList)
		{
			String key = symbolDict.get(symbol);
			String text = textDict.get(key);
			
			String keyShort = key.substring(0, key.indexOf(SYMBOL_SEPARATOR));
			int numArgs = this.getNumArguments(text);
			
			sb.append("\n\t/**\n");
			sb.append("\t   * "+text+" ["+symbol+"]\n");
			sb.append("\t   */\n");
			sb.append("\tpublic static String "+keyShort+this.getArgs(numArgs)+" {\n");
			sb.append("\t\treturn symbol ? "+this.getSymbolMethode(symbol, numArgs)+":"+this.getLangMethode(key, numArgs)+";\n");
			sb.append("\t}\n");			
		}
		
		sb.append("}");

		// Ausgabedatei anlegen
		Path path = Paths.get(this.outputClassPath, this.outputClassName + ".java");
		 
		//Use try-with-resource to get auto-closeable writer instance
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
		    writer.write(sb.toString());
		}
		catch (Exception x)
		{
			JOptionPane.showMessageDialog(this,
				    "Die Datei\n"+path.getFileName()+"\nkonnte nicht geschrieben werden.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JOptionPane.showMessageDialog(this,
			    "Hat funktioniert!",
			    "Erfolg",
			    JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		this.close();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	private void setProperties()
	{
		this.props.setProperty(PROPERTY_NAME_OUTPUT_PACKAGE_NAME, this.tfOutputPackageName.getText());
		this.props.setProperty(PROPERTY_NAME_OUTPUT_CLASS_NAME, this.tfOutputClassName.getText());
		this.props.setProperty(PROPERTY_NAME_OUTPUT_PATH, this.tfOutputClassPath.getText());
		this.props.setProperty(PROPERTY_NAME_PROPERTIES_FILE, this.tfPropertiesFile.getText());
		
		Writer writer = null;

		try
		{
		  writer = new FileWriter(PROPERTIES_FILE_NAME);

		  props.store( writer, "Resource Bunde Utlity" );
		}
		catch ( IOException e )
		{
		  e.printStackTrace();
		}
		finally
		{
		  try { writer.close(); } catch ( Exception e ) { }
		}

	}
	private void close()
	{
		this.setProperties();
		System.exit(0);
	}
	
	private int getNumArguments(String text)
	{
		int count = 0;
		
		do
		{
			if (text.indexOf("{"+count+"}") < 0)
				break;
			
			count++;
			
		} while (true);
		
		return count;
	}
	
	private String getArgs(int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("(boolean symbol");
		
		for (int i = 0; i < numArgs; i++)
		{
			sb.append(", String arg"+i);
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	private String getSymbolMethode(String symbol, int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\"");
		sb.append(SYMBOL_PREFIX);
		sb.append(symbol);
		
		if (numArgs > 0)
		{
			for (int i = 0; i < numArgs; i++)
			{
				if (i > 0)
					sb.append("+\"");
				
				sb.append(SYMBOL_PARAMETER_SEPARATOR);
				sb.append("\"+arg"+i);
			}
			
			sb.append("+\"");
			sb.append(SYMBOL_PREFIX);
			sb.append("\"");
		}
		else
		{
			sb.append(SYMBOL_PREFIX);
			sb.append("\"");
		}
		
		
		return sb.toString();
	}
	
	private String getLangMethode(String key, int numArgs)
	{
		StringBuilder sb = new StringBuilder();
		
		if (numArgs == 0)
			sb.append("messages.getString(\""+key+"\")");
		else
		{
			sb.append("MessageFormat.format(");
			
			sb.append("messages.getString(\""+key+"\")");
			
			for (int i = 0; i < numArgs; i++)
			{
				sb.append(", arg" + i);
			}
			
			sb.append(")");
		}
			
		
		
		return sb.toString();
	}
	
	private static void createConvertSymbolStringMethod(StringBuilder sb)
	{
		sb.append("\tpublic static String getString(String symbolString){\n");

		sb.append("\t\tStringBuilder sb = new StringBuilder();\n");
		sb.append("\t\tint pos = 0;\n\n");
		sb.append("\t\tdo {\n");
		sb.append("\t\t\tint startPos = symbolString.indexOf(\""+SYMBOL_PREFIX+"\", pos);\n");
		sb.append("\t\t\tif (startPos < 0){\n");
		sb.append("\t\t\t\tsb.append(symbolString.substring(pos, symbolString.length()));\n");
		sb.append("\t\t\t\tbreak;}\n");

		sb.append("\t\t\tsb.append(symbolString.substring(pos, startPos));\n");
		sb.append("\t\t\tint endPos = symbolString.indexOf(\""+SYMBOL_PREFIX+"\", startPos + 1);\n");
		sb.append("\t\t\tString subString = symbolString.substring(startPos + 1, endPos);\n");
		sb.append("\t\t\tObject[] parts = subString.split(\""+SYMBOL_PARAMETER_SEPARATOR+"\");\n");
		sb.append("\t\t\tif (symbolDict.containsKey(parts[0])){\n"); 
		sb.append("\t\t\t\tif (parts.length == 1)\n");
		sb.append("\t\t\t\t\tsb.append(messages.getString(symbolDict.get(parts[0])));\n");
		sb.append("\t\t\t\telse{\n");

		sb.append("\t\t\t\t\tObject[] args = new Object[parts.length - 1];\n");
		sb.append("\t\t\t\t\tfor (int i = 1; i < parts.length; i++)\n");
		sb.append("\t\t\t\t\t\targs[i-1] = parts[i];\n");
		
		sb.append("\t\t\t\t\t\tsb.append(MessageFormat.format(messages.getString(symbolDict.get(parts[0])) ,args));\n");
		sb.append("\t\t\t}}\n");
		sb.append("\t\t\tpos = endPos + 1;\n");
		sb.append("\t\t} while (true);\n");
		sb.append("\t\treturn sb.toString();\n");
		sb.append("\t}\n");
	}
}
