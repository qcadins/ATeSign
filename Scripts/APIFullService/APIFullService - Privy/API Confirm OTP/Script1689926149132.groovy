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
int countColmExcel = findTestData(excelPathConfirmOTP).columnNumbers

'looping API Sent OTP Signing'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        WebUI.callTestCase(findTestCase('APIFullService/APIFullService - Privy/Response API Sent OTP Signing'), [('excelPathAPISentOTPSigning') : excelPathConfirmOTP
                , ('sheet') : 'API Confirm OTP'], FailureHandling.CONTINUE_ON_FAILURE)

        if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Success')) {
            if (findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 19) == 'Yes') {
                'diberikan delay 40 detik untuk update db sesuai dengan otp code yang telah diterima'
                'Untuk privy, otp code tidak ada di db dan perlu diinput manual karena otp codenya ada di email'
                WebUI.delay(40)

                baseUrl = findTestData(excelPathConfirmOTP).getValue(2, 22)

                merchantKey = findTestData(excelPathConfirmOTP).getValue(2, 23)

                GlobalVariable.access_token = CustomKeywords.'connection.APIFullService.getVendorAccessToken'(conneSign, findTestData(excelPathConfirmOTP).getValue(
                        GlobalVariable.NumofColm, 11).toString().replace('"', ''))

                'HIT API'
                responConfirm = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Confirm OTP', [('base_url') : baseUrl
                            , ('merchant-key') : merchantKey, ('access_token') : accessToken, ('otpCode') : findTestData(excelPathConfirmOTP).getValue(GlobalVariable.NumofColm, 20)]))

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
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Confirm OTP', 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                            }
                        }
                    } else {
                        'get message'
                        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                        'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Confirm OTP', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, message)
                    }
                } else {
                        'get message'
                        message = WS.getElementPropertyValue(responConfirm, 'message', FailureHandling.OPTIONAL)

                        'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Confirm OTP', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, message)
                    }
            }
        }
    }
}

