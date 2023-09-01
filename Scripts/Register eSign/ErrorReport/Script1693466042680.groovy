import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import org.openqa.selenium.By
import org.openqa.selenium.Keys as Keys

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'click menu ErrorReport'
WebUI.click(findTestObject('ErrorReport/menu_ErrorReport'))

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'call function check paging'
checkPaging()

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'get total data error dari DB'
String resultTotalData = CustomKeywords.'connection.ErrorReport.getTotalDataError'(conneSign, WebUI.getAttribute(findTestObject(
            'ErrorReport/input_TanggalDari'), 'value'))

'get error detail dari DB'
String resultErrorDetail = CustomKeywords.'connection.ErrorReport.getErrorReportDetail'(conneSign, findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, rowExcel('$Nama')).toUpperCase())

'get total data'
totalData = WebUI.getText(findTestObject('ErrorReport/label_TotalData')).split(' ')

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch((totalData[0]).replace(',', ''), resultTotalData, false, FailureHandling.CONTINUE_ON_FAILURE), ' Total Data')

'select modul'
WebUI.setText(findTestObject('ErrorReport/select_Modul'), 'generate invitation link error history')

'send keys enter'
WebUI.sendKeys(findTestObject('ErrorReport/select_Modul'), Keys.chord(Keys.ENTER))

'input nama'
WebUI.setText(findTestObject('ErrorReport/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        rowExcel('$Nama')))

'input tanggal dari'
WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), currentDate)

'input tanggal ke'
WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), currentDate)

'input error type'
WebUI.setText(findTestObject('ErrorReport/select_Tipe'), GlobalVariable.ErrorType)

'enter untuk input tipe error'
WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'verify match tipe error'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_Tipe')), GlobalVariable.ErrorType, 
        false, FailureHandling.OPTIONAL), ' Tipe Error')

'click button view'
WebUI.click(findTestObject('ErrorReport/button_View'))

'verify match error detail'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_ErrorDetail')), resultErrorDetail, 
        false, FailureHandling.OPTIONAL), ' Error Detail')

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

'click button status aktivasi'
WebUI.click(findTestObject('ErrorReport/button_StatusAktivasi'))

'get total status aktivasi'
totalStatusAktivasi = WebUI.getText(findTestObject('ErrorReport/label_TotalStatusAktivasi')).split('')

'verify match total status aktivasi'
checkVerifyEqualOrMatch(WebUI.verifyMatch(totalStatusAktivasi[0], '0', false, FailureHandling.OPTIONAL), ' Status Aktivasi')

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

public checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyPaging(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
			';') + GlobalVariable.ReasonFailedPaging + reason)

		GlobalVariable.FlagFailed = 1
	}
}

public checkPaging() {
    'click button cari'
    WebUI.click(findTestObject('ErrorReport/button_Cari'))

    'click button set ulang'
    WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))

    'get defaultTanggalDari'
    String defaultTanggalDari = WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value')

    'get defaultTanggalKe'
    String defaultTanggalKe = WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value')

    'select modul'
    WebUI.setText(findTestObject('ErrorReport/select_Modul'), 'generate invitation link error history')

    'send keys enter'
    WebUI.sendKeys(findTestObject('ErrorReport/select_Modul'), Keys.chord(Keys.ENTER))

    'input no kontrak'
    WebUI.setText(findTestObject('ErrorReport/input_NoKontrak'), '0000')

    'input nama konsumen'
    WebUI.setText(findTestObject('ErrorReport/input_Nama'), 'abcd')

    'input lini bisnis'
    WebUI.setText(findTestObject('ErrorReport/input_LiniBisnis'), 'abcd')

    'input cabang'
    WebUI.setText(findTestObject('ErrorReport/input_Cabang'), 'abcd')

    'input wilayah'
    WebUI.setText(findTestObject('ErrorReport/input_Wilayah'), 'abcd')

    'input Tanggal dari'
    WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), '2023-08-01')

    'input Tanggal Ke'
    WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), '2023-08-11')

    'input Tanggal Ke'
    WebUI.setText(findTestObject('ErrorReport/select_Tipe'), 'REJECT')

    'send keys enter'
    WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))
	
    'click button cari'
    WebUI.click(findTestObject('ErrorReport/button_Cari'))

    'verif select modul'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Modul'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' modul')

    'verif no kontrak'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_NoKontrak'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' no kontrak')

    'verif nama konsumen'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Nama'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' nama konsumen')

    'verif lini bisnis'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_LiniBisnis'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' lini bisnis')

    'verif cabang'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Cabang'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' cabang')

    'verif wilayah'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Wilayah'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' wilayah')

    'verif tanggal dari'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'), 
            defaultTanggalDari, false, FailureHandling.CONTINUE_ON_FAILURE), ' tanggal dari')

    'verif tanggal ke'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value'), 
            defaultTanggalKe, false, FailureHandling.CONTINUE_ON_FAILURE), ' tanggal ke')

    'verif select tipe'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Tipe'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe')

    'click page 2'
    WebUI.click(findTestObject('ErrorReport/page_2'))

    'verify page 2 active'
    checkVerifyPaging(WebUI.getAttribute(findTestObject('ErrorReport/page_2'), 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' page 2')

    'click page 1'
    WebUI.click(findTestObject('ErrorReport/page_1'))

    'verify page 1 active'
    checkVerifyPaging(WebUI.getAttribute(findTestObject('ErrorReport/page_1'), 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' page 1')

    'click next page'
    WebUI.click(findTestObject('ErrorReport/button_NextPage'))

    'verify page 2 active'
    checkVerifyPaging(WebUI.getAttribute(findTestObject('ErrorReport/page_2'), 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' next page')

    'click previous page'
    WebUI.click(findTestObject('ErrorReport/button_PreviousPage'))

    'verify page 1 active'
    checkVerifyPaging(WebUI.getAttribute(findTestObject('ErrorReport/page_1'), 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' previous page')
	
	'get total page'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-error-report > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'modify object last Page'
	modifyObjectLastPage = WebUI.modifyObjectProperty(findTestObject('ErrorReport/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-error-report/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		(variable.size() - 2)) + ']', true)

	'click max page'
	WebUI.click(findTestObject('ErrorReport/button_MaxPage'))

	'verify paging di page terakhir'
	checkVerifyPaging(WebUI.getAttribute(modifyObjectLastPage, 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' last page')

	'click min page'
	WebUI.click(findTestObject('ErrorReport/button_MinPage'))

	 'verify page 1 active'
    checkVerifyPaging(WebUI.getAttribute(findTestObject('ErrorReport/page_1'), 'class', FailureHandling.CONTINUE_ON_FAILURE).contains('active'), ' first page')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}