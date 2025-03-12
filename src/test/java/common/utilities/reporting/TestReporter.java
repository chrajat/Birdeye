package common.utilities.reporting;

import common.launchsetup.Config;
//import common.utilities.DBUtil;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TestReporter implements IReporter {

	public static String testDescriptor;
	private ITestResult itResultFail;
	private LinkedList<String> result;
	private static Map<String, List<String>> dbInsertionMap = new LinkedHashMap<>();
//	private String indexHTMLFile = "test-output\\html\\index.html";

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		Config config = new Config();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Map<String, List<String>> testWiseResult = new LinkedHashMap<String, List<String>>();
		String testSuite = suites.get(0).getName();
		String startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
				.format(suites.get(0).getResults().values().iterator().next().getTestContext().getStartDate());
		Map<String, Map<String, List<String>>> suiteWiseResult = new LinkedHashMap<String, Map<String, List<String>>>();
		for (ISuite suite : suites) {
			System.out.println("suiteName ==> " + suite.getName());
			Map<String, ISuiteResult> suiteResults = suite.getResults();
			for (ISuiteResult sr : suiteResults.values()) {
				List<ITestNGMethod> i = Arrays.asList(sr.getTestContext().getAllTestMethods());
				i.forEach(a -> {
					testWiseResult.put(a.getTestClass().getName().split(".tests.")[1] + "-" + a.getMethodName() + ":"
							+ a.getDescription(), new LinkedList<>());
				});
				ITestContext tc = sr.getTestContext();
				Map<String, File> screenshotFileName = ScreenShots.getScreenshotFile();

				Set<ITestResult> passedSet = new LinkedHashSet<ITestResult>();
				passedSet.addAll(tc.getPassedTests().getAllResults());
				ITestResult itResultPass;
				ITestNGMethod iTestNGMethodPass;
				for (Iterator<ITestResult> it = passedSet.iterator(); it.hasNext();) {
					result = new LinkedList<>();
					itResultPass = it.next();
					String params = "";
					if (itResultPass.getParameters().length != 0) {
						params = "_" + itResultPass.getParameters()[0].toString();
					}
					result.add("PASS");
					result.add(String.valueOf((itResultPass.getEndMillis() - itResultPass.getStartMillis()) / 1000));
					result.add("N/A");
					iTestNGMethodPass = itResultPass.getMethod();
					result.add("N/A");
					testWiseResult.put(itResultPass.getTestClass().getName().split(".tests.")[1] + "-"
							+ iTestNGMethodPass.getMethodName() + params + ":" + iTestNGMethodPass.getDescription()
							+ params, result);
					dbInsertionMap.put(
							itResultPass.getTestClass().getName().split(".tests.")[1] + "-"
									+ iTestNGMethodPass.getMethodName() + params,
							Arrays.asList(itResultPass.getStartMillis() + "", iTestNGMethodPass.getDescription(),
									String.valueOf(
											(itResultPass.getEndMillis() - itResultPass.getStartMillis()) / 1000),
									"PASS", ""));
				}

				Set<ITestResult> failedSet = new LinkedHashSet<ITestResult>();
				failedSet.addAll(tc.getFailedTests().getAllResults());
				ITestNGMethod iTestNGMethodFail;
				for (Iterator<ITestResult> it = failedSet.iterator(); it.hasNext();) {
					result = new LinkedList<>();
					itResultFail = it.next();
					String params = "";
					if (itResultFail.getParameters().length != 0) {
						params = "_" + itResultFail.getParameters()[0].toString();
					}
					result.add("FAIL");
					result.add(String.valueOf((itResultFail.getEndMillis() - itResultFail.getStartMillis()) / 1000));
					String message = "";
					try {
						message = itResultFail.getThrowable().toString().replaceAll("System info:.*", "")
								.replaceAll("Build info:.*", "").replaceAll("Driver info:.*", "")
								.replaceAll("Capabilities.*", "").replaceAll("Session.*", "")
								.replaceAll("expected \\[[^,\r\n]+\\]", "").replace("java.lang.AssertionError:", "");
					} catch (Exception e) {
						message = itResultFail.getThrowable().toString();
					}
					System.out.println("Assertion failure messages=>" + message);
					result.add(message);
					iTestNGMethodFail = itResultFail.getMethod();

					Set<String> set = screenshotFileName.keySet().stream()
							.filter(s -> s.contains(itResultFail.getMethod().getMethodName()))
							.collect(Collectors.toSet());
					set.forEach(action -> {
						File file = screenshotFileName.get(action);
						try {
							result.add("<a href=" + file.getAbsolutePath() + ">" + file.getName() + "</a><br>");
						} catch (Exception e) {
							result.add("--");
						}
					});
					testWiseResult.put(iTestNGMethodFail.getTestClass().getName().split(".tests.")[1] + "-"
							+ iTestNGMethodFail.getMethodName() + params + ":" + iTestNGMethodFail.getDescription()
							+ params, result);
					dbInsertionMap.put(
							iTestNGMethodFail.getTestClass().getName().split(".tests.")[1] + "-"
									+ iTestNGMethodFail.getMethodName() + params,
							Arrays.asList(itResultFail.getStartMillis() + "", iTestNGMethodFail.getDescription(),
									String.valueOf(
											(itResultFail.getEndMillis() - itResultFail.getStartMillis()) / 1000),
									"FAIL", message.replaceAll("<[^>]+>", "")));
				}

				Set<ITestResult> failedConfigurationSet = new HashSet<ITestResult>();
				failedConfigurationSet.addAll(tc.getFailedConfigurations().getAllResults());
				ITestResult itResultFailConf;
				ITestNGMethod iTestNGMethodFailConf;
				for (Iterator<ITestResult> it = failedConfigurationSet.iterator(); it.hasNext();) {
					result = new LinkedList<>();
					itResultFailConf = it.next();
					result.add("FAIL");
					result.add(String
							.valueOf((itResultFailConf.getEndMillis() - itResultFailConf.getStartMillis()) / 1000));
					result.add("Configration Failure");
					iTestNGMethodFailConf = itResultFailConf.getMethod();
					result.add("N/A, It was a configuration failure for the suite.");
					testWiseResult.put(iTestNGMethodFailConf.getTestClass().getName().split(".tests.")[1] + "-"
							+ iTestNGMethodFailConf.getMethodName() + ":" + iTestNGMethodFailConf.getDescription(),
							result);

				}

				Set<ITestResult> skippedSet = new HashSet<ITestResult>();
				skippedSet.addAll(tc.getSkippedTests().getAllResults());
				skippedSet.addAll(tc.getSkippedConfigurations().getAllResults());
				ITestResult itResultSkip;
				ITestNGMethod iTestNGMethodSkip;
				for (Iterator<ITestResult> it = skippedSet.iterator(); it.hasNext();) {
					result = new LinkedList<>();
					itResultSkip = it.next();
					result.add("SKIP");
					result.add(String.valueOf((itResultSkip.getEndMillis() - itResultSkip.getStartMillis()) / 1000));
					result.add("The test case was skipped because of configuration failure.");
					iTestNGMethodSkip = itResultSkip.getMethod();
					result.add("N/A");
					testWiseResult.put(
							iTestNGMethodSkip.getTestClass().getName().split(".tests.")[1] + "-"
									+ iTestNGMethodSkip.getMethodName() + ":" + iTestNGMethodSkip.getDescription(),
							result);

				}
			}
			suiteWiseResult.put(testSuite + "|" + startTime, testWiseResult);
		}
		generateHTMLReport(testSuite, suiteWiseResult);
