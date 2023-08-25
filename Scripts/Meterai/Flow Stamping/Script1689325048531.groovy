import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import java.sql.Connection as Connection
import java.util.concurrent.ConcurrentHashMap.KeySetView as KeySetView
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String valueRefNum, nomorKontrakDocument

'penggunaan versi 3.1.0 atau 3.0.0 dengan 1 api yang sama'
if (useAPI == 'v3.1.0') {
    'set base url menjadi v.3.1.0'
    GlobalVariable.base_url = (GlobalVariable.base_url + '/services/external/document/requestStamping')

    'setting value body di api berdasarkan versi stamp nya'
    valueRefNum = '"refNumber"'
} else if (useAPI == 'v.3.0.0') {
    'set base url menjadi v.3.0.0'
    GlobalVariable.base_url = (GlobalVariable.base_url + '/services/confins/stampduty/attachMeteraiPajakku')

    'setting value body di api berdasarkan versi stamp nya'
    valueRefNum = '"agreementNo"'
}

if (excelPathStamping == 'ManualSign/ManualSigntoSign') {
	nomorKontrakDocument = '"' + findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 9) + '"'
	callerId = '"ADINS"'
} else {
	nomorKontrakDocument = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11)
	
	callerId = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 43)
}

saldoBefore = loginAdminGetSaldo(conneSign, 'No', sheet)

'get api key dari db'
GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)

'HIT API stamping'
respon = WS.sendRequest(findTestObject('Flow Stamping', [('callerId') : callerId, ('valueRefNum') : valueRefNum, ('refNumber') : nomorKontrakDocument]))

'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'jika code 0'
    if (code == 0) {
        'looping dari 1 hingga 12'
        for (i = 1; i <= 12; i++) {
            'mengambil value db proses ttd'
            int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakDocument.replace('"', ''))

            'jika proses materai gagal (51)'
            if (prosesMaterai == 51) {
				'Kasih delay untuk mendapatkan update db untuk error stamping'
				WebUI.delay(3)
				
				'get reason gailed error message untuk stamping'
				errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakDocument.replace('"', ''))
               
				 'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    GlobalVariable.ReasonFailedProsesStamping + ' dengan alasan ' + errorMessageDB.toString())

                GlobalVariable.FlagFailed = 1

                break
            } else if (prosesMaterai == 53) {
                'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                WebUI.delay(3)

                'Mengambil value total stamping dan total meterai'
                ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                    conneSign, nomorKontrakDocument.replace('"', ''))

                'declare arraylist arraymatch'
                arrayMatch = []

                'dibandingkan total meterai dan total stamp'
                arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], false, 
                        FailureHandling.CONTINUE_ON_FAILURE))

                'jika data db tidak bertambah'
                if (arrayMatch.contains(false)) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedStoredDB)

                    GlobalVariable.FlagFailed = 1
                } else {
					GlobalVariable.FlagFailed = 0
				}
                
                break
            } else {
                'Jika bukan 51 dan 53, maka diberikan delay 20 detik'
                WebUI.delay(10)

                'Jika looping berada di akhir, tulis error failed proses stamping'
                if (i == 12) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + (i * 12)) + 
                        ' detik ')

                    GlobalVariable.FlagFailed = 1
                }
            }
        }
        
        'Jika flag failed tidak 0'
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
			
			'Call verify meterai'
			WebUI.callTestCase(findTestCase('Meterai/verifyMeterai'), [('excelPathMeterai') : excelPathStamping, ('sheet') : sheet
					, ('noKontrak') : nomorKontrakDocument.replace('"', '')
					, ('linkDocumentMonitoring') : linkDocumentMonitoring], FailureHandling.CONTINUE_ON_FAILURE)
	
			saldoAfter = loginAdminGetSaldo(conneSign, 'Yes', sheet)
	
			if (saldoBefore == saldoAfter) {
				'write to excel status failed dan reason'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) +
					' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')
			} else {
				verifySaldoUsed(conneSign, sheet)
			}
        }
    } else {
        getErrorMessageAPI(respon)
    }
} else {
    getErrorMessageAPI(respon)
}

//   checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(WebUI.getText(modifyperrowpercolumn)), p, FailureHandling.CONTINUE_ON_FAILURE), 
//        'pada Saldo di Mutasi Saldo dengan nomor kontrak ' + findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
//            11).replace('"', ''))

def loginAdminGetSaldo(Connection conneSign, String start, String sheet) {
    String totalSaldo

    if (start == 'Yes') {
		
		'panggil fungsi login'
		WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
			('Path') : excelPathStamping], FailureHandling.CONTINUE_ON_FAILURE)
		
//        'Call test Case untuk login sebagai admin wom admin client'
//        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathStamping, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)
		
		'klik button saldo'
		WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
    }

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, 'Meterai', FailureHandling.OPTIONAL)) {
            'modify object mengenai ambil total jumlah saldo'
            modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c + 1)) + ']/div/div/div/div/div[2]', 
                true)

            'mengambil total saldo yang pertama'
            totalSaldo = WebUI.getText(modifyObjecttotalSaldoSign)

            break
        }
    }
    
    'return total saldo awal'
    return totalSaldo
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def verifySaldoUsed(Connection conneSign, String sheet) {
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, findTestData(excelPathStamping).getValue(
            GlobalVariable.NumofColm, 11).replace('"', ''))

    documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, findTestData(excelPathStamping).getValue(
            GlobalVariable.NumofColm, 11).replace('"', ''))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), 'Stamp Duty')

    'enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ARROW_DOWN))

    'enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use Stamp Duty')

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
            11).replace('"', ''))

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentName, FailureHandling.CONTINUE_ON_FAILURE)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))

    'get column di saldo'
    variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

    'get row di saldo'
    variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

    'ambil inquiry di db'
    ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

    index = 0

    for (int p = 1; p <= variableSaldoRow.size(); p++) {
        'looping mengenai columnnya'
        for (int u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                p) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

            'Jika u di lokasi qty atau kolom ke 9'
            if (u == 9) {
                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index]) == '-1')) {
                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                        findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    index++
                } else {
                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                    GlobalVariable.FlagFailed = 1

                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                        findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    index++
                }
            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                'Jika di kolom ke 10, atau di FE table saldo'
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false, 
                        FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, 11).replace('"', ''))

                index++
            }
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 2) + ';' + '<' + message + '>')
	
	GlobalVariable.FlagFailed = 1
}

