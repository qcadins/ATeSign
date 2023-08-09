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
int countColmExcel = findTestData(excelPathAPICheckStamping).columnNumbers

'looping API Status stamping'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPICheckStamping, GlobalVariable.NumofColm, 17)
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 15) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 16)
		} else if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 14)
        }
        
        'HIT API check Status stamping'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Check Stamping Status', [('callerId') : findTestData(
                        excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 9), ('refNumber') : findTestData(excelPathAPICheckStamping).getValue(
                        GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                docId = WS.getElementPropertyValue(respon, 'checkStampingStatus.documentId', FailureHandling.OPTIONAL)

                stampingstatus = WS.getElementPropertyValue(respon, 'checkStampingStatus.stampingStatus', FailureHandling.OPTIONAL)

                message = WS.getElementPropertyValue(respon, 'checkStampingStatus.messsage', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
				
                    arrayIndex = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.APIFullService.getAPICheckStampingStoreDB'(conneSign, 
                        findTestData(excelPathAPICheckStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    'declare arraylist arraymatch'
                    ArrayList<String> arrayMatch = new ArrayList<String>()

                    'looping sesuai size DB'
                    for (index = 0; index < (result.size() / 2); index++) {
                        'get docid DB'
                        docIdDB = (result[arrayIndex++])

                        'looping sesuai size response'
                        for (indexResponse = 0; indexResponse < docId.size(); indexResponse++) {
                            'check if docidDB == docid response'
                            if (docIdDB == (docId[indexResponse])) {
                                'verify doc id'
                                arrayMatch.add(WebUI.verifyMatch(docIdDB.toUpperCase(), (docId[indexResponse]).toUpperCase(), 
                                        false, FailureHandling.CONTINUE_ON_FAILURE))

	                            'verify stamping status'
	                            arrayMatch.add(WebUI.verifyEqual((result[arrayIndex++]), (stampingstatus[indexResponse]), 
										, FailureHandling.CONTINUE_ON_FAILURE))

                                'if stamping status = 2'
                                if ((stampingstatus[indexResponse]) == '2') {
                                    'verify error status'
                                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], message[indexResponse], false, 
                                            FailureHandling.CONTINUE_ON_FAILURE))
                                } else {
                                    'skip error status'
                                    arrayIndex++
                                }
                            } else {
                                continue
                                
                                arrayIndex--
                            }
                        }
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Stamping Status', 
                            GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPICheckStamping).getValue(
                                GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Stamping Status', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Stamping Status', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Stamping Status', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

