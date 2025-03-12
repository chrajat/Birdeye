package common.utilities.reporting;

import common.launchsetup.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.screentaker.ViewportPastingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScreenShots {
	private RemoteWebDriver driver = null;
	private File file;
	private static Map<String, File> mapFileNames = new HashMap<String, File>();
	private static String testCaseName;

	public ScreenShots() {
		if (BaseTest.driver != null)
			this.driver = BaseTest.driver;
	}

	public void takeScreenshotAndAttachInReports(String filename) {
		testCaseName = filename;
		try {

			if (driver != null) {
				Screenshot screenshot;
				try {
					screenshot = new AShot().shootingStrategy(new ViewportPastingStrategy(500)).takeScreenshot(driver);
				} catch (RasterFormatException rfe) {
					System.out.println("********************************" + rfe.getMessage());
					screenshot = new AShot().takeScreenshot(driver);
				}
				final BufferedImage image = screenshot.getImage();

				// System.out.println(filename);
				filename = filename.replaceAll("\\W+", "") + "_"
						+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
				String destFile = System.getProperty("user.dir") + "\\screenshots\\" + filename;
				file = new File(destFile);
				System.out.println(destFile);
				try {
					ImageIO.write(image, "PNG", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// FileUtils.copyFile(srcFile, new File(destFile));
				reportLogScreenshot(file);
				setMapOfAllFiles(testCaseName, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in taking screenshot " + e.getMessage());
		}
	}

	public void takeScreenshotWithoutScrollReports(String filename) {
		testCaseName = filename;
		try {

			if (driver != null) {
				Screenshot screenshot;

				screenshot = new AShot().takeScreenshot(driver);

				final BufferedImage image = screenshot.getImage();

				// System.out.println(filename);
				filename = filename.replaceAll("\\W+", "") + "_"
						+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
				String destFile = System.getProperty("user.dir") + "\\screenshots\\" + filename;
				file = new File(destFile);
				System.out.println(destFile);
				try {
					ImageIO.write(image, "PNG", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// FileUtils.copyFile(srcFile, new File(destFile));
				reportLogScreenshot(file);
				setMapOfAllFiles(testCaseName, file);
			}
		} catch (Exception e) {
			System.out.println("Exception in taking screenshot " + e.getMessage());
		}
	}

	public static void cleanAllScreenShots() {
		File screenshots = new File(System.getProperty("user.dir") + "/screenshots/");
		if (screenshots.exists()) {
			try {
				FileUtils.cleanDirectory(screenshots);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void reportLogScreenshot(File file) {
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		Reporter.log(
				"<a href=\"" + file + "\"><p align=\"left\">screenshot<a> <p> captured  at " + new Date() + "</p>");
	}

	private static void setMapOfAllFiles(String testCaseName, File file) {
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		// String testCaseName=file.getName().split("_")[0];
		mapFileNames.put(testCaseName, file);
	}

	public static Map<String, File> getScreenshotFile() {
		return mapFileNames;
	}

	public File getFile() {
		return file;
	}

	public void takeScreenshotAnotherDriver(WebDriver driver, String filename) {
		final Screenshot screenshot = new AShot().shootingStrategy(new ViewportPastingStrategy(500))
				.takeScreenshot(driver);
		final BufferedImage image = screenshot.getImage();
		// System.out.println(filename);
		filename = filename + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
		String destFile = System.getProperty("user.dir") + "\\screenshots\\" + filename;
		file = new File(destFile);
		try {
			ImageIO.write(image, "PNG", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File seleniumNativeScreenshot(RemoteWebDriver driver,String filename) {
		try{
		WebDriver wdriver = new Augmenter().augment(driver);
		TakesScreenshot scrShot = ((TakesScreenshot) wdriver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		filename = filename.replaceAll("\\W+", "") + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ ".png";
		String destFile = System.getProperty("user.dir") + "/screenshots/" + filename;
		file = new File(destFile);

		try {
			FileUtils.copyFile(SrcFile, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}catch(Exception e){
			
		}
		return file;

	}

	public void takeElementScreenshot(WebElement element, String filename) {
		final Screenshot screenshot = new AShot().shootingStrategy(new ViewportPastingStrategy(1500))
				.takeScreenshot(driver, element);
		final BufferedImage image = screenshot.getImage();
		filename = filename + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
		String destFile = System.getProperty("user.dir") + "\\screenshots\\" + filename;
		file = new File(destFile);
		try {
			ImageIO.write(image, "PNG", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
