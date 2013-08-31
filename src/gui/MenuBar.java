package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import logic.Colony;

/** The menu bar for the program */
public class MenuBar extends JMenuBar implements ActionListener {

	/* file menu items and info */
	private JMenu file;
	private JMenuItem new_colony = new JMenuItem(), open = new JMenuItem(),
			save = new JMenuItem();
	private JMenuItem[] items = { new_colony, open, save, };
	private String[][] info = { // info for the file menu items
	{ "New", "Create a new colony" },
			{ "Open", "Browse for and load a colony file." },
			{ "Save", "Save the current colony as a file." }, };
	private KeyStroke[] accelerators = { // keyboard shortcuts for the items
	KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), };

	/* view menu items and info */
	private JMenu view;
	private JMenuItem coloring = new JMenuItem("Coloring...");

	public static String SUFFIX = "32.png"; // for ImageIcon selection
	private LifeFileReader userFileDialog; // used to open/save files

	private Colony colony;

	private JFrame frame;
	private ColorsDialog colorsDialog;

	public MenuBar(Colony c, JFrame f, ColonyPanel cp) {
		super();
		colony = c;
		frame = f;
		userFileDialog = new LifeFileReader(this);
		colorsDialog = new ColorsDialog(cp, f);

		// set up file menu
		file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		for (int i = 0; i < items.length; i++) { // set up menu items
			items[i].setText(info[i][0]);
			items[i].setIcon(createImageIcon("/img/" + info[i][0] + SUFFIX));
			items[i].getAccessibleContext()
					.setAccessibleDescription(info[i][1]);
			items[i].addActionListener(this);
			items[i].setActionCommand(info[i][0]);
			items[i].setAccelerator(accelerators[i]);
			file.add(items[i]);
		}
		add(file);

		// set up view menu
		view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);
		view.add(coloring);
		coloring.setActionCommand("Color");
		coloring.addActionListener(this);
		add(view);
	}

	@Override
	/**
	 * Listens to menu item actions and takes the appropriate action.
	 */
	public void actionPerformed(ActionEvent e) {
		colony.stop(); // stop the simulation
		String cmd = e.getActionCommand();
		if (cmd.equals(new_colony.getActionCommand())) { // new
			new NewDialog(colony, frame);
		} else if (cmd.equals(open.getActionCommand())) { // open
			boolean[][] new_grid = null;
			try { // load grid from a file dialog
				new_grid = userFileDialog.load();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Unable to load the Colony.");
				ex.printStackTrace();
			}
			if (new_grid != null) { // if successful
				colony.load(new_grid); // load the new grid
			}
		} else if (cmd.equals(save.getActionCommand())) { // save
			try {
				userFileDialog.saveColony(colony); // save colony
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this,
						"Unable to save the Colony.");
				e1.printStackTrace();
			}
		} else if (cmd.equals(coloring.getActionCommand())) { // colors
			colorsDialog.setVisible(true);
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Toolbar.class.getResource(path.toLowerCase());
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path.toLowerCase());
			return null;
		}
	}
}
