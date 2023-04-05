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
import org.openqa.selenium.Keys
import org.apache.commons.collections.ArrayStack
import org.openqa.selenium.By
import com.kms.katalon.core.webui.driver.DriverFactory
import java.sql.Connection as Connection
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testobject.TestObject
import org.openqa.selenium.WebElement
'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPengaturanDokumen).getColumnNumbers(); GlobalVariable.NumofColm++) 
{
	'memanggil test case login untuk admin wom dengan Admin Legal'
	WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)
	
	'Klik tombol pengaturan dokumen'
	WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))
	
	'Klik tombol tambah pengaturan dokumen'
	WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))
	
	'Pengecekan apakah masuk page tambah pengaturan dokumen'
	if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TambahTemplatDokumen'), GlobalVariable.TimeOut)) 
	{
		'Input text documentTemplateCode'
		WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 9))
		
		'Input text documentTemplateName'
		WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateName'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 10))
		
		'Input text documentTemplateDescription'
		WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 11))
		
		'get data tipe-tipe pembayaran secara asc'
		ArrayList<String> tipepembayaran_db = CustomKeywords.'connection.dataVerif.getLovTipePembayaran'(conneSign)

		'Click dropdown mengenai tipe pembayaran'
		WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))
		
		'Mengambil value yang ada di UI dan displit dengan enter'
		tipepembayaran_ui = WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/ddl_tipepembayaran')).split('\\n')

		'Jika db dan UI cocok maka'
		if(tipepembayaran_ui == tipepembayaran_db) {
		}else {
			'Flag false'
			checkVerifyEqualorMatch(false)
		}
		
		'Input value tipe pembayaran'
		WebUI.setText(findTestObject('TandaTanganDokumen/input_tipePembayaran'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 12))
		
		'Input enter'
		WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))
		
		'Input value status'
		WebUI.setText(findTestObject('TandaTanganDokumen/input_StatusAktif'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 14))
		
		'Input enter'
		WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_StatusAktif'), Keys.chord(Keys.ENTER))
		
		'Jika panjang dokumen lebih besar dari 0'
		if(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm,13).length() > 0) {
		  'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
		   String userDir = System.getProperty('user.dir')
		   String filePath = userDir + findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm,13)
		
		   'Upload file berdasarkan filePath yang telah dirancang'
		   WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), filePath)
		   
	   }
	   else
	   {
		   'Upload file yang kosong'
		   WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), findTestObject('TandaTanganDokumen/input_documentExample').getValue(GlobalVariable.NumofColm,13))
	   }
	   'Klik button lanjut'
	   WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))
	   
	   'Jika label Konfirmasi muncul'
	   if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_konfirmasi'), GlobalVariable.TimeOut)) {
		   'Klik Konfirmasi no'
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))
		   
		   'Data-data yang telah diinput diverifikasi dengan excel'
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 9),WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'),'value'),false, FailureHandling.CONTINUE_ON_FAILURE))

		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 10), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'),'value'), false, FailureHandling.CONTINUE_ON_FAILURE))
		
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 11), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'),'value'), false, FailureHandling.CONTINUE_ON_FAILURE))
		   
		   //Tipe pembayaran belum karena tidak bisa ambil value dari boxnya walaupun bisa input
		   'Click dropdown mengenai tipe pembayaran'
		   WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))
		   
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 12), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')),false, FailureHandling.CONTINUE_ON_FAILURE))
		   
		   //Checking document
		  // checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 13), WebUI.getText(findTestObject('TandaTanganDokumen/input_documentExample')),false,FailureHandling.CONTINUE_ON_FAILURE))
		   
		   //Checking status
		   checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 14), WebUI.getText(findTestObject('TandaTanganDokumen/input_StatusAktif')) ,false, FailureHandling.CONTINUE_ON_FAILURE))
		   
		   'Klik lanjut'
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))
		   
		   'Klik Konfirmasi Yes'
		   WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))
			
		   'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk for kedepannya'
		   int countDel = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm,18)
		   int countLock = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm,19)
		   int indexLock = countDel
		   TipeTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 20).split(';',-1)
		   
		   for(int j = 1; j <= findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm,15) ; j++) {
			 
			  WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
				
			  WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_tipetandatangan'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
				
			  WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_tipetandatangan'), TipeTandaTangan[j-1] ,false)
				
			  WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))
			   
			  WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDtipetandatangan'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
			  
			  WebUI.verifyMatch(TipeTandaTangan[j-1], WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDtipetandatangan')), false)
			   
			  WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/lbl_ttddisini'),GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
			  
			  'Persiapan buat modify objectnya dengan case-case selanjutnya'
			  TestObject kotakttd = findTestObject('Object Repository/TandaTanganDokumen/div_kotakTTD')
  
			  WebUI.verifyElementPresent(kotakttd, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)

			  WebElement element = WebUI.findWebElement(kotakttd)

			  // Define the new translate3d CSS value
			  String newLocation = 'translate3d(400px, 500px, 0px)'
			  
			  // Modify the translate3d CSS value using JavaScript
			  String js = "arguments[0].style.transform = arguments[1]"
			  DriverFactory.getWebDriver().executeScript(js, element, newLocation)
			
			   if (countDel > 0) {
				   WebUI.click(findTestObject('TandaTangaDokumen/btn_DeleteTTD'))
				   
				   WebUI.verifyElementNotPresent(kotakttd,GlobalVariable.TimeOut,FailureHandling.CONTINUE_ON_FAILURE)
				   
				   countDel--
			   }else if (countDel == 0) {
				   if(countLock > 0) {
					   modifyobject = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/div_kotakTTD'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox['+ (j - indexLock) +']/div/div', false)
					   //modify object (jumlah ttd[x-indexlock])
					   
					   WebUI.click(findTestObject('TandaTangaDokumen/btn_LockTTD'))
					   
					   countLock--
				   }
				   else {
					   
				   }
			   }
		   }
	   }
	}
}






def checkVerifyEqualorMatch(Boolean isMatch) {
	 if (isMatch == false) {
	'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
	GlobalVariable.FlagFailed = 1
	//CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumOfColm, GlobalVariable.StatusFailed,
	//(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumOfColm, 2) + ';') + GlobalVariable.FailedReasonStoreDB)
	}
}