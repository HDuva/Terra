package configurations;

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
	public static String project;
	public static String browser;
	public static String Environment;

	public static ReadXMLData fwConfigData = new ReadXMLData("./TestData/Configuration.xml");

	private TerraLogin obTerraLogin =null;
	private TerraEventListPage ObTerraEventListPage=null;

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

	@BeforeSuite(alwaysRun = true)
	@Parameters({"Project","Browser","Environment"})
	public void beforeSuite(String Project,String browser,
			 @Optional ITestContext testContext, String Environment) {
		try {
			this.Environment=Environment;
			this.browser = browser;
			this.project=Project;
			outputDir = testContext.getOutputDirectory();
		} catch (Exception e) {
			logger.e("Error in beforeTestSuit!", e);
		}

		try {
			String path="./test-output";
			Utilities.deleteDir(path);
		}
		catch (Exception e) {
			logger.e("Error ");
		}

		// Open browser
		try {
			long limit = Integer.parseInt(fwConfigData.get("Configuration", "ImplicitTimeout"));
			bdriver = new BaseDriver(browser, limit,outputDir);
		}
		catch (Exception e) {
			logger.e(e);
			Assert.fail("Open browser failed!" + e);
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		  //Close Browser
		  bdriver.quit();
	}
}
	
	