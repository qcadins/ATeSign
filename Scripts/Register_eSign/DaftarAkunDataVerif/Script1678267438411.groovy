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

'navigate url ke daftar akun'
WebUI.navigateToUrl(GlobalVariable.Link)

'verify NIK sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NIK'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 9).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nama Lengkao sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NamaLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tempat lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TempatLahir'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'parse Date from MM/dd/yyyy > yyyy-MM-dd'
sdf = new SimpleDateFormat('MM/dd/yyyy')

parsedDate = null

sentDate = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 12)

parsedDate = sdf.parse(sentDate)

sdf = new SimpleDateFormat('yyyy-MM-dd')

sDate = sdf.format(parsedDate)

'verify tanggal lahir sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_TanggalLahir'), 'value').toUpperCase(), 
        sDate.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify No Handphone sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_NoHandphone'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Email sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Email'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify alamat sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_AlamatLengkap'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Provinsi'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kota'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Kecamatan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kecamatan'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Kelurahan sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_Kelurahan'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify KodePos sesuai inputan'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DaftarAkun/input_KodePos'), 'value').toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 22).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'click ambil foto sendiri'
WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoSendiri'))

'click ambil foto'
WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

'click ambil apply'
WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 30).length() == 0) {
    'click ambil foto KTP'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFotoKTP'))

    'click ambil foto'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_AmbilFoto'))

    'click ambil apply'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Apply'))
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 30).length() > 0) {
    'upload file'
    CustomKeywords.'customizeKeyword.uploadFile.uploadFunction'(findTestObject('Object Repository/DaftarAkun/button_PilihFileKTP'), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 30))
}

'cek centang syarat dan ketentuan'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 29).equalsIgnoreCase('Yes')) {
    'click checkbox'
    WebUI.click(findTestObject('DaftarAkun/checkbox_SyaratdanKetentuan'))
}

'click daftar akun'
WebUI.click(findTestObject('DaftarAkun/button_DaftarAkun'))

'cek if muncul popup'
if (WebUI.verifyElementPresent(findTestObject('DaftarAkun/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('DaftarAkun/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    'click button tutup error'
    WebUI.click(findTestObject('DaftarAkun/button_TutupError'))
} else {
    'connect DB eSign UAT'
    Connection conneSignUAT = CustomKeywords.'connection.connectDB.connectDBeSignUAT'()
	
	'delay untuk menunggu OTP'
	WebUI.delay(10)

    'get OTP dari DB'
    String OTP = CustomKeywords.'connection.dataVerif.getOTP'(conneSignUAT, findTestData(excelPathBuatUndangan).getValue(
            GlobalVariable.NumofColm, 15).toUpperCase())

    'input OTP'
    WebUI.setText(findTestObject('DaftarAkun/input_OTP'), OTP)

    'click verifikasi'
    WebUI.click(findTestObject('Object Repository/DaftarAkun/button_Verifikasi'))
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

