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
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'declare untuk split array excel dan beberapa variable yang dibutuhkan'
semicolon = ';'

splitIndex = -1

indexForCatatanStamp = 0

int looping

'panggil fungsi login'
WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
	('Path') : excelPathManualSigntoSign], FailureHandling.CONTINUE_ON_FAILURE)

'declare flag failed'
GlobalVariable.FlagFailed = 0

'Inisialisasi array dan variable'
ArrayList namaTandaTangan = [],  notelpTandaTangan = []

indexEmail = 0

'inisiasi index menjadi 8 untuk modify object ketika tidak pada e-meterai'
index = 8

'Inisialisasi variable yang dibutuhkan'
phonePenandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitIndex)

emailPenandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 20).split(semicolon, splitIndex)

editNamaAfterSearch = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitIndex)

namaPenandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 22).split(semicolon, splitIndex)

tipeTandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitIndex)

totalTandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 24).split(semicolon, splitIndex)

pindahkanSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 25).split(semicolon, splitIndex)

lokasiSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 26).split('\\n', splitIndex)

lockSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 27).split(semicolon, splitIndex)

catatanStamping = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 28).split(semicolon, splitIndex)

'Klik tombol menu manual sign'
WebUI.click(findTestObject('ManualSign/ManualSign'))

WebUI.delay(1)

