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
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as MobileBuiltInKeywords
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'Inisialisasi flag break untuk sequential'
int flagBreak = 0, isLocalhost = 0, alreadyVerif = 0,  saldoForCheckingDB = 0, jumlahHarusTandaTangan = 0, jumlahSignerTandaTangan = 0,  countAutosign = 0

HashMap<String, String> resultSaldoBefore, resultSaldoAfter

useBiom = 0

'reset value GV'
GlobalVariable.eSignData.putAt('VerifikasiOTP', 0)

GlobalVariable.eSignData.putAt('VerifikasiBiometric', 0)

'Inisialisasi array untuk Listotp, arraylist arraymatch'
ArrayList listOTP = [], arrayMatch = [], loopingEmailSigner = []

'declare arrayindex'
arrayIndex = 0

documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
    -1)

'inisialisasi variable'
forLoopingWithBreakAndContinue = 1

'looping'
for (o = 0; o < forLoopingWithBreakAndContinue; o++) {
    String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[
        0])

    'ambil kondisi default face compare'
    String mustFaceCompDB = CustomKeywords.'connection.DataVerif.getMustLivenessFaceCompare'(conneSign, GlobalVariable.Tenant)

    'ambil kondisi max liveness harian'
    int maxFaceCompDB = Integer.parseInt(CustomKeywords.'connection.DataVerif.getLimitLivenessDaily'(conneSign))

    'ambil metode verifikasi dari excel'
    verifMethod = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CaraVerifikasi(Biometric/OTP)')).split(
        ';', -1)

    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

    'ambil db checking ke UI Beranda'
    ArrayList sendToSign = CustomKeywords.'connection.APIFullService.getDataSendtoSign'(conneSign, GlobalVariable.storeVar.keySet()[
        0])

    'Mengambil tenantCode dari excel berdasarkan input body API'
    String tenantCode = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

    'dapatkan count untuk limit harian facecompare akun tersebut'
    int countLivenessFaceComp = CustomKeywords.'connection.DataVerif.getCountFaceCompDaily'(conneSign, GlobalVariable.storeVar.getAt(
            GlobalVariable.storeVar.keySet()[0]))

    'saldoUsedDocPertama hanya untuk dokumen pertama'
    int saldoUsedDocPertama = 0

    int saldoUsed = 0

    boolean ifDocFirstChecked = false, checkingAutoSign = false

    HashMap<String, String> result = new HashMap<String, String>()

    result = generateEncryptMessage(conneSign, GlobalVariable.storeVar.keySet()[0], GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[
            0]), tenantCode)

    'Inisialisasi variable yang dibutuhkan, Mengkosongkan nomor kontrak dan document Template Name'
    String noKontrak = ''

    String documentTemplateName = ''

    String noTelpSigner = ''

    String otpAfter

    String signType

    'Inisialisasi variable total document yang akan disign, count untuk resend, dan saldo yang akan digunakan'
    int totalDocSign

    int countResend = 0

    int countSaldoSplitLiveFCused = 0

    HashMap<String, String> resultSaldoBefore = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathFESignDocument
            , ('sheet') : sheet, ('vendor') : vendor, ('usageSaldo') : 'Sign'], FailureHandling.CONTINUE_ON_FAILURE)

    if (vendor.equalsIgnoreCase('Digisign')) {
        signType = 'Dokumen'
    } else {
        signType = 'TTD'
    }
    
    'mengambil saldo before'
    saldoSignBefore = resultSaldoBefore.get(signType)

    'tutup browsernya'
    WebUI.closeBrowser()

    'ubah flag untuk buka localhost jika syarat if terpenuhi'
    if (!(vendor.equalsIgnoreCase('Privy')) && (mustFaceCompDB == '1')) {
        'ubah keperluan untuk pakai Localhost'
        isLocalhost = 1
    }
    
    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excelPathFESignDocument, ('sheet') : sheet, ('linkUrl') : result.get(
                'encryptKotakMasuk')], FailureHandling.CONTINUE_ON_FAILURE)

    checkBulkSigning()

    refreshWebsite()

    WebUI.delay(3)

    'Get row lastest'
    variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'get row lastest'
    modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/button_Lastest'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variableLastest.size()) + ']/a', true)

    'jika btn lastest dapat diclick'
    if (WebUI.verifyElementClickable(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
        'Klik button Lastest'
        WebUI.click(modifyObjectBtnLastest, FailureHandling.OPTIONAL)
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
    
    if (variableLastest.size() == 0) {
        totalPage = 1
    } else {
        totalPage = (variableLastest.size() - 4)
    }
    
    'Looping berdasarkan page agar bergeser ke page sebelumnya'
    for (k = 1; k <= totalPage; k++) {
        'get row beranda'
        rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'looping untuk mengambil seluruh row'
        for (j = rowBeranda.size(); j >= 1; j--) {
            'deklarasi arrayIndex untuk pemakaian'
            arrayIndex = 0

            HashMap<String, String> resultObject = modifyObject(j)

            if (ifDocFirstChecked == false) {
                'Jika datanya match dengan db, mengenai referal number'
                if (WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextRefNumber')), sendToSign[arrayIndex++], 
                    false, FailureHandling.OPTIONAL) == true) {
                    'Mengenai tipe dokumen template'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateTipe')), 
                            sendToSign[arrayIndex++], false, FailureHandling.OPTIONAL), ' pada tipe document template ')

                    'Mengenai tanggal permintaan'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(resultObject.get('modifyObjectTextTglPermintaan')), 
                            sendToSign[arrayIndex++], false, FailureHandling.OPTIONAL), ' pada tanggal permintaan ')

                    countAutosign = CustomKeywords.'connection.APIFullService.getTotalAutosignOnDocument'(conneSign, GlobalVariable.storeVar.keySet()[
                        0])

                    if (vendor.equalsIgnoreCase('Privy')) {
                        if (countAutosign > 0) {
                            modifyObjectTextProsesTtd = WebUI.getText(resultObject.get('modifyObjectTextProsesTtd')).split(
                                ' / ', -1)

                            countAutosign++

                            if ((Integer.parseInt(modifyObjectTextProsesTtd[1]) - countAutosign) == Integer.parseInt(modifyObjectTextProsesTtd[
                                0])) {
                                checkingAutoSign = true
                            }
                        }
                    }
                    
                    'Input document Template Name dan nomor kontrak dari UI'
                    documentTemplateName = WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName'))

                    noKontrak = WebUI.getText(resultObject.get('modifyObjectTextRefNumber'))

                    'Klik checkbox tanda tangan'
                    WebUI.click(resultObject.get('modifyObjectCheckboxTtd'))

                    ifDocFirstChecked = true

                    'Jika total Document Signnya lebih besar dari 1, datanya continue'
                    if (totalDocSign > 1) {
                        continue
                    } else {
                        'Jika total document signnya itu 1, maka perlu break'
                        break
                    }
                }
            }
            
            'Jika bulk sign'
            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')).split(
                ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                'Jika loopingan sudah cukup untuk total doc sign'
                if (documentTemplateName.split(';', -1).size() == totalDocSign) {
                    break
                } else {
                    if (ifDocFirstChecked == true) {
                        'Jika document Template Namenya masih kosong'
                        if (documentTemplateName == '') {
                            'Input document Template Name dan nomor kontrak dari UI'
                            documentTemplateName = WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName'))

                            noKontrak = WebUI.getText(resultObject.get('modifyObjectTextRefNumber'))
                        } else {
                            'Input document Template Name dan nomor kontrak dari UI ditambah dengan delimiter ;'
                            documentTemplateName = ((WebUI.getText(resultObject.get('modifyObjectTextDocumentTemplateName')) + 
                            ';') + documentTemplateName)

                            noKontrak = ((WebUI.getText(resultObject.get('modifyObjectTextRefNumber')) + ';') + noKontrak)
                        }
                        
                        'Klik checkbox tanda tangan'
                        WebUI.click(resultObject.get('modifyObjectCheckboxTtd'))
                    }
                }
            }
        }
        
        'Jika sudah dilooping terakhir mengenai pagenya dan tetap tidak menemukan'
        if ((k == totalPage) && (documentTemplateName == '')) {
            'Input verifynya false dengan reason'
            checkVerifyEqualorMatch(false, ' dengan alasan tidak ditemukannya Nomor Kontrak yang diinginkan.')

            continue
        }
        
        'Jika masih tidak ditemukan datanya, maka'
        if (documentTemplateName == '') {
            'Klik previous'
            WebUI.click(findTestObject('APIFullService/Send to Sign/button_Previous'))
        } else {
            'Jika sudah ditemukan, maka break'
            break
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
            (((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedTotalDocTidakSesuai) + '<') + documentTemplateNamePerDoc.size()) + '> pada User ') + 
            '<') + GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0])) + '>')
    }
    
    if (!(vendor.equalsIgnoreCase('Digisign'))) {
        'Looping berdasarkan total document sign'
        for (c = 0; c < documentTemplateNamePerDoc.size(); c++) {
            'modify object btn Nama Dokumen '
            modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
                'equals', ('id("ngb-nav-' + c) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

            'verify nama dokumen massal dengan nama dokumen di paging'
            if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - 
                (c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
                'Jika tidak cocok, maka custom keywords jika tidak sama.'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusWarning, 
                    ((((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' dimana tidak sesuai di page Bulk Sign antara ') + 
                    '<') + WebUI.getText(modifyObjectbtnNamaDokumen)) + '> dengan ') + '<') + (documentTemplateNamePerDoc[
                    c])) + '>')
            }
        }
        
        'Check konfirmasi tanda tangan'
        checkKonfirmasiTTD()

        'jika page belum pindah'
        if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL))) {
            'Jika tidak ada, maka datanya tidak ada, atau save gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan page tidak berpindah di Bulk Sign View.')
        } else {
            'Looping berdasarkan document template name per dokumen'
            for (i = 0; i < documentTemplateNamePerDoc.size(); i++) {
                'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
                modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                    'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

                'verify nama dokumen dengan nama dokumen di paging'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), documentTemplateNamePerDoc[
                        (documentTemplateNamePerDoc.size() - (i + 1))], false), ' pada nama dokumen setelah konfirmasi ')
            }
        }
        
        WebUI.delay(1)

        'Scroll ke btn Proses'
        WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

        'Klik button proses'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

        WebUI.delay(3)

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
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' pada saat tidak muncul pop up Verifikasi Penanda Tangan')
        } else {
            noTelpSigner = checkBeforeChoosingOTPOrBiometric(GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[
                    0]), conneSign, vendor)

            'jika metode verifikasi tidak muncul'
            if ((verifMethod[GlobalVariable.indexUsed]).equalsIgnoreCase('Biometric')) {
                'cek apakah button biom tidak muncul'
                if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL))) {
                    'cek apakah mau ganti method'
                    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')).split(
                        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                        'panggil fungsi penyelesaian dengan OTP'
                        if (verifOTPMethod(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]), 
                            listOTP, noTelpSigner, otpAfter, vendor) == false) {
                            'jika ada error continue testcase'
                            continue
                        }
                        
                        alreadyVerif = 1
                    } else {
                        'tulis excel error dan lanjutkan testcase'

                        'jika muncul, tulis error ke excel'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'Verifikasi ') + (verifMethod[GlobalVariable.indexUsed])) + 
                            ' tidak tersedia')

                        continue
                    }
                }
            } else if ((verifMethod[GlobalVariable.indexUsed]).equalsIgnoreCase('OTP')) {
                'cek apakah button otp tidak muncul'
                if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifOTP'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL))) {
                    'cek apakah mau ganti method'
                    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')).split(
                        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                        'panggil fungsi verif menggunakan biometrik'
                        if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar.getAt(
                                GlobalVariable.storeVar.keySet()[0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                            'jika ada error break testcase'
                            break
                        }
                        
                        alreadyVerif = 1
                    } else {
                        'tulis excel error dan lanjutkan testcase'

                        'jika muncul, tulis error ke excel'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'Verifikasi OTP tidak tersedia')

                        continue
                    }
                }
            }
            
            'jika case privy dan mustliveness aktif serta diatas limit'
            if (vendor.equalsIgnoreCase('Privy') || (((mustFaceCompDB == '1') && (countLivenessFaceComp >= maxFaceCompDB)) && 
            (alreadyVerif == 0))) {
                'pastikan tombol verifikasi biometrik tidak muncul'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    GlobalVariable.FlagFailed = 1

                    'jika muncul, tulis error ke excel'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tombol Liveness muncul saat mustLiveness aktif dan limit sudah terpenuhi')
                }
                
                'jika tidak sesuai kondisi'
                if (vendor.equalsIgnoreCase('Privy') && (verifMethod[GlobalVariable.indexUsed]).equalsIgnoreCase('Biometric')) {
                    'jika muncul, tulis error ke excel'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Privy tidak mensupport verifikasi Biometric')

                    continue
                }
                
                'panggil fungsi penyelesaian dengan OTP'
                if (verifOTPMethod(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]), listOTP, 
                    noTelpSigner, otpAfter, vendor) == false) {
                    'cek apakah ingin coba metode lain'
                    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(
                        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                        'klik tombol untuk kembali ke laman proses'
                        WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))

                        inputDataforVerif()

                        'panggil fungsi verif menggunakan biometrik'
                        if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar.getAt(
                                GlobalVariable.storeVar.keySet()[0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                            'jika ada error break testcase'
                            break
                        }
                    } else {
                        'jika ada error continue testcase'
                        continue
                    }
                }
            } else if (((mustFaceCompDB == '1') && (countLivenessFaceComp < maxFaceCompDB)) && (alreadyVerif == 0)) {
                'pastikan button otp tidak ada'
                checkVerifyEqualorMatch(WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/btn_verifOTP'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL), 'Tombol OTP muncul pada Vendor selain Privy yang mewajibkan FaceCompare')

                'panggil fungsi verif menggunakan biometrik'
                if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar.getAt(
                        GlobalVariable.storeVar.keySet()[0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                    'cek apakah ingin coba metode lain'
                    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(
                        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                        'klik tombol untuk kembali ke laman proses'
                        WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))

                        inputDataforVerif()

                        'panggil fungsi penyelesaian dengan OTP'
                        if (verifOTPMethod(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]), 
                            listOTP, noTelpSigner, otpAfter, vendor) == false) {
                            'jika ada error continue testcase'
                            continue
                        }
                    } else {
                        'jika ada error break testcase'
                        break
                    }
                }
            } else if (alreadyVerif == 0) {
                'Jika cara verifikasinya menggunakan OTP'
                if ((verifMethod[GlobalVariable.indexUsed]) == 'OTP') {
                    'panggil fungsi penyelesaian dengan OTP'
                    if (verifOTPMethod(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]), listOTP, 
                        noTelpSigner, otpAfter, vendor) == false) {
                        'cek apakah ingin coba metode lain'
                        if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(
                            ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                            'klik tombol untuk kembali ke laman proses'
                            WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))

                            inputDataforVerif()

                            'panggil fungsi verif menggunakan biometrik'
                            if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar.getAt(
                                    GlobalVariable.storeVar.keySet()[0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                                'jika ada error break testcase'
                                break
                            }
                        } else {
                            'jika ada error continue testcase'
                            continue
                        }
                    }
                } else if ((verifMethod[GlobalVariable.indexUsed]) == 'Biometric') {
                    'panggil fungsi verif menggunakan biometrik'
                    if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar.getAt(
                            GlobalVariable.storeVar.keySet()[0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                        'cek apakah ingin coba metode lain'
                        if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(
                            ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
                            'klik tombol untuk kembali ke laman proses'
                            WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))

                            inputDataforVerif()

                            'panggil fungsi penyelesaian dengan OTP'
                            if (verifOTPMethod(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[
                                    0]), listOTP, noTelpSigner, otpAfter, vendor) == false) {
                                'jika ada error continue testcase'
                                continue
                            }
                        } else {
                            'jika ada error break testcase'
                            break
                        }
                    }
                }
            }
        }
        
        WebUI.delay(2)

        'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)) {
            'Diberikan delay 4 sec dikarenakan loading'
            WebUI.delay(4)

            inputMasukanAndWriteResultSign()

            if (checkErrorLog() == true) {
                continue
            }
            
            'Verifikasi label pop up ketika masukan telah selesai dikirim'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
            } else {
                'Klik OK'
                WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
            }
            
            'Mensplit nomor kontrak yang telah disatukan'
            noKontrakPerDoc = noKontrak.split(';', -1)

            'Jika masukan ratingnya tidak kosong'
            if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';', 
                -1)[GlobalVariable.indexUsed]) != '') {
                'StoreDB mengenai masukan'
                masukanStoreDB(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]), arrayMatch)
            }
            
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
            }
            
            'Mensplit nomor kontrak yang telah disatukan'
            noKontrakPerDoc = noKontrak.split(';', -1)

            'looping untuk mendapatkan total saldo yang digunakan per nomor kontrak'
            for (i = 0; i < noKontrakPerDoc.size(); i++) {
                if (checkingAutoSign == true) {
                    loopingEmailSigner = CustomKeywords.'connection.APIFullService.getSignersAutosignOnDocument'(conneSign, 
                        GlobalVariable.storeVar.keySet()[0])
                }
                
                loopingEmailSigner.add(0, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]))

                for (looping = 0; looping < loopingEmailSigner.size(); looping++) {
                    'Looping maksimal 100 detik untuk signing proses. Perlu lama dikarenakan walaupun requestnya done(3), tapi dari VIDAnya tidak secepat itu.'
                    for (y = 1; y <= 10; y++) {
                        'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
                        jumlahSignerTandaTangan = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, GlobalVariable.storeVar.keySet()[
                            0])

                        jumlahHarusTandaTangan = CustomKeywords.'connection.APIFullService.getTotalSign'(conneSign, GlobalVariable.storeVar.keySet()[
                            0])

                        'Mengambil value dari db mengenai tipe pembayran'
                        paymentType = CustomKeywords.'connection.APIFullService.getPaymentTypeMULTIDOC'(conneSign, GlobalVariable.storeVar.keySet()[
                            0])

                        saldoForCheckingDB = CustomKeywords.'connection.APIFullService.getSaldoUsedBasedonPaymentTypeMULTIDOC'(
                            conneSign, GlobalVariable.storeVar.keySet()[0], GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[
                                0]))

                        if (i == 0) {
                            saldoUsedDocPertama = (saldoUsedDocPertama + saldoForCheckingDB)
                        }
                        
                        if (y == 1) {
                            'Jika tipe pembayarannya per sign'
                            if (paymentType == 'Per Sign') {
                                'Saldo usednya akan ditambah dengan value db penggunaan saldo'
                                saldoUsed = (saldoUsed + saldoForCheckingDB)
                            } else {
                                saldoUsed = (saldoUsed + 1)
                            }
                        }
                        
                        if ((jumlahSignerTandaTangan != 0) && (jumlahSignerTandaTangan != jumlahHarusTandaTangan)) {
                            if (jumlahSignerTandaTangan == saldoForCheckingDB) { 
								saldoForCheckingDB = jumlahSignerTandaTangan
							} else {
							'menambah saldo for checking db ke jumlah signer tanda tangan'
                            saldoForCheckingDB = (saldoForCheckingDB + jumlahSignerTandaTangan)
							}
                        } else if ((jumlahSignerTandaTangan != 0) && (jumlahSignerTandaTangan == jumlahHarusTandaTangan)) {
                            saldoForCheckingDB = jumlahSignerTandaTangan
                        }
                        
                        jumlahSignerTandaTangan = (jumlahSignerTandaTangan + saldoUsed)

                        'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                        WebUI.delay(20)

                        'Jika signing process db untuk signing false, maka'
                        if (signingProcessStoreDB(conneSign, loopingEmailSigner[looping], saldoForCheckingDB, GlobalVariable.storeVar.keySet()[
                            0]) == false) {
                            if (GlobalVariable.ErrorType.size() > 0) {
                                saldoForCheckingDB = (saldoForCheckingDB - jumlahSignerTandaTangan)

                                saldoUsed = (saldoUsed - saldoForCheckingDB)

                                break
                            }
                            
                            'Jika looping waktu delaynya yang terakhir, maka'
                            if (y == 10) {
                                'Failed dengan alasan prosesnya belum selesai'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProcessNotDone)
                            }
                        } else {
                            'Jika hasil store dbnya true, maka'
                            break

                        }
                    }
                }
            }
			
			if (flagBreak == 1) {
				break
			}

        } else {
            'Jika popup berhasilnya tidak ada, maka Savenya gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page Berhasil mengirimkan permintaan tanda tangan dokumen.')

            continue
        }
    } else {
        'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
        WebUI.callTestCase(findTestCase('Main Flow/Signing Digisign'), [('excelPathFESignDocument') : excelPathFESignDocument
                , ('sheet') : sheet, ('nomorKontrak') : noKontrak, ('documentId') : documentId, ('emailSigner') : GlobalVariable.storeVar.getAt(
                    GlobalVariable.storeVar.keySet()[0])], FailureHandling.CONTINUE_ON_FAILURE)

        saldoUsed = (saldoUsed + 1)

        GlobalVariable.eSignData.putAt('VerifikasiOTP', 1)
    }
    
    'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
    WebUI.callTestCase(findTestCase('Main Flow/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathFESignDocument
            , ('sheet') : sheet, ('linkDocumentMonitoring') : result.get('encryptDocumentMonitoring'), ('nomorKontrak') : noKontrak
            , ('CancelDocsSign') : CancelDocsSign, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

    'Split dokumen template name dan nomor kontrak per dokumen berdasarkan delimiter ;'
    documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

    noKontrakPerDoc = noKontrak.split(';', -1)

    'beri maks 30 sec mengenai perubahan total sign'
    for (b = 1; b <= 1; b++) {
        resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathFESignDocument
                , ('sheet') : sheet, ('vendor') : vendor, ('usageSaldo') : 'Sign'], FailureHandling.CONTINUE_ON_FAILURE)

        'mengambil saldo before'
        saldoSignAfter = resultSaldoAfter.get(signType)
		
        'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang jumlah dokumen yang ditandatangani'
        if (WebUI.verifyEqual(Integer.parseInt(saldoSignBefore) - saldoUsed, Integer.parseInt(saldoSignAfter), FailureHandling.OPTIONAL)) {
            countResend = GlobalVariable.eSignData.getAt('VerifikasiOTP')

            countSaldoSplitLiveFCused = GlobalVariable.eSignData.getAt('VerifikasiBiometric')

            flagBreak = 0

            'cek apa pernah menggunakan biometrik'
            if (useBiom == 0) {
                'Jika count saldo otp after dengan yang before dikurangi 1 ditambah dengan '
                if (WebUI.verifyEqual(Integer.parseInt(resultSaldoBefore.get('OTP')) - countResend, Integer.parseInt(resultSaldoAfter.get(
                            'OTP')), FailureHandling.OPTIONAL)) {
                    flagBreak = 1
                }
            }
            
            if (countSaldoSplitLiveFCused > 0) {
                'cek saldo liveness facecompare dipisah atau tidak'
                String isSplitLivenessFc = CustomKeywords.'connection.APIFullService.getSplitLivenessFaceCompareBill'(conneSign)

                'jika saldo liveness digabung dengan facecompare'
                if (isSplitLivenessFc == '0') {
                    'cek apakah saldo liveness facecompare masih sama'
                    if (WebUI.verifyEqual(Integer.parseInt(resultSaldoBefore.get('Liveness Face Compare')) - countSaldoSplitLiveFCused, 
                        Integer.parseInt(resultSaldoAfter.get('Liveness Face Compare')), FailureHandling.OPTIONAL)) {
                        flagBreak = 1
                    }
                } else if (isSplitLivenessFc == '1') {
                    'cek apakah saldo liveness dan facecompare sama'
                    if (WebUI.verifyEqual(Integer.parseInt(resultSaldoBefore.get('Liveness')) - countSaldoSplitLiveFCused, 
                        Integer.parseInt(resultSaldoAfter.get('Liveness')), FailureHandling.OPTIONAL) && WebUI.verifyEqual(
                        Integer.parseInt(resultSaldoBefore.get('Face Compare')) - countSaldoSplitLiveFCused, Integer.parseInt(
                            resultSaldoAfter.get('Face Compare')), FailureHandling.OPTIONAL)) {
                        flagBreak = 1
                    }
                }
            }
        } else {
            if (b == 3) {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + ' saldo Pemotongan Tanda Tangan Before tidak sesuai dengan After ')
            }
        }
        
        'Masih sama, dikasi waktu delay 10'
        WebUI.delay(10)

        WebUI.refresh()

        if (flagBreak == 1) {
            break
        }
    }
    
	WebUI.comment(resultSaldoBefore.toString())
	WebUI.comment(resultSaldoAfter.toString())
	
    'looping berdasarkan total dokumen dari dokumen template code'
    for (i = 0; i < noKontrakPerDoc.size(); i++) {
        'Input filter di Mutasi Saldo'
        inputFilterTrx(conneSign, currentDate, noKontrakPerDoc[i], documentTemplateNamePerDoc[i], signType)

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
            saldoUsedperDoc = 1
        }
        
        'delay dari 10 sampe 60 detik'
        for (d = 1; d <= 6; d++) {
            'get column di saldo'
            variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

            'get row di saldo'
            variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            if (signType == 'Dokumen') {
                signType = 'Document'
            } else if (signType == 'TTD') {
                signType = 'Sign'
            }
            
            'Jika dokumennya ada, maka'
            if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'ambil inquiry di db'
                ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, noKontrakPerDoc[
                    i], saldoForCheckingDB.toString(), 'Use ' + signType)

                index = 0

                'looping mengenai rownya'
                for (j = 1; j <= variableSaldoRow.size(); j++) {
                    'looping mengenai columnnya'
                    for (u = 1; u <= variableSaldoColumn.size(); u++) {
                        'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                        modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

                        WebUI.scrollToElement(modifyperrowpercolumn, GlobalVariable.TimeOut)

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
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
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
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + 
                        '<') + (noKontrakPerDoc[i])) + '>')
                }
                
                'delay 10 detik'
                WebUI.delay(10)

                'Klik cari'
                WebUI.click(findTestObject('Saldo/btn_cari'))
            }
        }
    }
    
	checkAutoStamp(conneSign, noKontrak, resultSaldoBefore)
	
    'check flagBreak untuk sequential'
    if (flagBreak == 1) {
        continue
    }
} 

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptLink(Connection conneSign, String documentId, String emailSigner, String aesKey) {
    'get current date'
    def currentDateTimeStamp = new Date().format('yyyy-MM-dd HH:mm:ss')

    officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId)

    'pembuatan message yang akan dienkrip'
    msg = (((((('{"officeCode" : "' + officeCode) + '", "email" : ') + emailSigner) + ',"timestamp" : "') + currentDateTimeStamp) + 
    '"}')
	
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(msg, aesKey)

    return encryptMsg
}

