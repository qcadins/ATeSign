import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

'get privyId dari DB'
privyId = CustomKeywords.'connection.APIFullService.getPrivyId'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

'HIT API user access token'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/API User Access Token', [('privyId') : privyId]))

'Jika status HIT API 201 OK'
if (WS.verifyResponseStatusCode(respon, 201, FailureHandling.OPTIONAL) == true) {
    code = WS.getElementPropertyValue(respon, 'code', FailureHandling.OPTIONAL)

        'mengambil response'
        token = WS.getElementPropertyValue(respon, 'data.token', FailureHandling.OPTIONAL)

		'print token'
		println(token)
		
		'write to excel success'          
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing',
		      0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
		
} else {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'errors.messages', FailureHandling.OPTIONAL).toString().replace('[', '').replace(']', '')
	
    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, '<' + message + '>')
}

