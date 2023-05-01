import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get tenant code dari DB'
String resultTenant = CustomKeywords.'connection.dataVerif.getTenant'(conneSign, GlobalVariable.userLogin)

'looping berdasarkan total kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathAPISendDoc).getColumnNumbers(); (GlobalVariable.NumofColm)++) {
	
	'Inisialisasi ref No berdasarkan delimiter ;'
	ArrayList<String> refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

	'Inisialisasi document template code berdasarkan delimiter ;'
	ArrayList<String> documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 12).split(';',
		-1)

	'Inisialisasi office Code berdasarkan delimiter ;'
	ArrayList<String> officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 13).split(';', -1)
	
	'Inisialisasi office name berdasarkan delimiter ;'
	ArrayList<String> officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 14).split(';', -1)

	'Inisialisasi region code berdasarkan delimiter ;'
	ArrayList<String> regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 15).split(';', -1)

	'Inisialisasi region name berdasarkan delimiter ;'
	ArrayList<String> regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 16).split(';', -1)

	'Inisialisasi business line code berdasarkan delimiter ;'
	ArrayList<String> businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 17).split(';',
		-1)

	'Inisialisasi business line name berdasarkan delimiter ;'
	ArrayList<String> businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 18).split(';',
		-1)

	ArrayList<String> documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

	'split signer untuk doc1 dan signer untuk doc2'
	ArrayList<String> signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 27).split('\\|', -1)
	
	ArrayList<String> signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).split('\\|', -1)

	ArrayList<String> tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 29).split('\\|', -1)

	ArrayList<String> idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)
	
	ArrayList<String> email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)

	ArrayList<String> pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 21).split('\\|', -1)
	
	ArrayList<String> llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 22).split('\\|', -1)
	
	ArrayList<String> llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 23).split('\\|', -1)
	
	ArrayList<String> urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).split('\\|', -1)
	
	ArrayList<String> uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).split('\\|', -1)
	
	String stringRefno = new String()
	
	for (int i = 0; i < documentFile.size(); i++) {
		
		(signAction[i]) = (signAction[i]).split(';', -1)
		
		(signerType[i]) = (signerType[i]).split(';', -1)

		(tlp[i]) = (tlp[i]).split(';', -1)

		(idKtp[i]) = (idKtp[i]).split(';', -1)

		(email[i]) = (email[i]).split(';', -1)
		
		(pageStamp[i]) = (pageStamp[i]).split(';', -1)
		
		(llxStamp[i]) = (llxStamp[i]).split(';', -1)
		
		(llyStamp[i]) = (llyStamp[i]).split(';', -1)
		
		(urxStamp[i]) = (urxStamp[i]).split(';', -1)
		
		(uryStamp[i]) = (uryStamp[i]).split(';', -1)
		
		String string = new String()

		string = ''

		string = string + '{"referenceNo" : ' + refNo[i] + ', "documentTemplateCode": ' + documentTemplateCode[
		i] + ', "officeCode": ' + officeCode[i] + ', "officeName": ' + officeName[i] + ', "regionCode": ' + regionName[
		i] + ', "regionName": ' + regionName[i] + ', "businessLineCode": ' + businessLineCode[i] + ', "businessLineName": ' +
		businessLineName[i] + ','

		stringRefno = (stringRefno + string)
		
		string = new String()

		string = ''

		for (int t = 0; t < (signAction[i]).size(); t++) {

			ArrayList<String> pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 33).split('\\|\\|', -1)
			
			ArrayList<String> llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 34).split('\\|\\|', -1)
			
			ArrayList<String> llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 35).split('\\|\\|', -1)
			
			ArrayList<String> urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 36).split('\\|\\|', -1)
			
			ArrayList<String> urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).split('\\|\\|', -1)

			(pageSign[i]) = (pageSign[i]).split('\\|', -1)
			
			(llxSign[i]) = (llxSign[i]).split('\\|', -1)
			
			(llySign[i]) = (llySign[i]).split('\\|', -1)
			
			(urxSign[i]) = (urxSign[i]).split('\\|', -1)
			
			(urySign[i]) = (urySign[i]).split('\\|', -1)
			
			(pageSign[i]) = (pageSign[i][t]).split(';', -1)

			(llxSign[i]) = (llxSign[i][t]).split(';', -1)
			
			(llySign[i]) = (llySign[i][t]).split(';', -1)
			
			(urxSign[i]) = (urxSign[i][t]).split(';', -1)
			
			(urySign[i]) = (urySign[i][t]).split(';', -1)
			
			for(int l = 0 ; l < pageSign[i].size();l++) {
				if(l == 0) {
					string = string + ',"signLocations": [{ "page" : '+ pageSign[i][l] + ', "llx" : '+ llxSign[i][l]+', "lly" : '+ llySign[i][l]+', "urx" : '+ urxSign[i][l]+', "ury" : '+ urySign[i][l]+'},'
				} else if(l == pageSign[i].size() - 1){
					string = string + '{ "page" : '+ pageSign[i][l] + ', "llx" : '+ llxSign[i][l]+', "lly" : '+ llySign[i][l]+', "urx" : '+ urxSign[i][l]+', "ury" : '+ urySign[i][l]+'}]'
				}else {
					string = string + '{ "page" : '+ pageSign[i][l] + ', "llx" : '+ llxSign[i][l]+', "lly" : '+ llySign[i][l]+', "urx" : '+ urxSign[i][l]+', "ury" : '+ urySign[i][l]+'},'
				}
			}
			
			if (t == 0) {
				string = '"signers" : [{"signAction": ' + signAction[i][t] + ',"signerType": ' +
				signerType[i][t] + ',"tlp": ' + tlp[i][t] + ',"idKtp": ' + idKtp[i][t] + ',"email": ' +
				email[i][t] + string + '},'
			} else if (t == signAction[i].size() - 1){
				string = '{"signAction": ' + signAction[i][t] + ',"signerType": ' +
				signerType[i][t] + ',"tlp": ' + tlp[i][t] + ',"idKtp": ' + idKtp[i][t] + ',"email": ' +
				email[i][t] + string + '}],'
			} else {
				string = ',{"signAction": ' + signAction[i][t] + ',"signerType": ' +
				signerType[i][t] + ',"tlp": ' + tlp[i][t] + ',"idKtp": ' + idKtp[i][t] + ',"email": ' +
				email[i][t] + string + '},'
			}
			
			stringRefno = stringRefno + string
			string = ''
		}
		
		string = new ArrayList<String>()

		string = ''
		
			if(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 45) == 'Yes') {
				string = string + '"documentFile": "' + PDFtoBase64(documentFile[i]) + '"'
			}else {
				string = string + '"documentFile": "' + documentFile[i] + '"'
			}

		stringRefno = (stringRefno + string)
		
		string = ''
		
		if(documentTemplateCode[i] == '""') {
			for(int b = 0 ; b < pageStamp[i].size();b++) {
				if(b == 0) {
					string = string + ', "stampLocations" : [{ "page" : '+ pageStamp[i][b] + ', "llx" : '+ llxStamp[i][b]+', "lly" : '+ llyStamp[i][b]+', "urx" : '+ urxStamp[i][b]+', "ury" : '+ uryStamp[i][b]+ '},'				
				} 
				else if(b == pageStamp[i].size() - 1){
					string = string + '{ "page" : '+ pageStamp[i][b] + ', "llx" : '+ llxStamp[i][b]+', "lly" : '+ llyStamp[i][b]+', "urx" : '+ urxStamp[i][b]+', "ury" : '+ uryStamp[i][b]+ '}]'				
				}else {
					string = string + '{ "page" : '+ pageStamp[i][b] + ', "llx" : '+ llxStamp[i][b]+', "lly" : '+ llyStamp[i][b]+', "urx" : '+ urxStamp[i][b]+', "ury" : '+ uryStamp[i][b]+ '},'				
				}
				}
		}
		if (i == documentFile.size() - 1){
			string = string + '}'
		}
		else {
			string = string + '},'
		}
		
		stringRefno = stringRefno + string
	}
	
    if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 43) == 'No') {
        'set tenant kosong'
        GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 44)
    } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 43) == 'Yes') {
        GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
    }
    
    'check if mau menggunakan api_key yang salah atau benar'
    if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 41) == 'Yes') {
        'get api key dari db'
        GlobalVariable.api_key = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
    } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 41) == 'No') {
        'get api key salah dari excel'
        GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 42)
    }
    
    respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : findTestData(
                    excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 9), ('request') : stringRefno, ('callerId') : findTestData(
                    excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 39)]))

    'jika response 200 / hit api berhasil'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'ambil respon text dalam bentuk code.'
        status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'jika status codenya 0, verifikasi datanya benar'
        if (status_Code == 0) {
            documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)
			trxno = WS.getElementPropertyValue(respon, 'documents.trxnos', FailureHandling.OPTIONAL)

            'masih ada [ ] nya dalam documentid'
            GlobalVariable.Response = documentId


            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
            4, GlobalVariable.NumofColm - 1, documentId.toString())
			
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document',
			5, GlobalVariable.NumofColm - 1, trxno.toString())
			
			//Response API terhadap DB tersebut. Masih stuck dan bingung sehingga dicomment terlebih dahulu
			//ResponseAPIStoreDB()

			
            'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
            int jumlahsignertandatangan = 0

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

            /*  if (GlobalVariable.checkStoreDB == 'Yes') {
                'calexcelPathAPISendDocsponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('excelPathAPISendDoc') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            */
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error' /*
            if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
                'call test case error report'
                WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('excelPathAPISendDoc') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            */
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
        }
    }else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()
			responsecode = WS.getResponseStatusCode(respon)
			if(messageFailed.toString().replace('[','').replace(']','') == null) {
				'write to excel status failed dan reason'
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
						'-', '') + ';') + responsecode)
			} else {
				'write to excel status failed dan reason'
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
						'-', '') + ';') + messageFailed.toString())
			}

	}
}

