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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import common.ReleaseGetter;
import common.SternResources;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.DialogWindow;
import commonUi.DialogWindowResult;
import commonUi.LabelDark;
import commonUi.PanelDark;
import commonUi.PasswordFieldDark;
import commonUi.SpringUtilities;

@SuppressWarnings("serial") class ClipboardImportJDialog<T> extends JDialog
				implements ActionListener
{
	private static Font font;
	
	private ButtonDark butOk;
	private ButtonDark butCancel;
	private ButtonDark butImport;
	private ButtonDark butDelete;
	
	private PasswordFieldDark tfPassword;
	
	private JTextArea taImportData;
	private Class<T> expectedClass;
	
	private boolean passwordProtected;
	
	DialogWindowResult dlgResult = DialogWindowResult.CANCEL;
	Object obj;
	
	ClipboardImportJDialog(
			JDialog parent, 
			Class<T> expectedClass,
			boolean passwordProtected)
	{
		super (parent, SternResources.ClipboardImportJDIalogTitle(false), true);
		this.passwordProtected = passwordProtected;
		this.Initialize(expectedClass);
		this.setLocationRelativeTo(parent);	
	}
	
	ClipboardImportJDialog(
			Frame parent, 
			Class<T> expectedClass,
			boolean passwordProtected)
	{
		super (parent, SternResources.ClipboardImportJDIalogTitle(false), true);
		this.passwordProtected = passwordProtected;
		this.Initialize(expectedClass);
		this.setLocationRelativeTo(parent);	
	}
	
	private void Initialize(Class<T> expectedClass)
	{
		this.expectedClass = expectedClass;
		
		// Font laden
		font = DialogFontHelper.getFont();
		
		this.setLayout(new BorderLayout(5, 5));
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark();
		panMain.setLayout(new BorderLayout(20,10));
		
		// ---------------
		PanelDark panTextarea = new GroupBoxDark(
				SternResources.ClipboardImportJDIalogInhaltHierEinfuegen(false), font);
		panTextarea.setLayout(new BorderLayout());
		
		this.taImportData = new JTextArea("", 15, 30);
		this.taImportData.setFont(font);
		this.taImportData.setForeground(Color.white);
		this.taImportData.setBackground(Color.black);
		this.taImportData.setLineWrap(true);
		
		panTextarea.add(new JScrollPane(this.taImportData,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
									), BorderLayout.CENTER);
		
		if (this.passwordProtected)
		{
			PanelDark panPassword = new PanelDark(new FlowLayout(FlowLayout.LEFT));
			
			panPassword.add(new LabelDark(SternResources.Passwort(false), font));
			this.tfPassword = new PasswordFieldDark("", font);
			this.tfPassword.setColumns(30);
			panPassword.add(this.tfPassword);
			
			panTextarea.add(panPassword, BorderLayout.SOUTH);
		}
		
		panMain.add(panTextarea, BorderLayout.CENTER);
						
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.butImport = new ButtonDark(this, SternResources.Einfuegen(false), font);
		panButtons.add(this.butImport);
		
		this.butDelete = new ButtonDark(this, SternResources.Loeschen(false), font);
		panButtons.add(this.butDelete);
		
		this.butOk = new ButtonDark(this, SternResources.OK(false), font);
		panButtons.add(this.butOk);
		
		this.butCancel = new ButtonDark(this, SternResources.Abbrechen(false), font);
		panButtons.add(this.butCancel);
		
		panBase.add(panButtons, BorderLayout.SOUTH);
		
		panShell.add(panBase);
		
		SpringUtilities.makeCompactGrid(panShell,
                1, 1, //rows, cols
                10, 10, //initialX, initialY
                10, 10);//xPad, yPad
		
		this.add(panShell, BorderLayout.CENTER);
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				setVisible(false);
				dispose();
			}
		}
		);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		getRootPane().setDefaultButton(this.butCancel);
		
		this.pack();
		//this.setLocationRelativeTo(parent);	
		this.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		
		if (source == this.butCancel || source == this.getRootPane())
			this.close();
		else if (source == this.butDelete)
			this.taImportData.setText("");
		else if (source == this.butImport)
			this.taImportData.setText(EmailToolkit.getClipboardContent());
		else if (source == this.butOk)
		{
			String password = this.passwordProtected ?
								new String(this.tfPassword.getPassword()) :
								null;
								
			boolean ok = false;
			try
			{
				this.obj = EmailToolkit.parseEmail(this.taImportData.getText(), this.expectedClass, password);
				ok = (this.obj != null);
			}
			catch (Exception x)
			{
				this.obj = null;
			}
			
			if (ok)
			{
				this.dlgResult = DialogWindowResult.OK;
				this.close();
			}
			else
			{
				this.obj = null;
				if (password == null)
					DialogWindow.showError(
							this,
							SternResources.ClipboardImportJDIalogImportFehler(false,
									ReleaseGetter.getRelease()),
							SternResources.FehlerBeimLaden(false));
				else
					DialogWindow.showError(
							this,
							SternResources.ClipboardImportJDIalogImportFehlerPassword(false,
									ReleaseGetter.getRelease()),
							SternResources.FehlerBeimLaden(false));
			}
		}
	}
	
	private void close()
	{
		this.setVisible(false);
		this.dispose();
	}
}
