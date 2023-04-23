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
import customizeKeyword.checkSaveProccess as checkSaveProccess
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

GlobalVariable.Response = '00155D0B-7502-81A3-11ED-E1EF38DCEF30'

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

//ArrayList<String> documentTemplateName = new ArrayList<String>()
'Inisialisasi array-array yang dibutuhkan'
ArrayList<String> listOTP = new ArrayList<String>()

ArrayList<String> totalSaldo = new ArrayList<String>()

ArrayList<String> MasukanDB = new ArrayList<String>()

ArrayList<String> SendtoSign = CustomKeywords.'connection.dataVerif.getDataSendtoSign'(conneSign, GlobalVariable.Response)

ArrayList<String> SigningDB = new ArrayList<String>()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
int jumlahsignertandatangan = 0

//ArrayList<String> nokontrak = new ArrayList<String>()
String nokontrak

String saldo_before

String saldo_after

String otp_before

String otp_after

String documentTemplateName

String notelpsigner

'Mengambil email berdasarkan documentId'
ArrayList<String> emailSigner = CustomKeywords.'connection.dataVerif.getEmailLogin'(conneSign, GlobalVariable.Response).split(
    ';', -1)

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

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

    WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [('jumlahsignertandatangan') : jumlahsignertandatangan, ('emailSigner') : emailSigner], 
        FailureHandling.CONTINUE_ON_FAILURE)

    'Klik checkbox ttd untuk semua'
    WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

    'Klik button ttd bulk'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

    'klik tombol Batal'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))

    'refresh buat reset nav bar selanjutnya'
    WebUI.refresh()

    //'reset array. Digunakan untuk jika per signer dan tidak menumpuk'
    //documentTemplateName.clear()
    'Jika bukan di page 1, verifikasi menggunakan button Lastest'

    'get row lastest'
    variable_lastest = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

    modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variable_lastest.size()) + ']/a/i', true)

    if (WebUI.verifyElementPresent(modifyobjectbtnLastest, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'Klik button Lastest'
        WebUI.click(modifyobjectbtnLastest)
    }
    
    'get row beranda'
    variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

    'Jika check documentnya all'
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 7) == 'Yes') {
        'Klik checkbox ttd untuk semua'
        WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

        'looping untuk mengambil seluruh row'
        for (int k = 1; k <= variable.size(); k++) {
            'modify object text document template name di beranda'
            modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                k) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

            'modify object text no kontrak di beranda'
            modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                k) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

            'memasukkan value nomor kontrak ke variable string'
            nokontrak = WebUI.getText(modifyObjecttextrefnumber)

            'memasukkan value nama dokumen template ke variable string'
            documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename //Jika dia tidak check all sign
                )
        }
    } else {
        'asumsi document yang dipilih selalu 1 karena berdasarkan Send Document API doc'

        'Kesimpulan : 1 doc many signer'
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
            modifyObjectlbltglpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                j) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/span', true)

            'modify object text no kontrak di beranda'
            modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                j) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

            'Jika datanya match dengan db, mengenai referal number'
            if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnumber), SendtoSign[arrayindex++], false, FailureHandling.OPTIONAL)) {
                if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatetipe), SendtoSign[arrayindex++], false, 
                    FailureHandling.OPTIONAL)) {
                    if (WebUI.verifyMatch(WebUI.getText(modifyObjectlbltglpermintaan), SendtoSign[arrayindex++], false, 
                        FailureHandling.OPTIONAL)) {
                        documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

                        nokontrak = WebUI.getText(modifyObjecttextrefnumber)

                        WebUI.click(modifyObjectcheckboxttd)
                    }
                }
            } else if (j == variable.size()) {
                checkVerifyEqualorMatch(false)
            }
        }
    }
    
    'Klik button ttd bulk'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

    /*
    for (int a = 1; a <= documentTemplateName.size(); a++) {
        'modify object btn Nama Dokumen '
        modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
            'equals', ('id("ngb-nav-' + (a + 1)) + '")', true)

        'verify nama dokumen massal dengan nama dokumen di paging'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateName[(a - 1)], 
                false))
    }
    */
    'modify object btn Nama Dokumen '
    modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
        'equals', ('id("ngb-nav-' + 2) + '")', true)

    'verify nama dokumen massal dengan nama dokumen di paging'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateName, false))

    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    checkKonfirmasiTTD()

    'jika page belum pindah'
    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedSaveGagal //Jika dokumen sudah pindah
            //Jika error lognya tidak muncul
            //Jika tidak ada resend
            /*'Jika error lognya muncul' .Case kemarin mengenai documentIdnya sedang proses tanda tangan, namun list di Berandanya masih ada. Per 16 April 2023
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label').contains('Verifikasi OTP Berhasil'))
                    }*/ /*
        for (l = 1; l <= documentTemplateName.size(); l++) {
            'modify object text document template name di Tanda Tangan Dokumen'
            modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + l) + ']/label', true)

            'verify nama dokumen massal dengan nama dokumen di paging'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), [
                    (l - 1)], false))
        }
        */ )
    } else {
        'modify object text document template name di Tanda Tangan Dokumen'
        modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
            'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + 1) + ']/label', true)

        'verify nama dokumen massal dengan nama dokumen di paging'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), documentTemplateName, 
                false))

        'Klik button proses'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

        'mereset array index'
        arrayindex = 0

        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label'))
        } else {
            'Verifikasi label penanda tangan'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL))) {
                'Custom keyword mengenai savenya gagal'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
            } else {
                'Verifikasi antara email yang dinput dengan db'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_EmailAfterKonfirmasi'), 
                            'value'), emailSigner[(o - 1)], false))

                'Get text nomor telepon'
                notelpsigner = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_phoneNoAfterKonfirmasi'), 'value')

                'input text password'
                WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
                        GlobalVariable.NumofColm, 12))

                'klik buka * pada passworod'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))

                'verifikasi objek text yang diambil valuenya dengan password'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), 
                            'value'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 12), false))

                'Jika cara verifikasinya menggunakan OTP'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 10) == 'OTP') {
                    'Klik verifikasi by OTP'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifOTP'))

                    'Memindahkan variable ke findTestObject'
                    modifyObjectlabelRequestOTP = findTestObject('KotakMasuk/Sign/lbl_RequestOTP')

                    'Jika menyetujuinya yes'
                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 11) == 'Yes') {
                        'Klik button menyetujui untuk menandatangani'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
                    }
                    
                    'Klik lanjut after konfirmasi'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)

                    'Jika error lognya muncul'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label'))
                    } else {
                        'Jika tidak muncul untuk element selanjutnya'
                        if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
                        } else {
                            'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 
                                        'value'), notelpsigner, false))

                            'OTP yang pertama dimasukkan kedalam 1 var'
                            OTP_before = CustomKeywords.'connection.dataVerif.getOTPLoginId'(conneSign, emailSigner[(o - 
                                1)])

                            'clear arraylist sebelumnya'
                            listOTP.clear()

                            'add otp ke list'
                            listOTP.add(OTP_before)

                            'Resend OTP'
                            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 17) == 'Yes') {
                                'get count untuk resend OTP dari excel'
                                countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 18).toInteger()

                                for (int w = 1; w <= countResend; w++) {
                                    'taruh waktu delay'
                                    WebUI.delay(117)

                                    'Klik resend otp'
                                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

                                    'Memberikan delay 1 karena OTP after terlalu cepat'
                                    WebUI.delay(1)

                                    'OTP yang kedua'
                                    OTP_after = CustomKeywords.'connection.dataVerif.getOTPLoginId'(conneSign, CustomKeywords.'connection.dataVerif.getEmailLogin'(
                                            conneSign, GlobalVariable.Response))

                                    'add otp ke list'
                                    listOTP.add(OTP_after)

                                    'dicheck OTP pertama dan kedua dan seterusnya'
                                    if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false)) {
                                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', 
                                            GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(
                                                GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedOTPError)
                                    }
                                    
                                    'Jika looping telah diterakhir, baru set text'
                                    if (w == countResend) {
                                        'value OTP dari db'
                                        WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP_after)
                                    }
                                }
                            } else {
                                'bikin flag untuk dilakukan OTP by db'
                                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
                                    'value OTP dari db'
                                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP_before)
                                } else {
                                    'value OTP dari excel'
                                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
                                            GlobalVariable.NumofColm, 16))
                                }
                            }
                        }
                    }
                    
                    'klik verifikasi OTP'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))

                    'Jika label verifikasi OTPnya tidak muncul'
                    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), 
                        GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
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

                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 11) == 'Yes') {
                        'Klik button menyetujui untuk menandatangani'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
                    }
                    
                    'Klik lanjut after konfirmasi'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)

                    'Jika tidak muncul untuk element selanjutnya'
                    if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
                    }
                }
                
                'Jika error lognya muncul'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label')

                    if (errormessage == 'Verifikasi OTP Berhasil') {
                        'write to excel success'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Sign Document', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    } else {
                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + errormessage)
                    }
                }
                
                'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut)) {
                    'Verif success dan failednya belum dilakukan apa-apa'
                    String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

                    String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

                    'Menarik value count success ke excel'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Sign Document', 
                        30, GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            31) + ';') + countSuccessSign)

                    'Menarik value count failed ke excel'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Sign Document', 
                        31, GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            32) + ';') + countFailedSign)

                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 20) != '') {
                        'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
                        modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'), 
                            'xpath', 'equals', ('//ngb-rating[@id=\'rating\']/span[' + (findTestData(excelPathFESignDocument).getValue(
                                GlobalVariable.NumofColm, 20).toInteger() * 2)) + ']/span', true)

                        'Klik bintangnya bintang berapa'
                        WebUI.click(modifyObjectstarMasukan)

                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 21) != '') {
                            'Input komentar di rating'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
                                    GlobalVariable.NumofColm, 21))
                        }
                    }
                    
                    'klik button Kirim'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))

                    'Verifikasi label pop up ketika masukan telah selesai dikirim'
                    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
                    } else {
                        'Klik OK'
                        WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
                    }
                    
                    MasukanStoreDB(conneSign, emailSigner[(o - 1)], arrayMatch)

                    jumlahsignertandatangan = (jumlahsignertandatangan + Integer.parseInt(countSuccessSign.replace('Success: ', 
                            '')))

                    for (int y = 1; y <= 10; y++) {
                        'Kita berikan delay 100 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                        WebUI.delay(20)

                        if (SigningProcessStoreDB(conneSign, emailSigner[(o - 1)], jumlahsignertandatangan) == false) {
                            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2) + ';') + GlobalVariable.ReasonFailedProcessNotDone)
                        } else {
                            break
                        }

                    }
					
					'Browser ditutup'
					WebUI.closeBrowser()
                }
            }
        }
    }
    
    'persiapan loop untuk checking saldo. Membuat variable '
    int loop_saldo = 0

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

    'beri maks 30 sec mengenai perubahan total sign'
    for (int b = 1; b <= 3; b++) {
        'ambil saldo otp after'
        otp_after = checkSaldoOtp()

        'ambil saldo after'
        saldo_after = checkSaldoSign()

        'Jika count saldo otp diatas (after) sama dengan yang dulu/pertama (before) dikurang dengan jumlah transaksi, yaitu hanya 1'
        if (WebUI.verifyEqual(Integer.parseInt(otp_before) - 1, Integer.parseInt(otp_after), FailureHandling.OPTIONAL)) {
            'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang dengan jumlah transaksi, yaitu hanya 1'
            if (WebUI.verifyEqual(Integer.parseInt(saldo_before) - 1, Integer.parseInt(saldo_after), FailureHandling.OPTIONAL)) {
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
    
    'delay dari 10 sampe 60 detik'
    for (int d = 1; d <= 6; d++) {
        inputFiltertrx(currentDate,nokontrak,documentTemplateName)

        'Jika dokumennya ada, maka'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            break
        } else {
            'jika kesempatan yang terakhir'
            if (d == 6) {
                'Jika masih tidak ada'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI)
            }
            
            'delay 10 detik'
            WebUI.delay(10)

            WebUI.refresh()
        }
    }
    
    'get column di saldo'
    variable_saldo_column = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

    'ambil inquiry di db'
    ArrayList<String> inquiry_db = CustomKeywords.'connection.dataVerif.gettrxSaldo'(conneSign)

    'looping mengenai columnnya'
    for (int u = 1; u <= (variable_saldo_column.size() / o); u++) {
        'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
        modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            o) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

        'Jika u di lokasi qty'
        if (u == 9) {
            'Jika yang qtynya 1 dan databasenya juga, berhasil'
            if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiry_db[(u - 1)]) == '-1')) {
				
				'Jika bukan untuk 2 kolom itu, maka check ke db'
				checkVerifyEqualorMatch(WebUI.verifyMatch('-'+WebUI.getText(modifyperrowpercolumn), inquiry_db[(u - 1)], false,
				FailureHandling.CONTINUE_ON_FAILURE))
            } else {
                'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                GlobalVariable.FlagFailed = 1

                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedSignGagal)
            }
        }else if (u == (variable_saldo_column.size() / o)) {
            'Jika dia yang ke 10, atau di FE table saldo'

            'check saldo dari table dengan saldo yang sekarang'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), saldo_after, false, FailureHandling.CONTINUE_ON_FAILURE))
        } else {
            'Jika bukan untuk 2 kolom itu, maka check ke db'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiry_db[(u - 1)], false, 
                    FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
}

