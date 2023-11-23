import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.util.regex.Matcher
import java.util.regex.Pattern

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

String otp

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		GlobalVariable.FlagFailed = 0
		
		'check if tidak mau menggunakan vendor code yang benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'No') {
			'set vendor kosong'
			GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Vendor Code'))
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'Yes') {
			'get vendor per case dari colm excel'
			GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code'))
		}
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}
		
		'check if tidak mau menggunakan OTP yang benar'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct OTP')) == 'No') {
			'set otp salah'
			otp = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP'))
		} else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct OTP')) == 'Yes') {
			'get otp dari DB'
			otp = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email')))
		}
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'inisialisasi arrayList'
		ArrayList documentId = [], list = [], listDocId = []
	
		'Mengambil document id dari excel dan displit'
		documentId = findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Document Hash')).split(';', -1)
	
		for (int q = 0; q < documentId.size(); q++) {
			list.add('"' + documentId.get(q) + '"');
			
			if (q == 0) {
				listDocId.add(list.get(q));
			} else {
				listDocId.set(0, listDocId.get(0) + "," + list.get(q));
			}
		}
		
		'ubah menjadi string'
		String listDoc = listDocId.toString().replace('[','').replace(']','')
		
		'HIT API send otp ke email invitasi'
		respon = WS.sendRequest(findTestObject('Postman/API Signing Hash File', [
			('callerId') : ('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))) + '"',
			('loginId') : ('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email'))) + '"',
			('otp') : ('"' + otp + '"'),
				('documentHash') : listDoc]))

		'ambil lama waktu yang diperlukan hingga request menerima balikan'
		def elapsedTime = (respon.getElapsedTime()) / 1000 + ' second'
		
		'ambil body dari hasil respons'
		responseBody = respon.getResponseBodyContent()
		
		'panggil keyword untuk proses beautify dari respon json yang didapat'
		CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
			findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
		
		'write to excel response elapsed time'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm -
			1, elapsedTime.toString())
		
		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			'get Status Code'
			status_Code = WS.getElementPropertyValue(respon, 'status.code')

			'Jika status codenya 0'
			if (status_Code == 0) {
				
				'ambil data sesuai dengan jumlah document hash yang diinput'
				for (i = 0; i < listDocId.size(); i++) {
					
					'ambil respon sign hash dari array'
					signedHash = WS.getElementPropertyValue(respon, 'hashSignResult[' + i + '].signedHash')
					
					'ambil respon trxNum dari array'
					trxNo = WS.getElementPropertyValue(respon, 'hashSignResult[' + i + '].trxNo')
					
					'ambil respon signerID dari array'
					signerID = WS.getElementPropertyValue(respon, 'hashSignResult[' + i + '].signerId')
					
					'Menarik trxNo ke excel'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNo') - 
						1, GlobalVariable.NumofColm - 1, (((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
							'trxNo')) + ';') + '<') + trxNo) + '>')
					
					'Menarik signerID ke excel'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('SignerID') -
						1, GlobalVariable.NumofColm - 1, (((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
							'SignerID')) + ';') + '<') + trxNo) + '>')
					
					'Menarik signedHash ke excel'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('SignedHash') -
						1, GlobalVariable.NumofColm - 1, (((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
							'SignedHash')) + ';') + '<') + trxNo) + '>')
				}
				
				'write to excel success'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
					1, GlobalVariable.StatusSuccess)
				
			} else {
				getErrorMessageAPI(respon)
			}
		} else {
			getErrorMessageAPI(respon)
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

def parseCodeOnly(String url) {

	'ambil data sesudah "code="'
	Pattern pattern = Pattern.compile("code=([^&]+)")
	
	'ambil matcher dengan URL'
	Matcher matcher = pattern.matcher(url)
	
	'cek apakah apttern nya sesuai'
	if (matcher.find()) {
		'ubah jadi string'
		String code = matcher.group(1).replace('%3D','=')
		
		return code
	} else {
		
		return ''
	}
}

def decryptLink(Connection conneSign, String invCode) {
	aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)
	
	'enkripsi msg'
	encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(invCode, aesKey)
 
	return encryptMsg
}