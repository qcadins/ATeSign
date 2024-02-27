import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare untuk split array excel dan beberapa variable yang dibutuhkan'
semicolon = ';'

indexForCatatanStamp = 0

indexEmail = 0

'get tenant dari excel percase'
GlobalVariable.Tenant = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

'get psre dari excel percase'
GlobalVariable.Psre = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('psreCode'))

'declare flag failed'
GlobalVariable.FlagFailed = 0

'Inisialisasi array dan variable'
ArrayList<String> namaTandaTangan = []

ArrayList<String> notelpTandaTangan = []

'inisiasi index menjadi 8 untuk modify object ketika tidak pada e-meterai'
index = 13

'Inisialisasi variable yang dibutuhkan'
emailPenandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(
    semicolon, splitIndex)

tipeTandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$TipeTandaTangan (Send Manual)')).split(
    semicolon, splitIndex)

totalTandaTangan = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('jumlah signer lokasi per signer (Send Manual)')).split(
    semicolon, splitIndex)

pindahkanSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Pindahkan SignBox (Send Manual)')).split(
    semicolon, splitIndex)

lokasiSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Lokasi Pemindahan signbox (Send Manual)')).split(
    '\\n', splitIndex)

lockSignBox = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Lock Sign Box (Send Manual)')).split(
    semicolon, splitIndex)

catatanStamping = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Catatan Stamping (Send Manual)')).split(
    semicolon, splitIndex)

funcLogin()

loopcase = 1

