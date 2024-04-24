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
import com.kms.katalon.core.testobject.TestObject as TestObject

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

ArrayList nomorKontrakPerPilihan = []

'Memilih document monitoring menggunakan apa berdasarkan input. Jika embed, maka setting GV Yes, jika tidak, maka setting No'
if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?')) == 
'Embed V2') {
    GlobalVariable.RunWithEmbed = 'Yes'
} else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?')) == 
'Embed V1') {
    GlobalVariable.RunWithEmbed = 'V1'
} else {
    GlobalVariable.RunWithEmbed = 'No'
}

'Jika nomor Kontrak kosong'
if ((nomorKontrak == '') || (nomorKontrak.toString() == 'null')) {
    if (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'API Send Document External') || (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
            'Option for Send Document :')) == 'API Send Document Normal')) || (findTestData(excelPathFESignDocument).getValue(
        GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 'Manual Sign')) {
        'Mengambil nomor kontrak dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))
    } else if (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'Sign Only') || (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'Stamp Only')) || (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'Cancel Only')) {
        documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(
            ', ', -1)

        'Mengambil documen id dari excel'
        nomorKontrak = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])
    }
}

'mengambil hasil split dari totalLoop'
nomorKontrakPerPilihan = nomorKontrak.split(';', -1).toUnique()

if (vendor.toString() == 'null') {
    'ambil nama vendor dari DB'
    vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('documentid')))
}

ArrayList documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
    -1)

