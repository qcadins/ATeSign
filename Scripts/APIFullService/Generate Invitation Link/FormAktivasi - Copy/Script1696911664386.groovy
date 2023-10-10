import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import java.util.stream.IntStream

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'setting reset OTP pada DB 0'
CustomKeywords.'connection.DataVerif.settingResetOTPNol'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('email')).replace('"', '').toUpperCase())

int delayExpiredOTP

'check ada value maka Setting OTP Active Duration'
if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
	'Setting OTP Active Duration'
	CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
	
	delayExpiredOTP = 60 * Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
}

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiDIGI/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('email')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'input username untuk verify button set ulang'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_Username'), '@Abcd1234')

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_Reset'))

WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_Reset'))

'verify username sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiDIGI/input_Username'),
			'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Username')

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiDIGI/input_KataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi')

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/FormAktivasiDIGI/input_UlangKataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi')

'input kata sandi'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_KataSandi'), findTestData(excelPathAPIGenerateInvLink).getValue(
        GlobalVariable.NumofColm, rowExcel('Password')))

'input ulang kata sandi'
WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_UlangKataSandi'), findTestData(excelPathAPIGenerateInvLink).getValue(
        GlobalVariable.NumofColm, rowExcel('Retype Password')))

'cek switch di click atau tidak'
if(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Syarat dan Ketentuan DIGI')) == 'Yes') {
	'click switch sertifikat elektronik'	
	WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/switch_SertifikatElektronik'))
	
	'click switch syarat dan ketentuan'
	WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/switch_SyaratKetentuan'))
}

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertKataSandi'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) ||
	WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertUlangKataSandi'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reasonKataSandi = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertKataSandi'), FailureHandling.OPTIONAL).toString()

	'get text reason'
	reasonUlangKataSandi = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertUlangKataSandi'), FailureHandling.OPTIONAL).toString()
	
    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + '<' + reasonKataSandi + ';' + reasonUlangKataSandi + '>')

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementClickable(findTestObject('RegisterEsign/FormAktivasiDIGI/button_Proses'), FailureHandling.OPTIONAL)) {
	'flag untuk count inputed'
	int inputed = 0
	
	'call function input otp'
	inputed = inputOTP(inputed, delayExpiredOTP, conneSign)
	
	println(inputed)
	if(inputed > 0 && findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend After Check Expired')).equalsIgnoreCase('Yes')) {
		
		'call function input otp'
		inputOTP(inputed, delayExpiredOTP, conneSign)
	}
	
	if (GlobalVariable.checkStoreDB == 'Yes') {
		resultTrx = CustomKeywords.'connection.APIFullService.getAPIGenInvLinkOTPTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')).replace('"',''))

		'declare arraylist arraymatch'
		ArrayList<String> arrayMatch = []

		int sum = 0
		for (String value : resultTrx) {
			int intValue = Integer.parseInt(value)
			sum += intValue
		}
		
		'verify trx qty = -1'
		arrayMatch.add(WebUI.verifyEqual(sum, -GlobalVariable.Counter, FailureHandling.CONTINUE_ON_FAILURE))

		'jika data db tidak sesuai dengan excel'
		if (arrayMatch.contains(false)) {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
		}
	}
}

