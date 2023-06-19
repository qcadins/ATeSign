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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'call test case login andy'
WebUI.callTestCase(findTestCase('Login/Login_Andy'), [:], FailureHandling.STOP_ON_FAILURE)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'click menu pencarian dokumen'
WebUI.click(findTestObject('PencarianDokumen/menu_PencarianDokumen'))

'call function check paging'
checkPaging(conneSign)

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianDokumen).columnNumbers

'looping pencarian dokumen'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'input nama pelanggan'
        WebUI.setText(findTestObject('PencarianDokumen/input_NamaPelanggan'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 9))

        'input no kontrak'
        WebUI.setText(findTestObject('PencarianDokumen/input_NomorKontrak'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 10))

        'input TanggalPermintaanDari'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanDari'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 11))

        'input TanggalPermintaanSampai'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanSampai'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 12))

        'input TanggalSelesaiDari'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiDari'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 13))

        'input TanggalSelesaiSampai'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiSampai'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 14))

        'input tipeDokumen'
        WebUI.setText(findTestObject('PencarianDokumen/select_TipeDokumen'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 15))

        'click enter untuk input select ddl'
        WebUI.sendKeys(findTestObject('PencarianDokumen/select_TipeDokumen'), Keys.chord(Keys.ENTER))

        'input status'
        WebUI.setText(findTestObject('PencarianDokumen/select_Status'), findTestData(excelPathPencarianDokumen).getValue(
                GlobalVariable.NumofColm, 16))

        'click enter untuk input select ddl'
        WebUI.sendKeys(findTestObject('PencarianDokumen/select_Status'), Keys.chord(Keys.ENTER))

        'click button cari'
        WebUI.click(findTestObject('PencarianDokumen/button_Cari'))

        if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 7) == 'View Dokumen') {
            'click button view dokumen'
            WebUI.click(findTestObject('PencarianDokumen/button_ViewDokumen'))

            'verify no kontrak'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PencarianDokumen/label_noKontrak')).replace(
                        'No Kontrak ', ''), findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 10), 
                    false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

            'click button kembali'
            WebUI.click(findTestObject('PencarianDokumen/button_Kembali'))
        } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 7) == 'Download') {
            'click button Download'
            WebUI.click(findTestObject('PencarianDokumen/button_Download'))

            'delay untuk menunggu proses download'
            WebUI.delay(3)

            'check if file downloaded'
            if (WebUI.verifyEqual(CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathPencarianDokumen).getValue(
                        GlobalVariable.NumofColm, 18)), true, FailureHandling.CONTINUE_ON_FAILURE)) {
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianDokumen', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianDokumen', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedDownload)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 7) == 'View Signer') {
            'click button view Signer'
            WebUI.click(findTestObject('PencarianDokumen/button_ViewSigner'))

            'get data signer'
            ArrayList<String> resultDataSigner = CustomKeywords.'connection.PencarianDokumen.getSignerInfo'(conneSign, findTestData(
                    excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 10), GlobalVariable.Psre)

            'get row'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'index db'
            arrayIndexDB = 0

            'looping table data signer'
            for (index = 1; index <= variable.size(); index++) {
                'modify object label tipe'
                modifyObjectLabelTipe = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 'xpath', 
                    'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)

                'modify object label Nama'
                modifyObjectLabelNama = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 'xpath', 
                    'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                'modify object label Email'
                modifyObjectLabelEmail = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 'xpath', 
                    'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                'modify object label register status'
                modifyObjectLabelRegisterStatus = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 
                    'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

                'modify object label Status sign'
                modifyObjectLabelSignStatus = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 
                    'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

                'modify object label sign Date'
                modifyObjectLabelSignDate = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 
                    'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/span', true)

                'verify tipe'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelTipe), resultDataSigner[arrayIndexDB++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dok')

                'verify Nama'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelNama), resultDataSigner[arrayIndexDB++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

                'verify Email'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelEmail), resultDataSigner[arrayIndexDB++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

                'verify register Status'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelRegisterStatus), resultDataSigner[
                        arrayIndexDB++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Register Status')

                'verify sign status'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelSignStatus), resultDataSigner[arrayIndexDB++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign Status')

                'verify sign date'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLabelSignDate), resultDataSigner[arrayIndexDB++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign Date')
            }
            
            'click button X'
            WebUI.click(findTestObject('PencarianDokumen/button_X'))
        }
        
        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 
                5))

        if ((GlobalVariable.FlagFailed == 0) && (isMandatoryComplete == 0)) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianDokumen', 0, 
                GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        } else {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianDokumen', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + 
                GlobalVariable.ReasonFailedMandatory)
        }
    }
}

public checkPaging(Connection conneSign) {
    'input nama pelanggan'
    WebUI.setText(findTestObject('PencarianDokumen/input_NamaPelanggan'), 'nama pelanggan')

    'input no kontrak'
    WebUI.setText(findTestObject('PencarianDokumen/input_NomorKontrak'), '1234567890')

    'input TanggalPermintaanDari'
    WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanDari'), '2023-04-01')

    'input TanggalPermintaanSampai'
    WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanSampai'), '2023-04-10')

    'input TanggalSelesaiDari'
    WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiDari'), '2023-04-01')

    'input TanggalSelesaiSampai'
    WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiSampai'), '2023-04-10')

    'input tipeDokumen'
    WebUI.setText(findTestObject('PencarianDokumen/select_TipeDokumen'), 'Dokumen Umum')

    'click enter untuk input select ddl'
    WebUI.sendKeys(findTestObject('PencarianDokumen/select_TipeDokumen'), Keys.chord(Keys.ENTER))

    'input status'
    WebUI.setText(findTestObject('PencarianDokumen/select_Status'), 'Need Sign')

    'click enter untuk input select ddl'
    WebUI.sendKeys(findTestObject('PencarianDokumen/select_Status'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('PencarianDokumen/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_NamaPelanggan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_NomorKontrak'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_TanggalPermintaanDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_TanggalPermintaanSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_TanggalSelesaiDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/input_TanggalSelesaiSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/select_TipeDokumen'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/select_Status'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click button cari'
    WebUI.click(findTestObject('PencarianDokumen/button_Cari'))

    'get data signer'
    int resultTotalData = CustomKeywords.'connection.PencarianDokumen.getTotalPencarianDokumen'(conneSign, GlobalVariable.userLogin, 
        GlobalVariable.Tenant)

    'get text total data dari ui'
    Total = WebUI.getText(findTestObject('PencarianDokumen/label_TotalData')).split(' ')

    'verify total data pencarian dokumen'
    checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))

    'click page 2'
    WebUI.click(findTestObject('PencarianDokumen/button_Page2'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 1'
    WebUI.click(findTestObject('PencarianDokumen/button_Page1'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click next page'
    WebUI.click(findTestObject('PencarianDokumen/button_NextPage'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('PencarianDokumen/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'get total page'
    def variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object last Page'
    def modifyObjectLastPage = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        (variable.size() - 2).toString()) + ']', true)

    'click max page'
    WebUI.click(findTestObject('PencarianDokumen/button_MaxPage'))

    'verify paging di page terakhir'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectLastPage, 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click min page'
    WebUI.click(findTestObject('PencarianDokumen/button_MinPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
}

public checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

public checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

