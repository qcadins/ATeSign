import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'declare untuk split array excel dan beberapa variable yang dibutuhkan'
semicolon = ';'

splitIndex = -1

sheet = 'Manual Stamp to Stamp'

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
	('Path') : excelPathManualStamptoStamp], FailureHandling.CONTINUE_ON_FAILURE)

//'memanggil test case login untuk admin wom dengan Admin Client. Khusus login menuju ADMIN@ADINS.CO.ID'
//WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathManualStamptoStamp, ('sheet') : 'Manual Stamp to Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathManualStamptoStamp).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted') || 
    findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Warning')) {
        'declare flag failed'
        GlobalVariable.FlagFailed = 0

        'Inisialisasi array dan variable'
        indexEmail = 0

        'Inisialisasi variable yang dibutuhkan'
        totalMeterai = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 15)

        pindahkanSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 16).split(semicolon, splitIndex)

        lokasiSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 17).split('\\n', splitIndex)

        lockSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 18).split(semicolon, splitIndex)

        catatanStamping = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitIndex)

        saldoBefore = loginAdminGetSaldo(conneSign, 'No', sheet)

        'ambil index tab yang sedang dibuka di chrome'
        int currentTab = WebUI.getWindowIndex()

        'setting zoom menuju 80 persen'
        zoomSetting(80)

        'ganti fokus robot ke tab baru'
        WebUI.switchToWindowIndex(currentTab)

        'Klik tombol menu Manual Stamp'
        WebUI.click(findTestObject('ManualStamp/menu_ManualStamp'))
		
		'Klik tombol menu Manual Stamp'
		WebUI.click(findTestObject('ManualStamp/menu_ManualStamp'))
		
		'ambil index tab yang sedang dibuka di chrome'
		currentTab = WebUI.getWindowIndex()

		'setting zoom menuju 80 persen'
		zoomSetting(100)

		'ganti fokus robot ke tab baru'
		WebUI.switchToWindowIndex(currentTab)

        'Jika kolomnya berada pada kedua'
        if (GlobalVariable.NumofColm == 2) {
            inputCancel(conneSign)
        }
        
        'Pengecekan apakah masuk page Manual Stamp'
        if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_stampingMeterai'), GlobalVariable.TimeOut)) {
            'Input form yang ada pada page'
            inputForm()

            'check save ada attribute disabled'
            if (WebUI.verifyElementHasAttribute(findTestObject('ManualStamp/button_Selanjutnya'), 'disabled', GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'write to excel bahwa save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedSaveGagal)

                continue
            } else {
                'klik button selanjutnya'
                WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))

                'jika konfirmasi ada pada page tersebut'
                if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_KonfirmasiNext'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'klik button back'
                    WebUI.click(findTestObject('ManualStamp/button_BackNext'))

                    'verifikasi button selanjutnya ada pada page'
                    WebUI.verifyElementPresent(findTestObject('ManualStamp/button_Selanjutnya'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)

                    'klik button selanjutnya'
                    WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))

                    'klik button next'
                    WebUI.click(findTestObject('ManualStamp/button_Next'))

                    'jika element dokumen input ada'
                    if (WebUI.verifyElementPresent(findTestObject('ManualStamp/input_documentNo'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)) {
                        'check ui dan excel pada nomor dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentNo'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 8), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nomor dokumen ')

                        'check ui dan excel pada nama dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentName'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 9), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama dokumen ')

                        'check ui dan excel pada tipe dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docType'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 11), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi tipe dokumen ')

                        'check ui dan excel pada tipe dokumen peruri'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docTypePeruri'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 12), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi tipe dokumen peruri ')

                        'Klik button tanda tangan'
                        WebUI.click(findTestObject('ManualStamp/btn_meterai'))

                        'click button lock signbox'
                        WebUI.click(findTestObject('ManualStamp/btn_LockSignBox'))

                        'check isi catatan stamping ada pada page tersebut'
                        WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_isiCatatanStamping'), GlobalVariable.TimeOut, 
                            FailureHandling.CONTINUE_ON_FAILURE)

                        'set text catatan stamping'
                        WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[0])

                        'klik batal catatan stamping'
                        WebUI.click(findTestObject('ManualStamp/button_batalCatatanStamping'))

                        'click lock signbox'
                        WebUI.click(findTestObject('ManualStamp/btn_LockSignBox'))

                        WebUI.delay(2)

                        'check isi catatan stamping kosong'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch('', WebUI.getText(findTestObject('ManualStamp/input_isiCatatanStamping')), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada catatan meterai ')

                        'isi catatan stamping'
                        WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[0])

                        'klik simpan catatan stamping'
                        WebUI.click(findTestObject('ManualStamp/button_SimpanCatatanStamping'))

                        'check is locked'
                        isLocked = WebUI.getAttribute(findTestObject('ManualStamp/btn_LockSignBox'), 'class', FailureHandling.STOP_ON_FAILURE)

                        if ((catatanStamping[0]) != '') {
                            'verify sign box is locked'
                            checkVerifyEqualOrMatch(WebUI.verifyMatch(isLocked, 'fa fa-2x fa-lock', false, FailureHandling.CONTINUE_ON_FAILURE), 
                                ' pada sign locked ')
                        }
                        
                        'Klik button Delete'
                        WebUI.click(findTestObject('ManualStamp/btn_DeleteSignBox'))

                        'verify sign box is deleted'
                        checkVerifyEqualOrMatch(WebUI.verifyElementNotPresent(findTestObject('ManualSign/signBox'), GlobalVariable.TimeOut, 
                                FailureHandling.CONTINUE_ON_FAILURE), ' pada deletenya box Sign ')

                        'looping berdasarkan total tanda tangan'
                        for (int j = 1; j <= Integer.parseInt(totalMeterai); j++) {
                            'Klik button tanda tangan'
                            WebUI.click(findTestObject('Object Repository/ManualStamp/btn_meterai'))

                            'modify label tipe tanda tangan di kotak'
                            modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/ManualSign/modifyObject'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                j) + ']/div/div/small', true)

                            'Verifikasi antara excel dan UI, apakah tipenya sama'
                            WebUI.verifyMatch('Meterai', WebUI.getText(modifyobjectTTDlblRoleTandaTangan), false)

                            'Verify apakah tanda tangannya ada'
                            if (WebUI.verifyElementPresent(modifyobjectTTDlblRoleTandaTangan, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                                'check if signbox mau dipindahkan'
                                if ((pindahkanSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                                    'memindahkan sign box'
                                    CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        j) + ']/div', lokasiSignBox[(j - 1)])
                                }
                                
                                'check if signbox mau dilock posisinya'
                                if ((lockSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                                    'modify obejct lock signbox'
                                    modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('ManualSign/btn_LockSignBox'), 
                                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        j) + ']/div/button[1]/span', true)

                                    'click lock signbox'
                                    WebUI.click(modifyobjectLockSignBox)

                                    WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[j - 1])

                                    WebUI.click(findTestObject('ManualStamp/button_SimpanCatatanStamping'))
                                }
                            }
                        }
                        
                        'klik simpan'
                        WebUI.click(findTestObject('ManualStamp/button_Simpan'))

                        if (checkErrorLog() == true) {
                            continue
                        }
                        
                        if (GlobalVariable.FlagFailed == 0) {
                            'write to excel success'
                            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Manual Stamp to Stamp', 
                                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                result = CustomKeywords.'connection.ManualStamp.getManualStamp'(conneSign, findTestData(
                                        excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 8), GlobalVariable.Tenant)

                                index = 0

                                ArrayList arrayMatch = []

                                'verify ref number'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            8), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify document name'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            9), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tanggal dokumen'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            10), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            11), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            12), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                totalDocument = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 13).split(
                                    '\\n', -1)

                                'verify total dokumen'
                                arrayMatch.add(WebUI.verifyMatch(totalDocument.size().toString(), result[index++], false, 
                                        FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch('1', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch('0', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch(totalMeterai, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'jika data db tidak sesuai dengan excel'
                                if (arrayMatch.contains(false)) {
                                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', 
                                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathManualStamptoStamp).getValue(
                                            GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                                }
                            }
                            
                            inputEMeteraiMonitoring(conneSign)

                            saldoAfter = loginAdminGetSaldo(conneSign, 'No', sheet)

                            if (saldoBefore == saldoAfter) {
                                'write to excel status failed dan reason'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                        2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')

                                GlobalVariable.FlagFailed = 1
                            } else {
                                verifySaldoUsed(conneSign, sheet)
                            }

                        }
                    }
                }
            }
        }
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Permintaan pembubuhan e-Materai berhasil dibuat.'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            GlobalVariable.FlagFailed = 1

            return true
        }
    }
    
    return false
}

