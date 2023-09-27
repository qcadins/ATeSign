import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Inisialisasi flag break untuk sequential'
int flagBreak = 0, isLocalhost = 0, useBiom = 0, alreadyVerif = 0

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'Inisialisasi array untuk Listotp, arraylist arraymatch'
ArrayList listOTP = [], arrayMatch = []

'inisialisasi count resend dan saldo terpakai'
int countResend, countSaldoSplitLiveFCused

'declare arrayindex'
arrayIndex = 0

sheet = 'Send to Sign'

'looping untuk sending document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathFESignDocument).columnNumbers ; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    }else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'Call API Send doc'
        WebUI.callTestCase(findTestCase('Beranda/ResponseAPISendDoc'), [('API_Excel_Path') : excelPathFESignDocument, ('sheet') : sheet,
			('CancelDocsSend') : findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Send?'))], 
            FailureHandling.CONTINUE_ON_FAILURE)

		'jika ada proses cancel doc'
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Send?')) == 'Yes') {

			'lanjutkan loop'
			continue
		}
		
        'Jika tidak ada dokumen id di excel'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('docid')) == '') {
            'loop selanjutnya'
            continue
        }
        
        'Jika document tersebut tidak membutuhkan tanda tangan'
        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need Sign?')) == 'No') {
			'loop selanjutnya'
            continue
        }
		
        'ambil db checking ke UI Beranda'
        ArrayList sendToSign = CustomKeywords.'connection.SendSign.getDataSendtoSign'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('docid')))

        'Mengambil email berdasarkan documentId'
        ArrayList emailSigner = CustomKeywords.'connection.SendSign.getEmailLogin'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('docid'))).split(';', -1)
				
		'list data saldo yang perlu diambil'
		ArrayList saldoList = ['Liveness', 'Face Compare', 'Liveness Face Compare', 'OTP']
		
		'ambil kondisi default face compare'
		String mustFaceCompDB = CustomKeywords.'connection.DataVerif.getMustLivenessFaceCompare'(conneSign, GlobalVariable.Tenant)
		
		'ambil kondisi max liveness harian'
		int maxFaceCompDB = Integer.parseInt(CustomKeywords.'connection.DataVerif.getLimitLivenessDaily'(conneSign))
		
		'ambil nama vendor dari DB'
		String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"',''))
				
		'ambil metode verifikasi dari excel'
		String verifMethod = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CaraVerifikasi(Biometric/OTP)'))
			
		'declare saldo used untuk document pertama yaitu 0'
		int saldoUsedDocPertama = 0

        'declare jumlah signer tanda tangan'
        int jumlahSignerTandaTangan = CustomKeywords.'connection.SendSign.getTotalSigned'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('docid')))
		
        'looping email signer'
        for (int o = 1; o <= emailSigner.size(); o++) {
			
			'dapatkan count untuk limit harian facecompare akun tersebut'
			int countLivenessFaceComp = CustomKeywords.'connection.DataVerif.getCountFaceCompDaily'(conneSign, emailSigner[o-1])
			
            'Inisialisasi variable yang dibutuhkan'
            String noKontrak, saldoSignBefore, saldoSignAfter, otpAfter, documentTemplateName, noTelpSigner
			
            'Mengkosongkan nomor kontrak'
            noKontrak = ''

            'Mengkosongkan document Template Name'
            documentTemplateName = ''

            'Inisialisasi variable total document yang akan disign, count untuk resend, dan saldo yang akan digunakan'
            int totalDocSign, saldoUsed = 0

			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
				('Path') : excelPathFESignDocument, ('Email') : 'Email Login', ('Password') : 'Password Login',
				 ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.CONTINUE_ON_FAILURE)
			
            'mengambil saldo before'
            saldoSignBefore = checkSaldoSign(conneSign, vendor)

			'ambil saldo before'
			HashMap<String, String> saldoBefore = checkSaldo(saldoList, vendor)

            'tutup browsernya'
            WebUI.closeBrowser()

			'ubah flag untuk buka localhost jika syarat if terpenuhi'
			if (!vendor.equalsIgnoreCase('Privy') && mustFaceCompDB == '1') {
				
				'ubah keperluan untuk pakai Localhost'
				isLocalhost = 1
			}
			
            'call Test Case untuk login sebagai user berdasarkan doc id'
            WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [('email') : emailSigner[(o - 1)], ('isLocalhost') : isLocalhost], FailureHandling.CONTINUE_ON_FAILURE)
			
			String roleInput = CustomKeywords.'connection.SendSign.getRoleLogin'(conneSign, emailSigner[o - 1], GlobalVariable.Tenant)
		
			if (checkPopup() == true) {
				break
			}
			
            'Klik checkbox ttd untuk semua'
            WebUI.click(findTestObject('KotakMasuk/Sign/checkbox_ttdsemua'))

            'Klik button ttd bulk'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_ttdbulk'))

			if (checkPopupWarning() == false) {
				'klik tombol Batal'
				WebUI.click(findTestObject('KotakMasuk/Sign/btn_Batal'))
			}

            'refresh buat reset nav bar selanjutnya'
            WebUI.refresh()

			WebUI.delay(GlobalVariable.TimeOut)
			
            'Jika bukan di page 1, verifikasi menggunakan button Lastest. Get row lastest'
            variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

            'get row lastest'
            modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
                variableLastest.size()) + ']/a/i', true)

            'jika btn lastest dapat diclick'
            if (WebUI.verifyElementClickable(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
                'Klik button Lastest'
                WebUI.click(modifyObjectBtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'modify page untuk previous. Ini akan digunakan jika datanya tidak ditemukan'
            modifyObjectBtnPrevious = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/button_Lastest'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
                 2) + ']/a/i', true)

            'Jika ingin dilakukannya bulk sign'
            if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')) == 'Yes') {
                'Ambil data dari excel mengenai total dokumen yang ditandatangani'
                totalDocSign = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Total Doc for Bulk Sign ?')).toInteger()
            } else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')) == 'No') {
                'Total document sign hanya 1 (single)'
                totalDocSign = 1
            }
            
            'Looping berdasarkan page agar bergeser ke page sebelumnya'
            for (int k = 1; k <= (variableLastest.size() - 4); k++) {
                'get row beranda'
                rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

                'looping untuk mengambil seluruh row'
                for (int j = rowBeranda.size(); j >= 1; j--) {
                    'deklarasi arrayIndex untuk pemakaian'
                    arrayIndex = 0

                   'index row distart dari 2 karena yang 1 adalah button ttd'
					indexRow = 2
					
					'modify object text refnum'
					modifyObjectTextRefNumber = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals',
						('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell['+ indexRow++ +']/div', true)
					
					'modify object text document template tipe di beranda'
					modifyObjectTextDocumentTemplateTipe = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell['+ indexRow++ +']/div/p', true)
					
					'modify object text document template name di beranda'
					modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell['+ indexRow++ +']/div/p', true)

					if (roleInput != 'CUST') {
						'modify object text nama customer'
						modifyObjectTextNamaPelanggan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath',
							'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
							j) + ']/datatable-body-row/div[2]/datatable-body-cell['+ indexRow++ +']/div', true)
					}
					
					'modify object lbl tanggal permintaan'
					modifyObjectTextTglPermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell['+ indexRow++ +']/div/span', true)
					
					'modify object btn TTD Dokumen di beranda'
					modifyObjectCheckboxTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/checkbox_ttd'),
						'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

                    'Jika datanya match dengan db, mengenai referal number'
                    if (WebUI.verifyMatch(WebUI.getText(modifyObjectTextRefNumber), sendToSign[arrayIndex++], false, FailureHandling.OPTIONAL) == true) {
                        'Mengenai tipe dokumen template'
                        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentTemplateTipe), sendToSign[
                        arrayIndex++], false, FailureHandling.OPTIONAL), '')

                        'Mengenai tanggal permintaan'
                        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTextTglPermintaan), sendToSign[
                        arrayIndex++], false, FailureHandling.OPTIONAL), '')

                        'Input document Template Name dan nomor kontrak dari UI'
                        documentTemplateName = WebUI.getText(modifyObjectTextDocumentTemplateName)

                        noKontrak = WebUI.getText(modifyObjectTextRefNumber)

                        'Klik checkbox tanda tangan'
                        WebUI.click(modifyObjectCheckboxTtd)

						'Jika total document yang ingin ditandatangani lebih dari satu'
                        if (totalDocSign > 1) {
							'ganti loop'
                            continue
                        	} else {
							'jika hanya 1, maka break'
							break
						}
                    }
                    
					'Jika bulk sign'
                    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Bulk Signing ? (Yes/No)')) == 'Yes') {
						'jika j nya sudah di last row dari document yang ingin diambil'
                        if (j == (rowBeranda.size() - totalDocSign)) {
							'break looping'
                            break
                        } else {
                            'Jika document Template Namenya masih kosong'
                            if (documentTemplateName == '') {
                                'Input document Template Name dan nomor kontrak dari UI'
                                documentTemplateName = WebUI.getText(modifyObjectTextDocumentTemplateName)

                                noKontrak = WebUI.getText(modifyObjectTextRefNumber)

                                'Klik checkbox tanda tangan'
                                WebUI.click(modifyObjectCheckboxTtd)
                            } else {
                                'Input document Template Name dan nomor kontrak dari UI ditambah dengan delimiter ;'
                                documentTemplateName = ((WebUI.getText(modifyObjectTextDocumentTemplateName) + ';') + documentTemplateName)

                                noKontrak = ((WebUI.getText(modifyObjectTextRefNumber) + ';') + noKontrak)

                                'Klik checkbox tanda tangan'
                                WebUI.click(modifyObjectCheckboxTtd)
                            }
                        }
                    }
                }
                
				'Jika masih tidak ditemukan datanya, maka'
				if (documentTemplateName == '') {
					'Klik previous'
					WebUI.click(modifyObjectBtnPrevious)
				} else {
					'Jika sudah ditemukan, maka break'
					break
				}
				
                'Jika sudah dilooping terakhir mengenai pagenya dan tetap tidak menemukan'
                if ((k == (variableLastest.size() - 4)) && (documentTemplateName == '')) {
                    'Input verifynya false dengan reason'
                    checkVerifyEqualorMatch(false, ' dengan alasan tidak ditemukannya Nomor Kontrak yang diinginkan.')

                    continue
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
					((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedTotalDocTidakSesuai) +
					'<' + documentTemplateNamePerDoc.size() + '>') + ' pada User ') + '<' + (emailSigner[(o - 1)]) + '>')
			}
			
			'Looping berdasarkan total document template'
			for (int c = 0; c < documentTemplateNamePerDoc.size(); c++) {
				'modify object btn Nama Dokumen '
				modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'),
					'xpath', 'equals', ('id("ngb-nav-' + (c + 2)) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

				'verify nama dokumen massal dengan nama dokumen di paging'
				if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() -
					(c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
					'Jika tidak cocok, maka custom keywords jika tidak sama.'
					CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
							2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' dimana tidak sesuai di page Bulk Sign antara ') +
						'<' + WebUI.getText(modifyObjectbtnNamaDokumen) + '>') + ' dengan ') + '<' (documentTemplateNamePerDoc[c]) + '>')
				}
			}
			
			'Check konfirmasi tanda tangan'
			checkKonfirmasiTTD()

			'jika page belum pindah ke tahap selanjutnya'
			if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut,
				FailureHandling.OPTIONAL))) {
				'Jika tidak ada, maka datanya tidak ada, atau save gagal'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') +
					GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan page tidak berpindah di Bulk Sign View.')
			} else {
				'Looping berdasarkan document template name yang telah berisi dokumen akan ditandatangani'
				for (int i = 0; i < documentTemplateNamePerDoc.size(); i++) {
					'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
					modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'),
						'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

					'verify nama dokumen dengan nama dokumen di paging'
					checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi),
							documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - (i + 1))], false), '')
				}
			}
			
			WebUI.delay(GlobalVariable.TimeOut)
			
			'Scroll ke btn Proses'
			WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

            'Klik button proses'
            WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))

            'mereset array index'
            arrayIndex = 0

            'check error log'
            if (checkErrorLog() == true) {
                continue
            }
            
            'Jika error log tidak muncul, Jika verifikasi penanda tangan tidak muncul'
            if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiPenandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL))) {
                'Custom keyword mengenai savenya gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedSaveGagal) + ' pada saat tidak muncul pop up Verifikasi Penanda Tangan')
            } else {
                'Jika verifikasi penanda tangan muncul, Verifikasi antara email yang ada di UI dengan db'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_EmailAfterKonfirmasi'), 
                            'value'), emailSigner[(o - 1)], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Signer')

                'Get text nomor telepon'
                noTelpSigner = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_phoneNoAfterKonfirmasi'), 'value')

                'input text password'
                WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('PasswordOTP')))

                'klik buka * pada password'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))

                'verifikasi objek text yang diambil valuenya dengan password'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), 
                'value'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('PasswordOTP')), false), 
                'pada Kata Sandi Signer')
				
				'verifikasi objek text yang diambil valuenya dengan nomor telepon'
				checkVerifyEqualorMatch(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(noTelpSigner), CustomKeywords.'connection.APIFullService.getHashedNo'(conneSign, emailSigner[o-1]), false),
				'pada nomor telepon Signer')
				
				'jika metode verifikasi tidak muncul'
				if (verifMethod.equalsIgnoreCase('Biometric')) {
					
					'cek apakah button biom tidak muncul'
					if (!WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						
						'cek apakah mau ganti method'
						if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')) == 'Yes') {
							
							'panggil fungsi penyelesaian dengan OTP'
							if (verifOTPMethod(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
								
								'jika ada error continue testcase'
								continue
							}
							
							alreadyVerif = 1
						} else {
							
							'tulis excel error dan lanjutkan testcase'
							'jika muncul, tulis error ke excel'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
									2).replace('-', '') + ';') + 'Verifikasi '+ verifMethod + ' tidak tersedia'))
							
							continue
						}
					}
				} else if (verifMethod.equalsIgnoreCase('OTP')) {
					
					'cek apakah button otp tidak muncul'
					if (!WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifOTP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						
						'cek apakah mau ganti method'
						if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Unavailable')) == 'Yes') {
							
							'panggil fungsi verif menggunakan biometrik'
							if(verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
								
								'jika ada error break testcase'
								break
							}
							
							alreadyVerif = 1
						} else {
							
							'tulis excel error dan lanjutkan testcase'
							'jika muncul, tulis error ke excel'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
									2).replace('-', '') + ';') + 'Verifikasi OTP tidak tersedia'))
							
							continue
						}
					}
				}
				
				'jika case privy dan mustliveness aktif serta diatas limit'
				if (vendor.equalsIgnoreCase('Privy') || (mustFaceCompDB == '1' && countLivenessFaceComp >= maxFaceCompDB) && alreadyVerif == 0) {
					
					'pastikan tombol verifikasi biometrik tidak muncul'
					if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/btn_verifBiom'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						GlobalVariable.FlagFailed = 1
						
						'jika muncul, tulis error ke excel'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								2).replace('-', '') + ';') + 'Tombol Liveness muncul saat mustLiveness aktif dan limit sudah terpenuhi'))
					}
					
					'jika tidak sesuai kondisi'
					if (vendor.equalsIgnoreCase('Privy') && verifMethod.equalsIgnoreCase('Biometric')) {
						
						'jika muncul, tulis error ke excel'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								2).replace('-', '') + ';') + 'Privy tidak mensupport verifikasi Biometric'))
						
						continue
					}
					
					'panggil fungsi penyelesaian dengan OTP'
					if (verifOTPMethod(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
						
						'cek apakah ingin coba metode lain'
						if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) == 'Yes') {
							
							'klik tombol untuk kembali ke laman proses'
							WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))
							
							inputDataforVerif()
							
							'panggil fungsi verif menggunakan biometrik'
							if(verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
								
								'jika ada error break testcase'
								break
							}
							
						} else {
							
							'jika ada error continue testcase'
							continue
						}
					}
					
				} else if (mustFaceCompDB == '1' && countLivenessFaceComp < maxFaceCompDB && alreadyVerif == 0) {
					
					'pastikan button otp tidak ada'
					checkVerifyEqualorMatch(WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/btn_verifOTP'),
						GlobalVariable.TimeOut, FailureHandling.OPTIONAL), 'Tombol OTP muncul pada Vendor selain Privy yang mewajibkan FaceCompare')
					
					'panggil fungsi verif menggunakan biometrik'
					if(verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
						
						'cek apakah ingin coba metode lain'
						if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) == 'Yes') {
							
							'klik tombol untuk kembali ke laman proses'
							WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))
							
							inputDataforVerif()
							
							'panggil fungsi penyelesaian dengan OTP'
							if (verifOTPMethod(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
								
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
					if (verifMethod == 'OTP') {
						
						'panggil fungsi penyelesaian dengan OTP'
						if (verifOTPMethod(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
							
							'cek apakah ingin coba metode lain'
							if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) == 'Yes') {
								
								'klik tombol untuk kembali ke laman proses'
								WebUI.click(findTestObject('KotakMasuk/Sign/button_BackOTP'))
								
								inputDataforVerif()
								
								'panggil fungsi verif menggunakan biometrik'
								if(verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
									
									'jika ada error break testcase'
									break
								}
								
							} else {
								
								'jika ada error continue testcase'
								continue
							}
						}
						
					} else {
						
						'panggil fungsi verif menggunakan biometrik'
						if(verifBiomMethod(isLocalhost, maxFaceCompDB, countLivenessFaceComp, conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
							
							'cek apakah ingin coba metode lain'
							if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Force Change Method if other Method Failed?')) == 'Yes') {
								
								'klik tombol untuk kembali ke laman proses'
								WebUI.click(findTestObject('KotakMasuk/Sign/button_KembaliBiom'))
								
								inputDataforVerif()
								
								'panggil fungsi penyelesaian dengan OTP'
								if (verifOTPMethod(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
									
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
            }
				
            'Jika label verifikasi mengenai popup berhasil dan meminta masukan ada'
            if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_VerifikasiOTPBerhasildanMasukan'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'Mendapat total success dan failed'
                String countSuccessSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_success'))

                String countFailedSign = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_Failed'))

                'Menarik value count success ke excel'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 81, GlobalVariable.NumofColm - 
                    1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Count Success')) + ';') + '<' + countSuccessSign + '>')
				
				'Menarik value count failed ke excel'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 82, GlobalVariable.NumofColm -
					1, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Count Failed')) + ';') + '<' + countFailedSign + '>')

                'Jika masukan ratingnya tidak kosong'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')) != '') {
                    'modify object starmasukan, jika bintang 1 = 2, jika bintang 2 = 4'
                    modifyObjectstarMasukan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/span_starMasukan'), 
                        'xpath', 'equals', ('//ngb-rating[@id=\'rating\']/span[' + (findTestData(excelPathFESignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('$Rating')).toInteger() * 2)) + ']/span', true)

                    'Klik bintangnya bintang berapa'
                    WebUI.click(modifyObjectstarMasukan)
                }
                
                'Jika komentarnya tidak kosoong'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')) != '') {
                    'Input komentar di rating'
                    WebUI.setText(findTestObject('KotakMasuk/Sign/input_komentarMasukan'), findTestData(excelPathFESignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('comment')))
                }
                
                'Scroll ke btn Kirim'
                WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Kirim'), GlobalVariable.TimeOut)

                'klik button Kirim'
                WebUI.click(findTestObject('KotakMasuk/Sign/btn_Kirim'))

                if (checkErrorLog() == true) {
                    continue
                }
                
                'Verifikasi label pop up ketika masukan telah selesai dikirim'
                if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popupmasukan'), GlobalVariable.TimeOut))) {
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + GlobalVariable.ReasonFailedFeedbackGagal)
                } else {
                    'Klik OK'
                    WebUI.click(findTestObject('/KotakMasuk/Sign/button_OK'))
                }
                
                'Jika masukan ratingnya tidak kosong'
                if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')) != '') {
                    'StoreDB mengenai masukan'
                    masukanStoreDB(conneSign, emailSigner[(o - 1)], arrayMatch)
                }

				'Jika flag failednya 0'
				if (GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
				  1, GlobalVariable.StatusSuccess)
				}

                'Mensplit nomor kontrak yang telah disatukan'
                noKontrakPerDoc = noKontrak.split(';', -1)

                'looping untuk mendapatkan total saldo yang digunakan per nomor kontrak'
                for (i = 0; i < noKontrakPerDoc.size(); i++) {
                    'Mengambil value dari db mengenai tipe pembayran'
                    paymentType = CustomKeywords.'connection.SendSign.getPaymentType'(conneSign, noKontrakPerDoc[i])

					if (i == 0) {
						saldoUsedDocPertama = (saldoUsedDocPertama + CustomKeywords.'connection.SendSign.getSaldoUsedBasedonPaymentType'(
							conneSign, noKontrakPerDoc[i], emailSigner[(o - 1)]))
					}
					
                    'Jika tipe pembayarannya per sign'
                    if (paymentType == 'Per Sign') {
                        'Saldo usednya akan ditambah dengan value db penggunaan saldo'
                        saldoUsed = (saldoUsed + CustomKeywords.'connection.SendSign.getSaldoUsedBasedonPaymentType'(
                            conneSign, noKontrakPerDoc[i], emailSigner[(o - 1)]))
                    } else {
                        saldoUsed = (saldoUsed + 1)
                    }
                }
                
                'Jumlah signer tanda tangan akan ditambah dengan total saldo yang telah digunakan'
                jumlahSignerTandaTangan = (jumlahSignerTandaTangan + saldoUsed)

                'Looping maksimal 100 detik untuk signing proses. Perlu lama dikarenakan walaupun requestnya done(3), tapi dari VIDAnya tidak secepat itu.'
                for (int y = 1; y <= 5; y++) {
                    'Kita berikan delay per 20 detik karena proses signingnya masih dalam status In Progress (1), dan ketika selesai, status tanda tangan akan kembali menjadi 0'
                    WebUI.delay(20)

                    'Jika signing process db untuk signing false, maka'
                    if (signingProcessStoreDB(conneSign, emailSigner[(o - 1)], saldoUsedDocPertama) == false) {
                        'Jika looping waktu delaynya yang terakhir, maka'
                        if (y == 5) {
                            'Failed dengan alasan prosesnya belum selesai'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2) + ';') + GlobalVariable.ReasonFailedProcessNotDone)
                        }
                    } else {
                        'Jika hasil store dbnya true, maka'
                        break
                    }
                }
                
                'Browser ditutup'
                WebUI.closeBrowser()
            } else {
                'Jika popup berhasilnya tidak ada, maka Savenya gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page Berhasil mengirimkan permintaan tanda tangan dokumen.')

                continue
            }
            
			
			'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
			WebUI.callTestCase(findTestCase('Document Monitoring/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathFESignDocument
			 , ('sheet') : sheet, ("nomorKontrak") : noKontrak, ('CancelDocsSign') : findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Sign?'))], FailureHandling.CONTINUE_ON_FAILURE)
			
			'jika ada proses cancel doc'
			if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Sign?')) == 'Yes') {
				
				'lanjutkan loop'
				continue
			}
			
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
				('Path') : excelPathFESignDocument, ('Email') : 'Email Login', ('Password') : 'Password Login',
				 ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.CONTINUE_ON_FAILURE)

            'Split dokumen template name dan nomor kontrak per dokumen berdasarkan delimiter ;'
            documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

            noKontrakPerDoc = noKontrak.split(';', -1)
			
			'beri maks 30 sec mengenai perubahan total sign'
			for (int b = 1; b <= 3; b++) {
				'ambil saldo after'
				HashMap<String, String> saldoAfter = checkSaldo(saldoList, vendor)

				'ambil saldo after'
				saldoSignAfter = checkSaldoSign(conneSign, vendor)
				
				'Jika count saldo sign/ttd diatas (after) sama dengan yang dulu/pertama (before) dikurang jumlah dokumen yang ditandatangani'
				if (WebUI.verifyEqual(Integer.parseInt(saldoSignBefore) - saldoUsed, Integer.parseInt(saldoSignAfter),
					FailureHandling.OPTIONAL)) {
					
					'cek apa pernah menggunakan biometrik'
					if (useBiom == 0) {
						
						'Jika count saldo otp after dengan yang before dikurangi 1 ditambah dengan '
						if(WebUI.verifyEqual(Integer.parseInt(saldoBefore.get('OTP')) - (countResend), Integer.parseInt(saldoAfter.get('OTP')), FailureHandling.OPTIONAL)) {
							
							break
						}
	
					} else if (useBiom == 1){
						
						'cek saldo liveness facecompare dipisah atau tidak'
						String isSplitLivenessFc = CustomKeywords.'connection.APIFullService.getSplitLivenessFaceCompareBill'(conneSign)
						
						'jika saldo liveness digabung dengan facecompare'
						if (isSplitLivenessFc == '0') {
							
							'cek apakah saldo liveness facecompare masih sama'
							if(WebUI.verifyEqual(Integer.parseInt(saldoBefore.get('Liveness Face Compare')) - 1, Integer.parseInt(saldoAfter.get('Liveness Face Compare')), FailureHandling.OPTIONAL)) {
								
								break
							}
						}
						else if (isSplitLivenessFc == '1') {
							
							'cek apakah saldo liveness dan facecompare sama'
							if(WebUI.verifyEqual(Integer.parseInt(saldoBefore.get('Liveness')) - (countSaldoSplitLiveFCused), Integer.parseInt(saldoAfter.get('Liveness')), FailureHandling.OPTIONAL) &&
								WebUI.verifyEqual(Integer.parseInt(saldoBefore.get('Face Compare')) - (countSaldoSplitLiveFCused), Integer.parseInt(saldoAfter.get('Face Compare')), FailureHandling.OPTIONAL)) {
								
								break
							}
						}
					}
				}

				'Masih sama, dikasi waktu delay 10'
				WebUI.delay(10)

				WebUI.refresh()
			}
			
            'looping berdasarkan total dokumen dari dokumen template code'
            for (int i = 0; i < noKontrakPerDoc.size(); i++) {
                'Input filter di Mutasi Saldo'
                inputFilterTrx(conneSign, currentDate, noKontrakPerDoc[i], documentTemplateNamePerDoc[i])

                'Mengambil value dari db mengenai tipe pembayran'
                paymentType = CustomKeywords.'connection.SendSign.getPaymentType'(conneSign, noKontrakPerDoc[i])

                'Jika tipe pembayarannya per sign'
                if (paymentType == 'Per Sign') {
                    'Memanggil saldo total yang telah digunakan per dokumen tersebut'
                    saldoUsedperDoc = CustomKeywords.'connection.SendSign.getTotalSignedUsingRefNumber'(conneSign, noKontrakPerDoc[
                        i])
                } else {
                    saldoUsedperDoc = o
                }
                
                'delay dari 10 sampe 60 detik'
                for (int d = 1; d <= 6; d++) {
                    'Jika dokumennya ada, maka'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
               			'get column di saldo'
						variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

						'get row di saldo'
						variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

                        'ambil inquiry di db'
                        ArrayList inquiryDB = CustomKeywords.'connection.DataVerif.gettrxSaldo'(conneSign, (noKontrakPerDoc[i]), saldoUsedperDoc.toString())
						
						index = 0
						
						'check total row dengan yang tertandatangan'
						checkVerifyEqualorMatch(WebUI.verifyMatch(variableSaldoRow.size().toString(),
						saldoUsedperDoc.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada jumlah tertanda tangan dengan row transaksi ')
						
						'looping mengenai rownya'
						for (int j = 1; j <= variableSaldoRow.size();j++) {
                        'looping mengenai columnnya'
                        for (int u = 1; u <= (variableSaldoColumn.size()); u++) {
                            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                                'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                                j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', 
                                true)

                            'Jika u di lokasi qty atau kolom ke 9'
                            if (u == 9) {
                                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[(u - 1)]) == '-1')) {
                                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                                    checkVerifyEqualorMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), 
                                            inquiryDB[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                                        (noKontrakPerDoc[i]))
                                } else {
                                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                                    GlobalVariable.FlagFailed = 1

                                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                            2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                                        '<' + (noKontrakPerDoc[i]) + '>')
                                }
                            } else if (u == (variableSaldoColumn.size())) {
                                'Jika di kolom ke 10, atau di FE table saldo, check saldo dari table dengan saldo yang sekarang'
                        //        checkVerifyEqualorMatch(WebUI.verifyEqual(Integer.parseInt(WebUI.getText(modifyperrowpercolumn)), 
                        //                (Integer.parseInt(saldoSignBefore)- saldoUsedperDoc), FailureHandling.CONTINUE_ON_FAILURE), ' pada Saldo di Mutasi Saldo dengan nomor kontrak ' + 
                        //            (noKontrakPerDoc[i]))
                            } else {
                                'Jika bukan untuk 2 kolom itu, maka check ke db'
                                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[
                                        index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' + 
                                    (noKontrakPerDoc[i]))
                            }
                        }
						}
                        break
                    } else {
                        'jika kesempatan yang terakhir'
                        if (d == 6) {
                            'Jika masih tidak ada'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + 
                                '<' + (noKontrakPerDoc[i]) + '>')
                        }
                        
                        'delay 10 detik'
                        WebUI.delay(10)

                        'Klik cari'
                        WebUI.click(findTestObject('Saldo/btn_cari'))
                    }
                }
            }
        }
		
		'check flagBreak untuk sequential'
		if (flagBreak == 1) {
			continue
		}

		'Jika ingin melakukan stamping'
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Do Stamp ?')) == 'Yes') {
			'Call API Send doc'
			WebUI.callTestCase(findTestCase('Meterai/Flow Stamping'), [('excelPathStamping') : excelPathFESignDocument
			, ('sheet') : sheet, ('useAPI') : 'v3.0.0', ('linkDocumentMonitoring') : '', ('CancelDocsStamp') : findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Stamp?'))], FailureHandling.CONTINUE_ON_FAILURE)
			
			'jika ada proses cancel doc'
			if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Stamp?')) == 'Yes') {
				
				'lanjutkan loop'
				continue
			}
		}
	}
}

