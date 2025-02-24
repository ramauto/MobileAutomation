package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {
    protected static AppiumDriver driver;
    private static AppiumDriverLocalService service;
    protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void setupExtentReports() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("reports/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @BeforeClass
    public void setup() throws MalformedURLException {
        startAppiumServer();

        String apkPath = Paths.get(System.getProperty("user.dir"), "apk", "BitBarSampleApp.apk").toString();
        File app = new File(apkPath);
        if (!app.exists()) {
            throw new RuntimeException("❌ APK file missing at: " + apkPath);
        }

        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(ConfigReader.get("device"));
        options.setPlatformName(ConfigReader.get("platform"));
        options.setAutomationName("UiAutomator2");
        options.setApp(apkPath);
        options.setCapability("appWaitActivity", "*");
        options.setCapability("autoGrantPermissions", true);
        options.setCapability("noReset", true);
        options.setCapability("fullReset", false);

        String appiumUrl = ConfigReader.get("appiumURL");
        if (appiumUrl == null || appiumUrl.isEmpty()) {
            throw new RuntimeException("❌ Appium URL is not set in the configuration.");
        }

        System.out.println("🔍 Using Appium URL: " + appiumUrl);
        driver = new AndroidDriver(new URL(appiumUrl), options);
    }

    @BeforeMethod
    public void createTest(Method method) {
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void tearDownMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = takeScreenshot(result.getName());
            test.log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());
            test.addScreenCaptureFromPath(screenshotPath);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "✅ Test Passed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "⚠️ Test Skipped");
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
        stopAppiumServer();
    }

    @AfterSuite
    public void flushExtentReports() {

        extent.flush();
    }

    public static void startAppiumServer() {
        if (service != null && service.isRunning()) {
            System.out.println("ℹ️ Appium Server is already running!");
            return;
        }

        String appiumJSPath = "/opt/homebrew/lib/node_modules/appium/build/lib/main.js";
        if (!new File(appiumJSPath).exists()) {
            appiumJSPath = "/usr/local/lib/node_modules/appium/build/lib/main.js";
        }

        service = new AppiumServiceBuilder()
                .withAppiumJS(new File(appiumJSPath))
                .usingPort(4723)
                .withIPAddress("127.0.0.1")
                .build();

        service.start();

        if (service.isRunning()) {
            System.out.println("✅ Appium Server started on " + service.getUrl());
        } else {
            throw new RuntimeException("❌ Failed to start Appium Server.");
        }
    }

    public static void stopAppiumServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("🛑 Appium Server stopped...");
        }
    }

    public static String takeScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = "screenshots/" + testName + "_" + timestamp + ".png";
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File(screenshotPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return screenshotPath;
    }



}
