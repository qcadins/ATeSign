import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main Register.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathRegister).columnNumbers

'declare variable array'
HashMap<String, String> saldoBefore = new HashMap<String, String>()

HashMap<String, String> saldoAfter = new HashMap<String, String>()

'looping buat undangan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
		GlobalVariable.VerificationCount = 1
		
        int countCheckSaldo = 0

        'get psre per case'
        GlobalVariable.Psre = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if tidak mau menggunakan tenant code yang benar'
        if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') || findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
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

        'check ada value maka setting email service tenant'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
        0) {
            'setting email service tenant'
            CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Service')))
        }
        
        'check ada value maka setting email certif notif'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')).length() > 
        0) {
            'setting email email certif notif'
            CustomKeywords.'connection.Registrasi.settingSendCertNotifbySMS'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')))
        }
        
        'check ada value maka setting allow regenerate link'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')).length() > 
        0) {
            'setting allow regenerate link'
            CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))
        }
        
        'check ada value maka setting must use wa first'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')).length() > 
        0) {
            'setting must use wa first'
            CustomKeywords.'connection.APIFullService.settingMustUseWAFirst'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')))
        }
        
        'check ada value maka setting use wa message'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')).length() > 
        0) {
            'setting use wa message'
            CustomKeywords.'connection.APIFullService.settingUseWAMessage'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'get saldo before'
        saldoBefore = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathRegister, ('sheet') : sheet
                , ('vendor') : GlobalVariable.Psre, ('usageSaldo') : 'Register', ('countCheckSaldo') : countCheckSaldo], 
            FailureHandling.CONTINUE_ON_FAILURE)

        countCheckSaldo = 1

        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
            'Menu Buat Undangan')) {
            'call test case buat undangan'
            WebUI.callTestCase(findTestCase('Main Register/BuatUndangan'), [('excelPathRegister') : excelPathRegister], 
                FailureHandling.CONTINUE_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
            'API Generate Inv Link Normal')) {
            'call test case api generate invitation link Normal'
            WebUI.callTestCase(findTestCase('Main Register/API Gen Inv Link Normal'), [('excelPathRegister') : excelPathRegister
                    , ('sheet') : 'Main Register'], FailureHandling.STOP_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
            'API Generate Inv Link External')) {
            'call test case api generate invitation link external'
            WebUI.callTestCase(findTestCase('Main Register/API Gen Inv Link External'), [('excelPathRegister') : excelPathRegister
                    , ('sheet') : 'Main Register'], FailureHandling.STOP_ON_FAILURE)
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
            'API Register')) {
            'call test case api register external'
            WebUI.callTestCase(findTestCase('Main Register/API Registrasi'), [('excelPathRegister') : excelPathRegister, ('sheet') : 'Main Register'], 
                FailureHandling.STOP_ON_FAILURE)
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
                'API Register'))) {
                'check ada value maka setting Link Is Active'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')).length() > 
                0) {
                    'check if email kosong atau tidak'
                    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 
                    2) {
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
            
	            'call test case daftar akun verif'
	            WebUI.callTestCase(findTestCase('Main Register/DaftarAkunDataVerif'), [('excelPathRegister') : excelPathRegister
	                    , ('otpBefore') : saldoBefore.get('OTP')], FailureHandling.CONTINUE_ON_FAILURE)
	
	            'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form registrasi'
	            while (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase(
	                'Continue')) {
	                (GlobalVariable.NumofColm)++
	
	                GlobalVariable.FlagFailed = 0
	
	                'call test case daftar akun verif'
	                WebUI.callTestCase(findTestCase('Main Register/DaftarAkunDataVerif'), [('excelPathRegister') : excelPathRegister
	                        , ('otpBefore') : saldoBefore.get('OTP')], FailureHandling.CONTINUE_ON_FAILURE)
	            }
            }
            
            if (GlobalVariable.FlagFailed == 0) {
                'kurang saldo before dengan proses verifikasi'
                saldoBefore.put('Verifikasi', (Integer.parseInt(saldoBefore.get('Verifikasi')) - GlobalVariable.VerificationCount).toString())

                if (GlobalVariable.Psre == 'VIDA') {
                    'kurang saldo before dengan proses PNBP'
                    saldoBefore.put('PNBP', (Integer.parseInt(saldoBefore.get('PNBP')) - 1).toString())

                    if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
                        'API Register'))) {
                        'kurang saldo before dengan jumlah counter send OTP'
                        saldoBefore.put('OTP', (Integer.parseInt(saldoBefore.get('OTP')) - GlobalVariable.Counter).toString())
                    }
                } else {
                    if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Register With')).equalsIgnoreCase(
                        'API Register'))) {
                        'kurang saldo before dengan jumlah counter send OTP'
                        saldoBefore.put('OTP', (Integer.parseInt(saldoBefore.get('OTP')) - GlobalVariable.Counter).toString())
                    }
                }
                
                'get saldo after'
                saldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathRegister, ('sheet') : sheet
                        , ('vendor') : GlobalVariable.Psre, ('usageSaldo') : 'Register', ('countCheckSaldo') : countCheckSaldo], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                println(saldoAfter)

				WebUI.comment(saldoBefore.toString())
				WebUI.comment(saldoAfter.toString())
				
                'verify saldo before dan after'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(saldoBefore.toString(), saldoAfter.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' Saldo Gagal Potong')

                if ((((((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == 
                '0') || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == 
                'null')) && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                '1')) || (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                '0')) && findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).toUpperCase().contains(
                    'OUTLOOK.COM')) && (GlobalVariable.Psre == 'VIDA')) {
                    'call keyword get email'
                    String emailCert = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Password')).replace('"', ''), 'Certif')

                    'verify email cert'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(emailCert, 'Penerbitan Sertifikat Elektronik', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' email cert tidak terkirim')
                }
                
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).length() > 
                0) {
					'call test case pencarian pengguna'
                    WebUI.callTestCase(findTestCase('Main Register/PencarianPenggunaPelanggan'), [('excelPathRegister') : 'Registrasi/MainRegister'
                            , ('sheet') : 'Main Register'], FailureHandling.STOP_ON_FAILURE)
                }
            }
        }
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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
