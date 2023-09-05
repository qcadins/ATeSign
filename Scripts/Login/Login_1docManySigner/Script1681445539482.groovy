import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import org.openqa.selenium.Keys as Keys

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

if (isLocalhost == 0) {
	
	'open browser'
	WebUI.openBrowser('')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'maximized window'
	WebUI.maximizeWindow()
} else {
	
	'bukan dan arahkan ke gdk esign'
	WebUI.openBrowser(findTestData('Login/Login').getValue(1, 2))
	
	WebUI.delay(20)
	
	WebUI.navigateToUrl(GlobalVariable.urlLocalHost + '/login')
}

'input email'
WebUI.setText(findTestObject('Login/input_Email'), email)

'input password asumsi password = P@ssw0rd'
WebUI.setText(findTestObject('Login/input_Password'), 'P@ssw0rd')

'enter untuk input perusahaan'
WebUI.sendKeys(findTestObject('Login/input_Password'), Keys.chord(Keys.ENTER))

//'click button login'
//WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

if (WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
'input perusahaan'
WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData('Login/Login').getValue(4, 2))

'enter untuk input perusahaan'
WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

'input peran'
WebUI.setText(findTestObject('Login/input_Peran'),'Customer')

'enter untuk input peran'
WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

'click button pilih peran'
WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
}
