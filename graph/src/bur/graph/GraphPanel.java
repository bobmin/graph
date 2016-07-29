package bur.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Die Benutzeroberfläche definiert die Verteilung der Grafiken und ihre
 * Reaktion auf Größenänderungen.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class GraphPanel extends JPanel {

	/**
	 * Instanziiert die Benutzeroberfläche.
	 */
	public GraphPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		// Spalten: 5 + 1
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		// Reihen: 6 + 1
		gridBagLayout.rowHeights = new int[] { 100, 100, 100, 100, 50, 15, 0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
	}

	public void add(final int x, final int y, final JComponent comp) {
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = x;
		gbc.gridy = y;
		if (3 < y) {
			gbc.gridwidth = 5;
		}
		gbc.fill = GridBagConstraints.BOTH;
		add(comp, gbc);
	}

}
