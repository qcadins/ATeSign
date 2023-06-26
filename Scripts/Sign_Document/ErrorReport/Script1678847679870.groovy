import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.By
import org.openqa.selenium.Keys

'click menu ErrorReport'
WebUI.click(findTestObject('ErrorReport/menu_ErrorReport'))

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'call function check paging'
checkPaging()

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'get total data error dari DB'
String resultTotalData = CustomKeywords.'connection.ErrorReport.getTotalDataError'(conneSign, WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'))

'get status activation'
ArrayList<String> resultStatusAktivasi = CustomKeywords.'connection.ErrorReport.getStatusActivation'(conneSign)

'get total data'
totalData = WebUI.getText(findTestObject('ErrorReport/label_TotalData')).split(' ')

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(totalData[0].replace(',', ''), resultTotalData, false, FailureHandling.CONTINUE_ON_FAILURE), ' Total Data')

'select modul'
WebUI.setText(findTestObject('ErrorReport/select_Modul'), 'Sign Document error history')

'send keys enter'
WebUI.sendKeys(findTestObject('ErrorReport/select_Modul'), Keys.chord(Keys.ENTER))

'click button cari'
WebUI.click(findTestObject('ErrorReport/button_Cari'))

'click final page'
WebUI.click(findTestObject('ErrorReport/button_FinalPage'))

'get row'
variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-error-report > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

'modify object button view'
modifyObjectButtonView = WebUI.modifyObjectProperty(findTestObject('ErrorReport/button_View'), 'xpath', 'equals', 
	"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-error-report/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + variable.size() + "]/datatable-body-row/div[2]/datatable-body-cell[10]/div/a[1]/em",true)

'modify object label nama konsumen'
modifyObjectLabelNamaKonsumen = WebUI.modifyObjectProperty(findTestObject('ErrorReport/label_NamaKonsumen'), 'xpath', 'equals', 
	"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-error-report/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[" + variable.size() + "]/datatable-body-row/div[2]/datatable-body-cell[3]/div/p",true)

'get error detail dari DB'
String resultErrorDetail = CustomKeywords.'connection.ErrorReport.getErrorReportDetail'(conneSign, WebUI.getText(modifyObjectLabelNamaKonsumen).toUpperCase())

'click button view'
WebUI.click(modifyObjectButtonView)

'verify match Error detail'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_ErrorDetail')), resultErrorDetail, false, FailureHandling.OPTIONAL), ' Error Detail')

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

'click button status aktivasi'
WebUI.click(findTestObject('ErrorReport/button_StatusAktivasi'))

index = 0

'verify data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerType')), 'Customer', false, FailureHandling.CONTINUE_ON_FAILURE), ' Signer Type')

'verify data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerName')), resultStatusAktivasi[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Signer Name')

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_SignerNIK')), resultStatusAktivasi[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Signer NIK')

'declare status'
String status = '', isregis = resultStatusAktivasi[index++], isaktif = resultStatusAktivasi[index++]

'cek if regis = 0 && aktif = 0 maka status belum registrasi'
if(isregis == '0' && isaktif == '0') {
	status = 'Belum Registrasi'
//cek if regis = 1 && aktif = 0 maka status belum aktivasi
}else if(isregis == '1' && isaktif == '0') {
	status = 'Belum Aktivasi'
//cek if regis = 1 && aktif = 1 maka status sudah aktivasi
}else if(isregis == '1' && isaktif == '1') {
	status = 'Sudah Aktivasi'
}

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_StatusAktivasi')), status, false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Aktivasi')

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathSignDoc).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Bulk Sign Document', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathSignDoc).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

		GlobalVariable.FlagFailed = 1
	}
}

def checkPaging() {
	
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
	WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), '2020-12-12')
	
	'input Tanggal Ke'
	WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), '2020-12-12')
	
	'Select Tipe'
	WebUI.setText(findTestObject('ErrorReport/select_Tipe'), 'REJECT')
	
	'send keys enter'
	WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))
	
	'click button set ulang'
	WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))
	
	'click button cari'
	WebUI.click(findTestObject('ErrorReport/button_Cari'))
	
	'verif select modul'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Modul'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif no kontrak'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_NoKontrak'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif nama konsumen'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Nama'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif lini bisnis'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_LiniBisnis'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif cabang'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Cabang'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif wilayah'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Wilayah'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif tanggal dari'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'), defaultTanggalDari, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif tanggal ke'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value'), defaultTanggalKe, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verif select tipe'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Tipe'), 'value'), '', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click page 2'
	WebUI.click(findTestObject('ErrorReport/page_2'))
	
	'verify page 2 active'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click page 1'
	WebUI.click(findTestObject('ErrorReport/page_1'))
	
	'verify page 1 active'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click next page'
	WebUI.click(findTestObject('ErrorReport/button_NextPage'))
	
	'verify page 2 active'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click previous page'
	WebUI.click(findTestObject('ErrorReport/button_PreviousPage'))
	
	'verify page 1 active'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}