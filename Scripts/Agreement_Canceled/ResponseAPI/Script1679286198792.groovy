import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'mengambil AES Key untuk encrypt'
String aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17)
        }	

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Agreement Canceled', [('documentId') : ('"' + 
						CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9), aesKey)) + '"', ('msg') : ('"' + 
						CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        10), aesKey)) + '"', ('callerId') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        12)) + '"', ('tenantCode') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        14)) + '"']))

        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (status_Code == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Agreement Canceled', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Agreement_Canceled/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Agreement_Canceled/Agreement Canceled'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            } else {
                messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Agreement Canceled', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Agreement Canceled', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
        }
    }
}

