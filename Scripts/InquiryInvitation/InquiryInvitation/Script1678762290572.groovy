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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'click menu inquiry invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'call function check paging'
checkPaging()

int EditAfterRegister = CustomKeywords.'connection.DataVerif.getSetEditAfterRegister'(conneSign)

int ResendLink = CustomKeywords.'connection.DataVerif.getSetResendLink'(conneSign)

int InvLink = CustomKeywords.'connection.DataVerif.getSetInvLinkAct'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            15).toUpperCase())

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

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Edit') && EditAfterRegister == 1) {
    'click button Edit'
    WebUI.click(findTestObject('InquiryInvitation/button_Edit'))

    'get data buat undangan dari DB'
    ArrayList<String> result = CustomKeywords.'connection.DataVerif.InquiryInvitationViewDataVerif'(conneSign, findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase())

    '1 karena invited by belum bisa di get value dari UI'
    arrayindex = 1

    //'verify invitationby'
    //checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/select_InviteBy')).toUpperCase(), result[arrayindex++].toUpperCase(), false))
	
    'verify receiver'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Receiver'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify NIK'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NIK'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Nama'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Nama'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify TempatLahir'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify TanggalLahir'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'verify No Handphone'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NoHandphone'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Email'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Alamat'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Provinsi'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kota'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kota'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kecamatan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Kelurahan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify KodePos'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 59).length() > 0) {
        'input invited by'
        WebUI.setText(findTestObject('InquiryInvitation/select_InviteBy'), findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, 59))

        'send keys enter'
        WebUI.sendKeys(findTestObject('InquiryInvitation/select_InviteBy'), Keys.chord(Keys.ENTER))

        'input receiver detail'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Receiver'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                60))
    }
    
    'input NIK'
    WebUI.setText(findTestObject('InquiryInvitation/edit_NIK'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            9))

    'input nama lengkap'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            10))

    'input tempat lahir'
    WebUI.setText(findTestObject('InquiryInvitation/edit_TempatLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            11))

    'input tanggal lahir'
    WebUI.setText(findTestObject('InquiryInvitation/edit_TanggalLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            12))

    'cek if pria(M) / wanita(F)'
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('M')) {
        'click radio pria'
        WebUI.click(findTestObject('InquiryInvitation/radio_Male'))
    } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('F')) {
        'click radio wanita'
        WebUI.click(findTestObject('InquiryInvitation/radio_Female'))
    }
    
    if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_noHandphone'), 'disabled', FailureHandling.OPTIONAL) != 
    'true') {
        'input no handphone'
        WebUI.setText(findTestObject('InquiryInvitation/edit_noHandphone'), findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, 14))
    }
    
    if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'disabled', FailureHandling.OPTIONAL) != 'true') {
        'input email'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                15))
    }
    
    'input alamat lengkap'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Alamat'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            17))

    'input provinsi'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Provinsi'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            18))

    'input kota'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kota'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            19))

    'input kecamatan'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kecamatan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            20))

    'input kelurahan'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kelurahan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            21))

    'input kode pos'
    WebUI.setText(findTestObject('InquiryInvitation/edit_KodePos'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            22))

    'click button Simpan'
    WebUI.click(findTestObject('InquiryInvitation/button_Simpan'))

    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_OkSuccess'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
    WebUI.verifyElementNotPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

        'write to excel success'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call test store db inquiry invitation'
            WebUI.callTestCase(findTestCase('InquiryInvitation/InquiryInvitationStoreDB'), [:], FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
            'set text search box dengan email'
            WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(
                    GlobalVariable.NumofColm, 15))
        } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
            'set text search box dengan Phone'
            WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(
                    GlobalVariable.NumofColm, 14))
        } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
            'set text search box dengan NIK'
            WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(
                    GlobalVariable.NumofColm, 9))
        }
        
        'click button cari'
        WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

        'verify data yang sudah di edit dapat di search di inquiry invitation'
        WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
    } else {
        'get reason'
        ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + ReasonFailed)

        'click button OK'
        WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

        'click button batal'
        WebUI.click(findTestObject('InquiryInvitation/button_Batal'))
    }
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Kirim Ulang Undangan') && InvLink == 1) {
    'get label invited by'
    invitedBy = WebUI.getText(findTestObject('Object Repository/InquiryInvitation/label_InviteBy'))

    'click button Resend'
    WebUI.click(findTestObject('InquiryInvitation/button_KirimUlangUndangan'))

    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason'
            ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            if (!(ReasonFailed.contains('Undangan terkirim ke'))) {
                'write to excel status failed dan ReasonFailed'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + ReasonFailed)

                if (invitedBy.equalsIgnoreCase('SMS')) {

                    'get data saldo'
                    String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

                    'verify saldo'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            } else {
                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, 
                    GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                if (invitedBy.equalsIgnoreCase('SMS')) {

                    'get data saldo'
                    String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

                    'verify saldo'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            }
        }
    } else if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get reason'
        ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + ReasonFailed)
    }
}else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Kirim Ulang Aktivasi') && ResendLink == 1) {
    'get label invited by'
    invitedBy = WebUI.getText(findTestObject('Object Repository/InquiryInvitation/label_InviteBy'))

    'click button Resend'
    WebUI.click(findTestObject('InquiryInvitation/button_KirimUlangLinkAktivasi'))

    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason'
            ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            if (!(ReasonFailed.contains('Berhasil'))) {
                'write to excel status failed dan ReasonFailed'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + ReasonFailed)

                if (invitedBy.equalsIgnoreCase('SMS')) {

                    'get data saldo'
                    String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

                    'verify saldo'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            } else {
                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, 
                    GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                if (invitedBy.equalsIgnoreCase('SMS')) {

                    'get data saldo'
                    String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

                    'verify saldo'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            }
        }
    } else if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get reason'
        ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + ReasonFailed)
    }
}


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
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
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
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

