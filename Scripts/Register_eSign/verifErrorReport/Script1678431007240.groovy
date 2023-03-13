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

'call function check paging'
checkPaging()

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

def checkPaging() {
	
	'get defaultTanggalDari'
	String defaultTanggalDari = WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value')
	
	'get defaultTanggalKe'
	String defaultTanggalKe = WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value')
	
	'select modul'
	WebUI.setText(findTestObject('ErrorReport/select_Modul'), 'generate invitation link error history')
	
	'send keys enter'
	WebUI.sendKeys(findTestObject('ErrorReport/select_Modul'), Keys.chord(Keys.ENTER))
	
	'input no kontrak'
	WebUI.setText(findTestObject('ErrorReport/input_NoKontrak'), '0000')
	
	'input nama konsumen'
	WebUI.setText(findTestObject('ErrorReport/input_Nama'), 'abcd')
	
	'input lini bisnis'
	WebUI.setText(findTestObject('ErrorReport/input_LiniBisnis'), 'abcd')
	
	'input cabang'
	WebUI.setText(findTestObject('ErrorReport/input_Cabang'), 'abcd')
	
	'input wilayah'
	WebUI.setText(findTestObject('ErrorReport/input_Wilayah'), 'abcd')
	
	'input Tanggal dari'
	WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), '12/12/2012')
	
	'input Tanggal Ke'
	WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), '12/12/2020')
	
	'input Tanggal Ke'
	WebUI.setText(findTestObject('ErrorReport/select_Tipe'), 'REJECT')
	
	'send keys enter'
	WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))
	
	'click button set ulang'
	WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))
	
	'verif select modul'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Modul'), 'value'), '', false)
	
	'verif no kontrak'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_NoKontrak'), 'value'), '', false)
	
	'verif nama konsumen'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Nama'), 'value'), '', false)
	
	'verif lini bisnis'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_LiniBisnis'), 'value'), '', false)
	
	'verif cabang'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Cabang'), 'value'), '', false)
	
	'verif wilayah'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Wilayah'), 'value'), '', false)
	
	'verif tanggal dari'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'), defaultTanggalDari, false)
	
	'verif tanggal ke'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value'), defaultTanggalKe, false)
	
	'verif select tipe'
	WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Tipe'), 'value'), '', false)
}