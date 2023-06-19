import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathEditSignerData).columnNumbers

sheet = 'User Management'

int i, j

'looping DocumentMonitoring'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathEditSignerData).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        String emailFilter

        String peranFilter

  //      if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin'
            WebUI.callTestCase(findTestCase('Login/Login_AdmCredit'), [:], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu meterai'
            WebUI.click(findTestObject('User Management/menu_User Management'))

            'call function check paging'
  //          checkPaging(conneSign)
  //      }
        
        'Klik tombol pengaturan dokumen'
        if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('New')) {
            WebUI.click(findTestObject('User Management/btn_Baru'))

            WebUI.click(findTestObject('User Management/button_Batal'))

            WebUI.click(findTestObject('User Management/btn_Baru'))

            if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_InsertUserManagement'), 
                GlobalVariable.TimeOut)) {
                WebUI.setText(findTestObject('Object Repository/User Management/input_NamaLengkapNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 8))

                WebUI.setText(findTestObject('Object Repository/User Management/input_EmailNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 9))

                WebUI.setText(findTestObject('Object Repository/User Management/input_PeranNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 10))

                WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranNew'), Keys.chord(Keys.ENTER))

                WebUI.setText(findTestObject('Object Repository/User Management/input_KodeAksesNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 11))

                WebUI.setText(findTestObject('Object Repository/User Management/input_CabangNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 12))

                WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangNew'), Keys.chord(Keys.ENTER))

                if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/User Management/button_Lanjut'), 'disabled', 
                    GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
				
					WebUI.click(findTestObject('User Management/button_Batal'))
					
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' pada page New ')

                    continue
                } else {
                    WebUI.click(findTestObject('Object Repository/User Management/button_Lanjut'))

                    if (checkErrorLog() == true) {
                        WebUI.click(findTestObject('User Management/button_Batal'))

                        continue
                    }
                    
                    if (checkPopup() == true) {
                        'write to excel success'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                        emailFilter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 9)

                        peranFilter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 10)
                    }
                }
            }
        } else if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Setting')) {
            WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), findTestData(excelPathUserManagement).getValue(
                    GlobalVariable.NumofColm, 14))

            WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), findTestData(excelPathUserManagement).getValue(
                    GlobalVariable.NumofColm, 15))

            'enter untuk set status meterai'
            WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))

            'click button cari'
            WebUI.click(findTestObject('User Management/button_Cari'))

            if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_Value'), GlobalVariable.TimeOut)) {
                'modify object text document template name di beranda'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                    'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[5]/div/a/em', 
                    true)

				WebUI.delay(3)
				
                WebUI.click(modifyObjectLblValue)

                WebUI.click(findTestObject('Object Repository/User Management/button_Batal'))

                WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 14))

                WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 15))

                'enter untuk set status meterai'
                WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))

                'click button cari'
                WebUI.click(findTestObject('User Management/button_Cari'))

				WebUI.delay(3)
				
                WebUI.click(modifyObjectLblValue)

                if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_InsertUserManagement'), 
                    GlobalVariable.TimeOut)) {
                    resultEdit = CustomKeywords.'connection.UserManagement.getUserManagementonEdit'(conneSign, findTestData(
                            excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14))

                    index = 0

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_NamaEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_EmailEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Activated DateEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_PeranEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_CabangEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Status UserEdit'), 
                                'value'), resultEdit[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index - 1)]))

                    'set text status meterai'
                    WebUI.setText(findTestObject('Object Repository/User Management/input_PeranEdit'), findTestData(excelPathUserManagement).getValue(
                            GlobalVariable.NumofColm, 17))

                    'enter untuk set status meterai'
                    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

                    'set text status meterai'
                    WebUI.setText(findTestObject('Object Repository/User Management/input_CabangEdit'), findTestData(excelPathUserManagement).getValue(
                            GlobalVariable.NumofColm, 18))

                    'enter untuk set status meterai'
                    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(Keys.ENTER))

                    if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/User Management/button_Simpan'), 
                        'disabled', GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
					WebUI.click(findTestObject('User Management/button_Batal'))
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + ' pada page Edit ')

                        continue
                    } else {
                        WebUI.click(findTestObject('Object Repository/User Management/button_Simpan'))

                        if (checkErrorLog() == true) {
                            WebUI.click(findTestObject('User Management/button_Batal'))

                            continue
                        }
                        
                        if (checkPopup() == true) {
                            if (GlobalVariable.FlagFailed == 0) {
                                'write to excel success'
                                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                            }
                            
                            emailFilter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14)

                            peranFilter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17)
                        }
                    }
                }
            }
        }
        
        searchData(emailFilter, peranFilter)

        if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_Value'), GlobalVariable.TimeOut)) {
            'count signbox'
            rowValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-user-management > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

            index = 0

            result = CustomKeywords.'connection.UserManagement.getUserManagement'(conneSign, emailFilter)

            for (i = 1; i <= rowValue.size(); i++) {
                'modify object text document template name di beranda'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[' + 
                    i) + ']/div', true)

                if (i == 4) {
                    'modify object text document template name di beranda'
                    modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                        i) + ']/div/em', true)

                    valueStatus = WebUI.getAttribute(modifyObjectLblValue, 'class')

                    if ((valueStatus == 'ft-check text-success ng-star-inserted') && ((result[index]) == 'Aktif')) {
                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    } else if ((valueStatus == 'ng-star-inserted ft-x text-danger') && ((result[index]) == 'Tidak Aktif')) {
                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    } else {
                        checkVerifyEqualOrMatch(false, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    }
                } else if (i == 5) {
                    continue
                } else {
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result[index++], WebUI.getText(modifyObjectLblValue), false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[(index - 1)]))
                }
            }
        }
    }
}

def searchData(String emailFilter, String peranFilter) {
    WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), emailFilter)

    WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), peranFilter)

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('User Management/button_Cari'))
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + errormessage)

        return true
    }
}

def checkPaging(Connection conneSign) {
    totalUserManagementDB = CustomKeywords.'connection.UserManagement.getTotalUserManagement'(conneSign)

    'set text no kontrak'
    WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), 'userciie@ad-ins.com')

    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), 'Customer')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))

    'set text lini bisnis'
    WebUI.click(findTestObject('Object Repository/User Management/button_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/input_Email'), FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/User Management/input_Peran'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('User Management/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('User Management/button_Cari'))

    'set text tanggal pengiriman ke'
    totalUserManagementUI = WebUI.getText(findTestObject('Object Repository/User Management/label_TotalUserManagement')).split(
        ' ', -1)

    'verify total Meterai'
    checkVerifyPaging(WebUI.verifyMatch(totalUserManagementUI[0], totalUserManagementDB, false, FailureHandling.CONTINUE_ON_FAILURE))

    if (Integer.parseInt(totalUserManagementUI[0]) > 10) {
        'click next page'
        WebUI.click(findTestObject('Object Repository/User Management/button_Next'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/paging_Page'), 
                    'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('Meterai/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('User Management/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click last page'
        WebUI.click(findTestObject('User Management/button_LastPage'))

        'verify paging di last page'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('User Management/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), WebUI.getAttribute(findTestObject('User Management/page_Active'), 
                    'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace('page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'click first page'
        WebUI.click(findTestObject('User Management/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('User Management/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
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
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + lblpopup)
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))

        return true
    }
}

