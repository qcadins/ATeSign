import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.By as By
import java.time.LocalDate as LocalDate
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathMessageDeliveryReport).columnNumbers

'get dates'
currentDate = LocalDate.now()

firstDateOfMonth = currentDate.withDayOfMonth(1)

int firstRun = 0

'looping message delivery report'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 
    0) {
        break
    } else if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'set penanda error menjadi 0'
        GlobalVariable.FlagFailed = 0

        'get tenant dari excel percase'
        GlobalVariable.Tenant = findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel(
                'Tenant Login'))

        'get psre dari excel percase'
        GlobalVariable.Psre = findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check untuk login'
        if ((findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
        findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 
        0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathMessageDeliveryReport
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)

                'get ddl vendor'
                ArrayList resultVendor = CustomKeywords.'connection.MessageDeliveryReport.getDDLVendor'(conneSign, GlobalVariable.Tenant)

                'check dll vendor'
                checkDDL(findTestObject('MessageDeliveryReport/input_vendor'), resultVendor, 'DDL Vendor')

                'get ddl message media'
                ArrayList resultMessageMedia = CustomKeywords.'connection.MessageDeliveryReport.getDDLMessageMedia'(conneSign)

                'check ddl message media'
                checkDDL(findTestObject('MessageDeliveryReport/input_messageMedia'), resultMessageMedia, 'DDL Message Media')
            }
            
            firstRun = 1
        }
        
        settingzoom()

        if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'input message delivery reportnya'
        inputMessageDeliveryReport()

        'Input enter'
        WebUI.click(findTestObject('MessageDeliveryReport/button_search'))

        'check error log'
        if (checkErrorLog() == true) {
            continue
        }
        
        'jika hasil pencarian tidak memberikan hasil'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/MessageDeliveryReport/label_TableVendor'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'Get row yang ada'
            getRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-message-delivery-report > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'get column yang ada'
            getColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-message-delivery-report > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

            'db hasil pencarian'
            ArrayList result = CustomKeywords.'connection.MessageDeliveryReport.getFilterMessageDeliveryReport'(conneSign, 
                GlobalVariable.Tenant, storeHashMapForVerify())

            'jika rownya tidak ada'
            if (getRow.size() == 0) {
                'Failed alasan save gagal tidak bisa diklik.'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada menu Message Delivery Report ')

                break
            }
            
            'array index'
            arrayIndex = 0

            'looping per row'
            for (i = 1; i <= getRow.size(); i++) {
                'looping per kolom'
                for (j = 1; j <= getColumn.size(); j++) {
                    'jika column yang kedua'
                    if (j == 2) {
                        'modify object dengan change span di akhir'
                        modifyObject = WebUI.modifyObjectProperty(findTestObject('Object Repository/MessageDeliveryReport/modifyObject'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-message-delivery-report/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + j) + ']/div/span', true)
                    } else {
                        'modify object kolom'
                        modifyObject = WebUI.modifyObjectProperty(findTestObject('Object Repository/MessageDeliveryReport/modifyObject'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-message-delivery-report/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + j) + ']/div/p', true)
                    }
                    
                    'verify tabel dengan db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObject), result[arrayIndex++], false, 
                            FailureHandling.CONTINUE_ON_FAILURE), ' terhadap kolom ke ' + j)
                }
            }
        }
        
        'jika tidak ada failed'
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
                1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        }
    }
}

'tutup browser'
WebUI.closeBrowser()

def inputMessageDeliveryReport() {
    'input vendor'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_vendor'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Vendor')))

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_vendor'), Keys.chord(Keys.ENTER))

    'Input tipe message media'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_messageMedia'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Message Media')))

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_messageMedia'), Keys.chord(Keys.ENTER))

    'Input report time start'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_reportTimeStart'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Report Time Start')))

    'Input report time end'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_reportTimeEnd'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Report Time End')))

    'Input tipe dokumen'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_deliveryStatus'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Status Delivery')))

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_deliveryStatus'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('MessageDeliveryReport/input_recipient'), findTestData(excelPathMessageDeliveryReport).getValue(
            GlobalVariable.NumofColm, rowExcel('Recipient')))
}

