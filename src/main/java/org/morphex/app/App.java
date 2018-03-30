package org.morphex.app;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import java.util.List;
import java.io.*;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println("Starting Twitter dump");
	try {
	        setup_credentials();
		Twitter twitter = new TwitterFactory().getInstance();
		set_access_tokens(twitter);
		System.out.println("Statuses:");
		for (Status status : twitter.getUserTimeline()) {
			System.out.println(status.getText());
			System.out.println("----------------------");
		}
	} catch (TwitterException exception) {
		exception.printStackTrace();
		System.exit(-1);
	} catch (IOException exception) {
		exception.printStackTrace();
		System.exit(-1);
	}
    }

    private static void setup_credentials() {
      File file = new File("./credentials.txt");
      if (!file.isFile()) {
        // Setup authentication
        System.out.println("credentials.txt missing");
	System.out.println("Format: Consumer key<newline>Consumer secret<newline>Token<newline>Secret<newline>");
	System.exit(-1);
      }
    }

    private static void set_access_tokens(Twitter twitter) throws IOException {
	FileReader file = new FileReader("./credentials.txt");
	BufferedReader credentials = new BufferedReader(file);
	String consumer_key = credentials.readLine().trim();
	String consumer_secret = credentials.readLine().trim();
        String token = credentials.readLine().trim();
        String secret = credentials.readLine().trim();
        if (consumer_key.isEmpty() || consumer_secret.isEmpty() ||
	    token.isEmpty() || secret.isEmpty()) {
		System.out.println("One or more empty credentials");
		System.exit(-1);
        }
	System.out.println(consumer_key);
	System.out.println(consumer_secret);
	System.out.println(token);
	System.out.println(secret);
	twitter.setOAuthConsumer(consumer_key, consumer_secret);
	twitter.setOAuthAccessToken(new AccessToken(token, secret));

    }
}
