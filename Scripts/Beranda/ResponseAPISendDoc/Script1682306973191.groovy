import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.DataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

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
        pdfToBase64(documentFile[t])) + '", "psreCode" : ') + psreCode) + ', "successURL": ') + successURL) + ', "uploadURL": ') + 
        uploadURL)) + '}')
    } else {
        'Jika tidak diakhir maka membuat body API'
        stringRefno = ((stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + refNo) + ', "documentTemplateCode": ') + 
        documentTemplateCode) + ', "officeCode": ') + officeCode) + ', "officeName": ') + officeName) + ', "regionCode": ') + 
        regionCode) + ', "regionName": ') + regionName) + ', "businessLineCode": ') + businessLineCode) + ', "businessLineName": ') + 
        businessLineName) + ', "isSequence": ') + isSequence) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + 
        pdfToBase64(documentFile[t])) + '", "psreCode" : ') + psreCode) + ', "successURL": ') + successURL) + ', "uploadURL": ') + 
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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Send to Sign', 4, GlobalVariable.NumofColm - 
            1, GlobalVariable.Response.toString().replace('[', '').replace(']', ''))

        'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
        int jumlahsignertandatangan = 0
		
		ArrayList<String> emailSigner = email

        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Send to Sign', 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('excelPathFESignDocument') : 'Beranda/SendtoSign', ('jumlahsignertandatangan') : jumlahsignertandatangan], 
         FailureHandling.CONTINUE_ON_FAILURE)
        'Call test case mengenai kotak masuk dan melempar variable API_ExcelPath, dan jumlah signer untuk tanda tangan'
        MonitoringDocument(conneSign, refNo, regionName, officeName, jumlahsignertandatangan, emailSigner)

        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call Fungsi responseAPIStoreDB'
            responseAPIStoreDB(conneSign)
        }
        
        'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    } else {
        'mengambil message Failed'
        messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
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

'Fungsi responseAPIStoreDB'

def pdfToBase64(String fileName) {
    String base64 = CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)

    return base64
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def responseAPIStoreDB(Connection conneSign) {
    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()

    docid = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 5).split(', ', -1)

    for (int r = 0; r < docid.size(); r++) {
        'get data API Send Document dari DB (hanya 1 signer)'
        ArrayList<String> resultStoreDB = CustomKeywords.'connection.DataVerif.getSendDoc'(conneSign, docid[r])

        'declare arrayindex'
        arrayindex = 0

        'verify email'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signerType'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tenant code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref_number'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document_id'
        arrayMatch.add(WebUI.verifyMatch(docid[r], resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document template code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line name'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify is sequence'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify psre/vendor code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify success url'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify upload url'
        arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', 
                    ''), resultStoreDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify sign Action : hardcode. Yang penting tidak boleh kosong'
        arrayMatch.add(WebUI.verifyNotMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).replace(
                    '"', ''), '', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

def MonitoringDocument(Connection conneSign, String refNo, String regionName, String officeName, int jumlahsignertandatangan, ArrayList<String> emailSigner) {
	'Call test Case untuk login sebagai admin wom admin client'
	WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)
	
	'Pembuatan untuk array Index untuk total Sign dan total Signed'
    arrayIndexforSign = 0

    'Pembuatan untuk array Index result Query'
    arrayIndex = 0

    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()

    'Mengisi value hasil komparasi, total sign, dan total signed'
    jumlahSigned = CustomKeywords.'connection.DataVerif.getComparationTotalSignTotalSigned'(conneSign, refNo)

    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    fullNameCust = CustomKeywords.'connection.DataVerif.getuserCustomerondocument'(conneSign, refNo)

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

    'Klik Button Document Monitoring'
    WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

    if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/input_NamaPelanggan'), GlobalVariable.TimeOut, 
        FailureHandling.CONTINUE_ON_FAILURE)) {
        WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), fullNameCust)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                67))

        WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), regionName)

        WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), refNo)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), jumlahSigned[arrayIndexforSign++])

        WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), officeName)

        WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))

        WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/lbl_Value'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)

        int sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        int sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        resultQuery = CustomKeywords.'connection.DataVerif.getDocumentMonitoring'(conneSign, refNo)

        for (int j = 1; j <= sizeRowofLabelValue.size(); j++) {
            for (int i = 1; i <= sizeColumnofLabelValue.size() - 1; i++) {
                'modify object btn TTD Dokumen di beranda'
                modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 
                    'xpath', 'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div/p', true)

                if (i == 7) {
					totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ',-1)
					
					arrayMatch.add(WebUI.verifyMatch(totalSignandtotalSigned, jumlahsignertandatangan, false,
						FailureHandling.CONTINUE_ON_FAILURE))

					arrayMatch.add(WebUI.verifyEqual(emailSigner.size(), (prosesttd_pencariandokumen[1]).replace(' ', ''), FailureHandling.CONTINUE_ON_FAILURE))
					
                } else if (i == 8) {
                } else {
                    arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false, 
                            FailureHandling.CONTINUE_ON_FAILURE))
                }
            }
        }
    }
}

