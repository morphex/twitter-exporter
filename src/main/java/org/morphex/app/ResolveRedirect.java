package org.morphex.app;

import javax.net.ssl.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Set;
import java.util.Scanner;
import java.net.URL;

class HashCache {

	Hashtable<String,String> stash;
	String filecache = null;

	public HashCache() {
		stash = new Hashtable<String, String>();
	}
	public HashCache(String file) throws Exception {
		stash = new Hashtable<String, String>();
		loadFromFile(file);
		filecache = file;
	}

	public Boolean containsKey(String key) {
		return stash.containsKey(key);
	}

	public String get(String key) {
		return stash.get(key);
	}

	public void put(String key, String value) {
		stash.put(key, value);
		return;
	}

	private void loadFromFile(String file) throws Exception {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file));
		} catch (Exception exception) {
			throw exception;
		}
		String line;
		String[] parts;
		try {
		while ((line = scanner.nextLine()) != null) {
			parts = line.split("\\s");
			stash.put(parts[0], parts[1]);
		}
		} catch (Exception exception) { assert true; }
	}

	private void writeToFile() throws IOException {
		FileWriter file_ = new FileWriter(filecache);
		Set<String> keys = stash.keySet();
		for (String key:keys) {
			file_.write(key);
			file_.write(" ");
			file_.write(stash.get(key));
			file_.write("\n");
			file_.flush();
		}
		file_.close();
	}

	public void flush() throws Exception {
		if (filecache != null) {
			writeToFile();
		}
	}
}

class SSLConnection {
	SSLSocket socket;
	PrintWriter send;
	BufferedReader receive;
	String host;
	long lastRequest = 0;
	int throttleSeconds = 1;
	HashCache stash;

	public SSLConnection(String host_) throws Exception {
		host = host_;
		initializeConnection(host_);
		stash = new HashCache("hash.cache");
	}

	public void initializeConnection(String host_) throws Exception {
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket)factory.createSocket(host_, 443);
		socket.startHandshake();
		send = new PrintWriter(new BufferedWriter(
			new OutputStreamWriter(socket.getOutputStream())));
		receive = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
	}

	void waitForRequest() throws Exception {
		// Also a place to place lock for multi-thread access
		while ((System.currentTimeMillis() / 1000L) <
			(lastRequest + throttleSeconds)) {
			Thread.sleep(1000);
		}
		lastRequest = System.currentTimeMillis() / 1000L;
	}

	String getRedirectFromURL(String url) throws Exception {
		// Only gets the path, host has been set earlier
		URL url_ = new URL(url);
		if (!url_.getHost().equals(host)) {
			System.out.println("Skipping URL " + url);
			System.out.println(url_.getHost());
			System.out.println(host);
			flush(); // Save URLs so far
			System.exit(-1);
			return url;
		}
		try {
			return getRedirect(url_.getPath());
		} catch (javax.net.ssl.SSLException exception) {
			System.out.print("Failed on: " + url + "\n");
			// Connection, failure, wait 5 seconds and re-open connection
			Thread.sleep(5000);
			initializeConnection(host);
			return url;
		} catch (Exception exception) {
			System.out.print("Failed on: " + url + "\n");
			flush();
			throw exception;
		}
	}

	String getRedirect(String path) throws Exception {
		if (stash.containsKey(path)) {
			return stash.get(path);
		}
		waitForRequest();
		try {
			send.println("GET " + path + " HTTP/1.1");
			send.println("Host: " + host);
			send.println();
			send.flush();
		} catch (Exception exception) {
			System.out.println("Exception in fetching redirect URL");
	                exception.printStackTrace();
			// So all URLs so far are saved
			flush();
			throw exception; // So we can see longer up what the full URL is for example
		}
		String line = "";
		String location = "";
		do {
			// System.out.println(line);
			// System.out.println("Before readLine..");
			try {
				line = receive.readLine();
				if (line.toLowerCase().startsWith("location:")) {
					location = line.split("\\s")[1];
					if (location.trim().isEmpty()) {
						System.out.println("Empty location: " + line);
						Thread.sleep(10000);
					}
				}
			} catch (Exception exception) {
				System.out.println("Exception in fetching redirect URL");
		                exception.printStackTrace();
				// So all URLs so far are saved
				flush();
				throw exception; // So we can see longer up what the full URL is for example
			}
		} while (!line.trim().isEmpty());
		stash.put(path, location);
		return location;
	}

	public void flush() throws Exception {
		stash.flush();
	}

	protected void finalize() throws Throwable {
		try {
			send.close();
			receive.close();
			socket.close();
		} finally {
			super.finalize();
		}

	}
}

public class ResolveRedirect {
	SSLConnection connection;

	public String getRedirect(String path) throws Exception {
		return connection.getRedirect(path);
	}

	public String getRedirectFromURL(String url) throws Exception {
		return connection.getRedirectFromURL(url);
	}

	public ResolveRedirect(String host) throws Exception {
		connection = new SSLConnection(host);
	}
	public void flush() throws Exception {
		connection.flush();
	}

	public static void main(String[] args) throws Exception {
		SSLConnection connection = new SSLConnection("t.co");
		System.out.println("Lo: " +
			connection.getRedirect("/MdN9Zjwo43"));
		System.out.println("Lo: " +
			connection.getRedirect("/y21rFVTytt"));
		System.out.println("Lo: " +
			connection.getRedirectFromURL("https://t.co/PpiGpYBpBw"));
		connection.flush();
		connection = null;
	}
}
