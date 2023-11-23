import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String value

'jika pengguna belum login'
if (WebUI.verifyElementNotPresent(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'), GlobalVariable.TimeOut, 
    FailureHandling.OPTIONAL)) {
    'panggil fungsi login'
    WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathRegister, ('Email') : 'Username'
            , ('Password') : 'Password', ('Perusahaan') : 'Perusahaan', ('Peran') : 'Role'], FailureHandling.CONTINUE_ON_FAILURE)
}

'click menu pencarian pengguna'
WebUI.click(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'))

'click menu pelanggan'
WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/menu_Pelanggan'))

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('Email')) {
    'set text search box dengan email'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('$Email')).replace('"',''))
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('Phone')) {
    'set text search box dengan Phone'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"',''))
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('NIK')) {
    'set text search box dengan NIK'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('$NIK')).replace('"',''))
}

'click button cari'
WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

'jika hasil pencarian tidak muncul'
if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/noDataWarning'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + 'Hasil Pencarian tidak ada')
} else {
    'check if View / reset OTP'
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).equalsIgnoreCase(
        'View')) {
        'click button view'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_View'))

        'get data view dari DB'
        ArrayList<String> resultData = CustomKeywords.'connection.PencarianPengguna.getPencarianPengguna'(conneSign, findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"','').toUpperCase())

        index = 0

        'verify nama'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Nama'), 
                    'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Nama')

        'verify Email'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Email'), 
                    'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Email')

        'parse Date from yyyy-MM-dd > dd-MMM-yyyy'
        sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultData[index++], 'yyyy-MM-dd', 'dd-MMM-yyyy')

        'verify tanggal lahir'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_TanggalLahir'), 
                    'value', FailureHandling.OPTIONAL), sDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

        'verify Status AutoSign'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_StatusAutoSign'), 
                    'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Status Auto Sign')

        'verify Status'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Status'), 
                    'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Input Status')

        'click button kembali'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/View/button_Kembali'))
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).equalsIgnoreCase(
        'Reset OTP')) {
        'click button reset OTP'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResetOTP'))

        'click button Ya Kirim OTP'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_YaKirimOTP'))

        if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/MessagePopUp'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_OK'))

            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')) == 'Email') {
                value = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"','').toUpperCase()
            } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')) == 'Phone') {
                value = convertSHA256(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"',''))
            } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input with')) == 'Id no') {
                value = convertSHA256(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$NIK')).replace('"',''))
            }
            
            'get data reset request OTP dari DB'
            int resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, value)

            'verify OTP reset menjadi 0'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP.toString(), '0', false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' OTP tidak Kereset')
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
        }
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).equalsIgnoreCase(
        'Resend Link')) {
        'click button resend link'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResendLink'))

        if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'get alert'
            AlertMsg = WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            if (AlertMsg.contains('berhasil')) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            } else {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
            }
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
        }
    }
}

if (GlobalVariable.FlagFailed == 0) {
    'write to excel success'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
        1, GlobalVariable.StatusSuccess)
}

def checkPaging() {
    'input search box'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('$Email')).replace('"',''))

    'click button cari'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

    'click button reset'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Reset'))

    'verify search box reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def convertSHA256(String input) {
    return CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(input)
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

