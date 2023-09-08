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
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'Inisialisasi flag break untuk sequential'
int flagBreak = 0

'Inisialisasi array untuk Listotp, arraylist arraymatch'
ArrayList listOTP = []

ArrayList arrayMatch = []

'declare arrayindex'
arrayIndex = 0

documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
    -1)

for (o = 0; o < documentId.size(); o++) {
    String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

    'ambil db checking ke UI Beranda'
    ArrayList sendToSign = CustomKeywords.'connection.APIFullService.getDataSendtoSign'(conneSign, documentId[0])

    'Mengambil tenantCode dari excel berdasarkan input body API'
    String tenantCode = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, tenantCode)

    'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
    int jumlahSignerTandaTangan = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, refNumber)

    'saldoUsedDocPertama hanya untuk dokumen pertama'
    int saldoUsedDocPertama = 0

    'saldo Used untuk penggunaan saldo'
    int saldoUsed = 0

    'Inisialisasi variable yang dibutuhkan, Mengkosongkan nomor kontrak dan document Template Name'
    String noKontrak = '', documentTemplateName = '', noTelpSigner = ''

    'Inisialisasi variable total document yang akan disign, count untuk resend, dan saldo yang akan digunakan'
    int totalDocSign, countResend

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

    HashMap<String, String> resultSaldoBefore = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathFESignDocument
            , ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

    'mengambil saldo before'
    saldoSignBefore = resultSaldoBefore.get('saldoSign')

    'mengambil saldo otp before'
    saldoOtpBefore = resultSaldoBefore.get('saldoOTP')

    'tutup browsernya'
    WebUI.closeBrowser()

    'call Test Case untuk login sebagai user berdasarkan doc id'
    WebUI.callTestCase(findTestCase('Main Flow/Login_1docManySigner'), [('email') : emailSigner, ('excel') : excelPathFESignDocument], FailureHandling.CONTINUE_ON_FAILURE)

    String roleInput = CustomKeywords.'connection.SendSign.getRoleLogin'(conneSign, emailSigner, GlobalVariable.Tenant)

    if (checkPopup() == true) {
        break
    }
    
    checkBulkSigning()

    'refresh buat reset nav bar selanjutnya'
    WebUI.refresh()

    'Jika bukan di page 1, verifikasi menggunakan button Lastest. Get row lastest'
    variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

    'get row lastest'
    modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variableLastest.size()) + ']/a/i', true)

    'jika btn lastest dapat diclick'
    if (WebUI.verifyElementClickable(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
		WebUI.focus(modifyObjectBtnLastest)
		
        'Klik button Lastest'
        WebUI.click(modifyObjectBtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    'Jika ingin dilakukannya bulk sign'
    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'Ambil data dari excel mengenai total dokumen yang ditandatangani'
        totalDocSign = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Total Doc for Bulk Sign ?')).split(
            ';', -1)[GlobalVariable.indexUsed]).toInteger()
    } else if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'No') {
        'Total document sign hanya 1 (single)'
        totalDocSign = 1
    }
    
    'Looping berdasarkan page agar bergeser ke page sebelumnya'
    for (k = 1; k <= (variableLastest.size() - 4); k++) {
        'get row beranda'
        rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'looping untuk mengambil seluruh row'
        for (j = rowBeranda.size(); j >= 1; j--) {
            'deklarasi arrayIndex untuk pemakaian'
            arrayIndex = 0

			HashMap<String, TestObject> resultObject = modifyObject(j, roleInput)

            'Jika datanya match dengan db, mengenai referal number'
            if (WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextRefNumber')), sendToSign[arrayIndex++], false, FailureHandling.OPTIONAL) == 
            true) {
                'Mengenai tipe dokumen template'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateTipe')), sendToSign[
                        arrayIndex++], false, FailureHandling.OPTIONAL), ' pada tipe document template ')

                'Mengenai tanggal permintaan'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextTglPermintaan')), sendToSign[arrayIndex++], 
                        false, FailureHandling.OPTIONAL), ' pada tanggal permintaan ')

                'Input document Template Name dan nomor kontrak dari UI'
                documentTemplateName = WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName'))

                noKontrak = WebUI.getText(resultObject.get('modifyObjectTextRefNumber'))

                'Klik checkbox tanda tangan'
                WebUI.click(resultObject.get('modifyObjectCheckboxTtd'))

                'Jika total Document Signnya lebih besar dari 1, datanya continue'
                if (totalDocSign > 1) {
                    continue
                } else {
                    'Jika total document signnya itu 1, maka perlu break'
                    break
                }
            }
            
            'Jika bulk sign'
            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')).split(
                ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                'Jika loopingan sudah cukup untuk total doc sign'
                if (j == (rowBeranda.size() - totalDocSign)) {
                    break
                } else {
                    'Jika document Template Namenya masih kosong'
                    if (documentTemplateName == '') {
                        'Input document Template Name dan nomor kontrak dari UI'
                        documentTemplateName = WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName'))

                        noKontrak = WebUI.getText(resultObject.get('modifyObjectTextRefNumber'))
                    } else {
                        'Input document Template Name dan nomor kontrak dari UI ditambah dengan delimiter ;'
                        documentTemplateName = ((WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName')) + ';') + documentTemplateName)

                        noKontrak = ((WebUI.getText(resultObject.get('modifyObjectTextRefNumber')) + ';') + noKontrak)
                    }
                    
                    'Klik checkbox tanda tangan'
                    WebUI.click(resultObject.get('modifyObjectCheckboxTtd'))
                }
            }
        }
        
        'Jika masih tidak ditemukan datanya, maka'
        if (documentTemplateName == '') {
            'Klik previous'
            WebUI.click(findTestObject('KotakMasuk/Sign/button_Previous'))
        } else {
            'Jika sudah ditemukan, maka break'
            break
        }
        
        'Jika sudah dilooping terakhir mengenai pagenya dan tetap tidak menemukan'
        if ((k == (variableLastest.size() - 4)) && (documentTemplateName == '')) {
            'Input verifynya false dengan reason'
            checkVerifyEqualorMatch(false, ' dengan alasan tidak ditemukannya Nomor Kontrak yang diinginkan.')

            continue
        }
    }
    
    'Klik button Tanda tangan bulk'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TTDBulk'))

    'Split document Template Name berdasarkan delimiter'
    documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

    noKontrakPerDoc = noKontrak.split(';', -1)

    'diberi delay 2 detik untuk muncul popup'
    WebUI.delay(2)

    'check popup'
    if (checkPopup() == true) {
        'break untuk looping selanjutnya'
        flagBreak = 1

        'diberi break dengan alasan sequential signing'
        break
    }
    
    'Jika total document sign excel tidak sama dengan total document sign paging'
    if (totalDocSign != documentTemplateNamePerDoc.size()) {
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedTotalDocTidakSesuai) + 
            '<') + documentTemplateNamePerDoc.size()) + '> pada User ') + '<') + emailSigner) + '>')
    }
    
    'Looping berdasarkan total document sign'
    for (c = 0; c < documentTemplateNamePerDoc.size(); c++) {
        'modify object btn Nama Dokumen '
        modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
            'equals', ('id("ngb-nav-' + (c + 2)) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

        'verify nama dokumen massal dengan nama dokumen di paging'
        if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - 
            (c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
            'Jika tidak cocok, maka custom keywords jika tidak sama.'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                ' dimana tidak sesuai di page Bulk Sign antara ') + '<') + WebUI.getText(modifyObjectbtnNamaDokumen)) + 
                '>') + ' dengan ') + '<' + (documentTemplateNamePerDoc[c])) + '>')
        }
    }
    
    'Check konfirmasi tanda tangan'
    checkKonfirmasiTTD()

    'jika page belum pindah'
    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
        'Jika tidak ada, maka datanya tidak ada, atau save gagal'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + 
            ' dengan alasan page tidak berpindah di Bulk Sign View.')
    } else {
        'Looping berdasarkan document template name per dokumen'
        for (i = 0; i < documentTemplateNamePerDoc.size(); i++) {
            'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
            modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

            'verify nama dokumen dengan nama dokumen di paging'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), documentTemplateNamePerDoc[
                    (documentTemplateNamePerDoc.size() - (i + 1))], false), '')
        }
    }
    
    'Scroll ke btn Proses'
    WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

    'Klik button proses'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

    'mereset array index'
    arrayIndex = 0

    'check error log'
    if (checkErrorLog() == true) {
        break
    }
    
    'Jika error log tidak muncul, Jika verifikasi penanda tangan tidak muncul'
    if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL))) {
        'Custom keyword mengenai savenya gagal'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + 
            ' pada saat tidak muncul pop up Verifikasi Penanda Tangan')
    } else {
		noTelpSigner = checkBeforeChoosingOTPOrBiometric(emailSigner, conneSign, vendor)
		
        'Jika cara verifikasinya menggunakan OTP'
        if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CaraVerifikasi(Biometric/OTP)')).split(
            ';', -1)[GlobalVariable.indexUsed]) == 'OTP') {
            'Klik verifikasi by OTP'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifOTP'))

            'Memindahkan variable ke findTestObject'
            modifyObjectlabelRequestOTP = findTestObject('KotakMasuk/Sign/lbl_RequestOTP')

            'Jika button menyetujuinya yes'
            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Menyetujui(Yes/No)')).split(
                ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
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
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak bisa lanjut proses OTP')

                'kembali ke loop atas'
                continue
            }
            
            'check error log'
            if (checkErrorLog() == true) {
                continue
            }
            
            'Jika tidak muncul untuk element selanjutnya'
            if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE))) {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page input OTP')
            } else {
                'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 
                            'value'), noTelpSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' pada nomor telepon signer ')

                'OTP yang pertama dimasukkan kedalam 1 var'
                OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)

                'clear arraylist sebelumnya'
                listOTP.clear()

                'add otp ke list'
                listOTP.add(OTP)

                'bikin flag untuk dilakukan OTP by db'
                if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Correct OTP (Yes/No)')).split(
                    ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                    'value OTP dari db'
                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
                } else {
                    'value OTP dari excel'
                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(';', -1)[GlobalVariable.indexUsed])
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
                if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP (Yes/No)')).split(
                    ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                    'Ambil data dari excel mengenai countResend'
                    countResend = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CountResendOTP')).split(
                        ';', -1)[GlobalVariable.indexUsed]).toInteger()

                    'Looping dari 1 hingga total count resend OTP'
                    for (int w = 1; w <= countResend; w++) {
                        'taruh waktu delay'
                        WebUI.delay(298)

                        'Klik resend otp'
                        WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

                        'checkErrorLog'
                        checkErrorLog()

                        'check pop up'
                        checkPopup()

                        'Memberikan delay 3 karena OTP after terlalu cepat'
                        WebUI.delay(3)

                        'OTP yang kedua'
                        otpAfter = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)

                        'add otp ke list'
                        listOTP.add(otpAfter)

                        'dicheck OTP pertama dan kedua dan seterusnya'
                        if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.OPTIONAL)) {
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedOTPError)
                        }
                        
                        'Jika looping telah diterakhir, baru set text'
                        if (w == countResend) {
                            WebUI.delay(3)

                            'value OTP dari db'
                            WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), otpAfter, FailureHandling.CONTINUE_ON_FAILURE)

                            'klik verifikasi OTP'
                            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
                        }
                    }
                } else {
                    'tidak ada resend, namun menggunakan send otp satu kali'
                    countResend = 1
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
            'Klik biometric object'
            WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_verifBiom'))

            'Changing check di label request'
            modifyObjectlabelRequestOTP = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_RequestOTP'), 'xpath', 
                'equals', '/html/body/ngb-modal-window/div/div/app-camera-liveness/div[1]/h4', true)

            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Menyetujui(Yes/No)')).split(
                ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                'Klik button menyetujui untuk menandatangani'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
            }
            
            'Klik lanjut after konfirmasi'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)

            'Jika tidak muncul untuk element selanjutnya'
            if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page masukan')
            }
        }
        
        'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)) {
           inputMasukanAndWriteResultSign()
		   
            if (checkErrorLog() == true) {
                continue
            }
            
            'Verifikasi label pop up ketika masukan telah selesai dikirim'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedFeedbackGagal)
            } else {
                'Klik OK'
                WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
            }
            
            'Jika masukan ratingnya tidak kosong'
            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';', 
                -1)[GlobalVariable.indexUsed]) != '') {
                'StoreDB mengenai masukan'
                masukanStoreDB(conneSign, emailSigner, arrayMatch)
            }
            
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
            
            'Mensplit nomor kontrak yang telah disatukan'
            noKontrakPerDoc = noKontrak.split(';', -1)

            'looping untuk mendapatkan total saldo yang digunakan per nomor kontrak'
            for (i = 0; i < noKontrakPerDoc.size(); i++) {
                'Mengambil value dari db mengenai tipe pembayran'
                paymentType = CustomKeywords.'connection.APIFullService.getPaymentType'(conneSign, noKontrakPerDoc[i])

                if (i == 0) {
                    saldoUsedDocPertama = (saldoUsedDocPertama + CustomKeywords.'connection.APIFullService.getSaldoUsedBasedonPaymentType'(
                        conneSign, noKontrakPerDoc[i], emailSigner))
                }
                
                'Jika tipe pembayarannya per sign'
                if (paymentType == 'Per Sign') {
                    'Saldo usednya akan ditambah dengan value db penggunaan saldo'
                    saldoUsed = (saldoUsed + CustomKeywords.'connection.APIFullService.getSaldoUsedBasedonPaymentType'(conneSign, 
                        noKontrakPerDoc[i], emailSigner))
                } else {
                    saldoUsed = (saldoUsed + 1)
                }
            }
            
            'Jumlah signer tanda tangan akan ditambah dengan total saldo yang telah digunakan'
            jumlahSignerTandaTangan = (jumlahSignerTandaTangan + saldoUsed)

            'Looping maksimal 100 detik untuk signing proses. Perlu lama dikarenakan walaupun requestnya done(3), tapi dari VIDAnya tidak secepat itu.'
            for (y = 1; y <= 10; y++) {
                'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                WebUI.delay(20)

                'Jika signing process db untuk signing false, maka'
                if (signingProcessStoreDB(conneSign, emailSigner, saldoUsedDocPertama, documentId[o]) == false) {
                    'Jika looping waktu delaynya yang terakhir, maka'
                    if (y == 10) {
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
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + 
                ' dengan alasan tidak muncul page Berhasil mengirimkan permintaan tanda tangan dokumen.')

            continue
        }
    }
    
    'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
    WebUI.callTestCase(findTestCase('Main Flow/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathFESignDocument
            , ('sheet') : sheet, ('nomorKontrak') : noKontrak], FailureHandling.CONTINUE_ON_FAILURE)

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

    'Split dokumen template name dan nomor kontrak per dokumen berdasarkan delimiter ;'
    documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

    noKontrakPerDoc = noKontrak.split(';', -1)

    'beri maks 30 sec mengenai perubahan total sign'
    for (b = 1; b <= 3; b++) {
        HashMap<String, String> resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathFESignDocument
                , ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

        'mengambil saldo before'
        saldoSignAfter = resultSaldoAfter.get('saldoSign')

        'mengambil saldo otp before'
        saldoOtpAfter = resultSaldoAfter.get('saldoOTP')

        'Jika count saldo otp after dengan yang before dikurangi 1 ditambah dengan '
        if (WebUI.verifyEqual(Integer.parseInt(saldoOtpBefore) - countResend, Integer.parseInt(saldoOtpAfter), FailureHandling.OPTIONAL)) {
            'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang jumlah dokumen yang ditandatangani'
            if (WebUI.verifyEqual(Integer.parseInt(saldoSignBefore.replace(',', '')) - saldoUsed, Integer.parseInt(saldoSignAfter.replace(
                        ',', '')), FailureHandling.OPTIONAL)) {
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
        paymentType = CustomKeywords.'connection.APIFullService.getPaymentType'(conneSign, noKontrakPerDoc[i])

        'Jika tipe pembayarannya per sign'
        if (paymentType == 'Per Sign') {
            'Memanggil saldo total yang telah digunakan per dokumen tersebut'
            saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrakPerDoc[
                i])

            if (saldoUsedperDoc == '0') {
                WebUI.delay(10)

                'Memanggil saldo total yang telah digunakan per dokumen tersebut'
                saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrakPerDoc[
                    i])
            }
        } else {
            saldoUsedperDoc = o
        }
        
        'delay dari 10 sampe 60 detik'
        for (int d = 1; d <= 6; d++) {
            'Jika dokumennya ada, maka'
            if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get column di saldo'
                variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

                'get row di saldo'
                variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

                'ambil inquiry di db'
                ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, noKontrakPerDoc[
                    i], saldoUsedperDoc.toString())

                index = 0

                'check total row dengan yang tertandatangan'
                checkVerifyEqualorMatch(WebUI.verifyMatch(variableSaldoRow.size().toString(), saldoUsedperDoc.toString(), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' pada jumlah tertanda tangan dengan row transaksi ')

                'looping mengenai rownya'
                for (int j = 1; j <= variableSaldoRow.size(); j++) {
                    'looping mengenai columnnya'
                    for (int u = 1; u <= variableSaldoColumn.size(); u++) {
                        'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                        modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

                        'Jika u di lokasi qty atau kolom ke 9'
                        if (u == 9) {
                            'Jika yang qtynya 1 dan databasenya juga, berhasil'
                            if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[(u - 1)]) == '-1')) {
                                'Jika bukan untuk 2 kolom itu, maka check ke db'
                                checkVerifyEqualorMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[
                                        index++], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                                    (noKontrakPerDoc[i]))
                            } else {
                                'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                                GlobalVariable.FlagFailed = 1

                                'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                                    '<') + (noKontrakPerDoc[i])) + '>')
                            }
                        } else if (u == variableSaldoColumn.size()) {
                            'Jika di kolom ke 10, atau di FE table saldo, check saldo dari table dengan saldo yang sekarang. Takeout dari dev karena no issue dan sudah sepakat'
                        } else {
                            'Jika bukan untuk 2 kolom itu, maka check ke db'
                            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index++], 
                                    false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' + 
                                (noKontrakPerDoc[i]))
                        }
                    }
                }
                
                break
            } else {
                'jika kesempatan yang terakhir'
                if (d == 6) {
                    'Jika masih tidak ada'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + 
                        '<') + (noKontrakPerDoc[i])) + '>')
                }
                
                'delay 10 detik'
                WebUI.delay(10)

                'Klik cari'
                WebUI.click(findTestObject('Saldo/btn_cari'))
            }
        }
    }
    
    'check flagBreak untuk sequential'
    if (flagBreak == 1) {
        continue
    }

}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
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

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                '<') + lblpopup) + '>')

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
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

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                '<') + errormessage) + '>')

            return true
        }
    }
    
    return false
}