'Pengecekan apakah masuk page manual sign'
if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_ManualSign'), GlobalVariable.TimeOut)) {
    'Input form yang ada pada page'
    inputForm()

    'jika setting menggunakan e-meterai'
    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
        'index meningkat karena tambahan 2 kolom ketika menggunakan e-meterai'
        index = 10

        'modify label daftar penanda tangan dengan naiknya index'
        modifyObjectLblDaftarPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/lbl_daftarpenandatangan'), 
            'xpath', 'equals', ('//*[@id="msxForm"]/div[' + index) + ']/div[3]/table/tr/td/small', true)

        'modify button tambah penanda tangan dengan naiknya index'
        modifyObjectbuttonTambahPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/button_tambahTandaTangan'), 
            'xpath', 'equals', ('//*[@id="msxForm"]/div[' + index) + ']/div[2]/a', true)
    } else {
        'Memasukkan object kepada variable'
        modifyObjectLblDaftarPenandaTangan = findTestObject('ManualSign/lbl_daftarpenandatangan')

        'Memasukkan object kepada variable'
        modifyObjectbuttonTambahPenandaTangan = findTestObject('ManualSign/button_tambahTandaTangan')
    }
    
    'check element present kepada daftar penanda tangan yang kosong'
    WebUI.verifyElementPresent(modifyObjectLblDaftarPenandaTangan, GlobalVariable.TimeOut)

	if (phonePenandaTangan.toString() == '[]') {
		looping = emailPenandaTangan.size()
	}
	else {
		looping = phonePenandaTangan.size()
	}
	
    'looping berdasarkan email penanda tangan'
    for (int i = 0; i < looping; i++) {
        'jika email atau nama penanda tangan tidak kosong'
		if (((emailPenandaTangan[i]) != '' || phonePenandaTangan[i] != '')) {
			'klik tambah penanda tangan'
			WebUI.click(modifyObjectbuttonTambahPenandaTangan)
		} else if (((emailPenandaTangan[i]) == '' || phonePenandaTangan[i] == '')) {
			'jika nama dan email penanda tangan kosong'
			break
		}
        
        'jika label tambah penanda tangan muncul'
        if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_TambahPenandaTangan'), GlobalVariable.TimeOut) == 
        true) {
					if (emailPenandaTangan[i] != '') {
						'set text pada email penanda tangan'
						WebUI.setText(findTestObject('ManualSign/input_emailPenandaTangan'), emailPenandaTangan[i])
					}
					
					if (WebUI.verifyElementPresent(findTestObject('ManualSign/input_phonePenandaTangan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						if (phonePenandaTangan[i] != '') {
						'set text pada email penanda tangan'
						WebUI.setText(findTestObject('ManualSign/input_phonePenandaTangan'), phonePenandaTangan[i])
						
						'klik search penanda tangan'
						WebUI.click(findTestObject('ManualSign/button_searchPenandaTanganViaPhone'))
					} else {
						'klik search penanda tangan'
						WebUI.click(findTestObject('ManualSign/button_searchPenandaTanganViaEmail'))
						}
					} else {
						'klik search penanda tangan'
						WebUI.click(findTestObject('ManualSign/button_searchPenandaTanganViaPhone'))
					}
            'diberikan delay 10 detik dengan loading search'
            WebUI.delay(10)

            'jika muncul error log penanda tangan'
            if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog_PenandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'get text error log'
                error = WebUI.getText(findTestObject('ManualSign/errorLog_PenandaTangan'))

                'write excel mengenai error log tersebut'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + '<') + error) + 
                    '>')

                'diberikan delay 2 detik agar sudah ter write sebelum button cancel'
                WebUI.delay(2)

                'klik button cancel'
                WebUI.click(findTestObject('ManualSign/button_Cancel'))

                'delay 2 detik agar cancelnya benar-benar diclick'
                WebUI.delay(2)
            }
            
					if (emailPenandaTangan[i] != '') {
						'verifikasi signer ketika sudah submit signer'
						String verifikasiSigner = CustomKeywords.'connection.ManualSign.getVerificationSigner'(conneSign, emailPenandaTangan[i].toString().toUpperCase())
						
						'check data mengenai email penanda tangan'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_emailPenandaTangan'),
						'value'), emailPenandaTangan[i].toString().toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Penanda Tangan ')
						
						'check data nama dari UI dengan db'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_namaPenandaTangan'),
									'value'), verifikasiSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' pada nama Penanda Tangan ')
						
					} else if (phonePenandaTangan[i] != '') {
						'verifikasi signer ketika sudah submit signer'
						String verifikasiSigner = CustomKeywords.'connection.ManualSign.getVerificationSigner'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(phonePenandaTangan[i]))
						
						'check data mengenai email penanda tangan'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_phonePenandaTangan'),
						'value'), phonePenandaTangan[i], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada phone Penanda Tangan ')
	 
						'check data nama dari UI dengan db'
						checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_namaPenandaTangan'),
									'value'), verifikasiSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' pada nama Penanda Tangan ')
						
					}
					
            if ((editNamaAfterSearch[i]) == 'Yes') {
                'set text pada email penanda tangan'
                WebUI.setText(findTestObject('ManualSign/input_namaPenandaTangan'), namaPenandaTangan[i])
            }
        }
        
        'diberikan delay 3 detik untuk'
        WebUI.delay(2)

        'check save ada attribute disabled'
        if (WebUI.verifyElementHasAttribute(findTestObject('ManualSign/button_Save'), 'disabled', GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'write to excel save gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)

            break
        } else {
            'klik button save'
            WebUI.click(findTestObject('ManualSign/button_Save'))
        }
        
        for (int p = 0; p < looping; p++) {
            'Inisialisasi array dan index yang dibutuhkan'
            arrayIndex = 0

            arrayIndexValue = 0

            indexObject = 1

            'gmofidy modifyObjectInformasiPenandaTangan'
            modifyObjectInformasiPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/modifyObject'), 'xpath', 
                'equals', ((('//*[@id="msxForm"]/div[' + index) + ']/div[3]/table/tr[') + p + 1) + ']/td/p', true)

            'mengambil informasi signer '
            valueInformasi = WebUI.getText(modifyObjectInformasiPenandaTangan).replace('\n', ', ')

            'split informasi signer berdasarkan delimiter'
            valueInformasi = valueInformasi.split(', ', -1)

			if (emailPenandaTangan[i] != '') {
				'query check informasi dari user tersebut'
				queryCheckInformationUser = CustomKeywords.'connection.ManualSign.getInformationUser'(conneSign, emailPenandaTangan[indexEmail++].toString().toUpperCase(), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 8))
			} else {
				'query check informasi dari user tersebut'
				queryCheckInformationUser = CustomKeywords.'connection.ManualSign.getInformationUser'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(phonePenandaTangan[indexEmail++]), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 8))
			}

   				if (valueInformasi[2] == emailPenandaTangan[indexEmail - 1] || valueInformasi[1] == phonePenandaTangan[indexEmail - 1]) {
					'check ui dan query mengenai nama signer'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(valueInformasi[arrayIndexValue++], queryCheckInformationUser[arrayIndex++], 
                    false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama penanda tangan ')

					'check ui dan query mengenai nomor telepon signer'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(valueInformasi[
                        arrayIndexValue++]), queryCheckInformationUser[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
					' pada informasi nomor telepon penanda tangan ')

					'check ui dan query mengenai email signer'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(valueInformasi[arrayIndexValue++], queryCheckInformationUser[arrayIndex++].toString().toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi email penanda tangan ')

                'add nama tanda tangan yang sukses dan nomor telepon'
                namaTandaTangan.add(valueInformasi[0])

                notelpTandaTangan.add(valueInformasi[1])

                break
            } else {
				indexEmail--
			}
        }
    }
    
    'jika button next ada disabled'
    if (WebUI.verifyElementHasAttribute(findTestObject('ManualSign/button_Next'), 'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'write to excel bahwa save gagal'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
    } else {
        if (WebUI.verifyElementPresent(modifyObjectLblDaftarPenandaTangan, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'write to excel bahwa save gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + '<') + 'Silahkan tambah penanda tangan terlebih dulu!') + 
                '>')
        } else {
            WebUI.click(findTestObject('ManualSign/button_Next'))
        }
    }
    
    index = 0

    countValue = 0

    'looping berdasarkan total tanda tangan'
    for (int j = 1; j <= tipeTandaTangan.size(); j++) {
        if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
            'Klik button tanda tangan'
            WebUI.click(findTestObject('Object Repository/ManualSign/btn_ttd'))
        } else if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
            'Klik button tanda tangan'
            WebUI.click(findTestObject('Object Repository/ManualSign/btn_meterai'))
        }
        
        WebUI.delay(3)

        if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
            'Verify label tanda tangannya muncul atau tidak'
            WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)

            'Klik set tanda tangan'
            WebUI.click(findTestObject('ManualSign/ddl_TipeTandaTangan'))

            if (countValue == Integer.parseInt(totalTandaTangan[index])) {
                index++

                countValue = 0
            }
            
            'Memilih tipe signer apa berdasarkan excel'
            WebUI.selectOptionByLabel(findTestObject('ManualSign/ddl_TipeTandaTangan'), ((namaTandaTangan[index]) + ' - ') + 
                (notelpTandaTangan[index]), false)

            countValue++
			
			'Klik set tanda tangan'
			WebUI.click(findTestObject('ManualSign/btn_setTandaTangan'))
			
			'modify label tipe tanda tangan di kotak'
			modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/ManualSign/modifyObject'),
				'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
				j) + ']/div/div/small', true)
	
			'Verifikasi antara excel dan UI, apakah tipenya sama'
			WebUI.verifyMatch(namaTandaTangan[index], WebUI.getText(modifyobjectTTDlblRoleTandaTangan), false)
        }
        
        'Verify apakah tanda tangannya ada'
        if (WebUI.verifyElementPresent(modifyobjectTTDlblRoleTandaTangan, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
            'check if signbox mau dipindahkan'
            if ((pindahkanSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                'memindahkan sign box'
                CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                    ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    j) + ']/div', lokasiSignBox[(j - 1)])
            }
            
            'check if signbox mau dilock posisinya'
            if ((lockSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                'modify obejct lock signbox'
                modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    j) + ']/div/button[1]/span', true)

                'click lock signbox'
                WebUI.click(modifyobjectLockSignBox)

                if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
                    WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_isiCatatanStamping'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)

                    WebUI.setText(findTestObject('ManualSign/input_isiCatatanStamping'), catatanStamping[indexForCatatanStamp++])

                    WebUI.click(findTestObject('ManualSign/button_batalCatatanStamping'))

                    'click lock signbox'
                    WebUI.click(modifyobjectLockSignBox)

                    WebUI.delay(2)

                    checkVerifyEqualOrMatch(WebUI.verifyMatch('', WebUI.getText(findTestObject('ManualSign/input_isiCatatanStamping')), 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' pada catatan meterai ')

                    WebUI.setText(findTestObject('ManualSign/input_isiCatatanStamping'), catatanStamping[(indexForCatatanStamp - 
                        1)])

                    WebUI.click(findTestObject('ManualSign/button_SimpanCatatanStamping'))
                }
            }
        }
    }
    
    'click button simpan'
    WebUI.click(findTestObject('Object Repository/ManualSign/btn_proses'))

    WebUI.delay(25)

    checkErrorLog()

    if (GlobalVariable.FlagFailed == 0) {
        'write to excel success'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)

        if (GlobalVariable.checkStoreDB == 'Yes') {
            result = CustomKeywords.'connection.ManualSign.getManualSign'(conneSign, findTestData(excelPathManualSigntoSign).getValue(
                    GlobalVariable.NumofColm, 9))

            index = 0

            ArrayList arrayMatch = []

            'verify vendor'
            arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        8), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify ref number'
            arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        9), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify document name'
            arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        10), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify tanggal dokumen'
            arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        11), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify tipe pembayaran'
            arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        12), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            automaticAfterStamping = '0'

            if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
                tipeDokumenPeruri = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 16)

                if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 17) == 'Ya') {
                    automaticAfterStamping = '1'
                }
            } else {
                tipeDokumenPeruri = ''
            }
            
            'verify automatic stamping after sign'
            arrayMatch.add(WebUI.verifyMatch(automaticAfterStamping, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify tipe dokumen peruri'
            arrayMatch.add(WebUI.verifyMatch(tipeDokumenPeruri, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            totalDocument = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 13).split('\\n', 
                -1)

            'verify total dokumen'
            arrayMatch.add(WebUI.verifyMatch(totalDocument.size().toString(), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify manual upload'
            arrayMatch.add(WebUI.verifyMatch('1', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'jika data db tidak sesuai dengan excel'
            if (arrayMatch.contains(false)) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
            }
            
            String docId = CustomKeywords.'connection.DataVerif.getDocId'(conneSign, findTestData(excelPathManualSigntoSign).getValue(
                    GlobalVariable.NumofColm, 9), GlobalVariable.Tenant)

            'Write to excel mengenai Document ID'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5, GlobalVariable.NumofColm - 
                1, docId)

            String isDownloadDocument = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 30)

            String isDeleteDownloadedDocument = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                31)

            String isViewDocument = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 32)

            'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
            int jumlahsignertandatangan = 0

            'Call Test case mengneai Kotak Masuk'
            WebUI.callTestCase(findTestCase('Send Document/KotakMasuk'), [('excelPathFESignDocument') : excelPathManualSigntoSign
                    , ('jumlahsignertandatangan') : jumlahsignertandatangan, ('isDownloadDocument') : isDownloadDocument
                    , ('isDeleteDownloadedDocument') : isDeleteDownloadedDocument, ('isViewDocument') : isViewDocument, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

		'jika error message null, masuk untuk tulis error non-sistem'
		if (errormessage != null) {
			
			if (!(errormessage.contains('Permintaan tanda tangan berhasil dibuat.'))) {
				'Tulis di excel itu adalah error'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2).replace(
						'-', '') + ';') + '<') + errormessage) + '>')
			}
		} else {
			
			'Tulis di excel itu adalah error'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 2).replace(
					'-', '') + ';')) + 'Error tidak berhasil ditangkap'))
		}
		return true
    }
    
    return false
}

