import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import org.apache.commons.io.FileUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {
    static {Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);}
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String OUTPUT_PATH = "src/test/resources/output/";

    @BeforeTest
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver.set(new ChromeDriver(options));
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        //If running in Parallel, navigate to a starting URL immediately.
        //If Serial, the test will rely on the previous test's state or the first class's navigation.
        String runMode = System.getProperty("runMode");
        if ("classes".equalsIgnoreCase(runMode)) {
            handleParallelStartup();
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }

    //HELPER: File Saver/Scraper Helper
    public void saveToFile(String fileName, String content) {
        try {
            Files.createDirectories(Paths.get(OUTPUT_PATH));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + fileName, true))) {
                writer.write(content + "\n---\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //HELPER: Screenshot
    public void captureState(String fileName) {
        File src = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File("screenshots/" + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleParallelStartup() {
        getDriver().get("https://store.steampowered.com/");
    }

    @AfterTest
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}