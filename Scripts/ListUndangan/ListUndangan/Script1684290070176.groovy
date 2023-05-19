import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'call test case login admin'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

GlobalVariable.FlagFailed = 0

'click menu list undangan'
WebUI.click(findTestObject('ListUndangan/menu_ListUndangan'))

'call function check paging'
checkPaging()

'check if mau download list undangan'
if (findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('Yes')) {
    'click button download'
    WebUI.click(findTestObject('ListUndangan/button_UnduhExcel'))

    'delay 3 detik'
    WebUI.delay(3)

    'check isfiled downloaded'
    if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, 
            10)) == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('ListUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedDownload)

		GlobalVariable.FlagFailed = 1
    }
}

if (GlobalVariable.FlagFailed == 0) {
	'write to excel success'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Meterai', 0, GlobalVariable.NumofColm -
		1, GlobalVariable.StatusSuccess)
}

def checkPaging() {
    'set text nama'
    WebUI.setText(findTestObject('ListUndangan/input_Nama'), 'nama')

    'set text penerima undangan'
    WebUI.setText(findTestObject('ListUndangan/input_PenerimaUndangan'), 'penerima undangan')

    'set text tanggal pengiriman dari'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), '2023-03-01')

    'set text tanggal pengiriman ke'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), '2023-03-31')

    'set text Status Undangan'
    WebUI.setText(findTestObject('ListUndangan/input_StatusUndangan'), 'AKTIF')

    'enter untuk set status undangan'
    WebUI.sendKeys(findTestObject('ListUndangan/input_StatusUndangan'), Keys.chord(Keys.ENTER))

    'set text Pengiriman Melalui'
    WebUI.setText(findTestObject('ListUndangan/input_PengirimanMelalui'), 'SMS')

    'enter untuk set PengirimanMelalui'
    WebUI.sendKeys(findTestObject('ListUndangan/input_PengirimanMelalui'), Keys.chord(Keys.ENTER))

    'set text status registrasi'
    WebUI.setText(findTestObject('ListUndangan/input_StatusRegistrasi'), 'DONE')

    'enter untuk set status registrasi'
    WebUI.sendKeys(findTestObject('ListUndangan/input_StatusRegistrasi'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('ListUndangan/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/input_Nama'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/input_PenerimaUndangan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/input_TanggalPengirimanDari'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '2023-05-01', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/input_TanggalPengirimanKe'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '2023-05-17', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('ListUndangan/input_StatusUndangan'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('ListUndangan/input_PengirimanMelalui'))
	
    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/selected_DDL'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click ddl status meterai'
	WebUI.click(findTestObject('ListUndangan/input_StatusRegistrasi'))
	
    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/selected_DDL'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click object lain untuk close ddl'
	WebUI.click(findTestObject('ListUndangan/input_TanggalPengirimanDari'))
	
    'set text tanggal pengiriman dari'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), '2023-03-01')

    'set text tanggal pengiriman ke'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), '2023-03-31')

    'click button cari'
    WebUI.click(findTestObject('ListUndangan/button_Cari'))

    'click next page'
    WebUI.click(findTestObject('ListUndangan/button_NextPage'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('ListUndangan/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click last page'
    WebUI.click(findTestObject('ListUndangan/button_LastPage'))

    'verify paging di last page'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), WebUI.getAttribute(findTestObject('ListUndangan/page_Active'), 'aria-label', 
                FailureHandling.CONTINUE_ON_FAILURE).replace('page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

    'click first page'
    WebUI.click(findTestObject('ListUndangan/button_FirstPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('ListUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

