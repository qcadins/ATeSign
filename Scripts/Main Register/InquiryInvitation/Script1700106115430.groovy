import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

if (WebUI.verifyElementNotPresent(findTestObject('InquiryInvitation/menu_InquiryInvitation'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'call test case login per case'
    WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathRegister, ('Email') : 'Inveditor Login'
            , ('Password') : 'Inveditor Password Login', ('Perusahaan') : 'Inveditor Perusahaan Login', ('Peran') : 'Inveditor Peran Login'], 
        FailureHandling.STOP_ON_FAILURE)
}

'click menu inquiry invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

int editAfterRegister = CustomKeywords.'connection.InquiryInvitation.getSetEditAfterRegister'(conneSign)

int resendLink = CustomKeywords.'connection.InquiryInvitation.getSetResendLink'(conneSign)

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2) {
    receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', 
        '').toUpperCase()
} else {
    receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace(
        '"', '').toUpperCase()
}

int invLink = CustomKeywords.'connection.InquiryInvitation.getSetInvLinkAct'(conneSign, receiverDetail)

'call function inputSearch'
inputSearch()

'click button cari'
WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/Table_InquiryInvitation'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Edit') && (editAfterRegister == 1)) {
        'click button Edit'
        WebUI.click(findTestObject('InquiryInvitation/button_Edit'))

        'get data buat undangan dari DB'
        ArrayList result = CustomKeywords.'connection.InquiryInvitation.inquiryInvitationViewDataVerif'(conneSign, findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '').toUpperCase())

        '1 karena invited by belum bisa di get value dari UI'
        arrayindex = 1

        //'verify invitationby'
        //checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/select_InviteBy')).toUpperCase(), result[arrayindex++].toUpperCase(), false), ' Invitation by tidak sesuai')
        'verify receiver'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Receiver'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Receiver tidak sesuai')

        'verify NIK'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NIK'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' NIK tidak sesuai')

        'verify Nama'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Nama'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Nama tidak sesuai')

        'verify TempatLahir'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TempatLahir'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Tempat Lahir tidak sesuai')

        'verify TanggalLahir'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_TanggalLahir'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir tidak sesuai')

        'verify No Handphone'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_NoHandphone'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' No Handphone tidak sesuai')

        'verify Email'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Email tidak sesuai')

        'verify Alamat'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Alamat'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Alamat tidak sesuai')

        'verify Provinsi'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Provinsi'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Provinsi tidak sesuai')

        'verify Kota'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kota'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Kota tidak sesuai')

        'verify Kecamatan'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kecamatan'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Kecamatan tidak sesuai')

        'verify Kelurahan'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Kelurahan'), 
                    'value', FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Kelurahan tidak sesuai')

        'verify KodePos'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('InquiryInvitation/edit_KodePos'), 'value', 
                    FailureHandling.CONTINUE_ON_FAILURE).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Kode Pos tidak sesuai')

        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).length() > 0) {
            'input invited by'
			inputDDLExact('InquiryInvitation/select_InviteBy', findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Invite By')))

            'input receiver detail'
            WebUI.setText(findTestObject('InquiryInvitation/edit_Receiver'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Receiver Detail')))
        }
        
        'input NIK'
        WebUI.setText(findTestObject('InquiryInvitation/edit_NIK'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('NIK - Edit')).replace('"', ''))

        'input nama lengkap'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Nama'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Nama - Edit')).replace('"', ''))

        'input tempat lahir'
        WebUI.setText(findTestObject('InquiryInvitation/edit_TempatLahir'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Tempat Lahir - Edit')).replace('"', ''))

        'input tanggal lahir'
        WebUI.setText(findTestObject('InquiryInvitation/edit_TanggalLahir'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Tanggal Lahir - Edit')).replace('"', ''))

        'cek if pria(M) / wanita(F)'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin - Edit')).replace(
            '"', '').equalsIgnoreCase('M')) {
            'click radio pria'
            WebUI.click(findTestObject('InquiryInvitation/radio_Male'))
        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin - Edit')).replace(
            '"', '').equalsIgnoreCase('F')) {
            'click radio wanita'
            WebUI.click(findTestObject('InquiryInvitation/radio_Female'))
        }
        
        if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_noHandphone'), 'disabled', FailureHandling.OPTIONAL) != 
        'true') {
            'input no handphone'
            WebUI.setText(findTestObject('InquiryInvitation/edit_noHandphone'), findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('No Telepon - Edit')).replace('"', ''))
        }
        
        if (WebUI.getAttribute(findTestObject('InquiryInvitation/edit_Email'), 'disabled', FailureHandling.OPTIONAL) != 
        'true') {
            'input email'
            WebUI.setText(findTestObject('InquiryInvitation/edit_Email'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Email - Edit')).replace('"', ''))
        }
        
        'input alamat lengkap'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Alamat'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Alamat - Edit')).replace('"', ''))

        'input provinsi'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Provinsi'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Provinsi - Edit')).replace('"', ''))

        'input kota'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Kota'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Kota - Edit')).replace('"', ''))

        'input kecamatan'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Kecamatan'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Kecamatan - Edit')).replace('"', ''))

        'input kelurahan'
        WebUI.setText(findTestObject('InquiryInvitation/edit_Kelurahan'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Kelurahan - Edit')).replace('"', ''))

        'input kode pos'
        WebUI.setText(findTestObject('InquiryInvitation/edit_KodePos'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Kode Pos - Edit')).replace('"', ''))

        'click button Simpan'
        WebUI.click(findTestObject('InquiryInvitation/button_Simpan'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_OkSuccess'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
        WebUI.verifyElementNotPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test store db inquiry invitation'
                WebUI.callTestCase(findTestCase('Main Register/InquiryInvitationStoreDB'), [:], FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'set text search box dengan Phone'
            WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('NIK - Edit')).replace('"', ''))

            'click button cari'
            WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

            'verify data yang sudah di edit dapat di search di inquiry invitation'
            WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
        } else {
            'get reason'
            ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + ReasonFailed) + '>')

            GlobalVariable.FlagFailed = 1

            'click button OK'
            WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

            'click button batal'
            WebUI.click(findTestObject('InquiryInvitation/button_Batal'))
        }
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Kirim Ulang Undangan') && (invLink == 1)) {
        'get label invited by'
        invitedBy = WebUI.getText(findTestObject('Object Repository/InquiryInvitation/label_InviteBy'))

        'click button Resend'
        WebUI.click(findTestObject('InquiryInvitation/button_KirimUlangUndangan'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))

            if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason'
                ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                if (ReasonFailed.contains('Undangan terkirim ke')) {
                	'write to excel success'
                	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                			1, GlobalVariable.StatusSuccess)
                	
                	if (invitedBy.equalsIgnoreCase('SMS')) {
                		'get data saldo'
                		String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)
                				
        				'verify saldo'
        				if (!WebUI.verifyMatch(result, '-1', false, FailureHandling.CONTINUE_ON_FAILURE)) {
							'write to excel status failed dan ReasonFailed'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
									rowExcel('Reason Failed')).replace('-', '') + ';') + ' Saldo tidak sesuai')))
						} 
                	}
                } else {
					'write to excel status failed dan ReasonFailed'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + ReasonFailed) + '>')

					if (invitedBy.equalsIgnoreCase('SMS')) {
						'get data saldo'
						String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

						'verify saldo'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '0', false, FailureHandling.CONTINUE_ON_FAILURE),
							' Saldo tidak sesuai')
					}
					
					GlobalVariable.FlagFailed = 1
                }
            }
        } else if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason'
            ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + ReasonFailed) + '>')

            GlobalVariable.FlagFailed = 1
        }
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Kirim Ulang Aktivasi') && (resendLink == 1)) {
        'get label invited by'
        invitedBy = WebUI.getText(findTestObject('Object Repository/InquiryInvitation/label_InviteBy'))

        'click button Resend'
        WebUI.click(findTestObject('InquiryInvitation/button_KirimUlangLinkAktivasi'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))

            if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason'
                ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                if (ReasonFailed.contains('Berhasil')) {
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
						1, GlobalVariable.StatusSuccess)

					if (invitedBy.equalsIgnoreCase('SMS')) {
						'get data saldo'
						String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)

						'verify saldo'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '-1', false, FailureHandling.CONTINUE_ON_FAILURE),
							' Saldo tidak sesuai')
					}
                } else {
                	'write to excel status failed dan ReasonFailed'
                	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                			GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                					rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + ReasonFailed) + '>')
                	
                	GlobalVariable.FlagFailed = 1
                	
                	if (invitedBy.equalsIgnoreCase('SMS')) {
                		'get data saldo'
                		String result = CustomKeywords.'connection.DataVerif.getSaldo'(conneSign, GlobalVariable.userLogin)
                				
        				'verify saldo'
        				checkVerifyEqualOrMatch(WebUI.verifyMatch(result, '0', false, FailureHandling.CONTINUE_ON_FAILURE), 
        						' Saldo tidak sesuai')
                	}
                }
            }
        } else if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason'
            ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + ReasonFailed) + '>')

            GlobalVariable.FlagFailed = 1
        }
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase(
        'Regenerate invitation link')) {
        'click button Regenerate invitation link'
        WebUI.click(findTestObject('InquiryInvitation/button_RegenerateInvLink'))

        if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button ya proses'
            WebUI.click(findTestObject('InquiryInvitation/button_YaProses'))

            if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason'
                ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + ReasonFailed) + '>')

                GlobalVariable.FlagFailed = 1
            } else if (WebUI.getText(findTestObject('InquiryInvitation/label_PopUp'), FailureHandling.OPTIONAL).equalsIgnoreCase(
                'Success')) {
                'click button OK'
                WebUI.click(findTestObject('InquiryInvitation/button_OkSuccess'))

                'call function input search'
                inputSearch()

                'click button cari'
                WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

                'click button ViewLink'
                WebUI.click(findTestObject('InquiryInvitation/button_ViewLink'))

                'get link'
                GlobalVariable.Link = WebUI.getAttribute(findTestObject('InquiryInvitation/input_Link'), 'value', FailureHandling.OPTIONAL)

                'verify link bukan undefined atau kosong'
                if (GlobalVariable.Link.equalsIgnoreCase('Undefined') || GlobalVariable.Link.equalsIgnoreCase('')) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + ' Link tidak ter-regenerated')

                    GlobalVariable.FlagFailed = 1
                } else {
                    'HIT API get Invitation Link'
                    responGetInvLink = WS.sendRequest(findTestObject('Postman/Get Inv Link', [('callerId') : findTestData(
                                    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('receiverDetail') : receiverDetail]))

                    'Jika status HIT API 200 OK'
                    if (WS.verifyResponseStatusCode(responGetInvLink, 200, FailureHandling.OPTIONAL) == true) {
                        'get Status Code'
                        status_Code = WS.getElementPropertyValue(responGetInvLink, 'status.code')

                        'Jika status codenya 0'
                        if (status_Code == 0) {
                            'Get invitation Link'
                            InvitationLink = WS.getElementPropertyValue(responGetInvLink, 'invitationLink')

                            if (!(WebUI.verifyMatch(GlobalVariable.Link, InvitationLink, false, FailureHandling.CONTINUE_ON_FAILURE))) {
                                'write to excel status failed dan reason'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                                    ' FE Inquiry Inv View Link dan API Get Inv Link')

                                GlobalVariable.FlagFailed = 1
                            }
                        } else {
                            messageFailed = WS.getElementPropertyValue(responGetInvLink, 'status.message', FailureHandling.OPTIONAL).toString()

                            'write to excel status failed dan reason'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + messageFailed) + '>')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
                
                'click button TutupDapatLink'
                WebUI.click(findTestObject('InquiryInvitation/button_TutupDapatLink'))

                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            }
        }
    }
} else {
    'Write To Excel GlobalVariable.StatusFailed and reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ' Data Tidak Ditemukan di Inquiry Invitation')

    GlobalVariable.FlagFailed = 1
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def inputSearch() {
    'check if search dengan email/phone/id no'
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input With')).equalsIgnoreCase('Email')) {
        'set text search box dengan email'
        WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Email')).replace('"', ''))
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input With')).equalsIgnoreCase(
        'Phone')) {
        'set text search box dengan Phone'
        WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('No Telepon')).replace('"', ''))
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input With')).equalsIgnoreCase(
        'NIK')) {
        'set text search box dengan NIK'
        WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$NIK')).replace('"', ''))
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))
	
	'get token unik'
	tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')
	
	'modify object label Value'
	modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div['+ (i + 1) +']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}