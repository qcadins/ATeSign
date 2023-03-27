import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.Variable
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
'Split mengenai documentid'
ArrayList<String> documentIds = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(';', -1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()
'looping berdasarkan total document yang ada'
for (int i = 0; i < documentIds.size(); i++) 
{

	'get data API Bulk Sign Document dari DB'
	ArrayList<String> result = CustomKeywords.'connection.dataVerif.getbulkSign'(conneSign, documentIds[i], findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11))
	
	'declare arrayindex'
	arrayindex = 0

	'verify login id'
	arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify vendor code'
	arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Response,result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify total_signed'
	arrayMatch.add(WebUI.verifyMatch("0",result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}