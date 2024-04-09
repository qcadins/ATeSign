import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'get data store db'
ArrayList result = CustomKeywords.'connection.APIFullService.getGenInvLink'(conneSign, GlobalVariable.Tenant, findTestData(
        excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''), findTestData(excelPathRegister).getValue(
        GlobalVariable.NumofColm, rowExcel('$NIK')).replace('"', ''))

'declare arrayindex'
arrayindex = 0

'verify user crt / callerId'
arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.MaskingEsign.maskData'(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('callerId')).replace('"', '').toUpperCase()), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify gender'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kelurahan')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kecamatan')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kota')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify zip code'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Kode Pos')).replace(
            '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tgl Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Lahir')).replace(
            '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tmp Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tempat Lahir')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Provinsi')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

emailDB = (result[arrayindex++])

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')) != '') {
    'verify email'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace(
                '"', '').toUpperCase(), emailDB, false, FailureHandling.CONTINUE_ON_FAILURE))
}

'verify id no / ktp'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$NIK')).replace(
            '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify phone'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace(
            '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify address'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Alamat')).replace(
            '"', '').toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify full name'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace(
            '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant code'
arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Tenant.toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

