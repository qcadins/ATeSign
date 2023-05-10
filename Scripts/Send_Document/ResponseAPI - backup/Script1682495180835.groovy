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

//WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)
'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.DataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 2 /*findTestData(API_Excel_Path).getColumnNumbers()*/; (GlobalVariable.NumofColm)++) {
    ArrayList<String> refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

    ArrayList<String> documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(';', 
        -1)

    ArrayList<String> officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).split(';', -1)

    ArrayList<String> officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).split(';', -1)

    ArrayList<String> regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).split(';', -1)

    ArrayList<String> regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).split(';', -1)

    ArrayList<String> businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).split(';', 
        -1)

    ArrayList<String> businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).split(';', 
        -1)

    ArrayList<String> isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

    ArrayList<String> psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).split(';', -1)

    ArrayList<String> successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).split(';', -1)

    ArrayList<String> uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).split(';', -1)

    ArrayList<String> documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(';', -1)

    'split signer untuk doc1 dan signer untuk doc2'
    ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split('\\|', -1)

    ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split('\\|', -1)

    ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split('\\|', -1)

    ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split('\\|', -1)

    ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split('\\|', -1)

    ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)

    ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)

    ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split('\\|', -1)

    ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split('\\|', -1)

    ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split('\\|', -1)

    ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split('\\|', -1)

    ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split('\\|', -1)

    ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split('\\|', -1)

    ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split('\\|', -1)

    ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split('\\|', -1)

    ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split('\\|', -1)

    ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split('\\|', -1)

    ArrayList<String> idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split('\\|', -1)

    ArrayList<String> signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split('\\|', 
        -1)

    String stringRefno = new String()

    for (int i = 0; i < refNo.size(); i++) {
        (signAction[i]) = (signAction[i]).split(';', -1)

        (signerType[i]) = (signerType[i]).split(';', -1)

        (signSequence[i]) = (signSequence[i]).split(';', -1)

        (alamat[i]) = (alamat[i]).split(';', -1)

        (jenisKelamin[i]) = (jenisKelamin[i]).split(';', -1)

        (kecamatan[i]) = (kecamatan[i]).split(';', -1)

        (kelurahan[i]) = (kelurahan[i]).split(';', -1)

        (kodePos[i]) = (kodePos[i]).split(';', -1)

        (kota[i]) = (kota[i]).split(';', -1)

        (nama[i]) = (nama[i]).split(';', -1)

        (tlp[i]) = (tlp[i]).split(';', -1)

        (tglLahir[i]) = (tglLahir[i]).split(';', -1)

        (provinsi[i]) = (provinsi[i]).split(';', -1)

        (idKtp[i]) = (idKtp[i]).split(';', -1)

        (tmpLahir[i]) = (tmpLahir[i]).split(';', -1)

        (email[i]) = (email[i]).split(';', -1)

        (npwp[i]) = (npwp[i]).split(';', -1)

        (idPhoto[i]) = (idPhoto[i]).split(';', -1)

        (signerSelfPhoto[i]) = (signerSelfPhoto[i]).split(';', -1)


        String string = new String()

        string = ''

        string = (string + (((((((((((((((((('{"referenceNo" : ' + (refNo[i])) + ', "documentTemplateCode": ') + (documentTemplateCode[
        i])) + ', "officeCode": ') + (officeCode[i])) + ', "officeName": ') + (officeName[i])) + ', "regionCode": ') + (regionCode[
        i])) + ', "regionName": ') + (regionName[i])) + ', "businessLineCode": ') + (businessLineCode[i])) + ', "businessLineName": ') + 
        (businessLineName[i])) + ', "isSequence": ') + (isSequence[i])) + ','))

        stringRefno = (stringRefno + string)

        string = new String()

        string = ''

        for (int t = 0; t < (signAction[i]).size(); t++) {
            if (t == ((signAction[i]).size() - 1)) {
                string = (string + (((((((((((((((((((((((((((((((((((((('{"signAction": ' + ((signAction[i])[t])) + ',"signerType": ') + 
                ((signerType[i])[t])) + ',"signSequence":') + ((signSequence[i])[(t)])) + ',"alamat": ') + ((alamat[
                i])[t])) + ',"jenisKelamin": ') + ((jenisKelamin[i])[t])) + ',"kecamatan": ') + ((kecamatan[i])[t])) + ',"kelurahan": ') + 
                ((kelurahan[i])[t])) + ',"kodePos": ') + ((kodePos[i])[t])) + ',"kota": ') + ((kota[i])[t])) + ',"nama": ') + 
                ((nama[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"tglLahir": ') + ((tglLahir[i])[t])) + ',"provinsi": ') + 
                ((provinsi[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"tmpLahir": ') + ((tmpLahir[i])[t])) + ',"email": ') + 
                ((email[i])[t])) + ',"npwp": ') + ((npwp[i])[t])) + ',"idPhoto": ') + ((idPhoto[i])[t])) + ',"signerSelfPhoto": ') + 
                ((signerSelfPhoto[i])[t])) + '}],'))
            } else if (t == 0) {
                string = (string + (((((((((((((((((((((((((((((((((((((('"signer" : [{"signAction": ' + ((signAction[i])[
                t])) + ',"signerType": ') + ((signerType[i])[t])) + ',"signSequence":') + ((signSequence[i])[t])) + ',"alamat": ') + 
                ((alamat[i])[t])) + ',"jenisKelamin": ') + ((jenisKelamin[i])[t])) + ',"kecamatan": ') + ((kecamatan[i])[
                t])) + ',"kelurahan": ') + ((kelurahan[i])[t])) + ',"kodePos": ') + ((kodePos[i])[t])) + ',"kota": ') + 
                ((kota[i])[t])) + ',"nama": ') + ((nama[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"tglLahir": ') + ((tglLahir[
                i])[t])) + ',"provinsi": ') + ((provinsi[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"tmpLahir": ') + 
                ((tmpLahir[i])[t])) + ',"email": ') + ((email[i])[t])) + ',"npwp": ') + ((npwp[i])[t])) + ',"idPhoto": ') + 
                ((idPhoto[i])[t])) + ',"signerSelfPhoto": ') + ((signerSelfPhoto[i])[t])) + '},'))
            } else {
                string = (string + (((((((((((((((((((((((((((((((((((((('{"signAction": ' + ((signAction[i])[t])) + ',"signerType": ') + 
                ((signerType[i])[t])) + ',"signSequence":') + ((signSequence[i])[t])) + ',"alamat": ') + ((alamat[i])[t])) + 
                ',"jenisKelamin": ') + ((jenisKelamin[i])[t])) + ',"kecamatan": ') + ((kecamatan[i])[t])) + ',"kelurahan": ') + 
                ((kelurahan[i])[t])) + ',"kodePos": ') + ((kodePos[i])[t])) + ',"kota": ') + ((kota[i])[t])) + ',"nama": ') + 
                ((nama[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"tglLahir": ') + ((tglLahir[i])[t])) + ',"provinsi": ') + 
                ((provinsi[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"tmpLahir": ') + ((tmpLahir[i])[t])) + ',"email": ') + 
                ((email[i])[t])) + ',"npwp": ') + ((npwp[i])[t])) + ',"idPhoto": ') + ((idPhoto[i])[t])) + ',"signerSelfPhoto": ') + 
                ((signerSelfPhoto[i])[t])) + '},'))
            }
            
            stringRefno = (stringRefno + string)

            string = ''
        }
        
        string = new ArrayList<String>()

        string = ''

        if (i == (refNo.size() - 1)) {
            string = ((string + ((((((('"documentFile": "' + PDFtoBase64(documentFile[i])) + '", "psreCode" : ') + (psreCode[
            i])) + ', "successURL": ') + (successURL[i])) + ', "uploadURL": ') + (uploadURL[i]))) + '}')
        } else {
            string = ((string + ((((((('"documentFile": "' + PDFtoBase64(documentFile[i])) + '", "psreCode" : ') + (psreCode[
            i])) + ', "successURL": ') + (successURL[i])) + ', "uploadURL": ') + (uploadURL[i]))) + '},')
        }
        
        stringRefno = (stringRefno + string)
    }
    
    respon = WS.sendRequest(findTestObject('Postman/Send Document', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 9), ('request') : stringRefno, ('callerId') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 45)]))

    'jika response 200 / hit api berhasil'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'ambil respon text dalam bentuk code.'
        status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'jika status codenya 0, verifikasi datanya benar'
        if (status_Code == 0) {
            documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL)

            'masih ada [ ] nya dalam documentid'
            GlobalVariable.Response = documentId

            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                4, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString())

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

            //WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('API_Excel_Path') : 'Registrasi/SendDocument'], FailureHandling.CONTINUE_ON_FAILURE)
            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)

            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
                'call test case error report'
                WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }
}

def PDFtoBase64(String fileName) {
    String base64 = CustomKeywords.'customizeKeyword.ConvertFile.base64File'(fileName)

    return base64
}

