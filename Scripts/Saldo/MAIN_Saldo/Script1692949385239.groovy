import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import java.time.LocalDate as LocalDate

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathSaldo).columnNumbers

'get dates'
currentDate = LocalDate.now()

firstDateOfMonth = currentDate.withDayOfMonth(1)

int firstRun = 0

'looping saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {		
		'deklarasi tenant global'
		GlobalVariable.Tenant = findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		
		'deklarasi date'
        String startDate = findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Transaksi Dari'))

        String endDate = findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Transaksi Sampai'))
				
        if ((findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathSaldo, ('Email') : 'Email Login'
                    , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'click menu saldo'
                WebUI.click(findTestObject('saldo/menu_saldo'))

                'click ddl bahasa'
                WebUI.click(findTestObject('Login/button_bahasa'))

                'click english'
                WebUI.click(findTestObject('Login/button_English'))

//                inputSaldo(startDate, endDate)
//
//                'call function check paging'
//                checkPaging(conneSign)
//
//                'get ddl tenant'
//                ArrayList resultVendor = CustomKeywords.'connection.Saldo.getDDLVendor'(conneSign, CustomKeywords.'connection.Saldo.getTenantName'(
//                        conneSign))
//
//                'verify vendor DDL'
//                WebUI.verifyOptionsPresent(findTestObject('Saldo/ddl_Vendor'), resultVendor, FailureHandling.CONTINUE_ON_FAILURE)
//
//                'get ddl tipe Saldo'
//                ArrayList resultTipeSaldo = CustomKeywords.'connection.Saldo.getDDLTipeSaldo'(conneSign)
//
//                'call function check ddl untuk Tipe Saldo'
//                checkDDL(findTestObject('Saldo/input_tipesaldo'), resultTipeSaldo, 'DDL Tipe Saldo')
//
//                'get ddl tipe trx'
//                ArrayList resultTipeTrx = CustomKeywords.'connection.Saldo.getDDLTipeTrx'(conneSign)
//
//                'call function check ddl untuk Tipe Trx'
//                checkDDL(findTestObject('Saldo/input_tipetransaksi'), resultTipeTrx, 'DDL Tipe Trx')
//
//                'get ddl tipe dokumen'
//                ArrayList resultTipeDokumen = CustomKeywords.'connection.Saldo.getDDLTipeDokumen'(conneSign)
//
//                'call function check ddl untuk Tipe Dokumen'
//                checkDDL(findTestObject('Saldo/input_tipedokumen'), resultTipeDokumen, 'DDL Tipe Dokumen')
            }
            
            firstRun = 1
        }
        
        if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        WebUI.refresh()

        'cek apakah perlu input start date by katalon'
        if (startDate == '') {
            startDate = firstDateOfMonth.toString()
        }
        
        'cek apakah perlu input end date by katalon'
        if (endDate == '') {
            endDate = currentDate.toString()
        }
        
        inputSaldo(startDate, endDate)

        'klik pada button cari'
        WebUI.click(findTestObject('Object Repository/Saldo/btn_cari'))

        'jika hasil pencarian tidak memberikan hasil'
        if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Saldo/label_TableTrxNo'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            GlobalVariable.FlagFailed = 1

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ' Failed Search Saldo Data')

            continue
        }
        
        ArrayList result = CustomKeywords.'connection.Saldo.getTrxSaldo'(conneSign, startDate, endDate,
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Nomor Kontrak')),
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Saldo')),
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Nama Dokumen')), 
            findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Office Name')),
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login')),
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Tipe Transaksi')),
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Tipe Dokumen')))
		
        arrayIndex = 0

        'verify trx no ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTrxNo')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Trx No')

        'verify tgl trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTglTrx')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tgl Trx')

        'verify cabang ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableCabang')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang')

        'verify tipe trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTipeTrx')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

        'verify trx oleh ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTrxOleh')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Trx Oleh')

        'verify no kontrak ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNoKontrak')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

        'verify tipe dok ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTipeDok')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dok')

        'verify nama dok ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNamaDok')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dok')

        'verify notes ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNotes')), 
                result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Note')

        'verify qty ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableQty')).replace(
                    ',', ''), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty')

        if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Download File')) == 'Yes') {
            'klik pada tombol unduh excel'
            WebUI.click(findTestObject('Object Repository/Saldo/button_UnduhExcel'))

            WebUI.delay(5)

            'pengecekan file yang sudah didownload'
            boolean isDownloaded = CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathSaldo).getValue(
                    GlobalVariable.NumofColm, rowExcel('Delete Downloaded File ?')))

            'jika file tidak terdeteksi telah terdownload'
            checkVerifyEqualOrMatch(WebUI.verifyEqual(isDownloaded, true, FailureHandling.CONTINUE_ON_FAILURE), GlobalVariable.ReasonFailedDownload)
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

