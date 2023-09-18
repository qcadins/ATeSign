import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

ArrayList nomorKontrakPerPilihan = []

String settingHO = ''

'Jika nomor Kontrak kosong'
if (nomorKontrak == '') {
    if (isManualSign == '') {
        'Mengambil nomor kontrak dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 11).replace('"', '')
    } else if (isManualSign == 'Yes') {
        'Mengambil documen id dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 9)
		
		if (nomorKontrak.contains('"')) {
			nomorKontrak = nomorKontrak.replace('"', '')
		}
    }
}

'mengambil hasil split dari totalLoop'
nomorKontrakPerPilihan = nomorKontrak.split(';', -1)

if (isStamping == 'Yes') {
    saldoBefore = loginAdminGetSaldo(conneSign, 'No', sheet)
}

'looping untuk membuka dokumen'
for (int o = 1; o <= 1; o++) {
    if (linkDocumentMonitoring == 'Not Used') {
        'Klik Button menu Document Monitoring'
        WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        linkDocumentMonitoring = ''
    } else if (linkDocumentMonitoring == '') {
//        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathFESignDocument, ('sheet') : sheet],FailureHandling.STOP_ON_FAILURE)
		
		'panggil fungsi login'
		//WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
		//	('Path') : excelPathFESignDocument, ('Email') : 'Email Login', ('Password') : 'Password Login',
//				 ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.CONTINUE_ON_FAILURE)

        'Klik Button menu Document Monitoring'
        WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))
    } else {
        'check if ingin menggunakan embed atau tidakk'
        if (GlobalVariable.RunWithEmbed == 'Yes') {
            settingHO = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 83)

            'navigate url ke daftar akun'
            WebUI.openBrowser(GlobalVariable.embedUrl)

            WebUI.delay(4)

            WebUI.maximizeWindow()

            WebUI.setText(findTestObject('EmbedView/inputLinkEmbed'), linkDocumentMonitoring)

            'click button embed'
            WebUI.click(findTestObject('EmbedView/button_Embed'))

            if (GlobalVariable.RunWithEmbed == 'Yes') {
                'swith to iframe'
                WebUI.switchToFrame(findTestObject('EmbedView/iFrameEsign'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            'Jika error lognya muncul'
            if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + '<') + WebUI.getAttribute(
                        findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label')) + '>')

                break
            }
        } else if (GlobalVariable.RunWithEmbed == 'No') {
            'navigate url ke daftar akun'
            WebUI.openBrowser(linkDocumentMonitoring)

            WebUI.maximizeWindow()
        }
    }
    
    'Looping per document'
    for (int y = 0; y < nomorKontrakPerPilihan.size(); y++) {
        WebUI.delay(7)
		
		'inisialisasi emailSigner menjadi array list'
		ArrayList emailSigner = []
		
        'Mengambil email berdasarkan documentId'
        String emailSignerString = CustomKeywords.'connection.DocumentMonitoring.getEmailSigneronRefNumber'(conneSign, nomorKontrakPerPilihan[y])
		
		'jika email signer string tidak null, maka'
		if (emailSignerString != null) {
			'split email signer dan dimasukkan kepada ArrayList'
			emailSigner = emailSignerString.split(';', -1)
		}
		
		'declare arraylist arraymatch'
		ArrayList arrayMatch = []
		
       inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)
	   
        if (linkDocumentMonitoring == '') {
            modifyObjectvalues = findTestObject('DocumentMonitoring/lbl_Value')

            'Mengambil row size dari value'
            sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

            'Mengambil column size dari value'
            sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

            'Jika valuenya ada'
            if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'Pembuatan untuk array Index result Query'
                arrayIndex = 0

                'Mengambil value dari db menngenai data yang perlu diverif'
                resultQuery = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringBasedOnEmbed'(conneSign, 
                    nomorKontrakPerPilihan[y])

                'Mengambil value dari db mengenai total stamping'
                resultStamping = CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign, 
                    nomorKontrakPerPilihan[y])

                'Looping berdasarkan row yang ada pada value'
                for (int j = 1; j <= sizeRowofLabelValue.size(); j++) {
                    'Looping berdasarkan column yang ada pada value tanpa aksi.'
                    for (int i = 1; i <= ((sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()) - 1); i++) {
                        'modify object label Value'
                        modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 
                            'xpath', 'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

                        'Jika berada di column ke 7'
                        if (i == 7) {
                            'Split teks proses TTD'
                            totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

                            int jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(
                                conneSign, nomorKontrakPerPilihan[y])

                            'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
                            arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahSignerTelahTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
                        } else if (i == 8) {
                            'Jika berada di column ke 8'

                            'Split teks total Stamping'
                            totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

                            'looping berdasarkan total split dan diverif berdasarkan db.'
                            for (int k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
                                'Verifikasi UI dengan db'
                                arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[k], FailureHandling.CONTINUE_ON_FAILURE))
                            }
                        } else if (i == 10) {
                        } else {
                            'Selain di column 7 dan 8 maka akan diverif dengan db.'
                            arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    }
                }
            } else {
                'Jika tidak ada, maka datanya tidak ada di UI.'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

                GlobalVariable.FlagFailed = 1
            }
        } else {
            WebUI.delay(2)

            'modify object label Value'
            modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 'equals', 
                '/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', 
                true)

            'Jika valuenya ada'
            if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                WebUI.delay(5)

                'Mengambil row size dari value'
                sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

                'Mengambil column size dari value'
                sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-body-cell'))

                'Pembuatan untuk array Index result Query'
                arrayIndex = 0

                'Mengambil value dari db menngenai data yang perlu diverif'
                resultQuery = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringBasedOnEmbed'(conneSign, 
                    nomorKontrakPerPilihan[y])

                'Mengambil value dari db mengenai total stamping'
                resultStamping = CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign, 
                    nomorKontrakPerPilihan[y])

                'Looping berdasarkan row yang ada pada value'
                for (int j = 1; j <= sizeRowofLabelValue.size(); j++) {
                    'Looping berdasarkan column yang ada pada value tanpa aksi.'
                    for (int i = 1; i <= (sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()); i++) {
                        modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 
                            'xpath', 'equals', ((('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                            j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

                        'Jika berada di column ke 7'
                        if (i == 7) {
                            'Split teks proses TTD'
                            totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

                            int jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(
                                conneSign, nomorKontrakPerPilihan[y])

                            'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
                            arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahSignerTelahTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
                        } else if (i == 8) {
                            'Jika berada di column ke 8'

                            'Split teks total Stamping'
                            totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

                            'looping berdasarkan total split dan diverif berdasarkan db.'
                            for (int k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
                                'Verifikasi UI dengan db'
                                arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[k], FailureHandling.CONTINUE_ON_FAILURE))
                            }
                        } else if (i == 10) {
                        } else {
                            'Selain di column 7 dan 8 maka akan diverif dengan db.'
                            arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    }
                }
            } else {
                'Jika tidak ada, maka datanya tidak ada di UI.'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                    GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

                GlobalVariable.FlagFailed = 1
            }
        }
        
        'penggunaan checking print false'
        if (arrayMatch.contains(false)) {
            GlobalVariable.FlagFailed = 1

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                GlobalVariable.ReasonFailedStoredDB) + ' pada menu Document Monitoring ')
        }
        
		if (CancelDocsSend == 'Yes' || CancelDocsSign == 'Yes') {
			
			'panggil fungsi cancel docs sesudah send'
			cancelDoc(conneSign, nomorKontrakPerPilihan[y].toString())
			break
		}
		
        if (isStamping == 'Yes') {
            'click button start stamping'
            WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))

            'Jika start stamping'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'klik cancel'
                WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_cancelStartStamping'))

                'click button start stamping'
                WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))

                'klik yes'
                WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_yesStartStamping'))

                'diberikan delay 10 dengan loading'
                WebUI.delay(10)

                'jika ada error log'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'ambil teks errormessage'
                    errormessage = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                    'Tulis di excel itu adalah error'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + '<') + errormessage) + '>')
                }
                
                'jika start stamping muncul'
                if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'klik start ok untuk start stamping'
                    WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_OKStartStamping'))

                    'looping dari 1 hingga 12'
                    for (i = 1; i <= 12; i++) {
                        'mengambil value db proses meterai'
                        int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakPerPilihan[
                            y])

                        'jika proses materai gagal (51)'
                        if (prosesMaterai == 51) {
                            'Kasih delay untuk mendapatkan update db untuk error stamping'
                            WebUI.delay(3)

                            'get reason gailed error message untuk stamping'
                            errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakPerPilihan[
                                y])

                            'Write To Excel GlobalVariable.StatusFailed and errormessage'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (GlobalVariable.ReasonFailedProsesStamping + ' dengan alasan ') + 
                                errorMessageDB.toString())

                            GlobalVariable.FlagFailed = 1

                            break
                        } else if (prosesMaterai == 53) {
                            'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                            WebUI.delay(3)

                            'Mengambil value total stamping dan total meterai'
                            ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                                conneSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 10))

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
                                        2) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                GlobalVariable.FlagFailed = 1
                            }
                            
                            break
                        } else {
                            'Jika bukan 51 dan 51, maka diberikan delay 20 detik'
                            WebUI.delay(10)

                            'Jika looping berada di akhir, tulis error failed proses stamping'
                            if (i == 12) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        2) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + 
                                    (i * 12)) + ' detik ')

                                GlobalVariable.FlagFailed = 1
                            }
                        }
                    }
                }
            }

		    inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)
			
			if (CancelDocsStamp == 'Yes') {
				
				'panggil fungsi cancel docs sesudah send'
				cancelDoc(conneSign, nomorKontrakPerPilihan[y].toString())
				
				break
			}
	
			modifyObjectvalues = findTestObject('DocumentMonitoring/lbl_Value')

			'Mengambil row size dari value'
			sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

			'Mengambil column size dari value'
			sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

			'Jika valuenya ada'
			if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
				'Pembuatan untuk array Index result Query'
				arrayIndex = 0

				'Mengambil value dari db menngenai data yang perlu diverif'
				resultQuery = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringBasedOnEmbed'(conneSign,
					nomorKontrakPerPilihan[y])

				'Mengambil value dari db mengenai total stamping'
				resultStamping = CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign,
					nomorKontrakPerPilihan[y])

				'Looping berdasarkan row yang ada pada value'
				for (int j = 1; j <= sizeRowofLabelValue.size(); j++) {
					'Looping berdasarkan column yang ada pada value tanpa aksi.'
					for (int i = 1; i <= ((sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()) - 1); i++) {
						'modify object label Value'
						modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'),
							'xpath', 'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
							j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

						'Jika berada di column ke 7'
						if (i == 7) {
							'Split teks proses TTD'
							totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

							int jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(
								conneSign, nomorKontrakPerPilihan[y])

							'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
							arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahSignerTelahTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))

							arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
						} else if (i == 8) {
							'Jika berada di column ke 8'

							'Split teks total Stamping'
							totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

							'looping berdasarkan total split dan diverif berdasarkan db.'
							for (int k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
								'Verifikasi UI dengan db'
								arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[k], FailureHandling.CONTINUE_ON_FAILURE))
							}
						} else if (i == 10) {
						} else {
							'Selain di column 7 dan 8 maka akan diverif dengan db.'
							arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++],
									false, FailureHandling.CONTINUE_ON_FAILURE))
						}
					}
				}
			} else {
				'Jika tidak ada, maka datanya tidak ada di UI.'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') +
					GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

				GlobalVariable.FlagFailed = 1
			}
			
            saldoAfter = loginAdminGetSaldo(conneSign, 'Yes', sheet)

			'mengambil value db proses meterai'
			int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakPerPilihan[
				y])

			'jika proses meterai tidak Failed'
			if (prosesMaterai != 51) {
				'jika saldo before sama dengan saldo after'
				if (saldoBefore == saldoAfter) {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                    ' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')
				} else {
					verifySaldoUsed(conneSign, sheet)
				}
			} else {
				if (saldoBefore != saldoAfter) {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                    ' terhadap total saldo dimana saldo awal dan saldo setelah meterai tidak sama ')
				}
			}
        }
    }
}

