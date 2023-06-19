import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'click menu PencarianPengguna'
WebUI.click(findTestObject('PencarianPengguna/menu_PencarianPengguna'))

'call function check paging'
checkPaging()

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
    'set text search box dengan email'
    WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 15))
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
    'set text search box dengan Phone'
    WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 14))
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
    'set text search box dengan NIK'
    WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 9))
}

'click button cari'
WebUI.click(findTestObject('PencarianPengguna/button_Cari'))

if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Edit')) {
    'click button edit'
    WebUI.click(findTestObject('PencarianPengguna/button_Edit'))

    'get data PencarianPengguna dari DB'
    ArrayList<String> result = CustomKeywords.'connection.PencarianPengguna.getDataPencarianPengguna'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 15).toUpperCase())

    'declare arrayindex'
    arrayindex = 0

    'verify nama'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_NamaLengkap'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

    'verify tempat lahir'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TempatLahir'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir')

    'verify tanggal lahir'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TanggalLahir'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

    'verify email'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Email'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

    'verify provinsi'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Provinsi'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi')

    'verify kota'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kota'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kota')

    'verify kecamatan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kecamatan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan')

    'verify kelurahan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kelurahan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan')

    'verify kode pos'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_KodePos'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kode Pos')

    'edit phone number'
    WebUI.setText(findTestObject('PencarianPengguna/input_noHandphone'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 14))

    'click button simpan'
    WebUI.click(findTestObject('PencarianPengguna/button_Simpan'))

    'check if no error log atau tidak ada perubahan data '
    if (WebUI.verifyElementNotPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) || 
    WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL).equalsIgnoreCase(
        'Tidak ada perubahan data')) {
        if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('PencarianPengguna/button_OK'))
        }
        
        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 
            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
    } else if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
    WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('PencarianPengguna/button_OK'))

        'get alert'
        AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedPaging)

        'click button kembali'
        WebUI.click(findTestObject('PencarianPengguna/button_Kembali'))
    }
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Reset OTP')) {
    'click button reset OTP'
    WebUI.click(findTestObject('PencarianPengguna/button_ResetOTP'))

    'click button Ya Kirim OTP'
    WebUI.click(findTestObject('PencarianPengguna/button_YaKirimOTP'))

    if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button OK'
        WebUI.click(findTestObject('PencarianPengguna/button_OK'))

        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 
            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

        'get data reset request OTP dari DB'
        Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, 15).toUpperCase())

        'verify OTP reset menjadi 0'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP, 0, false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP tidak kereset')
    } else {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
    }
} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Resend Link')) {
    'click button resend link'
    WebUI.click(findTestObject('PencarianPengguna/button_ResendLinkaktivasi'))

    if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get alert'
        AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        if (AlertMsg.contains('berhasil')) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Inveditor', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
        }
    } else {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
    }
}

public checkPaging() {
    'input search box'
    WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 15))

    'click button cari'
    WebUI.click(findTestObject('PencarianPengguna/button_Cari'))

    'click button reset'
    WebUI.click(findTestObject('PencarianPengguna/button_Reset'))

    'verify search box reset'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_SearchBox'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' search box tidak kereset')

    'click button edit'
    WebUI.click(findTestObject('PencarianPengguna/button_Edit'))

    if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/button_Kembali'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button kembali'
        WebUI.click(findTestObject('PencarianPengguna/button_Kembali'))
    } else {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedPaging)
    }
}

public checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Inveditor', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

