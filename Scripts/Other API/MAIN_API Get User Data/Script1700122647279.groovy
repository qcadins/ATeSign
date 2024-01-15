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
		
		GlobalVariable.FlagFailed = 0
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

		'get vendor per case dari colm excel'
		GlobalVariable.Psre = '"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('vendorCode')) + '"'

		'get tenant per case dari colm excel'
		GlobalVariable.Tenant = '"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode')) + '"'
		
		'HIT API Login untuk token : andy@ad-ins.com'
		respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username'))
					, ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))
		
		'Jika status HIT API Login 200 OK'
		if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
		    'Parsing token menjadi GlobalVariable'
		    GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
			
			'HIT API Get User Data Document'
			responGetUserData = WS.sendRequest(findTestObject('Postman/Get User Data', [
				('callerId') : ('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))) + '"',
						('loginid') : ('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email'))) + '"']))
	
			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			def elapsedTime = (responGetUserData.getElapsedTime()) / 1000 + ' second'
			
			'ambil body dari hasil respons'
			responseBody = responGetUserData.getResponseBodyContent()
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1,
				findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
			
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 1, GlobalVariable.NumofColm -
				1, elapsedTime.toString())
			
			'Jika status HIT API 200 OK'
			if (WS.verifyResponseStatusCode(responGetUserData, 200, FailureHandling.OPTIONAL) == true) {
				'get Status Code'
				status_Code = WS.getElementPropertyValue(responGetUserData, 'status.code')
	
				'Jika status codenya 0'
				if (status_Code == 0) {
				  
					'lakukan cek db jika diperlukan'
					if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
						'declare arraylist arraymatch'
						ArrayList arrayMatch = []
						
						'get data store db'
						ArrayList result = CustomKeywords.'connection.PencarianPengguna.getUserDataAPI'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email')))
						
						'declare arrayindex'
						arrayindex = 0
						
						'verify provinsi'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.provinsi', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify kota'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.kota', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify kecamatan'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.kecamatan', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify loginId'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.loginId', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify email'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.email', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify aktivasi status'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.status', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify autosignStatus'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.autosignStatus', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify certExp'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.certExp', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify kelurahan'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.kelurahan', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify kodePos'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.kodePos', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify nama'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.nama', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify alamat'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.alamat', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify jenisKelamin'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.jenisKelamin', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify tlp'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.tlp', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify tmpLahir'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.tmpLahir', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify tglLahir'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.tglLahir', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify idKtp'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'userData.idKtp', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'verify vendorCode'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(responGetUserData, 'vendorCode', FailureHandling.OPTIONAL),
								result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'jika data db tidak sesuai dengan excel'
						if (arrayMatch.contains(false)) {
							GlobalVariable.FlagFailed = 1
							
							'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
						}
					}
				  
					'tulis sukses jika store DB berhasil'
					if (GlobalVariable.FlagFailed == 0) {
						'write to excel success'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
							1, GlobalVariable.StatusSuccess)
					}
				} else {
					getErrorMessageAPI(responGetUserData)
				}
			} else {
				getErrorMessageAPI(responGetUserData)
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