for (looping = 0; looping < loopcase; looping++) {
    'Pengecekan apakah masuk page manual sign'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_ManualSign'), GlobalVariable.TimeOut)) {
        'Input form yang ada pada page'
        inputForm(conneSign)

        'jika setting menggunakan e-meterai'
        if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Membutuhkan e-Meterai (Send Manual)')) == 
        'Yes') {
            'index meningkat karena tambahan 2 kolom ketika menggunakan e-meterai'
            index = 15
        } else {
            'modify menuju index normal'
            index = 13
        }
        
        'modify label daftar penanda tangan dengan naiknya index'
        modifyObjectLblDaftarPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/lbl_daftarpenandatangan'), 
            'xpath', 'equals', ('//*[@id="msxForm"]/div[' + index) + ']/div[3]/table/tr/td/small', true)

        'modify button tambah penanda tangan dengan naiknya index'
        modifyObjectbuttonTambahPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/button_tambahTandaTangan'), 
            'xpath', 'equals', ('//*[@id="msxForm"]/div[' + index) + ']/div[2]/a', true)

        'check ada value maka setting email service tenant'
        if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
        0) {
            for (loopingSigner = 0; loopingSigner < emailPenandaTangan.size(); loopingSigner++) {
                'setting email service tenant'
                CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(excelPathManualSigntoSign).getValue(
                        GlobalVariable.NumofColm, rowExcel('Setting Email Service')), emailPenandaTangan[loopingSigner])
            }
        }
        
        emailService = CustomKeywords.'connection.DataVerif.getEmailService'(conneSign, GlobalVariable.Tenant)

        'click tambah penanda tangan'
        WebUI.click(modifyObjectbuttonTambahPenandaTangan)

        'diberikan delay 3 detik dengan loading'
        WebUI.delay(1)

        if (checkErrorLog() == true) {
            continue
        }
        
        if (emailService.toString() == '1') {
            if (WebUI.verifyElementNotPresent(findTestObject('ManualSign/input_phonePenandaTangan'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'write excel mengenai error log tersebut'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + 'Field phone tidak muncul saat setting email service aktif pada tenant ') + GlobalVariable.Tenant)
            }
        }
        
        'klik button x untuk keluar dari tambahan penanda tangan'
        WebUI.click(findTestObject('ManualSign/button_x'))

        'diberikan delay 3 detik dengan loading'
        WebUI.delay(1)

        'looping berdasarkan email penanda tangan'
        for (int i = 0; i < emailPenandaTangan.size(); i++) {
            if ((emailPenandaTangan[i]) != '') {
                'klik tambah penanda tangan'
                WebUI.click(modifyObjectbuttonTambahPenandaTangan)
            } else if ((emailPenandaTangan[i]) == '') {
                'jika nama dan email penanda tangan kosong'
                continue
            }
            
            'jika label tambah penanda tangan muncul'
            if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_TambahPenandaTangan'), GlobalVariable.TimeOut) == 
            true) {
                'set text pada email penanda tangan'
                WebUI.setText(findTestObject('ManualSign/input_emailPenandaTangan'), emailPenandaTangan[i])

                if (emailService.toString() == '1') {
                    'klik search penanda tangan'
                    WebUI.click(findTestObject('ManualSign/button_searchPenandaTanganWithoutEmailService'))
                } else {
                    'klik search penanda tangan'
                    WebUI.click(findTestObject('ManualSign/button_searchPenandaTanganWithEmailService'))
                }
                
                'diberikan delay 1 detik dengan loading search'
                WebUI.delay(1)

                'jika muncul error log penanda tangan'
                if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog_PenandaTangan'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'get text error log'
                    error = WebUI.getText(findTestObject('ManualSign/errorLog_PenandaTangan'))

                    'write excel mengenai error log tersebut'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + error) + '>')

                    'diberikan delay 2 detik agar sudah ter write sebelum button cancel'
                    WebUI.delay(1)

                    'klik button cancel'
                    WebUI.click(findTestObject('ManualSign/button_Cancel'))

                    'delay 2 detik agar cancelnya benar-benar diclick'
                    WebUI.delay(1)

                    continue
                }
                
                'verifikasi signer ketika sudah submit signer'
                String verifikasiSigner = CustomKeywords.'connection.ManualSign.getVerificationSigner'(conneSign, emailPenandaTangan[
                    i])

                'check data mengenai email penanda tangan'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_emailPenandaTangan'), 
                            'value'), emailPenandaTangan[i], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada email Penanda Tangan ')

                'check data nama dari UI dengan db'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_namaPenandaTangan'), 
                            'value'), verifikasiSigner, false, FailureHandling.CONTINUE_ON_FAILURE), ' pada nama Penanda Tangan ')
            }
            
            'diberikan delay 3 detik untuk'
            WebUI.delay(1)

            'check save ada attribute disabled'
            if (WebUI.verifyElementHasAttribute(findTestObject('ManualSign/button_Save'), 'disabled', GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'write to excel save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)

                break
            } else {
                'klik button save'
                WebUI.click(findTestObject('ManualSign/button_Save'))
            }
            
            for (int p = 0; p < emailPenandaTangan.size(); p++) {
                'Inisialisasi array dan index yang dibutuhkan'
                arrayIndex = 0

                arrayIndexValue = 0

                indexObject = 1

                'gmofidy modifyObjectInformasiPenandaTangan'
                modifyObjectInformasiPenandaTangan = WebUI.modifyObjectProperty(findTestObject('ManualSign/modifyObject'), 
                    'xpath', 'equals', ((('//*[@id="msxForm"]/div[' + index) + ']/div[3]/table/tr[') + (p + 1)) + ']/td/p', 
                    true)

                'mengambil informasi signer '
                valueInformasi = WebUI.getText(modifyObjectInformasiPenandaTangan).replace('\n', ', ')

                'split informasi signer berdasarkan delimiter'
                valueInformasi = valueInformasi.split(', ', -1)

                'query check informasi dari user tersebut'
                queryCheckInformationUser = CustomKeywords.'connection.ManualSign.getInformationUser'(conneSign, emailPenandaTangan[
                    indexEmail++], GlobalVariable.Psre)

                if ((valueInformasi[2]) == (emailPenandaTangan[(indexEmail - 1)])) {
                    'check ui dan query mengenai nama signer'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(valueInformasi[arrayIndexValue++], queryCheckInformationUser[
                            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama penanda tangan ')

                    'check ui dan query mengenai nomor telepon signer'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                                valueInformasi[arrayIndexValue++]), queryCheckInformationUser[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' pada informasi nomor telepon penanda tangan ')

                    'check ui dan query mengenai email signer'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(valueInformasi[arrayIndexValue++], queryCheckInformationUser[
                            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi email penanda tangan ')

                    'add nama tanda tangan yang sukses dan nomor telepon'
                    namaTandaTangan.add(valueInformasi[0])

                    notelpTandaTangan.add(valueInformasi[1])

                    break
                } else {
                    indexEmail--
                }
            }
        }
        
        'jika button next ada disabled'
        if (WebUI.verifyElementHasAttribute(findTestObject('ManualSign/button_Next'), 'disabled', GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'write to excel bahwa save gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedSaveGagal)
        } else {
            if (WebUI.verifyElementPresent(modifyObjectLblDaftarPenandaTangan, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'write to excel bahwa save gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + 'Silahkan tambah penanda tangan terlebih dulu!') + '>')
            } else {
                WebUI.click(findTestObject('ManualSign/button_Next'))
            }
        }
        
        'check element present pada next tanda tangan'
        if (WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_documentNo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'check ui dan excel pada nomor dokumen'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/lbl_documentNo'), 'value'), 
                    findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')), 
                    false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nomor dokumen ')

            'check ui dan excel pada nama dokumen'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('ManualSign/input_documentName'), 
                        'value'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('documentName')), 
                    false, FailureHandling.CONTINUE_ON_FAILURE), ' pada informasi nama dokumen ')

            'Klik button tanda tangan'
            WebUI.click(findTestObject('Object Repository/ManualSign/btn_ttd'))

            'diberikan delay 5 detik untuk loading'
            WebUI.delay(1)

            'Klik set tanda tangan'
            WebUI.click(findTestObject('ManualSign/btn_setTandaTangan'))

            'click button lock signbox'
            WebUI.click(findTestObject('ManualSign/btn_LockSignBox'))

            isLocked = WebUI.getAttribute(findTestObject('ManualSign/btn_LockSignBox'), 'class', FailureHandling.STOP_ON_FAILURE)

            'verify sign box is locked'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(isLocked, 'fa fa-2x fa-lock', false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' pada sign locked ')

            'Klik button Delete'
            WebUI.click(findTestObject('ManualSign/btn_DeleteSignBox'))

            'verify sign box is deleted'
            checkVerifyEqualOrMatch(WebUI.verifyElementNotPresent(findTestObject('ManualSign/signBox'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE), ' pada deletenya box Sign ')

            index = 0

            countValue = 0

            'looping berdasarkan total tanda tangan'
            for (int j = 1; j <= tipeTandaTangan.size(); j++) {
                if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
                    'Klik button tanda tangan'
                    WebUI.click(findTestObject('Object Repository/ManualSign/btn_ttd'))
                } else if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
                    'Klik button tanda tangan'
                    WebUI.click(findTestObject('Object Repository/ManualSign/btn_meterai'))
                }
                
                WebUI.delay(1)

                'modify label tipe tanda tangan di kotak'
                modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/ManualSign/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    j) + ']/div/div/small', true)

                if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
                    'Verify label tanda tangannya muncul atau tidak'
                    WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
                        FailureHandling.CONTINUE_ON_FAILURE)

                    'Klik set tanda tangan'
                    WebUI.click(findTestObject('ManualSign/ddl_TipeTandaTangan'))

                    if (countValue == Integer.parseInt(totalTandaTangan[index])) {
                        index++

                        countValue = 0
                    }
                    
                    'Memilih tipe signer apa berdasarkan excel'
                    WebUI.selectOptionByLabel(findTestObject('ManualSign/ddl_TipeTandaTangan'), ((namaTandaTangan[index]) + 
                        ' - ') + (notelpTandaTangan[index]), false)

                    countValue++

                    'Klik set tanda tangan'
                    WebUI.click(findTestObject('ManualSign/btn_setTandaTangan'))

                    'Verifikasi antara excel dan UI, apakah tipenya sama'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(namaTandaTangan[index], WebUI.getText(modifyobjectTTDlblRoleTandaTangan), 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' pada nama tanda tangan signer')
                }
                
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
                        modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                            j) + ']/div/button[1]/span', true)

                        'click lock signbox'
                        WebUI.click(modifyobjectLockSignBox)

                        if ((tipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
                            WebUI.verifyElementPresent(findTestObject('ManualSign/lbl_isiCatatanStamping'), GlobalVariable.TimeOut, 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            WebUI.setText(findTestObject('ManualSign/input_isiCatatanStamping'), catatanStamping[indexForCatatanStamp++])

                            WebUI.click(findTestObject('ManualSign/button_batalCatatanStamping'))

                            'click lock signbox'
                            WebUI.click(modifyobjectLockSignBox)

                            WebUI.delay(2)

                            checkVerifyEqualOrMatch(WebUI.verifyMatch('', WebUI.getText(findTestObject('ManualSign/input_isiCatatanStamping')), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE), ' pada catatan meterai ')

                            WebUI.setText(findTestObject('ManualSign/input_isiCatatanStamping'), catatanStamping[(indexForCatatanStamp - 
                                1)])

                            WebUI.click(findTestObject('ManualSign/button_SimpanCatatanStamping'))
                        }
                    }
                }
            }
            
            'click button simpan'
            WebUI.click(findTestObject('Object Repository/ManualSign/btn_proses'))

            checkErrorLog()

            'susun urutan tanda tangan'
            sortingSequenceSign()

            checkErrorLog()
			
			String docId = CustomKeywords.'connection.DataVerif.getDocId'(conneSign, findTestData(excelPathManualSigntoSign).getValue(
				GlobalVariable.NumofColm, rowExcel('$referenceNo')), GlobalVariable.Tenant)
			
			'Write to excel mengenai Document ID'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
					'documentid') - 1, GlobalVariable.NumofColm - 1, docId)
			
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('PsRE Document') -
				1, GlobalVariable.NumofColm - 1, findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
					rowExcel('Vendor')))
			
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    result = CustomKeywords.'connection.ManualSign.getManualSign'(conneSign, findTestData(excelPathManualSigntoSign).getValue(
                            GlobalVariable.NumofColm, rowExcel('$referenceNo')))

                    index = 0

                    ArrayList<String> arrayMatch = []

                    'verify vendor'
                    arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Psre, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify ref number'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$referenceNo')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify document name'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('documentName')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tanggal dokumen'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$Tanggal Dokumen (Send Manual)')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tipe pembayaran'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$Jenis Pembayaran (Send Manual)')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    automaticAfterStamping = '0'

                    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Membutuhkan e-Meterai (Send Manual)')) == 
                    'Yes') {
                        tipeDokumenPeruri = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Tipe Dokumen Peruri (Send Manual)'))

                        if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Stamp Meterai Otomatis (Send Manual)')) == 
                        'Ya') {
                            automaticAfterStamping = '1'
                        }
                    } else {
                        tipeDokumenPeruri = ''
                    }
                    
                    'verify automatic stamping after sign'
                    arrayMatch.add(WebUI.verifyMatch(automaticAfterStamping, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tipe dokumen peruri'
                    arrayMatch.add(WebUI.verifyMatch(tipeDokumenPeruri, result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    totalDocument = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel(
                            'documentFile')).split('\\n', -1)

                    'verify total dokumen'
                    arrayMatch.add(WebUI.verifyMatch(totalDocument.size().toString(), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify manual upload'
                    arrayMatch.add(WebUI.verifyMatch('1', result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify is seq'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('isSequence')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('officeName')) != 
                    '') {
                        'verify office code'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('officeName')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
						index++
					}
                    
                    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('regionName')) != 
                    '') {
                        'verify region code'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('regionName')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
						index++
					}
                    
                    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')) != 
                    '') {
                        'verify business line code'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('businessLineName')), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
						index++
					}
                    
                    'verify use sign qr'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('QR')).replace('Ya', '1').replace('Tidak', '0'), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE))
					
                    if ((GlobalVariable.Psre == 'PRIVY') && tipeTandaTangan.contains('Meterai')) {
                        'pastikan privy sign loc tidak null'
                        arrayMatch.add(WebUI.verifyNotMatch('null', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                                    conneSign, docId), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'pastikan privy sign loc tidak kosong'
                        arrayMatch.add(WebUI.verifyNotMatch('', CustomKeywords.'connection.APIFullService.getPrivyStampLocation'(
                                    conneSign, docId), false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
            }
            
            if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).length() > 
            0) {
                checkSaldoWAOrSMS(conneSign, findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
                        rowExcel('$email')).replace('"', ''))
            }
        }
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('ManualSign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('ManualSign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('Permintaan tanda tangan berhasil dibuat.'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + errormessage) + '>')

            GlobalVariable.FlagFailed = 1

            return true
        }
    }
    
    false
}

