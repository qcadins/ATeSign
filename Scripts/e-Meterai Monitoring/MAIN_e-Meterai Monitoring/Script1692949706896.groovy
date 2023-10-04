import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDate as LocalDate
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get dates'
currentDate = LocalDate.now()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

firstDateOfMonth = currentDate.withDayOfMonth(1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathemeteraiMonitoring).columnNumbers

int firstRun = 0

'looping e meterai monitoring'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

		'get tenant dari excel percase'
		GlobalVariable.Tenant = findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		
		'get psre dari excel percase'
		GlobalVariable.Psre = findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		if(findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) !=
			findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')) || firstRun == 0) {
			'call test case login per case'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathemeteraiMonitoring, ('Email') : 'Email Login', ('Password') : 'Password Login'
				, ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)
			
			'apakah cek paging diperlukan di awal run'
			if(GlobalVariable.checkPaging.equals('Yes')) {
				 'call function check paging'
				 checkPaging(currentDate, firstDateOfMonth, conneSign)
			}
			
			firstRun = 1
		}
		
		'click menu meterai'
		WebUI.click(findTestObject('e-Meterai Monitoring/menu_emeteraiMonitoring'))
		
		if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}
        
        inputPagingAndVerify(conneSign)

        'ambil value db mengenai progress stamping'
        progressStamping = CustomKeywords.'connection.eMeteraiMonitoring.getProseseMeteraiMonitoring'(conneSign, findTestData(
                excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Nomor Dokumen')))
		
        'Jika stampingnya failed'
        if ((progressStamping[0]) == 'Failed') {
            'Jika aksinya View Error message'
            if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'View Error Message') {
                'Klik aksi'
                WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi View Error Message'))

                'Diberi delay 2 sec'
                WebUI.delay(2)

                'verify error message'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/label_ModalBody')), 
                        CustomKeywords.'connection.eMeteraiMonitoring.getErrorMessage'(conneSign, result[0]), false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' Error Message ')

                'Klik button X'
                WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_X'))
            } else if ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'Retry Stamping') || 
            (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'Retry Stamping From Upload')) {
                clickRetryAction()

                'Jika popup muncul'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'Klik button Tidak'
                    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Tidak'))

                    clickRetryAction()

                    'Klik button Ya'
                    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Ya'))

                    'Jika error lognya muncul'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						'ambil teks errormessage'
						errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
						GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
						rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + errormessage + '>')

                        GlobalVariable.FlagFailed = 1

                        break
                    }
					
                    'verify element present popup'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut)) {
                        'label popup diambil'
                        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

                        'jika popup bukan retry stamping'
                        if (!(lblpopup.contains('retry stamping'))) {
                            'Tulis di excel sebagai failed dan error.'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
							GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
							rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + lblpopup + '>')

                            'Flag Failed 1'
                            GlobalVariable.FlagFailed = 1
                        }
                        
                        'Klik OK untuk popupnya'
                        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

                        if (GlobalVariable.checkStoreDB == 'Yes') {
                            'looping per 10 detik hingga 60 detik'
                            for (int i = 1; i <= 6; i++) {
                                'Ambil value db lagi mengenai progress stamping'
                                progressStampingAfter = CustomKeywords.'connection.eMeteraiMonitoring.getProseseMeteraiMonitoring'(
                                    conneSign, findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Nomor Dokumen')))

                                'Jika masih in progress'
                                if ((progressStampingAfter[0]) == 'In Progress') {
                                    'Diberi delay 10 detik'
                                    WebUI.delay(10)

                                    'Jika sudah diloopingan terakhir'
                                    if (i == 6) {
                                        'Tulis di excel sebagai failed performance.'
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusWarning, ((((findTestData(excelPathemeteraiMonitoring).getValue(
                                                GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedPerformance) + 
                                            ' pada retry stamping hingga ') + (i * 10)) + ' detik ')
										
										GlobalVariable.FlagFailed = 1
                                    }
                                } else if ((progressStampingAfter[0]) == 'Failed') {
                                    'Jika retrynya failed, Tulis di excel sebagai failed dan error.'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, ((findTestData(excelPathemeteraiMonitoring).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                        ' pada saat melakukan Retry Stamping ')

                                    GlobalVariable.FlagFailed = 1

                                    break
                                } else if ((progressStampingAfter[0]) == 'Success') {
                                    'Jika sukses, maka verify match progress lama dengan yang baru. Apakah sama'
                                    if (WebUI.verifyMatch(progressStamping[0], progressStampingAfter[0], false, FailureHandling.CONTINUE_ON_FAILURE) == 
                                    true) {
                                        'Tulis di excel sebagai failed dan error.'
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusFailed, ((findTestData(excelPathemeteraiMonitoring).getValue(
                                                GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                            ' pada saat melakukan Retry Stamping ')
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
		
			inputPagingAndVerify(conneSign)
			'check if mau download data meterai'
			if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Need Download Document ?')).equalsIgnoreCase('Yes')) {
					'Klik download file'
					WebUI.click(findTestObject('e-Meterai Monitoring/btn_DownloadDocument'))

				'looping hingga 20 detik'
				for (int i = 1; i <= 4; i++) {
					'pemberian delay download 5 sec'
					WebUI.delay(5)
	
					'check isfiled downloaded'
					if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Delete Downloaded Document'))) == true) {
						'Jika sukses downloadnya lebih dari 10 detik'
						if (i > 2) {
							'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedPerformance'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusWarning, ((((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPerformance) + ' sejumlah ') + (i * 5)) + ' detik ')
	
							GlobalVariable.FlagFailed = 1
						}
						
						break
					}
					else {
						'Jika error lognya muncul'
						if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
							'ambil teks errormessage'
							errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)
	
							'Tulis di excel itu adalah error'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + errormessage + '>')
	
							GlobalVariable.FlagFailed = 1
	
							break
						}
						'Jika sudah loopingan terakhir'
						if (i == 5) {
							'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedDownload)
	
							GlobalVariable.FlagFailed = 1
						}
					}
				}
			}
			
			
			if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Need View Document ?')) == 'Yes') {
				
				WebUI.verifyElementPresent(findTestObject('e-Meterai Monitoring/btn_ViewDocument'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
				
				WebUI.click(findTestObject('e-Meterai Monitoring/btn_ViewDocument'))
	
				'Jika error lognya muncul'
				if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					'ambil teks errormessage'
					errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)
	
						'Tulis di excel itu adalah error'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
							(findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<' + errormessage + '>')
			}
			
				'Pemberian waktu 2 detik karena loading terus menerus'
				WebUI.delay(2)
	
				'verifikasi label dokumen'
				if (WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'), GlobalVariable.TimeOut,
					FailureHandling.CONTINUE_ON_FAILURE)) {
					'Mengambil label pada view Dokoumen'
					labelViewDoc = WebUI.getText(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'))
	
					'Jika pada label terdapat teks No Kontrak'
					if (labelViewDoc.contains('No Kontrak ')) {
						'Direplace dengan kosong agar mendapatkan nomor kontrak'
						labelViewDoc = labelViewDoc.replace('No Kontrak ', '')
					}
					
					'Diverifikasi dengan UI didepan'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Nomor Dokumen')), ' ' + labelViewDoc, false, FailureHandling.CONTINUE_ON_FAILURE),
						' pada nomor kontrak UI yaitu ' + labelViewDoc)

					'Klik kembali'
					WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_backViewDokumen'))
				} else {
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
						((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProcessNotDone) +
						' untuk proses View dokumen tanda tangan. ')
				}
			}
		
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth, Connection conneSign) {
    totaleMeteraiMonitoringDB = CustomKeywords.'connection.eMeteraiMonitoring.getTotaleMeteraiMonitoring'(conneSign, GlobalVariable.Tenant)
	'click menu meterai'
	WebUI.click(findTestObject('e-Meterai Monitoring/menu_emeteraiMonitoring'))
	
    'set text no kontrak'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), '20230616133400')

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), 'Dokumen Transaksi/Payment Receipt')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), firstDateOfMonth.toString())

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), 'Success')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), 'Daegu')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), 'Dokumen penerimaan uang (lebih dari 5 juta)')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), 'KWITANSI EMETERAI')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), currentDate.toString())

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), '')

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), 'Pemungut')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('Meterai/button_Cari'))

    WebUI.delay(2)

    'set text tanggal pengiriman ke'
    totaleMeteraiMonitoringUI = WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/Label_Totale-Meterai')).split(
        ' ', -1)

    checkVerifyPaging(WebUI.verifyMatch(totaleMeteraiMonitoringUI[0], totaleMeteraiMonitoringDB, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify total Meterai'
    if (Integer.parseInt(totaleMeteraiMonitoringUI[0]) > 10) {
		
		WebUI.focus(findTestObject('e-Meterai Monitoring/button_NextPage'))
		
        'click next page'
        WebUI.click(findTestObject('e-Meterai Monitoring/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 2', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('e-Meterai Monitoring/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click last page'
        WebUI.click(findTestObject('e-Meterai Monitoring/button_LastPage'))

		'get total data'
		lastPage = Double.parseDouble(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/Label_Totale-Meterai')).split(' ',-1)[0])/10
		
		'jika hasil perhitungan last page memiliki desimal'
		if (lastPage.toString().contains('.0')) {
			'tidak ada round up'
			additionalRoundUp = 0
		} else {
			'round up dengan tambahan 0.5'
			additionalRoundUp = 0.5
		}
		
        'verify paging di last page'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'aria-label', 
			FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage+additionalRoundUp).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
        
		'click first page'
        WebUI.click(findTestObject('e-Meterai Monitoring/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page 1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def inputPagingAndVerify(Connection conneSign) {
    'Klik set ulang setiap data biar reset'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))

    'set text no kontrak'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Nomor Dokumen')))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Input Tipe Dokumen')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Tanggal Dokumen Mulai')))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Hasil Stamping')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Cabang')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Tipe Dokumen Peruri')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Tanggal Dokumen Sampai')))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Pengaturan Dokumen')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Nomor Seri')))

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Jenis Pajak')))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('e-Meterai Monitoring/button_Cari'))

    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusWarning, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                rowExcel('Reason Failed')).replace('-', '') + ';') + '<' + errormessage + '>')

        GlobalVariable.FlagFailed = 1
    }
    
    'get stampduty data dari db'
    result = CustomKeywords.'connection.eMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, findTestData(excelPathemeteraiMonitoring).getValue(
            GlobalVariable.NumofColm, rowExcel('Nomor Dokumen')))

    index = 0

    'verify no meterai'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NomorDokumen')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Dokumen ')

    'verify no kontrak'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TanggalDokumen')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Dokumen ')

    'verify tanggal pakai'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Cabang')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang ')

    'verify biaya'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NamaDokumen')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dokumen ')

    'verify cabang'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumenPeruri')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen Peruri ')

    'verify wilayah'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumen')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NominalDokumen')).replace(
                ',', ''), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nominal Dokumen ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_PengaturanDokumen')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengaturan Dokumen ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_HasilStamping')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Hasil Stamping ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Nomor Seri')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Seri ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Proses Materai')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Proses Materai ')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_JenisPajak')), 
            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Jenis Pajak ')
}

def clickRetryAction() {
    if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Action')) == 'Retry Stamping') {
        'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
        WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping'))
    } else {
        'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
        WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping From Upload'))
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}