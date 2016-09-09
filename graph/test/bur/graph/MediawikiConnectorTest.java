package bur.graph;

import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Prüft die Klasse {@link MediawikiConnector}.
 * 
 * @author maik@btmx.net
 *
 */
public class MediawikiConnectorTest {

	/** der Server zum Testen */
	private static final String SERVER = System.getProperty(
			MediawikiConnectorTest.class.getSimpleName() + ".server");

	/** die Seite zum Testen */
	private static final String PAGE = System.getProperty(
			MediawikiConnectorTest.class.getSimpleName() + ".page");

	/** die Seite zum Testen */
	private static final String USER = System.getProperty(
			MediawikiConnectorTest.class.getSimpleName() + ".user");

	/** die Seite zum Testen */
	private static final String PASS = System.getProperty(
			MediawikiConnectorTest.class.getSimpleName() + ".pass");

	@BeforeClass
	public static void beforeClass() {
		if (null == SERVER || 0 == SERVER.trim().length()) {
			Assert.fail("[server] unknown");
		}
		if (null == PAGE || 0 == PAGE.trim().length()) {
			Assert.fail("[page] unknown");
		}
		if (null == USER || 0 == USER.trim().length()) {
			Assert.fail("[user] unknown");
		}
		if (null == PASS || 0 == PASS.trim().length()) {
			Assert.fail("[pass] unknown");
		}
	}

	@Test
	public void testGet() {
		final Set<String> values = printPageValues();
		System.out.println("values: " + values);
		Assert.assertNotNull(values);
		Assert.assertTrue("Keine Zeilen?", 0 < values.size());
	}

	@Test
	@Ignore
	public void testPost() {
		final int postIndex = 1;
		final Set<String> values = printPageValues();
		Assert.assertTrue("Index unbekannt?", postIndex < values.size());
		final String backup = values.toArray(new String[values.size()])[postIndex];
		System.out.println("backup[" + postIndex + "]: " + backup);
		// POST ausführen
		final MediawikiConnector conn = new MediawikiConnector(SERVER, PAGE);
		conn.post(USER, PASS, "1:* JUnit DEMO");
		final Set<String> postValues = printPageValues();
		final String checkValue = postValues.toArray(new String[values.size()])[postIndex];
		Assert.assertEquals("POST nicht erfolgreich?", "* JUnit DEMO", checkValue);
		conn.post(USER, PASS, "1:" + backup);
	}

	private Set<String> printPageValues() {
		final MediawikiConnector conn = new MediawikiConnector(SERVER, PAGE);
		conn.get();
		final Set<String> values = conn.getValue();
		System.out.println("values: " + values);
		return values;
	}

}
