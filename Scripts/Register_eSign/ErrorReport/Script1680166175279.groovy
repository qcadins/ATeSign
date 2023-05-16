import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
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
String resultTotalData = CustomKeywords.'connection.DataVerif.getTotalDataError'(conneSign, WebUI.getAttribute(findTestObject(
            'ErrorReport/input_TanggalDari'), 'value'))

'get error detail dari DB'
String resultErrorDetail = CustomKeywords.'connection.DataVerif.getErrorReportDetail'(conneSign, findTestData(excelPathBuatUndangan).getValue(
        GlobalVariable.NumofColm, 10).toUpperCase())

'get total data'
totalData = WebUI.getText(findTestObject('ErrorReport/label_TotalData')).split(' ')

'verify total data UI dan DB'
checkVerifyEqualOrMatch(WebUI.verifyMatch((totalData[0]).replace(',', ''), resultTotalData, false, FailureHandling.CONTINUE_ON_FAILURE))

'select modul'
WebUI.setText(findTestObject('ErrorReport/select_Modul'), 'generate invitation link error history')

'send keys enter'
WebUI.sendKeys(findTestObject('ErrorReport/select_Modul'), Keys.chord(Keys.ENTER))

'input nama'
WebUI.setText(findTestObject('ErrorReport/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        10))

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
        false, FailureHandling.OPTIONAL))

'click button view'
WebUI.click(findTestObject('ErrorReport/button_View'))

'verify match tipe error'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ErrorReport/label_ErrorDetail')), resultErrorDetail, 
        false, FailureHandling.OPTIONAL))

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

'click button status aktivasi'
WebUI.click(findTestObject('ErrorReport/button_StatusAktivasi'))

'get total status aktivasi'
totalStatusAktivasi = WebUI.getText(findTestObject('ErrorReport/label_TotalStatusAktivasi')).split('')

'verify match total status aktivasi'
checkVerifyEqualOrMatch(WebUI.verifyMatch(totalStatusAktivasi[0], '0', false, FailureHandling.OPTIONAL))

'click button X'
WebUI.click(findTestObject('ErrorReport/button_X'))

public checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch)

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
    WebUI.setText(findTestObject('ErrorReport/input_TanggalDari'), '2012-12-12')

    'input Tanggal Ke'
    WebUI.setText(findTestObject('ErrorReport/input_TanggalKe'), '2020-12-12')

    'input Tanggal Ke'
    WebUI.setText(findTestObject('ErrorReport/select_Tipe'), 'REJECT')

    'send keys enter'
    WebUI.sendKeys(findTestObject('ErrorReport/select_Tipe'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('ErrorReport/button_Cari'))

    'click button set ulang'
    WebUI.click(findTestObject('Object Repository/ErrorReport/button_Reset'))

    'verif select modul'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Modul'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif no kontrak'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_NoKontrak'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif nama konsumen'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Nama'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_LiniBisnis'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif cabang'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Cabang'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif wilayah'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_Wilayah'), 'value'), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif tanggal dari'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalDari'), 'value'), 
            defaultTanggalDari, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif tanggal ke'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/input_TanggalKe'), 'value'), 
            defaultTanggalKe, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verif select tipe'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/select_Tipe'), 'value'), '', 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 2'
    WebUI.click(findTestObject('ErrorReport/page_2'))

    'verify page 2 active'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), 
            '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 1'
    WebUI.click(findTestObject('ErrorReport/page_1'))

    'verify page 1 active'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), 
            '1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click next page'
    WebUI.click(findTestObject('ErrorReport/button_NextPage'))

    'verify page 2 active'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), 
            '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click previous page'
    WebUI.click(findTestObject('ErrorReport/button_PreviousPage'))

    'verify page 1 active'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ErrorReport/label_Page'), 'ng-reflect-page'), 
            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

