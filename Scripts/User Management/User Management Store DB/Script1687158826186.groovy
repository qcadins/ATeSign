import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection
/*
'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

	'ambil data role dari db'
	ArrayList<String> resultDB = CustomKeywords.'userManagement.UserVerif.getNewUserData'(conndev, 
		findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 18))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
	
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++){
		
		'tambahkan data ke resultExcel'
		resultExcel.add(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, (18+i)))
		
		if(resultExcel[i] != resultDB[i]) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB)
		}
	}
}
else if(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 8).equalsIgnoreCase('Edit')) {
	
	'ambil data role dari db'
	ArrayList<String> resultDB = CustomKeywords.'userManagement.UserVerif.getEditUserData'(conndev, 
		findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14))
	
	'ambil data role dari excel'
	ArrayList<String> resultExcel = []
		
	'cek data untuk tiap alamat di array'
	for (int i = 0; i < resultDB.size ; i++) {
	
		if (i == 0) {
			
			'tambahkan data ke array resultExcel'
			resultExcel.add(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 14))
		}
		else {
			
			'tambahkan data ke resultExcel'
			resultExcel.add(findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, (24+i)))
		}
		
		if(resultExcel[i] != resultDB[i]) {
			
			'tulis adanya error pada sistem web'
			CustomKeywords.'writeToExcel.WriteExcel.writeToExcelStatusReason'('User', GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, (findTestData(excelPathUserManagement).getValue(GlobalVariable.NumofColm, 2) + ';') +
					GlobalVariable.FailedReasonStoreDB)
		}
	}
}

*/