'penggunaan ini hanya untuk Masukan Store Db'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedStoredDB + ' untuk Masukan Store DB')
}

def verifOTPMethod(Connection conneSign, ArrayList emailSigner, ArrayList listOTP, int o, String noTelpSigner, ArrayList otpAfter) {
	
	'Klik verifikasi by OTP'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifOTP'))

	'Memindahkan variable ke findTestObject'
	modifyObjectlabelRequestOTP = findTestObject('KotakMasuk/Sign/lbl_RequestOTP')

	'Jika button menyetujuinya yes'
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Menyetujui(Yes/No)')) == 'Yes') {
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
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
				2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak bisa lanjut proses OTP')

		'kembali ke loop atas'
		return false
	}
	
	'check error log'
	if (checkErrorLog() == true) {
		return false
	}
	
	'Jika tidak muncul untuk element selanjutnya'
	if (!(WebUI.verifyElementPresent(modifyObjectlabelRequestOTP, GlobalVariable.TimeOut))) {
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
				2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' dengan alasan tidak muncul page input OTP')
	} else {
		
		if (verifOTPMethodDetail(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
			
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

def inputDataforVerif() {
	
	'Scroll ke btn Proses'
	WebUI.scrollToElement(findTestObject('KotakMasuk/Sign/btn_Proses'), GlobalVariable.TimeOut)

	'Klik button proses'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_Proses'))
	
	'input text password'
	WebUI.setText(findTestObject('KotakMasuk/Sign/input_KataSandiAfterKonfirmasi'), findTestData(excelPathFESignDocument).getValue(
			GlobalVariable.NumofColm, rowExcel('PasswordOTP')))

	'klik buka * pada password'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_EyePassword'))
}

def verifOTPMethodDetail(Connection conneSign, ArrayList emailSigner, ArrayList listOTP, int o, String noTelpSigner, ArrayList otpAfter) {
	
	'ubah pemakaian biom menjadi false'
	useBiom = 0
	
	'Verifikasi antara no telp yang dinput dengan yang sebelumnya'
	checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('KotakMasuk/Sign/lbl_phoneNo'),
				'value'), noTelpSigner, false), '')

	'OTP yang pertama dimasukkan kedalam 1 var'
	OTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[(o - 1)])

	'clear arraylist sebelumnya'
	listOTP.clear()

	'add otp ke list'
	listOTP.add(OTP)

	'bikin flag untuk dilakukan OTP by db'
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Correct OTP (Yes/No)')) == 'Yes') {
		'value OTP dari db'
		WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
	} else {
		'value OTP dari excel'
		WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
				GlobalVariable.NumofColm, rowExcel('Manual OTP')))
	}
	
	'klik verifikasi OTP'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))

	'Kasih delay 1 detik karena proses OTP akan trigger popup, namun loading. Tidak instan'
	WebUI.delay(1)

	'check pop up'
	if (checkPopup() == true) {
		return false
	}
	
	'Resend OTP'
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP (Yes/No)')) == 'Yes') {
		'Ambil data dari excel mengenai countResend'
		countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CountResendOTP')).toInteger()

		'Looping dari 1 hingga total count resend OTP'
		for (int w = 1; w <= countResend; w++) {
			'berikan waktu delay'
			WebUI.delay(115)

			'Klik resend otp'
			WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))

			'Memberikan delay 5 karena OTP after terlalu cepat'
			WebUI.delay(5)

			'OTP yang kedua'
			otpAfter = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, emailSigner[(o -
				1)])

			'add otp ke list'
			listOTP.add(otpAfter)

			'dicheck OTP pertama dan kedua dan seterusnya'
			if (WebUI.verifyMatch(listOTP[(w - 1)], listOTP[w], false, FailureHandling.CONTINUE_ON_FAILURE)) {
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
						2).replace('-', '') + ';') + GlobalVariable.ReasonFailedOTPError)
			}
			
			'Jika looping telah diterakhir, baru set text'
			if (w == countResend) {
				'value OTP dari db'
				WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), otpAfter, FailureHandling.CONTINUE_ON_FAILURE)

				'klik verifikasi OTP'
				WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
			}
		}
	} else {
		'tidak ada resend, namun menggunakan send otp satu kali'
		countResend = 1
	}
}

