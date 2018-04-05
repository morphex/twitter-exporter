package org.morphex.app;

import java.io.*;
import java.nio.charset.Charset;

public class WriteXHTML {
	static String charset_ = "UTF-16LE";
	// static Charset utf = Charset.forName(charset);
	static String header = new String("<?xml version='1.0' encoding='" + charset_ + "' ?><!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><title>Test</title></head><body>");
	static String footer = new String("</body></html>");

	public static byte[] e(String toEncode) throws Exception {
		return toEncode.getBytes(charset_);
	}

	public static void writeFile(String html) throws Exception {
		FileOutputStream out = new FileOutputStream("test.html");
		out.write(e(header));
		out.write(e(html));
		out.write(e(footer));
		out.close();
	}

	public static void main(String[] args)  throws Exception {
		writeFile(("This is a test"));

/*
// Disabled because W3C validator won't validate with BOM
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
*/
	}
}
