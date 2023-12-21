import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

def conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

String dateStart, dateEnd, deliveryStatus

// Looping to iterate from GlobalVariable.NumofColm to countColmExcel. Use GlobalVariable.NumofColm as the variable starting from 2.
for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; GlobalVariable.NumofColm++) {
	// Create a variable 'status' and store the value of the 'Status' cell in the current column
	def status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

	// If status is empty, break the loop
	if (status == '') {
		break
	}
	// If status is 'Unexecuted', set GlobalVariable.FlagFailed to 0 and perform some actions
	else if (status.equalsIgnoreCase('Unexecuted')) {
		GlobalVariable.FlagFailed = 0

		// Call the 'settingBaseUrl' keyword from the 'APIFullService' class with the necessary parameters
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'check if tidak mau menggunakan vendor code yang benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'No') {
			'set vendor kosong'
			GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Vendor Code'))
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'Yes') {
			'get vendor per case dari colm excel'
			GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code*'))
		}

		// Create a variable 'userCorrectTenantCode' and store the value of the 'Use Correct Tenant Code' cell in the current column
		def userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code'))

		// If userCorrectTenantCode is 'Yes', store the value of 'Tenant Login' cell in GlobalVariable.Tenant
		if (userCorrectTenantCode == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		// If userCorrectTenantCode is 'No', store the value of 'Wrong Tenant Code' cell in GlobalVariable.Tenant
		else if (userCorrectTenantCode == 'No') {
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		}
		
		'ambil data date start'
		dateStart = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Report Date Start*'))
		
		'ambil data date start'
		dateEnd = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Report Date End*'))

	   'HIT API Login untuk token'
		respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username'))
					, ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

		// If the response status code is 200, get the 'access_token' parameter from the response and store it in GlobalVariable.token
		if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL)) {
			GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
			
			'check delivery status dan convert menjadi integer yang benar'
			if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'All' ||
					findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == '') {
				'set deliverystatus kosong'
				deliveryStatus = ''
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Not Started') {
				'set deliverystatus 0'
				deliveryStatus = '0'
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Waiting') {
				'set deliverystatus 1'
				deliveryStatus = '1'
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Failed') {
				'set deliverystatus 2'
				deliveryStatus = '2'
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Delivered') {
				'set deliverystatus 3'
				deliveryStatus = '3'
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Read') {
				'set deliverystatus 4'
				deliveryStatus = '4'
			}
			
			// Send an API request to the Vendor object in the Postman repository and retrieve the response
			'HIT API utamanya'
			 respon = WS.sendRequest(findTestObject('Postman/listMessageDelivery', [
				 ('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')),
				  ('page') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Page*')),
				  ('messageMedia') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Message Media*')),
				  ('dateStart') : dateStart,
				  ('dateEnd') : dateEnd,
				  ('deliveryStatus') : deliveryStatus,
				  ('recipient') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Recipient*'))]))
	 
			 // Create a variable 'elapsedTime' which contains the elapsed time in seconds
			 def elapsedTime = respon.getElapsedTime() / 1000 + ' seconds'
	 
			 // Create a variable 'responseBody' which contains the response body content
			 def responseBody = respon.getResponseBodyContent()
	 
			 // Process the response body to beautify the JSON format
			 CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	 
			 // Write the elapsed time to the Excel file
			 CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
	 
			 // If the response status code is 200, perform some actions
			 if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
				 // Get the 'status.code' parameter from the response and store it in 'status_Code'
				 def status_Code = WS.getElementPropertyValue(respon, 'status.code')
	 
				 // If status_Code is 0, perform some actions
				 if (status_Code == 0) {
	 
					 // If GlobalVariable.checkStoreDB is 'Yes', perform some actions
					 if (GlobalVariable.checkStoreDB == 'Yes') {
						 
						 if (WS.getElementPropertyValue(respon, 'page').toString() != '' &&
							 	WS.getElementPropertyValue(respon, 'page').toString() != '0' &&
								 	WS.getElementPropertyValue(respon, 'page').toString() != 'null') {
								 
							 vendorname = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.vendorName')
							 
							 reportTime = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.reportTime')
							 
							 recipient = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.recipient')
							 
							 trxNo = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.trxNo')
							 
							 messageMedia = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.messageMedia')
							 
							 deliveryState = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.deliveryStatus')
							 
							 deliveryStatusInformation = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.deliveryStatusInformation')
							 
							 int index = (WS.getElementPropertyValue(respon, 'page') - 1) * 10
							
							 // Declare an ArrayList called 'arrayMatch'
							 def arrayMatch = []
							 
							 // Declare an ArrayList called 'result' and store the result of the 'getVendorofTenant' keyword from the 'APIFullService' class
							 ArrayList result = CustomKeywords.'connection.APIFullService.getListMessageDeliveryAPIOnly'(conneSign, GlobalVariable.Tenant, GlobalVariable.Psre,
								 findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Message Media*')),
								dateStart, dateEnd, deliveryStatus,
								findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Recipient*')))
		 
							 int arrayIndex = 0
	 
							 'loop untuk pengecekan hasil dari DB'
							 for (index; index < (vendorname.size()); index++) {
	 
								 'verify vendorname'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], vendorname[index], false, FailureHandling.CONTINUE_ON_FAILURE))
								 
								 'verify reportTime'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], reportTime[index], false, FailureHandling.CONTINUE_ON_FAILURE))
	 
								 'verify recipient'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], recipient[index], false, FailureHandling.CONTINUE_ON_FAILURE))
								 
								 'verify trxNo'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], trxNo[index], false, FailureHandling.CONTINUE_ON_FAILURE))
								 
								 'verify messageMedia'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], messageMedia[index], false, FailureHandling.CONTINUE_ON_FAILURE))
								 
								 'verify deliveryStatus'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], deliveryState[index], false, FailureHandling.CONTINUE_ON_FAILURE))
								 
								 'verify deliveryStatusInformation'
								 arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], deliveryStatusInformation[index], false, FailureHandling.CONTINUE_ON_FAILURE))
	 
							 }
							 
							 // If 'arrayMatch' contains 'false', set GlobalVariable.FlagFailed to 1 and perform some actions
							 if (arrayMatch.contains(false)) {
								 GlobalVariable.FlagFailed = 1
		 
								 // Write the status reason to the Excel file
								 CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';' + GlobalVariable.ReasonFailedStoredDB)
							 }
						 }
					 }
	 
					 // If GlobalVariable.FlagFailed is 0, perform some actions
					 if (GlobalVariable.FlagFailed == 0) {
						 // Write the status to the Excel file
						 CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					 }
				 }
				 // If status_Code is not 0, perform some actions
				 else {
					 // Get the error message from the response and write it to the Excel file
					 getErrorMessageAPI(respon)
				 }
			 }
			 // If the response status code is not 200, perform some actions
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
 *  This keyword is used to get the error message from an API response and write it to the Excel file.
 *
 *  @param respon The API response
 */
def getErrorMessageAPI(respon) {
	// Get the 'status.message' parameter from the response and store it in 'message'
	def message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	// Write the status reason to the Excel file
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';' + message)

	// Set GlobalVariable.FlagFailed to 1
	GlobalVariable.FlagFailed = 1
}

/*
 *  This keyword is used to get the row number of a cell in the Excel file.
 *
 *  @param cellValue The value of the cell
 *  @return The row number of the cell
 */
def rowExcel(cellValue) {
	// Call the 'getExcelRow' keyword from the 'WriteExcel' class with the necessary parameters
	CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}