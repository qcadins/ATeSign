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

def currentDate = new Date().format( 'dd-MMM-yyyy' )
println "${currentDate}"

'click menu Inquiry Invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'input email'
WebUI.setText(findTestObject('InquiryInvitation/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
		15))

'Looping delay untuk handling copy app selama +- 2 menit'
for(int i = 1;i<=8;i++){
	'click button cari'
	WebUI.click(findTestObject('InquiryInvitation/button_Cari'))
	
	'Pengecekan ada/tidak adanya button action pencil yang muncul'
	if(WebUI.verifyElementPresent(findTestObject('InquiryInvitation/input_Email'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)){
		break
	}
	else{
		'delay 14 detik'
		WebUI.delay(10)
	}
}

'verify nama'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Name')).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify email'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Receiver')).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify phone'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Phone')).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        14).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify invitation date'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_InvitationDate')).toUpperCase(), currentDate.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

		GlobalVariable.FlagFailed = 1
	}
}