def runWithEmbed(String linkUrl) {
    'check if ingin menggunakan embed atau tidak'
    if (GlobalVariable.RunWithEmbed == 'Yes') {
        'navigate url ke daftar akun'
        WebUI.openBrowser(GlobalVariable.embedUrl)

        'Diberikan delay 3 sec'
        WebUI.delay(3)

        'Maximize windows'
        WebUI.maximizeWindow()

        'Set text link Url'
        WebUI.setText(findTestObject('EmbedView/inputLinkEmbed'), linkUrl)

        'click button embed'
        WebUI.click(findTestObject('EmbedView/button_Embed'))

        'swith to iframe'
        WebUI.switchToFrame(findTestObject('EmbedView/iFrameEsign'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
    } else if (GlobalVariable.RunWithEmbed == 'No') {
        'navigate url ke daftar akun'
        WebUI.openBrowser(linkUrl)

        'Maximize Windows'
        WebUI.maximizeWindow()
    }
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

		if (!(lblpopup.contains('Kode OTP salah')) && !(lblpopup.contains('Kode OTP Anda sudah kadaluarsa'))) {
        'Tulis di excel sebagai failed dan error.'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + '<') + lblpopup) + '>')

			return true
		}

        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

    }
}

def checkPopupDoneness() {
	'Jika popup muncul'
	if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	} else {
		'label popup diambil'
		lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

		'Tulis di excel sebagai failed dan error.'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + '<') + lblpopup) + '>')

		'Klik OK untuk popupnya'
		WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

	}
}

