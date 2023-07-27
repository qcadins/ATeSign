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
String resultTenant = CustomKeywords.'connection.SendSign.getTenant'(conneSign, GlobalVariable.userLogin)

sheet = 'API Send Document'
'variable untuk keperluan split excel'
semicolon = ';'

int splitnum = -1

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
	if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	} else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) { 

    'Inisialisasi ref No berdasarkan delimiter ;'
    refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11)

    'Inisialisasi document template code berdasarkan delimiter ;'
    documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(semicolon, 
        splitnum)

    'Inisialisasi office Code berdasarkan delimiter ;'
    officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).split(semicolon, splitnum)

    'Inisialisasi office name berdasarkan delimiter ;'
    officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).split(semicolon, splitnum)

    'Inisialisasi region code berdasarkan delimiter ;'
    regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).split(semicolon, splitnum)

    'Inisialisasi region name berdasarkan delimiter ;'
    regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).split(semicolon, splitnum)

    'Inisialisasi business line code berdasarkan delimiter ;'
    businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).split(semicolon, splitnum)

    'Inisialisasi business line name berdasarkan delimiter ;'
    businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).split(semicolon, splitnum)

    'Inisialisasi is sequence berdasarkan delimiter ;'
    isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitnum)

	'Inisialisasi document file berdasarkan delimiter ;'
	documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitnum)
	
    'Inisialisasi psre Code berdasarkan delimiter ;'
    psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitnum)

	'Inisialisasi successUrl berdasarkan delimiter ;'
	successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).split(semicolon, splitnum)
	
	'Inisialisasi psre Code berdasarkan delimiter ;'
	uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitnum)

    'split signer untuk doc1 dan signer untuk doc2'
    signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(semicolon, splitnum)

    signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(semicolon, splitnum)

	signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(semicolon, splitnum)
	
	alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(semicolon, splitnum)
	
	jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(semicolon, splitnum)
	
	kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(semicolon, splitnum)
	
	kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(semicolon, splitnum)
	
	kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(semicolon, splitnum)
	
	kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(semicolon, splitnum)
	
	nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(semicolon, splitnum)
	
	tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(semicolon, splitnum)
	
	tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(semicolon, splitnum)
	
	provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(semicolon, splitnum)
	
	idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(semicolon, splitnum)
	
	tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(semicolon, splitnum)
	
	email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(semicolon, splitnum)
	
	npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(semicolon, splitnum)
	
	seqNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split(semicolon, splitnum)
	
	idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43)
		
	signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 44)
	
    String stringRefno = new String()
	
	stringRefno = ''
	
	'Pembuatan pengisian variable di sendRequest per jumlah signer.'
	ArrayList list = []

	String listSigner

	listSigner = ''
	ArrayList seqNoBodyAPI = []
	
	'looping mengenai total sequence number'
	for (int p = 0; p < seqNo.size(); p++) {
		'jika seq numbernya tidak kosong'
		if ((seqNo[p]) != '') {
			'Memasukkan value seqNo dan body API kepada array'
			seqNoBodyAPI.add(',"seqNo": ' + (seqNo[p]))
		} else {
			'Jika seq number kosong ,maka input kosong'
			seqNoBodyAPI.add('')
		}
	}

	',"seqNo": ' + seqNo[i]
	
	for (int i = 0; i < signAction.size(); i++) {
		if (i == signAction.size() - 1) {
			list.add('{"signAction": ' + signAction[i] + ',"signerType": ' +
				signerType[i] + ',"signSequence":' + signSequence[i] + ',"alamat": ' + alamat[i -
				1] + ',"jenisKelamin": ' + jenisKelamin[i] + ',"kecamatan": ' + kecamatan[i] + ',"kelurahan": ' +
				kelurahan[i] + ',"kodePos": ' + kodePos[i] + ',"kota": ' + kota[i] + ',"nama": ' +
				nama[i] + ',"tlp": ' + tlp[i] + ',"tglLahir": ' + tglLahir[i] + ',"provinsi": ' +
				provinsi[i] + ',"idKtp": ' + idKtp[i] + ',"tmpLahir": ' + tmpLahir[i] + ',"email": ' +
				email[i] + ',"npwp": ' + npwp[i] + seqNoBodyAPI + ',"idPhoto": ' + idPhoto + ',"signerSelfPhoto": ' + signerSelfPhoto
					 + '}')
		}
		
		list.add('{"signAction": ' + signAction[i - 1] + ',"signerType": ' +
		signerType[i - 1] + ',"signSequence":' + signSequence[i - 1] + ',"alamat": ' + alamat[i -
		 1] + ',"jenisKelamin": ' + jenisKelamin[i - 1] + ',"kecamatan": ' + kecamatan[i - 1] + ',"kelurahan": ' +
		 kelurahan[i - 1] + ',"kodePos": ' + kodePos[i - 1] + ',"kota": ' + kota[i - 1] + ',"nama": ' +
		 nama[i - 1] + ',"tlp": ' + tlp[i - 1] + ',"tglLahir": ' + tglLahir[i - 1] + ',"provinsi": ' +
		 provinsi[i - 1] + ',"idKtp": ' + idKtp[i - 1] + ',"tmpLahir": ' + tmpLahir[i - 1] + ',"email": ' +
		 email[i - 1] + ',"npwp": ' + npwp[i - 1] + ',"idPhoto": ' + idPhoto + ',"signerSelfPhoto": ' + signerSelfPhoto
		   + '},')

	    'Memasukkan seluruh BodyAPI ke listSigner'
	    listSigner = listSigner + list[(i - 1)]
		
		'check ada value maka setting email service tenant'
		if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 50).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 50), email[(i - 1)].replace('"',''))
		}
	}
	
	for(int t = 0; t < documentFile.size(); t++) {
		if(t == documentFile.size() - 1){
			stringRefno = stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + (refNo)) + ', "documentTemplateCode": ') +
				(documentTemplateCode)) + ', "officeCode": ') + (officeCode)) + ', "officeName": ') + (officeName)) + ', "regionCode": ') + (regionCode)) + ', "regionName": ') + (regionName)) + ', "businessLineCode": ') +
				(businessLineCode)) + ', "businessLineName": ') + (businessLineName)) + ', "isSequence": ') +
				(isSequence)) + ', "signer":[') + (listSigner)) + '], "documentFile": "') + pdftoBase64(documentFile[t])) + '", "psreCode" : ') +
				(psreCode)) + ', "successURL": ') + (successURL)) + ', "uploadURL": ') + (uploadURL)) + '}'
			}
		else 
		{
			stringRefno = stringRefno + ((((((((((((((((((((((((((('{"referenceNo" : ' + (refNo)) + ', "documentTemplateCode": ') + 
            (documentTemplateCode)) + ', "officeCode": ') + (officeCode)) + ', "officeName": ') + (officeName)) + ', "regionCode": ') + (regionCode)) + ', "regionName": ') + (regionName)) + ', "businessLineCode": ') + 
            (businessLineCode)) + ', "businessLineName": ') + (businessLineName)) + ', "isSequence": ') + 
            (isSequence)) + ', "signer":[') + (listSigner)) + '], "documentFile": "') + pdftoBase64(documentFile[t])) + '", "psreCode" : ') + 
            (psreCode)) + ', "successURL": ') + (successURL)) + ', "uploadURL": ') + (uploadURL)) + 
            '},'
		}

		}
		
		String isDownloadDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 47)
		
		String isDeleteDownloadedDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 48)
		
		String isViewDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 49)

		'Input tenant'
		GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
		
		'get api key dari db'
		 GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		
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
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                5, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString().replace('[','').replace(']',''))
					
			'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
			int jumlahsignertandatangan = 0

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			
			'Call Test case mengneai Kotak Masuk'
            WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('excelPathFESignDocument') : API_Excel_Path,('jumlahsignertandatangan') : jumlahsignertandatangan, ('isDownloadDocument') : isDownloadDocument, ('isDeleteDownloadedDocument') : isDeleteDownloadedDocument, ('isViewDocument') : isViewDocument, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
            
			if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : API_Excel_Path], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
			
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + semicolon) + messageFailed)

            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
                'call test case error report'
                WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : API_Excel_Path], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }  else {
        'write to excel status failed dan reason : failed hit api'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedHitAPI)

        'call test case login inveditor'
        WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

        'call test case error report'
        WebUI.callTestCase(findTestCase('Sign_Document/ErrorReport'), [('excelPathSignDoc') : 'Registrasi/SendDocument'], 
            FailureHandling.STOP_ON_FAILURE)

        'close browser'
        WebUI.closeBrowser()
    }
}
}

public pdftoBase64(String fileName) {
    String base64 = CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)

    return base64
}

