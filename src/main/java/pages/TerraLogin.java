package pages;

import configurations.ReadXMLData;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import configurations.BaseDriver;

public class TerraLogin extends AbstractionPOM{

    public static String GoToUrl = null;

    public TerraLogin(BaseDriver bdriver) {
        super(bdriver);
    }

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement LoginButton; 

    @FindBy(xpath = "//input[@id='email']")
    private WebElement EmailIdInput;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement PasswordInput;

    @FindBy(xpath = "//div[@class=' row action-bar']//span[text()='Events Manager']")
    private WebElement VerifyAdminToolLoginText;

    @FindBy(xpath = "//div[@class='detail-header__title detail-header__title-center mb-0']")
    private WebElement VerifyEventAppLoginText;

    @FindBy(css = "button[class='user-menu__button']")
    private WebElement UserMenuButton;

    @FindBy(xpath = "//a[contains(text(),'Logout')]")
    private WebElement LogoutButton;

    //will Navigate to Terra Home Page
    public void LoginIn(String tool,String emailId,String password) {
        try {
            String GoToUrl="";
            tool=tool.toLowerCase();
            try {
                String endURL=project.toLowerCase()+""+Environment.toLowerCase();
                GoToUrl = fwConfigData.get(endURL,tool).toString();
            }
            catch (Exception e) {
                Assert.fail(e.getMessage());
            }

            bdriver.gotoUrl(GoToUrl);
            bdriver.waitForElementVisible(EmailIdInput);
            bdriver.inputText(EmailIdInput,emailId);

            bdriver.waitForElementVisible(PasswordInput);
            bdriver.inputText(PasswordInput,password);

            bdriver.waitForElementVisible(LoginButton);
            bdriver.clickAndWait(LoginButton);

                if(tool.equalsIgnoreCase("AdminTool")) {
                    bdriver.waitForElementVisible(VerifyAdminToolLoginText);
                    if(!bdriver.getText(VerifyAdminToolLoginText).equalsIgnoreCase("Events Manager")) {
                        bdriver.captureScreenshot("User are not logged in");
                        Assert.fail("Actual and Expected Home page title is not matched");
                    }
                }

                else if (tool.equalsIgnoreCase("EventApp")) {
                    bdriver.waitForElementVisible(VerifyEventAppLoginText);
                    if(!bdriver.getText(VerifyEventAppLoginText).equalsIgnoreCase("Terra Events")) {
                        bdriver.captureScreenshot("User are not logged in");
                        Assert.fail("Actual and Expected Home page title is not matched");
                    }

                    bdriver.waitForElementVisible(UserMenuButton);
                    bdriver.clickAndWait(UserMenuButton);
                    bdriver.waitForElementVisible(LogoutButton);
                    bdriver.clickAndWait(UserMenuButton);
                }

        } catch (Exception e) {
            bdriver.captureScreenshot("Error produced while logging on");
            Assert.fail("Login Failed On " + GoToUrl+" because of ");
        }
    }

    //Log out from application
    public void Logout() {
        try {
            bdriver.waitForElementVisible(UserMenuButton);
            bdriver.clickAndWait(UserMenuButton);
            bdriver.waitForElementVisible(LogoutButton);
            bdriver.clickAndWait(LogoutButton);
            bdriver.takeScreenshot("Page_AfterLogout");
            bdriver.waitForElementVisible(LoginButton);
        } catch (Exception e) {
            bdriver.captureScreenshot("Error produced while logging on");
            Assert.fail("Login Failed On " + GoToUrl+" because of ");
        }
    }
}
