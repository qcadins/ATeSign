import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList arrayMatch = []

if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('New')) {	
    'ambil data role dari db'
    ArrayList<String> resultDB = CustomKeywords.'connection.UserManagement.getUserManagementNewStoreDB'(conneSign, findTestData(
            excelPathUserManagement).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).toUpperCase(), GlobalVariable.Tenant)

    startTakeExcel = 8

    'ambil data role dari excel'
    ArrayList<String> resultExcel = []

    'cek data untuk tiap alamat di array'
    for (int i = 0; i < resultDB.size; i++) {
        'khusus untuk 11, yaitu kode akses masih belum ketemu'
        if ((startTakeExcel + i) == 11) {
            continue
        }
        
        'tambahkan data ke resultExcel'
        resultExcel.add(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, startTakeExcel + i))

		'verify email'
		arrayMatch.add(WebUI.verifyMatch(resultExcel[i].toLowerCase(), resultDB[i].toLowerCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

        if (arrayMatch.contains(false)) {
			GlobalVariable.FlagFailed = 1
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) +
				';') + GlobalVariable.ReasonFailedStoredDB + ' pada data ' + resultExcel[i] + ' dan ' + resultDB[i]))
        }
    }
} else if (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('Setting')) {
    'ambil data role dari db'
    ArrayList<String> resultDB = CustomKeywords.'connection.UserManagement.getUserManagementEditStoreDB'(conneSign, findTestData(
            excelPathUserManagement).getValue(GlobalVariable.NumofColm, rowExcel('$Email')), GlobalVariable.Tenant)

    startTakeExcel = 14

    'ambil data role dari excel'
    ArrayList<String> resultExcel = []

    'cek data untuk tiap alamat di array'
    for (int i = 0; i < resultDB.size; i++) {
        'tambahkan data ke resultExcel'
        resultExcel.add(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, startTakeExcel + i))

		'verify email'
		arrayMatch.add(WebUI.verifyMatch(resultExcel[i].toLowerCase(), resultDB[i].toLowerCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

        if (arrayMatch.contains(false)) {
			GlobalVariable.FlagFailed = 1
			'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('User Management', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, ((findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) +
				';') + GlobalVariable.ReasonFailedStoredDB + ' pada data ' + resultExcel[i] + ' dan ' + resultDB[i]))
        }
    }
}

def rowExcel(String cellValue) {
	CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}