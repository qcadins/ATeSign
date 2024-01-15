import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

String pagenum, isOffice

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))
        
        'isoffice jika kosong akan dikirim sebagai empty string'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isOffice (emptiable)')).length() > 0) {
            'set pagenum tanpa kutip'
            isOffice = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isOffice (emptiable)'))
        } else {
            'set pagenum dengan kutip'
            isOffice = '""'
        }
        
        'page num jika kosong akan dikirim sebagai empty string'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('PageNum(angka/kosongkan)')).length() > 
        0) {
            'set pagenum tanpa kutip'
            pagenum = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('PageNum(angka/kosongkan)'))
        } else {
            'set pagenum dengan kutip'
            pagenum = '""'
        }
        
        'HIT API Login untuk token : andy@ad-ins.com'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [
						('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')), 
						('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API Inquiry TTD'
            responInquiry = WS.sendRequest(findTestObject('Postman/Inquiry', [
						('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')),
						('transactionStatus') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Transaction Status (emptiable)')), 
						('pageNum') : pagenum, 
						('inquiryType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('inquiry Type (emptiable)')), 
						('booleanisOffice') : isOffice]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responInquiry, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responInquiry, 'status.code')

				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = (responInquiry.elapsedTime / 1000) + ' second'
	
				'ambil body dari hasil respons'
				responseBody = responInquiry.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                'Jika status codenya 0'
                if (statusCode == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                } else {
                    getErrorMessageAPI(responInquiry)
                }
            } else {
                getErrorMessageAPI(responInquiry)
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

