import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StorePageTest extends BaseTest {

    @BeforeClass
    public void navigateToStore() {
        //Starts at the home page
        getDriver().get("https://store.steampowered.com/");
        captureState("StorePage_Initial_Load");
    }

    @Test(priority = 1)
    public void testCorrectPageURL() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("steampowered.com"), "URL does not match Steam Store!");
    }

    @Test(priority = 2)
    public void testSteamLogoDisplayed() {
        WebElement logo = getDriver().findElement(By.xpath("//div[@class='logo']//img"));
        Assert.assertTrue(logo.isDisplayed(), "Steam Logo is not visible on the Store page.");
    }

    @Test(priority = 3)
    public void testAboutPageNavigation() {
        WebElement aboutLink = getDriver().findElement(By.linkText("ABOUT"));
        aboutLink.click();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/about"), "Did not navigate to About page.");
        captureState("Store_AboutPage");

        //Navigate back to Store
        getDriver().navigate().back();
    }

    @Test(priority = 4)
    public void testLanguageIsCorrect() {
        //Click the language dropdown
        WebElement langTrigger = getDriver().findElement(By.id("language_pulldown"));
        langTrigger.click();

        //Check the list of languages, if English is already active it won't appear
        String dropdownText = getDriver().findElement(By.id("language_dropdown")).getText();
        Assert.assertFalse(dropdownText.contains("English"), "English should be the active language and not an option to change to.");

        //Close dropdown
        langTrigger.click();
    }

    @Test(priority = 5)
    public void testSearchBoxExists() {
        WebElement searchBox = getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/form/div/input"));
        Assert.assertTrue(searchBox.isDisplayed(), "Search box is missing from the navigation bar.");
        Assert.assertTrue(searchBox.isEnabled(), "Search box is present but not interactable.");
    }
}