'jika data db tidak sesuai dengan excel'

'per 19 April 2023, penggunaan ini hanya untuk Masukan Store Db'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def checkVerifyEqualorMatch(Boolean isMatch) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
    }
}

def checkKonfirmasiTTD() {
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

    MasukanDB = CustomKeywords.'connection.dataVerif.getFeedbackStoreDB'(conneSign, emailSigner)

    'verify rating'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 20), MasukanDB[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify komentar'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 21), MasukanDB[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def SigningProcessStoreDB(Connection conneSign, String emailSigner, int jumlahsignertandatangan) {
    
	'deklarasi arrayindex untuk penggunakan selanjutnya'
    arrayindex = 0

    SigningDB = CustomKeywords.'connection.dataVerif.getSigningStatusProcess'(conneSign, GlobalVariable.Response, emailSigner)

    for (int t = 1; t <= SigningDB.size(); t++) {
		ArrayList<String> arrayMatch = new ArrayList<String>()
        'verify request status. 3 berarti done request'
        arrayMatch.add(WebUI.verifyMatch('3', SigningDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify sign date. Jika ada, maka teksnya Sudah TTD. Sign Date sudah dijoin ke email masing-masing, sehingga pengecekan apakah sudah sign atau belum ditandai disini'
        arrayMatch.add(WebUI.verifyMatch('Sudah TTD', SigningDB[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify total signed. Total signed harusnya seusai dengan variable jumlah signed'
        arrayMatch.add(WebUI.verifyEqual(jumlahsignertandatangan, Integer.parseInt(SigningDB[arrayindex++]), FailureHandling.CONTINUE_ON_FAILURE))
		
		if (arrayMatch.contains(false)) {
			return false
			break
		}else {
			return true
			break
		}
    }
}

def inputFiltertrx(String currentDate, String nokontrak, String documentTemplateName) {
    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            23))

    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            24))

    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    WebUI.setText(findTestObject('Saldo/lbl_tipedokumen'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            25))

    WebUI.sendKeys(findTestObject('Saldo/lbl_tipedokumen'), Keys.chord(Keys.ENTER))

    WebUI.setText(findTestObject('Saldo/input_refnumber'), nokontrak)

    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    WebUI.click(findTestObject('Saldo/btn_cari'))
}

def checkSaldoSign() {
    String totalSaldo

    'klik button saldo'
    WebUI.click(findTestObject('Saldo/btn_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_tenant'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            26), false)

    'get total div di Saldo'
    variable_divSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variable_divSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                27), FailureHandling.OPTIONAL)) {
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
            28), false)

    'get total div di Saldo'
    variable_divSaldo = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variable_divSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                29), FailureHandling.OPTIONAL)) {
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

