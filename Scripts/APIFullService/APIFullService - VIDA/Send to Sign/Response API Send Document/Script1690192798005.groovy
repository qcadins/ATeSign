import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'Inisalisasi base url dengan perubahan base url pada saat menggunakan stamping'
GlobalVariable.base_url = findTestData(excelPathSetting).getValue(7,2)

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'Deklarasi variable mengenai signLoc untuk store db'
String signlocStoreDB = new String()

getDataExcel(semicolon, splitnum, delimiter, enter)

'Deklarasi variable string Ref no untuk full body API.'
String stringRefno = new String()

ArrayList split = []

split = setBodyAPI(semicolon, splitnum, delimiter, enter, stringRefno, signlocStoreDB)

stringRefno = split[0]

signlocStoreDB = split[1]

'Jika flag tenant no'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 47) == 'No') {
    'set tenant kosong'
    GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 48)
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 47) == 'Yes') {
    'Input tenant'
    GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
}

'check if mau menggunakan api_key yang salah atau benar'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 45) == 'Yes') {
    'get api key dari db'
    GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 45) == 'No') {
    'get api key salah dari excel'
    GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 46)
}

'Hit API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, 9), ('request') : stringRefno, ('callerId') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, 43)]))

'jika response 200 / hit api berhasil'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'ambil respon text dalam bentuk code.'
    status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'jika status codenya 0, verifikasi datanya benar'
    if (status_Code == 0) {
        'mengambil documentid dan trxno dari hasil response API'
        documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)

        trxno = WS.getElementPropertyValue(respon, 'documents.trxNos', FailureHandling.OPTIONAL)
		
		'Memasukkan documentid dan trxno ke dalam excel'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5, GlobalVariable.NumofColm -
		1, documentId.toString().replace('[', '').replace(']', ''))

		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 6, GlobalVariable.NumofColm -
		1, trxno.toString().replace('[', '').replace(']', ''))

		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
		1, GlobalVariable.StatusSuccess)
		
    'Jika check storedb'
    if (GlobalVariable.checkStoreDB == 'Yes') {
        'call test case storedb'
		WebUI.callTestCase(findTestCase('APIFullService/Response API Send Document Store DB'), [('excelPathAPISendDoc') : excelPathAPISendDoc
		, ('sheet') : sheet, ('signlocStoreDB') : signlocStoreDB], FailureHandling.CONTINUE_ON_FAILURE)
    }
    } else {
        'Mengambil message Failed'
        messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + semicolon) + messageFailed)
    }
} else {
    'mengambil message Failed'
    messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + semicolon) + messageFailed.toString())
}


def getDataExcel(String semicolon, int splitnum, String delimiter, String enter) {
    'Inisialisasi ref No berdasarkan delimiter ;'
     refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11)

    'Inisialisasi document template code berdasarkan delimiter ;'
     documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 12).split(semicolon, splitnum)

    'Inisialisasi document name berdasarkan delimiter ;'
     documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 13).split(semicolon, splitnum)

    'Inisialisasi office Code berdasarkan delimiter ;'
     officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 14).split(semicolon, splitnum)

    'Inisialisasi office name berdasarkan delimiter ;'
     officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 15).split(semicolon, splitnum)

    'Inisialisasi region code berdasarkan delimiter ;'
     regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 16).split(semicolon, splitnum)

    'Inisialisasi region name berdasarkan delimiter ;'
     regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 17).split(semicolon, splitnum)

    'Inisialisasi business line code berdasarkan delimiter ;'
     businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 18).split(semicolon, splitnum)

    'Inisialisasi business line name berdasarkan delimiter ;'
     businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitnum)

	'Inisialisasi is sequence berdasarkan delimiter ;'
	 isSequence = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitnum)

	'Inisialisasi is sequence berdasarkan delimiter ;'
	 psreCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitnum)

    'Inisialisasi document file berdasarkan delimiter ;'
     documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 22).split(enter, splitnum)

    'split signer untuk doc1 dan signer untuk doc2'
     signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 30).split(enter, splitnum)

     signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 31).split(enter, splitnum)

	 seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 32).split(enter, splitnum)
	
     tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 33).split(enter, splitnum)

     idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 34).split(enter, splitnum)

     email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 35).split(enter, splitnum)

     pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).split(delimiter, splitnum)

     llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).split(delimiter, splitnum)

     llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 26).split(delimiter, splitnum)

     urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 27).split(delimiter, splitnum)

     uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).split(delimiter, splitnum)
}

