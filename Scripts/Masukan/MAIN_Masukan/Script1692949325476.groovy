import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathMasukan).columnNumbers

'looping masukan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'panggil fungsi login'
		WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathMasukan], FailureHandling.CONTINUE_ON_FAILURE)

        'delay untuk nunggu alert error hilang'
        WebUI.delay(10)

        'click menu masukan'
        WebUI.click(findTestObject('Masukan/menu_Masukan'))
		
		if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}

        'check if cell rating > 0'
        if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('$Rating')).length() > 0) {
            'get bintang dari excel untuk modify object bintang yang ingin di click'
            index = (Integer.parseInt(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('$Rating'))) * 2)

            'modify object button Bintang'
            modifyObjectButtonBintang = WebUI.modifyObjectProperty(findTestObject('Masukan/button_RatingBintang'), 'xpath', 
                'equals', ('//*[@id="rating"]/span[' + index.toString()) + ']/span', true)

            'click bintang'
            WebUI.click(modifyObjectButtonBintang)
        }
        
        'input comment'
        WebUI.setText(findTestObject('Masukan/input_CommentMasukan'), findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 
                rowExcel('Comment')))

        'click kirim'
        WebUI.click(findTestObject('Masukan/button_Kirim'))

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Is Mandatory Complete')))

        'check if alert berhasil muncul'
        if (WebUI.verifyElementPresent(findTestObject('Masukan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'check if alert contain terimakasih && mandatory lengkap'
            if (WebUI.getAttribute(findTestObject('Masukan/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains(
                'Terimakasih') && (isMandatoryComplete == 0)) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'call test case masukan store db'
                    WebUI.callTestCase(findTestCase('Masukan/MasukanStoreDB'), [('excelPathMasukan') : 'Masukan/Masukan'], 
                        FailureHandling.STOP_ON_FAILURE)
                }
            }
        } else if (WebUI.verifyElementNotPresent(findTestObject('Masukan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
        (isMandatoryComplete > 0)) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedMandatory)
        }
        
        'close Browser'
        WebUI.closeBrowser()
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