def inputForm(Connection conneSign) {
    'Input teks input psre'
	inputDDLExact('ManualSign/input_psre', GlobalVariable.Psre)

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('ManualSign/input_referenceNo'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            rowExcel('$referenceNo')))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('ManualSign/input_documentName'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            rowExcel('documentName')))

    'Input tanggal document'
    WebUI.setText(findTestObject('ManualSign/input_documentDate'), findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tanggal Dokumen (Send Manual)')))

    'Input jenis pembayaran'
	inputDDLExact('ManualSign/input_jenisPembayaran', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Jenis Pembayaran (Send Manual)')))
	
    'Input is sequence'
	inputDDLExact('ManualSign/input_isSequence', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
		rowExcel('isSequence')))

    if (!(GlobalVariable.Psre.toString().equalsIgnoreCase('Privy'))) {
        'Input qr'
		inputDDLExact('ManualSign/input_qr', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('QR')))
    }
    
	'get data store db'
	int result = CustomKeywords.'connection.APIFullService.businessLineAPIOnly'(conneSign, GlobalVariable.Tenant)

	'check ddl busienss line code count'
	checkDDL(findTestObject('ManualSign/input_businessLineCode'), result, ' pada tipe Business Line ')
		
    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')) != '') {
		'Input pada business line'
		inputDDLExact('ManualSign/input_businessLineCode', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('businessLineName')))
    }
	
	'get data store db'
	result = CustomKeywords.'connection.APIFullService.getRegionListCount'(conneSign)

	'check ddl busienss line code count'
	checkDDL(findTestObject('ManualSign/input_regionCode'), result, ' pada tipe Region ')
		
    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('regionName')) != '') {
        'Input pada region code'
		inputDDLExact('ManualSign/input_regionCode', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('regionName')))
    }

    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('officeName')) != '') {
		'get data store db'
		result = CustomKeywords.'connection.APIFullService.getOfficeNameBasedOnRegionAndTenant'(conneSign, GlobalVariable.Tenant, findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('regionName')))
	
		'check ddl busienss line code count'
		checkDDLWithGetDDL(findTestObject('ManualSign/button_ddlOfficeCode'), result, ' pada tipe Office ', findTestObject('ManualSign/btn_closeddlOffice'))
		
		'Input pada office code'
		inputDDLExact('ManualSign/input_officeCode', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('officeName')))
    }
    
    'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
    String userDir = System.getProperty('user.dir') + '\\File'

    String filePath = userDir + findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')).replace(
        '/', '\\')

    'Upload file berdasarkan filePath yang telah dirancang'
    WebUI.uploadFile(findTestObject('ManualSign/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)

    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Membutuhkan e-Meterai (Send Manual)')) == 
    'Yes') {
        if (WebUI.verifyElementNotChecked(findTestObject('ManualSign/input_isE-Meterai'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Input AKtif pada input Status'
            WebUI.click(findTestObject('ManualSign/btn_E-Meterai'))
        }
        
        'Input Aktif pada input Status'
		inputDDLExact('ManualSign/input_tipeDokumenPeruri', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('Tipe Dokumen Peruri (Send Manual)')))

        if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Tipe Dokumen Peruri (Send Manual)')) == 
        '') {
            'Klik enter'
            WebUI.sendKeys(findTestObject('ManualSign/input_tipeDokumenPeruri'), Keys.chord(Keys.ENTER))
        }
        
        'Input AKtif pada input Status'
		inputDDLExact('ManualSign/input_isAutomatedStamp', findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm,
			rowExcel('$Stamp Meterai Otomatis (Send Manual)')))
    } else if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('$Membutuhkan e-Meterai (Send Manual)')) == 
    'No') {
        if (WebUI.verifyElementChecked(findTestObject('ManualSign/input_isE-Meterai'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'Input AKtif pada input Status'
            WebUI.click(findTestObject('ManualSign/btn_E-Meterai'))
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def sortingSequenceSign() {
    'check if Sequential signing iya'
    if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).equalsIgnoreCase(
        'Ya')) {
        if (findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Urutan Signing (Send Manual)')) != 
        '') {
            'get urutan seq sign dari excel'
            ArrayList<String> seqSignRole = findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Urutan Signing (Send Manual)')).toString().toUpperCase().split(';', -1)

            'count box'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('#cdk-drop-list-0 div'))

            'looping seq sign'
            for (int seq = 1; seq <= variable.size(); seq++) {
                'modify label tipe tanda tangan di kotak'
                modifyObject = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals', 
                    ('//*[@id="cdk-drop-list-0"]/div[' + seq) + ']', true)

                'get text pisah nomor dan psre'
                signer = WebUI.getText(modifyObject).split(' ', -1)

                index = (seqSignRole.indexOf(signer[1]) + 1)

                if (seq != index) {
                    'modify label tipe tanda tangan di kotak'
                    modifyObjectNew = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 
                        'equals', ('//*[@id="cdk-drop-list-0"]/div[' + index) + ']', true)

                    'pindahin ke urutan sesuai excel'
                    WebUI.dragAndDropToObject(modifyObject, modifyObjectNew)

                    'untuk proses pemindahan'
                    WebUI.delay(1)

                    seq--
                }
            }
        }
        
        'click button simpan'
        WebUI.click(findTestObject('Object Repository/ManualSign/btn_simpan'))

        'delay untuk loading simpan'
        WebUI.delay(1)
    }
}

