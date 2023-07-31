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
int countColmExcel = findTestData(excelPathCheckDocBeforeSigning).columnNumbers

'looping API Confirm OTP'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckDocBeforeSigning, GlobalVariable.NumofColm, 15)
		
	    'HIT API'
	    responLogin = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/Login', [('email') : findTestData(excelPathCheckDocBeforeSigning).getValue(
			GlobalVariable.NumofColm, 10).replace('"',''), ('password') : findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 11)]))
	
	    'Jika status HIT API 200 OK'
	    if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
			
			if(findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('Yes')) {
				'Parsing token menjadi GlobalVariable'
				GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')			
			} else if(findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('No')) {
				GlobalVariable.token = findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 14)
	    	}
	
	       println(GlobalVariable.token)
		   
		   'HIT API'
		   respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Check Document Before Signing', [('docId') : findTestData(excelPathCheckDocBeforeSigning).getValue(
			   GlobalVariable.NumofColm, 9), ('email') : findTestData(excelPathCheckDocBeforeSigning).getValue(GlobalVariable.NumofColm, 10)]))
	
		   if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			   'get  doc id'
			   docId = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.documentId', FailureHandling.OPTIONAL)
			   
			   'get  signing Process'
			   signingProcess = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.signingProcess', FailureHandling.OPTIONAL)
			
			   println(docId)
			   println(signingProcess)
	
			   if (GlobalVariable.checkStoreDB == 'Yes') {
				   
				   'declare arraylist arraymatch'
				   ArrayList<String> arrayMatch = []
				   
				   for(index = 0 ; index < docId.size() ; index++) {
					   
					   'get data from DB'
					   ArrayList<String> resultDB = CustomKeywords.'connection.APIFullService.getDocSignSequence'(conneSign, docId[index])
					   
					   arrayIndex = 0
					   
					   'verify doc ID'
					   arrayMatch.add(WebUI.verifyMatch(docId[index], resultDB[arrayIndex++],
							   false, FailureHandling.CONTINUE_ON_FAILURE))
					   
					   'verify status'
					   arrayMatch.add(WebUI.verifyMatch(signingProcess[index], resultDB[arrayIndex++],
							   false, FailureHandling.CONTINUE_ON_FAILURE))
				   }
				   
				   'jika data db tidak sesuai dengan excel'
				   if (arrayMatch.contains(false)) {
					   'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
					   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Doc Before Signing',
						   GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathCheckDocBeforeSigning).getValue(
							   GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
				   } else {
					   'write to excel success'
					   CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Doc Before Signing',
						   0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
				   }
			   }
			   
		   } else {
		        'call function get error message API'
				getErrorMessageAPI(respon)
		   }	
	    } else {
			'call function get error message API'
			getErrorMessageAPI(responLogin)
	    }
    }
}

def getErrorMessageAPI(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'error_description', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Check Doc Before Signing', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}
