import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathCheckVerificationStatus).columnNumbers

'looping API Check Verification Status'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckVerificationStatus, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'get psre dari excel per case'
		GlobalVariable.Psre = findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		} else if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}
		
	    'HIT API'
	    respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API CheckVerificationStatus', [('callerId') : findTestData(excelPathCheckVerificationStatus).getValue(
			GlobalVariable.NumofColm, rowExcel('$CallerId')), ('trxNo') : findTestData(excelPathCheckVerificationStatus).getValue(GlobalVariable.NumofColm, rowExcel('$TrxNo'))]))
	
		   if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			   'get status code'
			   statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
			   
			   if(statusCode == 0) {				   
				   'get  doc id'
				   verifStatus = WS.getElementPropertyValue(respon, 'verifStatus', FailureHandling.OPTIONAL)
				   
				   'get  signing Process'
				   results = WS.getElementPropertyValue(respon, 'results', FailureHandling.OPTIONAL)
				
				   'write to excel success'
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					   0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	
				   'write to excel verif status'
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					   5, GlobalVariable.NumofColm - 1, verifStatus.toString())
			   } else {
				   'call function get error message API'
				   getErrorMessageAPI(respon)
			   }
		   } else {
		        'call function get error message API'
				getErrorMessageAPI(respon)
		   }	
    }
}

def getErrorMessageAPI(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}