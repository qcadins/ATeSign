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

int delayExpiredOTP = 60

'check ada value maka Setting OTP Active Duration'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
	'Setting OTP Active Duration'
	CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
	
	delayExpiredOTP = delayExpiredOTP * Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
}

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Email')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'check nama lengkap sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_NamaLengkap'), 
            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Kata Sandi tidak kereset')

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Fiel Ulang Kata Sandi tidak kereset')

'input kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, rowExcel('Password')))

'click button mata kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataKataSandi'))

'get text kata sandi'
KataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 'value')

'check kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Password')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata sandi tidak sesuai inputan')

'input ulang kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, rowExcel('Retype Password')))

'click button mata ulang kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataUlangKataSandi'))

'get text ulang kata sandi'
UlangKataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 'value')

'check ulang kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Retype Password')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi tidak sesuai inputan')

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/alertText'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/alertText'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementClickable(findTestObject('BuatUndangan/FormAktivasi/button_Proses'), FailureHandling.OPTIONAL)) {
    'click button proses'
    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_Proses'))

    'delay untuk menunggu otp'
    WebUI.delay(5)

    'get otp dari DB'
    otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Email')).toUpperCase())

    '+1 count send otp'
    (GlobalVariable.Counter)++

    'clear arraylist sebelumnya'
    listOTP.clear()

    'add otp ke list'
    listOTP.add(otp)

    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Autofill OTP')).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu otp'
        WebUI.delay(5)

        'input otp'
        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), otp)

        'get count untuk resend otp dari excel'
        countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        'jika count resend otp excel > 0'
        if (countResend > 0) {
            'count mulai dari 2 karena pertama kali ngeklik resend otp sudah terhitung kedua kalinya request untuk resend otp'
            OTPResendCount = 2

            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('BuatUndangan/FormAktivasi/kirimKodeLagi'))

                '+1 count send otp'
                (GlobalVariable.Counter)++

                'verify popup message maximal resend otp'
                if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

                    'click button tutup error'
                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

                    GlobalVariable.FlagFailed = 1

                    break
                } else {
                    'get data reset request otp dari DB'
                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                            GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                    'verify counter otp Katalon sesuai dengan counter otp DB'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
                }
                
                'delay untuk menunggu otp'
                WebUI.delay(3)

                'get otp dari DB'
                otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                        GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                'add otp ke list'
                listOTP.add(otp)

                'check if otp resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

                'select otp'
                WebUI.sendKeys(findTestObject('DaftarAkun/input_OTP'), Keys.chord(Keys.CONTROL, 'A'))

                'input otp'
                WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), otp)
            }
        }
        
		'check if ingin testing expired otp'
		if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
			'delay untuk input expired otp'
			WebUI.delay(delayExpiredOTP + 10)
		}
    } else {
        'input otp'
        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, rowExcel('Manual OTP')))

        'get count untuk resend otp dari excel'
        countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        'jika count resend otp excel > 0'
        if (countResend > 0) {
            'count mulai dari 2 karena pertama kali ngeklik resend otp sudah terhitung kedua kalinya request untuk resend otp'
            OTPResendCount = 2

            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('BuatUndangan/FormAktivasi/kirimKodeLagi'))

                '+1 count send otp'
                (GlobalVariable.Counter)++

                'verify popup message maximal resend otp'
                if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

                    'click button tutup error'
                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

                    GlobalVariable.FlagFailed = 1

                    break
                } else {
                    'get data reset request otp dari DB'
                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                            GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                    'verify counter otp Katalon sesuai dengan counter otp DB'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
                }
                
                'delay untuk menunggu otp'
                WebUI.delay(5)

                'get otp dari DB'
                otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                        GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())

                'add otp ke list'
                listOTP.add(otp)

                'check if otp resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

                'input otp'
                WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathBuatUndangan).getValue(
                        GlobalVariable.NumofColm, rowExcel('Manual OTP')))
            }
        }
    }
	
	'click button proses otp'
	WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_ProsesOTP'))

	'check if aktivasi berhasil dengan otp yang benar'
	if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
		FailureHandling.OPTIONAL) && (GlobalVariable.FlagFailed == 0)) {
		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, SheetName, 0, GlobalVariable.NumofColm -
			1, GlobalVariable.StatusSuccess)
	} else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedOTPError)

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + '<' + reason + '>')

        'click button tutup error'
        WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

        'click button X tutup popup otp'
        WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_X'))

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}