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
import java.sql.Connection as Connection

WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.dataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++) {
    'Split mengenai signer 1 dan signer 2'
    ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(';', -1)

    ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(';', -1)

    ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(';', -1)

    ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(';', -1)

    ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(';', -1)

    ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(';', -1)

    ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(';', -1)

    ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

    ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(';', -1)

    ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(';', -1)

    ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(';', -1)

    ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(';', -1)

    ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(';', -1)

    ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(';', -1)

    ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(';', -1)

    ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split(';', -1)

    ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split(';', -1)

    'Pembuatan pengisian variable di sendRequest per jumlah signer.'
    ArrayList<String> list = new ArrayList<String>()

    ArrayList<String> ListSigner = new ArrayList<String>()

    (ListSigner[0]) = ''

    for (int i = 1; i <= signAction.size(); i++) {
        if (i == signAction.size()) {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') + 
                (signerType[(i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i - 
                1)])) + ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') + 
                (kelurahan[(i - 1)])) + ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') + 
                (nama[(i - 1)])) + ',"tlp": ') + (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') + 
                (provinsi[(i - 1)])) + ',"idKtp": ') + (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') + 
                (email[(i - 1)])) + ',"npwp": ') + (npwp[(i - 1)])) + ',"idPhoto": ') + findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 44)) + ',"signerSelfPhoto": ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                    45)) + '}')
        }
        
        list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') + 
            (signerType[(i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i - 1)])) + 
            ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') + 
            (kelurahan[(i - 1)])) + ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') + 
            (nama[(i - 1)])) + ',"tlp": ') + (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') + 
            (provinsi[(i - 1)])) + ',"idKtp": ') + (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') + 
            (email[(i - 1)])) + ',"npwp": ') + (npwp[(i - 1)])) + ',"idPhoto": ') + findTestData(API_Excel_Path).getValue(
                GlobalVariable.NumofColm, 44)) + ',"signerSelfPhoto": ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                45)) + '},')

        (ListSigner[0]) = ((ListSigner[0]) + (list[(i - 1)]))
    }
    
    respon = WS.sendRequest(findTestObject('Postman/Send Document', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 9), ('referenceNo') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                    11), ('documentId') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12), ('documentTemplateCode') : findTestData(
                    API_Excel_Path).getValue(GlobalVariable.NumofColm, 13), ('officeCode') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 14), ('officeName') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                    15), ('regionCode') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16), ('regionName') : findTestData(
                    API_Excel_Path).getValue(GlobalVariable.NumofColm, 17), ('businessLineCode') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 18), ('businessLineName') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                    19), ('branch') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20), ('isSequence') : findTestData(
                    API_Excel_Path).getValue(GlobalVariable.NumofColm, 21), ('deskripsi_signer') : ListSigner[0], ('psreCode') : findTestData(
                    API_Excel_Path).getValue(GlobalVariable.NumofColm, 23), ('successURL') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 24), ('uploadURL') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                    25), ('callerId') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 47)]))

    'jika response 200 / hit api berhasil'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'ambil respon text dalam bentuk code.'
        status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'jika status codenya 0, verifikasi datanya benar'
        if (status_Code == 0) {
            documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL).toString()

            GlobalVariable.documentId = documentId

            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
			
			if(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
	            'call test case error report'
	            WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
	                FailureHandling.STOP_ON_FAILURE)
			}
        }
    }
}

