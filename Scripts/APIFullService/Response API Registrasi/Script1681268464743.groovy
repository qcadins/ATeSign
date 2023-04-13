import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'HIT API Login untuk token : invenditor@womf'
respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2,
				4), ('password') : findTestData('Login/Login').getValue(3, 4)]))
'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
	'Parsing token menjadi GlobalVariable'
	GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
	
	println(GlobalVariable.token)
}
'get colm excel'
int countColmExcel = findTestData(excelPathAPIRegistrasi).getColumnNumbers()

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	} else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'HIT API'
		respon = WS.sendRequest(findTestObject('APIFullService/Postman/Register', [('callerId') : findTestData(excelPathAPIRegistrasi).getValue(
						GlobalVariable.NumofColm, 9), ('nama') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						11), ('email') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						12), ('tmpLahir') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						13), ('tglLahir') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						14), ('jenisKelamin') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						15), ('tlp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						16), ('idKtp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						17), ('alamat') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						18), ('kecamatan') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						19), ('kelurahan') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						20), ('kota') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						21), ('provinsi') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						22), ('kodePos') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						23), ('selfPhoto') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						24), ('idPhoto') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						25), ('password') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						26)]))
		
		'Jika status HIT API 200 OK'
		 if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
		          'mengambil status code berdasarkan response HIT API'
		          status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
		
		}
	}
}