def checkKonfirmasiTTD() {
    'Klik tanda tangan'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TtdSemuaTandaTanganDokumen'))

    'Klik tidak untuk konfirmasi ttd'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TidakTandaTanganDokumen'))

    WebUI.delay(1)

    'Klik tanda tangan'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TtdSemuaTandaTanganDokumen'))

    'Klik ya untuk konfirmasi ttd'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_YaTandaTanganDokumen'))
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (errormessage != null) {
            if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + errormessage) + '>')

                return true
            }
        }
    }
    
    return false
}

def inputFilterTrx(Connection conneSign, String currentDate, String noKontrak, String documentTemplateName, String signType) {
    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, noKontrak)

    if (signType == 'Dokumen') {
        signType = 'Document'
    } else if (signType == 'TTD') {
        signType = 'Sign'
    }
    
    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), signType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use ' + signType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

      // 'Input tipe dokumen'
    //WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

  //  'Input enter'
    //WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

  //  'Input documentTemplateName'
 //   WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

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
            (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + '<') + lblpopup) + '>')

        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

        GlobalVariable.FlagFailed = 1

        return true
    }
    
    return false
}

def generateEncryptMessage(Connection conneSign, String documentId, String emailSigner, String tenantCode) {
    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, tenantCode)

    encryptMsg = encryptLink(conneSign, documentId, emailSigner, aesKey)

    'membuat link document monitoring'
    linkDocumentMonitoring = ((((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Base Link Document Monitoring')) + 
    '?msg=') + encryptMsg) + '&isHO=') + (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
            'isHO')).split(';', -1)[GlobalVariable.indexUsed])) + '&isMonitoring=') + (findTestData(excelPathFESignDocument).getValue(
        GlobalVariable.NumofColm, rowExcel('isMonitoring')))) + '&tenantCode=') + 
    tenantCode)

    'membuat link kotak masuk'
    linkKotakMasuk = ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Base Link KotakMasuk')) + 
    '?msg=') + encryptMsg) + '&tenantCode=') + tenantCode)

    if (opsiSigning == 'Embed Sign') {
        GlobalVariable.RunWithEmbed = 'Yes'
    } else {
        GlobalVariable.RunWithEmbed = 'No'
    }
    
    HashMap<String, String> result = new HashMap<String, String>()

    result.put('encryptDocumentMonitoring', linkDocumentMonitoring)

    result.put('encryptKotakMasuk', linkKotakMasuk)

    return result
}

