package bur.graph;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import bur.graph.BarGraph.Mode;

/**
 * Prüft die Klasse {@link BarGraph}.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BarGraphTest {

	@Test
	public void testBlueData() {
		final double[] orig = new double[6];
		Arrays.fill(orig, 1000.0d);
		final BarGraph barGraph = new BarGraph();
		barGraph.setValues(orig, 0.0);
		check(barGraph, orig, null);
	}

	@Test
	public void testBlueRedData() {
		final double[] origBlue = new double[6];
		Arrays.fill(origBlue, 900.0d);
		final double[] origRed = new double[6];
		Arrays.fill(origRed, 100.0d);
		final BarGraph barGraph = new BarGraph();
		barGraph.setValues(Mode.RED_IN_BLUE, origBlue, origRed, 0.0);
		check(barGraph, origBlue, origRed);
	}

	@Test
	public void testMiniData() {
		final double[] origBlue = new double[] { 0.017890866666900002, 0.0275067333335, 0.032335533333500005,
				0.0299632666665, 0.0443292666665, 0.13442540000099998 };
		final double[] origRed = new double[] { 0.0314899999999, 0.0613749333336, 0.10737280000089999, 0.0897526000015,
				0.37404666666900005, 0.33393659999899994 };
		final BarGraph barGraph = new BarGraph();
		barGraph.setValues(Mode.RED_IN_BLUE, origBlue, origRed, 0.0);
		check(barGraph, origBlue, origRed);
		barGraph.setValues(Mode.RED_AND_BLUE_START_BY_ZERO, origBlue, origRed, 0.0);
		check(barGraph, origBlue, origRed);
	}

	private void check(final BarGraph barGraph, final double[] origBlue, final double[] origRed) {
		final double[] blueValues = barGraph.getNormBlueValues();
		final double[] redValues = barGraph.getNormRedValues();
		System.out.println("       orig    orig    norm    norm");
		System.out.println("       blue     red    blue     red");
		for (int idx = 0; idx < 6; idx++) {
			final double ob = origBlue[idx];
			final double or = (null == origRed ? 0.0d : origRed[idx]);
			final double nb = blueValues[idx];
			Assert.assertTrue(100.01d >= nb);
			final double nr = redValues[idx];
			Assert.assertTrue(100.01d >= nr);
			System.out.println(String.format("[%d] %7.2f %7.2f %7.2f %7.2f", idx, ob, or, nb, nr));
		}
	}

}
