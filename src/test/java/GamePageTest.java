import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class GamePageTest extends BaseTest {

    private WebDriverWait wait;

    // This setup runs before the tests to build a realistic browser history
    @BeforeClass
    public void navigateToPortal2() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));

        // 1. Start at the Home page
        getDriver().get("https://store.steampowered.com/");

        // 2. Perform a search for "Portal 2" to create a search history node
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='term']")));
        searchBox.sendKeys("Portal 2");
        searchBox.submit();
        wait.until(ExpectedConditions.urlContains("/search"));

        // 3. Click the game in the results to reach the target Game Page
        WebElement gameLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='search_resultsRows']//span[text()='Portal 2']/ancestor::a[1]")));
        gameLink.click();
        wait.until(ExpectedConditions.urlContains("app/620")); // 620 is Portal 2's ID

        captureState("GamePage_Portal2_Loaded");
    }

    // Checks that we actually landed on the correct game URL
    @Test(priority = 1)
    public void testCorrectURL() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("app/620/Portal_2"), "The URL does not match the expected Portal 2 App ID path.");
    }

    // Verifies the large game title says "Portal 2"
    @Test(priority = 2)
    public void testCorrectTitleDisplayed() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("appHubAppName")));
        Assert.assertEquals(title.getText(), "Portal 2", "The game title displayed is incorrect.");
    }

    // Ensures the main game picture is loaded and visible
    @Test(priority = 3)
    public void testGameImageDisplayed() {
        WebElement mainImage = getDriver().findElement(By.className("game_header_image_full"));
        Assert.assertTrue(mainImage.isDisplayed(), "The main game header image is not visible.");

        // Check the HTML attribute to ensure the image link isn't broken
        String src = mainImage.getAttribute("src");
        Assert.assertTrue(src != null && src.startsWith("https"), "Game image source is broken or missing.");
    }

    // Checks if the user is able to buy the game
    @Test(priority = 4)
    public void testAddToCartButtonExists() {
        WebElement addToCartBtn = getDriver().findElement(By.xpath("//a[contains(@id, 'btn_add_to_cart')]"));

        Assert.assertTrue(addToCartBtn.isDisplayed(), "The 'Add to Cart' button was not found on the page.");
        Assert.assertTrue(addToCartBtn.getText().contains("Add to Cart"), "Button text is incorrect.");
    }

    // Tests the browser's Back and Forward buttons
    @Test(priority = 5)
    public void testNavigationHistory() {
        // Go backward in history (should take us to the Search page we visited in Setup)
        getDriver().navigate().back();
        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search"), "Back button did not return to Search Results.");
        captureState("GamePage_Navigated_Back");

        // Go forward in history (should bring us right back to the Game page)
        getDriver().navigate().forward();
        wait.until(ExpectedConditions.urlContains("app/620"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("app/620"), "Forward button did not return to Game Page.");
        captureState("GamePage_Navigated_Forward");
    }
}