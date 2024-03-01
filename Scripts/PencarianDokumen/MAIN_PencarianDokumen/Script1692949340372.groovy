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

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianDokumen).columnNumbers

int firstRun = 0

'looping pencarian dokumen'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm - 1, rowExcel('Username')) != findTestData(
            excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Username'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathPencarianDokumen
                    , ('Email') : 'Username', ('Password') : 'Password', ('Perusahaan') : 'Perusahaan', ('Peran') : 'Role'], 
                FailureHandling.STOP_ON_FAILURE)

            'click menu pencarian dokumen'
            WebUI.click(findTestObject('PencarianDokumen/menu_PencarianDokumen'))

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)
            }
            
            firstRun = 1
        }
        
        'set tenant aktif'
        GlobalVariable.Tenant = findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

        'set vendor aktif'
        GlobalVariable.Psre = findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Vendor'))

        'set userlogin aktif'
        GlobalVariable.userLogin = findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Username'))

        if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'cek apakah field nama pelanggan muncul'
        if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/input_NamaPelanggan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'input nama pelanggan'
            WebUI.setText(findTestObject('PencarianDokumen/input_NamaPelanggan'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Nama Pelanggan')))

            'input no kontrak'
            WebUI.setText(findTestObject('PencarianDokumen/input_NomorKontrak'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Nomor Kontrak')))

            'input TanggalPermintaanDari'
            WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanDari'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Tanggal Permintaan Dari')))

            'input TanggalPermintaanSampai'
            WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanSampai'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Tanggal Permintaan Sampai')))

            'input TanggalSelesaiDari'
            WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiDari'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Tanggal Selesai Dari')))

            'input TanggalSelesaiSampai'
            WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiSampai'), findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Tanggal Selesai Sampai')))

            'input tipeDokumen'
			inputDDLExact('PencarianDokumen/select_TipeDokumen', findTestData(excelPathPencarianDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Tipe Dokumen')))
        }
        
        'input status'
		inputDDLExact('PencarianDokumen/select_Status', findTestData(excelPathPencarianDokumen).getValue(
			GlobalVariable.NumofColm, rowExcel('StatusDoc')))

        'click button cari'
        WebUI.click(findTestObject('PencarianDokumen/button_Cari'))

        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'ambil teks errormessage'
            errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            continue
        }
        
        if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Dokumen Action')) == 
        'View Dokumen') {
            'click button view dokumen'
            WebUI.click(findTestObject('PencarianDokumen/button_ViewDokumen'))

            'verify no kontrak'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PencarianDokumen/label_noKontrak')).replace(
                        'No Kontrak ', ''), findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                            '$Nomor Kontrak')), false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

            'click button kembali'
            WebUI.click(findTestObject('PencarianDokumen/button_Kembali'))
        } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Dokumen Action')) == 
        'Download') {
            'click button Download'
            WebUI.click(findTestObject('PencarianDokumen/button_Download'))

            'delay untuk menunggu proses download'
            WebUI.delay(3)

            'check if file downloaded'
            if (WebUI.verifyEqual(CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathPencarianDokumen).getValue(
                        GlobalVariable.NumofColm, rowExcel('$Delete File'))), true, FailureHandling.CONTINUE_ON_FAILURE)) {
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedDownload)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Dokumen Action')) == 
        'View Signer') {
            'click button view Signer'
            WebUI.click(findTestObject('PencarianDokumen/button_ViewSigner'))

            'get data signer'
            ArrayList resultDataSigner = CustomKeywords.'connection.PencarianDokumen.getSignerInfo'(conneSign, findTestData(
                    excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Kontrak')), GlobalVariable.Psre)

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
                rowExcel('Is Mandatory Complete')))

        if ((GlobalVariable.FlagFailed == 0) && (isMandatoryComplete == 0)) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        } else {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedMandatory)
        }
    }
}

def checkPaging(Connection conneSign) {
    'cek apakah field nama pelanggan muncul'
    if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/input_NamaPelanggan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
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
		inputDDLExact('PencarianDokumen/select_TipeDokumen', 'Dokumen Umum')
    }
    
    'input status'
	inputDDLExact('PencarianDokumen/select_Status', 'Need Sign')

    'click button set ulang'
    WebUI.click(findTestObject('PencarianDokumen/button_SetUlang'))

    'cek apakah field nama pelanggan muncul'
    if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/input_NamaPelanggan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
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
    }
    
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

    'cek apakah possible adanya halaman kedua'
    if (Integer.parseInt(Total[0]) > 10) {
        'click page 2'
        WebUI.click(findTestObject('PencarianDokumen/button_Page2'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page2'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click page 1'
        WebUI.click(findTestObject('PencarianDokumen/button_Page1'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click next page'
        WebUI.click(findTestObject('PencarianDokumen/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page2'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('PencarianDokumen/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click max page'
        WebUI.click(findTestObject('PencarianDokumen/button_MaxPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('PencarianDokumen/label_TotalData')).split(' ', -1)[
            0]) / 10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), Math.round(lastPage + additionalRoundUp), false, FailureHandling.CONTINUE_ON_FAILURE))

        'click min page'
        WebUI.click(findTestObject('PencarianDokumen/button_MinPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathPencarianDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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