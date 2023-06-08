import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'mengambil document dari excel yang telah diberikan.'
docId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 6).split(', ', -1)

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayIndex = 0

'jumlah signer yang telah tanda tangan masuk dalam variable dibawah'
int jumlahSignerTandaTangan = 0

'looping berdasarkan jumlah dokumen'
for (int y = 0; y < docId.size(); y++) {
    'Mengambil email berdasarkan documentId'
    ArrayList emailSigner = CustomKeywords.'connection.DataVerif.getEmailLogin'(conneSign, docId[y]).split(';', -1)

    'looping berdasarkan email Signer dari dokumen tersebut. '
    for (int t = 0; t < emailSigner.size(); t++) {
            'call Test Case untuk login sebagai user berdasarkan doc id'
            WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [('email') : emailSigner[t]], FailureHandling.STOP_ON_FAILURE)
        
        'get data kotak masuk send document secara asc, dimana customer no 1'
        ArrayList result = CustomKeywords.'connection.DataVerif.getKotakMasukSendDoc'(conneSign, docId[y])

        'declare array proses Ttd pada Pencarian Dokumen. Digunakan untuk membandingkan dengan Kotak Masuk / Beranda. '
        ArrayList prosesTtdPencarianDokumen = []

        'click menu pencarian dokumen'
        WebUI.click(findTestObject('PencarianDokumen/menu_PencarianDokumen'))

        'input status'
        WebUI.setText(findTestObject('PencarianDokumen/select_Status'), CustomKeywords.'connection.DataVerif.getSignStatus'(
                conneSign, docId[y]))

        'click enter untuk input select ddl'
        WebUI.sendKeys(findTestObject('PencarianDokumen/select_Status'), Keys.chord(Keys.ENTER))

        'Klik button cari'
        WebUI.click(findTestObject('Object Repository/PencarianDokumen/button_Cari'))

        'Jika pada halaman tersebut, jika tidak ada referal number yang muncul. Maka write failed tidak ada UI.'
        if (!(WebUI.verifyElementPresent(findTestObject('PencarianDokumen/text_refnum'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE))) {
            'Write excel tidak ada di UI pada menu Pencarian Dokumen.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                    2) + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada menu Pencarian Dokumen')
        }
        
        'Agar dapat ke Lastest pada Pencarian Dokumen, diambil selector'
        variableLastestPencarianDokumen = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'modifikasi button Lastest pada paging'
        modifyobjectBtnLastestPencarianDokumen = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variableLastestPencarianDokumen.size()) + ']/a/i', true)

        'Jika button Lastest dapat diklik'
        if (WebUI.verifyElementVisible(modifyobjectBtnLastestPencarianDokumen, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyobjectBtnLastestPencarianDokumen, FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'ambil row lastest pencarian dokumen'
        variablePencarianDokumenRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'ambil column lastest pencarian dokumen'
        variablePencarianDokumenColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        'loop berdasarkan jumlah kolom dan dicheck dari 1 - 10.'
        for (int i = 1; i <= (variablePencarianDokumenColumn.size() / variablePencarianDokumenRow.size()); i++) {
            'modify object text refnum, tipe dok, nama dok, tgl permintaan, tgl selesai, proses ttd, total materai, status'
            modifyObjectPencarianDokumen = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/text_refnum'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                variablePencarianDokumenRow.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', 
                true)

            'Jika kolom yang ingin di check berada pada urutan ke-5, yaitu Tgl Selesai'
            if (i == 5) {
                'Match text dengan completed date'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[5], false, FailureHandling.CONTINUE_ON_FAILURE))
            } else if (i == 6) {
                'Jika kolom dicheck pada Proses TTD, mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan. Yang kedua menjadi total signer'
                prosesTtdPencarianDokumen = WebUI.getText(modifyObjectPencarianDokumen).split('/', -1)

                'Pengecekan proses sign'
                arrayMatch.add(WebUI.verifyEqual(jumlahSignerTandaTangan, (prosesTtdPencarianDokumen[0]).replace(' ', ''), 
                        FailureHandling.CONTINUE_ON_FAILURE))

                'Pengecekan total proses tanda tangan'
                arrayMatch.add(WebUI.verifyEqual(emailSigner.size(), (prosesTtdPencarianDokumen[1]).replace(' ', ''), FailureHandling.CONTINUE_ON_FAILURE))
            } else if (i == 7) {
                'Jika kolom dicheck pada Total Meterai, keyword untuk mengecek total stamping dan total materai berdasarkan document id'
                resultStamping = CustomKeywords.'connection.DataVerif.getTotalStampingandTotalMaterai'(conneSign, docId[
                    y])

                'Mengambil teks dari UI dan displit berdasarkan proses stamping dan total materai'
                totalMateraiPencarianDokumen = WebUI.getText(modifyObjectPencarianDokumen).split('/', -1)

                'Pengecekan proses stamping'
                arrayMatch.add(WebUI.verifyEqual(totalMateraiPencarianDokumen[0], resultStamping[0], FailureHandling.CONTINUE_ON_FAILURE))

                'Pengecekan total materai'
                arrayMatch.add(WebUI.verifyEqual(totalMateraiPencarianDokumen[1], resultStamping[1], FailureHandling.CONTINUE_ON_FAILURE))
            } else if (i == 9) {
                'Untuk ke sembilan tidak dibuat pengecekan dikarenakan itu adalah aksi. AKsi tidak dapat dicheck'
            } else if (i == 8) {
                'Jika kolom dicheck pada Status, maka Pengecekan sign status'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), CustomKeywords.'connection.DataVerif.getSignStatus'(
                            conneSign, docId[y]), false, FailureHandling.CONTINUE_ON_FAILURE))
            } else {
                'Diverifikasi dengan UI didepan'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[arrayIndex++], false, 
                        FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        'Klik objek Beranda'
        WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

        'dekalarsi arrayindex menjadi 0'
        arrayIndex = 0

        'get row lastest'
        variableLastest = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

        'Agar dapat ke Lastest, modifikasi button Lastest pada paging'
        modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variableLastest.size()) + ']/a/i', true)

        'Jika button Lastest dapat diklik'
        if (WebUI.verifyElementClickable(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyObjectBtnLastest, FailureHandling.OPTIONAL)
        }
        
        'get row pada beranda'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'modify object text refnum'
        modifyObjectTextRefNum = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'Mengambil teks refnum'
        labelRefNum = WebUI.getText(modifyObjectTextRefNum)

        'modify object text document type'
        modifyObjectTextDocumentType = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipedokumen'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object text document template name'
        modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object text tanggal permintaan'
        modifyObjectTextTanggalPermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

        'modify object text proses ttd'
        modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div', true)

        'modify object text total materai'
        modifyObjectTextTotalMaterai = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div', true)

        'modify object text status TTD'
        modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object button signer'
        modifyObjectBtnSigner = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[4]/em', true)

        'modify object button View Document'
        modifyObjectBtnViewDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[2]/em', true)

        'verifikasi ref number dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextRefNum), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi doctype dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentType), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi document template name dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentTemplateName), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi tanggal permintaan dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextTanggalPermintaan), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi status ttd'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextStatusTtd), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'Mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan. Yang kedua menjadi total signer'
        ArrayList prosesTtd = WebUI.getText(modifyObjectTextProsesTtd).split('/', -1)

        'verifikasi total signer beranda dan pencarian dokumen'
        arrayMatch.add(WebUI.verifyMatch(prosesTtd.toString(), prosesTtdPencarianDokumen.toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label'))
        }
        
        'Klik button Signer'
        WebUI.click(modifyObjectBtnSigner)

        'get row popup'
        variableRowPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'get column popup'
        variableColPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        'loop untuk row popup'
        for (int i = 1; i <= variableRowPopup.size(); i++) {
            'get data kotak masuk send document secara asc, dimana customer no 1'
            ArrayList resultSigner = CustomKeywords.'connection.DataVerif.getSignerKotakMasukSendDoc'(conneSign, docId[y], 
                emailSigner[(i - 1)])

            'declare array index menjadi 0 per result'
            arrayIndex = 0

            'loop untuk column popup'
            for (int m = 1; m <= (variableColPopup.size() / variableRowPopup.size()); m++) {
                'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
                modifyObjectTextPopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'), 'xpath', 
                    'equals', ((('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + m) + ']/div', true)

                'signer nama,email,signerType,sudahAktivasi popup'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextPopup), resultSigner[arrayIndex++], false, 
                        FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        'set ulang array index untuk pencarian dokumen'
        arrayIndex = 0

        'Klik x terlebih dahulu pada popup'
        WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_X'))

        'Jika document ingin didownload, maka'
        if (isDownloadDocument == 'Yes') {
            'modify object button Download Doc'
            modifyObjectBtnDownloadDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[3]/em', true)

            'Klik download file'
            WebUI.click(modifyObjectBtnDownloadDoc)

            'Kasih waktu 4 detik untuk proses download'
            WebUI.delay(4)

            'Check apakah sudah terddownload menggunakan custom keyword'
            CustomKeywords.'customizekeyword.Download.isFileDownloaded'(isDeleteDownloadedDocument)
        }
        
        'Jika is View Document yes, maka '
        if (isViewDocument == 'Yes') {
            'Klik View Document'
            WebUI.click(modifyObjectBtnViewDoc)

            'Pemberian waktu 3 detik karena loading terus menerus'
            WebUI.delay(4)

            'verifikasi label dokumen'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'), GlobalVariable.TimeOut, 
                FailureHandling.CONTINUE_ON_FAILURE)) {
                'Mengambil label pada view Dokoumen'
                labelViewDoc = WebUI.getText(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'))

                'Jika pada label terdapat teks No Kontrak'
                if (labelViewDoc.contains('No Kontrak')) {
                    'Direplace dengan kosong agar mendapatkan nomor kontrak'
                    labelViewDoc = labelViewDoc.replace('No Kontrak ', '')
                }
                
                'Diverifikasi dengan UI didepan'
                arrayMatch.add(WebUI.verifyMatch(labelRefNum, labelViewDoc, false, FailureHandling.CONTINUE_ON_FAILURE))

                'Klik kembali'
                WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_backViewDokumen'))
            } else {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedProcessNotDone) + 
                    ' untuk proses View dokumen tanda tangan. ')
            }
        }
    }
    
    WebUI.callTestCase(findTestCase('DocumentMonitoring/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathFESignDocument
            , ('jumlahsignertandatangan') : jumlahSignerTandaTangan, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI)
    }
}

