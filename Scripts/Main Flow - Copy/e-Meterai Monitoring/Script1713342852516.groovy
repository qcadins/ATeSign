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

firstDateOfMonth = currentDate.withDayOfMonth(1)

'Call test Case untuk login sebagai admin wom admin client'
WebUI.callTestCase(findTestCase('Main Flow - Copy/Login'), [('excel') : excel, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'click menu meterai'
WebUI.click(findTestObject('e-Meterai Monitoring/menu_emeteraiMonitoring'))

'ambil value db mengenai progress stamping'
progressStamping = CustomKeywords.'connection.EMeteraiMonitoring.getProseseMeteraiMonitoring'(conneSign, noKontrak)

loopingPertama = 1

for (looping = 0; looping < loopingPertama; looping++) {
    'Jika stampingnya failed'
    if ((progressStamping[0]) == 'Failed') {
        inputPagingAndVerify(conneSign)

        'Jika aksinya View Error message'
        if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('View Error Message')) == 
        'Yes') {
            'Klik aksi'
            WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi View Error Message'))

            'Diberi delay 2 sec'
            WebUI.delay(2)

            'verify error message'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/label_ModalBody')), 
                    CustomKeywords.'connection.EMeteraiMonitoring.getErrorMessage'(conneSign, noKontrak), false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' Error Message ')

            'Klik button X'
            WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_X'))
        }
        
        if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Retry Stamping From Upload')) == 
        'Yes') {
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
                        GlobalVariable.StatusFailed, (((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

                    GlobalVariable.FlagFailed = 1

                    continue
                }
                
                'verify element present popup'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut)) {
                    'label popup diambil'
                    lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

                    'jika popup bukan retry stamping'
                    if (!(lblpopup.contains('Retry stamping'))) {
                        'Tulis di excel sebagai failed dan error.'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + lblpopup) + '>')

                        'Flag Failed 1'
                        GlobalVariable.FlagFailed = 1
                    }
                    
                    'Klik OK untuk popupnya'
                    WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'looping per 10 detik hingga 60 detik'
                        for (int i = 1; i <= GlobalVariable.LoopingPeriodStamping; i++) {
                            'Ambil value db lagi mengenai progress stamping'
                            progressStampingAfter = CustomKeywords.'connection.EMeteraiMonitoring.getProseseMeteraiMonitoring'(
                                conneSign, noKontrak)

                            'Jika masih in progress'
                            if ((progressStampingAfter[0]) == 'In Progress') {
                                'Diberi delay 10 detik'
                                WebUI.delay(GlobalVariable.TimeLoop)

                                'Jika sudah diloopingan terakhir'
                                if (i == GlobalVariable.LoopingPeriodStamping) {
                                    'Tulis di excel sebagai failed performance.'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusWarning, ((((findTestData(excelPathemeteraiMonitoring).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
                                        GlobalVariable.ReasonFailedPerformance) + ' pada retry stamping hingga ') + (i * 
                                        GlobalVariable.TimeLoop)) + ' detik ')

                                    GlobalVariable.FlagFailed = 1
                                }
                            } else if ((progressStampingAfter[0]) == 'Failed') {
                                'Jika retrynya failed, Tulis di excel sebagai failed dan error.'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                    ' pada saat melakukan Retry Stamping ')

                                GlobalVariable.FlagFailed = 1

                                break
                            } else if ((progressStampingAfter[0]) == 'Success') {
                                'Jika sukses, maka verify match progress lama dengan yang baru. Apakah sama'
                                if (WebUI.verifyMatch(progressStamping[0], progressStampingAfter[0], false, FailureHandling.OPTIONAL) == 
                                true) {
                                    'Tulis di excel sebagai failed dan error.'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, ((findTestData(excelPathemeteraiMonitoring).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
                                        GlobalVariable.ReasonFailedProsesStamping) + ' pada saat melakukan Retry Stamping ')
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
    if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Need Download Document ?')).equalsIgnoreCase(
        'Yes')) {
        'Klik download file'
        WebUI.click(findTestObject('e-Meterai Monitoring/btn_DownloadDocument'))

        'looping hingga 20 detik'
        for (int i = 1; i <= 4; i++) {
            'pemberian delay download 5 sec'
            WebUI.delay(5)

            'check isfiled downloaded'
            if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathemeteraiMonitoring).getValue(
                    GlobalVariable.NumofColm, rowExcel('Delete Downloaded Document'))) == true) {
                'Jika sukses downloadnya lebih dari 10 detik'
                if (i > 2) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedPerformance'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusWarning, ((((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPerformance) + ' sejumlah ') + 
                        (i * 5)) + ' detik ')

                    GlobalVariable.FlagFailed = 1
                }
                
                break
            } else {
                'Jika error lognya muncul'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'ambil teks errormessage'
                    errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                    'Tulis di excel itu adalah error'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

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
    
    if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Need View Document ?')) == 
    'Yes') {
        'check if button view document present'
        if (WebUI.verifyElementPresent(findTestObject('e-Meterai Monitoring/btn_ViewDocument'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)) {
            'click button view document'
            WebUI.click(findTestObject('e-Meterai Monitoring/btn_ViewDocument'))
        }
        
        'Jika error lognya muncul'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'ambil teks errormessage'
            errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                '<') + errormessage) + '>')
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
            checkVerifyEqualOrMatch(WebUI.verifyMatch(noKontrak, labelViewDoc, false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' pada nomor kontrak UI yaitu ' + labelViewDoc)

            'Klik kembali'
            WebUI.click(findTestObject('Object Repository/KotakMasuk/btn_backViewDokumen'))
        } else {
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedProcessNotDone) + ' untuk proses View dokumen tanda tangan. ')
        }
    }
    
    if (GlobalVariable.FlagFailed == 0) {
        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def inputPagingAndVerify(Connection conneSign) {
    'Klik set ulang setiap data biar reset'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))

    'set text no kontrak'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), noKontrak)

    'click button cari'
    WebUI.click(findTestObject('e-Meterai Monitoring/button_Cari'))

    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusWarning, 
            (((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + '<') + errormessage) + '>')

        GlobalVariable.FlagFailed = 1
    }
    
    'get stampduty data dari db'
    result = CustomKeywords.'connection.EMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, noKontrak)

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
    'Jika aksinya mengenai retry stamping dan retry stamping from upload, klik'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/table_Aksi Retry Stamping From Upload'))
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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
            'equals', ('//*[@id="' + tokenUnique) + '"]/div/div[2]', true)

        DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)

        for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
            if ((DDLFromToken.split('\n', -1)[i]).toString().toLowerCase() == input.toString().toLowerCase()) {
                modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 
                    'equals', ((('//*[@id="' + tokenUnique) + '"]/div/div[2]/div[') + (i + 1)) + ']', true)

                WebUI.click(modifyObjectClicked)

                break
            }
        }
    } else {
        WebUI.click(findTestObject(locationObject))

        WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
    }
}
