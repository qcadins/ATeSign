import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathGetTempSign).columnNumbers

'looping API Download Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGetTempSign, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API get template sign loc'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Get Template Sign Location', [('callerId') : findTestData(
                        excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('docTempCode') : findTestData(excelPathGetTempSign).getValue(
                        GlobalVariable.NumofColm, rowExcel('documentTemplateCode'))]))
		
		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		def elapsedTime = (respon.getElapsedTime()) / 1000 + ' second'
		
		'ambil body dari hasil respons'
		responseBody = respon.getResponseBodyContent()
		
		'panggil keyword untuk proses beautify dari respon json yang didapat'
		CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
			findTestData(excelPathGetTempSign).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
		
		'write to excel response elapsed time'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm -
			1, elapsedTime.toString())

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                
				if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
					'declare arraylist arraymatch'
					ArrayList arrayMatch = []
					
					'get data store db'
					ArrayList result = CustomKeywords.'connection.APIFullService.getDataInvRegist'(conneSign, GlobalVariable.Tenant.replace('"', ''), findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm,
							rowExcel('Email')).replace('"', ''))
					
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
					arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].recieverDetail', FailureHandling.OPTIONAL),
						result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify creation time'
					arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].invCrt', FailureHandling.OPTIONAL),
						result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify resendActivationLinkStatus'
					arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].resendActivationLinkStatus', FailureHandling.OPTIONAL),
						result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
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
					arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].isRegenerable', FailureHandling.OPTIONAL),
						result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
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
					arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'listUser[0].jenisKelamin', FailureHandling.OPTIONAL),
						result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
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
							GlobalVariable.StatusFailed, (findTestData(excelPathGetInvData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
					}
				}
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}