def inputCancel(Connection conneSign) {
    inputForm()

    'get data tipe-tipe pembayaran secara asc'
    ArrayList docTypeDB = CustomKeywords.'connection.ManualStamp.getDocType'(conneSign)

    'check ddl tipe pembayaran'
    checkDDL(findTestObject('ManualStamp/input_docType'), docTypeDB, ' pada DDL tipe dokumen ')

    'get data tipe-tipe pembayaran secara asc'
    ArrayList docTypePeruriDB = CustomKeywords.'connection.ManualStamp.getDocTypePeruri'(conneSign)

    'check ddl tipe pembayaran'
    checkDDL(findTestObject('ManualStamp/input_docTypePeruri'), docTypePeruriDB, ' pada DDL tipe dokumen peruri ')

    'Klik button cancel'
    WebUI.click(findTestObject('ManualStamp/button_Batal'))

    WebUI.delay(10)

    'verify field kode template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentNo'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field input psre tidak kosong ')

    'verify field Nama template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentName'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nomor Kontrak tidak kosong ')

    'verify field Deskripsi template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentDate'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nama Dokumen tidak kosong ')

    'verify field tipe pembayaran kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_docType'), 'value', FailureHandling.OPTIONAL), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Tanggal Dokumen tidak kosong ')

    'verify field status kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_docTypePeruri'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field jenis Pembayaran tidak kosong ')
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('ManualSign/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('ManualSign/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' + 
            id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    println(listDB)

    println(list)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def inputForm() {
    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('ManualStamp/input_documentNo'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            8))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('ManualStamp/input_documentName'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            9))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_documentDate'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            10))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docType'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            11))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docType'), Keys.chord(Keys.ENTER))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docTypePeruri'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            12))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docTypePeruri'), Keys.chord(Keys.ENTER))

    'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
    String userDir = System.getProperty('user.dir')

    String filePath = userDir + findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 13)

    'Upload file berdasarkan filePath yang telah dirancang'
    WebUI.uploadFile(findTestObject('ManualStamp/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)
}

def zoomSetting(int percentage) {
    Float percentageZoom = percentage / 100

    WebDriver driver = DriverFactory.webDriver

    'buka tab baru'
        ((driver) as JavascriptExecutor).executeScript('window.open();')

    'ambil index tab yang sedang dibuka di chrome'
    int currentTab = WebUI.getWindowIndex()

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab + 1)

    driver.get('chrome://settings/')

        ((driver) as JavascriptExecutor).executeScript(('chrome.settingsPrivate.setDefaultZoom(' + percentageZoom.toString()) + 
        ');')

    'close tab baru'
        ((driver) as JavascriptExecutor).executeScript('window.close();')
}

