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

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

'string otp'
String otp

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        GlobalVariable.FlagFailed = 0

		GlobalVariable.Tenant = CustomKeywords.'connection.APIFullService.getTenantCodeFromUser'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')))
		
		'get api key dari db'
		GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
	
        'request OTP dengan HIT API'

        'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
        respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : ('"' + findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))) + '"', ('phoneNo') : ('"' + 
                    findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo'))) + '"', ('email') : ('"' + 
                    findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email'))) + '"', ('refnumber') : '""'
                    , ('listDocumentId') : '""', ('vendor') : ('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('psreCode'))) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (code_otp == 0) {
                if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct OTP (From API) (FOR OUTLOOK EMAIL)')) == 
                'Yes') {
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')).contains('OUTLOOK.COM')) {
                        'call keyword get otp dari email'
                        otp = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('email')), findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('Password Outlook')), 'OTP')
                    } else {
                        'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP Privy via SMS.'
                        WebUI.delay(90)

                        otp = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP'))
                    }
                } else {
                    WebUI.delay(90)

                    otp = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP'))
                }
                
                'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
                respon = WS.sendRequest(findTestObject('Postman/Hash Sign', [('callerId') : findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('callerId')), ('email') : findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('email')), ('psreCode') : findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('psreCode')), ('otp') : otp, ('docSourceName') : findTestData(
                                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docSourceName')), ('docDestinationName') : findTestData(
                                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docDestinationName')), ('llx') : findTestData(
                                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('llx')), ('lly') : findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('lly')), ('urx') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                rowExcel('urx')), ('ury') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'ury')), ('page') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'page'))]))

                'ambil lama waktu yang diperlukan hingga request menerima balikan'
                def elapsedTime = (respon.getElapsedTime() / 1000) + ' second'

                'ambil body dari hasil respons'
                responseBody = respon.getResponseBodyContent()

                'panggil keyword untuk proses beautify dari respon json yang didapat'
                CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

                'write to excel response elapsed time'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                    1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                'Jika status HIT API 200 OK'
                if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                    'get status code'
                    code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

                    'jika codenya 0'
                    if (code == 0) {
                        println('success')
                    } else {
                        getErrorMessageAPI(respon)
                    }
                } else {
                    getErrorMessageAPI(respon)
                }
            } else {
                getErrorMessageAPI(respon_OTP)
            }
        } else {
            getErrorMessageAPI(respon_OTP)
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + ('<' + message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

