import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
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

int isCheckDDL = 0

sheet = 'User Management'

GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)

'call testcase login admin credit'
WebUI.callTestCase(findTestCase('Login/Login_AdmCredit'), [:], FailureHandling.CONTINUE_ON_FAILURE)

'click menu user management'
WebUI.click(findTestObject('User Management/menu_User Management'))

'looping User Management'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}

        'Jika kolom kedua'
        if (GlobalVariable.NumofColm == 2) {
			'get db tenant code dari user yang telah login'
			tenantCode = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, findTestData('Login/Login').getValue(2, 6).toUpperCase())
			
            'call function check paging'
            checkPaging(conneSign)
			
			'call function inputcancel'
			inputCancel()
			
			listDB = CustomKeywords.'connection.UserManagement.getddlRoleUserManagement'(conneSign, tenantCode)
			
			'call function check ddl untuk Peran pada Setting'
			checkDDL(findTestObject('Object Repository/User Management/input_Peran'), listDB)
        }
        
        'jika aksinya adalah new'
        if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('New')) {
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
                WebUI.click(findTestObject('Object Repository/User Management/button_SettingBatal'))

				searchData()
				
                WebUI.delay(3)

                'Klik setting'
                WebUI.click(modifyObjectLblValue)

                'Verify element header insert user management sudah muncul'
                if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_InsertUserManagement'), 
                    GlobalVariable.TimeOut)) {
		
					if (isCheckDDL == 0) {
					'get db tenant code dari user yang telah login'
					tenantCode = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, findTestData('Login/Login').getValue(2, 6).toUpperCase())
			
					listDBPeran = CustomKeywords.'connection.UserManagement.getddlRoleUserManagement'(conneSign, tenantCode)
					
					WebUI.delay(10)
					checkDDL(findTestObject('Object Repository/User Management/input_PeranEdit'), listDBPeran)
				
					//listDBPeran = CustomKeywords.'connection.UserManagement.getddlRoleUserManagement'(conneSign, tenantCode)
					
					//checkDDL(WebUI.click(findTestObject('Object Repository/User Management/input_CabangEdit')), listDBPeran)
					
					isCheckDDL == 1
					}
					
					'db result Edit'
                    resultEdit = CustomKeywords.'connection.UserManagement.getUserManagementonEdit'(conneSign, findTestData(
                            excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), GlobalVariable.Tenant)

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
					if ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14) == '')) {
						'set peran yaitu select role'
						WebUI.setText(findTestObject('Object Repository/User Management/input_PeranEdit'), 'Select Role')

						'enter peran'
						WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))

					} else {
						'set peran sesuai excel'
						WebUI.setText(findTestObject('Object Repository/User Management/input_PeranEdit'), findTestData(
								excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14))

						'enter peran'
						WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))
					}
					
					'jika kolom edit kosong'
					if ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 15) == '')) {
                        'set cabang yaitu select office'
                        WebUI.setText(findTestObject('Object Repository/User Management/input_CabangEdit'), 'Select Office')

                        'enter peran'
                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(
                                Keys.ENTER))
					}
					else {
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
				
				GlobalVariable.FlagFailed = 1
			}
        }
        
        'search data sesuai dengna email dan peran filter'
        searchDataAfterAction(conneSign)

