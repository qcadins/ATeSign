import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDate as LocalDate
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get dates'
currentDate = LocalDate.now()

firstDateOfMonth = currentDate.withDayOfMonth(1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathMeterai).columnNumbers

'looping meterai'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        if (GlobalVariable.NumofColm == 2) {
            'call testcase login admin'
            WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathMeterai, ('sheet') : 'Meterai'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu meterai'
            WebUI.click(findTestObject('Meterai/menu_Meterai'))

            'call function check paging'
            checkPaging(currentDate, firstDateOfMonth, conneSign)
        }
        
        'set text no kontrak'
        WebUI.setText(findTestObject('Meterai/input_NoKontrak'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                9))

        'set text status meterai'
        WebUI.setText(findTestObject('Meterai/input_StatusMeterai'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                10))

        'enter untuk set status meterai'
        WebUI.sendKeys(findTestObject('Meterai/input_StatusMeterai'), Keys.chord(Keys.ENTER))

        'set text lini bisnis'
        WebUI.setText(findTestObject('Meterai/input_LiniBisnis'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                11))

        'enter untuk set lini bisnis'
        WebUI.sendKeys(findTestObject('Meterai/input_LiniBisnis'), Keys.chord(Keys.ENTER))

        'set text tanggal wilayah'
        WebUI.setText(findTestObject('Meterai/input_Wilayah'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                12))

        'enter untuk set wilayah'
        WebUI.sendKeys(findTestObject('Meterai/input_Wilayah'), Keys.chord(Keys.ENTER))

        'set text tanggal cabang'
        WebUI.setText(findTestObject('Meterai/input_Cabang'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                13))

        'enter untuk set cabang'
        WebUI.sendKeys(findTestObject('Meterai/input_Cabang'), Keys.chord(Keys.ENTER))

        'set text tanggal pakai dari'
        WebUI.setText(findTestObject('Meterai/input_TanggalPakaiDari'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                14))

        'set text tanggal pakai sampai'
        WebUI.setText(findTestObject('Meterai/input_TanggalPakaiSampai'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                15))

        'set text no meterai'
        WebUI.setText(findTestObject('Meterai/input_NoMeterai'), findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 
                16))

        'click button cari'
        WebUI.click(findTestObject('Meterai/button_Cari'))

        'get stampduty data dari db'
        result = CustomKeywords.'connection.Meterai.getStampdutyData'(conneSign, findTestData(excelPathMeterai).getValue(
                GlobalVariable.NumofColm, 16))

        index = 0

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
        result = CustomKeywords.'connection.Meterai.getStampdutyTrxData'(conneSign, findTestData(excelPathMeterai).getValue(
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
        if (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 18).equalsIgnoreCase('Yes')) {
            'click button download'
            WebUI.click(findTestObject('Meterai/button_UnduhExcel'))
			
			'looping hingga 20 detik'
			for (int i = 1; i <= 4; i++) {
				'pemberian delay download 5 sec'
				WebUI.delay(5)

				'check isfiled downloaded'
				if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathMeterai).getValue(
						GlobalVariable.NumofColm, 19)) == true) {
					'Jika sukses downloadnya lebih dari 10 detik'
					if (i > 2) {
						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedPerformance'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm,
							GlobalVariable.StatusWarning, ((((findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm,
								2) + ';') + GlobalVariable.ReasonFailedPerformance) + ' sejumlah ') + (i * 5)) + ' detik ')

						GlobalVariable.FlagFailed = 1
					}
					
					break
				} else {
					'Jika error lognya muncul'
					if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
						'ambil teks errormessage'
						errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

						'Tulis di excel itu adalah error'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm,
								2).replace('-', '') + ';') + errormessage)

						GlobalVariable.FlagFailed = 1
					}
					
					'Jika sudah loopingan terakhir'
					if (i == 5) {
						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedDownload'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm,
								2) + ';') + GlobalVariable.ReasonFailedDownload)

						GlobalVariable.FlagFailed = 1
					}
				}
			}
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Meterai', 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def checkPaging(LocalDate currentDate, LocalDate firstDateOfMonth, Connection conneSign) {
	totalMeteraiDB = CustomKeywords.'connection.Meterai.getTotalMeterai'(conneSign)
	
    'set text no kontrak'
    WebUI.setText(findTestObject('Meterai/input_NoKontrak'), '000111')

    'set text status meterai'
    WebUI.setText(findTestObject('Meterai/input_StatusMeterai'), 'stamp duty Used')

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Meterai/input_StatusMeterai'), Keys.chord(Keys.ENTER))

    'set text lini bisnis'
    WebUI.setText(findTestObject('Meterai/input_LiniBisnis'), 'multiguna')

    'enter untuk set lini bisnis'
    WebUI.sendKeys(findTestObject('Meterai/input_LiniBisnis'), Keys.chord(Keys.ENTER))

    'set text tanggal wilayah'
    WebUI.setText(findTestObject('Meterai/input_Wilayah'), 'bogor')

    'enter untuk set wilayah'
    WebUI.sendKeys(findTestObject('Meterai/input_Wilayah'), Keys.chord(Keys.ENTER))

    'set text tanggal cabang'
    WebUI.setText(findTestObject('Meterai/input_Cabang'), 'irwan')

    'enter untuk set cabang'
    WebUI.sendKeys(findTestObject('Meterai/input_Cabang'), Keys.chord(Keys.ENTER))

    'set text tanggal pakai dari'
    WebUI.setText(findTestObject('Meterai/input_TanggalPakaiDari'), '2023-01-01')

    'set text tanggal pakai sampai'
    WebUI.setText(findTestObject('Meterai/input_TanggalPakaiSampai'), '2023-01-31')

    'set text no meterai'
    WebUI.setText(findTestObject('Meterai/input_NoMeterai'), '21975017285')

    'click button set ulang'
    WebUI.click(findTestObject('Meterai/button_SetUlang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_NoKontrak'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Meterai/input_StatusMeterai'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Meterai/input_LiniBisnis'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Meterai/input_Wilayah'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click ddl status meterai'
    WebUI.click(findTestObject('Meterai/input_Cabang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/selected_DDL')), 'All', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click field lain untuk close ddl'
    WebUI.click(findTestObject('Meterai/input_TanggalPakaiDari'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_TanggalPakaiDari'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            firstDateOfMonth.toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_TanggalPakaiSampai'), 'value', 
                FailureHandling.CONTINUE_ON_FAILURE), currentDate.toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/input_NoMeterai'), 'value', FailureHandling.CONTINUE_ON_FAILURE), 
            '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click button cari'
    WebUI.click(findTestObject('Meterai/button_Cari'))
	
    'set text tanggal pengiriman ke'
    totalMeteraiUI = WebUI.getText(findTestObject('Meterai/Label_TotalMeterai')).split(' ', -1)

	'verify total Meterai'
	checkVerifyPaging(WebUI.verifyMatch(totalMeteraiUI[0], totalMeteraiDB, false, FailureHandling.CONTINUE_ON_FAILURE))
    
	if (Integer.parseInt(totalMeteraiUI[0]) > 10) {		
	    'click next page'
	    WebUI.click(findTestObject('Meterai/button_NextPage'))
	
	    'verify paging di page 2'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '2', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click prev page'
	    WebUI.click(findTestObject('Meterai/button_PrevPage'))
	
	    'verify paging di page 1'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click last page'
	    WebUI.click(findTestObject('Meterai/button_LastPage'))
	
	    'verify paging di last page'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            WebUI.getAttribute(findTestObject('Meterai/page_Active'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE).replace(
	                'page ', ''), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	    'click first page'
	    WebUI.click(findTestObject('Meterai/button_FirstPage'))
	
	    'verify paging di page 1'
	    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Meterai/paging_Page'), 'ng-reflect-page', FailureHandling.CONTINUE_ON_FAILURE), 
	            '1', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Meterai', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