def cancelDoc(Connection conneSign, String nomorKontrakPerPilihan) {
	
	'jika action yang dilakukan sebelumnya adalah view dokumen'
	if (isStamping == 'Yes') {
		
		'set text no kontrak'
		WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), nomorKontrakPerPilihan)
		
		'click button cari'
		WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
	}
	
	'jika status text adalah complete'
	if (WebUI.getText(findTestObject('DocumentMonitoring/lblTable_status'), FailureHandling.OPTIONAL).equalsIgnoreCase('Complete')) {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) +
			';') + 'Cancel Document tidak bisa karena status Complete')

		GlobalVariable.FlagFailed = 1
		
		return
	}
	
	'klik pada tombol cancel doc'
	WebUI.click(findTestObject('DocumentMonitoring/button_CancelDoc'))
	
	'klik pada tombol batalkan'
	WebUI.click(findTestObject('DocumentMonitoring/button_TidakBatalkan'))
	
	'klik pada tombol cancel doc'
	WebUI.click(findTestObject('DocumentMonitoring/button_CancelDoc'))
	
	'klik pada tombol proses'
	WebUI.click(findTestObject('DocumentMonitoring/button_YaProses'))
	
	'ambil message yang muncul pada popup'
	String popupMsg = WebUI.getText(findTestObject('DocumentMonitoring/PopUpMessage'), FailureHandling.OPTIONAL)
	
	'klik pada tombol OK'
	WebUI.click(findTestObject('DocumentMonitoring/button_OKcancelDoc'))
	
	'jika message yang muncul adalah sukses'
	if (popupMsg.equalsIgnoreCase('Dokumen berhasil dibatalkan')) {
		
		'lakukan pengecekan ke DB'
		checkVerifyEqualorMatch(WebUI.verifyMatch('0', CustomKeywords.'connection.DocumentMonitoring.getCancelDocStatus'(
			conneSign, nomorKontrakPerPilihan),
				false, FailureHandling.CONTINUE_ON_FAILURE), 'Pengecekan ke DB Cancel Doc gagal')
	} else {
		
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) +
			';') + '<' + popupMsg + '>')

		GlobalVariable.FlagFailed = 1
	}
}

