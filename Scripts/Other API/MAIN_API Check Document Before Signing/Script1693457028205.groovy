import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathCheckDocBeforeSigning).columnNumbers

'looping API Confirm OTP'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 
    0) {
        break
    } else if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckDocBeforeSigning, GlobalVariable.NumofColm, 
            rowExcel('Use Correct Base Url'))

        'set psre sesuai inputan excel per case'
        GlobalVariable.Psre = findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'HIT API untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('email') : findTestData(excelPathCheckDocBeforeSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''), ('password') : findTestData(excelPathCheckDocBeforeSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'Yes')) {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                GlobalVariable.token = findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                        'Wrong Token'))
            }
            
            'HIT API'
            respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Check Document Before Signing', [
                        ('docId') : findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Doc ID')), ('email') : findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 
                            rowExcel('email'))]))

            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'get  doc id'
                docId = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.documentId', FailureHandling.OPTIONAL)

                'get  signing Process'
                signingProcess = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.signingProcess', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    for (index = 0; index < docId.size(); index++) {
                        'get data from DB'
                        ArrayList resultDB = CustomKeywords.'connection.APIFullService.getDocSignSequence'(conneSign, docId[
                            index], findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'email')).replace('"', ''))

                        arrayIndex = 0

                        'verify doc ID'
                        arrayMatch.add(WebUI.verifyMatch(docId[index], resultDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify status'
                        arrayMatch.add(WebUI.verifyMatch(signingProcess[index], resultDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'call function get error message API'
                getErrorMessageAPI(respon)
            }
        } else {
            'call function get error message API'
            getErrorMessageAPI(responLogin)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'error_description', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

