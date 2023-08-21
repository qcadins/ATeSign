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

'memanggil test case login untuk admin wom dengan Admin Client. Khusus login menuju ADMIN@ADINS.CO.ID'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathManualStamp, ('sheet') : 'Manual Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathManualStamp).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted') || 
    findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Warning')) {
        'declare flag failed'
        GlobalVariable.FlagFailed = 0

        'Inisialisasi array dan variable'
        indexEmail = 0

        'Inisialisasi variable yang dibutuhkan'
        totalMeterai = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 15)

        pindahkanSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 16).split(semicolon, splitIndex)

        lokasiSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 17).split('\\n', splitIndex)

        lockSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 18).split(semicolon, splitIndex)

        catatanStamping = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitIndex)

		'ambil index tab yang sedang dibuka di chrome'
		currentTab = WebUI.getWindowIndex()

		'setting zoom menuju 80 persen'
		zoomSetting(80)

		'ganti fokus robot ke tab baru'
		WebUI.switchToWindowIndex(currentTab)

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
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
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
                                    'value'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 8), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nomor dokumen ')

                        'check ui dan excel pada nama dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentName'), 
                                    'value'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 9), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama dokumen ')

                        'check ui dan excel pada tipe dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docType'), 
                                    'value'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 11), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi tipe dokumen ')

                        'check ui dan excel pada tipe dokumen peruri'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docTypePeruri'), 
                                    'value'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 12), 
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

						if (catatanStamping[0] != '') {
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

                                    WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[
                                        j - 1])

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
                            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Manual Stamp', 
                                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                result = CustomKeywords.'connection.ManualStamp.getManualStamp'(conneSign, findTestData(
                                        excelPathManualStamp).getValue(GlobalVariable.NumofColm, 8), GlobalVariable.Tenant)

                                index = 0

                                ArrayList arrayMatch = []

                                'verify ref number'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                            8), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify document name'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                            9), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tanggal dokumen'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                            10), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                            11), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                            12), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                totalDocument = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 13).split(
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
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp', 
                                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathManualStamp).getValue(
                                            GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                                }
                            }
                            
                            inputEMeteraiMonitoring(conneSign)
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
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (((findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 2).replace(
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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 2) + ';') + 
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
    WebUI.setText(findTestObject('ManualStamp/input_documentNo'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
            8))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('ManualStamp/input_documentName'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
            9))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_documentDate'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
            10))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docType'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
            11))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docType'), Keys.chord(Keys.ENTER))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docTypePeruri'), findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
            12))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docTypePeruri'), Keys.chord(Keys.ENTER))

    'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
    String userDir = System.getProperty('user.dir')

    String filePath = userDir + findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 13)

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
        totalMeterai = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 15)

        for (int i = 1; i <= 10; i++) {
            ArrayList inputEMeterai = CustomKeywords.'connection.ManualStamp.getInputeMeteraiMonitoring'(conneSign, findTestData(
                    excelPathManualStamp).getValue(GlobalVariable.NumofColm, 8), GlobalVariable.Tenant)

            indexInput = 0

			index = 0
			
            if (((inputEMeterai[6]) == 'Failed') || ((inputEMeterai[6]) == 'Success')) {
				if (inputEMeterai[6] == 'Failed') {
					totalMeterai = 1
				}

                for (int j = 1; j <= totalMeterai; j++) {
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
                   // WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), inputEMeterai[
                   //     indexInput++])
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
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamping', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusWarning, (((findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + '<') + errormessage) + '>')

                        GlobalVariable.FlagFailed = 1
                    }
                    
                    'get stampduty data dari db'
                    result = CustomKeywords.'connection.eMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, findTestData(
                            excelPathManualStamp).getValue(GlobalVariable.NumofColm, 8))

                    'verify no dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NomorDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Dokumen ')

                    'verify tanggal dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TanggalDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Dokumen ')

                    'verify cabang'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Cabang')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang ')

                    'verify nama dokumen'
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

                    'verify proses materai'
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
							2) + ';') + GlobalVariable.ReasonFailedProsesStamping + ' yaitu status meterai adalah ' + inputMeterai[6] + ' pada nomor dokumen tersebut selama ' + (i * 15))
				} else {
					WebUI.delay(15)
				}
            }
        }
    }
}

