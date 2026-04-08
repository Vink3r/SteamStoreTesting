import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class PublisherPageTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void navigateToPublisher() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //Since Portal 2 has the same Publisher, we will use a different game
        //Go to Marvel Rivals (NetEase) to find the Publisher link
        getDriver().get("https://store.steampowered.com/app/2767030/Marvel_Rivals/");
    }

    @Test(priority = 1)
    public void testPublisherSearchRedirect() {
        WebElement pubLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='dev_row'][contains(., 'Publisher')]//a")));
        pubLink.click();
        captureState("PublisherPage_NetEase_Loaded");

        //The Publisher doesn't have a dedicated page so Steam will bring us to search page
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("search/?publisher=NetEase%20Games"), "URL did not redirect to the filtered search results for NetEase.");
    }

    @Test(priority = 2)
    public void testSpecificGameInResults() {
        //Find "Blood Strike" in their result
        WebElement bloodStrike = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='search_resultsRows']//span[contains(text(), 'Blood Strike')]")));
        Assert.assertTrue(bloodStrike.isDisplayed(), "Blood Strike was not found in the publisher's list.");
    }

    @Test(priority = 3)
    public void testReturnViaSearchBox() {
        WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
        searchBox.click();
        searchBox.sendKeys("Portal 2");

        //Wait for the dynamic suggestion dropdown to appear
        //Steam's live search results usually have the ID 'searchSuggestions_«r0»'
        WebElement firstSuggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='searchSuggestions_«r0»']//a[contains(@href, 'app/620')]")));
        //Click the dynamic result directly
        firstSuggestion.click();

        //Verify we bypassed the search result page and landed straight on the App page
        wait.until(ExpectedConditions.urlContains("app/620"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("app/620"), "Failed to navigate directly to Portal 2 via live search suggestions.");

        captureState("LiveSearch_Direct_Navigation");
    }

    @Test(priority = 4)
    public void testValveURLVerification() {
        //Navigate to Valve Publisher page from the Portal 2 page
        WebElement valveLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='dev_row'][contains(., 'Publisher')]//a[text()='Valve']")));
        valveLink.click();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("publisher/valve"), "Valve publisher URL mismatch.");
        captureState("Valve_Publisher_Home");
    }

    @Test(priority = 5)
    public void testFollowerExtraction() {
        //Has About Page to click to and extract the Followers count
        //Valve's publisher page has an 'About' tab
        WebElement aboutTab = wait.until(ExpectedConditions.elementToBeClickable(By.className("about")));
        aboutTab.click();

        WebElement followerCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("count")));
        String countText = followerCount.getText();

        System.out.println("Valve's Followers: " + countText);

        Assert.assertTrue(getDriver().getCurrentUrl().contains("publisher/valve/about/"), "Valve publisher about URL mismatch.");
        captureState("Publisher_About_Page");
    }
}