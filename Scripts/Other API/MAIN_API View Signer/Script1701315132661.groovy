import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
        
        'HIT API Login untuk ambil bearer token'
        respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/View Signer', [('documentId') : (findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Document ID'))), ('callerId') : (findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username')))]))

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            def elapsedTime = (respon.getElapsedTime() / 1000) + ' second'

            'ambil body dari hasil respons'
            responseBody = respon.getResponseBodyContent()

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'mengambil status code berdasarkan response HIT API'
                status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

                'jika status codenya 0'
                if (status_Code == 0) {
					resultTotalResult = WS.getElementPropertyValue(respon, 'totalResult', FailureHandling.OPTIONAL)
					
					resultSignerType = WS.getElementPropertyValue(respon, 'listSigner.signerType', FailureHandling.OPTIONAL)
					
					resultSignerName = WS.getElementPropertyValue(respon, 'listSigner.signerName', FailureHandling.OPTIONAL)
					
					resultSignerPhone = WS.getElementPropertyValue(respon, 'listSigner.signerPhone', FailureHandling.OPTIONAL)
					
					resultSignerEmail = WS.getElementPropertyValue(respon, 'listSigner.signerEmail', FailureHandling.OPTIONAL)
					
					resultSignerStatus = WS.getElementPropertyValue(respon, 'listSigner.signStatus', FailureHandling.OPTIONAL)
					
					resultSignerDate = WS.getElementPropertyValue(respon, 'listSigner.signDate', FailureHandling.OPTIONAL)
					
					resultSignerRegisterStatus = WS.getElementPropertyValue(respon, 'listSigner.registerStatus', FailureHandling.OPTIONAL)

					if (GlobalVariable.checkStoreDB == 'Yes') {
						'declare arraylist arraymatch'
						ArrayList arrayMatch = []

						'get data store db'
						ArrayList result = CustomKeywords.'connection.APIFullService.getViewSigner'(conneSign, findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Document ID')))

						'declare arrayindex'
						arrayindex = 0

						'verify total signer'
						arrayMatch.add(WebUI.verifyMatch(resultTotalResult.toString(), (result.size() / 7).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
						
						for (index = 0; index < (result.size() / 7); index++) {
							'verify signer type'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerType[index], false, FailureHandling.CONTINUE_ON_FAILURE))

							'verify signer name'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerName[index], false, FailureHandling.CONTINUE_ON_FAILURE))
							
							SHA256Phone = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(resultSignerPhone[index])
							
							'verify signer phone'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], SHA256Phone, false, FailureHandling.CONTINUE_ON_FAILURE))
							
							'verify signer email'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerEmail[index], false, FailureHandling.CONTINUE_ON_FAILURE))
							
							'verify signer sign status'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerStatus[index], false, FailureHandling.CONTINUE_ON_FAILURE))
							
							if(resultSignerStatus[index] == 'Signed') {
								'verify signer sign date'
								arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerDate[index], false, FailureHandling.CONTINUE_ON_FAILURE))
							} else {
								'verify signer sign date'
								arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], '', false, FailureHandling.CONTINUE_ON_FAILURE))
							}
							
							'verify signer sign register status'
							arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultSignerRegisterStatus[index], false, FailureHandling.CONTINUE_ON_FAILURE))
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
										
                    'tulis sukses jika store DB berhasil'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                        '') + ';') + GlobalVariable.ReasonFailedHitAPI)
            }
        } else {
            getErrorMessageAPI(respon_login)
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

