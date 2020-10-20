package pages;

import configurations.BaseDriver;
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

    //will check user profile displayed on event list page
    public void verifyUserProfile() {
        try {
            bdriver.waitForElementVisible(UserMenuButton);
            bdriver.clickAndWait(UserMenuButton);
            bdriver.waitForAllElementsVisible(UserMenuList);
        }

        catch(Exception e) {
            bdriver.captureScreenshot("Something wrong while verifying user profile"+e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

}
