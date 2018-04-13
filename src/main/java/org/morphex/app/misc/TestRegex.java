import java.util.regex.*;

public class TestRegex {
	static String test = "https://t.co/ab :)https://t.co/ba :ohttps://t.co/";
	static Pattern re = Pattern.compile("(http|https)://.*?(\\z|$|\\s)");
	public static void main(String[] args) {
		Matcher matcher = re.matcher(test);
		while (matcher.find()) {
			System.out.println(test.substring(
				matcher.start(),
				matcher.end()
			));
		}
	}
}
