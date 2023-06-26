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
int countColmExcel = findTestData(excelPathUserManagement).columnNumbers

sheet = 'User Management'

GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)

'looping User Management'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'Jika kolom kedua'
        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin credit'
            WebUI.callTestCase(findTestCase('Login/Login_AdmCredit'), [:], FailureHandling.CONTINUE_ON_FAILURE)

            'click menu user management'
            WebUI.click(findTestObject('User Management/menu_User Management'))

            'call function check paging'
            checkPaging(conneSign)
        }
        
        'jika aksinya adalah new'
        if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('New')) {
            'Klik button baru'
            WebUI.click(findTestObject('User Management/btn_Baru'))

            'Klik button batal'
            WebUI.click(findTestObject('User Management/button_Batal'))

            'Klik button baru'
            WebUI.click(findTestObject('User Management/btn_Baru'))

            'verify element sudah berubah'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_InsertUserManagement'), 
                GlobalVariable.TimeOut)) {
                'Set text nama lengkap'
                WebUI.setText(findTestObject('Object Repository/User Management/input_NamaLengkapNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 8))

                'Set text email'
                WebUI.setText(findTestObject('Object Repository/User Management/input_EmailNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 9))

                'Set text peran'
                WebUI.setText(findTestObject('Object Repository/User Management/input_PeranNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 10))

                'Enter peran'
                WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranNew'), Keys.chord(Keys.ENTER))

                'Set text kode akses'
                WebUI.setText(findTestObject('Object Repository/User Management/input_KodeAksesNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 11))

                'Input cabang'
                WebUI.setText(findTestObject('Object Repository/User Management/input_CabangNew'), findTestData(excelPathUserManagement).getValue(
                        GlobalVariable.NumofColm, 12))

                'Enter cabang'
                WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangNew'), Keys.chord(Keys.ENTER))

                if (checkPagingConfirmation(' pada menu New ') == true) {
                    continue
                }
            }
        } else if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Setting')) {
            searchData()	

            'Verify element value'
            if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_Value'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'modify object lbl value untuk button setting'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                    'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[5]/div/a/em', 
                    true)

                WebUI.delay(3)

                'Klik setting'
                WebUI.click(modifyObjectLblValue)

                'Klik batal'
                WebUI.click(findTestObject('Object Repository/User Management/button_Batal'))

				searchData()
				
                WebUI.delay(3)

                'Klik setting'
                WebUI.click(modifyObjectLblValue)

                'Verify element header insert user management sudah muncul'
                if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_InsertUserManagement'), 
                    GlobalVariable.TimeOut)) {
                    'db result Edit'
                    resultEdit = CustomKeywords.'connection.UserManagement.getUserManagementonEdit'(conneSign, findTestData(
                            excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17), GlobalVariable.Tenant)

                    index = 0

                    'verify db dengan ui mengenai nama'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_NamaEdit'), 
                                'value'), resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(index)]))
					
					index++

                    'verify db dengan ui mengenai email'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_EmailEdit'), 
                                'value'), resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Email dengan value db yaitu ' + 
                        (resultEdit[(index)]))

					index++
					
                    'verify db dengan ui mengenai Activated Date'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Activated DateEdit'), 
                                'value'), resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Activate Date dengan value db yaitu ' + 
                        (resultEdit[(index)]))

					index++
					
                    'Klik peran'
                    WebUI.click(findTestObject('Object Repository/User Management/input_PeranEdit'))

                    'get text dari peran'
                    peranBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))

                    'enter peran'
                    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

                    'Klik cabang'
                    WebUI.click(findTestObject('Object Repository/User Management/input_CabangEdit'))

                    'get text dari cabang'
                    cabangBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))

                    'enter cabang'
                    WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(Keys.ENTER))

                    'verify db dengan ui mengenai peran'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(peranBefore, resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' pada data Edit Peran dengan value db yaitu ' + (resultEdit[(index)]))
					
					index++

                    'verify db dengan ui mengenai cabang'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(cabangBefore, resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' pada data Edit Cabang dengan value db yaitu ' + (resultEdit[(index)]))
					
					index++

                    'verify db dengan ui mengenai Status user'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Status UserEdit'), 
                                'value'), resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Status user dengan value db yaitu ' + 
                        (resultEdit[(index)]))
					
					index++

                    'jika kolom edit kosong'
                    if ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14) == '') && (findTestData(
                        excelPathUserManagement).getValue(GlobalVariable.NumofColm, 15) == '')) {
                        'set peran yaitu select role'
                        WebUI.setText(findTestObject('Object Repository/User Management/input_PeranEdit'), 'Select Role')

                        'enter peran'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

                        'set cabang yaitu select office'
                        WebUI.setText(findTestObject('Object Repository/User Management/input_CabangEdit'), 'Select Office')

                        'enter peran'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(
                                Keys.ENTER))
                    } else {
                        'set peran sesuai excel'
                        WebUI.setText(findTestObject('Object Repository/User Management/input_PeranEdit'), findTestData(
                                excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14))

                        'enter peran'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

                        'set cabang sesuai excel'
                        WebUI.setText(findTestObject('Object Repository/User Management/input_CabangEdit'), findTestData(
                                excelPathUserManagement).getValue(GlobalVariable.NumofColm, 15))

                        'enter cabang'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(
                                Keys.ENTER))
                    }
					
					if (checkPagingConfirmation (' pada menu Edit ') == true) {
						continue
					}
                }
            } else {
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) +
					';') + GlobalVariable.ReasonFailedNoneUI))
			}
        }
        
        'search data sesuai dengna email dan peran filter'
        searchDataAfterAction()

        WebUI.delay(4)

        'Jika valuenya muncul'
        if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_Value'), GlobalVariable.TimeOut)) {
            'ambil jumlah kolom'
            colValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-user-management > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))

            index = 0

            'result db'
            result = CustomKeywords.'connection.UserManagement.getUserManagement'(conneSign, findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17), GlobalVariable.Tenant)

            'looping berdasarkan kolom'
            for (i = 1; i <= colValue.size(); i++) {
                'modify object label value per kolom'
                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[' + 
                    i) + ']/div', true)

                'Jika yang keempat, yaitu mengenai status gambar centang'
                if (i == 4) {
                    'modify object kolom keempat'
                    modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
                        i) + ']/div/em', true)

                    'ambil class dari status tersebut'
                    valueStatus = WebUI.getAttribute(modifyObjectLblValue, 'class')

                    'Jika value statusnya aktif'
                    if ((valueStatus == 'ft-check text-success ng-star-inserted') && ((result[index]) == 'Aktif')) {
                        'verify ui dengan db bahwa statusnya sama dan benar'
                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    } else if ((valueStatus == 'ng-star-inserted ft-x text-danger') && ((result[index]) == 'Tidak Aktif')) {
                        'Jika value statusnya tidak aktif, verify ui dengan db bahwa statusnya sama dan benar'
                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    } else {
                        'Jika bukan 2 2 nya, maka verify ui dengan db bahwa statusnya sama dan benar'
                        checkVerifyEqualOrMatch(false, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
                    }
                } else if (i == 5) {
                    'Jika kolom kelima, yaitu aksi'
                    if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Setting')) {
                        'modify object lbl value untuk button setting'
                        modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
                            'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[5]/div/a/em', 
                            true)

                        WebUI.delay(3)

                        'Klik setting'
                        WebUI.click(modifyObjectLblValue)

                        'db result Edit'
                        resultEdit = CustomKeywords.'connection.UserManagement.getUserManagementonEdit'(conneSign, findTestData(
                                excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17), GlobalVariable.Tenant)

                        indexEdit = 0

                        'verify db dengan ui mengenai nama'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_NamaEdit'), 
                        'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
                        (resultEdit[(indexEdit)]))
						
						indexEdit++
						
                        'verify db dengan ui mengenai email'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_EmailEdit'), 
                                    'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Email dengan value db yaitu ' + 
                            (resultEdit[(indexEdit)]))

						indexEdit++
						
                        'verify db dengan ui mengenai Activated Date'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Activated DateEdit'), 
                                    'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Activate Date dengan value db yaitu ' + 
                            (resultEdit[(indexEdit)]))
						
						indexEdit++

                        'Klik peran'
                        WebUI.click(findTestObject('Object Repository/User Management/input_PeranEdit'))

                        'get text dari peran'
                        peranBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))

                        'enter peran'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

                        'Klik cabang'
                        WebUI.click(findTestObject('Object Repository/User Management/input_CabangEdit'))

                        'get text dari cabang'
                        cabangBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))

                        'enter cabang'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(
                                Keys.ENTER))

                        'verify db dengan ui mengenai peran'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(peranBefore, resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' pada data Edit Peran dengan value db yaitu ' + (resultEdit[(indexEdit)]))
						
						indexEdit++
						
                        'verify db dengan ui mengenai cabang'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(cabangBefore, resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' pada data Edit Cabang dengan value db yaitu ' + (resultEdit[(index)]))

						indexEdit++
						
                        'verify db dengan ui mengenai Status user'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Status UserEdit'), 
                                    'value'), resultEdit[index], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Status user dengan value db yaitu ' + 
                            (resultEdit[(index)]))
						
						indexEdit++
                        'Klik batal'
                        WebUI.click(findTestObject('User Management/button_Batal'))
                    }
                } else {
                    'Jika bukan semuanya, maka verify ui dengan db'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result[index], WebUI.getText(modifyObjectLblValue), false, 
                            FailureHandling.CONTINUE_ON_FAILURE), ((' pada kolom ke ' + i) + ' dimana data db adalah ') + 
                        (result[(index)]))
					
					index++
                }
            }
			
			if (GlobalVariable.FlagFailed == 0) {
				'write to excel success'
				CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'User Management', 0, 
				GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)				
			}
        }
    }
}

