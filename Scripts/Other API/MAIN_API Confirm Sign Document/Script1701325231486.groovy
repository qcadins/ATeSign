import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('email') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''), ('password') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'Yes')) {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
            }

            'HIT API Sign Document'
            responConfirmSignDoc = WS.sendRequest(findTestObject('Postman/Confirm Sign Document', [('callerId') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('email') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')), ('documentId') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentId')), ('browser') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('browser'))]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responConfirmSignDoc, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responConfirmSignDoc, 'status.code')

                'Jika status codenya 0'
                if (statusCode == 0) {
                    'get current date'
                    currentDate = new Date().format('yyyy-MM-dd')

                    'ambil lama waktu yang diperlukan hingga request menerima balikan'
                    elapsedTime = (responConfirmSignDoc.elapsedTime / 1000) + ' second'

                    'ambil body dari hasil respons'
                    responseBody = responConfirmSignDoc.responseBodyContent

                    'panggil keyword untuk proses beautify dari respon json yang didapat'
                    CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, 
                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

                    'write to excel response elapsed time'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Process Time') - 1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                    'check Db'
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        for (timeout = 1; timeout <= 10; timeout++) {
                            'declare arraylist arraymatch'
                            ArrayList arrayMatch = []

                            'get sign date from db'
                            result = CustomKeywords.'connection.SendSign.getSignDocEmbedStoreDB'(conneSign, findTestData(
                                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentid')))

                            'verify date'
                            arrayMatch.add(WebUI.verifyMatch(currentDate, result, false, FailureHandling.OPTIONAL))

                            if ((timeout == 5) && arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and melebihi batas waktu delay'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' Job Sign tidak jalan selama delay 100 detik')

                                GlobalVariable.FlagFailed = 1
                            } else {
                                'delay 20 detik'
                                WebUI.delay(20)

                                continue
                            }
                        }
                    }
                    
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    getErrorMessageAPI(responConfirmSignDoc)
                }
            } else {
                getErrorMessageAPI(responConfirmSignDoc)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(responLogin, 'error_description', FailureHandling.OPTIONAL).toString()

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + '<') + message) + '>')

            GlobalVariable.FlagFailed = 1
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

