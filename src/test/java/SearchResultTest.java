import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class SearchResultTest extends BaseTest {

    private WebDriverWait wait;

    // Setup runs before the tests to make sure we are on the Steam Storefront
    @BeforeClass
    public void setupSearch() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        // Check if the browser is blank or on the wrong site, if so, go to Steam
        if (getDriver().getCurrentUrl().equals("about:blank") || !getDriver().getCurrentUrl().contains("steampowered")) {
            getDriver().get("https://store.steampowered.com/");
        }
    }

    // Tests if typing a query updates the URL correctly
    @Test(priority = 1)
    public void testSearchUrlPattern() {
        // Find the search box, clear it, type "Portal 2", and hit enter
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='term']")));
        searchBox.clear();
        searchBox.sendKeys("Portal 2");
        searchBox.submit();

        // Wait for the URL to change and confirm it contains our search term
        wait.until(ExpectedConditions.urlContains("term="));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("term=Portal"), "URL didn't update to the search pattern.");
        captureState("SearchResult_URL_Verified");
    }

    // Checks that a highly popular game appears properly
    @Test(priority = 2)
    public void testPopularGameExists() {
        // Look at the very first result in the list
        WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='search_resultsRows']//a[1]//span[@class='title']")));

        // Verify that the top result is exactly what we searched for
        Assert.assertTrue(firstResult.getText().contains("Portal 2"), "Portal 2 was not the first result!");
    }

    // Checks if DLCs/Soundtracks are included in the search results
    @Test(priority = 3)
    public void testSoundtrackExists() {
        // Wait for the result list to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='title']")));

        // Grab all the titles from the search results and see if any of them contain "Soundtrack"
        List<WebElement> results = getDriver().findElements(By.xpath("//span[@class='title']"));
        boolean foundSoundtrack = results.stream().anyMatch(e -> e.getText().contains("Portal 2 Soundtrack"));

        Assert.assertTrue(foundSoundtrack, "Portal 2 Soundtrack should be visible in search results.");
    }

    // Tests searching for a smaller, lesser-known game
    @Test(priority = 4)
    public void testNicheGameSearch() {
        // Find search box, clear previous query, and search for a niche game
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='term']")));
        searchBox.clear();
        searchBox.sendKeys("Funi Raccoon Game");
        searchBox.submit();

        // Verify the game shows up in the results
        WebElement resultTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='search_resultsRows']//span[@class='title']")));

        Assert.assertTrue(resultTitle.getText().contains("Funi Raccoon"), "Niche game not found!");
        captureState("SearchResult_NicheGame");
    }

    // Tests how Steam handles a search for something that doesn't exist
    @Test(priority = 5)
    public void testNonExistentGame() {
        // Find search box, clear it, and type random keyboard mashing
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='term']")));
        searchBox.clear();
        searchBox.sendKeys("audfawyjdfawduk");
        searchBox.submit();

        // Check the text that says how many results were found
        WebElement searchSummary = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search_results_count")));

        // Verify it says "0 results" OR check if the list of links is completely empty
        boolean noResults = searchSummary.getText().contains("0") ||
                getDriver().findElements(By.cssSelector("#search_resultsRows a")).isEmpty();

        Assert.assertTrue(noResults, "Search should have returned 0 results for random characters.");
        captureState("SearchResult_EmptyResult");
    }
}