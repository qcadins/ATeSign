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
int countColmExcel = findTestData(excelPathViewUserOTP).columnNumbers

'get dates'
currentDate = LocalDate.now()

firstDateOfMonth = currentDate.withDayOfMonth(1)

int firstRun = 0

'looping message delivery report'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'set penanda error menjadi 0'
        GlobalVariable.FlagFailed = 0

        'check untuk login'
        if ((findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathViewUserOTP, ('Email') : 'Email Login'
                    , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)
            }
            
            firstRun = 1
        }
        
        if (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'input message delivery reportnya'
        inputViewUserOTP()

        'Input enter'
        WebUI.click(findTestObject('View User OTP/button_Search'))

        'check error log'
        if (checkErrorLog() == true) {
            continue
        }
        
        'jika hasil pencarian tidak memberikan hasil'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/View User OTP/label_Nama'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'Get row yang ada'
            getRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-view-codes > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper'))

            'get column yang ada'
            getColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-view-codes > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

            'db hasil pencarian'
            ArrayList result = CustomKeywords.'connection.ViewUserOTP.getFilterViewUserOTP'(conneSign, findTestData(excelPathViewUserOTP).getValue(
                    GlobalVariable.NumofColm, rowExcel('Email / NIK / No Hp')))

            'jika rownya tidak ada'
            if (getRow.size() == 0) {
                'Failed alasan save gagal tidak bisa diklik.'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada menu Message Delivery Report ')

                break
            }
            
            'array index'
            arrayIndex = 0

            'looping per row'
            for (i = 1; i <= getRow.size(); i++) {
                'looping per kolom'
                for (j = 1; j <= (getColumn.size() / getRow.size()); j++) {
                    'modify object dengan change span di akhir'
                    modifyObject = WebUI.modifyObjectProperty(findTestObject('Object Repository/MessageDeliveryReport/modifyObject'), 
                        'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-view-codes/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + j) + ']/div/p', true)

                    if (j == 5) {
                        if (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 
                        'Lihat OTP') {
                            WebUI.click(findTestObject('View User OTP/button_LihatOTP'))

                            'check error log'
                            if (checkErrorLog() == true) {
                                checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'connection.ViewUserOTP.getOtpCode'(
                                            conneSign, findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                rowExcel('Email / NIK / No Hp'))), 'null', false, FailureHandling.CONTINUE_ON_FAILURE), 
                                    'pada OTP di Aksi Lihat OTP')
                            } else {
                                WebUI.click(findTestObject('View User OTP/button_TutupLihatOtp'))

                                WebUI.click(findTestObject('View User OTP/button_LihatOTP'))

                                if (WebUI.verifyElementPresent(findTestObject('View User OTP/modal_LihatOtp'), GlobalVariable.TimeOut, 
                                    FailureHandling.CONTINUE_ON_FAILURE)) {
                                    otpFromFE = WebUI.getAttribute(findTestObject('View User OTP/text_otp'), 'value')

                                    checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'connection.ViewUserOTP.getOtpCode'(
                                                conneSign, findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                    rowExcel('Email / NIK / No Hp'))), otpFromFE, false, FailureHandling.CONTINUE_ON_FAILURE), 
                                        'pada OTP di Aksi Lihat OTP')

                                    'get current date'
                                    String currentDate = new Date().format('yyyy-MM-dd')

                                    ArrayList resultAccessLog = CustomKeywords.'connection.ViewUserOTP.getAccessLog'(conneSign, 
                                        'VIEW_OTP')

                                    ArrayList arrayMatch = []

                                    arrayIndexAccessLog = 0

                                    arrayMatch.add(WebUI.verifyMatch(resultAccessLog[arrayIndexAccessLog++], currentDate, false, 
                                            FailureHandling.CONTINUE_ON_FAILURE))

                                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toUpperCase().replace(
                                                'VIEW OTP', 'Lihat OTP').replace('VIEW RESET CODE', 'Lihat Reset Code'), 
                                            findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel(
                                                    'Action')), false, FailureHandling.CONTINUE_ON_FAILURE))

                                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toLowerCase(), 
                                            findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel(
                                                    'Email Login')).toLowerCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                                    'jika data db tidak sesuai dengan excel'
                                    if (arrayMatch.contains(false)) {
                                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusFailed, (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                        GlobalVariable.FlagFailed = 1
                                    }
                                } else {
                                    'tidak muncul popup'

                                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + ' Tidak muncul modal dan error log')

                                    GlobalVariable.FlagFailed = 1
                                }
                                
                                WebUI.click(findTestObject('View User OTP/button_TutupLihatOtp'))
                            }
                        } else if (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 
                        'Lihat Reset Code') {
                            WebUI.click(findTestObject('View User OTP/button_LihatResetCode'))

                            'check error log'
                            if (checkErrorLog() == true) {
                                checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'connection.ViewUserOTP.getOtpCode'(
                                            conneSign, findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                rowExcel('Email / NIK / No Hp'))), 'null', false, FailureHandling.CONTINUE_ON_FAILURE), 
                                    'pada Reset Code di Aksi Lihat Reset Code')
                            } else {
                                WebUI.click(findTestObject('View User OTP/button_TutupLihatOtp'))

                                WebUI.click(findTestObject('View User OTP/button_LihatResetCode'))

                                if (WebUI.verifyElementPresent(findTestObject('View User OTP/modal_LihatOtp'), GlobalVariable.TimeOut, 
                                    FailureHandling.CONTINUE_ON_FAILURE)) {
                                    otpFromFE = WebUI.getAttribute(findTestObject('View User OTP/text_otp'), 'value')

                                    checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'connection.ViewUserOTP.getResetCode'(
                                                conneSign, findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                    rowExcel('Email / NIK / No Hp'))), otpFromFE, false, FailureHandling.CONTINUE_ON_FAILURE), 
                                        'pada Reset Code di Aksi Lihat Reset Code')

                                    ArrayList resultAccessLog = CustomKeywords.'connection.ViewUserOTP.getAccessLog'(conneSign, 
                                        'VIEW_RESET_CODE')

                                    'get current date'
                                    String currentDate = new Date().format('yyyy-MM-dd')

                                    ArrayList arrayMatch = []

                                    arrayIndexAccessLog = 0

                                    arrayMatch.add(WebUI.verifyMatch(resultAccessLog[arrayIndexAccessLog++], currentDate, false, 
                                            FailureHandling.CONTINUE_ON_FAILURE))

                                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toUpperCase().replace(
                                                'VIEW OTP', 'Lihat OTP').replace('VIEW RESET CODE', 'Lihat Reset Code'), 
                                            findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel(
                                                    'Action')), false, FailureHandling.CONTINUE_ON_FAILURE))

                                    arrayMatch.add(WebUI.verifyMatch((resultAccessLog[arrayIndexAccessLog++]).toString().toLowerCase(), 
                                            findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel(
                                                    'Email Login')).toLowerCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                                    'jika data db tidak sesuai dengan excel'
                                    if (arrayMatch.contains(false)) {
                                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusFailed, (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                        GlobalVariable.FlagFailed = 1
                                    }
                                } else {
                                    'tidak muncul popup'

                                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + ' Tidak muncul modal dan error log')

                                    GlobalVariable.FlagFailed = 1
                                }
                            }
                            
                            WebUI.click(findTestObject('View User OTP/button_TutupLihatOtp'))
                        }
                    } else {
                        'verify tabel dengan db'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObject), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' terhadap kolom ke ' + j)
                    }
                }
            }
        } else {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                ' Data Tidak Ditemukan')

            GlobalVariable.FlagFailed = 1
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

