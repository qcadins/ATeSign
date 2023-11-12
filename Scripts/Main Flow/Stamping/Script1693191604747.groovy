import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import java.sql.Connection as Connection
import java.util.concurrent.ConcurrentHashMap.KeySetView as KeySetView
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

documentId = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

int prosesMaterai

'mengambil value db proses ttd'
prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, refNumber)

    'ubah vendor stamping jika diperlukan '
    if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')).length() > 
    0) && (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')) != 
    'No')) {
        'ambil idLov untuk diupdate secara otomatis ke DB'
        int idLov = CustomKeywords.'connection.ManualStamp.getIdLovVendorStamping'(conneSign, findTestData(excelPathStamping).getValue(
                GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))

        'lakukan update vendor stamping yang akan dipakai'
        CustomKeywords.'connection.UpdateData.updateVendorStamping'(conneSign, idLov)
    }
    
    String valueRefNum

    String nomorKontrakDocument

    'pasang flag error DMS'
    int flagErrorDMS = 0

    GlobalVariable.base_url = findTestData('Login/Setting').getValue(7, 2)

    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

    HashMap<String, String> getSaldo = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathStamping
            , ('sheet') : sheet, ('vendor') : vendor, ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

    saldoBefore = getSaldo.get('Meterai')

    if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'API Stamping External') || (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'API Stamping Normal')) {
        'penggunaan versi 3.1.0 atau 3.0.0 dengan 1 api yang sama'
        if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
        'API Stamping External') {
            'set base url menjadi v.3.1.0'
            GlobalVariable.base_url = (GlobalVariable.base_url + '/services/external/document/requestStamping')

            'setting value body di api berdasarkan versi stamp nya'
            valueRefNum = '"refNumber"'
        } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
        'API Stamping Normal') {
            'set base url menjadi v.3.0.0'
            GlobalVariable.base_url = (GlobalVariable.base_url + '/services/confins/stampduty/attachMeteraiPajakku')

            'setting value body di api berdasarkan versi stamp nya'
            valueRefNum = '"agreementNo"'
        }
        
        nomorKontrakDocument = (('"' + refNumber) + '"')

        callerId = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('callerId (API Stamping External and API Stamping Normal)'))

        'get api key dari db'
        GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)

        'HIT API stamping'
        respon = WS.sendRequest(findTestObject('Flow Stamping', [('callerId') : callerId, ('valueRefNum') : valueRefNum, ('refNumber') : nomorKontrakDocument]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika code 0'
            if (code == 0) {
                'looping dari 1 hingga 12'
                for (i = 1; i <= 12; i++) {
                    'mengambil value db proses ttd'
                    prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakDocument.replace(
                            '"', ''))

                    'jika proses materai gagal (51)'
                    if (((prosesMaterai == 51) || (prosesMaterai == 61)) && (flagErrorDMS == 0)) {
                        'Kasih delay untuk mendapatkan update db untuk error stamping'
                        WebUI.delay(3)

                        'get reason gailed error message untuk stamping'
                        errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakDocument.replace(
                                '"', ''))

                        'Write To Excel GlobalVariable.StatusFailed and errormessage'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                            errorMessageDB.toString())

                        GlobalVariable.FlagFailed = 1

                        if (!(errorMessageDB.toString().contains('upload DMS'))) {
                            break
                        } else {
                            flagErrorDMS = 1

                            continue
                        }
                    } else if (((prosesMaterai == 53) || (prosesMaterai == 63)) || (flagErrorDMS == 1)) {
                        'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                        WebUI.delay(3)

                        'Mengambil value total stamping dan total meterai'
                        totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, nomorKontrakDocument.replace('"', ''))

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

                        'jika data db tidak bertambah'
                        if (arrayMatch.contains(false)) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                            GlobalVariable.FlagFailed = 1
                        } else {
                            GlobalVariable.FlagFailed = 0
                        }
                        
                        break
                    } else {
                        'Jika bukan 51/61 dan 53/63, maka diberikan delay 20 detik'
                        WebUI.delay(10)

                        'Jika looping berada di akhir, tulis error failed proses stamping'
                        if (i == 12) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + 
                                (i * 12)) + ' detik ')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
                
                'Jika flag failed tidak 0'
                if (GlobalVariable.FlagFailed == 0) {
                    if (flagErrorDMS == 1) {
                        'write to excel Failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                    
                    'Mengambil value total stamping dan total meterai'
                    totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(conneSign, 
                        nomorKontrakDocument.replace('"', ''))

                    if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63)) {
                        'Call verify meterai'
                        WebUI.callTestCase(findTestCase('Main Flow/verifyMeterai'), [('excelPathMeterai') : excelPathStamping
                                , ('sheet') : sheet, ('noKontrak') : nomorKontrakDocument.replace('"', ''), ('linkDocumentMonitoring') : linkDocumentMonitoring
                                , ('CancelDocsStamp') : CancelDocsStamp], FailureHandling.CONTINUE_ON_FAILURE)
                    }
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'Start Stamping') {
        'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
        WebUI.callTestCase(findTestCase('Main Flow/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathStamping
                , ('sheet') : sheet, ('linkDocumentMonitoring') : 'Not Used', ('nomorKontrak') : refNumber, ('isStamping') : 'Yes'
                , ('CancelDocsStamp') : CancelDocsStamp], FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    getSaldo = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathStamping, ('sheet') : sheet, ('vendor') : vendor
            , ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

    saldoAfter = getSaldo.get('Meterai')

    'mengambil value db proses ttd'
    prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, refNumber)

    if ((prosesMaterai == 53) || (prosesMaterai == 63)) {
        if (WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter), FailureHandling.OPTIONAL)) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')
        } else {
            verifySaldoUsed(conneSign, sheet, refNumber, prosesMaterai)
        }
    } else if ((prosesMaterai == 51) || (prosesMaterai == 61)) {
        if (saldoBefore != saldoAfter) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai tidak sama ')
        }
    }


def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def verifySaldoUsed(Connection conneSign, String sheet, String refNumber, int prosesMaterai) {
    'deklarasi array inquiryDB'
    ArrayList inquiryDB = []

    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, refNumber)

    documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, refNumber)

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
    WebUI.setText(findTestObject('Saldo/input_refnumber'), refNumber)

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
        inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeteraiPrivy'(conneSign, refNumber)
    } else {
        'ambil inquiry di db'
        inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, refNumber)
    }
    
    index = 0

    for (int p = 1; p <= variableSaldoRow.size(); p++) {
        'looping mengenai columnnya'
        for (int u = 1; u <= (variableSaldoColumn.size() / variableSaldoRow.size()); u++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                p) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

            WebUI.scrollToElement(modifyperrowpercolumn, GlobalVariable.TimeOut)

            'Jika u di lokasi qty atau kolom ke 9'
            if (u == 9) {
                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index++]) == '-1')) {
                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                        refNumber)

                } else {
                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                    GlobalVariable.FlagFailed = 1

                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                        refNumber)

                }
            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                'Jika di kolom ke 10, atau di FE table saldo'
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index++], false, 
                        FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + refNumber)

            }
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + '<') + 
        message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

