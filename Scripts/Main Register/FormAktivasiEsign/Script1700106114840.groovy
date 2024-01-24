import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'check if ingin menggunakan embed atau tidak'
if (GlobalVariable.RunWithEmbed == 'Yes') {
    'replace https > http'
    link = GlobalVariable.Link

    'check if ingin menggunakan local host atau tidak'
    if (GlobalVariable.useLocalHost == 'Yes') {
        'navigate url ke daftar akun'
        WebUI.navigateToUrl(GlobalVariable.embedUrl.replace('http://gdkwebsvr:8080', GlobalVariable.urlLocalHost))

        WebUI.delay(3)

        'navigate url ke daftar akun'
        WebUI.setText(findTestObject('EmbedView/inputLinkEmbed'), link.replace('http://gdkwebsvr:8080', GlobalVariable.urlLocalHost))
    } else if (GlobalVariable.useLocalHost == 'No') {
        'navigate url ke daftar akun'
        WebUI.navigateToUrl(GlobalVariable.embedUrl)

        WebUI.delay(3)

        'navigate url ke daftar akun'
        WebUI.setText(findTestObject('EmbedView/inputLinkEmbed'), link)
    }
    
    'click button embed'
    WebUI.click(findTestObject('EmbedView/button_Embed'))

    'swith to iframe'
    WebUI.switchToFrame(findTestObject('EmbedView/iFrameEsign'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
} else if (GlobalVariable.RunWithEmbed == 'No') {
    'replace https > http'
    link = GlobalVariable.Link.replace('https', 'http')

    'check if ingin menggunakan local host atau tidak'
    if (GlobalVariable.useLocalHost == 'Yes') {
        'navigate url ke daftar akun'
        WebUI.openBrowser(link)

        'delay 3 detik'
        WebUI.delay(3)

        'replace gdk > localhost'
        link = GlobalVariable.Link.replace('https://gdkwebsvr:8080', GlobalVariable.urlLocalHost)

        'navigate url ke daftar akun'
        WebUI.navigateToUrl(link)
    } else if (GlobalVariable.useLocalHost == 'No') {
        'navigate url ke daftar akun'
        WebUI.openBrowser(link)
    }
}

'maximize window'
WebUI.maximizeWindow()

if (GlobalVariable.Psre == 'PRIVY') {
    if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_SuccessPrivy'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get message dari ui'
        reason = WebUI.getText(findTestObject('DaftarAkun/label_SuccessPrivy'))

        'check if registrasi berhasil dan write ke excel'
        if (reason.equalsIgnoreCase('Proses verifikasi anda sedang diproses. Harap menunggu proses verifikasi selesai.') && 
        (GlobalVariable.FlagFailed == 0)) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + reason) + '>')

            GlobalVariable.FlagFailed = 1
        }
        
        'call function check mutation trx'
        checkTrxMutation(conneSign)
    }
} else {
    'check if email kosong atau tidak'
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Edit') && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase(
        'Email') || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).length() > 
    0))) {
        'get email dari row edit'
        email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).replace('"', 
            '')
    } else if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2) && 
    !(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Edit'))) {
        'get email dari row input'
        email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '')
    } else {
        'get name + email hosting'
        email = (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"', '') + 
        CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign))
    }
    
    'setting reset OTP pada DB 0'
    CustomKeywords.'connection.DataVerif.settingResetOTPNol'(conneSign, email.toUpperCase())

    int delayExpiredOTP

    'check ada value maka Setting OTP Active Duration'
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
    0) {
        'Setting OTP Active Duration'
        CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))

        delayExpiredOTP = (60 * Integer.parseInt(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Setting OTP Active Duration'))))
    }
    
    'check email sesuai dengan inputan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_Email'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), email.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Email')

    'check nama lengkap sesuai dengan inputan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_NamaLengkap'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Nama')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

    'input kata sandi untuk verify button set ulang'
    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), '@Abcd1234')

    'input ulang kata sandi untuk verify button set ulang'
    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), '@Abcd1234')

    'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_SetUlang'))

    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_SetUlang'))

    'verify kata sandi sudah kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), 
                'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi')

    'verify ulang kata sandi sudah kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), 
                'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi')

    'input kata sandi'
    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Password - Aktivasi')))

    'click button mata kata sandi'
    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_MataKataSandi'))

    'get text kata sandi'
    KataSandi = WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), 'value')

    'check kata sandi sesuai inputan excel'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Password - Aktivasi')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi')

    'input ulang kata sandi'
    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Retype Password - Aktivasi')))

    'click button mata ulang kata sandi'
    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_MataUlangKataSandi'))

    'get text ulang kata sandi'
    UlangKataSandi = WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), 'value')

    'check ulang kata sandi sesuai inputan excel'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Retype Password - Aktivasi')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi')

    'verify warning password'
    if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/alertText'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'get text reason'
        reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/alertText'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + '<') + reason) + '>')

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementClickable(findTestObject('RegisterEsign/FormAktivasiEsign/button_Proses'), FailureHandling.OPTIONAL)) {
        'flag untuk count inputed'
        int inputed = 0

        'call function input otp'
        inputed = inputOTP(inputed, delayExpiredOTP, conneSign)

        println(inputed)

        if ((inputed > 0) && findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Resend After Check Expired')).equalsIgnoreCase(
            'Yes')) {
            'call function input otp'
            inputOTP(inputed, delayExpiredOTP, conneSign)
        }
        
        if (GlobalVariable.checkStoreDB == 'Yes') {
            'check jika Must use WA message = 1'
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')) == 
            '1') {
                usedSaldo = 'WhatsApp Message'
            } else {
                'check jika email service on'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                '1') {
                    'check jika use WA message = 1'
                    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')) == 
                    '1') {
                        usedSaldo = 'WhatsApp Message'
                    } else {
                        'jika use WA message bukan 1 maka use OTP'
                        usedSaldo = 'OTP'
                    }
                } else {
                    'jika use WA message bukan 1 maka use OTP'
                    usedSaldo = 'OTP'
                }
            }
            
            resultTrx = CustomKeywords.'connection.APIFullService.getAPIGenInvLinkOTPTrx'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"', ''), usedSaldo)

            'declare arraylist arraymatch'
            ArrayList arrayMatch = []

            int sum = 0

            for (String value : resultTrx) {
                int intValue = Integer.parseInt(value)

                sum += intValue
            }
            
            'verify trx qty = -1'
            arrayMatch.add(WebUI.verifyEqual(sum, -(GlobalVariable.Counter), FailureHandling.CONTINUE_ON_FAILURE))

            'jika data db tidak sesuai dengan excel'
            if (arrayMatch.contains(false)) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    GlobalVariable.ReasonFailedStoredDB)
            }
        }
    }
}

