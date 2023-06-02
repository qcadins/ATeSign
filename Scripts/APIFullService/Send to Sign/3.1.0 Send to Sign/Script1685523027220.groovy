import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'Inisialisasi array untuk Listotp'
ArrayList listOTP = []

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayIndex = 0

sheet = 'Send to Sign'

'looping untuk sending document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= 2/*findTestData(excelPathFESignDocument).columnNumbers*/ ; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'Call API Send doc'
        WebUI.callTestCase(findTestCase('APIFullService/Send to Sign/Response API Send Document'), [('excelPathAPISendDoc') : excelPathFESignDocument, ('sheet') : sheet], 
            FailureHandling.CONTINUE_ON_FAILURE)

        'Jika tidak ada dokumen id di excel'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 5) == '') {
            'loop selanjutnya'
            continue
        }
        
        'Jika document tersebut tidak membutuhkan tanda tangan'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 48) == 'No') {
            continue
        }
        
        'ambil db checking ke UI Beranda'
        ArrayList SendtoSign = CustomKeywords.'connection.DataVerif.getDataSendtoSign'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, 6))

        'Mengambil email berdasarkan documentId'
        ArrayList emailSigner = CustomKeywords.'connection.DataVerif.getEmailLogin'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, 6)).split(';', -1)

        'Declare variable jumlahSignerTelahTtd untuk proses ttd, saldo Used untuk penggunaan saldo, dan saldoUsedDocPertama hanya untuk dokumen pertama'
        int jumlahSignerTelahTtd = 0, saldoUsed = 0, saldoUsedDocPertama = 0

        'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
        int jumlahSignerTandaTangan = CustomKeywords.'connection.DataVerif.getTotalSigned'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, 6))

        'looping email signer'
        for (int o = 1; o <= emailSigner.size(); o++) {
            'Inisialisasi variable yang dibutuhkan'
            String noKontrak, saldoSignBefore, saldoSignAfter,otpBefore, otpAfter, documentTemplateName, noTelpSigner

            'Mengkosongkan nomor kontrak'
            noKontrak = ''

            'Mengkosongkan document Template Name'
            documentTemplateName = ''

            'Inisialisasi variable total document yang akan disign, count untuk resend, dan saldo yang akan digunakan'
            int totalDocSign, countResend

            'Call test Case untuk login sebagai admin wom admin client'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], 
                FailureHandling.STOP_ON_FAILURE)

            'mengambil saldo before'
            saldoSignBefore = checkSaldoSign()

            'mengambil saldo otp before'
            otpBefore = checkSaldoOtp()

            'tutup browsernya'
            WebUI.closeBrowser()
			
            'call Test Case untuk login sebagai user berdasarkan doc id'
            WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [('email') : emailSigner[(o - 1)]], FailureHandling.STOP_ON_FAILURE)

            'Klik checkbox ttd untuk semua'
            WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

            'Klik button ttd bulk'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

            'klik tombol Batal'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))

            'refresh buat reset nav bar selanjutnya'
            WebUI.refresh()

            'Jika bukan di page 1, verifikasi menggunakan button Lastest. Get row lastest'
            variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

            'get row lastest'
            modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
                variableLastest.size()) + ']/a/i', true)

            'jika btn lastest dapat diclick'
            if (WebUI.verifyElementClickable(modifyobjectbtnLastest, FailureHandling.OPTIONAL)) {
                'Klik button Lastest'
                WebUI.click(modifyobjectbtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'get row beranda'
            rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

            'Jika ingin dilakukannya bulk sign'
            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
                'Ambil data dari excel mengenai total dokumen yang ditandatangani'
                totalDocSign = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 50).toInteger()

                'looping untuk mengambil seluruh row'
                for (int k = 1; k <= rowBeranda.size(); k++) {
                    'deklarasi arrayIndex untuk pemakaian'
                    arrayIndex = 0

                    'modify object text document template name di beranda'
                    modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        k) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

                    'modify object text document template tipe di beranda'
                    modifyObjecttextdocumenttemplatetipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        k) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                    'modify object btn TTD Dokumen di beranda'
                    modifyObjectcheckboxttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        k) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

                    'modify object lbl tanggal permintaan'
                    modifyObjectlbltglpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        k) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/span', true)

                    'modify object text no kontrak di beranda'
                    modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 'xpath', 
                        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        k) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                    'Jika looping lebih besar dari total row dikurangi total dokumen yang ingin ditandatangani. Ini pengecekan agar mengambil total dokumen yang diinginkan.'
                    if (k > (rowBeranda.size() - totalDocSign)) {
                        'Jika looping berada di akhir row. Row selalu di akhir untuk mengecek data, dimana data terakhir pasti data yang baru dikirim'
                        if (k == rowBeranda.size()) {
                            'Jika datanya match dengan db, mengenai referal number'
                            if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnumber), SendtoSign[arrayIndex++], false, 
                                FailureHandling.OPTIONAL) == true) {
                                'Mengenai tipe dokumen template'
                                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatetipe), 
                                        SendtoSign[arrayIndex++], false, FailureHandling.OPTIONAL), '')

                                'Mengenai tanggal permintaan'
                                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlbltglpermintaan), SendtoSign[
                                        arrayIndex++], false, FailureHandling.OPTIONAL), '')
                            } else {
                                'Data tidak ada di UI pada page ke '
                                checkVerifyEqualorMatch(false, '')
                            }
                        }
                        
                        'Klik checkbox tanda tangan'
                        WebUI.click(modifyObjectcheckboxttd)

                        'Jika document Template Namenya masih kosong'
                        if (documentTemplateName == '') {
                            'Input document Template Name dan nomor kontrak dari UI'
                            documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

                            noKontrak = WebUI.getText(modifyObjecttextrefnumber)
                        } else {
                            'Input document Template Name dan nomor kontrak dari UI ditambah dengan delimiter ;'
                            documentTemplateName = ((WebUI.getText(modifyObjecttextdocumenttemplatename) + ';') + documentTemplateName)

                            noKontrak = ((WebUI.getText(modifyObjecttextrefnumber) + ';') + noKontrak)
                        }
                    }
                }
                
                'Klik button ttd bulk'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

                'Split document Template Name berdasarkan delimiter'
                documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

                'check popup'
                if (checkPopup() == true) {
                    continue
                }
                
                'Jika total document sign excel tidak sama dengan total document sign paging'
                if (totalDocSign != documentTemplateNamePerDoc.size()) {
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedTotalDocTidakSesuai) + documentTemplateNamePerDoc.size()) + 
                        'pada User ') + (emailSigner[(o - 1)]))
                }
                
                'Looping berdasarkan total document sign'
                for (int c = 0; c < documentTemplateNamePerDoc.size(); c++) {
                    'modify object btn Nama Dokumen '
                    modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 
                        'xpath', 'equals', ('id("ngb-nav-' + (c + 2)) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

                    'verify nama dokumen massal dengan nama dokumen di paging'
                    if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - 
                        (c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
                        'Jika tidak cocok, maka custom keywords jika tidak sama.'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' dimana tidak sesuai di page Bulk Sign antara ') + 
                            WebUI.getText(modifyObjectbtnNamaDokumen)) + ' dengan ') + (documentTemplateNamePerDoc[c]))
                    }
                }
            } else {
                'Total document sign hanya 1 (single)'
                totalDocSign = 1

                'modify page untuk previous. Ini akan digunakan jika datanya tidak ditemukan'
                modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
                    2) + ']/a/i', true)

                'Looping berdasarkan page agar bergeser ke page sebelumnya'
                for (int k = 1; k <= (variableLastest.size() - 4); k++) {
                    'get row beranda'
                    rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

                    'asumsi document yang dipilih selalu 1 karena berdasarkan Send Document API doc. Kesimpulan : 1 doc many signer'
                    for (j = 1; j <= rowBeranda.size(); j++) {
                        'deklarasi arrayIndex untuk pemakaian'
                        arrayIndex = 0

                        'modify object text document template name di beranda'
                        modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

                        'modify object text document template tipe di beranda'
                        modifyObjecttextdocumenttemplatetipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

                        'modify object btn TTD Dokumen di beranda'
                        modifyObjectcheckboxttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

                        'modify object lbl tanggal permintaan'
                        modifyObjectlbltglpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/span', true)

                        'modify object text no kontrak di beranda'
                        modifyObjecttextrefnumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/lbl_refnumber'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

                        'Jika datanya match dengan db, mengenai referal number'
                        if (WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnumber), SendtoSign[arrayIndex++], false, 
                            FailureHandling.OPTIONAL)) {
                            'Mengenai tipe dokumen template'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatetipe), 
                                    SendtoSign[arrayIndex++], false, FailureHandling.OPTIONAL), '')

                            'Mengenai tanggal permintaan'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlbltglpermintaan), SendtoSign[
                                    arrayIndex++], false, FailureHandling.OPTIONAL), '')

                            'Ambil document template name, nomor kontrak dan klik checkbox khusus 1 signer'
                            documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

                            noKontrak = WebUI.getText(modifyObjecttextrefnumber)

                            WebUI.click(modifyObjectcheckboxttd)
                        }
                    }
                    
                    'Jika masih tidak ditemukan datanya, maka'
                    if (documentTemplateName == '') {
                        'Klik previous'
                        WebUI.click(modifyobjectbtnLastest)
                    } else {
                        'Jika sudah ditemukan, maka break'
                        break
                    }
                    
                    'Jika sudah dilooping terakhir mengenai pagenya dan tetap tidak menemukan'
                    if ((k == (variableLastest.size() - 4)) && (documentTemplateName == '')) {
                        'Input verifynya false dengan reason'
                        checkVerifyEqualorMatch(false, ' dengan alasan tidak ditemukannya document Template Name yang diinginkan.')

                        continue
                    }
                }
                
                'Klik button ttd bulk'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

                'modify object btn Nama Dokumen '
                modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 
                    'xpath', 'equals', ('id("ngb-nav-' + 2) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

                'check popup'
                if (checkPopup() == true) {
                    continue
                }
                
                'verify nama dokumen massal dengan nama dokumen di paging'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateName, 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ((' dimana tidak sesuai di page Bulk Sign antara ' + 
                    WebUI.getText(modifyObjectbtnNamaDokumen)) + ' dengan ') + documentTemplateName)
            }
            
            'Check konfirmasi tanda tangan'
            checkKonfirmasiTTD()

            'jika page belum pindah'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL))) {
                'Jika tidak ada, maka datanya tidak ada, atau save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan page tidak berpindah di Bulk Sign View.')
            } else {
                'Jika bulk signing'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
                    'Split document Template name per dokumen berdasarkan delimiter'
                    documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

                    'Looping berdasarkan document template name per dokumen'
                    for (int i = 0; i < documentTemplateNamePerDoc.size(); i++) {
                        'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
                        modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                            'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

                        'verify nama dokumen dengan nama dokumen di paging'
                        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), 
                                documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - (i + 1))], false), '')
                    }
                } else {
                    'verify nama dokumen dengan nama dokumen di paging'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi')), 
                            documentTemplateName, false), '')
                }
                
                'Scroll ke btn Proses'
                WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

                'Klik button proses'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

                'mereset array index'
                arrayIndex = 0

                'check error log'
                if (checkErrorLog() == true) {
                    continue
                }
                
                'Jika error log tidak muncul, Jika verifikasi penanda tangan tidak muncul'
                if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL))) {
                    'Custom keyword mengenai savenya gagal'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' pada saat tidak muncul pop up Verifikasi Penanda Tangan')
                } else {
                    'Jika verifikasi penanda tangan muncul, Verifikasi antara email yang ada di UI dengan db'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_EmailAfterKonfirmasi'), 
                                'value'), emailSigner[(o - 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Signer')

                    'Get text nomor telepon'
                    noTelpSigner = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_phoneNoAfterKonfirmasi'), 'value')

                    'input text password'
                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
                            GlobalVariable.NumofColm, 54))

                    'klik buka * pada passworod'
                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))

                    'verifikasi objek text yang diambil valuenya dengan password'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), 
                                'value'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 54), 
                            false), 'pada nomor telepon Signer')

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
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak bisa lanjut proses OTP')

                            'kembali ke loop atas'
                            continue
                        }
                        
                        'check error log'
                        if (checkErrorLog() == true) {
                            continue
                        }
                        
                        'Jika tidak muncul untuk element selanjutnya'
                        if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page input OTP')
                        } else {
                            'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 
                                        'value'), noTelpSigner, false), '')

                            'OTP yang pertama dimasukkan kedalam 1 var'
                            OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[(o - 1)])

                            'clear arraylist sebelumnya'
                            listOTP.clear()

                            'add otp ke list'
                            listOTP.add(OTP)

                            'bikin flag untuk dilakukan OTP by db'
                            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 57) == 'Yes') {
                                'value OTP dari db'
                                WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
                            } else {
                                'value OTP dari excel'
                                WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
                                        GlobalVariable.NumofColm, 58))
                            }
                            
                            'klik verifikasi OTP'
                            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))

                            'Kasih delay 1 detik karena proses OTP akan trigger popup, namun loading. Tidak instan'
                            WebUI.delay(1)

                            'check pop up'
                            if (checkPopup() == true) {
                                continue
                            }
                            
                            'Resend OTP'
                            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 59) == 'Yes') {
                                'Ambil data dari excel mengenai countResend'
                                countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 60).toInteger()

                                'Looping dari 1 hingga total count resend OTP'
                                for (int w = 1; w <= countResend; w++) {
                                    'taruh waktu delay'
                                    WebUI.delay(115)

                                    'Klik resend otp'
                                    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

                                    'Memberikan delay 3 karena OTP after terlalu cepat'
                                    WebUI.delay(3)

                                    'OTP yang kedua'
                                    otpAfter = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[
                                        (o - 1)])

                                    'add otp ke list'
                                    listOTP.add(otpAfter)

                                    'dicheck OTP pertama dan kedua dan seterusnya'
                                    if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.CONTINUE_ON_FAILURE)) {
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(
                                                GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedOTPError)
                                    }
                                    
                                    'Jika looping telah diterakhir, baru set text'
                                    if (w == countResend) {
                                        'value OTP dari db'
                                        WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), otpAfter, FailureHandling.CONTINUE_ON_FAILURE)

                                        'klik verifikasi OTP'
                                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
                                    }
                                }
                            } else {
                                'tidak ada resend'
                                countResend = 0
                            }
                        }
                        
						'check error log'
						if (checkErrorLog() == true) {
							continue
						}
						
                        'check pop up'
                        if (checkPopup() == true) {
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
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page masukan')
                        }
                    }
                    
                    'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), 
                        GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                        'Mendapat total success dan failed'
                        String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

                        String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

                        'Menarik value count success ke excel'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 71, 
                            GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                72) + ';') + countSuccessSign)

                        'Menarik value count failed ke excel'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 72, 
                            GlobalVariable.NumofColm - 1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                73) + ';') + countFailedSign)

                        'Jika masukan ratingnya tidak kosong'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 62) != '') {
                            'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
                            modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'), 
                                'xpath', 'equals', ('//ngb-rating[@id=\'rating\']/span[' + (findTestData(excelPathFESignDocument).getValue(
                                    GlobalVariable.NumofColm, 62).toInteger() * 2)) + ']/span', true)

                            'Klik bintangnya bintang berapa'
                            WebUI.click(modifyObjectstarMasukan)
                        }
                        
                        'Jika komentarnya tidak kosoong'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 63) != '') {
                            'Input komentar di rating'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
                                    GlobalVariable.NumofColm, 63))
                        }
                        
                        'Scroll ke btn Kirim'
                        WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Kirim'), GlobalVariable.TimeOut)

                        'klik button Kirim'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))

                        if (checkErrorLog() == true) {
                            continue
                        }
                        
                        'Verifikasi label pop up ketika masukan telah selesai dikirim'
                        if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
                        } else {
                            'Klik OK'
                            WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
                        }
                        
                        'Jika masukan ratingnya tidak kosong'
                        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 62) != '') {
                            'StoreDB mengenai masukan'
                            masukanStoreDB(conneSign, emailSigner[(o - 1)], arrayMatch)
                        }
                        
                        'write to excel success'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                        'Mensplit nomor kontrak yang telah disatukan'
                        noKontrakPerDoc = noKontrak.split(';', -1)

                        'looping untuk mendapatkan total saldo yang digunakan per nomor kontrak'
                        for (i = 0; i < noKontrakPerDoc.size(); i++) {
                            'Mengambil value dari db mengenai tipe pembayran'
                            paymentType = CustomKeywords.'connection.DataVerif.getPaymentType'(conneSign, noKontrakPerDoc[
                                i])

                            'Jika tipe pembayarannya per sign'
                            if (paymentType == 'Per Sign') {
                                'Saldo usednya akan ditambah dengan value db penggunaan saldo'
                                saldoUsed = (saldoUsed + CustomKeywords.'connection.DataVerif.getSaldoUsedBasedonPaymentType'(
                                    conneSign, noKontrakPerDoc[i], emailSigner[(o - 1)]))

                                if (i == 0) {
                                    saldoUsedDocPertama = (saldoUsedDocPertama + CustomKeywords.'connection.DataVerif.getSaldoUsedBasedonPaymentType'(
                                        conneSign, noKontrakPerDoc[i], emailSigner[(o - 1)]))
                                }
                            } else {
                                saldoUsed = (saldoUsed + 1)

                                if (i == 0) {
                                    saldoUsedDocPertama = (saldoUsedDocPertama + CustomKeywords.'connection.DataVerif.getSaldoUsedBasedonPaymentType'(
                                        conneSign, noKontrakPerDoc[i], emailSigner[(o - 1)]))
                                }
                            }
                        }
                        
                        'Jumlah signer tanda tangan akan ditambah dengan total saldo yang telah digunakan'
                        jumlahSignerTandaTangan = (jumlahSignerTandaTangan + saldoUsed)

                        'Jumlah signer telah ttd lebih kepada proses tanda tangan telah bertambah 1.'
                        jumlahSignerTelahTtd = (jumlahSignerTelahTtd + 1)

                        'Looping maksimal 100 detik untuk signing proses. Perlu lama dikarenakan walaupun requestnya done(3), tapi dari VIDAnya tidak secepat itu.'
                        for (int y = 1; y <= 5; y++) {
                            'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                            WebUI.delay(20)

                            'Jika signing process db untuk signing false, maka'
                            if (signingProcessStoreDB(conneSign, emailSigner[(o - 1)], saldoUsedDocPertama) == false) {
                                'Jika looping waktu delaynya yang terakhir, maka'
                                if (y == 5) {
                                    'Failed dengan alasan prosesnya belum selesai'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
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
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page Berhasil mengirimkan permintaan tanda tangan dokumen.')

                        continue
                    }
                }
            }
            
            'Memanggil DocumentMonitoring untuk dicheck apakah proses ttdnya bertambah'
            WebUI.callTestCase(findTestCase('DocumentMonitoring/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathFESignDocument
                    , ('jumlahsignertandatangan') : jumlahSignerTelahTtd, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

            'Call test Case untuk login sebagai admin wom admin client'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], 
                FailureHandling.STOP_ON_FAILURE)

            'Split dokumen template name dan nomor kontrak per dokumen berdasarkan delimiter ;'
            documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

            noKontrakPerDoc = noKontrak.split(';', -1)

            'beri maks 30 sec mengenai perubahan total sign'
            for (int b = 1; b <= 3; b++) {
                'ambil saldo otp after'
                otpAfter = checkSaldoOtp()

                'ambil saldo after'
                saldoSignAfter = checkSaldoSign()

                'Jika count saldo otp after dengan yang before dikurangi 1 ditambah dengan '
                if (WebUI.verifyEqual(Integer.parseInt(otpBefore) - (countResend + 1), Integer.parseInt(otpAfter), FailureHandling.OPTIONAL)) {
                    'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang jumlah dokumen yang ditandatangani'
                    if (WebUI.verifyEqual(Integer.parseInt(saldoSignBefore) - saldoUsed, Integer.parseInt(saldoSignAfter), 
                        FailureHandling.OPTIONAL)) {
                        break
                    }
                } else {
                    'Masih sama, dikasi waktu delay 10'
                    WebUI.delay(10)

                    WebUI.refresh()
                }
            }
            
            'looping berdasarkan total dokumen dari dokumen template code'
            for (int i = 0; i < noKontrakPerDoc.size(); i++) {
                'Input filter di Mutasi Saldo'
                inputFilterTrx(conneSign, currentDate, noKontrakPerDoc[i], documentTemplateNamePerDoc[i])

                'Mengambil value dari db mengenai tipe pembayran'
                paymentType = CustomKeywords.'connection.DataVerif.getPaymentType'(conneSign, noKontrakPerDoc[i])

                'Jika tipe pembayarannya per sign'
                if (paymentType == 'Per Sign') {
                    'Memanggil saldo total yang telah digunakan per dokumen tersebut'
                    saldoUsedperDoc = CustomKeywords.'connection.DataVerif.getTotalSignedUsingRefNumber'(conneSign, noKontrakPerDoc[
                        i])
                } else {
                    saldoUsedperDoc = o
                }
                
                'delay dari 10 sampe 60 detik'
                for (int d = 1; d <= 6; d++) {
                    'Jika dokumennya ada, maka'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        'get column di saldo'
                        variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

                        'get row di saldo'
                        variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

                        'ambil inquiry di db'
                        ArrayList inquiryDB = CustomKeywords.'connection.DataVerif.gettrxSaldo'(conneSign, (noKontrakPerDoc[
                            i]).toString())

                        'looping mengenai columnnya'
                        for (int u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
                            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                                'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                                variableSaldoRow.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', 
                                true)

                            'Jika u di lokasi qty atau kolom ke 9'
                            if (u == 9) {
                                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[(u - 1)]) == '-1')) {
                                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                                    checkVerifyEqualorMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), 
                                            inquiryDB[(u - 1)], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                                        (noKontrakPerDoc[i]))
                                } else {
                                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                                    GlobalVariable.FlagFailed = 1

                                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                            2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                                        (noKontrakPerDoc[i]))
                                }
                            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                                'Jika di kolom ke 10, atau di FE table saldo'

                                'check saldo dari table dengan saldo yang sekarang'
                                checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(WebUI.getText(modifyperrowpercolumn)), 
                                        saldoUsedperDoc, FailureHandling.CONTINUE_ON_FAILURE), ' pada Saldo di Mutasi Saldo dengan nomor kontrak ' + 
                                    (noKontrakPerDoc[i]))
                            } else {
                                'Jika bukan untuk 2 kolom itu, maka check ke db'
                                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[
                                        (u - 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' + 
                                    (noKontrakPerDoc[i]))
                            }
                        }
                        
                        break
                    } else {
                        'jika kesempatan yang terakhir'
                        if (d == 6) {
                            'Jika masih tidak ada'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + 
                                (noKontrakPerDoc[i]))
                        }
                        
                        'delay 10 detik'
                        WebUI.delay(10)

                        'Klik cari'
                        WebUI.click(findTestObject('Saldo/btn_cari'))
                    }
                }
            }
        }
    }
}

