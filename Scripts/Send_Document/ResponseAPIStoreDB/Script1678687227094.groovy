import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList arrayMatch = []

docid = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 6).replace('[', '').replace(']', '').split(', ', -1)

for (int i = 0; i < docid.size(); i++) {
    'get data API Send Document dari DB (hanya 1 signer)'
    ArrayList<String> result = CustomKeywords.'connection.SendSign.getSendDoc'(conneSign, docid[i])

	'get data psre code'
	String psreCodeDB = CustomKeywords.'connection.SendSign.getVendorCodeUsingDocId'(conneSign, docid[i].toString())

	'Mengambil email berdasarkan documentId'
	ArrayList<String> emailSigner = CustomKeywords.'connection.SendSign.getEmailLogin'(conneSign, docid[i]).split(';', -1)

	'split email dari excel'
	email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).replace('"','').split(';', -1)
	
	'split signer type dari excel'
	signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"','').split(';', -1)
	
	for(int r = 0 ; r < emailSigner.size() ; r++) {
		ArrayList<String> resultStoreEmailandType = CustomKeywords.'connection.SendSign.getSendDocForEmailAndSignerType'(conneSign, docid[i], emailSigner[r])
		
		'declare arrayindex'
		arrayindex = 0

		'verify email'
		arrayMatch.add(WebUI.verifyMatch(email[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify signerType'
		arrayMatch.add(WebUI.verifyMatch(signerType[r], resultStoreEmailandType[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
	}
	'declare arrayindex'
	arrayindex = 0

    'verify tenant code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ref_number'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document_id'
    arrayMatch.add(WebUI.verifyMatch(docid[i], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document template code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify is sequence'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify psre/vendor code'
	if (WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', ''), 
    result[arrayindex], false, FailureHandling.CONTINUE_ON_FAILURE)) {
		'verify psre/vendor code == true'
		arrayMatch.add(true)	
	} else {
		'verify psre Code'
		arrayMatch.add(WebUI.verifyMatch(psreCodeDB, result[arrayindex], false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	
	arrayindex++
	
    'verify success url'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify upload url'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify total document'
	arrayMatch.add(WebUI.verifyEqual(docid.size(),Integer.parseInt(result[arrayindex++]), FailureHandling.CONTINUE_ON_FAILURE))

    'verify sign Action : hardcode. Yang penting tidak boleh kosong'
    arrayMatch.add(WebUI.verifyNotMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).replace('"', 
                ''), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

