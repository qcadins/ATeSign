import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare untuk split array excel'
semicolon = ';'

splitIndex = -1

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathManualSign).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
        //int isMandatoryComplete = Integer.parseInt(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm,
        //		5))
        //		if (GlobalVariable.NumofColm == 2) {
        //			'call function input cancel'
        //			inputCancel()
        //		}
    } else if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        if (GlobalVariable.NumofColm == 2) {
            'memanggil test case login untuk admin wom dengan Admin Client'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathManualSign, ('sheet') : 'Manual Sign'], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'declare isMmandatory Complete'
        GlobalVariable.FlagFailed = 0

        'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
        emailPenandaTangan = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitIndex)

        namaPenandaTangan = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitIndex)

        emailSearchByFrontEnd = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 21).split(semicolon, 
            splitIndex)
		
		titleTandaTangan = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 22).split(semicolon,
			splitIndex)
		
        urutanSigner = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitIndex)

        pindahkanSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 24).split(semicolon, splitIndex)

        lokasiSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 25).split('\\n', splitIndex)

        lockSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 26).split(semicolon, splitIndex)

        'Klik tombol pengaturan dokumen'
        WebUI.click(findTestObject('ManualSign/ManualSign'))

        'Pengecekan apakah masuk page tambah pengaturan dokumen'
        if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_ManualSign'), GlobalVariable.TimeOut)) {
            'get data tipe-tipe pembayaran secara asc'
            ArrayList vendorOfTenantDB = CustomKeywords.'connection.ManualSign.getVendorofTenant'(conneSign, GlobalVariable.Tenant)

            'check ddl tipe pembayaran'
            checkDDL(findTestObject('ManualSign/input_psre'), vendorOfTenantDB, ' pada DDL psre ')

            'Input teks di nama template dokumen'
            WebUI.setText(findTestObject('ManualSign/input_psre'), findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                    8))

            'Klik enter'
            WebUI.sendKeys(findTestObject('ManualSign/input_psre'), Keys.chord(Keys.ENTER))

            'Input teks kode template dokumen'
            WebUI.setText(findTestObject('ManualSign/input_referenceNo'), findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                    9))

            'Input AKtif pada input Status'
            WebUI.setText(findTestObject('ManualSign/input_documentName'), findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                    10))

            'Input AKtif pada input Status'
            WebUI.setText(findTestObject('ManualSign/input_documentDate'), findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                    11))

            'get data tipe-tipe pembayaran secara asc'
            ArrayList jenisPembayaranByVendorDB = CustomKeywords.'connection.ManualSign.getPaymentTypeBasedOnTenantAndVendor'(
                conneSign, GlobalVariable.Tenant, findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 8))

            'check ddl tipe pembayaran'
            checkDDL(findTestObject('ManualSign/input_jenisPembayaran'), jenisPembayaranByVendorDB, ' pada DDL jenis pembayaran ')

            'Input AKtif pada input Status'
            WebUI.setText(findTestObject('ManualSign/input_jenisPembayaran'), findTestData(excelPathManualSign).getValue(
                    GlobalVariable.NumofColm, 12))

            'Klik enter'
            WebUI.sendKeys(findTestObject('ManualSign/input_jenisPembayaran'), Keys.chord(Keys.ENTER))

            'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
            String userDir = System.getProperty('user.dir')

            String filePath = userDir + findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 13)

            'Upload file berdasarkan filePath yang telah dirancang'
            WebUI.uploadFile(findTestObject('ManualSign/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)

            if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
                'Input AKtif pada input Status'
                WebUI.click(findTestObject('ManualSign/input_isE-Meterai'))

                'Input AKtif pada input Status'
                WebUI.setText(findTestObject('ManualSign/input_tipeDokumenPeruri'), findTestData(excelPathManualSign).getValue(
                        GlobalVariable.NumofColm, 16))

                'Klik enter'
                WebUI.sendKeys(findTestObject('ManualSign/input_tipeDokumenPeruri'), Keys.chord(Keys.ENTER))

                'Input AKtif pada input Status'
                WebUI.setText(findTestObject('ManualSign/input_isAutomatedStamp'), findTestData(excelPathManualSign).getValue(
                GlobalVariable.NumofColm, 17))

                'Klik enter'
                WebUI.sendKeys(findTestObject('ManualSign/input_isAutomatedStamp'), Keys.chord(Keys.ENTER))
				
				
				'get row lastest'
				modifyObjectLblDaftarPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/lbl_daftarpenandatangan'),
				'xpath', 'equals', '//*[@id="msxForm"]/div[10]/div[3]/table/tr/td/small', true)
            }
			else {
				modifyObjectLblDaftarPenandaTangan = findTestObject('ManualSign/lbl_daftarpenandatangan')
			}
			
			WebUI.verifyElementPresent(modifyObjectLblDaftarPenandaTangan, GlobalVariable.TimeOut)
			
			WebUI.click(findTestObject('ManualSign/button_tambahtandaTangan'))
			
			WebUI.click(findTestObject('ManualSign/button_Cancel'))
			
			WebUI.verifyElementPresent(modifyObjectLblDaftarPenandaTangan, GlobalVariable.TimeOut)
			
			if (emailPenandaTangan != "") {
				for (int i = 0; i < emailPenandaTangan.size(); i++) {
					WebUI.click(findTestObject('ManualSign/button_tambahtandaTangan'))
					
					if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_TambahPenandaTangan'), GlobalVariable.TimeOut) == true) {
						if (namaPenandaTangan[i] != "") {
							WebUI.setText(findTestObject('ManualSign/input_namaPenandaTangan'), namaPenandaTangan[i])
						}
						
						WebUI.setText(findTestObject('ManualSign/input_emailPenandaTangan'), emailPenandaTangan[i])
						
						if (emailSearchByFrontEnd[i] == 'Yes') {
							WebUI.click(findTestObject('ManualSign/button_searchPenandaTangan'))
							
							WebUI.delay(7)
							}
							
							String verifikasiSigner = CustomKeywords.'connection.ManualSign.getVerificationSigner'(conneSign, emailPenandaTangan[i])

							checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ManualSign/input_emailPenandaTangan').toString()),emailPenandaTangan[i], FailureHandling.CONTINUE_ON_FAILURE), ' pada email Penanda Tangan ')
							
							if (namaPenandaTangan[i] != "") {
								checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ManualSign/input_namaPenandaTangan').toString()),namaPenandaTangan[i], FailureHandling.CONTINUE_ON_FAILURE), ' pada email Penanda Tangan ')
							}
							
							checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ManualSign/input_namaPenandaTangan').toString()),verifikasiSigner, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Penanda Tangan ')
					}
				}
				
				WebUI.click(findTestObject('ManualSign/button_Save'))
			}
            
			'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
			emailPenandaTangan = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitIndex)
	
			namaPenandaTangan = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitIndex)
	
			emailSearchByFrontEnd = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 22).split(semicolon,
				splitIndex)
	
			urutanSigner = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 24).split(semicolon, splitIndex)
	
			pindahkanSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 25).split(semicolon, splitIndex)
	
			lokasiSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 26).split('\\n', splitIndex)
	
			lockSignBox = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 27).split(semicolon, splitIndex)
	
			WebUI.delay(100)

            'Input value tipe pembayaran'
            WebUI.setText(findTestObject('ManualSign/input_tipePembayaran'), findTestData(excelPathManualSign).getValue(
                    GlobalVariable.NumofColm, 12))

            'Input enter'
            WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))

            'get data tipe-tipe pembayaran secara asc'
            ArrayList resultVendorDDL = CustomKeywords.'connection.PengaturanDokumen.getDDLVendor'(conneSign)

            'check ddl psre'
            checkDDL(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), resultVendorDDL, ' pada DDL Psre ')

            'Input value Psre'
            WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), findTestData(excelPathManualSign).getValue(
                    GlobalVariable.NumofColm, 20))

            'Input enter'
            WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), Keys.chord(Keys.ENTER))

            'Input value sequential sign'
            WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/select_SequentialSigning'), findTestData(
                    excelPathManualSign).getValue(GlobalVariable.NumofColm, 21))

            'Input enter'
            WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/select_SequentialSigning'), Keys.chord(Keys.ENTER))

            'Input value status'
            WebUI.setText(findTestObject('TandaTanganDokumen/input_Status'), findTestData(excelPathManualSign).getValue(
                    GlobalVariable.NumofColm, 14))

            'Input enter'
            WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

            'Jika panjang dokumen lebih besar dari 0'
            if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 13).length() > 0) {
                'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'

                'Upload file berdasarkan filePath yang telah dirancang'
                WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'Klik button lanjut'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

            if (isMandatoryComplete > 0) {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + semicolon) + GlobalVariable.ReasonFailedMandatory)

                GlobalVariable.FlagFailed = 1
            } else if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_konfirmasi'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'Klik Konfirmasi no'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))

                'Data document template code, template name, template description yang telah diinput diverifikasi dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            9), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 'value'), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Document Template Code ')

                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            10), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 'value'), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Document Template Name')

                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            11), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                            'value'), false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Deskripsi Document Template')

                'Klik dropdown mengenai tipe pembayaran'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

                'Verifikasi input tipe pembayaran dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            12), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' pada Tipe Pembayaran ')

                'Klik dropdown mengenai status'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlaktif'))

                'Verifikasi input status dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            14), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' pada Status ')

                'Input enter'
                WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

                'Klik lanjut'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

                'Klik Konfirmasi Yes'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))

                if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/errorlog'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'get reason'
                    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + semicolon) + ReasonFailed)

                    GlobalVariable.FlagFailed = 1
                } else if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'Klik button tanda tangan'
                    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))

                    'Klik set tanda tangan'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))

                    'click button lock signbox'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_LockSignBox'))

                    isLocked = WebUI.getAttribute(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 'ng-reflect-ng-class', 
                        FailureHandling.STOP_ON_FAILURE)

                    'verify sign box is locked'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(isLocked, 'fa-lock', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' pada sign locked ')

                    'Klik button Delete'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_DeleteSignBox'))

                    'verify sign box is deleted'
                    checkVerifyEqualorMatch(WebUI.verifyElementNotPresent(findTestObject('TandaTanganDokumen/signBox'), 
                            GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE), ' pada deletenya box Sign ')

                    'looping berdasarkan total tanda tangan'
                    for (int j = 1; j <= RoleTandaTangan.size(); j++) {
                        if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
                            'Klik button tanda tangan'
                            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
                        } else if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('Paraf')) {
                            'Klik button tanda tangan'
                            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_paraf'))
                        } else if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
                            'Klik button tanda tangan'
                            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_materai'))
                        }
                        
                        if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD') || (TipeTandaTangan[(j - 1)]).equalsIgnoreCase(
                            'Paraf')) {
                            'Verify label tanda tangannya muncul atau tidak'
                            WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            'Klik set tanda tangan'
                            WebUI.click(findTestObject('TandaTanganDokumen/ddl_TipeTandaTangan'))

                            'Memilih tipe signer apa berdasarkan excel'
                            WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_TipeTandaTangan'), RoleTandaTangan[
                                (j - 1)], false)

                            'Klik set tanda tangan'
                            WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))

                            'modify label tipe tanda tangan di kotak'
                            modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDTipeTandaTangan'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                j) + ']/div/div/small', true)

                            'Verifikasi antara excel dan UI, apakah tipenya sama'
                            WebUI.verifyMatch(RoleTandaTangan[(j - 1)], WebUI.getText(modifyobjectTTDlblRoleTandaTangan), 
                                false)
                        }
                        
                        'Verify apakah tanda tangannya ada'
                        if (WebUI.verifyElementPresent(modifyobjectTTDlblRoleTandaTangan, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                            'check if signbox mau dipindahkan'
                            if ((SignBoxAction[(j - 1)]).equalsIgnoreCase('Yes')) {
                                'memindahkan sign box'
                                CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                    ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                    j) + ']/div', SignBoxLocation[(j - 1)])
                            }
                            
                            'check if signbox mau dilock posisinya'
                            if ((LockSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                                'modify obejct lock signbox'
                                modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
                                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                    j) + ']/div/button[1]/span', true)

                                'click lock signbox'
                                WebUI.click(modifyobjectLockSignBox)
                            }
                        }
                    }
                    
                    'click button simpan'
                    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

                    'check if Sequential signing iya'
                    if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 21).equalsIgnoreCase('Iya')) {
                        'get urutan seq sign dari excel'
                        ArrayList seqSignRole = findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 22).split(
                            ';', -1)

                        'count box'
                        variable = DriverFactory.webDriver.findElements(By.cssSelector('#cdk-drop-list-0 div'))

                        'looping seq sign'
                        for (seq = 1; seq <= variable.size(); seq++) {
                            'modify label tipe tanda tangan di kotak'
                            modifyObject = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                                'xpath', 'equals', ('//*[@id="cdk-drop-list-0"]/div[' + seq) + ']', true)

                            index = (seqSignRole.indexOf(WebUI.getText(modifyObject)) + 1)

                            if (seq != index) {
                                'modify label tipe tanda tangan di kotak'
                                modifyObjectNew = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                                    'xpath', 'equals', ('//*[@id="cdk-drop-list-0"]/div[' + index) + ']', true)

                                'pindahin ke urutan sesuai excel'
                                WebUI.dragAndDropToObject(modifyObject, modifyObjectNew)

                                'mines karena ada perpindahan object'
                                seq--
                            }
                        }
                        
                        'click button simpan'
                        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

                        'delay untuk loading simpan'
                        WebUI.delay(3)
                    }
                    
                    'call function checkpopupberhasil'
                    checkPopUpBerhasil(isMandatoryComplete)
                }
            } else {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + semicolon) + GlobalVariable.ReasonFailedSaveGagal) + ' pada Pembuatan Document Template ')
            }
        }
        
        'check if new or edit'
        if (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New') || findTestData(
            excelPathManualSign).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit')) {
            'call fucntion after edit'
            verifyAfterAddorEdit(TipeTandaTangan)

            'check if storedb = yes dan flagfailed = 0'
            if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                'call test case pengaturan dokumen store db'
                WebUI.callTestCase(findTestCase('PengaturanDokumen/PengaturanDokumenStoreDB'), [('excelPathManualSign') : 'PengaturanDokumen/PengaturanDokumen'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkPaging() {
    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 2'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Page2'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 1'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Page1'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'get total page'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-documents > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object next Page'
    modifyObjectNextPage = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-documents/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        (variable.size() - 1)) + ']', true)

    'click next page'
    WebUI.click(modifyObjectNextPage)

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('TandaTanganDokumen/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'modify object last Page'
    modifyObjectLastPage = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-documents/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        (variable.size() - 2)) + ']', true)

    'click max page'
    WebUI.click(findTestObject('TandaTanganDokumen/button_MaxPage'))

    'verify paging di page terakhir'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectLastPage, 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click min page'
    WebUI.click(findTestObject('TandaTanganDokumen/button_MinPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def searchPengaturanDokumen() {
    'click menu pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 24))

    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 25))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 26))

    'Klik enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Klik button cari'
    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_cari'))
}

