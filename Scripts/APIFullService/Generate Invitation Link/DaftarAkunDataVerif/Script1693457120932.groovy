import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection

import org.openqa.selenium.By
import org.openqa.selenium.Keys

import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

'declare userDir'
String userDir = System.getProperty('user.dir')

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

'delay 3 detik'
WebUI.delay(3)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'verify Email sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Email'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' NIK')

'verify Nama Lengkap sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Nama Lengkap')

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir')

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' No Handphone')

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('alamat')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Alamat')

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi')

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kota')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kota')

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan')

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan')

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kodePos')).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE), ' KodePos')

'check mau foto selfie atau tidak'
if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Foto Selfie')) == 'Yes') {
    'click ambil foto sendiri'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))

	'check if run with mobile / web'
	if(GlobalVariable.RunWith == 'Mobile') {
		'tap allow camera'
		MobileBuiltInKeywords.tapAndHoldAtPosition(920, 1220, 3)
		
		'tap allow camera'
		MobileBuiltInKeywords.tapAndHoldAtPosition(550, 1820, 3)
	}
	
    'delay untuk camera on'
    WebUI.delay(5)

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
}

if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto')) == '""') {
    'check mau foto KTP atau tidak'
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Foto KTP')) == 'Yes') {
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP')).length() == 0) {
            'click ambil foto KTP'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

            'delay untuk camera on'
            WebUI.delay(5)

            'click ambil foto'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

            'click ambil apply'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP')).length() > 0) {
            'get file path'
            String filePath = userDir + findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Upload Foto KTP'))

            'upload file'
            CustomKeywords.'customizekeyword.UploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
                filePath)

            'click ambil apply'
            WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
        }
    }
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Syarat dan Kententuan')).equalsIgnoreCase('Yes')) {
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
        GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + '<' + ReasonFailed + '>')

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))

    GlobalVariable.FlagFailed = 1
} else {
    ArrayList<String> listOTP = []

    'get OTP dari DB'
    String OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('email')).replace('"', '').toUpperCase())

	println(OTP)
	
    'add OTP ke list'
    listOTP.add(OTP)

    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Input Correct OTP')).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)

        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(315)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
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
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                rowExcel('Wrong OTP')))

        countResend = Integer.parseInt(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP')))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(315)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' OTP')

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('Wrong OTP')))
            }
        }
    }
    
    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))

	if(GlobalVariable.Psre == 'VIDA') {		
	    'get reason error log'
	    reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).toString()
	
		'check saldo OTP'
		checkSaldoOTP()
		
	    'cek if berhasil pindah page'
	    if (reason.contains('gagal') || reason.contains('Saldo') || reason.contains('Invalid')) {	
	        'write to excel status failed dan reason'
	        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
	            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
	                '-', '') + ';') + '<' + reason + '> Daftar Akun Esign')
	
	        GlobalVariable.FlagFailed = 1
	
			'if gagal verifikasi wajah maka cek saldo verifikasi berkurang 1'		
			if (reason.contains('gagal')) {
				'call function checkTrxMutation'
				checkTrxMutation(conneSign)
			}
	    } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasiEsign/input_KataSandi'), GlobalVariable.TimeOut, 
	        FailureHandling.OPTIONAL)) {
			
			checkTrxMutation(conneSign)
		
	        'call testcase form aktivasi vida'
	        WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/FormAktivasi'), [('excelPathAPIGenerateInvLink') : 'APIFullService/API_GenInvLink'], 
				FailureHandling.CONTINUE_ON_FAILURE)
	    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	        reason = WebUI.getText(findTestObject('DaftarAkun/label_PopupMsg'))
	
	        'write to excel status failed dan reason'
	        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
	            GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
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
					GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
						'-', '') + ';') + '<' + reason + '>')
			}
			
			'check saldo OTP'
			checkSaldoOTP()
			
			'call function check mutation trx'
			checkTrxMutation(conneSign)
		}
	}
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

def checkTrxMutation(Connection conneSign) {
	if ((GlobalVariable.checkStoreDB == 'Yes')) {
		resultTrx = CustomKeywords.'connection.APIFullService.getAPIGenInvLinkVerifTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('email')).replace('"',''), findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('tlp')).replace('"',''))

		'declare arraylist arraymatch'
		ArrayList<String> arrayMatch = []

		'verify trx qty = -1'
		arrayMatch.add(WebUI.verifyMatch(resultTrx[0], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

		'jika data db tidak sesuai dengan excel'
		if (arrayMatch.contains(false)) {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet,
				GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Saldo Verif tidak terpotong')
		}
	}
}

def checkSaldoOTP() {
	'open new tab'
	WebUI.executeJavaScript('window.open();', [])
	
	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(1)
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'maximized window'
	WebUI.maximizeWindow()
	
	'store user login'	
	GlobalVariable.userLogin = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase()
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')))
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Password Login')))
	
	'click button login'
	WebUI.click(findTestObject('Login/button_Login'))
	
	if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
		'input perusahaan'
		WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan Login')))
		
		'enter untuk input perusahaan'
		WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
		
		'input peran'
		WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Peran Login')))
		
		'enter untuk input peran'
		WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
		
		'click button pilih peran'
		WebUI.click(findTestObject('Login/button_pilihPeran'))
	}

	'click menu saldo'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/menu_Saldo'))
	
	'click ddl bahasa'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_bahasa'))

	'click english'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_English'))

	'select vendor'
	WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + 'ESIGN/ADINS', true)
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	for (index = 2; index <= variable.size(); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo OTP'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('OTP')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'verify saldo tidak berkurang'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty).replace(',', ''), otpBefore, false, FailureHandling.CONTINUE_ON_FAILURE), ' SALDO OTP TIDAK SESUAI')

			break
		}
	}
	
	'input tipe saldo'
	WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), 'OTP')

	'enter untuk input tipe saldo'
	WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

	'input tipe transaksi'
	WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), 'Use OTP')

	'enter untuk input tipe saldo'
	WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), Keys.chord(Keys.ENTER))

	'click button cari'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_Cari'))

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	'modify object button last page'
	modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		variable.size()) + ']', true)

	if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
		'click button last page'
		WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_LastPage'))
	}
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'modify object user'
	modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

	'verify user name ui = excel'
	checkVerifyEqualOrMatch(WebUI.verifyNotMatch(WebUI.getText(modifyObjectUser), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"','').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(0)
	
	'close tab saldo'
	WebUI.closeWindowIndex(1)
	
	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(0)
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}