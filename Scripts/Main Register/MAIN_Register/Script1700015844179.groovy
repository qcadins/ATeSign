import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main Register.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathRegister).columnNumbers

'declare variable array'
HashMap<String, String> saldoBefore = [:], saldoAfter = [:]

'looping buat undangan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        GlobalVariable.VerificationCount = 1

        GlobalVariable.Counter = 0

        int countCheckSaldo = 0

        'get psre per case'
        GlobalVariable.Psre = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if tidak mau menggunakan tenant code yang benar'
        if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') || findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'Menu Buat Undangan')) {
            GlobalVariable.Tenant = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        }
        
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathRegister, GlobalVariable.NumofColm, rowExcel(
                'Use Correct base Url'))

        'setting update db Main Register'
        CustomKeywords.'connection.UpdateData.updateDBMainRegister'(conneSign, excelPathRegister, sheet)

        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Run API Only - Gen Link')).equalsIgnoreCase(
            'No') || findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'Menu Buat Undangan')) {
            'get saldo before'
            saldoBefore = WebUI.callTestCase(findTestCase('Main Flow - Copy/getSaldo'), [('excel') : excelPathRegister, ('sheet') : sheet
                    , ('vendor') : GlobalVariable.Psre, ('usageSaldo') : 'Register', ('countCheckSaldo') : countCheckSaldo], 
                FailureHandling.CONTINUE_ON_FAILURE)

            countCheckSaldo = 1
        }
        
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'Menu Buat Undangan')) {
            'call test case buat undangan'
            WebUI.callTestCase(findTestCase('Main Register/BuatUndangan'), [('excelPathRegister') : excelPathRegister], 
                FailureHandling.CONTINUE_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'API Generate Inv Link Normal')) {
            'call test case api generate invitation link Normal'
            WebUI.callTestCase(findTestCase('Main Register/API Gen Inv Link Normal'), [('excelPathRegister') : excelPathRegister
                    , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'API Generate Inv Link External')) {
            'call test case api generate invitation link external'
            WebUI.callTestCase(findTestCase('Main Register/API Gen Inv Link External'), [('excelPathRegister') : excelPathRegister
                    , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'API Register')) {
            'call test case api register external'
            WebUI.callTestCase(findTestCase('Main Register/API Registrasi'), [('excelPathRegister') : excelPathRegister, ('sheet') : 'Main Register'], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        if (((GlobalVariable.FlagFailed == 0) && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                'Run API Only - Gen Link')).equalsIgnoreCase('No') || findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Generate Link With')).equalsIgnoreCase('Menu Buat Undangan'))) && !(findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase('API Register'))) {
            'check ada value maka setting Link Is Active'
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')).length() > 
            0) {
                'check if email kosong atau tidak'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2) {
                    'setting Link Is Active'
                    CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Setting is_active Link')), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', ''))
                } else {
                    'setting Link Is Active'
                    CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Setting is_active Link')), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))
                }
            }
            
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).length() > 
            0) {
                'call test case inquiry invitation'
                WebUI.callTestCase(findTestCase('Main Register/InquiryInvitation'), [('excelPathRegister') : excelPathRegister], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            } else {
                'call test case verif Submit Data'
                WebUI.callTestCase(findTestCase('Main Register/verifSubmitData'), [('excelPathRegister') : excelPathRegister], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
                'API Register by Invitation')) {
                'call test case api register by invitation'
                WebUI.callTestCase(findTestCase('Main Register/API Register By Invitation'), [('excelPathRegister') : excelPathRegister
                        , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)

                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Run API Only - Register')).equalsIgnoreCase(
                    'Yes')) {
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                    
                    continue
                }
            } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
                'Front End Register')) {
                'call test case daftar akun verif'
                WebUI.callTestCase(findTestCase('Main Register/DaftarAkunDataVerif'), [('excelPathRegister') : excelPathRegister
                        , ('otpBefore') : saldoBefore.get('OTP')], FailureHandling.CONTINUE_ON_FAILURE)

                'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form registrasi'
                while (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase(
                    'Continue') && (GlobalVariable.FlagFailed > 0)) {
                    (GlobalVariable.NumofColm)++

                    GlobalVariable.FlagFailed = 0

                    'call test case daftar akun verif'
                    WebUI.callTestCase(findTestCase('Main Register/DaftarAkunDataVerif'), [('excelPathRegister') : excelPathRegister
                            , ('otpBefore') : saldoBefore.get('OTP')], FailureHandling.CONTINUE_ON_FAILURE)
                }
            }
            
            if (GlobalVariable.FlagFailed == 0) {
                println(GlobalVariable.VerificationCount)

                println(saldoBefore.get('Verifikasi'))

                'get saldo after'
                saldoAfter = WebUI.callTestCase(findTestCase('Main Flow - Copy/getSaldo'), [('excel') : excelPathRegister
                        , ('sheet') : sheet, ('vendor') : GlobalVariable.Psre, ('usageSaldo') : 'Register', ('countCheckSaldo') : countCheckSaldo], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                println(GlobalVariable.VerificationCount)

                println(saldoBefore.get('Verifikasi'))

                'kurang saldo before dengan proses verifikasi'
                saldoBefore.put('Verifikasi', (Integer.parseInt(saldoBefore.get('Verifikasi')) - GlobalVariable.VerificationCount).toString())

                'call fucntion input filter saldo'
                inputFilterSaldo('Verification', conneSign, GlobalVariable.Psre)

                if (GlobalVariable.Psre == 'VIDA') {
                    'kurang saldo before dengan proses PNBP'
                    saldoBefore.put('PNBP', (Integer.parseInt(saldoBefore.get('PNBP')) - 1).toString())

                    'call fucntion input filter saldo'
                    inputFilterSaldo('PNBP', conneSign, GlobalVariable.Psre)
                }
                
                if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
                    'API Register'))) {
                    'get tenant di table ms notif type of tenant'
                    tenantType = CustomKeywords.'connection.UpdateData.checkNotifTypeExistforTenant'(conneSign)

                    if (tenantType == 0) {
                        'call function potong saldo WA or OTP untuk transaksi otp'
                        potongSaldoWAorOTP('', saldoBefore, conneSign)

                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
                            'API Generate Inv Link Normal')) {
                            potongSaldoSendGenInvLink('', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
                            'Menu Buat Undangan')) {
                            potongSaldoSendGenInvLink('', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                            'Regenerate invitation link')) {
                            potongSaldoSendGenInvLink('', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                            'Kirim Ulang Undangan')) {
                            potongSaldoSendGenInvLink('', saldoBefore, conneSign)
                        }
                    } else {
                        'call function potong saldo WA or OTP untuk transaksi otp'
                        potongSaldoWAorOTP(' - OTP Act', saldoBefore, conneSign)

                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
                            'API Generate Inv Link Normal')) {
                            potongSaldoSendGenInvLink(' - Gen Inv', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
                            'Menu Buat Undangan')) {
                            potongSaldoSendGenInvLink(' - Gen Inv Menu', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                            'Regenerate invitation link')) {
                            potongSaldoSendGenInvLink(' - Regen Link', saldoBefore, conneSign)
                        }
                        
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                            'Kirim Ulang Undangan')) {
                            potongSaldoSendGenInvLink(' - Resend Inv', saldoBefore, conneSign)
                        }
                    }
                    
                    if (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) == 
                    '1') && (GlobalVariable.Psre == 'VIDA')) && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                        rowExcel('$Email')).length() <= 2)) {
                        potongSaldoSendCertNotif(saldoBefore, conneSign)
                    }
                    
                    WebUI.comment(saldoBefore.toString())

                    WebUI.comment(saldoAfter.toString())

                    'verify saldo before dan after'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(saldoBefore.toString(), saldoAfter.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' Saldo Gagal Potong')

                    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).length() > 
                    0) {
                        'call test case pencarian pengguna'
                        WebUI.callTestCase(findTestCase('Main Register/PencarianPenggunaPelanggan'), [('excelPathRegister') : 'Registrasi/MainRegister'
                                , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
                    }
                }
                
                'check if email kosong atau tidak'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                    'Edit') && findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase(
                    'Email')) {
                    'get email dari row edit'
                    email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).replace(
                        '"', '')
                } else if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 
                2) && !(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
                    'Edit'))) {
                    'get email dari row input'
                    email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace(
                        '"', '')
                } else {
                    'get name + email hosting'
                    email = (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace(
                        '"', '') + CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign))
                }
                
                if (((((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) == 
                '0') || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) == 
                'null')) && ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                '1') || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                '0'))) && email.toUpperCase().contains('OUTLOOK.COM')) && (GlobalVariable.Psre == 'VIDA')) {
                    'call keyword get email'
                    String emailCert = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', ''), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Password - Aktivasi')).replace('"', ''), 'Certif')

                    'verify email cert'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(emailCert, 'Penerbitan Sertifikat Elektronik', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' email cert tidak terkirim')
                }
                
                'call test case Store DB'
                WebUI.callTestCase(findTestCase('Main Register/NewUserStoreDB'), [('excelPathRegister') : 'Registrasi/MainRegister'
                        , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def potongSaldoWAorOTP(String type, HashMap<String, String> saldoBefore, Connection conneSign) {
    if ((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'TKNAJ')) {
        'check jika Must use WA message = 1'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First' + type)) == 
        '1') {
            usedSaldo = 'WhatsApp Message'
        } else {
            'check jika email service on'
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
            '1') {
                'check jika use WA message = 1'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message' + 
                        type)) == '1') {
                    usedSaldo = 'WhatsApp Message'
                } else {
                    'jika use WA message bukan 1 maka use OTP'
                    usedSaldo = 'OTP'
                }
            } else {
                'jika use WA message bukan 1 maka use OTP'
                usedSaldo = 'OTP'
            }
        }
        
        'kurang saldo before dengan jumlah counter send OTP'
        saldoBefore.put(usedSaldo, (Integer.parseInt(saldoBefore.get(usedSaldo)) - GlobalVariable.Counter).toString())
    } else {
        usedSaldo = 'OTP'

        'kurang saldo before dengan jumlah counter send OTP'
        saldoBefore.put(usedSaldo, (Integer.parseInt(saldoBefore.get('OTP')) - GlobalVariable.Counter).toString())
    }
    
    'call fucntion input filter saldo'
    inputFilterSaldo(usedSaldo, conneSign, 'ESIGN/ADINS')
}