def loginAdminGetSaldo(Connection conneSign, String start, String sheet) {
    String totalSaldo

    if (start == 'Yes') {
        'klik button saldo'
        WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))
    }
    
    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, 'Meterai', FailureHandling.OPTIONAL)) {
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
}


def verifySaldoUsed(Connection conneSign, String sheet) {
	'get current date'
	def currentDate = new Date().format('yyyy-MM-dd')

	documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, findTestData(excelPathStamping).getValue(
			GlobalVariable.NumofColm, 11).replace('"', ''))

	documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, findTestData(excelPathStamping).getValue(
			GlobalVariable.NumofColm, 11).replace('"', ''))

	'klik ddl untuk tenant memilih mengenai Vida'
	WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

	'input filter dari saldo'
	WebUI.setText(findTestObject('Saldo/input_tipesaldo'), 'Stamp Duty')

	'enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ARROW_DOWN))

	'enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ENTER))

	'Input tipe transaksi'
	WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use Stamp Duty')

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

	'Input tipe dokumen'
	WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

	'Input enter'
	WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

	'Input referal number'
	WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm,
			11).replace('"', ''))

	'Input documentTemplateName'
	WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentName, FailureHandling.CONTINUE_ON_FAILURE)

	'Input date sekarang'
	WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

	'Klik cari'
	WebUI.click(findTestObject('Saldo/btn_cari'))

	'get column di saldo'
	variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

	'get row di saldo'
	variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

	'ambil inquiry di db'
	ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

	index = 0

	for (int p = 1; p <= variableSaldoRow.size(); p++) {
		'looping mengenai columnnya'
		for (int u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
			'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
			modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath',
				'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				p) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

			'Jika u di lokasi qty atau kolom ke 9'
			if (u == 9) {
				'Jika yang qtynya 1 dan databasenya juga, berhasil'
				if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index]) == '-1')) {
					'Jika bukan untuk 2 kolom itu, maka check ke db'
					checkVerifyEqualorMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index],
							false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' +
						findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

					index++
				} else {
					'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
					GlobalVariable.FlagFailed = 1

					'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
							2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') +
						findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

					index++
				}
			} else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
				'Jika di kolom ke 10, atau di FE table saldo'
			} else {
				'check table'
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false,
						FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + findTestData(excelPathStamping).getValue(
						GlobalVariable.NumofColm, 11).replace('"', ''))

				index++
			}
		}
	}
}

