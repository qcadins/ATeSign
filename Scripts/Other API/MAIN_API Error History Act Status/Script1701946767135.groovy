import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk token : andy@ad-ins.com'
        respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
        } else {
            getErrorMessageAPI(respon_login)
        }
        
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Token')) == 'No') {
            GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
        }
        
        'HIT API Login untuk token : andy@ad-ins.com'
        respon = WS.sendRequest(findTestObject('Postman/Error History Act Status', [('tenantCode') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('tenantCode')), ('idErrorHistory') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('idErrorHistory')), ('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))
					
		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		def elapsedTime = (respon.getElapsedTime()) / 1000 + ' second'
		
		'ambil body dari hasil respons'
		responseBody = respon.getResponseBodyContent()
		
		'panggil keyword untuk proses beautify dari respon json yang didapat'
		CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
			findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
		
		'write to excel response elapsed time'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm -
			1, elapsedTime.toString())
		
        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
            'Jika status codenya 0'
            if (status_Code == 0) {

				if (GlobalVariable.checkStoreDB == 'Yes') {
					ArrayList result = CustomKeywords.'connection.ErrorReport.getErrorHistoryActStatus'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('idErrorHistory')))
					
					ArrayList resultName = WS.getElementPropertyValue(respon, 'listActStatus.name', FailureHandling.OPTIONAL)
					
					ArrayList resultIdNo = WS.getElementPropertyValue(respon, 'listActStatus.idNo', FailureHandling.OPTIONAL)
					
					ArrayList resultActivationStatus = WS.getElementPropertyValue(respon, 'listActStatus.activationStatus', FailureHandling.OPTIONAL)
					
					ArrayList resultSignerType = WS.getElementPropertyValue(respon, 'listActStatus.signerType', FailureHandling.OPTIONAL)
					
					ArrayList arrayMatch = []
					
					ArrayList resultStatusActivation = []
					
					arrayIndex = 0
					
					for (index = 0; index < (result.size() / 4); index++) {
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultName[index], false, FailureHandling.CONTINUE_ON_FAILURE))

						'verify signer name'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultIdNo[index], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						resultStatusActivation = CustomKeywords.'connection.ErrorReport.getStatusActivationAPI'(conneSign, resultName[index], findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('idErrorHistory')))

						'verify signer sign status'
						arrayMatch.add(WebUI.verifyMatch(resultActivationStatus[index], resultStatusActivation[0], false, FailureHandling.CONTINUE_ON_FAILURE))

						'verify signer sign status'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultSignerType[index], false, FailureHandling.CONTINUE_ON_FAILURE))
						}

							'jika data db tidak sesuai dengan excel'
							if (arrayMatch.contains(false)) {
								GlobalVariable.FlagFailed = 1

								'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
								CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
									GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm,
										rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
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

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	if (message == null) {
		'mengambil status code berdasarkan response HIT API'
		message = WS.getElementPropertyValue(respon, 'error_description', FailureHandling.OPTIONAL)
	
	}
    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-','') + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}