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

package stern;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.Colors;
import common.Constants;
import common.Spiel;
import common.SpielOptionen;
import common.Spieler;
import common.SternResources;
import common.Utils;
import commonServer.ServerConstants;
import commonUi.ButtonDark;
import commonUi.ComboBoxDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.SpringUtilities;
import commonUi.TextFieldDark;

@SuppressWarnings("serial") 
class SpielparameterJDialog extends JDialog implements ActionListener, IColorChooserCallback
{
	private ArrayList<Spieler> spieler;
	private String emailAdresseSpielleiter;
	private int anzPl;
	private int anzSp;
	private int maxJahre;
	private HashSet<SpielOptionen> optionen;
	private boolean abort;
	
	private SpielparameterDialogModus modus;
	
	private PanelDark[] panSpieler;
	private TextFieldDark[] tfSpieler;
	private CheckBoxDark[] cbSpieler;
	private SpielerFarbenCanvas[] canvasSpielerFarben;
	private Hashtable<SpielOptionen,CheckBoxDark> cbOptionen;
	private ComboBoxDark<String> comboLetztesJahr;
	private ComboBoxDark<String> comboSpieler;
	private ComboBoxDark<String> comboPlaneten;
	private ButtonDark butOk;
	private ButtonDark butAbbruch;
	private ButtonDark butEmailKonf;
	private static Font font;
	private ArrayList<String> emailAdressen;
	
	private final static String MAX_JAHRE_UNENDLICH = SternResources.SpielparameterJDialogUnendlich(false);
	