def potongSaldoSendGenInvLink(String type, HashMap<String, String> saldoBefore, Connection conneSign) {
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <= 2) {
        'check jika Must use WA message = 1'
        if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First' + type)) == 
        '1') || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message' + 
                type)) == '1')) {
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA GenInv')) == 
            '1') {
                usedSaldo = 'WhatsApp Message'
            } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA GenInv')) == 
            '0') {
				return
            }
        } else {
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) == 
            '1') {
                'jika use WA message bukan 1 maka use SMS Notif'
                usedSaldo = 'SMS Notif'
            } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) == 
            '0') {
				return
            }
        }
        
        'kurang saldo before dengan proses send link undangan melalui sms / WA'
        saldoBefore.put(usedSaldo, (Integer.parseInt(saldoBefore.get(usedSaldo)) - 1).toString())

        'call fucntion input filter saldo'
        inputFilterSaldo(usedSaldo.replace(' Notif', ''), conneSign, 'ESIGN/ADINS')
    }
}

def potongSaldoSendCertNotif(HashMap<String, String> saldoBefore, Connection conneSign) {
    'jika use WA message bukan 1 maka use SMS Notif'
    usedSaldo = 'SMS Notif'

    'kurang saldo before dengan proses send link undangan melalui sms / WA'
    saldoBefore.put('SMS Notif', (Integer.parseInt(saldoBefore.get(usedSaldo)) - 1).toString())

    'call fucntion input filter saldo'
    inputFilterSaldo('SMS', conneSign, 'ESIGN/ADINS')
}

