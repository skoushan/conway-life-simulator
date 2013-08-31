package gui;

import logic.Colony;

/**
 * Used by ColonyPanel to listen to Colony changes.
 * 
 * @author Soheil Koushan
 * 
 */
public interface ColonyChangeListener {
	public void colonyChanged(Colony colony);

	public void simulationToggled();

	public void colonyAdvanced(Colony colony);
}
