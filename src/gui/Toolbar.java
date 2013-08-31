package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logic.Colony;

/**
 * The toolbar panel containing the basic functions.
 */
public class Toolbar extends JToolBar implements ActionListener,
		ChangeListener, ColonyChangeListener {

	public Colony colony;
	public ColonyPanel cp;

	// wow i'm even using 2D arrays in my GUI!
	public String[][] info = { { "Reset", "Return to initial colony." },
			{ "Start", "Start the simulation." },
			{ "Next", "Go to the next generation." },
			{ "Populate", "Draw an area to be populated." },
			{ "Eradicate", "Draw an area to eradicate." }, };
	private ImageIcon[] icons = new ImageIcon[info.length];

	/* used for alternating play/pause button */
	private String[][] alt_info = { { "Pause", "Pause the simulation" }, };
	private ImageIcon[] alt_icons = new ImageIcon[alt_info.length];

	/* JButtons and their mnemonics */
	private JButton reset = new JButton(), start = new JButton(),
			next = new JButton(), populate = new JButton(),
			eradicate = new JButton();
	private JButton[] buttons = { reset, start, next, populate, eradicate };
	private int[] mnemonics = { KeyEvent.VK_O, KeyEvent.VK_S, KeyEvent.VK_R,
			KeyEvent.VK_SPACE, KeyEvent.VK_P, KeyEvent.VK_E };

	public static final String SUFFIX = "32.png"; // used for ImageIcons

	private JSpinner density; // density for drawing
	private JSpinner cell_size_spinner;
	public int cell_size = 3;

	/*
	 * because the delay has an exponential effect, some math must be done for
	 * the JSlider
	 */
	private JSlider delay; // simulation speed
	private Object f;
	static int wantedMin = 0;
	static double wantedMax = 2000;
	static int min = 100;
	static int exp = 8;
	static int max = (int) Math.pow(wantedMax * Math.pow(min, exp), 1.0 / exp);
	public static int DEFAULT_DELAY = min + (max - min) / 2;

	public Toolbar(Colony c) {
		super();
		colony = c;

		// set up icons
		for (int i = 0; i < icons.length; i++) {
			icons[i] = MenuBar.createImageIcon("/img/" + info[i][0] + SUFFIX);
		}
		for (int i = 0; i < alt_icons.length; i++) {
			alt_icons[i] = MenuBar.createImageIcon("/img/" + alt_info[i][0]
					+ SUFFIX);
		}

		// set up buttons
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setActionCommand(info[i][0]);
			buttons[i].addActionListener(this);
			buttons[i].setMnemonic(mnemonics[i]);
			buttons[i].setIcon(icons[i]);
			buttons[i].setToolTipText(info[i][1]);
		}
		populate.setSelected(true); // select populate button (default)

		// set up delay
		delay = new JSlider(JSlider.HORIZONTAL, min, max, DEFAULT_DELAY);
		delay.setInverted(true); // goes from high number to low
		delay.addChangeListener(this);
		delay.setBorder(BorderFactory.createTitledBorder("Simulation Speed"));

		// set up density spinner
		density = new JSpinner(new SpinnerNumberModel(1, 0.1, 1.0, 0.1));
		density.setBorder(BorderFactory.createTitledBorder("Drawing Density"));
		density.setToolTipText("Success rate for populate and eradicate.");

		cell_size_spinner = new JSpinner(new SpinnerNumberModel(cell_size, 1,
				10, 1));
		cell_size_spinner.setBorder(BorderFactory
				.createTitledBorder("Cell Size"));
		cell_size_spinner.setToolTipText("Cell size for drawing");
		cell_size_spinner.addChangeListener(this);

		add(reset);
		add(start);
		add(next);
		add(delay);
		addSeparator();
		add(populate);
		add(eradicate);
		add(density);
		add(cell_size_spinner);
	}

	/**
	 * 
	 * @return the density set on the JSpinner
	 */
	public double getDensity() {
		SpinnerNumberModel dateModel = (SpinnerNumberModel) density.getModel();
		return dateModel.getNumber().doubleValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(reset.getActionCommand())) { // reset
			colony.reset();
		} else if (cmd.equals(start.getActionCommand())) { // start/pause
			colony.toggleTimer();
		} else if (cmd.equals(populate.getActionCommand())) { // populate
			// toggle drawing tools
			populate.setSelected(true);
			eradicate.setSelected(false);
		} else if (cmd.equals(eradicate.getActionCommand())) { // eradicate
			// toggle drawing tools
			eradicate.setSelected(true);
			populate.setSelected(false);
		} else if (cmd.equals(next.getActionCommand())) { // advance
			colony.advance();
		}
	}

	/**
	 * 
	 * @return the drawing tool selected ("eradicate" or "populate")
	 */
	public String getSelectedButton() {
		if (eradicate.isSelected())
			return "eradicate";
		else
			return "populate";
	}

	@Override
	/**
	 * Change colony delay when JSlider changed
	 */
	public void stateChanged(ChangeEvent e) {
		double value = (double) delay.getValue();
		double delay = Math.pow(value / min, exp) + wantedMin - 1;
		colony.timer.setDelay((int) delay);

		cell_size = (Integer) (cell_size_spinner.getValue());
		cp.setCellSize(cell_size);
	}

	@Override
	public void colonyChanged(Colony colony) {
	}

	@Override
	/**
	 * Toggles play/pause button when simulation is started/stopped
	 */
	public void simulationToggled() {
		if (colony.timer.isRunning()) { // if running
			start.setToolTipText(alt_info[0][1]);// set to pause text
			start.setIcon(alt_icons[0]);// set to pause icon
		} else {
			start.setToolTipText(info[1][1]);// set to play text
			start.setIcon(icons[1]); // set to play icon
		}
	}

	@Override
	public void colonyAdvanced(Colony colony) {
	}

}
