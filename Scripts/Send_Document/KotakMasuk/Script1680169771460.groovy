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

WebUI.callTestCase(findTestCase('Login/Login_Andy'), [:], FailureHandling.STOP_ON_FAILURE)

'get data API Send Document dari DB (hanya 1 signer)'
ArrayList<String> result = CustomKeywords.'connection.dataVerif.getKotakMasukSendDoc'(conneSign,GlobalVariable.Response.replace('[', '').replace(']', ''))

WebUI.maximizeWindow()

WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Lastest'))

'get row beranda'
variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

//UInya berubah 31 Maret 2023 7.31
//'modify object text refnum'
//modifyObjecttextrefnum = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'),'xpath','equals',
//"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p",true)
	
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
String tes = WebUI.getText(modifyObjecttextname)
if (result[arrayindex++].contains(tes) == true)
{
	arrayMatch.add(true)
}else
{
arrayMatch.add(false)
}

'splitting signer type untuk checking popup'
result[4] = result[4].split(';',-1)

'splitting email untuk checking popup'
result[3] = result[3].split(';',-1)

'splitting name untuk checking popup'
result[2] = result[2].split(';',-1)

'splitting aktivasi akun untuk checking popup'
result[5] = result[5].split(';',-1)

ArrayList<String> resultaktivasi =  new ArrayList<String>()
for(int j = 1; j<= result[5].size();j++) {
	if(result[5][j-1] == '1') 
	{
		resultaktivasi.add("Sudah Aktivasi")
	} else {
		resultaktivasi.add("Belum Aktivasi")
	}
}


'Klik btnSigner'
WebUI.click(modifyObjectbtnSigner)

'get row popup'
variable_popup = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

for(int i = 0 ; i <= variable_popup.size() ; i++) 
{
	'modify object text signer Type'
	modifyObjecttextsignerTypepopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'),'xpath','equals',
	"/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (i + 1) +"]/datatable-body-row/div[2]/datatable-body-cell[1]/div/p",true)
	
	'modify object text nama'
	modifyObjecttextnamapopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namasignerpopup'),'xpath','equals',
	"/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (i + 1) +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div/p",true)

	'modify object text email'
	modifyObjecttextemailpopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_emailsignerpopup'),'xpath','equals',
	"/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (i + 1) +"]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p",true)

	'modify object text sudahaktivasi'
	modifyObjecttextsudahaktivasipopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_sudahaktivasipopup'),'xpath','equals',
	"/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ (i + 1) +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div/p",true)
	
	'signer Type popup'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextsignerTypepopup), result[4][i], false, FailureHandling.CONTINUE_ON_FAILURE))

	'signer email popup'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextemailpopup), result[3][i], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'signer nama popup'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextnamapopup), result[2][i], false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'signer aktivasi akun'
	arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextsudahaktivasipopup), resultaktivasi[i], false, FailureHandling.CONTINUE_ON_FAILURE))
	
}	


'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'(GlobalVariable.Response, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI)
}
	
	
