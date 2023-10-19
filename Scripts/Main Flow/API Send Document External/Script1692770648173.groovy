import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
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
CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISendDoc, GlobalVariable.NumofColm, rowExcel('Use Correct base Url (Send External)'))

'Deklarasi variable mengenai signLoc untuk store db'
String signlocStoreDB

getDataExcel(semicolon, splitnum, delimiter, enter)

'Deklarasi variable string Ref no untuk full body API.'
String stringRefno = new String()

ArrayList split = []

split = setBodyAPI(stringRefno, signlocStoreDB)

stringRefno = split[0]

signlocStoreDB = split[1]

'Jika flag tenant no'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Send External)')) == 'No') {
    'set tenant kosong'
    GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code (Send External)'))
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Send External)')) == 'Yes') {
    'Input tenant'
    GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
}

'check if mau menggunakan api_key yang salah atau benar'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Send External)')) == 'Yes') {
    'get api key dari db'
    GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Send External)')) == 'No') {
    'get api key salah dari excel'
    GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key (Send External)'))
}

'Hit API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, rowExcel('$tenantCode (Send External)')), ('request') : stringRefno, ('callerId') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId (Send External)'))]))

'jika response 200 / hit api berhasil'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'ambil respon text dalam bentuk code.'
    status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'jika status codenya 0, verifikasi datanya benar'
    if (status_Code == 0) {
        'mengambil documentid, trxno dan response doc template code dari hasil response API'
        documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)

		'value trxno akan ada jika melakukan sign action autosign(at)'
        trxno = WS.getElementPropertyValue(respon, 'documents.trxNos', FailureHandling.OPTIONAL)

		'get respons doc template code'
        responseDocTemplateCode = WS.getElementPropertyValue(respon, 'documents.docTemplateCode', FailureHandling.OPTIONAL)
		
		'get respons psre Code'
		responsePsreCode = WS.getElementPropertyValue(respon, 'documents.psreCode', FailureHandling.OPTIONAL)
		
        'Jika doc template code di excel tidak sesuai dengan response doc template code'
        if (documentTemplateCode.toString().replace('"', '') != responseDocTemplateCode.toString()) {
            'write to excel status failed dan reason failed h'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + semicolon) + GlobalVariable.ReasonFailedSaveGagal) + ' pada perbedaan document template code ')
        }
        
        'Memasukkan documentid dan trxno ke dalam excel'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('documentid') - 1, GlobalVariable.NumofColm - 
            1, documentId.toString().replace('[', '').replace(']', ''))

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNo') - 1, GlobalVariable.NumofColm - 
            1, trxno.toString().replace('[', '').replace(']', ''))

        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 1, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        'Jika check storedb'
        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call test case storedb'
            WebUI.callTestCase(findTestCase('Main Flow/API Send Document External StoreDB'), [('excelPathAPISendDoc') : excelPathAPISendDoc
                    , ('sheet') : sheet, ('signlocStoreDB') : signlocStoreDB, ('responsePsreCode') : responsePsreCode], FailureHandling.CONTINUE_ON_FAILURE)
        }
    } else {
        getErrorMessageAPI(respon)
    }
} else {
    getErrorMessageAPI(respon)
}

'Fungsi PDF to Base64'

'Fungsi storedb'

