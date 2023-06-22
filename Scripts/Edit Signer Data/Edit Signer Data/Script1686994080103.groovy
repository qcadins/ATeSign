import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathEditSignerData).columnNumbers

'get tenant dari setting'
GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)

'looping Edit Signer Data'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

		'Declare variable yang dibutuhkan'
		String email, emailOrNIKHash, getVendor
		
        'Jika kolom kedua'
        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin credit'
            WebUI.callTestCase(findTestCase('Login/Login_AdmCredit'), [:], FailureHandling.CONTINUE_ON_FAILURE)

            'click menu Edit Signer Data'
            WebUI.click(findTestObject('Edit Signer Data/menu_Edit Signer Data'))

            'call function check paging'
            //checkPaging(conneSign)
        }
        
        'check if search dengan email/NIK'
        if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('Email')) {
            email = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 10)
        } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('NIK')) {
            email = findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 11)
        }
        
		'search data menggunakan email'
        searchData(email)

		'Jika aksi Edit Data'
        if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit Data')) {
			WebUI.delay(4)
            'click button Edit Data'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditData'))

			'Klik batal'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

			'search data menggunakan email'
            searchData(email)
			WebUI.delay(4)
			'klik aksi edit data'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditData'))

			'Jika sudah ada header edit'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/Edit Signer Data/lbl_HeaderEdit'), GlobalVariable.TimeOut)) {
				'Jika input menggunakan NIK'
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('NIK')) {
                    'convert input menjadi SHA256'
					emailOrNIKHash = convertSHA256(email)
                } else {
					'email'
                    emailOrNIKHash = email
                }
                
				'mengambil return vendor dari UI sekaligus check result'
                getVendor = resultCheck(conneSign, emailOrNIKHash)

				'ambil hasil db sebelum edit'
                ArrayList resultBefore = CustomKeywords.'connection.EditSignerData.getBeforeEditDataEditSignerData'(conneSign, 
                    emailOrNIKHash, GlobalVariable.Tenant, getVendor)

				'Set Text nama'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_NamaEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, 13))

				'Set text email'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_EmailNewEditData'), findTestData(
                        excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 14))

				'Set text nomor telepon'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_noPhoneEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, 15))

				'Set text nomor ktp'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_noKtpEditData'), findTestData(excelPathEditSignerData).getValue(
                        GlobalVariable.NumofColm, 16))

				'set text tanggal lahir'
                WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_dateofBirthEditData'), findTestData(
                        excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 17))
				
				'Jikak checkPagingConfirmationnya dapat return true'
                if (checkPagingConfirmation() == true) {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Edit Signer Data', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					'delay update db selama 3'
					WebUI.delay(3)
					'ambil value db mengenai hasil edit'
                    ArrayList resultAfter = CustomKeywords.'connection.EditSignerData.getBeforeEditDataEditSignerData'(conneSign, 
                        emailOrNIKHash, GlobalVariable.Tenant, getVendor)

					'jika before sama dengan after'
                    if (resultBefore.equals(resultAfter) == true) {
						'Write failed stored db'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada Edit Data ')
                    }
                } else {
					'Jika tidak sama, maka continue'
					continue
				}

            }
        }	else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit Aktivasi')) {
            'Jika aksi edit aktivasi, click button Edit Aktivasi'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditAktivasi'))

			'Klik batal'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

			'Search data menggunakan email'
            searchData(email)

			'Klik button edit aktivasi'
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_AksiEditAktivasi'))

			'Jika headernya muncul'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/Edit Signer Data/lbl_HeaderEdit'), GlobalVariable.TimeOut)) {
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('NIK')) {
                    'convert NIK menjadi SHA256'
					emailOrNIKHash = convertSHA256(email)
                } else {
					'email'
                    emailOrNIKHash = email
                }
                
				'mengambil return vendor dari UI'
                getVendor = resultCheck(conneSign, emailOrNIKHash)

				'mengambil hasil aktivasi dari db'
                String isActivate = CustomKeywords.'connection.EditSignerData.getStatusActivationEditSignerData'(conneSign, 
                    emailOrNIKHash, GlobalVariable.Tenant, getVendor)

                'Jika input aktivasi yes'
                if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 19) == 'Yes') {
                    'Jika kondisi sekarang masih Belum Aktivasi'
                    if (isActivate == 'Belum Aktivasi') {
                        'Klik button input aktivasi'
                        WebUI.click(findTestObject('Object Repository/Edit Signer Data/slider_StatusAktivasiEditStatus'))
                    }
                } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 19) == 'No') {
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
                    if (isActivate.equals(isActivateAfter) == true) {
						'Write excel failed store db'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' pada Edit Aktivasi')
                    }
                } else {
					'Jika tidak sama, maka continue'
					continue
				}
            }
        } else {
			if (GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Edit Signer Data', 0,
					GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
			}
		}
    }
}

def searchData(String email) {
    WebUI.setText(findTestObject('Object Repository/Edit Signer Data/input_loginid'), email)

    'click button cari'
    WebUI.click(findTestObject('Edit Signer Data/button_Cari'))
}

def verifyPage(String email, int i, int j) {
    if (WebUI.verifyElementPresent(findTestObject('Object Repository/Edit Signer Data/lbl_VendorValue'), GlobalVariable.TimeOut)) {
        if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 9).equalsIgnoreCase('NIK')) {
            email = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(email)
        }
        
        ArrayList result = CustomKeywords.'connection.EditSignerData.getEditSignerData'(conneSign, email, GlobalVariable.Tenant)

        index = 0

        'get row beranda'
        rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-data-pengguna > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

        'get row beranda'
        colBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-data-pengguna > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

        for (i = 1; i >= rowBeranda.size(); i++) {
            for (j = 1; j >= (colBeranda.size() - 1); j++) {
                'modify object text document template name di beranda'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Edit Signer Data/lbl_VendorValue'), 'xpath', 
                    'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-data-pengguna/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                    i) + ']/datatable-body-row/div[2]/datatable-body-cell[') + j) + ']/div/p', true)

                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectLblValue), result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ((' pada data ' + WebUI.getText(modifyObjectLblValue)) + ' dan ') + (result[(index - 1)]))
            }
        }
    }
}

def checkPaging(Connection conneSign) {
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

        'verify paging di last page'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Edit Signer Data/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), WebUI.getAttribute(findTestObject('Edit Signer Data/page_Active'), 
                    'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace('page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + errormessage)

        return true
    }
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Success'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + lblpopup)
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

        return true
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def convertSHA256(String input) {
    return CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(input)
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

    return vendorUI
}

def checkPagingConfirmation() {
    if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/Edit Signer Data/button_Update'), 'disabled', 
        GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Edit Signer Data', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedSaveGagal)
    } else {
        WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Update'))

        if (checkErrorLog() == true) {
            WebUI.click(findTestObject('Object Repository/Edit Signer Data/button_Batal'))

            return false
        }
        
        if (checkPopup() == true) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Edit Signer Data', 0, 
                GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

            return true
        }
    }
}

