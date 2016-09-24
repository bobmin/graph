package bur.graph;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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

	/** die Programmkennung */
	private static final String USERAGENT = MediawikiConnector.class.getSimpleName()
			+ ".java/0.1 (https://github.com/bobmin/graph/)";

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(MediawikiConnector.class.getName());

	/** der Server wird verwendet */
	private final String server;

	/** der Seite wird abgefragt/bearbeitet */
	private final String page;

	/** das Cookie zur Anmeldung */
	private Map<String, String> cookies = new HashMap<>();

	/** die verschiedenen Zeilenwerte */
	private final Set<String> values = new LinkedHashSet<>();

	/** die API-Adresse vom Wiki */
	private final String apiUrl;

	/** der Token zur Anmeldung */
	private String token = null;

	/**
	 * Das Hauptprogramm liest die Konsolenparamter und führt den Aufruf aus.
	 * <ul>
	 * <li>--set="1:neuer Wert"</li>
	 * </ul>
	 * 
	 * @param args
	 *            die Konsolenparameter
	 */
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
		if (null != cmd) {
			final String postdata = conn.createText(cmd);
			conn.publish(user, pass, postdata);
		}
	}

	/**
	 * Instanziiert das Objekt für den Server und lädt die Seite.
	 * 
	 * @param server
	 *            der Server
	 * @param page
	 *            der Seitentitel
	 */
	public MediawikiConnector(final String server, final String page) {
		this.server = server;
		this.page = page;
		this.apiUrl = "http://" + server + "/wiki/api.php?format=json";
		doGet("http://" + server + "/wiki/index.php?title=" + page + "&action=raw", true);
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
	 *            der neue Seiteninhalt
	 * @throws NullPointerException
	 *             wenn {@code postdata} gleich <code>null</code>
	 */
	public void publish(final String user, final String pass, final String postdata) {
		Objects.requireNonNull(postdata);

		// erster Login-Aufruf holt Token
		final Map<String, String> loginData = new HashMap<>();
		loginData.put("lgname", user);
		loginData.put("lgpassword", pass);
		doPost(apiUrl + "&action=login", loginData);

		// zweiter Login-Aufruf nutzt Token
		loginData.put("lgtoken", token);
		doPost(apiUrl + "&action=login", loginData);

		// den Token zur Bearbeitung holen
		doGet(apiUrl + "&action=query&prop=info&intoken=edit&titles=Neuigkeiten", false);

		// die Bearbeitung durchführen
		final Map<String, String> editData = new HashMap<>();
		editData.put("title", page);
		editData.put("text", postdata);
		editData.put("token", token.replace("\\\\", "\\"));
		doPost(apiUrl + "&action=edit", editData);

	}

	/**
	 * Holt die Seite zur Adresse vom Server. Ist die Speicheroption gleich
	 * <code>true</code>, werden die Zeilen der Seite in {@code values}
	 * gespeichert.
	 * 
	 * @param address
	 *            die Seitenadresse
	 * @param per­sis­tent
	 *            die Speicheroption
	 */
	private void doGet(final String address, final boolean per­sis­tent) {
		final StringBuffer content = new StringBuffer();

		if (per­sis­tent && 0 < values.size()) {
			values.clear();
		}

		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader rd = null;

		try {
			final URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();

			setCookies(connection);

			is = connection.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));

			grabCookies(connection);

			String line;
			while ((line = rd.readLine()) != null) {
				if (0 < content.length()) {
					content.append("\n");
				}
				content.append(line);
				if (per­sis­tent) {
					values.add(line);
					LOG.info("line added: " + line);
				}
			}
			rd.close();

			final String response = content.toString();
			LOG.info("response recived:\n" + response);

			grabToken(response);

		} catch (final IOException ex) {
			LOG.log(Level.SEVERE, "", ex);

		} finally {
			close(connection, is, rd);

		}
	}

	/**
	 * Führt einen POST-Request mit den Daten zum Server aus.
	 * 
	 * @param address
	 *            die Adresse
	 * @param data
	 *            die Daten
	 */
	private void doPost(final String address, final Map<String, String> data) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader rd = null;
		try {
			final URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();

			setCookies(connection);

			final StringBuffer dataEncode = new StringBuffer();

			for (Entry<String, String> e : data.entrySet()) {
				if (0 < dataEncode.length()) {
					dataEncode.append("&");
				}
				dataEncode.append(URLEncoder.encode(e.getKey(), "UTF-8"));
				dataEncode.append("=");
				dataEncode.append(URLEncoder.encode(e.getValue(), "UTF-8"));
			}

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

			is = connection.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));

			grabCookies(connection);

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
			LOG.info("response recived: " + response);

			grabToken(response);

		} catch (final IOException ex) {
			LOG.log(Level.SEVERE, "", ex);

		} finally {
			close(connection, is, rd);

		}
	}

	/**
	 * Sucht den Token in der Nachricht und übernimmt diesen für die folgende
	 * Aktion.
	 * 
	 * @param response
	 *            die Nachricht
	 */
	private void grabToken(final String response) {
		final Pattern p = Pattern.compile("\"(token|lgtoken|edittoken)\":\"([^\"]+)\"");
		final Matcher m = p.matcher(response);
		if (m.find()) {
			token = m.group(2);
			LOG.info("token assigned: " + token);
		}
	}

	/**
	 * Liest die Kopfdaten der Nachricht und übernimmt vorhandene Cookie-Daten.
	 * 
	 * @param conn
	 *            die Verbindung
	 */
	private void grabCookies(URLConnection conn) {
		String headerName;
		for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
			if (headerName.equals("Set-Cookie")) {
				String cookie = conn.getHeaderField(i);
				cookie = cookie.substring(0, cookie.indexOf(';'));
				String name = cookie.substring(0, cookie.indexOf('='));
				String value = cookie.substring(cookie.indexOf('=') + 1, cookie.length());
				if (!value.equals("deleted")) {
					cookies.put(name, value);
					LOG.info("cookie assigned: " + name + " = " + value);
				}
			}
	}

	/**
	 * Setzt zuvor gespeicherte Cookie-Daten.
	 * 
	 * @param conn
	 *            die Verbindung
	 */
	protected void setCookies(URLConnection conn) {
		StringBuilder cookie = new StringBuilder(100);
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			cookie.append(entry.getKey());
			cookie.append("=");
			cookie.append(entry.getValue());
			cookie.append("; ");
		}
		conn.setRequestProperty("Cookie", cookie.toString());
		conn.setRequestProperty("User-Agent", USERAGENT);
	}

	/**
	 * Durchläuft die gespeicherten Werte und ersetzt die gewünschte Zeile. Die
	 * Änderung wird durch den Index und den neuen Wert (getrennt durch
	 * Doppelpunkt) angegeben. Wird kein neuer Wert gesetzt oder ist
	 * {@code change} gleich <code>null</code>, dann wird der gespeicherte Text
	 * geliefert.
	 * 
	 * @param change
	 *            die Änderung aus Index und Wert duch Doppelpunkt getrennt
	 * @return eine Zeichenkette, niemals <code>null</code>
	 */
	public String createText(final String change) {
		final StringBuffer x = new StringBuffer();
		final int match = (null == change ? -1 : change.indexOf(':'));
		int index = 0;
		final Iterator<String> it = values.iterator();
		while (it.hasNext()) {
			final String line = it.next();
			if (0 < index) {
				x.append("\n");
			}
			if (match == index) {
				x.append(change.substring(match + 1));
			} else {
				x.append(line);
			}
			index++;
		}
		return x.toString();
	}

	/**
	 * Schließt die Objekte ohne Ausnahmefehler. Wird eine Exception ausgelöst,
	 * wird eine Warnung protokolliert.
	 * 
	 * @param connection
	 *            die Verbindung
	 * @param is
	 *            die Eingabe
	 * @param rd
	 *            die Ausgabe
	 */
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
