import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data doc template dari DB'
ArrayList result = CustomKeywords.'connection.PengaturanDokumen.dataDocTemplateStoreDB'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
        GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')))

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayindex = 0

'cek untuk data diisi di excel atau tidak'
if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')).length() > 
0) {
    'verify doc template code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kode Templat Dokumen')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

'cek untuk data diisi di excel atau tidak'
if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Nama Templat Dokumen')).length() > 
0) {
    'verify doc template name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Nama Templat Dokumen')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

'verify deskripsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Deskripsi')).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe pembayran'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Pembayaran TTD')).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'cek untuk data diisi di excel atau tidak'
if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status Active')).length() > 0) {
    'verify status'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Status Active')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Input Psre')).length() > 0) {
    'verify Psre == inputan excel'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
    		'Input Psre')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE)) 
} else {
	arrayindex++
	//	'verify Psre == default vendor yang active'
	//	arrayMatch.add(WebUI.verifyMatch('null', (result[arrayindex++]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
}

'cek untuk data diisi atau tidak'
if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Sequential Signing')).length() > 
0) {
    'verify isSequence'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Sequential Signing')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

'cek untuk data diisi di excel atau tidak'
if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Urutan Signing')).length() > 0) {
    String getUrutanSigning = CustomKeywords.'connection.PengaturanDokumen.getUrutanSigning'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')))

    'verify urutan sequence sign'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Urutan Signing')).toUpperCase(), getUrutanSigning.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
}

'verify qr sign'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
				'QR Lacak Ttd')).replace('Ya', '1').replace('Tidak', '0'), CustomKeywords.'connection.APIFullService.getUseSignQRFromDocTemplate'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen'))), false, FailureHandling.CONTINUE_ON_FAILURE))


'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        GlobalVariable.ReasonFailedStoredDB)
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

