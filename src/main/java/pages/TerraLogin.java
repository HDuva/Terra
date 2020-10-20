package pages;

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

    @FindBy(xpath = "//h1[text()='Multi Event Admin']")
    private WebElement VerifyAdminToolLoginText;

    @FindBy(xpath = "//div[@class='detail-header__title detail-header__title-center mb-0']")
    private WebElement VerifyEventAppLoginText;

    //will Navigate to Bridgestone Home Page
    public void LoginIntoBridgestone(String tool,String env,String emailId,String password) {
        try {
            env=env.toLowerCase();
           switch (tool) {
                case "Event App" -> GoToUrl = "https://terra." + env + ".conduit4.com/event/list";
                case "AdminTool" -> GoToUrl = "https://terra." + env + ".conduit4.com/tool/admin/multi/";
                case "Ilt" -> GoToUrl = "https://terra." + env + ".conduit4.com/tool/ilt/Default/content/browse.php";
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
                }

                else if (tool.equalsIgnoreCase("Event App")) {
                    bdriver.waitForElementVisible(VerifyEventAppLoginText);
                    if(!bdriver.getText(VerifyEventAppLoginText).equalsIgnoreCase("Bridgestone Connect")) {
                        bdriver.captureScreenshot("User are not logged in");
                        Assert.fail("Actual and Expected Home page title is not matched");
                    }
                }

        } catch (Exception e) {
            bdriver.captureScreenshot("Error produced while logging on"+e.getMessage());
            Assert.fail("Login Failed On " + GoToUrl+" because of "+e.getMessage());
        }
    }
}
