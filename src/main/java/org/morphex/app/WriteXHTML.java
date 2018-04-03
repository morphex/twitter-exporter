import java.io.*;
import java.nio.charset.Charset;

public class WriteXHTML {
	static Charset utf = Charset.forName("UTF-8");
	static String header = new String("<?xml version='1.0' encoding='utf-8' ?><!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><title>Test</title></head><body>");
	static String footer = new String("</body></html>");

	public static void main(String[] args)  throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(
			"test.html"), "UTF-8");
		out.write(header);
		String test = new String("This is a test");
		out.write("This is a test");
		out.write(footer);
		out.close();
	}
}
