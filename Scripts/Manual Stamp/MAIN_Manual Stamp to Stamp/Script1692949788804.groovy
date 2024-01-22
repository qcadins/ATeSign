import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare untuk split array excel dan beberapa variable yang dibutuhkan'
semicolon = ';'

splitIndex = -1

int firstRun = 0

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathManualStamptoStamp).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'get tenant dari excel percase'
        GlobalVariable.Tenant = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'get psre dari excel percase'
        GlobalVariable.Psre = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'ubah vendor stamping jika diperlukan'
        if ((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')).length() > 
        0) && (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')) != 
        'No')) {
            'ambil idLov untuk diupdate secara otomatis ke DB'
            int idLov = CustomKeywords.'connection.ManualStamp.getIdLovVendorStamping'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))

            'lakukan update vendor stamping yang akan dipakai'
            CustomKeywords.'connection.UpdateData.updateVendorStamping'(conneSign, idLov)
        }
        
        if ((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
        findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 
        0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathManualStamptoStamp
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                inputCancel(conneSign)
            }
            
            firstRun = 1
        }
        
        'Inisialisasi array dan variable'
        indexEmail = 0

        'Inisialisasi variable yang dibutuhkan'
        totalMeterai = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Jumlah Meterai'))

        pindahkanSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Pindahkan SignBox')).split(
            semicolon, splitIndex)

        lokasiSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Lokasi Pemindahan signbox')).split(
            '\\n', splitIndex)

        lockSignBox = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Lock Sign Box')).split(
            semicolon, splitIndex)

        catatanStamping = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Catatan Stamping')).split(
            semicolon, splitIndex)

        saldoBefore = loginAdminGetSaldo('No', sheet)

        settingzoom()

        if (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'Pengecekan apakah masuk page Manual Stamp'
        if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_stampingMeterai'), GlobalVariable.TimeOut)) {
            'Input form yang ada pada page'
            inputForm()

            if (checkErrorLog() == true) {
                continue
            }
            
            if (Integer.parseInt(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Is Mandatory Complete'))) > 
            0) {
                'write to excel bahwa save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)

                GlobalVariable.FlagFailed = 1

                continue
            } else if (WebUI.verifyElementHasAttribute(findTestObject('ManualStamp/button_Selanjutnya'), 'disabled', GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'write to excel bahwa save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedSaveGagal)

                GlobalVariable.FlagFailed = 1

                continue
            } else {
                'klik button selanjutnya'
                WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))

                'jika konfirmasi ada pada page tersebut'
                if (WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_KonfirmasiNext'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'klik button back'
                    WebUI.click(findTestObject('ManualStamp/button_BackNext'))

                    'verifikasi button selanjutnya ada pada page'
                    WebUI.verifyElementPresent(findTestObject('ManualStamp/button_Selanjutnya'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)

                    'klik button selanjutnya'
                    WebUI.click(findTestObject('ManualStamp/button_Selanjutnya'))

                    'klik button next'
                    WebUI.click(findTestObject('ManualStamp/button_Next'))

                    'jika element dokumen input ada'
                    if (WebUI.verifyElementPresent(findTestObject('ManualStamp/input_documentNo'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)) {
                        'check ui dan excel pada nomor dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentNo'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$Nomor Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nomor dokumen ')

                        'check ui dan excel pada nama dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentName'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$Nama Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama dokumen ')

                        'check ui dan excel pada tipe dokumen'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docType'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$Tipe Dokumen')), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi tipe dokumen ')

                        'check ui dan excel pada tipe dokumen peruri'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/lbl_docTypePeruri'), 
                                    'value'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$Tipe Dokumen Peruri')), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi tipe dokumen peruri ')

                        'Klik button tanda tangan'
                        WebUI.click(findTestObject('ManualStamp/btn_meterai'))

                        'click button lock signbox'
                        WebUI.click(findTestObject('ManualStamp/btn_LockSignBox'))

                        'check isi catatan stamping ada pada page tersebut'
                        WebUI.verifyElementPresent(findTestObject('ManualStamp/lbl_isiCatatanStamping'), GlobalVariable.TimeOut, 
                            FailureHandling.CONTINUE_ON_FAILURE)

                        'set text catatan stamping'
                        WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[0])

                        'klik batal catatan stamping'
                        WebUI.click(findTestObject('ManualStamp/button_batalCatatanStamping'))

                        'click lock signbox'
                        WebUI.click(findTestObject('ManualStamp/btn_LockSignBox'))

                        WebUI.delay(2)

                        'check isi catatan stamping kosong'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch('', WebUI.getText(findTestObject('ManualStamp/input_isiCatatanStamping')), 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada catatan meterai ')

                        'isi catatan stamping'
                        WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[0])

                        'klik simpan catatan stamping'
                        WebUI.click(findTestObject('ManualStamp/button_SimpanCatatanStamping'))

                        'check is locked'
                        isLocked = WebUI.getAttribute(findTestObject('ManualStamp/btn_LockSignBox'), 'class', FailureHandling.STOP_ON_FAILURE)

                        if ((catatanStamping[0]) != '') {
                            'verify sign box is locked'
                            checkVerifyEqualOrMatch(WebUI.verifyMatch(isLocked, 'fa fa-2x fa-lock', false, FailureHandling.CONTINUE_ON_FAILURE), 
                                ' pada sign locked ')
                        }
                        
                        'Klik button Delete'
                        WebUI.click(findTestObject('ManualStamp/btn_DeleteSignBox'))

                        'verify sign box is deleted'
                        checkVerifyEqualOrMatch(WebUI.verifyElementNotPresent(findTestObject('ManualSign/signBox'), GlobalVariable.TimeOut, 
                                FailureHandling.CONTINUE_ON_FAILURE), ' pada deletenya box Sign ')

                        'looping berdasarkan total tanda tangan'
                        for (int j = 1; j <= Integer.parseInt(totalMeterai); j++) {
                            'Klik button tanda tangan'
                            WebUI.click(findTestObject('Object Repository/ManualStamp/btn_meterai'))

                            'modify label tipe tanda tangan di kotak'
                            modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/ManualSign/modifyObject'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                j) + ']/div/div/small', true)

                            'Verifikasi antara excel dan UI, apakah tipenya sama'
                            WebUI.verifyMatch('Meterai', WebUI.getText(modifyobjectTTDlblRoleTandaTangan), false)

                            'Verify apakah tanda tangannya ada'
                            if (WebUI.verifyElementPresent(modifyobjectTTDlblRoleTandaTangan, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                                'check if signbox mau dipindahkan'
                                if ((pindahkanSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                                    'memindahkan sign box'
                                    CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        j) + ']/div', lokasiSignBox[(j - 1)])
                                }
                                
                                'check if signbox mau dilock posisinya'
                                if ((lockSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                                    'modify obejct lock signbox'
                                    modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('ManualSign/btn_LockSignBox'), 
                                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        j) + ']/div/button[1]/span', true)

                                    'click lock signbox'
                                    WebUI.click(modifyobjectLockSignBox)

                                    WebUI.setText(findTestObject('ManualStamp/input_isiCatatanStamping'), catatanStamping[
                                        (j - 1)])

                                    WebUI.click(findTestObject('ManualStamp/button_SimpanCatatanStamping'))
                                }
                            }
                        }
                        
                        'klik simpan'
                        WebUI.click(findTestObject('ManualStamp/button_Simpan'))

                        if (checkErrorLog() == true) {
                            continue
                        }
                        
                        if ((GlobalVariable.FlagFailed == 0) || findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Status')).equalsIgnoreCase('Warning')) {
                            if (GlobalVariable.FlagFailed == 0) {
                                'write to excel success'
                                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                            }
                            
                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                result = CustomKeywords.'connection.ManualStamp.getManualStamp'(conneSign, findTestData(
                                        excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), 
                                    GlobalVariable.Tenant)

                                String docId = CustomKeywords.'connection.DataVerif.getDocId'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
                                        GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), GlobalVariable.Tenant)

                                index = 0

                                ArrayList arrayMatch = []

                                'verify ref number'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('$Nomor Dokumen')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify document name'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('$Nama Dokumen')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tanggal dokumen'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('$Tanggal Dokumen')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('$Tipe Dokumen')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify tipe pembayaran'
                                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('$Tipe Dokumen Peruri')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                totalDocument = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$Dokumen')).split('\\n', -1)

                                'verify total dokumen'
                                arrayMatch.add(WebUI.verifyMatch(totalDocument.size().toString(), result[index++], false, 
                                        FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch('1', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch('0', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify manual upload'
                                arrayMatch.add(WebUI.verifyMatch(totalMeterai, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'pengecekan khusus privy sign location'
                                if (GlobalVariable.Psre == 'PRIVY') {
                                    'Jika documentTemplateCode di dokumen pertama adalah kosong'
                                    if ((CustomKeywords.'connection.APIFullService.getTemplateDocPrivyStampLoc'(conneSign, 
                                        docId) != '') && (CustomKeywords.'connection.APIFullService.getTemplateDocPrivyStampLoc'(
                                        conneSign, docId) != null)) {
                                        'ambil data privy sign location based on document_template'
                                        arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                                                    conneSign, docId), CustomKeywords.'connection.APIFullService.getTemplateDocPrivyStampLoc'(
                                                    conneSign, docId), false, FailureHandling.CONTINUE_ON_FAILURE))
                                    } else {
                                        'pastikan privy sign loc tidak null'
                                        arrayMatch.add(WebUI.verifyNotMatch('null', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                                                    conneSign, docId), false, FailureHandling.CONTINUE_ON_FAILURE))

                                        'pastikan privy sign loc tidak kosong'
                                        arrayMatch.add(WebUI.verifyNotMatch('', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                                                    conneSign, docId), false, FailureHandling.CONTINUE_ON_FAILURE))
                                    }
                                }
                                
                                'jika data db tidak sesuai dengan excel'
                                if (arrayMatch.contains(false)) {
                                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPathManualStamptoStamp).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                                }
                            }
                            
                            prosesMeterai = inputEMeteraiMonitoring(conneSign)

                            saldoAfter = loginAdminGetSaldo('No', sheet)

                            if (prosesMeterai == 'Success') {
                                arrayMatch = []

                                ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
                                    conneSign, findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('$Nomor Dokumen')), 'Stamping')

                                'lakukan loop untuk pengecekan data'
                                for (int i = 0; i < (officeRegionBline.size() / 2); i++) {
                                    'verify business line dan office code'
                                    arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[
                                            (i + 3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
                                }
                                
                                if (saldoBefore == saldoAfter) {
                                    'write to excel status failed dan reason'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, ((findTestData(excelPathManualStamptoStamp).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
                                        GlobalVariable.ReasonFailedVerifyEqualOrMatch) + ' terhadap total saldo dimana saldo awal dan saldo setelah meterai sama ')

                                    GlobalVariable.FlagFailed = 1
                                } else {
                                    verifySaldoUsed(conneSign)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Permintaan pembubuhan e-Meterai berhasil dibuat.'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            GlobalVariable.FlagFailed = 1

            return true
        }
    }
    
    false
}

