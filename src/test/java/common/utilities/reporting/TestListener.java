/**
 * 
 */
package common.utilities.reporting;

import common.launchsetup.BaseTest;
import org.testng.*;
import org.testng.annotations.Test;

import java.util.List;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener, IMethodInterceptor {

	// This belongs to ISuiteListener and will execute before the Suite start

	@Override
	public void onStart(ISuite arg0) {
		Reporter.log("About to begin executing Suite " + arg0.getName(), true);
	}

	// This belongs to ISuiteListener and will execute, once the Suite is
	// finished

	@Override
	public void onFinish(ISuite arg0) {
		Reporter.log("About to end executing Suite " + arg0.getName(), true);
	}

	// This belongs to ITestListener and will execute before starting of Test
	// set/batch

	public void onStart(ITestContext arg0) {
		Reporter.log("About to begin executing Test " + arg0.getName(), true);
	}

	// This belongs to ITestListener and will execute, once the Test set/batch
	// is finished

	public void onFinish(ITestContext arg0) {
		Reporter.log("Completed executing test " + arg0.getName(), true);
	}

	// This belongs to ITestListener and will execute only when the test is pass

	public void onTestSuccess(ITestResult arg0) {
		// This is calling the printTestResults method
		printTestResults(arg0);
	}

	// This belongs to ITestListener and will execute only on the event of fail
	// test

	public void onTestFailure(ITestResult arg0) {
		// This is calling the printTestResults method
		printTestResults(arg0);
		System.out.println("FAILURE REASON:" + arg0.getThrowable().getMessage());
		String params = "";
		if (arg0.getParameters().length != 0) {
			/*
			 * for (Object parameter : arg0.getParameters()) { params +=
			 * parameter.toString() + ","; }
			 */

			params = "_" + arg0.getParameters()[0].toString();

			// params = "_" + params;
		}

		if (BaseTest.platform != null && BaseTest.platform.contains("App"))
			new ScreenShots().seleniumNativeScreenshot(BaseTest.driver, arg0.getName() + params);
		else if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("Web"))
			new ScreenShots().takeScreenshotAndAttachInReports(arg0.getName() + params);
		else if (BaseTest.platform != null && BaseTest.platform.equalsIgnoreCase("WAP"))
			new ScreenShots().takeScreenshotWithoutScrollReports(arg0.getName() + params);
		else if (BaseTest.platform != null && BaseTest.platform.contains("Api")) {
			// do nothing screenshot is not required
		} else
			new ScreenShots().takeScreenshotWithoutScrollReports(arg0.getName());

		if (params.endsWith(","))
			params = params.substring(0, params.length() - 1);
		/*
		 * if (BaseTest.platform!=null && BaseTest.platform.contains("android")) { try {
		 * if (!((AndroidDriver<?>)
		 * BaseTest.driver).currentActivity().equalsIgnoreCase(Config.
		 * fetchConfigProperty("cap_appActivity")))
		 * AppLoggingHelper.captureLog(((AndroidDriver<?>)BaseTest.driver),
		 * arg0.getName()); } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
	}

	// This belongs to ITestListener and will execute before the main test start
	// (@Test)

	public void onTestStart(ITestResult arg0) {
		System.out.println("The execution of the main test starts now");
	}

	// This belongs to ITestListener and will execute only if any of the main
	// test(@Test) get skipped

	public void onTestSkipped(ITestResult arg0) {
		String params = "";
		printTestResults(arg0);
		if (arg0.getParameters().length != 0) {
			params = "_" + arg0.getParameters()[0].toString();
		}
		new ScreenShots().takeScreenshotWithoutScrollReports(arg0.getName() + params);
	}

	// This is the method which will be executed in case of test pass or fail

	// This will provide the information on the test

	private void printTestResults(ITestResult result) {
		Reporter.log("Test Method resides in " + result.getTestClass().getName(), true);
		if (result.getParameters().length != 0) {
			String params = "";
			for (Object parameter : result.getParameters()) {
				params += parameter.toString() + ",";
			}
			Reporter.log("Test Method had the following parameters : " + params, true);
		}

		String status = null;
		switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			status = "Pass";
			break;
		case ITestResult.FAILURE:
			status = "Failed";
			break;
		case ITestResult.SKIP:
			status = "Skipped";
		}
		Reporter.log("Test Status: " + status, true);
	}

	// This belongs to IInvokedMethodListener and will execute before every
	// method including @Before @After @Test

	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {

		String description = arg1.getMethod().getDescription();
		if (description == null || description.isEmpty())
			description = "";
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		Reporter.log("<h3><font color='#0b47a8'> Description : </font>" + description + "</h3>");

		String textMsg = "About to begin executing following method : " + returnMethodName(arg0.getTestMethod());
		Reporter.log(textMsg, true);
	}

	// This belongs to IInvokedMethodListener and will execute after every
	// method including @Before @After @Test

	public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {
		String textMsg = "Completed executing following method : " + returnMethodName(arg0.getTestMethod());
		Reporter.log(textMsg, true);

	}

	// This will return method names to the calling function

	private String returnMethodName(ITestNGMethod method) {
		return method.getRealClass().getSimpleName() + "." + method.getMethodName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.
	 * testng.ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// Ignore this method
	}

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		int methCount = methods.size();

		for (int i = 0; i < methCount; i++) {
			IMethodInstance instns = methods.get(i);
			TestReporter.testDescriptor = instns.getMethod().getConstructorOrMethod().getMethod()
					.getAnnotation(Test.class).description();
		}

		return methods;
	}
}