'tutup browser'
WebUI.closeBrowser()

def inputSaldo(String startDate, String endDate) {
    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), '(?i)' + findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 
            rowExcel('Psre Login')), true)

    'input filter dari saldo'
	inputDDLExact('Saldo/input_tipesaldo', findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tipe Saldo')))

    'Input tipe transaksi'
	inputDDLExact('Saldo/input_tipetransaksi', findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm,
		rowExcel('Tipe Transaksi')))
	
    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), startDate)

    'Input tipe dokumen'
	inputDDLExact('Saldo/input_tipedokumen', findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm,
		rowExcel('Tipe Dokumen')))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 
            rowExcel('Nomor Kontrak')))

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 
            rowExcel('Nama Dokumen')))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), endDate)

    'Input office name'
	inputDDLExact('Saldo/input_officeName', findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm,
		rowExcel('Office Name')))
}

def checkPaging(Connection conneSign) {
    'Klik set ulang'
    WebUI.click(findTestObject('Saldo/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_fromdate'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - tanggal dari')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_refnumber'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - ref number')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_namadokumen'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - nama dokumen')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_todate'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - tanggal sampai')

    'click ddl tipe saldo'
    WebUI.click(findTestObject('Saldo/input_tipesaldo'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' field search form tidak kereset - DDL tipe saldo')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'click ddl tipe transaksi'
    WebUI.click(findTestObject('Saldo/input_tipetransaksi'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' field search form tidak kereset - DDL tipe transaksi')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'click ddl tipe dokumen'
    WebUI.click(findTestObject('Saldo/input_tipedokumen'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' field search form tidak kereset - DDL tipe dokumen')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), 'Sign')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))

    'ambil total trx berdasarkan filter yang telah disiapkan pada ui'
    totalTrxUI = WebUI.getText(findTestObject('Saldo/Label_TotalSaldo')).split(' ', -1)

    tenantCodeByUserLogin = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, findTestData(excelPathSaldo).getValue(
            GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase())

    'ambil total trx berdasarkan filter yang telah disiapkan pada db'
    totalTrxDB = CustomKeywords.'connection.Saldo.getTotalTrxBasedOnVendorAndBalanceType'(conneSign, tenantCodeByUserLogin, 
        findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login')).toUpperCase(), 'Sign')

    'verify total Saldo'
    checkVerifyPaging(WebUI.verifyMatch(totalTrxUI[0], totalTrxDB, false, FailureHandling.CONTINUE_ON_FAILURE), ' total transaksi ui dan db tidak match')

    if (Integer.parseInt(totalTrxUI[0]) > 10) {
        'click next page'
        WebUI.click(findTestObject('Saldo/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 2', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page selanjutnya tidak berfungsi')

        'click prev page'
        WebUI.click(findTestObject('Saldo/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page sebelumnya tidak berfungsi')

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click last page'
        WebUI.click(findTestObject('Saldo/button_LastPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('Saldo/label_TotalData')).split(' ', -1)[0]) / 10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page ' + Math.round(lastPage + additionalRoundUp), false, FailureHandling.CONTINUE_ON_FAILURE), 
            'last page')

        'click first page'
        WebUI.click(findTestObject('Saldo/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE), 
                'page 1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page pertama tidak berfungsi')
    }
}

def checkVerifyPaging(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPaging) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('isiSaldo/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('isiSaldo/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' + 
            id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
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
		'equals', '//*[@id="'+tokenUnique+'"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="'+tokenUnique+'"]/div/div[2]/div['+ (i + 1) +']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}
