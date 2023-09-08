import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'Split mengenai documentid'
ArrayList<String> documentIds = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'looping berdasarkan total document yang ada'
for (int i = 1; i <= documentIds.size(); i++) {
    ArrayList<String> emailsigner = CustomKeywords.'connection.SendSign.getEmailsSign'(conneSign, documentIds[(i - 1)])

    'get data API Bulk Sign Document dari DB'
    ArrayList<String> result = CustomKeywords.'connection.SendSign.getbulkSign'(conneSign, documentIds[(i - 1)])

    'declare arrayindex'
    arrayindex = 0

    'verify login id'
    (result[arrayindex]) = (result[arrayindex]).split(';', -1)

    for (int b = 1; b <= (result[arrayindex]).size(); b++) {
        if (WebUI.verifyMatch(emailsigner[(b - 1)], (result[arrayindex])[(b - 1)], false, FailureHandling.CONTINUE_ON_FAILURE) == 
        true) {
            arrayMatch.add(true)
        } else {
            arrayMatch.add(false)
        }
    }
    
    arrayindex++

    'verify vendor code'
    arrayMatch.add(WebUI.verifyMatch(GlobalVariable.Response, result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify total_signed'
    arrayMatch.add(WebUI.verifyMatch('0', result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(GlobalVariable.Response, GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}