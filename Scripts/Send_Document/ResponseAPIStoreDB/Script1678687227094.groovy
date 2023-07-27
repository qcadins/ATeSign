import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'Inisialisasi tenant Code'
tenantCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9)

'Inisialisasi ref No berdasarkan delimiter ;'
refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11)

'Inisialisasi document template code berdasarkan delimiter ;'
documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(semicolon, splitnum)

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
documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(enter, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitnum)

'Inisialisasi successUrl berdasarkan delimiter ;'
successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).split(semicolon, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitnum)

'split signer untuk doc1 dan signer untuk doc2'
signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(enter, splitnum)

signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(enter, splitnum)

signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(enter, splitnum)

alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(enter, splitnum)

jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(enter, splitnum)

kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(enter, splitnum)

kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(enter, splitnum)

kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(enter, splitnum)

kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(enter, splitnum)

nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(enter, splitnum)

tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(enter, splitnum)

tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(enter, splitnum)

provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(enter, splitnum)

idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(enter, splitnum)

tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(enter, splitnum)

email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(enter, splitnum)

npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(enter, splitnum)

idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42)

signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43)

docid = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 6).replace('[', '').replace(']', '').split(', ', -1)

for (int i = 0; i < docid.size(); i++) {
	'Split result dari signerType per signer berdasarkan excel yang telah displit per dokumen. '
	signerTypeExcel = (signerType[i]).replace('"', '').split(semicolon, splitnum)

	'Splitting email berdasarkan excel per dokumen'
	emailExcel = (email[i]).replace('"', '').split(semicolon, splitnum)

	'Splitting email berdasarkan excel per dokumen'
	emailExcel = (email[i]).replace('"', '').split(semicolon, splitnum)

	'get current date'
	currentDate = new Date().format('yyyy-MM-dd')
	
	'get data API Send Document dari DB (hanya 1 signer)'
	ArrayList result = CustomKeywords.'connection.SendSign.getSendDoc'(conneSign, docid[i])

	'Mengambil email berdasarkan documentId'
	ArrayList emailSigner = CustomKeywords.'connection.SendSign.getEmailLogin'(conneSign, docid[i]).split(';', -1)

	for(int r = 0 ; r < emailExcel.size() ; r++) {
		if ((emailExcel[r]) == '') {
			'Splitting email berdasarkan excel per dokumen'
			idKtpExcel = (idKtp[i]).replace('"', '').split(semicolon, splitnum)

			(emailExcel[r]) = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(idKtpExcel[r])
		}
  
		ArrayList resultStoreEmailandType = CustomKeywords.'connection.SendSign.getSendDocForEmailAndSignerType'(conneSign, docid[i], emailSigner[r])
		
		if (resultStoreEmailandType.size() > 1) {
			'declare arrayindex'
			arrayindex = 0

			'verify email'
			arrayMatch.add(WebUI.verifyMatch(emailExcel[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

			'verify signerType'
			arrayMatch.add(WebUI.verifyMatch(signerTypeExcel[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

			'Get value db mengenai seq Number berdasarkan signer type dan doc template'
			seqNoBasedOnDocTemplate = CustomKeywords.'connection.APIFullService.getSeqNoBasedOnDocTemplate'(conneSign,
			(documentTemplateCode[i]).replace('"', ''), resultStoreEmailandType[(arrayindex - 1)])

			'verify sequence number'
			arrayMatch.add(WebUI.verifyMatch(seqNoBasedOnDocTemplate.toString(), resultStoreEmailandType[arrayindex++],
			false, FailureHandling.CONTINUE_ON_FAILURE))
		}		
	}
	'declare array index'
	arrayindex = 0

    'verify tenant code'
    arrayMatch.add(WebUI.verifyMatch(tenantCode.replace('"',''),result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ref_number'
    arrayMatch.add(WebUI.verifyMatch(refNo.replace('"', ''),result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document_id'
    arrayMatch.add(WebUI.verifyMatch(docid[i], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document template code'
    arrayMatch.add(WebUI.verifyMatch(documentTemplateCode[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office code'
    arrayMatch.add(WebUI.verifyMatch(officeCode[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office name'
    arrayMatch.add(WebUI.verifyMatch(officeName[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region code'
    arrayMatch.add(WebUI.verifyMatch(regionCode[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region name'
    arrayMatch.add(WebUI.verifyMatch(regionName[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line code'
    arrayMatch.add(WebUI.verifyMatch(businessLineCode[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line name'
    arrayMatch.add(WebUI.verifyMatch(businessLineName[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify is sequence'
    arrayMatch.add(WebUI.verifyMatch(isSequence[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'get data psre code'
	String psreCodeDB = CustomKeywords.'connection.SendSign.getVendorCodeUsingDocId'(conneSign, docid[i])

	'verify psre Code'
	arrayMatch.add(WebUI.verifyMatch(psreCodeDB, result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
    'verify success url'
    arrayMatch.add(WebUI.verifyMatch(successURL[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify upload url'
    arrayMatch.add(WebUI.verifyMatch(uploadURL[i].replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify total document'
	arrayMatch.add(WebUI.verifyEqual(docid.size(),Integer.parseInt(result[arrayindex++]), FailureHandling.CONTINUE_ON_FAILURE))

    'Looping berdasarkan jumlah dari signAction'
    for (int z = 0; z < signAction.size(); z++) {
        'Jika signAction tersebut adalah AT'
        if ((signAction[z]).replace('"', '') == 'at') {
            'Mengambil emailSign dari excel dan displit kembali'
            emailSign = idKtp[i].split(semicolon, splitnum)[z]

            'Mengambil trxno dari column tersebut'
            trxno = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 7)

            'get data result trx untuk signing'
            resulttrxsigning = CustomKeywords.'connection.SendSign.getTrxSendDocSigning'(conneSign, trxno)

            'declare arrayindex'
            arrayindex = 0

            'verify trx no'
            arrayMatch.add(WebUI.verifyMatch(trxno, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify ref no di trx'
            arrayMatch.add(WebUI.verifyMatch(refNo, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify date req di trx'
            arrayMatch.add(WebUI.verifyMatch(currentDate, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify trx qty = splitnum'
            arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify trx autosign'
            arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], ('Auto Sign (' + emailSign) + ')', false, FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

