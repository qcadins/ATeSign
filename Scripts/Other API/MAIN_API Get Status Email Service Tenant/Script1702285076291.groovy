import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    // Create a variable 'status' and store the value of the 'Status' cell in the current column
    String status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    // If status is empty, break the loop
    if (status == '') {
        break
    }
    
    // If status is 'Unexecuted', set GlobalVariable.FlagFailed to 0 and perform some actions
    if (status == 'Unexecuted') {
        GlobalVariable.FlagFailed = 0

        // Call a custom keyword 'settingBaseUrl' from the 'APIFullService' class with 3 parameters
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        // Create a variable 'userCorrectTenantCode' and store the value of the 'Use Correct Tenant Code' cell in the current column
      //  String userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code'))

        // If userCorrectTenantCode is 'Yes', store the value of 'Tenant Login' cell in GlobalVariable.Tenant
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))
        
        // Send an API request to the Login object in the Postman repository and retrieve the response
        'HIT API Login untuk ambil bearer token'
        reponLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        // If the response status code is 200 and failurehandling.OPTIONAL is true, get the 'access_token' parameter from the response and store it in GlobalVariable.Token
        if (WS.verifyResponseStatusCode(reponLogin, 200, FailureHandling.OPTIONAL) == true) {
            GlobalVariable.token = WS.getElementPropertyValue(reponLogin, 'access_token')

            // Send an API request to the Vendor object in the Postman repository and retrieve the response
            respon = WS.sendRequest(findTestObject('Postman/getStatusEmailServiceTenant', [
						('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username'))]))

            // If the response status code is 200 and failurehandling.OPTIONAL is true, get the 'status.code' parameter from the response and store it in 'statusCode'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                statusCode = WS.getElementPropertyValue(respon, 'status.code')

				// Create a variable 'elapsedTime' and store the elapsed time of the request in seconds
				elapsedTime = ((respon.elapsedTime / 1000) + ' seconds')
	
				// Create a variable 'responseBody' and store the response body content
				responseBody = respon.responseBodyContent
	
				// Call a custom keyword 'process' from the 'BeautifyJson' class with 5 parameters
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				// Call a custom keyword 'writeToExcel' from the 'WriteExcel' class with 5 parameters
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                // If statusCode is 0, perform some actions
                if (statusCode == 0) {
                    // If GlobalVariable.checkStoreDB is 'Yes', perform some actions
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        // Declare an ArrayList<String> called 'arrayMatch'
                        ArrayList arrayMatch = []

                        // Declare an ArrayList<String> called 'result' and store the result of the 'getVendorofTenant' custom keyword with 1 parameter
                        result = CustomKeywords.'connection.APIFullService.getStatusEmailServiceAPIOnly'(conneSign, GlobalVariable.Tenant)

                        'deklarasi array index'
                        arrayIndex = 0

                        'verify code di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], WS.getElementPropertyValue(respon, 'tenantCode', 
                                    FailureHandling.OPTIONAL).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify description di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], WS.getElementPropertyValue(respon, 'tenantName', 
                                    FailureHandling.OPTIONAL).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify description di API dengan DB'
                        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], WS.getElementPropertyValue(respon, 'emailService', 
                                    FailureHandling.OPTIONAL).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'jika data db tidak sesuai dengan excel'
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                        }
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

def getErrorMessageAPI(ResponseObject respon) {
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + '<' + message + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

