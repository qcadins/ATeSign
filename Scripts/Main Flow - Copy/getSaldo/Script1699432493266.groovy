import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'inisialisasi result'
HashMap result = [:]

'jika vendornya ada teks digi ataupun DIGI'
if (vendor.toString().contains('digi') || vendor.toString().contains('DIGI')) {
	'vendornya digisign'
	vendor = 'DIGISIGN'
} else if (vendor.toString().contains('TKNAJ')) {
	'jika vendornya TKNAJ, maka tekenaja'
	vendor = 'TEKENAJA'
}

funcLogin()

if (usageSaldo == 'Send') {
	funcSaldoSend(result)
}

if (usageSaldo == 'Sign') {
	funcSaldoSign(result)
}

if (usageSaldo == 'Stamp') {
	funcSaldoStamp(result)
}

if (usageSaldo == 'Register') {
	funcSaldoRegis(result, countCheckSaldo, conneSign)
}

result

def rowExcel(String cellValue) {
	CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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
			WebUI.callTestCase(findTestCase('Main Flow - Copy/Login'), [('excel') : excel, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
		}
	} else {
		WebUI.refresh()
	}
}

def funcSaldoSend(HashMap result) {
	'string vendorVerifikasi, saldo list'
	String vendorVerifikasi
	
	ArrayList saldoList = []

	'vendor yang telah diinput akan masuk sebagai open dan get saldo menggunakan vendor verifikasi'
	vendorVerifikasi = vendor
	
	'jika digisign, maka saldo list yang akan diambil adalah dokumen, selainnya adalah TTD'
	if (vendorVerifikasi.equalsIgnoreCase('DIGISIGN')) {
		saldoList = ['Dokumen']
	} else {
		saldoList = ['TTD']
	}
	
	'untuk autosign akan dihidupkan agar saldo document/ttd akan disimpan pada hashmap dalam nama vendor'
	forAutosign = true
	
	'get menggunakan func find saldo'
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	'vendor yang akan dibuka yaitu ESIGN/ADINS untuk mengambil SMS dan WA'
	vendorVerifikasi = 'ESIGN/ADINS'
	
	saldoList = ['SMS Notif', 'WhatsApp Message']
	
	'forautosign akan dimatikan'
	forAutosign = false
	
	'get saldo menggunakan func find saldo'
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
	
	result
}

def funcSaldoSign(HashMap result) {
	'inisialisasi vendor verifikasi dan saldo list beserta list-list yang akan diambil'
	String vendorVerifikasi
	
	ArrayList saldoList = []
	
	'untuk sign, yang akan diambil adalah meterai, sms, dan wa'
	saldoList = ['Meterai', 'SMS Notif', 'WhatsApp Message']
	
	'vendor verifikasi akan membuka ESIGN/ADINS'
	vendorVerifikasi = 'ESIGN/ADINS'
	
	'get saldo menggunakan func find saldo'
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
	
	'jika vendornya bukan privy dan bukan digisng'
	if (!vendor.equalsIgnoreCase('Privy') && !vendor.equalsIgnoreCase('DIGISIGN')) {
		'list data saldo yang perlu diambil'
		saldoList = ['Liveness', 'Face Compare', 'Liveness Face Compare', 'OTP']
		
		'vendor verifikasi open ESIGN ADINS lagi'
		vendorVerifikasi = 'ESIGN/ADINS'
	} else if (vendor.equalsIgnoreCase('Privy')) {
		'vendornya akan open Privy dan get OTP'
		vendorVerifikasi = vendor
		
		'list data saldo yang perlu diambil'
		saldoList = ['OTP']
	} else if (vendor.equalsIgnoreCase('DIGISIGN')) {
		'vendor akan open DIGISIGN dan get OTP'
		vendorVerifikasi = vendor
		
		saldoList = ['OTP']
	}
	
	'get saldo menggunakan func find saldo'
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	'vendor tetap pada vendornya'
	vendorVerifikasi = vendor
	
	'jika digisign, maka get Dokumen, jika yang lain, maka TTD'
	if (vendorVerifikasi.equalsIgnoreCase('DIGISIGN')) {
		saldoList = ['Dokumen']
	} else {
		saldoList = ['TTD']
	}
	
	'for autosign akan true'
	forAutosign = true
	
	'get saldo'
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	result
}

def funcSaldoStamp(HashMap result) {
	'inisialisasi vendor verifikasi dengan mengambil value meterai di ESIGN/ADINS'
	String vendorVerifikasi
	
	ArrayList saldoList = []
	vendorVerifikasi = 'ESIGN/ADINS'
	
	saldoList = ['Meterai']
	
	funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)

	result
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
					result.put(saldoList[i], WebUI.getText(modifyObjecttotalSaldoSign).replace(',', ''))
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
		
		saldoList = ['WhatsApp Message', 'OTP', 'SMS Notif']
		
		funcFindSaldo(result, vendorVerifikasi, saldoList, forAutosign)
		
//		if (((countCheckSaldo == 1) && ((GlobalVariable.FlagFailed == 0) || findTestData(excel).getValue(GlobalVariable.NumofColm,
//			rowExcel('Status')).equalsIgnoreCase('Warning'))) && (GlobalVariable.Psre == 'VIDA')) {
//			'call function input filter saldo'
//			inputFilterSaldo(useSaldo, conneSign)
//			
//			if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) ==
//				'1' || findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) ==
//				'1') && findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <= 2) {
//				inputFilterSaldo('SMS', conneSign)
//			}
//		}
	}
	
	saldoList = ['Verifikasi', 'PNBP']
	
	funcFindSaldo(result, vendor, saldoList, forAutosign)

//	if (countCheckSaldo == 1) {
//		'call function input filter saldo'
//		inputFilterSaldo('Verification', conneSign)
//	
//		if ((GlobalVariable.FlagFailed == 0) && (GlobalVariable.Psre == 'VIDA')) {
//			'call function input filter saldo'
//			inputFilterSaldo('PNBP', conneSign)
//		}
//		
//		if ((GlobalVariable.FlagFailed == 0) && (GlobalVariable.Psre == 'DIGI')) {
//			'call function input filter saldo'
//			inputFilterSaldo('Text Verification', conneSign)
//		}
//	}
	
	result
}

