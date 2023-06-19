import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathJobResult).columnNumbers

sheet = 'Job Result'

'declare variable looping'
int i,j

'looping Job Result'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin esign'
            WebUI.callTestCase(findTestCase('Login/Login_AdminEsign'), [('excel') : excelPathJobResult, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu job result'
            WebUI.click(findTestObject('Job Result/menu_Hasil Job'))

            'call function check paging'
            checkPaging(conneSign)
        }
        
        'set text date start'
        WebUI.setText(findTestObject('Job Result/input_requestDateStart'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                9))

        'set text date end'
        WebUI.setText(findTestObject('Job Result/input_requestDateEnd'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                12))

        'set text nama job'
        WebUI.setText(findTestObject('Job Result/input_JobName'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                10))

        'enter untuk nama job'
        WebUI.sendKeys(findTestObject('Job Result/input_JobName'), Keys.chord(Keys.ENTER))

        'set text process result'
        WebUI.setText(findTestObject('Job Result/input_ProcessResult'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                13))

        'enter untuk process result'
        WebUI.sendKeys(findTestObject('Job Result/input_ProcessResult'), Keys.chord(Keys.ENTER))

        'set text diminta oleh'
        WebUI.setText(findTestObject('Job Result/input_DimintaOleh'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                11))

        'click button cari'
        WebUI.click(findTestObject('Job Result/button_Cari'))

        'Jika value muncul'
        if (WebUI.verifyElementPresent(findTestObject('Job Result/lbl_value'), GlobalVariable.TimeOut)) {
			'Jika aksi yang dipilih adalah View Request Param'
            if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 7) == 'View Request Param') {
				'get row'
				row = DriverFactory.webDriver.findElements(By.cssSelector('#listJobResult > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))
	
                'modify object kepada aksi yang ada'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_value'), 'xpath', 'equals', 
                    '//*[@id="listJobResult"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + row.size() + ']/div/a[1]/em', true)

                'Klik aksi tersebut'
                WebUI.click(modifyObjectLblValue)

                'Jika modal title muncul'
                if (WebUI.verifyElementPresent(findTestObject('Object Repository/Job Result/lbl_titleViewRequestParam'), 
                    GlobalVariable.TimeOut)) {
                    'get row modal'
                    rowModal = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-view-request-param > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

                    'get job result data dari db'
                    result = CustomKeywords.'connection.JobResult.jobResultDB'(conneSign, findTestData(excelPathJobResult).getValue(
                            GlobalVariable.NumofColm, 9), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                            12))

                    index = 0

                    'looping row Modal'
                    for (j = 1; j <= rowModal.size(); j++) {
                        'modify object label value untuk modal'
                        modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_ValueModal'), 'xpath', 
                            'equals', ('/html/body/ngb-modal-window/div/div/app-view-request-param/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                            j) + ']/div/p', true)

                        'check value modal ke db'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLblValue), result[index++], 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ((' pada view request param kolom ke ' + j) + 
                            ' value db adalah ') + (result[(index - 1)]))
                    }
					
					'click button X'
					WebUI.click(findTestObject('KotakMasuk/btn_X'))
                } else {
					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedNoneUI'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Job Result', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI + ' pada View Request Param'))
				}
            } else if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 7) == '-') {
				'Jika tidak ada aksi yang dipilih, maka check flag failed'
				if (GlobalVariable.FlagFailed == 0) {
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
						1, GlobalVariable.StatusSuccess)
				}
			}
        } else {
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedNoneUI'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI + ' pada data tersebut '))
        }
	}
}

        
		/*
        'Jika value muncul'
        if (WebUI.verifyElementPresent(findTestObject('Job Result/lbl_value'), GlobalVariable.TimeOut)) {
            'get job result data dari db'
            result = CustomKeywords.'connection.JobResult.jobResultDB'(conneSign, findTestData(excelPathJobResult).getValue(
                    GlobalVariable.NumofColm, 8), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 11))

            index = 0

            'get row'
            row = DriverFactory.webDriver.findElements(By.cssSelector('#listJobResult > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

            'looping row'
            for (i = 1; i <= row.size(); i++) {
                'modify object label value setiap row'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_value'), 'xpath', 'equals', 
                    ('//*[@id="listJobResult"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                    i) + ']/div', true)

                'Jika row sudah kedelapan'
                if (i == 8) {
                    'modify object kepada aksi yang ada'
                    modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_value'), 'xpath', 'equals', 
                        ('//*[@id="listJobResult"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                        i) + ']/div/a[1]/em', true)

                    'Klik aksi tersebut'
                    WebUI.click(modifyObjectLblValue)

                    'Jika modal title muncul'
                    if (WebUI.verifyElementPresent(findTestObject('Object Repository/Job Result/lbl_titleViewRequestParam'), 
                        GlobalVariable.TimeOut)) {
                        'get row modal'
                        rowModal = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-view-request-param > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

                        'looping row Modal'
                        for (j = 1; j <= rowModal.size(); j++) {
                            'modify object label value untuk modal'
                            modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_ValueModal'), 
                                'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-view-request-param/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                                j) + ']/div/p', true)

                            'check value modal ke db'
                            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLblValue), result[index++], 
                                    false, FailureHandling.CONTINUE_ON_FAILURE), ((' pada view request param kolom ke ' + 
                                j) + ' value db adalah ') + (result[(index - 1)]))
                        }
                    }
                } else {
                    'verify teks di row kepada db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLblValue), result[index++], false, 
                            FailureHandling.CONTINUE_ON_FAILURE), ((' pada row ke ' + i) + ' value db adalah ') + (result[
                        (index - 1)]))
                }
            }
        }
        


        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Meterai', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
        */