def checkBeforeChoosingOTPOrBiometric(String emailSigner, Connection conneSign, String vendor) {
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
            false), 'pada Kata Sandi Signer')

    'verifikasi objek text yang diambil valuenya dengan nomor telepon'
    checkVerifyEqualorMatch(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(noTelpSigner), 
            CustomKeywords.'connection.APIFullService.getHashedNo'(conneSign, emailSigner), false, FailureHandling.CONTINUE_ON_FAILURE), 
        'pada nomor telepon Signer')

    'cek jika vendor yang dipakai adalah privy'
    if (vendor.equalsIgnoreCase('Privy')) {
        'pastikan tombol verifikasi biometrik tidak muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            GlobalVariable.FlagFailed = 1

            'jika muncul, tulis error ke excel'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tombol Liveness muncul saat vendor Privy')
        }
    }
    
    return noTelpSigner
}

def modifyObject(int j) {
    'modify object text document template name di beranda'
    modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

    'modify object text document template tipe di beranda'
    modifyObjectTextDocumentTemplateTipe = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

    'modify object btn TTD Dokumen di beranda'
    modifyObjectCheckboxTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

    'modify object lbl tanggal permintaan'
    modifyObjectTextTglPermintaan = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/span', true)

    'modify object nama pelanggan'
    modifyObjectLblNamaPelanggan = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

    'modify object text no kontrak di beranda'
    modifyObjectTextRefNumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

    'modify object test status tanda tangan di beranda'
    modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/p', true)

    'modify object text proses ttd di beranda'
    modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/p', true)

    HashMap<String, String> result = new HashMap<String, String>()

    result.put('modifyObjectTextDocumentTemplateName', modifyObjectTextDocumentTemplateName)

    result.put('modifyObjectTextDocumentTemplateTipe', modifyObjectTextDocumentTemplateTipe)

    result.put('modifyObjectCheckboxTtd', modifyObjectCheckboxTtd)

    result.put('modifyObjectTextTglPermintaan', modifyObjectTextTglPermintaan)

    result.put('modifyObjectLblNamaPelanggan', modifyObjectLblNamaPelanggan)

    result.put('modifyObjectTextRefNumber', modifyObjectTextRefNumber)

    result.put('modifyObjectTextStatusTtd', modifyObjectTextStatusTtd)

    result.put('modifyObjectTextProsesTtd', modifyObjectTextProsesTtd)

    return result
}

