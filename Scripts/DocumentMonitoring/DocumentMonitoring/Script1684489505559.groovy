import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import java.time.LocalDate
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

currentDate = LocalDate.now()
firstDateOfMonth = currentDate.withDayOfMonth(1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'call testcase login admin'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

GlobalVariable.FlagFailed = 0

'click menu DocumentMonitoring'
WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

'call function check paging'
checkPaging(currentDate, firstDateOfMonth)

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth) {
    'set text nama Pelanggan'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'nama pelanggan')

    'set text tanggal permintaan dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), '2023-01-01')

    'set text tanggal permintaan sampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), '2023-01-31')

    'set text TanggalSelesaoSampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), '2023-01-01')

	'set text TanggalSelesaoSampai'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), '2023-01-31')
	
	'set text tipe dok'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), 'dokumen')

	'enter untuk set tipe dok'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

	'set text status'
	WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), 'complete')

	'enter untuk set status'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

	'set text tanggal wilayah'
	WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), 'bogor')

	'enter untuk set wilayah'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

	'set text tanggal cabang'
	WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), 'irwan')

	'enter untuk set cabang'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))
	
    'click button set ulang'
    WebUI.click(findTestObject('DocumentMonitoring/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
			'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click ddl status meterai'
	WebUI.click(findTestObject('DocumentMonitoring/input_TipeDok'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('DocumentMonitoring/input_Status'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('DocumentMonitoring/input_Wilayah'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('DocumentMonitoring/input_Cabang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click field lain untuk close ddl'
	WebUI.click(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'))

    'click button cari'
    WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))

    'click next page'
    WebUI.click(findTestObject('DocumentMonitoring/button_NextPage'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
            '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('DocumentMonitoring/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
            '1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click last page'
    WebUI.click(findTestObject('DocumentMonitoring/button_LastPage'))

    'verify paging di last page'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
            WebUI.getAttribute(findTestObject('DocumentMonitoring/page_Active'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace(
                'page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

    'click first page'
    WebUI.click(findTestObject('DocumentMonitoring/button_FirstPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

