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

'click menu ErrorReport'
WebUI.click(findTestObject('ErrorReport/menu_ErrorReport'))

'input nama'
WebUI.setText(findTestObject('ErrorReport/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        10))

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'get total data'
String total = WebUI.getText(findTestObject('ErrorReport/label_Total'))

'verify match tipe error'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/ErrorReport/label_Tipe')), GlobalVariable.ErrorType, false, FailureHandling.OPTIONAL))

'verify total >= 1'
checkVerifyEqualOrMatch(WebUI.verifyGreaterThanOrEqual(Integer.parseInt(total[0]), 1, FailureHandling.OPTIONAL))

def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

		GlobalVariable.FlagFailed = 1
	}
}