def checkBulkSigning() {
    'Klik checkbox ttd untuk semua'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/checkboxTtdSemua'))

    'Klik button ttd bulk'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TTDBulk'))

    if (checkPopupWarning() == false) {
		WebUI.focus(findTestObject('Object Repository/APIFullService/Send to Sign/button_BatalTandaTanganDokumen'))
        'klik tombol Batal'
        WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_BatalTandaTanganDokumen'))
    }
}

def inputMasukanAndWriteResultSign() {
    'Mendapat total success dan failed'
    String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

    String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

    'Menarik value count success ke excel'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Success') - 
        1, GlobalVariable.NumofColm - 1, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                'Result Count Success')) + ';') + '<') + countSuccessSign) + '>')

    'Menarik value count failed ke excel'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Failed') - 
        1, GlobalVariable.NumofColm - 1, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                'Result Count Failed')) + ';') + '<') + countFailedSign) + '>')

    'Jika masukan ratingnya tidak kosong'
    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';', -1)[GlobalVariable.indexUsed]) != 
    '') {
        'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
        modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'), 'xpath', 
            'equals', ('//ngb-rating[@id=\'rating\']/span[' + ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Rating')).split(';', -1)[GlobalVariable.indexUsed]).toInteger() * 2)) + ']/span', true)

        'Klik bintangnya bintang berapa'
        WebUI.click(modifyObjectstarMasukan)
    }
    
    'Jika komentarnya tidak kosoong'
    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')).split(';', -1)[GlobalVariable.indexUsed]) != 
    '') {
        'Input komentar di rating'
        WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('comment')).split(';', -1)[GlobalVariable.indexUsed])
    }
    
    'Scroll ke btn Kirim'
    WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Kirim'), GlobalVariable.TimeOut)

    'klik button Kirim'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))
}

