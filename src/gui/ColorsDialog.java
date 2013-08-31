package gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The dialog box for setting the coloring mode.
 */
public class ColorsDialog extends JDialog implements ActionListener {

	private ColonyPanel panel;

	/* cell buttons & info */
	private JRadioButton cell_random = new JRadioButton("Random"),
			cell_color = new JRadioButton("Color");
	private JRadioButton[] cell_buttons = { cell_random, cell_color, };
	private String[] cell_actionCommands = { "cell_random", "cell_color", };

	/* background buttons & info */
	private JRadioButton back_color = new JRadioButton("Color");

	public ColorsDialog(ColonyPanel p, Frame f) {
		super(f, "Coloring Setup", true);
		panel = p;

		/* set up cell color chooser panel */
		JPanel cell_panel = new JPanel(new GridLayout(0, 1));
		cell_panel.add(new JLabel("Cell Coloring"));
		ButtonGroup cell = new ButtonGroup();
		for (int i = 0; i < cell_buttons.length; i++) {
			cell_buttons[i].setActionCommand(cell_actionCommands[i]);
			cell_buttons[i].addActionListener(this);
			cell.add(cell_buttons[i]);
			cell_panel.add(cell_buttons[i]);
		}

		/* set up background color chooser panel */
		JPanel background_panel = new JPanel(new GridLayout(0, 1));
		background_panel.add(new JLabel("Background Coloring"));
		ButtonGroup background = new ButtonGroup();
		back_color.setActionCommand("back_color");
		back_color.addActionListener(this);
		background.add(back_color);
		background_panel.add(back_color);

		// select the following by default
		cell_color.setSelected(true);
		back_color.setSelected(true);
		updateButtonColors();

		/* add items to dialog */
		setLayout(new BorderLayout());
		add(cell_panel, BorderLayout.LINE_START);
		add(background_panel, BorderLayout.LINE_END);

		pack();
	}

	@Override
	/* called when radio buttons change */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(cell_random.getActionCommand()))
			panel.randomForeground = true;
		else if (cmd.equals(cell_color.getActionCommand())) { // setting color
			// get color from JColorChooser
			panel.cellColor = JColorChooser.showDialog(this,
					"Choose Cell Color", panel.cellColor);
			panel.randomForeground = false;
		} else if (cmd.equals(back_color.getActionCommand())) {
			// get color from JColorChooser
			panel.backgroundColor = JColorChooser.showDialog(this,
					"Choose Background Color", panel.backgroundColor);
		}
		updateButtonColors();
		panel.repaint();
	}

	/**
	 * Sets the text color of the JRadioButtons to what they are in the colony
	 * panel.
	 */
	private void updateButtonColors() {
		back_color.setForeground(panel.backgroundColor);
		cell_color.setForeground(panel.cellColor);
	}
}
