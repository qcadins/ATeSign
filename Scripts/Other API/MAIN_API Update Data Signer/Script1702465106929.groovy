import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk token : andy@ad-ins.com'
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
        
        'HIT API Login untuk token : andy@ad-ins.com'
        respon = WS.sendRequest(findTestObject('Postman/Update Data Signer', [('tenantCode') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('tenantCode')), ('callerId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('loginId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('loginId')), ('vendorCode') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('vendorCode')), ('fullName') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('fullName')), ('dateOfBirth') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('dateOfBirth')), ('noKtp') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('noKtp')), ('noPhone') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('noPhone')), ('newEmail') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('newEmail'))]))

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

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'ambil hasil db sebelum edit'
                    ArrayList result = CustomKeywords.'connection.EditSignerData.getUpdateDataSignerAPI'(conneSign, findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('loginId')), findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('tenantCode')), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('vendorCode')))

                    arrayIndex = 0

                    ArrayList arrayMatch = []

                    for (index = 0; index < (result.size() / 5); index++) {
                        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('fullName')) != '') {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('fullName')), false, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            arrayIndex++
                        }
                        
                        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('newEmail')) != '') {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('newEmail')), false, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('loginId')), false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('noPhone')) != '') {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('noPhone'))), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            arrayIndex++
                        }
                        
                        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('noKtp')) != '') {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('noKtp'))), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            arrayIndex++
                        }
                        
                        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('dateOfBirth')) != '') {
                            'verify full name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('dateOfBirth')), false, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            arrayIndex++
                        }
                    }
                    
					
					'get current date'
					String currentDate = new Date().format('yyyy-MM-dd')
					
					'access log pada edit signer data'
					ArrayList resultAccessLog = CustomKeywords.'connection.DataVerif.getAccessLog'(conneSign,
						'EDIT_SIGNER_DATA')

					'inisialisasi kepada array index pada access log'
					arrayIndexAccessLog = 0

					'array match kepada current date'
					arrayMatch.add(WebUI.verifyMatch(resultAccessLog[arrayIndexAccessLog++], currentDate, false,
							FailureHandling.CONTINUE_ON_FAILURE))

					'array match edit signer data'
					if (resultAccessLog[arrayIndexAccessLog++].toString().contains('Edit Signer Data')) {
						'setting array match true'
						arrayMatch.add(true)
					} else {
						'setting array match false'
						arrayMatch.add(false)
					}
					'array match kepada user create'
					arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toLowerCase(),
							findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
									'callerId')).toLowerCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
                
				'Jika flag failed kosong'
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
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

