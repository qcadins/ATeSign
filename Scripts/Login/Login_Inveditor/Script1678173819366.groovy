import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 4))

'maximize window'
WebUI.maximizeWindow()

'set value userLogin'
GlobalVariable.userLogin = findTestData('Login/Login').getValue(2, 4).toUpperCase()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData('Login/Login').getValue(2, 4))

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData('Login/Login').getValue(3, 4))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData('Login/Login').getValue(4, 4))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData('Login/Login').getValue(5, 4))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
}