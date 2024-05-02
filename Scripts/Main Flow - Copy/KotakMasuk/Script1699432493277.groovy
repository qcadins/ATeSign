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

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

'mengambil document dari excel yang telah diberikan.'
ArrayList docId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(
    ', ', -1)

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayIndex = 0

'bikin variable hashmap untuk ambil count per signer'
HashMap<String, String> resultHashMap = [:]

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, docId[0])

flagBreak = 0

boolean usingHashMap = false

'looping berdasarkan jumlah dokumen'
resultHashMap = loopingMultiDoc(docId, conneSign, refNumber, resultHashMap)

for (t = 0; t < resultHashMap.keySet().size(); t++) {
    if (GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])] == resultHashMap.keySet()[t]) {
        break
    } else if (t == (resultHashMap.keySet().size() - 1)) {
        WebUI.comment('error tidak menemukan email')
    }
}

'inisialisasi variable untuk looping. Looping diperlukan untuk break/continue'
forLoopingWithBreakAndContinue = 1

'looping'
for (o = 0; o < forLoopingWithBreakAndContinue; o++) {
    if ((GlobalVariable.storeVar.toString() == '[:]') || (usingHashMap == true)) {
        usingHashMap = true

        forLoopingWithBreakAndContinue = resultHashMap.keySet().size()

        GlobalVariable.storeVar[0] = resultHashMap.keySet()[o]
    }
    
	WebUI.comment(resultHashMap.toString())
    'call Test Case untuk login sebagai user berdasarkan doc id'
    WebUI.callTestCase(findTestCase('Main Flow - Copy/Login'), [('email') : GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], ('excel') : excelPathFESignDocument
            , ('checkBeforeSigning') : checkBeforeSigning, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)

	WebUI.comment(GlobalVariable.storeVar.toString())
		
    'get data kotak masuk send document secara asc, dimana customer no 1'
    ArrayList result = CustomKeywords.'connection.SendSign.getKotakMasukSendDoc'(conneSign, GlobalVariable.storeVar.keySet()[0], GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    ArrayList documentIdBasedOnLogin = CustomKeywords.'connection.DataVerif.getDocIdBasedOnLoginSigner'(conneSign, refNumber, GlobalVariable.Tenant, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    'declare array proses Ttd pada Pencarian Dokumen. Digunakan untuk membandingkan dengan Kotak Masuk / Beranda. '
    ArrayList prosesTtdPencarianDokumen = [], totalMateraiPencarianDokumen = []

    openHamburgAndroid()

    'click menu pencarian dokumen'
    WebUI.click(findTestObject('PencarianDokumen/menu_PencarianDokumen'))

    closeHamburgAndroid()

    'query untuk input pencarian dokumen'
    ArrayList inputPencarianDokumen = CustomKeywords.'connection.SendSign.getDataPencarianDokumen'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], refNumber)

    'inisialisasi arrayindex'
    arrayIndex = 0

    'diberikan delay untuk mendapatkan input nama pelanggan'
    WebUI.delay(2)

    if (GlobalVariable.roleLogin == 'BM MF') {
        not_run: WebUI.setText(findTestObject('PencarianDokumen/input_NamaPelanggan'), inputPencarianDokumen[arrayIndex++])

        'input nama pelanggan'
        arrayIndex++

        'input no kontrak'
        WebUI.setText(findTestObject('PencarianDokumen/input_NomorKontrak'), inputPencarianDokumen[arrayIndex++])

        'input TanggalPermintaanDari'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanDari'), currentDate)

        'input TanggalPermintaanSampai'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalPermintaanSampai'), currentDate)

        'input TanggalSelesaiDari'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiDari'), currentDate)

        'input TanggalSelesaiSampai'
        WebUI.setText(findTestObject('PencarianDokumen/input_TanggalSelesaiSampai'), currentDate)
		
		'input tipe dokumen'
		inputDDLExact('PencarianDokumen/select_TipeDokumen', inputPencarianDokumen[arrayIndex++])
    }
    'input status'
	inputDDLExact('PencarianDokumen/select_Status', CustomKeywords.'connection.SendSign.getSignStatus'(conneSign, 
            refNumber))
	
    'Klik button cari'
    WebUI.click(findTestObject('Object Repository/PencarianDokumen/button_Cari'))

    'Jika pada halaman tersebut, jika tidak ada referal number yang muncul. Maka write failed tidak ada UI.'
    if (!(WebUI.verifyElementPresent(findTestObject('PencarianDokumen/text_refnum'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE))) {
        'Write excel tidak ada di UI pada menu Pencarian Dokumen.'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedNoneUI) + ' pada menu Pencarian Dokumen')
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
    
    'declare arrayindex'
    arrayIndex = 0

    'Tambahan column untuk MF dan Cust agar dapat 1 code saja'
    tambahanColumn = 0

    indexArrayMeterai = 0

    'Jika kolom dicheck pada Total Meterai, keyword untuk mengecek total stamping dan total materai berdasarkan document id'
    resultStamping = CustomKeywords.'connection.SendSign.getTotalStampingandTotalMaterai'(conneSign, result[0], GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])])

    loopingPerDocument = 0

    for (c = 0; c < resultHashMap.get(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]); c++) {
        'ambil row lastest pencarian dokumen'
        variablePencarianDokumenRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'ambil column lastest pencarian dokumen'
        variablePencarianDokumenColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

        if (c >= variablePencarianDokumenRow.size()) {
            'modifikasi button Lastest pada paging'
            modifyobjectBtnPreviousPencarianDokumen = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
                'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[2]/a/i', 
                true)

            WebUI.click(modifyobjectBtnPreviousPencarianDokumen)

            'ambil row lastest pencarian dokumen'
            variablePencarianDokumenRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

            'ambil column lastest pencarian dokumen'
            variablePencarianDokumenColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper > datatable-body-row datatable-body-cell'))

            variablePencarianDokumenRow.size() == (variablePencarianDokumenRow.size() - c)

            resultHashMap.put(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], resultHashMap.get(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]) - c)

            c = 0
        }
        
        'loop berdasarkan jumlah kolom dan dicheck dari 1 - 10.'
        for (int i = 1; i <= (variablePencarianDokumenColumn.size() / variablePencarianDokumenRow.size()); i++) {
            'modify object text refnum, tipe dok, nama dok, tgl permintaan, tgl selesai, proses ttd, total materai, status'
            modifyObjectPencarianDokumen = WebUI.modifyObjectProperty(findTestObject('PencarianDokumen/text_refnum'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                (variablePencarianDokumenRow.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', 
                true)

            WebUI.scrollToElement(modifyObjectPencarianDokumen, GlobalVariable.TimeOut)

            if (i == (6 + tambahanColumn)) {
                'Jika kolom dicheck pada Proses TTD, mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan. Yang kedua menjadi total signer'
                prosesTtdPencarianDokumen.add(WebUI.getText(modifyObjectPencarianDokumen).split('/', -1))

                jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign, result[
                    0])

                jumlahSignerHarusTandaTangan = CustomKeywords.'connection.SendSign.getTotalSignerTtd'(conneSign, documentIdBasedOnLogin[
                    c])

                'Pengecekan proses sign'
                arrayMatch.add(WebUI.verifyEqual(jumlahSignerTelahTandaTangan, ((prosesTtdPencarianDokumen[loopingPerDocument])[
                        0]).replace(' ', ''), FailureHandling.CONTINUE_ON_FAILURE))

                'Pengecekan total proses tanda tangan'
                arrayMatch.add(WebUI.verifyEqual(jumlahSignerHarusTandaTangan, ((prosesTtdPencarianDokumen[loopingPerDocument])[
                        1]).replace(' ', ''), FailureHandling.CONTINUE_ON_FAILURE))
            } else if (i == (7 + tambahanColumn)) {
                'Mengambil teks dari UI dan displit berdasarkan proses stamping dan total materai'
                totalMateraiPencarianDokumen.add(WebUI.getText(modifyObjectPencarianDokumen).split('/', -1))

                'Pengecekan proses stamping'
                arrayMatch.add(WebUI.verifyEqual((totalMateraiPencarianDokumen[loopingPerDocument])[0], resultStamping[indexArrayMeterai++], 
                        FailureHandling.CONTINUE_ON_FAILURE))

                'Pengecekan total materai'
                arrayMatch.add(WebUI.verifyEqual((totalMateraiPencarianDokumen[loopingPerDocument])[1], resultStamping[indexArrayMeterai++], 
                        FailureHandling.CONTINUE_ON_FAILURE))
            } else if (i == (variablePencarianDokumenColumn.size() / variablePencarianDokumenRow.size())) {
                'Untuk ke sembilan tidak dibuat pengecekan dikarenakan itu adalah aksi. AKsi tidak dapat dicheck'
            } else if (i == 4) {
                'Jika login sebagai MF'
                if (GlobalVariable.roleLogin != 'Customer') {
                    'Diverifikasi dengan UI didepan'
                    arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    tambahanColumn = 1
                } else {
                    arrayIndex++

                    'Diverifikasi dengan UI didepan'
                    arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[arrayIndex++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            } else if (i == 3) {
				'Diverifikasi dengan UI didepan'
				arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[arrayIndex++], false,
						FailureHandling.CONTINUE_ON_FAILURE))
				
				docunentName = WebUI.getText(modifyObjectPencarianDokumen)
				} else {
                'Diverifikasi dengan UI didepan'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectPencarianDokumen), result[arrayIndex++], false, 
                        FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        loopingPerDocument++
    }
    
    openHamburgAndroid()

    'Klik objek Beranda'
    WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_Beranda'))

    closeHamburgAndroid()

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
    
    WebUI.delay(GlobalVariable.TimeOut)

    'looping berdasarkan jumlah dokumen'
    resultHashMap = loopingMultiDoc(docId, conneSign, refNumber, resultHashMap)

    'variable count total document sebelum dilakukannya view document'
    int countClickedButtonPrevious = 0

    loopingPerDocument = 0

    for (c = 0; c < resultHashMap.get(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]); c++) {
        if ((isViewDocument != 'Yes') || (c == 0)) {
            'get row pada beranda'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))
        } else {
            'Klik button Lastest'
            WebUI.click(modifyObjectBtnLastest, FailureHandling.OPTIONAL)
        }
        
        if (c >= variable.size()) {
            'Agar dapat ke Lastest, modifikasi button Lastest pada paging'
            modifyObjectBtnPrevious = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/btn_Lastest'), 
                'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[2]/a/i', 
                true)

            countClickedButtonPrevious++

            for (loopingClickPrevious = 0; loopingClickPrevious < countClickedButtonPrevious; loopingClickPrevious++) {
                WebUI.click(modifyObjectBtnPrevious)
            }
            
            'get row pada beranda'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

            variable.size() == (variable.size() - c)

            resultHashMap.put(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])], resultHashMap.get(GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[0])]) - c)

            c = 0
        }
        
        'get row pada beranda'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'index row distart dari 2 karena yang 1 adalah button ttd'
        indexRow = 2

        'modify object text refnum'
        modifyObjectTextRefNum = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_refnum'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        'Mengambil teks refnum'
        labelRefNum = WebUI.getText(modifyObjectTextRefNum)

        'modify object text document type'
        modifyObjectTextDocumentType = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipedokumen'), 'xpath', 
            'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div/p', true)

        'modify object text document template name'
        modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_namadokumentemplate'), 
            'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        if (GlobalVariable.roleLogin != 'Customer') {
            'modify object text nama customer'
            modifyObjectTextNamaCustomer = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)
        }
        
        'modify object text tanggal permintaan'
        modifyObjectTextTanggalPermintaan = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        'modify object text proses ttd'
        modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        'modify object text total materai'
        modifyObjectTextTotalMaterai = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 
            'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        'modify object text status TTD'
        modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_Berandaname'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow++) + ']/div', true)

        'modify object button signer'
        modifyObjectBtnSigner = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow) + ']/div/a[4]/em', 
            true)

        'modify object button View Document'
        modifyObjectBtnViewDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow) + ']/div/a[2]/em', 
            true)

        'modify object button Download Doc'
        modifyObjectBtnDownloadDoc = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/btn_signer'), 'xpath', 'equals', 
            ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            (variable.size() - c)) + ']/datatable-body-row/div[2]/datatable-body-cell[') + indexRow) + ']/div/a[3]/em', 
            true)

        WebUI.scrollToElement(modifyObjectTextRefNum, GlobalVariable.TimeOut)

        'verifikasi ref number dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextRefNum), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi doctype dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentType), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verifikasi document template name dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentTemplateName), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))
		
		documentName = WebUI.getText(modifyObjectTextDocumentTemplateName)
		
        if (GlobalVariable.roleLogin != 'Customer') {
            'verifikasi nama customer'
            arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextNamaCustomer), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
        } else {
            arrayIndex++
        }
        
        'verifikasi tanggal permintaan dengan database'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextTanggalPermintaan), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE))

        'tambah 1 array index dari query dikarenakan sebagai tanggal selesai'
        arrayIndex++

        if ((result[arrayIndex++]) == 'Need Sign') {
            statusTtd = 'Belum TTD'
        }
        
        'verifikasi status ttd'
        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextStatusTtd), statusTtd, false, FailureHandling.CONTINUE_ON_FAILURE))

        'Mengambil text mengenai proses tanda tangan dan displit menjadi 2, yang pertama menjadi jumlah signer yang sudah tanda tangan. Yang kedua menjadi total signer'
        ArrayList prosesTtd = WebUI.getText(modifyObjectTextProsesTtd).split('/', -1)

        'verifikasi total signer beranda dan pencarian dokumen'
        arrayMatch.add(WebUI.verifyMatch(prosesTtd.toString(), (prosesTtdPencarianDokumen[loopingPerDocument]).toString(), 
                false, FailureHandling.CONTINUE_ON_FAILURE))

        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + '<') + WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label')) + '>')
        }
        
        'Klik button Signer'
        WebUI.click(modifyObjectBtnSigner)

        'get row popup'
        variableRowPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'get column popup'
        variableColPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

        'loop untuk row popup'
        for (int i = 1; i <= variableRowPopup.size(); i++) {
            'get data kotak masuk send document secara asc, dimana customer no 1'
            ArrayList resultSigner = CustomKeywords.'connection.SendSign.getSignerKotakMasukSendDoc'(conneSign, documentIdBasedOnLogin[
                c], documentName)

            'declare array index menjadi 0 per result'
            arrayIndexSigner = 0

            'loop untuk column popup'
            for (int m = 1; m <= variableColPopup.size(); m++) {
                'modify object text nama, email, signer Type, sudah aktivasi Untuk yang terakhir belum bisa, dikarenakan masih gak ada data (-) Dikarenakan modifynya bukan p di lastnya, melainkan span'
                modifyObjectTextPopup = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/text_tipepopup'), 'xpath', 
                    'equals', ((('/html/body/ngb-modal-window/div/div/app-signer/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + m) + ']/div', true)

                'signer nama,email,signerType,sudahAktivasi popup'
                arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectTextPopup), resultSigner[arrayIndexSigner++], 
                        false, FailureHandling.CONTINUE_ON_FAILURE))
            }
        }
        
        'focus klik x'
        WebUI.focus(findTestObject('KotakMasuk/btn_X'))

        'Klik x terlebih dahulu pada popup'
        WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_X'))

        isDownloadDocument = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need Download Document ?'))

        isDeleteDownloadedDocument = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Delete Downloaded Document'))

        isViewDocument = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need View Document ?'))

        'Jika document ingin didownload, maka'
        if (isDownloadDocument == 'Yes') {
            'Klik download file'
            WebUI.click(modifyObjectBtnDownloadDoc)

            'Kasih waktu 4 detik untuk proses download'
            WebUI.delay(4)

            'Check apakah sudah terddownload menggunakan custom keyword'
            CustomKeywords.'customizekeyword.Download.isFileDownloaded'(isDeleteDownloadedDocument)
        }
        
        'Jika is View Document yes, maka '
        if (isViewDocument == 'Yes') {
            'ambil count total document sebelum diview'
            countTotalDocumentBeforeView = variable.size()

            'Klik View Document'
            WebUI.click(modifyObjectBtnViewDoc)

            'Jika error lognya muncul'
            if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'ambil teks errormessage'
                errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                'Tulis di excel itu adalah error'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + errormessage) + '>')
            }
            
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
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedProcessNotDone) + ' untuk proses View dokumen tanda tangan. ')
            }
        }
        
        loopingPerDocument++
    }
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + 
        ' pada Menu Kotak Masuk ')
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

