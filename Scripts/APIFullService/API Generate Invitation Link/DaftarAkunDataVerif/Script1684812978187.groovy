import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'declare userDir'
String userDir = System.getProperty('user.dir')

WebUI.openBrowser('')

WebUI.maximizeWindow()

'check if ingin menggunakan local host atau tidak'
if (GlobalVariable.useLocalHost == 'Yes') {
    'navigate url ke daftar akun'
    WebUI.navigateToUrl(GlobalVariable.Link.replace('https://gdkwebsvr:8080', GlobalVariable.urlLocalHost))
} else if (GlobalVariable.useLocalHost == 'No') {
    'navigate url ke daftar akun'
    WebUI.navigateToUrl(GlobalVariable.Link)
}

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'verify Email sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Email'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 17).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' NIK')

'verify Nama Lengkap sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 11).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir')

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 14).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 16).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' No Handphone')

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 18).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Alamat')

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi')

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kota')

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 19).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan')

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan')

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 23).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' KodePos')

'check mau foto selfie atau tidak'
if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 39) == 'Yes') {
    'click ambil foto sendiri'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))

    'delay untuk camera on'
    WebUI.delay(2)

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
}

if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 25) == '""') {
    'check mau foto KTP atau tidak'
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 40) == 'Yes') {
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 41).length() == 0) {
            'click ambil foto KTP'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

            'delay untuk camera on'
            WebUI.delay(2)

            'click ambil foto'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

            'click ambil apply'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 41).length() > 0) {
            'get file path'
            String filePath = userDir + findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 30)

            'upload file'
            CustomKeywords.'customizekeyword.UploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
                filePath)

            'click ambil apply'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
        }
    }
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 42).equalsIgnoreCase('Yes')) {
    'click checkbox'
    WebUI.click(findTestObject('DaftarAkun/checkbox_SyaratdanKetentuan'))

    'click checkbox setuju'
    WebUI.click(findTestObject('DaftarAkun/checkbox_Setuju'))
}

'click daftar akun'
WebUI.click(findTestObject('DaftarAkun/button_DaftarAkun'))

'cek if muncul popup alert'
if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('DaftarAkun/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2).replace(
            '-', '') + ';') + ReasonFailed)

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))

    GlobalVariable.FlagFailed = 1
} else {
    ArrayList<String> listOTP = []

    'get OTP dari DB'
    String OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            12).replace('"', '').toUpperCase())

    'add OTP ke list'
    listOTP.add(OTP)

    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 43).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)

        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 45))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), , ' OTP')

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)
            }
        }
    } else {
        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                44))

        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 45))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        44))
            }
        }
    }
    
    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))

    WebUI.delay(3)

    'cek if berhasil pindah page'
    if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
		
		checkTrxMutation(conneSign)
	
        'call testcase form aktivasi vida'
        WebUI.callTestCase(findTestCase('APIFullService/API Generate Invitation Link/FormAktivasiVida'), [('excelPathAPIGenerateInvLink') : 'APIFullService/API_GenInvLink'], 
			FailureHandling.CONTINUE_ON_FAILURE)
    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get reason error log'
        reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).toString()
		
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + reason)

        GlobalVariable.FlagFailed = 1
		
		checkTrxMutation(conneSign)
		
    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        reason = WebUI.getText(findTestObject('DaftarAkun/label_PopupMsg'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + reason)

        'click button tutup error'
        WebUI.click(findTestObject('DaftarAkun/button_OK'))

        'click button X tutup popup otp'
        WebUI.click(findTestObject('DaftarAkun/button_X'))

        GlobalVariable.FlagFailed = 1
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

def checkTrxMutation(Connection conneSign) {
	if ((GlobalVariable.checkStoreDB == 'Yes')) {
		resultTrx = CustomKeywords.'connection.DataVerif.getAPIGenInvLinkVerifTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, 11).replace('"',''))

		'declare arraylist arraymatch'
		ArrayList<String> arrayMatch = []

		'verify trx qty = -1'
		arrayMatch.add(WebUI.verifyMatch(resultTrx[0], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

		'jika data db tidak sesuai dengan excel'
		if (arrayMatch.contains(false)) {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link',
				GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
		}
	}
}