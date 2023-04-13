import com.kms.katalon.core.model.FailureHandling as FailureHandling
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.main.CustomKeywordDelegatingMetaClass as CustomKeywordDelegatingMetaClass
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
import java.sql.Statement as Statement
import java.sql.DriverManager as DriverManager
import java.sql.ResultSet as ResultSet
import java.sql.ResultSetMetaData as ResultSetMetaData
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++) {
    'Pembuatan pengisian variable di sendRequest per jumlah documentid.'
	'Case yang dilakukan dimana email tidak berdasarkan excel, melainkan database'
	'Sehingga 1 document id untuk seluruh signer'
    ArrayList<String> list = new ArrayList<String>()
    'membersihkan list'
	list.clear()
	'array list untuk document id'
    ArrayList<String> Listdocid = new ArrayList<String>()
	'list dengan array 0 harus kosong'
    (Listdocid[0]) = ''
	'array list untuk email'
    ArrayList<String> Listemail = new ArrayList<String>()
	'list dengan array 0 harus kosong'
    (Listemail[0]) = ''
	'Mengambil document id dari excel dan displit'
    ArrayList<String> documentid = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(';', -1)

	'Looping berdasarkan jumlah dokumen id di excel'
    for (int q = 1; q <= documentid.size(); q++) {
		'Memasukkan ke dalam list mengenai isi send Request dengan document Id excel'
        (list[(q - 1)]) = (('"documentId": "' + (documentid[(q - 1)])) + '",')

		'Memasukkan list kedalam Listdocid agar menyatu'
        (Listdocid[0]) = ((Listdocid[0]) + (list[(q - 1)]))
		
		'Mengkosongkan List agar dapat digunakan di loop email'
        (list[(q - 1)]) = ''

		'mengambil isi email signer berdasarkan database.'
        ArrayList<String> emailsigner = CustomKeywords.'connection.dataVerif.getEmailsSign'(conneSign, documentid[(q - 1)])

		'loop berdasarkan jumlah email signer di database'
        for (int i = 1; i <= emailsigner.size(); i++) {
			'Memasukkan ke dalam list mengenai isi send Request dengan email'
            (list[(i - 1)]) = (('"email": "' + (emailsigner[(i - 1)])) + '",')
			'Memasukkan list kedalam Listemail agar menyatu'
            (Listemail[0]) = ((Listemail[0]) + (list[(i - 1)]))
			'Mengkosongkan List agar dapat digunakan'
            (list[(i - 1)]) = ''
        }
    }
    
    'HIT API Login untuk token : andy@ad-ins.com'
    respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2, 
                    3), ('password') : findTestData('Login/Login').getValue(3, 3)]))

    'Jika status HIT API Login 200 OK'
    if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

        'HIT API Sign Document'
        respon_signdoc = WS.sendRequest(findTestObject('Postman/Sign Doc', [('callerId') : ('"' + findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, 9)) + '"', ('email') : Listemail[0], ('documentid') : Listdocid[0], ('msg') : ('"' + 
                    findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13)) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_signdoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon_signdoc, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'get vendor Code'
                GlobalVariable.Response = WS.getElementPropertyValue(respon_signdoc, 'vendorCode')

                'write to excel success'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'Memanggil testCase mengenai SignDocumentStoreDb'
                WebUI.callTestCase(findTestCase('Sign_Document/SignDocumentStoreDb'), [('API_Excel_Path') : 'Registrasi/SignDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                'Jika tidak ada, maka '
            } else {
                'write to excel status failed dan reason : message Failed dari response'
                messageFailed = WS.getElementPropertyValue(respon_signdoc, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
            'write to excel status failed dan reason : message Failed dari response'
            messageFailed = WS.getElementPropertyValue(respon_signdoc, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
        }
    } else {
        'write to excel status failed dan reason : failed hit api'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedHitAPI)

        'call test case login inveditor'
        WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

        'call test case error report'
        WebUI.callTestCase(findTestCase('Sign_Document/ErrorReport'), [('excelPathSignDoc') : 'Registrasi/BulkSignDocument'], 
            FailureHandling.STOP_ON_FAILURE)

        'close browser'
        WebUI.closeBrowser()
    }
}

