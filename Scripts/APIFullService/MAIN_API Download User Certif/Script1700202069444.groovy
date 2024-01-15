import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIDownload).columnNumbers

'looping API Download User certif'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIDownload, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'get psre dari excel per case'
        GlobalVariable.Psre = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if tidak mau menggunakan psre code yang benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Psre Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Psre = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Psre Code'))
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Psre Code')) == 
        'Yes') {
            GlobalVariable.Psre = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API check Download User certif'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Download user Certificate', [('callerId') : findTestData(
                        excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('loginId') : findTestData(
                        excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Email')), ('noTelp') : findTestData(
                        excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('No Telp'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			elapsedTime = (respon.elapsedTime / 1000) + ' second'
	
			'ambil body dari hasil respons'
			responseBody = respon.responseBodyContent
	
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
					excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
			
            if (code == 0) {
                'mengambil response'
                base64CRT = WS.getElementPropertyValue(respon, 'userCertificate', FailureHandling.OPTIONAL)

                'decode Bas64 to File certif'
                CustomKeywords.'customizekeyword.ConvertFile.decodeBase64crt'(base64CRT, findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, rowExcel('File Name')))

                'check is file downloaded dan apakah mau di delete'
                if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, rowExcel('Delete File ?'))) == true) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                getErrorMessageAPI(respon)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
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

