package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BiometricPage;
import utils.Helper;

public class SampleTest extends BaseTest {
    Helper helper = new Helper();

    @Test
    public void launchAppTest() {
        test.info("Launching Biometric Page");  // Log in Extent Report

        BiometricPage biometricPage = new BiometricPage(driver);
        biometricPage.clickBiometricAuth();
        test.info("Clicked on Biometric Auth Button");

        helper.waitForElementToBeVisible(driver, biometricPage.getBiometricAuthText());
        test.info("Waiting for biometric text to appear");

        String actualText = biometricPage.getBiometricAuthText().getText();
        test.info("Captured text: " + actualText);

        try {
            Assert.assertEquals(actualText, "Biometric authentication", "❌ Text does not match!");
            test.pass("✅ Text matched successfully!");
        } catch (AssertionError e) {
            String screenshotPath = takeScreenshot("launchAppTest");  // Capture screenshot on failure
            test.fail("Test failed! ❌ Expected: 'Biometric authentication' but found: '" + actualText + "'")
                    .addScreenCaptureFromPath(screenshotPath);
            throw e;  // Rethrow the exception so TestNG marks it as failed
        }
    }
}
