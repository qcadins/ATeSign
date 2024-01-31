import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDateTime as LocalDateTime
import java.time.format.DateTimeFormatter as DateTimeFormatter
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathDocumentMonitoring).columnNumbers

int firstRun = 0

'looping DocumentMonitoring'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'ambil email user yang akan digunakan untuk ambil tenant code'
        String email = findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))

        'deklarasi email konversi kedalam bentuk SHA256'
        String emailSHA256

        if (email.contains('@')) {
            emailSHA256 = email
        } else {
            emailSHA256 = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(email)
        }
        
        if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        GlobalVariable.Tenant = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, email)

        CustomKeywords.'connection.UpdateData.updateDBDocumentMonitoring'(conneSign, excelPathDocumentMonitoring, sheet)

        'get office code dari db'
        officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('No Kontrak')))

        if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Run With')).equalsIgnoreCase(
            'Normal')) {
            'check if email login case selanjutnya masih sama dengan sebelumnya'
            if (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
            findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 
            0)) || (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm - 1, rowExcel('Run With')) != 
            findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Run With')))) {
                'call test case login per case'
                WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathDocumentMonitoring
                        , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login'
                        , ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)

                'apakah cek paging diperlukan di awal run'
                if (GlobalVariable.checkPaging == 'Yes') {
                    'call function check paging'
                    checkPaging()
                }
                
                firstRun = 1
            }
            
            'click menu DocumentMonitoring'
            WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))
        } else {
            'setting untuk membuat lokasi default folder download'
            HashMap<String, String> chromePrefs = [:]

            chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

            chromePrefs.put('profile.default_content_setting_values.media_stream_camera', 1)

            RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

            String url

            'open browser'
            WebUI.openBrowser('')

            if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Run With')).equalsIgnoreCase(
                'Embed V1')) {
                'get aesKey general'
                aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

                'pembuatan message yang akan dienkrip'
                msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + email) + '\',\'tenantCode\':\'') + 
                GlobalVariable.Tenant) + '\'}')

                endcodedMsg = encryptValue(msg, aesKey)

                url = (((GlobalVariable.BaseLink + 'embed/inquiry?msg=') + endcodedMsg) + '&isMonitoring=true&isHO=1')
            } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Run With')).equalsIgnoreCase(
                'Embed V2')) {
                'get aesKey Tenant'
                aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

                currentDate = LocalDateTime.now()

                localeIndonesia = new Locale('id', 'ID')

                formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss', localeIndonesia)

                formattedDate = currentDate.format(formatter)

                'pembuatan message yang akan dienkrip'
                msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + email) + '\',\'timestamp\':\'') + 
                formattedDate) + '\'}')

                endcodedMsg = encryptValue(msg, aesKey)

                url = ((((GlobalVariable.BaseLink + 'embed/V2/inquiry?msg=') + endcodedMsg) + '&isHO=1&isMonitoring=true&tenantCode=') + 
                GlobalVariable.Tenant)
            }
            
            'navigate to url esign'
            WebUI.navigateToUrl(url)

            WebUI.delay(GlobalVariable.TimeOut)

            'maximized window'
            WebUI.maximizeWindow()
        }
        
        'set text nama Pelanggan'
        WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Nama Pelanggan')))

        'set text no kontrak'
        WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('No Kontrak')))

        'set text tanggal permintaan dari'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Permintaan Dari')))

        'set text tanggal permintaan sampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Permintaan Sampai')))

        'set text TanggalSelesaoSampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Selesai Dari')))

        'set text TanggalSelesaoSampai'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Selesai Sampai')))

        'set text tipe dok'
        WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Tipe Dokumen')))

        'enter untuk set tipe dok'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

        'set text status'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Status Dokumen')))

        'enter untuk set status'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

        'set text tanggal wilayah'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Wilayah')))

        'enter untuk set wilayah'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

        'set text tanggal cabang'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Cabang')))

        'enter untuk set cabang'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))

        'click untuk menutup ddl'
        WebUI.click(findTestObject('DocumentMonitoring/label_Judul'))

        'click button cari'
        WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))

        'check if action yang dilakukan sesuai excel'
        if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'View Dokumen')) {
            'click button view dokumen'
            WebUI.click(findTestObject('DocumentMonitoring/button_View'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + errorLog) + '>')

                GlobalVariable.FlagFailed = 1
            } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_NoKontrakView'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'check if no kontrak sama dengan inputan excel'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/label_NoKontrakView')), 
                        'No Kontrak ' + findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel(
                                'No Kontrak')), false, FailureHandling.CONTINUE_ON_FAILURE), ' dokumen yang di view berbeda')

                'click button Kembali'
                WebUI.click(findTestObject('DocumentMonitoring/button_Kembali'))
            } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_View'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                checkVerifyEqualOrMatch(false, ' button view tidak berfungsi')
            }
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Download')) {
            'click button download'
            WebUI.click(findTestObject('DocumentMonitoring/button_Download'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + errorLog) + '>')

                GlobalVariable.FlagFailed = 1
            }
            
            'check isfiled downloaded'
            if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathDocumentMonitoring).getValue(
                    GlobalVariable.NumofColm, rowExcel('Delete Downloaded File ?'))) == false) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedDownload)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'View Signer')) {
            'click button view signer'
            WebUI.click(findTestObject('DocumentMonitoring/button_Signer'))

            'get row'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'get signer data dari db'
            result = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringSigner'(conneSign, findTestData(
                    excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('No Kontrak')).toUpperCase())

            'looping table yang muncul pada ui'
            for (index = 1; index <= variable.size(); index++) {
                'modify object user type'
                modifyObjectUserType = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 'xpath', 
                    'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    index) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

                arrayIndex = result.indexOf(WebUI.getText(modifyObjectUserType))

                userType = (result[arrayIndex++])

                if (WebUI.getText(modifyObjectUserType).equalsIgnoreCase(userType)) {
                    'modify object user name'
                    modifyObjectUserName = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

                    'modify object user email'
                    modifyObjectUserEmail = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

                    'modify object register status'
                    modifyObjectRegisterStatus = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

                    'modify object sign status'
                    modifyObjectSignStatus = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

                    'modify object sign Date'
                    modifyObjectSignDate = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/modifyObject'), 
                        'xpath', 'equals', ('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        index) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div', true)

                    'verify user type'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserType), userType, false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' User Type')

                    'verify user name'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserName), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' User Name')

                    'verify user email'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUserEmail), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' User Email')

                    'verify register status'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectRegisterStatus), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Register Status')

                    'verify sign status'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectSignStatus), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign Status')

                    'verify sign date'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectSignDate), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' Sign date')
                }
            }
            
            'click button X'
            WebUI.click(findTestObject('DocumentMonitoring/button_X'))
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Kirim Ulang Notifikasi')) {
            'click button KirimUlangNotifikasi'
            WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + errorLog) + '>')

                GlobalVariable.FlagFailed = 1
            } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_YaProses'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'click button tidak batalkan'
                WebUI.click(findTestObject('DocumentMonitoring/button_TidakBatalkan'))

                'click button KirimUlangNotifikasi'
                WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

                'click button ya proses'
                WebUI.click(findTestObject('DocumentMonitoring/button_YaProses'))

                'check if pop up success message'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_OK'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('DocumentMonitoring/button_OK'))

                    'lihat apakah perlu pengecekan ke DB'
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'cek balance mutation dan juga pemotongan saldo'
                        checkBalanceMutation(conneSign, emailSHA256)
                    }
                } else {
                    'check if error alert present'
                    if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        'get reason dari error log'
                        errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                        'Write To Excel GlobalVariable.StatusFailed and errorLog'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + '<') + errorLog) + '>')

                        GlobalVariable.FlagFailed = 1
                    }
                    
                    if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/PopupMessage'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        'get text dari popup message'
                        errorLog = WebUI.getText(findTestObject('DocumentMonitoring/PopUpMessage'))

                        'Write To Excel GlobalVariable.StatusFailed and errorLog'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + '<') + errorLog) + '>')

                        GlobalVariable.FlagFailed = 1
                    }
                }
            }
        } else if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Start Stamping') || findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Retry Stamping')) {
            if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'Start Stamping') {
                'click button start stamping'
                WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))
            } else {
                'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
                WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping'))
            }
            
            'Jika start stamping'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'klik cancel'
                WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_cancelStartStamping'))

                if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 
                'Start Stamping') {
                    'click button start stamping'
                    WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))
                } else {
                    'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
                    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping'))
                }
                
                'klik yes'
                WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_yesStartStamping'))

                'diberikan delay 10 dengan loading'
                WebUI.delay(10)

                'jika ada error log'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'ambil teks errormessage'
                    errormessage = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                    'Tulis di excel itu adalah error'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')
                }
                
                'jika start stamping muncul'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'klik start ok untuk start stamping'
                    WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_OKStartStamping'))

                    'looping dari 1 hingga 12'
                    for (i = 1; i <= 12; i++) {
                        'mengambil value db proses ttd'
                        int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, findTestData(
                                excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('No Kontrak')))

                        'jika proses materai gagal (51)'
                        if ((prosesMaterai == 51) || (prosesMaterai == 61)) {
                            'get reason gailed error message untuk stamping'
                            errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, findTestData(
                                    excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('No Kontrak')))

                            'Write To Excel GlobalVariable.StatusFailed and errormessage'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                                errorMessageDB.toString())

                            GlobalVariable.FlagFailed = 1

                            break
                        } else if ((prosesMaterai == 53) || (prosesMaterai == 63)) {
                            'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                            WebUI.delay(3)

                            'Mengambil value total stamping dan total meterai'
                            ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                                conneSign, findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('No Kontrak')))

                            'declare arraylist arraymatch'
                            arrayMatch = []

                            'dibandingkan total meterai dan total stamp'
                            arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[
                                    1], false, FailureHandling.CONTINUE_ON_FAILURE))

                            ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
                                conneSign, findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('No Kontrak')), 'Stamping')

                            'lakukan loop untuk pengecekan data'
                            for (int i = 0; i < (officeRegionBline.size() / 2); i++) {
                                'verify business line dan office code'
                                arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                                        3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            'jika data db tidak bertambah'
                            if (arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                GlobalVariable.FlagFailed = 1
                            }
                            
                            break
                        } else {
                            'Jika bukan 51 dan 53, maka diberikan delay 20 detik'
                            WebUI.delay(10)

                            'Jika looping berada di akhir, tulis error failed proses stamping'
                            if (i == 12) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((((findTestData(excelPathDocumentMonitoring).getValue(
                                        GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                    ' dengan jeda waktu ') + (i * 12)) + ' detik ')

                                GlobalVariable.FlagFailed = 1
                            }
                        }
                    }
                }
            }
        }
        
        'cek apakah perlu melakukan cancel doc'
        if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Doc?')).equalsIgnoreCase(
            'Yes')) {
            'panggil fungsi cancel doc'
            cancelDoc(conneSign)
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def cancelDoc(Connection conneSign) {
    'jika action yang dilakukan sebelumnya adalah view dokumen'
    if (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
        'View Dokumen')) {
        'set text no kontrak'
        WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('No Kontrak')))

        'click button cari'
        WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
    }
    
    'jika status text adalah complete'
    if (WebUI.getText(findTestObject('DocumentMonitoring/lblTable_status'), FailureHandling.OPTIONAL).equalsIgnoreCase('Complete')) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            'Cancel Document tidak bisa karena status Complete')

        GlobalVariable.FlagFailed = 1

        return null
    }
    
    'klik pada tombol cancel doc'
    WebUI.click(findTestObject('DocumentMonitoring/button_CancelDoc'))

    'klik pada tombol batalkan'
    WebUI.click(findTestObject('DocumentMonitoring/button_TidakBatalkan'))

    'klik pada tombol cancel doc'
    WebUI.click(findTestObject('DocumentMonitoring/button_CancelDoc'))

    'klik pada tombol proses'
    WebUI.click(findTestObject('DocumentMonitoring/button_YaProses'))

    'ambil message yang muncul pada popup'
    String popupMsg = WebUI.getText(findTestObject('DocumentMonitoring/PopUpMessage'), FailureHandling.OPTIONAL)

    'klik pada tombol OK'
    WebUI.click(findTestObject('DocumentMonitoring/button_OKcancelDoc'))

    'jika message yang muncul adalah sukses'
    if (popupMsg.equalsIgnoreCase('Dokumen berhasil dibatalkan')) {
        'lakukan pengecekan ke DB'
        checkVerifyEqualOrMatch(WebUI.verifyMatch('0', CustomKeywords.'connection.DocumentMonitoring.getCancelDocStatus'(
                    conneSign, findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('No Kontrak'))), 
                false, FailureHandling.CONTINUE_ON_FAILURE), 'Pengecekan ke DB Cancel Doc gagal')
    } else {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + '<') + popupMsg) + '>')

        GlobalVariable.FlagFailed = 1
    }
}

