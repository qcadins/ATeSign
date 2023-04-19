import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

GlobalVariable.FlagFailed = 0

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathIsiSaldo).getColumnNumbers()

int countCheckSaldo

'declare variable array'
ArrayList<String> saldoBefore, saldoAfter

'looping isi saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {

		'counter check saldo'
		countCheckSaldo = 0
		
		'call function login admin get saldo'
		saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)
		
		'counter after check saldo'
		countCheckSaldo = 1
		
        'call test case login admin esign'
        WebUI.callTestCase(findTestCase('Login/Login_AdminEsign'), [:], FailureHandling.STOP_ON_FAILURE)

        'click menu isi saldo'
        WebUI.click(findTestObject('isiSaldo/menu_isiSaldo'))

        'get ddl tenant'
        ArrayList<String> resultTenant = CustomKeywords.'connection.dataVerif.getDDLTenant'(conneSign)

		'call function check ddl untuk tenant'
		checkDDL(findTestObject('isiSaldo/input_PilihTenant'), resultTenant)

        'input tenant'
        WebUI.setText(findTestObject('isiSaldo/input_PilihTenant'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                14))

        'enter untuk input tenant'
        WebUI.sendKeys(findTestObject('isiSaldo/input_PilihTenant'), Keys.chord(Keys.ENTER))

        'get ddl vendor'
        ArrayList<String> resultVendor = CustomKeywords.'connection.dataVerif.getDDLVendor'(conneSign, findTestData(excelPathIsiSaldo).getValue(
                GlobalVariable.NumofColm, 14))

		'call function check ddl untuk vendor'
		checkDDL(findTestObject('isiSaldo/input_PilihVendor'), resultVendor)
		
        'input vendor'
        WebUI.setText(findTestObject('isiSaldo/input_PilihVendor'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                15))

        'enter untuk input vendor'
        WebUI.sendKeys(findTestObject('isiSaldo/input_PilihVendor'), Keys.chord(Keys.ENTER))

        'get ddl tipe saldo'
        ArrayList<String> resultTipeSaldo = CustomKeywords.'connection.dataVerif.getDDLTipeSaldo'(conneSign, findTestData(
                excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 14), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                15))

		'call function check ddl untuk vendor'
		checkDDL(findTestObject('isiSaldo/input_TipeSaldo'), resultTipeSaldo)

        'input tipe saldo'
        WebUI.setText(findTestObject('isiSaldo/input_TipeSaldo'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                16))

        'enter untuk input tipe saldo'
        WebUI.sendKeys(findTestObject('isiSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

        'input tambah saldo'
        WebUI.setText(findTestObject('isiSaldo/input_TambahSaldo'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                17))

		'tambah saldo before dengan jumlah isi saldo'
		saldoBefore.set(0, (Integer.parseInt(saldoBefore[0])+Integer.parseInt(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm,
				17))).toString())
		
		saldoBefore.set(1, (Integer.parseInt(saldoBefore[1])+Integer.parseInt(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm,
			17))).toString())
		
        'input nomor tagihan'
        WebUI.setText(findTestObject('isiSaldo/input_nomorTagihan'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                18))

        'input catatan'
        WebUI.setText(findTestObject('isiSaldo/input_Catatan'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                19))

        'input tanggal pembelian'
        WebUI.setText(findTestObject('isiSaldo/input_TanggalPembelian'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                20))

        'click field untuk refresh button lanjut agar bisa di click'
        WebUI.click(findTestObject('isiSaldo/input_Catatan'))

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 4))

        'check mandatory excel = 0'
        if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('isiSaldo/button_Lanjut'), 'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
            'click lanjut'
            WebUI.click(findTestObject('isiSaldo/button_Lanjut'))

            'click ya proses'
            WebUI.click(findTestObject('isiSaldo/button_YaProses'))

            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'isiSaldo', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case store db'
                WebUI.callTestCase(findTestCase('IsiSaldo/IsiSaldoStoreDB'), [('excelPathIsiSaldo') : 'Saldo/isiSaldo'], 
                    FailureHandling.STOP_ON_FAILURE)
            }
			'close browser'
			WebUI.closeBrowser()
			
			'call function login admin get saldo'
			saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
			
			'verify saldoafter tidak sama dengan saldo before'
			checkVerifyEqualOrMatch(saldoAfter.equals(saldoBefore))
			
			println(saldoBefore)
			println(saldoAfter)
			
        } else if (isMandatoryComplete > 0) {
            'click batal'
            WebUI.click(findTestObject('isiSaldo/button_Batal'))

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('isiSaldo', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + 
                GlobalVariable.ReasonFailedMandatory)
			
			'close browser'
			WebUI.closeBrowser()
        }
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('isiSaldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB) {
	
	'declare array untuk menampung ddl'
	ArrayList<String> list = new ArrayList<String>()
	
	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)

	'get id ddl'
	id = WebUI.getAttribute(findTestObject('isiSaldo/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

	'get row'
	variable = DriverFactory.getWebDriver().findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

	'looping untuk get ddl kedalam array'
	for (i = 1; i < variable.size(); i++) {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('isiSaldo/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
			id) + '-') + i) + '\']', true)

		'add ddl ke array'
		list.add(WebUI.getText(modifyObjectDDL))
	}
	
	'verify ddl ui = db'
	checkVerifyEqualOrMatch(listDB.containsAll(list))

	'verify jumlah ddl ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE))
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
	
	ArrayList<String> saldo = new ArrayList<>()
	
	'open browser'
	WebUI.openBrowser('')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))
	
	'maximize window'
	WebUI.maximizeWindow()
	
	'set value userLogin'
	GlobalVariable.userLogin = findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 9).toUpperCase()
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 9))
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 10))
	
	'click button login'
	WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)
	
	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 11))
	
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
	
	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 12))
	
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
	'click menu saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
	
	'click ddl bahasa'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/button_bahasa'))
	
	'click english'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/button_English'))
	
	'select vendor'
	WebUI.selectOptionByLabel(findTestObject('isiSaldo/SaldoAdmin/select_Vendor'), '(?i)' + findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 15), true)
		
	'get row'
	variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))
	
	for(index = 2; index <= variable.size(); index++) {
		
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div["+ index +"]/div/div/div/div/div[1]/h3",true)
	
		'check if box info = tipe saldo di excel'
		if(WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 16))) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
				"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div["+ index +"]/div/div/div/div/div[2]/h3",true)
			
			'get qty saldo before'
			saldo.add(WebUI.getText(modifyObjectQty))
			
			break
		}
	}
	
	'input tipe saldo'
	WebUI.setText(findTestObject('isiSaldo/SaldoAdmin/input_TipeSaldo'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 16))
	
	'enter untuk input tipe saldo'
	WebUI.sendKeys(findTestObject('isiSaldo/SaldoAdmin/input_TipeSaldo'), Keys.chord(Keys.ENTER))
	
	if(countCheckSaldo == 1) {
		'input no kontrak'
		WebUI.setText(findTestObject('isiSaldo/SaldoAdmin/input_NoKontrak'), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18))
	}
	
	'click button cari'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/button_Cari'))
	
	'get row'
	variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
	
	'modify object button last page'
	modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
		"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li["+ variable.size() +"]",true)
	
	if(WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
		'click button last page'
		WebUI.click(findTestObject('isiSaldo/SaldoAdmin/button_LastPage'))
	}
	
	
	'get row'
	variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
	
	'modify object balance'
	modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
		"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[10]/div",true)
	
	'get trx saldo'
	saldo.add(WebUI.getText(modifyObjectBalance))
	
	if(countCheckSaldo == 1) {
		'modify object no transaksi'
		modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[1]/div",true)
		
		'modify object tanggal transaksi'
		modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[2]/div",true)
		
		'modify object tipe transaksi'
		modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[3]/div",true)
		
		'modify object user'
		modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[4]/div",true)
		
		'modify object no kontrak'
		modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[5]/div",true)
		
		'modify object Catatan'
		modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[8]/div",true)
		
		'modify object qty'
		modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('isiSaldo/SaldoAdmin/modifyObject'),'xpath','equals',
			"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper["+ variable.size() +"]/datatable-body-row/div[2]/datatable-body-cell[9]/div",true)
		
		'get trx dari db'
		ArrayList<String> result = CustomKeywords.'connection.dataVerif.getIsiSaldoTrx'(conneSign, findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                18))
		
		arrayIndex = 0
		
		'verify no trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify tgl trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), result[arrayIndex++].replace('.0',''), false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify tipe trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify user trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify no kontrak ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoKontrak), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify note trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify qty trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
		
	}
	
	'close browser'
	WebUI.closeBrowser()
	
	return saldo
}