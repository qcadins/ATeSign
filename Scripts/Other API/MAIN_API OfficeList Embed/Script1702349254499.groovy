import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.text.SimpleDateFormat

import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'get tenant per case dari colm excel'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
		'get aesKet Tenant'
		aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

		def currentDate = new Date()

		def dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

		def timestamp = dateFormat.format(currentDate)

		if (aesKey.toString() != 'null') {
			'pembuatan message yang akan dienkrip'
			msg = (((((("{'officeCode':'" + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('officeCode'))) + "','email':'") + findTestData(excelPath).getValue(GlobalVariable.NumofColm,
				rowExcel('email'))) + "','timestamp':'") + timestamp) + "'}")

			if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'No') {
				'officecode + email + time stamp tanpa encrypt'
				endcodedMsg = msg
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'Yes') {
				'encrypt and decode officecode + email + time stamp'
				endcodedMsg = encryptEncodeValue(msg, aesKey)
			}
		} else {
			endcodedMsg = ''
		}
		
            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/OfficeList Embed', [('callerId') : (findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('callerId'))), ('msg') : endcodedMsg]))

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
                'mengambil status code berdasarkan response HIT API'
                status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

                'jika status codenya 0'
                if (status_Code == 0) {
                    resultOfficeCode = WS.getElementPropertyValue(respon, 'officeList.officeCode', FailureHandling.OPTIONAL)

                    resultOfficeName = WS.getElementPropertyValue(respon, 'officeList.officeName', FailureHandling.OPTIONAL)

                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'declare arraylist arraymatch'
                        ArrayList arrayMatch = []

                        'get data store db'
                        ArrayList result = CustomKeywords.'connection.APIFullService.getOfficeList'(conneSign, '')

                        'declare arrayindex'
                        arrayindex = 0

                        for (index = 0; index < (result.size() / 2); index++) {
							
							'encrypt office code'
							encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(result[arrayindex++], aesKey)
							
                            'verify office code'
                            arrayMatch.add(WebUI.verifyMatch(encryptMsg, resultOfficeCode[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify office name'
                            arrayMatch.add(WebUI.verifyMatch(result[arrayindex++], resultOfficeName[index], false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
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
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                        '') + ';') + GlobalVariable.ReasonFailedHitAPI)
            }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptEncodeValue(String value, String aesKey) {
	'enkripsi msg'
	encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)

	println(encryptMsg)

	try {
		return URLEncoder.encode(encryptMsg, StandardCharsets.UTF_8.toString())
	}
	catch (UnsupportedEncodingException ex) {
		throw new RuntimeException(ex.getCause())
	}
}