//        WebUI.delay(4)
//
//        'Jika valuenya muncul'
//        if (WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/lbl_Value'), GlobalVariable.TimeOut)) {
//            'ambil jumlah kolom'
//            colValue = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-user-management > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))
//
//            index = 0
//
//            'result db'
//            result = CustomKeywords.'connection.UserManagement.getUserManagement'(conneSign, findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17), GlobalVariable.Tenant)
//
//            'looping berdasarkan kolom'
//            for (i = 1; i <= colValue.size(); i++) {
//                'modify object label value per kolom'
//                modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
//                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[' + 
//                    i) + ']/div', true)
//
//                'Jika yang keempat, yaitu mengenai status gambar centang'
//                if (i == 4) {
//                    'modify object kolom keempat'
//                    modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
//                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper/datatable-body-row/div[2]/datatable-body-cell[' + 
//                        i) + ']/div/em', true)
//
//                    'ambil class dari status tersebut'
//                    valueStatus = WebUI.getAttribute(modifyObjectLblValue, 'class')
//
//                    'Jika value statusnya aktif'
//                    if ((valueStatus == 'ft-check text-success ng-star-inserted') && ((result[index]) == 'Aktif')) {
//                        'verify ui dengan db bahwa statusnya sama dan benar'
//                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
//                    } else if ((valueStatus == 'ng-star-inserted ft-x text-danger') && ((result[index]) == 'Tidak Aktif')) {
//                        'Jika value statusnya tidak aktif, verify ui dengan db bahwa statusnya sama dan benar'
//                        checkVerifyEqualOrMatch(true, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
//                    } else {
//                        'Jika bukan 2 2 nya, maka verify ui dengan db bahwa statusnya sama dan benar'
//                        checkVerifyEqualOrMatch(false, ((' pada kolom ke ' + i) + ' dimana data db adalah ') + (result[index]))
//                    }
//                } else if (i == 5) {
//                    'Jika kolom kelima, yaitu aksi'
//                    if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Setting')) {
//                        'modify object lbl value untuk button setting'
//                        modifyObjectLblValue = WebUI.modifyObjectProperty(findTestObject('Object Repository/User Management/lbl_Value'), 
//                            'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-list-user-management/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[1]/datatable-body-row/div[2]/datatable-body-cell[5]/div/a/em', 
//                            true)
//
//                        WebUI.delay(3)
//
//                        'Klik setting'
//                        WebUI.click(modifyObjectLblValue)
//
//                        'db result Edit'
//                        resultEdit = CustomKeywords.'connection.UserManagement.getUserManagementonEdit'(conneSign, findTestData(
//                                excelPathUserManagement).getValue(GlobalVariable.NumofColm, 17), GlobalVariable.Tenant)
//
//                        indexEdit = 0
//
//                        'verify db dengan ui mengenai nama'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_NamaEdit'), 
//                        'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Nama dengan value db yaitu ' + 
//                        (resultEdit[(indexEdit)]))
//						
//						indexEdit++
//						
//                        'verify db dengan ui mengenai email'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_EmailEdit'), 
//                                    'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Email dengan value db yaitu ' + 
//                            (resultEdit[(indexEdit)]))
//
//						indexEdit++
//						
//                        'verify db dengan ui mengenai Activated Date'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Activated DateEdit'), 
//                                    'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Activate Date dengan value db yaitu ' + 
//                            (resultEdit[(indexEdit)]))
//						
//						indexEdit++
//
//                        'Klik peran'
//                        WebUI.click(findTestObject('Object Repository/User Management/input_PeranEdit'))
//
//                        'get text dari peran'
//                        peranBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))
//
//                        'enter peran'
//                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranEdit'), Keys.chord(Keys.ENTER))
//
//                        'Klik cabang'
//                        WebUI.click(findTestObject('Object Repository/User Management/input_CabangEdit'))
//
//                        'get text dari cabang'
//                        cabangBefore = WebUI.getText(findTestObject('Object Repository/User Management/DDLEdit'))
//
//                        'enter cabang'
//                        WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangEdit'), Keys.chord(
//                                Keys.ENTER))
//
//                        'verify db dengan ui mengenai peran'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(peranBefore, resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), 
//                            ' pada data Edit Peran dengan value db yaitu ' + (resultEdit[(indexEdit)]))
//						
//						indexEdit++
//
//                        'verify db dengan ui mengenai cabang'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(cabangBefore, resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), 
//                            ' pada data Edit Cabang dengan value db yaitu ' + (resultEdit[(indexEdit)]))
//
//						indexEdit++
//						
//                        'verify db dengan ui mengenai Status user'
//                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_Status UserEdit'), 
//                                    'value'), resultEdit[indexEdit], false, FailureHandling.CONTINUE_ON_FAILURE), ' pada data Edit Status user dengan value db yaitu ' + 
//                            (resultEdit[(indexEdit)]))
//						
//						indexEdit++
//						
//                        'Klik batal'
//                        WebUI.click(findTestObject('User Management/button_Batal'))
//                    }
//                } else {
//                    'Jika bukan semuanya, maka verify ui dengan db'
//                    checkVerifyEqualOrMatch(WebUI.verifyMatch(result[index], WebUI.getText(modifyObjectLblValue), false, 
//                            FailureHandling.CONTINUE_ON_FAILURE), ((' pada kolom ke ' + i) + ' dimana data db adalah ') + 
//                        (result[(index)]))
//					
//					index++
//                }
//            }
//        }
		
        if (GlobalVariable.FlagFailed == 0) {
        	'write to excel success'
        	CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'User Management', 0, 
        			GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)				
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
					(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<' + lblpopup + '>')
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

def searchDataAfterAction(Connection conneSign) {
	if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6) == 'Setting') {
		peranAfter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14)
	} else {
		peranAfter = findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 18)
	}
	
	'set text email'
	WebUI.setText(findTestObject('Object Repository/User Management/input_Email'), findTestData(excelPathUserManagement).getValue(
			GlobalVariable.NumofColm, 17))
	
	'set text peran'
	WebUI.setText(findTestObject('Object Repository/User Management/input_Peran'), peranAfter)
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/User Management/input_Peran'), Keys.chord(Keys.ENTER))
	
	'click button cari'
	WebUI.click(findTestObject('User Management/button_Cari'))
	
	'Get Role Name'
	rolename = CustomKeywords.'connection.UserManagement.convertRoleCodetoName'(conneSign, WebUI.getText(findTestObject('Object Repository/User Management/label_Role')))
	
	if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 6) == 'Setting') {
		'verify email after setting'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/label_Email')).toUpperCase(), findTestData(excelPathUserManagement).getValue(
				GlobalVariable.NumofColm, 17).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email tidak sama')		
	} else {
		'verify username after add'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/label_UserName')).toUpperCase(), findTestData(excelPathUserManagement).getValue(
				GlobalVariable.NumofColm, 8).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' username tidak sama')
		
		'verify email after add'
		checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/label_Email')).toUpperCase(), findTestData(excelPathUserManagement).getValue(
				GlobalVariable.NumofColm, 9).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email tidak sama')		
	}
	
	'verify Role after add / setting'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(rolename.toUpperCase(), peranAfter.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Role tidak sama')
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
            (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + '<' + errormessage + '>')

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
		
		'get total page'
		variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-list-user-management > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
		
		'click last page'
		WebUI.click(findTestObject('User Management/button_LastPage'))
	
		'get total data'
		lastPage = Double.parseDouble(WebUI.getText(findTestObject('User Management/label_TotalData')).split(' ',-1)[0])/10
		
		'jika hasil perhitungan last page memiliki desimal'
		if (lastPage.toString().contains('.0')) {
			'tidak ada round up'
			additionalRoundUp = 0
		} else {
			'round up dengan tambahan 0.5'
			additionalRoundUp = 0.5
		}
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('User Management/paging_Page'), 'aria-label',
					FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage+additionalRoundUp).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

        'click first page'
        WebUI.click(findTestObject('User Management/button_FirstPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('User Management/paging_Page'), 'ng-reflect-page', 
                    FailureHandling.CONTINUE_ON_FAILURE), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB) {
	'declare array untuk menampung ddl'
	ArrayList<String> list = []

	'click untuk memunculkan ddl'
	WebUI.click(objectDDL)

	'get id ddl'
	id = WebUI.getAttribute(findTestObject('isiSaldo/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

	'looping untuk get ddl kedalam array'
	for (i = 1; i < variable.size(); i++) {
		'modify object DDL'
		modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('isiSaldo/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' +
			id) + '-') + i) + '\']', true)

		'add ddl ke array'
		list.add(WebUI.getText(modifyObjectDDL))
	}
	
	'verify ddl ui = db'
	checkVerifyEqualOrMatch(listDB.containsAll(list), ' DDL SALDO')

	'verify jumlah ddl ui = db'
	checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah DDL Saldo')
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

