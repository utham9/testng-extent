import org.apache.commons.io.FileUtils;
import org.listeners.ExtentITestListenerClassAdapter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.function.Function;

@Listeners({ExtentITestListenerClassAdapter.class})
public class StandaloneChrome {

  private WebDriver webDriver;
  private URL selenium_hub = new URL("http://localhost:4444/wd/hub");
  private FluentWait fluentWait;

  public StandaloneChrome() throws MalformedURLException {}

  @BeforeClass
  public void setup() {
    ChromeOptions chromeOptions = new ChromeOptions();
    // chromeOptions.setHeadless(true);
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.addArguments("--verbose");
    chromeOptions.addArguments("--disable-dev-shm-usage");
    DesiredCapabilities dc = new DesiredCapabilities();
    dc.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    webDriver = new RemoteWebDriver(selenium_hub, dc);
    fluentWait = new FluentWait(webDriver);
    fluentWait.withTimeout(Duration.ofMillis(5000));
    fluentWait.pollingEvery(Duration.ofMillis(250));
    fluentWait.ignoring(NoSuchElementException.class);
  }

  @Test
  public void sout() {
    webDriver.get("https://www.google.com/");
    WebElement search = getElement(By.xpath("//input[@title='Search']"));
    search.sendKeys("jarvis investment");
    search.sendKeys(Keys.ENTER);
    WebElement jarvisLink =
        getElement(By.xpath("//h3[normalize-space()='Jarvis Invest : Wisdom meets science']"));
    jarvisLink.click();
    WebElement jarvisLogo = getElement(By.xpath("//img[@alt='Jarvis Logo']"));
    takeSnapShot(webDriver, "jarvis.png");
    Assert.assertNotNull(jarvisLogo);
  }

  private WebElement getElement(By locator) {
    return (WebElement)
        fluentWait.until(
            new Function() {
              @Override
              public Object apply(Object o) {
                return webDriver.findElement(locator);
              }
            });
  }

  private void takeSnapShot(WebDriver webdriver, String fileWithPath) {
    TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
    File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
    File DestFile = new File(fileWithPath);
    try {
      FileUtils.copyFile(SrcFile, DestFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public void tearDown() {
    webDriver.close();
    webDriver.quit();
  }
}
