import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.DataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

semicolon = ';'
splitIndex = -1

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 4/*findTestData(API_Excel_Path).columnNumbers*/; (GlobalVariable.NumofColm)++) {
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

    ArrayList<String> documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitIndex)

    'split signer untuk doc1 dan signer untuk doc2'
    ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(semicolon, splitIndex)

    ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(semicolon, splitIndex)

    ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(semicolon, splitIndex)

    ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(semicolon, splitIndex)

    ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(semicolon, splitIndex)

    ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(semicolon, splitIndex)

    ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(semicolon, splitIndex)

    ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(semicolon, splitIndex)

    ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(semicolon, splitIndex)

    ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(semicolon, splitIndex)

    ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(semicolon, splitIndex)

    ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(semicolon, splitIndex)

    ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(semicolon, splitIndex)

    ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(semicolon, splitIndex)

    ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(semicolon, splitIndex)

    ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(semicolon, splitIndex)

    ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(semicolon, splitIndex)

    String idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42)
	
    String signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43)
	
    String stringRefno = new String()
	
	stringRefno = ''

	'Pembuatan pengisian variable di sendRequest per jumlah signer.'
	ArrayList<String> list = new ArrayList<String>()

	String listSigner = new String()

	listSigner = ''

	for (int i = 1; i <= signAction.size(); i++) {
		if (i == signAction.size()) {
			list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') +
				(signerType[(i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i -
				1)])) + ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') +
				(kelurahan[(i - 1)])) + ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') +
				(nama[(i - 1)])) + ',"tlp": ') + (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') +
				(provinsi[(i - 1)])) + ',"idKtp": ') + (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') +
				(email[(i - 1)])) + ',"npwp": ') + (npwp[(i - 1)])) + ',"idPhoto": ') + idPhoto + ',"signerSelfPhoto": ') + signerSelfPhoto
					)) + '}')
		}
		
		list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signAction[(i - 1)])) + ',"signerType": ') +
		(signerType[(i - 1)])) + ',"signSequence":') + (signSequence[(i - 1)])) + ',"alamat": ') + (alamat[(i -
		 1)])) + ',"jenisKelamin": ') + (jenisKelamin[(i - 1)])) + ',"kecamatan": ') + (kecamatan[(i - 1)])) + ',"kelurahan": ') +
		 (kelurahan[(i - 1)])) + ',"kodePos": ') + (kodePos[(i - 1)])) + ',"kota": ') + (kota[(i - 1)])) + ',"nama": ') +
		 (nama[(i - 1)])) + ',"tlp": ') + (tlp[(i - 1)])) + ',"tglLahir": ') + (tglLahir[(i - 1)])) + ',"provinsi": ') +
		 (provinsi[(i - 1)])) + ',"idKtp": ') + (idKtp[(i - 1)])) + ',"tmpLahir": ') + (tmpLahir[(i - 1)])) + ',"email": ') +
		 (email[(i - 1)])) + ',"npwp": ') + (npwp[(i - 1)])) + ',"idPhoto": ') + idPhoto + ',"signerSelfPhoto": ') + signerSelfPhoto
		  )) + '},')

    'Memasukkan seluruh BodyAPI ke listSigner'
    listSigner = listSigner + list[(i - 1)]
		
	}
	
	for(int t = 0; t < documentFile.size();t++) {
		if(t == documentFile.size() - 1){
			stringRefno = stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + (refNo)) + ', "documentTemplateCode": ') +
				(documentTemplateCode)) + ', "officeCode": ') + (officeCode)) + ', "officeName": ') + (officeName)) + ', "regionCode": ') + (regionCode)) + ', "regionName": ') + (regionName)) + ', "businessLineCode": ') +
				(businessLineCode)) + ', "businessLineName": ') + (businessLineName)) + ', "isSequence": ') +
				(isSequence)) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + PDFtoBase64(documentFile[t])) + '", "psreCode" : ') +
				(psreCode)) + ', "successURL": ') + (successURL)) + ', "uploadURL": ') + (uploadURL)) + '}'
			}
		else 
		{
			stringRefno = stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + (refNo)) + ', "documentTemplateCode": ') + 
            (documentTemplateCode)) + ', "officeCode": ') + (officeCode)) + ', "officeName": ') + (officeName)) + ', "regionCode": ') + (regionCode)) + ', "regionName": ') + (regionName)) + ', "businessLineCode": ') + 
            (businessLineCode)) + ', "businessLineName": ') + (businessLineName)) + ', "isSequence": ') + 
            (isSequence)) + ', "signer":[') + (ListSigner[0])) + '], "documentFile": "') + PDFtoBase64(documentFile[t])) + '", "psreCode" : ') + 
            (psreCode)) + ', "successURL": ') + (successURL)) + ', "uploadURL": ') + (uploadURL)) + 
            '},'
		}

		}
		
		String isDownloadDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 47)
		
		String isDeleteDownloadedDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 48)
		
		String isViewDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 49)
		
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
			
			'Write to excel mengenai Document ID'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                5, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString().replace('[','').replace(']',''))
					
			'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
			int jumlahsignertandatangan = 0

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			
			'Call Test case mengneai Kotak Masuk'
            WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('excelPathFESignDocument') : 'Registrasi/SendDocument',('jumlahsignertandatangan') : jumlahsignertandatangan, ('isDownloadDocument') : isDownloadDocument, ('isDeleteDownloadedDocument') : isDeleteDownloadedDocument, ('isViewDocument') : isViewDocument], FailureHandling.CONTINUE_ON_FAILURE)
            
			if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + semicolon) + messageFailed)

            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
                'call test case error report'
                WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }
}


public PDFtoBase64(String fileName) {
    String base64 = CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)

    return base64
}

