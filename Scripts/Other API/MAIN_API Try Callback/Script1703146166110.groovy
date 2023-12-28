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
for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; GlobalVariable.NumofColm++) {
    // Create a variable 'status' and store the value of the 'Status' cell in the current column
    String status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    // If status is empty, break the loop
    if (status == '') {
        break
    }
    // If status is 'Unexecuted', set GlobalVariable.FlagFailed to 0 and perform some actions
    else if (status.equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        // Call the 'settingBaseUrl' keyword from the 'APIFullService' class in the 'connection' package
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        // Create a variable 'userCorrectTenantCode' and store the value of the 'use Correct Tenant Code' cell in the current column
        String userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code'))

        // If userCorrectTenantCode is 'Yes', store the value of 'Tenant Login' cell in GlobalVariable.Tenant
        if (userCorrectTenantCode == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        // If userCorrectTenantCode is 'No', store the value of 'Wrong Tenant Code' cell in GlobalVariable.Tenant
        else if (userCorrectTenantCode == 'No') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        }

        // Send an API request to the Login object in the Postman repository and retrieve the response
        'HIT API Login untuk ambil bearer token'
        respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                    rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                    rowExcel('password'))]))

        // If the response status code is 200, get the 'access_token' parameter from the response and store it in GlobalVariable.token
        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL)) {
            GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
			
			// Send an API request to the Vendor object in the Postman repository and retrieve the response
			respon = WS.sendRequest(findTestObject('Postman/tryCallback', [
				('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username'))]))
	
			// Create a variable 'elapsedTime' which contains the elapsed time in seconds
			def elapsedTime = respon.getElapsedTime() / 1000 + ' seconds'
	
			// Create a variable 'responseBody' which contains the response body content
			def responseBody = respon.getResponseBodyContent()
	
			// Process the response body to beautify the JSON format
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			// Write the elapsed time to the Excel file
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
	
			// If the response status code is 200, perform some actions
			if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
				// Get the 'status.code' parameter from the response and store it in 'statusCode'
				def statusCode = WS.getElementPropertyValue(respon, 'status.code')
	
				// If statusCode is 0, perform some actions
				if (statusCode == 0) {
	
					// If GlobalVariable.checkStoreDB is 'Yes', perform some actions
					if (GlobalVariable.checkStoreDB == 'Yes') {
						// Declare an ArrayList called 'arrayMatch'
						ArrayList arrayMatch = []
	
						// Declare an ArrayList called 'result' and store the result of the 'getVendorofTenant' keyword from the 'connection' class in the 'CustomKeywords' package
						ArrayList result = CustomKeywords.'connection.APIFullService.getPsrePriorityAPIOnly'(conneSign, GlobalVariable.Tenant)
	
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
	
						// If 'arrayMatch' contains false, set GlobalVariable.FlagFailed to 1 and perform some actions
						if (arrayMatch.contains(false)) {
							GlobalVariable.FlagFailed = 1
	
							// Write the status reason to the Excel file
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';' + GlobalVariable.ReasonFailedStoredDB)
						}
					}
	
					// If statusCode is 0 and GlobalVariable.FlagFailed is 0, perform some actions
					if (statusCode == 0 && GlobalVariable.FlagFailed == 0) {
						// Write the status to the Excel file
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					}
				}
				// Else, perform some actions
				else {
					// Get the error message from the response and write it to the Excel file
					getErrorMessageAPI(respon)
				}
			}
			// Else, perform some actions
			else {
				// Get the error message from the response and write it to the Excel file
				getErrorMessageAPI(respon)
			}
        } else {
			// Get the error message from the response and write it to the Excel file
			getErrorMessageAPI(respon_login)
		}
    }
}

/*
 *  This function is used to get the error message from an API response and write it to the Excel file.
 *
 *  @param respon The API response
 */
def getErrorMessageAPI(def respon) {
    // Get the 'status.message' parameter from the response and store it in 'message'
    String message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    // Write the status reason to the Excel file
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';' + message)

    // Set GlobalVariable.FlagFailed to 1
    GlobalVariable.FlagFailed = 1
}

/*
 *  This function is used to get the row number of a cell in the Excel file.
 *
 *  @param cellValue The value of the cell
 *  @return The row number of the cell
 */
def rowExcel(cellValue) {
    // Call the 'getExcelRow' keyword from the 'WriteExcel' class in the 'customizekeyword' package
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}