def setBodyAPI(String semicolon, int splitnum, String delimiter, String enter, String stringRefno, String signlocStoreDB) {
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

		'Splitting dari dokumen pertama per signer mengenai stamping'
		pageStamps = (pageStamp[i]).split(semicolon, splitnum)

		llxStamps = (llxStamp[i]).split(semicolon, splitnum)

		llyStamps = (llyStamp[i]).split(semicolon, splitnum)

		urxStamps = (urxStamp[i]).split(semicolon, splitnum)

		uryStamps = (uryStamp[i]).split(semicolon, splitnum)

		'inisialisasi bodyAPI untuk menyusun body'
		String bodyAPI = new String()

		'Pengisian body'
		bodyAPI = (((((((((((((((((((((((bodyAPI + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + (documentTemplateCode[
		i])) + ', "documentName": ') + (documentName[i])) + ', "officeCode": ') + (officeCode[i])) + ', "officeName": ') +
		(officeName[i])) + ', "regionCode": ') + (regionCode[i])) + ', "regionName": ') + (regionName[i])) + ', "businessLineCode": ') +
		(businessLineCode[i])) + ', "businessLineName": ') + (businessLineName[i])) + ', "isSequence": ') + (isSequence[
		i])) + ',  "psreCode": ') + (psreCode[i])) + ',')

		'Memasukkan bodyAPI ke stringRefno'
		stringRefno = (stringRefno + bodyAPI)

		'inisialisasi bodyAPI untuk menyusun body'
		bodyAPI = new String()

		'inisialisasi body untuk seq no sebagai array'
		ArrayList seqNoBodyAPI = []

		'looping berdasarkan jumlah dari signAction di dokumen pertama'
		for (int t = 0; t < signActions.size(); t++) {
			'Jika semua data mengenai Sign Location seperti page, llx, lly, urx, ury tidak kosong'
			if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).length() == 0) && (findTestData(
				excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 38).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(
				GlobalVariable.NumofColm, 39).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,
				40).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 41).length() ==
			0))) {
				'Split mengenai signLocation dimana berdasarkan dokumen'
				pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).split(enter, splitnum)

				llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 38).split(enter, splitnum)

				llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 39).split(enter, splitnum)

				urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 40).split(enter, splitnum)

				urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 41).split(enter, splitnum)

				'split mengenai sequence Number'
				seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 32).split(enter, splitnum)

				'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer'
				def pageSigns = (pageSign[i]).split(delimiter, splitnum)

				def llxSigns = (llxSign[i]).split(delimiter, splitnum)

				def llySigns = (llySign[i]).split(delimiter, splitnum)

				def urxSigns = (urxSign[i]).split(delimiter, splitnum)

				def urySigns = (urySign[i]).split(delimiter, splitnum)

				'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer. Didapatlah semua lokasi signLocation di satu signer.'
				pageSigns = (pageSigns[t]).split(semicolon, splitnum)

				llxSigns = (llxSigns[t]).split(semicolon, splitnum)

				llySigns = (llySigns[t]).split(semicolon, splitnum)

				urxSigns = (urxSigns[t]).split(semicolon, splitnum)

				urySigns = (urySigns[t]).split(semicolon, splitnum)

				'looping menuju jumlah lokasi pageSign di 1 signer'
				for (int l = 0; l < pageSigns.size(); l++) {
					'split seq number per documentnya'
					def seqNos = (seqNo[i]).split(semicolon, splitnum)

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
									bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
									l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '}]')
								} else if ((llxSigns[l]) == '""') {
									'Input body mengenai page'
									bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '}]')
								} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
									'Input body mengenai page dan llx,lly,urx, dan ury'
									bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') +
									(llxSigns[l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) +
									', "ury" : ') + (urySigns[l])) + '}]')
								}
								
								'Jika loopingan sudah di akhir'
								if (t == (signActions.size() - 1)) {
									'isi signlocStoreDB'
									signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') +
									(llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '}')
								} else {
									'isi signlocStoreDB'
									signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') +
									(llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
								}
							}
						} else {
							if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
								'Isi bodyAPI'
								bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')

								'Jika pageSign yang pertama di signer pertama kosong'
								if ((pageSigns[l]) == '') {
									'Input body mengenai x dan y'
									bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
									l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '},')
								} else if ((llxSigns[l]) == '""') {
									'Input body mengenai page'
									bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '},')
								} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
									'Input body mengenai page dan koordinat'
									bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') +
									(llxSigns[l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) +
									', "ury" : ') + (urySigns[l])) + '},')
								}
								
								'isi signlocStoreDB'
								signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') +
								(llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
							}
						}
					} else if (l == (pageSigns.size() - 1)) {
						if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
							'Jika pageSign yang pertama di signer pertama kosong'
							if ((pageSigns[l]) == '') {
								'Input body mengenai koordinat'
								bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
								l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '}]')
							} else if ((llxSigns[l]) == '""') {
								'Input body mengenai page'
								bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '}]')
							} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
								'Input body mengenai page dan koordinat'
								bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + (llxSigns[
								l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') +
								(urySigns[l])) + '}]')
							}
						}
						
						'Jika loopingan sudah di akhir'
						if (t == (signActions.size() - 1)) {
							'isi signlocStoreDB'
							signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
							l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '}')
						} else {
							'isi signlocStoreDB'
							signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
							l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
						}
					} else {
						if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
							'Jika pageSign yang pertama di signer pertama kosong'
							if ((pageSigns[l]) == '') {
								'Input body mengenai koordinat'
								bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
								l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '},')
							} else if ((llxSigns[l]) == '""') {
								'Input body mengenai page'
								bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '},')
							} else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
								'Input body mengenai page dan koordinat'
								bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + (llxSigns[
								l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') +
								(urySigns[l])) + '},')
							}
						}
						
						'isi signlocStoreDB'
						signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
						l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
					}
				}
			}
			
			'Jika signAction yang pertama untuk dokumen pertama'
			if (t == 0) {
				if (t == (signActions.size() - 1)) {
					'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI = ((((((((((('"signers" : [{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[
					t])) + ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) +
					bodyAPI) + '}],')
				} else {
					'isi bodyAPI dengan bodyAPI yang atas'
					bodyAPI = ((((((((((('"signers" : [{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[
					t])) + ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) +
					bodyAPI) + '},')
				}
			} else if (t == (signActions.size() - 1)) {
				'isi bodyAPI dengan bodyAPI yang atas'
				bodyAPI = ((((((((((('{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[t])) +
				',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + bodyAPI) +
				'}],')
			} else {
				'isi bodyAPI dengan bodyAPI yang atas'
				bodyAPI = ((((((((((('{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[t])) +
				',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + bodyAPI) +
				'},')
			}
			
			'check ada value maka setting email service tenant'
			if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 50).length() > 0) {
				'setting email service tenant'
				CustomKeywords.'connection.APIFullService.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(
						excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 50), (emails[t]).replace('"', ''))
			}
			
			'Memasukkan bodyAPI ke stringRefno'
			stringRefno = (stringRefno + bodyAPI)

			'Mengkosongkan bodyAPI untuk digunakan selanjutnya'
			bodyAPI = ''
		}
		
		'Deklarasi bodyAPI kembali'
		bodyAPI = new String()

		'Jika dokumennya menggunakan base64'
		if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
			'input bodyAPI dengan Base64'
			bodyAPI = (((bodyAPI + '"documentFile": "') + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[i])) + '"')
		} else {
			'input bodyAPI tidak dengan Base64'
			bodyAPI = (((bodyAPI + '"documentFile": "') + (documentFile[i])) + '"')
		}
		
		'Input bodyAPI ke stringRefno'
		stringRefno = (stringRefno + bodyAPI)

		'Mengkosongkan bodyAPI'
		bodyAPI = ''

		'Jika informasi di excel mengenai stampLocation seperti page dan koordinat ada, maka'
		if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).length() == 0) && (findTestData(
			excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(
			GlobalVariable.NumofColm, 26).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,
			27).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).length() ==
		0))) {
			'looping berdasarkan pagestamp per dokumen'
			for (int b = 0; b < pageStamps.size(); b++) {

                    'Jika dia loopingan yang pertama'
                    if (b == 0) {

                        if (b == (pageStamps.size() - 1)) {
							if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {

                            if ((pageStamps[b]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '}]')
                            } else if ((llxStamps[b]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '}]')
                            } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                (uryStamps[b])) + '}]')
                            }
							
                        }
                        } else {
							if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                            'Isi bodyAPI'
                            bodyAPI = (bodyAPI + ',"stampLocations": [')

                            'Jika pageStampnya kosong'
                            if ((pageStamps[b]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '},')
                            } else if ((llxStamps[b]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '},')
                            } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                (uryStamps[b])) + '},')
                            }
                        }
                        }
                    } else if (b == (pageStamps.size() - 1)) {
						if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {

                        'Jika pageStampnya kosong'
                        if ((pageStamps[b]) == '') {
                            'Input body mengenai koordinat'
                            bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[b])) + 
                            ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '}]')
                        } else if ((llxStamps[b]) == '""') {
                            'Input body mengenai page'
                            bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '}]')
                        } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                            'Input body mengenai page dan koordinat'
                            bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                            b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[
                            b])) + '}]')
                        }
						}
                    } else {
						if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                        'Jika pageStampnya kosong'
                        if ((pageStamps[b]) == '') {
                            'Input body mengenai koordinat'
                            bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[b])) + 
                            ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '},')
                        } else if ((llxStamps[b]) == '""') {
                            'Input body mengenai page'
                            bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '},')
                        } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                            'Input body mengenai page dan koordinat'
                            bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                            b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[
                            b])) + '},')
                        }
                    }
                }}
		}
		
		'jika dokumennya di akhir'
		if (i == (documentFile.size() - 1)) {
			'input body API berdasarkan bodyAPI diatasnya'
			bodyAPI = (bodyAPI + '}')
		} else {
			'input body API berdasarkan bodyAPI diatasnya'
			bodyAPI = (bodyAPI + '},')
		}

		'input body API kedalam stringRefno'
		stringRefno = (stringRefno + bodyAPI)
	
		ArrayList returning = []
		
		returning.add(stringRefno)
		
		returning.add(signlocStoreDB)
		
		return returning
	}
}
