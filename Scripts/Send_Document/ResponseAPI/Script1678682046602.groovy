import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager as WSResponseManager
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'Split mengenai signer 1 dan signer 2'
ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,27).split(';',-1)
ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,28).split(';',-1)
ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,29).split(';',-1)
ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,30).split(';',-1)
ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,31).split(';',-1)
ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,32).split(';',-1)
ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,33).split(';',-1)
ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,34).split(';',-1)
ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,35).split(';',-1)
ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,36).split(';',-1)
ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,37).split(';',-1)
ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,38).split(';',-1)
ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,39).split(';',-1)
ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,40).split(';',-1)
ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,41).split(';',-1)
ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,42).split(';',-1)
ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,43).split(';',-1)

	for(GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers() ; GlobalVariable.NumofColm++) {
	respon = WS.sendRequest(findTestObject('Object Repository/Postman/Send Document',
	 		[
			'tenantCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,9),
			'referenceNo' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,11), 
            'documentId' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,12),
            'documentTemplateCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,13),
            'officeCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,14),
            'officeName' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,15),
            'regionCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,16),
            'regionName' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,17),
            'businessLineCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,18),
            'businessLineName' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,19),
            'branch' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,20),
            'isSequence' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,21),

			's1.signAction' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,27).split(';').first(),
			's1.signerType' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,28).split(';').first(),
			's1.signSequence' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,29).split(';').first(),
			's1.alamat' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,30).split(';').first(),
			's1.jenisKelamin' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,31).split(';').first(),
			's1.kecamatan' :findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,32).split(';').first(),
			's1.kelurahan' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,33).split(';').first(),
			's1.kodePos' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,34).split(';').first(),
			's1.kota' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,35).split(';').first(),
			's1.nama' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,36).split(';').first(),
			's1.tlp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,37).split(';').first(),
			's1.tglLahir' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,38).split(';').first(),
			's1.provinsi' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,39).split(';').first(),
			's1.idKtp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,40).split(';').first(),
			's1.tmpLahir' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,41).split(';').first(),
			's1.email' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,42).split(';').first(),
			's1.npwp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,43).split(';').first(),
			's1.idPhoto' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,44),
			's1.signerSelfPhoto' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,45),
				
			's2.signAction' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,27).split(';').last(),
			's2.signerType' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,28).split(';').last(),
			's2.signSequence' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,29).split(';').last(),
			's2.alamat' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,30).split(';').last(),
			's2.jenisKelamin' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,31).split(';').last(),
			's2.kecamatan' :findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,32).split(';').last(),
			's2.kelurahan' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,33).split(';').last(),
			's2.kodePos' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,34).split(';').last(),
			's2.kota' :findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,35).split(';').last(),
			's2.nama' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,36).split(';').last(),
			's2.tlp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,37).split(';').last(),
			's2.tglLahir' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,38).split(';').last(),
			's2.provinsi' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,39).split(';').last(),
			's2.idKtp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,40).split(';').last(),
			's2.tmpLahir' :findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,41).split(';').last(),
			's2.email' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,42).split(';').last(),
			's2.npwp' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,43).split(';').last(),
			's2.idPhoto' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,44),
			's2.signerSelfPhoto' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,45),
			
			'psreCode' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,23),
			'successURL' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,24),
			'uploadURL' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,25),
			'callerId' : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,47)		
			 ]))

	'jika response 200 / hit api berhasil'
	if(WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
		
	'ambil respon text dalam bentuk code.'
	status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
	
	'jika status codenya 0, verifikasi datanya benar'
	if (status_Code == 0) {
		documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL).toString()
		GlobalVariable.documentId = documentId
		'write to excel success'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 0, GlobalVariable.NumofColm -
			1, GlobalVariable.StatusSuccess)
		
		if(GlobalVariable.checkStoreDB == 'Yes') {
			'call test case ResponseAPIStoreDB'
			WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
				FailureHandling.CONTINUE_ON_FAILURE)
		}
		
	'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
	} else {
		messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()
		'write to excel status failed dan reason'
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + messageFailed)
	}
	}
}