def checkSaldoWAOrSMS(Connection conneSign, String emailSigner) {
    'inisialisasi penggunaan saldo, balmut, tipe saldo'
    int penggunaanSaldo = 0
	
	int pemotonganSaldo = 0

	int increment

    ArrayList<String> balmut = []

    String tipeSaldo

    'looping per email signer'
    for (loopingEmailPerDoc = 0; loopingEmailPerDoc < emailSigner.split(';', -1).size(); loopingEmailPerDoc++) {
        fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner[loopingEmailPerDoc])

        notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, emailSigner[loopingEmailPerDoc], 
            'MANUAL_SIGN_REQ', GlobalVariable.Tenant)

		if (notifTypeDB == 'Email') {
			continue
		}
		
        if (notifTypeDB == '0' || notifTypeDB == 'Level Tenant') {
            mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

            'get setting email service, full name, must use wa first'
            emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, emailSigner[
                loopingEmailPerDoc])

            'jika must use wa first'
            if (mustUseWAFirst == '1') {
                tipeSaldo = 'WhatsApp Message'

                'menggunakan saldo wa'
                balmut = (CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser))

				penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
            } else {
                'jika email service adalah 1, maka get setting use wa message'
                if (emailServiceOnVendor == '1') {
                    useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                    'jika use wa message = 1'
                    if (useWAMessage == '1') {
                        tipeSaldo = 'WhatsApp Message'

                        'menggunakan saldo wa'
                        balmut = (CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, 
                            fullNameUser))

						penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
                    } else if (useWAMessage == '0') {
                        'ke sms / wa'
                        SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Send Document')

                        if (SMSSetting == '1') {
                            'ke sms'
                            tipeSaldo = 'SMS Notif'

                            balmut = (CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, 
                                fullNameUser))

							penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
                        }
                    }
                }
            }
        } else {
            tipeSaldo = notifTypeDB

            'menggunakan saldo wa'
            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

			penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
        }

        if (penggunaanSaldo > 0) {
            'get looping penggunaan saldo'
            for (looping = 1; looping <= penggunaanSaldo; looping++) {
                'jika looping pertama, incrementnya tetap 0'
                if (looping == 1) {
                    increment = 0
                } else {
                    'increment naik 10'
                    increment = (increment + 10)
                }
                
                'get pemotongan saldo dari query dimasukkan kepada pemotongan saldo'
                pemotonganSaldo = (pemotonganSaldo + Integer.parseInt((balmut[(increment + 9)]).replace('-', '')))

                'trxno akan dimasukkan kepada hashmap'
                (GlobalVariable.eSignData['allTrxNo']) = (((GlobalVariable.eSignData['allTrxNo']) + (balmut[increment])) + 
                ';')

                'sign type akan dimasukkan kepada hashmap'
                (GlobalVariable.eSignData['allSignType']) = (((GlobalVariable.eSignData['allSignType']) + (balmut[(increment + 
                3)]).replace('Use ', '')) + ';')

                'email usage sign akan dimasukkan kepada hashmap'
                (GlobalVariable.eSignData['emailUsageSign']) = (((GlobalVariable.eSignData['emailUsageSign']) + fullNameUser) + 
                ';')
            }
            
            if (tipeSaldo == 'WhatsApp Message') {
                (GlobalVariable.eSignData['CountVerifikasiWA']) = pemotonganSaldo
            } else if (tipeSaldo == 'SMS Notif') {
                (GlobalVariable.eSignData['CountVerifikasiSMS']) = pemotonganSaldo
            }
        }
    }
}

