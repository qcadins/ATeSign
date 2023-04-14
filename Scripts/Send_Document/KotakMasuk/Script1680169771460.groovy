import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys


'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'call Test Case untuk login sebagai user'
WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [:], FailureHandling.STOP_ON_FAILURE)

'get data kotak masuk send document secara asc, dimana customer no 1'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getKotakMasukSendDoc'(conneSign,GlobalVariable.Response.replace('[', '').replace(']', ''))

'Max windows'
WebUI.maximizeWindow()

'Klik objek Beranda'
WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

'Klik lastest dari kotak masuk yang ada'
WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Lastest'), FailureHandling.OPTIONAL)

'get row beranda'
variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

'modify object text refnum'
modifyObjecttextrefnum = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p",true)
	
'modify object text document type'
modifyObjecttextdocumenttype = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipedokumen'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p",true)

'modify object text document template name'
modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p",true)
	
'modify object text name cust'
modifyObjecttextname = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div/p",true)
	
'modify object button signer'
modifyObjectbtnSigner = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'),'xpath','equals',
"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[4]/em",true)
 
'doctype'
arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttype), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	
'document template name'
arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatename), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'nama Customer'
arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextname), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'get data kotak masuk send document secara asc, dimana customer no 1'
ArrayList<String> resultSigner = CustomKeywords.'connection.dataVerif.getSignerKotakMasukSendDoc'(conneSign,GlobalVariable.Response.replace('[', '').replace(']', ''))

'Klik btnSigner'
WebUI.click(modifyObjectbtnSigner)

'get row popup'
variable_row_popup = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

'get column popup'
variable_col_popup = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

'declare arrayindex buat signer'
arrayindex_signer = 0

'loop untuk row popup'
for(int i = 1 ; i <= variable_row_popup.size() ; i++) 
{
	'loop untuk column popup'
	for(int m = 1 ; m <= variable_col_popup.size()/variable_row_popup.size();m++) 
	{
		'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
		modifyObjecttextpopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'),'xpath','equals',"/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ i +"]/datatable-body-row/div[2]/datatable-body-cell["+m+"]/div/p",true)
		
		'signer nama,email,signerType,sudahAktivasi popup'
		arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextpopup), resultSigner[arrayindex_signer++], false, FailureHandling.CONTINUE_ON_FAILURE))
			
	}
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'(GlobalVariable.Response, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI)
}
	
	
