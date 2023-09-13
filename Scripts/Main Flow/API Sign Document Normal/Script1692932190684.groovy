import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

documentId = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

documentIdInput = '"documentId":"' + documentId[0] + '",'

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

ArrayList totalSignedBefore = [], totalSignedAfter = []

HashMap<String, String> saldoBefore = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : API_Excel_Path
	, ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

ttdBefore = saldoBefore.get('TTD')

emailSigner = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email (Sign Normal)')).split(';', -1)

'mengambil isi email signer berdasarkan excel dan di split.'
emailSignerInput = '"email": '+emailSigner[GlobalVariable.indexUsed]+','

'Mengambil aes key based on tenant tersebut'
String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

msg = encryptLink(conneSign, documentId[0].toString(), emailSigner[GlobalVariable.indexUsed], aesKey)

'HIT API Login untuk token : andy@ad-ins.com'
respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : emailSigner[GlobalVariable.indexUsed].toString().replace('"','')
            , ('password') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer'))]))

'Jika status HIT API Login 200 OK'
if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
    'Parsing token menjadi GlobalVariable'
    GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

	'looping berdasarkan ukuran dari dokumen id'
	for (int z = 0; z < documentId.size(); z++) {
		'Memasukkan input dari total signed'
		(totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, (documentId[z]).toString())
	}
	
    'HIT API Sign Document'
    respon_signdoc = WS.sendRequest(findTestObject('Postman/Sign Doc', [('callerId') : ('"' + findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId (Sign Normal)')).split(';', -1)[GlobalVariable.indexUsed] ) + '"', ('email') : emailSignerInput, ('documentId') : documentIdInput, ('msg') : ('"' + 
                msg) + '"']))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon_signdoc, 200, FailureHandling.OPTIONAL) == true) {
        'get Status Code'
        status_Code = WS.getElementPropertyValue(respon_signdoc, 'status.code')

        'Jika status codenya 0'
        if (status_Code == 0) {
            'get vendor Code'
            GlobalVariable.Response = WS.getElementPropertyValue(respon_signdoc, 'vendorCode')

			'Loop berdasarkan jumlah documen id'
			for (int x = 0; x < documentId.size(); x++) {

				signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, documentId[x].toString(), emailSigner[GlobalVariable.indexUsed].replace('"', ''))
	
				'Loop untuk check db update sign. Maksimal 200 detik.'
				for (int v = 1; v <= 20; v++) {
					'Mengambil total Signed setelah sign'
					(totalSignedAfter[x]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[x])

					'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
					if ((totalSignedAfter[x]) == ((totalSignedBefore[x]) + Integer.parseInt(signCount))) {
						WebUI.verifyEqual(totalSignedAfter[x], (totalSignedBefore[x]) + Integer.parseInt(signCount), FailureHandling.CONTINUE_ON_FAILURE)
	
						if (GlobalVariable.FlagFailed == 0) {
						'write to excel success'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
							0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
						}
			
						'Write to excel mengenai Document ID'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Success') - 1, GlobalVariable.NumofColm - 1, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Result Count Success')) + ';' + 'Success : 1')
						
						'Write to excel mengenai Document ID'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Failed') - 1, GlobalVariable.NumofColm - 1, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Result Count Failed')) + ';' + 'Failed : 0')

						'check Db'
						if (GlobalVariable.checkStoreDB == 'Yes') {
							'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
							
							'Memanggil testCase mengenai SignDocumentStoreDb'
							WebUI.callTestCase(findTestCase('Main Flow/API Sign Document Normal StoreDB'), [('API_Excel_Path') : API_Excel_Path, ('sheet') : sheet],
								FailureHandling.CONTINUE_ON_FAILURE)
						}

						HashMap<String, String> saldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : API_Excel_Path
							, ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)
						
						ttdAfter = saldoAfter.get('TTD')
				
					'check saldo before dan aftar'
					if (ttdBefore == ttdAfter) {
						'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedNoneUI) + ' terhadap pemotongan saldo ')
					} else {
						verifySaldoSigned(conneSign, documentId[0])
						
					}
						break
					} else if (v == 20) {
						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu 200 detik ')
	
						GlobalVariable.FlagFailed = 1
					} else {
						'Delay 10 detik.'
						WebUI.delay(10)
					}
				}
			}
        } else {
			getErrorMessageAPI(respon_signdoc)
        }
    } else {
		getErrorMessageAPI(respon_signdoc)
    }
} else {
    'write to excel status failed dan reason : failed hit api'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + GlobalVariable.ReasonFailedHitAPI)

    'close browser'
    WebUI.closeBrowser()
}

