import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data buat undangan dari DB'
result = CustomKeywords.'connection.DataVerif.inquiryInvitationStoreDB'(conneSign, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 9))

'declare arraylist arraymatch'
arrayMatch = []

'declare arrayindex'
arrayindex = 0

'verify invite by'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 59).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify receiver detail'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 60).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify NIK'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 9).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Nama'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tempat lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'parse Date from MM/dd/yyyy > yyyy-MM-dd'
sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 12), 'MM/dd/yyyy', 'yyyy-MM-dd')

'verify tanggal lahir'
arrayMatch.add(WebUI.verifyMatch(sDate.toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

'verify jenis kelamin'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify no telpon'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

if(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15) == 'Email') {
	'verify email'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))
}else {
	arrayindex++
}

'verify alamat'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 17).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kode pos'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 22).toUpperCase(), (result[arrayindex++]).toUpperCase(),
		false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';' + GlobalVariable.ReasonFailedStoredDB)
}

