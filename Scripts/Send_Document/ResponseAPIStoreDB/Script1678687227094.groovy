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
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getSendDoc'(conneSign, GlobalVariable.Response.replace('[', 
        '').replace(']', ''))

'declare arrayindex'
arrayindex = 0

'verify email'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify signerType'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify ref_number'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify document_id'
arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Response.replace('[', '').replace(']', ''), result[arrayindex++], false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify document template code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify office code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify office name'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify region code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify region name'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify business line code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify business line name'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify is sequence'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify psre/vendor code'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify success url'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify upload url'
arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

//'verify sign Action : hardcode. Yang penting tidak boleh kosong'
//arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).replace('"', ''),
//	"", true, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

