package org.morphex.app;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;
import java.io.*;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.codec.EncoderException;

class TwitterStatusFetcher {
	Twitter twitter;
	ArrayList statuses = new ArrayList();
	Integer page = 1;
	Integer count = 20;

	TwitterStatusFetcher(Twitter twitter_) {
		twitter = twitter_;
	}

	Status getNextStatus() throws TwitterException {
		if (statuses.isEmpty()) {
			// See if more tweets can be found
			ResponseList results = twitter.getUserTimeline(
				new Paging(page, count));
			page++;
			statuses.addAll(results);
		}
		if (statuses.isEmpty()) {
			return null;
		}
		Status status = (Status) statuses.get(0);
		statuses.remove(0);
		return status;
	}
}

/**
 * Hello world!
 *
 */
public class App
{
    static ConfigurationBuilder config = null;
    static String outputXHTML = "";
    static ResolveRedirect resolver;
    static URLCodec URLEncoder = new URLCodec();

    public static String URLtoHTML(String URL) throws EncoderException {
	URL = URLEncoder.encode(URL);
        return "<a href='" + URL + "'>" + URL + "</a>";
    }

    public static String expandURL(URLEntity[] urls, String url) throws Exception {
        for (URLEntity JSONURL: urls) {
            if (JSONURL.getURL().toString().equals(url)) {
                return JSONURL.getExpandedURL().toString();
            }
        }
	if (url.startsWith("https://t.co")) {
		// We have a URL which hasn't been included in the
		// Tweet metadata
		String newURL = resolver.getRedirectFromURL(url);
		if (!newURL.trim().isEmpty()) {
		    return newURL;
		}
	}
        return url;
    }

    public static void main( String[] args ) throws Exception
    {
        System.out.println("Starting Twitter dump");
	config = new ConfigurationBuilder();
	config.setTweetModeExtended(true);
	// Not necessary to resolve URLs as expanded versions are included in the tweet
	// Update: Some shortened URLs are not included in Tweet metadata
	resolver = new ResolveRedirect("t.co");
	try {
	        setup_credentials();
		Twitter twitter = new TwitterFactory(config.build()).getInstance();
		set_access_tokens(twitter);
		System.out.print("Statuses: ");
		TwitterStatusFetcher fetcher = new TwitterStatusFetcher(twitter);
		Integer count = 0;
		Status status = fetcher.getNextStatus();
		while (status != null) {
			String statusText = status.getText();
			ArrayList urls = DetectURL.getURLs(statusText);
			outputXHTML = outputXHTML + "\n<div class='tweet'>" +
			  status.getCreatedAt().toString() + "&nbsp;" + 
			  StringEscapeUtils.escapeXml10(statusText) +
			  "</div>";
			String redirectURL = "";
			URLEntity[] shortenedURLs = status.getURLEntities();
			for (Object url : urls) {
				System.out.println("URL: " + url.toString() + "\n");
				// redirectURL = resolver.getRedirectFromURL(url.toString());
                                redirectURL = expandURL(shortenedURLs,url.toString());
				redirectURL = URLtoHTML(redirectURL);
				outputXHTML = outputXHTML.replace(url.toString(), redirectURL);
			}
			// System.out.print("Detected URLs: ");
			// System.out.print(String.join(",",urls));
			// System.out.print("\n");
			status = fetcher.getNextStatus();
			// Thread.sleep(2000);
			count += 1;
			// Safeguard for testing
			if (count >= 50 && true) {
				break;
			}
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
