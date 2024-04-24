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
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
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
            bodyAPI = pdfToBase64(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Dokumen')))
        } else {
            'input bodyAPI tidak dengan Base64'
            bodyAPI = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Dokumen'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Insert Stamping Payment Receipt', [('documentTemplateCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), ('callerId') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('documentNumber') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), ('documentTransactionId') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTransactionId')), ('docName') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Nama Dokumen')), ('docDate') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('$Tanggal Dokumen')), ('docType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('docTypeCode')), ('docNominal') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('docNominal')), ('peruriDocTypeId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('peruriDocTypeId')), ('officeCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('officeCode')), ('officeName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('officeName')), ('regionCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('regionCode')), ('regionName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('regionName')), ('idType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('idType')), ('idNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'idNo')), ('taxOwedsName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'taxOwedsName')), ('returnStampResult') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('returnStampResult')), ('documentFile') : bodyAPI]))

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
                                        GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('documentTransactionId')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('$Nama Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('$Tanggal Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE))

                            'check store db dari input'
                            arrayMatch.add(WebUI.verifyMatch(resultStoreDB[arrayIndex++], findTestData(excelPath).getValue(
                                        GlobalVariable.NumofColm, rowExcel('docTypeCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

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
                        arrayMatch.add(WebUI.verifyEqual(resultStoreDB.size() / 16, Integer.parseInt(CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign,
					findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))[1]), FailureHandling.CONTINUE_ON_FAILURE))

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

