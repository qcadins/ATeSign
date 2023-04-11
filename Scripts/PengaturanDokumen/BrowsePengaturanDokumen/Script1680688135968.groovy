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
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'memanggil test case login untuk admin wom dengan Admin Legal'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

'Klik tombol pengaturan dokumen'
WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlFilter'))

WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlFilter'))

WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/lbl_Status'), GlobalVariable.TimeOut)

WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/lbl_NamaTemplatDokumen'), GlobalVariable.TimeOut)

WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/lbl_KodeTemplatDokumen'), GlobalVariable.TimeOut)

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 'Active')

WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), 'e')

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 'f')

WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_Set Ulang'))

WebUI.click(findTestObject('TandaTanganDokumen/btn_ddlaktif'))

WebUI.verifyMatch(findTestObject('Object Repository/TandaTanganDokumen/input_Status'),WebUI.getText(findTestObject('TandaTanganDokumen/check_ddl')), false)

WebUI.verifyMatch(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'),'', false)

WebUI.verifyMatch(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'),'', false)

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 'Active')

WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), 'b')

WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 'c')

// 0 itu excel2an semua ya ges ya


