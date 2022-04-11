package testCases;

import configurations.BaseTest;
import configurations.ReadXMLData;
import org.testng.annotations.Test;

public class CheckLaunchEvent extends BaseTest {

    protected ReadXMLData EventData = new ReadXMLData(
            "./TestData/User/EventData.xml");

    String EventName = EventData.get("EventDetails", "EventName");

    @Test(enabled = true)
    public void LaunchEvent()  {
        try {
            ObTerraEventListPage().searchAndLaunchEvent(EventName);
        }
        catch (Exception e) {
            logger.e("Tcs Failed");
        }
    }
}
