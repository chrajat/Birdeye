package common.utilities;

import common.launchsetup.BaseTest;
import common.launchsetup.Config;
import common.utilities.reporting.TestReporter;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBUtil {

	private static Connection connection = null;
	private static String dbIP = new Config().fetchConfig(new File("./suiterun.properties"), "dbIP");

	public static Connection getSQLConnection(String dbIP, String port, String dbName, String userName, String password)
			throws Exception {
		if (connection == null) {
			synchronized (DBUtil.class) {
				if (connection == null) {
					try {
						Class.forName("com.mysql.jdbc.Driver");
						connection = DriverManager.getConnection("jdbc:mysql://" + dbIP + ":" + port + "/" + dbName,
								userName, password);
						System.out.println("SQL DB connection created successfully with " + dbName + " !!!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return connection;
	}

	private static void insertPerformanceRecord(Date runOn, String platform, String pageName, Double firstViewTime,
			Double loadTime, String status, String failureReason) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(runOn);
		String query = "'" + currentTime + "','" + platform + "','" + pageName + "','" + firstViewTime + "','"
				+ loadTime + "','" + status + "','" + failureReason + "'";
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO performance (RunOn,Platform,PageName,firstView,LoadTime, Status,FailureReason) values ("
					+ query + ");";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	private static void insertBasisTableNameColumnValues(String tableName, String... columnValues) {
		String query = "";
		for (int i = 0; i < columnValues.length; i++) {
			query += "'" + columnValues[i] + "',";

		}
		query = query.substring(0, query.length() - 1);
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO " + tableName + " values (" + query + ");";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	private static void insertNewsletter(String newsletterName, String runOn, String subject) {
		subject = subject.replaceAll("\"", "\\\\\"");
		String query = "\"" + newsletterName + "\",\"" + runOn + "\",\"" + subject + "\"";
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO newsletter (NewsletterName,RunOn,Subject) values (" + query + ");";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	private static void updateColumnValue(String tableName, String whereColumnName, String whereColumnVal,
			String setColumnName, String setColumnVal) {
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "UPDATE " + tableName + " set  " + setColumnName + " = '" + setColumnVal + "' where "
					+ whereColumnName + " = '" + whereColumnVal + "';";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("update records into the table...");
	}

	public static void updateByQuery(String updateQuery) {
		System.out.println(connection == null);
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			PreparedStatement preparedStmt = connection.prepareStatement(updateQuery);
			preparedStmt.executeUpdate();
			closeSQLConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
	}

	private static void insertAPIRecord(Date runOn, String apiUrl, Double responseTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(runOn);
		String query = "'" + currentTime + "','" + apiUrl + "','" + responseTime + "'";
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO apiResponse (runOn,apiUrl,responseTime) values (" + query + ");";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	public static void truncateTable(String tableName) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "TRUNCATE TABLE " + tableName + ";";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("truncateTable...");
	}

	private static void insertRegressionRecord(String platform, Date runOn, String testCaseName, String testCaseDetails,
			String executionTime, String status, String failureReason) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(runOn);
		String query = "'" + platform + "','" + currentTime + "','" + testCaseName + "','" + testCaseDetails + "','"
				+ executionTime + "','" + status + "','" + failureReason + "'";
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO regression (Platform,RunOn,TestCaseName,TestCaseDetails,ExecutionTime, Status, FailureReason) values ("
					+ query + ");";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	private static void insertMonitoringRecord(String Platform, Date RunOn, String TestCaseName, String TestCaseDetails,
			String Status, String FailureReason, String screenshotPath) {
		try {
			screenshotPath = screenshotPath.replace("\\", "\\\\\\\\");
		} catch (NullPointerException e) {

		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(RunOn);
		String query = "'" + Platform + "','" + currentTime + "','" + TestCaseName + "','" + TestCaseDetails + "','"
				+ Status.toUpperCase() + "','" + FailureReason + "','" + screenshotPath + "'";
		Statement statement;
		try {
			statement = connection.createStatement();

			String sql = "INSERT INTO monitoring (Platform,RunOn,TestCaseName,TestCaseDetails, Status, FailureReason,screenshotPath) values ("
					+ query + ");";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inserted records into the table...");
	}

	public static void dbMonitoringInsert(String tableName, String platform, Date runOn, String testCaseName,
			String testCaseDetails, String status, String failureReason, String screenshotPath) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			insertMonitoringRecord(platform, runOn, testCaseName, testCaseDetails, status, failureReason,
					screenshotPath);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeSQLConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
			connection = null;
			System.out.println("SQL DB connection closed !!!");
		}
	}

	public static void dbInsertNewsletter(String newsletterName, String runOn, String subject) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			insertNewsletter(newsletterName, runOn, subject);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dbInsertPerformance(Date RunOn, String Platform, String PageName, Double firstViewTime,
			Double LoadTime, String Status, String FailureReason) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			insertPerformanceRecord(RunOn, Platform, PageName, firstViewTime, LoadTime, Status, FailureReason);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dbInsertColumnValues(String table, String... columnValues) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			insertBasisTableNameColumnValues(table, columnValues);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dbUpdateColumnValues(String table, String whereColumnName, String whereColumnValue,
			String setColumnName, String setColumnValue) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			updateColumnValue(table, whereColumnName, whereColumnValue, setColumnName, setColumnValue);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dbInsertApi(Date RunOn, String ApiUrl, Double responseTime) {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			insertAPIRecord(RunOn, ApiUrl, responseTime);
			closeSQLConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void dbInsertRegression() {
		try {
			getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");

			System.out.println(TestReporter.getDbInsertionMap().size());
			TestReporter.getDbInsertionMap().forEach((K, V) -> {
				System.out.println(BaseTest.platform + " " + V.get(0) + " " + K + " " + V.get(1) + " " + V.get(2) + " "
						+ V.get(3) + " " + V.get(4));
				insertRegressionRecord(BaseTest.platform, new Date(Long.parseLong(V.get(0))), K, V.get(1), V.get(2),
						V.get(3), V.get(4));
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<String[]> getResultsAsArrayList(String strQuery) {
		ArrayList<String[]> dbRes = new ArrayList<String[]>();
		String query = strQuery;
		System.out.println(query);
		try {
			connection = getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();

			int i = 0;
			while (rs.next()) {
				String[] dbResCol = new String[numberOfColumns];
				for (int j = 0; j < numberOfColumns; j++) {
					int k = j + 1;
					dbResCol[j] = rs.getString(k);
				}
				dbRes.add(i++, dbResCol);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (dbRes);
	}

	public static ArrayList<String[]> getRecords(String tableName, String... whereClauseColumnValue) throws Exception {
		getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
		String whereClause = "where ";
		int size = whereClauseColumnValue.length;
		for (int i = 0; i < size; i++) {
			whereClause += whereClauseColumnValue[i];
			if (i > size - 1)
				whereClause += whereClause + " AND ";
		}

		ArrayList<String[]> li = getResultsAsArrayList("Select * from "+tableName +" "+ whereClause + ";");
		closeSQLConnection();
		return li;
	}

	public static ArrayList<String[]> selectLast3Records(String testCaseName, String platform) throws Exception {
		getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
		ArrayList<String[]> li = getResultsAsArrayList("Select * from monitoring where TestCaseName='" + testCaseName
				+ "' and Platform='" + platform + "' order by id desc limit 3");
		closeSQLConnection();
		return li;

	}

	public static void updateEmailSent(String testCaseName, String platform) throws Exception {
		getSQLConnection(dbIP, "3306", "et_automation", "EtAUTOStgUsr", "Et@UT0StguSR");
		List<String[]> results = selectLast3Records(testCaseName, platform);
		String ids = "";
		for (String[] row : results) {
			ids += row[0] + ",";
		}
		String query = "update monitoring set isMailSent=1 where TestCaseName='" + testCaseName + "' and Platform ='"
				+ platform + "' and id in (" + ids.substring(0, ids.length() - 1) + ")";
		System.out.println(query);
		updateByQuery(query);
		closeSQLConnection();
	}
}
