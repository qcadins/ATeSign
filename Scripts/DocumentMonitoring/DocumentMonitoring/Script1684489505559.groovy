import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'call testcase login admin'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathDocumentMonitoring, ('sheet') : 'DocumentMonitoring'], FailureHandling.CONTINUE_ON_FAILURE)

GlobalVariable.FlagFailed = 0

'click menu DocumentMonitoring'
WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

'call function check paging'
checkPaging()

'get colm excel'
int countColmExcel = findTestData(excelPathDocumentMonitoring).columnNumbers

'looping DocumentMonitoring'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'set text nama Pelanggan'
        WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 9))

        'set text no kontrak'
        WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 10))

        'set text tanggal permintaan dari'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 11))

        'set text tanggal permintaan sampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 12))

        'set text TanggalSelesaoSampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 13))

        'set text TanggalSelesaoSampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 14))

        'set text tipe dok'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 15))

        'enter untuk set tipe dok'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

        'set text status'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 16))

        'enter untuk set status'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

        'set text tanggal wilayah'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 17))

        'enter untuk set wilayah'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

        'set text tanggal cabang'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, 18))

        'enter untuk set cabang'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))

        'click button cari'
        WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
		
        'check if action yang dilakukan sesuai excel'
        if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('View Dokumen')) {
            'click button view dokumen'
            WebUI.click(findTestObject('DocumentMonitoring/button_View'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + errorLog)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Download')) {
            'click button download'
            WebUI.click(findTestObject('DocumentMonitoring/button_Download'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + errorLog)

                GlobalVariable.FlagFailed = 1
            }
            
            'check isfiled downloaded'
            if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathDocumentMonitoring).getValue(
                    GlobalVariable.NumofColm, 19)) == false) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedDownload)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('View Signer')) {
            'click button view signer'
            WebUI.click(findTestObject('DocumentMonitoring/button_Signer'))

            'get row'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'get signer data dari db'
            result = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringSigner'(conneSign, findTestData(excelPathDocumentMonitoring).getValue(
                    GlobalVariable.NumofColm, 10).toUpperCase())

            'looping table yang muncul pada ui'
            for (index = 1; index <= variable.size(); index++) {
                'modify object user type'
                modifyObjectUserType = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 'xpath', 
                    'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

                arrayIndex = result.indexOf(WebUI.getText(modifyObjectUserType))

                userType = (result[arrayIndex++])

                if (WebUI.getText(modifyObjectUserType).equalsIgnoreCase(userType)) {
                    'modify object user name'
                    modifyObjectUserName = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

                    'modify object user email'
                    modifyObjectUserEmail = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

                    'modify object register status'
                    modifyObjectRegisterStatus = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

                    'modify object sign status'
                    modifyObjectSignStatus = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

                    'modify object sign Date'
                    modifyObjectSignDate = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div', true)

                    'verify user type'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserType), userType, false, FailureHandling.CONTINUE_ON_FAILURE), ' User Type')

                    'verify user name'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserName), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' User Name')

                    'verify user email'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserEmail), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' User Email')

                    'verify register status'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectRegisterStatus), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Register Status')

                    'verify sign status'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectSignStatus), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign Status')

                    'verify sign date'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectSignDate), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign date')
                }
            }
            
            'click button X'
            WebUI.click(findTestObject('DocumentMonitoring/button_X'))
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Kirim Ulang Notifikasi')) {
            'click button KirimUlangNotifikasi'
            WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + errorLog)

                GlobalVariable.FlagFailed = 1
            } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_YaProses'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'click button tidak batalkan'
                WebUI.click(findTestObject('DocumentMonitoring/button_TidakBatalkan'))

                'click button KirimUlangNotifikasi'
                WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

                'click button ya proses'
                WebUI.click(findTestObject('DocumentMonitoring/button_YaProses'))

                'check if pop up success message'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_OK'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                    'click button OK'
                    WebUI.click(findTestObject('DocumentMonitoring/button_OK'))
                } else {
					'check if error alert present'
					if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
						'get reason dari error log'
						errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
		
						'Write To Excel GlobalVariable.StatusFailed and errorLog'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm,
								2) + ';') + errorLog)
		
						GlobalVariable.FlagFailed = 1
					}
					
					if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/PopupMessage'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
					'get text dari popup message'
                    WebUI.getText(findTestObject('DocumentMonitoring/PopUpMessage'))

                    'Write To Excel GlobalVariable.StatusFailed and errorLog'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + errorLog)

                    GlobalVariable.FlagFailed = 1
					}
                }
            }
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'DocumentMonitoring', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        }
    }
}

def checkPaging() {
    'set text nama Pelanggan'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'nama pelanggan')

    'set text no kontrak'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), 'no kontrak')

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
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_NoKontrak'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_TipeDok'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Status'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Wilayah'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Cabang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click field lain untuk close ddl'
    WebUI.click(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'))

    'click button cari'
    WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))

	WebUI.delay(3)
	
    'click next page'
    WebUI.click(findTestObject('DocumentMonitoring/button_NextPage'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('DocumentMonitoring/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click last page'
    WebUI.click(findTestObject('DocumentMonitoring/button_LastPage'))

    'verify paging di last page'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), WebUI.getAttribute(findTestObject('DocumentMonitoring/page_Active'), 
                'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace('page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

    'click first page'
    WebUI.click(findTestObject('DocumentMonitoring/button_FirstPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'ng-reflect-page', 
                FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('DocumentMonitoring', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

