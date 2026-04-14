import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;
import java.io.File;

public class GameInfoTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void ensureStateForInfoScraping() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //File refresher (delete old file)
        File infoFile = new File("src/test/resources/output/Info.txt");
        if (infoFile.exists()) {
            if (infoFile.delete()) {
                System.out.println("Cleaned up old Info.txt before starting Scrape Phase.");
            }
        }

        //Continuity Check: Ensure we are on the Portal 2 page before scrolling
        if (parallel) {
            getDriver().get("https://store.steampowered.com/app/620/Portal_2/");
        }
        else {
            WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
            searchBox.clear();
            searchBox.sendKeys("Portal 2");
            searchBox.submit();
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='search_resultsRows']//a[1]//span[@class='title']")));
            firstResult.click();
        }
    }

    //Test Description area existed
    @Test(priority = 1)
    public void testDescriptionAreaExists() {
        WebElement description = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("game_area_description")));
        Assert.assertTrue(description.isDisplayed(), "Game description area is missing from the page.");
    }

    //Web scraping test
    @Test(priority = 2)
    public void testScrapeAboutGame() {
        //"About this Game" displayed -> Scrape Text
        WebElement aboutHeader = getDriver().findElement(By.xpath("//h2[text()='About This Game']"));

        //Scroll so the header is at the top of the view
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", aboutHeader);

        String descriptionText = getDriver().findElement(By.id("game_area_description")).getText();

        //Save to Info.txt using the helper in BaseTest
        saveToFile("Info.txt", "SECTION: GAME INFO\n" + descriptionText);

        Assert.assertTrue(descriptionText.length() > 0, "Failed to scrape description text.");
        captureState("Info_AboutGame_Scraped");
    }

    //Scrolling test for Requirement section
    @Test(priority = 3)
    public void testSystemRequirementsDisplayed() {
        WebElement sysReq = getDriver().findElement(By.className("sysreq_tabs"));

        //Scroll to view
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", sysReq);

        Assert.assertTrue(sysReq.isDisplayed(), "System Requirements section is not visible.");
    }

    //Test Minimum spec label displayed
    @Test(priority = 4)
    public void testMinimumSpecsDisplayed() {
        //We look for the bold 'Minimum:' label inside the requirements list
        WebElement minimumLabel = getDriver().findElement(By.className("game_area_sys_req_full"));

        Assert.assertTrue(minimumLabel.isDisplayed(), "Minimum specs label was not found.");

        //Scrape the actual requirements text to verify it's there
        String specs = minimumLabel.getText();
        saveToFile("Info.txt", "SECTION: MINIMUM SPECS\n" + specs);
    }

    //Test for Steam Deck verification badge
    @Test(priority = 5)
    public void testSteamDeckVerifiedStatus() {
        WebElement deckSection = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div[2]/div[1]/div[4]/div[1]/div[8]/div/div[2]/div[1]"));
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", deckSection);

        String status = deckSection.getText();
        //Verify it contains 'Verified' for Portal 2
        Assert.assertTrue(status.contains("Verified"), "Portal 2 should show as Steam Deck Verified.");

        captureState("Info_SteamDeck_Final");
    }
}