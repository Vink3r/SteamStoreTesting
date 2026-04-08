import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class DevPageTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void navigateToDevPage() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //If parallel, jump to store, if serial, we should already be here
        if (!getDriver().getCurrentUrl().contains("app/620")) {
            getDriver().get("https://store.steampowered.com/app/620/Portal_2/");
        }

        //Click on "Valve" in the Developer section
        WebElement devLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='developers_list']//a")));
        devLink.click();
        captureState("DevPage_Valve_Loaded");
    }

    @Test(priority = 1)
    public void testDevNavigationAndUrl() {
        //Verify URL contains 'developer/valve' or the specific dev ID
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("developer") || currentUrl.contains("pub/valve"), "Navigation to Developer page failed.");
    }

    @Test(priority = 2)
    public void testFollowButtonTrigger() {
        //Click Follow Button -> Please Log in pop up
        WebElement followBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("CuratorFollowBtn_4")));
        followBtn.click();

        //Verify the login modal appears
        WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("newmodal")));
        Assert.assertTrue(loginModal.isDisplayed(), "Login popup did not appear after clicking Follow.");

        //Close the modal to continue testing (clicking outside or the 'X')
        getDriver().findElement(By.xpath("/html/body/div[4]/div[3]/div/div[2]/div/span")).click();
    }

    @Test(priority = 3)
    public void testCorrectDisplayName() {
        //Verification of the Page Header/Name
        WebElement devName = getDriver().findElement(By.id("header_curator_details"));
        Assert.assertTrue(devName.getText().contains("Valve"), "Developer name does not match.");
    }

    @Test(priority = 4)
    public void testBannerDisplay() {
        //Check for the custom developer banner/background
        WebElement banner = getDriver().findElement(By.className("background_header_ctn"));
        Assert.assertTrue(banner.isDisplayed(), "Developer banner is missing.");
    }

    @Test(priority = 5)
    public void testDoubleBackNavigation() {
        getDriver().navigate().back();  //Back to Game Page
        getDriver().navigate().back();  //Back to Search Results

        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search"), "Failed to return to search page after double-back navigation.");
        captureState("DevPage_DoubleBack_Success");
    }
}