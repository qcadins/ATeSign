import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver as WebDriver
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

'looping saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	} else if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		'set penanda error menjadi 0'
		GlobalVariable.FlagFailed = 0
		
        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathSaldo, ('sheet') : 'Saldo'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu saldo'
            WebUI.click(findTestObject('saldo/menu_saldo'))

			'click ddl bahasa'
			WebUI.click(findTestObject('Login/button_bahasa'))
			
			'click english'
			WebUI.click(findTestObject('Login/button_English'))
			
            'call function check paging'
            checkPaging(currentDate, firstDateOfMonth, conneSign)
			
			'get ddl tenant'
			ArrayList<String> resultVendor = CustomKeywords.'connection.Saldo.getDDLVendor'(conneSign, CustomKeywords.'connection.Saldo.getTenantName'(conneSign))
	
			'verify vendor DDL'
			WebUI.verifyOptionsPresent(findTestObject('Saldo/ddl_Vendor'), resultVendor, FailureHandling.CONTINUE_ON_FAILURE)
			
			'get ddl tipe Saldo'
			ArrayList<String> resultTipeSaldo = CustomKeywords.'connection.Saldo.getDDLTipeSaldo'(conneSign)
			
			'call function check ddl untuk Tipe Saldo'
			checkDDL(findTestObject('Saldo/input_tipesaldo'), resultTipeSaldo, 'DDL Tipe Saldo')
			
			'get ddl tipe trx'
			ArrayList<String> resultTipeTrx = CustomKeywords.'connection.Saldo.getDDLTipeTrx'(conneSign)
			
			'call function check ddl untuk Tipe Trx'
			checkDDL(findTestObject('Saldo/input_tipetransaksi'), resultTipeTrx, 'DDL Tipe Trx')
			
			'get ddl tipe dokumen'
			ArrayList<String> resultTipeDokumen = CustomKeywords.'connection.Saldo.getDDLTipeDokumen'(conneSign)
			
			'call function check ddl untuk Tipe Dokumen'
			checkDDL(findTestObject('Saldo/input_tipedokumen'), resultTipeDokumen, 'DDL Tipe Dokumen')
        }
		
		
		inputSaldo()
		
		'klik pada button cari'
		WebUI.click(findTestObject('Object Repository/Saldo/btn_cari'))
		
		'jika hasil pencarian tidak memberikan hasil'
		if(WebUI.verifyElementNotPresent(findTestObject('Object Repository/Saldo/label_TableTrxNo'),
			GlobalVariable.TimeOut, FailureHandling.OPTIONAL)){
		
			GlobalVariable.FlagFailed = 1
			
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + ' Failed Search Saldo Data')
		}
		
		ArrayList<String> result = CustomKeywords.'connection.Saldo.getTrxSaldo'(conneSign, findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 13), 
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 15), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 12), 
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 16))
		
		arrayIndex = 0
		
		'verify trx no ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTrxNo')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Trx No')
		
		'verify tgl trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTglTrx')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tgl Trx')
		
		'verify tipe trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTipeTrx')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')
		
		'verify trx oleh ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTrxOleh')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Trx Oleh')
		
		'verify no kontrak ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNoKontrak')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')
		
		'verify tipe dok ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableTipeDok')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dok')
		
		'verify nama dok ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNamaDok')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dok')
		
		'verify notes ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableNotes')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Note')
		
		'verify qty ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/label_TableQty')), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty')
		
		'verify Total Data ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Saldo/Label_TotalSaldo')), (result.size()/9).toString() + ' total', false, FailureHandling.CONTINUE_ON_FAILURE), ' Total Data Tidak Match')
		
		if (findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 19) == 'Yes'){
			
			'klik pada tombol unduh excel'
			WebUI.click(findTestObject('Object Repository/Saldo/button_UnduhExcel'))
			
			WebUI.delay(10)
			
			'pengecekan file yang sudah didownload'
			boolean isDownloaded = CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 20))
			
			println(isDownloaded)
		
			'jika file tidak terdeteksi telah terdownload'
			checkVerifyEqualOrMatch(WebUI.verifyEqual(isDownloaded, true, FailureHandling.CONTINUE_ON_FAILURE), GlobalVariable.ReasonFailedDownload)	
		}
	}
}

'tutup browser'
WebUI.closeBrowser()

def inputSaldo() {
	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), '(?i)' + findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 9), true)
	
   'input filter dari saldo'
	WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 11))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

	'Input tipe transaksi'
	WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 12))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_fromdate'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 13))

	'Input tipe dokumen'
	WebUI.setText(findTestObject('Saldo/input_tipedokumen'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 14))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

	'Input referal number'
	WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 15))

	'Input documentTemplateName'
	WebUI.setText(findTestObject('Saldo/input_namadokumen'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 16))

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_todate'), findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 17))
}

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth, Connection conneSign) {
	inputSaldo()

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
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe saldo')

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))
	
	'click ddl tipe transaksi'
	WebUI.click(findTestObject('Saldo/input_tipetransaksi'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe transaksi')
	
	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))
	
	'click ddl tipe dokumen'
	WebUI.click(findTestObject('Saldo/input_tipedokumen'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe dokumen')

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
	
	tenantCodeByUserLogin = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, findTestData(excelPathSetting).getValue(2,2).toUpperCase())
	
	'ambil total trx berdasarkan filter yang telah disiapkan pada db'
	totalTrxDB = CustomKeywords.'connection.Saldo.getTotalTrxBasedOnVendorAndBalanceType'(conneSign, tenantCodeByUserLogin, findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 9).toUpperCase(), 'Sign')
	
	'verify total Saldo'
	checkVerifyPaging(WebUI.verifyMatch(totalTrxUI[0], totalTrxDB, false, FailureHandling.CONTINUE_ON_FAILURE), ' total transaksi ui dan db tidak match')
	
	if (Integer.parseInt(totalTrxUI[0]) > 10) {
		'click next page'
		WebUI.click(findTestObject('Saldo/button_NextPage'))
	
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'2', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page selanjutnya tidak berfungsi')
	
		'click prev page'
		WebUI.click(findTestObject('Saldo/button_PrevPage'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page sebelumnya tidak berfungsi')
	
		'click last page'
		WebUI.click(findTestObject('Saldo/button_LastPage'))
	
		'verify paging di last page'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				WebUI.getAttribute(findTestObject('Saldo/page_Active'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace(
					'page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' button page terakhir tidak berfungsi')
	
		'click first page'
		WebUI.click(findTestObject('Saldo/button_FirstPage'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page pertama tidak berfungsi')
	}
}

def checkVerifyPaging(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
	'declare array untuk menampung ddl'
	ArrayList<String> list = []

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
	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + reason)
	
	'Input enter untuk tutup ddl'
	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}