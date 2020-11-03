package testScripts;

import configurations.BaseTest;
import configurations.ReadXMLData;
import org.testng.annotations.Test;

public class CheckUserLogout extends BaseTest {

	@Test(enabled = true)
	public void LogoutUserFromTerra()  {
		try {
			ObTerraLogin().Logout();
		} 
		 catch (Exception e) {
			logger.e("Tcs Failed");
		}	
	}
}


