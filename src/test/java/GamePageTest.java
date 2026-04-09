import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class GamePageTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void navigateToPortal2() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //If parallel, recreate linear search test; if serial, replicate the process to get here from Search
        if (parallel) {
            getDriver().get("https://store.steampowered.com/search?term=Portal+2");
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='search_resultsRows']//a[1]//span[@class='title']")));
            firstResult.click();
        }
        else {
            WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
            searchBox.clear();
            searchBox.sendKeys("Portal 2");
            searchBox.submit();
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='search_resultsRows']//a[1]//span[@class='title']")));
            firstResult.click();
        }
        captureState("GamePage_Portal2_Loaded");
    }

    @Test(priority = 1)
    public void testCorrectURL() {
        //App 620 is the official Steam ID for Portal 2
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("app/620/Portal_2"), "The URL does not match the expected Portal 2 App ID path.");
    }

    @Test(priority = 2)
    public void testCorrectTitleDisplayed() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("appHubAppName")));
        Assert.assertEquals(title.getText(), "Portal 2", "The game title displayed is incorrect.");
    }

    @Test(priority = 3)
    public void testGameImageDisplayed() {
        //Steam uses 'game_header_image' for the main capsule image
        WebElement mainImage = getDriver().findElement(By.className("game_header_image_full"));
        Assert.assertTrue(mainImage.isDisplayed(), "The main game header image is not visible.");

        //Check if the image source is actually loaded
        String src = mainImage.getAttribute("src");
        Assert.assertTrue(src != null && src.startsWith("https"), "Game image source is broken or missing.");
    }

    @Test(priority = 4)
    public void testAddToCartButtonExists() {
        //Look for the 'Add to Cart' button specifically for the standard edition
        WebElement addToCartBtn = getDriver().findElement(By.xpath("//a[contains(@id, 'btn_add_to_cart')]"));

        Assert.assertTrue(addToCartBtn.isDisplayed(), "The 'Add to Cart' button was not found on the page.");
        Assert.assertTrue(addToCartBtn.getText().contains("Add to Cart"), "Button text is incorrect.");
    }

    @Test(priority = 5)
    public void testNavigationHistory() {
        getDriver().navigate().back();
        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search"), "Back button did not return to Search Results.");
        captureState("GamePage_Navigated_Back");

        getDriver().navigate().forward();
        wait.until(ExpectedConditions.urlContains("app/620"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("app/620"), "Forward button did not return to Game Page.");
        captureState("GamePage_Navigated_Forward");
    }

}