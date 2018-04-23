package org.morphex.app;

import java.io.*;
import java.nio.charset.Charset;

public class WriteXHTML {
	static String charset_ = "UTF-16LE";
	// static Charset utf = Charset.forName(charset);
	static String header1 = new String("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml' lang='");
	static String header2 = new String("' xml:lang='");
	static String header3 = new String("'><head><title>Test</title>");
	// Validator https://validator.w3.org/i18n-checker/ says one
	// should use meta charset tag instead of http-equiv.
	// static String header4 = new String("<meta http-equiv='content-type' content='text/xhtml; charset=");
	static String header4 = new String("<meta charset='");
	static String header5 = new String("' />");
	static String header6 = new String("</head><body>");
	static String footer = new String("</body></html>");
	static String language = new String("en");

	public static String getHeader() {
		String charset__ = "";
		// W3C HTML validator gives weird errors, but
		// https://validator.w3.org/i18n-checker/
		// explains that UTF-16 should be specified
		// as an encoding inside the document, and a
		// BOM should indicate the endian orientation
		//
		// Commented out as W3C validator complains about
		// UTF-16 in meta http-equiv content-type and charset.
		/*
		if (charset_.equals("UTF-16LE") || 
		    charset_.equals("UTF-16BE")) {
			charset__ = "UTF-16";
		} else {
			charset__ = charset_;
		}
		*/
		/*
		return header1 + language + header2 + language + header3 +
			header4 + charset__ + header5 + header6;
		*/
		return header1 + language + header2 + language + header3 +
			header6;
	}

	public static byte[] e(String toEncode) throws Exception {
		return toEncode.getBytes(charset_);
	}

	public static void writeFile(String html) throws Exception {
		FileOutputStream out = new FileOutputStream("test.html");
		if (charset_ == "UTF-16LE") {
			out.write((byte) 0xFF);
			out.write((byte) 0xFE);
		} else if (charset_ == "UTF-32LE") {
			out.write((byte) 0xFF);
			out.write((byte) 0xFE);
			out.write((byte) 0x00);
			out.write((byte) 0x00);
		} else if (charset_ == "UTF-8") {
			out.write((byte) 0xEF);
			out.write((byte) 0xBB);
			out.write((byte) 0xBF);
		} else {
			throw new java.lang.Error("Unsupported encoding for unicode BOM");
		}
		out.write(e(getHeader()));
		out.write(e(html));
		out.write(e(footer));
		out.close();
	}

	public static void main(String[] args)  throws Exception {
		writeFile(("This is a test"));

	}
}
