package common.utilities;

import common.launchsetup.BaseTest;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HTTPResponse {
	static URL url = null;
	static String target = null;

	public static String getLocationFor301Or302StatusCode1(String currentUrl) {
		target = "";
		System.setProperty("jsse.enableSNIExtension", "false");
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36";
		if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
			userAgent = "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36";
		URL url = null;
		try {
			url = new URL(currentUrl);
		} catch (MalformedURLException e) {
			System.out.println("Failed while checking for ==> " + currentUrl);
			e.printStackTrace();
		}
		HttpURLConnection http = null;
		try {
			http = (HttpURLConnection) url.openConnection();
			http.setInstanceFollowRedirects(false);
			HttpURLConnection.setFollowRedirects(false);
			http.setConnectTimeout(15000);
		} catch (IOException e1) {
			System.out.println("Failed while checking for ==> " + currentUrl);
			e1.printStackTrace();
		}
		try {
			http.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.out.println("Failed while checking for ==> " + currentUrl);
			e.printStackTrace();
		}
		http.addRequestProperty("User-Agent", userAgent);
		try {
			http.connect();
		} catch (IOException e) {
			System.out.println("Failed while checking for ==> " + currentUrl);
			e.printStackTrace();
		}
		int status = 0;

		try {
			status = http.getResponseCode();

			System.out.println("status:" + status);
			try {
				if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
					target = http.getHeaderField("Location");
					return getLocationFor301Or302StatusCode(target);
				} else {
					target = status == 200 ? currentUrl : "";
				}
			} catch (Exception e) {
				System.out.println("Failed while checking for ==> " + currentUrl);
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("this is the target returned: " + target);
		return target;
	}

	/*
	 * public static int checkResponseCode1(String href) { if
	 * (!href.startsWith("http")) { href = "https:" + href; }
	 * 
	 * System.setProperty("https.protocols", "TLSv1.3,TLSv1.2,TLSv1.1,TLSv1,SSLv3");
	 * System.setProperty("jsse.enableSNIExtension", "false"); String userAgent =
	 * "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
	 * ;
	 * 
	 * if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
	 * userAgent =
	 * "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1"
	 * ;
	 * 
	 * try { url = new URL(href); } catch (MalformedURLException |
	 * IllegalArgumentException e) {
	 * System.out.println("Failed while checking for ==> " + href);
	 * e.printStackTrace(); return 0; } HttpURLConnection http = null; try { http =
	 * (HttpURLConnection) url.openConnection(); http.setConnectTimeout(15000);
	 * http.setInstanceFollowRedirects(false); } catch (IOException |
	 * IllegalArgumentException e1) {
	 * System.out.println("Failed while checking for ==> " + href);
	 * e1.printStackTrace(); } try { http.setRequestMethod("GET"); } catch
	 * (ProtocolException | IllegalArgumentException e) {
	 * System.out.println("Failed while checking for ==> " + href);
	 * e.printStackTrace(); } http.addRequestProperty("User-Agent", userAgent);
	 * 
	 * try { http.connect(); } catch (IOException | IllegalArgumentException e) {
	 * System.out.println("Failed while checking for ==> " + href);
	 * e.printStackTrace(); return 0; } int responseCode = 0; try { responseCode =
	 * http.getResponseCode(); } catch (IOException | IllegalArgumentException e1) {
	 * // TODO Auto-generated catch block e1.printStackTrace(); } try { int counter
	 * = 0; while (responseCode != 200 && counter < 5) {
	 * 
	 * if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode ==
	 * HttpURLConnection.HTTP_MOVED_TEMP) { URL base = new URL(href); String
	 * location = http.getHeaderField("Location").replaceAll(" ", "%20");
	 * 
	 * URL next = new URL(base, location); href = next.toExternalForm(); url = new
	 * URL(href); http = (HttpURLConnection) url.openConnection(); http.connect(); }
	 * try { responseCode = http.getResponseCode(); } catch (Exception e) {
	 * 
	 * } counter++; } } catch (Exception e) {
	 * System.out.println("Failed while checking for ==> " + href);
	 * e.printStackTrace(); responseCode = 0; } return responseCode; }
	 */

	public static int checkResponseCodeExact(String href) {
		if (!href.startsWith("http")) {
			href = "https:" + href;
		}
		System.setProperty("jsse.enableSNIExtension", "false");
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36";

		if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
			userAgent = "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36";

		try {
			url = new URL(href);
		} catch (MalformedURLException | IllegalArgumentException e) {
			System.out.println("Failed while checking for ==> " + href);
			e.printStackTrace();
			return 0;
		}
		HttpURLConnection http = null;
		try {
			http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(15000);
			http.setInstanceFollowRedirects(false);
		} catch (IOException | IllegalArgumentException e1) {
			System.out.println("Failed while checking for ==> " + href);
			e1.printStackTrace();
		}
		try {
			http.setRequestMethod("GET");
		} catch (ProtocolException | IllegalArgumentException e) {
			System.out.println("Failed while checking for ==> " + href);
			e.printStackTrace();
		}
		http.addRequestProperty("User-Agent", userAgent);

		try {
			http.connect();
		} catch (IOException | IllegalArgumentException e) {
			System.out.println("Failed while checking for ==> " + href);
			e.printStackTrace();
			return 0;
		}
		int responseCode = 0;
		try {
			responseCode = http.getResponseCode();
		} catch (IOException | IllegalArgumentException e1) {
			e1.printStackTrace();
		}
		return responseCode;
	}

	public static URL getUrl() {
		return url;
	}

	public static int checkResponseCode(String href) {
		int statusCode = 0;
		if (!href.startsWith("http")) {
			href = "https:" + href;
		}
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

		if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
			userAgent = "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36";
		try {
			Response response = Jsoup.connect(href)
					.userAgent(userAgent).ignoreContentType(true).timeout(20000).execute();
			statusCode = response.statusCode();
		} catch (Exception e) {
			System.out.println("Failed while checking for ==> " + href);
			e.printStackTrace();
		}
		return statusCode;
	}

	public static String getLocationFor301Or302StatusCode(String currentUrl) {
		target = "";
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
		if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
			userAgent = "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36";
		try {
			Response response = Jsoup.connect(currentUrl).userAgent(userAgent).followRedirects(false).execute();
			target = response.header("Location");
			if (currentUrl.contains("liveblog")) {
				target = currentUrl;
			}
		} catch (Exception e) {
			System.out.println("Failed while checking for ==> " + currentUrl);
			e.printStackTrace();
		}
		System.out.println("this is the target returned: " + target);
		return target;
	}

}
