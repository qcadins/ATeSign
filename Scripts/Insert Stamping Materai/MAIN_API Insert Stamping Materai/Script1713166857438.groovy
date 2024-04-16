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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        String bodyAPI = ''
		
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'Jika flag tenant no'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'Jika dokumennya menggunakan base64'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 'Yes') {
            'input bodyAPI dengan Base64'
            bodyAPI = pdfToBase64(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')))
        } else {
            'input bodyAPI tidak dengan Base64'
            bodyAPI = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentFile'))
        }
		
		bodyStampingLocations = '{'
		
		for (loopingStampPage = 0; 
			loopingStampPage < findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('stampPage')).toString().split(';', -1).size();
			 loopingStampPage++) {
			 
				 bodyStampingLocations = bodyStampingLocations + '"stampPage" :' +
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('stampPage'))
				 .toString().split(';', -1)[loopingStampPage] + ', "notes" :"' +
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('notes'))
				 .toString().split(';', -1)[loopingStampPage] + '"' + ', "stampLocation" : { "llx" :' +
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('llx'))
				 .toString().split(';', -1)[loopingStampPage] + ',"lly" : ' + 
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('lly'))
				 .toString().split(';', -1)[loopingStampPage] + ',"urx" : ' + 
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('urx'))
				 .toString().split(';', -1)[loopingStampPage] + ',"ury" : ' + 
				 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('ury'))
				 .toString().split(';', -1)[loopingStampPage] + '}'

			 if (loopingStampPage == (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
				 rowExcel('stampPage')).toString().split(';', -1).size() - 1)) {
			 bodyStampingLocations = bodyStampingLocations + '}'
			 } else {
				 bodyStampingLocations = bodyStampingLocations + ','
			 }
		}

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/insertStampingMaterai', [('documentTemplateCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), ('callerId') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('documentNumber') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentNumber')), ('docName') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docName')), ('docDate') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('docDate')), ('docTypeCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('docTypeCode')), ('docNominal') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('docNominal')), ('peruriDocTypeId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('peruriDocTypeId')), ('officeCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('officeCode')), ('officeName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('officeName')), ('regionCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('regionCode')), ('regionName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('regionName')), ('businessLineCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('businessLineCode')), ('businessLineName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('businessLineName')),('taxType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('taxType')), ('idType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('idType')), ('idNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'idNo')), ('taxOwedsName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'taxOwedsName')), ('documentFile') : bodyAPI, ('stampingLocations') : bodyStampingLocations]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = ((respon.elapsedTime / 1000) + ' second')

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            'jika status codenya 0'
            if (statusCode == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                'tulis sukses jika store DB berhasil'
                if (GlobalVariable.FlagFailed == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        ArrayList resultStoreDB = CustomKeywords.'connection.Meterai.getInsertStampingPaymentReceipt'(conneSign, 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTransactionId')))

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        arrayIndex = 0

                        for (index = 0; index < (resultStoreDB.size() / 16); index++) {
                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], GlobalVariable.Tenant, false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('documentNumber')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('documentTransactionId')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('docName')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('docDate')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('docType')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('docNominal')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('peruriDocTypeId')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('officeCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('officeName')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('regionCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('regionName')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('idType')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('idNo')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('taxOwedsName')), false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyEqual(resultStoreDB.size() / 16, Integer.parseInt(CustomKeywords.'connection.Meterai.getCountTotalStampDutyOnTemplate'(
                                        conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))) + 
                                1, FailureHandling.CONTINUE_ON_FAILURE))

                        'jika data db tidak bertambah'
                        if (arrayMatch.contains(false)) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
            } else {
                'call function get API error message'
                getErrorMessageAPI(respon)
            }
        } else {
          'call function get API error message'
           getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def pdfToBase64(String fileName) {
    CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)
}

