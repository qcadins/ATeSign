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

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'check if action new/services'
if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New') || findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit')) {
	
'get data balance mutation dari DB'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getTenantStoreDB'(conneSign, findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 14))

'get data services dari DB'
ArrayList<String> resultServices = CustomKeywords.'connection.dataVerif.getTenantServicesDescription'(conneSign, findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 12))

'declare arrayindex'
arrayindex = 0

'verify tenant name'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 12).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

if(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New')){
	
	'verify tenant code'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 13).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
}else {
	'skip'
	arrayindex++
}

'verify label ref number'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify API Key'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 16).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Email reminder'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

ArrayList<String> arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).split(';',-1)

ArrayList<String> arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).split(';',-1)

'looping untuk verif services dan bata saldo'
indexServices = 0 
for(indexExcel = 0 ; indexExcel < arrayServices.size(); indexExcel++) {
	String services = resultServices[indexServices++]
	
	if(services.equalsIgnoreCase(arrayServices[indexExcel])) {
		'verify services'
		arrayMatch.add(WebUI.verifyMatch(services, arrayServices[indexExcel].toUpperCase(),
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify service batas saldo'
		arrayMatch.add(WebUI.verifyMatch(resultServices[indexServices++], arrayServicesBatasSaldo[indexExcel],
				false, FailureHandling.CONTINUE_ON_FAILURE))
	}

}

}else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Service')) {
	'get data balacne mutation dari DB'
	String result = CustomKeywords.'connection.dataVerif.getTenantServices'(conneSign, findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 9)).replace('{','').replace('}','').replace('"','').replace(',','')
	
	'split result to array'
	ArrayList<String> resultarray = result.split(':0')
	
	'get array Services dari excel'
	ArrayList<String> arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 25).split(';', -1)
	
	'verify services'
	arrayMatch.add(arrayServices.containsAll(resultarray))
}


'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {

	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';' + GlobalVariable.ReasonFailedStoredDB)
	
}