def verifyAfterAddorEdit(def TipeTTD) {
    'verify after add / edit kode pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_KodePengaturanDokumen')), 
            findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 9), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' kode pengaturan dokumen')

    'verify after add / edit nama pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_NamaPengaturanDokumen')), 
            findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Nama pengaturan dokumen')

    'verify after add / edit deskripsi pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_DeskripsiPengaturanDokumen')), 
            findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Deskripsi pengaturan dokumen')

    'verify after add / edit TTD pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_TTD')), 
            TipeTTD.count('TTD').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' TTD pengaturan dokumen')

    'verify after add / edit Paraf pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_Paraf')), 
            TipeTTD.count('Paraf').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Paraf pengaturan dokumen')

    'verify after add / edit Meterai pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_Meterai')), 
            TipeTTD.count('Meterai').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Meterai pengaturan dokumen')

    'verify after add / edit Tipe Pembayaran Dokumen pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_TipePembayaranTTD')), 
            findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 12), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Tipe Pembayaran Dokumen pengaturan dokumen')
}

def inputCancel() {
    'click menu pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Klik tombol tambah pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

    'Input text documentTemplateCode'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 9))

    'Input text documentTemplateName'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateName'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 10))

    'Input text documentTemplateDescription'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 11))

    'Input value tipe pembayaran'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_tipePembayaran'), findTestData(excelPathManualSign).getValue(
            GlobalVariable.NumofColm, 12))

    'Input enter'
    WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))

    'Input value status'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_Status'), findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 
            14))

    'Input enter'
    WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Klik button cancel'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Cancel'))

    'check if sudah tercancel dan pindah halaman utama'
    if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/input_tipePembayaran'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        checkVerifyEqualorMatch(false, ' FAILED TO CANCEL')
    }
    
    'Klik tombol tambah pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

    'verify field kode template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Kode Template Dokumen tidak kosong')

    'verify field Nama template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nama Template Dokumen tidak kosong')

    'verify field Deskripsi template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Deskripsi Template Dokumen tidak kosong')

    'verify field tipe pembayaran kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_tipePembayaran'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Tipe Pembayaran tidak kosong')

    'verify field status kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_Status'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field status tidak kosong')

    'Klik button cancel'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Cancel'))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('ManualSign/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('ManualSign/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' + 
            id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    println(listDB)

    println(list)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def checkPopUpBerhasil(int isMandatoryComplete) {
    'check if pengaturan document berhasil disimpan'
    if (isMandatoryComplete > 0) {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathManualSign).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + semicolon) + GlobalVariable.ReasonFailedMandatory)

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.getText(findTestObject('TandaTanganDokumen/label_PopUp')).equalsIgnoreCase('Document Template berhasil di simpan')) {
        'click button ok'
        WebUI.click(findTestObject('TandaTanganDokumen/button_Ok'))

        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PengaturanDokumen', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        }
    }
}