def checkPaging(Connection conneSign) {
    'set text tanggal awal'
    WebUI.setText(findTestObject('Job Result/input_requestDateStart'), '2023-04-01')

    'set text tanggal akhir'
    WebUI.setText(findTestObject('Job Result/input_requestDateEnd'), '2023-04-30')

    'set text job name'
    WebUI.setText(findTestObject('Job Result/input_JobName'), 'Reconsile OTP Digisign')

    'enter untuk job name'
    WebUI.sendKeys(findTestObject('Job Result/input_JobName'), Keys.chord(Keys.ENTER))

    'set text process result'
    WebUI.setText(findTestObject('Job Result/input_ProcessResult'), 'Completed')

    'enter untuk process result'
    WebUI.sendKeys(findTestObject('Job Result/input_ProcessResult'), Keys.chord(Keys.ENTER))

    'set text diminta oleh'
    WebUI.setText(findTestObject('Job Result/input_DimintaOleh'), 'ADMESIGN')

    'click button set ulang'
    WebUI.click(findTestObject('Job Result/button_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_DimintaOleh'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' pada input diminta oleh')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_requestDateStart'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' pada input permintaan tanggal mulai')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_requestDateEnd'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' pada input permintaan tanggal berakhir ')

    'click ddl process result'
    WebUI.click(findTestObject('Job Result/input_ProcessResult'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Job Result/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada ddl Hasil Proses')

    'enter untuk process result'
    WebUI.sendKeys(findTestObject('Job Result/input_ProcessResult'), Keys.chord(Keys.ENTER))

    'click ddl job name'
    WebUI.click(findTestObject('Job Result/input_JobName'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Job Result/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada ddl Nama Job ')

    'enter untuk job name'
    WebUI.sendKeys(findTestObject('Job Result/input_JobName'), Keys.chord(Keys.ENTER))

    'set text date start'
    WebUI.setText(findTestObject('Job Result/input_requestDateStart'), '2023-04-01')

    'set text date end'
    WebUI.setText(findTestObject('Job Result/input_requestDateEnd'), '2023-04-30')

    'click button cari'
    WebUI.click(findTestObject('Job Result/button_Cari'))

    'get text total job dari UI'
    totalJobResultUI = WebUI.getText(findTestObject('Job Result/label_TotalJobResult')).split(' ', -1)

    'get text total job dari DB'
    totalJobResultDB = CustomKeywords.'connection.JobResult.countJobResult'(conneSign, '2023-04-01', '2023-04-30')

    'verify total job result'
    checkVerifyPaging(WebUI.verifyMatch(totalJobResultUI[0], totalJobResultDB, false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada total Hasil Job ')
}

def checkVerifyPaging(Boolean isMatch, def reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Job Result', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Job Result', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

