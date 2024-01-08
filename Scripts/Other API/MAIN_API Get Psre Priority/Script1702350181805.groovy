import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

// Delcare an int countColmExcel and store findTestData with path excelPath and use keywords columnNumbers
int countColmExcel = findTestData(excelPath).columnNumbers

// Looping to iterate from GlobalVariable.NumofColm to countColmExcel. Use GlobalVariable.NumofColm as the variable starting from 2.
for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    // Create a variable 'status' and store the value of the 'Status' cell in the current column
    String status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    // If status is empty, break the loop
    if (status == '') {
        break
    } else if (status == 'Unexecuted') {
        GlobalVariable.FlagFailed = 0

        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        String userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code'))

        if (userCorrectTenantCode == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        } else if (userCorrectTenantCode == 'No') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        }
        
        'HIT API Login untuk ambil bearer token'
        respon_login = WS.sendRequest(findTestObject('Postman/Login', [
		    ('username'): findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')),
		    ('password'): findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password')),
		]))


        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL)) {
            GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

            respon = WS.sendRequest(findTestObject('Postman/getPsrePriority', [('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username'))]))

            String elapsedTime = (respon.elapsedTime / 1000) + ' seconds'

            String responseBody = respon.responseBodyContent

            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
                statusCode = WS.getElementPropertyValue(respon, 'status.code')

                if (statusCode == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        ArrayList arrayMatch = []

                        ArrayList result = CustomKeywords.'connection.APIFullService.getPsrePriorityAPIOnly'(conneSign, 
                            GlobalVariable.Tenant)

                        vendorCode = WS.getElementPropertyValue(respon, 'listPsrePriority.vendorCode', FailureHandling.OPTIONAL)

                        vendorName = WS.getElementPropertyValue(respon, 'listPsrePriority.vendorName', FailureHandling.OPTIONAL)

                        priority = WS.getElementPropertyValue(respon, 'listPsrePriority.priority', FailureHandling.OPTIONAL)

                        'declare arrayindex'
                        arrayindex = 0

                        'loop untuk pengecekan hasil dari DB'
                        for (index = 0; index < (result.size() / 3); index++) {
                            'verify tenant name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], vendorCode[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify tenant name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], vendorName[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify tenant name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], priority[index], false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                        }
                    }
                    
                    if ((statusCode == 0) && (GlobalVariable.FlagFailed == 0)) {
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
            getErrorMessageAPI(respon_login)
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

