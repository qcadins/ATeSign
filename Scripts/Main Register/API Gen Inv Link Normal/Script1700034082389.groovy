import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
ArrayList<String> listInvitation = []

'Declare variable untuk sendRequest'
(listInvitation[0]) = (((((((((((((((((((((((((('{"email" :' + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
    rowExcel('$Email'))) + ',"nama" :') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama'))) + 
',"tlp": ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No telepon'))) + ',"jenisKelamin" : ') + 
findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin'))) + ',"tmpLahir" : ') + findTestData(
    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tempat Lahir'))) + ',"tglLahir" : ') + findTestData(
    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Lahir'))) + ',"idKtp" : ') + findTestData(excelPathRegister).getValue(
    GlobalVariable.NumofColm, rowExcel('$NIK'))) + ', "provinsi" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
    rowExcel('Provinsi'))) + ', "kota" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        'Kota'))) + ', "kecamatan" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kecamatan'))) + 
',"kelurahan": ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kelurahan'))) + ',"kodePos" : ') + 
findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kode Pos'))) + ',"alamat" : ') + findTestData(
    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Alamat'))) + '}  ')

'HIT API'
respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId')), ('tenantCode') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('$Tenant Code')), ('users') : listInvitation[0]]))

'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'mengambil status code berdasarkan response HIT API'
    status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'jika status codenya 0'
    if (status_Code == 0) {
        'Mengambil links berdasarkan response HIT API'
        GlobalVariable.Link = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL).toString().replace('[', 
            '').replace(']', '')

        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Main Register/APIGenInvLinkStoreDB'), [('excelPathRegister') : 'Registrasi/MainRegister'
                        , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    } else {
        'call function get API error message'
        getAPIErrorMessage(respon)
    }
} else {
    'write to excel status failed dan reason : '
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + GlobalVariable.ReasonFailedHitAPI)
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

def getAPIErrorMessage(def respon) {
    'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason : '
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + messageFailed) + '>')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

