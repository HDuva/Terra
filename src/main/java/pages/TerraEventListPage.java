package pages;

import configurations.BaseDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.testng.Assert;

import java.util.List;

public class TerraEventListPage extends AbstractionPOM{

    public TerraEventListPage(BaseDriver bdriver) {
        super(bdriver);
    }

    @FindBy(css = "button[class='user-menu__button']")
    private WebElement UserMenuButton;

    @FindBys({
            @FindBy(css = "div.user-menu__menu-item a.user-menu__menu-item-label")
    })
    private List<WebElement> UserMenuList;

    @FindBys({
            @FindBy(xpath = "//div[@class='event-tile__event-name']")
    })
    private List<WebElement> eventHorizontallyList;

    @FindBy(xpath = "//input[@id='search-value']")
    private WebElement searchBox;

    @FindBy(xpath = "//div[@class='all-event__event-name']")
    private WebElement searchEventName;

    @FindBy(css = ".div.event-details__event-buttons__l-button button")
    private WebElement launchButton;

    @FindBy(xpath = "//span[@class='logo__header']")
    private WebElement headerEventName;

    //will check user profile displayed on event list page
    public void verifyUserProfileMenuList() {
        try {
            bdriver.waitForElementVisible(UserMenuButton);
            bdriver.clickAndWait(UserMenuButton);
            bdriver.waitForAllElementsVisible(UserMenuList);
            bdriver.clickAndWait(UserMenuButton);
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while verifying user profile");
            Assert.fail(e.getMessage());
        }
    }

    //will details of Event Displayed Horizontally on event list page
    public void verifyEventDisplayedHorizontally(String eventName) {
        try {
            bdriver.scrollHorizontalUntillElementIsView(bdriver.driver.findElement(By.xpath("//div[@class='event-tile__event-name'][text()='"+eventName+"']")));
            bdriver.takeScreenshot("Page_AfterVerifyEventHorizontally");
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while verifying event horizontally");
            Assert.fail("Something wrong while verifying event horizontally"+e.getMessage());
        }
    }

    //will search and verify event
    public void searchAndVerifyEvent(String eventName) {
        try {
            bdriver.waitForElementVisible(searchBox);
            bdriver.inputText(searchBox,eventName);
            bdriver.sendKey(searchBox, Keys.ENTER);
            bdriver.waitForElementVisible(searchEventName);
            if(!bdriver.getText(searchEventName).equals(eventName)) {
                bdriver.captureScreenshot("Searched Event name is not same as searched");
                Assert.fail("Searched Event name is not same as searched");
            }
            bdriver.takeScreenshot("Page_AfterSearchEvent");
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while searching and launching event");
            Assert.fail(e.getMessage());
        }
    }

    //will search and launch event
    public void searchAndLaunchEvent(String eventName) {
        try {
            this.searchAndVerifyEvent(eventName);
            bdriver.clickAndWait(searchEventName);
            this.launchEvent(eventName);
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while searching and launching event");
            Assert.fail(e.getMessage());
        }
    }

    //will launch event from horizontally
    public void launchEventFromHorizontally(String eventName) {
        try {
            bdriver.scrollHorizontalUntillElementIsView(bdriver.driver.findElement(By.xpath("//div[@class='event-tile__event-name'][text()='"+eventName+"']")));
            bdriver.clickAndWait(bdriver.driver.findElement(By.xpath("//div[@class='event-tile__event-name'][text()='"+eventName+"']")));
            this.launchEvent(eventName);
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while searching and launching event");
            Assert.fail(e.getMessage());
        }
    }

    // launch event
    public void launchEvent(String eventName) {
        try {
            bdriver.waitForElementVisible(launchButton);
            bdriver.clickAndWait(launchButton);
            bdriver.waitForElementVisible(headerEventName);
            if (bdriver.getText(headerEventName).equalsIgnoreCase(eventName)) {
                bdriver.captureScreenshot("Launch Event name is not same as Displayed event");
                Assert.fail("Displayed event name is different after launch");
            }
        }
        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while searching and launching event");
            Assert.fail(e.getMessage());
        }
    }






}
