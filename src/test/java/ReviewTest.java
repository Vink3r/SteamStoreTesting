import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class ReviewTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void prepareReviewState() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //Ensure we are on the Portal 2 page to see the reviews
        if (!getDriver().getCurrentUrl().contains("app/620")) {
            getDriver().get("https://store.steampowered.com/app/620/Portal_2/");
        }
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, 0);");
    }

    @Test(priority = 1)
    public void testSentimentLabel() {
        //Steam uses 'game_review_summary' for the high-level sentiment
        WebElement sentiment = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 'game_review_summary')][contains(text(), 'Overwhelmingly Positive')]")));

        Assert.assertTrue(sentiment.isDisplayed(), "The sentiment label is not 'Overwhelmingly Positive'.");
        captureState("Review_Sentiment_Verified");
    }

    @Test(priority = 2)
    public void testJumpToReviews() {
        //Click the review summary to jump down the page
        WebElement reviewLink = getDriver().findElement(By.xpath("//a[contains(@href, '#app_reviews_hash')]"));
        reviewLink.click();

        //Check if the URL now includes the hash anchor
        Assert.assertTrue(getDriver().getCurrentUrl().contains("#app_reviews_hash"), "Page did not jump to the reviews section anchor.");
    }

    @Test(priority = 3)
    public void testReviewHashExists() {
        //Verify the "app_reviews_hash" ID exists in the DOM
        WebElement reviewSection = getDriver().findElement(By.id("app_reviews_hash"));
        Assert.assertTrue(reviewSection.isDisplayed(), "The reviews section anchor is missing.");
    }

    @Test(priority = 4)
    public void testNegativeFilterAndScreenshot() {
        //Apply Negative filter -> Verify "Not Recommended" exists

        //1. Locate the 'Negative' radio/filter button
        getDriver().findElement(By.className("user_reviews_filter_menu")).click();
        WebElement negativeFilter = wait.until(ExpectedConditions.elementToBeClickable(By.id("review_type_negative")));
        getDriver().findElement(By.className("user_reviews_filter_menu")).click();

        //Make sure the filter is in view before clicking
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block: 'center'});", negativeFilter);
        negativeFilter.click();

        //2. Wait for the review list to refresh with filtered results
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div[2]/div[1]/div[6]/div/div/div[7]")));

        //3. Verify that a "Not Recommended" review box is now visible
        WebElement notRecommendedLabel = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div[2]/div[1]/div[6]/div/div/div[7]/div/div[2]"));
        Assert.assertTrue(notRecommendedLabel.isDisplayed(), "Negative filter failed to display 'Not Recommended' reviews.");

        captureState("Review_Negative_Filter_Applied");
    }

    @Test(priority = 5)
    public void testRemoveFilter() {
        //Click "All" to reset filters
        getDriver().findElement(By.className("user_reviews_filter_menu")).click();
        WebElement allFilter = getDriver().findElement(By.id("review_type_all"));
        allFilter.click();

        //Wait for the 'Negative' selection to disappear from the active state
        wait.until(ExpectedConditions.elementToBeClickable(By.id("review_type_negative")));

        captureState("Review_Filters_Cleared");

        //Final verify: Ensure we are back to seeing all reviews
        String returnedFilterValue = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div[2]/div[1]/div[6]/div/div/div[7]/div/div[1]/div/div/span")).getText();
        Assert.assertTrue(returnedFilterValue.contains("Overwhelmingly Positive"), "Filters were not successfully reset.");
    }
}