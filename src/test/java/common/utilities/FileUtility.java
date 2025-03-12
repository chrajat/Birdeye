package common.utilities;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileUtility {

	public static String separator = System.getProperty("file.separator");
	public static Properties prop = null;

	public static void writeIntoPropertiesFile(String recipient, String failureRecipient, String mailSubject,
			String mailBody, String emailType, String dbFlag) {
		 prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(System.getProperty("user.dir") + "/maven.properties");
			// set the properties value
			prop.setProperty("recipient", recipient);
			prop.setProperty("failureRecipient", failureRecipient);
			prop.setProperty("mailSubject", mailSubject);
			prop.setProperty("mailBody", mailBody);
			prop.setProperty("emailType", emailType);
			prop.setProperty("dbFlag", dbFlag);
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writePropertiesFile(String file, String key, String value) {

		FileInputStream in;
		 prop = new Properties();
		try {
			in = new FileInputStream( file);
			prop.load(in);
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			// set the properties value
			prop.setProperty(key, value);
			// save properties to project root folder
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String readTextFile(String fileName) throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(fileName)));
		return content;
	}

	public static void createNewFile(String filePath) {

		try {
			// create a file at the given path in the current working directory
			File file = new File(filePath);
			if (file.exists()) {
				deleteFile(filePath);
				System.out.println("The already existing file is deleted");
			}
			if (file.createNewFile()) {

				System.out.println("New file is created at the given path");
			} else {

				System.out.println("Failure, new file is not created");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static List<String> readTextFileByLines(String fileName) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(fileName));
		return lines;
	}

	public static void writeToTextFile(String fileName, String content) throws IOException {
		Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.CREATE);
	}

	public static HashMap<Integer, List<String>> checkDiffInFiles(String file1, String file2) throws IOException {
		HashMap<Integer, List<String>> differenceMap = new LinkedHashMap<>();
		List<String> lineContents;
		BufferedReader reader1 = new BufferedReader(new FileReader(file1));

		BufferedReader reader2 = new BufferedReader(new FileReader(file2));

		String line1 = reader1.readLine();

		String line2 = reader2.readLine();

		boolean areEqual = false;

		int lineNum = 1;

		while (line1 != null || line2 != null) {
			lineContents = new LinkedList<>();
			if (line1 == null || line1.length() == 0) {
				lineContents.add("a blank line or new line is added");
			} else {
				lineContents.add(line1);
			}
			if (line2 == null || line2.length() == 0) {
				lineContents.add("a blank line or new line is added");
			} else {
				lineContents.add(line2);
			}
			if (line1 == null || line2 == null) {

				differenceMap.put(lineNum, lineContents);
			} else if (!line1.equalsIgnoreCase(line2)) {
				differenceMap.put(lineNum, lineContents);
			}

			line1 = reader1.readLine();

			line2 = reader2.readLine();

			lineNum++;
		}

		reader1.close();

		reader2.close();

		return differenceMap;
	}

	public static List<String> getDiffLines(List<String> original, List<String> updated) {
		List<String> diffLines = new LinkedList<String>();
		try {
			Patch<String> patch = DiffUtils.diff(original, updated);

			// simple output the computed patch to console

			for (AbstractDelta<String> delta : patch.getDeltas()) {

				if (delta.getType() == DeltaType.CHANGE) {
					diffLines.add("<br>This line in updated Robots file is <b>changed</b> at <b>"
							+ delta.toString().replace("[ChangeDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated Robots file is changed at "
							+ delta.toString().replace("[ChangeDelta,", "").replace("[", "").replace("]", ""));

				}
				if (delta.getType() == DeltaType.INSERT) {
					diffLines.add("<br>This line in updated Robots file is <b>inserted</b> at <b>"
							+ delta.toString().replace("[InsertDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated Robots file is inserted at "
							+ delta.toString().replace("[InsertDelta,", "").replace("[", "").replace("]", ""));

				}
				if (delta.getType() == DeltaType.DELETE) {
					diffLines.add("<br>This line is <b>deleted</b> and is not there in updated robots file at <b>"
							+ delta.toString().replace("[DeleteDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated Robots file is deleted at "
							+ delta.toString().replace("[DeleteDelta,", "").replace("[", "").replace("]", ""));
				}
				// System.out.println(delta);
			}
		} catch (com.github.difflib.algorithm.DiffException ee) {
			diffLines.add("<b>Exception is comparing the files happened</b>");
		}
		return diffLines;
	}

	public static List<String> getDiffBetweenString(String original, String updated) {
		List<String> diffLines = new LinkedList<String>();
		try {
			Patch<String> patch = DiffUtils.diffInline(original, updated);

			// simple output the computed patch to console

			for (AbstractDelta<String> delta : patch.getDeltas()) {

				if (delta.getType() == DeltaType.CHANGE) {
					diffLines.add("<br>This line in updated String is <b>changed</b> at <b>"
							+ delta.toString().replace("[ChangeDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated String is changed at "
							+ delta.toString().replace("[ChangeDelta,", "").replace("[", "").replace("]", ""));

				}
				if (delta.getType() == DeltaType.INSERT) {
					diffLines.add("<br>This line in updated String is <b>inserted</b> at <b>"
							+ delta.toString().replace("[InsertDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated String is inserted at "
							+ delta.toString().replace("[InsertDelta,", "").replace("[", "").replace("]", ""));

				}
				if (delta.getType() == DeltaType.DELETE) {
					diffLines.add("<br>This line is <b>deleted</b> and is not there in updated String at <b>"
							+ delta.toString().replace("[DeleteDelta,", "").replace("[", "").replace("]", "")
							+ "</b></br>");
					System.out.println("This line in updated String is deleted at "
							+ delta.toString().replace("[DeleteDelta,", "").replace("[", "").replace("]", ""));
				}
				// System.out.println(delta);
			}
		} catch (com.github.difflib.algorithm.DiffException ee) {
			diffLines.add("<b>Exception is comparing the files happened</b>");
		}
		return diffLines;

	}
	
	public static String getPropertyValue(String key) {
		return prop.getProperty(key).trim();
	}

}
