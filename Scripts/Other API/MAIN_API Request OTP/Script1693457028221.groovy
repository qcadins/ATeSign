import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'call test case untuk ambil user access token'
WebUI.callTestCase(findTestCase('Other API/Response API User Access Token'), [('excelPath') : excelPathRequestOTP
        , ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

if (findTestData(excelPathRequestOTP).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Success')) {
    'Jika access token tidak yang sebenarnya atau berasal dari call test case user access token'
    if (findTestData(excelPathRequestOTP).getValue(GlobalVariable.NumofColm, 11) == 'No') {
        GlobalVariable.AccessToken = findTestData(excelPathRequestOTP).getValue(GlobalVariable.NumofColm, 12)
    }
    
    'HIT API'
    responConfirm = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API OTP Request', [('base_url') : GlobalVariable.base_url
                , ('merchant-key') : GlobalVariable.Merchantkey, ('access_token') : GlobalVariable.AccessToken]))

    'Jika status HIT API 200 atau 201'
    if (WS.verifyResponseStatusCodeInRange(responConfirm, 200, 202, FailureHandling.OPTIONAL) == true) {
        'get status code'
        code = WS.getElementPropertyValue(responConfirm, 'code', FailureHandling.OPTIONAL)

        'jika code yang didapat angka 200 atau 201'
        if ((code == 200) || (code == 201)) {
            'get message'
            message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

            'Jika message yang diapat itu berisi OTP sent to'
            if (message.toString().contains('OTP sent to ')) {
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            }
        }
    } else {
        'get message'
        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

        'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            '<' + message + '>')
    }
}