def inputCancel() {
	'click menu user management'
	WebUI.click(findTestObject('User Management/menu_User Management'))
	
	'Klik button baru'
	WebUI.click(findTestObject('User Management/btn_Baru'))
	
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
	
	'Klik button batal'
	WebUI.click(findTestObject('User Management/button_Batal'))

	'check if sudah cancel dan pindah halaman'
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/User Management/input_CabangNew'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		checkVerifyEqualOrMatch(false, 'FAILED TO CANCEL')
	}
	
	'Klik button baru'
	WebUI.click(findTestObject('User Management/btn_Baru'))
	
	'verify field nama lengkap kosong'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_NamaLengkapNew'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field nama lengkap tidak kosong')
	
	'verify field email kosong'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_EmailNew'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field email tidak kosong')

	'Klik button baru'
	WebUI.click(findTestObject('Object Repository/User Management/input_PeranNew'))
	
	'verify field role tereset'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/selectedDDL')), 'Pilih role', false, FailureHandling.CONTINUE_ON_FAILURE), ' field role tidak tereset default')

	'Enter peran'
	WebUI.sendKeys(findTestObject('Object Repository/User Management/input_PeranNew'), Keys.chord(Keys.ENTER))
	
	'verify field access code kosong'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/User Management/input_KodeAksesNew'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field kode akses tidak kosong')
	
	'Klik button baru'
	WebUI.click(findTestObject('Object Repository/User Management/input_CabangNew'))
	
	'verify field office tereset'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/User Management/selectedDDL')), 'Pilih cabang', false, FailureHandling.CONTINUE_ON_FAILURE), ' field office tidak tereset default')
	
	'Enter cabang'
	WebUI.sendKeys(findTestObject('Object Repository/User Management/input_CabangNew'), Keys.chord(Keys.ENTER))
	
	'Klik button batal'
	WebUI.click(findTestObject('User Management/button_Batal'))
}