def inputEMeteraiMonitoring(Connection conneSign) {
	if (WebUI.verifyElementPresent(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'), GlobalVariable.TimeOut)) {
		totalMeterai = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 15)

		for (int i = 1; i <= 10; i++) {
			ArrayList inputEMeterai = CustomKeywords.'connection.ManualStamp.getInputeMeteraiMonitoring'(conneSign, findTestData(
					excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 8), GlobalVariable.Tenant)

			indexInput = 0

			index = 0
			
			if (((inputEMeterai[6]) == 'Failed') || ((inputEMeterai[6]) == 'Success')) {

				for (int j = 1; j <= Integer.parseInt(totalMeterai); j++) {
					'Klik set ulang setiap data biar reset'
					WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))
			
					'set text no kontrak'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), inputEMeterai[
						indexInput++])

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), inputEMeterai[
						indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(
							Keys.ENTER))

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), inputEMeterai[
						indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(
							Keys.ENTER))

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), inputEMeterai[
						indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(
							Keys.ENTER))

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'),
						inputEMeterai[indexInput++])

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), inputEMeterai[
						indexInput++])

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/button_prosesStamping'), inputEMeterai[
						indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/button_prosesStamping'), Keys.chord(
							Keys.ENTER))

					'set text status meterai'
					//WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), inputEMeterai[
					//	indexInput++])
					
					indexInput++
					
					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), inputEMeterai[indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

					'set text status meterai'
					WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), inputEMeterai[
						indexInput++])

					'enter untuk set status meterai'
					WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(
							Keys.ENTER))

					'click button cari'
					WebUI.click(findTestObject('e-Meterai Monitoring/button_Cari'))

					'Jika error lognya muncul'
					if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						'ambil teks errormessage'
						errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

						'Tulis di excel itu adalah error'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('e-Meterai Monitoring', GlobalVariable.NumofColm,
							GlobalVariable.StatusWarning, (((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm,
								2).replace('-', '') + ';') + '<') + errormessage) + '>')

						GlobalVariable.FlagFailed = 1
					}
					
					'get stampduty data dari db'
					result = CustomKeywords.'connection.eMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, findTestData(
							excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 8))

					'verify no dokumen'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NomorDokumen')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Dokumen ')

					'verify tanggal dokumen'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TanggalDokumen')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Dokumen ')

					'verify cabang'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Cabang')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang ')

					'verify nama dokume'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NamaDokumen')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dokumen ')

					'verify tipe dokumen peruri'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumenPeruri')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen Peruri ')

					'verify tipe dokumen'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumen')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen ')

					'verify nominal dokumen'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NominalDokumen')).replace(
								',', ''), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nominal Dokumen ')

					'verify pengaturan dokumen'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_PengaturanDokumen')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengaturan Dokumen ')

					'verify hasil stamping'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_HasilStamping')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Hasil Stamping ')

					'verify nomor seri'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Nomor Seri')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Seri ')
					
					'verify proses meterai'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Proses Materai')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Proses Materai ')

					'verify jenis pajak'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_JenisPajak')),
							result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Jenis Pajak ')
				}
				break
			} else {
				if (i == 10) {
					WebUI.delay(15)
					
					'write to excel bahwa save gagal'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm,
							2) + ';') + GlobalVariable.ReasonFailedProsesStamping + ' yaitu status meterai adalah ' + inputEMeterai[6] + ' pada nomor dokumen tersebut selama ' + (i * 15))
				} else {
					WebUI.delay(15)
				}

			}
		}
	}
}

