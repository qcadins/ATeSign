import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        'get tenant yang ditesting dari excel per colm'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

		'get office code dari db'
		officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm,
				rowExcel('Document ID')))

		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Embed Version')).equalsIgnoreCase('V2')) {
			'get aesKet Tenant'
			aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

			currentDate = LocalDateTime.now()

			localeIndonesia = new Locale('id', 'ID')

			formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss', localeIndonesia)

			formattedDate = currentDate.format(formatter)

			'pembuatan message yang akan dienkrip'
			msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm,
				rowExcel('email'))) + '\',\'timestamp\':\'') + formattedDate) + '\'}')

			url = 'embed/document/signConfirmDocument'
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Embed Version')).equalsIgnoreCase(
			'V1')) {
			'get aesKet general'
			aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

			'pembuatan message yang akan dienkrip'
			msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm,
				rowExcel('email'))) + '\',\'tenantCode\':\'') + GlobalVariable.Tenant) + '\'}')

			url = 'document/signConfirmDocument'
		}
		
		if (aesKey.toString() != 'null') {
			if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'No') {
				'officecode + email + time stamp tanpa encrypt'
				endcodedMsg = msg
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'Yes') {
				'encrypt and decode officecode + email + time stamp'
				endcodedMsg = encryptEncodeValue(msg, aesKey)
			}
			
			if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) == 'No') {
				'get document id polos dari excel'
				endcodedDocumentId = '"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Document ID')) + '"'
			} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) ==
			'Yes') {
				'encrypt document id'
				endcodedDocumentId = '"' + encryptEncodeValue(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
							'Document ID')), aesKey) + '"'
			}
		} else {
			endcodedMsg = ''
			endcodedDocumentId = ''
		}
        
        'HIT API Sign Document'
        responConfirmSignDoc = WS.sendRequest(findTestObject('Postman/Confirm Sign Document Embed', [('callerId') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('msg') : endcodedMsg, ('documentId') : endcodedDocumentId, ('ipAddress') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('ipAddress')), ('browser') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('browser')), ('url') : url]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responConfirmSignDoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(responConfirmSignDoc, 'status.code')

			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			elapsedTime = ((responConfirmSignDoc.elapsedTime / 1000) + ' second')
	
			'ambil body dari hasil respons'
			responseBody = responConfirmSignDoc.responseBodyContent
	
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
					excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
			
            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(responConfirmSignDoc)
            }
        } else {
            getErrorMessageAPI(responConfirmSignDoc)
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

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

