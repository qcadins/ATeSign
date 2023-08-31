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
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIDownload, GlobalVariable.NumofColm, rowExcel('Use Correct Base URL'))
		
		'set psre sesuai inputan excel per case'
		GlobalVariable.Psre = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }	

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Agreement Canceled', [('documentId') : ('"' + CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentId')), aesKey)) + '"' , ('msg') : ('"' + 
						CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        rowExcel('$msg')), aesKey)) + '"', ('callerId') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        rowExcel('callerId'))) + '"', ('tenantCode') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        rowExcel('Tenant Code'))) + '"']))

        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (status_Code == 0) {
				message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)
				if (message == 'Success') {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Agreement_Canceled/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Agreement_Canceled/Agreement Canceled'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
				} else {
					'write to excel status failed dan reason : '
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
							'-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
				}
            } else {
                'call function get error message'
				getErrorMessage(respon)
            }
        } else {
			'call function get error message'
            getErrorMessage(respon)
        }
    }
}

def getErrorMessage(def respon) {
	messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)
	
	'write to excel status failed dan reason : '
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + '<' + messageFailed + '>')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}