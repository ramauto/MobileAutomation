package pages;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class BiometricPage {
    private AppiumDriver driver;

    // Locators using @FindBy
    @FindBy(xpath = "//android.widget.TextView[@text='BIOMETRIC AUTHENTICATION']")
    private WebElement biometricAuthTitle;

    @FindBy(xpath = "//android.widget.TextView[@text='Biometric authentication']")
    private WebElement biometricAuthText;

    // Constructor
    public BiometricPage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // Actions
    public void clickBiometricAuth() {
        biometricAuthTitle.click();
    }
    public WebElement getBiometricAuthText() {
        return biometricAuthText;
    }


}

