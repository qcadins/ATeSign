import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

HashMap<String, String> result = new HashMap<String, String>()

if (vendor.toString().contains('digi') || vendor.toString().contains('DIGI')) {
	vendor = 'DIGISIGN'
} else if (vendor.toString().contains('TKNAJ')) {
	vendor = 'TEKENAJA'
}

'return total saldo awal'
String totalSaldoOTP, vendorVerifikasi, totalSaldo

funcLogin()
if (usageSaldo == 'Send') {
	funcSaldoSend(result)
} else if (usageSaldo == 'Sign') {
	funcSaldoSign(result)
} else if (usageSaldo == 'Stamp') {
	funcSaldoStamp(result)
} else if (usageSaldo == 'Register') {
	funcSaldoRegis(result, countCheckSaldo, conneSign)
}

return result

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def funcLogin() {
	if (!(WebUI.verifyElementPresent(findTestObject('Saldo/ddl_Vendor'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
		if (WebUI.verifyElementPresent(findTestObject('Saldo/menu_Saldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'cek apakah elemen menu ditutup'
			if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
				'klik pada button hamburber'
				WebUI.click(findTestObject('button_HamburberSideMenu'))
			}
			
			'klik button saldo'
			WebUI.click(findTestObject('Saldo/menu_Saldo'))
	
			'cek apakah tombol x terlihat'
			if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
				'klik pada button X'
				WebUI.click(findTestObject('buttonX_sideMenu'))
			}
		} else {
			'Call test Case untuk login sebagai admin wom admin client'
			WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excel, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
		}
	} else {
		WebUI.refresh()
	}
}

def funcSaldoSend(HashMap result) {
	String vendorVerifikasi
	
	ArrayList saldoList = []

	vendorVerifikasi = vendor
	
	if (vendorVerifikasi.equalsIgnoreCase('DIGISIGN')) {
		saldoList = ['Dokumen']
	} else {
		saldoList = ['TTD']
	}
	
	forAutosign = true
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	vendorVerifikasi = 'ESIGN/ADINS'
	
	saldoList = ['SMS Notif', 'WhatsApp Message']
	
	forAutosign = false
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
	
	WebUI.comment(result.toString())
	return result
}

def funcSaldoSign(HashMap result) {
	String vendorVerifikasi
	
	ArrayList saldoList = []
	
	saldoList = ['Meterai', 'SMS Notif', 'WhatsApp Message']
	
	vendorVerifikasi = 'ESIGN/ADINS'
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
	
	if (!vendor.equalsIgnoreCase('Privy') && !vendor.equalsIgnoreCase('DIGISIGN')) {
		'list data saldo yang perlu diambil'
		saldoList = ['Liveness', 'Face Compare', 'Liveness Face Compare', 'OTP']
		
		vendorVerifikasi = 'ESIGN/ADINS'
	} else if (vendor.equalsIgnoreCase('Privy')) {
		vendorVerifikasi = vendor
		
		'list data saldo yang perlu diambil'
		saldoList = ['OTP']
	} else if (vendor.equalsIgnoreCase('DIGISIGN')) {
		vendorVerifikasi = vendor
		
		saldoList = ['OTP']
	}
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	vendorVerifikasi = vendor
	
	if (vendorVerifikasi.equalsIgnoreCase('DIGISIGN')) {
		saldoList = ['Dokumen']
	} else {
		saldoList = ['TTD']
	}
	
	forAutosign = true
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	return result
}

def funcSaldoStamp(HashMap result) {
	String vendorVerifikasi
	
	ArrayList saldoList = []
	vendorVerifikasi = 'ESIGN/ADINS'
	
	saldoList = ['Meterai']
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	return result
}

def funcFindSaldo(HashMap result, String vendorVerifikasi, ArrayList saldoList, boolean forAutosign) {
	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), '(?i)' + vendorVerifikasi, true)
	
	'get total div di Saldo'
	variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))
	
	for (int i = 0; i < saldoList.size(); i++) {
		'looping berdasarkan total div yang ada di saldo'
		for (int c = 2; c <= variableDivSaldo.size(); c++) {
			'modify object mengenai find tipe saldo'
			modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' +
				c) + ']/div/div/div/div/div[1]', true)
	
			'verifikasi label saldonya '
			if (WebUI.verifyElementText(modifyObjectFindSaldoSign, saldoList[i], FailureHandling.OPTIONAL)) {
				'modify object mengenai ambil total jumlah saldo'
				modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals',
					('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + c) + ']/div/div/div/div/div[2]',
					true)
				
				if (forAutosign == true) {
					result.put(vendor, WebUI.getText(modifyObjecttotalSaldoSign).replace(',', ''))
				} else {
					result.put(saldoList[i], WebUI.getText(modifyObjecttotalSaldoSign).replace(',',''))
				}
			}
		}
	}
}

