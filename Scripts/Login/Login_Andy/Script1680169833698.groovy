import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'open browser'
WebUI.openBrowser('')

'navigate to url esign'
WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 3))

'maximized window'
WebUI.maximizeWindow()

'input email'
WebUI.setText(findTestObject('Login/input_Email'), findTestData('Login/Login').getValue(2, 3))

'store user login id'
GlobalVariable.userLogin = findTestData('Login/Login').getValue(2, 3).toUpperCase()

'input password'
WebUI.setText(findTestObject('Login/input_Password'), findTestData('Login/Login').getValue(3, 3))

'click button login'
WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

'input perusahaan'
WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData('Login/Login').getValue(4, 3))

'enter untuk input perusahaan'
WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

'input peran'
WebUI.setText(findTestObject('Login/input_Peran'), findTestData('Login/Login').getValue(5, 3))

'enter untuk input peran'
WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

'click button pilih peran'
WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)

