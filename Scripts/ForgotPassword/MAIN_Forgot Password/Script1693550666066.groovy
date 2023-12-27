import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathForgotPass).columnNumbers

sheet = 'Forgot Password'

'looping saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'call function open browser'
        openBrowser()

        String email = findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('$EmailForPassChange'))

        String emailSHA256

        int countResend

        if (!(email.contains('@'))) {
            emailSHA256 = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(email)
        } else {
            emailSHA256 = email
        }
        
		'setting email service tenant'
		CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(excelPathForgotPass).getValue(
			GlobalVariable.NumofColm, rowExcel('Setting Email Service')),emailSHA256)
		
        String tenantcode = CustomKeywords.'connection.ForgotPassword.getTenantCode'(conneSign, emailSHA256)

        GlobalVariable.Tenant = tenantcode

        if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Empty/0/1/>1)')).length() > 
        0) {
            'ubah durasi aktif di DB'
            CustomKeywords.'connection.ForgotPassword.updateOTPActiveDuration'(conneSign, tenantcode, Integer.parseInt(findTestData(
                        excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Empty/0/1/>1)'))))
        }
        
        'angka untuk menghitung data mandatory yang tidak terpenuhi'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                rowExcel('Mandatory Complete')))

        'ambil limit untuk request reset code harian'
        int LimitResetCode = CustomKeywords.'connection.ForgotPassword.getResetCodeLimit'(conneSign)

        'klik pada link forgot password'
        WebUI.click(findTestObject('ForgotPassword/Lupa Kode Akses'))

        'input email yang akan dilakukan reset pass'
        WebUI.setText(findTestObject('ForgotPassword/inputEmail_ForgotPage'), findTestData(excelPathForgotPass).getValue(
                GlobalVariable.NumofColm, rowExcel('$EmailForPassChange')))

        'klik pada tombol batal'
        WebUI.click(findTestObject('ForgotPassword/button_Batal_ForgotPage'))

        'klik pada link forgot password'
        WebUI.click(findTestObject('ForgotPassword/Lupa Kode Akses'))

        'pastikan field email kosong setelah klik cancel'
        checkVerifyEqualorMatch(WebUI.verifyElementAttributeValue(findTestObject('ForgotPassword/inputEmail_ForgotPage'), 
                'class', 'form-control ng-untouched ng-pristine ng-invalid', GlobalVariable.TimeOut, FailureHandling.OPTIONAL), 
            'Field email tidak kosong setelah klik cancel')

        'input email yang akan dilakukan reset pass'
        WebUI.setText(findTestObject('ForgotPassword/inputEmail_ForgotPage'), findTestData(excelPathForgotPass).getValue(
                GlobalVariable.NumofColm, rowExcel('$EmailForPassChange')))

        'cek apakah tombol lanjut tidak di disable'
        if (WebUI.verifyElementNotHasAttribute(findTestObject('ForgotPassword/button_Lanjut_ForgotPage'), 'disabled', GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'klik pada tombol lanjut'
            WebUI.click(findTestObject('ForgotPassword/button_Lanjut_ForgotPage'))

            'klik pada tombol periksa lagi'
            WebUI.click(findTestObject('ForgotPassword/button_Periksa lagi'))

            'cek apakah inputan nya masih ada'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ForgotPassword/inputEmail_ForgotPage'), 
                        'value', FailureHandling.OPTIONAL), findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                        rowExcel('$EmailForPassChange')), false, FailureHandling.OPTIONAL), 'Field email tidak tersimpan setelah klik periksa lagi')

            'klik pada tombol lanjut'
            WebUI.click(findTestObject('ForgotPassword/button_Lanjut_ForgotPage'))

            'klik pada tombol ya'
            WebUI.click(findTestObject('ForgotPassword/button_YaKonfirm'))

			'check apakah muncul error'
			if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
				'ambil error dan get text dari error tersebut'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
					';') + '<') + WebUI.getText(findTestObject('ForgotPassword/errorLog'))) + '>')
 
				'klik pada tombol batal'
				WebUI.click(findTestObject('ForgotPassword/button_Batal'))
 
				continue
			} else if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
				'ambil error dan get text dari error tersebut'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
					';') + '<') + WebUI.getText(findTestObject('ForgotPassword/lbl_popup'))) + '>')
 
				WebUI.click(findTestObject('ForgotPassword/button_OK'))
				
				'klik pada tombol batal'
				WebUI.click(findTestObject('ForgotPassword/button_Batal'))
 
				continue
			}
			
			if (GlobalVariable.checkStoreDB == 'Yes' &&
				findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == '1') {
				
				WebUI.delay(1)
				
				'declare arraylist arraymatch'
				ArrayList arrayMatch = []
				
				'ambil data last transaction dari DB'
				ArrayList resultDB = CustomKeywords.'connection.ForgotPassword.getBusinessLineOfficeCode'(conneSign,
					findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('$EmailForPassChange')))
				
				'declare arrayindex'
				arrayindex = 0
				
				'lakukan loop untuk pengecekan data'
				for (int i = 0; i < (resultDB.size() / 2); i++) {
					
					'verify business line dan office code'
					arrayMatch.add(WebUI.verifyMatch(resultDB[i].toString(), resultDB[i+2].toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
				}
				
				'jika data db tidak sesuai dengan excel'
				if (arrayMatch.contains(false)) {
					GlobalVariable.FlagFailed = 1

					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
				}
			}
			
            'ambil request num'
            int requestNumresetCode = CustomKeywords.'connection.ForgotPassword.getResetNum'(conneSign, emailSHA256)

            'jika request number sesuai dengan limit'
            if (requestNumresetCode == LimitResetCode) {
                'reset request num ke 0'
                CustomKeywords.'connection.ForgotPassword.updateResetRequestNum'(conneSign, emailSHA256)
            }
        } else {
            'klik pada tombol batal'
            WebUI.click(findTestObject('ForgotPassword/button_Batal'))

            if (isMandatoryComplete != 0) {
                'tulis adanya error pada sistem web'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    GlobalVariable.ReasonFailedMandatory)
            }
            
            continue
        }
        
        'ambil reset code dari DB untuk email yang dituju'
        ArrayList resetCodefromDB = []

        'hitung jumlah resend yang diperlukan'
        countResend = Integer.parseInt(findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Count Resend')))

        'tambahkan reset code dari DB'
        resetCodefromDB.add(CustomKeywords.'connection.ForgotPassword.getResetCode'(conneSign, emailSHA256))

        'pengecekan untuk pakai code yang salah'
        if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('WrongResetCode?')) == 'No') {
            'input reset code dari DB'
            WebUI.setText(findTestObject('ForgotPassword/input_resetCode'), resetCodefromDB[0])
        } else {
            'input code yang salah dari DB'
            WebUI.setText(findTestObject('ForgotPassword/input_resetCode'), findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                    rowExcel('FalseCode')))

            'klik pada button verifikasi'
            WebUI.click(findTestObject('ForgotPassword/button_Verifikasi'))

            'check apakah muncul error'
            if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'klik pada tombol OK'
                WebUI.click(findTestObject('ForgotPassword/button_OK'))
            }
        }
      
        countResend = resendFunction(conneSign, countResend, resetCodefromDB)

		checkSaldoWAOrSMS(conneSign, emailSHA256)
		
        'panggil fungsi konfirmasi verif'
        if (verifConfirmation(conneSign) == true) {
            continue
        }

        'input password baru'
        WebUI.setText(findTestObject('ForgotPassword/input_newPass'), findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Password Baru')))

        'input password confirm'
        WebUI.setText(findTestObject('ForgotPassword/input_confirmNewPass'), findTestData(excelPathForgotPass).getValue(
                GlobalVariable.NumofColm, rowExcel('$PasswordBaruConfirm')))

        'klik button lanjut'
        WebUI.click(findTestObject('ForgotPassword/button_Simpan'))

        'klik periksa lagi'
        WebUI.click(findTestObject('ForgotPassword/button_Periksa lagi'))

        'ambil pass baru yang baru diinput di UI'
        String newPass = WebUI.getAttribute(findTestObject('ForgotPassword/input_newPass'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

        'ambil pass confirm baru yang diinput di UI'
        String newPassConfirm = WebUI.getAttribute(findTestObject('ForgotPassword/input_confirmNewPass'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

        'cek apakah inputan nya masih ada'
        checkVerifyEqualorMatch(WebUI.verifyMatch(newPass, findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$Password Baru')), false, FailureHandling.OPTIONAL), 'Field new Password tidak tersimpan setelah klik periksa lagi')

        'cek apakah inputan nya masih ada'
        checkVerifyEqualorMatch(WebUI.verifyMatch(newPassConfirm, findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$PasswordBaruConfirm')), false, FailureHandling.OPTIONAL), 'Field new Password Confirm tidak tersimpan setelah klik periksa lagi')

        'klik button lanjut'
        WebUI.click(findTestObject('ForgotPassword/button_Simpan'))

        'klik pada tombol YA'
        WebUI.click(findTestObject('ForgotPassword/button_YaKonfirm'))

        'check apakah muncul error'
        if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'ambil error dan get text dari error tersebut'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                '<') + WebUI.getText(findTestObject('ForgotPassword/errorLog'))) + '>')

            'navigate to url esign'
            WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

            continue
        } else if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            if (WebUI.getText(findTestObject('ForgotPassword/lbl_popup'), FailureHandling.OPTIONAL) != 'Your access code has been successfully changed') {
                'ambil error dan get text dari error tersebut'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + WebUI.getText(findTestObject('ForgotPassword/lbl_popup'))) + '>')

                'klik pada tombol OK'
                WebUI.click(findTestObject('ForgotPassword/button_OK'))

                'navigate to url esign'
                WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

                continue
            }
        }
        
        'klik pada tombol OK'
        WebUI.click(findTestObject('ForgotPassword/button_OK'))
		
        'input email untuk login'
        WebUI.setText(findTestObject('ForgotPassword/input_email'), findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                rowExcel('$EmailForPassChange')))

        'input password baru yang direset'
        WebUI.setText(findTestObject('ForgotPassword/input_pass'), findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Password Baru')))

        'fokus ke button login'
        WebUI.focus(findTestObject('ForgotPassword/button_Masuk'), FailureHandling.OPTIONAL)

        'klik pada button login'
        WebUI.click(findTestObject('ForgotPassword/button_Masuk'))

        if (WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'input perusahaan'
            WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData('Login/Login').getValue(4, 2))

            'enter untuk input perusahaan'
            WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

            'input peran'
            WebUI.setText(findTestObject('Login/input_Peran'), 'Customer')

            'enter untuk input peran'
            WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

            'click button pilih peran'
            WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
        }
        
        'cek apakah berhasil login'
        if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/span_Profile'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'klik pada tombol profile'
            WebUI.click(findTestObject('ForgotPassword/span_Profile'))

            'klik pada tombol logout'
            WebUI.click(findTestObject('ForgotPassword/button_Logout'))

            if ((GlobalVariable.FlagFailed == 0) && (isMandatoryComplete == 0)) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                'hilangkan failed reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 1, GlobalVariable.NumofColm - 
                    1, '')
            }
        } else {
            'tulis adanya error pada sistem web'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                'Gagal Login')
        }
    }
}

