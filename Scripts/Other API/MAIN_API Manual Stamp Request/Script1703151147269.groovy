import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
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
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Code'))
        }
        
        'HIT API Login untuk ambil bearer token'
        respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

			String stampLocBody = '', documentFile = ''
			
			'Inisialisasi documentFile'
			if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Base64')) ==
			'Yes') {
				documentFile = CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPath).getValue(
						GlobalVariable.NumofColm, rowExcel('docFile')))
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Base64')) ==
			'No') {
				documentFile = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docFile'))
			}
			
			'Inisialisasi stampPage'
			stampPage = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('stampPage')).split('\\n', -1)
			
			'Inisialisasi transform'
			transform = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('transform')).split('\\n', -1)
			
			'Inisialisasi notes'
			notes = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('notes')).split('\\n', -1)
			
			'Inisialisasi stampLocation'
			stampLocation = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('stampLocation')).split('\\n', -1)
			
			'Inisialisasi positionPrivy'
			positionPrivy = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('positionPrivy')).split('\\n', -1)
			
			for (index = 1; index <= stampPage.size(); index++) {
				if (index < stampPage.size()) {
					stampLocBody = stampLocBody + '{ "stampPage": "' + stampPage[index-1] + '", "transform": "' + transform[index-1] + '", "notes": "' + notes[index-1] + '", "stampLocation": ' + stampLocation[index-1] + ', "positionPrivy": "' + positionPrivy[index-1] +'"},'
				} else if (index == stampPage.size()) {
					stampLocBody = stampLocBody + '{ "stampPage": "' + stampPage[index-1] + '", "transform": "' + transform[index-1] + '", "notes": "' + notes[index-1] + '", "stampLocation": ' + stampLocation[index-1] + ', "positionPrivy": "' + positionPrivy[index-1] +'"}'
				}
			}
			
            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Manual Stamp Request', [('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('username')), ('refNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('refNo')), ('docName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('docName')), ('docDate') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('docDate')), ('peruriDocType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('peruriDocType')), ('docType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('docType')), ('docFile') : documentFile, ('stampLoc') : stampLocBody]))

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

//                    if (GlobalVariable.checkStoreDB == 'Yes') {}
                    
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
        } else {
            getErrorMessageAPI(respon_login)
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

