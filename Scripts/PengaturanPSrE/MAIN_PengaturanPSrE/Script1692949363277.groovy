import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

int firstRun = 0

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPath).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'check if email login case selanjutnya masih sama dengan sebelumnya'
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
			findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')) || firstRun == 0) {
			'call test case login per case'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPath, ('Email') : 'Email Login', ('Password') : 'Password Login'
				, ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)
			
			firstRun = 1
		}
		
		'click menu pengaturan PSrE'
		WebUI.click(findTestObject('PengaturanPSrE/menu_PengaturanPSrE'))
		
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}
		
        if (GlobalVariable.NumofColm == 2) {
            'call function check paging'
            verifyPaging()

            'click button cari'
            WebUI.click(findTestObject('PengaturanPSrE/button_Cari'))

            'get total PSrE dari DB'
            String resultTotalPSrE = CustomKeywords.'connection.PengaturanPSrE.getTotalPSrE'(conneSign)

            'verify total PSrE'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/label_TotalPSrE')), resultTotalPSrE + 
                    ' total', false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Total data PSrE')

            'call function verif and input'
            verifinputEdit(conneSign)

            'get data Vendor Payment Type'
            ArrayList<String> resultDDLVendorPayment = CustomKeywords.'connection.PengaturanPSrE.getDDLVendorPaymentType'(
                conneSign)

            'check ddl Vendor Payment Type'
            checkDDL(findTestObject('PengaturanPSrE/input_VendorPaymentType'), resultDDLVendorPayment, ' pada DDL Vendor Payment Type')

            'click button cancel'
            WebUI.click(findTestObject('PengaturanPSrE/button_Cancel'))

            'verif after cancel'
            if (WebUI.verifyElementPresent(findTestObject('PengaturanPSrE/input_VendorPaymentType'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Gagal Cancel')

                GlobalVariable.FlagFailed = 1
            }
        }
        
        'call function verif and input'
        verifinputEdit(conneSign)

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Is Mandatory Complete')))

        if (isMandatoryComplete > 0) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedMandatory)

            GlobalVariable.FlagFailed = 1

            'click button cancel'
            WebUI.click(findTestObject('PengaturanPSrE/button_Cancel'))

            continue
        }
        
        'click button simpan'
        WebUI.click(findTestObject('PengaturanPSrE/button_Simpan'))

        if (WebUI.getText(findTestObject('PengaturanPSrE/label_PopUP')).equalsIgnoreCase('Success')) {
            'click button OK'
            WebUI.click(findTestObject('PengaturanPSrE/button_OK'))

            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            'call function after edit verify'
            verifAfterEdit()

            'check if store DB Yes'
            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call function pengaturan PSrE store DB'
                pengaturanPSrEStoreDB(conneSign)
            }
        } else {
            'write to excel Failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusFailed)
        }
    }
}

'close browser'
WebUI.closeBrowser()

