import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

int firstRun = 0

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPriorityPsre).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathPriorityPsre
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            firstRun = 1
        }
        
        'get tenant code dari excel per case'
        GlobalVariable.Tenant = findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'get Psre code dari excel per case'
        GlobalVariable.Psre = findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'scroll to menu psre prior'
        WebUI.scrollToElement(findTestObject('PengaturanPSrE/PSRe Priority/menu_PsrePriority'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)

        'click menu priority PSrE'
        WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/menu_PsrePriority'))

        if (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'delay untuk menu Psre loading'
        WebUI.delay(5)

        'click menu priority PSrE'
        WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/menu_PsrePriority'))

        'declare array list'
        ArrayList resultDB = [], resultUI = [], seqPsreRole = []

        resultDB = CustomKeywords.'connection.PengaturanPSrE.getPsrePriority'(conneSign)

        'call function get FE priority'
        getFEPriority(resultUI)

        'verify default vendor psre db = ui before edit'
        checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB.toString(), resultUI.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' urutan psre tidak sesuai dengan default vendor DB before edit')

        'get urutan seq psre dari excel'
        seqPsreRole = findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Urutan Psre')).toUpperCase().split(
            '\\n', -1)

        'looping seq Psre'
        for (seq = 1; seq <= variable.size(); seq++) {
            'modify label tipe tanda tangan di kotak'
            modifyObject = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'), 'xpath', 
                'equals', ('//*[@id="cdk-drop-list-0"]/div[' + seq) + ']/div', true)

            'get text pisah nomor dan psre'
            vendor = WebUI.getText(modifyObject).split(' ', -1)

            index = (seqPsreRole.indexOf((vendor[1]).toUpperCase()) + 1)

            if (seq != index) {
                'modify label tipe tanda tangan di kotak'
                modifyObjectNew = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'), 
                    'xpath', 'equals', ('//*[@id="cdk-drop-list-0"]/div[' + index) + ']/div', true)

                'pindahin ke urutan sesuai excel'
                WebUI.dragAndDropToObject(modifyObject, modifyObjectNew)

                'untuk proses pemindahan'
                WebUI.delay(2)

                seq--
            }
        }
        
        'click button simpan'
        WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/button_Simpan'))

        'delay untuk loading simpan'
        WebUI.delay(3)

        'check if muncul button konfirmasi'
        if (WebUI.verifyElementPresent(findTestObject('PengaturanPSrE/PSRe Priority/button_Konfirmasi'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'click button button konfirmasi'
            WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/button_Konfirmasi'))
        }
        
        'check if muncul popup'
        if (WebUI.verifyElementPresent(findTestObject('PengaturanPSrE/label_PopUP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            if (WebUI.getText(findTestObject('PengaturanPSrE/label_PopUP')).equalsIgnoreCase('Success')) {
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    resultDB = CustomKeywords.'connection.PengaturanPSrE.getPsrePriority'(conneSign)

                    'clear arraylist'
                    resultUI.clear()

                    'call function get FE priority'
                    getFEPriority(resultUI)

                    'verify default vendor psre db = ui before edit'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB.toString(), resultUI.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' urutan psre tidak sesuai dengan default vendor DB after edit')
                }
                
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'Write to excel status failed'
                GlobalVariable.FlagFailed = 1

                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + ' GAGAL PENGATURAN PRIORITAS PSRE')
            }
            
            'click button OK'
            WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/button_OK'))
        }
    }
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def getFEPriority(ArrayList resultUI) {
    'count PSrE'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('#cdk-drop-list-0 div div'))

    'looping jumlah psre pada ui untuk get urutan psre'
    for (index = 1; index <= variable.size(); index++) {
        'modify object psre box'
        modifyObjectBox = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'), 'xpath', 
            'equals', ('//*[@id="cdk-drop-list-0"]/div[' + index) + ']/div', true)

        'get text pisah nomor dan psre'
        vendor = WebUI.getText(modifyObjectBox).split(' ', -1)

        'add psre kedalam arraylist'
        resultUI.add(vendor[1])
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