def loopingMultiDoc(ArrayList docId, Connection conneSign, String refNumber, LinkedHashMap resultHashMap) {
    'looping document id'
    for (loopingDocument = 0; loopingDocument < docId.size(); loopingDocument++) {
        'Mengambil email berdasarkan documentId'
        ArrayList emailSigner = CustomKeywords.'connection.SendSign.getEmailLogin'(conneSign, docId[loopingDocument]).split(
            ';', -1)

        'ambil vendor berdasarkan document id'
        String vendor = CustomKeywords.'connection.SendSign.getVendorCodeUsingDocId'(conneSign, docId[loopingDocument])

        'get looping email signer'
        for (loopingEmailSigner = 0; loopingEmailSigner < emailSigner.size(); loopingEmailSigner++) {
            'jika vendor privy, maka cehcking apakah signer tersebut autosign. Jika autosign ,maka continue'
            if (vendor.equalsIgnoreCase('Privy')) {
                ifSignerAuto = CustomKeywords.'connection.APIFullService.getIfSignerAutosign'(conneSign, docId[loopingDocument], 
                    emailSigner[loopingEmailSigner])

                if (ifSignerAuto == 'Autosign') {
                    continue
                }
            }
            
            'get total document based on signer'
            count = CustomKeywords.'connection.SendSign.getTotalDocumentBasedOnSigner'(conneSign, refNumber, emailSigner[loopingEmailSigner])

            'jika countnya lebih dari 0'
            if (count > 0) {
                'jika result hashmapnya sizenya 0, maka langsung input email beserta count'
                if (resultHashMap.keySet().size() == 0) {
                    resultHashMap.put(emailSigner[loopingEmailSigner], count)
                } else {
                    'looping result hash map'
                    for (q = 0; q < resultHashMap.keySet().size(); q++) {
                        'jika result hash map tidak sesuai dengan email signer'
                        if ((resultHashMap.keySet()[q]) != (emailSigner[loopingEmailSigner])) {
                            'result hash map akan diinput dengan email yang berbeda beserta count'
                            resultHashMap.put(emailSigner[loopingEmailSigner], count)
                        }
                    }
                }
            }
        }
    }
    resultHashMap
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