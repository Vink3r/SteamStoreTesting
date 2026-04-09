import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class CartPageTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeClass
    public void prepareCartState() {
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        //Ensure we are back on the Portal 2 page to add it to the cart
        if (parallel) {
            getDriver().get("https://store.steampowered.com/app/620/Portal_2/");
        }
        else {
            ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, 0);");
        }
    }

    @Test(priority = 1)
    public void testAddToCart() {
        //Add to Cart button clicked
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@id, 'btn_add_to_cart')]")));
        addToCartBtn.click();
        //Click on Go to my Cart
        getDriver().findElement(By.xpath("/html/body/div[3]/dialog/div/div[2]/div/div[3]/div/div[3]/button[2]")).click();
        //Game should be in Cart page now
        wait.until(ExpectedConditions.urlContains("/cart"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/cart"), "Did not navigate to the Cart page.");
        captureState("Cart_Page_Loaded");
    }

    @Test(priority = 2)
    public void testCorrectItemInCart() {
        //Portal 2 existed in the Cart
        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div/div/div[2]/div[3]/div[1]/div[1]/div/div/div/div[2]/div[1]/div")));

        String actualGame = cartItem.getText();

        Assert.assertEquals(actualGame, "Portal 2", "Portal 2 is not showing in the cart list.");
    }

    @Test(priority = 3)
    public void testContinueToPaymentShowsLogin() {
        //Continue to Payment will show the Login Page
        WebElement purchaseBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div/div/div[2]/div[3]/div[1]/div[3]/div[1]/button[2]")));
        purchaseBtn.click();

        //Verify the Login Pop-up appeared
        WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/dialog/div[2]/div/div/div[3]")));
        captureState("Cart_Login_Popup_Verified");

        //Close the popup using className "closeButton"
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("Layer_2")));
        closeBtn.click();

        //Verify the pop-up is gone so we can see the Cart again
        wait.until(ExpectedConditions.invisibilityOf(loginModal));
    }

    @Test(priority = 4)
    public void testRemoveGameFromCart() {
        WebElement removeLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div[7]/div[7]/div[3]/div/div/div[2]/div[3]/div[1]/div[1]/div/div/div/div[2]/div[4]/div[2]/div[2]")));
        removeLink.click();

        //Verify the cart list area is gone or shows empty
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//a[contains(text(), 'Portal 2')]")));
        captureState("Cart_Item_Removed");
    }

    @Test(priority = 5)
    public void testContinueShoppingReturnsHome() {
        //Continue Shopping button will bring us back to home Page
        //Steam's "Continue Shopping" button usually has a specific class or text link
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//html/body/div[1]/div[7]/div[7]/div[3]/div/div/div[2]/div[3]/div[1]/div[3]/div[1]/button[1]")));
        continueBtn.click();

        //Verify we are back on the main store page
        wait.until(ExpectedConditions.urlContains("store.steampowered.com"));
        //Check for Store page URL
        Assert.assertTrue(getDriver().getCurrentUrl().equals("https://store.steampowered.com/"), "We did not end up on the Store Page");
        captureState("Journey_Completed_At_Home");
    }
}