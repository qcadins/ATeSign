import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

		String bodyAPI = ''
		
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

		'Jika flag tenant no'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
			'Input tenant'
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}

		'Jika dokumennya menggunakan base64'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 'Yes') {
			'input bodyAPI dengan Base64'
			bodyAPI =  PDFtoBase64(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')))
		} else {
			'input bodyAPI tidak dengan Base64'
			bodyAPI =  findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentFile'))
		}
		
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Insert Stamping Payment Receipt', [
					('documentTemplateCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), 
					('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')),
					('documentNumber') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentNumber')),
					('documentTransactionId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTransactionId')),
					('docName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docName')),
					('docDate') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docDate')),
					('docType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docType')),
					('docNominal') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('docNominal')),
					('peruriDocTypeId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('peruriDocTypeId')),
					('officeCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')),
					('officeName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('officeName')),
					('regionCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')),
					('regionName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('regionName')),
					('idType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('idType')),
					('idNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('idNo')),
					('taxOwedsName') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('taxOwedsName')),
					('returnStampResult') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('returnStampResult')),
					('documentFile') : bodyAPI]))

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
					excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
			
            'jika status codenya 0'
            if (statusCode == 0) {
                result = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

                'tulis sukses jika store DB berhasil'
                if ((GlobalVariable.FlagFailed == 0) && result.equalsIgnoreCase('Success')) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'call function get API error message'
                getErrorMessageAPI(respon)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + GlobalVariable.ReasonFailedHitAPI)
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
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)

    println(encryptMsg)

    try {
        return URLEncoder.encode(encryptMsg, StandardCharsets.UTF_8.toString())
    }
    catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex.cause)
    } 
}

def PDFtoBase64(String fileName) {
	return CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)
}
