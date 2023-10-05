import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'call test case login per case'
WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathListUndangan, ('Email') : 'Email Login', ('Password') : 'Password Login'
	, ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)

if (findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
	GlobalVariable.FlagFailed = 0
}

'click menu list undangan'
WebUI.click(findTestObject('ListUndangan/menu_ListUndangan'))

'apakah cek paging diperlukan'
if(GlobalVariable.checkPaging.equals('Yes')) {
	'call function check paging'
	checkPaging()
}

'set text tanggal pengiriman dari'
WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), '2023-03-01')

'set text tanggal pengiriman ke'
WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), '2023-03-31')

'click button cari'
WebUI.click(findTestObject('ListUndangan/button_Cari'))

'check if mau download list undangan'
if (findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Download File')).equalsIgnoreCase('Yes')) {
    'click button download'
    WebUI.click(findTestObject('ListUndangan/button_UnduhExcel'))

    'delay 3 detik'
    WebUI.delay(3)

    'check isfiled downloaded'
    if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Keep Download file ?'))) == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedDownload)

        GlobalVariable.FlagFailed = 1
    }
}

if (GlobalVariable.FlagFailed == 0) {
    'write to excel success'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
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
                FailureHandling.CONTINUE_ON_FAILURE), '2023-10-01', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/input_TanggalPengirimanKe'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '2023-10-04', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('ListUndangan/input_StatusUndangan'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('ListUndangan/input_PengirimanMelalui'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('ListUndangan/input_StatusRegistrasi'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE)) 

    'click object lain untuk close ddl'
    WebUI.click(findTestObject('ListUndangan/input_TanggalPengirimanDari'))

	'check if ada paging'
	if(WebUI.verifyElementVisible(findTestObject('DocumentMonitoring/button_NextPage'), FailureHandling.OPTIONAL)) {
		'click next page'
		WebUI.click(findTestObject('ListUndangan/button_NextPage'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'aria-label',
		FailureHandling.CONTINUE_ON_FAILURE), 'page 2', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click prev page'
		WebUI.click(findTestObject('ListUndangan/button_PrevPage'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'aria-label',
		FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click last page'
		WebUI.click(findTestObject('ListUndangan/button_LastPage'))
	
		'get total data'
		lastPage = Double.parseDouble(WebUI.getText(findTestObject('ListUndangan/label_TotalData')).split(' ',-1)[0])/10
		
		'jika hasil perhitungan last page memiliki desimal'
		if (lastPage.toString().contains('.0')) {
			'tidak ada round up'
			additionalRoundUp = 0
		} else {
			'round up dengan tambahan 0.5'
			additionalRoundUp = 0.5
		}
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'aria-label',
		FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage+additionalRoundUp).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

		'click first page'
		WebUI.click(findTestObject('ListUndangan/button_FirstPage'))

		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ListUndangan/paging_Page'), 'aria-label',
		FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))
	}

}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathListUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}