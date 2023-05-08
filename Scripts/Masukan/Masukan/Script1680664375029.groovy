import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys


'get colm excel'
int countColmExcel = findTestData(excelPathMasukan).getColumnNumbers()

'looping masukan'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'call test case login admin'
        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [:], FailureHandling.STOP_ON_FAILURE)

        'delay untuk nunggu alert error hilang'
        WebUI.delay(10)

        'click menu masukan'
        WebUI.click(findTestObject('Masukan/menu_Masukan'))

        'check if cell rating > 0'
        if (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 9).length() > 0) {
            'get bintang dari excel untuk modify object bintang yang ingin di click'
            index = (Integer.parseInt(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 9)) * 2)

            'modify object button Bintang'
            modifyObjectButtonBintang = WebUI.modifyObjectProperty(findTestObject('Masukan/button_RatingBintang'), 'xpath', 
                'equals', ('//*[@id="rating"]/span[' + index.toString()) + ']/span', true)

            'click bintang'
            WebUI.click(modifyObjectButtonBintang)
        }
        
        'input comment'
        WebUI.setText(findTestObject('Masukan/input_CommentMasukan'), findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 
                10))

        'click kirim'
        WebUI.click(findTestObject('Masukan/button_Kirim'))

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 4))

        'check if alert berhasil muncul'
		if(WebUI.verifyElementPresent(findTestObject('Masukan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			'check if alert contain terimakasih && mandatory lengkap'
	        if (WebUI.getAttribute(findTestObject('Masukan/errorLog'), 'aria-label', FailureHandling.OPTIONAL).contains('Terimakasih') && 
	        (isMandatoryComplete == 0)) {
	            'write to excel success'
	            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Masukan', 0, GlobalVariable.NumofColm - 
	                1, GlobalVariable.StatusSuccess)
	
	            if (GlobalVariable.checkStoreDB == 'Yes') {
					'call test case masukan store db'
					WebUI.callTestCase(findTestCase('Masukan/MasukanStoreDB'), [('excelPathMasukan') : 'Masukan/Masukan'], FailureHandling.STOP_ON_FAILURE)
	            }
				
	        }
        } else if (WebUI.verifyElementNotPresent(findTestObject('Masukan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
        (isMandatoryComplete > 0)) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('Masukan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathMasukan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedMandatory)
        }
        
        'close Browser'
        WebUI.closeBrowser()
    }
}