def verifBiomMethod(int isLocalhost, int maxFaceCompDB, int countLivenessFaceComp, Connection conneSign, ArrayList emailSigner, ArrayList listOTP, int o, String noTelpSigner, ArrayList otpAfter) {
	useBiom = 1
	
	'Klik biometric object'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_verifBiom'))
	
	'button menyetujui'
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Menyetujui(Yes/No)')) == 'Yes') {
		'Klik button menyetujui untuk menandatangani'
		WebUI.click(findTestObject('KotakMasuk/Sign/btn_MenyetujuiMenandatangani'))
	}
	
	'Klik lanjut after konfirmasi'
	WebUI.click(findTestObject('KotakMasuk/Sign/btn_LanjutAfterKonfirmasi'), FailureHandling.OPTIONAL)
	
	'jika localhost aktif'
	if (isLocalhost == 1 && GlobalVariable.RunWith == 'Mobile') {
		
		'tap allow camera'
		MobileBuiltInKeywords.tapAndHoldAtPosition(878, 1404, 2)
		
		'delay'
		WebUI.delay(GlobalVariable.TimeOut)
	}
	
	'looping hingga count sampai batas maksimal harian'
	while(countLivenessFaceComp != (maxFaceCompDB + 1)) {
		
		'klik untuk ambil foto'
		WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesBiom'))
		
		'jika error muncul'
		if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), 60, FailureHandling.OPTIONAL)) {
			
			'ambil message error'
			String messageError = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'))
			
			if(messageError.equalsIgnoreCase('Percobaan verifikasi wajah sudah melewati batas harian')) {
				
				countSaldoSplitLiveFCused++
				
				'klik tombol OK'
				WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/button_OK'))
				
				'klik tombol lanjut dengan OTP'
				WebUI.click(findTestObject('Object Repository/KotakMasuk/Sign/btn_LanjutdenganOTP'))
				
				'panggil fungsi verifOTP'
				if (verifOTPMethodDetail(conneSign, emailSigner, listOTP, o, noTelpSigner, otpAfter) == false) {
					
					return false
				}
			} else if (messageError.equalsIgnoreCase('Verifikasi user gagal. Foto Diri tidak sesuai.') ||
				messageError.equalsIgnoreCase('Lebih dari satu wajah terdeteksi. Pastikan hanya satu wajah yang terlihat')) {
				
				countSaldoSplitLiveFCused++			
			} 
			
			'ambil message error'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
					2).replace('-', '') + ';') + '<' + messageError + '>')
			
			'klik pada tombol OK'
			WebUI.click(findTestObject('KotakMasuk/Sign/button_OK'))
			
			GlobalVariable.FlagFailed = 1
			
			'ambil terbaru count dari DB'
			countLivenessFaceComp = CustomKeywords.'connection.DataVerif.getCountFaceCompDaily'(conneSign, emailSigner[o-1])
			
		} else {
			
			break
		}
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)
		
        return false
    }
	
    return true
}