def loginAdminGetSaldo(Connection conneSign, String start, String sheet) {
    String totalSaldo

    if (start == 'Yes') {
		'panggil fungsi login'
		WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
			('Path') : excelPathManualStamptoStamp], FailureHandling.STOP_ON_FAILURE)
		
//        'Call test Case untuk login sebagai admin wom admin client'
//        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathManualStamptoStamp, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)
    }
    
	'klik button saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
	
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

def verifySaldoUsed(Connection conneSign, String sheet) {
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, 8))

    documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, 8))

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
    WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            8))

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
    ArrayList inquiryDB = CustomKeywords.'connection.ManualStamp.gettrxSaldoForMeterai'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, 8))

    index = 0

        'looping mengenai columnnya'
        for (int u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                variableSaldoRow.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

            'Jika u di lokasi qty atau kolom ke 9'
            if (u == 9) {
                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index]) == '-1')) {
                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                        findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    index++
                } else {
                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                    GlobalVariable.FlagFailed = 1

                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                        findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    index++
                }
            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                'Jika di kolom ke 10, atau di FE table saldo'
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false, 
                        FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + findTestData(excelPathManualStamptoStamp).getValue(
                        GlobalVariable.NumofColm, 11).replace('"', ''))

                index++
            }
        }
}

