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
}else if(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
	'set text search box dengan NIK'
	WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			9))
}

'click button cari'
WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

'click button Edit'
WebUI.click(findTestObject('InquiryInvitation/button_Edit'))

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get data buat undangan dari DB'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.InquiryInvitationViewDataVerif'(conneSign, findTestData(
        excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase())

'1 karena invited by belum bisa di get value dari UI'
arrayindex = 1

//'verify invitationby'
//checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/select_InviteBy')).toUpperCase(), result[arrayindex++].toUpperCase(), false))
'verify receiver'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Receiver'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify NIK'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NIK'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nama'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Nama'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify TempatLahir'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify TanggalLahir'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify No Handphone'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NoHandphone'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Email'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Alamat'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Provinsi'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Kota'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kota'), 'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Kecamatan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Kelurahan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify KodePos'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

if (WebUI.getAttribute(findTestObject('InquiryInvitation/select_InviteBy'), 'disabled') != true) {
    'input invited by'
    WebUI.setText(findTestObject('InquiryInvitation/select_InviteBy'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            32))

	'send keys enter'
    WebUI.sendKeys(findTestObject('InquiryInvitation/select_InviteBy'), Keys.chord(Keys.ENTER))
	
	'input receiver detail'
	WebUI.setText(findTestObject('InquiryInvitation/edit_Receiver'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			33))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NIK'), 'disabled') != 'true') {
    'input NIK'
    WebUI.setText(findTestObject('InquiryInvitation/edit_NIK'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            9))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Nama'), 'disabled') != 'true') {
    'input nama lengkap'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            10))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 'disabled') != 'true') {
    'input tempat lahir'
    WebUI.setText(findTestObject('InquiryInvitation/edit_TempatLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            11))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 'disabled') != 'true') {
    'input tanggal lahir'
    WebUI.setText(findTestObject('InquiryInvitation/edit_TanggalLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            12))
}

//'cek if pria(M) / wanita(F)'
//if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('M')) {
//	'click radio pria'
//	WebUI.click(findTestObject('BuatUndangan/radio_Pria'))
//} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('F')) {
//	'click radio wanita'
//	WebUI.click(findTestObject('BuatUndangan/radio_Wanita'))
//}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_noHandphone'), 'disabled') != 'true') {
    'input no handphone'
    WebUI.setText(findTestObject('InquiryInvitation/edit_noHandphone'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            14))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'disabled') != 'true') {
    'input email'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            15))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 'disabled') != 'true') {
    'input alamat lengkap'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Alamat'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            17))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 'disabled') != 'true') {
    'input provinsi'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Provinsi'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            18))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kota'), 'disabled') != 'true') {
    'input kota'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kota'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            19))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 'disabled') != 'true') {
    'input kecamatan'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kecamatan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            20))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 'disabled') != 'true') {
    'input kelurahan'
    WebUI.setText(findTestObject('InquiryInvitation/edit_Kelurahan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            21))
}

if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 'disabled') != 'true') {
    'input kode pos'
    WebUI.setText(findTestObject('InquiryInvitation/edit_KodePos'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            22))
}

'call function get data'
getData()

'click button Simpan'
WebUI.click(findTestObject('InquiryInvitation/button_Simpan'))

if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_OkSuccess'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
WebUI.verifyElementNotPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'click button OK'
    WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

    'write to excel success'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm - 
        1, GlobalVariable.StatusSuccess)

    if (GlobalVariable.checkStoreDB == 'Yes') {
        'call test store db inquiry invitation'
        WebUI.callTestCase(findTestCase('Register_eSign/InquiryInvitationStoreDB'), [:], FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    'set text search box dengan NIK'
    WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), GlobalVariable.eSignData[2])

    'click button cari'
    WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

    'verify data yang sudah di edit dapat di search di inquiry invitation'
    WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
} else {
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    'click button OK'
    WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

    'click button batal'
    WebUI.click(findTestObject('InquiryInvitation/button_Batal'))
}

def checkPaging() {
    if (GlobalVariable.checkPaging == 'Yes') {
        'set text'
        WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), '081220380088')

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

def getData() {
    'get invitationby'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/select_InviteBy'), 'value'))

    'get receiver'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Receiver'), 'value'))

    'get NIK'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NIK'), 'value'))

    'get Nama'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Nama'), 'value'))

    'get TempatLahir'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 'value'))

    'get TanggalLahir'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 'value'))

    'get No Handphone'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NoHandphone'), 'value'))

    'get Email'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'value'))

    'get Alamat'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 'value'))

    'get Provinsi'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 'value'))

    'get Kota'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kota'), 'value'))

    'get Kecamatan'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 'value'))

    'get Kelurahan'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 'value'))

    'get KodePos'
    GlobalVariable.eSignData.add(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 'value'))
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

