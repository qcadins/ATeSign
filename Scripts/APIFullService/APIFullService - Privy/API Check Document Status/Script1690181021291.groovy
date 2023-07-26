import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services - Privy.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathCheckDocumentStatus).columnNumbers

'looping API Request OTP'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckDocumentStatus).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathCheckDocumentStatus).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'call function untuk Upload Document'
		uploadDocument()

        if (findTestData(excelPathCheckDocumentStatus).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Success')) {
                baseUrl = findTestData(excelPathCheckDocumentStatus).getValue(2, 14)

                merchantKey = findTestData(excelPathCheckDocumentStatus).getValue(2, 15)
				
				'Jika access token tidak yang sebenarnya atau berasal dari call test case user access token'
				if (findTestData(excelPathCheckDocumentStatus).getValue(GlobalVariable.NumofColm,11) == 'No') {
					GlobalVariable.AccessToken = findTestData(excelPathCheckDocumentStatus).getValue(GlobalVariable.NumofColm,12)
				}

                'HIT API'
                responConfirm = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API OTP Request', [('base_url') : baseUrl
                            , ('merchant-key') : merchantKey, ('access_token') : GlobalVariable.AccessToken]))

                'Jika status HIT API 200 atau 201'
                if (WS.verifyResponseStatusCodeInRange(responConfirm, 200, 202, FailureHandling.OPTIONAL) == true) {
                    'get status code'
                    code = WS.getElementPropertyValue(responConfirm, 'code', FailureHandling.OPTIONAL)

                    if ((code == 200) || (code == 201)) {
                        'get message'
                        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                        if (message.toString().contains('OTP sent to +62')) {
                            if (GlobalVariable.FlagFailed == 0) {
                                'write to excel success'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Request OTP', 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                            }
                        }
                    } else {
                        'get message'
                        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                        'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Request OTP', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, message)
                    }
                } else {
                        'get message'
                        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                        'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Request OTP', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, message)
                    }
            
        }
    }
}

def uploadDocument() {
	
}
