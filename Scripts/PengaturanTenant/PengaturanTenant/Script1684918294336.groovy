import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Call test Case untuk login sebagai admin wom admin client'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathFEPengaturanTenant, ('sheet') : 'PengaturanTenant'], 
    FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/PengaturanTenant/menu_PengaturanTenant'))

WebUI.verifyElementPresent(findTestObject('Object Repository/PengaturanTenant/label_PengaturanTenant'), GlobalVariable.TimeOut)

WebUI.setText(findTestObject('Object Repository/PengaturanTenant/input_LabelRefNumber'), findTestData(excelPathFEPengaturanTenant).getValue(
        GlobalVariable.NumofColm, 8))

WebUI.setText(findTestObject('Object Repository/PengaturanTenant/input_URLUpload'), findTestData(excelPathFEPengaturanTenant).getValue(
        GlobalVariable.NumofColm, 9))

WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Copy'))

checkerrorLog()

tipeSaldo = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 10).split(';', -1)

for (int i = 0; i < tipeSaldo.size(); i++) {
    'modify object text document template name di beranda'
    modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/label_TipeBatasSaldo'), 
        'for', 'equals', ('' + (tipeSaldo[i])) + '', true)

    if (WebUI.getText(modifyObjectTipeBatasSaldo) == null) {
		CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PengaturanTenant', GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2) + ';' + GlobalVariable.ReasonFailedNoneUI
	 	+ 'mengenai tipe saldo ' + tipeSaldo[i])

		continue
    } else if (WebUI.getText(modifyObjectTipeBatasSaldo) == tipeSaldo[i]) {
            'modify object text document template name di beranda'
			modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/input_BatasSaldo'), 
				'id', 'equals', ('' + (tipeSaldo[i])) + '', true)

        WebUI.setText(modifyObjectTipeBatasSaldo,findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 11))
    }
}

int countEmailInput = 
ArrayList<String> resultDb = CustomKeywords.'connection.DataVerif.getPengaturanTenant'(conneSign, findTestData('Login/Login').getValue(2, 2).toUpperCase())

println(resultDb) //WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Batal'))
//WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Simpan'))

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Kode OTP salah'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + lblpopup)

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('KotakMasuk/Sign/errorLog_OK'))
    }
    
    return false
}

def checkerrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (!(errormessage.contains('URL Upload tersalin')) && !(errormessage.contains('feedback'))) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + errormessage)

            return true
        }
    }
    
    return false
}

