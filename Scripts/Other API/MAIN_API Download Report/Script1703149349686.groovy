import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

// Delcare an int countColmExcel and store findTestData with path excelPath and use keywords columnNumbers
int countColmExcel = findTestData(excelPath).columnNumbers

int idManualReport

// Looping to iterate from GlobalVariable.NumofColm to countColmExcel. Use GlobalVariable.NumofColm as the variable starting from 2.
for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    // Create a variable 'status' and store the value of the 'Status' cell in the current column
    String status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    // If status is empty, break the loop
    if (status == '') {
        break       
    } else if (status.equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code'))

        if (userCorrectTenantCode == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        } else if (userCorrectTenantCode == 'No') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        }
        
        'jika file name terisi'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('File Name')) != '') {
            'ambil id manual Report dari DB'
            idManualReport = CustomKeywords.'connection.APIFullService.getIDManualReportAPIOnly'(conneSign, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('File Name')))
        }
        
        'HIT API Login untuk ambil bearer token'
        reponLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        if (WS.verifyResponseStatusCode(reponLogin, 200, FailureHandling.OPTIONAL)) {
            GlobalVariable.token = WS.getElementPropertyValue(reponLogin, 'access_token')

            respon = WS.sendRequest(findTestObject('Postman/downloadReport', [('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username')), ('idManReport') : idManualReport]))

            elapsedTime = (respon.elapsedTime / 1000) + ' seconds'

            responseBody = respon.responseBodyContent

            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
                def statusCode = WS.getElementPropertyValue(respon, 'status.code')

                if (statusCode == 0) {
                    resultbase64 = WS.getElementPropertyValue(respon, 'xlBase64', FailureHandling.OPTIONAL)

                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Save Document')).equalsIgnoreCase(
                        'Yes')) {
                        'decode Bas64 to File PDF'
                        CustomKeywords.'customizekeyword.ConvertFile.decodeBase64Excel'(resultbase64, findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('Scenario')))
                    }
                    
                    'tulis sukses jika store DB berhasil'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    getErrorMessageAPI(respon)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(reponLogin)
        }
    }
}

def getErrorMessageAPI(def respon) {
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + message)

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