'Looping per document'
for (y = 0; y < nomorKontrakPerPilihan.size(); y++) {
    if (linkDocumentMonitoring == 'Not Used') {
        openHamburgAndroid()

        'Fokus ke document monitoring'
        WebUI.focus(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        'Klik Button menu Document Monitoring'
        WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        closeHamburgAndroid()

        linkDocumentMonitoring = ''
    } else if (linkDocumentMonitoring == '') {
        'memilih document yang pertama dan login menggunakan email pertama'
        funcLogin(conneSign, documentId.toString().replace('[', '').replace(']', ''))
    }
    
    'inisialisasi emailSigner menjadi array list'
    ArrayList emailSigner = []

    'Mengambil email berdasarkan documentId'
    String emailSignerString = CustomKeywords.'connection.DocumentMonitoring.getEmailSigneronRefNumber'(conneSign, nomorKontrakPerPilihan[
        y], GlobalVariable.Tenant)

    'jika email signer string tidak null, maka'
    if (emailSignerString != null) {
        'split email signer dan dimasukkan kepada ArrayList'
        emailSigner = emailSignerString.split(';', -1)
    }
    
    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = []

    inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring)

    if (linkDocumentMonitoring == '') {
		arrayMatch = verifyListAdminClient(conneSign, nomorKontrakPerPilihan, documentId, y, emailSigner)
		} else {
		arrayMatch = verifyListEmbed(conneSign, nomorKontrakPerPilihan, documentId, y, emailSigner)
    }
    
    'penggunaan checking print false'
    if (arrayMatch.contains(false)) {
        GlobalVariable.FlagFailed = 1

        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada menu Document Monitoring ')
    }
    
    if ((CancelDocsSend == 'Yes') || (CancelDocsSign == 'Yes')) {
        'panggil fungsi cancel docs sesudah send'
        if (cancelDoc(conneSign, (nomorKontrakPerPilihan[y]).toString()) == true) {
            'input kembali data sesudah selesai cancel doc'
            inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring)

            'lakukan pengecekan ke di UI untuk memastikan cancel berhasil'
            checkVerifyEqualOrMatch(WebUI.verifyMatch('Tidak ada data untuk diperlihatkan', WebUI.getText(findTestObject(
                            'DocumentMonitoring/SearchResult'), FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), 
                'Data gagal terhapus di FE')
        }
        
        break
    }
    
    if ((isStamping == 'Yes') || (retryStamping == 'Yes')) {
        if (isStamping == 'Yes') {
            'click button start stamping'
            WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))
        } else {
            'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
            WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping'))
        }
        
        'Jika start stamping'
        if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)) {
            'klik cancel'
            WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_cancelStartStamping'))

            if (isStamping == 'Yes') {
                'click button start stamping'
                WebUI.click(findTestObject('DocumentMonitoring/button_startStamping'))
            } else {
                'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
                WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping'))
            }
            
            'klik yes'
            WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_yesStartStamping'))

            'diberikan delay 2 dengan loading'
            WebUI.delay(2)

            'jika ada error log'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'ambil teks errormessage'
                errormessage = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + errormessage) + '>')
            }
            
            'jika start stamping muncul'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_startStamping'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'klik start ok untuk start stamping'
                WebUI.click(findTestObject('Object Repository/DocumentMonitoring/button_OKStartStamping'))

                'looping dari 1 hingga 12'
                for (i = 1; i <= GlobalVariable.LoopingPeriodStamping; i++) {
                    'mengambil value db proses meterai'
                    int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakPerPilihan[
                        y])

                    'jika proses materai gagal (51)/(61)'
                    if ((prosesMaterai == 51) || (prosesMaterai == 61)) {
                        'Kasih delay untuk mendapatkan update db untuk error stamping'
                        WebUI.delay(3)

                        'get reason gailed error message untuk stamping'
                        errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakPerPilihan[
                            y])

                        'Write To Excel GlobalVariable.StatusFailed and errormessage'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                            errorMessageDB.toString())

                        GlobalVariable.FlagFailed = 1

                        break
                    } else if ((prosesMaterai == 53) || (prosesMaterai == 63)) {
                        'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                        WebUI.delay(3)

                        'Mengambil value total stamping dan total meterai'
                        ArrayList<String> totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, nomorKontrakPerPilihan[y])

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

                        (GlobalVariable.eSignData['VerifikasiMeterai']) = ((GlobalVariable.eSignData['VerifikasiMeterai']) + 
                        Integer.parseInt(totalMateraiAndTotalStamping[0]))

                        ArrayList<String> officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
                            conneSign, nomorKontrakPerPilihan[y], 'Stamping')

                        'lakukan loop untuk pengecekan data'
                        for (int i = 0; i < (officeRegionBline.size() / 2); i++) {
                            'verify business line dan office code'
                            arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                                    3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        'jika data db tidak bertambah'
                        if (arrayMatch.contains(false)) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                            GlobalVariable.FlagFailed = 1
                        }
                        
                        break
                    } else {
                        'Jika bukan 51 dan 61, maka diberikan delay 20 detik'
                        WebUI.delay(GlobalVariable.TimeLoop)

                        'Jika looping berada di akhir, tulis error failed proses stamping'
                        if (i == GlobalVariable.LoopingPeriodStamping) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + 
                                (i * GlobalVariable.TimeLoop)) + ' detik ')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
            }
        }
        
        inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring)
		
		'declare arraylist arraymatch'
		arrayMatch = []
		
		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?')) == 'Login Via Admin Client') {
			arrayMatch = verifyListAdminClient(conneSign, nomorKontrakPerPilihan, documentId, y, emailSigner)
		} else {
			arrayMatch = verifyListEmbed(conneSign, nomorKontrakPerPilihan, documentId, y, emailSigner)
		}
		
		'penggunaan checking print false'
		if (arrayMatch.contains(false)) {
			GlobalVariable.FlagFailed = 1
	
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
					'-', '') + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada menu Document Monitoring ')
		}
    }
    
    if (CancelDocsStamp == 'Yes') {
        'panggil fungsi cancel docs sesudah send'
        if (cancelDoc(conneSign, (nomorKontrakPerPilihan[y]).toString()) == true) {
            'input kembali data sesudah selesai cancel doc'
            inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring)

            'lakukan pengecekan ke di UI untuk memastikan cancel berhasil'
            checkVerifyEqualOrMatch(WebUI.verifyMatch('Tidak ada data untuk diperlihatkan', WebUI.getText(findTestObject(
                            'DocumentMonitoring/SearchResult'), FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), 
                'Data gagal terhapus di FE')
        }
        
        break
    }
}

