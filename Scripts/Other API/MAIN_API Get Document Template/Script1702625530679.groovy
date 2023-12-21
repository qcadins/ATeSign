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

//store customkeyword inside Connection variable type conneSign  with CustomKeywords from connection with class ConnectDB and keyword connectDBeSign without parameter.

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

//Store customkeyword as value to GlobalVariable.DataFilePath with CustomKeywords from customizekeyword with class WriteExcel and keyword getExcelPath with 1 parameter filled with '\\Excel\\2.1 Esign - API Only.xlsx'.

//GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

// Make variable with name countColmExcel as Int and store findTestData with path excelPath and use keywords columnNumbers
//int countColmExcel = findTestData(GlobalVariable.DataFilePath).columnNumbers

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
		respon = WS.sendRequest(findTestObject('Postman/Get Document Template', [('tenantCode') : findTestData(excelPath).getValue(
						GlobalVariable.NumofColm, rowExcel('tenantCode')), ('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))
						, ('documentTemplateCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode'))]))
					
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
					ArrayList result = CustomKeywords.'connection.PengaturanDokumen.getDocumentTemplateAPI'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))
					
					arrayIndex = 0
					
					ArrayList resultSignLocation = WS.getElementPropertyValue(respon, 'signer.signLocation', FailureHandling.OPTIONAL)
					
					ArrayList resultSignerTypeCode = WS.getElementPropertyValue(respon, 'signer.signerTypeCode', FailureHandling.OPTIONAL)
					 
					ArrayList resultSignTypeCode = WS.getElementPropertyValue(respon, 'signer.signTypeCode', FailureHandling.OPTIONAL)
					
					ArrayList resultSignPage = WS.getElementPropertyValue(respon, 'signer.signPage', FailureHandling.OPTIONAL)
					
					ArrayList resultTransform = WS.getElementPropertyValue(respon, 'signer.transform', FailureHandling.OPTIONAL)
					
					ArrayList resultPosition = WS.getElementPropertyValue(respon, 'signer.position', FailureHandling.OPTIONAL)
					
					ArrayList resultPositionVida = WS.getElementPropertyValue(respon, 'signer.positionVida', FailureHandling.OPTIONAL)
					
					ArrayList resultPositionPrivy = WS.getElementPropertyValue(respon, 'signer.positionPrivy', FailureHandling.OPTIONAL)
					
					ArrayList resultSeqNo = WS.getElementPropertyValue(respon, 'signer.seqNo', FailureHandling.OPTIONAL)
					
					resultDocumentTemplateCode = WS.getElementPropertyValue(respon, 'documentTemplateCode', FailureHandling.OPTIONAL)
					
					resultDocumentTemplateName = WS.getElementPropertyValue(respon, 'documentTemplateName', FailureHandling.OPTIONAL)
					
					resultDocumentTemplateDescription = WS.getElementPropertyValue(respon, 'documentTemplateDescription', FailureHandling.OPTIONAL)
					
					resultIsActive = WS.getElementPropertyValue(respon, 'isActive', FailureHandling.OPTIONAL)
					
					resultPaymentSignTypeCode = WS.getElementPropertyValue(respon, 'paymentSignTypeCode', FailureHandling.OPTIONAL)
					
					resultIsSequence = WS.getElementPropertyValue(respon, 'isSequence', FailureHandling.OPTIONAL)
					
					resultVendorCode = WS.getElementPropertyValue(respon, 'vendorCode', FailureHandling.OPTIONAL)
					
					ArrayList arrayMatch = []

					for (index = 0; index < result.size() / 9; index++) {

						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++].replace('"','').replace(':','='), resultSignLocation[index].toString().replace('[','{').replace(']','}').replace(' ',''), false, FailureHandling.CONTINUE_ON_FAILURE))
					
						if (result[arrayIndex] != null) {
							'verify signer type'
							arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultSignerTypeCode[index], false, FailureHandling.CONTINUE_ON_FAILURE))
						} else {
							arrayIndex++
						}
						
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultSignTypeCode[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultSignPage[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultTransform[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultPosition[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultPositionVida[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], resultPositionPrivy[index], false, FailureHandling.CONTINUE_ON_FAILURE))
					
						'verify signer type'
						arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++].toString(), resultSeqNo[index].toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
					}
					
					ArrayList result2 = CustomKeywords.'connection.PengaturanDokumen.getDocumentTemplateAPI2'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))
					
					arrayIndex = 0

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultDocumentTemplateCode, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultDocumentTemplateName, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultDocumentTemplateDescription, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultIsActive, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultPaymentSignTypeCode, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultIsSequence, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'verify signer type'
					arrayMatch.add(WebUI.verifyMatch(resultVendorCode, result2[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					
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

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) +
		'>')

	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}