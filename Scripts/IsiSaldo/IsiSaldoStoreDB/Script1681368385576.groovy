import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data balance mutation dari DB'
ArrayList<String> result = CustomKeywords.'connection.Saldo.getIsiSaldoStoreDB'(conneSign, findTestData(excelPathIsiSaldo).getValue(
        GlobalVariable.NumofColm, 18))

'get data tenant yang tidak topup dari DB'
String resultTenantTidakTopup = CustomKeywords.'connection.Saldo.getTenantTidakIsiSaldo'(conneSign, findTestData(excelPathIsiSaldo).getValue(
        GlobalVariable.NumofColm, 14), findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18))

'declare arraylist arraymatch'
ArrayList<String> arrayMatch

'declare arrayindex'
arrayindex = 0

'verify tenant'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify vendor'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tipe saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 16).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tambah saldo'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nomor tagihan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Catatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tanggal pembelian'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), (result[
        arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant tidak ada'
arrayMatch.add(WebUI.verifyMatch(resultTenantTidakTopup.toString(), 'null', false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('isiSaldo', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

