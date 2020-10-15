package testScripts;

import configurations.BaseTest;
import configurations.ReadXMLData;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

public class CheckUserLogin extends BaseTest {

	protected ReadXMLData LoginData = new ReadXMLData(
			"./TestData/Admin/LoginData.xml");

	String UserName = LoginData.get("UserDetails", "UserName");
	String Password = LoginData.get("UserDetails", "Password");

	@Test(enabled = true)
	public void LoginIntoTerraEventApp()  {
		try {
			ObBridgestoneLogin().LoginIntoBridgestone("Event App",Environment,UserName,Password);
		}
		catch (Exception e) {
			logger.e("Tcs Failed");
		}
	}
}


