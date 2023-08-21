import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'call test case login adm esign'
WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathPriorityPsre, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPriorityPsre).columnNumbers; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
		break
	} else if (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted') ||
		findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('WARNING')) {
		
		'click menu priority PSrE'
		WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/menu_PsrePriority'))
		
		'declare array list'
		ArrayList<String> resultDB = [], resultUI = [], seqPsreRole = []
		
		resultDB = CustomKeywords.'connection.PengaturanPSrE.getPsrePriority'(conneSign, findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 13))
		
		'count PSrE'
		variable = DriverFactory.webDriver.findElements(By.cssSelector('#cdk-drop-list-0 div'))
		
		'looping jumlah psre pada ui untuk get urutan psre'
		for(index = 1; index <= variable.size(); index++) {
			'modify object psre box'
			modifyObjectBox = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'),
				'xpath', 'equals', '//*[@id="cdk-drop-list-0"]/div['+ index +']', true)
			
			'add psre kedalam arraylist'
			resultUI.add(WebUI.getText(modifyObjectBox))
		}
		
		'verify default vendor psre db = ui'
		checkVerifyEqualorMatch(WebUI.verifyMatch(resultDB.toString(), resultUI.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' urutan psre tidak sesuai dengan default vendor DB')
		
		'get urutan seq psre dari excel'
		seqPsreRole = findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 16).split('\\n',-1)
		
		'looping seq Psre'
		for (seq = 1; seq <= variable.size(); seq++) {
			'modify label tipe tanda tangan di kotak'
			modifyObject = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'),
				'xpath', 'equals', '//*[@id="cdk-drop-list-0"]/div['+ seq +']', true)
			
			index = seqPsreRole.indexOf(WebUI.getText(modifyObject)) + 1
			
			if (seq != index) {
				'modify label tipe tanda tangan di kotak'
				modifyObjectNew = WebUI.modifyObjectProperty(findTestObject('PengaturanPSrE/PSRe Priority/modifyObject'),
					'xpath', 'equals', '//*[@id="cdk-drop-list-0"]/div['+ index +']', true)
				
				'pindahin ke urutan sesuai excel'
				WebUI.dragAndDropToObject(modifyObject, modifyObjectNew)
				
				'untuk proses pemindahan'
				WebUI.delay(2)
				
				seq--
			}
		}
		
		'click button simpan'
		WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/button_Simpan'))
		
		'delay untuk loading simpan'
		WebUI.delay(3)
	
		'check if muncul popup'
		if(WebUI.verifyElementPresent(findTestObject('PengaturanPSrE/label_PopUP'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)){
			if(WebUI.getText(findTestObject('PengaturanPSrE/label_PopUP')).equalsIgnoreCase('Success')){
				if(GlobalVariable.FlagFailed == 0) {
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PSrE Priority',
						0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
				}
			} else {
				'Write to excel status failed'
				GlobalVariable.FlagFailed = 1
		
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PSrE Priority', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 2) +
					';') + ' GAGAL PENGATURAN PRIORITAS PSRE')
			}
			
			'click button OK'
			WebUI.click(findTestObject('PengaturanPSrE/PSRe Priority/button_OK'))
		}
	}
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1

		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PSrE Priority', GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(excelPathPriorityPsre).getValue(GlobalVariable.NumofColm, 2) +
			';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)
	}
}