' penggunaan ini hanya untuk Masukan Store Db'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        return false
    }
    
    return true
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

def masukanStoreDB(Connection conneSign, String emailSigner, ArrayList arrayMatch) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'MasukanDB mengambil value dari hasil query'
    masukanDB = CustomKeywords.'connection.DataVerif.getFeedbackStoreDB'(conneSign, emailSigner)

    'verify rating'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 62), masukanDB[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify komentar'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 63), masukanDB[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def signingProcessStoreDB(Connection conneSign, String emailSigner, int jumlahSignerTandaTangan) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'SigningDB mengambil value dari hasil query'
    signingDB = CustomKeywords.'connection.DataVerif.getSigningStatusProcess'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, 6), emailSigner)

    'looping berdasarkan size dari signingDB'
    for (int t = 1; t <= signingDB.size(); t++) {
        ArrayList arrayMatch = new ArrayList()

        'verify request status. 3 berarti done request. Terpaksa hardcode karena tidak ada masternya untuk 3.'
        arrayMatch.add(WebUI.verifyMatch('3', signingDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify sign date. Jika ada, maka teksnya Sudah TTD. Sign Date sudah dijoin ke email masing-masing, sehingga pengecekan apakah sudah sign atau belum ditandai disini'
        arrayMatch.add(WebUI.verifyMatch('Sudah TTD', signingDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify total signed. Total signed harusnya seusai dengan variable jumlah signed'
        arrayMatch.add(WebUI.verifyEqual(jumlahSignerTandaTangan, Integer.parseInt(signingDB[arrayIndex++]), FailureHandling.CONTINUE_ON_FAILURE))

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

def inputFilterTrx(Connection conneSign, String currentDate, String noKontrak, String documentTemplateName) {
    documentType = CustomKeywords.'connection.DataVerif.getDocumentType'(conneSign, noKontrak)

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
    WebUI.setText(findTestObject('Saldo/lbl_tipedokumen'), documentType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/lbl_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

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
            67), false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                68), FailureHandling.OPTIONAL)) {
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
    String totalSaldo = new ArrayList()

    'klik button saldo'
    WebUI.click(findTestObject('Saldo/btn_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_tenant'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            69), false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                70), FailureHandling.OPTIONAL)) {
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
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + lblpopup)

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
    }
    
    return false
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'ambil teks errormessage'
		errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

		if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
			'Tulis di excel itu adalah error'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + errormessage)

			return true
		}
    }
		
    return false
}

