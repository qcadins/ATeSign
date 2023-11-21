import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'declare invitation link inquiry'
String invitationLinkInquiry = ''

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathGenerateLink).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'get psre from excel percase'
		GlobalVariable.Psre = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		'get Tenant from excel percase'
		GlobalVariable.Tenant = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
	
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGenerateLink, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))
		
		'call test case login per case'
		WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathGenerateLink, ('Email') : 'Inveditor Login', ('Password') : 'Inveditor Password Login'
			, ('Perusahaan') : 'Inveditor Perusahaan Login', ('Peran') : 'Inveditor Peran Login'], FailureHandling.STOP_ON_FAILURE)
		
		'check ada value maka setting email service tenant'
		if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.Registrasi.settingEmailServiceTenant'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Servicel')))
		}
		
		'check ada value maka setting email certif notif'
		if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')).length() > 0) {
			'setting email email certif notif'
			CustomKeywords.'connection.Registrasi.settingSendCertNotifbySMS'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')))
		}
		
		'check ada value maka setting allow regenerate link'
		if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')).length() > 0) {
			'setting allow regenerate link'
			CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))
		}

		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		} else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}
		
        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList<String> listInvitation = []

        'Declare variable untuk sendRequest'
        (listInvitation[0]) = (((((((((((((((((((((((((('{"email" :' + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('email'))) + ',"nama" :') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('nama'))) + ',"tlp": ') + findTestData(
            excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp'))) + ',"jenisKelamin" : ') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('jenisKelamin'))) + ',"tmpLahir" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('tmpLahir'))) + ',"tglLahir" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir'))) + ',"idKtp" : ') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp'))) + ', "provinsi" : ') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('provinsi'))) + ', "kota" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('kota'))) + ', "kecamatan" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan'))) + ',"kelurahan": ') + 
        findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan'))) + ',"kodePos" : ') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('kodePos'))) + ',"alamat" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('alamat'))) + '}  ')

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : findTestData(excelPathGenerateLink).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('tenantCode') : findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                        rowExcel('$tenantCode')), ('users') : listInvitation[0]]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (status_Code == 0) {
                'Mengambil links berdasarkan response HIT API'
                links = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL)

				'check ada value maka setting Link Is Active'
				if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')) == '0') {
					'setting Link Is Active'
					CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')),
						findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))
					
					'HIT API'
			        respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : findTestData(excelPathGenerateLink).getValue(
			                        GlobalVariable.NumofColm, rowExcel('callerId')), ('tenantCode') : findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
			                        rowExcel('$tenantCode')), ('users') : listInvitation[0]]))
					
					'Jika status HIT API 200 OK'
					if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
						'get status code'
						code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
						
						if (code == 0) {
							'Mengambil links berdasarkan response HIT API'
							links = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL)
							
							'write to excel failed'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
									'-', '') + ';') + ' Link tergenerate walupun sudah tidak active')
						} else {
						   'call function get API error message'
						   getAPIErrorMessage(respon)
						}
					} else {
					   'call function get API error message'
					   getAPIErrorMessage(respon)
					}
					
					continue
				}
				
                'Klik menu Inquiry Invitation'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/menu_InquiryInvitation'))

                'Jika pencarian menggunakan email'
                if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('email')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(excelPathGenerateLink).getValue(
                            GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

                    'Jika pencarian menggunakan nomor telp'
                } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('phone')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(excelPathGenerateLink).getValue(
                            GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', ''))
                } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input with')).equalsIgnoreCase('id no')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(excelPathGenerateLink).getValue(
                            GlobalVariable.NumofColm, rowExcel('idKtp')).replace('"', ''))
                }
                
                'Klik button Cari'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_Cari'), FailureHandling.CONTINUE_ON_FAILURE)

                if (WebUI.verifyElementPresent(findTestObject('Object Repository/InquiryInvitation/button_ViewLink'), 2, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'Klik button View Link'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_ViewLink'))

                    if (WebUI.verifyElementPresent(findTestObject('Object Repository/InquiryInvitation/errorLog'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        errorLog = WebUI.getAttribute(findTestObject('Object Repository/InquiryInvitation/errorLog'), 'aria-label')

                        'write to excel status failed dan reason : errorLog UI'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + errorLog + '>')
                    }
                    
                    'Klik pop up link'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/input_Link'), FailureHandling.CONTINUE_ON_FAILURE)

                    'Mengambil value dari pop up'
                    invitationLinkInquiry = WebUI.getAttribute(findTestObject('Object Repository/InquiryInvitation/input_Link'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE)

                    'Button tutup'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_TutupDapatLink'), FailureHandling.CONTINUE_ON_FAILURE)
                }
                
                'Klik menu Inquiry Invitation'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/menu_InquiryInvitation'))

                'Verify value pop up dan response HIT API'
                if (WebUI.verifyMatch(links.toString().replace('[', '').replace(']', ''), invitationLinkInquiry, false, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                    if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                        'call test case ResponseAPIStoreDB'
                        WebUI.callTestCase(findTestCase('Generate Invitation Link/ResponseAPIStoreDB'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link'], 
                            FailureHandling.CONTINUE_ON_FAILURE)
                    }
                    
                    'store link di GV'
                    GlobalVariable.Link = links.toString().replace('[', '').replace(']', '')

                    'call test case daftar akun'
                    WebUI.callTestCase(findTestCase('Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link'], 
                        FailureHandling.STOP_ON_FAILURE)
					
					'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form registrasi'
					while (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase('Continue')) {
						GlobalVariable.NumofColm++
						
						GlobalVariable.FlagFailed = 0
						
						'call test case daftar akun'
	                    WebUI.callTestCase(findTestCase('Generate Invitation Link/DaftarAkunDataVerif'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link'], 
	                        FailureHandling.STOP_ON_FAILURE)
					}
					
					if ((((findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == '0' || 
								findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Certif Notif')) == 'null') && 
								(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '1')) || (
								(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Services')) == '0'))) &&
								findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase().contains('OUTLOOK.COM') &&
								GlobalVariable.Psre == 'VIDA') {
						'call keyword get email'
						String emailCert = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(findTestData(excelPathGenerateLink).getValue(
											GlobalVariable.NumofColm, rowExcel('email')).replace('"',''), findTestData(excelPathGenerateLink).getValue(
											GlobalVariable.NumofColm, rowExcel('Password')), 'Certif')
						
						'verify email cert'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(emailCert, 'Penerbitan Sertifikat Elektronik',
								false, FailureHandling.CONTINUE_ON_FAILURE), ' email cert tidak terkirim')
					}
                } else {
                    'write to excel status failed dan reason : '
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                            '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
                }
            } else {
				'call function get API error message'
				getAPIErrorMessage(respon)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
        }
        
        'close browser'
        WebUI.closeBrowser()
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def getAPIErrorMessage(def respon) {
	'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
	messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'write to excel status failed dan reason : '
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
			'-', '') + ';') + '<' + messageFailed + '>')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}