def refreshWebsite() {
    'Jika running menggunakan embed, maka'
    if (GlobalVariable.RunWithEmbed == 'Yes') {
        'Ganti frame ke default'
        WebUI.switchToDefaultContent(FailureHandling.CONTINUE_ON_FAILURE)

        'click button embed untuk refresh'
        WebUI.click(findTestObject('EmbedView/button_Embed'))

        'swith to iframe'
        WebUI.switchToFrame(findTestObject('EmbedView/iFrameEsign'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
    } else {
        'Jika running tidak menggunakan embed, maka refresh saja'
        WebUI.refresh()
    }
    
    'Diberikan delay 1 sec dikarenakan agar Lastest dapat diambil'
    WebUI.delay(1)
}

def masukanStoreDB(Connection conneSign, String emailSigner, ArrayList arrayMatch) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'MasukanDB mengambil value dari hasil query'
    masukanDB = CustomKeywords.'connection.APIFullService.getFeedbackStoreDB'(conneSign, emailSigner)

    'verify rating'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(
                ';', -1)[GlobalVariable.indexUsed], masukanDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify komentar'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')).split(
                ';', -1)[GlobalVariable.indexUsed], masukanDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def signingProcessStoreDB(Connection conneSign, String emailSigner, int jumlahSignerTandaTangan, String documentId) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'SigningDB mengambil value dari hasil query'
    signingDB = CustomKeywords.'connection.SendSign.getSigningStatusProcess'(conneSign, documentId, emailSigner)

    'looping berdasarkan size dari signingDB'
    for (t = 1; t <= signingDB.size(); t++) {
        ArrayList arrayMatch = []

        if ((signingDB[arrayIndex]) == '2') {
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedProcessFailed)

            GlobalVariable.ErrorType = (signingDB[arrayIndex])

            return false
        }
        
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

def verifOTPMethod(Connection conneSign, String emailSigner, ArrayList listOTP, String noTelpSigner, String otpAfter, String vendor) {
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
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak bisa lanjut proses OTP')

        'kembali ke loop atas'
        return false
    }
    
    'check error log'
    if (checkErrorLog() == true) {
        return false
    }
    
    WebUI.delay(2)

    'Jika tidak muncul untuk element selanjutnya'
    if (WebUI.verifyElementNotPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut, FailureHandling.OPTIONAL) == 
    true) {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page input OTP')
    } else {
        if (verifOTPMethodDetail(conneSign, emailSigner, listOTP, noTelpSigner, otpAfter, vendor) == false) {
            return false
        }
    }
    
    'check error log'
    if (checkErrorLog() == true) {
        return false
    }
    
    'check pop up'
    if (checkPopup() == true) {
        return false
    }
}

