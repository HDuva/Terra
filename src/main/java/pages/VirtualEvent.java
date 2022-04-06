package pages;

import configurations.BaseDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.testng.Assert;

import java.util.List;

public class VirtualEvent extends AbstractionPOM{

    public VirtualEvent(BaseDriver bdriver) {
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

    //will search and launch event
    public void ClickOnProfileIcon(String eventName) {
        try {

        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while clicking on profile icon");
            Assert.fail(e.getMessage());
        }
    }
}
