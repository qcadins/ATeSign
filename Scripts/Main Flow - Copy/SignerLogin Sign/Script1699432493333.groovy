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
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Inisialisasi flag break untuk sequential'
int flagBreak = 0

int isLocalhost = 0

int alreadyVerif = 0

int saldoForCheckingDB = 0

int jumlahHarusTandaTangan = 0

int jumlahSignerTandaTangan = 0

int countAutosign = 0

useBiom = 0

'Inisialisasi array untuk Listotp, arraylist arraymatch'
ArrayList listOTP = []

ArrayList arrayMatch = []

ArrayList loopingEmailSigner = []

'declare arrayindex'
arrayIndex = 0

List documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
    -1)

'inisialisasi variable untuk looping. Looping diperlukan untuk break/continue'
forLoopingWithBreakAndContinue = 1

'looping'
for (o = 0; o < forLoopingWithBreakAndContinue; o++) {
    'ambil nomor kontrak dari document id'
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
        [0]])

    'dapatkan count untuk limit harian facecompare akun tersebut'
    int countLivenessFaceComp = CustomKeywords.'connection.DataVerif.getCountFaceCompDaily'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    'saldoUsedDocPertama hanya untuk dokumen pertama'
    int saldoUsedDocPertama = 0

    int saldoUsed = 0

    'Inisialisasi variable yang dibutuhkan, Mengkosongkan nomor kontrak dan document Template Name'
    String noKontrak = ''

    String documentTemplateName = ''

    String noTelpSigner = ''

    String otpAfter

    boolean ifDocFirstChecked = false

    boolean checkingAutoSign = false

    'Inisialisasi variable total document yang akan disign, count untuk resend, dan saldo yang akan digunakan'
    int totalDocSign
	
	'ambil kondisi max liveness harian'
	int maxFaceCompValidationDB = Integer.parseInt(CustomKeywords.'connection.DataVerif.getLimitValidationLivenessDaily'(conneSign))

	'dapatkan count untuk limit harian facecompare akun tersebut'
	int countLivenessValidationFaceComp = CustomKeywords.'connection.DataVerif.getCountValidationFaceCompDaily'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    'ubah flag untuk buka localhost jika syarat if terpenuhi'
    if ((!(vendor.equalsIgnoreCase('Privy')) && !(vendor.equalsIgnoreCase('Digisign'))) && (mustFaceCompDB == '1')) {
        'ubah keperluan untuk pakai Localhost'
        isLocalhost = 1
    }
    
    'check popup'
    if (checkPopup() == true) {
        break
    }
    
    checkBulkSigning()

	checkPopupWarning()
	
    'refresh buat reset nav bar selanjutnya'
    WebUI.refresh()

    WebUI.delay(2)

    'Get row lastest'
    variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'get row lastest'
    modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variableLastest.size()) + ']/a/i', true)

    'jika btn lastest dapat diclick'
    if (WebUI.verifyElementVisible(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
        WebUI.focus(modifyObjectBtnLastest)

        'Klik button Lastest'
        WebUI.click(modifyObjectBtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
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
    
    'jika variable lastestnya tidak ada, maka total pagenya ada 1. Jika ada, maka diminus 4'
    if (variableLastest.size() == 0) {
        totalPage = 1
    } else {
        totalPage = (variableLastest.size() - 4)
    }
    
    'Looping berdasarkan page agar bergeser ke page sebelumnya'
    for (k = 1; k <= totalPage; k++) {
        'get row beranda'
        rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'looping untuk mengambil seluruh row'
        for (j = rowBeranda.size(); j >= 1; j--) {
            'deklarasi arrayIndex untuk pemakaian'
            arrayIndex = 0

            'mengambil seluruh value modify object menuju hashmap'
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

                    'get value countautosign'
                    countAutosign = CustomKeywords.'connection.APIFullService.getTotalAutosignOnDocument'(conneSign, GlobalVariable.storeVar.keySet()[
                        0])

                    'jika vendorny privy'
                    if (vendor.equalsIgnoreCase('Privy')) {
                        'jika count autosignnya lebih dari 0'
                        if (countAutosign > 0) {
                            'get text proses ttd'
                            modifyObjectTextProsesTtd = WebUI.getText(resultObject.get('modifyObjectTextProsesTtd')).split(
                                ' / ', -1)

                            'tingkatkan countautosign + 1'
                            countAutosign++

                            'jika totalnya sudah ingin terjadi autosign'
                            if ((Integer.parseInt(modifyObjectTextProsesTtd[1]) - countAutosign) == Integer.parseInt(modifyObjectTextProsesTtd[
                                0])) {
                                'checking autosignnya true'
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
                if (j == (rowBeranda.size() - totalDocSign)) {
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
            WebUI.click(findTestObject('KotakMasuk/Sign/button_Previous'))
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
            '<') + GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]) + '>')
    }
    
    if (vendor.equalsIgnoreCase('Digisign')) {
        'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
        WebUI.callTestCase(findTestCase('Main Flow - Copy/Signing Digisign'), [('excelPathFESignDocument') : excelPathFESignDocument
                , ('sheet') : sheet, ('nomorKontrak') : noKontrak, ('documentId') : documentId, ('emailSigner') : GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]], FailureHandling.CONTINUE_ON_FAILURE)
		
		saldoUsed = (saldoUsed + noKontrakPerDoc.size())
    } else {
		
		'Looping berdasarkan total document sign'
		for (c = 0; c < documentTemplateNamePerDoc.size(); c++) {
			'modify object btn Nama Dokumen '
			modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath',
				'equals', ('id("ngb-nav-' + (c + 2)) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

			'verify nama dokumen massal dengan nama dokumen di paging'
			if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() -
				(c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
				'Jika tidak cocok, maka custom keywords jika tidak sama.'
				CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(((((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) +
					';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' dimana tidak sesuai di page Bulk Sign antara ') +
					'<') + WebUI.getText(modifyObjectbtnNamaDokumen)) + '>') + ' dengan ') + '<') + (documentTemplateNamePerDoc[
					c])) + '>')
			}
		}
		
		'Check konfirmasi tanda tangan'
		checkKonfirmasiTTD()

		'jika page belum pindah'
		if ((WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL))) {
			'Looping berdasarkan document template name per dokumen'
			for (i = 0; i < documentTemplateNamePerDoc.size(); i++) {
				'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
				modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'),
					'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

				'verify nama dokumen dengan nama dokumen di paging'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), documentTemplateNamePerDoc[
						(documentTemplateNamePerDoc.size() - (i + 1))], false), '')
			}
		} else {
			'Jika tidak ada, maka datanya tidak ada, atau save gagal'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
					'-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan page tidak berpindah di Bulk Sign View.')
		
		}

		'Scroll ke btn Proses'
		WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

		'Klik button proses'
		WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

		'mereset array index'
		arrayIndex = 0

		'check error log'
		if (checkErrorLog() == true) {
			break
		}
		
		'Jika error log tidak muncul, Jika verifikasi penanda tangan tidak muncul'
		if ((WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL))) {
		
		noTelpSigner = checkBeforeChoosingOTPOrBiometric(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], conneSign, vendor)

		'jika metode verifikasi tidak muncul'
		if ((verifMethod[GlobalVariable.indexUsed]).equalsIgnoreCase('Biometric')) {
			'cek apakah button biom tidak muncul'
			if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut,
				FailureHandling.OPTIONAL))) {
				'cek apakah mau ganti method'
				if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')).split(';', -1)[GlobalVariable.indexUsed] == 'Yes') {
					'panggil fungsi penyelesaian dengan OTP'
					if (verifOTPMethod(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])],
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
				if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')).split(';', -1)[GlobalVariable.indexUsed] ==
				'Yes') {
					'panggil fungsi verif menggunakan biometrik'
					if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
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
		if (vendor.equalsIgnoreCase('Privy') || ((mustFaceCompDB == '1' && (countLivenessFaceComp >= maxFaceCompDB) && (countLivenessValidationFaceComp >= maxFaceCompValidationDB)) &&
		alreadyVerif == 0)) {
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
			if (vendor.equalsIgnoreCase('Privy') && verifMethod[GlobalVariable.indexUsed].equalsIgnoreCase('Biometric')) {
				'jika muncul, tulis error ke excel'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('Reason Failed')).replace('-', '') + ';') + 'Privy tidak mensupport verifikasi Biometric')

				continue
			}
			
			'panggil fungsi penyelesaian dengan OTP'
			if (verifOTPMethod(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP,
				noTelpSigner, otpAfter, vendor) == false) {
				'cek apakah ingin coba metode lain'
				if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(
					';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
					'klik tombol untuk kembali ke laman proses'
					WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))

					inputDataforVerif()

					'panggil fungsi verif menggunakan biometrik'
					if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
						'jika ada error break testcase'
						break
					}
				} else {
					'jika ada error continue testcase'
					continue
				}
			}
		} else if (((mustFaceCompDB == '1') && (countLivenessFaceComp < maxFaceCompDB) && (countLivenessValidationFaceComp < maxFaceCompValidationDB)) && (alreadyVerif == 0)) {
			'pastikan button otp tidak ada'
			checkVerifyEqualorMatch(WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/btn_verifOTP'), GlobalVariable.TimeOut,
					FailureHandling.OPTIONAL), 'Tombol OTP muncul pada Vendor selain Privy yang mewajibkan FaceCompare')

			'panggil fungsi verif menggunakan biometrik'
			if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
				'cek apakah ingin coba metode lain'
				if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) ==
				'Yes') {
					'klik tombol untuk kembali ke laman proses'
					WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))

					inputDataforVerif()

					'panggil fungsi penyelesaian dengan OTP'
					if (verifOTPMethod(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])],
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
				if (verifOTPMethod(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP,
					noTelpSigner, otpAfter, vendor) == false) {
					'cek apakah ingin coba metode lain'
					if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) ==
					'Yes') {
						'klik tombol untuk kembali ke laman proses'
						WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))

						inputDataforVerif()

						'panggil fungsi verif menggunakan biometrik'
						if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
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
				if (verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
					'cek apakah ingin coba metode lain'
					if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) ==
					'Yes') {
						'klik tombol untuk kembali ke laman proses'
						WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))

						inputDataforVerif()

						'panggil fungsi penyelesaian dengan OTP'
						if (verifOTPMethod(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], listOTP, noTelpSigner, otpAfter, vendor) == false) {
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
			} else {
					'Custom keyword mengenai savenya gagal'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
						((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
							'-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' pada saat tidak muncul pop up Verifikasi Penanda Tangan')
			}
		
		'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
		if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut,
			FailureHandling.CONTINUE_ON_FAILURE)) {
			inputMasukanAndWriteResultSign()

			if (checkErrorLog() == true) {
				continue
			}
			
			'Verifikasi label pop up ketika masukan telah selesai dikirim'
			if ((WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
				'Klik OK'
				WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
			} else {
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
						'-', '') + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
			}
			
			'Jika masukan ratingnya tidak kosong'
			if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).split(';',
				-1)[GlobalVariable.indexUsed]) != '') {
				'StoreDB mengenai masukan'
				masukanStoreDB(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], arrayMatch)
			}
			
			if (GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') -
					1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			}
			
			'Mensplit nomor kontrak yang telah disatukan'
			noKontrakPerDoc = noKontrak.split(';', -1)

			GlobalVariable.eSignData['NoKontrakProcessed'] = GlobalVariable.eSignData['NoKontrakProcessed'] + noKontrak
			
			'looping untuk mendapatkan total saldo yang digunakan per nomor kontrak'
			for (i = 0; i < noKontrakPerDoc.size(); i++) {
				'jika dia ada autosign'
				if (checkingAutoSign == true) {
					'ambiil signer autosign pada document tersebut'
					loopingEmailSigner = CustomKeywords.'connection.APIFullService.getSignersAutosignOnDocument'(conneSign,
						GlobalVariable.storeVar.keySet()[0])
				}
				
				'input signernya ke loopingEmailSigner pada urutan terakhir'
				loopingEmailSigner.add(0, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

				'looping berdasarkan jumlah email signer'
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

						'mengambil value dari total saldo based on payment type berdasarkan email signer'
						saldoForCheckingDB = CustomKeywords.'connection.APIFullService.getSaldoUsedBasedonPaymentTypeMULTIDOC'(
							conneSign, GlobalVariable.storeVar.keySet()[0], loopingEmailSigner[looping])

						'jika loopingan pertama, maka saldoUsedDocPertama akan menggunakan value saldo For checking db'
						if (i == 0) {
							saldoUsedDocPertama = (saldoUsedDocPertama + saldoForCheckingDB)
						}
						
						'jika dia looping time pertama'
						if (y == 1) {
							'Jika tipe pembayarannya per sign'
							if (paymentType == 'Per Sign') {
								'Saldo usednya akan ditambah dengan value db penggunaan saldo'
								saldoUsed = (saldoUsed + saldoForCheckingDB)
							} else {
								saldoUsed = (saldoUsed + 1)
							}
						}
						
						'jika jumlahsignertanda tangannya bukan 0 dan jumlahnya tidak sesuai dengan seharusnya document tersebut'
						if ((jumlahSignerTandaTangan != 0) && (jumlahSignerTandaTangan != jumlahHarusTandaTangan)) {
							if (jumlahSignerTandaTangan == saldoForCheckingDB) {
								saldoForCheckingDB = jumlahSignerTandaTangan
							} else {
								'menambah saldo for checking db ke jumlah signer tanda tangan'
								saldoForCheckingDB = (saldoForCheckingDB + jumlahSignerTandaTangan)
							}
						} else if ((jumlahSignerTandaTangan != 0) && (jumlahSignerTandaTangan == jumlahHarusTandaTangan)) {
							'jika jumlah signer tanda tangan bukan 0 dan jumlahnya sama dengan signing seharusnya, maka jumlah signer tanda tangan akan masuk ke saldo checking db'
							saldoForCheckingDB = jumlahSignerTandaTangan
						}
						
						'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
						WebUI.delay(20)

						'Jika signing process db untuk signing false, maka'
						if (signingProcessStoreDB(conneSign, loopingEmailSigner[looping], saldoForCheckingDB, GlobalVariable.storeVar.keySet()[
							0]) == false) {
							'jika errortypenya tidak ada value'
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
							ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
								conneSign,  CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[[0]]), 'Signing')
					
							WebUI.comment(officeRegionBline.toString())
							
							'lakukan loop untuk pengecekan data'
							for (int a = 0; a < (officeRegionBline.size() / 2); a++) {
								'verify business line dan office code'
								checkVerifyEqualorMatch(WebUI.verifyMatch((officeRegionBline[a]).toString(), (officeRegionBline[(a +
										3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 'Pada Pengecekan Office dan Business Line')
							}
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
	}
    
    'Split dokumen template name dan nomor kontrak per dokumen berdasarkan delimiter ;'
    documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

    noKontrakPerDoc = noKontrak.split(';', -1)

    GlobalVariable.eSignData['VerifikasiSign'] = saldoUsed

    checkAutoStamp(conneSign, noKontrak, GlobalVariable.saldo)

    'check flagBreak untuk sequential'
    if (flagBreak == 1) {
        continue
    }
} //WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)
//  'Input enter'
//WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))
//  'Input documentTemplateName'
//   WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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
	WebUI.delay(0.25)
    'Jika popup muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
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

def checkKonfirmasiTTD() {
    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik tidak untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TidakKonfirmasiTTD'))

    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik ya untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_YaKonfirmasiTTD'))
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (errormessage == null) {
            return false
        }
        
        if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            return true
        }
    }
    
    false
}

def inputFilterTrx(Connection conneSign, String currentDate, String noKontrak, String signType) {
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

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

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
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'), FailureHandling.OPTIONAL)

		'Klik checkbox ttd untuk semua'
		WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))
		
        return true
    }
    
    return false
}

