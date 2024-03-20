import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String selfPhoto, idPhoto

GlobalVariable.VerificationCount = 1

GlobalVariable.Counter = 0

GlobalVariable.FlagFailed = 0

'check if self photo mau menggunakan base64 yang salah atau benar'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
    selfPhoto = CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('selfPhoto')))
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
'No') {
    selfPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
}

'check if id photo mau menggunakan base64 yang salah atau benar'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'Yes') {
    idPhoto = CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('idPhoto')))
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
'No') {
    idPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
}

'HIT API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('$Nama')), ('email') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Email')), ('tmpLahir') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Tempat Lahir')), ('tglLahir') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Tanggal Lahir')), ('jenisKelamin') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Jenis Kelamin')), ('tlp') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('No Telepon')), ('idKtp') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$NIK')), ('alamat') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Alamat')), ('kecamatan') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kecamatan')), ('kelurahan') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kelurahan')), ('kota') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kota')), ('provinsi') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Provinsi')), ('kodePos') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kode Pos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Wilayah')), ('type') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Type')), ('office') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'OfficeName')), ('businessLine') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'BusinessLineName')), ('taskNo') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Ref No')), ('callerId') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'callerId'))]))

'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'get status code'
    code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'ambil lama waktu yang diperlukan hingga request menerima balikan'
    elapsedTime = (respon.elapsedTime / 1000) + ' second'

    'ambil body dari hasil respons'
    responseBody = respon.responseBodyContent

    'panggil keyword untuk proses beautify dari respon json yang didapat'
    CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Scenario')))

    'write to excel response elapsed time'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
        1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

    if (code == 0) {
        'mengambil response'
        GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

        'write to excel generated link'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Link Invitation') - 
            1, GlobalVariable.NumofColm - 1, GlobalVariable.Link)

        if (GlobalVariable.Link == '') {
            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                'Generate Link Null / Kosong')

            GlobalVariable.FlagFailed = 1
        }
        
        if (WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL) == 'User sudah terdaftar') {
            'cek ke excel bahwa data user sudah diregist otomatis ke tenant lain'
            result = CustomKeywords.'connection.Registrasi.checkAddUserOtherTenant'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))

            if (result == 0) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    'Error User di Tenant baru tidak berhasil ditambahkan')

                GlobalVariable.FlagFailed = 1
            } else if (result > 1) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    'Terdapat 2 user yang sama di tenant ') + GlobalVariable.Tenant)

                GlobalVariable.FlagFailed = 1
            } else {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
        }
        
        if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            'call test case ResponseAPIStoreDB'
            WebUI.callTestCase(findTestCase('Main Register/APIGenInvLinkStoreDB'), [('excelPathRegister') : excelPathRegister], 
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

def getAPIErrorMessage(ResponseObject respon) {
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
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