def getErrorMessageAPI(def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + '>')

	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}

def encryptLink(Connection conneSign, String documentId, String emailSigner, String aesKey) {
	officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant')))
	
	'pembuatan message yang akan dienkrip'
	msg = '{"tenantCode":"'+findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))+'","officeCode":"'+officeCode+'","email":"'+emailSigner+'"}'
	
	'enkripsi msg'
	encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(msg, aesKey)

	return encryptMsg
}

def verifySaldoSigned(Connection conneSign, String documentId) {
	'get current date'
	def currentDate = new Date().format('yyyy-MM-dd')

	'klik button saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

	noKontrak = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId)

	documentTemplateName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, noKontrak)

	'ambil nama vendor dari DB'
	vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, noKontrak)

	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

	documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, noKontrak)

	'input filter dari saldo'
	WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
			rowExcel('TipeSaldo')))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

	'Input tipe transaksi'
	WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
			rowExcel('TipeTransaksi')))

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

	'Input tipe dokumen'
	WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

	'Input referal number'
	WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

	'Input documentTemplateName'
	WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

	'Klik cari'
	WebUI.click(findTestObject('Saldo/btn_cari'))

	'Mengambil value dari db mengenai tipe pembayran'
	paymentType = CustomKeywords.'connection.APIFullService.getPaymentType'(conneSign, noKontrak)

	'Jika tipe pembayarannya per sign'
	if (paymentType == 'Per Sign') {
		'Memanggil saldo total yang telah digunakan per dokumen tersebut'
		saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrak)

		if (saldoUsedperDoc == '0') {
			WebUI.delay(10)

			'Memanggil saldo total yang telah digunakan per dokumen tersebut'
			saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrak)
		}
	} else {
		saldoUsedperDoc = 1
	}
	
	'delay dari 10 sampe 60 detik'
	for (int d = 1; d <= 6; d++) {
		'Jika dokumennya ada, maka'
		if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'get column di saldo'
			variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

			'get row di saldo'
			variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

			'ambil inquiry di db'
			ArrayList<String> inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, noKontrak, saldoUsedperDoc.toString())

			index = 0

			'check total row dengan yang tertandatangan'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(variableSaldoRow.size().toString(), saldoUsedperDoc.toString(), false,
					FailureHandling.CONTINUE_ON_FAILURE), ' pada jumlah tertanda tangan dengan row transaksi ')

			'looping mengenai rownya'
			for (int j = 1; j <= variableSaldoRow.size(); j++) {
				'looping mengenai columnnya'
				for (int u = 1; u <= variableSaldoColumn.size(); u++) {
					'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
					modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'),
						'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

					WebUI.scrollToElement(modifyperrowpercolumn, GlobalVariable.TimeOut)

					'Jika u di lokasi qty atau kolom ke 9'
					if (u == 9) {
						'Jika yang qtynya 1 dan databasenya juga, berhasil'
						if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[(u - 1)]) == '-1')) {
							'Jika bukan untuk 2 kolom itu, maka check ke db'
							checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[
									index++], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' +
								noKontrak)
						} else {
							'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
							GlobalVariable.FlagFailed = 1

							'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
									rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') +
								'<') + noKontrak) + '>')
						}
					} else if (u == variableSaldoColumn.size()) {
						'Jika di kolom ke 10, atau di FE table saldo, check saldo dari table dengan saldo yang sekarang. Takeout dari dev karena no issue dan sudah sepakat'
					} else {
						'Jika bukan untuk 2 kolom itu, maka check ke db'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index++],
								false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' +
							noKontrak)
					}
				}
			}
			
			break
		} else {
			'jika kesempatan yang terakhir'
			if (d == 6) {
				'Jika masih tidak ada'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') +
					';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + '<') + noKontrak) + '>')
			}
			
			'delay 10 detik'
			WebUI.delay(10)

			'Klik cari'
			WebUI.click(findTestObject('Saldo/btn_cari'))
		}
	}
}
