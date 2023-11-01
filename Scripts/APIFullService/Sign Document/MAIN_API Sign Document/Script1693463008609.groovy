import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import java.net.InetAddress as InetAddress
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).columnNumbers

'declafe split'
int splitnum = -1

'looping API Sign Document beserta checking saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'ambil tenant dan vendor code yang akan digunakan document'
		ArrayList tenantVendor = CustomKeywords.'connection.DataVerif.getTenantandVendorCode'(conneSign, findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"','').replace('[','').replace(']',''))
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISignDocument, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'setting vendor otp dimatikan/diaktifkan'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable User Vendor OTP?(0/1/'Empty')")).length() > 0) {
			'update setting vendor otp ke table di DB'
			CustomKeywords.'connection.UpdateData.updateVendorOTP'(conneSign, tenantVendor[1], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable User Vendor OTP?(0/1/'Empty')")))
		}
		
		'setting tenant otp dimatikan/diaktifkan'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable Need OTP for signing?(0/1/'Empty')")).length() > 0) {
			'update setting otp ke table di DB'
			CustomKeywords.'connection.UpdateData.updateTenantOTPReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable Need OTP for signing?(0/1/'Empty')")))
		}
		
		'setting tenant password dimatikan/diaktifkan'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable Need Password for signing?(0/1/'Empty')")).length() > 0) {
			'update setting pass tenant ke table di DB'
			CustomKeywords.'connection.UpdateData.updateTenantPassReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel("Enable Need Password for signing?(0/1/'Empty')")))
		}

		'declare variable array'
		ArrayList saldoBefore = []

		ArrayList saldoAfter = []

		saldoBefore = loginAdminGetSaldo(conneSign)

		GlobalVariable.FlagFailed = 0

		'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
		String otp, photo, ipaddress

		'Mengambil PSre dari setting excel percase'
		GlobalVariable.Psre = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		ArrayList totalSignedBefore = [], totalSignedAfter = [], flaggingOTP = []

		'Split dokumen id agar mendapat dokumenid 1 per 1 dengan case bulk'
		documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace('[', '').replace(
			']', '').replace('"', '').split(',', splitnum)

		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
			'Mengambil tenant dari setting'
			GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}
		
		String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

		String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)
		
		flaggingOTP = CustomKeywords.'connection.DataVerif.getParameterFlagPassOTP'(conneSign, findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"','').replace('[','').replace(']',''))
		
		if (vendor.equalsIgnoreCase('Privy')) {
			'request OTP dengan HIT API'

			'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
			respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing siap delete', [('callerId') : findTestData(
							excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
							excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo')), ('email') : findTestData(excelPathAPISignDocument).getValue(
							GlobalVariable.NumofColm, rowExcel('email')), ('refnumber') : ('"' + CustomKeywords.'connection.APIFullService.getRefNumber'(
							conneSign, documentId[0])) + '"']))

			'Jika status HIT API 200 OK'
			if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
				'get status code'
				code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

				'jika codenya 0'
				if (code_otp == 0) {
					'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP.'
					WebUI.delay(50)

					otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP'))
				}
			}
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database')) == 'Yes') {
			'check if mau menggunakan OTP yang salah atau benar'

			'request OTP dengan HIT API'

			'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
			respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing siap delete', [('callerId') : findTestData(
							excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
							excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo')), ('email') : findTestData(excelPathAPISignDocument).getValue(
							GlobalVariable.NumofColm, rowExcel('email')), ('refnumber') : ('"' + CustomKeywords.'connection.APIFullService.getRefNumber'(
							conneSign, documentId[0])) + '"']))

			'Jika status HIT API 200 OK'
			if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
				'get status code'
				code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

				'jika codenya 0'
				if (code_otp == 0) {
					'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
					WebUI.delay(1)

					'Mengambil otp dari database'
					otp = (('"' + CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
							GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))) + '"')
				} else {
					getErrorMessageAPI(respon_OTP)
				}
			} else {
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedOTPError)
			}
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database')) == 'No') {
			'get otp dari excel'
			otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP'))
		}
		
		'check if mau menggunakan base64 untuk photo yang salah atau benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto')) == 'Yes') {
			'get base64 photo dari fungsi'
			photo = (('"' + phototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto')))) +
			'"')
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto')) == 'No') {
			'get base64 photo salah dari excel'
			photo = (('"' + findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto'))) + '"')
		}
		
		'check if mau menggunakan ip address yang salah atau benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress')) == 'Yes') {
			'get ip address dari fungsi'
			ipaddress = (('"' + correctipAddress()) + '"')
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress')) == 'No') {
			'get ip address salah dari excel'
			ipaddress = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('ipAddress'))
		}
		
		'looping berdasarkan ukuran dari dokumen id'
		for (int z = 0; z < documentId.size(); z++) {
			'Memasukkan input dari total signed'
			(totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[z])
		}
		break
		
		'HIT API Sign'
		respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
						GlobalVariable.NumofColm, rowExcel('callerId')), ('documentId') : findTestData(excelPathAPISignDocument).getValue(
						GlobalVariable.NumofColm, rowExcel('$documentid')), ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('email')), ('password') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('password'))
					, ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('browserInfo')), ('otp') : otp, ('selfPhoto') : photo]))

		'Jika status HIT API 200 OK'
		if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			'get status code'
			code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

			'get status code'
			trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

			'Jika trxNonya tidak kosong dari response'
			if (trxNo != null) {
				'Input excel'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
					rowExcel('trxno')-1, GlobalVariable.NumofColm - 1, trxNo.toString().replace('[', '').replace(']', ''))
			}
			
			'jika codenya 0'
			if (code == 0) {
				'Loop berdasarkan jumlah documen id'
				for (int x = 0; x < documentId.size(); x++) {
					signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, documentId[x], findTestData(
							excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

					'Loop untuk check db update sign. Maksimal 200 detik.'
					for (int v = 1; v <= 20; v++) {
						'Mengambil total Signed setelah sign'
						(totalSignedAfter[x]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[
							x])

						'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
						if ((totalSignedAfter[x]) == ((totalSignedBefore[x]) + Integer.parseInt(signCount))) {
							WebUI.verifyEqual(totalSignedAfter[x], (totalSignedBefore[x]) + Integer.parseInt(signCount),
								FailureHandling.CONTINUE_ON_FAILURE)

							'write to excel success'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
								0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

							'check Db'
							if (GlobalVariable.checkStoreDB == 'Yes') {
								'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
								responseAPIStoreDB(conneSign, ipaddress, documentId)
							}
							
							break
						} else if (v == 20) {
							'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
									rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu 200 detik ')

							GlobalVariable.FlagFailed = 1
						} else {
							'Delay 10 detik.'
							WebUI.delay(10)
						}
					}
				}
			} else {
				getErrorMessageAPI(respon)
			}
			
			if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')) != '') {
				'ambil trx no untuk displit'
				trxNo = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')).split(', ', -1)

				'Diberikan delay dengan pembuatan trx no di db sebesar 5 detik'
				WebUI.delay(5)

				'looping per trx no'
				for (int i = 0; i < trxNo.size(); i++) {
					'Mengambil tipe saldo yang telah digunakan'
					checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, trxNo[
						i])

					if (GlobalVariable.FlagFailed == 1) {
						'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')) + ';') + ' Transaksi dengan nomor ') + ('<' + trxNo[i])) + '> digunakan untuk ') + checkTypeofUsedSaldo)
					}
				}
				
				'check saldo'
				saldoAfter = loginAdminGetSaldo(conneSign)

				'check saldo before dan aftar'
				if (saldoBefore == saldoAfter) {
					'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedNoneUI) + ' terhadap pemotongan saldo ')
				} else {
					verifySaldoUsed(conneSign)
				}
			}
		} else {
			getErrorMessageAPI(respon)

		}
	}
}

