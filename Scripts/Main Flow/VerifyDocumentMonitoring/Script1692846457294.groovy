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

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()	

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

ArrayList<String> nomorKontrakPerPilihan = []

String settingHO = ''

'Jika nomor Kontrak kosong'
if (nomorKontrak == '') {
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'API Send Document External') {
        'Mengambil nomor kontrak dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo (Send External)')).replace(
            '"', '')
    } else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'API Send Document Normal') {
        'Mengambil nomor kontrak dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo (Send Normal)')).replace(
            '"', '')
    } else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
    'Manual Sign') {
        'Mengambil documen id dari excel'
        nomorKontrak = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen (Send Manual)'))
    }
}

'mengambil hasil split dari totalLoop'
nomorKontrakPerPilihan = nomorKontrak.split(';', -1).toUnique()

documentId = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

'Looping per document'
for (y = 0; y < nomorKontrakPerPilihan.size(); y++) {
    if (isStamping == 'Yes') {
        saldoBefore = loginAdminGetSaldo(conneSign, 'No', sheet)
    }
    
    if (linkDocumentMonitoring == 'Not Used') {
        openHamburgAndroid()

        'Fokus ke document monitoring'
        WebUI.focus(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        'Klik Button menu Document Monitoring'
        WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        closeHamburgAndroid()

        linkDocumentMonitoring = ''
    } else if (linkDocumentMonitoring == '') {
        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excelPathFESignDocument, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)

        'Fokus ke document monitoring'
        WebUI.focus(findTestObject('DocumentMonitoring/DocumentMonitoring'))

        'Klik Button menu Document Monitoring'
        WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))
    } else {
        'check if ingin menggunakan embed atau tidakk'
        if (GlobalVariable.RunWithEmbed == 'Yes') {
            settingHO = (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('isHO')).split(
                ';', -1)[GlobalVariable.indexUsed])

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

                break
            }
        } else if (GlobalVariable.RunWithEmbed == 'No') {
            'navigate url ke daftar akun'
            WebUI.openBrowser(linkDocumentMonitoring)

            WebUI.maximizeWindow()
        }
    }
    
    'inisialisasi emailSigner menjadi array list'
    ArrayList<String> emailSigner = []

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

    inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)

    if (linkDocumentMonitoring == '') {
        modifyObjectvalues = findTestObject('DocumentMonitoring/lbl_Value')

        'Mengambil row size dari value'
        sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'Mengambil column size dari value'
        sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

        'Jika valuenya ada'
        if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
            'scroll menuju values element'
            WebUI.scrollToElement(modifyObjectvalues, GlobalVariable.TimeOut)

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

                    'Jika berada di column ke 7'
                    if (i == 7) {
                        'Split teks proses TTD'
                        totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

                        if (vendor.equalsIgnoreCase('Digisign')) {
                            jumlahSignerTelahTandaTangan = CustomKeywords.'connection.APIFullService.getUserAlreadySigned'(
                                conneSign, nomorKontrakPerPilihan[y])
                        } else if (vendor.equalsIgnoreCase('Privy')) {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgressPrivy'(conneSign,
								documentId[j-1])
							} else {
                            jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign, 
                                documentId[j-1])
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
                        'Jika berada di column ke 8'

                        'Split teks total Stamping'
                        totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

                        'looping berdasarkan total split dan diverif berdasarkan db.'
                        for (k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
                            'Verifikasi UI dengan db'
                            arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[arrayIndexStamping++], 
                                    FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    } else if (i == 10) {
                    } else {
                        'Selain di column 7 dan 8 maka akan diverif dengan db.'
                        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                }
            }
			
			actionDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y])
        } else {
            'Jika tidak ada, maka datanya tidak ada di UI.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

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

            'scroll menuju values element'
            WebUI.scrollToElement(modifyObjectvalues, GlobalVariable.TimeOut)

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

                    'Jika berada di column ke 7'
                    if (i == 7) {
                        'Split teks proses TTD'
                        totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)
						
						if (vendor.equalsIgnoreCase('Digisign')) {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.APIFullService.getUserAlreadySigned'(
								conneSign, nomorKontrakPerPilihan[y])
						} else if (vendor.equalsIgnoreCase('Privy')) {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgressPrivy'(conneSign,
								documentId[j-1])
							} else {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign,
								documentId[j-1])
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
                    } else {
                        'Selain di column 7 dan 8 maka akan diverif dengan db.'
                        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                }
            }
        } else {
            'Jika tidak ada, maka datanya tidak ada di UI.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

            GlobalVariable.FlagFailed = 1
        }
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
            inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)

            'lakukan pengecekan ke di UI untuk memastikan cancel berhasil'
            checkVerifyEqualorMatch(WebUI.verifyMatch('Tidak ada data untuk diperlihatkan', WebUI.getText(findTestObject(
                            'DocumentMonitoring/SearchResult'), FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), 
                'Data gagal terhapus di FE')
        }
        
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
                for (i = 1; i <= 12; i++) {
                    'mengambil value db proses meterai'
                    int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakPerPilihan[
                        y])

                    'jika proses materai gagal (51)/(61)'
                    if (((prosesMaterai == 51) || (prosesMaterai == 61))) {
                        'Kasih delay untuk mendapatkan update db untuk error stamping'
                        WebUI.delay(3)

                        'get reason gailed error message untuk stamping'
                        errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakPerPilihan[
                            y])

                        'Write To Excel GlobalVariable.StatusFailed and errormessage'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                        errorMessageDB.toString()

                        GlobalVariable.FlagFailed = 1

                        break
                    } else if (((prosesMaterai == 53) || (prosesMaterai == 63))) {
                        'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                        WebUI.delay(3)

                        'Mengambil value total stamping dan total meterai'
                        ArrayList<String> totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 10))

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

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
                        WebUI.delay(10)

                        'Jika looping berada di akhir, tulis error failed proses stamping'
                        if (i == 12) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + 
                                (i * 12)) + ' detik ')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
            }
        }
        
        inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)

        modifyObjectvalues = findTestObject('DocumentMonitoring/lbl_Value')

        'Mengambil row size dari value'
        sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'Mengambil column size dari value'
        sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        'Jika valuenya ada'
        if (WebUI.verifyElementPresent(modifyObjectvalues, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
            'scroll menuju values element'
            WebUI.scrollToElement(modifyObjectvalues, GlobalVariable.TimeOut)

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
                for (i = 1; i <= ((sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()) - 1); i++) {
                    'modify object label Value'
                    modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 
                        'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)

                    'Jika berada di column ke 7'
                    if (i == 7) {
                        'Split teks proses TTD'
                        totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)

						if (vendor.equalsIgnoreCase('Digisign')) {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.APIFullService.getUserAlreadySigned'(
								conneSign, nomorKontrakPerPilihan[y])
						} else if (vendor.equalsIgnoreCase('Privy')) {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgressPrivy'(conneSign,
								documentId[j-1])
							} else {
							jumlahSignerTelahTandaTangan = CustomKeywords.'connection.SendSign.getProsesTtdProgress'(conneSign,
								documentId[j-1])
						}
                        'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
                        arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahSignerTelahTandaTangan, FailureHandling.CONTINUE_ON_FAILURE))

                        arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(), FailureHandling.CONTINUE_ON_FAILURE))
                    } else if (i == 8) {
                        'Jika berada di column ke 8'

                        'Split teks total Stamping'
                        totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)

                        'looping berdasarkan total split dan diverif berdasarkan db.'
                        for (k = 0; k < totalStampingAndTotalMaterai.size(); k++) {
                            'Verifikasi UI dengan db'
                            arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[k], resultStamping[k], FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    } else if (i == 10) {
                    } else {
                        'Selain di column 7 dan 8 maka akan diverif dengan db.'
                        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                }
            }
        } else {
            'Jika tidak ada, maka datanya tidak ada di UI.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')

            GlobalVariable.FlagFailed = 1
        }
        
        saldoAfter = loginAdminGetSaldo(conneSign, 'Yes', sheet)

        'mengambil value db proses meterai'
        int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakPerPilihan[y])

        'jika proses meterai tidak Failed'
        if (((prosesMaterai != 51) || (prosesMaterai != 61))) {
            'jika saldo before sama dengan saldo after'
            if (saldoBefore == saldoAfter) {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')
            } else {
                verifySaldoUsed(conneSign, sheet, nomorKontrak, prosesMaterai)
            }
        } else {
            if (saldoBefore != saldoAfter) {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai tidak sama ')
            }
        }
    }
    
    if (CancelDocsStamp == 'Yes') {
        'panggil fungsi cancel docs sesudah send'
        if (cancelDoc(conneSign, (nomorKontrakPerPilihan[y]).toString()) == true) {
            'input kembali data sesudah selesai cancel doc'
            inputDocumentMonitoring(conneSign, nomorKontrakPerPilihan[y], linkDocumentMonitoring, settingHO)

            'lakukan pengecekan ke di UI untuk memastikan cancel berhasil'
            checkVerifyEqualorMatch(WebUI.verifyMatch('Tidak ada data untuk diperlihatkan', WebUI.getText(findTestObject(
                            'DocumentMonitoring/SearchResult'), FailureHandling.OPTIONAL), false, FailureHandling.CONTINUE_ON_FAILURE), 
                'Data gagal terhapus di FE')
        }
        
        break
    }
}

