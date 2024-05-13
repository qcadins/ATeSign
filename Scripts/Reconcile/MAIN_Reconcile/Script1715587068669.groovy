import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathReconcile).columnNumbers

int firstRun = 0

'looping Reconcile'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if (((findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != findTestData(
            excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 0))) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathReconcile, ('Email') : 'Email Login'
                    , ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

			'flag first run'
            firstRun = 1
			
			'click menu Reconcile'
			WebUI.click(findTestObject('Reconcile/menu_Reconcile'))
        }
		WebUI.refresh()
		
        GlobalVariable.FlagFailed = 0
        
		'jika input ada error, maka continue'
        if (inputForm() == true) {
			continue
		}

        'check save ada attribute disabled'
        if (WebUI.verifyElementHasAttribute(findTestObject('Reconcile/button_Save'), 'disabled', GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL)) {
            'write to excel save gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                GlobalVariable.ReasonFailedMandatory)

            continue
        } else {
			'click button save'
            WebUI.click(findTestObject('Reconcile/button_Save'))

			'get error check log'
            if (checkErrorLog() == true) {
                continue
            }
            
			'jika flag failed tidak hidup'
            if (GlobalVariable.FlagFailed == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
        }
        'close Browser'
        WebUI.closeBrowser()
    }
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
            'equals', ('//*[@id="' + tokenUnique) + '"]/div/div[2]', true)

        DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)

        for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
            if ((DDLFromToken.split('\n', -1)[i]).toString().toLowerCase() == input.toString().toLowerCase()) {
                modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 
                    'equals', ((('//*[@id="' + tokenUnique) + '"]/div/div[2]/div[') + (i + 1)) + ']', true)

                WebUI.click(modifyObjectClicked)

                break
            }
        }
    } else {
        WebUI.click(findTestObject(locationObject))

        WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
    }
}

def checkErrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        'Tulis di excel itu adalah error'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + '<') + errormessage) + '>')

        GlobalVariable.FlagFailed = 1

        return true
    }
}

def inputForm() {
    inputDDLExact('Reconcile/inputVendor', findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel(
                '$Vendor')))

    if (checkErrorLog() == true) {
        return true
    }
    
    inputDDLExact('Reconcile/inputTenant', findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel(
                '$Tenant')))

    inputDDLExact('Reconcile/inputTipeSaldo', findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, rowExcel(
                '$Tipe Saldo')))

    WebUI.setText(findTestObject('Reconcile/inputTanggalMulai'), findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tanggal Mulai')))

    WebUI.setText(findTestObject('Reconcile/inputTanggalAkhir'), findTestData(excelPathReconcile).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Tanggal Akhir')))
}

