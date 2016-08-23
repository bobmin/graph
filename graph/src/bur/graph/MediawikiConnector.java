package bur.graph;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

/**
 * der Befehl wird ausgeführt, wenn nicht <code>null</code>
 * <dl>
 * <dt>--set="1:neuer Wert"</dt>
 * <dd>Ersetzt die erste Zeile mit "neuer Wert".</dd>
 * </dl>
 * 
 * @author maik@btmx.net
 * 
 */
public class MediawikiConnector {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(MediawikiConnector.class.getName());

	/** der Server wird verwendet */
	private final String server;

	/** der Seite wird abgefragt/bearbeitet */
	private final String page;

	private final Set<String> values = new LinkedHashSet<>();

	public static void main(String[] args) {
		String server = null, page = null, cmd = null;
		for (String x : args) {
			if (x.startsWith("--server=")) {
				server = x.substring(9);
			} else if (x.startsWith("--page=")) {
				page = x.substring(7);
			} else if (x.startsWith("--set=")) {
				cmd = x.substring(6);
			} else {
				LOG.warning("[args] unknown: " + x);
			}
		}
		if (null == server || null == page) {
			throw new IllegalArgumentException("usage: --server=SERVERNAME --page=PAGETITLE");
		}
		LOG.info("[args] assigned: server = " + server + ", page = " + page + ", cmd = " + cmd);
		final MediawikiConnector conn = new MediawikiConnector(server, page);
		conn.get();
		if (null != cmd) {
			final String postdata = conn.createNewText(cmd);
			conn.post(postdata);
		}
	}

	/**
	 * Instanziiert das Objekt für den Server und die Seite.
	 * 
	 * @param server
	 *            der Server
	 * @param page
	 *            der Seitentitel
	 */
	public MediawikiConnector(final String server, final String page) {
		this.server = server;
		this.page = page;
	}

	private void get() {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			// Create connection
			final URL url = new URL("http://" + server + "/wiki/index.php?title=" + page + "&action=raw");
			connection = (HttpURLConnection) url.openConnection();

			// HTML-Seite holen
			is = connection.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				values.add(line);
				LOG.info("line added: " + line);
			}
			rd.close();

		} catch (final IOException ex) {
			LOG.log(Level.SEVERE, "", ex);

		} finally {
			close(connection, is, rd);

		}
	}

	private void post(final String postdata) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			// Create connection
			final URL url = new URL("http://" + server + "/wiki/index.php?title=" + page + "&action=submit");
			connection = (HttpURLConnection) url.openConnection();

			byte[] data = postdata.getBytes(StandardCharsets.UTF_8);

			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", Integer.toString(data.length));

			final String encoded = DatatypeConverter.printBase64Binary("username:password".getBytes());
			connection.setRequestProperty("Authorization", "Basic " + encoded);

			connection.setUseCaches(false);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.write(data);
			}

			LOG.info("command performed:\n" + postdata);

		} catch (final IOException ex) {
			LOG.log(Level.SEVERE, "", ex);

		} finally {
			close(connection, is, rd);

		}
	}

	private String createNewText(final String replace) {
		final StringBuffer sb = new StringBuffer();
		int match = replace.indexOf(':');
		int index = 0;
		final Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			final String line = it.next();
			if (0 < index) {
				sb.append("\n");
			}
			if (match == index) {
				sb.append("* ").append(replace.substring(match + 1));
			} else {
				sb.append(line);
			}
			index++;
		}
		return sb.toString();
	}

	private void close(HttpURLConnection connection, InputStream is, BufferedReader rd) {
		if (null != rd) {
			try {
				rd.close();
			} catch (final IOException ex) {
				LOG.log(Level.WARNING, "[rd] ending corrupt", ex);
			}
		}
		if (null != is) {
			try {
				is.close();
			} catch (final IOException ex) {
				LOG.log(Level.WARNING, "[is] ending corrupt", ex);
			}
		}
		if (null != connection) {
			connection.disconnect();
		}
	}

	/**
	 * Liefert die Werte.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 */
	public Set<String> getValue() {
		return values;
	}

}
