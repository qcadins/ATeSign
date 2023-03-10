import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.support.ui.Select as Select

'click menu inwuiry invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'call function check paging'
checkPaging()

if(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {	
	'set text search box dengan email'
	WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
	        15))
}else if(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
	'set text search box dengan Phone'
	WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			14))
}else ifif(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
	'set text search box dengan NIK'
	WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			9))
}

'click button cari'
WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

'get label invited by'
invitedBy = WebUI.getText(findTestObject('Object Repository/InquiryInvitation/label_InviteBy'))

'click button Resend'
WebUI.click(findTestObject('InquiryInvitation/button_Resend'))

if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'click button OK'
    WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))
	
	if(WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'get reason'
		ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
	
		if(!(ReasonFailed.contains('Undangan terkirim ke'))) {
			
			'write to excel status failed dan ReasonFailed'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)
			
			if(invitedBy.equalsIgnoreCase('SMS')) {
				'connect DB eSign UAT'
				Connection conneSignUAT = CustomKeywords.'connection.connectDB.connectDBeSignUAT'()
				
				'get data saldo'
				String result = CustomKeywords.'connection.dataVerif.getSaldo'(conneSignUAT, GlobalVariable.userLogin)
				
				'verify saldo'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
			}
			
		}else {
			
			'write to excel success'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
			
			if(invitedBy.equalsIgnoreCase('SMS')) {
				'connect DB eSign UAT'
				Connection conneSignUAT = CustomKeywords.'connection.connectDB.connectDBeSignUAT'()
				
				'get data saldo'
				String result = CustomKeywords.'connection.dataVerif.getSaldo'(conneSignUAT, GlobalVariable.userLogin)
				
				'verify saldo'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))
			}
		}
	}	
} else if(WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)){
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)
}


def checkPaging() {
    if (GlobalVariable.checkPaging == 'Yes') {
        'set text'
        WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), '2733849283748273')

        'click button cari'
        WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

        'verify table muncul'
        WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)

        'click button Reset'
        WebUI.click(findTestObject('InquiryInvitation/button_Reset'))

        'verify search box reset'
        WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/input_SearchBox'), 'value'), '', false)

        'click button ViewLink'
        WebUI.click(findTestObject('InquiryInvitation/button_ViewLink'))

        'get link'
        link = WebUI.getAttribute(findTestObject('InquiryInvitation/input_Link'), 'value', FailureHandling.OPTIONAL)

        'verify link bukan undefined atau kosong'
        if (link.equalsIgnoreCase('Undefined') || link.equalsIgnoreCase('')) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

            GlobalVariable.FlagFailed = 1
        }
        
        'click button TutupDapatLink'
        WebUI.click(findTestObject('InquiryInvitation/button_TutupDapatLink'))

        'click button Edit'
        WebUI.click(findTestObject('InquiryInvitation/button_Edit'))

        'click button Batal'
        WebUI.click(findTestObject('InquiryInvitation/button_Batal'))
    }
}


def checkVerifyEqualOrMatch(Boolean isMatch) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

		GlobalVariable.FlagFailed = 1
	}
}