import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get current date'
def currentDate = new Date().format('dd-MMM-yyyy')

'click menu Inquiry Invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'input email'
WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        15))

'Looping delay untuk handling search data selama +- 2 menit'
for (int i = 1; i <= 8; i++) {
    'click button cari'
    WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

    'Pengecekan ada/tidak adanya data yang muncul'
    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
        break
    } else {
        'delay 10 detik'
        WebUI.delay(10)
    }
}

'verify nama'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Name')).toUpperCase(), findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify email'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Receiver')).toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify phone'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Phone')).toUpperCase(), findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify invitation date'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_InvitationDate')).toUpperCase(), 
        currentDate.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

