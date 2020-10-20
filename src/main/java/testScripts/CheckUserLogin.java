package testScripts;

import configurations.BaseTest;
import configurations.ReadXMLData;
import org.testng.annotations.Test;

public class CheckUserLogin extends BaseTest {

	protected ReadXMLData LoginData = new ReadXMLData(
			"./TestData/User/LoginData.xml");

	String UserName = LoginData.get("UserDetails", "UserName");
	String Password = LoginData.get("UserDetails", "Password");

	@Test(enabled = true)
	public void LoginIntoTerraEventApp()  {
		try {
			ObTerraLogin().LoginIntoBridgestone("Event App",Environment,UserName,Password);
		}
		catch (Exception e) {
			logger.e("Tcs Failed");
		}
	}
}


