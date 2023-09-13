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
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String totalSaldo

HashMap<String, String> result = new HashMap<String, String>()

ArrayList saldoList
'return total saldo awal'
String totalSaldoOTP

vendorSigning = vendor

String vendorVerifikasi

if (!WebUI.verifyElementPresent(findTestObject('Saldo/ddl_Vendor'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    if (WebUI.verifyElementPresent(findTestObject('Saldo/menu_Saldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'cek apakah elemen menu ditutup'
		if (WebUI.verifyElementVisible(findTestObject('button_HamburberSideMenu'), FailureHandling.OPTIONAL)) {
			'klik pada button hamburber'
			WebUI.click(findTestObject('button_HamburberSideMenu'))
		}
	
		'klik button saldo'
		WebUI.click(findTestObject('Saldo/menu_Saldo'))
		
		'cek apakah tombol x terlihat'
		if (WebUI.verifyElementVisible(findTestObject('buttonX_sideMenu'), FailureHandling.OPTIONAL)) {
			'klik pada button X'
			WebUI.click(findTestObject('buttonX_sideMenu'))
		}

    } else {
        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Main Flow/Login'), [('excel') : excel, ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
    }
}

if (vendor.equalsIgnoreCase('Privy')) {
    vendorSigning = 'PRIVY'
	vendorVerifikasi = 'PRIVY'
	
	'list data saldo yang perlu diambil'
	saldoList = ['OTP']
} else {
	vendorVerifikasi = 'ESIGN/ADINS'
	
	'list data saldo yang perlu diambil'
	saldoList = ['Liveness', 'Face Compare', 'Liveness Face Compare','OTP','Meterai']
	
}

'klik ddl untuk tenant memilih mengenai Vida'
WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendorVerifikasi, false)

'get total div di Saldo'
variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))

for (int i = 0 ; i < saldoList.size(); i++) {
    'looping berdasarkan total div yang ada di saldo'
    for (int c = 2; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, saldoList[i], FailureHandling.OPTIONAL)) {
            'modify object mengenai ambil total jumlah saldo'
            modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c)) + ']/div/div/div/div/div[2]', 
                true)

			result.put(saldoList[i], WebUI.getText(modifyObjecttotalSaldoSign))
        }
    }
}

'klik ddl untuk tenant memilih mengenai Vida'
WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendorSigning, false)

'get total div di Saldo'
variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div > div'))

'looping berdasarkan total div yang ada di saldo'
for (int c = 2; c <= variableDivSaldo.size(); c++) {
    'modify object mengenai find tipe saldo'
    modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
        (c)) + ']/div/div/div/div/div[1]', true)

    'verifikasi label saldonya '
    if (WebUI.verifyElementText(modifyObjectFindSaldoSign, findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                'Tipe')), FailureHandling.OPTIONAL)) {
        'modify object mengenai ambil total jumlah saldo'
        modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals', 
            '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + c + ']/div/div/div/div/div[2]', 
            true)

		result.put(findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Tipe')).toString(), WebUI.getText(modifyObjecttotalSaldoSign))
        break
    }
}

return result

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}