def inputFilterSaldo(String tipeSaldo, Connection conneSign, String vendorVerifikasi) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'klik button saldo'
    WebUI.click(findTestObject('Saldo/menu_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), '(?i)' + vendorVerifikasi, true)

    'input tipe saldo'
    WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), tipeSaldo)

    'enter untuk input tipe saldo'
    WebUI.sendKeys(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

    'input tanggal Transaksi'
    WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TanggalTransaksi'), currentDate)

    'click button cari'
    WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_Cari'))

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object button last page'
    modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variable.size()) + ']', true)

    if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
        'click button last page'
        WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_LastPage'))
    }
    
    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'modify object no transaksi'
    modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

    'modify object tanggal transaksi'
    modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

    'modify object office'
    modifyObjectOffice = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

    'modify object tipe transaksi'
    modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

    'modify object user'
    modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

    'modify object no kontrak'
    modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div', true)

    'modify object Catatan'
    modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

    'modify object qty'
    modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div', true)

    'get trx dari db'
    ArrayList result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, 'Use ' + tipeSaldo)

    arrayIndex = 0

    'verify no trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' No Trx ' + tipeSaldo)

    'verify tgl trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), result[arrayIndex++], false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx ' + tipeSaldo)

    'verify office ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectOffice), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Office ' + tipeSaldo)

    'verify tipe trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Tipe Trx ' + tipeSaldo)

    'verify user trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' User ' + tipeSaldo)

    'verify ref no trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoKontrak), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Ref No ' + tipeSaldo)

    Note = WebUI.getText(modifyObjectCatatan)

    'verify note trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(Note, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes ' + 
        tipeSaldo)

    if ((tipeSaldo == 'SMS') && Note.toLowerCase().contains('error')) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + '; <') + Note) + 
            '>')

        GlobalVariable.FlagFailed = 1
    }
    
    'verify qty trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).toString().replace(
                '-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx ' + tipeSaldo)

    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
        'API Generate Inv Link Normal')) {
        'verify office code trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                        'OfficeCode')), (result[arrayIndex++]).toString().replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' office code Trx ' + tipeSaldo)

        'verify office name trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                        'OfficeName')), (result[arrayIndex++]).toString().replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' office name Trx ' + tipeSaldo)

        'verify business line code trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                        'BusinessLineCode')), (result[arrayIndex++]).toString().replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' business line code Trx ' + tipeSaldo)

        'verify business line name trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                        'BusinessLineName')), (result[arrayIndex++]).toString().replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' business line name Trx ' + tipeSaldo)
    }
}

