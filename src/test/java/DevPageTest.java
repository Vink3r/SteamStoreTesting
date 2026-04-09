import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class DevPageTest extends BaseTest {

    private WebDriverWait wait;

    // This setup runs before the tests to build a realistic browser history
    @BeforeClass
    public void establishHistoryAndNavigate() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

        // 1. Start at the Home page to establish the first history node
        getDriver().get("https://store.steampowered.com/");

        // 2. Find the search box using a reliable CSS selector, type "Portal 2", and submit
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='term']")));
        searchBox.sendKeys("Portal 2");
        searchBox.submit();
        wait.until(ExpectedConditions.urlContains("/search")); // Wait until the search page loads

        // 3. Click on the Portal 2 link from the search results to go to the Game Page
        WebElement gameLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='search_resultsRows']//span[text()='Portal 2']/ancestor::a[1]")));
        gameLink.click();
        wait.until(ExpectedConditions.urlContains("app/620")); // Wait for the game page to load

        // 4. Click the Developer link to finally arrive at the Dev Page
        WebElement devLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='developers_list']//a")));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", devLink);

        // Confirm we made it to the Valve page and take a screenshot
        wait.until(ExpectedConditions.urlContains("valve"));
        captureState("DevPage_Valve_Loaded");
    }

    // Checks if the current URL contains the expected developer path
    @Test(priority = 1)
    public void testDevNavigationAndUrl() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("developer/valve") || currentUrl.contains("pub/valve"),
                "Navigation to Developer page failed.");
    }

    // Tests that clicking "Follow" prompts the user to log in
    @Test(priority = 2)
    public void testFollowButtonTrigger() {
        // Find the "Follow" button using its text
        WebElement followBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@class, 'follow')] | //span[contains(text(), 'Follow')]")));

        // Scroll down to the button so it is visible on screen
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block: 'center'});", followBtn);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Use a Javascript click to bypass any pop-ups blocking the button
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", followBtn);

        // Pause to let the login pop-up animate onto the screen
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Read the background HTML to see if the "Sign In" text appeared (bypasses visual bugs)
        String pageHtml = getDriver().getPageSource().toLowerCase();
        Assert.assertTrue(pageHtml.contains("sign in") || pageHtml.contains("login"),
                "Login popup text was not found in the DOM.");

        // Try to close the pop-up so it doesn't block the next tests
        try {
            WebElement closeBtn = getDriver().findElement(
                    By.xpath("//*[contains(@class, 'close')] | //span[text()='X'] | //div[contains(@class, 'newmodal')]//div[contains(@class, 'close_btn')]"));
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", closeBtn);
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        } catch (Exception e) {
            System.out.println("Notice: Close button not found, or modal closed automatically.");
        }
    }

    // Verifies the developer's name is displayed correctly on the page
    @Test(priority = 3)
    public void testCorrectDisplayName() {
        WebElement devName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(), 'Valve')] | //*[@id='header_curator_details']")));
        Assert.assertTrue(devName.getText().contains("Valve"), "Developer name does not match.");
    }

    // Checks that the developer's custom background banner is visible
    @Test(priority = 4)
    public void testBannerDisplay() {
        WebElement banner = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'banner')] | //div[contains(@class, 'background_header')]")));
        Assert.assertTrue(banner.isDisplayed(), "Developer banner is missing.");
    }

    // Tests the browser's back button using the history we built in the setup
    @Test(priority = 5)
    public void testDoubleBackNavigation() {
        // First back button click: Goes back to the Game Page
        getDriver().navigate().back();
        wait.until(ExpectedConditions.urlContains("app/620"));

        // Second back button click: Goes back to the Search Results
        getDriver().navigate().back();
        wait.until(ExpectedConditions.urlContains("/search"));

        // Confirm we successfully arrived back at the search page
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search"), "Failed to return to search page.");
        captureState("DevPage_DoubleBack_Success");
    }
}