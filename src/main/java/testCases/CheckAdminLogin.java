package testCases;

import org.testng.annotations.Test;
import configurations.BaseTest;
import configurations.ReadXMLData;

public class CheckAdminLogin extends BaseTest {

	protected ReadXMLData LoginData = new ReadXMLData(
			"./TestData/Admin/LoginData.xml");
	String UserName = LoginData.get("UserDetails", "UserName");
	String Password = LoginData.get("UserDetails", "Password");

	@Test(enabled = true)
	public void LoginIntoTerraAdmin()  {
		try {
			ObTerraLogin().LoginIn("AdminTool",UserName,Password);
		} 
		 catch (Exception e) {
			logger.e("Tcs Failed");
		}	
	}
}


