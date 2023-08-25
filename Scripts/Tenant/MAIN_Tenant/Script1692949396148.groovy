import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathTenant).columnNumbers

'call test case login admin esign'
WebUI.callTestCase(findTestCase('Login/Login_AdminEsign'), [:], FailureHandling.STOP_ON_FAILURE)

'click menu tenant'
WebUI.click(findTestObject('Tenant/menu_Tenant'))

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 5))

'looping tenant'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		if (GlobalVariable.NumofColm == 2) {
			'call function check paging'
			checkPaging(conneSign)
			
			'call function cancel'
			inputCancel()
		}
		
        'check if action new/services'
        if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New')) {
            'click button Baru'
            WebUI.click(findTestObject('Tenant/Button_Baru'))

            'get total form'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

            'input nama tenant'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    12))

            'input tenant code'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    13))

            'input label ref number'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, 14))

            'check if ingin menginput api secara manual/generate'
            if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15) == 'No') {
                'input API Key'
                WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                        16))
            } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
                'click button generate api key'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

                'get api key'
                APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

                'write to excel api key'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumofColm - 
                    1, APIKEY)
            }
            
            'get array services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).split(';', -1)

            'get array batas saldo dari excel'
            arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

            'looping untuk input bata saldo'
            for (index = 5; index < variable.size(); index++) {
                'modify object button services'
                modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/button', true)

                'break if index udah lebih dari 23 HARDCODE karena tidak bisa di track objectnya'
                if (index > 23) {
                    break
                }
                
                'looping untuk array service excel'
                for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
                    'check if button contain service name'
                    if (WebUI.verifyElementNotPresent(modifyObjectButtonServices, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        continue
                    } else if (!(WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel]))) {
                        continue
                    } else if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])) {
                        'click button add services'
                        WebUI.click(modifyObjectButtonServices)

                        'modify object input services'
                        modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                            index) + ']/div/input', true)

                        'input batas saldo'
                        WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

                        break
                    }
                }
            }
            
            'get array email reminder dari excel'
            arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).split(';', -1)

            'looping untuk input email reminder'
            for (index = 1; index <= arrayEmailReminder.size(); index++) {
                'modify object input email'
                modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    (23 + index)) + ']/div/input', true)

                'click tambah email'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

                'input email reminder'
                WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(index - 1)])
            }
            
            'input email user admin'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, 22))

            'input kode akses user admin'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, 23))

            'check if mandatory complete dan button simpan clickable'
            if (!(WebUI.verifyElementHasAttribute(findTestObject('Tenant/TenantBaru/button_Simpan'),'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
                'click button simpan'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_Simpan'))

                'verify pop up message berhasil'
                if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('Tenant/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					
					'call function after add'
					verifyAfterAddEdit()
                }
            } else {
				'click button Batal'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))
				
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) +
					';') + GlobalVariable.ReasonFailedSaveGagal)
				
				GlobalVariable.FlagFailed = 1
				}
			 
				 if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
				
				GlobalVariable.FlagFailed = 1
            }
			
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Service')) {
            'call function search'
            searchTenant()

            'click button services balance'
            WebUI.click(findTestObject('Tenant/button_ServiceBalance'))

            'get array Services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 25).split(';', -1)

            'get array Vendor dari excel'
            arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 26).split(';', -1)

            'looping untuk input services check'
            for (index = 0; index < arrayServices.size(); index++) {
                'modify object checkbox'
                modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath', 
                    'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

                'check if check box is unchecked'
                if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click checkbox'
                    WebUI.click(modifyObjectCheckbox)
                }
            }
            
            'get array Services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 27).split(';', -1)

            'get array Vendor dari excel'
            arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 28).split(';', -1)

            'looping untuk input services uncheck'
            for (index = 0; index < arrayServices.size(); index++) {
                'modify object checkbox'
                modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath', 
                    'equals', ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

                'check if check box is checked'
                if (WebUI.verifyElementChecked(modifyObjectCheckbox, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click checkbox'
                    WebUI.click(modifyObjectCheckbox)
                }
            }
            
            'check if mandatory complete dan button simpan clickable'
            if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Services/button_Simpan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
                'click button simpan'
                WebUI.click(findTestObject('Tenant/Services/button_Simpan'))

                'check if alert berhasil muncul'
                if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
                    'berhasil')) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Services/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
            }
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit')) {
            'call function search'
            searchTenant()

            'click button edit'
            WebUI.click(findTestObject('Tenant/button_Edit'))

			'input nama tenant'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    12))
			
            'input tenant code'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    13), FailureHandling.OPTIONAL)

            'input label ref number'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, 14))

            'check if ingin menginput api secara manual/generate'
            if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15) == 'No') {
                'input API Key'
                WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                        16))
            } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
                'click button generate api key'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

                'get api key'
                APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

                'write to excel api key'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumofColm - 
                    1, APIKEY)
            }
            
            'get total form'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

            'get array services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).split(';', -1)

            'get array batas saldo dari excel'
            arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

            'looping untuk input bata saldo'
            for (index = 5; index < variable.size(); index++) {
                'modify object button Hapus'
                modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/div/button', true)

                'modify object label services'
                modifyObjectLabelServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/label', true)

                'modify object button services'
                modifyObjectButtonServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/button', true)

                'break if index udah lebih dari 23 HARDCODE karena tidak bisa di track objectnya'
                if (index > 23) {
                    break
                }
                
                'looping untuk array service excel'
                for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
                    'check if label present'
                    if (WebUI.verifyElementPresent(modifyObjectButtonHapus, GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
                    WebUI.verifyElementPresent(modifyObjectLabelServices, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'check if button label = service name di excel'
                        if (WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel])) {
                            'modify object input services'
                            modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                                index) + ']/div/input', true)

                            'input batas saldo'
                            WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

                            break
                        } else if (!(WebUI.getText(modifyObjectLabelServices).equalsIgnoreCase(arrayServices[indexExcel]))) {
                            if ((indexExcel + 1) == arrayServices.size()) {
                                'click button hapus'
                                WebUI.click(modifyObjectButtonHapus)

                                break
                            }
                        }
                    } else if (WebUI.verifyElementPresent(modifyObjectButtonServices, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        if (WebUI.getText(modifyObjectButtonServices).contains(arrayServices[indexExcel])) {
                            'click button add service'
                            WebUI.click(modifyObjectButtonServices)

                            'modify object input services'
                            modifyObjectInputServices = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                                index) + ']/div/input', true)

                            'input batas saldo'
                            WebUI.setText(modifyObjectInputServices, arrayServicesBatasSaldo[indexExcel])

                            break
                        }
                    }
                }
            }
            
            'get array email reminder dari excel'
            arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).split(';', -1)

            'looping untuk hapus email reminder yang tidak ada di excel'
            for (index = 24; index <= variable.size(); index++) {
                'modify object input email'
                modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/div/input', true)

                'modify object button hapus'
                modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/div/button', true)

                if (WebUI.verifyElementPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'looping untuk input email reminder'
                    for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) {
                        'check if email ui = excel'
                        if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
                            arrayEmailReminder[(indexexcel - 1)])) {
                            break
                        } else {
                            if (indexexcel == arrayEmailReminder.size()) {
                                'click hapus email'
                                WebUI.click(modifyObjectButtonHapus)

                                index--
                            }
                        }
                    }
                } else {
                    break
                }
            }
            
            'looping untuk input email reminder yang tidak ada di ui'
            for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) {
                'looping untuk input email reminder'
                for (index = 24; index <= variable.size(); index++) {
                    'modify object input email'
                    modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                        index) + ']/div/input', true)

                    if (WebUI.verifyElementNotPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'click tambah email'
                        WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

                        'input email reminder'
                        WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(indexexcel - 1)])

                        break
                    } else if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
                        arrayEmailReminder[(indexexcel - 1)])) {
                        break
                    }
                }
            }
            
            'check if mandatory complete dan button simpan clickable'
            if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Edit/button_Simpan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
                'click button simpan'
                WebUI.click(findTestObject('Tenant/Edit/button_Simpan'))

                'verify pop up message berhasil'
                if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('Tenant/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					
					'call function after edit'
					verifyAfterAddEdit()
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Edit/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
            }
        }
        
        'check if store db'
        if ((GlobalVariable.checkStoreDB == 'Yes') && (isMandatoryComplete == 0) ) {
            'call test case tenant store db'
            WebUI.callTestCase(findTestCase('Tenant/TenantStoreDB'), [('excelPathTenant') : 'Tenant/Tenant'], FailureHandling.STOP_ON_FAILURE)
        }
    }
}

