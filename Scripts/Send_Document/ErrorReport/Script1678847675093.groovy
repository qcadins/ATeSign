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

'click menu ErrorReport'
WebUI.click(findTestObject('ErrorReport/menu_ErrorReport'))

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'call function check paging'
checkPaging()

String nomorKontrak = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"','')
ArrayList<String> namasigner = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(';', -1)

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'get total data error dari DB'
String resultTotalData = CustomKeywords.'connection.dataVerif.getTotalDataError'(conneSign, WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'))

'get error detail dari DB'
String resultErrorDetail = CustomKeywords.'connection.dataVerif.getErrorReportDetail'(conneSign, namasigner.last().toString().replace('"',''))

'get status activation'
ArrayList<String> resultStatusAktivasi = CustomKeywords.'connection.dataVerif.getStatusActivation'(conneSign)

'get total data'
totalData = WebUI.getText(findTestObject('ErrorReport/label_TotalData')).split(' ')

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(totalData[0], resultTotalData, false, FailureHandling.CONTINUE_ON_FAILURE))

'input nama'
WebUI.setText(findTestObject('ErrorReport/input_Nama'), namasigner.last().toString().replace('"',''))

'input no kontrak'
WebUI.setText(findTestObject('ErrorReport/input_NoKontrak'), nomorKontrak)

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

//'verify match tipe error'
//checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_Tipe')), GlobalVariable.ErrorType, false, FailureHandling.OPTIONAL))

'click button view'
WebUI.click(findTestObject('ErrorReport/button_View'))

'verify match tipe error'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_ErrorDetail')), resultErrorDetail, false, FailureHandling.OPTIONAL))

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

'click button set ulang'
WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))

'input nama'
WebUI.setText(findTestObject('ErrorReport/input_Nama'), namasigner.last().toString().replace('"',''))

'input no kontrak'
WebUI.setText(findTestObject('ErrorReport/input_NoKontrak'), nomorKontrak)

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'click button status aktivasi'
WebUI.click(findTestObject('ErrorReport/button_StatusAktivasi'))

index = 0

'verify data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerType')), 'Customer', false, FailureHandling.CONTINUE_ON_FAILURE))

'verify data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerName')), resultStatusAktivasi[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerNIK')), resultStatusAktivasi[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

'declare status'
String status, isregis = resultStatusAktivasi[index++], isaktif = resultStatusAktivasi[index++]

'cek if regis = 0 && aktif = 0 maka status belum registrasi'
if(isregis == '0' && isaktif == '0') {
	status = 'Belum Registrasi'
//cek if regis = 1 && aktif = 0 maka status belum aktivasi
}else if(isregis == '1' && isaktif == '0') {
	status = 'Belum Aktivasi'
//cek if regis = 1 && aktif = 1 maka status sudah aktivasi
}else if(isregis == '1' && isaktif == '1') {
	status = 'Sudah Aktivasi'
}

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_StatusAktivasi')), status, false, FailureHandling.CONTINUE_ON_FAILURE))

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

		GlobalVariable.FlagFailed = 1
	}
}

def checkPaging() {
	
	'click button cari'
	WebUI.click(findTestObject('ErrorReport/button_Cari'))
	
	'click button set ulang'
	WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))
	
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
	WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), '2012-12-12')
	
	'input Tanggal Ke'
	WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), '2020-12-12')
	
	'input Tanggal Ke'
	WebUI.setText(findTestObject('ErrorReport/select_Tipe'), 'REJECT')
	
	'send keys enter'
	WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))
	
	'click button cari'
	WebUI.click(findTestObject('ErrorReport/button_Cari'))
	
	'click button set ulang'
	WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))
	
	'verif select modul'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Modul'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif no kontrak'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_NoKontrak'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif nama konsumen'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Nama'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif lini bisnis'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_LiniBisnis'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif cabang'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Cabang'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif wilayah'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Wilayah'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif tanggal dari'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'), defaultTanggalDari, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif tanggal ke'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value'), defaultTanggalKe, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif select tipe'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Tipe'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click page 2'
	WebUI.click(findTestObject('ErrorReport/page_2'))
	
	'verify page 2 active'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click page 1'
	WebUI.click(findTestObject('ErrorReport/page_1'))
	
	'verify page 1 active'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click next page'
	WebUI.click(findTestObject('ErrorReport/button_NextPage'))
	
	'verify page 2 active'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click previous page'
	WebUI.click(findTestObject('ErrorReport/button_PreviousPage'))
	
	'verify page 1 active'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
}