package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import logic.Colony;

/**
 * Displays its associated Colony. Files can be dragged and dropped onto this.
 * 
 * @author Soheil Koushan
 * 
 */
public class ColonyPanel extends JPanel implements ColonyChangeListener {

	private int cell_size;

	private Colony colony;
	private Toolbar toolbar;

	/* for selection area drawing */
	private Rectangle currentRect = null;
	private Rectangle rectToDraw = null;
	private Rectangle previousRectDrawn = new Rectangle();

	/* for cell drawing */
	public Color backgroundColor = Color.WHITE;
	public Color cellColor = Color.BLACK;
	public boolean randomForeground = false;

	public ColonyPanel(Colony c, Toolbar t) {
		super();
		colony = c;
		toolbar = t;
		cell_size = t.cell_size;

		setPreferredSize(new Dimension(colony.getWidth() * cell_size,
				colony.getHeight() * cell_size));

		// Create the drag and drop listener
		MyDragDropListener myDragDropListener = new MyDragDropListener();

		// Connect the label with a drag and drop listener
		new DropTarget(this, myDragDropListener);

		// List to mouse events for drawing
		MyMouseListener mouseListener = new MyMouseListener();
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (randomForeground) // get random color every repaint
			cellColor = (Color.getHSBColor((float) Math.random(),
					randomInRange(0.8F, 1.0F), 1.0F));

		/* iterate over the grid and draw using CELL_SIZE */
		for (int row = 0; row < colony.getHeight(); row++)
			for (int col = 0; col < colony.getWidth(); col++) {
				if (colony.alive(row, col)) {
					g.setColor(cellColor);
					g.fillRect(col * cell_size, row * cell_size, cell_size,
							cell_size);
				} else {
					g.setColor(backgroundColor);
					g.fillRect(col * cell_size, row * cell_size, cell_size,
							cell_size);
				}
			}

		if (currentRect != null) { // draw rectangle if necessary
			g.setXORMode(cellColor); // Color of line varies
											// depending on image colors
			g.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width - 1,
					rectToDraw.height - 1);
		}
	}

	/**
	 * Returns a random float between the ranges given.
	 * 
	 * @param a
	 *            the lower range
	 * @param b
	 *            the upper range
	 * @return
	 */
	public static float randomInRange(float a, float b) {
		return (float) (Math.random() * (b - a) + a);
	}

	/**
	 * Updates the rectangleToDraw. Takes into account negative widths/heights.
	 * 
	 * @param compWidth
	 *            width of the entire draw-able area
	 * @param compHeight
	 *            height of the entire draw-able area
	 */
	private void updateDrawableRect(int compWidth, int compHeight) {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;

		// Make the width and height positive, if necessary.
		if (width < 0) {
			width = 0 - width; // absolute value
			x = x - width + 1; // move x to the left
			if (x < 0) { // if left corner extends past the drawing area
				x = 0; // set the corner to 0
				width += x; // add the extra to the width
			}
		}
		if (height < 0) { // similar to above
			height = 0 - height;
			y = y - height + 1;
			if (y < 0) {
				height += y;
				y = 0;
			}
		}

		if ((x + width) > compWidth) {// if right side extends past the drawing
										// area.
			width = compWidth - x; // snap it to the edge
		}
		if ((y + height) > compHeight) { // similar to above
			height = compHeight - y;
		}

		// Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			previousRectDrawn.setBounds(rectToDraw.x, rectToDraw.y,
					rectToDraw.width, rectToDraw.height);
			rectToDraw.setBounds(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}

	/**
	 * Listens to mouse events for drawing
	 * 
	 * @author Soheil Koushan
	 * 
	 */
	private class MyMouseListener extends MouseInputAdapter {
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (e.getModifiers() == KeyEvent.VK_CONTROL) { // if CTRL is held
				currentRect = new Rectangle(x, y, 0, 0); // set up the rectangle
				updateDrawableRect(getWidth(), getHeight());

			} else { // eradicate/populate square under pointer
				if (toolbar.getSelectedButton().equals("eradicate"))
					colony.eradicate(e.getX() / cell_size,
							e.getY() / cell_size, 1, 1, toolbar.getDensity());
				else
					colony.populate(e.getX() / cell_size, e.getY() / cell_size,
							1, 1, toolbar.getDensity());
			}
			repaint();
		}

		public void mouseDragged(MouseEvent e) {
			if (currentRect != null) // if drawing rectangle
				updateSize(e); // update its size

			else {
				// eradicate/populate area under pointer
				if (toolbar.getSelectedButton().equals("eradicate"))
					colony.eradicate(e.getX() / cell_size,
							e.getY() / cell_size, 1, 1, toolbar.getDensity());
				else
					colony.populate(e.getX() / cell_size, e.getY() / cell_size,
							1, 1, toolbar.getDensity());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (rectToDraw != null) { // if rectangle exists
				// eradicate/populate the area in the rectangle
				if (toolbar.getSelectedButton().equals("eradicate"))
					colony.eradicate(rectToDraw.x / cell_size, rectToDraw.y
							/ cell_size, rectToDraw.width / cell_size + 1,
							rectToDraw.height / cell_size + 1,
							toolbar.getDensity());
				else
					colony.populate(rectToDraw.x / cell_size, rectToDraw.y
							/ cell_size, rectToDraw.width / cell_size + 1,
							rectToDraw.height / cell_size + 1,
							toolbar.getDensity());
			}
			currentRect = null;
			rectToDraw = null;
		}

		/*
		 * Update the size of the current rectangle and calls repaint. For
		 * efficiency, a painting region is specified.
		 */
		void updateSize(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			currentRect.setSize(x - currentRect.x, y - currentRect.y);
			updateDrawableRect(colony.getWidth() * cell_size,
					colony.getHeight() * cell_size);
			Rectangle totalRepaint = rectToDraw.union(previousRectDrawn);
			repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width,
					totalRepaint.height);
		}
	}

	/**
	 * Listens to drag and drop events.
	 * 
	 * @author Soheil Koushan
	 * 
	 */
	class MyDragDropListener extends DropTargetAdapter {

		@Override
		public void drop(DropTargetDropEvent event) {

			// Accept copy drops
			event.acceptDrop(DnDConstants.ACTION_COPY);

			// Get the transfer which can provide the dropped item data
			Transferable transferable = event.getTransferable();

			// Get the data formats of the dropped item
			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			// Loop through the flavors
			for (DataFlavor flavor : flavors) {
				try {
					// If the drop items are files
					if (flavor.isFlavorJavaFileListType()) {
						// Get all of the dropped files
						List<File> files = (List<File>) transferable
								.getTransferData(flavor);

						// Loop them through
						for (File file : files) {
							if (file != null) {
								colony.load(LifeFileReader.load(file));
								break; // leave the loop
							}
						}
					}
				} catch (Exception e) {
					// Print out the error stack
					e.printStackTrace();
				}
			}
			// Inform that the drop is complete
			event.dropComplete(true);
		}
	}

	/** sets the cell size */
	public void setCellSize(int size) {
		cell_size = size;
		setPreferredSize(new Dimension(colony.getWidth() * cell_size,
				colony.getHeight() * cell_size));
		repaint();
		revalidate();
	}

	@Override
	/**
	 * repaints when colony advances.
	 */
	public void colonyAdvanced(Colony colony) {
		repaint();
	}

	@Override
	public void colonyChanged(Colony colony) {
		// reset the size
		setPreferredSize(new Dimension(colony.getWidth() * cell_size,
				colony.getHeight() * cell_size));
		repaint();
		revalidate(); // this will show scroll bars if necessary
	}

	@Override
	public void simulationToggled() {
	}
}