def loginAdminGetSaldo(Connection conneSign, String start, String sheet) {
    String totalSaldo

    if (start == 'Yes') {
        openHamburgAndroid()

        'klik button saldo'
        WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

        closeHamburgAndroid()
    }
    
    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (c = 1; c <= variableDivSaldo.size(); c++) {
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

def verifySaldoUsed(Connection conneSign, String sheet, String nomorKontrak, int prosesMaterai) {
	'deklarasi array inquiryDB'
	ArrayList inquiryDB = []
	
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, nomorKontrak)

    documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, nomorKontrak)

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
    WebUI.setText(findTestObject('Saldo/input_refnumber'), nomorKontrak)

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

    if (prosesMaterai == 63) {
		'ambil inquiry di db'
		inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeteraiPrivy'(conneSign, nomorKontrak)
	} else {
		'ambil inquiry di db'
		inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, nomorKontrak)
	}

    index = 0

    for (p = 1; p <= variableSaldoRow.size(); p++) {
        'looping mengenai columnnya'
        for (u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                p) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

            'Jika u di lokasi qty atau kolom ke 9'
            if (u == 9) {
                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index]) == '-1')) {
                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                        nomorKontrak)

                    index++
                } else {
                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                    GlobalVariable.FlagFailed = 1

                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (GlobalVariable.ReasonFailedSignGagal + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                        nomorKontrak)

                    index++
                }
            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                'Jika di kolom ke 10, atau di FE table saldo'
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false, 
                FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + nomorKontrak)
					
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

    'Set text mengenai teks customer'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), inputDocumentMonitoring[arrayIndex++], FailureHandling.OPTIONAL)

    'Set text mengneai input nomor kontrak'
    WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), nomorKontrakPerPilihan)

    'Set text mengenai tanggal permintaan dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), inputDocumentMonitoring[arrayIndex++])

    'Set text mengenai tanggal selesai dari'
    WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), inputDocumentMonitoring[(arrayIndex - 1)])

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
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

        'Set text mengenai input cabang'
        WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), inputDocumentMonitoring[arrayIndex++], FailureHandling.OPTIONAL)

        'Enter'
        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))
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
	
	if (linkDocumentMonitoring == '') {
		'modify object test status tanda tangan di beranda'
		modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'),
		'xpath', 'equals', ("//*[@id = 'stampingStatus']/div/div/div[3]/input"), true)
	} else if (linkDocumentMonitoring.contains('embed/V2/inquiry?')) {
		'modify object test status tanda tangan di beranda'
		modifyObjectProsesMeterai = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/input_prosesMeterai'),
		'xpath', 'equals', ("//*[@id='prosesMaterai']/div/div/div[3]/input"), true)
	}
	'Set text mengenai input cabang'
	WebUI.setText(modifyObjectProsesMeterai, inputDocumentMonitoring[arrayIndex++])
    
	'Enter'
	WebUI.sendKeys(modifyObjectProsesMeterai, Keys.chord(Keys.ENTER))

    'Klik enter Cari'
    WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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
    
    'jika status text adalah complete'
    if (WebUI.getText(findTestObject('DocumentMonitoring/lblTable_status'), FailureHandling.OPTIONAL).equalsIgnoreCase('Complete')) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + 'Cancel Document tidak bisa karena status Complete')

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
                    conneSign, nomorKontrakPerPilihan), false, FailureHandling.CONTINUE_ON_FAILURE), 'Pengecekan ke DB Cancel Doc gagal')

        return true
    } else {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + '<') + popupMsg) + '>')

        GlobalVariable.FlagFailed = 1
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

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def actionDocumentMonitoring(Connection conneSign, String nomorKontrakPerPilihan) {
	'click button view signer'
	WebUI.click(findTestObject('DocumentMonitoring/button_Signer'))
	
	'get row popup'
	variableRowPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

	'get column popup'
	variableColPopup = DriverFactory.webDriver.findElements(By.cssSelector('body > ngb-modal-window > div > div > app-signer > div.modal-body > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row datatable-body-cell'))

	ArrayList arrayMatch = []
	'loop untuk row popup'
	for (int i = 1; i <= variableRowPopup.size(); i++) {
		'Input email signer based on sequentialnya'
		emailSignerBasedOnSequence = CustomKeywords.'connection.APIFullService.getEmailBasedOnSequence'(conneSign, nomorKontrakPerPilihan).split(';', -1)

		'get data kotak masuk send document secara asc, dimana customer no 1'
		ArrayList resultSigner = CustomKeywords.'connection.SendSign.getSignerKotakMasukSendDoc'(conneSign, nomorKontrakPerPilihan, emailSignerBasedOnSequence[(i - 1)])

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

	if (arrayMatch.contains(false)) {
		'Write To Excel GlobalVariable.StatusFailed and errorLog'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
				rowExcel('Reason Failed')) + ';')) + ' Kesalahan view Signer pada Document Monitoring'))
	}
	
	'focus klik x'
	WebUI.focus(findTestObject('KotakMasuk/btn_X'))

	'Klik x terlebih dahulu pada popup'
	WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_X'))
	
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need Download Document ?')) ==
		'Yes') {
			'click button download'
			WebUI.click(findTestObject('DocumentMonitoring/button_Download'))
	
			'check if error alert present'
			if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
				'get reason dari error log'
				errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)
	
				'Write To Excel GlobalVariable.StatusFailed and errorLog'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('Reason Failed')) + ';') + '<') + errorLog) + '>')
	
				GlobalVariable.FlagFailed = 1
			}
			
			'check isfiled downloaded'
			if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathFESignDocument).getValue(
					GlobalVariable.NumofColm, rowExcel('Delete Downloaded Document'))) == false) {
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
						rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedDownload)
	
				GlobalVariable.FlagFailed = 1
			}
		}
		
	if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Need View Document ?')) ==
	'Yes') {
		'click button view dokumen'
		WebUI.click(findTestObject('DocumentMonitoring/button_View'))

		'check if error alert present'
		if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'get reason dari error log'
			errorLog = WebUI.getAttribute(findTestObject('DocumentMonitoring/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

			'Write To Excel GlobalVariable.StatusFailed and errorLog'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
					rowExcel('Reason Failed')) + ';') + '<') + errorLog) + '>')

			GlobalVariable.FlagFailed = 1
		} else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/label_NoKontrakView'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			'check if no kontrak sama dengan inputan excel'
			checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('DocumentMonitoring/label_NoKontrakView')),
					'No Kontrak ' + (nomorKontrakPerPilihan), false, FailureHandling.CONTINUE_ON_FAILURE), ' dokumen yang di view berbeda')

			'click button Kembali'
			WebUI.click(findTestObject('DocumentMonitoring/button_Kembali'))
		} else if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/button_View'), GlobalVariable.TimeOut,
			FailureHandling.OPTIONAL)) {
			checkVerifyEqualOrMatch(false, ' button view tidak berfungsi')
		}
	}
}
