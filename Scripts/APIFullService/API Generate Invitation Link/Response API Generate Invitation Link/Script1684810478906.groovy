import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIGenerateInvLink).columnNumbers

String selfPhoto, idPhoto

'looping API Generate Invitation Link'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 34) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 35)
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 34) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 32) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.DataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 32) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 33)
        }
        
		'check if self photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 36) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, 24))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 36) == 'No') {
            selfPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 24)
        }
        
		'check if id photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 37) == 'Yes') {
            idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, 25))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 37) == 'No') {
            idPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 25)
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 11), ('email') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 12), ('tmpLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 13), ('tglLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 14), ('jenisKelamin') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 15), ('tlp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        16), ('idKtp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 17)
                    , ('alamat') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 18), ('kecamatan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 19), ('kelurahan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 20), ('kota') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 21), ('provinsi') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 22), ('kodePos') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 23), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 26), ('type') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 27), ('office') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        28), ('businessLine') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        29), ('taskNo') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 30)
                    , ('callerId') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 9)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('APIFullService/API Generate Invitation Link/APIGenInvLinkStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'], 
						FailureHandling.CONTINUE_ON_FAILURE)
                }
                
                'call test case daftar akun verif'
                WebUI.callTestCase(findTestCase('APIFullService/API Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathGenerateLink') : 'APIFullService/API_GenInvLink'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

