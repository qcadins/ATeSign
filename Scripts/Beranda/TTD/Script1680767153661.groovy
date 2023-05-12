import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import customizeKeyword.CheckSaveProccess as checkSaveProccess
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'Inisialisasi array-array yang dibutuhkan'

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'Inisialisasi array untuk Listotp'
ArrayList<String> listOTP = new ArrayList<String>()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'Inisialisasi variable yang dibutuhkan'
String nokontrak

String saldo_before

String saldo_after

String otp_before

String otp_after

String documentTemplateName

String notelpsigner

nokontrak = ''

documentTemplateName = ''

int totaldocsign

boolean isbreak

'looping berdasarkan jumlah dokumen yang dikirimkan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 2 /*findTestData(excelPathFESignDocument).getColumnNumbers()*/ ; (GlobalVariable.NumofColm)++) {
    'Call API Send doc'
    WebUI.callTestCase(findTestCase('Beranda/ResponseAPISendDoc'), [:], FailureHandling.CONTINUE_ON_FAILURE)

    'Jika Responsenya kosong, atau hitnya gagal'
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 5) == '') {
        'loop selanjutnya'
        continue
    }
    
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 60) == '') {
        countResend = 0
    } else {
        countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 60).toInteger()
    }
    
    'ambil db checking ke UI Beranda'
    ArrayList<String> SendtoSign = CustomKeywords.'connection.DataVerif.getDataSendtoSign'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, 5))

    'Mengambil email berdasarkan documentId'
    ArrayList<String> emailSigner = CustomKeywords.'connection.DataVerif.getEmailLogin'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, 5)).split(';', -1)

    'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
    int jumlahsignertandatangan = 0

    'looping email signer'
    for (int o = 1; o <= emailSigner.size(); o++) {
        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

        'mengambil saldo before'
        saldo_before = checkSaldoSign()

        'mengambil saldo otp before'
        otp_before = checkSaldoOtp()

        'tutup browsernya'
        WebUI.closeBrowser()

        'call Test Case untuk login sebagai user berdasarkan doc id'
        WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [('email') : emailSigner[(o - 1)]], FailureHandling.STOP_ON_FAILURE)

        'Jika document tersebut tidak membutuhkan tanda tangan'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 47) == 'No') {
            continue
        }
        
        'Klik checkbox ttd untuk semua'
        WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

        'Klik button ttd bulk'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

        'klik tombol Batal'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))

        'refresh buat reset nav bar selanjutnya'
        WebUI.refresh()

        'Jika bukan di page 1, verifikasi menggunakan button Lastest'

        'get row lastest'
        variable_lastest = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

        'get row lastest'
        modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable_lastest.size()) + ']/a/i', true)

        'jika btn lastest dapat diclick'
        if (WebUI.verifyElementClickable(modifyobjectbtnLastest, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyobjectbtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 50) == '') {
            totaldocsign = 1
        } else {
            totaldocsign = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 50).toInteger()
        }
        
        'get row beranda'
        variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        changePage = false

        'Jika check documentnya all'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
            'looping untuk mengambil seluruh row'
            for (int k = 1; k <= variable.size(); k++) {
                'deklarasi arrayindex untuk pemakaian'
                arrayindex = 0

                'modify object text document template name di beranda'
                modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    k) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

                'modify object text document template tipe di beranda'
                modifyObjecttextdocumenttemplatetipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    k) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                'modify object btn TTD Dokumen di beranda'
                modifyObjectcheckboxttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    k) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

                'modify object lbl tanggal permintaan'
                modifyObjectlbltglpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    k) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/span', true)

                'modify object text no kontrak di beranda'
                modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    k) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                if (k > (variable.size() - totaldocsign)) {
                    if (k == variable.size()) {
                        'Jika datanya match dengan db, mengenai referal number'
                        if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnumber), SendtoSign[arrayindex++], false, 
                            FailureHandling.OPTIONAL)) {
                            'Mengneai tipe dokumen'
                            if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatetipe), SendtoSign[arrayindex++], 
                                false, FailureHandling.OPTIONAL)) {
                                'Mengenai tanggal permintaan'
                                if (WebUI.verifyMatch(WebUI.getText(modifyObjectlbltglpermintaan), SendtoSign[arrayindex++], 
                                    false, FailureHandling.OPTIONAL)) {
                                    'Jika tidak ketemu hingga akhir row'
                                    checkVerifyEqualorMatch(true)
                                }
                            }
                        }
                    }
                    
                    WebUI.click(modifyObjectcheckboxttd)

                    if (documentTemplateName == '') {
                        documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

                        nokontrak = WebUI.getText(modifyObjecttextrefnumber)
                    } else {
                        documentTemplateName = ((WebUI.getText(modifyObjecttextdocumenttemplatename) + ';') + documentTemplateName)

                        nokontrak = ((WebUI.getText(modifyObjecttextrefnumber) + ';') + nokontrak)
                    }
                }
            }
            
            'Klik button ttd bulk'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

            documentTemplateName_perdoc = documentTemplateName.split(';', -1)

            for (int c = 0; c < totaldocsign; c++) {
                if (totaldocsign > documentTemplateName_perdoc.size()) {
                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedSignGagal)

                    break
                }
                
                'modify object btn Nama Dokumen '
                modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 
                    'xpath', 'equals', ('id("ngb-nav-' + (c + 2)) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

                'verify nama dokumen massal dengan nama dokumen di paging'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateName_perdoc[totaldocsign - (c + 1)], false))
            }
        } else {
            'asumsi document yang dipilih selalu 1 karena berdasarkan Send Document API doc. Kesimpulan : 1 doc many signer'
            for (int j = 1; j <= variable.size(); j++) {
                'deklarasi arrayindex untuk pemakaian'
                arrayindex = 0

                'modify object text document template name di beranda'
                modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

                'modify object text document template tipe di beranda'
                modifyObjecttextdocumenttemplatetipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                'modify object btn TTD Dokumen di beranda'
                modifyObjectcheckboxttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

                'modify object lbl tanggal permintaan'
                modifyObjectlbltglpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/span', true)

                'modify object text no kontrak di beranda'
                modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    j) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                'Jika datanya match dengan db, mengenai referal number'
                if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnumber), SendtoSign[arrayindex++], false, FailureHandling.OPTIONAL)) {
                    'Mengneai tipe dokumen'
                    if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatetipe), SendtoSign[arrayindex++], 
                        false, FailureHandling.OPTIONAL)) {
                        'Mengenai tanggal permintaan'
                        if (WebUI.verifyMatch(WebUI.getText(modifyObjectlbltglpermintaan), SendtoSign[arrayindex++], false, 
                            FailureHandling.OPTIONAL)) {
                            'Ambil document template name, nomor kontrak dan klik checkbox khusus 1 signer'
                            documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

                            nokontrak = WebUI.getText(modifyObjecttextrefnumber)

                            WebUI.click(modifyObjectcheckboxttd)
                        }
                    }
                    
                    'Jika tidak ketemu hingga akhir row'
                } else if (j == variable.size()) {
                    'Input verifynya false'
                    checkVerifyEqualorMatch(false)
                }
            }
            
            'Klik button ttd bulk'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

            'modify object btn Nama Dokumen '
            modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
                'equals', ('id("ngb-nav-' + 2) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

            'check popup'
            if (checkPopup() == true) {
                continue
            }
            
            'verify nama dokumen massal dengan nama dokumen di paging'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateName, false, 
                    FailureHandling.CONTINUE_ON_FAILURE))
        }
        
        'Check konfirmasi tanda tangan'
        checkKonfirmasiTTD()

        'jika page belum pindah'
        if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL))) {
            'Jika tidak ada, maka datanya tidak ada, atau save gagal'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal)
        } else {
            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
                documentTemplateName_perdoc = documentTemplateName.split(';', -1)

                for (int i = 1; i <= documentTemplateName_perdoc.size(); i++) {
                    'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
                    modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                        'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + i) + ']/label', true)

                    'verify nama dokumen massal dengan nama dokumen di paging'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), 
                            documentTemplateName_perdoc[(documentTemplateName_perdoc.size() - (i))], false))
                }
            } else {
                'verify nama dokumen massal dengan nama dokumen di paging'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi')), 
                        documentTemplateName, false))
            }
            
            WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

            'Klik button proses'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

            'mereset array index'
            arrayindex = 0

            if (checkerrorLog() == true) {
                continue
            }
            
            'Jika error log tidak muncul, Jika verifikasi penanda tangan tidak muncul'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL))) {
                'Custom keyword mengenai savenya gagal'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal)
            } else {
                'Jika verifikasi penanda tangan muncul, Verifikasi antara email yang ada di UI dengan db'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_EmailAfterKonfirmasi'), 
                            'value'), emailSigner[(o - 1)], false, FailureHandling.CONTINUE_ON_FAILURE))

                'Get text nomor telepon'
                notelpsigner = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_phoneNoAfterKonfirmasi'), 'value')

                'input text password'
                WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
                        GlobalVariable.NumofColm, 54))

                'klik buka * pada passworod'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))

                'verifikasi objek text yang diambil valuenya dengan password'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), 
                            'value'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 54), false))

                'Jika cara verifikasinya menggunakan OTP'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 52) == 'OTP') {
                    'Klik verifikasi by OTP'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifOTP'))

                    'Memindahkan variable ke findTestObject'
                    modifyObjectlabelRequestOTP = findTestObject('KotakMasuk/Sign/lbl_RequestOTP')

                    'Jika button menyetujuinya yes'
                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 53) == 'Yes') {
                        'Klik button menyetujui untuk menandatangani'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
                    }
                    
                    'Jika btn lanjut setelah konfirmasi untuk mengarah ke otp dapat diklik'
                    if (WebUI.verifyElementClickable(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)) {
                        'Klik lanjut after konfirmasi'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)
                    } else {
                        'Jika btn lanjut setelah konfirmasi untuk mengarah ke otp tidak dapat diklik'

                        'Failed alasan save gagal tidak bisa diklik.'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal)

                        'kembali ke loop atas'
                        continue
                    }
                    
                    if (checkerrorLog() == true) {
                        continue
                    }
                    
                    'Jika tidak muncul untuk element selanjutnya'
                    if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal)
                    } else {
                        'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
                        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 
                                    'value'), notelpsigner, false))

                        'OTP yang pertama dimasukkan kedalam 1 var'
                        OTP_before = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[(o - 1)])

                        'clear arraylist sebelumnya'
                        listOTP.clear()

                        'add otp ke list'
                        listOTP.add(OTP_before)

                        'bikin flag untuk dilakukan OTP by db'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 57) == 'Yes') {
                            'value OTP dari db'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP_before)
                        } else {
                            'value OTP dari excel'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
                                    GlobalVariable.NumofColm, 58))
                        }
                        
                        'klik verifikasi OTP'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))

                        'Kasih delay 1 detik karena proses OTP akan trigger popup, namun loading. Tidak instan'
                        WebUI.delay(1)

                        if (checkPopup() == true) {
                            continue
                        }
                        
                        'Resend OTP'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 59) == 'Yes') {
                            'get count untuk resend OTP dari excel'

                            'Looping dari 1 hingga total count resend OTP'
                            for (int w = 1; w <= countResend; w++) {
                                'taruh waktu delay'
                                WebUI.delay(115)

                                'Klik resend otp'
                                WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

                                'Memberikan delay 3 karena OTP after terlalu cepat'
                                WebUI.delay(3)

                                'OTP yang kedua'
                                OTP_after = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[
                                    (o - 1)])

                                'add otp ke list'
                                listOTP.add(OTP_after)

                                'dicheck OTP pertama dan kedua dan seterusnya'
                                if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.CONTINUE_ON_FAILURE)) {
                                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', 
                                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(
                                            GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedOTPError)
                                }
                                
                                'Jika looping telah diterakhir, baru set text'
                                if (w == countResend) {
                                    'value OTP dari db'
                                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP_after, FailureHandling.CONTINUE_ON_FAILURE)

                                    'klik verifikasi OTP'
                                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
                                }
                            }
                        }
                    }
                    
                    if (checkPopup() == true) {
                        continue
                    }
                    
                    if (checkerrorLog() == true) {
                        continue
                    }
                } else {
                    'Klik verifikasi by Biometric'
                    modifyObjectverifBiometric = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_verifOTP'), 
                        'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-signer-signing-verification/div/div/form/div[4]/div[1]/span', 
                        true)

                    'Klik biometric object'
                    WebUI.click(modifyObjectverifBiometric)

                    'Changing check di label request'
                    modifyObjectlabelRequestOTP = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_RequestOTP'), 
                        'xpath', 'equals', '/html/body/ngb-modal-window/div/div/app-camera-liveness/div[1]/h4', true)

                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 53) == 'Yes') {
                        'Klik button menyetujui untuk menandatangani'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
                    }
                    
                    'Klik lanjut after konfirmasi'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)

                    'Jika tidak muncul untuk element selanjutnya'
                    if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal)
                    }
                }
                
                'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut)) {
                    'Mendapat total success dan failed'
                    String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

                    String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

                    'Menarik value count success ke excel'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Send to Sign', 
                        72, GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            73) + ';') + countSuccessSign)

                    'Menarik value count failed ke excel'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Send to Sign', 
                        73, GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            74) + ';') + countFailedSign)

                    'Jika masukan ratingnya tidak kosong'
                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 62) != '') {
                        'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
                        modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'), 
                            'xpath', 'equals', ('//ngb-rating[@id=\'rating\']/span[' + (findTestData(excelPathFESignDocument).getValue(
                                GlobalVariable.NumofColm, 62).toInteger() * 2)) + ']/span', true)

                        'Klik bintangnya bintang berapa'
                        WebUI.click(modifyObjectstarMasukan)

                        'Jika komentarnya tidak kosoong'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 63) != '') {
                            'Input komentar di rating'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
                                    GlobalVariable.NumofColm, 63))
                        }
                    }
                    
					WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Kirim'), GlobalVariable.TimeOut)
                    'klik button Kirim'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))

                    'Verifikasi label pop up ketika masukan telah selesai dikirim'
                    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
                    } else {
                        'Klik OK'
                        WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
                    }
                    
                    'StoreDB mengenai masukan'
                    MasukanStoreDB(conneSign, emailSigner[(o - 1)], arrayMatch)

                    'Jummlah signer tanda tangan akan ditambah mengenai total success berdasarkan countSuccess'
                    jumlahsignertandatangan = (jumlahsignertandatangan + 1)

                    'Looping maksimal 100 detik untuk signing proses. Perlu lama dikarenakan walaupun requestnya done(3), tapi dari VIDAnya tidak secepat itu.'
                    for (int y = 1; y <= 5; y++) {
                        'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                        WebUI.delay(20)

                        'Jika signing process db untuk signing false, maka'
                        if (SigningProcessStoreDB(conneSign, emailSigner[(o - 1)], jumlahsignertandatangan) == false) {
                            'Jika looping waktu delaynya yang terakhir, maka'
                            if (y == 5) {
                                'Failed dengan alasan prosesnya belum selesai'
                                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        2) + ';') + GlobalVariable.ReasonFailedProcessNotDone)
                            }
                        } else {
                            'Jika hasil store dbnya true, maka'
                            break
                        }
                    }
                    
                    'Browser ditutup'
                    WebUI.closeBrowser()
                } else {
                    'Jika popup berhasilnya tidak ada, maka Savenya gagal'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
                }
            }
        }
        
        'persiapan loop untuk checking saldo. Membuat variable '
        int loop_saldo = 0

        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

        documentTemplateName_perdoc = documentTemplateName.split(';', -1)

        nokontrak_perdoc = nokontrak.split(';', -1)

        'beri maks 30 sec mengenai perubahan total sign'
        for (int b = 1; b <= 3; b++) {
            'ambil saldo otp after'
            otp_after = checkSaldoOtp()

            'ambil saldo after'
            saldo_after = checkSaldoSign()

            'Jika count saldo otp diatas (after) sama dengan yang dulu/pertama (before) dikurang dengan jumlah transaksi, yaitu hanya 1'
            if (WebUI.verifyEqual(Integer.parseInt(otp_before) - (countResend + 1), Integer.parseInt(otp_after), FailureHandling.OPTIONAL)) {
                'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang dengan jumlah transaksi, yaitu hanya 1'
                if (WebUI.verifyEqual(Integer.parseInt(saldo_before) - documentTemplateName_perdoc.size(), Integer.parseInt(
                        saldo_after), FailureHandling.OPTIONAL)) {
                    break
                } else {
                    'Masih sama, dikasi waktu delay 10'
                    WebUI.delay(10)

                    WebUI.refresh()
                }
            } else {
                'Masih sama, dikasi waktu delay 10'
                WebUI.delay(5)

                WebUI.refresh()
            }
        }
        
        if (totaldocsign != documentTemplateName_perdoc.size()) {
            'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + 'Total dokumen untuk di bulk tidak sesuai dengan yang ditandatangani.')
        }
        
        for (int i = 0; i < documentTemplateName_perdoc.size(); i++) {
			inputFiltertrx(currentDate, nokontrak_perdoc[i], documentTemplateName_perdoc[i])
            'delay dari 10 sampe 60 detik'
            for (int d = 1; d <= 6; d++) {
                'Jika dokumennya ada, maka'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {

                    'get column di saldo'
                    variable_saldo_column = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

                    'get row di saldo'
                    variable_saldo_row = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

                    'ambil inquiry di db'
                    ArrayList<String> inquiry_db = CustomKeywords.'connection.DataVerif.gettrxSaldo'(conneSign)

                    'looping mengenai columnnya'
                    for (int u = 1; u <= (variable_saldo_column.size() / variable_saldo_row.size()); u++) {
						isbreak = true
                        'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                        modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            variable_saldo_row.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', 
                            true)

                        'Jika u di lokasi qty atau kolom ke 9'
                        if (u == 9) {
                            'Jika yang qtynya 1 dan databasenya juga, berhasil'
                            if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiry_db[(u - 1)]) == '-1')) {
                                'Jika bukan untuk 2 kolom itu, maka check ke db'
                                checkVerifyEqualorMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiry_db[
                                        (u - 1)], false, FailureHandling.CONTINUE_ON_FAILURE))
                            } else {
                                'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                                GlobalVariable.FlagFailed = 1

                                'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        2) + ';') + GlobalVariable.ReasonFailedSignGagal)
                            }
                        } else if (u == (variable_saldo_column.size() / variable_saldo_row.size())) {
                            'Jika di kolom ke 10, atau di FE table saldo'

                            'check saldo dari table dengan saldo yang sekarang'
                            checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(WebUI.getText(modifyperrowpercolumn)), 
                                    jumlahsignertandatangan, FailureHandling.CONTINUE_ON_FAILURE))
                        } else {
                            'Jika bukan untuk 2 kolom itu, maka check ke db'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiry_db[(u - 
                                    1)], false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
						if(isbreak == true) {
							break
						}
                    }
					} else {
                    'jika kesempatan yang terakhir'
                    if (d == 6) {
                        'Jika masih tidak ada'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI)
                    }
                    
                    'delay 10 detik'
                    WebUI.delay(10)

                    WebUI.refresh()
                }
			}
        }
    }
}