def inputDocumentMonitoring(Connection conneSign, String nomorKontrakPerPilihan, String linkDocumentMonitoring) {
    'Pembuatan untuk array Index result Query'
    arrayIndexInput = 0

    'Mengambil value db untuk input-input monitoring seperti nomor kontrak, cabang, dan wilayah'
    inputDocumentMonitoring = CustomKeywords.'connection.DocumentMonitoring.getInputDocumentMonitoring'(conneSign, nomorKontrakPerPilihan)

    'Set text mengenai teks customer'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), inputDocumentMonitoring[arrayIndexInput++], 
        FailureHandling.OPTIONAL)

    'Set text mengneai input nomor kontrak'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), nomorKontrakPerPilihan)

    'Set text mengenai tanggal permintaan dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), inputDocumentMonitoring[arrayIndexInput++])

    'Set text mengenai tanggal selesai dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), inputDocumentMonitoring[(arrayIndexInput - 
        1)])

    'Set text tanggal permintaan sampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), inputDocumentMonitoring[arrayIndexInput++])

    'Set text tanggal selesai sampai'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), inputDocumentMonitoring[(arrayIndexInput - 
        1)])

    'Set text mengenai tipe dokumen'
	inputDDLExact('DocumentMonitoring/input_TipeDok', inputDocumentMonitoring[arrayIndexInput++])
	
    'Set text mengenai status dokumen'
	inputDDLExact('DocumentMonitoring/input_Status', inputDocumentMonitoring[arrayIndexInput++])

    if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/input_Wilayah'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'Set text mengenai wilayah'
		inputDDLExact('DocumentMonitoring/input_Wilayah', inputDocumentMonitoring[arrayIndexInput++])
		
        'Set text mengenai input cabang'
		inputDDLExact('DocumentMonitoring/input_Cabang', inputDocumentMonitoring[arrayIndexInput++])
    }
    
    if (linkDocumentMonitoring == '') {
        'modify object test status tanda tangan di beranda'
        modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'), 
            'xpath', 'equals', '//*[@id="stampingStatus"]/div/div/div[3]/input', true)
    } else if (linkDocumentMonitoring.contains('embed')) {
        'modify object test status tanda tangan di beranda'
        modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'), 
            'xpath', 'equals', '//*[@id=\'prosesMaterai\']/div/div/div[3]/input', true)
    }
	
	if (WebUI.verifyElementNotPresent(modifyObjectProsesMeterai, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		if (linkDocumentMonitoring == '') {
			'modify object test status tanda tangan di beranda'
			modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'),
				'xpath', 'equals', '//*[@id="stampingStatus"]/div/div/div[2]/input', true)
		} else if (linkDocumentMonitoring.contains('embed')) {
			'modify object test status tanda tangan di beranda'
			modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'),
				'xpath', 'equals', '//*[@id=\'prosesMaterai\']/div/div/div[2]/input', true)
		}
	}
	
	inputDDLExactRelativesObject(modifyObjectProsesMeterai, inputDocumentMonitoring[arrayIndexInput++])
    
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?')).contains('Admin Client')) {
		'Set text mengenai input status document'
		inputDDLExact('DocumentMonitoring/input_StatusDocument', inputDocumentMonitoring[arrayIndexInput++])
	}
	
    'Klik enter Cari'
    WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def openHamburgAndroid() {
    'cek apakah elemen menu ditutup'
    if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
        'klik pada button hamburber'
        WebUI.click(findTestObject('button_HamburberSideMenu'))
    }
}

