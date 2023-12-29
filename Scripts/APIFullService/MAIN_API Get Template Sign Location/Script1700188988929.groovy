import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper as JsonSlurper

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathGetTempSign).columnNumbers

'looping API Download Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGetTempSign, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API get template sign loc'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Get Template Sign Location', [('callerId') : findTestData(
                        excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('docTempCode') : findTestData(
                        excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode'))]))

        'ambil lama waktu yang diperlukan hingga request menerima balikan'
        elapsedTime = ((respon.elapsedTime / 1000) + ' second')

        'ambil body dari hasil respons'
        responseBody = respon.responseBodyContent

        'panggil keyword untuk proses beautify dari respon json yang didapat'
        CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

        'write to excel response elapsed time'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
            1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'get data store db'
                    ArrayList result = CustomKeywords.'connection.APIFullService.getTemplateSignloc'(conneSign, findTestData(
                            excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')).replace(
                            '"', ''), GlobalVariable.Tenant)

                    'ambil countresult yang dikembalikan oleh query result'
                    int countresult = CustomKeywords.'connection.APIFullService.getCountSignLoc'(conneSign, findTestData(
                            excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')).replace(
                            '"', ''), GlobalVariable.Tenant)

                    'declare arrayindex'
                    arrayindex = 0

                    'looping semua array yang terdapat di respon API dan DB'
                    for (i = 0; i < countresult; i++) {
                        'verify signerType'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, ('templateSignLocation[' + i) + 
                                    '].signerType', FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify signType'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, ('templateSignLocation[' + i) + 
                                    '].signType', FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify signPage'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, ('templateSignLocation[' + i) + 
                                    '].signPage', FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify digiSignLocation'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, ('templateSignLocation[' + i) + 
                                    '].digiSignLocation', FailureHandling.OPTIONAL).toString().replace(' ', '').replace(
                                    '{', '').replace('}', '').replace('=', ':'), (result[arrayindex++]).toString().replace(
                                    '"', '').replace('{', '').replace('}', '').replace(' ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify tknajSignLocation'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, ('templateSignLocation[' + i) + 
                                    '].tknajSignLocation', FailureHandling.OPTIONAL).toString().replace(' ', '').replace(
                                    '{', '').replace('}', '').replace('=', ':'), (result[arrayindex++]).toString().replace(
                                    '"', '').replace('{', '').replace('}', '').replace(' ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'ambil sign loc untuk vida dan privy'
                    ArrayList resultDetail = CustomKeywords.'connection.APIFullService.getTemplateSignlocDetail'(conneSign, 
                        findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')).replace(
                            '"', ''), GlobalVariable.Tenant).toString().replace('"h"', '').replace('"w"', '').replace('"x"', 
                        '').replace('"y"', '').replace('{', '').replace(':', '').replace(' ', '').replace(' ', '').replace(
                        '[', '').replace(']', '').replace('}', '').split(',', -1).sort()

                    def jsonSlurper = new JsonSlurper()

                    def jsonResponseObject = jsonSlurper.parseText(responseBody)

                    'ambil seluruh vida sign loc dari response'
                    ArrayList vidaSignLocation = jsonResponseObject.templateSignLocation.collect({ 
                            it.vidaSignLocation
                        }).toString().replace('x:', '').replace('y:', '').replace('h:', '').replace('w:', '').replace(' ', 
                        '').replace('[', '').replace(']', '').split(',', -1)

                    'ambil seluruh privy sign loc dari response'
                    ArrayList privySignLocation = jsonResponseObject.templateSignLocation.collect({ 
                            it.privySignLocation
                        }).toString().replace('x:', '').replace('y:', '').replace('h:', '').replace('w:', '').replace(' ', 
                        '').replace('[', '').replace(']', '').split(',', -1)

                    'gabungkan vida dan privysignloc'
                    vidaSignLocation.addAll(privySignLocation)

                    'sort ulang vidaSignloc'
                    vidaSignLocation.sort()

                    'looping semua array yang terdapat di respon API dan DB'
                    for (i = 0; i < resultDetail.size(); i++) {
                        'verify vida and privySignLocation'
                        arrayMatch.add(WebUI.verifyMatch(resultDetail[i], vidaSignLocation[i], false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
                
                'tulis sukses jika store DB berhasil'
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

