import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

int delayExpiredOTP

'check if email kosong atau tidak'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).length() > 0) {
	'setting reset OTP pada DB 0'
	CustomKeywords.'connection.DataVerif.settingResetOTPNol'(conneSign, findTestData(excelPathBuatUndangan).getValue(
			GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())
} else {
	'setting reset OTP pada DB 0'
	CustomKeywords.'connection.DataVerif.settingResetOTPNol'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			rowExcel('$No Handphone')).replace('"', '').toUpperCase())
}

'check ada value maka Setting OTP Active Duration'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
	'Setting OTP Active Duration'
	CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
	
	delayExpiredOTP = 60 * Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
}

'check if email kosong atau tidak'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).length() > 0) {
	email = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email'))
} else {
	'get name + email hosting'
	email = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')) + CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign)
}

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), email.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'check nama lengkap sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_NamaLengkap'), 
            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_SetUlang'))

WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_SetUlang'))

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Kata Sandi tidak kereset')

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Fiel Ulang Kata Sandi tidak kereset')

'input kata sandi'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, rowExcel('Password')))

'click button mata kata sandi'
WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_MataKataSandi'))

'get text kata sandi'
KataSandi = WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_KataSandi'), 'value')

'check kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Password')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata sandi tidak sesuai inputan')

'input ulang kata sandi'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, rowExcel('Retype Password')))

'click button mata ulang kata sandi'
WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_MataUlangKataSandi'))

'get text ulang kata sandi'
UlangKataSandi = WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiEsign/input_UlangKataSandi'), 'value')

'check ulang kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Retype Password')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi tidak sesuai inputan')

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/alertText'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/alertText'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementClickable(findTestObject('RegisterEsign/FormAktivasiEsign/button_Proses'), FailureHandling.OPTIONAL)) {
    'flag untuk count inputed'
	int inputed = 0
	
	'call function input otp'
	inputed = inputOTP(inputed, delayExpiredOTP, conneSign)
	
	if(inputed > 0 && findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Resend After Check Expired')).equalsIgnoreCase('Yes')) {
		
		'call function input otp'
		inputOTP(inputed, delayExpiredOTP, conneSign)
	}
}

def inputOTP(int inputed, int delayExpiredOTP, Connection conneSign) {
	'declare arraylist OTP'
	ArrayList<String> listOTP = []
	
	'click button proses'
	WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_Proses'))

	'delay untuk menunggu otp'
	WebUI.delay(5)

	'get otp dari DB'
	otp = getOTP(conneSign)

	'+1 count send otp'
	(GlobalVariable.Counter)++

	'clear arraylist sebelumnya'
	listOTP.clear()

	'add otp ke list'
	listOTP.add(otp)
	
	println(otp)

	if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Autofill OTP - Aktivasi')).equalsIgnoreCase('Yes')) {
		'delay untuk menunggu otp'
		WebUI.delay(5)

		'input otp'
		WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), otp)

		'get count untuk resend otp dari excel'
		countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))

		'jika count resend otp excel > 0'
		if (countResend > 0) {
			'count mulai dari 2 karena pertama kali ngeklik resend otp sudah terhitung kedua kalinya request untuk resend otp'
			OTPResendCount = 2

			for (int i = 0; i < countResend; i++) {
				'tunggu button resend otp'
				WebUI.delay(315)

				'klik pada button kirim ulang otp'
				WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/kirimKodeLagi'))

				'+1 count send otp'
				(GlobalVariable.Counter)++

				'verify popup message maximal resend otp'
				if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut,
					FailureHandling.OPTIONAL)) {
					reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

					'write to excel status failed dan reason'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

					'click button tutup error'
					WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

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
				otp = getOTP(conneSign)

				'add otp ke list'
				listOTP.add(otp)

				'check if otp resend berhasil'
				checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

				'select otp'
				WebUI.sendKeys(findTestObject('DaftarAkun/input_OTP'), Keys.chord(Keys.CONTROL, 'A'))

				'input otp'
				WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), otp)
			}
		}
		
		'check if ingin testing expired otp'
		if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0 && inputed == 0) {
			'delay untuk input expired otp'
			WebUI.delay(delayExpiredOTP + 10)
		}
	} else {
		'input otp'
		WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), findTestData(excelPathBuatUndangan).getValue(
				GlobalVariable.NumofColm, rowExcel('Manual OTP - Aktivasi')))

		'get count untuk resend otp dari excel'
		countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))

		'jika count resend otp excel > 0'
		if (countResend > 0) {
			'count mulai dari 2 karena pertama kali ngeklik resend otp sudah terhitung kedua kalinya request untuk resend otp'
			OTPResendCount = 2

			for (int i = 0; i < countResend; i++) {
				'tunggu button resend otp'
				WebUI.delay(315)

				'klik pada button kirim ulang otp'
				WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/kirimKodeLagi'))

				'+1 count send otp'
				(GlobalVariable.Counter)++

				'verify popup message maximal resend otp'
				if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut,
					FailureHandling.OPTIONAL)) {
					reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

					'write to excel status failed dan reason'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')

					'click button tutup error'
					WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

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
				otp = getOTP(conneSign)

				'add otp ke list'
				listOTP.add(otp)

				'check if otp resend berhasil'
				checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

				'input otp'
				WebUI.setText(findTestObject('RegisterEsign/FormAktivasiEsign/input_OTP'), findTestData(excelPathBuatUndangan).getValue(
						GlobalVariable.NumofColm, rowExcel('Manual OTP - Aktivasi')))
			}
		}
	}
	
	'click button proses otp'
	WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_ProsesOTP'))

	'check if aktivasi berhasil dengan otp yang benar'
	if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
		FailureHandling.OPTIONAL) && (GlobalVariable.FlagFailed == 0)) {
		if (GlobalVariable.FlagFailed == 0) {
			'write to excel success'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, SheetName, 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
		}
	} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
		FailureHandling.OPTIONAL)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
			';') + GlobalVariable.ReasonFailedOTPError)

		GlobalVariable.FlagFailed = 1
		
		inputed = 1
	} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'), GlobalVariable.TimeOut,
		FailureHandling.OPTIONAL)) {
		reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiEsign/label_PopupMsg'))

		'write to excel status failed dan reason'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + '<' + reason + '>')

		'click button tutup error'
		WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_OK'))

		'click button X tutup popup otp'
		WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_X'))

		GlobalVariable.FlagFailed = 1
		
		inputed = 1
	}
	
	return inputed
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

def getOTP(Connection conneSign) {
	'declare string OTP'
	String OTP

	'check if email kosong atau tidak'
	if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).length() > 0) {
		'get OTP dari DB'
		OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase())
	} else {
		'get OTP dari DB'
		OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('$No Handphone')).toUpperCase())
	}
	
	return OTP
}