def PDFtoBase64(String fileName) 
{
    return CustomKeywords.'customizeKeyword.convertFile.BASE64File'(fileName)
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def ResponseAPIStoreDB() {
	'connect DB eSign'
	Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()
	
	'declare arraylist arraymatch'
	ArrayList<String> arrayMatch = new ArrayList<String>()
	
	docid = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 5).replace('[', '').replace(']', '').split(', ',
		-1)
	for (int i = 0; i < docid.size(); i++) {
		'get data API Send Document dari DB (hanya 1 signer)'
		ArrayList<String> result = CustomKeywords.'connection.dataVerif.getSendDoc'(conneSign, docid[i])
	
		'declare arrayindex'
		arrayindex = 0
		
		'verify email'
		arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).replace('"', ''),
				result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'verify signerType'
		arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"', ''),
				result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
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
		arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', ''),
				result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'verify success url'
		arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', ''),
				result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'verify upload url'
		arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', ''),
				result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify total document'
		arrayMatch.add(WebUI.verifyEqual(docid.size(),result[arrayindex++].toInteger(), FailureHandling.CONTINUE_ON_FAILURE))
	
		'verify sign Action : hardcode. Yang penting tidak boleh kosong'
		arrayMatch.add(WebUI.verifyNotMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).replace('"',
					''), '', false, FailureHandling.CONTINUE_ON_FAILURE))

	}
}