def inputViewUserOTP() {
    'input vendor'
    WebUI.setText(findTestObject('View User OTP/input_EmailNIKNoHp'), findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, 
            rowExcel('Email / NIK / No Hp')))

    'Input enter'
    WebUI.sendKeys(findTestObject('View User OTP/input_EmailNIKNoHp'), Keys.chord(Keys.ENTER))
}

def checkPaging(Connection conneSign) {
    'click menu'
    WebUI.click(findTestObject('View User OTP/button_ViewUserOTP'))

    inputViewUserOTP()

    'Klik set ulang'
    WebUI.click(findTestObject('View User OTP/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('View User OTP/input_EmailNIKNoHp'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field search form tidak kereset - vendor')

    inputViewUserOTP()

    'klik search'
    WebUI.click(findTestObject('View User OTP/button_Search'))

    'ambil total trx berdasarkan filter yang telah disiapkan pada ui'
    totalTrxUI = WebUI.getText(findTestObject('View User OTP/label_TotalViewUserOTP')).split(' ', -1)

    'ambil total trx berdasarkan filter yang telah disiapkan pada db'
    totalTrxDB = CustomKeywords.'connection.ViewUserOTP.getTotalViewUserOTP'(conneSign, findTestData(excelPathViewUserOTP).getValue(
            GlobalVariable.NumofColm, rowExcel('Email / NIK / No Hp')))

    'verify total trx pada message delivery report'
    checkVerifyPaging(WebUI.verifyMatch(totalTrxUI[0], totalTrxDB, false, FailureHandling.CONTINUE_ON_FAILURE), ' total transaksi ui dan db tidak match')
}

def checkVerifyPaging(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
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

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('View User OTP/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('View User OTP/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'jika error message null, masuk untuk tulis error non-sistem'
        if (errormessage != null) {
            if (!(errormessage.contains('request OTP terlebih dahulu'))) {
                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + errormessage) + '>')
            }
            
            return true
        } else {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathViewUserOTP).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Error tidak berhasil ditangkap')
        }
    }
    
    false
}

