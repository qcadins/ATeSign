import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration

'setting untuk membuat lokasi default folder download'
HashMap<String, ArrayList> chromePrefs = new HashMap<String, ArrayList>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

docid = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 5).replace('[', '').replace(']', '').split(', ', 
    -1)

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

'declare arrayindex'
arrayindex = 0

for (int y = 0; y < docid.size(); y++) {
    'Mengambil email berdasarkan documentId'
    ArrayList<String> emailSigner = CustomKeywords.'connection.DataVerif.getEmailLogin'(conneSign, docid[y]).split(';',-1)

    for (int t = 0; t < emailSigner.size(); t++) {
        'call Test Case untuk login sebagai user berdasarkan doc id'
        WebUI.callTestCase(findTestCase('Login/Login_1docManySigner'), [('email') : emailSigner[t]], FailureHandling.STOP_ON_FAILURE)

        'get data kotak masuk send document secara asc, dimana customer no 1'
        ArrayList<String> result = CustomKeywords.'connection.DataVerif.getKotakMasukSendDoc'(conneSign, docid[y])

        ArrayList<String> prosesttd_pencariandokumen = new ArrayList<String>()

        'click menu pencarian dokumen'
        WebUI.click(findTestObject('PencarianDokumen/menu_PencarianDokumen'))

        'input status'
        WebUI.setText(findTestObject('PencarianDokumen/select_Status'), CustomKeywords.'connection.DataVerif.getSignStatus'(
                conneSign, docid[y]))

        'click enter untuk input select ddl'
        WebUI.sendKeys(findTestObject('PencarianDokumen/select_Status'), Keys.chord(Keys.ENTER))

        'Klik button cari'
        WebUI.click(findTestObject('Object Repository/PencarianDokumen/button_Cari'))

        if (!(WebUI.verifyElementPresent(findTestObject('PencarianDokumen/text_refnum'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE))) {
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + GlobalVariable.ReasonFailedNoneUI)
        }
        
        'Agar dapat ke Lastest'
        variable_lastest_pencariandokumen = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'modifikasi button Lastest pada paging'
        modifyobjectbtnLastestpencariandokumen = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable_lastest_pencariandokumen.size()) + ']/a/i', true)

        'Jika button Lastest dapat diklik'
        if (WebUI.verifyElementVisible(modifyobjectbtnLastestpencariandokumen, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyobjectbtnLastestpencariandokumen, FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'ambil row lastest pencarian dokumen'
        variable_pencariandokumen_row = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'ambil column lastest pencarian dokumen'
        variable_pencariandokumen_column = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        'loop berdasarkan jumlah kolom dan dicheck dari 1 - 10.'
        for (int i = 1; i <= (variable_pencariandokumen_column.size() / variable_pencariandokumen_row.size()); i++) {
            'modify object text refnum, tipe dok, nama dok, tgl permintaan, tgl selesai, proses ttd, total materai, status'
            modifyObjectpencariandokumen = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/text_refnum'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                variable_pencariandokumen_row.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', 
                true)

            'Jika kolom yang ingin di check berada pada urutan ke-5'
            if (i == 5) {
                if (jumlahsignertandatangan <= emailSigner.size()) {
                    arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectpencariandokumen), '-', false, FailureHandling.CONTINUE_ON_FAILURE) //Jika kolom yang ingin di check berada pada urutan ke-6
                        ) //Kolom materai, masih belum get dari Beranda
                } //Jika selain kolom2 tersebut, maka
            } else if (i == 6) {
                'mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan'

                'yang kedua menjadi total signer'
                prosesttd_pencariandokumen = WebUI.getText(modifyObjectpencariandokumen).split('/', -1)

                arrayMatch.add(WebUI.verifyEqual(jumlahsignertandatangan, (prosesttd_pencariandokumen[0]).replace(' ', ''), 
                        FailureHandling.CONTINUE_ON_FAILURE))

                arrayMatch.add(WebUI.verifyEqual(emailSigner.size(), (prosesttd_pencariandokumen[1]).replace(' ', ''), FailureHandling.CONTINUE_ON_FAILURE))
            } else if ((i == 7) || (i == 9)) {
            } else if (i == 8) {
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectpencariandokumen), CustomKeywords.'connection.DataVerif.getSignStatus'(
                            conneSign, docid[y]), false, FailureHandling.CONTINUE_ON_FAILURE))

                arrayindex++
            } else {
                'Diverifikasi dengan UI didepan'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectpencariandokumen), result[arrayindex++], false, 
                        FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        'Klik objek Beranda'
        WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

        'dekalarsi arrayindex menjadi 0'
        arrayindex = 0

        'get row lastest'
        variable_lastest = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager li'))

        'Agar dapat ke Lastest'
        'modifikasi button Lastest pada paging'
        modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable_lastest.size()) + ']/a/i', true)

        'Jika button Lastest dapat diklik'
        if (WebUI.verifyElementClickable(modifyobjectbtnLastest, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyobjectbtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
        }
        
        'get row pada beranda'
        variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'modify object text refnum'
        modifyObjecttextrefnum = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'Mengambil teks refnum'
        lbl_refnum = WebUI.getText(modifyObjecttextrefnum)

        'modify object text document type'
        modifyObjecttextdocumenttype = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipedokumen'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object text document template name'
        modifyObjecttextdocumenttemplatename = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object text tanggal permintaan'
        modifyObjecttexttanggalpermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

        'modify object text proses ttd'
        modifyObjecttextprosesttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div', true)

        'modify object text total materai'
        modifyObjecttexttotalmaterai = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div', true)

        'modify object text status TTD'
        modifyObjecttextstatusttd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object button signer'
        modifyObjectbtnSigner = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[4]/em', true)

        'modify object button View Document'
        modifyObjectbtnViewDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[2]/em', true)

        'modify object button Download Doc'
        modifyObjectbtnDownloadDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/a[3]/em', true)

        'verifikasi ref number dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextrefnum), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi doctype dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttype), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi document template name dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextdocumenttemplatename), result[arrayindex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi tanggal permintaan dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttexttanggalpermintaan), result[arrayindex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi status ttd'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextstatusttd), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan'
        'yang kedua menjadi total signer'
        ArrayList<String> prosesttd = WebUI.getText(modifyObjecttextprosesttd).split('/', -1)

        'verifikasi total signer beranda dan pencarian dokumen'
        arrayMatch.add(WebUI.verifyMatch(prosesttd.toString(), prosesttd_pencariandokumen.toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
		/*
        'Klik View Document'
        WebUI.click(modifyObjectbtnViewDoc)

        'Pemberian waktu 3 detik karena loading terus menerus'
        WebUI.delay(4)

        'verifikasi label dokumen'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)) {
            'Mengambil label pada view Dokoumen'
            lbl_viewDoc = WebUI.getText(findTestObject('Object Repository/KotakMasuk/lbl_viewDokumen'))

            'Jika pada label terdapat teks No Kontrak'
            if (lbl_viewDoc.contains('No Kontrak')) {
                'Direplace dengan kosong agar mendapatkan nomor kontrak'
                lbl_viewDoc = lbl_viewDoc.replace('No Kontrak ', '')
            }
            
            'Diverifikasi dengan UI didepan'
            arrayMatch.add(WebUI.verifyMatch(lbl_refnum, lbl_viewDoc, false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik kembali'
            WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_backViewDokumen'))
        } else {
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + GlobalVariable.ReasonFailedProcessNotDone)
        }
        */
        'Agar dapat ke Lastest. Modifikasi button Lastest pada paging'
        modifyobjectbtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable_lastest.size()) + ']/a/i', true)

        'Jika button Lastest dapat diklik'
        if (WebUI.verifyElementClickable(modifyobjectbtnLastest, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyobjectbtnLastest)
        }
        
		/*
        'Klik download file'
        WebUI.click(modifyObjectbtnDownloadDoc)
		*/
        'Kasih waktu 2 detik untuk proses download'
        WebUI.delay(4)

        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label'))
        }
        
        'Check apakah sudah terddownload menggunakan custom keyword'
        CustomKeywords.'customizeKeyword.Download.isFileDownloaded'('Yes')

        'get data kotak masuk send document secara asc, dimana customer no 1'
        ArrayList<String> resultSigner = CustomKeywords.'connection.DataVerif.getSignerKotakMasukSendDoc'(conneSign, docid[
            y])

        'Klik btnSigner'
        WebUI.click(modifyObjectbtnSigner)

        'get row popup'
        variable_row_popup = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'get column popup'
        variable_col_popup = DriverFactory.getWebDriver().findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        'declare arrayindex buat signer'
        arrayindex_signer = 0

        'loop untuk row popup'
        for (int i = 1; i <= variable_row_popup.size(); i++) {
            'loop untuk column popup'
            for (int m = 1; m <= (variable_col_popup.size() / variable_row_popup.size()); m++) {
                'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
                modifyObjecttextpopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'), 'xpath', 
                    'equals', ((('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + m) + ']/div', true)

                'signer nama,email,signerType,sudahAktivasi popup'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjecttextpopup), resultSigner[arrayindex_signer++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        'set ulang array index untuk pencarian dokumen'
        arrayindex = 0

        'Klik x terlebih dahulu pada popup'
        WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_X'))
    }
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(docid[y], GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedNoneUI)
}

