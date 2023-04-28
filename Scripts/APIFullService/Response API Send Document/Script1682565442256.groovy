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

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 2 /*findTestData(API_Excel_Path).getColumnNumbers()*/ ; (GlobalVariable.NumofColm)++) {
    
	ArrayList<String> refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

    ArrayList<String> documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 12).split(';', 
        -1)

    ArrayList<String> officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 13).split(';', -1)

    ArrayList<String> officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 14).split(';', -1)

    ArrayList<String> regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 15).split(';', -1)

    ArrayList<String> regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 16).split(';', -1)

    ArrayList<String> businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 17).split(';', 
        -1)

    ArrayList<String> businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 18).split(';', 
        -1)

    ArrayList<String> documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

	ArrayList<String> pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 21).split(';', -1)
	
	ArrayList<String> llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 22).split(';', -1)
	
	ArrayList<String> llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 23).split(';', -1)
	
	ArrayList<String> urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).split(';', -1)
	
	ArrayList<String> uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).split(';', -1)
	
    'split signer untuk doc1 dan signer untuk doc2'
	
    ArrayList<String> signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 27).split('\\|', -1)

    ArrayList<String> signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).split('\\|', -1)

    ArrayList<String> tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 29).split('\\|', -1)

    ArrayList<String> idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)

    ArrayList<String> email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)
	
	ArrayList<String> pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 33).split('\\|', -1)
	
	ArrayList<String> llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 34).split('\\|', -1)
	
	ArrayList<String> llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 35).split('\\|', -1)
	
	ArrayList<String> urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 36).split('\\|', -1)
	
	ArrayList<String> urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).split('\\|', -1)

    String stringRefno = new String()

    for (int i = 0; i < refNo.size(); i++) {
		
        (signAction[i]) = (signAction[i]).split(';', -1)

        (signerType[i]) = (signerType[i]).split(';', -1)

        (tlp[i]) = (tlp[i]).split(';', -1)

        (idKtp[i]) = (idKtp[i]).split(';', -1)

        (email[i]) = (email[i]).split(';', -1)
		
		(pageSign[i]) = (pageSign[i]).split(';', -1)
		
		(llxSign[i]) = (llxSign[i]).split(';', -1)
		
		(llySign[i]) = (llySign[i]).split(';', -1)
		
		(urxSign[i]) = (urxSign[i]).split(';', -1)
		
		(urySign[i]) = (urySign[i]).split(';', -1)

        String string = new String()

        string = ''

        string = (string + (((((((((((((((((('{"referenceNo" : ' + (refNo[i])) + ', "documentTemplateCode": ') + (documentTemplateCode[
        i])) + ', "officeCode": ') + (officeCode[i])) + ', "officeName": ') + (officeName[i])) + ', "regionCode": ') + (regionName[
        i])) + ', "regionName": ') + (regionName[i])) + ', "businessLineCode": ') + (businessLineCode[i])) + ', "businessLineName": ') + 
        (businessLineName[i])) + ','))))

        stringRefno = (stringRefno + string)

        string = new String()

        string = ''
		
		signlocation = new String()
		
		signLocation = ''
		
		stampLocation = new String()
		
		stampLocation = ''

		if(documentTemplateCode[i] == '""') {
		}
		else {
			for(int e = 0 ; e < documentTemplateCode.size();e++) {
				if(e == documentTemplateCode.size() - 1){
					stampLocation = stampLocation + '{"page": '+ pageStamp[e] + ',"llx" : '+ llxStamp[e] + ',"lly" : '+ llyStamp[e]+',"urx" : '+urxStamp[e]+', "ury": '+uryStamp[e]+'}]'
				} else if (e == 0) {
					stampLocation = stampLocation + ',"stampLocation" : [{"page": '+ pageStamp[e] + ',"llx" : '+ llxStamp[e] + ',"lly" : '+ llyStamp[e]+',"urx" : '+urxStamp[e]+', "ury": '+uryStamp[e]+'},'
				} else {
					stampLocation = stampLocation + '{"page": '+ pageStamp[e] + ',"llx" : '+ llxStamp[e] + ',"lly" : '+ llyStamp[e]+',"urx" : '+urxStamp[e]+', "ury": '+uryStamp[e]+'},'
				}
			}
		}

		
		for (int r = 0; r < pageSign[i].size(); r++) 
		{
			if(r == 0) 
				{
					signLocation = signLocation + '"signLocation" : [{"page": '+ pageSign[i][r] + ',"llx" : '+ llxSign[i][r] + ',"lly" : '+ llySign[i][r]+',"urx" : '+urxSign[i][r]+', "ury": '+urySign[i][r]+'},'
				}
				else if  (r == documentTemplateCode.size() - 1)
					{
						signLocation = signLocation + '{"page": '+ pageSign[i][r] + ',"llx" : '+ llxSign[i][r] + ',"lly" : '+ llySign[i][r]+',"urx" : '+urxSign[i][r]+', "ury": '+urySign[i][r]+'}]'
					}
					else {
						signLocation = signLocation + '{"page": '+ pageSign[r] + ',"llx" : '+ llxSign[r] + ',"lly" : '+ llySign[r]+',"urx" : '+urxSign[r]+', "ury": '+urySign[r]+'},'
			
					}
		}	
        for (int t = 0; t < (signAction[i]).size(); t++) {
            if (t == ((signAction[i]).size() - 1)) {
                string = (string + (((((((((('{"signAction": ' + ((signAction[i])[t])) + ',"signerType": ') + 
                ((signerType[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"email": ') + 
                ((email[i])[t])) + ', '+ signLocation + '}],'))
            } else if (t == 0) {
                string = (string + (((((((((('"signers": [{"signAction": ' + ((signAction[i])[t])) + ',"signerType": ') + 
                ((signerType[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"email": ') + 
                ((email[i])[t])) + ', '+signLocation + '},'))
            } else {
                string = (string + (((((((((('{"signAction": ' + ((signAction[i])[t])) + ',"signerType": ') + 
                ((signerType[i])[t])) + ',"tlp": ') + ((tlp[i])[t])) + ',"idKtp": ') + ((idKtp[i])[t])) + ',"email": ') + 
                ((email[i])[t])) + ', '+signLocation + '},'))
            }
            
            stringRefno = (stringRefno + string)

            string = ''
        }
        
        string = new ArrayList<String>()

        string = ''

        if (i == (refNo.size() - 1)) {
            string = (string + ('"documentFile": "' + PDFtoBase64(documentFile[i])) + '"' + stampLocation +'}')
        } else {
            string = (string + ('"documentFile": "' + PDFtoBase64(documentFile[i])) + '"' + stampLocation +'},')
        }
        
        stringRefno = (stringRefno + string)
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
            documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL)

            'masih ada [ ] nya dalam documentid'
            GlobalVariable.Response = documentId

            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                4, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString())

            'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
            int jumlahsignertandatangan = 0

            //call test case mengenai sign doc FE (kemungkinan)
            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

          /*  if (GlobalVariable.checkStoreDB == 'Yes') {
                'calexcelPathAPISendDocsponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Send_Document/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            */
            'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
/*
            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', '') == resultTenant) {
                'call test case error report'
                WebUI.callTestCase(findTestCase('Send_Document/ErrorReport'), [('API_Excel_Path') : 'Registrasi/SendDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            */
        }
    }
}

def PDFtoBase64(String fileName) {
    String base64 = CustomKeywords.'customizeKeyword.convertFile.BASE64File'(fileName)

    return base64
}

