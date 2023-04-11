import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.By
import java.sql.Connection as Connection

GlobalVariable.Response = '00155D0B-7502-9C6D-11ED-D4450E9C9C50'

'call Test Case untuk login sebagai user berdasarkan doc id'
WebUI.callTestCase(findTestCase('Login/Login_Signer'), [:], FailureHandling.STOP_ON_FAILURE)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'Klik btn Beranda'
WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_Beranda'))

'get row beranda'
variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

'modify object text document template name di beranda'
modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div/p",true)

'get text dari modify'
String documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/Sign/btn_TTDDokumen'), GlobalVariable.TimeOut)
	
WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_TTDDokumen'))
	
WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/Sign/lbl_TTDTandaTanganDokumenMassal'),GlobalVariable.TimeOut)
	
WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/Sign/btn_NamaDokumen')), documentTemplateName,false)

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_Batal') )

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_TTDDokumen'))

WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/Sign/lbl_TTDTandaTanganDokumenMassal'),GlobalVariable.TimeOut)

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_TTDSemua'))

WebUI.click(findTestObject('KotakMasuk/Sign/btn_TidakKonfirmasiTTD'))

WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/Sign/lbl_TTDTandaTanganDokumenMassal'),GlobalVariable.TimeOut)

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_TTDSemua'))

WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_konfirmasiTTD') , 0)

WebUI.click(findTestObject('KotakMasuk/Sign/btn_YaKonfirmasiTTD'))

WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen') , 0)

WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi')), documentTemplateName,false)
'check email'

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_Proses'))

WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), 0)

WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/Sign/input_EmailAfterKonfirmasi')), 'emailnya',false)
	
WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/Sign/input_phoneNoAfterKonfirmasi')), documentTemplateName, false)

WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'),'P@ssw0rd')

WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_EyePassword'))

WebUI.verifyMatch(WebUI.getText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi')),'P@ssw0rd', false)

WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifOTP'))

WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))

WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

