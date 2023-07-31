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
int countColmExcel = findTestData(excelPathAPICheckSigning).columnNumbers

'looping API Status Signing'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPICheckSigning, GlobalVariable.NumofColm, 17)
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 15) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 16)
        } else if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 14)
        }
        
        'HIT API check Status Signing'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Check Status Signing', [('callerId') : findTestData(
                        excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 9), ('refNumber') : findTestData(excelPathAPICheckSigning).getValue(
                        GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
				statusSigning = WS.getElementPropertyValue(respon, 'statusSigning', FailureHandling.OPTIONAL)
				
				'write to excel statusSigning'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Signing Status',
					5, GlobalVariable.NumofColm - 1, statusSigning.toString())
				
                docId = WS.getElementPropertyValue(respon, 'statusSigning.documentId', FailureHandling.OPTIONAL)

                email = WS.getElementPropertyValue(respon, 'statusSigning.signer.email', FailureHandling.OPTIONAL)

				signerType = WS.getElementPropertyValue(respon, 'statusSigning.signer.signerType', FailureHandling.OPTIONAL)
				
                statusSigning = WS.getElementPropertyValue(respon, 'statusSigning.signer.signStatus', FailureHandling.OPTIONAL)

                signDate = WS.getElementPropertyValue(respon, 'statusSigning.signer.signDate', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                	int indexArrayDB = 0, indexColResp = 0, indexRowResp = 0, i = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.APIFullService.getAPICheckSigningStoreDB'(conneSign, 
                        findTestData(excelPathAPICheckSigning).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    'declare arraylist arraymatch'
                    ArrayList<String> arrayMatch = []

                    for (i = 0; i < (result.size() / 4); i++) {
                        docIdDB = (result[indexArrayDB++])

                        if ((docId[indexColResp]).equalsIgnoreCase(docIdDB)) {
                            'verify doc ID'
                            arrayMatch.add(WebUI.verifyMatch((docId[indexColResp]).toUpperCase(), docIdDB.toUpperCase(), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))

                            for (indexRowResp = 0; indexRowResp < (email[indexColResp]).size(); indexRowResp++) {
                                emailDB = (result[indexArrayDB++])

                                if (((email[indexColResp])[indexRowResp]).equalsIgnoreCase(emailDB)) {
                                    'verify email'
                                    arrayMatch.add(WebUI.verifyMatch(((email[indexColResp])[indexRowResp]).toUpperCase(), 
                                            emailDB.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

									'verify signer type'
									arrayMatch.add(WebUI.verifyMatch(((signerType[indexColResp])[indexRowResp]).toUpperCase(),
											(result[indexArrayDB++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
									
                                    'verify status signing'
                                    arrayMatch.add(WebUI.verifyMatch(((statusSigning[indexColResp])[indexRowResp]).toUpperCase(), 
                                            (result[indexArrayDB++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                                    if (((statusSigning[indexColResp])[indexRowResp]) == '1') {
                                        'verify sign date'
                                        arrayMatch.add(WebUI.verifyMatch(((signDate[indexColResp])[indexRowResp]).toUpperCase(), 
                                                (result[indexArrayDB++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                                    } else {
                                        indexArrayDB++
                                    }
                                    
                                    indexRowResp = 0

                                    indexColResp = 0

                                    break
                                } else {
                                    indexArrayDB--
                                }
                            }
                        } else {
                            indexArrayDB--

                            indexColResp++
                        }
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Signing Status', 
                            GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathAPICheckSigning).getValue(
                                GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Signing Status', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Signing Status', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Signing Status', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