def checkKonfirmasiTTD() {
	'delay'
	WebUI.delay(GlobalVariable.TimeOut)
	
    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik tidak untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TidakKonfirmasiTTD'))

    'Klik tanda tangan'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_TTDSemua'))

    'Klik ya untuk konfirmasi ttd'
    WebUI.click(findTestObject('KotakMasuk/Sign/btn_YaKonfirmasiTTD'))
}

def masukanStoreDB(Connection conneSign, String emailSigner, ArrayList arrayMatch) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'MasukanDB mengambil value dari hasil query'
    masukanDB = CustomKeywords.'connection.DataVerif.getFeedbackStoreDB'(conneSign, emailSigner)

    'verify rating'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')), masukanDB[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify komentar'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('comment')), masukanDB[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

def signingProcessStoreDB(Connection conneSign, String emailSigner, int jumlahSignerTandaTangan) {
    'deklarasi arrayIndex untuk penggunakan selanjutnya'
    arrayIndex = 0

    'SigningDB mengambil value dari hasil query'
    signingDB = CustomKeywords.'connection.SendSign.getSigningStatusProcess'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('docid')), emailSigner)

    'looping berdasarkan size dari signingDB'
    for (int t = 1; t <= signingDB.size(); t++) {
        ArrayList arrayMatch = new ArrayList()

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
            
            'dibreak ke looping code'
            break
        }
    }
}

