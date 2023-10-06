import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'setting menggunakan base url yang benar atau salah'
CustomKeywords.'connection.APIFullService.settingBaseUrl'(API_Excel_Path, GlobalVariable.NumofColm, rowExcel('Use Correct base Url (Send Normal)'))

'Inisialisasi ref No berdasarkan delimiter ;'
refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo (Send Normal)'))

'Inisialisasi document template code berdasarkan delimiter ;'
documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi office Code berdasarkan delimiter ;'
officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeCode (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi office name berdasarkan delimiter ;'
officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeName (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi region code berdasarkan delimiter ;'
regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionCode (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi region name berdasarkan delimiter ;'
regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionName (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi business line code berdasarkan delimiter ;'
businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi business line name berdasarkan delimiter ;'
businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi is sequence berdasarkan delimiter ;'
isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isSequence (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi document file berdasarkan delimiter ;'
documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentFile (Send Normal)')).split(
    enter, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$psreCode (Send Normal)')).split(semicolon, 
    splitnum)

'Inisialisasi successUrl berdasarkan delimiter ;'
successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('successURL (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('uploadURL (Send Normal)')).split(semicolon, 
    splitnum)

'split signer untuk doc1 dan signer untuk doc2'
signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signAction (Send Normal)')).split(
    enter, splitnum)

signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signerType (Send Normal)')).split(
    enter, splitnum)

signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signSequence (Send Normal)')).split(
    enter, splitnum)

alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('alamat (Send Normal)')).split(enter, 
    splitnum)

jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('jenisKelamin (Send Normal)')).split(
    enter, splitnum)

kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan (Send Normal)')).split(enter, 
    splitnum)

kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan (Send Normal)')).split(enter, 
    splitnum)

kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kodePos (Send Normal)')).split(enter, 
    splitnum)

kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kota (Send Normal)')).split(enter, splitnum)

nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$nama (Send Normal)')).split(enter, splitnum)

tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tlp (Send Normal)')).split(enter, splitnum)

tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir (Send Normal)')).split(enter, 
    splitnum)

provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('provinsi (Send Normal)')).split(enter, 
    splitnum)

idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp (Send Normal)')).split(enter, splitnum)

tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir (Send Normal)')).split(enter, 
    splitnum)

email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email (Send Normal)')).split(enter, splitnum)

npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('npwp (Send Normal)')).split(enter, splitnum)

idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto (Send Normal)')).split(enter, splitnum)

signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signerSelfPhoto (Send Normal)')).split(enter, splitnum)

String stringRefno = new String()

String bodyAPI = new String()

