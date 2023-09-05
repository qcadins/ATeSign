import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.Keys as Keys

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

chromePrefs.put("profile.default_content_setting_values.media_stream_camera", 1)

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

WebUI.delay(GlobalVariable.TimeOut)

'maximized window'
WebUI.maximizeWindow()

'store user login'	
GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel(Email)).toUpperCase()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel(Email)))

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel(Password)))

'click button login'
WebUI.click(findTestObject('Login/button_Login'))

if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel(Perusahaan)))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, rowExcel(Peran)))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'focus untuk pilih peran'
	WebUI.focus(findTestObject('Login/button_pilihPeran'))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'))
}

'Jika error lognya muncul'
if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	'ambil teks errormessage'
	errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)
	
	'Tulis di excel itu adalah error'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
		GlobalVariable.StatusWarning, (findTestData(Path).getValue(GlobalVariable.NumofColm, 2).replace(
		'-', '') + ';') + '<' + errormessage + '>')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}