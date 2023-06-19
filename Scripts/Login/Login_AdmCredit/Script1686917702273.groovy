import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 6))

'maximize window'
WebUI.maximizeWindow()

'set value userLogin'
GlobalVariable.userLogin = findTestData('Login/Login').getValue(2, 6).toUpperCase()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData('Login/Login').getValue(2, 6))

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData('Login/Login').getValue(3, 6))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

'click ddl bahasa'
WebUI.click(findTestObject('Login/button_bahasa'))

'click english'
WebUI.click(findTestObject('Login/button_English'))