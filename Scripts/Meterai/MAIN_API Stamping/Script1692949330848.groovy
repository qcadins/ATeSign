import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathStamping).columnNumbers

'looping API stamping'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIDownload, GlobalVariable.NumofColm, 20)

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        saldoBefore = loginAdminGetSaldo('Yes')

        GlobalVariable.FlagFailed = 0

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
        
        'HIT API stamping'
        respon = WS.sendRequest(findTestObject('Postman/Stamping', [('callerId') : findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('refNumber') : findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika code 0'
            if (code == 0) {
                'looping dari 1 hingga 6'
                for (i = 1; i <= 6; i++) {
                    'mengambil value db proses ttd'
                    int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, findTestData(excelPathStamping).getValue(
                            GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"', ''))

                    'jika proses materai gagal (51)'
                    if ((prosesMaterai == 51) || (prosesMaterai == 61)) {
                        'Diberikan delay 3 detik untuk update error message pada db'
                        WebUI.delay(3)

                        'get reason gailed error message untuk stamping'
                        errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, findTestData(excelPathStamping).getValue(
                                GlobalVariable.NumofColm, rowExcel('refNumber')))

                        'Write To Excel GlobalVariable.StatusFailed and errormessage'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (GlobalVariable.ReasonFailedProsesStamping + ' dengan alasan ') + 
                            errorMessageDB.toString())

                        GlobalVariable.FlagFailed = 1

                        break
                    } else if ((prosesMaterai == 53) || (prosesMaterai == 63)) {
                        'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                        WebUI.delay(3)

                        'Mengambil value total stamping dan total meterai'
                        ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')).replace(
                                '"', ''))

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

                        ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, 
                            findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')), 'Stamping')

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
                                GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                            GlobalVariable.FlagFailed = 1
                        }
                        
                        break
                    } else {
                        'Jika bukan 51 dan 51, maka diberikan delay 10 detik'
                        WebUI.delay(10)

                        'Jika looping berada di akhir, tulis error failed proses stamping'
                        if (i == 6) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu 60 detik ')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
                
                'Jika flag failed tidak 0'
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    'Call verify meterai'
                    WebUI.callTestCase(findTestCase('Meterai/verifyMeterai'), [('excelPathMeterai') : excelPathStamping, ('sheet') : sheet
                            , ('noKontrak') : findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'refNumber')).replace('"', '')], FailureHandling.CONTINUE_ON_FAILURE)

                    saldoAfter = loginAdminGetSaldo('No')

                    if (saldoBefore == saldoAfter) {
                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                            ' terhadap total saldo ')
                    } else {
                        verifySaldoUsed(conneSign)
                    }
                }
            } else {
                'Jika code bukan 0, mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + message) + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + message) + '>')
        }
    }
}

def loginAdminGetSaldo(String start) {
    String totalSaldo

    if (start == 'Yes') {
        'call test case login per case'
        WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathStamping, ('Email') : 'Email Login'
                , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)
    }
    
    'klik button saldo'
    WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

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
    totalSaldo
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def verifySaldoUsed(Connection conneSign) {
    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), 'Stamp Duty')

    'enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ARROW_DOWN))

    'enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipeSaldo'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
            rowExcel('refNumber')).replace('"', ''))

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))

    'get column di saldo'
    variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

    'get row di saldo'
    variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

    'ambil inquiry di db'
    ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, findTestData(excelPathStamping).getValue(
            GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"', ''))

    index = 0

	'looping mengenai rownnya'
    for (int p = 1; p <= variableSaldoRow.size(); p++) {
        'looping mengenai columnnya'
        for (int u = 1; u <= (variableSaldoColumn.size()); u++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                p) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

            'Jika u di lokasi qty atau kolom ke 9'
            if (u == 10) {
                'Jika yang qtynya 1 dan databasenya juga, berhasil'
                if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[index]) == '-1')) {
                    'Jika bukan untuk 2 kolom itu, maka check ke db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                        findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')).replace(
                            '"', ''))
                } else {
                    'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                    GlobalVariable.FlagFailed = 1

                    'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                        findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')).replace(
                            '"', ''))

                    index++
                }
            } else if (u == (variableSaldoColumn.size() / variableSaldoRow.size())) {
                'Jika di kolom ke 10, atau di FE table saldo'
                checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(WebUI.getText(modifyperrowpercolumn)), p, FailureHandling.CONTINUE_ON_FAILURE), 
                    'pada Saldo di Mutasi Saldo dengan nomor kontrak ' + findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                        rowExcel('refNumber')).replace('"', ''))
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false, 
                        FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"', ''))

                index++
            }
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