def checkPaging(Connection conneSign) {
    settingzoom()

    inputMessageDeliveryReport()

    'Klik set ulang'
    WebUI.click(findTestObject('MessageDeliveryReport/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_vendor'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - vendor')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_messageMedia'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - message Media')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_reportTimeStart'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - report time start')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_reportTimeEnd'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - report time end')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_deliveryStatus'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - delivery status')

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/input_recipient'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - recipient')

    'click ddl vendor'
    WebUI.click(findTestObject('MessageDeliveryReport/input_vendor'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('MessageDeliveryReport/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe saldo')

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_vendor'), Keys.chord(Keys.ENTER))

    'click ddl message media'
    WebUI.click(findTestObject('MessageDeliveryReport/input_messageMedia'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('MessageDeliveryReport/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe transaksi')

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_messageMedia'), Keys.chord(Keys.ENTER))

    'click ddl delivery status'
    WebUI.click(findTestObject('MessageDeliveryReport/input_deliveryStatus'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('MessageDeliveryReport/selected_DDLDeliveryStatus')), 
            'All', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - DDL tipe dokumen')

    'Input enter'
    WebUI.sendKeys(findTestObject('MessageDeliveryReport/input_deliveryStatus'), Keys.chord(Keys.ENTER))

    'klik search'
    WebUI.click(findTestObject('MessageDeliveryReport/button_search'))

    'ambil total trx berdasarkan filter yang telah disiapkan pada ui'
    totalTrxUI = WebUI.getText(findTestObject('MessageDeliveryReport/label_TotalMessageDeliveryReport')).split(' ', -1)

    'ambil total trx berdasarkan filter yang telah disiapkan pada db'
    totalTrxDB = CustomKeywords.'connection.MessageDeliveryReport.getTotalMessageDeliveryReport'(conneSign, GlobalVariable.Tenant)

    'verify total trx pada message delivery report'
    checkVerifyPaging(WebUI.verifyMatch(totalTrxUI[0], totalTrxDB, false, FailureHandling.CONTINUE_ON_FAILURE), ' total transaksi ui dan db tidak match')

    if (Integer.parseInt(totalTrxUI[0]) > 10) {
        'click next page'
        WebUI.click(findTestObject('MessageDeliveryReport/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page selanjutnya tidak berfungsi')

        'click prev page'
        WebUI.click(findTestObject('MessageDeliveryReport/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page sebelumnya tidak berfungsi')

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-message-delivery-report > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click last page'
        WebUI.click(findTestObject('MessageDeliveryReport/button_LastPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('MessageDeliveryReport/label_TotalData')).split(' ', 
                -1)[0]) / 10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), Math.round(lastPage + additionalRoundUp).toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            'gagal verify page di page terakhir')

        'click first page'
        WebUI.click(findTestObject('MessageDeliveryReport/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('MessageDeliveryReport/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE), ' button page pertama tidak berfungsi')
    }
}

def checkVerifyPaging(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedPaging) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('MessageDeliveryReport/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('MessageDeliveryReport/modifyObject'), 'xpath', 'equals', 
            ((('//*[@id=\'' + id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def settingzoom() {
    'ambil index tab yang sedang dibuka di chrome'
    int currentTab = WebUI.windowIndex

    'setting zoom menuju 70 persen'
    zoomSetting(70)

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab)

    'focus ke menu'
    WebUI.click(findTestObject('MessageDeliveryReport/menu_MessageDeliveryReport'))

    'click menu MessageDeliveryReport'
    WebUI.click(findTestObject('MessageDeliveryReport/menu_MessageDeliveryReport'))

    'ambil index tab yang sedang dibuka di chrome'
    currentTab = WebUI.windowIndex

    'setting zoom menuju 100 persen'
    zoomSetting(100)

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab)

    'click ddl bahasa'
    WebUI.click(findTestObject('Login/button_bahasa'))

    'click english'
    WebUI.click(findTestObject('Login/button_English'))
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def zoomSetting(int percentage) {
    Float percentageZoom = percentage / 100

    WebDriver driver = DriverFactory.webDriver

    'buka tab baru'
        ((driver) as JavascriptExecutor).executeScript('window.open();')

    'ambil index tab yang sedang dibuka di chrome'
    int currentTab = WebUI.windowIndex

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab + 1)

    driver.get('chrome://settings/')

        ((driver) as JavascriptExecutor).executeScript(('chrome.settingsPrivate.setDefaultZoom(' + percentageZoom) + 
        ');')

    'close tab baru'
        ((driver) as JavascriptExecutor).executeScript('window.close();')
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'jika error message null, masuk untuk tulis error non-sistem'
        if (errormessage != null) {
            if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + errormessage) + '>')

                return true
            }
        } else {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Error tidak berhasil ditangkap')
        }
    }
    
    false
}

def storeHashMapForVerify() {
    HashMap<String, String> result = [:]

    String code

    result.put('Vendor', findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Vendor')))

    result.put('Message Media', findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel(
                'Message Media')))

    result.put('Report Time Start', findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel(
                'Report Time Start')))

    result.put('Report Time End', findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel(
                'Report Time End')))

    if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status Delivery')).toString().equalsIgnoreCase(
        'Not Started')) {
        code = '0'
    } else if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status Delivery')).toString().equalsIgnoreCase(
        'Waiting')) {
        code = '1'
    } else if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status Delivery')).toString().equalsIgnoreCase(
        'Failed')) {
        code = '2'
    } else if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status Delivery')).toString().equalsIgnoreCase(
        'Delivered')) {
        code = '3'
    } else if (findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Status Delivery')).toString().equalsIgnoreCase(
        'Read')) {
        code = '4'
    } else {
        code = ''
    }
    
    result.put('Status Delivery', code.replace('"', ''))

    result.put('Recipient', findTestData(excelPathMessageDeliveryReport).getValue(GlobalVariable.NumofColm, rowExcel('Recipient')))

    	result
}

