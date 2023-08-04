import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

ArrayList<String> listOTP = []

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            13).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'check nama lengkap sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_NamaLengkap'), 
            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            14).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi Tidak Kereset')

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi Tidak Kereset')

'input kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), findTestData(excelPathGenerateLink).getValue(
        GlobalVariable.NumofColm, 49))

'click button mata kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataKataSandi'))

'get text kata sandi'
KataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 'value')

'check kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            49), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi Tidak Sesuai Inputan')

'input ulang kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), findTestData(excelPathGenerateLink).getValue(
        GlobalVariable.NumofColm, 50))

'click button mata ulang kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataUlangKataSandi'))

'get text ulang kata sandi'
UlangKataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 'value')

'check ulang kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            50), false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi tidak Sesuai Inputan')

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/alertText'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/alertText'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2).replace(
            '-', '') + ';') + '<' + reason + '>')

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementClickable(findTestObject('BuatUndangan/FormAktivasi/button_Proses'), FailureHandling.OPTIONAL)) {
    'click button proses'
    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_Proses'))

    'delay untuk menunggu OTP'
    WebUI.delay(5)

    'get OTP dari DB'
    OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            13).replace('"', '').toUpperCase())

    'clear arraylist sebelumnya'
    listOTP.clear()

    'add otp ke list'
    listOTP.add(OTP)

    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 51).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'input OTP'
        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), OTP)

        'get count untuk resend OTP dari excel'
        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 53))

        'jika count resend otp excel > 0'
        if (countResend > 0) {
            'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
            OTPResendCount = 2

            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('BuatUndangan/FormAktivasi/kirimKodeLagi'))

                'verify popup message maximal resend OTP'
                if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + '<' + reason + '>')

                    'click button tutup error'
                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

                    GlobalVariable.FlagFailed = 1

                    break
                } else {
                    'get data reset request OTP dari DB'
                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                            GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                    'verify counter OTP Katalon sesuai dengan counter OTP DB'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' Count OTP Tidak Sesuai')
                }
                
                'delay untuk menunggu OTP'
                WebUI.delay(3)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP tidak kereset')

                'select OTP'
                WebUI.sendKeys(findTestObject('DaftarAkun/input_OTP'), Keys.chord(Keys.CONTROL, 'A'))

                'input OTP'
                WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), OTP)
            }
        }
        
        'click button proses OTP'
        WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_ProsesOTP'))

        'check if aktivasi berhasil dengan OTP yang benar'
        if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL) && (GlobalVariable.FlagFailed == 0)) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Generate Inv Link', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        }
    } else {
        'input OTP'
        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathGenerateLink).getValue(
                GlobalVariable.NumofColm, 52))

        'get count untuk resend OTP dari excel'
        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 53))

        'jika count resend OTP excel > 0'
        if (countResend > 0) {
            'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
            OTPResendCount = 2

            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('BuatUndangan/FormAktivasi/kirimKodeLagi'))

                'verify popup message maximal resend OTP'
                if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + reason)

                    'click button tutup error'
                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

                    GlobalVariable.FlagFailed = 1

                    break
                } else {
                    'get data reset request OTP dari DB'
                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                            GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                    'verify counter OTP Katalon sesuai dengan counter OTP DB'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' Count OTP tidak sesuai')
                }
                
                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP tidak Kereset')

                'input OTP'
                WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, 52))
            }
        }
        
        'click button proses OTP'
        WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_ProsesOTP'))

        'check if aktivasi berhasil dengan OTP yang Salah'
        if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + GlobalVariable.ReasonFailedOTPError)

            GlobalVariable.FlagFailed = 1
        } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + '<' + reason + '>')

            'click button tutup error'
            WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

            'click button X tutup popup OTP'
            WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_X'))

            GlobalVariable.FlagFailed = 1
        }
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

