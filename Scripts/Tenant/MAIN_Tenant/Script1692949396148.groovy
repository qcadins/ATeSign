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

int firstRun = 0

'looping tenant'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathTenant, ('Email') : 'Email Login'
                    , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
                'call function check paging'
                checkPaging(conneSign)

                'call function cancel'
                inputCancel()
            }
            
            firstRun = 1
        }
        
        'click menu tenant'
        WebUI.click(findTestObject('Tenant/menu_Tenant'))

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 5))

        if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'check if action new/services'
        if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('New')) {
            'click button Baru'
            WebUI.click(findTestObject('Tenant/Button_Baru'))

            'get total form'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

            'input nama tenant'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$NamaTenant')))

            'input tenant code'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$KodeTenant')))

            'input label ref number'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, rowExcel('$LabelRefNumber')))

            'check if ingin menginput api secara manual/generate'
            if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Auto Generate API Key')) == 'No') {
                'input API Key'
                WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                        21))
            } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Auto Generate API Key')) == 
            'Yes') {
                'click button generate api key'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

                'get api key'
                APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

                'write to excel api key'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 20, GlobalVariable.NumofColm - 
                    1, APIKEY)
            }
            
            'get array services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Services')).split(
                ';', -1)

            'get array batas saldo dari excel'
            arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Batas Saldo Services')).split(
                ';', -1)

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
            arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email')).split(
                ';', -1)

            'looping untuk input email reminder'
            for (index = 1; index <= arrayEmailReminder.size(); index++) {
                'modify object input email'
                modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                    (25 + index)) + ']/div/input', true)

                'click tambah email'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

                'input email reminder'
                WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(index - 1)])
            }
            
            'input email user admin'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Email User Admin')))

            'input kode akses user admin'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, rowExcel('$Kode Akses User Admin')))

            'check if mandatory complete dan button simpan clickable'
            if (WebUI.verifyElementHasAttribute(findTestObject('Tenant/TenantBaru/button_Simpan'), 'disabled', GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
            	'click button Batal'
            	WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))
            	
            	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
            	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            			(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
            	
            	GlobalVariable.FlagFailed = 1
            } else {
				'click button simpan'
				WebUI.click(findTestObject('Tenant/TenantBaru/button_Simpan'))

				if (WebUI.verifyElementPresent(findTestObject('Tenant/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					'ambil text dari errorlog'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) +
						';') + WebUI.getText(findTestObject('Tenant/errorLog')))

					'click button Batal'
					WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

					GlobalVariable.FlagFailed = 1
				} else if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					'click button OK'
					WebUI.click(findTestObject('Tenant/button_OK'))

					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
						1, GlobalVariable.StatusSuccess)

					'call function after add'
					verifyAfterAddEdit()
				}
            }
            
            if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)

                GlobalVariable.FlagFailed = 1
            }
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Service')) {
            'call function search'
            searchTenant()

            'click button services balance'
            WebUI.click(findTestObject('Tenant/button_ServiceBalance'))

            'get array Services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('ServicesCheck')).split(
                ';', -1)

            'get array Vendor dari excel'
            arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('VendorCheck')).split(
                ';', -1)

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
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('ServicesUncheck')).split(
                ';', -1)

            'get array Vendor dari excel'
            arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('VendorUncheck')).split(
                ';', -1)

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
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Services/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)
            }
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Edit')) {
            'call function search'
            searchTenant()

            'click button edit'
            WebUI.click(findTestObject('Tenant/button_Edit'))

            'input nama tenant'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$NamaTenant')))

            'input tenant code'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                    rowExcel('$KodeTenant')), FailureHandling.OPTIONAL)

            'input label ref number'
            WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(
                    GlobalVariable.NumofColm, rowExcel('$LabelRefNumber')))

            'check if ingin menginput api secara manual/generate'
            if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Auto Generate API Key')) == 'No') {
                'input API Key'
                WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                        rowExcel('API Key')))
            } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Auto Generate API Key')) == 
            'Yes') {
                'click button generate api key'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

                'get api key'
                APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

                'write to excel api key'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('API Key') - 
                    1, GlobalVariable.NumofColm - 1, APIKEY)
            }
            
            'get total form'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

            'get array services dari excel'
            arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Services')).split(
                ';', -1)

            'get array batas saldo dari excel'
            arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Batas Saldo Services')).split(
                ';', -1)

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
            arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email')).split(
                ';', -1)

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
                for (index = 26; index <= variable.size(); index++) {
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
            if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Edit/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)
            } else if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Edit/button_Simpan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
                'click button simpan'
                WebUI.click(findTestObject('Tenant/Edit/button_Simpan'))

                if (WebUI.verifyElementPresent(findTestObject('Tenant/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'ambil text dari errorlog'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                        ';') + WebUI.getText(findTestObject('Tenant/errorLog')))

                    'click button Batal'
                    WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

                    GlobalVariable.FlagFailed = 1
                } else if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('Tenant/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    'call function after add'
                    verifyAfterAddEdit()
                }
            }
        }
        
        'check if store db'
        if (((GlobalVariable.checkStoreDB == 'Yes') && (isMandatoryComplete == 0)) && (GlobalVariable.FlagFailed == 0)) {
            'call test case tenant store db'
            WebUI.callTestCase(findTestCase('Tenant/TenantStoreDB'), [('excelPathTenant') : 'Tenant/Tenant'], FailureHandling.STOP_ON_FAILURE)
        }
    }
}

