import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDate as LocalDate
import java.time.YearMonth as YearMonth
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

int firstRun = 0

'looping Job Result'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathJobResult, ('Email') : 'Email Login'
                    , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)
            }
            
            firstRun = 1
        }
        
        if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'click menu job result'
        WebUI.click(findTestObject('Job Result/menu_Hasil Job'), FailureHandling.OPTIONAL)

        'set text date start'
        WebUI.setText(findTestObject('Job Result/input_requestDateStart'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                rowExcel('Permintaan Tanggal Mulai')))

        'set text date end'
        WebUI.setText(findTestObject('Job Result/input_requestDateEnd'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                rowExcel('Permintaan Tanggal Berakhir')))

        'set text nama job'
		inputDDLExact('Job Result/input_JobName', findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                rowExcel('Nama Job')))

        'set text process result'
		inputDDLExact('Job Result/input_ProcessResult', findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm,
			rowExcel('Hasil Proses')))

        'set text diminta oleh'
        WebUI.setText(findTestObject('Job Result/input_DimintaOleh'), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                rowExcel('Diminta Oleh')))

        'click button cari'
        WebUI.click(findTestObject('Job Result/button_Cari'))

        if (checkErrorLog() == true) {
            break
        }
		
        'Jika value muncul'
        if (WebUI.verifyElementPresent(findTestObject('Job Result/lbl_value'), GlobalVariable.TimeOut)) {
			'get column popup'
			variableColPopup = DriverFactory.webDriver.findElements(By.cssSelector('#listJobResult > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))
			
			ArrayList storeDB = CustomKeywords.'connection.JobResult.jobResultDB'(conneSign, findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Permintaan Tanggal Mulai')), findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Permintaan Tanggal Berakhir')))
			
			arrayIndex = 0
			for (loopingColumn = 1; loopingColumn <= variableColPopup.size(); loopingColumn++) {
				'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
				modifyObject = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'), 'xpath', 'equals',
					'//*[@id="listJobResult"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + loopingColumn + ']/div', true)
				
				if (loopingColumn == variableColPopup.size()) {
					continue
				}
				
				'check value modal ke db'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObject), storeDB[arrayIndex++],
						false, FailureHandling.CONTINUE_ON_FAILURE), 'Seaching Page kolom ke-' + loopingColumn)
			}
            'Jika aksi yang dipilih adalah View Request Param'
            if (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'View Request Param') {
                'get row'
                row = DriverFactory.webDriver.findElements(By.cssSelector('#listJobResult > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

                'modify object kepada aksi yang ada'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Job Result/lbl_value'), 'xpath', 'equals', 
                    ('//*[@id="listJobResult"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                    row.size()) + ']/div/a[1]/em', true)

                'Klik aksi tersebut'
                WebUI.click(modifyObjectLblValue)

                'Jika modal title muncul'
                if (WebUI.verifyElementPresent(findTestObject('Object Repository/Job Result/lbl_titleViewRequestParam'), 
                    GlobalVariable.TimeOut)) {
                    'get row modal'
                    rowModal = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-view-request-param > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

                    'get job result data dari db'
                    result = CustomKeywords.'connection.JobResult.jobResultViewReqParamDB'(conneSign, findTestData(excelPathJobResult).getValue(
                            GlobalVariable.NumofColm, rowExcel('Permintaan Tanggal Mulai')), findTestData(excelPathJobResult).getValue(
                            GlobalVariable.NumofColm, rowExcel('Permintaan Tanggal Berakhir')))

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
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada View Request Param')
                }
            }
            
            'Jika tidak ada aksi yang dipilih, maka check flag failed'
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
            
            'click button set ulang'
            WebUI.click(findTestObject('Job Result/button_Set Ulang'))
        } else {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedNoneUI'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                GlobalVariable.ReasonFailedNoneUI) + ' pada data tersebut ')
        }
    }
}

def checkPaging(Connection conneSign) {
    'click menu job result'
    WebUI.click(findTestObject('Job Result/menu_Hasil Job'), FailureHandling.OPTIONAL)

    'get dates'
    currentDate = LocalDate.now()

    firstDate = YearMonth.now().atDay(1)

    'set text tanggal awal'
    WebUI.setText(findTestObject('Job Result/input_requestDateStart'), '2023-04-01')

    'set text tanggal akhir'
    WebUI.setText(findTestObject('Job Result/input_requestDateEnd'), '2023-04-30')

    'set text job name'
	inputDDLExact('Job Result/input_JobName', 'Reconsile OTP Digisign')
	
    'set text process result'
	inputDDLExact('Job Result/input_ProcessResult', 'Completed')

    'set text diminta oleh'
    WebUI.setText(findTestObject('Job Result/input_DimintaOleh'), 'ADMESIGN')

    'click button set ulang'
    WebUI.click(findTestObject('Job Result/button_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_DimintaOleh'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' pada input diminta oleh')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_requestDateStart'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), firstDate.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada input permintaan tanggal mulai')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/input_requestDateEnd'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            currentDate.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada input permintaan tanggal berakhir ')

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

    'verify total Meterai'
    if (Integer.parseInt(totalJobResultUI[0]) > 10) {
        'click next page'
        WebUI.click(findTestObject('Job Result/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 2', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('Job Result/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click last page'
        WebUI.click(findTestObject('Job Result/button_LastPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('Job Result/label_TotalJobResult')).split(' ', -1)[0]) / 
        10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page ' + Math.round(lastPage + additionalRoundUp), false, FailureHandling.CONTINUE_ON_FAILURE))

        'click first page'
        WebUI.click(findTestObject('Job Result/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Job Result/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyPaging(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPaging) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<') + errormessage) + 
            '>')

        return true
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathJobResult).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))
	
	'get token unik'
	tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')
	
	'modify object label Value'
	modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div[' + (i + 1) + ']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}