def checkingSaldo(ArrayList<String> balmut, String tipeSaldo, int penggunaanSaldo) {
	'jika balmutnya tidak ada value'
	if (balmut.size() == 0) {
		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			((findTestData(excelPathManualSigntoSign).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') +
			';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') + tipeSaldo)
	} else {
		'penggunaan saldo didapat dari ikuantitaas query balmut'
		penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
	}
	penggunaanSaldo
}

def funcLogin() {
    if (!(WebUI.verifyElementPresent(findTestObject('ManualSign/input_psre'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
        if (WebUI.verifyElementPresent(findTestObject('ManualSign/ManualSign'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'cek apakah elemen menu ditutup'
            if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
                'klik pada button hamburber'
                WebUI.click(findTestObject('button_HamburberSideMenu'))
            }
            
            'klik button saldo'
            WebUI.click(findTestObject('ManualSign/ManualSign'))

            'cek apakah tombol x terlihat'
            if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
                'klik pada button X'
                WebUI.click(findTestObject('buttonX_sideMenu'))
            }
        } else {
            'Call test Case untuk login sebagai admin wom admin client'
            WebUI.callTestCase(findTestCase('Main Flow - Copy/Login'), [('excel') : excelPathManualSigntoSign, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'klik button saldo'
            WebUI.click(findTestObject('ManualSign/ManualSign'))
        }
    } else {
        WebUI.refresh()
    }
}

def checkDDLWithGetDDL(TestObject objectDDL, int listDB, String reason, TestObject objectClick) {
	'declare array untuk menampung ddl'
	ArrayList list = []

	WebUI.scrollToElement(objectDDL, GlobalVariable.TimeOut)
	
	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)

	'get id ddl'
	id = WebUI.getAttribute(findTestObject('TandaTanganDokumen/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

	'looping untuk get ddl kedalam array'
	for (i = 1; i < variable.size(); i++) {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
			((('//*[@id=\'' + id) + '-') + i) + '\']', true)

		'add ddl ke array'
		list.add(WebUI.getText(modifyObjectDDL))
	}

	'verify jumlah ddl ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB, FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' +
		reason)

	'Input click untuk tutup ddl'
	WebUI.click(objectClick)
}

def checkDDL(TestObject objectDDL, int listDB, String reason) {
	WebUI.scrollToElement(objectDDL, GlobalVariable.TimeOut)
	
	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)

	'get id ddl'
	id = WebUI.getAttribute(findTestObject('TandaTanganDokumen/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

	'verify jumlah ddl ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyEqual(variable.size() - 1, listDB, FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' +
		reason)

	'Input enter untuk tutup ddl'
	WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	WebUI.click(findTestObject(locationObject))
	
	'get token unik'
	tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')
	
	'modify object label Value'
	modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="'+tokenUnique+'"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
				'equals', '//*[@id="'+tokenUnique+'"]/div/div[2]/div['+ (i + 1) +']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
}