def checkBulkSigning() {
    'Klik checkbox ttd untuk semua'
    WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

    'Klik button Tanda tangan bulk'
    WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TTDBulk'))

    if (checkPopupWarning() == false) {
        WebUI.focus(findTestObject('KotakMasuk/Sign/btn_Batal'))

        'klik tombol Batal'
        WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))
    }
}

def modifyObject(int j) {
    'index row distart dari 2 karena yang 1 adalah button ttd'
    indexRow = 2

    TestObject modifyObjectTextNamaPelanggan

    'modify object text refnum'
    modifyObjectTextRefNumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals', 
        ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

    'modify object text document template tipe di beranda'
    modifyObjectTextDocumentTemplateTipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
        'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

    'modify object text document template name di beranda'
    modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
        'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

    if (GlobalVariable.roleLogin != 'Customer') {
        'modify object text nama customer'
        modifyObjectTextNamaPelanggan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)
    }
    
    'modify object lbl tanggal permintaan'
    modifyObjectTextTglPermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 
        'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/span', true)

    'modify object lbl proses ttd'
    modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 'equals', 
        ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

    'modify object lbl status ttd'
    modifyObjectTextProsesMeterai = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 
        'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/span', true)

    'modify object lbl status ttd'
    modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 'equals', 
        ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/span', true)

    'modify object btn TTD Dokumen di beranda'
    modifyObjectCheckboxTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

    HashMap result = [:]

    result.put('modifyObjectTextRefNumber', modifyObjectTextRefNumber)

    result.put('modifyObjectTextDocumentTemplateTipe', modifyObjectTextDocumentTemplateTipe)

    result.put('modifyObjectTextDocumentTemplateName', modifyObjectTextDocumentTemplateName)

    result.put('modifyObjectTextNamaPelanggan', modifyObjectTextNamaPelanggan)

    result.put('modifyObjectTextTglPermintaan', modifyObjectTextTglPermintaan)

    result.put('modifyObjectCheckboxTtd', modifyObjectCheckboxTtd)

    result.put('modifyObjectTextStatusTtd', modifyObjectTextStatusTtd)

    result.put('modifyObjectTextProsesTtd', modifyObjectTextProsesTtd)

    result.put('modifyObjectTextProsesMeterai', modifyObjectTextProsesMeterai)

    result
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
            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kata Sandi Signer')

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
    
    noTelpSigner
}

