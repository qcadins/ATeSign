import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
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

            'click menu meterai'
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
        }
	}
	
	'dari sini sampe kebawah belum. Check ddl tipe transaksi querynya udah dapet, tpi count ddlnya belum'
		
//		'ambil nama saldo tenant yang aktif di DB'
//		ArrayList<String> activeBalanceDB = CustomKeywords.'saldo.VerifSaldo.getListActiveBalance'(conn, tenantcode)
//		
//		'ambil nama saldo tenant aktif di UI'
//		ArrayList<String> activeBalanceUI = []
//		
//		'cari element dengan nama saldo'
//		def elementNamaSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
//			' div > div.main-panel > div > div.content-wrapper > app-balance-prod >'+
//			' div.row.match-height > div > lib-balance-summary > div > div'))
//		
//		'lakukan loop untuk cari nama saldo yang ditentukan'
//		for (int i=1; i<=elementNamaSaldo.size(); i++){
//			
//			'cari nama saldo yang sesuai di list saldo'
//			def modifyNamaSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/API_KEY/Page_Balance/span_OCR KK'),
//				 'xpath', 'equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[1]/div/"+
//				 "lib-balance-summary/div/div["+ (i) +"]/div/div/div/div/div[1]/span", true)
//			
//			'tambahkan nama saldo ke array'
//			activeBalanceUI.add(WebUI.getText(modifyNamaSaldo))
//		}
//		
//		'jika hasil UI dan DB tidak sama'
//		if (!activeBalanceUI.containsAll(activeBalanceDB)){
//			
//			GlobalVariable.FlagFailed = 1
//			
//			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonBalanceUI'
//			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
//			GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
//			';') + GlobalVariable.FailedReasonBalanceUI)
//		}
//		
//		'check if mandatory complete dan button simpan clickable'
//		if ((isMandatoryComplete == 0) && GlobalVariable.FlagFailed == 0){
//			
//			'write to excel success'
//			CustomKeywords.'writeToExcel.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Saldo', 0,
//				GlobalVariable.NumOfColumn - 1, GlobalVariable.StatusSuccess)
//		}
//		else if (isMandatoryComplete > 0){
//			
//			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonMandatory'
//			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
//				GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
//				';') + GlobalVariable.FailedReasonMandatory)
//		}
//		
//		WebUI.refresh()
//		
//		'cek apakah muncul error unknown setelah refresh'
//		if (WebUI.verifyElementNotPresent(findTestObject('Object Repository/Profile/Page_Balance/div_Unknown Error'),
//			GlobalVariable.Timeout, FailureHandling.OPTIONAL) == false) {
//			
//			GlobalVariable.FlagFailed = 1
//			
//			'tulis adanya error pada sistem web'
//			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
//				GlobalVariable.StatusWarning, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) + ';') +
//					GlobalVariable.FailedReasonUnknown)
//		}
	}

'tutup browser'
WebUI.closeBrowser()

'fungsi untuk filter saldo berdasarkan input user'
def filterSaldo() {
	'driver chrome untuk pengalihan proses download'
	WebDriver driver = DriverFactory.getWebDriver()
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), 
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
	
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	
	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 10))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'), Keys.chord(Keys.ENTER))
	
	'isi tanggal transaksi awal'
	WebUI.setText(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Dari_transactionDateStart'),
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 11))
	
	'input pengguna dari transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Pengguna_user'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 12))
	
	'input hasil proses berdasarkan ddl di excel'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 13))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'), Keys.chord(Keys.ENTER))
	
	'input reference number transaksi'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Ref Number_referenceNo'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 14))
	
	'input nama dokumen'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/input_Nama Dokumen_documentName'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 15))
	
	'input batas tanggal transaksi terakhir'
	WebUI.setText(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Sampai_transactionDateEnd'), 
			findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 16))
	
	'input kantor'
	WebUI.setText(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 17))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'), Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), 
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
	
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
		GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
		';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	'klik pada tombol set ulang'
	WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Set Ulang'))
	
	'verify field tipe saldo ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/inputtipesaldo'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tipe transaksi ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/inputtipetransaksi'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tanggal transaksi awal ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Dari_transactionDateStart'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field pengguna ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Pengguna_user'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field hasil proses ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/inputhasilproses'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ref number ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Ref Number_referenceNo'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'',
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field nama dokumen ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Nama Dokumen_documentName'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field tanggal transaksi akhir ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/input_Tanggal Transaksi Sampai_transactionDateEnd'),
		'value', FailureHandling.CONTINUE_ON_FAILURE), '', 
			false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field kantor ter-reset'
	checkVerifyReset(WebUI.verifyMatch(WebUI.getAttribute(
		findTestObject('Object Repository/Saldo/Page_Balance/inputkantor'),
		'value', FailureHandling.CONTINUE_ON_FAILURE),'', 
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'isi field input tipe saldo'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 9))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipesaldo'), Keys.chord(Keys.ENTER))
	
	'isi field tipe transaksi'
	WebUI.setText(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), 
		findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 10))
	
	'pencet enter'
	WebUI.sendKeys(findTestObject('Object Repository/API_KEY/Page_Balance/inputtipetranc'), Keys.chord(Keys.ENTER))
	
	'klik pada button cari'
	WebUI.click(findTestObject('Object Repository/API_KEY/Page_Balance/button_Cari'))
	
	'jika hasil pencarian tidak memberikan hasil'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Saldo/Page_Balance/hasil search'), 
		GlobalVariable.Timeout, FailureHandling.OPTIONAL)){
	
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonSearchFailed)
	}
	
	'user menentukan apakah file yang didownload langsung dihapus atau tidak lewat excel'
	String downloadFile = findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 20)
	
	'user menentukan apakah file yang didownload langsung dihapus atau tidak lewat excel'
	String flagDelete = findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 21)
	
	'mengambil alamat dari project katalon ini'
	String userDir = System.getProperty('user.dir')
	
	'directory tempat file akan didownload'
	String filePath = userDir + '\\Download'
	
	if (downloadFile == 'Yes'){
		
		'klik pada tombol unduh excel'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/button_Unduh Excel'))
		
		WebUI.delay(GlobalVariable.Timeout)
		
		'pengecekan file yang sudah didownload'
		boolean isDownloaded = CustomKeywords.'documentationAPI.CheckDocumentation.isFileDownloaded'(flagDelete)
		
		'jika file tidak terdeteksi telah terdownload'
		if (!WebUI.verifyEqual(isDownloaded, true, FailureHandling.OPTIONAL)){
			
			GlobalVariable.FlagFailed = 1
			
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
				GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
					';') + GlobalVariable.FailedReasonDownloadProblem)
		}
	}
}