def inputFilterTrx(Connection conneSign, String currentDate, String noKontrak, String documentTemplateName) {
    documentType = CustomKeywords.'connection.SendSign.getDocumentType'(conneSign, noKontrak)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeSaldo')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeTransaksi')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))
}

def checkSaldoSign(Connection conneSign, String vendor) {
    String totalSaldo
	
    'cek apakah elemen menu ditutup'
	if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
		
		'klik pada button hamburber'
		WebUI.click(findTestObject('button_HamburberSideMenu'))
	}

	'klik button saldo'
	WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
	
	'cek apakah tombol x terlihat'
	if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
		
		'klik pada button X'
		WebUI.click(findTestObject('buttonX_sideMenu'))
	}

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('LabelTipe1')), FailureHandling.OPTIONAL)) {
            'modify object mengenai ambil total jumlah saldo'
            modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c + 1)) + ']/div/div/div/div/div[2]', 
                true)

            'mengambil total saldo yang pertama'
            totalSaldo = WebUI.getText(modifyObjecttotalSaldoSign)

            break
        }
    }
    
    'return total saldo awal'
    return totalSaldo
    
    'tutup browsernya'
    WebUI.closeBrowser()
}

def checkSaldo(ArrayList rowName, String vendor) {
	
	HashMap<String, String> result = new HashMap<>()
    
	for (int b = 0; b < rowName.size(); b++) {
		
		'deklarasi totalSaldo'
		String totalSaldo = ''
		
		'cek apakah elemen menu ditutup'
		if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
			
			'klik pada button hamburber'
			WebUI.click(findTestObject('button_HamburberSideMenu'))
		}
	
		'klik button saldo'
		WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
		
		'cek apakah tombol x terlihat'
		if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
			
			'klik pada button X'
			WebUI.click(findTestObject('buttonX_sideMenu'))
		}
	
		'cek apakah vendor merupakan privy'
		if (vendor.equalsIgnoreCase('Privy') && rowName[b].equals('OTP')) {
		
			'klik ddl untuk tenant memilih mengenai Vida'
			WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor.toUpperCase(), false)	
		} else {
			
			'klik ddl untuk tenant memilih mengenai Vida'
			WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)
		}
	
		'get total div di Saldo'
		variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))
		
		'looping berdasarkan total div yang ada di saldo'
		for (int c = 2; c <= variableDivSaldo.size(); c++) {
			
			'modify object mengenai find tipe saldo'
			modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' +
				(c)) + ']/div/div/div/div/div[1]', true)
	
			'verifikasi label saldonya '
			if (WebUI.verifyElementText(modifyObjectFindSaldoSign, rowName[b], FailureHandling.OPTIONAL)) {
				'modify object mengenai ambil total jumlah saldo'
				modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals',
					('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c)) + ']/div/div/div/div/div[2]',
					true)
	
				'mengambil total saldo yang dipilih'
				totalSaldo = WebUI.getText(modifyObjecttotalSaldoSign)
				
				result.put(rowName[b], totalSaldo)
			}
		}
	}
    
    'return total saldo awal'
    return result
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + lblpopup + '>')

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
    }
    
    return false
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Verifikasi OTP berhasil')) && !(errormessage.contains('feedback'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + errormessage + '>')

            return true
        }
    }
    
    return false
}

def checkPopupWarning() {
	'Jika popup muncul'
	if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'label popup diambil'
		lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

			'Tulis di excel sebagai failed dan error.'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusWarning,
				(((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') +
				'<') + lblpopup) + '>')
			
		'Klik OK untuk popupnya'
		WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
		
		return true
	}
	
	return false
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}