def inputDocumentMonitoring(Connection conneSign, String nomorKontrakPerPilihan, String linkDocumentMonitoring, String settingHO) {
	'Pembuatan untuk array Index result Query'
	arrayIndex = 0

	'Mengambil value db untuk input-input monitoring seperti nomor kontrak, cabang, dan wilayah'
	inputDocumentMonitoring = CustomKeywords.'connection.DocumentMonitoring.getInputDocumentMonitoring'(conneSign, nomorKontrakPerPilihan)
	
	println inputDocumentMonitoring

	'Set text mengenai teks customer'
	WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), inputDocumentMonitoring[arrayIndex++], FailureHandling.OPTIONAL)

	'Set text mengneai input nomor kontrak'
	WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), nomorKontrakPerPilihan)

	'Set text mengenai tanggal permintaan dari'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), inputDocumentMonitoring[arrayIndex++])

	'Set text mengenai tanggal selesai dari'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), inputDocumentMonitoring[(arrayIndex -
		1)])

	'Set text tanggal permintaan sampai'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), inputDocumentMonitoring[arrayIndex++])

	'Set text tanggal selesai sampai'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), inputDocumentMonitoring[(arrayIndex -
		1)])

	'Set text mengenai tipe dokumen'
	WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), inputDocumentMonitoring[arrayIndex++])

	'Enter'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

	'Set text mengenai status dokumen'
	WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), inputDocumentMonitoring[arrayIndex++])

	'Enter'
	WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

	if (linkDocumentMonitoring == '') {
		'Set text mengenai wilayah'
		WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), inputDocumentMonitoring[arrayIndex++], FailureHandling.OPTIONAL)

		'Enter'
		WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)

		'Set text mengenai input cabang'
		WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), inputDocumentMonitoring[arrayIndex++], FailureHandling.OPTIONAL)

		'Enter'
		WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER), FailureHandling.OPTIONAL)
	}
	
	if (WebUI.verifyMatch(settingHO.toString(), '1', true, FailureHandling.OPTIONAL) == true) {
		'Set text mengenai wilayah'
		WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), inputDocumentMonitoring[arrayIndex++])

		'Enter'
		WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

		'Set text mengenai input cabang'
		WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), inputDocumentMonitoring[arrayIndex++])

		'Enter'
		WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))
	}
	
	'Klik enter Cari'
	WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
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