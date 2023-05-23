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
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

'Mengambil documen id dari excel'
docid = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 6).split(', ', -1)

'Looping per document'
for (int y = 0; y < docid.size(); y++) {
    'Mengambil email berdasarkan documentId'
    ArrayList<String> emailSigner = CustomKeywords.'connection.DataVerif.getEmailLogin'(conneSign, docid[y]).split(';', 
        -1)

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

    'Pembuatan untuk array Index result Query'
    arrayIndex = 0

    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()
	
	'Mengambil value db untuk input-input monitoring seperti nomor kontrak, cabang, dan wilayah'
    inputDocumentMonitoring = CustomKeywords.'connection.DataVerif.getInputDocumentMonitoring'(conneSign, docid[y])

    'Mengisi value hasil komparasi, total sign, dan total signed'
    documentStatus = CustomKeywords.'connection.DataVerif.getDocumentStatus'(conneSign, inputDocumentMonitoring[arrayIndex])
	
	'Mengambil value db mengenai nama Customer'
    fullNameCust = CustomKeywords.'connection.DataVerif.getuserCustomerondocument'(conneSign, inputDocumentMonitoring[arrayIndex])
	
	'Mengambil value db mengenai tipe dokumen'
    documentType = CustomKeywords.'connection.DataVerif.getDocumentType'(conneSign, inputDocumentMonitoring[arrayIndex])

    'Klik Button menu Document Monitoring'
    WebUI.click(findTestObject('DocumentMonitoring/DocumentMonitoring'))
	
	'Jika input nama pelanggan telah muncul'
    if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/input_NamaPelanggan'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
        'Set text yang dibutuhkan di document monitoring'
		WebUI.setText(findTestObject('DocumentMonitoring/input_NamaPelanggan'), fullNameCust)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanDari'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiDari'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TipeDok'), documentType)

        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_TipeDok'), Keys.chord(Keys.ENTER))

        WebUI.setText(findTestObject('DocumentMonitoring/input_NoKontrak'), inputDocumentMonitoring[arrayIndex++])

        WebUI.setText(findTestObject('DocumentMonitoring/input_Cabang'), inputDocumentMonitoring[arrayIndex++])

        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Cabang'), Keys.chord(Keys.ENTER))

        WebUI.setText(findTestObject('DocumentMonitoring/input_Wilayah'), inputDocumentMonitoring[arrayIndex++])

        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Wilayah'), Keys.chord(Keys.ENTER))

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalPermintaanSampai'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_TanggalSelesaiSampai'), currentDate)

        WebUI.setText(findTestObject('DocumentMonitoring/input_Status'), documentStatus)

        WebUI.sendKeys(findTestObject('DocumentMonitoring/input_Status'), Keys.chord(Keys.ENTER))

        WebUI.click(findTestObject('DocumentMonitoring/button_Cari'))
		
		'Jika valuenya ada'
        if (WebUI.verifyElementPresent(findTestObject('DocumentMonitoring/lbl_Value'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
			'Mengambil row size dari value'
            sizeRowofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))
			
			'Mengambil column size dari value'
            sizeColumnofLabelValue = DriverFactory.webDriver.findElements(By.cssSelector('#listDokumen > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-body-cell'))
			
			'Mengambil value dari db menngenai data yang perlu diverif'
            resultQuery = CustomKeywords.'connection.DataVerif.getDocumentMonitoring'(conneSign, inputDocumentMonitoring[
                arrayIndex], fullNameCust)

			'Mengambil value dari db mengenai total stamping'
            resultStamping = CustomKeywords.'connection.DataVerif.getTotalStampingandTotalMaterai'(conneSign, docid[y])
			
			'Looping berdasarkan row yang ada pada value'
            for (int j = 1; j <= sizeRowofLabelValue.size(); j++) {
				'Looping berdasarkan column yang ada pada value tanpa aksi.'
                for (int i = 1; i <= ((sizeColumnofLabelValue.size() / sizeRowofLabelValue.size()) - 1); i++) {
                    'modify object label Value'
                    modifyObjectvalues = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath', 
                        'equals', ((('//*[@id="listDokumen"]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + i) + ']/div', true)
					
					'Jika berada di column ke 7'
                    if (i == 7) {
						'Split teks proses TTD'
                        totalSignandtotalSigned = WebUI.getText(modifyObjectvalues).split(' / ', -1)
						
						'Verif hasil split, dimana proses awal hingga akhir. Awal dibandingkan dengan jumlahsignertandatangan, sedangkan akhir dibandingkan dengan total signer dari email'
                        arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[0], jumlahsignertandatangan, FailureHandling.CONTINUE_ON_FAILURE))

                        arrayMatch.add(WebUI.verifyEqual(totalSignandtotalSigned[1], emailSigner.size(),FailureHandling.CONTINUE_ON_FAILURE))
                    } 
					else if (i == 8) {
						'Jika berada di column ke 8'
						'Split teks total Stamping'
                        totalStampingAndTotalMaterai = WebUI.getText(modifyObjectvalues).split('/', -1)
						'looping berdasarkan total split dan diverif berdasarkan db.'
                        for (int j = 1; j < totalStampingAndTotalMaterai.size(); j++) {
                            arrayMatch.add(WebUI.verifyEqual(totalStampingAndTotalMaterai[j], resultStamping[j], FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    } else {
						'Selain di column 7 dan 8 maka akan diverif dengan db.'
                        arrayMatch.add(WebUI.verifyMatch(WebUI.getText(modifyObjectvalues), resultQuery[arrayIndex++], false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                }
            }
        } else {
            'Jika tidak ada, maka datanya tidak ada di UI.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 
                    2).replace('-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' pada Page Document Monitoring.')
        }
    }
    
    'penggunaan checking print false'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Send to Sign', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

