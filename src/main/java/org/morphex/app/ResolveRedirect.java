import javax.net.ssl.*;
import java.io.*;

public class ResolveRedirect {
	public static void main(String[] args) throws Exception {
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket)factory.createSocket("t.co", 443);
		socket.startHandshake();
		PrintWriter send = new PrintWriter(new BufferedWriter(
			new OutputStreamWriter(socket.getOutputStream())));
		send.println("GET /TQuFITDB4r HTTP/1.0");
		send.println("Host: t.co:443");
		send.println();
		send.flush();
		BufferedReader receive = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
		String line = "";
		do {
			System.out.println(line);
			line = receive.readLine();
		} while (line != null);
		send.close();
		receive.close();
		socket.close();
	}
}
