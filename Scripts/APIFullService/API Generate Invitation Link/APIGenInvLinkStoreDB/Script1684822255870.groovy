import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'get data API Send Document dari DB (hanya 1 signer)'
ArrayList<String> result = CustomKeywords.'connection.APIFullService.getGenInvLink'(conneSign, GlobalVariable.Tenant, findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 
        16).replace('"', ''), findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 17).replace('"', ''), findTestData(
        excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase())

'declare arrayindex'
arrayindex = 0

'verify user crt / callerId'
//arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 9).replace('"', '').toUpperCase(), 
//        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
arrayindex++

'verify gender'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 15).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kelurahan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kecamatan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 19).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify kota'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify zip code'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 23).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tgl Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 14).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tmp Lahir'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify provinsi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify email'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify id no / ktp'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 17).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify phone'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 16).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify address'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 18).replace('"', '').toUpperCase(), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify full name'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 11).replace('"', ''), result[
        arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify tenant code'
arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Tenant.toUpperCase(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(excelPathGenInvLink).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