def inputFilterTrx(Connection conneSign, String currentDate, String noKontrak, String documentTemplateName) {
    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, noKontrak)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeSaldo')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeTransaksi')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))
}

def checkPopupWarning() {
    'Jika popup muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel sebagai failed dan error.'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusWarning, 
            (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<') + 
            lblpopup) + '>')

        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

        return true
    }
    
    return false
}

def checkBulkSigning() {
    'Klik checkbox ttd untuk semua'
    WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

    'Klik button ttd bulk'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

    if (checkPopupWarning() == false) {
        'klik tombol Batal'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))
    }
}

def modifyObject (int j, String roleInput) {
	'index row distart dari 2 karena yang 1 adalah button ttd'
	indexRow = 2

	TestObject modifyObjectTextNamaPelanggan 
	
	'modify object text refnum'
	modifyObjectTextRefNumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals',
		((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

	'modify object text document template tipe di beranda'
	modifyObjectTextDocumentTemplateTipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),
		'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

	'modify object text document template name di beranda'
	modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),
		'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

	if (roleInput != 'Customer') {
		'modify object text nama customer'
		modifyObjectTextNamaPelanggan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'),
			'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
			j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)
	}
	
	'modify object lbl tanggal permintaan'
	modifyObjectTextTglPermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath',
		'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/span', true)
	
	'modify object lbl proses ttd'
	modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath',
		'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/span', true)
	
	'modify object lbl status ttd'
	modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath',
		'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++ + 1) + ']/div/span', true)

	'modify object btn TTD Dokumen di beranda'
	modifyObjectCheckboxTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
		j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)
	
	HashMap<String, TestObject> result = new HashMap<>()
	
	result.put("modifyObjectTextRefNumber", modifyObjectTextRefNumber)
	
	result.put("modifyObjectTextDocumentTemplateTipe", modifyObjectTextDocumentTemplateTipe)
	
	result.put("modifyObjectTextDocumentTemplateName", modifyObjectTextDocumentTemplateName)
	
	result.put("modifyObjectTextNamaPelanggan", modifyObjectTextNamaPelanggan)
	
	result.put("modifyObjectTextTglPermintaan", modifyObjectTextTglPermintaan)
	
	result.put("modifyObjectCheckboxTtd", modifyObjectCheckboxTtd)
	
	result.put("modifyObjectTextStatusTtd", modifyObjectTextStatusTtd)
	
	result.put("modifyObjectTextProsesTtd", modifyObjectTextProsesTtd)
	
	return result
}

