import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathUpdateData).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
	
		GlobalVariable.FlagFailed = 0
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathUpdateData, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
	
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = '"' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code')) + '"'
		} else if (findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
			'get tenant per case dari colm excel'
			GlobalVariable.Tenant = '"' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login')) + '"'
		}
		
        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList listInvitation = []

        'Declare variable untuk sendRequest'
		listInvitation[0] =
		'"oldRecieverDetail": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('old receiver detail')) + '",' +
		'"invitationBy": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('invitation by')) + '",' +
		'"address": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('alamat')) + '",' +
		'"dateOfBirth": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir')) + '",' +
		'"email": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('email')) + '",' +
		'"fullName": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('nama')) + '",' +
		'"gender": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('jenisKelamin')) + '",' +
		'"idNo": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('idKtp')) + '",' +
		'"kecamatan": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')) + '",' +
		'"kelurahan": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')) + '",' +
		'"kota": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('kota')) + '",' +
		'"phone": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).toString() + '",' +
		'"placeOfBirth": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir')) + '",' +
		'"provinsi": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')) + '",' +
		'"receiverDetail": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('receiver detail')) + '",' +
		'"zipCode": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('kodePos')) + '",' +
		'"vendorCode": "' + findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('vendorCode')) + '",'
		
		'HIT API Login untuk ambil bearer token'
		respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('username'))
					, ('password') : findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))
		
		'Jika status HIT API Login 200 OK'
		if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
			'Parsing token menjadi GlobalVariable'
			GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
			
			'HIT API'
			respon = WS.sendRequest(findTestObject('Postman/Update Data Invitation Regist', [('callerId') : '"' + findTestData(excelPathUpdateData).getValue(
						GlobalVariable.NumofColm, rowExcel('callerId')) + '"', ('updateData') : listInvitation[0]]))
			
			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			def elapsedTime = (respon.getElapsedTime()) / 1000 + ' second'
			
			'ambil body dari hasil respons'
			responseBody = respon.getResponseBodyContent()
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
			
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm -
				1, elapsedTime.toString())
			
			'Jika status HIT API 200 OK'
			if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
				'mengambil status code berdasarkan response HIT API'
				status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
	
				'jika status codenya 0'
				if (status_Code == 0) {
					
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
						1, GlobalVariable.StatusSuccess)
	
					if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
						'call test case ResponseAPIStoreDB'
						WebUI.callTestCase(findTestCase('Generate Invitation Link/ResponseAPIStoreDB'), [('excelPathGenerateLink') : excelPathUpdateData,
							('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
					}
				} else {
					'call function get API error message'
					getErrorMessageAPI(respon)
				}
			} else {
				'write to excel status failed dan reason : '
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
						'-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
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
		((findTestData(excelPathUpdateData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) +
		'>')

	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}