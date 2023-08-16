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

indexForCatatanStamp = 0

'memanggil test case login untuk admin wom dengan Admin Client'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathManualStamp, ('sheet') : 'Manual Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathManualStamp).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
        //        emailPenandaTangan = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitIndex)
        //		
        //		editNamaAfterSearch = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 20).split(semicolon,
        //			splitIndex)
        //		
        //        namaPenandaTangan = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitIndex)
        //
        //        tipeTandaTangan = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 22).split(semicolon, splitIndex)
        //
        //		totalTandaTangan = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitIndex)
        //		
        //        pindahkanSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 24).split(semicolon, splitIndex)
        //
        //        lokasiSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 25).split('\\n', splitIndex)
        //
        //        lockSignBox = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 26).split(semicolon, splitIndex)
        //
        //        catatanStamping = findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 27).split(semicolon, splitIndex)
    } else if (findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted') || 
    findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Warning')) {
        'declare flag failed'
        GlobalVariable.FlagFailed = 0

        'Inisialisasi array dan variable'
        ArrayList<String> namaTandaTangan = [], notelpTandaTangan = []

        indexEmail = 0

        'inisiasi index menjadi 8 untuk modify object ketika tidak pada e-meterai'
        index = 8

        'Inisialisasi variable yang dibutuhkan'

        'ambil index tab yang sedang dibuka di chrome'
        int currentTab = WebUI.getWindowIndex()

        zoomSetting(80)

        'ganti fokus robot ke tab baru'
        WebUI.switchToWindowIndex(currentTab)

        'Klik tombol menu manual sign'
        WebUI.click(findTestObject('ManualStamp/menu_ManualStamp'))

        'Jika kolomnya berada pada kedua'
        if (GlobalVariable.NumofColm == 2) {
            inputCancel(conneSign)
        }
        
        'Pengecekan apakah masuk page manual sign'
        if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_stampingMeterai'), GlobalVariable.TimeOut)) {
            'Input form yang ada pada page'
            inputForm()

            'check save ada attribute disabled'
            if (WebUI.verifyElementHasAttribute(findTestObject('ManualStamp/button_Selanjutnya'), 'disabled', GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
            }
			else {
				
				WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))
				
                if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_KonfirmasiNext'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    WebUI.click(findTestObject('ManualStamp/button_BackNext'))

                    WebUI.verifyElementPresent(findTestObject('ManualStamp/button_Selanjutnya'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)

                    WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))
					
					WebUI.click(findTestObject('ManualStamp/button_Next'))
                }
            }
        }
    }
} //go to the chrome settings
//set the zoom to 70%

def inputCancel(Connection conneSign) {
    inputForm()

    'get data tipe-tipe pembayaran secara asc'
    ArrayList<String> docTypeDB = CustomKeywords.'connection.ManualStamp.getDocType'(conneSign)

    'check ddl tipe pembayaran'
    checkDDL(findTestObject('ManualStamp/input_docType'), docTypeDB, ' pada DDL tipe dokumen ')

    'get data tipe-tipe pembayaran secara asc'
    ArrayList<String> docTypePeruriDB = CustomKeywords.'connection.ManualStamp.getDocTypePeruri'(conneSign)

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Sign', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList<String> list = []

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

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Permintaan tanda tangan berhasil dibuat.'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (((findTestData(excelPathManualStamp).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            return true
        }
    }
    
    return false
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

