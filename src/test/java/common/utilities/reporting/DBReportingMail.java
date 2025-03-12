package common.utilities.reporting;

import common.utilities.DBUtil;
import common.utilities.VerificationUtil;
import org.joda.time.DateTime;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class DBReportingMail {
	String html = "<p>Hi All,<br /><br />Please find today's automation job report:<br /><br /></p> "
			+ "<table border=\"2\" width=\"100%\"> <tbody> <tr> <td align=\"center\" bgcolor=\"#e7f064\" width=\"15%\"><strong>"
			+ "<span style=\"color: #000000; font-family: Tahoma; font-size: 15px;\">Project</span></strong></td> "
			+ "<td align=\"center\" bgcolor=\"#e7f064\" width=\"15%\"><strong><span style=\"color: #000000; font-family: Tahoma; font-size: 15px;\">Total Runs</span></strong></td> "
			+ "<td align=\"center\" bgcolor=\"#e7f064\" width=\"15%\">"
			+ "<strong><span style=\"color: #000000; font-family: Tahoma; font-size: 15px;\">Failure Percentage</span></strong></td> <td align=\"center\" bgcolor=\"#e7f064\" width=\"15%\">"
			+ "<strong><span style=\"color: #000000; font-family: Tahoma; font-size: 15px;\">Pass Runs</span></strong></td> <td align=\"center\" bgcolor=\"#e7f064\" width=\"15%\"><strong>"
			+ "<span style=\"color: #000000; font-family: Tahoma; font-size: 15px;\">Fail Runs</span></strong></td> </tr>";

	String totalCount;
	String passCount;
	String failCount;
	String tempHtml = "";
	String failPercent;
	boolean flag = false;
	String restrictedRecipients = "navdeep.gill@timesinternet.in";
	String temp;

	@Parameters({ "sendTo", "emailSubject", "tableName", "testCaseName" })
	@Test
	public void mailWithDailyDBResults(String sendTo, String emailSubject, String tableName, String testCaseName) {
		DateTime dateTime = new DateTime();
		String date = dateTime.toString().split("T")[0];
		List<String> testCases = Arrays.asList(testCaseName.split("\\s*,\\s*"));
		for (String testCase : testCases) {
			tempHtml += "<tr>";

			getResultsFromDB(tableName, date, testCase.trim());
			failPercent = getFailPercentage(failCount, totalCount);
			if (!(Double.parseDouble(failPercent) > 1) && !(temp.equalsIgnoreCase(sendTo)))
				temp = restrictedRecipients;
			else
				temp = sendTo;
			String projectName = getProjectName(testCase);
			tempHtml += "<td style=\"text-align: center;\">" + projectName + "</td><td style=\"text-align: center;\">"
					+ totalCount + "</td><td style=\"text-align: center;\">" + failPercent
					+ "</td><td style=\"text-align: center;\">" + passCount + "</td><td style=\"text-align: center;\">"
					+ failCount + "</td>";
			tempHtml += "</tr>";
		}
		tempHtml += "</tbody> </table>";
		html += tempHtml;
		System.out.println(html);
		String[] recipient = temp.split("\\s*,\\s*");
		SendEmail.sendCustomEmail(recipient, emailSubject, html);
	}

	private String getProjectName(String testCaseName) {
		String temp = "";
		switch (testCaseName) {
		case "Login":
			temp = "ET-Main";
			break;
		case "Portfolio Login":
			temp = "Portfolio";
			break;
		default:
			break;
		}
		return temp;
	}

	private String getFailPercentage(String failCount, String totalCount) {
		String val = ""
				+ VerificationUtil.round((Double.parseDouble(failCount) / Double.parseDouble(totalCount)) * 100, 2);
		System.out.println(val);
		return val;
	}

	@Parameters({ "sendTo", "emailSubject", "html" })
	@Test
	public void sendMailHTML(String sendTo, String emailSubject, String html) {
		String[] recipient = sendTo.split("\\s*,\\s*");
		SendEmail.sendCustomEmail(recipient, emailSubject, html);
	}

	private void getResultsFromDB(String tableName, String date, String testCaseName) {
		String query = "select count(*) from " + tableName + " where testCaseName='" + testCaseName
				+ "' and date(runOn)='" + date + "'";
		totalCount = DBUtil.getResultsAsArrayList(query + ";").iterator().next()[0];
		passCount = DBUtil.getResultsAsArrayList(query + " and status='PASS'").iterator().next()[0];
		failCount = DBUtil.getResultsAsArrayList(query + " and status='FAIL'").iterator().next()[0];

	}

}
