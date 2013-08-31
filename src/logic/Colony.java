package logic;

import gui.ColonyChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

/**
 * A 2D boolean array implementation of a colony.
 * 
 * @author Soheil Koushan
 * 
 */
public class Colony implements ActionListener {

	private boolean[][] grid;
	private boolean[][] initial; // used for reseting to initial state
	private ArrayList<ColonyChangeListener> listeners = new ArrayList<ColonyChangeListener>();

	public Timer timer = new Timer(gui.Toolbar.DEFAULT_DELAY, this);

	public Colony(boolean[][] grid) {
		this.grid = grid;
		initial = grid;
	}

	/** default constructor */
	public Colony(double density) {
		this(density, 200, 150);
	}

	/**
	 * Generates a grid based on the density given.
	 * 
	 * @param density
	 *            a number from 0 to 1
	 */
	public Colony(double density, int width, int height) {
		grid = new boolean[height][width];
		initial = grid;
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
				grid[row][col] = Math.random() < density;
	}

	/**
	 * @return whether the cell is alive.
	 */
	public boolean alive(int row, int column) {
		if (withinArray(row, column))
			return grid[row][column];
		else
			return false;
	}

	/**
	 * @return if the cell should live based on the game rules.
	 */
	public boolean live(int row, int column) {
		if (!withinArray(row, column))
			return false;

		int neighbours = neighbours(row, column); // count the neighbours

		/* game rules */
		if (neighbours == 3)
			return true;
		if (neighbours == 2 && grid[row][column]) // if 2 and alive
			return true;
		else
			return false;
	}

	/**
	 * advances the colony by 1 generation.
	 */
	public void advance() {
		boolean[][] advanced = new boolean[grid.length][grid[0].length];

		// loop through all cells and check if it should live
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
				advanced[row][col] = live(row, col);

		grid = advanced;
		notifyAdvanceListeners();
	}

	public int getWidth() {
		return grid[0].length;
	}

	public int getHeight() {
		return grid.length;
	}

	public boolean[][] getGrid() {
		return grid;
	}

	/**
	 * used to set all the values given by the parameters to the boolean given
	 */
	private void iterate(int x, int y, int width, int height, double density,
			boolean bool) {
		for (int row = y; row < y + height; row++)
			for (int column = x; column < x + width; column++) {
				if (withinArray(row, column) && Math.random() < density)
					grid[row][column] = bool;
			}
		notifyAdvanceListeners();
	}

	public void populate(int x, int y, int width, int height, double density) {
		iterate(x, y, width, height, density, true);
	}

	public void eradicate(int x, int y, int width, int height, double density) {
		iterate(x, y, width, height, density, false);
	}

	/**
	 * @return number of live neighbours
	 */
	private int neighbours(int row, int column) {
		int count = 0;
		// check the first and last row (skips middle row with +=2)
		for (int r = row - 1; r <= row + 1; r += 2)
			for (int c = column - 1; c <= column + 1; c++) {
				if (withinArray(r, c) && grid[r][c]) {
					count++;
				}
			}

		// check the second row (skips the center column with +=2)
		for (int c = column - 1; c <= column + 1; c += 2) {
			if (withinArray(row, c) && grid[row][c]) {
				count++;
			}
		}

		return count;
	}

	/**
	 * A check used frequently to prevent ArrayIndexOutOfBoundsExceptions
	 * 
	 * @return if the row and column are bounded by the grid
	 */
	private boolean withinArray(int row, int column) {
		if (row >= 0 && row < grid.length && column >= 0
				&& column < grid[0].length)
			return true;
		else
			return false;
	}

	/**
	 * Class method for printing out any grid
	 * 
	 * @param grid
	 * @return a string representation of the grid
	 */
	public static String toString(boolean[][] grid) {
		String output = "Colony:\n";
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				output += grid[row][col] ? '*' : '.';
			}
			output += '\n';
		}
		return output;
	}

	public String toString() {
		return toString(grid);
	}

	/**
	 * Becomes the given grid.
	 */
	public void load(boolean[][] grid) {
		this.grid = grid;
		initial = this.grid; // reset inital grid
		notifyListeners(false, true, false);
	}

	/**
	 * Resets the grid to its initial state.
	 */
	public void reset() {
		grid = initial;
		notifyAdvanceListeners();
	}

	/* methods for colony changes */
	public void addColonyChangeListener(ColonyChangeListener cgl) {
		if (!listeners.contains(cgl))
			listeners.add(cgl);
	}

	public boolean removeColonyChangeListener(ColonyChangeListener cgl) {
		return listeners.remove(cgl);
	}

	public void notifyListeners(boolean colonyAdvanced, boolean colonyChanged,
			boolean simulationToggled) {
		for (int i = 0; i < listeners.size(); i++) {
			ColonyChangeListener cgl = listeners.get(i);
			if (colonyAdvanced)
				cgl.colonyAdvanced(this);
			if (colonyChanged)
				cgl.colonyChanged(this);
			if (simulationToggled)
				cgl.simulationToggled();
		}
	}

	// only notify that an advance has taken place
	public void notifyAdvanceListeners() {
		notifyListeners(true, false, false);
	}

	/**
	 * Called by the timer. Advanced the colony.
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		advance();
		notifyAdvanceListeners();
	}

	/**
	 * Toggles the simulation (stops/starts it).
	 */
	public void toggleTimer() {
		if (timer.isRunning()) {
			stop();
		} else {
			start();
		}
	}

	public void stop() {
		timer.stop();
		notifyListeners(false, false, true);
	}

	public void start() {
		timer.start();
		notifyListeners(false, false, true);
	}
}
