import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

// Looping to iterate from GlobalVariable.NumofColm to countColmExcel. Use GlobalVariable.NumofColm as the variable starting from 2.
for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    // Create a variable 'status' and store the value of the 'Status' cell in the current column
    status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    // If status is empty, break the loop
    if (status == '') {
        break
    } else if (status == 'Unexecuted') {
        GlobalVariable.FlagFailed = 0

        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get vendor per case dari colm excel'
        GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code'))

       //userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code'))

        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        'HIT API Login untuk token : andy@ad-ins.com'
        reponLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        if (WS.verifyResponseStatusCode(reponLogin, 200, FailureHandling.OPTIONAL)) {
            GlobalVariable.token = WS.getElementPropertyValue(reponLogin, 'access_token')

            'HIT API utamanya'
            respon = WS.sendRequest(findTestObject('Postman/checkRegisterAutoFill', [('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username')), ('email') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Email'))]))

            elapsedTime = (respon.elapsedTime / 1000) + ' seconds'

            responseBody = respon.responseBodyContent

            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
                statusCode = WS.getElementPropertyValue(respon, 'status.code')

                if (statusCode == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        arrayMatch = []

                        result = CustomKeywords.'connection.APIFullService.getCheckRegistAutoFillAPIOnly'(conneSign, 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email')))

                        arrayIndex = 0

                        'verify fullName di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], WS.getElementPropertyValue(respon, 'fullName', 
                                    FailureHandling.OPTIONAL).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify emailUser di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], WS.getElementPropertyValue(respon, 'emailUser', 
                                    FailureHandling.OPTIONAL).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify phoneNumUser di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                                    WS.getElementPropertyValue(respon, 'phoneNumUser', FailureHandling.OPTIONAL).toString()), 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                        }
                    }
                    
                    if (GlobalVariable.FlagFailed == 0) {
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
        (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + '<' + message + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

