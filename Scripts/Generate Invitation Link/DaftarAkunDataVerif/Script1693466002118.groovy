import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

'declare userDir'
String userDir = System.getProperty('user.dir')

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
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' NIK')

'verify Nama Lengkap sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir')

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' No Handphone')

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('alamat')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Alamat')

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi')

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kota')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kota')

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan')

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan')

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kodePos')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' KodePos')

'check mau foto selfie atau tidak'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Foto Selfie')) == 'Yes') {
    'click ambil foto sendiri'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))
	
    'delay untuk camera on'
    WebUI.delay(5)

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
}

'check mau foto KTP atau tidak'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Foto KTP')) == 'Yes') {
    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP')).length() == 0) {
        'click ambil foto KTP'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

        'delay untuk camera on'
        WebUI.delay(5)

        'click ambil foto'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP')).length() > 0) {
        'get file path'
        String filePath = userDir + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP'))

        'upload file'
        CustomKeywords.'customizekeyword.UploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
            filePath)

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    }
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Syarat dan Kententuan')).equalsIgnoreCase('Yes')) {
    'click checkbox'
    WebUI.click(findTestObject('DaftarAkun/checkbox_SyaratdanKetentuan'))

	if(GlobalVariable.Psre == 'VIDA') {
	    'click checkbox setuju'
	    WebUI.click(findTestObject('DaftarAkun/checkbox_Setuju'))
		
		'click checkbox setuju'
		WebUI.click(findTestObject('DaftarAkun/checkbox_SetujuVIDA'))
	}
}

'click daftar akun'
WebUI.click(findTestObject('DaftarAkun/button_DaftarAkun'))

'cek if muncul popup alert'
if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('DaftarAkun/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + '<' + ReasonFailed + '>')

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))

    GlobalVariable.FlagFailed = 1
} else {
    ArrayList<String> listOTP = []

    'get OTP dari DB'
    String OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('email')).replace('"', '').toUpperCase())

	println(OTP)
	
    'add OTP ke list'
    listOTP.add(OTP)

    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input Correct OTP')).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)

        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(315)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())

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
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                rowExcel('Wrong OTP')))

        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(315)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('Wrong OTP')))
            }
        }
    }
    
    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))

	if(GlobalVariable.Psre == 'VIDA') {		
	    'get reason error log'
	    reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).toString()
	
	    'cek if berhasil pindah page'
	    if (reason.contains('gagal') || reason.contains('Saldo') || reason.contains('Invalid')) {	
	        'write to excel status failed dan reason'
	        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
	            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
	                '-', '') + ';') + '<' + reason + '> Daftar Akun Esign')
	
	        GlobalVariable.FlagFailed = 1
	    } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasiEsign/input_KataSandi'), GlobalVariable.TimeOut, 
	        FailureHandling.OPTIONAL)) {
	        'call testcase form aktivasi vida'
	        WebUI.callTestCase(findTestCase('Generate Invitation Link/FormAktivasiVida'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link',
				('sheet') : 'API Generate Inv Link'], FailureHandling.CONTINUE_ON_FAILURE)
			
			'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form aktivasi'
			while (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase('Continue')) {
				GlobalVariable.NumofColm++
				
				'call testcase form aktivasi vida'
				WebUI.callTestCase(findTestCase('Generate Invitation Link/FormAktivasiVida'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link',
					('sheet') : 'API Generate Inv Link'], FailureHandling.CONTINUE_ON_FAILURE)
			}
	    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	        reason = WebUI.getText(findTestObject('DaftarAkun/label_PopupMsg'))
	
	        'write to excel status failed dan reason'
	        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
	            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
	                '-', '') + ';') + '<' + reason + '>')
	
	        'click button tutup error'
	        WebUI.click(findTestObject('DaftarAkun/button_OK'))
	
	        'click button X tutup popup otp'
	        WebUI.click(findTestObject('DaftarAkun/button_X'))
	
	        GlobalVariable.FlagFailed = 1
	    }
	} else if(GlobalVariable.Psre == 'PRIVY') {
		if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_SuccessPrivy'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'get message dari ui'
			reason = WebUI.getText(findTestObject('DaftarAkun/label_SuccessPrivy'))
		
			'check if registrasi berhasil dan write ke excel'
			if (reason.equalsIgnoreCase('Proses verifikasi anda sedang diproses. Harap menunggu proses verifikasi selesai.') && GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			} else {
				'write to excel status failed dan reason'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
						'-', '') + ';') + '<' + reason + '>')
			}
		}
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}