def inputCancel(Connection conneSign) {
    settingzoom()

    inputForm()

    'get data tipe-tipe pembayaran secara asc'
    ArrayList docTypeDB = CustomKeywords.'connection.ManualStamp.getDocType'(conneSign)

    'check ddl tipe pembayaran'
    checkDDL(findTestObject('ManualStamp/input_docType'), docTypeDB, ' pada DDL tipe dokumen ')

    'get data tipe-tipe pembayaran secara asc'
    ArrayList docTypePeruriDB = CustomKeywords.'connection.ManualStamp.getDocTypePeruri'(conneSign)

    'check ddl tipe pembayaran'
    checkDDL(findTestObject('ManualStamp/input_docTypePeruri'), docTypePeruriDB, ' pada DDL tipe dokumen peruri ')

    'Klik button cancel'
    WebUI.click(findTestObject('ManualStamp/button_Batal'))

    WebUI.delay(2)

    'verify field kode template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentNo'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field input psre tidak kosong ')

    'verify field Nama template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentName'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nomor Kontrak tidak kosong ')

    'verify field Deskripsi template dokumen kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_documentDate'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nama Dokumen tidak kosong ')

    'verify field tipe pembayaran kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_docType'), 'value', FailureHandling.OPTIONAL), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Tanggal Dokumen tidak kosong ')

    'verify field status kosong'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualStamp/input_docTypePeruri'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field jenis Pembayaran tidak kosong ')
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('ManualSign/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('ManualSign/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' + 
            id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    println(listDB)

    println(list)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def inputForm() {
    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('ManualStamp/input_documentNo'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nomor Dokumen')))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('ManualStamp/input_documentName'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama Dokumen')))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_documentDate'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tanggal Dokumen')))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docType'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tipe Dokumen')))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docType'), Keys.chord(Keys.ENTER))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualStamp/input_docTypePeruri'), findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, rowExcel('$Tipe Dokumen Peruri')))

    'Klik enter'
    WebUI.sendKeys(findTestObject('ManualStamp/input_docTypePeruri'), Keys.chord(Keys.ENTER))

    'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
    String userDir = System.getProperty('user.dir')

    String filePath = userDir + findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Dokumen'))

    'Upload file berdasarkan filePath yang telah dirancang'
    WebUI.uploadFile(findTestObject('ManualStamp/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)

    'untuk handle ddl yang tidak tertutup'
    WebUI.click(findTestObject('ManualStamp/label_Judul'))
}

def zoomSetting(int percentage) {
    BigDecimal percentageZoom = percentage / 100

    WebDriver driver = DriverFactory.webDriver

    'open new tab'
    WebUI.executeJavaScript('window.open();', [])

    'ambil index tab yang sedang dibuka di chrome'
    int currentTab = WebUI.windowIndex

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab + 1)

    driver.get('chrome://settings/')

    'open new tab'
    WebUI.executeJavaScript(('chrome.settingsPrivate.setDefaultZoom(' + percentageZoom) + ');', [])

    'close tab baru'
    WebUI.executeJavaScript('window.close();', [])
}

