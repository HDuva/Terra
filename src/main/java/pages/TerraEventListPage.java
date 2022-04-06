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

    @FindBy(xpath = "//input[@id='search-value']")
    private WebElement searchBox;

    @FindBy(xpath = "//div[@class='all-event__event-name']")
    private WebElement searchEventName;

    @FindBy(xpath = "//button[contains(text(),'Launch Event')]")
    private WebElement launchButton;

    @FindBy(css = "span[class='logo__header']")
    private WebElement headerEventName;

    @FindBy(xpath = "//div[@class='percentage-loader__container']")
    private WebElement Loader;

    //will search and launch event
    public void searchAndLaunchEvent(String eventName) {
        try {
            bdriver.waitForElementVisible(searchBox);
            bdriver.inputText(searchBox,eventName);
            bdriver.sendKey(searchBox, Keys.ENTER);
            bdriver.waitForElementVisible(searchEventName);
            if(!bdriver.getText(searchEventName).equals(eventName)) {
                bdriver.captureScreenshot("Searched Event name is not same as searched");
                Assert.fail("Searched Event name is not same as searched");
            }
            bdriver.clickAndWait(searchEventName);
            bdriver.waitForElementVisible(launchButton);
            bdriver.clickAndWait(launchButton);
            bdriver.waitForElementVisible(headerEventName);
            if (!bdriver.getText(headerEventName).equalsIgnoreCase(eventName)) {
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