def funcSaldoRegis(HashMap result, int countCheckSaldo, Connection conneSign) {
	String vendorVerifikasi
	
	ArrayList saldoList = []
	
	if (!findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
		'API Register')) {
	
		vendorVerifikasi = 'ESIGN/ADINS'
		
		'check jika Must use WA message = 1'
		if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')) == '1')) {
			useSaldo = 'WhatsApp Message'
			
			saldoList = [useSaldo,'OTP']
		} else {
			'check jika email service on'
			if (findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == '1') {
				'check jika use WA message = 1'
				if((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')) == '1')) {
					useSaldo = 'WhatsApp Message'
			
					saldoList = [useSaldo,'OTP']
				} else {
					'jika use WA message bukan 1 maka use OTP'
					useSaldo = 'OTP'
			
					saldoList = [useSaldo]
				}
			} else {
				'jika email service 0'
				useSaldo = 'OTP'
		
				saldoList = [useSaldo]
			}
		}
			
		if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) ==
				'1' || findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) ==
				'1') && findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <= 2) {
			saldoList.add('SMS Notif')
		}
		
		funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
		
		if (((countCheckSaldo == 1) && ((GlobalVariable.FlagFailed == 0) || findTestData(excel).getValue(GlobalVariable.NumofColm,
			rowExcel('Status')).equalsIgnoreCase('Warning'))) && (GlobalVariable.Psre == 'VIDA')) {
			'call function input filter saldo'
			inputFilterSaldo(useSaldo, conneSign)
			
			if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) ==
				'1' || findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) ==
				'1') && findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <= 2) {
				inputFilterSaldo('SMS', conneSign)
			}
		}
	}
	
	if (vendor == 'VIDA') {
		saldoList = ['Verifikasi','PNBP']
	} else if (vendor == 'PRIVY' || vendor == 'TEKENAJA') {
		saldoList = ['Verifikasi']
	}
	
	funcFindSaldo(result, vendor, saldoList, forAutosign)

	if (countCheckSaldo == 1) {
		'call function input filter saldo'
		inputFilterSaldo('Verification', conneSign)
	
		if ((GlobalVariable.FlagFailed == 0) && (GlobalVariable.Psre == 'VIDA')) {
			'call function input filter saldo'
			inputFilterSaldo('PNBP', conneSign)
		}
		
		if ((GlobalVariable.FlagFailed == 0) && (GlobalVariable.Psre == 'DIGI')) {
			'call function input filter saldo'
			inputFilterSaldo('Text Verification', conneSign)
		}
	}
	
	return result
}

def inputFilterSaldo(String tipeSaldo, Connection conneSign) {
	'get current date'
	currentDate = new Date().format('yyyy-MM-dd')

	'input tipe saldo'
	WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), tipeSaldo)

	'enter untuk input tipe saldo'
	WebUI.sendKeys(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

	'input tanggal Transaksi'
	WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TanggalTransaksi'), currentDate)

	'click button cari'
	WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_Cari'))

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	'modify object button last page'
	modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		variable.size()) + ']', true)

	if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
		'click button last page'
		WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_LastPage'))
	}
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

	'modify object no transaksi'
	modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

	'modify object tanggal transaksi'
	modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

	'modify object tipe transaksi'
	modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

	'modify object user'
	modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

	'modify object no kontrak'
	modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

	'modify object Catatan'
	modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

	'modify object qty'
	modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

	'check if email kosong atau tidak'
	if (findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase('Email')) {
		'get email dari row edit'
		email = findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).replace('"', '')
	} else if (findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2 &&
		!findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit')) {
		'get email dari row input'
		email = findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '')
	} else {
		'get name + email hosting'
		email = findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"', '') + CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign)
	}
	
	'get trx dari db'
	ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, email, findTestData(excel).getValue(
			GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''), 'Use ' + tipeSaldo)

	arrayIndex = 0

	'verify no trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
		' No Trx ' + tipeSaldo)

	'verify tgl trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), result[arrayIndex++], false,
			FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx ' + tipeSaldo)

	'verify tipe trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
		' Tipe Trx ' + tipeSaldo)

	'verify user trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
		' User ' + tipeSaldo)

	Note = WebUI.getText(modifyObjectCatatan)
	
	'verify note trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(Note, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
		' Notes ' + tipeSaldo)

	if(tipeSaldo == 'SMS' && Note.toLowerCase().contains('error')) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + '; <') +
			Note + '>'))

		GlobalVariable.FlagFailed = 1
	}
	
	'verify qty trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).toString().replace(
				'-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx ' + tipeSaldo)
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

		GlobalVariable.FlagFailed = 1
	}
}
