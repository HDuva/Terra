package pages;

import org.openqa.selenium.support.PageFactory;
import configurations.BaseDriver;
import configurations.BaseTest;

public class AbstractionPOM extends BaseTest {

	// Default constructor
	public AbstractionPOM(BaseDriver bdriver) {
		this.bdriver = bdriver;
		// This initElements method will create all WebElements
		PageFactory.initElements(bdriver.driver, this);
	}
	
	public void updateBDriver(BaseDriver bdriver) {
		logger.i("updateBDriver");
		this.bdriver = bdriver;
		// This initElements method will create all WebElements
				PageFactory.initElements(bdriver.driver, this);
	}
	
}
