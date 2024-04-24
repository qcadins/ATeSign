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

indexTrx = 0

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
arrayMatch = []

'check active atau tidaknya document based on nomor kontrak + "-"'
String checkActiveDocument = CustomKeywords.'connection.SendSign.getCheckingActiveDocument'(conneSign, findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')))

'jika check active document tidak kosong, maka ada document yang masih aktif padahal secara nomor kontrak sudah ada _'
if (checkActiveDocument != '') {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + semicolon) +
		'<Nomor Kontrak ' + checkActiveDocument + 'masih aktif>')
}

'Mengambil documentid di excel dan displit'
docid = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', splitnum)

'inisialisasi varaible pada value-value yang diperlukan'
signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(
    enter, splitnum)

seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).split(enter, 
    splitnum)

tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(enter, 
    splitnum)

idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).split(enter, 
    splitnum)

email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(enter, 
    splitnum)

refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

'looping berdasarkan jumlah dari document id '
for (int i = 0; i < docid.size(); i++) {
    'Inisialisasi document template code berdasarkan delimiter ;'
    documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode')).split(
        semicolon, splitnum)

    'Inisialisasi document name berdasarkan delimiter ;'
    documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentName')).split(
        semicolon, splitnum)

    'Inisialisasi office Code berdasarkan delimiter ;'
    officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')).split(
        semicolon, splitnum)

    'Inisialisasi office name berdasarkan delimiter ;'
    officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeName')).split(
        semicolon, splitnum)

    'Inisialisasi region code berdasarkan delimiter ;'
    regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')).split(
        semicolon, splitnum)

    'Inisialisasi region name berdasarkan delimiter ;'
    regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionName')).split(
        semicolon, splitnum)

    'Inisialisasi business line code berdasarkan delimiter ;'
    businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode')).split(
        semicolon, splitnum)

    'Inisialisasi business line name berdasarkan delimiter ;'
    businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')).split(
        semicolon, splitnum)

	'Inisialisasi business line name berdasarkan delimiter ;'
	useSignQr = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('QR')).replace('Ya', '1').replace('Tidak', '0').split(
		semicolon, splitnum)
	
    'Inisialisasi is sequence berdasarkan delimiter ;'
    isSequence = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).split(
        semicolon, splitnum)

    'Inisialisasi psre Code berdasarkan delimiter ;'
    psreCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('psreCode')).split(
        semicolon, splitnum)

    'Inisialisasi pageSign berdasarkan delimiter enter'
    pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send signExternal)')).split(
        enter, splitnum)

    'get data API Send Document dari DB (hanya 1 signer)'
    result = CustomKeywords.'connection.APIFullService.getSendDocSigning'(conneSign, docid[i])

    'declare arrayindex'
    arrayindex = 0

	'sign loc displit |'
    signlocStoreDBSplit = signlocStoreDB.split('\\|', -1)

    'Jika documentTemplateCode di dokumen pertama adalah kosong'
    if ((documentTemplateCode[i]).replace('"', '') == '') {
        'Maka pengecekan signlocation yang diinput'
        arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getSignLocation'(conneSign, docid[i]), 
                signlocStoreDBSplit[i], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'jika qrnya kosong'
		if (useSignQr[i] == '') {
			'otomatis 0'
			useSignQr[i] = '0'
		}
		
		'Maka pengecekan use sign qr yang diinput'
        arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getUseSignQR'(conneSign, docid[i]), 
                useSignQr[i], false, FailureHandling.CONTINUE_ON_FAILURE))
    } else {
		'Maka pengecekan use sign qr via document template'
		arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getUseSignQRFromDocTemplate'(conneSign, documentTemplateCode[i]),
				CustomKeywords.'connection.APIFullService.getUseSignQR'(conneSign, docid[i]), false, FailureHandling.CONTINUE_ON_FAILURE))
	}
    
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'declare array index'
    arrayindex = 0

    'Split result dari signerType per signer berdasarkan excel yang telah displit per dokumen. '
    signerTypeExcel = (signerType[i]).replace('"', '').split(semicolon, splitnum)

    'Splitting email berdasarkan excel per dokumen'
    emailExcel = (email[i]).replace('"', '').split(semicolon, splitnum)
    
	'looping per email'
    for (int r = 0; r < emailExcel.size(); r++) {
		'jika email excelnya kosong'
        if ((emailExcel[r]) == '') {
            'Splitting email berdasarkan excel per dokumen'
            idKtpExcel = (idKtp[i]).replace('"', '').split(semicolon, splitnum)

			'convert id ktp menjadi sha256'
            (emailExcel[r]) = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(idKtpExcel[r])
        }
        
		'get email dan signer type pada send doc'
        ArrayList resultStoreEmailandType = CustomKeywords.'connection.APIFullService.getSendDocForEmailAndSignerType'(conneSign, 
            docid[i], emailExcel[r])

		'jika result email dan signer type sizenya lebih dari 1'
        if (resultStoreEmailandType.size() > 1) {
            'declare arrayindex'
            arrayindex = 0

            'verify email'
            arrayMatch.add(WebUI.verifyMatch(emailExcel[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify signerType'
            arrayMatch.add(WebUI.verifyMatch(signerTypeExcel[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

			'verify dormant'
			arrayMatch.add(WebUI.verifyMatch('0', CustomKeywords.'connection.DataVerif.getDormantUser'(conneSign, emailExcel[r]), false, FailureHandling.CONTINUE_ON_FAILURE))

            'Jika documentTemplateCode di dokumen pertama adalah kosong'
            if ((documentTemplateCode[i]).replace('"', '') == '') {
                'Jika is sequence nya bukan 0'
                if ((isSequence[i]).replace('"', '') != '0') {
					if (!findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).length() == 0) {
						'Inisialisasi sequence number berdasarkan delimiter enter'
						seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).split(
							enter, splitnum)
						
						'Splitting sequence number berdasarkan excel per signer'
						seqNoExcel = (seqNo[i]).split(semicolon, splitnum)

                    'Jika looping i lebih kecil dari sequence number'
                    if (i < seqNoExcel.size()) {
                        'verify sequence number'
                        arrayMatch.add(WebUI.verifyMatch((seqNoExcel[r]).replace('"', ''), resultStoreEmailandType[arrayindex++], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
					}
                }
            } else {
                'Get value db mengenai seq Number berdasarkan signer type dan doc template'
                seqNoBasedOnDocTemplate = CustomKeywords.'connection.APIFullService.getSeqNoBasedOnDocTemplate'(conneSign, 
                    (documentTemplateCode[i]).replace('"', ''), resultStoreEmailandType[(arrayindex - 1)])

                'verify sequence number'
                arrayMatch.add(WebUI.verifyMatch(seqNoBasedOnDocTemplate.toString(), resultStoreEmailandType[arrayindex++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
    }
    
    'declare arrayindex'
    arrayindex = 0

    'get data psre code'
    psreCodeDB = CustomKeywords.'connection.APIFullService.getVendorCodeUsingDocId'(conneSign, docid[i])

    psreCodeResult = (result[arrayindex++])

    'Jika verify psre Code sesuai'
    if (WebUI.verifyMatch(psreCodeDB, psreCodeResult, false, FailureHandling.CONTINUE_ON_FAILURE) && WebUI.verifyMatch(psreCodeDB, 
        (responsePsreCode[i]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE)) {
        arrayMatch.add(true)

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('PsRE Document') - 
            1, GlobalVariable.NumofColm - 1, psreCodeDB)
    } else {
        arrayMatch.add(false)
    }
    
    'verify tenant code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Tenant')).replace(
                '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ref number'
    arrayMatch.add(WebUI.verifyMatch(refNo.replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document_id'
    arrayMatch.add(WebUI.verifyMatch(docid[i], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document template code'
    arrayMatch.add(WebUI.verifyMatch((documentTemplateCode[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office code'
    arrayMatch.add(WebUI.verifyMatch((officeCode[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office name'
    arrayMatch.add(WebUI.verifyMatch((officeName[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region code'
    arrayMatch.add(WebUI.verifyMatch((regionCode[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region name'
    arrayMatch.add(WebUI.verifyMatch((regionName[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line code'
    arrayMatch.add(WebUI.verifyMatch((businessLineCode[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line name'
    arrayMatch.add(WebUI.verifyMatch((businessLineName[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify total document'
    arrayMatch.add(WebUI.verifyMatch(docid.size().toString(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify is Sequence'
    arrayMatch.add(WebUI.verifyMatch((isSequence[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    if (psreCodeDB == 'PRIVY') {
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send stampExternal)')) != 
        '') {
            'Jika documentTemplateCode di dokumen pertama adalah kosong'
            if ((documentTemplateCode[i]).replace('"', '') != '') {
                'ambil data privy sign location based on document_template'
                arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(conneSign, 
                            docid[i]), CustomKeywords.'connection.APIFullService.getTemplateDocPrivyStampLoc'(conneSign, 
                            docid[i]), false, FailureHandling.CONTINUE_ON_FAILURE))
            } else {
                'pastikan privy sign loc tidak null'
                arrayMatch.add(WebUI.verifyNotMatch('null', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                            conneSign, docid[i]), false, FailureHandling.CONTINUE_ON_FAILURE))

                'pastikan privy sign loc tidak kosong'
                arrayMatch.add(WebUI.verifyNotMatch('', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                            conneSign, docid[i]), false, FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
    } 
	
	if (psreCodeDB != 'PRIVY') {
        'split signer untuk doc1 dan signer untuk doc2'
        signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).split(
            enter, splitnum)

            signActions = (signAction[i]).split(semicolon, splitnum)

            for (loopPerSignActionPerSigner = 0; loopPerSignActionPerSigner < signActions.size(); loopPerSignActionPerSigner++) {
                'Jika signAction tersebut adalah AT'
                if ((signActions[loopPerSignActionPerSigner]).replace('"', '') == 'at') {
                    'Mengambil trxno dari column tersebut'
                    trxno = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('trxNo')).split(
                        ', ', -1)

                    'Mengambil emailSign dari excel dan displit kembali'
                    emailSign = ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$email')).replace(
                        '"', '').split(enter, splitnum)[i]).split(semicolon, splitnum)[loopPerSignActionPerSigner])

					'jika emailnya kosong, maka get email dari NIK'
					if (emailSign == '') {
						'Mengambil emailSign dari excel dan displit kembali'
						ktpSign = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).replace('"', '').split(enter, splitnum)[i].split(semicolon, splitnum)[loopPerSignActionPerSigner])
					
						emailSign = CustomKeywords.'connection.DataVerif.getEmailFromNIK'(conneSign, ktpSign)
					}

                    'get data result trx untuk signing'
                    resulttrxsigning = CustomKeywords.'connection.APIFullService.getTrxSendDocSigning'(conneSign, trxno[indexTrx])

                    'declare arrayindex'
                    arrayindex = 0

                    'verify trx no'
                    arrayMatch.add(WebUI.verifyMatch(trxno[indexTrx++], resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify ref no di trx'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$referenceNo')).replace('"', ''), resulttrxsigning[arrayindex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify date req di trx'
                    arrayMatch.add(WebUI.verifyMatch(currentDate, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx qty = splitnum'
                    arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx autosign'
                    arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], ('Auto Sign (' + emailSign) + ')', 
                            false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify office code'
					arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], officeCode[i],
							false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify region code'
					arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], regionCode[i],
							false, FailureHandling.CONTINUE_ON_FAILURE))
					
					'verify business line code'
					arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], businessLineCode[i],
							false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            }
	}
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + semicolon) + 
            GlobalVariable.ReasonFailedStoredDB)
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

