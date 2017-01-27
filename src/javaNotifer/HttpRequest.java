package javaNotifer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
	private static final String USER_AGENT = "Mozilla/5.0";
	
	public static String getRequest(String url, int port)throws Exception {
		URL obj = new URL(url + ":" + port);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		// request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		int responseCode = con.getResponseCode();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf8"));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		
		return response.toString();
	}
}
