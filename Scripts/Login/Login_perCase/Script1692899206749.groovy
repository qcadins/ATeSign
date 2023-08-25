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

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

'maximized window'
WebUI.maximizeWindow()

if (TC == '3.1.0SendSign') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 94))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 94)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 95))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 96))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 97))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'ResponseAPISignDoc') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 32))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 32)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 33))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 34))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 35))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'AllSendthenSign') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 88))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 88)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 89))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 90))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 91))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'SendtoSign') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 88))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 88)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 89))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 90))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 91))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'ManualSigntoSign') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 65))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 65)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 66))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 67))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 68))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'ManualSignOnly') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 30))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 30)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 31))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 32))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 33))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'DocumentMonitoring') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 21))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 21)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 22))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 23))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 24))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'IsiSaldo') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 22))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 22)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 23))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 24))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 25))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
} else if (TC == 'ListUndangan') {
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(Path).getValue(GlobalVariable.NumofColm, 12))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(Path).getValue(GlobalVariable.NumofColm, 12)
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(Path).getValue(GlobalVariable.NumofColm, 13))
	
	'click button login'
	WebUI.click(findTestObject(Path), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(Path).getValue(GlobalVariable.NumofColm, 14))
	
	'enter untuk input perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(Path).getValue(GlobalVariable.NumofColm, 15))
	
	'enter untuk input peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
}

'Jika error lognya muncul'
if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && GlobalVariable.FlagFailed == 0) {
	'ambil teks errormessage'
	errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)
	
	'Tulis di excel itu adalah error'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
		GlobalVariable.StatusWarning, (findTestData(Path).getValue(GlobalVariable.NumofColm, 2).replace(
		'-', '') + ';') + '<' + errormessage + '>')
	
	GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && GlobalVariable.FlagFailed > 0) {
	'ambil teks errormessage'
	errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
	
	'write to excel reason warning'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, SheetName,
		1, GlobalVariable.NumofColm - 1, (findTestData(Path).getValue(GlobalVariable.NumofColm, 2).replace(
		'-', '') + ';') + '<' + errormessage + '>')
}