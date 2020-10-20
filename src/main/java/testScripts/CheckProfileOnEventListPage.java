package testScripts;

import configurations.BaseTest;
import org.testng.annotations.Test;

public class CheckProfileOnEventListPage extends BaseTest {

    @Test(enabled = true)
    public void verifyProfileSectionOnEventListPage() {
        try {
            ObTerraEventListPage().verifyUserProfile();
        }
        catch (Exception e) {
            logger.e("Tcs Failed");
        }
    }
}