' penggunaan ini hanya untuk Masukan Store Db'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedStoredDB)
}

'Fungsi untuk check konfirmasi tandatangan'

'Fungsi untuk Masukan store DB'

'Fungsi untuk Signing Proses store DB'

'Fungsi input filter pada transaksi'

'input Check saldo Sign'

'Fungsi untuk check Saldo OTP'

def checkVerifyEqualorMatch(Boolean isMatch) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
    }
}

def checkKonfirmasiTTD() {
    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik tidak untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TidakKonfirmasiTTD'))

    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik ya untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_YaKonfirmasiTTD'))
}

def MasukanStoreDB(Connection conneSign, String emailSigner, ArrayList<String> arrayMatch) {
    'deklarasi arrayindex untuk penggunakan selanjutnya'
    arrayindex = 0

    'MasukanDB mengambil value dari hasil query'
    MasukanDB = CustomKeywords.'connection.DataVerif.getFeedbackStoreDB'(conneSign, emailSigner)

    'verify rating'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 62), MasukanDB[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify komentar'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 63), MasukanDB[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def SigningProcessStoreDB(Connection conneSign, String emailSigner, int jumlahsignertandatangan) {
    'deklarasi arrayindex untuk penggunakan selanjutnya'
    arrayindex = 0

    'SigningDB mengambil value dari hasil query'
    SigningDB = CustomKeywords.'connection.DataVerif.getSigningStatusProcess'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, 5), emailSigner)

    'looping berdasarkan size dari signingDB'
    for (int t = 1; t <= SigningDB.size(); t++) {
        ArrayList<String> arrayMatch = new ArrayList<String>()

        'verify request status. 3 berarti done request. Terpaksa hardcode karena tidak ada masternya untuk 3.'
        arrayMatch.add(WebUI.verifyMatch('3', SigningDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify sign date. Jika ada, maka teksnya Sudah TTD. Sign Date sudah dijoin ke email masing-masing, sehingga pengecekan apakah sudah sign atau belum ditandai disini'
        arrayMatch.add(WebUI.verifyMatch('Sudah TTD', SigningDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify total signed. Total signed harusnya seusai dengan variable jumlah signed'
        arrayMatch.add(WebUI.verifyEqual(jumlahsignertandatangan, Integer.parseInt(SigningDB[arrayindex++]), FailureHandling.CONTINUE_ON_FAILURE))

        'Jika arraymatchnya ada false'
        if (arrayMatch.contains(false)) {
            'mengembalikan false'
            return false
            
            'dibreak ke looping code'
            break
        } else {
            'jika semuanya true'

            'mengembalikan true'
            return true
            
            'dibreak ke looping code'
            break
        }
    }
}

def inputFiltertrx(String currentDate, String nokontrak, String documentTemplateName) {
    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            65))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            66))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/lbl_tipedokumen'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            67))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/lbl_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), nokontrak)

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))
}

def checkSaldoSign() {
    String totalSaldo

    'klik button saldo'
    WebUI.click(findTestObject('Saldo/btn_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_tenant'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            68), false)

    'get total div di Saldo'
    variable_divSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variable_divSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                69), FailureHandling.OPTIONAL)) {
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
    
    'tutup browsernya'
    WebUI.closeBrowser()
}

def checkSaldoOtp() {
    String totalSaldo = new ArrayList<String>()

    'klik button saldo'
    WebUI.click(findTestObject('Saldo/btn_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_tenant'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            70), false)

    'get total div di Saldo'
    variable_divSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variable_divSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                71), FailureHandling.OPTIONAL)) {
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

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + lblpopup)

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
    }
    
    return false
}

def checkerrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Verifikasi OTP berhasil'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + errormessage)

            return true
        }
    }
    
    return false
}

