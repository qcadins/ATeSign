import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String selfPhoto, idPhoto

GlobalVariable.VerificationCount = 1

GlobalVariable.Counter = 0

'declare variable array'
ArrayList<String> saldoBefore, saldoAfter

int countCheckSaldo = 0

GlobalVariable.FlagFailed = 0

'check if self photo mau menggunakan base64 yang salah atau benar'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
'Yes') {
    selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
'No') {
    selfPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
}

'check if id photo mau menggunakan base64 yang salah atau benar'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
'Yes') {
    idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
'No') {
    idPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
}

'HIT API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Nama')), ('email') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Email')), ('tmpLahir') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Tempat Lahir')), ('tglLahir') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Lahir')), ('jenisKelamin') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')), ('tlp') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('No Telepon')), ('idKtp') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('NIK')), ('alamat') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Alamat')), ('kecamatan') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Kecamatan')), ('kelurahan') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Kelurahan')), ('kota') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Kota')), ('provinsi') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Provinsi')), ('kodePos') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Kode Pos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wilayah')), ('type') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Type')), ('office') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Office')), ('businessLine') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Lini Bisnis')), ('taskNo') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Task No')), ('callerId') : findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))

'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'get status code'
    code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    if (code == 0) {
        'mengambil response'
        GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

        if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
            'call test case ResponseAPIStoreDB'
            WebUI.callTestCase(findTestCase('Main Register/APIGenInvLinkStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
    } else {
        'call function get API error message'
        getAPIErrorMessage(respon)
    }
} else {
    'call function get API error message'
    getAPIErrorMessage(respon)
}

def getAPIErrorMessage(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
	
	GlobalVariable.FlagFailed = 1
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

