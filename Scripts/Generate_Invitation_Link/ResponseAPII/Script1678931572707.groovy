import internal.GlobalVariable as GlobalVariable
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
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
import groovy.json.JsonOutput as JsonOutput

'Open test case untuk login sebagai Invenditor'
WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++) 
{
	'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
    List<String> ListInvitation = new ArrayList<String>()

	'Declare variable untuk sendRequest'
    (ListInvitation[(GlobalVariable.NumofColm - 2)]) = ('     {"email" : "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,13) + '","nama" : "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,14)+ '","tlp": "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,15)+ '","jenisKelamin" : "' + WebUI.selectOptionByIndex(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,16), 0)+ '","tmpLahir" : "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,17)+ '","tglLahir" : "' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,18) + '","idKtp" : "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,19)+ '", "provinsi" : "' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,20)+ '", "kota" : "' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,21)+ '", "kecamatan" : "' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,22)+ '","kelurahan": "' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,23)+ '","kodePos" : "' +findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,24) + '","alamat" : "' + findTestData(I_Excel_Path).getValue(GlobalVariable.NumofColm,25)+ '"}  ')
	
	'HIT API'
    respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : '"tes"', ('tenantCode') : '"WOMF"'
                , ('users') : ListInvitation[(GlobalVariable.NumofColm - 2)]]))

	'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) 
	{
		'mengambil status code berdasarkan response HIT API'
		status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

		'jika status codenya 0'
		if (status_Code == 0) 
		{
			'Mengambil links berdasarkan response HIT API'
			links = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL)
			
			'Maximize windows untuk cek Inquiry'
			WebUI.maximizeWindow()
			
			'Klik menu Inquiry Invitation'
			WebUI.click(findTestObject('Object Repository/InquiryInvitation/menu_InquiryInvitation'))
			
			'Set search box di Inquiry Invitation'
			WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(API_Excel_Path).getValue(
				GlobalVariable.NumofColm, 14))
			
			'Klik button Cari'
			WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_Cari'))

			'Klik button View Link'
			WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_ViewLink'))

			'Klik pop up link'
			WebUI.click(findTestObject('Object Repository/InquiryInvitation/input_Link'))
			
			'Mengambil value dari pop up'
			invitation_link_inquiry = WebUI.getAttribute(findTestObject('Object Repository/InquiryInvitation/input_Link'),'value')
		
			'Verify value pop up dan response HIT API'
			if (WebUI.verifyMatch(links, '[' + invitation_link_inquiry + ']', false)) 
			{
				'write to excel success'
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan',
				0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			
			} 
			else 
			{
				'write to excel status failed dan reason : '
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-',
				'') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
			}
        
       'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
		}
		else 
		{
			messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()
			'write to excel status failed dan reason : '
			CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-',
			'') + ';') + messageFailed)
		}	
	}
}


