import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection

import org.openqa.selenium.By
import org.openqa.selenium.Keys

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIGenerateInvLink).columnNumbers

String selfPhoto, idPhoto

'looping API Generate Invitation Link'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGenerateInvLink, GlobalVariable.NumofColm, 53)
		
		'declare variable array'
		ArrayList<String> saldoBefore, saldoAfter
		
		int countCheckSaldo = 0
		
		WebUI.openBrowser('')
		
		saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)
		
		countCheckSaldo = 1
		
		GlobalVariable.FlagFailed = 0
		
		'check ada value maka setting email service tenant'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 52).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 52))
		}
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 34) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 35)
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 34) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 32) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 32) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 33)
        }
        
		'check if self photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 36) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, 24))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 36) == 'No') {
            selfPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 24)
        }
        
		'check if id photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 37) == 'Yes') {
            idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, 25))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 37) == 'No') {
            idPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 25)
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 11), ('email') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 12), ('tmpLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 13), ('tglLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 14), ('jenisKelamin') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 15), ('tlp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        16), ('idKtp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 17)
                    , ('alamat') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 18), ('kecamatan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 19), ('kelurahan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 20), ('kota') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 21), ('provinsi') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 22), ('kodePos') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 23), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 26), ('type') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, 27), ('office') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        28), ('businessLine') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        29), ('taskNo') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 30)
                    , ('callerId') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 9)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('APIFullService/APIFullService - VIDA/API Generate Invitation Link/APIGenInvLinkStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'], 
						FailureHandling.CONTINUE_ON_FAILURE)
                }
				
				'call test case daftar akun verif'
				WebUI.callTestCase(findTestCase('APIFullService/APIFullService - VIDA/API Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathGenerateLink') : 'APIFullService/API_GenInvLink', ('otpBefore') : saldoBefore[0]], 
						FailureHandling.CONTINUE_ON_FAILURE)
					
				if (GlobalVariable.FlagFailed == 0) {
					'kurang saldo before dengan proses verifikasi'
				    saldoBefore.set(1, (Integer.parseInt(saldoBefore[1]) - 1).toString())
				
					if (GlobalVariable.Psre == 'VIDA') {
						'kurang saldo before dengan prose PNBP'
						saldoBefore.set(2, (Integer.parseInt(saldoBefore[2]) - 1).toString())
												
						'kurang saldo before dengan jumlah counter send OTP'
						saldoBefore.set(0, (Integer.parseInt(saldoBefore[0]) - GlobalVariable.Counter).toString())
					} else {
						'kurang saldo before dengan jumlah counter send OTP'
						saldoBefore.set(0, (Integer.parseInt(saldoBefore[0]) - GlobalVariable.Counter).toString())
					}
					
				    saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
				
				    'verify saldoafter tidak sama dengan saldo before'
				    checkVerifyEqualOrMatch(WebUI.verifyMatch(saldoAfter.toString(), saldoBefore.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Saldo')
					
					'print untuk menunjukan saldobefore dan saldoafter'
					println('saldoBefore : ' + saldoBefore.toString())
					println('saldoAfter : ' + saldoAfter.toString())
				}
            } else {
               'call function get API error message'
			   getAPIErrorMessage(respon)
            }
        } else {
			'call function get API error message'
			getAPIErrorMessage(respon)
        }
    }
}

def getAPIErrorMessage(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
	ArrayList<String> saldo = []
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))

	'maximize window'
	WebUI.maximizeWindow()

	'set value userLogin'
	GlobalVariable.userLogin = findTestData(excelPathAPIGenerateInvLink).getValue(2, 55).toUpperCase()

	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathAPIGenerateInvLink).getValue(2, 55))

	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathAPIGenerateInvLink).getValue(2,
			56))

	'click button login'
	WebUI.click(findTestObject('Login/button_Login'), FailureHandling.CONTINUE_ON_FAILURE)

	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathAPIGenerateInvLink).getValue(2,
			57))

	'enter untuk select perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathAPIGenerateInvLink).getValue(2,
			58))

	'enter untuk select peran'
	WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.CONTINUE_ON_FAILURE)

	'check if button menu visible atau tidak'
	if(WebUI.verifyElementNotVisible(findTestObject('BuatUndangan/checkSaldo/menu_Saldo'), FailureHandling.OPTIONAL)) {
		'click menu saldo'
		WebUI.click(findTestObject('button_HamburberSideMenu'))
	}
	
	'click menu saldo'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/menu_Saldo'))

	'click ddl bahasa'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_bahasa'))

	'click english'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_English'))
	
	'select vendor'
	WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + 'ESIGN/ADINS', true)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	for (index = 2; index <= variable.size(); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo di excel'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('OTP')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'get qty saldo before'
			saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

			break
		}
	}

	if ((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0) && GlobalVariable.Psre == 'VIDA') {
		'call function input filter saldo'
		inputFilterSaldo('OTP', conneSign)
		
//		'call function verify list undangan'
//		verifyListUndangan()
	}

	'select vendor'
	WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + GlobalVariable.Psre, true)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	for (index = 2; index <= variable.size(); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo di excel'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Verification') || (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('PNBP') && GlobalVariable.Psre == 'VIDA')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'get qty saldo before'
			saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

			'if saldo sudah terisi 2 verification dan pnbp'
			if(saldo.size() == 3 && GlobalVariable.Psre == 'VIDA') {
				break
			} else if(saldo.size() == 2 && GlobalVariable.Psre == 'PRIVY') {
				break
			}
			
			continue
		}
	}
	
	if ((countCheckSaldo == 1)) {
		'call function input filter saldo'
		inputFilterSaldo('Verification', conneSign)
		
		if(GlobalVariable.FlagFailed == 0 && GlobalVariable.Psre == 'VIDA') {
			'call function input filter saldo'
			inputFilterSaldo('PNBP', conneSign)
		}
	}
	return saldo
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 2) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def inputFilterSaldo(String tipeSaldo, Connection conneSign) {
	'get current date'
	currentDate = new Date().format('yyyy-MM-dd')
	
	'input tipe saldo'
	WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), tipeSaldo)

	'enter untuk input tipe saldo'
	WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

	'input tanggal Transaksi'
	WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TanggalTransaksi'), currentDate)

	'click button cari'
	WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_Cari'))

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	'modify object button last page'
	modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		variable.size()) + ']', true)

	if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
		'click button last page'
		WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_LastPage'))
	}
	
	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

	'modify object no transaksi'
	modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

	'modify object tanggal transaksi'
	modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'),
		'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

	'modify object tipe transaksi'
	modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

	'modify object user'
	modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

	'modify object no kontrak'
	modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

	'modify object Catatan'
	modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

	'modify object qty'
	modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

	'get trx dari db'
	ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
			GlobalVariable.NumofColm, 12).replace('"', ''), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 16).replace('"', ''),
		'Use ' + tipeSaldo)

	arrayIndex = 0

	'verify no trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Trx ' + tipeSaldo)

	'verify tgl trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
				'.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx ' + tipeSaldo)

	'verify tipe trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false,
			FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx ' + tipeSaldo)

	'verify user trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' User ' + tipeSaldo)

	'verify note trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes ' + tipeSaldo)

	'verify qty trx ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''),
			false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx ' + tipeSaldo)
}