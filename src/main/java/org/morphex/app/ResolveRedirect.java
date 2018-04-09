import javax.net.ssl.*;
import java.io.*;

class SSLConnection {
	SSLSocket socket;
	PrintWriter send;
	BufferedReader receive;
	String host;

	public SSLConnection(String host_) throws Exception {
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket)factory.createSocket(host_, 443);
		socket.startHandshake();
		host = host_;
		send = new PrintWriter(new BufferedWriter(
			new OutputStreamWriter(socket.getOutputStream())));
		receive = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
	}

	String getRedirect(String path) throws IOException {
		send.println("GET " + path + " HTTP/1.1");
		send.println("Host: " + host);
		send.println();
		send.flush();
		String line = "";
		String location = "";
		do {
			// System.out.println(line);
			// System.out.println("Before readLine..");
			line = receive.readLine();
			if (line.toLowerCase().startsWith("location:")) {
				location = line.split("\\s")[1];
			}
		} while (!line.trim().isEmpty());
		return location;
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

	public static void main(String[] args) throws Exception {
		SSLConnection connection = new SSLConnection("t.co");
		System.out.println("Lo: " +
			connection.getRedirect("/MdN9Zjwo43"));
		connection = null;
	}
}