def checkBeforeChoosingOTPOrBiometric(String emailSigner,Connection conneSign, String vendor) {
	
	'Jika verifikasi penanda tangan muncul, Verifikasi antara email yang ada di UI dengan db'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_EmailAfterKonfirmasi'),
				'value'), emailSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Signer')

	'Get text nomor telepon'
	noTelpSigner = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_phoneNoAfterKonfirmasi'), 'value')

	'input text password'
	WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Password Signer')))

	'klik buka * pada passworod'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))

	'verifikasi objek text yang diambil valuenya dengan password'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'),
				'value'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer')),
			false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kata Sandi Signer')

	'verifikasi objek text yang diambil valuenya dengan nomor telepon'
	checkVerifyEqualorMatch(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(noTelpSigner),
			CustomKeywords.'connection.APIFullService.getHashedNo'(conneSign, emailSigner), false, FailureHandling.CONTINUE_ON_FAILURE), 'pada nomor telepon Signer')

	'cek jika vendor yang dipakai adalah privy'
	if (vendor.equalsIgnoreCase('Privy')) {
		'pastikan tombol verifikasi biometrik tidak muncul'
		if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			GlobalVariable.FlagFailed = 1

			'jika muncul, tulis error ke excel'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') +
				'Tombol Liveness muncul saat vendor Privy')
		}
	}
	
	return noTelpSigner
}

