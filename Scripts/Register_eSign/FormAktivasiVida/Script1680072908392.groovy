import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

ArrayList<String> listOTP = new ArrayList<String>()

'check email sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_Email'), 'value', 
            FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'check nama lengkap sesuai dengan inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_NamaLengkap'), 
            'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'input kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), '@Abcd1234')

'input ulang kata sandi untuk verify button set ulang'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), '@Abcd1234')

'click button set ulang untuk reset field kata sandi dan ulang kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_SetUlang'))

'verify kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 'value')
	, '', false, FailureHandling.CONTINUE_ON_FAILURE))

'verify ulang kata sandi sudah kosong'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 'value')
	, '', false, FailureHandling.CONTINUE_ON_FAILURE))

'input kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, 35))

'click button mata kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataKataSandi'))

'get text kata sandi'
KataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), 'value')

'check kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(KataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			35), false, FailureHandling.CONTINUE_ON_FAILURE))

'input ulang kata sandi'
WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, 36))

'click button mata ulang kata sandi'
WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_MataUlangKataSandi'))

'get text ulang kata sandi'
UlangKataSandi = WebUI.getAttribute(findTestObject('BuatUndangan/FormAktivasi/input_UlangKataSandi'), 'value')

'check ulang kata sandi sesuai inputan excel'
checkVerifyEqualOrMatch(WebUI.verifyMatch(UlangKataSandi, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			36), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify warning password'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/alertText'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get text reason'
    reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/alertText'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + reason)
	
	GlobalVariable.FlagFailed = 1
	
} else if (WebUI.verifyElementClickable(findTestObject('BuatUndangan/FormAktivasi/button_Proses'), FailureHandling.OPTIONAL)) {
    'click button proses'
    WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_Proses'))
	
	'delay untuk menunggu OTP'
	WebUI.delay(5)
	
	'get OTP dari DB'
	OTP = CustomKeywords.'connection.dataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			15).toUpperCase())
	
	'clear arraylist sebelumnya'
	listOTP.clear()
	
	'add otp ke list'
	listOTP.add(OTP)
	
	if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 37).equalsIgnoreCase('Yes')) {
		'delay untuk menunggu OTP'
		WebUI.delay(5)
	
		'input OTP'
		WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), OTP)
	
		'get count untuk resend OTP dari excel'
		countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 39))
	
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
				if(WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))
							
					'write to excel status failed dan reason'
					CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
									'-', '') + ';') + reason)
							
					'click button tutup error'
					WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
							
					GlobalVariable.FlagFailed = 1
					
					break
				}else {
					'get data reset request OTP dari DB'
					Integer resultResetOTP = CustomKeywords.'connection.dataVerif.getResetOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
							GlobalVariable.NumofColm, 15).toUpperCase())
					
					'verify counter OTP Katalon sesuai dengan counter OTP DB'
					checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE))
				}
				
				'delay untuk menunggu OTP'
				WebUI.delay(3)
	
				'get OTP dari DB'
				OTP = CustomKeywords.'connection.dataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(
						GlobalVariable.NumofColm, 15).toUpperCase())
	
				'add OTP ke list'
				listOTP.add(OTP)
	
				'check if OTP resend berhasil'
				checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE))
	
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
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
		}
		
	} else {
		'input OTP'
		WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
				38))
	
		'get count untuk resend OTP dari excel'
		countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 39))
	
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
				if(WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))
							
					'write to excel status failed dan reason'
					CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
									'-', '') + ';') + reason)
							
					'click button tutup error'
					WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
							
					GlobalVariable.FlagFailed = 1
					
					break
				}else {
					'get data reset request OTP dari DB'
					Integer resultResetOTP = CustomKeywords.'connection.dataVerif.getResetOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
							GlobalVariable.NumofColm, 15).toUpperCase())
					
					'verify counter OTP Katalon sesuai dengan counter OTP DB'
					checkVerifyEqualOrMatch(WebUI.verifyEqual(resultResetOTP, OTPResendCount++, FailureHandling.CONTINUE_ON_FAILURE))
				}
				
				'delay untuk menunggu OTP'
				WebUI.delay(5)
	
				'get OTP dari DB'
				OTP = CustomKeywords.'connection.dataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathBuatUndangan).getValue(
						GlobalVariable.NumofColm, 15).toUpperCase())
	
				'add OTP ke list'
				listOTP.add(OTP)
	
				'check if OTP resend berhasil'
				checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE))
	
				'input OTP'
				WebUI.setText(findTestObject('BuatUndangan/FormAktivasi/input_OTP'), findTestData(excelPathBuatUndangan).getValue(
						GlobalVariable.NumofColm, 38))
			}
		}
		
		'click button proses OTP'
		WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_ProsesOTP'))
		
		'check if aktivasi berhasil dengan OTP yang Salah'
		if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/popUp_AktivasiBerhasil'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') +
				GlobalVariable.ReasonFailedOTPError)
			
			GlobalVariable.FlagFailed = 1
		}else if(WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			reason = WebUI.getText(findTestObject('BuatUndangan/FormAktivasi/label_PopupMsg'))
					
			'write to excel status failed dan reason'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
							'-', '') + ';') + reason)
					
			'click button tutup error'
			WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_OK'))
					
			'click button X tutup popup OTP'
			WebUI.click(findTestObject('BuatUndangan/FormAktivasi/button_X'))
					
			GlobalVariable.FlagFailed = 1
		}
	}
}


def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