//		if (Boolean.parseBoolean(new Config().fetchForReporting("dbFlag"))) {
//			DBUtil.dbInsertRegression();
//			try {
//				DBUtil.closeSQLConnection();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		String mailSubject = config.fetchForReporting("mailSubject");
		String tempSub = "Automation Report for " + testSuite;
		mailSubject = (mailSubject != null & mailSubject.trim().length() > 0)
				? tempSub + "--" + mailSubject + "|" + startTime
				: tempSub + "|" + startTime;
		new SendEmail().sendEmail(mailSubject, "HTMLReport.html", "recipient");
		if (Boolean.parseBoolean(config.fetchForReporting("isFailPresent"))) {
			if (testSuite.contains("Critical")) {
				new SendEmail().sendEmail("ALERT: Crtical failures " + mailSubject.replace("Automation", ""), "FailureReport.html",
						"failureRecipient");
			} else {
				new SendEmail().sendEmail("Failure " + mailSubject.replace("Automation", ""), "FailureReport.html",
						"failureRecipient");
			}
		}
	}

	public void generateHTMLReport(String suiteName, Map<String, Map<String, List<String>>> suiteWiseResult) {
		// String directory = new Config().fetchForReporting("directory");

		try {
			File file = new File("HTMLReport.html");
			File failureFile = new File("FailureReport.html");
			FileWriter f = new FileWriter(file, false);
			BufferedWriter bw = new BufferedWriter(f);
			FileWriter ff = new FileWriter(failureFile, false);
			BufferedWriter fw = new BufferedWriter(ff);

			bw.write("<html>");
			if (suiteWiseResult.size() > 0) {
				for (Entry<String, Map<String, List<String>>> e : suiteWiseResult.entrySet()) {

					bw.write("<title>Test Report:" + e.getKey() + "</title>");
					String[] extractValues = e.getKey().split("\\|");
					String testSuiteName = extractValues.length > 0 ? extractValues[0] : "";
					String testSuiteStart = extractValues.length > 1 ? extractValues[1] : "";
					String messageContent = new Config().fetchForReporting("mailBody");
					messageContent = messageContent.trim().length() > 0 ? messageContent
							: "Please find the report of last test suite run :";
					String message1 = "<head><p style='padding-left: 30px;'><span style='color: #333333;'>"
							+ "Hi, <br> <br>" + messageContent + "<br> </span></p>"
							+ "<h1 style='text-align: center;'><span style='text-decoration: underline;'><em>Report for "
							+ testSuiteName + "</em></span></h1><h3 style='text-align: center;'> Started at: "
							+ testSuiteStart + "</h3></head>";
					bw.write(message1);
					fw.write(message1);

					String msg = "<table border='2' width='50%'><tbody><tr style='height: 40px; background-color: #e7f064;'>"
							+ "<th style='text-align: center;'  width='50'><strong>Test Status</strong></th>"
							+ "<th style='text-align: center;'  width='50'><strong> Count</strong></th>" + "</tr>";
					bw.write(msg);
					fw.write(msg);

					List<Integer> totalCount = getCount(e.getValue());
					String totalPass = "Total Test Cases Passed";
					String totalFail = "Total Test Cases Failed";
					String totalSkip = "Total Test Cases Skipped";
					String totalTC = "Total Test Cases Executed";
					int totalTCExecuted = totalCount.get(0) + totalCount.get(1) + totalCount.get(2);

					String msg2 = "<tr><td style='height: 20px; text-align: left; padding-left: 5px' width='50'><strong>"
							+ totalPass + "</strong></td>"
							+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
							+ totalCount.get(0)
							+ "<tr><td style='height: 20px; text-align: left; padding-left: 5px' width='50'><strong>"
							+ totalFail + "</strong></td>"
							+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
							+ totalCount.get(1)
							+ "<tr><td style='height: 20px; text-align: left; padding-left: 5px' width='50'><strong>"
							+ totalSkip + "</strong></td>"
							+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
							+ totalCount.get(2)
							+ "<tr><td style='height: 20px; text-align: left; padding-left: 5px' width='50'><strong>"
							+ totalTC + "</strong></td>"
							+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
							+ totalTCExecuted;
					bw.write(msg2);
					fw.write(msg2);
					bw.write("</table> <br> <br>");
					fw.write("</table> <br> <br>");

					String message2 = "<table border='2' width='100%'><tbody><tr style='height: 40px; background-color: #e7f064;'>"
							+ "<th style='text-align: center;'  width='200'><strong>Test Case Name</strong></th>"
							+ "<th style='text-align: center;'  width='200'><strong>Test Case Description</strong></th>"
							+ "<th style='text-align: center;' width='50'><strong>Result</strong></th>"
							+ "<th style='text-align: center;' width='200'><strong>Reason of Failure (If any)</strong></th>"
							// + "<th style='text-align: center;'
							// width='200'><strong>Screenshot</strong></th>"
							+ "</tr>";
					bw.write(message2);
					fw.write(message2);
					Map<String, List<String>> suite = e.getValue();

					boolean isFailPresent = suite.entrySet().stream()
							.anyMatch(entry -> entry.getValue().contains("FAIL"))
							|| suite.entrySet().stream().anyMatch(entry -> entry.getValue().contains("SKIP"));
					System.out.println("Were there any failures: " + isFailPresent);
					if (isFailPresent)
						Config.writePropertiesFile("./maven.properties", "isFailPresent", "true");
					else
						Config.writePropertiesFile("./maven.properties", "isFailPresent", "false");

					for (Entry<String, List<String>> entry : suite.entrySet()) {
						try {

							String testName = entry.getKey().split(":")[0];
							String testDescription = entry.getKey().split(":")[1];
							List<String> values = entry.getValue();
							String result = values.get(0);
							String timeTaken = values.get(1);
							String message = values.get(2);
							String screenShot = "";

							try {
								if (values.size() > 4) {
									for (int i = 3; i < values.size(); i++) {
										screenShot += values.get(i);
									}
								} else {
									screenShot += values.get(3);
								}
							} catch (IndexOutOfBoundsException i) {
								// do nothing
							}

							String message3 = "<tr><td style='height: 20px; text-align: left; padding-left: 5px' width='200'><strong>"
									+ testName + "</strong></td>"
									+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
									+ testDescription + "</td>"
									+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='50'>"
									+ result + "</td>"
									+ "<td style='height: 20px; text-align: left; padding-left: 5px' width='200'>"
									+ message + "</td>"
									// + "<td style='height: 20px; text-align:
									// left; padding-left: 5px' width='200'>" +
									// screenShot + "</td>"
									+ "</tr>";
							if (result.equalsIgnoreCase("Fail") || result.equalsIgnoreCase("Skip"))
								fw.write(message3);
							bw.write(message3);

						}

						catch (Exception ex) {
							// do nothing
							ex.printStackTrace();
						}
					}
					bw.write("</table>");
					fw.write("</table>");
					// bw.write("<p> <span style='color: #333333;'>Please find
					// details of report <a href=\"" + resultURL + "\">here</a>
					// </span></p>");

				}
			}

			bw.write("</body>");
			fw.write("</body>");
			bw.close();
			fw.close();
			f.close();
			ff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getHostName() {
		InetAddress ip;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostname;
	}

	public static Map<String, List<String>> getDbInsertionMap() {
		return dbInsertionMap;
	}

	public static List<Integer> getCount(Map<String, List<String>> suite) {
		int passCount = 0;
		int failCount = 0;
		int skipCount = 0;
		for (Entry<String, List<String>> entry : suite.entrySet()) {
			try {
				List<String> values = entry.getValue();
				String result = values.get(0);

				if (result.equalsIgnoreCase("PASS"))
					passCount++;
				if (result.equalsIgnoreCase("FAIL") && entry.getKey().split(":").length > 1)
					failCount++;
				if (result.equalsIgnoreCase("SKIP") && entry.getKey().split(":").length > 1)
					skipCount++;

			}

			catch (Exception ex) {
				// do nothing
			}
		}
		List<Integer> count = new ArrayList<Integer>();
		count.add(passCount);
		count.add(failCount);
		count.add(skipCount);
		return count;

	}

}
