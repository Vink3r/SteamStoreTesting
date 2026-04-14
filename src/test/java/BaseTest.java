import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestContext;
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
    protected static boolean parallel = false;      //Bool value for parallel trigger

    //Setup Before Test
    @BeforeTest
    public void setUp(ITestContext context) {
        //Set system property
        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        //Disable version degrade warning
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.OFF);
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        String parallelMode = context.getSuite().getXmlSuite().getParallel().toString();
        if (!parallelMode.equalsIgnoreCase("none")) {       //Check parallel by string context
            parallel = true;
        }
        else       //Setup Linear driver for Linear
        {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--remote-allow-origins=*");
            driver.set(new ChromeDriver(options));
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        }
    }

    @BeforeClass
    public void setUpParallel() {
        //Setup individual Drivers for Parallel
        if (parallel) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--remote-allow-origins=*");
            driver.set(new ChromeDriver(options));
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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

    //Tear down and clean up
    @AfterClass
    public void tearDown() {
        if (parallel && getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
    @AfterTest
    public void tearDownLinear() {
        if (!parallel && getDriver() != null) {
            getDriver().quit();
        }
    }
}