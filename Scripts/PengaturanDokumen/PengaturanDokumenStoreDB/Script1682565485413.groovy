import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data doc template dari DB'
ArrayList<String> result = CustomKeywords.'connection.DataVerif.dataDocTemplateStoreDB'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
        GlobalVariable.NumofColm, 9))

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'declare arrayindex'
arrayindex = 0

'verify doc template code'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 9).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify doc template name'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify deskripsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe pembayran'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 12).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify status'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), 
        (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('PengaturanDokumen', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 2) + ';') + 
        GlobalVariable.ReasonFailedStoredDB)
}

