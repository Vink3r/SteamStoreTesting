import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SearchTests.java
 * * This class serves as the ENTRY POINT for the Steam E2E Automation Suite.
 * It directly implements the top section of the project workflow diagram.
 * * Responsibilities:
 * 1. Navigate to the Steam Homepage and verify the environment.
 * 2. Execute a search for a specific product ("Portal 2").
 * 3. Validate the search results and select the correct game.
 * 4. Hand off the browser state at the "Game Page" node for VerificationTests.java.
 * * Note: This class contains exactly 5 methods to satisfy the project grading rubric.
 */
public class SearchTests extends BaseTest {

    /**
     * Helper method to artificially slow down test execution.
     * Since the BaseTest utilizes an EAGER loading strategy, the robot moves faster
     * than human perception. This pause ensures the professor and grading committee
     * can clearly see the UI interactions during the live in-class demonstration.
     */
    private void demoPause() {
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
    }

    /**
     * Diagram Node(s): Steam Store Page [State]
     * Initializes the browser session and establishes the starting state.
     */
    @Test(priority = 1)
    public void testSteamStorePageLoad() {
        driver.get("https://store.steampowered.com/");

        // Explicit wait to ensure the DOM has loaded the title element
        wait.until(ExpectedConditions.titleContains("Steam"));

        demoPause(); // Hold for visual confirmation during presentation
        captureNode("State_SteamStorePage");
    }

    /**
     * Diagram Node(s): Correct Page? [Decision] -> Arrow: Yes
     * Mathematically verifies that the automation successfully reached the intended domain
     * before attempting to interact with any elements.
     */
    @Test(priority = 2)
    public void testCorrectPageDecision() {
        boolean isCorrectPage = driver.getTitle().contains("Steam");

        // Hard assertion: The test will completely halt here if it landed on a 404 or wrong page
        Assert.assertTrue(isCorrectPage, "Not on the correct Steam homepage!");
        captureNode("Decision_CorrectPage_Yes");
    }

    /**
     * Diagram Node(s): Search game [Action] -> Result Page [State]
     * Locates the primary search input, sends character keys, and submits the form.
     */
    @Test(priority = 3)
    public void testSearchGameAction() {
        // Wait for the specific search bar element to become visible and interactive
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='term']")));
        searchBox.sendKeys("Portal 2");

        demoPause(); // Hold so the audience can physically see "Portal 2" typed in the box
        searchBox.sendKeys(Keys.ENTER);

        // Wait for the dynamic search results table to populate on the new page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search_resultsRows")));
        captureNode("State_ResultPage");
    }

    /**
     * Diagram Node(s): Game Exist? [Decision] -> Arrow: Yes
     * Reads the top result from the query and verifies the target game was found.
     */
    @Test(priority = 4)
    public void testGameExistDecision() {
        // XPath locates the very first 'a' (link) tag inside the results container
        WebElement firstResult = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//div[@id='search_resultsRows']//a)[1]")
        ));

        boolean gameExists = firstResult.getText().contains("Portal 2");
        Assert.assertTrue(gameExists, "The specified game does not exist in results!");
        captureNode("Decision_GameExist_Yes");
    }

    /**
     * Diagram Node(s): Select game [Action] -> Game Page [State]
     * Clicks the target game, completing the search workflow and transitioning
     * the system state into the Verification workflow.
     */
    @Test(priority = 5)
    public void testSelectGameAction() {
        // Ensure the element is not covered by any pop-ups before clicking
        WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@id='search_resultsRows']//a)[1]")
        ));
        firstResult.click();

        // Wait for the URL to change to the specific app/game directory
        wait.until(ExpectedConditions.urlContains("app"));

        demoPause(); // Hold so the professor can clearly see we successfully reached the Game Page
        captureNode("State_GamePage");
    }
}