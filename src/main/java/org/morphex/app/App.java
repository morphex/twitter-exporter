package org.morphex.app;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;
import java.io.*;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Hello world!
 *
 */
public class App
{
    public static String URLtoHTML(String URL) {
        return "<a href='" + URL + "'>" + URL + "</a>";
    }

    public static void main( String[] args )
    {
        System.out.println("Starting Twitter dump");
	String outputXHTML = "";
	ConfigurationBuilder config = new ConfigurationBuilder();
	config.setTweetModeExtended(true);
	ResolveRedirect resolver = null;
	try {
		resolver = new ResolveRedirect("t.co");
	} catch (Exception exeption) {
		assert true;
	}
	try {
	        setup_credentials();
		Twitter twitter = new TwitterFactory(config.build()).getInstance();
		set_access_tokens(twitter);
		System.out.println("Statuses:");
		for (Status status : twitter.getUserTimeline()) {
			System.out.println(status.getText());
			System.out.println("----------------------");
			String statusText = status.getText();
			ArrayList urls = DetectURL.getURLs(statusText);
			outputXHTML = outputXHTML + "\n<div class='tweet'>" +
			  StringEscapeUtils.escapeXml10(statusText) +
			  "</div>";
			String redirectURL = "";
			for (Object url : urls) {
				redirectURL = resolver.getRedirectFromURL(url.toString());
				redirectURL = URLtoHTML(redirectURL);
				outputXHTML = outputXHTML.replace(url.toString(), redirectURL);
			}
			System.out.print("Detected URLs: ");
			System.out.print(String.join(",",urls));
			System.out.print("\n");
		}
		WriteXHTML.writeFile(outputXHTML);
	} catch (TwitterException exception) {
		exception.printStackTrace();
		System.exit(-1);
	} catch (IOException exception) {
		exception.printStackTrace();
		System.exit(-1);
	} catch (Exception exception) {
		exception.printStackTrace();
		System.exit(-1);
	} finally {
		// To write hash cache stash
		if (resolver != null) {
			try {
				resolver.flush();
			} catch (Exception exception) {
				assert true;
			}
			resolver = null;
		}
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
