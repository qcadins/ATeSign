import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable

import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import org.openqa.selenium.Keys as Keys

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

if (GlobalVariable.RunWith == 'Mobile') {
	
	'ambil koordinat dari settings'
	ArrayList coordinates = findTestData('Login/Setting').getValue(13, 2).split(',')
	
	'open browser'
	WebUI.openBrowser(findTestData('Login/Login').getValue(1, 2))
	
	'klik titik tiga'
	Mobile.tapAtPosition(1000, 180, FailureHandling.OPTIONAL)
	
	'aktifkan view desktop sites'
	Mobile.tapAtPosition(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), FailureHandling.OPTIONAL)
	
	'jika pakai localhost aktif'
	if(GlobalVariable.useLocalHost == 'Yes') {
		
		'delay'
		WebUI.delay(15)
		
		'arahkan ke localhost'
		WebUI.navigateToUrl(GlobalVariable.urlLocalHost + '/login')
	}
	
} else {
	'open browser'
	WebUI.openBrowser('')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'maximized window'
	WebUI.maximizeWindow()
}

'input email'
WebUI.setText(findTestObject('Login/input_Email'), email)

<<<<<<< HEAD
=======
'input password asumsi password = P@ssw0rd'
WebUI.setText(findTestObject('Login/input_Password'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer')))

>>>>>>> branch 'master' of https://github.com/qcadins/ATeSign
'input password asumsi password = P@ssw0rd'
WebUI.setText(findTestObject('Login/input_Password'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer')))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)


if (WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
<<<<<<< HEAD

	'input perusahaan'
WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan')))

=======
'input perusahaan'
WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan')))
	
>>>>>>> branch 'master' of https://github.com/qcadins/ATeSign
'enter untuk input perusahaan'
WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

'input peran'
WebUI.click(findTestObject('Login/input_Peran'))

WebUI.delay(1)

GlobalVariable.roleLogin = WebUI.getText(findTestObject('peranTerpilih'))

'enter untuk input peran'
WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

'click button pilih peran'
WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}