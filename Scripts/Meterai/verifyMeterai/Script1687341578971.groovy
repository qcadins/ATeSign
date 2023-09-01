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

'looping DocumentMonitoring'
GlobalVariable.FlagFailed = 0

'click menu meterai'
WebUI.click(findTestObject('Meterai/menu_Meterai'))

'get totalMaterai from db'
ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(conneSign, noKontrak)

'declare index yang akan digunakan'
int indexInput = 0, indexValue = 0, indexGetNomorMaterai = 0

'looping per total meterai yang telah distamp'
for (j = 1; j <= (Integer.parseInt(totalMateraiAndTotalStamping[1].replace(' ',''))); j++) {
	'ambil value db untuk mau input apa'
	ArrayList inputBasedOnAPIStamping = CustomKeywords.'connection.Meterai.getInputMeterai'(conneSign, noKontrak)

	'set text lini bisnis all untuk reset'
	WebUI.click(findTestObject('Object Repository/Meterai/button_SetUlang'))
	
    'set text no kontrak'
    WebUI.setText(findTestObject('Meterai/input_NoKontrak'), inputBasedOnAPIStamping[indexInput++])

    'set text status meterai'
    WebUI.setText(findTestObject('Meterai/input_StatusMeterai'), inputBasedOnAPIStamping[indexInput++])

    'enter untuk set status meterai'
    WebUI.sendKeys(findTestObject('Meterai/input_StatusMeterai'), Keys.chord(Keys.ENTER))

    'set text lini bisnis'
    WebUI.setText(findTestObject('Meterai/input_LiniBisnis'),inputBasedOnAPIStamping[indexInput++])

    'enter untuk set lini bisnis'
    WebUI.sendKeys(findTestObject('Meterai/input_LiniBisnis'), Keys.chord(Keys.ENTER))

    'set text tanggal wilayah'
    WebUI.setText(findTestObject('Meterai/input_Wilayah'), inputBasedOnAPIStamping[indexInput++])

    'enter untuk set wilayah'
    WebUI.sendKeys(findTestObject('Meterai/input_Wilayah'), Keys.chord(Keys.ENTER))

    'set text tanggal cabang'
    WebUI.setText(findTestObject('Meterai/input_Cabang'),inputBasedOnAPIStamping[indexInput++])

    'enter untuk set cabang'
    WebUI.sendKeys(findTestObject('Meterai/input_Cabang'), Keys.chord(Keys.ENTER))

    'set text tanggal pakai dari'
    WebUI.setText(findTestObject('Meterai/input_TanggalPakaiDari'), inputBasedOnAPIStamping[indexInput++])

    'set text tanggal pakai sampai'
    WebUI.setText(findTestObject('Meterai/input_TanggalPakaiSampai'), inputBasedOnAPIStamping[indexInput++])

    'set text no meterai'
    WebUI.setText(findTestObject('Meterai/input_NoMeterai'), inputBasedOnAPIStamping[indexInput++])

    'click button cari'
    WebUI.click(findTestObject('Meterai/button_Cari'))

	'Beri delay 5sec loading Cari'
	WebUI.delay(5)
	
    'get value meterai data dari db'
    result = CustomKeywords.'connection.Meterai.getValueMeterai'(conneSign, noKontrak)

    'verify no meterai'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_NomorMeterai')), result[indexValue++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' No Materai')

    'verify no kontrak'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_NoKontrak')), result[indexValue++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

    'verify tanggal pakai'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_TanggalPakai')), result[indexValue++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Pakai')

    'verify biaya'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Biaya')).replace(',', ''), result[indexValue++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Biaya')

    'verify cabang'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Cabang')), result[indexValue++], false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Cabang')

    'verify wilayah'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_Wilayah')), result[indexValue++], false, 
            FailureHandling.CONTINUE_ON_FAILURE), ' Wilayah')

    'verify lini bisnis'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/table_LiniBisnis')), result[indexValue++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Lini bisnis')

    status = (result[indexValue++]).toUpperCase()

    'verify status'
    checkVerifyEqualOrMatch(status.contains(WebUI.getText(findTestObject('Meterai/table_Status'))), ' Status')

    'click nomor meterai untuk membuka hyperlink'
    WebUI.click(findTestObject('Meterai/table_NomorMeterai'))

    'get stampduty trx data dari db'
    resultPopup = CustomKeywords.'connection.Meterai.getValueDetailMeterai'(conneSign, result[indexGetNomorMaterai])
	
	'index get nomor materai ditingkatkan 8 berdasarkan jumalh kolom value'
	indexGetNomorMaterai = indexGetNomorMaterai + 8
	
	'declare index'
	index = 0
	
    'verify no meterai'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NoTrx')), resultPopup[index++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' No Materai')

    'verify no kontrak'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NoKontrak')), resultPopup[index++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' No Kontrak')

    'verify nama dok'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NamaDok')), resultPopup[index++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Dokumen')

    'verify nama pelanggan'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_NamaPelanggan')), resultPopup[
            index++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama Pelanggan')

    'verify tipe trx'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_TipeTrx')), resultPopup[index++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

    'verify tanggal trx'
    checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Meterai/tableDetail_TanggalTrx')), resultPopup[index++], 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

    'click button X'
    WebUI.click(findTestObject('Meterai/button_X'))
}

if (linkDocumentMonitoring == '') {
linkDocumentMonitoring = 'Not Used'
}

'call testcase verify document monitoring'
WebUI.callTestCase(findTestCase('Document Monitoring/verifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathMeterai, ('sheet') : sheet, ('nomorKontrak') : noKontrak, ('linkDocumentMonitoring') : linkDocumentMonitoring], FailureHandling.CONTINUE_ON_FAILURE)

if (GlobalVariable.FlagFailed == 0) {
	'write to excel success'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet , 0, GlobalVariable.NumofColm -
		1, GlobalVariable.StatusSuccess)
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathMeterai).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

