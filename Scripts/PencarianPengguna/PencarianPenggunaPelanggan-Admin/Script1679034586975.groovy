import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'call test case login admin'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'call function check paging'
checkPaging()

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
    'set text search box dengan email'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 11))
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
    'set text search box dengan Phone'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 10))
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
    'set text search box dengan NIK'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 9))
}

'click button cari'
WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

'check if View / reset OTP'
if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('View')) {
	
	'click button view'
	WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_View'))
	
	'get data view dari DB'
	ArrayList<String> resultData = CustomKeywords.'connection.DataVerif.getPencarianPengguna'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
			GlobalVariable.NumofColm, 11).toUpperCase())
	
	index = 0
	
	'verify nama'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Nama'), 'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Email'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Email'), 'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'parse Date from yyyy-MM-dd > dd-MMM-yyyy'
	sDate = CustomKeywords.'customizeKeyword.ParseDate.parseDateFormat'(resultData[index++], 'yyyy-MM-dd', 'dd-MMM-yyyy')
	
	'verify tanggal lahir'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_TanggalLahir'), 'value', FailureHandling.OPTIONAL), sDate, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Status AutoSign'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_StatusAutoSign'), 'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Status'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Status'), 'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click button kembali'
	WebUI.click(findTestObject('PencarianPenggunaAdmin/View/button_Kembali'))
	
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Reset OTP')) {
    'click button reset OTP'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResetOTP'))

    'click button Ya Kirim OTP'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_YaKirimOTP'))

    if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/MessagePopUp'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_OK'))

        'write to excel success'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Pelanggan', 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        'get data reset request OTP dari DB'
        String resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, 11).toUpperCase())

        'verify OTP reset menjadi 0'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
    } else {
        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
    }
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Resend Link')) {
    'click button resend link'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResendLink'))

    if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get alert'
        AlertMsg = WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        if (AlertMsg.contains('berhasil')) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Pelanggan', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
        }
    } else {
        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
    }
}

public checkPaging() {
    'click menu pencarian pengguna'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'))

    'click menu pelanggan'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/menu_Pelanggan'))

    'input search box'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 11))

    'click button cari'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

    'click button reset'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Reset'))

    'verify search box reset'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

public checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