def closeHamburgAndroid() {
    'cek apakah tombol x terlihat'
    if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
        'klik pada button X'
        WebUI.click(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)
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
        checkVerifyEqualOrMatch(WebUI.verifyMatch('0', CustomKeywords.'connection.DocumentMonitoring.getCancelDocStatus'(
                    conneSign, nomorKontrakPerPilihan), false, FailureHandling.CONTINUE_ON_FAILURE), 'Pengecekan ke DB Cancel Doc gagal')

        true
    } else {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + '<') + popupMsg) + '>')

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def actionDocumentMonitoring(Connection conneSign, String nomorKontrakPerPilihan, String linkDocumentMonitoring, int j, String documentName) {
    if (GlobalVariable.RunWithEmbed == 'No') {
        helperModifyObject = '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-monitoring-document/'
		
		columnAction = 11
    } else {
        helperModifyObject = '/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/'
		
		columnAction = 10
    }
    
    'get row lastest'
    modifyObjectSigner = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/button_modifyDownload'), 'xpath', 
        'equals', ((helperModifyObject + 'app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[') + 
        j) + ']/datatable-body-row/div[2]/datatable-body-cell[' + columnAction + ']/div/a[3]/em', true)

    'click button view signer'
    WebUI.click(modifyObjectSigner)

    WebUI.delay(1)

    'get row popup'
    variableRowPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

    'get column popup'
    variableColPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

    arrayMatch = []
	
    'loop untuk row popup'
    for (int i = 1; i <= variableRowPopup.size(); i++) {
        'get data kotak masuk send document secara asc, dimana customer no 1'
        ArrayList<String> resultSigner = CustomKeywords.'connection.SendSign.getSignerKotakMasukSendDoc'(conneSign, nomorKontrakPerPilihan, documentName)

        'loop untuk column popup'
        for (int m = 1; m <= variableColPopup.size(); m++) {
            'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
            modifyObjectTextPopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'), 'xpath', 'equals', 
                ((('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + m) + ']/div', true)

            'signer nama,email,signerType,sudahAktivasi popup'
            arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextPopup), resultSigner[arrayIndexSigner++], false, 
                    FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
    
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and errorLog'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            ' Kesalahan view Signer pada Document Monitoring')
    }
    
    'focus klik x'
    WebUI.focus(findTestObject('KotakMasuk/btn_X'))

    'Klik x terlebih dahulu pada popup'
    WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_X'))

    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need Download Document ?')) == 
    'Yes') {
        'get row lastest'
        modifyObjectDownload = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/button_modifyDownload'), 'xpath', 
            'equals', ((helperModifyObject + 'app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[') + 
            j) + ']/datatable-body-row/div[2]/datatable-body-cell[' + columnAction + ']/div/a[2]/em', true)

        'click button download'
        WebUI.click(modifyObjectDownload)

        'check if error alert present'
        if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason dari error log'
            errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errorLog'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + '<') + errorLog) + '>')

            GlobalVariable.FlagFailed = 1
        }
        
        'check isfiled downloaded'
        if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('Delete Downloaded Document'))) == false) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                GlobalVariable.ReasonFailedDownload)

            GlobalVariable.FlagFailed = 1
        }
    }
    
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need Resend Notification ?')) == 
    'Yes') {
        'click button KirimUlangNotifikasi'
        WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

        'check if error alert present'
        if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason dari error log'
            errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errorLog'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + '<') + errorLog) + '>')

            GlobalVariable.FlagFailed = 1
        } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_YaProses'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'click button tidak batalkan'
            WebUI.click(findTestObject('DocumentMonitoring/button_TidakBatalkan'))

            'click button KirimUlangNotifikasi'
            WebUI.click(findTestObject('DocumentMonitoring/button_KirimUlangNotifikasi'))

            'click button ya proses'
            WebUI.click(findTestObject('DocumentMonitoring/button_YaProses'))

            'check if error alert present'
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get reason dari error log'
                errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errorLog'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + errorLog) + '>')

                GlobalVariable.FlagFailed = 1
            }
            
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/PopupMessage'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'get text dari popup message'
                errorLog = WebUI.getText(findTestObject('DocumentMonitoring/PopUpMessage'))

                if (errorLog.toString().toUpperCase().contains('SUKSES')) {
                    'Input email signer based on sequentialnya'
                    emailSigner = CustomKeywords.'connection.APIFullService.getEmailBasedOnSequence'(conneSign, nomorKontrakPerPilihan)

                    'cek balance mutation dan juga pemotongan saldo'
                    checkSaldoWAOrSMS(conneSign, emailSigner)
                } else {
                    'Write To Excel GlobalVariable.StatusFailed and errorLog'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + '<') + errorLog) + '>')

                    GlobalVariable.FlagFailed = 1
                }
            }
        }
    }
    
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need View Document ?')) == 'Yes') {
        'get row lastest'
        modifyObjectView = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/button_modifyDownload'), 'xpath', 
            'equals', ((helperModifyObject + 'app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[') + 
            j) + ']/datatable-body-row/div[2]/datatable-body-cell[' + columnAction + ']/div/a[1]/em', true)

        'click button view dokumen'
        WebUI.click(modifyObjectView)

        'check if error alert present'
        if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get reason dari error log'
            errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errorLog'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + '<') + errorLog) + '>')

            GlobalVariable.FlagFailed = 1
        } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_NoKontrakView'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'check if no kontrak sama dengan inputan excel'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/label_NoKontrakView')), 
                    'No Kontrak ' + nomorKontrakPerPilihan, false, FailureHandling.CONTINUE_ON_FAILURE), ' dokumen yang di view berbeda')

            'click button Kembali'
            WebUI.click(findTestObject('DocumentMonitoring/button_Kembali'))
        } else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_View'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            checkVerifyEqualOrMatch(false, ' button view tidak berfungsi')
        }
        inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan, linkDocumentMonitoring)
    }
}

