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

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPengaturanDokumen).getColumnNumbers(); (GlobalVariable.NumofColm)++) {

	WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

	WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

	WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

	if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/text_TambahTemplatDokumen'), GlobalVariable.TimeOut)) {
   
		WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 9))

	    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateName'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 10))

		WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 11))

		WebUI.setText(findTestObject('TandaTanganDokumen/input_tipePembayaran'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 12))

		WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))

	   WebUI.setText(findTestObject('TandaTanganDokumen/input_StatusAktif'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 13))

	   WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_StatusAktif'), Keys.chord(Keys.ENTER))

	   String userDir = System.getProperty('user.dir')
	   String filePath = userDir + '/Documents/PengaturanDokumen/AdIns - Basic Accounting and Basic Journal in CONFINS.pdf'
    
	   WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), filePath)

	   WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

	   if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/text_konfirmasi'), GlobalVariable.TimeOut)) {
        
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))
		   
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 9), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode','value')),false, FailureHandling.CONTINUE_ON_FAILURE))
			
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 10), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName','value')), false, FailureHandling.CONTINUE_ON_FAILURE))
		
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 11), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription','value')), false, FailureHandling.CONTINUE_ON_FAILURE))

		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 12), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_tipePembayaran','value')), false, FailureHandling.CONTINUE_ON_FAILURE))
			
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 13), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_StatusAktif','value')), false , FailureHandling.CONTINUE_ON_FAILURE))
		   
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))
		
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))
		
	   }
	}
}






def checkVerifyEqualorMatch(Boolean isMatch) {
	 if (isMatch == false) {
	'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
	GlobalVariable.FlagFailed = 1
	CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumOfColm, GlobalVariable.StatusFailed,
	(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumOfColm, 2) + ';') + GlobalVariable.FailedReasonStoreDB)
	}
}