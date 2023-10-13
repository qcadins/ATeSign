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
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		GlobalVariable.VerificationCount = 1
		
		'setting psre per case'
		GlobalVariable.Psre = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGenerateInvLink, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))
		
		'declare variable array'
		ArrayList<String> saldoBefore, saldoAfter
		
		int countCheckSaldo = 0
		
		saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)
		
		countCheckSaldo = 1
		
		GlobalVariable.FlagFailed = 0
		
		'check ada value maka setting email service tenant'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')))
		}
		
		'check ada value maka setting email certif notif'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')).length() > 0) {
			'setting email email certif notif'
			CustomKeywords.'connection.Registrasi.settingSendCertNotifbySMS'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')))
		}
		
		'check ada value maka setting allow regenerate link'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')).length() > 0) {
			'setting allow regenerate link'
			CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))
		}
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
		'check if self photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'No') {
            selfPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
        }
        
		'check if id photo mau menggunakan base64 yang salah atau benar'
        if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'Yes') {
            idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
                    GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
        } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'No') {
            idPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')), ('email') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')), ('tmpLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('tmpLahir')), ('tglLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('tglLahir')), ('jenisKelamin') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('jenisKelamin')), ('tlp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('tlp')), ('idKtp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp'))
                    , ('alamat') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('alamat')), ('kecamatan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')), ('kelurahan') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')), ('kota') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('kota')), ('provinsi') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('provinsi')), ('kodePos') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('kodePos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
                        excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('region')), ('type') : findTestData(excelPathAPIGenerateInvLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('type')), ('office') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('office')), ('businessLine') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('businessLine')), ('taskNo') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('taskNo'))
                    , ('callerId') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/APIGenInvLinkStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'], 
						FailureHandling.CONTINUE_ON_FAILURE)
                }
				
				'check ada value maka setting Link Is Active'
				if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('is_active Link')).length() > 0) {
					'setting Link Is Active'
					CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('is_active Link')), 
						findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))
					
					'HIT API'
					respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(
									excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')), ('email') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('email')), ('tmpLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('tmpLahir')), ('tglLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('tglLahir')), ('jenisKelamin') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('jenisKelamin')), ('tlp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
									rowExcel('tlp')), ('idKtp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp'))
								, ('alamat') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('alamat')), ('kecamatan') : findTestData(
									excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')), ('kelurahan') : findTestData(
									excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')), ('kota') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('kota')), ('provinsi') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('provinsi')), ('kodePos') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('kodePos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
									excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('region')), ('type') : findTestData(excelPathAPIGenerateInvLink).getValue(
									GlobalVariable.NumofColm, rowExcel('type')), ('office') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
									rowExcel('office')), ('businessLine') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
									rowExcel('businessLine')), ('taskNo') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('taskNo'))
								, ('callerId') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))
					
					'Jika status HIT API 200 OK'
					if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
						'get status code'
						code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
						
						if (code == 0) {
							'mengambil response'
							GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)
							
							if(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('is_active Link')) == '0') {
								'write to excel failed'
								CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
									GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
										'-', '') + ';') + ' Link tergenerate walupun sudah tidak active')
							}
						} else {
			               'call function get API error message'
						   getAPIErrorMessage(respon)
						   
						   continue
			            }
					} else {
		               'call function get API error message'
					   getAPIErrorMessage(respon)

					   continue
		            }
				}
				
				'call test case daftar akun verif'
				WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathAPIGenerateInvLink') : 'APIFullService/API_GenInvLink', ('otpBefore') : saldoBefore[0]], 
						FailureHandling.CONTINUE_ON_FAILURE)
					
				'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form registrasi'
				while (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase('Continue')) {
					GlobalVariable.NumofColm++
					
					GlobalVariable.FlagFailed = 0
					
					'call test case daftar akun verif'
					WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathAPIGenerateInvLink') : 'APIFullService/API_GenInvLink', ('otpBefore') : saldoBefore[0]], 
						FailureHandling.CONTINUE_ON_FAILURE)
				}
				
				if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
					'call test case NewUserStoreDB setelah registrasi dan aktivasi'
					WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/NewUserStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'],
						FailureHandling.CONTINUE_ON_FAILURE)
				}
				
				if (GlobalVariable.FlagFailed == 0) {
					'kurang saldo before dengan proses verifikasi'
				    saldoBefore.set(1, (Integer.parseInt(saldoBefore[1]) - GlobalVariable.VerificationCount).toString())
				
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
					
					if (((findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == '0' && 
						(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '1')) || (
						(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '0'))) &&
						findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase().contains('OUTLOOK.COM') &&
							GlobalVariable.Psre == 'VIDA') {
						'call keyword get email'
						String emailCert = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPathAPIGenerateInvLink).getValue(
											GlobalVariable.NumofColm, rowExcel('email')).replace('"',''), findTestData(excelPathAPIGenerateInvLink).getValue(
											GlobalVariable.NumofColm, rowExcel('Password')))
						
						'verify email cert'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(emailCert, 'Penerbitan Sertifikat Elektronik',
								false, FailureHandling.CONTINUE_ON_FAILURE), ' email cert tidak terkirim')
					}
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
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
	ArrayList<String> saldo = []
	
	'call test case login per case'
	WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathAPIGenerateInvLink, ('Email') : 'Email Login', ('Password') : 'Password Login'
		, ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)

	'check if button menu visible atau tidak'
	if(WebUI.verifyElementNotVisible(findTestObject('RegisterEsign/checkSaldo/menu_Saldo'), FailureHandling.OPTIONAL)) {
		'click menu saldo'
		WebUI.click(findTestObject('button_HamburberSideMenu'))
	}
	
	'click menu saldo'
	WebUI.click(findTestObject('RegisterEsign/checkSaldo/menu_Saldo'))

	'click ddl bahasa'
	WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_bahasa'))

	'click english'
	WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_English'))
	
	'select vendor'
	WebUI.selectOptionByLabel(findTestObject('RegisterEsign/checkSaldo/select_Vendor'), '(?i)' + 'ESIGN/ADINS', true)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div h3'))

	for (index = 2; index <= (variable.size()/2); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo di excel'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('OTP')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'get qty saldo before'
			saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

			break
		}
	}

	if ((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0 || findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Warning')) && GlobalVariable.Psre == 'VIDA') {
		'call function input filter saldo'
		inputFilterSaldo('OTP', conneSign)
	}

	'select vendor'
	WebUI.selectOptionByValue(findTestObject('RegisterEsign/checkSaldo/select_Vendor'), '(?i)' + GlobalVariable.Psre, true)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	for (index = 2; index <= (variable.size()/2); index++) {
		'modify object box info'
		modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
			']/div/div/div/div/div[1]/h3', true)

		'check if box info = tipe saldo di excel'
		if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Verification') || (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('PNBP') && GlobalVariable.Psre == 'VIDA')
			 || (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Text Verification') && GlobalVariable.Psre == 'DIGI')) {
			'modify object qty'
			modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
				'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) +
				']/div/div/div/div/div[2]/h3', true)

			'get qty saldo before'
			saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

			'if saldo sudah terisi 2 verification dan pnbp'
			if(saldo.size() == 3 && (GlobalVariable.Psre == 'VIDA' || GlobalVariable.Psre == 'DIGI')) {
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
		
		if(GlobalVariable.FlagFailed == 0 && GlobalVariable.Psre == 'DIGI') {
			'call function input filter saldo'
			inputFilterSaldo('Text Verification', conneSign)
		}
		
		//		'call function verify list undangan'
		//		verifyListUndangan()
	}
	return saldo
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
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
	modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'),
		'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
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
	modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

	'modify object qty'
	modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals',
		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

	'get trx dari db'
	ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(
			GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''), findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', ''),
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

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