def inputOTP(int inputed, int delayExpiredOTP, Connection conneSign) {
	'declare list otp'
	ArrayList<String> listOTP = []
	
	'click button proses'
	WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_Proses'))

	'verify warning password'
	if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertKataSandi'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) ||
		WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertUlangKataSandi'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	    'get text reason'
	    reasonKataSandi = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertKataSandi'), FailureHandling.OPTIONAL).toString()
	
		'get text reason'
		reasonUlangKataSandi = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_AlertUlangKataSandi'), FailureHandling.OPTIONAL).toString()
		
	    'write to excel status failed dan reason'
	    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
	        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
	            '-', '') + ';') + '<' + reasonKataSandi + ';' + reasonUlangKataSandi + '>')
	
	    GlobalVariable.FlagFailed = 1
	} else {
	
		'+1 karena request otp'
		GlobalVariable.Counter++
	
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Input Correct OTP - Aktivasi')).equalsIgnoreCase('Yes')) {
			'delay untuk input manual otp ke excel'
			WebUI.delay(30)
			
			'input OTP'
			WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('Wrong OTP - Aktivasi')))
	
			'get count untuk resend OTP dari excel'
			countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))
	
			'jika count resend otp excel > 0'
			if (countResend > 0) {
				'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
				OTPResendCount = 2
				
				for (int i = 0; i < countResend; i++) {
					'tunggu button resend otp'
					WebUI.delay(65)
	
					'klik pada button kirim ulang otp'
					WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_KirimOTPLagi'))
	
					'verify popup message maximal resend OTP'
					if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'), GlobalVariable.TimeOut,
						FailureHandling.OPTIONAL)) {
						reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'))
	
						'write to excel status failed dan reason'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')
	
						'click button tutup error'
						WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_OKPopUp'))
	
						GlobalVariable.FlagFailed = 1
	
						break
					} else {
						'get data reset request OTP dari DB'
						Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
								GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())
	
						'verify counter OTP Katalon sesuai dengan counter OTP DB'
						checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
					}
					
					'delay untuk menunggu OTP'
					WebUI.delay(30)
	
					'+1 karena request otp'
					GlobalVariable.Counter++
	
					'check if OTP resend berhasil'
					checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
	
					'select OTP'
					WebUI.sendKeys(findTestObject('RegisterEsign/FormAktivasiDIGI/input_OTP'), Keys.chord(Keys.CONTROL, 'A'))
	
					'input OTP'
					WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_OTP'), OTP)
				}
			}
		} else {
			'input OTP'
			WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('Wrong OTP - Aktivasi')))
	
			'get count untuk resend OTP dari excel'
			countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP - Aktivasi')))
	
			'jika count resend OTP excel > 0'
			if (countResend > 0) {
				'count mulai dari 2 karena pertama kali ngeklik resend OTP sudah terhitung kedua kalinya request untuk resend OTP'
				OTPResendCount = 2
	
				for (int i = 0; i < countResend; i++) {
					'tunggu button resend otp'
					WebUI.delay(315)
	
					'klik pada button kirim ulang otp'
					WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_KirimOTPLagi'))
					
					'verify popup message maximal resend OTP'
					if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'), GlobalVariable.TimeOut,
						FailureHandling.OPTIONAL)) {
						reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'))
	
						'write to excel status failed dan reason'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + reason + '>')
	
						'click button tutup error'
						WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_OKPopUp'))
	
						GlobalVariable.FlagFailed = 1
	
						break
					} else {
						'get data reset request OTP dari DB'
						Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
								GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())
	
						'verify counter OTP Katalon sesuai dengan counter OTP DB'
						checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
					}
					
					'input OTP'
					WebUI.setText(findTestObject('RegisterEsign/FormAktivasiDIGI/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(
							GlobalVariable.NumofColm, rowExcel('Wrong OTP - Aktivasi')))
				}
			}
		}
		
		'click button proses OTP'
		WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_ProsesOTP'))

		'check if aktivasi berhasil dengan OTP yang benar'
		if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			if (GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
						0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			}
		} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiEsign/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
				';') + GlobalVariable.ReasonFailedOTPError)

			GlobalVariable.FlagFailed = 1
			
			inputed = 1
		} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			reason = WebUI.getText(findTestObject('RegisterEsign/FormAktivasiDIGI/label_PopUp'))

			'write to excel status failed dan reason'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
					'-', '') + ';') + '<' + reason + '>')
			
			'click button tutup error'
			WebUI.click(findTestObject('RegisterEsign/FormAktivasiDIGI/button_OKPopUp'))

//			'click button X tutup popup OTP'
//			WebUI.click(findTestObject('RegisterEsign/FormAktivasiEsign/button_X'))

			GlobalVariable.FlagFailed = 1
			
			inputed = 1
		}
	}
	
	return inputed
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}