WebUI.closeBrowser()

def verifConfirmation(Connection conneSign) {
    boolean shouldContinue = false

    'jika perlu tunggu hingga code expired'
    if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Empty/0/1/>1)')).length() > 
    0) {
        'ambil durasi aktif OTP dari update excel'
        int OTPActiveDuration = Integer.parseInt(findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Setting OTP Active Duration (Empty/0/1/>1)')))

        'kondisi jika OTP tidak 0 dan null'
        if ((OTPActiveDuration != 0) && (OTPActiveDuration != null)) {
            'hitung durasi aktif otp'
            int OTPDuration = (OTPActiveDuration * 60) + 10

            'buat delay hingga melebihi waktu OTP Active Duration'
            WebUI.delay(OTPDuration)
        } else {
            'ambil error dan get text dari error tersebut'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                'OTP Active Duration belum di set oleh tenant')
        }
    }
    
    'klik pada button verifikasi'
    WebUI.click(findTestObject('ForgotPassword/button_Verifikasi'))

    'check apakah muncul error'
    if (WebUI.verifyElementPresent(findTestObject('ForgotPassword/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil error dan get text dari error tersebut'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-','') + ';') + 
            '<') + WebUI.getText(findTestObject('ForgotPassword/lbl_popup'))) + '>')

        'klik pada tombol OK'
        WebUI.click(findTestObject('ForgotPassword/button_OK'))

        'klik pada tombol silang verifikasi'
        WebUI.click(findTestObject('ForgotPassword/tombolXVerif'))

        'klik pada tombol batal'
        WebUI.click(findTestObject('ForgotPassword/button_Batal'))

        shouldContinue = true
    }
    
    return shouldContinue
}

