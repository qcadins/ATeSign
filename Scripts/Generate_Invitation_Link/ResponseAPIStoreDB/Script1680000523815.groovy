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

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'get data API Send Document dari DB (hanya 1 signer)'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getGenInvLink'(conneSign,  findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,11).replace('"', '') , findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,15).replace('"', ''), findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', ''), findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

'declare arrayindex'
arrayindex = 0

'verify user crt / callerId'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify gender'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify zip code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 24).replace('"', ''),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tgl Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', ''),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tmp Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,17).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify email'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify id no / ktp'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', ''),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify phone'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', ''),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify address'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify full name'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', ''),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', '').toUpperCase(),
		result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}