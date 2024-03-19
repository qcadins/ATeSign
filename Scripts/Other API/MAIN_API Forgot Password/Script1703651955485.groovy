import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        String email = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email'))

        String emailSHA256

        if (email.contains('@')) {
            emailSHA256 = email
        } else {
            emailSHA256 = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(email)
        }
        
        'setting email service tenant'
        CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(excelPath).getValue(
                GlobalVariable.NumofColm, rowExcel('Setting Email Service')), emailSHA256)

        GlobalVariable.Tenant = CustomKeywords.'connection.ForgotPassword.getTenantCode'(conneSign, emailSHA256)

        CustomKeywords.'connection.UpdateData.updateDBForgotPasswordLevelNotification'(conneSign, excelPath, sheet)

        'write to excel otp before'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('OTP before') - 
            1, GlobalVariable.NumofColm - 1, CustomKeywords.'connection.ForgotPassword.getResetCode'(conneSign, email))

        'HIT API send otp ke email invitasi'
        respon = WS.sendRequest(findTestObject('Postman/forgotPassword', [('callerId') : email, ('loginId') : email, ('sendingPointOption') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption'))]))

        checkBalanceMutation(conneSign, emailSHA256)

        'ambil lama waktu yang diperlukan hingga request menerima balikan'
        elapsedTime = ((respon.elapsedTime / 1000) + ' second')

        'ambil body dari hasil respons'
        responseBody = respon.responseBodyContent

        'panggil keyword untuk proses beautify dari respon json yang didapat'
        CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

        'write to excel response elapsed time'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
            1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'write to excel otp latest'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('OTP latest') - 
                    1, GlobalVariable.NumofColm - 1, CustomKeywords.'connection.ForgotPassword.getResetCode'(conneSign, 
                        email))

                'cek apakah send ulang OTP berhasil'
                if (WebUI.verifyNotMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('OTP before')), 
                    findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('OTP latest')), false, FailureHandling.OPTIONAL)) {
                    if ((GlobalVariable.checkStoreDB == 'Yes') && (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('Setting Email Service')) == '1')) {
                        WebUI.delay(1)

                        'declare arraylist arraymatch'
                        ArrayList arrayMatch = []

                        'ambil data last transaction dari DB'
                        ArrayList resultDB = CustomKeywords.'connection.ForgotPassword.getBusinessLineOfficeCode'(conneSign, 
                            email)

                        'declare arrayindex'
                        arrayindex = 0

                        'lakukan loop untuk pengecekan data'
                        for (int i = 0; i < (resultDB.size() / 2); i++) {
                            'verify business line dan office code'
                            arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 2)]).toString(), false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        'jika data db tidak sesuai dengan excel'
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
                        }
                    }
                    
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    'Write To Excel GlobalVariable.StatusFailed and errormessage'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Hit sukses, namun OTP tidak ter-update')
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
} /* Code#1
    if (mustUseWAFirst == '1') {
        'menggunakan saldo wa'
        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
        }
        
        if ((balmut[9]) != -1) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + 'Saldo WA tidak terpotong')
        }
    } else {
        if (emailServiceOnVendor == '1') {
            useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

            if (useWAMessage == '1') {
                'menggunakan saldo wa'
                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                    fullNameUser)

                if (balmut.size() == 0) {
                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                }
                
                if ((balmut[9]) != -1) {
                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')).replace('-', '') + ';') + 'Saldo WA tidak terpotong')
                }
            } else if (useWAMessage == '0') {
                'ke sms / wa'
                SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Forgot Password')

                if (SMSSetting == '1') {
                    'ke sms'

                    'menggunakan saldo wa'
                    ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'SMS Notif', fullNameUser)

                    if (balmut.size() == 0) {
                        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
                    }
                    
                    if ((balmut[9]) != -1) {
                        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')).replace('-', '') + ';') + 'Saldo SMS tidak terpotong')
                    }
                }
            }
        }
    }
    */

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkBalanceMutation(Connection conneSign, String emailSigner) {
    emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, emailSigner)

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner)

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

    'Code#1'

    'mengambil value dari sendingpoint option'
    GlobalVariable.chooseOTP = findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption'))

    'jika chooseotpnya adalah wa'
    if (GlobalVariable.chooseOTP.toString().contains('WA')) {
        'menggunakan saldo wa'
        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WA')
        }
        
        if ((balmut[9]) != '-1') {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Saldo WA tidak terpotong')
        }
    }
    
    'jika sending pointnya adalah sms'
    if (GlobalVariable.chooseOTP.toString().contains('SMS')) {
        'menggunakan saldo wa'
        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'OTP', fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
        }
        
        if ((balmut[9]) != '-1') {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + 'Saldo SMS tidak terpotong')
        }
    }
    
    'cek apakah perlu untuk pengecekan DB'
    if (GlobalVariable.checkStoreDB == 'Yes') {
        WebUI.delay(1)

        'declare arraylist arraymatch'
        ArrayList arrayMatch = []

        'ambil data last transaction dari DB'
        ArrayList resultDB = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, findTestData(excelPath).getValue(
                GlobalVariable.NumofColm, rowExcel('Email')), 'Document')

        'declare arrayindex'
        arrayindex = 0

        'lakukan loop untuk pengecekan data'
        for (int i = 0; i < (resultDB.size() / 2); i++) {
            'verify business line dan office code'
            arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
        }
        
        'jika data db tidak sesuai dengan excel'
        if (arrayMatch.contains(false)) {
            GlobalVariable.FlagFailed = 1

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
        }
    }
}