	SpielparameterJDialog (
			Frame owner,
			String title,
			SpielparameterDialogModus modus,
			Spiel spiel,
			ArrayList<String> emailAdressen)
	{
		super (owner, title, true);
		
		// Font laden
		font = DialogFontHelper.getFont();
		
		this.modus = modus;
		this.emailAdressen = emailAdressen;
		
		this.getInitialValues(spiel);
		
		this.panSpieler = new PanelDark[Constants.ANZAHL_SPIELER_MAX];
		this.tfSpieler = new TextFieldDark[Constants.ANZAHL_SPIELER_MAX];
		this.cbSpieler = new CheckBoxDark[Constants.ANZAHL_SPIELER_MAX];
		this.canvasSpielerFarben = new SpielerFarbenCanvas[Constants.ANZAHL_SPIELER_MAX];
		this.cbOptionen = new Hashtable<SpielOptionen,CheckBoxDark>();		
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				setVisible(false);
				dispose();
			}
		}
		);
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark(new GridLayout(6, 2, 20, 0));
		
		panMain.add(add(this.getSpielerPanel(0)));
		
		PanelDark panPlanetenSub1 = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panPlanetenSub1.add(new LabelDark(SternResources.Spieler(false), font));
		String[] spieler = new String[Constants.ANZAHL_SPIELER_MAX - Constants.ANZAHL_SPIELER_MIN + 1];
		for (int i = Constants.ANZAHL_SPIELER_MIN; i <= Constants.ANZAHL_SPIELER_MAX; i++)
			spieler[i-Constants.ANZAHL_SPIELER_MIN] = Integer.toString(i);
		this.comboSpieler = new ComboBoxDark<String>(spieler, font);
		this.comboSpieler.setSelectedItem(Integer.toString(this.anzSp));
		this.comboSpieler.addActionListener(this);
		panPlanetenSub1.add(this.comboSpieler);
		
		panPlanetenSub1.add(new JSeparator());
		
		panPlanetenSub1.add(new LabelDark(SternResources.Planeten(false), font));
		String[] planeten = new String[Constants.ANZAHL_PLANETEN_MAX - Constants.ANZAHL_SPIELER_MAX + 1];
		for (int i = Constants.ANZAHL_SPIELER_MAX; i <= Constants.ANZAHL_PLANETEN_MAX; i++)
			planeten[i-Constants.ANZAHL_SPIELER_MAX] = Integer.toString(i);
		this.comboPlaneten = new ComboBoxDark<String>(planeten, font);
		this.comboPlaneten.setSelectedItem(Integer.toString(this.anzPl));
		this.comboPlaneten.addActionListener(this);
		panPlanetenSub1.add(this.comboPlaneten);
		
		panMain.add(panPlanetenSub1);
		
		panMain.add(add(this.getSpielerPanel(1)));
		
		PanelDark panBisJahr = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		panBisJahr.add(new LabelDark(SternResources.SpielparameterJDialogSpieleBisJahr(false)+" ", font));
		String[] jahre = { MAX_JAHRE_UNENDLICH, "15", "20", "30", "40", "50", "75", "100", "150", "200" };
		this.comboLetztesJahr = new ComboBoxDark<String>(jahre, font);
		panBisJahr.add(this.comboLetztesJahr);
		panMain.add(panBisJahr);
		
		panMain.add(add(this.getSpielerPanel(2)));

		CheckBoxDark cbSimpel = new CheckBoxDark(SternResources.SpielparameterJDialogSimpelStern(false), true, font);
		this.cbOptionen.put(SpielOptionen.SIMPEL, cbSimpel);
		this.cbOptionen.get(SpielOptionen.SIMPEL).addActionListener(this);
		panMain.add(cbSimpel);
		
		panMain.add(add(this.getSpielerPanel(3)));

		CheckBoxDark cbAutoSave = new CheckBoxDark(SternResources.SpielparameterJDialogAutoSave(false), true, font);
		this.cbOptionen.put(SpielOptionen.AUTO_SAVE, cbAutoSave);
		panMain.add(cbAutoSave);
		
		panMain.add(add(this.getSpielerPanel(4)));
		
		CheckBoxDark cbEmail = new CheckBoxDark(SternResources.SpielparameterJDialogEmailModus(false), false, font);
		this.cbOptionen.put(SpielOptionen.EMAIL, cbEmail);
		panMain.add(cbEmail);
		
		panMain.add(add(this.getSpielerPanel(5)));
				
		PanelDark panEmailKonfButton = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		this.butEmailKonf = new ButtonDark(this, SternResources.SpielparameterJDialogEMailEinstellungen(false), font);
		panEmailKonfButton.add(this.butEmailKonf);
		panMain.add(panEmailKonfButton);
		// ----
		
		panBase.add(panMain, BorderLayout.CENTER);
		
		// ----
		
		PanelDark panButtons = new PanelDark(new FlowLayout(FlowLayout.RIGHT));
		
		this.butAbbruch = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butAbbruch);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		// ---
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panShell, BorderLayout.CENTER);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().setDefaultButton(this.butOk);
		
		this.pack();
		this.setLocationRelativeTo(owner);	
		this.setResizable(false);
		
		this.setInitialControlValues();
		this.setControlsEnabled();
		
	}
	
	@SuppressWarnings("unchecked")
	private void getInitialValues(Spiel spiel)
	{
		if (spiel == null)
		{
			this.spieler = Spiel.getDefaultSpieler();
			this.optionen = Spiel.getDefaultSpielOptionen();
			
			this.emailAdresseSpielleiter = "";
			
			this.anzPl = Constants.DEFAULT_ANZAHL_PLANETEN;
			this.anzSp = Constants.DEFAULT_ANZAHL_SPIELER;
			this.maxJahre = Constants.DEFAULT_MAX_JAHRE;
		}
		else
		{
			this.anzPl = spiel.getAnzPl();
			this.anzSp = spiel.getAnzSp();
			
			this.emailAdresseSpielleiter = spiel.getEmailAdresseSpielleiter();
			
			this.optionen = (HashSet<SpielOptionen>)Utils.klon(spiel.getOptionen());
			
			if (this.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL))
			{
				this.maxJahre = spiel.getMaxJahre();
			}
			else
				this.maxJahre = Constants.DEFAULT_MAX_JAHRE;
			
			this.spieler = new ArrayList<Spieler>(); // >new Spieler[Constants.ANZAHL_SPIELER_MAX];
			ArrayList<Spieler> defaultSpieler = Spiel.getDefaultSpieler();
			
			boolean[] colorIndicesUsed = new boolean[Constants.ANZAHL_SPIELER_MAX  + Colors.SPIELER_FARBEN_OFFSET];
			
			for (int sp = 0; sp < Constants.ANZAHL_SPIELER_MAX; sp++)
			{
				if (sp < this.anzSp)
				{
					colorIndicesUsed[spiel.getSpieler()[sp].getColIndex()] = true;
					spieler.add((Spieler)Utils.klon(spiel.getSpieler()[sp]));
				}
				else
				{
					byte tempColorIndex = defaultSpieler.get(sp).getColIndex();
					
					for (int i = 0; i < Constants.ANZAHL_SPIELER_MAX; i++)
					{
						if (!colorIndicesUsed[tempColorIndex])
						{
							colorIndicesUsed[tempColorIndex] = true;
							spieler.add(new Spieler("", "", tempColorIndex, false, false));
							break;
						}
						tempColorIndex = (byte)((tempColorIndex + 1) % Constants.ANZAHL_SPIELER_MAX);
					}
				}
			}
		}		
	}
	
	private void setInitialControlValues()
	{
		// Alles ausser Spielernamen
		for (SpielOptionen option: this.cbOptionen.keySet())
			this.cbOptionen.get(option).setSelected(this.optionen.contains(option));

		if (this.optionen.contains(SpielOptionen.KEIN_ENDLOSSPIEL))
			this.comboLetztesJahr.setSelectedItem(Integer.toString(this.maxJahre));
		else
			this.comboLetztesJahr.setSelectedItem(MAX_JAHRE_UNENDLICH);
		
	}
	
	private void setControlsEnabled()
	{
		boolean simpel = this.cbOptionen.get(SpielOptionen.SIMPEL).isSelected();
		
		// Checkboxen
		for (SpielOptionen option: this.cbOptionen.keySet())
		{
			if (option == SpielOptionen.AUTO_SAVE)
				this.cbOptionen.get(option).setEnabled(this.modus != SpielparameterDialogModus.EMAIL_SPIEL);
			else if (option == SpielOptionen.SIMPEL)
				this.cbOptionen.get(option).setEnabled(this.modus == SpielparameterDialogModus.NEUES_SPIEL);
			else if (option == SpielOptionen.EMAIL)
				this.cbOptionen.get(option).setEnabled(
						this.modus != SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL &&
						this.modus != SpielparameterDialogModus.EMAIL_SPIEL);			
		}
		
		// Enabled/Disabled?
		this.comboLetztesJahr.setEnabled(
				this.modus != SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL &&
				this.modus != SpielparameterDialogModus.EMAIL_SPIEL);
		this.comboPlaneten.setEnabled(this.modus == SpielparameterDialogModus.NEUES_SPIEL);
		this.comboSpieler.setEnabled(this.modus == SpielparameterDialogModus.NEUES_SPIEL);
		
		for (int sp = 0; sp < Constants.ANZAHL_SPIELER_MAX; sp++)
		{
			if (sp < this.anzSp)
			{
				this.panSpieler[sp].setVisible(true);
				this.canvasSpielerFarben[sp].setEnabled(
						this.modus != SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL &&
						this.modus != SpielparameterDialogModus.EMAIL_SPIEL);
				this.tfSpieler[sp].setEditable(
						this.modus != SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL &&
						this.modus != SpielparameterDialogModus.EMAIL_SPIEL);
			}
			else
				this.panSpieler[sp].setVisible(false);
			
			if (!simpel)
				this.cbSpieler[sp].setSelected(false);
			
			this.cbSpieler[sp].setEnabled(this.modus == SpielparameterDialogModus.NEUES_SPIEL && simpel);
		}
	}
	
	private PanelDark getSpielerPanel(int spIndex)
	{
		this.panSpieler[spIndex] = new PanelDark(new FlowLayout(FlowLayout.LEFT));
		
		canvasSpielerFarben[spIndex] = new SpielerFarbenCanvas(
				this, 
				this,
				spIndex, 
				this.spieler.get(spIndex).getColIndex(),
				font);
		canvasSpielerFarben[spIndex].setPreferredSize(new Dimension(14,14));
		this.panSpieler[spIndex].add(canvasSpielerFarben[spIndex]);
		
		this.tfSpieler[spIndex] = new TextFieldDark(font, 20);
		this.tfSpieler[spIndex].setText(this.spieler.get(spIndex).getName());
		
		this.panSpieler[spIndex].add(this.tfSpieler[spIndex]);
		
		this.cbSpieler[spIndex] = new CheckBoxDark(SternResources.SpielparameterJDialogBot(false), this.spieler.get(spIndex).istComputer(), font);
		
		this.cbSpieler[spIndex].addActionListener(this);
		this.panSpieler[spIndex].add(this.cbSpieler[spIndex]);
		
		return this.panSpieler[spIndex];
	}
	
	public ArrayList<Spieler> getSpieler()
	{
		ArrayList<Spieler> spielerNeu = new ArrayList<Spieler>();
		for (int sp = 0; sp < this.anzSp; sp++)
			spielerNeu.add((Spieler)Utils.klon(this.spieler.get(sp)));
			
		return spielerNeu;
	}

	public int getAnzPl() {
		return anzPl;
	}

	public int getMaxJahre() {
		return maxJahre;
	}
	
	public String getEmailAdresseSpielleiter()
	{
		return this.emailAdresseSpielleiter;
	}

	public HashSet<SpielOptionen> getOptionen() {
		return optionen;
	}
	
	public boolean isAbort() {
		return abort;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.getRootPane())
		{
			this.abort = true;
			this.close();
		}
		else if (e.getSource() == this.cbOptionen.get(SpielOptionen.SIMPEL))
		{
			this.setControlsEnabled();
		}
		else if (e.getSource() == this.comboSpieler)
		{
			this.anzSp = Integer.parseInt((String)this.comboSpieler.getSelectedItem());
			
			// Planetenzahl anpassen
			this.anzPl = Utils.round(this.anzSp * ( 
					(double)Constants.ANZAHL_PLANETEN_MAX / (double)Constants.ANZAHL_SPIELER_MAX));
			
			this.comboPlaneten.setSelectedItem(Integer.toString(this.anzPl));
			
			this.setControlsEnabled();
		}
		else if (e.getSource() == this.comboPlaneten)
		{
			this.anzPl = Integer.parseInt((String)this.comboPlaneten.getSelectedItem());
		}

		else if (e.getSource().getClass() == CheckBoxDark.class)
		{
			for (int i = 0; i < Constants.ANZAHL_SPIELER_MAX; i++)
			{
				if (e.getSource() == this.cbSpieler[i])
				{
					this.tfSpieler[i].setEnabled(!this.cbSpieler[i].isSelected());
					
					if (this.cbSpieler[i].isSelected())
					{
						this.tfSpieler[i].setText(SternResources.SpielparameterJDialogBot(false)+(i+1));
					}
					break;
				}
			}
		}
		else
		{
			JButton button = (JButton)e.getSource();
			
			if (button == this.butAbbruch)
			{
				this.abort = true;
				this.close();
			}
			else if (button == this.butEmailKonf)
			{
				boolean ok = this.getSpielerFromControls(
						this.spieler,
						Integer.parseInt((String)this.comboSpieler.getSelectedItem()));
				
				if (ok)
				{
					EmailSettingsJDialog dlg = new EmailSettingsJDialog(
													this,
													this.emailAdresseSpielleiter,
													this.spieler,
													this.emailAdressen,
													this.modus == SpielparameterDialogModus.ABGESCHLOSSENES_SPIEL ||
													this.modus == SpielparameterDialogModus.EMAIL_SPIEL);
					
					dlg.setVisible(true);
					
					this.emailAdresseSpielleiter = dlg.emailAdresseSpielleiter;
					
					for (int i = 0; i < dlg.spieler.size(); i++)
					{
						this.spieler.get(i).setEmail(dlg.spieler.get(i).istEmail());
						this.spieler.get(i).setEmailAdresse(dlg.spieler.get(i).getEmailAdresse());
					}
				}
			}
			else if (button == this.butOk)
			{
				boolean ok = true;
				
				this.anzSp = Integer.parseInt((String)this.comboSpieler.getSelectedItem());
				this.anzPl = Integer.parseInt((String)this.comboPlaneten.getSelectedItem());
				
				// Spielernamen
				ok = this.getSpielerFromControls(this.spieler, this.anzSp);
				
				// Spieloptionen
				for (SpielOptionen option: this.cbOptionen.keySet())
				{
					if (this.cbOptionen.get(option).isSelected())
						this.optionen.add(option);
					else
						this.optionen.remove(option);
				}
				
				if (this.optionen.contains(SpielOptionen.SIMPEL))
				{
					this.optionen.remove(SpielOptionen.FESTUNGEN);
					this.optionen.remove(SpielOptionen.KOMMANDOZENTRALEN);
					this.optionen.remove(SpielOptionen.KOMMANDOZENTRALEN_UNBEWEGLICH);
				}
				else
				{
					this.optionen.add(SpielOptionen.FESTUNGEN);
					this.optionen.add(SpielOptionen.KOMMANDOZENTRALEN);
				}
				
				// Max. Jahre
				String maxJahreString = (String)this.comboLetztesJahr.getSelectedItem();
				
				if (maxJahreString.equals(MAX_JAHRE_UNENDLICH))
				{
					this.maxJahre = 0;
					this.optionen.remove(SpielOptionen.KEIN_ENDLOSSPIEL);
				}
				else
				{
					this.maxJahre = Integer.parseInt(maxJahreString);
					this.optionen.add(SpielOptionen.KEIN_ENDLOSSPIEL);
				}
				
				// Pruefe E-Mail-Einstellungen
				if (ok)
				{
					if (this.optionen.contains(SpielOptionen.EMAIL))
						ok = checkEmailSettings(this, this.emailAdresseSpielleiter, this.spieler);
				}
				
				if (ok)
				{
					this.abort = false;
					this.close();
				}
			}
		}
	}
	
	private boolean getSpielerFromControls(ArrayList<Spieler> spieler, int anzSp)
	{
		if (spieler == null)
			spieler = new ArrayList<Spieler>();
		
		for (int spIndex = spieler.size() - 1; spIndex >= anzSp; spIndex--)
			spieler.remove(spIndex);
		
		boolean ok = true;
		
		for (int spIndex = 0; spIndex < anzSp; spIndex++)
		{						
			this.tfSpieler[spIndex].setText(this.tfSpieler[spIndex].getText().trim());
			
			Spieler sp = null;
			
			if (spIndex < spieler.size())
			{
				sp = spieler.get(spIndex);
				
				sp.setName(this.tfSpieler[spIndex].getText());
				sp.setColIndex(this.canvasSpielerFarben[spIndex].colIndex);
				sp.setComputer(this.cbSpieler[spIndex].isSelected());
			}
			else
			{
				sp = new Spieler(this.tfSpieler[spIndex].getText(), "", 
						this.canvasSpielerFarben[spIndex].colIndex, this.cbSpieler[spIndex].isSelected(), false);
				
				spieler.add(sp);
			}
			
			if (sp.istComputer())
			{
				sp.setEmail(false);
				sp.setEmailAdresse("");
			}
			
			boolean userAllowed = 
					(sp.getName().length() >= Constants.SPIELER_NAME_MIN_LAENGE &&
					sp.getName().length() <= Constants.SPIELER_NAME_MAX_LAENGE &&
					!sp.getName().toLowerCase().equals(ServerConstants.ADMIN_USER.toLowerCase()) &&
					Pattern.matches(Constants.SPIELER_REGEX_PATTERN, sp.getName())
					);
			
			if (this.tfSpieler[spIndex].isEditable() && !userAllowed)
			{
				DialogWindow.showError(
						this,
						SternResources.SpielparameterJDialogNameZuLang(
								false, 
								sp.getName(), 
								Integer.toString(Constants.SPIELER_NAME_MIN_LAENGE), 
								Integer.toString(Constants.SPIELER_NAME_MAX_LAENGE)),
						SternResources.Fehler(false));
				ok = false;
				break;
			}
		}

		return ok;
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
	
	@Override
	public void colorChanged(int sp, byte newColorIndex, byte oldColorIndex)
	{
		for (SpielerFarbenCanvas c: this.canvasSpielerFarben)
		{
			if (c.spieler != sp && c.colIndex == newColorIndex)
			{
				c.setColor(oldColorIndex);
				break;
			}
		}
	}
	
	static boolean checkEmailSettings(Component c, String emailAdresseSpielleiter, ArrayList<Spieler> spieler)
	{
		boolean ok = true;
		
		if (emailAdresseSpielleiter == null || !emailAdresseSpielleiter.contains("@"))
		{
			DialogWindow.showError(
					c,
					SternResources.SpielparameterJDialogSpielleiterEMail(false), 
					SternResources.Fehler(false));
			return false;
		}
		
		for (Spieler sp: spieler)
		{
			if (sp.istComputer())
			{
				sp.setEmailAdresse("");
				sp.setEmail(false);
			}
			else
			{
				if (sp.istEmail() && !sp.getEmailAdresse().contains("@"))
				{
					ok = false;
					DialogWindow.showError(
							c,
							SternResources.SpielparameterJDialogSpielerEMail(false, sp.getName()),
							SternResources.Fehler(false));
					break;
				}
			}
		}
		
		return ok;
	}
}
