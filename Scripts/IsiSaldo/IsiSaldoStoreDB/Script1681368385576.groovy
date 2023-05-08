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
import java.sql.Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data balance mutation dari DB'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getIsiSaldoStoreDB'(conneSign, findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18))

'get data tenant yang tidak topup dari DB'
String resultTenantTidakTopup = CustomKeywords.'connection.dataVerif.getTenantTidakIsiSaldo'(conneSign, findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 14), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18))

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'verify tenant'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify vendor'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 16).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tambah saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nomor tagihan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Catatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal pembelian'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant tidak ada'
arrayMatch.add(WebUI.verifyMatch(resultTenantTidakTopup.toString(), 'null', false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('isiSaldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 2) + ';' + GlobalVariable.ReasonFailedStoredDB)
	
}