def funcLogin(Connection conneSign, String documentId) {
    if (GlobalVariable.RunWithEmbed == 'Yes') {
        aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, findTestData(excelPathFESignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Tenant')))

        emailSigner = ''

        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
        'Sign Only') {
            emailSigner = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email Signer (Sign Only)')).split(
                ';', -1)[0])
        } else {
            emailSigner = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(
                ';', -1)[0])

            if ((emailSigner == '') && (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                    '$idKtp')) != '')) {
                emailSigner = CustomKeywords.'connection.SendSign.getEmaiLFromNIK'(conneSign, findTestData(excelPathFESignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('$idKtp')).split(';', -1)[0])
            }
        }
        
        encryptMsg = encryptLink(conneSign, documentId, emailSigner, aesKey)

        'membuat link document monitoring'
        linkDocumentMonitoring = ((((((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                'Base Link Document Monitoring')) + '?msg=') + encryptMsg) + '&isHO=') + findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('isHO'))) + '&isMonitoring=') + findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('isMonitoring'))) + '&tenantCode=') + findTestData(excelPathFESignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('Tenant')))

        settingHO = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('isHO'))

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
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + '<') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label')) + '>')
        }
    } else if (GlobalVariable.RunWithEmbed == 'V1') {
        'get aesKey general'
        aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

        officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId)

        emailSigner = ''

        if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
        'Sign Only') {
            emailSigner = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email Signer (Sign Only)')).split(
                ';', -1)[0])
        } else {
            emailSigner = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(
                ';', -1)[0])

            if ((emailSigner == '') && (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                    '$idKtp')) != '')) {
                emailSigner = CustomKeywords.'connection.SendSign.getEmaiLFromNIK'(conneSign, findTestData(excelPathFESignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('$idKtp')).split(';', -1)[0])
            }
        }
        
        'pembuatan message yang akan dienkrip'
        msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + emailSigner) + '\',\'tenantCode\':\'') + GlobalVariable.Tenant) + 
        '\'}')

        endcodedMsg = encryptValue(msg, aesKey)

        linkDocumentMonitoring = ((((((GlobalVariable.BaseLink + 'embed/inquiry?msg=') + endcodedMsg) + '&isMonitoring=') + 
        findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('isMonitoring'))) + '&isHO=') + 
        findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('isHO')))

        WebUI.openBrowser(linkDocumentMonitoring)
    } else {
        if (!(WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/input_NamaPelanggan'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL))) {
            if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/DocumentMonitoring'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'cek apakah elemen menu ditutup'
                if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
                    'klik pada button hamburber'
                    WebUI.click(findTestObject('button_HamburberSideMenu'))
                }
                
                'klik button saldo'
                WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

                'cek apakah tombol x terlihat'
                if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
                    'klik pada button X'
                    WebUI.click(findTestObject('buttonX_sideMenu'))
                }
            } else {
                'Call test Case untuk login sebagai admin wom admin client'
                WebUI.callTestCase(findTestCase('Main Flow - Copy/Login'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                'klik button saldo'
                WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))
            }
        }
    }
}

def encryptValue(String value, String aesKey) {
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

def encryptLink(Connection conneSign, String documentId, String emailSigner, String aesKey) {
    'get current date'
    String currentDateTimeStamp = new Date().format('yyyy-MM-dd HH:mm:ss')

    officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId)

    'pembuatan message yang akan dienkrip'
    msg = (((((('{"officeCode" : "' + officeCode) + '", "email" : "') + emailSigner) + '","timestamp" : "') + currentDateTimeStamp) + 
    '"}')

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(msg, aesKey)

    encryptMsg
}

