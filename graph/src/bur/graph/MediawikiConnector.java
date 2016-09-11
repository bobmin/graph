package bur.graph;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private String token = null;

	public static void main(String[] args) {
		String server = null, page = null, user = null, pass = null, cmd = null;
		for (final String x : args) {
			if (x.startsWith("--server=")) {
				server = x.substring(9);
			} else if (x.startsWith("--page=")) {
				page = x.substring(7);
			} else if (x.startsWith("--user=")) {
				user = x.substring(7);
			} else if (x.startsWith("--pass=")) {
				pass = x.substring(7);
			} else if (x.startsWith("--set=")) {
				cmd = x.substring(6);
			} else {
				LOG.warning("[args] unknown: " + x);
			}
		}
		if (null == server || null == page) {
			throw new IllegalArgumentException("usage: --server=SERVERNAME --page=PAGETITLE");
		}
		if (null != cmd && (null == user || null == pass)) {
			throw new IllegalArgumentException(
					"usage: --server=SERVERNAME --page=PAGETITLE --user=USERNAME --pass=PASSWORD");
		}
		LOG.info("[args] assigned: server = " + server + ", page = " + page + ", cmd = " + cmd);
		final MediawikiConnector conn = new MediawikiConnector(server, page);
		conn.get();
		if (null != cmd) {
			final String postdata = conn.createNewText(cmd);
			conn.post(user, pass, postdata);
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

	/**
	 * Holt die Seite vom Server und speichert ihre Zeilen in {@link #values}.
	 */
	public void get() {
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

	/**
	 * Bearbeitet die Seiteninhalte mit {@code postdata} und schickt die
	 * komplette Seite wieder zum Server.
	 * 
	 * @param user
	 *            der Benutzer
	 * @param pass
	 *            das Kennwort
	 * @param postdata
	 *            die neuen Seiteninhalte (basierend auf dem Zeilenindex)
	 */
	public void post(final String user, final String pass, final String postdata) {
		// http://www.hccp.org/java-net-cookie-how-to.html
		// https://www.mediawiki.org/wiki/API:Login/de
		// https://github.com/Alfresco/alfresco-php-sdk/blob/master/mediawiki-integration/source/java/org/alfresco/module/mediawikiintegration/action/MediaWikiActionExecuter.java
		final Map<String, String> loginData = new HashMap<>();
		loginData.put("action", "login");
		loginData.put("format", "json");
		loginData.put("lgname", user);
		loginData.put("lgpassword", pass);

		doPost("http://" + server + "/wiki/api.php", loginData);

		final Map<String, String> editData = new HashMap<>();
		editData.put("action", "edit");
		editData.put("format", "json");
		editData.put("title", page);
		editData.put("text", "HALLOWELT");

		doPost("http://" + server + "/wiki/api.php", editData);

	}

	public void doPost(final String address, final Map<String, String> data) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			final URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();

			final StringBuffer dataEncode = new StringBuffer();

			for (Entry<String, String> e : data.entrySet()) {
				dataEncode.append(URLEncoder.encode(e.getKey(), "UTF-8"));
				dataEncode.append("=");
				dataEncode.append(URLEncoder.encode(e.getValue(), "UTF-8"));
				dataEncode.append("&");
			}
			dataEncode.append(URLEncoder.encode("token", "UTF-8"));
			dataEncode.append("=");
			dataEncode.append(URLEncoder.encode(((null == token ? "" : token) + "+\""), "UTF-8"));

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", Integer.toString(dataEncode.length()));

			connection.setUseCaches(false);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(dataEncode.toString());
				wr.flush();
				wr.close();
			}

			// String headerName = null;
			// for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) !=
			// null; i++) {
			// if (headerName.equals("Set-Cookie")) {
			// String cookie = connection.getHeaderField(i);
			// System.out.println("cookie: " + cookie);
			// }
			// }

			is = connection.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));
			String line;
			final StringBuffer sb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				if (0 < sb.length()) {
					sb.append("\n");
				}
				sb.append(line);
			}
			rd.close();
			final String response = sb.toString();
			// {"login":{"result":"NeedToken","token":"903165633db63a2a507878384d92be9d","cookieprefix":"wiki","sessionid":"05a015be811c5d50ccdf63151228d343"}}
			LOG.info("response recived: " + response);

			final Pattern p = Pattern.compile("\"token\":\"([^\"]+)\"");
			final Matcher m = p.matcher(sb.toString());
			if (m.find()) {
				token = m.group(1);
			}

			LOG.info("token assigned: " + token);

		} catch (final IOException ex) {
			LOG.log(Level.SEVERE, "", ex);

		} finally {
			close(connection, is, rd);

		}
	}

	private String createNewText(final String replace) {
		final StringBuffer sb = new StringBuffer();
		final int match = replace.indexOf(':');
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
