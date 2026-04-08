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

    @BeforeClass
    public void setupSearch() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        //If parallel, jump to store; if serial, we should already be here
        if (getDriver().getCurrentUrl().equals("about:blank")) {
            getDriver().get("https://store.steampowered.com/");
        }

        //Tester, please remember to delete
        //wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        //getDriver().get("https://store.steampowered.com/");
    }

    @Test(priority = 1)
    public void testSearchUrlPattern() {
        WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
        searchBox.clear();
        searchBox.sendKeys("Portal 2");
        searchBox.submit();

        wait.until(ExpectedConditions.urlContains("/search?term="));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/search?term=Portal+2"), "URL didn't update to the search pattern.");
        captureState("SearchResult_URL_Verified");
    }

    @Test(priority = 2)
    public void testPopularGameExists() {
        //Find the title of the first result
        WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='search_resultsRows']//a[1]//span[@class='title']")));

        Assert.assertEquals(firstResult.getText(), "Portal 2", "Portal 2 was not the first result!");
    }

    @Test(priority = 3)
    public void testSoundtrackExists() {
        //Checking if the Soundtrack version appears in the list
        List<WebElement> results = getDriver().findElements(By.xpath("//span[@class='title']"));
        boolean foundSoundtrack = results.stream().anyMatch(e -> e.getText().contains("Portal 2 Soundtrack"));

        Assert.assertTrue(foundSoundtrack, "Portal 2 Soundtrack should be visible in search results.");
    }

    @Test(priority = 4)
    public void testNicheGameSearch() {
        WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
        searchBox.clear();
        searchBox.sendKeys("Funi Raccoon Game");
        searchBox.submit();

        WebElement resultTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='search_resultsRows']//span[@class='title']")));

        Assert.assertTrue(resultTitle.getText().contains("Funi Raccoon"), "Niche game not found!");
        captureState("SearchResult_NicheGame");
    }

    @Test(priority = 5)
    public void testNonExistentGame() {
        WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
        searchBox.clear();
        searchBox.sendKeys("audfawyjdfawduk");
        searchBox.submit();

        //Should show "0 results match your search"
        WebElement noResultsMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("search_results_count")));

        Assert.assertTrue(noResultsMsg.getText().contains("0 results"), "Search should have returned 0 results for random characters.");
        captureState("SearchResult_EmptyResult");
    }
}