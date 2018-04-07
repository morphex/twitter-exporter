package org.morphex.app;

import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.linkedin.urls.Url;
import java.util.*;

public class DetectURL {
	public static void main(String[] args) {
		print();
	}
        public static void print() {
		String data = "asdf https://t.co fdsa";
		UrlDetector parser = new UrlDetector(data,
			UrlDetectorOptions.Default);
		List<Url> urls = parser.detect();

		for (Url url : urls) {
			System.out.print("URL: ");
			System.out.println(url);
		}

        }
}
