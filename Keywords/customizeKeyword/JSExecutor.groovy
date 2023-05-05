package customizeKeyword

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory

public class JSExecutor {

	//JS Executor Function
	@Keyword
	public jsExecutionFunction(String jsCode, String xpath, String newJS){
		'Mengambil web element dari kotaknya'
		WebElement element = DriverFactory.getWebDriver().findElement(By.xpath(xpath));

		'menentukan lokasi terbaru untuk object'
		String newLocation = newJS

		String js = jsCode

		DriverFactory.getWebDriver().executeScript(js, element, newLocation)
	}
}