def verifOTPMethodDetail(Connection conneSign, String emailSigner, ArrayList listOTP, String noTelpSigner, String otpAfter, String vendor) {
    'check ada value maka Setting OTP Active Duration'
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
    0) {
        'Setting OTP Active Duration'
        CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))

        delayExpiredOTP = (60 * Integer.parseInt(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('Setting OTP Active Duration'))))
    }
	
		'setting sent otp by email'
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')).length() >
		0) {
			'update setting sent otp by email'
			CustomKeywords.'connection.SendSign.settingSentOTPbyEmail'(conneSign, findTestData(excelPathFESignDocument).getValue(
					GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')))
		}
	
    'ubah pemakaian biom menjadi false'
    useBiom = 0
	
    if (CustomKeywords.'connection.DataVerif.getEmailServiceFromTenant'(conneSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))) == '1') {
		noTelpSigner = CustomKeywords.'connection.DataVerif.getEmailFromPhone'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
			noTelpSigner))
		}

    'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 'value'), 
            noTelpSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' kesalahan value email / no hp pada page Request OTP ')

	email = GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0])
	
    if (vendor.equalsIgnoreCase('Privy')) {
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) ==
		'1' && email.contains('OUTLOOK.COM')) {
			'call keyword get otp dari email'
			OTP = (CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(email, findTestData(excelPathFESignDocument).getValue(
					GlobalVariable.NumofColm, rowExcel('Password Signer')), 'OTP'))
		} else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) ==
		'0' || !(email.contains('OUTLOOK.COM'))) {
			'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP Privy via SMS.'
			WebUI.delay(50)

			OTP = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(';', -1)[GlobalVariable.indexUsed])
		}
	} else {
		'OTP yang pertama dimasukkan kedalam 1 var'
		OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)		
	}

    'clear arraylist sebelumnya'
    listOTP.clear()

    'add otp ke list'
    listOTP.add(OTP)


    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Correct OTP (Yes/No)')).split(';', -1)[GlobalVariable.indexUsed] == 'Yes') {
        'value OTP dari db / email'
        WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
		
		'check if ingin testing expired otp'
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() >
		0) {
			'delay untuk input expired otp'
			delayExpiredOTP = (delayExpiredOTP + 10)

			WebUI.delay(delayExpiredOTP)
		}
    } else {        
        'value OTP dari excel'
        WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('Manual OTP')).split(';', -1)[GlobalVariable.indexUsed])
    }
    
    'klik verifikasi OTP'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))

    'Kasih delay 2 detik karena proses OTP akan trigger popup, namun loading. Tidak instan'
    WebUI.delay(2)

    'check pop up'
    if (checkPopup() == true) {
        return false
    }
    
    'Resend OTP'
    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP (Yes/No)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'Ambil data dari excel mengenai countResend'
        countResend = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CountResendOTP')).split(
            ';', -1)[GlobalVariable.indexUsed]).toInteger()

        'Looping dari 1 hingga total count resend OTP'
        for (int w = 1; w <= countResend; w++) {
            'berikan waktu delay'
            WebUI.delay(298 - delayExpiredOTP)

			WebUI.focus(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))
			
            'Klik resend otp'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'), FailureHandling.CONTINUE_ON_FAILURE)

            for (loopingTimer = 1; loopingTimer <= 5; loopingTimer++) {
                'Memberikan delay 10 karena OTP after terlalu cepat'
                WebUI.delay(loopingTimer * 2)

                'OTP yang kedua'
                otpAfter = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)

                'add otp ke list'
                listOTP.add(otpAfter)

                'dicheck OTP pertama dan kedua dan seterusnya'
                if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.OPTIONAL)) {
                    if (loopingTimer == 5) {
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'OTP Before dan After sama setelah ') + 
                            (loopingTimer * 2)) + ' detik')
                    } else {
						listOTP.remove(w)
					}
                } else {
					break
				}
            }
            
            'Jika looping telah diterakhir, baru set text'
            if (w == countResend) {
                'value OTP dari db'
                WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), otpAfter, FailureHandling.CONTINUE_ON_FAILURE)

				WebUI.focus(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
                'klik verifikasi OTP'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
            }
        }
    } else {
        'tidak ada resend, namun menggunakan send otp satu kali'
        countResend = 0
    }

	'check pop up'
	if (checkPopupDoneness() == true) {
		return false
	}
	
    countResend++

    GlobalVariable.eSignData.putAt('VerifikasiOTP', countResend)
}

def verifBiomMethod(int isLocalhost, int maxFaceCompDB, int countLivenessFaceComp, Connection conneSign, String emailSigner, ArrayList listOTP, String noTelpSigner, String otpAfter, String vendor) {
    useBiom = 1

    'Klik biometric object'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifBiom'))

    'button menyetujui'
    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Menyetujui(Yes/No)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'Klik button menyetujui untuk menandatangani'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
    }
    
    'Klik lanjut after konfirmasi'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)

    'jika localhost aktif'
    if ((isLocalhost == 1) && (GlobalVariable.RunWith == 'Mobile')) {
        'tap allow camera'
        MobileBuiltInKeywords.tapAndHoldAtPosition(895, 1364, 3)
    }
    
    'looping hingga count sampai batas maksimal harian'
    while (countLivenessFaceComp != (maxFaceCompDB + 1)) {
        'klik untuk ambil foto'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesBiom'))

        'jika error muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), 60, FailureHandling.OPTIONAL)) {
            'ambil message error'
            String messageError = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'))

            if (messageError.equalsIgnoreCase('Percobaan verifikasi wajah sudah melewati batas harian')) {
                countSaldoSplitLiveFCused++

                'klik tombol OK'
                WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/button_OK'))

                'klik tombol lanjut dengan OTP'
                WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_LanjutdenganOTP'))

                'panggil fungsi verifOTP'
                if (verifOTPMethodDetail(conneSign, emailSigner, listOTP, noTelpSigner, otpAfter, vendor) == false) {
                    return false
                }
            } else if (messageError.equalsIgnoreCase('Verifikasi user gagal. Foto Diri tidak sesuai.') || messageError.equalsIgnoreCase(
                'Lebih dari satu wajah terdeteksi. Pastikan hanya satu wajah yang terlihat')) {
                countSaldoSplitLiveFCused++
            }
            
            GlobalVariable.eSignData.putAt('VerifikasiBiometric', countSaldoSplitLiveFCused)

            'ambil message error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + messageError) + '>')

            'klik pada tombol OK'
            WebUI.click(findTestObject('KotakMasuk/Sign/button_OK'))

            GlobalVariable.FlagFailed = 1

            if (messageError.equalsIgnoreCase('Image resolution too small')) {
                break
            }
            
            'ambil terbaru count dari DB'
            countLivenessFaceComp = CustomKeywords.'connection.DataVerif.getCountFaceCompDaily'(conneSign, emailSigner)
        } else {
            break
        }
    }
}