def resendFunction(Connection conndev, int countResend, ArrayList resetCodefromDB) {
    'cek apakah perlu resend reset code'
    if (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Resend Reset Code?')) == 'Yes') {
        'ulangi sesuai flag dari excel'
        for (int i = 1; i <= countResend; i++) {
            WebUI.delay(297)

            'klik pada resend code'
            WebUI.click(findTestObject('ForgotPassword/ResendCode'))

            'tunggu agar data di DB terupdate'
            WebUI.delay(5)

            'ambil kembali code dari DB'
            resetCodefromDB.add(CustomKeywords.'connection.ForgotPassword.getResetCode'(conndev, findTestData(excelPathForgotPass).getValue(
                        GlobalVariable.NumofColm, rowExcel('$EmailForPassChange'))))

            'cek apakah resend gagal'
            if ((resetCodefromDB[(i - 1)]) == (resetCodefromDB[i])) {
                GlobalVariable.FlagFailed = 1

                'tulis adanya error pada resend'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    'Reset code tetap sama sesudah resend')
            }
            
            'input reset code dari DB'
            WebUI.setText(findTestObject('ForgotPassword/input_resetCode'), resetCodefromDB[i])

            countResend++
        }
    } else {
        countResend++
    }
    
    return countResend
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def openBrowser() {
    'open browser'
    WebUI.openBrowser('')

    'navigate to url esign'
    WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

    'maximized window'
    WebUI.maximizeWindow()
}

def checkSaldoWAOrSMS(Connection conneSign, String emailSigner) {
    emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, emailSigner)

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner)

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

    if (mustUseWAFirst == '1') {
        'menggunakan saldo wa'
        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', fullNameUser)
		
		if (balmut.size() == 0) {
			'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-',
					'') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp'))
		}
    } else {
		if (emailServiceOnVendor == '1') {
            useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

            if (useWAMessage == '1') {
                'menggunakan saldo wa'
                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                    fullNameUser)
			
				if (balmut.size() == 0) {
					'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
						((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-',
							'') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp'))
				}
            } else if (useWAMessage == '0') {
                'ke sms / wa'
                SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Forgot Password')

                if (SMSSetting == '1') {
                    'ke sms'
                    'menggunakan saldo wa'
                    ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'SMS Notif', 
                        fullNameUser)
					
					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
							((findTestData(excelPathForgotPass).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-',
								'') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS'))
					}
                }
            }
        }
    }
}