def checkPagingConfirmation(String reason) {
	if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6) == 'Setting') {
		modifyObjectBtnSave = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/button_Lanjut'),
			'class', 'equals', 'btn btn-info mr-5', true)
	} else {
		modifyObjectBtnSave = findTestObject('Object Repository/User Management/button_Lanjut')
	}
		
    'Jika button lanjut disabled'
    if (WebUI.verifyElementHasAttribute(modifyObjectBtnSave, 'disabled', GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'Klik batal'
        WebUI.click(findTestObject('User Management/button_Batal'))

        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedSaveGagal'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedSaveGagal) + 
            reason)

        return true
    } else {
        'Jika button lanjut tidak disabled, klik button lanjut'
        WebUI.click(modifyObjectBtnSave)

        'Jika check error log ada'
        if (checkErrorLog() == true) {
            'Klik batal'
            WebUI.click(findTestObject('User Management/button_Batal'))

            return true
        }
        
		'Jika popup muncul'
		if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		
			'label popup diambil'
			lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)
	
			'Jika popup bukan success'
			if (!(lblpopup.contains('Success'))) {
				'Tulis di excel sebagai failed dan error.'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + lblpopup)
			} else {
				'write to excel success'
				CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'User Management', 0,
					GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	
				'call testcase store db'
				WebUI.callTestCase(findTestCase('User Management/User Management Store DB'), [:], FailureHandling.CONTINUE_ON_FAILURE)
			}
			'Klik OK untuk popupnya'
			WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
		}

    }
}

def searchDataAfterAction() {
	if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6) == 'Setting') {
		peranAfter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14)
	} else {
		peranAfter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 18)
	}
		WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), findTestData(excelPathUserManagement).getValue(
		GlobalVariable.NumofColm, 17))
	
		WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'),peranAfter)
	
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))
	
		'click button cari'
		WebUI.click(findTestObject('User Management/button_Cari'))
	}

def searchData() {
    WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), findTestData(excelPathUserManagement).getValue(
    GlobalVariable.NumofColm, 17))

    WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), findTestData(excelPathUserManagement).getValue(
    GlobalVariable.NumofColm, 18))

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


