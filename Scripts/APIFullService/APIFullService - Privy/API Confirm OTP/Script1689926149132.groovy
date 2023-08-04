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
int countColmExcel = findTestData(excelPathConfirmOTP).columnNumbers

'looping API Confirm OTP'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathConfirmOTP, GlobalVariable.NumofColm, 16)
		
        'call test case menuju Request OTP Privy'
        WebUI.callTestCase(findTestCase('APIFullService/APIFullService - Privy/API Request OTP'), [('excelPathRequestOTP') : excelPathConfirmOTP
                , ('sheet') : 'API Confirm OTP - Privy'], FailureHandling.CONTINUE_ON_FAILURE)

        'Jika request otpnya success'
        if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Success')) {
            if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
                'delay 40 detik untuk input otp code manual via SMS ke excel(otp privy tidak masuk ke db)'
                WebUI.delay(40)
            }
            
            'HIT API'
            responConfirm = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Confirm OTP', [('base_url') : GlobalVariable.base_url
                        , ('merchant-key') : GlobalVariable.Merchantkey, ('access_token') : GlobalVariable.AccessToken, ('otpCode') : findTestData(
                            excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 15)]))

            'Jika status HIT API 200 atau 201'
            if (WS.verifyResponseStatusCodeInRange(responConfirm, 200, 202, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code = WS.getElementPropertyValue(responConfirm, 'code', FailureHandling.OPTIONAL)

                if ((code == 200) || (code == 201)) {
                    'get message'
                    message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                    if (message.toString().contains('success')) {
                        if (GlobalVariable.FlagFailed == 0) {
                            'write to excel success'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Confirm OTP - Privy', 
                                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                        }
                    }
                }
            } else {
                'get message'
                message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Confirm OTP - Privy', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        }
    }
}

