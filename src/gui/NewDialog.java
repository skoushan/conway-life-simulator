package gui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.Colony;

/**
 * Dialog for creating a new colony.
 */
public class NewDialog extends JDialog implements PropertyChangeListener {

	private JTextField density, width, height;
	private int[] dimensions = new int[2];
	private JOptionPane optionPane;

	private Colony colony;

	public int[] getDimensions() { // returns selected dimensions
		return dimensions;
	}

	public NewDialog(Colony c, Frame f) {
		super(f, "New Colony", true);
		colony = c;

		// set up option pane contents
		density = new JTextField(5);
		width = new JTextField(5);
		height = new JTextField(5);
		JPanel message = new JPanel(new GridLayout(3, 2));
		message.add(new JLabel("Density:"));
		message.add(density);
		message.add(new JLabel("Width:"));
		message.add(width);
		message.add(new JLabel("Height:"));
		message.add(height);

		// set up option pane
		optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		optionPane.addPropertyChangeListener(this);

		setContentPane(optionPane);
		pack();
		setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) { // if it should care about this
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) { // ignore reset
				return;
			}

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user
			// presses the same button next time, no
			// property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (value.equals(JOptionPane.OK_OPTION)) { // OK
				double density;
				int row, col;
				try {
					density = Double.parseDouble(this.density.getText());
					row = Integer.parseInt(height.getText());
					col = Integer.parseInt(width.getText());
				} catch (Exception ex) { // return if input was invalid
					return;
				}
				colony.load(new Colony(density,col, row).getGrid());
			}
			setVisible(false); // hide when done

		}
	}

}
