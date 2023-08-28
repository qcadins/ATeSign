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
    if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'get tenant dari excel per case'
    	GlobalVariable.Tenant = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckInvRegisterStatus, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Psre')).equalsIgnoreCase('No')) {
			GlobalVariable.Psre = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Psre'))
		} else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Psre')).equalsIgnoreCase('Yes')) {
			GlobalVariable.Psre = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		}
		
		String value
		
		if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase('Yes')) {
			'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
			value = encodeValue(findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Email')), conneSign)
		} else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase('No')) {
			value = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Message'))
		}
		
		println(value)
		
	    'HIT API'
	    respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Check Inv Register Status', [('callerId') : findTestData(excelPathCheckInvRegisterStatus).getValue(
			GlobalVariable.NumofColm, rowExcel('CallerId')), ('msg') : '"' + value + '"']))
	
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
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					   0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	
				   'write to excel verif status'
				   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					   5, GlobalVariable.NumofColm - 1, activeStatus + ';' + registrationStatus + ';' + verificationInProgress + ';' + verificationResult)
				   
				   if(GlobalVariable.checkStoreDB == 'Yes') {
					   'declare arraylist arraymatch'
					   ArrayList<String> arrayMatch = []
					   
					   if(GlobalVariable.Psre == 'PRIVY') {
						   'get result dari db'
						   result = CustomKeywords.'connection.APIFullService.getCheckInvRegisStoreDB'(conneSign, findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, 11))
						   
						   arrayIndex = 0 
						   
						   'verify activeStatus'
						   arrayMatch.add(WebUI.verifyMatch(activeStatus, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify registrationStatus'
						   arrayMatch.add(WebUI.verifyMatch(registrationStatus, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify verificationInProgress'
						   arrayMatch.add(WebUI.verifyMatch(verificationInProgress, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify verificationResult'
						   arrayMatch.add(WebUI.verifyMatch(verificationResult, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						   
					   } else if(GlobalVariable.Psre == 'VIDA') {
						   'verify activeStatus'
						   arrayMatch.add(WebUI.verifyMatch(activeStatus, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify registrationStatus'
						   arrayMatch.add(WebUI.verifyMatch(registrationStatus, '1', false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify verificationInProgress'
						   arrayMatch.add(WebUI.verifyMatch(verificationInProgress, '0', false, FailureHandling.CONTINUE_ON_FAILURE))
						   
						   'verify verificationResult'
						   arrayMatch.add(WebUI.verifyMatch(verificationResult, '', false, FailureHandling.CONTINUE_ON_FAILURE))
					   }
					   
					   'jika data db tidak sesuai dengan excel'
					   if (arrayMatch.contains(false)) {
						   'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							   GlobalVariable.StatusFailed, (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm,
								   rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
						   
						   GlobalVariable.FlagFailed = 1
					   }
				   }
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


def encodeValue(String value, Connection conneSign) {
	'get invitation code dari db'
	String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, value)

	'Mengambil aes key based on tenant tersebut'
	String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)
	
	'encrypt invitation code'
	String encryptCode = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)

	try {
		return URLEncoder.encode(encryptCode, StandardCharsets.UTF_8.toString());
	} catch (UnsupportedEncodingException ex) {
		throw new RuntimeException(ex.getCause());
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
