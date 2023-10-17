import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.VerificationCount = 1

'get psre per case'
GlobalVariable.Psre = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

'get tenant per case'
GlobalVariable.Tenant = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

'declare variable array'
ArrayList<String> saldoBefore, saldoAfter

int countCheckSaldo = 0

GlobalVariable.FlagFailed = 0

'check ada value maka setting email service tenant'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')).length() > 
0) {
    'setting email service tenant'
    CustomKeywords.'connection.Registrasi.settingEmailServiceTenant'(conneSign, findTestData(excelPathBuatUndangan).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting Email Services')))
}

'check ada value maka setting email certif notif'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')).length() >
0) {
	'setting email email certif notif'
	CustomKeywords.'connection.Registrasi.settingSendCertNotifbySMS'(conneSign, findTestData(excelPathBuatUndangan).getValue(
			GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')))
}

'check ada value maka setting allow regenerate link'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')).length() > 
0) {
    'setting allow regenerate link'
    CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathBuatUndangan).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))
}

'call function input > cancel > verify form kosong'
inputCancel()

'input form buat undangan'
inputBuatUndangan()

'click button save'
WebUI.click(findTestObject('RegisterEsign/button_Save'))

if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'click button ya proses'
    WebUI.click(findTestObject('RegisterEsign/button_YaProses'))
}

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel(
            'Is Mandatory Complete')))

