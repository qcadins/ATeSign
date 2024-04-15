import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\1. Login_eSign.xlsm')

'get all table on esign'
ArrayList tableName = CustomKeywords.'connection.MaskingEsign.getTableName'(conneSign)

'inisialize rowChecking'
rowChecking = 1

'looping of every tables name'
for (loopingTable = 0; loopingTable < tableName.size(); loopingTable++) {
	'get usr_crt and usr_upd on verify based on tableName'
    ArrayList verify = CustomKeywords.'connection.MaskingEsign.verifyUserCrtUserDpd'(conneSign, tableName[loopingTable])
	
	'if the verify is exists with size is 2'
    if (verify.size() < 2) {
		'continue loop'
        continue
    } else {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Mask Checking', rowChecking,
			0, tableName[loopingTable])
	}

	'get last data on the table'
    ArrayList check = CustomKeywords.'connection.MaskingEsign.getLastDataTable'(conneSign, tableName[loopingTable])

    'declare arraylist arraymatch'
    ArrayList arrayMatch = []

    arrayIndex = 0

    for (i = 0; i < check.size(); i++) {
        if ((check[i]).toString().length() > 2) {
            arrayMatch.add((check[i]).toString().contains('*'))
        } else {
            arrayMatch.add(!((check[i]).toString().contains('*')))
        }
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Mask Checking', rowChecking++, 
            1, GlobalVariable.StatusFailed)
    } else {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Mask Checking', rowChecking++, 
            1, GlobalVariable.StatusSuccess)
    }
}

