import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'call login inveditor test case'
WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'click menu inquiry invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'call function check paging'
checkPaging()

Integer editAfterRegister = CustomKeywords.'connection.InquiryInvitation.getSetEditAfterRegister'(conneSign)

'check if search dengan email/phone/id no'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
    'set text search box dengan email'
    WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            15))
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
    'set text search box dengan Phone'
    WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            14))
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
    'set text search box dengan NIK'
    WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            9))
}

'click button cari'
WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

'cek if button edit aktif'
if (editAfterRegister == 1) {
    'click button Edit'
    WebUI.click(findTestObject('InquiryInvitation/button_Edit'))

    'verify invitationby'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/select_InviteBy'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify receiver'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Receiver'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify NIK'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_NIK'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Nama'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Nama'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify TempatLahir'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify TanggalLahir'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify No Handphone'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_NoHandphone'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Email'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Email'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Alamat'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Provinsi'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kota'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Kota'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kecamatan'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kelurahan'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'verify KodePos'
    checkVerifyEditableOrUneditable(WebUI.verifyEqual(WebUI.verifyElementHasAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), true, FailureHandling.CONTINUE_ON_FAILURE))

    'click button Simpan'
    WebUI.click(findTestObject('InquiryInvitation/button_Simpan'))

    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_OkSuccess'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
    (ReasonFailed == 'Link undangan sudah tidak aktif.')) {
        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        'click button OK'
        WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

        'click button batal'
        WebUI.click(findTestObject('InquiryInvitation/button_Batal'))
    }
}

'close browser'
WebUI.closeBrowser()

def checkPaging() {
    if (GlobalVariable.checkPaging == 'Yes') {
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
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
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

def checkVerifyEditableOrUneditable(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedEditable)

        GlobalVariable.FlagFailed = 1
    }
}

