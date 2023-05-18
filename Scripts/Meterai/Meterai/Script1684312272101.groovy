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

'click menu meterai'
WebUI.click(findTestObject('Meterai/menu_Meterai'))

checkPaging()

def checkPaging() {
	'set text no kontrak'
	WebUI.setText(findTestObject('Meterai/input_NoKontrak'), '000111')

	'set text status meterai'
	WebUI.setText(findTestObject('Meterai/input_StatusMeterai'), 'stamp duty Used')

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Meterai/input_StatusMeterai'), Keys.chord(Keys.ENTER))
	
	'set text lini bisnis'
	WebUI.setText(findTestObject('Meterai/input_LiniBisnis'), 'multiguna')

	'enter untuk set lini bisnis'
	WebUI.sendKeys(findTestObject('Meterai/input_LiniBisnis'), Keys.chord(Keys.ENTER))
	
	'set text tanggal wilayah'
	WebUI.setText(findTestObject('Meterai/input_Wilayah'), 'bogor')

	'enter untuk set wilayah'
	WebUI.sendKeys(findTestObject('Meterai/input_Wilayah'), Keys.chord(Keys.ENTER))
	
	'set text tanggal cabang'
	WebUI.setText(findTestObject('Meterai/input_Cabang'), 'irwan')

	'enter untuk set cabang'
	WebUI.sendKeys(findTestObject('Meterai/input_Cabang'), Keys.chord(Keys.ENTER))
	
	'set text tanggal pakai dari'
	WebUI.setText(findTestObject('Meterai/input_TanggalPakaiDari'), '2023-01-01')

	'set text tanggal pakai sampai'
	WebUI.setText(findTestObject('Meterai/input_TanggalPakaiSampai'), '2023-01-31')

	'set text no meterai'
	WebUI.setText(findTestObject('Meterai/input_NoMeterai'), '21975017285')
	
	'click button set ulang'
	WebUI.click(findTestObject('Meterai/button_SetUlang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_NoKontrak'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('Meterai/input_StatusMeterai'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('Meterai/input_LiniBisnis'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click ddl status meterai'
	WebUI.click(findTestObject('Meterai/input_Wilayah'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('Meterai/input_Cabang'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click field lain untuk close ddl'
	WebUI.click(findTestObject('Meterai/input_TanggalPakaiDari'))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_TanggalPakaiDari'), 'value',
				FailureHandling.CONTINUE_ON_FAILURE), '2023-05-01', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_TanggalPakaiSampai'), 'value',
				FailureHandling.CONTINUE_ON_FAILURE), '2023-05-17', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_NoMeterai'), 'value',
				FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'set text tanggal pengiriman dari'
	WebUI.setText(findTestObject('Meterai/input_TanggalPakaiDari'), '2023-01-01')

	'set text tanggal pengiriman ke'
	WebUI.setText(findTestObject('Meterai/input_TanggalPakaiSampai'), '2023-01-31')

	'click button cari'
	WebUI.click(findTestObject('Meterai/button_Cari'))

	'click next page'
	WebUI.click(findTestObject('Meterai/button_NextPage'))

	'verify paging di page 2'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page',
				FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click prev page'
	WebUI.click(findTestObject('Meterai/button_PrevPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page',
				FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click last page'
	WebUI.click(findTestObject('Meterai/button_LastPage'))

	'verify paging di last page'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page',
				FailureHandling.CONTINUE_ON_FAILURE), WebUI.getAttribute(findTestObject('Meterai/page_Active'), 'aria-label',
				FailureHandling.CONTINUE_ON_FAILURE).replace('page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

	'click first page'
	WebUI.click(findTestObject('Meterai/button_FirstPage'))

	'verify paging di page 1'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page',
				FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedPaging)

		GlobalVariable.FlagFailed = 1
	}
}