def inputDataforVerif() {
    'Scroll ke btn Proses'
    WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

    'Klik button proses'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

    'input text password'
    WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('Password Signer')))

    'klik buka * pada password'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))
}


def checkAutoStamp(Connection conneSign, String noKontrak, HashMap<String, String> resultSaldoBefore) {
	'split nomor kontrak full'
	noKontrakPerDoc = noKontrak.split(';', -1)

	'inisialisasi flag error dms'
	int flagErrorDMS = 0
	
	HashMap resultSaldoAfter = new HashMap()

	'inisialisasi saldo after dan saldo before'
	String saldoAfter, saldoBefore = resultSaldoBefore.get('Meterai')

	'looping'
	for (loopingPerKontrak = 0; loopingPerKontrak < noKontrakPerDoc.size(); loopingPerKontrak++) {
		'check autostamp'
		automaticStamp = CustomKeywords.'connection.Meterai.getAutomaticStamp'(conneSign, noKontrakPerDoc[loopingPerKontrak])
		
		'jika autostamp'
		if (automaticStamp == '1') {
			'check sign status'
			getSignStatus = CustomKeywords.'connection.SendSign.getSignStatus'(conneSign, noKontrakPerDoc[loopingPerKontrak])

			'check sign status jika complete'
			if (getSignStatus == 'Complete') {
				'mengambil value db proses ttd'
				prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[loopingPerKontrak])

				'jika bukan 0 atau berproses'
				if (prosesMaterai != 0) {
					
					'looping dari 1 hingga 12'
					for (i = 1; i <= 12; i++) {
						'mengambil value db proses ttd'
						prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[loopingPerKontrak])
	
						'jika proses materai gagal (51)'
						if (((prosesMaterai == 51) || (prosesMaterai == 61)) && (flagErrorDMS == 0)) {
							'Kasih delay untuk mendapatkan update db untuk error stamping'
							WebUI.delay(3)
	
							'get reason gailed error message untuk stamping'
							errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, noKontrakPerDoc[
								loopingPerKontrak])
	
							'Write To Excel GlobalVariable.StatusFailed and errormessage'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
									rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') +
								errorMessageDB.toString())
	
							GlobalVariable.FlagFailed = 1
	
							if (!(errorMessageDB.toString().contains('upload DMS'))) {
								break
							} else {
								flagErrorDMS = 1
	
								continue
							}
						} else if (((prosesMaterai == 53) || (prosesMaterai == 63)) || (flagErrorDMS == 1)) {
							WebUI.delay(3)
							
							'get saldo after'
							resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'),
								[('excel') : excelPathFESignDocument, ('sheet') : sheet, ('usageSaldo') : 'Stamp'],
								FailureHandling.CONTINUE_ON_FAILURE)
							
							'get saldo after meterai'
							saldoAfter = resultSaldoAfter.get('Meterai')
	
							'Mengambil value total stamping dan total meterai'
							totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
								conneSign, noKontrakPerDoc[loopingPerKontrak])
	
							'declare arraylist arraymatch'
							arrayMatch = []
	
							'dibandingkan total meterai dan total stamp'
							arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1],
									false, FailureHandling.CONTINUE_ON_FAILURE))
	
							'jika data db tidak bertambah'
							if (arrayMatch.contains(false)) {
								'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
								CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
									GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
										rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
	
								GlobalVariable.FlagFailed = 1
							} else {
								GlobalVariable.FlagFailed = 0
								
								WebUI.comment(saldoBefore)
								WebUI.comment(saldoAfter)

								'check saldo meterai'
								checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore) - Integer.parseInt(totalMateraiAndTotalStamping[1]), Integer.parseInt(saldoAfter), FailureHandling.CONTINUE_ON_FAILURE), ' pada pemotongan saldo Meterai Autostamp')
							}
							
							break
						} else {
							'Jika bukan 51/61 dan 53/63, maka diberikan delay 20 detik'
							WebUI.delay(10)
	
							'Jika looping berada di akhir, tulis error failed proses stamping'
							if (i == 12) {
								'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
								CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
									GlobalVariable.StatusFailed, ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
										rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') +
									(i * 12)) + ' detik ')
	
								GlobalVariable.FlagFailed = 1
							}
						}
					}
					
					'Jika flag failed tidak 0'
					if (GlobalVariable.FlagFailed == 0) {
						if (flagErrorDMS == 1) {
							'write to excel Failed'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
									'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
						} else {
							'write to excel success'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
									'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
						}
						
						'Mengambil value total stamping dan total meterai'
						totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(conneSign,
							noKontrakPerDoc[loopingPerKontrak])
	
						if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63)) {
							'Call verify meterai'
							WebUI.callTestCase(findTestCase('Main Flow/verifyMeterai'), [('excelPathMeterai') : excelPathFESignDocument
									, ('sheet') : sheet, ('noKontrak') : noKontrakPerDoc[loopingPerKontrak], ('linkDocumentMonitoring') : ''
									, ('CancelDocsStamp') : findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
										rowExcel('Cancel Docs after Stamp?'))], FailureHandling.CONTINUE_ON_FAILURE)
						}
					}
				} else {
					'Jika masih tidak ada'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + 'Autostamp gagal ')

					'get saldo after'
					resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'),
						[('excel') : excelPathFESignDocument, ('sheet') : sheet, ('usageSaldo') : 'Stamp'],
						FailureHandling.CONTINUE_ON_FAILURE)
					
					'check saldo before after'
					checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter),
							FailureHandling.CONTINUE_ON_FAILURE), ' pada pemotongan saldo Meterai Gagal Autostamp')
				}
			}
		}
	}
}