def checkPaging(Connection conneSign) {
    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/input_NamaTenant'), 'nama tenant')

    'input status'
    WebUI.setText(findTestObject('Tenant/input_Status'), 'Active')

    'click enter untuk input select ddl'
    WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('Tenant/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_NamaTenant'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_Status'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click button cari'
    WebUI.click(findTestObject('Tenant/button_Cari'))

    'get data tenant'
    int resultTotalData = CustomKeywords.'connection.Tenant.getTotalTenant'(conneSign)

    'get text total data dari ui'
    Total = WebUI.getText(findTestObject('Tenant/label_TotalData')).split(' ')

    'verify total data tenant'
    checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Integer.parseInt(Total[0]), FailureHandling.CONTINUE_ON_FAILURE))

    'click page 2'
    WebUI.click(findTestObject('Tenant/button_Page2'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 1'
    WebUI.click(findTestObject('Tenant/button_Page1'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'get total page'
    def variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-tenant > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object next Page'
    def modifyObjectNextPage = WebUI.modifyObjectProperty(findTestObject('Tenant/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variable.size() - 1) + ']', true)

    'click next page'
    WebUI.click(modifyObjectNextPage)

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('Tenant/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'modify object last Page'
    def modifyObjectLastPage = WebUI.modifyObjectProperty(findTestObject('Tenant/modifyObject'), 'xpath', 'equals', '//*[@class = "pager"]/li[' + (variable.size() - 2).toString() + ']', true)

    'click max page'
    WebUI.click(findTestObject('Tenant/button_MaxPage'))

    'verify paging di page terakhir'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectLastPage, 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click min page'
    WebUI.click(findTestObject('Tenant/button_MinPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def searchTenant() {
    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            9))

    'input status'
    WebUI.setText(findTestObject('Tenant/input_Status'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            10))

    'click enter untuk input select ddl'
    WebUI.sendKeys(findTestObject('Tenant/input_Status'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('Tenant/button_Cari'))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') +
			GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

		GlobalVariable.FlagFailed = 1
	}
}

def verifyAfterAddEdit() {
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm,
			12))
	
	'click button cari'
	WebUI.click(findTestObject('Tenant/button_Cari'))
	
	'verify nama tenant after add or edit'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_NamaTenant')), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 12), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Tenant after Add or Edit')
	
	'verify Kode tenant after add or edit'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_KodeTenant')), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 13), false, FailureHandling.CONTINUE_ON_FAILURE), ' Kode Tenant after Add or Edit')
}

def inputCancel() {
	
	'click button Baru'
	WebUI.click(findTestObject('Tenant/Button_Baru'))

	'get total form'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))
	
	'input nama tenant'
	WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm,
			12))

	'input tenant code'
	WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm,
			13))

	'input label ref number'
	WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(
			GlobalVariable.NumofColm, 14))

	'click button generate api key'
	WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))
	
	'click button batas saldo 1'
	WebUI.click(findTestObject('Tenant/button_BatasSaldo1'))
	
	'input batas saldo 1'
	WebUI.setText(findTestObject('Tenant/input_BatasSaldo1'), '10')
	
	'click button Email Reminder Saldo'
	WebUI.click(findTestObject('Tenant/button_EmailReminderSaldo'))
	
	'input Email Reminder Saldo'
	WebUI.setText(findTestObject('Tenant/input_EmailReminderSaldo'), 'ABCDE@GMAIL.COM')

	'input email user admin'
	WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(excelPathTenant).getValue(
			GlobalVariable.NumofColm, 22))

	'input kode akses user admin'
	WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(excelPathTenant).getValue(
			GlobalVariable.NumofColm, 23))
	
	'click button batal'
	WebUI.click(findTestObject('Object Repository/Tenant/TenantBaru/button_Batal'))

	if(WebUI.verifyElementPresent(findTestObject('Tenant/button_BatasSaldo1'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		checkVerifyEqualOrMatch(false, 'FAILED TO CANCEL')
	}
	
	'click button Baru'
	WebUI.click(findTestObject('Tenant/Button_Baru'))

	'delay untuk tunggu button batas saldo'
	WebUI.delay(5)
	
	'check field nama tenant'	
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_NamaTenant'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Nama Tenant Tidak Kosong')
	
	'check field kode tenant'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_TenantCode'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Kode Tenant Tidak Kosong')
	
	'check field label ref number tenant'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Label Ref Number Tenant Tidak Kosong')
	
	'check field API Key'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field API Key Tidak Kosong')
	
	'click button batas saldo 1'
	WebUI.click(findTestObject('Tenant/button_BatasSaldo1'))
	
	'check field Batas Saldo'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_BatasSaldo1'), 'value', FailureHandling.OPTIONAL), '0', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Batas Saldo Tidak Kosong')
	
	'click button Email Reminder Saldo'
	WebUI.click(findTestObject('Tenant/button_EmailReminderSaldo'))
	
	'check field EmailReminderSaldo'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_EmailReminderSaldo'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field EmailReminderSaldo Tidak Kosong')
	
	'check field email user admin'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field email user admin Tidak Kosong')

	'check kode akses user admin'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), 'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field kode akses user admin Tidak Kosong')
	
	'click button batal'
	WebUI.click(findTestObject('Object Repository/Tenant/TenantBaru/button_Batal'))
}