import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianPengguna).columnNumbers

int firstRun = 0

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        if ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')) != findTestData(
            excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathPencarianPengguna
                    , ('Email') : 'Inveditor Login', ('Password') : 'Inveditor Password Login', ('Perusahaan') : 'Inveditor Perusahaan Login'
                    , ('Peran') : 'Inveditor Peran Login'], FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging()
            }
            
            firstRun = 1
        }
        
        'click menu PencarianPengguna'
        WebUI.click(findTestObject('PencarianPengguna/menu_PencarianPengguna'))

        if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase(
            'Email')) {
            'set text search box dengan email'
            WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, rowExcel('Email')))
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase(
            'Phone')) {
            'set text search box dengan Phone'
            WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, rowExcel('$No Handphone')))
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase(
            'Id no')) {
            'set text search box dengan NIK'
            WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, rowExcel('$NIK')))
        }
        
        'click button cari'
        WebUI.click(findTestObject('PencarianPengguna/button_Cari'))

        'jika hasil pencarian tidak muncul'
        if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/noDataWarning'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Hasil Pencarian tidak ada')
        } else {
            if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                'Edit')) {
                'click button edit'
                WebUI.click(findTestObject('PencarianPengguna/button_Edit'))

                'get data PencarianPengguna dari DB'
                ArrayList result = CustomKeywords.'connection.PencarianPengguna.getDataPencarianPengguna'(conneSign, findTestData(
                        excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                'declare arrayindex'
                arrayindex = 0

                'verify nama'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_NamaLengkap'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

                'verify tempat lahir'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TempatLahir'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir')

                'verify tanggal lahir'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_TanggalLahir'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

                'verify email'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Email'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

                'verify provinsi'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Provinsi'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi')

                'verify kota'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kota'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Kota')

                'verify kecamatan'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kecamatan'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan')

                'verify kelurahan'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_Kelurahan'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan')

                'verify kode pos'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPengguna/input_KodePos'), 
                            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Kode Pos')

                'edit phone number'
                WebUI.setText(findTestObject('PencarianPengguna/input_noHandphone'), findTestData(excelPathPencarianPengguna).getValue(
                        GlobalVariable.NumofColm, rowExcel('$No Handphone')))

                'click button simpan'
                WebUI.click(findTestObject('PencarianPengguna/button_Simpan'))

                'check if no error log atau tidak ada perubahan data '
                if (WebUI.verifyElementNotPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL) || WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', 
                    FailureHandling.OPTIONAL).equalsIgnoreCase('Tidak ada perubahan data')) {
                    if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        'click button OK'
                        WebUI.click(findTestObject('PencarianPengguna/button_OK'))
                    }
                    
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                } else if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL) && WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), 
                    GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('PencarianPengguna/button_OK'))

                    'get alert'
                    AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedPaging)

                    'click button kembali'
                    WebUI.click(findTestObject('PencarianPengguna/button_Kembali'))
                }
            } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                'Reset OTP')) {
                'click button reset OTP'
                WebUI.click(findTestObject('PencarianPengguna/button_ResetOTP'))

                'click button Ya Kirim OTP'
                WebUI.click(findTestObject('PencarianPengguna/button_YaKirimOTP'))

                if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/MessagePopUp'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('PencarianPengguna/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    'get data reset request OTP dari DB'
                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
                            GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                    'verify OTP reset menjadi 0'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, 0, FailureHandling.CONTINUE_ON_FAILURE), ' OTP tidak kereset')
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
                }
            } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                'Resend Link')) {
                'click button resend link'
                WebUI.click(findTestObject('PencarianPengguna/button_ResendLinkaktivasi'))

                if (WebUI.verifyElementPresent(findTestObject('PencarianPengguna/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'get alert'
                    AlertMsg = WebUI.getAttribute(findTestObject('PencarianPengguna/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                    if (AlertMsg.contains('berhasil')) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    } else {
                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + 
                            ' Link')
                    }
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
                }
            }
        }
    }
}

def checkPaging() {
    'click menu PencarianPengguna'
    WebUI.click(findTestObject('PencarianPengguna/menu_PencarianPengguna'))

    'input search box'
    WebUI.setText(findTestObject('PencarianPengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, rowExcel('Email')))

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedPaging)
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}