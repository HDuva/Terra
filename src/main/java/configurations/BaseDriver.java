package configurations;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class BaseDriver {

	public  WebDriver driver = null;
	private WebDriverWait webDriverwait = null;
	private String mainWindowHandle = null;
	private final Browsers browser;
	Long limit = null;

	String outputPath;
	AutoLogger logger = new AutoLogger(BaseDriver.class);

	public DesiredCapabilities capability =null;

	public enum Browsers {
		FIREFOX, INTERNETEXPLORER, CHROME, SAFARI
	}

	public BaseDriver(String browser,
					  long limit, String outputDir) {
		this.browser = parseBrowser(browser);
		this.limit =  limit;
		this.outputPath = outputDir;
		loadWebDriverObject();
	}

	public Browsers getBrowser() {
		return this.browser;
	}

	public Browsers parseBrowser(String browserStr) {
		Browsers browser;

		List<String> ffKeys = new ArrayList<>();
		ffKeys.add("firefox");
		ffKeys.add("ff");
		ffKeys.add("Firefox");
		ffKeys.add("MozilaFirefox");
		ffKeys.add("Mozilafirefox");
		ffKeys.add("mozilafirefox");

		List<String> ieKeys = new ArrayList<>();
		ieKeys.add("internetexplorer");
		ieKeys.add("ie");
		ieKeys.add("iexplorer");

		List<String> chromeKeys = new ArrayList<>();
		chromeKeys.add("googlechrome");
		chromeKeys.add("chrome");
		chromeKeys.add("Chrome");
		chromeKeys.add("GC");

		if (ffKeys.contains(browserStr)) {
			browser = Browsers.FIREFOX;
		} else if (ieKeys.contains(browserStr)) {
			browser = Browsers.INTERNETEXPLORER;
		} else if (chromeKeys.contains(browserStr)) {
			browser = Browsers.CHROME;
		} else {
			browser = Browsers.CHROME;
		}
		return browser;
	}

	private void loadWebDriverObject() {
		try {
			String filePath = System.getProperty("user.dir") + "\\Dependency\\";

			if (browser.equals(Browsers.FIREFOX)) {
				System.setProperty("webdriver.gecko.driver",filePath+"/geckodriver.exe");
				driver=new FirefoxDriver();
			 }
			else if (browser.equals(Browsers.CHROME)) {
					System.setProperty("webdriver.chrome.driver",filePath+"/chromedriver.exe");
					driver=new ChromeDriver();
			}

			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
		} catch (Exception e) {
			Assert.fail("Failed to get the driver object: " + e.getMessage());
		}
	}

	public void setBrowserSize() {

		try {
			Robot rob= new Robot();
			for (int i=0;i<3;i++) {
				rob.keyPress(KeyEvent.VK_CONTROL);
				rob.keyPress(KeyEvent.VK_SUBTRACT);
				rob.keyRelease(KeyEvent.VK_SUBTRACT);
				rob.keyRelease(KeyEvent.VK_CONTROL);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}

		/*for(int i=0; i<3; i++){
			System.out.println(i+"time");
			driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL,Keys.SUBTRACT));
		}*/

		/*WebElement html = driver.findElement(By.tagName("html"));
		html.sendKeys(Keys.chord(Keys.CONTROL,Keys.SUBTRACT));*/

		/*JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("document.body.style.zoom='75%'");*/

		/*Dimension dm = new Dimension(1536,864);
		//Setting the current window to that dimension
		driver.manage().window().setSize(dm);*/
	}

	public void changeStyleAttrWithElementID(String elementID, String TagName, String newValue) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String executeScriptText = "document.getElementById('" + elementID + "').setAttribute('" + TagName + "', '"
				+ newValue + "')";
		js.executeScript(executeScriptText);
	}

	public void setFocusOnBrowser() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.focus();");
	}

	public void resetBrowserSize() {
		Dimension d = new Dimension(1024, 768);
		driver.manage().window().setSize(d);
	}

	public void waitUntilPageIsLoaded() {

		setImplicitWait(0);

		WebDriverWait wait = new WebDriverWait(driver, limit);

		ExpectedCondition<Boolean> pageLoadCondition = driver -> {
			assert driver != null;
			return "complete".equals(((JavascriptExecutor) driver).executeScript("return document.readyState"));
		};

		wait.until(pageLoadCondition);

		resetImplicitWait();

	}

	public void waitForPageLoad() {

		WebDriverWait wait = new WebDriverWait(driver, limit);
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return "complete".equals(((JavascriptExecutor) driver).executeScript("return document.readyState"));
			}
		};

		wait.until(pageLoadCondition);

		resetImplicitWait();

	}

	public void switchToFrame(WebElement we) {

		logger.i("switchToFrame");
		driver.switchTo().frame(we);
	}

	public void switchToDefault() {
		logger.i("switch To Default Frame");
		driver.switchTo().defaultContent();
	}

	public void inputTextToiFrame(WebElement we, String expText) {

		logger.i("inputTextToiFrame");
		switchToFrame(we);
		switchToInnerFrame(we, expText);
		switchToDefault();
	}

	public void switchToInnerFrame(WebElement we, String expText) {
		logger.i("switchToInnerFrame");
		we.click();
		Actions act = new Actions(driver);
		act.sendKeys(we, expText).build().perform();
	}

	public void verifyLabelPresentInList(WebElement we, String expLabel) {
		logger.i("verifyLabelPresentInList");
		String[] actList = getAllListItems(we);
		Assert.assertTrue(Arrays.asList(actList).contains(expLabel), "Text" + expLabel + "is not present in list!");
	}

	public void verifyLabelNotPresentInList(WebElement we, String expLabel) {
		logger.i("verifyLabelNotPresentInList");
		String[] actList = getAllListItems(we);
		Assert.assertFalse(Arrays.asList(actList).contains(expLabel), "Text" + expLabel + "is present in list!");
	}

	public void clickAndWait(WebElement we, boolean waitForPageLoad) {
		we.click();
		if (waitForPageLoad) {
			waitUntilPageIsLoaded();
		}
	}

	public void clickAndWait(WebElement we) {
		logger.i("Click On "+we.getText());
		clickAndWait(we, true);
	}

	public void doubleClickAndWait(WebElement we, boolean waitForPageLoad) {
		logger.i("doubleClickAndWait, wait?=%s");
		(new Actions(driver)).doubleClick(we).perform();
		if (waitForPageLoad) {
			waitUntilPageIsLoaded();
		}
	}

	public void doubleClickAndWait(WebElement we) {
		doubleClickAndWait(we, true);
	}

	public void inputText(WebElement we, String text) {
		logger.i("inputText, text=%s", text);
		we.clear();
		we.sendKeys(text);
	}

	public void pressTab(WebElement we) {
		logger.i("pressTab");
		we.click();
		we.sendKeys(Keys.TAB);
	}

	public void inputTextAndDefocus(WebElement we, String text) {
		logger.i("inputTextAndDefocus, text=%s", text);
		we.clear();
		we.sendKeys(text);
		((JavascriptExecutor) driver).executeScript("arguments[0].onblur();", we);
		// element.sendKeys(Keys.TAB);
		waitUntilPageIsLoaded();
	}

	public void executeBlurEvent(WebElement we) {
		((JavascriptExecutor) driver).executeScript("arguments[0].onblur();", we);
	}

	public void inputTextAndChangefocus(WebElement we, String text) {
		logger.i("inputTextAndDefocus, text=%s", text);
		we.clear();
		we.sendKeys(text);
		we.sendKeys(Keys.TAB);
		waitUntilPageIsLoaded();
	}

	public void sendKey(WebElement we, Keys key) {
		logger.i("sendkey, key=%s", key.name());
		we.sendKeys(key);
		waitUntilPageIsLoaded();
	}

	public void setText(WebElement we, String text) {
		logger.i("setText, text=%s", text);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].value='" + text + "';", we);
	}

	public void setInnerHTML(WebElement we, String text) {
		logger.i("setInnerHTML, text=%s", text);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].innerHTML='" + text + "';", we);
	}

	public void setTextContent(WebElement we, String text) {
		logger.i("setTextContent, text=%s", text);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].textContent='" + text + "';", we);
	}

	public void waitForDropDownToLoad(final WebElement elementId) {

		logger.i("waitForDropDownToLoad");
		setImplicitWait(0);
		WebDriverWait wait = new WebDriverWait(driver, limit);
		ExpectedCondition<Boolean> waitCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				Object ret = ((JavascriptExecutor) driver)
						.executeScript("return document.getElementById('" + elementId + "').length > 1");
				return ret.equals(true);
			}
		};
		wait.until(waitCondition);
		resetImplicitWait();
	}

	public void waitForElementToBeClickable(WebElement webElement, int implicitTime) {

		logger.i("waitForElementToBeClickable");

		if (implicitTime > 0) {
			webDriverwait = new WebDriverWait(driver, implicitTime);
		}

		webDriverwait.until(ExpectedConditions.elementToBeClickable(webElement));
	}

	public void waitForElementState(WebElement webElement, int implicitTime, boolean elementState) {

		logger.i("waitForElementState");

		if (implicitTime > 0) {
			webDriverwait = new WebDriverWait(driver, implicitTime);
		}

		webDriverwait.until(ExpectedConditions.elementSelectionStateToBe(webElement, elementState));
	}

	public void waitForTextChangeInElement(WebElement we) {
		logger.i("waitForTextChangeInElement");

		String actText = getText(we);

		waitForTextNotInElement(we, actText);
	}

	public void waitForTextNotInElement(WebElement we, String text) {

		logger.i("waitForTextNotInElement");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(we, text)));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}

	public void waitForElement(WebElement we) {

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.elementToBeClickable(we));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}

	public void waitForValueNotInElement(WebElement we, String text) {

		logger.i("waitForValueNotInElement");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementValue(we, text)));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}

	public void waitForTextInElement(WebElement we, String expText) {

		logger.i("waitForTextInElement");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.textToBePresentInElement(we, expText));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}

	public void waitForTextToBePresentInElement(WebElement we, String expText) {

		logger.i("waitForTextInElementWithAttribute");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.textToBePresentInElement(we, expText));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}

	public void waitForTextToBePresentInElementWithAttribute(WebElement we, String expText, String attributeName) {

		logger.i("waitForTextToBePresentInElementWithAttribute");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

		int attempts = 0;
		while (attempts < 500) {
			try {
				// //System.out.println("1");
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
				if (we.getAttribute(attributeName).contains(expText)) {
					break;
				}
			} catch (Exception e) {
				logger.e(e);
			}
			attempts++;
		}

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
	}

	public void waitForValueInElement(WebElement we, String expText) {

		logger.i("waitForValueInElement");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.textToBePresentInElementValue(we, expText));

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
	}

	public void waitForJSCondition(final String jsCondition) {
		logger.i("waitForJSCondition: %s", jsCondition);
		setImplicitWait(0);
		WebDriverWait wait = new WebDriverWait(driver, limit);
		ExpectedCondition<Boolean> waitCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				Object ret = ((JavascriptExecutor) driver).executeScript("return " + jsCondition);
				return ret.equals(true);
			}
		};
		wait.until(waitCondition);
		resetImplicitWait();
	}

	public boolean isVisible(WebElement we) {
		logger.i("isVisible");
		return we.isDisplayed();
	}

	public String getCurrentUrl() {
		logger.i("getCurrentUrl");
		return driver.getCurrentUrl();
	}

	public boolean isSelected(WebElement we) {
		logger.i("isSelected");
		return we.isSelected();
	}

	public void selectCheckbox(WebElement we) {
		logger.i("selectCheckbox");
		if (!isSelected(we)) {
			logger.d("Element not checked, perform click");
			clickAndWait(we, false);
		} else {
			logger.d("Element already checked, skipping click");
		}
	}

	public void unselectCheckbox(WebElement we) {
		logger.i("unselectCheckbox");
		if (isSelected(we)) {
			logger.d("Element checked, perform click");
			clickAndWait(we, false);
		} else {
			logger.d("Element already unchecked, skipping click");
		}
	}

	public String getText(WebElement we) {
		logger.i(we.getText());
		String message = we.getText();
		return message;
	}

	public WebDriver wd() {
		return driver;
	}

	public void quit() {
		logger.i("Driver Quit");
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}

	public String getTextByAttribute(WebElement we, String attribute) {
		String message = we.getAttribute(attribute);
		logger.i("getTextByAttribute=%s", message);
		return message;
	}

	public void selectByLabel(WebElement we, String label) {
		logger.i("selectByLabel, label=%s", label);
		Select select = new Select(we);
		select.selectByVisibleText(label);
	}

	public void selectByIndex(WebElement we, int index) {
		logger.i("selectByIndex, index=%s", index);
		Select select = new Select(we);
		select.selectByIndex(index);
	}

	public String getSelectedLabel(WebElement we) {
		logger.i("getSelectedLabel");
		Select select = new Select(we);
		String actLabel = select.getFirstSelectedOption().getText();
		logger.d("getSelectedLabel, actualLabel=%s", actLabel);
		return actLabel;
	}

	public void verifySelectedLabel(WebElement we, String expLabel) {
		logger.i("verifySelectedLabel, expLabel=%s", expLabel);
		String actLabel = getSelectedLabel(we);

		Assert.assertEquals(actLabel, expLabel, "Selected label mismatch!");
	}

	public void verifyText(WebElement we, String expText) {
		logger.i("verifyText");
		String actText = getText(we);
		Assert.assertEquals(actText, expText, "Element text mismatch!");
	}

	public void verifyTextNotInElement(WebElement we, String expText) {
		logger.i("verifyTextNotInElement");
		String actText = getText(we);
		Assert.assertNotEquals(actText, expText, "Element text mismatch!");
	}

	public String getValue(WebElement we) {
		logger.i("getValue");
		String actValue = we.getAttribute("value");
		logger.d("getValue=%s", actValue);
		return actValue;
	}

	public void verifyValue(WebElement we, String expValue) {
		logger.i("verifyValue");
		String actValue = getValue(we);
		Assert.assertEquals(actValue, expValue, "Element value mismatch!");
	}

	public void takeScreenshot(String fileSuffix) {
		try {

			WebDriver augmentedDriver = new Augmenter().augment(wd());
			File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
			String relativePath = "UI" + File.separator + "_" + fileSuffix + ".png";

			File outputDir = new File(outputPath);
			String parentDir = outputDir.getParent();
			File finalPath = new File(parentDir, relativePath);
			FileUtils.copyFile(screenshot, finalPath);

		} catch (Exception e) {
			logger.e("Error while taking screenshot", e);
		}
	}

	public void captureScreenshot(String fileSuffix) {
		try {

			WebDriver augmentedDriver = new Augmenter().augment(wd());
			File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);

			String timestamp = new SimpleDateFormat("YYYYMMdd_hhmmss").format(new Date());
			String nameScreenshot = browser.toString().toUpperCase() + "_" + timestamp + "_" + fileSuffix + ".png";

			String relativePath = "FailedScenarioScreenShots" + File.separator + nameScreenshot;
			String reportSrcPath = "." + File.separator + ".." + File.separator + relativePath;

			File outputDir = new File(outputPath);
			String parentDir = outputDir.getParent();
			File finalPath = new File(parentDir, relativePath);
			FileUtils.copyFile(screenshot, finalPath);

			logger.i("ScreenShot: <br/> <a target=\"_blank\" href=\"" + reportSrcPath + "\"><img width=\"500\" src=\""
					+ reportSrcPath + "\" alt=\"" + fileSuffix + "\"/></p></a><br />");
		} catch (Exception e) {
			logger.e("Error while taking screenshot", e);
		}
	}

	public void waitForAllElementsVisible(List<WebElement> weLst) throws InterruptedException {

		Thread.sleep(1000);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);

		wait.until(ExpectedConditions.visibilityOfAllElements(weLst));
		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
	}

	public void waitForElementVisible(WebElement we) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);
		wait.until(ExpectedConditions.visibilityOf(we));
		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
	}
	public void waitForElementNotVisible(WebElement we) {

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, limit);
		//wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(we)));
		wait.until(ExpectedConditions.invisibilityOf(we));
		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);

	}
	public void verifyIsNotVisible(WebElement we) {
		logger.i("verifyIsNotVisible");

		if (!isVisible(we)) {
			Assert.assertFalse(isVisible(we), "Element '" + we + "' is found visible!");
		} else {
			Assert.assertTrue(isVisible(we), "Element count is non-zero!");
		}
	}

	public void verifyIsSelected(WebElement we) {

		logger.i("verifyIsSelected");
		Assert.assertTrue(isSelected(we), "Element '" + we + "' is not selected!");
	}

	public void verifyIsNotSelected(WebElement we) {
		logger.i("verifyIsNotSelected");
		Assert.assertFalse(isSelected(we), "Element '" + we + "' is found selected!");
	}

	public void verifyTextBoxNotEmpty(WebElement we) {
		logger.i("verifyTextBoxNotEmpty");
		Assert.assertNotSame(we.getAttribute("value"), "", "Element '" + we + "' Text Box is empty ");
	}

	public void verifyElementTextNotEmpty(WebElement we) {
		logger.i("verifyElementTextNotEmpty");
		Assert.assertNotSame(we.getText(), "", "Element '" + we + "' Text is empty ");
	}

	public void scrollBottom() {
		logger.i("scrollBottom");
		((JavascriptExecutor) driver).executeScript(
				"window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight))");
	}

	public void alertConfirm(boolean switchBackToMainWindow) {
		logger.i("alertConfirm: backToMainWaindow=%s", switchBackToMainWindow);

		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();

			sleep(2000, "Required time to settle down after alert in IE");
		} catch (Exception e) {
			logger.e(e);
		}

		if (switchBackToMainWindow) {
			driver.switchTo().defaultContent();
		}
	}

	public void promptConfirm(String sendKeys) {
		try {
		Alert promptAlert  = driver.switchTo().alert();
		//Send some text to the alert
		promptAlert .sendKeys(sendKeys);
		sleep(2000, "Required time to settle down after alert in IE");
     	}

    	catch (Exception e) {
	 	logger.e(e);
	  }
	}
	public void alertConfirm() {
		alertConfirm(true);
	}

	public void alertDismiss() {
		logger.i("alertDismiss");

		try {
			Alert alert = driver.switchTo().alert();
			alert.dismiss();

			// Required time to settle down after alert in IE
			sleep(2000, "Required time to settle down after alert in IE");
		} catch (Exception e) {
			logger.e(e);
		}

		driver.switchTo().defaultContent();
	}

	public void switchToNewWindow() {

		logger.i("switchToNewWindow");

		int trials = 3;

		Set<String> windowIds;
		do {
			windowIds = driver.getWindowHandles();
			if (trials != 3) {
				sleep(1000, "Requires time to select newly opened window");
			}
		} while (windowIds.size() <= 1 && --trials > 0);

		// Switch to new window opened
		for (String winHandle : windowIds) {
			if (!winHandle.equals(mainWindowHandle)) {
				logger.d("Switching to '%s'", winHandle);
				driver.switchTo().window(winHandle);
			}
		}
	}

	public void switchToMainWindow() {
		logger.i("switchToMainWindow, mainWindowHandle=%s", mainWindowHandle);

		Assert.assertNotNull(mainWindowHandle, "Main Window Handle not initialized!");

		driver.switchTo().window(mainWindowHandle);
	}

	public void closeWindow() {
		logger.i("closeWindow");

		driver.close();
	}

	public void verifyWindowTitle(String title) {
		logger.i("verifyWindowTitle, expTitle=%s", title);

		String actTitle = getTitle();

		logger.d("verifyWindowTitle, actTitle=%s", actTitle);

		Assert.assertEquals(actTitle, title, "Window title mismatch!");
	}

	public void mouseHoverClickOnElement(WebElement ele) {

		 Actions action = new Actions(driver);
		logger.d("mouseHover And Click On "+ele.getText());
		action.moveToElement(ele).click().build().perform();
	}

	public void clickOnElementOnSpecificDimesion(WebElement clickOnTopSelectMenu) {
		 Actions action = new Actions(driver);
		int xyard=clickOnTopSelectMenu.getSize().width-10;
		int yard=clickOnTopSelectMenu.getSize().height-5;
		action.moveToElement(clickOnTopSelectMenu,xyard,yard).perform();
		action.moveToElement(clickOnTopSelectMenu,xyard,yard).clickAndHold().release().build().perform();
	}

	public void clickOnCheckBoxOnSpecificDimesion(WebElement clickOnTopSelectMenu) {
		 Actions action = new Actions(driver);
		int xyard=clickOnTopSelectMenu.getSize().width/2;
		int yard=clickOnTopSelectMenu.getSize().height/2;
		action.moveToElement(clickOnTopSelectMenu,xyard,yard).perform();
		action.moveToElement(clickOnTopSelectMenu,xyard,yard).clickAndHold().release().build().perform();
	}

	public void dragAndDrop(WebElement sourceWE, WebElement targetWE) {
		logger.i("dragAndDrop");

		(new Actions(driver)).dragAndDrop(sourceWE, targetWE).perform();
	}

	public void setImplicitWait(int timeInSec) {

		driver.manage().timeouts().implicitlyWait(timeInSec, TimeUnit.SECONDS);
	}

	public void resetImplicitWait() {

		driver.manage().timeouts().implicitlyWait(limit, TimeUnit.SECONDS);
	}

	public void refreshPage() {
		logger.i("refreshPage");
		driver.navigate().refresh();
	}

	public void bakcPage() {
		driver.navigate().back();
	}

	public void sleep(long milliseconds, String reasonForSleep) {
		Assert.assertNotEquals("", reasonForSleep.trim(), "Reason for sleep not specified!");

		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
			logger.e(e);
		}
	}

	public void gotoUrl(String urlAddress) {
		logger.i("gotoUrl: " + urlAddress);
		driver.get(urlAddress);
	}

	public void maximizeWindow() {
		logger.i("Maximize window");
		driver.manage().window().maximize();
	}

	public void verifyElementContainsText(WebElement we, String expLabel) {
		logger.i("verifySelectedLabel, expLabel=%s", expLabel);
		String actText = getText(we);

		Assert.assertTrue(actText.contains(expLabel), "Expected String is not present");

	}

	public void verifyElementTextIsSubstring(WebElement we, String expLabel) {
		logger.i("verifySelectedLabel, expLabel=%s", expLabel);
		String actText = getText(we);

		Assert.assertTrue(expLabel.contains(actText), "Expected String is not present");

	}

	public String alertGetMessage() {
		logger.i("getAlertMessage");
		String msg = "";
		try {
			Alert alert = driver.switchTo().alert();
			msg = alert.getText();
			logger.d("getAlertMessage: %s", msg);
		} catch (Exception e) {
			logger.e(e);
		}

		return msg;

	}

	public void verifyElementTextMatchesRegex(WebElement we, String regex) {
		logger.i("verifyElementTextMatchesRegex, regex=%s", regex);
		String actText = getText(we);
		Assert.assertTrue(actText.matches(regex), "Actual String does not matches Regex");
	}

	public String[] getAllListItems(WebElement we) {
		logger.i("getAllListItems");

		Select select = new Select(we);
		List<WebElement> options = select.getOptions();
		int j = 0;
		String[] val = new String[options.size()];
		for (WebElement weOption : options) {
			val[j++] = weOption.getText();
		}

		logger.d("getAllListItems: %s", Utilities.getStringFromArray(val));

		return val;
	}

	public void verifyAllListItems(WebElement we, String[] items) {
		String expItemsStr = Utilities.getStringFromArray(items);
		logger.i("verifyAllListItems: expItemsLen=%d, expItems=%s", items.length, expItemsStr);
		String[] actItems = getAllListItems(we);

		Assert.assertEquals(actItems.length, items.length, "List items length mismatch!");
		Assert.assertEquals(Utilities.getStringFromArray(actItems), expItemsStr, "List items mismatch!");
	}

	public boolean isPresent(WebElement we) {
		logger.i("isPresent");
		boolean present = we.isDisplayed();
		logger.d("isPresent: %s", present);
		return present;
	}

	public String getTitle() {
		logger.i("getTitle");
		String actTitle = driver.getTitle();
		logger.d("getTitle: %s", actTitle);
		return actTitle;
	}

	public void waitForAlert() {
		logger.i("waitForAlert");
		WebDriverWait wait = new WebDriverWait(driver, limit);
		wait.until(ExpectedConditions.alertIsPresent());
	}

	public void verifyColor(WebElement we, String expcolor) {

		String color = we.getCssValue("color");

		String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
		int r = Integer.parseInt(numbers[0].trim());
		int g = Integer.parseInt(numbers[1].trim());
		int b = Integer.parseInt(numbers[2].trim());
		String hex = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
		Assert.assertEquals(hex.toUpperCase(), expcolor.toUpperCase(),
				"Element text color mismatch! Expected:" + expcolor.toUpperCase() + "actual:" + hex.toUpperCase());

	}

	public void verifyBackgroundColor(WebElement we, String expcolor) {

		String color = we.getCssValue("background-color");
		String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
		int r = Integer.parseInt(numbers[0].trim());
		int g = Integer.parseInt(numbers[1].trim());
		int b = Integer.parseInt(numbers[2].trim());
		String hex = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
		Assert.assertEquals(hex.toUpperCase(), expcolor.toUpperCase(), "Element background color mismatch! Expected:"
				+ expcolor.toUpperCase() + "actual:" + hex.toUpperCase());

	}

	public void verifyElementValueIsEmpty(WebElement we) {
		logger.i("verifyElementValueIsEmpty");

		Assert.assertEquals(getValue(we), "", "Element '" + we + "' Value is not empty");

	}

	public void openDuplicateWindow() {
		logger.i("openDuplicateWindow");
		((JavascriptExecutor) driver).executeScript("(window.open(document.URL))");

	}



	public void verifyTextBoxIsNotEditable(WebElement we) {
		logger.i("verifyTextBoxIsNotEditable");
		String value = we.getAttribute("readonly");
		Assert.assertTrue(value.contentEquals("true"), "Element '" + we + "' Is Editable");

	}

	public void verifyTextIsUnderLined(WebElement we) {
		logger.i("verifyTextBoxIsNotEditable");
		String value = we.getAttribute("style");
		Assert.assertTrue(value.contains("text-decoration: underline"),
				"Text of Element '" + we + "' Is not Underlined");

	}

	public void uploadFile(WebElement we, String path) {
		logger.i("uploadFile");
		we.sendKeys(path);
	}

	public void waitForElementTextNotEmpty(WebElement we) {

		logger.i("waitForElementTextNotEmpty");

		int trials = 10;
		int len;

		do {
			String text = getText(we);
			len = text.length();
			if (trials != 10) {
				logger.d("Waiting");
				sleep(2000, "Requires time for text to be displayed");
			}
		} while (len <= 1 && --trials > 0);

	}

	public void waitForElementValueNotEmpty(WebElement we) {

		logger.i("waitForElementValueNotEmpty");

		int trials = 10;
		int len;

		do {
			String text = getValue(we);
			len = text.length();
			if (trials != 10) {
				logger.d("Waiting");
				sleep(2000, "Requires time for text to be displayed");
			}
		} while (len <= 1 && --trials > 0);

	}

	public void clearText(WebElement we) {
		logger.i("clearText");
		we.clear();
	}

	public void verifyElementContainsValue(WebElement we, String expLabel) {
		logger.i("verifySelectedLabel, expLabel=%s", expLabel);

		String actValue = getValue(we);
		Assert.assertTrue(actValue.contains(expLabel), "Expected String is not present");

	}

	public void verifyElementValueNotEmpty(WebElement we) {
		logger.i("verifyElementValueNotEmpty");

		Assert.assertNotEquals(getValue(we), "", "Element '" + we + "' Value is empty");

	}

	public void selectRadioButton(List<WebElement> weLst, int optionNo) {
		logger.i("selectRadioButton");
		if (optionNo > 0 && optionNo <= weLst.size()) {
			weLst.get(optionNo - 1).click();
		} else {
			throw new NotFoundException("option " + optionNo + " not found");
		}
	}

	public void verifySelectionBoxIsMultiple(String elementId) {

		logger.i("verifyIsMultipleSelectionBox");

		Object isMultiple = ((JavascriptExecutor) driver)
				.executeScript("return document.getElementById('" + elementId + "').multiple");
		Assert.assertEquals(isMultiple, true, "Element is not Multiple Selection Box");
	}

	public int getselectedRadioButton(List<WebElement> weLst) {

		logger.i("getselectedRadioButton");

		int optionNo = 0;
		for (int i = 0; i <= weLst.size(); i++) {
			if (weLst.get(i).isSelected()) {
				optionNo = (i + 1);
				break;
			}
		}

		return optionNo;
	}

	public void verifyJSCondition(final String jsCondition, boolean status) {

		logger.i("verifyJSCondition: %s", jsCondition);
		Object ret = ((JavascriptExecutor) driver).executeScript("return " + jsCondition);
		Assert.assertEquals(status, ret, "Status for jsCondition :" + jsCondition + " is not '" + status + "'");

	}

	public void executeJSCondition(final String jsCondition) {

		logger.i("executeJSCondition: %s", jsCondition);
		((JavascriptExecutor) driver).executeScript(jsCondition);
	}

	public void verifyScrollBarPresent(WebElement elementId) {
		logger.i("verifyScrollBarPresent");
		String condition = "document.getElementById('" + elementId + "').scrollHeight > document.getElementById('"
				+ elementId + "').clientHeight";
		Object ret = ((JavascriptExecutor) driver).executeScript("return " + condition);
		Assert.assertEquals(ret, true, "Scroll Bar with 'element Id' :" + elementId + " is not present!");

	}


	public void scrollUntilElementIsView(WebElement ele) {
		this.waitForElementVisible(ele);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
	}

	public void waitForChangeInLengthOfValue(final WebElement we, final int valueLen) {

		logger.i("waitForChangeInLengthOfValue");
		setImplicitWait(0);

		WebDriverWait wait = new WebDriverWait(driver, limit);

		ExpectedCondition<Boolean> waitCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				boolean flag = false;
				if (we.getAttribute("value").length() != valueLen) {
					flag = true;
				}
				return flag;
			}
		};

		wait.until(waitCondition);

		resetImplicitWait();
	}

	public void appendText(WebElement we, String text) {
		logger.i("appendText, text=%s", text);
		we.sendKeys(text);
	}

	public void verifyScrollBarNotPresent(String elementId) {
		logger.i("verifyScrollBarPresent");
		String condition = "document.getElementById('" + elementId + "').scrollHeight > document.getElementById('"
				+ elementId + "').clientHeight";
		Object ret = ((JavascriptExecutor) driver).executeScript("return " + condition);
		Assert.assertEquals(ret, false, "Scroll Bar with 'element Id' :" + elementId + " is present!");
	}

	public void waitForWindowToClose() {

		final int windowCount = driver.getWindowHandles().size();

		logger.i("waitForWindowToClose");
		if (windowCount > 1) {

			setImplicitWait(0);

			WebDriverWait wait = new WebDriverWait(driver, limit);

			ExpectedCondition<Boolean> windowClosedCondition = new ExpectedCondition<>() {
				public Boolean apply(WebDriver driver) {
					assert driver != null;
					return driver.getWindowHandles().size() < windowCount;
				}
			};

			wait.until(windowClosedCondition);

		}

		switchToMainWindow();
		resetImplicitWait();

	}

	public boolean isAlertPresent() {

		boolean presentFlag = false;

		try {
			logger.i("isAlertPresent");

			WebDriverWait wait = new WebDriverWait(driver,
					15 /*
						 * timeout in seconds
						 */);
			if (wait.until(ExpectedConditions.alertIsPresent()) != null) {
				// Alert present; set the flag
				presentFlag = true;
			}  // Alert present; set the flag

			// Check the presence of alert
			// driver.switchTo().alert();
			// Alert present; set the flag
			// presentFlag = true;
			// driver.switchTo().defaultContent();

		} catch (NoAlertPresentException ex) {
			// Alert not present
			logger.e(ex);
		}
		return presentFlag;
	}

	public void selectByValue(WebElement we, String value) {
		this.sleep(2000,"For showing case Create page");
		logger.i("selectByValue, label=%s", value);
		Select select = new Select(we);
		select.selectByValue(value);
	}

	public void selectByVisibleText(WebElement we, String value) {
		logger.i("selectByValue, label=%s", value);
		Select select = new Select(we);
		select.selectByVisibleText(value);
	}

	public void verifyIsEnabled(WebElement we) {

		logger.i("verifyIsEnabled");

		Assert.assertTrue(isEnabled(we), "Element '" + we + "' is not enabled!");
	}

	public void verifyIsDisabled(WebElement we) {

		logger.i("verifyIsDisabled");
		Assert.assertFalse(isEnabled(we), "Element '" + we + "' is not enabled!");
	}

	public boolean isEnabled(WebElement we) {
		logger.i("isEnabled");
		return we.isEnabled();
	}

	public boolean isReadOnly(WebElement we) {
		logger.i("isReadOnly");
		return Boolean.parseBoolean(we.getAttribute("readonly"));
	}

	public boolean findElementByXpath(String object) {
		logger.i("findElementByXpath");
		try {
			driver.findElement(By.xpath(object));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getTextColor(WebElement we) {
		logger.i("getTextColor");
		String color = we.getCssValue("color");

		if (!color.contains("#")) {
			String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
			int r = Integer.parseInt(numbers[0].trim());
			int g = Integer.parseInt(numbers[1].trim());
			int b = Integer.parseInt(numbers[2].trim());
			color = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
		}
		return color;
	}

	public void mouseHoverOnElement(WebElement we) {

		Actions action = new Actions(driver);
		action.moveToElement(we).moveToElement(we).build().perform();
	    this.sleep(2000,"Check Is Hover Show Detail");
	}


	public void moveOnElement(WebElement we) {
		logger.i("moveOnElement "+we.getText());

		Actions actions = new Actions(driver);
		actions.moveToElement(we);
		actions.perform();

	}

	public void Wait(int timeOut) {
		driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);
	}

	public WebElement FindElementByCss(String object)
	{
		logger.i("findElementByCSS");
		try {
			WebElement element = driver.findElement(By.cssSelector(object));
			return element;
		} catch (Exception e) {
			return null;
		}
	}

	public List<WebElement> FindElementsByCss(String object) {
		List<WebElement> element;
		try {
			element=driver.findElements(By.cssSelector(object));
			return element;
		} catch (Exception e) {
			return null;
		}

	}

	public List<String> getListItemFromString(String stringArray) {

		List<String> secondDropDownItemList;
		try {
			secondDropDownItemList = new ArrayList<>(Arrays.asList(stringArray.split(",")));
			return secondDropDownItemList;
		} catch (Exception e) {
			return null;
		}
	}

	public void selectItemFromListItem(List<WebElement> we,String selectItem) {
		try {
			boolean isitemGet=false;
			for(WebElement DropDownItem:we){
				if(DropDownItem.getText().trim().contains(selectItem)) {
					    Actions action = new Actions(driver);
						logger.i("mouseHoverClickOnElement "+selectItem);
						isitemGet=true;
						action.moveToElement(DropDownItem).click().build().perform();
				        break;
				 }
			}

			if(!isitemGet) {
				logger.e("Selected item is Not Found");
			}

		} catch (Exception e) {
			Assert.fail(e.getMessage());
			logger.e(e.getMessage());
		}
	}

	  public void scrollHorizontalUntillElementIsView(WebElement ele) {
		  ((JavascriptExecutor) driver).executeScript("window.scrollBy(1000,0)", ele);
         logger.i("Scroll Horzontal");
	     this.sleep(2000,"Element find");
		}

       public void scrollHorizontalUntillListElementsIsView(List<WebElement> eleList) {
		   ((JavascriptExecutor) driver).executeScript("window.scrollBy(1000,0)", eleList);
         logger.i("Scroll Horzontal");
	     this.sleep(2000,"Element find");

		}


	public boolean isClickable(WebElement el)
	{
		try{
			WebDriverWait wait = new WebDriverWait(driver, 6);
			wait.until(ExpectedConditions.elementToBeClickable(el));
			return true;
		}
		catch (Exception e){
			return false;
		}
	}


}