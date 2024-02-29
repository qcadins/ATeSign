import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathGetInvData).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGetInvData, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathGetInvData).getValue(
                        GlobalVariable.NumofColm, rowExcel('username')), ('password') : findTestData(excelPathGetInvData).getValue(
                        GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Get Data Invitation Registration', [('callerId') : findTestData(
                            excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('loginid') : findTestData(
                            excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Email'))]))

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
						excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                'jika status codenya 0'
                if (statusCode == 0) {
                    if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                        'declare arraylist arraymatch'
                        ArrayList arrayMatch = []

                        'get data store db'
                        ArrayList result = CustomKeywords.'connection.APIFullService.getDataInvRegist'(conneSign, GlobalVariable.Tenant.replace(
                                '"', ''), findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Email')).replace('"', ''))

                        'declare arrayindex'
                        arrayindex = 0

                        'verify provinsi'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].provinsi', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kota'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].kota', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kecamatan'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].kecamatan', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify email'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].email', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify is active'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].isActive', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify invitation by'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].invBy', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify recieverDetail'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].recieverDetail', 
                                    FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify creation time'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].invCrt', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify resendActivationLinkStatus'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].resendActivationLinkStatus', 
                                    FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify vendorName'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].vendorName', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify vendorCode'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].vendorCode', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify isEditable'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].isEditable', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify isRegenerable'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].isRegenerable', 
                                    FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kelurahan'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].kelurahan', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kodePos'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].kodePos', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify nama'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].nama', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify alamat'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].alamat', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify jenisKelamin'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].jenisKelamin', 
                                    FailureHandling.OPTIONAL), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify tlp'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].tlp', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify tmpLahir'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].tmpLahir', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify tglLahir'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].tglLahir', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify idKtp'
                        arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].idKtp', FailureHandling.OPTIONAL), 
                                result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'jika data db tidak sesuai dengan excel'
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, 
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
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
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
        ((findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + 
        message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

