import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathGenerateLink).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGenerateLink, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

            'get tenant per case dari colm excel'
            GlobalVariable.Tenant = (('"' + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'tenantCode'))) + '"')
        
        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList listInvitation = []

        'Declare variable untuk sendRequest'
        (listInvitation[0]) = (((((((((((((((((((((((((((((((((((((((((((((((((((('{"email": "' + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('email'))) + '",') + '"tlp": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('tlp')).toString()) + '",') + '"jenisKelamin": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('jenisKelamin'))) + '",') + '"tmpLahir": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('tmpLahir'))) + '",') + '"tglLahir": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('tglLahir'))) + '",') + '"idKtp": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('idKtp'))) + '",') + '"provinsi": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('provinsi'))) + '",') + '"kota": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('kota'))) + '",') + '"kecamatan": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('kecamatan'))) + '",') + '"kelurahan": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('kelurahan'))) + '",') + '"kodePos": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('kodePos'))) + '",') + '"alamat": "') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('alamat'))) + '",') + '"selfPhoto": "",') + '"idPhoto": "",') + '"region": "') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Wilayah'))) + '",') + '"office": "') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Office'))) + '",') + '"businessLine": "') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Lini Bisnis'))) + '",') + '"taskNo": "') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Task No'))) + '",') + '"nama": "') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('nama'))) + '"}')

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('username')), ('password') : findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Generate Invitation Link Secured', [('callerId') : ('"' + findTestData(
                            excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))) + '"', ('users') : listInvitation[
                        0]]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'mengambil status code berdasarkan response HIT API'
                statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = (respon.elapsedTime / 1000) + ' second'
	
				'ambil body dari hasil respons'
				responseBody = respon.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                'jika status codenya 0'
                if (statusCode == 0) {
                    'Mengambil links berdasarkan response HIT API'
                    links = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL)

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    'write to excel link dari generate invitation'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Links') - 1, GlobalVariable.NumofColm - 1, links.toString().replace('[', '').replace(']', ''))

                    if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                        'call test case ResponseAPIStoreDB'
                        WebUI.callTestCase(findTestCase('null'), [('excelPathGenerateLink') : excelPathGenerateLink
                                , ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
                    }
                } else {
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
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
        ((findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + 
        message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

