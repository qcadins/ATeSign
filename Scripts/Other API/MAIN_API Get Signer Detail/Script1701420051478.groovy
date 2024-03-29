import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get vendor per case dari colm excel'
        GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code'))
             
       'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        
        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API'
            responSignerDetail = WS.sendRequest(findTestObject('Postman/Get Signer Detail', [
						('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
						('loginId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email'))]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responSignerDetail, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responSignerDetail, 'status.code')
				
				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = (responSignerDetail.elapsedTime / 1000) + ' second'
	
				'ambil body dari hasil respons'
				responseBody = responSignerDetail.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                'Jika status codenya 0'
                if (statusCode == 0) {
                    if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                        'declare arraylist arraymatch'
                        ArrayList arrayMatch = []

                        'get data store db'
                        ArrayList result = CustomKeywords.'connection.APIFullService.getSignerDetailAPIOnly'(conneSign, 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email')))

                        'declare arrayindex'
                        arrayindex = 0

                        'verify phoneNo'
                        arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(WS.getElementPropertyValue(
                                        responSignerDetail, 'phoneNo', FailureHandling.OPTIONAL)), result[arrayindex++], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify email'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responSignerDetail, 'email', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify maxLivenessFaceCompareAttempt'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responSignerDetail, 'maxLivenessFaceCompareAttempt', 
                                    FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'jika data db tidak sesuai dengan excel'
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                        }
                    }
                    
                    'tulis sukses jika store DB berhasil'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    getErrorMessageAPI(responSignerDetail)
                }
            } else {
                getErrorMessageAPI(responSignerDetail)
            }
        } else {
            getErrorMessageAPI(responLogin)
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

