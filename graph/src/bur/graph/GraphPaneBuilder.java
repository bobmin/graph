package bur.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Erstellt eine Benutzeroberfläche.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class GraphPaneBuilder {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(GraphPaneBuilder.class.getName());

	/** die Spaltenbreiten */
	private final int[] columnWidths;

	/** die Spaltengewichte */
	private final double[] columnWeights;

	/** die Zeilenhöhen */
	private final int[] rowHeights;

	/** die Zeilengewichte */
	private final double[] rowWeights;

	/** die Zeilenverbundeinstellung */
	private final boolean[] rowFilled;

	public static GraphPaneBuilder create(final int xCount, final int yCount) {
		final int cols = xCount + 1;
		final int rows = yCount + 1;
		// Spaltenbreite
		final int[] columnWidths = new int[cols];
		Arrays.fill(columnWidths, 100);
		columnWidths[cols - 1] = 0;
		// Spaltengewicht
		final double[] columnWeights = new double[cols];
		Arrays.fill(columnWeights, 1.0);
		columnWeights[cols - 1] = Double.MIN_VALUE;
		// Zeilenhöhe
		final int[] rowHeights = new int[rows];
		Arrays.fill(rowHeights, 100);
		rowHeights[rows - 1] = 0;
		// Zeilengewicht
		final double[] rowWeights = new double[rows];
		Arrays.fill(rowWeights, 1.0);
		rowWeights[rows - 1] = Double.MIN_VALUE;
		// Zeilenverbund
		final boolean[] lineFilled = new boolean[rows];
		Arrays.fill(lineFilled, false);
		// Go!
		return new GraphPaneBuilder(columnWidths, columnWeights, rowHeights, rowWeights, lineFilled);
	}

	/**
	 * Initziiert eine Standardbenutzeroberfläche in Form eines Gitters aus
	 * 100 x 100 Pixel großen Komponenten.
	 * 
	 * @param xCount
	 *            die Spaltenanzahl
	 * @param yCount
	 *            die Zeilenanzahl
	 */
	private GraphPaneBuilder(
			final int[] columnWidths, final double[] columnWeights,
			final int[] rowHeights, final double[] rowWeights,
			final boolean[] lineFilled) {
		this.columnWidths = columnWidths;
		this.columnWeights = columnWeights;
		this.rowHeights = rowHeights;
		this.rowWeights = rowWeights;
		this.rowFilled = lineFilled;
	}

	public GraphPaneBuilder rowHeight(final int rowIndex, final int value) {
		rowHeights[rowIndex] = value;
		return GraphPaneBuilder.this;
	}

	public GraphPaneBuilder rowWeight(final int rowIndex, final double value) {
		rowWeights[rowIndex] = value;
		return GraphPaneBuilder.this;
	}

	public GraphPaneBuilder rowFill(final int rowIndex, final boolean value) {
		rowFilled[rowIndex] = value;
		return GraphPaneBuilder.this;
	}

	/**
	 * Erstellt die Benutzeroberfläche.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public GraphPane build() {
		return new GraphPane();
	}

	/**
	 * Die Oberfläche für den Benutzer.
	 */
	public class GraphPane extends JPanel {

		private GraphPane() {
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = GraphPaneBuilder.this.columnWidths;
			gridBagLayout.columnWeights = GraphPaneBuilder.this.columnWeights;
			gridBagLayout.rowHeights = GraphPaneBuilder.this.rowHeights;
			gridBagLayout.rowWeights = GraphPaneBuilder.this.rowWeights;
			setLayout(gridBagLayout);
			setOpaque(false);
		}

		public void add(final int x, final int y, final JComponent comp) {
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.gridx = x;
			gbc.gridy = y;
			if (rowFilled[y]) {
				gbc.gridwidth = columnWidths.length;
			}
			gbc.fill = GridBagConstraints.BOTH;
			add(comp, gbc);
			LOG.fine("component added: x = " + x + ", y = " + y + ", fill = " + rowFilled[y] + ", class = "
					+ comp.getClass().getName());
		}

	}

}
