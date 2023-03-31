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

GlobalVariable.Response = '[00155D0B-7502-9C29-11ED-CF990D7DFD60]'

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

WebUI.callTestCase(findTestCase('Login/Login_Andy'), [:], FailureHandling.STOP_ON_FAILURE)

'get data API Send Document dari DB (hanya 1 signer)'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getKotakMasukSendDoc'(conneSign,GlobalVariable.Response.replace('[', '').replace(']', ''))

WebUI.maximizeWindow()

WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Lastest'))

WebUI.getText(findTestObject('Object Repository/KotakMasuk/text_refnum'))

if(WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/btn_userSigner') , GlobalVariable.TimeOut)) {
	
	'refnum'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/text_refnum')), result[arrayindex++], false))
	
	'doctype'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/text_tipedokumen')), result[arrayindex++], false))
	
	'document template name'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/KotakMasuk/text_namadokumentemplate')), result[arrayindex++], false))
	
	'nama Customer'
	arrayMatch.add(WebUI.getText(findTestObject('Object Repository/KotakMasuk/text_Berandaname')).contains(result[arrayindex++]))
	
	
	
}