def inputMasukanAndWriteResultSign() {
	'Diberikan delay 4 sec dikarenakan loading'
	WebUI.delay(4)

	'Mendapat total success dan failed'
	String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

	String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

	'Menarik value count success ke excel'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Success') -
		1, GlobalVariable.NumofColm - 1, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
			rowExcel('Result Count Success')) + ';') + '<') + countSuccessSign) + '>')

	'Menarik value count failed ke excel'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Failed') -
		1, GlobalVariable.NumofColm - 1, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
			rowExcel('Result Count Failed')) + ';') + '<') + countFailedSign) + '>')

	'Jika masukan ratingnya tidak kosong'
	if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';',
		-1)[GlobalVariable.indexUsed]) != '') {
		'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
		modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'),
			'xpath', 'equals', ('//ngb-rating[@id=\'rating\']/span[' + (findTestData(excelPathFESignDocument).getValue(
				GlobalVariable.NumofColm, rowExcel('$Rating')).split(';', -1)[GlobalVariable.indexUsed].toInteger() * 2)) + ']/span',
			true)

		'Klik bintangnya bintang berapa'
		WebUI.click(modifyObjectstarMasukan)
	}
	
	'Jika komentarnya tidak kosoong'
	if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')).split(';',
		-1)[GlobalVariable.indexUsed]) != '') {
		'Input komentar di rating'
		WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
				GlobalVariable.NumofColm, rowExcel('comment')).split(';', -1)[GlobalVariable.indexUsed])
	}
	
	'Scroll ke btn Kirim'
	WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Kirim'), GlobalVariable.TimeOut)

	'klik button Kirim'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))

}

