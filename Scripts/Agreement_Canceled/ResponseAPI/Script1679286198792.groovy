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
import com.kms.katalon.util.CryptoUtil

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

WebUI.en

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++)
{
	
		ArrayList<String> listMsg = new ArrayList<String>()
		listMsg.add('{"tenantCode":"'+ findTestData(Excel_API_Path).getValue(GlobalVariable.NumofColm,10)+'","officeCode":"'+ findTestData(Excel_API_Path).getValue(GlobalVariable.NumofColm,11) +'","email":"'+findTestData(Excel_API_Path).getValue(GlobalVariable.NumofColm,12)+'"}')
		'HIT API'
		respon = WS.sendRequest(findTestObject('Postman/Agreement Canceled', [('documentId') : '"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,9) + '"',('msg') :  '"' + listMsg[0] + '"' ,('callerId') :'"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,12) + '"',('tenantCode') : '"' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,14) + '"']))
		if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true)
		{
			'mengambil status code berdasarkan response HIT API'
			status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
		
			'jika status codenya 0'
			if (status_Code == 0) 
				{
					'write to excel success'
					CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Agreement Canceled',
					0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
				}
				else
				{
					messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)
					'write to excel status failed dan reason : '
					CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Agreement Canceled', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-',
					'') + ';') + messageFailed)
				}
		}
		else
		{
			'write to excel status failed dan reason : '
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-',
			'') + ';') + GlobalVariable.ReasonFailedHitAPI)
		}
	}
	
	
		
		
		
		
		
		
		
		
		
		