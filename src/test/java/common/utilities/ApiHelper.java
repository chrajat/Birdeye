package common.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.launchsetup.Config;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiHelper {
	private static String output;
	private static JSONObject jsonObject;

	public static int httpResponseCode(String url_0) {
		URL url = null;
		try {
			url = new URL(url_0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection http = null;
		try {
			http = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			http.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		try {
			http.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int responseCode = 0;
		try {
			responseCode = http.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	public static String getAPIResponse(String api) {
		try {

			URL url = new URL(api);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " for url: " + api);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			// System.out.println("Output from Server .... \n");
			output = br.readLine();
			System.out.println("Fetched from server for " + api);
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return output;
	}

	public static String getXMLData(String api,File file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(api).openStream());
			TransformerFactory transformerFactory= TransformerFactory.newInstance();
			Transformer xform = transformerFactory.newTransformer();
			xform.transform(new DOMSource(doc), new StreamResult(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return api;
	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, ArrayList<String>> getApiResponseArray(String output) {
		ObjectMapper oMapper = new ObjectMapper();
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(output);
			System.out.println(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkedHashMap<String, ArrayList<String>> map = json != null ? oMapper.convertValue(json, LinkedHashMap.class)
				: new LinkedHashMap<String, ArrayList<String>>();
		System.out.println("map " + map);
		return (map);
	}

	public static List<String> getNewsItems(String output, String inKey1, String inKey2, String outKey) {
		String allowedTypes = "audio,video,slide,news,web,primeplus";
		String articleTypes = "podcast,slideshow,articleshow,videoshow,economictimes.indiatimes.com";
		List<String> newsSV = Arrays.asList(allowedTypes.split("\\s*,\\s*"));
		List<String> listArticleTypes = Arrays.asList(articleTypes.split("\\s*,\\s*"));
		List<String> values = new LinkedList<>();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // the location of the file
		JSONArray jarray = null;
		if (!obj.getClass().getName().contains("JSONObject")) {
			jarray = (JSONArray) obj;
			for (Object arr : jarray) {
				jsonObject = (JSONObject) arr;
				String temp = (String) jsonObject.get(outKey);
				values.add(temp);
			}
		}

		else {
			jsonObject = (JSONObject) obj;
			if (jsonObject.get(inKey1).getClass().getName().contains("org.json.simple.JSONArray")) {
				jarray = (JSONArray) jsonObject.get(inKey1);
				try {
					String array = jarray.toJSONString();
					if (inKey2.trim().length() == 0) {
						if (jsonObject.get(inKey1).getClass().getName().contains("JSONArray")) {
							jarray = (JSONArray) parser.parse(array);
							for (Object element : jarray) {
								JSONObject jElement = (JSONObject) element;
								String temp = (String) jElement.get(outKey);
								if (temp != null)
									values.add(temp);
							}
						} else {
							jsonObject = (JSONObject) jsonObject.get(outKey);
							values.add(jsonObject.toJSONString());
						}
					} else {
						for (Object arr : jarray) {
							jsonObject = (JSONObject) arr;
							jarray = (JSONArray) jsonObject.get(inKey2);
							jarray = jarray == null ? jarray = new JSONArray() : jarray;
							for (Object ja : jarray) {
								jsonObject = (JSONObject) ja;
								String temp = (String) jsonObject.get(outKey);
								if (outKey.equals("da") && ((String) jsonObject.get("upd") != null
										&& ((String) jsonObject.get("upd")).trim().length() > 0))
									temp = (String) jsonObject.get("upd");
								try {
									if (newsSV.stream().filter(o -> o.equals(jsonObject.get("sv").toString()))
											.findFirst().isPresent()
											&& (listArticleTypes.stream()
													.filter(o -> jsonObject.get("wu").toString().contains(o))
													.findFirst().isPresent())
											|| outKey.equals("wu")) {

										if (temp != null)
											values.add(temp);

									}
								} catch (Exception ex) {
									if (temp != null)
										values.add(temp);

								}

							}
						}
					}
				} catch (Exception e) {
					System.out.println("Exception:" + e.getMessage());

				}
			}

			else {
				jsonObject = (JSONObject) jsonObject.get(inKey1);
				if (inKey2.trim().length() > 1) {
					if (jsonObject.get(inKey2).getClass().getName().contains("JSONArray")) {
						jarray = (JSONArray) jsonObject.get(inKey2);
						for (Object ja : jarray) {

							jsonObject = (JSONObject) ja;

							String temp = (String) jsonObject.get(outKey);
							values.add(temp);
						}
					} else {
						String temp = (String) jsonObject.get(outKey);
						values.add(temp);
					}
				} else {
					String temp = (String) jsonObject.get(outKey);
					values.add(temp);
				}
			}
		}
		return values;

	}

	public static String getChildValueInheritTree(String output, String inKey1, String inKey2, String identifyingKey1,
			String identifyingValue1, String identifyingKey2, String identifyingValue2, String outKey) {
		JSONParser parser = new JSONParser();
		String value = "";
		Object obj = null;
		try {
			obj = parser.parse(output);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // the location of the file
		jsonObject = (JSONObject) obj;
		JSONArray jarray = null;

		Object obj1 = jsonObject.get(inKey1);
		if (jsonObject.get(inKey1).getClass().getName().contains("org.json.simple.JSONArray")) {
			jarray = (JSONArray) jsonObject.get(inKey1);
			try {
				boolean flag = false;
				for (Object arr : jarray) {
					jsonObject = (JSONObject) arr;
					// identify parent
					if (((String) jsonObject.get(identifyingKey1)).equals(identifyingValue1)) {
						if (inKey2.trim().length() > 0
								&& jsonObject.get(inKey2).getClass().getName().contains("org.json.simple.JSONArray")) {
							jarray = (JSONArray) jsonObject.get(inKey2);
							jarray = jarray == null ? jarray = new JSONArray() : jarray;
							if (identifyingKey2.trim().length() < 1 && identifyingValue2.trim().length() < 1) {
								value = ((String) jsonObject.get(outKey)) == null ? ""
										: (String) jsonObject.get(outKey);
								flag = value.trim().length() > 1 ? true : false;
							}
							for (Object ja : jarray) {
								jsonObject = (JSONObject) ja;
								if (identifyingValue2.trim().length() > 1
										&& ((String) jsonObject.get(identifyingKey2)).equals(identifyingValue2)) {
									value = ((String) jsonObject.get(outKey));
									flag = true;
									break;
								}
							}
						} else {
							value = ((String) jsonObject.get(outKey));
						}
						if (flag)
							break;

					}
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e.getMessage());
			}
		} else {
			value = ((String) jsonObject.get(outKey));

		}

		return value;

	}

	public static List<String> getValuesFromJSONResponse(String output, String outKey, String identifyingKey,
			String identifyingKVal, String... inKeys) {
		// System.out.println("\n\nWith identifying key " + identifyingKVal);
		JSONParser parser = new JSONParser();
		Object obj = null;
		List<String> li = new LinkedList<>();
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // the location of the file
		List<JSONObject> liJson = new LinkedList<JSONObject>();
		if (obj.getClass().getName().contains("org.json.simple.JSONArray")) {
			liJson = jsonArrayToObject((JSONArray) obj);

		} else {
			jsonObject = (JSONObject) obj;

			liJson.add(jsonObject);
			for (String key : inKeys) {
				// System.out.println("With key:" + key);
				for (JSONObject o : liJson) {
					// System.out.println("With JsonObject:" + o);
					liJson = new LinkedList<JSONObject>();
					if (o.get(key) == null) {
						if (o.get(identifyingKey) != null) {

							if (((String) o.get(identifyingKey)).equalsIgnoreCase(identifyingKVal)) {
								break;
							} else
								continue;

						} else {
							if (identifyingKey.trim().length() == 0)
								li.add((String) o.get(outKey));

							continue;
						}
					}
					if (o.get(key).getClass().getName().contains("org.json.simple.JSONArray")) {
						liJson = jsonParser(o, key);

						if (o.get(identifyingKey) != null) {
							if (((String) o.get(identifyingKey)).equalsIgnoreCase(identifyingKVal)) {
								break;
							} else
								continue;
						} else
							continue;

					}

					else
						liJson.add((JSONObject) o.get(key));

				}
			}
		}
		liJson.forEach(jsObject -> {
			if (identifyingKey.trim().length() > 0) {
				String temp = (String) jsObject.get(outKey);
				if (temp != null && temp.trim().length() > 0)
					li.add(temp);

			} else {
				String temp = (String) jsObject.get(outKey);
				if (temp != null && temp.trim().length() > 0)
					li.add(temp);
			}

		});

		return li;
	}

	private static List<JSONObject> jsonParser(JSONObject obj, String key) {
		// System.out.println("jsonParser Returning object " + obj + "\nkey:" +
		// key);
		List<JSONObject> liJson = new LinkedList<JSONObject>();
		JSONArray jarray = (JSONArray) obj.get(key);
		for (Object arrObj : jarray) {
			try {
				jsonObject = (JSONObject) arrObj;
				liJson.add(jsonObject);
			} catch (ClassCastException e) {
				continue;
			}
		}
		// System.out.println("jsonParser Returning json " + liJson + "\n" +
		// liJson.size());
		return liJson;
	}

	private static List<JSONObject> jsonArrayToObject(JSONArray jsonArray) {
		List<JSONObject> liJSONObject = new LinkedList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			liJSONObject.add((JSONObject) jsonArray.get(i));
		}
		return liJSONObject;

	}

	public static List<String> getDataFromAllPages(String api, String outKey, String level1Key) {
		List<String> completeList = new LinkedList<String>();
		try {
			api = String.format(api, Config.fetchConfigProperty("APIPlatformName"),
					Config.fetchConfigProperty("ETAppVersion"));
			String output = ApiHelper.getAPIResponse(api);
			String tp = getValuesFromJSONResponse(output, "tp", "", "", "pn").iterator().next();
			// setCompleteList(new LinkedList<String>());
			for (int i = 1; i <= Integer.parseInt(tp); i++) {
				try {
					output = ApiHelper.getAPIResponse(api + "&curpg=" + i);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
				List<String> returnedLi = getValuesFromJSONResponse(output, outKey, "", "", level1Key);
				completeList.addAll(returnedLi);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("returning: " + completeList);
		return completeList;
	}

	public static List<String> getValueFromAPI(String api, String outKey, String identifyingKey, String identifyingKVal,
			String... inKeys) {
		output = ApiHelper.getAPIResponse(String.format(api, Config.fetchConfigProperty("APIPlatformName"),
				Config.fetchConfigProperty("ETAppVersion")));
		List<String> apiResult = ApiHelper.getValuesFromJSONResponse(output, outKey, identifyingKey, identifyingKVal,
				inKeys);
		return apiResult;
	}

	public static String getOutput() {
		return output;
	}

	public static List<String> getListOfAllValues(String output, String outKey, String identifyingKey,
			String identifyingKVal, String... inKeys) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		List<String> li = new LinkedList<>();
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // the location of the file
		jsonObject = (JSONObject) obj;
		List<JSONObject> liJson = new LinkedList<JSONObject>();
		liJson.add(jsonObject);
		for (String key : inKeys) {
			for (JSONObject o : liJson) {
				liJson = new LinkedList<JSONObject>();
				if (o.get(key) == null)
					continue;
				if (o.get(key).getClass().getName().contains("org.json.simple.JSONArray"))
					liJson = jsonParser(o, key);
				else {
					liJson.add((JSONObject) o.get(key));
				}
				// }
			}
		}
		liJson.forEach(jsObject -> {
			if (identifyingKey.trim().length() > 0) {
				if (jsObject.get(identifyingKey) != null
						&& ((String) jsObject.get(identifyingKey)).equalsIgnoreCase(identifyingKVal)) {
					String temp = jsObject.get(outKey) + "";
					if (temp != null && temp.trim().length() > 0)
						li.add(temp);
				}
			} else {
				String temp = jsObject.get(outKey) + "";
				if (temp != null && temp.trim().length() > 0)
					li.add(temp);
			}
		});
		return li;
	}

	public static List<String> getListOfAllValues_contains(String output, String outKey, String identifyingKey,
			String identifyingKValContains, String... inKeys) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		List<String> li = new LinkedList<>();
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // the location of the file
		jsonObject = (JSONObject) obj;
		List<JSONObject> liJson = new LinkedList<JSONObject>();
		liJson.add(jsonObject);
		for (String key : inKeys) {
			for (JSONObject o : liJson) {
				liJson = new LinkedList<JSONObject>();
				if (o.get(key) == null)
					continue;
				if (o.get(key).getClass().getName().contains("org.json.simple.JSONArray"))
					liJson = jsonParser(o, key);
				else {
					liJson.add((JSONObject) o.get(key));
				}
				// }
			}
		}
		liJson.forEach(jsObject -> {
			if (identifyingKey.trim().length() > 0) {
				if (jsObject.get(identifyingKey) != null
						&& ((String) jsObject.get(identifyingKey)).contains(identifyingKValContains)) {
					String temp = jsObject.get(outKey) + "";
					if (temp != null && temp.trim().length() > 0)
						li.add(temp);
				}
			} else {
				String temp = jsObject.get(outKey) + "";
				if (temp != null && temp.trim().length() > 0)
					li.add(temp);
			}
		});
		return li;
	}

	public static List<String> getDataForBreakingNews(String api) {
		output = ApiHelper.getAPIResponse(String.format(api, Config.fetchConfigProperty("APIPlatformName")));
		Boolean isBreakingNews = Boolean
				.parseBoolean(getValuesFromJSONResponse(output, "Status", "", "", "").iterator().next());
		List<String> liNews = new LinkedList<>();
		if (isBreakingNews)
			liNews.addAll(getValuesFromJSONResponse(output, "News", "", "", "BreakingNews"));
		return liNews;
	}


	public static List<String> getPremiumArticleList(String output, String inKey1, String inKey2, String outKey) {
		List<String> values = new LinkedList<>();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		JSONArray jarray = null;

		jsonObject = (JSONObject) obj;
		if (jsonObject.get(inKey1).getClass().getName().contains("org.json.simple.JSONArray")) {
			jarray = (JSONArray) jsonObject.get(inKey1);
			try {
				for (Object arr : jarray) {
					jsonObject = (JSONObject) arr;
					jarray = (JSONArray) jsonObject.get(inKey2);
					jarray = jarray == null ? jarray = new JSONArray() : jarray;
					for (Object ja : jarray) {
						jsonObject = (JSONObject) ja;
						if (jsonObject.containsKey("isPrime")) {
							if (((String) jsonObject.get("isPrime")).equals("true")) {
								String temp = (String) jsonObject.get(outKey);
								if (temp != null)
									values.add(temp);
							}
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Exception:" + e.getMessage());

			}
		}

		return values;

	}

	public static String getGETResponse(String api) {
		try {

			URL url = new URL(api);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + " for url: " + api);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			// System.out.println("Output from Server .... \n");
			output = br.readLine();
			System.out.println("Fetched from server for " + api);
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		System.out.println(output);
		return output;
	}


	public static String getEmailContentFromGetNada(String email, String emailSubject, long currentEpocTime)
	{
		boolean flag = false;
		String htmlValue = "";
		try {
			String getInbox = "https://getnada.com/api/v1/inboxes/"+email;
			String getMsgID = "";
			/* Hitting First API for Sing Up */
			String res = ApiHelper.getGETResponse(getInbox);

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(res);
			JSONArray ja = (JSONArray) jsonObject.get("msgs");

			Iterator itr2 = ja.iterator();
			int count = 0;
			while (itr2.hasNext())
			{
				Map<String, String> mp = (Map<String, String>) itr2.next();
				System.out.println(Long.parseLong(String.valueOf(mp.get("r"))));
				//System.out.println(currentEpocTime);
				if(mp.containsValue(emailSubject) && Long.parseLong(String.valueOf(mp.get("r")))> currentEpocTime)
				{
					getMsgID = mp.get("uid");
					count = 1;
					break;
				}

			}
			if(count == 0)
				throw new Exception("Unable to Find Email with Subject");
			String getSubject = "https://getnada.com/api/v1/messages/html/"+getMsgID;
			//htmlValue = ApiHelper.getGETResponse(getSubject);
			htmlValue = ApiHelper.getApiResponseUsingRestAssured(getSubject);
			//			jsonObject = (JSONObject) parser.parse(res2);
			//			htmlValue = (String) jsonObject.get("html");
			flag = true;
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		if(flag)
			return  htmlValue;
		else
			return "";

	}

	/* This method extracts all URL's from String
	 * Input:- String
	 * Output:- List<String> that contains URL
	 */
	public static List<String> extractUrls(String text)
	{
		List<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(text);

		while (urlMatcher.find())
		{
			containedUrls.add(text.substring(urlMatcher.start(0),
					urlMatcher.end(0)));
		}
		return containedUrls;
	}

	public static LinkedHashMap<String, ArrayList<ArrayList<String>>> getDataFromAPI(String feed, String inKey1,
			String inKey2, String inKey3, String... inKeys) {
		LinkedHashMap<String, ArrayList<ArrayList<String>>> dataList = new LinkedHashMap<String, ArrayList<ArrayList<String>>>();
		String url = String.format(feed, Config.fetchConfigProperty("ETAppVersion"),
				Config.fetchConfigProperty("APIPlatformName"));
		String output = ApiHelper.getAPIResponse(url);
		List<String> pageNumbers = ApiHelper.getNewsItems(output, "pn", "", "tp");
		String pno = pageNumbers.size() > 0 ? pageNumbers.get(0) : "1";
		for (int i = 1; i <= Integer.parseInt(pno); i++) {
			url = url.replaceAll("&curpg=[\\d]+", "");
			url += "&curpg=" + i;
			output = ApiHelper.getAPIResponse(url);
			dataList.putAll(ApiHelper.parseJsonData(output, inKey1, inKey2, inKey3, inKeys));

		}
		System.out.println(dataList);
		return dataList;
	}


	public static HashMap<String, ArrayList<ArrayList<String>>> parseJsonData(String output, String inKey1,
			String inKey2, String inKey3, String... inKeys) {
		HashMap<String, ArrayList<ArrayList<String>>> apiDataList = new LinkedHashMap<String, ArrayList<ArrayList<String>>>();
		List<String> values = new LinkedList<>();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(output);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		JSONArray jarray = null;

		jsonObject = (JSONObject) obj;
		if (jsonObject.get(inKey1).getClass().getName().contains("org.json.simple.JSONArray")) {
			jarray = (JSONArray) jsonObject.get(inKey1);
			try {
				for (Object arr : jarray) {
					jsonObject = (JSONObject) arr;
					String sectionName = (String) jsonObject.get(inKey3);
					ArrayList<ArrayList<String>> sectionList = new ArrayList<ArrayList<String>>();
					jarray = (JSONArray) jsonObject.get(inKey2);
					jarray = jarray == null ? jarray = new JSONArray() : jarray;

					for (Object ja : jarray) {
						ArrayList<String> newsItem = new ArrayList<String>();
						jsonObject = (JSONObject) ja;
						for (String key : inKeys) {
							if (jsonObject.containsKey(key)) {
								newsItem.add((String) jsonObject.get(key));
							} else {
								newsItem.add(("NA"));
							}
						}
						if (!apiDataList.containsKey(sectionName)) {
							sectionList = new ArrayList<ArrayList<String>>();
							sectionList.add(newsItem);
							apiDataList.put(sectionName, sectionList);
						} else {
							ArrayList<ArrayList<String>> tmp = apiDataList.get(sectionName);
							tmp.add(newsItem);
							apiDataList.put(sectionName, tmp);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e.getMessage());
			}
		}
		return apiDataList;
	}

	public static String formatText(String text) {
		if (text == null)
			return text;
		try {
			byte[] d = text.getBytes("cp1252");
			text = new String(d, Charset.forName("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}

	public static String getApiResponseUsingRestAssured(String api) {
		String result="";
		try {
			Response response = RestAssured.given().get(api);
			result = response.getBody().asString();
		} catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return result;
	}

}