def checkPaging() {
    'click menu DocumentMonitoring'
    WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

    'set text nama Pelanggan'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'nama pelanggan')

    'set text no kontrak'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), 'no kontrak')

    'set text tanggal permintaan dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), '2023-01-01')

    'set text tanggal permintaan sampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), '2023-01-31')

    'set text TanggalSelesaoSampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), '2023-01-01')

    'set text TanggalSelesaoSampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), '2023-01-31')

    'set text tipe dok'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), 'dokumen')

    'enter untuk set tipe dok'
    WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

    'set text status'
    WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), 'complete')

    'enter untuk set status'
    WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

    'set text tanggal wilayah'
    WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), 'bogor')

    'enter untuk set wilayah'
    WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

    'set text tanggal cabang'
    WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), 'irwan')

    'enter untuk set cabang'
    WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('DocumentMonitoring/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_NamaPelanggan'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_NoKontrak'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_TipeDok'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Status'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Wilayah'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('DocumentMonitoring/input_Cabang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'click field lain untuk close ddl'
    WebUI.click(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'))

    'click button cari'
    WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))

    WebUI.delay(3)

    'check if ada paging'
    if (WebUI.verifyElementVisible(findTestObject('DocumentMonitoring/button_NextPage'), FailureHandling.OPTIONAL)) {
        'click next page'
        WebUI.click(findTestObject('DocumentMonitoring/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 2', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('DocumentMonitoring/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click last page'
        WebUI.click(findTestObject('DocumentMonitoring/button_LastPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('DocumentMonitoring/label_TotalData')).split(' ', -1)[
            0]) / 10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage + additionalRoundUp), false, FailureHandling.CONTINUE_ON_FAILURE))

        'click first page'
        WebUI.click(findTestObject('DocumentMonitoring/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DocumentMonitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkBalanceMutation(Connection conneSign, String emailSigner) {
    'cek apakah perlu untuk pengecekan DB'
    if (GlobalVariable.checkStoreDB == 'Yes') {
        emailServiceOnTenant = CustomKeywords.'connection.DataVerif.getEmailService'(conneSign, GlobalVariable.Tenant)

        fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner)

        notifTypeDBResendSignNotifNormal = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF', GlobalVariable.Tenant)

        notifTypeDBResendSignNotifEmbedV1 = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF_EMBED_V1', GlobalVariable.Tenant)

        notifTypeDBResendSignNotifEmbedV2 = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF_EMBED_V2', GlobalVariable.Tenant)

        mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

		String runWithType = findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?'));
		
		if (runWithType.equals('Login Via Admin Client')) {
			notifTypeDB = notifTypeDBResendSignNotifNormal
		} else if (runWithType.equals('Embed V1')) {
			notifTypeDB = notifTypeDBResendSignNotifEmbedV1
		} else if (runWithType.equals('Embed V2')) {
			notifTypeDB = notifTypeDBResendSignNotifEmbedV2
		}
        
        if (notifTypeDB == '0' || notifTypeDB == 'Level Tenant') {
            if (mustUseWAFirst == '1') {
                'menggunakan saldo wa'
                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                    fullNameUser)

                if (balmut.size() == 0) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                }
                
                if ((balmut[10]) != -1) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
                }
            } else {
                if (emailServiceOnTenant == 1) {
                    useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                    if (useWAMessage == '1') {
                        'menggunakan saldo wa'
                        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                            fullNameUser)

                        if (balmut.size() == 0) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                        }
                        
                        if ((balmut[10]) != -1) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
                        }
                    } else if (useWAMessage == '0') {
                        'menggunakan saldo wa'
                        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'SMS Notif', 
                            fullNameUser)

                        if (balmut.size() == 0) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
                        }
                        
                        if ((balmut[10]) != -1) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Saldo SMS tidak terpotong')
                        }
                    }
                }
            }
        } else {
            'menggunakan saldo wa'
            ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, notifTypeDB, fullNameUser)

            if (balmut.size() == 0) {
                GlobalVariable.FlagFailed = 1

                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
            }
            
            if ((balmut[10]) != -1) {
                GlobalVariable.FlagFailed = 1

                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + 'Saldo SMS tidak terpotong')
            }
        }
        
        WebUI.delay(1)

        'declare arraylist arraymatch'
        ArrayList arrayMatch = []

        'ambil data last transaction dari DB'
        ArrayList resultDB = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, findTestData(excelPathDocumentMonitoring).getValue(
                GlobalVariable.NumofColm, rowExcel('Email Login')), 'Document')

        'declare arrayindex'
        arrayindex = 0

        'lakukan loop untuk pengecekan data'
        for (int i = 0; i < (resultDB.size() / 2); i++) {
            'verify business line dan office code'
            arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
        }
        
        'jika data db tidak sesuai dengan excel'
        if (arrayMatch.contains(false)) {
            GlobalVariable.FlagFailed = 1

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathDocumentMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 'pada pengecekan Office/Region/Bline')
        }
    }
}

def encryptValue(String value, String aesKey) {
    'enkripsi msg'
    CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

