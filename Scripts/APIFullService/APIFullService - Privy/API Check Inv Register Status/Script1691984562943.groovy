import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable

import java.nio.charset.StandardCharsets
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathCheckInvRegisterStatus).columnNumbers

'looping API Check Inv Register Status'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckInvRegisterStatus, GlobalVariable.NumofColm, 12)
		
		'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
		value = encodeValue(findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, 10), conneSign)
		
	    'HIT API'
	    respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Check Inv Register Status', [('callerId') : findTestData(excelPathCheckInvRegisterStatus).getValue(
			GlobalVariable.NumofColm, 9), ('msg') : '"' + value + '"']))
	
		   if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			   
			   'get  code'
			   code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
			   
			   if(code == 0) {
				   'get activeStatus'
				   activeStatus = WS.getElementPropertyValue(respon, 'activeStatus', FailureHandling.OPTIONAL)
				   
				   'get registrationStatus'
				   registrationStatus = WS.getElementPropertyValue(respon, 'registrationStatus', FailureHandling.OPTIONAL)
				   
				   'get verificationInProgress'
				   verificationInProgress = WS.getElementPropertyValue(respon, 'verificationInProgress', FailureHandling.OPTIONAL)
				   
				   'get verificationResult'
				   verificationResult = WS.getElementPropertyValue(respon, 'verificationResult', FailureHandling.OPTIONAL)
				
				   'write to excel success'
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Inv Register Status',
					   0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	
				   'write to excel verif status'
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Inv Register Status',
					   5, GlobalVariable.NumofColm - 1, verifStatus)
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
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Inv Register Status', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}


def encodeValue(String value, Connection conneSign) {
	
	'get invitation code dari db'
	String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, value)
	
	'Mengambil aes key based on tenant tersebut'
	String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)
	
	'encrypt invitation code'
	String encryptCode = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)
	
	try {
		return URLEncoder.encode(encryptCode, StandardCharsets.UTF_8.toString());
	} catch (UnsupportedEncodingException ex) {
		throw new RuntimeException(ex.getCause());
	}
}
