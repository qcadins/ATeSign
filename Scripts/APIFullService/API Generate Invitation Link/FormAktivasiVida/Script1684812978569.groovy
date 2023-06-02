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

ArrayList<String> listOTP = []

int reqOTP

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            12).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'check nama lengkap sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_NamaLengkap'), 
            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            11).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi (2x karena klik 1x hanya menghilangkan warning saja)'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi')

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 
            'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi')

'input kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), findTestData(excelPathAPIGenerateInvLink).getValue(
        GlobalVariable.NumofColm, 47))

'click button mata kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataKataSandi'))

'get text kata sandi'
KataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 'value')

'check kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            47), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kata Sandi')

'input ulang kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), findTestData(excelPathAPIGenerateInvLink).getValue(
        GlobalVariable.NumofColm, 48))

'click button mata ulang kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataUlangKataSandi'))

'get text ulang kata sandi'
UlangKataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 'value')

'check ulang kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            48), false, FailureHandling.CONTINUE_ON_FAILURE), ' Ulang Kata Sandi')

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/alertText'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/alertText'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2).replace(
            '-', '') + ';') + reason)

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementClickable(findTestObject('BuatUndangan/FormAktivasi/button_Proses'), FailureHandling.OPTIONAL)) {
    'click button proses'
    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_Proses'))

	'verify popup message maximal resend OTP'
	if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut,
		FailureHandling.OPTIONAL)) {
		reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))

		'write to excel status failed dan reason'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
				2).replace('-', '') + ';') + reason + ' Form Aktivasi Vida')

		'click button tutup error'
		WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))

		GlobalVariable.FlagFailed = 1
	} else {	
	    'delay untuk menunggu OTP'
	    WebUI.delay(5)
	
	    'get OTP dari DB'
	    OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
	            12).replace('"', '').toUpperCase())
	
		'+1 karena request otp'
		reqOTP++
		
	    'clear arraylist sebelumnya'
	    listOTP.clear()
	
	    'add otp ke list'
	    listOTP.add(OTP)
	
	    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 49).equalsIgnoreCase('Yes')) {
	        'delay untuk menunggu OTP'
	        WebUI.delay(5)
	
	        'input OTP'
	        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), OTP)
	
	        'get count untuk resend OTP dari excel'
	        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 51))
	
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
	                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
	                        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
	                            2).replace('-', '') + ';') + reason)
	
	                    'click button tutup error'
	                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
	
	                    GlobalVariable.FlagFailed = 1
	
	                    break
	                } else {
	                    'get data reset request OTP dari DB'
	                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
	                            GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())
	
	                    'verify counter OTP Katalon sesuai dengan counter OTP DB'
	                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
	                }
	                
	                'delay untuk menunggu OTP'
	                WebUI.delay(3)
	
	                'get OTP dari DB'
	                OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
	                        GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())
	
					'+1 karena request otp'
					reqOTP++
					
	                'add OTP ke list'
	                listOTP.add(OTP)
	
	                'check if OTP resend berhasil'
	                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
	
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
	            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Generate Invitation Link', 
	                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	        }
	    } else {
	        'input OTP'
	        WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(
	                GlobalVariable.NumofColm, 50))
	
	        'get count untuk resend OTP dari excel'
	        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 51))
	
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
	                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
	                        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
	                            2).replace('-', '') + ';') + reason)
	
	                    'click button tutup error'
	                    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
	
	                    GlobalVariable.FlagFailed = 1
	
	                    break
	                } else {
	                    'get data reset request OTP dari DB'
	                    Integer resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
	                            GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())
	
	                    'verify counter OTP Katalon sesuai dengan counter OTP DB'
	                    checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
	                }
	                
	                'delay untuk menunggu OTP'
	                WebUI.delay(5)
	
	                'get OTP dari DB'
	                OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
	                        GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())
	
					'+1 karena request otp'
					reqOTP++
					
	                'add OTP ke list'
	                listOTP.add(OTP)
	
	                'check if OTP resend berhasil'
	                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')
	
	                'input OTP'
	                WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(
	                        GlobalVariable.NumofColm, 50))
	            }
	        }
	        
	        'click button proses OTP'
	        WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_ProsesOTP'))
	
	        'check if aktivasi berhasil dengan OTP yang Salah'
	        if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut, 
	            FailureHandling.OPTIONAL)) {
	            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
	            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
	                GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2) + 
	                ';') + GlobalVariable.ReasonFailedOTPError)
	
	            GlobalVariable.FlagFailed = 1
	        } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, 
	            FailureHandling.OPTIONAL)) {
	            reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))
	
	            'write to excel status failed dan reason'
	            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
	                GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2).replace(
	                    '-', '') + ';') + reason)
	
	            'click button tutup error'
	            WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
	
	            'click button X tutup popup OTP'
	            WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_X'))
	
	            GlobalVariable.FlagFailed = 1
	        }
	    }
		
		if ((GlobalVariable.checkStoreDB == 'Yes')) {
			resultTrx = CustomKeywords.'connection.DataVerif.getAPIGenInvLinkOTPTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, 11).replace('"',''))
	
			'declare arraylist arraymatch'
			ArrayList<String> arrayMatch = []
	
			int sum = 0
			for (String value : resultTrx) {
			    int intValue = Integer.parseInt(value)
			    sum += intValue
			}
			
			'verify trx qty = -1'
			arrayMatch.add(WebUI.verifyEqual(sum, -reqOTP, FailureHandling.CONTINUE_ON_FAILURE))
	
			'jika data db tidak sesuai dengan excel'
			if (arrayMatch.contains(false)) {
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link',
					GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
			}
		}
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}