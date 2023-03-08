import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
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

'click menu buat undangan'
WebUI.click(findTestObject('BuatUndangan/menu_BuatUndangan'))

'input NIK'
WebUI.setText(findTestObject('BuatUndangan/input_NIK'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        9))

'input nama lengkap'
WebUI.setText(findTestObject('BuatUndangan/input_NamaLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        10))

'input tempat lahir'
WebUI.setText(findTestObject('BuatUndangan/input_TempatLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        11))

'input tanggal lahir'
WebUI.setText(findTestObject('BuatUndangan/input_TanggalLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        12))

'cek if pria / wanita'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('Pria')) {
    'click radio pria'
    WebUI.click(findTestObject('BuatUndangan/radio_Pria'))
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('Wanita')) {
    'click radio wanita'
    WebUI.click(findTestObject('BuatUndangan/radio_Wanita'))
}

'input no handphone'
WebUI.setText(findTestObject('BuatUndangan/input_noHandphone'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        14))

'input email'
WebUI.setText(findTestObject('BuatUndangan/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        15))

'input alamat lengkap'
WebUI.setText(findTestObject('BuatUndangan/input_AlamatLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        17))

'input provinsi'
WebUI.setText(findTestObject('BuatUndangan/input_Provinsi'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        18))

'input kota'
WebUI.setText(findTestObject('BuatUndangan/input_Kota'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        19))

'input kecamatan'
WebUI.setText(findTestObject('BuatUndangan/input_Kecamatan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        20))

'input kelurahan'
WebUI.setText(findTestObject('BuatUndangan/input_Kelurahan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        21))

'input kode pos'
WebUI.setText(findTestObject('BuatUndangan/input_KodePos'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        22))

'input wilayah'
WebUI.setText(findTestObject('BuatUndangan/input_Wilayah'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        23))

'input office'
WebUI.setText(findTestObject('BuatUndangan/input_Office'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        25))

'input lini bisnis'
WebUI.setText(findTestObject('BuatUndangan/input_LiniBisnis'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        26))

'input task no'
WebUI.setText(findTestObject('BuatUndangan/input_TaskNo'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        27))

'click button save'
WebUI.click(findTestObject('BuatUndangan/button_Save'))

'click button ya proses'
WebUI.click(findTestObject('BuatUndangan/button_YaProses'))

'cek if muncul popup'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('BuatUndangan/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    'click button tutup error'
    WebUI.click(findTestObject('BuatUndangan/button_TutupError'))

    'click button cancel'
    WebUI.click(findTestObject('BuatUndangan/button_Cancel'))

    'click button ya batal undangan'
    WebUI.click(findTestObject('BuatUndangan/button_YaBatalUndangan'))
} else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    'click button cancel'
    WebUI.click(findTestObject('BuatUndangan/button_Cancel'))

    'click button ya batal undangan'
    WebUI.click(findTestObject('BuatUndangan/button_YaBatalUndangan'))
} else {
    'write to excel success'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm - 
        1, GlobalVariable.StatusSuccess)
}

'get link'
GlobalVariable.Link = WebUI.getAttribute(findTestObject('BuatUndangan/PopUp/input_Link'), 'value')

WebUI.callTestCase(findTestCase('BuatUndangan/DaftarAkunDataVerif'), [:], FailureHandling.STOP_ON_FAILURE)

