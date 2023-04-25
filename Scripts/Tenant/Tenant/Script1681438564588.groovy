import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathTenant).getColumnNumbers()

'call test case login admin esign'
WebUI.callTestCase(findTestCase('Login/Login_AdminEsign'), [:], FailureHandling.STOP_ON_FAILURE)

'click menu tenant'
WebUI.click(findTestObject('Tenant/menu_Tenant'))

'call function check paging'
checkPaging(conneSign)

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 4))

'looping tenant'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if action new/services'
        if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New')) {
            'click button Baru'
            WebUI.click(findTestObject('Tenant/Button_Baru'))

            'get total form'
            variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))

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
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumofColm - 
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
				
				'break if index udah lebih dari 20 HARDCODE karena tidak bisa di track objectnya'
				if (index > 20) {
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
                    (20 + index).toString()) + ']/div/input', true)

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
            if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/TenantBaru/button_Simpan'), 
                'disabled', GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
                'click button simpan'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_Simpan'))

                'verify pop up message berhasil'
                if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('Tenant/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
            }
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Service')) {
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
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Services/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
            }
        } else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit')) {
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

            'click button edit'
            WebUI.click(findTestObject('Tenant/button_Edit'))

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
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 15, GlobalVariable.NumofColm - 
                    1, APIKEY)
            }
            
            'get total form'
            variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-add-tenant > div.row.match-height > div > div > div > div > form div'))
			
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

                'break if index udah lebih dari 20 HARDCODE karena tidak bisa di track objectnya'
                if (index > 20) {
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
            for (index = 21; index <= variable.size(); index++) {
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
                                'click tambah email'
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
                for (index = 21; index <= variable.size(); index++) {
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
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else if (isMandatoryComplete > 0) {
                'click button Batal'
                WebUI.click(findTestObject('Tenant/Edit/button_Batal'))

                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedMandatory)
            }
        }
        
        'check if store db'
        if (GlobalVariable.checkStoreDB == 'Yes') {
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
    int resultTotalData = CustomKeywords.'connection.dataVerif.getTotalTenant'(conneSign)

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
	def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-tenant > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	'modify object next Page'
	def modifyObjectNextPage = WebUI.modifyObjectProperty(findTestObject('Tenant/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		(variable.size() - 1).toString()) + ']', true)
	
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
    def modifyObjectLastPage = WebUI.modifyObjectProperty(findTestObject('Tenant/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        (variable.size() - 2).toString()) + ']', true)

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
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