def PDFtoBase64(String fileName) {
    return CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (';<' + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getDataExcel(String semicolon, int splitnum, String delimiter, String enter) {
	'Inisialisasi ref No'
	refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo (Send External)'))
	
	'Inisialisasi document template code berdasarkan delimiter ;'
	documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi document name berdasarkan delimiter ;'
	documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentName (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi office Code berdasarkan delimiter ;'
	officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeCode (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi office name berdasarkan delimiter ;'
	officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeName (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi region code berdasarkan delimiter ;'
	regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionCode (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi region name berdasarkan delimiter ;'
	regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionName (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi business line code berdasarkan delimiter ;'
	businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi business line name berdasarkan delimiter ;'
	businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi is sequence berdasarkan delimiter ;'
	isSequence = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('isSequence (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi psre Code berdasarkan delimiter ;'
	psreCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('psreCode (Send External)')).split(semicolon, splitnum)
	
	'Inisialisasi document file berdasarkan delimiter ;'
	documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentFile (Send External)')).split(enter, splitnum)
	
	'split signer untuk doc1 dan signer untuk doc2'
	signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signAction (Send External)')).split(enter, splitnum)
	
	signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signerType (Send External)')).split(enter, splitnum)
	
	tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$tlp (Send External)')).split(enter, splitnum)
	
	idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp (Send External)')).split(enter, splitnum)
	
	email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$email (Send External)')).split(enter, splitnum)
	
	pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send stampExternal)')).split(delimiter, splitnum)
	
	llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send stampExternal)')).split(delimiter, splitnum)
	
	llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (Send stampExternal)')).split(delimiter, splitnum)
	
	urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send stampExternal)')).split(delimiter, splitnum)
	
	uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send stampExternal)')).split(delimiter, splitnum)
}

def setBodyAPI(String stringRefno, String signlocStoreDB) {
	'Looping berdasarkan total dari dokumen file ukuran'
	for (int i = 0; i < documentFile.size(); i++) {
		'signloc store db harus dikosongkan untuk loop dokumen selanjutnya.'
		signlocStoreDB = ''
	
		'Splitting kembali dari dokumen pertama per signer'
		signActions = (signAction[i]).split(semicolon, splitnum)
	
		signerTypes = (signerType[i]).split(semicolon, splitnum)
	
		tlps = (tlp[i]).split(semicolon, splitnum)
	
		idKtps = (idKtp[i]).split(semicolon, splitnum)
	
		emails = (email[i]).split(semicolon, splitnum)

		'inisialisasi bodyAPI untuk menyusun body'
		String bodyAPI = new String()
	
		'Pengisian body'
		bodyAPI = (((((((((((((((((((((((bodyAPI + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + (documentTemplateCode[
		i])) + ', "documentName": ') + (documentName[i])) + ', "officeCode": ') + (officeCode[i])) + ', "officeName": ') + (officeName[
		i])) + ', "regionCode": ') + (regionCode[i])) + ', "regionName": ') + (regionName[i])) + ', "businessLineCode": ') +
		(businessLineCode[i])) + ', "businessLineName": ') + (businessLineName[i])) + ', "isSequence": ') + (isSequence[i])) +
		',  "psreCode": ') + (psreCode[i])) + ',')
	
		'Memasukkan bodyAPI ke stringRefno'
		stringRefno = (stringRefno + bodyAPI)
	
		'inisialisasi bodyAPI untuk menyusun body'
		bodyAPI = new String()
	
		'inisialisasi body untuk seq no sebagai array'
		ArrayList seqNoBodyAPI = []

		'looping berdasarkan jumlah dari signAction di dokumen pertama'
		for (int t = 0; t < signActions.size(); t++) {
			'Jika semua data mengenai Sign Location seperti page, llx, lly, urx, ury tidak kosong'
			if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send signExternal)')).length() == 0) && (findTestData(
				excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send signExternal)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(
				GlobalVariable.NumofColm, rowExcel('lly (Send signExternal)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,
				rowExcel('urx (Send signExternal)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send signExternal)')).length() ==
			0))) {
				'Split mengenai signLocation dimana berdasarkan dokumen'
				pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send signExternal)')).split(enter, splitnum)
	
				llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send signExternal)')).split(enter, splitnum)
	
				llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (Send signExternal)')).split(enter, splitnum)
	
				urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send signExternal)')).split(enter, splitnum)
	
				urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send signExternal)')).split(enter, splitnum)
	
				'split mengenai sequence Number per document'
				seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).split(enter, splitnum)
	
				'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer'
				pageSigns = (pageSign[i]).split(delimiter, splitnum)
	
				llxSigns = (llxSign[i]).split(delimiter, splitnum)
	
				llySigns = (llySign[i]).split(delimiter, splitnum)
	
				urxSigns = (urxSign[i]).split(delimiter, splitnum)
	
				urySigns = (urySign[i]).split(delimiter, splitnum)
	
				'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer. Didapatlah semua lokasi signLocation di satu signer.'
				pageSigns = (pageSigns[t]).split(semicolon, splitnum)
	
				llxSigns = (llxSigns[t]).split(semicolon, splitnum)
	
				llySigns = (llySigns[t]).split(semicolon, splitnum)
	
				urxSigns = (urxSigns[t]).split(semicolon, splitnum)
	
				urySigns = (urySigns[t]).split(semicolon, splitnum)
	
				'looping menuju jumlah lokasi pageSign di 1 signer'
				for (int l = 0; l < pageSigns.size(); l++) {
					if (!(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).length() == 0)) {
					'split seq number per signer'
					seqNos = (seqNo[i]).split(semicolon, splitnum)
	
					'looping mengenai total sequence number'
					for (int p = 0; p < seqNos.size(); p++) {
						'jika seq numbernya tidak kosong'
						if ((seqNos[p]) != '') {
							'Memasukkan value seqNo dan body API kepada array'
							seqNoBodyAPI.add(',"seqNo": ' + (seqNos[p]))
						} else {
							'Jika seq number kosong ,maka input kosong'
							seqNoBodyAPI.add('')
						}
					}
					} else {
							seqNoBodyAPI.add('')
						}
					'Jika loopingan pertama'
					if (l == 0) {
						'Jika dari loopingan pertama, pageSignnya hanya ada 1 dan yang terakhir'
						if (l == (pageSigns.size() - 1)) {
							if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
								'Isi bodyAPI'
								bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')
	
								'Jika pageSign untuk yang pertama di signer pertama kosong'
								if ((pageSigns[l]) == '') {
									'Input body mengenai llx dan lly'
									bodyAPI = bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ']'
								} else if ((llxSigns[l]) == '""') {
									'Input body mengenai page'
									bodyAPI = bodyAPI + bodyLocationPage(pageSigns[l]) + ']'
								} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
									'Input body mengenai page dan llx,lly,urx, dan ury'
									bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l], pageSigns[l]) + ']'
								}
								
								'Jika loopingan sudah di akhir'
								if (t == (signActions.size() - 1)) {
									'isi signlocStoreDB'
									signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l])
								} else {
									'isi signlocStoreDB'
									signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ';'
								}
							}
						} else {
							if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
								'Isi bodyAPI'
								bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')
	
								'Jika pageSign yang pertama di signer pertama kosong'
								if ((pageSigns[l]) == '') {
									'Input body mengenai x dan y'
									bodyAPI = bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ','
								} else if ((llxSigns[l]) == '""') {
									'Input body mengenai page'
									bodyAPI = bodyAPI + bodyLocationPage(pageSigns[l]) + ','
								} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
									'Input body mengenai page dan koordinat'
									bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l], pageSigns[l]) + ','
								}
								
								'isi signlocStoreDB'
								signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ';'
							}
						}
					} else if (l == (pageSigns.size() - 1)) {
						if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
							'Jika pageSign yang pertama di signer pertama kosong'
							if ((pageSigns[l]) == '') {
								'Input body mengenai koordinat'
								bodyAPI = bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ']'
							} else if ((llxSigns[l]) == '""') {
								'Input body mengenai page'
								bodyAPI = bodyAPI + bodyLocationPage(pageSigns[l]) + ']'
							} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l], pageSigns[l]) + ']'
							}
						}
						
						'Jika loopingan sudah di akhir'
						if (t == (signActions.size() - 1)) {
							'isi signlocStoreDB'
							signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l])
						} else {
							'isi signlocStoreDB'
							signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ';'
						}
					} else {
						if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
							'Jika pageSign yang pertama di signer pertama kosong'
							if ((pageSigns[l]) == '') {
								'Input body mengenai koordinat'
								bodyAPI = bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ','
							} else if ((llxSigns[l]) == '""') {
								'Input body mengenai page'
								bodyAPI = bodyAPI + bodyLocationPage(pageSigns[l]) + ','
							} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l], pageSigns[l]) + ','
							}
						}
						
						'isi signlocStoreDB'
						signlocStoreDB = signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[l]) + ';'
					}
				}
			}
			
			'Jika signAction yang pertama untuk dokumen pertama'
			if (t == 0) {
				if (t == (signActions.size() - 1)) {
					'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI = '"signers" : [' + bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + '],'
				} else {
					'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI = '"signers" : [' + bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + ','
				}
			} else if (t == (signActions.size() - 1)) {
					'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI =  bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + '],'
			} else {
				'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI = bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + ','
			}
			
			'check ada value maka setting email service tenant'
			if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service (Send External)')).length() > 0) {
				'setting email service tenant'
				CustomKeywords.'connection.APIFullService.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(excelPathAPISendDoc).getValue(
						GlobalVariable.NumofColm, rowExcel('Setting Email Service (Send External)')), (emails[t]).replace('"', ''))
			}
			
			'Memasukkan bodyAPI ke stringRefno'
			stringRefno = (stringRefno + bodyAPI)
	
			'Mengkosongkan bodyAPI untuk digunakan selanjutnya'
			bodyAPI = ''
		}
		
		'Deklarasi bodyAPI kembali'
		bodyAPI = new String()
	
		'Jika dokumennya menggunakan base64'
		if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document (Send External)')) == 'Yes') {
			'input bodyAPI dengan Base64'
			bodyAPI = (((bodyAPI + '"documentFile": "') + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[
				i])) + '"')
		} else {
			'input bodyAPI tidak dengan Base64'
			bodyAPI = (((bodyAPI + '"documentFile": "') + (documentFile[i])) + '"')
		}
		
		'Input bodyAPI ke stringRefno'
		stringRefno = (stringRefno + bodyAPI)
	
		'Mengkosongkan bodyAPI'
		bodyAPI = ''
	
		bodyAPI = setBodyForStampingLocation(pageStamp[i], llxStamp[i], llyStamp[i], urxStamp[i], uryStamp[i], bodyAPI)
		
		'jika dokumennya di akhir'
		if (i == documentFile.size() - 1) {
			'input body API berdasarkan bodyAPI diatasnya'
			bodyAPI = (bodyAPI + '}')
		} else {
			'input body API berdasarkan bodyAPI diatasnya'
			bodyAPI = (bodyAPI + '},')
		}
		
		'input body API kedalam stringRefno'
		stringRefno = (stringRefno + bodyAPI)
	}
		ArrayList returning = []
		
		returning.add(stringRefno)
		
		returning.add(signlocStoreDB)
		
		return returning

}