'cek if muncul popup'
if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('RegisterEsign/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + '<') + ReasonFailed) + '>')

    'click button tutup error'
    WebUI.click(findTestObject('RegisterEsign/button_TutupError'))

    'click button cancel'
    WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

    'click button ya batal undangan'
    WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))

    GlobalVariable.FlagFailed = 1 
	
	GlobalVariable.LoginAgain = 1
} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'call function get error log'
    getErrorLog()
	
	GlobalVariable.LoginAgain = 1
} else if (WebUI.getAttribute(findTestObject('RegisterEsign/PopUp/input_Link'), 'value', FailureHandling.OPTIONAL) == 'undefined') {
    GlobalVariable.ErrorType = 'ERROR'

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + GlobalVariable.ErrorType)

    'click tutup popup'
    WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))

    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Register eSign/ErrorReport'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
	
	GlobalVariable.LoginAgain = 1
} else if (isMandatoryComplete > 0) {
    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + GlobalVariable.ReasonFailedMandatory)
	
	GlobalVariable.LoginAgain = 1
} else {
    'get link'
    GlobalVariable.Link = WebUI.getAttribute(findTestObject('RegisterEsign/PopUp/input_Link'), 'value')

    'write to excel Link buat undangan'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, SheetName, rowExcel('Link Invitation') - 
        1, GlobalVariable.NumofColm - 1, GlobalVariable.Link)

    'HIT API Login untuk token : invenditor@womf'
    responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathBuatUndangan).getValue(
                    GlobalVariable.NumofColm, rowExcel('Inveditor Login')), ('password') : findTestData(excelPathBuatUndangan).getValue(
                    GlobalVariable.NumofColm, rowExcel('Inveditor Password Login'))]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

        'HIT API get Invitation Link'
        responGetInvLink = WS.sendRequest(findTestObject('Postman/Get Inv Link', [('callerId') : '""', ('receiverDetail') : ('"' + 
                    findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email'))) + '"', ('tenantCode') : ('"' + 
                    GlobalVariable.Tenant) + '"', ('vendorCode') : ('"' + GlobalVariable.Psre) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responGetInvLink, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responGetInvLink, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'Get invitation Link'
                InvitationLink = WS.getElementPropertyValue(responGetInvLink, 'invitationLink')

                if (!WebUI.verifyMatch(GlobalVariable.Link, InvitationLink, false, FailureHandling.CONTINUE_ON_FAILURE)) {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + ' FE Buat Undangan Gen Link dan API Get Inv Link')
					
					GlobalVariable.FlagFailed = 1
                }
            } else {
                messageFailed = WS.getElementPropertyValue(responGetInvLink, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + messageFailed) + '>')

				GlobalVariable.FlagFailed = 1
            }
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
			
			GlobalVariable.FlagFailed = 1
        }
    } else {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedHitAPI)
		
		GlobalVariable.FlagFailed = 1
    }
    
    'click tutup popup'
    WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))

	if(GlobalVariable.FlagFailed == 0) {
		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, SheetName, 0, GlobalVariable.NumofColm -
			1, GlobalVariable.StatusSuccess)
	}
	
    'check ada value maka setting Link Is Active'
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')).length() > 
    0) {
    	'setting Link Is Active'
    	CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathBuatUndangan).getValue(
    			GlobalVariable.NumofColm, rowExcel('Setting is_active Link')), findTestData(excelPathBuatUndangan).getValue(
    					GlobalVariable.NumofColm, rowExcel('Email')))
    	
    	inputBuatUndangan()
    	
    	'click button save'
    	WebUI.click(findTestObject('RegisterEsign/button_Save'))
    	
    	if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    		'click button ya proses'
    		WebUI.click(findTestObject('RegisterEsign/button_YaProses'))
    	}
    	
    	if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    		'call function get error log'
    		getErrorLog()
    	} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')) == 
    			'0') {
    		'write to excel failed'
    		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
    				(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
    						'-', '') + ';') + ' Link tergenerate walupun sudah tidak active')
    		
    		GlobalVariable.FlagFailed = 1
    	} else {
    		'click tutup popup'
    		WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))
    	}
    }
	  
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).length() > 
    0) {
    	'call test case inquiry invitation'
    	WebUI.callTestCase(findTestCase('Register eSign/InquiryInvitation/InquiryInvitation'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
    			FailureHandling.CONTINUE_ON_FAILURE)
    } else {
    	'call test case verif Submit Data'
    	WebUI.callTestCase(findTestCase('Register eSign/verifSubmitData'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
    			FailureHandling.CONTINUE_ON_FAILURE)
    }
    
	'get saldo before'
	saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)
	
	countCheckSaldo = 1
	
    'call test case daftar akun data verif'
    WebUI.callTestCase(findTestCase('Register eSign/DaftarAkunDataVerif'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'
    	, ('saldoBefore') : saldoBefore[0]], FailureHandling.CONTINUE_ON_FAILURE)
    
	'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form registrasi'
	while (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase('Continue')) {
		GlobalVariable.NumofColm++
		
		GlobalVariable.FlagFailed = 0
		
		'call test case daftar akun data verif'
	    WebUI.callTestCase(findTestCase('Register eSign/DaftarAkunDataVerif'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'
	    	, ('saldoBefore') : saldoBefore[0]], FailureHandling.CONTINUE_ON_FAILURE)
	}
	
    if (GlobalVariable.checkStoreDB == 'Yes' && (GlobalVariable.FlagFailed == 0 || findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Warning'))
		 && (GlobalVariable.FlagFailed == 0 || findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted'))) {
    	'delay nunggu data db'
    	WebUI.delay(5)
    	
    	'call test case BuatUndanganStore DB'
    	WebUI.callTestCase(findTestCase('Register eSign/BuatUndanganStoreDB'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
    			FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    if (GlobalVariable.FlagFailed == 0) {
    	'kurang saldo before dengan proses verifikasi'
    	saldoBefore.set(1, (Integer.parseInt(saldoBefore[1]) - GlobalVariable.VerificationCount).toString())
    	
    	if (GlobalVariable.Psre == 'VIDA') {
    		'kurang saldo before dengan prose PNBP'
    		saldoBefore.set(2, (Integer.parseInt(saldoBefore[2]) - 1).toString())
    		
    		'kurang saldo before dengan jumlah counter send OTP'
    		saldoBefore.set(0, (Integer.parseInt(saldoBefore[0]) - GlobalVariable.Counter).toString())
    	} else {
    		'kurang saldo before dengan jumlah counter send OTP'
    		saldoBefore.set(0, (Integer.parseInt(saldoBefore[0]) - GlobalVariable.Counter).toString())
    	}
    	
    	saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
    			
		'verify saldoafter tidak sama dengan saldo before'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(saldoAfter.toString(), saldoBefore.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
				' Saldo')
		
		'print untuk menunjukan saldobefore dan saldoafter'
		println('saldoBefore : ' + saldoBefore.toString())
		
		println('saldoAfter : ' + saldoAfter.toString())
		
		if ((((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == '0' || 
			findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == 'null') && 
			(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '1')) || (
			(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '0'))) &&
			findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase().contains('OUTLOOK.COM') &&
			GlobalVariable.Psre == 'VIDA') {
			'call keyword get email'
			String emailCert = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPathBuatUndangan).getValue(
								GlobalVariable.NumofColm, rowExcel('Email')), findTestData(excelPathBuatUndangan).getValue(
								GlobalVariable.NumofColm, rowExcel('Password')))
			
			'verify email cert'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(emailCert, 'Penerbitan Sertifikat Elektronik',
					false, FailureHandling.CONTINUE_ON_FAILURE), ' email cert tidak terkirim')
		}
    }
	
	GlobalVariable.LoginAgain = 0
}

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Check Inquiry Setelah Register')).equalsIgnoreCase(
    'Yes') && (GlobalVariable.FlagFailed == 0)) {
    'call test case untuk cek inquiry invitation field after register'
    WebUI.callTestCase(findTestCase('Register eSign/InquiryInvitation/InquiryInvitationAfterRegist'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
    ArrayList<String> saldo = []

    'navigate to url esign'
    WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))

    'maximize window'
    WebUI.maximizeWindow()

    'store user login'	
	GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase()
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')))
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Password Login')))
	
	'click button login'
	WebUI.click(findTestObject('Login/button_Login'))
	
	if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
		'input perusahaan'
		WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan Login')))
		
		'enter untuk input perusahaan'
		WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
		
		'input peran'
		WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Peran Login')))
		
		'enter untuk input peran'
		WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
		
		'click button pilih peran'
		WebUI.click(findTestObject('Login/button_pilihPeran'))
	}

    'check if button menu visible atau tidak'
    if (WebUI.verifyElementNotVisible(findTestObject('RegisterEsign/checkSaldo/menu_Saldo'), FailureHandling.OPTIONAL)) {
        'click menu saldo'
        WebUI.click(findTestObject('button_HamburberSideMenu'))
    }
    
    'click menu saldo'
    WebUI.click(findTestObject('RegisterEsign/checkSaldo/menu_Saldo'))

    'click ddl bahasa'
    WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_bahasa'))

    'click english'
    WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_English'))

    'select vendor'
    WebUI.selectOptionByLabel(findTestObject('RegisterEsign/checkSaldo/select_Vendor'), '(?i)' + 'ESIGN/ADINS', true)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    for (index = 2; index <= variable.size(); index++) {
        'modify object box info'
        modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
            ']/div/div/div/div/div[1]/h3', true)

        'check if box info = tipe saldo di excel'
        if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('OTP')) {
            'modify object qty'
            modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
                ']/div/div/div/div/div[2]/h3', true)

            'get qty saldo before'
            saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

            break
        }
    }
    
    if (((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0)) && (GlobalVariable.Psre == 'VIDA') && (GlobalVariable.Psre == 'PRIVY')) {
        'call function input filter saldo'
        inputFilterSaldo('OTP', conneSign)
    }
    
	'cek apakah vendor merupakan Tekenaja'
	if (GlobalVariable.Psre == 'TKNAJ') {
		
		'select vendor'
		WebUI.selectOptionByLabel(findTestObject('RegisterEsign/checkSaldo/select_Vendor'), '(?i)' + 'TEKENAJA', true)
	} else {
		
		'select vendor'
		WebUI.selectOptionByLabel(findTestObject('RegisterEsign/checkSaldo/select_Vendor'), '(?i)' + GlobalVariable.Psre, true)
	}
    
    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))
	
    for (index = 2; index <= variable.size(); index++) {
        'modify object box info'
        modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
            ']/div/div/div/div/div[1]/h3', true)

        'check if box info = tipe saldo di excel'
        if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Verification') || (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase(
            'PNBP') && (GlobalVariable.Psre == 'VIDA'))) {
            'modify object qty'
            modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
                ']/div/div/div/div/div[2]/h3', true)

            'get qty saldo before'
            saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

            'if saldo sudah terisi 2 verification dan pnbp'
            if ((saldo.size() == 3) && (GlobalVariable.Psre == 'VIDA')) {
                break
            } else if ((saldo.size() == 2) && (GlobalVariable.Psre == 'PRIVY')) {
                break
            }
            
            continue
        }
    }
    
    if (countCheckSaldo == 1) {
        'call function input filter saldo'
        inputFilterSaldo('Verification', conneSign)

        if (GlobalVariable.Psre == 'VIDA') {
			if(GlobalVariable.FlagFailed == 0) {
				'call function input filter saldo'
				inputFilterSaldo('PNBP', conneSign)				
			}
			'call function verify list undangan'
			verifyListUndangan()
        }        
    }
    
