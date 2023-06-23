import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDate as LocalDate
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get dates'
currentDate = LocalDate.now()

firstDateOfMonth = currentDate.withDayOfMonth(1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathemeteraiMonitoring).columnNumbers

sheet = 'e-Meterai Monitoring'

'looping DocumentMonitoring'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathemeteraiMonitoring, ('sheet') : 'Meterai'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu meterai'
            WebUI.click(findTestObject('e-Meterai Monitoring/menu_emeteraiMonitoring'))

            'call function check paging'
            checkPaging(currentDate, firstDateOfMonth, conneSign)
        }
        
		'set text no kontrak'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 9))
	
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 10))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))
		
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 11))
	
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 12))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))
		
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 13))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))
		
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 14))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))
		
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 15))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))
	
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 16))
		
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 17))
	
		'set text status meterai'
		WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 18))
		
		'enter untuk set status meterai'
		WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))
	
        'click button cari'
        WebUI.click(findTestObject('e-Meterai Monitoring/button_Cari'))

        'get stampduty data dari db'
        result = CustomKeywords.'connection.eMeteraiMonitoring.geteMeteraiMonitoring'(conneSign, findTestData(excelPathemeteraiMonitoring).getValue(
                GlobalVariable.NumofColm, 9))

        index = 0
		
		println result
        'verify no meterai'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_NomorMeterai')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' No Materai')

        'verify no kontrak'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_NoKontrak')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

        'verify tanggal pakai'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_TanggalPakai')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Pakai')

        'verify biaya'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Biaya')).replace(',', ''), 
                result[index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Biaya')

        'verify cabang'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Cabang')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Cabang')

        'verify wilayah'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Wilayah')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Wilayah')

        'verify lini bisnis'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_LiniBisnis')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Lini bisnis')

        status = (result[index++]).toUpperCase()

        'verify status'
        checkVerifyEqualOrMatch(status.contains(WebUI.getText(findTestObject('Meterai/table_Status'))), ' Status')

        'click nomor meterai untuk membuka hyperlink'
        WebUI.click(findTestObject('Meterai/table_NomorMeterai'))

        'get stampduty trx data dari db'
        result = CustomKeywords.'connection.Meterai.getStampdutyTrxData'(conneSign, findTestData(excelPathemeteraiMonitoring).getValue(
                GlobalVariable.NumofColm, 16))

        index = 0

        'verify no meterai'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NoTrx')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' No Materai')

        'verify no kontrak'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NoKontrak')), result[
                index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

        'verify nama dok'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NamaDok')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dokumen')

        'verify nama pelanggan'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NamaPelanggan')), result[
                index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Pelanggan')

        'verify tipe trx'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_TipeTrx')), result[index++], 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

        'verify tanggal trx'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_TanggalTrx')), result[
                index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

        'click button X'
        WebUI.click(findTestObject('Meterai/button_X'))

        'check if mau download data meterai'
        if (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 18).equalsIgnoreCase('Yes')) {
            'click button download'
            WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Unduh Excel Pajak'))

            'delay 5 detik'
            WebUI.delay(5)
			
            'check isfiled downloaded'
            if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 
                    19)) == false) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 2) + 
                    ';') + GlobalVariable.ReasonFailedDownload)

                GlobalVariable.FlagFailed = 1
            }
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth, Connection conneSign) {
	
	//totalMeteraiDB = CustomKeywords.'connection.Meterai.getTotalMeterai'(conneSign)
	
    'set text no kontrak'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), '20230616133400')

	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), 'Dokumen Transaksi/Payment Receipt')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))
	
    'set text status meterai'
    WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), firstDateOfMonth)

	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), 'Failed')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))
	
	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), 'Daegu')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))
	
	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), 'Dokumen penerimaan uang (lebih dari 5 juta)')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))
	
	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), 'KWITANSI EMETERAI')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))

	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), currentDate)
	
	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), '')

	'set text status meterai'
	WebUI.setText(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), 'Pemungut')
	
	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))

    'click button set ulang'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/button_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Dokumen'), 'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Tipe Dokumen'), Keys.chord(Keys.ENTER))
	
	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Tanggal Dokumen Mulai'), 'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_HasilStamping'), Keys.chord(Keys.ENTER))

	'click ddl status meterai'
	WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_Cabang'), Keys.chord(Keys.ENTER))

	'click ddl status meterai'
	WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_TipeDokumenPeruri'), Keys.chord(Keys.ENTER))

	'click ddl status meterai'
	WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_PengaturanDokumen'), Keys.chord(Keys.ENTER))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_TanggalDokumenSampai'), 'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/e-Meterai Monitoring/input_Nomor Seri'), 'value', FailureHandling.CONTINUE_ON_FAILURE),'', false, FailureHandling.CONTINUE_ON_FAILURE))

	'click ddl status meterai'
	WebUI.click(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'))

	'verify field ke reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('e-Meterai Monitoring/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

	'enter untuk set status meterai'
	WebUI.sendKeys(findTestObject('Object Repository/e-Meterai Monitoring/input_JenisPajak'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('Meterai/button_Cari'))
	
	WebUI.delay(10)
	
    'set text tanggal pengiriman ke'
    totaleMeteraiUI = WebUI.getText(findTestObject('Object Repository/e-Meterai Monitoring/Label_Totale-Meterai')).split(' ', -1)

	'verify total Meterai'
//	checkVerifyPaging(WebUI.verifyMatch(totalMeteraiUI[0], totalMeteraiDB, false, FailureHandling.CONTINUE_ON_FAILURE))
    
	if (Integer.parseInt(totaleMeteraiUI[0]) > 10) {		
	    'click next page'
	    WebUI.click(findTestObject('e-Meterai Monitoring/button_NextPage'))
	
	    'verify paging di page 2'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click prev page'
	    WebUI.click(findTestObject('e-Meterai Monitoring/button_PrevPage'))
	
	    'verify paging di page 1'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click last page'
	    WebUI.click(findTestObject('e-Meterai Monitoring/button_LastPage'))
	
	    'verify paging di last page'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            WebUI.getAttribute(findTestObject('e-Meterai Monitoring/page_Active'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace(
	                'page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click first page'
	    WebUI.click(findTestObject('e-Meterai Monitoring/button_FirstPage'))
	
	    'verify paging di page 1'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('e-Meterai Monitoring/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('e-Meterai Monitoring', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('e-Meterai Monitoring', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathemeteraiMonitoring).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