def signingProcessStoreDB(Connection conneSign, String emailSigner, int jumlahSignerTandaTangan, String documentId) {
	'deklarasi arrayIndex untuk penggunakan selanjutnya'
	arrayIndex = 0

	'SigningDB mengambil value dari hasil query'
	signingDB = CustomKeywords.'connection.SendSign.getSigningStatusProcess'(conneSign, documentId, emailSigner)

	'looping berdasarkan size dari signingDB'
	for (t = 1; t <= signingDB.size(); t++) {
		ArrayList<String> arrayMatch = new ArrayList<String>()

		'verify request status. 3 berarti done request. Terpaksa hardcode karena tidak ada masternya untuk 3.'
		arrayMatch.add(WebUI.verifyMatch('3', signingDB[arrayIndex++], false, FailureHandling.OPTIONAL))

		'verify sign date. Jika ada, maka teksnya Sudah TTD. Sign Date sudah dijoin ke email masing-masing, sehingga pengecekan apakah sudah sign atau belum ditandai disini'
		arrayMatch.add(WebUI.verifyMatch('Sudah TTD', signingDB[arrayIndex++], false, FailureHandling.OPTIONAL))

		'verify total signed. Total signed harusnya seusai dengan variable jumlah signed'
		arrayMatch.add(WebUI.verifyEqual(jumlahSignerTandaTangan, Integer.parseInt(signingDB[arrayIndex++]), FailureHandling.OPTIONAL))

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

def masukanStoreDB(Connection conneSign, String emailSigner, ArrayList<String> arrayMatch) {
	'deklarasi arrayIndex untuk penggunakan selanjutnya'
	arrayIndex = 0

	'MasukanDB mengambil value dari hasil query'
	masukanDB = CustomKeywords.'connection.APIFullService.getFeedbackStoreDB'(conneSign, emailSigner)

	'verify rating'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';', -1)[GlobalVariable.indexUsed], masukanDB[
			arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify komentar'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')).split(';', -1)[GlobalVariable.indexUsed], masukanDB[
			arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}