def checkSaldoWAOrSMS(Connection conneSign, String emailSigner) {
    'inisialisasi arraylist balmut'
    ArrayList<String> balmut = []

    'inisialisasi penggunaan saldo, pemotongan saldo, dan increment untuk kebutuhan selanjutnya'
    int penggunaanSaldo = 0

    int pemotonganSaldo = 0

    int increment

    'inisialisasi tipesaldo'
    String tipeSaldo

    'looping per email per kontrak'
    for (loopingEmail = 0; loopingEmail < emailSigner.split(';', -1).size(); loopingEmail++) {
        'split lagi per ;'
        ArrayList<String> email = emailSigner.split(';', -1)

        emailServiceOnTenant = CustomKeywords.'connection.DataVerif.getEmailService'(conneSign, GlobalVariable.Tenant)

        fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, email[loopingEmail])

        notifTypeDBResendSignNotifNormal = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF', GlobalVariable.Tenant)

        notifTypeDBResendSignNotifEmbedV1 = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF_EMBED_V1', GlobalVariable.Tenant)

        notifTypeDBResendSignNotifEmbedV2 = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, 
            emailSigner, 'RESEND_SIGN_NOTIF_EMBED_V2', GlobalVariable.Tenant)

        mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

        String runWithType = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Document Monitoring Using ?'))

        if (runWithType == 'Login Via Admin Client') {
            notifTypeDB = notifTypeDBResendSignNotifNormal
        } else if (runWithType == 'Embed V1') {
            notifTypeDB = notifTypeDBResendSignNotifEmbedV1
        } else if (runWithType == 'Embed V2') {
            notifTypeDB = notifTypeDBResendSignNotifEmbedV2
        }
        
        if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
            'get email service dari email tersebut'
            emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, email[loopingEmail])

            'get settinog must use wa first'
            mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

            'jika must use wa first'
            if (mustUseWAFirst == '1') {
                'tipe saldonya menjadi wa'
                tipeSaldo = 'WhatsApp Message'

                'menggunakan saldo wa'
                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                'jika arraylist tidak ada isinya'
                if (balmut.size() == 0) {
                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via WhatsApp')
                } else {
                    'penggunaan saldo get dari kuantitas per array list balance mutation'
                    penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                }
            } else {
                'jika email servicenya 1'
                if (emailServiceOnVendor == '1') {
                    'check use wa message'
                    useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                    'jika menggunakan wa'
                    if (useWAMessage == '1') {
                        tipeSaldo = 'WhatsApp Message'

                        'menggunakan saldo wa'
                        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                        'jika balmutnya tidak ada value'
                        if (balmut.size() == 0) {
                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via WhatsApp')
                        } else {
                            'penggunaan saldo didapat dari ikuantitaas query balmut'
                            penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                        }
                    } else if (useWAMessage == '0') {
                        'jika tidak menggunakan use wa message, maka mengarah ke sms'

                        'ke sms / wa'
                        SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Send Document')

                        'jika setting sms = 1'
                        if (SMSSetting == '1') {
                            'ke sms'
                            tipeSaldo = 'SMS Notif'

                            'get balmut dari sms '
                            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                            'jika tidak ada balmut'
                            if (balmut.size() == 0) {
                                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via SMS')
                            } else {
                                'get kuantitas dari balmut'
                                penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                            }
                        }
                    }
                }
            }
        } else {
            tipeSaldo = notifTypeDB

            'menggunakan saldo wa'
            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

            'jika balmutnya tidak ada value'
            if (balmut.size() == 0) {
                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') + 
                    tipeSaldo.replace(' Message', '').replace(' Notif', ''))
            } else {
                'penggunaan saldo didapat dari ikuantitaas query balmut'
                penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
            }
        }
        
        'jika ada penggunaan saldo'
        if (penggunaanSaldo > 0) {
            'looping mengenai penggunaan saldo'
            for (looping = 1; looping <= penggunaanSaldo; looping++) {
                'jika loopingan pertama, incrementnya 0'
                if (looping == 0) {
                    increment = 0
                } else {
                    'increment meningkat 9'
                    increment = (increment + 10)
                }
                
                'pemotongan saldo akan di get berdasarkan kuantitas yang awalnya hanya get dari total 11 row query'
                pemotonganSaldo = (pemotonganSaldo + Integer.parseInt((balmut[(increment + 9)]).replace('-', '')))

                'input value trx number ke hashmap'
                (GlobalVariable.eSignData['allTrxNo']) = (((GlobalVariable.eSignData['allTrxNo']) + (balmut[(increment + 
                0)])) + ';')

                'input value sign type ke hashmap'
                (GlobalVariable.eSignData['allSignType']) = (((GlobalVariable.eSignData['allSignType']) + (balmut[(increment + 
                3)]).replace('Use ', '')) + ';')

                'input email usage sign (nama/email) ke hashmap'
                (GlobalVariable.eSignData['emailUsageSign']) = (((GlobalVariable.eSignData['emailUsageSign']) + fullNameUser) + 
                ';')
            }
        }
        
        'jika tipenya wa, maka tambah pemotongan saldo ke countverifikasiwa, jika sms, maka ke sms'
        if (tipeSaldo == 'WhatsApp Message') {
            (GlobalVariable.eSignData['CountVerifikasiWA']) = pemotonganSaldo
        } else if (tipeSaldo == 'SMS Notif') {
            (GlobalVariable.eSignData['CountVerifikasiSMS']) = pemotonganSaldo
        }
    }
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
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div[' + (i + 1) + ']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}

