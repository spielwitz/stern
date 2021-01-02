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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.SternResources;
import common.Utils;
import commonUi.ButtonDark;
import commonUi.DialogFontHelper;
import commonUi.PanelDark;
import commonUi.SpringUtilities;

@SuppressWarnings("serial") class EmailAdressenJDialog extends JDialog
			implements 	ActionListener,
			ListSelectionListener,
			MouseListener,
			WindowListener
{
	private static Font font;
	
	private ButtonDark butCancel;
	private ButtonDark butSelect;
	private ButtonDark butDelete;
	
	private ArrayList<String> emailAdressen;
	private JList<String> list;
	private int[] seq;
	
	int selectedIndex = -1;
	
	EmailAdressenJDialog(
			JDialog parent,
			ArrayList<String> emailAdressen)
	{
		super (parent, SternResources.EmailAdressenJDialogTitel(false), true);
		
		font = DialogFontHelper.getFont();
		
		// Liste
		DefaultListModel<String> model = new DefaultListModel<String>();

		this.list = new JList<String>(model);
		this.list.setFont(font);
		this.list.setForeground(Color.white);
		this.list.setBackground(Color.BLACK);
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setLayoutOrientation(JList.VERTICAL);
		this.list.setVisibleRowCount(-1);
		this.list.addListSelectionListener(this);
		this.list.addMouseListener(this);
		this.addWindowListener(this);
		
		JScrollPane listScroller = new JScrollPane(this.list);
		listScroller.setPreferredSize(new Dimension(400, 300));
		
		this.emailAdressen = emailAdressen;
		//this.parent = parent;
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(30, 30, 30));
		PanelDark panShell = new PanelDark(new SpringLayout());
		panShell.setBackground(new Color(30, 30, 30));
		
		PanelDark panBase = new PanelDark();
		panBase.setLayout(new BorderLayout(10,10));
		// ---------------
		PanelDark panMain = new PanelDark();
		panMain.setLayout(new BorderLayout(20,10));
		
		// ---------------
		panMain.add(listScroller, BorderLayout.CENTER);
		
		// ----
		panBase.add(panMain, BorderLayout.CENTER);
		// ----
		
		PanelDark panButtons = new PanelDark();
		panButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));

		this.butDelete = new ButtonDark(this, SternResources.Loeschen(false), font);
		panButtons.add(this.butDelete);

		this.butSelect = new ButtonDark(this, SternResources.Auswaehlen(false), font);
		panButtons.add(this.butSelect);
		
		this.butCancel = new ButtonDark(this, SternResources.Schliessen(false), font);
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
		this.setLocationRelativeTo(parent);	
		this.setResizable(false);
		
		this.refreshListModel();
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.close(true);
		
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

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
        {
            int index = list.locationToIndex(e.getPoint());
            
            if (index >= 0)
            		this.butSelect.doClick();
        }
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		int selectedIndex = list.getSelectedIndex();
		
		if (selectedIndex >= 0)
			this.selectedIndex = this.seq[selectedIndex];
		else
			this.selectedIndex = -1;
		
		this.setControlsEnabled();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();

		if (obj == this.getRootPane())
		{
			// Abbruch
			this.close(true);
		}
		else if (obj == this.butCancel)
		{
			this.close(true);
		}
		else if (obj == this.butDelete)
		{
			this.emailAdressen.remove(this.selectedIndex);
			this.refreshListModel();
		}
		else if (obj == this.butSelect)
		{
			this.close(false);
		}
	}
	
	private void refreshListModel()
	{
		DefaultListModel<String> model = (DefaultListModel<String>) this.list.getModel();
		
		model.removeAllElements();
		
		Object[] objectList = this.emailAdressen.toArray();
		String[] adressenUnsorted =  Arrays.copyOf(objectList,objectList.length,String[].class);
		
		this.seq = Utils.listeSortieren(adressenUnsorted, false);
		
		for (int i = 0; i < this.emailAdressen.size(); i++)
			model.addElement(this.emailAdressen.get(this.seq[i]));
		
		this.setControlsEnabled();
	}
	
	private void setControlsEnabled()
	{
		this.butDelete.setEnabled(!this.list.isSelectionEmpty());
		this.butSelect.setEnabled(!this.list.isSelectionEmpty());
	}
	
	private void close(boolean abort)
	{
		if (abort)
			this.selectedIndex = -1;
		
		this.setVisible(false);
		this.dispose();
	}
}
