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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'click menu PencarianPengguna'
WebUI.click(findTestObject('PencarianPengguna/menu_PencarianPengguna'))

'call function check paging'
checkPaging()

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
	'set text search box dengan email'
	WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
		15))
}else if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
	'set text search box dengan Phone'
	WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,
			14))
}else if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
	'set text search box dengan NIK'
	WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,
		9))
}

'click button cari'
WebUI.click(findTestObject('PencarianPengguna/button_Cari'))

if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Edit')) {
	
	'click button edit'
	WebUI.click(findTestObject('PencarianPengguna/button_Edit'))
		
	'get data PencarianPengguna dari DB'
	ArrayList<String> result = CustomKeywords.'connection.dataVerif.getDataPencarianPengguna'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
			GlobalVariable.NumofColm, 15).toUpperCase())
	
	'declare arraylist arraymatch'
	ArrayList<String> arrayMatch = new ArrayList<String>()
	
	'declare arrayindex'
	arrayindex = 0
	
	'verify nama'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_NamaLengkap'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tempat lahir'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TempatLahir'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tanggal lahir'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TanggalLahir'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify email'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Email'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify provinsi'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Provinsi'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kota'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kota'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kecamatan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kecamatan'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kelurahan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kelurahan'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kode pos'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_KodePos'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'edit phone number'
	WebUI.setText(findTestObject('PencarianPengguna/input_noHandphone'), findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,
			14))
	
	'click button simpan'
	WebUI.click(findTestObject('PencarianPengguna/button_Simpan'))
	
	'check if no error log atau tidak ada perubahan data '
	if(WebUI.verifyElementNotPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) ||
			WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL).equalsIgnoreCase('Tidak ada perubahan data')) {
		
		if(WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'click button OK'
			WebUI.click(findTestObject('PencarianPengguna/button_OK'))
		}
		
		'write to excel success'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
	}else if(WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) &&
			WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)){
		
		'click button OK'
		WebUI.click(findTestObject('PencarianPengguna/button_OK'))
		
		'get alert'
		AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
		
		'write to excel status failed dan reason'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedPaging)
		
		'click button kembali'
		WebUI.click(findTestObject('PencarianPengguna/button_Kembali'))
	}

}else if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Reset OTP')) {
	'click button reset OTP'
	WebUI.click(findTestObject('PencarianPengguna/button_ResetOTP'))
	
	'click button Ya Kirim OTP'
	WebUI.click(findTestObject('PencarianPengguna/button_YaKirimOTP'))
	
	if(WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'click button OK'
		WebUI.click(findTestObject('PencarianPengguna/button_OK'))
		
		'write to excel success'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
		
		'get data reset request OTP dari DB'
		String resultResetOTP = CustomKeywords.'connection.dataVerif.getResetOTP'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
				GlobalVariable.NumofColm, 15).toUpperCase())
		
		'verify OTP reset menjadi 0'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
	}else {
		'write to excel status failed dan reason'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend + ' OTP')
	}
}else if(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Resend Link')) {
	'click button resend link'
	WebUI.click(findTestObject('PencarianPengguna/button_ResendLinkaktivasi'))
	
	if(WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'get alert'
		AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
		
		if(AlertMsg.contains('berhasil')) {
			'write to excel success'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 0, GlobalVariable.NumofColm -
					1, GlobalVariable.StatusSuccess)
		}else {
			'write to excel status failed dan reason'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend + ' Link')
		}
	}else {
		'write to excel status failed dan reason'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend + ' Link')
	}
}


def checkPaging() {
	'input search box'
	WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 15))
	
	'click button cari'
	WebUI.click(findTestObject('PencarianPengguna/button_Cari'))
	
	'click button reset'
	WebUI.click(findTestObject('PencarianPengguna/button_Reset'))
	
	'verify search box reset'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_SearchBox'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
	    '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click button edit'
	WebUI.click(findTestObject('PencarianPengguna/button_Edit'))
	
	if(WebUI.verifyElementPresent(findTestObject('PencarianPengguna/button_Kembali'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'click button kembali'
		WebUI.click(findTestObject('PencarianPengguna/button_Kembali'))
	}else {
		'write to excel status failed dan reason'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedPaging)
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch)

		GlobalVariable.FlagFailed = 1
	}
}