//    'swicth tab ke new tab'
//    WebUI.switchToWindowIndex(0)
//
//    'close tab saldo'
//    WebUI.closeWindowIndex(1)
//
//    'swicth tab ke new tab'
//    WebUI.switchToWindowIndex(0)

    return saldo
}

def verifyListUndangan() {
    currentDate = new Date().format('yyyy-MM-dd')

    'click menu list undangan'
    WebUI.click(findTestObject('ListUndangan/menu_ListUndangan'))

    'set text nama'
    WebUI.setText(findTestObject('ListUndangan/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama')))

    'set text penerima undangan'
    WebUI.setText(findTestObject('ListUndangan/input_PenerimaUndangan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Email')))

    'set text tanggal pengiriman dari'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), currentDate)

    'set text tanggal pengiriman ke'
    WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), currentDate)

    'click button cari'
    WebUI.click(findTestObject('ListUndangan/button_Cari'))

    'verify nama'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_Nama')), findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, rowExcel('$Nama')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

    'verify pengiriman melalui'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PengirimanMelalui')), 'Email', 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengiriman Melalui')

    'verify penerima undangan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PenerimaUndangan')), findTestData(
                excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Penerima Undangan')

    tanggalPengiriman = WebUI.getText(findTestObject('ListUndangan/table_TanggalPengiriman')).split(' ', -1)

    parsedDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(tanggalPengiriman[0], 'dd-MMM-yyyy', 'yyyy-MM-dd')

    'verify tanggal pengiriman'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(parsedDate, currentDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Pengiriman')

    if (GlobalVariable.Psre == 'VIDA') {
		tanggalRegistrasi = WebUI.getText(findTestObject('ListUndangan/table_TanggalRegistrasi')).split(' ', -1)

        parsedDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(tanggalRegistrasi[0], 'dd-MMM-yyyy', 'yyyy-MM-dd')

        'verify tanggal registrasi'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(parsedDate, currentDate, false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Tanggal Registrasi')
    
	    'verify status registrasi'
	    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusRegistrasi')), 'DONE', 
	            false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Registrasi')
		
		'verify Status undangan'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusUndangan')), 'NON AKTIF',
				false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Undangan')
    } else if(GlobalVariable.Psre == 'PRIVY') {
		'verify tanggal registrasi'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_TanggalRegistrasi')), '-', false, FailureHandling.CONTINUE_ON_FAILURE),
			' Tanggal Registrasi')
	
		'verify status registrasi'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusRegistrasi')), 'NOT DONE',
				false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Registrasi')
		
		'verify Status undangan'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusUndangan')), 'AKTIF',
				false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Undangan')
	}
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def inputBuatUndangan() {
    'click menu buat undangan'
    WebUI.click(findTestObject('RegisterEsign/menu_BuatUndangan'))

    'input NIK'
    WebUI.setText(findTestObject('RegisterEsign/input_NIK'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$NIK')))

    'input nama lengkap'
    WebUI.setText(findTestObject('RegisterEsign/input_NamaLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama')))

    'input tempat lahir'
    WebUI.setText(findTestObject('RegisterEsign/input_TempatLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tempat Lahir')))

    'input tanggal lahir'
    WebUI.setText(findTestObject('RegisterEsign/input_TanggalLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tanggal Lahir')))

    'cek if pria(M) / wanita(F)'
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).equalsIgnoreCase(
        'M')) {
        'click radio pria'
        WebUI.click(findTestObject('RegisterEsign/radio_Pria'))
    } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).equalsIgnoreCase(
        'F')) {
        'click radio wanita'
        WebUI.click(findTestObject('RegisterEsign/radio_Wanita'))
    }
    
    'input no handphone'
    WebUI.setText(findTestObject('RegisterEsign/input_noHandphone'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$No Handphone')))

    'input email'
    WebUI.setText(findTestObject('RegisterEsign/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Email')))

    'input alamat lengkap'
    WebUI.setText(findTestObject('RegisterEsign/input_AlamatLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Alamat')))

    'input provinsi'
    WebUI.setText(findTestObject('RegisterEsign/input_Provinsi'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Provinsi')))

    'input kota'
    WebUI.setText(findTestObject('RegisterEsign/input_Kota'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kota')))

    'input kecamatan'
    WebUI.setText(findTestObject('RegisterEsign/input_Kecamatan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kecamatan')))

    'input kelurahan'
    WebUI.setText(findTestObject('RegisterEsign/input_Kelurahan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kelurahan')))

    'input kode pos'
    WebUI.setText(findTestObject('RegisterEsign/input_KodePos'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kode Pos')))

    'input wilayah'
    WebUI.setText(findTestObject('RegisterEsign/input_Wilayah'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Wilayah')))

    'input office'
    WebUI.setText(findTestObject('RegisterEsign/input_Office'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Office')))

    'input lini bisnis'
    WebUI.setText(findTestObject('RegisterEsign/input_LiniBisnis'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Lini Bisnis')))

    'input task no'
    WebUI.setText(findTestObject('RegisterEsign/input_TaskNo'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('Task No')))
}

def inputCancel() {
    if (GlobalVariable.NumofColm == 2) {
        'input form buat undangan'
        inputBuatUndangan()

        'click button cancel'
        WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

        'click button ya batal undangan'
        WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))

        'check if sudah ter cancel dan pindah page ke home'
        if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/input_Kecamatan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            checkVerifyEqualOrMatch(false, 'FAILED TO CANCEL')
        }
        
        'click menu buat undangan'
        WebUI.click(findTestObject('RegisterEsign/menu_BuatUndangan'))

        'verify field NIK kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_NIK'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field NIK tidak kosong')

        'verify field Nama Lengkap kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_NamaLengkap'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Nama Lengkap tidak kosong')

        'verify field tempat lahir kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TempatLahir'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Tempat Lahir tidak kosong')

        'verify field tanggal lahir kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TanggalLahir'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field tanggal Lahir tidak kosong')

        'verify field no handphone kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_noHandphone'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field no Handphone tidak kosong')

        'verify field email kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Email'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Email tidak kosong')

        'verify field alamat Lengkap kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_AlamatLengkap'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field alamat lengkap tidak kosong')

        'verify field Provinsi kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Provinsi'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Provinsi tidak kosong')

        'verify field Kota kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kota'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Kota tidak kosong')

        'verify field kecamatan kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kecamatan'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Kecamatan tidak kosong')

        'verify field kelurahan kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kelurahan'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field kelurahan tidak kosong')

        'verify field kode pos kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_KodePos'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field kode pos tidak kosong')

        'verify field wilayah kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Wilayah'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field wilayah tidak kosong')

        'verify field office kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Office'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field office tidak kosong')

        'verify field lini bisnis kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_LiniBisnis'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Lini Bisnis tidak kosong')

        'verify field task no kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TaskNo'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field task no tidak kosong')

        'click button cancel'
        WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

        'click button ya batal undangan'
        WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))
    }
}

def inputFilterSaldo(String tipeSaldo, Connection conneSign) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

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

    'modify object tipe transaksi'
    modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

    'modify object user'
    modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

    'modify object no kontrak'
    modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

    'modify object Catatan'
    modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

    'modify object qty'
    modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

    'get trx dari db'
    ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathBuatUndangan).getValue(
            GlobalVariable.NumofColm, rowExcel('Email')), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
            rowExcel('$No Handphone')), 'Use ' + tipeSaldo)

    arrayIndex = 0

    'verify no trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' No Trx ' + tipeSaldo)

    'verify tgl trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
                '.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx ' + tipeSaldo)

    'verify tipe trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Tipe Trx ' + tipeSaldo)

    'verify user trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' User ' + tipeSaldo)

    'verify note trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan).replace(' ', ' '), (result[arrayIndex++]).replace(
                ' ', ' '), false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes ' + tipeSaldo)

    'verify qty trx ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''), false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx ' + tipeSaldo)
}

def getErrorLog() {
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + '<') + ReasonFailed) + '>')

    if ((ReasonFailed.contains('sudah digunakan di link') || ReasonFailed.contains('sudah terdaftar')) || ReasonFailed.contains(
        'Tidak bisa generate ulang invitation link')) {
        'declare error type error'
        GlobalVariable.ErrorType = 'ERROR'
    } else {
        'declare error type reject'
        GlobalVariable.ErrorType = 'REJECT'
    }
    
    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Register eSign/ErrorReport'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}

