package common.utilities;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.Reporter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class VerificationUtil {

	private static List<String> missingMenuOptions;

	// All Methods in this class should be public static
	/**
	 * Validates for the equality of contents of the Map
	 * 
	 * @param map1
	 * @param map2
	 * @return - Failure Message in case if any thing doesn't match in two maps,
	 *         otherwise null
	 */
	public static String validateTwoMapsForEquality(Map<String, List<String>> map1, Map<String, List<String>> map2) {
		String failureMsg = "";
		if (map1 == null || map2 == null || map1.isEmpty() || map2.isEmpty())
			return "Either List<?>is empty";
		for (Entry<String, List<String>> mapFirst : map1.entrySet()) {
			if (map2.containsKey(mapFirst.getKey())) {
				List<String> map2Values = map2.get(mapFirst.getKey());
				List<String> map1Values = mapFirst.getValue();
				boolean areValuesEqual = validateContentOfList(map1Values, map2Values);
				if (!areValuesEqual)
					failureMsg = failureMsg + " Values for KEY :" + mapFirst.getKey() + " are NOT same \n";
			} else {
				failureMsg = failureMsg + " doesn't contain KEY " + mapFirst.getKey() + "\n";
			}
		}
		if (failureMsg.equals("")) {
			return null;
		}
		return failureMsg;
	}

	/**
	 * Checks for Equality of lists
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean validateContentOfList(List<?> expected, List<?> actual) {
		if (actual.size() != expected.size()) {
			System.out.println("Size of Expected : " + expected.size() + "  whereas Size of Actual : " + actual.size());
			return false;
		} else {

			for (int i = 0; i < actual.size(); i++) {
				if (actual.get(i).equals(null) || actual.get(i).equals("") || actual.get(i).equals(null)
						|| actual.get(i).equals("")) {
					System.out.println("Null value getting compared for method"
							+ Thread.currentThread().getStackTrace()[3].getMethodName());
					Reporter.log("Null Value is getting compared,Issue in supplied list");
					return false;
				}
				if (!expected.contains(actual.get(i))) {
					System.out.println("Expected doesn't contains " + expected.get(i) + " for method " + "\n "
							+ "Actual contains value " + actual.get(i));
					Reporter.log("Actual doesn't contain :" + actual.get(i));
					return false;
				}

			}
		}
		return true;
	}

	/**
	 * Checks actual List<?>is contained in expected give smaller List<?>in
	 * expected and bigger in actual to verifys
	 * 
	 * @param expected
	 * @param actual
	 * @return
	 */
	public static boolean listActualInExpected(List<?> expectedList, List<?> actualList) {
		missingMenuOptions = new LinkedList<String>();
		if (actualList.isEmpty()) {
			missingMenuOptions = (List<String>) expectedList;
			return false;
		}

		for (int i = 0; i < expectedList.size(); i++) {
			if (!actualList.contains(expectedList.get(i))) {
				missingMenuOptions.add((String) expectedList.get(i));
				System.out.println((String) expectedList.get(i) + " Not contained in expected list");
				Reporter.log((String) expectedList.get(i) + " Not contained in expected list");

			}
		}
		if (missingMenuOptions.size() > 0)
			return false;
		else
			return true;
	}

	public static List<String> getMissingMenuOptionList() {
		return missingMenuOptions;
	}

	/**
	 * Checks actual List<?>is contained in expected
	 * 
	 * @param expected
	 *            smaller list
	 * @param actual
	 *            bigger list
	 * @return
	 */
	public static List<String> differenceTwoLists(List<String> actual, List<String> expected) {
		List<String> missedList = new LinkedList<>();
		for (int i = 0; i < expected.size(); i++) {
			if (!actual.contains(expected.get(i))) {
				missedList.add(expected.get(i));

			}
		}
		return missedList;
	}

	/**
	 * Checks for Equality of lists maxUpto the limit provided Will not check
	 * the size of the two lists, will just check the equality of contents of
	 * List<?>till the maxUpto
	 * 
	 * @param list1
	 * @param list2
	 * @param maxUpto
	 *            - till where the lists will be matched
	 * @return
	 */
	public static boolean validateContentOfListUptoMaxElement(List<?> list1, List<?> list2, int maxUpto) {

		for (int i = 0; i < list1.size() && i < maxUpto; i++) {
			if (!list2.contains(list1.get(i))) {
				System.out.println("List2 doesn't contain :" + list1.get(i));
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks for Equality of lists maxUpto the limit provided Will not check
	 * the size of the two lists, will just check the equality of contents of
	 * List<?>till the maxUpto
	 * 
	 * @param list1
	 * @param list2
	 * @param maxUpto
	 *            - till where the lists will be matched
	 * @return
	 */
	public static boolean validateContentOfListUptoMaxElementWithOrder(List<?> list1, List<?> list2, int maxUpto) {
		if (list1 == null || list2 == null)
			return false;
		for (int i = 0; i < list1.size() && i < maxUpto; i++) {
			if (!list2.contains(list1.get(i))) {
				System.out.println("List2 doesn't contain :" + list1.get(i));
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates for the equality of contents of the Map where the values of the
	 * List<?>will be compared maximum upto the argument 'maxUpto'
	 * 
	 * @param map1
	 * @param map2
	 * @return - Failure Message in case if any thing doesn't match in two maps,
	 *         otherwise null
	 */
	public static String validateTwoMapsForEqualityMaxValuesUpto(Map<String, List<String>> map1,
			Map<String, List<String>> map2, int maxUpto) {
		String failureMsg = "";
		int count = 0;
		Iterator<Entry<String, List<String>>> entries = map2.entrySet().iterator();
		Entry<String, List<String>> map2Entry = null;
		for (Entry<String, List<String>> mapFirst : map1.entrySet()) {
			if (entries.hasNext()) {
				map2Entry = entries.next();
				// Will match up-to max count else break
				if (count <= maxUpto && count < map2.size()) {
					if (map2Entry.getKey().equals(mapFirst.getKey())) {
						List<String> map2Values = map2.get(mapFirst.getKey());
						List<String> map1Values = mapFirst.getValue();
						boolean areValuesEqual = validateContentOfListUptoMaxElementWithOrder(map1Values, map2Values,
								maxUpto);
						if (!areValuesEqual)
							failureMsg = failureMsg + " Values for KEY :" + mapFirst.getKey() + " are NOT same \n";
					} else {
						failureMsg = failureMsg + "  from First Entry set  : " + mapFirst.getKey()
								+ " doesn't appear in the Other Entry Set \n";
					}
				} else {
					break;
				}
				count++;
			} else {
				failureMsg = failureMsg + "Less Entry Set found on the UI side";
				return failureMsg;
			}
		}
		return failureMsg;
	}

	/**
	 * Checks if the two lists are having same contents in the same order
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean areListsEqualIgnoreCaseSensitivity(List<String> list1, List<String> list2) {
		if (list1 == null && list2 == null) {
			System.out.println("Both NUll");
			return true;
		}

		if ((list1 == null && list2 != null) || list1 != null && list2 == null || list1.size() != list2.size()) {
			try {
				Assert.assertTrue(false, "Size of Both the List<?>is not Same");
				System.out.println("Size of Both the List<?>is not Same");
			} catch (AssertionError e) {
			}
			return false;
		} else {
			for (int i = 0; i < list1.size(); i++) {
				if (!list1.get(i).equalsIgnoreCase(list2.get(i))) {
					try {
						Assert.assertTrue(false, "List2 doesn't contain :" + list1.get(i));
					} catch (AssertionError e) {
					}
					System.out.println("List2 doesn't contain :" + list1.get(i));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check if two list has same elements irrespective of item order
	 */
	public static boolean arelistsEqualIgnoreOrder(List<String> l1, List<String> l2) {
		boolean flag = true;
		if (l1 == null || l2 == null)
			return false;
		if (l1.size() != l2.size())
			return false;
		for (int i = 0; i < l1.size(); i++) {
			if (!l2.contains(l1.get(i))) {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * Checks if the two lists are having same contents in the same order
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static boolean areListsEqual(List<String> list1, List<String> list2) {
		if (list1 == null && list2 == null) {
			return true;
		}

		if ((list1 == null && list2 != null) || list1 != null && list2 == null || list1.size() != list2.size()) {
			return false;
		}

		return list1.equals(list2);
	}

	/**
	 * Gets the List<?>of texts of element list
	 * 
	 * @param list1
	 * @return
	 */
	public static List<String> getLinkTextList(List<? extends WebElement> webElList) {
		if (webElList.size() == 0) {
			return new LinkedList<>();
		}
		List<String> list1texts = new LinkedList<String>();
		boolean retryFlag = false;
		int counter = 0;
		do {
			for (int i = 0; i < webElList.size(); i++) {
				try {
					list1texts.add(webElList.get(i).getText());
					retryFlag = false;
					counter = 0;
				} catch (WebDriverException e) {
					retryFlag = true;
					counter++;
					System.out.println("Retrying Stale Element finding " + counter);
				}
			}
		} while (retryFlag && counter < 10);
		System.out.println(list1texts);
		return list1texts;
	}

	public static List<String> getLinkHrefList(List<? extends WebElement> webElList) {
		List<String> listHref = new LinkedList<>();
		System.out.println(webElList.size());
		if (webElList == null || webElList.size() == 0)
			return listHref;
		boolean retryFlag = false;
		int counter = 0;
		do {
			for (int i = 0; i < webElList.size(); i++) {
				try {
					listHref.add(webElList.get(i).getAttribute("href"));
					retryFlag = false;
					counter = 0;
				} catch (WebDriverException e) {
					retryFlag = true;
					counter++;
					System.out.println("Retrying Stale Element finding " + counter);
				}
			}
		} while (retryFlag && counter < 10);
		return listHref;
	}

	public static String getLinkTextElement(WebElement element) {
		return element.getText();
	}

	/**
	 * Compares two maps for equality
	 * 
	 * @param countValueFromWeb
	 * @param countValueFromAPI
	 * @return
	 */
	public static boolean compareTwoMapValues(Map<String, Long> countValueFromWeb,
			Map<String, Long> countValueFromAPI) {
		for (String key : countValueFromWeb.keySet()) {
			if (!countValueFromAPI.containsKey(key)) {
				return false;
			}

			Long webValue = countValueFromWeb.get(key);
			if (!webValue.equals(countValueFromAPI.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two maps for equality
	 * 
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static boolean compareTwoObjectMapValues(Map<String, String> map1, Map<String, String> map2) {
		if (map1.size() != map2.size())
			return false;
		for (Object key : map1.keySet()) {
			if (!map2.containsKey(key)) {
				return false;
			}

			Object webValue = map2.get(key);
			if (!webValue.equals(map1.get(key))) {
				return false;
			}
		}
		return true;
	}

	public static boolean valueIsInRange(double actual, double expected, int maxdiff) {
		int diff = (int) Math.abs(actual - expected);
		if (diff >= 0 && diff <= maxdiff)
			return true;
		return false;

	}

	public static boolean valueIsInRange(double actual, double expected, double perdiff) {
		int diff = (int) Math.abs(actual - expected);
		if ((diff / actual) * 100 <= perdiff)
			return true;
		return false;

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static boolean matchHeadline(String actual, String expected) {
		String[] arraysOfWordsS1 = actual.split("\\s+");
		System.out.println(actual + "\n" + arraysOfWordsS1.toString() + "\n" + expected);
		int counter = 0;
		for (int i = 0; i < arraysOfWordsS1.length; i++) {
			if (arraysOfWordsS1[i].trim().length() > 1)
				if (expected.contains(arraysOfWordsS1[i]))
					counter++;
		}
		if (counter > (int) arraysOfWordsS1.length * 0.40) {
			return true;
		}
		return false;
	}

	public static boolean isListHavingData(List<? extends WebElement> list) {
		boolean flag = false;
		List<String> actualText = VerificationUtil.getLinkTextList(list);
		for (String linkText : actualText) {
			// System.out.println(linkText);
			if (!(linkText.trim().length() > 0))
				flag = false;
			else
				flag = true;
		}
		return flag;
	}

	public static int returnIndexList(List<? extends WebElement> li, String value) {
		for (int i = 0; i < li.size(); i++) {
			if (li.get(i).getText().equals(value)) {
				return i;
			}
		}
		return 0;

	}

	/**
	 * Converts date from one format to another
	 * 
	 * @param date
	 * @param fromFormat
	 * @param toFormat
	 * @return
	 */
	public static DateTime convertDateMethod(String date) {
		String checkDate = date.split(",\\d{1}\\d{1}")[0];
		Pattern pattern = Pattern.compile("\\d+");
		
		Matcher matcher = pattern.matcher(checkDate.replaceAll("[,]", ""));
		matcher.find();
		DateTime myDate = null;
		try {
			if (matcher.group() == null)
				return null;
			if (matcher.group().length() == 1) {
				date = date.replaceFirst(matcher.group(), "0" + matcher.group());
			}

			date = date.replaceAll(",", "").replaceAll(" ", "");
			DateTimeParser[] parsers = { DateTimeFormat.forPattern("MMMddyyyyhh:mma").getParser(),
					DateTimeFormat.forPattern("ddMMyyyyhh:mma").getParser(),
					DateTimeFormat.forPattern("ddMMyyyyHH:mm").getParser(),
					DateTimeFormat.forPattern("MMMddyyyyHH:mm").getParser(),
					DateTimeFormat.forPattern("MMMddyyyy").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyyHH:mm").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyyhh:mma").getParser(),
					DateTimeFormat.forPattern("ddMMyyyy").getParser(),
					DateTimeFormat.forPattern("EEEMMMddyyyy").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyyHHmm").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyy").getParser(),
					DateTimeFormat.forPattern("yyyyMMddHH:mm:ss").getParser(),
					DateTimeFormat.forPattern("yyyyMMddHH:mm:ss").getParser(),
					DateTimeFormat.forPattern("EEEEddMMMyyyy").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyyhh:mm").getParser(),
					DateTimeFormat.forPattern("hh:mmddMMMyyyy").getParser(),
					DateTimeFormat.forPattern("hh:mmMMMddyyyy").getParser(),
					DateTimeFormat.forPattern("ddMMM").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyyHHmmss").getParser(),
			};
			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
			if (date.endsWith("0000"))
				myDate = new DateTime(Long.parseLong(date.trim()));
			else
				myDate = formatter.parseDateTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Formatted Date:" + myDate);
		return myDate;

	}

	public static String convertDateToGivenFormat(Date date, String expectedDatePattern) {
		DateFormat formatter = new SimpleDateFormat(expectedDatePattern);
		String formattedDate = formatter.format(date);
		System.out.println("formatted date:" + formattedDate);
		return formattedDate;
	}

	public static Date convertDateToGivenFormat(String date, String expectedDatePattern) {
		DateFormat formatter = new SimpleDateFormat(expectedDatePattern);
		Date returnedDate = null;
		try {
			returnedDate = formatter.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("formatted date:" + returnedDate);
		return returnedDate;
	}

	public static Boolean isLatest(String dateString, int allowedNoOfdays) {
		if (dateString == null)
			return null;
		dateString = dateString.replaceAll("[\\s]", "").replaceAll("[.]", ":");
		dateString = dateString.replace("IST", "");
		DateTime curDate = new DateTime();
		System.out.println(dateString);
		DateTime articleDate = VerificationUtil.convertDateMethod(dateString);

		if (articleDate != null && Days.daysBetween(articleDate, curDate).getDays() <= allowedNoOfdays)
			return true;
		else
			return false;
	}

	public static int diffDays(Date d1, Date d2) {
		LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int diff = date2.getDayOfMonth() - date1.getDayOfMonth();
		System.out.println("Days diff betweek dates:" + diff);
		return diff;
	}
	
	public static int diffDaysInYear(Date d1, Date d2) {
		LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int diff = date2.getDayOfYear() - date1.getDayOfYear();
		System.out.println("Days diff betweek dates:" + diff);
		return diff;
	}

	/**
	 * Checks the Order of the elements in the two lists are same
	 * 
	 * @param list1
	 *            - list1
	 * @param list2
	 *            - list2
	 * @return
	 */
	public static boolean checkSameOrderInTwoLists(List<String> list1, List<String> list2) {
		int i = 0;
		if (list1.isEmpty() && list2.isEmpty())
			return true;
		if (list1.size() != list2.size())
			return false;
		for (String listElt : list2) {
			if (!listElt.equalsIgnoreCase(list1.get(i++))) {
				Reporter.log("Not Matching :" + list1.get(i - 1) + " - With :" + listElt);
				System.out.println("Not Matching :" + list1.get(i - 1) + " - With :" + listElt);
				return false;
			}
		}
		return true;
	}

	public static List<String> isListUnique(List<?> list) {
		final Set<String> setToReturn = new HashSet<String>();
		final Set<Object> set1 = new HashSet<Object>();
		List<String> dupList = new LinkedList<>();
		if (list == null)
			return dupList;
		for (Object yourInt : list) {
			if (yourInt.toString().trim().length() == 0)
				continue;
			if (!set1.add(yourInt)) {
				setToReturn.add((String) yourInt);
			}
		}
		dupList.addAll(setToReturn);
		return dupList;
	}

	public static Map<String, List<Integer>> getDupMapWithIndexes(List<String> dupList, List<String> uniqueList) {
		Map<String, List<Integer>> indexes = new HashMap<>();
		for (int i = 0; i < uniqueList.size(); i++) {
			LinkedList<Integer> ll = new LinkedList<Integer>();
			String headline = uniqueList.get(i);
			int[] indices = IntStream.range(0, dupList.size())
					.filter(j -> dupList.get(j).trim().equals(headline.trim())).toArray();
			for (int k : indices) {
				ll.add(k);
				System.out.println("Adding " + dupList.get(k) + "for " + headline);
			}
			indexes.put(headline, ll);

		}
		return indexes;
	}

	public static int getSubstringOccurences(String main, String subString) {
		Pattern p = Pattern.compile(subString);
		Matcher m = p.matcher(main);
		int count = 0;
		while (m.find()) {
			count += 1;
		}
		return count;
	}

	public static boolean isNews(String url) {
		Pattern p = Pattern.compile(".*/[\\d]+.cms.*");
		Matcher matcher = p.matcher(url);
		return matcher.matches();
	}

	public static double parseDouble(String value) {
		double val = 0;
		try {
			val = Double.parseDouble(value);
		} catch (NumberFormatException e) {

		}
		return val;
	}

	public static int parseInt(String value) {
		int val = 0;
		try {
			val = Integer.parseInt(value);
		} catch (NumberFormatException e) {

		}
		return val;
	}

	public static int getRandomNumber() {
		Random random = new Random();
		int x = random.nextInt(900) + 100;
		return x;
	}

	public static int getRandomNumberMaxMinLimit(int high, int low) {
		Random random = new Random();
		int x = random.nextInt(high - low) + low;
		return x;
	}
}
