import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main Register.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathRegister).columnNumbers

String lovCode = '', emailPhoneNo = ''

'declare variable array'
HashMap<String, String> saldoBefore = [:]

HashMap<String, String> saldoAfter = [:]

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
		
		'jika email kosng akan ambil phone num, tapi jika terisi akan ambil email'
		if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() == 0) {
			emailPhoneNo = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon'))
		} else {
			emailPhoneNo = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email'))
		}
		
		if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')) == 'API Generate Inv Link Normal' ||
			findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')) == 'API Generate Inv Link External') {
			'lovCode menjadi GEN_INV'
			lovCode = 'GEN_INV'
		} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')) == 'Menu Buat Undangan') {
			'lovCode menjadi GEN_INV menu'
			lovCode = 'GEN_INV_MENU'
		}
		
		'ambil result get inv link'
		ArrayList checkNotifTypeAvailable = CustomKeywords.'connection.APIFullService.checkNotifTypeExistforTenant'(conneSign, lovCode)
		
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
        
        'check ada value maka setting SMS certif notif'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')).length() > 
        0) {
            'setting email SMS certif notif'
            CustomKeywords.'connection.Registrasi.settingSendCertNotifbySMS'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')))
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
			if (checkNotifTypeAvailable.empty) {
				'setting must use wa first'
				CustomKeywords.'connection.APIFullService.settingMustUseWAFirst'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')))
			}
			else {
				'setting must use wa by notiftype tenant'
				CustomKeywords.'connection.UpdateData.updateMustWALevelNotifGenInvLink'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')))
			}
        }
        
        'check ada value maka setting use wa message'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')).length() > 
        0) {
			if (checkNotifTypeAvailable.isEmpty()) {
				'setting use wa message'
				CustomKeywords.'connection.APIFullService.settingUseWAMessage'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')))
			}
			else {
				'setting must use wa by notiftype tenant'
				CustomKeywords.'connection.UpdateData.updateUseWAMsgLevelNotifGenInvLink'(conneSign, findTestData(excelPathRegister).getValue(
					GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')))
			}
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
        
        'check ada value maka setting send sms gen inv link'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')).length() > 
        0) {
            'setting send sms gen inv link'
            CustomKeywords.'connection.APIFullService.settingSendSMSGenInv'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')))
        }
		
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Run API Only - Gen Link')).equalsIgnoreCase(
            'No')) {
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
                'kurang saldo before dengan proses verifikasi'
                saldoBefore.put('Verifikasi', (Integer.parseInt(saldoBefore.get('Verifikasi')) - GlobalVariable.VerificationCount).toString())

                if (checkNotifTypeAvailable.isEmpty()) {
					if ((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'TKNAJ')) {
						if (GlobalVariable.Psre == 'VIDA') {
							'kurang saldo before dengan proses PNBP'
							saldoBefore.put('PNBP', (Integer.parseInt(saldoBefore.get('PNBP')) - 1).toString())
						}
						
						if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
							'API Register'))) {
							'check jika Must use WA message = 1'
							if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')) ==
							'1') {
								usedSaldo = 'WhatsApp Message'
							} else {
								'check jika email service on'
								if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) ==
									'1' && findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')) == '') {
									'check jika use WA message = 1'
									if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')) ==
									'1') {
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
						}
						
						if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) ==
						'1') {
							if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <=
							2) {
								'ambil hasil return untuk pemakaian saldo menggunakan notiftype'
								ArrayList getResultNotifType = CustomKeywords.'connection.APIFullService.getResultNotifTypeGenInvLink'(conneSign, emailPhoneNo, lovCode)
		
								usedSaldo = getResultNotifType[0]

								if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
									'Regenerate invitation link') && !(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
									rowExcel('Generate Link With')).equalsIgnoreCase('API Generate Inv Link Normal'))) {
									'kurang saldo before dengan proses send link undangan melalui sms 1x regenarate link'
									saldoBefore.put(usedSaldo, (Integer.parseInt(saldoBefore.get(usedSaldo)) - 1).toString())
								} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
									'API Generate Inv Link Normal') && findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
									rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Regenerate invitation link')) {
									'kurang saldo before dengan proses send link undangan melalui sms 1x hit api 1x regenrate link'
									saldoBefore.put(usedSaldo, (Integer.parseInt(saldoBefore.get(usedSaldo)) - 2).toString())
								}
							}
						}
						
						if (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')) ==
						'1') && (GlobalVariable.Psre == 'VIDA')) && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
							rowExcel('$Email')).length() <= 2)) {
							'kurang saldo before dengan proses send certif melalui sms'
							saldoBefore.put('SMS Notif', (Integer.parseInt(saldoBefore.get('SMS Notif')) - 1).toString())
						}
					} else {
						if (!(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
							'API Register'))) {
							'kurang saldo before dengan jumlah counter send OTP'
							saldoBefore.put('OTP', (Integer.parseInt(saldoBefore.get('OTP')) - GlobalVariable.Counter).toString())
						}
					}
				}
                
                'get saldo after'
                saldoAfter = WebUI.callTestCase(findTestCase('Main Flow - Copy/getSaldo'), [('excel') : excelPathRegister
                        , ('sheet') : sheet, ('vendor') : GlobalVariable.Psre, ('usageSaldo') : 'Register', ('countCheckSaldo') : countCheckSaldo], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                println(saldoAfter)

                WebUI.comment(saldoBefore.toString())

                WebUI.comment(saldoAfter.toString())

                'verify saldo before dan after'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(saldoBefore.toString(), saldoAfter.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' Saldo Gagal Potong')

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
                
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Check Pencarian Pengguna')).length() > 
                0) {
                    'call test case pencarian pengguna'
                    WebUI.callTestCase(findTestCase('Main Register/PencarianPenggunaPelanggan'), [('excelPathRegister') : 'Registrasi/MainRegister'
                            , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
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