def inputEMeteraiMonitoring(Connection conneSign) {
    'input flagBreak'
    int flagBreak = 0

    int retry = 0

    boolean isRetryStamping = true

    if (WebUI.verifyElementPresent(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'), GlobalVariable.TimeOut)) {
        totalMeterai = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('Jumlah Meterai'))

        ArrayList inputEMeterai = CustomKeywords.'connection.ManualStamp.getInputeMeteraiMonitoring'(conneSign, findTestData(
                excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), GlobalVariable.Tenant)

        for (int i = 1; i <= 20; i++) {
            if (flagBreak == 1) {
                break
            }
            
            inputEMeterai = CustomKeywords.'connection.ManualStamp.getInputeMeteraiMonitoring'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')), GlobalVariable.Tenant)

            indexInput = 0

            index = 0

            if (((inputEMeterai[6]) == 'Failed') || ((inputEMeterai[6]) == 'Success')) {
                if ((inputEMeterai[6]) == 'Failed') {
                    Integer.parseInt(totalMeterai) == 1

                    isRetryStamping = false
                }
                
                for (int j = 1; j <= Integer.parseInt(totalMeterai); j++) {
                    'Klik set ulang setiap data biar reset'
                    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))

                    'set text no kontrak'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), inputEMeterai[
                        indexInput++])

                    'set text TipeDokumenPeruri'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), inputEMeterai[
                        indexInput++])

                    'enter untuk set TipeDokumenPeruri'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(
                            Keys.ENTER))

                    'set text Tipe Dokumen'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), inputEMeterai[
                        indexInput++])

                    'enter untuk set Tipe Dokumen'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(
                            Keys.ENTER))

                    'set text PengaturanDokumen'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), inputEMeterai[
                        indexInput++])

                    'enter untuk set PengaturanDokumen'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(
                            Keys.ENTER))

                    'set text Tanggal Dokumen Mulai'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), 
                        inputEMeterai[indexInput++])

                    'set text TanggalDokumenSampai'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), inputEMeterai[
                        indexInput++])

                    'set text prosesStamping'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/button_prosesStamping'), inputEMeterai[
                        indexInput++])

                    'enter untuk set prosesStamping'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/button_prosesStamping'), Keys.chord(
                            Keys.ENTER))

                    'set text Nomor Seri'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), inputEMeterai[
                        indexInput++])

                    'set text Cabang'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), inputEMeterai[indexInput++])

                    'enter untuk set Cabang'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

                    'set text JenisPajak'
                    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), inputEMeterai[
                        indexInput++])

                    'enter untuk set JenisPajak'
                    WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(
                            Keys.ENTER))

                    'click button cari'
                    WebUI.click(findTestObject('e-Meterai Monitoring/button_Cari'))

                    'Jika error lognya muncul'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'ambil teks errormessage'
                        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('e-Meterai Monitoring', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusWarning, (((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

                        GlobalVariable.FlagFailed = 1
                    }
                    
                    'get stampduty data dari db'
                    ArrayList result = CustomKeywords.'connection.EMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, findTestData(
                            excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))

                    'verify no dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NomorDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Dokumen ')

                    'verify tanggal dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TanggalDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Dokumen ')

                    'verify cabang'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Cabang')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang ')

                    'verify nama dokume'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NamaDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dokumen ')

                    'verify tipe dokumen peruri'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumenPeruri')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen Peruri ')

                    'verify tipe dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_TipeDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Dokumen ')

                    'verify nominal dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_NominalDokumen')).replace(
                                ',', ''), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nominal Dokumen ')

                    'verify pengaturan dokumen'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_PengaturanDokumen')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengaturan Dokumen ')

                    'verify hasil stamping'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_HasilStamping')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Hasil Stamping ')

                    'verify nomor seri'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Nomor Seri')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nomor Seri ')

                    'verify proses meterai'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_Proses Materai')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Proses Materai ')

                    'verify jenis pajak'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/table_JenisPajak')), 
                            result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Jenis Pajak ')

                    if ((inputEMeterai[6]) == 'Failed') {
                        if (isRetryStamping == false) {
                            doRetry = findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Using Retry Stamping Feature'))

                            if ((doRetry == 'Yes') && (retry == 0)) {
                                WebUI.click(findTestObject('ManualStamp/btn_RetryStamping'))

                                WebUI.click(findTestObject('ManualStamp/btn_CancelRetryStamping'))

                                WebUI.click(findTestObject('ManualStamp/btn_RetryStamping'))

                                WebUI.click(findTestObject('ManualStamp/btn_YesRetryStamping'))

                                isRetryStamping = true

                                if (WebUI.verifyElementPresent(findTestObject('ManualStamp/text_retryStamping'), GlobalVariable.TimeOut, 
                                    FailureHandling.CONTINUE_ON_FAILURE)) {
                                    WebUI.click(findTestObject('ManualStamp/button_OKRetryStamping'))
                                }
                                
                                retry = 1

                                break
                            } else {
                                WebUI.click(findTestObject('ManualStamp/btn_ShowErrorMessage'))

                                errorMessageUI = WebUI.getText(findTestObject('ManualStamp/text_ErrorMessage'))

                                'get reason gailed error message untuk stamping'
                                errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, findTestData(
                                        excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))

                                if (WebUI.verifyMatch(errorMessageUI.toString().toUpperCase(), errorMessageDB.toString().toUpperCase(), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE)) {
                                    'write to excel bahwa save gagal'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', 
                                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, (((findTestData(excelPathManualStamptoStamp).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                        ' dengan alasan ') + errorMessageUI.toString())

                                    WebUI.click(findTestObject('e-Meterai Monitoring/button_X'))

                                    flagBreak = 1

                                    break
                                }
                            }
                        }
                    } else if ((inputEMeterai[6]) == 'Success') {
                        flagBreak = 1
                    }
                }
            } else {
                if (i == 20) {
                    WebUI.delay(15)

                    'write to excel bahwa save gagal'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Manual Stamp to Stamp', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((((findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' yaitu status meterai adalah ') + 
                        (inputEMeterai[6])) + ' pada nomor dokumen tersebut selama ') + (i * 15))
                } else {
                    WebUI.delay(15)
                }
            }
        }
        
        return inputEMeterai[6]
    }
}

