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

	public static void main(String[] args)  throws Exception {
		FileOutputStream out = new FileOutputStream("test.html");
		out.write(e(header));
		String test = new String("This is a test");
		out.write(e("This is a test"));
		out.write(e(footer));
		out.close();
	}
}
