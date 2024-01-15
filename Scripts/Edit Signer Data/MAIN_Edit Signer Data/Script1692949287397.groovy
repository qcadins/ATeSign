import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathEditSignerData).columnNumbers

int firstRun = 0

'looping Edit Signer Data'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'Declare variable yang dibutuhkan'
        String emailOrNIKExcel, emailOrNIKHash, getVendor

        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathEditSignerData
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)
            }
            
            firstRun = 1
        }
        
        'click menu Edit Signer Data'
        WebUI.click(findTestObject('Edit Signer Data/menu_Edit Signer Data'))

        'check if search dengan email/NIK'
        if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
            'Email')) {
            emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('$Email'))
        } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
            'NIK')) {
            emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('$NIK'))
        }
        
        'search data menggunakan email'
        searchData(emailOrNIKExcel)

        'Jika aksi Edit Data'
        if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Edit Data')) {
            WebUI.delay(4)

            'click button Edit Data'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditData'))

            'Klik batal'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

            'search data menggunakan email'
            searchData(emailOrNIKExcel)

            WebUI.delay(4)

            'klik aksi edit data'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditData'))

            'Jika sudah ada header edit'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/Edit Signer Data/lbl_HeaderEdit'), GlobalVariable.TimeOut)) {
                'Jika input menggunakan NIK'
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
                    'NIK')) {
                    'convert input menjadi SHA256'
                    emailOrNIKHash = convertSHA256(emailOrNIKExcel)
                } else {
                    'email'
                    emailOrNIKHash = emailOrNIKExcel.toString().toUpperCase()
                }
                
                'mengambil return vendor dari UI sekaligus check result'
                getVendor = resultCheck(conneSign, emailOrNIKHash)

                'ambil hasil db sebelum edit'
                ArrayList resultBefore = CustomKeywords.'connection.EditSignerData.getBeforeEditDataEditSignerData'(conneSign, 
                    emailOrNIKHash, GlobalVariable.Tenant, getVendor)

                'Set Text nama'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_NamaEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, rowExcel('Nama')))

                'Set text email'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_EmailNewEditData'), findTestData(
                        excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email')))

                'Set text nomor telepon'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_noPhoneEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, rowExcel('No Handphone')))

                'Set text nomor ktp'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_noKtpEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, rowExcel('No. KTP')))

                'set text tanggal lahir'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_dateofBirthEditData'), findTestData(
                        excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Lahir')))

                'Jikak checkPagingConfirmationnya dapat return true'
                if (checkPagingConfirmation() == true) {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    'delay update db selama 3'
                    WebUI.delay(3)

                    'ambil value db mengenai hasil edit'
                    ArrayList resultAfter = CustomKeywords.'connection.EditSignerData.getBeforeEditDataEditSignerData'(conneSign, 
                        emailOrNIKHash, GlobalVariable.Tenant, getVendor)

                    'jika before sama dengan after'
                    if (resultBefore == resultAfter) {
                        'Write failed stored db'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada Edit Data ')

                        GlobalVariable.FlagFailed = 1
                    }
                } else {
                    'Jika tidak sama, maka continue'
                    continue
                }
            }
        } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Edit Aktivasi')) {
            'Jika aksi edit aktivasi, click button Edit Aktivasi'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditAktivasi'))

            'Klik batal'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

            'Search data menggunakan email'
            searchData(emailOrNIKExcel)

            'Klik button edit aktivasi'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditAktivasi'))

            'Jika headernya muncul'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/Edit Signer Data/lbl_HeaderEdit'), GlobalVariable.TimeOut)) {
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
                    'NIK')) {
                    'convert NIK menjadi SHA256'
                    emailOrNIKHash = convertSHA256(emailOrNIKExcel)
                } else {
                    'email'
                    emailOrNIKHash = emailOrNIKExcel.toString().toUpperCase()
                }
                
                'mengambil return vendor dari UI'
                getVendor = resultCheck(conneSign, emailOrNIKHash)

                'mengambil hasil aktivasi dari db'
                String isActivate = CustomKeywords.'connection.EditSignerData.getStatusActivationEditSignerData'(conneSign, 
                    emailOrNIKHash, GlobalVariable.Tenant, getVendor)

                'Jika input aktivasi yes'
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Aktivasi')) == 'Yes') {
                    'Jika kondisi sekarang masih Belum Aktivasi'
                    if (isActivate == 'Belum Aktivasi') {
                        'Klik button input aktivasi'
                        WebUI.click(findTestObject('Object Repository/Edit Signer Data/slider_StatusAktivasiEditStatus'))
                    }
                } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Aktivasi')) == 
                'No') {
                    'Jika input aktivasi no, namun kondisi sekarang Sudah Aktivasi'
                    if (isActivate == 'Sudah Aktivasi') {
                        'Klik button input aktivasi'
                        WebUI.click(findTestObject('Object Repository/Edit Signer Data/slider_StatusAktivasiEditStatus'))
                    }
                }
                
                'jika check paging confirmation mendapat return true'
                if (checkPagingConfirmation() == true) {
                    'Memberikan delay 3 sec untuk update db'
                    WebUI.delay(3)

                    'Mengambil value db untuk update isActive setelah edit'
                    String isActivateAfter = CustomKeywords.'connection.EditSignerData.getStatusActivationEditSignerData'(
                        conneSign, emailOrNIKHash, GlobalVariable.Tenant, getVendor)

                    'Jika before dan after sama'
                    if (isActivate == isActivateAfter) {
                        GlobalVariable.FlagFailed = 1

                        'Write excel failed store db'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada Edit Aktivasi')
                    }
                } else {
                    'Jika tidak sama, maka continue'
                    continue
                }
            }
        }
        
        'call function verify after edit'
        verifyAfterEdit()

        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def searchData(String emailOrNIKExcel) {
    WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_loginid'), emailOrNIKExcel)

    'click button cari'
    WebUI.click(findTestObject('Edit Signer Data/button_Cari'))
}

