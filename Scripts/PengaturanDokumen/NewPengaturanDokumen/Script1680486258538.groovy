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
import org.apache.commons.collections.ArrayStack as ArrayStack
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebElement as WebElement

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPengaturanDokumen).getColumnNumbers(); (GlobalVariable.NumofColm)++) {
    'memanggil test case login untuk admin wom dengan Admin Legal'
    WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

    'Klik tombol pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Klik tombol tambah pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

    'Pengecekan apakah masuk page tambah pengaturan dokumen'
    if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TambahTemplatDokumen'), GlobalVariable.TimeOut)) {
        'Input text documentTemplateCode'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, 9))

        'Input text documentTemplateName'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateName'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, 10))

        'Input text documentTemplateDescription'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, 11))

        'get data tipe-tipe pembayaran secara asc'
        ArrayList<String> tipepembayaran_db = CustomKeywords.'connection.dataVerif.getLovTipePembayaran'(conneSign)

        'Click dropdown mengenai tipe pembayaran'
        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

        'Mengambil value yang ada di UI dan displit dengan enter'
        tipepembayaran_ui = WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/ddl_tipepembayaran')).split(
            '\\n')

        'Jika db dan UI tidak cocok maka'
        if (tipepembayaran_ui != tipepembayaran_db) {
            'Flag false'
            checkVerifyEqualorMatch(false)
        }
        
        'Input value tipe pembayaran'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_tipePembayaran'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, 12))

        'Input enter'
        WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))

        'Input value status'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_StatusAktif'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, 14))

        'Input enter'
        WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_StatusAktif'), Keys.chord(Keys.ENTER))

        'Jika panjang dokumen lebih besar dari 0'
        if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 13).length() > 0) {
            'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
            String userDir = System.getProperty('user.dir')

            String filePath = userDir + findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 13)

            'Upload file berdasarkan filePath yang telah dirancang'
            WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), filePath)
        } else {
            'Upload file yang kosong'
            WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), findTestObject('TandaTanganDokumen/input_documentExample').getValue(
                    GlobalVariable.NumofColm, 13))
        }
        
        'Klik button lanjut'
        WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

        'Jika label Konfirmasi muncul'
        if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_konfirmasi'), GlobalVariable.TimeOut)) {
            'Klik Konfirmasi no'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))

            'Data document template code, template name, template description yang telah diinput diverifikasi dengan excel'
            checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                        9), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 'value'), 
                    false, FailureHandling.CONTINUE_ON_FAILURE))

            checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                        10), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 'value'), 
                    false, FailureHandling.CONTINUE_ON_FAILURE))

            checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                        11), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                        'value'), false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik dropdown mengenai tipe pembayaran'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

			'Verifikasi input tipe pembayaran dengan excel'
            checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                        12), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), false, FailureHandling.CONTINUE_ON_FAILURE))
			
			'Klik dropdown mengenai status'
			WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlaktif'))
			
            'Verifikasi input status dengan excel'
            checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                        14), WebUI.getText(findTestObject('TandaTanganDokumen/input_StatusAktif')), false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik lanjut'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

            'Klik Konfirmasi Yes'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))

            'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
            int countDel = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 18)

            int countLock = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 19)

            int indexLock = countDel

            TipeTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 20).split(';', 
                -1)

			'looping berdasarkan total tanda tangan'
            for (int j = 1; j <= findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 15); j++) {
                'Klik button tanda tangan'
				WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
				
				'Verify label tanda tangannya muncul atau tidak'
                WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_tipetandatangan'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)

				'Memilih tipe signer apa berdasarkan excel'
                WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_tipetandatangan'), TipeTandaTangan[(j - 
                    1)], false)
				
				'Klik set tanda tangan'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))
				
				'modify label tipe tanda tangan di kotak'
				modifyobjectTTDlbltipetandatangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDtipetandatangan'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox['+j+']/div/div/small', false)
				
				'Verify apakah tanda tangannya ada'
                WebUI.verifyElementPresent(modifyobjectTTDlbltipetandatangan, 
                    GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
				
				'Verifikasi antara excel dan UI, apakah tipenya sama'
                WebUI.verifyMatch(TipeTandaTangan[(j - 1)], WebUI.getText(modifyobjectTTDlbltipetandatangan),false)

                'Persiapan buat modify objectnya dengan case-case selanjutnya'
                TestObject kotakttd = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/div_kotakTTD'), 'xpath', 'equals', '/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox['+j+']/div', false)
				
				'Verifikasi kotaknya'
                WebUI.verifyElementPresent(kotakttd, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
				
				'Mengambil web element dari kotaknya'
                WebElement element = WebUI.findWebElement(kotakttd)
				
				'Memasang lokasi, untuk sekarang masih di hardcode'
				'Cara yang digunakan yaitu mengubah css agar lebih tepat. Jika drag and drop, kurang tepat lokasinya'
				'Bisa diubah'
                String newLocation = 'translate3d(400px, 500px, 0px)'
				
                String js = 'arguments[0].style.transform = arguments[1]'
				
                DriverFactory.getWebDriver().executeScript(js, element, newLocation)
				
				'Jika total deletenya lebih besar dari 0'
				'Asumsinya melakukan delete terlebih dahulu'
                if (countDel > 0) {
					'Klik button Delete'
                    WebUI.click(findTestObject('TandaTangaDokumen/btn_DeleteTTD'))
					
					'Dicheck apakah kotaknya tidak ada'
                    WebUI.verifyElementNotPresent(kotakttd, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)

                    countDel--
                } else if (countDel == 0) {
                    if (countLock > 0) {

                        WebUI.click(findTestObject('TandaTangaDokumen/btn_LockTTD'))
						
						'Dicheck apakah kotaknya ada'
						WebUI.verifyElementPresent(kotakttd, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
	
                        countLock--
                    } else {
                    }
                }
            }
        }
    }
}
 //CustomKeywords.'writeToExcel.writeExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumOfColm, GlobalVariable.StatusFailed,
//(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumOfColm, 2) + ';') + GlobalVariable.FailedReasonStoreDB)

def checkVerifyEqualorMatch(Boolean isMatch) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1
		
		CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('Sign Document', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
    }
}

