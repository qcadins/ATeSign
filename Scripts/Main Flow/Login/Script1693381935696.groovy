import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'maximized window'
WebUI.maximizeWindow()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Username')))

'store GV user login'
GlobalVariable.userLogin = findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Username'))

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Password')))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

'input perusahaan'
WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan')))

'enter untuk input perusahaan'
WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

'input peran'
WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Role')))

'enter untuk input peran'
WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

'fokus pada tombol pilih peran'
WebUI.focus(findTestObject('Login/button_pilihPeran'))

'click button pilih peran'
WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)

'Jika error lognya muncul'
if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && GlobalVariable.FlagFailed == 0) {
	'ambil teks errormessage'
	errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)
	
	'Tulis di excel itu adalah error'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusWarning, (findTestData(excel).getValue(GlobalVariable.NumofColm, 2).replace(
		'-', '') + ';') + '<' + errormessage + '>')
	
	GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && GlobalVariable.FlagFailed > 0) {
	'ambil teks errormessage'
	errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
	
	'write to excel reason warning'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
		1, GlobalVariable.NumofColm - 1, (findTestData(excel).getValue(GlobalVariable.NumofColm, 2).replace(
		'-', '') + ';') + '<' + errormessage + '>')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}