def correctipAddress() {
	return InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
	return CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
}

def responseAPIStoreDB(Connection conneSign, String ipaddress, String[] documentId) {
	'get current date'
	currentDate = new Date().format('yyyy-MM-dd')

	'declare arraylist arraymatch'
	ArrayList arrayMatch = []

	'loop berdasarkan dokumen id'
	for (int i = 0; i < documentId.size(); i++) {
		'get data from db'
		arrayIndex = 0

		'Array result. Value dari db'
		result = CustomKeywords.'connection.APIFullService.getSign'(conneSign, (documentId[i]).replace('"', ''), findTestData(
				excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

		'verify qty dalam transaksi. Jika done = 1'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

		'Check liveness compare adalah 0 dikarenakan trxNo yang didapat adalah transaksi untuk liveness compare.'

		'Ini perlu dideklarasi dikarenakan jika 2 dokumen, trxNo tetap 1, sehingga perlu diflag apakah dia sudah check trxnya atau belum'
		checkLivenessCompare = 0

		'Jika trxNonya tidak kosong dan checkLivenessComparenya 0'
		if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')) != '') && (checkLivenessCompare ==
		0)) {
			'verify trx no. Jika sesuai, maka'
			if (WebUI.verifyEqual(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
					rowExcel('trxno')), FailureHandling.CONTINUE_ON_FAILURE)) {
				'Ditambah 1'
				checkLivenessCompare++

				'arrayMatchnya diinput true'
				arrayMatch.add(true)
			}
		} else {
			'Tambah dari arrayIndex'
			arrayIndex++
		}
		
		'verify request status. 3 = done'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '3', false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify ref number yang tertandatangan'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign,
					(documentId[i]).replace('"', '')), false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify ip address'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify user browser'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
					rowExcel('browserInfo')).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify callerId'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
					rowExcel('callerId')).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
		arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

		'verify tanggal tanda tangan'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify api key'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

		'verify tenant'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	
	'jika data db tidak sesuai dengan excel'
	if (arrayMatch.contains(false)) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
			';') + GlobalVariable.ReasonFailedStoredDB)
	}
}