def loginAdminGetSaldo(String start, String sheet) {
    String totalSaldo

    if (start == 'Yes') {
        'call test case login per case'
        WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathManualStamptoStamp
                , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
            FailureHandling.STOP_ON_FAILURE)
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

def verifySaldoUsed(Connection conneSign) {
    'deklarasi array inquiryDB'
    ArrayList inquiryDB = []

    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))

    documentName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))

    officeName = CustomKeywords.'connection.DataVerif.getOfficeName'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
            GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), 'Stamp Duty')

    if (!(WebUI.verifyElementText(findTestObject('ManualStamp/selected_DDL_Saldo'), 'Stamp Duty', FailureHandling.OPTIONAL))) {
        WebUI.mouseOver(findTestObject('ManualStamp/selected_DDL_Saldo'))

        'Input page down'
        WebUI.sendKeys(findTestObject('ManualStamp/input_tipeSaldo'), Keys.chord(Keys.ARROW_DOWN))
    }
    
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
    WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nomor Dokumen')))

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentName, FailureHandling.CONTINUE_ON_FAILURE)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Input office name'
    WebUI.setText(findTestObject('Saldo/input_officeName'), officeName)

    'Input office name'
    WebUI.sendKeys(findTestObject('Saldo/input_officeName'), Keys.chord(Keys.ENTER))

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))

    'get column di saldo'
    variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

    'get row di saldo'
    variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper '))

    if (CustomKeywords.'connection.ManualStamp.getLovVendorStamping'(conneSign, GlobalVariable.Tenant) == 'Privy') {
        'ambil inquiry di db'
        inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeteraiPrivy'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
                GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))
    } else {
        'ambil inquiry di db'
        inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldoForMeterai'(conneSign, findTestData(excelPathManualStamptoStamp).getValue(
                GlobalVariable.NumofColm, rowExcel('$Nomor Dokumen')))
    }
    
    index = 0

    'looping mengenai rownnya'
    for (row = 1; row <= variableSaldoRow.size(); row++) {
        'looping mengenai columnnya'
        for (colm = 1; colm <= variableSaldoColumn.size(); colm++) {
            'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
            modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 'xpath', 
                'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                row) + ']/datatable-body-row/div[2]/datatable-body-cell[') + colm) + ']/div', true)

            'Jika u di lokasi qty atau kolom ke 9'
            if (colm == 9) {
                'Jika bukan untuk 2 kolom itu, maka check ke db'
                checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[index], 
                        false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                    findTestData(excelPathManualStamptoStamp).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Dokumen')).replace(
                        '"', ''))

                index++
            } else if (colm == 10) {
                'Jika di kolom ke 10, atau di FE table saldo'
            } else {
                'check table'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index], false, 
                        FailureHandling.CONTINUE_ON_FAILURE), 'pada Mutasi Saldo dengan nomor Kontrak ' + findTestData(excelPathManualStamptoStamp).getValue(
                        GlobalVariable.NumofColm, rowExcel('$Tipe Dokumen')).replace('"', ''))

                index++
            }
        }
    }
}

def settingzoom() {
    'ambil index tab yang sedang dibuka di chrome'
    int currentTab = WebUI.windowIndex

    'setting zoom menuju 80 persen'
    zoomSetting(60)

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab)

    'Klik tombol menu Manual Stamp'
    WebUI.click(findTestObject('ManualStamp/menu_ManualStamp'))

    'Klik tombol menu Manual Stamp'
    WebUI.click(findTestObject('ManualStamp/menu_ManualStamp'))

    'ambil index tab yang sedang dibuka di chrome'
    currentTab = WebUI.windowIndex

    'setting zoom menuju 80 persen'
    zoomSetting(100)

    'ganti fokus robot ke tab baru'
    WebUI.switchToWindowIndex(currentTab)
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

