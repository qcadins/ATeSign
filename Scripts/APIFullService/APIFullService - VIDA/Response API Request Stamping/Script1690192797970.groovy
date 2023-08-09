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
int countColmExcel = findTestData(excelPathAPIRequestStamping).columnNumbers

'looping API request stamping'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGetTotalUnsignedDocuments, GlobalVariable.NumofColm, 17)
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 15) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 16)
		} else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 14)
        }
        
        'HIT API check Status stamping'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Request Stamping', [('callerId') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, 9), ('refNumber') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                message = WS.getElementPropertyValue(respon, 'checkStampingStatus.message', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
					'get totalMaterai from db'
					String totalMaterai = CustomKeywords.'connection.APIFullService.getTotalMaterai'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, 11).replace('"',''))
					
					'looping untuk delay sebanyak total materai yang ada pada document'
					for (i = 0 ; i <= Integer.parseInt(totalMaterai) ; i++) {
						'delay untuk menunggu proses transaksi selesai sebanyak total materai yang ada'
						WebUI.delay(10)
					}
					
                    'get trx from db'
                    String result = CustomKeywords.'connection.APIFullService.getAPIRequestStampingTrx'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, 11).replace('"',''), totalMaterai)

                    'declare arraylist arraymatch'
                    arrayMatch = []
					
                    'verify saldo terpotong'
                    arrayMatch.add(WebUI.verifyMatch(result, '-'+totalMaterai, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Request Stamping', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Request Stamping', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Request Stamping', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Request Stamping', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

