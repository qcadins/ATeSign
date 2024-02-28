import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [
						('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')), 
						('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
        } else {
            getErrorMessageAPI(responLogin)
        }
        
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Token')) == 'No') {
            GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Get Data User Management View', [
						('tenantCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode')), 
						('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
						('role') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('role')), 
						('loginId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('loginId'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			elapsedTime = (respon.elapsedTime / 1000) + ' second'
	
			'ambil body dari hasil respons'
			responseBody = respon.responseBodyContent
	
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
					excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
			
            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    ArrayList result = CustomKeywords.'connection.UserManagement.getDataUserManagementViewAPI'(conneSign, 
                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('role')), findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('tenantCode')), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('loginId')))

                    resultLoginId = WS.getElementPropertyValue(respon, 'loginId', FailureHandling.OPTIONAL)

                    resultName = WS.getElementPropertyValue(respon, 'name', FailureHandling.OPTIONAL)

                    resultRole = WS.getElementPropertyValue(respon, 'role', FailureHandling.OPTIONAL)

                    resultActivatedDate = WS.getElementPropertyValue(respon, 'activatedDate', FailureHandling.OPTIONAL)

                    resultOfficeCode = WS.getElementPropertyValue(respon, 'officeCode', FailureHandling.OPTIONAL)

                    resultOffice = WS.getElementPropertyValue(respon, 'office', FailureHandling.OPTIONAL)

                    resultIsActive = WS.getElementPropertyValue(respon, 'isActive', FailureHandling.OPTIONAL)

                    arrayIndex = 0

                    ArrayList arrayMatch = []

                    'verify user data'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultLoginId, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultName, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultRole, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultActivatedDate, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultOfficeCode, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultOffice, false, FailureHandling.CONTINUE_ON_FAILURE))

                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultIsActive, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
                
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

