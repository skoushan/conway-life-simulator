package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import logic.Colony;

/**
 * Contains the ColonyPanel,Toolbar, and MenuBar. Has the main method.
 * 
 */
public class MainPanel extends JPanel {

	private Colony colony;
	private ColonyPanel colonyPanel;
	private Toolbar toolbar;

	public MainPanel(Colony c) {
		super(new BorderLayout());
		colony = c;

		// set up tool bar and colonyPanel
		toolbar = new Toolbar(colony);
		colony.addColonyChangeListener(toolbar);

		colonyPanel = new ColonyPanel(colony, toolbar);
		colony.addColonyChangeListener(colonyPanel);
		toolbar.cp = colonyPanel;

		// add them to this panel
		add(toolbar, BorderLayout.PAGE_START);
		add(new JScrollPane(colonyPanel), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Game of Life");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try { // try setting native look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		Colony colony = new Colony(0.2); // default colony
		MainPanel p = new MainPanel(colony);
		f.setContentPane(p);
		f.setJMenuBar(new MenuBar(colony, f, p.colonyPanel)); // set the menu
																// bar
		f.pack();
		f.setVisible(true);
	}
}