def inputDDLExactRelativesObject(TestObject locationObject, String input) {
	'Input value status'
	WebUI.setText(locationObject, input)

	if (input != '') {
		WebUI.click(locationObject)
	
	'get token unik'
	tokenUnique = WebUI.getAttribute(locationObject, 'aria-owns')
	
	'modify object label Value'
	modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div[' + (i + 1) + ']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(locationObject)
		
		WebUI.sendKeys(locationObject, Keys.chord(Keys.ENTER))
	}
}

def verifyListAdminClient(Connection conneSign, ArrayList nomorKontrakPerPilihan, ArrayList documentId, int y, ArrayList emailSigner) {
	'declare arraylist arraymatch'
	arrayMatch = []

	modifyObjectvalues = findTestObject('DocumentMonitoring/lbl_Value')

	'Mengambil row size dari value'
	sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

	'Mengambil column size dari value'
	sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

	'Jika valuenya ada'
	if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'Pembuatan untuk array Index result Query'
		arrayIndex = 0

		'Mengambil value dari db menngenai data yang perlu diverif'
		resultQuery = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringBasedOnEmbed'(conneSign, nomorKontrakPerPilihan[
			y])

		'Mengambil value dari db mengenai total stamping'
		resultStamping = CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign, nomorKontrakPerPilihan[
			y])

		'array index untuk stamping'
		arrayIndexStamping = 0

		arrayIndexJumlahHarusTandaTangan = 0

		'Looping berdasarkan row yang ada pada value'
		for (j = 1; j <= sizeRowofLabelValue.size(); j++) {
			'Looping berdasarkan column yang ada pada value tanpa aksi.'
			for (i = 1; i <= sizeColumnofLabelValue.size(); i++) {
				'modify object label Value'
				modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
					'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
					j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

				WebUI.scrollToElement(modifyObjectvalues, GlobalVariable.TimeOut)

				'Jika berada di column ke 7'
				if (i == 7) {
					'Split teks proses TTD'
					totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

					if (vendor.equalsIgnoreCase('Digisign')) {
						jumlahSignerTelahTandaTangan = CustomKeywords.'connection.APIFullService.getUserAlreadySigned'(
							conneSign, nomorKontrakPerPilihan[y])
					} else if (vendor.equalsIgnoreCase('Privy')) {
						jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgressPrivy'(
							conneSign, documentId[(j - 1)])
					} else {
						jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign,
							documentId[(j - 1)])
					}
					
					'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
					arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahSignerTelahTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))

					if ((sizeRowofLabelValue.size() > 1) && (nomorKontrakPerPilihan.size() == 1)) {
						jumlahSignerHarusTandaTangan = CustomKeywords.'connection.SendSign.getTotalSignerTtd'(conneSign,
							documentId[(j - 1)])

						arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], jumlahSignerHarusTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))
					} else {
						arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
					}
				} else if (i == 8) {
					'Jika berada di column ke 8. Split teks total Stamping'
					totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

					'looping berdasarkan total split dan diverif berdasarkan db.'
					for (k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
						'Verifikasi UI dengan db'
						arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[arrayIndexStamping++],
								FailureHandling.CONTINUE_ON_FAILURE))
					}
				} else if (i == 11) {
					continue
				} else if (i == 10) {
					'get xpath status'
					modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
						'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
						j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div/em', true)
					
					'get classnya'
					statusDocument = WebUI.getAttribute(modifyObjectvalues, 'class')
					
					'jika dbnya 1'
					if (resultQuery[arrayIndex++] == '1') {
						'jika statusnya X'
						if (statusDocument.toString().contains('danger')) {
							'input array false'
							arrayMatch.add(false)
						}
					} else if (resultQuery[arrayIndex++] == '0') {
						'jika dbnya 0, namun statusnya success'
						if (statusDocument.toString().contains('success')) {
							'input array false'
							arrayMatch.add(false)
						}
					}
				} else if (i == 3) {
					'Selain di column 7 dan 8 maka akan diverif dengan db.'
					arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false,
							FailureHandling.CONTINUE_ON_FAILURE))
					
					documentName = WebUI.getText(modifyObjectvalues)
				} else {
					'Selain di column 7 dan 8 maka akan diverif dengan db.'
					arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false,
							FailureHandling.CONTINUE_ON_FAILURE))
				}
			}
			
			if (((((CancelDocsStamp != 'Yes') && (isStamping != 'Yes')) && (retryStamping != 'Yes')) && (CancelDocsSend !=
			'Yes')) && (CancelDocsSign != 'Yes')) {
				actionDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, j, documentName)
			}
		}
	} else {
		'Jika tidak ada, maka datanya tidak ada di UI.'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

		GlobalVariable.FlagFailed = 1
	}
	arrayMatch
}

