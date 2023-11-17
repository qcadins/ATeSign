import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathBuatUndangan).columnNumbers

int firstRun = 0

'looping buat undangan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}
		
		if((findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Login')) !=
			findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Login'))) || firstRun == 0
			|| GlobalVariable.LoginAgain == 0){
			'call test case login per case'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : SheetName, ('Path') : excelPathBuatUndangan, ('Email') : 'Inveditor Login', ('Password') : 'Inveditor Password Login'
				, ('Perusahaan') : 'Inveditor Perusahaan Login', ('Peran') : 'Inveditor Peran Login'], FailureHandling.STOP_ON_FAILURE)
			
			firstRun = 1
		}
		
        'call test case buat undangan'
        WebUI.callTestCase(findTestCase('Register eSign/BuatUndangan'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan']
			, FailureHandling.CONTINUE_ON_FAILURE)
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}