def inputMasukanAndWriteResultSign() {
    'Diberikan delay 4 sec dikarenakan loading'
    WebUI.delay(2)

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

def signingProcessStoreDB(Connection conneSign, String emailSigner, int jumlahSignerTandaTangan, String documentId) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    GlobalVariable.ErrorType = ''

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
            
        }
    }
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
	countResend = 0

    'check ada value maka Setting OTP Active Duration'
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
    0) {
        delayExpiredOTP = (60 * Integer.parseInt(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('Setting OTP Active Duration'))))
    }
    
    'ubah pemakaian biom menjadi false'
    useBiom = 0

    if (CustomKeywords.'connection.DataVerif.getEmailServiceFromTenant'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('Tenant'))) == '1') {
        noTelpSigner = CustomKeywords.'connection.DataVerif.getEmailFromPhone'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                noTelpSigner))
    }
    
    'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'), 'value'), 
            noTelpSigner, false), '')

	GlobalVariable.eSignData['VerifikasiOTP'] = 1

    email = GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]

    if (vendor.equalsIgnoreCase('Privy')) {
        if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) == 
        '1') && email.contains('OUTLOOK.COM')) {
            'call keyword get otp dari email'
            OTP = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(email, findTestData(excelPathFESignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('Password Signer')), 'OTP')
        } else if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) == 
        '0') || !(email.contains('OUTLOOK.COM'))) {
		if (CustomKeywords.'connection.DataVerif.getEmailServiceFromTenant'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0' &&
		CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0' &&
		CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0') {
			if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Getting OTP Value from SMS Via Pushbullet ?')) == 'Yes') {
				OTP = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
			}
			else {
				'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP Privy via SMS.'
				WebUI.delay(50)
	
				OTP = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(
					';', -1)[GlobalVariable.indexUsed])
			}
			}
        }
    } else {
        'OTP yang pertama dimasukkan kedalam 1 var'
        OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)
    }
    
    'clear arraylist sebelumnya'
    listOTP.clear()

    'add otp ke list'
    listOTP.add(OTP)

    if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Correct OTP (Yes/No)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
		if (OTP == '') {
        if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) == 
        '1') && email.contains('OUTLOOK.COM')) {
            'call keyword get otp dari email'
            OTP = CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(email, findTestData(excelPathFESignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('Password Signer')), 'OTP')
        }    else if ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')) == 
        '0') || !(email.contains('OUTLOOK.COM'))) {
		if (CustomKeywords.'connection.DataVerif.getEmailServiceFromTenant'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0' &&
		CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0' &&
		CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('Tenant'))) == '0') {
			if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Getting OTP Value from SMS Via Pushbullet ?')) == 'Yes') {
				WebUI.delay(2)
				
				OTP = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
			}
			}
        }
        }
      
		if (OTP.find(/\d/)) {
			'value OTP dari db / email'
			WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
		} else {
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
					rowExcel('Reason Failed')).replace('-', '') + ';' + OTP.toString())
		}
		
        'check if ingin testing expired otp'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
        0) {
            'delay untuk input expired otp'
            delayExpiredOTP = (delayExpiredOTP + 10)

            WebUI.delay(delayExpiredOTP)
        }
    } else {
		'check if ingin testing expired otp'
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() >
		0) {
			'delay untuk input expired otp'
			delayExpiredOTP = (delayExpiredOTP + 10)

			WebUI.delay(delayExpiredOTP)
		}
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
        countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CountResendOTP')).toInteger()

        'Looping dari 1 hingga total count resend OTP'
        for (int w = 1; w <= countResend; w++) {
            'berikan waktu delay'
            WebUI.delay(298 - delayExpiredOTP)

            'Klik resend otp'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

            for (loopingTimer = 1; loopingTimer <= 5; loopingTimer++) {
                'Memberikan delay 3 karena OTP after terlalu cepat'
                WebUI.delay(loopingTimer * 2)

                'OTP yang kedua'
                otpAfter = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner)

                'add otp ke list'
                listOTP.add(otpAfter)

                'dicheck OTP pertama dan kedua dan seterusnya'
                if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.CONTINUE_ON_FAILURE)) {
                    if (loopingTimer == 5) {
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'OTP Before dan After sama setelah ') + 
                            (loopingTimer * 2)) + ' detik')
                    }
                }
            }
            
            'Jika looping telah diterakhir, baru set text'
            if (w == countResend) {
                'value OTP dari db'
                WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), otpAfter, FailureHandling.CONTINUE_ON_FAILURE)

                'klik verifikasi OTP'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
            }
        }
    }
	
	checkSaldoWAOrSMS(conneSign, vendor)
	
	GlobalVariable.eSignData['VerifikasiOTP'] = GlobalVariable.eSignData['VerifikasiOTP'] + countResend
}

