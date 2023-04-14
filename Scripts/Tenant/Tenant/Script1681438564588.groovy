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

'click menu tenant'
WebUI.click(findTestObject('Tenant/menu_Tenant'))

'call function check paging'
checkPaging(conneSign)

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 4))

'check if action new/services'
if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New')) {
    'click button Baru'
    WebUI.click(findTestObject('Tenant/Button_Baru'))

    'input nama tenant'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_NamaTenant'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            12))

    'input tenant code'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            13))

    'input label ref number'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            14))

    'check if ingin menginput api secara manual/generate'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15).length() > 0) {
        'input API Key'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                15))
    } else {
        'click button generate api key'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

        'get api key'
        APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

        'write to excel api key'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 14, GlobalVariable.NumofColm - 
            1, APIKEY)
    }
    
    'check if ingin input batas otp'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 17).length() > 0) {
        'click button OTP'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOTP'))

        'input Batas OTP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OTP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                17))
    }
    
    'check if ingin input batas verification'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).length() > 0) {
        'click button verification'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddVerification'))

        'input Batas verification'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Verification'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                18))
    }
    
    'check if ingin input batas Dokumen'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).length() > 0) {
        'click button dokumen'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddDokumen'))

        'input Batas dokumen'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Dokumen'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                19))
    }
    
    'check if ingin input batas TTD'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 20).length() > 0) {
        'click button TTD'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddSign'))

        'input Batas TTD'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_TTD'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                20))
    }
    
    'check if ingin input batas Verifikasi Wajah'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).length() > 0) {
        'click button Verifikasi Wajah'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddFaceVerify'))

        'input Batas Verifikasi Wajah'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_VerifikasiWajah'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 21))
    }
    
    'check if ingin input batas DukcapilBiometrik'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 22).length() > 0) {
        'click button DukcapilBiometrik'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddDukcapilBiometrik'))

        'input Batas DukcapilBiometrik'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_DukcapilBiometrik'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 22))
    }
    
    'check if ingin input batas OCR KTP'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 23).length() > 0) {
        'click button OCR KTP'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRKTP'))

        'input Batas OCR KTP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRKTP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                23))
    }
    
    'check if ingin input batas OCR NPWP'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 24).length() > 0) {
        'click button OCR NPWP'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRNPWP'))

        'input Batas OCR NPWP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRNPWP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                24))
    }
    
    'check if ingin input batas OCR BPKB'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 25).length() > 0) {
        'click button OCR BPKB'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRBPKB'))

        'input Batas OCR BPKB'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRBPKB'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                25))
    }
    
    'check if ingin input batas phone active check'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 26).length() > 0) {
        'click button phone active check'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddPhoneactiveCheck'))

        'input Batas phone active check'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_PhoneactiveCheck'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 26))
    }
    
    'check if ingin input batas OCR KK'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 27).length() > 0) {
        'click button OCR KK'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRKK'))

        'input Batas OCR KK'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRKK'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                27))
    }
    
    'check if ingin input batas stamdutyPostpaid'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 28).length() > 0) {
        'click button stamdutyPostpaid'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddStampdutyPostPaid'))

        'input Batas stamdutyPostpaid'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_StampDutyPostPaid'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 28))
    }
    
    'check if ingin input batas smsNotif'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 29).length() > 0) {
        'click button smsNotif'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddSMSNotif'))

        'input Batas smsNotif'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_SMSNotif'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                29))
    }
    
    'check if ingin input batas stampduty'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 30).length() > 0) {
        'click button stampduty'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddStampduty'))

        'input Batas stampduty'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Materai'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                30))
    }
    
    'check if ingin input batas OCR STNK'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 31).length() > 0) {
        'click button OCR STNK'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRSTNK'))

        'input Batas OCR STNK'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRSTNK'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                31))
    }
    
    'check if ingin input batas LivenessFaceCompare'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 32).length() > 0) {
        'click button LivenessFaceCompare'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddLivenessFaceCompare'))

        'input Batas LivenessFaceCompare'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_LivenessFaceCompare'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 32))
    }
    
    'get array email reminder dari excel'
    arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

    'looping untuk input email reminder'
    for (index = 1; index <= arrayEmailReminder.size(); index++) {
        'modify object input email'
        modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
            (20 + index).toString()) + ']/div/input', true)

        'click tambah email'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

        'input email reminder'
        WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(index - 1)])
    }
    
    'input email user admin'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_EmailUserAdmin'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            35))

    'input kode akses user admin'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_KodeAksesUserAdmin'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            36))

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
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    } else if (isMandatoryComplete > 0) {
        'click button Batal'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_Batal'))

        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)
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
    arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 38).split(';', -1)

    'get array Vendor dari excel'
    arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 39).split(';', -1)

    'looping untuk input services check'
    for (index = 0; index < arrayServices.size(); index++) {
        'modify object checkbox'
        modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath', 'equals', 
            ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

        'check if check box is unchecked'
        if (WebUI.verifyElementNotChecked(modifyObjectCheckbox, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click checkbox'
            WebUI.click(modifyObjectCheckbox)
        }
    }
    
    'get array Services dari excel'
    arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 40).split(';', -1)

    'get array Vendor dari excel'
    arrayVendor = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 41).split(';', -1)

    'looping untuk input services uncheck'
    for (index = 0; index < arrayServices.size(); index++) {
        'modify object checkbox'
        modifyObjectCheckbox = WebUI.modifyObjectProperty(findTestObject('Tenant/Services/modifyObject'), 'xpath', 'equals', 
            ((('//*[@id="' + (arrayServices[index])) + '@') + (arrayVendor[index])) + '"]', true)

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
        if (WebUI.getAttribute(findTestObject('Tenant/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains('berhasil')) {
            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    } else if (isMandatoryComplete > 0) {
        'click button Batal'
        WebUI.click(findTestObject('Tenant/Services/button_Batal'))

        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)
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

    'click button services balance'
    WebUI.click(findTestObject('Tenant/button_Edit'))

    'input tenant code'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_TenantCode'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            13), FailureHandling.OPTIONAL)

    'input label ref number'
    WebUI.setText(findTestObject('Tenant/TenantBaru/input_LabelRefNumber'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
            14))

    'check if ingin menginput api secara manual/generate'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 15).length() > 0) {
        'input API Key'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_APIKEY'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                15))
    } else {
        'click button generate api key'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_GenerateAPIKEY'))

        'get api key'
        APIKEY = WebUI.getAttribute(findTestObject('Tenant/TenantBaru/input_APIKEY'), 'value', FailureHandling.CONTINUE_ON_FAILURE)

        'write to excel api key'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 14, GlobalVariable.NumofColm - 
            1, APIKEY)
    }
    
    'check if ingin input batas otp'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 17).length() > 0) {
		if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOTP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			
        'click button OTP'
        WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOTP'))
		}

        'input Batas OTP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OTP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                17))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOTP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OTP'
        WebUI.click(findTestObject('Tenant/Edit/HapusOTP'))
    }
    
    'check if ingin input batas verification'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusVerification'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button verification'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddVerification'))
        }
        
        'input Batas verification'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Verification'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                18))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusVerification'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus verification'
        WebUI.click(findTestObject('Tenant/Edit/HapusVerification'))
    }
    
    'check if ingin input batas Dokumen'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusDokumen'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button dokumen'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddDokumen'))
        }
        
        'input Batas dokumen'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Dokumen'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                19))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusDokumen'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus Dokumen'
        WebUI.click(findTestObject('Tenant/Edit/HapusDokumen'))
    }
    
    'check if ingin input batas TTD'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 20).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusSign'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button TTD'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddSign'))
        }
        
        'input Batas TTD'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_TTD'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                20))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusSign'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus Sign'
        WebUI.click(findTestObject('Tenant/Edit/HapusSign'))
    }
    
    'check if ingin input batas Verifikasi Wajah'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusFaceVerify'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button Verifikasi Wajah'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddFaceVerify'))
        }
        
        'input Batas Verifikasi Wajah'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_VerifikasiWajah'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 21))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusFaceVerify'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus FaceVerify'
        WebUI.click(findTestObject('Tenant/Edit/HapusFaceVerify'))
    }
    
    'check if ingin input batas DukcapilBiometrik'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 22).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusDukBio'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button DukcapilBiometrik'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddDukcapilBiometrik'))
        }
        
        'input Batas DukcapilBiometrik'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_DukcapilBiometrik'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 22))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusDukBio'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus DukBio'
        WebUI.click(findTestObject('Tenant/Edit/HapusDukBio'))
    }
    
    'check if ingin input batas OCR KTP'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 23).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOCRKTP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OCR KTP'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRKTP'))
        }
        
        'input Batas OCR KTP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRKTP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                23))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOCRKTP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OCRKTP'
        WebUI.click(findTestObject('Tenant/Edit/HapusOCRKTP'))
    }
    
    'check if ingin input batas OCR NPWP'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 24).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOCRNPWP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OCR NPWP'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRNPWP'))
        }
        
        'input Batas OCR NPWP'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRNPWP'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                24))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOCRNPWP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OCRNPWP'
        WebUI.click(findTestObject('Tenant/Edit/HapusOCRNPWP'))
    }
    
    'check if ingin input batas OCR BPKB'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 25).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOCRBPKB'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OCR BPKB'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRBPKB'))
        }
        
        'input Batas OCR BPKB'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRBPKB'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                25))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOCRBPKB'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OCRBPKB'
        WebUI.click(findTestObject('Tenant/Edit/HapusOCRBPKB'))
    }
    
    'check if ingin input batas phone active check'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 26).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusPhoneActiveCheck'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button phone active check'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddPhoneactiveCheck'))
        }
        
        'input Batas phone active check'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_PhoneactiveCheck'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 26))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusPhoneActiveCheck'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus phone active check'
        WebUI.click(findTestObject('Tenant/Edit/HapusPhoneActiveCheck'))
    }
    
    'check if ingin input batas OCR KK'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 27).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOCRKK'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OCR KK'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRKK'))
        }
        
        'input Batas OCR KK'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRKK'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                27))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOCRKK'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OCRKK'
        WebUI.click(findTestObject('Tenant/Edit/HapusOCRKK'))
    }
    
    'check if ingin input batas stamdutyPostpaid'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 28).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusStampdutyPostPaid'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'click button stamdutyPostpaid'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddStampdutyPostPaid'))
        }
        
        'input Batas stamdutyPostpaid'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_StampDutyPostPaid'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 28))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusStampdutyPostPaid'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'click button hapus StampdutyPostPaid'
        WebUI.click(findTestObject('Tenant/Edit/HapusStampdutyPostPaid'))
    }
    
    'check if ingin input batas smsNotif'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 29).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusSMS'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button smsNotif'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddSMSNotif'))
        }
        
        'input Batas smsNotif'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_SMSNotif'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                29))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusSMS'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus SMS'
        WebUI.click(findTestObject('Tenant/Edit/HapusSMS'))
    }
    
    'check if ingin input batas stampduty'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 30).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusStampduty'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button stampduty'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddStampduty'))
        }
        
        'input Batas stampduty'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_Materai'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                30))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusStampduty'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus Stampduty'
        WebUI.click(findTestObject('Tenant/Edit/HapusStampduty'))
    }
    
    'check if ingin input batas OCR STNK'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 31).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusOCRSTNK'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OCR STNK'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddOCRSTNK'))
        }
        
        'input Batas OCR STNK'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_OCRSTNK'), findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 
                31))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusOCRSTNK'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'click button hapus OCRSTNK'
        WebUI.click(findTestObject('Tenant/Edit/HapusOCRSTNK'))
    }
    
    'check if ingin input batas LivenessFaceCompare'
    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 32).length() > 0) {
        if (WebUI.verifyElementNotPresent(findTestObject('Tenant/Edit/HapusLivenessFaceCompare'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'click button LivenessFaceCompare'
            WebUI.click(findTestObject('Tenant/TenantBaru/button_AddLivenessFaceCompare'))
        }
        
        'input Batas LivenessFaceCompare'
        WebUI.setText(findTestObject('Tenant/TenantBaru/input_LivenessFaceCompare'), findTestData(excelPathTenant).getValue(
                GlobalVariable.NumofColm, 32))
    } else if (WebUI.verifyElementPresent(findTestObject('Tenant/Edit/HapusLivenessFaceCompare'), GlobalVariable.TimeOut, 
        FailureHandling.OPTIONAL)) {
        'click button hapus LivenessFaceCompare'
        WebUI.click(findTestObject('Tenant/Edit/HapusLivenessFaceCompare'))
    }
    
    'get array email reminder dari excel'
    arrayEmailReminder = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 34).split(';', -1)

    'looping untuk hapus email reminder yang tidak ada di excel'
    for (index = 1; index <= 100; index++) {
        'modify object input email'
        modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
            (20 + index).toString()) + ']/div/input', true)

        'modify object button hapus'
        modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
            (20 + index).toString()) + ']/div/button', true)

        if (WebUI.verifyElementPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'looping untuk input email reminder'
            for (indexexcel = 1; indexexcel <= arrayEmailReminder.size(); indexexcel++) {
                'check if email ui = excel'
                if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(arrayEmailReminder[
                    (indexexcel - 1)])) {
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
        for (index = 1; index <= 100; index++) {
            'modify object input email'
            modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Tenant/TenantBaru/modifyObject'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-add-tenant/div[2]/div/div/div/div/form/div[' + 
                (20 + index).toString()) + ']/div/input', true)

            if (WebUI.verifyElementNotPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'click tambah email'
                WebUI.click(findTestObject('Tenant/TenantBaru/button_TambahEmail'))

                'input email reminder'
                WebUI.setText(modifyObjectInputEmail, arrayEmailReminder[(indexexcel - 1)])

                break
            } else if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(arrayEmailReminder[
                (indexexcel - 1)])) {
                break
            }
        }
    }
    
    'check if mandatory complete dan button simpan clickable'
    if ((isMandatoryComplete == 0) && !(WebUI.verifyElementHasAttribute(findTestObject('Tenant/Edit/button_Simpan'), 'disabled', 
        GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
        'click button simpan'
        WebUI.click(findTestObject('Tenant/Edit/button_Simpan'))

        'verify pop up message berhasil'
        if (WebUI.verifyElementPresent(findTestObject('Tenant/popUpMsg'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'click button OK'
            WebUI.click(findTestObject('Tenant/button_OK'))

            'write to excel success'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Tenant', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    } else if (isMandatoryComplete > 0) {
        'click button Batal'
        WebUI.click(findTestObject('Tenant/Edit/button_Batal'))

        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedMandatory'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedMandatory)
    }
}

'check if store db'
if (GlobalVariable.checkStoreDB == 'Yes') {
    'call test case tenant store db'
    WebUI.callTestCase(findTestCase('Tenant/TenantStoreDB'), [('excelPathTenant') : 'Tenant/Tenant'], FailureHandling.STOP_ON_FAILURE)
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

    'get total page'
    def variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-tenant > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

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

