package priv.wangao.LogAnalysis;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;

public class MyTest {

	public MyTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws URISyntaxException, MalformedURLException {
		System.out.println(System.getProperty("user.dir"));
		String packName = MyTest.class.getPackage().getName();
		System.err.println(packName);

		System.err.println(packName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));

		System.out.println(System.getProperty("user.dir") + File.separator
				+ packName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));

		URL url = MyTest.class.getResource("/");

		if (url != null) {
			System.err.println(new File(url.getPath()).getAbsolutePath());
		} else {
			System.err.println("->" + new File(MyTest.class.getProtectionDomain().getCodeSource().getLocation().toString()).getParent());
		}
	}

}
