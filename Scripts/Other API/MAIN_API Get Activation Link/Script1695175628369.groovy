import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'check ada value maka setting email service tenant'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
            'setting email service tenant'
            CustomKeywords.'connection.APIFullService.settingEmailServiceUser'(conneSign, findTestData(excelPath).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Service')), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                    rowExcel('idKTP')).replace('"', ''))
        }
        
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'get tenant per case dari colm excel'
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
        
        'HIT API GetActLink Document'
        responGetActLink = WS.sendRequest(findTestObject('Postman/Get Activation Link', [('callerId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('idKTP') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('idKTP'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responGetActLink, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(responGetActLink, 'status.code')

            'Jika status codenya 0'
            if (statusCode == 0) {
                'get url'
                url = WS.getElementPropertyValue(responGetActLink, 'status.message')

                'write to excel url'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 4, GlobalVariable.NumofColm - 
                    1, ('<' + url) + '>')

                'declare arraylist arraymatch'
                arrayMatch = []

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    ArrayList result = CustomKeywords.'connection.APIFullService.checkAPIGetActLinkStoreDB'(conneSign, findTestData(
                            excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('idKTP')).replace('"', ''))

                    if ((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'PRIVY')) {
                        'verify is_active'
                        arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else if ((GlobalVariable.Psre == 'DIGI') || (GlobalVariable.Psre == 'TKNAJ')) {
                        'verify is_active'
                        arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '0', false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'verify is_registered'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
                
                'jika data db tidak sesuai dengan excel'
                if (arrayMatch.contains(false)) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                    GlobalVariable.FlagFailed = 1
                } else if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(responGetActLink)
            }
        } else {
            getErrorMessageAPI(responGetActLink)
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

def encryptLink(Connection conneSign, String msg) {
    'get invitation code dari db'
    String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, msg)

    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)

    println(invCode)

    println(aesKey)

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)

    println(encryptMsg)

    encryptMsg
}

