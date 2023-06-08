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

'modify page untuk previous. Ini akan digunakan jika datanya tidak ditemukan'
modifyObjectBtnPrevious = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/button_Lastest'), 
    'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
    2) + ']/a', true)

'Jika ingin dilakukannya bulk sign'
if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 50) == 'Yes') {
    'Ambil data dari excel mengenai total dokumen yang ditandatangani'
    totalDocSign = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 51).toInteger()
} else if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 50) == 'No') {
    'Total document sign hanya 1 (single)'
    totalDocSign = 1
}

'Looping berdasarkan page agar bergeser ke page sebelumnya'
for (int k = 1; k <= (variableLastest.size() - 4); k++) {
	'get row beranda'
	rowBeranda = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-dashboard1 > div:nth-child(3) > div > div > div.card-content > div > app-msx-datatable > section > ngx-datatable > div > datatable-body datatable-row-wrapper'))

'looping untuk mengambil seluruh row'
for (int k = rowBeranda.size(); k >= 1; k--) {
    'deklarasi arrayIndex untuk pemakaian'
    arrayIndex = 0

    'modify object text document template name di beranda'
    modifyObjectTextDocumentTemplateName = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div/p', true)

    'modify object text document template tipe di beranda'
    modifyObjectTextDocumentTemplateTipe = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/p', true)

    'modify object btn TTD Dokumen di beranda'
    modifyObjectCheckboxTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/div/input', true)

    'modify object lbl tanggal permintaan'
    modifyObjectTextTglPermintaan = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[6]/div/span', true)

    'modify object nama pelanggan'
    modifyObjectLblNamaPelanggan = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div/p', true)

    'modify object text no kontrak di beranda'
    modifyObjectTextRefNumber = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div/p', true)

    'modify object text no kontrak di beranda'
    modifyObjectTextStatusTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div/p', true)

    'modify object text no kontrak di beranda'
    modifyObjectTextProsesTtd = WebUI.modifyObjectProperty(findTestObject('Object Repository/APIFullService/Send to Sign/text_NamaDokumen'), 
        'xpath', 'equals', ('/html/body/app-root/app-content-layout/div/div/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        k) + ']/datatable-body-row/div[2]/datatable-body-cell[7]/div/p', true)

    'Jika datanya match dengan db, mengenai referal number'
    if (WebUI.verifyMatch(WebUI.getText(modifyObjectTextRefNumber), sendToSign[arrayIndex++], false, FailureHandling.OPTIONAL) == 
    true) {
        'check Kotak Masuk'
        checkKotakMasuk(conneSign, jumlahSignerTelahTtd, emailSigner, sheet, modifyObjectTextRefNumber, modifyObjectTextDocumentTemplateTipe, 
            modifyObjectTextDocumentTemplateName, modifyObjectTextTglPermintaan, modifyObjectTextStatusTtd, modifyObjectTextProsesTtd, 
            rowBeranda.size())

        'jika btn lastest dapat diclick'
        if (WebUI.verifyElementClickable(modifyObjectBtnLastest, FailureHandling.OPTIONAL)) {
            'Klik button Lastest'
            WebUI.click(modifyObjectBtnLastest, FailureHandling.OPTIONAL)
        }
        
        'Mengenai tipe dokumen template'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTextDocumentTemplateTipe), sendToSign[arrayIndex++], 
                false, FailureHandling.OPTIONAL), '')

        'Mengenai tanggal permintaan'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTextTglPermintaan), sendToSign[arrayIndex++], 
                false, FailureHandling.OPTIONAL), '')

        'Jika document Template Namenya masih kosong'
        if (documentTemplateName == '') {
            'Input document Template Name dan nomor kontrak dari UI'
            documentTemplateName = WebUI.getText(modifyObjecttextdocumenttemplatename)

            noKontrak = WebUI.getText(modifyObjecttextrefnumber)

            'Klik checkbox tanda tangan'
            WebUI.click(modifyObjectcheckboxttd)

        } else {
            'Input document Template Name dan nomor kontrak dari UI ditambah dengan delimiter ;'
            documentTemplateName = ((WebUI.getText(modifyObjecttextdocumenttemplatename) + ';') + documentTemplateName)

            noKontrak = ((WebUI.getText(modifyObjecttextrefnumber) + ';') + noKontrak)

            'Klik checkbox tanda tangan'
            WebUI.click(modifyObjectcheckboxttd)
        }
    }

	'Jika masih tidak ditemukan datanya, maka'
	if (documentTemplateName == '') {
		'Klik previous'
		WebUI.click(modifyObjectBtnPrevious)
	} else {
		'Jika sudah ditemukan, maka break'
		break
	}
	}

}
	


'Klik button Tanda tangan bulk'
WebUI.click(findTestObject('Object Repository/APIFullService/Send to Sign/button_TTDBulk'))

'Split document Template Name berdasarkan delimiter'
documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

noKontrakPerDoc = noKontrak.split(';', -1)

'check popup'
if (checkPopup() == true) {
    continue
}

'Jika total document sign excel tidak sama dengan total document sign paging'
if (totalDocSign != documentTemplateNamePerDoc.size()) {
    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedTotalDocTidakSesuai) + 
        documentTemplateNamePerDoc.size()) + ' pada User ') + (emailSigner[(o - 1)]))
}

'Looping berdasarkan total document sign'
for (int c = 0; c < documentTemplateNamePerDoc.size(); c++) {
    'modify object btn Nama Dokumen '
    modifyObjectbtnNamaDokumen = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/btn_NamaDokumen'), 'xpath', 
        'equals', ('id("ngb-nav-' + c) + '")', true, FailureHandling.CONTINUE_ON_FAILURE)

    'verify nama dokumen massal dengan nama dokumen di paging'
    if (WebUI.verifyMatch(WebUI.getText(modifyObjectbtnNamaDokumen), documentTemplateNamePerDoc[(documentTemplateNamePerDoc.size() - 
        (c + 1))], false, FailureHandling.CONTINUE_ON_FAILURE) == false) {
        'Jika tidak cocok, maka custom keywords jika tidak sama.'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            ' dimana tidak sesuai di page Bulk Sign antara ') + WebUI.getText(modifyObjectbtnNamaDokumen)) + ' dengan ') + 
            (documentTemplateNamePerDoc[c]))
    }
}

'Check konfirmasi tanda tangan'
checkKonfirmasiTTD()

'jika page belum pindah'
if (!(WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_TandaTanganDokumen'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL))) {
    'Jika tidak ada, maka datanya tidak ada, atau save gagal'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedSaveGagal) + 
        ' dengan alasan page tidak berpindah di Bulk Sign View.')
} else {
    'Jika bulk signing'
    if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
        'Split document Template name per dokumen berdasarkan delimiter'
        documentTemplateNamePerDoc = documentTemplateName.split(';', -1)

        'Looping berdasarkan document template name per dokumen'
        for (int i = 0; i < documentTemplateNamePerDoc.size(); i++) {
            'Jika page sudah berpindah maka modify object text document template name di Tanda Tangan Dokumen'
            modifyObjectlabelnamadokumenafterkonfirmasi = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_NamaDokumenAfterKonfirmasi'), 
                'xpath', 'equals', ('//*[@id="pdf-main-container"]/div[1]/ul/li[' + (i + 1)) + ']/label', true)

            'verify nama dokumen dengan nama dokumen di paging'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectlabelnamadokumenafterkonfirmasi), documentTemplateNamePerDoc[
                    (documentTemplateNamePerDoc.size() - (i + 1))], false), '')
        }
    }
}

String aa

String bb

