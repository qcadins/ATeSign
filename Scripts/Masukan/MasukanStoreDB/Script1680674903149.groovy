import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data masukan dari DB'
ArrayList result = CustomKeywords.'connection.DataVerif.getFeedbackStoreDB'(conneSign, GlobalVariable.userLogin.toString().toUpperCase())

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'declare arrayindex'
arrayindex = 0

'verify rating'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify comment'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Comment')), 
        result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

