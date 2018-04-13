package org.morphex.app;

import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.linkedin.urls.Url;
import java.util.*;
import java.util.regex.*;

public class DetectURL {
	static Pattern re_url = Pattern.compile("(http|https)://.*?(\\z|$|\\s)");

	public static void main(String[] args) {
		print();
	}
	public static ArrayList getURLs(String text) {
		UrlDetector parser = new UrlDetector(text,
			UrlDetectorOptions.Default);
		List<Url> urls = parser.detect();
		ArrayList URLStrings = new ArrayList();
		for (Url url : urls) {
			URLStrings.add(url.toString());
		}
                Matcher matcher = re_url.matcher(text);
		String mURL = "";
                while (matcher.find()) {
                        mURL = text.substring(matcher.start(), matcher.end()).trim();
			if (!URLStrings.contains(mURL)) {
				URLStrings.add(mURL);
			}
                }
		return URLStrings;
	}
        public static void print() {
		// Test code
		String data = "asdf https://t.co fdsa :)https://t.co/ff";
		UrlDetector parser = new UrlDetector(data,
			UrlDetectorOptions.Default);
		List<Url> urls = parser.detect();

		for (Url url : urls) {
			System.out.print("URL: ");
			System.out.println(url);
		}

        }
}
