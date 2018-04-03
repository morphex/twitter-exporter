import java.io.*;

public class WriteXHTML {
	static String header = "<?xml version='1.0' encoding='utf-16' ?><!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><title>Test</title></head><body>";
	static String footer = "</body></html>";

	public static void main(String[] args)  throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(
			"test.html"), "UTF-16");
		out.write(header);
		out.write("This is a test");
		out.write(footer);
		out.close();
	}
}
