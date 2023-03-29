import com.kms.katalon.core.model.FailureHandling

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
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
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.driver.DriverFactory


GlobalVariable.Response = 'API Bulk Sign Document'

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++) 
{
	'Split mengenai documentids'
	ArrayList<String> documentIds = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(';', -1)
	
	'Pembuatan pengisian variable di sendRequest per jumlah document.'
	ArrayList<String> list = new ArrayList<String>()

	ArrayList<String> Listdocument = new ArrayList<String>()

	(Listdocument[0]) = ''

	for (int i = 1; i <= documentIds.size(); i++) 
		{
		if (i == documentIds.size()) 
		{
			list.add('"' + documentIds[i-1] + '"')
		}
		list.add('"' + documentIds[i-1] + '",')
	
		(Listdocument[0]) = ((Listdocument[0]) + (list[(i - 1)]))
		}
		
	'HIT API Login untuk token : andy@ad-ins.com'
	respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2, 3), ('password') : findTestData('Login/Login').getValue(3, 3)]))
	
	'Jika status HIT API Login 200 OK'
	if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true)
	{
		'Parsing token menjadi GlobalVariable'
		GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')
		
		'HIT API Bulk Sign Document'
		respon_bulksign = WS.sendRequest(findTestObject('Postman/Bulk Sign', [('callerId') : '"'+ findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9) +'"',('loginId') : '"'+ findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11) +'"', ('documentIds') : Listdocument[0]]))
			
		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(respon_bulksign, 200, FailureHandling.OPTIONAL) == true)
		{
			'get Status Code'
			status_Code = WS.getElementPropertyValue(respon_bulksign, 'status.code')
			
			'Jika status codenya 0'
			if(status_Code == 0)
			{
				'Get signLink'
				signLink = WS.getElementPropertyValue(respon_bulksign, 'signLink')
				
				'Open signLink buat check apakah dokumen yang ada disana dengan documentId excel sama'
				WebUI.openBrowser(signLink)
				
				'looping berdasarkan berapa banyak dokumen yang ada.'
				for(int j = 0; j <= documentIds.size();j++) 
				{
					if(j == 0) {
						'fungsi select untuk yang mengarah ke element document pertama'
						select = new Select(DriverFactory.getWebDriver().findElement(By.xpath("//div[@id='pdf-main-container']/div/ul/li")))
					
					}
					
					'fungsi select untuk yang mengarah ke element document pertama'
					select = new Select(DriverFactory.getWebDriver().findElement(By.xpath("//[@id='pdf-main-container']/div/ul/li["+(j+1)+"]")))
					
					'ambil text yang diselect oleh dropdown list tersebut'
					optionLabel = WebUI.getAttribute(select, 'label')
					
					
					'fungsi select untuk yang mengarah ke element document pertama'
					select = new Select(DriverFactory.getWebDriver().findElement(By.xpath("//[@id='pdf-main-container']/div/ul/li["+ (j+1) +"]")))
				
					'ambil text yang diselect oleh dropdown list tersebut'
					optionLabel = WebUI.getAttribute(select, 'label')
					
					if(optionLabel.contains(documentIds[i]) == true) 
					{
						//FE Belum done
						WebUI.closeBrowser()
						
						'Get vendorCode'
						GlobalVariable.Response = WS.getElementPropertyValue(respon_bulksign, 'vendorCode')
						
						'Get documents'
						documents = WS.getElementPropertyValue(respon_bulksign, 'documents')
		
						if(documents == null)
						{
							'Memanggil testCase mengenai Bulk_Sign_DocumentStoreDb'
							WebUI.callTestCase(findTestCase('Sign_Document/SignDocumentStoreDb'), [('API_Excel_Path') : 'Registrasi/BulkSignDocument'],FailureHandling.CONTINUE_ON_FAILURE)
							
							'write to excel success'
							CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Bulk Sign Document',
							0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
						}
						else
						{
							'Get documentId yang bermasalah'
							documentId = WS.getElementPropertyValue(respon_bulksign, 'documents.documentId')
							
							'Get notif / message'
							notif = WS.getElementPropertyValue(respon_bulksign, 'documents.notif')
							
							'write to excel status failed dan reason'
							CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
							'-', '') + ';') + notif + ' : ' + documentId)
						}
					}
					else
					{
						'write to excel status failed dan reason'
						CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
						'-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
					}
				}
				
			}
			else
			{
				messageFailed = WS.getElementPropertyValue(respon_bulksign, 'status.message', FailureHandling.OPTIONAL).toString()
				'write to excel status failed dan reason'
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
				'-', '') + ';') + messageFailed)
			}
		}
		else
		{
			'write to excel status failed dan reason'
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
			'-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
			
		}
	}
}