def checkPaging(Connection conneSign) {
    'click menu Edit Signer Data'
    WebUI.click(findTestObject('Edit Signer Data/menu_Edit Signer Data'))

    targetEmailCheck = 'ANDY@AD-INS.COM'

    WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_loginid'), targetEmailCheck)

    'click button Reset'
    WebUI.click(findTestObject('Edit Signer Data/button_Set Ulang'))

    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Edit Signer Data/input_loginid')), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_loginid'), targetEmailCheck)

    'click button cari'
    WebUI.click(findTestObject('Edit Signer Data/button_Cari'))

    'verify table muncul'
    WebUI.verifyElementPresent(findTestObject('Edit Signer Data/lbl_VendorValue'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)

    'get text total job dari UI'
    totalEditSignerDataUI = WebUI.getText(findTestObject('Edit Signer Data/label_TotalEditSignerData')).split(' ', -1)

    'get text total job dari DB'
    totalEditSignerDataDB = CustomKeywords.'connection.EditSignerData.countEditSignerDataBasedOnEmail'(conneSign, targetEmailCheck, 
        GlobalVariable.Tenant)

    'verify total job result'
    checkVerifyPaging(WebUI.verifyMatch(totalEditSignerDataUI[0], totalEditSignerDataDB, false, FailureHandling.CONTINUE_ON_FAILURE))

    if (Integer.parseInt(totalEditSignerDataUI[0]) > 10) {
        'click next page'
        WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Next'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/Edit Signer Data/paging_Page'), 
                    'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('Meterai/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Edit Signer Data/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click last page'
        WebUI.click(findTestObject('Edit Signer Data/button_LastPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('Edit Signer Data/label_TotalEditSignerData')).split(
                ' ', -1)[0]) / 10)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().contains('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di last page'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Edit Signer Data/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage + additionalRoundUp), 
                false, FailureHandling.CONTINUE_ON_FAILURE))

        'click first page'
        WebUI.click(findTestObject('Edit Signer Data/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Edit Signer Data/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + '<') + errormessage) + '>')

        return true
    }
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Success'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + lblpopup) + '>')
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

        return true
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def convertSHA256(String input) {
    CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(input)
}

def resultCheck(Connection conneSign, String emailOrNIKHash) {
    ArrayList resultCheck = CustomKeywords.'connection.EditSignerData.getEditDataAndEditActivationEditSignerData'(conneSign, 
        emailOrNIKHash, GlobalVariable.Tenant)

    index = 0

    emailUI = WebUI.getAttribute(findTestObject('Object Repository/Edit Signer Data/input_EmailEdit'), 'value')

    vendorUI = WebUI.getAttribute(findTestObject('Object Repository/Edit Signer Data/input_VendorEdit'), 'value')

    checkVerifyEqualOrMatch(WebUI.verifyMatch(resultCheck[index++], emailUI, false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada email di Edit Data ')

    checkVerifyEqualOrMatch(WebUI.verifyMatch(resultCheck[index++], vendorUI, false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada vendor di Edit Data ')

    vendorUI
}

def checkPagingConfirmation() {
    if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/Edit Signer Data/button_Update'), 'disabled', 
        GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedSaveGagal)
    } else {
        WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Update'))

        if (checkErrorLog() == true) {
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

            return false
        }
        
        if (checkPopup() == true) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            return true
        }
    }
}

def verifyAfterEdit() {
    'check if search dengan email/NIK'
    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
        'Email') && (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email')).length() > 
    0)) {
        emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email'))
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
        'NIK') && (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('No. KTP')).length() > 
    0)) {
        emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('No. KTP'))
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
        'Email')) {
        emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('$Email'))
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Use input ?')).equalsIgnoreCase(
        'NIK')) {
        emailOrNIKExcel = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('$NIK'))
    }
    
    'search data'
    searchData(emailOrNIKExcel)

    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email')).length() > 0) {
        'verify email yang berhasil di edit'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Edit Signer Data/label_Email')), 
                findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase(), 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' email user')
    }
    
    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Nama')).length() > 0) {
        'verify nama yang berhasil di edit'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Edit Signer Data/label_Nama')), 
                findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, rowExcel('Nama')).toUpperCase(), 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' nama user')
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

