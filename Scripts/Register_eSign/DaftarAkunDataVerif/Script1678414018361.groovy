import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys as Keys

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

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 9).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' NIK')

'verify Nama Lengkap sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Nama Lengkap')

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Tempat Lahir')

'parse Date from MM/dd/yyyy > yyyy-MM-dd'
sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        12), 'MM/dd/yyyy', 'yyyy-MM-dd')

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        sDate.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' No Handphone')

'verify Email sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Email'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Email')

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Alamat')

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Provinsi')

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Kota')

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Kecamatan')

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Kelurahan')

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 22).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
    ' Kode Pos')

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 30) == 'Yes') {
    'click ambil foto sendiri'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))

    'delay untuk camera on'
    WebUI.delay(2)

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
}

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 31) == 'Yes') {
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 32).length() == 0) {
        'click ambil foto KTP'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

        'delay untuk camera on'
        WebUI.delay(2)

        'click ambil foto'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 32).length() > 0) {
        'get file path'
        String filePath = userDir + findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 32)

        'upload file'
        CustomKeywords.'customizekeyword.UploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
            filePath)

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    }
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 29).equalsIgnoreCase('Yes')) {
    'click checkbox syarat'
    WebUI.click(findTestObject('DaftarAkun/checkbox_SyaratdanKetentuan'))

    'click checkbox setuju'
    WebUI.click(findTestObject('DaftarAkun/checkbox_Setuju'))
	
	'click checkbox setuju'
	WebUI.click(findTestObject('DaftarAkun/checkbox_SetujuVIDA'))
}

'click daftar akun'
WebUI.click(findTestObject('DaftarAkun/button_DaftarAkun'))

'cek if muncul popup alert'
if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('DaftarAkun/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<' + ReasonFailed + '>')

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))

    GlobalVariable.FlagFailed = 1 //	if (WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains('berhasil')) {
    //	    } 
} else {
    ArrayList listOTP = []

    'get otp dari DB'
    String otp = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            15).toUpperCase())

    'add otp ke list'
    listOTP.add(otp)

    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 31).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu otp'
        WebUI.delay(5)

        'input otp'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), otp)

        countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 35))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu otp'
                WebUI.delay(5)

                'get otp dari DB'
                otp = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                        GlobalVariable.NumofColm, 15).toUpperCase())

                'add otp ke list'
                listOTP.add(otp)

                'check if otp resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' OTP')

                'input otp'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), otp)
            }
        }
    } else {
        'input otp'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                34))

        countResend = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 35))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu otp'
                WebUI.delay(5)

                'get otp dari DB'
                otp = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                        GlobalVariable.NumofColm, 15).toUpperCase())

                'add otp ke list'
                listOTP.add(otp)

                'check if otp resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' OTP')

                'input otp'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                        34))
            }
        }
    }
    
    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))

    'get reason error log'
    reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).toString()

	'check saldo OTP'
	checkSaldoOTP()
	
    'cek if berhasil pindah page'
    if ((reason.contains('gagal') || reason.contains('Saldo')) || reason.contains('Invalid')) {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + '<' + reason + '>')

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'call testcase form aktivasi vida'
        WebUI.callTestCase(findTestCase('APIFullService/APIFullService - VIDA/API Generate Invitation Link/FormAktivasiVida'), [('excelPathBuatUndangan') : 'APIFullService/API_GenInvLink'], 
            FailureHandling.CONTINUE_ON_FAILURE)
    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        reason = WebUI.getText(findTestObject('DaftarAkun/label_PopupMsg'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + '<' + reason + '>')

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkSaldoOTP() {
    'open new tab'
    WebUI.executeJavaScript('window.open();', [])
	
	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(1)
	
	'navigate to url esign'
    WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))

    'maximize window'
    WebUI.maximizeWindow()

   'set value userLogin'
	GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(2, 66).toUpperCase()

	'input email'
    WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathBuatUndangan).getValue(2, 66))

    'input password'
    WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathBuatUndangan).getValue(2, 
            67))

    'click button login'
    WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

    'input perusahaan'
    WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathBuatUndangan).getValue(2, 
            68))

    WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

    'input peran'
    WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathBuatUndangan).getValue(2, 
            69))

    WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)

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
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty).replace(',', ''), saldoBefore, false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

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
	checkVerifyEqualOrMatch(WebUI.verifyNotMatch(WebUI.getText(modifyObjectUser), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(0)
	
	'close tab saldo'
	WebUI.closeWindowIndex(1)
	
	'swicth tab ke new tab'
	WebUI.switchToWindowIndex(0)
}