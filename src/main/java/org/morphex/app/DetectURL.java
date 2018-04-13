package org.morphex.app;

import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.linkedin.urls.Url;
import java.util.*;

public class DetectURL {
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
