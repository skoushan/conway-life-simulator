package gui;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import logic.Colony;

/**
 * Can set up a JFileChooser and then convert a text file to a grid.
 */
public class LifeFileReader {

	private JFileChooser fileChooser;
	private File file;
	private Component parent; // needed for JFileChooser

	public LifeFileReader(Component f) {
		parent = f;
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new LifeFilter());
	}

	public boolean[][] load() throws IOException {
		// Show the JFileChoose dialog
		int returnVal = fileChooser.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) { // if file selected
			return load(fileChooser.getSelectedFile()); // load it
		} else {
			return null;
		}
	}

	class LifeFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			if (f.getName().endsWith("lif") || f.getName().endsWith("col")
					|| f.getName().endsWith("txt")) {
				return true;
			} else
				return false;
		}

		public String getDescription() {
			return ".lif, .col, .txt";
		}
	}

	/**
	 * called to load any type of file.
	 */
	public static boolean[][] load(File f) throws IOException {
		if (f.getName().endsWith(".lif"))
			return expandGrid(loadLif(f), 50); // default expand grid files
		else if (f.getName().endsWith(".col") || f.getName().endsWith(".txt"))
			return loadCol(f);
		else
			return null;
	}

	/**
	 * Generates a grid based on a text file. The file should look just like a
	 * grid, with '.' representing dead cells and '*' representing live cells.
	 * 
	 * @param file
	 *            a .col file.
	 * @return a boolean array representing the grid
	 * @throws IOException
	 */
	public static boolean[][] loadCol(File file) throws IOException {
		// Buffer the content for faster reading
		BufferedReader reader = new BufferedReader(new FileReader(file));

		// number of columns in the length of the first line
		String line = reader.readLine();
		int numColumns = line.length();

		// number of rows is the number of lines in the file
		int numRows = countLines(new BufferedInputStream(new FileInputStream(
				file))) + 1; // +1 because count lines counts '\n' and there
								// won't be one at the end of the file

		boolean[][] grid = new boolean[numRows][numColumns]; // set up grid

		int row = 0;
		while (line != null && row < grid.length) { // loop through each line
			// read characters in this line
			for (int col = 0; col < grid[0].length; col++) {
				if (col < line.length() && line.charAt(col) == '*')
					grid[row][col] = true;
				else
					grid[row][col] = false;
			}
			row++;
			line = reader.readLine();
		}
		reader.close();
		return grid;
	}

	/**
	 * Counts the number of lines in a text file (found on StackOverflow)
	 * 
	 * @author martinus
	 */
	public static int countLines(InputStream is) throws IOException {
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
			}
			return count;
		} finally {
			is.close();
		}
	}

	/**
	 * Saves the grid to a file.
	 * 
	 * @param grid
	 *            the grid to be saved.
	 * @throws IOException
	 */
	public void saveGrid(boolean[][] grid) throws IOException {
		// show the file chooser
		int returnVal = fileChooser.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) { // if file selected
			file = fileChooser.getSelectedFile(); // get the file
			// add .col extension if doesn't already have it
			if (!file.getName().endsWith(".col"))
				file = new File(file.getAbsolutePath() + ".col");
		} else {
			return;
		}

		PrintWriter fileout = new PrintWriter(new FileWriter(file));
		// loop through rows and columns
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				// print '*' for live, '.' for dead
				fileout.print(grid[row][col] ? '*' : '.');
			}
			// don't line break the last line
			if (row < grid.length - 1)
				fileout.println();
		}
		fileout.close();
	}

	/* overloaded to accept a colony */
	public void saveColony(Colony c) throws IOException {
		saveGrid(c.getGrid());
	}

	/**
	 * Reads standard .lif files
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static boolean[][] loadLif(File f) throws IOException {
		// Buffer the content for faster reading
		Scanner s = new Scanner(new BufferedReader(new FileReader(f)));
		boolean[][] grid;

		int width = 0, height = 0;

		// loop lines until it reaches info line (x = 5, y = 2, ...)
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.charAt(0) == '#') { // ignore the commented lines
				System.out.println(line);
			} else { // once it has reached info line
				// create scanner for reading line (and remove spaces)
				Scanner inf_reader = new Scanner(line.replaceAll(" ", ""));
				inf_reader.useDelimiter(","); // info separated by commas

				// get width as a string by finding the '=' sign
				String x_string = inf_reader.next();
				x_string = x_string.substring(x_string.indexOf('=') + 1);

				// get height as a string by finding the '=' sign
				String y_string = inf_reader.next();
				y_string = y_string.substring(y_string.indexOf('=') + 1);

				// convert the strings to integers
				width = Integer.parseInt(x_string);
				height = Integer.parseInt(y_string);
				break; // stop looping through lines
			}
		}

		grid = new boolean[height][width];

		// '$' represents a new line in .lif format
		s.useDelimiter("\\$");
		int row = 0;
		while (row < grid.length && s.hasNext()) {
			String line = s.next();
			int col = 0;
			// loop through every character of the line
			for (int i = 0; i < line.length(); i++) {
				switch (line.charAt(i)) {
				case 'o':
					grid[row][col] = true;
					col++; // move onto next column
					break;
				case 'b':
					grid[row][col] = false;
					col++; // move onto next column
					break;
				case '\n':
					break; // ignore line breaks
				case '!':
					break; // stop at end of file
				default: // if it's a new run
					// get the numeric part of the string
					String col_string = getIntInString(i, line);
					int run = Integer.parseInt(col_string);

					// if the run is a run of new lines
					if (i + col_string.length() + 1 > line.length()) {
						row += run - 1; // increment the row accordingly
					} else {// check what to fill the run with
						boolean dead_alive = line.charAt(i + 1) == 'o';

						// fill the run
						for (int x = 0; x < run; x++) {
							grid[row][col] = dead_alive;
							col++;
						}

						// increment original character index
						// because it should skip these numbers
						i += col_string.length();
					}
					break;
				}
			}
			row++; // move onto next row
		}
		return grid;
	}

	/**
	 * Expands a grid on all 4 sides by the given size.
	 */
	public static boolean[][] expandGrid(boolean[][] grid, int size) {
		boolean[][] new_grid = new boolean[grid.length + size * 2][grid[0].length
				+ size * 2];

		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++) {
				new_grid[size + row][size + col] = grid[row][col];
			}
		return new_grid;
	}

	/**
	 * Returns the sequence of integers found at the beginning of a string. For
	 * example, 104fjios3 would return 104.
	 * 
	 * @param line
	 * @return a string representation of the integer
	 */
	private static String getIntInString(int start, String line) {
		String num_string = "";
		// loop through the string
		for (int i = start; i < line.length(); i++) {
			// if the character is digit
			if (Character.isDigit(line.charAt(i))) {
				num_string += line.charAt(i); // add it to the num_string
			} else
				break;
		}
		return num_string;
	}
}