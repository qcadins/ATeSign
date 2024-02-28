import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
        } else {
            getErrorMessageAPI(responLogin)
        }
        
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Token')) == 'No') {
            GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/View OTP', [('tenantCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('tenantCode')), ('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('callerId')), ('email') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'email')), ('ipAddress') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'ipAddress'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = ((respon.elapsedTime / 1000) + ' second')

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.FlagFailed == 0) {
                    'db hasil pencarian'
                    String result = CustomKeywords.'connection.ViewUserOTP.getOtpCode'(conneSign, findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('email')))

                    'get Status Code'
                    otpCode = WS.getElementPropertyValue(respon, 'otpCode', FailureHandling.OPTIONAL)
					
					'inisialisasi array match'
                    ArrayList arrayMatch = []

					'array match kepada otp code'
                    arrayMatch.add(WebUI.verifyMatch(result, otpCode, false, FailureHandling.CONTINUE_ON_FAILURE))

					'array match kepada view otp'
                    ArrayList resultAccessLog = CustomKeywords.'connection.DataVerif.getAccessLog'(conneSign, 'VIEW_OTP')

                    'get current date'
                    String currentDate = new Date().format('yyyy-MM-dd')

					'inisialisasi array index'
                    arrayIndexAccessLog = 0

					'array match kepada current date'
                    arrayMatch.add(WebUI.verifyMatch(resultAccessLog[arrayIndexAccessLog++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

					'array match kepada description'
                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toUpperCase(), 'VIEW OTP', false, 
                            FailureHandling.CONTINUE_ON_FAILURE))

					'array match kepada caller id'
                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toLowerCase(), 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')).toLowerCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

                        GlobalVariable.FlagFailed = 1
                    }
                    
					'jika flag failed tidak aktif'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