def loginAdminGetSaldo(Connection conneSign) {
	ArrayList totalSaldo = []

	docid = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"', '').replace('[',
		'').replace(']', '').split(',' - 1)

	noKontrak = ''

	for (int i = 1; i <= docid.size(); i++) {
		if (noKontrak != '') {
			noKontrak = (noKontrak + ';')
		}
		
		noKontrak = (noKontrak + CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, docid[(i - 1)]))
	}
	
	String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, noKontrak)
	
	'panggil fungsi login'
	WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
		('Path') : excelPathAPISignDocument, ('Email') : 'Email Login', ('Password') : 'Password Login',
			('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)

	'klik button saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

	'get total div di Saldo'
	variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

	'looping berdasarkan total div yang ada di saldo'
	for (int c = 1; c <= variableDivSaldo.size(); c++) {
		'modify object mengenai find tipe saldo'
		modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' +
			(c + 1)) + ']/div/div/div/div/div[1]', true)

		'verifikasi label saldonya '
		if (WebUI.verifyElementText(modifyObjectFindSaldoSign, 'TTD', FailureHandling.OPTIONAL)) {
			'modify object mengenai ambil total jumlah saldo'
			modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals',
				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c + 1)) + ']/div/div/div/div/div[2]',
				true)

			'mengambil total saldo yang pertama'
			totalSaldo.add(WebUI.getText(modifyObjecttotalSaldoSign))

			break
		}
	}
	
	vendor = 'ESIGN/ADINS'

	if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP')) != '') {
		isSplit = CustomKeywords.'connection.APIFullService.getSplitLivenessFaceCompareBill'(conneSign)

		if (isSplit == '1') {
			paymentType = ['Liveness', 'Face Compare']
		} else {
			paymentType = ['Liveness Face Compare']
		}
	} else {
		paymentType = ['OTP']
	}
	
	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

	'get total div di Saldo'
	variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))

	for (int i = 0; i < paymentType.size(); i++) {
		'looping berdasarkan total div yang ada di saldo'
		for (int c = 2; c <= variableDivSaldo.size(); c++) {
			'modify object mengenai find tipe saldo'
			modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals',
				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c)) + ']/div/div/div/div/div[1]',
				true)

			'verifikasi label saldonya '
			if (WebUI.verifyElementText(modifyObjectFindSaldoSign, paymentType[i], FailureHandling.OPTIONAL)) {
				'modify object mengenai ambil total jumlah saldo'
				modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath',
					'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' +
					(c)) + ']/div/div/div/div/div[2]', true)

				'mengambil total saldo yang pertama'
				totalSaldo.add(WebUI.getText(modifyObjecttotalSaldoSign))
			}
		}
	}
	
	'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
	WebUI.callTestCase(findTestCase('Document Monitoring/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathAPISignDocument
			, ('sheet') : sheet, ('linkDocumentMonitoring') : 'Not Used', ('nomorKontrak') : noKontrak], FailureHandling.CONTINUE_ON_FAILURE)

	'return total saldo awal'
	return totalSaldo
}

def verifySaldoUsed(Connection conneSign) {
	'klik button saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

	docid = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"', '').replace('[',
		'').replace(']', '').split(',' - 1)

	noKontrak = ''

	for (int i = 1; i <= docid.size(); i++) {
		if (noKontrak != '') {
			noKontrak = (noKontrak + ';')
		}
		
		noKontrak = (noKontrak + CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, docid[(i - 1)]))
	}
	
	'ambil trx no untuk displit'
	trxNo = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')).split(',', -1)

	'looping per trx no'
	for (int i = 0; i < trxNo.size(); i++) {
		'Mengambil tipe saldo yang telah digunakan'
		checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, trxNo[i])

		'input tipe saldo'
		WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), checkTypeofUsedSaldo.replace('Use ', ''))

		'enter untuk input tipe saldo'
		WebUI.sendKeys(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

		'click button cari'
		WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_Cari'))

		'get row'
		variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

		'modify object button last page'
		modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'),
			'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
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

		'modify object qty'
		modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
			'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
			variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div', true)

		'get trx dari db'
		ArrayList result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathAPISignDocument).getValue(
				GlobalVariable.NumofColm, 11).replace('"', ''), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
				rowExcel('callerId')).replace('"', ''), checkTypeofUsedSaldo)

		arrayIndex = 0

		'verify no trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
			' No Trx')

		'verify tgl trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
					'.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

		'verify tipe trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false,
				FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

		'verify user trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
			' User')

		'verify note trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE),
			' Notes')

		'verify qty trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''),
				false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx')

		'verify qty trx ui = db'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectBalance), '1'.replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE),
			' Balance ')
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, ((findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
				rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def getErrorMessageAPI(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, ('<' + message) + '>')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}