def verifBiomMethod(int isLocalhost, int maxFaceCompDB, int countLivenessFaceComp, Connection conneSign, String emailSigner, ArrayList listOTP, String noTelpSigner, String otpAfter, String vendor) {
    useBiom = 1

	'ambil kondisi max liveness harian'
	maxFaceCompValidationDB = Integer.parseInt(CustomKeywords.'connection.DataVerif.getLimitValidationLivenessDaily'(conneSign))

	'dapatkan count untuk limit harian facecompare akun tersebut'
	countLivenessValidationFaceComp = CustomKeywords.'connection.DataVerif.getCountValidationFaceCompDaily'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])
	
    countSaldoSplitLiveFCused = 0
	
	countValidation = 0

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

    WebUI.delay(GlobalVariable.TimeOut)

    'jika localhost aktif'
    if (GlobalVariable.RunWith == 'Mobile' || isLocalhost == '1') {
        'tap allow camera'
        Mobile.tapAtPosition(882, 1407, FailureHandling.OPTIONAL)

        WebUI.delay(5)
    }
    
    'looping hingga count sampai batas maksimal harian'
    while (countLivenessFaceComp != (maxFaceCompDB + 1) || countLivenessValidationFaceComp != (maxFaceCompValidationDB + 1)) {
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

				if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')).split(';', -1)[GlobalVariable.indexUsed] == 'Yes') {
					'klik tombol lanjut dengan OTP'
					WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_LanjutdenganOTP'))
					
					'panggil fungsi verifOTP'
					if (verifOTPMethodDetail(conneSign, emailSigner, listOTP, noTelpSigner, otpAfter, vendor) == false) {
						return false
					}
				} else {
					'klik tombol Cancel'
					WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_LanjutdenganBatal'))
				}

            } else if (messageError.equalsIgnoreCase('Verifikasi user gagal. Foto Diri tidak sesuai.') || messageError.equalsIgnoreCase(
                'Lebih dari satu wajah terdeteksi. Pastikan hanya satu wajah yang terlihat')) {
                countSaldoSplitLiveFCused++
            } else if (messageError.equalsIgnoreCase('Face detected blurry') || messageError.equalsIgnoreCase('Wajah terdeteksi terlalu jauh dengan kamera. Dekatkan jarak antara wajah Anda dengan kamera')) {
				countValidation++
			}
			
            GlobalVariable.eSignData['VerifikasiBiometric'] = countSaldoSplitLiveFCused

            'ambil message error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + messageError) + '>')

            'klik pada tombol OK'
            WebUI.click(findTestObject('KotakMasuk/Sign/button_OK'))

            GlobalVariable.FlagFailed = 1
            
			'dapatkan count untuk limit harian facecompare akun tersebut'
			countLivenessValidationFaceComp = CustomKeywords.'connection.DataVerif.getCountValidationFaceCompDaily'(conneSign, GlobalVariable.storeVar.getAt(
					GlobalVariable.storeVar.keySet()[0]))
			
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
    'nomor kontrak displit karena get dari atas dalam bentuk full value termasuk bulk sigj'
    noKontrakPerDoc = noKontrak.split(';', -1)

    'inisialisasi flagerrordms'
    int flagErrorDMS = 0

    'inisialisasi saldoafter dan saldobefore dengan resultsaldobefore'
    String saldoAfter

    String saldoBefore = resultSaldoBefore.get('Meterai')

    'looping per kontrak'
    for (loopingPerKontrak = 0; loopingPerKontrak < noKontrakPerDoc.size(); loopingPerKontrak++) {
        'check autostamp'
        automaticStamp = CustomKeywords.'connection.Meterai.getAutomaticStamp'(conneSign, noKontrakPerDoc[loopingPerKontrak])

        'jika autosign'
        if (automaticStamp == '1') {
            'check sign status'
            getSignStatus = CustomKeywords.'connection.SendSign.getSignStatus'(conneSign, noKontrakPerDoc[loopingPerKontrak])

            'jika sudah complete'
            if (getSignStatus == 'Complete') {
                'get proses meterai'

                'mengambil value db proses ttd'
                prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[loopingPerKontrak])

                'jika bukan 0'
                if (prosesMaterai != 0) {
                    'looping dari 1 hingga 12'
                    for (i = 1; i <= 12; i++) {
                        'mengambil value db proses ttd'
                        prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[
                            loopingPerKontrak])

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

                            if ((errorMessageDB.toString().contains('upload DMS'))) {
                                flagErrorDMS = 1

                                continue
                            } else {
								break
                            }
                        } else if (((prosesMaterai == 53) || (prosesMaterai == 63)) || (flagErrorDMS == 1)) {
                            WebUI.delay(3)

                            'get saldo after'
                            resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow - Copy/getSaldo'), [('excel') : excelPathFESignDocument
                                    , ('sheet') : sheet, ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

                            'get saldo after meterai'
                            saldoAfter = resultSaldoAfter.get('Meterai')

                            'Mengambil value total stamping dan total meterai'
                            totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                                conneSign, noKontrakPerDoc[loopingPerKontrak])

                            'declare arraylist arraymatch'
                            arrayMatch = []

                            'dibandingkan total meterai dan total stamp'
                            arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[
                                    1], false, FailureHandling.CONTINUE_ON_FAILURE))

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

                                'check saldo after ada perubahan dengan before'
                                checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore) - Integer.parseInt(
                                            totalMateraiAndTotalStamping[1]), Integer.parseInt(saldoAfter), FailureHandling.CONTINUE_ON_FAILURE), 
                                    ' pada pemotongan saldo Meterai Autostamp')
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
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                    ' dengan jeda waktu ') + (i * 12)) + ' detik ')

                                GlobalVariable.FlagFailed = 1
                            }
                        }
                    }
                    
                    'Jika flag failed tidak 0'
                    if (GlobalVariable.FlagFailed == 0) {
                        if (flagErrorDMS == 1) {
                            'write to excel Failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
                        } else {
                            'write to excel success'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                        }
                        
                        'Mengambil value total stamping dan total meterai'
                        totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, noKontrakPerDoc[loopingPerKontrak])

                        if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63)) {
                            'Call verify meterai'
                            WebUI.callTestCase(findTestCase('Main Flow - Copy/Stamping'), [('excelPathMeterai') : excelPathFESignDocument
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
                    resultSaldoAfter = WebUI.callTestCase(findTestCase('null'), [('excel') : excelPathFESignDocument
                            , ('sheet') : sheet, ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

                    'get saldo after meterai'
                    saldoAfter = resultSaldoAfter.get('Meterai')

                    'check saldo tidak ada perubahan'
                    checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter), 
                            FailureHandling.CONTINUE_ON_FAILURE), ' pada pemotongan saldo Meterai Gagal Autostamp')
                }
            }
        }
    }
}

