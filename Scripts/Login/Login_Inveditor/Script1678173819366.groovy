import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.Keys

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

chromePrefs.put("profile.default_content_setting_values.media_stream_camera", 1)

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 4))

'maximize window'
WebUI.maximizeWindow()

'set value userLogin'
GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Login')).toUpperCase()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Login')))

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Password Login')))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Perusahaan Login')))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Peran Login')))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}