def verifyListEmbed(Connection conneSign, ArrayList nomorKontrakPerPilihan, ArrayList documentId, int y, ArrayList emailSigner) {
	'declare arraylist arraymatch'
	arrayMatch = []

   WebUI.delay(2)

   'modify object label Value'
   modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 'equals',
	   '/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[1]/div/p',
	   true)

   'Jika valuenya ada'
   if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
	   WebUI.delay(2)

	   'Mengambil row size dari value'
	   sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

	   'Mengambil column size dari value'
	   sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-content-layout > div > div > div > div.content-wrapper.p-0 > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller >  datatable-row-wrapper > datatable-body-row datatable-body-cell'))

	   'Pembuatan untuk array Index result Query'
	   arrayIndex = 0

	   'Mengambil value dari db menngenai data yang perlu diverif'
	   resultQuery = CustomKeywords.'connection.DocumentMonitoring.getDocumentMonitoringBasedOnEmbed'(conneSign, nomorKontrakPerPilihan[
		   y])

	   'Mengambil value dari db mengenai total stamping'
	   resultStamping = CustomKeywords.'connection.DocumentMonitoring.getTotalStampingandTotalMaterai'(conneSign, nomorKontrakPerPilihan[
		   y])

	   'Looping berdasarkan row yang ada pada value'
	   for (j = 1; j <= sizeRowofLabelValue.size(); j++) {
		   'Looping berdasarkan column yang ada pada value tanpa aksi.'
		   for (i = 1; i <= (sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()); i++) {
			   modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
				   'equals', ((('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				   j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

			   WebUI.scrollToElement(modifyObjectvalues, GlobalVariable.TimeOut)

			   'Jika berada di column ke 7'
			   if (i == 7) {
				   'Split teks proses TTD'
				   totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

				   if (vendor.equalsIgnoreCase('Digisign')) {
					   jumlahSignerTelahTandaTangan = CustomKeywords.'connection.APIFullService.getUserAlreadySigned'(
						   conneSign, nomorKontrakPerPilihan[y])
				   } else if (vendor.equalsIgnoreCase('Privy')) {
					   jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgressPrivy'(
						   conneSign, documentId[(j - 1)])
				   } else {
					   jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign,
						   documentId[(j - 1)])
				   }
				   
				   if ((sizeRowofLabelValue.size() > 1) && (nomorKontrakPerPilihan.size() == 1)) {
					   jumlahSignerHarusTandaTangan = CustomKeywords.'connection.SendSign.getTotalSignerTtd'(conneSign,
						   documentId[(j - 1)])

					   arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], jumlahSignerHarusTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))
				   } else {
					   arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
				   }
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
				   /*modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
				   'equals', ((('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				   j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div/em', true)

				   statusDocument = WebUI.getAttribute(modifyObjectvalues, 'class')
				   
				   if (resultQuery[arrayIndex++] == '1') {
					   if (statusDocument.toString().contains('danger')) {
						   arrayMatch.add(false)
					   }
				   } else if (resultQuery[arrayIndex++] == '0') {
					   if (statusDocument.toString().contains('success')) {
						   arrayMatch.add(false)
					   }
				   }
				   */
				   arrayIndex++
				   
				   continue
			   } else if (i == 3) {
				   'Selain di column 7 dan 8 maka akan diverif dengan db.'
				   arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false,
						   FailureHandling.CONTINUE_ON_FAILURE))
				   documentName = WebUI.getText(modifyObjectvalues)
			   } else {
				   'Selain di column 7 dan 8 maka akan diverif dengan db.'
				   arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false,
						   FailureHandling.CONTINUE_ON_FAILURE))
			   }
		   }
		   
		   if (((((CancelDocsStamp != 'Yes') && (isStamping != 'Yes')) && (retryStamping != 'Yes')) && (CancelDocsSend !=
		   'Yes')) && (CancelDocsSign != 'Yes')) {
			   actionDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, j, documentName)
		   }
	   }
   } else {
	   'Jika tidak ada, maka datanya tidak ada di UI.'
	   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		   ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
			   '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

	   GlobalVariable.FlagFailed = 1
   }
	arrayMatch
}