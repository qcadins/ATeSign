import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import java.text.SimpleDateFormat as SimpleDateFormat
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
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 19).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify Nama Lengkap sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 14).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 17).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 18).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 15).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify Email sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Email'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 25).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 23).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 24).replace('"', '').toUpperCase(), false, 
        FailureHandling.CONTINUE_ON_FAILURE))

'check mau foto selfie atau tidak'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 28) == 'Yes') {
    'click ambil foto sendiri'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))

    'delay untuk camera on'
    WebUI.delay(2)

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
}

'check mau foto KTP atau tidak'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 29) == 'Yes') {
    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 30).length() == 0) {
        'click ambil foto KTP'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

        'delay untuk camera on'
        WebUI.delay(2)

        'click ambil foto'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 30).length() > 0) {
		
		'get file path'
		String filePath = userDir + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 30)
		
        'upload file'
        CustomKeywords.'customizeKeyword.uploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
            filePath)

        'click ambil apply'
        WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
    }
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 27).equalsIgnoreCase('Yes')) {
    'click checkbox'
    WebUI.click(findTestObject('DaftarAkun/checkbox_SyaratdanKetentuan'))
}

'click daftar akun'
WebUI.click(findTestObject('DaftarAkun/button_DaftarAkun'))

'cek if muncul popup alert'
if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('DaftarAkun/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2).replace(
            '-', '') + ';') + ReasonFailed)

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))

    GlobalVariable.FlagFailed = 1
} else {
    ArrayList<String> listOTP = new ArrayList<String>()

    'get OTP dari DB'
    String OTP = CustomKeywords.'connection.dataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            13).replace('"', '').toUpperCase())

    'add OTP ke list'
    listOTP.add(OTP)

    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 31).equalsIgnoreCase('Yes')) {
        'delay untuk menunggu OTP'
        WebUI.delay(5)

        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)

        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 33))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.dataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE))

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)
            }
        }
    } else {
        'input OTP'
        WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                32))

        countResend = Integer.parseInt(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 33))

        if (countResend > 0) {
            for (int i = 0; i < countResend; i++) {
                'tunggu button resend otp'
                WebUI.delay(115)

                'klik pada button kirim ulang otp'
                WebUI.click(findTestObject('DaftarAkun/button_KirimKodeLagi'))

                'delay untuk menunggu OTP'
                WebUI.delay(5)

                'get OTP dari DB'
                OTP = CustomKeywords.'connection.dataVerif.getOTP'(conneSign, findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase())

                'add OTP ke list'
                listOTP.add(OTP)

                'check if OTP resend berhasil'
                checkVerifyEqualOrMatch(WebUI.verifyNotMatch(listOTP[i], listOTP[(i + 1)], false, FailureHandling.CONTINUE_ON_FAILURE))

                'input OTP'
                WebUI.setText(findTestObject('DaftarAkun/input_OTP'), findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                        32))
            }
        }
    }
    
    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))

    'cek if popup error msg'
    if (!(WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains('berhasil'))) {
        'get reason error log'
        reason = WebUI.getAttribute(findTestObject('DaftarAkun/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + reason)

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_PopupMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        reason = WebUI.getText(findTestObject('DaftarAkun/label_PopupMsg'))

        'write to excel status failed dan reason'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + reason)

        'click button tutup error'
        WebUI.click(findTestObject('DaftarAkun/button_OK'))

        'click button X tutup popup OTP'
        WebUI.click(findTestObject('DaftarAkun/button_X'))

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/FormAktivasi/input_KataSandi'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'call testcase form aktivasi vida'
        WebUI.callTestCase(findTestCase('Generate_Invitation_Link/FormAktivasiVida'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link'], 
			FailureHandling.CONTINUE_ON_FAILURE)
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}