def inputOTP(int inputed, int delayExpiredOTP, Connection conneSign) {
    'declare list otp'
    ArrayList listOTP = []

    'click button proses'
    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_Proses'))

    'verify popup message maximal resend OTP'
    if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + '<') + reason) + '> Form Aktivasi')

        'click button tutup error'
        WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

        GlobalVariable.FlagFailed = 1
    } else {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'call function get otp'
        OTP = getOTP(conneSign)

        '+1 karena request otp'
        (GlobalVariable.Counter)++

        'clear arraylist sebelumnya'
        listOTP.clear()

        'add otp ke list'
        listOTP.add(OTP)

        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input Correct OTP - Aktivasi')).equalsIgnoreCase(
            'Yes')) {
            'delay untuk menunggu OTP'
            WebUI.delay(5)

            'input OTP'
            WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), OTP)

            'get count untuk resend OTP dari excel'
            countResend = Integer.parseInt(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))

            'jika count resend otp excel > 0'
            if (countResend > 0) {
                'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
                OTPResendCount = 2

                for (int i = 0; i < countResend; i++) {
                    'tunggu button resend otp'
                    WebUI.delay(315)

                    'klik pada button kirim ulang otp'
                    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/kirimKodeLagi'))

                    'verify popup message maximal resend OTP'
                    if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + reason) + '>')

                        'click button tutup error'
                        WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

                        GlobalVariable.FlagFailed = 1

                        break
                    } else {
                        'get data reset request OTP dari DB'
                        Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, email.toUpperCase())

                        'verify counter OTP Katalon sesuai dengan counter OTP DB'
                        checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' OTP')
                    }
                    
                    'delay untuk menunggu OTP'
                    WebUI.delay(3)

                    'call function get otp'
                    OTP = getOTP(conneSign)

                    '+1 karena request otp'
                    (GlobalVariable.Counter)++

                    'add OTP ke list'
                    listOTP.add(OTP)

                    'check if OTP resend berhasil'
                    checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' OTP')

                    'select OTP'
                    WebUI.sendKeys(findTestObject('DaftarAkun/input_OTP'), Keys.chord(Keys.CONTROL, 'A'))

                    'input OTP'
                    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), OTP)
                }
            }
            
            'check if ingin testing expired otp'
            if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
            0) && (inputed == 0)) {
                'delay untuk input expired otp'
                WebUI.delay(delayExpiredOTP + 10)
            }
        } else {
            'input OTP'
            WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Wrong OTP - Aktivasi')))

            'get count untuk resend OTP dari excel'
            countResend = Integer.parseInt(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))

            'jika count resend OTP excel > 0'
            if (countResend > 0) {
                'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
                OTPResendCount = 2

                for (int i = 0; i < countResend; i++) {
                    'tunggu button resend otp'
                    WebUI.delay(315)

                    'klik pada button kirim ulang otp'
                    WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/kirimKodeLagi'))

                    'verify popup message maximal resend OTP'
                    if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + reason) + '>')

                        'click button tutup error'
                        WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

                        GlobalVariable.FlagFailed = 1

                        break
                    } else {
                        'get data reset request OTP dari DB'
                        Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, email.toUpperCase())

                        'verify counter OTP Katalon sesuai dengan counter OTP DB'
                        checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' OTP')
                    }
                    
                    'delay untuk menunggu OTP'
                    WebUI.delay(5)

                    'call function get otp'
                    OTP = getOTP(conneSign)

                    '+1 karena request otp'
                    (GlobalVariable.Counter)++

                    'add OTP ke list'
                    listOTP.add(OTP)

                    'check if OTP resend berhasil'
                    checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' OTP')

                    'input OTP'
                    WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Wrong OTP - Aktivasi')))
                }
            }
        }
        
        'click button proses OTP'
        WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_ProsesOTP'))

        'check if error / success'
        if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason error log'
            reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).toString().toLowerCase()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + reason) + '>')

            GlobalVariable.FlagFailed = 1

            inputed = 1
        } else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/popUp_AktivasiBerhasil'), 
            GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
        } else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + reason) + '>')

            'click button tutup error'
            WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

            'click button X tutup popup OTP'
            WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_X'))

            GlobalVariable.FlagFailed = 1

            inputed = 1
        }
    }
    
    inputed
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

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getOTP(Connection conneSign) {
    'declare string OTP'
    String otp

    'get OTP dari DB'
    otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, email.toUpperCase())

    otp
}

