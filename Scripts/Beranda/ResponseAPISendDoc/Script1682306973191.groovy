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
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.dataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

'Inisialisasi seluruh data yang dibuuthkan'
String refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11)

String documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12)

String officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13)

String officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14)

String regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15)

String regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16)

String businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17)

String businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18)

String isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19)

String psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21)

String successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22)

String uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23)

ArrayList<String> documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(';', -1)

'split signer untuk doc1 dan signer untuk doc2'
ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(';', -1)

ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(';', -1)

ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(';', -1)

ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(';', -1)

ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(';', -1)

ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(';', -1)

ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(';', -1)

ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(';', -1)

ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(';', -1)

ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(';', -1)

ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(';', -1)

ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(';', -1)

ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(';', -1)

ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(';', -1)

ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(';', -1)

ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(';', -1)

String idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42)

String signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43)

String stringRefno = new String()

stringRefno = ''

'Pembuatan pengisian variable di sendRequest per jumlah signer.'
ArrayList<String> list = new ArrayList<String>()

ArrayList<String> ListSigner = new ArrayList<String>()

(ListSigner[0]) = ''

'Looping berdasarkan jumlah array dari signAction'
for (int i = 1; i <= signAction.size(); i++) {
    'Jika signactionnya telah di akhir, maka'
    if (i == signAction.size()) {
        'Membuat Body API'
        list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') + 
            (signerType[(i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i - 1)])) + 
            ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') + 
            (kelurahan[(i - 1)])) + ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') + 
            (nama[(i - 1)])) + ',"tlp": ') + (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') + 
            (provinsi[(i - 1)])) + ',"idKtp": ') + (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') + 
            (email[(i - 1)])) + ',"npwp": ') + (npwp[(i - 1)])) + ',"idPhoto": ') + idPhoto) + ',"signerSelfPhoto": ') + 
            signerSelfPhoto) + '}')
    }
    
    'Membuat Body API'
    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') + (signerType[
        (i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i - 1)])) + ',"jenisKelamin": ') + 
        (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') + (kelurahan[(i - 1)])) + 
        ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') + (nama[(i - 1)])) + ',"tlp": ') + 
        (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') + (provinsi[(i - 1)])) + ',"idKtp": ') + 
        (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') + (email[(i - 1)])) + ',"npwp": ') + 
        (npwp[(i - 1)])) + ',"idPhoto": ') + idPhoto) + ',"signerSelfPhoto": ') + signerSelfPhoto) + '},')

    'Memasukkan seluruh BodyAPI ke ListSigner[0]'
    (ListSigner[0]) = ((ListSigner[0]) + (list[(i - 1)]))
}

'looping berdasarkan jumlah dari documentFile'
for (int t = 0; t < documentFile.size(); t++) {
    'Jika ukuran looping telah di akhir yaitu ukuran dari document File'
    if (t == (documentFile.size() - 1)) {
        'Membuat body API'
        stringRefno = ((stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + refNo) + ', "documentTemplateCode": ') + 
        documentTemplateCode) + ', "officeCode": ') + officeCode) + ', "officeName": ') + officeName) + ', "regionCode": ') + 
        regionCode) + ', "regionName": ') + regionName) + ', "businessLineCode": ') + businessLineCode) + ', "businessLineName": ') + 
        businessLineName) + ', "isSequence": ') + isSequence) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + 
        PDFtoBase64(documentFile[t])) + '", "psreCode" : ') + psreCode) + ', "successURL": ') + successURL) + ', "uploadURL": ') + 
        uploadURL)) + '}')
    } else {
        'Jika tidak diakhir maka membuat body API'
        stringRefno = ((stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + refNo) + ', "documentTemplateCode": ') + 
        documentTemplateCode) + ', "officeCode": ') + officeCode) + ', "officeName": ') + officeName) + ', "regionCode": ') + 
        regionCode) + ', "regionName": ') + regionName) + ', "businessLineCode": ') + businessLineCode) + ', "businessLineName": ') + 
        businessLineName) + ', "isSequence": ') + isSequence) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + 
        PDFtoBase64(documentFile[t])) + '", "psreCode" : ') + psreCode) + ', "successURL": ') + successURL) + ', "uploadURL": ') + 
        uploadURL)) + '},')
    }
}

'Hit API'
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

        'Responsenya diwrite di excel'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Send to Sign', 4, GlobalVariable.NumofColm - 
            1, GlobalVariable.Response.toString())

        'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
        int jumlahsignertandatangan = 0

        'write to excel success'

        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        'Call test case mengenai kotak masuk dan melempar variable API_ExcelPath, dan jumlah signer untuk tanda tangan'
        WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('excelPathFESignDocument') : 'Beranda/SendtoSign', ('jumlahsignertandatangan') : jumlahsignertandatangan], 
            FailureHandling.CONTINUE_ON_FAILURE)

        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call Fungsi ResponseAPIStoreDB'
            ResponseAPIStoreDB(conneSign)
        }
        
        'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    } else {
        'mengambil message Failed'
        messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

        'write to excel status failed dan reason'

        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 

            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', 
                '') + ';') + messageFailed)

        'Jika result Tenantnya sama, maka check error report'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
            'call test case error report'
            WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
    }
}

'Fungsi PDF to Base64'
def PDFtoBase64(String fileName) {
    String base64 = CustomKeywords.'customizeKeyword.ConvertFile.BASE64File'(fileName)

    return base64
}

'Fungsi responseAPIStoreDB'
def ResponseAPIStoreDB(Connection conneSign) {
    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()

    docid = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 5).replace('[', '').replace(']', '').split(', ', 
        -1)

    for (int r = 0; r < docid.size(); r++) {
        'get data API Send Document dari DB (hanya 1 signer)'
        ArrayList<String> result = CustomKeywords.'connection.dataVerif.getSendDoc'(conneSign, docid[r])

        'declare arrayindex'
        arrayindex = 0

        'verify email'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signerType'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tenant code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref_number'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document_id'
        arrayMatch.add(WebUI.verifyMatch(docid[r], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document template code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify is sequence'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify psre/vendor code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify success url'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify upload url'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', 
                    ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify sign Action : hardcode. Yang penting tidak boleh kosong'
        arrayMatch.add(WebUI.verifyNotMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).replace(
                    '"', ''), '', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

