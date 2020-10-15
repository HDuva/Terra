package configurations;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.BridgestoneLogin;

public class BaseTest extends TestListenerAdapter {

	public BaseDriver bdriver = null;
	protected String outputDir;
	protected String testMethod;
	protected AutoLogger logger = new AutoLogger(BaseTest.class);
	public String testTagName;
	public String testClassName;
	private String browser;
	private String version;
	private String operatingSystem;


	public String Environment;

	protected ReadXMLData fwConfigData = new ReadXMLData("./TestData/Configuration.xml");
	private BridgestoneLogin ObBridgestoneLogin =null;

	private void updateBDriverInKeywords() {
		if (ObBridgestoneLogin != null) {
			ObBridgestoneLogin.updateBDriver(bdriver);
		}
	}
	
	public BridgestoneLogin ObBridgestoneLogin() {
		try {
			if (ObBridgestoneLogin == null) {
				ObBridgestoneLogin = new BridgestoneLogin(bdriver);
			}
		} catch (Exception e) {
			logger.e(e.getMessage());
		}
		Assert.assertNotNull(ObBridgestoneLogin, "Order Confirmation Page is not initialized!");
		return ObBridgestoneLogin;
	}

	/**
	 * TestNG Annotated methods
	 * 
	 * @param browser
	 * @throws Exception
	 */
	@BeforeClass(alwaysRun = true)
	@Parameters({ "browser", "version", "operatingSystem", "Build", "Environment"})
	public void beforeTestClass(@Optional String browser, @Optional String version, @Optional String operatingSystem,
			@Optional String build, @Optional ITestContext testContext, String Environment) {

		logger.i("BeforeTestClass :: browser=%s", browser);

		try {
        	String path="./test-output";
    		Utilities.deleteDir(path);
        }
        catch (Exception e) {
    		logger.e("");
    	}
		try {
			this.Environment=Environment;
			this.browser = browser;
			this.version = version;
			this.operatingSystem = operatingSystem;
			outputDir = testContext.getOutputDirectory();
			testTagName = testContext.getName();
			logger.i("Test Tag Name: <b>%s</b><br/>Test Class Name: <b>%s</b>", testTagName, testClassName);

		} catch (Exception e) {
			logger.e("Error in beforeTestClass!", e);
		}
	}

	@BeforeMethod
	public void beforeTestMethod(Method method) {

		logger.i("beforeTestMethod");
		testMethod=method.getName();

		// Open browser
		try {
			long limit = Integer.parseInt(fwConfigData.get("Configuration", "ImplicitTimeout"));
			bdriver = new BaseDriver(browser, version, operatingSystem, limit,outputDir);
			// Re-initialize base driver object in all keywords
			  updateBDriverInKeywords();
		}
		 catch (Exception e) {
			logger.e(e);
			Assert.fail("Open browser failed!" + e);
		}
		logger.d("Before Test Method: %s ", testMethod);
	 }
		
	@AfterMethod(alwaysRun = true)
	public void afterMethod() {
		logger.d("After Test Method: %s ", testMethod);
		//Close Browser
		bdriver.quit();
	}
	
    @AfterClass(alwaysRun = true)
	public void afterTestClass() {
    	logger.i("After Class Name: <b>%s</b><br/>Test Class Name: <b>%s</b>", testTagName, testClassName);
	}
    
}
	
	