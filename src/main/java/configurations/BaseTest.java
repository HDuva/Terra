package configurations;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.annotations.*;
import pages.TerraLogin;
import pages.TerraEventListPage;

public class BaseTest extends TestListenerAdapter {

	public static BaseDriver bdriver = null;
	protected String outputDir;
	protected String testMethod;
	protected AutoLogger logger = new AutoLogger(BaseTest.class);
	public String testTagName;
	public String testClassName;
	private String browser;
	public static Boolean isLogin=false;

	public String Environment;

	protected ReadXMLData fwConfigData = new ReadXMLData("./TestData/Configuration.xml");
	private TerraLogin obTerraLogin =null;
	private TerraEventListPage ObTerraEventListPage=null;

	private void updateBDriverInKeywords() {
		if (obTerraLogin != null) {
			obTerraLogin.updateBDriver(bdriver);
		}

		if (ObTerraEventListPage != null) {
			ObTerraEventListPage.updateBDriver(bdriver);
		}
	}

	public TerraLogin ObTerraLogin() {
		try {
			if (obTerraLogin == null) {
				obTerraLogin = new TerraLogin(bdriver);
			}
		} catch (Exception e) {
			logger.e(e.getMessage());
		}
		Assert.assertNotNull(obTerraLogin, "Login Page is not initialized!");
		return obTerraLogin;
	}

	public TerraEventListPage ObTerraEventListPage() {
		try {
			if (ObTerraEventListPage == null) {
				ObTerraEventListPage = new TerraEventListPage(bdriver);
			}
		} catch (Exception e) {
			logger.e(e.getMessage());
		}
		Assert.assertNotNull(ObTerraEventListPage, "Event List Page is not initialized!");
		return ObTerraEventListPage;
	}

	@BeforeClass(alwaysRun = true)
	@Parameters({ "browser", "Environment"})
	public void beforeTestClass(@Optional String browser,
			 @Optional ITestContext testContext, String Environment) {

			logger.i("BeforeTestClass :: browser=%s", browser);
			try {
				this.Environment=Environment;
				this.browser = browser;
				outputDir = testContext.getOutputDirectory();
				testTagName = testContext.getName();
				logger.i("Test Tag Name: <b>%s</b><br/>Test Class Name: <b>%s</b>", testTagName, testClassName);

			} catch (Exception e) {
				logger.e("Error in beforeTestClass!", e);
			}
	}

	@BeforeMethod
	public void beforeTestMethod(Method method) {
		if(!isLogin) {
			try {
				String path="./test-output";
				Utilities.deleteDir(path);
			}
			catch (Exception e) {
				logger.e("");
			}
			isLogin=true;
			logger.i("beforeTestMethod");
			testMethod=method.getName();

			// Open browser
			try {
				long limit = Integer.parseInt(fwConfigData.get("Configuration", "ImplicitTimeout"));
				bdriver = new BaseDriver(browser, limit,outputDir);
				// Re-initialize base driver object in all keywords
			}
			catch (Exception e) {
				logger.e(e);
				Assert.fail("Open browser failed!" + e);
			}
			logger.d("Before Test Method: %s ", testMethod);
		}
		updateBDriverInKeywords();
	 }

	@AfterMethod(alwaysRun = true)
	public void afterMethod() {
		logger.d("After Test Method: %s ", testMethod);
	}

    @AfterClass(alwaysRun = true)
	public void afterTestClass() {
    	logger.i("After Class Name: <b>%s</b><br/>Test Class Name: <b>%s</b>", testTagName, testClassName);
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		  //Close Browser
		  bdriver.quit();
	}

}
	
	