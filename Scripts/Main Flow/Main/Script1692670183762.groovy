import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection


'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

sheet = 'Main'

'looping untuk sending document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathMain).columnNumbers; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	} else if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 16) == 'API Send Document External') {
			WebUI.callTestCase(findTestCase('Main Flow/Response API Send Document'), [('excelPathAPISendDoc') : excelPathMain, ('sheet') : sheet, ('inputStartRow') : inputStartRow],
			FailureHandling.CONTINUE_ON_FAILURE)
			
		} else if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 16) == 'API Send Document Normal') {
			
		}
		WebUI.callTestCase(findTestCase('Main Flow/Login_Admin'), [('excel') : excelPathMain, ('sheet') : sheet],
			FailureHandling.CONTINUE_ON_FAILURE)
	}
}