def inputForm() {
    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('ManualSign/input_psre'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            8))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualSign/input_psre'), Keys.chord(Keys.ENTER))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('ManualSign/input_referenceNo'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            9))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualSign/input_documentName'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            10))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualSign/input_documentDate'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            11))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualSign/input_jenisPembayaran'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            12))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualSign/input_jenisPembayaran'), Keys.chord(Keys.ENTER))

    'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
    String userDir = System.getProperty('user.dir')

    String filePath = userDir + findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 13)

    'Upload file berdasarkan filePath yang telah dirancang'
    WebUI.uploadFile(findTestObject('ManualSign/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)

    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
        index = 10

        if (WebUI.verifyElementNotChecked(findTestObject('ManualSign/input_isE-Meterai'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Input AKtif pada input Status'
            WebUI.click(findTestObject('ManualSign/btn_E-Meterai'))
        }
        
        'Input Aktif pada input Status'
        WebUI.setText(findTestObject('ManualSign/input_tipeDokumenPeruri'), findTestData(excelPathManualSigntoSign).getValue(
                GlobalVariable.NumofColm, 16))

        'Klik enter'
        WebUI.sendKeys(findTestObject('ManualSign/input_tipeDokumenPeruri'), Keys.chord(Keys.ENTER))

        'Input AKtif pada input Status'
        WebUI.setText(findTestObject('ManualSign/input_isAutomatedStamp'), findTestData(excelPathManualSigntoSign).getValue(
                GlobalVariable.NumofColm, 17))

        'Klik enter'
        WebUI.sendKeys(findTestObject('ManualSign/input_isAutomatedStamp'), Keys.chord(Keys.ENTER))
    } else if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 14) == 'No') {
        if (WebUI.verifyElementChecked(findTestObject('ManualSign/input_isE-Meterai'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Input AKtif pada input Status'
            WebUI.click(findTestObject('ManualSign/btn_E-Meterai'))
        }
    }
}