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

    @FindBy(css = "button[class='user-profile user-points-widget__user-profile'] img")
    private WebElement headerProfileIcon;

    @FindBy(css = "button[class='user-profile profile-modal__user-profile']")
    private WebElement avatarIconProfileModal;

    @FindBys({
            @FindBy(css = "div.user-menu__menu-item a.user-menu__menu-item-label")
    })
    private List<WebElement> UserMenuList;

    //will redirect to edit profile page
    public void ClickOnProfileIcon(String eventName) {
        try {
            bdriver.waitForElementVisible(headerProfileIcon);
            bdriver.clickAndWait(headerProfileIcon);
            bdriver.waitForElementNotVisible(avatarIconProfileModal);
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while clicking on profile icon");
            Assert.fail(e.getMessage());
        }
    }
}