def checkPaging(Connection conneSign) {
    'click menu tenant'
    WebUI.click(findTestObject('Tenant/menu_Tenant'))

    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/input_NamaTenant'), 'nama tenant')

    'input status'
	inputDDLExact('Tenant/input_Status', 'Active')
	
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

    'click next page'
    WebUI.click(findTestObject('Tenant/button_NextPage'))

    'verify paging di page 2'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click prev page'
    WebUI.click(findTestObject('Tenant/button_PrevPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click max page'
    WebUI.click(findTestObject('Tenant/button_MaxPage'))

    'get total data'
    lastPage = (Double.parseDouble(WebUI.getText(findTestObject('Tenant/label_TotalData')).split(' ', -1)[0]) / 10)

    'jika hasil perhitungan last page memiliki desimal'
    if (lastPage.toString().contains('.0')) {
        'tidak ada round up'
        additionalRoundUp = 0
    } else {
        'round up dengan tambahan 0.5'
        additionalRoundUp = 0.5
    }
    
    'verify paging di page terakhir'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
            Math.round(lastPage + additionalRoundUp).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'click min page'
    WebUI.click(findTestObject('Tenant/button_MinPage'))

    'verify paging di page 1'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
            'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def searchTenant() {
    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama Tenants')))

    'input status'
	inputDDLExact('Tenant/input_Status', findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status')))

    'click button cari'
    WebUI.click(findTestObject('Tenant/button_Cari'))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def verifyAfterAddEdit() {
    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$NamaTenant')))

    'click button cari'
    WebUI.click(findTestObject('Tenant/button_Cari'))

    'verify nama tenant after add or edit'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_NamaTenant')), 
            findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('$NamaTenant')), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Nama Tenant after Add or Edit')

    'verify Kode tenant after add or edit'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/Tenant/label_KodeTenant')), 
            findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('$KodeTenant')), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Kode Tenant after Add or Edit')
}

def inputCancel() {
    'click button Baru'
    WebUI.click(findTestObject('Tenant/Button_Baru'))

    'get total form'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$NamaTenant')))

    'input tenant code'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$KodeTenant')))

    'input label ref number'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$LabelRefNumber')))

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
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Email User Admin')))

    'input kode akses user admin'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Kode Akses User Admin')))

    'click button batal'
    WebUI.click(findTestObject('Object Repository/Tenant/TenantBaru/button_Batal'))

    if (WebUI.verifyElementPresent(findTestObject('Tenant/button_BatasSaldo1'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        checkVerifyEqualOrMatch(false, 'FAILED TO CANCEL')
    }
    
    'click button Baru'
    WebUI.click(findTestObject('Tenant/Button_Baru'))

    'delay untuk tunggu button batas saldo'
    WebUI.delay(5)

    'check field nama tenant'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_NamaTenant'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Nama Tenant Tidak Kosong')

    'check field kode tenant'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_TenantCode'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Kode Tenant Tidak Kosong')

    'check field label ref number tenant'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Label Ref Number Tenant Tidak Kosong')

    'check field API Key'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field API Key Tidak Kosong')

    'click button batas saldo 1'
    WebUI.click(findTestObject('Tenant/button_BatasSaldo1'))

    'check field Batas Saldo'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_BatasSaldo1'), 'value', FailureHandling.OPTIONAL), 
            '0', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field Batas Saldo Tidak Kosong')

    'click button Email Reminder Saldo'
    WebUI.click(findTestObject('Tenant/button_EmailReminderSaldo'))

    'check field EmailReminderSaldo'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/input_EmailReminderSaldo'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field EmailReminderSaldo Tidak Kosong')

    'check field email user admin'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field email user admin Tidak Kosong')

    'check kode akses user admin'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), 'Field kode akses user admin Tidak Kosong')

    'click button batal'
    WebUI.click(findTestObject('Object Repository/Tenant/TenantBaru/button_Batal'))
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
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}