def setBodyForStampingLocation(String pageStamp, String llxStamp, String llyStamp, String urxStamp, String uryStamp, String bodyAPI) {
	'Jika informasi di excel mengenai stampLocation seperti page dan koordinat ada, maka'
	if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send stampExternal)')).length() == 0) && (findTestData(excelPathAPISendDoc).getValue(
		GlobalVariable.NumofColm, rowExcel('llx (Send stampExternal)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,
		rowExcel('lly (Send stampExternal)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send stampExternal)')).length() == 0)) &&
	(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send stampExternal)')).length() == 0))) {

	'Splitting dari dokumen pertama per signer mengenai stamping'
	pageStamps = (pageStamp).split(semicolon, splitnum)
	
	llxStamps = (llxStamp).split(semicolon, splitnum)

	llyStamps = (llyStamp).split(semicolon, splitnum)

	urxStamps = (urxStamp).split(semicolon, splitnum)

	uryStamps = (uryStamp).split(semicolon, splitnum)

	'looping berdasarkan pagestamp per dokumen'
		for (int b = 0; b < pageStamps.size(); b++) {
			'Jika dia loopingan yang pertama'
			if (b == 0) {
				if (b == (pageStamps.size() - 1)) {
					if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
						'Isi bodyAPI'
						bodyAPI = (bodyAPI + ',"stampLocations": [')
						if ((pageStamps[b]) == '') {
							'Input body mengenai koordinat'
							bodyAPI = bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b]) + ']'
						} else if ((llxStamps[b]) == '""') {
							'Input body mengenai page'
							bodyAPI = bodyAPI + bodyLocationPage(pageStamps[b]) + ']'
						} else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
							'Input body mengenai page dan koordinat'
							bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b], pageStamps[b]) + ']'
						}
					}
				} else {
					if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
						'Isi bodyAPI'
						bodyAPI = (bodyAPI + ',"stampLocations": [')

						'Jika pageStampnya kosong'
						if ((pageStamps[b]) == '') {
							'Input body mengenai koordinat'
							bodyAPI = bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b]) + ','
						} else if ((llxStamps[b]) == '""') {
							'Input body mengenai page'
							bodyAPI = bodyAPI + bodyLocationPage(pageStamps[b]) + ','
						} else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
							'Input body mengenai page dan koordinat'
							bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b], pageStamps[b]) + ','
						}
					}
				}
			} else if (b == (pageStamps.size() - 1)) {
				if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
					'Jika pageStampnya kosong'
					if ((pageStamps[b]) == '') {
						'Input body mengenai koordinat'
						bodyAPI = bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b]) + ']'
			} else if ((llxStamps[b]) == '""') {
						'Input body mengenai page'
						bodyAPI = bodyAPI + bodyLocationPage(pageStamps[b]) + ']'
					} else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
						'Input body mengenai page dan koordinat'
						bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b], pageStamps[b]) + ']'
					}
				}
			} else {
				if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
					'Jika pageStampnya kosong'
					if ((pageStamps[b]) == '') {
						'Input body mengenai koordinat'
						bodyAPI = bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b]) + ','
					} else if ((llxStamps[b]) == '""') {
						'Input body mengenai page'
						bodyAPI = bodyAPI + bodyLocationPage(pageStamps[b]) + ','
					} else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
						'Input body mengenai page dan koordinat'
						bodyAPI = bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[b], pageStamps[b]) + ','
					}
				}
			}
		}
	}
	
	return bodyAPI
}


def bodyLocationCoordinate(String llxSigns, String llySigns, String urxSigns, String urySigns) {
	return '{"llx":' + llxSigns + ',"lly":' + llySigns +',"urx":' + urxSigns + ',"ury":' + urySigns +'}'
}

def bodyLocationPage(String pageSigns) {
	return '{"page":' + pageSigns + '}'
}

def bodyLocationCoordinatePage(String llxSigns, String llySigns, String urxSigns, String urySigns, String pageSigns) {
	return  '{"page":' + pageSigns + ',"llx":' + llxSigns + ',"lly":' + llySigns + ',"urx":' + urxSigns + ', "ury":' + urySigns + '}'
}

def bodySigner(String signActions, String signerTypes, String tlps, String idKtps, String emails, String bodyAPI) {
	return '{"signAction": ' + signActions + ',"signerType": ' + signerTypes + ',"tlp": ' + tlps + ',"idKtp": ' + idKtps + ',"email": ' + emails + bodyAPI + '}'
}
