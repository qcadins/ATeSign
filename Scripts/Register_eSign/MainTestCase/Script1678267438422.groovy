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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'call test case login inveditor'
WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

'maximize window'
WebUI.maximizeWindow()

'get colm excel'
int countColmExcel = findTestData(excelPathBuatUndangan).getColumnNumbers()

'looping buat undangan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; GlobalVariable.NumofColm++) {
	if(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	}else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        WebUI.callTestCase(findTestCase('Register_eSign/BuatUndangan'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
            FailureHandling.CONTINUE_ON_FAILURE)
    }
}

