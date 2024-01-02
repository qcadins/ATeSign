import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data buat undangan dari DB'
result = CustomKeywords.'connection.InquiryInvitation.inquiryInvitationStoreDB'(conneSign, findTestData(excelPathRegister).getValue(
        GlobalVariable.NumofColm, rowExcel('$NIK')).replace('"', ''))

'declare arraylist arraymatch'
arrayMatch = []

'declare arrayindex'
arrayindex = 0

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).length() > 0) {
    'verify invite by'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Receiver Detail')).length() > 0) {
    'verify receiver detail'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Receiver Detail')).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

'verify NIK'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('NIK - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nama'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Nama - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tempat lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tempat Lahir - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'parse Date from yyyy-MM-dd > MM/dd/yyyy'
sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
        rowExcel('Tanggal Lahir - Edit')).replace('"', ''), 'MM/dd/yyyy', 'yyyy-MM-dd')

'verify tanggal lahir'
arrayMatch.add(WebUI.verifyMatch(sDate, (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify jenis kelamin'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify no telpon'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '') == 'Email') {
    'verify email'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).replace(
                '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else {
    arrayindex++
}

'verify alamat'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Alamat - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Provinsi - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kota - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kecamatan - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kelurahan - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kode pos'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kode Pos - Edit')).replace(
            '"', '').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

