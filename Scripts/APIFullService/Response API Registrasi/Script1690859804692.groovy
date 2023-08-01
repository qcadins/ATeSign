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
int countColmExcel = findTestData(excelPathAPIRegistrasi).columnNumbers

String selfPhoto, idPhoto

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIRegistrasi, GlobalVariable.NumofColm, 35)
			
		'check ada value maka setting email service tenant'
		if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 34).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 34))
		}
		
		'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30)
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 28) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 28) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 29)
        }
        
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 32) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, 24))) + '"')
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 32) == 'No') {
            selfPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 24)
        }
		
		if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 33) == 'Yes') {
			idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
					GlobalVariable.NumofColm, 25))) + '"')
		} else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 33) == 'No') {
			idPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 25)
		}
        
		if(GlobalVariable.Psre == 'VIDA') {
			'declare variable array'
			String saldoBefore, saldoAfter
			
			int countCheckSaldo = 0
			
			WebUI.openBrowser('')
			
			saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)
			
			countCheckSaldo = 1
			
			println(saldoBefore)
		}
		
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Register', [('callerId') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 9), ('nama') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        11), ('email') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12), ('tmpLahir') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 13), ('tglLahir') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 14), ('jenisKelamin') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 15), ('tlp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        16), ('idKtp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 17), ('alamat') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 18), ('kecamatan') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 19), ('kelurahan') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        20), ('kota') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 21), ('provinsi') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 22), ('kodePos') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 23), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('password') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        26)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                email = WS.getElementPropertyValue(respon, 'email', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.APIFullService.checkAPIRegisterActive'(conneSign, findTestData(
                            excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12).replace('"', ''), findTestData(
                            excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 16).replace('"', ''))
					
                    String resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo.toString().replace('[', '').replace(']', ''))

                    ArrayList<String> resultDataUser = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, 
                        findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12).replace('"', ''))
					
                    'declare arraylist arraymatch'
                    arrayMatch = []

                    'verify is_active'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify is_registered'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx qty = -1'
                    arrayMatch.add(WebUI.verifyMatch(resultTrx, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

					'reset index kembali 0 untuk array selanjutnya'
					arrayIndex = 0
					
                    'verify full name'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 11).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tempat lahir'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'parse Date from MM/dd/yyyy > yyyy-MM-dd'
                    sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataUser[arrayIndex++], 'MM/dd/yyyy', 'yyyy-MM-dd')

                    'verify tanggal lahir'
                    arrayMatch.add(WebUI.verifyMatch(sDate.toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                            GlobalVariable.NumofColm, 14).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify jenis kelamin'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 15).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify provinsi'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kota'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kecamatan'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 19).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kelurahan'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kode pos'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 23).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
					
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB) 
						} else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Registrasi', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
					
					if(GlobalVariable.Psre == 'VIDA') {
						'kurang saldo before dengan proses verifikasi'
						saldoBefore = (Integer.parseInt(saldoBefore) - 1).toString()
						
						saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
						
						println(saldoAfter)
						
						'verify saldo before dan after'
						checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter), FailureHandling.CONTINUE_ON_FAILURE), ' Saldo Gagal Potong')
					}
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)

                if ((GlobalVariable.checkStoreDB == 'Yes') && (trxNo != null)) {
					String resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo.toString().replace('[', '').replace(']', ''))

                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

					if (GlobalVariable.Psre == 'VIDA') {
	                    'kurang saldo before dengan proses verifikasi'
						saldoBefore = (Integer.parseInt(saldoBefore) - 1).toString()
					
						saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
						
						'verify saldo before dan after'
						checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter), FailureHandling.CONTINUE_ON_FAILURE), ' Saldo Gagal Potong')
					} else if (GlobalVariable.Psre == 'PRIVY') {
						'verify saldo privy'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(resultTrx, '0', false, FailureHandling.CONTINUE_ON_FAILURE), ' Gaga Verifikasi Saldo Terpotong - Privy')
					}

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
	String saldo

	'get current date'
	currentDate = new Date().format('yyyy-MM-dd')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))

	'maximize window'
	WebUI.maximizeWindow()

	'set value userLogin'
	GlobalVariable.userLogin = findTestData(excelPathAPIRegistrasi).getValue(2, 36).toUpperCase()

	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathAPIRegistrasi).getValue(2, 36))

	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathAPIRegistrasi).getValue(2,
			37))

	'click button login'
	WebUI.click(findTestObject('Login/button_Login'), FailureHandling.CONTINUE_ON_FAILURE)

	'input perusahaan'
	WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathAPIRegistrasi).getValue(2,
			38))

	'enter untuk select perusahaan'
	WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

	'input peran'
	WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathAPIRegistrasi).getValue(2,
			39))

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
	WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + GlobalVariable.Psre, true)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	for (index = 2; index <= variable.size(); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo di excel'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Verification')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'get qty saldo before'
			saldo = WebUI.getText(modifyObjectQty).replace(',', '')

			break
		}
	}

	if ((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0)) {
		'input tipe saldo'
		WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), 'Verification')
	
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
		ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathAPIRegistrasi).getValue(
				GlobalVariable.NumofColm, 12).replace('"', ''), findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 16).replace('"', ''),
			'Use Verification')

		arrayIndex = 0

		'verify no trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Trx')

		'verify tgl trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
					'.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

		'verify tipe trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false,
				FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

		'verify user trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

		'verify note trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes')

		'verify qty trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''),
				false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx')
	}
	return saldo
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 2) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}