import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIGetTotalUnsignedDocuments).columnNumbers

'looping API get Total Unsigned Documents'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase(
        'Unexecuted')) {
	
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGetTotalUnsignedDocuments, GlobalVariable.NumofColm, 17)
	
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 15) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 
                16)
        } else if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 
                14)
        }
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Get Total Unsigned Documents', [('callerId') : findTestData(
                        excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 9), ('email') : findTestData(
                        excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (code == 0) {
                'mengambil response totalUnsignedDocuments'
                totalUnsignedDocuments = WS.getElementPropertyValue(respon, 'totalUnsignedDocuments', FailureHandling.OPTIONAL)

                'input di excel mengenai totalUnsignedDocuments yang telah didapat'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Get Total Unsigned Document', 
                    5, GlobalVariable.NumofColm - 1, totalUnsignedDocuments.toString())

                'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'get data from db'
                    result = CustomKeywords.'connection.APIFullService.getTotalUnsignedDocuments'(conneSign, GlobalVariable.Tenant, 
                        findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 11).replace(
                            '"', ''))
					
                    'verify total Unsigned Documents'
                    if (WebUI.verifyEqual(result, findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(GlobalVariable.NumofColm, 
                            6), FailureHandling.CONTINUE_ON_FAILURE)) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Get Total Unsigned Document', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    } else {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Get Total Unsigned Document', 
                            GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (((((findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(
                                GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' dimana Value DB yaitu ') + 
                            '<' + (result)) + '> dan Value Excel yaitu ') + findTestData(excelPathAPIGetTotalUnsignedDocuments).getValue(
                                GlobalVariable.NumofColm, 6))
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Get Total Unsigned Document', 
                    GlobalVariable.NumofColm, GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Get Total Unsigned Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