'fungsi langsung ke laman akhir'
def checkTableandPaging(Connection connection, String tenantcode, String tipeSaldo) {
	
	'ambil total data yang dicari dari DB'
	int resultTotalData = CustomKeywords.'saldo.VerifSaldo.getCountTotalData'(connection, tenantcode, tipeSaldo)
	
	'cek apakah total data di table dan db equal'
	Total = WebUI.getText(findTestObject('Object Repository/Saldo/Page_Balance/totalDataTable')).split(' ')
	
	'verify total data tenant'
	if(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE) == false){
		
		GlobalVariable.FlagFailed = 1
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.FailedReasonsearchFailed'
		CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumOfColumn,
			GlobalVariable.StatusFailed, (findTestData(excelPathSaldo).getValue(GlobalVariable.NumOfColumn, 2) +
				';') + GlobalVariable.FailedReasonVerifyEqualorMatch)
	}
	
	'cek apakah button enable atau disable'
	if(WebUI.verifyElementVisible(findTestObject('Object Repository/Saldo/Page_Balance/lastPage'), 
		FailureHandling.OPTIONAL)){
		
		'klik button page 2'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/page2'))
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Saldo/Page_Balance/page2'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button page 1'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/page1'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Saldo/Page_Balance/page1'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'cari button skip di footer'
		def elementbuttonskip = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout >'+
			' div > div.main-panel > div > div.content-wrapper > app-balance-prod > div.ng-star-inserted > app-msx-paging-v2 >'+
				' app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
		
		'ambil banyaknya laman footer'
		int lastPage = elementbuttonskip.size()
		
		'ubah path object button next page'
		def modifybuttonNextPage = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/'+
			'Page_Balance/modifybuttonpage'),'xpath','equals', 
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance-prod/div[3]/app-msx-paging-v2/"+
			"app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ (lastPage-1) +"]", true)

		'klik tombol next page'
		WebUI.click(modifybuttonNextPage)
		
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Saldo/Page_Balance/page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik button previous page'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/previousPage'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Saldo/Page_Balance/page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'klik button skip to last page'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/lastPage'))
		
		'ubah path object button laman terakhir'
		def modifybuttonMaxPage = WebUI.modifyObjectProperty(findTestObject('Object Repository/Saldo/'+
			'Page_Balance/modifybuttonpage'),'xpath','equals', "/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/"+
			"app-balance-prod/div[3]/app-msx-paging-v2/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/"+
			"datatable-pager/ul/li["+ (lastPage-2) +"]", true)
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifybuttonMaxPage, 
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'klik tombol kembali ke laman pertama'
		WebUI.click(findTestObject('Object Repository/Saldo/Page_Balance/firstPage'))
		
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(
			findTestObject('Object Repository/Saldo/Page_Balance/page1'),
			'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth, Connection conneSign) {
	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), '(?i)Vida', true)
	
   'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'),'Sign')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use Sign')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), '2023-07-13')

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/input_tipedokumen'), 'Dokumen Kontrak')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), 'TTDQEWHJULI05')

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), 'DOKUMEN DIKIRIM PER SATU CAPITAL')

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), '2023-07-13')

    'Klik set ulang'
    WebUI.click(findTestObject('Saldo/button_SetUlang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_fromdate'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
	'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_refnumber'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
	'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_namadokumen'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
	'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/input_todate'), 'value', FailureHandling.CONTINUE_ON_FAILURE),
	'', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'click ddl tipe saldo'
	WebUI.click(findTestObject('Saldo/input_tipesaldo'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))
	
	'click ddl tipe transaksi'
	WebUI.click(findTestObject('Saldo/input_tipetransaksi'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))
	
	'click ddl tipe dokumen'
	WebUI.click(findTestObject('Saldo/input_tipedokumen'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Saldo/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

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
	totalTrxDB = CustomKeywords.'connection.Saldo.getTotalTrxBasedOnVendorAndBalanceType'(conneSign, tenantCodeByUserLogin, 'VIDA', 'Sign')
	
	'verify total Meterai'
	checkVerifyPaging(WebUI.verifyMatch(totalTrxUI[0], totalTrxDB, false, FailureHandling.CONTINUE_ON_FAILURE))
	
	if (Integer.parseInt(totalTrxUI[0]) > 10) {
		'click next page'
		WebUI.click(findTestObject('Saldo/button_NextPage'))
	
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click prev page'
		WebUI.click(findTestObject('Saldo/button_PrevPage'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click last page'
		WebUI.click(findTestObject('Saldo/button_LastPage'))
	
		'verify paging di last page'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				WebUI.getAttribute(findTestObject('Saldo/page_Active'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace(
					'page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click first page'
		WebUI.click(findTestObject('Saldo/button_FirstPage'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Saldo/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE),
				'1', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Saldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

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
	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah DDL Saldo')
}