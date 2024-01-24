import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.testobject.ResponseObject

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

documentId = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

documentIdInput = '"documentId":"' + documentId[0] + '",'

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

ArrayList totalSignedBefore = [], totalSignedAfter = []

HashMap<String, String> saldoBefore = WebUI.callTestCase(findTestCase('null'), [('excel') : API_Excel_Path
	, ('sheet') : sheet, ('vendor') : vendor, ('usageSaldo') : 'Sign'], FailureHandling.CONTINUE_ON_FAILURE)

ttdBefore = saldoBefore.get('TTD')

emailSigner = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email (Sign Normal)')).split(';', -1)

'mengambil isi email signer berdasarkan excel dan di split.'
emailSignerInput = '"email": ' + emailSigner[GlobalVariable.indexUsed] + ','

'Mengambil aes key based on tenant tersebut'
String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

msg = encryptLink(conneSign, documentId[0].toString(), emailSigner[GlobalVariable.indexUsed], aesKey)

'HIT API Login untuk token : andy@ad-ins.com'
respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : emailSigner[GlobalVariable.indexUsed].toString().replace('"', '')
            , ('password') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer'))]))

'Jika status HIT API Login 200 OK'
if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
    'Parsing token menjadi GlobalVariable'
    GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

	'looping berdasarkan ukuran dari dokumen id'
	for (int z = 0; z < documentId.size(); z++) {
		'Memasukkan input dari total signed'
		(totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, (documentId[z]).toString())
	}
	
    'HIT API Sign Document'
    respon_signdoc = WS.sendRequest(findTestObject('Postman/Sign Doc', [('callerId') : ('"' + findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId (Sign Normal)')).split(';', -1)[GlobalVariable.indexUsed] ) + '"', ('email') : emailSignerInput, ('documentId') : documentIdInput, ('msg') : ('"' + 
                msg) + '"']))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon_signdoc, 200, FailureHandling.OPTIONAL) == true) {
        'get Status Code'
        status_Code = WS.getElementPropertyValue(respon_signdoc, 'status.code')

        'Jika status codenya 0'
        if (status_Code == 0) {
            'get vendor Code'
            GlobalVariable.Response = WS.getElementPropertyValue(respon_signdoc, 'vendorCode')

			'Loop berdasarkan jumlah documen id'
			for (int x = 0; x < documentId.size(); x++) {
				signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, documentId[x].toString(), emailSigner[GlobalVariable.indexUsed].replace('"', ''))
	
				'Loop untuk check db update sign. Maksimal 200 detik.'
				for (int v = 1; v <= 20; v++) {
					'Mengambil total Signed setelah sign'
					(totalSignedAfter[x]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[x])

					'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
					if ((totalSignedAfter[x]) == ((totalSignedBefore[x]) + Integer.parseInt(signCount))) {
						WebUI.verifyEqual(totalSignedAfter[x], (totalSignedBefore[x]) + Integer.parseInt(signCount), FailureHandling.CONTINUE_ON_FAILURE)
	
						if (GlobalVariable.FlagFailed == 0) {
						'write to excel success'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
							0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
						}
			
						'Write to excel mengenai Document ID'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Success') - 1, GlobalVariable.NumofColm - 1, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Result Count Success')) + ';' + 'Success : 1')
						
						'Write to excel mengenai Document ID'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Failed') - 1, GlobalVariable.NumofColm - 1, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Result Count Failed')) + ';' + 'Failed : 0')

						'check Db'
						if (GlobalVariable.checkStoreDB == 'Yes') {
							'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
							
							'Memanggil testCase mengenai SignDocumentStoreDb'
							WebUI.callTestCase(findTestCase('null'), [('API_Excel_Path') : API_Excel_Path, ('sheet') : sheet],
								FailureHandling.CONTINUE_ON_FAILURE)
						}

						HashMap<String, String> saldoAfter = WebUI.callTestCase(findTestCase('null'), [('excel') : API_Excel_Path
							, ('sheet') : sheet, ('vendor') : vendor, ('usageSaldo') : 'Sign'], FailureHandling.CONTINUE_ON_FAILURE)
						
						ttdAfter = saldoAfter.get('TTD')
				
					'check saldo before dan aftar'
					if (ttdBefore == ttdAfter) {
						'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedNoneUI) + ' terhadap pemotongan saldo ')
					} else {
						verifySaldoSigned(conneSign, documentId[0])
					}
						break
					} else if (v == 20) {
						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu 200 detik ')
	
						GlobalVariable.FlagFailed = 1
					} else {
						'Delay 10 detik.'
						WebUI.delay(10)
					}
					
					'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
					WebUI.callTestCase(findTestCase('null'), [('excelPathFESignDocument') : API_Excel_Path
							, ('sheet') : sheet, ('linkDocumentMonitoring') : 'Not Used', ('nomorKontrak') : refNumber, ('CancelDocsSign') : CancelDocsSign],
						FailureHandling.CONTINUE_ON_FAILURE)
				}
			}
        } else {
			getErrorMessageAPI(respon_signdoc)
        }
    } else {
		getErrorMessageAPI(respon_signdoc)
    }
} else {
    'write to excel status failed dan reason : failed hit api'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + GlobalVariable.ReasonFailedHitAPI)

    'close browser'
    WebUI.closeBrowser()
}

def getErrorMessageAPI(ResponseObject respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + '>')

	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptLink(Connection conneSign, String documentId, String emailSigner, String aesKey) {
	officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant')))
	
	'pembuatan message yang akan dienkrip'
	msg = '{"tenantCode":"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant')) + '","officeCode":"' + officeCode + '","email":"' + emailSigner + '"}'
	
	'enkripsi msg'
	encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(msg, aesKey)

	encryptMsg
}