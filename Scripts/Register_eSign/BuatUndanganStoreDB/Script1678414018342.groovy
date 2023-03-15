import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection

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

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get data buat undangan dari DB'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.BuatUndanganStoreDB'(conneSign, findTestData(excelPathBuatUndangan).getValue(
		GlobalVariable.NumofColm, 15).toUpperCase())

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'verify nama'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tempat lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 12).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify jenis kelamin'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify email'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kode pos'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 22).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))


'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';' + GlobalVariable.ReasonFailedStoredDB)
	
}