def verifAfterEdit() {
    'input nama vendor'
    WebUI.setText(findTestObject('PengaturanPSrE/input_NamaVendor'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama Vendor - Setting')))

    'input kode vendor'
    WebUI.setText(findTestObject('PengaturanPSrE/input_KodeVendor'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Kode Vendor - Setting')))

    'input status'
    WebUI.setText(findTestObject('PengaturanPSrE/input_Status'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Active - Setting')))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_Status'), Keys.chord(Keys.ENTER))

    'input status operational'
    WebUI.setText(findTestObject('PengaturanPSrE/input_StatusOperational'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Operating - Setting')))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_StatusOperational'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('PengaturanPSrE/button_Cari'))

    'verify nama vendor after edit'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/label_NamaVendor')).toUpperCase(), 
            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Nama Vendor - Setting')).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' After Edit Nama Vendor Berbeda')

    'verify kode vendor after edit'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/label_KodeVendor')).toUpperCase(), 
            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Kode Vendor - Setting')).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' After Edit Kode Vendor Berbeda')

    'verify status vendor after edit'
    checkStatus(findTestObject('PengaturanPSrE/label_Status'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Active - Setting')), ' After Edit Status Vendor Berbeda')

    'verify status operational vendor after edit'
    checkStatus(findTestObject('PengaturanPSrE/label_StatusOperational'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Operating - Setting')), ' After Edit Status Operational Vendor Berbeda')

    'verify tipe pembayaran vendor after edit'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/label_TipePembayaranTTD')).toUpperCase(), 
            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Pembayaran TTD - Setting')).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' After Edit Tipe Pembayaran Vendor Berbeda')
}

def checkStatus(TestObject object, String statsuExcel, String reason) {
    String status

    if (statsuExcel.equalsIgnoreCase('Aktif')) {
        status = 'ft-check'
    } else {
        status = 'ft-x'
    }
    
    'verify tipe pembayaran vendor after edit'
    checkVerifyEqualOrMatch(WebUI.getAttribute(object, 'class', FailureHandling.OPTIONAL).contains(status), reason)
}

def verifinputEdit(Connection conneSign) {
    'search PSrE'
    inputSearchPSrE()

    'click button cari'
    WebUI.click(findTestObject('PengaturanPSrE/button_Cari'))

    'click button edit'
    WebUI.click(findTestObject('PengaturanPSrE/button_Edit'))

    'get total data PSrE dari DB'
    ArrayList<String> resultDataPSrE = CustomKeywords.'connection.PengaturanPSrE.getDataVendor'(conneSign, findTestData(
            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code')))

    arrayIndex = 0

    'verify vendor Code'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PengaturanPSrE/input_Kodevendor'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), resultDataPSrE[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada Kode vendor ')

    'verify vendor name'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PengaturanPSrE/input_Namavendor'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), resultDataPSrE[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada Nama vendor ')

    'click ddl status'
    WebUI.click(findTestObject('PengaturanPSrE/input_Status'))

    'verify field ke reset'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/selected_DDL')), resultDataPSrE[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Status')

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_Status'), Keys.chord(Keys.ENTER))

    'click ddl status Operational'
    WebUI.click(findTestObject('PengaturanPSrE/input_StatusOperational'))

    'verify field ke reset'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/selected_DDL')), resultDataPSrE[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Operational')

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_StatusOperational'), Keys.chord(Keys.ENTER))

    'click ddl vendor payment type'
    WebUI.click(findTestObject('PengaturanPSrE/input_VendorPaymentType'))

    'verify field ke reset'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/selected_DDL')), resultDataPSrE[
            arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Vendor Payment Type')

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_VendorPaymentType'), Keys.chord(Keys.ENTER))

    'input nama vendor'
    WebUI.setText(findTestObject('PengaturanPSrE/input_NamaVendor'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama Vendor - Setting')))

    'input status'
    WebUI.setText(findTestObject('PengaturanPSrE/input_Status'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Active - Setting')))

    'call function pilih DDL'
    pilihDDL(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Status Active - Setting')))

    'input status operational'
    WebUI.setText(findTestObject('PengaturanPSrE/input_StatusOperational'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Status Operating - Setting')))

    'call function pilih DDL'
    pilihDDL(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Status Operating - Setting')))

    'input Vendor Payment Type'
    WebUI.setText(findTestObject('PengaturanPSrE/input_VendorPaymentType'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tipe Pembayaran TTD - Setting')))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_VendorPaymentType'), Keys.chord(Keys.ENTER))
}

def inputSearchPSrE() {
    'input nama vendor'
    WebUI.setText(findTestObject('PengaturanPSrE/input_NamaVendor'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('Vendor Name')))

    'input kode vendor'
    WebUI.setText(findTestObject('PengaturanPSrE/input_KodeVendor'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('Vendor Code')))

    'input status all untuk reset'
    WebUI.setText(findTestObject('PengaturanPSrE/input_Status'), 'All')

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_Status'), Keys.chord(Keys.ENTER))

    'input status sesuai excel'
    WebUI.setText(findTestObject('PengaturanPSrE/input_Status'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('Status Active')))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_Status'), Keys.chord(Keys.ENTER))

    'input status operational all untuk reset'
    WebUI.setText(findTestObject('PengaturanPSrE/input_StatusOperational'), 'All')

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_StatusOperational'), Keys.chord(Keys.ENTER))

    'input status operational sesuai excel'
    WebUI.setText(findTestObject('PengaturanPSrE/input_StatusOperational'), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
            rowExcel('Status Operating')))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_StatusOperational'), Keys.chord(Keys.ENTER))
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def verifyPaging() {
    'call function input search form'
    inputSearchPSrE()

    'click button set ulang'
    WebUI.click(findTestObject('PengaturanPSrE/button_SetUlang'))

    'check if field nama vendor sudah kosong'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/input_NamaVendor')), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'check if field kode vendor sudah kosong'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/input_KodeVendor')), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status'
    WebUI.click(findTestObject('PengaturanPSrE/input_Status'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status operational'
    WebUI.click(findTestObject('PengaturanPSrE/input_StatusOperational'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('PengaturanPSrE/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'Input enter'
    WebUI.sendKeys(findTestObject('PengaturanPSrE/input_StatusOperational'), Keys.chord(Keys.ENTER))
}

def checkDDL(TestObject objectDDL, ArrayList<String> listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList<String> list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('PengaturanPSrE/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/modifyObject'), 'xpath', 'equals', ((('//*[@id=\'' + 
            id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    println(listDB)

    println(list)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def pilihDDL(String testData) {
    def modifyObjectDDL

    if (testData.equalsIgnoreCase('Aktif')) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/modifyObject'), 'xpath', 'equals', '//*[contains(@id, "-0")]', 
            true)
    } else {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/modifyObject'), 'xpath', 'equals', '//*[contains(@id, "-1")]', 
            true)
    }
    
    'Input enter'
    WebUI.click(modifyObjectDDL)
}

def pengaturanPSrEStoreDB(Connection conneSign) {
    'get total data PSrE dari DB'
    ArrayList<String> resultDataPSrE = CustomKeywords.'connection.PengaturanPSrE.getDataVendor'(conneSign, findTestData(
            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code')))

    arrayIndex = 0

    'verify kode vendor'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Kode Vendor - Setting')).toUpperCase(), 
            (resultDataPSrE[arrayIndex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' kode vendor store DB')

    'verify nama vendor'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Nama Vendor - Setting')).toUpperCase(), 
            (resultDataPSrE[arrayIndex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' nama vendor store DB')

    'verify status vendor'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Status Active - Setting')).toUpperCase(), 
            (resultDataPSrE[arrayIndex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Status vendor store DB')

    'verify status operational vendor'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Status Operating - Setting')).toUpperCase(), 
            (resultDataPSrE[arrayIndex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Status operational vendor store DB')

    'verify tipe pembayaran TTD vendor'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Pembayaran TTD - Setting')).toUpperCase(), 
            (resultDataPSrE[arrayIndex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' tipe pembayaran TTD vendor store DB')
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