'Looping berdasarkan total dari dokumen file ukuran'
for (int o = 0; o < documentFile.size(); o++) {
    'split signer untuk doc1 dan signer untuk doc2'
    signActions = (signAction[o]).split(semicolon, splitnum)

    signerTypes = (signerType[o]).split(semicolon, splitnum)

    signSequences = (signSequence[o]).split(semicolon, splitnum)

    alamats = (alamat[o]).split(semicolon, splitnum)

    jenisKelamins = (jenisKelamin[o]).split(semicolon, splitnum)

    kecamatans = (kecamatan[o]).split(semicolon, splitnum)

    kelurahans = (kelurahan[o]).split(semicolon, splitnum)

    kodePoss = (kodePos[o]).split(semicolon, splitnum)

    kotas = (kota[o]).split(semicolon, splitnum)

    namas = (nama[o]).split(semicolon, splitnum)

    tlps = (tlp[o]).split(semicolon, splitnum)

    tglLahirs = (tglLahir[o]).split(semicolon, splitnum)

    provinsis = (provinsi[o]).split(semicolon, splitnum)

    idKtps = (idKtp[o]).split(semicolon, splitnum)

    tmpLahirs = (tmpLahir[o]).split(semicolon, splitnum)

    emails = (email[o]).split(semicolon, splitnum)

    npwps = (npwp[o]).split(semicolon, splitnum)
	
	idPhotos = (idPhoto[o]).split(semicolon, splitnum)

	signerSelfPhotos = (signerSelfPhoto[o]).split(semicolon, splitnum)
	
    String listSigner

    listSigner = ''

	'Pembuatan pengisian variable di sendRequest per jumlah signer.'
	ArrayList<String> list = []
	
    for (int i = 0; i < signActions.size(); i++) {

        if ((i == 0) && (i == (signActions.size() - 1))) {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + (signerTypes[
                i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[(i - 1)])) + ',"jenisKelamin": ') + 
                (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + (kelurahans[i])) + ',"kodePos": ') + 
                (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + 
                (tglLahirs[i])) + ',"provinsi": ') + (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + 
                (tmpLahirs[i])) + ',"email": ') + (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhotos[i]) + 
                ',"signerSelfPhoto": ') + signerSelfPhotos[i]) + '}')
        } else if (i == (signActions.size() - 1)) {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + (signerTypes[
                i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[(i - 1)])) + ',"jenisKelamin": ') + 
                (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + (kelurahans[i])) + ',"kodePos": ') + 
                (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + 
                (tglLahirs[i])) + ',"provinsi": ') + (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + 
                (tmpLahirs[i])) + ',"email": ') + (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhotos[i]) + 
                ',"signerSelfPhoto": ') + signerSelfPhotos[i]) + '}')
        } else {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + (signerTypes[
                i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[i])) + ',"jenisKelamin": ') + 
                (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + (kelurahans[i])) + ',"kodePos": ') + 
                (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + 
                (tglLahirs[i])) + ',"provinsi": ') + (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + 
                (tmpLahirs[i])) + ',"email": ') + (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhotos[i]) + 
                ',"signerSelfPhoto": ') + signerSelfPhotos[i]) + '},')
        }

        'Memasukkan seluruh BodyAPI ke listSigner'
        listSigner = (listSigner + (list[(i)]))

        'check ada value maka setting email service tenant'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service (Send Normal)')).length() > 
        0) {
			for (loopingSignerEmailActive = 0; loopingSignerEmailActive < idKtps.size(); loopingSignerEmailActive++) {
				SHA256IdNo = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'( idKtps[loopingSignerEmailActive].replace('"', ''))
				
				'setting email service tenant'
				CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Service (Send Normal)')),SHA256IdNo)
			}
        }
    }
    
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document (Send Normal)')) == 
    'Yes') {
        bodyAPI = (('"documentFile": "' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[o])) + '"')
    } else {
        bodyAPI = (('"documentFile": "' + (documentFile[o])) + '"')
    }
    
    if (o == (documentFile.size() - 1)) {
        stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + 
        (documentTemplateCode[o])) + ', "officeCode": ') + (officeCode[o])) + ', "officeName": ') + (officeName[o])) + ', "regionCode": ') + 
        (regionCode[o])) + ', "regionName": ') + (regionName[o])) + ', "businessLineCode": ') + (businessLineCode[o])) + 
        ', "businessLineName": ') + (businessLineName[o])) + ', "isSequence": ') + (isSequence[o])) + ', "signer":[') + 
        listSigner) + '],') + bodyAPI) + ', "psreCode" : ') + (psreCode[o])) + ', "successURL": ') + (successURL[o])) + 
        ', "uploadURL": ') + (uploadURL[o])) + '}')
    } else {
        stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + 
        (documentTemplateCode[o])) + ', "officeCode": ') + (officeCode[o])) + ', "officeName": ') + (officeName[o])) + ', "regionCode": ') + 
        (regionCode[o])) + ', "regionName": ') + (regionName[o])) + ', "businessLineCode": ') + (businessLineCode[o])) + 
        ', "businessLineName": ') + (businessLineName[o])) + ', "isSequence": ') + (isSequence[o])) + ', "signer":[') + 
        listSigner) + '],') + bodyAPI) + ', "psreCode" : ') + (psreCode[o])) + ', "successURL": ') + (successURL[o])) + 
        ', "uploadURL": ') + (uploadURL[o])) + '},')
    }

}

'Jika flag tenant no'
if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Send Normal)')) == 
'No') {
    'set tenant kosong'
    GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code (Send Normal)'))
} else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Send Normal)')) == 
'Yes') {
    'Input tenant'
    GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
}

'check if mau menggunakan api_key yang salah atau benar'
if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Send Normal)')) == 'Yes') {
    'get api key dari db'
    GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
} else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Send Normal)')) == 
'No') {
    'get api key salah dari excel'
    GlobalVariable.api_key = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key (Send Normal)'))
}

'Hit API'
respon = WS.sendRequest(findTestObject('Postman/Send Document', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                GlobalVariable.NumofColm, rowExcel('$tenantCode (Send Normal)')), ('request') : stringRefno, ('callerId') : findTestData(
                API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('callerId (Send Normal)'))]))

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('documentid') - 
            1, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString().replace('[', '').replace(']', ''))

        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumofColm -
			1, GlobalVariable.StatusSuccess)
		
        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call test case ResponseAPIStoreDB'
            WebUI.callTestCase(findTestCase('Main Flow/API Send Document Normal StoreDB'), [('API_Excel_Path') : API_Excel_Path, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    } else {
       getErrorMessageAPI(respon)
    }
} else {
	getErrorMessageAPI(respon)	
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';' + '<') + 
            message + '>')

	GlobalVariable.FlagFailed = 1
}