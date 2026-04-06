import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

/**
 * BaseTest.java
 * * This is the foundational parent class for the Steam E2E Automation Suite.
 * All workflow test classes (SearchTests, VerificationTests, CartWorkflowTests)
 * inherit from this class. It centrally manages the WebDriver lifecycle (setup/teardown)
 * and provides shared utility methods, ensuring adherence to the DRY (Don't Repeat Yourself) principle.
 */
public class BaseTest {

    // Protected variables allow child classes to access the driver and wait directly
    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Initializes the WebDriver and configures browser options before any tests run.
     */
    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();

        // EAGER strategy prevents Selenium from waiting for heavy background videos/assets to load,
        // significantly speeding up test execution and preventing timeouts.
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
        options.addArguments("--remote-allow-origins=*");

        // Forces Chrome to open natively maximized. This ensures the website renders in desktop mode,
        // preventing UI elements (like search bars) from being hidden inside mobile hamburger menus.
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);

        // Global explicit wait of 20 seconds to be used across all child test classes
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Closes the browser and safely terminates the WebDriver session after all tests in a class finish.
     */
    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ==========================================
    // --- WORKFLOW DIAGRAM UTILITY HELPERS ---
    // ==========================================

    /**
     * Fulfills the project workflow diagram requirement:
     * "Screenshot shall be taken after every Decision Node and when test entered a Page/State Node"
     * * Takes a picture of the current browser state and saves it to a designated folder.
     * * @param nodeName The specific name of the flowchart node (e.g., "State_SteamStorePage")
     */
    protected void captureNode(String nodeName) {
        try {
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File srcFile = scrShot.getScreenshotAs(OutputType.FILE);

            // Create the directory if it does not already exist
            File destDir = new File("./workflow_screenshots/");
            if (!destDir.exists()) destDir.mkdir();

            // Sanitize the node name to prevent file-system errors (removes invalid characters)
            String safeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "_");
            FileHandler.copy(srcFile, new File(destDir, safeName + ".png"));

        } catch (IOException e) {
            System.out.println("Failed to capture screenshot for node: " + nodeName);
        }
    }

    /**
     * Fulfills the project workflow diagram requirement:
     * "Save to Info.txt" and "Save to Reviews.txt"
     * * Appends extracted web data into local text files for verification.
     * * @param fileName The target text file (e.g., "Info.txt")
     * @param content  The scraped string data to write into the file
     */
    protected void saveTextToFile(String fileName, String content) {
        try {
            // Create the data extraction directory if it does not already exist
            File destDir = new File("./extracted_data/");
            if (!destDir.exists()) destDir.mkdir();

            // The 'true' parameter enables append mode, ensuring previous data isn't overwritten
            FileWriter writer = new FileWriter("./extracted_data/" + fileName, true);
            writer.write(content + "\n\n");
            writer.close();

        } catch (IOException e) {
            System.out.println("Failed to write extracted data to " + fileName);
        }
    }
}