def checkSaldoWAOrSMS(Connection conneSign, String vendor) {
    ArrayList balmut = []

    int penggunaanSaldo = 0

    String tipeSaldo

    emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

	if (vendor.equalsIgnoreCase('Privy')) {
		mustUseWaFirst = '0'
		
		emailServiceOnVendor = '0'
	}
	
    if (mustUseWAFirst == '1') {
        tipeSaldo = 'WhatsApp Message'

        'menggunakan saldo wa'
        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
			
			GlobalVariable.FlagFailed = 1
        } else {
            penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
        }
    } else {
        if (emailServiceOnVendor == '1') {
            useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

            if (useWAMessage == '1') {
                tipeSaldo = 'WhatsApp Message'

                'menggunakan saldo wa'
				balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                if (balmut.size() == 0) {
                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
					
					GlobalVariable.FlagFailed = 1
                } else {
                    penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                }
            } else if (useWAMessage == '0') {
					'ke sms'
					tipeSaldo = 'OTP'

					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')
						
						GlobalVariable.eSignData[('VerifikasiOTP')] = GlobalVariable.eSignData[('VerifikasiOTP')] - 1
						
						GlobalVariable.FlagFailed = 1
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
					}
				
            }
        } else {
					'ke sms'
					tipeSaldo = 'OTP'

					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')
						
						GlobalVariable.eSignData[('VerifikasiOTP')] = GlobalVariable.eSignData[('VerifikasiOTP')] - 1
					
						GlobalVariable.FlagFailed = 1
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
					}
				}
    }
    
    int pemotonganSaldo = 0

    int increment

	if (penggunaanSaldo > 0) {
    for (looping = 0; looping < penggunaanSaldo; looping++) {
        if (looping == 0) {
            increment = 0
        } else {
            increment = (increment + 10)
        }
        
		pemotonganSaldo = (pemotonganSaldo + Integer.parseInt(balmut[(increment + 9)].replace('-', '')))
		
		GlobalVariable.eSignData[('allTrxNo')] = GlobalVariable.eSignData[('allTrxNo')] + balmut[increment + 0] + ';'
		
		GlobalVariable.eSignData[('allSignType')] = GlobalVariable.eSignData[('allSignType')] + balmut[increment + 3].replace('Use ', '') + ';'
		
		GlobalVariable.eSignData[('emailUsageSign')] = GlobalVariable.eSignData[('emailUsageSign')] + fullNameUser + ';'
    }
	
    if (tipeSaldo == 'WhatsApp Message') {
		GlobalVariable.eSignData[('CountVerifikasiWA')] = GlobalVariable.eSignData[('CountVerifikasiWA')] + pemotonganSaldo
		
		GlobalVariable.eSignData[('VerifikasiOTP')] = GlobalVariable.eSignData[('VerifikasiOTP')] - GlobalVariable.eSignData[('CountVerifikasiWA')]
	    } else if (tipeSaldo == 'SMS Notif') {
      	GlobalVariable.eSignData[('CountVerifikasiSMS')] = GlobalVariable.eSignData[('CountVerifikasiSMS')] + pemotonganSaldo
		
		GlobalVariable.eSignData[('VerifikasiOTP')] = GlobalVariable.eSignData[('VerifikasiOTP')] - GlobalVariable.eSignData[('CountVerifikasiSMS')]
    }
	}
}
