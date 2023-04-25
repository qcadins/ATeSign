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
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.dataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 2 /*findTestData(API_Excel_Path).getColumnNumbers()*/ ; (GlobalVariable.NumofColm)++) {
    ArrayList<String> referenceNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

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

    'split signer untuk doc1 dan signer untuk doc2'
    ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split('|', -1)

    ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split('|', -1)

    ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split('|', -1)

    ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split('|', -1)

    ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split('|', -1)

    ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split('|', -1)

    ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split('|', -1)

    ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split('|', -1)

    ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split('|', -1)

    ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split('|', -1)

    ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split('|', -1)

    ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split('|', -1)

    ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split('|', -1)

    ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split('|', -1)

    ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split('|', -1)

    ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split('|', -1)

    ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split('|', -1)

    ArrayList<String> idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split('|', -1)

    ArrayList<String> signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split('|', -1)

    ArrayList<String> documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(',', -1)

    (signAction[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(';', -1)

    (signerType[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(';', -1)

    (signSequence[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(';', -1)

    (alamat[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(';', -1)

    (jenisKelamin[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(';', -1)

    (kecamatan[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(';', -1)

    (kelurahan[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(';', -1)

    (kodePos[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(';', -1)

    (kota[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(';', -1)

    (nama[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

    (tlp[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(';', -1)

    (tglLahir[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(';', -1)

    (provinsi[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(';', -1)

    (idKtp[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(';', -1)

    (tmpLahir[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(';', -1)

    (email[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(';', -1)

    (npwp[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(';', -1)

    (idPhoto[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split(';', -1)

    (signerSelfPhoto[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split(';', -1)

    (documentFile[0]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(',', -1)

    (signAction[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(';', -1)

    (signerType[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(';', -1)

    (signSequence[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(';', -1)

    (alamat[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(';', -1)

    (jenisKelamin[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(';', -1)

    (kecamatan[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(';', -1)

    (kelurahan[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(';', -1)

    (kodePos[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(';', -1)

    (kota[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(';', -1)

    (nama[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

    (tlp[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(';', -1)

    (tglLahir[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(';', -1)

    (provinsi[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(';', -1)

    (idKtp[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(';', -1)

    (tmpLahir[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(';', -1)

    (email[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(';', -1)

    (npwp[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(';', -1)

    (idPhoto[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split(';', -1)

    (signerSelfPhoto[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split(';', -1)

    (documentFile[1]) = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(',', -1)

    'looping per document.'
    ArrayList<String> list = new ArrayList<String>()

    ArrayList<String> ListDocFile = new ArrayList<String>()

    ArrayList<String> ListSigner = new ArrayList<String>()

    (ListDocFile[0]) = ''

    (ListSigner[0]) = ''

    for (int y = 1; y <= referenceNo.size(); y++) {
        for (int i = 1; i <= signAction.size(); i++) {
            if (i == signAction.size()) {
                list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + ((signAction[(y - 1)])[(i - 1)])) + ',"signerType": ') + 
                    ((signerType[(y - 1)])[(i - 1)])) + ',"signSequence":') + ((signSequence[(y - 1)])[(i - 1)])) + ',"alamat": ') + 
                    ((alamat[(y - 1)])[(i - 1)])) + ',"jenisKelamin": ') + ((jenisKelamin[(y - 1)])[(i - 1)])) + ',"kecamatan": ') + 
                    ((kecamatan[(y - 1)])[(i - 1)])) + ',"kelurahan": ') + ((kelurahan[(y - 1)])[(i - 1)])) + ',"kodePos": ') + 
                    ((kodePos[(y - 1)])[(i - 1)])) + ',"kota": ') + ((kota[(y - 1)])[(i - 1)])) + ',"nama": ') + ((nama[
                    (y - 1)])[(i - 1)])) + ',"tlp": ') + ((tlp[(y - 1)])[(i - 1)])) + ',"tglLahir": ') + ((tglLahir[(y - 
                    1)])[(i - 1)])) + ',"provinsi": ') + ((provinsi[(y - 1)])[(i - 1)])) + ',"idKtp": ') + ((idKtp[(y - 
                    1)])[(i - 1)])) + ',"tmpLahir": ') + ((tmpLahir[(y - 1)])[(i - 1)])) + ',"email": ') + ((email[(y - 
                    1)])[(i - 1)])) + ',"npwp": ') + ((npwp[(y - 1)])[(i - 1)])) + ',"idPhoto": ') + ((idPhoto[(y - 1)])[
                    (i - 1)])) + ',"signerSelfPhoto": ') + ((signerSelfPhoto[(y - 1)])[(i - 1)])) + '}')
            }
            
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + ((signAction[(y - 1)])[(i - 1)])) + ',"signerType": ') + 
                ((signerType[(y - 1)])[(i - 1)])) + ',"signSequence":') + ((signSequence[(y - 1)])[(i - 1)])) + ',"alamat": ') + 
                ((alamat[(y - 1)])[(i - 1)])) + ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[
                (i - 1)])) + ',"kelurahan": ') + ((kelurahan[(y - 1)])[(i - 1)])) + ',"kodePos": ') + ((kodePos[(y - 1)])[
                (i - 1)])) + ',"kota": ') + ((kota[(y - 1)])[(i - 1)])) + ',"nama": ') + ((nama[(y - 1)])[(i - 1)])) + ',"tlp": ') + 
                ((tlp[(y - 1)])[(i - 1)])) + ',"tglLahir": ') + ((tglLahir[(y - 1)])[(i - 1)])) + ',"provinsi": ') + ((provinsi[
                (y - 1)])[(i - 1)])) + ',"idKtp": ') + ((idKtp[(y - 1)])[(i - 1)])) + ',"tmpLahir": ') + ((tmpLahir[(y - 
                1)])[(i - 1)])) + ',"email": ') + ((email[(y - 1)])[(i - 1)])) + ',"npwp": ') + ((npwp[(y - 1)])[(i - 1)])) + 
                ',"idPhoto": ') + ((idPhoto[(y - 1)])[(i - 1)])) + ',"signerSelfPhoto": ') + ((signerSelfPhoto[(y - 1)])[
                (i - 1)])) + '},')

            (ListSigner[0]) = ((ListSigner[0]) + (list[(i - 1)]))

            list.clear()
        }
        
        if (i == referenceNo.size()) {
            list.add(((((((((((((((((((((((((((('{"referenceNo" : ' + (referenceNo[(y - 1)])) + ', "documentTemplateCode": ') + 
                (documentTemplateCode[(y - 1)])) + ', "officeCode": ') + (officeCode[(y - 1)])) + ', "officeName": ') + 
                (officeName[(y - 1)])) + ', , "regionCode": ') + (regionName[(y - 1)])) + ', "regionName": ') + (regionName[
                (y - 1)])) + ', "businessLineCode": ') + (businessLineCode[(y - 1)])) + ', "businessLineName": ') + (businessLineName[
                (y - 1)])) + ', "isSequence": ') + (isSequence[(y - 1)])) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + 
                PDFtoBase64(documentFile[(y - 1)])) + '", "psreCode" : ') + (psreCode[(y - 1)])) + ', "successURL": ') + 
                (successURL[(y - 1)])) + ', "uploadURL": ') + (uploadURL[(y - 1)])) + ',}')
        }
        
        list.add(((((((((((((((((((((((((((('{"referenceNo" : ' + (referenceNo[(y - 1)])) + ', "documentTemplateCode": ') + 
            (documentTemplateCode[(y - 1)])) + ', "officeCode": ') + (officeCode[(y - 1)])) + ', "officeName": ') + (officeName[
            (y - 1)])) + ', , "regionCode": ') + (regionName[(y - 1)])) + ', "regionName": ') + (regionName[(y - 1)])) + 
            ', "businessLineCode": ') + (businessLineCode[(y - 1)])) + ', "businessLineName": ') + (businessLineName[(y - 
            1)])) + ', "isSequence": ') + (isSequence[(y - 1)])) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + 
            PDFtoBase64(documentFile[(y - 1)])) + '", "psreCode" : ') + (psreCode[(y - 1)])) + ', "successURL": ') + (successURL[
            (y - 1)])) + ', "uploadURL": ') + (uploadURL[(y - 1)])) + ',},')

        (ListDocFile[0]) = ((ListDocFile[0]) + (list[(i - 1)]))
    }
    
    respon = WS.sendRequest(findTestObject('Postman/Send Document docFile', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 9), ('request') : ListDocFile[0], ('callerId') : findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 45)]))

    'jika response 200 / hit api berhasil'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'ambil respon text dalam bentuk code.'
        status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'jika status codenya 0, verifikasi datanya benar'
        if (status_Code == 0) {
            documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL).toString()

            'masih ada [ ] nya dalam documentid'
            GlobalVariable.Response = documentId.replace('[', '').replace(']', '')

            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                4, GlobalVariable.NumofColm - 1, GlobalVariable.Response)

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
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
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
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
    return CustomKeywords.'customizeKeyword.convertFile.BASE64File'(fileName)
}

